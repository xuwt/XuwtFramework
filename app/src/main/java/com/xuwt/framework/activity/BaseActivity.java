package com.xuwt.framework.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Toast;

import com.xuwt.framework.common.ViewInject;
import com.xuwt.framework.net.ITask;
import com.xuwt.framework.net.ITaskContext;
import com.xuwt.framework.utils.OSUtils;
import com.xuwt.framework.widget.MToast;
import com.xuwt.framework.widget.base.IView;

import java.lang.reflect.Field;


public abstract class BaseActivity extends Activity
	implements IActivityContext, ITaskContext {

	private ActivityProxy mProxy = new ActivityProxy(this);

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
	 * 是否开启硬件加速
	 * @return
	 */
	protected boolean enabledHardwareAccelerated() {
		return true;
	}
	

	/**
	 * 开启硬件加速
	 */
	protected void onHardwareAccelerated() {
		if(enabledHardwareAccelerated()) {
			if (OSUtils.hasHoneycomb()) {
				getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, 
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
			}
		}
	}

	/**
	 * 取得传递的参数
	 */
	protected void onQueryArguments(Intent intent) {
		
	}
	
	/**
	 * 初始化控件、获取内部控件（注释类遍历）
	 */
	protected void onInjectView() {
		try {
			Class<?> clazz = this.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(ViewInject.class)) {
					ViewInject inject = field.getAnnotation(ViewInject.class);
					int id = inject.value();
					if (id != 0) {
						field.setAccessible(true);
						field.set(this, findViewById(id));
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
	protected void onFindView() {
		
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
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		initContentView();
	}
	
	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		initContentView();
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		initContentView();
	}
	
	private void initContentView() {
		onHardwareAccelerated();
		onQueryArguments(getIntent());
		onInjectView();
		onFindView();
		onBindListener();
		onApplyData();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	if(mProxy != null) {
			mProxy.onCreate(savedInstanceState);
		}
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onStart() {
    	if(mProxy != null) {
			mProxy.onStart();
		}
    	super.onStart();
    }
    
    @Override
    protected void onRestart() {
    	if(mProxy != null) {
			mProxy.onRestart();
		}
    	super.onRestart();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	if(mProxy != null) {
			mProxy.onNewIntent(intent);
		}
    	super.onNewIntent(intent);
    }
    
    @Override
    protected void onResume() {
    	if(mProxy != null) {
			mProxy.onResume();
		}
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	if(mProxy != null) {
			mProxy.onPause();
		}
    	super.onPause();
    }
    
    @Override
    protected void onStop() {
    	if(mProxy != null) {
			mProxy.onStop();
		}
    	super.onStop();
    }
    
    @Override
	protected void onDestroy() {
    	if(mProxy != null) {
			mProxy.onDestroy();
		}
    	mProxy = null;
		super.onDestroy();
	}
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	if(mProxy != null) {
			mProxy.onSaveInstanceState(outState);
		}
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	if(mProxy != null) {
			mProxy.onRestoreInstanceState(savedInstanceState);
		}
    	super.onRestoreInstanceState(savedInstanceState);
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
    	startActivity(new Intent(this, cls));
    }
    
    protected void startActivity(String action) {
    	startActivity(new Intent(action));
    }
    
    protected void startActivity(Class<?> cls, Intent data) {
    	if(data == null) {
    		return;
    	}
    	data.setClass(this, cls);
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
    	startActivityForResult(new Intent(this, cls), requestCode);
    }
    
    protected void startActivityForResult(String action, int requestCode) {
    	startActivityForResult(new Intent(action), requestCode);
    }
    
    protected void startActivityForResult(Class<?> cls, Intent data, int requestCode) {
    	if(data == null) {
    		return;
    	}
    	data.setClass(this, cls);
    	startActivityForResult(data, requestCode);
    }
    
    protected void startActivityForResult(String action, Intent data, int requestCode) {
    	if(data == null) {
    		return;
    	}
    	data.setAction(action);
    	startActivityForResult(data, requestCode);
    }
    
    protected void performBackKeyClicked() {
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
}
