package com.xuwt.framework.data;

public interface IDataContext {
	
	void setBindingValue(int property, String value);
	void setDataContext(Object data);
	Object getDataContext();
}
