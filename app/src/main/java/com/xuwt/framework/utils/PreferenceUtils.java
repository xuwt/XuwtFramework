package com.xuwt.framework.utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.xuwt.framework.BaseApplication;

import java.util.Set;


@SuppressLint("CommitPrefEdits")
public class PreferenceUtils {
	
	public static void putString(String key, String value) {
		try {
			Editor editor = PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).edit().putString(key, value);
			commitEditor(editor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getString(String key) {
		return getString(key, null);
	}
	
	public static String getString(String key, String defValue) {
		try {
			return PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).getString(key, defValue);
		} catch (Exception e) {
			e.printStackTrace();
			return defValue;
		}
	}
	
	public static void putStringSet(String key, Set<String> value) {
		try {
			Editor editor = PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).edit().putStringSet(key, value);
			commitEditor(editor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Set<String> getStringSet(String key, Set<String> defValue) {
		try {
			return PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).getStringSet(key, defValue);
		} catch (Exception e) {
			e.printStackTrace();
			return defValue;
		}
	}
	
	public static void putBoolean(String key, boolean value) {
		try {
			Editor editor = PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).edit().putBoolean(key, value);
			commitEditor(editor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	public static boolean getBoolean(String key, boolean defValue) {
		try {
			return PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).getBoolean(key, defValue);
		} catch (Exception e) {
			e.printStackTrace();
			return defValue;
		}
	}
	
	public static void putFloat(String key, float value) {
		try {
			Editor editor = PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).edit().putFloat(key, value);
			commitEditor(editor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static float getFloat(String key) {
		return getFloat(key, 0);
	}
	
	public static float getFloat(String key, float defValue) {
		try {
			return PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).getFloat(key, defValue);
		} catch (Exception e) {
			e.printStackTrace();
			return defValue;
		}
	}
	
	public static void putInt(String key, int value) {
		try {
			Editor editor = PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).edit().putInt(key, value);
			commitEditor(editor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int getInt(String key) {
		return getInt(key, 0);
	}
	
	public static int getInt(String key, int defValue) {
		try {
			return PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).getInt(key, defValue);
		} catch (Exception e) {
			e.printStackTrace();
			return defValue;
		}
	}
	
	public static void putLong(String key, long value) {
		try {
			Editor editor = PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).edit().putLong(key, value);
			commitEditor(editor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static long getLong(String key) {
		return getLong(key, 0);
	}
	
	public static long getLong(String key, long defValue) {
		try {
			return PreferenceManager.getDefaultSharedPreferences(BaseApplication.get()).getLong(key, defValue);
		} catch (Exception e) {
			e.printStackTrace();
			return defValue;
		}
	}
	
	/**
	 * 提交Editor， 针对版本不同，提交方式不一样。 
	 * 从api9开始，建议使用apply，是异步的。提速
	 * @param editor
	 */
	public static void commitEditor(Editor editor) {
		if(editor == null) 
			return;
		if(OSUtils.hasGingerbread()) {
			editor.apply();
		} else {
			editor.commit();
		}
	}
}
