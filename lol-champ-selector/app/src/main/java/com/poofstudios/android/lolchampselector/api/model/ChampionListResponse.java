package com.poofstudios.android.lolchampselector.api.model;

import java.util.List;

/*
SAMPLE API RESPONSE
{
    "type": "champion",
    "version": "6.6.1",
    "data": {}              // -> List<Champion>
}
 */

public class ChampionListResponse {
    String version;
    List<Champion> data;
}
