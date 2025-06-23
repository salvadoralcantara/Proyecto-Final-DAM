package com.example.testlogin.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.testlogin.model.Blog;

import java.util.List;

@Dao
public interface BlogDao {
    @Insert
    long insert(Blog blog);

    @Update
    void update(Blog blog);

    @Query("DELETE FROM blogs WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT * FROM blogs ORDER BY id DESC")
    List<Blog> getAll();
}
