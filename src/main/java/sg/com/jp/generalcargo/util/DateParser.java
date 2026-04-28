package sg.com.jp.generalcargo.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateParser {

	private static final Log log = LogFactory.getLog(DateParser.class);
	public static final String DATE_PARTERN = "ddMMyyyy";
	public static final String DATETIME_PARTERN = "ddMMyyyy hhmm";
	public static final String DATE24TIME_PARTERN = "ddMMyyyy HHmm";
	
	
	public static Date parseDate(String str, String parsePattern) throws ParseException {
		if (str == null || parsePattern == null) {
			throw new IllegalArgumentException("Date and Pattern must not be null");
	    }
	    
	    SimpleDateFormat parser = new SimpleDateFormat(parsePattern);
	    Date date = parser.parse(str);
        if (date != null) {
            return date;
        }
	    throw new ParseException("Unable to parse the date: " + str, -1);
	}
	
	/**
     * Convert Date to String according to date pattern
     *
     * @param date Date need to convert to String
     * @return String of date converted based on the pattern
     */
    public static String dateToString(Date date) {
    	return convertDateToString(date, DATETIME_PARTERN);
    }
    
    public static String dateNoTimeToString(Date date) {
    	return convertDateToString(date, DATE_PARTERN);
    }
    
    public static String date24ToString(Date date) {
    	return convertDateToString(date, DATE24TIME_PARTERN);
    }
    
    /**
     * Convert Date to String according to date pattern
     *
     * @param date Date need to convert to String
     * @return String of date converted based on the pattern
     */
    public static String monthToName(String month) {
        if ("01".equals(month)) {  
        	return "Jan";
        }
        if ("02".equals(month)) {  
        	return "Feb";
        }
		if ("03".equals(month)) {  
			return "Mar";
		}
		if ("04".equals(month)) {  
			return "Apr";
		}
		if ("05".equals(month)) {  
			return "May";
		}
		if ("06".equals(month)) {  
			return "Jun";
		}
		if ("07".equals(month)) {  
			return "Jul";
		}
		if ("08".equals(month)) {  
			return "Aug";
		}
		if ("09".equals(month)) {  
			return "Sep";
		}
		if ("10".equals(month)) {  
			return "Oct";
		}
		if ("11".equals(month)) {  
			return "Nov";
		}
		if ("12".equals(month)) {  
			return "Dec";
		}
		
		return "";
    }
	
	/**
	 * get Date object with time from resultset
	 * @param rs
	 * @param colName
	 * @return date(time) 
	 * @throws SQLException
	 */
	public static java.sql.Date getDateTime(ResultSet rs, String colName) throws SQLException {
		java.sql.Date date =  rs.getDate(colName);
    	Timestamp ts = rs.getTimestamp(colName);
    	
    	if (date != null) 
    		date.setTime(ts.getTime());
    	
    	return date;
	}
	
	public static java.sql.Date parseSqlDate(String str, String parsePattern) throws ParseException {
		if (str == null)
			return null;
		
		Date date = parseDate(str, parsePattern);
		return new java.sql.Date(date.getTime());
	}
	
	/**
	 * 
	 */
	public static String getSysDate() {
		return convertDateToString(new Date(), "ddMMyyyy");
	}
	
	public static String getSysTime() {
		return convertDateToString(new Date(), "HHmm");
	}
	
	/**
	 * 
	 * @param amount
	 * @return Date that is less than "amount" months from current date 
	 */
	public static Date getMonthAddedSysDate(int amount) {
		Calendar now = Calendar.getInstance( );
		now.add(Calendar.MONTH, amount);
		return now.getTime();	
	}
	
	/**
	 * 
	 * @param amount
	 * @return Date that is less than "amount" months from current date 
	 */
	public static Date getMonthAdded(Date date, int month) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(GregorianCalendar.MONTH, month);
		return gc.getTime();
	}
	
	public static String getDateStringForThreeMonthsAgo() {
		return convertDateToString(getMonthAddedSysDate(-3), DATE_PARTERN);
	}
	
	/** 
	 * @param amount
	 * @return Date that is less than "amount" months from current date 
	 */
	public static Date getDayAddedSysDate(int amount) {
		Calendar now = Calendar.getInstance( );
		now.add(Calendar.DAY_OF_YEAR, amount);
		return now.getTime();	
	}
	
	public static String convertDateToString(Date date, String pattern) {
		if (date == null) {
			return "";
		}	
		SimpleDateFormat formatter = new SimpleDateFormat(pattern); 
		return formatter.format(date);
	}
	
	/**
	 * Compares two java.util.Date objects and returns a boolean based upon
	 * beginDate is before endDate
	 * 
	 * @param beginDate - java.util.Date
	 * @param endDate -  java.util.Date
	 * @return boolean
	 */
	public static boolean isBefore(java.util.Date beginDate, java.util.Date endDate) {
		if (beginDate == null || endDate == null) {
			return false;
		}
		
		GregorianCalendar calBegin = new GregorianCalendar();
		calBegin.setTime(beginDate);
		GregorianCalendar calEnd = new GregorianCalendar();
		calEnd.setTime(endDate);

		return calBegin.before(calEnd);

	}// isBefore()
	
	public static boolean isBeforeOrEqualMonthYear(Date beginDate, Date endDate) {
		if (beginDate == null || endDate == null) {
			return false;
		}
		
		GregorianCalendar calBegin = new GregorianCalendar();
		calBegin.setTime(beginDate);
		GregorianCalendar calEnd = new GregorianCalendar();
		calEnd.setTime(endDate);

		if (calBegin.get(GregorianCalendar.YEAR) < calEnd.get(GregorianCalendar.YEAR)) {
			return true;
		}
		else if (calBegin.get(GregorianCalendar.YEAR) == calEnd.get(GregorianCalendar.YEAR)) {
			return calBegin.get(GregorianCalendar.MONTH) <= calEnd.get(GregorianCalendar.MONTH);
		}
		
		return false;
	}
	
	/**
	 * Compares two java.util.Date objects and returns a boolean based upon
	 * beginDate is before endDate
	 * 
	 * @param fromDate - java.util.Date
	 * @param toDate -  java.util.Date
	 * @return boolean
	 */
	public static boolean isEqual(Date fromDate, Date toDate) {
		if (fromDate == null || toDate == null) {
			return false;
		}

		GregorianCalendar calBegin = new GregorianCalendar();
		calBegin.setTime(fromDate);
		GregorianCalendar calEnd = new GregorianCalendar();
		calEnd.setTime(toDate);

		return calBegin.get(GregorianCalendar.DATE) == calEnd.get(GregorianCalendar.DATE) 
							&& calBegin.get(GregorianCalendar.MONTH) == calEnd.get(GregorianCalendar.MONTH)
							&& calBegin.get(GregorianCalendar.YEAR) == calEnd.get(GregorianCalendar.YEAR);

	}// isBefore()
	
	/**
	 * Compares two FromDate and ToDate and returns a boolean based upon
	 * FromDate < ToDate
	 * 
	 * @param beginDate - java.util.Date
	 * @param endDate -  java.util.Date
	 * @return boolean
	 */
	public static boolean isLessThan(Date fromDate, Date toDate) {
		if (fromDate == null || toDate == null) {
			return false;
		}

		Date fromDateNoTime = removeTime(fromDate); 
		Date toDateNoTime = removeTime(toDate);
		GregorianCalendar gcFromDateNoTime = new GregorianCalendar();
		gcFromDateNoTime.setTime(fromDateNoTime);
		
		GregorianCalendar gcToDateNoTime = new GregorianCalendar();
		gcToDateNoTime.setTime(toDateNoTime);

		return gcFromDateNoTime.getTimeInMillis() < gcToDateNoTime.getTimeInMillis();

	}// isBefore()

	public static String getYear(java.util.Date date) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
        String year = String.valueOf(gc.get(GregorianCalendar.YEAR));
        return year;

    }
	
	public static String getPreviousMonthYear(java.util.Date date, int addMonth) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(GregorianCalendar.MONTH, addMonth);
        String year = String.valueOf(gc.get(GregorianCalendar.YEAR));
        return year;

    }
	
	public static int getYearInt(java.util.Date date) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
        return gc.get(GregorianCalendar.YEAR);
    }

	public static Date getCurrentDate() {
		GregorianCalendar gc = new GregorianCalendar();
		return gc.getTime();
	}
	
	public static String getCurrentStringDate() {
		GregorianCalendar gc = new GregorianCalendar();
		return convertDateToString(gc.getTime(), DATETIME_PARTERN);
	}
	
	public static int getCurrentDay() {
        return getDay(getCurrentDate());
    }
	
	public static String getCurrentMonth() {
        return getMonth(getCurrentDate());
    }
	
	public static String getPreviousMonth() {
        return getPreviousMonth(getCurrentDate(), -1);
    }
	
	public static String getPreviousMonth(int addMonth) {
        return getPreviousMonth(getCurrentDate(), addMonth);
    }
	
	public static String getPreviousMonthYear() {
        return getPreviousMonthYear(getCurrentDate(), -1);
    }
	
	public static String getPreviousMonthYear(int addMonth) {
        return getPreviousMonthYear(getCurrentDate(), addMonth);
    }

	public static String getCurrentYear() {
        return getYear(getCurrentDate());
    }
	
    public static String getMonth(java.util.Date date) {
    	GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
        String month = String.valueOf(gc.get(GregorianCalendar.MONTH) + 1);
        month = (month.length() == 1) ? "0" + month : month;        

        return month;
    }
    
    public static int getDay(java.util.Date date) {
    	GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
        return gc.get(GregorianCalendar.DATE);
    }
    
    public static String getPreviousMonth(java.util.Date date, int addMonth) {
    	GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(GregorianCalendar.MONTH, addMonth);
        String month = String.valueOf(gc.get(GregorianCalendar.MONTH) + 1);
        month = (month.length() == 1) ? "0" + month : month;        

        return month;
    }
    
    public static int getMonthInt(java.util.Date date) {
    	GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
        return gc.get(GregorianCalendar.MONTH) + 1;
    }
    
    /**
     * Make Date
     * @param days
     * @param month
     * @param year
     * @return
     */
    public static Date getDate(int days, int month, int year) {
    	GregorianCalendar gc = new GregorianCalendar();
    	gc.set(GregorianCalendar.DATE, days);
    	gc.set(GregorianCalendar.MONTH, month - 1);
    	gc.set(GregorianCalendar.YEAR, year);
    	
    	return gc.getTime();
    }
    /**
     * Make Date with time and AM or PM
     * @param days
     * @param month
     * @param year
     * @param hh
     * @param mm
     * @param isAM
     * @return
     */
    public static Date getDateTime(int days, int month, int year, int hh, int mm, boolean isAM) {
    	GregorianCalendar gc = new GregorianCalendar();

    	if (isAM) 
    		gc.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
    	else 
    		gc.set(GregorianCalendar.AM_PM, GregorianCalendar.PM);

    	gc.set(GregorianCalendar.MINUTE, mm);
    	gc.set(GregorianCalendar.HOUR, hh);
    	gc.set(GregorianCalendar.DATE, days);
    	gc.set(GregorianCalendar.MONTH, month - 1);
    	gc.set(GregorianCalendar.YEAR, year);
    	return gc.getTime();
    }
    /**
     * Make Date with time 
     * @param days
     * @param month
     * @param year
     * @param hh
     * @param mm
     * @return
     */
    public static Date getDateTime(int days, int month, int year, int hh, int mm) {
    	GregorianCalendar gc = new GregorianCalendar();
    	gc.set(GregorianCalendar.MINUTE, mm);
    	gc.set(GregorianCalendar.HOUR, hh);
    	gc.set(GregorianCalendar.DATE, days);
    	gc.set(GregorianCalendar.MONTH, month - 1);
    	gc.set(GregorianCalendar.YEAR, year);
    	return gc.getTime();
    }
    
    public static Date removeTime(Date date) {
        try {
        	String dateNoTime = dateNoTimeToString(date);
        	SimpleDateFormat fm = new SimpleDateFormat(DATE_PARTERN);
            fm.setLenient(false);
			return fm.parse(dateNoTime.trim());
		} catch (ParseException e) {
			log.error("Exception removeTime : ", e);
		}
		return getCurrentDate();
    }
    
    /**
     * Make next date for date
     * @param days
     * @param date
     * @return
     */
    public static Date getDateFor(int days, Date date) {
    	GregorianCalendar nextDate = new GregorianCalendar();
    	nextDate.setTime(date);
    	nextDate.add(GregorianCalendar.DATE, 1);
		return nextDate.getTime();
    }
    
    public static Date stringToDate(String stringDate, String format) throws Exception {
        if (stringDate == null || stringDate.trim().length() == 0) {
            return null;
        }

        try {
            SimpleDateFormat fm = new SimpleDateFormat(format);
            fm.setLenient(false);
            return fm.parse(stringDate.trim());

        } catch (ParseException e) {
        	log.error("Exception stringToDate : ", e);
        }
        
        return null;
    }

    /**
     * get begining date of month
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getBeginOfMonth(Date date) throws Exception {
    	Date fromDate; // get end_date of from date 
    	Date endDate; // // get end_date of current date
    	Date currentDate = DateParser.getCurrentDate();
    	
    	fromDate = getEndOfMonthFor(date);
    	endDate = getEndOfMonthFor(currentDate);
    	
    	if(isEqual(fromDate, endDate) == true) {
    		return date;
    	} else {
    		//get first Date of month 01/CurrentMonth/CurrentYear
    		return DateParser.getDate(1, DateParser.getMonthInt(currentDate), 
    				DateParser.getYearInt(currentDate));
    	}
    	//return null;
    }

    /**
     * Get date end of month
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getEndOfMonthFor(Date date) {
    	GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return CommonUtil.removeTime(DateParser.getDate(gc.getActualMaximum(Calendar.DAY_OF_MONTH), 
        		DateParser.getMonthInt(date), DateParser.getYearInt(date)));
    } 
    
    /**
     * Get date end of month
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getEndOfMonth() throws Exception {
    	GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(getCurrentDate());
		Date date = gc.getTime();
		
		return CommonUtil.removeTime(DateParser.getDate(gc.getActualMaximum(Calendar.DAY_OF_MONTH), 
        		DateParser.getMonthInt(date), DateParser.getYearInt(date)));

		
    }
    
    /**
     * Get date begin of month
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getBeginOfMonth() throws Exception {
    	Date currentDate = getCurrentDate();
		return DateParser.getDate(1, DateParser.getMonthInt(currentDate), 
				DateParser.getYearInt(currentDate));
    }
    
    /**
     * Get date begin of month
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getBeginOfMonthFor(Date date) {
		return DateParser.getDate(1, DateParser.getMonthInt(date), 
				DateParser.getYearInt(date));
    }
    
    /**
     * Get date end of month
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getEndOfMonth(Date fromDate, Date terminalDate)  {
    	Date endOfMonthDate = getEndOfMonthFor(fromDate);
		return endOfMonthDate.before(terminalDate) ? endOfMonthDate : terminalDate;
    }
    
    /**
     * Get Day begin of month
     * @param date
     * @return
     */
    public static int getDateEndMonth(Date date) {
    	GregorianCalendar currentDate = new GregorianCalendar();
    	currentDate.setTime(date);
    	return currentDate.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
    }
    /**
     * Get Hours
     * @param date
     * @return
     */
    public static int getHours(Date date) {
    	GregorianCalendar builDate = new GregorianCalendar();
    	builDate.setTime(date);
    	return builDate.get(GregorianCalendar.HOUR_OF_DAY);
    }

    /**
     * Get Minutes
     * @param date
     * @return
     */
    public static int getMinutes(Date date) {
    	GregorianCalendar builDate = new GregorianCalendar();
    	builDate.setTime(date);
    	return builDate.get(GregorianCalendar.MINUTE);

    }
    /**
     * Get HoursMinutes value from datetime.
     * @param date Date needs to get Hours Minutes
     * @return HoursMinutes value
     */
    public static int getHoursMinutes(Date date) {
    	String minutes = String.valueOf(getMinutes(date));
    	return Integer.parseInt(String.valueOf(getHours(date)) + ((minutes.length() == 1) ? "0" + minutes : minutes));
    }
    
    /**
     * Get days different between fromDate and toDate
     * @param fromDate begin Date
     * @param toDate End Date.
     * @return days value
     */
    public static long getDaysDifferent(Date fromDate, Date toDate) {
    	long fromDateLong = fromDate.getTime();
		long toDateLong = toDate.getTime();
		long days = (toDateLong - fromDateLong) / 86400000L;
	
		long hours = getHoursDifferent(fromDate, toDate);
		if (hours - (days * 24) >= 20) {
			days += 1;
		}
		return days;
    }
    
    
    /**
     * Get days different between fromDate and toDate
     * @param fromDate begin Date
     * @param toDate End Date.
     * @return days value
     */
    public static long getHoursDifferent(Date fromDate, Date toDate) {
    	long fromDateLong = fromDate.getTime();
		long toDateLong = toDate.getTime();
		return (toDateLong - fromDateLong) / 3600000L;
    }
    
    /**
	 * generate billing month
	 * @return
	 */
	public static String generateMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");      
        return sdf.format(getCurrentDate());
    }
	
	public static Timestamp getDateFromBillMonth(String billMonth) {
		GregorianCalendar gc = new GregorianCalendar();
    	gc.set(GregorianCalendar.MINUTE, 0);
    	gc.set(GregorianCalendar.HOUR, 0);
    	gc.set(GregorianCalendar.DATE, 1);
    	gc.set(GregorianCalendar.MONTH, new Integer(billMonth.substring(4, 6)).intValue() - 1);
    	gc.set(GregorianCalendar.YEAR, new Integer(billMonth.substring(0, 4)).intValue());
    	
 		return new Timestamp(gc.getTimeInMillis());
	}
	
	/**
	 * generate billing month
	 * @return
	 */
	public static String getCurrentMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");      
        return sdf.format(getCurrentDate());
    }
	
	/**
	 * generate billing month
	 * @return
	 */
	public static String getMonthYear(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");      
        return sdf.format(date);
    }
	/**
     *  Caculate Other Time Unit value
     * @return timeUnit
     */
    public static double getTimeUnit(Date fromDate, Date terminalDate) {
    	double timeUnit = 0;
    	//total day in one month
    	double totalDays = 0; 
    	double remainDays = 0;
    	Calendar cal = Calendar.getInstance();
    	try {
    		Date startDate = getBeginOfMonth();
    		Date endDate = getEndOfMonth();
    		
    		// check enddate must be less than termination date
    		boolean isTheSameMonth = getMonthInt(startDate) == getMonthInt(fromDate);
    		if (isTheSameMonth) {
    			if (startDate.before(fromDate)) {
    				startDate = fromDate;
    			}
    		}
    		
    		isTheSameMonth = getMonthInt(endDate) == getMonthInt(terminalDate);
    		
    		if (isTheSameMonth) {
    			if (endDate.after(terminalDate)) {
    				endDate = terminalDate;
    			}
    		}
			
    		cal.setTime(startDate);
    		
			totalDays = CommonUtil.parserDouble(cal.getActualMaximum(Calendar.DAY_OF_MONTH) + "");   		
			remainDays = CommonUtil.daysBetweenDouble(endDate, startDate) + 1;
		
			// get Time unit
			timeUnit = remainDays / totalDays;

    	} catch (Exception e) {
    		return 0;
		} 

    	return timeUnit;
    }
    
    
    /**
     *  Caculate Other Time Unit value
     * @return timeUnit
     */
    public static double getTimeUnitForAMonth(Date fromDate, Date endMonthDate) {
    	double timeUnit = 0;
    	//total day in one month
    	double totalDays = 0; 
    	double remainDays = 0;
    	Calendar cal = Calendar.getInstance();
    	try {
    		cal.setTime(fromDate);
    		
			totalDays = CommonUtil.parserDouble(cal.getActualMaximum(Calendar.DAY_OF_MONTH) + "");   		
			remainDays = CommonUtil.daysBetweenDouble(endMonthDate, fromDate) + 1;
		
			// get Time unit
			timeUnit = remainDays / totalDays;

    	} catch (Exception e) {
    		return 0;
		} 

    	return timeUnit;
    }
   
    public static Date getTruncDate(Date date) {
    	Calendar gc = new GregorianCalendar();
    	gc.setTime(date);
    	gc.set(Calendar.MILLISECOND, 0);
		gc.set(Calendar.SECOND, 0);
    	gc.set(Calendar.MINUTE, 0);
    	gc.set(Calendar.HOUR_OF_DAY, 0);
    	
    	
    	return new Date(gc.getTimeInMillis());
    }
   
    public static Timestamp getCurrentTimestamp() {
		GregorianCalendar gc = new GregorianCalendar();
		//return gc.getTime().getTime();
		
		return new Timestamp(gc.getTimeInMillis());
	}
}
