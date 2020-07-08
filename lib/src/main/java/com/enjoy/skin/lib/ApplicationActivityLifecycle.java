package com.enjoy.skin.lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.Log;
import android.view.LayoutInflater;

import com.enjoy.skin.lib.utils.SkinThemeUtils;

import java.lang.reflect.Field;
import java.util.Observable;

public class ApplicationActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    public static final String TAG="ApplicationActivityLifecycle";
    private Observable mObserable;
    private ArrayMap<Activity, SkinLayoutInflaterFactory> mLayoutInflaterFactories = new
            ArrayMap<>();

    @SuppressLint("LongLogTag")
    public ApplicationActivityLifecycle(Observable observable) {
        Log.d(TAG,"ApplicationActivityLifecycle");
        mObserable = observable;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG,"onActivityCreated");
        /**
         *  更新状态栏
         */
        SkinThemeUtils.updateStatusBarColor(activity);

        /**
         *  更新布局视图
         */
        //获得Activity的布局加载器
        LayoutInflater layoutInflater = activity.getLayoutInflater();

        try {
            //Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
            //如设置过抛出一次
            //设置 mFactorySet 标签为false
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //使用factory2 设置布局加载工程
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory
                (activity);
        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
        mLayoutInflaterFactories.put(activity, skinLayoutInflaterFactory);

        mObserable.addObserver(skinLayoutInflaterFactory);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG,"onActivityStarted");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG,"onActivityResumed");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG,"onActivityPaused");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG,"onActivityStopped");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG,"onActivitySaveInstanceState");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG,"onActivityDestroyed");
        SkinLayoutInflaterFactory observer = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(observer);
    }
}
