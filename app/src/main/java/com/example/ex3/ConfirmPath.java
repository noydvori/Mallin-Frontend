package com.example.ex3;

import static com.example.ex3.MyApplication.context;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex3.adapters.ConfirmPathAdapter;
import com.example.ex3.api.NavigationAPI;
import com.example.ex3.entities.Store;
import com.example.ex3.utils.UserPreferencesUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ConfirmPath extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConfirmPathAdapter adapter;
    private List<Store> chosenStores;
    private String bearerToken;
    private List<Store> favoriteStores;
    private List<Store> optimalOrder;

    private ToggleButton toggleOptimal;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_path);

        bearerToken = UserPreferencesUtils.getToken(context);

        chosenStores = UserPreferencesUtils.getChosenStores(context);
        optimalOrder = new ArrayList<>(chosenStores);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ConfirmPathAdapter(chosenStores);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                adapter.moveItem(fromPosition, toPosition);
                checkToggleState();
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        toggleOptimal = findViewById(R.id.toggle_optimal);
        toggleOptimal.setChecked(true);
        toggleOptimal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chosenStores.clear();
                chosenStores.addAll(optimalOrder);
                adapter.notifyDataSetChanged();
                Toast.makeText(ConfirmPath.this, "Order reset to optimal", Toast.LENGTH_SHORT).show();
            }
        });

        checkToggleState(); // Check the toggle state initially

        Button buttonConfirm = findViewById(R.id.button_confirm);
        buttonConfirm.setOnClickListener(v -> {
            fetchOrderedRout(UserPreferencesUtils.getLocation(), chosenStores);
            List<Store> reorderedStores = adapter.getChosenStores();
            Intent intent = new Intent(ConfirmPath.this, NavigateActivity.class);
            startActivity(intent);
        });

        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmPath.this, CurrentLocation.class);
            startActivity(intent);
        });

        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        return true;
                    case R.id.menu_navigate:
                        Intent navigateIntent = new Intent(ConfirmPath.this, NavigateActivity.class);
                        startActivity(navigateIntent);
                        return true;
                    case R.id.menu_favorites:
                        Intent favoritesIntent = new Intent(ConfirmPath.this, Favorites.class);
                        startActivity(favoritesIntent);
                        return true;
                }
                return false;
            }
        });
    }
    private void fetchOrderedRout(Store store, List<Store> stores) {
        String token = UserPreferencesUtils.getToken(this);
        NavigationAPI.getInstance().createOrderedRout(token, store, stores).thenAccept(nodes -> {
            UserPreferencesUtils.setNodes(this, nodes);
        }).exceptionally(throwable -> {
            return null;
        });
    }

    private void checkToggleState() {
        boolean isOptimal = chosenStores.equals(optimalOrder);
        toggleOptimal.setOnCheckedChangeListener(null); // Disable the listener temporarily
        toggleOptimal.setChecked(isOptimal);
        toggleOptimal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chosenStores.clear();
                chosenStores.addAll(optimalOrder);
                adapter.notifyDataSetChanged();
                Toast.makeText(ConfirmPath.this, "Order reset to optimal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
