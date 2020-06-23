package com.example.expensetracker2020.Database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expensetracker2020.Database.Entities.Account;

import java.util.List;

@Dao
public interface AccountDao {
    @Query("SELECT * FROM account")
    LiveData<List<Account>> getAll();

    @Query("SELECT * FROM account LIMIT 1")
    LiveData<Account> getAccount();

    @Query("SELECT * FROM account WHERE id IN (:ids)")
    LiveData<List<Account>> loadAllByIds(int[] ids);

    @Insert
    void insert(Account account);

    @Update
    void update(Account account);

    @Insert
    void insertAll(Account... accounts);

    @Delete
    void delete(Account account);
}
