package com.example.user.bluetooth_howtopair.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

public class ConfigApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public static final int ACTIVITY_ONCREATE = 1;
    public static final int ACTIVITY_ONDESTROY = 7;
    public static final int ACTIVITY_ONPAUSE = 4;
    public static final int ACTIVITY_ONRESUME = 3;
    public static final int ACTIVITY_ONSAVEINSTANCESTATE = 6;
    public static final int ACTIVITY_ONSTART = 2;
    public static final int ACTIVITY_ONSTOP = 5;
    private HashMap<String, Integer> activityHashMap;
    private ConfigParamsKeeper configkeeper;
    private String mainactivity;
    private String topactivity;

    public void onCreate() {
        super.onCreate();
        this.configkeeper = new ConfigParamsKeeper(getApplicationContext());
    }

    protected void enableActivityLifeCallback(String mainactivity) {
        this.mainactivity = mainactivity;
        this.activityHashMap = new HashMap();
        registerActivityLifecycleCallbacks(this);
    }

    protected void disenableActivityLifeCallback() {
        unregisterActivityLifecycleCallbacks(this);
    }

    protected boolean hasKey(String key) {
        return this.configkeeper.hasKey(key);
    }

    public int getIntValue(String key) {
        return this.configkeeper.getIntValue(key);
    }

    public void setValue(String key, int value) {
        this.configkeeper.setValue(key, value);
    }

    public long getLongValue(String key) {
        return this.configkeeper.getLongValue(key);
    }

    public void setValue(String key, long value) {
        this.configkeeper.setValue(key, value);
    }

    public float getFloatValue(String key) {
        return this.configkeeper.getFloatValue(key);
    }

    public void setValue(String key, float value) {
        this.configkeeper.setValue(key, value);
    }

    public boolean getBooleanValue(String key) {
        return this.configkeeper.getBooleanValue(key);
    }

    public void setValue(String key, boolean value) {
        this.configkeeper.setValue(key, value);
    }

    public void setValue(String key, String value) {
        this.configkeeper.setValue(key, value);
    }

    public String getStringValue(String key) {
        return this.configkeeper.getStringValue(key);
    }

    private void setLife(Activity activity, int life) {
        this.activityHashMap.put(activity.getClass().getCanonicalName(), Integer.valueOf(life));
        onActivityLifeChange(this.activityHashMap);
        if (life == ACTIVITY_ONRESUME) {
            this.topactivity = activity.getClass().getCanonicalName();
        } else if (life == ACTIVITY_ONSTOP) {
            switch (((Integer) this.activityHashMap.get(this.topactivity)).intValue()) {
            }
            if (!isRunningForeground()) {
                onApplicationGoback();
            }
        } else if (life == ACTIVITY_ONDESTROY && !isRunningForeground()) {
            onApplicationExit();
        }
    }

    public boolean isRunningForeground() {
        String packageName = getPackageName(this);
        String topActivityClassName = getTopActivityName(this);
        System.out.println("packageName=" + packageName + ",topActivityClassName=" + topActivityClassName);
        if (packageName == null || topActivityClassName == null || !topActivityClassName.startsWith(packageName)) {
            System.out.println("---> isRunningBackGround");
            return false;
        }
        System.out.println("---> isRunningForeGround");
        return true;
    }

    public String getTopActivityName(Context context) {
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(ACTIVITY_ONCREATE);
        if (runningTaskInfos != null) {
            return ((ActivityManager.RunningTaskInfo) runningTaskInfos.get(0)).topActivity.getClassName();
        }
        return null;
    }

    public String getPackageName(Context context) {
        return context.getPackageName();
    }

    protected void onActivityLifeChange(HashMap<String, Integer> hashMap) {
    }

    protected void onApplicationGoback() {
    }

    protected void onApplicationExit() {
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.v("onActivityCreated", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONCREATE);
    }

    public void onActivityStarted(Activity activity) {
        Log.v("onActivityStarted", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONSTART);
    }

    public void onActivityResumed(Activity activity) {
        Log.v("onActivityResumed", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONRESUME);
    }

    public void onActivityPaused(Activity activity) {
        Log.v("onActivityPaused", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONPAUSE);
    }

    public void onActivityStopped(Activity activity) {
        Log.v("onActivityStopped", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONSTOP);
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.v("onActivitySaveInstanceState", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONSAVEINSTANCESTATE);
    }

    public void onActivityDestroyed(Activity activity) {
        Log.v("onActivityDestroyed", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONDESTROY);
    }
}

