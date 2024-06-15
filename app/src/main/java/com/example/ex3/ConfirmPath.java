package com.example.ex3;

import static com.example.ex3.MyApplication.context;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ex3.adapters.ConfirmPathAdapter;
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


    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_path);
        bearerToken = UserPreferencesUtils.getToken(context);
        chosenStores = UserPreferencesUtils.getChosenStores(context);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ConfirmPathAdapter(chosenStores);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                adapter.moveItem(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // No swipe actions needed
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        Button buttonConfirm = findViewById(R.id.button_confirm);
        buttonConfirm.setOnClickListener(v -> {
            List<Store> reorderedStores = adapter.getChosenStores();
            Intent intent = new Intent(ConfirmPath.this, NavigateActivity.class);
            startActivity(intent);
        });

        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmPath.this, CurrentLocation.class);
            startActivity(intent);
        });

        // Initialize the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);

        // Set up the item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // Already on the Home screen, do nothing
                        return true;
                    case R.id.menu_navigate:
                        // Navigate to NavigateActivity and pass the token
                        Intent navigateIntent = new Intent(ConfirmPath.this, NavigateActivity.class);
                        startActivity(navigateIntent);
                        return true;
                    case R.id.menu_favorites:
                        // Navigate to SettingsActivity and pass the token
                        Intent favoritesIntent = new Intent(ConfirmPath.this, Favorites.class);
                        // Assuming bearerToken is your token variable
                        startActivity(favoritesIntent);
                        return true;
                }
                return false;
            }
        });
    }
}
