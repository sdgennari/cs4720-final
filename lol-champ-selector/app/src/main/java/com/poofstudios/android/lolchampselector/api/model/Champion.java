package com.poofstudios.android.lolchampselector.api.model;

import java.util.LinkedList;
import java.util.List;

/*
SAMPLE API RESPONSE
"Thresh": {
    "id": 412,
    "key": "Thresh",
    "name": "Thresh",
    "title": "the Chain Warden",
    "image": {...},     // -> RiotImage
    "tags": [...],      // -> List<String>
    "info": {...},      // -> <ChampionInfo>
    "spells": [...],    // -> List<ChampionSpell>
    "passive": {...}    // -> ChampionPassive
},
 */

public class Champion {
    int id;
    String key;
    String name;
    String title;
    RiotImage championImage;
    LinkedList<String> tags;            // Maximum of 6 tags, so use a LinkedList to save memory
    ChampionInfo info;
    List<ChampionSpell> spells;
    ChampionPassive passive;

    public List<String> getTags() {
        return tags;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return name;
    }

    public ChampionInfo getInfo() {
        return info;
    }
}
