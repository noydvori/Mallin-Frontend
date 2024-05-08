package com.example.ex3.localDB;

import static androidx.room.Room.databaseBuilder;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.ex3.MyApplication;
import com.example.ex3.converters.MsgConverter;
import com.example.ex3.converters.UserConverter;
import com.example.ex3.daos.ChatDao;
import com.example.ex3.daos.MsgDao;
import com.example.ex3.daos.StoreDao;
import com.example.ex3.daos.TokenDao;
import com.example.ex3.daos.UserDao;
import com.example.ex3.entities.Chat;
import com.example.ex3.entities.Msg;
import com.example.ex3.entities.Store;
import com.example.ex3.entities.Token;
import com.example.ex3.entities.User;

@Database(entities = {Token.class, Msg.class, User.class, Chat.class, Store.class}, version = 6, exportSchema = false)
@TypeConverters({UserConverter.class, MsgConverter.class})
public abstract class AppDB extends RoomDatabase {
    private static AppDB instance;

    public abstract TokenDao tokenDao();
    public abstract ChatDao chatDao();
    public abstract StoreDao storeDao();
    public abstract MsgDao msgDao();
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
