package com.cks.hiroyuki2.worksupport3;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.icu.text.AlphabeticIndex;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEvent;
import com.cks.hiroyuki2.worksupprotlib.MainActivity;
import com.cks.hiroyuki2.worksupprotlib.UtilSpec;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.Contract;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.List;

import static com.cks.hiroyuki2.worksupprotlib.Util.NOTIFICATION_CHANNEL;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by hiroyuki2 on 2017/10/30.
 */

public class Util {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef ({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    @interface permission {}

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CODE_WRITE_STORAGE, CODE_READ_STORAGE, CODE_CAMERA})
    @interface codePermission{};

    public static final int CODE_WRITE_STORAGE = 0;
    public static final int CODE_READ_STORAGE = 1;
    public static final int CODE_CAMERA = 2;

    public static void showUploadingNtf(UploadTask.TaskSnapshot taskSnapshot, String fileName, int id){
        String text = getApplicationContext().getString(R.string.msg_start_upload);
        NotificationCompat.Builder builder = createNtfBase(fileName, text, id)
                .setCategory(Notification.CATEGORY_PROGRESS)
                .setAutoCancel(false)
                .setProgress((int) taskSnapshot.getTotalByteCount(), (int) taskSnapshot.getBytesTransferred(), false);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        showNtf(id, notification);
    }

    public static void showDownloadingNtf(FileDownloadTask.TaskSnapshot taskSnapshot, String fileName, int id){
        String text = getApplicationContext().getString(R.string.msg_succeed_download);
        NotificationCompat.Builder builder = createNtfBase(fileName, text, id)
                .setCategory(Notification.CATEGORY_PROGRESS)
                .setAutoCancel(false)
                .setProgress((int) taskSnapshot.getTotalByteCount(), (int) taskSnapshot.getBytesTransferred(), false);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        showNtf(id, notification);
    }

    public static void showCompleteNtf(String fileName, int id, @StringRes int textRes){
        String text = getApplicationContext().getString(textRes);
        NotificationCompat.Builder builder = createNtfBase(fileName, text, id)
                .setCategory(Notification.CATEGORY_STATUS);
        showNtf(id, builder.build());
    }

    private static PendingIntent createPendingIntent(int id){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP  // 起動中のアプリがあってもこちらを優先する
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED  // 起動中のアプリがあってもこちらを優先する
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS  // 「最近利用したアプリ」に表示させない
        );
        return PendingIntent.getActivity(
                getApplicationContext(),
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
    }

    private static NotificationCompat.Builder createNtfBase(String fileName, String text, int id){
        return new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_cloud_upload_white_24dp)
                .setContentTitle(fileName)
                .setContentText(text)
                .setTicker(text)
                .setContentIntent(createPendingIntent(id));
    }

    private static void showNtf(int id, Notification ntf){
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(id, ntf);
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

    public static void checkPermission(@NonNull Activity activity, PermissionListener listener){
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(listener)
                .check();
    }
}
