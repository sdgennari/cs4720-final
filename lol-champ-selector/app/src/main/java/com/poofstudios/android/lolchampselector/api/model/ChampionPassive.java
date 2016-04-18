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

    public String getName() {
        return name;
    }

    public String getSanitizedDescription() {
        return sanitizedDescription;
    }

    public String getFullImageName() {
        return image.full;
    }
}
