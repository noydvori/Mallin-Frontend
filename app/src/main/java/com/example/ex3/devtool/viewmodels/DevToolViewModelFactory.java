package com.example.ex3.devtool.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ex3.adapters.GrapthDataAdapter;
import com.example.ex3.devtool.database.GraphDatabase;

public class DevToolViewModelFactory implements ViewModelProvider.Factory {
    GraphDatabase mGraphDatabase;
    String name;
    public DevToolViewModelFactory(GraphDatabase database,String name) {
        this.mGraphDatabase = database;
        this.name =name;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DevToolViewModel.class)) {
            return (T) new DevToolViewModel(mGraphDatabase,name);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

