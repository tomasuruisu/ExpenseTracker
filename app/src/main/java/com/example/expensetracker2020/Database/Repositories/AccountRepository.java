package com.example.expensetracker2020.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.expensetracker2020.Database.AppDatabase;
import com.example.expensetracker2020.Database.DAOs.AccountDao;
import com.example.expensetracker2020.Database.Entities.Account;

import java.util.List;

public class AccountRepository {

    private AccountDao accountDao;
    private LiveData<List<Account>> accounts;
    private LiveData<Account> account;

    public AccountRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        accountDao = database.accountDao();
        accounts = accountDao.getAll();
        account = accountDao.getAccount();
    }

    public LiveData<List<Account>> getAll() {
        return accounts;
    }

    public LiveData<Account> getAccount() {
        return account;
    }

    public void insert(Account account) {
        new InsertAccountAsyncTask(accountDao).execute(account);
    }

    public void update(Account account) {
        new UpdateAccountAsyncTask(accountDao).execute(account);
    }

    private static class InsertAccountAsyncTask extends AsyncTask<Account, Void, Void> {
        private AccountDao accountDao;

        private InsertAccountAsyncTask(AccountDao accountDao) {
            this.accountDao = accountDao;
        }
        @Override
        protected Void doInBackground(Account... accounts) {
            accountDao.insert(accounts[0]);
            return null;
        }
    }

    private static class UpdateAccountAsyncTask extends AsyncTask<Account, Void, Void> {
        private AccountDao accountDao;

        private UpdateAccountAsyncTask(AccountDao accountDao) {
            this.accountDao = accountDao;
        }
        @Override
        protected Void doInBackground(Account... accounts) {
            accountDao.update(accounts[0]);
            return null;
        }
    }

}
