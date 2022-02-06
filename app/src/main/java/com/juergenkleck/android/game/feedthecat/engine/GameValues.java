package com.juergenkleck.android.game.feedthecat.engine;

import com.juergenkleck.android.game.feedthecat.Constants;
import com.juergenkleck.android.game.feedthecat.free.R;

/**
 * Android app - FeedTheCat
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class GameValues extends com.juergenkleck.android.gameengine.system.GameValues {

    public static final int EXPANSION_COINS_BONUS = 3000;
    public static final int EXPANSION_POINTS_BONUS = 50000;

    public static final int TYPE_FOOD = 0;
    public static final int TYPE_TRAP = 1;
    public static final int TYPE_COIN = 2;

    public static final long gameRoundDelay = 2500L;
    public static final long movementDelay = 50L;
    public static final int movementPixel = 9;
    public static final long createFoodDelay = 1000L;
    public static final long createTrapDelay = 1500L;
    public static final long createCoinBuffer = 2500L;

    public static final long nightDimming = 100L;
    public static final int nightMin = 0;
    public static final int nightMax = 95;
    public static final int nightStep = 5;

    public static final long catJumping = 50L;
    public static final long[] catJumpAirTime = {2000L, 1750L, 1500L};

    public static final int catPosJumped = 450;
    public static final int catPosGround = 50;

    public static final float totalLife = 1.0f;

    public static final long roundTime = 60000L;
    public static final long dayTime = 30000L;
    public static final long nightTime = 15000L;

    public static final float[] foodPerRound = {0.10f, 0.10f, 0.10f, 0.10f, 0.10f, 0.20f, 0.10f, 0.20f};
    public static final int[] maxFoodPerRound = {2, 2, 2, 2, 2, 1, 2, 1};
    public static final float[] trapPerRound = {0.1f, 0.1f, 0.15f, 0.20f, 0.15f, 0.15f, 0.2f, 0.15f};
    public static final int[] maxTrapPerRound = {2, 2, 3, 4, 3, 3, 3, 4};

    public static final int[] coinsPerRound = {10, 10, 12, 20, 8, 10, 8, 13};

    public static final float[] pointsBonusPerRound = {1.0f, 1.0f, 1.2f, 1.0f, 2.0f, 1.5f, 1.75f, 2.5f};
    public static final float[] coinsBonusPerRound = {1.0f, 1.0f, 1.0f, 3.0f, 1.0f, 1.25f, 1.0f, 1.25f};

    public static final float[] pointsBonusDifficulty = {1.0f, 1.25f, 1.5f};
    public static final float[] coinsBonusDifficulty = {1.0f, 1.25f, 1.5f};

    public static final int[] trapTypes = {R.drawable.trap1, R.drawable.trap2, R.drawable.trap3, R.drawable.trap4, R.drawable.trap5};
    public static final int[] foodTypes = {R.drawable.catfood1, R.drawable.catfood2, R.drawable.catfood3};
    public static final int[] foodPoints = {10, 15, 20};
    public static final float[] trapSize = {0.200f, 0.175f, 0.200f, 0.175f, 0.175f};
    public static final float[] foodSize = {0.075f, 0.075f, 0.125f};
    public static final float coinSize = 0.075f;
    public static final float lifePoint = 0.1f;
    public static final float lifeCatFood = 0.1f;
    public static final float lifeCatMilk = 0.15f;

    public static final int[] backgroundPerRound = {R.drawable.level1, R.drawable.level2, R.drawable.level3, R.drawable.level4, R.drawable.level5, R.drawable.level6, R.drawable.level7, R.drawable.level8};

    public static final int[] maxUpgrades = {
            3, 3, 3, 1, 9, 9
    };

    // first index is extension
    // second index is extension level
    // third index is price and coins
    public static final int[][][] upgradePricesPointsCoins = {
            {{10000, 250}, {15000, 500}, {25000, 1000}} // EXTENSION_SKILL_BETTER_JUMP
            , {{10000, 250}, {15000, 500}, {25000, 1000}} // EXTENSION_SKILL_MORE_LIFE
            , {{10000, 250}, {15000, 500}, {25000, 1000}} // EXTENSION_SKILL_REGENERATION
            , {{30000, 2000}} // EXTENSION_SKILL_KEEP_POINTS_COINS
            , {{500, 0}} // EXTENSION_ITEM_CATFOOD
            , {{750, 0}} // EXTENSION_ITEM_CATMILK
//		,{ { 5000, 50 } } // EXTENSION_ITEM_COLLAR
//		,{ { 5000, 50 } } // EXTENSION_ITEM_FLOWER
    };

    // first index is extension
    // second index is extension level
    // third index is price and coins
    public static final int[][] levelPricesPointsCoins = {
            {4000, 100} // EXTENSION_LEVEL_3 street
            , {20000, 2000} // EXTENSION_LEVEL_4 star
            , {12000, 600} // EXTENSION_LEVEL_5 desert
            , {8000, 150} // EXTENSION_LEVEL_6 blocks
            , {12000, 200} // EXTENSION_LEVEL_7 beach
            , {16000, 800} // EXTENSION_LEVEL_8 ship
    };

    public static final long[] upgradeBetterJump = {500L, 1000L, 1500L};
    public static final float[] upgradeMoreLife = {0.1f, 0.2f, 0.3f};
    public static final float[] upgradeRegeneration = {0.05f, 0.1f, 0.15f};

    public static final int[] levelImages = {R.drawable.preview_level1, R.drawable.preview_level2, R.drawable.preview_level3, R.drawable.preview_level4, R.drawable.preview_level5, R.drawable.preview_level6, R.drawable.preview_level7, R.drawable.preview_level8};
    public static final String[] levelPurchasable = {null, null, Constants.EXTENSION_LEVEL_3, Constants.EXTENSION_LEVEL_4, Constants.EXTENSION_LEVEL_5, Constants.EXTENSION_LEVEL_6, Constants.EXTENSION_LEVEL_7, Constants.EXTENSION_LEVEL_8};

    public static final int[] upgradeId = {
            R.string.text_extension_better_jump
            , R.string.text_extension_morelife
            , R.string.text_extension_regeneration
            , R.string.text_extension_keeppoints
            , R.string.text_extension_catfood
            , R.string.text_extension_catmilk
            //,R.string.text_extension_collar
            //,R.string.text_extension_flowers
    };

    public static final int[] levelId = {
            R.drawable.preview_level3,
            R.drawable.preview_level4,
            R.drawable.preview_level5,
            R.drawable.preview_level6,
            R.drawable.preview_level7,
            R.drawable.preview_level8
    };

    public static final int[] levelNameId = {
            R.string.level_3,
            R.string.level_4,
            R.string.level_5,
            R.string.level_6,
            R.string.level_7,
            R.string.level_8
    };

    public static final String[] upgradeNames = {
            Constants.EXTENSION_SKILL_BETTER_JUMP
            , Constants.EXTENSION_SKILL_MORE_LIFE
            , Constants.EXTENSION_SKILL_REGENERATION
            , Constants.EXTENSION_SKILL_KEEP_POINTS_COINS
            , Constants.EXTENSION_ITEM_CATFOOD
            , Constants.EXTENSION_ITEM_CATMILK
//		,Constants.EXTENSION_ITEM_COLLAR
//		,Constants.EXTENSION_ITEM_FLOWER
    };

    public static final String[] levelNames = {
            Constants.EXTENSION_LEVEL_3
            , Constants.EXTENSION_LEVEL_4
            , Constants.EXTENSION_LEVEL_5
            , Constants.EXTENSION_LEVEL_6
            , Constants.EXTENSION_LEVEL_7
            , Constants.EXTENSION_LEVEL_8
    };

}
