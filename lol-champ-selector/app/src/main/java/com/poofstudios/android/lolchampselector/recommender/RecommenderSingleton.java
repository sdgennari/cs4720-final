package com.poofstudios.android.lolchampselector.recommender;

import android.support.annotation.NonNull;

import com.poofstudios.android.lolchampselector.api.model.Champion;

import java.util.Map;

public class RecommenderSingleton {

    static ChampionRecommender sChampionRecommender = new ChampionRecommender();

    @NonNull
    public static ChampionRecommender getChampionRecommender() {
        return sChampionRecommender;
    }

    public static void initChampionRecommender(Map<String, Champion> championMap) {
        sChampionRecommender.init(championMap);
    }

}
