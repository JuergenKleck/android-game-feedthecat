package com.juergenkleck.android.game.feedthecat.system;

import com.juergenkleck.android.gameengine.system.Game;
import com.juergenkleck.android.gameengine.system.GameRound;

/**
 * Android app - FeedTheCat
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class FeedTheCatGame extends Game {

    public boolean complete;
    public long points;
    public long coins;
    public long totalCoins;
    public boolean jumped;
    public boolean credited;
    public int difficulty;

    public FeedTheCatGame(GameRound[] rounds) {
        super(rounds);
    }

    public void reset() {
        super.reset();
        life = 0.0f;
    }

}
