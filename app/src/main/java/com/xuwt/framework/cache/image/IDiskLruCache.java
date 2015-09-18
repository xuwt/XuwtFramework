package com.xuwt.framework.cache.image;


import android.graphics.Bitmap;

public interface IDiskLruCache {
	boolean contains(String key);
	Bitmap getBitmap(String key, ImageCache cache);
	void putBitmap(String key, Bitmap data, String ext);
	void clearCache();
	void setCompressParams(ImageCache.CompressFormat compressFormat, int quality);
}
