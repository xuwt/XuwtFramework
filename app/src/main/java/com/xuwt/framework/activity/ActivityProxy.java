package com.xuwt.framework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xuwt.framework.cache.image.ImageFetcher;
import com.xuwt.framework.manager.ActivityManager;
import com.xuwt.framework.manager.ITheme;
import com.xuwt.framework.manager.ThemeManager;
import com.xuwt.framework.net.ITask;
import com.xuwt.framework.widget.IViewCycle;
import com.xuwt.framework.widget.base.IView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


class ActivityProxy {
	
	private String mTheme;
	private IActivityContext mActivity;

	private ArrayList<WeakReference<IView>> mRegistControls =
			new ArrayList<WeakReference<IView>>();

	private ArrayList<WeakReference<ITask>> mRegistTasks =
			new ArrayList<WeakReference<ITask>>();
	
	ActivityProxy(IActivityContext context) {
		mActivity = context;
	}
	
	void registView(IView view) {
		this.mRegistControls.add(new WeakReference<IView>(view));
	}
	
	void unregistView(IView view) {
		for(int i = 0; i < this.mRegistControls.size(); i ++) {
			WeakReference<IView> wf = this.mRegistControls.get(i);
			if(wf != null && wf.get() != null && wf.get().equals(view)) {
				this.mRegistControls.remove(i);
				break;
			}
		}
	}
	
	void registTask(ITask task) {
		this.mRegistTasks.add(new WeakReference<ITask>(task));
	}
	
	void unregistTask(ITask task) {
		for(int i = 0; i < this.mRegistTasks.size(); i ++) {
			WeakReference<ITask> wf = this.mRegistTasks.get(i);
			if(wf != null && wf.get() != null && wf.get().equals(task)) {
				this.mRegistTasks.remove(i);
				break;
			}
		}
	}
	
	void cancelTasks() {
		for(int i = 0; i < this.mRegistTasks.size(); i ++) {
			WeakReference<ITask> wf = this.mRegistTasks.get(i);
			if(wf != null && wf.get() != null) {
				wf.get().cancel();
			}
		}
	}
	
	void onChangeTheme(String theme) {
		for(int i=0;i<this.mRegistControls.size();i++) {
			WeakReference<IView> wf = this.mRegistControls.get(i);
			if(wf != null && wf.get() != null && wf.get() instanceof ITheme) {
				((ITheme) wf.get()).onChangeTheme(theme);
			}
		}
		if(TextUtils.isEmpty(theme))
			return;
		if(theme.equals(mTheme))
			return;
		mTheme = theme;
		if(mActivity != null) {
			mActivity.onApplyTheme(theme);
		}
	}
	
    void onCreate(Bundle savedInstanceState) {
    	
    }
    
    void onStart() {
		for(int i=0;i<this.mRegistControls.size();i++) {
			WeakReference<IView> wf = this.mRegistControls.get(i);
			if(wf != null && wf.get() != null && wf.get() instanceof IViewCycle) {
				((IViewCycle)wf.get()).onStart();
			}
		}
		if(mActivity != null) {
			ActivityManager.get().pushActivity(mActivity);
			onChangeTheme(ThemeManager.get().getTheme());
		}
    }
    
    void onRestart() {
		for(int i=0;i<this.mRegistControls.size();i++) {
			WeakReference<IView> wf = this.mRegistControls.get(i);
			if(wf != null && wf.get() != null && wf.get() instanceof IViewCycle) {
				((IViewCycle)wf.get()).onRestart();
			}
		}
    }
    
    void onNewIntent(Intent intent) {
		for(int i=0;i<this.mRegistControls.size();i++) {
			WeakReference<IView> wf = this.mRegistControls.get(i);
			if(wf != null && wf.get() != null && wf.get() instanceof IViewCycle) {
				((IViewCycle)wf.get()).onNewIntent(intent);
			}
		}
    }
    
    void onResume() {
		for(int i=0;i<this.mRegistControls.size();i++) {
			WeakReference<IView> wf = this.mRegistControls.get(i);
			if(wf != null && wf.get() != null && wf.get() instanceof IViewCycle) {
				((IViewCycle)wf.get()).onResume();
			}
		}
    }
    
    void onPause() {
		for(int i=0;i<this.mRegistControls.size();i++) {
			WeakReference<IView> wf = this.mRegistControls.get(i);
			if(wf != null && wf.get() != null && wf.get() instanceof IViewCycle) {
				((IViewCycle)wf.get()).onPause();
			}
		}
		ImageFetcher.get().setPauseWork(false);
		ImageFetcher.get().clearQueue();
    }
    
    void onStop() {
		for(int i=0;i<this.mRegistControls.size();i++) {
			WeakReference<IView> wf = this.mRegistControls.get(i);
			if(wf != null && wf.get() != null && wf.get() instanceof IViewCycle) {
				((IViewCycle)wf.get()).onStop();
			}
		}
    }
    
	void onDestroy() {
		for(int i=0;i<this.mRegistControls.size();i++) {
			WeakReference<IView> wf = this.mRegistControls.get(i);
			if(wf != null && wf.get() != null && wf.get() instanceof IViewCycle) {
				((IViewCycle)wf.get()).onDestroy();
			}
		}
		if(mActivity != null)
			ActivityManager.get().popupActivity(mActivity);
		cancelTasks();
	}
    
    void onSaveInstanceState(Bundle outState) {
		for(int i=0;i<this.mRegistControls.size();i++) {
			WeakReference<IView> wf = this.mRegistControls.get(i);
			if(wf != null && wf.get() != null && wf.get() instanceof IViewCycle) {
				((IViewCycle)wf.get()).onSaveInstanceState(outState);
			}
		}
    }
    
    void onRestoreInstanceState(Bundle savedInstanceState) {
		for(int i=0;i<this.mRegistControls.size();i++) {
			WeakReference<IView> wf = this.mRegistControls.get(i);
			if(wf != null && wf.get() != null && wf.get() instanceof IViewCycle) {
				((IViewCycle)wf.get()).onRestoreInstanceState(savedInstanceState);
			}
		}
    }
    
    @Override
    protected void finalize() {
    	try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
    	mActivity = null;
    	mRegistControls.clear();
    	mRegistControls = null;
    	mRegistTasks.clear();
    	mRegistTasks = null;
    }
}
