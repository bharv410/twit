package com.kidgeniushq;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kidgeniushq.models.HNHHArticle;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin.harvey on 7/31/15.
 */
public class ArticlesDatasource {// Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_INFO, MySQLiteHelper.COLUMN_LINK, MySQLiteHelper.COLUMN_CAPTION };

    public ArticlesDatasource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public HNHHArticle createArticle(String title, String url, String description) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_INFO, description);
        values.put(MySQLiteHelper.COLUMN_LINK, url);
        values.put(MySQLiteHelper.COLUMN_CAPTION, title);
        long insertId = database.insert(MySQLiteHelper.TABLE_ARTICLES, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ARTICLES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        HNHHArticle newComment = cursorToComment(cursor);
        cursor.close();
        return new HNHHArticle(title, url, description, insertId);
    }

    public void deleteArticle(HNHHArticle article) {
        long id = article.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_ARTICLES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<HNHHArticle> getAllArticles() {
        List<HNHHArticle> comments = new ArrayList<HNHHArticle>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ARTICLES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HNHHArticle comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private HNHHArticle cursorToComment(Cursor cursor) {
        HNHHArticle blankArticle = new HNHHArticle("");
        blankArticle.setId(cursor.getLong(0));
        blankArticle.setInfo(cursor.getString(1));
        blankArticle.setLink(cursor.getString(2));
        blankArticle.setCaption(cursor.getString(3));
        return blankArticle;
    }
}