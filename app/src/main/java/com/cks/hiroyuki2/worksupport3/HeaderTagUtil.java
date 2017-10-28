/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * TreeMapをまわりを保持するクラス。TreeMapはCalenderFragment, RecordVPAdapterで使用される。
 */

public class HeaderTagUtil {

    private static final String TAG = "MANUAL_TAG: " + HeaderTagUtil.class.getSimpleName();
    public HashMap<Integer, HashMap<String, String>> arrayMap = new HashMap<>();//keyにはyearMonを代入する。
    static private HeaderTagUtil htd = new HeaderTagUtil();

    public static HeaderTagUtil getInstance() {
        return htd;
    }

    @Nullable
    public String retrieveRecordData(Calendar cal){
        Log.d(TAG, "retrieveRecordData: fire");
        int ym = Integer.parseInt(Util.cal2date(cal, Util.DATE_PATTERN_YM));
        String dayStr = Integer.toString(cal.get(Calendar.DATE));

        if (arrayMap == null || arrayMap.isEmpty() || !arrayMap.containsKey(ym)){
            Log.w(TAG, "arrayMap == null || arrayMap.isEmpty() || !arrayMap.containsKey(ym)");
            return null;
        }

        HashMap<String, String> hashMap = arrayMap.get(ym);
        if (hashMap == null || hashMap.isEmpty() || !hashMap.containsKey(dayStr)){
            Log.w(TAG, "retrieveRecordData: hashMap == null || hashMap.isEmpty()");
            return null;
        }

        return hashMap.get(dayStr);
    }

    public boolean isYmDataExist(Calendar cal){
        Log.d(TAG, "retrieveRecordMonData() called with: cal = [" + cal + "]");
        int ym = Integer.parseInt(Util.cal2date(cal, Util.DATE_PATTERN_YM));

        return (arrayMap != null && !arrayMap.isEmpty() && arrayMap.containsKey(ym));
    }

    public boolean makeNewHeaderTag(@NonNull String key, @NonNull String txt, View v, View.OnClickListener listener, Context context){
        TextView tv = (TextView) v.findViewById(R.id.tv);
        tv.setOnClickListener(listener);
        ImageView iv = (ImageView) v.findViewById(R.id.remove);
        iv.setOnClickListener(listener);
//        View view = (View)tv.getParent();
        v.setTag(R.id.data_num, key);
        v.setTag(R.id.data_txt, txt);

        String shownStr = txt.substring(0, txt.length()-1);
        tv.setText(shownStr);
        int n = Integer.parseInt(txt.substring(txt.length()-1));
        if (n >= Util.colorId.size())//todo 色指定とかどうするの？？
            return false;

//        LinearLayout cardView = v.findViewById(R.id.container);
        CardView cv = (CardView) v.findViewById(R.id.card_container);
        cv.setCardBackgroundColor(ContextCompat.getColor(context, Util.colorId.get(n)));
        return true;
    }

    /**
     *
     * @param string
     * @param context
     * @param listener
     * @param addedView LinearLayoutまたはFlowLayoutが代入されます。これらは共にViewGroupを親クラスに持つため、このようなキャストが行えることに注意してください。
     */
    public void addTag(@NonNull String string, Context context, View.OnClickListener listener, View addedView){
        Log.d(TAG, "addTag() called with: string = [" + string + "], context = [" + context + "], listener = [" + listener + "], addedView = [" + addedView + "]");
        
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (string.contains(FirebaseConnection.delimiter)){
            String[] strings = string.split(FirebaseConnection.delimiter);
            for (int i=0; i<strings.length; i++){
                View v = inflater.inflate(R.layout.record_vp_item_headertag_tag, null);
                if (makeNewHeaderTag(Integer.toString(i), strings[i], v, listener, context)){
                    ((ViewGroup)addedView).addView(v);
                }
            }
        } else {
            //デリミタが存在しない⇒tagはひとつだけ
            View v = inflater.inflate(R.layout.record_vp_item_headertag_tag, null);
            if (makeNewHeaderTag("0", string, v, listener, context)){
                ((ViewGroup)addedView).addView(v);
            }
        }
    }

    /**
     * tagBox LinearLayoutまたはFlowLayoutが代入されます。これらは共にViewGroupを親クラスに持つため、このようなキャストが行えることに注意してください。
     * ⇒flowレイアウトのみに変更しました
     */
    public void removeTag(View view, Calendar cal, View currentPage){
        String dataNum = (String) ((View) view.getParent().getParent().getParent()).getTag(R.id.data_num);//もしもheadertag_tag.xmlのレイアウトを変更したら、ここも変更しなければならない。
        Log.d(TAG, "onClick: " + dataNum);
        ViewGroup tagBox = (ViewGroup) currentPage.findViewById(R.id.tag_box);
        Log.w(TAG, "onClick: getChildCount" + tagBox.getChildCount());
        tagBox.removeViewAt(Integer.parseInt(dataNum));
//        if (currentPage.findViewById(R.id.tag_box) instanceof FlowLayout){//これはRecordVPAからこのメソッドがコールされていることを表します
//            setFlowLayoutCenter((FlowLayout) tagBox, currentPage.getContext());
//        }

        String string = HeaderTagUtil.getInstance().retrieveRecordData(cal);
        if (string == null) return;
        Log.d(TAG, "onClick: " + string);
        if (string.contains(FirebaseConnection.delimiter)){
            String[] strings = string.split(FirebaseConnection.delimiter);
            List<String> list = new ArrayList<>(Arrays.asList(strings));
            list.remove(Integer.parseInt(dataNum));
            StringBuilder bf = new StringBuilder();
            for (String item: list) {
                bf.append(item);
                bf.append(FirebaseConnection.delimiter);
            }
            bf.delete(bf.length() - FirebaseConnection.delimiter.length(), bf.length());//末尾のデリミタを削除する
            string = bf.toString();
            Log.d(TAG, "onClick: " + string);

            //tagにtagを振りなおす
            for (int i=0; i<list.size(); i++){
                tagBox.getChildAt(i).setTag(R.id.data_num, Integer.toString(i));
            }
        } else {
            string = null;
        }
        String ymStr = Util.cal2date(cal, Util.DATE_PATTERN_YM);
        int ym = Integer.parseInt(ymStr);
        String day1 = Integer.toString(cal.get(Calendar.DATE));
        HeaderTagUtil.getInstance().arrayMap.get(ym).put(day1, string);

        Toast.makeText(view.getContext(), "タグを削除しました", Toast.LENGTH_LONG).show();

        FirebaseConnection.getInstance().userParamSeries.child(ymStr).child(day1).setValue(string, FirebaseConnection.getInstance());
    }

    public void updateTag(Bundle bundle, String bundleCommand, Context context, Calendar cal, View.OnClickListener listener, View currentPage){
        Log.d(TAG, "updateHeaderTag: " + bundle.toString());
        String text1 = bundle.getString(bundleCommand);
        if (text1 == null) return;
        String num = bundle.getString(Integer.toString(R.id.data_num));
        if (num == null) return;
        Log.d(TAG, "onActivityResult: CALLBACK_EDIT_TAG" + text1);

        ViewGroup tagBox = (ViewGroup) currentPage.findViewById(R.id.tag_box);
        View tag = tagBox.getChildAt(Integer.parseInt(num));
        TextView tv = (TextView) tag.findViewById(R.id.tv);
        tv.setOnClickListener(listener);
        ImageView iv = (ImageView) tag.findViewById(R.id.remove);
        iv.setOnClickListener(listener);
//        View view = (View)tv.getParent();
        HeaderTagUtil.getInstance().makeNewHeaderTag(num, text1, tag, listener, context);

        String text2 =  HeaderTagUtil.getInstance().retrieveRecordData(cal);
        if (text2 == null){
            text2 = text1;
        } else {
            String[] strings = text2.split(FirebaseConnection.delimiter);
            strings[Integer.parseInt(num)] = text1;
            StringBuilder sb = new StringBuilder();
            for (String string : strings) {
                sb.append(string);
                sb.append(FirebaseConnection.delimiter);
            }
            sb.delete(sb.length() - FirebaseConnection.delimiter.length(), sb.length());//末尾のデリミタを削除する
            text2 = sb.toString();
        }
        String ymStr1 = Util.cal2date(cal, Util.DATE_PATTERN_YM);
        int ym1 = Integer.parseInt(Util.cal2date(cal, Util.DATE_PATTERN_YM));
        String day3 = Integer.toString(cal.get(Calendar.DATE));
        HeaderTagUtil.getInstance().arrayMap.get(ym1).put(day3, text2);

        FirebaseConnection.getInstance().userParamSeries.child(ymStr1).child(day3).setValue(text2, FirebaseConnection.getInstance());
    }

//    void setFlowLayoutCenter(FlowLayout tagBox, Context context){
//        if (tagBox.getChildCount() == 1){//1は+ボタンの分
//            int padding = context.getResources().getDimensionPixelSize(R.dimen.record_header_addbtn_padding);
//            tagBox.setPadding(padding, 0, padding, 0);
//        }
//    }

//    void setFlowLayoutWide(FlowLayout tagBox){
//        if (tagBox.getChildCount() >1 && tagBox.getPaddingLeft() != 0){
//            tagBox.setPadding(0, 0, 0, 0);
//        }
//    }
}
