package com.example.testlogin.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlogDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "blogs.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE = "blogs";
    public static final String C_ID = "_id";
    public static final String C_TITLE = "title";
    public static final String C_STORY = "story";
    public static final String C_IMAGE_URI = "image_uri";

    private static final String CREATE =
            "CREATE TABLE " + TABLE + " (" +
                    C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    C_TITLE + " TEXT NOT NULL, " +
                    C_STORY + " TEXT NOT NULL, " +
                    C_IMAGE_URI + " TEXT);";

    public BlogDbHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
