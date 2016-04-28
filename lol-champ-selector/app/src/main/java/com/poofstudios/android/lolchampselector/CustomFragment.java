package com.poofstudios.android.lolchampselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.poofstudios.android.lolchampselector.api.model.ChampionInfo;
import com.poofstudios.android.lolchampselector.api.model.CustomChampion;
import com.poofstudios.android.lolchampselector.recommender.ChampionRecommender;
import com.poofstudios.android.lolchampselector.recommender.RecommenderSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CustomFragment extends Fragment {

    private SeekBar mAttackBar;
    private SeekBar mDefenseBar;
    private SeekBar mMagicBar;
    private SeekBar mDifficultyBar;
    private Spinner mPrimaryTagSpinner;
    private Spinner mSecondaryTagSpinner;
    private TextView mNumChampionsView;
    private SeekBar mNumChampionsBar;
    private Button mCustomButton;

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
        mNumChampionsBar = (SeekBar) rootView.findViewById(R.id.bar_num_champions);
        mNumChampionsView = (TextView) rootView.findViewById(R.id.num_champions_text);
        mCustomButton = (Button) rootView.findViewById(R.id.button_custom);

        // Configure spinners
        final List<String> tagList = new ArrayList<>(mChampionRecommender.getTags());
        Collections.sort(tagList);
        tagList.add(0, "N/A");
        mPrimaryTagAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, mChampionRecommender.getTags());
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
                if (position+1 == mSecondaryTagSpinner.getSelectedItemPosition()) {
                    mSecondaryTagSpinner.setSelection(0);
                }

                // Update the options in the second spinner
                List<String> refinedTags = new ArrayList<>(tagList);
                refinedTags.remove(position+1);
                mSecondaryTagAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, refinedTags);
                mSecondaryTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSecondaryTagSpinner.setAdapter(mSecondaryTagAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Configure num champions slider
        mNumChampionsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mNumChampionsView.setText(String.valueOf(progress+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mNumChampionsBar.setProgress(4);

        // Configure button listener
        mCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch detail activity with ids of champions
                if (mChampionRecommender.isInitialized()) {
                    launchDetailActivity();
                } else {
                    Log.w("LOL", "ChampionRecommender not initialized yet");
                }
            }
        });

        return rootView;
    }

    private void launchDetailActivity() {
        CustomChampion customChampion = new CustomChampion();
        ChampionInfo customInfo = new ChampionInfo();

        // Create a custom champion with the info entered by the user
        customInfo.attack = mAttackBar.getProgress();
        customInfo.defense = mDefenseBar.getProgress();
        customInfo.magic = mMagicBar.getProgress();
        customInfo.difficulty = mDifficultyBar.getProgress();
        customChampion.setInfo(customInfo);

        // Get the tag information from the spinners
        LinkedList<String> tagList = new LinkedList<>();
        tagList.add(String.valueOf(mPrimaryTagSpinner.getSelectedItem()));
        if (mSecondaryTagSpinner.getSelectedItemPosition() != 0) {
            tagList.add(String.valueOf(mSecondaryTagSpinner.getSelectedItem()));
        }
        customChampion.setTags(tagList);

        // Get the number of champions requested
        int numChampions = mNumChampionsBar.getProgress()+1;

        // Find the k closest champions based on the info specified
        ArrayList<Integer> championIdList = mChampionRecommender.getKSimilarChampionsCustom(
                customChampion, numChampions);

        // Launch the detail activity to display the results
        Intent intent = new Intent(getActivity(), ChampionDetailActivity.class);
        intent.putIntegerArrayListExtra(MainActivity.EXTRA_CHAMPION_ID_LIST, championIdList);
        startActivity(intent);
    }
}
