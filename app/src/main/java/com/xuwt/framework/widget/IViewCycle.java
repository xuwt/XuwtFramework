package com.xuwt.framework.widget;


import android.content.Intent;
import android.os.Bundle;

import com.xuwt.framework.manager.ITheme;

public interface IViewCycle extends ITheme {
	void onStart();
	void onResume();
	void onPause();
	void onStop();
	void onDestroy();
	void onRestart();
	void onNewIntent(Intent intent);
	void onSaveInstanceState(Bundle outState);
	void onRestoreInstanceState(Bundle savedInstanceState);
}
