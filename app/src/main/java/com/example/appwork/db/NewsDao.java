package com.example.appwork.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.appwork.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class NewsDao {
    private static NewsDao instance;
    private final DatabaseHelper dbHelper;

    private NewsDao(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static synchronized NewsDao getInstance(Context context) {
        if (instance == null) {
            instance = new NewsDao(context);
        }
        return instance;
    }

    // 将NewsItem转换为ContentValues
    private ContentValues newsItemToContentValues(NewsItem newsItem) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_UNIQUE_KEY, newsItem.getUniquekey());
        values.put(DatabaseHelper.COLUMN_TITLE, newsItem.getTitle());
        values.put(DatabaseHelper.COLUMN_DATE, newsItem.getDate());
        values.put(DatabaseHelper.COLUMN_CATEGORY, newsItem.getCategory());
        values.put(DatabaseHelper.COLUMN_AUTHOR_NAME, newsItem.getAuthor_name());
        values.put(DatabaseHelper.COLUMN_URL, newsItem.getUrl());
        values.put(DatabaseHelper.COLUMN_THUMBNAIL_PIC_S, newsItem.getThumbnail_pic_s());
        return values;
    }

    // 将Cursor转换为NewsItem
    private NewsItem cursorToNewsItem(Cursor cursor) {
        NewsItem newsItem = new NewsItem();
        newsItem.setUniquekey(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UNIQUE_KEY)));
        newsItem.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)));
        newsItem.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)));
        newsItem.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY)));
        newsItem.setAuthor_name(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AUTHOR_NAME)));
        newsItem.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_URL)));
        newsItem.setThumbnail_pic_s(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_THUMBNAIL_PIC_S)));
        return newsItem;
    }

    // --- 收藏夹操作 ---

    public void addFavorite(NewsItem newsItem) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = newsItemToContentValues(newsItem);
        db.insertWithOnConflict(DatabaseHelper.TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void removeFavorite(String uniqueKey) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COLUMN_UNIQUE_KEY + " = ?", new String[]{uniqueKey});
        db.close();
    }

    public boolean isFavorite(String uniqueKey) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_FAVORITES, null,
                DatabaseHelper.COLUMN_UNIQUE_KEY + " = ?", new String[]{uniqueKey},
                null, null, null);
        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isFavorite;
    }

    public List<NewsItem> getFavorites() {
        List<NewsItem> favoritesList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_FAVORITES, null, null, null, null, null, DatabaseHelper.COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                favoritesList.add(cursorToNewsItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoritesList;
    }

    // --- 历史记录操作 ---

    public void addHistory(NewsItem newsItem) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = newsItemToContentValues(newsItem);
        db.insertWithOnConflict(DatabaseHelper.TABLE_HISTORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<NewsItem> getHistory() {
        List<NewsItem> historyList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_HISTORY, null, null, null, null, null, DatabaseHelper.COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                historyList.add(cursorToNewsItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return historyList;
    }
    
    public void clearHistory() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_HISTORY, null, null);
        db.close();
    }
}
