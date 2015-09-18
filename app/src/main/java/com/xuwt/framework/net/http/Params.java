package com.xuwt.framework.net.http;

public class Params {
	public static final String PROXY_CTWAP = "10.0.0.200";
	public static final String PROXY_DEFAULT = "10.0.0.172";
	public static final String PORT_DEFAULT = "80";
	public static final String PREFERRED_APN_URI = "content://telephony/carriers/preferapn";

	public static final String ACCEPT_ENCODING = "gzip,deflate";
	public static final String ACCEPT = "*/*";
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1";

	public static final int HTTPGET_CONN_TIME_OUT = 15 * 1000;
	public static final int HTTPGET_CONN_TIME_OUT_LONG = 25 * 1000;
	public static final int HTTPGET_READ_TIME_OUT = 15 * 1000;
	public static final int HTTPGET_READ_TIME_OUT_LONG = 30 * 1000;

	public static final int HTTPPOST_CONN_TIME_OUT = 10 * 1000;
	public static final int HTTPPOST_CONN_TIME_OUT_LONG = 20 * 1000;
	public static final int HTTPPOST_READ_TIME_OUT = 20 * 1000;
	public static final int HTTPPOST_READ_TIME_OUT_LONG = 30 * 1000;
}
