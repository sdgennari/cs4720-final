package com.poofstudios.android.lolchampselector.api;

public class UrlUtil {

    private static final String IMAGE_SPLASH_BASE =
            "http://ddragon.leagueoflegends.com/cdn/img/champion/splash/%s_0.jpg";

    private static final String IMAGE_BASE =
            "http://ddragon.leagueoflegends.com/cdn/6.6.1/img/champion/%s.png";

    public static String getChampionSplashUrl(String championName) {
        String url = String.format(IMAGE_SPLASH_BASE, championName);
        return url;
    }

    public static String getChampionImageUrl(String championName) {
        String url = String.format(IMAGE_BASE, championName);
        return url;
    }

}