package com.xuwt.framework.manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ActivityManager {

    private static final byte[] mLock = new byte[0];
	private static ActivityManager mInstance = null;
	public final static ActivityManager get() {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new ActivityManager();
            }
            return mInstance;
        }
    }
	
	private ArrayList<WeakReference<IContext>> activityStack; 
    
    private ActivityManager() {
    	activityStack = new ArrayList<WeakReference<IContext>>();
    }
    
    public IContext currentActivity() { 
    	IContext activity = null; 
    	if(activityStack.size() > 0) {
    		WeakReference<IContext> reference= activityStack.get(activityStack.size() - 1);
    		if(reference != null) {
    			activity = reference.get();
    		}
    	}
        return activity; 
    }
    
    public IContext bottomActivity() { 
    	IContext activity = null; 
    	if(activityStack.size() > 0) {
    		WeakReference<IContext> reference= activityStack.get(0);
    		if(reference != null) {
    			activity = reference.get();
    		}
    	}
        return activity; 
    }
    
    public void pushActivity(IContext activity) { 
        if (activityStack == null || activity == null) {
        	return;
        }
        for(int i = activityStack.size() - 1; i >= 0; i--) {
    		WeakReference<IContext> reference= activityStack.get(i);
    		if(reference != null) {
        		IContext context = reference.get();
        		if(context != null && context.equals(activity)) {
        			return;
        		}
    		}
    	}
        activityStack.add(new WeakReference<IContext>(activity)); 
    } 
    
    private void popupActivity(IContext activity, boolean callgc, boolean finish) { 
        if (activity != null) { 
        	if(finish) {
        		activity.finish(); 
        	}
        	for(int i = activityStack.size() - 1; i >= 0; i--) {
        		WeakReference<IContext> reference= activityStack.get(i);
        		if(reference != null) {
            		IContext context = reference.get();
            		if(context != null && context.equals(activity)) {
            			activityStack.remove(i);
            			break;
            		}
        		}
        	}
            activity = null; 
        }
        if(callgc) {
        	if(activityStack.size() < 1)
        		android.os.Process.killProcess(android.os.Process.myPid());
        	System.gc();
        }
    } 
    
    public void popupActivity(IContext activity) { 
    	popupActivity(activity, true, false);
    } 
    
    public void popupAllActivityAfterThis(Class<?> activity) { 
        while (true) { 
        	IContext current = currentActivity(); 
            if (current == null) { 
                break; 
            } 
            if (activity != null && activity.equals(current.getClass())) { 
                break; 
            } 
            popupActivity(current, false, true); 
        } 
    	if(activityStack.size() < 1)
    		android.os.Process.killProcess(android.os.Process.myPid());
        System.gc();
    } 
    
    public void popupAllActivityBeforeThis(Class<?> activity) { 
        while (true) { 
        	IContext bottom = bottomActivity(); 
            if (bottom == null) { 
                break; 
            } 
            if (activity != null && activity.equals(bottom.getClass())) { 
                break; 
            } 
            popupActivity(bottom, false, true); 
        } 
    	if(activityStack.size() < 1)
    		android.os.Process.killProcess(android.os.Process.myPid());
        System.gc();
    } 
    
    public void popupAllActivity() { 
    	popupAllActivityAfterThis(null);
    }
    
    /**
     * 改变主题
     * @param theme
     */
    public void changeTheme(String theme) {
    	for(int i = activityStack.size() - 1; i >= 0; i--) {
    		WeakReference<IContext> reference= activityStack.get(i);
    		if(reference != null) {
        		IContext activity = reference.get();
        		activity.onChangeTheme(theme);
    		}
    	}
    }
}
