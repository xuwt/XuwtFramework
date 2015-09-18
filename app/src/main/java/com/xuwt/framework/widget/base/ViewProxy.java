package com.xuwt.framework.widget.base;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.xuwt.framework.activity.IActivityContext;
import com.xuwt.framework.data.IDataContext;
import com.xuwt.framework.fragment.Fragment;
import com.xuwt.framework.fragment.IFragment;
import com.xuwt.framework.fragment.IFragmentContext;
import com.xuwt.framework.manager.ITheme;

import java.lang.reflect.Field;
import java.util.HashMap;


public class ViewProxy {
	
	private static final String BINDING_PREFIX = "{Binding ";
	private static final String BINDING_SUFFIX = "}";
	
	private String mTheme;
	private Object mDataContext;
	private HashMap<Integer, String> mBinding;
	
	private Context mContext;
	private IView mView;
	private IFragment mFragment;

	public ViewProxy(Context context, IView view) {
		mContext = context;
		mView = view;
		mBinding = new HashMap<Integer, String>();
	}
	
	public void onChangeTheme(String theme) {
		if(TextUtils.isEmpty(theme))
			return;
		if(theme.equals(mTheme))
			return;
		mTheme = theme;
		if(mView != null && mView instanceof ITheme) {
			((ITheme)mView).onApplyTheme(theme);
		}
	}
	
	public IView registerView() {
		if(mContext != null) {
			if(mContext instanceof IActivityContext) {
				((IActivityContext) mContext).registView(mView);
				return mView;
			}
			if(mContext instanceof IFragmentContext) {
				Fragment fragment = ((IFragmentContext) mContext).getCurrentFragment();
				if(fragment != null && fragment instanceof IFragment) {
					((IFragment) fragment).registView(mView);
					return mView;
				}
			}
		}
		return null;
	}
	
	public void unregisterView() {
		if(mContext != null) {
			if(mContext instanceof IActivityContext) {
				((IActivityContext) mContext).unregistView(mView);
			}
			if(mContext instanceof IFragmentContext) {
				Fragment fragment = ((IFragmentContext) mContext).getCurrentFragment();
				if(fragment != null && fragment instanceof IFragment) {
					((IFragment) fragment).unregistView(mView);
				}
			}
		}
	}

	public void setDataContext(Object data) {
		mDataContext = data;
		if(mView instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup)mView;
			for(int i = 0; i < vg.getChildCount(); i++) {
				View view = vg.getChildAt(i);
				if(view instanceof IDataContext) {
					((IDataContext)view).setDataContext(data);
				}
			}
		}
	}

	public IFragment getFragment() {
		return mFragment;
	}

	public void setFragment(IFragment fragment) {
		mFragment = fragment;
	}

	public Object getDataContext() {
		return mDataContext;
	}
	
	public boolean setBindingValue(int property, String value) {
		if(!TextUtils.isEmpty(value)) {
			String binding = value.trim();
			if(binding.startsWith(BINDING_PREFIX) && binding.endsWith(BINDING_SUFFIX)) {
				binding = binding.substring(BINDING_PREFIX.length(), 
						binding.length() - BINDING_SUFFIX.length());
				if(mBinding.containsKey(property)) {
					mBinding.remove(property);
				}
				mBinding.put(property, binding);
				return true;
			}
		}
		return false;
	}
	
	public boolean hasBindingValue(int property) {
		if(mBinding.containsKey(property) && mContext != null) {
			return true;
		}
		return false;
	}
	
	public Object getBindingValue(int property) {
		Object result = null;
		if(mBinding.containsKey(property)) {
			try {
				String value = mBinding.get(property);
				if(mDataContext != null) {
					if(TextUtils.isEmpty(value) || value.toLowerCase().trim() == "this") {
						result = mDataContext;
					} else {
						Class<?> cls = mDataContext.getClass();
						Field field = cls.getField(value);
						result = field.get(mDataContext);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
}
