package com.juergenkleck.android.game.feedthecat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.juergenkleck.android.appengine.storage.dto.BasicTable;
import com.juergenkleck.android.game.feedthecat.storage.dto.CurrentGame;
import com.juergenkleck.android.game.feedthecat.storage.dto.Inventory;

/**
 * Android app - FeedTheCat
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class DBDriver extends com.juergenkleck.android.appengine.storage.DBDriver {

    private static final String SQL_CREATE_INVENTORY =
            "CREATE TABLE " + StorageContract.TableInventory.TABLE_NAME + " (" +
                    StorageContract.TableInventory._ID + " INTEGER PRIMARY KEY," +
                    StorageContract.TableInventory.COLUMN_POINTS + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_COINS + TYPE_INT +
                    " );";
    private static final String SQL_CREATE_CURRENTGAME =
            "CREATE TABLE " + StorageContract.TableCurrentGame.TABLE_NAME + " (" +
                    StorageContract.TableCurrentGame._ID + " INTEGER PRIMARY KEY," +
                    StorageContract.TableCurrentGame.COLUMN_ROUND + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_LEVEL + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_LIFE + TYPE_TEXT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_POINTS + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_COINS + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_TOTAL_COINS + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_JUMPED + TYPE_TEXT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_TIME + TYPE_TEXT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_RAGE + TYPE_TEXT +
                    " );";

    private static final String SQL_DELETE_INVENTORY =
            "DROP TABLE IF EXISTS " + StorageContract.TableInventory.TABLE_NAME;
    private static final String SQL_DELETE_CURRENTROUND =
            "DROP TABLE IF EXISTS " + StorageContract.TableCurrentGame.TABLE_NAME;

    public DBDriver(String dataBaseName, int dataBaseVersion, Context context) {
        super(dataBaseName, dataBaseVersion, context);
    }

    public static DBDriver getInstance() {
        return (DBDriver) com.juergenkleck.android.appengine.storage.DBDriver.getInstance();
    }


    @Override
    public void createTables(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_INVENTORY);
        db.execSQL(SQL_CREATE_CURRENTGAME);
    }


    @Override
    public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(SQL_DELETE_INVENTORY);
//        db.execSQL(SQL_DELETE_CURRENTGAME);
//        db.execSQL(SQL_DELETE_LOCALDATA);
    }

    @Override
    public String getExtendedTable(BasicTable data) {
        return Inventory.class.isInstance(data) ? StorageContract.TableInventory.TABLE_NAME :
                CurrentGame.class.isInstance(data) ? StorageContract.TableCurrentGame.TABLE_NAME : null;
    }

    @Override
    public void storeExtended(com.juergenkleck.android.appengine.storage.StoreData data) {
        store(StoreData.class.cast(data).inventory);
        store(StoreData.class.cast(data).currentGame);
    }

    @Override
    public void readExtended(com.juergenkleck.android.appengine.storage.StoreData data, SQLiteDatabase db) {
        readInventory(StoreData.class.cast(data), db);
        readCurrentGame(StoreData.class.cast(data), db);
    }

    @Override
    public com.juergenkleck.android.appengine.storage.StoreData createStoreData() {
        return new StoreData();
    }


    public boolean store(Inventory data) {
        ContentValues values = new ContentValues();
        values.put(StorageContract.TableInventory.COLUMN_COINS, data.coins);
        values.put(StorageContract.TableInventory.COLUMN_POINTS, data.points);
        return persist(data, values, StorageContract.TableInventory.TABLE_NAME);
    }

    private void readInventory(StoreData data, SQLiteDatabase db) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                StorageContract.TableInventory._ID,
                StorageContract.TableInventory.COLUMN_COINS,
                StorageContract.TableInventory.COLUMN_POINTS
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;
        Cursor c = db.query(
                StorageContract.TableInventory.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        boolean hasResults = c.moveToFirst();
        while (hasResults) {
            Inventory i = new Inventory();
            i.id = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory._ID));
            i.coins = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_COINS));
            i.points = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_POINTS));
            data.inventory = i;
            hasResults = c.moveToNext();
        }
        c.close();
    }

    public boolean store(CurrentGame data) {
        ContentValues values = new ContentValues();
        values.put(StorageContract.TableCurrentGame.COLUMN_ROUND, data.round);
        values.put(StorageContract.TableCurrentGame.COLUMN_LEVEL, data.level);
        values.put(StorageContract.TableCurrentGame.COLUMN_POINTS, data.points);
        values.put(StorageContract.TableCurrentGame.COLUMN_COINS, data.coins);
        values.put(StorageContract.TableCurrentGame.COLUMN_TOTAL_COINS, data.totalCoins);
        values.put(StorageContract.TableCurrentGame.COLUMN_TIME, Long.toString(data.time));
        values.put(StorageContract.TableCurrentGame.COLUMN_JUMPED, Boolean.toString(data.jumped));
        values.put(StorageContract.TableCurrentGame.COLUMN_LIFE, Float.toString(data.life));
        values.put(StorageContract.TableCurrentGame.COLUMN_RAGE, Float.toString(data.rage));
        return persist(data, values, StorageContract.TableCurrentGame.TABLE_NAME);
    }

    private void readCurrentGame(StoreData data, SQLiteDatabase db) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                StorageContract.TableCurrentGame._ID,
                StorageContract.TableCurrentGame.COLUMN_COINS,
                StorageContract.TableCurrentGame.COLUMN_TIME,
                StorageContract.TableCurrentGame.COLUMN_JUMPED,
                StorageContract.TableCurrentGame.COLUMN_LIFE,
                StorageContract.TableCurrentGame.COLUMN_POINTS,
                StorageContract.TableCurrentGame.COLUMN_RAGE,
                StorageContract.TableCurrentGame.COLUMN_ROUND,
                StorageContract.TableCurrentGame.COLUMN_LEVEL,
                StorageContract.TableCurrentGame.COLUMN_TOTAL_COINS
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;
        Cursor c = db.query(
                StorageContract.TableCurrentGame.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        boolean hasResults = c.moveToFirst();
        while (hasResults) {
            CurrentGame i = new CurrentGame();
            i.id = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame._ID));
            i.round = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_ROUND));
            i.level = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_LEVEL));
            i.points = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_POINTS));
            i.coins = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_COINS));
            i.totalCoins = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_TOTAL_COINS));
            i.time = Long.valueOf(c.getString(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_TIME)));
            i.jumped = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_JUMPED)));
            i.life = Float.valueOf(c.getString(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_LIFE)));
            i.rage = Float.valueOf(c.getString(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_RAGE)));
            data.currentGame = i;
            hasResults = c.moveToNext();
        }
        c.close();
    }

}
