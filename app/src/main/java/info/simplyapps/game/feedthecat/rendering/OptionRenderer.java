package info.simplyapps.game.feedthecat.rendering;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.widget.Toast;

import java.text.MessageFormat;
import java.util.Properties;
import java.util.Random;

import info.simplyapps.appengine.storage.dto.Configuration;
import info.simplyapps.appengine.storage.dto.Extensions;
import info.simplyapps.game.feedthecat.Constants;
import info.simplyapps.game.feedthecat.Constants.OptionRenderMode;
import info.simplyapps.game.feedthecat.SystemHelper;
import info.simplyapps.game.feedthecat.engine.GameValues;
import info.simplyapps.game.feedthecat.free.R;
import info.simplyapps.game.feedthecat.sprites.OptionViewSprites;
import info.simplyapps.game.feedthecat.storage.DBDriver;
import info.simplyapps.game.feedthecat.storage.dto.Inventory;
import info.simplyapps.gameengine.EngineConstants;
import info.simplyapps.gameengine.rendering.kits.Renderkit;
import info.simplyapps.gameengine.rendering.kits.ScreenKit;
import info.simplyapps.gameengine.rendering.kits.ScreenKit.ScreenPosition;
import info.simplyapps.gameengine.rendering.objects.Graphic;

public class OptionRenderer extends FeedTheCatRendererTemplate {

    private Random rnd;
    private OptionRenderMode mRenderMode;
    private Paint mLayer;
    private Paint mLayerBorder;
    private Configuration cDifficulty;

    // swipe handling
    private int swipeStartX;
    private int swipeStartY;
    private int swipeMedianX;
    private int swipeMedianY;
    private int swipePageLR;

    // navigation handling
    private int optionLoaded;

    public OptionRenderer(Context context, Properties p) {
        super(context, p);
        mRenderMode = OptionRenderMode.NONE;
    }

    public OptionViewSprites getSprites() {
        return OptionViewSprites.class.cast(super.sprites);
    }

    @Override
    public void doStart() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void unpause() {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (delayedAction == EngineConstants.ACTION_NONE) {
            // determine button click

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_MOVE:
                    // move
                    swipeMedianX = Float.valueOf(event.getX()).intValue();
                    swipeMedianY = Float.valueOf(event.getY()).intValue();
                    break;
                case MotionEvent.ACTION_DOWN:
                    // touch gesture started
                    swipeStartX = Float.valueOf(event.getX()).intValue();
                    swipeStartY = Float.valueOf(event.getY()).intValue();
                    break;
                case MotionEvent.ACTION_UP:
                    // touch gesture completed
                    int swipeEndX = Float.valueOf(event.getX()).intValue();
                    int swipeEndY = Float.valueOf(event.getY()).intValue();

                    // swiped through one third of the screen
                    int swipeRangeLR = swipeStartX - swipeEndX;
                    if (swipeRangeLR < 0) {
                        swipeRangeLR *= -1;
                    }
                    int swipeRangeTB = swipeStartY - swipeEndY;
                    if (swipeRangeTB < 0) {
                        swipeRangeTB *= -1;
                    }
                    boolean swipedLR = screenWidth / 3 < swipeRangeLR;
                    boolean swipedTB = screenHeight / 2 < swipeRangeTB;

                    if (swipedLR) {
                        if (swipeEndX > swipeStartX) {
                            // swiped left
                            if (mRenderMode == OptionRenderMode.UPGRADE || mRenderMode == OptionRenderMode.LEVEL) {
                                swipeLeft();
                            }
                        } else {
                            // swiped right
                            if (mRenderMode == OptionRenderMode.UPGRADE) {
                                swipeRight(GameValues.upgradeNames.length);
                            }
                            if (mRenderMode == OptionRenderMode.LEVEL) {
                                swipeRight(GameValues.levelNames.length);
                            }
                        }
                    } else if (!swipedTB) {
                        if (containsClick(getSprites().gSlideRight, event.getX(), event.getY())) {
                            if (mRenderMode == OptionRenderMode.UPGRADE) {
                                swipeRight(GameValues.upgradeNames.length);
                            }
                            if (mRenderMode == OptionRenderMode.LEVEL) {
                                swipeRight(GameValues.levelNames.length);
                            }
                        }
                        if (containsClick(getSprites().gSlideLeft, event.getX(), event.getY())) {
                            if (mRenderMode == OptionRenderMode.UPGRADE || mRenderMode == OptionRenderMode.LEVEL) {
                                swipeLeft();
                            }
                        }
                    }


                    if (Constants.OptionRenderMode.NONE == mRenderMode) {
                        if (containsClick(getSprites().rBtnSettings, event.getX(), event.getY())) {
                            updateRenderMode(OptionRenderMode.SETTINGS);
                        }
                        if (containsClick(getSprites().rBtnLevels, event.getX(), event.getY())) {
                            optionLoaded = 0;
                            swipePageLR = 0;
                            updateRenderMode(OptionRenderMode.LEVEL);
                        }
                        if (containsClick(getSprites().rBtnUpgrades, event.getX(), event.getY())) {
                            optionLoaded = 0;
                            swipePageLR = 0;
                            updateRenderMode(OptionRenderMode.UPGRADE);
                        }
                        if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                            delayedActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
                        }
                    } else if (Constants.OptionRenderMode.SETTINGS == mRenderMode) {

                        if (containsClick(getSprites().rButtonDifficultEasy, event.getX(), event.getY())) {
                            cDifficulty.value = Integer.toString(GameValues.DIFFICULTY_EASY);
                            DBDriver.getInstance().store(cDifficulty);
                            SystemHelper.setConfiguration(cDifficulty);
                        }
                        if (containsClick(getSprites().rButtonDifficultMedium, event.getX(), event.getY())) {
                            cDifficulty.value = Integer.toString(GameValues.DIFFICULTY_MEDIUM);
                            DBDriver.getInstance().store(cDifficulty);
                            SystemHelper.setConfiguration(cDifficulty);
                        }
                        if (containsClick(getSprites().rButtonDifficultHard, event.getX(), event.getY())) {
                            cDifficulty.value = Integer.toString(GameValues.DIFFICULTY_HARD);
                            DBDriver.getInstance().store(cDifficulty);
                            SystemHelper.setConfiguration(cDifficulty);
                        }

                        if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                            updateRenderMode(OptionRenderMode.NONE);
                            optionLoaded = -1;
                        }
                    } else if (Constants.OptionRenderMode.LEVEL == mRenderMode) {
                        if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                            updateRenderMode(OptionRenderMode.NONE);
                            optionLoaded = -1;
                        }
                        if (containsClick(getSprites().rBtnBuy, event.getX(), event.getY())) {
                            handleBuyLevel();
                        }
                    } else if (Constants.OptionRenderMode.UPGRADE == mRenderMode) {
                        if (containsClick(getSprites().rBtnBuy, event.getX(), event.getY())) {
                            handleBuyUpgrade();
                        }

                        if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                            updateRenderMode(OptionRenderMode.NONE);
                            optionLoaded = -1;
                        }
                    }
                    break;
            }

        }

        return true;
    }


    private void swipeLeft() {
        if (swipePageLR > 0) {
            swipePageLR--;
            handleSelectionGraphic();
        }
    }

    private void swipeRight(int max) {
        if (swipePageLR + 1 < max) {
            swipePageLR++;
            handleSelectionGraphic();
        }
    }

    private void handleSelectionGraphic() {
        switch (mRenderMode) {
            case UPGRADE:
                if (swipePageLR != optionLoaded) {
                    optionLoaded = swipePageLR;
                }
                break;
            case LEVEL:
                if (swipePageLR != optionLoaded) {
                    getSprites().gCurrentLevel = Renderkit.loadGraphic(mContext.getResources(), GameValues.levelId[swipePageLR], 0, 0);
                    ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.3f, 125, 50, getSprites().gCurrentLevel);
                    optionLoaded = swipePageLR;
                }
                break;
        }
    }

    private boolean hasLevel() {
        Extensions ext = SystemHelper.getExtensions(GameValues.levelNames[swipePageLR]);
        return ext != null && ext.amount >= 0;
    }

    private boolean canBuyLevel() {
        if (!hasLevel()) {
            Inventory inv = SystemHelper.getInventory();
            int requiredPoints = GameValues.levelPricesPointsCoins[swipePageLR][0];
            int requiredCoins = GameValues.levelPricesPointsCoins[swipePageLR][1];
            return inv.points >= requiredPoints && inv.coins >= requiredCoins;
        }
        return false;
    }

    private boolean canBuyUpgrade() {
        if (!hasMaxUpgrade()) {
            int level = 0;
            Extensions ext = SystemHelper.getExtensions(GameValues.upgradeNames[swipePageLR]);
            Inventory inv = SystemHelper.getInventory();
            if (!GameValues.upgradeNames[swipePageLR].equals(Constants.EXTENSION_ITEM_CATFOOD) && !GameValues.upgradeNames[swipePageLR].equals(Constants.EXTENSION_ITEM_CATMILK)) {
                level = ext.amount + 1;
            }
            if (level < GameValues.maxUpgrades[swipePageLR]) {
                int requiredPoints = GameValues.upgradePricesPointsCoins[swipePageLR][level][0];
                int requiredCoins = GameValues.upgradePricesPointsCoins[swipePageLR][level][1];
                return inv.points >= requiredPoints && inv.coins >= requiredCoins;
            }
        }
        return false;
    }

    private boolean hasMaxUpgrade() {
        Extensions ext = SystemHelper.getExtensions(GameValues.upgradeNames[swipePageLR]);
        if (ext.amount + 1 >= GameValues.maxUpgrades[swipePageLR]) {
            return true;
        }
        return false;
    }

    private void handleBuyLevel() {
        if (canBuyLevel()) {
            Extensions ext = SystemHelper.getExtensions(GameValues.levelNames[swipePageLR]);
            ext.amount += 1;
            if (DBDriver.getInstance().store(ext)) {
                Inventory inv = SystemHelper.getInventory();
                int requiredPoints = GameValues.upgradePricesPointsCoins[swipePageLR][ext.amount][0];
                int requiredCoins = GameValues.upgradePricesPointsCoins[swipePageLR][ext.amount][1];
                inv.points -= requiredPoints;
                inv.coins -= requiredCoins;
                if (DBDriver.getInstance().store(inv)) {
                    SystemHelper.setExtensions(ext);
                } else {
                    Toast.makeText(getScreen(), R.string.save_failed, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getScreen(), R.string.save_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleBuyUpgrade() {
        if (canBuyUpgrade()) {
            Extensions ext = SystemHelper.getExtensions(GameValues.upgradeNames[swipePageLR]);
            ext.amount += 1;
            if (DBDriver.getInstance().store(ext)) {
                int level = 0;
                if (!GameValues.upgradeNames[swipePageLR].equals(Constants.EXTENSION_ITEM_CATFOOD) && !GameValues.upgradeNames[swipePageLR].equals(Constants.EXTENSION_ITEM_CATMILK)) {
                    level = ext.amount;
                }
                Inventory inv = SystemHelper.getInventory();
                int requiredPoints = GameValues.upgradePricesPointsCoins[swipePageLR][level][0];
                int requiredCoins = GameValues.upgradePricesPointsCoins[swipePageLR][level][1];
                inv.points -= requiredPoints;
                inv.coins -= requiredCoins;
                if (DBDriver.getInstance().store(inv)) {
                    SystemHelper.setExtensions(ext);
                } else {
                    Toast.makeText(getScreen(), R.string.save_failed, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getScreen(), R.string.save_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void doUpdateRenderState() {
    }

    @Override
    public void doDrawRenderer(Canvas canvas) {

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

        if (Constants.OptionRenderMode.NONE == mRenderMode) {
            getSprites().gTextLogo.image.draw(canvas);

            // draw buttons last to overlay the background items
            choiceBaseDraw(canvas, getSprites().rBtnSettings, getSprites().gButtonBlue, getSprites().gButtonBlue, activeButton, EngineConstants.ACTION_OPTIONS, GameValues.cFilterBlue);
            choiceBaseDraw(canvas, getSprites().rBtnLevels, getSprites().gButtonBlue, getSprites().gButtonBlue, activeButton, EngineConstants.ACTION_OPTIONS, GameValues.cFilterBlue);
            choiceBaseDraw(canvas, getSprites().rBtnUpgrades, getSprites().gButtonBlue, getSprites().gButtonBlue, activeButton, EngineConstants.ACTION_OPTIONS, GameValues.cFilterBlue);

            drawText(canvas, getSprites().rBtnSettings, getString(R.string.text_button_settings), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
            drawText(canvas, getSprites().rBtnLevels, getString(R.string.text_button_levels), ScreenKit.scaleWidth(Constants.spaceMainBtnLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceMainBtnTB, screenHeight));
            drawText(canvas, getSprites().rBtnUpgrades, getString(R.string.text_button_upgrades), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));

        } else if (Constants.OptionRenderMode.SETTINGS == mRenderMode) {
            drawSettings(canvas);
        } else if (Constants.OptionRenderMode.UPGRADE == mRenderMode) {
            drawUpgrades(canvas);
        } else if (Constants.OptionRenderMode.LEVEL == mRenderMode) {
            drawLevels(canvas);
        }

        choiceBaseDraw(canvas, getSprites().rBtnBack, getSprites().gButtonBlue, getSprites().gButtonBlue, activeButton, EngineConstants.ACTION_OPTIONS, GameValues.cFilterBlue);
        drawText(canvas, getSprites().rBtnBack, getString(R.string.menubutton_back), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));

    }

    private void drawSettings(Canvas canvas) {
        drawTextUnboundedScaled(canvas, getSprites().rMsgDifficulty, getString(R.string.options_difficulty), GameValues.cFilterBlue);

        choiceBaseDraw(canvas, getSprites().rButtonDifficultEasy, getSprites().gButtonOverlay, getSprites().gButton, Integer.valueOf(cDifficulty.value), GameValues.DIFFICULTY_EASY, GameValues.cFilterGreen);
        choiceBaseDraw(canvas, getSprites().rButtonDifficultMedium, getSprites().gButtonOverlay, getSprites().gButton, Integer.valueOf(cDifficulty.value), GameValues.DIFFICULTY_MEDIUM, GameValues.cFilterGreen);
        choiceBaseDraw(canvas, getSprites().rButtonDifficultHard, getSprites().gButtonOverlay, getSprites().gButton, Integer.valueOf(cDifficulty.value), GameValues.DIFFICULTY_HARD, GameValues.cFilterGreen);
        drawText(canvas, getSprites().rButtonDifficultEasy, getString(R.string.easy));
        drawText(canvas, getSprites().rButtonDifficultMedium, getString(R.string.medium));
        drawText(canvas, getSprites().rButtonDifficultHard, getString(R.string.hard));
    }

    private void drawUpgrades(Canvas canvas) {
        drawTextUnbounded(canvas, getSprites().rUpgradeText, getString(R.string.text_upgrade), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
        drawTextUnbounded(canvas, getSprites().rCurrentUpgrade, getString(GameValues.upgradeId[swipePageLR]), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight), GameValues.cFilterBlue);

        if (swipePageLR == 0) {
            getSprites().gSlideRight.image.draw(canvas);
        } else if (swipePageLR + 1 >= GameValues.upgradeNames.length) {
            getSprites().gSlideLeft.image.draw(canvas);
        } else {
            getSprites().gSlideLeft.image.draw(canvas);
            getSprites().gSlideRight.image.draw(canvas);
        }

        Extensions ext = SystemHelper.getExtensions(GameValues.upgradeNames[swipePageLR]);
        if (ext != null) {
            int level = 0;
            int extLvl = -1;
            if (!GameValues.upgradeNames[swipePageLR].equals(Constants.EXTENSION_ITEM_CATFOOD) && !GameValues.upgradeNames[swipePageLR].equals(Constants.EXTENSION_ITEM_CATMILK)) {
                level = ext.amount + 1;
                extLvl = level;
            }

            drawTextUnbounded(canvas, getSprites().rTextPurchased, MessageFormat.format(getString(R.string.text_purchased), Integer.toString(ext.amount + 1), GameValues.maxUpgrades[swipePageLR]));
            if (!hasMaxUpgrade()) {
                if (extLvl > -1) {
                    getSprites().gExtensionLevel[extLvl].image.draw(canvas);
                }

                drawTextUnbounded(canvas, getSprites().rTextCost, getString(R.string.text_cost));
                drawTextUnbounded(canvas, getSprites().rTextAvailable, getString(R.string.text_available));
                drawTextUnbounded(canvas, getSprites().rPointsCost, Integer.toString(GameValues.upgradePricesPointsCoins[swipePageLR][level][0]));
                drawTextUnbounded(canvas, getSprites().rCoinsCost, Integer.toString(GameValues.upgradePricesPointsCoins[swipePageLR][level][1]));
                drawTextUnbounded(canvas, getSprites().rPointsAvailable, Long.toString(SystemHelper.getInventory().points));
                drawTextUnbounded(canvas, getSprites().rCoinsAvailable, Long.toString(SystemHelper.getInventory().coins));
                getSprites().gCoin.image.setBounds(getSprites().rCoins);
                getSprites().gCoin.image.draw(canvas);
                getSprites().gPoints.image.setBounds(getSprites().rPoints);
                getSprites().gPoints.image.draw(canvas);
                getSprites().gCoin.image.setBounds(getSprites().rCoins2);
                getSprites().gCoin.image.draw(canvas);
                getSprites().gPoints.image.setBounds(getSprites().rPoints2);
                getSprites().gPoints.image.draw(canvas);

                if (canBuyUpgrade()) {
                    baseDraw(canvas, getSprites().rBtnBuy, getSprites().gButtonBlue);
                    drawText(canvas, getSprites().rBtnBuy, getString(R.string.btn_buy), GameValues.cFilterGreen);
                } else {
                    baseDraw(canvas, getSprites().rBtnBuy, getSprites().gButtonRed);
                    drawText(canvas, getSprites().rBtnBuy, getString(R.string.btn_buy), GameValues.cFilterRed);
                }
            }
        }
    }

    private void drawLevels(Canvas canvas) {
        getSprites().gCurrentLevel.image.draw(canvas);

        drawTextUnbounded(canvas, getSprites().rUpgradeText, getString(R.string.text_level), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
        drawTextUnbounded(canvas, getSprites().rCurrentUpgrade, getString(GameValues.levelNameId[swipePageLR]), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight), GameValues.cFilterBlue);

        if (swipePageLR == 0) {
            getSprites().gSlideRight.image.draw(canvas);
        } else if (swipePageLR + 1 >= GameValues.levelNames.length) {
            getSprites().gSlideLeft.image.draw(canvas);
        } else {
            getSprites().gSlideLeft.image.draw(canvas);
            getSprites().gSlideRight.image.draw(canvas);
        }

        Extensions ext = SystemHelper.getExtensions(GameValues.levelNames[swipePageLR]);
        if (ext != null) {
            if (!hasLevel()) {
                drawTextUnbounded(canvas, getSprites().rTextCost, getString(R.string.text_cost));
                drawTextUnbounded(canvas, getSprites().rTextAvailable, getString(R.string.text_available));
                drawTextUnbounded(canvas, getSprites().rPointsCost, Integer.toString(GameValues.levelPricesPointsCoins[swipePageLR][0]));
                drawTextUnbounded(canvas, getSprites().rCoinsCost, Integer.toString(GameValues.levelPricesPointsCoins[swipePageLR][1]));
                drawTextUnbounded(canvas, getSprites().rPointsAvailable, Long.toString(SystemHelper.getInventory().points));
                drawTextUnbounded(canvas, getSprites().rCoinsAvailable, Long.toString(SystemHelper.getInventory().coins));
                getSprites().gCoin.image.setBounds(getSprites().rCoins);
                getSprites().gCoin.image.draw(canvas);
                getSprites().gPoints.image.setBounds(getSprites().rPoints);
                getSprites().gPoints.image.draw(canvas);
                getSprites().gCoin.image.setBounds(getSprites().rCoins2);
                getSprites().gCoin.image.draw(canvas);
                getSprites().gPoints.image.setBounds(getSprites().rPoints2);
                getSprites().gPoints.image.draw(canvas);

                if (canBuyLevel()) {
                    baseDraw(canvas, getSprites().rBtnBuy, getSprites().gButtonBlue);
                    drawText(canvas, getSprites().rBtnBuy, getString(R.string.btn_buy), GameValues.cFilterGreen);
                } else {
                    baseDraw(canvas, getSprites().rBtnBuy, getSprites().gButtonRed);
                    drawText(canvas, getSprites().rBtnBuy, getString(R.string.btn_buy), GameValues.cFilterRed);
                }
            } else {
                drawTextUnbounded(canvas, getSprites().rTextPurchased, getString(R.string.text_purchased_level));
            }
        }
    }

    @Override
    public void restoreGameState() {

    }

    @Override
    public void saveGameState() {
    }

    @Override
    public void doInitThread(long time) {
        super.sprites = new OptionViewSprites();

        cDifficulty = SystemHelper.getConfiguration(EngineConstants.CONFIG_DIFFICULTY, EngineConstants.DEFAULT_CONFIG_DIFFICULTY);

        rnd = new Random();

        getSprites().gBackground = loadGraphic(R.drawable.background);

        // button backgrounds
        getSprites().gButtonBlue = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_blue, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButtonRed = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_red, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButton = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_flat, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButtonOverlay = loadGraphic(R.drawable.button_flat_white);

        getSprites().gTextLogo = loadGraphic(R.drawable.text_logo);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.4f, 50, 50, getSprites().gTextLogo.image.getBounds());

        // navigation and text buttons
        getSprites().rBtnBack = getSprites().gButtonRed.image.copyBounds();
        getSprites().rBtnBuy = getSprites().gButtonRed.image.copyBounds();
        getSprites().rBtnLevels = getSprites().gButtonBlue.image.copyBounds();
        getSprites().rBtnSettings = getSprites().gButtonRed.image.copyBounds();
        getSprites().rBtnUpgrades = getSprites().gButtonBlue.image.copyBounds();
        getSprites().rMsgWait = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.35f, 50, 100, getSprites().rBtnSettings);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.35f, 50, 300, getSprites().rBtnLevels);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.35f, 50, 300, getSprites().rBtnUpgrades);

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.20f, 50, 25, getSprites().rBtnBack);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.20f, 50, 25, getSprites().rBtnBuy);

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.80f, 0, 0, getSprites().rMsgWait);

        mLayer = new Paint();
        mLayer.setColor(Color.WHITE);
        mLayer.setAlpha(75);
        mLayer.setStyle(Style.FILL_AND_STROKE);
        mLayerBorder = new Paint();
        mLayerBorder.setColor(Color.BLACK);
        mLayerBorder.setAlpha(100);
        mLayerBorder.setStyle(Style.STROKE);


        // Slider
        getSprites().gSlideLeft = Renderkit.loadGraphic(mContext.getResources(), R.drawable.pressbuttons, 0, 0);
        getSprites().gSlideRight = Renderkit.loadGraphic(mContext.getResources(), R.drawable.pressbuttons2, 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER_LEFT, 0.065f, 5, 0, getSprites().gSlideLeft);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER_RIGHT, 0.065f, 5, 0, getSprites().gSlideRight);

        getSprites().gCoin = Renderkit.loadGraphic(mContext.getResources(), R.drawable.coin, 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.045f, 0, 130, getSprites().gCoin);

        getSprites().gPoints = Renderkit.loadGraphic(mContext.getResources(), R.drawable.points, 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.045f, 0, 30, getSprites().gPoints);


        getSprites().rMsgDifficulty = getSprites().gButton.image.copyBounds();
        getSprites().rButtonDifficultEasy = getSprites().gButton.image.copyBounds();
        getSprites().rButtonDifficultMedium = getSprites().gButton.image.copyBounds();
        getSprites().rButtonDifficultHard = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.20f, 200, 50, getSprites().rMsgDifficulty);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.25f, 130, 250, getSprites().rButtonDifficultEasy);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.25f, 130, 500, getSprites().rButtonDifficultMedium);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.25f, 130, 750, getSprites().rButtonDifficultHard);

        // Extensions
        getSprites().gExtensionLevel = new Graphic[3];
        getSprites().gExtensionLevel[0] = Renderkit.loadGraphic(mContext.getResources(), R.drawable.extension_level1, 0, 0);
        getSprites().gExtensionLevel[1] = Renderkit.loadGraphic(mContext.getResources(), R.drawable.extension_level2, 0, 0);
        getSprites().gExtensionLevel[2] = Renderkit.loadGraphic(mContext.getResources(), R.drawable.extension_level3, 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.075f, 225, 90, getSprites().gExtensionLevel);

        getSprites().rUpgradeText = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.20f, 50, 100, getSprites().rUpgradeText);
        getSprites().rCurrentUpgrade = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.25f, 50, 225, getSprites().rCurrentUpgrade);

        getSprites().rTextPurchased = getSprites().gButton.image.copyBounds();
        getSprites().rTextCost = getSprites().gButton.image.copyBounds();
        getSprites().rTextAvailable = getSprites().gButton.image.copyBounds();
        getSprites().rCoins = getSprites().gCoin.image.copyBounds();
        getSprites().rCoins2 = getSprites().gCoin.image.copyBounds();
        getSprites().rCoinsCost = getSprites().gButton.image.copyBounds();
        getSprites().rCoinsAvailable = getSprites().gButton.image.copyBounds();
        getSprites().rPoints = getSprites().gPoints.image.copyBounds();
        getSprites().rPoints2 = getSprites().gPoints.image.copyBounds();
        getSprites().rPointsCost = getSprites().gButton.image.copyBounds();
        getSprites().rPointsAvailable = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.15f, 100, 150, getSprites().rTextPurchased);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.15f, 100, 450, getSprites().rTextCost);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.15f, 300, 450, getSprites().rTextAvailable);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.05f, 75, 350, getSprites().rCoins);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.05f, 275, 350, getSprites().rCoins2);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.15f, 100, 350, getSprites().rCoinsCost);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.15f, 300, 350, getSprites().rCoinsAvailable);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.05f, 75, 275, getSprites().rPoints);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.05f, 275, 275, getSprites().rPoints2);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.15f, 100, 275, getSprites().rPointsCost);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.15f, 300, 275, getSprites().rPointsAvailable);

        getSprites().gCurrentLevel = Renderkit.loadGraphic(mContext.getResources(), GameValues.levelId[0], 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.3f, 125, 50, getSprites().gCurrentLevel);
    }

    public synchronized void updateRenderMode(OptionRenderMode renderMode) {
        mRenderMode = renderMode;
    }

    @Override
    public void reset() {
    }

    @Override
    public float getCharSpacing() {
        return Constants.CHAR_SPACING;
    }

}
