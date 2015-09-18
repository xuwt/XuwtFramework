package com.xuwt.baidu;

import com.xuwt.framework.BaseApplication;
import com.xuwt.framework.BaseConfigure;

/**
 * Created by xuweitao on 2015/4/10.
 */
public class Application extends BaseApplication{

    public static Application get() {
        return (Application) BaseApplication.instance;
    }
    public Application() {
        super();
        BaseApplication.instance = this;
    }

    @Override
    public BaseConfigure getBaseConfigure() {
        return null;
    }

    @Override
    public String getCrashToastText() {
        return null;
    }

    @Override
    public void uploadCrashLog(String filePath, String crashInfo) {

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        exit();
    }

    @Override
    public void exit() {
		/*
		 * AudioPlayer.get().unbindService();
		 */
        super.exit();
    }

}
