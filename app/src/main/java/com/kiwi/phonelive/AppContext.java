package com.kiwi.phonelive;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.kiwi.phonelive.activity.MainActivity;
import com.kiwi.phonelive.utils.LocalManageUtil;
import com.mob.MobSDK;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.kiwi.phonelive.http.HttpUtil;
import com.kiwi.phonelive.im.ImMessageUtil;
import com.kiwi.phonelive.im.ImPushUtil;
import com.kiwi.phonelive.utils.L;

import cn.tillusory.sdk.TiSDK;


/**
 * Created by cxf on 2017/8/3.
 */

public class AppContext extends MultiDexApplication {

    public static AppContext sInstance;
    public static boolean sDeBug;
    private int mCount;
    private boolean mFront;//是否前台
    //public static RefWatcher sRefWatcher;
    private boolean mBeautyInited;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sDeBug = BuildConfig.DEBUG;
//        if (sDeBug) {
//            sRefWatcher = LeakCanary.install(this);
//        }
        LocalManageUtil.setApplicationLanguage(this);
        //初始化腾讯bugly
        CrashReport.initCrashReport(this);
        CrashReport.setAppVersion(this,AppConfig.getInstance().getVersion());

        //初始化Http
        HttpUtil.init();
        //初始化ShareSdk
        MobSDK.init(this);
        //初始化极光推送
        ImPushUtil.getInstance().init(this);
        //初始化极光IM
        ImMessageUtil.getInstance().init();
        //初始化友盟统计
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        registerActivityLifecycleCallbacks();
    }

    /**
     * 初始化萌颜
     */
    public void initBeautySdk(String beautyKey) {
        if(AppConfig.TI_BEAUTY_ENABLE){
            if (!mBeautyInited) {
                mBeautyInited = true;
                TiSDK.init(beautyKey,  this);
                L.e("萌颜初始化------->");
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        //保存系统选择语言
        LocalManageUtil.saveSystemCurrentLanguage(base);
        super.attachBaseContext(LocalManageUtil.setLocal(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocalManageUtil.onConfigurationChanged(getApplicationContext());
    }

    private void registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mCount++;
                if (!mFront) {
                    mFront = true;
                    L.e("AppContext------->处于前台");
                    AppConfig.getInstance().setFrontGround(true);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mCount--;
                if (mCount == 0) {
                    mFront = false;
                    L.e("AppContext------->处于后台");
                    AppConfig.getInstance().setFrontGround(false);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}