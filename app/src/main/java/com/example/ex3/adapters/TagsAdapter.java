package com.example.ex3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.chip.Chip;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ex3.R;
import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

    private final List<String> tags;
    private OnTagClickListener listener;
    private int selectedPosition = -1;

    public TagsAdapter(List<String> tags) {
        this.tags = tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String tag = tags.get(position);
        holder.chip.setText(tag);
        holder.chip.setChecked(position == selectedPosition);  // Set the chip checked state based on selection
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

    public void selectTag(String tag) {
        int position = tags.indexOf(tag);
        if (position >= 0) {
            selectedPosition = position;
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        ViewHolder(View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        selectedPosition = position;
                        notifyDataSetChanged();  // Update the UI to reflect the selected tag
                        listener.onTagClick(tags.get(position));
                    }
                }
            });
        }
    }
}
