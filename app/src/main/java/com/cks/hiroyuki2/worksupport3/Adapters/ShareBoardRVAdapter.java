/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupprotlib.Entity.Content;
import com.cks.hiroyuki2.worksupprotlib.Entity.Document;
import com.cks.hiroyuki2.worksupprotlib.Entity.DocumentEle;
import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.cks.hiroyuki2.worksupprotlib.FirebaseConnection;
import com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;
import static com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment.DIALOG_CODE_DATA_VERT;
import static com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment.DIALOG_CODE_DOC_VERT;
import static com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment.DIALOG_CODE_ITEM_VERT;
import static com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment.DIALOG_TAG_DATA_VERT;
import static com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment.DIALOG_TAG_DOC_VERT;
import static com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment.DIALOG_TAG_ITEM_VERT;
import static com.cks.hiroyuki2.worksupprotlib.Util.setImgFromStorage;

/**
 * ボードの中身を表示するおじさん！頼りになる！
 * ButterKnifeのonClickを実装しないのは、なぜかそうすると落ちるから。
 */

public class ShareBoardRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MANUAL_TAG: " + ShareBoardRVAdapter.class.getSimpleName();
    private static final int ITEM_TYPE_DATA = 0;
    private static final int ITEM_TYPE_UPLOADED = 1;
    private static final int ITEM_TYPE_DOCUMENT = 2;
    private static final int ITEM_TYPE_FOOTER = 3;
    private static final int ITEM_TYPE_UNKNOWN = -1;/**多分ここにはこない。*/
    public static final String BUNDLE_KEY_POSITION = "BUNDLE_KEY_POSITION";
    public static final String BUNDLE_KEY_OLD_COMMENT = "BUNDLE_KEY_OLD_COMMENT";
    public static final String BUNDLE_KEY_NEW_COMMENT = "BUNDLE_KEY_NEW_COMMENT";
    public static final String BUNDLE_KEY_TYPE = "BUNDLE_KEY_TYPE";
    public static String BUNDLE_KEY_HTML = "BUNDLE_KEY_HTML";
    private ShareBoardFragment fragment;
    private LayoutInflater inflater;
    private Group group;
    private List<Boolean> expandList;
    @BindColor(R.color.word_blue_dark) int blue;
    @BindColor(R.color.word_red) int red;

    public ShareBoardRVAdapter(@NonNull Group group, @NonNull ShareBoardFragment fragment){
        super();
        this.fragment = fragment;
        this.group = group;
        setExpandList();
        inflater = LayoutInflater.from(fragment.getContext());
    }

    class ViewHolderUploaded extends RecyclerView.ViewHolder {
        @BindView(R.id.icon) ImageView iconIv;
        @BindView(R.id.title) TextView titleTv;
        @BindView(R.id.sub_title) TextView subTitleTv;
        @BindView(R.id.fl_uploaded) FrameLayout fl;
        @BindView(R.id.card) CardView card;
        @BindView(R.id.comment_ll) LinearLayout commentLl;
        @BindView(R.id.sub_titlef) TextView commentTv;
        @BindView(R.id.vert) ImageView vert;
        ViewHolderUploaded(View v){
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick(R.id.vert)
        void onClickVert(View ll){
            onClickVertAsset(ll);
        }

        @OnClick(R.id.card)
        void onClickCard(View v){
            onClickCardAsset(v);
        }
    }

    class ViewHolderFile extends RecyclerView.ViewHolder {
        @BindView(R.id.icon) CircleImageView iconIv;
        @BindView(R.id.title) TextView titleTv;
        @BindView(R.id.sub_title) TextView subTitleTv;
        @BindView(R.id.fl) FrameLayout fl;
//        @BindView(R.id.btn_ll) LinearLayout btn_ll;
        @BindView(R.id.comment_ll) LinearLayout commentLl;
        @BindView(R.id.sub_titlef) TextView commentTv;
//        @BindView(R.id.item1) LinearLayout item1;
//        @BindView(R.id.item2) LinearLayout item2;
//        @BindView(R.id.item3) LinearLayout item3;
        @BindView(R.id.vert) ImageView vert;
        ViewHolderFile(View v){
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick(R.id.vert)
        void onClickVert(View ll){
            onClickVertAsset(ll);
        }

        @OnClick(R.id.card)
        void onClickCard(View v){
            onClickCardAsset(v);
        }

//        @OnClick({R.id.item2, R.id.item3})
//        void onClickItem(View v){
//            onClickItemAsset(v);
//        }
    }

    class ViewHolderDocument extends RecyclerView.ViewHolder {
        @BindView(R.id.card) CardView card;
        @BindView(R.id.icon) ImageView iconIv;
        @BindView(R.id.fl) FrameLayout fl;
        @BindView(R.id.vert) ImageView vert;
        @BindView(R.id.title) TextView titleTv;
        @BindView(R.id.sub_title) TextView subTitleTv;
        @BindView(R.id.add_comment) ImageButton expandIv;
        @BindView(R.id.expand_view) LinearLayout expandView;
        @BindView(R.id.content_main_doc) TextView docEllipsis;
        ViewHolderDocument(View v){
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick(R.id.vert)
        void onClickVert(View ll){
            onClickVertAsset(ll);
        }

        @OnClick(R.id.card)
        void onClickCard(View v){
            onClickCardAsset(v);
        }

        @OnClick(R.id.add_comment)
        void onClickAddComment(View v){
            onClickAddCommentAsset(v);
        }
    }

    class ViewHolderFooter extends RecyclerView.ViewHolder {
        @BindView(R.id.horizontal_sv) HorizontalScrollView hsv;
        @BindView(R.id.hsv_ll) LinearLayout ll;
        @BindDimen(R.dimen.board_icon_padding) int iconPadding;
        @BindDimen(R.dimen.board_icon) int iconSize;

        ViewHolderFooter(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_DATA:{
                View v = inflater.inflate(R.layout.board_rv_item, parent, false);
                ButterKnife.bind(this, v);
                return new ViewHolderFile(v);}
            case ITEM_TYPE_UPLOADED:{
                View v = inflater.inflate(R.layout.board_rv_item_uploaded, parent, false);
                ButterKnife.bind(this, v);
                return new ViewHolderUploaded(v);}
            case ITEM_TYPE_DOCUMENT:{
                View v = inflater.inflate(R.layout.board_rv_item_document, parent, false);
                ButterKnife.bind(this, v);
                return new ViewHolderDocument(v);}
            case ITEM_TYPE_FOOTER:{
                View v = inflater.inflate(R.layout.share_rv_footer, parent, false);
                ButterKnife.bind(this, v);
                return new ViewHolderFooter(v);}
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int actualPos) {
        int listPos = actualPos-1;

        int type = getItemViewTypeForListPos(listPos);
        if (type == ITEM_TYPE_FOOTER){
            ((ViewHolderFooter) holder).ll.removeAllViews();
            for (User user: group.userList) {
                CircleImageView iconIv = new CircleImageView(fragment.getContext());
                iconIv.setLayoutParams(new ViewGroup.LayoutParams(((ViewHolderFooter)holder).iconSize, ((ViewHolderFooter)holder).iconSize));
                iconIv.setPadding(((ViewHolderFooter)holder).iconPadding, ((ViewHolderFooter)holder).iconPadding, ((ViewHolderFooter)holder).iconPadding, ((ViewHolderFooter)holder).iconPadding);
                ((ViewHolderFooter)holder).ll.addView(iconIv);
                setImgFromStorage(user.getPhotoUrl(), iconIv, R.drawable.ic_face_origin_48dp);
                /*member.addedによって画像をホワイトアウトする？*/
            }

        } else {
            Content content = group.contentList.get(listPos);
            String subTitle = createSubtitle(content);
            initView(holder, listPos, subTitle);//多分、subtitleはnullにならないよ...

            if (type == ITEM_TYPE_DATA){
                String photoUrl = getPhotoUrl(listPos);
                setImgFromStorage(photoUrl, ((ViewHolderFile)holder).iconIv, R.drawable.ic_face_origin_48dp);
//                ((ViewHolderFile) holder).item1.setOnClickListener(this);
//                ((ViewHolderFile) holder).fl.setTag(listPos);
//                ((ViewHolderFile) holder).item3.setTag(actualPos);

            } else if (type == ITEM_TYPE_UPLOADED){
                if (content.type.contains("image/gif")) {//　image/gif, text/plainはicon存在しない...
                    ((ViewHolderUploaded) holder).iconIv.setImageResource(R.drawable.file);
                } else if (content.type.equals("image/jpeg")) {
                    ((ViewHolderUploaded) holder).iconIv.setImageResource(R.drawable.jpg);
                } else if (content.type.equals("image/png")) {
                    ((ViewHolderUploaded) holder).iconIv.setImageResource(R.drawable.png);
                } else if (content.type.equals("text/plain")) {
                    ((ViewHolderUploaded) holder).iconIv.setImageResource(R.drawable.file);
                } else if (content.type.equals("text/html")) {
                    ((ViewHolderUploaded) holder).iconIv.setImageResource(R.drawable.html);
                } else if (content.type.equals("text/css")) {
                    ((ViewHolderUploaded) holder).iconIv.setImageResource(R.drawable.css);
                } else if (content.type.equals("text/xml")) {
                    ((ViewHolderUploaded) holder).iconIv.setImageResource(R.drawable.xml);
                } else if (content.type.equals("text/txt")) {
                    ((ViewHolderUploaded) holder).iconIv.setImageResource(R.drawable.txt);
                } else if (content.type.equals("application/pdf")) {
                    ((ViewHolderUploaded) holder).iconIv.setImageResource(R.drawable.pdf);
                } else {
                    ((ViewHolderUploaded) holder).iconIv.setImageResource(R.drawable.file);
                }

            } else if (type == ITEM_TYPE_DOCUMENT){
                String photoUrl = getPhotoUrl(listPos);
                setImgFromStorage(photoUrl, ((ViewHolderDocument)holder).iconIv, R.drawable.ic_face_origin_48dp);
                //isExpandならばexpandする
                String comment = group.contentList.get(listPos).comment;
                if (comment == null)
                    return;

                ((ViewHolderDocument) holder).expandIv.setTag(listPos);

                Gson gson = new Gson();
                Document doc = gson.fromJson(comment, Document.class);
                DocumentEle eleFirst = doc.eleList.get(0);
                ((ViewHolderDocument) holder).docEllipsis.setText(eleFirst.content);
                ((ViewHolderDocument) holder).expandView.removeAllViews();

                if (isExpand(listPos)){
                    ((ViewHolderDocument) holder).docEllipsis.setMaxLines(Integer.MAX_VALUE);
                    ((ViewHolderDocument) holder).docEllipsis.setEllipsize(null);
                    ((ViewHolderDocument) holder).expandView.setVisibility(VISIBLE);
                    ((ViewHolderDocument) holder).expandIv.setVisibility(VISIBLE);

                    for (int i = 1; i < doc.eleList.size(); i++) {
                        DocumentEle ele = doc.eleList.get(i);

                        View reply = inflater.inflate(R.layout.document_ele, null);
                        TextView replyTv = reply.findViewById(R.id.doc_content);
                        replyTv.setText(doc.eleList.get(i).content);
                        CircleImageView iconRep = reply.findViewById(R.id.icon_ele);
                        setImgFromStorage(ele.user.getPhotoUrl(), iconRep, R.drawable.ic_face_origin_48dp);
                        TextView subTv = reply.findViewById(R.id.sub_title);
                        String text = ele.lastEdit + "posted by " + ele.user.getName();
                        subTv.setText(text);

                        ((ViewHolderDocument) holder).expandView.addView(reply);
                    }

                } else {
                    ((ViewHolderDocument) holder).docEllipsis.setMaxLines(3);
                    ((ViewHolderDocument) holder).docEllipsis.setEllipsize(TextUtils.TruncateAt.END);
                    ((ViewHolderDocument) holder).expandView.setVisibility(GONE);
                    ((ViewHolderDocument) holder).expandIv.setVisibility(GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return group.contentList.size() +1;//headerの分
    }

    //region onClick系列
    private void onClickVertAsset(View ll){
        int listPos = (int) ll.getTag();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_POSITION, listPos);
        switch (getItemViewTypeForListPos(listPos)){
            case ITEM_TYPE_DATA:{
                kickDialogInOnClick(DIALOG_TAG_DATA_VERT, DIALOG_CODE_DATA_VERT, bundle, fragment);
                break;}
            case ITEM_TYPE_UPLOADED:{
                kickDialogInOnClick(DIALOG_TAG_ITEM_VERT, DIALOG_CODE_ITEM_VERT, bundle, fragment);
                break;}
            case ITEM_TYPE_DOCUMENT:{
                kickDialogInOnClick(DIALOG_TAG_DOC_VERT, DIALOG_CODE_DOC_VERT, bundle, fragment);
                break;}
        }
    }

    private void onClickCardAsset(View view){
        final int listPos = (int)((View) view.getParent()).getTag();
        int type = getItemViewTypeForListPos(listPos);

        if (type == ITEM_TYPE_UPLOADED) {
            fragment.onClickItemUploaded(listPos);
        } else if (type == ITEM_TYPE_DOCUMENT) {
            fragment.onClickExpandableView(listPos);
        } else if (type == ITEM_TYPE_DATA) {
            String memberUid = group.contentList.get(listPos).whose;
            fragment.kickViewerActivity(memberUid);
        }
    }

//    private void onClickItemAsset(View v) {
//        int listPos = (int)v.getTag();
//        String memberUid = group.contentList.get(listPos).whose;
//        switch (v.getId()){
//            case R.id.item2:
//                fragment.kickViewerActivity(memberUid, SharedDataViewActivity.CODE_ANALYTICS);
//                break;
//            case R.id.item3:
//                fragment.kickViewerActivity(memberUid, SharedDataViewActivity.CODE_CALENDAR);
//                break;
//        }
//    }

    private void onClickAddCommentAsset(View v){
        int listPos = (int)v.getTag();
        fragment.showEditDocActAsComment(listPos, group.contentList.get(listPos).comment);
    }
    //endregion

    @Override
    public int getItemViewType(int actualPos) {
        return getItemViewTypeForListPos(actualPos-1);
    }

    private int getItemViewTypeForListPos(int listPos){
        if (listPos == -1)
            return ITEM_TYPE_FOOTER;
        Content content = group.contentList.get(listPos);
        if (content.type.equals("data")) {
            return ITEM_TYPE_DATA;
        } else if (content.type.equals("document")){
            return ITEM_TYPE_DOCUMENT;
        } else if (content.type.contains("/")) {
            return ITEM_TYPE_UPLOADED;
        } else {
            return ITEM_TYPE_UNKNOWN;//多分ここには来ない。
        }
    }

    //これどうやったらきれいにまとめられるんだ？？
    private void initView(RecyclerView.ViewHolder holder, int listPos, @NonNull String subTitle){
        Content content = group.contentList.get(listPos);

        switch (getItemViewTypeForListPos(listPos)){
            case ITEM_TYPE_DATA:
                ((ViewHolderFile) holder).fl.setTag(listPos);
                ((ViewHolderFile) holder).vert.setTag(listPos);
                ((ViewHolderFile) holder).titleTv.setText(content.contentName);
                ((ViewHolderFile) holder).subTitleTv.setText(subTitle);
                if (content.comment != null){
                    ((ViewHolderFile) holder).commentTv.setText(content.comment);
                    ((ViewHolderFile) holder).commentLl.setVisibility(VISIBLE);
                } else {
                    ((ViewHolderFile) holder).commentLl.setVisibility(GONE);
                }
                break;

            case ITEM_TYPE_UPLOADED:
                ((ViewHolderUploaded) holder).fl.setTag(listPos);
                ((ViewHolderUploaded) holder).vert.setTag(listPos);
                ((ViewHolderUploaded) holder).titleTv.setText(content.contentName);
                ((ViewHolderUploaded) holder).subTitleTv.setText(subTitle);
                if (content.comment != null){
                    ((ViewHolderUploaded) holder).commentTv.setText(content.comment);
                    ((ViewHolderUploaded) holder).commentLl.setVisibility(VISIBLE);
                } else {
                    ((ViewHolderUploaded) holder).commentLl.setVisibility(GONE);
                }
                break;

            case ITEM_TYPE_DOCUMENT:
                ((ViewHolderDocument) holder).fl.setTag(listPos);
                ((ViewHolderDocument) holder).vert.setTag(listPos);
                ((ViewHolderDocument) holder).titleTv.setText(content.contentName);
                ((ViewHolderDocument) holder).subTitleTv.setText(subTitle);
                break;
        }
    }

    @Nullable
    private String getPhotoUrl(int pos){
        String author = group.contentList.get(pos).whose;
        for (User user : group.userList)
            if (user.getUserUid().equals(author))
                return user.getPhotoUrl();

        return null;
    }

    @Nullable
    private String getLastEditorName(Content content){
        for (User user : group.userList)
            if (user.getUserUid().equals(content.lastEditor))
                return user.getName();

        return null;
    }

    /**
     * @return lastEditor, lastEditともにnullの場合、nullがこのメソッドから返される。
     */
    @Nullable
    private String createSubtitle(Content content){
        String lastEditor = getLastEditorName(content);
        String lastEdit = Util.convertCalPattern(content.lastEdit, Util.datePattern, Util.DATE_PATTERN_DOT_YMD);
        if (lastEditor == null || lastEditor.isEmpty()){
            return lastEdit;
        } else {
            return lastEdit +" by "+ lastEditor;
        }
    }

    private void setExpandList(){
        expandList = new ArrayList<>();
        for (int i = 0; i <group.contentList.size()+1 ; i++) {
            expandList.add(false);
        }
    }

    public boolean isExpand(int pos){
        return expandList.get(pos);
    }

    public void addExpandList(){
        expandList.add(false);
    }

    public void setExpand(int pos, boolean expand){
        expandList.set(pos, expand);
    }
}
