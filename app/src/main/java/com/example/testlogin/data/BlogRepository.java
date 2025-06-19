package com.example.testlogin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.testlogin.model.Blog;

import java.util.ArrayList;
import java.util.List;

public class BlogRepository {

    private final BlogDbHelper helper;

    public BlogRepository(Context ctx) { helper = new BlogDbHelper(ctx); }

    public long insert(Blog b) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(BlogDbHelper.C_TITLE, b.title);
        cv.put(BlogDbHelper.C_STORY, b.story);
        return db.insert(BlogDbHelper.TABLE, null, cv);
    }

    public List<Blog> getAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(BlogDbHelper.TABLE, null, null, null,
                null, null, BlogDbHelper.C_ID + " DESC");
        List<Blog> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(new Blog(
                    c.getLong(c.getColumnIndexOrThrow(BlogDbHelper.C_ID)),
                    c.getString(c.getColumnIndexOrThrow(BlogDbHelper.C_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(BlogDbHelper.C_STORY))
            ));
        }
        c.close();
        return list;
    }

    public void update(Blog b) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(BlogDbHelper.C_TITLE, b.title);
        cv.put(BlogDbHelper.C_STORY, b.story);
        db.update(BlogDbHelper.TABLE, cv, BlogDbHelper.C_ID + "=?",
                new String[]{String.valueOf(b.id)});
    }

    public void delete(long id) {
        helper.getWritableDatabase()
                .delete(BlogDbHelper.TABLE, BlogDbHelper.C_ID + "=?",
                        new String[]{String.valueOf(id)});
    }
}
