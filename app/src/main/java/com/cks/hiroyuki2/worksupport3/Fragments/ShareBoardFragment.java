/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.cks.hiroyuki2.worksupport3.Activities.EditDocActivity;
import com.cks.hiroyuki2.worksupport3.Activities.MainActivity;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.DialogFragments.ShareBoardDialog;
import com.cks.hiroyuki2.worksupport3.ServiceMessage;
import com.cks.hiroyuki2.worksupprotlib.Entity.Content;
import com.cks.hiroyuki2.worksupprotlib.Entity.Document;
import com.cks.hiroyuki2.worksupprotlib.Entity.DocumentEle;
import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.cks.hiroyuki2.worksupprotlib.Entity.GroupInUserDataNode;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter;
import com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil;
import com.cks.hiroyuki2.worksupprotlib.PreventableAnimator;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.example.hiroyuki3.worksupportlibw.Adapters.ShareBoardRVAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import icepick.Icepick;
import icepick.State;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.cks.hiroyuki2.worksupport3.BackService.SEND_CODE_ADD_COMMENT;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;
import static com.cks.hiroyuki2.worksupport3.DialogFragments.ShareBoardDialog.ADD_ITEM_DIALOG;
import static com.cks.hiroyuki2.worksupport3.Util.API_URL;
import static com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter.CODE_SET_VALUE;
import static com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter.CODE_UPDATE_CHILDREN;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.LIMIT_SIZE;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.getShortenUrlFromRes;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.isOverSize;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.uploadFile;
import static com.cks.hiroyuki2.worksupprotlib.FriendJsonEditor.getOneGroupFromJson;
import static com.cks.hiroyuki2.worksupprotlib.FriendJsonEditor.snap2Json;
import static com.cks.hiroyuki2.worksupprotlib.Util.cal2date;
import static com.cks.hiroyuki2.worksupprotlib.Util.datePattern;
import static com.cks.hiroyuki2.worksupprotlib.Util.getFileName;
import static com.cks.hiroyuki2.worksupprotlib.Util.getMimeType;
import static com.cks.hiroyuki2.worksupprotlib.Util.getUserMe;
import static com.cks.hiroyuki2.worksupprotlib.Util.intentKicker;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;
import static com.cks.hiroyuki2.worksupprotlib.Util.makeScheme;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import static com.cks.hiroyuki2.worksupprotlib.Util.showCompleteNtf;
import static com.cks.hiroyuki2.worksupprotlib.Util.showDownloadingNtf;
import static com.cks.hiroyuki2.worksupprotlib.Util.showUploadingNtf;
import static com.cks.hiroyuki2.worksupprotlib.Util.toastNullable;
import static com.example.hiroyuki3.worksupportlibw.Adapters.ShareBoardRVAdapter.ITEM_TYPE_DATA;
import static com.example.hiroyuki3.worksupportlibw.Adapters.ShareBoardRVAdapter.ITEM_TYPE_DOCUMENT;
import static com.example.hiroyuki3.worksupportlibw.Adapters.ShareBoardRVAdapter.ITEM_TYPE_UPLOADED;

@EFragment(R.layout.fragment_share_board)
public class ShareBoardFragment extends Fragment implements OnFailureListener, SwipeRefreshLayout.OnRefreshListener, ValueEventListener, ShareBoardRVAdapter.IShareBoardRVAdapter {

    //region statics
    private static final String TAG = "MANUAL_TAG: " + ShareBoardFragment.class.getSimpleName();
    public final static int DIALOG_CODE = 1142;
    public final static String DIALOG_TAG = "DIALOG_TAG";
    public final static String DIALOG_TAG_MY_DATA = "DIALOG_TAG_MY_DATA";
    public final static int DIALOG_CODE_MY_DATA = 1143;
    public final static String DIALOG_TAG_ITEM_VERT = "DIALOG_TAG_ITEM_VERT";//rvのitemでvertをclickしたらdialogを出す、その時のやつ。
    public final static int DIALOG_CODE_ITEM_VERT = 1144;
    public final static String DIALOG_TAG_EDIT_COMMENT = "DIALOG_TAG_EDIT_COMMENT";
    public final static int DIALOG_CODE_EDIT_COMMENT = 1145;
    public final static String DIALOG_TAG_DOC_VERT = "DIALOG_TAG_DOC_VERT";
    public final static int DIALOG_CODE_DOC_VERT = 1146;
    public final static String DIALOG_TAG_DATA_VERT = "DIALOG_TAG_DOC_DATA_VERT";
    public final static int DIALOG_CODE_DATA_VERT = 1147;
    private static final int WRITE_REQUEST_CODE = 43;

    public static final int REQ_CODE_UPLOAD_MYFILE = 1300;
    public static final String URL_SHORTEN_API = "https://www.googleapis.com/urlshortener/v1/url"
            + "?key=AIzaSyDDrs60sI8h7JeNjR-VTAJcbdnwRB5bVrk";

    public static final int INTENT_REQ_VIEW_ACTVITIY = 2548;
    //endregion

    @FragmentArg Group group;
    @FragmentArg GroupInUserDataNode groupNode;
    private ShareBoardRVAdapter rvAdapter;
    private PreventableAnimator animator;
    private final View.OnTouchListener touchEater = new View.OnTouchListener() {
        @Contract(value = "_, _ -> true", pure = true)
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    };
    private Transition expandCollapse;
    private FirebaseStorageUtil storageUtil;
    private Context context;
    @State int listPosForDL;

    @ViewById(R.id.srl) SwipeRefreshLayout srl;
    @ViewById(R.id.grid) RecyclerView rv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        storageUtil = new FirebaseStorageUtil(getContext(), group);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String toolbarTitle;
        if (group != null){
            toolbarTitle = group.groupName;
        } else if (groupNode != null){
            toolbarTitle = groupNode.name;
        } else {
            toolbarTitle = "";
        }
        ((MainActivity) context).initToolBar(this, toolbarTitle);
        return null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    private void retrieveGroup(GroupInUserDataNode groupNode){
//        srl.setRefreshing(true);
        getRef("group", groupNode.groupKey)
                .addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        JSONObject jo = snap2Json(dataSnapshot);

        if (jo != null)
            try {
                group = getOneGroupFromJson(jo, groupNode.groupKey);
                storageUtil = new FirebaseStorageUtil(getContext(), group);
                if (group != null){
                    if (group.contentList == null)
                        group.contentList = new ArrayList<>();
//                    srl.setRefreshing(false);
                    drawUI();
                    return;
                }
            } catch (JSONException e) {
                logStackTrace(e);
            }

//        srl.setRefreshing(false);
        toastNullable(getContext(), R.string.error);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        onError(this, TAG+databaseError.getDetails(), R.string.error);
    }

    @AfterViews
    void onAfterViews(){
        srl.setColorSchemeResources(R.color.word_red, R.color.word_green, R.color.word_blue, R.color.word_purple);
        srl.setOnRefreshListener(this);
//        srl.setEnabled(false);

        if (group == null) {
//            ((MainActivity) context).toolbar.setTitle(groupNode.name);
            retrieveGroup(groupNode);
        } else {
//            srl.setEnabled(true);
//            ((MainActivity) context).toolbar.setTitle(group.groupName);
            drawUI();
        }
    }

    private void drawUI(){
        if (getContext() == null)
            return;

        ((MainActivity) getContext()).showFab(R.drawable.ic_add_white_24dp);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdapter = new ShareBoardRVAdapter(group, this);
        rv.setAdapter(rvAdapter);

        animator = new PreventableAnimator();
        rv.setItemAnimator(animator);

        setExpandCollapse();
    }

    private void setExpandCollapse(){
        expandCollapse = new AutoTransition();
        expandCollapse.setDuration(120);
        int interpolator = android.os.Build.VERSION.SDK_INT >= 21?
                android.R.interpolator.fast_out_slow_in:
                android.R.interpolator.linear;
        expandCollapse.setInterpolator(AnimationUtils.loadInterpolator(getContext(), interpolator));

        expandCollapse.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(@NonNull Transition transition) {
                rv.setOnTouchListener(touchEater);
            }

            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                animator.setAnimateMoves(true);
                rv.setOnTouchListener(null);
            }

            @Override
            public void onTransitionCancel(@NonNull Transition transition) {}

            @Override
            public void onTransitionPause(@NonNull Transition transition) {}

            @Override
            public void onTransitionResume(@NonNull Transition transition) {}
        });
    }

    @Override
    public void onClickExpandableView(int listPos){
        boolean isExpanded = rvAdapter.isExpand(listPos);
        if (isExpanded){
            //もし既に展開されていたら、もう一切閉じない。todo いや閉じるべきでは？
            return;
        }
        TransitionManager.beginDelayedTransition(rv, expandCollapse);
        animator.setAnimateMoves(false);
        rvAdapter.setExpand(listPos, !isExpanded);
        rvAdapter.notifyItemChanged(listPos+1);
    }

    public void showAddItemDialog(){
        String[] arr = getContext().getResources().getStringArray(R.array.add_item_dialog);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(arr));
        for (Content content: group.contentList) {
            if(content.type.equals("data") && content.whose.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                list.remove(2);
                break;
            }
        }
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ADD_ITEM_DIALOG, list);
        kickDialogInOnClick(DIALOG_TAG, DIALOG_CODE, bundle, this);
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);
            }
        }, 3000);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("group/" + group.groupKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                srl.setRefreshing(false);
                if (!dataSnapshot.exists()){
                    onError(ShareBoardFragment.this, TAG+ref.toString(), R.string.error);
                    return;
                }

                JSONObject jo = snap2Json(dataSnapshot);
                if (jo == null){
                    onError(ShareBoardFragment.this, TAG+"jo == null", R.string.error);
                    return;
                }

                Group groupC;
                try {
                    groupC = getOneGroupFromJson(jo, group.groupKey);
                } catch (JSONException e) {
                    logStackTrace(e);
                    toastNullable(getContext(), R.string.error);
                    return;
                }

                toastNullable(getContext(), R.string.success_swipe);
                group = groupC;
                storageUtil = new FirebaseStorageUtil(getContext(), group);
                srl.setRefreshing(false);
                onAfterViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                srl.setRefreshing(false);
                onError(ShareBoardFragment.this, databaseError.getDetails(), R.string.error);
            }
        });
    }

    @OnActivityResult(ShareBoardFragment.DIALOG_CODE)
    void onResultAddItem(int resultCode, @OnActivityResult.Extra(ShareBoardDialog.DIALOG_WITCH_CLICKED) int pos){
        if (resultCode != RESULT_OK) return;
        switch (pos){
            case 0:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                String[] mimeTypes = {"image/*", "text/plain", "text/html", "text/css", "application/pdf"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(intent, REQ_CODE_UPLOAD_MYFILE);
                break;
            case 1:
                showEditDocAct();
                break;
            case 2:
                Bundle bundle = new Bundle();
                bundle.putString("groupName", group.groupName);
                kickDialogInOnClick(DIALOG_TAG_MY_DATA, DIALOG_CODE_MY_DATA, bundle, this);
                break;
            default:
                onError(this, TAG+"ShareBoardFragment.DIALOG_CODE", R.string.error);
                break;
        }
    }

    @OnActivityResult(DIALOG_CODE_MY_DATA)
    void onResultShareMyData(int resultCode){
        if (resultCode != RESULT_OK) return;
        shareMyRecord();
    }

    @OnActivityResult(REQ_CODE_UPLOAD_MYFILE)
    void onResultUploadMyFile(Intent data, int resultCode){
        if (resultCode != RESULT_OK) return;
        uploadMyFile(data);
    }

    @OnActivityResult(DIALOG_CODE_ITEM_VERT)
    void onResultClickVert(int resultCode, Intent data,
                           @OnActivityResult.Extra(ShareBoardDialog.DIALOG_WITCH_CLICKED) final int witch,
                           @OnActivityResult.Extra(ShareBoardRVAdapter.BUNDLE_KEY_POSITION) final int listPos){
        if (resultCode != RESULT_OK) return;

        /** @see strings.xmlのvort_dialog */
        switch (witch) {
            case 0: {
                Bundle bundle = data.getExtras();
                bundle.putString(ShareBoardRVAdapter.BUNDLE_KEY_OLD_COMMENT, group.contentList.get(listPos).comment);
                kickDialogInOnClick(DIALOG_TAG_EDIT_COMMENT, DIALOG_CODE_EDIT_COMMENT, bundle, this);
                break;}
            case 1:
                onChoose2ndItem(listPos);
                break;
            case 2:
                onChoose3rdItem(listPos);
                break;
            case 3:
                onChoose4thItem(listPos, false);
                break;
        }
    }

    @OnActivityResult(DIALOG_CODE_DOC_VERT)
    void onResultDocVert(int resultCode, Intent data,
                         @OnActivityResult.Extra(ShareBoardDialog.DIALOG_WITCH_CLICKED) final int witch,
                         @OnActivityResult.Extra(ShareBoardRVAdapter.BUNDLE_KEY_POSITION) final int listPos){
        if (resultCode != RESULT_OK) return;

        if (witch == 0)
            onChoose4thItem(listPos, true);
    }

    @OnActivityResult(DIALOG_CODE_DATA_VERT)
    void onResultDataVert(Intent data, int resultCode,
        @OnActivityResult.Extra(ShareBoardDialog.DIALOG_WITCH_CLICKED) final int witch,
        @OnActivityResult.Extra(ShareBoardRVAdapter.BUNDLE_KEY_POSITION) final int listPos){
            if (resultCode != RESULT_OK) return;

            if (witch == 0)
                onChoose4thItem(listPos, true);
    }

    @OnActivityResult(DIALOG_CODE_EDIT_COMMENT)
    void onResultEditComment(Intent data, int resultCode,
                             @OnActivityResult.Extra(ShareBoardRVAdapter.BUNDLE_KEY_NEW_COMMENT) final String newComment,
                             @OnActivityResult.Extra(ShareBoardRVAdapter.BUNDLE_KEY_POSITION) final int listPos){
        if (resultCode != RESULT_OK) return;

//        Bundle bundle = data.getExtras();

        final Content content = group.contentList.get(listPos);
        DatabaseReference checkRef = getRef("group", group.groupKey, "contents", content.contentKey);
        DatabaseReference writeRef = getRef(checkRef, "comment");
        FbCheckAndWriter writer = new FbCheckAndWriter(checkRef, writeRef, getContext(), newComment) {
            @Override
            public void onSuccess(DatabaseReference ref) {
                toastNullable(getContext(), R.string.updated_comment_msg);
                //リスト書き込みしてUI更新
                content.comment = newComment;/*リスト書き込みはするが、はてローカルに書き込む？*/
                rvAdapter.notifyItemChanged(listPos);
            }
        };
        writer.update(CODE_SET_VALUE);
    }

    @OnActivityResult(EditDocActivity.REQ_INTENT_CODE)
    void onResultAddDoc(Intent data, int resultCode){
        if (resultCode == RESULT_CANCELED) {
            toastNullable(getContext(), R.string.msg_cancel);
        } else if (resultCode == RESULT_OK) {
            addDoc(data);
        }
    }

    @OnActivityResult(EditDocActivity.REQ_INTENT_CODE_COMMENT)
    void onResultComment(int resultCode, Intent data){
        if (resultCode != RESULT_OK) return;
        addDocComment(data);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        logStackTrace(e);
        toastNullable(getContext(), R.string.error);
    }

    //region onChoose2ndItem系列

    private void onChoose2ndItem(final int listPos){
        storageUtil.getStorageUrl(listPos, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                storageUtil.onSuccessShortenUrl(uri, new Callback() {
                    @Override
                    public void onFailure(@Nullable Call call, @Nullable IOException e) {
                        if (e != null)
                            logStackTrace(e);
                        showToastOnMainThread();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String shortenUrl = getShortenUrlFromRes(response);
                        if (shortenUrl == null) {
                            showToastOnMainThread();
                            return;
                        }

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        String subject = group.contentList.get(listPos).contentName;
                        share.putExtra(Intent.EXTRA_SUBJECT, subject);
                        share.putExtra(Intent.EXTRA_TEXT, shortenUrl);
                        String title = getResources().getStringArray(R.array.vort_dialog)[1];
                        startActivity(Intent.createChooser(share, title));
                    }
                });
            }
        }, this);
    }

    @MainThread
    private void showToastOnMainThread(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                onError(ShareBoardFragment.this, TAG + " onErrorメソッド", R.string.error);
            }
        });
    }
    //endregion

    //region onChoose3rdItem系列
    private void onChoose3rdItem(final int listPos){
        listPosForDL = listPos;

        String fileName = group.contentList.get(listPos).contentName;
        String mimeType = group.contentList.get(listPos).type;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);

//        File file = storageUtil.createLocalFile(listPos, FirebaseStorageUtil.CODE_CHASHE_FILE);
//        if (file == null){
//            toastNullable(getContext(), R.string.error);
//            return;
//        }
//
//        final int ntfId = (int)System.currentTimeMillis();
//        final String fileName = group.contentList.get(listPos).contentName;
//
//        storageUtil.downloadFile(listPos, file, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        toastNullable(getContext(), R.string.msgDlSuccess);
//                        showCompleteNtf(fileName, ntfId, R.string.msgDlSuccess);
//                    }
//                },
//                storageUtil
//                , new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        showDownloadingNtf(taskSnapshot, fileName, ntfId);
//                    }
//                }, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        onFailureOparation(e, fileName, ntfId, R.string.msg_failed_download);
//                    }
//                });
    }

    /**
     * 一旦キャッシュファイルを作成しているのは、new File(uri.getPath())ではどういうわけかDL時に例外がスローされるから。
     */
    @OnActivityResult(WRITE_REQUEST_CODE)
    void onResultWriteReq(Intent intent, int resultCode){
        if (resultCode != Activity.RESULT_OK)
            return;
        Uri uri = intent.getData();
        if (uri == null)
            return;

        setPermissionInvariant(uri);//パーミッション永続化は不必要化もしれないけど・・・

        String tempFileName = String.valueOf(new Random().nextInt());
        File file = new File(getContext().getCacheDir(), tempFileName);

        final int ntfId = (int)System.currentTimeMillis();
        final String fileName = group.contentList.get(listPosForDL).contentName;

        storageUtil.downloadFile(listPosForDL, file, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        toastNullable(getContext(), R.string.msgDlSuccess);
                        showCompleteNtf(MainActivity.class, getContext(), fileName, ntfId, R.string.msgDlSuccess);
                    }
                },
                storageUtil,
                new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        showDownloadingNtf(MainActivity.class, getContext(), taskSnapshot, fileName, ntfId);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFailureOparation(e, fileName, ntfId, R.string.msg_failed_download);
                    }
                });

        try(OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri)) {
            FileInputStream fis = new FileInputStream(file);

            // byte型の配列を宣言
            byte[] buf = new byte[256];
            int len;

            // ファイルの終わりまで読み込む
            while((len = fis.read(buf)) != -1){
                outputStream.write(buf);
            }

            //ファイルに内容を書き込む
            outputStream.flush();

            //ファイルの終了処理
            outputStream.close();
            fis.close();
        } catch(Exception e){
            logStackTrace(e);
            toastNullable(getContext(), R.string.error);
        } finally {
            file.delete();
        }
    }

    /**
     * safで、パーミッションの永続化をするためのメソッド。
     */
    private void setPermissionInvariant(@NonNull Uri uri){
        Intent intent = new Intent();
        final int takeFlags = intent.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        getContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
    }
    //endregion

    //region onChoose4thItem系列
    private void onChoose4thItem(final int listPos, final boolean isDoc){
        final Content content = group.contentList.get(listPos);
        getRef("group", group.groupKey, "contents", content.contentKey)
                .removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null)
                    onError(ShareBoardFragment.this, TAG + databaseError.getMessage(), R.string.error);
                else {
                    toastNullable(getContext(), R.string.msg_delete_item);
                    group.contentList.remove(listPos);
                    rvAdapter.notifyDataSetChanged();
                    if (!isDoc)
                        onSuccessRemoveData(content);
                }

            }
        });
    }

    private void onFailureOparation(Exception e, String fileName, int ntfId, @StringRes int string){
        logStackTrace(e);
        toastNullable(getContext(), R.string.error);
        showCompleteNtf(MainActivity.class, getContext(), fileName, ntfId, string);
    }

    /**このメソッドでストレージのデータを削除できようができまいが、ここに到達した時点で{@link #onChoose4thItem(int, boolean)}でDatabaseは削除しているので、
     * トーストは{@link #onChoose4thItem(int, boolean)}で出すようにする。*/
    private void onSuccessRemoveData(Content content){
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("shareFile/"+group.groupKey+"/"+content.contentKey);
        ref.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                logStackTrace(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: addOnSuccessListener");
            }
        });
    }
    //endregion

    //region EditDocActivityにintent投げる系
    private void showEditDocAct(){
        com.cks.hiroyuki2.worksupport3.Activities.EditDocActivity_
                .intent(this)
                .startForResult(EditDocActivity.REQ_INTENT_CODE);
    }

    @Override
    public void showEditDocActAsComment(/**これpositionじゃなくてcontentKey渡した方がいいんじゃないの？ほかのpositionでitemを特定している所も含めて*/int listPos, @NonNull String comment){
        com.cks.hiroyuki2.worksupport3.Activities.EditDocActivity_
                .intent(this)
                .pos(listPos)
                .group(group)
                .startForResult(EditDocActivity.REQ_INTENT_CODE_COMMENT);
    }
    //endregion

    /** firebaseに上げているファイル名は、表示されているファイル名とは別物であることに注意してください。*/
    @Override
    public void onClickItemUploaded(int listPos){
        final Content content = group.contentList.get(listPos);
        if (content.type.equals("application/pdf")){
            Intent intent = new Intent();
            intent.setType("application/pdf");
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                //pdfをダウンロードしちゃう。
//                dlAndShowPdf(listPos);
                showUrlPdf(listPos);
                return;
            }
        }

        storageUtil.getStorageUrl(listPos, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                intentKicker(getContext(), content.contentName, uri, Intent.ACTION_VIEW, content.type);
            }
        }, this);
    }

    /**
     * pdfをブラウザから開く！ダウンロードの必要なし！
     */
    private void showUrlPdf(int listPos){
        storageUtil.getStorageUrl(listPos, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse( "http://docs.google.com/viewer?url=" + uri), "text/html");
                startActivity(intent);
            }
        }, this);
    }

//    void dlAndShowPdf(final int listPos){
//        final File file = storageUtil.createLocalFile(listPos, FirebaseStorageUtil.CODE_CHASHE_FILE);
//        if (file == null){
//            toastNullable(getContext(), R.string.error);
//            return;
//        }
//
//        final int ntfId = (int) System.currentTimeMillis();
//        final Content content = group.contentList.get(listPos);
//
//        storageUtil.downloadFile(listPos, file, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                //todo ここうまくいかないんですけど！
//                Uri uri = Uri.fromFile(file);
//                intentKicker(getContext(), content.contentName, uri, Intent.ACTION_VIEW, content.type);
//            }
//        }, storageUtil, storageUtil, this);
//    }

    /**このアプリを使っている以上、自分のデータのノードはあるだろうから、ここではノードの存在チェックは行わない*/
    private void shareMyRecord(){
        final User me = new User(getUserMe());
        DatabaseReference ref = getRef("group", group.groupKey);

        HashMap<String, Object> children = new HashMap<>();
        final String contentsKey = ref.push().getKey();
        final String ymd = cal2date(Calendar.getInstance(), datePattern);
        final String contentsName = me.getName()+"さんの記録";
        final Content content = new Content(contentsKey, contentsName, ymd, me.getUserUid(), me.getUserUid(), "data", null);
        children.put("contents/"+contentsKey+"/lastEdit", content.lastEdit);
        children.put("contents/"+contentsKey+"/lastEditor", content.lastEditor);
        children.put("contents/"+contentsKey+"/whose", content.whose);
        children.put("contents/"+contentsKey+"/type", content.type);
        children.put("contents/"+contentsKey+"/contentName", content.contentName);
        children.put("contents/"+contentsKey+"/comment", content.comment);

        ref.updateChildren(children, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null)
                    onError(ShareBoardFragment.this, databaseError.getMessage(), R.string.error);
                else {
                    toastNullable(getContext(), R.string.msg_sync_data);
                    group.contentList.add(content);
                    rvAdapter.notifyItemInserted(group.contentList.size()-1);
                }
            }
        });
    }

    private void uploadMyFile(Intent data){
        final Uri uri = data.getData();
        if (isOverSize(uri, LIMIT_SIZE)){
            toastNullable(getContext(), R.string.over_size_err);
            return;
        }

        final String fileName = getFileName(getContext(), uri);
        if (fileName == null) {
            onError(this, TAG+"fileName == null", R.string.error);
            return;
        }

        /*まず、nodeが存在していることを確認
         *      →push().getKey()→contentを作成→画像をuploadFile
         *          →Databaseにcontentを書き込み
         *              →listに書き込み・UI更新*/
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("group/" + group.groupKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    /*ここで、contentsノードに対してではなく、group.groupKeyに対してnullチェックをしていることに注意してください。
                    * contentsノードは存在しなくてもOKなのです！*/
                    onError(ShareBoardFragment.this, TAG+"!dataSnapshot.exists()", R.string.error);
                    return;
                }

                toastNullable(getContext(), R.string.msg_start_upload);
                final String contentsKey = ref.push().getKey();
                final int ntfId = (int) System.currentTimeMillis();//NotificationのIdは現在時刻から生成する。
                uploadFile("shareFile/"+ group.groupKey +"/"+ contentsKey, uri, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFailureOparation(e, fileName, ntfId, R.string.msg_failed_upload);
                    }
                }, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        User me = new User(FirebaseAuth.getInstance().getCurrentUser());
                        String ymd = cal2date(Calendar.getInstance(), datePattern);
                        String mimeType = getMimeType(getContext(), uri);
                        final Content content = new Content(contentsKey, fileName, ymd, me.getUserUid(), me.getUserUid(), mimeType, null);

                        getRef("group", group.groupKey, "contents", contentsKey)
                                .setValue(content, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            onError(ShareBoardFragment.this, TAG + databaseError.getDetails(), R.string.error);
                                            showCompleteNtf(MainActivity.class, getContext(), fileName, ntfId, R.string.msg_failed_upload);
                                        } else {
                                            toastNullable(getContext(), R.string.msg_succeed_upload);
                                            addContent(content);
                                            showCompleteNtf(MainActivity.class, getContext(), fileName, ntfId, R.string.msg_succeed_upload);
                                        }
                                    }
                                });
                    }
                }, storageUtil, new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        showUploadingNtf(MainActivity.class, getContext(), taskSnapshot, fileName, ntfId);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onError(ShareBoardFragment.this, TAG+databaseError.getDetails(), R.string.error);
            }
        });
    }

    private void addDoc(Intent data){
        final String docStr = data.getStringExtra(EditDocActivity.INTENT_KEY_DOC);
        Gson gson = new Gson();
        final Document doc = gson.fromJson(docStr, Document.class);
//        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("group/" + group.groupKey);
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
                String contentsKey = getRef("keyPusher").push().getKey();
                String ymd = cal2date(Calendar.getInstance(), datePattern);
                final User me = new User(FirebaseAuth.getInstance().getCurrentUser());
                final Content content = new Content(contentsKey, doc.title, ymd, me.getUserUid(), me.getUserUid(), "document", docStr);
                HashMap<String, Object> children = new HashMap<>();
                children.put("contents/" + contentsKey + "/lastEdit", content.lastEdit);
                children.put("contents/" + contentsKey + "/lastEditor", content.lastEditor);
                children.put("contents/" + contentsKey + "/whose", content.whose);
                children.put("contents/" + contentsKey + "/type", content.type);
                children.put("contents/" + contentsKey + "/contentName", content.contentName);
                children.put("contents/" + contentsKey + "/comment", content.comment);

                DatabaseReference ref = getRef("group", group.groupKey);
                FbCheckAndWriter writerInner = new FbCheckAndWriter(ref, ref, getContext(), children){
                    @Override
                    public void onSuccess(DatabaseReference ref) {
                        toastNullable(getContext(), R.string.msg_succeed_make_doc);
                        addContent(content);
                    }
                };
                writerInner.update(CODE_UPDATE_CHILDREN);
//            }

//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                onError(ShareBoardFragment.this, TAG+databaseError.getDetails(), R.string.error);
//            }
//        });
    }

    private void addContent(@NonNull Content content){
        group.contentList.add(content);
        rvAdapter.addExpandList();
        rvAdapter.notifyDataSetChanged();
//        rvAdapter.notifyItemRangeInserted(group.contentList.size()-1, 1);//これデバッグすること
    }

    /**
     * トランザクションで値を上書きします。ノードの存在をチェックしなくても、落ちません。
     */
    private void addDocComment(final Intent data){
        final int listPos = data.getIntExtra(EditDocActivity.INTENT_KEY_POS, Integer.MAX_VALUE);
        FirebaseUser user = getUserMe();
        if (listPos == Integer.MAX_VALUE || user == null){
            onError(this, "pos == Integer.MAX_VALUE", R.string.error);
            return;
        }

        final Content content = group.contentList.get(listPos);
        final DocumentEle docEle = (DocumentEle) data.getSerializableExtra(EditDocActivity.INTENT_KEY_DOC);

        ServiceMessage sm = new ServiceMessage(docEle, user);
        ((MainActivity)getActivity()).getConnector().send(SEND_CODE_ADD_COMMENT, sm);

        DatabaseReference ref = getRef(makeScheme("group", group.groupKey, "contents", content.contentKey, "comment"));
        ref.runTransaction(new Transaction.Handler() {
            private String newVal;

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData == null)
                    return Transaction.abort();
                String value = (String) mutableData.getValue();
                Document doc = new Gson().fromJson(value, Document.class);
                doc.eleList.add(docEle);
                newVal = new Gson().toJson(doc);
                mutableData.setValue(newVal);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null)
                    onError(ShareBoardFragment.this, databaseError.toString(), R.string.error);
                else {
                    content.comment = newVal;
                    int actualPos = listPos+1;
                    rvAdapter.notifyItemChanged(actualPos);
                }
            }
        });
    }

    @Override
    public void kickViewerActivity(@NonNull String memberUid){
        com.cks.hiroyuki2.worksupport3.Activities.SharedDataViewActivity_.
                intent(this)
                .uid(memberUid)
                .startForResult(INTENT_REQ_VIEW_ACTVITIY);
    }

    @Override
    public void onClickVertAsset(int i, Bundle bundle) {
        switch (i){
            case ITEM_TYPE_DATA:{
                    kickDialogInOnClick(DIALOG_TAG_DATA_VERT, DIALOG_CODE_DATA_VERT, bundle, this);
                    break;}
            case ITEM_TYPE_UPLOADED:{
                    kickDialogInOnClick(DIALOG_TAG_ITEM_VERT, DIALOG_CODE_ITEM_VERT, bundle, this);
                    break;}
            case ITEM_TYPE_DOCUMENT:{
                    kickDialogInOnClick(DIALOG_TAG_DOC_VERT, DIALOG_CODE_DOC_VERT, bundle, this);
                    break;}
        }
    }
}
