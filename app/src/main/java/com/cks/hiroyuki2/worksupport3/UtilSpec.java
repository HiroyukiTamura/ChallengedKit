package com.cks.hiroyuki2.worksupport3;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by hiroyuki2 on 2017/10/29.
 */

public class UtilSpec {
    public final static List<Integer> circleId = setCircleId();
    public final static List<Integer> colorId = setColorId();

    static Drawable getColoredGroupIcon(Context context){
        Drawable drawable = ContextCompat.getDrawable(context, com.cks.hiroyuki2.worksupportlibrary.R.drawable.ic_group_white_24dp).mutate();
        int color = ContextCompat.getColor(context, com.cks.hiroyuki2.worksupportlibrary.R.color.colorPrimary);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static CoordinatorLayout.LayoutParams getFabLp(Context context){
        WindowManager wm = (WindowManager)context.getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int defaultMargin =  context.getResources().getDimensionPixelSize(com.cks.hiroyuki2.worksupportlibrary.R.dimen.fab_margin);
        int adHeight;
        if (size.y <= context.getResources().getDimensionPixelSize(com.cks.hiroyuki2.worksupportlibrary.R.dimen.banner_size0)){
            adHeight = context.getResources().getDimensionPixelSize(com.cks.hiroyuki2.worksupportlibrary.R.dimen.fab_margin_min400dp);
        } else if (size.y <= context.getResources().getDimensionPixelSize(com.cks.hiroyuki2.worksupportlibrary.R.dimen.banner_size1)){
            adHeight = context.getResources().getDimensionPixelSize(com.cks.hiroyuki2.worksupportlibrary.R.dimen.fab_margin_min720dp);
        } else {
            adHeight = context.getResources().getDimensionPixelSize(com.cks.hiroyuki2.worksupportlibrary.R.dimen.fab_margin_over720dp);
        }
        lp.setMargins(defaultMargin, 0, defaultMargin, adHeight + defaultMargin);
        lp.gravity = (Gravity.BOTTOM | Gravity.END);
        return lp;
    }

    public static void initAdMob(Context context){
        MobileAds.initialize(context.getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView adView = new AdView(context);
        adView.setAdUnitId(context.getString(com.cks.hiroyuki2.worksupportlibrary.R.string.banner_ad_unit_id_test));
        adView.setAdSize(AdSize.SMART_BANNER);
        FrameLayout adFrameLayout = ((Activity)context).findViewById(com.cks.hiroyuki2.worksupportlibrary.R.id.bannersizes_fl_adframe);
        adFrameLayout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private static List<Integer> setCircleId(){
        List<Integer> circleId = new ArrayList<>();
        circleId.add(com.cks.hiroyuki2.worksupportlibrary.R.id.color0);
        circleId.add(com.cks.hiroyuki2.worksupportlibrary.R.id.color1);
        circleId.add(com.cks.hiroyuki2.worksupportlibrary.R.id.color2);
        circleId.add(com.cks.hiroyuki2.worksupportlibrary.R.id.color3);
        return circleId;
    }

    //    R.color.word_red, R.color.word_green, R.color.word_blue, R.color.word_purple);
    private static List<Integer> setColorId(){
        List<Integer> colorId = new ArrayList<>();
        colorId.add(com.cks.hiroyuki2.worksupportlibrary.R.color.word_red);
        colorId.add(com.cks.hiroyuki2.worksupportlibrary.R.color.word_green);
        colorId.add(com.cks.hiroyuki2.worksupportlibrary.R.color.word_blue);
        colorId.add(com.cks.hiroyuki2.worksupportlibrary.R.color.word_purple);
        return colorId;
    }

    public List<String> makeWofList(int startOfWeek, Context context){
        List<String> listDayOfWeek = Arrays.asList(context.getResources().getStringArray(com.cks.hiroyuki2.worksupportlibrary.R.array.dof_en));
        for (int i = 0; i < startOfWeek; i++) {
            String head = listDayOfWeek.remove(0);
            listDayOfWeek.add(head);
        }
        return listDayOfWeek;
    }
}
