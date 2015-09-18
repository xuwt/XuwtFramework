package com.xuwt.framework.manager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Rect;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.xuwt.framework.BaseApplication;

import java.lang.reflect.Field;


public class WindowManager {
	
	private static final byte[] mLock = new byte[0];
	private static WindowManager mInstance = null;
	public final static WindowManager get() {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new WindowManager(BaseApplication.get());
            }
            return mInstance;
        }
    }
	
	private Context mContext = null;
	private int mScreenWidth;
	private int mScreenHeight;
	private double mDensity;
	private int mDensityDpi;
	private float mStatusBarHeight;
	
	public int getScreenWidth() {
		return mScreenWidth;
	}

	public int getScreenHeight() {
		return mScreenHeight;
	}

	public double getDensity() {
		return mDensity;
	}

	public int getDensityDpi() {
		return mDensityDpi;
	}
	
	public float getStatusBarHeight() {
		if(mStatusBarHeight <= 0) {
			IContext context = ActivityManager.get().currentActivity();
			if(context != null && context instanceof Activity) {
				Activity activity = (Activity) context;
		        Rect rect = new Rect();
		        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		        mStatusBarHeight = rect.top;
			}
		}
		return mStatusBarHeight;
	}
	
	private WindowManager(Context context) {
		mContext = context;
		initialize();
	}
	
	private void initialize() {
		DisplayMetrics metric = new DisplayMetrics();
		android.view.WindowManager wm = (android.view.WindowManager) mContext.
				getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        mDensity = metric.density;
        mDensityDpi = metric.densityDpi;
        initStatusBarHeight();
	}
	
	private void initStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
	        Object obj = c.newInstance();
	        Field field = c.getField("status_bar_height");
	        int resId = Integer.parseInt(field.get(obj).toString());
	        mStatusBarHeight = BaseApplication.get().getResources().getDimensionPixelSize(resId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取系统屏幕亮度
	 * @return
	 */
	public int getScreenBrightness() {
		int nowBrightnessValue = 0;
		ContentResolver resolver = mContext.getContentResolver();
		try {
			nowBrightnessValue = Settings.System.getInt(
					resolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowBrightnessValue;
	}
	
	/**
	 * 获取系统屏幕亮度是否自动调节
	 * @return
	 */
	public boolean isAutoBrightness() {
		ContentResolver resolver = mContext.getContentResolver();
        boolean automicBrightness = false;
        try {
            int mode = Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            automicBrightness = mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }
	
	/**
	 * 设置系统屏幕亮度自动调节
	 */
    public void startAutoBrightness() {
		ContentResolver resolver = mContext.getContentResolver();
		try {
	        Settings.System.putInt(resolver,
	                Settings.System.SCREEN_BRIGHTNESS_MODE,
	                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
    /**
	 * 停止系统屏幕亮度自动调节
	 */
    public void stopAutoBrightness() {
		ContentResolver resolver = mContext.getContentResolver();
		try {
	        Settings.System.putInt(resolver,
	                Settings.System.SCREEN_BRIGHTNESS_MODE,
	                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 针对横屏键盘弹起不调整窗体大小的bug
     * @param activity
     */
    public void assistActivity (Activity activity) {
        new AndroidBug5497Workaround(activity);
    }

    private class AndroidBug5497Workaround {
    	
	    private View mChildOfContent;
	    private int usableHeightPrevious;
	    private FrameLayout.LayoutParams frameLayoutParams;
	
	    private AndroidBug5497Workaround(Activity activity) {
	        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
	        mChildOfContent = content.getChildAt(0);
	        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	                possiblyResizeChildOfContent();
	            }
	        });
	        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
	    }
	
	    private void possiblyResizeChildOfContent() {
	        int usableHeightNow = computeUsableHeight();
	        if (usableHeightNow != usableHeightPrevious) {
	            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
	            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
	            if (heightDifference > (usableHeightSansKeyboard/4)) {
	                // keyboard probably just became visible
	                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
	            } else {
	                // keyboard probably just became hidden
	                frameLayoutParams.height = usableHeightSansKeyboard;
	            }
	            mChildOfContent.requestLayout();
	            usableHeightPrevious = usableHeightNow;
	        }
	    }
	
	    private int computeUsableHeight() {
	        Rect r = new Rect();
	        mChildOfContent.getWindowVisibleDisplayFrame(r);
	        return (r.bottom - r.top);
	    }
    }

}
