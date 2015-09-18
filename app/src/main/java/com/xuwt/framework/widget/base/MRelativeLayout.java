package com.xuwt.framework.widget.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xuwt.framework.R;
import com.xuwt.framework.data.IDataContext;
import com.xuwt.framework.fragment.IFragment;
import com.xuwt.framework.manager.ThemeHelper;
import com.xuwt.framework.widget.MToast;


public class MRelativeLayout extends RelativeLayout implements IDataContext, IView {

	protected ViewProxy mProxy;

	public MRelativeLayout(Context context) {
		super(context);
		initializeView(context);
	}
	
	public MRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView(context);
		initializeAttr(attrs, 0);
	}
	
	public MRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeView(context);
		initializeAttr(attrs, defStyle);
	}
	
	protected void initializeView(Context context) {
		mProxy = new ViewProxy(context, this);
		mProxy.registerView();
	}

	protected void initializeAttr(AttributeSet attrs, int defStyle) {
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
				R.styleable.DataContext, defStyle, 0);
		
		String onclick = typedArray.getString(R.styleable.DataContext_binding_onclick);
		if(mProxy.setBindingValue(R.styleable.DataContext_binding_onclick, onclick)) {
			setOnClickListener(null);
		}
		String background = typedArray.getString(R.styleable.DataContext_binding_background);
		if(mProxy.setBindingValue(R.styleable.DataContext_binding_background, background)) {
			setBackgroundResource(0);
		}
		String visibility = typedArray.getString(R.styleable.DataContext_binding_visibility);
		mProxy.setBindingValue(R.styleable.DataContext_binding_visibility, visibility);



		typedArray.recycle();
	}
	@Override
	protected void onDetachedFromWindow() {
		mProxy.unregisterView();
		super.onDetachedFromWindow();
	}

	@Override
	public void setBindingValue(int property, String value) {
		mProxy.setBindingValue(property, value);
	}

	@Override
	public void setDataContext(Object data) {
		mProxy.setDataContext(data);
		try {
			if(mProxy.hasBindingValue(R.styleable.DataContext_binding_onclick)) {
				Object objonclick = mProxy.getBindingValue(R.styleable.
						DataContext_binding_onclick);
				if(objonclick != null && objonclick instanceof OnClickListener) {
					setOnClickListener((OnClickListener)objonclick);
				} else {
					setOnClickListener(null);
				}
			}
			if(mProxy.hasBindingValue(R.styleable.DataContext_binding_background)) {
				Object objbackground = mProxy.getBindingValue(R.styleable.
						DataContext_binding_background);
				if(objbackground != null && objbackground instanceof Integer) {
					ThemeHelper.setBackgroundResourceID(this, (Integer) objbackground);
				} else {
					setBackgroundResource(0);
				}
			}
			if(mProxy.hasBindingValue(R.styleable.DataContext_binding_visibility)) {
				Object objvisibility = mProxy.getBindingValue(R.styleable.
						DataContext_binding_visibility);
				if(objvisibility != null && objvisibility instanceof Integer) {
					setVisibility((Integer)objvisibility);
				} else {
					setVisibility(View.VISIBLE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object getDataContext() {
		return mProxy.getDataContext();
	}
	
	@Override
	public void setFragment(IFragment fragment) {
		mProxy.setFragment(fragment);
	}
	
	@Override
	public IFragment getFragment() {
		return mProxy.getFragment();
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
    	if(getContext() instanceof Activity) {
    		((Activity) getContext()).startActivity(new Intent(getContext(), cls));
    	}
    }
    
    protected void startActivity(String action) {
    	if(getContext() instanceof Activity) {
    		((Activity) getContext()).startActivity(new Intent(action));
    	}
    }
    
    protected void startActivity(Class<?> cls, Intent data) {
    	if(getContext() instanceof Activity) {
    		if(data == null) {
	    		return;
	    	}
	    	data.setClass(getContext(), cls);
	    	((Activity) getContext()).startActivity(data);
    	}
    }
    
    protected void startActivity(String action, Intent data) {
    	if(getContext() instanceof Activity) {
    		if(data == null) {
	    		return;
	    	}
	    	data.setAction(action);
	    	((Activity) getContext()).startActivity(data);
    	}
    }
    
    protected void startActivityForResult(Class<?> cls, int requestCode) {
    	if(getContext() instanceof Activity) {
    		((Activity) getContext()).startActivityForResult(new Intent(getContext(), cls), requestCode);
    	}
    }
    
    protected void startActivityForResult(String action, int requestCode) {
    	if(getContext() instanceof Activity) {
    		((Activity) getContext()).startActivityForResult(new Intent(action), requestCode);
    	}
    }
    
    protected void startActivityForResult(Class<?> cls, Intent data, int requestCode) {
    	if(getContext() instanceof Activity) {
    		if(data == null) {
	    		return;
	    	}
	    	data.setClass(getContext(), cls);
	    	((Activity) getContext()).startActivityForResult(data, requestCode);
    	}
    }
    
    protected void startActivityForResult(String action, Intent data, int requestCode) {
    	if(getContext() instanceof Activity) {
    		if(data == null) {
	    		return;
	    	}
	    	data.setAction(action);
	    	((Activity) getContext()).startActivityForResult(data, requestCode);
    	}
    }

}
