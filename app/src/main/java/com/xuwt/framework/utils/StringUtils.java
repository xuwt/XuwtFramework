package com.xuwt.framework.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.xuwt.framework.BaseApplication;
import com.xuwt.framework.widget.MToast;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {
	
	public static final String URL_KEY_HEADER = "urlheader";
	
	private static int[] pyvalue = new int[] { -20319, -20317, -20304, -20295,
		-20292, -20283, -20265, -20257, -20242, -20230, -20051, -20036,
		-20032, -20026, -20002, -19990, -19986, -19982, -19976, -19805,
		-19784, -19775, -19774, -19763, -19756, -19751, -19746, -19741,
		-19739, -19728, -19725, -19715, -19540, -19531, -19525, -19515,
		-19500, -19484, -19479, -19467, -19289, -19288, -19281, -19275,
		-19270, -19263, -19261, -19249, -19243, -19242, -19238, -19235,
		-19227, -19224, -19218, -19212, -19038, -19023, -19018, -19006,
		-19003, -18996, -18977, -18961, -18952, -18783, -18774, -18773,
		-18763, -18756, -18741, -18735, -18731, -18722, -18710, -18697,
		-18696, -18526, -18518, -18501, -18490, -18478, -18463, -18448,
		-18447, -18446, -18239, -18237, -18231, -18220, -18211, -18201,
		-18184, -18183, -18181, -18012, -17997, -17988, -17970, -17964,
		-17961, -17950, -17947, -17931, -17928, -17922, -17759, -17752,
		-17733, -17730, -17721, -17703, -17701, -17697, -17692, -17683,
		-17676, -17496, -17487, -17482, -17468, -17454, -17433, -17427,
		-17417, -17202, -17185, -16983, -16970, -16942, -16915, -16733,
		-16708, -16706, -16689, -16664, -16657, -16647, -16474, -16470,
		-16465, -16459, -16452, -16448, -16433, -16429, -16427, -16423,
		-16419, -16412, -16407, -16403, -16401, -16393, -16220, -16216,
		-16212, -16205, -16202, -16187, -16180, -16171, -16169, -16158,
		-16155, -15959, -15958, -15944, -15933, -15920, -15915, -15903,
		-15889, -15878, -15707, -15701, -15681, -15667, -15661, -15659,
		-15652, -15640, -15631, -15625, -15454, -15448, -15436, -15435,
		-15419, -15416, -15408, -15394, -15385, -15377, -15375, -15369,
		-15363, -15362, -15183, -15180, -15165, -15158, -15153, -15150,
		-15149, -15144, -15143, -15141, -15140, -15139, -15128, -15121,
		-15119, -15117, -15110, -15109, -14941, -14937, -14933, -14930,
		-14929, -14928, -14926, -14922, -14921, -14914, -14908, -14902,
		-14894, -14889, -14882, -14873, -14871, -14857, -14678, -14674,
		-14670, -14668, -14663, -14654, -14645, -14630, -14594, -14429,
		-14407, -14399, -14384, -14379, -14368, -14355, -14353, -14345,
		-14170, -14159, -14151, -14149, -14145, -14140, -14137, -14135,
		-14125, -14123, -14122, -14112, -14109, -14099, -14097, -14094,
		-14092, -14090, -14087, -14083, -13917, -13914, -13910, -13907,
		-13906, -13905, -13896, -13894, -13878, -13870, -13859, -13847,
		-13831, -13658, -13611, -13601, -13406, -13404, -13400, -13398,
		-13395, -13391, -13387, -13383, -13367, -13359, -13356, -13343,
		-13340, -13329, -13326, -13318, -13147, -13138, -13120, -13107,
		-13096, -13095, -13091, -13076, -13068, -13063, -13060, -12888,
		-12875, -12871, -12860, -12858, -12852, -12849, -12838, -12831,
		-12829, -12812, -12802, -12607, -12597, -12594, -12585, -12556,
		-12359, -12346, -12320, -12300, -12120, -12099, -12089, -12074,
		-12067, -12058, -12039, -11867, -11861, -11847, -11831, -11798,
		-11781, -11604, -11589, -11536, -11358, -11340, -11339, -11324,
		-11303, -11097, -11077, -11067, -11055, -11052, -11045, -11041,
		-11038, -11024, -11020, -11019, -11018, -11014, -10838, -10832,
		-10815, -10800, -10790, -10780, -10764, -10587, -10544, -10533,
		-10519, -10331, -10329, -10328, -10322, -10315, -10309, -10307,
		-10296, -10281, -10274, -10270, -10262, -10260, -10256, -10254 };
	
	private static String[] pystr = new String[] { "a", "ai", "an", "ang",
		"ao", "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng",
		"bi", "bian", "biao", "bie", "bin", "bing", "bo", "bu", "ca",
		"cai", "can", "cang", "cao", "ce", "ceng", "cha", "chai", "chan",
		"chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou",
		"chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci",
		"cong", "cou", "cu", "cuan", "cui", "cun", "cuo", "da", "dai",
		"dan", "dang", "dao", "de", "deng", "di", "dian", "diao", "die",
		"ding", "diu", "dong", "dou", "du", "duan", "dui", "dun", "duo",
		"e", "en", "er", "fa", "fan", "fang", "fei", "fen", "feng", "fo",
		"fou", "fu", "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen",
		"geng", "gong", "gou", "gu", "gua", "guai", "guan", "guang", "gui",
		"gun", "guo", "ha", "hai", "han", "hang", "hao", "he", "hei",
		"hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang",
		"hui", "hun", "huo", "ji", "jia", "jian", "jiang", "jiao", "jie",
		"jin", "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka",
		"kai", "kan", "kang", "kao", "ke", "ken", "keng", "kong", "kou",
		"ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la",
		"lai", "lan", "lang", "lao", "le", "lei", "leng", "li", "lia",
		"lian", "liang", "liao", "lie", "lin", "ling", "liu", "long",
		"lou", "lu", "lv", "luan", "lue", "lun", "luo", "ma", "mai", "man",
		"mang", "mao", "me", "mei", "men", "meng", "mi", "mian", "miao",
		"mie", "min", "ming", "miu", "mo", "mou", "mu", "na", "nai", "nan",
		"nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang",
		"niao", "nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan",
		"nue", "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei",
		"pen", "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po",
		"pu", "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing",
		"qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao",
		"re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui",
		"run", "ruo", "sa", "sai", "san", "sang", "sao", "se", "sen",
		"seng", "sha", "shai", "shan", "shang", "shao", "she", "shen",
		"sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang",
		"shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui",
		"sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng",
		"ti", "tian", "tiao", "tie", "ting", "tong", "tou", "tu", "tuan",
		"tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen",
		"weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie",
		"xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya",
		"yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong",
		"you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang",
		"zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan", "zhang",
		"zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu",
		"zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi",
		"zong", "zou", "zu", "zuan", "zui", "zun", "zuo" };
	
	private static int getChsAscii(String chs) {
        int asc = 0;
        try {
            byte[] bytes = chs.getBytes("gb2312");
            if (bytes == null || bytes.length > 2 || bytes.length <= 0) { // 错误
                throw new RuntimeException("illegal resource string");
            }
            if (bytes.length == 1) { // 英文字符
                asc = bytes[0];
            }
            if (bytes.length == 2) { // 中文字符
                int hightByte = 256 + bytes[0];
                int lowByte = 256 + bytes[1];
                asc = (256 * hightByte + lowByte) - 256 * 256;
            }
        } catch (Exception e) {
            System.out.println("ERROR:ChineseSpelling.class-getChsAscii(String chs)" + e);
            // e.printStackTrace();
        }
        return asc;
    }
	
    /**
     * 转换一个或多个汉字
     * 
     * @param str
     * @return
     */
    public static String convertHan2PinYin(String str) {
        String result = "";
        String strTemp = null;
        for (int j = 0; j < str.length(); j++) {
            strTemp = str.substring(j, j + 1);
            int ascii = getChsAscii(strTemp);
            if (ascii > 0 && ascii < 160) {
                result += String.valueOf((char) ascii);
            } else {
                for (int i = (pyvalue.length - 1); i >= 0; i--) {
                    if (pyvalue[i] <= ascii) {
                        result += pystr[i];
                        break;
                    }
                }
            }
        }
        return result;
    }
		
	public static String MD5(String value){
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
				  'a', 'b', 'c', 'd', 'e', 'f' }; 
		try { 
			byte[] strTemp = value.getBytes(); 
			//使用MD5创建MessageDigest对象 
			MessageDigest mdTemp = MessageDigest.getInstance("MD5"); 
			mdTemp.update(strTemp); 
			byte[] md = mdTemp.digest(); 
			int j = md.length; 
			char str[] = new char[j * 2]; 
			int k = 0; 
			for (int i = 0; i < j; i++) { 
				byte b = md[i]; 
				//System.out.println((int)b); 
				//将没个数(int)b进行双字节加密 
				str[k++] = hexDigits[b >> 4 & 0xf]; 
				str[k++] = hexDigits[b & 0xf]; 
			} 
			return new String(str); 
		} catch (Exception e) {
			return null;
		} 
	}

	public static String URLEncode(String s) {
		String value = s;
		try {
			value = URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public static String URLDecode(String s) {
		String value = s;
		try {
			value = URLDecoder.decode(s, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public static String string2Unicode(String s) {
		try {
			StringBuffer out = new StringBuffer("");
			byte[] bytes = s.getBytes("unicode");
			for (int i = 2; i < bytes.length - 1; i += 2) {
				out.append("u");
				String str = Integer.toHexString(bytes[i + 1] & 0xff);
				for (int j = str.length(); j < 2; j++) {
					out.append("0");
				}
				String str1 = Integer.toHexString(bytes[i] & 0xff);

				out.append(str);
				out.append(str1);
				out.append(" ");
			}
			return out.toString().toUpperCase();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String unicode2String(String unicodeStr) {
		StringBuffer sb = new StringBuffer();
		String str[] = unicodeStr.toUpperCase().split("U");
		for (int i = 0; i < str.length; i++) {
			if (str[i].equals(""))
				continue;
			char c = (char) Integer.parseInt(str[i].trim(), 16);
			sb.append(c);
		}
		return sb.toString();
	}
	
	public static String replaceURLHost(String original, String host) {
        String re = "";
        final String http = "http://";
        int idx = 0;
        if (original.startsWith(http)) {
            idx = http.length()
                    + original.substring(http.length(), original.length())
                            .indexOf('/');
        } else {
            idx = original.indexOf('/');
        }
        re = original.substring(idx + 1, original.length());
        return host + re;
    }
	
	public static String getURLDomain(String original) {
        try {
            if (original.startsWith("http://")) {
                URL uri = new URL(original);
                return uri.getHost();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return original;
    }
	
	public static HashMap<String, String> parseUrl(String url) {
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			if (!TextUtils.isEmpty(url)) {
				String spStr[] = url.split("://");
				if (spStr.length == 2) {
					map.put(URL_KEY_HEADER, spStr[0]);
					String strand = spStr[1];
					if (!TextUtils.isEmpty(strand)) {
						String spStr1[] = strand.split("&");
						if (spStr1 != null) {
							for (int i = 0; i < spStr1.length; i++) {
								String spStr2[] = spStr1[i].split("=");
								if (spStr2.length == 1) {
									map.put(spStr2[0], "");
								} else {
									map.put(spStr2[0], spStr2[1]);
								}
							}
						}
					}
				}
			}
			return map;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getDecimalFormatString(float money) {
		return getDecimalFormatString(money, "##,###.00");
	}
	
	public static String getDecimalFormatString(float money, String format) {
		DecimalFormat myformat = new DecimalFormat();
		myformat.applyPattern(format);
		return myformat.format(money);
	}
	
	public static String replaceSpaceChars(String value) {
		if(value != null) {
			String dest = "";
	        if (value!=null) {
	            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	            Matcher m = p.matcher(value);
	            dest = m.replaceAll("");
	        }
	        return dest;
		} else {
			return null;
		}
	}
	
	/**
	 * 比较版本号
	 * @param local
	 * @param server
	 * @return -1 local > server, 0 local = server, 1 local < server
	 */
	public static int compareVersion(String local, String server) {
		int result = 0;
		try {
			String[] arrlocal = local.split("\\.");
			String[] arrserver = server.split("\\.");
			
			long longlocal = 0;
			if(arrlocal.length > 0)
				longlocal += Integer.parseInt(arrlocal[0]) * 1000000000;
			if(arrlocal.length > 1)
				longlocal += Integer.parseInt(arrlocal[1]) * 1000000;
			if(arrlocal.length > 2)
				longlocal += Integer.parseInt(arrlocal[2]) * 1000;
			if(arrlocal.length > 3)
				longlocal += Integer.parseInt(arrlocal[3]) * 1;
			
			long longserver = 0;
			if(arrserver.length > 0)
				longserver += Integer.parseInt(arrserver[0]) * 1000000000;
			if(arrserver.length > 1)
				longserver += Integer.parseInt(arrserver[1]) * 1000000;
			if(arrserver.length > 2)
				longserver += Integer.parseInt(arrserver[2]) * 1000;
			if(arrserver.length > 3)
				longserver += Integer.parseInt(arrserver[3]) * 1;
			
			if(longlocal > longserver)
				result = -1;
			else if(longlocal < longserver)
				result = 1;
			else
				result = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	public static void copyToClipboard(String text, String toast) {
		if(OSUtils.hasHoneycomb()) {
			ClipboardManager cm = (ClipboardManager) BaseApplication.get().
					getSystemService(Context.CLIPBOARD_SERVICE);
			cm.setPrimaryClip(ClipData.newPlainText(null, text));
		} else {
			android.text.ClipboardManager cm = (android.text.ClipboardManager) BaseApplication.get().
					getSystemService(Context.CLIPBOARD_SERVICE);
			cm.setText(text);
		}
		if(!TextUtils.isEmpty(text)) {
			MToast.showToastMessage(toast, Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 检测是否有emoji字符
	 * 
	 * @param source
	 * @return 一旦含有就抛出
	 */
	public static boolean containsEmoji(String source) {
		if (TextUtils.isEmpty(source)) {
			return false;
		}
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (isEmojiCharacter(codePoint)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
				|| (codePoint == 0xD)
				|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
				|| ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
	}

	/**
	 * 过滤emoji 或者 其他非文字类型的字符
	 * 
	 * @param source
	 * @return
	 */
	public static String filterEmoji(String source) {
		// 到这里铁定包含
		StringBuilder buf = new StringBuilder();
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (!isEmojiCharacter(codePoint)) {
				buf.append(codePoint);
			}
		}
		if (buf.length() == len) {
			buf = null;
			return source;
		} else {
			return buf.toString();
		}
	}
}
