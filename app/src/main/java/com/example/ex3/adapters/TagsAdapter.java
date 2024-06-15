package com.example.ex3.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex3.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

    private final List<String> tags;
    private OnTagClickListener listener;
    private int selectedPosition = 0;

    public TagsAdapter(List<String> tags) {
        this.tags = tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String tag = tags.get(position);
        holder.chip.setText(tag);

        if (position == selectedPosition) {
            holder.chip.setChipBackgroundColorResource(R.color.selected_tag_background);
            holder.chip.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.selected_tag_text));
        } else {
            holder.chip.setChipBackgroundColorResource(R.color.unselected_tag_background);
            holder.chip.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.unselected_tag_text));
        }

        holder.chip.setOnClickListener(v -> {
            if (listener != null) {
                selectedPosition = position;
                notifyDataSetChanged();
                listener.onTagClick(tag);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public interface OnTagClickListener {
        void onTagClick(String tag);
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectTag(String tag) {
        int position = tags.indexOf(tag);
        if (position >= 0) {
            selectedPosition = position;
            listener.onTagClick(tag);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        ViewHolder(View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip);
        }
    }
}
