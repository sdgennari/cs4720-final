package com.poofstudios.android.lolchampselector;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class ChampionDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_detail);

        // Configure toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get data from extras
        int championId = getIntent().getIntExtra(RandomFragment.EXTRA_RANDOM, -1);

        // Make a ChampionDetailFragment
        ChampionDetailFragment fragment = new ChampionDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ChampionDetailFragment.ARG_CHAMPION_ID, championId);
        fragment.setArguments(args);

        // Retain the instance of the fragment if the device rotates
        fragment.setRetainInstance(true);

        if (savedInstanceState == null) {
            // Add the champion detail fragment to the screen
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            // savedInstanceState not null, so do not make a new fragment
            Log.i("LOL", "savedInstanceState not null");
        }
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
