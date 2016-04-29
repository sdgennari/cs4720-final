package com.poofstudios.android.lolchampselector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.poofstudios.android.lolchampselector.api.RiotGamesApi;
import com.poofstudios.android.lolchampselector.api.RiotGamesService;
import com.poofstudios.android.lolchampselector.api.model.Champion;
import com.poofstudios.android.lolchampselector.api.model.ChampionListResponse;
import com.poofstudios.android.lolchampselector.recommender.ChampionRecommender;
import com.poofstudios.android.lolchampselector.recommender.RecommenderSingleton;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_CHAMPION_ID_LIST = "EXTRA_CHAMPION_ID_LIST";
    public static final String STATE_FRAGMENT_IDX = "STATE_FRAGMENT_IDX";

    private RiotGamesService mRiotGamesService;
    private Map<String, Champion> mChampionMap;
    private ChampionRecommender mChampionRecommender;

    private ViewPager mViewPager;

    private float prevX = -1;
    private float prevY = -1;
    private float prevZ = -1;
    private SensorManager mSensorManager;
    private final SensorEventListener mShakeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float diff = Math.abs(x - prevX) + Math.abs(y - prevY) + Math.abs(z - prevZ);
            if (diff > 15 && prevX != -1 && prevY != -1 && prevZ != -1)  {
                handleShake();
            }

            // Update the previous sensor values
            prevX = x;
            prevY = y;
            prevZ = z;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Load the previous page from the saved instance state (if applicable)
        if (savedInstanceState != null) {
            mViewPager.setCurrentItem(savedInstanceState.getInt(STATE_FRAGMENT_IDX, 0));
        }

        // Create instance of the RiotGamesService
        mRiotGamesService = RiotGamesApi.getService();

        // Update API with region and locale from prefs
        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        RiotGamesApi.setRegion(prefs.getString(getString(R.string.key_region), "na"));
        RiotGamesApi.setLocale(prefs.getString(getString(R.string.key_locale), "en_US"));

        // Load data from the API
        mChampionRecommender = RecommenderSingleton.getChampionRecommender();
        if (!mChampionRecommender.isInitialized()) {
            loadData();
        }

        // Configure the shake sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mShakeListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void handleShake() {
        // Check that the random fragment is showing
        if (mViewPager.getCurrentItem() == 0) {
            Intent intent = new Intent(this, ChampionDetailActivity.class);
            ArrayList<Integer> championIdList = new ArrayList<>();
            championIdList.add(mChampionRecommender.getRandomChampionId());
            intent.putIntegerArrayListExtra(MainActivity.EXTRA_CHAMPION_ID_LIST, championIdList);
            startActivity(intent);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RandomFragment(), "RANDOM");
        adapter.addFragment(new ChampionFragment(), "CHAMPION");
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_FRAGMENT_IDX, mViewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
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
