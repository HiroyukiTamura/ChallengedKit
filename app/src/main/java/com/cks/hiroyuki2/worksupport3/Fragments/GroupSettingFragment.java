/*
 * Copyright 2017 Hiroyuki Tamura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.FbIntentService_;
import com.cks.hiroyuki2.worksupport3.RxBus;
import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.cks.hiroyuki2.worksupprotlib.Entity.GroupInUserDataNode;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.RxMsg.RxMsgForUpdateGroupIcon;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.example.hiroyuki3.worksupportlibw.Adapters.GroupSettingRVAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.cks.hiroyuki2.worksupport3.Activities.AddGroupActivity.KEY_PARCELABLE;
import static com.cks.hiroyuki2.worksupport3.Activities.AddGroupActivity.REQ_CODE_ADD_GROUP_MEMBER;
import static com.cks.hiroyuki2.worksupport3.DialogFragments.RecordDialogFragment.WITCH_CLICKED;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickInputDialog;
import static com.cks.hiroyuki2.worksupport3.Util.OLD_GRP_NAME;
import static com.cks.hiroyuki2.worksupprotlib.Entity.User.makeUserFromSnap;
import com.trello.rxlifecycle2.components.support.RxFragment;

import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.LIMIT_SIZE_PROF;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.isOverSize;
import static com.cks.hiroyuki2.worksupport3.Fragments.AddGroupFragment.REQ_CODE_ICON;
import static com.cks.hiroyuki2.worksupport3.DialogFragments.RecordDialogFragmentInput.INPUT;
import static com.cks.hiroyuki2.worksupprotlib.Util.DEFAULT;
import static com.cks.hiroyuki2.worksupprotlib.Util.getPosFromUid;
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
public class GroupSettingFragment extends RxFragment implements Callback, OnFailureListener, GroupSettingRVAdapter.IGroupSettingRVAdapter {

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

        RxBus.subscribe(RxBus.UPDATE_GROUP_NAME, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                String groupKeyMsg = (String)o;
                if (groupKeyMsg == group.groupKey)
                    toastNullable(getContext(), R.string.updated_group_name);
            }
        });

        RxBus.subscribe(RxBus.UPDATE_GROUP_PHOTO, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                RxMsgForUpdateGroupIcon msg = (RxMsgForUpdateGroupIcon)o;
                if (msg.getGroupKey() == group.groupKey)
                    return;
                group.photoUrl = msg.getDownloadUrl().toString();
                Picasso.with(getContext())
                        .load(msg.getDownloadUrl())
                        .into(icon, GroupSettingFragment.this);
            }
        });

        RxBus.subscribe(RxBus.REMOVE_MEMBER, this, new Consumer<Object>() {
            @Override
            public void accept(Object name) throws Exception {
//                Log.d(TAG, "accept: "+ uid.toString());
//                String name = adapter.getUser(adapter.getPosFromUid((String) uid)).getName();
                toastNullable(getContext(), "「"+ (String) name + "」さんをグループから削除しました");
            }
        });
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
        bundle.putSerializable(GROUP, group);
        kickDialogInOnClick(TAG_EXIT_GROUP, CALLBACK_EXIT_GROUP, bundle, this);
    }

    /**
     * 即時にリスナが走ってほしいので、IntentServiceは使わない。
     * また、画面回転等を超えてリスナが走る必要もないのでServiceにも投げない。
     * よってfragment内でobserberパターンを走らせる。
     */
    @OnClick(R.id.item_invite)
    public void onClickInvite(){
        String uid = FirebaseAuth.getInstance().getUid();

        Single.create(new SingleOnSubscribe<DataSnapshot>() {
            @Override
            public void subscribe(SingleEmitter<DataSnapshot> emitter) throws Exception {
                getRef("friend", uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                emitter.onSuccess(dataSnapshot);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                emitter.onError(databaseError.toException());
                            }
                        });
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindToLifecycle())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                if (dataSnapshot == null || !dataSnapshot.exists()){
                    Util.onError(GroupSettingFragment.this, "dataSnapshot == null || !dataSnapshot.exists()", R.string.error);
                } else {
                    List<User> userList = new ArrayList<>();
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        if (child.getKey().equals(DEFAULT))
                            continue;
                        User user = makeUserFromSnap(child);
                        int pos = getPosFromUid(group.userList, user.getUserUid());
                        if (pos != Integer.MAX_VALUE)
                            continue;

                        userList.add(user);
                    }

                    if (userList.isEmpty()){
                        toastNullable(getContext(), R.string.grp_no_addable_member);
                        return;
                    }

                    com.cks.hiroyuki2.worksupport3.Activities.AddGroupActivity_
                            .intent(GroupSettingFragment.this)
                            .userList((ArrayList<User>) userList)
                            .requestCode(REQ_CODE_ADD_GROUP_MEMBER)
                            .startForResult(REQ_CODE_ADD_GROUP_MEMBER);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Util.onError(GroupSettingFragment.this, TAG+throwable.getMessage(), R.string.error);
            }
        });

//        getRef("friend", getUserMe().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot == null || !dataSnapshot.exists()){
//                    Util.onError(GroupSettingFragment.this, "dataSnapshot == null || !dataSnapshot.exists()", R.string.error);
//                } else {
//                    List<User> userList = new ArrayList<>();
//                    for (DataSnapshot child: dataSnapshot.getChildren()) {
//                        if (child.getKey().equals(DEFAULT))
//                            continue;
//                        User user = makeUserFromSnap(child);
//                        int pos = getPosFromUid(group.userList, user.getUserUid());
//                        if (pos != Integer.MAX_VALUE)
//                            continue;
//
//                        userList.add(user);
//                    }
//
//                    if (userList.isEmpty()){
//                        toastNullable(getContext(), R.string.grp_no_addable_member);
//                        return;
//                    }
//
//                    com.cks.hiroyuki2.worksupport3.Activities.AddGroupActivity_
//                            .intent(GroupSettingFragment.this)
//                            .userList((ArrayList<User>) userList)
//                            .requestCode(REQ_CODE_ADD_GROUP_MEMBER)
//                            .startForResult(REQ_CODE_ADD_GROUP_MEMBER);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Util.onError(GroupSettingFragment.this, TAG+databaseError.getDetails(), R.string.error);
//            }
//        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.unregister(this);
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

            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null){
                Util.onError(this, "uid == null", R.string.error);
                return;
            }

            FbIntentService_.intent(getActivity().getApplication())
                    .updateGroupPhotoUrl(uid, group.groupKey, group.groupName, uri)
                    .start();

//            Toast.makeText(getContext(), "アップロードしています...", Toast.LENGTH_LONG).show();
//            String type = getExtension(getContext(), uri);
//            String key = getRef("keyPusher").push().getKey();
//            final String fileName = key + "." + type;
//
//            final int ntfId = (int) System.currentTimeMillis();
//            uploadFile("keyPusher/" + fileName, uri, this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Uri uri = taskSnapshot.getDownloadUrl();
//                    group.photoUrl = uri.toString();/*Firebaseの仕様上NPEはあり得ないので、you can ignore this warning*/
//                    Picasso.with(getContext())
//                            .load(uri)
//                            .into(icon, GroupSettingFragment.this);
//                    updateValue(UPDATE_CODE_PHOTO_URL, group.photoUrl, ntfId);
//                }
//            }, storageUtil, new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    showUploadingNtf(MainActivity.class, getContext(), taskSnapshot, fileName, ntfId);
//                }
//            });
        } else if (requestCode == CALLBACK_EXIT_GROUP && resultCode == RESULT_OK){
            getActivity().setResult(RESULT_OK, data);
            getActivity().finish();
        } else if (requestCode == CALLBACK_SET_TAG_MK_GROUP && resultCode == RESULT_OK) {
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null){
                Util.onError(this, "uid == null", R.string.error);
                return;
            }
            String input = data.getStringExtra(INPUT);
            group.groupName = input;
            name.setText(input);
            FbIntentService_.intent(getActivity().getApplication())
                    .updateGroupName(uid, group.groupKey, input)
                    .start();
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
        } else if (requestCode == REQ_CODE_ADD_GROUP_MEMBER && resultCode == RESULT_OK){
            onResultAddMember(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * {@link onClickInvite()}の流れで発火する。
     * これIntentServiceに移植するべきでは？
     */
    private void onResultAddMember(Intent data){
        final List<User> newMembers = data.getParcelableArrayListExtra(KEY_PARCELABLE);

        Single.create(new SingleOnSubscribe<DatabaseReference>() {
            @Override
            public void subscribe(SingleEmitter<DatabaseReference> emitter) throws Exception {
                getRef("group", group.groupKey, "member")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String uid = FirebaseAuth.getInstance().getUid();

                                if (dataSnapshot == null || !dataSnapshot.exists()){
                                    emitter.onError(new IllegalArgumentException("dataSnapshot == null || !dataSnapshot.exists() グループ消滅？"));
                                    return;
                                }

                                if (!dataSnapshot.hasChild(FirebaseAuth.getInstance().getUid())){
                                    emitter.onError(new IllegalArgumentException("!dataSnapshot.hasChild(FirebaseAuth.getInstance().getUid()) グループ脱退？"));
                                }

                                if (!dataSnapshot.child(uid).child("isChecked").getValue(Boolean.class)){
                                    //ここには来ないはず
                                    emitter.onError(new IllegalArgumentException("!dataSnapshot.child(uid).child(isChecked).getValue(Boolean.class) グループ未参加？"));
                                }

                                HashMap<String, Object> childMap = new HashMap<>();
                                for (User user: newMembers) {
                                    GroupInUserDataNode smGroup = new GroupInUserDataNode(group.groupName, group.groupKey, group.photoUrl, false);
                                    childMap.put(makeScheme("userData", user.getUserUid(), "group", group.groupKey), smGroup);
                                    childMap.put(makeScheme("group", group.groupKey, "member", user.getUserUid()), user);
                                }

                                getRef().updateChildren(childMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null){
                                            emitter.onError(databaseError.toException());
                                            return;
                                        }

                                        emitter.onSuccess(databaseReference);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                emitter.onError(databaseError.toException());
                            }
                        });
            }
        })
        .compose(bindToLifecycle())
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindToLifecycle())
        .subscribe(new Consumer<DatabaseReference>() {
            @Override
            public void accept(DatabaseReference databaseReference) throws Exception {
                toastNullable(getContext(), R.string.invite_done);
                group.userList.addAll(newMembers);
                adapter.notifyDataSetChanged();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Util.onError(GroupSettingFragment.this, TAG+throwable.getMessage(), R.string.error);
            }
        });
    }

    //region コメントアウトにつき消さないで
    /*---------コメントアウトにつき消さないで---------*/
//    /**
//     * {@link SocialFragment}と共通化できる。
//     */
//    private void onResultRegistUser(Intent data){
//        User newFriend = (User)data.getSerializableExtra(USER);
//        final FirebaseUser userMe = Util.getUserMe();
//        if (userMe == null){
//            Util.onError(this, "FirebaseAuth.getInstance().getCurrentUser() == null", R.string.error);
//            return;
//        }
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("/"+ userMe.getUid() +"/"+ newFriend.getUserUid() +"/name", newFriend.getName());
//        hashMap.put("/"+ userMe.getUid() +"/"+ newFriend.getUserUid() +"/photoUrl", newFriend.getPhotoUrl());
//        hashMap.put("/"+ newFriend.getUserUid() + "/" + userMe.getUid() + "/name", userMe.getDisplayName());
//        String myPhotoUrl = "null";
//        if (userMe.getPhotoUrl() != null){
//            myPhotoUrl = userMe.getPhotoUrl().toString();
//        }
//        hashMap.put("/"+ newFriend.getUserUid() + "/" + userMe.getUid() + "/photoUrl", myPhotoUrl);
//        getRef("friend").updateChildren(hashMap, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                if (databaseError != null)
//                    Util.onError(GroupSettingFragment.this, TAG+databaseError.getDetails(), R.string.error);
//                else {
//                    toastNullable(getContext(), "ユーザ登録しました");
//                }
//            }
//        });
//    }
    /*---------ここまで---------*/
    //endregion

    private void onResultRemoveMember(Intent data){
        User user = (User) data.getSerializableExtra(USER);
        if (user == null){
            Util.onError(this, TAG+"uid == null", R.string.error);
            return;
        }

        final String uid = user.getUserUid();
        final int pos = adapter.getPosFromUid(uid);
        if (pos == Integer.MAX_VALUE){
            Util.onError(this, TAG+"pos == Integer.MAX_VALUE", R.string.error);
            return;
        }

        adapter.removeMember(pos);

        FbIntentService_.intent(getActivity().getApplicationContext())
                .removeMember(group.groupKey, uid, user.getName())
                .start();
//        DatabaseReference checkRef = getRef("group", group.groupKey);
//        HashMap<String, Object> children = new HashMap<>();
//        children.put(makeScheme("group", group.groupKey, "member", uid), null);
//        children.put(makeScheme("userData", uid, "group", group.groupKey), null);
//        new FbCheckAndWriter(checkRef, getRootRef(), getContext(), children) {
//            @Override
//            public void onSuccess(DatabaseReference ref) {
//                String name = adapter.getUser(adapter.getPosFromUid(uid)).getName();
//                toastNullable(getContext(), "「"+ name + "」さんをグループから削除しました");
//            }
//        }.update(CODE_UPDATE_CHILDREN);
    }

//    private void updateValue(@updateCode final int code, final String value, /*UPDATE_CODE_PHOTO_URLでのみ使用*/final int ntfId){
//        FbIntentService_.intent(getActivity().getApplication())
//                .updateGroupName(group.groupKey, value)
//                .start();

//        HashMap<String, Object> hashMap = new HashMap<>();
//        switch (code) {
//            case UPDATE_CODE_NAME:{
//                hashMap.put(makeScheme("group", group.groupKey, "groupName"), value);
////                for (User user: group.userList) {
////                    hashMap.put(makeScheme("userData", user.getUserUid(), "group", group.groupKey, "name"), value);
////                }
//                break;}
//            case UPDATE_CODE_PHOTO_URL:{
//                hashMap.put(makeScheme("group", group.groupKey, "photoUrl"), value);
//                for (User user: group.userList) {
//                    hashMap.put(makeScheme("userData", user.getUserUid(), "group", group.groupKey, "photoUrl"), value);
//                }
//                break;}
//        }
//        DatabaseReference ref = getRef("group", group.groupKey);
//        FbCheckAndWriter writer = new FbCheckAndWriter(ref, getRootRef(), getContext(), hashMap) {
//            @Override
//            public void onSuccess(DatabaseReference ref) {
//                switch (code){
//                    case UPDATE_CODE_NAME:
//                        toastNullable(getContext(), R.string.updated_group_name);
////                        GroupSettingActivity activity = (GroupSettingActivity)getActivity();
////                        if (activity != null)
////                            activity.setNewGroupName(value);
//                        break;
//                    case UPDATE_CODE_PHOTO_URL:
//                        showCompleteNtf(MainActivity.class, getContext(), group.groupName, ntfId, R.string.ntf_txt_change_group_img);
//                        Picasso.with(getContext())/*もともとはデフォルトの画像が挿入されていて、もし画像取得ができれば、デフォルトのImageView手前にあるImageViewに描画し、デフォルトのImageViewを隠す。*/
//                                .load(group.photoUrl)
//                                .into(icon, GroupSettingFragment.this);
//                        break;
//                }
//            }
//        };
//        writer.update(CODE_UPDATE_CHILDREN);
//    }

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
