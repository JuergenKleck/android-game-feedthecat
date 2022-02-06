package com.juergenkleck.android.game.feedthecat.storage;

import android.provider.BaseColumns;

/**
 * Android app - FeedTheCat
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class StorageContract extends
        com.juergenkleck.android.appengine.storage.StorageContract {

    public static abstract class TableInventory implements BaseColumns {
        public static final String TABLE_NAME = "inventory";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_COINS = "coins";
    }

    public static abstract class TableCurrentGame implements BaseColumns {
        public static final String TABLE_NAME = "currentgame";
        public static final String COLUMN_ROUND = "round";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_LIFE = "life";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_COINS = "coins";
        public static final String COLUMN_TOTAL_COINS = "totalcoins";
        public static final String COLUMN_JUMPED = "jumped";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_RAGE = "rage";
    }
}
