package com.xuwt.framework.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.xuwt.framework.BaseApplication;
import com.xuwt.framework.net.NetType;
import com.xuwt.framework.net.http.Params;

import org.apache.http.HttpHost;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;


public class HttpUtils {
	
	private final static String mTag = "HttpUtils";
	
	private static HashMap<String, String> HASH_CONTENTTYPE;
	public static final String CONTENTTYPE_STREAM = "application/octet-stream";
	public static final String CONTENTTYPE_JPEG = "image/jpeg";
	
	static {
		HASH_CONTENTTYPE = new HashMap<String, String>();
		HASH_CONTENTTYPE.put(".*", CONTENTTYPE_STREAM);
		HASH_CONTENTTYPE.put(".001", "application/x-001");
		HASH_CONTENTTYPE.put(".301", "application/x-301");
		HASH_CONTENTTYPE.put(".323", "text/h323");
		HASH_CONTENTTYPE.put(".906", "application/x-906");
		HASH_CONTENTTYPE.put(".907", "drawing/907");
		HASH_CONTENTTYPE.put(".a11", "application/x-a11");
		HASH_CONTENTTYPE.put(".acp", "audio/x-mei-aac");
		HASH_CONTENTTYPE.put(".ai", "application/postscript");
		HASH_CONTENTTYPE.put(".aif", "audio/aiff");
		HASH_CONTENTTYPE.put(".aifc", "audio/aiff");
		HASH_CONTENTTYPE.put(".aiff", "audio/aiff");
		HASH_CONTENTTYPE.put(".anv", "application/x-anv");
		HASH_CONTENTTYPE.put(".asa", "text/asa");
		HASH_CONTENTTYPE.put(".asf", "video/x-ms-asf");
		HASH_CONTENTTYPE.put(".asp", "text/asp");
		HASH_CONTENTTYPE.put(".asx", "video/x-ms-asf");
		HASH_CONTENTTYPE.put(".au", "audio/basic");
		HASH_CONTENTTYPE.put(".avi", "video/avi");
		HASH_CONTENTTYPE.put(".awf", "application/vnd.adobe.workflow");
		HASH_CONTENTTYPE.put(".biz", "text/xml");
		HASH_CONTENTTYPE.put(".bmp", "application/x-bmp");
		HASH_CONTENTTYPE.put(".bot", "application/x-bot");
		HASH_CONTENTTYPE.put(".c4t", "application/x-c4t");
		HASH_CONTENTTYPE.put(".c90", "application/x-c90");
		HASH_CONTENTTYPE.put(".cal", "application/x-cals");
		HASH_CONTENTTYPE.put(".cat", "application/vnd.ms-pki.seccat");
		HASH_CONTENTTYPE.put(".cdf", "application/x-netcdf");
		HASH_CONTENTTYPE.put(".cdr", "application/x-cdr");
		HASH_CONTENTTYPE.put(".cel", "application/x-cel");
		HASH_CONTENTTYPE.put(".cer", "application/x-x509-ca-cert");
		HASH_CONTENTTYPE.put(".cg4", "application/x-g4");
		HASH_CONTENTTYPE.put(".cgm", "application/x-cgm");
		HASH_CONTENTTYPE.put(".cit", "application/x-cit");
		HASH_CONTENTTYPE.put(".class", "java/*");
		HASH_CONTENTTYPE.put(".cml", "text/xml");
		HASH_CONTENTTYPE.put(".cmp", "application/x-cmp");
		HASH_CONTENTTYPE.put(".cmx", "application/x-cmx");
		HASH_CONTENTTYPE.put(".cot", "application/x-cot");
		HASH_CONTENTTYPE.put(".crl", "application/pkix-crl");
		HASH_CONTENTTYPE.put(".crt", "application/x-x509-ca-cert");
		HASH_CONTENTTYPE.put(".csi", "application/x-csi");
		HASH_CONTENTTYPE.put(".css", "text/css");
		HASH_CONTENTTYPE.put(".cut", "application/x-cut");
		HASH_CONTENTTYPE.put(".dbf", "application/x-dbf");
		HASH_CONTENTTYPE.put(".dbm", "application/x-dbm");
		HASH_CONTENTTYPE.put(".dbx", "application/x-dbx");
		HASH_CONTENTTYPE.put(".dcd", "text/xml");
		HASH_CONTENTTYPE.put(".dcx", "application/x-dcx");
		HASH_CONTENTTYPE.put(".der", "application/x-x509-ca-cert");
		HASH_CONTENTTYPE.put(".dgn", "application/x-dgn");
		HASH_CONTENTTYPE.put(".dib", "application/x-dib");
		HASH_CONTENTTYPE.put(".dll", "application/x-msdownload");
		HASH_CONTENTTYPE.put(".doc", "application/msword");
		HASH_CONTENTTYPE.put(".dot", "application/msword");
		HASH_CONTENTTYPE.put(".drw", "application/x-drw");
		HASH_CONTENTTYPE.put(".dtd", "text/xml");
		HASH_CONTENTTYPE.put(".dwf", "Model/vnd.dwf");
		HASH_CONTENTTYPE.put(".dwf", "application/x-dwf");
		HASH_CONTENTTYPE.put(".dwg", "application/x-dwg");
		HASH_CONTENTTYPE.put(".dxb", "application/x-dxb");
		HASH_CONTENTTYPE.put(".dxf", "application/x-dxf");
		HASH_CONTENTTYPE.put(".edn", "application/vnd.adobe.edn");
		HASH_CONTENTTYPE.put(".emf", "application/x-emf");
		HASH_CONTENTTYPE.put(".eml", "message/rfc822");
		HASH_CONTENTTYPE.put(".ent", "text/xml");
		HASH_CONTENTTYPE.put(".epi", "application/x-epi");
		HASH_CONTENTTYPE.put(".eps", "application/x-ps");
		HASH_CONTENTTYPE.put(".eps", "application/postscript");
		HASH_CONTENTTYPE.put(".etd", "application/x-ebx");
		HASH_CONTENTTYPE.put(".exe", "application/x-msdownload");
		HASH_CONTENTTYPE.put(".fax", "image/fax");
		HASH_CONTENTTYPE.put(".fdf", "application/vnd.fdf");
		HASH_CONTENTTYPE.put(".fif", "application/fractals");
		HASH_CONTENTTYPE.put(".fo", "text/xml");
		HASH_CONTENTTYPE.put(".frm", "application/x-frm");
		HASH_CONTENTTYPE.put(".g4", "application/x-g4");
		HASH_CONTENTTYPE.put(".gbr", "application/x-gbr");
		HASH_CONTENTTYPE.put(".gcd", "application/x-gcd");
		HASH_CONTENTTYPE.put(".gif", "image/gif");
		HASH_CONTENTTYPE.put(".gl2", "application/x-gl2");
		HASH_CONTENTTYPE.put(".gp4", "application/x-gp4");
		HASH_CONTENTTYPE.put(".hgl", "application/x-hgl");
		HASH_CONTENTTYPE.put(".hmr", "application/x-hmr");
		HASH_CONTENTTYPE.put(".hpg", "application/x-hpgl");
		HASH_CONTENTTYPE.put(".hpl", "application/x-hpl");
		HASH_CONTENTTYPE.put(".hqx", "application/mac-binhex40");
		HASH_CONTENTTYPE.put(".hrf", "application/x-hrf");
		HASH_CONTENTTYPE.put(".hta", "application/hta");
		HASH_CONTENTTYPE.put(".htc", "text/x-component");
		HASH_CONTENTTYPE.put(".htm", "text/html");
		HASH_CONTENTTYPE.put(".html", "text/html");
		HASH_CONTENTTYPE.put(".htt", "text/webviewhtml");
		HASH_CONTENTTYPE.put(".htx", "text/html");
		HASH_CONTENTTYPE.put(".icb", "application/x-icb");
		HASH_CONTENTTYPE.put(".ico", "image/x-icon");
		HASH_CONTENTTYPE.put(".ico", "application/x-ico");
		HASH_CONTENTTYPE.put(".iff", "application/x-iff");
		HASH_CONTENTTYPE.put(".ig4", "application/x-g4");
		HASH_CONTENTTYPE.put(".igs", "application/x-igs");
		HASH_CONTENTTYPE.put(".iii", "application/x-iphone");
		HASH_CONTENTTYPE.put(".img", "application/x-img");
		HASH_CONTENTTYPE.put(".ins", "application/x-internet-signup");
		HASH_CONTENTTYPE.put(".isp", "application/x-internet-signup");
		HASH_CONTENTTYPE.put(".IVF", "video/x-ivf");
		HASH_CONTENTTYPE.put(".java", "java/*");
		HASH_CONTENTTYPE.put(".jfif", CONTENTTYPE_JPEG);
		HASH_CONTENTTYPE.put(".jpe", CONTENTTYPE_JPEG);
		HASH_CONTENTTYPE.put(".jpe", "application/x-jpe");
		HASH_CONTENTTYPE.put(".jpeg", CONTENTTYPE_JPEG);
		HASH_CONTENTTYPE.put(".jpg", CONTENTTYPE_JPEG);
		//HASH_CONTENTTYPE.put(".jpg", "application/x-jpg");
		HASH_CONTENTTYPE.put(".js", "application/x-javascript");
		HASH_CONTENTTYPE.put(".jsp", "text/html");
		HASH_CONTENTTYPE.put(".la1", "audio/x-liquid-file");
		HASH_CONTENTTYPE.put(".lar", "application/x-laplayer-reg");
		HASH_CONTENTTYPE.put(".latex", "application/x-latex");
		HASH_CONTENTTYPE.put(".lavs", "audio/x-liquid-secure");
		HASH_CONTENTTYPE.put(".lbm", "application/x-lbm");
		HASH_CONTENTTYPE.put(".lmsff", "audio/x-la-lms");
		HASH_CONTENTTYPE.put(".ls", "application/x-javascript");
		HASH_CONTENTTYPE.put(".ltr", "application/x-ltr");
		HASH_CONTENTTYPE.put(".m1v", "video/x-mpeg");
		HASH_CONTENTTYPE.put(".m2v", "video/x-mpeg");
		HASH_CONTENTTYPE.put(".m3u", "audio/mpegurl");
		HASH_CONTENTTYPE.put(".m4e", "video/mpeg4");
		HASH_CONTENTTYPE.put(".mac", "application/x-mac");
		HASH_CONTENTTYPE.put(".man", "application/x-troff-man");
		HASH_CONTENTTYPE.put(".math", "text/xml");
		HASH_CONTENTTYPE.put(".mdb", "application/msaccess");
		HASH_CONTENTTYPE.put(".mdb", "application/x-mdb");
		HASH_CONTENTTYPE.put(".mfp", "application/x-shockwave-flash");
		HASH_CONTENTTYPE.put(".mht", "message/rfc822");
		HASH_CONTENTTYPE.put(".mhtml", "message/rfc822");
		HASH_CONTENTTYPE.put(".mi", "application/x-mi");
		HASH_CONTENTTYPE.put(".mid", "audio/mid");
		HASH_CONTENTTYPE.put(".midi", "audio/mid");
		HASH_CONTENTTYPE.put(".mil", "application/x-mil");
		HASH_CONTENTTYPE.put(".mml", "text/xml");
		HASH_CONTENTTYPE.put(".mnd", "audio/x-musicnet-download");
		HASH_CONTENTTYPE.put(".mns", "audio/x-musicnet-stream");
		HASH_CONTENTTYPE.put(".mocha", "application/x-javascript");
		HASH_CONTENTTYPE.put(".movie", "video/x-sgi-movie");
		HASH_CONTENTTYPE.put(".mp1", "audio/mp1");
		HASH_CONTENTTYPE.put(".mp2", "audio/mp2");
		HASH_CONTENTTYPE.put(".mp2v", "video/mpeg");
		HASH_CONTENTTYPE.put(".mp3", "audio/mp3");
		HASH_CONTENTTYPE.put(".mp4", "video/mpeg4");
		HASH_CONTENTTYPE.put(".mpa", "video/x-mpg");
		HASH_CONTENTTYPE.put(".mpd", "application/vnd.ms-project");
		HASH_CONTENTTYPE.put(".mpe", "video/x-mpeg");
		HASH_CONTENTTYPE.put(".mpeg", "video/mpg");
		HASH_CONTENTTYPE.put(".mpg", "video/mpg");
		HASH_CONTENTTYPE.put(".mpga", "audio/rn-mpeg");
		HASH_CONTENTTYPE.put(".mpp", "application/vnd.ms-project");
		HASH_CONTENTTYPE.put(".mps", "video/x-mpeg");
		HASH_CONTENTTYPE.put(".mpt", "application/vnd.ms-project");
		HASH_CONTENTTYPE.put(".mpv", "video/mpg");
		HASH_CONTENTTYPE.put(".mpv2", "video/mpeg");
		HASH_CONTENTTYPE.put(".mpw", "application/vnd.ms-project");
		HASH_CONTENTTYPE.put(".mpx", "application/vnd.ms-project");
		HASH_CONTENTTYPE.put(".mtx", "text/xml");
		HASH_CONTENTTYPE.put(".mxp", "application/x-mmxp");
		HASH_CONTENTTYPE.put(".net", "image/pnetvue");
		HASH_CONTENTTYPE.put(".nrf", "application/x-nrf");
		HASH_CONTENTTYPE.put(".nws", "message/rfc822");
		HASH_CONTENTTYPE.put(".odc", "text/x-ms-odc");
		HASH_CONTENTTYPE.put(".out", "application/x-out");
		HASH_CONTENTTYPE.put(".p10", "application/pkcs10");
		HASH_CONTENTTYPE.put(".p12", "application/x-pkcs12");
		HASH_CONTENTTYPE.put(".p7b", "application/x-pkcs7-certificates");
		HASH_CONTENTTYPE.put(".p7c", "application/pkcs7-mime");
		HASH_CONTENTTYPE.put(".p7m", "application/pkcs7-mime");
		HASH_CONTENTTYPE.put(".p7r", "application/x-pkcs7-certreqresp");
		HASH_CONTENTTYPE.put(".p7s", "application/pkcs7-signature");
		HASH_CONTENTTYPE.put(".pc5", "application/x-pc5");
		HASH_CONTENTTYPE.put(".pci", "application/x-pci");
		HASH_CONTENTTYPE.put(".pcl", "application/x-pcl");
		HASH_CONTENTTYPE.put(".pcx", "application/x-pcx");
		HASH_CONTENTTYPE.put(".pdf", "application/pdf");
		HASH_CONTENTTYPE.put(".pdf", "application/pdf");
		HASH_CONTENTTYPE.put(".pdx", "application/vnd.adobe.pdx");
		HASH_CONTENTTYPE.put(".pfx", "application/x-pkcs12");
		HASH_CONTENTTYPE.put(".pgl", "application/x-pgl");
		HASH_CONTENTTYPE.put(".pic", "application/x-pic");
		HASH_CONTENTTYPE.put(".pko", "application/vnd.ms-pki.pko");
		HASH_CONTENTTYPE.put(".pl", "application/x-perl");
		HASH_CONTENTTYPE.put(".plg", "text/html");
		HASH_CONTENTTYPE.put(".pls", "audio/scpls");
		HASH_CONTENTTYPE.put(".plt", "application/x-plt");
		HASH_CONTENTTYPE.put(".png", "image/png");
		HASH_CONTENTTYPE.put(".png", "application/x-png");
		HASH_CONTENTTYPE.put(".pot", "application/vnd.ms-powerpoint");
		HASH_CONTENTTYPE.put(".ppa", "application/vnd.ms-powerpoint");
		HASH_CONTENTTYPE.put(".ppm", "application/x-ppm");
		HASH_CONTENTTYPE.put(".pps", "application/vnd.ms-powerpoint");
		HASH_CONTENTTYPE.put(".ppt", "application/vnd.ms-powerpoint");
		HASH_CONTENTTYPE.put(".ppt", "application/x-ppt");
		HASH_CONTENTTYPE.put(".pr", "application/x-pr");
		HASH_CONTENTTYPE.put(".prf", "application/pics-rules");
		HASH_CONTENTTYPE.put(".prn", "application/x-prn");
		HASH_CONTENTTYPE.put(".prt", "application/x-prt");
		HASH_CONTENTTYPE.put(".ps", "application/x-ps");
		HASH_CONTENTTYPE.put(".ps", "application/postscript");
		HASH_CONTENTTYPE.put(".ptn", "application/x-ptn");
		HASH_CONTENTTYPE.put(".pwz", "application/vnd.ms-powerpoint");
		HASH_CONTENTTYPE.put(".r3t", "text/vnd.rn-realtext3d");
		HASH_CONTENTTYPE.put(".ra", "audio/vnd.rn-realaudio");
		HASH_CONTENTTYPE.put(".ram", "audio/x-pn-realaudio");
		HASH_CONTENTTYPE.put(".ras", "application/x-ras");
		HASH_CONTENTTYPE.put(".rat", "application/rat-file");
		HASH_CONTENTTYPE.put(".rdf", "text/xml");
		HASH_CONTENTTYPE.put(".rec", "application/vnd.rn-recording");
		HASH_CONTENTTYPE.put(".red", "application/x-red");
		HASH_CONTENTTYPE.put(".rgb", "application/x-rgb");
		HASH_CONTENTTYPE.put(".rjs", "application/vnd.rn-realsystem-rjs");
		HASH_CONTENTTYPE.put(".rjt", "application/vnd.rn-realsystem-rjt");
		HASH_CONTENTTYPE.put(".rlc", "application/x-rlc");
		HASH_CONTENTTYPE.put(".rle", "application/x-rle");
		HASH_CONTENTTYPE.put(".rm", "application/vnd.rn-realmedia");
		HASH_CONTENTTYPE.put(".rmf", "application/vnd.adobe.rmf");
		HASH_CONTENTTYPE.put(".rmi", "audio/mid");
		HASH_CONTENTTYPE.put(".rmj", "application/vnd.rn-realsystem-rmj");
		HASH_CONTENTTYPE.put(".rmm", "audio/x-pn-realaudio");
		HASH_CONTENTTYPE.put(".rmp", "application/vnd.rn-rn_music_package");
		HASH_CONTENTTYPE.put(".rms", "application/vnd.rn-realmedia-secure");
		HASH_CONTENTTYPE.put(".rmvb", "application/vnd.rn-realmedia-vbr");
		HASH_CONTENTTYPE.put(".rmx", "application/vnd.rn-realsystem-rmx");
		HASH_CONTENTTYPE.put(".rnx", "application/vnd.rn-realplayer");
		HASH_CONTENTTYPE.put(".rp", "image/vnd.rn-realpix");
		HASH_CONTENTTYPE.put(".rpm", "audio/x-pn-realaudio-plugin");
		HASH_CONTENTTYPE.put(".rsml", "application/vnd.rn-rsml");
		HASH_CONTENTTYPE.put(".rt", "text/vnd.rn-realtext");
		HASH_CONTENTTYPE.put(".rtf", "application/msword");
		HASH_CONTENTTYPE.put(".rtf", "application/x-rtf");
		HASH_CONTENTTYPE.put(".rv", "video/vnd.rn-realvideo");
		HASH_CONTENTTYPE.put(".sam", "application/x-sam");
		HASH_CONTENTTYPE.put(".sat", "application/x-sat");
		HASH_CONTENTTYPE.put(".sdp", "application/sdp");
		HASH_CONTENTTYPE.put(".sdw", "application/x-sdw");
		HASH_CONTENTTYPE.put(".sit", "application/x-stuffit");
		HASH_CONTENTTYPE.put(".slb", "application/x-slb");
		HASH_CONTENTTYPE.put(".sld", "application/x-sld");
		HASH_CONTENTTYPE.put(".slk", "drawing/x-slk");
		HASH_CONTENTTYPE.put(".smi", "application/smil");
		HASH_CONTENTTYPE.put(".smil", "application/smil");
		HASH_CONTENTTYPE.put(".smk", "application/x-smk");
		HASH_CONTENTTYPE.put(".snd", "audio/basic");
		HASH_CONTENTTYPE.put(".sol", "text/plain");
		HASH_CONTENTTYPE.put(".sor", "text/plain");
		HASH_CONTENTTYPE.put(".spc", "application/x-pkcs7-certificates");
		HASH_CONTENTTYPE.put(".spl", "application/futuresplash");
		HASH_CONTENTTYPE.put(".spp", "text/xml");
		HASH_CONTENTTYPE.put(".ssm", "application/streamingmedia");
		HASH_CONTENTTYPE.put(".sst", "application/vnd.ms-pki.certstore");
		HASH_CONTENTTYPE.put(".stl", "application/vnd.ms-pki.stl");
		HASH_CONTENTTYPE.put(".stm", "text/html");
		HASH_CONTENTTYPE.put(".sty", "application/x-sty");
		HASH_CONTENTTYPE.put(".svg", "text/xml");
		HASH_CONTENTTYPE.put(".swf", "application/x-shockwave-flash");
		HASH_CONTENTTYPE.put(".tdf", "application/x-tdf");
		HASH_CONTENTTYPE.put(".tg4", "application/x-tg4");
		HASH_CONTENTTYPE.put(".tga", "application/x-tga");
		HASH_CONTENTTYPE.put(".tif", "image/tiff");
		HASH_CONTENTTYPE.put(".tif", "application/x-tif");
		HASH_CONTENTTYPE.put(".tiff", "image/tiff");
		HASH_CONTENTTYPE.put(".tld", "text/xml");
		HASH_CONTENTTYPE.put(".top", "drawing/x-top");
		HASH_CONTENTTYPE.put(".torrent", "application/x-bittorrent");
		HASH_CONTENTTYPE.put(".tsd", "text/xml");
		HASH_CONTENTTYPE.put(".txt", "text/plain");
		HASH_CONTENTTYPE.put(".uin", "application/x-icq");
		HASH_CONTENTTYPE.put(".uls", "text/iuls");
		HASH_CONTENTTYPE.put(".vcf", "text/x-vcard");
		HASH_CONTENTTYPE.put(".vda", "application/x-vda");
		HASH_CONTENTTYPE.put(".vdx", "application/vnd.visio");
		HASH_CONTENTTYPE.put(".vml", "text/xml");
		HASH_CONTENTTYPE.put(".vpg", "application/x-vpeg005");
		HASH_CONTENTTYPE.put(".vsd", "application/vnd.visio");
		HASH_CONTENTTYPE.put(".vsd", "application/x-vsd");
		HASH_CONTENTTYPE.put(".vss", "application/vnd.visio");
		HASH_CONTENTTYPE.put(".vst", "application/vnd.visio");
		HASH_CONTENTTYPE.put(".vst", "application/x-vst");
		HASH_CONTENTTYPE.put(".vsw", "application/vnd.visio");
		HASH_CONTENTTYPE.put(".vsx", "application/vnd.visio");
		HASH_CONTENTTYPE.put(".vtx", "application/vnd.visio");
		HASH_CONTENTTYPE.put(".vxml", "text/xml");
		HASH_CONTENTTYPE.put(".wav", "audio/wav");
		HASH_CONTENTTYPE.put(".wax", "audio/x-ms-wax");
		HASH_CONTENTTYPE.put(".wb1", "application/x-wb1");
		HASH_CONTENTTYPE.put(".wb2", "application/x-wb2");
		HASH_CONTENTTYPE.put(".wb3", "application/x-wb3");
		HASH_CONTENTTYPE.put(".wbmp", "image/vnd.wap.wbmp");
		HASH_CONTENTTYPE.put(".wiz", "application/msword");
		HASH_CONTENTTYPE.put(".wk3", "application/x-wk3");
		HASH_CONTENTTYPE.put(".wk4", "application/x-wk4");
		HASH_CONTENTTYPE.put(".wkq", "application/x-wkq");
		HASH_CONTENTTYPE.put(".wks", "application/x-wks");
		HASH_CONTENTTYPE.put(".wm", "video/x-ms-wm");
		HASH_CONTENTTYPE.put(".wma", "audio/x-ms-wma");
		HASH_CONTENTTYPE.put(".wmd", "application/x-ms-wmd");
		HASH_CONTENTTYPE.put(".wmf", "application/x-wmf");
		HASH_CONTENTTYPE.put(".wml", "text/vnd.wap.wml");
		HASH_CONTENTTYPE.put(".wmv", "video/x-ms-wmv");
		HASH_CONTENTTYPE.put(".wmx", "video/x-ms-wmx");
		HASH_CONTENTTYPE.put(".wmz", "application/x-ms-wmz");
		HASH_CONTENTTYPE.put(".wp6", "application/x-wp6");
		HASH_CONTENTTYPE.put(".wpd", "application/x-wpd");
		HASH_CONTENTTYPE.put(".wpg", "application/x-wpg");
		HASH_CONTENTTYPE.put(".wpl", "application/vnd.ms-wpl");
		HASH_CONTENTTYPE.put(".wq1", "application/x-wq1");
		HASH_CONTENTTYPE.put(".wr1", "application/x-wr1");
		HASH_CONTENTTYPE.put(".wri", "application/x-wri");
		HASH_CONTENTTYPE.put(".wrk", "application/x-wrk");
		HASH_CONTENTTYPE.put(".ws", "application/x-ws");
		HASH_CONTENTTYPE.put(".ws2", "application/x-ws");
		HASH_CONTENTTYPE.put(".wsc", "text/scriptlet");
		HASH_CONTENTTYPE.put(".wsdl", "text/xml");
		HASH_CONTENTTYPE.put(".wvx", "video/x-ms-wvx");
		HASH_CONTENTTYPE.put(".xdp", "application/vnd.adobe.xdp");
		HASH_CONTENTTYPE.put(".xdr", "text/xml");
		HASH_CONTENTTYPE.put(".xfd", "application/vnd.adobe.xfd");
		HASH_CONTENTTYPE.put(".xfdf", "application/vnd.adobe.xfdf");
		HASH_CONTENTTYPE.put(".xhtml", "text/html");
		HASH_CONTENTTYPE.put(".xls", "application/vnd.ms-excel");
		HASH_CONTENTTYPE.put(".xls", "application/x-xls");
		HASH_CONTENTTYPE.put(".xlw", "application/x-xlw");
		HASH_CONTENTTYPE.put(".xml", "text/xml");
		HASH_CONTENTTYPE.put(".xpl", "audio/scpls");
		HASH_CONTENTTYPE.put(".xq", "text/xml");
		HASH_CONTENTTYPE.put(".xql", "text/xml");
		HASH_CONTENTTYPE.put(".xquery", "text/xml");
		HASH_CONTENTTYPE.put(".xsd", "text/xml");
		HASH_CONTENTTYPE.put(".xsl", "text/xml");
		HASH_CONTENTTYPE.put(".xslt", "text/xml");
		HASH_CONTENTTYPE.put(".xwd", "application/x-xwd");
		HASH_CONTENTTYPE.put(".x_b", "application/x-x_b");
		HASH_CONTENTTYPE.put(".x_t", "application/x-x_t");
	}
	
	/**
	 * 判断网络是否连接
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		boolean isOk = true;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(connectivityManager == null){
				return false;
			}
			NetworkInfo mobNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetInfo != null && !wifiNetInfo.isConnectedOrConnecting()) {
				if (mobNetInfo != null && !mobNetInfo.isConnectedOrConnecting()) {
					NetworkInfo info = connectivityManager.getActiveNetworkInfo();
					if (info == null) {
						isOk = false;
					}
				}
			}
			mobNetInfo = null;
			wifiNetInfo = null;
			connectivityManager = null;
		} catch (Exception e) {
			LogUtils.error(mTag, e);
		}
		return isOk;
	}
	
	/**
	 * 获取网络类型
	 * @param context
	 * @return
	 */
	public static NetType getNetworkType(Context context) {
		NetType netType = NetType.Unknown;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(connectivityManager == null){
				return  netType;
			}
			NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetInfo != null && wifiNetInfo.isConnectedOrConnecting()) {
				netType = NetType.Wifi;
			} else {
				NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if (mobNetInfo != null && mobNetInfo.isConnectedOrConnecting()) {
					String info = mobNetInfo.getExtraInfo().toLowerCase();
					if(!info.contains("wap") && !info.contains("net")) {
				        Cursor cursor =  context.getContentResolver().query(Uri.parse(Params.PREFERRED_APN_URI), null, null, null, "name ASC");
				        if (cursor.getCount() > 0) {
				            cursor.moveToFirst();
				            info = cursor.getString(5);
				        }
				        cursor.close();
					}
					if(info.contains("cmnet"))
						netType = NetType.CMNet;
					else if(info.contains("cmwap"))
						netType = NetType.CMWap;
					else if(info.contains("uninet"))
						netType = NetType.UNNet;
					else if(info.contains("uniwap"))
						netType = NetType.UNWap;
					else if(info.contains("ctnet"))
						netType = NetType.CTNet;
					else if(info.contains("ctwap"))
						netType = NetType.CTWap;
					else if(info.contains("3gnet"))
						netType = NetType.G3Net;
					else if(info.contains("3gwap"))
						netType = NetType.G3Wap;
					else
						netType = NetType.Unknown;
				} else {
					netType = NetType.Unknown;
				}
				mobNetInfo = null;
			}
			wifiNetInfo = null;
			connectivityManager = null;
		} catch (Exception e) {
			LogUtils.error(mTag, e);
		}
		return netType;
	}
	
	/**
	 * 判断是否是2G网络
	 * @return
	 */
	public static boolean isNetWork2G(Context context){
		boolean res = false;
		try {
			ConnectivityManager connectivityManager = 
					(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(connectivityManager == null){
				return true;
			}
			NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetInfo != null && wifiNetInfo.isConnectedOrConnecting()) {
				return false;
			}
			NetworkInfo  activeNetInfo = connectivityManager.getActiveNetworkInfo();
			if(activeNetInfo == null){
				res = false;
			}else if(activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE){
				int subType = activeNetInfo.getSubtype();
				if(subType==TelephonyManager.NETWORK_TYPE_1xRTT || subType == TelephonyManager.NETWORK_TYPE_EDGE
						||subType == TelephonyManager.NETWORK_TYPE_EVDO_0 || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
						||subType == TelephonyManager.NETWORK_TYPE_GPRS){
					res = true;
				}else{
					res = false;
				}
			}else if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI){
				res = false;
			}else{
				res = false;
			}
		} catch (Exception e) {
			LogUtils.error(mTag, e);
		}
		return res;
	}
	
	/**
	 * 判断是否是cmwap网络
	 * @return
	 */
	public static boolean isNetWorkCMWap(Context context){
		boolean res = false;
		NetType state = getNetworkType(context);
		if(state == NetType.CMWap || state == NetType.UNWap || state == NetType.G3Wap)
			res = true;
		return res;
	}
	
	/**
	 * 获取代理（Get）
	 * @param context
	 * @return
	 */
	public static Proxy getProxyForGet(Context context) {
		Proxy urlProxy = null;
		String[] proxy = getProxyInfo(context);
		if (proxy != null) {
			urlProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(String
					.valueOf(proxy[0]), Integer.valueOf(proxy[1])));
		}
		proxy = null;
		return urlProxy;
	}
	
	/**
	 * 获取代理（Post）
	 * @param context
	 * @return
	 */
	public static HttpHost getProxyForPost(Context context) {
		HttpHost postProxy = null;
		String[] proxy = getProxyInfo(context);
		if (proxy != null) {
			postProxy = new HttpHost(String.valueOf(proxy[0]), Integer
					.valueOf(proxy[1]));
		}
		proxy = null;
		return postProxy;
	}
	
	/**
	 * 获取代理信息
	 * @param context
	 * @return
	 */
	public static String[] getProxyInfo(Context context) {
		String[] proxy = null;
		NetType state = getNetworkType(context);
		if(state == NetType.CMWap || state == NetType.UNWap || state == NetType.G3Wap)
			proxy = new String[] { Params.PROXY_DEFAULT, Params.PORT_DEFAULT };
		else if(state == NetType.CTWap)
			proxy = new String[] { Params.PROXY_CTWAP, Params.PORT_DEFAULT };
		return proxy;
	}
	
	/**
	 * 创建Http的连接
	 * @return
	 */
	public static HttpURLConnection createHttpConnection(String url) {
		return createHttpConnection(url, false);
	}
	
	/**
	 * 创建Http的连接
	 * @return
	 */
	public static HttpURLConnection createHttpConnection(String url, boolean image) {
		HttpURLConnection conn = null;
        try {
			boolean isCmwap = HttpUtils.isNetWorkCMWap(BaseApplication.get());
			URL connUrl = null;
			if (isCmwap) {
				String cmwaphost = "http://" + Params.PROXY_DEFAULT + ":" + 
						Params.PORT_DEFAULT + "/";
			    connUrl = new URL(StringUtils.replaceURLHost(url, cmwaphost));
			    conn = (HttpURLConnection) connUrl.openConnection();
			} else {
			    connUrl = new URL(url);
			    Proxy proxy = HttpUtils.getProxyForGet(BaseApplication.get());
			    if (proxy == null) {
			        conn = (HttpURLConnection) connUrl.openConnection();
			    } else {
			        conn = (HttpURLConnection) connUrl.openConnection(proxy);
			    }
			}
			connUrl = null;
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setAllowUserInteraction(true);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty(HTTP.CONTENT_ENCODING, HTTP.UTF_8);
			conn.setRequestProperty(HTTP.USER_AGENT, Params.USER_AGENT);
			if(!image) {
				conn.setRequestProperty("Accept-Encoding", Params.ACCEPT_ENCODING);
			}
			conn.setRequestProperty("Accept", Params.ACCEPT);
			if (isCmwap) {
				conn.setRequestProperty("User-Agent", "mlj.framework");
				conn.setRequestProperty("X-online-Host", StringUtils.getURLDomain(url));
			}
		} catch (MalformedURLException e) {
			LogUtils.error(mTag, e);
		} catch (ProtocolException e) {
			LogUtils.error(mTag, e);
		} catch (IOException e) {
			LogUtils.error(mTag, e);
		}
        return conn;
	}
	
	public static String getContentType(String file) {
		try {
			file = file.substring(file.lastIndexOf("."));
			file = file.toLowerCase().trim();
			String value = HASH_CONTENTTYPE.get(file);
			if(value == null) {
				value = CONTENTTYPE_STREAM;
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CONTENTTYPE_STREAM;
	}
}

