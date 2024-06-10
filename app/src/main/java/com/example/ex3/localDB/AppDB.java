package com.example.ex3.localDB;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.ex3.MyApplication;
import com.example.ex3.daos.CategoryDao;
import com.example.ex3.daos.FavoriteStoreDao;
import com.example.ex3.daos.StoreDao;
import com.example.ex3.daos.TokenDao;
import com.example.ex3.daos.UserDao;
import com.example.ex3.entities.Category;
import com.example.ex3.entities.FavoriteStore;
import com.example.ex3.entities.Store;
import com.example.ex3.entities.Token;
import com.example.ex3.entities.User;

@Database(entities = {Category.class, Token.class, Store.class, User.class}, version = 2)
@TypeConverters({StoreListConverter.class})
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
