/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.Activities.MainActivity;
import com.cks.hiroyuki2.worksupport3.Adapters.RecordParamsRVAdapter;
import com.cks.hiroyuki2.worksupport3.Adapters.RecordVPAdapter;
import com.cks.hiroyuki2.worksupport3.DialogKicker;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.RecordDiaogFragmentTag;
import com.cks.hiroyuki2.worksupport3.RecordRVAdapter;
import com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItem;
import com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemComment;
import com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemParam;
import com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemTime;
import com.cks.hiroyuki2.worksupport3.RecordVpItems.TempItemTagPool;
import com.cks.hiroyuki2.worksupport3.TemplateEditor;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.cks.hiroyuki2.worksupport3.Adapters.RecordVPAdapter.DATA_NUM;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_ADD2;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_CLICK;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_CLICK2;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.DIALOG_TAG_ITEM_CLICK2;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.RV_POS;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.TIME_EVENT;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.CALLBACK_RANGE_CLICK_TIME;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.CALLBACK_RANGE_CLICK_VALUE;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.POSITION;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.POS_IN_LIST;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickCircleAndInputDialog;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickInputDialog;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickWidgetDialog;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.delimiter;
import static com.cks.hiroyuki2.worksupport3.RecordUiOperator.syncTimeDataMap;
import static com.cks.hiroyuki2.worksupport3.RecordUiOperator.updateCommentMap;
import static com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemTime.CALLBACK_RANGE_COLOR;
import static com.cks.hiroyuki2.worksupport3.TempWidgetDialogFragment.APPLY_TEMP_IS_SUCCESS;
import static com.cks.hiroyuki2.worksupport3.TempWidgetDialogFragment.CALLBACK_TEMPLATE_ADD;
import static com.cks.hiroyuki2.worksupport3.TempWidgetDialogFragment.CALLBACK_TEMPLATE_EDIT;
import static com.cks.hiroyuki2.worksupport3.TempWidgetDialogFragment.TEMPLATE_ADD;
import static com.cks.hiroyuki2.worksupport3.TempWidgetDialogFragment.TEMPLATE_EDIT;
import static com.cks.hiroyuki2.worksupport3.TemplateEditor.applyTemplate;
import static com.cks.hiroyuki2.worksupprotlib.Util.PARAMS_VALUES;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import static com.cks.hiroyuki2.worksupprotlib.Util.toast;
import static com.cks.hiroyuki2.worksupprotlib.Util.toast;

@EFragment(R.layout.fragment_setting_fragmnet)
public class EditTemplateFragment extends Fragment implements RecordVpItemComment.onClickCommentListener, RecordVpItemParam.OnClickParamsNameListener {
    private static final String TAG = "MANUAL_TAG: " + EditTemplateFragment.class.getSimpleName();

    private TreeMap<Integer, RecordRVAdapter> timeAdapterTree = new TreeMap<>();
    private List<RecordData> list;
    private List<RecordVpItem> uiList = new ArrayList<>();
    private ScrollView rootView;
    private Context context;
    @ViewById(R.id.root_container) LinearLayout ll;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @AfterViews
    void afterViews(){
        rootView = (ScrollView) getView();

        list = TemplateEditor.deSerialize(getContext());
        if (list == null) {
            onError(this, TAG+"list == null", R.string.error);
            return;
        }

        for (int i=0; i<list.size(); i++) {
            RecordData data = list.get(i);
            switch (data.dataType){
                case 0://ヘッダータグ
                    ll.addView(new View(getContext()), ll.getChildCount()-1);
                    uiList.add(new RecordVpItem(list.get(i), i, null, this) {
                        @Override
                        public View buildView() {
                            return null;
                        }
                    });
                    break;
                case 1://タイムライン
                case 2://タグプール
                case 3://リスト
                case 4://自由記述
                    setView(data, i);
                    break;
            }
        }
    }

    public List<RecordData> getList() {
        return list;
    }

    @Click(R.id.bottom_add_btn)
    void onClickAddBtn(){
        kickWidgetDialog(null, TEMPLATE_ADD, CALLBACK_TEMPLATE_ADD, EditTemplateFragment.this);
    }

    @Click(R.id.bottom_edit_btn)
    void onClickEditBtn(){
        kickWidgetDialog(null, TEMPLATE_EDIT, CALLBACK_TEMPLATE_EDIT, EditTemplateFragment.this);
    }

    private void setView(RecordData data, int dataNum){
        RecordVpItem vpItem;
        switch (data.dataType){
            case 1:
                vpItem = new RecordVpItemTime(data, dataNum, this);
                break;
            case 2:
                vpItem = new TempItemTagPool(this, dataNum);
                break;
            case 3:
                vpItem = new RecordVpItemParam(list.get(dataNum), dataNum, null, this);
                break;
            case 4:
                vpItem = new RecordVpItemComment(list.get(dataNum), dataNum, null, this, this);
                break;
            default:
                throw new IllegalArgumentException("ふぁｆじゃｆｋじゃｌｆじゃｌ");
        }

        uiList.add(vpItem);
        ll.addView(vpItem.buildView(), ll.getChildCount()-1);
    }

    void setTagsView(final int dataNum){
        TempItemTagPool pool = new TempItemTagPool(this, dataNum);
        uiList.add(pool);
        ll.addView(pool.buildView(), ll.getChildCount()-1);
    }

    void setCommentView(final int dataNum){
        RecordVpItemComment comment = new RecordVpItemComment(list.get(dataNum), dataNum, null, this, this);
        uiList.add(comment);
        ll.addView(comment.buildView(), ll.getChildCount()-1);
    }

    private void setParamsView(final int dataNum){
        RecordVpItemParam param = new RecordVpItemParam(list.get(dataNum), dataNum, null, this);
        uiList.add(param);
        ll.addView(param.buildView(), ll.getChildCount()-1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: fire");
        if (resultCode != Activity.RESULT_OK)
            return;

        Bundle bundle = data.getExtras();
        switch (requestCode){
            case RecordRVAdapter.CALLBACK_LONGTAP:{
                Log.d(TAG, "onActivityResult: RecordRVAdapter.CALLBACK_LONGTAP");
                int index = bundle.getInt(RecordRVAdapter.INDEX);
                int key1 = bundle.getInt(RecordRVAdapter.KEY, 100);
                if (timeAdapterTree == null || key1 == 100 || !timeAdapterTree.containsKey(key1))
                    break;
                RecordRVAdapter rvAdapter1 = timeAdapterTree.get(key1);
                rvAdapter1.deleteItem(index, key1);//テンプレ書き換えはこのメソッド内で行う
                break;}

            /*ここからデバッグ済*/
            case Util.CALLBACK_TEMPLATE_PARAMS_NAME:
            case RecordVPAdapter.CALLBACK_TAGVIEW_NAME:
            case RecordVPAdapter.CALLBACK_COMMENT_NAME: {
                updateDateName(bundle);
                break;}
            case RecordVPAdapter.CALLBACK_COMMENT:{
                updateComment(bundle);
                break;}

            case Util.CALLBACK_TEMPLATE_TIME_COLOR: {
                boolean isNewItem = bundle.getBoolean(RecordRVAdapter.ITEM_ADD, false);
                int key = bundle.getInt(RecordRVAdapter.KEY, 100);
                if (timeAdapterTree == null || !timeAdapterTree.containsKey(key) || key == 100)
                    break;
                RecordRVAdapter rvAdapter = timeAdapterTree.get(key);
                if (isNewItem) {
                    if (rvAdapter != null)
                        rvAdapter.postAddItem(bundle, key);//テンプレ書き換えはこのメソッド内で行う
                    bundle.putBoolean(RecordRVAdapter.ITEM_ADD, false);//元に戻す
                } else {
                    rvAdapter.changeData(bundle, key, this);//テンプレ書き換えはこのメソッド内で行う
                }
//                RecordRVAdapter adapter = timeAdapterTree.get(bundle.getInt(RecordRVAdapter.KEY));
//                int key = bundle.getInt(RecordRVAdapter.KEY, 100);
//                if (timeAdapterTree == null || key == 100 || !timeAdapterTree.containsKey(key))
//                    break;
//                adapter.updateCircle(bundle, key);//テンプレ書き換えはこのメソッド内で行う
                break;
            }
            case CALLBACK_TEMPLATE_ADD:{
                addNewWidget(bundle);
                break;}
            case CALLBACK_TEMPLATE_EDIT:{
                onEditWidget(bundle);
                break;}

            case RecordVPAdapter.CALLBACK_TAG_ITEM:{
                String val = bundle.getString(RecordVPAdapter.TAG_ITEM);
                if (val == null){//Dialog側でnullCheckしているので、多分ここにはこないはず
                    onError(this, TAG+"bundle.getString(RecordVPAdapter.TAG_ITEM", R.string.error);
                    return;
                }

                int dataNum = bundle.getInt(RecordVPAdapter.DATA_NUM);
                TempItemTagPool tagPool = (TempItemTagPool) uiList.get(dataNum);
                int tagNum = getList().get(dataNum).data.size();//新しくタグを追加するので、data.size()がtagNumとなる
                tagPool.inputTag(val, tagNum);
                updateMap(dataNum, tagNum, val);

                boolean success = applyTemplate(list, getContext());
                toast(getContext(), success, R.string.succeed_adding_tag, R.string.error);
                break;}

            case Util.CALLBACK_TEMPLATE_TAG_EDIT:{
                String val = bundle.getString(Util.TEMPLATE_TAG_EDIT);
                if (val == null){//Dialog側でnullCheckしているので、多分ここにはこないはず
                    onError(this,  TAG +"val == null" , R.string.error);
                    return;
                }

                int tagNum = bundle.getInt(Integer.toString(R.id.data_num));
                int dataNum = bundle.getInt(RecordVPAdapter.DATA_NUM);
                updateMap(dataNum, tagNum, val);

                TempItemTagPool tagPool = (TempItemTagPool)uiList.get(dataNum);
                tagPool.updateTag(tagNum, val);

                boolean b = applyTemplate(list, getContext());
                toast(getContext(), b, R.string.succeed_edit_tag, R.string.error);
                break;}

            case Util.CALLBACK_TEMPLATE_PARAMS_ITEM:{
                int dataNum = bundle.getInt(RecordVPAdapter.DATA_NUM, 100);
                int plotPos = bundle.getInt(RecordParamsRVAdapter.INDEX, 100);
                String[] newStr = bundle.getStringArray(PARAMS_VALUES);
                String joinedNewStr = Util.joinArr(newStr, delimiter);

                if (dataNum == 100 || plotPos == 100){
                    onError(this, TAG+"dataNum == 100 || index == 100", R.string.error);
                    return;
                }

                updateMap(dataNum, plotPos, joinedNewStr);

                RecordVpItemParam itemParam = (RecordVpItemParam) uiList.get(dataNum);
                itemParam.updateItemValue(plotPos, newStr);

                boolean b = applyTemplate(list, getContext());
                if (!b)
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();

                break;}

            case Util.CALLBACK_TEMPLATE_PARAMS_SLIDER_MAX:{
                int dataNum = bundle.getInt(RecordVPAdapter.DATA_NUM);
                int plotNum = bundle.getInt(Util.TEMPLATE_PARAMS_SLIDER_MAX);
                String[] strings = bundle.getStringArray(PARAMS_VALUES);
                String key = Integer.toString(plotNum);
                String value = Util.joinArr(strings, delimiter);
                list.get(dataNum).data.put(key, value);

                RecordVpItemParam itemParam = (RecordVpItemParam) uiList.get(dataNum);
                itemParam.updateItemValue(plotNum, strings);

                boolean b = applyTemplate(list, getContext());
                if (!b)
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
                break;}
            case Util.CALLBACK_TEMPLATE_PARAMS_ADD:{
                int dataNum = bundle.getInt(RecordVPAdapter.DATA_NUM);
                RecordVpItemParam itemParam = (RecordVpItemParam) uiList.get(dataNum);
                itemParam.addItem(bundle);
                break;}
            case CALLBACK_ITEM_CLICK:
                kickCircleAndInputDialog(DIALOG_TAG_ITEM_CLICK2, CALLBACK_ITEM_CLICK2, bundle, this);
                break;
            case CALLBACK_ITEM_CLICK2:
                updateTimeEventTime(data);
                break;
            case CALLBACK_RANGE_CLICK_TIME:
            case CALLBACK_RANGE_CLICK_VALUE:
                updateTimeRange(data, requestCode);
            case CALLBACK_ITEM_ADD2:
                addItem(data);
            case CALLBACK_RANGE_COLOR:
                updateComment(data);
                break;
        }
    }

    private void syncTimeDataMapAndLocal(int dataNum, RecordVpItemTime itemTime){
        syncTimeDataMap(list, dataNum, itemTime);
        boolean b = applyTemplate(list, getContext());
        if (!b)
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
    }

    public void updateTimeEventTime(@NonNull Intent data){
        TimeEvent timeEvent = (TimeEvent) data.getSerializableExtra(TIME_EVENT);
        int dataNum = data.getIntExtra(DATA_NUM, Integer.MAX_VALUE);
        int rvPos = data.getIntExtra(RV_POS, Integer.MAX_VALUE);
        if (dataNum == Integer.MAX_VALUE || rvPos == Integer.MAX_VALUE){
            onError(this, "dataNum == Integer.MAX_VALUE || rvPos == Integer.MAX_VALUE", R.string.error);
            return;
        }

        RecordVpItemTime itemTime = (RecordVpItemTime) uiList.get(dataNum);
        itemTime.updateTime(rvPos, timeEvent);

        syncTimeDataMapAndLocal(dataNum, itemTime);
    }

    public void updateTimeRange(@NonNull Intent data, @RecordVPAdapter.UpdateCode int requestCode){
        TimeEvent timeEvent = (TimeEvent) data.getSerializableExtra(TIME_EVENT);
        int dataNum = data.getIntExtra(DATA_NUM, Integer.MAX_VALUE);
        int pos = data.getIntExtra(POSITION, Integer.MAX_VALUE);
        int posInList = data.getIntExtra(POS_IN_LIST, Integer.MAX_VALUE);
        if (dataNum == Integer.MAX_VALUE || pos == Integer.MAX_VALUE || posInList == Integer.MAX_VALUE){
            onError(this, "dataNum == Integer.MAX_VALUE || pos == Integer.MAX_VALUE || posInList == Integer.MAX_VALUE", R.string.error);
            return;
        }

        RecordVpItemTime itemTime = (RecordVpItemTime) uiList.get(dataNum);
        switch (requestCode){
            case CALLBACK_RANGE_CLICK_TIME:
                itemTime.updateRangeTime(timeEvent, pos, posInList);
                break;
            case CALLBACK_RANGE_CLICK_VALUE:
                itemTime.updateRangeValue(timeEvent, pos, posInList);
                break;
        }

        syncTimeDataMapAndLocal(dataNum, itemTime);
    }

    public void addItem(@NonNull Intent data){
        TimeEvent timeEvent = (TimeEvent) data.getSerializableExtra(TIME_EVENT);
        int dataNum = data.getIntExtra(DATA_NUM, Integer.MAX_VALUE);
        if (dataNum == Integer.MAX_VALUE){
            onError(this, "dataNum == Integer.MAX_VALUE", R.string.error);
            return;
        }

        RecordVpItemTime itemTime = (RecordVpItemTime) uiList.get(dataNum);
        itemTime.addItem(timeEvent);

        syncTimeDataMapAndLocal(dataNum, itemTime);
    }

    public void updateComment(@NonNull Intent data) {
        String comment = data.getStringExtra(RecordVPAdapter.COMMENT);
        int dataNum = data.getIntExtra(DATA_NUM, Integer.MAX_VALUE);
        if (comment == null || dataNum == Integer.MAX_VALUE) {
            onError(this, "comment == null || dataNum == Integer.MAX_VALUE", R.string.error);
            return;
        }

        updateCommentMap(list, dataNum, comment);
        RecordVpItemComment itemComment = (RecordVpItemComment) uiList.get(dataNum);
        itemComment.updateComment();

        boolean b = applyTemplate(list, getContext());
        if (!b)
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
    }

    private void addNewWidget(Bundle bundle){
        int cardInt = bundle.getInt(Util.CARD_INT);

        RecordData dataT;
        switch (cardInt){
            case 0:{
                dataT = new RecordData();
                dataT.dataType = 4;
                dataT.dataName = " ";
                dataT.data = new HashMap<>();
                dataT.data.put("comment", null);
                list.add(dataT);
                setCommentView(list.size()-1);
                break;}
            case 1:
                dataT = new RecordData();
                dataT.dataType = 2;
                dataT.dataName = " ";
                list.add(dataT);
                setTagsView(list.size()-1);
                break;
            case 2:
                dataT = new RecordData();
                dataT.dataType = 3;
                dataT.dataName = " ";
                dataT.data = new HashMap<>();
                dataT.data.put("0",  "0" + delimiter +"項目1"+ delimiter +"false");
                dataT.data.put("1", "1"+ delimiter +"項目2"+ delimiter +"3"+ delimiter +"5");
                list.add(dataT);
                setParamsView(list.size()-1);
        }

        applyTemplate(list, getContext());
    }

    private void onEditWidget(Bundle bundle){
        Log.d(TAG, "onEditWidget() called with: bundle = [" + bundle + "]");
        boolean b = bundle.getBoolean(APPLY_TEMP_IS_SUCCESS);
        if (!b) {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
        } else {
            uiList = new ArrayList<>();
            int limit = ll.getChildCount()-1;
            for (int i = 0; i < limit; i++)
                ll.removeViewAt(0);
            afterViews();
            rootView.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    private void updateMap(int dataNum, int tagNum, String val){
        if (getList().get(dataNum).data == null)
            getList().get(dataNum).data = new HashMap<>();
        getList().get(dataNum).data.put(Integer.toString(tagNum), val);
    }

    private void updateDateName(Bundle bundle){
        String newName = bundle.getString(RecordVPAdapter.NAME, null);
        int dataNum = bundle.getInt(RecordVPAdapter.DATA_NUM, 100);
        if (newName == null || dataNum == 100){
            onError(this, TAG+"newName == null || dataNum == 100", R.string.error);
            return;
        }

        Object obj = uiList.get(dataNum);
        if (obj instanceof RecordVpItemParam)
            ((RecordVpItemParam)obj).updateName(newName);
        else if (obj instanceof RecordVpItemComment)
            ((RecordVpItemComment)obj).updateName(newName);
        else if (obj instanceof TempItemTagPool)
            ((TempItemTagPool)obj).updateName(newName);
        list.get(dataNum).setDataName(newName);
        boolean b = applyTemplate(list, getContext());
        toast(getContext(), b, R.string.succeed_edit_name, R.string.error);
    }

    private void updateComment(Bundle bundle){
        String newComment = bundle.getString(RecordVPAdapter.COMMENT);
        int dataNum = bundle.getInt(RecordVPAdapter.DATA_NUM, 100);
        if (dataNum == 100){
            onError(this, TAG+"newName == null || dataNum == 100", R.string.error);
            return;
        }

        RecordVpItemComment comment = (RecordVpItemComment) uiList.get(dataNum);
        comment.updateComment();
        updateCommentMap(list, dataNum, newComment);

        boolean b = applyTemplate(list, getContext());
        if (!b)
            onError(this, TAG+"!b", R.string.error);
    }

    @Override
    public void onClickCommentName(int dataNum) {
        Bundle bundle = DialogKicker.makeBundleInOnClick(null, RecordVPAdapter.COMMENT_NAME, dataNum);
        bundle.putString(RecordVPAdapter.NAME, list.get(dataNum).dataName);//Nullでありうる
        kickInputDialog(bundle, RecordVPAdapter.COMMENT_NAME, RecordVPAdapter.CALLBACK_COMMENT_NAME, this);
    }

    @Override
    public void onClickCommentEdit(int dataNum, String comment) {
        Bundle bundle = DialogKicker.makeBundleInOnClick(null, RecordVPAdapter.COMMENT, dataNum);
        bundle.putString(RecordVPAdapter.COMMENT, comment);
        bundle.putString(RecordVPAdapter.COMMENT_NAME, list.get(dataNum).dataName);//Nullでありうる
        kickInputDialog(bundle, RecordVPAdapter.COMMENT, RecordVPAdapter.CALLBACK_COMMENT, this);
    }

    public void onClickTagPoolName(int dataNum){
        Bundle bundle = DialogKicker.makeBundleInOnClick(null, RecordVPAdapter.TAGVIEW_NAME, dataNum);
        bundle.putString(RecordVPAdapter.NAME, list.get(dataNum).dataName);
        kickInputDialog(bundle, RecordVPAdapter.TAGVIEW_NAME, RecordVPAdapter.CALLBACK_TAGVIEW_NAME, this);
    }

    public void onClickTagPoolAdd(int dataNum){
        Bundle bundle = DialogKicker.makeBundleInOnClick(null, RecordVPAdapter.TAG_ITEM, dataNum);
        bundle.putSerializable(RecordDiaogFragmentTag.RECORD_DATA, getList().get(dataNum));
        RecordDiaogFragmentTag dialog = RecordDiaogFragmentTag.newInstance(bundle);
        dialog.setTargetFragment(this, RecordVPAdapter.CALLBACK_TAG_ITEM);
        dialog.show(this.getActivity().getSupportFragmentManager(), RecordVPAdapter.TAG_ITEM);
    }

    public void onClickTag(int tagNum, int dataNum, String value) {
        Bundle bundle = DialogKicker.makeBundleInOnClick(null, Util.TEMPLATE_TAG_EDIT, dataNum);
        bundle.putInt(Integer.toString(R.id.data_num), tagNum);
        bundle.putString(Integer.toString(R.id.data_txt), value);
        bundle.putSerializable(RecordDiaogFragmentTag.RECORD_DATA, getList().get(dataNum));

        RecordDiaogFragmentTag dialog = RecordDiaogFragmentTag.newInstance(bundle);
        dialog.setTargetFragment(this, Util.CALLBACK_TEMPLATE_TAG_EDIT);
        dialog.show(this.getActivity().getSupportFragmentManager(), Util.TEMPLATE_TAG_EDIT);
    }

    @Override
    public void onClickParamsName(int dataNum) {
        Bundle bundle = DialogKicker.makeBundleInOnClick(null, RecordVPAdapter.TAGVIEW_NAME, dataNum);
        bundle.putString(RecordVPAdapter.NAME, list.get(dataNum).dataName);
        kickInputDialog(bundle, Util.TEMPLATE_PARAMS_NAME, Util.CALLBACK_TEMPLATE_PARAMS_NAME, this);
    }

    @Override
    public void onClickParamsAddBtn(int dataNum) {
        Bundle bundle = DialogKicker.makeBundleInOnClick(null, Util.TEMPLATE_PARAMS_ADD, dataNum);
        bundle.putInt(Util.PARAM_ITEM_LEN, list.size());
        ArrayList<String> strings = new ArrayList<>();
        HashMap<String, Object> data = list.get(dataNum).data;
        if (data != null && !data.isEmpty())
            for (Object value: list.get(dataNum).data.values()) {
                String str = ((String)value).split(delimiter)[1];
                strings.add(str);
            }
        bundle.putStringArrayList(PARAMS_VALUES, strings);
        kickDialogInOnClick(Util.TEMPLATE_PARAMS_ADD, Util.CALLBACK_TEMPLATE_PARAMS_ADD, bundle, EditTemplateFragment.this);
    }

    public void onClickRemoveTagBtn(int dataNum, int tagNum) {
        //todo これタグを削除する前に、「タグを削除しますか？」っていうDialogを挟むべきでは？
        TempItemTagPool tagPool = (TempItemTagPool) uiList.get(dataNum);
        tagPool.removeTag(tagNum);
        removeItemFromData(getList().get(dataNum), Integer.toString(tagNum));

        boolean b = applyTemplate(getList(), getContext());
        toast(getContext(), b, R.string.remove_tag_toast, R.string.error);
    }

    @Contract("null, _ -> false")
    static boolean removeItemFromData(@Nullable RecordData data, @NonNull String key){
        if (data == null || data.data == null || data.data.isEmpty() || !data.data.containsKey(key))
            return false;

        data.data.remove(key);

        List<Object> list = new LinkedList<>();
        for (Map.Entry entry : data.data.entrySet()) {
            list.add(entry.getValue());
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            hashMap.put(Integer.toString(i), list.get(i));
        }

        data.data = hashMap;

        return true;
    }
}
