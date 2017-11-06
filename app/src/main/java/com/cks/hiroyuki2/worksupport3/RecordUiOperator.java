/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.cks.hiroyuki2.worksupport3.Adapters.RecordVPAdapter;
import com.cks.hiroyuki2.worksupport3.Fragments.EditTemplateFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.RecordFragment;
import com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItem;
import com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemComment;
import com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemParam;
import com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemTagPool;
import com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemTime;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.cks.hiroyuki2.worksupport3.Adapters.RecordVPAdapter.DATA_NUM;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.RV_POS;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.TIME_EVENT;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.CALLBACK_RANGE_CLICK_TIME;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.CALLBACK_RANGE_CLICK_VALUE;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.POSITION;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.POS_IN_LIST;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickInputDialog;
import static com.cks.hiroyuki2.worksupprotlib.Util.datePattern;
import static com.cks.hiroyuki2.worksupprotlib.Util.delimiter;
import static com.cks.hiroyuki2.worksupport3.RecordDialogFragment.ADD_NEW_TAG;
import static com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemTime.TIME_EVE_RANGE;
import static com.cks.hiroyuki2.worksupprotlib.Util.cal2date;
import static com.cks.hiroyuki2.worksupprotlib.Util.joinArr;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEventRange;
import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEvent;

/**
 * {@link RecordVPAdapter}のビューをいい感じに設定する人。
 */

public class RecordUiOperator implements RecordVpItemTagPool.onClickCardListener, RecordVpItemParam.OnClickParamsNameListener, RecordVpItemComment.onClickCommentListener{

    private static final String TAG = "MANUAL_TAG: " + RecordUiOperator.class.getSimpleName();
    private List<RecordData> list;
    private LinearLayout ll;
    private Calendar cal;
    private Fragment fragment;
    private EditTemplateFragment editFrag;
    private List<RecordVpItem> itemList;

    public RecordUiOperator(@NonNull List<RecordData> list/*このRecordDataの中身のcalは特に使われていません*/, LinearLayout ll, Calendar cal, Fragment fragment){
        this.list = list;
        this.ll = ll;
        this.cal = cal;
        this.fragment = fragment;
    }

    public void initRecordData(){
        Log.d(TAG, "initRecordData: fire");
        itemList = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            RecordData data = list.get(i);
            View view;
            if (data.dataType == 0)
                view = new View(fragment.getContext());
            else {
                RecordVpItem item = buildView(data, i);
                itemList.add(item);
                view= item.buildView();
            }

            ll.addView(view);
        }
    }

    @NonNull
    private RecordVpItem buildView(RecordData data, int i){
        switch (data.dataType){
            case 1:
                return new RecordVpItemTime(data, i, cal, fragment);
            case 2:
                return new RecordVpItemTagPool(data, i, cal, fragment, this);
            case 3:
                return new RecordVpItemParam(data, i, cal, fragment);
            case 4:
                return new RecordVpItemComment(data, i, cal, fragment, this);
        }
        throw new IllegalArgumentException("data.dataTypeの値がおかしい"+ data.dataType);
    }

    @Override
    public void onClickTagPoolContent(Calendar cal, int dataNum) {
        Bundle bundle = makeBundleInOnClick(RecordVPAdapter.TAG_ADD, cal, dataNum);
        kickDialogInOnClick(bundle, RecordVPAdapter.TAG_ADD, RecordVPAdapter.CALLBACK_TAG_ADD);
    }

    @Override
    public void onClickParamsName(int dataNum) {
        //なにもしない
    }

    @Override
    public void onClickParamsAddBtn(int dataNum) {
        //なにもしない
    }

    @Override
    public void onClickCommentEdit(int dataNum, String comment) {
        Bundle bundle = makeBundleInOnClick(RecordVPAdapter.COMMENT, cal, dataNum);
        bundle.putString(RecordVPAdapter.COMMENT, comment);
        String dataName = list.get(dataNum).dataName;
        if (dataName != null)
            bundle.putString(RecordVPAdapter.COMMENT_NAME, dataName);/*Nullでありうる*/
        kickInputDialog(bundle, RecordVPAdapter.COMMENT, RecordVPAdapter.CALLBACK_COMMENT, fragment);
    }

    @Override
    public void onClickCommentName(int dataNum) {
        //do nothing
    }

    public void updateTimeEventTime(@NonNull Intent data){
        TimeEvent timeEvent = (TimeEvent) data.getSerializableExtra(TIME_EVENT);
        int dataNum = data.getIntExtra(DATA_NUM, Integer.MAX_VALUE);
        int rvPos = data.getIntExtra(RV_POS, Integer.MAX_VALUE);
        if (dataNum == Integer.MAX_VALUE || rvPos == Integer.MAX_VALUE){
            onError(fragment, "dataNum == Integer.MAX_VALUE || rvPos == Integer.MAX_VALUE", R.string.error);
            return;
        }

        RecordVpItemTime itemTime = (RecordVpItemTime) itemList.get(dataNum-1);//tagの分があるのでマイナス1
        itemTime.updateTime(rvPos, timeEvent);

        syncTimeDataMapAndFb(dataNum, itemTime);
    }

    public void updateTimeRangeColor(@NonNull Intent data){
        TimeEventRange range = (TimeEventRange) data.getSerializableExtra(TIME_EVE_RANGE);
        int dataNum = data.getIntExtra(DATA_NUM, Integer.MAX_VALUE);
        int posInList = data.getIntExtra(POS_IN_LIST, Integer.MAX_VALUE);
        if (dataNum == Integer.MAX_VALUE || posInList == Integer.MAX_VALUE){
            onError(fragment.getContext(), "dataNum == Integer.MAX_VALUE || posInList == Integer.MAX_VALUE", R.string.error);
            return;
        }

        RecordVpItemTime itemTime = (RecordVpItemTime) itemList.get(dataNum-1);
        itemTime.updateRangeColor(range, posInList);

        syncTimeDataMapAndFb(dataNum, itemTime);
    }

    public void updateTimeRange(@NonNull Intent data, @RecordVPAdapter.UpdateCode int requestCode){
        TimeEvent timeEvent = (TimeEvent) data.getSerializableExtra(TIME_EVENT);
        int dataNum = data.getIntExtra(DATA_NUM, Integer.MAX_VALUE);
        int pos = data.getIntExtra(POSITION, Integer.MAX_VALUE);
        int posInList = data.getIntExtra(POS_IN_LIST, Integer.MAX_VALUE);
        if (dataNum == Integer.MAX_VALUE || pos == Integer.MAX_VALUE || posInList == Integer.MAX_VALUE){
            onError(fragment.getContext(), "dataNum == Integer.MAX_VALUE || pos == Integer.MAX_VALUE || posInList == Integer.MAX_VALUE", R.string.error);
            return;
        }

        RecordVpItemTime itemTime = (RecordVpItemTime) itemList.get(dataNum-1);
        switch (requestCode){
            case CALLBACK_RANGE_CLICK_TIME:
                itemTime.updateRangeTime(timeEvent, pos, posInList);
                break;
            case CALLBACK_RANGE_CLICK_VALUE:
                itemTime.updateRangeValue(timeEvent, pos, posInList);
                break;
        }

        syncTimeDataMapAndFb(dataNum, itemTime);
    }

    public void addItem(@NonNull Intent data){
        TimeEvent timeEvent = (TimeEvent) data.getSerializableExtra(TIME_EVENT);
        int dataNum = data.getIntExtra(DATA_NUM, Integer.MAX_VALUE);
        if (dataNum == Integer.MAX_VALUE){
            onError(fragment.getContext(), "dataNum == Integer.MAX_VALUE", R.string.error);
            return;
        }

        RecordVpItemTime itemTime = (RecordVpItemTime) itemList.get(dataNum-1);
        itemTime.addItem(timeEvent);

        syncTimeDataMapAndFb(dataNum, itemTime);
    }

    public void updateTagPool(Intent data){
        int dataNum = data.getIntExtra(DATA_NUM, Integer.MAX_VALUE);
        if (dataNum == Integer.MAX_VALUE) {
            onError(fragment.getContext(), "dataNum == Integer.MAX_VALUE", R.string.error);
            return;
        }

        List<Integer> elevatedList = data.getIntegerArrayListExtra(ADD_NEW_TAG);
        updateTag2Map(dataNum, elevatedList);
        RecordVpItemTagPool itemTag = (RecordVpItemTagPool) itemList.get(dataNum - 1);
        itemTag.updateTag();
    }

    public void updateComment(@NonNull Intent data) {
        String comment = data.getStringExtra(RecordVPAdapter.COMMENT);
        int dataNum = data.getIntExtra(DATA_NUM, Integer.MAX_VALUE);
        if (comment == null || dataNum == Integer.MAX_VALUE) {
            onError(fragment.getContext(), "comment == null || dataNum == Integer.MAX_VALUE", R.string.error);
            return;
        }

        updateCommentMap(list, dataNum, comment);
        RecordVpItemComment itemComment = (RecordVpItemComment) itemList.get(dataNum - 1);
        itemComment.updateComment();

        ((RecordFragment)fragment).adapter.syncDataMapAndFireBase(list, cal2date(cal, datePattern));
    }

    private void updateTag2Map(int dataNum, List<Integer> elevatedList){
        HashMap<String, Object> data = getDataNonNull(list, dataNum);
        for (int i = 0; i < elevatedList.size(); i++) {
            String numStr = Integer.toString(i);
            String value = (String)data.get(numStr);
            if (value == null)
                continue;
            String[] strings = value.split(delimiter);
            strings[2] = Boolean.toString(i != 0);
            data.put(numStr,  joinArr(strings, delimiter));
        }
    }

    public static void updateCommentMap(List<RecordData> list, int dataNum, String val){
        getDataNonNull(list, dataNum).put("comment", val);
    }

    private void syncTimeDataMapAndFb(int dataNum, RecordVpItemTime itemTime){
        syncTimeDataMap(list, dataNum, itemTime);
        ((RecordFragment)fragment).adapter.syncDataMapAndFireBase(list, cal2date(cal, datePattern));
    }

    public static void syncTimeDataMap(List<RecordData> list, int dataNum, RecordVpItemTime itemTime){
        getDataNonNull(list, dataNum).put("0", new Gson().toJson(itemTime.getDataSet()));
    }

    //region onClick周りのメソッド
    public static Bundle makeBundleInOnClick(String command, Calendar cal, int num){
        Bundle bundle = new Bundle();
        bundle.putString("from", command);
        String date = cal2date(cal, datePattern);
        bundle.putString(RecordVPAdapter.PAGE_TAG, date);
        bundle.putInt(DATA_NUM, num);
        return bundle;
    }

    private void kickDialogInOnClick(Bundle bundle, String command, int commandInt){
        RecordDialogFragment dialog = RecordDialogFragment.newInstance(bundle);
        dialog.setTargetFragment(fragment, commandInt);
        dialog.show(fragment.getActivity().getSupportFragmentManager(), command);
    }
    //endregion

    public static HashMap<String, Object> getDataNonNull(List<RecordData> list, int dataNum){
        HashMap<String, Object> hashMap = list.get(dataNum).data;
        if (hashMap == null)
            hashMap = new HashMap<>();
        return hashMap;
    }

    public RecordVpItem getItem(int pos){
        return itemList.get(pos);
    }
}
