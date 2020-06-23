package com.example.expensetracker2020;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.ViewModels.AccountViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment {
    private AccountViewModel accountViewModel;
    private TextView currentBalance;
    private TextView balance;
    private TextView month;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        balance = view.findViewById(R.id.balance);
        currentBalance = view.findViewById(R.id.current_balance);
        month = view.findViewById(R.id.month);


        // let the user know the entered transaction has been inserted into the database
        if (getArguments() != null && getArguments().getBoolean("transaction_success")) {
            Toast.makeText(view.getContext(), "Transaction Success!", Toast.LENGTH_SHORT).show();
        }

        // get current month
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(calendar.getTime());
        month.setText(month_name);

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
            public void onChanged(@Nullable Account account) {
                if (account != null) {
                    double d = account.getBalance();
                    balance.setText("€ " + d);
                }
            }
        });
    }
}