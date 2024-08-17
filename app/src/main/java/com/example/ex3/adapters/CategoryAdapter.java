package com.example.ex3.adapters;

import static com.example.ex3.MyApplication.context;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ex3.R;
import com.example.ex3.api.FavoritesAPI;
import com.example.ex3.entities.Store;
import com.example.ex3.entities.Category;
import com.example.ex3.utils.UserPreferencesUtils;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<Category> categoryList;
    private List<Store> chosenStores;
    private List<Store> favStores;
    private final TextView badgeTextView;

    public CategoryAdapter(Context context, List<Category> categoryList, List<Store> chosenStores,List<Store> favStores, TextView badgeTextView) {
        this.context = context;
        this.categoryList = categoryList;
        this.chosenStores = chosenStores;
        this.badgeTextView = badgeTextView;
        this.favStores = favStores;
    }

    private void updateBadge() {
        if (badgeTextView != null) {
            int numberOfChosenStores = chosenStores.size();
            if (numberOfChosenStores > 0) {
                badgeTextView.setVisibility(View.VISIBLE);
                badgeTextView.setText(String.valueOf(numberOfChosenStores));
            } else {
                badgeTextView.setVisibility(View.GONE);
            }
        }
    }
    public void updateChosenStores(List<Store> newChosenStores) {
        this.chosenStores = newChosenStores;
        notifyDataSetChanged();
    }

    public void updateFavoriteStores(List<Store> newFavoriteStores) {
        this.favStores = newFavoriteStores;
        notifyDataSetChanged();
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

        StoreItemAdapter storeItemAdapter = new StoreItemAdapter(context, category.getStoresList(), chosenStores,favStores, new StoreItemAdapter.OnStoreInteractionListener() {
            @Override
            public void onStoreAddedToList(Store store) {
                if (chosenStores.contains(store)) {
                    chosenStores.remove(store);
                    UserPreferencesUtils.setChosenStores(context, chosenStores);

                } else {
                    chosenStores.add(store);
                    UserPreferencesUtils.setChosenStores(context, chosenStores);
                }
                updateBadge();
            }

            @Override
            public void onStoreAddedToFavorites(Store store) {
                String bearerToken = UserPreferencesUtils.getToken(context);
                if (favStores.contains(store)) {
                    favStores.remove(store);
                    FavoritesAPI.getInstance().removeFromFavorites(bearerToken,store);
                    UserPreferencesUtils.removeFavoriteStore(context, store);
                } else {
                    favStores.add(store);
                    FavoritesAPI.getInstance().addToFavorites(bearerToken, store);
                    UserPreferencesUtils.addFavoriteStore(context, store);

                }
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

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.tvCategoryHeader);
            storeItemRecyclerView = itemView.findViewById(R.id.rvStoreItems);
        }
    }
}
