package com.feng.download.downloaddemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liu on 16/5/25.
 */
public class DBHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "download.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE download (_id INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER, url VARCHAR, size INTEGER, completesize INTEGER, state INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS download");
        onCreate(db);
    }
}
