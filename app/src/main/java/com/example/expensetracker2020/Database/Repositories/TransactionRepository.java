package com.example.expensetracker2020.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.expensetracker2020.Database.AppDatabase;
import com.example.expensetracker2020.Database.DAOs.AccountDao;
import com.example.expensetracker2020.Database.DAOs.TagDao;
import com.example.expensetracker2020.Database.DAOs.TransactionDao;
import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.Entities.Tag;
import com.example.expensetracker2020.Database.Entities.Transaction;
import com.example.expensetracker2020.Database.Entities.TransactionAndTag;

import java.util.List;

public class TransactionRepository {

    private TransactionDao transactionDao;
    private LiveData<List<TransactionAndTag>> transactions;

    public TransactionRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        transactionDao = database.transactionDao();
        transactions = transactionDao.getAll();
    }

    public LiveData<List<TransactionAndTag>> getAll() {
        return transactions;
    }

    public void insert(Transaction transaction) {
        new TransactionRepository.InsertTransactionAsyncTask(transactionDao).execute(transaction);
    }

    private static class InsertTransactionAsyncTask extends AsyncTask<Transaction, Void, Void> {
        private TransactionDao transactionDao;

        private InsertTransactionAsyncTask(TransactionDao transactionDao) {
            this.transactionDao = transactionDao;
        }
        @Override
        protected Void doInBackground(Transaction... transactions) {
            transactionDao.insert(transactions[0]);
            return null;
        }
    }

}
