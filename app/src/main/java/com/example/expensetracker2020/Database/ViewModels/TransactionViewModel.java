package com.example.expensetracker2020.Database.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensetracker2020.Database.Entities.Tag;
import com.example.expensetracker2020.Database.Entities.Transaction;
import com.example.expensetracker2020.Database.Entities.TransactionAndTag;
import com.example.expensetracker2020.Database.Repositories.TransactionRepository;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {

    private TransactionRepository transactionRepository;

    private LiveData<List<TransactionAndTag>> transactions;

    public TransactionViewModel(Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        transactions = transactionRepository.getAll();
    }

    public LiveData<List<TransactionAndTag>> getTransactions() { return transactions; };

    public void insert(Transaction transaction) {
        transactionRepository.insert(transaction);
    }
}
