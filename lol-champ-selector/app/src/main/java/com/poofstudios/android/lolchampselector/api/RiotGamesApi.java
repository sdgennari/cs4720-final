package com.poofstudios.android.lolchampselector.api;


import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Singleton to create an instance of the RiotGamesService with Retrofit
 */
public class RiotGamesApi {

    static RiotGamesService sRiotGamesService;

    public static RiotGamesService getService() {
        if (sRiotGamesService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RiotGamesService.BASE_ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            sRiotGamesService = retrofit.create(RiotGamesService.class);
        }
        return sRiotGamesService;
    }

}
