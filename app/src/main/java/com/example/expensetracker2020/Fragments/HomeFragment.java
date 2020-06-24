package com.example.expensetracker2020.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
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
    final int ACCOUNT_INITIALIZED_VALUE = 1;
    private AlertDialog.Builder alert;
    private AccountViewModel accountViewModel;
    private TransactionViewModel transactionViewModel;
    private Calendar calendar = Calendar.getInstance();
    private Date startMonth;
    private Date endMonth;
    private Date currentDate = new Date();
    private TextView balance;
    private TextView month;
    private TextView calculatedBalanceText;
    private TextView toSpendOrSave;
    private List<TransactionAndTag> transactionsFromCurrentMonth;
    private List<TransactionAndTag> transactionsBeforeCurrentDateAndUnaccountedFor;
    private double calculatedBalance;
    private DecimalFormat balanceFormat = new DecimalFormat("#######0.00");
    private final int DECIMAL_FILTER_DIGITS_BEFORE_ZERO = 20;
    private final int DECIMAL_FILTER_DIGITS_AFTER_ZERO = 2;
    private Account account;

    // important boolean. Since LiveData keeps track of the data it's observing, it will want to
    // call the onChanged function whenever we have updated transactions to be accounted for.
    // this means that the onChanged function will be called one extra time and mess up the values
    // shown to the user. This boolean is added to prevent this problem.
    private boolean calculatingTransactions;

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
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.transaction_success), Toast.LENGTH_SHORT).show();
            setArguments(new Bundle());
        }
        alert = new AlertDialog.Builder(getContext());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        balance = view.findViewById(R.id.balance);
        calculatedBalanceText = view.findViewById(R.id.calculatedBalance);
        toSpendOrSave = view.findViewById(R.id.to_spend_or_save);
        month = view.findViewById(R.id.month);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        calculatedBalance = 0.0;
        calculatingTransactions = false;


        // get current month and display it to the user
        getCurrentMonth();

        // make the balance editable when the user taps on it
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

        getAccountBalance();
        getTransactionListAndCalculateCurrentBalance();
    }

    /**
     * create a pop-up window where the user can edit the balance
     */
    private void editBalance() {
        // Create an EditText for the user to fill their bank balance and make sure the user
        // can only create decimal numbers
        final EditText editBalance = new EditText(getContext());
        editBalance.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editBalance.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(DECIMAL_FILTER_DIGITS_BEFORE_ZERO, DECIMAL_FILTER_DIGITS_AFTER_ZERO)});
        editBalance.setText(balance.getText().toString().replaceAll("[^\\\\.0123456789]", ""));
        alert.setMessage(getContext().getResources().getString(R.string.edit_your_balance));

        alert.setView(editBalance);

        // update the account with the new balance
        alert.setPositiveButton(getContext().getResources().getString(R.string.prompt_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                account.setBalance(Double.parseDouble(editBalance.getText().toString()));
                // by setting the initialized value of the account to 1, the app knows that the
                // balance has been set
                if (account.getInitialized() != ACCOUNT_INITIALIZED_VALUE) {
                    account.setInitialized(ACCOUNT_INITIALIZED_VALUE);
                }
                accountViewModel.update(account);
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.account_balance_updated), Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton(getContext().getResources().getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }

    /**
     * Get the current account balance and show it to the user
     */
    private void getAccountBalance() {
        accountViewModel.getAccount().observe(getViewLifecycleOwner(), new Observer<Account>() {
            @Override
            public void onChanged(@Nullable Account accountGet) {
                if (accountGet != null) {
                    account = accountGet;
                    if (accountGet.getInitialized() == ACCOUNT_INITIALIZED_VALUE) {
                        double d = account.getBalance();
                        balance.setText("€ " + balanceFormat.format(d));
                    }
                }
            }
        });
    }

    /**
     * Get all transactions from the current month and calculate how much the user can spend or
     * has to save this month to stay even
     */
    private void getTransactionListAndCalculateCurrentBalance() {
        // get the start and end date of the month so the application can query the transactions
        // correctly. Then reset the calendar because we need it for other stuff
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        startMonth = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
        endMonth = calendar.getTime();
        calendar.setTime(currentDate);

        transactionViewModel.getTransactionsFromCurrentMonth(startMonth, endMonth).observe(getViewLifecycleOwner(), new Observer<List<TransactionAndTag>>() {
            @Override
            public void onChanged(@Nullable final List<TransactionAndTag> transactionsAndTags) {
                if (!calculatingTransactions) {
                    transactionsFromCurrentMonth = transactionsAndTags;
                    for (TransactionAndTag transaction : transactionsFromCurrentMonth) {
                        /*
                         * this switch case calculates how much money you can spend or have to save
                         * to get even this month
                         */
                        switch (transaction.getTransaction().getType()) {
                            case "Deposit":
                                calculatedBalance += transaction.getTransaction().getAmount();
                                break;
                            case "Withdrawal":
                                calculatedBalance -= transaction.getTransaction().getAmount();
                                break;
                        }
                    }
                    // set calculated balance in view
                    if (calculatedBalance >= 0.00) {
                        calculatedBalanceText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPositiveBalance));
                        toSpendOrSave.setText(getString(R.string.to_spend));
                    } else {
                        calculatedBalanceText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorNegativeBalance));
                        toSpendOrSave.setText(getString(R.string.to_save));
                    }
                    calculatedBalanceText.setText("€ " + balanceFormat.format(abs(calculatedBalance)));
                    getUnaccountedForTransactions();
                }
            }
        });
    }

    /**
     * Get all unaccounted for transactions and check if they are scheduled before right now.
     * If they are then adjust the bank balance and update the transactions to let the app
     * know that they are now accounted for.
     */
    private void getUnaccountedForTransactions() {
        transactionViewModel.getUnaccountedForTransactions().observe(getViewLifecycleOwner(), new Observer<List<TransactionAndTag>>() {
            @Override
            public void onChanged(@Nullable final List<TransactionAndTag> transactionsAndTags) {
                if (!calculatingTransactions) {
                    transactionsBeforeCurrentDateAndUnaccountedFor = transactionsAndTags;
                    for (TransactionAndTag transaction : transactionsBeforeCurrentDateAndUnaccountedFor) {
                        if (transaction.getTransaction().getDate().compareTo(calendar.getTime()) < 0) {
                            switch (transaction.getTransaction().getType()) {
                                case "Deposit":
                                    account.setBalance(account.getBalance() + transaction.getTransaction().getAmount());
                                    break;
                                case "Withdrawal":
                                    account.setBalance(account.getBalance() - transaction.getTransaction().getAmount());
                                    break;
                            }
                            accountViewModel.update(account);
                            transaction.getTransaction().setIsAccountedFor(1);
                            transactionViewModel.update(transaction.getTransaction());
                        }
                    }
                }
                calculatingTransactions = true;
            }
        });

    }

    // get current month and set it on screen for the user
    private void getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(calendar.getTime());
        month.setText(month_name);
    }
}