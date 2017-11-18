/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.example.hiroyuki3.worksupportlibw.Adapters.GroupSettingRVAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.cks.hiroyuki2.worksupport3.DialogFragments.RecordDialogFragment.WITCH_CLICKED;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickInputDialog;
import static com.cks.hiroyuki2.worksupport3.Util.OLD_GRP_NAME;
import static com.cks.hiroyuki2.worksupport3.Util.showCompleteNtf;
import static com.cks.hiroyuki2.worksupport3.Util.showUploadingNtf;
import static com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter.CODE_UPDATE_CHILDREN;
import com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRootRef;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.LIMIT_SIZE_PROF;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.isOverSize;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.uploadFile;
import static com.cks.hiroyuki2.worksupport3.Fragments.AddGroupFragment.REQ_CODE_ICON;
import static com.cks.hiroyuki2.worksupport3.DialogFragments.RecordDialogFragmentInput.INPUT;
import static com.cks.hiroyuki2.worksupprotlib.Util.UID;
import static com.cks.hiroyuki2.worksupprotlib.Util.getExtension;
import static com.cks.hiroyuki2.worksupprotlib.Util.getUserMe;
import static com.cks.hiroyuki2.worksupprotlib.Util.kickIntentIcon;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;
import static com.cks.hiroyuki2.worksupprotlib.Util.makeScheme;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import static com.cks.hiroyuki2.worksupprotlib.Util.setNullableText;
import static com.cks.hiroyuki2.worksupprotlib.Util.toastNullable;
import static com.example.hiroyuki3.worksupportlibw.Adapters.GroupSettingRVAdapter.CALLBACK_CLICK_GROUP_MEMBER;
import static com.example.hiroyuki3.worksupportlibw.Adapters.GroupSettingRVAdapter.CLICK_GROUP_MEMBER;
import static com.example.hiroyuki3.worksupportlibw.Adapters.GroupSettingRVAdapter.USER;

/**
 * このクラスは{@link com.cks.hiroyuki2.worksupport3.Activities.AddGroupActivity}と酷似しています。ヘッダ部分のロジックとレイアウトを合わせてFragmentとして切り出してもいいかもしれません。
 * それかCustomViewもいいですね。ある程度実装が固まってから、リファクタリングするかどうか検討しましょう。
 * layoutをincludeしているので、AndroidAnnotationがうまくいかない。
 */
public class GroupSettingFragment extends Fragment implements Callback, OnFailureListener, GroupSettingRVAdapter.IGroupSettingRVAdapter {

    private static final String TAG = "MANUAL_TAG: " + GroupSettingFragment.class.getSimpleName();
    public final static String GROUP = "group";
    public static final String SET_TAG_MK_GROUP = "SET_TAG_MK_GROUP";
    public static final int CALLBACK_SET_TAG_MK_GROUP = 17854;
    public static final String TAG_EXIT_GROUP = "TAG_EXIT_GROUP";
    public static final int CALLBACK_EXIT_GROUP = 17856;
    public static final String INTENT_EXIT = "INTENT_EXIT";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UPDATE_CODE_NAME, UPDATE_CODE_PHOTO_URL})
    @interface updateCode {}
    private static final int UPDATE_CODE_NAME = 0;
    private static final int UPDATE_CODE_PHOTO_URL = 1;

    private Unbinder unbinder;
    private Group group;
    private GroupSettingRVAdapter adapter;
    private FirebaseStorageUtil storageUtil;
    @BindView(R.id.icon) CircleImageView icon;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.def_icon) ImageView defIcon;
    @BindView(R.id.def_icon_fl) FrameLayout defIconContainer;
    @BindView(R.id.rv) RecyclerView rv;

    public static GroupSettingFragment newInstance(@NonNull Group group) {
        GroupSettingFragment fragment = new GroupSettingFragment();
        Bundle args = new Bundle();
        args.putSerializable(GROUP, group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = (Group) getArguments().getSerializable(GROUP);
            storageUtil = new FirebaseStorageUtil(getContext(), group);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_setting, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setNullableText(name, group.groupName);
        defIcon.setImageResource(R.drawable.ic_group_white_24dp);
        Picasso.with(getContext())/*もともとはデフォルトの画像が挿入されていて、もし画像取得ができれば、デフォルトのImageView手前にあるImageViewに描画し、デフォルトのImageViewを隠す。*/
                .load(group.photoUrl)
                .into(icon, this);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupSettingRVAdapter(this, group.userList, getUserMe());
        rv.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onSuccess() {
        defIconContainer.setVisibility(GONE);
    }

    @Override
    public void onError() {
        //デフォルト画像が表示されたままなので、特になにもしない
    }

    @OnClick(R.id.item_exit)
    public void onClickItemExit() {
        Bundle bundle = new Bundle();
        bundle.putString("from", TAG_EXIT_GROUP);
        bundle.putString(GROUP, group.groupName);
        kickDialogInOnClick(TAG_EXIT_GROUP, CALLBACK_EXIT_GROUP, bundle, this);
    }

    @OnClick(R.id.item_invite)
    public void onClickInvite(){

    }

    @OnClick(R.id.icon_fl)
    public void onClickIcon() {
        kickIntentIcon(this, REQ_CODE_ICON);
    }

    @OnClick({R.id.edit_name, R.id.name})
    void onClickEditBtn() {
        String string = (String) name.getText();
        Bundle bundle = new Bundle();
        bundle.putString(OLD_GRP_NAME, group.groupName);
        kickInputDialog(bundle , SET_TAG_MK_GROUP, CALLBACK_SET_TAG_MK_GROUP, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * AndroidAnnotations導入してないので@OnActivityResult使えません。
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == REQ_CODE_ICON && resultCode == RESULT_OK){
            final Uri uri = data.getData();
            if (isOverSize(uri, LIMIT_SIZE_PROF)){
                Toast.makeText(getContext(), R.string.over_size_err, Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(getContext(), "アップロードしています...", Toast.LENGTH_LONG).show();
            String type = getExtension(getContext(), uri);
            String key = getRef("keyPusher").push().getKey();
            final String fileName = key + "." + type;

            final int ntfId = (int) System.currentTimeMillis();
            uploadFile("keyPusher/" + fileName, uri, this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri uri = taskSnapshot.getDownloadUrl();
                    group.photoUrl = uri.toString();/*Firebaseの仕様上NPEはあり得ないので、you can ignore this warning*/
                    Picasso.with(getContext())
                            .load(uri)
                            .into(icon, GroupSettingFragment.this);
                    updateValue(UPDATE_CODE_PHOTO_URL, group.photoUrl, ntfId);
                }
            }, storageUtil, new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    showUploadingNtf(taskSnapshot, fileName, ntfId);
                }
            });
        } else if (requestCode == CALLBACK_EXIT_GROUP && resultCode == RESULT_OK){
            Intent i = getActivity().getIntent();
            i.putExtra(INTENT_EXIT, true);
            getActivity().setResult(RESULT_OK);
            getActivity().finish();
        } else if (requestCode == CALLBACK_SET_TAG_MK_GROUP && resultCode == RESULT_OK) {
            String input = data.getStringExtra(INPUT);
            group.groupName = input;
            name.setText(input);
            updateValue(UPDATE_CODE_NAME, input, 0);
        } else if (requestCode == CALLBACK_CLICK_GROUP_MEMBER && resultCode == RESULT_OK) {
            int witch = data.getIntExtra(WITCH_CLICKED, Integer.MAX_VALUE);
            if (witch == R.id.register_user){
                /*onResultRegistUser(data);*/
                /*今はユーザ追加は許していない*/
            } else if (witch == R.id.item_remove){
                onResultRemoveMember(data);
            } else {
                Util.onError(this, "witch == Integer.MAX_VALUE", R.string.error);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * {@link SocialFragment}と共通化できる。
     */
    private void onResultRegistUser(Intent data){
        User newFriend = (User)data.getSerializableExtra(USER);
        final FirebaseUser userMe = Util.getUserMe();
        if (userMe == null){
            Util.onError(this, "FirebaseAuth.getInstance().getCurrentUser() == null", R.string.error);
            return;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("/"+ userMe.getUid() +"/"+ newFriend.getUserUid() +"/name", newFriend.getName());
        hashMap.put("/"+ userMe.getUid() +"/"+ newFriend.getUserUid() +"/photoUrl", newFriend.getPhotoUrl());
        hashMap.put("/"+ newFriend.getUserUid() + "/" + userMe.getUid() + "/name", userMe.getDisplayName());
        String myPhotoUrl = "null";
        if (userMe.getPhotoUrl() != null){
            myPhotoUrl = userMe.getPhotoUrl().toString();
        }
        hashMap.put("/"+ newFriend.getUserUid() + "/" + userMe.getUid() + "/photoUrl", myPhotoUrl);
        getRef("friend").updateChildren(hashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null)
                    Util.onError(GroupSettingFragment.this, TAG+databaseError.getDetails(), R.string.error);
                else {
                    toastNullable(getContext(), "ユーザ登録しました");
                }
            }
        });
    }

    private void onResultRemoveMember(Intent data){
        User user = (User) data.getSerializableExtra(USER);
        if (user == null){
            Util.onError(this, TAG+"uid == null", R.string.error);
            return;
        }

        String uid = user.getUserUid();
        final int pos = adapter.getPosFromUid(uid);
        if (pos == Integer.MAX_VALUE){
            Util.onError(this, TAG+"pos == Integer.MAX_VALUE", R.string.error);
            return;
        }

        adapter.removeMember(pos);
        DatabaseReference checkRef = getRef("group", group.groupKey);
        HashMap<String, Object> children = new HashMap<>();
        children.put(makeScheme("group", group.groupKey, "member", uid), null);
        children.put(makeScheme("userData", uid, "group", group.groupKey), null);
        new FbCheckAndWriter(checkRef, getRootRef(), getContext(), children) {
            @Override
            public void onSuccess(DatabaseReference ref) {
                String name = adapter.getUser(pos).getName();
                toastNullable(getContext(), "「"+ name + "」さんをグループから削除しました");
            }
        }.update(CODE_UPDATE_CHILDREN);
    }

    private void updateValue(@updateCode final int code, String value, /*UPDATE_CODE_PHOTO_URLでのみ使用*/final int ntfId){
        HashMap<String, Object> hashMap = new HashMap<>();
        switch (code) {
            case UPDATE_CODE_NAME:{
                hashMap.put(makeScheme("group", group.groupKey, "groupName"), value);
                for (User user: group.userList) {
                    hashMap.put(makeScheme("userData", user.getUserUid(), "group", group.groupKey, "name"), value);
                }
                break;}
            case UPDATE_CODE_PHOTO_URL:{
                hashMap.put(makeScheme("group", group.groupKey, "photoUrl"), value);
                for (User user: group.userList) {
                    hashMap.put(makeScheme("userData", user.getUserUid(), "group", group.groupKey, "photoUrl"), value);
                }
                break;}
        }
        DatabaseReference ref = getRef("group", group.groupKey);
        FbCheckAndWriter writer = new FbCheckAndWriter(ref, getRootRef(), getContext(), hashMap) {
            @Override
            public void onSuccess(DatabaseReference ref) {
                switch (code){
                    case UPDATE_CODE_NAME:
                        toastNullable(getContext(), R.string.updated_group_name);
                        break;
                    case UPDATE_CODE_PHOTO_URL:
                        showCompleteNtf(group.groupName, ntfId, R.string.ntf_txt_change_group_img);
                        Picasso.with(getContext())/*もともとはデフォルトの画像が挿入されていて、もし画像取得ができれば、デフォルトのImageView手前にあるImageViewに描画し、デフォルトのImageViewを隠す。*/
                                .load(group.photoUrl)
                                .into(icon, GroupSettingFragment.this);
                        break;
                }
            }
        };
        writer.update(CODE_UPDATE_CHILDREN);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        logStackTrace(e);
        toastNullable(getContext(), R.string.error);
    }

    //region GroupSettingRVAdapter.IGroupSettingRVAdapter
//    @Override
//    public void onClickRemoveMe() {
//        onClickItemExit();
//    }

    @Override
    public void onClickGroupMember(Bundle bundle) {
        kickDialogInOnClick(CLICK_GROUP_MEMBER, CALLBACK_CLICK_GROUP_MEMBER, bundle, this);
    }
    //endregion
}
