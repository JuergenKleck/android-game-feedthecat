package com.juergenkleck.android.game.feedthecat.rendering;

import android.content.Context;

import java.util.Properties;

import com.juergenkleck.android.gameengine.rendering.GenericRendererTemplate;

/**
 * Android app - FeedTheCat
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public abstract class FeedTheCatRendererTemplate extends GenericRendererTemplate {

    public FeedTheCatRendererTemplate(Context context, Properties properties) {
        super(context, properties);
    }

    public boolean logEnabled() {
        return false;
    }

}
