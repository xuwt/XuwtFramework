package com.xuwt.framework.widget.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.xuwt.framework.R;
import com.xuwt.framework.data.IDataContext;
import com.xuwt.framework.fragment.IFragment;
import com.xuwt.framework.manager.ThemeHelper;


public class MButton extends Button implements IDataContext, IView {

	protected ViewProxy mProxy;
	
	public MButton(Context context) {
		super(context);
		initializeView(context);
	}
	
	public MButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView(context);
		initializeAttr(attrs, 0);
	}
	
	public MButton(Context context, AttributeSet attrs, int defStyle) {
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
		
		String text = typedArray.getString(R.styleable.DataContext_binding_text);
		if(mProxy.setBindingValue(R.styleable.DataContext_binding_text, text)) {
			setText(null);
		}
		String onclick = typedArray.getString(R.styleable.DataContext_binding_onclick);
		if(mProxy.setBindingValue(R.styleable.DataContext_binding_onclick, onclick)) {
			setOnClickListener(null);
		}
		String background = typedArray.getString(R.styleable.DataContext_binding_background);
		if(mProxy.setBindingValue(R.styleable.DataContext_binding_background, background)) {
			setBackgroundResource(0);
		}
		String forground = typedArray.getString(R.styleable.DataContext_binding_forground);
		mProxy.setBindingValue(R.styleable.DataContext_binding_forground, forground);
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
			if(mProxy.hasBindingValue(R.styleable.DataContext_binding_text)) {
				Object objText = mProxy.getBindingValue(R.styleable.
						DataContext_binding_text);
				if(objText != null) {
					setText(objText.toString());
				} else {
					setText(null);
				}
			}
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
			if(mProxy.hasBindingValue(R.styleable.DataContext_binding_forground)) {
				Object objforground = mProxy.getBindingValue(R.styleable.
						DataContext_binding_forground);
				if(objforground != null && objforground instanceof Integer) {
					ThemeHelper.setTextColor(this, (Integer)objforground);
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
}
