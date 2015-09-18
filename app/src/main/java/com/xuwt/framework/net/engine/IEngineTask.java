package com.xuwt.framework.net.engine;

public interface IEngineTask extends Runnable {

	public static final int MINPRIORITY = 1;
	public static final int MAXPRIORITY = 10;
	public static final int DEFAULTPRIORITY = 5;
	public static final int FILEPRIORITY = 2;
	public static final int IMAGEPRIORITY = 3;
	public static final int POSTPRIORITY = 9;
	
	/**
	 * 开始任务
	 */
	void run();
	
	/**
	 * 取消任务
	 */
	void cancel();
	
	/**
	 * 任务优先级
	 * @return
	 */
	int getPriority();
	
	/**
	 * 可执行的条件
	 * @return
	 */
	boolean isValid();
	
	/**
	 * 通知执行完成
	 */
	void notifyWake();
}
