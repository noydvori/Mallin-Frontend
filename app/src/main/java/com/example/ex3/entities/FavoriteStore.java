package com.example.ex3.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "favoritestore")
public class FavoriteStore implements Parcelable{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "storename")
    private String storename;
    @ColumnInfo(name = "workingHours")

    private String workingHours;
    @ColumnInfo(name = "floor")

    private String floor;
    @ColumnInfo(name = "logoPic")

    private String logoPic;
    @ColumnInfo(name = "storeType")

    private String storeType;
    @ColumnInfo(name = "categoryId")
    private int categoryId;


    private boolean isAddedToList;

    private boolean isFavorite;

    public FavoriteStore(String storename,String workingHours, String floor, String logoPic,String storeType,boolean isFavorite) {
        this.storename = storename;
        this.workingHours = workingHours;
        this.floor = floor;
        this.logoPic =logoPic;
        this.storeType = storeType;
        this.isAddedToList = false;
        this.isFavorite = isFavorite;
    }

    @NonNull
    public String getStoreName() {
        return this.storename;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(@NonNull String name) {
        this.storename = name;
    }

    public String getFloor() {
        return floor;
    }

    public String getLogoUrl() {
        return logoPic;
    }
    public boolean isAddedToList() {
        return isAddedToList;
    }

    public void setAddedToList(boolean addedToList) {
        isAddedToList = addedToList;
    }

    public String getLogoPic() {
        return logoPic;
    }

    public String getStoreType() {
        return storeType;
    }

    @NonNull
    public String getStorename() {
        return storename;
    }

    public String getWorkingHours() {
        return workingHours;
    }
    public boolean isOpen(){
        return true;
    }
    public boolean isFavorite(){
        return this.isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(storename, store.getStoreName());
    }
    @Override
    public int hashCode() {
        return Objects.hash(storename);
    }

    protected FavoriteStore(Parcel in) {
        storename = in.readString();
        storeType = in.readString();
        floor = in.readString();
        logoPic = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Store> CREATOR = new Parcelable.Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(storename);
        dest.writeString(storeType);
        dest.writeString(floor);
        dest.writeString(logoPic);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}
