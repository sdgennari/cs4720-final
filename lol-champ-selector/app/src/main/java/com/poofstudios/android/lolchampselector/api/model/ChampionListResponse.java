package com.poofstudios.android.lolchampselector.api.model;

import java.util.ArrayList;
import java.util.HashMap;

/*
SAMPLE API RESPONSE
{
    "type": "champion",
    "version": "6.6.1",
    "data": {}              // -> Map<String, Champion>
}
 */

public class ChampionListResponse {
    String version;
    HashMap<String, Champion> data;

    public HashMap<String, Champion> getChampionMap() {
        return data;
    }

    public ArrayList<Champion> getChampionList() {
        ArrayList<Champion> championList = new ArrayList<>();
        for (String key : data.keySet()) {
            championList.add(data.get(key));
        }
        return championList;
    }
}
