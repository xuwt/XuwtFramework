package com.xuwt.framework.manager;


import com.xuwt.framework.utils.PreferenceUtils;

/**
 * 主题管理只适用资源内嵌在apk中
 * @author lijunma
 *
 */
public class ThemeManager {
	
	/**
	 * 默认主题
	 */
	public final static String DEFAULT = "default";
	
	private static final byte[] mLock = new byte[0];
	private static ThemeManager mInstance = null;
	public final static ThemeManager get() {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new ThemeManager();
            }
            return mInstance;
        }
    }
	
	private String mTheme = DEFAULT;
	private final String mKey = "apptheme";
	
	private ThemeManager() {
		loadTheme();
	}
	
	private void loadTheme() {
		mTheme = PreferenceUtils.getString(mKey, DEFAULT);
	}
	
	private void saveTheme() {
		PreferenceUtils.putString(mKey, mTheme);
	}
	
	public String getTheme() {
		return mTheme;
	}
	
	public void setTheme(String value) {
		mTheme = value;
		saveTheme();
		ActivityManager.get().changeTheme(mTheme);
	}
}
