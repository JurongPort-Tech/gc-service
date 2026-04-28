/*
* CommonUtility.java
* @version 1.0
* @lastupdate 25/4/2000
*
* Copyright 2000
*
* @Copyright 2001 National Computer System Pte Ltd. All Rights Reserved.
*
*----------------------------------------------------------------------
*Revision History (ONLY applicable after UAT)
*----------------
*Version Number              Change Request Number               Description
* 1.0                        lastupdate 25/4/2000
* 1.01                       24/12/2002 by Qida                  Modify parseDateToFmtStr(Date date)
*                                                                       parseDateToFmtStr(Timestamp tm)
*                                                                Add    formatDateToStr( Date date, String format)
* 1.02						02/07/2003 GCL						added a new replace string
*
* 1.03                      13/08/2003 by Jaisankar              Added a method to convert Milliseconds in Long to HH MM format
* 							17/Jan/2007	by YJ		    		SSL-CIM- 0000062 - ITH message receiver
* 1.04                      10/Mar/2011                         Enhancement to Berthing Reports
*-------------------------------------------------------------------------
*/

package sg.com.jp.generalcargo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
//import isms.parameter.mgr.*;
import java.math.BigDecimal;
// java Classes
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import isms.code.object.*;

/**
 * A common utility class for ISMS
 */

public class CommonUtility {
	private static final Log log = LogFactory.getLog(CommonUtility.class);

	public static String MTH_JAN = "JAN";
	public static String MTH_FEB = "FEB";
	public static String MTH_MAR = "MAR";
	public static String MTH_APR = "APR";
	public static String MTH_MAY = "MAY";
	public static String MTH_JUN = "JUN";
	public static String MTH_JUL = "JUL";
	public static String MTH_AUG = "AUG";
	public static String MTH_SEP = "SEP";
	public static String MTH_OCT = "OCT";
	public static String MTH_NOV = "NOV";
	public static String MTH_DEC = "DEC";
	
	
	public static final String WARNING = "warning";
	public static String DATA = "data";
	public static String TOTAL = "total";
	
	//back to forward param
	public static String RESULT = "result";
	public static String SERIALIZER = "NewBASerializer";
	public static String MSG = "msg";
	public static String ERROR = "error";
	public static String SUCCESS = "Operation is completed.";
	public static String JP_APPROVAL = "The amendment request is subject to JP approval.";
	
	//BARequest Status
	public static String PENDING = "P";//Pending
	public static String APPROVED = "A";//Approved
	public static String REJECTED = "R";//Rejected
	public static String CANCELLED = "C";//Cancelled
	
	 //Added by zhengjiliang 18/8/2011
	//NewBA
	public static String FLOATING_CRANE = "Floating Crane Operation";
	public static String TANDEM_LIFTING = "Tandem Lifting";
	//Amended on 9/3/2012 by Dongsheng for the request from Shah.
	//public static String TRANSPORTATION_HLIFTS = "Transportation of H/lifts";
	public static String TRANSPORTATION_HLIFTS = "Transportation of H/lifts exceeding 100T";
	//end
	
	//Start CR-OPS- 0000187 - Unify Berth Application
	public static String GC_OPERATIONS = "Gc Operations";
	//End CR-OPS- 0000187 - Unify Berth Application
	
	//Added by zhengjiliang 22/8/2011
	public static String SUBJECT_ITEM = "{item}";
	public static String VESSEL_VOYOUT = "{vessel/voyageOut}";
	public static String VOYIN = "{voyageIn}";
	public static String VOYOUT = "{voyageOut}";
	public static String BTR = "{Btr}";
	public static String ETB = "{Etb}";
	public static String ETD = "{Etd}";
	public static String PERSON = "{Person}";
	public static String PHONE = "{Phone}";
	public static String FAX = "{Fax}";
	public static String ANGENT = "{Agent}";
	//end
	//Added by zhengjiliang 18/9/2011
	public static String VAR_CODE = "{varCode}";
	//end
	public static String VESSEL_NAME = "{vessel name}";
	public static String OUT_VOYAGE = "{voyage}";
	public static String DATE_BA_SUBMITTED = "{date BA submitted}";
	
	
	/*
	 * This is NOT using SimpleDateFormat class, only dd, MM, yyyy, HH, mm is
	 * supported, You can swap the position only
	 */
	public static String DATETIME_FORMAT = "ddMMyyyy  HHmm";
	
	// List of allowed special characters
	public static String listOfAllowedSpecialChar = " _-";
	
	//Added by NS on 12-06-20 to show errors related To CVRS for BA-GB  update 
	//M1000103,M1000105 NS  changed msg from 24 to 36 Hours  as per Wan advice
	public static final Map<String, String> CVRS_ERROR_CONSTANT_MAP = 
		    Collections.unmodifiableMap(new HashMap<String, String>() {{ 
		        put("M1000101", "BTR Amend Request cannot be approved due to CVRS adjustment process failure. Please try again later.");
		        put("M1000102", "The selected booking date overlaps with existing immobilised slot/RW booking.");
		        put("M1000103","The number of accumulated BTR amendment hours will exceed 36. The adjustment is not allowed.");
		        put("M1000104","Reservation Window has been cancelled by another user. BA conversion is not allowed.");
		        put("M1000105","36 hours BTR amendment has been used up. Are you sure you want to proceed?");
		        put("M1000106","Accumulated BTR amendment will exceed 24. The adjustment is not allowed. Please find another available slot.");
		        put("M1000107","Reservation Window has been converted to BA by another user. ");
		        put("M1000108","Insufficient time clearance to adjacent vessel. The adjustment is not allowed.");
		        put("M40551","Update not allowed. Please enter a BTR more than 3 working days for Bulk Cement vessel.");
		        put("M40531","Berth Application not allowed. Please enter a BTR more than 3 working days for Bulk Cement vessel.");
		    }});


	// sets default timezone to China/Taiwan = Singapore
	static {
		TimeZone.setDefault(TimeZone.getTimeZone("CTT"));
	}

	/**
	 * Convert date/month to two digits.
	 *
	 * @author Henry Wahyudi
	 * @date 19/02/2000
	 *
	 * @param strField the date/month used, ie. 1, 2,..
	 * @return a string date/month in two digits form
	 */
	public static String convertTwoDigits(String strField) {
		// change date/month to two digits, ie. 01, 02..
		strField = (strField.length() == 1) ? "0" + strField : strField;
		return strField;
	} // public static String convertTwoDigits (String strField)

	/**
	 * Parse a string and break into various parts by the specified delimiters.
	 *
	 * @author Loo Tong
	 * @date 17/03/2000
	 * @param str the string to be parsed
	 * @param the delimiter
	 *
	 * @return the string array containing the parts
	 */
	public static final String[] parseString(String str, String delim) {
		// tentatively allocate only an array of 3 strings
		// if not enough, later then add some more
		int max = 3;
		String[] result = new String[max];

		int index = 0;
		int pos = 0;
		int count = 0;

		while (true) {
			pos = str.indexOf(delim, index);

			// no more parts
			if (pos == -1) {
				result[count] = str.substring(index);
				count++;
				break;
			}

			// put into the array
			result[count] = str.substring(index, pos);

			// increment count
			count++;

			// array not big enough
			if (count == max) {
				String[] tmp = new String[max *= 2];
				System.arraycopy(result, 0, tmp, 0, result.length);

				result = tmp;
				tmp = null;
			}

			// must cater for delimitor with length > 1
			// so cannot just + 1
			index = pos + delim.length();

		} // parseString()

		// compact the array
		String[] tmp = new String[count];
		System.arraycopy(result, 0, tmp, 0, count);

		result = null;
		return tmp;

	} // end of parseString()

	/**
	 * To pad the given string with spaces up to the given length. <br>
	 * e.g. rPad("ABCD", 10, ' ') returns "ABCD " which has a length of 10.
	 *
	 * This method has built-in 'intelligence' to handle cases where calling method
	 * tries to be funny and supply the following<br>
	 * - rPad("abc", 10, "123") it will return, "abc1231231"
	 *
	 * @author Loo Tong
	 * @date 17/03/2000
	 * @param str     String to be padded
	 * @param length  The required length of the resulted string
	 * @param padChar The required padding character.
	 * @return the padded string. If str already <I>longer<I> than <I>length<I>,
	 *         return str itself.
	 */
	public static final String rPad(String str, int length, String padStr) {
		int lOriginal = str.length();
		int lPadStr = padStr.length();
		int times2Pad = 0;
		int lPadded = 0;

		if (lOriginal >= length)
			return str;

		StringBuffer sb = new StringBuffer(str); // add the original str first
		String padded;

		times2Pad = (length - lOriginal) / lPadStr; // will give (1) if 3/2

		padded = dup(padStr, times2Pad);
		lPadded = padded.length();
		sb.append(padded); // pad in the repetitive characters

		// if still insufficient by the modulus e.g. 30/20 is 10
		if (lOriginal + lPadded < length) {
			int more = length - (lOriginal + lPadded);

			// add in the difference which is less entire length of padStr
			sb.append(padStr.substring(0, more));
		}

		return sb.toString();

	} // end of rPad()

	/**
	 * To pad the given string with a user specified character on the left up to the
	 * given length .<br>
	 * e.g. padString("ABCD", 10, 'X') returns "XXXXXXABCD" which has a length of
	 * 10.
	 *
	 * This method has built-in 'intelligence' to handle cases where calling method
	 * tries to be funny and supply the following<br>
	 * - lPad("abc", 10, "123") it will return, "1231231abc"
	 *
	 * @author Loo Tong
	 * @date 17/03/2000
	 * @param str    String to be padded.
	 * @param length The required length of the resulted string.
	 * @param padStr The required padding string.
	 * @return the padded string. If <I>str</I> already longer than <I>length</I>,
	 *         return <I>str</I> itself.
	 */
	public static final String lPad(String str, int length, String padStr) {
		int lOriginal = str.length();
		int lPadStr = padStr.length();
		int times2Pad = 0;
		int lPadded = 0;

		if (lOriginal >= length)
			return str;

		StringBuffer sb = new StringBuffer();
		String padded;

		times2Pad = (length - lOriginal) / lPadStr; // will give (1) if 3/2

		padded = dup(padStr, times2Pad);
		lPadded = padded.length();
		sb.append(padded); // pad in the repetitive characters

		// if still insufficient by the modulus e.g. 30/20 is 10
		if (lOriginal + lPadded < length) {
			int more = length - (lOriginal + lPadded);

			// add in the difference which is less entire length of padStr
			sb.append(padStr.substring(0, more));
		}

		sb.append(str); // pad the original string behind

		return sb.toString();

		// int l = str.length();

		// return ((l < length) ? dup(String.valueOf(padChar), length - l) : "") + str;

	} // end of lPad()

	/**
	 * To return a string which is filled with a specified string. <br>
	 * e.g. dup("*", 5) returns "*****", dup("OK", 3) returns "OKOKOK") repeated for
	 * given number of times
	 *
	 * @author Loo Tong
	 * @date 17/03/2000
	 * @param str String to be repeated/duplicated
	 * @param n   Number of time the string to be repeated/duplicated
	 * @return the resulted string
	 */
	public static final String dup(String str, int n) {
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < n; i++) {
			result.append(str);
		}
		return (result.toString());
	} // end of dup()

	/**
	 * Tests whether the specified string has the specified length.
	 *
	 * @param str     The string.
	 * @param intSize The desired size of the string.
	 * @return <CODE>true</CODE> if the specified string is of the specified
	 *         length;<CODE>false</CODE> otherwise.
	 */
	public static boolean isCorrectLength(String str, int intSize) {
		if (str == null) {
			return false;
		} // if (str == null)

		int intLen = str.length();
		return (intLen == intSize) ? true : false;

	} // end of isCorrectLength ()

	/**
	 * Tests whether the specified string's length is less than or equal to the
	 * specified length.
	 *
	 * @param str     The string.
	 * @param intSize The maximum length of the string.
	 * @return <CODE>true</CODE> if the specified string is less than or equal to
	 *         the specified length;<CODE>false</CODE> otherwise.
	 */
	public static boolean isLessMaxLength(String str, int intSize) {
		if (str == null) {
			return false;
		} // if (str == null)

		int intLen = str.length();
		return (intLen <= intSize) ? true : false;

	} // end of isLessMaxLength ()

	/**
	 * Tests whether the string is a valid Double.
	 *
	 * @param str    The string.
	 * @param booNeg boolean value, <CODE>true</CODE> if negative value is allowed,
	 *               <CODE>false</CODE> otherwise.
	 * @return <CODE>true</CODE> if the string is a valid Double; <CODE>false</CODE>
	 *         otherwise.
	 */
	public static boolean isValidDouble(String str, boolean booNeg) {
		double dblString;

		if (str == null) {
			return false;
		} // if (str == null)

		try {
			dblString = Double.valueOf(str.trim()).doubleValue();
		} catch (Exception exc) {
			return false;
		}

		if ((!booNeg) && dblString < 0) {
			return false;
		}

		return true;

	} // end of isValidDouble ()

	/**
	 * Search through the classpath and get the <code>File</code> object of the
	 * specified file that is first found.
	 *
	 * @param filename the file to find
	 * @return the File object
	 */
	public static File getFileFromClasspath(String filename) {

		String classpath = System.getProperty("java.class.path");
		String pathSeparator = System.getProperty("path.separator");

		StringTokenizer st = new StringTokenizer(classpath, pathSeparator);

		File f = null;

		while (st.hasMoreTokens()) {
			// for each path, try if the properties file exists
			f = new File(st.nextToken(), filename);
			if (f.exists()) {

				return f;
			}
		}

		return null;
	}

	/**
	 * Return the value as <code>""</code>(empty string) if the value is
	 * <code>null</code>
	 *
	 * @param val the value
	 * @return <code>""</code> (empty string) if value is <code>null</code>
	 */
	public static String deNull(String value) {
		return ((value == null) ? "" : value);
	}

	/**
	 * Check if the String is a valid date.
	 *
	 * @param strDate the date in format dd/mm/yyyy.
	 * @return true, if it is a valid date. Otherwise false, it is an invalid date.
	 */
	public static boolean isValidDate(String strDate) {
		int day, month, year;

		if (strDate == null || strDate.length() != 10)
			return false;

		try {
			day = Integer.parseInt(strDate.substring(0, 2));
			month = Integer.parseInt(strDate.substring(3, 5));
			year = Integer.parseInt(strDate.substring(6));
		} catch (NumberFormatException nfe) {
			log.error("Exception isValidDate : ", nfe);
			return false;
		}

		if (day <= 0 || month <= 0 || year <= 0)
			return false;
		if (month > 12)
			return false;

		// Check for a Leap year
		int modulo4 = year % 4;
		int modulo100 = year % 100;
		int modulo400 = year % 400;
		boolean leapYear = false;

		if (modulo4 == 0) {
			if (modulo400 == 0 && modulo100 == 0) {
				leapYear = true;
			} else if (modulo100 != 0) {
				leapYear = true;
			}
		}

		// Check max of days
		if ((month == 1) || (month == 3) || (month == 5) || (month == 7) || (month == 8) || (month == 10)
				|| (month == 12)) {
			if (day > 31)
				return false;
		} else if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
			if (day > 30)
				return false;
		} else {
			if (leapYear) {
				if (day > 29)
					return false;
			} else // not leap year
			{
				if (day > 28)
					return false;
			}
		}
		return true;
	}

	/**
	 * Check if the String is a valid time.
	 *
	 * @param strTime the time in format hh:mm.
	 * @return true, if it is a valid time. Otherwise false, it is an invalid time.
	 */
	public static boolean isValidTime(String strTime) {
		int hour, minute;

		if (strTime == null || strTime.length() != 5)
			return false;

		try {
			hour = Integer.parseInt(strTime.substring(0, 2));
			minute = Integer.parseInt(strTime.substring(3));
		} catch (NumberFormatException nfe) {
			log.error("Exception isValidTime : ", nfe);
			return false;
		}

		if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
			return false;

		return true;
	}

	/**
	 * Gets current system date in a calendar object form
	 *
	 * @author Loo Tong
	 * @date 02/03/2000
	 * @return Calendar object with the default TimeZone set to "CTT"
	 */
	public static Calendar getSysCalendar() {
		Calendar cal;

		Date today = new Date();
		cal = Calendar.getInstance();
		cal.setTime(today);

		// return a calendar object with the default TimeZone set to "CTT"
		return cal;
	}

	/**
	 * Gets current system date in the following format dd?mm?yyyy, where ?
	 * (separator) can be specified
	 *
	 * @author Loo Tong
	 * @date 02/03/2000
	 * @param separator that indicates separator required
	 * @return date string in dd?mm?yyyy format
	 */
	public static String getSysDate(String separator) {
		Date today = new Date();

		return getDate(today, separator);

	} // public static String getSysDate()

	/**
	 * Returns current system date measured to the nearest millisecond. or returns
	 * time set by config file
	 *
	 * @author Lim Wee Siong
	 * @date 17/03/2000
	 * @return date java.util.Date object in millisecond.
	 */
	public static Date getSysDate() {

		/*
		 * //this segment gets a date from t_parameter for testing purposes UtilParamMgr
		 * paramMgr = UtilParamMgr.getInstance(); String testDtStr = null; testDtStr =
		 * paramMgr.getParam("DATE", "TESTDATE").getDesc();
		 * 
		 * if (testDtStr == null) { return new Date(); } else {
		 * 
		 * Date date = new java.util.Date(); String dateFormat = "yyyy.MM.dd-hh:mm:ss";
		 * SimpleDateFormat formatter = new SimpleDateFormat( dateFormat );
		 * ParsePosition pos = new ParsePosition(0);
		 * 
		 * return formatter.parse( testDtStr, pos);
		 * 
		 * } //
		 */
		return new Date();

	}

	/**
	 * Returns a date object from input string indicating year, month and day
	 *
	 * @author Lim Wee Siong
	 * @date 15/05/2000
	 *
	 * @param year  indicator
	 * @param month indicator, 1=jan 2=feb ...
	 * @param date  indicator
	 * @return date java.util.Date object in millisecond.
	 */
	public static java.util.Date getDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day, 0, 0, 0);
		return cal.getTime();
	}

	/*
	 * public static Date getSysDate() {
	 * 
	 * Date date = null;
	 * 
	 * //if using system date using this block //return new Date(); //
	 * 
	 * //for testing purpose use this block //read from input file String ipfile =
	 * "DateConfig.cfg"; String dateFormat = "yyyy.MM.dd-hh:mm:ss";
	 * 
	 * String s1, s2 = new String(); try { BufferedReader in = new BufferedReader(
	 * new FileReader(ipfile) );
	 * 
	 * while ( (s1 = in.readLine()) != null ) s2 += s1 + "\n"; //trace
	 * log.info("s2" + s2); in.close(); } catch ( IOException e ) { return
	 * new Date(); }
	 * 
	 * //find token StringTokenizer st = new StringTokenizer(s2); while
	 * (st.hasMoreTokens()) { String tok = st.nextToken(); //trace
	 * log.info("tok" + tok); if ( tok.compareTo("[DATE_TIME_STAMP]") == 0
	 * ) { String dateString = st.nextToken(); //parse date with this format
	 * SimpleDateFormat formatter = new SimpleDateFormat( dateFormat );
	 * ParsePosition pos = new ParsePosition(0); date = formatter.parse( dateString,
	 * pos) ; } } return date; } //
	 */

	/**
	 * Gets current system time in the following format hh?mi, where ? (separator)
	 * can be specified
	 *
	 * @author Loo Tong
	 * @date 02/03/2000
	 * @param separator     that indicates separator required, if don't want any
	 *                      separator/delimiter, pass in ""
	 * @param international 24 hours or 12 hours ?
	 * @param seconds       get a time with seconds ?
	 * @param milliseconds  get a time with milliseconds don't try to be a cartoon
	 *                      by specifying false for 'seconds' and true for
	 *                      'milliseconds'
	 * @return date string in dd?mm?yyyy format
	 */
	public static String getSysTime(String separator, boolean international, boolean seconds, boolean milliseconds) {
		// get system time
		Date today = new Date();

		return getTime(today, separator, international, seconds, milliseconds);

	} // public static String getSysTime()

	/**
	 * Gets the required date string from a Calendar instance in the following
	 * format dd?mm?yyyy, where ? (separator) can be specified
	 *
	 * @author Loo Tong
	 * @date 03/03/2000
	 * @param cal       A Calendar instance
	 * @param separator that indicates separator required
	 * @return date string in dd?mm?yyyy format
	 */
	public static String getDateFromCal(Calendar cal, String separator) {
		if (cal == null)
			return null;

		Date date = cal.getTime();

		return getDate(date, separator);

	} // public static String getDateFromCal()

	/**
	 * Gets the required time string from a Calendar instance in the following
	 * format hh?mi, where ? (separator) can be specified
	 *
	 * @author Loo Tong
	 * @date 03/03/2000
	 * @param cal           A Calendar instance
	 * @param separator     that indicates separator required, if don't want any
	 *                      separator/delimiter, pass in ""
	 * @param international 24 hours or 12 hours ?
	 * @param seconds       get a time with seconds ?
	 * @param milliseconds  get a time with milliseconds don't try to be a cartoon
	 *                      by specifying false for 'seconds' and true for
	 *                      'milliseconds'
	 * @return date string in dd?mm?yyyy format
	 */
	public static String getTimeFromCal(Calendar cal, String separator, boolean international, boolean seconds,
			boolean milliseconds) {
		if (cal == null)
			return null;

		Date date = cal.getTime();

		return getTime(date, separator, international, seconds, milliseconds);

	} // public static String getTimeFromCal()

	/**
	 * Gets a date string in the following format dd?mm?yyyy, where ? (separator)
	 * can be specified
	 *
	 * @author Loo Tong
	 * @date 02/03/2000
	 * @param date      required date to be converted
	 * @param separator that indicates separator required
	 * @return date string in dd?mm?yyyy format
	 */
	public static String getDate(Date date, String separator) {
		String strDate;
		String strFormat;
		SimpleDateFormat dateFormat;

		if (date == null)
			return null;

		strFormat = "dd" + separator + "MM" + separator + "yyyy";

		dateFormat = new SimpleDateFormat(strFormat);

		strDate = dateFormat.format(date);

		return strDate;

	} // public static String getDate()

	/**
	 * Parse Date to array of string
	 *
	 * @author Rudy Sutjiato
	 * @date 13/03/2001
	 * @param date date to be converted
	 * @return array of string where String[0] = dd, String[1] = mm, String[2] =
	 *         yyyy
	 */
	public static String[] parseDateToStr(Date date) {
		String strFormat = "ddMMyyyy";
		SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
		String[] arrDate = new String[3];
		String strDate = null;
		if (date == null)
			return arrDate;

		strDate = dateFormat.format(date);
		arrDate[0] = strDate.substring(0, 2);
		arrDate[1] = strDate.substring(2, 4);
		arrDate[2] = strDate.substring(4);
		return arrDate;

	} // End of parseDateToStr()

	/**
	 * Parse Date to formatted string
	 *
	 * @author Rudy Sutjiato
	 * @date 22/03/2001
	 * @param date date to be converted
	 * @return String in required format
	 *
	 *         Format : dd = Day MM = Month yyyy = Year All format same as
	 *         SimpleDateFormat
	 */
	public static String parseDateToFmtStr(Date date, String formatStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
		if (date == null)
			return null;
		else
			return dateFormat.format(date);
	} // End of parseDateToFmtStr()

	/**
	 * Parse Date to date-time formatted string
	 *
	 * @author Raymond Wong
	 * @date 25/10/2001
	 * @param date date to be converted
	 * @return String in required format Only support following format Format : dd =
	 *         Day MM = Month yyyy = Year HH = Hour in 24hour format mm = minute ss
	 *         = seconds
	 */
	public static String parseDateToFmtStr(Date date) {
		return formatDateToStr(date, DATETIME_FORMAT);
	}

	/**
	 * Parse Date to date-time formatted string
	 *
	 * @author Wang Qida
	 * @date 23/12/2002
	 * @param date   date to be converted
	 * @param format pattern to be converted
	 * @return String in required format Only support following format Format : dd =
	 *         Day MM = Month yyyy = Year HH = Hour in 24hour format mm = minute ss
	 *         = seconds
	 */
	public static String formatDateToStr(Date date, String format) {
		if (date == null)
			return null;
		else {
			StringBuffer result = new StringBuffer();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			char ch;
			int j, day, mon, year, hour, min, sec;
			for (int i = 0; i < format.length(); i++) {
				ch = format.charAt(i);
				switch (ch) {
				case 'd':
					day = cal.get(Calendar.DAY_OF_MONTH);
					if ((i + 1) < format.length() && format.charAt(i + 1) == 'd') {
						i++; // Skip next 'd'
						result.append(day > 9 ? "" : "0").append(day);
					} else
						result.append(day);
					break;
				case 'M':
					mon = cal.get(Calendar.MONTH) + 1;
					j = 0;
					// Skip 'M' and count 'M'
					while ((i + j) < format.length() && format.charAt(i + j) == 'M')
						j++;
					i += j - 1;
					if (j > 2) { // "MMM" and above - Text name
						String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
								"Dec" };
						result.append(months[mon - 1]);
					} else if (j > 1) { // "MM" - always 2 digits
						result.append(mon > 9 ? "" : "0").append(mon);
					} else { // "M" - short form
						result.append(mon);
					}
					break;
				case 'y':
					year = cal.get(Calendar.YEAR);
					j = 0;
					// Skip 'y'
					while ((i + j) < format.length() && format.charAt(i + j) == 'y')
						j++;
					i += j - 1;
					if (j > 1) {
						result.append(year);
					} else {
						year = year % 100;
						result.append(year > 10 ? "" : "0").append(year);
					}
					break;
				case 'H':
					hour = cal.get(Calendar.HOUR_OF_DAY);
					if ((i + 1) < format.length() && format.charAt(i + 1) == 'H') {
						i++; // Skip next 'H'
						result.append(hour > 9 ? "" : "0").append(hour);
					} else
						result.append(hour);
					break;
				case 'm':
					min = cal.get(Calendar.MINUTE);
					if ((i + 1) < format.length() && format.charAt(i + 1) == 'm') {
						i++; // Skip next 'm'
						result.append(min > 9 ? "" : "0").append(min);
					} else
						result.append(min);
					break;
				case 's':
					sec = cal.get(Calendar.HOUR_OF_DAY);
					if ((i + 1) < format.length() && format.charAt(i + 1) == 's') {
						i++; // Skip next 's'
						result.append(sec > 9 ? "" : "0").append(sec);
					} else
						result.append(sec);
					break;
				default:
					result.append(ch);
				}
			}
			return result.toString();
		}
	} // End of parseDateToFmtStr()

	/**
	 * Parse Timestamp to date-time formatted string
	 *
	 * @author Raymond Wong
	 * @date 25/10/2001
	 * @param date date to be converted
	 * @return String in required format
	 *
	 *         Format : dd = Day MM = Month yyyy = Year HH = Hour in 24hour format
	 *         mm = minute
	 */
	public static String parseDateToFmtStr(Timestamp timestamp) {
		if (timestamp == null)
			return null;
		else
			return parseDateToFmtStr((Date) timestamp);
	} // End of parseDateToFmtStr()

	/**
	 * Parse Date to Date and Time string
	 *
	 * @author Rudy Sutjiato
	 * @date 14/03/2001
	 * @param date   date to be converted
	 * @param String date separator
	 * @param String time separator
	 * @return String in the following format DD?MM?YYYY HH!MM AM/PM
	 */
	public static String dateToDateTimeStr(Date date, String dateSeparator, String timeSeparator) {
		String strFormat = "dd" + dateSeparator + "MM" + dateSeparator + "yyyy" + " hh" + timeSeparator + "mm a";
		SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
		String strDate = null;
		if (date == null)
			return strDate;

		return dateFormat.format(date);

	} // End of datetoDateTimeStr()

	/**
	 * Gets a time string in the following format hh?mi, where ? (separator) can be
	 * specified
	 *
	 * @author Loo Tong
	 * @date 02/03/2000
	 * @param date          required date to be converted
	 * @param separator     that indicates separator required, if don't want any
	 *                      separator/delimiter, pass in ""
	 * @param international 24 hours or 12 hours ?
	 * @param seconds       get a time with seconds ?
	 * @param milliseconds  get a time with milliseconds don't try to be a cartoon
	 *                      by specifying false for 'seconds' and true for
	 *                      'milliseconds'
	 * @return date string in dd?mm?yyyy format
	 */
	public static String getTime(Date date, String separator, boolean international, boolean seconds,
			boolean milliseconds) {
		String strTime;
		String strFormat;
		SimpleDateFormat dateFormat;

		if (date == null)
			return null;

		/*
		 * h hour in am/pm (1~12) (Number) 12 H hour in day (0~23) (Number) 0 m minute
		 * in hour (Number) 30 s second in minute (Number) 55 S millisecond (Number) 978
		 * a am/pm marker (Text) PM
		 */

		if (international) // 24 hour clock
		{
			if (seconds) {
				if (milliseconds)
					strFormat = "HH" + separator + "mm" + separator + "ss" + separator + "SSS";
				else
					strFormat = "HH" + separator + "mm" + separator + "ss";
				;
			} else
				strFormat = "HH" + separator + "mm";
		} else // 12 hour clock
		{
			if (seconds) {
				if (milliseconds)
					strFormat = "hh" + separator + "mm" + separator + "ss" + separator + "SSS" + " a";
				else
					strFormat = "hh" + separator + "mm" + separator + "ss" + " a";
			} else
				strFormat = "hh" + separator + "mm" + " a";
		}

		dateFormat = new SimpleDateFormat(strFormat);

		strTime = dateFormat.format(date);

		return strTime;

	} // public static String getTime()

	/**
	 * Converts the specified date-time string to Timestamp.
	 *
	 * @param dateTimeStr The String object.
	 * @return Timestamp object. Format used is meant for Oracle dbs only
	 */
	public static java.sql.Timestamp toTimestamp(String dateTimeStr) {
		if (dateTimeStr == null)
			return null;
		java.sql.Date date = toSQLDateTime(dateTimeStr);
		return toTimestamp(date);
	} // public static Timestamp toTimestamp (Calendar cal)

	
	
	
	/**
	 * Converts the specified Calendar to Timestamp.
	 *
	 * @param date The Date object.
	 * @return Timestamp object. Format used is meant for Oracle dbs only
	 */
	public static java.sql.Timestamp toTimestamp(java.util.Date date) {
		if (date == null)
			return null;

		/*
		 * return new Timestamp(cal.get(Calendar.YEAR)-1900, cal.get(Calendar.MONTH),
		 * cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY),
		 * cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), 0);
		 */

		return new java.sql.Timestamp(date.getTime());

	} // public static Timestamp toTimestamp (Calendar cal)

	/**
	 * Converts the specified Calendar to Timestamp.
	 *
	 * @param cal The Calendar object.
	 * @return Timestamp object. Format used is meant for Oracle dbs only
	 */
	public static java.sql.Timestamp toTimestamp(Calendar cal) {
		if (cal == null)
			return null;

		/*
		 * return new Timestamp(cal.get(Calendar.YEAR)-1900, cal.get(Calendar.MONTH),
		 * cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY),
		 * cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), 0);
		 */
		Date date = cal.getTime();

		return new java.sql.Timestamp(date.getTime());

	} // public static Timestamp toTimestamp (Calendar cal)

	/**
	 * Converts the specified date string to SQL Date object. <CODE>null</CODE> is
	 * returned if the specified date is invalid.
	 *
	 * @param strDate The date string with format 'DD/MM/YYYY'.
	 * @return the SQL Date representation.
	 */
	public static java.sql.Date toSQLDate(String strDate) {
		int day = 0, month = 0, year = 0;
		long lTime = 0; // numbers of milliseconds since 1 Jan 1970

		try {
			day = Integer.parseInt(strDate.substring(0, 2));
			month = Integer.parseInt(strDate.substring(3, 5));
			year = Integer.parseInt(strDate.substring(6));

			Calendar cal = Calendar.getInstance();
			cal.set(year, month - 1, day);
			lTime = cal.getTime().getTime();
			return new java.sql.Date(lTime);
		} catch (NumberFormatException nfe) {
		}
		return null;
	} // public java.sql.Date toSQLDate (String strDate)

	/**
	 * Converts the specified date-time string to SQL Date object based on the
	 * specified date-time format. <CODE>null</CODE> is returned if the specified
	 * date is invalid.
	 *
	 * @param strDateTime    The date string with format 'DD/MM/YYYY'.
	 * @param dateTimeFormat Similar style to that of SimpleDateFormat.
	 * @return the SQL Date representation.
	 */
	public static java.sql.Date toSQLDateTime(String strDateTime, String dateTimeFormat) {
		if ((strDateTime == null) || (dateTimeFormat == null))
			return null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateTimeFormat);
		if ((dateTimeFormat.length() > 10) && (dateTimeFormat.substring(8, 10).equals("  "))) {
			if (strDateTime.indexOf(" ") == 8) {
				// To check first the string has one space between date and time.If one
				// space,then replace it with 2 spaces.
				if ((strDateTime.length() > 10) && (!strDateTime.substring(8, 10).equals("  "))) {
					strDateTime = strDateTime.replaceAll(" ", "  ");
				}
			}
		}
		java.util.Date date = dateFormat.parse(strDateTime, new ParsePosition(0));
		if (date == null)
			return null;
		return new java.sql.Date(date.getTime());
	} // public java.sql.Date toSQLDate (String strDateTime, String format)

	/**
	 * Converts the specified date-time string to SQL Date object based on the
	 * specified date-time format. <CODE>null</CODE> is returned if the specified
	 * date is invalid.
	 *
	 * @param strDateTime The date string with format 'DD/MM/YYYY'.
	 * @return the SQL Date representation.
	 */
	public static java.sql.Date toSQLDateTime(String strDateTime) {
		return toSQLDateTime(strDateTime, DATETIME_FORMAT);
	}

	/**
	 * Converts the specified date string to SQL Date object. <CODE>null</CODE> is
	 * returned if the specified date is invalid.
	 *
	 * @param strDay The string contains the day.
	 * @param strMth The string contains the month'.
	 * @param strYr  The string contains the year'.
	 * @return the SQL Date representation.
	 */
	public static java.sql.Date toSQLDate(String strDay, String strMth, String strYr) {
		int day = 0, month = 0, year = 0;
		long lTime = 0; // numbers of milliseconds since 1 Jan 1970

		try {
			day = Integer.parseInt(strDay);
			month = Integer.parseInt(strMth);
			year = Integer.parseInt(strYr);

			Calendar cal = Calendar.getInstance();
			cal.set(year, month - 1, day);
			lTime = cal.getTime().getTime();
			return new java.sql.Date(lTime);
		} catch (NumberFormatException nfe) {
		}
		return null;
	} // public java.sql.Date toSQLDate (String strDay, String strMth, String strYr)

	/**
	 * Converts the specified date string to Date object. <CODE>null</CODE> is
	 * returned if the specified date is invalid.
	 *
	 * @author Rudy Sutjiato
	 * @date 14/03/2001
	 * @param strDay The string contains the day.
	 * @param strMth The string contains the month'.
	 * @param strYr  The string contains the year'.
	 * @return the Date representation.
	 */
	public static Date toUtilDate(String strDay, String strMth, String strYr) {
		int day = 0, month = 0, year = 0;
		long lTime = 0;

		try {
			day = Integer.parseInt(strDay);
			month = Integer.parseInt(strMth);
			year = Integer.parseInt(strYr);

			Calendar cal = Calendar.getInstance();
			cal.set(year, month - 1, day);
			return cal.getTime();
		} catch (NumberFormatException nfe) {
		}
		return null;
	} // End of toUtilDate()

	/**
	 * Gets a specified month string as January, February..
	 *
	 * @author Henry Wahyudi
	 * @date 27/03/2000
	 *
	 * @param strMth month of 2 digits For example, "01", "02",..
	 * @return a specified month string. If the specified month is unknown, empty
	 *         string is returned.
	 */
	public static String getStrMth(String strIntMth) {

		if (strIntMth.equals("01")) {
			return MTH_JAN;
		} else if (strIntMth.equals("02")) {
			return MTH_FEB;
		} else if (strIntMth.equals("03")) {
			return MTH_MAR;
		} else if (strIntMth.equals("04")) {
			return MTH_APR;
		} else if (strIntMth.equals("05")) {
			return MTH_MAY;
		} else if (strIntMth.equals("06")) {
			return MTH_JUN;
		} else if (strIntMth.equals("07")) {
			return MTH_JUL;
		} else if (strIntMth.equals("08")) {
			return MTH_AUG;
		} else if (strIntMth.equals("09")) {
			return MTH_SEP;
		} else if (strIntMth.equals("10")) {
			return MTH_OCT;
		} else if (strIntMth.equals("11")) {
			return MTH_NOV;
		} else if (strIntMth.equals("12")) {
			return MTH_DEC;
		} else {
			return "";
		}

//  return null;
	}

	/**
	 * Gets an integer string representation of the specified month.
	 *
	 * @author Henry Wahyudi
	 * @date 27/03/2000
	 *
	 * @param strMth month of three letter string. For example, "Jan", "Feb",..
	 * @return a string number equivalent of the specified month string. If the
	 *         specified month is unknown, zero string is returned.
	 */
	public static String getIntMth(String strMth) {

		if (strMth.equals(MTH_JAN)) {
			return "01";
		} else if (strMth.equals(MTH_FEB)) {
			return "02";
		} else if (strMth.equals(MTH_MAR)) {
			return "03";
		} else if (strMth.equals(MTH_APR)) {
			return "04";
		} else if (strMth.equals(MTH_MAY)) {
			return "05";
		} else if (strMth.equals(MTH_JUN)) {
			return "06";
		} else if (strMth.equals(MTH_JUL)) {
			return "07";
		} else if (strMth.equals(MTH_AUG)) {
			return "08";
		} else if (strMth.equals(MTH_SEP)) {
			return "09";
		} else if (strMth.equals(MTH_OCT)) {
			return "10";
		} else if (strMth.equals(MTH_NOV)) {
			return "11";
		} else if (strMth.equals(MTH_DEC)) {
			return "12";
		} else {
			return "00";
		} // else if (strMth.equals(MTH_DEC))

//   return null;
	} // public static int getIntMth (String strMth)

	/**
	 * @param str24Hr Time in 24hr format
	 * @param delim   The delimitor used to separate the hour, minute & AM/PM
	 * @return Return the 24hr time in 12hr format (AM/PM). e.g. convert24HrTo12Hr(
	 *         "1715", ";" ) returns "5;15;PM"
	 */
	public static String convert24HrTo12Hr(String str24Hr, String delim) {
		String strAmPm = "AM";
		int intHour = Integer.parseInt(str24Hr.substring(0, 2));
		String strMin = str24Hr.substring(2, 4);
		StringBuffer sb = new StringBuffer();

		strAmPm = (intHour < 12) ? "AM" : "PM";
		if (intHour > 12)
			intHour -= 12;
		String strHour = String.valueOf(intHour);

		sb.append(strHour);
		sb.append(delim);
		sb.append(strMin);
		sb.append(delim);
		sb.append(strAmPm);

		return sb.toString();
	}

	/**
	 * @param str12Hr Time in 12hr format
	 * @param delim   The delimitor that has separated the hour, minute & AM/PM in
	 *                str12Hr
	 * @return Return the 12hr time in 24hr format. e.g. convert12HrTo24Hr(
	 *         "7;25;PM", ";" ) returns "1925"
	 */
	public static String convert12HrTo24Hr(String str12Hr, String delim) {
		String strHr, strHrMin, strMin, strAmPm;
		String[] strData = parseString(str12Hr, delim);
		int intHr = Integer.parseInt(strData[0]);
		strMin = strData[1];
		strAmPm = strData[2];

//		if ( strAmPm.equals("PM") && (intHr > 12) ) intHr += 12;
		if (strAmPm.equals("PM") && (intHr < 12))
			intHr += 12; // fern
		if (strAmPm.equals("AM") && (intHr == 12))
			intHr = 0;
		strHr = String.valueOf(intHr);
		if (intHr < 10)
			strHr = "0" + strHr;

		return (strHr + strMin);
	}

	/**
	 * To return the properties base on the given properties file.
	 * 
	 * @param strPath     The path of output file
	 * @param strFileName The name of output file
	 * @return Return the outputstream of the output file
	 */
	public static OutputStream getOutputStream(String strPath, String strFileName) throws IOException {
		File f = null;
		FileOutputStream fos = null;

		try {
			log.info("Dir. Path= " + strPath);
			f = new File(strPath);

			// If the directory specified does not exists, create it.
			if (!f.exists()) {
				log.info("The specified direcotry does not exist.");
				if (f.mkdirs()) {
					log.info("Directory created.");
				} // if (f.mkdirs())
			} // if (!f.exists())

			// Create the output file
			f = new File(f, strFileName);
			log.info("Full Name=" + f.toString());
			fos = new FileOutputStream(f);
		} catch (IOException ioe) {
			log.error("Exception getOutputStream : ", ioe);
		} // catch(IOException excIO)

		return fos;
	} // private OutputStream getOutputStream(prop, strFileName)

	// ------------------------------------
	// This method is written by Kim Boon
	// ------------------------------------
	/**
	 * To return the properties base on the given properties file.
	 * 
	 * @param path The path where the properties file is stored.
	 * @return Return the properties.
	 */
	public static Properties getProperties(String path) throws IOException {

		FileInputStream fis = null;
		Properties prop = new Properties();

		try {

			File f = new File(path);
			log.info("Properties path: " + f.toString());

			fis = new FileInputStream(f);
			prop.load(fis);
		} catch (Exception e) {
			
		} finally {
			if (fis != null) {
				fis.close();

			}
		}

		return prop;

	} // private Properties getConfig ()

	// ------------------------------------
	// This method is written by Kim Boon
	// ------------------------------------
	/**
	 * To return error message got from an exception.
	 * 
	 * @param e The exception to check its error message.
	 * @return Return the error message tied with the exception. If there is no
	 *         error message, return the exception name.
	 */
	public static String getExceptionMessage(Exception e) {
		String strMsg = e.getMessage();
		if (strMsg == null) {
			// Get the exception class : eg. NullPointerException instead of
			// java.lang.NullPointerException
			String[] tmp = parseString(e.toString(), ".");
			strMsg = tmp[tmp.length - 1];
		}
		return (strMsg);
	}

	// ------------------------------------
	// This method is written by Kim Boon
	// ------------------------------------
	/**
	 * To word wrap a string and return the result as vector
	 * 
	 * @param s       String to be word-wrapped
	 * @param maxlen  Maximum number of characters per line
	 * @param maxline Maximum number of lines
	 * @return Vector of strings up to <I>maxline</I> lines of string
	 */
	public static Vector wordWrap(String s, int maxlen, int maxline) {
		char newline = '\n';
		Vector v = new Vector();

		StringBuffer sb = new StringBuffer(s);
		int i;

		// First identify all the new line char, insert a space before and after the new
		// line char
		// if there isn't any.
		i = sb.toString().indexOf(newline, 0);
		while (i > -1) {
			if (i > 0) // Insert space only if it's not first char.
				if (sb.charAt(i - 1) != ' ')
					sb.insert(i++, " ");
			if (i == sb.length() - 1)
				break; // It's already last char, don't continue.
			if (sb.charAt(i + 1) != ' ')
				sb.insert(i + 1, " ");
			i = sb.toString().indexOf(newline, i + 1);
		}

		// Generate the word list
		String[] word = parseString(sb.toString(), " ");

		// reset the string buffer
		sb.setLength(0);

		for (i = 0; (i < word.length) && (v.size() < maxline); i++) {
			// The word is a newline, store the current line and force to start a new line.
			if (word[i].indexOf(newline) > -1) {
				v.addElement(sb.toString());
				sb.setLength(0);
				continue;
			}

			// Truncate the word if it's longer than max length.
			if (word[i].length() > maxlen)
				word[i] = word[i].substring(0, maxlen);

			// If the word can't fit, store the current line and start with a new line
			if (sb.length() + word[i].length() > maxlen) {
				v.addElement(sb.toString());
				sb.setLength(0);
			}

			// Append the word to the line
			if (sb.length() + word[i].length() <= maxlen) {
				// append the word to the current line
				sb.append(word[i] + " ");
			}

			// Force to new line if last word
			if (i == word.length - 1) {
				v.addElement(sb.toString());
				sb.setLength(0);
			}

		}

		// max line but there are still more words --- append "..." at the last line.
		if ((i < word.length) && (v.size() == maxline)) {
			String tmp = (String) v.elementAt(v.size() - 1);
			if (tmp.length() > maxlen - 3)
				tmp = tmp.substring(0, maxlen - 3);
			tmp += "...";
			v.setElementAt(tmp, v.size() - 1);
		}

		log.info(v);
		return v;
	}

	// --------------------------------------------
	// The following method is created by Loo Tong
	// --------------------------------------------

	// The weights for computing the Check Digit for Singapore IC.
	private final static int sWeight[] = { 2, 7, 6, 5, 4, 3, 2 };

	// The Check Digits for Singapore IC.
	private final static char sChkDigit[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'Z', 'J' };

	/**
	 * Checks if the Singapore IC Number is valid. Able to handle NRIC number of
	 * people born after 31/12/1999 (whom NRIC will starts with 'T' instead of 'S')
	 *
	 * @param icPassed The NRIC number passed for validation.
	 * @return status True if the IC number is valid; false otherwise.
	 */
	public static boolean checkSingaporeIC(String strIcPassed) {
		char icPassed[] = strIcPassed.toCharArray();
		String msg = new String();
		Date sysDate = new Date();

		String currentYear;
		SimpleDateFormat dateFormat;

		dateFormat = new SimpleDateFormat("yyyy");
		currentYear = dateFormat.format(sysDate);

		if (icPassed.length != 9) {
			msg = "Invalid Singapore IC.";
			return false;
		}

		// if (((sysDate.getYear() + 1900) < 2000) && icPassed[0] == 'T')
		// deprecated so better change. LT 3/3/2000
		if ((Integer.parseInt(currentYear) < 2000) && icPassed[0] == 'T') {
			msg = "Invalid Singapore IC.";
			return false;
		}

		boolean valid;
		int sum = 0;

		// add up the numeric values of the NRIC multiply by
		// the individual weightage for each specific position of
		// the NRIC number.

		for (int i = 0; i < 7; i++)
			sum += ((int) icPassed[i + 1] - 48) * sWeight[i];

		if (icPassed[0] == 'T') // DOB >= Yr 2000
			sum += 4;

		sum = Math.abs(sum);
		valid = (sChkDigit[10 - (sum % 11)] == icPassed[8]);

		if (!valid)
			msg = "Invalid Singapore IC.";
		return valid;
	}

	// The weights for computing the Check Digit for Foreign IC.
	private final static int fWeight[] = { 2, 7, 6, 5, 4, 3, 2 };

	// The Check Digits for FIN.
	private final static char fChkDigit[] = { 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'T', 'U', 'W', 'X' };

	/**
	 * Checks if the FIN is valid. Able to handle FIN number of people born after
	 * 31/12/1999 (whom FIN will starts with 'F' instead of 'G')
	 *
	 * @param icPassed The FIN number passed for validation.
	 * @return status True if the IC number is valid; false otherwise.
	 */
	public static boolean checkFIN(String strIcPassed) {
		char icPassed[] = strIcPassed.toCharArray();
		String msg = new String();
		Date sysDate = new Date();

		String currentYear;
		SimpleDateFormat dateFormat;

		dateFormat = new SimpleDateFormat("yyyy");
		currentYear = dateFormat.format(sysDate);

		if (icPassed.length != 9) {
			msg = "Invalid FIN.";
			return false;
		}

		// if (((sysDate.getYear() + 1900) < 2000) && icPassed[0] == 'T')
		// deprecated so better change. LT 3/3/2000
		if ((Integer.parseInt(currentYear) < 2000) && icPassed[0] == 'G') {
			msg = "Invalid FIN.";
			return false;
		}

		boolean valid;
		int sum = 0;

		// add up the numeric values of the NRIC multiply by
		// the individual weightage for each specific position of
		// the NRIC number.
		for (int i = 0; i < 7; i++)
			sum += ((int) icPassed[i + 1] - 48) * fWeight[i];

		if (icPassed[0] == 'G') // DOB >= Yr 2000
			sum += 4;

		sum = Math.abs(sum);
		valid = (fChkDigit[10 - (sum % 11)] == icPassed[8]);

		if (!valid)
			msg = "Invalid FIN.";
		return valid;
	}

	/**
	 * Serialised an object to byte array
	 * 
	 * @param obj the object to be serialised
	 * @return the byte array return
	 */
	public static final byte[] ObjectToByteArray(Object obj) {
		ByteArrayOutputStream bout = null;
		ObjectOutputStream oout = null;

		byte[] data = null;
		try {
			oout = new ObjectOutputStream(bout = new ByteArrayOutputStream());

			oout.writeObject(obj);
			data = bout.toByteArray();
		} catch (Exception e) {
		} finally {
			closeIO(null, bout);
			closeIO(null, oout);

			// cleanup
			bout = null;
			oout = null;
		}
		return data;
	}

	/**
	 * Unserialised an object from a byte array
	 * 
	 * @param data the byte array to be unserialised
	 * @return the object return
	 */
	public static final Object byteArrayToObject(byte[] data) {
		ObjectInputStream oin = null;
		Object obj = null;

		try {
			oin = new ObjectInputStream(new ByteArrayInputStream(data));
			obj = oin.readObject();
		} catch (Exception e) {
			log.error("Exception byteArrayToObject : ", e);
		} finally {
			// close the I/O stream
			closeIO(oin, null);

			oin = null;
		}
		return obj;
	}

	/**
	 * Close an Input and Output Stream
	 * 
	 * @param in  the InputStream
	 * @param out the OutputStream
	 */
	public static final void closeIO(InputStream in, OutputStream out) {
		try {
			if (in != null)
				in.close();

			if (out != null)
				out.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Find the <file>.properties in the classpath
	 * 
	 * @param pfile the properties filename
	 * @return the <file>.properties file name
	 */
	public static String getPropertiesFile(String pfile) {
		// get system properties
		String clspath = System.getProperty("java.class.path");
		String path_sep = System.getProperty("path.separator");
		String file_sep = System.getProperty("file.separator");
		// testing
		// log.info(clspath);

		// parse the class path
		String data[] = parseString(clspath, path_sep);

		int size = data.length;

		String fname = null;
		String filename = null;
		File file = null;
		for (int i = 0; i < size; i++) {
			fname = data[i];

			// testing
			log.info(fname);

			// check if the file is properties file
			if (fname.endsWith(".properties")) {
				if (fname.endsWith(pfile))
					return fname;

				continue;
			}

			// zip file ignore
			if (fname.endsWith(".zip"))
				continue;

			// jar file ignore
			if (fname.endsWith(".jar"))
				continue;

			// ends with file separator:/ or \ ignore
			if (fname.endsWith(file_sep))
				continue;

			// check if the file exists in that directory
			filename = fname + file_sep + pfile;
			file = new File(filename);

			// found the properties file
			if (file.exists())
				return filename;
		}
		return null;
	}

	// added by TLT - 6/5/1999
	/**
	 * Generalized the following method for anyone that wish to load in properties
	 * from a specified file
	 * 
	 * @param pfile the properties filename
	 * @return the properties loaded from the file
	 */
	public static Properties loadProperties(String pFile) {
		Properties properties = new Properties();
		FileInputStream in = null;
		try {
			// Create InputStream to the property file
			in = new FileInputStream(pFile);
			// properties = new Properties();

			// load in the properties from specified file
			properties.load(in);

		} catch (Exception e) {
			log.error("Exception loadProperties : ", e);
			// Utility.log(this,"loadProperties throws exception!");
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}

		return properties;
	}

	/**
	 * Replace the searchkey within the existing string with the required value
	 *
	 * @author Loo Tong 17/10/2000
	 *
	 * @param msgConstant Existing String to be replace
	 * @param searchKey   Key within the String to be searched and replaced
	 * @param value       The replaced value
	 *
	 * @return the resulted string
	 */
	public static String replaceStr(String msgToReplace, String searchKey, String value) {
		return replaceStr(msgToReplace, searchKey, value, "", "");
	}

	/**
	 * Replace the searchkey within the existing string with the required value
	 *
	 * @author Loo Tong 17/10/2000
	 *
	 * @param msgConstant Existing String to be replace
	 * @param searchKey   Key within the String to be searched and replaced
	 * @param value       The replaced value
	 * @param msgPrefix   Prefix defined (if any) before the searchKey
	 * @param msgSuffix   Suffix defined (if any) after the searchKey
	 *
	 * @return the resulted string
	 */
	public static String replaceStr(String msgToReplace, String searchKey, String value, String msgPrefix,
			String msgSuffix) {
		int count = 0;

		String msg = new String(msgToReplace);

		String key = msgPrefix + searchKey + msgSuffix;

		int pos = -1;

		while ((pos = msg.indexOf(key, count)) >= 0) {
			// peong 17102000, added 1 line
			StringBuffer sb = new StringBuffer(msg);

			sb.replace(pos, pos + key.length(), value);
			count = pos + value.length();
			// peong 17102000, added 1 line
			msg = sb.toString();
		}

		return msg;

	} // end of replaceStr()

	/**
	 * Reset Time fields of date to 00:00
	 *
	 * @author Rudy Sutjiato 20/03/2001
	 *
	 * @param date Date to be reset the time to zero
	 * @return date Converted Date
	 *
	 */
	public static Date resetTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(cal.HOUR, 0);
		cal.set(cal.MINUTE, 0);
		cal.set(cal.SECOND, 0);
		return cal.getTime();
	} // End of resetTime()

	/**
	 * Add Days
	 *
	 * @author Rudy Sutjiato 22/03/2001
	 *
	 * @param date Date to be add
	 * @param long Number of days to add
	 * @return date Added Date
	 *
	 */
	public static Date addDaysToDate(Date date, long numDay) {
		return new Date(resetTime(date).getTime() + (numDay * 86400000L));
	} // End of addDate()

	/**
	 * Add Hours
	 *
	 * @author Raymond Wong 27/10/2001
	 *
	 * @param date Date to be add
	 * @param int  Number of hours to add
	 * @return date Added Date
	 *
	 */
	public static Date addHoursToDate(Date date, long numHours) {
		return new Date(resetTime(date).getTime() + (numHours * 3600000L));
	} // End of addDate()

	/**
	 * Add Minutes
	 *
	 * @author Raymond Wong 27/10/2001
	 *
	 * @param date Date to be add
	 * @param int  Number of minutes to add
	 * @return date Added Date
	 *
	 */
	public static Date addMinutesToDate(Date date, long numMins) {
		return new Date(resetTime(date).getTime() + (numMins * 60000L));
	} // End of addDate()

	/**
	 * Compare the 2 dates.
	 *
	 * @author Grace Hui 24/03/2001
	 *
	 * @param Date startDate
	 * @param Date endDate
	 * @return true if the <code>startDate</code> is before <code>endDate</code>.
	 */
	public static boolean isStartBeforeEndDate(Date startDate, Date endDate) {
		boolean valid = true;

		if (startDate.after(endDate)) {
			valid = false;
		} else if (startDate.equals(endDate)) {
			valid = false;
		}
		return valid;
	}

	/**
	 * Compare the 2 dates.
	 *
	 * @author Lim Wee Siong 24/04/2001
	 *
	 * @param date1 date to compare
	 * @param date2 date to compare
	 * @return true if code>date1</code> equals to <code>date2</code>.
	 */
	public static boolean isDateEqual(java.util.Date date1, java.util.Date date2) {
		return resetTime(date1).compareTo(resetTime(date2)) == 0 ? true : false;
	}

	/**
	 * Format Numeric to specified scale
	 *
	 * @author Rudy Sutjiato 29/03/2001
	 *
	 * @param float floatNum
	 * @param int   scale
	 * @return float value in specified scale
	 */
	public static float formatNumToScale(float floatNum, int scale) {
		return new BigDecimal(Float.toString(floatNum)).setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	/**
	 * Format Numeric to specified scale
	 *
	 * @author Rudy Sutjiato 29/03/2001
	 *
	 * @param double doubleNum
	 * @param int    scale
	 * @return double value in specified scale
	 */
	public static double formatNumToScale(double doubleNum, int scale) {
		return new BigDecimal(Double.toString(doubleNum)).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * remove all spaces in a string
	 *
	 * @author Lim Wee Siong
	 *
	 * @param str input string
	 * @return string with all spaces removed
	 */
	public static String getStringTokens(String str) {
		StringTokenizer st = new StringTokenizer(str);
		StringBuffer sb = new StringBuffer();
		while (st.hasMoreTokens()) {
			sb.append(st.nextToken());
		}

		return sb.toString();
	}

	/**
	 * calculate the duration in years, months and days.
	 * 
	 * @param startDate Start Date of a period.
	 * @param endDate   End date of a period.
	 * @return array of three ints (not Integers). [0]=duration in years,
	 *         [1]=duration in months, [2]=duration in days.
	 * @author Ng Kim Boon
	 */
	public final static int[] computeDuration(Date startDate, Date endDate) {
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.setTime(startDate);
		to.setTime(endDate);

		int birthYYYY = from.get(Calendar.YEAR);
		int birthMM = from.get(Calendar.MONTH);
		int birthDD = from.get(Calendar.DAY_OF_MONTH);

		int asofYYYY = to.get(Calendar.YEAR);
		int asofMM = to.get(Calendar.MONTH);
		int asofDD = to.get(Calendar.DAY_OF_MONTH);

		int ageInYears = asofYYYY - birthYYYY;
		int ageInMonths = asofMM - birthMM;
		int ageInDays = asofDD - birthDD + 1;

		if (ageInDays < 0) {
			// Guaranteed after this single treatment, ageInDays will be >= 0.
			// i.e. ageInDays = asofDD - birthDD + daysInBirthMM.
			ageInDays += from.getActualMaximum(Calendar.DAY_OF_MONTH);
			ageInMonths--;
		}

		if (ageInDays == to.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			ageInDays = 0;
			ageInMonths++;
		}

		if (ageInMonths < 0) {
			ageInMonths += 12;
			ageInYears--;
		}
		if (birthYYYY < 0 && asofYYYY > 0)
			ageInYears--;

		if (ageInYears < 0) {
			ageInYears = 0;
			ageInMonths = 0;
			ageInDays = 0;
		}

		int[] result = new int[3];
		result[0] = ageInYears;
		result[1] = ageInMonths;
		result[2] = ageInDays;
		return result;
	}

	/**
	 * Replace the source string's pattern with other values
	 * 
	 * @param source     the string to be replaced
	 * @param pattern    the pattern string to be located within the string
	 * @param value      the value to replace the string
	 * @param replaceAll true if to replace all instance of the pattern
	 * @return the result string
	 */
	public static String replaceString(String source, String pattern, String value, boolean replaceAll) {
		if (source == null)
			return null;
		if (pattern == null)
			return null;
		int index = source.indexOf(pattern);
		try {
			while (index > -1) {
				String sFront = source.substring(0, index);
				String sEnd = source.substring(index + pattern.length(), source.length());
				source = sFront + value + sEnd;
				index = source.indexOf(pattern);
				if (!replaceAll) {
					break;
				}
			}
		} catch (Exception e) {
			log.error("Exception replaceString : ", e);
		}
		return source;
	}

	/**
	 * Replace the source string's pattern with other values. Note that the pattern
	 * and value size must the same if not a null is return.
	 * 
	 * @param source     the string to be replaced
	 * @param pattern    the patterns
	 * @param value      the value to replace the string
	 * @param replaceAll true if to replace all instance of the pattern
	 * @return the result string
	 */
	public static String replaceString(String source, String[] pattern, String[] value, boolean replaceAll) {
		if (source == null) {
			return null;
		}
		if (pattern == null) {
			return null;
		}
		if (value == null) {
			return null;
		}
		if (pattern.length != value.length) {
			return null;
		}
		for (int i = 0; i < pattern.length; i++) {
			source = replaceString(source, pattern[i], value[i], replaceAll);
		}
		return source;
	}

	/**
	 * Return a sub group of map entries based on key
	 * 
	 * @param groupKey the key to extract the sub group
	 * @param map      the map which contain the sub group
	 * @return a map of the sub entries
	 */
	public static Map getGroupKeyValue(String groupKey, Map map) {
		Map retVal = new HashMap();
		Iterator iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (key.startsWith(groupKey)) {
				String s = "";
				if (key.charAt(groupKey.length()) == '.' || key.charAt(groupKey.length()) == '|'
						|| key.charAt(groupKey.length()) == '/' || key.charAt(groupKey.length()) == '-'
						|| key.charAt(groupKey.length()) == '*' || key.charAt(groupKey.length()) == '#'
						|| key.charAt(groupKey.length()) == '!' || key.charAt(groupKey.length()) == '$') {
					s = key.substring(groupKey.length() + 1, key.length());
				} else {
					s = key.substring(groupKey.length(), key.length());
				}
				retVal.put(s, map.get(key));
			}
		}
		return retVal;
	}

	/**
	 * Parse a long and convert into Hours:Minutes.
	 *
	 * @author Jaisankar R
	 * @date Aug 5, 2003 1:24:01 PM
	 * @param long time in Milliseconds
	 *
	 * @return the string as Hours:Minutes
	 *
	 *
	 *         Eg. The EXIT_DTTM - ARR_DTTM for a Container from Table is in
	 *         MilliSeconds This Difference in arrival and exit is displayed as
	 *         HH:MM i.e. 10:25
	 *
	 */

	public static String convertLongHourToStr(long x) {
		if (x == 0)
			return "hrs mins";
		long inSec = (long) x / 1000;
		long totMin = inSec / 60;
		long totHr = (int) totMin / 60;
		int Min = (int) totMin % 60;
		String retHr = "";
		retHr = totHr + "hrs ";
		if (Min < 10) {
			retHr += "0" + Min;
		} else
			retHr += Min;

		retHr += "mins";
		return retHr;
	}

	/*
	 * public static void main(String args[]){ String s =
	 * "${1}-${3}${2}${4}--${4}-${1}/${1}.jpg"; String param[][] = {{"${1}", "${2}",
	 * "${3}", "${4}"},{"i", "am", "very", "good"}}; s =
	 * CommonUtility.replaceString(s, param[0], param[1], true); log.info
	 * (s); } //
	 */
	/*
	 * public static void main( String[] args ) { try { int[] d = computeDuration(
	 * new java.text.SimpleDateFormat( "dd/MM/yyyy" ).parse( args[0] ), new
	 * java.text.SimpleDateFormat( "dd/MM/yyyy" ).parse( args[1] ) );
	 * 
	 * log.info( "" + d[0] + " y " + d[1] + " m " + d[2] + " d " ); }
	 * catch ( Exception e ) { e.printStackTrace(); } } //
	 */
	/*
	 * public static void main( String[] args ) { try{ Timestamp
	 * tm=Timestamp.valueOf("2002-05-01 01:01:01.100"); log.info(
	 * "parseDateToFmtStr("+tm+")="+parseDateToFmtStr(tm)); log.info(
	 * "formatDateToStr("+tm+"'MM-dd-yy HH:mm:ss')="+formatDateToStr(
	 * tm,"MM-dd-yy HH:mm:ss")); tm = Timestamp.valueOf("2002-09-09 09:09:09.100");
	 * log.info( "parseDateToFmtStr("+tm+")="+parseDateToFmtStr(tm));
	 * log.info(
	 * "formatDateToStr("+tm+"'yyyyMMddyy HH:mm')="+formatDateToStr(
	 * tm,"yyyyMMddyy HH:mm")); tm = Timestamp.valueOf("2002-10-10 10:10:10.100");
	 * log.info( "parseDateToFmtStr("+tm+")="+parseDateToFmtStr(tm));
	 * log.info(
	 * "formatDateToStr("+tm+"'yyy/M/d/y HH:mm:ss')="+formatDateToStr(
	 * tm,"yyy/M/d/y HH:mm:ss")); tm = Timestamp.valueOf("2002-12-31 18:59:59.100");
	 * log.info( "parseDateToFmtStr("+tm+")="+parseDateToFmtStr(tm));
	 * log.info(
	 * "formatDateToStr("+tm+"'yyy/M/d H:m:s')="+formatDateToStr(tm,"yyy/M/d H:m:s")
	 * ); tm = Timestamp.valueOf("2000-12-01 01:00:00.100"); log.info(
	 * "parseDateToFmtStr("+tm+")="+parseDateToFmtStr(tm)); log.info(
	 * "formatDateToStr("+tm+"'M-d-y H:m:s')="+formatDateToStr(tm,"M-d-y H:m:s"));
	 * tm = new Timestamp(Calendar.getInstance().getTime().getTime());
	 * log.info( "parseDateToFmtStr("+tm+")="+parseDateToFmtStr(tm));
	 * log.info(
	 * "formatDateToStr("+tm+"'H:m:s MMMM-d-y')="+formatDateToStr(
	 * tm,"H:m:s MMMM-d-y")); }catch(Exception e){ e.printStackTrace(); } } //
	 */
	/**
	 * remove invalid characters inside an XML
	 * 
	 * @param input a <code>String</code>.
	 * @return the cleaned XML <code>String</code>.
	 * @author yj for SSL-CIM- 0000062 - ITH message receiver 17/Jan/2007
	 */
	public static String cleanXML(String input) {
		// Search for the first occurence of XML tag, to eliminate the extract
		// characters in front
		String modelAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String result = "";
		if (input == null) {
			return null;
		} else if (input.indexOf("RFH") > -1) { // JMS header exists inside
			String tmp = input.toUpperCase();
			int startOfPayloadPos = tmp.indexOf("</JMS>"); // locate end of header
			if (startOfPayloadPos > -1) {
				input = deNull(input.substring(startOfPayloadPos + 1, input.length())).trim();
			}
		}
		Pattern pat = Pattern.compile("<(.)*>", Pattern.DOTALL);
		Matcher matcher = pat.matcher(input);
		boolean found = false;
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			result = input.substring(start, end);
			found = true;
		}
		// default the corrupted message to model answer to avoid upset the processor
		if (!found) {
			result = modelAnswer;
		}
		return result;
	}

	/**
	 * validate a string for XML formation
	 * 
	 * @param input a <code>String</code>.
	 * @return true valid XML false invalid
	 * @author yj for SSL-CIM- 0000062 - ITH message receiver 17/Jan/2007
	 */
//	public static boolean validateXML(String input) {
//		boolean result = true;
//		Document doc = null;
//		try {
//			InputStream is = new ByteArrayInputStream(input.getBytes());
//			InputSource ips = new InputSource(is);
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			factory.setValidating(false);
//			factory.setNamespaceAware(false);
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			doc = builder.parse(ips);
//		} catch (IOException e) {
//			result = false;
//			log.error("IO Exception:" + e);
//		} catch (ParserConfigurationException e) {
//			result = false;
//			log.error("Pasing Configuration Exception:" + e);
//		} catch (SAXException e) {
//			result = false;
//			log.error("Parsing Exception:" + e);
//		}
//		return result;
//
//	}

	

	/**
	 * Convert a string into an array with delimiter
	 * 
	 * @param input a <code>String</code>.
	 * @return String array
	 * @author yj for SSL-CIM- 0000062 - ITH message receiver 17/Jan/2007
	 */
	public static String[] string2StringArray(String input, String delimeter) {
		String[] dummy = { "" };
		if (input != null && input.length() > 0) {
			StringTokenizer tokenizer = new StringTokenizer(input, delimeter);
			ArrayList names = new ArrayList();
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (token != null) {
					token = token.trim();
					if (!token.equals("")) {
						names.add(token);
					}
				}
			}
			return (String[]) names.toArray(new String[0]);
		} else {
			return dummy;
		}
	}

	/*
	 * date formate 20062008 time 2150
	 */

	public static String getLocalTime(String datestr, String timestr) {
		String year = "";
		String date = "";
		String month = "";
		String hh = "";
		String mm = "";
		String sp = "-";
		String sdate = "";
		String startDate = datestr;
		date = startDate.substring(0, 2);
		month = startDate.substring(2, 4);
		year = startDate.substring(4, 8);
		String startTime = "";

		startTime = ("".equals(timestr) || timestr == null) ? "0000" : timestr;
		hh = startTime.substring(0, 2);
		mm = startTime.substring(2, 4);
		sdate = year + sp + month + sp + date + "  " + hh + ":" + mm + ":00";

		return sdate;
	}

	// datestr formate ddMMyyyy
	public static Timestamp toTimestamp(String datestr, String timestr) {
		if (!"".equals(datestr) && null != datestr) {
			String year = "";
			String date = "";
			String month = "";
			String hh = "";
			String mm = "";
			String sp = "-";
			String sdate = "";
			date = datestr.substring(0, 2);
			month = datestr.substring(2, 4);
			year = datestr.substring(4, 8);
			String startTime = "";

			startTime = ("".equals(timestr) || timestr == null) ? "0000" : timestr;
			hh = startTime.substring(0, 2);
			mm = startTime.substring(2, 4);
			sdate = year + sp + month + sp + date + " " + hh + ":" + mm + ":00";// this line is different with
																				// getLocalTime in line 2408
			Timestamp time = Timestamp.valueOf(sdate);
			return time;
		} else {
			return null;
		}

	}

	/**
	 * @param str
	 * @return
	 */
	public static String trimString(String str) {
		return (str != null) ? str.trim() : str;
	}

	public static String avoidStringNull(String s) {
		return (s == null) ? "" : s;
	}

	public static String avoidIntegerNull(Integer i) {
		return (i == null) ? "" : String.valueOf(i);
	}

	public static String avoidLongNull(Long l) {
		return (l == null) ? "" : String.valueOf(l);
	}

	/**
	 * added by jasonxu on 30 Dec 2008
	 *
	 * Convert an integer to an expected string with prefix zero.
	 *
	 * @param src An int sourcec number
	 * @param len the expected return length of string
	 * @return
	 */
	public static String prefixZeroStr(int src, int len) {
		String str = new Integer(src).toString();
		for (int i = 0; i < len; i++) {
			if (str.length() < len)
				str = "0" + str;
		}

		return str;
	}

	/**
	 * added by jasonxu on 30 Dec 2008
	 *
	 * @param dt java.util.Date
	 * @return in format "ddmmyyyy hh24:mi:ss"
	 */
	public static String dateTimeToStr(Date dt) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);

		String dtStr = "";
		dtStr = prefixZeroStr(cal.get(Calendar.DAY_OF_MONTH), 2) + prefixZeroStr(cal.get(Calendar.MONTH) + 1, 2)
				+ prefixZeroStr(cal.get(Calendar.YEAR), 4) + " " + prefixZeroStr(cal.get(Calendar.HOUR_OF_DAY), 2) + ":"
				+ prefixZeroStr(cal.get(Calendar.MINUTE), 2) + ":" + prefixZeroStr(cal.get(Calendar.SECOND), 2);

		return dtStr;
	}

	// Add by Ding Xijia(harbortek) 10-Mar-2011 : START
	public static String escape(String str) {
		if (!str.isEmpty()) {
			return "%" + str.replaceAll("\\\\", "\\\\\\\\").replaceAll("%", "\\\\%").replaceAll("_", "\\\\_") + "%";
		}
		return str;
	}
	// Add by Ding Xijia(harbortek) 10-Mar-2011 : END

	public static String formatTariffRate(String tariffRate) {
		if (tariffRate == null || tariffRate.equals("") || tariffRate.trim().equals("")) {
			tariffRate = "0";
		}

		int scale = 0;

		int decimalPointPosition = tariffRate.indexOf(".");
		if (decimalPointPosition == -1) {
			scale = 2;
		} else {
			scale = tariffRate.length() - (decimalPointPosition + 1);

			if (scale <= 2) {
				scale = 2;
			}
		}

		BigDecimal tariffRateBigDecimal = new BigDecimal(tariffRate);
		BigDecimal formattedBigDecimal = tariffRateBigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP);

		return formattedBigDecimal.toString();
	}
	

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>BIT</code>.
 */
        public final static int BIT             =  -7;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>TINYINT</code>.
 */
        public final static int TINYINT         =  -6;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>SMALLINT</code>.
 */
        public final static int SMALLINT        =   5;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>INTEGER</code>.
 */
        public final static int INTEGER         =   4;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>BIGINT</code>.
 */
        public final static int BIGINT          =  -5;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>FLOAT</code>.
 */
        public final static int FLOAT           =   6;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>REAL</code>.
 */
        public final static int REAL            =   7;


/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>DOUBLE</code>.
 */
        public final static int DOUBLE          =   8;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>NUMERIC</code>.
 */
        public final static int NUMERIC         =   2;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>DECIMAL</code>.
 */
        public final static int DECIMAL         =   3;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>CHAR</code>.
 */
        public final static int CHAR            =   1;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>VARCHAR</code>.
 */
        public final static int VARCHAR         =  12;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>LONGVARCHAR</code>.
 */
        public final static int LONGVARCHAR     =  -1;


/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>DATE</code>.
 */
        public final static int DATE            =  91;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>TIME</code>.
 */
        public final static int TIME            =  92;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>TIMESTAMP</code>.
 */
        public final static int TIMESTAMP       =  93;


/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>BINARY</code>.
 */
        public final static int BINARY          =  -2;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>VARBINARY</code>.
 */
        public final static int VARBINARY       =  -3;

/**
 * <P>The constant in the Java programming language, sometimes referred
 * to as a type code, that identifies the generic SQL type
 * <code>LONGVARBINARY</code>.
 */
        public final static int LONGVARBINARY   =  -4;

/**
 * <P>The constant in the Java programming language
 * that identifies the generic SQL value
 * <code>NULL</code>.
 */
        public final static int NULL            =   0;

    /**
     * The constant in the Java programming language that indicates
     * that the SQL type is database-specific and
     * gets mapped to a Java object that can be accessed via
     * the methods <code>getObject</code> and <code>setObject</code>.
     */
        public final static int OTHER           = 1111;



    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>JAVA_OBJECT</code>.
     * @since 1.2
     */
        public final static int JAVA_OBJECT         = 2000;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>DISTINCT</code>.
     * @since 1.2
     */
        public final static int DISTINCT            = 2001;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>STRUCT</code>.
     * @since 1.2
     */
        public final static int STRUCT              = 2002;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>ARRAY</code>.
     * @since 1.2
     */
        public final static int ARRAY               = 2003;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>BLOB</code>.
     * @since 1.2
     */
        public final static int BLOB                = 2004;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>CLOB</code>.
     * @since 1.2
     */
        public final static int CLOB                = 2005;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>REF</code>.
     * @since 1.2
     */
        public final static int REF                 = 2006;

    /**
     * The constant in the Java programming language, somtimes referred to
     * as a type code, that identifies the generic SQL type <code>DATALINK</code>.
     *
     * @since 1.4
     */
    public final static int DATALINK = 70;

    /**
     * The constant in the Java programming language, somtimes referred to
     * as a type code, that identifies the generic SQL type <code>BOOLEAN</code>.
     *
     * @since 1.4
     */
    public final static int BOOLEAN = 16;

    //------------------------- JDBC 4.0 -----------------------------------

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>ROWID</code>
     *
     * @since 1.6
     *
     */
    public final static int ROWID = -8;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>NCHAR</code>
     *
     * @since 1.6
     */
    public static final int NCHAR = -15;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>NVARCHAR</code>.
     *
     * @since 1.6
     */
    public static final int NVARCHAR = -9;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>LONGNVARCHAR</code>.
     *
     * @since 1.6
     */
    public static final int LONGNVARCHAR = -16;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>NCLOB</code>.
     *
     * @since 1.6
     */
    public static final int NCLOB = 2011;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>XML</code>.
     *
     * @since 1.6
     */
    public static final int SQLXML = 2009;

    //--------------------------JDBC 4.2 -----------------------------

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type {@code REF CURSOR}.
     *
     * @since 1.8
     */
    public static final int REF_CURSOR = 2012;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * {@code TIME WITH TIMEZONE}.
     *
     * @since 1.8
     */
    public static final int TIME_WITH_TIMEZONE = 2013;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * {@code TIMESTAMP WITH TIMEZONE}.
     *
     * @since 1.8
     */
    public static final int TIMESTAMP_WITH_TIMEZONE = 2014;

    //sg.com.jp.dpe.util -->DpeCommonUtil -->generateRandomNumber()
    /**
	 * Generate a random number for the last digits of ASN.
	 *
	 * @author HaiVM
	 * @param maxlength Length of random digit
	 * @return a random number
	 */
	public static String generateRandomNumber(int length, boolean event) {
		String result;
		Random rnd = new Random();
		int upperNbr;
		if (length == 3) {
			// Bulk GB EDO & Bulk ESN case
			upperNbr = 1000;
		} else {
			upperNbr = 100000;
		}
		int rndNumber = 0;
		if (event) {
			//For Esn, Tesn
			rndNumber = rnd.nextInt(upperNbr/2);
			rndNumber = rndNumber * 2;
		} else {
			//For EDO
			rndNumber = rnd.nextInt((upperNbr - 1) / 2 - 1 /2);
			rndNumber = rndNumber * 2 + 1;
		}
		int nbrLength = String.valueOf(rndNumber).length();
		switch (nbrLength) {
		case 1:
			result = "0000" + rndNumber;
			break;
		case 2:
			result = "000" + rndNumber;
			break;
		case 3:
			result = "00" + rndNumber;
			break;
		case 4:
			result = "0" + rndNumber;
			break;
		case 5:
			result = "" + rndNumber;
			break;
		default:
			result = "00000";
			break;
		}
		return result.substring(result.length() - length, result.length());
	}
	
	public static String addApostr(String s) {
		if (s != null && !s.equals("")) {
			boolean flag = false;
			String s1 = new String(s);
			StringBuffer stringbuffer;
			for (int i = 0; i < s1.length(); s1 = stringbuffer.toString()) {
				stringbuffer = new StringBuffer(s1);
				if (s1.charAt(i) == '\'') {
					stringbuffer.insert(i, "'");
					i += 2;
				} else {
					i++;
				}
			}
			return s1;
		} else {
			return "";
		}
	}
	
	public static Boolean containSpecialCharacter(String input) {
		boolean containSpecialChar = false;
		Pattern regexPattern = Pattern.compile("[\\W]"); // \\W check for matches the non-word characters
	     Matcher matcher = regexPattern.matcher(input);
	     boolean flag = matcher.find();

	     

	     if (flag) {
	    	 // String contains special character
		     String allowedSpecialCharList = "[0-9a-zA-Z"+listOfAllowedSpecialChar+"]";
		     Pattern allowedPattern = Pattern.compile(allowedSpecialCharList);
		     Matcher allowedMatcher = allowedPattern.matcher(input);
		     boolean allowedFlag = allowedMatcher.find();
		     
		     if(allowedFlag) {
		    	 for(int i=0;i<input.length();i++) {
			    	 CharSequence seq = new StringBuilder(1).append(input.charAt(i));
			    	 Matcher checkAllowedMatcher = allowedPattern.matcher(seq);
				     boolean checkAllowedFlag = checkAllowedMatcher.find();
			    	 if(!checkAllowedFlag) {
			    		 containSpecialChar = true;
			    		 break;
			    	 }
			     }  
		     }else {
		    	 containSpecialChar = true;
		     }
 
	     }

		return containSpecialChar;
	}
} // public class ISMSUtil
