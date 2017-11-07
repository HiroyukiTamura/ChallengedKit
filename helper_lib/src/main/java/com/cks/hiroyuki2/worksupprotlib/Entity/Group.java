/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib.Entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.Util;
import com.google.firebase.database.DataSnapshot;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.cks.hiroyuki2.worksupport3.FriendJsonEditor.getOneGroupFromJson;
import static com.cks.hiroyuki2.worksupport3.FriendJsonEditor.snap2Json;
import static com.cks.hiroyuki2.worksupport3.Util.logStackTrace;

/**
 * Fbの大きなノードのひとつGroupを一手に扱う巨大なクラス。このクラスは、{@link User}や{@link Content}から構成されます。
 * このクラスはFbの書き込み/読み込みに直接は使いません。
 */

public class Group implements Serializable {

    private static final String TAG = "MANUAL_TAG: " + Group.class.getSimpleName();
    public List<User> userList;
    public String groupName;
    public String groupKey;
    public List<Content> contentList;
    public String host;
    public String photoUrl;

    public Group(@NonNull List<User> userList, @NonNull String groupName, @NonNull String groupKey, @Nullable List<Content> contentList, @NonNull String host, @Nullable String photoUrl) {
        this.userList = userList;
        this.groupName = groupName;
        this.groupKey = groupKey;
        this.contentList =
                contentList == null ?
                        new ArrayList<Content>() :
                        contentList;
        this.host = host;
        this.photoUrl =
                photoUrl == null?
                        "null":
                        photoUrl;
    }


    @Contract("_, null, _ -> null")
    @Nullable
    public  static Group makeGroupFromSnap(@NonNull Context context, @Nullable DataSnapshot dataSnapshot, @NonNull String groupKey){
        Group group;

        if (dataSnapshot == null || !dataSnapshot.exists())
            return null;

        JSONObject jo = snap2Json(dataSnapshot);
        if (jo == null){
            Util.onError(context, "jo == null", R.string.error);
            return null;
        }

        try {
            group = getOneGroupFromJson(jo, groupKey);
            if (group != null){
                if (group.contentList == null)
                    group.contentList = new ArrayList<>();
            }
        } catch (JSONException e) {
            logStackTrace(e);
            Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show();
            return null;
        }

        return group;
    }
}
