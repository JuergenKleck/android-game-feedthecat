package info.simplyapps.game.feedthecat.rendering;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.Properties;
import java.util.Random;

import info.simplyapps.appengine.storage.dto.Configuration;
import info.simplyapps.game.feedthecat.Constants;
import info.simplyapps.game.feedthecat.Constants.RenderMode;
import info.simplyapps.game.feedthecat.engine.GameValues;
import info.simplyapps.game.feedthecat.free.R;
import info.simplyapps.game.feedthecat.sprites.HomeViewSprites;
import info.simplyapps.game.feedthecat.storage.dto.Inventory;
import info.simplyapps.gameengine.EngineConstants;
import info.simplyapps.gameengine.rendering.kits.Renderkit;
import info.simplyapps.gameengine.rendering.kits.ScreenKit;
import info.simplyapps.gameengine.rendering.kits.ScreenKit.ScreenPosition;

public class HomeRenderer extends FeedTheCatRendererTemplate {

    Random rnd;
    public RenderMode mRenderMode;
    boolean hasAds = false;
    Configuration cDifficulty = null;
    Inventory inventory = null;

    public HomeRenderer(Context context, Properties p) {
        super(context, p);
        mRenderMode = RenderMode.HOME;
    }

    public boolean canAds() {
        return false;
    }

    public HomeViewSprites getSprites() {
        return HomeViewSprites.class.cast(super.sprites);
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
                    break;
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    if (Constants.RenderMode.HOME == mRenderMode) {
                        if (containsClick(getSprites().rBtnStart, event.getX(), event.getY())) {
                            delayedActionHandler(EngineConstants.ACTION_START, EngineConstants.ACTION_START);
                        }
                        if (containsClick(getSprites().rBtnQuit, event.getX(), event.getY())) {
                            delayedActionHandler(EngineConstants.ACTION_QUIT, EngineConstants.ACTION_QUIT);
                        }
                        if (containsClick(getSprites().rBtnOptions, event.getX(), event.getY())) {
                            delayedActionHandler(EngineConstants.ACTION_OPTIONS, EngineConstants.ACTION_OPTIONS);
                        }
                    }

                    break;
            }

        }

        return true;
    }

    @Override
    public void doUpdateRenderState() {

        if (Constants.RenderMode.OPTIONS == mRenderMode) {
            if (canAds() && hasAds) {
                hasAds = false;
            }
        } else if (Constants.RenderMode.GAME == mRenderMode) {
            mRenderMode = Constants.RenderMode.HOME;
        } else {
            if (canAds() && !hasAds) {
                hasAds = true;
            }
        }
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

        if (Constants.RenderMode.HOME == mRenderMode) {
            getSprites().gTextLogo.image.draw(canvas);

            // draw buttons last to overlay the background items
            choiceBaseDraw(canvas, getSprites().rBtnStart, getSprites().gButtonBlue, getSprites().gButtonBlue, activeButton, EngineConstants.ACTION_START, GameValues.cFilterBlue);
            choiceBaseDraw(canvas, getSprites().rBtnOptions, getSprites().gButtonBlue, getSprites().gButtonBlue, activeButton, EngineConstants.ACTION_OPTIONS, GameValues.cFilterBlue);
            choiceBaseDraw(canvas, getSprites().rBtnQuit, getSprites().gButtonRed, getSprites().gButtonRed, activeButton, EngineConstants.ACTION_QUIT, GameValues.cFilterRed);

            drawText(canvas, getSprites().rBtnStart, getString(R.string.menubutton_start), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
            drawText(canvas, getSprites().rBtnQuit, getString(R.string.menubutton_quit), ScreenKit.scaleWidth(Constants.spaceMainBtnLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceMainBtnTB, screenHeight), GameValues.cFilterRed);
            drawText(canvas, getSprites().rBtnOptions, getString(R.string.menubutton_options), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));

        } else if (Constants.RenderMode.GAME == mRenderMode) {
            drawText(canvas, getSprites().rMsgWait, getString(R.string.message_loading));
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
        super.sprites = new HomeViewSprites();


        rnd = new Random();

        getSprites().gBackground = loadGraphic(R.drawable.background);
//				GameValues.backgrounds[rnd.nextInt(GameValues.backgrounds.length)], 0, 0);

//        getSprites().gLogo = loadGraphic(R.drawable.ic_logo, 0, 0);
//        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.10f, 10, 10, getSprites().gLogo);

        // button backgrounds
        getSprites().gButtonBlue = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_blue, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButtonRed = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_red, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButton = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_flat, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButtonOverlay = loadGraphic(R.drawable.button_flat_white);

        getSprites().gTextLogo = loadGraphic(R.drawable.text_logo);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.4f, 50, 50, getSprites().gTextLogo.image.getBounds());

        // navigation and text buttons
        getSprites().rBtnBack = getSprites().gButtonRed.image.copyBounds();
        getSprites().rBtnStart = getSprites().gButtonBlue.image.copyBounds();
        getSprites().rBtnQuit = getSprites().gButtonRed.image.copyBounds();
        getSprites().rBtnOptions = getSprites().gButtonBlue.image.copyBounds();
        getSprites().rMsgWait = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.35f, 50, 100, getSprites().rBtnStart);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.35f, 50, 100, getSprites().rBtnOptions);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.25f, 50, 100, getSprites().rBtnQuit);

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.20f, 50, 25, getSprites().rBtnBack);

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.80f, 0, 0, getSprites().rMsgWait);
    }

    public synchronized void updateRenderMode(RenderMode renderMode) {
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
