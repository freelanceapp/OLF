package com.raushan.roomapps;

import android.app.Activity;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.raushan.roomapps.di.AppInjector;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;


/**
 * Created by Panacea-Soft on 29/09/2019.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class PsApp extends MultiDexApplication implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AppInjector.init(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();


//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
//        setupLeakCanary();

    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

//    protected RefWatcher setupLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return RefWatcher.DISABLED;
//        }
//
//        ExcludedRefs excludedRefs = AndroidExcludedRefs.createAppDefaults()
//                .instanceField("android.view.inputmethod.InputMethodManager", "sInstance")
//                .instanceField("android.view.inputmethod.InputMethodManager", "mLastSrvView")
//                .instanceField("com.android.internal.policy.PhoneWindow$DecorView", "mContext")
//                .instanceField("android.support.v7.widget.SearchView$SearchAutoComplete", "mContext")
//                .build();
//
//        return LeakCanary.refWatcher(this).excludedRefs(excludedRefs).buildAndInstall();
//                //.install(this);
//    }

}
