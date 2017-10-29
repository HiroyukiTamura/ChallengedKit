/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.cks.hiroyuki2.worksupport3.Util.PREF_KEY_COLOR;
import static com.cks.hiroyuki2.worksupport3.Util.circleId;
import static com.cks.hiroyuki2.worksupport3.Util.colorId;
import static com.cks.hiroyuki2.worksupport3.Util.nullableEqual;
import static com.cks.hiroyuki2.worksupportlibrary.Util.PREF_KEY_COLOR;
import static com.cks.hiroyuki2.worksupportlibrary.Util.PREF_NAME;
import static com.cks.hiroyuki2.worksupportlibrary.Util.nullableEqual;

/**
 * Created by hiroyuki2 on 2017/09/18.
 */

public class UtilDialog implements DialogInterface.OnShowListener{
    private static final String TAG = "MANUAL_TAG: " + UtilDialog.class.getSimpleName();
    private AlertDialog dialog;
    private TextInputEditText editText;
    private TextInputLayout inputLayout;
    private @StringRes int restriction;
    private @StringRes int secondRst;
    private String hintDef;
    private View contentView;

    public UtilDialog(AlertDialog dialog){
        this.dialog = dialog;
    }

    public void initView(View contentView){
        this.contentView = contentView;
        editText = contentView.findViewById(R.id.edit_text);
        inputLayout = contentView.findViewById(R.id.text_input);
        hintDef = dialog.getContext().getString(R.string.hint_default);
    }

    public void setHintDef(@StringRes int hintDef){
        this.hintDef = dialog.getContext().getString(hintDef);
    }

    public void setHintDef(@Nullable String hintDef){
        this.hintDef = hintDef;
    }

    public void setRestriction(@StringRes int restriction){
        this.restriction = restriction;
    }

    public void setSecondRst(@StringRes int secondRst){
        this.secondRst = secondRst;
    }

    private void setDisEnableOkAtFirstIfNull(){
        dialog.setOnShowListener(this);
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        Editable editable = editText.getText();
        if (editable == null || editable.toString().isEmpty()){
            dialog.getButton(BUTTON_POSITIVE).setEnabled(false);
        }
    }

    public TextWatcher createTwMax(@IntegerRes final int max, final boolean rejectNull){
        if (rejectNull)
            setDisEnableOkAtFirstIfNull();

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (rejectNull && (editable == null || editable.toString().isEmpty())){
                    dialog.getButton(BUTTON_POSITIVE).setEnabled(false);
                } else if (editable.toString().length() > dialog.getContext().getResources().getInteger(max)){
                    setDisEnableOk(restriction);
                } else {
                    setEnableOk(hintDef);
                }
            }
        };
    }

    public TextWatcher createTwMin(@IntegerRes final int min){
        setDisEnableOkAtFirstIfNull();

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int minLen = dialog.getContext().getResources().getInteger(min);
                if (editable == null || editable.toString().length() < minLen){
                    setDisEnableOk(restriction);
                } else {
                    setEnableOk(hintDef);
                }
            }
        };
    }

    public TextWatcher createTwMaxAndMulti(final int max, @Nullable final String oldName, @Nullable final List<String> multiList){
        setDisEnableOkAtFirstIfNull();

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable == null || editable.toString().isEmpty()){
                    dialog.getButton(BUTTON_POSITIVE).setEnabled(false);
                } else if (editable.toString().length() > dialog.getContext().getResources().getInteger(max)){
                    setDisEnableOk(restriction);
                } else if (oldName != null && editable.toString().equals(oldName)){
                    setEnableOk(hintDef);
                } else if (multiList == null){
                    setEnableOk(hintDef);
                } else {
                    for (String name: multiList) {
                        if (nullableEqual(name, editable.toString())){
                            setDisEnableOk(secondRst);
                            return;
                        }
                    }

                    setEnableOk(hintDef);
                }
            }
        };
    }

    private void setEnableOk(@Nullable String hintDef){
        inputLayout.setHint(hintDef);
        dialog.getButton(BUTTON_POSITIVE).setEnabled(true);
    }

    private void setDisEnableOk(@StringRes int restriction){
        inputLayout.setHint(dialog.getContext().getString(restriction));
        dialog.getButton(BUTTON_POSITIVE).setEnabled(false);
    }

    static void sendIntent(int callback, DialogFragment dialogFragment){
        Fragment target = dialogFragment.getTargetFragment();
        if (target == null) return;
        Intent intent = new Intent();
        intent.putExtras(dialogFragment.getArguments());
        target.onActivityResult(callback, Activity.RESULT_OK, intent);
    }

    static AlertDialog.Builder editBuilder(AlertDialog.Builder builder, @Nullable String title, int positiveId, int negativeId, @Nullable View view, DialogInterface.OnClickListener positiveListener, @Nullable DialogInterface.OnClickListener negativeListener){
        if (title != null && !title.isEmpty())
            builder.setTitle(title);
        return innerEditBuilder(builder, view, positiveId, negativeId, positiveListener, negativeListener);
    }

    static AlertDialog.Builder editBuilder(AlertDialog.Builder builder, @StringRes int titleId, int positiveId, int negativeId, @Nullable View view, DialogInterface.OnClickListener positiveListener, @Nullable DialogInterface.OnClickListener negativeListener){
        builder.setTitle(titleId);
        return innerEditBuilder(builder, view, positiveId, negativeId, positiveListener, negativeListener);
    }

    private static AlertDialog.Builder innerEditBuilder(AlertDialog.Builder builder, @Nullable View view, int positiveId, int negativeId, DialogInterface.OnClickListener positiveListener, @Nullable DialogInterface.OnClickListener negativeListener){
        if (view != null)
            builder.setView(view);
        if (positiveId != 0)
            builder.setPositiveButton(positiveId, positiveListener);
        if (negativeId != 0)
            builder.setNegativeButton(negativeId, negativeListener);
        return builder;
    }

    //カラーを選択したら、最後に選択した色をPrefに書き込み、記憶しておく。次回ダイアログを表示したときには、その色にチェックをして表示する
    static void setColorCircle(@NonNull Context context, @NonNull LinearLayout root, @Nullable View.OnClickListener listener, final int num){
        Log.d(TAG, "setColorCircle: fire");
        int id = circleId.get(num);
        FrameLayout fm = root.findViewById(id);
        ImageView iv = (ImageView) fm.getChildAt(0);
        iv.setColorFilter(ContextCompat.getColor(context, colorId.get(num)));
        iv.setOnClickListener(listener);
    }

    static void onClickCircle(Context context, View clickedView, View rootView){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        int colorNum = Integer.valueOf((String) ((FrameLayout)clickedView.getParent()).getTag());
        editor.putInt(PREF_KEY_COLOR, colorNum);
        editor.apply();

        for (int id: circleId) {
            FrameLayout fm = rootView.findViewById(id);
            ImageView iv = (ImageView) fm.getChildAt(1);
            if (fm.getChildAt(1).getVisibility() == View.VISIBLE){
                iv.setVisibility(View.INVISIBLE);
            }
        }

        ((FrameLayout)clickedView.getParent()).getChildAt(1).setVisibility(View.VISIBLE);
    }
}
