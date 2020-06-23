package com.example.expensetracker2020.Database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.Entities.Interval;

import java.util.List;

@Dao
public interface IntervalDao {
    @Query("SELECT * FROM interval")
    LiveData<List<Interval>> getAll();

    @Query("SELECT * FROM interval WHERE id IN (:ids)")
    LiveData<List<Interval>> loadAllByIds(int[] ids);

    @Insert
    void insert(Interval interval);

    @Insert
    void insertAll(Interval... intervals);

    @Delete
    void delete(Interval interval);
}
