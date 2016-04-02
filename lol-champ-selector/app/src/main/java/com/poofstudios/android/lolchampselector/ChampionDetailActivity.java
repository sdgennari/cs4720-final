package com.poofstudios.android.lolchampselector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChampionDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_detail);

        // Make a ChampionDetailFragment to show a random champion
        ChampionDetailFragment fragment = new ChampionDetailFragment();
        Bundle args = new Bundle();
        args.putBoolean(ChampionDetailFragment.ARG_RANDOM, true);
        fragment.setArguments(args);

        // Add the champion detail fragment to the screen
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
