/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Entity.Content;
import com.cks.hiroyuki2.worksupprotlib.Entity.Document;
import com.cks.hiroyuki2.worksupprotlib.Entity.DocumentEle;
import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import icepick.Icepick;
import icepick.State;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;
import static com.cks.hiroyuki2.worksupprotlib.Util.logAnalytics;
import static com.cks.hiroyuki2.worksupprotlib.Util.setImgFromStorage;

@EActivity(R.layout.activity_edit_doc2)
public class EditDocActivity extends AppCompatActivity implements TextWatcher /*implements View.OnFocusChangeListener*/ {
    private static final String TAG = "MANUAL_TAG: " + EditDocActivity.class.getSimpleName();
    public static final int REQ_INTENT_CODE = 2010;
    public static final int REQ_INTENT_CODE_COMMENT = 2011;
    public static final String INTENT_KEY_DOC = "INTENT_KEY_DOC";
    public static final String INTENT_KEY_GROUP = "group";
    public static final String INTENT_KEY_POS = "pos";
    public static final String INTENT_KEY_ELEMENT = "addedEle";
    private EditText editTitle;
    private EditText editContent;
    private Document doc;
    @State String input;//null, empty, 代入有の3通りあります
    @State String title;//null, empty, 代入有の3通りあります
    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.vert) View vert;
    @ViewById(R.id.icon) CircleImageView imv;
    @ViewById(R.id.title) TextView titleTv;
    @ViewById(R.id.content_main_doc) TextView mainDoc;
    @ViewById(R.id.sub_title) TextView subTitleTv;
    @ViewById(R.id.add_comment) ImageButton ib;
    @ViewById(R.id.expand_view) LinearLayout expandView;
    @ViewById(R.id.titles_container) LinearLayout ll;
    @DimensionPixelSizeRes(R.dimen.horizontal_margin) int margin;
    @org.androidannotations.annotations.res.StringRes(R.string.edit_hint) String hint;
    @org.androidannotations.annotations.res.StringRes(R.string.edit_hint_title) String hintTitle;

    @Extra int pos = -1;
    @Extra Group group = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.saveInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.restoreInstanceState(this, outState);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        input = editable.length() == 0 ?
                "" : editable.toString();
    }

    @AfterViews
    void onAfterViews(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        textInputEditText.setOnFocusChangeListener(this);

        initAdMob(this);
        logAnalytics(TAG + "起動", this);

        vert.setVisibility(GONE);

        if (pos != -1 && group != null){
            //この辺共通化できるよね
            setImgFromStorage(getAuhtor(pos), imv, R.drawable.ic_face_origin_48dp);

            Content content = group.contentList.get(pos);

            titleTv.setText(content.contentName);
            String subTitle = content.lastEdit + " by "+ getLastEditorName(content);
            subTitleTv.setText(subTitle);

            Gson gson = new Gson();
            doc = gson.fromJson(content.comment, Document.class);
            mainDoc.setText(doc.eleList.get(0).content);

//            LinearLayout expandView = v.findViewById(R.id.expand_view);
            expandView.setVisibility(VISIBLE);
            for (int i = 1; i < doc.eleList.size(); i++) {
                DocumentEle ele = doc.eleList.get(i);

                View reply = getLayoutInflater().inflate(R.layout.document_ele, null);
                TextView replyTv = reply.findViewById(R.id.doc_content);
                replyTv.setText(doc.eleList.get(i).content);

                CircleImageView iconRep = reply.findViewById(R.id.icon_ele);
                setImgFromStorage(ele.user, iconRep, R.drawable.ic_face_origin_48dp);

                TextView subTv = reply.findViewById(R.id.sub_title);
                String text = ele.lastEdit + " by " + ele.user.getName();
                subTv.setText(text);

                expandView.addView(reply);
            }

            View reply = getLayoutInflater().inflate(R.layout.document_ele, null);
            TextView replyTv = reply.findViewById(R.id.doc_content);
            ViewGroup parent = (ViewGroup)replyTv.getParent();
            parent.removeAllViews();
            editContent = new EditText(this);
            editContent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            editContent.setHint(hint);
            parent.addView(editContent);
            CircleImageView iconRep = reply.findViewById(R.id.icon_ele);
            setImgFromStorage(FirebaseAuth.getInstance().getCurrentUser(), iconRep, R.drawable.ic_face_origin_48dp);

            expandView.addView(reply);

        } else {
            setImgFromStorage(FirebaseAuth.getInstance().getCurrentUser(), imv, R.drawable.ic_face_origin_48dp);
            mainDoc.setVisibility(GONE);
            editContent = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = margin;
            lp.rightMargin = margin;
            lp.bottomMargin = margin*2;
            editContent.setLayoutParams(lp);

            editContent.setHint(hint);
            ViewGroup parent = (ViewGroup)mainDoc.getParent();
            parent.removeView(editContent);
            parent.addView(editContent, 1);

            editTitle = new EditText(this);
            editTitle.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            editTitle.setHint(hintTitle);
            if (title != null)
                editTitle.setText(title);

            editTitle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    title = editable.length() == 0 ?
                            "" : editable.toString();
                }
            });
//            LinearLayout ll = .findViewById(R.id.titles_container);
            ll.removeAllViews();
            ll.addView(editTitle);

            ib.setImageResource(R.drawable.ic_publish_white_36dp);
        }

        if (input != null)
            editContent.setText(input);
        editContent.addTextChangedListener(this);
    }

    @Click(R.id.add_comment)
    void onClickIb(){
        if (!validateEt(editContent, R.string.edit_content_error))
            return;

        if (pos != -1 && group != null){
            User user = new User(FirebaseAuth.getInstance().getCurrentUser());
            DocumentEle ele = new DocumentEle(user, editContent.getText().toString());
            doc.eleList.add(ele);
            Intent data = new Intent();
            data.putExtra(INTENT_KEY_DOC, doc);
            data.putExtra(INTENT_KEY_POS, pos);
            setResult(RESULT_OK, data);
            finish();

        } else {
            if (!validateEt(editTitle, R.string.edit_title_error))
                return;

            User user = new User(FirebaseAuth.getInstance().getCurrentUser());
            DocumentEle ele = new DocumentEle(user, editContent.getText().toString());
            List<DocumentEle> eleList = new ArrayList<>(1);
            eleList.add(ele);
            Document doc = new Document(editTitle.getText().toString(), eleList);

            Intent data = new Intent();
            Gson gson = new Gson();
            data.putExtra(INTENT_KEY_DOC, gson.toJson(doc));
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private boolean validateEt(EditText editText, @StringRes int toast){
        Editable editable = editText.getText();
        if (editable == null || editable.toString().isEmpty()){
            Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Nullable
    private User getAuhtor(int pos){
        String author = group.contentList.get(pos).whose;
        for (User user : group.userList)
            if (user.getUserUid().equals(author))
                return user;

        return null;
    }

    @Nullable
    private String getLastEditorName(Content content){
        for (User user : group.userList)
            if (user.getUserUid().equals(content.lastEditor))
                return user.getUserUid();

        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
