package info.simplyapps.game.feedthecat.sprites;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

import info.simplyapps.game.feedthecat.rendering.objects.Obstacle;
import info.simplyapps.gameengine.rendering.objects.Animation;
import info.simplyapps.gameengine.rendering.objects.Graphic;
import info.simplyapps.gameengine.sprites.ViewSprites;

public class GameViewSprites implements ViewSprites {

    public Graphic gBackground;
    public Graphic gGameBackground1;
    public Graphic gGameBackground2;

    public Graphic gButton;
    public Graphic gButtonBlue;
    public Graphic gButtonRed;
    public Graphic gButtonOverlay;

    public Rect rMsgGameState;
    //	public Rect rBtnPause;
//	public Rect rBtnResume;
    public Rect rBtnBack;
//	public Rect rMsgScore;
//	public Rect rScore;

    public List<Obstacle> gObstacle;

    public Rect rReady;
    public Rect rGameOver;

    public Graphic[] gFoods;
    public Graphic[] gTraps;
    public Graphic[] gCoins;

    public Animation aCatAnim;
    public Graphic[] gCatAnim;

    public Graphic[] gHealth;

    public Rect rTextFinished;
    public Rect rTextPause;
    public Rect rTextCollected;
    public Rect rTextBonus;

    public Graphic[] gJumpButton;

    public Graphic gLevelChooser;
    public Graphic gPreviewLevels;
    public Rect rLevelSelect;

    public Rect rLevelUpgrade;

    public Graphic gIconPause;
    public Graphic gIconResume;

    public Graphic gPoints;
    public Graphic gCoin;

    public Rect rButtonCatMilk;
    public Rect rButtonCatFood;

    public Rect rCatMilk;
    public Rect rCatFood;
    public Rect rBtnReset;


    @Override
    public void init() {
    }

    @Override
    public void clean() {
        gBackground = null;

        if (gObstacle != null) {
            gObstacle.clear();
        }
        gObstacle = new ArrayList<Obstacle>();
        gHealth = null;
    }

}
