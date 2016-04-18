package com.poofstudios.android.lolchampselector;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.poofstudios.android.lolchampselector.api.RiotGamesApi;
import com.poofstudios.android.lolchampselector.api.RiotGamesService;
import com.poofstudios.android.lolchampselector.api.UrlUtil;
import com.poofstudios.android.lolchampselector.api.model.Champion;
import com.poofstudios.android.lolchampselector.api.model.ChampionInfo;
import com.poofstudios.android.lolchampselector.api.model.ChampionPassive;
import com.poofstudios.android.lolchampselector.api.model.ChampionSpell;
import com.poofstudios.android.lolchampselector.recommender.ChampionRecommender;
import com.poofstudios.android.lolchampselector.recommender.RecommenderSingleton;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChampionDetailFragment extends Fragment {

    public static final String ARG_CHAMPION_ID = "ARG_CHAMPION_ID";

    private RiotGamesService mRiotGamesService;

    private ImageView mSplashImageView;
    private ImageView mChampionImageView;
    private TextView mTitleView;
    private TextView mSubtitleView;
    private TextView mTagView;
    private TextView mInformationView;

    private ImageView mPassiveImageView;
    private TextView mPassiveNameView;
    private TextView mPassiveDescView;

    private ImageView[] mSpellImageViews = new ImageView[4];
    private TextView[] mSpellNameViews = new TextView[4];
    private TextView[] mSpellDescViews = new TextView[4];


    public ChampionDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get instance of the RiotGamesService
        mRiotGamesService = RiotGamesApi.getService();
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

        mPassiveImageView = (ImageView) rootView.findViewById(R.id.passive_image);
        mPassiveNameView = (TextView) rootView.findViewById(R.id.passive_name);
        mPassiveDescView = (TextView) rootView.findViewById(R.id.passive_description);

        ViewGroup spellViewGroup = (ViewGroup) rootView.findViewById(R.id.spell_q);
        mSpellImageViews[0] = (ImageView) spellViewGroup.findViewById(R.id.spell_image);
        mSpellNameViews[0] = (TextView) spellViewGroup.findViewById(R.id.spell_name);
        mSpellDescViews[0] = (TextView) spellViewGroup.findViewById(R.id.spell_description);

        spellViewGroup = (ViewGroup) rootView.findViewById(R.id.spell_w);
        mSpellImageViews[1] = (ImageView) spellViewGroup.findViewById(R.id.spell_image);
        mSpellNameViews[1] = (TextView) spellViewGroup.findViewById(R.id.spell_name);
        mSpellDescViews[1] = (TextView) spellViewGroup.findViewById(R.id.spell_description);

        spellViewGroup = (ViewGroup) rootView.findViewById(R.id.spell_e);
        mSpellImageViews[2] = (ImageView) spellViewGroup.findViewById(R.id.spell_image);
        mSpellNameViews[2] = (TextView) spellViewGroup.findViewById(R.id.spell_name);
        mSpellDescViews[2] = (TextView) spellViewGroup.findViewById(R.id.spell_description);

        spellViewGroup = (ViewGroup) rootView.findViewById(R.id.spell_r);
        mSpellImageViews[3] = (ImageView) spellViewGroup.findViewById(R.id.spell_image);
        mSpellNameViews[3] = (TextView) spellViewGroup.findViewById(R.id.spell_name);
        mSpellDescViews[3] = (TextView) spellViewGroup.findViewById(R.id.spell_description);

        getDataFromArgs();

        return rootView;
    }

    private void getDataFromArgs() {
        // Get args passed to fragment
        Bundle args = this.getArguments();
        int championId = args.getInt(ARG_CHAMPION_ID);
        final Call<Champion> championCall = mRiotGamesService.getChampionData(
                RiotGamesApi.getRegion(),
                championId,
                RiotGamesApi.getLocale());

        championCall.enqueue(new Callback<Champion>() {
            @Override
            public void onResponse(Call<Champion> call, Response<Champion> response) {
                Champion champion = response.body();
                showChampionData(champion);
            }

            @Override
            public void onFailure(Call<Champion> call, Throwable t) {
                // TODO Show error screen
            }
        });
    }

    private void showChampionData(Champion champion) {
        // Update the TextViews with the champion data
        mTitleView.setText(champion.getName());
        mSubtitleView.setText(champion.getTitle());
        List<String> tags = champion.getTags();
        String tagString = "";
        for (int i = 0; i < tags.size()-1; i++) {
            tagString += tags.get(i);
            tagString += ", ";
        }
        tagString += tags.get(tags.size()-1);
        mTagView.setText(tagString);
        ChampionInfo info = champion.getInfo();
        String infoString = String.format("Attack: %d; Defense: %d; Magic: %d; Difficulty: %d",
                info.attack, info.defense, info.magic, info.difficulty);
        mInformationView.setText(infoString);

        // Load splash art image
        String championSplashUrl = UrlUtil.getChampionSplashUrl(champion.getKey());
        Picasso.with(getContext()).load(championSplashUrl)
                .into(mSplashImageView);

        // Load champion image
        String championImageUrl = UrlUtil.getChampionImageUrl(champion.getKey());
        Picasso.with(getContext()).load(championImageUrl)
                .into(mChampionImageView);

        // PASSIVE
        ChampionPassive passive = champion.getPassive();
        mPassiveNameView.setText(passive.getName());
        mPassiveDescView.setText(passive.getSanitizedDescription());
        String passiveImageUrl = UrlUtil.getPassiveImageUrl(passive.getFullImageName());
        Picasso.with(getContext()).load(passiveImageUrl)
                .into(mPassiveImageView);

        // SPELLS
        int idx = 0;
        String spellImageUrl;
        for (ChampionSpell spell : champion.getSpells()) {
            mSpellNameViews[idx].setText(spell.getName());
            mSpellDescViews[idx].setText(spell.getSanitizedDescription());
            spellImageUrl = UrlUtil.getSpellImageUrl(spell.getFullImageName());
            Picasso.with(getContext()).load(spellImageUrl)
                    .into(mSpellImageViews[idx]);
            idx++;
        }
    }

}
