package com.xuwt.framework.fragment;

import android.text.TextUtils;

import com.xuwt.framework.cache.image.ImageFetcher;
import com.xuwt.framework.manager.ActivityManager;
import com.xuwt.framework.manager.ThemeManager;
import com.xuwt.framework.net.ITask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


class FragmentActivityProxy {
	
	private String mTheme;
	private IFragmentContext mActivity;

	private ArrayList<WeakReference<IFragment>> mRegistFragments = 
			new ArrayList<WeakReference<IFragment>>();

	private ArrayList<WeakReference<ITask>> mRegistTasks =
			new ArrayList<WeakReference<ITask>>();
	
	FragmentActivityProxy(IFragmentContext context) {
		mActivity = context;
	}
	
	void registFragment(IFragment fragment) {
		this.mRegistFragments.add(new WeakReference<IFragment>(fragment));
	}
	
	void unregistFragment(IFragment fragment) {
		for(int i = 0; i < this.mRegistFragments.size(); i ++) {
			WeakReference<IFragment> wf = this.mRegistFragments.get(i);
			if(wf != null && wf.get() != null && wf.get().equals(fragment)) {
				this.mRegistFragments.remove(i);
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
		for(int i=0;i<this.mRegistFragments.size();i++) {
			WeakReference<IFragment> wf = this.mRegistFragments.get(i);
			if(wf != null && wf.get() != null) {
				wf.get().onChangeTheme(theme);
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
    
    void onStart() {
		if(mActivity != null) {
			ActivityManager.get().pushActivity(mActivity);
			onChangeTheme(ThemeManager.get().getTheme());
		}
    }
    
    void onPause() {
		ImageFetcher.get().setPauseWork(false);
		ImageFetcher.get().clearQueue();
    }
    
	void onDestroy() {
		if(mActivity != null)
			ActivityManager.get().popupActivity(mActivity);
		cancelTasks();
	}
    
    @Override
    protected void finalize() {
    	try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
    	mActivity = null;
    	mRegistFragments.clear();
    	mRegistFragments = null;
    	mRegistTasks.clear();
    	mRegistTasks = null;
    }
}
