package com.example.expensetracker2020.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "transaction")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;

    public double amount;

    public Date date;

    public String type;

    @ColumnInfo( name = "tag_id")
    public int tagId;

    public Transaction(String title, double amount, Date date, String type, int tagId) {
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.tagId = tagId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public int getTagId() {
        return tagId;
    }

    public void setId(int id) {
        this.id = id;
    }
}
