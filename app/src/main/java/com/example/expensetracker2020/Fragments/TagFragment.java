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
import android.view.Gravity;
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
    private int toDeleteTagTopPadding = 50;

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

        // set adapter for the recyclerview containing the list of tags
        final TagAdapter tagListAdapter = new TagAdapter();
        tagListView.setAdapter(tagListAdapter);

        // start process of adding the new tag when button is pressed
        addNewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag();
            }
        });

        // get tags and add them to the adapter and then to the recyclerview
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

    /**
     * get the filled in text and insert the new tag into the database
     * if the text is empty, don't do anything and instead show an error message to the user
     */
    private void addTag() {
        String tagName = newTag.getText().toString();
        if (!tagName.isEmpty()) {
            tagViewModel.insert(new Tag(tagName));
            newTag.setText("");
        } else {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.tag_insert_error),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * set up a pop-up window where the user can edit the selected tag
     */
    private void editTag() {
        // create an EditText for the user to edit the tag name
        final EditText editTag = new EditText(getContext());
        editTag.setText(currentTag.getName());
        alert.setMessage(getContext().getResources().getString(R.string.edit_tag));
        alert.setView(editTag);

        // when the update button is pressed, check if the EditText is empty and if it isn't
        // update the tag name in the database
        alert.setPositiveButton(getContext().getResources().getString(R.string.prompt_update), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!editTag.getText().toString().isEmpty()) {
                    currentTag.setName(editTag.getText().toString());
                    tagViewModel.update(currentTag);
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.tag_update_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton(getContext().getResources().getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // don't do anything except close the pop-up
            }
        });
        // The alert pop-up is now fully built, so show it to the user
        alert.show();
    }

    /**
     * Show a pop-up asking the user to confirm the deletion of the tag
     */
    private void deleteTag() {
        final TextView deleteTag = new TextView(getContext());
        alert.setMessage(getContext().getResources().getString(R.string.tag_delete_confirm));
        deleteTag.setText(currentTag.getName());
        deleteTag.setGravity(Gravity.CENTER);
        deleteTag.setPadding(0, toDeleteTagTopPadding, 0, 0);
        alert.setView(deleteTag);
        alert.setPositiveButton(getContext().getResources().getString(R.string.prompt_delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                tagViewModel.delete(currentTag);
            }
        });
        alert.setNegativeButton(getContext().getResources().getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }
}