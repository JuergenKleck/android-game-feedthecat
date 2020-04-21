package info.simplyapps.game.feedthecat.rendering;

import android.content.Context;

import java.util.Properties;

import info.simplyapps.gameengine.rendering.GenericRendererTemplate;

public abstract class FeedTheCatRendererTemplate extends GenericRendererTemplate {

    public FeedTheCatRendererTemplate(Context context, Properties properties) {
        super(context, properties);
    }

    public boolean logEnabled() {
        return false;
    }

}
