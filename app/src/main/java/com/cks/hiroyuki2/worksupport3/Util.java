package com.cks.hiroyuki2.worksupport3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

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
import com.cks.hiroyuki2.worksupprotlib.Entity.Content;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.cks.hiroyuki2.worksupprotlib.Util.URL_SHORTEN_API;

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
    private static final int TIMEOUT_SEC = 7;

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

    @NonNull
    public static Retrofit getRetroFit(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public final static String BASE_URL = "https://www.googleapis.com/urlshortener/v1/url/";
    public final static String API_KEY = "AIzaSyDDrs60sI8h7JeNjR-VTAJcbdnwRB5bVrk";

    public interface urlShortenApi {
        @POST(".")
        @Headers({
                "Content-Type: application/json"
        })
        Single<ShortenUrlResponse> getData(@Body HashMap<String, String> postData, @Query("key") String key);
    }

    // TODO: 2017/11/27 libに移管してposで取得⇒操作系を一掃して！！
    @Nullable
    public static Content getContentByKey(@NonNull List<Content> contentList, @NonNull String key){
        for (Content content : contentList)
            if (content.contentKey.equals(key))
                return content;
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
