/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.leakcanary.LeakCanary;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * アプリケーションクラス
 */

public class MainApplication extends Application {
    private static final String TAG = "MANUAL_TAG: " + MainApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Log.d(TAG, "onCreate: fire");

        if (LeakCanary.isInAnalyzerProcess(this)) {
            //何も書き加えてはいけない
            return;
        }
        LeakCanary.install(this);
    }

    public static void initAdMob(Context context){
        MobileAds.initialize(context.getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView adView = new AdView(context);
        adView.setAdUnitId(context.getString(R.string.banner_ad_unit_id_test));
        adView.setAdSize(AdSize.SMART_BANNER);
        FrameLayout adFrameLayout = ((Activity)context).findViewById(R.id.bannersizes_fl_adframe);
        adFrameLayout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
