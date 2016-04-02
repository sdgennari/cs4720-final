package com.poofstudios.android.lolchampselector;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.poofstudios.android.lolchampselector.api.model.Champion;
import com.poofstudios.android.lolchampselector.recommender.ChampionRecommender;
import com.poofstudios.android.lolchampselector.recommender.RecommenderSingleton;


public class ChampionDetailFragment extends Fragment {

    public static final String ARG_RANDOM = "ARG_RANDOM";

    private ChampionRecommender mChampionRecommender;
    private Champion mChampion;

    private TextView mTitleView;

    public ChampionDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_champion_detail, container, false);

        // Get instance of the ChampionRecommender
        mChampionRecommender = RecommenderSingleton.getChampionRecommender();

        // Get args passed to fragment
        Bundle args = this.getArguments();
        if (args.getBoolean(ARG_RANDOM, false)) {
            // Select a random champion from the ChampionRecommender
            mChampion = mChampionRecommender.getRandomChampion();
        }

        // Set the title to the current champion name
        mTitleView = (TextView) rootView.findViewById(R.id.title);
        mTitleView.setText(mChampion.getName());

        return rootView;
    }

}
