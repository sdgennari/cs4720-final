package com.poofstudios.android.lolchampselector.recommender;

import android.support.annotation.Nullable;

import com.poofstudios.android.lolchampselector.api.model.Champion;

import java.util.Map;

public class RecommenderSingleton {

    static ChampionRecommender sChampionRecommender;

    @Nullable
    public static ChampionRecommender getChampionRecommender() {
        return sChampionRecommender;
    }

    public static void initChampionRecommender(Map<String, Champion> championMap) {
        if (sChampionRecommender == null) {
            sChampionRecommender = new ChampionRecommender(championMap);
        }
    }

}
