package com.poofstudios.android.lolchampselector.recommender;

import android.util.Log;

import com.poofstudios.android.lolchampselector.api.model.Champion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Recommends champions based on a graph of champions
 */
public class ChampionRecommender {

    Map<String, Champion> mChampionMap;
    Set<String> mTagSet;

    public ChampionRecommender(Map<String, Champion> championMap) {
        mChampionMap = championMap;
        initializeTagSet();
        buildChampionGraph();
    }

    private void initializeTagSet() {
        mTagSet = new HashSet<>();
        mTagSet.add("Assassin");
        mTagSet.add("Fighter");
        mTagSet.add("Mage");
        mTagSet.add("Marksman");
        mTagSet.add("Support");
        mTagSet.add("Tank");
    }

    /**
     * Creates a map of champions to a list of champions to represent a graph between champions.
     * Edges are added to the graph if the tags between two champions match
     * @return Map of champions to related champions
     */
    private Map<Champion, Set<Champion>> buildChampionGraph() {
        Map<Champion, Set<Champion>> graph = new HashMap<>();

        // Get a list of champions from the map
        List<Champion> championList = new ArrayList<>();
        for (String name : mChampionMap.keySet()) {
            championList.add(mChampionMap.get(name));
        }

        // Map tags to champions with those tags
        Map<String, Set<Champion>> tagChampionMap = new HashMap<>();
        for (String tag : mTagSet) {
            Set<Champion> tagChampions = new HashSet<>();
            // Add all champions that have the current tag
            for (Champion champion : championList) {
                if (champion.getTags().contains(tag)) {
                    tagChampions.add(champion);
                }
            }
            // Add the champion set to the graph
            tagChampionMap.put(tag, tagChampions);
        }

        // Add all edges to the graph
        Set<Champion> edges;
        for (Champion champion : championList) {
            edges = new HashSet<>();
            // Take union of all tag sets
            for (String tag : champion.getTags()) {
                // Add this check here to catch unknown tags
                if (tagChampionMap.containsKey(tag)) {
                    edges.addAll(tagChampionMap.get(tag));
                }
            }

            // Remove current champion from the set
            edges.remove(champion);

            // Add the edges to the graph
            graph.put(champion, edges);
        }

        Log.d("LOL", "" + graph.get(mChampionMap.get("Thresh")).size());

        return graph;
    }
}
