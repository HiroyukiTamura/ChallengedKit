/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib.Entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

/**
 * ソーシャルまわりでユーザを持つ。
 */

public class User implements Parcelable, Serializable {
    public String name;
    public String photoUrl;
    @Exclude public String userUid;
    public boolean isChecked;

    User(){
        //Firebaseで使用
    }

    public User(@Nullable String userUid, @Nullable String name, @Nullable String photoUrl){
        this(userUid, name, photoUrl, false);
    }

    public User(@Nullable String userUid, @Nullable String name, @Nullable String photoUrl, boolean isChecked){
        this.userUid = userUid;
        this.name = name;
        this.photoUrl = photoUrl;
        this.isChecked = isChecked;
    }

    public User(@NonNull FirebaseUser user){
        this.userUid = user.getUid();
        this.photoUrl = user.getPhotoUrl() == null ?
                "null" : user.getPhotoUrl().toString();
        this.name = user.getDisplayName();
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    @Exclude
    public String getUserUid() {
        return userUid;
    }

    public boolean getIsChecked(){
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Exclude
    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Exclude
    public int describeContents() {
        return 0;
    }

    @Exclude
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(photoUrl);
        out.writeString(userUid);
        out.writeString(Boolean.toString(isChecked));
    }

    @Exclude
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Contract("_ -> !null")
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Contract(value = "_ -> !null", pure = true)
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        name = in.readString();
        photoUrl = in.readString();
        userUid = in.readString();
        isChecked = Boolean.parseBoolean(in.readString());
    }

    @Exclude
    @Contract("null -> null")
    @Nullable
    public static User makeUserFromSnap(@Nullable DataSnapshot snap){
        if (snap == null)
            return null;
        String key = snap.getKey();
        User user = snap.getValue(User.class);
        if (user != null)
            user.setUserUid(key);
        return user;
    }
}
