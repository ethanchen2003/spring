package com.ec.spring.utility;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * SystemTimeUtility is the system date/time utility to replace Calendar.getInstance() and new Date()
 * so that we can set specified date/time to test our code. And we can move the date to next day to simulate
 * the application run over day by day.
 * 
 * SystemTimeUtility cannot be spring bean because the specified date must be set before all spring beans created.
 * So all methods must be static method.
 */
public class SystemTimeUtility {
	private static Logger logger = LoggerFactory.getLogger(SystemTimeUtility.class);
	
	public final static int MILLSECOND_PER_HOUR = (1000 * 60 * 60);
	
	private final static String timeZoneKey = "app.timezone";
	
	private final static String defaultTimeZone = "UTC";
	
	private static int secondOffset;

	private static int minuteOffset;
	
	private static int hourOffset;
	
	private static int year;
	
	private static int month; //month starts from 1 to 12
	
	private static int date;
	
	private static long timeOffset = 0;
	
	private static boolean dirty = false;
	
	public static TimeZone setTimezone() {
		String timezone = System.getProperty(timeZoneKey);
		if (timezone == null || timezone.isEmpty()) {
			timezone = defaultTimeZone;
		} else {
			String[] validIDs = TimeZone.getAvailableIDs();
			boolean validFlag = false;
			for (String str : validIDs) {
				if (str != null && str.equals(timezone)) {
					validFlag = true;
					break;
				}
			}
			
			if(validFlag) {
				logger.info("timezone " + timezone + " is valid timezone.");
			} else {
				logger.info("timezone " + timezone + " is invalid timezone. use default time zone - " + defaultTimeZone);
				timezone = defaultTimeZone;
			}
		}
		
		TimeZone timeZone = TimeZone.getTimeZone(timezone);
		TimeZone.setDefault(timeZone);
		return timeZone;
	}
	
	
	private static long getOffSet(Calendar calendar1) {
		if(dirty) {
			Calendar calendar = (Calendar)calendar1.clone();
			long actualTime = calendar.getTimeInMillis();
			
			
			if(SystemTimeUtility.hourOffset != 0) {
				calendar.add(Calendar.HOUR_OF_DAY, hourOffset);
			}
	
			if(SystemTimeUtility.minuteOffset != 0) {
				calendar.add(Calendar.MINUTE, minuteOffset);
			}
			
			if(SystemTimeUtility.secondOffset != 0) {
				calendar.add(Calendar.SECOND, secondOffset);
			}
			
			if(year != 0) {
				calendar.set(Calendar.YEAR, year);
			}
	
			if(month != 0) {
				calendar.set(Calendar.MONTH, month - 1);
			}
			
			if(date != 0) {
				calendar.set(Calendar.DATE, date);
			}
			
			long offsetTime = calendar.getTimeInMillis();
			timeOffset = actualTime - offsetTime;
			dirty = false;
		}
		return timeOffset;
	}
	
	private static void addTimeInMills(long timeToAddInMillis) {
		//remove time from offset = add time
		timeOffset = timeOffset - timeToAddInMillis;
	}
	/*
	 * get Current Calendar
	 */
	public static Calendar getInstance() {
		Calendar calendar = Calendar.getInstance();
		long offset = getOffSet(calendar);
		if(offset != 0) {
			long tm = calendar.getTimeInMillis() - offset;
			Timestamp timestamp = new Timestamp(tm);
			calendar.setTime(timestamp);
		}
		return calendar;
	}

	public static long currentTimeMillis() {
		return getInstance().getTimeInMillis();
	}
	
	/*
	 * get Current Date
	 */
	public static Date now() {
		return getInstance().getTime();
	}
	
	public static void reset() {
		dirty = false;
		year = 0;
		month = 0;
		date = 0;
		hourOffset = 0;
		minuteOffset = 0;
		secondOffset = 0;
		timeOffset = 0;
	}
	
	public static void setYear(int year) {
		SystemTimeUtility.year = year;
		dirty = true;
	}

	public static void setMonth(int month) {
		SystemTimeUtility.month = month;
		dirty = true;
	}

	public static void setDate(int date) {
		SystemTimeUtility.date = date;
		dirty = true;
	}

	public static void setOffset(int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		hourOffset = hour - calendar.get(Calendar.HOUR_OF_DAY);
		minuteOffset = minute - calendar.get(Calendar.MINUTE);
		dirty = true;
	}	
	
	public static void setOffset(int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		hourOffset = hour - calendar.get(Calendar.HOUR_OF_DAY);
		minuteOffset = minute - calendar.get(Calendar.MINUTE);
		secondOffset = second - calendar.get(Calendar.SECOND);
		dirty = true;
	}	
	
	public static void setHourOffset(int hourOffsetParm) {
		hourOffset = hourOffsetParm;
		dirty = true;
	}	
	
	public static void toNextHours(int hours) {
		if(hours < 1) {
			return;
		}
		addTimeInMills(MILLSECOND_PER_HOUR * hours);
	}
	
	/*
	 * Move the system date to next date, same time.
	 */
	public static void toNextDay() {
		addTimeInMills(MILLSECOND_PER_HOUR * 24);
	}
	
	/*
	 * get offset from timezone to UTC in hour. Time Saving time is handled by java automatically.
	 */
	public static int getOffSet(String timeZone) {
		return getOffSet(TimeZone.getTimeZone(timeZone));
	}
		
	/*
	 * get offset from timezone to UTC in hour. Time Saving time is handled by java automatically.
	 */	
	public static int getOffSet(TimeZone timeZone) {
		Date dt = getInstance().getTime();;
		int hour = timeZone.getOffset(dt.getTime()) / MILLSECOND_PER_HOUR;
		return hour;
	}
	
	public static Date toLocalTime(Date utcDate, String timeZone) {
		return toLocalTime(utcDate, TimeZone.getTimeZone(timeZone));
	}
	
	public static Date toLocalTime(Date utcDate, TimeZone timeZone) {
		long utcTime = utcDate.getTime();
		long time = utcTime + timeZone.getOffset(utcTime);
		return new Date(time);
	}
}
