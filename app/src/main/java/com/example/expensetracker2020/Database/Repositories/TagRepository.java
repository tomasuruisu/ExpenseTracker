package com.example.expensetracker2020.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.expensetracker2020.Database.AppDatabase;
import com.example.expensetracker2020.Database.DAOs.IntervalDao;
import com.example.expensetracker2020.Database.DAOs.TagDao;
import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.Entities.Interval;
import com.example.expensetracker2020.Database.Entities.Tag;

import java.util.List;

public class TagRepository {

    private TagDao tagDao;
    private LiveData<List<Tag>> tags;

    public TagRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        tagDao = database.tagDao();
        tags = tagDao.getAll();
    }

    public LiveData<List<Tag>> getAll() {
        return tags;
    }

    public void insert(Tag tag) {
        new TagRepository.InsertTagAsyncTask(tagDao).execute(tag);
    }

    private static class InsertTagAsyncTask extends AsyncTask<Tag, Void, Void> {
        private TagDao tagDao;

        private InsertTagAsyncTask(TagDao tagDao) {
            this.tagDao = tagDao;
        }
        @Override
        protected Void doInBackground(Tag... tags) {
            tagDao.insert(tags[0]);
            return null;
        }
    }

}
