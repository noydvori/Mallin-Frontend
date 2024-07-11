package com.example.ex3.viewModels;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ex3.devtool.database.GraphDatabase;

public class NavigateViewModelFactory implements ViewModelProvider.Factory {
    GraphDatabase mGraphDatabase;
    public NavigateViewModelFactory(GraphDatabase database) {
        this.mGraphDatabase = database;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NavigateViewModel.class)) {
            return (T) new NavigateViewModel(mGraphDatabase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

