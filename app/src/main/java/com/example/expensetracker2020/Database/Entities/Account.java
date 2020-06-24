package com.example.expensetracker2020.Database.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "account")
public class Account {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double balance;

    private int initialized;

    public Account(double balance, int initialized) {
        this.balance = balance;
        this.initialized = initialized;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getInitialized() {
        return initialized;
    }

    public void setInitialized(int initialized) {
        this.initialized = initialized;
    }
}
