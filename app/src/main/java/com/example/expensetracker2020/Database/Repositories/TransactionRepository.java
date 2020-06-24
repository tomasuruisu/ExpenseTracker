package com.example.expensetracker2020.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.expensetracker2020.Database.AppDatabase;
import com.example.expensetracker2020.Database.DAOs.AccountDao;
import com.example.expensetracker2020.Database.DAOs.TagDao;
import com.example.expensetracker2020.Database.DAOs.TransactionDao;
import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.Entities.Tag;
import com.example.expensetracker2020.Database.Entities.Transaction;
import com.example.expensetracker2020.Database.Entities.TransactionAndTag;

import java.util.Date;
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

    public LiveData<List<TransactionAndTag>> getTransactionsFromCurrentMonth(Date start, Date end) {
        return transactionDao.getTransactionsFromCurrentMonth(start, end);
    }

    public LiveData<List<TransactionAndTag>> getUnaccountedForTransactions() {
        return transactionDao.getUnaccountedForTransactions();
    }

    public void insert(Transaction transaction) {
        new TransactionRepository.InsertTransactionAsyncTask(transactionDao).execute(transaction);
    }

    public void update(Transaction transaction) {
        new TransactionRepository.UpdateTransactionAsyncTask(transactionDao).execute(transaction);
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

    private static class UpdateTransactionAsyncTask extends AsyncTask<Transaction, Void, Void> {
        private TransactionDao transactionDao;

        private UpdateTransactionAsyncTask(TransactionDao transactionDao) {
            this.transactionDao = transactionDao;
        }
        @Override
        protected Void doInBackground(Transaction... transactions) {
            transactionDao.update(transactions[0]);
            return null;
        }
    }

}
