package com.cks.hiroyuki2.worksupport3;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * @see "https://piercezaifman.com/how-to-make-an-event-bus-with-rxjava-and-rxandroid/"
 */

public class RxBus {
    private static Map<Integer, PublishSubject<Object>> sSubjectMap = new HashMap<>();
    private static Map<Object, CompositeDisposable> sSubscriptionsMap = new HashMap<>();

    private RxBus() {
        // hidden constructor
    }

    public static final int UPDATE_GROUP_NAME = 0;
    public static final int UPDATE_GROUP_PHOTO = 1;
    public static final int UPDATE_GROUP_PHOTO2 = 2;
    public static final int REMOVE_MEMBER = 3;
    public static final int REMOVE_STORAGE_FILE = 4;
    public static final int CREATE_GROUP_NEW_IMG = 5;
    public static final int UPDATE_PROF_NAME_SUCCESS = 6;
    public static final int UPDATE_PROF_NAME_FAILURE = 7;
    @IntRange(from = 0, to = 7)
    @interface subject{}

    /**
     * Get the subject or create it if it's not already in memory.
     */
    @NonNull
    private static PublishSubject<Object> getSubject(@subject int subjectCode) {
        PublishSubject<Object> subject = sSubjectMap.get(subjectCode);
        if (subject == null) {
            subject = PublishSubject.create();
            subject.subscribeOn(AndroidSchedulers.mainThread());
            sSubjectMap.put(subjectCode, subject);
        }

        return subject;
    }

    /**
     * Get the CompositeDisposable or create it if it's not already in memory.
     */
    @NonNull
    private static CompositeDisposable getCompositeDisposable(@NonNull Object object) {
        CompositeDisposable compositeDisposable = sSubscriptionsMap.get(object);
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
            sSubscriptionsMap.put(object, compositeDisposable);
        }

        return compositeDisposable;
    }

    /**
     * Subscribe to the specified subject and listen for updates on that subject. Pass in an object to associate
     * your registration with, so that you can unsubscribe later.
     * <br/><br/>
     * <b>Note:</b> Make sure to call {@link RxBus#unregister(Object)} to avoid memory leaks.
     */
    public static void subscribe(@subject int subject, @NonNull Object lifecycle, @NonNull Consumer<Object> action) {
        Disposable disposable = getSubject(subject).subscribe(action);
        getCompositeDisposable(lifecycle).add(disposable);
    }

    /**
     * Unregisters this object from the bus, removing all subscriptions.
     * This should be called when the object is going to go out of memory.
     */
    public static void unregister(@NonNull Object lifecycle) {
        //We have to remove the composition from the map, because once you dispose it can't be used anymore
        CompositeDisposable compositeDisposable = sSubscriptionsMap.remove(lifecycle);
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    /**
     * Publish an object to the specified subject for all subscribers of that subject.
     */
    public static void publish(int subject, @NonNull Object message) {
        getSubject(subject).onNext(message);
    }
}
