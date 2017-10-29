/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.Adapters.ShareBoardRVAdapter;
import com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment.DIALOG_CODE_DATA_VERT;
import static com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment.DIALOG_CODE_DOC_VERT;
import static com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment.DIALOG_CODE_EDIT_COMMENT;
import static com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment.DIALOG_CODE_ITEM_VERT;
import static com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment.DIALOG_CODE_MY_DATA;
import static com.cks.hiroyuki2.worksupprotlib.Util.nullableEqual;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import static com.cks.hiroyuki2.worksupprotlib.Util.nullableEqual;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;

/**
 * Created by hiroyuki2 on 2017/09/03.
 */

public class ShareBoardDialog extends DialogFragment implements DialogInterface.OnClickListener, View.OnClickListener{
    private static final String TAG = "MANUAL_TAG: " + ShareBoardDialog.class.getSimpleName();
    //共通の変数
    private View content;
    private LayoutInflater inflater;
    //FRAGMENT_TAG_MY_DATA専用の変数
    private String groupName;
    //DIALOG_CODE_ITEM_VERT専用の変数;
    public static final String DIALOG_WITCH_CLICKED = "DIALOG_WITCH_CLICKED";
    public static final String CLICK_POSITION = "CLICK_POSITION";
    public static final String ADD_ITEM_DIALOG = "ADD_ITEM_DIALOG";
    private Bundle bundle;

    public static ShareBoardDialog newInstance(@Nullable Bundle bundle){
        Log.d(TAG, "newInstance: fire");
        ShareBoardDialog frag = new ShareBoardDialog();
        if (bundle != null){
            frag.setArguments(bundle);
        }
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null)
            return;
        bundle = getArguments();
        inflater = LayoutInflater.from(getContext());
        groupName = getArguments().getString("groupName");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getTargetRequestCode() == ShareBoardFragment.DIALOG_CODE){
            return makeAlert();
        } else if (getTargetRequestCode() == DIALOG_CODE_MY_DATA){
            return makeChooseAlert();
        } else if (getTargetRequestCode() == DIALOG_CODE_ITEM_VERT){
            return makeVertAlert(R.array.vort_dialog);
        } else if (getTargetRequestCode() == DIALOG_CODE_EDIT_COMMENT){
            return makeEditCommentAlert();
        } else if (getTargetRequestCode() == DIALOG_CODE_DOC_VERT) {
            return makeVertAlert(R.array.vort_dialog_doc);
        } else if (getTargetRequestCode() == ShareBoardFragment.DIALOG_CODE_DATA_VERT){
            return makeVertAlert(R.array.vort_dialog_doc);
        }

        return new AlertDialog.Builder(getContext()).create();
    }

    //region getTargetRequestCode() == ShareBoardFragment.FRAGMENT_TAG)系列
    AlertDialog makeAlert(){
//        setAlertContent();
        ShareBoardDialogAdapter adapter = new ShareBoardDialogAdapter(getContext(), getTargetRequestCode(), bundle.getStringArrayList(ADD_ITEM_DIALOG));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setAdapter(adapter, this);
        return builder.create();
    }

    void setAlertContent(){
        content = inflater.inflate(R.layout.board_dialog_add_item2, null);
        content.findViewById(R.id.btn).setOnClickListener(this);
        setDialogCompo(R.id.compo0, R.string.dialog_compo0, R.drawable.firebase_auth_120dp);
        setDialogCompo(R.id.compo1, R.string.dialog_compo1, R.drawable.firebase_auth_120dp);
        setDialogCompo(R.id.compo2, R.string.dialog_compo2, R.drawable.firebase_auth_120dp);
        setDialogCompo(R.id.compo3, R.string.dialog_compo3, R.drawable.firebase_auth_120dp);
    }

    void setDialogCompo(@IdRes int compo, @StringRes int title, @DrawableRes int icon){
        LinearLayout ll = content.findViewById(compo);
        ll.setTag(compo);//layoutのidをタグ付けする
        ll.setOnClickListener(this);
        ((TextView) (ll.findViewById(R.id.title))).setText(title);
        ((ImageView) (ll.findViewById(R.id.icon))).setImageResource(icon);
    }
    //endregion


    /**getTargetRequestCode() == ShareBoardFragment.DIALOG_CODE_MY_DATA 系列*/
    private AlertDialog makeChooseAlert(){
        String msg = "自分のデータをグループ「" +groupName+ "」に公開します";
        return new AlertDialog.Builder(getContext())
                .setMessage(msg)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    /**getTargetRequestCode() == ShareBoardFragment.DIALOG_CODE_ITEM_VERT 系列*/
    private AlertDialog makeVertAlert(@ArrayRes int arrRes){
        return new AlertDialog.Builder(getContext())
                .setItems(arrRes, this)
                .setTitle("操作を選択")
                .create();
    }

    private AlertDialog makeEditCommentAlert(){
        View v = inflater.inflate(R.layout.dialog_content, null);
        TextInputEditText editText = v.findViewById(R.id.edit_text);
        String oldComment = bundle.getString(ShareBoardRVAdapter.BUNDLE_KEY_OLD_COMMENT);
        if (oldComment != null) 
            editText.setText(oldComment);
        return new AlertDialog.Builder(getContext())
                .setView(v)
                .setTitle("コメントを編集")
                .setNegativeButton(R.string.cancel, this)
                .setPositiveButton(R.string.ok, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (getTargetRequestCode() == DIALOG_CODE_MY_DATA){
            sendIntent(getTargetRequestCode(), null);

        } else if (getTargetRequestCode() ==  DIALOG_CODE_ITEM_VERT || getTargetRequestCode() == DIALOG_CODE_DOC_VERT || getTargetRequestCode() == DIALOG_CODE_DATA_VERT){
            int actualPos = getArguments().getInt(ShareBoardRVAdapter.BUNDLE_KEY_POSITION, 10000);
            if (actualPos == 10000){
                onError(getContext(), TAG + "  position == 10000", R.string.error);
                dismiss();
            } else {
                Intent intent = new Intent();
                bundle.putInt(DIALOG_WITCH_CLICKED, i);
                bundle.putInt(ShareBoardRVAdapter.BUNDLE_KEY_POSITION, actualPos);
                intent.putExtras(bundle);
                sendIntent(getTargetRequestCode(), intent);
            }

        } else if (getTargetRequestCode() == DIALOG_CODE_EDIT_COMMENT){
            switch (i){
                case BUTTON_NEGATIVE:
                    dismiss();
                    break;
                case BUTTON_POSITIVE:
                    TextInputEditText editText = ((AlertDialog)dialogInterface).findViewById(R.id.edit_text);
                    if (editText == null){
                        onError(getContext(), TAG + "getTargetRequestCode() == ShareBoardFragment.DIALOG_CODE_EDIT_COMMENT: editText == null", R.string.error);
                        dismiss();
                        return;
                    }

                    String commentNew = null;
                    if (editText.getText() != null)
                        commentNew = editText.getText().toString();
                    String commentOld = bundle.getString(ShareBoardRVAdapter.BUNDLE_KEY_OLD_COMMENT);
                    if (nullableEqual(commentNew, commentOld)){
                        Toast.makeText(getContext(), "更新しました", Toast.LENGTH_LONG).show();
                        dismiss();
                        return;
                    }

                    bundle.putString(ShareBoardRVAdapter.BUNDLE_KEY_NEW_COMMENT, commentNew);
                    sendIntent(getTargetRequestCode(), new Intent().putExtras(bundle));
                    break;
            }
        } else if (getTargetRequestCode() == ShareBoardFragment.DIALOG_CODE){
            bundle.putInt(DIALOG_WITCH_CLICKED, i);
            sendIntent(getTargetRequestCode(), new Intent().putExtras(bundle));
        }
    }

    @Override
    public void onClick(View view) {
        if (getTargetRequestCode() == ShareBoardFragment.DIALOG_CODE){
            if (view.getId() == R.id.btn){
                getDialog().dismiss();
                return;
            }

            int tag = (int)view.getTag();
            sendIntent(tag, null);
            getDialog().dismiss();
        }
    }

    /**
     * @param requestCode from==ShareBoardFragment.FRAGMENT_TAGの場合、compoIdが代入される(ex.R.id.compo0)
     * @see #onClick(View)
     */
    private void sendIntent(int requestCode, @Nullable Intent intent){
        Fragment target = getTargetFragment();
        if (target == null) return;
        target.onActivityResult(requestCode, Activity.RESULT_OK, intent);
    }
}
