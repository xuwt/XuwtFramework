package com.xuwt.framework.fragment;


import com.xuwt.framework.manager.ITheme;
import com.xuwt.framework.widget.base.IView;

public interface IFragment extends ITheme {
	void registView(IView view);
	void unregistView(IView view);
	
	boolean popBackStack();
    void popAllBackStack();
	void performBackKeyClicked();
}
