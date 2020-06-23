package com.example.expensetracker2020.Database.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker2020.Database.Entities.Account;
import com.example.expensetracker2020.Database.Entities.Interval;
import com.example.expensetracker2020.Database.Entities.Tag;
import com.example.expensetracker2020.Database.Repositories.AccountRepository;
import com.example.expensetracker2020.Database.Repositories.TagRepository;

import java.util.List;

public class TagViewModel extends AndroidViewModel {

    private TagRepository tagRepository;
    private LiveData<List<Tag>> tags;


    public TagViewModel(Application application) {
        super(application);
        tagRepository = new TagRepository(application);
        tags = tagRepository.getAll();
    }

    public LiveData<List<Tag>> getTags() { return tags; };

    public void insert(Tag tag) {
        tagRepository.insert(tag);
    }

    public void update(Tag tag) {
        tagRepository.update(tag);
    }

    public void delete(Tag tag) {
        tagRepository.delete(tag);
    }
}
