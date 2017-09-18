package com.justh.dell.essayjoke.base;

import android.app.Application;

import com.justh.dell.baselibrary.ExceptionCrashHandler;

/**
 * Created by DELL on 2017/9/6.
 * Description :
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ExceptionCrashHandler.getInstance().init(this);
    }
}
