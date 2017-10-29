/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.RecordVpItems;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cks.hiroyuki2.worksupprotlib.Util.getNullableText;
import static com.cks.hiroyuki2.worksupprotlib.Util.setNullableText;

/**
 * RecordVpItem兄弟のcommentおじさん！
 */

public class RecordVpItemComment extends RecordVpItem {

    private static final String TAG = "MANUAL_TAG: " + RecordVpItemComment.class.getSimpleName();
    @BindView(R.id.comment_name) TextView name;
    @BindView(R.id.edit_text) TextView tv;
    private View view;
    private onClickCommentListener listener;

    public interface onClickCommentListener{
        void onClickCommentName(int dataNum);
        void onClickCommentEdit(int dataNum, String comment);
    }

    public RecordVpItemComment(RecordData data, int dataNum, Calendar cal, Fragment fragment, onClickCommentListener listener) {
        super(data, dataNum, cal, fragment);
        this.listener = listener;
    }

    @Override
    public View buildView() {
        view = getFragment().getLayoutInflater().inflate(R.layout.record_vp_item_comment, null);
        ButterKnife.bind(this, view);

        setNullableText(name, getData().dataName);
        updateComment();
        return view;
    }

    public void updateComment(){
        if (getData().data != null && !getData().data.isEmpty() && getData().data.containsKey("comment")){
            String string = (String) getData().data.get("comment");
            setNullableText(tv, string);
        }
    }

    public void updateName(String title){
        setNullableText(name, title);
    }

    @OnClick(R.id.comment_name)
    void onClickCommentName(){
        listener.onClickCommentName(getDataNum());
    }

    @OnClick(R.id.edit_text)
    void onClickEditText(){
        listener.onClickCommentEdit(getDataNum(), getNullableText(tv));
    }
}