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
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.xuwt.baidu.framework.BuildConfig;
import com.xuwt.framework.BaseApplication;
import com.xuwt.framework.cache.image.ImageCache.ImageCacheParams;
import com.xuwt.framework.manager.WindowManager;
import com.xuwt.framework.net.engine.IEngineTask;
import com.xuwt.framework.net.http.Params;
import com.xuwt.framework.utils.FileUtils;
import com.xuwt.framework.utils.HttpUtils;
import com.xuwt.framework.utils.ImageUtils;
import com.xuwt.framework.utils.LogUtils;
import com.xuwt.framework.utils.OSUtils;

import org.apache.http.protocol.HTTP;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;


/**
 * A simple subclass of {@link ImageResizer} that fetches and resizes images fetched from a URL.
 */
public class ImageFetcher extends ImageResizer {
    private static final String TAG = "ImageFetcher";
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

    private static final byte[] mLockInstance = new byte[0];
    private static ImageFetcher instance;
    public static ImageFetcher get() {
    	synchronized (mLockInstance) {
			if(instance == null) {
				instance = new ImageFetcher(BaseApplication.get());
				instance.initCache(BaseApplication.get());
			}
			return instance;
		}
    }
    
    public static ImageFetcher get(int diskCacheSize) {
    	synchronized (mLockInstance) {
			if(instance == null) {
				instance = new ImageFetcher(BaseApplication.get());
				instance.initCache(BaseApplication.get(), diskCacheSize);
			}
			return instance;
		}
    }

    private ImageFetcher(Context context) {
        super(context);
    }
    
    private void initCache(Context context) {
    	initCache(context, DEFAULT_DISK_CACHE_SIZE);
    }
    
    private void initCache(Context context, int diskCacheSize) {
		ImageCacheParams cacheParams = new ImageCacheParams(BaseApplication.get());
		if (WindowManager.get().getScreenWidth() > 480 && OSUtils.getMaxMemory() < 70) {
			cacheParams.setMemCacheSizePercent(0.1f);
		} else {
			cacheParams.setMemCacheSizePercent(0.15f);
		}
		cacheParams.setDiskCacheSize(DEFAULT_DISK_CACHE_SIZE);
		addImageCache(cacheParams);
    }
    
    /**
     * get the used space
     * @return
     */
    public long getUsableSpace() {
    	return FileUtils.getFolderLength(mImageCache.getCacheParams().diskCacheDir);
    }

    /**
     * The main process method, which will be called by the ImageWorker in the AsyncTask background
     * thread.
     *
     * @param data The data to load the bitmap, in this case, a regular http URL
     * @return The downloaded and resized bitmap
     */
    @Override
    protected Bitmap processBitmap(Object data, final ProcessCallback callback) {
    	if (BuildConfig.DEBUG) {
            Log.d(TAG, "processBitmap - " + data);
        }
    	final String url = String.valueOf(data);
    	if(TextUtils.isEmpty(url)) {
    		return null;
    	}
        Bitmap bitmap = null;
        // check is file
        File file = new File(url);
        if(file.exists()) {
        	bitmap = ImageUtils.file2Bitmap(BaseApplication.get(), url,
                    WindowManager.get().getScreenWidth() * 2,
                    WindowManager.get().getScreenHeight() * 2);
        } else {
	        final ByteArrayOutputStream output = new ByteArrayOutputStream(IO_BUFFER_SIZE);
	        try {
	        	final byte[] locktask = new byte[0];
	        	IEngineTask task = new IEngineTask() {
					
					@Override
					public void run() {
						synchronized (locktask) {
							downloadUrlToStream(url, output, callback);
						}
					}
					
					@Override
					public void notifyWake() {
						synchronized (locktask) {
							try {
								locktask.notify();
							} catch (Exception e) {
								
							}
						}
					}
					
					@Override
					public boolean isValid() {
						return true;
					}
					
					@Override
					public int getPriority() {
						return IEngineTask.IMAGEPRIORITY;
					}
					
					@Override
					public void cancel() { }
				};
	        	synchronized (locktask) {
	            	try {
	            		ThreadPoolExecutor.get().execute(task);
	            		locktask.wait();
	            	} catch (Exception e) {
	            		
	            	}
				}
		        byte[] byteImage = ((ByteArrayOutputStream)output).toByteArray();
		        bitmap = decodeSampledBitmapFromByte(byteImage, mImageCache);
	        } catch (Exception e) {
	            Log.e(TAG, "processBitmap - Exception " + e);
	        } finally {
	        	try {
	        		if(output != null) {
	        			output.close();
	        		}
	    		} catch (final IOException e) { }
	        }
        }
        return bitmap;
    }
    
    /**
     * Download a bitmap from a URL and write the content to an output stream.
     *
     * @param urlString The URL to fetch
     * @return true if successful, false otherwise
     */
    public boolean downloadUrlToStream(final String urlString, OutputStream outputStream, final ProcessCallback callback) {

        byte[] buffer = new byte[IO_BUFFER_SIZE];
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        OutputStream out_write = null;

        try {
        	if(!HttpUtils.isNetWorkConnected(BaseApplication.get())) {
        		return false;
        	}
        	if (callback != null) {
        		new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
		        		callback.onTaskHttpBegin(urlString);
					}
				});
			}
            urlConnection = HttpUtils.createHttpConnection(urlString, true);
            setURLConnectionParam(urlConnection);
            LogUtils.info(TAG, "get image : " + urlString);
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            out_write = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
            final long total = urlConnection.getContentLength();
            int b, read = 0;
            while ((b = in.read(buffer)) != -1) {
            	out_write.write(buffer, 0, b);
            	read += b;
            	final long readfinal = read;
            	if(callback != null) {
            		new Handler(Looper.getMainLooper()).post(new Runnable() {
    					@Override
    					public void run() {
    	            		callback.onTaskHttpProcess(urlString, readfinal, total);
    					}
    				});
            	}
            }
            return true;
        } catch (final IOException e) {
            Log.e(TAG, "Error in downloadBitmap - " + e);
        } catch (Exception e){
            Log.e(TAG, "Error in downloadBitmap - " + e);
        } finally {
            if (urlConnection != null) {
                try {
                	urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                urlConnection = null;
            }
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (out_write != null) {
                	out_write.close();
                	out_write = null;
                }
            } catch (final IOException e) {}
        }
        return false;
    }
    
    private void setURLConnectionParam(HttpURLConnection conn) {
		try {
		    conn.setConnectTimeout(Params.HTTPGET_CONN_TIME_OUT_LONG);
		    conn.setReadTimeout(Params.HTTPGET_READ_TIME_OUT_LONG);
		    conn.setRequestProperty(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
			conn.addRequestProperty(HTTP.CONTENT_TYPE, HTTP.OCTET_STREAM_TYPE);
			conn.setRequestMethod("GET");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
    }
}