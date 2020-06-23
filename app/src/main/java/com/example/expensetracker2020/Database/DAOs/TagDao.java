package com.example.expensetracker2020.Database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expensetracker2020.Database.Entities.Tag;

import java.util.List;

@Dao
public interface TagDao {
    @Query("SELECT * FROM tag")
    LiveData<List<Tag>> getAll();

    @Query("SELECT * FROM tag WHERE id IN (:ids)")
    LiveData<List<Tag>> loadAllByIds(int[] ids);

    @Insert
    void insert(Tag tag);

    @Insert
    void insertAll(Tag... tags);

    @Update
    void update(Tag tag);

    @Delete
    void delete(Tag tag);
}
