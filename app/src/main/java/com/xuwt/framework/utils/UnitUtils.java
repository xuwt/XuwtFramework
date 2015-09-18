package com.xuwt.framework.utils;

import android.content.Context;

public class UnitUtils {

	public static int dip2pix(Context context, int dips) {
		int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
		return (dips * densityDpi) / 160;
	}

	public static int pix2dip(Context context, int pixs) {
		int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
		return (pixs * 160) / densityDpi;
	}
	
}
