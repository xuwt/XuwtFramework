package com.xuwt.framework.manager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xuwt.framework.BaseApplication;
import com.xuwt.framework.utils.LogUtils;
import com.xuwt.framework.utils.OSUtils;


/**
 * 其他主题的资源的命名规则是，在默认资源名称的名称前加  主题名称_
 * eg: 默认主题的图片为 logo.jpg， 则夜间模式主题(主题名称为night)的图片为 night_logo.jpg
 * @author lijunma
 *
 */
public class ThemeHelper {
	
	private final static String TAG = ThemeHelper.class.getSimpleName();

	private static Context mContext;
	private static StringBuilder mResourceName = new StringBuilder();
	private static StringBuilder mResourceType = new StringBuilder();
	
	private static void initializeContext() {
		if(mContext == null)
			mContext = BaseApplication.get();
	}
	
	/**
	 * 根据当前主题获取相应的资源id
	 * @param resourceID
	 * @return
	 */
	public static int getResourceID(String resType, String resName) {
		if(TextUtils.isEmpty(resType) || TextUtils.isEmpty(resName))
			return 0;
		initializeContext();
		try {
			mResourceName.delete(0, mResourceName.length());
			mResourceType.delete(0, mResourceType.length());
			
			String currTheme = ThemeManager.get().getTheme();
			if(currTheme != null && !currTheme.equals(ThemeManager.DEFAULT)) {
				mResourceName.append(currTheme).append("_");
			}
			mResourceType.append(resType);
			mResourceName.append(resName);
			return mContext.getResources().getIdentifier(mResourceName.toString(), 
					mResourceType.toString(), mContext.getPackageName());
		} catch (Exception e) {
			e.printStackTrace();
			System.gc();
		}
		return 0;
	}
	
	/**
	 * 根据当前主题获取相应的资源Drawable
	 * @param resourceID
	 * @return
	 */
	public static Drawable getResourceDrawable(int resourceID) {
		if(resourceID <= 0)
			return null;
		initializeContext();
		try {
			String currTheme = ThemeManager.get().getTheme();
			if(currTheme != null && !currTheme.equals(ThemeManager.DEFAULT)) {
				Resources res = mContext.getResources();
				mResourceName.delete(0, mResourceName.length());
				mResourceType.delete(0, mResourceType.length());
				mResourceName.append(currTheme).append("_").
					append(res.getResourceEntryName(resourceID));
				mResourceType.append(res.getResourceTypeName(resourceID));
				int newResID = res.getIdentifier(mResourceName.toString(), 
						mResourceType.toString(), mContext.getPackageName());
				if(newResID > 0) {
					return mContext.getResources().getDrawable(newResID);
				} else {
					LogUtils.error(TAG, "未找到主题资源" + mResourceName.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.gc();
		}
		try {
			return mContext.getResources().getDrawable(resourceID);
		} catch (Exception e) {
			e.printStackTrace();
			System.gc();
		}
		return null;
	}
	
	/**
	 * 根据当前主题获取相应的资源color
	 * @param resourceID
	 * @return
	 */
	public static int getResourceColor(int resourceID) {
		if(resourceID <= 0)
			return 0;
		initializeContext();
		try {
			String currTheme = ThemeManager.get().getTheme();
			if(currTheme != null && !currTheme.equals(ThemeManager.DEFAULT)) {
				Resources res = mContext.getResources();
				mResourceName.delete(0, mResourceName.length());
				mResourceType.delete(0, mResourceType.length());
				mResourceName.append(currTheme).append("_").
					append(res.getResourceEntryName(resourceID));
				mResourceType.append(res.getResourceTypeName(resourceID));
				int newResID = res.getIdentifier(mResourceName.toString(), 
						mResourceType.toString(), mContext.getPackageName());
				if(newResID > 0) {
					return mContext.getResources().getColor(newResID);
				} else {
					LogUtils.error(TAG, "未找到主题资源" + mResourceName.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.gc();
		}
		try {
			return mContext.getResources().getColor(resourceID);
		} catch (Exception e) {
			e.printStackTrace();
			System.gc();
		}
		return 0;
	}
	

	/**
	 * 根据当前主题获取相应的资源ColorStateList
	 * @param resourceID
	 * @return
	 */
	public static ColorStateList getResourceColorStateList(int resourceID) {
		if(resourceID <= 0)
			return null;
		initializeContext();
		try {
			String currTheme = ThemeManager.get().getTheme();
			if(currTheme != null && !currTheme.equals(ThemeManager.DEFAULT)) {
				Resources res = mContext.getResources();
				mResourceName.delete(0, mResourceName.length());
				mResourceType.delete(0, mResourceType.length());
				mResourceName.append(currTheme).append("_").
					append(res.getResourceEntryName(resourceID));
				mResourceType.append(res.getResourceTypeName(resourceID));
				int newResID = res.getIdentifier(mResourceName.toString(), 
						mResourceType.toString(), mContext.getPackageName());
				if(newResID > 0) {
					return mContext.getResources().getColorStateList(newResID);
				} else {
					LogUtils.error(TAG, "未找到主题资源" + mResourceName.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.gc();
		}
		try {
			return mContext.getResources().getColorStateList(resourceID);
		} catch (Exception e) {
			e.printStackTrace();
			System.gc();
		}
		return null;
	}
	
	/**
	 * 根据当前主题获取相应的资源resourceID
	 * @param resourceID
	 * @return
	 */
	public static int getResourceID(int resourceID) {
		if(resourceID <= 0)
			return 0;
		initializeContext();
		try {
			String currTheme = ThemeManager.get().getTheme();
			if(currTheme != null && !currTheme.equals(ThemeManager.DEFAULT)) {
				Resources res = mContext.getResources();
				mResourceName.delete(0, mResourceName.length());
				mResourceType.delete(0, mResourceType.length());
				mResourceName.append(currTheme).append("_").
					append(res.getResourceEntryName(resourceID));
				mResourceType.append(res.getResourceTypeName(resourceID));
				int newResID = res.getIdentifier(mResourceName.toString(), 
						mResourceType.toString(), mContext.getPackageName());
				if(newResID > 0) {
					return newResID;
				} else {
					LogUtils.error(TAG, "未找到主题资源" + mResourceName.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.gc();
		}
		return resourceID;
	}
	
	
	public static void setImageDrawable(ImageView imageView, int resourceID) {
		if (imageView != null) {
			imageView.setImageDrawable(getResourceDrawable(resourceID));
		}
	}
	
	public static void setChildDivider(ExpandableListView listView, int resourceID) {
		if (listView != null) {
			listView.setChildDivider(getResourceDrawable(resourceID));
		}
	}
	
	public static void setDivider(ExpandableListView listView, int resourceID) {
		if (listView != null) {
			listView.setDivider(getResourceDrawable(resourceID));
		}
	}

	public static void setSelector(ListView listView, int resourceID) {
		if (listView != null) {
			listView.setSelector(getResourceDrawable(resourceID));
		}
	}

	public static void  setCompoundDrawablesWithIntrinsicBounds(TextView textView,
	    int leftResourceID, int TopResourceID, int rightResourceID, int bottomResourceID ) {
		if (textView != null) {
		    textView.setCompoundDrawablesWithIntrinsicBounds(
		    		getResourceDrawable(leftResourceID),
		    		getResourceDrawable(TopResourceID),
		    		getResourceDrawable(rightResourceID),
		    		getResourceDrawable(bottomResourceID));
		}
	}

	public static void setTextColor(TextView textView, int resourceID) {
		if (textView != null) {
			textView.setTextColor(getResourceColor(resourceID));
		}
	}
	
	public static void setTextColorStateList(TextView textView, int resourceID) {
		if (textView != null) {
			textView.setTextColor(getResourceColorStateList(resourceID));
		}
	}

	public static void setHintTextColor(TextView textView, int resourceID) {
		if (textView != null) {
			textView.setHintTextColor(getResourceColor(resourceID));
		}
	}

	public static void setHintTextColorStateList(TextView textView, int resourceID) {
		if (textView != null) {
			textView.setHintTextColor(getResourceColorStateList(resourceID));
		}
	}

	public static void setTextColor(EditText edittext, int resourceID) {
		if (edittext != null) {
			edittext.setTextColor(getResourceColor(resourceID));
		}
	}
	
	public static void setTextColorStateList(EditText edittext, int resourceID) {
		if (edittext != null) {
			edittext.setTextColor(getResourceColorStateList(resourceID));
		}
	}

	public static void setHintTextColor(EditText edittext, int resourceID) {
		if (edittext != null) {
			edittext.setHintTextColor(getResourceColor(resourceID));
		}
	}

	public static void setHintTextColorStateList(EditText edittext, int resourceID) {
		if (edittext != null) {
			edittext.setHintTextColor(getResourceColorStateList(resourceID));
		}
	}
	
	public static void  setCompoundDrawablesWithIntrinsicBounds(EditText editeText,
	    int leftResourceID, int TopResourceID, int rightResourceID, int bottomResourceID ) {
		if (editeText != null) {
			editeText.setCompoundDrawablesWithIntrinsicBounds(
		    		getResourceDrawable(leftResourceID),
		    		getResourceDrawable(TopResourceID),
		    		getResourceDrawable(rightResourceID),
		    		getResourceDrawable(bottomResourceID));
		}
	}
	
	public static void setTextColor(Button button, int resourceID) {
		if (button != null) {
			button.setTextColor(getResourceColor(resourceID));
		}
    }
	
	public static void setTextColorStateList(Button button, int resourceID) {
		if (button != null) {
			button.setTextColor(getResourceColorStateList(resourceID));
		}
    }

	public static void setHintTextColor(Button button, int resourceID) {
		if (button != null) {
			button.setHintTextColor(getResourceColor(resourceID));
		}
	}

	public static void setHintTextColorStateList(Button button, int resourceID) {
		if (button != null) {
			button.setHintTextColor(getResourceColorStateList(resourceID));
		}
	}
	
	public static void setTextColor(CheckBox checkbox, int resourceID) {
		if (checkbox != null) {
			checkbox.setTextColor(getResourceColor(resourceID));
		}
    }
	
	public static void setTextColorStateList(CheckBox checkbox, int resourceID) {
		if (checkbox != null) {
			checkbox.setTextColor(getResourceColorStateList(resourceID));
		}
    }

	public static void setHintTextColor(CheckBox checkbox, int resourceID) {
		if (checkbox != null) {
			checkbox.setHintTextColor(getResourceColor(resourceID));
		}
	}

	public static void setHintTextColorStateList(CheckBox checkbox, int resourceID) {
		if (checkbox != null) {
			checkbox.setHintTextColor(getResourceColorStateList(resourceID));
		}
	}
	
	public static void setTextColor(RadioButton radiobutton, int resourceID) {
		if (radiobutton != null) {
			radiobutton.setTextColor(getResourceColor(resourceID));
		}
    }
	
	public static void setTextColorStateList(RadioButton radiobutton, int resourceID) {
		if (radiobutton != null) {
			radiobutton.setTextColor(getResourceColorStateList(resourceID));
		}
    }

	public static void setHintTextColor(RadioButton radiobutton, int resourceID) {
		if (radiobutton != null) {
			radiobutton.setHintTextColor(getResourceColor(resourceID));
		}
	}

	public static void setHintTextColorStateList(RadioButton radiobutton, int resourceID) {
		if (radiobutton != null) {
			radiobutton.setHintTextColor(getResourceColorStateList(resourceID));
		}
	}

	public static void setButtonDrawable(CheckBox checkbox, int resourceID) {
		if (checkbox != null) {
			checkbox.setButtonDrawable(getResourceDrawable(resourceID));
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void setBackgroundDrawable(View view, int resourceID) {
		if(view != null) {
			int leftPX = view.getPaddingLeft();
			int rightPX = view.getPaddingRight();
			int topPX = view.getPaddingTop();
			int bottomPX = view.getPaddingBottom();
			if(OSUtils.hasJellyBean())
				view.setBackground(getResourceDrawable(resourceID));
			else
				view.setBackgroundDrawable(getResourceDrawable(resourceID));
			view.setPadding(leftPX, topPX, rightPX, bottomPX);
		}
	}
	
	public static void setBackgroundColor(View view, int resourceID) {
		if(view != null) {
			int leftPX = view.getPaddingLeft();
			int rightPX = view.getPaddingRight();
			int topPX = view.getPaddingTop();
			int bottomPX = view.getPaddingBottom();
			view.setBackgroundColor(getResourceColor(resourceID));
			view.setPadding(leftPX, topPX, rightPX, bottomPX);
		}
	}
	
	public static void setBackgroundResourceID(View view, int resourceID) {
		if(view != null) {
			int leftPX = view.getPaddingLeft();
			int rightPX = view.getPaddingRight();
			int topPX = view.getPaddingTop();
			int bottomPX = view.getPaddingBottom();
			view.setBackgroundResource(getResourceID(resourceID));
			view.setPadding(leftPX, topPX, rightPX, bottomPX);
		}
	}
	
	public static void setIndeterminateDrawable(ProgressBar progressBar, int resourceID) {
		if (progressBar != null) {
			progressBar.setIndeterminateDrawable(getResourceDrawable(resourceID));
		}
	}

	public static void setWindowBackgroud(Window window, int resourceID) {
		if(window != null) {
			window.setBackgroundDrawable(getResourceDrawable(resourceID));
		}
	}
	
}
