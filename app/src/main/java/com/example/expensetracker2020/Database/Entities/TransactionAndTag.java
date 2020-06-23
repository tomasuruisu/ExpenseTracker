package com.example.expensetracker2020.Database.Entities;

import androidx.room.Embedded;
import androidx.room.Relation;

public class TransactionAndTag {
    @Embedded
    public Transaction transaction;
    @Relation(
            parentColumn = "tag_id",
            entityColumn = "id"
    )
    public Tag tag;

    public TransactionAndTag(Transaction transaction, Tag tag) {
        this.transaction = transaction;
        this.tag = tag;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
