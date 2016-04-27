package com.poofstudios.android.lolchampselector;

import android.content.res.Resources;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ChampionDetailActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_detail);

        // Configure toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get data from extras
        ArrayList<Integer> championIdList =
                getIntent().getIntegerArrayListExtra(MainActivity.EXTRA_CHAMPION_ID_LIST);

        // Setup the view pager
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.padding_default));
        // If there is more than one item in the ViewPager,
        // set the padding to peek the next fragment
        if (championIdList.size() > 1) {
            mViewPager.setPadding(mViewPager.getPaddingLeft(),
                    mViewPager.getPaddingTop(),
                    getResources().getDimensionPixelOffset(R.dimen.padding_double),
                    mViewPager.getPaddingBottom());
        }
        setupViewPager(mViewPager, championIdList);
    }

    private void setupViewPager(ViewPager viewPager, List<Integer> championIdList) {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Create a new fragment for each champion id
        ChampionDetailFragment fragment;
        Bundle args;
        for (Integer championId : championIdList) {
            fragment = new ChampionDetailFragment();
            args = new Bundle();
            args.putInt(ChampionDetailFragment.ARG_CHAMPION_ID, championId);
            fragment.setArguments(args);
            // Retain the instance of the fragment if the device rotates
            fragment.setRetainInstance(true);
            mAdapter.addFragment(fragment, String.valueOf(championId));
        }

        // Set the adapter for the ViewPager
        viewPager.setAdapter(mAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
