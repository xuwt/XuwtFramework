package com.xuwt.framework;

/**
 * 配置信息类
 * @author lijunma
 *
 */
public interface BaseConfigure {
	
	/**
	 * 是否调试模式
	 * @return
	 */
	boolean getIsDebug();

	/**
	 * 是否存储日志
	 * @return
	 */
	boolean getIsStorageLog();
	
	/**
	 * 是否存储数据库日志
	 * @return
	 */
	boolean getIsStorageDBLog();

}
