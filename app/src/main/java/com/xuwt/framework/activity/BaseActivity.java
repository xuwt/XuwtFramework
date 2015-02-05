/**
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuwt.framework.activity;

import android.app.Activity;
import android.view.View;


import com.xuwt.framework.annotation.ViewInject;

import java.lang.reflect.Field;

public abstract class BaseActivity extends Activity {


	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		initInjectedView(this);
	}

	

	public static void initInjectedView(Activity activity){
		initInjectedView(activity, activity.getWindow().getDecorView());
	}
	
	
	public static void initInjectedView(Object injectedSource,View sourceView){
		Field[] fields = injectedSource.getClass().getDeclaredFields();
		if(fields!=null && fields.length>0){
			for(Field field : fields){
				try {
					field.setAccessible(true);
					
					if(field.get(injectedSource)!= null )
						continue;
				
					ViewInject viewInject = field.getAnnotation(ViewInject.class);
					if(viewInject!=null){
						
						int viewId = viewInject.id();
					    field.set(injectedSource,sourceView.findViewById(viewId));

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
