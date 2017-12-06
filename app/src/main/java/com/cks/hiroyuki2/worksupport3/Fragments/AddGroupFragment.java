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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.Activities.AddGroupActivity;
import com.cks.hiroyuki2.worksupport3.Activities.MainActivity;
import com.cks.hiroyuki2.worksupport3.FbIntentService;
import com.cks.hiroyuki2.worksupport3.FbIntentService_;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.RxBus;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.cks.hiroyuki2.worksupprotlib.FirebaseConnection;
import com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.example.hiroyuki3.worksupportlibw.Adapters.SocialListRVAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import icepick.Icepick;
import icepick.State;
import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickInputDialog;
import static com.cks.hiroyuki2.worksupport3.DialogFragments.RecordDialogFragmentInput.INPUT;
import static com.cks.hiroyuki2.worksupport3.RxBus.CREATE_GROUP_NEW_IMG;
import static com.cks.hiroyuki2.worksupport3.Util.OLD_GRP_NAME;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.LIMIT_SIZE_PROF;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.isOverSize;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.uploadFile;
import static com.cks.hiroyuki2.worksupprotlib.Util.getExtension;
import static com.cks.hiroyuki2.worksupprotlib.Util.kickIntentIcon;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;
import static com.cks.hiroyuki2.worksupprotlib.Util.showCompleteNtf;
import static com.cks.hiroyuki2.worksupprotlib.Util.showUploadingNtf;
import static com.cks.hiroyuki2.worksupprotlib.Util.toastNullable;
import static com.example.hiroyuki3.worksupportlibw.AdditionalUtil.CODE_ADD_GROUP_FRAG;

/**
 * {@link com.cks.hiroyuki2.worksupport3.Activities.AddGroupActivity}の子分！彼が扱うFragmentはこのFragmentだけ。
 */
@EFragment(R.layout.fragment_add_group)
public class AddGroupFragment extends Fragment implements Callback , SocialListRVAdapter.ISocialListRVAdapter{

    private static final String TAG = "MANUAL_TAG: " + AddGroupFragment.class.getSimpleName();
    public static final int REQ_CODE_ICON = 2498;
    public static final String DLG_TAG_MK_GROUP = "DLG_TAG_MK_GROUP";
    public static final int CALLBACK_DLG_MK_GROUP = 2499;

    @ViewById(R.id.icon) CircleImageView icon;
    @ViewById(R.id.name) TextView name;
    @ViewById(R.id.recycler) RecyclerView rvUser;
    @ViewById(R.id.def_icon_fl) FrameLayout defIconContainer;
    @ViewById(R.id.header_layout) View header;

    @StringRes(R.string.new_group_name) public String groupName;

    // TODO: 2017/11/27 これって@stateしなくていいの？
    @FragmentArg ArrayList<User> userList;//空でありうる
    @FragmentArg int requestCode;
    @FragmentArg String groupKey;

    public SocialListRVAdapter userAdapter;
    public String dlIconUri = "null";
    private IAddGroupFragment listener;
    private FirebaseStorageUtil storageUtil;

    public interface IAddGroupFragment{
        void showFab();
        void hideFab();
    }

    public void kickShowFab(){
        if (listener != null)
            listener.showFab();
    }

    public void kickHideFab(){
        if (listener != null)
            listener.hideFab();
    }

    @AfterViews
    void afterViews(){
        if (requestCode == AddGroupActivity.REQ_CODE_ADD_GROUP_MEMBER){
            header.setVisibility(GONE);
            userAdapter = new SocialListRVAdapter(userList, this, CODE_ADD_GROUP_FRAG);
        } else {
            name.setText(groupName);
            userAdapter = new SocialListRVAdapter(userList, this, CODE_ADD_GROUP_FRAG);
        }
        storageUtil = new FirebaseStorageUtil(getContext(), null);
        rvUser.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUser.setNestedScrollingEnabled(false);
        rvUser.setAdapter(userAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IAddGroupFragment)
            listener = (IAddGroupFragment) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Click(R.id.icon_fl)
    void onClickIcon(){
        kickIntentIcon(this, REQ_CODE_ICON);
    }

    @Click({R.id.edit_name, R.id.name})
    void onClickEditBtn(){
        Bundle bundle = new Bundle();
        bundle.putString(OLD_GRP_NAME, groupName);
        kickInputDialog(bundle, DLG_TAG_MK_GROUP, CALLBACK_DLG_MK_GROUP, this);
    }

    @OnActivityResult(REQ_CODE_ICON)
    void onResultUpload(Intent data, int resultCode){
        if (resultCode != RESULT_OK) return;

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

//    void uploadGroupIcon(@NonNull String uid, @NonNull String groupKey, @NonNull String groupName, /*このuriは、ローカルファイルのuri*/ @NonNull Uri uri){
        FbIntentService_.intent(getActivity().getApplicationContext())
                .uploadGroupIcon(groupKey, groupName, uri, CREATE_GROUP_NEW_IMG)
                .start();

//        toastNullable(getContext(), R.string.msg_start_upload);
//        String type = getExtension(getContext(), uri);
//        String key = FirebaseConnection.getRef("keyPusher").push().getKey();
//        final String fileName = key + "." + type;
//        final int ntfId = (int) System.currentTimeMillis();
//
//        uploadFile("keyPusher/" + fileName, uri, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        onFailureOparation(e, fileName, ntfId, R.string.msg_failed_upload);
//                    }
//                }, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        dlIconUri = taskSnapshot.getDownloadUrl().toString();
//                        if (getContext() != null)
//                            Picasso.with(getContext())
//                                    .load(uri)
//                                    .into(icon, AddGroupFragment.this);
//                        showCompleteNtf(MainActivity.class, getContext(), fileName, ntfId, R.string.ntf_txt_change_group_img);
//                    }
//                }, storageUtil
//                , new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                        showUploadingNtf(MainActivity.class, getContext(), taskSnapshot, fileName, ntfId);
//                    }
//                });
    }

    @OnActivityResult(CALLBACK_DLG_MK_GROUP)
    public void onResultInputDialog(Intent data, int resultCode){
        if (resultCode != RESULT_OK)
            return;

        groupName = data.getStringExtra(INPUT);
        name.setText(groupName);
    }

    @Override
    public void onSuccess() {
        defIconContainer.setVisibility(GONE);
    }

    @Override
    public void onError() {
        Util.onError(this, TAG + "onError()", R.string.error);
    }

    private void onFailureOparation(Exception e, String fileName, int ntfId, @android.support.annotation.StringRes int string){
        logStackTrace(e);
        toastNullable(getContext(), R.string.error);
        showCompleteNtf(MainActivity.class, getContext(), fileName, ntfId, string);
    }
}
