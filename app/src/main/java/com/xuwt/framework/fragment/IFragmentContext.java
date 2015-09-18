package com.xuwt.framework.fragment;


import com.xuwt.framework.manager.IContext;

public interface IFragmentContext extends IContext {
	void registFragment(IFragment fragment);
	void unregistFragment(IFragment fragment);
	Fragment getCurrentFragment();
	boolean popBackStack();
    void popAllBackStack();
	void performBackKeyClicked();
}
