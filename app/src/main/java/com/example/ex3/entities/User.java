package com.example.ex3.entities;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "user")
public class User implements Parcelable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "displayName")
    private String displayName;


    public User(@NonNull String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(displayName);
        dest.writeString(username);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        displayName = in.readString();
        username = Objects.requireNonNull(in.readString());
    }
    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
