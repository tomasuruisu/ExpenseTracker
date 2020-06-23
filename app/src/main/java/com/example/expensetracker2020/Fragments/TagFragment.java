package com.example.expensetracker2020.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker2020.Adapters.TagAdapter;
import com.example.expensetracker2020.Database.Entities.Tag;
import com.example.expensetracker2020.Database.ViewModels.TagViewModel;
import com.example.expensetracker2020.HelperClasses.DecimalDigitsInputFilter;
import com.example.expensetracker2020.R;

import java.util.List;

public class TagFragment extends Fragment {
    private AlertDialog.Builder alert;
    TagViewModel tagViewModel;
    RecyclerView tagListView;
    private EditText newTag;
    private ImageButton addNewTag;
    private Tag currentTag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alert = new AlertDialog.Builder(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newTag = view.findViewById(R.id.new_tag);
        addNewTag = view.findViewById(R.id.add_tag);
        tagViewModel = new ViewModelProvider(this).get(TagViewModel.class);
        tagListView = view.findViewById(R.id.tag_list_view);
        tagListView.setLayoutManager(new LinearLayoutManager(getContext()));
        tagListView.setHasFixedSize(true);

        final TagAdapter tagListAdapter = new TagAdapter();
        tagListView.setAdapter(tagListAdapter);

        addNewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag();
            }
        });

        tagViewModel.getTags().observe(getViewLifecycleOwner(), new Observer<List<Tag>>() {
            @Override
            public void onChanged(List<Tag> tags) {
                tagListAdapter.setTags(tags);
            }
        });

        tagListAdapter.setOnItemClickListener(new TagAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Tag tag) {
                currentTag = tag;
                editTag();
            }

            @Override
            public void onDeleteClick(Tag tag) {
                currentTag = tag;
                deleteTag();
            }
        });
    }

    private void addTag() {
        String tagName = newTag.getText().toString();
        if (!tagName.isEmpty()) {
            tagViewModel.insert(new Tag(tagName));
            newTag.setText("");
        } else {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.tag_insert_error),Toast.LENGTH_SHORT).show();
        }
    }

    private void editTag() {
        final EditText editTag = new EditText(getContext());
        editTag.setText(currentTag.getName());
        alert.setMessage("Edit tag");
        alert.setView(editTag);
        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!editTag.getText().toString().isEmpty()) {
                    currentTag.setName(editTag.getText().toString());
                    tagViewModel.update(currentTag);
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.tag_update_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void deleteTag() {
        final TextView deleteTag = new TextView(getContext());
        alert.setMessage("Are you sure you want to delete this tag?");
        deleteTag.setText(currentTag.getName());
        alert.setView(deleteTag);
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                tagViewModel.delete(currentTag);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }
}