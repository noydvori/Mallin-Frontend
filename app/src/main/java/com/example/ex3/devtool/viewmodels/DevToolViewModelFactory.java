package com.example.ex3.devtool.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ex3.adapters.GrapthDataAdapter;
import com.example.ex3.devtool.database.GraphDatabase;

public class DevToolViewModelFactory implements ViewModelProvider.Factory {
    GraphDatabase mGraphDatabase;
    public DevToolViewModelFactory(GraphDatabase database) {
        this.mGraphDatabase = database;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DevToolViewModel.class)) {
            return (T) new DevToolViewModel(mGraphDatabase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

