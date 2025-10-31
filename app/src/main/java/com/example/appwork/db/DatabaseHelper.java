package com.example.appwork.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // 数据库信息
    private static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 1;

    // 表名
    public static final String TABLE_FAVORITES = "favorites";
    public static final String TABLE_HISTORY = "history";

    // 通用列名
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UNIQUE_KEY = "uniquekey";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_AUTHOR_NAME = "author_name";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_THUMBNAIL_PIC_S = "thumbnail_pic_s";
    public static final String COLUMN_TIMESTAMP = "timestamp"; // 用于排序的时间戳

    // 创建收藏表的SQL语句
    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE " + TABLE_FAVORITES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_UNIQUE_KEY + " TEXT UNIQUE," +
            COLUMN_TITLE + " TEXT," +
            COLUMN_DATE + " TEXT," +
            COLUMN_CATEGORY + " TEXT," +
            COLUMN_AUTHOR_NAME + " TEXT," +
            COLUMN_URL + " TEXT," +
            COLUMN_THUMBNAIL_PIC_S + " TEXT," +
            COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ");";

    // 创建历史记录表的SQL语句
    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE " + TABLE_HISTORY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_UNIQUE_KEY + " TEXT UNIQUE," +
            COLUMN_TITLE + " TEXT," +
            COLUMN_DATE + " TEXT," +
            COLUMN_CATEGORY + " TEXT," +
            COLUMN_AUTHOR_NAME + " TEXT," +
            COLUMN_URL + " TEXT," +
            COLUMN_THUMBNAIL_PIC_S + " TEXT," +
            COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORITES);
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 简单处理：删除旧表，创建新表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }
}
