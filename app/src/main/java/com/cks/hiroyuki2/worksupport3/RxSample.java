package com.cks.hiroyuki2.worksupport3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cks.hiroyuki2.worksupport3.Util.API_URL;
import static com.cks.hiroyuki2.worksupprotlib.Util.getUserMe;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import static com.cks.hiroyuki2.worksupprotlib.Util.toastNullable;

/**
 * Created by hiroyuki2 on 2017/11/25.
 */

public class RxSample {
    private static final String TAG = "RxSample";
    private Context context;
    
    public RxSample(Context context){
        this.context = context;
    }

    public void init(){

        Observable.just(Observable.fromArray("hogehoge")
                .map(new Function<String, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(String s) throws Exception {
                Log.d(TAG, "apply() called with: s = [" + s + "]");

                return new ObservableSource<Object>() {
                    @Override
                    public void subscribe(Observer<? super Object> observer) {
                        observer.onNext("てってれー");
                    }
                };
            }
        })).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object value) {
                        Log.d(TAG, "onNext() called with: value = [" + value.toString() + "]");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete() called");
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
                    }
                });
    }

    public void access(){
        FirebaseUser user = getUserMe();
        if (user != null){
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()){
                        String token = task.getResult().getToken();

                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();

                        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                        OkHttpClient client = new OkHttpClient.Builder()
                                .addInterceptor(interceptor)
                                .retryOnConnectionFailure(true)
                                .connectTimeout(15, TimeUnit.SECONDS)
                                .build();

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://api.github.com/")
                                .client(client)
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();

                        ShareBoardFragment.ApiService apiService = retrofit.create(ShareBoardFragment.ApiService.class);
                        apiService.getData("Bearer " + token)
                                .enqueue(new retrofit2.Callback<RequestBody>() {

                                    @Override
                                    public void onResponse(@Nullable retrofit2.Call<RequestBody> call, @Nullable retrofit2.Response<RequestBody> response) {
                                        if (response == null || !response.isSuccessful()){
                                            toastNullable(context, R.string.error);
                                        } else {
                                            Log.d(TAG, "onResponse: 成功!");
                                        }
                                    }

                                    @Override
                                    public void onFailure(@Nullable retrofit2.Call<RequestBody> call, @Nullable Throwable t) {
                                        String msg = t == null ? "": t.getMessage();
                                        onError(context, msg, R.string.error);
                                    }
                                });
                    } else {
                        onError(context, task.toString(), R.string.error);
                    }
                }
            });
        }
    }
}
