package com.example.ex3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ex3.R;
import com.example.ex3.entities.Store;
import com.example.ex3.objects.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<Category> categoryList;
    private final List<Store> chosenStores;
    private final TextView badgeTextView;

    public interface OnAddStoreClickListener {
        void onAddStoreClick(Store store);
    }

    public CategoryAdapter(Context context, List<Category> categoryList, List<Store> chosenStores, TextView badgeTextView) {
        this.context = context;
        this.categoryList = categoryList;
        this.chosenStores = chosenStores;
        this.badgeTextView = badgeTextView;
    }

    private void updateBadge() {
        if (badgeTextView != null) {
            // Update the badge text with the number of chosen stores
            int numberOfChosenStores = chosenStores.size();
            if (numberOfChosenStores > 0) {
                badgeTextView.setVisibility(View.VISIBLE);
                badgeTextView.setText(String.valueOf(numberOfChosenStores));
            } else {
                badgeTextView.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryNameTextView.setText(category.getCategoryName());
        StoreItemAdapter storeItemAdapter = new StoreItemAdapter(context, category.getStoresList(), new OnAddStoreClickListener() {
            @Override
            public void onAddStoreClick(Store store) {
                // Remove or add to the chosen list
                if (chosenStores.contains(store)) {
                    chosenStores.remove(store);
                } else {
                    chosenStores.add(store);
                }
                // Update the badge after adding or removing a store
                updateBadge();
            }
        });
        holder.storeItemRecyclerView.setAdapter(storeItemAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        RecyclerView storeItemRecyclerView;
        Button btnAddStore;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.tvCategoryHeader);
            storeItemRecyclerView = itemView.findViewById(R.id.rvStoreItems);
            btnAddStore = itemView.findViewById(R.id.btnAddStore);
        }
    }
}
