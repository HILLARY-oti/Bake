package com.hillary.baking.Adapters;

import com.hillary.baking.Models.Recipe;
import com.hillary.baking.Util.AppUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {

    @GET(AppUtils.JSON_LOC)
    Call<List<Recipe>> getJSON();
}