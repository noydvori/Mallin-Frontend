package com.example.ex3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex3.R;
import com.example.ex3.entities.Store;
import com.example.ex3.objects.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.categoryList = categoryList;
    }
    public CategoryAdapter(Context context) {
        this.categoryList = new ArrayList<>();
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
        StoreItemAdapter storeItemAdapter = new StoreItemAdapter(category.getStoresList());
        holder.storeItemRecyclerView.setAdapter(storeItemAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        RecyclerView storeItemRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.tvCategoryHeader);
            storeItemRecyclerView = itemView.findViewById(R.id.rvStoreItems);
        }

    }
}
