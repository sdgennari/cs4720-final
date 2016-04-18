package com.poofstudios.android.lolchampselector.api;


import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Singleton to create an instance of the RiotGamesService with Retrofit
 */
public class RiotGamesApi {

    static RiotGamesService sRiotGamesService;

    private static String sRegion;
    private static String sLocale;

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

    public static void setRegion(String region) {
        sRegion = region;
    }

    public static void setLocale(String locale) {
        sLocale = locale;
    }

    public static String getRegion() {
        if (sRegion == null) {
            return "na";
        }
        return sRegion;
    }

    public static String getLocale() {
        if (sLocale == null) {
            return "en_US";
        }
        return sLocale;
    }



}
