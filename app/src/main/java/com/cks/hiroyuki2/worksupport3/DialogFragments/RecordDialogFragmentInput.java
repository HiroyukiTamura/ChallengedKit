/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.DialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.util.Log;

import com.cks.hiroyuki2.worksupport3.Fragments.AddGroupFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.GroupSettingFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEvent;
import com.cks.hiroyuki2.worksupprotlib.TemplateEditor;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.cks.hiroyuki2.worksupprotlib.UtilDialog;
import com.example.hiroyuki3.worksupportlibw.Adapters.RecordVPAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.cks.hiroyuki2.worksupprotlib.Util.PARAMS_VALUES;
import static com.cks.hiroyuki2.worksupprotlib.Util.delimiter;
import static com.cks.hiroyuki2.worksupprotlib.UtilDialog.editBuilder;
import static com.cks.hiroyuki2.worksupprotlib.UtilDialog.sendIntent;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRVAdapter.TIME_EVENT;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRangeRVAdapter.CALLBACK_RANGE_CLICK_VALUE;

/**
 * Created by hiroyuki2 on 2017/09/18.
 */

public class RecordDialogFragmentInput extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String TAG = "MANUAL_TAG: " + RecordDialogFragmentInput.class.getSimpleName();
    private TextInputLayout inputLayout;
    private TextInputEditText editText;
    private AlertDialog dialog;
    private String from;
    public static final String INPUT ="INPUT";

    public static RecordDialogFragmentInput newInstance(Bundle bundle){
        Log.d(TAG, "newInstance: fire");
        RecordDialogFragmentInput frag = new RecordDialogFragmentInput();
        frag.setArguments(bundle);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (dialog != null)
            return dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            switch (getTargetRequestCode()){
                case RecordVPAdapter.CALLBACK_COMMENT:{
                    String title = getArguments().getString(RecordVPAdapter.COMMENT_NAME, null);
                    String oldComment = getArguments().getString(RecordVPAdapter.COMMENT, "");
                    setInputLayout(oldComment);
                    dialog = UtilDialog.editBuilder(builder, title, R.string.ok, R.string.cancel, inputLayout, this, null).create();
                    editText.addTextChangedListener(createTwMax(R.string.comment_max_restriction, 0, R.integer.comment_max_len, false));
                    break;}
                case RecordVPAdapter.CALLBACK_COMMENT_NAME:
                case RecordVPAdapter.CALLBACK_TAGVIEW_NAME:
                case Util.CALLBACK_TEMPLATE_PARAMS_NAME:{
                    String oldTitle = getArguments().getString(RecordVPAdapter.NAME, "");
                    setInputLayout(oldTitle);
                    editText.setSingleLine();
                    dialog = editBuilder(builder, R.string.edit_name, R.string.ok, R.string.cancel, inputLayout, this, null).create();
                    TextWatcher tw = createTwOnEditName(oldTitle);
                    if (tw == null)
                        return builder.create();
                    editText.addTextChangedListener(tw);
                    break;}
                case Util.CALLBACK_TEMPLATE_PARAMS_ITEM:{
                    String[] arr = getArguments().getStringArray(PARAMS_VALUES);
                    int dataNum = getArguments().getInt(RecordVPAdapter.DATA_NUM);
                    if (arr == null)
                        return builder.create();
                    setInputLayout(arr[1]);
                    editText.setSingleLine();
                    dialog = editBuilder(builder, R.string.edit_item, R.string.ok, R.string.cancel, inputLayout, this, null).create();
                    TextWatcher tw = createTwOnParamsName(arr[1], dataNum);
                    editText.addTextChangedListener(tw);
                    break;}
                case GroupSettingFragment.CALLBACK_SET_TAG_MK_GROUP:
                case AddGroupFragment.CALLBACK_DLG_MK_GROUP:{
                    setInputLayout(getContext().getString(R.string.new_group_name));
                    editText.setHint(null);
                    editText.setSingleLine();
                    dialog = editBuilder(builder, R.string.group_dialog, R.string.ok, R.string.cancel, inputLayout, this, null)
                            .create();
                    editText.addTextChangedListener(createTwMax(R.string.comment_max_restriction,0, R.integer.tag_max_len, true));
                    break;}
                case CALLBACK_RANGE_CLICK_VALUE:{
                    TimeEvent timeEvent = (TimeEvent) getArguments().getSerializable(TIME_EVENT);
                    setInputLayout(timeEvent.getName());
                    editText.setSingleLine();
                    inputLayout.setHint(null);
                    dialog = editBuilder(builder, R.string.title_range_val_title, R.string.ok, R.string.cancel, inputLayout, this, null)
                            .create();
                    editText.addTextChangedListener(createTwMax(R.string.comment_max_restriction, 0, R.integer.tag_max_len, true));
                    break;}
            }
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialog = null;
    }

    private void setInputLayout(String oldTxt){
        inputLayout = (TextInputLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_content, null);
        inputLayout.setHint(null);
        editText = inputLayout.findViewById(R.id.edit_text);
        editText.setText(oldTxt);
    }

    // TODO: 2017/10/21 createTw()にまとめられる
    private TextWatcher createTwForMkGroup(){
        UtilDialog util = new UtilDialog(dialog);
        util.initView(inputLayout);
        util.setRestriction(R.string.comment_max_restriction);
        util.setHintDef(null);
        return util.createTwMax(R.integer.tag_max_len, true);
    }

    private TextWatcher createTwMax(@StringRes int restriction, @StringRes int hintDef, @IntegerRes int max, boolean rejectNullInput){
        UtilDialog util = new UtilDialog(dialog);
        util.initView(inputLayout);
        util.setRestriction(restriction);
        if (hintDef == 0)
            util.setHintDef(null);
        else
            util.setHintDef(hintDef);
        return util.createTwMax(max, rejectNullInput);
    }

    @Nullable
    private TextWatcher createTwOnEditName(String oldTitle){
        UtilDialog util = new UtilDialog(dialog);
        util.initView(inputLayout);
        util.setRestriction(R.string.comment_max_restriction);
        util.setSecondRst(R.string.restriction_multi_name);
        util.setHintDef(null);

        List<String> list = new ArrayList<>();
        List<RecordData> dataList = TemplateEditor.deSerialize(getContext());
        if (dataList == null) return null;
        for (RecordData data: dataList)
            list.add(data.getDataName());

        return util.createTwMaxAndMulti(R.integer.tag_max_len, oldTitle, list);
    }

    @Nullable
    private TextWatcher createTwOnParamsName(String oldTerm, int dataNum){
        UtilDialog util = new UtilDialog(dialog);
        util.initView(inputLayout);
        util.setRestriction(R.string.comment_max_restriction);
        util.setSecondRst(R.string.restriction_multi_plot);
        util.setHintDef(null);

        List<String> list = new ArrayList<>();
        List<RecordData> dataList = TemplateEditor.deSerialize(getContext());
        if (dataList == null)
            return null;
        RecordData data = dataList.get(dataNum);
        if (data.data == null)
            return null;
        for (String key : data.data.keySet()) {
            String string = (String)data.data.get(key);
            if (string == null) continue;
            list.add(string.split(delimiter)[1]);
        }

        return util.createTwMaxAndMulti(R.integer.tag_max_len, oldTerm, list);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (getTargetRequestCode()){
            case RecordVPAdapter.CALLBACK_COMMENT:
                getArguments().putString(RecordVPAdapter.COMMENT, editText.getText().toString());//TextWatcherで見ているのでeditText.getText() != nullである
                break;
            case RecordVPAdapter.CALLBACK_COMMENT_NAME:
            case RecordVPAdapter.CALLBACK_TAGVIEW_NAME:
            case Util.CALLBACK_TEMPLATE_PARAMS_NAME:
                getArguments().putString(RecordVPAdapter.NAME, editText.getText().toString());//TextWatcherで見ているのでeditText.getText() != nullである
                break;
            case Util.CALLBACK_TEMPLATE_PARAMS_ITEM:
                String[] arr = getArguments().getStringArray(PARAMS_VALUES);
                arr[1] = editText.getText().toString();//TextWatcherで見ているのでeditText.getText() != nullである
                break;
            case AddGroupFragment.CALLBACK_DLG_MK_GROUP:
            case GroupSettingFragment.CALLBACK_SET_TAG_MK_GROUP:
                getArguments().putString(INPUT, editText.getText().toString());
                break;
            case CALLBACK_RANGE_CLICK_VALUE:
                TimeEvent timeEvent = (TimeEvent) getArguments().getSerializable(TIME_EVENT);
                timeEvent.setName(editText.getText().toString());
                break;
        }

        sendIntent(getTargetRequestCode(), this);
    }
}
