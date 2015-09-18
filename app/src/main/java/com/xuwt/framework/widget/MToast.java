package com.xuwt.framework.widget;

import android.widget.Toast;

import com.xuwt.framework.BaseApplication;


public class MToast {

	private static Toast mToast;

	public static void showToastMessage(int resId) {
		showToastMessage(resId, Toast.LENGTH_SHORT);
	}

	public static void showToastMessage(int resId, int duration) {
		showToastMessage(BaseApplication.get().getResources().getString(resId), duration);
	}

	public static void showToastMessage(String text) {
		showToastMessage(text, Toast.LENGTH_SHORT);
	}

	public static void showToastMessage(String text, int duration) {
		if(mToast == null) {
			mToast = Toast.makeText(BaseApplication.get(), text, duration);
		} else {
			mToast.setText(text);
			mToast.setDuration(duration);
		}
		mToast.show();
	}
	
}
