package com.toptime.cmssync.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * 系统常用字符串处理方法
 * 
 * @author Tomws
 * 
 */
public class StringUtils {
	
	public static String replace(String text, String searchString, String replacement){
		return org.apache.commons.lang.StringUtils.replace(text, searchString, replacement);
	}
	
	public static String[] split(String str, String separatorChar){
		return org.apache.commons.lang.StringUtils.split(str, separatorChar);
	}

	/**
	 * 判断字符串是否为空<br />
	 * 已处理字符串为空格情况
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {

		if (str == null) {
			return true;
		}
		if (str.trim().equals("")) {
			return true;
		}
		return false;

	}

	/**
	 * 是否包含要查找的字符
	 * 
	 * @param str
	 *            要查找的字符串
	 * @param searchStr
	 *            是否包含的字符
	 * @return
	 */
	public static boolean isInclude(String str, String searchStr) {

		int temp = org.apache.commons.lang.StringUtils.indexOf(str, searchStr);
		if (temp > -1)
			return true;
		else
			return false;

	}

	/**
	 * 格式化字符串方法<br />
	 * 空字符串返回为""格式,字符串做trim处理
	 * 
	 * @param str
	 *            需要处理的字符串
	 * @return 格式化后的字符串
	 */
	public static String passerStr(String str) {
		str = org.apache.commons.lang.StringUtils.trimToEmpty(str);

		return str;

	}

	/**
	 * 格式化字符串方法<br />
	 * 字符串做trim处理,为空或""时赋值
	 * 
	 * @param str
	 *            需要处理的字符串
	 * @param defaultVaule
	 *            默认值
	 * @return 格式化后的字符串
	 */
	public static String passerStr(String str, String defaultVaule) {
		str = org.apache.commons.lang.StringUtils.trimToEmpty(str);
		if (str.equals("")) {
			str = defaultVaule;
		}

		return str;

	}

	/**
	 * 用来过滤查询条件的表达式中的 OR,AND,NOT 之类的逻辑连接词
	 * 
	 * @param queryText
	 *            查询条件表达式
	 * @return
	 */
	public static String getTrainingText(String queryText) {
		String destStr = queryText;
		destStr = destStr.replaceAll("\\sOR", " ");
		destStr = destStr.replaceAll("\\sAND", " ");
		destStr = destStr.replaceAll("\\sNOT", " ");
		destStr = destStr.replaceAll("\\(", "");
		destStr = destStr.replaceAll("\\)", "");
		return destStr;
	}

	/**
	 * 替换字符串html字符,保留标红字段
	 * 
	 * @param strText
	 * @return
	 */
	public static String formatStr(String strText) {

		if (!isEmpty(strText)) {

			strText = strText.replace("<", "&lt;");
			strText = strText.replace(">", "&gt;");
			strText = strText.replace("\"", "&quot;");

			// 替换回标红
			strText = strText.replace("&lt;font color=red&gt;", "<font color=red>");
			strText = strText.replace("&lt;/font&gt;", "</font>");
		}

		return strText;

	}

	/**
	 * 禁止html代码生效
	 * 
	 * @param str
	 *            处理前的字符串
	 * @return 处理后的字符串
	 */
	public static String disableHtmlStr(String str) {

		str = str.replace("<", "&lt;");
		str = str.replace(">", "&gt;");
		str = str.replace("\"", "&quot;");

		return str;
	}

	/**
	 * 转义字符串的方法
	 * 
	 * @param str
	 * @return
	 */
	public static String encodeStr(String str) {
		try {
			str = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 处理以某些字符串结尾的方法
	 * 
	 * @param str
	 *            要处理的字符串
	 * @param endWithStr
	 *            要去掉的结尾字段串
	 * @return
	 */
	public static String deleteEndWithStr(String str, String endWithStr) {
		if (isEmpty(str) || isEmpty(endWithStr)) {
			return str;
		}

		if (str.endsWith(endWithStr)) {
			str = str.substring(0, str.length() - endWithStr.length());
		}
		return str;
	}

	/**
	 * 判断一个字符串中是否含有数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isHasNumber(String str) {
		Pattern pattern = Pattern.compile("[0-9]+");
		return pattern.matcher(str).find();
	}
	
	/**
	 * 处理查询结果中特殊字符
	 * 
	 * @param str
	 * @return
	 */
	public static String passerTitleString(String str) {
		str = StringUtils.replace(str, "}", " ");
		str = StringUtils.replace(str, "{", " ");
		str = StringUtils.replace(str, "\"", " ");
		str = StringUtils.replace(str, "&lt;", " ");
		str = StringUtils.replace(str, "&gt;", " ");
		return str;
	}
	
	/**
	 * 处理查询结果中特殊字符
	 * 
	 * @param str
	 * @return
	 */
	public static String passerListString(String str) {
		str = StringUtils.replace(str, "&lt;", " ");
		str = StringUtils.replace(str, "&gt;", " ");
		str = StringUtils.replace(str, "\"", " ");
		str = StringUtils.replace(str, ">", " ");
		str = StringUtils.replace(str, "<", " ");
		str = StringUtils.replace(str, "alert", "\\alert\\");
		str = StringUtils.replace(str, "script", "\\script\\");
		return str;
	}
	
	/**
	 * 传值替换敏感字符
	 * @param content
	 * @return
	 */
	public static String replaceH(String content) {
		
		content=content.replaceAll("&","&amp;amp;"); 
		content=content.replaceAll("<","&amp;lt;"); 
		content=content.replaceAll(">","&amp;gt;");
		content=content.replaceAll("'","&amp;#039;");

		//content=content.replaceAll("\"","&amp;quot;"); 



		return content; 

	}
	
	/**
	 * 字符串转换成 MD5 值
	 */
	public static String md5Hex32(String str) {
		StringBuffer hexString = new StringBuffer();
		if (str != null && str.trim().length() != 0) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(str.getBytes());
				byte[] hash = md.digest();
				for (int i = 0; i < hash.length; i++) {
					if ((0xff & hash[i]) < 0x10) {
						hexString.append("0"+ Integer.toHexString((0xFF & hash[i])));
					} else {
						hexString.append(Integer.toHexString(0xFF & hash[i]));
					}
				}
			} catch (NoSuchAlgorithmException e) {
				System.err.println(e.getMessage());
				return "";
			}

		}
		return hexString.toString().toUpperCase(); // 8~24位
	}
	
	/**
	 * 替换结果异常内容
	 * @param result
	 * @return
	 */
	public static String passerResults(String result){
		result = result.replaceAll("&#0", "");
		return result;
	}
	
}
