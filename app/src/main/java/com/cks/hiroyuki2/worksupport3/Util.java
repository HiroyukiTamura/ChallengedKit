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
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.Fragments.AboutFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.AddFriendFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.AddGroupFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.AnalyticsFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.BlankFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.EditTemplateFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.GroupSettingFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.HelpFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.OnAddedFriendFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.RecordFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.SettingFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.SharedCalendarFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.SocialFragment;
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
import java.util.Locale;

import static android.os.Build.VERSION.SDK_INT;
import static com.cks.hiroyuki2.worksupprotlib.Util.NOTIFICATION_CHANNEL;
import static com.cks.hiroyuki2.worksupprotlib.Util.NOTIFICATION_CHANNEL_SECOND;

/**
 * 特段の理由がない限り、Util系のメソッドはlibに移管してください。
 */
public class Util {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef ({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    @interface permission {}

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CODE_WRITE_STORAGE, CODE_READ_STORAGE, CODE_CAMERA})
    @interface codePermission{}

    public static final int CODE_WRITE_STORAGE = 0;
    public static final int CODE_READ_STORAGE = 1;
    public static final int CODE_CAMERA = 2;
    public static final String OLD_GRP_NAME = "OLD_GRP_NAME";

//    public static void showUploadingNtf(Context context, UploadTask.TaskSnapshot taskSnapshot, String fileName, int id){
//        String text = context.getString(R.string.msg_start_upload);
//        NotificationCompat.Builder builder = createNtfBase(context, fileName, text, id)
//                .setAutoCancel(false)
//                .setProgress((int) taskSnapshot.getTotalByteCount(), (int) taskSnapshot.getBytesTransferred(), false);
//        if (SDK_INT >= 21)
//            builder.setCategory(Notification.CATEGORY_PROGRESS);
//        Notification notification = builder.build();
//        notification.flags = Notification.FLAG_NO_CLEAR;
//        showNtf(context, id, notification);
//    }

//    public static void showDownloadingNtf(Context context, FileDownloadTask.TaskSnapshot taskSnapshot, String fileName, int id){
//        String text = context.getString(R.string.msg_succeed_download);
//        NotificationCompat.Builder builder = createNtfBase(context, fileName, text, id)
//                .setAutoCancel(false)
//                .setProgress((int) taskSnapshot.getTotalByteCount(), (int) taskSnapshot.getBytesTransferred(), false);
//        if (SDK_INT >= 21)
//            builder.setCategory(Notification.CATEGORY_PROGRESS);
//        Notification notification = builder.build();
//        notification.flags = Notification.FLAG_NO_CLEAR;
//        showNtf(context, id, notification);
//    }
//
//    public static void showCompleteNtf(Context context, String fileName, int id, @StringRes int textRes){
//        String text = context.getString(textRes);
//        NotificationCompat.Builder builder = createNtfBase(context, fileName, text, id);
//        if (SDK_INT >= 21)
//            builder.setCategory(Notification.CATEGORY_STATUS);
//        showNtf(context, id, builder.build());
//    }
//
//    private static PendingIntent createPendingIntent(Context context, int id){
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setFlags(
//                Intent.FLAG_ACTIVITY_CLEAR_TOP  // 起動中のアプリがあってもこちらを優先する
//                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED  // 起動中のアプリがあってもこちらを優先する
//                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS  // 「最近利用したアプリ」に表示させない
//        );
//        return PendingIntent.getActivity(context, id, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
//    }
//
//    private static NotificationCompat.Builder createNtfBase(Context context, String fileName, String text, int id){
//        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
//                .setSmallIcon(R.drawable.ic_cloud_upload_white_24dp)// TODO: 2017/11/19 これ直すこと
//                .setContentTitle(fileName)
//                .setContentText(text)
//                .setTicker(text)
//                .setContentIntent(createPendingIntent(context, id));
//    }
//
//    private static void showNtf(Context context, int id, Notification ntf){
//        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
//        manager.notify(id, ntf);
//    }

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

    @Nullable
    public static String getFragmentTag(Fragment fragment){
        if (fragment instanceof AboutFragment)
            return AboutFragment.class.getSimpleName();
        else if (fragment instanceof AddFriendFragment)
            return AddFriendFragment.class.getSimpleName();
        else if (fragment instanceof AddGroupFragment)
            return AddGroupFragment.class.getSimpleName();
        else if (fragment instanceof AnalyticsFragment)
            return AnalyticsFragment.class.getSimpleName();
        else if (fragment instanceof BlankFragment)
            return BlankFragment.class.getSimpleName();
        else if (fragment instanceof EditTemplateFragment)
            return EditTemplateFragment.class.getSimpleName();
        else if (fragment instanceof GroupSettingFragment)
            return GroupSettingFragment.class.getSimpleName();
        else if (fragment instanceof HelpFragment)
            return HelpFragment.class.getSimpleName();
        else if (fragment instanceof OnAddedFriendFragment)
            return OnAddedFriendFragment.class.getSimpleName();
        else if (fragment instanceof RecordFragment)
            return RecordFragment.class.getSimpleName();
        else if (fragment instanceof SettingFragment)
            return SettingFragment.class.getSimpleName();
        else if (fragment instanceof ShareBoardFragment)
            return ShareBoardFragment.class.getSimpleName();
        else if (fragment instanceof SharedCalendarFragment)
            return SharedCalendarFragment.class.getSimpleName();
        else if (fragment instanceof SocialFragment)
            return SocialFragment.class.getSimpleName();
        return null;
    }

//    public static void showFcmMsg(String messageBody, Context context) {
//        final int ntfId = (int)System.currentTimeMillis();
//        String title = context.getString(R.string.app_name);
//
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_SECOND)
//                        .setSmallIcon(R.drawable.ic_info_white_24dp)
//                        .setContentTitle(title)
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setTicker(title)
//                        .setContentIntent(createPendingIntent(context, ntfId));
//
//        if (SDK_INT >= 21)
//            notificationBuilder.setCategory(Notification.CATEGORY_MESSAGE);
//
//        showNtf(context, ntfId, notificationBuilder.build());
//    }
}
