package com.poofstudios.android.lolchampselector;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.poofstudios.android.lolchampselector.recommender.ChampionRecommender;
import com.poofstudios.android.lolchampselector.recommender.RecommenderSingleton;


public class RandomFragment extends Fragment {

    public static final String EXTRA_RANDOM = "EXTRA_RANDOM";

    private ChampionRecommender mChampionRecommender;
    private Button mRandomButton;

    public RandomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_random, container, false);

        mChampionRecommender = RecommenderSingleton.getChampionRecommender();

        // Setup random button
        mRandomButton = (Button) rootView.findViewById(R.id.button_random);
        mRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch detail activity with id of random champion
                if (mChampionRecommender.isInitialized()) {
                    Intent intent = new Intent(getActivity(), ChampionDetailActivity.class);
                    intent.putExtra(EXTRA_RANDOM, mChampionRecommender.getRandomChampionId());
                    startActivity(intent);
                } else {
                    Log.w("LOL", "ChampionRecommender not initialized yet");
                }
            }
        });

        return rootView;
    }
}
