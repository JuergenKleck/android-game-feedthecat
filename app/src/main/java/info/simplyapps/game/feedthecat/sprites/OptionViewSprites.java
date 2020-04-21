package info.simplyapps.game.feedthecat.sprites;

import android.graphics.Rect;

import info.simplyapps.gameengine.rendering.objects.Graphic;
import info.simplyapps.gameengine.sprites.ViewSprites;

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
