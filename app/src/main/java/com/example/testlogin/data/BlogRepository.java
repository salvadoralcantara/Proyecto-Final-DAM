package com.example.testlogin.data;

import android.content.Context;

import com.example.testlogin.model.Blog;

import java.util.List;

public class BlogRepository {
    private final BlogDao dao;

    public BlogRepository(Context ctx) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        dao = db.blogDao();
    }

    public long insert(Blog b) {
        return dao.insert(b);
    }

    public List<Blog> getAll() {
        return dao.getAll();
    }

    public void update(Blog b) {
        dao.update(b);
    }

    public void delete(long id) {
        dao.deleteById(id);
    }
}
