package com.example.expensetracker2020.Database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expensetracker2020.Database.Entities.Transaction;
import com.example.expensetracker2020.Database.Entities.TransactionAndTag;

import java.util.Date;
import java.util.List;

@Dao
public interface TransactionDao {
    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction`")
    LiveData<List<TransactionAndTag>> getAll();

    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction` WHERE DATE(date) BETWEEN DATE((:start)) AND DATE((:end))")
    LiveData<List<TransactionAndTag>> getTransactionsFromCurrentMonth(Date start, Date end);

    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction` WHERE is_accounted_for != 1")
    LiveData<List<TransactionAndTag>> getUnaccountedForTransactions();

    @androidx.room.Transaction
    @Insert
    void insert(Transaction transaction);

    @androidx.room.Transaction
    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);
}
