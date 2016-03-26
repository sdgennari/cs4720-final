package com.poofstudios.android.lolchampselector.api.model;

/*
SAMPLE API RESPONSE
"passive": {
    "name": "Damnation",
    "description": "...",
    "sanitizedDescription": "...",
    "image": {...}                  // -> RiotImage
}
 */

public class ChampionPassive {
    String name;
    String sanitizedDescription;
    RiotImage image;
}
