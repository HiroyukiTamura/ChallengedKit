/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupportlibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cks.hiroyuki2.worksupportlibrary.Entity.Content;
import com.cks.hiroyuki2.worksupportlibrary.Entity.Group;
import com.cks.hiroyuki2.worksupportlibrary.Entity.User;
import com.google.firebase.database.DataSnapshot;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.cks.hiroyuki2.worksupportlibrary.Util.DEFAULT;
import static com.cks.hiroyuki2.worksupportlibrary.Util.logStackTrace;

/**
 * 友人リストをローカルで保持するファイルを編集するおじさん！
 */

public class FriendJsonEditor {

    private static final String TAG = "MANUAL_TAG: " + FriendJsonEditor.class.getSimpleName();
    private final static String FRIEND_FILE = "friend.json";
    private final static String FRIEND_FILE_CHARSET = "UTF-8";
    private final static String PREF_FILE = "friend_pref";
    private final static String GROUP_PREF_FILE = "group_pref";
    private final static String KEY_USER_UID_SET = "KEY_USER_UID_SET";

    public static boolean dataSnap2JsonFile(Context context, @NonNull DataSnapshot dataSnapshot) {
        JSONObject obj = new JSONObject();
        for (DataSnapshot snap : dataSnapshot.getChildren()) {
            try {
                obj.put(snap.getKey(), snap.getValue());
            } catch (JSONException e) {
                logStackTrace(e);
                return false;
            }
        }

        try {
            FileOutputStream fos = context.openFileOutput(FRIEND_FILE, Context.MODE_PRIVATE);
            fos.write(obj.toString().getBytes(FRIEND_FILE_CHARSET));
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            logStackTrace(e);
        } catch (UnsupportedEncodingException e) {
            logStackTrace(e);
        } catch (IOException e) {
            logStackTrace(e);
        }
        return false;
    }

    /**
     * Jsonの構造に注意してください。今のところ、jsonは以下の通りです。
     * [
     * {"userUid" : "DEFAULT"
     * "name" : "DEFAULT"
     * "photoUrl" : "DEFAULT"},
     * ...
     * ]
     */
    public static boolean initDefaultJsonFile(Context context) {
        try {
            JSONObject jo = new JSONObject();
            JSONObject params = new JSONObject();
            params.put("name", DEFAULT);
            params.put("photoUrl", DEFAULT);
            jo.put(DEFAULT, params);
            JSONObject friend = new JSONObject();
            friend.put("friend", jo);
            BufferedOutputStream bos = new BufferedOutputStream(context.openFileOutput(FRIEND_FILE, Context.MODE_PRIVATE));
            bos.write(friend.toString().getBytes(FRIEND_FILE_CHARSET));
            bos.close();
            return true;

        } catch (JSONException | FileNotFoundException | UnsupportedEncodingException e) {
            logStackTrace(e);
        } catch (IOException e) {
            logStackTrace(e);
        }
        return false;
    }

    /**
     * @return Exceptionまたはユーザが0の場合、空を返します。
     */
    @NonNull
    public static List<User> json2UserList(@NonNull JSONArray ja) {
        List<User> list = new ArrayList<>(ja.length());
        for (int i = 0; i < ja.length(); i++) {
            try {
                JSONObject jo = ja.getJSONObject(i);
                list.add(new User(jo.getString("userUid"), jo.getString("name"), jo.getString("photoUrl")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

//    @Nullable
//    static JSONObject readFriends(Context context){
//        JSONObject jo = null;
//        try {
//            InputStreamReader bis = new InputStreamReader(new BufferedInputStream(context.openFileInput(FRIEND_FILE)));
//            StringBuilder sb = new StringBuilder();
//            int ch;
//            while ((ch = bis.read()) != -1){
//                sb.append((char)ch);
//            }
//            bis.close();
//            jo = new JSONObject(sb.toString());
//        } catch (FileNotFoundException | JSONException e) {
//            Util.logStackTrace(e);
//            Util.onError(context, "FileNotFoundException", "FileNotFoundException");
//        } catch (IOException e){
//            Util.logStackTrace(e);
//            Util.onError(context, "IOException", "IOException");
//        }
//        return jo;
//    }

    /**
     * Jsonの構造に注意してください。今のところ、jsonは以下の通りです。
     * [
     * {"userId" : "DEFAULT"
     * "name" : "DEFAULT"
     * "photoUrl" : "DEFAULT"},
     * ...
     * ]
     *
     * @return 友達が一人もいない場合、またはエラー時、nullを返します。
     */
    @NonNull
    public static JSONArray readFriendPref(@NonNull Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String string = pref.getString("friend", null);
        if (string == null)
            return new JSONArray();

        try {
            return new JSONArray(string);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

//    static boolean addFriend(@NonNull Context context, @NonNull String userUid, @NonNull String name, @Nullable String photoUrl){
//
//        JSONObject jo = readFriends(context);
//        if (jo == null){
//            Util.onError(context, "jo == null", "jo == null");
//            return false;
//        }
//
//        try {
//            JSONObject friends = jo.getJSONObject("friend");
//            JSONObject params = new JSONObject();
//            jo.put("name", name);
//            jo.put("photoUrl", photoUrl);
//            friends.put(userUid, params);
//            BufferedWriter bos = new BufferedWriter (new OutputStreamWriter(context.openFileOutput(FRIEND_FILE, Context.MODE_PRIVATE)));
//            bos.write(friends.toString());
//            bos.flush();
//            bos.close();
//            return true;
//
//        } catch (JSONException | UnsupportedEncodingException | FileNotFoundException e){
//            Util.logStackTrace(e);
//            Util.onError(context, "JSONException UnsupportedEncodingException FileNotFoundException", "JSONException UnsupportedEncodingException FileNotFoundException");
//        } catch (IOException e){
//            Util.logStackTrace(e);
//            Util.onError(context, "IOException", "IOException");
//        }
//        return false;
//    }

    public static boolean addFriendPref(@NonNull Context context, @NonNull String userUid, @NonNull String name, @Nullable String photoUrl) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("userUid", userUid);
            jo.put("name", name);
            jo.put("photoUrl", photoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        JSONArray ja = readFriendPref(context);
        String newStr = ja.put(jo).toString();

        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("friend", newStr);
        editor.apply();

        return true;
    }

    public static void writeFriendPref(@NonNull Context context, @Nullable String content) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("friend", content);
        editor.apply();
    }

    public static void writeGroupKeys(@NonNull Context context, @Nullable List<String> groupKeys) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Set<String> set = null;
        if (groupKeys != null)
            set = new HashSet<>(groupKeys);
        editor.putStringSet("groupKeys", set);
        editor.apply();
    }

//    static boolean readGroupPref(@NonNull Context context, @NonNull List<String> uidList){
//        SharedPreferences pref = context.getSharedPreferences(GROUP_PREF_FILE, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.apply();
//    }

    /**
     * jsonの構造に注意してください。今のところ、jsonの構造は以下の通りです。
     * [
     * {
     * "groupName":"DEFAULT"
     * "groupKey":"DEFAULT"
     * "added": false
     * },
     * ...
     * ]
     */
    @Nullable
    public static JSONArray readGroupPref(@NonNull Context context) {
        SharedPreferences pref = context.getSharedPreferences(GROUP_PREF_FILE, Context.MODE_PRIVATE);
        String string = pref.getString("group", null);
        if (string == null) return null;
        JSONArray ja = null;
        try {
            ja = new JSONArray(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    public static boolean addGroupPref(@NonNull Context context, @NonNull List<String> uidList, @NonNull String groupName, @NonNull String groupKey) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("groupName", groupName);
            jo.put("groupKey", groupKey);
//            jo.put("photoUrl", photoUrl);
            JSONArray jArr = new JSONArray();
            for (String uid : uidList)
                jArr.put(uid);
            jo.put("uidList", jArr);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        JSONArray ja = readGroupPref(context);
        if (ja == null) return false;
        ja.put(jo);

        SharedPreferences pref = context.getSharedPreferences(GROUP_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("group", ja.toString());
        editor.apply();

        return true;
    }

    public static void writeGroup(Context context, @NonNull String groupKey, @NonNull JSONObject jo) {
        SharedPreferences pref = context.getSharedPreferences(GROUP_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(groupKey, jo.toString());
        editor.apply();
    }

    /**
     * Groupクラス、Firebaseのnodeとはちょっと構造が違う。(なぜ?笑)
     *
     * @return emptyではありえない
     */
    @Nullable
    public static JSONObject snap2Json(@Nullable DataSnapshot snapshot) {
        if (snapshot == null || !snapshot.exists())
            return null;

        //このチェック不必要では?
        if (!(snapshot.hasChild("groupName") && snapshot.hasChild("host") && snapshot.hasChild("member") && snapshot.hasChild("photoUrl"))) {
            return null;
        }

        JSONObject jo = new JSONObject();
        String groupName = (String) snapshot.child("groupName").getValue();
        String host = (String) snapshot.child("host").getValue();
        String photoUrlG = (String) snapshot.child("photoUrl").getValue();
        try {
            jo.put("groupName", groupName);
            jo.put("host", host);
            jo.put("photoUrl", photoUrlG);

            JSONArray ja = new JSONArray();
            for (DataSnapshot snap : snapshot.child("member").getChildren()) {
                String userUid = snap.getKey();
                if (userUid.equals(DEFAULT)) continue;

                String name = (String) snap.child("name").getValue();
                String photoUrl = (String) snap.child("photoUrl").getValue();
                boolean added = (boolean) snap.child("isChecked").getValue();

                JSONObject userJo = new JSONObject();
                userJo.put("userUid", userUid);
                userJo.put("name", name);
                userJo.put("isChecked", added);
                userJo.put("photoUrl", photoUrl);

                ja.put(userJo);
            }
            jo.put("member", ja);

            jo.put("contents", makeContentsJoFromSnap(snapshot));

        } catch (JSONException e) {
            logStackTrace(e);
            return null;
        }

        return jo;
    }

    @Nullable
    static private JSONArray makeContentsJoFromSnap(DataSnapshot snap) throws JSONException {
        if (!snap.hasChild("contents"))
            return null;

        JSONArray ja = new JSONArray();
        for (DataSnapshot child : snap.child("contents").getChildren()) {
            String contentsKey = child.getKey();
            String contentName = (String) child.child("contentName").getValue();
            String lastEdit = (String) child.child("lastEdit").getValue();
            String lastEditor = (String) child.child("lastEditor").getValue();
            String whose = (String) child.child("whose").getValue();
            String type = (String) child.child("type").getValue();
            String comment = (String) child.child("comment").getValue();

            JSONObject joC = new JSONObject();
            joC.put("contentName", contentName);
            joC.put("lastEdit", lastEdit);
            joC.put("lastEditor", lastEditor);
            joC.put("whose", whose);
            joC.put("type", type);
            joC.put("contentsKey", contentsKey);
            joC.put("comment", comment);

            ja.put(joC);
        }

        return ja;
    }

    /**
     * @return 空でありうる
     */
    @NonNull
    public static List<Group> readAllJsonGroup(Context context) {
        SharedPreferences pref = context.getSharedPreferences(GROUP_PREF_FILE, Context.MODE_PRIVATE);
        List<Group> groupList = new ArrayList<>();
        try {
            for (String groupKey : pref.getAll().keySet()) {
                String val = pref.getString(groupKey, null);
                if (val == null) continue;

                Group group = getOneGroupFromJson(new JSONObject(val), groupKey);
                if (group == null) continue;

                groupList.add(group);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            logStackTrace(e);
        }

        return groupList;
    }

    @Nullable
    public static Group getOneGroup(Context context, @NonNull String groupKey) {
        SharedPreferences pref = context.getSharedPreferences(GROUP_PREF_FILE, Context.MODE_PRIVATE);
        String joStr = pref.getString(groupKey, null);
        try {
            return getOneGroupFromJson(new JSONObject(joStr), groupKey);
        } catch (JSONException e) {
            e.printStackTrace();
            logStackTrace(e);
            return null;
        }

    }

    @Nullable
    public static Group getOneGroupFromJson(@Nullable JSONObject jo, @NonNull String groupKey) throws JSONException {
        if (jo == null) return null;

        String groupName = jo.getString("groupName");
        String host = jo.getString("host");
        String photoUrlGroup =
                jo.has("photoUrl") || !jo.isNull("photoUrl")
                ? jo.getString("photoUrl")
                : null;

        JSONArray memberJa = jo.getJSONArray("member");
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < memberJa.length(); i++) {
            JSONObject memberJo = (JSONObject) memberJa.get(i);
            String userUid = memberJo.getString("userUid");
            String name = memberJo.getString("name");
            String photoUrl = memberJo.getString("photoUrl");
            boolean added = memberJo.getBoolean("isChecked");
            userList.add(new User(userUid, name, photoUrl, added));
        }

//        JSONArray jaC = jo.getJSONArray("contents");
        JSONArray jaC = jo.has("contents")
                ? jo.getJSONArray("contents")
                : new JSONArray();
        List<Content> contentList = new ArrayList<>();
//        if (jaC == null || jaC.length() == 0)
//            return null;

        for (int i = 0; i < jaC.length(); i++) {
            JSONObject contentJo = jaC.getJSONObject(i);
            String contentName = contentJo.getString("contentName");
            String lastEdit = contentJo.getString("lastEdit");
            String lastEditor = contentJo.getString("lastEditor");
            String whose = contentJo.getString("whose");
            String type = contentJo.getString("type");
            String contentsKey = contentJo.getString("contentsKey");
            String comment;
            if (contentJo.isNull("comment"))
                comment = null;
            else
                comment = contentJo.getString("comment");
            contentList.add(new Content(contentsKey, contentName, lastEdit, lastEditor, whose, type, comment));
        }

        return new Group(userList, groupName, groupKey, contentList, host, photoUrlGroup);
    }

    public static boolean readGroupExsit(Context context, @NonNull String groupKey) {
        SharedPreferences pref = context.getSharedPreferences(GROUP_PREF_FILE, Context.MODE_PRIVATE);

        for (String key : pref.getAll().keySet())
            if (groupKey.equals(key))
                return true;

        return false;
    }

    /**
     * @return 空でありうる
     */
    @NonNull
    static TreeSet<String> getGroupKeys(Context context) {
        SharedPreferences pref = context.getSharedPreferences(GROUP_PREF_FILE, Context.MODE_PRIVATE);
        return (TreeSet<String>) pref.getAll().keySet();
    }

    @NonNull
    static List<User> generateUserList(Context context) {
        JSONArray ja = readFriendPref(context);
        return json2UserList(ja);
    }
}
