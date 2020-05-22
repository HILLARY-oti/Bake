package com.hillary.baking;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.hillary.baking.Models.Step;
import com.hillary.baking.Util.AppUtils;
import com.hillary.baking.fragments.RecipeInfoDetailFragment;

public class RecipeInfoDetailActivity extends AppCompatActivity {

    // Fragment key to restore onSaveInstanceState
    private static final String DETAIL_FRAGMENT_KEY = "detail_fragment";
    private Fragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info_detail);

        // persistence: Save the state of the detail fragment so video is preserved after device rotation
        if (savedInstanceState==null){
            detailFragment = new RecipeInfoDetailFragment();
        }
        else{
            detailFragment = getSupportFragmentManager().getFragment(savedInstanceState, DETAIL_FRAGMENT_KEY);
        }

        Bundle bundle = new Bundle();
        Step step = getIntent().getParcelableExtra(AppUtils.EXTRAS_STEP);
        String recipeName = getIntent().getStringExtra(AppUtils.EXTRAS_RECIPE_NAME);
        bundle.putParcelable(AppUtils.EXTRAS_STEP, step);
        detailFragment.setArguments(bundle);

        ActionBar ab = getSupportActionBar();
        if (step.getId()>0)
            ab.setTitle(getString(R.string.step) + (step.getId()));
        else
            ab.setTitle(getString(R.string.recipe_introduction));
        ab.setSubtitle(recipeName);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frm_recipe_info_detail, detailFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, DETAIL_FRAGMENT_KEY, detailFragment);
    }
}