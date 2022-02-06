package com.juergenkleck.android.game.feedthecat.sprites;

import android.graphics.Rect;

import com.juergenkleck.android.gameengine.rendering.objects.Graphic;
import com.juergenkleck.android.gameengine.sprites.ViewSprites;

/**
 * Android app - FeedTheCat
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class OptionViewSprites implements ViewSprites {

    // main
    public Graphic gBackground;

    // generic buttons
    public Graphic gButton;
    public Graphic gButtonRed;
    public Graphic gButtonBlue;
    public Graphic gButtonOverlay;

    public Graphic gTextLogo;

    // options
    public Rect rMsgDifficulty;
    public Rect rButtonDifficultEasy;
    public Rect rButtonDifficultMedium;
    public Rect rButtonDifficultHard;

    // main menu system
    public Rect rBtnSettings;
    public Rect rBtnLevels;
    public Rect rBtnUpgrades;
    public Rect rBtnBack;
    public Rect rBtnBuy;
    public Rect rMsgWait;

    public Graphic gSlideLeft;
    public Graphic gSlideRight;

    public Graphic[] gExtensionLevel;
    public Graphic gPoints;
    public Graphic gCoin;

    public Graphic gCurrentLevel;
    public Rect rUpgradeText;
    public Rect rCurrentUpgrade;
    public Rect rTextCost;
    public Rect rTextAvailable;
    public Rect rCoins;
    public Rect rCoins2;
    public Rect rCoinsCost;
    public Rect rCoinsAvailable;
    public Rect rPoints;
    public Rect rPoints2;
    public Rect rPointsCost;
    public Rect rPointsAvailable;
    public Rect rTextPurchased;

    @Override
    public void init() {
    }

    @Override
    public void clean() {
    }

}
