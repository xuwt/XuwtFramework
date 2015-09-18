/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.xuwt.framework.cache.image;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.StatFs;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.xuwt.framework.BuildConfig;
import com.xuwt.framework.cache.DiskLruCache;
import com.xuwt.framework.graphics.MBitmapDrawable;
import com.xuwt.framework.utils.FileUtils;
import com.xuwt.framework.utils.OSUtils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


/**
 * This class handles disk and memory caching of bitmaps in conjunction with the
 * {@link ImageWorker} class and its subclasses. Use
 * {@link com.xuwt.framework.cache.image.ImageCache#get(android.support.v4.app.FragmentManager, com.xuwt.framework.cache.image.ImageCache.ImageCacheParams)} to get an instance of this
 * class, although usually a cache should be added directly to an {@link ImageWorker} by calling
 * {@link ImageWorker#addImageCache(android.support.v4.app.FragmentManager, com.xuwt.framework.cache.image.ImageCache.ImageCacheParams)}.
 */
public class ImageCache {
    private static final String TAG = "ImageCache";
    
    public static final String ROOT_CACHE_DIR = "images";
    public static final String THUMB_CACHE_DIR = "thumb";
    public static final String ALBUM_CACHE_DIR = "album";

    // Default memory cache size in kilobytes
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 5; // 5MB

    // Default disk cache size in bytes
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

    // Compression settings when writing images to disk cache
    private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.AUTO;
    private static final int DEFAULT_COMPRESS_QUALITY = 70;

    // Constants to easily toggle various caches
    private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
    private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
    private static final boolean DEFAULT_INIT_DISK_CACHE_ON_CREATE = false;

    private IDiskLruCache mDiskLruCache;
    private LruCache<String, BitmapDrawable> mMemoryCache;
    private ImageCacheParams mCacheParams;
    private final Object mDiskCacheLock = new Object();
    private final Object mReusableBitmapsLock = new Object();
    private boolean mDiskCacheStarting = true;

    private HashSet<SoftReference<Bitmap>> mReusableBitmaps;

    public ImageCacheParams getCacheParams() {
    	return mCacheParams;
    }
    
    /**
     * Create a new ImageCache object using the specified parameters. This should not be
     * called directly by other classes, instead use
     * {@link com.xuwt.framework.cache.image.ImageCache#get(android.support.v4.app.FragmentManager, com.xuwt.framework.cache.image.ImageCache.ImageCacheParams)} to fetch an ImageCache
     * instance.
     *
     * @param cacheParams The cache parameters to use to initialize the cache
     */
    private ImageCache(ImageCacheParams cacheParams) {
        init(cacheParams);
    }

    private static final byte[] mLock = new byte[0];
	private static ImageCache mInstance = null;
	/**
     * Return an {@link com.xuwt.framework.cache.image.ImageCache} instance. A {@link RetainFragment} is used to retain the
     * ImageCache object across configuration changes such as a change in device orientation.
     *
     * @param fragmentManager The fragment manager to use when dealing with the retained fragment.
     * @param cacheParams The cache parameters to use if the ImageCache needs instantiation.
     * @return An existing retained ImageCache object or a new one if one did not exist
     */
	public final static ImageCache get(ImageCacheParams cacheParams) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new ImageCache(cacheParams);
            }
            return mInstance;
        }
    }

    /**
     * Initialize the cache, providing all parameters.
     *
     * @param cacheParams The cache parameters to initialize the cache
     */
    private void init(ImageCacheParams cacheParams) {
        mCacheParams = cacheParams;

        // Set up memory cache
        if (mCacheParams.memoryCacheEnabled) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Memory cache created (size = " + mCacheParams.memCacheSize + ")");
            }

            // If we're running on Honeycomb or newer, then
            if (OSUtils.hasHoneycomb()) {
                mReusableBitmaps = new HashSet<SoftReference<Bitmap>>();
            }

            mMemoryCache = new LruCache<String, BitmapDrawable>(mCacheParams.memCacheSize) {

                /**
                 * Notify the removed entry that is no longer being cached
                 */
                @Override
                protected void entryRemoved(boolean evicted, String key,
                        BitmapDrawable oldValue, BitmapDrawable newValue) {
                	if(evicted || newValue == null) {
	                    if (MBitmapDrawable.class.isInstance(oldValue)) {
	                        // The removed entry is a recycling drawable, so notify it 
	                        // that it has been removed from the memory cache
	                        ((MBitmapDrawable) oldValue).setIsCached(false);
	                    } else {
	                        // The removed entry is a standard BitmapDrawable
	
	                        if (OSUtils.hasHoneycomb()) {
	                            // We're running on Honeycomb or later, so add the bitmap
	                            // to a SoftRefrence set for possible use with inBitmap later
	                        	synchronized (mReusableBitmapsLock) {
	                                mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
								}
	                        }
	                    }
                	}
                }

                /**
                 * Measure item size in kilobytes rather than units which is more practical
                 * for a bitmap cache
                 */
                @Override
                protected int sizeOf(String key, BitmapDrawable value) {
                    final int bitmapSize = getBitmapSize(value) / 1024;
                    return bitmapSize == 0 ? 1 : bitmapSize;
                }
            };
        }

        // By default the disk cache is not initialized here as it should be initialized
        // on a separate thread due to disk access.
        if (cacheParams.initDiskCacheOnCreate) {
            // Set up disk cache
            initDiskCache();
        }
    }

    /**
     * Initializes the disk cache.  Note that this includes disk access so this should not be
     * executed on the main/UI thread. By default an ImageCache does not initialize the disk
     * cache when it is created, instead you should call initDiskCache() to initialize it on a
     * background thread.
     */
    public void initDiskCache() {
        // Set up disk cache
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache == null) {
                File diskCacheDir = mCacheParams.diskCacheDir;
                if (mCacheParams.diskCacheEnabled && diskCacheDir != null) {
                    if (!diskCacheDir.exists()) {
                        diskCacheDir.mkdirs();
                    }
                    if (getUsableSpace(diskCacheDir) > mCacheParams.diskCacheSize) {
                        try {
                        	mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, mCacheParams.diskCacheSize);
                        	mDiskLruCache.setCompressParams(mCacheParams.compressFormat, mCacheParams.compressQuality);
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "Disk cache initialized");
                            }
                        } catch (final Exception e) {
                            mCacheParams.diskCacheDir = null;
                            Log.e(TAG, "initDiskCache - " + e);
                        }
                    }
                }
            }
            mDiskCacheStarting = false;
            mDiskCacheLock.notifyAll();
        }
    }

    /**
     * Adds a bitmap to both memory and disk cache.
     * @param data Unique identifier for the bitmap to store
     * @param value The bitmap drawable to store
     */
    public void addBitmapToCache(String data, BitmapDrawable value) {
        if (data == null || value == null) {
            return;
        }

        // Add to memory cache
        if (mMemoryCache != null) {
            if (MBitmapDrawable.class.isInstance(value)) {
                // The removed entry is a recycling drawable, so notify it 
                // that it has been added into the memory cache
                ((MBitmapDrawable) value).setIsCached(true);
            }
            mMemoryCache.put(data, value);
        }

        synchronized (mDiskCacheLock) {
            // Add to disk cache
            if (mDiskLruCache != null) {
                final String key = hashKeyForDisk(data);
                final String ext = data.substring(data.lastIndexOf("."));
                try {
                	if (mDiskLruCache != null && !mDiskLruCache.contains(key)) {
                		mDiskLruCache.putBitmap(key, value.getBitmap(), ext);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "addBitmapToCache - " + e);
                }
            }
        }
    }

    /**
     * Get from memory cache.
     *
     * @param data Unique identifier for which item to get
     * @return The bitmap drawable if found in cache, null otherwise
     */
    public BitmapDrawable getBitmapFromMemCache(String data) {
        BitmapDrawable memValue = null;

        if (mMemoryCache != null) {
            memValue = mMemoryCache.get(data);
        }

        if (BuildConfig.DEBUG && memValue != null) {
            Log.d(TAG, "Memory cache hit");
        }

        return memValue;
    }

    /**
     * Get from disk cache.
     *
     * @param data Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getBitmapFromDiskCache(String data) {
        final String key = hashKeyForDisk(data);
        Bitmap bitmap = null;

        synchronized (mDiskCacheLock) {
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {}
            }
            if (mDiskLruCache != null) {
                try {
                	bitmap = mDiskLruCache.getBitmap(key, this);
                } catch (final Exception e) {
                    Log.e(TAG, "getBitmapFromDiskCache - " + e);
                }
            }
            return bitmap;
        }
    }

    /**
     * @param options - BitmapFactory.Options with out* options populated
     * @return Bitmap that case be used for inBitmap
     */
    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;
        synchronized (mReusableBitmapsLock) {
	        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
	        	final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
	            ArrayList<Object> lstDelete = new ArrayList<Object>();
	            Bitmap item;
	            while (iterator.hasNext()) {
	            	SoftReference<Bitmap> obj = iterator.next();
	            	item = obj.get();
		
	                if (null != item && item.isMutable()) {
	                    // Check to see it the item can be used for inBitmap
	                    if (canUseForInBitmap(item, options)) {
	                        bitmap = item;
	                        // Remove from reusable set so it can't be used again
	                        //iterator.remove();
	                        lstDelete.add(obj);
	                        break;
	                    }
	                } else {
	                    // Remove from the set if the reference has been cleared.
	                    //iterator.remove();
	                    lstDelete.add(obj);
	                }
	            }
	            mReusableBitmaps.retainAll(lstDelete);
	        }
        }
        return bitmap;
    }

    /**
     * Clears both the memory and disk cache associated with this ImageCache object. Note that
     * this includes disk access so this should not be executed on the main/UI thread.
     */
    public void clearCache() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Memory cache cleared");
            }
        }

        synchronized (mDiskCacheLock) {
            mDiskCacheStarting = true;
            if (mDiskLruCache != null) {
                try {
                    mDiskLruCache.clearCache();
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Disk cache cleared");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "clearCache - " + e);
                }
                mDiskLruCache = null;
                initDiskCache();
            }
        }
    }

    /**
     * A holder class that contains cache parameters.
     */
    public static class ImageCacheParams {
        
        public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
        public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
        public File diskCacheDir;
        public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
        public int compressQuality = DEFAULT_COMPRESS_QUALITY;
        public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
        public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
        public boolean initDiskCacheOnCreate = DEFAULT_INIT_DISK_CACHE_ON_CREATE;

        /**
         * Create a set of image cache parameters that can be provided to
         * {@link com.xuwt.framework.cache.image.ImageCache#get(android.support.v4.app.FragmentManager, com.xuwt.framework.cache.image.ImageCache.ImageCacheParams)} or
         * {@link ImageWorker#addImageCache(android.support.v4.app.FragmentManager, com.xuwt.framework.cache.image.ImageCache.ImageCacheParams)}.
         * @param context A context to use.
         */
        public ImageCacheParams(Context context) {
        	this(context, THUMB_CACHE_DIR);
        }

        /**
         * Create a set of image cache parameters that can be provided to
         * {@link com.xuwt.framework.cache.image.ImageCache#get(android.support.v4.app.FragmentManager, com.xuwt.framework.cache.image.ImageCache.ImageCacheParams)} or
         * {@link ImageWorker#addImageCache(android.support.v4.app.FragmentManager, com.xuwt.framework.cache.image.ImageCache.ImageCacheParams)}.
         * @param context A context to use.
         */
        public ImageCacheParams(Context context, String subdir) {
            diskCacheDir = getDiskCacheDir(context, subdir);
        }
        
        /**
         * Create a set of image cache parameters that can be provided to
         * {@link com.xuwt.framework.cache.image.ImageCache#get(android.support.v4.app.FragmentManager, com.xuwt.framework.cache.image.ImageCache.ImageCacheParams)} or
         * {@link ImageWorker#addImageCache(android.support.v4.app.FragmentManager, com.xuwt.framework.cache.image.ImageCache.ImageCacheParams)}.
         * @param cachedir A unique subdirectory that will be appended to the
         *                               application cache directory. Usually "cache" or "images"
         *                               is sufficient.
         */
        public ImageCacheParams(File cachedir) {
            diskCacheDir = cachedir;
        }

        /**
         * Sets the memory cache size based on a percentage of the max available VM memory.
         * Eg. setting percent to 0.2 would set the memory cache to one fifth of the available
         * memory. Throws {@link IllegalArgumentException} if percent is < 0.05 or > .8.
         * memCacheSize is stored in kilobytes instead of bytes as this will eventually be passed
         * to construct a LruCache which takes an int in its constructor.
         *
         * This value should be chosen carefully based on a number of factors
         * Refer to the corresponding Android Training class for more discussion:
         * http://developer.android.com/training/displaying-bitmaps/
         *
         * @param percent Percent of available app memory to use to size memory cache
         */
        public void setMemCacheSizePercent(float percent) {
            if (percent < 0.05f || percent > 0.8f) {
                throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                        + "between 0.05 and 0.8 (inclusive)");
            }
            memCacheSize = Math.round(percent * Runtime.getRuntime().maxMemory() / 1024);
        }
        
        /**
         * set disk cache size
         * @param size byte
         */
        public void setDiskCacheSize(int size) {
            if (size <= 0) {
                throw new IllegalArgumentException("setDiskCacheSize - size must be > 0");
            }
            diskCacheSize = size;
        }
    }

    /**
     * @param candidate - Bitmap to check
     * @param targetOptions - Options that have the out* value populated
     * @return true if <code>candidate</code> can be used for inBitmap re-use with
     *      <code>targetOptions</code>
     */
    private static boolean canUseForInBitmap(
            Bitmap candidate, BitmapFactory.Options targetOptions) {
        int width = targetOptions.outWidth / targetOptions.inSampleSize;
        int height = targetOptions.outHeight / targetOptions.inSampleSize;

        return candidate.getWidth() == width && candidate.getHeight() == height;
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath = FileUtils.getCachePath();
        return new File(cachePath + File.separator + ROOT_CACHE_DIR + 
        		File.separator + uniqueName);
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * memery cache key.
     */
    public static String hashKeyForMem(String url, String urlExtra) {
        return hashKeyForDisk(url + urlExtra);
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Get the size in bytes of a bitmap in a BitmapDrawable.
     * @param value
     * @return size in bytes
     */
    @TargetApi(12)
    public static int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();

        if (OSUtils.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
    
    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to check
     * @return The space available in bytes
     */
    @TargetApi(9)
    public static long getUsableSpace(File path) {
        if (OSUtils.hasGingerbread()) {
            //return path.getUsableSpace();
        	try {
                Method mthd = File.class.getDeclaredMethod("getUsableSpace");
                return (Long) mthd.invoke(path);
            } catch(Exception e) {
            	e.printStackTrace();
            }
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }
    
    public enum CompressFormat {
        JPEG    (0),
        PNG     (1),
        WEBP    (2),
        AUTO    (3);

        CompressFormat(int nativeInt) {
            this.nativeInt = nativeInt;
        }
        final int nativeInt;
        
        public Bitmap.CompressFormat getAndroidCompressFormat(String ext) {
        	if(this == AUTO) {
        		if(TextUtils.isEmpty(ext)) {
        			return Bitmap.CompressFormat.PNG;
        		} else {
        			ext = ext.toLowerCase().trim();
        			if(".png".equals(ext)) {
        				return Bitmap.CompressFormat.PNG;
        			} else if(".webp".equals(ext)) {
        				return Bitmap.CompressFormat.PNG;
        			} else if(".jpg".equals(ext) || ".jpeg".equals(ext) || ".bmp".equals(ext) || ".gif".equals(ext)) {
        				return Bitmap.CompressFormat.JPEG;
        			} else {
        				return Bitmap.CompressFormat.PNG;
        			}
        		}
        	} else {
        		return Bitmap.CompressFormat.valueOf(this.toString());
        	}
        }
    }

}
