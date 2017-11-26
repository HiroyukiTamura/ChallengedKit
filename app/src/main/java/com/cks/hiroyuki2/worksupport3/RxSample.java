package com.cks.hiroyuki2.worksupport3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

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
        FirebaseUser user = getUserMe();
        if (user == null){
            onError(context, "user == null", R.string.error);
            return;
        }

        Single<String> tokenSingle = Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()){
                            emitter.onSuccess(task.getResult().getToken());
                        } else {
                            emitter.onError(task.getException());
                        }
                    }
                });
            }
        });

        tokenSingle.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String token) throws Exception {
                    Log.d(TAG, "accept:" + token);
                    ApiService apiService = getRetroFit().create(ApiService.class);
                    apiService.getData("Bearer " + token)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Consumer<RequestBody>() {
                                @Override
                                public void accept(RequestBody requestBody) throws Exception {
                                    Log.d(TAG, "onSuccess: " + requestBody.toString());
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    com.cks.hiroyuki2.worksupprotlib.Util.onError(context, throwable.getMessage(), R.string.error);
                                }
                            });
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
        });

//        Observable.just(0)
//                .map(new Function<Integer, Object>() {
//                    @Override
//                    public Object apply(Integer integer) throws Exception {
//                        return tokenObs;
//                    }
//                })
//                .flatMap(new Function<Object, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(Object o) throws Exception {
//                        return tokenObs;
//                    }
//                })
//
//        Observable.just(user)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .map(new Function<FirebaseUser, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(FirebaseUser firebaseUser) throws Exception {
//                        FbTokenObserber obserber = new FbTokenObserber();
//                        obserber.setListener(firebaseUser);
//
//                        //token取得時のタイムアウト
//                        for (int i = 0; i < 7; i++) {
//                            Thread.sleep(1000);
//                            if (obserber.isListenerFired())
//                                break;
//                        }
//
//                        ApiService apiService = getRetroFit().create(ApiService.class);
//                        return apiService.getData("Bearer " + obserber.getToken())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribeOn(Schedulers.newThread());
//                    }
//                })
//                .subscribe(new Observer<Object>() {
//                    @Override
//                    public void onNext(Object o) {
//                        Log.d(TAG, "onNext: "+o.toString());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.d(TAG, "onComplete() called");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        com.cks.hiroyuki2.worksupprotlib.Util.onError(context, TAG+e.getMessage(), R.string.error);
//                    }
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.d(TAG, "onSubscribe: fire");
//                    }
//        });
//        Observable.just(Observable.fromArray("hogehoge")
//                .map(new Function<String, ObservableSource<?>>() {
//            @Override
//            public ObservableSource<?> apply(String s) throws Exception {
//                Log.d(TAG, "apply() called with: s = [" + s + "]");
//
//                return new ObservableSource<Object>() {
//                    @Override
//                    public void subscribe(Observer<? super Object> observer) {
//                        observer.onNext("てってれー");
//                    }
//                };
//            }
//        })).observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(new Observer<Object>() {
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(Object value) {
//                        Log.d(TAG, "onNext() called with: value = [" + value.toString() + "]");
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.d(TAG, "onComplete() called");
//                    }
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
//                    }
//                });
    }

    public void access() {
        FirebaseUser user = getUserMe();
        if (user == null) {
            onError(context, "user == null", R.string.error);
            return;
        }

        user.getIdToken(true).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                logStackTrace(e);
                toastNullable(context, R.string.error);
            }
        }).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                String token = getTokenResult.getToken();
                ApiService apiService = getRetroFit().create(ApiService.class);
                apiService.getData("Bearer " + token)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Consumer<RequestBody>() {
                            @Override
                            public void accept(RequestBody requestBody) throws Exception {
                                Log.d(TAG, "onSuccess: " + requestBody.toString());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                com.cks.hiroyuki2.worksupprotlib.Util.onError(context, throwable.getMessage(), R.string.error);
                            }
                        });
            }
        });
    }

//        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
//            @Override
//            public void onComplete(@NonNull Task<GetTokenResult> task) {
//                if (task.isSuccessful()){
//                    String token = task.getResult().getToken();
//                    ApiService apiService = getRetroFit().create(ApiService.class);
//
//                    apiService.getData("Bearer " + token)
//                            .enqueue(new retrofit2.Callback<RequestBody>() {
//
//                                @Override
//                                public void onResponse(@Nullable retrofit2.Call<RequestBody> call, @Nullable retrofit2.Response<RequestBody> response) {
//                                    if (response == null || !response.isSuccessful()){
//                                        toastNullable(context, R.string.error);
//                                    } else {
//                                        Log.d(TAG, "onResponse: 成功!");
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(@Nullable retrofit2.Call<RequestBody> call, @Nullable Throwable t) {
//                                    String msg = t == null ? "": t.getMessage();
//                                    onError(context, msg, R.string.error);
//                                }
//                            });
//                } else {
//                    onError(context, task.toString(), R.string.error);
//                }
//            }
//        });

    @NonNull
    private Retrofit getRetroFit(){
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

        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public interface ApiService {
        @GET("users/HiroyukTamura/repos")
        @Headers({
                "User-Agent: Retrofit-Sample-App"
        })
        Observable<RequestBody> getData(@Header("Authorization") String authorization);
    }
}
