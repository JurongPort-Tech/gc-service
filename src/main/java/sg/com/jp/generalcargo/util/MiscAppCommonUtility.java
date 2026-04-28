package sg.com.jp.generalcargo.util;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sg.com.jp.generalcargo.dao.TextParaRepository;
import sg.com.jp.generalcargo.domain.TextParaVO;

@Component
public class MiscAppCommonUtility {

	private static final Log log = LogFactory.getLog(MiscAppCommonUtility.class);
	
	
	private static TextParaRepository textParaRepo;
	
	@Autowired
	private  TextParaRepository textParRepo;
	
	
	
	/**
	 * Return String that represent date according format: DDMMYYYY
	 * @param date
	 * @return String that represent date
	 */
	public static String formatDate(Date date) {
		if(date == null) {
			return null;
		}
		Format formater = new SimpleDateFormat("ddMMyyyy");
		 return formater.format(date);
	}
	
	
	/**
	 * Return String that represent date according format: HHMM
	 * @param time 
	 * @return String that represent date
	 */
	public static String formatTime(Date time) {
		if(time == null) {
			return null;
		}
		Format formater = new SimpleDateFormat("HHmm");
		 return formater.format(time);
	}
	/**
	 * Return Date from String that according format DDMMYYYY
	 * @param dateString
	 * @return
	 */
	public static Date parseDate(String dateString) {
		dateString = StringUtils.trimToNull(dateString);
		if(dateString == null) {
			return null;
		}
		DateFormat formatter = new SimpleDateFormat("ddMMyyyy");
		try {
			return formatter.parse(dateString);
		} catch (ParseException pe) {
			log.error(pe);
			return null;
		}	
	}
	
	/**
	 * Return Date from String that according format HHMM
	 * @param timeString
	 * @return
	 */
	public static Date parseTime(String timeString) {
		timeString = StringUtils.trimToNull(timeString);
		if(timeString == null) {
			return null;
		}
		DateFormat formatter = new SimpleDateFormat("HHmm");
		try {
			return formatter.parse(timeString);
		} catch (ParseException pe) {
			log.error(pe);
			return null;
		}	
	}
	
	/**
	 * Return Calendar that represent date and time from two Strings
	 * @param timeString
	 * @param dateString
	 * @return
	 */
	public Calendar parseDateTime(String dateString, String timeString) {
		Date date = parseDate(dateString);
		Date time = parseTime(timeString);
		if(date == null || time == null) {
			return null;
		}
		Calendar timeCalen = GregorianCalendar.getInstance();
		timeCalen.setTime(time);
		
		Calendar calen = GregorianCalendar.getInstance();
		calen.setTime(date);
		calen.set(Calendar.HOUR_OF_DAY, timeCalen.get(Calendar.HOUR_OF_DAY));
		calen.set(Calendar.MINUTE, timeCalen.get(Calendar.MINUTE));
		calen.set(Calendar.SECOND, 0);
		calen.set(Calendar.MILLISECOND, 0);
		return calen;
		
	}
	/**
	 * Get current date string that respect following format DDMMYYYY
	 * @return
	 */
	public static String getCurrentDateString() {
		Calendar currentCalen = GregorianCalendar.getInstance();
		return formatDate(currentCalen.getTime());
	}
	
	/**
	 * Get date that after <code>after</code> day, the return value respect following format DDMMYYYY
	 * @param after
	 * @return
	 */
	public static String getAfterCurrentDateString(int after) {
		Calendar currentCalen = GregorianCalendar.getInstance();
		currentCalen.add(Calendar.DAY_OF_MONTH, after);
		return formatDate(currentCalen.getTime());
	}
	//start updated by thanhbtl6b on 27/09/13 for TPA Enhancement
	
//	public final static String[] TIME_LIST = { "0000", "0030", "0100", "0130",
//			"0200", "0230", "0300", "0330", "0400", "0430", "0500", "0530",
//			"0600", "0630", "0700", "0730", "0800", "0830", "0900", "0930",
//			"1000", "1030", "1100", "1130", "1200", "1230", "1300", "1330",
//			"1400", "1430", "1500", "1530", "1600", "1630", "1700", "1730",
//			"1800", "1830", "1900", "1930", "2000", "2030", "2100", "2130",
//			"2200", "2230", "2300", "2330" };
	
	

//	public final static String[] COLUMN_LABEL = { "0000-0030", "0030-0100",
//			"0100-0130", "0130-0200", "0200-0230", "0230-0300", "0300-0330",
//			"0330-0400", "0400-0430", "0430-0500", "0500-0530", "0530-0600",
//			"0600-0630", "0630-0700", "0700-0730", "0730-0800", "0800-0830",
//			"0830-0900", "0900-0930", "0930-1000", "1000-1030", "1030-1100",
//			"1100-1130", "1130-1200", "1200-1230", "1230-1300", "1300-1330",
//			"1330-1400", "1400-1430", "1430-1500", "1500-1530", "1530-1600",
//			"1600-1630", "1630-1700", "1700-1730", "1730-1800", "1800-1830",
//			"1830-1900", "1900-1930", "1930-2000", "2000-2030", "2030-2100",
//			"2100-2130", "2130-2200", "2200-2230", "2230-2300", "2300-2330",
//			"2330-0000" };
//	public static final long MILISECONDS_IN_12_HOURS = 12*60*60*1000;
	public static final long MILISECONDS_IN_1_HOUR = 60*60*1000;
	
	//END  updated by thanhbtl6b on 27/09/13 for TPA Enhancement
	
	public Date parseStrToDate(String dateStr, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setLenient(false);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			return date;
		}
		return date;
	}
	
	  public String parseDateToStr( Date date, String strFormat)
	  {
	    SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
	    String strDate = null;
	    strDate = dateFormat.format(date);
	    return strDate;

	  }
	  /**
	   * Calculate available column label base on start date and end date
	   * @param startDate
	   * @param endDate
	   * @return
	   */
	  public String[] calculateAvailableColLabel( String startDate,String endDate)
	  {
		 String strBreak = "<BR />";
		 Calendar startDateTime = parseDateTime(startDate, "0000");
		 Calendar beforeStartDateTime = GregorianCalendar.getInstance();	
		 int avaiableCol = calculateAvailableCol(startDate,endDate);	
		
		 int startDay = startDateTime.get(Calendar.DAY_OF_MONTH);
		 int startMonth = startDateTime.get(Calendar.MONTH);
		 String[] blockTime = getBlockTime();
		 String[] timeList = createTimeList(blockTime);
		 int hourPerBlock = getHourPerBlock();		
	 
		 String[] avaiableColLabel = new String[avaiableCol];		
		 
		 for ( int i = 0; i < avaiableCol; i++){
			 startDay = startDateTime.get(Calendar.DAY_OF_MONTH);
			 startMonth = startDateTime.get(Calendar.MONTH);
			 startMonth = startMonth + 1;
			 avaiableColLabel[i] = formatMonthOrDay(startDay) + "/" + formatMonthOrDay(startMonth) + " " +  formatMonthOrDay(startDay) + "/" + formatMonthOrDay(startMonth) + strBreak +  timeList[i] + "-" + timeList[i+1];			 
			
			 if( !( (Integer.parseInt( getHourFromTime(timeList[i]) ) + hourPerBlock ) < 24 ) ){
				 startDateTime.add(Calendar.DATE, 1);
				 startDay = startDateTime.get(Calendar.DAY_OF_MONTH);
				 startMonth = startDateTime.get(Calendar.MONTH);
				 startMonth = startMonth + 1;
				 beforeStartDateTime.setTime(startDateTime.getTime());
				 beforeStartDateTime.add(Calendar.DATE, -1);				
				 avaiableColLabel[i] = formatMonthOrDay(beforeStartDateTime.get(Calendar.DAY_OF_MONTH)) + "/" + formatMonthOrDay((beforeStartDateTime.get(Calendar.MONTH) + 1)) + " "  + formatMonthOrDay(startDay) + "/" + formatMonthOrDay(startMonth) + strBreak + timeList[i] + "-" + timeList[i+1];
			 }			 
		 }		 
	     return avaiableColLabel;
	    	
		  
	  }  
	  
	  @PostConstruct
	  public void init() {
		  textParaRepo = this.textParRepo;
	  }
	  
	  
	  /**
	   * Calculate available column base on start date and end date
	   * @param startDate
	   * @param endDate
	   * @return
	   */
	  public int getHourPerBlock()
	  {
		
		 TextParaVO textParaVO = new TextParaVO();
		 textParaVO.setParaCode("TPA_BLK");
		 int hourPerBlock = 0;
		 try{
			 
			 textParaVO = textParaRepo.getParaCodeInfo(textParaVO);			
	         if(textParaVO != null){
	        	 hourPerBlock = Integer.parseInt(textParaVO.getValue()); 
	         }			
		 }catch (Exception ex){
			 return 0;
		 }		
		 			 
	     return hourPerBlock ;
	  }  
	  
	  
	    
	  
	  
	  /**
	   * Calculate available column base on start date and end date
	   * @param startDate
	   * @param endDate
	   * @return
	   */
	  public int getNumberOfDays()
	  {
		
		 TextParaVO textParaVO = new TextParaVO();
		 textParaVO.setParaCode("TPA_ENQ_RN");
		 int numberOfDays = 0;
		 try{		
		
			 textParaVO = textParaRepo.getParaCodeInfo(textParaVO);			
	         if(textParaVO != null){
	        	 numberOfDays = Integer.parseInt(textParaVO.getValue()); 
	         }		 
		 }catch (Exception ex){
			 return 0;
		 }		 			 
	     return numberOfDays ;
	  }  
	  /**
	   * Calculate available column base on start date and end date
	   * @param startDate
	   * @param endDate
	   * @return
	   */
	  public int calculateAvailableCol( String startDate,String endDate)
	  {
		 Calendar startDateTime = parseDateTime(startDate, "0000");				
		 Calendar endDateTime = parseDateTime(endDate, "2359");	
		 // MiscCodeValueObject miscValue = null;
		 long avaiableCol = 0;
		 avaiableCol = (endDateTime.getTimeInMillis() - startDateTime.getTimeInMillis())/(getHourPerBlock() * MILISECONDS_IN_1_HOUR);
	    
		 			 
	     return (int)avaiableCol ;
	  }  
	 
	  /**
	   * Get block time list which was defined in misc type code
	   * @return
	   */
	  public String[] getBlockTime()
	  {
		 String[] blockTime = null;
		 TextParaVO textParaVO = new TextParaVO();
		 textParaVO.setParaCode("TPA_FRTM");
		 try{
			 
			 textParaVO = textParaRepo.getParaCodeInfo(textParaVO);			
	         if(textParaVO != null){
	        	 blockTime = textParaVO.getValue().split(","); 
	         }
		 }catch (Exception ex){
			 return null;
		 }		 
	     return blockTime;
	  } 
	  
	  /**
	   * Create time list base on block time list for 3 days.
	   * @param blockTime
	   * @return
	   */
	  public  String[] createTimeList(String[] blockTime)
	  {
		 String[] timeList = null;
		 int numberOfDays = getNumberOfDays() + 1;		 
		 if(blockTime != null && blockTime.length > 0){
			 int blockTimeCnt = blockTime.length;
			 int cnt = 0;
			 timeList = new String[blockTimeCnt*numberOfDays];
			 for(int j = 0; j < numberOfDays; j++){
				 for(int i = 0; i < blockTime.length; i++){
					 timeList[cnt]=blockTime[i].replace(":", "");				 								 
					 cnt++;					 
				 }
			 }
		 }
	     return timeList;
	  } 
	  public static String formatMonthOrDay(int monthOrDay)
	  {	
	     return (monthOrDay < 10)?"0" + String.valueOf(monthOrDay):String.valueOf(monthOrDay) ;
	  }
	  
	  public static String getHourFromTime(String time)
	  {	
	     return (time.startsWith("0"))?time.substring(1, 2) : time.substring(0,2) ;
	  }
	  
	  
	  
}
