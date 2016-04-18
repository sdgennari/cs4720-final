package com.poofstudios.android.lolchampselector.api;

public class UrlUtil {

    private static final String IMAGE_SPLASH_BASE =
            "http://ddragon.leagueoflegends.com/cdn/img/champion/splash/%s_0.jpg";

    private static final String IMAGE_BASE =
            "http://ddragon.leagueoflegends.com/cdn/6.7.1/img/champion/%s.png";

    private static final String IMAGE_PASSIVE_BASE =
            "http://ddragon.leagueoflegends.com/cdn/6.7.1/img/passive/%s";

    private static final String IMAGE_SPELL_BASE =
            "http://ddragon.leagueoflegends.com/cdn/6.7.1/img/spell/%s";

    public static String getChampionSplashUrl(String championKey) {
        return String.format(IMAGE_SPLASH_BASE, championKey);
    }

    public static String getChampionImageUrl(String championKey) {
        return String.format(IMAGE_BASE, championKey);
    }

    public static String getPassiveImageUrl(String fullImageName) {
        return String.format(IMAGE_PASSIVE_BASE, fullImageName);
    }

    public static String getSpellImageUrl(String fullImageName) {
        return String.format(IMAGE_SPELL_BASE, fullImageName);
    }

}
