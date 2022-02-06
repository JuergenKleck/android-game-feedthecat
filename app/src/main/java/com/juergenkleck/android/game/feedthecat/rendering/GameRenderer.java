
package com.juergenkleck.android.game.feedthecat.rendering;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.juergenkleck.android.appengine.storage.dto.Configuration;
import com.juergenkleck.android.appengine.storage.dto.Extensions;
import com.juergenkleck.android.game.feedthecat.Constants;
import com.juergenkleck.android.game.feedthecat.SystemHelper;
import com.juergenkleck.android.game.feedthecat.engine.FeedTheCatEngine;
import com.juergenkleck.android.game.feedthecat.engine.GameValues;
import com.juergenkleck.android.game.feedthecat.free.R;
import com.juergenkleck.android.game.feedthecat.rendering.objects.Obstacle;
import com.juergenkleck.android.game.feedthecat.sprites.GameViewSprites;
import com.juergenkleck.android.game.feedthecat.storage.DBDriver;
import com.juergenkleck.android.game.feedthecat.storage.dto.CurrentGame;
import com.juergenkleck.android.game.feedthecat.system.FeedTheCatGame;
import com.juergenkleck.android.gameengine.EngineConstants;
import com.juergenkleck.android.gameengine.rendering.kits.AnimationKit;
import com.juergenkleck.android.gameengine.rendering.kits.Renderkit;
import com.juergenkleck.android.gameengine.rendering.kits.ScreenKit;
import com.juergenkleck.android.gameengine.rendering.kits.ScreenKit.ScreenPosition;
import com.juergenkleck.android.gameengine.rendering.objects.Animation;
import com.juergenkleck.android.gameengine.rendering.objects.Graphic;
import com.juergenkleck.android.gameengine.system.BasicGame;
import com.juergenkleck.android.gameengine.system.GameRound;
import com.juergenkleck.android.gameengine.system.GameState;
import com.juergenkleck.android.gameengine.system.GameSubState;

/**
 * Android app - FeedTheCat
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class GameRenderer extends FeedTheCatRendererTemplate implements FeedTheCatEngine {

    /**
     * The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN
     */
    private GameState mMode;
    private GameSubState mSubMode;

    private FeedTheCatGame mGame;

    private Random rnd;

    private long delay = 0L;
    private long lastTime;

    private final float standardNumberWidth = 0.05f;
    private final float standardNumberHeight = 1.20f;

    private Paint pNight;

    // dynamic ingame data
    private long lastFood;
    private long lastTrap;
    private long lastCoin;
    private int nightDimming = -1;
    private long lastDim;
    private long lastJump;
    private int catPos;
    private boolean inJump;
    private boolean pressed;
    private boolean exhausted;

    // swiping for level selection
    private int swipeStartX;
    // level selection
    private int previewLevel;
    private int previewLevelLoaded;
    private final int previewLevelMax = 8;

    // ingame data
    private int extensionBetterJump = -1;
    private int extensionRegeneration = -1;
    private float maxLife = GameValues.totalLife;
    private long jumpDelay = 0L;
    private long fpsdelay = 0L;
    private long coinDelay = 0L;

    // endgame data
    private long bonusCoins;
    private long bonusPoints;

    public GameRenderer(Context context, Properties p) {
        super(context, p);
    }

    @Override
    public void doInitThread(long time) {

        rnd = new Random();

        sprites = new GameViewSprites();
        mMode = GameState.NONE;
        mSubMode = GameSubState.NONE;

        previewLevelLoaded = -1;

        if (mGame == null) {
            createGame();
        }

        Configuration cDifficulty = SystemHelper.getConfiguration(EngineConstants.CONFIG_DIFFICULTY, EngineConstants.DEFAULT_CONFIG_DIFFICULTY);
        mGame.difficulty = Integer.parseInt(cDifficulty.value);

        // load background for level choosing
        getSprites().gBackground = loadGraphic(R.drawable.background);

        getSprites().rMsgGameState = new Rect(0, 0, screenWidth, screenHeight);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.5f, 0, 0, getSprites().rMsgGameState);
        getSprites().rMsgGameState.bottom -= getSprites().rMsgGameState.height() / 3;

        getSprites().gButtonBlue = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_blue, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButtonRed = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_red, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButton = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_flat, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButtonOverlay = loadGraphic(R.drawable.button_flat_white, 0, 0);

        getSprites().rBtnBack = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.20f, 20, 25, getSprites().rBtnBack);

        // Create text
        getSprites().rReady = getSprites().gButton.image.copyBounds();
        getSprites().rGameOver = getSprites().gButton.image.copyBounds();

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.25f, 0, 0, getSprites().rReady);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.25f, 0, 0, getSprites().rGameOver);

        initGamingArea(time);
    }

    private void createGame() {
        mGame = new FeedTheCatGame(new GameRound[]{
                new GameRound(0, GameValues.roundTime, -1)
        });
        mGame.life = GameValues.totalLife;
        mGame.points = 0;
        mGame.coins = 0;
        mGame.jumped = false;
        mGame.totalCoins = 0;
        mGame.currentRound = 0;
        setGameDelay();
    }

    private void setGameDelay() {
        if (mGame.hasGame()) {
            delay = GameValues.gameRoundDelay;
        }
    }


    /**
     * Create the play area
     * Do this only once per game
     */
    private void initGamingArea(long time) {

        pNight = new Paint();
        pNight.setColor(Color.BLACK);
        pNight.setStyle(Paint.Style.FILL);
        pNight.setAlpha(0);

        getSprites().gCatAnim = new Graphic[2];
        getSprites().gCatAnim[0] = loadGraphic(R.drawable.cat_move_0, 0, 0);
        getSprites().gCatAnim[1] = loadGraphic(R.drawable.cat_move_1, 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, 0.10f, 0, 0, getSprites().gCatAnim);

        getSprites().aCatAnim = new Animation();
        AnimationKit.addAnimation(getSprites().aCatAnim, 0, 250);
        AnimationKit.addAnimation(getSprites().aCatAnim, 1, 250);

        initCatPhysics(mGame.jumped, time);

        // create healthbar
        getSprites().gHealth = new Graphic[11];
        getSprites().gHealth[0] = loadGraphic(R.drawable.lifebar0);
        getSprites().gHealth[1] = loadGraphic(R.drawable.lifebar10);
        getSprites().gHealth[2] = loadGraphic(R.drawable.lifebar20);
        getSprites().gHealth[3] = loadGraphic(R.drawable.lifebar30);
        getSprites().gHealth[4] = loadGraphic(R.drawable.lifebar40);
        getSprites().gHealth[5] = loadGraphic(R.drawable.lifebar50);
        getSprites().gHealth[6] = loadGraphic(R.drawable.lifebar60);
        getSprites().gHealth[7] = loadGraphic(R.drawable.lifebar70);
        getSprites().gHealth[8] = loadGraphic(R.drawable.lifebar80);
        getSprites().gHealth[9] = loadGraphic(R.drawable.lifebar90);
        getSprites().gHealth[10] = loadGraphic(R.drawable.lifebar100);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.40f, 10, 20, getSprites().gHealth);


        // Create text
        getSprites().rTextPause = getSprites().gButton.image.copyBounds();
        getSprites().rTextFinished = getSprites().gButton.image.copyBounds();
        getSprites().rTextCollected = getSprites().gButton.image.copyBounds();
        getSprites().rTextBonus = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.50f, 0, 0, getSprites().rTextPause);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.50f, 0, 0, getSprites().rTextFinished);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.20f, 10, 10, getSprites().rTextCollected);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.20f, 10, 350, getSprites().rTextBonus);


        Extensions ext = SystemHelper.getExtensions(Constants.EXTENSION_SKILL_BETTER_JUMP);
        if (ext != null && ext.amount > -1) {
            extensionBetterJump = ext.amount;
        }
        ext = SystemHelper.getExtensions(Constants.EXTENSION_SKILL_MORE_LIFE);
        if (ext != null && ext.amount > -1) {
            maxLife += GameValues.upgradeMoreLife[ext.amount];
        }
        ext = SystemHelper.getExtensions(Constants.EXTENSION_SKILL_REGENERATION);
        if (ext != null && ext.amount > -1) {
            extensionRegeneration = ext.amount;
        }

        // jump button
        getSprites().gJumpButton = new Graphic[3];
        getSprites().gJumpButton[0] = loadGraphic(R.drawable.jumpbutton_blue);
        getSprites().gJumpButton[1] = loadGraphic(R.drawable.jumpbutton_green);
        getSprites().gJumpButton[2] = loadGraphic(R.drawable.jumpbutton_red);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.20f, 10, 20, getSprites().gJumpButton);

        getSprites().gLevelChooser = loadGraphic(R.drawable.levelchooser);
        getSprites().rLevelSelect = getSprites().gButton.image.copyBounds();
        getSprites().gPreviewLevels = loadGraphic(R.drawable.preview_level1);
        previewLevel = 0;
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, 0.50f, 0, 0, getSprites().gPreviewLevels);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, 0.60f, 0, 0, getSprites().gLevelChooser);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER_BOTTOM, 0.40f, 0, 30, getSprites().rLevelSelect);

        getSprites().gIconResume = loadGraphic(R.drawable.game_resume);
        getSprites().gIconPause = loadGraphic(R.drawable.game_pause);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.08f, 20, 20, getSprites().gIconPause);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.08f, 20, 20, getSprites().gIconResume);

        getSprites().gCoin = loadGraphic(R.drawable.coin);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.05f, 0, 0, getSprites().gCoin);

        getSprites().gPoints = loadGraphic(R.drawable.points);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.05f, 0, 0, getSprites().gPoints);

        getSprites().rBtnReset = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.2f, 140, 25, getSprites().rBtnReset);

        getSprites().rCatMilk = getSprites().gButton.image.copyBounds();
        getSprites().rCatFood = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.11f, 200, 180, getSprites().rCatMilk);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.11f, 100, 180, getSprites().rCatFood);

        getSprites().rButtonCatFood = getSprites().gButton.image.copyBounds();
        getSprites().rButtonCatMilk = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.22f, 20, 20, getSprites().rButtonCatFood);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.22f, 150, 20, getSprites().rButtonCatMilk);

        getSprites().gTraps = new Graphic[GameValues.trapTypes.length];
        for (int i = 0; i < GameValues.trapTypes.length; i++) {
            getSprites().gTraps[i] = loadGraphic(GameValues.trapTypes[i]);
            ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, 0.10f, 0, 0, getSprites().gTraps[i]);
        }
        getSprites().gFoods = new Graphic[GameValues.foodTypes.length];
        for (int i = 0; i < GameValues.foodTypes.length; i++) {
            getSprites().gFoods[i] = loadGraphic(GameValues.foodTypes[i]);
            ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, 0.10f, 0, 0, getSprites().gFoods[i]);
        }
        getSprites().gCoins = new Graphic[1];
        getSprites().gCoins[0] = loadGraphic(R.drawable.coin);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, 0.10f, 0, 0, getSprites().gCoins[0]);

        bonusPoints = 0;
        bonusCoins = 0;

        // create objects
        getSprites().gObstacle = new ArrayList<>();
    }


    /**
     * Update only
     *
     * @param jumped
     * @param time
     */
    private void updateCatPhysics(boolean jumped, long time) {

        if (catPos < GameValues.catPosGround) {
            catPos = GameValues.catPosGround;//jumped ? 450 : 50;
        }

        switch (catPos) {
            case GameValues.catPosJumped:
                catPos = jumped ? catPos : catPos - 50;
                if (lastJump + GameValues.catJumping < time) {
                    lastJump = time;
                }
                break;
            case GameValues.catPosGround:
                catPos = jumped ? catPos + 50 : catPos;
                if (lastJump + GameValues.catJumping < time) {
                    lastJump = time;
                }
                break;
            default:
                catPos = jumped ? catPos + 50 : catPos - 50;
                if (lastJump + GameValues.catJumping < time) {
                    lastJump = time;
                }
                break;
        }

        getSprites().aCatAnim.coord.y = screenHeight - getSprites().aCatAnim.rect.height() - ScreenKit.scaleHeight(catPos, screenHeight);
    }

    /**
     * Initialize only
     *
     * @param jumped
     * @param time
     */
    private void initCatPhysics(boolean jumped, long time) {

        // set cat
//		final int catWidth = Float.valueOf(screenWidth * 0.2f).intValue();
//		catHeight = catWidth * (getSprites().gCatAnim[0].image.getBounds().bottom - getSprites().gCatAnim[0].image.getBounds().top) / getSprites().gCatAnim[0].image.getBounds().right;

        catPos = GameValues.catPosGround;

        final int screenHeightCatStart = screenHeight - ScreenKit.scaleHeight(catPos, screenHeight);
        int screenWidthCatStart = ScreenKit.scaleWidth(20, screenWidth);
        if (isMedium()) {
            screenWidthCatStart = ScreenKit.scaleWidth(130, screenWidth);
        } else if (isHard()) {
            screenWidthCatStart = ScreenKit.scaleWidth(200, screenWidth);
        }

//		getSprites().gCatAnim[0].image.setBounds(screenWidthCatStart, screenHeightCatStart, screenWidthCatStart + catWidth, screenHeightCatStart + catHeight);
        getSprites().aCatAnim.rect = getSprites().gCatAnim[0].image.copyBounds();
        getSprites().aCatAnim.coord.x = screenWidthCatStart;
        getSprites().aCatAnim.coord.y = screenHeightCatStart - getSprites().aCatAnim.rect.height();
    }

    public synchronized BasicGame getGame() {
        return mGame;
    }

    /**
     * Starts the game, setting parameters for the current difficulty.
     */
    public void doStart() {
        if (mMode == GameState.NONE) {
            setMode(GameState.INIT);
        }
    }

    /**
     * Pauses the physics update & animation.
     */
    public synchronized void pause() {
        saveGameState();
        setSubMode(GameSubState.PAUSE);
    }

    /**
     * Resumes from a pause.
     */
    public synchronized void unpause() {
        //set state back to running
        lastTime = System.currentTimeMillis();
        setSubMode(GameSubState.NONE);
    }

    public synchronized void exit() {
        super.exit();
        getSprites().clean();
    }

    public synchronized void create() {
        delay = GameValues.gameRoundDelay;
        super.create();
    }

    public synchronized void restoreGameState() {
        log("restoreGameState()");

        CurrentGame cg = SystemHelper.getCurrentGame();
        if (cg.life > 0.0f && cg.time > 0L) {
            mGame.currentRound = 0;
            mGame.getCurrentRound().round = cg.round;
            mGame.getCurrentRound().level = cg.level;
            mGame.jumped = cg.jumped;
            mGame.life = cg.life;
            mGame.points = cg.points;
            mGame.coins = cg.coins;
            if (mGame.life <= 0.0f || mGame.currentRound < 0) {
                mGame.reset();
                setMode(GameState.NONE);
            }
            // update time
            if (mGame.hasGame()) {
                mGame.getCurrentRound().time = cg.time;
            }
            if (mGame.getCurrentRound().time <= 0l) {
                mGame.reset();
                setMode(GameState.NONE);
            }
        }

        if (mGame.hasGame() && mGame.getCurrentRound().level >= 0) {
            updateRoundGraphic();
        }
        initCatPhysics(mGame.jumped, System.currentTimeMillis());
    }

    public synchronized void saveGameState() {
        log("saveGameState()");
        CurrentGame cg = SystemHelper.getCurrentGame();

        final boolean finished = mGame.finished();
        if (!finished) {
            cg.round = mGame.currentRound;
            cg.level = mGame.getCurrentRound().level;
            cg.jumped = mGame.jumped;
            cg.life = mGame.life;
            cg.points = mGame.points;
            cg.coins = mGame.coins;
            cg.time = mGame.getCurrentRound().time;
        } else {
            // reset after won
            cg.round = -1;
            cg.level = -1;
            cg.jumped = false;
            cg.points = 0;
            cg.coins = 0;
            cg.life = GameValues.totalLife;
            cg.time = 0L;
        }

        DBDriver.getInstance().store(cg);
    }

    /**
     * Restores game state from the indicated Bundle. Typically called when the
     * Activity is being restored after having been previously destroyed.
     *
     * @param savedState Bundle containing the game state
     */
    public synchronized void restoreState(Bundle savedState) {
        setMode(GameState.INIT);
        restoreGameState();
    }

    /**
     * Dump game state to the provided Bundle. Typically called when the
     * Activity is being suspended.
     *
     * @return Bundle with this view's state
     */
    public Bundle saveState(Bundle map) {
        if (map != null) {
            saveGameState();
        }
        return map;
    }

    @Override
    public void doUpdateRenderState() {
        final long time = System.currentTimeMillis();

        if (delay > 0l && lastTime > 0l) {
            delay -= time - lastTime;
        }
        if (jumpDelay > 0l && lastTime > 0l) {
            jumpDelay -= time - lastTime;
        }

        switch (mMode) {
            case NONE: {
                // move to initialization
                setMode(GameState.INIT);
                setSubMode(GameSubState.CHOOSE_LEVEL);
            }
            break;
            case INIT: {
                if (mGame.currentRound < 0) {
                    mGame.currentRound = 0;
                }

                if (mGame.getCurrentRound().level < 0) {
                    setSubMode(GameSubState.CHOOSE_LEVEL);
                }

                if (mSubMode == GameSubState.NONE) {

                    updateRoundGraphic();

                    if (mGame.getCurrentRound().night) {
                        mGame.getCurrentRound().night = false;
                    }

                    // setup the game
                    setMode(GameState.READY);
                }

            }
            break;
            case READY: {
                setMode(GameState.PLAY);
            }
            break;
            case PLAY: {
                // active gameplay

                if (fpsdelay <= 0L) {
                    fpsdelay = GameValues.movementDelay;
                } else if (fpsdelay > 0L && lastTime > 0L) {
                    fpsdelay -= time - lastTime;
                }

                // calculate game time
                if (delay <= 0L && lastTime > 0L) {
                    if (mSubMode == GameSubState.NONE) {
                        mGame.getCurrentRound().time -= time - lastTime;
                    }
                }

                if (mGame.getCurrentRound().time > GameValues.dayTime + GameValues.nightTime) {
                    mGame.getCurrentRound().night = false;
                } else if (mGame.getCurrentRound().time < GameValues.dayTime + GameValues.nightTime) {
                    mGame.getCurrentRound().night = true;
                }
                if (mGame.getCurrentRound().time < GameValues.dayTime) {
                    mGame.getCurrentRound().night = false;
                }

                // update graphic positions
                updatePhysics();

                // end game
                if (mGame.getCurrentRound().time < 0) {
                    setMode(GameState.END);
                    mGame.complete = true;
                    bonusPoints = Float.valueOf(Long.valueOf(mGame.points).floatValue() * GameValues.pointsBonusPerRound[mGame.getCurrentRound().level]).intValue() - mGame.points;
                    bonusCoins = Float.valueOf(Long.valueOf(mGame.coins).floatValue() * GameValues.coinsBonusPerRound[mGame.getCurrentRound().level]).intValue() - mGame.coins;
                    if (isMedium()) {
                        bonusPoints = Float.valueOf((float) bonusPoints * GameValues.pointsBonusDifficulty[1]).intValue();
                        bonusCoins = Float.valueOf((float) bonusCoins * GameValues.coinsBonusDifficulty[1]).intValue();
                    }
                    if (isHard()) {
                        bonusPoints = Float.valueOf((float) bonusPoints * GameValues.pointsBonusDifficulty[2]).intValue();
                        bonusCoins = Float.valueOf((float) bonusCoins * GameValues.coinsBonusDifficulty[2]).intValue();
                    }
                    updateStatistics();
                    mGame.credited = true;
                }
                if (mGame.life <= 0.0F) {
                    setMode(GameState.END);
                    mGame.complete = true;
                    if (!mGame.credited && SystemHelper.hasExtensionByName(Constants.EXTENSION_SKILL_KEEP_POINTS_COINS)) {
                        updateStatistics();
                        mGame.credited = true;
                    }
                }

            }
            break;
            case END: {
                if (mGame.finished() && mGame.complete) {
                    // update once
                    if (mGame.currentRound > -1) {
                        mGame.reset();
                    }
                }
            }
            break;
            default:
                setMode(GameState.NONE);
                break;
        }

        lastTime = time;
    }

    private void updateRoundGraphic() {
        // create background
        getSprites().gGameBackground1 = loadGraphic(GameValues.backgroundPerRound[mGame.getCurrentRound().level]);
        getSprites().gGameBackground1.image.setBounds(0, 0, screenWidth, screenHeight);
        getSprites().gGameBackground2 = loadGraphic(GameValues.backgroundPerRound[mGame.getCurrentRound().level]);
        getSprites().gGameBackground2.image.setBounds(0, 0, screenWidth, screenHeight);

        coinDelay = (GameValues.roundTime - GameValues.createCoinBuffer) / GameValues.coinsPerRound[mGame.getCurrentRound().level];
    }

    private void updateStatistics() {
        log("updateStatistics()");

        SystemHelper.getInventory().points += mGame.points;
        SystemHelper.getInventory().coins += mGame.coins;
        SystemHelper.getInventory().points += bonusPoints;
        SystemHelper.getInventory().coins += bonusCoins;

        DBDriver.getInstance().store(SystemHelper.getInventory());
    }

    /**
     * Used to signal the thread whether it should be running or not. Passing
     * true allows the thread to run; passing false will shut it down if it's
     * already running. Calling start() after this was most recently called with
     * false will result in an immediate shutdown.
     *
     * @param b true to run, false to shut down
     */
    public void setRunning(boolean b) {
        super.setRunning(b);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (mMode == GameState.PLAY) {
            if (mSubMode == GameSubState.NONE) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:

                        if (containsClick(getSprites().gJumpButton[0].image.getBounds(), event.getX(), event.getY())) {
                            pressed = true;
                            if (!inJump) {
                                mGame.jumped = true;
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (containsClick(getSprites().gIconPause, event.getX(), event.getY())) {
                            setSubMode(GameSubState.PAUSE);
                        }

                        if (containsClick(getSprites().gJumpButton[0].image.getBounds(), event.getX(), event.getY())) {
                            pressed = false;
                            mGame.jumped = false;
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // move
                        break;
                }
            }
        }

        if (mMode == GameState.END) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                        delayedActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
                    }
            }
        }

        if (mSubMode == GameSubState.PAUSE) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:

                    if (containsClick(getSprites().gIconResume, event.getX(), event.getY())) {
                        setSubMode(GameSubState.NONE);
                    }
                    if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                        delayedActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
                    }

                    if (containsClick(getSprites().rBtnReset, event.getX(), event.getY())) {
                        mGame.reset();
                        mGame.life = 0.0f;
                        delayedActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
                    }
                    if (containsClick(getSprites().rButtonCatFood, event.getX(), event.getY())) {
                        Extensions ext = SystemHelper.getExtensions(Constants.EXTENSION_ITEM_CATFOOD);
                        if (ext.amount > -1 && mGame.life < maxLife) {
                            ext.amount -= 1;
                            mGame.life += GameValues.lifeCatFood;
                            if (mGame.life > maxLife) {
                                mGame.life = maxLife;
                            }
                            DBDriver.getInstance().store(ext);
                        }
                    }
                    if (containsClick(getSprites().rButtonCatMilk, event.getX(), event.getY())) {
                        Extensions ext = SystemHelper.getExtensions(Constants.EXTENSION_ITEM_CATMILK);
                        if (ext.amount > -1 && mGame.life < maxLife) {
                            ext.amount -= 1;
                            mGame.life += GameValues.lifeCatMilk;
                            if (mGame.life > maxLife) {
                                mGame.life = maxLife;
                            }
                            DBDriver.getInstance().store(ext);
                        }
                    }
                    break;
            }
        }


        if (mMode == GameState.INIT) {
            if (mSubMode == GameSubState.CHOOSE_LEVEL) {
                int x = Float.valueOf(event.getX()).intValue();
                int y = Float.valueOf(event.getY()).intValue();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        swipeStartX = x;
                        break;
                    case MotionEvent.ACTION_UP:
                        int swipeEndX = x;
//    				int swipeEndY = y;

                        // swiped through one third of the screen
                        int swipeRangeLR = swipeStartX - swipeEndX;
                        if (swipeRangeLR < 0) {
                            swipeRangeLR *= -1;
                        }
                        boolean swipedLR = screenWidth / 3 < swipeRangeLR;

                        if (swipedLR) {
                            if (swipeEndX > swipeStartX) {
                                // swiped left
                                swipeLeft();
                            } else {
                                // swiped right
                                swipeRight();
                            }
                        } else if (!swipedLR) {
                            // just clicked
                            if (!requireUpgrade() && containsClick(getSprites().rLevelSelect, event.getX(), event.getY())) {
                                createGame();
                                mGame.getCurrentRound().level = previewLevel;
                                updateRoundGraphic();
                                setMode(GameState.READY);
                                setSubMode(GameSubState.NONE);
                            }

                            if (previewLevel > 0) {
                                Rect tmpLeft = getSprites().gPreviewLevels.image.copyBounds();
                                tmpLeft.offset(tmpLeft.width() * -1, 0);
                                if (tmpLeft.contains(x, y)) {
                                    swipeLeft();
                                }
                            }
                            if (previewLevel + 1 < previewLevelMax) {
                                Rect tmpRight = getSprites().gPreviewLevels.image.copyBounds();
                                tmpRight.offset(tmpRight.width(), 0);
                                if (tmpRight.contains(x, y)) {
                                    swipeRight();
                                }
                            }
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        // move
                        break;
                }
            }
        }

        return true;
    }


    /**
     * Update the graphic x/y values in real time. This is called before the
     * draw() method
     */
    private void updatePhysics() {

        // the fixed time for drawing this frame
        final long time = System.currentTimeMillis();

        if (mMode == GameState.PLAY) {

            if (mSubMode == GameSubState.NONE) {

                if (mGame.getCurrentRound().night) {
                    if (nightDimming < GameValues.nightMax) {
                        if (lastDim + GameValues.nightDimming < time) {
                            lastDim = time;
                            nightDimming += GameValues.nightStep;
                        }
                        pNight.setAlpha(nightDimming);
                        if (pNight.getAlpha() > GameValues.nightMax) {
                            pNight.setAlpha(GameValues.nightMax);
                        }
                    }
                } else {
                    if (nightDimming > GameValues.nightMin) {
                        if (lastDim + GameValues.nightDimming < time) {
                            lastDim = time;
                            nightDimming -= GameValues.nightStep;
                        }
                        pNight.setAlpha(nightDimming);
                        if (pNight.getAlpha() < GameValues.nightMin) {
                            pNight.setAlpha(GameValues.nightMin);
                        }
                    }
                }

                // update cat position prior to other data
                if (mGame.jumped && catPos < GameValues.catPosJumped) {
                    updateCatPhysics(mGame.jumped, time);
                    inJump = true;
                } else if (!mGame.jumped && catPos > GameValues.catPosGround) {
                    updateCatPhysics(mGame.jumped, time);
                } else if (mGame.jumped && catPos >= GameValues.catPosJumped) {
                    // invert jumping as we reached the top position
                    if (jumpDelay <= 0 && pressed && !exhausted) {
                        jumpDelay = GameValues.catJumpAirTime[isHard() ? 2 : isMedium() ? 1 : 0];
                        if (extensionBetterJump > -1) {
                            jumpDelay += GameValues.upgradeBetterJump[extensionBetterJump];
                        }
                        exhausted = true;
                    }
                    if (jumpDelay <= 0 && inJump) {
                        mGame.jumped = false;
                    }
                } else if (!mGame.jumped && inJump && catPos <= GameValues.catPosGround) {
                    inJump = false;
                    exhausted = false;
                }


                final boolean movement = fpsdelay <= 0l;

                final int pixelMove = movement ? ScreenKit.scaleWidth(GameValues.movementPixel, screenWidth) : 0;

                if (getSprites().gGameBackground1 != null) {
                    getSprites().gGameBackground1.image.setBounds(
                            getSprites().gGameBackground1.image.getBounds().left - pixelMove,
                            getSprites().gGameBackground1.image.getBounds().top,
                            getSprites().gGameBackground1.image.getBounds().right - pixelMove,
                            getSprites().gGameBackground1.image.getBounds().bottom
                    );
                    if (getSprites().gGameBackground1.image.getBounds().left < -1 * (getSprites().gGameBackground1.image.getBounds().right - getSprites().gGameBackground1.image.getBounds().left)) {
                        getSprites().gGameBackground1.image.setBounds(
                                0,
                                getSprites().gGameBackground1.image.getBounds().top,
                                getSprites().gGameBackground1.image.getBounds().right - getSprites().gGameBackground1.image.getBounds().left,
                                getSprites().gGameBackground1.image.getBounds().bottom
                        );
                    }
                }

                if (movement) {
                    getSprites().aCatAnim.nextFrame(time);
                }

                if (movement) {
                    // create trap
                    boolean createTrap = lastTrap + GameValues.createTrapDelay < time && rnd.nextFloat() < GameValues.trapPerRound[mGame.getCurrentRound().level] && count(GameValues.TYPE_TRAP, getSprites().gObstacle) < GameValues.maxTrapPerRound[mGame.getCurrentRound().level];
                    if (createTrap) {
                        final int trap = rnd.nextInt(GameValues.trapTypes.length);
                        lastTrap = time;
                        getSprites().gObstacle.add(new Obstacle(GameValues.TYPE_TRAP, GameValues.lifePoint, trap, 0));
                        calculateStartPosition(getSprites().gObstacle.get(getSprites().gObstacle.size() - 1), screenWidth, screenHeight, GameValues.trapSize[trap]);
                    }
                    // create food
                    boolean createFood = !createTrap && lastFood + GameValues.createFoodDelay < time && rnd.nextFloat() < GameValues.foodPerRound[mGame.getCurrentRound().level] && count(GameValues.TYPE_FOOD, getSprites().gObstacle) < GameValues.maxFoodPerRound[mGame.getCurrentRound().level];
                    if (createFood) {
                        final int food = rnd.nextInt(GameValues.foodTypes.length);
                        lastFood = time;
                        getSprites().gObstacle.add(new Obstacle(GameValues.TYPE_FOOD, GameValues.lifePoint, food, GameValues.foodPoints[food]));
                        calculateStartPosition(getSprites().gObstacle.get(getSprites().gObstacle.size() - 1), screenWidth, screenHeight, GameValues.foodSize[food]);
                    }
                    boolean createCoin = lastCoin + coinDelay < time && mGame.totalCoins < GameValues.coinsPerRound[mGame.getCurrentRound().level];
                    if (createCoin) {
                        lastCoin = time;
                        mGame.totalCoins += 1;
                        getSprites().gObstacle.add(new Obstacle(GameValues.TYPE_COIN, 0.0f, 0, 1));
                        calculateStartPosition(getSprites().gObstacle.get(getSprites().gObstacle.size() - 1), screenWidth, screenHeight, GameValues.coinSize);
                    }

                }

                List<Obstacle> removals = new ArrayList<Obstacle>();

                Rect catRect = new Rect(getSprites().aCatAnim.rect);
                catRect.left += getSprites().aCatAnim.rect.width() - Float.valueOf(getSprites().aCatAnim.rect.width() * 0.75f).intValue();
                catRect.right -= getSprites().aCatAnim.rect.width() - Float.valueOf(getSprites().aCatAnim.rect.width() * 0.75f).intValue();
                catRect.top += getSprites().aCatAnim.rect.height() - Float.valueOf(getSprites().aCatAnim.rect.height() * 0.75f).intValue();
                catRect.bottom -= getSprites().aCatAnim.rect.height() - Float.valueOf(getSprites().aCatAnim.rect.height() * 0.75f).intValue();
                for (Obstacle obstc : getSprites().gObstacle) {
                    // movetime expired - move osbstacle, set new movetime
                    if (movement) {
                        obstc.rect.set(
                                obstc.rect.left - pixelMove,
                                obstc.rect.top,
                                obstc.rect.right - pixelMove,
                                obstc.rect.bottom
                        );
                    }

                    // obstacle moved outside of the screen - initialize again ( by removing the obstacle )
                    if (obstc.rect.right < 0) {
                        removals.add(obstc);
                    }

                    // cat catches food or trap
                    if (Rect.intersects(obstc.rect, catRect) && !obstc.hit) {
                        switch (obstc.type) {
                            case GameValues.TYPE_FOOD:
                                mGame.life += obstc.life;
                                if (extensionRegeneration > -1) {
                                    mGame.life += GameValues.upgradeRegeneration[extensionRegeneration];
                                }
                                mGame.points += obstc.points;
                                if (isMedium()) {
                                    mGame.points += obstc.points;
                                } else if (isHard()) {
                                    mGame.points += obstc.points * 2;
                                }
                                if (mGame.life > maxLife) {
                                    mGame.life = maxLife;
                                }
                                obstc.hit = true;
                                removals.add(obstc);
                                break;
                            case GameValues.TYPE_TRAP:
                                // either the cat contains the center of the object or reverse
                                if (obstc.rect.contains(getSprites().aCatAnim.rect.centerX(), getSprites().aCatAnim.rect.centerY()) ||
                                        getSprites().aCatAnim.rect.contains(obstc.rect.centerX(), obstc.rect.centerY())) {
                                    mGame.life -= obstc.life;
                                    obstc.hit = true;
                                    removals.add(obstc);
                                }
                                break;
                            case GameValues.TYPE_COIN:
                                mGame.coins += obstc.points;
                                obstc.hit = true;
                                removals.add(obstc);
                                break;
                        }
                    }

                }
                // remove obstacle
                if (removals.size() > 0) {
                    getSprites().gObstacle.removeAll(removals);
                    removals.clear();
                }


            }

        }
    }

    /**
     * Draws the graphics onto the Canvas.
     */
    @Override
    public void doDrawRenderer(Canvas canvas) {
        // Draw the background image. Operations on the Canvas accumulate
        // so this is like clearing the screen.
        if (mMode != GameState.PLAY) {

            if (getSprites().gBackground != null) {
                // draw image across screen
                int h = 0;
                int v = 0;
                Rect r = getSprites().gBackground.image.copyBounds();
                r.offsetTo(0, 0);
                getSprites().gBackground.image.setBounds(r);
                getSprites().gBackground.image.draw(canvas);
                while (h < screenWidth && v < screenHeight) {
                    r = getSprites().gBackground.image.copyBounds();
                    h = r.right;
                    if (h > screenWidth) {
                        v = r.bottom;
                        h = 0;
                    }
                    r.offsetTo(h, v);
                    getSprites().gBackground.image.setBounds(r);
                    if (v > screenHeight) {
                        r.offsetTo(0, 0);
                        getSprites().gBackground.image.setBounds(r);
                        break;
                    }
                    getSprites().gBackground.image.draw(canvas);
                }
            }
        }

        if (mMode == GameState.PLAY) {
            if (getSprites().gGameBackground1 != null) {
                getSprites().gGameBackground1.image.draw(canvas);
                if (getSprites().gGameBackground2 != null) {
                    // make second background to simulate movement
                    getSprites().gGameBackground2.image.setBounds(
                            getSprites().gGameBackground1.image.getBounds().right,
                            getSprites().gGameBackground2.image.getBounds().top,
                            getSprites().gGameBackground1.image.getBounds().right + getSprites().gGameBackground1.image.getBounds().right - getSprites().gGameBackground1.image.getBounds().left,
                            getSprites().gGameBackground2.image.getBounds().bottom
                    );
                    getSprites().gGameBackground2.image.draw(canvas);
                }
            }
        }

        // the fixed time for drawing this frame
        final long time = System.currentTimeMillis();

        if (mMode == GameState.PLAY) {

            if (mGame.hasGame() && mGame.getCurrentRound() != null) {

                getSprites().aCatAnim.nextFrame();
                getSprites().aCatAnim.rect.offsetTo(getSprites().aCatAnim.coord.x, getSprites().aCatAnim.coord.y);
                getSprites().gCatAnim[getSprites().aCatAnim.nextFrame().gReference].image.setBounds(getSprites().aCatAnim.rect);
                getSprites().gCatAnim[getSprites().aCatAnim.nextFrame().gReference].image.draw(canvas);


                // obstacle animation - draw obstacle
                // draw traps
                for (Obstacle obstc : getSprites().gObstacle) {
                    if (obstc.type == GameValues.TYPE_TRAP) {
                        getSprites().gTraps[obstc.gReference].image.setBounds(obstc.rect);
                        getSprites().gTraps[obstc.gReference].image.draw(canvas);
                    }
                }
                // draw foods
                for (Obstacle obstc : getSprites().gObstacle) {
                    if (obstc.type == GameValues.TYPE_FOOD) {
                        getSprites().gFoods[obstc.gReference].image.setBounds(obstc.rect);
                        getSprites().gFoods[obstc.gReference].image.draw(canvas);
                    }
                }
                // draw coins
                for (Obstacle obstc : getSprites().gObstacle) {
                    if (obstc.type == GameValues.TYPE_COIN) {
                        getSprites().gCoins[obstc.gReference].image.setBounds(obstc.rect);
                        getSprites().gCoins[obstc.gReference].image.draw(canvas);
                    }
                }

                // draw only if we have a night effect
                if (nightDimming > GameValues.nightMin) {
                    canvas.drawRect(0, 0, screenWidth, screenHeight, pNight);
                }

                // draw lifebar
                if (getSprites().gHealth != null) {
                    if (mGame.life >= 1.0f) {
                        getSprites().gHealth[10].image.draw(canvas);
                    } else if (mGame.life > 0.9f) {
                        getSprites().gHealth[9].image.draw(canvas);
                    } else if (mGame.life > 0.8f) {
                        getSprites().gHealth[8].image.draw(canvas);
                    } else if (mGame.life > 0.7f) {
                        getSprites().gHealth[7].image.draw(canvas);
                    } else if (mGame.life > 0.6f) {
                        getSprites().gHealth[6].image.draw(canvas);
                    } else if (mGame.life > 0.5f) {
                        getSprites().gHealth[5].image.draw(canvas);
                    } else if (mGame.life > 0.4f) {
                        getSprites().gHealth[4].image.draw(canvas);
                    } else if (mGame.life > 0.3f) {
                        getSprites().gHealth[3].image.draw(canvas);
                    } else if (mGame.life > 0.2f) {
                        getSprites().gHealth[2].image.draw(canvas);
                    } else if (mGame.life > 0.1f) {
                        getSprites().gHealth[1].image.draw(canvas);
                    } else if (mGame.life > 0.0f) {
                        getSprites().gHealth[0].image.draw(canvas);
                    } else if (mGame.life <= 0.0f) {
                        getSprites().gHealth[0].image.draw(canvas);
                    }
                }

                // Draw Time
                if (mGame.getCurrentRound().time > -1l) {
                    long minutes = (mGame.getCurrentRound().time) / 60000;
                    long seconds = (mGame.getCurrentRound().time) / 1000;
                    if (seconds > 60) {
                        seconds = seconds - (minutes * 60);
                    }
                    if (seconds == 60) {
                        seconds = 0;
                    }
                    String strValue = MessageFormat.format("{0}:{1,number,00}", minutes, seconds);
                    drawNumbers(canvas, 50, 115, strValue, null, standardNumberWidth * 0.6f, standardNumberHeight * 0.6f, GameValues.cFilterBlue);
                }
            }

            drawNumbers(canvas, 420, 10, mGame.points, getSprites().gPoints, standardNumberWidth, standardNumberHeight);
            drawNumbers(canvas, 420, 130, mGame.coins, getSprites().gCoin, standardNumberWidth, standardNumberHeight);
        }

        if (mMode == GameState.PLAY && mSubMode == GameSubState.NONE) {
//            getSprites().gButton.image.setBounds(new Rect(getSprites().rBtnPause));
//            getSprites().gButton.image.draw(canvas);
//            drawText(canvas, getSprites().rBtnPause, getString(R.string.menubutton_pause));

            drawJumpButton(canvas);

            getSprites().gIconPause.image.draw(canvas);

//            choiceBaseDraw(canvas, getSprites().rBtnPause, getSprites().gButtonOverlay, getSprites().gButton, activeButton, EngineConstants.ACTION_NONE, GameValues.cFilterGreen);
//            drawText(canvas, getSprites().rBtnPause, getString(R.string.menubutton_pause));
        }

        if (mMode == GameState.PLAY && mSubMode == GameSubState.PAUSE) {
            baseDraw(canvas, getSprites().rBtnBack, getSprites().gButtonRed);
            drawText(canvas, getSprites().rBtnBack, getString(R.string.menubutton_back), GameValues.cFilterRed);

//            getSprites().gButton.image.setBounds(new Rect(getSprites().rBtnResume));
//            getSprites().gButton.image.draw(canvas);

            getSprites().gIconResume.image.draw(canvas);

            baseDraw(canvas, getSprites().rButtonCatFood, getSprites().gButtonBlue);
            baseDraw(canvas, getSprites().rButtonCatMilk, getSprites().gButtonBlue);
            drawText(canvas, getSprites().rButtonCatFood, getString(R.string.text_button_catfood), GameValues.cFilterBlue);
            drawText(canvas, getSprites().rButtonCatMilk, getString(R.string.text_button_catmilk), GameValues.cFilterBlue);

            drawText(canvas, getSprites().rTextPause, getString(R.string.text_pause));

            baseDraw(canvas, getSprites().rBtnReset, getSprites().gButtonRed);
            drawText(canvas, getSprites().rBtnReset, getString(R.string.text_reset), GameValues.cFilterRed);

            // draw available items
            Extensions ext = SystemHelper.getExtensions(Constants.EXTENSION_ITEM_CATFOOD);
            drawText(canvas, getSprites().rCatFood, ext.amount + 1 + "X");

            ext = SystemHelper.getExtensions(Constants.EXTENSION_ITEM_CATMILK);
            drawText(canvas, getSprites().rCatMilk, ext.amount + 1 + "X");

        }

        if (mMode == GameState.READY) {
            drawText(canvas, getSprites().rMsgGameState, MessageFormat.format(getString(R.string.message_gameround), mGame.currentRound + 1), 0, 0);
        }

        if (mMode == GameState.END) {
            baseDraw(canvas, getSprites().rBtnBack, getSprites().gButtonRed);
            drawText(canvas, getSprites().rBtnBack, getString(R.string.menubutton_back), GameValues.cFilterRed);

            if (mGame.complete && mGame.life > 0.0f) {
                drawText(canvas, getSprites().rMsgGameState, getString(R.string.message_gamewon));

                drawText(canvas, getSprites().rTextBonus, getString(R.string.text_bonus));
                drawText(canvas, getSprites().rTextCollected, getString(R.string.text_collected));
                // draw collected and bonus score
                drawNumbers(canvas, 500, 130, mGame.points, getSprites().gPoints, standardNumberWidth, standardNumberHeight);
                drawNumbers(canvas, 500, 240, mGame.coins, getSprites().gCoin, standardNumberWidth, standardNumberHeight);
                drawNumbers(canvas, 500, 460, bonusPoints, getSprites().gPoints, standardNumberWidth, standardNumberHeight);
                drawNumbers(canvas, 500, 570, bonusCoins, getSprites().gCoin, standardNumberWidth, standardNumberHeight);

            } else {
                drawText(canvas, getSprites().rMsgGameState, getString(R.string.message_gamelost));

                if (SystemHelper.hasExtensionByName(Constants.EXTENSION_SKILL_KEEP_POINTS_COINS)) {
                    drawText(canvas, getSprites().rTextBonus, getString(R.string.text_bonus));
                    drawText(canvas, getSprites().rTextCollected, getString(R.string.text_collected));
                    // draw collected and bonus score
                    drawNumbers(canvas, 500, 130, mGame.points, getSprites().gPoints, standardNumberWidth, standardNumberHeight);
                    drawNumbers(canvas, 500, 240, mGame.coins, getSprites().gCoin, standardNumberWidth, standardNumberHeight);
                    drawNumbers(canvas, 500, 460, bonusPoints, getSprites().gPoints, standardNumberWidth, standardNumberHeight);
                    drawNumbers(canvas, 500, 570, bonusCoins, getSprites().gCoin, standardNumberWidth, standardNumberHeight);
                }

//                drawText(canvas, getSprites().rMsgScoreAward, MessageFormat.format(getString(R.string.message_bonus_coins), getSprites().pPlayer.score / GameValues.scoreDividerSinglePlayer), 0, 0, GameValues.cFilterYellow);
            }

//            drawText(canvas, getSprites().rMsgScore, getString(R.string.menubutton_score));
//            drawTextUnboundedScaled(canvas, getSprites().rScore, Long.toString(mGame.points), GameValues.cFilterYellow);

            // draw score
//            drawNumbers(canvas, 420, 10, mGame.points, null, standardNumberWidth, standardNumberHeight);
        }

        if (mMode == GameState.INIT && mSubMode == GameSubState.CHOOSE_LEVEL) {
            if (previewLevelLoaded != previewLevel) {
                getSprites().gPreviewLevels = loadGraphic(GameValues.levelImages[previewLevel]);
                ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, 0.50f, 0, 0, getSprites().gPreviewLevels);
                previewLevelLoaded = previewLevel;
            }
            getSprites().gPreviewLevels.image.draw(canvas);
            if (previewLevel > 0) {
                canvas.save();
                Rect tmpRect = getSprites().gPreviewLevels.image.copyBounds();
                tmpRect.offset(tmpRect.width() * -1, 0);
                getSprites().gLevelChooser.image.setBounds(tmpRect);
                getSprites().gLevelChooser.image.draw(canvas);
                canvas.restore();
            }
            if (previewLevel + 1 < previewLevelMax) {
                canvas.save();
                Rect tmpRect = getSprites().gPreviewLevels.image.copyBounds();
                tmpRect.offset(tmpRect.width(), 0);
                getSprites().gLevelChooser.image.setBounds(tmpRect);
                getSprites().gLevelChooser.image.draw(canvas);
                canvas.restore();
            }
            if (requireUpgrade()) {
                canvas.save();
                Rect tmpRect = getSprites().gPreviewLevels.image.copyBounds();
                //getSprites().gLevelUpgrade.image.setBounds(tmpRect);
                //getSprites().gLevelUpgrade.image.draw(canvas);
                canvas.restore();
            } else {
                // only allow select button if the level is purchased
//				drawButtonBackground(canvas, getSprites().gLevelSelect);
//				getSprites().gLevelSelect.image.draw(canvas);
                baseDraw(canvas, getSprites().rLevelSelect, getSprites().gButtonBlue);
                drawText(canvas, getSprites().rLevelSelect, getString(R.string.text_levelselect));
            }
        }


    }


    private void drawJumpButton(Canvas canvas) {
        int btn = 0;

        if (mGame.jumped && pressed && inJump) {
            btn = 1;
        }
        if (!mGame.jumped && inJump) {
            btn = 2;
        }

        getSprites().gJumpButton[btn].image.draw(canvas);
    }


    public synchronized void setMode(GameState mode) {
        synchronized (mMode) {
            mMode = mode;
        }
    }

    public synchronized GameState getMode() {
        return mMode;
    }

    public synchronized void setSubMode(GameSubState mode) {
        mSubMode = mode;
    }

    public synchronized GameSubState getSubMode() {
        return mSubMode;
    }

    public boolean isPurchase() {
        return false;
    }

    public void setPurchase(boolean b) {
    }

    public void setBonus(boolean b) {
    }

    @Override
    public void actionHandler(int action) {
        // handle click actions directly to the game screen
        getScreen().actionHandler(action);
    }

    public GameViewSprites getSprites() {
        return GameViewSprites.class.cast(super.sprites);
    }


    @Override
    public void reset() {
    }

    @Override
    public float getCharSpacing() {
        return Constants.CHAR_SPACING;
    }


    private void swipeLeft() {
        if (previewLevel > 0) {
            previewLevel--;
        }
    }

    private void swipeRight() {
        if (previewLevel + 1 < previewLevelMax) {
            previewLevel++;
        }
    }

    private static int count(int type, List<Obstacle> list) {
        int cnt = 0;
        for (Obstacle o : list) {
            if (o.type == type) {
                cnt++;
            }
        }
        return cnt;
    }

    private boolean requireUpgrade() {
        return GameValues.levelPurchasable[previewLevel] != null && !SystemHelper.hasExtensionByName(GameValues.levelPurchasable[previewLevel]);
    }


    public void calculateStartPosition(Obstacle obstacle, int screenWidth, int screenHeight, float size) {

        final int foodWidth = Float.valueOf(screenWidth * size).intValue();

        int screenHeightStart = screenHeight - ScreenKit.scaleHeight(GameValues.catPosGround, screenHeight);

        if (obstacle.type == GameValues.TYPE_COIN) {
            int heightMin = screenHeight - ScreenKit.scaleHeight(GameValues.catPosJumped, screenHeight);
            int newHeight = rnd.nextInt(screenHeightStart);
            while (newHeight > screenHeightStart || newHeight < heightMin) {
                newHeight = rnd.nextInt(screenHeightStart);
            }
            screenHeightStart = newHeight;
        }

        int screenWidthStart = screenWidth;

        switch (obstacle.type) {
            case GameValues.TYPE_FOOD:
                obstacle.rect = getSprites().gFoods[obstacle.gReference].image.copyBounds();
                break;
            case GameValues.TYPE_TRAP:
                obstacle.rect = getSprites().gTraps[obstacle.gReference].image.copyBounds();
                break;
            case GameValues.TYPE_COIN:
                obstacle.rect = getSprites().gCoins[obstacle.gReference].image.copyBounds();
                break;
        }

        float factor = 1.0f;

        obstacle.rect.set(screenWidthStart,
                screenHeightStart - Float.valueOf(foodWidth * factor).intValue(),
                screenWidthStart + foodWidth,
                screenHeightStart);

    }

    private boolean isMedium() {
        return mGame.difficulty == GameValues.DIFFICULTY_MEDIUM;
    }

    private boolean isHard() {
        return mGame.difficulty == GameValues.DIFFICULTY_HARD;
    }

}
