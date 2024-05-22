package com.example.ex3.localDB;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ex3.MyApplication;
import com.example.ex3.daos.CategoryDao;
import com.example.ex3.daos.StoreDao;
import com.example.ex3.daos.TokenDao;
import com.example.ex3.daos.UserDao;
import com.example.ex3.entities.Store;
import com.example.ex3.entities.Token;
import com.example.ex3.entities.User;
import com.example.ex3.entities.Category;

@Database(entities = {Token.class, User.class, Store.class, Category.class}, version = 6, exportSchema = false)
public abstract class AppDB extends RoomDatabase {
    private static AppDB instance;

    public abstract TokenDao tokenDao();
    public abstract StoreDao storeDao();
    public abstract CategoryDao categoryDao();

    public abstract UserDao userDao();

    public static synchronized AppDB getInstance(){
        if (instance == null){
            instance = Room.databaseBuilder(MyApplication.context, AppDB.class, "AppDB")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();
        }
        return instance;
    }

}
