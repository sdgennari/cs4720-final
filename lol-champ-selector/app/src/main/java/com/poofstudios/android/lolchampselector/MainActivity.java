package com.poofstudios.android.lolchampselector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.poofstudios.android.lolchampselector.api.RiotGamesApi;
import com.poofstudios.android.lolchampselector.api.RiotGamesService;
import com.poofstudios.android.lolchampselector.api.model.Champion;
import com.poofstudios.android.lolchampselector.api.model.ChampionListResponse;
import com.poofstudios.android.lolchampselector.recommender.ChampionRecommender;
import com.poofstudios.android.lolchampselector.recommender.RecommenderSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RiotGamesService mRiotGamesService;
    private Map<String, Champion> mChampionMap;
    private ChampionRecommender mChampionRecommender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create instance of the RiotGamesService
        mRiotGamesService = RiotGamesApi.getService();

        // Load data from the API
        loadData();
    }

    private void loadData() {
        Log.d("LOL", "Loading champion data...");
        final Call<ChampionListResponse> championDataCall = mRiotGamesService.getChampionData();
        championDataCall.enqueue(new Callback<ChampionListResponse>() {
            @Override
            public void onResponse(Call<ChampionListResponse> call,
                                   Response<ChampionListResponse> response) {
                ChampionListResponse championListResponse = response.body();
                mChampionMap = championListResponse.getChampionMap();
                Log.d("LOL", "" + mChampionMap.size());

                // Initialize ChampionRecommender with Champion data
                initChampionRecommender();

                // Launch detail activity
                Intent intent = new Intent(MainActivity.this, ChampionDetailActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ChampionListResponse> call, Throwable t) {
                Log.d("LOL", "Request failed: " + t.getLocalizedMessage());
            }
        });
    }

    private void initChampionRecommender() {
        RecommenderSingleton.initChampionRecommender(mChampionMap);
        mChampionRecommender = RecommenderSingleton.getChampionRecommender();
    }
}
