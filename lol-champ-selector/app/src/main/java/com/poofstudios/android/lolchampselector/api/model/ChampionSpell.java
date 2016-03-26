package com.poofstudios.android.lolchampselector.api.model;

import java.util.List;

/*
SAMPLE API RESPONSE
{
    "name": "Death Sentence",
    "description": "..."
    "sanitizedDescription": "...",
    "tooltip": "...",
    "leveltip": {},
    "image": {},                        // -> RiotImage
    "resource": "{{ cost }} Mana",
    "maxrank": 5,
    "cost": [],
    "costType": "Mana",
    "costBurn": "80",
    "cooldown": [],
    "cooldownBurn": "20/18/16/14/12",
    "effect": [],
    "effectBurn": [],
    "vars": [],                         // -> List<ChampionSpellVar>
    "range": [],
    "rangeBurn": "1075",
    "key": "ThreshQ"
},
 */

public class ChampionSpell {
    String name;
    String description;
    String sanitizedTooltip;
    RiotImage image;
    String resource;
    List<Integer> cost;
    String costType;
    List<ChampionSpellVar> vars;
    String key;

    /*
    SAMPLE API RESPONSE
    {
        "key": "a1",
        "link": "spelldamage",
        "coeff": [
            0.5
        ]
    }
     */
    class ChampionSpellVar {
        String key;
        String link;
        List<Double> coeff;
    }
}
