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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.Activities.AddFriendActivity;
import com.cks.hiroyuki2.worksupport3.Activities.AddGroupActivity;
import com.cks.hiroyuki2.worksupport3.Activities.MainActivity;
import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.cks.hiroyuki2.worksupprotlib.Entity.GroupInUserDataNode;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.DialogFragments.RecordDialogFragment;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.example.hiroyuki3.worksupportlibw.Adapters.SocialGroupListRVAdapter;
import com.example.hiroyuki3.worksupportlibw.Adapters.SocialListRVAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.view.View.VISIBLE;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;
import static com.cks.hiroyuki2.worksupport3.FbIntentService.PREF_KEY_ACCESS_SOCIAL;
import static com.cks.hiroyuki2.worksupprotlib.Entity.Group.makeGroupFromSnap;
import static com.cks.hiroyuki2.worksupprotlib.Entity.User.makeUserFromSnap;

import com.trello.rxlifecycle2.components.support.RxFragment;

import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRootRef;
import static com.cks.hiroyuki2.worksupprotlib.Util.DEFAULT;
import static com.cks.hiroyuki2.worksupprotlib.Util.PREF_NAME;
import static com.cks.hiroyuki2.worksupprotlib.Util.makeScheme;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import static com.cks.hiroyuki2.worksupprotlib.Util.setImgFromStorage;
import static com.cks.hiroyuki2.worksupprotlib.Util.toastNullable;
import static com.example.hiroyuki3.worksupportlibw.Adapters.SocialGroupListRVAdapter.CALLBACK_GROUP_NON_ADDED;
import static com.example.hiroyuki3.worksupportlibw.Adapters.SocialGroupListRVAdapter.GROUP;
import static com.example.hiroyuki3.worksupportlibw.Adapters.SocialGroupListRVAdapter.TAG_GROUP_NON_ADDED;
import static com.example.hiroyuki3.worksupportlibw.AdditionalUtil.CODE_SOCIAL_FRAG;

/**
 * SocialListRVAdapter.ISocialListRVAdapterはimplementしないでください。だって、SocialFragmentでグループ作成しないでしょ？
 */
@EFragment(R.layout.fragment_social2)
public class SocialFragment extends RxFragment implements SocialGroupListRVAdapter.ISocialGroupListRVAdapter {

    private static final String TAG = "MANUAL_TAG: " + SocialFragment.class.getSimpleName();
    public static final int REQ_CODE_CREATE_GROUP = 1632;

    @ViewById(R.id.name) TextView myNameTv;
    @ViewById(R.id.icon) CircleImageView myIconIv;
    @ViewById(R.id.recycler_user) RecyclerView rvUser;
    @ViewById(R.id.recycler_share) RecyclerView rvShare;
    @ViewById(R.id.scroll) ScrollView sv;
    @ViewById(R.id.maintain_rl) RelativeLayout maintain;
    SocialListRVAdapter userAdapter;
    SocialGroupListRVAdapter groupAdapter;
    private List<User> userList = new ArrayList<>();
    private List<GroupInUserDataNode> groupList;

    private IOnCompleteGroup mListener;
    private User me;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = new User(FirebaseAuth.getInstance().getCurrentUser());
//        userList = FriendJsonEditor.generateUserList(getContext());
//        groupList = generateGroupList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof IOnCompleteGroup) {
            mListener = (IOnCompleteGroup) context;
        }
    }

    @AfterViews
    void onAfterViews(){
//        switch (((MainActivity) context).socialDbState){
//            case UNKNOWN_STATE:
//                onError(this, TAG+"checkSocialState() == UNKNOWN_STATE", R.string.error);
//                break;
//            case REJECT_SOCIAL:
//                onOutOfService();
//                break;
//            case ACCEPT_SOCIAL:
//                onAcceptSocial();
//                break;
//        }
        boolean isSocialAvialble = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getBoolean(PREF_KEY_ACCESS_SOCIAL, true);
        if (isSocialAvialble)
            onAcceptSocial();
        else
            onOutOfService();
    }

    private void onAcceptSocial(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            onError(this, "user == null", R.string.error);
            return;
        }

        myNameTv.setText(user.getDisplayName());
        setImgFromStorage(user, myIconIv, R.drawable.ic_face_origin_48dp);

        rvUser.setNestedScrollingEnabled(false);
        rvUser.setLayoutManager(new LinearLayoutManager(getContext()));

        rvShare.setNestedScrollingEnabled(false);
        rvShare.setLayoutManager(new LinearLayoutManager(getContext()));

        retrieveUserList();
    }

    private void onOutOfService(){
        maintain.setVisibility(VISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        context = null;
    }

    @Click(R.id.add_user)
    void showAddFriendActivity(){
        com.cks.hiroyuki2.worksupport3.Activities.AddFriendActivity_
                .intent(this)
                .userList((ArrayList<User>) userList)
                .startForResult(AddFriendActivity.REQ_CODE);
    }

//    void showGroupBtn(){
//        rl.setVisibility(View.VISIBLE);
//    }

    @Click(R.id.my_profile)
    void onClickMyProf(){
        ((MainActivity)context).editMyProf();
    }

    @Click(R.id.add_group)
    void onClickAddGroup(){
        if (userList.isEmpty()){
            Toast.makeText(getContext(), R.string.non_user_err, Toast.LENGTH_LONG).show();
            return;
        }

        com.cks.hiroyuki2.worksupport3.Activities.AddGroupActivity_
                .intent(this)
                .userList((ArrayList<User>) userList)
                .requestCode(REQ_CODE_CREATE_GROUP)
                .startForResult(REQ_CODE_CREATE_GROUP);
    }

    /**
     * SocialGroupListRVAdapter.ISocialGroupListRVAdapter 所属
     */
    public void showBoard(@NonNull GroupInUserDataNode groupNode){
        if (mListener != null)
            mListener.onClickGroupItem(groupNode);
    }

    public void updateFriend(@NonNull List<User> newUserList, List<String> newUids){
        if (userAdapter != null)
            userAdapter.updateAllItem(newUserList, newUids);
        userList = newUserList;
    }

    @OnActivityResult(AddFriendActivity.REQ_CODE)
    void onResultAddFriend(final Intent data, int resultCode,
                           @OnActivityResult.Extra final String name,
                           @OnActivityResult.Extra final String photoUrl,
                           @OnActivityResult.Extra final String userUid){
        if (resultCode != RESULT_OK)
            return;

        final FirebaseUser userMe = Util.getUserMe();
        if (userMe == null){
            onError(this, "FirebaseAuth.getInstance().getCurrentUser() == null", R.string.error);
//            if (getActivity() != null){
//                getActivity().setResult(RESULT_CANCELED);
//                getActivity().finish();
//            }
            return;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("/"+ userMe.getUid() +"/"+ userUid +"/name", name);
        hashMap.put("/"+ userMe.getUid() +"/"+ userUid +"/photoUrl", photoUrl);
        hashMap.put("/"+ userUid + "/" + userMe.getUid() + "/name", userMe.getDisplayName());
        String myPhotoUrl = "null";
        if (userMe.getPhotoUrl() != null){
            myPhotoUrl = userMe.getPhotoUrl().toString();
        }
        hashMap.put("/"+ userUid + "/" + userMe.getUid() + "/photoUrl", myPhotoUrl);
        getRef("friend").updateChildren(hashMap);
    }

    @OnActivityResult(REQ_CODE_CREATE_GROUP)
    void onResultCreateGroup(Intent data, int resultCode,
                             @OnActivityResult.Extra(AddGroupActivity.INTENT_BUNDLE_GROUP_NAME) final String groupName,
                             @OnActivityResult.Extra(AddGroupActivity.INTENT_BUNDLE_GROUP_PHOTO_URL) @Nullable String photoUrl,
                             @OnActivityResult.Extra String groupKey){

        if (resultCode != RESULT_OK) return;

        List<User> userListOpe = new ArrayList<>(userList);
        me.setChecked(true);
        userListOpe.add(me);
        userListOpe.remove(0);/*DEFAULT値を取り除く DEFAULT値をどうするかは後で考えましょう*/

        HashMap<String, Object> childMap = makeMap(me.getUserUid(), groupKey, groupName, userListOpe, photoUrl);

        for (User user: userListOpe) {
            GroupInUserDataNode smGroup = new GroupInUserDataNode(groupName, groupKey, photoUrl, user.equals(me));
            childMap.put(makeScheme("userData", user.getUserUid(), "group", groupKey), smGroup);
        }

        childMap.put(makeScheme("calendar", groupKey, DEFAULT), DEFAULT);

        final Group group = new Group(userListOpe, groupName, groupKey, null, me.getUserUid(), photoUrl);//groupインスタンスを作成。
        getRootRef().updateChildren(childMap, (databaseError, databaseReference) -> {
            if (databaseError != null){
                Log.w(TAG, "onComplete: " + databaseError.getMessage());
            } else {
                if (mListener != null)
                    mListener.onCompleteGroup(group);
            }
        });
    }

    @OnActivityResult(CALLBACK_GROUP_NON_ADDED)
    void onResultGroupNonAdd(Intent data, int resultCode,
                             @OnActivityResult.Extra(GROUP) final GroupInUserDataNode groupNode,
                             @OnActivityResult.Extra(RecordDialogFragment.DIALOG_BUTTON) int button){
//        final GroupInUserDataNode groupNode = (GroupInUserDataNode)data.getSerializableExtra(SocialGroupListRVAdapter.GROUP);
//        int button = data.getIntExtra(RecordDialogFragment.DIALOG_BUTTON, Integer.MAX_VALUE);
        if (button == BUTTON_POSITIVE){
            if (mListener == null) return;

            groupNode.added = true;
//            DatabaseReference checkRef = getRef("group", groupNode.groupKey);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(makeScheme("group", groupNode.groupKey, "member", me.getUserUid(), "added"), true);
            hashMap.put(makeScheme("userData", me.getUserUid(), "group", groupNode.groupKey, "added"), true);

            Single.create((SingleOnSubscribe<DatabaseReference>) emitter -> getRootRef().updateChildren(hashMap, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    emitter.onSuccess(databaseReference);
                } else {
                    emitter.onError(databaseError.toException());
                }
            })).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindToLifecycle())
                    .subscribe(databaseReference -> Single.create((SingleOnSubscribe<DataSnapshot>) emitterI -> databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            emitterI.onSuccess(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            emitterI.onError(databaseError.toException());
                        }
                    })).subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(bindToLifecycle())
                            .subscribe(dataSnapshot -> {

                                toastNullable(getContext(), R.string.msg_add_group);
                                if (getContext() == null)
                                    return;
                                Group group = makeGroupFromSnap(getContext(), dataSnapshot, groupNode.groupKey);
                                if (group == null) {
                                    Util.onError(SocialFragment.this, TAG+ "group == null", R.string.error);
                                    return;
                                }
                                mListener.onCompleteGroup(group);
                                groupAdapter.notifyAddedToGroup(groupNode.groupKey);

                            }), throwable -> Util.onError(SocialFragment.this, TAG+ throwable.getMessage(), R.string.error));

        } else if (button == BUTTON_NEGATIVE){
            exitGroup(me.getUserUid(), groupNode.groupKey, R.string.reject_invitation_toast);
        }
    }

    private static HashMap<String, Object> makeMap(@NonNull String myUid, @NonNull String key, @NonNull String groupName, @NonNull List<User> userList, @Nullable String photoUrl){
        String photoUrlC = photoUrl;
        if (photoUrl == null)
            photoUrlC = "null";

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(makeScheme("group", key, "groupName"), groupName);
        hashMap.put(makeScheme("group", key, "host"), myUid);
        hashMap.put(makeScheme("group", key, "photoUrl"), photoUrlC);
        for (User user: userList) {
            hashMap.put(makeScheme("group", key, "member", user.getUserUid()), user);
        }
        return hashMap;
    }

    /**
     * todo 未デバッグ
     */
    public void exitGroup(@NonNull String meUid, @NonNull final String groupKey, @StringRes final int toast){
        Single.create((SingleOnSubscribe<DataSnapshot>) emitter -> getRootRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emitter.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                emitter.onError(databaseError.toException());
            }
        })).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(dataSnapshot -> {
                    toastNullable(getContext(), toast);
                    groupAdapter.notifyExitGroup(groupKey);
                }, throwable -> Util.onError(SocialFragment.this, TAG+ throwable.getMessage(), R.string.error));
    }

    //自分は「参加」、他の人は「未参加」とする
    private List<User> createMemberList(@NonNull User me){
        List<User> userListOpe = new ArrayList<>(userList);
        userListOpe.remove(0);/*DEFAULT値を取り除く DEFAULT値をどうするかは後で考えましょう*/
//        for (User user: userListOpe)
//            user.setChecked(false);
//        me.setChecked(true);
//        userListOpe.add(me);
        return userListOpe;
    }

    public interface IOnCompleteGroup {
        void onCompleteGroup(@NonNull Group group);
        void onClickGroupItem(@NonNull GroupInUserDataNode groupNode);
    }

    private void retrieveUserList(){
        Single.create((SingleOnSubscribe<DataSnapshot>) emitter -> getRef("friend", me.getUserUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emitter.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                emitter.onError(databaseError.toException());
            }
        })).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(dataSnapshot -> {
                    if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()){/*このノードはNonNUllが保障されている*/
                        onError(context, TAG+"!dataSnapshot.exists()"+dataSnapshot.toString(), R.string.error);
                        return;
                    }

                    userList = new ArrayList<>();
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        if (child.getKey().equals(DEFAULT))
                            continue;
                        userList.add(makeUserFromSnap(child));
                    }

                    userAdapter = new SocialListRVAdapter(userList, this, CODE_SOCIAL_FRAG);
                    if (rvUser.getAdapter() == null)
                        rvUser.setAdapter(userAdapter);
                    else
                        rvUser.swapAdapter(userAdapter, false);


                    Single.create((SingleOnSubscribe<DataSnapshot>) emitterI -> getRef("userData", me.getUserUid(), "group").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            emitterI.onSuccess(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            emitterI.onError(databaseError.toException());
                        }
                    })).subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(bindToLifecycle())
                            .subscribe(dataSnapshotI -> {
                                if (!dataSnapshotI.exists() || !dataSnapshotI.hasChildren()){/*このノードはNonNUllが保障されている*/
                                    onError(SocialFragment.this, TAG+"!dataSnapshot.exists()"+dataSnapshotI.toString(), R.string.error);
                                    return;
                                }

                                groupList = new ArrayList<>();
                                for (DataSnapshot data: dataSnapshotI.getChildren()) {
                                    if (data.getKey().equals(DEFAULT))
                                        continue;
                                    GroupInUserDataNode groupNode = data.getValue(GroupInUserDataNode.class);
                                    groupNode.groupKey = data.getKey();
                                    groupList.add(groupNode);
                                }
                                groupAdapter = new SocialGroupListRVAdapter(groupList, SocialFragment.this);
                                if (rvShare.getAdapter() == null)
                                    rvShare.setAdapter(groupAdapter);
                                else
                                    rvShare.swapAdapter(groupAdapter, false);

                                sv.setVisibility(VISIBLE);
                            }, throwable -> Util.onError(SocialFragment.this, TAG+throwable.getMessage(), R.string.error));
                }, throwable -> Util.onError(SocialFragment.this, TAG+throwable.getMessage(), R.string.error));
    }

    /**
     * SocialGroupListRVAdapter.ISocialGroupListRVAdapter 所属
     */
    @Override
    public void showDialog(GroupInUserDataNode groupInUserDataNode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(GROUP, groupInUserDataNode);
        bundle.putString("from", TAG_GROUP_NON_ADDED);
        kickDialogInOnClick(TAG_GROUP_NON_ADDED, CALLBACK_GROUP_NON_ADDED, bundle, this);
    }
}
