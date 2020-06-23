package com.example.expensetracker2020.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.expensetracker2020.Database.Entities.Interval;
import com.example.expensetracker2020.Database.Entities.Tag;
import com.example.expensetracker2020.Database.Entities.Transaction;
import com.example.expensetracker2020.Database.ViewModels.IntervalViewModel;
import com.example.expensetracker2020.Database.ViewModels.TagViewModel;
import com.example.expensetracker2020.Database.ViewModels.TransactionViewModel;
import com.example.expensetracker2020.HelperClasses.DecimalDigitsInputFilter;
import com.example.expensetracker2020.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionFragment extends Fragment {
    private final Calendar transactionDateCalendar = Calendar.getInstance();
    private final int TRANSACTION_DAILY = 1;
    private final int TRANSACTION_WEEKLY = 7;
    private final int TRANSACTION_MONTHLY = 30;
    private final int TRANSACTION_DAILY_AMOUNT = 30;
    private final int TRANSACTION_WEEKLY_AMOUNT = 51;
    private final int TRANSACTION_MONTHLY_AMOUNT = 11;
    private TagViewModel tagViewModel;
    private IntervalViewModel intervalViewModel;
    private TransactionViewModel transactionViewModel;
    private DatePickerDialog.OnDateSetListener date;
    private EditText editAmount;
    private EditText editTitle;
    private EditText editDate;
    private EditText editTag;
    private EditText editInterval;
    private RadioGroup editType;
    private String dateFormat = "dd/MM/yyyy";
    private List<Tag> tagList;
    private Spinner tagSpinner;
    private List<Interval> intervalList;
    private Spinner intervalSpinner;
    private double amount;
    private Tag tag;
    private Interval interval;
    private String type = "Deposit";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editAmount = view.findViewById(R.id.amount);
        editTitle = view.findViewById(R.id.editTitle);
        editDate = view.findViewById(R.id.editDate);
        editTag = view.findViewById(R.id.editTag);
        editInterval = view.findViewById(R.id.editInterval);
        tagSpinner = view.findViewById(R.id.tag_spinner);
        intervalSpinner = view.findViewById(R.id.interval_spinner);
        editType = view.findViewById(R.id.edit_type);

        editType.check(R.id.is_deposit);
        editAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        // initialize date to today
        dateToString();

        // Let the user choose a date with a date picker
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                transactionDateCalendar.set(Calendar.YEAR, year);
                transactionDateCalendar.set(Calendar.MONTH, monthOfYear);
                transactionDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateToString();
            }
        };
        editDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), date, transactionDateCalendar
                        .get(Calendar.YEAR), transactionDateCalendar.get(Calendar.MONTH),
                        transactionDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // get tags and add them to the tag spinner
        tagViewModel = new ViewModelProvider(this).get(TagViewModel.class);
        tagViewModel.getTags().observe(getViewLifecycleOwner(), new Observer<List<Tag>>() {
            @Override
            public void onChanged(@Nullable final List<Tag> tags) {
                tagList = tags;
                String[] tagArray = new String[tags.size()];
                for (Tag tag : tags
                ) {
                    tagArray[tag.getId() - 1] = tag.getName();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                        android.R.layout.simple_spinner_item, tagArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tagSpinner.setAdapter(adapter);
            }
        });

        // open tag spinner when edit tag is selected
        editTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagSpinner.performClick();
                // visibility is set to invisible because we only want to show the dialog. not the
                // selectable dropdown
                tagSpinner.setVisibility(View.INVISIBLE);
            }
        });

        // set tag when a tag is selected
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedTag = tagSpinner.getSelectedItem().toString();
                editTag.setText(selectedTag);
                tag = tagList.get(i);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        // get intervals and add them to the interval spinner
        intervalViewModel = new ViewModelProvider(this).get(IntervalViewModel.class);
        intervalViewModel.getIntervals().observe(getViewLifecycleOwner(), new Observer<List<Interval>>() {
            @Override
            public void onChanged(@Nullable final List<Interval> intervals) {
                intervalList = intervals;
                String[] intervalArray = new String[intervals.size()];
                for (Interval interval : intervals
                ) {
                    intervalArray[interval.getId() - 1] = interval.getName();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                        android.R.layout.simple_spinner_item, intervalArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                intervalSpinner.setAdapter(adapter);
            }
        });

        // open interval spinner when edit interval is selected
        editInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intervalSpinner.performClick();
                // visibility is set to invisible because we only want to show the dialog. not the
                // selectable dropdown
                intervalSpinner.setVisibility(View.INVISIBLE);
            }
        });

        // set interval when an interval is selected
        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedInterval = intervalSpinner.getSelectedItem().toString();
                editInterval.setText(selectedInterval);
                interval = intervalList.get(i);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        editType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.is_deposit:
                        type = "Deposit";
                        break;
                    case R.id.is_withdrawal:
                        type = "Withdrawal";
                        break;
                }
            }
        });

        // save the transaction and navigate back to home
        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tag != null) {
                    Transaction transaction = new Transaction(editTitle.getText().toString(), Double.parseDouble(editAmount.getText().toString()), stringToDate(), type, tag.getId());
                    if (validateTransaction(transaction)) {
                            addTransaction(transaction);
                    } else {
                        showError(view);
                        return;
                    }
                } else {
                    showError(view);
                    return;
                }
            }
        });
    }

    private void dateToString() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        editDate.setText(sdf.format(transactionDateCalendar.getTime()));
    }

    @Nullable
    private Date stringToDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
            Date date = sdf.parse(editDate.getText().toString());
            return date;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean validateTransaction(Transaction transaction) {
        return !transaction.getTitle().isEmpty() && transaction.getAmount() != 0.0 && transaction.getDate() != null && (transaction.getType() == "Deposit" || transaction.getType() == "Withdrawal") && transaction.getTagId() >= 0;
    }

    private void showError(View view) {
        Toast.makeText(view.getContext(), "One of the fields contains a wrong value, please update the transaction", Toast.LENGTH_SHORT).show();
    }

    private void addTransaction(Transaction transaction) {
        Transaction newTransaction;
        transactionViewModel.insert(transaction);
        switch (interval.getDays()) {
            case TRANSACTION_DAILY:
                for (int i = 0; i < TRANSACTION_DAILY_AMOUNT; i++) {
                    transactionDateCalendar.add(Calendar.DAY_OF_YEAR, 1);
                    newTransaction = new Transaction(transaction.getTitle(), transaction.getAmount(), transactionDateCalendar.getTime(), transaction.getType(), transaction.getTagId());
                    transactionViewModel.insert(newTransaction);
            }
                break;
            case TRANSACTION_WEEKLY:
                for (int i = 0; i < TRANSACTION_WEEKLY_AMOUNT; i++) {
                    transactionDateCalendar.add(Calendar.WEEK_OF_YEAR, 1);
                    newTransaction = new Transaction(transaction.getTitle(), transaction.getAmount(), transactionDateCalendar.getTime(), transaction.getType(), transaction.getTagId());
                    transactionViewModel.insert(newTransaction);
                }
                break;
            case TRANSACTION_MONTHLY:
                for (int i = 0; i < TRANSACTION_MONTHLY_AMOUNT; i++) {
                    transactionDateCalendar.add(Calendar.MONTH, 1);
                    newTransaction = new Transaction(transaction.getTitle(), transaction.getAmount(), transactionDateCalendar.getTime(), transaction.getType(), transaction.getTagId());
                    transactionViewModel.insert(newTransaction);
                }
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("transaction_success", true);
        NavHostFragment.findNavController(TransactionFragment.this)
                .navigate(R.id.action_TransactionFragment_to_HomeFragment, bundle);
    }
}