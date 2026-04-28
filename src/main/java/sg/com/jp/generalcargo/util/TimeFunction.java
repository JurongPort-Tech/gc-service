package sg.com.jp.generalcargo.util;

import java.sql.Timestamp;
import java.util.Date;

public class TimeFunction {
	 public static final String NO_TIMESTAMP_INDICATOR = "***NONE***";
	    public static final String DDMMYYYY_HHMM_AA = "ddMMyyyy hh:mm aa";
	    public static final String DDMMYYYY_HHMM = "ddMMyyyy HH:mm";
	    
	    
	    /** Creates new TimeFunction */
	    public TimeFunction() {
	    }
	    
	    /**
	     * Get previous day
	     */
	    public static Date previousDate(Date date) {
	    	int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
	    	Date prevDate = new Date(date.getTime() - MILLIS_IN_DAY);
	    	return prevDate;
	    }
	    /**
	     * Validates that the time entered is in the military time format (2400 hours).
	     *
	     * @param hhTime The time to be checked.
	     * @return <code>true</code> if the time conforms to the military time standard;
	     *            <code>false</code> otherwise.
	     */
	    public static boolean validateMilitaryTime(int hhTime) {
	        String hhTimeStr = Integer.toString(hhTime);
	        int hhTimeStrLength = hhTimeStr.length();
	        
	        if ((hhTime >= 0) && (hhTime <= 2359)) {
	            String hhStr = null;
	            String mmStr = null;
	            
	            if (hhTimeStrLength == 4) {
	                hhStr = hhTimeStr.substring(hhTimeStrLength-4, hhTimeStrLength-2);
	            } else if (hhTimeStrLength == 3) {
	                hhStr = hhTimeStr.substring(hhTimeStrLength-3, hhTimeStrLength-2);
	            }
	            
	            if (hhTimeStrLength == 2) {
	                mmStr = hhTimeStr.substring(hhTimeStrLength-2, hhTimeStrLength);
	            } else if (hhTimeStrLength == 1) {
	                mmStr = hhTimeStr.substring(hhTimeStrLength-1, hhTimeStrLength);
	            }
	            
	            if (hhStr != null) {
	                if (Integer.parseInt(hhStr) > 23) {
	                    return false;
	                }
	            }
	            
	            if (mmStr != null) {
	                if (Integer.parseInt(mmStr) > 59) {
	                    return false;
	                }
	            }
	            
	            return true;
	        } else {
	            return false;
	        }
	    }
	    
	    /**
	     * Converts the time entered to military time display.
	     * <p>E.g. 700 -> 07:00</p>
	     *
	     * @param hhTime The time to be converted.
	     * @return A String of the time converted to conform to the military time standard.
	     */
	    public static String convertNumberTo24HourDisplay(int hhTime) {
	        StringBuffer hhTimeStrBuf = new StringBuffer(Integer.toString(hhTime));
	        
	        switch (hhTimeStrBuf.length()) {
	            case 1:
	                hhTimeStrBuf.insert(0, "000");
	                break;
	            case 2:
	                hhTimeStrBuf.insert(0, "00");
	                break;
	            case 3:
	                hhTimeStrBuf.insert(0, '0');
	                break;
	            case 4:
	                // No conversion needed
	                break;
	            default:
	        }
	        
	        hhTimeStrBuf = hhTimeStrBuf.insert(2, ':');
	        
	        return hhTimeStrBuf.toString();
	    }
	    
	    /**
	     * Converts the militray time display entered to a time value.
	     * <p>E.g. 07:00 -> 700 -or- 0700 -> 700</p>
	     *
	     * @param hhTimeStr The time to be converted.
	     * @return An integer of the time converted.
	     */
	    public static int convert24HourDisplayToNumber(String hhTimeStr) {
	        int colonIndx = hhTimeStr.indexOf(':');
	        int hhTime = -1;
	        
	        if (colonIndx != -1) {
	            // String is in the HH:MM format
	            StringBuffer hhTimeStrBuf = new StringBuffer(hhTimeStr);
	            hhTimeStrBuf = hhTimeStrBuf.deleteCharAt(colonIndx);
	            hhTime = Integer.parseInt(hhTimeStrBuf.toString());
	        } else {
	            hhTime = Integer.parseInt(hhTimeStr);
	        }
	            
	        return hhTime;
	    }
	    
	    /**
	     * Converts the date (DD/MM/YYYY) to a format to be stored into the database.
	     *
	     * @param dateStr The date to be converted.
	     * @param dateSeparator The separator charactor the separates the day from month from year.
	     * @return A java.sql.Timestamp date for insertion into the database; <CODE>null</CODE>
	     *          if there is nothing to convert.
	     */
	    public static Timestamp convertDateToDBTimestamp(String dateStr, String dateSeparator) {
	        if ((dateStr != null) && (!dateStr.equals(""))) {
	            Timestamp result = new Timestamp(0);
	            int separatorIndx1 = dateStr.indexOf(dateSeparator);
	            int separatorIndx2 = dateStr.indexOf(dateSeparator, separatorIndx1+1);
	            int spaceSeparatorIndx = dateStr.indexOf(" ", separatorIndx2+1);
	            int timeSeparatorIndx1 = dateStr.indexOf(":", spaceSeparatorIndx+1);
	            int timeSeparatorIndx2 = dateStr.indexOf(":", timeSeparatorIndx1+1);
	            int timeSeparatorIndx3 = dateStr.indexOf(".", timeSeparatorIndx2+1);

	            String dayStr = dateStr.substring(0, separatorIndx1);
	            String monthStr = dateStr.substring(separatorIndx1+1, separatorIndx2);
	            String yearStr = null;

	            // Check if there is a time component
	            if (spaceSeparatorIndx != -1) {
	                String hhStr = dateStr.substring(spaceSeparatorIndx+1, timeSeparatorIndx1);
	                String mmStr = dateStr.substring(timeSeparatorIndx1+1, timeSeparatorIndx2);
	                String ssStr = dateStr.substring(timeSeparatorIndx2+1, timeSeparatorIndx3);
	                String msStr = dateStr.substring(timeSeparatorIndx3+1);

	                result.setHours(Integer.parseInt(hhStr));
	                result.setMinutes(Integer.parseInt(mmStr));
	                result.setSeconds(Integer.parseInt(ssStr));
	                result.setNanos(Integer.parseInt(msStr));

	                yearStr = dateStr.substring(separatorIndx2+1, spaceSeparatorIndx);
	            } else {
	                yearStr = dateStr.substring(separatorIndx2+1);
	            }

	            int monthInt = Integer.parseInt(monthStr);
	            monthInt -= 1; // Month goes from 0-11
	            int yearInt = Integer.parseInt(yearStr);
	            yearInt -= 1900;    // Java starts year at 1900

	            result.setDate(Integer.parseInt(dayStr));
	            result.setMonth(monthInt);
	            result.setYear(yearInt);

	            return result;
	        } else {
	            // Nothing to convert
	            return null;
	        }
	    }
	    /**
	     * Converts the database Date date a displayable date (DD/MM/YYYY).
	     *
	     * @param dateTimestamp The database date to be converted.
	     * @param dateSeparator The separator charactor the separates the day from month from year.
	     * @param returnTime <code>true</code> if the results should contain the time
	     *                      component; <code>false</code> otherwise.
	     * @return A String date in the format DD/MM/YYYY.
	     */
	    public static String convertDBDateToDate(Date date) {
	        if ((date == null) || (date.equals(""))) {
	            return "";
	        } else {
	            String result = null;

	            int dayInt = date.getDate();
	            String dayStr = Integer.toString(dayInt);

	            if (dayInt < 10) {
	                // Single digit, add padding
	                dayStr = "0" + dayStr;
	            }

	            int monthInt = date.getMonth();
	            monthInt += 1; // Month goes from 0-11
	            String monthStr = Integer.toString(monthInt);

	            if (monthInt < 10) {
	                // Single digit, add padding
	                monthStr = "0" + monthStr;
	            }

	            int yearInt = date.getYear();
	            yearInt += 1900; // Java starts year at 1900
	            String yearStr = Integer.toString(yearInt);
	            result = dayStr  + monthStr  + yearStr;
	            return result;
	        }
	    }
	    
	    
	    /**
	     * Converts the database Timestamp date a displayable date (DD/MM/YYYY).
	     *
	     * @param dateTimestamp The database date to be converted.
	     * @param dateSeparator The separator charactor the separates the day from month from year.
	     * @param returnTime <code>true</code> if the results should contain the time
	     *                      component; <code>false</code> otherwise.
	     * @return A String date in the format DD/MM/YYYY.
	     */
	    public static String convertDBTimestampToDate(Timestamp dateTimestamp,
	        String dateSeparator, boolean returnTime) {
	//DebugOut.println("[TimeFunction:convertDBTimestampToDate] Entering method");
	        if ((dateTimestamp == null) || (dateTimestamp.equals(""))) {
	            return NO_TIMESTAMP_INDICATOR;
	        } else {
	            String result = null;

	            int dayInt = dateTimestamp.getDate();
	            String dayStr = Integer.toString(dayInt);

	            if (dayInt < 10) {
	                // Single digit, add padding
	                dayStr = "0" + dayStr;
	            }

	            int monthInt = dateTimestamp.getMonth();
	            monthInt += 1; // Month goes from 0-11
	            String monthStr = Integer.toString(monthInt);

	            if (monthInt < 10) {
	                // Single digit, add padding
	                monthStr = "0" + monthStr;
	            }

	            int yearInt = dateTimestamp.getYear();
	            yearInt += 1900; // Java starts year at 1900
	            String yearStr = Integer.toString(yearInt);

	            result = dayStr + dateSeparator + monthStr + dateSeparator + yearStr;

	            if (returnTime) {
	                String hhStr = Integer.toString(dateTimestamp.getHours());
	                if(dateTimestamp.getHours() < 10){
	                	hhStr = "0" + hhStr;
	                }
	                String mmStr = Integer.toString(dateTimestamp.getMinutes());
	                if(dateTimestamp.getMinutes() < 10){
	                	mmStr = "0" + mmStr;
	                }
	                String ssStr = Integer.toString(dateTimestamp.getSeconds());
	                if(dateTimestamp.getSeconds() < 10){
	                	ssStr = "0" + ssStr;
	                }
	                String msStr = Integer.toString(dateTimestamp.getNanos());

	                result += " " + hhStr + ":" + mmStr + ":" + ssStr + "." + msStr;
	            }

	//DebugOut.println("[TimeFunction:convertDBTimestampToDate] result = '" + result + "'");
	//DebugOut.println("[TimeFunction:convertDBTimestampToDate] Exiting method");
	            return result;
	        }
	    }
	    
	    /**
	     * Converts the database Timestamp date a displayable date (DD/MM/YYYY).
	     *
	     * @param dateTimestamp The database date to be converted.
	     * @param dateSeparator The separator charactor the separates the day from month from year.
	     * @return A String date in the format DD/MM/YYYY.
	     */
	    public static String convertDBTimestampToDate(Timestamp dateTimestamp,
	        String dateSeparator) {
	        return convertDBTimestampToDate(dateTimestamp, dateSeparator, false);
	    }
	    
	    /**
	     * Removes the time associated with a timestamp.
	     *
	     * @param date The timestamp to have the time portion removed. The format of the
	     *              timstamp must be in (DD/MM/YYYY HH:MM:SS.NS).
	     * @return A String date in the format DD/MM/YYYY.
	     */
	    public static String timestampToDateOnlyFormat(String timestamp) {
	        if ((timestamp == null) || (timestamp.equals(""))) {
	            return NO_TIMESTAMP_INDICATOR;
	        } else {
	            int sepIndx = timestamp.indexOf(" ");

	            return (sepIndx != -1) ? timestamp.substring(0, sepIndx) : NO_TIMESTAMP_INDICATOR;
	        }
	    }
	    
	    /**
	     * Converts the Date String (DDMMYYYY) to a TimeStamp.
	     *
	     * @param dateStr The date to be converted.
	     * @return A java.sql.Timestamp date for insertion into the database; <CODE>null</CODE>
	     *          if there is nothing to convert.
	     */
	    public static Timestamp convertStringToTimestamp(String dateStr) {
	         
	        if ((dateStr != null) && (!dateStr.equals(""))) {
	            Timestamp result = new Timestamp(0);

	            String dayStr   = dateStr.substring(0, 2);
	            String monthStr = dateStr.substring(2, 4);
	            String yearStr  = dateStr.substring(4);
	            
	            int monthInt = Integer.parseInt(monthStr);
	            monthInt -= 1; // Month goes from 0-11
	            int yearInt = Integer.parseInt(yearStr);
	            yearInt -= 1900;    // Java starts year at 1900

	            result.setDate(Integer.parseInt(dayStr));
	            result.setMonth(monthInt);
	            result.setYear(yearInt);

	            return result;
	        } else {
	            // Nothing to convert
	            return null;
	        }
	    }
	    
}
