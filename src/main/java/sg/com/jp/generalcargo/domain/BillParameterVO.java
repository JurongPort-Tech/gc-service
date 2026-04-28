package sg.com.jp.generalcargo.domain;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sg.com.jp.generalcargo.util.ProcessChargeConst;

public class BillParameterVO extends UserTimestampVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(BillParameterVO.class);
	// member variable
    public BillParameterVO() {
    }
	public void doGet(Object object) {}
	public void doSet(Object object) {}

	private static StringBuffer parseString;
	private static DecimalFormat decFmt;
	private static SimpleDateFormat dateFmt;
	private static FieldPosition fpos;
	private static SimpleDateFormat timeFmt;

	static{
		parseString = new StringBuffer();
		decFmt = new DecimalFormat();
		decFmt.setMaximumFractionDigits(3);
		decFmt.setMinimumFractionDigits(2);
		decFmt.setGroupingSize(3);
		dateFmt = new SimpleDateFormat("dd/MM/yyyy");
		timeFmt = new SimpleDateFormat("HH:mm");
		fpos = new FieldPosition(NumberFormat.FRACTION_FIELD);
	}

	public static String parseMoney(String str){
		double a = 0.0;
		try{
			a = Double.parseDouble(str);
		}catch(Exception e){
		}
		return parseMoney(a);
	}

	public static String parseMoney(double value){
		//add by hujun on 17/10/2011
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(4);
		nf.setMinimumFractionDigits(2);
		return nf.format(value);
		//add end
		/*
		parseString.setLength(0);
		try{
			decFmt.format(value, parseString, fpos);
		}catch(Exception e){
			parseString.append(value);
		}
		return parseString.toString();
		*/
	}
	
	public static String parseDate(java.sql.Date value){
		if (value == null){ return ""; }
		String s;
		java.util.Date dt = new java.util.Date(value.getTime());
		try{
			s = dateFmt.format(dt);
		}catch(Exception e){
			s = value.toString();
		}
		return s;
	}

	public static String parseDate(java.sql.Timestamp value){
		if (value == null){ return ""; }
		String s;
		java.util.Date dt = new java.util.Date(value.getTime());
		try{
			s = dateFmt.format(dt);
		}catch(Exception e){
			s = value.toString();
		}
		return s;
	}

	public static String parseTime(java.sql.Timestamp value) {
		if (value == null){ return ""; }
		String s;
		java.util.Date dt = new java.util.Date(value.getTime());
		try{
			s = timeFmt.format(dt);
		}catch(Exception e){
			s = value.toString();
		}
		return s;
	}

	public static String parseUnit(	String mainCat, 
									String subCat,
									int tier){
		String s = "";
		if (mainCat == null || subCat == null){
			return s;
		}
		
		if (mainCat.equals(ProcessChargeConst.TARIFF_MAIN_ADMIN)){
			if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_BERTH_APPL)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_CHANGES_AFTER_CLOSING)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_CNTR_DETAILS_AMEND)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_DELIVERY_ORDER)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_DO_CANCEL)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_IMPORT_STATUS)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_PIPELINE_WATER)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_PREGATE_CANCEL)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_REEXPORT_STATUS)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_SEAL_AMEND)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_SHIPPING_NOTE)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_SUBMIT_BAYPLAN)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_WATER_REQUEST)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_ADMIN_WEIGHT_WRONG)){
			}
		}else if (mainCat.equals(ProcessChargeConst.TARIFF_MAIN_CLOSING_TIME_PENALTY)){
		}else if (mainCat.equals(ProcessChargeConst.TARIFF_MAIN_LOLO)){
			s = " /CONT";
		}else if (mainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE)){
			if (subCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE) ||
				subCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_SPECIAL_DOCKAGE)){
				if (tier == 1){
					s = " /100m/hr";
				}else if (tier == 2){
					s = " /m/hr";
				}else{
					s = " /m/hr";
				}
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY)){
				s = " /m/15min";
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_BERTH_UNBERTH) ||
				subCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_BERTH_APPL_NO_QUAYCRANE) ||
				subCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_BERTH_APPL_QUAYCRANE)){
				s = "";
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_LATE_ARRIVAL)){
				s = "min";
			}
		}else if (mainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_FREQUENCY)){
			if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_CHANGE_STATUS)){
				s = " /CONT";
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_ELEC_SUPPLY)){
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_EXTRA_MVT)){
				s = " /CONT";
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_ITH)){
				s = " /CONT";
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_ITH_LOLO)){
				s = " /CONT";
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_REEFER_PRETRIP)){
				s = " /CONT";
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_RENOM)){
				s = " /CONT";
			}
		}else if (mainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_VESSEL)){
			if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_VSL_DIRECT_LOADING)){
				s = " /CONT";
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_VSL_SHUTOUT)){
				s = " /CONT";
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_VSL_STANDBY_LABOUR)){
				if (tier == 1){
					s = " /15min";
				}else{
					s = " /5min";
				}
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_VSL_STANDBY_QUAY_CRANE)){
				if (tier == 1){
					s = " /15min";
				}else{
					s = " /5min";
				}
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_VSL_STOWAGE_AMEND_CNTR)){
				s = " /CONT";
			}else if (subCat.equals(ProcessChargeConst.TARIFF_SUB_OTHERS_VSL_STOWAGE_AMEND_REQUEST)){
				s = "";
			}
		}else if (mainCat.equals(ProcessChargeConst.TARIFF_MAIN_STEVEDORAGE)){
			if (subCat.equals(ProcessChargeConst.TARIFF_SUB_STO_GENERAL) ||
				subCat.equals(ProcessChargeConst.TARIFF_SUB_STE_SHIFT) ||
				subCat.equals(ProcessChargeConst.TARIFF_SUB_STE_LASH_CELL) ||
				subCat.equals(ProcessChargeConst.TARIFF_SUB_STE_LASH_NON) ||
				subCat.equals(ProcessChargeConst.TARIFF_SUB_STE_REEFER)){
				s = " /CONT";
			}else{
				s = " /CONT";
			}
		}else if (mainCat.equals(ProcessChargeConst.TARIFF_MAIN_STORE_RENT)){
			// one sub cat only
			s = " /CONT/DAYS";
		}
		return s;
	}

	private final int LOG_INFO = 1;
	private final int LOG_TXN = 2;
	private final int LOG_WARN = 3;
	private final int LOG_DEBUG = 4;
	private final int LOG_ERROR = 5;
	private final int LOG_FATAL = 6;
	private boolean showMsg = false;

	/**
	 * Logs the message to the log file.
	 */
	private void log(String s, int type){
		String msg = "BPVO: " + s;
		if (showMsg){
			log.info(msg);
		}else{
			switch(type){
				case LOG_INFO:
					log.info(msg);
					break;
				case LOG_ERROR:
					log.info(msg);
					break;
				case LOG_DEBUG:
					log.info(msg);
					break;
				case LOG_FATAL:
					log.info(msg);
					break;
				case LOG_TXN:
					log.info(msg);
					break;
				case LOG_WARN:
					log.info(msg);
					break;
			}
		}
	}

	/**
	 * Reset the state.
	 */
	public void reset(){
	}
}

	
