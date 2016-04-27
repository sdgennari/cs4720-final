package com.poofstudios.android.lolchampselector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.poofstudios.android.lolchampselector.api.RiotGamesApi;
import com.poofstudios.android.lolchampselector.api.RiotGamesService;
import com.poofstudios.android.lolchampselector.api.model.Champion;
import com.poofstudios.android.lolchampselector.api.model.ChampionListResponse;
import com.poofstudios.android.lolchampselector.recommender.ChampionRecommender;
import com.poofstudios.android.lolchampselector.recommender.RecommenderSingleton;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_CHAMPION_ID_LIST = "EXTRA_CHAMPION_ID_LIST";

    private RiotGamesService mRiotGamesService;
    private Map<String, Champion> mChampionMap;
    private ChampionRecommender mChampionRecommender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Create instance of the RiotGamesService
        mRiotGamesService = RiotGamesApi.getService();

        // Update API with region and locale from prefs
        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        RiotGamesApi.setRegion(prefs.getString(getString(R.string.key_region), "na"));
        RiotGamesApi.setLocale(prefs.getString(getString(R.string.key_locale), "en_US"));

        // Load data from the API
        loadData();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RandomFragment(), "RANDOM");
        adapter.addFragment(new RandomFragment(), "CHAMPION");
        adapter.addFragment(new CustomFragment(), "CUSTOM");
        viewPager.setAdapter(adapter);
    }

    private void loadData() {
        Log.d("LOL", "Loading champion data...");
        final Call<ChampionListResponse> championDataCall =
                mRiotGamesService.getAllChampionData(RiotGamesApi.getRegion(), RiotGamesApi.getLocale());
        championDataCall.enqueue(new Callback<ChampionListResponse>() {
            @Override
            public void onResponse(Call<ChampionListResponse> call,
                                   Response<ChampionListResponse> response) {
                ChampionListResponse championListResponse = response.body();
                mChampionMap = championListResponse.getChampionMap();
                Log.d("LOL", "" + mChampionMap.size());

                // Initialize ChampionRecommender with Champion data
                initChampionRecommender();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                launchSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
