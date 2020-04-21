package info.simplyapps.game.feedthecat.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

import java.util.Properties;

import info.simplyapps.game.feedthecat.Constants;
import info.simplyapps.game.feedthecat.Constants.RenderMode;
import info.simplyapps.game.feedthecat.SystemHelper;
import info.simplyapps.game.feedthecat.engine.FeedTheCatEngine;
import info.simplyapps.game.feedthecat.free.R;
import info.simplyapps.game.feedthecat.rendering.GameRenderer;
import info.simplyapps.game.feedthecat.rendering.HomeRenderer;
import info.simplyapps.game.feedthecat.rendering.OptionRenderer;
import info.simplyapps.game.feedthecat.storage.StorageUtil;
import info.simplyapps.gameengine.EngineConstants;
import info.simplyapps.gameengine.RenderingSystem;
import info.simplyapps.gameengine.screens.HomeScreenTemplate;

public class HomeScreen extends HomeScreenTemplate {

    public static String ICICLE_KEY = "feedthecat-view";

    public static boolean mGameModeContinue = false;

    RenderMode mLastRenderMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getViewKey() {
        return ICICLE_KEY;
    }

    @Override
    public int getScreenLayout() {
        return R.layout.homescreen;
    }

    @Override
    public int getViewLayoutId() {
        return R.id.homeview;
    }

    @Override
    public void prepareStorage(Context context) {
        StorageUtil.prepareStorage(getApplicationContext());
    }

    @Override
    public void actionNewGame() {
        mGameModeContinue = false;
        startGameScreen();
    }

    @Override
    public void actionContinueGame() {
    }

    @Override
    public void actionOptions() {
        HomeRenderer.class.cast(getScreenView().getBasicEngine()).updateRenderMode(RenderMode.OPTIONS);
        getScreenView().setBasicEngine(new OptionRenderer(this, getEngineProperties()));
    }

    @Override
    public void actionQuit() {
        finish();
    }

    @Override
    public void actionAdditionalAction(int action) {
        switch (action) {
            case Constants.ACTION_SCORE:
                actionScore();
                break;
            case Constants.ACTION_HOME:
                actionHome();
                break;
        }
    }

    public void actionHome() {
        if (!isHomeActive()) {
            getScreenView().changeEngine(new HomeRenderer(this, getEngineProperties()));
        } else {
            HomeRenderer.class.cast(getScreenView().getBasicEngine()).updateRenderMode(RenderMode.HOME);
        }
    }

    @Override
    public void doUpdateChecks() {
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (isGameActive()) {
            getScreenView().getBasicEngine().pause();
            mLastRenderMode = RenderMode.GAME;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isGameActive()) {
            getScreenView().getBasicEngine().unpause();
        } else if (!isGameActive() && RenderMode.GAME.equals(mLastRenderMode)) {
            loadGameEngine();
        } else if (mLastRenderMode != null) {
            ((HomeRenderer) getScreenView().getBasicEngine()).updateRenderMode(mLastRenderMode);
        }
        mLastRenderMode = null;
    }

    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state. (Done via HOME button or another activity popping up
     *
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getScreenView().getBasicEngine().saveState(outState);
    }

    public void actionScore() {
        ((HomeRenderer) getScreenView().getBasicEngine()).updateRenderMode(RenderMode.SCORE);
    }

    private void startGameScreen() {
        activateGame();
    }

    public void activateGame() {
        loadGameEngine();
        // start the game
        getScreenView().getBasicEngine().doStart();
    }

    private void loadGameEngine() {
        getScreenView().changeEngine(new GameRenderer(this, getEngineProperties()));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean isHomeActive() {
        return getScreenView() != null && HomeRenderer.class.isInstance(getScreenView().getBasicEngine());
    }

    private boolean isGameActive() {
        return getScreenView() != null && GameRenderer.class.isInstance(getScreenView().getBasicEngine());
    }

    private boolean isOptionsActive() {
        return getScreenView() != null && OptionRenderer.class.isInstance(getScreenView().getBasicEngine());
    }

    private boolean isScore() {
        return HomeRenderer.class.isInstance(getScreenView().getBasicEngine()) && Constants.RenderMode.SCORE == HomeRenderer.class.cast(getScreenView().getBasicEngine()).mRenderMode;
    }

    private boolean isHome() {
        return HomeRenderer.class.isInstance(getScreenView().getBasicEngine()) && Constants.RenderMode.HOME == HomeRenderer.class.cast(getScreenView().getBasicEngine()).mRenderMode;
    }

    private boolean isOptions() {
        return HomeRenderer.class.isInstance(getScreenView().getBasicEngine()) && Constants.RenderMode.OPTIONS == HomeRenderer.class.cast(getScreenView().getBasicEngine()).mRenderMode;
    }

    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (isHomeActive()) {
            if (isHome()) {
                return super.onKeyDown(keyCode, e);
            } else if (isScore() || isOptions()) {
                actionHome();
            }
        } else if (isOptionsActive()) {
            actionHome();
        } else if (isGameActive()) {
            getEngine().pause();
        }
        return false;
    }

    public FeedTheCatEngine getEngine() {
        if (FeedTheCatEngine.class.isInstance(getScreenView().getBasicEngine())) {
            return FeedTheCatEngine.class.cast(getScreenView().getBasicEngine());
        }
        return null;
    }

    public HomeView getHomeView() {
        return (HomeView) getScreenView();
    }

    @Override
    public Properties getEngineProperties() {
        Properties p = new Properties();
        p.put(EngineConstants.GameProperties.RENDERING_SYSTEM, RenderingSystem.SINGLE_PLAYER);
        p.put(EngineConstants.GameProperties.SCREEN_SCALE, getScreenView().getScreenScaleValue());
        p.put(EngineConstants.GameProperties.LEVEL, 0);
        p.put(EngineConstants.GameProperties.SPACE_LR, Constants.spaceLR);
        p.put(EngineConstants.GameProperties.SPACE_TB, Constants.spaceTB);
        return p;
    }

}