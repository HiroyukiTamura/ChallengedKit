/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEvent;
import com.cks.hiroyuki2.worksupprotlib.UtilDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.TIME_EVENT;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;

/**
 * ダイアログ作成おじさん！
 */
public class RecordDialogFragmentPicker extends DialogFragment implements DialogInterface.OnClickListener{

    private static final String TAG = "MANUAL_TAG: " + RecordDialogFragmentPicker.class.getSimpleName();
    static final String DIALOG_TIME_TIME = "DIALOG_TIME";
    private TimeEvent timeEvent;
    @BindView(R.id.time_picker) TimePicker timePicker;
    @BindView(R.id.radio_group) RadioGroup radioGroup;
    @BindView(R.id.today) RadioButton radioToday;
    @BindView(R.id.yesterday) RadioButton radioYesterday;
    @BindView(R.id.tomorrow) RadioButton radioTommorow;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.timepicker_custom, null);
        ButterKnife.bind(this, view);
        timePicker.setIs24HourView(true);
        if (timeEvent.getOffset() >0){
            radioTommorow.toggle();
        } else if (timeEvent.getOffset() <0){
            radioYesterday.toggle();
        }
        return UtilDialog.editBuilder(builder, null, R.string.ok, R.string.cancel, view, this, null).create();
//        TimePickerDialog dialog =  new TimePickerDialog(getActivity(), this, timeEvent.getHour(), timeEvent.getMin(), true);
//        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(R.string.cancel), this);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == Dialog.BUTTON_POSITIVE){
            Fragment target = getTargetFragment();
            if (target == null){
                onError(getContext(), TAG + "target == null", R.string.error);
                return;
            }

            timeEvent.setHour(timePicker.getHour());
            timeEvent.setMin(timePicker.getMinute());
            int offset = 0;
            switch (radioGroup.getCheckedRadioButtonId()){
                case R.id.yesterday:
                    offset = -1;
                    break;
                case R.id.today:
                    break;
                case R.id.tomorrow:
                    offset = 1;
                    break;
            }
            timeEvent.setOffset(offset);

            Intent intent = new Intent();
            intent.putExtras(getArguments());
            target.onActivityResult(getTargetRequestCode(), RESULT_OK, intent);//targetはRecordFragment or EditTemplateFragmentのどちらかです
        }
    }
}
