package com.example.expensetracker2020.Database.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.Entities.Interval;
import com.example.expensetracker2020.Database.Entities.Tag;
import com.example.expensetracker2020.Database.Repositories.IntervalRepository;
import com.example.expensetracker2020.Database.Repositories.TagRepository;

import java.util.List;

public class IntervalViewModel extends AndroidViewModel {

    private IntervalRepository intervalRepository;
    private LiveData<List<Interval>> intervals;

    public IntervalViewModel(Application application) {
        super(application);
        intervalRepository = new IntervalRepository(application);
        intervals = intervalRepository.getAll();
    }

    public LiveData<List<Interval>> getIntervals() { return intervals; };

    public void insert(Interval interval) {
        intervalRepository.insert(interval);
    }
}
