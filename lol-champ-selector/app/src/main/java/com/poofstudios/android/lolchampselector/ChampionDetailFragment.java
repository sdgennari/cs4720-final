package com.poofstudios.android.lolchampselector;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.poofstudios.android.lolchampselector.api.UrlUtil;
import com.poofstudios.android.lolchampselector.api.model.Champion;
import com.poofstudios.android.lolchampselector.api.model.ChampionInfo;
import com.poofstudios.android.lolchampselector.recommender.ChampionRecommender;
import com.poofstudios.android.lolchampselector.recommender.RecommenderSingleton;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;


public class ChampionDetailFragment extends Fragment {

    public static final String ARG_RANDOM = "ARG_RANDOM";

    private ChampionRecommender mChampionRecommender;
    private Champion mChampion;

    private ImageView mSplashImageView;
    private ImageView mChampionImageView;
    private TextView mTitleView;
    private TextView mSubtitleView;
    private TextView mTagView;
    private TextView mInformationView;

    public ChampionDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get instance of the ChampionRecommender
        mChampionRecommender = RecommenderSingleton.getChampionRecommender();

        // Get args passed to fragment
        Bundle args = this.getArguments();
        if (args.getBoolean(ARG_RANDOM, false)) {
            // Select a random champion from the ChampionRecommender
            mChampion = mChampionRecommender.getRandomChampion();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_champion_detail, container, false);

        // Get views from the fragment
        mSplashImageView = (ImageView) rootView.findViewById(R.id.image_splash);
        mChampionImageView = (ImageView) rootView.findViewById(R.id.image_champ);
        mTitleView = (TextView) rootView.findViewById(R.id.title);
        mSubtitleView = (TextView) rootView.findViewById(R.id.subtitle);
        mTagView = (TextView) rootView.findViewById(R.id.tags);
        mInformationView = (TextView) rootView.findViewById(R.id.info_text);

        // Update the TextViews with the champion data
        mTitleView.setText(mChampion.getName());
        mSubtitleView.setText(mChampion.getTitle());
        List<String> tags = mChampion.getTags();
        String tagString = "";
        for (int i = 0; i < tags.size()-1; i++) {
            tagString += tags.get(i);
            tagString += ", ";
        }
        tagString += tags.get(tags.size()-1);
        mTagView.setText(tagString);
        ChampionInfo info = mChampion.getInfo();
        String infoString = String.format("Attack: %d; Defense: %d; Magic: %d; Difficulty: %d",
                info.attack, info.defense, info.magic, info.difficulty);
        mInformationView.setText(infoString);


        // Load splash art image
        String championSplashUrl = UrlUtil.getChampionSplashUrl(mChampion.getName());
        Picasso.with(rootView.getContext()).load(championSplashUrl)
                .into(mSplashImageView);

        // Load champion image
        String championImageUrl = UrlUtil.getChampionImageUrl(mChampion.getName());
        Picasso.with(rootView.getContext()).load(championImageUrl)
                .into(mChampionImageView);

        return rootView;
    }

}
