package com.poofstudios.android.lolchampselector;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.poofstudios.android.lolchampselector.api.model.Champion;
import com.poofstudios.android.lolchampselector.recommender.ChampionRecommender;
import com.poofstudios.android.lolchampselector.recommender.RecommenderSingleton;

import java.util.ArrayList;
import java.util.List;

public class ChampionFragment extends Fragment {

    private ChampionRecommender mChampionRecommender;

    private AutoCompleteTextView mChampionAutoCompleteView;
    private TextInputLayout mAutoCompleteLayout;
    private SeekBar mNumChampionsBar;
    private TextView mNumChampionsView;
    private Button mFindChampionsButton;


    public ChampionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_champion, container, false);

        // Get ChampionRecommender instance
        mChampionRecommender = RecommenderSingleton.getChampionRecommender();

        // Get the views
        mChampionAutoCompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.champion_autocomplete);
        mAutoCompleteLayout = (TextInputLayout) rootView.findViewById(R.id.champion_autocomplete_layout);
        mNumChampionsBar = (SeekBar) rootView.findViewById(R.id.bar_num_champions);
        mNumChampionsView = (TextView) rootView.findViewById(R.id.num_champions_text);
        mFindChampionsButton = (Button) rootView.findViewById(R.id.button_find_champions);

        // Configure autocomplete view
        if (mChampionRecommender.isInitialized()) {
            configureAutoCompleteData();
        } else {
            mChampionRecommender.addOnInitializedListener(new ChampionRecommender.OnInitializedListener() {
                @Override
                public void onChampionRecommenderInitialized() {
                    configureAutoCompleteData();
                }
            });
            Log.w("LOL", "ChampionRecommender not initialized yet");
        }

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
        mFindChampionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChampionRecommender.isInitialized()) {
                    launchDetailActivity();
                } else {
                    Log.w("LOL", "ChampionRecommender not initialized yet");
                }
            }
        });

        return rootView;
    }

    private void configureAutoCompleteData() {
        List<String> championNameList = mChampionRecommender.getChampionNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                championNameList.toArray(new String[championNameList.size()]));
        mChampionAutoCompleteView.setAdapter(adapter);
    }

    private void launchDetailActivity() {
        String inputName = mChampionAutoCompleteView.getText().toString();

        boolean hasNameError = true;
        for (String championName : mChampionRecommender.getChampionNames()) {
            if (inputName.equalsIgnoreCase(championName)) {
                hasNameError = false;
            }
        }

        if (hasNameError) {
            mAutoCompleteLayout.setError(getString(R.string.champion_error));
            return;
        } else {
            mAutoCompleteLayout.setErrorEnabled(false);
        }

        // Get the number of champions requested
        int numChampions = mNumChampionsBar.getProgress()+1;

        // Get the champion based on the name entered
        Champion champion = mChampionRecommender.getChampionByName(inputName);
        if (champion == null) {
            return;
        }

        // Find the k closest champions based on the champion specified
        ArrayList<Integer> championIdList = mChampionRecommender.getKSimilarChampions(
                champion, numChampions);

        // Launch the detail activity to display the results
        Intent intent = new Intent(getActivity(), ChampionDetailActivity.class);
        intent.putIntegerArrayListExtra(MainActivity.EXTRA_CHAMPION_ID_LIST, championIdList);
        startActivity(intent);
    }

}
