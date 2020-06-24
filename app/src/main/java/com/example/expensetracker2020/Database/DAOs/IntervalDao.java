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

    @Insert
    void insert(Interval interval);

    @Delete
    void delete(Interval interval);
}
