package com.toptime.cmssync.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 常用方法
 */
public class CommonUtil {

	private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
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
	 * 格式化字符串方法 空字符串返回为""格式,字符串做trim处理
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
	 * 替换结果异常内容
	 * 
	 * @param result
	 * @return
	 */
	public static String passerResults(String result) {
		result = result.replaceAll("&#0", "");
		result = result.replace("&lt;", "<");
		result = result.replace("lt;", "<");
		result = result.replace("&gt;", ">");
		result = result.replace("gt;", ">");
		// 半方大的空白
		result = result.replace("&ensp;", "");
		// 全方大的空白
		result = result.replace("&emsp;", "");
		// 不断行的空白格
		result = result.replace("&nbsp;", "");
		result = result.replace("&amp;", "&");
		result = result.replace("&quot;", "\"");
		return result;
	}

	/**
	 * 获得本地 IP 地址.使用于 Windows
	 * 
	 * @return
	 */
	public static String getWindowsIP() {
		String ip = "";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();
		} catch (UnknownHostException e) {
		}
		return ip.toString();
	}

	/**
	 * 返回时间的6种格式。 0=dayLong,1=monthLong,2=yearLong,
	 * 3=dayStr,4=monthStr,5=yearStr, <br>
	 * 以 2009-12-18 18:02:10 为例 毫秒设定为为000 <br>
	 * dayLong 当前日期的长整型，秒级1261065600(2009-12-18 00:00:00的长整型) <br>
	 * monthLong 当前月份的长整型，秒级1259596800(2009-12-01 00:00:00的长整型) <br>
	 * yearLong 当前年份的长整型，秒级1230739200(2009-01-01 00:00:00的长整型) <br>
	 * dayStr 包含至天数的时间 2009-12-18 <br>
	 * monthStr 包含至月份的时间 2009-12 <br>
	 * dayStr 包含至年份的时间 2009<br/>
	 * 
	 * @param longTime
	 *            毫秒级的时间
	 * @return
	 */
	public static List<String> getFormatTime(long longTime) {

		List<String> timeList = new ArrayList<String>(6);
		for (int i = 0; i < 6; i++) {
			timeList.add("");
		}
		if (longTime > 138850560000L && longTime < 631123200000L) {
			// 2014年01月01日 00:00->1388505600000 1990年01月01日 00:00->631123200000
			return timeList;
		}
		String dayLong = "";
		String monthLong = "";
		String yearLong = "";
		String dayStr = "";
		String monthStr = "";
		String yearStr = "";

		Date date = new Date(longTime);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		dayLong = String.valueOf(cal.getTimeInMillis() / 1000);

		// showLong(cal.getTimeInMillis());//1261065600

		String dataFormat = "yyyy-MM-dd";
		// 1261128540000->2009-12-18 17:29:00
		String strTime = long2String(cal.getTimeInMillis(), dataFormat);

		dayStr = strTime.substring(0);
		monthStr = strTime.substring(0, 7);
		yearStr = strTime.substring(0, 4);

		cal.set(Calendar.DAY_OF_MONTH, 1);
		monthLong = String.valueOf(cal.getTimeInMillis() / 1000);

		cal.set(Calendar.MONTH, 0);
		yearLong = String.valueOf(cal.getTimeInMillis() / 1000);

		timeList.set(0, dayLong);
		timeList.set(1, monthLong);
		timeList.set(2, yearLong);
		timeList.set(3, dayStr);
		timeList.set(4, monthStr);
		timeList.set(5, yearStr);
		return timeList;
	}

	/**
	 * 长整型转换为日期类型
	 * 
	 * @param longTime
	 *            长整型时间
	 * @param dataFormat
	 *            时间格式
	 * @return
	 */
	public static String long2String(long longTime, String dataFormat) {
		// （注意时区）
		Date d = new Date(longTime + getTimeZoneRawOffset(TimeZone.getDefault().getID()));
		SimpleDateFormat s = new SimpleDateFormat(dataFormat);
		String str = s.format(d);
		return str;
	}

	/**
	 * 日期时间转化为字符串.
	 * 
	 * @param formater
	 *            日期或时间的字符串格式.
	 * @param date
	 *            日期信息
	 * @return 如果日期或时间字符串格式\日期信息有效,则返回日期按照格式转化后的字符串;否则,返回null.
	 */
	public static String dateToString(String formater, Date date) {
		if (formater == null || "".equals(formater))
			return null;
		if (date == null)
			return null;
		return (new SimpleDateFormat(formater)).format(date);
	}

	/**
	 * 当前日期时间转化为字符串.<br>
	 * (默认使用<code>yyyy-MM-dd HH:mm:ss</code>作为日期时间的格式).
	 * 
	 * @return 返回转换后的日期字符串.
	 */
	public static String dateToString() {
		return dateToString(DATE_PATTERN, new Date());
	}

	/**
	 * 将日期时间字符串转换为指定时区的日期时间字符串.
	 * 
	 * @param srcFormater
	 *            待转化的日期时间的格式.
	 * @param srcDateTime
	 *            待转化的日期时间字符串.
	 * @param tagFormater
	 *            目标的日期时间的格式.
	 * @param tagTimeZoneId
	 *            目标的时区编号.
	 * @return 如果所有参数有效,则返回转化后的日期时间字符串;否则,返回null.
	 */
	private static String getDateStrBySrcDateStrAndTagTimezoneId(String srcFormater, String srcDateTime, String tagFormater, String tagTimeZoneId) {
		if (srcFormater == null || "".equals(srcFormater))
			return null;
		if (srcDateTime == null || "".equals(srcDateTime))
			return null;
		if (tagFormater == null || "".equals(tagFormater))
			return null;
		if (tagTimeZoneId == null || "".equals(tagTimeZoneId))
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(srcFormater);
		try {
			int diffTime = getTimeZoneRawOffset(tagTimeZoneId);
			Date d = sdf.parse(srcDateTime);
			long nowTime = d.getTime();
			long newNowTime = nowTime - diffTime;
			d = new Date(newNowTime);
			return dateToString(tagFormater, d);
		} catch (ParseException e) {
			return null;
		} finally {
			sdf = null;
		}
	}

	/**
	 * 获取指定时区与GMT的时间差.(单位:毫秒)
	 * 
	 * @param timeZoneId
	 *            时区Id
	 * @return 指定时区与GMT的时间差.(单位:毫秒)
	 */
	public static int getTimeZoneRawOffset(String timeZoneId) {
		return TimeZone.getTimeZone(timeZoneId).getRawOffset();
	}

	/**
	 * 根据日期时间字符串和时区,得到格林威治日期时间的毫秒值(Long) .<br>
	 * 但必须保证日期时间和时区是对应的,即:如果日期时间是北京时间,那么时区必须为GMT+8;反之亦然. (日期时间字符串的样式必须是
	 * <code>yyyy-MM-dd HH:mm:ss</code>).
	 * 
	 * @param dateStr
	 *            待转化的日期时间.
	 * @param timeZoneId
	 *            待转化的日期时间的时区.
	 * @return 如果日期时间以及时区有效,则返回转化后的格林威治日期时间的毫秒值;否则,返回null.
	 * @see #getDateStrBySrcDateStrAndTagTimezoneId(String, String, String,
	 *      String)
	 */
	public static Long dateStrToGMTLong(String dateStr, String timeZoneId) {
		return dateStrToGMTLong(dateStr, timeZoneId, null);
	}

	/**
	 * 根据日期时间字符串和时区,得到格林威治日期时间的毫秒值(Long) .<br>
	 * 但必须保证日期时间和时区是对应的,即:如果日期时间是北京时间,那么时区必须为GMT+8;反之亦然.
	 * 
	 * @param dateStr
	 *            待转化的日期时间.
	 * @param timeZoneId
	 *            待转化的日期时间的时区.
	 * @param datePattern
	 *            日期样式.(为空时默认为<code>yyyy-MM-dd HH:mm:ss</code>)
	 * @return 如果日期时间以及时区有效,则返回转化后的格林威治日期时间的毫秒值;否则,返回null.
	 * @see #getDateStrBySrcDateStrAndTagTimezoneId(String, String, String,
	 *      String)
	 */
	public static Long dateStrToGMTLong(String dateStr, String timeZoneId, String datePattern) {
		if (isNull(datePattern)) {
			datePattern = DATE_PATTERN;
		}
		if (dateStr == null || "".equals(dateStr))
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		try {
			if (timeZoneId == null || "".equals(timeZoneId)) {
				timeZoneId = "GMT+8:00";
			}
			String GMTDate = getDateStrBySrcDateStrAndTagTimezoneId(datePattern, dateStr, datePattern, timeZoneId);
			Date d = sdf.parse(GMTDate);
			return d.getTime();
		} catch (ParseException e) {
			return null;
		} finally {
			sdf = null;
		}
	}

	/**
	 * 判断一个字符串是否为空
	 * 
	 * @param str
	 *            传入的字符串
	 * @return 如果str为空返回true，不为空返回false
	 */
	public static boolean isNull(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

}
