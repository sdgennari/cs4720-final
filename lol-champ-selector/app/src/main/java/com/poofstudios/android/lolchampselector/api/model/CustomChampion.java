package com.poofstudios.android.lolchampselector.api.model;

import java.util.LinkedList;

public class CustomChampion extends Champion {

    public void setInfo(ChampionInfo championInfo) {
        this.info = championInfo;
    }

    public void setTags(LinkedList<String> tagList) {
        this.tags = tagList;
    }
}
