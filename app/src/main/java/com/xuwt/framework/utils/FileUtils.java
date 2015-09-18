package com.xuwt.framework.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.xuwt.framework.BaseApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


public class FileUtils {
	public static final String framworkCachePath = File.separator + ".xuwt.framework";
	public static final String framworkCacheLogPath = framworkCachePath + File.separator + "Log";
	public static final String framworkCacheCrashPath = framworkCachePath + File.separator + "Crash";
	public static final String framworkCacheDBPath = framworkCachePath + File.separator + "Database";
	public static final String framworkCacheApkPath = framworkCachePath + File.separator + "Apk";

	private static final int BUFF_SIZE = 1024 * 10;

	/**
	 * 是否挂载sd卡
	 * @return 
	 */
	public static boolean existExternalStorage() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获取sd卡根目录
	 * @return 
	 */
	public static String getSDCardRootPath() {
		String cachePath;
    	Context context = BaseApplication.get();
        try {
            String exterState = Environment.getExternalStorageState();
            boolean hasExternalStorage = exterState.equalsIgnoreCase(Environment.MEDIA_MOUNTED);
            if(hasExternalStorage) {
            	cachePath = Environment.getExternalStorageDirectory().getPath();
            } else {
            	cachePath = context.getCacheDir().getPath();
            }
        } catch (Exception e) {
            File cacheDir = context.getCacheDir();
            if (cacheDir == null) {
                return null;
            }
            cachePath = cacheDir.getPath();
        }
        return cachePath;
	}
	
	/**
	 * 获取程序缓存的目录
	 * @return
	 */
	public static String getCachePath() {
		String cachePath;
    	Context context = BaseApplication.get();
        try {
        	boolean hasExternalStorage = false;
        	boolean hasInternalStorage = false;
            String exterState = Environment.getExternalStorageState();
            hasExternalStorage = exterState.equalsIgnoreCase(Environment.MEDIA_MOUNTED);
            if(!hasExternalStorage) {
            	if(OSUtils.hasGingerbread() && !Environment.isExternalStorageRemovable() &&
            			!exterState.equalsIgnoreCase(Environment.MEDIA_SHARED)) {
            		hasInternalStorage = true;
            	}
            }
            if(hasExternalStorage || hasInternalStorage) {
            	if (OSUtils.hasFroyo()) {
            		cachePath = context.getExternalCacheDir().getPath();
		        } else {
			        cachePath = Environment.getExternalStorageDirectory().getPath() + 
			        		"/Android/data/" + context.getPackageName() + "/cache/";
		        }
            } else {
            	cachePath = context.getCacheDir().getPath();
            }
        } catch (Exception e) {
            File cacheDir = context.getCacheDir();
            if (cacheDir == null) {
                return null;
            }
            cachePath = cacheDir.getPath();
        }
        if(cachePath.endsWith(File.separator)) {
        	cachePath = cachePath.substring(0, cachePath.length() - 1);
        }
        return cachePath;
	}
	
	/**
	 * 保存字符串至程序缓存目录
	 * @param data
	 * @param filename
	 * @return
	 */
	public static String saveString2CachePath(String data, String filename) {
		return saveString2CachePath(data, filename, null);
	}
	
	/**
	 * 保存字符串至程序缓存目录
	 * @param data
	 * @param encoding
	 * @param filename
	 * @return
	 */
	public static String saveString2CachePath(String data, String filename, String encoding) {
		String cachePath = getCachePath();
		if(TextUtils.isEmpty(cachePath))
			return null;
		String filePath = cachePath + File.separator + filename;
		OutputStream out = null;
		try {
			out = openFileOutputStream(filePath);
			if(TextUtils.isEmpty(encoding)) {
				out.write(data.getBytes());
			} else {
				out.write(data.getBytes(encoding));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out = null;
			}
		}
		return filePath;
	}
	
	/**
	 * 保存字节数组至文件
	 * @param bytes
	 * @param filePath
	 * @return
	 */
	public static boolean saveBytes2CachePath(byte[] bytes, String filePath) {
		OutputStream out = null;
		try {
			out = openFileOutputStream(filePath);
			out.write(bytes);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out = null;
			}
		}
		return false;
	}

	public static String getStringFromCachePath(String filename) {
		return getStringFromCachePath(filename, null);
	}
	
	/**
	 * 从程序缓存获取文件内容
	 * @param filename
	 * @param encoding
	 * @return
	 */
	public static String getStringFromCachePath(String filename, String encoding) {
		String cachePath = getCachePath();
		if(TextUtils.isEmpty(cachePath))
			return null;
		String filePath = cachePath + File.separator + filename;
		File file = new File(filePath);
		if(!file.exists()) {
			return "";
		}
		String content = "";
		InputStream input = null;
		InputStreamReader reader = null;
		StringWriter sw = new StringWriter();
		try {
			input = new FileInputStream(filePath);
			if(TextUtils.isEmpty(encoding)) {
				reader = new InputStreamReader(input);
			} else {
				reader = new InputStreamReader(input, encoding);
			}
			char[] buffer = new char[1024 * 4];
			int n = 0;
			while ((n = reader.read(buffer)) != -1) {
				sw.write(buffer, 0, n);
			}
			content = sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(sw != null) {
				try {
					sw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				sw = null;
			}
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				reader = null;
			}
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				input = null;
			}
		}
		return content;
	}

	/**
	 * 打开文件流
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
	public static FileOutputStream openFileOutputStream(String filePath) throws IOException {
		File file = new File(filePath);
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file + "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null && parent.exists() == false) {
				if (parent.mkdirs() == false) {
					throw new IOException("File '" + file + "' could not be created");
				}
			}
			file.createNewFile();
		}
		return new FileOutputStream(file);
	}
	
	/**
	 * 打开文件流
	 * @param filePath
	 * @return FileInputStream
	 * @throws java.io.IOException
	 */
	public static FileInputStream openFileInputStream(String filePath) throws IOException {
		File file = new File(filePath);
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file + "' does not exist");
		}
		return new FileInputStream(file);
	}
	
	/**
	 * 创建文件目录
	 * @param directory
	 */
	public static void createFolderDirectory(String directory) {
		File file = new File(directory);
        if (!file.exists()) {
            try {
            	file.mkdirs(); 
            } catch (Exception e) {
            }
        } 
	}
	
	/**
	 * 向stream写入文件
	 * @param context
	 * @param uri
	 * @param os
	 * @return
	 */
	public static void writeUriToStream(Context context, Uri uri, OutputStream os) {
		try {
			InputStream is = context.getContentResolver().openInputStream(uri);
			byte[] buffer = new byte[1024];  
            int len = 0;  
            while ((len = is.read(buffer)) != -1) {  
            	os.write(buffer, 0, len);  
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取文件夹的大小
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	public static long getFolderLength(File folder) { 
		long size = 0; 
		try {
			File flist[] = folder.listFiles(); 
			for (int i = 0; i < flist.length; i++) { 
				if (flist[i].isDirectory()) { 
					size = size + getFolderLength(flist[i]); 
				} else { 
					size = size + flist[i].length(); 
				} 
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size; 
	}

	
	/**
	 * 格式化文件大小为字符串
	 * @param fileS
	 * @return
	 */
	public static String formetFileLength(long fileS) {
		DecimalFormat df = new DecimalFormat("0.##");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取文件夹中的文件个数（递归）
	 * @param folder
	 * @return
	 */
	public long getFileNumberInFolder(File folder) { 
		long size = 0; 
		File flist[] = folder.listFiles(); 
		size = flist.length; 
		for (int i = 0; i < flist.length; i++) { 
			if (flist[i].isDirectory()) { 
				size += getFileNumberInFolder(flist[i]); 
				size--; 
			} 
		} 
		return size; 
	}
	
	/**
	 * 获取文件长度
	 * @param context
	 * @param uri
	 * @return
	 */
	public static long getFileLength(Context context, Uri uri) {
        try {
        	InputStream is = context.getContentResolver().openInputStream(uri);
            return is.available();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return 0;
	}
	
	/**
	 * 获取文件长度
	 * @param filePath
	 * @return
	 */
	public static long getFileLength(String filePath) {
        try {
        	File file = new File(filePath);
            if (file.exists()) {
                return file.length();
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return 0;
	}
    
	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * @param zipFile
	 * @param folderPath
	 * @return
	 * @throws java.util.zip.ZipException
	 * @throws java.io.IOException
	 */
	public int upZipFile(File zipFile, String folderPath) 
			throws ZipException, IOException {
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[BUFF_SIZE];
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory()) {
				String dirstr = folderPath + ze.getName();
				// dirstr.trim();
				dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
				File f = new File(dirstr);
				f.mkdir();
				continue;
			}
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					getRealFileName(folderPath, ze.getName())));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		return 0;
	}
	
	/**
	 * 获取Assert文件夹下的文件字节
	 * @param assertPath
	 */
	public static byte[] getAssertByte(String assertPath) {
		byte[] bytes = null;
		InputStream assertInput = null;
		try {
	        assertInput = BaseApplication.get().getAssets().open(assertPath);  
			if (assertInput != null) {
				bytes = new byte[assertInput.available()];
				assertInput.read(bytes);
				assertInput.close();
			}  
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(assertInput != null) {
					assertInput.close();
					assertInput = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}
	
	/**
	 * 获取Assert文件夹下的字符串
	 * @param assertPath
	 */
	public static String getAssertString(String assertPath) {
		StringBuilder sbResult = new StringBuilder();
		InputStreamReader inputReader = null;
		BufferedReader bufReader = null;
		try {
			inputReader = new InputStreamReader(BaseApplication.get()
					.getAssets().open(assertPath));
			bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
				sbResult.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufReader != null) {
					bufReader.close();
					bufReader = null;
				}
				if (inputReader != null) {
					inputReader.close();
					inputReader = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sbResult.toString();
	}
	
	/**
	 * 获取Assert文件夹下的字符串数组
	 * @param assertPath
	 */
	public static ArrayList<String> getAssertStringArray(String assertPath) {
		ArrayList<String> lstResult = new ArrayList<String>();
		InputStreamReader inputReader = null;
		BufferedReader bufReader = null;
		try {
			inputReader = new InputStreamReader(BaseApplication.get()
					.getAssets().open(assertPath));
			bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
				lstResult.add(line);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufReader != null) {
					bufReader.close();
					bufReader = null;
				}
				if (inputReader != null) {
					inputReader.close();
					inputReader = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lstResult;
	}
	
	/**
	 * 复制Assert文件夹下的文件至SD卡
	 * @param assertPath
	 * @param sdcardPath
	 */
	public static void copyAssert2SDCard(String assertPath, String sdcardPath) {
		OutputStream sdcardOutput = null;
		InputStream assertInput = null;
		try {
			sdcardOutput = openFileOutputStream(sdcardPath);
	        assertInput = BaseApplication.get().getAssets().open(assertPath);  
	        byte[] buffer = new byte[1024];  
	        int length;  
	        while ((length = assertInput.read(buffer)) > 0) {  
	        	sdcardOutput.write(buffer, 0, length);  
	        }  
	        sdcardOutput.flush();  
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(sdcardOutput != null) {
					sdcardOutput.close();
					sdcardOutput = null;
				}
				if(assertInput != null) {
					assertInput.close();
					assertInput = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 复制SD卡文件至SD卡
	 * @param fromFile
	 * @param targetFile
	 */
	public static boolean copySDCard2SDCard(String fromFile, String targetFile) {
		InputStream sdcardInput = null;
		OutputStream sdcardOutput = null;
		try {
			sdcardInput = openFileInputStream(fromFile);
			sdcardOutput = openFileOutputStream(targetFile);
	        byte[] buffer = new byte[1024];  
	        int length;  
	        while ((length = sdcardInput.read(buffer)) > 0) {  
	        	sdcardOutput.write(buffer, 0, length);  
	        }  
	        sdcardOutput.flush();  
	        return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if(sdcardOutput != null) {
					sdcardOutput.close();
					sdcardOutput = null;
				}
				if(sdcardInput != null) {
					sdcardInput.close();
					sdcardInput = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * 
	 * @param baseDir 指定根目录
	 * @param absFileName 相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	private static File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split(File.separator);
		File ret = new File(baseDir);
		String substr = null;
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				substr = dirs[i];
				try {
					substr = new String(substr.getBytes("8859_1"), "GB2312");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				ret = new File(ret, substr);
			}
			if (!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length - 1];
			try {
				substr = new String(substr.getBytes("8859_1"), "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			ret = new File(ret, substr);
			return ret;
		}
		return ret;
	}
	
	/**
	 * 移除文件
	 * @param mContext
	 * @param file
	 * @return
	 */
	public static boolean removeFile(Context mContext, String file) {
		File f = new File(file);
		if(f.exists()) {
			return f.delete();
		}
		return true;
	}
	
	/**
	 * 移除文件夹
	 * @param mContext
	 * @param folder
	 * @return
	 */
	public static boolean removeFolder(Context mContext, String folder) {
		boolean result = false;
		if(folder.endsWith(File.separator))
			folder = folder.substring(0, folder.length() - 1);
		File file = new File(folder);
		String renameTo = folder + "_back";
		if (file.exists()) {
			result = file.renameTo(new File(renameTo));
			rmFolder(mContext, renameTo);
		}
		return result;
	}
	
	private static void rmFolder(final Context mContext, final String folder) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File file = new File(folder);
					if (file.exists()) {
						Process process = Runtime.getRuntime().exec("rm -r " + file.getPath());
						process.waitFor();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
