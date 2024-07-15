package com.example.ex3.viewModels;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CurrentLocationViewModelFactory implements ViewModelProvider.Factory {
    public CurrentLocationViewModelFactory() {

    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CurrentLocationViewModel.class)) {
            return (T) new CurrentLocationViewModel();
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

