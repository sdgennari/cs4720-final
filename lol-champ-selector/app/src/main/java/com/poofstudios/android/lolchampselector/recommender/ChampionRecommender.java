package com.poofstudios.android.lolchampselector.recommender;

import android.util.Log;

import com.poofstudios.android.lolchampselector.api.model.Champion;
import com.poofstudios.android.lolchampselector.api.model.ChampionInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Recommends champions based on a graph of champions
 */
public class ChampionRecommender {

    // Integers to weight different components of champion info
    // Note: Use integers from 0 to 10 here to avoid doubles to make math faster
    private static final int INFO_ATTACK_WEIGHT = 10;
    private static final int INFO_DEFENSE_WEIGHT = 10;
    private static final int INFO_MAGIC_WEIGHT = 10;
    private static final int INFO_DIFFICULTY_WEIGHT = 5;

    Map<String, Champion> mChampionMap;
    Set<String> mTagSet;
    Map<Champion, Set<Champion>> mChampionGraph;

    // Use a protected constructor so that only classes in the same package can access it
    protected ChampionRecommender(Map<String, Champion> championMap) {
        mChampionMap = championMap;
        initializeTagSet();
        mChampionGraph = buildChampionGraph();

        // Test champion similarity
        Champion test = getSimilarChampion(mChampionMap.get("Thresh"));
        Log.d("LOL", test.getName());

        test = getSimilarChampion(mChampionMap.get("Azir"));
        Log.d("LOL", test.getName());

        test = getSimilarChampion(mChampionMap.get("Caitlyn"));
        Log.d("LOL", test.getName());
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

        return graph;
    }

    public Champion getChampionByName(String name) {
        return mChampionMap.get(name);
    }

    public Champion getRandomChampion() {
        ArrayList<String> championList = new ArrayList<>(mChampionMap.keySet());
        int randIdx = (int)(Math.random() * championList.size());
        return mChampionMap.get(championList.get(randIdx));
    }

    private Champion getSimilarChampion(Champion targetChampion) {
        int curDist;
        int bestDist = Integer.MAX_VALUE;
        Champion result = null;
        Set<Champion> edges = mChampionGraph.get(targetChampion);

        // Iterate through all champions and find closest one
        for (Champion champ : edges) {
            // Calculate distance between current and target
            curDist = calculateChampionDistance(targetChampion, champ);

            // Compare the result to the best known champ thus far
            if (curDist < bestDist) {
                Log.d("LOL", "best distance thus far is: " + curDist);
                result = champ;
                bestDist = curDist;
            }
        }

        return result;
    }

    /**
     * Approximates the similarity between two champions by calculating the distance between
     * their stats
     * @param lhs First champion to compare
     * @param rhs Second champion to compare
     * @return An int representing the distance between the two champions
     */
    private int calculateChampionDistance(Champion lhs, Champion rhs) {
        int dist = 0;
        ChampionInfo lhsInfo = lhs.getInfo();
        ChampionInfo rhsInfo = rhs.getInfo();

        // Attack
        dist += INFO_ATTACK_WEIGHT * Math.abs(lhsInfo.attack - rhsInfo.attack);

        // Defense
        dist += INFO_DEFENSE_WEIGHT * Math.abs(lhsInfo.defense- rhsInfo.defense);

        // Magic
        dist += INFO_MAGIC_WEIGHT * Math.abs(lhsInfo.magic - rhsInfo.magic);

        // Difficulty
        dist += INFO_DIFFICULTY_WEIGHT * Math.abs(lhsInfo.difficulty - rhsInfo.difficulty);

        return dist;
    }
}
