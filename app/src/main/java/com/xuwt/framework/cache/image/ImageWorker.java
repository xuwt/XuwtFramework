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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.xuwt.framework.BuildConfig;
import com.xuwt.framework.graphics.MBitmapDrawable;
import com.xuwt.framework.graphics.MRecycleBitmapDrawable;
import com.xuwt.framework.utils.OSUtils;

import java.lang.ref.WeakReference;

/**
 * This class wraps up completing some arbitrary long running work when loading a bitmap to an
 * ImageView. It handles things like using a memory and disk cache, running the work in a background
 * thread and setting a placeholder image.
 */
public abstract class ImageWorker {
    private static final String TAG = "ImageWorker";
    private static final int FADE_IN_TIME = 200;

    protected ImageCache mImageCache;
    private boolean mFadeInBitmap = true;
    private boolean mExitTasksEarly = false;
    protected boolean mPauseWork = false;
    private final Object mPauseWorkLock = new Object();

    protected Resources mResources;

    private static final int MESSAGE_CLEAR = 0;
    private static final int MESSAGE_INIT_DISK_CACHE = 1;

    protected ImageWorker(Context context) {
        mResources = context.getResources();
    }

    public void loadImage(ImageView imageView, Object data) {
        loadImage(imageView, data, 0, 0, true, null);
    }
    
    /**
     * Load an image specified by the data parameter into an ImageView (override
     * {@link com.xuwt.framework.cache.image.ImageWorker#processBitmap(Object)} to define the processing logic). A memory and
     * disk cache will be used if an {@link ImageCache} has been added using
     * {@link com.xuwt.framework.cache.image.ImageWorker#addImageCache(android.support.v4.app.FragmentManager, ImageCache.ImageCacheParams)}. If the
     * image is found in the memory cache, it is set immediately, otherwise an {@link AsyncTask}
     * will be created to asynchronously load the bitmap.
     * 
     * @param imageView
     * @param data
     * @param loadingResId
     * @param loadFromWeb
     * @param callback
     */
    public void loadImage(ImageView imageView, Object data, int loadingResId, 
    		int loaderrResId, boolean loadFromWeb, ProcessCallback callback) {
        if (data == null) {
            return;
        }
        String url = String.valueOf(data);
        BitmapDrawable value = null;

        if (mImageCache != null) {
            value = mImageCache.getBitmapFromMemCache(url);
        }

        if (value != null) {
        	if (null != callback) {
        		callback.onMemoryOver(url, value);
			}
            // Bitmap found in memory cache
            imageView.setImageDrawable(value);
            return;
        }
        boolean notrunning = false;
        BitmapWorkerTask task = null;
        synchronized (imageView) {
        	notrunning = cancelPotentialWork(imageView, data, loadFromWeb);
        	if(notrunning) {
	        	if (callback != null) {
	        		callback.onTaskBegin(url);
				}
	            task = new BitmapWorkerTask(imageView, loaderrResId, loadFromWeb, callback);
				imageView.setImageDrawable(getAsyncDrawable(loadingResId, task));
	        }
		}
	    // NOTE: This uses a custom version of AsyncTask that has been pulled from the
	    // framework and slightly modified. Refer to the docs at the top of the class
	    // for more info on what was changed.
        if(notrunning && task != null) {
        	task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, data);
        }
    }

    /**
     * Adds an {@link ImageCache} to this {@link com.xuwt.framework.cache.image.ImageWorker} to handle disk and memory bitmap
     * caching.
     * @param fragmentManager
     * @param cacheParams The cache parameters to use for the image cache.
     */
    public void addImageCache(ImageCache.ImageCacheParams cacheParams) {
        mImageCache = ImageCache.get(cacheParams);
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }

    /**
     * Adds an {@link ImageCache} to this {@link com.xuwt.framework.cache.image.ImageWorker} to handle disk and memory bitmap
     * caching.
     * @param activity
     * @param diskCacheDirectoryName See
     * {@link ImageCache.ImageCacheParams#ImageCacheParams(android.content.Context, String)}.
     */
    public void addImageCache(Context context) {
        mImageCache = ImageCache.get(new ImageCache.ImageCacheParams(context));
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }

    /**
     * If set to true, the image will fade-in once it has been loaded by the background thread.
     */
    public void setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
    }

    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
        setPauseWork(false);
    }

    /**
     * Subclasses should override this to define any processing or work that must happen to produce
     * the final bitmap. This will be executed in a background thread and be long running. For
     * example, you could resize a large bitmap here, or pull down an image from the network.
     *
     * @param data The data to identify which image to process, as provided by
     *            {@link com.xuwt.framework.cache.image.ImageWorker#loadImage(Object, android.widget.ImageView)}
     * @return The processed bitmap
     */
    protected abstract Bitmap processBitmap(Object data, ProcessCallback callbak);

    /**
     * @return The {@link ImageCache} object currently being used by this ImageWorker.
     */
    protected ImageCache getImageCache() {
        return mImageCache;
    }

    /**
     * Cancels any pending work attached to the provided ImageView.
     * @param imageView
     */
    public static void cancelWork(ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            bitmapWorkerTask.cancel(true);
            if (BuildConfig.DEBUG) {
                final Object bitmapData = bitmapWorkerTask.data;
                Log.d(TAG, "cancelWork - cancelled work for " + bitmapData);
            }
        }
    }

    /**
     * Returns true if the current work has been canceled or if there was no work in
     * progress on this image view.
     * Returns false if the work in progress deals with the same data. The work is not
     * stopped in that case.
     */
    public static boolean cancelPotentialWork(ImageView imageView, Object data, boolean loadFromWeb) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.data;
            final boolean bitmapFromWeb = bitmapWorkerTask.mLoadFromWeb;
            if (bitmapData == null || !bitmapData.equals(data) || bitmapFromWeb != loadFromWeb) {
                bitmapWorkerTask.cancel(true);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "cancelPotentialWork - cancelled work for " + data);
                }
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active work task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
    	try {
    		if (imageView != null) {
    			final Drawable drawable = imageView.getDrawable();
    			if (drawable instanceof AsyncDrawable) {
    				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
    				return asyncDrawable.getBitmapWorkerTask();
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

    /**
     * The actual AsyncTask that will asynchronously process the image.
     */
    private class BitmapWorkerTask extends AsyncTask<Object, Void, BitmapDrawable> {
        private Object data;
        private final WeakReference<ImageView> imageViewReference;
        private boolean mLoadFromWeb;
        private int mLoadErrResId;
		private ProcessCallback mCallback;

        public BitmapWorkerTask(ImageView imageView, int loaderrResId, 
        		boolean loadFromWeb, ProcessCallback callback) {
            mLoadErrResId = loaderrResId;
            mLoadFromWeb = loadFromWeb;
            mCallback = callback;
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Background processing.
         */
        @Override
        protected BitmapDrawable doInBackground(Object... params) {

            data = params[0];
            final String dataString = String.valueOf(data);
            Bitmap bitmap = null;
            BitmapDrawable drawable = null;

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "doInBackground - starting work for " + dataString);
            }
            try {
	            // Wait here if work is paused and the task is not cancelled
	            synchronized (mPauseWorkLock) {
	                while (mPauseWork && !isCancelled()) {
	                    try {
	                        mPauseWorkLock.wait();
	                    } catch (InterruptedException e) {}
	                }
	            }
	
	            // If the image cache is available and this task has not been cancelled by another
	            // thread and the ImageView that was originally bound to this task is still bound back
	            // to this task and our "exit early" flag is not set then try and fetch the bitmap from
	            // the cache
	            if (mImageCache != null && !isCancelled() && getAttachedImageView() != null
	                    && !mExitTasksEarly) {
	                bitmap = mImageCache.getBitmapFromDiskCache(dataString);
	            }
	
	            // load from local file path
	            if (bitmap == null && !isCancelled() && getAttachedImageView() != null
	                    && !mExitTasksEarly && mCallback != null) {
	            	bitmap = mCallback.loadBitmapFromFile(dataString);
	            }
	            
	            // If the bitmap was not found in the cache and this task has not been cancelled by
	            // another thread and the ImageView that was originally bound to this task is still
	            // bound back to this task and our "exit early" flag is not set, then call the main
	            // process method (as implemented by a subclass)
	            if (bitmap == null && !isCancelled() && getAttachedImageView() != null
	                    && !mExitTasksEarly && mLoadFromWeb) {
	                bitmap = processBitmap(params[0], mCallback);
	            }
	
	            // If the bitmap was processed and the image cache is available, then add the processed
	            // bitmap to the cache for future use. Note we don't check if the task was cancelled
	            // here, if it was, and the thread is still running, we may as well add the processed
	            // bitmap to our cache as it might be used again in the future
	            if (bitmap != null) {
	                Bitmap newBitmap = null;
	                
	            	if(null != mCallback) {
	            		newBitmap = mCallback.processBitmap(dataString, bitmap);
	            	} else {
	            		newBitmap = bitmap;
	            	}
	            	
	                if (OSUtils.hasHoneycomb()) {
	                    // Running on Honeycomb or newer, so wrap in a standard BitmapDrawable
	                    drawable = new MBitmapDrawable(mResources, newBitmap);
	                } else {
	                    // Running on Gingerbread or older, so wrap in a RecyclingBitmapDrawable
	                    // which will recycle automagically
	                    drawable = new MRecycleBitmapDrawable(mResources, newBitmap);
	                }
	
	                if (mImageCache != null) {
	                    mImageCache.addBitmapToCache(dataString, drawable);
	                }
	                
	                // recycle bitmap
	                if(newBitmap != null && !newBitmap.equals(bitmap)) {
	                	bitmap.recycle();
	                	bitmap = null;
	                }
	            }
            } catch (Exception e) {
            	e.printStackTrace();
            }
	
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "doInBackground - finished work for " + dataString);
            }

            return drawable;
        }

        /**
         * Once the image is processed, associates it to the imageView
         */
        @Override
        protected void onPostExecute(BitmapDrawable value) {
            // if cancel was called on this task or the "exit early" flag is set then we're done
            if (isCancelled() || mExitTasksEarly) {
                value = null;
                return;
            }
            String url = String.valueOf(data);
            final ImageView imageView = getAttachedImageView();
            if (imageView != null) {
            	if(value != null) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onPostExecute - setting bitmap for " + url);
                    }
            		setImageDrawable(imageView, value);
                	if (mCallback != null) {
        				mCallback.onTaskOver(url, value);
        			}
            	} else {
            		setImageDrawable(imageView, getAsyncDrawable(mLoadErrResId, null));
            		if (mCallback != null) {
        				mCallback.onTaskError(url);
        			}
            	}
            }
        }

        @Override
        protected void onCancelled(BitmapDrawable value) {
            super.onCancelled(value);
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
            final ImageView imageView = getAttachedImageView();
			if(imageView != null) {
				if(null != mCallback) {
					mCallback.onTaskCancel(String.valueOf(data));
				}
			}
        }

        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Returns null otherwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }

    /**
     * A custom Drawable that will be attached to the imageView while the work is in progress.
     * Contains a reference to the actual worker task, so that it can be stopped if a new binding is
     * required, and makes sure that only the last started worker process can bind its result,
     * independently of the finish order.
     */
    private static class AsyncDrawable extends BitmapDrawable {
    	
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
    
    private AsyncDrawable getAsyncDrawable(int resId, BitmapWorkerTask task) {
    	Bitmap bitmap = null;
        if(resId > 0)
        	bitmap = BitmapFactory.decodeResource(mResources, resId);
        return new AsyncDrawable(mResources, bitmap, task);
    }

    /**
     * Called when the processing is complete and the final drawable should be 
     * set on the ImageView.
     *
     * @param imageView
     * @param drawable
     */
    private void setImageDrawable(ImageView imageView, Drawable drawable) {
        if (mFadeInBitmap) {
            // Transition drawable with a transparent drawable and the final drawable
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                    		imageView.getDrawable(),
                            drawable
                    });
            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        } else {
            imageView.setImageDrawable(drawable);
        }
    }

    /**
     * Pause any ongoing background work. This can be used as a temporary
     * measure to improve performance. For example background work could
     * be paused when a ListView or GridView is being scrolled using a
     * {@link android.widget.AbsListView.OnScrollListener} to keep
     * scrolling smooth.
     * <p>
     * If work is paused, be sure setPauseWork(false) is called again
     * before your fragment or activity is destroyed (for example during
     * {@link android.app.Activity#onPause()}), or there is a risk the
     * background thread will never finish.
     */
    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }
    
    public void clearQueue() {
		AsyncTask.clearQuene();
	}

    protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            switch ((Integer)params[0]) {
                case MESSAGE_CLEAR:
                    clearCacheInternal();
                    break;
                case MESSAGE_INIT_DISK_CACHE:
                    initDiskCacheInternal();
                    break;
            }
            return null;
        }
    }

    protected void initDiskCacheInternal() {
        if (mImageCache != null) {
            mImageCache.initDiskCache();
        }
    }

    protected void clearCacheInternal() {
        if (mImageCache != null) {
            mImageCache.clearCache();
        }
    }

    public void clearCache() {
        new CacheAsyncTask().execute(MESSAGE_CLEAR);
    }

    public void clearCacheImmediate() {
    	clearCacheInternal();
    }
    
    public abstract static class ProcessCallback {
    	
		/**
		 * 从内存中加载成功
		 */
		public void onMemoryOver(String url, Drawable drawable) {
		};
		
		/**
		 * 从内存获取失败，开始Task前
		 */
		public void onTaskBegin(String url){
		}
		
		/**
		 * 开始Http请求前
		 */
		public void onTaskHttpBegin(String url){
		}

		/**
		 * Http请求进度
		 */
		public void onTaskHttpProcess(String url, long current, long total) {
		};

		/**
		 * 从缓存中加载成功
		 */
		public void onTaskOver(String url, Drawable drawable) {
		};

		/**
		 * Http请求发生错误
		 */
		public void onTaskError(String url) {
		};
		
		/**
		 * 从缓存中取消加载
		 */
		public void onTaskCancel(String url) {
		}

		/**
		 * 在缓存中未找到，则从文件中加载
		 * 注意: 此方法在非UI线程调用
		 */
		public Bitmap loadBitmapFromFile(String url) {
            return null;
		}

		/**
		 * Http请求成功后，对bitmap进行操作，eg：保存bitmap至文件，制作圆角图片等
		 * 注意: 此方法在非UI线程调用
		 */
		public Bitmap processBitmap(String url, Bitmap bitmap) {
            return bitmap;
		}
	}
}
