package com.poofstudios.android.lolchampselector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.poofstudios.android.lolchampselector.recommender.ChampionRecommender;
import com.poofstudios.android.lolchampselector.recommender.RecommenderSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomFragment extends Fragment {

    private SeekBar mAttackBar;
    private SeekBar mDefenseBar;
    private SeekBar mMagicBar;
    private SeekBar mDifficultyBar;
    private Spinner mPrimaryTagSpinner;
    private Spinner mSecondaryTagSpinner;

    private ArrayAdapter<String> mPrimaryTagAdapter;
    private ArrayAdapter<String> mSecondaryTagAdapter;

    private ChampionRecommender mChampionRecommender;

    public CustomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_custom, container, false);

        // Get ChampionRecommender instance
        mChampionRecommender = RecommenderSingleton.getChampionRecommender();

        mAttackBar = (SeekBar) rootView.findViewById(R.id.bar_attack);
        mDefenseBar = (SeekBar) rootView.findViewById(R.id.bar_defense);
        mMagicBar = (SeekBar) rootView.findViewById(R.id.bar_magic);
        mDifficultyBar = (SeekBar) rootView.findViewById(R.id.bar_difficulty);
        mPrimaryTagSpinner = (Spinner) rootView.findViewById(R.id.spinner_tag_primary);
        mSecondaryTagSpinner = (Spinner) rootView.findViewById(R.id.spinner_tag_secondary);

        // Configure spinners
        final List<String> tagList = mChampionRecommender.getTags();
        Collections.sort(tagList);
        tagList.add(0, "N/A");
        mPrimaryTagAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, tagList);
        mPrimaryTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPrimaryTagSpinner.setAdapter(mPrimaryTagAdapter);
        mSecondaryTagAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, tagList);
        mSecondaryTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSecondaryTagSpinner.setAdapter(mSecondaryTagAdapter);
        mPrimaryTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // If the two spinners would have the same value, set the second one to n/a
                if (position == mSecondaryTagSpinner.getSelectedItemPosition()) {
                    mSecondaryTagSpinner.setSelection(0);
                }

                // Update the options in the second spinner
                List<String> refinedTags = new ArrayList<>(tagList);
                if (position != 0) {
                    refinedTags.remove(position);
                }
                mSecondaryTagAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, refinedTags);
                mSecondaryTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSecondaryTagSpinner.setAdapter(mSecondaryTagAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }
}
