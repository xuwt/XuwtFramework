package com.xuwt.framework.graphics;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.xuwt.framework.BuildConfig;


public class MRecycleBitmapDrawable extends MBitmapDrawable {

	public MRecycleBitmapDrawable(Resources res, Bitmap bitmap) {
		super(res, bitmap);
	}

	@Override
    protected synchronized void checkState() {
        // If the drawable cache and display ref counts = 0, and this drawable
        // has been displayed, then recycle
        if (mCacheRefCount <= 0 && mDisplayRefCount <= 0 && mHasBeenDisplayed
                && hasValidBitmap()) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "No longer being used or cached so recycling. "
                        + toString());
            }
            getBitmap().recycle();
        }
    }

    private synchronized boolean hasValidBitmap() {
        Bitmap bitmap = getBitmap();
        return bitmap != null && !bitmap.isRecycled();
    }
    
}
