package com.example.expensetracker2020.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.Entities.TransactionAndTag;
import com.example.expensetracker2020.Database.ViewModels.AccountViewModel;
import com.example.expensetracker2020.Database.ViewModels.TransactionViewModel;
import com.example.expensetracker2020.HelperClasses.DecimalDigitsInputFilter;
import com.example.expensetracker2020.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.round;


public class HomeFragment extends Fragment {
    private AlertDialog.Builder alert;
    private AccountViewModel accountViewModel;
    private TransactionViewModel transactionViewModel;
    private Calendar calendar = Calendar.getInstance();
    private Date startMonth;
    private Date endMonth;
    private TextView balance;
    private TextView month;
    private TextView calculatedBalanceText;
    private TextView toSpendOrSave;
    private List<TransactionAndTag> transactionsFromCurrentMonth;
    private double calculatedBalance;
    private DecimalFormat balanceFormat = new DecimalFormat("#######0.00");
    private Account account;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // let the user know the entered transaction has been inserted into the database
        if (getArguments() != null && getArguments().getBoolean("transaction_success")) {
            Toast.makeText(getContext(), "Transaction Success!", Toast.LENGTH_SHORT).show();
        }
        alert = new AlertDialog.Builder(getContext());
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        balance = view.findViewById(R.id.balance);
        calculatedBalanceText = view.findViewById(R.id.calculatedBalance);
        toSpendOrSave = view.findViewById(R.id.to_spend_or_save);
        month = view.findViewById(R.id.month);
        calculatedBalance = 0.0;

        // get current month
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(calendar.getTime());
        month.setText(month_name);

        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBalance();
            }
        });

        // set listener for the 'add transaction' button
        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_HomeFragment_to_TransactionFragment);
            }
        });

        // get account balance and show it on screen
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        accountViewModel.getAccount().observe(getViewLifecycleOwner(), new Observer<Account>() {
            @Override
            public void onChanged(@Nullable Account accountGet) {
                if (accountGet != null) {
                    account = accountGet;
                    double d = account.getBalance();
                    balance.setText("€ " + d);
                }
            }
        });

        // get a list of all transactions of the current month
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        startMonth = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
        endMonth = calendar.getTime();
        transactionViewModel.getTransactionsFromCurrentMonth(startMonth, endMonth).observe(getViewLifecycleOwner(), new Observer<List<TransactionAndTag>>() {
            @Override
            public void onChanged(@Nullable final List<TransactionAndTag> transactionsAndTags) {
                transactionsFromCurrentMonth = transactionsAndTags;
                for (TransactionAndTag transaction : transactionsFromCurrentMonth) {
                    switch (transaction.getTransaction().getType()) {
                        case "Deposit":
                            calculatedBalance += transaction.getTransaction().getAmount();
                            break;
                        case "Withdrawal":
                            calculatedBalance -= transaction.getTransaction().getAmount();
                            break;
                    }
                }
                if (calculatedBalance > 0.00) {
                    calculatedBalanceText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPositiveBalance));
                    toSpendOrSave.setText(getString(R.string.to_spend));
                } else {
                    calculatedBalanceText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorNegativeBalance));
                    toSpendOrSave.setText(getString(R.string.to_save));
                }
                calculatedBalanceText.setText("€ " + balanceFormat.format(abs(calculatedBalance)));
            }
        });
    }

    private void editBalance() {
        final EditText editBalance = new EditText(getContext());
        editBalance.setInputType(InputType.TYPE_CLASS_NUMBER);
        editBalance.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(20,2)});
        editBalance.setText(balance.getText().toString().replaceAll("[^\\\\.0123456789]",""));
        alert.setMessage("Edit your balance");

        alert.setView(editBalance);

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // What ever you want to do with the value
                account.setBalance(Double.parseDouble(editBalance.getText().toString()));
                accountViewModel.update(account);
                Toast.makeText(getContext(), "Account balance updated!", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }
}