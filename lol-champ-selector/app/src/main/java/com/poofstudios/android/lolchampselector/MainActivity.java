package com.poofstudios.android.lolchampselector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    private RiotGamesService mRiotGamesService;
    private Map<String, Champion> mChampionMap;
    private ChampionRecommender mChampionRecommender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

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
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new RandomFragment(), "RANDOM");
        mAdapter.addFragment(new RandomFragment(), "CHAMPION");
        mAdapter.addFragment(new RandomFragment(), "DATA");
        viewPager.setAdapter(mAdapter);
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mTitleList.add(title);
        }
    }
}
