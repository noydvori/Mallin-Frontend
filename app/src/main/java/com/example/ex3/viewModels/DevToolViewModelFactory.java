package com.example.ex3.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ex3.adapters.GrapthDataAdapter;

public class DevToolViewModelFactory implements ViewModelProvider.Factory {
    GrapthDataAdapter mGraphDataAdapter;
    public DevToolViewModelFactory(GrapthDataAdapter adapter) {
        this.mGraphDataAdapter = adapter;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DevToolViewModel.class)) {
            return (T) new DevToolViewModel(mGraphDataAdapter);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

