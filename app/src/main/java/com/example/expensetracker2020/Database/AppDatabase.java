package com.example.expensetracker2020.Database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.expensetracker2020.Database.DAOs.AccountDao;
import com.example.expensetracker2020.Database.DAOs.IntervalDao;
import com.example.expensetracker2020.Database.DAOs.TagDao;
import com.example.expensetracker2020.Database.DAOs.TransactionDao;
import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.Entities.Interval;
import com.example.expensetracker2020.Database.Entities.Tag;
import com.example.expensetracker2020.Database.Entities.Transaction;

@Database(entities = {Transaction.class, Tag.class, Interval.class, Account.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract TransactionDao transactionDao();
    public abstract TagDao tagDao();
    public abstract IntervalDao intervalDao();
    public abstract AccountDao accountDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getDatabase(final Context context) {
        if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(initializeDatabase)
                            .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback initializeDatabase = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        AccountDao accountDao;
        IntervalDao intervalDao;
        TagDao tagDao;
        public PopulateDBAsyncTask(AppDatabase db) {
            accountDao = db.accountDao();
            intervalDao = db.intervalDao();
            tagDao = db.tagDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            accountDao.insert(new Account(0.0, 0));
            tagDao.insert(new Tag("Groceries"));
            tagDao.insert(new Tag("Work"));
            tagDao.insert(new Tag("Miscellaneous"));
            intervalDao.insert(new Interval("Once", 0));
            intervalDao.insert(new Interval("Daily", 1));
            intervalDao.insert(new Interval("Weekly", 7));
            intervalDao.insert(new Interval("Monthly", 30));
            return null;
        }
    };


}
