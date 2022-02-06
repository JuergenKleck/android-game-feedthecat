package com.juergenkleck.android.game.feedthecat;

/**
 * Android app - FeedTheCat
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class Constants {

    public static final String DATABASE = "feedthecat.db";
    public static final int DATABASE_VERSION = 1;

    public static final String PREFERENCE_NS = "http://com.juergenkleck.android.game.feedthecat.rendering";

    public static final int ACTION_SCORE = 101;
    public static final int ACTION_HOME = 300;

    public static final String EXTENSION_SKILL_BETTER_JUMP = "betterjump";
    public static final String EXTENSION_SKILL_MORE_LIFE = "morelife";
    public static final String EXTENSION_SKILL_REGENERATION = "regeneration";
    public static final String EXTENSION_SKILL_KEEP_POINTS_COINS = "keeppointscoins";
    public static final String EXTENSION_ITEM_CATFOOD = "catfood";
    public static final String EXTENSION_ITEM_CATMILK = "catmilk";
    public static final String EXTENSION_ITEM_COLLAR = "collar";
    public static final String EXTENSION_ITEM_FLOWER = "flower";
    public static final String EXTENSION_LEVEL_3 = "level3";
    public static final String EXTENSION_LEVEL_4 = "level4";
    public static final String EXTENSION_LEVEL_5 = "level5";
    public static final String EXTENSION_LEVEL_6 = "level6";
    public static final String EXTENSION_LEVEL_7 = "level7";
    public static final String EXTENSION_LEVEL_8 = "level8";

    public static final int spaceLR = 10;
    public static final int spaceTB = 8;
    public static final int spaceMainBtnLR = 10;
    public static final int spaceMainBtnTB = 16;
    public static final float CHAR_SPACING = 0.35f;

    public enum RenderMode {
        HOME, GAME, SCORE, OPTIONS;
    }

    public enum OptionRenderMode {
        NONE, SETTINGS, UPGRADE, LEVEL;
    }

}
