package com.hillary.baking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hillary.baking.Adapters.MainDataAdapter;
import com.hillary.baking.Adapters.RequestInterface;
import com.hillary.baking.Models.Recipe;
import com.hillary.baking.Util.AppUtils;
import com.hillary.baking.databinding.ActivityMainBinding;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Context context;
    private ActivityMainBinding binding;
    private MainDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setTitle(getString(R.string.title_activity_main));

        AppUtils.setIdleResourceTo(false);

        context = this;
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.rvMaster.setLayoutManager(layoutManager);

        // allow swipe to refresh
        binding.mainLayoutSwipe.setOnRefreshListener(this);

        loadJSON();
    }

    private void loadJSON() {
        binding.mainLayoutSwipe.setRefreshing(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppUtils.BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<List<Recipe>> call = request.getJSON();

        Log.d("JSON CALL", "RETROFIT CALL");
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {

                Log.i("JSON RESPONSE", "SUCCESS");
                List<Recipe> jsonResponse = response.body();

                adapter = new MainDataAdapter(jsonResponse, clickedItemIndex -> {
                    Recipe recipe = adapter.getRecipeAtIndex(clickedItemIndex);
                    Intent intent = new Intent(context, RecipeInfoActivity.class);
                    intent.putExtra(AppUtils.EXTRAS_RECIPE, recipe);
                    startActivityForResult(intent, 1);
                });
                binding.rvMaster.setAdapter(adapter);
                binding.mainLayoutSwipe.setRefreshing(false);

                AppUtils.setIdleResourceTo(true);
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Log.e("JSON RESPONSE", "FAIL: " + t.getMessage());
                binding.mainLayoutSwipe.setRefreshing(false);
                
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.network)
                        .setNegativeButton(R.string.no_network_try_again, (dialog, id) -> loadJSON())
                        .setPositiveButton(R.string.no_network_close, (dialog, id) -> finish());
                builder.create().show();

                AppUtils.setIdleResourceTo(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        loadJSON();
    }
}