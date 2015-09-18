package com.xuwt.framework.activity;


import com.xuwt.framework.manager.IContext;
import com.xuwt.framework.widget.base.IView;

public interface IActivityContext extends IContext {
	void registView(IView view);
	void unregistView(IView view);

	void finish();
}
