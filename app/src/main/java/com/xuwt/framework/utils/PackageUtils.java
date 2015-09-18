package com.xuwt.framework.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

public class PackageUtils {
	
	private static final String TAG = PackageUtils.class.getSimpleName();
	
	/**
	 * 获取应用名称
	 * @param context
	 * @return
	 */
	public static String getApplicationName(Context context) { 
        PackageManager packageManager = null; 
        ApplicationInfo applicationInfo = null; 
        try {
            packageManager = context.getPackageManager(); 
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0); 
        } catch (NameNotFoundException e) {
            applicationInfo = null; 
        } 
        String applicationName =  
        (String) packageManager.getApplicationLabel(applicationInfo); 
        return applicationName; 
    } 
	
	/**
	 * 获取包名
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		return context.getPackageName();
	}
	
	/**
	 * 指定的Activity是否存在
	 * @param context
	 * @return
	 */
	public static boolean isActivityExists(Context context, String packageName, String className) {
		Intent intent = new Intent();  
        intent.setClassName(packageName, className);
        return context.getPackageManager().resolveActivity(intent, 0) != null;
	}
	
	/**
	 * 设置组件的状态
	 * @param context
	 * @return
	 */
	public static void setComponentEnabled(Context context, Class<?> cls) {
		try {
			ComponentName component = new ComponentName(context, cls);
			int currState = context.getPackageManager().getComponentEnabledSetting(component);
			if(currState != PackageManager.COMPONENT_ENABLED_STATE_DEFAULT &&
			   currState != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
				context.getPackageManager().setComponentEnabledSetting(component, 
						PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, 
						PackageManager.DONT_KILL_APP);
			}
		} catch (Exception e) {
			LogUtils.error(TAG, e.getMessage());
		}
	}
	
	/**
	 * 检查是否安装app
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean checkInstalledApp(Context context, String packageName) {
		try {
			if(!TextUtils.isEmpty(packageName)) {
				PackageManager pm = context.getPackageManager();
				if(pm != null) {
					try {
						PackageInfo pinfo = pm.getPackageInfo(packageName, 0);
						if(pinfo != null) {
							return true;
						}
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 打开已安装app
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean openInstalledApp(Context context, String packageName) {
		try {
			if(!TextUtils.isEmpty(packageName)) {
				PackageManager pm = context.getPackageManager();
				if(pm != null) {
					Intent intentS = pm.getLaunchIntentForPackage(packageName);
					if(intentS != null) {
						context.startActivity(intentS);
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
