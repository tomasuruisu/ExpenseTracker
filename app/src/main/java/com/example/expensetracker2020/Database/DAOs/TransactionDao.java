package com.example.expensetracker2020.Database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Relation;

import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.Entities.Transaction;
import com.example.expensetracker2020.Database.Entities.TransactionAndTag;

import java.util.List;

@Dao
public interface TransactionDao {
    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction`")
    LiveData<List<TransactionAndTag>> getAll();

    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction` WHERE id IN (:ids)")
    LiveData<List<TransactionAndTag>> loadAllByIds(int[] ids);

    @androidx.room.Transaction
    @Insert
    void insert(Transaction transaction);

    @Insert
    void insertAll(Transaction... transactions);

    @Delete
    void delete(Transaction transaction);
}
