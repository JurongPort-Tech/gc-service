package sg.com.jp.generalcargo.dao.impl;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.ContainerCommonFunctionRepo;
import sg.com.jp.generalcargo.dao.ContainerDataRepo;
import sg.com.jp.generalcargo.dao.GeneralCargoDnRepository;
import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.domain.EdoVO;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.domain.GcOpsDnReport;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("DnJdbcRepository")
public class GeneralCargoDnJdbcRepository implements GeneralCargoDnRepository {
	private static final Log log = LogFactory.getLog(GeneralCargoDnJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private ContainerCommonFunctionRepo containerCommonFunctionRepo;

	@Autowired
	private ContainerDataRepo containerDataRepo;

	// ejb.sessionBeans.gbms.ops.dnua.dn--->dnBean
	public java.lang.String logStatusGlobal = "Y";

	public static Date getNextDayStart(Date date) throws BusinessException {
		Calendar cal = Calendar.getInstance();
		Date time = new Date();
		try {
			log.info("START: getNextDayStart date:" + date);

			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.DATE, 1);

			time = cal.getTime();
		} catch (Exception e) {
			log.error("Exception getNextDayStart :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getNextDayStart  DAO  Result:" + time.toString());
		}
		return time;
	}

	// ejb.sessionBeans.gbms.ops.dnua.dn--->dnBean
	// StartRegion updateVehicleNo
	@Override
	public void updateVehicleNo(String dnNo, String vehicleNo) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		int update = 0;
		try {
			log.info("START: updateVehicleNo dnNo:" + dnNo + "vehicleNo:" + vehicleNo);

			sb.append("UPDATE DN_DETAILS SET TRUCK_NBR=:vehicleNo WHERE DN_NBR =:dnNo");

			paramMap.put("vehicleNo", vehicleNo);
			paramMap.put("dnNo", dnNo);
			log.info("SQL" + sb.toString());
			update = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (NullPointerException e) {
			log.error("Exception updateVehicleNo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception updateVehicleNo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateVehicleNo  DAO  Result:" + update);
		}
	}

	public String getDummyNumber() throws BusinessException {
		String dummyNumber = "";
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getDummyNumber");

			sb.append("SELECT value FROM text_para WHERE para_cd = 'DUM_VEHNBR'");

			log.info("SQL" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				dummyNumber = rs.getString("VALUE");
			}
		} catch (NullPointerException e) {
			log.error("Exception getDummyNumber :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getDummyNumber :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDummyNumber  DAO  Result:" + dummyNumber);
		}
		return dummyNumber;
	}

	@Override
	public boolean isValidVehicleNumber(String vehicleNumber, String companyCode) throws BusinessException {
		SqlRowSet rs = null;
		boolean isValid = true;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: isValidVehicleNumber vehicleNumber:" + vehicleNumber + "companyCode:" + companyCode);

			sb.append("SELECT COUNT(*) FROM gss_veh_info WHERE veh_nbr =:vehicleNumber and status_cd ='A'");
			String dummyNumber = getDummyNumber();
			if (!(dummyNumber.equals(vehicleNumber) && "JP".equals(companyCode))) {

				paramMap.put("vehicleNumber", vehicleNumber);
				log.info("SQL" + sb.toString() + ",paramMap:" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

				rs.next();
				int res = rs.getInt(1);
				
				if (res == 0) {
					isValid = false;
				}
			}
		} catch (NullPointerException e) {
			log.error("Exception isValidVehicleNumber :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception isValidVehicleNumber :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isValidVehicleNumber  DAO  Result:" + isValid);
		}
		return isValid;
	}

	@Override
	public String insertTempDNPrintOut(String strEdoNo, String DNNbr, String transtype, String searchcrg,
			String esnasnnbr) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		int countua = 0;
		List<EdoVO> temptablevect = new ArrayList<EdoVO>();
		List<EdoVO> temptablevect_vech = new ArrayList<EdoVO>();
		Map<String, String> paramMap = new HashMap<String, String>();

		String esnno = "";
		String vslnm = "";
		String atb = "";
		String outvoy = "";
		String bkref = "";
		String dcpkgs = "";
		String wt = "";
		String vol = "";
		String conttype = "";
		String contno = "";
		String crgdesc = "";
		String markings = "";
		String contsize = "";
		String cod = "";
		String balpkgs = "";
		String date_time = "";
		String transQty = "";
		String nric_no = "";
		String ictype = "";
		String veh1 = "";
		String veh2 = "";
		String veh3 = "";
		String veh4 = "";
		String veh5 = "";
		String ttype = "";
		String dateval = "";
		String vv_cd = "";

		try {
			log.info("START: insertTempDNPrintOut  DAO  Start strEdoNo " + strEdoNo + " DNNbr" + DNNbr + " transtype"
					+ transtype + " searchcrg " + searchcrg + " esnasnnbr" + esnasnnbr);

			temptablevect = fetchShutoutDNDetail(strEdoNo, DNNbr);
			if (temptablevect == null || temptablevect.size() == 0) {
				temptablevect = fetchDNDetail(strEdoNo, DNNbr, transtype, searchcrg, esnasnnbr);
			}
			// amended end
			temptablevect_vech = getVechDetails(DNNbr);
			for (int j = 0; j < temptablevect_vech.size(); j++) {
				EdoVO edoVo = new EdoVO();
				edoVo = (EdoVO) temptablevect_vech.get(j);
				veh1 = edoVo.getVech1();
				veh2 = edoVo.getVech2();
				veh3 = edoVo.getVech3();
				veh4 = edoVo.getVech4();
				veh5 = edoVo.getVech5();
			}

			for (int i = 0; i < temptablevect.size(); i++) {
				EdoVO edoVo = new EdoVO();
				edoVo = (EdoVO) temptablevect.get(i);
				esnno = edoVo.getEdoAsnNbr();
				vslnm = edoVo.getVslName();
				outvoy = edoVo.getInVoyNbr();
				contno = edoVo.getCntrNo();
				// Added by SONLT
				// Check ESN whether it link with CNTR or not
				if (checkESNCntr(strEdoNo)) {
					// Get cntrNbr of this DN
					contno = getCntrNo(DNNbr);
				}
				contno = CommonUtility.deNull(contno);
				// END SONLT
				conttype = edoVo.getCntrType();
				contsize = edoVo.getCntrSize();
				bkref = edoVo.getBlNbr();
				dcpkgs = edoVo.getNoPkgs();
				wt = edoVo.getNomWt();
				vol = edoVo.getNomVol();
				crgdesc = edoVo.getCrgDes();
				markings = edoVo.getMarkings();
				balpkgs = edoVo.getBalance();
				cod = edoVo.getCOD();
				date_time = edoVo.getTransDate();
				transQty = edoVo.getDeliveredPkgs();
				nric_no = edoVo.getAAIcNbr();
				ictype = edoVo.getAACustCD();
				ttype = edoVo.getTransType();
				atb = edoVo.getATB();
				dateval = atb;
				// add by hujun on 26/12/2011
				if (dateval == null) {
					dateval = getVesselATBDate(edoVo.getEdoAsnNbr());
				}
				// add end
				if (ttype.equals("L"))
					ttype = "L";
				else
					ttype = "T";
				if (cod == null || cod.equals("") || cod.equals("null"))
					cod = "";
				String sql_vvcd = "select v.scheme, g.var_nbr, g.ta_cust_cd, g.ta_name from vessel_call v, gb_edo g where g.var_nbr = v.vv_cd and g.edo_asn_nbr = :esnno ";

				paramMap.put("esnno", esnno);

				log.info("insertTempDNPrintOut  DAO  SQL " + sql_vvcd.toString() + " ,paramMap:" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql_vvcd.toString(), paramMap);

				String taCCode = "";
				String taNmByJP = "";
				String scheme = "";
				if (rs.next()) {
					vv_cd = rs.getString("var_nbr");
					taCCode = rs.getString("ta_cust_cd");
					taNmByJP = rs.getString("ta_name");
					scheme = rs.getString("scheme");
				}

				String ab_cd = "Delivery Note must be endorsed by ";
				if (StringUtils.isBlank(taNmByJP) && StringUtils.isBlank(taCCode)) {
					if (StringUtils.isBlank(scheme)) {
						ab_cd = "";
					} else {
						ab_cd += scheme;
					}
				} else if (StringUtils.isNotBlank(taNmByJP)) {
					ab_cd += taNmByJP;
				} else {
					ab_cd += taCCode;
				}
				// End, THANHPT6, JCMS, 06/01/2016
				// added by vani start -- 27th Oct,03
				// for ab_cd
				// String sql_abcd = "SELECT DECODE(vessel_call.mixed_scheme_ind,'Y'," +
				// "(SELECT DECODE(ab_cd,NULL,''," +
				// "'Delivery Note must be endorsed BY AB Operator : '
				// ||vessel_scheme.scheme_cd) " +
				// "FROM vessel_scheme WHERE acct_nbr = " +
				// "(SELECT mixed_scheme_acct_nbr FROM manifest_details " +
				// // Added by satish on 18 Mar 2004 :Retrive Active Bl for Printing of
				// DN.:SL-GBMS-20040306-1
				// //"WHERE var_nbr='" + vv_cd + "' AND bl_nbr=" +
				// "WHERE var_nbr='" + vv_cd + "' and bl_status='A' AND bl_nbr=" +
				// "(SELECT bl_nbr FROM gb_edo WHERE edo_asn_nbr='" + esnno + "' AND " +
				// "var_nbr='" + vv_cd + "')))," +
				// "(SELECT AB_CD FROM VESSELSCHEME WHERE VV_CD='" + vv_cd +
				// "')) ab_cd " +
				// "FROM vessel_call WHERE vv_cd='" + vv_cd + "'";
				// Commended by THANHPT6 FOR JCMS 06/01/2016
				// rs1 = stmt.executeQuery(sql_abcd);
				// String ab_cd = "";
				// if (rs1.next())
				// ab_cd = CommonUtility.deNull(rs1.getString("ab_cd"));
				// rs1.close();

				// log.info("13131313 abcd: " + ab_cd);
				// added by vani end -- 27th Oct,03
				StringBuffer sb = new StringBuffer();

				sb.append("Insert into webdnuatemp(DateTime,TransRefno,ATB,COD");
				sb.append(",Vslnm,voyno,contno,transtype,contsize,conttype,asnno,");
				sb.append("crgref,wt,vol,declqty,transqty,balqty,nricpassportno,");
				sb.append("marking,crg_desc,veh1,veh2,veh3,veh4,veh5,vv_cd,AB_CD)");
				sb.append("values(to_date(:date_time,'DD/MM/YYYY HH24:MI'),");
				sb.append(":DNNbr,to_date(:dateval,'DD/MM/YYYY HH24:MI'),");
				sb.append("to_date(:cod,'DD/MM/YYYY HH24:MI'),:vslnm,");
				sb.append(":outvoy,:contno,:ttype,:contsize,:conttype,:esnno,");
				sb.append(":bkref,:wt ,:vol,:dcpkgs,:transQty,:balpkgs,:nric_no,:markings,:crgdesc,:veh1,:veh2,");
				sb.append(":veh3,:veh4,:veh5,:vv_cd,:ab_cd)");

				sql = sb.toString();
				// remove usage for addApostr to proper get the value - NS Oct 2023
				paramMap.put("veh5", (veh5));
				paramMap.put("veh4", (veh4));
				paramMap.put("veh3", (veh3));
				paramMap.put("veh2", (veh2));
				paramMap.put("veh1", (veh1));
				paramMap.put("crgdesc", (crgdesc));
				paramMap.put("markings", (markings));
				paramMap.put("vv_cd", vv_cd);
				paramMap.put("ab_cd", ab_cd);
				paramMap.put("nric_no", ictype + nric_no);
				paramMap.put("balpkgs", balpkgs);
				paramMap.put("transQty", transQty);
				paramMap.put("dcpkgs", dcpkgs);
				paramMap.put("vol", vol);
				paramMap.put("wt", wt);
				paramMap.put("date_time", date_time);
				paramMap.put("DNNbr", DNNbr);
				paramMap.put("dateval", dateval);
				paramMap.put("cod", cod);
				paramMap.put("vslnm", (vslnm));
				paramMap.put("outvoy", (outvoy));
				paramMap.put("contno", (contno));
				paramMap.put("contsize", contsize);
				paramMap.put("ttype", ttype);
				paramMap.put("contsize", contsize);
				paramMap.put("conttype", conttype);
				paramMap.put("esnno", esnno);
				paramMap.put("bkref", (bkref));

				log.info(" insertTempDNPrintOut  DAO  SQL " + sql.toString());
				countua = countua + namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			}

			if (countua == 0) {
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
		} catch (NullPointerException e) {
			log.error("Exception insertTempDNPrintOut :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception insertTempDNPrintOut :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertTempDNPrintOut  DAO Result:" + countua);
		}
		return "" + countua;

	}

	@Override
	public String insertTempBill(String uanbr, String tarcdser, String tardescser, double billtonsser, double urateser,
			double totchrgamtser, String actnbrser, String tarcdwf, String tardescwf, double billtonswf, double uratewf,
			double totchrgamtwf, String actnbrwf, String tarcdsr, String tardescsr, double billtonssr, double uratesr,
			double totchrgamtsr, String actnbrsr, String UserID, String esnactnbr, String tarcdsr1, String tardescsr1,
			double billtonssr1, double uratesr1, double totchrgamtsr1, String actnbrsr1, String tarcdsr2,
			String tardescsr2, double billtonssr2, double uratesr2, double totchrgamtsr2, String actnbrsr2,
			double tunitser, double tunitwhf, double tunitsr, double tunitstore, double tunitserwhf)
			throws BusinessException {
		String sql = "";
		int countua = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: insertTempBill  DAO  Start Obj " + " uanbr:" + uanbr + " tarcdser:" + tarcdser
					+ " tardescser:" + tardescser + " billtonsser:" + billtonsser + " urateser:" + urateser
					+ " totchrgamtser:" + totchrgamtser + " actnbrser:" + actnbrser + " tardescwf:" + tardescwf
					+ " billtonswf:" + billtonswf + " uratewf:" + uratewf + " totchrgamtwf:" + totchrgamtwf
					+ " actnbrwf:" + actnbrwf + " tarcdsr:" + tarcdsr + " tardescsr:" + tardescsr + " billtonssr:"
					+ billtonssr + " uratesr:" + uratesr + " totchrgamtsr:" + totchrgamtsr + " actnbrsr:" + actnbrsr
					+ " UserID:" + UserID + " esnactnbr:" + esnactnbr + " tarcdsr1:" + tarcdsr1 + " tunitser:"
					+ tunitser + " tunitwhf:" + tunitwhf + " tunitsr:" + tunitsr + " tunitstore:" + tunitstore
					+ " tunitserwhf:" + tunitserwhf);

			sb.append("insert into sst_bill(DN_UA_NBR,TARRIF_CD_SER_CHRG,TARRIF_DESC_SER_CHRG,BILLABLE_TON_SER_CHRG,");
			sb.append("UNIT_RATE_SER_CHRG,TOTAL_AMT_SER_CHRG,TARRIF_CD_WHARF_CHRG,TARRIF_DESC_WHARF_CHRG,");
			sb.append("BILLABLE_TON_WHARF_CHRG,UNIT_RATE_WHARF_CHRG,TOTAL_AMT_WHARF_CHRG,TARRIF_CD_STORE_CHRG,");
			sb.append("TARRIF_DESC_STORE_CHRG,BILLABLE_TON_STORE_CHRG,UNIT_RATE_STORE_CHRG,TOTAL_AMT_STORE_CHRG,");
			sb.append("ACCT_NBR_SER_CHRG,ACCT_NBR_WHARF_CHRG,ACCT_NBR_STORE_CHRG,PRINT_IND,LAST_MODIFY_USER_ID,");
			sb.append("LAST_MODIFY_DTTM,EDO_ACCT_NBR,TARRIF_CD_SR_CHRG,TARRIF_DESC_SR_CHRG,BILLABLE_TON_SR_CHRG,");
			sb.append(
					"UNIT_RATE_SR_CHRG,TOTAL_AMT_SR_CHRG,ACCT_NBR_SR_WHARF_CHRG,TARRIF_CD_SER_WHARF_CHRG,TARRIF_DESC_SER_WHARF_CHRG,");
			sb.append(
					"BILLABLE_TON_SER_WHARF_CHRG,UNIT_RATE_SER_WHARF_CHRG,TOTAL_AMT_SER_WHARF_CHRG,ACCT_NBR_SER_WHARF_CHRG,");
			sb.append("TIME_UNIT_SER,TIME_UNIT_WHF,TIME_UNIT_SR,TIME_UNIT_SER_WHF,TIME_UNIT_STORE) ");
			sb.append("values(:uanbr,:tarcdser,:tardescser,:billtonsser,");
			sb.append(":urateser,:totchrgamtser,:tarcdwf,:tardescwf,");
			sb.append(":billtonswf,:uratewf,:totchrgamtwf,:tarcdsr,");
			sb.append(":tardescsr,:billtonssr,:uratesr,:totchrgamtsr,");
			sb.append(":actnbrser,:actnbrwf,:actnbrsr,'WEB',:UserID,sysdate,:esnactnbr,:tarcdsr1,");
			sb.append(":tardescsr1,:billtonssr1,:uratesr1,:totchrgamtsr1,:actnbrsr1,:tarcdsr2,");
			sb.append(":tardescsr2,:billtonssr2,:uratesr2,:totchrgamtsr2,:actnbrsr2,");
			sb.append(":tunitser,:tunitwhf,:tunitsr,:tunitstore,:tunitserwhf)");

			paramMap.put("tunitserwhf", tunitserwhf);
			paramMap.put("totchrgamtsr1", totchrgamtsr1);
			paramMap.put("actnbrsr1", actnbrsr1);
			paramMap.put("tarcdsr2", tarcdsr2);
			paramMap.put("tardescsr2", tardescsr2);
			paramMap.put("billtonssr2", billtonssr2);
			paramMap.put("uratesr2", uratesr2);
			paramMap.put("totchrgamtsr2", totchrgamtsr2);
			paramMap.put("actnbrsr2", actnbrsr2);
			paramMap.put("tunitser", tunitser);
			paramMap.put("tunitwhf", tunitwhf);
			paramMap.put("tunitsr", tunitsr);
			paramMap.put("tunitstore", tunitstore);
			paramMap.put("actnbrwf", actnbrwf);
			paramMap.put("actnbrsr", actnbrsr);
			paramMap.put("UserID", UserID);
			paramMap.put("esnactnbr", esnactnbr);
			paramMap.put("tarcdsr1", tarcdsr1);
			paramMap.put("tardescsr1", tardescsr1);
			paramMap.put("billtonssr1", billtonssr1);
			paramMap.put("uratesr1", uratesr1);
			paramMap.put("uanbr", uanbr);
			paramMap.put("tarcdser", tarcdser);
			paramMap.put("tardescser", tardescser);
			paramMap.put("billtonsser", billtonsser);
			paramMap.put("urateser", urateser);
			paramMap.put("totchrgamtser", totchrgamtser);
			paramMap.put("tarcdwf", tarcdwf);
			paramMap.put("tardescwf", tardescwf);
			paramMap.put("billtonswf", billtonswf);
			paramMap.put("uratewf", uratewf);
			paramMap.put("totchrgamtwf", totchrgamtwf);
			paramMap.put("tarcdsr", tarcdsr);
			paramMap.put("tardescsr", tardescsr);
			paramMap.put("billtonssr", billtonssr);
			paramMap.put("uratesr", uratesr);
			paramMap.put("totchrgamtsr", totchrgamtsr);
			paramMap.put("actnbrser", actnbrser);

			sql = sb.toString();

			log.info("insertTempBill SQL" + sql.toString() + " ,paramMap:" + paramMap.toString());
			countua = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

			log.info("esnactnbr---->>>>" + esnactnbr);
			if (countua == 0) {
				log.info("Writing from UAEJB.insertTempBill");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
		} catch (NullPointerException e) {
			log.error("Exception insertTempBill :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception insertTempBill :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertTempBill  DAO Result:" + countua);
		}
		return "" + countua;
	}

	@Override
	public void purgetemptableDN(String dnnbr) throws BusinessException {
		String sql = "";
		int count = 0;
		String sql1 = "";
		int count1 = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: purgetemptableDN  DAO dnnbr:" + dnnbr);

			sql = "delete from webdnuatemp where TransRefno =:dnnbr";
			sql1 = "delete from sst_bill where print_ind = 'WEB' and dn_ua_nbr = :dnnbr";

			paramMap.put("dnnbr", dnnbr);
			log.info("SQL:" + sql + " ,paramMap:" + paramMap.toString());

			count = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			log.info("count : " + count);

			log.info(" *** purgetemptableDN SQL *****" + sql1);
			count1 = namedParameterJdbcTemplate.update(sql1.toString(), paramMap);
			log.info("count : " + count1);
		} catch (NullPointerException e) {
			log.error("Exception purgetemptableDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception purgetemptableDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: purgetemptableDN  DAO  END");
		}
	}

	@Override
	public List<String[]> getCntrNbr(String edoasn) throws BusinessException {
		String sql = "";
		List<String[]> cntrNbr = new ArrayList<String[]>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCntrNbr  DAO  Start edoasn " + edoasn);

			sql = " select a.cntr_seq_nbr, b.cntr_nbr from edo_cntr a, cntr b  where a.edo_asn_nbr = :edoasn and a.cntr_seq_nbr = b.cntr_seq_nbr  and b.txn_status <> 'D'";
			paramMap.put("edoasn", edoasn);
			log.info("getCntrNbr SQL" + sql.toString() + " ,paramMap:" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				String[] arr = new String[2];
				arr[0] = CommonUtility.deNull(rs.getString("cntr_seq_nbr"));
				arr[1] = CommonUtility.deNull(rs.getString("cntr_nbr"));
				cntrNbr.add(arr);
			}
		} catch (NullPointerException e) {
			log.error("Exception getCntrNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getCntrNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCntrNbr  DAO  Result:" + cntrNbr.size());
		}
		return cntrNbr;
	}

	public boolean chkVVStatus(String esnNbrR) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		boolean bblno = false;
		try {
			log.info("START: chkVVStatus  DAO  Start Obj " + " esnNbrR:" + esnNbrR);

			sb.append("SELECT VC.VV_STATUS_IND FROM VESSEL_CALL VC,GB_EDO GB WHERE ");
			sb.append("VC.VV_STATUS_IND IN('UB','BR','CL') ");
			sb.append("AND VC.VV_CD = GB.VAR_NBR AND GB.EDO_ASN_NBR =:esnNbrR ");

			paramMap.put("esnNbrR", esnNbrR);
			log.info("chkVVStatus SQL" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bblno = true;
			} else {
				bblno = false;
			}
		} catch (NullPointerException e) {
			log.error("Exception chkVVStatus :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception chkVVStatus :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkVVStatus  DAO  Result: " + bblno);
		}
		return bblno;
	}

	@Override
	public List<EdoVO> fetchDNCreateDetail(String edoNbr, String transType, String searchcrg, String tesn_nbr)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<EdoVO> BJDetailsVect = new ArrayList<EdoVO>();
		try {
			log.info("START: fetchDNCreateDetail  DAO edoNbr:" + edoNbr + ",transType:" + transType + ",searchcrg:"
					+ searchcrg + ",tesn_nbr:" + tesn_nbr);

			boolean chkEdostatus = chkEdoStatus(edoNbr);
			if (!chkEdostatus) {
				log.info("Writing from DnBeanBean.fetchDNCreateDetail");
				log.info("EDO has been cancelled");
				throw new BusinessException("M80001");
			}

			boolean chkVvstatus = chkVVStatus(edoNbr);
			if (!chkVvstatus) {
				log.info("Writing from DnBeanBean.fetchDNCreateDetail");
				log.info("Vessel has not Berthed yet.  DN cannot be printed");
				throw new BusinessException("M80003");
			}

			if (transType.equals("L")) {
				sb.append(
						"SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM , VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB,");
				sb.append(
						"TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES,MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE ,");
				sb.append(
						"MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,GB_EDO.NBR_PKGS - GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.DN_NBR_PKGS AS BALANCE ,");
				sb.append(
						"GB_EDO.DN_NBR_PKGS ,GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , GB_EDO.EDO_ASN_NBR , BL_CNTR_DETAILS.CNTR_NBR,");
				sb.append("GB_EDO.BL_NBR,GB_EDO.CRG_STATUS AS TRANS_TYPE, ");
				sb.append(
						" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND ");
				sb.append(
						" FROM GB_EDO , VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS ,  MFT_MARKINGS,CC_UNSTUFF_MANIFEST ");
				sb.append(
						"WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND SHIFT_IND = 1 AND ");
				sb.append(
						"GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) AND  MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND GB_EDO.EDO_ASN_NBR =:edoNbr ");
				sb.append(" AND GB_EDO.EDO_STATUS='A'");

			} else {
				sb.append(
						"SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM , VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB,");
				sb.append(
						"TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES,MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE ,");
				sb.append(
						"MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.TRANS_DN_NBR_PKGS AS BALANCE ,");
				sb.append(
						"GB_EDO.DN_NBR_PKGS ,GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , GB_EDO.EDO_ASN_NBR , BL_CNTR_DETAILS.CNTR_NBR,");
				sb.append("GB_EDO.BL_NBR,GB_EDO.CRG_STATUS AS TRANS_TYPE, ");
				sb.append(
						" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND ");
				sb.append(
						" FROM GB_EDO , VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS ,  MFT_MARKINGS,CC_UNSTUFF_MANIFEST ");
				sb.append(
						"WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND SHIFT_IND = 1 AND ");
				sb.append(
						"GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) AND MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND GB_EDO.EDO_ASN_NBR =:edoNbr ");
				sb.append(" AND GB_EDO.EDO_STATUS='A'");

			}

			paramMap.put("edoNbr", edoNbr);
			log.info("fetchDNCreateDetail SQL" + sb.toString() + " ,paramMap:" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			String atb = "";
			String cod = "";
			String vName = "";
			String voyNbr = "";
			String cntrNo = "";
			String cntType = "";
			String cntrSize = "";
			String edoAsnNbr = "";
			String blNbr = "";
			String declaredWt = "";
			String declaredVol = "";
			String declaredQty = "";
			String marking = "";
			String crgDesc = "";
			String accNo = "";
			String balance = "";
			String cuurnetdate = "";
			while (rs.next()) {

				cuurnetdate = rs.getString("trans_dttm");
				atb = rs.getString("atb");
				cod = rs.getString("cod");
				vName = rs.getString("vsl_nm");
				voyNbr = rs.getString("in_voy_nbr");
				cntrNo = rs.getString("CNTR_NBR"); //
				cntType = rs.getString("CNTR_TYPE"); //
				cntrSize = rs.getString("CNTR_SIZE"); //
				edoAsnNbr = rs.getString("EDO_ASN_NBR"); //
				blNbr = rs.getString("bl_nbr");
				declaredWt = rs.getString("nom_wt");
				declaredVol = rs.getString("NOM_VOL");
				declaredQty = rs.getString("edoNbr");
				marking = rs.getString("mft_markings");
				crgDesc = rs.getString("CRG_DES");
				accNo = rs.getString("acct_nbr");
				balance = rs.getString("balance");
				EdoVO edoVo = new EdoVO();

				edoVo.setTransDate(cuurnetdate);
				edoVo.setATB(atb);
				edoVo.setCOD(cod);
				edoVo.setVslName(vName);
				edoVo.setInVoyNbr(voyNbr);
				edoVo.setBlNbr(blNbr);
				edoVo.setNomWt(declaredWt);
				edoVo.setNomVol(declaredVol);
				edoVo.setNoPkgs(declaredQty);
				edoVo.setMarkings(marking);
				edoVo.setCrgDes(crgDesc);
				edoVo.setAcctNo(accNo);
				edoVo.setCntrNo(cntrNo);
				edoVo.setCntrSize(cntrSize);
				edoVo.setCntrType(cntType);
				edoVo.setEdoAsnNbr(edoAsnNbr);
				edoVo.setBalance(balance);
				edoVo.setTransType(transType);
				edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				BJDetailsVect.add(edoVo);

			}
		} catch (NullPointerException e) {
			log.error("Exception fetchDNCreateDetail :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception fetchDNCreateDetail :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchDNCreateDetail  DAO  Result:"
					+ (BJDetailsVect != null ? BJDetailsVect.toString() : ""));
		}
		return BJDetailsVect;
	}

	@Override
	public boolean checkVehicleExit(String dnnbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = null;
		Date gate_out_dttm = null;
		boolean exist = false;
		try {
			log.info("START: checkVehicleExit  DAO  Start dnnbr " + dnnbr);

			sql = "SELECT GATE_OUT_DTTM FROM DN_DETAILS WHERE DN_NBR = :dnnbr ";

			paramMap.put("dnnbr", dnnbr);
			log.info("checkVehicleExit SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next())
				gate_out_dttm = rs.getDate("GATE_OUT_DTTM");

			log.info("END: *** checkVehicleExit Result *****" + gate_out_dttm);
			if (gate_out_dttm != null) {
				exist = true;
				return exist;
			} else {
				return exist;
			}
		} catch (NullPointerException e) {
			log.error("Exception checkVehicleExit :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception checkVehicleExit :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkVehicleExit  DAO  Result:" + exist);
		}

	}

	public boolean chkEdoStatus(String esnNbrR) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		boolean bblno = false;
		try {
			log.info("START: chkEdoStatus  DAO  Start esnNbrR " + esnNbrR);

			sql = "SELECT EDO_STATUS FROM gb_edo WHERE EDO_STATUS='A' AND EDO_ASN_NBR = :esnNbrR";

			paramMap.put("esnNbrR", esnNbrR);
			log.info("chkEdoStatus SQL" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				bblno = true;
			} else {
				bblno = false;
			}
		} catch (NullPointerException e) {
			log.error("Exception chkEdoStatus :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception chkEdoStatus :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkEdoStatus  DAO Result:" + bblno);
		}
		return bblno;
	}

	public String getVesselATBDate(String esnNbrR) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		String atb = "", etb = "", btr = "";
		try {
			log.info("START: getVesselATBDate  DAO esnNbrR: " + esnNbrR);

			sb.append(
					"SELECT TO_CHAR(B.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB_DTTM ,TO_CHAR(B.ETB_DTTM,'DD/MM/YYYY HH24:MI') AS ETB_DTTM,TO_CHAR(VC.VSL_BERTH_DTTM, ");
			sb.append("'DD/MM/YYYY HH24:MI') AS BTR_DTTM FROM VESSEL_CALL VC,GB_EDO GB,BERTHING B ");
			sb.append("WHERE  VC.VV_CD = GB.VAR_NBR AND B.VV_CD=VC.VV_CD AND GB.EDO_ASN_NBR =:esnNbrR ");

			paramMap.put("esnNbrR", esnNbrR);
			log.info("getVesselATBDate SQL" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				atb = rs.getString("ATB_DTTM");
				etb = rs.getString("ETB_DTTM");
				btr = rs.getString("BTR_DTTM");
				if (atb != null) {
					return atb;
				}
				if (etb != null) {
					return etb;
				}

				if (btr != null) {
					return btr;
				}
			}
		} catch (NullPointerException e) {
			log.error("Exception getVesselATBDate :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getVesselATBDate :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselATBDate  DAO Result: " + atb + ",etb:" + etb + ",btr:" + btr);
		}
		return null;
	}

	@Override
	public List<EdoVO> fetchShutoutDNCreateDetail(String edoNbr, String transType, String searchcrg, String tesn_nbr)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<EdoVO> BJDetailsVect = new ArrayList<EdoVO>();
		try {
			log.info("START: fetchShutoutDNCreateDetail  DAO edoNbr: " + edoNbr + ",transType:" + transType
					+ ",searchcrg:" + searchcrg + ",tesn_nbr:" + tesn_nbr);

			boolean chkEdostatus = chkEdoStatus(edoNbr);
			if (!chkEdostatus) {
				log.info("Writing from DnBeanBean.fetchShutoutDNCreateDetail");
				log.info("EDO has been cancelled");
				throw new BusinessException("M80001");
			}

			String atbDate = getVesselATBDate(edoNbr);
			if (atbDate == null || atbDate.length() == 0) {
				log.info("Writing from DnBeanBean.fetchShutoutDNCreateDetail");
				log.info("Vessel has not Berthed yet.  DN cannot be printed");
				throw new BusinessException("M80003");
			}

			if (isEsn(null, edoNbr)) {
				sb.append(" SELECT ");
				sb.append(" TO_CHAR (SYSDATE, 'DD/MM/YYYY HH24:MI') AS TRANS_DTTM, ");
				sb.append(" VC.VSL_NM, ");
				sb.append(" VC.OUT_VOY_NBR, ");
				sb.append(
						" TO_CHAR((SELECT MIN(UA.TRANS_DTTM) FROM UA_DETAILS UA,GB_EDO EDO WHERE EDO.ESN_ASN_NBR = UA.ESN_ASN_NBR AND EDO.EDO_ASN_NBR =:edoNbr ");
				sb.append("'), 'DD/MM/YYYY HH24:MI') AS FIRST_UA,  ");
				sb.append(" EDO.NBR_PKGS AS EDONBR, ");
				sb.append(" EDO.NBR_PKGS - EDO.DN_NBR_PKGS - EDO.TRANS_DN_NBR_PKGS AS BALANCE, ");
				sb.append(" EDO.DN_NBR_PKGS, ");
				sb.append(" EDO.NOM_WT, ");
				sb.append(" EDO.NOM_VOL, ");
				sb.append(" EDO.ACCT_NBR, ");
				sb.append(" EDO.EDO_ASN_NBR, ");
				sb.append(" EDO.BL_NBR, ");
				sb.append(" EDO.CRG_STATUS AS TRANS_TYPE, ");
				sb.append(" ESNM.MARKINGS, ");
				sb.append(" ESN.CRG_DES, ");
				sb.append(" vc.terminal, ");
				sb.append(" vc.scheme, ");
				sb.append(" vc.combi_gc_scheme, ");
				sb.append(" vc.combi_gc_ops_ind  ");
				sb.append(" FROM GB_EDO EDO, VESSEL_CALL VC, BERTHING B, ESN_DETAILS ESN, ESN_MARKINGS ESNM  ");
				sb.append(" WHERE ");
				sb.append(" EDO.VAR_NBR = VC.VV_CD ");
				sb.append(" AND ESN.ESN_ASN_NBR = EDO.ESN_ASN_NBR ");
				sb.append(" AND ESNM.ESN_ASN_NBR = EDO.ESN_ASN_NBR ");
				sb.append(" AND VC.VV_CD = B.VV_CD ");
				sb.append(" AND SHIFT_IND = 1 ");
				sb.append(" AND EDO.EDO_ASN_NBR = :edoNbr ");
				sb.append(" AND EDO.EDO_STATUS = 'A'");
			} else {
				sb.append(" SELECT ");
				sb.append(" TO_CHAR (SYSDATE, 'DD/MM/YYYY HH24:MI') AS TRANS_DTTM, ");
				sb.append(" VC.VSL_NM, ");
				sb.append(" VC.OUT_VOY_NBR, ");
				sb.append(
						" TO_CHAR((SELECT MIN(UA.TRANS_DTTM) FROM UA_DETAILS UA,GB_EDO EDO WHERE EDO.ESN_ASN_NBR = UA.ESN_ASN_NBR AND EDO.EDO_ASN_NBR =:edoNbr ");
				sb.append("'), 'DD/MM/YYYY HH24:MI') AS FIRST_UA,  ");
				sb.append(" EDO.NBR_PKGS AS EDONBR, ");
				sb.append(" EDO.NBR_PKGS - EDO.DN_NBR_PKGS - EDO.TRANS_DN_NBR_PKGS AS BALANCE, ");
				sb.append(" EDO.DN_NBR_PKGS, ");
				sb.append(" EDO.NOM_WT, ");
				sb.append(" EDO.NOM_VOL, ");
				sb.append(" EDO.ACCT_NBR, ");
				sb.append(" EDO.EDO_ASN_NBR, ");
				sb.append(" EDO.BL_NBR, ");
				sb.append(" EDO.CRG_STATUS AS TRANS_TYPE, ");
				sb.append(" ESNM.MARKINGS, ");
				sb.append(" ESN.CRG_DES,  ");
				sb.append(" vc.terminal,  ");
				sb.append(" vc.scheme,  ");
				sb.append(" vc.combi_gc_scheme,  ");
				sb.append(" vc.combi_gc_ops_ind  ");
				sb.append(" FROM GB_EDO EDO, VESSEL_CALL VC, BERTHING B, TESN_PSA_JP ESN, ESN_MARKINGS ESNM  ");
				sb.append(" WHERE  ");
				sb.append(" EDO.VAR_NBR = VC.VV_CD ");
				sb.append(" AND ESN.ESN_ASN_NBR = EDO.ESN_ASN_NBR ");
				sb.append(" AND ESNM.ESN_ASN_NBR = EDO.ESN_ASN_NBR ");
				sb.append(" AND VC.VV_CD = B.VV_CD ");
				sb.append(" AND SHIFT_IND = 1 ");
				sb.append(" AND EDO.EDO_ASN_NBR =:edoNbr ");
				sb.append(" AND EDO.EDO_STATUS = 'A'");
			}

			paramMap.put("edoNbr", edoNbr);
			log.info("fetchShutoutDNCreateDetail SQL" + sb.toString() + " ,paramMap:" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				String cuurnetdate = rs.getString("trans_dttm");
				String vName = rs.getString("VSL_NM");
				String voyNbr = rs.getString("OUT_VOY_NBR");
				String firstUa = rs.getString("FIRST_UA");
				String declaredQty = rs.getString("EDONBR");
				String balance = rs.getString("BALANCE");
				String declaredVol = rs.getString("NOM_VOL");
				String declaredWt = rs.getString("NOM_WT");
				String accNo = rs.getString("ACCT_NBR");
				String blNbr = rs.getString("BL_NBR");
				String edoAsnNbr = rs.getString("EDO_ASN_NBR");
				String crgDes = rs.getString("CRG_DES");
				String markings = rs.getString("MARKINGS");

				EdoVO edoVo = new EdoVO();

				edoVo.setTransDate(cuurnetdate);
				edoVo.setVslName(vName);
				edoVo.setFirstUa(firstUa);
				edoVo.setInVoyNbr(voyNbr);
				edoVo.setBlNbr(blNbr);
				edoVo.setNomWt(declaredWt);
				edoVo.setNomVol(declaredVol);
				edoVo.setNoPkgs(declaredQty);
				edoVo.setAcctNo(accNo);
				edoVo.setEdoAsnNbr(edoAsnNbr);
				edoVo.setBalance(balance);
				edoVo.setTransType(transType);
				edoVo.setCrgDes(crgDes);
				edoVo.setMarkings(markings);
				edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				BJDetailsVect.add(edoVo);

			}
		} catch (NullPointerException e) {
			log.error("Exception fetchShutoutDNCreateDetail :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception fetchShutoutDNCreateDetail :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchShutoutDNCreateDetail  DAO  Result:"
					+ (BJDetailsVect != null ? BJDetailsVect.toString() : ""));
		}
		return BJDetailsVect;
	}

	@Override
	public String getCntrNo(String dnNbr) throws BusinessException {
		String sql = "";
		String cntrNbr = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCntrNo dnNbr:" + dnNbr);

			sql = "select cntr_nbr from dn_details where dn_nbr =:dnNbr";
			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				cntrNbr = rs.getString(1);
			}
		} catch (NullPointerException e) {
			log.error("Exception getCntrNo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getCntrNo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCntrNo  DAO  Result:" + cntrNbr);
		}
		return cntrNbr;
	}

	@Override
	public List<EdoVO> fetchDNDetail(String strEdoNo, String edoNbr, String status, String searchcrg, String tesn_nbr)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<EdoVO> BJDetailsVect = new ArrayList<EdoVO>();
		try {
			log.info("START: fetchDNDetail  DAO  Start Obj strEdoNo:" + strEdoNo + "edoNbr:" + edoNbr + "status:"
					+ status + "searchcrg:" + searchcrg + "tesn_nbr:" + tesn_nbr);

			String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
			String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);
			boolean checkEdoStuff = chkEDOStuffing(strEdoNo); // add vinayak added on 8 jan 2004
			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {

				sb.append(
						"SELECT GB_EDO.EDO_ASN_NBR,TO_CHAR(DN_DETAILS.TRANS_DTTM,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM , VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB, ");
				sb.append(
						"TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES,MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE , ");
				sb.append(
						"MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,DN_DETAILS.DN_NBR ,DN_DETAILS.NBR_PKGS,DN_DETAILS.DP_IC_NBR ,DN_DETAILS.DP_IC_TYPE ,DN_DETAILS.DP_NM,");
				sb.append(
						"GB_EDO.NBR_PKGS - GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.DN_NBR_PKGS AS BALANCE ,NVL(GB_EDO.NBR_PKGS,0) - NVL(GB_EDO.RELEASE_NBR_PKGS,0) - NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)- NVL(GB_EDO.DN_NBR_PKGS,0)  AS BAL2,");
				sb.append(
						"GB_EDO.DN_NBR_PKGS ,GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , GB_EDO.EDO_ASN_NBR , BL_CNTR_DETAILS.CNTR_NBR,GB_EDO.AA_NM,NVL(GB_EDO.RELEASE_NBR_PKGS,0) AS RELEASEOTY, ");
				sb.append(
						"DN_DETAILS.BILLABLE_TON,GB_EDO.CRG_STATUS AS TRANS_TYPE,GB_EDO.BL_NBR,GB_EDO.VAR_NBR,GB_EDO.CUT_OFF_NBR_PKGS,MANIFEST_DETAILS.NBR_PKGS_IN_PORT,MANIFEST_DETAILS.NBR_PKGS AS MFTNBR,NVL(GB_EDO.RELEASE_NBR_PKGS,0)-NVL(GB_EDO.TRANS_DN_NBR_PKGS,0) AS TRNSPKGS,GB_EDO.MFT_SEQ_NBR AS MFTSQNBR, ");
				sb.append("DN_DETAILS.CRG_DEST, ");
				sb.append(
						" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND ");
				sb.append(
						"FROM GB_EDO , DN_DETAILS,VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS ,  MFT_MARKINGS,CC_UNSTUFF_MANIFEST ");
				sb.append(
						"WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND  ");
				sb.append(
						"GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND DN_DETAILS.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND SHIFT_IND = 1 AND GB_EDO.EDO_STATUS='A' AND  ");
				sb.append(
						"MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) AND DN_DETAILS.DN_NBR =:edoNbr  ");

				// log.info("881 --if (searchcrg != null && !searchcrg.equals() &&
				// searchcrg.equals(LT)) :"+sql);

			} else {
				if (chktesnJpJp_nbr.equals("Y")) {

					sb.append(
							"SELECT GB_EDO.EDO_ASN_NBR,TO_CHAR(DN_DETAILS.TRANS_DTTM,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM , VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB, ");
					sb.append(
							"TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES,MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE , ");
					sb.append(
							"MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,DN_DETAILS.DN_NBR ,DN_DETAILS.NBR_PKGS,DN_DETAILS.DP_IC_NBR ,DN_DETAILS.DP_IC_TYPE ,DN_DETAILS.DP_NM, ");
					sb.append(
							"GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.TRANS_DN_NBR_PKGS AS BALANCE ,NVL(GB_EDO.NBR_PKGS,0) - NVL(GB_EDO.RELEASE_NBR_PKGS,0) - NVL(GB_EDO.TRANS_DN_NBR_PKGS,0) - NVL(GB_EDO.DN_NBR_PKGS,0) AS BAL2, ");
					sb.append(
							"GB_EDO.DN_NBR_PKGS ,GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , GB_EDO.EDO_ASN_NBR , BL_CNTR_DETAILS.CNTR_NBR,GB_EDO.AA_NM,NVL(GB_EDO.RELEASE_NBR_PKGS,0) AS RELEASEOTY, ");
					sb.append(
							"DN_DETAILS.BILLABLE_TON,GB_EDO.CRG_STATUS AS  TRANS_TYPE,GB_EDO.BL_NBR,GB_EDO.VAR_NBR,F.NBR_PKGS AS JPJPNPKG,F.DN_NBR_PKGS AS JPJPDN_NPKG,GB_EDO.CUT_OFF_NBR_PKGS,MANIFEST_DETAILS.NBR_PKGS_IN_PORT,MANIFEST_DETAILS.NBR_PKGS AS MFTNBR,NVL(GB_EDO.RELEASE_NBR_PKGS,0)-NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)-NVL(GB_EDO.DN_NBR_PKGS,0) AS TRNSPKGS,GB_EDO.MFT_SEQ_NBR AS MFTSQNBR, ");
					sb.append("DN_DETAILS.CRG_DEST, ");
					sb.append(
							" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND ");
					sb.append(
							" FROM GB_EDO , DN_DETAILS,VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS ,  MFT_MARKINGS,TESN_JP_JP F,CC_UNSTUFF_MANIFEST   ");
					sb.append(
							"WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND SHIFT_IND = 1 AND  ");
					sb.append(
							"GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND  MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND DN_DETAILS.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND ");
					sb.append(
							"MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) AND F.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND DN_DETAILS.DN_NBR = :edoNbr ");
					sb.append(" AND F.ESN_ASN_NBR=:tesn_nbr ");
					sb.append(" AND GB_EDO.EDO_STATUS='A'");
					// log.info("907 --if (chktesnJpJp_nbr.equals(Y)) in fetchDNDEtail():"+sql);

				} else if (chktesnJpPsa_nbr.equals("Y")) {

					sb.append(
							"SELECT GB_EDO.EDO_ASN_NBR,TO_CHAR(DN_DETAILS.TRANS_DTTM,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM , VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB,");
					sb.append(
							"TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES,MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE , ");
					sb.append(
							"MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,DN_DETAILS.DN_NBR ,DN_DETAILS.NBR_PKGS,DN_DETAILS.DP_IC_NBR ,DN_DETAILS.DP_IC_TYPE ,DN_DETAILS.DP_NM, ");
					sb.append(
							"GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.TRANS_DN_NBR_PKGS AS BALANCE ,NVL(GB_EDO.NBR_PKGS,0) - NVL(GB_EDO.RELEASE_NBR_PKGS,0) - NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)- NVL(GB_EDO.DN_NBR_PKGS,0)  AS BAL2, ");
					sb.append(
							"GB_EDO.DN_NBR_PKGS ,GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , GB_EDO.EDO_ASN_NBR , BL_CNTR_DETAILS.CNTR_NBR,GB_EDO.AA_NM,NVL(GB_EDO.RELEASE_NBR_PKGS,0) AS RELEASEOTY, ");
					sb.append(
							"DN_DETAILS.BILLABLE_TON,GB_EDO.CRG_STATUS AS  TRANS_TYPE,GB_EDO.BL_NBR,GB_EDO.VAR_NBR,F.NBR_PKGS AS JPPSANPKG,F.DN_NBR_PKGS AS JPPSADN_NPKG,GB_EDO.CUT_OFF_NBR_PKGS,MANIFEST_DETAILS.NBR_PKGS_IN_PORT,MANIFEST_DETAILS.NBR_PKGS AS MFTNBR,NVL(GB_EDO.RELEASE_NBR_PKGS,0)-NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)- NVL(GB_EDO.DN_NBR_PKGS,0) AS TRNSPKGS,GB_EDO.MFT_SEQ_NBR AS MFTSQNBR, ");
					sb.append("DN_DETAILS.CRG_DEST, ");
					sb.append(
							" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND  ");
					sb.append(
							" FROM GB_EDO , DN_DETAILS,VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS ,  MFT_MARKINGS,TESN_JP_PSA F,CC_UNSTUFF_MANIFEST  ");
					sb.append(
							"WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND SHIFT_IND = 1 AND  ");
					sb.append(
							"GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND DN_DETAILS.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND ");
					sb.append(
							"MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) AND MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND F.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND DN_DETAILS.DN_NBR =:edoNbr  ");
					sb.append("AND F.ESN_ASN_NBR=:tesn_nbr ");
					sb.append(" AND GB_EDO.EDO_STATUS='A'");

				} else if (checkEdoStuff) {

					sb.append(
							"SELECT GB_EDO.EDO_ASN_NBR,TO_CHAR(DN_DETAILS.TRANS_DTTM,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM ,VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB,TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES, ");
					sb.append(
							"MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE ,MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,DN_DETAILS.DN_NBR ,DN_DETAILS.NBR_PKGS,DN_DETAILS.DP_IC_NBR, DN_DETAILS.DP_IC_TYPE ,DN_DETAILS.DP_NM,GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.TRANS_DN_NBR_PKGS AS BALANCE ,NVL(GB_EDO.NBR_PKGS,0) - NVL(GB_EDO.RELEASE_NBR_PKGS,0) - NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)- NVL(GB_EDO.DN_NBR_PKGS,0) AS BAL2,GB_EDO.DN_NBR_PKGS , ");
					sb.append(
							"GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , BL_CNTR_DETAILS.CNTR_NBR,GB_EDO.AA_NM,NVL(GB_EDO.RELEASE_NBR_PKGS,0) AS RELEASEOTY,DN_DETAILS.BILLABLE_TON,GB_EDO.CRG_STATUS AS  TRANS_TYPE,GB_EDO.BL_NBR,GB_EDO.VAR_NBR,  ");
					sb.append(
							"CC_STUFFING_DETAILS.NBR_PKGS AS STUFFNPKG,CC_STUFFING_DETAILS.DN_NBR_PKGS AS STUFFDNNPKG,GB_EDO.CUT_OFF_NBR_PKGS,MANIFEST_DETAILS.NBR_PKGS_IN_PORT,MANIFEST_DETAILS.NBR_PKGS AS MFTNBR,NVL(GB_EDO.RELEASE_NBR_PKGS,0)-NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)- NVL(GB_EDO.DN_NBR_PKGS,0) AS TRNSPKGS,GB_EDO.MFT_SEQ_NBR AS MFTSQNBR,  ");
					sb.append(
							" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND  ");
					sb.append(
							" FROM GB_EDO,DN_DETAILS,VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS , MFT_MARKINGS,CC_STUFFING_DETAILS,CC_STUFFING,CC_UNSTUFF_MANIFEST");
					sb.append(
							" WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND BERTHING.SHIFT_IND = 1 AND GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND DN_DETAILS.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) ");
					sb.append(
							"AND MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND CC_STUFFING.STUFF_CLOSED='Y' AND CC_STUFFING_DETAILS.REC_STATUS='A' AND CC_STUFFING.ACTIVE_STATUS='A' AND GB_EDO.EDO_ASN_NBR=DN_DETAILS.EDO_ASN_NBR AND DN_DETAILS.EDO_ASN_NBR=CC_STUFFING_DETAILS.EDO_ESN_NBR AND CC_STUFFING.STUFF_SEQ_NBR=CC_STUFFING_DETAILS.STUFF_SEQ_NBR AND DN_DETAILS.DN_NBR = :edoNbr AND CC_STUFFING_DETAILS.STUFF_SEQ_NBR=:tesn_nbr AND GB_EDO.EDO_STATUS='A'  ");
				}
			}

			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
				paramMap.put("edoNbr", edoNbr);

			} else {
				paramMap.put("edoNbr", edoNbr);
				paramMap.put("tesn_nbr", tesn_nbr);
			}

			log.info("fetchDNDetail SQL" + sb.toString() + " ,paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			String cuurnetdate = "";
			String atb = "";
			String cod = "";
			String vName = "";
			String voyNbr = "";
			String cntrNo = "";
			String cntType = "";
			String cntrSize = "";
			String transType = "";
			String edoAsnNbr = "";
			String blNbr = "";
			String declaredWt = "";
			String declaredVol = "";
			String declaredQty = "";
			String transQty = "";
			String nricNo = "";
			String marking = "";
			String crgDesc = "";
			String accNo = "";
			String billableUnit = "";
			String balance = "";
			String icType = "";
			String name = "";
			String SchemeName = "";
			String vvcd = "";
			String transNbrPkgs = "";
			String mftNbr = "";
			String sumCtDNnbr = "";
			String sumEdoCtDNnbr = "";
			String mftSqNbr = "";
			String aa_name = "";
			// ++ vietnd02
			String crgDes = "";
			// --
			while (rs.next()) {
				cuurnetdate = rs.getString("trans_dttm");
				atb = CommonUtility.deNull(rs.getString("atb"));
				cod = rs.getString("cod");
				vName = rs.getString("vsl_nm");
				voyNbr = rs.getString("in_voy_nbr");
				cntrNo = CommonUtility.deNull(rs.getString("CNTR_NBR"));
				cntType = CommonUtility.deNull(rs.getString("CNTR_TYPE"));
				cntrSize = CommonUtility.deNull(rs.getString("CNTR_SIZE"));
				transType = rs.getString("TRANS_TYPE");
				edoAsnNbr = rs.getString("EDO_ASN_NBR");
				blNbr = rs.getString("bl_nbr");
				declaredWt = rs.getString("nom_wt");
				declaredVol = rs.getString("NOM_VOL");
				declaredQty = rs.getString("edoNbr");
				transQty = rs.getString("NBR_PKGS");
				nricNo = rs.getString("DP_IC_NBR");
				icType = rs.getString("DP_IC_TYPE");
				name = rs.getString("DP_NM");
				marking = rs.getString("mft_markings");
				crgDesc = rs.getString("CRG_DES");
				accNo = rs.getString("acct_nbr");
				aa_name = rs.getString("aa_nm");
				// billableUnit =rs.getString("billable_ton");
				balance = rs.getString("balance");
				vvcd = rs.getString("var_nbr");
				SchemeName = getSchemeName(accNo, vvcd);
				mftNbr = rs.getString("mftNbr");
				mftSqNbr = rs.getString("mftsqnbr");
				sumCtDNnbr = getCTDNnbr(mftSqNbr);
				sumEdoCtDNnbr = getEDOCTDNnbr(mftSqNbr);
				transNbrPkgs = rs.getString("trnsPkgs");

				crgDes = rs.getString("CRG_DEST");
				if ("O".equalsIgnoreCase(crgDes))
					crgDes = "Out Of JP";
				else if ("V".equalsIgnoreCase(crgDes))
					crgDes = "Vessel";
				else if ("L".equalsIgnoreCase(crgDes))
					crgDes = "Leased Area";

				double nom_wt = 0;
				nom_wt = Double.parseDouble(declaredWt);
				double nom_vol = 0;
				double Bill_ton = 0;
				nom_vol = Double.parseDouble(declaredVol);

				// if(ftrans.equals("True"))
				// {
				if ((nom_wt / 1000) > nom_vol) {
					Bill_ton = nom_wt / 1000;
				} else {
					Bill_ton = nom_vol;
				} // end if nomwt

				billableUnit = "" + Bill_ton;

				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
					String bal_cutoff = rs.getString("CUT_OFF_NBR_PKGS");
					String bal_short_land = rs.getString("NBR_PKGS_IN_PORT");
					int con3 = 0;
					int con4 = 0;
					int con5 = 0;
					if (bal_cutoff != null && !bal_cutoff.equals(""))
						con3 = Integer.parseInt(bal_cutoff);
					if (balance != null && !balance.equals(""))
						con4 = Integer.parseInt(balance);
					if (bal_short_land != null && !bal_short_land.equals(""))
						con5 = Integer.parseInt(bal_short_land);
					int blPkgs = 0;
					blPkgs = Integer.parseInt(mftNbr) - (con5 + Integer.parseInt(sumCtDNnbr));
					String relPkgsNbr_str = rs.getString("bal2");
					int relPkgsNbr = Integer.parseInt(relPkgsNbr_str);
					String maxQty = rs.getString("releaseOty");
					int bal4 = con4 - con3;

					if (aa_name != null && !aa_name.equals("")) {
						bal4 = bal4 + Integer.parseInt(maxQty);
						if (relPkgsNbr <= bal4)
							bal4 = relPkgsNbr;

					}

					if (blPkgs < bal4)
						bal4 = blPkgs;
					// int bal4 = con4 - (con3+con5);
					balance = String.valueOf(bal4);
				} else {
					String bal_cutoff = rs.getString("CUT_OFF_NBR_PKGS");
					String bal_short_land = rs.getString("NBR_PKGS_IN_PORT");
					int con3 = 0;

					int con5 = 0;
					if (bal_cutoff != null && !bal_cutoff.equals(""))
						con3 = Integer.parseInt(bal_cutoff);
					// if(balance != null && !balance.equals(""))
					// con4 = Integer.parseInt(balance);
					if (bal_short_land != null && !bal_short_land.equals(""))
						con5 = Integer.parseInt(bal_short_land);

					String relPkgsNbr_str = rs.getString("bal2");
					int relPkgsNbr = Integer.parseInt(relPkgsNbr_str);
					int edoNbrPkgs = Integer.parseInt(declaredQty) - Integer.parseInt(transNbrPkgs) - con3;
					int blPkgs = 0;
					blPkgs = Integer.parseInt(mftNbr) - (con5 + Integer.parseInt(sumEdoCtDNnbr));

					if (chktesnJpJp_nbr.equals("Y")) {
						String bal1 = rs.getString("jpjpnpkg");
						String bal2 = rs.getString("jpjpdn_npkg");
						// transQty = bal2;
						declaredQty = bal1;
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr < bal3)
									bal3 = relPkgsNbr - con3;
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;
						balance = String.valueOf(bal3);
					} else if (chktesnJpPsa_nbr.equals("Y")) {
						String bal1 = rs.getString("jppsanpkg");
						String bal2 = rs.getString("jppsadn_npkg");
						// transQty = bal2;
						declaredQty = bal1;
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr < bal3)
									bal3 = relPkgsNbr - con3;
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;

						balance = String.valueOf(bal3);
					} else if (checkEdoStuff) {
						String bal1 = rs.getString("stuffnpkg");
						String bal2 = rs.getString("stuffdnnpkg");
						// transQty = bal2;
						declaredQty = bal1; // declaredQty=gb_edo.nbr_pkgs
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						if (blPkgs >= bal3)
						// blPkgs=manifest_details.NBR_PKGS - (manifest_details.NBR_PKGS_IN_PORT +
						// (select
						// sum(nvl(DN_NBR_PKGS,0)+nvl(CUT_OFF_NBR_PKGS,0)+nvl(trans_dn_nbr_pkgs,0))
						// from gb_edo where MFT_SEQ_NBR =''))
						{
							if (edoNbrPkgs >= bal3)
							// edoNbrPkgs=gb_edo.nbr_pkgs - gb_edo.dn_nbr_pkgs -
							// gb_edo.CUT_OFF_NBR_PKGS
							{
								if (relPkgsNbr >= bal3) {
									// relPkgsNbr=NVL(gb_edo.nbr_pkgs,0) - NVL(gb_edo.release_nbr_pkgs,0) -
									// NVL(gb_edo.trans_dn_nbr_pkgs,0)- NVL(gb_edo.dn_nbr_pkgs,0)
									int tmp = bal3;
									bal3 = tmp;
								} else {
									bal3 = relPkgsNbr - con3; // con3=gb_edo.CUT_OFF_NBR_PKGS
								}

							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;

						balance = String.valueOf(bal3);
					}
				}
				EdoVO edoVo = new EdoVO();
				edoVo.setTransDate(cuurnetdate);
				edoVo.setATB(atb);
				edoVo.setCOD(cod);
				edoVo.setVslName(vName);
				edoVo.setInVoyNbr(voyNbr);
				edoVo.setBlNbr(blNbr);
				edoVo.setNomWt(declaredWt);
				edoVo.setNomVol(declaredVol);
				edoVo.setNoPkgs(declaredQty);
				edoVo.setDeliveredPkgs(transQty);
				edoVo.setAAIcNbr(nricNo);
				edoVo.setAAName(name);
				edoVo.setAACustCD(icType);
				edoVo.setMarkings(marking);
				edoVo.setCrgDes(crgDesc);
				edoVo.setAcctNo(accNo);
				edoVo.setBillTon(billableUnit);
				edoVo.setCntrNo(cntrNo);
				edoVo.setCntrSize(cntrSize);
				edoVo.setCntrType(cntType);
				edoVo.setEdoAsnNbr(edoAsnNbr);
				edoVo.setTransType(transType);
				// ++
				edoVo.setCrgDestination(crgDes);
				// --
				int temp_balance = Integer.parseInt(balance);
				if (temp_balance < 0)
					balance = "0";
				edoVo.setBalance(balance);
				edoVo.setDnStatus(SchemeName);
				edoVo.setVvCd(vvcd);
				edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				BJDetailsVect.add(edoVo);

			}
		} catch (NullPointerException e) {
			log.error("Exception fetchDNDetail :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception fetchDNDetail :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchDNDetail  DAO Result:" + (BJDetailsVect != null ? BJDetailsVect.toString() : ""));
		}
		return BJDetailsVect;
	}

	@Override
	public List<EdoVO> getVechDetails(String dnNbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<EdoVO> vechDetails = new ArrayList<EdoVO>();
		EdoVO edoVo = new EdoVO();
		// int vechSqNbr = 0;
		try {
			log.info("START:  *** getVechDetails Dao Start : *** " + dnNbr);
			// To use truck number from DN_DETAILS instead.  26/5/2010.
		    //sql = "SELECT VEH_NO,DN_VEH_SEQ FROM DN_VEH WHERE DN_NBR = '" + dnNbr + "' ORDER BY DN_VEH_SEQ";
			sql = "SELECT TRUCK_NBR FROM DN_DETAILS WHERE DN_NBR =:dnNbr";

			paramMap.put("dnNbr", dnNbr);
			log.info("getVechDetails SQL" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				edoVo.setVech1(CommonUtility.deNull(rs.getString("TRUCK_NBR")));
				/*
				 * vechSqNbr = rs.getInt("DN_VEH_SEQ"); if (vechSqNbr == 1)
				 * edoVo.setVech1(CommonUtility.deNull(rs.getString("VEH_NO"))); if (vechSqNbr
				 * == 2) edoVo.setVech2(CommonUtility.deNull(rs.getString("VEH_NO"))); if
				 * (vechSqNbr == 3)
				 * edoVo.setVech3(CommonUtility.deNull(rs.getString("VEH_NO"))); if (vechSqNbr
				 * == 4) edoVo.setVech4(CommonUtility.deNull(rs.getString("VEH_NO"))); if
				 * (vechSqNbr == 5)
				 * edoVo.setVech5(CommonUtility.deNull(rs.getString("VEH_NO")));
				 */

				vechDetails.add(edoVo);
			}
		} catch (NullPointerException e) {
			log.error("Exception getVechDetails :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getVechDetails :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVechDetails  DAO Result:" + (vechDetails != null ? vechDetails.toString() : ""));
		}
		return vechDetails;
	}

	private boolean isEsn(String esnAsnNO, String edoAsnNbr) throws BusinessException {
		boolean isEsn = false;
		String type = "C";
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		if (esnAsnNO != null) {
			sb.append("SELECT TRANS_TYPE FROM ESN WHERE ESN_ASN_NBR= :esnAsnNO");
		} else {
			sb.append("SELECT ESN.TRANS_TYPE FROM ESN ESN,GB_EDO EDO ");
			sb.append("WHERE ESN.ESN_ASN_NBR=EDO.ESN_ASN_NBR AND EDO.EDO_ASN_NBR= :edoAsnNbr");
		}
		try {
			log.info("START:isEsn DAO esnAsnNO:" + esnAsnNO + "edoAsnNbr:" + edoAsnNbr);

			if (esnAsnNO != null) {
				paramMap.put("esnAsnNO", esnAsnNO);
			} else {
				paramMap.put("edoAsnNbr", edoAsnNbr);
			}

			log.info("SQL" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				type = CommonUtility.deNull(rs.getString("TRANS_TYPE"));
			}

			if (type.equals("E"))
				isEsn = true;
		} catch (NullPointerException e) {
			log.error("Exception isEsn :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception isEsn :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isEsn  DAO  Result:" + isEsn);
		}
		return isEsn;
	}

	private String getSchemeName(String accNo, String vvcd) throws BusinessException {
		String sql = "";
		String sch = "";
		String schemeName = "";
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getSchemeName accNo:" + accNo + "vvcd:" + vvcd);

			String sql1 = "SELECT SCHEME FROM VESSEL_CALL WHERE VV_CD =:vvcd ";
			sql = "SELECT SCHEME_CD FROM VESSEL_SCHEME WHERE ACCT_NBR=:accNo";
			paramMap.put("vvcd", vvcd);
			log.info("SQL" + sql1.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
			if (rs.next()) {
				schemeName = rs.getString("SCHEME");
			}

			// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08>
			// if (! (schemeName.equals("JLR") || schemeName.equals("JNL") ||
			// schemeName.equals("JBT"))) {
			// add new scheme for LCT, 13.feb.11 by hpeng
			if (!(schemeName.equals("JLR") || schemeName.equals("JNL") || schemeName.equals("JBT")
					|| schemeName.equals("JWP") || schemeName.equals(ProcessChargeConst.LCT_SCHEME))) {
				// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08/>

				paramMap.put("accNo", accNo);
				log.info("SQL" + sql.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
				if (rs1.next()) {
					sch = rs1.getString("SCHEME_CD");
				}
			}
		} catch (NullPointerException e) {
			log.error("Exception getSchemeName :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getSchemeName :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeName  DAO Result:" + sch);
		}
		return sch;
	}

	@Override
	public List<EdoVO> fetchShutoutDNDetail(String strEdoNo, String dnNo) throws BusinessException {
		List<EdoVO> BJDetailsVect = new ArrayList<EdoVO>();
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("fetchShutoutDNDetail DAO START strEdoNo:" + strEdoNo + "dnNo:" + dnNo);
			if (isEsn(null, strEdoNo)) {

				sb.append("SELECT ");
				sb.append("	gb_edo.edo_asn_nbr, ");
				sb.append("	TO_CHAR (dn_details.TRANS_DTTM, ");
				sb.append("	'dd/mm/yyyy hh24:mi') AS trans_dttm, ");
				sb.append("	vessel_call.vsl_nm, ");
				sb.append("	vessel_call.in_voy_nbr, ");
				sb.append("	vessel_call.out_voy_nbr, ");
				sb.append(
						"	TO_CHAR((SELECT MIN(UA.TRANS_DTTM) FROM UA_DETAILS UA, GB_EDO EDO WHERE EDO.ESN_ASN_NBR = UA.ESN_ASN_NBR AND EDO.EDO_ASN_NBR =:strEdoNo ");
				sb.append("), 'DD/MM/YYYY HH24:MI') AS FIRST_UA, ");
				sb.append("	gb_edo.nbr_pkgs AS edoNbr, ");
				sb.append("	dn_details.dn_nbr, ");
				sb.append("	dn_details.NBR_PKGS, ");
				sb.append("	dn_details.DP_IC_NBR, ");
				sb.append("	dn_details.DP_IC_TYPE, ");
				sb.append("	dn_details.DP_NM, ");
				sb.append(
						"	gb_edo.NBR_PKGS - NVL(gb_edo.DN_NBR_PKGS, 0) - NVL(gb_edo.TRANS_DN_NBR_PKGS, 0) AS BALANCE, ");
				sb.append("	gb_edo.dn_nbr_pkgs, ");
				sb.append("	gb_edo.nom_wt, ");
				sb.append("	gb_edo.NOM_VOL, ");
				sb.append("	gb_edo.acct_nbr, ");
				sb.append("	gb_edo.EDO_ASN_NBR, ");
				sb.append("	gb_edo.aa_nm, ");
				sb.append("	gb_edo.WH_IND, ");
				sb.append("	gb_edo.WH_AGGR_NBR, ");
				sb.append("	gb_edo.WH_REMARKS, ");
				sb.append("	gb_edo.ADP_CUST_CD, ");
				sb.append("	gb_edo.ADP_NM, ");
				sb.append("	gb_edo.ADP_IC_TDBCR_NBR, ");
				sb.append("	gb_edo.PAYMENT_MODE, ");
				sb.append("	gb_edo.ACCT_NBR, ");
				sb.append("	gb_edo.NBR_PKGS, ");
				sb.append("	gb_edo.CRG_STATUS, ");
				sb.append("	esn_details.CRG_DES, ");
				sb.append("	ESN_MARKINGS.MARKINGS, ");
				sb.append("	NVL (gb_edo.release_nbr_pkgs, ");
				sb.append("	0) AS releaseOty, ");
				sb.append("	dn_details.billable_ton, ");
				sb.append("	gb_edo.CRG_STATUS AS TRANS_TYPE, ");
				sb.append("	gb_edo.bl_nbr, ");
				sb.append("	gb_edo.var_nbr, ");
				sb.append("	gb_edo.CUT_OFF_NBR_PKGS, ");
				sb.append("	NVL (gb_edo.release_nbr_pkgs, ");
				sb.append("	0) - NVL (gb_edo.trans_dn_nbr_pkgs, ");
				sb.append("	0) AS trnsPkgs, ");
				sb.append("	dn_details.CRG_DEST, ");
				sb.append("	esn_details.nbr_pkgs AS ESNPKGS, ");
				sb.append("	gb_edo.ESN_ASN_NBR, ");
				sb.append("	TO_CHAR(berthing.ATB_DTTM, 'DD/MM/YYYY HH24:MI') AS atb, ");
				sb.append("	vessel_call.terminal, ");
				sb.append("	vessel_call.scheme, ");
				sb.append("	vessel_call.combi_gc_scheme, ");
				sb.append("	vessel_call.combi_gc_ops_ind ");
				sb.append("FROM ");
				sb.append("	gb_edo, ");
				sb.append("	dn_details, ");
				sb.append("	vessel_call, ");
				sb.append("	berthing , ");
				sb.append("	esn_details, ");
				sb.append("	ESN_MARKINGS ");
				sb.append("WHERE ");
				sb.append("	gb_edo.var_nbr = vessel_call.vv_cd ");
				sb.append("	AND vessel_call.vv_cd = berthing.vv_cd ");
				sb.append("	AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr ");
				sb.append("	AND SHIFT_IND = 1 ");
				sb.append("	AND gb_edo.edo_status = 'A' ");
				sb.append("	AND ESN_DETAILS.ESN_ASN_NBR = GB_EDO.ESN_ASN_NBR ");
				sb.append("	AND ESN_MARKINGS.ESN_ASN_NBR = GB_EDO.ESN_ASN_NBR ");
				sb.append("	AND dn_details.DN_NBR =:dnNo ");
				sb.append("	AND GB_EDO.EDO_ASN_NBR =:strEdoNo ");
			} else {

				sb.append("SELECT gb_edo.edo_asn_nbr, ");
				sb.append(" TO_CHAR (dn_details.TRANS_DTTM, 'dd/mm/yyyy hh24:mi') AS trans_dttm, ");
				sb.append(" vessel_call.vsl_nm, ");
				sb.append(" vessel_call.in_voy_nbr, ");
				sb.append(" vessel_call.out_voy_nbr, ");
				sb.append(
						" TO_CHAR((SELECT MIN(UA.TRANS_DTTM) FROM UA_DETAILS UA,GB_EDO EDO WHERE EDO.ESN_ASN_NBR = UA.ESN_ASN_NBR AND EDO.EDO_ASN_NBR = :strEdoNo ");
				sb.append("), 'DD/MM/YYYY HH24:MI') AS FIRST_UA, ");
				sb.append(" gb_edo.nbr_pkgs AS edoNbr, ");
				sb.append(" dn_details.dn_nbr, ");
				sb.append(" dn_details.NBR_PKGS, ");
				sb.append(" dn_details.DP_IC_NBR, ");
				sb.append(" dn_details.DP_IC_TYPE, ");
				sb.append(" dn_details.DP_NM, ");
				sb.append(
						" gb_edo.NBR_PKGS - nvl(gb_edo.DN_NBR_PKGS,0) - nvl(gb_edo.TRANS_DN_NBR_PKGS,0) AS BALANCE, ");
				sb.append(" gb_edo.dn_nbr_pkgs, ");
				sb.append(" gb_edo.nom_wt, ");
				sb.append(" gb_edo.NOM_VOL, ");
				sb.append(" gb_edo.acct_nbr, ");
				sb.append(" gb_edo.EDO_ASN_NBR, ");
				sb.append(" gb_edo.aa_nm, ");
				sb.append(" gb_edo.WH_IND, ");
				sb.append(" gb_edo.WH_AGGR_NBR, ");
				sb.append(" gb_edo.WH_REMARKS, ");
				sb.append(" gb_edo.ADP_CUST_CD, ");
				sb.append(" gb_edo.ADP_NM, ");
				sb.append(" gb_edo.ADP_IC_TDBCR_NBR, ");
				sb.append(" gb_edo.PAYMENT_MODE, ");
				sb.append(" gb_edo.ACCT_NBR, ");
				sb.append(" gb_edo.NBR_PKGS, ");
				sb.append(" gb_edo.CRG_STATUS, ");
				sb.append(" TESN_PSA_JP.CRG_DES, ");
				sb.append(" ESN_MARKINGS.MARKINGS, ");
				sb.append(" NVL (gb_edo.release_nbr_pkgs, 0) AS releaseOty, ");
				sb.append(" dn_details.billable_ton, gb_edo.CRG_STATUS AS TRANS_TYPE, ");
				sb.append(" gb_edo.bl_nbr, gb_edo.var_nbr, gb_edo.CUT_OFF_NBR_PKGS, ");
				sb.append(" NVL (gb_edo.release_nbr_pkgs, 0) - NVL (gb_edo.trans_dn_nbr_pkgs, 0) AS trnsPkgs, ");
				sb.append(" dn_details.CRG_DEST, ");
				sb.append(" gb_edo.ESN_ASN_NBR, ");
				sb.append(" TESN_PSA_JP.nbr_pkgs AS ESNPKGS, ");
				sb.append(" TO_CHAR(berthing.ATB_DTTM,'DD/MM/YYYY HH24:MI') as atb, ");
				sb.append(
						" vessel_call.terminal, vessel_call.scheme, vessel_call.combi_gc_scheme,  vessel_call.combi_gc_ops_ind ");
				sb.append(" FROM ");
				sb.append(" gb_edo, dn_details, vessel_call, berthing , TESN_PSA_JP, ESN_MARKINGS ");
				sb.append(" WHERE ");
				sb.append(" gb_edo.var_nbr = vessel_call.vv_cd ");
				sb.append(" AND vessel_call.vv_cd = berthing.vv_cd ");
				sb.append(" AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr ");
				sb.append(" AND SHIFT_IND = 1 AND gb_edo.edo_status = 'A' ");
				sb.append(" AND TESN_PSA_JP.ESN_ASN_NBR = GB_EDO.ESN_ASN_NBR ");
				sb.append(" AND ESN_MARKINGS.ESN_ASN_NBR = GB_EDO.ESN_ASN_NBR ");
				sb.append(" AND dn_details.DN_NBR =:dnNo ");
				sb.append(" AND GB_EDO.EDO_ASN_NBR =:strEdoNo ");
			}

			paramMap.put("strEdoNo", strEdoNo);
			paramMap.put("dnNo", dnNo);
			paramMap.put("strEdoNo", strEdoNo);
			log.info("SQL" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				String cuurnetdate = CommonUtility.deNull(rs.getString("trans_dttm"));
				String edoasnnbr = CommonUtility.deNull(rs.getString("edo_asn_nbr"));
				String vName = CommonUtility.deNull(rs.getString("vsl_nm"));
				String outVoyNbr = CommonUtility.deNull(rs.getString("out_voy_nbr"));
				String transType = CommonUtility.deNull(rs.getString("TRANS_TYPE"));
				edoasnnbr = CommonUtility.deNull(rs.getString("EDO_ASN_NBR"));
				String esnAsnNbr = CommonUtility.deNull(rs.getString("ESN_ASN_NBR"));
				String declaredWt = CommonUtility.deNull(rs.getString("nom_wt"));
				String declaredVol = CommonUtility.deNull(rs.getString("NOM_VOL"));
				String declaredQty = CommonUtility.deNull(rs.getString("edoNbr"));
				String transQty = CommonUtility.deNull(rs.getString("NBR_PKGS"));
				String nricNo = CommonUtility.deNull(rs.getString("DP_IC_NBR"));
				String icType = CommonUtility.deNull(rs.getString("DP_IC_TYPE"));
				String name = CommonUtility.deNull(rs.getString("DP_NM"));
				String accNo = CommonUtility.deNull(rs.getString("acct_nbr"));
				String vvcd = CommonUtility.deNull(rs.getString("var_nbr"));
				String SchemeName = getSchemeName(accNo, vvcd);
				String crgDes = CommonUtility.deNull(rs.getString("CRG_DES"));
				String firstUa = CommonUtility.deNull(rs.getString("FIRST_UA"));
				String balance = CommonUtility.deNull(rs.getString("BALANCE"));
				String whInd = CommonUtility.deNull(rs.getString("WH_IND"));
				String adpNm = CommonUtility.deNull(rs.getString("ADP_NM"));
				String adpIcNbr = CommonUtility.deNull(rs.getString("ADP_IC_TDBCR_NBR"));
				String adpcustcd = CommonUtility.deNull(rs.getString("ADP_CUST_CD"));
				if (adpcustcd != null) {

					sb.setLength(0);

					sb.append("SELECT ");
					sb.append("	CC.CO_NM, ");
					sb.append("	( ");
					sb.append("	SELECT ");
					sb.append(
							"		DECODE(TDB_CR_NBR, NULL, UEN_NBR, TDB_CR_NBR FROM customer WHERE cust_cd =:adpcustcd) TDB_CR_NBR ");
					sb.append("	FROM ");
					sb.append("		CUSTOMER, ");
					sb.append("		COMPANY_CODE CC ");
					sb.append("	WHERE ");
					sb.append("		CUST_CD = CC.CO_CD ");
					sb.append("		AND cust_cd =:adpcustcd)TDB_CR_NBR ");
					sb.append("FROM ");
					sb.append("	CUSTOMER, ");
					sb.append("	COMPANY_CODE CC ");
					sb.append("WHERE ");
					sb.append("	CUST_CD = CC.CO_CD ");
					sb.append("	AND cust_cd =:adpcustcd");
					paramMap.put("adpcustcd", adpcustcd);
					log.info("SQL" + sb.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					if (rs1.next()) {
						adpNm = CommonUtility.deNull(rs1.getString("co_nm"));
						adpIcNbr = CommonUtility.deNull(rs1.getString("TDB_CR_NBR"));
					}

				}
				String payNbr = rs.getString("PAYMENT_MODE");
				String billparty = "";
				String accNbr = rs.getString("ACCT_NBR");
				if (payNbr.equals("A")) {
					sb.setLength(0);
					sb.append(
							" SELECT C.CO_NM FROM CUST_ACCT CA,COMPANY_CODE C WHERE CA.CUST_CD=C.CO_CD AND CA.ACCT_NBR=:accNbr  ");
					paramMap.put("accNbr", accNbr);
					log.info("SQL" + sb.toString());
					rs2 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					if (rs2.next()) {
						billparty = CommonUtility.deNull(rs2.getString(1));
						payNbr = accNbr;
					}

				}
				String declPkgs = rs.getString("NBR_PKGS");
				String whAggrNbr = CommonUtility.deNull(rs.getString("WH_AGGR_NBR"));
				String whRemarks = CommonUtility.deNull(rs.getString("WH_REMARKS"));
				String crgDesc = "";
				if ("O".equalsIgnoreCase(crgDes))
					crgDesc = "Out Of JP";
				else if ("V".equalsIgnoreCase(crgDes))
					crgDesc = "Vessel";
				else if ("L".equalsIgnoreCase(crgDes))
					crgDesc = "Leased Area";

				double nom_wt = 0;
				nom_wt = Double.parseDouble(declaredWt);
				double nom_vol = 0;
				double Bill_ton = 0;
				nom_vol = Double.parseDouble(declaredVol);

				if ((nom_wt / 1000) > nom_vol) {
					Bill_ton = nom_wt / 1000;
				} else {
					Bill_ton = nom_vol;
				}
				String billableUnit = "" + Bill_ton;
				String crgStatus = CommonUtility.deNull(rs.getString("CRG_STATUS"));
				String markings = CommonUtility.deNull(rs.getString("MARKINGS"));
				String atb = rs.getString("atb");

				EdoVO edoVo = new EdoVO();
				edoVo.setTransDate(cuurnetdate);
				edoVo.setVslName(vName);
				edoVo.setInVoyNbr(outVoyNbr);
				edoVo.setOutVoyNbr(outVoyNbr);
				edoVo.setNomWt(declaredWt);
				edoVo.setNomVol(declaredVol);
				edoVo.setNoPkgs(declaredQty);
				edoVo.setDeliveredPkgs(transQty);
				edoVo.setAAIcNbr(nricNo);
				edoVo.setAAName(name);
				edoVo.setAACustCD(icType);
				edoVo.setAcctNo(accNo);
				edoVo.setBillTon(billableUnit);
				edoVo.setEdoAsnNbr(edoasnnbr);
				edoVo.setTransType(transType);
				edoVo.setCrgDestination(crgDesc);
				edoVo.setFirstUa(firstUa);
				edoVo.setCOD(firstUa);
				edoVo.setWhInd(whInd);
				int temp_balance = Integer.parseInt(balance);
				if (temp_balance < 0)
					balance = "0";
				edoVo.setBalance(balance);
				edoVo.setDnStatus(SchemeName);

				edoVo.setBlNbr(esnAsnNbr);
				edoVo.setConsName("");
				edoVo.setAdpNm(adpNm);
				edoVo.setAdpIcNbr(adpIcNbr);
				edoVo.setCaName("");
				edoVo.setPayMode(payNbr);
				edoVo.setBillParty(billparty);
				edoVo.setDeclPkgs(declPkgs);
				edoVo.setWhAggrNbr(whAggrNbr);
				edoVo.setWhRemarks(whRemarks);
				edoVo.setCrgStatus(crgStatus);
				edoVo.setCrgDes(crgDes);
				edoVo.setMarkings(markings);
				edoVo.setATB(atb);
				edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				BJDetailsVect.add(edoVo);

			}
		} catch (NullPointerException e) {
			log.error("Exception fetchShutoutDNDetail :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception fetchShutoutDNDetail :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END fetchShutoutDNDetail DAO Result:" + (BJDetailsVect != null ? BJDetailsVect.toString() : ""));
		}
		return BJDetailsVect;
	}

	public int getSpencialPackage(String edoNbr) throws BusinessException {
		int pkgs = 0;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getSpencialPackage edoNbr:" + edoNbr);
			sb.append("  select b.SPECIAL_ACTION_PKGS from gb_edo a, manifest_details b where a.mft_seq_nbr= ");
			sb.append(" b.mft_seq_nbr and a.EDO_ASN_NBR=:edoNbr and a.CRG_STATUS='L' ");

			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				pkgs = rs.getInt("SPECIAL_ACTION_PKGS");
			}
		} catch (NullPointerException e) {
			log.error("Exception getSpencialPackage :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getSpencialPackage :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSpencialPackage  DAO  Result:" + pkgs);
		}
		return pkgs;
	}

	@Override
	public List<EdoValueObjectOps> fetchSubAdpDetails(String edoNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		List<EdoValueObjectOps> subAdpVect = new ArrayList<EdoValueObjectOps>();
		try {
			log.info("START: fetchSubAdpDetails edoNbr:" + edoNbr);
			sb.append(" select TRUCKER_IC, TRUCKER_NM from SUB_ADP where ESN_ASN_NBR =:edoNbr ");
			sb.append(" and EDO_ESN_IND = 1 and STATUS_CD ='A' order by SUB_ADP_NBR ");

			paramMap.put("edoNbr", edoNbr);
			log.info("fetchSubAdpDetails SQL" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			String truckerIc;
			String trcukerNm;
			int count = 0;
			while (rs.next()) {

				truckerIc = rs.getString("TRUCKER_IC");
				trcukerNm = rs.getString("TRUCKER_NM");

				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo.setVech1(count++ + "");
				edoVo.setVech2(truckerIc);
				edoVo.setVech3(trcukerNm);
				subAdpVect.add(edoVo);
			}
		} catch (NullPointerException e) {
			log.error("Exception fetchSubAdpDetails :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception fetchSubAdpDetails :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchSubAdpDetails  DAO  Result:" + subAdpVect.size());
		}
		return subAdpVect;
	}

	@Override
	public List<EdoValueObjectOps> fetchDNList(String edoNbr, String searchcrg, String tesn_nbr)
			throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: fetchDNList edoNbr:" + edoNbr + "searchcrg:" + searchcrg + "tesn_nbr:" + tesn_nbr);
			boolean checkEdoStuff = chkEDOStuffing(edoNbr);
			if ("ALL".equalsIgnoreCase(searchcrg)) {
				sb.append(
						"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, CNTR_NBR, GOE.LANE_ID LANE_NBR, u.USER_NAME LAST_MODIFY_USER_ID, DN.TRUCK_NBR   ");
				sb.append(" FROM DN_DETAILS DN LEFT JOIN  (");
				sb.append("   SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF  FROM GATE_OUT_EVENT_DETAILS ");
				sb.append("   GROUP BY TRANS_REF  ) GOD  ON DN.DN_NBR = GOD.TRANS_REF ");
				sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
				sb.append(" LEFT JOIN adm_user u ON u.USER_ACCT =  GOE.GATE_STAFF_ID ");
				sb.append(" WHERE EDO_ASN_NBR=:edoNbr order by dn_nbr");
				sql = sb.toString();

			} else if ((searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT"))) {
				sb.append(
						"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, CNTR_NBR, GOE.LANE_ID LANE_NBR, u.USER_NAME LAST_MODIFY_USER_ID,DN.TRUCK_NBR   ");
				sb.append(" FROM DN_DETAILS DN  LEFT JOIN  (");
				sb.append("   SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF  FROM GATE_OUT_EVENT_DETAILS ");
				sb.append("   GROUP BY TRANS_REF  ) GOD  ON DN.DN_NBR = GOD.TRANS_REF ");
				sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
				sb.append(" LEFT JOIN adm_user u ON u.USER_ACCT =  GOE.GATE_STAFF_ID ");
				sb.append(" WHERE EDO_ASN_NBR=:edoNbr and tesn_asn_nbr is null order by dn_nbr");
				sql = sb.toString();
			}

			else {
				if (checkEdoStuff) {
					sb.append(
							"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, CNTR_NBR, GOE.LANE_ID LANE_NBR, u.USER_NAME LAST_MODIFY_USER_ID,DN.TRUCK_NBR   ");
					sb.append(" FROM DN_DETAILS DN LEFT JOIN  (");
					sb.append("   SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF   FROM GATE_OUT_EVENT_DETAILS ");
					sb.append("   GROUP BY TRANS_REF  ) GOD  ON DN.DN_NBR = GOD.TRANS_REF ");
					sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
					sb.append(" LEFT JOIN adm_user u ON u.USER_ACCT =  GOE.GATE_STAFF_ID ");
					sb.append(" WHERE EDO_ASN_NBR=:edoNbr and tesn_asn_nbr=:tesnNbr order by dn_nbr");
					sql = sb.toString();
				} else {
					sb.append(
							"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, CNTR_NBR, GOE.LANE_ID LANE_NBR,  u.USER_NAME LAST_MODIFY_USER_ID, DN.TRUCK_NBR  ");
					sb.append(" FROM DN_DETAILS DN LEFT JOIN  (");
					sb.append("   SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF   FROM GATE_OUT_EVENT_DETAILS ");
					sb.append("   GROUP BY TRANS_REF  ) GOD  ON DN.DN_NBR = GOD.TRANS_REF ");
					sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
					sb.append(" LEFT JOIN adm_user u ON u.USER_ACCT =  GOE.GATE_STAFF_ID ");
					sb.append("  WHERE EDO_ASN_NBR=:edoNbr and tesn_asn_nbr=:tesnNbr order by dn_nbr");
					sql = sb.toString();
				}
			}
			// End Changed by Irene Tan on 20 Oct 2004 : SL-GBMS-20041020-1

			paramMap.put("edoNbr", edoNbr);
			if ("ALL".equalsIgnoreCase(searchcrg)) {
				paramMap.put("edoNbr", edoNbr);

			} else if ((searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT"))) {
				paramMap.put("edoNbr", edoNbr);
			} else {
				paramMap.put("edoNbr", edoNbr);
				paramMap.put("tesnNbr", tesn_nbr);

			}
			log.info("SQL" + sql.toString() + ",paramMap:" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			String dnNbr;
			String noPkgs;
			String billTon;
			String dnStatus;
			String trans = "";
			String billStatus;
			String cntrNo = "";
			while (rs.next()) {
				dnNbr = rs.getString("DN_NBR");
				noPkgs = rs.getString("NBR_PKGS");
				billTon = rs.getString("BILLABLE_TON");
				dnStatus = rs.getString("DN_STATUS");
				trans = rs.getString("trnsDate");
				billStatus = rs.getString("BILL_STATUS");
				cntrNo = rs.getString("CNTR_NBR");

				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo.setDnNbr(dnNbr);
				edoVo.setNoPkgs(noPkgs);
				edoVo.setBillTon(billTon);
				edoVo.setDnStatus(dnStatus);
				edoVo.setTransDate(trans);
				edoVo.setBillStatus(billStatus);
				edoVo.setCntrNo(cntrNo);
				edoVo.setLane_nbr(CommonUtility.deNull(rs.getString("LANE_NBR")));
				edoVo.setLast_modify_user_id(CommonUtility.deNull(rs.getString("LAST_MODIFY_USER_ID")));
				edoVo.setVech1(CommonUtility.deNull(rs.getString("TRUCK_NBR")));
				BJDetailsVect.add(edoVo);
			}
		} catch (NullPointerException e) {
			log.error("Exception fetchDNList :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception fetchDNList :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchDNList  DAO  Result:" + (BJDetailsVect != null ? BJDetailsVect.toString() : ""));
		}

		return BJDetailsVect;
	}

	public String chktesnJpJp_nbr(String esn_asnNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		String tesnjpjp = "N";
		try {
			log.info("START: chktesnJpJp_nbr esn_asnNbr:" + esn_asnNbr);
			sql = "select * from tesn_jp_jp jpjp,esn e where e.esn_asn_nbr = jpjp.esn_asn_nbr and e.esn_status ='A' and jpjp.ESN_ASN_NBR =:esn_asnNbr and e.trans_crg != 'Y'";

			paramMap.put("esn_asnNbr", CommonUtility.deNull(esn_asnNbr));
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				tesnjpjp = "Y";
			}
		} catch (NullPointerException e) {
			log.error("Exception chktesnJpJp_nbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception chktesnJpJp_nbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chktesnJpJp_nbr  DAO  Result:" + tesnjpjp);
		}
		return tesnjpjp;
	}

	private String getCTDNnbr(String mftsqnbr) throws BusinessException {
		String total = "";
		StringBuffer sql = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCTDNnbr mftsqnbr:" + mftsqnbr);
			sql.append("select sum(nvl(DN_NBR_PKGS,0)+nvl(CUT_OFF_NBR_PKGS,0)+nvl(trans_dn_nbr_pkgs,0))");
			sql.append(" from gb_edo where MFT_SEQ_NBR =:mftSeqNbr ");

			paramMap.put("mftSeqNbr", mftsqnbr);
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			while (rs.next()) {
				total = rs.getString(1);
			}
		} catch (NullPointerException e) {
			log.error("Exception getCTDNnbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getCTDNnbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCTDNnbr  DAO  Result:" + total);
		}
		return total;
	}

	private String getEDOCTDNnbr(String mftsqnbr) throws BusinessException {
		String total = "";
		StringBuffer sql = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getEDOCTDNnbr mftsqnbr:" + mftsqnbr);
			sql.append("select sum(nvl(trans_dn_nbr_pkgs,0)+nvl(CUT_OFF_NBR_PKGS,0)+nvl(DN_NBR_PKGS,0))");
			sql.append(" from gb_edo where MFT_SEQ_NBR =:mftSeqNbr ");

			paramMap.put("mftSeqNbr", mftsqnbr);
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			while (rs.next()) {
				total = rs.getString(1);
			}
		} catch (NullPointerException e) {
			log.error("Exception getEDOCTDNnbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getEDOCTDNnbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEDOCTDNnbr  DAO  Result:" + total);
		}
		return total;
	}

	@Override
	public List<EdoValueObjectOps> fetchEdoDetails(String edoNbr, String searchcrg, String tesnnbr)
			throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: fetchEdoDetails edoNbr:" + edoNbr + "searchcrg:" + searchcrg + "tesnnbr:" + tesnnbr);

			boolean checkEdoStuff = chkEDOStuffing(edoNbr); // vinayak added 07 jan 2004
			String chktesnJpJp_nbr = chktesnJpJp_nbr(tesnnbr);
			String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesnnbr);
			
			log.info("checkEdoStuff" + checkEdoStuff);
			log.info("chktesnJpJp_nbr" + chktesnJpJp_nbr);
			log.info("chktesnJpPsa_nbr" + chktesnJpPsa_nbr);
			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
				/*
				 * sql =
				 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
				 * ; sql = sql +
				 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
				 * ; sql = sql +
				 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, to_char(c.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
				 * ; sql = sql +
				 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0)+nvl(a.trans_dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e where "
				 * ; sql = sql +
				 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and a.EDO_ASN_NBR ="
				 * + edoNbr.trim();
				 */

				/*
				 * sql =
				 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
				 * ; sql = sql +
				 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
				 * ; sql = sql +
				 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,f.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
				 * ; sql = sql +
				 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0)+nvl(a.trans_dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,cc_unstuff_manifest f where "
				 * ; sql = sql +
				 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr AND d.unstuff_seq_nbr=f.UNSTUFF_SEQ_NBR(+) and a.EDO_ASN_NBR ="
				 * + edoNbr.trim();
				 */

				sql = "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,";
				sql = sql
						+ " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,";
				sql = sql
						+ " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,f.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,";
				sql = sql
						+ " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0)+nvl(a.trans_dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,a.mft_seq_nbr as mftsqnbr, a.wh_ind, a.wh_aggr_nbr, a.wh_remarks, b.terminal, b.scheme,b.combi_gc_scheme, b.combi_gc_ops_ind from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,cc_unstuff_manifest f where ";
				sql = sql
						+ " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr AND d.unstuff_seq_nbr=f.UNSTUFF_SEQ_NBR(+) and a.EDO_ASN_NBR =:edoNbr";

				// log.info("sql :"+sql);
			} else {
				// if(checkjpjp && checkjppsa){
				if (chktesnJpJp_nbr.equals("Y")) {
					/*
					 * sql =
					 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
					 * ; sql = sql +
					 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
					 * ; sql = sql +
					 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, to_char(c.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
					 * ; sql = sql +
					 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
					 * ; sql = sql +
					 * " f.NBR_PKGS as jpjpnpkg,f.DN_NBR_PKGS as jpjpdn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_jp f where"
					 * ; sql = sql +
					 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
					 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
					 */

					/*
					 * sql =
					 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
					 * ; sql = sql +
					 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
					 * ; sql = sql +
					 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
					 * ; sql = sql +
					 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
					 * ; sql = sql +
					 * " f.NBR_PKGS as jpjpnpkg,f.DN_NBR_PKGS as jpjpdn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_jp f, cc_unstuff_manifest g where"
					 * ; sql = sql +
					 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
					 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
					 */

					sql = "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,";
					sql = sql
							+ " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,";
					sql = sql
							+ " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,";
					sql = sql
							+ " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,";
					sql = sql
							+ " f.NBR_PKGS as jpjpnpkg,f.DN_NBR_PKGS as jpjpdn_npkg,a.mft_seq_nbr as mftsqnbr , a.wh_ind, a.wh_aggr_nbr, a.wh_remarks, b.terminal, b.scheme,b.combi_gc_scheme, b.combi_gc_ops_ind  from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_jp f, cc_unstuff_manifest g where";
					sql = sql
							+ " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr =:tesnNbr and a.EDO_ASN_NBR =:edoNbr";

					// log.info("sql :"+sql);
				} else if (chktesnJpPsa_nbr.equals("Y")) {
					/*
					 * sql =
					 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
					 * ; sql = sql +
					 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
					 * ; sql = sql +
					 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, to_char(c.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
					 * ; sql = sql +
					 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
					 * ; sql = sql +
					 * " f.NBR_PKGS as jppsanpkg,f.DN_NBR_PKGS as jppsadn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_psa f where"
					 * ; sql = sql +
					 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
					 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
					 */
					/*
					 * sql =
					 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
					 * ; sql = sql +
					 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
					 * ; sql = sql +
					 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
					 * ; sql = sql +
					 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
					 * ; sql = sql +
					 * " f.NBR_PKGS as jppsanpkg,f.DN_NBR_PKGS as jppsadn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_psa f,cc_unstuff_manifest g where"
					 * ; sql = sql +
					 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
					 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
					 */
					sql = "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,";
					sql = sql
							+ " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,";
					sql = sql
							+ " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,";
					sql = sql
							+ " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,";
					sql = sql
							+ " f.NBR_PKGS as jppsanpkg,f.DN_NBR_PKGS as jppsadn_npkg,a.mft_seq_nbr as mftsqnbr, a.wh_ind, a.wh_aggr_nbr, a.wh_remarks, b.terminal, b.scheme,b.combi_gc_scheme, b.combi_gc_ops_ind  from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_psa f,cc_unstuff_manifest g where";
					sql = sql
							+ " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr =:tesnNbr and a.EDO_ASN_NBR =:edoNbr";

					// log.info("sql :"+sql);
				}
				// vinayak added on 8 jan 2004
				else if (checkEdoStuff) {

					sb.append(
							"SELECT a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,");
					sb.append(
							"b.vsl_nm , b.in_voy_nbr ,TO_CHAR(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') AS atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') AS cod,NVL(d.CRG_DES,' '),");
					sb.append(
							"NVL(e.mft_markings,'') ,a.nbr_pkgs,a.nbr_pkgs - a.trans_nbr_pkgs - NVL(a.release_nbr_pkgs,0)  - NVL(a.dn_nbr_pkgs,0) ,NVL(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,NVL(a.nbr_pkgs,0) - NVL(a.release_nbr_pkgs,0) - NVL(a.trans_dn_nbr_pkgs,0) - NVL(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,NVL(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,");
					sb.append(
							"a.trans_nbr_pkgs AS transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS AS mftNbr,csd.NBR_PKGS AS stuffnpkg,csd.DN_NBR_PKGS AS stuffdnnpkg,a.mft_seq_nbr AS mftsqnbr,csd.STUFF_SEQ_NBR stuffseq, a.wh_ind, a.wh_aggr_nbr, a.wh_remarks, b.terminal, b.scheme,b.combi_gc_scheme, b.combi_gc_ops_ind  FROM gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,cc_stuffing_details csd,cc_stuffing cs,cc_unstuff_manifest g WHERE a.var_nbr =  b.vv_cd AND ");
					sb.append(
							"b.vv_cd = c.vv_cd  AND d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) AND SHIFT_IND = 1 AND a.edo_status='A' AND a.mft_seq_nbr = d.mft_seq_nbr AND d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND cs.STUFF_CLOSED='Y' AND csd.REC_STATUS='A' AND cs.ACTIVE_STATUS='A' AND csd.EDO_ESN_NBR = a.EDO_ASN_NBR AND csd.EDO_ESN_IND='EDO' AND cs.STUFF_SEQ_NBR=csd.STUFF_SEQ_NBR AND csd.STUFF_SEQ_NBR =:tesnNbr AND a.EDO_ASN_NBR =:edoNbr");
					sql = sb.toString();
					// end changed by Irene Tan on 17 Feb 2004

					// log.info("sql for checkEdoStuff :"+sql);
				}
			}

			paramMap.put("edoNbr", edoNbr.trim());
			if (chktesnJpJp_nbr.equals("Y") || checkEdoStuff || chktesnJpPsa_nbr.equals("Y")) {
				paramMap.put("tesnNbr", tesnnbr);
			}
			log.info("SQL" + sql.toString() + ",paramMap:" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			String blNbr;
			String accNbr;
			String payNbr;
			String nomWt;
			String nomVol;
			String adpcustcd;
			String adpIcNbr;
			String adpNm;
			String caCustcd;
			String caIcNbr;
			String caNm;
			String aaNm;
			String aaCustCd;
			String aaIcNbr;
			String vslName;
			String atb;
			String cod;
			String crgDes;
			String declPkgs;
			String balance;
			String dnPkgs;
			String crgStatus;
			String mftmarkings;
			String invoyNbr;
			String outvoyNbr;
			String consname;
			String billparty = "";
			String maxQty = "";
			String transNbr = "";
			String transLocalCheck = "";
			String mftNbr = "";
			String mftSqNbr = "";
			String sumCtDNnbr = "";
			String sumEdoCtDNnbr = "";
			// Warehouse details

			String whInd = "";
			String whAggrNbr = "";
			String whRemarks = "";
			while (rs.next()) {
				blNbr = rs.getString("BL_NBR");
				edoNbr = rs.getString("edo_asn_nbr");
				accNbr = rs.getString("ACCT_NBR");
				payNbr = rs.getString("PAYMENT_MODE");
				nomWt = rs.getString("NOM_WT");
				nomVol = rs.getString("NOM_VOL");
				crgDes = rs.getString("crg_des");
				adpcustcd = rs.getString("ADP_CUST_CD");
				adpIcNbr = rs.getString("ADP_IC_TDBCR_NBR");
				adpNm = rs.getString("ADP_NM");
				caCustcd = rs.getString("CA_CUST_CD");
				caIcNbr = rs.getString("CA_IC_TDBCR_NBR");
				caNm = rs.getString("CA_NM");
				aaNm = rs.getString("aa_nm");
				aaCustCd = rs.getString("AA_CUST_CD");
				aaIcNbr = rs.getString("AA_IC_TDBCR_NBR");
				vslName = rs.getString("vsl_nm");
				atb = rs.getString(18);
				cod = rs.getString(19);
				crgDes = rs.getString(20);
				mftmarkings = rs.getString(21);
				declPkgs = rs.getString("nbr_pkgs");
				balance = rs.getString(23);
				dnPkgs = rs.getString(24);
				crgStatus = rs.getString("CRG_STATUS");
				invoyNbr = rs.getString("in_voy_nbr");
				outvoyNbr = rs.getString("out_voy_nbr");
				consname = rs.getString("CONS_NM");
				maxQty = rs.getString(30);
				transNbr = rs.getString("transNbr");
				transLocalCheck = rs.getString(28);
				mftNbr = rs.getString("mftNbr");
				mftSqNbr = rs.getString("mftsqnbr");
				sumCtDNnbr = getCTDNnbr(mftSqNbr);
				sumEdoCtDNnbr = getEDOCTDNnbr(mftSqNbr);
				// Warehosue details
				whInd = CommonUtility.deNull(rs.getString("WH_IND"));
				whAggrNbr = CommonUtility.deNull(rs.getString("WH_AGGR_NBR"));
				whRemarks = CommonUtility.deNull(rs.getString("WH_REMARKS"));

				if (payNbr.equals("A")) {
					StringBuffer sql1 = new StringBuffer();
					sql1.append(
							"SELECT C.CO_NM FROM CUST_ACCT CA,COMPANY_CODE C WHERE CA.CUST_CD=C.CO_CD AND CA.ACCT_NBR=:accNbr");

					paramMap.put("accNbr", accNbr);
					log.info("SQL" + sql1.toString() + "pstmt:");
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);

					if (rs1.next()) {
						billparty = rs1.getString(1);
						payNbr = accNbr;
					}

				}
				if (adpcustcd != null) {
					// Punitha - UEN Enhancement
					// SqlRowSet rs1 = sqlstmt1.executeQuery("SELECT CC.CO_NM,CUSTOMER.TDB_CR_NBR
					// FROM CUSTOMER, COMPANY_CODE CC WHERE CUSTOMER.CUST_CD=CC.CO_CD AND
					// CUSTOMER.CUST_CD='" + adpcustcd + "'");
					StringBuffer sql1 = new StringBuffer();
					sql1.append(" SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) from customer ");
					sql1.append(
							" where cust_cd=:adpcustcd) TDB_CR_NBR FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd =:adpcustcd ");

					paramMap.put("adpcustcd", adpcustcd);
					log.info("SQL" + sql1.toString() + "pstmt:");
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);

					if (rs1.next()) {
						adpNm = rs1.getString("co_nm");
						adpIcNbr = rs1.getString("TDB_CR_NBR");
					}

				}

				if (caCustcd != null) {
					// Punitha - UEN Enhancement
					// SqlRowSet rs1 = sqlstmt1.executeQuery("SELECT CC.CO_NM,CUSTOMER.TDB_CR_NBR
					// FROM CUSTOMER, COMPANY_CODE CC WHERE CUSTOMER.CUST_CD=CC.CO_CD AND
					// CUSTOMER.CUST_CD='" + caCustcd + "'");
					StringBuffer sql1 = new StringBuffer();
					sql1.append(" SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR)  ");
					sql1.append(
							" from customer where cust_cd =:caCustcd) TDB_CR_NBR FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd =:caCustcd ");

					paramMap.put("caCustcd", caCustcd);
					log.info("SQL" + sql1.toString() + "pstmt:");
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);

					if (rs1.next()) {
						caNm = rs1.getString("co_nm");
						caIcNbr = rs1.getString("TDB_CR_NBR");
					}
				}

				if (aaCustCd != null) {
					// Punitha - UEN Enhancement
					// SqlRowSet rs1 = sqlstmt1.executeQuery("SELECT CC.CO_NM,CUSTOMER.TDB_CR_NBR
					// FROM CUSTOMER, COMPANY_CODE CC WHERE CUSTOMER.CUST_CD=CC.CO_CD AND
					// CUSTOMER.CUST_CD='" + aaCustCd + "'");
					StringBuffer sql1 = new StringBuffer();
					sql1.append(
							" SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) from customer where cust_cd =:aaCustCd ");
					sql1.append(
							" ) TDB_CR_NBR FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd =:aaCustCd ");

					paramMap.put("aaCustCd", aaCustCd);
					log.info("SQL" + sql1.toString() + "pstmt:");
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);

					if (rs1.next()) {
						aaNm = rs1.getString("co_nm");
						aaIcNbr = rs1.getString("TDB_CR_NBR");
					}
				}
				if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
					String bal_cutoff = rs.getString("CUT_OFF_NBR_PKGS");
					String bal_short_land = rs.getString("NBR_PKGS_IN_PORT");
					int con3 = 0;
					int con5 = 0;
					if (bal_cutoff != null && !bal_cutoff.equals(""))
						con3 = Integer.parseInt(bal_cutoff);
					if (bal_short_land != null && !bal_short_land.equals(""))
						con5 = Integer.parseInt(bal_short_land);
					int relPkgsNbr = Integer.parseInt(declPkgs) - Integer.parseInt(maxQty)
							- Integer.parseInt(transLocalCheck) - Integer.parseInt(dnPkgs);
					int edoNbrPkgs = Integer.parseInt(declPkgs) - Integer.parseInt(maxQty)
							- Integer.parseInt(transLocalCheck) - Integer.parseInt(dnPkgs) - con3;
					int blPkgs = 0;
					blPkgs = Integer.parseInt(mftNbr) - (con5 + Integer.parseInt(sumEdoCtDNnbr));
					// int blPkgs = 0;
					// blPkgs = Integer.parseInt(declPkgs) - Integer.parseInt(sumEdoCtDnNbr);
					// int bal4 = con4 - con3;
					// if(blPkgs >= bal4)
					// bal4 = bal4;
					// else
					// bal4 = blPkgs;
					// balance = String.valueOf(bal4) ;

					if (chktesnJpJp_nbr.equals("Y")) {
						String bal1 = rs.getString("jpjpnpkg");
						String bal2 = rs.getString("jpjpdn_npkg");
						dnPkgs = bal2;
						declPkgs = bal1;
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);

						int bal3 = con1 - con2;

						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr < bal3)
									bal3 = relPkgsNbr - con3;
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;
						balance = String.valueOf(bal3);
					} else if (chktesnJpPsa_nbr.equals("Y")) {
						String bal1 = rs.getString("jppsanpkg");
						String bal2 = rs.getString("jppsadn_npkg");
						dnPkgs = bal2;
						declPkgs = bal1;
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr < bal3)
									bal3 = relPkgsNbr - con3;
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;

						balance = String.valueOf(bal3);
					}
					// added by vinayak on 8 jan 2004
					else if (checkEdoStuff) {
						String bal1 = rs.getString("stuffnpkg");
						String bal2 = rs.getString("stuffdnnpkg");
						dnPkgs = bal2; // dnPkgs=gb_edo.dn_nbr_pkgs
						declPkgs = bal1; // declPkgs=gb_edo.nbr_pkgs
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						/*
						 * if (blPkgs >= bal3) { //blPkgs=manifest_details.NBR_PKGS -
						 * (manifest_details.NBR_PKGS_IN_PORT + select
						 * sum(nvl(trans_dn_nbr_pkgs,0)+nvl(CUT_OFF_NBR_PKGS,0)+nvl(DN_NBR_PKGS,0)) from
						 * gb_edo where MFT_SEQ_NBR = '") if (edoNbrPkgs >= bal3)
						 * //edoNbrPkgs=gb_edo.nbr_pkgs - gb_edo.release_nbr_pkgs -
						 * gb_edo.trans_dn_nbr_pkgs - gb_edo.dn_nbr_pkgs - gb_edo.CUT_OFF_NBR_PKGS { if
						 * (relPkgsNbr >= bal3) //relPkgsNbr=gb_edo.nbr_pkgs - gb_edo.release_nbr_pkgs -
						 * gb_edo.trans_dn_nbr_pkgs - gb_edo.dn_nbr_pkgs bal3 = bal3; else bal3 =
						 * relPkgsNbr - con3; } else bal3 = edoNbrPkgs; } else bal3 = blPkgs;
						 */
						balance = String.valueOf(bal3);
						// log.info("*****balance fetchEdoDetails() :"+balance);
					}
				} else {
					String bal_cutoff = rs.getString("CUT_OFF_NBR_PKGS");
					String bal_short_land = rs.getString("NBR_PKGS_IN_PORT");
					int con3 = 0;
					int con4 = 0;
					int con5 = 0;

					if (bal_cutoff != null && !bal_cutoff.equals(""))
						con3 = Integer.parseInt(bal_cutoff);
					if (balance != null && !balance.equals(""))
						con4 = Integer.parseInt(balance);
					if (bal_short_land != null && !bal_short_land.equals(""))
						con5 = Integer.parseInt(bal_short_land);
					int blPkgs = 0;
					blPkgs = Integer.parseInt(mftNbr) - (con5 + Integer.parseInt(sumCtDNnbr));

					int relPkgsNbr = Integer.parseInt(declPkgs) - Integer.parseInt(maxQty)
							- Integer.parseInt(transLocalCheck) - Integer.parseInt(dnPkgs);

					int bal4 = con4 - con3;

					if (aaNm != null && !aaNm.equals("")) {
						bal4 = bal4 + Integer.parseInt(maxQty);
						if (relPkgsNbr <= bal4)
							bal4 = relPkgsNbr;
					}

					if (blPkgs < bal4)
						bal4 = blPkgs;

					balance = String.valueOf(bal4);
				}
				/*
				 * if (crgStatus.equals("T") || crgStatus.equals("R")) { String bal1 =
				 * rs.getString(27); String bal2 = rs.getString(28); dnPkgs = rs.getString(29);
				 * int con1 = Integer.parseInt(bal1); int con2 = Integer.parseInt(bal2);
				 * if(!checkjpjp && !checkjppsa){ balance = String.valueOf(con1) ; }else{ if
				 * (con1 < con2) { balance = String.valueOf(con1) ; } else { balance =
				 * String.valueOf(con2) ; } } }
				 */

				if (dnPkgs == null || dnPkgs.equals(""))
					dnPkgs = "0";
				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo.setAACustCD(aaCustCd);
				// Added by Punitha on 30/07/2009
				edoVo.setAAIcNbr(aaIcNbr);
				edoVo.setBlNbr(blNbr);
				edoVo.setAcctNo(accNbr);
				edoVo.setPayMode(payNbr);
				edoVo.setNomWt(nomWt);
				edoVo.setNomVol(nomVol);
				edoVo.setAdpcustcd(adpcustcd);
				edoVo.setAdpIcNbr(adpIcNbr);
				edoVo.setAdpNm(adpNm);
				edoVo.setCaCustcd(caCustcd);
				edoVo.setCaIcNbr(caIcNbr);
				edoVo.setCaName(caNm);
				edoVo.setAAName(aaNm);
				edoVo.setATB(atb);
				edoVo.setCOD(cod);
				edoVo.setCrgDes(crgDes);
				edoVo.setDeclPkgs(declPkgs);
				edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				int temp_balance = Integer.parseInt(balance);
				if (temp_balance < 0)
					balance = "0";
				edoVo.setBalance(balance);
				edoVo.setDeliveredPkgs(dnPkgs);
				edoVo.setCrgStatus(crgStatus);
				edoVo.setMarkings(mftmarkings);
				edoVo.setInVoyNbr(invoyNbr);
				edoVo.setOutVoyNbr(outvoyNbr);
				edoVo.setAACustCD(aaCustCd);
				edoVo.setVslName(vslName);
				edoVo.setConsName(consname);
				edoVo.setBillParty(billparty);
				edoVo.setBillTon(maxQty);
				edoVo.setDnNbr(transNbr);
				edoVo.setNoPkgs(transLocalCheck);
				// Warehouse
				edoVo.setWhInd(whInd);
				edoVo.setWhAggrNbr(whAggrNbr);
				edoVo.setWhRemarks(whRemarks);
				BJDetailsVect.add(edoVo);
			}
			log.info("END: *** fetchEdoDetails Result *****" + BJDetailsVect.toString());
		} catch (NullPointerException e) {
			log.error("Exception fetchEdoDetails :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception fetchEdoDetails :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchEdoDetails  DAO  END");
		}

		return BJDetailsVect;
	}

	@Override
	public List<EdoValueObjectOps> fetchShutoutDNList(String edoNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();
		try {
			log.info("START: fetchShutoutDNList edoNbr:" + edoNbr);
			sql.append(" SELECT DN_NBR, NBR_PKGS, BILLABLE_TON, DN_STATUS, BILL_STATUS,");
			sql.append(
					" TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, ");
			sql.append(" CNTR_NBR, TRUCK_NBR FROM DN_DETAILS WHERE EDO_ASN_NBR=:edoNbr ");
			sql.append(" and tesn_asn_nbr is null order by dn_nbr ");

			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			String dnNbr;
			String noPkgs;
			String billTon;
			String dnStatus;
			String trans = "";
			String billStatus;
			String cntrNo = "";
			String truckNbr = "";
			while (rs.next()) {
				dnNbr = rs.getString("DN_NBR");
				noPkgs = rs.getString("NBR_PKGS");
				billTon = rs.getString("BILLABLE_TON");
				dnStatus = rs.getString("DN_STATUS");
				trans = rs.getString("trnsDate");
				billStatus = rs.getString("BILL_STATUS");
				cntrNo = rs.getString("CNTR_NBR");
				truckNbr = CommonUtility.deNull(rs.getString("TRUCK_NBR"));

				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo.setDnNbr(dnNbr);
				edoVo.setNoPkgs(noPkgs);
				edoVo.setBillTon(billTon);
				edoVo.setDnStatus(dnStatus);
				edoVo.setTransDate(trans);
				edoVo.setBillStatus(billStatus);
				edoVo.setCntrNo(cntrNo);
				edoVo.setVech1(truckNbr);
				BJDetailsVect.add(edoVo);
			}
			log.info("END: *** fetchShutoutDNList Result *****" + BJDetailsVect.toString());
		} catch (NullPointerException e) {
			log.error("Exception checkValifetchShutoutDNListdVesselVoyage :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception fetchShutoutDNList :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchShutoutDNList  DAO  END");
		}

		return BJDetailsVect;
	}

	public String chktesnJpPsa_nbr(String esn_asnNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		String tesnjppsa = "N";
		try {
			log.info("START: chktesnJpPsa_nbr esn_asnNbr:" + esn_asnNbr);
			sql = "select * from tesn_jp_psa jppsa,esn e where e.esn_asn_nbr = jppsa.esn_asn_nbr and e.esn_status ='A' and jppsa.ESN_ASN_NBR =:esnAsnNbr";

			paramMap.put("esnAsnNbr", esn_asnNbr);
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				tesnjppsa = "Y";
			}

			log.info("END: *** chktesnJpPsa_nbr Result *****" + tesnjppsa);
		} catch (NullPointerException e) {
			log.error("Exception chktesnJpPsa_nbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception chktesnJpPsa_nbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chktesnJpPsa_nbr  DAO  END");
		}

		return tesnjppsa;
	}

	public long getWeight(String cntrSeq) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		long weight = 0;
		StringBuffer sql = new StringBuffer();
		try {
			log.info("START: updateWeight cntrSeq:" + cntrSeq);
			sql.append(" select DECLR_WT from CNTR where CNTR_SEQ_NBR =:cntrSeq ");

			paramMap.put("cntrSeq", cntrSeq);
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				weight = rs.getLong("DECLR_WT");
			}
			log.info("END: *** getWeight Result *****" + weight);
		} catch (NullPointerException e) {
			log.error("Exception getWeight :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getWeight :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWeight  DAO  END");
		}

		return weight;
	}

	@Override
	public void updateWeight(String cntrSeq, long weight, String user, String times) throws BusinessException {
		StringBuffer sql = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		long wght = 0l;
		try {
			log.info("START: updateWeight cntrSeq:" + cntrSeq + "weight:" + weight + "user:" + user + "times:" + times);
			if ("ADD".equals(times)) {
				wght = getWeight(cntrSeq) + weight;
				sql.append(
						" UPDATE CNTR SET LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID =:user, DECLR_WT =:weight ");
				sql.append(" WHERE CNTR_SEQ_NBR=:cntrSeq ");
			} else if ("SUB".equals(times)) {
				wght = getWeight(cntrSeq) - weight;
				sql.append(
						" UPDATE CNTR SET LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID =:user, DECLR_WT =:weight ");
				sql.append(" WHERE CNTR_SEQ_NBR=:cntrSeq ");
			} else {
				throw new BusinessException("M4201");
			}

			paramMap.put("cntrSeq", cntrSeq);
			paramMap.put("weight", wght);
			paramMap.put("user", user);
			log.info("SQL" + sql.toString() + "pstmt:");
			namedParameterJdbcTemplate.update(sql.toString(), paramMap);

			log.info("END: *** updateWeight Result *****");
		} catch (NullPointerException e) {
			log.error("Exception updateWeight :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception updateWeight :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateWeight  DAO  END");
		}

	}

	@Override
	public void cancel1stDn(String cntrSeq, String cntrNbr, String user) throws BusinessException {
		StringBuffer sql = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Timestamp dttmFirst = new Timestamp(new java.util.Date().getTime());
		int insert = 0;
		try {
			log.info("START: cancel1stDn cntrSeq:" + cntrSeq + "cntrNbr:" + cntrNbr + "user" + user);
			sql.append(
					"INSERT INTO CNTR_TXN(CNTR_SEQ_NBR, CNTR_NBR, TXN_CD, TXN_DTTM, LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID) ");
			sql.append("VALUES( :cntrSeq, :cntrNbr, 'USTF', :dttmFirst, sysdate, :user')");

			paramMap.put("cntrSeq", cntrSeq);
			paramMap.put("cntrNbr", cntrNbr);
			paramMap.put("user", user);
			paramMap.put("dttmFirst", dttmFirst);
			log.info("SQL" + sql.toString() + "paramMap:" + paramMap.toString());
			insert = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (NullPointerException e) {
			log.error("Exception cancel1stDn :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception cancel1stDn :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cancel1stDn  DAO Result:" + insert);
		}
	}

	@Override
	public void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException {
		int count = 0;
		String sql = "";
		SqlRowSet rs = null;
		String cntrStatus = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: changeStatusCntr cntrSeq:" + cntrSeq + "user:" + user + "newCatCode:" + newCatCode);
			sql = "select ERROR_MSG from CNTR_TXN where cntr_seq_nbr =:cntrSeq and TXN_CD = 'STF' ORDER by txn_dttm ASC";

			paramMap.put("cntrSeq", cntrSeq);
			paramMap.put("user", user);
			paramMap.put("newCatCode", newCatCode);
			log.info("SQL" + sql + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				cntrStatus = rs.getString("ERROR_MSG");
			}
			if (cntrStatus != null && cntrStatus.length() > 0) {
				cntrStatus = cntrStatus.substring(0, 1);
			}
			if ("E".equals(cntrStatus)) {
				// update the status of cntr to E
				StringBuffer sb = new StringBuffer();
				sb.append("UPDATE CNTR SET CAT_CD =:newCatCode, STATUS = 'E', LAST_MODIFY_DTTM = sysdate,");
				sb.append(" LAST_MODIFY_USER_ID =:user WHERE CNTR_SEQ_NBR=:cntrSeq ");

				paramMap.clear();
				paramMap.put("newCatCode", newCatCode);
				paramMap.put("user", user);
				paramMap.put("cntrSeq", cntrSeq);
				log.info("SQL" + sb.toString() + "paramMap:" + paramMap.toString());
				count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			}
		} catch (NullPointerException e) {
			log.error("Exception changeStatusCntr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception changeStatusCntr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: changeStatusCntr  DAO  Result:" + count);
		}
	}

	@Override
	public String getNewCatCd(String cntrSeq) throws BusinessException {
		String newCatCode = "";
		try {
			log.info("START: getNewCatCd cntrSeq:" + cntrSeq);

			// First: Get the container vo object to get required values
			ContainerValueObject cntrVo = containerDataRepo.getContainerByPrimaryKey(Long.parseLong(cntrSeq));

			// Second: Get the new cntr cat code
			String newStatus = "F".equals(cntrVo.getStatus()) ? "E" : "F";
			newCatCode = containerCommonFunctionRepo.getCntrCatCd(cntrVo.getIsoCode(), null, cntrVo.getOogOH(),
					cntrVo.getOogOlFront(), cntrVo.getOogOlBack(), cntrVo.getOogOwRight(), cntrVo.getOogOwLeft(),
					cntrVo.getReeferInd(), cntrVo.getUCInd(), cntrVo.getOverSizeInd(), cntrVo.getSpecialDetails(),
					newStatus);

			log.info("END: *** getNewCatCd Result *****" + newCatCode.toString());
		} catch (NullPointerException e) {
			log.error("Exception getNewCatCd :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getNewCatCd :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getNewCatCd  DAO  END");
		}
		return newCatCode;

	}

	@Override
	public int checkFirstDN(String edoNbr, String cntrNo) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		int resultStf = 0;
		int resultUstf = 0;
		try {
			log.info("START: checkFirstDN edoNbr:" + edoNbr + "cntrNo:" + cntrNo);
			sb.append(" select count(*) from cntr_txn,cntr where txn_cd = 'STF'");
			sb.append(" and cntr_txn.cntr_seq_nbr = cntr.cntr_seq_nbr and cntr_txn.cntr_nbr = cntr.cntr_nbr ");
			sb.append(" and cntr.cntr_nbr =:cntrNbr and cntr.txn_status = 'A' and cntr.purp_cd = 'ST' ");
			sb.append(" and cntr.misc_app_nbr is not null ");

			paramMap.put("cntrNbr", cntrNo);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				resultStf = rs.getInt(1);
			}

			sb2.append(" select count(*) from cntr_txn,cntr where txn_cd = 'USTF' ");
			sb2.append(" and cntr_txn.cntr_seq_nbr = cntr.cntr_seq_nbr and cntr_txn.cntr_nbr = cntr.cntr_nbr ");
			sb2.append(" and cntr.cntr_nbr =:cntrNbr and cntr.txn_status = 'A' and cntr.purp_cd = 'ST' ");
			sb2.append(" and cntr.misc_app_nbr is not null ");

			paramMap.put("cntrNbr", cntrNo);
			log.info("SQL" + sb2.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);
			while (rs.next()) {
				resultUstf = rs.getInt(1);
			}
			log.info("END: *** checkFirstDN Result *****" + resultStf + -+resultUstf);
		} catch (NullPointerException e) {
			log.error("Exception checkFirstDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception checkFirstDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkFirstDN  DAO  END");
		}
		return resultStf - resultUstf;
	}

	@Override
	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		int count = 0;
		try {
			log.info("START: updateCntrStatus cntrSeq:" + cntrSeq + "userID:" + userID);
			sb.append(" UPDATE CNTR SET STATUS = 'E', LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID =:userID ");
			sb.append(" WHERE CNTR_SEQ_NBR =:cntrSeq ");
			paramMap.put("userID", userID);
			paramMap.put("cntrSeq", cntrSeq);
			log.info("SQL" + sb.toString() + "pstmt:");
			count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			if (count == 0) {
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			log.info("END: *** updateCntrStatus Result *****" + count);
		} catch (NullPointerException e) {
			log.error("Exception updateCntrStatus :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception updateCntrStatus :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateCntrStatus  DAO  END");
		}
		return "" + count;
	}

	@Override
	public boolean countDNBalance(String cntrNbr) throws BusinessException {
		boolean bal = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: countDNBalance cntrNbr:" + cntrNbr);
			sb.append(" select * from dn_details where cntr_nbr=:cntrNbr and DN_STATUS='A' ");

			paramMap.put("cntrNbr", cntrNbr);
			log.info("SQL" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				bal = true;
			}
			log.info("END: *** countDNBalance Result *****" + bal);
		} catch (NullPointerException e) {
			log.error("Exception countDNBalance :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception countDNBalance :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: countDNBalance  DAO  END");
		}
		return bal;
	}

	@Override
	public String getUaNbr(String esnNbr, int nbrPkgs, String transDttm, String dpNm, String dpIcNbr)
			throws BusinessException {
		String uaNbr = null;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getUaNbr esnNbr:" + esnNbr + "nbrPkgs:" + nbrPkgs + "transDttm:" + transDttm + "dpNm:"
					+ dpNm + "dpIcNbr:" + dpIcNbr);
			sb.append(" select ua_nbr from ua_details where esn_asn_nbr=:esnNbr and nbr_pkgs=:nbrPkgs and ");
			sb.append(
					" to_char(trans_dttm, 'DD/MM/YYYY HH24:MI') =:transDttm and dp_ic_nbr=:dpIcNbr and ua_status='A' ");

			if (dpNm != null && !"".equalsIgnoreCase(dpNm)) {
				sb.append(" and dp_nm=:dpNm ");
			}

			paramMap.put("esnNbr", esnNbr);
			paramMap.put("nbrPkgs", nbrPkgs);
			paramMap.put("transDttm", transDttm);
			paramMap.put("dpNm", dpNm);
			paramMap.put("dpIcNbr", dpIcNbr);
			log.info("SQL" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				uaNbr = rs.getString("ua_nbr");
			}
			log.info("END: *** getUaNbr Result *****" + uaNbr);
		} catch (NullPointerException e) {
			log.error("Exception getUaNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getUaNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUaNbr  DAO  END");
		}
		return uaNbr;
	}

	/**
	 * This method used to update the status of DN Details into cancel
	 * 
	 * @param String edoNbr
	 * @param String dnNbr
	 * @param String userid
	 * @param String transtype
	 * @param String searchcrg
	 * @param String tesn_nbr
	 * @return String Number of record updated into Cancel
	 * @exception BusinessException
	 */
	@Override
	public String cancelDN(String edoNbr, String dnNbr, String userid, String transtype, String searchcrg,
			String tesn_nbr) throws BusinessException {
		String sql = "";
		String dnsql = "";
		String edoupdsql = "";
		String tesnupdsql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
		String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);
		String sqlupdJp_trans = "";
		String dn_sql_trans = "";
		String sqltlog1 = "";
		String sqltlog = "";
		int stransno = 0;
		String sqltlog2 = "";
		int stransno2 = 0;
		int count_trans2 = 0;
		int stransno1 = 0;
		int count_trans1 = 0;
		int newuanbrpkgs = 0;
		int countua = 0;
		int count = 0;
		int count1 = 0;
		int count2 = 0;
		try {
			log.info("START: cancelDN edoNbr:" + edoNbr + "dnNbr:" + dnNbr + "userid:" + userid + "transtype:"
					+ transtype + "searchcrg:" + searchcrg + ",tesn_nbr:" + tesn_nbr);
			sqltlog2 = "SELECT MAX(TRANS_NBR) FROM DN_DETAILS_TRANS WHERE dn_NBR= :dnNbr";
			paramMap.put("dnNbr", dnNbr);

			// boolean checkjpjp = chktesnJpJp(edoNbr);
			// boolean checkjppsa = chktesnJpPsa(edoNbr);
			boolean checkEdoStuff = chkEDOStuffing(edoNbr); // vinayak added 09 jan 2004
			// LogManager.instance.logInfo("checkEdoStuff cancelDN() :"+checkEdoStuff);
			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
				if (chktesnJpJp_nbr.equals("Y")) {
					tesnupdsql = "update tesn_jp_jp set dn_nbr_pkgs = dn_nbr_pkgs -";
					// lak added for Audit Trial
					sqlupdJp_trans = "insert into tesn_jp_jp_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
					sqltlog1 = "SELECT MAX(TRANS_NBR) FROM tesn_jp_jp_TRANS WHERE esn_asn_NBR= :tesn_nbr";
					paramMap.put("tesn_nbr", tesn_nbr);
				} else if (chktesnJpPsa_nbr.equals("Y")) {
					tesnupdsql = "update tesn_jp_psa set dn_nbr_pkgs = dn_nbr_pkgs -";
					// lak added for Audit Trial
					sqlupdJp_trans = "insert into tesn_jp_psa_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
					sqltlog1 = "SELECT MAX(TRANS_NBR) FROM tesn_jp_psa_TRANS WHERE esn_asn_NBR= :tesn_nbr ";
					paramMap.put("tesn_nbr", tesn_nbr);
				}
			}
			dnsql = "select * from dn_details where DN_NBR = :dnNbr";
			paramMap.put("dnNbr", dnNbr);

			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
				edoupdsql = "update gb_edo set last_modify_dttm = sysdate, dn_nbr_pkgs = dn_nbr_pkgs -";
				// lak added for audit trail
				// edoupdsql_trans = "insert into gb_edo_trans
				// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,dn_nbr_pkgs,remarks)
				// values(";
			} else {
				edoupdsql = "update gb_edo set last_modify_dttm = sysdate, trans_dn_nbr_pkgs = trans_dn_nbr_pkgs -";
				// lak added for audit trail
				// edoupdsql_trans = "insert into gb_edo_trans
				// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,trans_dn_nbr_pkgs,remarks)
				// values(";
			}

			rs = namedParameterJdbcTemplate.queryForRowSet(dnsql, paramMap);

			if (rs.next()) {
				newuanbrpkgs = rs.getInt("NBR_PKGS");
				log.info("dnsql :" + dnsql + " --newuanbrpkgs :" + newuanbrpkgs);
			}

			if (checkEdoStuff) // vinayak added 09 jan 2004
			{
				String ccstuffsql = "UPDATE cc_stuffing_details SET DN_NBR_PKGS=DN_NBR_PKGS- :newuanbrpkgs  WHERE STUFF_SEQ_NBR= :tesn_nbr AND EDO_ESN_NBR= :edoNbr ";
				String edostuffsql = "update gb_edo set trans_dn_nbr_pkgs = trans_dn_nbr_pkgs- :newuanbrpkgs where edo_asn_nbr = :edoNbr ";
				String dnstuffsql = "update dn_details set dn_status='X' where dn_nbr= :dnNbr  and edo_asn_nbr= :edoNbr ";

				paramMap.put("newuanbrpkgs", newuanbrpkgs);
				paramMap.put("tesn_nbr", tesn_nbr);
				paramMap.put("edoNbr", edoNbr);
				paramMap.put("dnNbr", dnNbr);

				count2 = namedParameterJdbcTemplate.update(ccstuffsql, paramMap);
				int count3 = namedParameterJdbcTemplate.update(edostuffsql, paramMap);
				int count4 = namedParameterJdbcTemplate.update(dnstuffsql, paramMap);

				log.info("ccstuffsql cancelDN():" + ccstuffsql + " -- count2 :" + count2 + " -- edostuffsql :"
						+ edostuffsql + " -- count3 :" + count3 + " -- dnstuffsql :" + dnstuffsql + " --count4 :"
						+ count4);

				log.info("**************inside if of checkEDO STuff :" + dnstuffsql);
				if (chktesnJpPsa_nbr.equals("Y")) {
					// To update for TESN JP PSA
					tesnupdsql = "update tesn_jp_psa set dn_nbr_pkgs = dn_nbr_pkgs -:newuanbrpkgs  where esn_asn_nbr = :tesn_nbr ";
					paramMap.put("newuanbrpkgs", newuanbrpkgs);
					paramMap.put("tesn_nbr", tesn_nbr);

					log.info("for JP PSA1 ***:" + tesnupdsql + ":" + newuanbrpkgs + ":" + tesn_nbr);
					int count5 = namedParameterJdbcTemplate.update(tesnupdsql, paramMap);
					log.info("count5 : " + count5);
					tesnupdsql = "";
				}
			} else {
				edoupdsql = edoupdsql + newuanbrpkgs + " where edo_asn_nbr = :edoNbr";
				paramMap.put("edoNbr", edoNbr);

				// lak added for Audit Trail start
				sqltlog = "SELECT MAX(TRANS_NBR) FROM gb_edo_TRANS WHERE Edo_ASN_NBR= :edoNbr ";
				paramMap.put("edoNbr", edoNbr);

				SqlRowSet rs1 = null;
				if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
					// LogManager.instance.logInfo("sqltlog :"+sqltlog);
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
					if (rs1.next()) {
						stransno = (rs1.getInt(1)) + 1;
					} else {
						stransno = 0;
					}
					log.info("stransno : " + stransno);
				}

				// edoupdsql_trans = edoupdsql_trans
				// +stransno+","+edoNbr+",sysdate,'"+userid+"',"+newuanbrpkgs+",'DN Del')";

				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					// count_trans = stmt.executeUpdate(edoupdsql_trans);
				}

				SqlRowSet rs3 = null;
				if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
					// LogManager.instance.logInfo("sqltlog2 :"+sqltlog2);
					rs3 = namedParameterJdbcTemplate.queryForRowSet(sqltlog2, paramMap);
					if (rs3.next()) {
						stransno2 = (rs3.getInt(1)) + 1;
					} else {
						stransno2 = 0;
					}
					log.info("stransno2 : " + stransno2);
				}
				dn_sql_trans = "INSERT INTO DN_DETAILS_TRANS(TRANS_NBR,DN_NBR,DN_STATUS,LAST_MODIFY_DTTM,LAST_MODIFY_USER_ID) VALUES( :stransno2 ,:dnNbr ,'X',SYSDATE, :userid)";
				paramMap.put("stransno2", stransno2);
				paramMap.put("dnNbr", dnNbr);
				paramMap.put("userid", userid);
				// lak added for Audit Trail end

				if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {

					tesnupdsql = tesnupdsql + newuanbrpkgs + " where esn_asn_nbr = :tesn_nbr ";
					paramMap.put("tesn_nbr", tesn_nbr);

					count1 = namedParameterJdbcTemplate.update(tesnupdsql, paramMap);
					log.info("tesnupdsql :" + tesnupdsql + " -- count1 :" + count1);
					// lak added for audit trail
					SqlRowSet rs2 = null;
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
						// LogManager.instance.logInfo("0000 sqltlog1: \n" + sqltlog1);
						rs2 = namedParameterJdbcTemplate.queryForRowSet(sqltlog1, paramMap);
						if (rs2.next()) {
							stransno1 = (rs2.getInt(1)) + 1;
						} else {
							stransno1 = 0;
						}
						log.info("stransno1 : " + stransno1);
					}

					sqlupdJp_trans = sqlupdJp_trans
							+ " :stransno1, :edoNbr , :tesn_nbr , :newuanbrpkgs ,sysdate, :userid,'DN Del')";

					paramMap.put("stransno1", stransno1);
					paramMap.put("edoNbr", edoNbr);
					paramMap.put("tesn_nbr", tesn_nbr);
					paramMap.put("newuanbrpkgs", newuanbrpkgs);
					paramMap.put("userid", userid);

					if (logStatusGlobal.equalsIgnoreCase("Y")) {

						count_trans1 = namedParameterJdbcTemplate.update(sqlupdJp_trans, paramMap);
						log.info("sqlupdJp_trans: " + sqlupdJp_trans + " -- count_trans1 :" + count_trans1);

					}

				}

				count = namedParameterJdbcTemplate.update(edoupdsql, paramMap);

				log.info("edoupdsql :" + edoupdsql + "after update ==== count :" + count + " -- count_trans1 :"
						+ count_trans1);
				sql = "UPDATE DN_DETAILS SET DN_STATUS='X',LAST_MODIFY_USER_ID= :userid ,LAST_MODIFY_DTTM=SYSDATE WHERE DN_NBR = :dnNbr ";
				paramMap.put("userid", userid);
				paramMap.put("dnNbr", dnNbr);
				log.info("table update :" + sql);

				countua = namedParameterJdbcTemplate.update(sql, paramMap);

				log.info("insertion countua >>>>>>>>> " + countua);
				log.info("insertion count >>>>>>>>> " + count);

				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					count_trans2 = namedParameterJdbcTemplate.update(dn_sql_trans, paramMap);
					log.info("dn_sql_trans :" + dn_sql_trans + " -- count_trans2 :" + count_trans2);

				}
				log.info("countua :" + countua + " --count :" + count);
				if (countua == 0 || count == 0) {
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}

			}
			log.info("END: *** cancelDN Result *****" + count);
		} catch (NullPointerException e) {
			log.error("Exception cancelDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception cancelDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cancelDN  DAO  END");
		}
		return "" + count;
	}

	@Override
	public String cancelShutoutDN(String edoNbr, String dnNbr, String userid) throws BusinessException {
		String sql = "";
		String dnsql = "";
		String edoupdsql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String dn_sql_trans = "";
		String sqltlog = "";
		int stransno = 0;
		String sqltlog2 = "";
		int stransno2 = 0;
		int count_trans2 = 0;
		int countua = 0;
		int count = 0;
		try {
			log.info("START: getDnCntrFirst edoNbr:" + edoNbr + "dnNbr" + dnNbr + "userid" + userid);
			sqltlog2 = "SELECT MAX(TRANS_NBR) FROM DN_DETAILS_TRANS WHERE dn_NBR=:dnNbr ";
			dnsql = "select * from dn_details where DN_NBR =:dnNbr ";

			int newuanbrpkgs = 0;

			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + dnsql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(dnsql.toString(), paramMap);

			if (rs.next()) {
				newuanbrpkgs = rs.getInt("NBR_PKGS");
			}

			edoupdsql = "update gb_edo set dn_nbr_pkgs = dn_nbr_pkgs -" + newuanbrpkgs + " where edo_asn_nbr =:edoNbr ";

			sqltlog = "SELECT MAX(TRANS_NBR) FROM gb_edo_TRANS WHERE Edo_ASN_NBR=:edoNbr ";

			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + sqltlog.toString() + "pstmt:");
			SqlRowSet rs1 = null;
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);

				if (rs1.next()) {
					stransno = (rs1.getInt(1)) + 1;
				} else {
					stransno = 0;
				}

				log.info("stransno : " + stransno);
			}

			SqlRowSet rs3 = null;
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				rs3 = namedParameterJdbcTemplate.queryForRowSet(sqltlog2.toString(), paramMap);
				if (rs3.next()) {
					stransno2 = (rs3.getInt(1)) + 1;
				} else {
					stransno2 = 0;
				}
				log.info("stransno2 : " + stransno2);
			}
			dn_sql_trans = "INSERT INTO DN_DETAILS_TRANS(TRANS_NBR,DN_NBR,DN_STATUS,LAST_MODIFY_DTTM,LAST_MODIFY_USER_ID) VALUES(";
			dn_sql_trans = dn_sql_trans + stransno2 + ",:dnNbr,'X',SYSDATE,:userid)";

			paramMap.put("userid", userid);
			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + edoupdsql.toString() + "pstmt:");
			count = namedParameterJdbcTemplate.update(edoupdsql.toString(), paramMap);
			log.info("count : " + count);

			sql = "UPDATE DN_DETAILS SET DN_STATUS='X',LAST_MODIFY_USER_ID=:userid ,LAST_MODIFY_DTTM=SYSDATE WHERE DN_NBR =:dnNbr ";

			paramMap.put("userid", userid);
			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sqltlog.toString() + "pstmt:");
			countua = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				count_trans2 = namedParameterJdbcTemplate.update(dn_sql_trans.toString(), paramMap);
				log.info("count_trans2 : " + count_trans2);
			}
			if (countua == 0 || count == 0) {

				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			// Commented on 3/6/2016 Sripriya to nnot allow to update shutoutdelivery pkgs
			// in Booking as it is done @ Shutout Out Cargo SCreen
			// update booking_details's shutout delivery packages
			// String bookingRefNbr = getBookingRefNbr(edoNbr);
			// if (bookingRefNbr!=null && bookingRefNbr.length()>0){
			// updateBookingShutoutDeliveryPkgs(bookingRefNbr,-newuanbrpkgs);
			// }
			log.info("END: *** cancelShutoutDN Result *****" + dnNbr);
		} catch (NullPointerException e) {
			log.error("Exception cancelShutoutDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception cancelShutoutDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cancelShutoutDN  DAO  END");
		}
		return dnNbr;
	}

	@Override
	public String getDnCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException {
		String dnFirst = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getDnCntrFirst cntrSeq:" + cntrSeq + "cntrNbr" + cntrNbr);
			int resultStf = 0;
			int resultUstf = 0;

			sb.append(" select count(*) from cntr_txn where txn_cd = 'STF' and cntr_txn.cntr_seq_nbr =:cntrSeqNbr ");

			paramMap.put("cntrSeqNbr", cntrSeq);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				resultStf = rs.getInt(1);
			}

			sb.append(" select count(*) from cntr_txn where txn_cd = 'USTF' and cntr_txn.cntr_seq_nbr =:cntrSeqNbr ");

			paramMap.put("cntrSeqNbr", cntrSeq);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				resultUstf = rs.getInt(1);
			}
			if (resultStf - resultUstf <= 0) {
				return "";
			}

			sb.append(" select dn_nbr from dn_details where edo_asn_nbr in ");
			sb.append(" (select edo_asn_nbr from edo_cntr where cntr_seq_nbr =:cntrSeqNbr) ");
			sb.append(" and dn_status = 'A' and cntr_nbr =:cntrNbr order by mot_create_dttm ASC ");

			paramMap.put("cntrSeqNbr", cntrSeq);
			paramMap.put("cntrNbr", cntrNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				dnFirst = rs.getString("dn_nbr");
			}
			log.info("END: *** getDnCntrFirst Result *****" + dnFirst.toString());
		} catch (NullPointerException e) {
			log.error("Exception getDnCntrFirst :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getDnCntrFirst :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDnCntrFirst  DAO  END");
		}
		return dnFirst;
	}

	/**
	 * MCConsulting get dn created date time and check the date has reached the
	 * threshold limit till next day and return false if the
	 * 
	 * @param dnNbr
	 * @return
	 * @throws RemoteException
	 * @throws BusinessException
	 */
	@Override
	public boolean checkCancelDN(String dnNbr) throws BusinessException {
		boolean canCancel = false;
		Date dn_dttm = null;
		Date dnNextDayStart = null;
		Date today = new Date();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: checkCancelDN dnNbr:" + dnNbr);
			sb.append(" SELECT DN_CREATE_DTTM FROM DN_DETAILS WHERE DN_NBR=:dnNbr ");

			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs != null) {
				while (rs.next()) {
					dn_dttm = rs.getDate(1);
				}
			}
			dnNextDayStart = getNextDayStart(dn_dttm);
			log.info("** dn_dttm -->" + dn_dttm);
			log.info("** dnNextDayStart --> " + dnNextDayStart);
			// compare dates and allow
			if (today.before(dnNextDayStart)) {
				log.info("**Can Cancel DN as the date threshold is not crossed*** ");
				canCancel = true;
			}

			log.info("END: *** checkCancelDN Result *****" + canCancel);
		} catch (NullPointerException e) {
			log.error("Exception checkCancelDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception checkCancelDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkCancelDN  DAO  END");
		}
		return canCancel;
	}

	@Override
	public String getCntrSeq(String cntrNo) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		String cntrSeq = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCntrSeq cntrNo:" + cntrNo);
			sb.append(" SELECT CNTR_SEQ_NBR FROM CNTR WHERE CNTR_NBR =:cntrNo AND PURP_CD='ST' ");
			sb.append(" AND TXN_STATUS = 'A' AND MISC_APP_NBR IS NOT NULL");

			paramMap.put("cntrNo", cntrNo);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				cntrSeq = Integer.toString(rs.getInt(1));
			}
			log.info("END: *** getCntrSeq Result *****" + cntrSeq.toString());
		} catch (NullPointerException e) {
			log.error("Exception getCntrSeq :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getCntrSeq :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCntrSeq  DAO  END");
		}
		return cntrSeq;
	}

	@Override
	public boolean checkESNCntr(String edoasn) throws BusinessException {
		boolean esnCntr = false;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: checkESNCntr edoasn:" + edoasn);
			sb.append(" select a.cntr_seq_nbr, b.cntr_nbr from edo_cntr a, cntr b ");
			sb.append(" where a.edo_asn_nbr =:edoAsnNbr ");
			sb.append(" and a.cntr_seq_nbr = b.cntr_seq_nbr ");
			sb.append(" and b.txn_status <> 'D' and b.txn_status <> 'I' ");
			paramMap.put("edoAsnNbr", edoasn);
			log.info("SQL: checkESNCntr:" + sb.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				esnCntr = true;
			} else {
				esnCntr = false;
			}
			log.info("END: *** checkESNCntr Result *****" + esnCntr);
		} catch (NullPointerException e) {
			log.error("Exception checkESNCntr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception checkESNCntr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkESNCntr  DAO  END");
		}
		return esnCntr;
	}

	/**
	 * This method used to check tesnJp or not for Bulk Cargo
	 * 
	 * @param String edoNbr
	 * @return boolean if transcrg is Y return true else false
	 * 
	 * @exception BusinessException
	 */
	@Override
	public boolean BchktesnJpJp(String edoNbr) throws BusinessException {
		String sql = "";
		boolean tesnjpjp = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: BchktesnJpJp  DAO  Start edoNbr " + edoNbr);

			sql = "select * from Bulk_tesn_jp_jp jpjp,bulk_esn e where e.esn_asn_nbr = jpjp.esn_asn_nbr and e.esn_status ='A' and EDO_ASN_NBR = :edoNbr  and e.trans_crg != 'Y'";

			paramMap.put("edoNbr", edoNbr.trim());
			log.info(" BchktesnJpJp  DAO  SQL " + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				tesnjpjp = true;
			}
			log.info("END: *** BchktesnJpJp Result *****" + tesnjpjp);
		} catch (NullPointerException e) {
			log.error("Exception BchktesnJpJp :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception BchktesnJpJp :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: BchktesnJpJp  DAO  END");
		}
		return tesnjpjp;
	} // end of chktesnJpJp

	@Override
	public boolean chktesnJpPsa(String edoNbr) throws BusinessException {
		String sql = "";
		boolean tesnjppsa = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: chktesnJpPsa  DAO  Start edoNbr " + edoNbr);

			sql = "select * from tesn_jp_psa jppsa,esn e where e.esn_asn_nbr = jppsa.esn_asn_nbr and e.esn_status ='A' and EDO_ASN_NBR = :edoNbr";

			paramMap.put("edoNbr", edoNbr);
			log.info(" chktesnJpPsa  DAO  SQL " + sql.toString());
			log.info(" chktesnJpPsa  DAO  paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				tesnjppsa = true;
			}

			log.info(" chktesnJpPsa  DAO  Result" + tesnjppsa);
		} catch (NullPointerException e) {
			log.error("Exception chktesnJpPsa :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception chktesnJpPsa :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chktesnJpPsa  DAO  END");
		}
		return tesnjppsa;
	} // end of chktesnJpJp

	/**
	 * This method used to check tesnJp or not
	 * 
	 * @param String edoNbr
	 * @return boolean if transcrg is Y return true else false
	 * 
	 * @exception BusinessException
	 */
	@Override
	public boolean chktesnJpJp(String edoNbr) throws BusinessException {
		String sql = "";
		boolean tesnjpjp = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: chktesnJpJp  DAO  Start edoNbr " + edoNbr);

			sql = "select * from tesn_jp_jp jpjp,esn e where e.esn_asn_nbr = jpjp.esn_asn_nbr and e.esn_status ='A' and EDO_ASN_NBR = :edoNbr and e.trans_crg != 'Y' ";

			paramMap.put("edoNbr", edoNbr);
			log.info(" chktesnJpJp  DAO  SQL " + sql.toString());
			log.info(" chktesnJpJp  DAO  paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				tesnjpjp = true;
			}
			log.info(" chktesnJpJp  DAO  Result" + tesnjpjp);
		} catch (NullPointerException e) {
			log.error("Exception chktesnJpJp :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception chktesnJpJp :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chktesnJpJp  DAO  END");
		}
		return tesnjpjp;
	} // end of chktesnJpJp

	/**
	 * This method chkEdoNbr check the gb_edo table
	 * 
	 * @param String edoNbr
	 * @return Yes or No
	 * 
	 * @exception BusinessException
	 *
	 */
	@Override
	public String chkEdoNbr(String edoNbr) throws BusinessException {
		String sql = "";
		String edo_Nbr = "NO";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: chkEdoNbr  DAO  Start edoNbr " + edoNbr);

			sql = "select edo_asn_nbr from gb_edo where edo_asn_nbr = :edoNbr ";

			paramMap.put("edoNbr", edoNbr);
			log.info(" chkEdoNbr  DAO  SQL " + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				edo_Nbr = "YES";
			}

			log.info("END: *** chkEdoNbr Result *****" + edo_Nbr);
		} catch (NullPointerException e) {
			log.error("Exception chkEdoNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception chkEdoNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkEdoNbr  DAO  END");
		}
		return edo_Nbr;
	}

	/**
	 * This method used to check Tesn EDO in database
	 * 
	 * @param String edoNbr
	 * @return boolean if YES or NO
	 * 
	 * @exception BusinessException
	 */
	@Override
	public String chktesnEdo(String edoNbr) throws BusinessException {
		String sql = "";
		String edoNbrChk = "NO";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: chktesnEdo  DAO  Start edoNbr " + edoNbr);

			sql = "select b.ATB_DTTM from gb_edo a,berthing b where b.SHIFT_IND = 1 and a.var_nbr = b.vv_cd and a.edo_asn_nbr = :edoNbr and b.ATB_DTTM is null";

			paramMap.put("edoNbr", edoNbr);
			log.info(" chktesnEdo  DAO  SQL " + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				edoNbrChk = "YES";
			}

			log.info("END: *** chktesnEdo Result *****" + edoNbrChk);
		} catch (NullPointerException e) {
			log.error("Exception chktesnEdo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception chktesnEdo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chktesnEdo  DAO  END");
		}
		return edoNbrChk;
	}

	/**
	 * This method retrieves the list of EDO.
	 * 
	 * @param edoNbr    EDO ASN No
	 * @param searchCrg Search Indicator (LT - Local/TS Delivered Locally, T - TS)
	 * @exception RemoteException, BusinessException
	 */
	private List<String> fetchEdoList(String edoNbr, String searchcrg) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		List<String> edoVect = new ArrayList<String>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: fetchEdoList  DAO  Start   edoNbr " + edoNbr + " searchcrg" + searchcrg);

			if (edoNbr != null && !edoNbr.equals("")) {
				if (edoNbr.substring(0, 2).equals("00")) {
					if (edoNbr.length() > 0)
						edoNbr = edoNbr.substring(2);
				} else if (edoNbr.substring(0, 1).equals("0")) {
					if (edoNbr.length() > 0)
						edoNbr = edoNbr.substring(1);
				}
			}

			if (searchcrg != null && !"".endsWith(searchcrg) && searchcrg.endsWith("SL")) {
				sql = "SELECT ESN_ASN_NBR AS esn,edo_asn_nbr, nbr_pkgs AS nbr_pkgs, crg_status, NVL (dn_nbr_pkgs, 0) + NVL (trans_dn_nbr_pkgs, 0) AS dnpkgs FROM gb_edo  WHERE edo_asn_nbr like:edoNbr AND edo_status = 'A' and ESN_ASN_NBR is not null and SHUTOUT_IND='Y'";

			} else {

				sql = "select  a.edo_asn_nbr from gb_edo a, manifest_details b, vessel_call c where EDO_ASN_NBR like :edoNbr and a.mft_seq_nbr = b.mft_seq_nbr and edo_status = 'A' and a.var_nbr = c.vv_cd  and vv_status_ind in ('BR','UB','CL') and a.dis_type in ('D','N') order by a.edo_asn_nbr desc";

			}

			paramMap.put("edoNbr", "%" + edoNbr + "%");
			log.info(" fetchEdoList  DAO  SQL " + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			while (rs.next()) {
				String edoasnNbr = CommonUtility.deNull(rs.getString("edo_asn_nbr"));

				edoVect.add(edoasnNbr);

			}

			log.info(" fetchEdoList  DAO  Result" + edoVect.toString());

		} catch (NullPointerException e) {
			log.error("Exception fetchEdoList :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception fetchEdoList :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchEdoList  DAO  END");
		}
		return edoVect;

	}

	/**
	 * This method checks whether a particular TS EDO is stuffed into container.
	 * 
	 * @param edoNbr EDO ASN No
	 * @return <code>true</code>
	 * @excpetion BusinessException, RemoteException
	 */
	@Override
	public boolean chkEDOStuffing(String edoNbr) throws BusinessException {
		String strQuery = "";
		boolean edoExst = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: chkEDOStuffing  DAO  Start edoNbr " + edoNbr);

			strQuery = "SELECT distinct EDO_ESN_NBR FROM cc_stuffing_details WHERE EDO_ESN_IND='EDO' AND EDO_ESN_NBR= :edoNbr AND REC_STATUS='A'";

			paramMap.put("edoNbr", edoNbr.trim());
			log.info(" chkEDOStuffing  DAO  SQL " + strQuery.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(strQuery.toString(), paramMap);

			if (rs.next()) {
				edoExst = true;
			}

			log.info("END: *** chkEDOStuffing Result *****" + edoExst);
		} catch (NullPointerException e) {
			log.error("Exception chkEDOStuffing :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception chkEDOStuffing :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkEDOStuffing  DAO  END");
		}
		return edoExst;
	}

	/**
	 * This method retrieves the list of EDOs for DN creation.
	 *
	 * @param edoNbr    EDO ASN No
	 * @param compCode  Completion Code
	 * @param searchCrg Search Indicator (LT - Local/TS Delivered Locally, T - TS)
	 * @exception RemoteException, BusinessException
	 */
	@Override
	public List<EdoVO> fetchEdo(String edoNbr, String compCode, String searchcrg) throws BusinessException {
		String sql = "";
		List<EdoVO> edoVect = new ArrayList<EdoVO>();
		List<String> edoAsnNbr = new ArrayList<String>();
		edoAsnNbr = fetchEdoList(edoNbr, searchcrg);
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info(
					"START: fetchEdo  DAO  Start edoNbr " + edoNbr + " compCode" + compCode + " searchcrg" + searchcrg);

			for (int i = 0; i < edoAsnNbr.size(); i++) {
				String edoasnNbr = (String) edoAsnNbr.get(i);
				boolean checkEdoStuff = chkEDOStuffing(edoasnNbr); // vinayak added 07 jan 2004
				boolean checkjpjp = chktesnJpJp(edoasnNbr);
				boolean checkjppsa = chktesnJpPsa(edoasnNbr);
				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
					// if(!checkjpjp && !checkjppsa){
					// Begin ThanhPT6 SGS 25 Sep 2015: add d.MFT_SEQ_NBR, d.VAR_NBR to sql
					// sql = "select a.edo_asn_nbr,a.nbr_pkgs as nbr_pkgs,a.bl_nbr
					// ,a.crg_status,nvl(d.NBR_PKGS_IN_PORT,0) as nbrPkgsPort,a.nbr_pkgs -
					// a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0) -
					// nvl(a.dn_nbr_pkgs,0)-nvl(a.CUT_OFF_NBR_PKGS,0) as
					// balance,nvl(a.dn_nbr_pkgs,0)+nvl(trans_dn_nbr_pkgs,0) as
					// dnPkgs,nvl(a.CUT_OFF_NBR_PKGS,0) as cutoffPkgs, nom_wt, NOM_VOL from gb_edo
					// a,manifest_details d where EDO_ASN_NBR ='" + edoasnNbr + "' and a.mft_seq_nbr
					// = d.mft_seq_nbr and a.edo_status='A'";
					sql = "select  a.edo_asn_nbr,a.nbr_pkgs as nbr_pkgs,a.bl_nbr ,a.crg_status,nvl(d.NBR_PKGS_IN_PORT,0) as nbrPkgsPort,a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0)-nvl(a.CUT_OFF_NBR_PKGS,0) as balance,nvl(a.dn_nbr_pkgs,0)+nvl(trans_dn_nbr_pkgs,0) as dnPkgs,nvl(a.CUT_OFF_NBR_PKGS,0) as cutoffPkgs, nom_wt, NOM_VOL, d.MFT_SEQ_NBR, d.VAR_NBR, vc.terminal, vc.scheme, vc.combi_gc_scheme, vc.combi_gc_ops_ind from vessel_call vc, gb_edo a,manifest_details d where EDO_ASN_NBR = :edoasnNbr  and a.var_nbr = vc.vv_cd and a.mft_seq_nbr = d.mft_seq_nbr and a.edo_status='A'";
					// End ThanhPT6 SGS 25 Sep 2015

					paramMap.put("edoasnNbr", edoasnNbr);
					log.info(" fetchEdo  DAO  SQL " + sql.toString());

					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					while (rs.next()) {
						EdoVO edoVo = new EdoVO();
						edoVo.setEdoAsnNbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
						edoVo.setNoPkgs(CommonUtility.deNull(rs.getString("nbr_pkgs")));
						edoVo.setBlNbr(CommonUtility.deNull(rs.getString("bl_nbr")));
						edoVo.setCrgStatus(CommonUtility.deNull(rs.getString("crg_status")));
						edoVo.setBalance(CommonUtility.deNull(rs.getString("balance")));
						edoVo.setDeliveredPkgs(CommonUtility.deNull(rs.getString("dnPkgs")));
						edoVo.setDeclPkgs(CommonUtility.deNull(rs.getString("nbrPkgsPort")));
						edoVo.setBillTon(CommonUtility.deNull(rs.getString("cutoffPkgs")));
						edoVo.setNomWt(CommonUtility.deNull(rs.getString("nom_wt")));
						edoVo.setNomVol(CommonUtility.deNull(rs.getString("NOM_VOL")));
						// Begin ThanhPT6 SGS 25 Sep 2015 get more field: d.MFT_SEQ_NBR, d.VAR_NBR
						edoVo.setMtf_seq_nbr(CommonUtility.deNull(rs.getString("MFT_SEQ_NBR")));
						edoVo.setVar_nbr(CommonUtility.deNull(rs.getString("VAR_NBR")));
						// End ThanhPT6 SGS 25 Sep 2015
						edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
						edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
						edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
						edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
						edoVect.add(edoVo);
					}

				} else {
					if (checkjpjp) {
						// Begin FPT modify to includes Local EDO (crg_status = 'L'), just need to have
						// record in tesn_jp_jp
						// sql = "select
						// vsl_nm,v.out_voy_nbr,c.esn_asn_nbr,a.edo_asn_nbr,c.nbr_pkgs,a.bl_nbr
						// ,a.crg_status,nvl(c.DN_NBR_PKGS,0) as dnPkgs,c.nbr_pkgs -
						// nvl(c.DN_NBR_PKGS,0) as balance from gb_edo a,vessel_call v,tesn_jp_jp c,esn
						// e where " +
						// "v.vv_cd = e.out_voy_var_nbr and c.EDO_ASN_NBR = a.EDO_ASN_NBR and
						// c.EDO_ASN_NBR ='" +
						// edoasnNbr + "' and a.crg_status in('T','R') and a.edo_status='A' and
						// e.esn_asn_nbr =c.esn_asn_nbr and e.ESN_STATUS ='A'";

						StringBuffer sb = new StringBuffer();
						sb.append("SELECT ");
						sb.append("	vsl_nm, ");
						sb.append("	v.out_voy_nbr, ");
						sb.append("	c.esn_asn_nbr, ");
						sb.append("	a.edo_asn_nbr, ");
						sb.append("	c.nbr_pkgs, ");
						sb.append("	a.bl_nbr , ");
						sb.append("	a.crg_status, ");
						sb.append("	nvl(c.DN_NBR_PKGS, 0) AS dnPkgs, ");
						sb.append("	c.nbr_pkgs - nvl(c.DN_NBR_PKGS, 0) AS balance, ");
						sb.append("	c.nom_wt, ");
						sb.append("	c.NOM_VOL, ");
						sb.append("	bk.trans_crg, ");
						sb.append("	v.terminal, ");
						sb.append("	v.scheme, ");
						sb.append("	v.combi_gc_scheme, ");
						sb.append("	v.combi_gc_ops_ind ");
						sb.append("FROM ");
						sb.append("	gb_edo a, ");
						sb.append("	vessel_call v, ");
						sb.append("	tesn_jp_jp c, ");
						sb.append("	esn e, ");
						sb.append("	bk_details bk ");
						sb.append("WHERE ");
						sb.append("	v.vv_cd = e.out_voy_var_nbr ");
						sb.append("	AND c.EDO_ASN_NBR = a.EDO_ASN_NBR ");
						sb.append("	AND c.EDO_ASN_NBR = :edoasnNbr ");
						sb.append("	AND a.crg_status IN('L', 'T', 'R') ");
						sb.append("	AND a.edo_status = 'A' ");
						sb.append("	AND e.esn_asn_nbr = c.esn_asn_nbr ");
						sb.append("	AND e.ESN_STATUS = 'A' ");
						sb.append("	AND e.bk_ref_nbr = bk.bk_ref_nbr");
						sql = sb.toString();
						// End FPT modify to includes Local EDO (crg_status = 'L'), just need to have
						// record in tesn_jp_jp

						paramMap.put("edoasnNbr", edoasnNbr);
						log.info(" fetchEdo  DAO  SQL " + sql.toString());

						rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
						while (rs.next()) {
							EdoVO edoVo = new EdoVO();
							edoVo.setEdoAsnNbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
							edoVo.setNoPkgs(CommonUtility.deNull(rs.getString("nbr_pkgs")));
							edoVo.setBlNbr(CommonUtility.deNull(rs.getString("bl_nbr")));
							edoVo.setCrgStatus(CommonUtility.deNull(rs.getString("crg_status")));
							edoVo.setVslName(CommonUtility.deNull(rs.getString("vsl_nm")));
							edoVo.setCaIcNbr(CommonUtility.deNull(rs.getString("esn_asn_nbr")));
							edoVo.setBalance(CommonUtility.deNull(rs.getString("balance")));
							edoVo.setDeliveredPkgs(CommonUtility.deNull(rs.getString("dnPkgs")));
							edoVo.setOutVoyNbr(CommonUtility.deNull(rs.getString("out_voy_nbr")));
							edoVo.setNomWt(CommonUtility.deNull(rs.getString("nom_wt")));
							edoVo.setNomVol(CommonUtility.deNull(rs.getString("NOM_VOL")));
							edoVo.setTrans_crg(CommonUtility.deNull(rs.getString("trans_crg")));
							edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
							edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
							edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
							edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
							edoVect.add(edoVo);
						}

					}
					if (checkjppsa) {
						// Condition removed to check bk_details fix Sripriya 8 Dec 2014

						StringBuffer sb = new StringBuffer();
						sb.append("SELECT ");
						sb.append("	SECOND_CAR_VES_NM, ");
						sb.append("	SECOND_CAR_VOY_NBR, ");
						sb.append("	c.esn_asn_nbr, ");
						sb.append("	a.edo_asn_nbr, ");
						sb.append("	c.nbr_pkgs, ");
						sb.append("	a.bl_nbr , ");
						sb.append("	a.crg_status, ");
						sb.append("	nvl(c.DN_NBR_PKGS, 0) AS dnPkgs, ");
						sb.append("	c.nbr_pkgs - nvl(c.DN_NBR_PKGS, 0) AS balance, ");
						sb.append("	c.nom_wt, ");
						sb.append("	c.NOM_VOL, ");
						sb.append("	vc.terminal, ");
						sb.append("	vc.scheme, ");
						sb.append("	vc.combi_gc_scheme, ");
						sb.append("	vc.combi_gc_ops_ind ");
						sb.append("FROM ");
						sb.append("	gb_edo a, ");
						sb.append("	vessel_call vc, ");
						sb.append("	tesn_jp_psa c, ");
						sb.append("	esn e ");
						sb.append("WHERE ");
						sb.append("	c.EDO_ASN_NBR = a.EDO_ASN_NBR ");
						sb.append("	AND c.EDO_ASN_NBR = :edoasnNbr ");
						sb.append("	AND a.var_nbr = vc.vv_cd ");
						sb.append("	AND a.crg_status IN('T', 'R') ");
						sb.append("	AND a.edo_status = 'A' ");
						sb.append("	AND e.esn_asn_nbr = c.esn_asn_nbr ");
						sb.append("	AND e.ESN_STATUS = 'A'");
						sql = sb.toString();

						paramMap.put("edoasnNbr", edoasnNbr);
						log.info(" fetchEdo  DAO  SQL " + sql.toString());

						rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
						while (rs.next()) {
							EdoVO edoVo = new EdoVO();
							edoVo.setEdoAsnNbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
							edoVo.setNoPkgs(CommonUtility.deNull(rs.getString("nbr_pkgs")));
							edoVo.setBlNbr(CommonUtility.deNull(rs.getString("bl_nbr")));
							edoVo.setCrgStatus(CommonUtility.deNull(rs.getString("crg_status")));
							edoVo.setVslName(CommonUtility.deNull(rs.getString("SECOND_CAR_VES_NM")));
							edoVo.setCaIcNbr(CommonUtility.deNull(rs.getString("esn_asn_nbr")));
							edoVo.setBalance(CommonUtility.deNull(rs.getString("balance")));
							edoVo.setDeliveredPkgs(CommonUtility.deNull(rs.getString("dnPkgs")));
							edoVo.setOutVoyNbr(CommonUtility.deNull(rs.getString("SECOND_CAR_VOY_NBR")));
							edoVo.setNomWt(CommonUtility.deNull(rs.getString("nom_wt")));
							edoVo.setNomVol(CommonUtility.deNull(rs.getString("NOM_VOL")));
							edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
							edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
							edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
							edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
							// edoVo.setTrans_crg(CommonUtility.deNull(rs.getString("trans_crg")));
							edoVect.add(edoVo);
						}

					}
					// vinayak added on 7 jan 2004
					if (checkEdoStuff) {
						// changed by Irene Tan on 17 Feb 2004 : to allow extraction for EDO from other
						// vessel stuff into container of different vessel
						// sql = "SELECT
						// vc.VSL_NM,vc.OUT_VOY_NBR,ge.EDO_ASN_NBR,csd.NBR_PKGS,ge.BL_NBR,ge.CRG_STATUS,NVL(csd.DN_NBR_PKGS,0)
						// AS dnPkgs,csd.NBR_PKGS-NVL(csd.DN_NBR_PKGS,0) AS balance, csd.STUFF_SEQ_NBR
						// AS tesn,cs.CNTR_NBR as cntr FROM gb_edo ge,vessel_call vc,cc_stuffing_details
						// csd,cc_stuffing cs WHERE ge.VAR_NBR=vc.vv_cd AND vc.vv_cd=cs.VAR_NBR AND
						// ge.EDO_ASN_NBR=csd.EDO_ESN_NBR AND cs.STUFF_SEQ_NBR=csd.STUFF_SEQ_NBR AND
						// ge.EDO_ASN_NBR='"+edoasnNbr+"' AND ge.EDO_STATUS='A' AND cs.STUFF_CLOSED='Y'
						// AND csd.REC_STATUS='A' AND cs.ACTIVE_STATUS='A' AND ge.CRG_STATUS IN
						// ('T','R')";
						sql = "SELECT vc.VSL_NM,vc.OUT_VOY_NBR,ge.EDO_ASN_NBR,csd.NBR_PKGS,ge.BL_NBR,ge.CRG_STATUS,NVL(csd.DN_NBR_PKGS,0) AS dnPkgs,csd.NBR_PKGS-NVL(csd.DN_NBR_PKGS,0) AS balance, csd.STUFF_SEQ_NBR AS tesn,cs.CNTR_NBR as cntr, ge.nom_wt, ge.NOM_VOL, vc.terminal, vc.scheme, vc.combi_gc_scheme, vc.combi_gc_ops_ind  FROM gb_edo ge,vessel_call vc,cc_stuffing_details csd,cc_stuffing cs WHERE ge.VAR_NBR=vc.vv_cd AND ge.EDO_ASN_NBR=csd.EDO_ESN_NBR AND cs.STUFF_SEQ_NBR=csd.STUFF_SEQ_NBR AND ge.EDO_ASN_NBR='"
								+ edoasnNbr
								+ "' AND ge.EDO_STATUS='A' AND cs.STUFF_CLOSED='Y' AND csd.REC_STATUS='A' AND cs.ACTIVE_STATUS='A' AND ge.CRG_STATUS IN ('T','R')";
						// end changed by Irene Tan on 17 Feb 2004
						// log.info("sql :"+sql);

						paramMap.put("edoasnNbr", edoasnNbr);
						log.info(" fetchEdo  DAO  SQL " + sql.toString());

						rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
						while (rs.next()) {
							EdoVO edoVo = new EdoVO();
							edoVo.setEdoAsnNbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
							edoVo.setNoPkgs(CommonUtility.deNull(rs.getString("nbr_pkgs")));
							edoVo.setBlNbr(CommonUtility.deNull(rs.getString("bl_nbr")));
							edoVo.setCrgStatus(CommonUtility.deNull(rs.getString("crg_status")));
							edoVo.setVslName(CommonUtility.deNull(rs.getString("cntr")));
							edoVo.setBalance(CommonUtility.deNull(rs.getString("balance")));
							edoVo.setDeliveredPkgs(CommonUtility.deNull(rs.getString("dnPkgs")));
							edoVo.setOutVoyNbr(CommonUtility.deNull(rs.getString("out_voy_nbr")));
							edoVo.setCaIcNbr(CommonUtility.deNull(rs.getString("tesn")));
							edoVo.setChkEdoIndRecStatus(checkEdoStuff);
							edoVo.setNomWt(CommonUtility.deNull(rs.getString("nom_wt")));
							edoVo.setNomVol(CommonUtility.deNull(rs.getString("NOM_VOL")));
							edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
							edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
							edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
							edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
							edoVect.add(edoVo);
						}

					}
				}
			} // End of for loop
			log.info(" fetchEdo  DAO  Result" + edoVect.toString());
		} catch (NullPointerException e) {
			log.error("Exception fetchEdo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception fetchEdo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchEdo  DAO  END");
		}
		return edoVect;
	}

	@Override
	public List<EdoVO> fetchShutoutEdo(String edoNbr, String compCode, String searchcrg) throws BusinessException {
		String sql = "";
		List<EdoVO> edoVect = new ArrayList<EdoVO>();
		List<String> edoAsnNbr = new ArrayList<String>();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: fetchShutoutEdo  DAO  Start edoNbr " + edoNbr + " compCode" + compCode + " searchcrg"
					+ searchcrg);

			edoAsnNbr = fetchEdoList(edoNbr, searchcrg);
			for (int i = 0; i < edoAsnNbr.size(); i++) {
				String edoasnNbr = (String) edoAsnNbr.get(i);
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT ");
				sb.append("	ESN_ASN_NBR AS esn, ");
				sb.append("	edo_asn_nbr, ");
				sb.append("	nbr_pkgs AS nbr_pkgs, ");
				sb.append("	crg_status, ");
				sb.append("	NVL (dn_nbr_pkgs, ");
				sb.append("	0) + NVL (trans_dn_nbr_pkgs, ");
				sb.append("	0) AS dnpkgs, ");
				sb.append("	nom_wt, ");
				sb.append("	NOM_VOL, ");
				sb.append("	vc.terminal, ");
				sb.append("	vc.scheme, ");
				sb.append("	vc.combi_gc_scheme, ");
				sb.append("	vc.combi_gc_ops_ind ");
				sb.append("FROM ");
				sb.append("	gb_edo a, ");
				sb.append("	vessel_call vc ");
				sb.append("WHERE ");
				sb.append("	edo_asn_nbr = :edoasnNbr ");
				sb.append("	AND edo_status = 'A' ");
				sb.append("	AND a.var_nbr = vc.vv_cd ");
				sb.append("	AND ESN_ASN_NBR IS NOT NULL ");
				sb.append("	AND SHUTOUT_IND = 'Y'");
				sql = sb.toString();

				paramMap.put("edoasnNbr", edoasnNbr);
				log.info(" fetchShutoutEdo  DAO  SQL " + sql.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
				while (rs.next()) {
					EdoVO edoValueObject = new EdoVO();
					edoValueObject.setEsnAsnNbr(CommonUtility.deNull(rs.getString("esn")));
					edoValueObject.setEdoAsnNbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
					edoValueObject.setNoPkgs(CommonUtility.deNull(rs.getString("nbr_pkgs")));
					edoValueObject.setDeliveredPkgs(CommonUtility.deNull(rs.getString("dnpkgs")));
					edoValueObject.setCrgStatus(CommonUtility.deNull(rs.getString("crg_status")));
					edoValueObject.setNomWt(CommonUtility.deNull(rs.getString("nom_wt")));
					edoValueObject.setNomVol(CommonUtility.deNull(rs.getString("NOM_VOL")));
					edoValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
					edoValueObject.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
					edoValueObject.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
					edoValueObject.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
					edoVect.add(edoValueObject);
				}

			}

			log.info(" fetchShutoutEdo  DAO  Result" + edoVect.toString());
		} catch (NullPointerException e) {
			log.error("Exception fetchShutoutEdo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception fetchShutoutEdo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchShutoutEdo  DAO  END");
		}
		return edoVect;

	}

	@Override
	public int getTotalCustCdByIcNumber(String nric, String type) throws BusinessException {
		String sql = "";
		int total_cust_cd = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getTotalCustCdByIcNumber  DAO  Start nric " + nric + " type" + type);

			if ("P".equalsIgnoreCase(type)) {
				sb.append(
						"SELECT COUNT(*) AS TOTAL_CUST_CD FROM JC_CARDDTL  WHERE (ID_NO     = :nric   OR CARD_SERIALNO = :nric OR PASSPORT_NO = :nric)");
				sb.append(" AND EXPIRY_DT >= SYSDATE");
			} else {
				sb.append(
						"SELECT COUNT(*) AS TOTAL_CUST_CD FROM JC_CARDDTL WHERE (1=1) AND STATUS_CD  IN ('USE','SUS') AND (ID_NO = :nric OR FIN_NO= :nric) ");
				sb.append(" AND EXPIRY_DT >= SYSDATE");
			}

			sql = sb.toString();

			paramMap.put("nric", nric);

			log.info("***getTotalCustCdByIcNumber*** sql " + sql);

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				total_cust_cd = rs.getInt("TOTAL_CUST_CD");
			}

			log.info("END: *** getTotalCustCdByIcNumber Result *****" + total_cust_cd);
		} catch (NullPointerException e) {
			log.error("Exception getTotalCustCdByIcNumber :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getTotalCustCdByIcNumber :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTotalCustCdByIcNumber  DAO  END");
		}
		return total_cust_cd;
	}

	@Override
	public String createShutoutDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String userid, String icType,
			String searchcrg, String tesn_nbr, String cargoDes) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		String cargoDest = "";
		String sql = "";
		String DNNbr = "";
		String ftrans = "";
		String edosql = "";
		String edoupdsql = "";
		String dnnbrtrans = "";
		String sqlveh1 = "";
		String sqlveh1_trans = "";
		String sql_trans = "";
		String sqltlog = "";
		int stransno = 0;
		int cnt_trans = 0;
		SqlRowSet rs = null;
		StringTokenizer dntrans = null;
		int countua = 0;
		int count = 0;
		double Bill_ton = 0.0;
		double dn_nom_wt = 0;
		double dn_nom_vol = 0;
		int newuanbrpkgs = 0;
		try {
			log.info("START: createShutoutDN DAO edoNbr:" + edoNbr + " transtype:" + transtype + " transtype:"
					+ transtype + " edo_Nbr_Pkgs:" + edo_Nbr_Pkgs + " NomWt:" + NomWt + " NomVol:" + NomVol
					+ " date_time:" + date_time + " transQty:" + transQty + " nric_no:" + nric_no + " dpname:" + dpname
					+ " veh1:" + veh1 + " userid:" + userid + " icType:" + icType + " searchcrg:" + searchcrg
					+ " tesn_nbr:" + tesn_nbr + " cargoDes:" + cargoDes);

			if (cargoDes.equalsIgnoreCase("Vessel"))
				cargoDest = "V";
			else if (cargoDes.equalsIgnoreCase("Leased Area"))
				cargoDest = "L";
			else
				cargoDest = "O";
			dnnbrtrans = getDNNbr(edoNbr);
			dntrans = new java.util.StringTokenizer(dnnbrtrans, "-");
			DNNbr = (dntrans.nextToken()).trim();
			ftrans = (dntrans.nextToken()).trim();
			log.info("ftrans : " + ftrans);
			dn_nom_wt = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomWt);
			dn_nom_vol = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomVol);
			if ((dn_nom_wt / 1000) > dn_nom_vol) {
				Bill_ton = dn_nom_wt / 1000;
			} else {
				Bill_ton = dn_nom_vol;
			}
			edosql = "select * from gb_edo where edo_asn_nbr = :edoNbr ";
			paramMap.put("edoNbr", edoNbr);
			// boolean checkEdoStuff = chkEDOStuffing(edoNbr);

			edoupdsql = "update gb_edo set dn_nbr_pkgs =";
			sb.append(" insert into dn_details(TRUCK_NBR,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
			sb.append(
					" TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
			sb.append(
					" LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR, CRG_DEST, GATE_OUT_DTTM) VALUES(:veh1, :DNNbr,:edoNbr,'A',");

			sql = sb.toString();
			paramMap.put("veh1", veh1);
			paramMap.put("DNNbr", DNNbr);
			paramMap.put("edoNbr", edoNbr);

			sb.setLength(0);
			sb.append(
					" insert into dn_details_trans(TRUCK_NBR,trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
			sb.append(
					" TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
			sb.append(
					" LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR, CRG_DEST, GATE_OUT_DTTM) VALUES(:veh1,'0',:DNNbr,:edoNbr,'A',");

			sql_trans = sb.toString();
			paramMap.put("veh1", veh1);
			paramMap.put("DNNbr", DNNbr);
			paramMap.put("edoNbr", edoNbr);

			sqlveh1 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,1,:veh1)";
			sqlveh1_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,1,:veh1)";

			rs = namedParameterJdbcTemplate.queryForRowSet(edosql, paramMap);
			if (rs.next()) {
				newuanbrpkgs = rs.getInt("dn_nbr_pkgs");
				newuanbrpkgs = newuanbrpkgs + Integer.parseInt(transQty);
			}

			edoupdsql = edoupdsql + newuanbrpkgs + " where nbr_pkgs >= (select nvl(sum(nbr_pkgs),0)+" + transQty
					+ " from dn_details where dn_status='A' and edo_asn_nbr='" + edoNbr + "') and edo_asn_nbr ='"
					+ edoNbr + "'";
			count = namedParameterJdbcTemplate.update(edoupdsql, paramMap);

			sqltlog = "SELECT MAX(TRANS_NBR) FROM gb_edo_TRANS WHERE Edo_ASN_NBR='" + edoNbr + "'";
			SqlRowSet rs1 = null;
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);

				if (rs1.next()) {
					stransno = (rs1.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
				log.info("stransno : " + stransno);
			}

			if (count == 0) {
				log.info("Record Cannot be added to Database");
				DNNbr = "";
				throw new BusinessException("M4201");
			} else if (count > 0) {

				sb.setLength(0);
				sb.append(
						" :nric_no ,:dpname ,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:transQty, :dn_nom_wt ,:dn_nom_vol ,:Bill_ton ");
				sb.append(" ,:userid ,sysdate,:userid,sysdate,'C',:transtype , null, :cargoDest,");
				sql = sql + sb.toString();

				paramMap.put("nric_no", nric_no);
				paramMap.put("dpname", dpname);
				paramMap.put("icType", icType);
				paramMap.put("date_time", date_time);
				paramMap.put("transQty", transQty);
				paramMap.put("dn_nom_wt", String.valueOf(dn_nom_wt));
				paramMap.put("dn_nom_vol", String.valueOf(dn_nom_vol));
				paramMap.put("Bill_ton", String.valueOf(Bill_ton));
				paramMap.put("userid", userid);
				paramMap.put("transtype", transtype);
				paramMap.put("cargoDest", cargoDest);

				if (cargoDest.equals("O")) {
					sb.setLength(0);
					sb.append(
							" CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') THEN NULL ELSE TO_DATE(:date_time ,'DD/MM/YYYY HH24:MI') END)");

					sql = sql + sb.toString();
					paramMap.put("date_time", date_time);
				} else {
					sql = sql + "null)";
				}
				sb.setLength(0);
				sb.append(
						" :nric_no , :dpname ,:icType ,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:transQty , :dn_nom_wt ,:dn_nom_vol,:Bill_ton ");
				sb.append(" , :userid ,sysdate, :userid, sysdate,'C',:transtype ,null, :cargoDest,");

				sql_trans = sql_trans + sb.toString();
				paramMap.put("nric_no", nric_no);
				paramMap.put("dpname", dpname);
				paramMap.put("icType", icType);
				paramMap.put("date_time", date_time);
				paramMap.put("transQty", transQty);
				paramMap.put("dn_nom_wt", String.valueOf(dn_nom_wt));
				paramMap.put("dn_nom_vol", String.valueOf(dn_nom_vol));
				paramMap.put("Bill_ton", String.valueOf(Bill_ton));
				paramMap.put("userid", userid);
				paramMap.put("transtype", transtype);
				paramMap.put("cargoDest", cargoDest);

				if (cargoDest.equals("O")) {
					sb.setLength(0);
					sb.append(
							" CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') THEN NULL ELSE TO_DATE(:date_time ,'DD/MM/YYYY HH24:MI') END)");

					sql_trans = sql_trans + sb.toString();
					paramMap.put("date_time", date_time);
				} else {
					sql_trans = sql_trans + "null)";
				}

				countua = namedParameterJdbcTemplate.update(sql, paramMap);
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					cnt_trans = namedParameterJdbcTemplate.update(sql_trans, paramMap);
					log.info("cnt_trans : " + cnt_trans);
				}

				if (veh1 != null && !veh1.equals("")) {
					int cntveh1 = namedParameterJdbcTemplate.update(sqlveh1, paramMap);
					log.info("cntveh1 : " + cntveh1);
					if (logStatusGlobal.equalsIgnoreCase("Y")) {
						int cntveh1_trans = namedParameterJdbcTemplate.update(sqlveh1_trans, paramMap);
						log.info("cntveh1_trans : " + cntveh1_trans);
					}
				}
			}
			if (countua == 0) {
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

			// Commented on 3/6/2016 Sripriya to nnot allow to update shutoutdelivery pkgs
			// in Booking as it is done @ Shutout Out Cargo SCreen
			// update booking_details's shutout delivery packages
			// String bookingRefNbr = getBookingRefNbr(edoNbr);
			// if (bookingRefNbr!=null && bookingRefNbr.length()>0){
			// updateBookingShutoutDeliveryPkgs(bookingRefNbr,Integer.parseInt(transQty));
			// }

		} catch (NullPointerException e) {
			log.error("Exception getTotalCustCdByIcNumber :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getTotalCustCdByIcNumber :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTotalCustCdByIcNumber  DAO Result:" + DNNbr);
		}
		return DNNbr;
	}

	private String getDNNbr(String edoNo) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		String DnNbr = "";
		String ftrans = "";
		String retval = "";
		int count = 0;
		int tempInt = 0;
		String tempval = "";
		String tempua = "";
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getDNNbr  DAO  Start edoNo " + edoNo);

			if (edoNo.length() == 7) {
				edoNo = "0" + edoNo;
			} else if (edoNo.length() == 6) {
				edoNo = "00" + edoNo;
			}

			sb.append("select max(DN_NBR) as maxDnNbr from dn_details where dn_nbr like 'D" + edoNo + "%'");

			sql = sb.toString();

			log.info("***getDNNbr*** sql " + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				if (rs.getString("maxDnNbr") != null && rs.getString("maxDnNbr").length() != 0) {
					count = 1;
					tempua = rs.getString("maxDnNbr");
				} else
					count = 0;
			}
			// LogManager.instance.logInfo("Count--" + count);
			if (count == 0) {
				DnNbr = "D" + edoNo + "0000";
				ftrans = "True";
			} else {
				if (tempua.length() == 12)
					tempua = "D0" + (tempua).substring(1);
				else if (tempua.length() == 11)
					tempua = "D00" + (tempua).substring(1);

				tempInt = Integer.parseInt((tempua).substring(9, 13));
				tempInt = tempInt + 1;

				if (tempInt >= 0 && tempInt <= 9) {
					tempval = "000" + tempInt;
				} else {
					if (tempInt >= 10 && tempInt <= 99) {
						tempval = "00" + tempInt;
					} else if (tempInt >= 100 && tempInt <= 999) {
						tempval = "0" + tempInt;
					} else {
						tempval = "" + tempInt;
					}
				} // inside if close
				DnNbr = "D" + edoNo + tempval;
				ftrans = "False";
			} // outside if close

			retval = DnNbr + "-" + ftrans;

			log.info("END: *** getDNNbr Result *****" + retval);
		} catch (NullPointerException e) {
			log.error("Exception getDNNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getDNNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDNNbr  DAO  END");
		}
		return retval;
	}

	@Override
	public String createDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String userid, String icType,
			String searchcrg, String tesn_nbr, String cargoDes) throws BusinessException {
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String cargoDest = "";
		// -- VietND02
		String DNNbr = "";
		String edosql = "";
		String sqlJp = "";
		String sqlupdJp = "";
		String edoupdsql = "";
		String dnnbrtrans = "";
		String sqlveh1 = "";
		// lak added for audit trial 23/01/2003 start
		String sqlupdJp_trans = "";
		String sqlveh1_trans = "";
		String sql_trans = "";
		String sqltlog1 = "";
		String sqltlog = "";
		int stransno = 0;
		int stransno1 = 0;
		StringTokenizer dntrans = null;
		int countua = 0;
		int count = 0;
		int count1 = 0;
		double Bill_ton = 0.0;
		double dn_nom_wt = 0;
		double dn_nom_vol = 0;
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: getTotalCustCdByIcNumber  DAO  Start edoNbr " + edoNbr + " transtype" + transtype
					+ " edo_Nbr_Pkgs" + edo_Nbr_Pkgs + " NomWt" + NomWt + " NomVol" + NomVol + " date_time" + date_time
					+ " transQty" + transQty + " nric_no" + nric_no + " dpname" + dpname + " veh1" + veh1 + " userid"
					+ userid + " icType" + icType + " searchcrg" + searchcrg + " tesn_nbr" + tesn_nbr + " cargoDes"
					+ cargoDes);

			if (cargoDes.equalsIgnoreCase("Vessel"))
				cargoDest = "V";
			else if (cargoDes.equalsIgnoreCase("Leased Area"))
				cargoDest = "L";
			else
				cargoDest = "O"; // cargoDes = Out of JP
			dnnbrtrans = getDNNbr(edoNbr);
			dntrans = new StringTokenizer(dnnbrtrans, "-");
			DNNbr = (dntrans.nextToken()).trim();
			dn_nom_wt = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomWt);
			dn_nom_vol = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomVol);
			// if(ftrans.equals("True"))
			// {
			if ((dn_nom_wt / 1000) > dn_nom_vol) {
				Bill_ton = dn_nom_wt / 1000;
			} else {
				Bill_ton = dn_nom_vol;
			} // end if nomwt

			edosql = "select * from gb_edo where edo_asn_nbr = :edoNbr";

			String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
			// LogManager.instance.logInfo("22222chktesnJpJp_nbr == " + chktesnJpJp_nbr);
			String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);
			boolean checkEdoStuff = chkEDOStuffing(edoNbr);

			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
				if (chktesnJpJp_nbr.equals("Y")) {
					sqlJp = "select * from tesn_jp_jp where esn_asn_nbr = :tesn_nbr ";

					sqlupdJp = "update tesn_jp_jp set dn_nbr_pkgs =";

					// lak added for Audit Trial
					sqlupdJp_trans = "insert into tesn_jp_jp_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
					sqltlog1 = "SELECT MAX(TRANS_NBR) FROM tesn_jp_jp_TRANS WHERE esn_asn_NBR= :tesn_nbr";
					// LogManager.instance.logInfo("sqlJp :" + sqlJp+" //sqlupdJp :"+sqlupdJp+"
					// //sqlupdJp_trans :"+sqlupdJp_trans+" //sqltlog1 :"+sqltlog1);
				} else if (chktesnJpPsa_nbr.equals("Y")) {
					sqlJp = "select * from tesn_jp_psa where esn_asn_nbr = :tesn_nbr ";
					sqlupdJp = "update tesn_jp_psa set dn_nbr_pkgs =";
					// lak added for Audit Trial
					sqlupdJp_trans = "insert into tesn_jp_psa_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
					sqltlog1 = "SELECT MAX(TRANS_NBR) FROM tesn_jp_psa_TRANS WHERE esn_asn_NBR= :tesn_nbr ";
					// LogManager.instance.logInfo("sqlJp :" + sqlJp+" //sqlupdJp :"+sqlupdJp+"
					// //sqlupdJp_trans :"+sqlupdJp_trans+" //sqltlog1 :"+sqltlog1);
				}

			}

			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
				edoupdsql = "update gb_edo set last_modify_dttm = sysdate, dn_nbr_pkgs ="; // "update bulk_gb_edo set
																							// dn_wt =";VANI-16th JUly
				/*
				 * sql =
				 * "insert into dn_details(DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
				 * +
				 * "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
				 * + "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE) VALUES('" + DNNbr + "','" +
				 * edoNbr + "','A','";
				 */
				// ++ VietND Change SQL
				sql = "insert into dn_details(TRUCK_NBR,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
						+ "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
						+ "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE, CRG_DEST, GATE_OUT_DTTM) VALUES(:veh1,:DNNbr,:edoNbr,'A',";
				// -- VietND
				// laks added for audit trial
				/*
				 * sql_trans =
				 * "insert into dn_details_trans(trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
				 * +
				 * "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
				 * + "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE) VALUES('0','" + DNNbr + "','"
				 * + edoNbr + "','A','";
				 */
				// ++ VietND Change SQL
				sql_trans = "insert into dn_details_trans(TRUCK_NBR,trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
						+ "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
						+ "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE, CRG_DEST, GATE_OUT_DTTM) VALUES(:veh1,'0', :DNNbr, :edoNbr ,'A', ";
				// -- VietND
				// edoupdsql_trans = "insert into gb_edo_trans
				// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,dn_nbr_pkgs,remarks)
				// values(";
				// LogManager.instance.logInfo("sql :" +sql+" //sql_trans :"+sql_trans);
			} else {

				edoupdsql = "update gb_edo set last_modify_dttm = sysdate, trans_dn_nbr_pkgs =";
				/*
				 * sql =
				 * "insert into dn_details(DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
				 * +
				 * "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
				 * + "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR) VALUES('" + DNNbr
				 * + "','" + edoNbr + "','A','";
				 */
				// ++ VietND Change SQL
				sql = "insert into dn_details(TRUCK_NBR,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
						+ "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
						+ "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR, CRG_DEST, GATE_OUT_DTTM) VALUES(:veh1,:DNNbr,:edoNbr,'A',";
				// -- VietND

				// laks added for audit trial
				/*
				 * sql_trans =
				 * "insert into dn_details_trans(trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
				 * +
				 * "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
				 * + "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR) VALUES('0','" +
				 * DNNbr + "','" + edoNbr + "','A','";
				 */
				// ++ VietND Change SQL
				sql_trans = "insert into dn_details_trans(TRUCK_NBR,trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
						+ "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
						+ "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR, CRG_DEST, GATE_OUT_DTTM) VALUES(:veh1,'0',:DNNbr,:edoNbr,'A',";
				// -- VietND
				// LogManager.instance.logInfo("edoupdsql :"+edoupdsql+" //sql :" +sql+"
				// //sql_trans :"+sql_trans);
				// edoupdsql_trans = "insert into gb_edo_trans
				// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,trans_dn_nbr_pkgs,remarks)
				// values(";
			}

			sqlveh1 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,1,:veh1)";

			sqlveh1_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,1,:veh1)";

			int newuanbrpkgs = 0;
			int newuanbrpkgs_tesn = 0;

			paramMap.put("DNNbr", DNNbr);
			paramMap.put("veh1", veh1);
			paramMap.put("edoNbr", edoNbr);
			paramMap.put("tesn_nbr", tesn_nbr);
			paramMap.put("userid", userid);
			rs = namedParameterJdbcTemplate.queryForRowSet(edosql, paramMap);

			if (rs.next()) {
				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT"))
					newuanbrpkgs = rs.getInt("dn_nbr_pkgs"); // ("dn_wt");
				else
					newuanbrpkgs = rs.getInt("TRANS_DN_NBR_PKGS");

				newuanbrpkgs = newuanbrpkgs + Integer.parseInt(transQty);
			}

			if ((checkEdoStuff) && (chktesnJpJp_nbr.equals("N")) && (chktesnJpPsa_nbr.equals("N"))
					&& (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("T")))
			// vinayak added 19 jan 2004 modified 06 feb 2004
			{
				sb.append(
						"SELECT nvl(csd.DN_NBR_PKGS,0) as DN_NBR_PKGS FROM cc_stuffing cs,cc_stuffing_details csd WHERE ");
				sb.append("cs.STUFF_CLOSED='Y' AND cs.ACTIVE_STATUS='A' AND cs.STUFF_SEQ_NBR=csd.STUFF_SEQ_NBR AND ");
				sb.append("csd.REC_STATUS='A' AND csd.STUFF_SEQ_NBR=:tesn_nbr  AND csd.EDO_ESN_NBR=:edoNbr");
				String strStuffSql = sb.toString();

				sb.setLength(0);
				sb.append("UPDATE cc_stuffing_details SET DN_NBR_PKGS=DN_NBR_PKGS+");
				sb.append(transQty);
				sb.append(" WHERE NBR_PKGS>=(SELECT NVL(SUM(NBR_PKGS),0)+ ");
				sb.append(transQty);
				sb.append(" FROM dn_details WHERE DN_STATUS='A' AND EDO_ASN_NBR=:edoNbr and tesn_asn_nbr=:tesn_nbr)");
				sb.append(" AND STUFF_SEQ_NBR=:tesn_nbr AND EDO_ESN_NBR=:edoNbr ");
				String strStuffSqlupd = sb.toString();

				paramMap.put("newuanbrpkgs", newuanbrpkgs);
				rs = namedParameterJdbcTemplate.queryForRowSet(strStuffSql, paramMap);
				if (rs.next()) {
					count1 = namedParameterJdbcTemplate.update(strStuffSqlupd, paramMap);
				}
			}

			sb.setLength(0);
			sb.append(edoupdsql);
			sb.append(":newuanbrpkgs where nbr_pkgs >= (select nvl(sum(nbr_pkgs),0)+");
			sb.append(transQty);
			sb.append(" from dn_details where dn_status='A' and edo_asn_nbr=:edoNbr");
			sb.append(" ) and edo_asn_nbr =:edoNbr");
			edoupdsql = sb.toString();
			paramMap.put("newuanbrpkgs", newuanbrpkgs);
			paramMap.put("edoNbr", edoNbr);
			count = namedParameterJdbcTemplate.update(edoupdsql, paramMap);

			sqltlog = "SELECT MAX(TRANS_NBR) FROM gb_edo_TRANS WHERE Edo_ASN_NBR=:edoNbr ";

			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);

				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
				log.info("stransno == " + stransno);
			}

			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
				if (chktesnJpJp_nbr.equals("Y") || chktesnJpPsa_nbr.equals("Y")) { // added by vinay 06 feb 2004
					rs = namedParameterJdbcTemplate.queryForRowSet(sqlJp, paramMap);
					if (rs.next()) {
						newuanbrpkgs_tesn = rs.getInt("DN_NBR_PKGS");
						newuanbrpkgs_tesn = newuanbrpkgs_tesn + Integer.parseInt(transQty);
					}
					sb.setLength(0);
					sb.append(sqlupdJp);
					sb.append(":newuanbrpkgs_tesn");
					sb.append(" where nbr_pkgs >= (select nvl(sum(nbr_pkgs),0)+ ");
					sb.append(transQty);
					sb.append(" from dn_details where dn_status='A' and tesn_asn_nbr=:tesn_nbr");
					sb.append(") and esn_asn_nbr =:tesn_nbr");
					sqlupdJp = sb.toString();

					paramMap.put("newuanbrpkgs_tesn", newuanbrpkgs_tesn);
					count1 = namedParameterJdbcTemplate.update(sqlupdJp, paramMap);
					// lak added for audit Trail

					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
						rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog1, paramMap);
						if (rs.next()) {
							stransno1 = (rs.getInt(1)) + 1;
						} else {
							stransno1 = 0;
						}
					}

					sb.setLength(0);
					sb.append(sqlupdJp_trans);
					sb.append(":stransno1, :edoNbr, :tesn_nbr");
					sb.append(",:newuanbrpkgs_tesn, sysdate, :userid,'DN Add')");
					sqlupdJp_trans = sb.toString();

					paramMap.put("stransno1", stransno1);
					paramMap.put("newuanbrpkgs_tesn", newuanbrpkgs_tesn);
					if (logStatusGlobal.equalsIgnoreCase("Y")) {
						namedParameterJdbcTemplate.update(sqlupdJp_trans, paramMap);
					}
					// lak added for audit Trail
				} // end of check
			}

			if (count == 0 || (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT") && count1 == 0)) {
				log.info("Record Cannot be added to Database");
				DNNbr = "";
				throw new BusinessException("M4201");
			} else if (count > 0) {
				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
					sb.setLength(0);
					sb.append(sql);
					sb.append(":nric_no, :dpname, :icType, to_date(:date_time,'DD/MM/YYYY HH24:MI'),");
					sb.append(":transQty, :dn_nom_wt, :dn_nom_vol, :Bill_ton, :userid");
					sb.append(",sysdate, :userid, sysdate, 'C', :transtype, :cargoDest,");
					sql = sb.toString();

					// For back-dated DN of more than x hours for cargo destination = Out of JP,
					// default the truck's gate-out time as the back-dated time. 26/5/2010.
					if (cargoDest.equals("O")) {// Cargo Destination = Out of JP
						sb.setLength(0);
						sb.append(sql);
						sb.append(" CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-");
						sb.append("(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') ");
						sb.append("THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)");
						sql = sb.toString();
					} else {
						sql = sql + "null)";
					}

					// lak added for Audit trail
					sb.setLength(0);
					sb.append(sql_trans);
					sb.append(":nric_no, :dpname, :icType, to_date(:date_time,'DD/MM/YYYY HH24:MI'),");
					sb.append(":transQty, :dn_nom_wt, :dn_nom_vol, :Bill_ton, :userid");
					sb.append(",sysdate, :userid, sysdate, 'C', :transtype, :cargoDest,");
					sql_trans = sb.toString();

					// For back-dated DN of more than x hours for cargo destination = Out of JP,
					// default the truck's gate-out time as the back-dated time. 26/5/2010.
					if (cargoDest.equals("O")) {// Cargo Destination = Out of JP
						sb.setLength(0);
						sb.append(sql_trans);
						sb.append(" CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-");
						sb.append("(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') ");
						sb.append("THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)");
						sql_trans = sb.toString();
					} else {
						sql_trans = sql_trans + "null)";
					}

				} else {
					sb.setLength(0);
					sb.append(sql);
					sb.append(":nric_no, :dpname, :icType, to_date(:date_time,'DD/MM/YYYY HH24:MI'),");
					sb.append(":transQty, :dn_nom_wt, :dn_nom_vol, :Bill_ton, :userid");
					sb.append(",sysdate, :userid, sysdate, 'C', :transtype, :tesn_nbr, :cargoDest,");
					sql = sb.toString();

					// For back-dated DN of more than x hours for cargo destination = Out of JP,
					// default the truck's gate-out time as the back-dated time. 26/5/2010.
					if (cargoDest.equals("O")) { // Cargo Destination = Out of JP
						sb.setLength(0);
						sb.append(sql);
						sb.append(" CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-");
						sb.append("(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') ");
						sb.append("THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)");
						sql = sb.toString();
					} else {
						sql = sql + "null)";
					}

					// lak added for Audit trail

					sb.setLength(0);
					sb.append(sql_trans);
					sb.append(":nric_no, :dpname, :icType, to_date(:date_time,'DD/MM/YYYY HH24:MI'),");
					sb.append(":transQty, :dn_nom_wt, :dn_nom_vol, :Bill_ton, :userid");
					sb.append(",sysdate, :userid, sysdate, 'C', :transtype, :tesn_nbr, :cargoDest,");
					sql_trans = sb.toString();

					// For back-dated DN of more than x hours for cargo destination = Out of JP,
					// default the truck's gate-out time as the back-dated time. 26/5/2010.
					if (cargoDest.equals("O")) {// Cargo Destination = Out of JP
						sb.setLength(0);
						sb.append(sql_trans);
						sb.append(" CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-");
						sb.append("(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') ");
						sb.append("THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)");
						sql_trans = sb.toString();
					} else {
						sql_trans = sql_trans + "null)";
					}

				}

				paramMap.put("nric_no", nric_no);
				paramMap.put("dpname", dpname);
				paramMap.put("icType", icType);
				paramMap.put("date_time", date_time);
				paramMap.put("transQty", transQty);
				paramMap.put("dn_nom_wt", dn_nom_wt);
				paramMap.put("dn_nom_vol", dn_nom_vol);
				paramMap.put("Bill_ton", Bill_ton);
				paramMap.put("userid", userid);
				paramMap.put("transtype", transtype);
				paramMap.put("cargoDest", cargoDest);

				log.info("SQL: " + sql);
				log.info("paramMap: " + paramMap);
				countua = namedParameterJdbcTemplate.update(sql, paramMap);

				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info("SQL sql_trans: " + sql_trans);
					log.info("paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(sql_trans, paramMap);
				}

				if (veh1 != null && !veh1.equals("")) {
					namedParameterJdbcTemplate.update(sqlveh1, paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 24/01/2003
						namedParameterJdbcTemplate.update(sqlveh1_trans, paramMap);
					}
				}
				/*
				 * if (veh2 != null && !veh2.equals("")) { int cntveh2 =
				 * stmt.executeUpdate(sqlveh2);
				 * LogManager.instance.logInfo("14141414141414 sqlveh2 == " +
				 * sqlveh2+" //cntveh2 :"+cntveh2); if (logStatusGlobal.equalsIgnoreCase("Y")) {
				 * // Transaction Log Table Insertion 24/01/2003 int cntveh2_trans =
				 * stmt.executeUpdate(sqlveh2_trans);
				 * LogManager.instance.logInfo("15151515151515 sqlveh2_trans == " +
				 * sqlveh2_trans + "\tcntveh2_trans == " + cntveh2_trans); } } if (veh3 != null
				 * && !veh3.equals("")) { int cntveh3 = stmt.executeUpdate(sqlveh3);
				 * LogManager.instance.logInfo("16161616161616 sqlveh3 " +
				 * sqlveh3+" //cntveh3 :"+cntveh3); if (logStatusGlobal.equalsIgnoreCase("Y")) {
				 * // Transaction Log Table Insertion 24/01/2003 int cntveh3_trans =
				 * stmt.executeUpdate(sqlveh3_trans);
				 * LogManager.instance.logInfo("16161616161616 sqlveh3_trans " +
				 * sqlveh3_trans+" //cntveh3_trans :"+cntveh3_trans); } } if (veh4 != null &&
				 * !veh4.equals("")) { int cntveh4 = stmt.executeUpdate(sqlveh4);
				 * LogManager.instance.logInfo("17171717171717 sqlveh4 " +
				 * sqlveh4+" //cntveh4 :"+cntveh4); if (logStatusGlobal.equalsIgnoreCase("Y")) {
				 * // Transaction Log Table Insertion 24/01/2003 int cntveh4_trans =
				 * stmt.executeUpdate(sqlveh4_trans);
				 * LogManager.instance.logInfo("18181818181818 sqlveh4_trans " +
				 * sqlveh4_trans+" //cntveh4_trans :"+cntveh4_trans); } } if (veh5 != null &&
				 * !veh5.equals("")) { int cntveh5 = stmt.executeUpdate(sqlveh5);
				 * LogManager.instance.logInfo("19191919191919 sqlveh5 " +
				 * sqlveh5+" //cntveh5 :"+cntveh5); if (logStatusGlobal.equalsIgnoreCase("Y")) {
				 * // Transaction Log Table Insertion 24/01/2003 int cntveh5_trans =
				 * stmt.executeUpdate(sqlveh5_trans);
				 * LogManager.instance.logInfo("20202020202020 sqlveh5_trans " +
				 * sqlveh5_trans+" //cntveh5_trans :"+cntveh5_trans); } }
				 */
			} // end if count

			if (countua == 0) {
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
		} catch (NullPointerException e) {
			log.error("Exception getTotalCustCdByIcNumber :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getTotalCustCdByIcNumber :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTotalCustCdByIcNumber  DAO  END");
		}
		return DNNbr;
	}

	@Override
	public boolean isTESN_JP_JP(String edoNbr, String esnNbr) throws BusinessException {
		boolean isJP = false;
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: isTESN_JP_JP  DAO  Start edoNbr " + edoNbr + "esnNbr " + esnNbr);

			sb.append("select * from TESN_JP_JP where EDO_ASN_NBR=:edoNbr and ESN_ASN_NBR=:esnNbr");

			paramMap.put("edoNbr", edoNbr);
			paramMap.put("esnNbr", esnNbr);

			sql = sb.toString();

			log.info(" isTESN_JP_JP  DAO  SQL " + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				isJP = true;
			} else {
				isJP = false;
			}

			log.info("END: *** isTESN_JP_JP Result *****" + isJP);
		} catch (NullPointerException e) {
			log.error("Exception isTESN_JP_JP :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception isTESN_JP_JP :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isTESN_JP_JP  DAO  END");
		}
		return isJP;
	}

	@Override
	public void updateDN(String cntrNo, String dnNo) throws BusinessException {
		int count = 0;
		String sql = "";
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: updateDN  DAO  Start cntrNo " + cntrNo + "dnNo " + dnNo);

			sb.append(" update dn_details set cntr_nbr = :cntrNo, MOT_CREATE_DTTM = :timestamp ");
			sb.append(" where dn_nbr = :dnNo");

			sql = sb.toString();

			paramMap.put("cntrNo", cntrNo);
			paramMap.put("timestamp", new Timestamp(new Date().getTime()));
			paramMap.put("dnNo", dnNo);

			log.info(" updateDN  DAO  SQL " + sql.toString());
			count = namedParameterJdbcTemplate.update(sql, paramMap);
		} catch (NullPointerException e) {
			log.error("Exception updateDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception updateDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateDN  DAO Result:" + count);
		}
	}

	@Override
	public void updateCntr(String cntrseq, String cntrNo, String user, String newCatCode) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String status = "";
		int weight = 0;
		int count = 0;
		String descCntr = "";
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: updateCntr  DAO  Start cntrseq " + cntrseq + "cntrNo " + cntrNo + "user " + user
					+ "newCatCode" + newCatCode);

			sb.setLength(0);
			sb.append("select STATUS, DECLR_WT from CNTR where CNTR_SEQ_NBR = :cntrseq");

			paramMap.put("cntrseq", cntrseq);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				status = rs.getString("STATUS");
				weight = rs.getInt("DECLR_WT");
			}

			descCntr = status + weight;

			if ("E".equals(status)) {
				// update the status of cntr to F

				sb = new StringBuffer();
				sb.append("UPDATE CNTR SET CAT_CD = :newCatCode, STATUS = 'F', LAST_MODIFY_DTTM = sysdate,");
				sb.append(" LAST_MODIFY_USER_ID = :user ");
				sb.append(" WHERE CNTR_SEQ_NBR= :cntrseq ");
				// LogManager.instance.logInfo("SQL DN:" + sqla + "," + newCatCode + "," + user
				// + "," + cntrseq);
				String sqla = sb.toString();
				paramMap.put("newCatCode", newCatCode);
				paramMap.put("user", user);
				paramMap.put("cntrseq", cntrseq);
				count = namedParameterJdbcTemplate.update(sqla, paramMap);
				log.info("count:" + count);

			}
			// capture dttm
			sb = new StringBuffer();
			sb.append("INSERT INTO CNTR_TXN(CNTR_SEQ_NBR, CNTR_NBR, TXN_CD, TXN_DTTM,");
			sb.append(" LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID, ERROR_MSG) ");
			sb.append("VALUES(:cntrseq, :cntrNo, 'STF', :timestamp, sysdate, :user, :descCntr)");

			String sqlb = sb.toString();

			paramMap.put("cntrseq", cntrseq);
			paramMap.put("cntrNo", cntrNo);
			paramMap.put("user", user);
			paramMap.put("descCntr", descCntr);
			paramMap.put("timestamp", new Timestamp(new java.util.Date().getTime()));
			count = namedParameterJdbcTemplate.update(sqlb, paramMap);

		} catch (NullPointerException e) {
			log.error("Exception updateCntr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception updateCntr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateCntr  DAO Result:" + count);
		}
	}

	@Override
	public boolean chkCntrCrgDn(String strDnNbr) throws BusinessException {
		boolean isCntrCrgDn = false;
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: chkCntrCrgDn  DAO  Start strDnNbr " + strDnNbr);

			// "SELECT 'TRUE' FROM manifest_details WHERE bl_status = 'A' AND bl_nbr=" +
			sb.append("SELECT count(*) FROM manifest_details WHERE  bl_status = 'A' AND bl_nbr=");
			sb.append("(SELECT bl_nbr FROM gb_edo WHERE edo_asn_nbr = (SELECT edo_asn_nbr FROM dn_details ");
			sb.append("WHERE dn_nbr=:strDnNbr");
			sb.append(")) AND unstuff_seq_nbr<>0 AND var_nbr = (SELECT var_nbr ");
			sb.append("FROM gb_edo WHERE edo_asn_nbr = (SELECT edo_asn_nbr FROM dn_details WHERE dn_nbr=");
			sb.append(":strDnNbr))");

			sql = sb.toString();

			paramMap.put("strDnNbr", strDnNbr);

			log.info(" chkCntrCrgDn  DAO  SQL " + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				// LogManager.instance.logInfo("rs.getString(1)-" + rs.getInt(1));
				if (rs.getInt(1) == 1)
					isCntrCrgDn = true;
				else {
					isCntrCrgDn = false;
					// LogManager.instance.logInfo("chkCntrCrgDn-false");
				}
			} else {
				log.info("chkCntrCrgDn-No record");
			}

			log.info("END: *** chkCntrCrgDn Result *****" + isCntrCrgDn);
		} catch (NullPointerException e) {
			log.error("Exception chkCntrCrgDn :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception chkCntrCrgDn :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkCntrCrgDn  DAO  END");
		}
		return isCntrCrgDn;
	}

	@Override
	public boolean chkraiseCharge(String edonbr) throws BusinessException {
		boolean countua = false;
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: chkraiseCharge  DAO  Start edonbr " + edonbr);

			sb.append("select to_char(b.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb from gb_edo a,berthing b");
			sb.append(",vessel_call vc where a.var_nbr =  vc.vv_cd and vc.vv_cd = b.vv_cd and a.edo_asn_nbr=:edonbr");
			sb.append(" and to_date(to_char(b.ATB_DTTM,'ddmmyyyy'),'ddmmyyyy') >= to_date('06112002','ddmmyyyy')");

			paramMap.put("edonbr", edonbr);
			sql = sb.toString();

			log.info(" chkraiseCharge  DAO  SQL " + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next())
				countua = true;

			log.info("END: *** chkraiseCharge Result *****" + countua);
		} catch (NullPointerException e) {
			log.error("Exception chkraiseCharge :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception chkraiseCharge :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkraiseCharge  DAO  END");
		}
		return countua;
	}

	@Override
	public List<GcOpsDnReport> getDNPrintJasper(String dnNbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<GcOpsDnReport> listDnDetails = new ArrayList<GcOpsDnReport>();
		try {
			log.info("getDNPrintJasper DAO START uaNbr:" + dnNbr);

			sb.append(
					"SELECT DISTINCT TO_CHAR(WEBDNUATEMP.DATETIME, 'DD/MM/YYYY HH24:MI') datetime,WEBDNUATEMP.TRANSREFNO AS DNVIEWRPT_DN_NBR,");
			sb.append(" TO_CHAR(WEBDNUATEMP.ATB, 'DD/MM/YYYY HH24:MI') ATB,");
			sb.append("       WEBDNUATEMP.VSLNM,WEBDNUATEMP.VOYNO,WEBDNUATEMP.CONTNO,");
			sb.append("       WEBDNUATEMP.TRANSTYPE,WEBDNUATEMP.CONTSIZE,WEBDNUATEMP.CONTTYPE,");
			sb.append("       WEBDNUATEMP.ASNNO,WEBDNUATEMP.CRGREF,WEBDNUATEMP.WT,WEBDNUATEMP.VOL,");
			sb.append("       WEBDNUATEMP.DECLQTY,WEBDNUATEMP.TRANSQTY,WEBDNUATEMP.BALQTY,");
			sb.append("       WEBDNUATEMP.NRICPASSPORTNO,WEBDNUATEMP.VEH1,WEBDNUATEMP.MARKING,");
			sb.append(
					"       WEBDNUATEMP.CRG_DESC,TO_CHAR(WEBDNUATEMP.COD, 'DD/MM/YYYY HH24:MI') COD,SST_BILL.TARRIF_CD_SER_CHRG,");
			sb.append("       SST_BILL.TARRIF_DESC_SER_CHRG,SST_BILL.BILLABLE_TON_SER_CHRG,");
			sb.append("       SST_BILL.UNIT_RATE_SER_CHRG,SST_BILL.TOTAL_AMT_SER_CHRG,");
			sb.append("       SST_BILL.TOTAL_AMT_WHARF_CHRG,SST_BILL.TOTAL_AMT_STORE_CHRG,");
			sb.append("       SST_BILL.TOTAL_AMT_SER_WHARF_CHRG,SST_BILL.TOTAL_AMT_SR_CHRG,");
			sb.append("       SST_BILL.ACCT_NBR_SER_CHRG,SST_BILL.EDO_ACCT_NBR,SST_BILL.TARRIF_CD_WHARF_CHRG,");
			sb.append("       SST_BILL.TARRIF_DESC_WHARF_CHRG,SST_BILL.BILLABLE_TON_WHARF_CHRG,");
			sb.append("       SST_BILL.UNIT_RATE_WHARF_CHRG,SST_BILL.TARRIF_CD_STORE_CHRG,");
			sb.append("       SST_BILL.TARRIF_DESC_STORE_CHRG,SST_BILL.BILLABLE_TON_STORE_CHRG,");
			sb.append("       SST_BILL.UNIT_RATE_STORE_CHRG,SST_BILL.TARRIF_CD_SR_CHRG,");
			sb.append("       SST_BILL.TARRIF_DESC_SR_CHRG,SST_BILL.BILLABLE_TON_SR_CHRG,");
			sb.append("       SST_BILL.UNIT_RATE_SR_CHRG,SST_BILL.UNIT_RATE_SER_WHARF_CHRG,");
			sb.append("       SST_BILL.BILLABLE_TON_SER_WHARF_CHRG,SST_BILL.TARRIF_DESC_SER_WHARF_CHRG,");
			sb.append("       SST_BILL.TARRIF_CD_SER_WHARF_CHRG,SST_BILL.TIME_UNIT_SER,");
			sb.append("       SST_BILL.TIME_UNIT_WHF,SST_BILL.TIME_UNIT_SR,SST_BILL.TIME_UNIT_SER_WHF,");
			sb.append("       SST_BILL.TIME_UNIT_STORE,DN_DETAILS.\"CRG_DEST\" AS DN_DETAILS_CRG_DEST,");
			sb.append("       (");
			sb.append(
					"     	CASE WHEN (GB_EDO.\"TA_NAME\" IS NULL AND GB_EDO.\"TA_CUST_CD\" IS NULL) THEN replace(DNVIEWRPT.\"SCHEME\", 'Delivery Note must be endorsed by AB Operator:', 'Delivery Note must be endorsed by ')");
			sb.append(
					"        	WHEN (GB_EDO.\"TA_NAME\" IS NULL AND GB_EDO.\"TA_CUST_CD\" IS NOT NULL) THEN ('Delivery Note must be endorsed by ' || GB_EDO.\"TA_CUST_CD\")");
			sb.append(
					"        	WHEN (GB_EDO.\"TA_NAME\" IS NOT NULL ) THEN ('Delivery Note must be endorsed by ' || GB_EDO.\"TA_NAME\")");
			sb.append("        END) AS DNVIEWRPT_SCHEME");
			sb.append(
					" FROM  ((((DN_DETAILS INNER JOIN WEBDNUATEMP WEBDNUATEMP ON WEBDNUATEMP.TRANSREFNO = DN_DETAILS.DN_NBR)");
			sb.append("        INNER JOIN VESSELSCHEME VESSELSCHEME ON WEBDNUATEMP.VV_CD=VESSELSCHEME.VV_CD ) ");
			sb.append("        INNER JOIN SST_BILL SST_BILL ON WEBDNUATEMP.TRANSREFNO=SST_BILL.DN_UA_NBR)");
			sb.append("        INNER JOIN DNVIEWRPT ON DN_DETAILS.DN_NBR = DNVIEWRPT.DN_NBR)");
			sb.append("        INNER JOIN GB_EDO ON DN_DETAILS.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR");
			sb.append(" WHERE (WEBDNUATEMP.TRANSREFNO = :dnNbr)");
			sb.append(" AND SST_BILL.PRINT_IND='WEB'");
			sb.append(" ORDER BY WEBDNUATEMP.TRANSREFNO");

			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			listDnDetails = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<GcOpsDnReport>(GcOpsDnReport.class));
		} catch (NullPointerException e) {
			log.error("Exception getDNPrintJasper :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getDNPrintJasper :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getDNPrintJasper DAO Result:" + listDnDetails.size());
		}
		return listDnDetails;
	}

	// START added by NS OCT 2022 to get truck records
	@Override
	public int truckerOut(String edo_asn_nbr, String truck_nbr) throws BusinessException {
		String sql = "";
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		int result = 0;
		try {
			log.info("truckerOut DAO START edo_asn_nbr:" + edo_asn_nbr + ",truck_nbr:" + truck_nbr);
			sb.append("select * from dn_details  where  dn_status = 'A' and truck_nbr = :truck_nbr ");
			sb.append("and edo_asn_nbr = :edo_asn_nbr and gate_out_dttm is null and crg_dest = 'O'");
			sql = sb.toString();

			paramMap.put("truck_nbr", truck_nbr);
			paramMap.put("edo_asn_nbr", edo_asn_nbr);

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				result = 1;
			}
		} catch (NullPointerException e) {
			log.error("Exception getDNPrintJasper :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getDNPrintJasper :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getDNPrintJasper DAO end");

		}
		return result;
	}
}
