package com.poofstudios.android.lolchampselector.api;

import com.poofstudios.android.lolchampselector.api.model.ChampionListResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RiotGamesService {
    String BASE_ENDPOINT = "https://global.api.pvp.net/api/lol/";
    String API_KEY = "5d59cc26-12e6-4888-930e-c2ca54eda63f";

    /**
     * Gets a static list of all available champions in the game
     * @return List of all Champions available
     */
    @GET("static-data/na/v1.2/champion?champData=image,info,passive,spells,tags&api_key=" + API_KEY)
    Call<ChampionListResponse> getChampionData();
}
