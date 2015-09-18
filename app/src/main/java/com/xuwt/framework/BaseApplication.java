package com.xuwt.framework;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.content.LocalBroadcastManager;

import com.xuwt.framework.manager.ActivityManager;
import com.xuwt.framework.manager.WindowManager;


public abstract class BaseApplication extends Application {
	
	protected static BaseApplication instance;
	public static BaseApplication get() {
		return instance;
	}

	/**
	 * 获取发生crash时弹出的toast提示语
	 * @return
	 */
	public abstract BaseConfigure getBaseConfigure();

	/**
	 * 获取发生crash时弹出的toast提示语
	 * @return
	 */
	public abstract String getCrashToastText();
	
	/**
	 * 上传crash日志
	 * @param filePath
	 * @param crashInfo
	 */
	public abstract void uploadCrashLog(String filePath, String crashInfo);

	/**
	 * 发生crash时是否弹出toast提示
	 * @return
	 */
	protected boolean showCrashToast() {
		return true;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.initException();
		this.initWindow();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	/**
	 * 结束进程
	 */
	protected void exit() {
		ActivityManager.get().popupAllActivity();
	}
	
	
	private void initException() {
		CrashHandler.get();  
	}
	
	private void initWindow() {
		WindowManager.get();
	}
	
	public void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent);
    }
    
    public void unregisterLocalReceiver(BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
	
    public void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }
}