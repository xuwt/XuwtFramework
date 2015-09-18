package com.xuwt.framework.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.xuwt.framework.manager.WindowManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageUtils {

    public static InputStream drawable2InputStream(Drawable d) {  
        Bitmap bitmap = drawable2Bitmap(d);
        return bitmap2InputStream(bitmap);
    }
    
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		try {
			if (drawable instanceof BitmapDrawable) {
				return ((BitmapDrawable) drawable).getBitmap();
			} else {
				Bitmap bitmap = Bitmap.createBitmap(
								drawable.getIntrinsicWidth(),
								drawable.getIntrinsicHeight(),
								drawable.getOpacity() != PixelFormat.OPAQUE ? 
										Config.ARGB_8888 : Config.RGB_565);
				Canvas canvas = new Canvas(bitmap);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				drawable.draw(canvas);
				return bitmap;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public static byte[] drawable2Bytes(Drawable drawable, CompressFormat format) {
		Bitmap bitmap = drawable2Bitmap(drawable);
		if(bitmap == null)
			return null;
		return bitmap2Bytes(bitmap, format);
	}

    public static InputStream bitmap2InputStream(Bitmap bm) {  
        return bitmap2InputStream(bm, CompressFormat.JPEG, 100);
    } 
    
    public static InputStream bitmap2InputStream(Bitmap bm, CompressFormat format, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(format, quality, baos);  
        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
        return is;  
    }
	
	public static Drawable bitmap2Drawable(Context context, Bitmap bitmap) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}
	
	public static byte[] bitmap2Bytes(Bitmap bitmap, CompressFormat format) {
		return bitmap2Bytes(bitmap, format, true);
	}
	
	public static byte[] bitmap2Bytes(Bitmap bitmap, CompressFormat format, boolean recycle) {
		ByteArrayOutputStream oStream = null;
		try {
			oStream = new ByteArrayOutputStream();
			if(bitmap == null)
				return null;
			if(format == null)
				format = CompressFormat.PNG;
			bitmap.compress(format, 100, oStream);
			if(recycle) {
				bitmap.recycle();
				bitmap = null;
			}
			oStream.flush();
			return oStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oStream != null) {
				try {
					oStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static InputStream byte2InputStream(byte[] b) {  
        ByteArrayInputStream bais = new ByteArrayInputStream(b);  
        return bais;  
    }
	
	public static Bitmap bytes2Bimap(byte[] b){
		try {
	        if(b != null && b.length!=0) {
	        	return BitmapFactory.decodeByteArray(b, 0, b.length);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Drawable bytes2Drawable(Context context, byte[] b){
        Bitmap bitmap = bytes2Bimap(b);
        if(bitmap != null)
        	return bitmap2Drawable(context, bitmap);
        else
        	return null;
	}

    public static Drawable inputStream2Drawable(Context context, InputStream is) {  
        Bitmap bitmap = inputStream2Bitmap(is);  
        return bitmap2Drawable(context, bitmap);
    }

    public static Bitmap inputStream2Bitmap(InputStream is) {  
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inPreferredConfig = Config.RGB_565;
    	options.inPurgeable = true;
    	options.inInputShareable = true;
    	options.inJustDecodeBounds = false;
    	options.inTempStorage=new byte[32 * 1024]; 
    	try {
    		return BitmapFactory.decodeStream(is, null, options);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return null;  
    }

    public static byte[] inputStream2Bytes(InputStream is) {
    	ByteArrayOutputStream bytestream = new ByteArrayOutputStream();  
		try {
			byte[] buffer = new byte[1024];  
            int len = 0;  
            while ((len = is.read(buffer)) != -1) {  
            	bytestream.write(buffer, 0, len);  
            }
			return bytestream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bytestream.close();
			} catch (Exception e) { }
		}
		return null;
    }
    
    public static Bitmap uri2Bitmap(Context context, Uri uri) {
    	return uri2Bitmap(context, uri, WindowManager.get().getScreenWidth(),
    			WindowManager.get().getScreenHeight());
    }
    
    public static Bitmap uri2Bitmap(Context context, Uri uri, int width, int height) {
    	Bitmap bitmap = null;
    	BufferedInputStream bis = null;
    	InputStream is = null;
    	try {
    		is = context.getContentResolver().openInputStream(uri);
    		bis = new BufferedInputStream(is, 32 * 1024);
	    	BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeStream(bis, null, options);
			int dimen = Math.min(width, height);
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options,
					dimen, dimen);
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
	    	options.inPreferredConfig = Config.RGB_565;
	    	options.inPurgeable = true;
	    	options.inInputShareable = true;
	    	options.inTempStorage=new byte[32 * 1024]; 
			bitmap = BitmapFactory.decodeStream(bis, null, options);
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		if(bis != null) {  
                try {  
                	bis.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }
                bis = null;
            }
    		if(is != null) {  
                try {  
                    is.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }
                is = null;
            }
    	}
		return bitmap;
	}
    
    public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}
    
    public static byte[] uri2Bytes(Context context, Uri uri) {
    	InputStream is = null;
		try {
			is = context.getContentResolver().openInputStream(uri);
	    	return inputStream2Bytes(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				is = null;
			}
		}
		return null;
	}
    
    /**
     * sd卡文件转为bitmap
     * @param context
     * @param file
     * @return
     */
    public static Bitmap file2Bitmap(Context context, String file) {
    	return file2Bitmap(context, file, WindowManager.get().getScreenWidth(), 
    			WindowManager.get().getScreenHeight());
    }
    
    /**
     * sd卡文件转为bitmap
     * @param context
     * @param file
     * @param width
     * @param height
     * @return
     */
    public static Bitmap file2Bitmap(Context context, String file, int width, int height) {
        FileInputStream fs = null;
    	try {
	    	BitmapFactory.Options options = new BitmapFactory.Options();
	    	if(width > 0 && height > 0) {
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(file, options);
				int dimen = Math.min(width, height);
				// Calculate inSampleSize
				options.inSampleSize = calculateInSampleSize(options,
						dimen, dimen);
	    	}
	    	// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			options.inDither=false;
			options.inPurgeable=true;
			options.inInputShareable=true;
			options.inTempStorage=new byte[32 * 1024];
			File f = new File(file);
			if(f.exists()) {
		        fs = new FileInputStream(f);  
	            if(fs!=null) {
	                return BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
	            }
			}
    	} catch (Exception e) {
    		e.printStackTrace();
        } finally {   
            if(fs != null) {  
                try {  
                    fs.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }
                fs = null;
            }  
        }
        return null;  
	}
    
    /**
     * 取得bitmap的rect大小
     * @param context
     * @param file
     * @return
     */
    public static Rect file2Rect(Context context, String file) {
    	Rect rect = null;
    	try {
    		BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(file, options);
			rect = new Rect(0, 0, options.outWidth, options.outHeight);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		return rect;
	}
    
    /**
     * 按比例压缩图片
     * @param bitmap
     * @param scale
     * @return
     */
    public static Bitmap compressBitmapWithScale(Bitmap bitmap, double scale){
        bitmap = zoomBitmap(bitmap, bitmap.getWidth() / scale,
                bitmap.getHeight() / scale);
        return bitmap;
    }
    
    /**
	 * 按大小压缩图片
	 * @param image
	 * @return
	 */
	public  static Bitmap compressBitmapWithSize(Bitmap image, int maxLen) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(CompressFormat.JPEG, 100, baos);
        int quality = 100;
        while (baos.toByteArray().length / 1024 > maxLen) {
            baos.reset();  
            image.compress(CompressFormat.JPEG, quality, baos);
            quality -= 10;
        }
        ByteArrayInputStream isBm = null;
        try {
	        isBm = new ByteArrayInputStream(baos.toByteArray());  
	        BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inDither=false;
			options.inPurgeable=true;
			options.inInputShareable=true;
			options.inTempStorage=new byte[32 * 1024];
			return BitmapFactory.decodeStream(isBm, null, options);  
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if(isBm != null) {
        		try {
        			isBm.close();
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        		isBm = null;
        	}
        }
        return null;
    }
    
    /**
     * 根据最大、最小宽度、高度截取图片
     * @param unscaledBitmap
     * @param minWidth 单位像素px
     * @param maxWidth 单位像素px
     * @param minHeight 单位像素px
     * @param maxHeight 单位像素px
     * @return
     */
    public static Bitmap compressBitmapWithRect(Bitmap unscaledBitmap, 
    		int minWidth, int maxWidth, int minHeight, int maxHeight) {
    	return compressBitmapWithRect(unscaledBitmap, minWidth, maxWidth, 
    			minHeight, maxHeight, true);
    }
    
    /**
     * 根据最大、最小宽度、高度截取图片
     * @param unscaledBitmap
     * @param minWidth 单位像素px
     * @param maxWidth 单位像素px
     * @param minHeight 单位像素px
     * @param maxHeight 单位像素px
     * @param recycle 是否回收bitmap
     * @return
     */
    public static Bitmap compressBitmapWithRect(Bitmap unscaledBitmap, 
    		int minWidth, int maxWidth, int minHeight, int maxHeight, boolean recycle) {
    	int srcWidth = unscaledBitmap.getWidth();
    	int srcHeight = unscaledBitmap.getHeight();
    	int dstWidth = 0, dstHeight = 0;
    	float widthAspect = 1.0f, heightAspect = 1.0f;
    	if(srcWidth < minWidth)
    		widthAspect = (float)minWidth / (float)srcWidth;
    	else if(srcWidth > maxWidth)
    		widthAspect = (float)maxWidth / (float)srcWidth;
    	if(srcHeight < minHeight)
    		heightAspect = (float)minHeight / (float)srcHeight;
    	else if(srcHeight > maxHeight)
    		heightAspect = (float)maxHeight / (float)srcHeight;
    	
    	if(widthAspect > 1.0 || heightAspect > 1.0) {
    		float aspect = Math.max(widthAspect, heightAspect);
			dstWidth = (int)(srcWidth * aspect);
			dstHeight = (int)(srcHeight * aspect);
			if(dstHeight > maxHeight) {
				dstHeight = maxHeight;
				srcHeight = (int)(maxHeight / aspect);
			}
			if(dstWidth > maxWidth) {
				dstWidth = maxWidth;
				srcWidth = (int)(maxWidth / aspect);
			}
    	} else if(widthAspect < 1.0 || heightAspect < 1.0) {
    		float aspect = Math.min(widthAspect, heightAspect);
			dstWidth = (int)Math.ceil(srcWidth * aspect);
			dstHeight = (int)Math.ceil(srcHeight * aspect);
			if(dstHeight < minHeight) {
				dstHeight = (int)minHeight;
				dstWidth = (int)maxWidth;
				srcWidth = (int)((float)dstWidth * ((float)srcHeight / (float)dstHeight));
			}
			if(dstWidth < minWidth) {
				dstWidth = (int)minWidth;
				dstHeight = (int)maxHeight;
				srcHeight = (int)((float)dstHeight * ((float)srcWidth / (float)dstWidth));
			}
    	} else {
    		dstWidth = srcWidth;
			dstHeight = srcHeight;
    	}
    	Rect srcRect= new Rect(0, 0, (int)srcWidth, (int)srcHeight);
    	Rect dstRect= new Rect(0, 0, (int)dstWidth, (int)dstHeight);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
        if(recycle) {
	        unscaledBitmap.recycle();
	        unscaledBitmap = null;
        }
        return scaledBitmap;
    }
    
    /**
     * 自定义(宽，高)压缩图片
     * @param bgimage
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bgimage, double newWidth, double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }
    
	/**
	 * 旋转图片
	 * @param bitmap
	 * @param degree
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, float degree){
		/*
		Matrix matrix = new Matrix();
		matrix.preRotate(degree);
		return Bitmap.createBitmap(bitmap ,0,0, bitmap .getWidth(), bitmap .getHeight(),matrix,true);
		*/
		Matrix m = new Matrix();
		m.setRotate(degree, 
				(float) bitmap.getWidth() / 2,
				(float) bitmap.getHeight() / 2);
		float targetX, targetY;
		if (degree == 90) {
			targetX = bitmap.getHeight();
			targetY = 0;
		} else {
			targetX = bitmap.getHeight();
			targetY = bitmap.getWidth();
		}

		final float[] values = new float[9];
		m.getValues(values);

		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];

		m.postTranslate(targetX - x1, targetY - y1);

		Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(),
				Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bitmap2);
		canvas.drawBitmap(bitmap, m, paint);
		
		if(bitmap != null) {
			if(!bitmap.isRecycled()) {
				bitmap.recycle();
			}
			bitmap = null;
		}
		return bitmap2;
	}
    
    /**
     * 在一张图片上打水印另外一张图片
     * @param canvas
     * @param drawable
     * @return
     */
    public static Bitmap drawWaterMark(Drawable canvas, Drawable waterMark){
        Bitmap bitmap = Bitmap.createBitmap(
                        canvas.getIntrinsicWidth(),
                        canvas.getIntrinsicHeight(),
                        canvas.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                                : Config.RGB_565);
        Canvas c = new Canvas(bitmap);
        canvas.setBounds(0, 0, canvas.getIntrinsicWidth(),
                canvas.getIntrinsicHeight());
        canvas.draw(c);
        waterMark.setAlpha(100);
        waterMark.setBounds(0, 0, canvas.getIntrinsicWidth(),
        		waterMark.getIntrinsicHeight());
        waterMark.draw(c);
        return bitmap;
    }
    
    /**
     * 获取圆型图片
     * @param bitmap
     * @param gravity_x Gravity 常量
     * @param gravity_y Gravity 常量
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap, int gravity_x, int gravity_y){
        Bitmap output = null;
        try {
        	if(bitmap == null)
        		return null;
        	int diameterPx = Math.min(bitmap.getWidth(), bitmap.getHeight());
        	output = Bitmap.createBitmap(diameterPx, diameterPx, Config.ARGB_8888);
        	Canvas canvas = new Canvas(output);  

            final int color = 0xff424242;
            Paint paint = new Paint();
            Rect rectF = null;
            Rect rectT = new Rect(0, 0, diameterPx, diameterPx);
            
            if(bitmap.getWidth() >= bitmap.getHeight()) {
            	if(gravity_x == Gravity.LEFT) {
            		rectF = new Rect(0, 0, bitmap.getHeight(), bitmap.getHeight());
            	} else if(gravity_x == Gravity.RIGHT) {
            		rectF = new Rect(bitmap.getWidth() - diameterPx, 0, bitmap.getWidth(), bitmap.getHeight());
            	} else {
            		rectF = new Rect((bitmap.getWidth() - diameterPx) / 2, 0, 
            				(bitmap.getWidth() - diameterPx) / 2 + diameterPx, bitmap.getHeight());
            	}
            } else {
            	if(gravity_y == Gravity.TOP) {
            		rectF = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
            	} else if(gravity_y == Gravity.BOTTOM) {
            		rectF = new Rect(0, bitmap.getHeight() - diameterPx, bitmap.getWidth(), bitmap.getHeight());
            	} else {
            		rectF = new Rect(0, (bitmap.getHeight() - diameterPx) / 2, bitmap.getWidth(), 
            				(bitmap.getHeight() - diameterPx) / 2 + diameterPx);
            	}
            }
            
            paint.setAntiAlias(true);
            paint.setColor(color);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawRoundRect(new RectF(rectT), diameterPx / 2f, diameterPx / 2f, paint);  
            
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rectF, rectT, paint);
        } catch (OutOfMemoryError e) {
        	System.gc();
        	output = null;
        } finally {
        	if(bitmap != null) {
        		if(!bitmap.isRecycled()) {
        			bitmap.recycle();
        		}
        		bitmap = null;
        	}
        }
        return output;
    }
    
    /**
     * 获取圆型图片
     * @param bitmap
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap){
    	return getCircleBitmap(bitmap, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
    }
    
    /**
     * 获取圆角图片
     * @param bitmap
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx){
        Bitmap output = null;
        try {
        	if(bitmap == null)
        		return null;
        	output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        	Canvas canvas = new Canvas(output);  

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);  
            
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
            
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        } catch (OutOfMemoryError e) {
        	System.gc();
        	output = null;
        }
        return output;
    }
    
    /**
     * 获取圆角图片
     * @param drawable
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Drawable drawable, float roundPx) {
        return getRoundedCornerBitmap(drawable2Bitmap(drawable), roundPx);
    }
	
    /************************** begin Webp *********************************/
    
    /************************** end Webp *********************************/
    
    public static void saveBitma2Disk(Bitmap bitmap, String folder, String filename) {
    	saveBitma2Disk(bitmap, folder, filename, CompressFormat.JPEG);
    }
    
    public static void saveBitma2Disk(Bitmap bitmap, String folder, String filename, CompressFormat format) {
    	String filepath = folder;
    	if(!filepath.endsWith(File.separator))
    		filepath += File.separator;
    	filepath += filename;
    	try {
	    	FileUtils.createFolderDirectory(folder);
	    	File file = new File(filepath);
	    	if(file.exists())
	    		file.delete();
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));  
	        bitmap.compress(format, 100, bos);  
	        bos.flush();
	        bos.close();
    	} catch (Exception e) { }
    }
    


	public static Bitmap createVRepeaterInDp(Context context, float dpWwidth, float dpHeight, Bitmap src) {
		int pixWidth = UnitUtils.dip2pix(context, (int)dpWwidth);
		int pixHeight = UnitUtils.dip2pix(context, (int)dpHeight);
		return createVRepeaterInPix(pixWidth, pixHeight, src);
	}

	public static Bitmap createVRepeaterInPix(int pixWidth, int pixHeight,
			Bitmap src) {
		int count = (pixHeight + src.getHeight() - 1) / src.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(pixWidth, pixHeight,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		for (int idx = 0; idx < count; ++idx) {
			Rect srcRc = new Rect(0, 0, src.getWidth(), src.getHeight());
			Rect dstRc = new Rect(0, idx * src.getHeight(), pixWidth, (idx + 1)
					* src.getHeight());
			canvas.drawBitmap(src, srcRc, dstRc, null);
		}
		return bitmap;
	}
	
	public static Bitmap createHRepeaterInDp(Context context, float dpWwidth, float dpHeight, Bitmap src){
		int pixWidth = UnitUtils.dip2pix(context, (int)dpWwidth);
		int pixHeight = UnitUtils.dip2pix(context, (int)dpHeight);
		return createHRepeaterInPix(pixWidth, pixHeight, src);
	}	

	public static Bitmap createHRepeaterInPix(int pixWidth, int pixHeight, Bitmap src){
		int count = (pixWidth + src.getWidth() - 1) / src.getWidth();
		Bitmap bitmap = Bitmap.createBitmap(pixWidth, pixHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		for(int idx = 0; idx < count; ++ idx){
			Rect srcRc = new Rect(0, 0, src.getWidth(), src.getHeight());
			Rect dstRc = new Rect(idx * src.getWidth(), 0, (idx + 1)* src.getWidth(), pixHeight);
			canvas.drawBitmap(src, srcRc, dstRc, null);
		}
		return bitmap;
	}	

	public static void drawHRepeaterInPix(int left, int top, int right, int bottom, Bitmap src, Canvas canvas){
		int count = (right - left + src.getWidth() - 1) / src.getWidth();
		for(int idx = 0; idx < count; ++ idx){
			Rect srcRc = new Rect(0, 0, src.getWidth(), src.getHeight());
			Rect dstRc = new Rect(left + idx * src.getWidth(), 0, left + (idx + 1)* src.getWidth(), bottom - top);
			canvas.drawBitmap(src, srcRc, dstRc, null);
		}
	}

	public static void drawVRepeaterInPix(int left, int top, int right, int bottom, Bitmap src, Canvas canvas) {
		int count = (bottom - top + src.getHeight() - 1) / src.getHeight();
		for (int idx = 0; idx < count; ++idx) {
			Rect srcRc = new Rect(0, 0, src.getWidth(), src.getHeight());
			Rect dstRc = new Rect(0, top + idx * src.getHeight(), right - left, top + (idx + 1)
					* src.getHeight());
			canvas.drawBitmap(src, srcRc, dstRc, null);
		}
	}

	public static Bitmap decodeResource(Resources resources, int id) {
		TypedValue value = new TypedValue();
		resources.openRawResource(id, value);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inTargetDensity = value.density;
		return BitmapFactory.decodeResource(resources, id, opts);
	}
	
	public static Bitmap compressBitmapByBound(Context context, Uri uri, 
			float compressWidth, float compressHeight) {
		InputStream is = null;
		try {
	        BitmapFactory.Options options = new BitmapFactory.Options();  
	        options.inJustDecodeBounds = true;  
	        BitmapFactory.decodeStream(context.getContentResolver().
	        		openInputStream(uri), null, options);
	        int w = options.outWidth;  
	        int h = options.outHeight;  
	        float ww = compressWidth;
	        float hh = compressHeight;;
	        int be = 1;
	        if (w > h && w > ww) {
	            be = (int) (options.outWidth / ww);  
	        } else if (w < h && h > hh) {
	            be = (int) (options.outHeight / hh);  
	        }  
	        if (be <= 0)  
	            be = 1;  
	        options.inJustDecodeBounds = false;  
	        options.inSampleSize = be;
	        options.inDither=false;
			options.inPurgeable=true;
			options.inInputShareable=true;
			options.inTempStorage=new byte[32 * 1024];
	        is = context.getContentResolver().openInputStream(uri);
	        return BitmapFactory.decodeStream(is, null, options);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				is = null;
			}
		}
		return null;
    } 
	
	public static Uri compressBitmapByQuanlity(Bitmap bitmap, 
			long compressFileSize, String folder, String file) {  
        int options = 100;  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bitmap.compress(CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length > compressFileSize) {
            if(options <= 10) {
            	options -= 1;
            	if(options <= 0) {
            		break;
            	}
            } else {
            	options -= 10;
            }
            baos.reset();
            bitmap.compress(CompressFormat.JPEG, options, baos);
        }
		File compressFile = new File(folder, file);
		boolean result = FileUtils.saveBytes2CachePath(baos.toByteArray(), compressFile.getPath());
		if(result) {
			return Uri.fromFile(compressFile);
		} else {
			return null;
		}
    }  
	
	/**
	 * 获取图片的旋转角度
	 * @param file
	 * @return
	 */
	public static int getBitmapDegree(String file) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(file);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree; 
	}
	
	/**
	 * 截获view的截图并保存至文件
	 * @param v
	 * @param file
	 */
	public static void captureView2File(View v, String folder, String file) {
		captureView2File(v, folder, file, CompressFormat.PNG);
	}
	
	
	/**
	 * 截获view的截图并保存至文件
	 * @param v
	 * @param file
	 */
	public static void captureView2File(View v, String folder, String file, CompressFormat format) {
        int width = v.getWidth();
        int height = v.getHeight();
        if(v instanceof ViewGroup) {
        	int heightv = 0;
        	ViewGroup vg = (ViewGroup) v;
	        for (int i = 0; i < vg.getChildCount(); i++) {
	        	heightv += vg.getChildAt(i).getHeight();
	        }
	        height = Math.max(height, heightv);
        }
        Bitmap bitmap = null;
        try {
        	FileUtils.createFolderDirectory(folder);
	        bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	        final Canvas canvas = new Canvas(bitmap);
	        v.draw(canvas);
	        // 测试输出
	        FileOutputStream out = new FileOutputStream(folder + 
	        		(folder.endsWith(File.separator) ? "" : File.separator) + file);
            if (null != out) {
                bitmap.compress(format, 80, out);
                out.flush();
                out.close();
                out = null;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if(bitmap != null) {
        		if(!bitmap.isRecycled()) {
        			bitmap.recycle();
        		}
        		bitmap = null;
        	}
        }
    }
}
