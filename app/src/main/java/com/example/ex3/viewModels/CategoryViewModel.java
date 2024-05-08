package com.example.ex3.viewModels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ex3.entities.Store;
import com.example.ex3.repositories.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel extends ViewModel {
    private String categoryName;
    private List<Store> storesList;
    private final CategoryRepository contactsRepository;

    public CategoryViewModel(String categoryName, List<Store> storeItemList, CategoryRepository contactsRepository) {
        this.categoryName = categoryName;
        this.storesList = storeItemList;
        this.contactsRepository = contactsRepository;
    }
    public CategoryViewModel(String token, String type){
        contactsRepository = new CategoryRepository(token,type);
        storesList = new ArrayList<>();
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Store> getStoreItemList() {
        return storesList;
    }

    public void setStoreItemList(List<Store> storeItemList) {
        this.storesList = storeItemList;
    }


    public void reload() {
        contactsRepository.reload();
    }

    public void logout() {
        contactsRepository.logout();
    }

}