/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupportlibrary;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupportlibrary.Entity.RecordData;
import com.cks.hiroyuki2.worksupportlibrary.Entity.TimeEvent;
import com.cks.hiroyuki2.worksupportlibrary.Entity.TimeEventDataSet;
import com.cks.hiroyuki2.worksupportlibrary.Entity.User;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Contract;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 便利屋おじさん！
 */
public class Util {
    public static final String datePattern = "yyyyMMdd";
    public static final String delimiter = "9mVSv";
    public static final String delimiterOfNum = ",";

    public final static float ELEVATION = 4;
    public final static String PREF_NAME = "pref";
    public final static String PREF_KEY_TEMPLATE = "template";
    public final static String PREF_KEY_FIRST_LAUNCH = "FIRST_LAUNCH";
    public final static String PREF_KEY_PW ="PREF_KEY_PW";
    public final static String PREF_KEY_PROF = "prof_params";
    public final static String PREF_KEY_TEMPLATE_DES = "template_des";
    public final static String PREF_KEY_COLOR = "color";
    public final static String PREF_KEY_START_OF_WEEK = "start_of_week";
    public  final static String PREF_KEY_PHOTO_UPLOADED = "photo_uploaded";//最初はこの値はfalseとなっている。一旦画像を設定すると、trueになる。
    public final static String PREF_KEY_WIDTH = "width";
    public final static String PREF_KEY_GROUP_USER_DATA = "group_in_userdata_node";
    public final static String TEMPLATE_SERIALIZE = "template.bat";
    public final static String QR_FILE_NAME = "uid_qr.img";
    public final static String PREF_KEY_PARAM_RADIO_ID = "param_radio_id";
    public final static String DATE_PATTERN_YM = "yyyyMM";
    public final static String DATE_PATTERN_DOT_YM = "yyyy.MM";
    public final static String DATE_PATTERN_COLON_HM = "HH:mm";
    public final static String DATE_PATTERN_DOT_YMD = "yyyy.MM.dd";
    public final static String DATE_PATTERN_DOT_MD = "MM.dd";
    public final static String DATE_PATTERN_SLASH_MD = "M/dd";

    public final static String DEFAULT = "DEFAULT";
    public final static String PARAM_ITEM_LEN = "PARAM_ITEM_LEN";

    public final static String PREF_KEY_QR_PW = "qr_pw";
    public final static List<String> listParam = Util.makeParam();

    public final static int PARAMS_SLIDER_MAX_MAX = 10;

    public final static String TEMPLATE_TIME_PAIR = "TEMPLATE_TIME_PAIR";
    public final static int CALLBACK_TEMPLATE_TIME_PAIR = 1000;
    public final static String TEMPLATE_TIME_PAIR_DES = "TEMPLATE_TIME_PAIR_DES";
    public final static int CALLBACK_TEMPLATE_TIME_PAIR_DES = 1010;
    public final static String TEMPLATE_TIME_COLOR = "TEMPLATE_TIME_COLOR";
    public final static String COLOR_NUM = "COLOR_NUM";
    public final static int CALLBACK_TEMPLATE_TIME_COLOR = 1020;
    public final static String CARD_INT = "CARD_INT";
//    final static String TEMPLATE_TAG_ADD = "TEMPLATE_TAG_ADD";
//    final static int CALLBACK_TEMPLATE_TAG_ADD = 1050;
    public final static String TEMPLATE_TAG_EDIT ="TEMPLATE_TAG_EDIT";
    public final static int CALLBACK_TEMPLATE_TAG_EDIT = 1060;
    public final static String PARAMS_NAME = "PARAMS_NAME";
    public final static int CALLBACK_PARAMS_NAME = 1100;
    public final static String TEMPLATE_PARAMS_NAME = "TEMPLATE_PARAMS_NAME";
    public final static int CALLBACK_TEMPLATE_PARAMS_NAME = 1110;
    public final static String PARAMS_VALUES = "PARAMS_VALUES";
    public final static String TEMPLATE_PARAMS_SLIDER_MAX = "TEMPLATE_PARAMS_SLIDER_MAX";
    public final static int CALLBACK_TEMPLATE_PARAMS_SLIDER_MAX = 1120;
    public final static String TEMPLATE_PARAMS_ITEM = "TEMPLATE_PARAMS_ITEM";
    public final static int CALLBACK_TEMPLATE_PARAMS_ITEM = 1130;
    public final static String TEMPLATE_PARAMS_ADD = "TEMPLATE_PARAMS_ADD";
    public final static int CALLBACK_TEMPLATE_PARAMS_ADD = 1140;
    public static final String UID = "UID";
    public static final String IS_DATA_MINE ="IS_DATA_MINE";
    public static final int NOTIFICATION_ID = 15973;
    public static final String NOTIFICATION_CHANNEL = "CHANNEL_0";
    public static final String LIST_MAP_HOUR = "HOUR";
    public static final String LIST_MAP_MIN = "MIN";
    public static final String LIST_MAP_VALUE = "VALUE";
    public static final String INDEX = "INDEX";
    public static final int RC_SIGN_IN = 100;

    final static String storageRoot = "gs://worksupport3.appspot.com/";

    private static final String TAG = "MANUAL_TAG: " + Util.class.getSimpleName();

    //region Calender⇔Date
    public static String cal2date(Calendar cal, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    public static Calendar date2Cal(String string, String pattern) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(string));
        return cal;
    }
    //endregion

    @Nullable
    public static String convertCalPattern(String calStr, String prePattern, String toPattern){
        Calendar cal;
        try {
            cal = date2Cal(calStr, prePattern);
        } catch (ParseException e) {
            logStackTrace(e);
            return null;
        }
        return cal2date(cal, toPattern);
    }

    //region px, dp, spなどの変換まわり
    public static int dp2px(Context context, int dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return  (int) (dp * scale + 0.5f);
    }

    public static float px2dp(Context context, int px){
        final float scale = context.getResources().getDisplayMetrics().density;
        return px/scale;
    }

    public static float sp2px(float sp, Context context) {
        return  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static float px2sp(float px, Context content){
        return px / content.getResources().getDisplayMetrics().scaledDensity;
    }
    //endregion

    @NonNull
    public static String time2String(int hour, int min){
        StringBuilder sb = new StringBuilder();
        modifyDigit(sb, hour);
        sb.append(":");
        modifyDigit(sb, min);
        return sb.toString();
    }

    private static void modifyDigit(StringBuilder sb, int num){
        if (num == 0){
            sb.append("00");
        } else if (num<10){
            sb.append("0").append(num);
        } else {
            sb.append(num);
        }
    }

    //scrollViewのスクショ撮るときに使ってください
    static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

    public static RecyclerView setRecycler(Context context, View view, RecyclerView.Adapter adapter, int id){
        RecyclerView recyclerView = view.findViewById(id);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    //region guava代替まわり
    /**************************** guava代替まわり ここから ***************************/
    @Contract("null, null -> true; null, !null -> false; !null, null -> false")
    public static boolean nullableEqual(@Nullable Object x, @Nullable Object y){
        if (x == null && y == null) return true;

        if (x == null || y == null) return false;

        return x.equals(y);
    }

    public static boolean setStrEqual(Set<String> x, Set<String> y){
        Set<String> subX = new HashSet<>(x);
        Set<String> subY = new HashSet<>(y);
        subX.removeAll(x);
        subY.removeAll(y);
        return subX.isEmpty() && subY.isEmpty();
    }

    private static List<String> makeParam(){
        List<String> list = new ArrayList<>();
        list.add("動悸・汗をかく");
        list.add("焦り");
        list.add("自殺願望");
        return list;
    }

    @NonNull
    public static String joinArr(String[] strings, @NonNull String delimiter){
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string);
            sb.append(delimiter);
        }
        sb.delete(sb.lastIndexOf(delimiter), sb.length());
        return sb.toString();
    }

    @NonNull
    public static String joinArr(String[] strings){
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string);
        }
        return sb.toString();
    }

    /**************************** guava代替 ここまで ***************************/
    //endregion

    //region data⇔Bundle
    /**************************** data⇔Bundle **********************************/

    /**
     * @return Nonnullかつ、エラー時にはemptyであることに注意してください
     */
    @NonNull
    public static List<Bundle> data2Bundle(RecordData data){
        Log.d(TAG, "setDateData: fire");

        List<Bundle> list = new ArrayList<>();
        if (data.data == null || data.data.isEmpty())
            return list;

        List<String> keyList = new ArrayList<>(data.data.keySet());
        Collections.sort(keyList, new Comparator<String>() {
            @Override
            public int compare(String x, String y) {
                String[] timeX = x.split(":");
                String[] timeY = y.split(":");
                if (!timeX[0].equals(timeY[0]))
                    return Integer.parseInt(timeX[0]) - Integer.parseInt(timeY[0]);
                else
                    return Integer.parseInt(timeX[1]) - Integer.parseInt(timeY[1]);
            }
        });

        int i =0;
        for (String key : keyList) {
            Bundle bundle = new Bundle();
            String[] time = key.split(":");
            bundle.putInt(LIST_MAP_HOUR, Integer.parseInt(time[0]));
            bundle.putInt(LIST_MAP_MIN, Integer.parseInt(time[1]));
            String value = (String)data.data.get(key);
            bundle.putString(LIST_MAP_VALUE, value);
            bundle.putInt(INDEX, i);
            list.add(bundle);
            i++;
        }

        return list;
    }

    @NonNull
    public static RecordData bundle2Data(List<Bundle> bundles, @Nullable String dataName, int dataType, int year, int mon, int day){
        RecordData data = new RecordData();
        data.dataName = dataName;
        data.dataType = dataType;
        data.year = year;
        data.mon = mon;
        data.day = day;
        data.data = innerBundle2Data(bundles);
        return data;
    }

    @NonNull
    public static RecordData bundle2Data(List<Bundle> bundles, @Nullable String dataName, int dataType, Calendar cal){
        RecordData data = new RecordData();
        data.dataName = dataName;
        data.dataType = dataType;
        data.year = cal.get(Calendar.YEAR);
        data.mon = cal.get(Calendar.MONTH);
        data.day = cal.get(Calendar.DATE);
        data.data = innerBundle2Data(bundles);
        return data;
    }

    @NonNull
    private static HashMap<String, Object> innerBundle2Data(@NonNull List<Bundle> bundles){
        HashMap<String, Object> hashMap = new HashMap<>(bundles.size());
        for (Bundle bundle : bundles) {
            int hour = bundle.getInt(LIST_MAP_HOUR);
            int min = bundle.getInt(LIST_MAP_MIN);
            String value = bundle.getString(LIST_MAP_VALUE);
            String timeStr = Util.time2String(hour, min);
            hashMap.put(timeStr, value);
        }
        return hashMap;
    }

    /**************************** data⇔Bundle ここまで**********************************/
    //endregion

    //region data⇔BundleParams
    /**************************** data⇔BundleParams ここから**********************************/

    /**
     * @return Nonnullかつ、エラー時にはemptyであることに注意してください
     */
    @NonNull
    public static List<Bundle> data2BundleParams(RecordData data){
        List<Bundle> list = new ArrayList<>();
        if (data.data == null || data.data.isEmpty())
            return list;

        int i=0;
        for (String key : data.data.keySet()) {
            Bundle bundle = new Bundle();
            String value = (String) data.data.get(key);
            String[] values = value.split(delimiter);
            bundle.putStringArray(PARAMS_VALUES, values);
            bundle.putInt(INDEX, i);
            list.add(bundle);
            i++;
        }
        return list;
    }

    public static RecordData bundle2DataParams(@NonNull List<Bundle> bundles, @Nullable String dataName, int year, int mon, int day){
        RecordData data = new RecordData();
        data.year = year;
        data.mon = mon;
        data.day = day;
        data.dataType = 3;
        data.dataName = dataName;
        data.data = innerBundle2DataParams(bundles);
        return data;
    }

    public static RecordData bundle2DataParams(@NonNull List<Bundle> bundles, @Nullable String dataName, Calendar cal){
        RecordData data = new RecordData();
        data.year = cal.get(Calendar.YEAR);
        data.mon = cal.get(Calendar.MONTH);
        data.day = cal.get(Calendar.DATE);
        data.dataType = 3;
        data.dataName = dataName;
        data.data = innerBundle2DataParams(bundles);
        return data;
    }

    private static HashMap<String, Object> innerBundle2DataParams(List<Bundle> bundles){
        HashMap<String, Object> data = new HashMap<>();
        for (int i = 0; i < bundles.size(); i++) {
            String[] strings = bundles.get(i).getStringArray(PARAMS_VALUES);
            if (strings == null)
                continue;
            String string = Util.joinArr(strings, delimiter);
            data.put(Integer.toString(i), string);
        }
        return data;
    }

    /**************************** data⇔BundleParams ここまで**********************************/
    //endregion

    static void printHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    public static void logAnalytics(@NonNull String eventName, Context context){
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        analytics.logEvent(eventName, new Bundle());
    }

    public static void logStackTrace(Exception e){
        e.printStackTrace();
        Crashlytics.logException(e);
    }

    /**
     * Fabricを必ずセットアップしないと、落ちます。
     */
    public static void onError(@NonNull Context context, @NonNull String string, @StringRes int toastRes){
        Log.w(TAG, string);
        logAnalytics(string, context);
        Answers.getInstance().logCustom(new CustomEvent(string));
        Toast.makeText(context, toastRes, Toast.LENGTH_LONG).show();
    }

    /**
     * Fabricを必ずセットアップしないと、落ちます。
     */
    public static void onError(@NonNull Fragment fragment, @NonNull String string, @StringRes int toastRes){
        if (fragment.getContext() == null)
            return;
        onError(fragment.getContext(), string, toastRes);
    }

    /**
     * Fabricを必ずセットアップしないと、落ちます。
     */
    public static void onError(@NonNull Context context, @NonNull String string, @Nullable String toast){
        Log.w(TAG, string);
        logAnalytics(string, context);
        Answers.getInstance().logCustom(new CustomEvent(string));
        if (toast != null)
            Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
    }

    /**
     * Fabricを必ずセットアップしないと、落ちます。
     */
    public static void onError(@NonNull Fragment fragment, @NonNull String string, @Nullable String toast){
        if (fragment.getContext() == null)
            return;
        onError(fragment.getContext(), string, toast);
    }

    public static void toastNullable(@Nullable Context context, @StringRes int stringRes){
        if (context == null)
            return;
        Toast.makeText(context, stringRes, Toast.LENGTH_LONG).show();
    }

    public static void toastNullable(@Nullable Context context, @NonNull String string){
        if (context == null)
            return;
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }

    @Contract(pure = true)
    @NonNull
    public static String getTextNullable(@Nullable String txt, @NonNull String subTxt){
        return txt == null ? subTxt : txt;
    }

    /**
     * @see "https://goo.gl/f6kZaG"
     */
    public static String getExtension(Context context, Uri uri){
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static String getMimeType(Context context, Uri uri){
        ContentResolver cR = context.getContentResolver();
        return cR.getType(uri);
    }

    //region setImgFromStorage
    public static void setImgFromStorage(User user, ImageView iv, @DrawableRes int errorDrw){
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().equals("null")){
            setImgWithPicasso(iv.getContext(), user.getPhotoUrl(), errorDrw, iv);
        } else {
            iv.setImageResource(errorDrw);
        }
    }

    public static void setImgFromStorage(@NonNull FirebaseUser user, ImageView iv, @DrawableRes int errorDrw){
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().toString().equals("null")){
            setImgWithPicasso(iv.getContext(), user.getPhotoUrl(), errorDrw, iv);
        } else {
            iv.setImageResource(errorDrw);
        }
    }

    public static void setImgFromStorage(@Nullable String photoUrl, ImageView iv, @DrawableRes int errorDrw){
        if (photoUrl != null && !photoUrl.equals("null")){
            setImgWithPicasso(iv.getContext(), photoUrl, errorDrw, iv);
        } else {
            iv.setImageResource(errorDrw);
        }
    }

    private static void setImgWithPicasso(Context context, Uri uri, @DrawableRes int errorImg, ImageView target){
        Picasso.with(context)
                .load(uri)
                .error(errorImg)
                .into(target);
    }

    private static void setImgWithPicasso(Context context, String uri, @DrawableRes int errorImg, ImageView target){
        Picasso.with(context)
                .load(uri)
                .error(errorImg)
                .into(target);
    }
    //endregion

    //region safにintent送る
    public static void kickSaf(Activity activity, int requestCode){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void kickSaf(Fragment fragment, int requestCode){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        fragment.startActivityForResult(intent, requestCode);
    }
    //endregion

    @Nullable
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static void intentKicker(@NonNull Context context, @NonNull String contentName, @NonNull Uri uri, @NonNull String action, @Nullable String type){
        final Intent share = new Intent(action);
        share.putExtra(Intent.EXTRA_SUBJECT, contentName);
        share.putExtra(Intent.EXTRA_TEXT, uri.toString());
        if (type != null)
            share.setDataAndType(uri, type);
        context.startActivity(share);
    }

    public static void setNullableText(TextView tv, @Nullable String string){
        if (string != null)
            tv.setText(string);
    }

    @Nullable
    static String getNullableText(EditText editText){
        Editable e = editText.getText();
        if (e == null)
            return null;
        return e.toString();
    }

    @Nullable
    public static String getNullableText(TextView textView){
        CharSequence c = textView.getText();
        if (c == null)
            return null;
        return c.toString();
    }

    public static void toast(Context context, boolean succeed, @StringRes int trueStr, @StringRes int falseStr){
        if (succeed)
            Toast.makeText(context, trueStr, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, falseStr, Toast.LENGTH_LONG).show();
    }

    public static void kickIntentIcon(Fragment fragment, int intentKey){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        fragment.getActivity().startActivityFromFragment(fragment, intent, intentKey);
    }

    public static void kickIntentIcon(Activity activity, int intentKey){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, intentKey);
    }

    @NonNull
    public static String makeScheme(@NonNull String... params){
        StringBuilder sb = new StringBuilder();
        for (String param: params) {
            sb.append(param);
            sb.append("/");
        }
        return sb.toString();
    }

    @Nullable
    public static FirebaseUser getUserMe(){
       return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static void initRecycler(Context context, RecyclerView rv, RecyclerView.Adapter adapter){
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(adapter);
    }

    public static Calendar getCalFromTimeEvent(@NonNull TimeEvent timeEvent){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, timeEvent.getHour());
        calendar.set(Calendar.MINUTE, timeEvent.getMin());
        return calendar;
    }

    @Contract("null -> null") @Nullable
    public static TimeEventDataSet getTimeEveDataSetFromRecordData(@Nullable RecordData recordData){
        if (recordData == null)
            return null;

        String string = (String) recordData.data.get("0");
        if (string == null)
            return null;

        return new Gson().fromJson(string, TimeEventDataSet.class);
    }
}
