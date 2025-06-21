package com.example.testlogin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class BlogRepository {

    private final BlogDbHelper helper;

    public BlogRepository(Context ctx) {
        helper = new BlogDbHelper(ctx);
    }

    public long insert(Blog b) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(BlogDbHelper.C_TITLE, b.titulo);
        cv.put(BlogDbHelper.C_STORY, b.historia);
        cv.put(BlogDbHelper.C_IMAGE_URI, b.imagenUri);
        cv.put(BlogDbHelper.C_AUTOR, b.autor);
        return db.insert(BlogDbHelper.TABLE, null, cv);
    }

    public List<Blog> getAll(String autor) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Blog> list = new ArrayList<>();

        Cursor c = db.query(
                BlogDbHelper.TABLE,
                null,
                BlogDbHelper.C_AUTOR + " = ?",
                new String[]{autor},
                null,
                null,
                BlogDbHelper.C_ID + " DESC"
        );

        while (c.moveToNext()) {
            list.add(new Blog(
                    c.getLong(c.getColumnIndexOrThrow(BlogDbHelper.C_ID)),
                    c.getString(c.getColumnIndexOrThrow(BlogDbHelper.C_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(BlogDbHelper.C_STORY)),
                    c.getString(c.getColumnIndexOrThrow(BlogDbHelper.C_IMAGE_URI)),
                    c.getString(c.getColumnIndexOrThrow(BlogDbHelper.C_AUTOR))
            ));
        }
        c.close();
        return list;
    }

    public void update(Blog b) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(BlogDbHelper.C_TITLE, b.titulo);
        cv.put(BlogDbHelper.C_STORY, b.historia);
        cv.put(BlogDbHelper.C_IMAGE_URI, b.imagenUri);
        db.update(BlogDbHelper.TABLE, cv, BlogDbHelper.C_ID + "=? AND " + BlogDbHelper.C_AUTOR + "=?", new String[]{String.valueOf(b.id), b.autor});
    }

    public void delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(BlogDbHelper.TABLE, BlogDbHelper.C_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Clase interna Blog
    public static class Blog {
        public long id;
        public String titulo;
        public String historia;
        public String imagenUri;
        public String autor;

        public Blog(long id, String titulo, String historia, String imagenUri, String autor) {
            this.id = id;
            this.titulo = titulo;
            this.historia = historia;
            this.imagenUri = imagenUri;
            this.autor = autor;
        }

        public Blog(String titulo, String historia, String imagenUri, String autor) {
            this(-1, titulo, historia, imagenUri, autor);
        }
    }
}
