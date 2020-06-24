package com.example.expensetracker2020.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker2020.Database.Entities.Tag;
import com.example.expensetracker2020.R;

import java.util.ArrayList;
import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagHolder> {
    private List<Tag> tags = new ArrayList<>(); // needs to be initialized, otherwise we might get a nullpointer exception
    private OnItemClickListener listener;

    @NonNull
    @Override
    public TagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.tag_item, parent, false);
        return new TagHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TagHolder holder, int position) {
        Tag currentTag = tags.get(position);
        holder.tagName.setText(currentTag.getName());
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
        notifyDataSetChanged();
    }

    class TagHolder extends RecyclerView.ViewHolder {
        private TextView tagName;
        private ImageButton editTag;
        private ImageButton deleteTag;

        public TagHolder(View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.tag_name);
            editTag = itemView.findViewById(R.id.edit_tag);
            deleteTag = itemView.findViewById(R.id.delete_tag);

            editTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(tags.get(position));
                    }
                }
            });

            deleteTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(tags.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onEditClick(Tag tag);
        void onDeleteClick(Tag tag);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
