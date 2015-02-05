package com.xuwt.framework;

import android.app.Application;
import android.content.Context;

/**
 * Created by xuweitao on 2015/2/5.
 */
public class BaseApplication extends Application {

    protected static BaseApplication mInstance;
    protected static Context mContext;

    public static BaseApplication getInstance(){
        return mInstance;
    }

    public static Context getContext(){
        return  mContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        mContext=this.getApplicationContext();
    }
}
