package com.example.ex3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.example.ex3.entities.Store;
import com.example.ex3.fetchers.StoreFetcher;
import com.example.ex3.objects.Category;
import com.example.ex3.viewModels.CategoryViewModel;
import com.example.ex3.viewModels.CategoryViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.annotation.SuppressLint;import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ex3.adapters.CategoryAdapter;
import com.example.ex3.entities.Chat;
import com.example.ex3.entities.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    private MutableLiveData<List<Chat>> filteredChats;
    private User me;
    String bearerToken;
    private CategoryViewModel viewModel;
    CategoryAdapter adapter;
    private List<Category> categories = new ArrayList<>();
    private int typesToFetch = 3; // Number of types to fetch


    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved theme preference
        SharedPreferences sharedPreferencesSettings = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkThemeEnabled = sharedPreferencesSettings.getBoolean("DarkTheme", false);
        filteredChats = new MutableLiveData<List<Chat>>();
        // Apply the saved theme preference
        if (isDarkThemeEnabled) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_list);
        // Retrieve the Intent that started this activity
        Intent intent = getIntent();






        // Extract token & get user details from database
        //SharedPreferences userSharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        //String myUsername = userSharedPreferences.getString("User", "");
        //me = chatsViewModel.getUser(myUsername);
        String myName = "Noy";

        // Add contact button
        FloatingActionButton addContactButton = findViewById(R.id.addContact);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddContactDialog();
            }
        });
        // Retrieve the token from the Intent extras
        String token = intent.getStringExtra("token");
        bearerToken = token;
        SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Perform the refresh action here
                fetchDataFromServer();
                // Stop the refreshing animation
                refreshLayout.setRefreshing(false);
            }
        });


        String storeType = "food"; // Example store type
        //StoreFetcher storeFetcher = new StoreFetcher();
        //storeFetcher.fetchStores(token, storeType);
        // Fetch stores for each store type
        fetchStoresByType(token, "food");
        fetchStoresByType(token, "fashion and sports");
        fetchStoresByType(token, "fashion");
        fetchStoresByType(token, "shoes");
        fetchStoresByType(token, "electricity");
        fetchStoresByType(token, "accessories & jewelries");
        //RecyclerView categoriesList = findViewById(R.id.categories);
        //categoriesList.setBackgroundResource(R.drawable.bg_dark_rounded);
        //adapter = new CategoryAdapter(this);
        //categoriesList.setAdapter(adapter);
        //categoriesList.setLayoutManager(new LinearLayoutManager(this));
        //CategoryViewModelFactory factory = new CategoryViewModelFactory(bearerToken,"food");
        //viewModel = new ViewModelProvider(this, factory).get(CategoryViewModel.class);
        //viewModel.reload();
        //viewModel.getStoresList().observe(this, stores -> {
        //    // Update UI with the new list of stores
        //    if (stores != null) {
        //        adapter.setStoresList(stores);
        //    }
        //});


       // Add more categories and items as needed...


       // Set up RecyclerView with adapter
       //RecyclerView categoriesList = findViewById(R.id.categories);
       //categoriesList.setBackgroundResource(R.drawable.bg_dark_rounded);
       //categoriesList.setLayoutManager(new LinearLayoutManager(this));
       //CategoryAdapter adapter = new CategoryAdapter(this, categories);
       //categoriesList.setAdapter(adapter);

        // Chat item listener
        //adapter = new ChatListAdapter(this, filteredChats);
        //adapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
        //@Override

            //public void onItemClick(Chat chat) {
                ;
            //}
        //});
        // In your activity or fragment where you have access to the badge TextView:
        TextView badgeTextView = findViewById(R.id.locationBadge);

        // Set the desired number dynamically
        // TODO: badge.
        int number = 10; // Replace this with your desired number
        badgeTextView.setText(String.valueOf(number));




        // Handle search
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setBackgroundResource(R.drawable.bg_white_rounded);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String str) {
                if (str.isEmpty()) {
                    // Clear the search query and fetch all stores again
                    categories.clear();
                    fetchStoresByType(token, "food");
                    fetchStoresByType(token, "fashion and sports");
                    fetchStoresByType(token, "fashion");
                } else {
                    // Call server endpoint with the search query
                    fetchStoresByName(token, str);
                }
                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                // You can implement live search here if needed
                // For example, you can filter the list of categories based on the new text
                // and update the UI accordingly

                if (newText.isEmpty()) {
                    // If the search query is empty, clear the list and fetch all stores again
                    categories.clear();
                    fetchStoresByType(token, "food");
                    fetchStoresByType(token, "fashion and sports");
                    fetchStoresByType(token, "fashion");
                    fetchStoresByType(token, "shoes");
                    fetchStoresByType(token, "electricity");
                    fetchStoresByType(token, "accessories & jewelries");
                } else {
                    // Call server endpoint with the new search query
                    fetchStoresByName(token, newText);
                }

                return true;
            }
        });

    }
    private void fetchDataFromServer() {
        // Fetch your data from the server
        // For example, you can re-fetch your store items
        fetchStoresByType(bearerToken, "food");
        fetchStoresByType(bearerToken, "fashion and sports");
        fetchStoresByType(bearerToken, "fashion");
        fetchStoresByType(bearerToken, "shoes");
        fetchStoresByType(bearerToken, "electricity");
        fetchStoresByType(bearerToken, "accessories & jewelries");
    }
    private void fetchStoresByName(String token, String str) {
        // Call the server endpoint to fetch stores by name
        StoreFetcher storeFetcher = new StoreFetcher();
        storeFetcher.fetchStoresByName(token, str, new StoreFetcher.FetchStoresCallback() {
            @Override
            public void onSuccess(Category category) {
                // Update UI with the filtered stores
                List<Category> filteredCategories = new ArrayList<>();
                filteredCategories.add(category);
                updateUI(filteredCategories);
            }

            @Override
            public void onError(Throwable throwable) {
                // Handle error
                Toast.makeText(Home.this, "Error fetching stores by name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(List<Category> filteredCategories) {
        RecyclerView categoriesList = findViewById(R.id.categories);
        categoriesList.setBackgroundResource(R.drawable.bg_dark_rounded);
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        CategoryAdapter adapter = new CategoryAdapter(this, filteredCategories);
        categoriesList.setAdapter(adapter);
    }


    private void fetchStoresByType(String token, String storeType) {
        StoreFetcher storeFetcher = new StoreFetcher();
        storeFetcher.fetchStores(token, storeType, new StoreFetcher.FetchStoresCallback() {
            @Override
            public void onSuccess(Category category) {
                // Add fetched category to the list
                categories.add(category);
                // Update UI if all types are fetched
                if (categories.size() == typesToFetch) {
                    updateUI();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                // Handle error
                Toast.makeText(Home.this, "Error fetching " + storeType + " stores", Toast.LENGTH_SHORT).show();
                // Update UI if all types are fetched (considering error)
                if (categories.size() == typesToFetch) {
                    updateUI();
                }
            }
        });
    }

    private void updateUI() {
        RecyclerView categoriesList = findViewById(R.id.categories);
        categoriesList.setBackgroundResource(R.drawable.bg_dark_rounded);
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        categoriesList.setAdapter(adapter);
    }
    private void openAddContactDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_contact);
        dialog.setTitle("Add Contact");
        final EditText usernameEditText = dialog.findViewById(R.id.usernameEditText);
        // Add contact button
        Button addButton = dialog.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                if (!username.isEmpty()) {
                    addContact(username);
                    // Restart the activity to apply the theme changes
                    Intent intent = new Intent(Home.this, Home.class);

                    finish();
                    startActivity(intent);
                }
            }
        });
        dialog.show();
    }

    private void openSettingsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_settings);
        dialog.setTitle("Settings");

        // Initialize views
        Switch themeSwitch = dialog.findViewById(R.id.themeSwitch);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        // Load saved theme preference
        SharedPreferences sharedPreferencesSettings = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkThemeEnabled = sharedPreferencesSettings.getBoolean("DarkTheme", false);
        themeSwitch.setChecked(isDarkThemeEnabled);
        //EditText serverPortEditText = dialog.findViewById(R.id.serverPortEditText); // Add this line



        // Theme switch listener
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save theme preference
                SharedPreferences.Editor editor = sharedPreferencesSettings.edit();
                editor.putBoolean("DarkTheme", isChecked);
                editor.apply();

                // Restart the activity to apply the theme changes
                Intent intent = new Intent(Home.this, Home.class);

                finish();
                startActivity(intent);
            }
        });

        // Save button listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StringFormatInvalid")
            @Override
            public void onClick(View v) {
                // Get the entered server path
                //String serverPort = serverPortEditText.getText().toString().trim();

                // Save server path and port preferences
                //SharedPreferences.Editor editor = sharedPreferences.edit();
                //editor.putString("ServerPort", serverPort);
                //.apply();

                // Update the BaseUrl value
                //tring newBaseUrl = getString(R.string.ServerPath) + ":" + getString(R.string.ServerPort) + "/";
                //MyApplication.context.getResources().getString(R.string.BaseUrl, newBaseUrl);

                // Restart the activity to apply the theme changes
                //Intent intent = new Intent(ChatsList.this, MainActivity.class);

                //finish();
                //startActivity(intent);
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //chatsViewModel.getChats(me.getUsername());
    }



    private void addContact(String username) {

        //LiveData<Chat> newChat = chatsViewModel.createChat(username);
        //chatsViewModel.getChats(me.getUsername()).observe(this, chats -> {
        //    filteredChats.setValue(chats); // Initialize filteredChats with all chats
        //    adapter.setChats(chats);
        //});
        //newChat.observe(this, chat -> {
        //    Log.e("ChatAPI", "on observe");
        //    List<Chat> newListChats = filteredChats.getValue();
        //    newListChats.add(chat); // Initialize filteredChats with all chats
         //   filteredChats.setValue(newListChats);
        //    adapter.setChats(newListChats);

        //});
        //Log.e("ChatAPI", "newChat in activity "+ newChat.getValue());
        // TODO: add chat to relevant list
        //adapter.notifyDataSetChanged();
    }
}