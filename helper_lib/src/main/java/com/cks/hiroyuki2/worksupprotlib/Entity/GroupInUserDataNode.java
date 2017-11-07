/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib.Entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

/**
 * FirebaseのuserDataノードの子孫に、そのユーザが参加/未参加のGroupを表す子孫があります。この子孫のSnapshotを扱うオブジェクトです。
 * {@link Group}
 */

public class GroupInUserDataNode implements Serializable, Parcelable {
    private static final String TAG = "MANUAL_TAG: " + GroupInUserDataNode.class.getSimpleName();
    public String name;
    @Exclude
    public String groupKey;
    public String photoUrl;
    public boolean added;

    public GroupInUserDataNode(){}

    public GroupInUserDataNode(@NonNull String name, @NonNull String groupKey, @Nullable String photoUrl, boolean added){
        this.name = name;
        this.groupKey = groupKey;
        this.photoUrl = photoUrl;
        this.added = added;
    }

    @Override @Exclude
    public int describeContents() {
        return 0;
    }

    @Override @Exclude
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.groupKey);
        dest.writeString(this.photoUrl);
        dest.writeByte(this.added ? (byte) 1 : (byte) 0);
    }

    protected GroupInUserDataNode(Parcel in) {
        this.name = in.readString();
        this.groupKey = in.readString();
        this.photoUrl = in.readString();
        this.added = in.readByte() != 0;
    }

    @Exclude
    public static final Creator<GroupInUserDataNode> CREATOR = new Creator<GroupInUserDataNode>() {
        @Override @Contract("_ -> !null")
        public GroupInUserDataNode createFromParcel(Parcel source) {
            return new GroupInUserDataNode(source);
        }

        @Override @Contract(value = "_ -> !null", pure = true)
        public GroupInUserDataNode[] newArray(int size) {
            return new GroupInUserDataNode[size];
        }
    };
}
