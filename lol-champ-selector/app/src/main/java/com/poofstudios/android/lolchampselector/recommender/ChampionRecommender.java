package com.poofstudios.android.lolchampselector.recommender;

import android.support.v4.util.Pair;
import android.util.Log;

import com.poofstudios.android.lolchampselector.api.model.Champion;
import com.poofstudios.android.lolchampselector.api.model.ChampionInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
    private static final int INFO_DIFFICULTY_WEIGHT = 7;
    private static final int TAG_WEIGHT = 4;

    Map<String, Champion> mChampionMap;
    List<Champion> mChampionList;

    // Matrix to store the distances between champions
    int[][] mChampionAdjMatrix;

    /* Make a matrix to represent the distances between two tags. The positions are given by the
     * index of the string in the tag list.
     * 0 : Assassin
     * 1 : Fighter
     * 2 : Mage
     * 3 : Marksman
     * 4 : Support
     * 5 : Tank
     */
    int[][] mTagAdjMatrix = {
            {0, 4, 2, 5, 7, 7},
            {4, 0, 4, 6, 5, 3},
            {2, 4, 0, 3, 4, 7},
            {5, 6, 3, 0, 4, 5},
            {7, 5, 4, 4, 0, 2},
            {7, 3, 7, 5, 2, 0}
    };

    // List of tags for champions
    List<String> mTagList;

    boolean isInitialized = false;

    protected  ChampionRecommender() {
        initializeTagList();
    }

    protected void init(Map<String, Champion> championMap) {
        isInitialized = true;
        mChampionMap = championMap;

        // Get a list of champions from the map
        mChampionList = new ArrayList<>();
        for (String name : mChampionMap.keySet()) {
            mChampionList.add(mChampionMap.get(name));
        }

        // Sort the list of champions for consistency
        Collections.sort(mChampionList, new Comparator<Champion>() {
            @Override
            public int compare(Champion lhs, Champion rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });

        // Populate the champion adj matrix
        mChampionAdjMatrix = new int[mChampionMap.size()][mChampionMap.size()];
        buildChampionAjdMatrix(mChampionAdjMatrix);
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    private void initializeTagList() {
        mTagList = new ArrayList<>();
        mTagList.add("Assassin");
        mTagList.add("Fighter");
        mTagList.add("Mage");
        mTagList.add("Marksman");
        mTagList.add("Support");
        mTagList.add("Tank");
    }

    public List<String> getTags() {
        return mTagList;
    }

    private void buildChampionAjdMatrix(int[][] adjMatrix) {
        // Get the size of the matrix (adjMatrix must be square)
        int size = adjMatrix.length;

        // Populate the adjMatrix with distances between each champion
        Champion lhsChampion, rhsChampion;
        for (int row = 0; row < size; row++) {
            lhsChampion = mChampionList.get(row);
            for (int col = 0; col < size; col++) {
                if (col < row) {
                    // Below the diagonal, so copy the value from above because the matrix is
                    // symmetric
                    adjMatrix[row][col] = adjMatrix[col][row];
                } else if (row == col) {
                    // On the diagonal, so the distance between a champion and itself is INT_MAX
                    // Note: Set this to INT_MAX so that a champ will never match itself
                    adjMatrix[row][col] = Integer.MAX_VALUE;
                } else {
                    // Above the diagonal, so calculate the distance
                    rhsChampion = mChampionList.get(col);
                    adjMatrix[row][col] = calculateChampionDistance(lhsChampion, rhsChampion);
                }
            }
        }
    }

    public int getRandomChampionId() {
        ArrayList<String> championList = new ArrayList<>(mChampionMap.keySet());
        int randIdx = (int)(Math.random() * championList.size());
        return mChampionMap.get(championList.get(randIdx)).getId();
    }

    public ArrayList<Integer> getKSimilarChampions(Champion targetChampion, int k) {
        int targetChampionIdx = mChampionList.indexOf(targetChampion);
        int size = mChampionAdjMatrix[targetChampionIdx].length;

        PriorityQueue<IntPair> bestKMatches = new PriorityQueue<>(k, new Comparator<IntPair>() {
            @Override
            public int compare(IntPair lhs, IntPair rhs) {
                return rhs.distance - lhs.distance;
            }
        });

        // Add the initial k champions to the max heap
        for (int i = 0; i < k; i++) {
            bestKMatches.add(new IntPair(i, mChampionAdjMatrix[targetChampionIdx][i]));
        }

        // Check the remaining champions
        int curDist;
        IntPair curPair;
        for (int i = k; i < size; i++) {
            curDist = mChampionAdjMatrix[targetChampionIdx][i];
            // If the current distance is better than any of the k best so far, update the heap
            if (curDist < bestKMatches.peek().distance) {
                curPair = new IntPair(i, curDist);
                bestKMatches.poll();
                bestKMatches.add(curPair);
            }
        }

        // Get the champions from the best k matches
        ArrayList<Integer> result = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            result.add(mChampionList.get(bestKMatches.poll().championListIdx).getId());
        }
        return result;
    }

    public ArrayList<Integer> getKSimilarChampionsCustom(Champion customChampion, int k) {
        PriorityQueue<IntPair> bestKMatches = new PriorityQueue<>(k, new Comparator<IntPair>() {
            @Override
            public int compare(IntPair lhs, IntPair rhs) {
                return rhs.distance - lhs.distance;
            }
        });

        // Add the initial k champions to the max heap
        for (int i = 0; i < k; i++) {
            bestKMatches.add(new IntPair(i, calculateChampionDistance(customChampion,
                    mChampionList.get(i))));
        }

        // Check the remaining champions
        int curDist;
        IntPair curPair;
        for (int i = k; i < mChampionList.size(); i++) {
            curDist = calculateChampionDistance(customChampion, mChampionList.get(i));
            // If the current distance is better than any of the k best so far, update the heap
            if (curDist < bestKMatches.peek().distance) {
                curPair = new IntPair(i, curDist);
                bestKMatches.poll();
                bestKMatches.add(curPair);
            }
        }

        // Get the champions from the best k matches
        ArrayList<Integer> result = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            result.add(mChampionList.get(bestKMatches.poll().championListIdx).getId());
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

        // Tags
        int numTags = 1;
        int tagDist = 0;
        int lhsIdx, rhsIdx;
        List<String> lhsTags = lhs.getTags();
        List<String> rhsTags = rhs.getTags();
        if (lhsTags.size() > 0 && rhsTags.size() > 0) {
            lhsIdx = mTagList.indexOf(lhsTags.get(0));
            rhsIdx = mTagList.indexOf(rhsTags.get(0));
            if (lhsIdx != -1 && rhsIdx != -1) {
                tagDist = mTagAdjMatrix[lhsIdx][rhsIdx];
            } else {
                tagDist = 10;
            }
        }
        // If both champions have two tags, add the secondary tag distance
        if (lhsTags.size() > 1 && rhsTags.size() > 1) {
            lhsIdx = mTagList.indexOf(lhsTags.get(1));
            rhsIdx = mTagList.indexOf(rhsTags.get(1));
            if (lhsIdx != -1 && rhsIdx != -1) {
                tagDist = mTagAdjMatrix[lhsIdx][rhsIdx];
            } else {
                tagDist += 10;
            }
            numTags++;
        }
        // Divide the tagDist by the number of tags to handle 1 or 2 tags
        tagDist = tagDist / numTags;
        dist += TAG_WEIGHT * tagDist;

        return dist;
    }

    class IntPair {
        int championListIdx;
        int distance;

        public IntPair(int championListIdx, int distance) {
            this.championListIdx = championListIdx;
            this.distance = distance;
        }
    }
}
