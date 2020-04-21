package info.simplyapps.game.feedthecat.sprites;

import android.graphics.Rect;

import info.simplyapps.gameengine.rendering.objects.Graphic;
import info.simplyapps.gameengine.sprites.ViewSprites;

public class HomeViewSprites implements ViewSprites {

    // main
    public Graphic gBackground;
    public Graphic gLogo;

    // generic buttons
    public Graphic gButton;
    public Graphic gButtonBlue;
    public Graphic gButtonRed;
    public Graphic gButtonOverlay;

    public Graphic gTextLogo;

    // main menu system
    public Rect rBtnStart;
    public Rect rBtnOptions;
    public Rect rBtnQuit;
    public Rect rBtnBack;
    public Rect rMsgWait;

    @Override
    public void init() {
    }

    @Override
    public void clean() {
    }

}
