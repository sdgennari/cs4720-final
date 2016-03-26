package com.poofstudios.android.lolchampselector.api.model;

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
    List<String> tags;
    ChampionInfo info;
    List<ChampionSpell> spells;
    ChampionPassive passive;
}
