package com.example.expensetracker2020.Database.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "interval")
public class Interval {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private int days;

    public Interval(String name, int days) {
        this.name = name;
        this.days = days;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDays() {
        return days;
    }

    public void setId(int id) {
        this.id = id;
    }
}
