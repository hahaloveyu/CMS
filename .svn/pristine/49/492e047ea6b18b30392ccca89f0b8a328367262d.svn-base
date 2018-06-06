package com.toptime.cmssync.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

/**
 * 日期类转换工具
 * 
 * @author ws
 * 
 */
public class DateUtil {

	private static final Logger logger = Logger.getLogger(DateUtil.class);

	/**
	 * 长整形时间类型转换为字符串时间类型
	 * 
	 * @param longdate
	 *            世纪秒的长整形格式
	 * @param pattern
	 *            要转换的格式
	 * @return
	 */
	public static String parseLongToDate(long longdate, String pattern) {
		Date d = new Date(longdate);
		String format = DateFormatUtils.format(d, pattern);
		return format;
	}

	/**
	 * 长整形时间类型转换为字符串时间类型
	 * 
	 * @param longdate
	 *            世纪秒的长整形格式
	 * @return
	 */
	public static String parseLongToDate(long longdate) {
		Date d = new Date(longdate);
		String format = DateFormatUtils.format(d, "yyyy-MM-dd HH:mm:ss");
		return format;
	}

	/**
	 * <B>按指定格式格式化日期</B>
	 * 
	 * @param date
	 *            日期对象
	 * @param returntype
	 *            返回日期格式
	 * @return String 格式化后的日期字符串
	 */
	public static String DateFormat(Date date, String returntype) {

		SimpleDateFormat df1 = new SimpleDateFormat(returntype);

		return df1.format(date);
	}

	/**
	 * <B>按指定格式格式化日期</B>
	 * 
	 * @param date
	 *            字符串日期
	 * @param type
	 *            字符串格式
	 * @return Date 格式化后的日期字段
	 * @throws ParseException
	 *             日期parse异常
	 */
	public static Date DateFormat(String date, String type) {

		SimpleDateFormat df1 = new SimpleDateFormat(type);
		date = StringUtils.trimToEmpty(date);
		Date mydate = null;
		try {
			mydate = df1.parse(date);
		} catch (ParseException e1) {
			logger.info(e1.getMessage());
		}
		return mydate;
	}
	
	/**
	 * 获取2周前
	 * @return
	 */
	public static Long get2WeekTime() {   
        Calendar cal = Calendar.getInstance();   
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -14);   
        cal.set(Calendar.SECOND, 0);   
        cal.set(Calendar.MINUTE, 0);   
        cal.set(Calendar.HOUR_OF_DAY, 0);   
        return cal.getTime().getTime();   
    }  
	
	/**
	 * 获取1周前
	 * @return
	 */
	public static Long get1WeekTime() {   
        Calendar cal = Calendar.getInstance();   
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -7);   
		// cal.set(Calendar.SECOND, 0);
		// cal.set(Calendar.MINUTE, 0);
		// cal.set(Calendar.HOUR_OF_DAY, 0);   
        return cal.getTime().getTime();   
    }  
	
	/**
	 * 得到几天前的时间
	 */

	public static Date getDateBefore(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		return now.getTime();
	}

	/**
	 * 得到几天后的时间
	 */

	public static Date getDateAfter(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}

	/**
	 * 获取两个日期之间的所有日期集合
	 * 
	 * @param startDate
	 * @param endDate
	 * @return List<String>
	 */
	public static List<String> getBetweenDate(String startDate, String endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		List<String> list = new ArrayList<String>();
		try {
			Date date_start = sdf.parse(startDate);
			Date date_end = sdf.parse(endDate);
			Date date = date_start;
			Calendar cd = Calendar.getInstance();

			while (date.getTime() <= date_end.getTime()) {
				list.add(sdf.format(date));
				cd.setTime(date);
				cd.add(Calendar.DATE, 1);// 增加一天
				date = cd.getTime();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	/**
	 * 字符串时间转换为Date
	 * 
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static Date parseStringToDate(String dateStr, String pattern) {
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			date = new Date(0);
		}

		return date;
	}
	
}
