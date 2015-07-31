package com.kidgeniushq;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by benjamin.harvey on 7/31/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ARTICLES = "articles";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CAPTION = "caption";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_INFO = "info";

    private static final String DATABASE_NAME = "articles.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ARTICLES + "(" + COLUMN_ID
            + " integer primary key autoincrement, "  + COLUMN_INFO
            + " text not null," + COLUMN_LINK
            + " text not null,"+ COLUMN_CAPTION
            + " text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
        onCreate(db);
    }

}