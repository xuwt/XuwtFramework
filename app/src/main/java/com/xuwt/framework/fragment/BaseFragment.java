package com.xuwt.framework.fragment;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xuwt.framework.BaseApplication;
import com.xuwt.framework.common.ViewInject;
import com.xuwt.framework.net.ITask;
import com.xuwt.framework.net.ITaskContext;
import com.xuwt.framework.widget.MToast;
import com.xuwt.framework.widget.base.IView;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;


public abstract class BaseFragment extends Fragment implements IFragment, ITaskContext {

	private FragmentProxy mProxy = new FragmentProxy(this);

	@Override
	public final void registView(IView view) {
		if(mProxy != null) {
			mProxy.registView(view);
		}
	}
	
	@Override
	public final void unregistView(IView view) {
		if(mProxy != null) {
			mProxy.unregistView(view);
		}
	}
	
	@Override
	public void registTask(ITask task) {
		if(mProxy != null) {
			mProxy.registTask(task);
		}
	}

	@Override
	public void unregistTask(ITask task) {
		if(mProxy != null) {
			mProxy.unregistTask(task);
		}
	}
	
	@Override
	public final void onChangeTheme(String theme) {
		if(mProxy != null) {
			mProxy.onChangeTheme(theme);
		}
	}
	
	/**
	 * 取得传递的参数
	 */
	protected void onQueryArguments() {
		
	}
	
	/**
	 * 初始化控件、获取内部控件（注释类遍历）
	 */
	protected void onInjectView(View rootView) {
		try {
			Class<?> clazz = this.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(ViewInject.class)) {
					ViewInject inject = field.getAnnotation(ViewInject.class);
					int id = inject.value();
					if (id != 0) {
						field.setAccessible(true);
						field.set(this, rootView.findViewById(id));
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化控件、获取内部控件
	 */
	protected void onFindView(View rootView) {
		
	}
	
	/**
	 * 设置监听事件
	 */
	protected void onBindListener() {
		
	}
	
	/**
	 * 加载数据
	 */
	protected void onApplyData() {
		
	}
	
	/**
	 * 切换主题
	 */
	@Override
	public void onApplyTheme(String theme) {
		
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @return
	 */
	public Object getQueryParam(String key) {
		Bundle bundle = getArguments();
		if(bundle != null) {
			return bundle.get(key);
		}
		return "";
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @return
	 */
	public int getQueryParamInteger(String key) {
		return getQueryParamInteger(key, 0);
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @param defvalue
	 * @return
	 */
	public int getQueryParamInteger(String key, int defvalue) {
		int value = defvalue;
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object object = bundle.get(key);
			if(object != null) {
				try {
					value = Integer.parseInt(object.toString());
				} catch (Exception e) {
					e.printStackTrace();
					value = defvalue;
				}
			}
		}
		return value;
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @return
	 */
	public long getQueryParamLong(String key) {
		return getQueryParamLong(key, 0);
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @param defvalue
	 * @return
	 */
	public long getQueryParamLong(String key, long defvalue) {
		long value = defvalue;
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object object = bundle.get(key);
			if(object != null) {
				try {
					value = Long.parseLong(object.toString());
				} catch (Exception e) {
					e.printStackTrace();
					value = defvalue;
				}
			}
		}
		return value;
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @return
	 */
	public float getQueryParamFloat(String key) {
		return getQueryParamFloat(key, 0f);
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @param defvalue
	 * @return
	 */
	public float getQueryParamFloat(String key, float defvalue) {
		float value = defvalue;
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object object = bundle.get(key);
			if(object != null) {
				try {
					value = Float.parseFloat(object.toString());
				} catch (Exception e) {
					e.printStackTrace();
					value = defvalue;
				}
			}
		}
		return value;
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @return
	 */
	public double getQueryParamDouble(String key) {
		return getQueryParamDouble(key, 0d);
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @param defvalue
	 * @return
	 */
	public double getQueryParamDouble(String key, double defvalue) {
		double value = defvalue;
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object object = bundle.get(key);
			if(object != null) {
				try {
					value = Double.parseDouble(object.toString());
				} catch (Exception e) {
					e.printStackTrace();
					value = defvalue;
				}
			}
		}
		return value;
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @return
	 */
	public boolean getQueryParamBoolean(String key) {
		return getQueryParamBoolean(key, false);
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @param defvalue
	 * @return
	 */
	public boolean getQueryParamBoolean(String key, boolean defvalue) {
		boolean value = defvalue;
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object object = bundle.get(key);
			if(object != null) {
				try {
					value = Boolean.parseBoolean(object.toString());
				} catch (Exception e) {
					e.printStackTrace();
					value = defvalue;
				}
			}
		}
		return value;
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @return
	 */
	public String getQueryParamString(String key) {
		return getQueryParamString(key, null);
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @param defvalue
	 * @return
	 */
	public String getQueryParamString(String key, String defvalue) {
		String value = defvalue;
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object object = bundle.get(key);
			if(object != null) {
				value = object.toString();
			}
		}
		return value;
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @return
	 */
	public String[] getQueryParamStringArray(String key) {
		Bundle bundle = getArguments();
		if(bundle != null) {
			return bundle.getStringArray(key);
		}
		return null;
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @return
	 */
	public ArrayList<String> getQueryParamStringArrayList(String key) {
		Bundle bundle = getArguments();
		if(bundle != null) {
			return bundle.getStringArrayList(key);
		}
		return null;
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getQueryParamSerializable(String key) {
		Bundle bundle = getArguments();
		if(bundle != null) {
			Serializable sz = bundle.getSerializable(key);
			if(sz != null) {
				return (T) sz;
			}
		}
		return null;
	}
	
	/**
	 * 获取传递参数
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getQueryParamParcelable(String key) {
		Bundle bundle = getArguments();
		if(bundle != null) {
			Parcelable p = bundle.getParcelable(key);
			if(p != null) {
				return (T) p;
			}
		}
		return null;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return false;
	}
	
	public BaseFragment() {
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity != null && activity instanceof IFragmentContext) {
			((IFragmentContext) activity).registFragment(this);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int resId = getContentResId();
		if(resId != 0) {
			return inflater.inflate(resId, container, false);
		} else {
			View view = getContentView(inflater, container, savedInstanceState);
			if(view == null) {
				throw new IllegalStateException("you should override getContentResId or getContentView method");
			} else {
				return view;
			}
		}
	}
	
	protected int getContentResId() {
		return 0;
	}
	
	protected View getContentView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return null;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initContentView();
	}
	
	@Override
	public void onStart() {
		if(mProxy != null) {
			mProxy.onStart();
		}
		super.onStart();
	}
	
	private void initContentView() {
		onQueryArguments();
		onInjectView(getView());
		onFindView(getView());
		onBindListener();
		onApplyData();
	}

	@Override
	public void onResume() {
		if(mProxy != null) {
			mProxy.onResume();
		}
		super.onResume();
	}
	
	@Override
	public void onPause() {
		if(mProxy != null) {
			mProxy.onPause();
		}
		super.onPause();
	}
    
	@Override
	public void onStop() {
		if(mProxy != null) {
    		mProxy.onStop();
		}
		super.onStop();
    }
	
	@Override
	public void onDestroy() {
		if(mProxy != null) {
			mProxy.onDestroy();
		}
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		if(getActivity() != null && getActivity() instanceof IFragmentContext) {
			((IFragmentContext) getActivity()).unregistFragment(this);
		}
		mProxy = null;
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		if(mProxy != null) {
			mProxy.onRestoreInstanceState(savedInstanceState);
		}
		super.onViewStateRestored(savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		if(mProxy != null) {
			mProxy.onSaveInstanceState(outState);
		}
		super.onSaveInstanceState(outState);
	}
	
	public Resources getAppResources() {
		return mActivity != null ? mActivity.getResources() : 
			BaseApplication.get().getResources();
	}
	
	/**
	 * 设置返回结果
	 * @param code
	 * @param intent
	 */
	public void setResult(int resultCode) {
		setResult(resultCode);
	}
	
	/**
	 * 设置返回结果
	 * @param code
	 * @param intent
	 */
	public void setResult(int resultCode, Intent data) {
		Fragment fragment = getTargetFragment();
		int requestCode = getTargetRequestCode();
		if(fragment != null) {
			fragment.onActivityResult(requestCode, resultCode, data);
			performBackKeyClicked();
		}
	}
	
	public boolean popBackStack() {
		Activity activity = getActivity();
		if(activity != null) {
			if(activity instanceof BaseFragmentActivity) {
				return ((BaseFragmentActivity)activity).popBackStack();
			}
		}
		return false;
	}
	
    public void popAllBackStack() {
    	Activity activity = getActivity();
		if(activity != null) {
			if(activity instanceof BaseFragmentActivity) {
				((BaseFragmentActivity)activity).popAllBackStack();
			} else if(activity instanceof FragmentActivity) {
				FragmentManager fm = ((FragmentActivity)activity).getSupportFragmentManager();
				fm.popAllBackStack();
			}
		}
	}
    
    public void finish() {
    	try {
	    	BaseFragmentActivity activity = (BaseFragmentActivity) getActivity();
	    	FragmentManager fm = activity.getSupportFragmentManager();
	    	if(fm.getBackStackEntryCount() > 0) {
				fm.popBackStack();
	    	} else {
	    		activity.finish();
	    	}
    	} catch (IllegalStateException e) {
    		e.printStackTrace();
    	}
    }
	
	public void performBackKeyClicked() {
    	Activity activity = getActivity();
		if(activity != null) {
			if(activity instanceof BaseFragmentActivity) {
				((BaseFragmentActivity)activity).performBackKeyClicked();
				return;
			} 
		}
		new Thread() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
    
    protected void showToastMessage(String text) {
    	showToastMessage(text, Toast.LENGTH_SHORT);
	}
    
    protected void showToastMessage(String text, int duration) {
    	MToast.showToastMessage(text, duration);
	}
    
    protected void showToastMessage(int resId) {
    	showToastMessage(resId, Toast.LENGTH_SHORT);
	}
    
    protected void showToastMessage(int resId, int duration) {
    	MToast.showToastMessage(resId, duration);
	}
    
    protected void startActivity(Class<?> cls) {
    	startActivity(new Intent(getActivity(), cls));
    }
    
    protected void startActivity(String action) {
    	startActivity(new Intent(action));
    }
    
    protected void startActivity(Class<?> cls, Intent data) {
    	if(data == null) {
    		return;
    	}
    	data.setClass(getActivity(), cls);
    	startActivity(data);
    }
    
    protected void startActivity(String action, Intent data) {
    	if(data == null) {
    		return;
    	}
    	data.setAction(action);
    	startActivity(data);
    }
    
    protected void startActivityForResult(Class<?> cls, int requestCode) {
    	startActivityForResult(new Intent(getActivity(), cls), requestCode);
    }
    
    protected void startActivityForResult(String action, int requestCode) {
    	startActivityForResult(new Intent(action), requestCode);
    }
    
    protected void startActivityForResult(Class<?> cls, Intent data, int requestCode) {
    	if(data == null) {
    		return;
    	}
    	data.setClass(getActivity(), cls);
    	startActivityForResult(data, requestCode);
    }
    
    protected void startActivityForResult(String action, Intent data, int requestCode) {
    	if(data == null) {
    		return;
    	}
    	data.setAction(action);
    	startActivityForResult(data, requestCode);
    }
}
