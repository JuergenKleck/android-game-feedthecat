package com.juergenkleck.android.game.feedthecat.storage.dto;

import java.io.Serializable;

import com.juergenkleck.android.appengine.storage.dto.BasicTable;

/**
 * Android app - FeedTheCat
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class CurrentGame extends BasicTable implements Serializable {

    /**
     * serial id
     */
    private static final long serialVersionUID = -7101789009229738387L;

    // the round number
    public int round;
    // the level to play
    public int level;
    // the life
    public float life;
    // the points collected
    public long points;
    // the collected cat coins
    public long coins;
    public long totalCoins;
    // is in jump
    public boolean jumped;
    // time elapsed
    public long time;
    // the rage amount
    public float rage;

}
