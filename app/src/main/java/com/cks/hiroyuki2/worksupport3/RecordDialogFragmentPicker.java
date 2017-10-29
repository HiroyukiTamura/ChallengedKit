/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TimePicker;

import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEvent;

import static android.app.Activity.RESULT_OK;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.TIME_EVENT;

/**
 * ダイアログ作成おじさん！
 */

public class RecordDialogFragmentPicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener, DialogInterface.OnClickListener{

    private static final String TAG = "MANUAL_TAG: " + RecordDialogFragmentPicker.class.getSimpleName();
    static final String DIALOG_TIME_TIME = "DIALOG_TIME";
    private TimeEvent timeEvent;

    public static RecordDialogFragmentPicker newInstance(Bundle bundle){
        Log.d(TAG, "newInstance: fire");
        RecordDialogFragmentPicker frag = new RecordDialogFragmentPicker();
        frag.setArguments(bundle);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        timeEvent = (TimeEvent) getArguments().getSerializable(TIME_EVENT);
        TimePickerDialog dialog =  new TimePickerDialog(getActivity(), this, timeEvent.getHour(), timeEvent.getMin(), true);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(R.string.cancel), this);
        return dialog;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        Log.d(TAG, "onTimeSet: " + i +":" + i1);
        Fragment target = getTargetFragment();
        if (target == null) return;

        Intent intent = new Intent();
        timeEvent.setHour(i);
        timeEvent.setMin(i1);
        intent.putExtras(getArguments());

        target.onActivityResult(getTargetRequestCode(), RESULT_OK, intent);//targetはRecordFragment or EditTemplateFragmentのどちらかです
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Log.d(TAG, "onClick: キャンセル");
    }
}
