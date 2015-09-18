package com.xuwt.framework.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.xuwt.framework.BaseApplication;

import java.io.File;
import java.io.FileNotFoundException;


public class OSUtils {

	/**
	 * 转到操作系统设置
	 */
	public static void gotoSetting(Context context) {
		context.startActivity(new Intent(Settings.ACTION_SETTINGS));
	}
	
	/**
	 * 转到操作系统网络设置
	 */
	public static void gotoNetSetting(Context context) {
		context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
	}

	/**
	 * 获取app最大可用内存
	 */
	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}
	
	/**
	 * 安装apk文件
	 * @param apkFilePath
	 */
	public static void installApk(String apkFilePath) throws FileNotFoundException {
		File file = new File(apkFilePath);
		if(!file.exists()) {
			throw new FileNotFoundException();
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);  
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		intent.setDataAndType(Uri.parse("file://" + apkFilePath), "application/vnd.android.package-archive");  
		BaseApplication.get().startActivity(intent);
	}
	
	/**
	 * 弹出软键盘
	 * @param context
	 * @param view
	 */
	public static void ShowSoftInput(Context context, View view){
		try {
			view.requestFocus();
			InputMethodManager imm = (InputMethodManager)context.getApplicationContext().
				getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) { }
	}
	
	/**
	 * 隐藏软键盘
	 * @param context
	 */
	public static void hideSoftInput(Context context){
		try {
			if(context instanceof Activity) {
				hideSoftInput(context, (Activity)context);
			}
		} catch (Exception e) { }
	}
	
	/**
	 * 隐藏软键盘
	 * @param context
	 * @param activity
	 */
	public static void hideSoftInput(Context context, Activity activity){
		try {
			InputMethodManager imm = (InputMethodManager)context.getApplicationContext().
				getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 
				InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) { }
	}
	
	/**
	 * Android 2.0
	 * @return
	 */
	public static boolean hasECLAIR() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR;
	}
	
	/**
	 * Android 2.0.1
	 * @return
	 */
	public static boolean hasECLAIR01() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1;
	}
	
	/**
	 * Android 2.1.x
	 * @return
	 */
	public static boolean hasEclairMr1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1;
	}
	
	/**
	 * Android 2.2.x
	 * @return
	 */
	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	/**
	 * Android 2.3
	 * Android 2.3.1
	 * Android 2.3.2
	 * @return
	 */
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	/**
	 * Android 2.3.3
	 * Android 2.3.4
	 * @return
	 */
	public static boolean hasGingerbreadMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1;
	}

	/**
	 * Android 3.0.x
	 * @return
	 */
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}


	/**
	 * Android 3.1.x
	 * @return
	 */
	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}


	/**
	 * Android 3.2
	 * @return
	 */
	public static boolean hasHoneycombMR2() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
	}


	/**
	 * Android 4.0
	 * Android 4.0.1
	 * Android 4.0.2
	 * @return
	 */
	public static boolean hasIcecreamSandwich(){
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}


	/**
	 * Android 4.0.3
	 * Android 4.0.4
	 * @return
	 */
	public static boolean hasIcecreamSandwichMR1(){
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
	}


	/**
	 * Android 4.1
	 * Android 4.1.1
	 * @return
	 */
	public static boolean hasJellyBean(){
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

}
