package com.juergenkleck.android.game.feedthecat.storage.dto;

import java.io.Serializable;

import com.juergenkleck.android.appengine.storage.dto.BasicTable;

/**
 * Android app - FeedTheCat
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class Inventory extends BasicTable implements Serializable {

    /**
     * serial id
     */
    private static final long serialVersionUID = -7101789009229738387L;

    public long points;
    public long coins;

    public Inventory() {
        coins = 0L;
        points = 0L;
    }

}
