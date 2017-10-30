/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cks.hiroyuki2.worksupprotlib.FirebaseConnection;
import com.cks.hiroyuki2.worksupprotlib.FirebaseEventHandler;
import com.cks.hiroyuki2.worksupport3.Fragments.RecordFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.cks.hiroyuki2.worksupport3.RecordDataUtil;
import com.cks.hiroyuki2.worksupport3.RecordRVAdapter;
import com.cks.hiroyuki2.worksupport3.RecordUiOperator;
import com.cks.hiroyuki2.worksupprotlib.TemplateEditor;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_ADD2;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_CLICK2;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.CALLBACK_RANGE_CLICK_TIME;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.CALLBACK_RANGE_CLICK_VALUE;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;
import static com.cks.hiroyuki2.worksupport3.RecordUiOperator.makeBundleInOnClick;
import static com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemTime.CALLBACK_RANGE_COLOR;
import static com.cks.hiroyuki2.worksupprotlib.Util.DATE_PATTERN_YM;
import static com.cks.hiroyuki2.worksupprotlib.Util.DEFAULT;
import static com.cks.hiroyuki2.worksupprotlib.Util.cal2date;
import static com.cks.hiroyuki2.worksupprotlib.Util.date2Cal;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;

/**
 * VP関係のadapterおじさん！ロジックを持っちゃう！
 */
public class RecordVPAdapter extends PagerAdapter {

    private final static int PAGE_NUM = RecordTabVPAdapter.PAGE_NUM * 7;
    public static int MED_NUM = PAGE_NUM/2;
    public final static int DATALIST_DEF_LENGTH = 11;
    public RecordFragment fragment;
    public LayoutInflater inflater;
    private Calendar calMed;
    private static final String TAG = "MANUAL_TAG: " + RecordVPAdapter.class.getSimpleName();
    private TreeMap<Integer, TreeSet<Integer>> indexTree = new TreeMap<>();//indexTreeは空でありえない()
    private String templateCode = null;

    public TreeMap<Integer, TreeMap<Integer, RecordRVAdapter>> timeAdapterTree = new TreeMap<>();//外側はdateInt, 内側はdataNumを表す
    public HashMap<Integer, HashMap<String, String>> arrayMap = new HashMap<>();
    private ArrayMap<Integer, RecordUiOperator> pageMap = new ArrayMap<>();

    //region static member
    public final static int CALLBACK_TAGVIEW_NAME = 400;
    public final static String TAGVIEW_NAME = "TAGVIEW_NAME";
    public final static String NAME = "NAME";
    public final static String DATA_NUM = "DATA_NUM";
    public final static String PAGE_TAG = "PAGE_TAG";
    public final static int CALLBACK_TAG_ITEM = 410;
    public final static String TAG_ITEM = "TAG_ITEM";
    public final static String TAG_ADD = "TAG_ADD";
    public final static int CALLBACK_TAG_ADD = 411;
    public final static String COMMENT = "COMMENT";
    public final static int CALLBACK_COMMENT = 420;
    public final static String COMMENT_NAME = "COMMENT_NAME";
    public final static int CALLBACK_COMMENT_NAME = 421;
    public final static String HEADER_TAG_ADD = "HEADER_TAG_ADD";
    public final static int CALLBACK_HEADER_TAG_ADD = 430;
    public final static String HEADER_TAG_VALUE = "HEADER_TAG_VALUE";
    public final static String HEADER_TAG_EDIT = "HEADER_TAG_EDIT";
    public final static int CALLBACK_HEADER_TAG_EDIT = 440;
    public boolean isInitilizedData = false;
//    final static String HEADER_TAG_LIST = "HEADER_TAG_LIST";
    //endregion

    public View currentPage;
    private List<Boolean> isInitOneDayData = new ArrayList<>();
    private List<Boolean> isInitHeaderTagData = new ArrayList<>();
    private RecordDataUtil rdu;
    private int currentPos;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {CALLBACK_RANGE_CLICK_TIME, CALLBACK_RANGE_CLICK_VALUE})
    public @interface UpdateCode {}

    public RecordVPAdapter(Calendar calMed, RecordFragment fragment){
//        RecordUiUtil.getInstance().setAdapter(this);
        this.fragment = fragment;
        inflater = (LayoutInflater)fragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.calMed = calMed;
        rdu = RecordDataUtil.getInstance();
    }

    //region 初期化系列メソッド
    /*初期化系列メソッドここから*/
    public void initData(){
        String calMedStr = cal2date(calMed, Util.datePattern);
        try {
            this.calMed = date2Cal(calMedStr, Util.datePattern);
        } catch (ParseException e) {
            logStackTrace(e);/*エラー処理*/
        }

        getTemplate();

        Calendar calTemp = Calendar.getInstance();
        calTemp.set(calMed.get(Calendar.YEAR), calMed.get(Calendar.MONTH), calMed.get(Calendar.DATE));
        retrieveData(calTemp);
    }

    public void retrieveData(Calendar calTemp){
        Date date = calTemp.getTime();
        calTemp.add(Calendar.DATE, -DATALIST_DEF_LENGTH/2);
        for (int i=0; i<DATALIST_DEF_LENGTH; i++){
            makeOneDayDataList(calTemp);
            calTemp.add(Calendar.DATE, 1);
        }

        calTemp = Calendar.getInstance();
        calTemp.setTime(date);
        retrieveOneMonthHeaderTag(calTemp);
        calTemp.add(Calendar.MONTH, -1);
        retrieveOneMonthHeaderTag(calTemp);
        calTemp.add(Calendar.MONTH, +2);
        retrieveOneMonthHeaderTag(calTemp);
    }

    private void getTemplate(){
        Log.d(TAG, "getTemplate: fire");
        final SharedPreferences pref = fragment.getContext().getSharedPreferences(Util.PREF_NAME, Context.MODE_PRIVATE);
        templateCode = pref.getString(Util.PREF_KEY_TEMPLATE, DEFAULT);

        final DatabaseReference ref = FirebaseConnection.getInstance().userAttrDir.child("template");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    Log.w(TAG, "onDataChange: templateノードがありませんよ");
                    ref.setValue(DEFAULT, FirebaseConnection.getInstance());//書き込みに失敗したとしても、それはデフォルト値なので、特段問題はない。
                } else {
                    templateCode = (String) dataSnapshot.getValue();
                    Log.d(TAG, "onDataChange: template:" + templateCode);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Util.PREF_KEY_TEMPLATE, templateCode);
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: templateノード取得できませんでした " + databaseError.getMessage());
            }
        });
    }
    /*初期化系列メソッドここまで*/
    //endregion

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentPage = (View) object;
        currentPos = position;
        Log.d(TAG, "setPrimaryItem: " + currentPage.getTag().toString());
        super.setPrimaryItem(container, position, object);
    }

    public int findPosition(Calendar cal){
        Log.d(TAG, "findPosition()" + cal2date(cal, Util.datePattern));
        int diffDays =  (int) TimeUnit.MILLISECONDS.toDays(cal.getTimeInMillis() - calMed.getTimeInMillis());
        return MED_NUM + diffDays;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return PAGE_NUM;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        pageMap.remove(position);
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem: fire");
        View v =  inflater.inflate(R.layout.record_vp_item_frame, null);
        Calendar cal = getTodayCal(position);

        //このitemの日付(cal)を"20170415"のようなintに変換し、タグ付けする。
        int date = Integer.parseInt(cal2date(cal, Util.datePattern));
        v.setTag(date);

        LinearLayout ll = v.findViewById(R.id.item_ll);

        if (rdu.dataMap.containsKey(date) && rdu.dataMap.get(date) != null && !rdu.dataMap.get(date).isEmpty()){
            Log.d(TAG, "instantiateItem: dataMap.containsKey");
            List<RecordData> list = rdu.dataMap.get(date);
            if (list == null || list.isEmpty())
                switchTemplateCode(ll, position);
            else
                initRecordViewUtil(list, ll, position, fragment);
        } else
            //templateCode != nullは必ず満たされる。なぜなら、このinstantiateItem()の前にgetTemplate()内でtemplateCodeに値を代入しているから。
            switchTemplateCode(ll, position);

        container.addView(v);
        return v;
    }

    //region instantiateItem()付属メソッド
    /**calMedとpositionの値からこのitemの日付を算出*/
    private Calendar getTodayCal(int position){
        Calendar cal = Calendar.getInstance();
        cal.setTime(calMed.getTime());
        int diff = position - MED_NUM;
        cal.add(Calendar.DATE, diff);
        return cal;
    }

    private void switchTemplateCode(LinearLayout ll, int position){
        Log.d(TAG, "switchTemplateCode: fire");
        switch (templateCode){
            case "DEFAULT":
                List<RecordData> list = TemplateEditor.deSerialize(fragment.getContext());
                if (list == null) return;/*エラー処理*/
//                initRecordData(list, ll, cal);
                initRecordViewUtil(list, ll, position, fragment);
        }
    }
    //endregion

    private void initRecordViewUtil(List<RecordData> list, LinearLayout ll, int position, RecordFragment fragment){
        RecordUiOperator util2 = new RecordUiOperator(list, ll, getTodayCal(position), fragment);
        pageMap.put(position, util2);
        util2.initRecordData();
    }

    private void makeOneDayDataList(final Calendar cal){
        final String date = cal2date(cal, Util.datePattern);
        Log.d(TAG, "makeOneDayDataList: date:" + date);
        DatabaseReference ref = FirebaseConnection.getInstance().userRecDir.child(date);
        FirebaseEventHandler handler = new FirebaseEventHandler(cal) {
            @Override
            public void onOnDataChange(DataSnapshot dataSnapshot, boolean isSnapShotExist) {
                if (!isSnapShotExist || list.isEmpty()){//listはnullではありえず、かつ、listは空でありうることに注意してください。
                    list = null;
                }
                rdu.dataMap.put(Integer.parseInt(date), list);
                isInitOneDayData.add(true);
                if (checkIsFinishInitData())
                    kickCallBack();
            }

            @Override
            public void onOnCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onOnCancelled: " + databaseError.getMessage());
                isInitOneDayData.add(true);
                if (checkIsFinishInitData())
                    kickCallBack();
                rdu.dataMap.put(Integer.parseInt(date), null);
            }
        };
        handler.initValueEventListener();
        ref.addListenerForSingleValueEvent(handler.getListener());
    }

    private void hogehoge(final String date, Calendar cal, int index){
        Log.d(TAG, "hogehoge: fire");
        final int dateNum = Integer.parseInt(date);
        boolean isExist = indexTree.get(index).contains(cal.get(Calendar.DAY_OF_MONTH));
        if (isExist){
            DatabaseReference ref = FirebaseConnection.getInstance().userRecDir.child(date);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: fire");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: fire");
                }
            });

        } else {
            rdu.dataMap.put(dateNum, null);
        }
    }

    private void retrieveOneMonthHeaderTag(final Calendar cal){
        String ym = cal2date(cal, DATE_PATTERN_YM);
        final int ymStr = Integer.parseInt(ym);
        DatabaseReference ref = FirebaseConnection.getInstance().userParamSeries.child(ym);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    HashMap<String, String> hashMap = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        hashMap.put(child.getKey(), (String) child.getValue());
                    }
                    HeaderTagUtil.getInstance().arrayMap.put(ymStr, hashMap);
                }
                isInitHeaderTagData.add(true);
                if (checkIsFinishInitData())
                    kickCallBack();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: " + databaseError.getMessage());
                isInitHeaderTagData.add(true);
                if (checkIsFinishInitData())
                    kickCallBack();
            }
        });
    }

    private void kickCallBack(){
        if (isInitHeaderTagData.size() == 3 && isInitOneDayData.size() == DATALIST_DEF_LENGTH){/*ここいらなくね？*/
            isInitHeaderTagData = new ArrayList<>();
            isInitOneDayData = new ArrayList<>();
            if (!isInitilizedData){
                isInitilizedData = true;
                fragment.onPostInitData();
            } else {
                fragment.onPostUpdateData();
            }
        }
    }

    private boolean checkIsFinishInitData(){
        return isInitHeaderTagData.size() == 3 && isInitOneDayData.size() == DATALIST_DEF_LENGTH;
    }

    //region RecordFragment onPageSelected()参照先メソッド
    public void onPageSelected(Calendar cal){
        Log.d(TAG, "onPageSelected: fire");

        int year = cal.get(Calendar.YEAR);
        int mon = cal.get(Calendar.MONTH);
        cal.add(Calendar.DATE, -DATALIST_DEF_LENGTH/2);//前5日
        inOnPageSelected(cal, year, mon);
        cal.add(Calendar.DATE, DATALIST_DEF_LENGTH);//後5日
        inOnPageSelected(cal, year, mon);
    }

    private void inOnPageSelected(Calendar cal, int year, int mon){
        String ym = cal2date(cal, DATE_PATTERN_YM);
        int StrInt = Integer.parseInt(ym);
        makeOneDayDataList(cal);
        if ((cal.get(Calendar.YEAR) != year || cal.get(Calendar.MONTH) != mon) && !HeaderTagUtil.getInstance().arrayMap.containsKey(StrInt)){
            retrieveOneMonthHeaderTag(cal);
        }
    }
    //endregion

    public void callbackFragment(@NonNull Intent data, int requestCode){
        RecordUiOperator operator = pageMap.get(currentPos);
        switch (requestCode){
            case CALLBACK_ITEM_CLICK2:
                operator.updateTimeEventTime(data);
                break;
            case CALLBACK_RANGE_CLICK_TIME:
            case CALLBACK_RANGE_CLICK_VALUE:
                operator.updateTimeRange(data, requestCode);
                break;
            case CALLBACK_ITEM_ADD2:
                operator.addItem(data);
                break;
            case CALLBACK_RANGE_COLOR:
                operator.updateTimeRangeColor(data);
                break;
            case RecordVPAdapter.CALLBACK_COMMENT:
                operator.updateComment(data);
                break;
            case RecordVPAdapter.CALLBACK_TAG_ADD:
                operator.updateTagPool(data);
                break;
        }
    }

    @Nullable
    public List<RecordData> retrieveList(int dateInt){
        List<RecordData> list;
        if (rdu.dataMap.containsKey(dateInt)) {
            list = rdu.dataMap.get(dateInt);
            if (list == null || list.isEmpty()) {
                list = TemplateEditor.deSerialize(fragment.getContext());
            }
        } else
            list = TemplateEditor.deSerialize(fragment.getContext());

        return list;
    }

    public void syncDataMapAndFireBase(List<RecordData> list, String date) {
        FirebaseConnection fireBase = FirebaseConnection.getInstance();
        boolean isTemplate = fireBase.checkIsTemplateData(list, fragment.getContext());
        if (!isTemplate) {
            rdu.dataMap.put(Integer.parseInt(date), list);
            fireBase.userRecDir.child(date).setValue(list, fireBase);
        } else {
            rdu.dataMap.put(Integer.parseInt(date), null);
            fireBase.userRecDir.child(date).setValue(null, fireBase);
        }
    }
}
