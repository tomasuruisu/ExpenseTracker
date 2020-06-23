package com.example.expensetracker2020.Database.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.Repositories.AccountRepository;

import java.util.List;

public class AccountViewModel extends AndroidViewModel {
    private AccountRepository accountRepository;
    private LiveData<List<Account>> accounts;
    private LiveData<Account> account;

    public AccountViewModel (@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
        accounts = accountRepository.getAll();
        account = accountRepository.getAccount();
    }

    public LiveData<List<Account>> getAccounts() {
        return accounts;
    };

    public LiveData<Account> getAccount() {
        return account;
    };

    public void insert(Account account) {
        accountRepository.insert(account);
    }
}
