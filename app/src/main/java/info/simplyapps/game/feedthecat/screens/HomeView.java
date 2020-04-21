package info.simplyapps.game.feedthecat.screens;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Properties;

import info.simplyapps.game.feedthecat.Constants;
import info.simplyapps.game.feedthecat.rendering.HomeRenderer;
import info.simplyapps.gameengine.EngineConstants;
import info.simplyapps.gameengine.RenderingSystem;
import info.simplyapps.gameengine.rendering.GenericViewTemplate;

public class HomeView extends GenericViewTemplate {

    public HomeView(Context context) {
        super(context);
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void createThread(Context context) {
        Properties p = new Properties();
        p.put(EngineConstants.GameProperties.RENDERING_SYSTEM, RenderingSystem.SINGLE_PLAYER);
        p.put(EngineConstants.GameProperties.SCREEN_SCALE, getScreenScaleValue());
        p.put(EngineConstants.GameProperties.LEVEL, 0);

        setBasicEngine(new HomeRenderer(getContext(), p));
    }

    @Override
    public boolean isDragable() {
        return true;
    }

    @Override
    public String getNameSpace() {
        return Constants.PREFERENCE_NS;
    }

}
