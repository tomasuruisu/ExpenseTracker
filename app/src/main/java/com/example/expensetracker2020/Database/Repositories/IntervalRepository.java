package com.example.expensetracker2020.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.expensetracker2020.Database.AppDatabase;
import com.example.expensetracker2020.Database.DAOs.AccountDao;
import com.example.expensetracker2020.Database.DAOs.IntervalDao;
import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.Entities.Interval;
import com.example.expensetracker2020.Database.Entities.TransactionAndTag;

import java.util.List;

public class IntervalRepository {

    private IntervalDao intervalDao;
    private LiveData<List<Interval>> intervals;

    public IntervalRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        intervalDao = database.intervalDao();
        intervals = intervalDao.getAll();
    }

    public LiveData<List<Interval>> getAll() {
        return intervals;
    }

    public void insert(Interval interval) {
        new IntervalRepository.InsertIntervalAsyncTask(intervalDao).execute(interval);
    }

    private static class InsertIntervalAsyncTask extends AsyncTask<Interval, Void, Void> {
        private IntervalDao intervalDao;

        private InsertIntervalAsyncTask(IntervalDao intervalDao) {
            this.intervalDao = intervalDao;
        }
        @Override
        protected Void doInBackground(Interval... intervals) {
            intervalDao.insert(intervals[0]);
            return null;
        }
    }

}
