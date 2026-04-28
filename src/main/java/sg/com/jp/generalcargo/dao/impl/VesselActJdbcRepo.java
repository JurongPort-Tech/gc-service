package sg.com.jp.generalcargo.dao.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import sg.com.jp.generalcargo.dao.LateArrivalRepo;
import sg.com.jp.generalcargo.dao.VesselActRepo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Email;
import sg.com.jp.generalcargo.domain.EmailValueObject;
import sg.com.jp.generalcargo.domain.GbArrivalWaiver;
import sg.com.jp.generalcargo.domain.IMessageValueObject;
import sg.com.jp.generalcargo.domain.OSDExemptionClauses;
import sg.com.jp.generalcargo.domain.OSDReviewObject;
import sg.com.jp.generalcargo.domain.Sms;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselActValueObject;

import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Repository("vesselActRepo")
public class VesselActJdbcRepo implements VesselActRepo {

	private static final Log log = LogFactory.getLog(VesselActJdbcRepo.class);

	@Autowired
	private LateArrivalRepo lateArrivalRepo;

	// ejb.sessionBeans.gbms.ops.vesselact -->VesselActEjb

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public String logStatusGlobal = "N";
	
	
	@Value("${review.osd.file.attachment.path}")
	String fileUploadDirectory;
	
	@Value("${jp.common.notificationProperties.emailEndpoint}")
	String commonServiceUrl;
	

	// ejb.sessionBeans.gbms.ops.vesselact -->VesselActEjb

	@Override
	public int checkImportCntr(String outVoyNbr) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int count = 0;

		String sql = "select count(*) as cnt from cntr where DISC_VV_CD  = :outVoyNbr and PURP_CD = 'IM' and TXN_STATUS <> 'D'";
		try {
			log.info("START: checkImportCntr  DAO  Start Obj " + " outVoyNbr:" + outVoyNbr);

			paramMap.put("outVoyNbr", outVoyNbr);

			log.info(" *** checkImportCntr SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				count = rs.getInt("cnt");
			}

			log.info("END: *** checkImportCntr Result *****" + count);
			return count;
		} catch (NullPointerException e) {
			log.error("Exception: checkImportCntr ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: checkImportCntr ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkImportCntr  DAO  END");
		}

	}

	@Override
	public List<VesselActValueObject> getVesselActShiftList(String strCustCode, String strvvcd)
			throws BusinessException {

		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet delayRs = null;

		List<VesselActValueObject> vesselactlist = new ArrayList<VesselActValueObject>();
		try {
			log.info("START: getVesselActShiftList  DAO  Start Obj " + " strCustCode:" + strCustCode + " strvvcd:"
					+ strvvcd);

			// START Code added by Tirumal to implement VesselActivity CR, dated 04-12-2007
			String delaySql = "select misc_type_cd, misc_type_nm from MISC_TYPE_CODE where cat_cd = 'WC_DELAY' and rec_status = 'A'  order by misc_type_nm";
			// END Code added by Tirumal to implement VesselActivity CR, dated 04-12-2007

			StringBuffer sb = new StringBuffer();

			/* Vessel productivity, santosh ,madhu, changed the query */
			sb.append("SELECT A.VSL_NM, A.VV_CD, A.IN_VOY_NBR,A.OUT_VOY_NBR,");
			sb.append(" to_char(B.ATB_DTTM, 'DD/MM/YYYY/HH24/MI'),");
			sb.append(" to_char(B.ATU_DTTM, 'DD/MM/YYYY/HH24/MI'),");
			sb.append(" to_char(B.GB_COD_DTTM, 'DD/MM/YYYY/HH24/MI'),");
			sb.append(" to_char(B.GB_COL_DTTM, 'DD/MM/YYYY/HH24/MI'),");
			sb.append(" to_char(B.GB_BCOD_DTTM, 'DD/MM/YYYY/HH24/MI'),");
			sb.append(" to_char(B.GB_BCOL_DTTM, 'DD/MM/YYYY/HH24/MI'),");
			sb.append(" to_char(B.GB_FIRST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'),");
			sb.append(" to_char(B.GB_LAST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'),");
			sb.append(" to_char(B.GB_FIRST_CARGO_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'),");
			sb.append(" A.GB_CLOSE_VSL_IND, A.VV_STATUS_IND, A.SCHEME,");
			sb.append(" B.SHIFT_IND, B.MVT_IND,");
			sb.append(" to_char(B.ETB_DTTM, 'DD-MM-YYYY HH24:MI'), ");
			sb.append(" to_char(B.ETU_DTTM, 'DD-MM-YYYY HH24:MI'),");
			sb.append(" to_char(SYSDATE, 'DD-MM-YYYY HH24:MI'),a.vsl_under_tow_ind,  ");
			// Added by Tirumal, dated 04-12-2007);
			sb.append(" B.GANG_NBR, B.HATCH_NBR,B.DELAY_RSN_CD, B.REMARKS, B.TOT_GEN_CARGO_ACT, ");
			sb.append(
					" A.GB_ARRIVAL_WAIVER_IND AS GB_ARRIVAL_WAIVER_IND, A.GB_ARRIVAL_WAIVER_AMOUNT AS GB_ARRIVAL_WAIVER_AMOUNT,");
			sb.append(" to_char(B.COD_DTTM, 'DD/MM/YYYY/HH24/MI') COD_DTTM, ");
			sb.append(" to_char(B.COL_DTTM, 'DD/MM/YYYY/HH24/MI') COL_DTTM, ");
			sb.append(" A.TERMINAL, A.COMBI_GC_OPS_IND");
			sb.append(" FROM VESSEL_CALL A, BERTHING B,VESSEL C ");
			sb.append(" WHERE A.VV_CD=B.VV_CD AND");
			sb.append(" A.VV_STATUS_IND != 'CX' AND");
			// +" C.VSL_TYPE_CD != 'SC' AND" - updated by swarna on 16-08);
			sb.append(" A.VSL_NM= C.VSL_NM AND ");
			sb.append(" A.CREATE_CUST_CD IS NOT NULL");
			sb.append(" AND A.VV_CD= :strvvcd ORDER BY B.SHIFT_IND ");
			sql = sb.toString();

			paramMap.put("strvvcd", strvvcd);

			log.info(" *** getVesselActShiftList SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			// START Code added by Tirumal to implement VesselActivity CR, dated 04-12-2007

			log.info(" *** getVesselActShiftList SQL *****" + delaySql + " paramMap " + paramMap.toString());
			delayRs = namedParameterJdbcTemplate.queryForRowSet(delaySql, paramMap);

			List<String> delayOptionList = new ArrayList<String>();

			while (delayRs.next()) {
				String delayOptionString = delayRs.getString(1) + "-" + delayRs.getString(2);
				delayOptionList.add(delayOptionString);
			}
			// END Code added by Tirumal to implement VesselActivity CR, dated 04-12-2007

			while (rs.next()) {
				VesselActValueObject vesselActValueObject = new VesselActValueObject();
				String vslnm = CommonUtility.deNull(rs.getString(1));
				String vvcd = CommonUtility.deNull(rs.getString(2));
				String invoynbr = CommonUtility.deNull(rs.getString(3));
				String outvoynbr = CommonUtility.deNull(rs.getString(4));
				String atbdttm = CommonUtility.deNull(rs.getString(5));
				String atudttm = CommonUtility.deNull(rs.getString(6));
				String coddttm = CommonUtility.deNull(rs.getString(7));
				String coldttm = CommonUtility.deNull(rs.getString(8));
				String bcoddttm = CommonUtility.deNull(rs.getString(9));
				String bcoldttm = CommonUtility.deNull(rs.getString(10));
				String firstactdttm = CommonUtility.deNull(rs.getString(11));
				String lastactdttm = CommonUtility.deNull(rs.getString(12));
				/* Start fix Added by santosh */
				String firstcargoactdttm = CommonUtility.deNull(rs.getString(13));
				String gbclosevslind = CommonUtility.deNull(rs.getString(14));
				// modifed after may 02
				String vvstatusind = CommonUtility.deNull(rs.getString(15));
				String scheme = CommonUtility.deNull(rs.getString(16));

				String shiftind = CommonUtility.deNull(rs.getString(17));
				String mvtind = CommonUtility.deNull(rs.getString(18));
				String etbdttm = CommonUtility.deNull(rs.getString(19));
				String etudttm = CommonUtility.deNull(rs.getString(20));
				String strsysdate = CommonUtility.deNull(rs.getString(21));
				String vesslUnderTowedInd = CommonUtility.deNull(rs.getString(22));
				/* End fix Santosh */

				// START Code added by Tirumal for VesselActivity CR, dated 04-12-2007
				String noOfGangs = CommonUtility.deNull(rs.getString(23));
				String noOfHatches = CommonUtility.deNull(rs.getString(24));
				String reasonForDelay = CommonUtility.deNull(rs.getString(25));
				String remarks = CommonUtility.deNull(rs.getString(26));
				// END Code added by Tirumal for VesselActivity CR, dated 04-12-2007
				// START Code added by Madhu for VesselProductivty Billing CR, dated 25-09-2008
				String totGenCargoActivity = CommonUtility.deNull(rs.getString(27));
				// END Code added by Madhu for VesselProductivty Billing CR, dated 25-09-2008

				// 20190328 koktsing, Combi Enhancement
				// determine GB/CT last activity date based on later date among COD_DTTM,
				// COL_DTTM, GB_LAST_ACT_DTTM
				String cntrcoddttm = CommonUtility.deNull(rs.getString("COD_DTTM"));
				String cntrcoldttm = CommonUtility.deNull(rs.getString("COL_DTTM"));
				String terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				String combiGcOpsInd = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));

				String gbArrivalWaiverInd = CommonUtility.deNull(rs.getString("GB_ARRIVAL_WAIVER_IND"));
				double gbArrivalWaiverAmount = rs.getDouble("GB_ARRIVAL_WAIVER_AMOUNT");


				vesselActValueObject.setVarNbr(vvcd);
				vesselActValueObject.setVslNm(vslnm);
				vesselActValueObject.setInVoyNbr(invoynbr);
				vesselActValueObject.setOutVoyNbr(outvoynbr);
				vesselActValueObject.setAtbDttm(atbdttm);
				vesselActValueObject.setAtuDttm(atudttm);
				vesselActValueObject.setCodDttm(coddttm);
				vesselActValueObject.setColDttm(coldttm);
				vesselActValueObject.setBcodDttm(bcoddttm);
				vesselActValueObject.setBcolDttm(bcoldttm);
				vesselActValueObject.setFirstActDttm(firstactdttm);
				vesselActValueObject.setLastActDttm(lastactdttm);
				/* Start fix Santosh */
				vesselActValueObject.setFirstCargoActDttm(firstcargoactdttm);
				/* End fix Santosh */
				vesselActValueObject.setGbCloseVslInd(gbclosevslind);
				vesselActValueObject.setVvStatusInd(vvstatusind);
				vesselActValueObject.setScheme(scheme);
				// modifed after may 02
				vesselActValueObject.setShiftInd(shiftind);
				vesselActValueObject.setMvtInd(mvtind);
				vesselActValueObject.setEtbDttm(etbdttm);
				vesselActValueObject.setEtuDttm(etudttm);
				vesselActValueObject.setTodayDttm(strsysdate);
				vesselActValueObject.setLineTowedVessel(vesslUnderTowedInd);

				// START Code added by Tirumal for VesselActivity CR, dated 04-12-2007
				vesselActValueObject.setNoOfGangsSupplied(noOfGangs);
				vesselActValueObject.setNoOfWorkableHatches(noOfHatches);
				vesselActValueObject.setReasonForDelay(reasonForDelay);
				vesselActValueObject.setRemarks(remarks);
				vesselActValueObject.setTempList(delayOptionList);
				// END Code added by Tirumal for VesselActivity CR, dated 04-12-2007
				// START Code added by Madhu for VesselProductivty Billing CR, dated 25-09-2008
				vesselActValueObject.setTotGenCargoActivity(totGenCargoActivity);

				vesselActValueObject.setGbArrivalWaiverInd(gbArrivalWaiverInd);
				vesselActValueObject.setGbArrivalWaiverAmount(new BigDecimal(gbArrivalWaiverAmount));

				// 20190328 Combi Enhancement: koktsing
				// determine GB/CT last activity date based on later date among COD_DTTM,
				// COL_DTTM, GB_LAST_ACT_DTTM
				vesselActValueObject.setCntrCodDttm(cntrcoddttm);
				vesselActValueObject.setCntrColDttm(cntrcoldttm);
				vesselActValueObject.setTerminal(terminal);
				vesselActValueObject.setCombiGcOpsInd(combiGcOpsInd);

				// END Code added by Madhu for VesselProductivty Billing CR, dated 25-09-2008
				vesselactlist.add(vesselActValueObject);
			}

			log.info("END: *** getVesselActShiftList Result *****" + vesselactlist.toString());
			return vesselactlist;
		} catch (NullPointerException e) {
			log.error("Exception: getVesselActShiftList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getVesselActShiftList ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getVesselActShiftList  DAO  END");
		}
	}

	@Override
	public List<String> getWaiverBillingList(String strvvcd) throws BusinessException {

		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3 = null;
		String sql1 = "";
		String sql2 = "";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<String> waiverbill = new ArrayList<String>();
		String strwaivercd = "--";
		String strwaivernm = "--";
		String strbillind = "--";
		try {
			log.info("START: getWaiverBillingList  DAO  Start Obj " + " strvvcd:" + strvvcd);

			sql1 = "SELECT W.WAIVER_CD, W.WAIVER_NM FROM VESSEL_CALL V, WAIVER_CODE W WHERE V.GB_ARRIVAL_WAIVER_CD=W.WAIVER_CD AND V.VV_CD = :strvvcd ";

			sql2 = "SELECT W.WAIVER_CD, W.WAIVER_NM FROM VESSEL_CALL V, WAIVER_CODE W WHERE V.GB_DEPARTURE_WAIVER_CD=W.WAIVER_CD AND V.VV_CD = :strvvcd ";

			String sql3 = "SELECT V.GB_BERT_BILL_IND FROM VESSEL_CALL V WHERE V.VV_CD = :strvvcd ";
			try {

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** getWaiverBillingList SQL *****" + sql1 + " paramMap " + paramMap.toString());

				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
				while (rs1.next()) {
					strwaivercd = CommonUtility.deNull(rs1.getString(1));
					strwaivernm = CommonUtility.deNull(rs1.getString(2));
				}

				if (!(strwaivercd.equalsIgnoreCase("--") || strwaivernm.equalsIgnoreCase("--"))) {
					strwaivercd = strwaivercd.concat("--");
					strwaivercd = strwaivercd.concat(strwaivernm);
				}
				waiverbill.add(strwaivercd);

			} catch (Exception e) {
				log.error("Exception: getWaiverBillingList ", e);
				throw new BusinessException("M1004");
			}
			strwaivercd = "--";
			strwaivernm = "--";
			try {
				paramMap.put("strvvcd", strvvcd);

				log.info(" *** getWaiverBillingList SQL *****" + sql2 + " paramMap " + paramMap.toString());

				rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
				while (rs2.next()) {
					strwaivercd = CommonUtility.deNull(rs2.getString(1));
					strwaivernm = CommonUtility.deNull(rs2.getString(2));
				}
				if (!(strwaivercd.equalsIgnoreCase("--") || strwaivernm.equalsIgnoreCase("--"))) {
					strwaivercd = strwaivercd.concat("--");
					strwaivercd = strwaivercd.concat(strwaivernm);
				}
				waiverbill.add(strwaivercd);

			} catch (Exception e) {
				log.error("Exception: getWaiverBillingList ", e);
				throw new BusinessException("M1004");
			}
			try {

				paramMap.put("strvvcd", strvvcd);
				log.info(" *** getWaiverBillingList SQL *****" + sql3 + " paramMap " + paramMap.toString());
				rs3 = namedParameterJdbcTemplate.queryForRowSet(sql3, paramMap);
				while (rs3.next()) {
					strbillind = CommonUtility.deNull(rs3.getString(1));
				}
				if (strbillind.equalsIgnoreCase("")) {
					strbillind = "--";
				}
				waiverbill.add(strbillind);

			} catch (Exception e) {
				log.error("Exception: getWaiverBillingList ", e);
				throw new BusinessException("M1004");
			}

			log.info("END: *** getWaiverBillingList Result *****" + waiverbill.toString());
			return waiverbill;
		} catch (BusinessException e) {
			log.error("Exception: getWaiverBillingList ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: getWaiverBillingList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getWaiverBillingList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWaiverBillingList  DAO  END");
		}
	}

	@Override
	public int checkExportCntr(String outVoyNbr) throws BusinessException {

		SqlRowSet rs = null;
		int count = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "select count(*) as cnt from cntr where LOAD_VV_CD  = :outVoyNbr and PURP_CD = 'EX' and TXN_STATUS <> 'D'";
		// log.info("view sql"+sql);
		try {
			log.info("START: checkExportCntr  DAO  Start Obj " + " outVoyNbr:" + outVoyNbr);

			paramMap.put("outVoyNbr", outVoyNbr);
			log.info(" *** checkExportCntr SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				count = rs.getInt("cnt");
			}

			log.info("END: *** checkExportCntr Result *****" + count);
			return count;
		} catch (NullPointerException e) {
			log.error("Exception: checkExportCntr ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: checkExportCntr ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkExportCntr  DAO  END");
		}

	}

	@Override
	public String checkShipStore(String outVoyNbr) throws BusinessException {

		int count = 0;
		String isShipStore = "N";
		String transType = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START: checkShipStore  DAO  Start Obj " + " outVoyNbr:" + outVoyNbr);

			String sql = "select distinct(trans_type) trans_type from esn  where OUT_VOY_VAR_NBR = :outVoyNbr and esn_status = 'A'  order by trans_type";

			paramMap.put("outVoyNbr", outVoyNbr);

			log.info(" *** checkShipStore SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				transType = rs.getString("trans_type");
				count = count + 1;
			}
			if (count == 1 && transType.equalsIgnoreCase("S")) {
				isShipStore = "Y";
			}

			log.info("END: *** checkShipStore Result *****" + isShipStore.toString());
			return isShipStore;
		} catch (NullPointerException e) {
			log.error("Exception: checkShipStore ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: checkShipStore ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkShipStore  DAO  END");
		}

	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateVesselActStatus(String strvvstatus, String strvvcd, String struserid) throws BusinessException {
		String gbclosevslind = "N";
		String gbclosebjind = "Y";
		String gbcloseshpind = "Y";
		String strnewvvstatusind = "";
		String strtransaction = "O";
		String strTerminal = "";
		boolean chkSemiConVsl = false;
		String sql2 = "";
		String sql3 = "";
		if (strvvstatus.equalsIgnoreCase("CL")) {
			gbclosevslind = "Y";
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();

		String strtransnbr = "0";
		String strUpdatetrans = "";

		SqlRowSet rs2 = null;

		SqlRowSet rscnt = null;

		SqlRowSet rscntr = null;

		SqlRowSet CntrCntRs = null;

		SqlRowSet rslog = null;

		try {
			log.info("START: updateVesselActStatus  DAO  Start Obj " + " strvvstatus:" + strvvstatus + " strvvcd:"
					+ strvvcd + " struserid:" + struserid);

			sql2 = "SELECT GB_CLOSE_BJ_IND, GB_CLOSE_SHP_IND, VV_STATUS_IND, TERMINAL, CNTR_VSL_IND FROM VESSEL_CALL WHERE VV_CD= :strvvcd";

			String sqllog = "SELECT MAX(TRANS_NBR) FROM OPS_CLOSE_TRANS WHERE VV_CD= :strvvcd ";

			paramMap = new HashMap<String, Object>();

			paramMap.put("strvvcd", strvvcd);

			log.info(" *** getBundleList SQL *****" + sql2 + " paramMap " + paramMap.toString());
			rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);

			while (rs2.next()) {
				gbclosebjind = CommonUtility.deNull(rs2.getString(1));
				gbcloseshpind = CommonUtility.deNull(rs2.getString(2));
				strnewvvstatusind = CommonUtility.deNull(rs2.getString(3));
				strTerminal = CommonUtility.deNull(rs2.getString(4));
			}

			if (strTerminal.equalsIgnoreCase("CT"))
			// in future to check
			// if ((strTerminal.equalsIgnoreCase("CT")) &&
			// (strVslCntrInd.equalsIgnoreCase("B")
			{
				chkSemiConVsl = true;
			}
			if (strnewvvstatusind.equalsIgnoreCase("CL")) {
				throw new BusinessException("M20850");
			}
			/*
			 * if (gbclosevslind.equalsIgnoreCase("Y") && gbclosebjind.equalsIgnoreCase("Y")
			 * && gbcloseshpind.equalsIgnoreCase("Y") && (!chkSemiConVsl)) { sql3=
			 * "UPDATE VESSEL_CALL SET VV_STATUS_IND='"+strvvstatus
			 * +"' , GB_CLOSE_VSL_IND ='"+gbclosevslind
			 * +"', LAST_MODIFY_DTTM = sysdate,VV_CLOSE_DTTM=sysdate,"
			 * +" LAST_MODIFY_USER_ID = '"+struserid +"' WHERE VV_CD='"+strvvcd+"'"; }else{
			 * sql3= "UPDATE VESSEL_CALL SET GB_CLOSE_VSL_IND ='"+gbclosevslind
			 * +"', LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = '"+struserid
			 * +"' WHERE VV_CD='"+strvvcd+"'"; }
			 */
			/* update by swarna 16-08 */
			if (gbclosevslind.equalsIgnoreCase("Y")) {

				String mftcnt = "N", bkcnt = "N";
				String sqlcnt = "SELECT COUNT(*) FROM MANIFEST_DETAILS WHERE VAR_NBR = :strvvcd AND BL_STATUS = 'A'";
				String sqlcnt1 = "SELECT COUNT(*) FROM ESN WHERE OUT_VOY_VAR_NBR = :strvvcd AND ESN_STATUS = 'A' ";

				paramMap = new HashMap<String, Object>();

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** updateVesselActStatus SQL *****" + sqlcnt + " paramMap " + paramMap.toString());
				rscnt = namedParameterJdbcTemplate.queryForRowSet(sqlcnt, paramMap);

				if (rscnt.next()) {
					mftcnt = rscnt.getString(1);
				}
				if (!(mftcnt.equalsIgnoreCase("0"))) {
					mftcnt = "N";
				}
				paramMap = new HashMap<String, Object>();

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** updateVesselActStatus SQL *****" + sqlcnt1 + " paramMap " + paramMap.toString());
				rscnt = namedParameterJdbcTemplate.queryForRowSet(sqlcnt1, paramMap);

				if (rscnt.next()) {
					bkcnt = rscnt.getString(1);
				}
				if (!(bkcnt.equalsIgnoreCase("0"))) {
					bkcnt = "N";
				}

				// Bulk Cargo Segment - added on 24/07/2003 - Muthu
				String bulk_mftcnt = "";
				String bulk_esncnt = "";

				String sqlcntB = "Select count(*) from bulk_manifest_details where Var_nbr = :strvvcd and BL_status ='A'";
				String sqlcntBn = "SELECT COUNT(*) FROM Bulk_ESN WHERE OUT_VOY_VAR_NBR = :strvvcd And ESN_STATUS = 'A'";

				paramMap = new HashMap<String, Object>();

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** updateVesselActStatus SQL *****" + sqlcntB + " paramMap " + paramMap.toString());
				rscntr = namedParameterJdbcTemplate.queryForRowSet(sqlcntB, paramMap);

				if (rscntr.next()) {
					bulk_mftcnt = rscntr.getString(1);
				} else {
					bulk_mftcnt = "0";
				}

				paramMap = new HashMap<String, Object>();
				paramMap.put("strvvcd", strvvcd);

				log.info(" *** updateVesselActStatus SQL *****" + sqlcntBn + " paramMap " + paramMap.toString());
				rscntr = namedParameterJdbcTemplate.queryForRowSet(sqlcntBn, paramMap);
				if (rscntr.next()) {
					bulk_esncnt = rscntr.getString(1);
				} else {
					bulk_esncnt = "0";
				}

				// End Bulk Cargo Segment
				// following changes made by karthi on 15/04/04
				String discCntrCnt = "";
				String loadCntrCnt = "";

				String discCntrCntSql = "";
				String loadCntrCntSql = "";

				discCntrCntSql = "select count(*) from cntr where disc_vv_cd= :strvvcd and txn_status<>'D' and shipment_status not in ('SH') and ct_planned_disc is not null";
				loadCntrCntSql = "select count(*) from cntr where load_vv_cd= :strvvcd and txn_status<>'D' and shipment_status not in ('SO') and ct_planned_load is not null";

				paramMap = new HashMap<String, Object>();

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** updateVesselActStatus SQL *****" + discCntrCntSql + " paramMap " + paramMap.toString());
				CntrCntRs = namedParameterJdbcTemplate.queryForRowSet(discCntrCntSql, paramMap);

				if (CntrCntRs.next()) {
					discCntrCnt = CntrCntRs.getString(1);
				} else {
					discCntrCnt = "0";
				}
				paramMap = new HashMap<String, Object>();

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** updateVesselActStatus SQL *****" + loadCntrCntSql + " paramMap " + paramMap.toString());
				CntrCntRs = namedParameterJdbcTemplate.queryForRowSet(loadCntrCntSql, paramMap);
				if (CntrCntRs.next()) {
					loadCntrCnt = CntrCntRs.getString(1);
				} else {
					loadCntrCnt = "0";
				}

				// upto this karthi
				log.info("#########Testing karthi from VesselActivityEJB################");
				log.info("mftcnt-- is-->" + mftcnt);
				log.info("mftcnt-- is-->" + mftcnt);

				// New If Clause added on 24/07/2003 - Muthu
				if (((gbclosevslind.equalsIgnoreCase("Y")) && (gbclosebjind.equalsIgnoreCase("Y"))
						&& (gbcloseshpind.equalsIgnoreCase("Y")) && (bulk_mftcnt.equalsIgnoreCase("0"))
						&& (bulk_esncnt.equalsIgnoreCase("0"))) ||

						((mftcnt.equalsIgnoreCase("0") && bkcnt.equalsIgnoreCase("0")
								&& discCntrCnt.equalsIgnoreCase("0")// added by karthi on 15/04/04
								&& loadCntrCnt.equalsIgnoreCase("0")// added by karthi on 15/04/04
								&& bulk_mftcnt.equalsIgnoreCase("0") && bulk_esncnt.equalsIgnoreCase("0")) ||

								((mftcnt.equalsIgnoreCase("N") || !discCntrCnt.equalsIgnoreCase("0"))// added by karthi
																										// on 15/04/04
										&& bkcnt.equalsIgnoreCase("0") && gbclosebjind.equalsIgnoreCase("Y")
										&& loadCntrCnt.equalsIgnoreCase("0")// added by karthi on 15/04/04
										&& bulk_mftcnt.equalsIgnoreCase("0") && bulk_esncnt.equalsIgnoreCase("0"))
								||

								(mftcnt.equalsIgnoreCase("0") && discCntrCnt.equalsIgnoreCase("0")// added by karthi on
																									// 15/04/04
										&& (bkcnt.equalsIgnoreCase("N") || !loadCntrCnt.equalsIgnoreCase("0"))// added
																												// by
																												// karthi
																												// on
																												// 15/04/04
										&& gbcloseshpind.equalsIgnoreCase("Y") && bulk_mftcnt.equalsIgnoreCase("0")
										&& bulk_esncnt.equalsIgnoreCase("0"))))

				/*
				 * if (((gbclosevslind.equalsIgnoreCase("Y")) &&
				 * (gbclosebjind.equalsIgnoreCase("Y")) &&
				 * (gbcloseshpind.equalsIgnoreCase("Y"))) || ((mftcnt.equalsIgnoreCase("0") &&
				 * bkcnt.equalsIgnoreCase("0")) || (mftcnt.equalsIgnoreCase("N") &&
				 * bkcnt.equalsIgnoreCase("0") && gbclosebjind.equalsIgnoreCase("Y")) ||
				 * (mftcnt.equalsIgnoreCase("0") && bkcnt.equalsIgnoreCase("N") &&
				 * gbcloseshpind.equalsIgnoreCase("Y"))))
				 */
				{
					paramMap = new HashMap<String, Object>();
					if (!chkSemiConVsl) {

						sql3 = "UPDATE VESSEL_CALL SET VV_STATUS_IND= :strvvstatus , GB_CLOSE_VSL_IND = :gbclosevslind, LAST_MODIFY_DTTM = sysdate,VV_CLOSE_DTTM=sysdate, LAST_MODIFY_USER_ID = :struserid WHERE VV_CD= :strvvcd ";

						paramMap.put("gbclosevslind", gbclosevslind);
						paramMap.put("strvvstatus", strvvstatus);
						paramMap.put("struserid", struserid);
						paramMap.put("strvvcd", strvvcd);

					} else {

						sql3 = "UPDATE VESSEL_CALL SET GB_CLOSE_VSL_IND = :gbclosevslind, LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid WHERE VV_CD= :strvvcd ";

						paramMap.put("gbclosevslind", gbclosevslind);
						paramMap.put("strvvstatus", strvvstatus);
						paramMap.put("struserid", struserid);
						paramMap.put("strvvcd", strvvcd);
					}
				} else {
					paramMap = new HashMap<String, Object>();

					sql3 = "UPDATE VESSEL_CALL SET GB_CLOSE_VSL_IND =:gbclosevslind, LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid WHERE VV_CD= :strvvcd ";


					paramMap.put("gbclosevslind", gbclosevslind);
					paramMap.put("strvvstatus", strvvstatus);
					paramMap.put("struserid", struserid);
					paramMap.put("strvvcd", strvvcd);
				}
			}
			/* end */

			if (gbclosevslind.equalsIgnoreCase("N")) {
				paramMap = new HashMap<String, Object>();
				if (!chkSemiConVsl) {
					sql3 = "UPDATE VESSEL_CALL SET VV_STATUS_IND= :strvvstatus , GB_CLOSE_VSL_IND = :gbclosevslind, LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid WHERE VV_CD= :strvvcd";

					paramMap.put("gbclosevslind", gbclosevslind);
					paramMap.put("strvvstatus", strvvstatus);
					paramMap.put("struserid", struserid);
					paramMap.put("strvvcd", strvvcd);

				} else {
					sql3 = "UPDATE VESSEL_CALL SET GB_CLOSE_VSL_IND = :gbclosevslind, LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid WHERE VV_CD= :strvvcd ";

					paramMap.put("gbclosevslind", gbclosevslind);
					paramMap.put("strvvstatus", strvvstatus);
					paramMap.put("struserid", struserid);
					paramMap.put("strvvcd", strvvcd);

				}
			}

			log.info(" *** updateVesselActStatus SQL *****" + sql3 + " paramMap " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sql3, paramMap);

			// 04/08/2011 PCYAP To implement late arrival waiver approving workflow
			if (gbclosevslind != null && gbclosevslind.equalsIgnoreCase("Y")) {

				BigDecimal gbArrivalWaiverAmount = lateArrivalRepo.calculateGbArrivalWaiverAmount(strvvcd);

				lateArrivalRepo.updateGbArrivalWaiver(strvvcd, null, gbArrivalWaiverAmount, struserid);
			}

			if (count == 0) {
				throw new BusinessException("M1007");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				try {

					paramMap.put("strvvcd", strvvcd);
					log.info(" *** updateVesselActStatus SQL *****" + sqllog + " paramMap " + paramMap.toString());
					rslog = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
					while (rslog.next()) {
						strtransnbr = CommonUtility.deNull(rslog.getString(1));
					}

					if (strtransnbr.equalsIgnoreCase("")) {
						strtransnbr = "0";
					} else {
						strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
					}
				} catch (Exception e) {
					log.error("Exception: updateVesselActStatus ", e);
					throw new BusinessException("M4201");
				}
				if (gbclosevslind.equalsIgnoreCase("Y")) {
					strtransaction = "C";
				}
				if (gbclosevslind.equalsIgnoreCase("N")) {
					strtransaction = "O";
				}
				strUpdatetrans = "INSERT INTO OPS_CLOSE_TRANS (TRANS_NBR,VV_CD, TRANS_TYPE,TRANS_ACTION, LAST_MODIFY_DTTM,LAST_MODIFY_USER_ID) VALUES (:strtransnbr,:strvvcd,'V',:strtransaction,sysdate,:struserid) ";

				paramMap.put("strtransnbr", strtransnbr);
				paramMap.put("strvvcd", strvvcd);
				paramMap.put("strtransaction", strtransaction);
				paramMap.put("struserid", struserid);

				log.info(" *** updateVesselActStatus SQL *****" + strUpdatetrans + " paramMap " + paramMap.toString());

				int count1 = namedParameterJdbcTemplate.update(strUpdatetrans, paramMap);

				if (count1 == 0) {
					throw new BusinessException("M1007");
				}

			}
			log.info("END: *** updateVesselActStatus Result *****");
		} catch (BusinessException e) {
			log.error("Exception: updateVesselActStatus", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: updateVesselActStatus", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception: updateVesselActStatus", e);
			throw new BusinessException(e.getMessage());
		} finally {

			log.info("END: updateVesselActStatus  DAO  END");
		}

	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateVesselActivity(String strvvstatus, String stratbdttm, String stratudttm, String strcoddttm,
			String strcoldttm, String strbcoddttm, String strbcoldttm, String strdiscdttm, String strloaddttm,
			String struserid, String strvvcd, String strfgcdttm, String strtotgencargoactivity)
			throws BusinessException {
		String sql1 = "";

		String strcntrcount = "0";
		String strtransnbr = "0";
		String strUpdatetrans = "";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs0 = null;

		SqlRowSet rslog = null;

		try {
			log.info("START: updateVesselActivity  DAO  Start Obj " + " strvvstatus:" + strvvstatus + " stratbdttm:"
					+ stratbdttm + " stratudttm:" + stratudttm + " strcoddttm:" + strcoddttm + " strcoldttm:"
					+ strcoldttm + " strbcoddttm:" + strbcoddttm + " strbcoldttm:" + strbcoldttm + " strdiscdttm:"
					+ strdiscdttm + " strloaddttm:" + strloaddttm + " struserid:" + struserid + " strvvcd:" + strvvcd
					+ " strfgcdttm:" + strfgcdttm + " strtotgencargoactivity:" + strtotgencargoactivity);

			String sql0 = "";
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	SUM(CNT) AS count ");
			sb.append("FROM ");
			sb.append("	( ");
			sb.append("	SELECT ");
			sb.append("		COUNT(VV_CD) AS cnt ");
			sb.append("	FROM ");
			sb.append("		VV_CNTR ");
			sb.append("	WHERE ");
			sb.append("		VV_CD = :strvvcd ");
			sb.append("UNION ");
			sb.append("	SELECT ");
			sb.append("		count(disc_vv_cd) AS cnt ");
			sb.append("	FROM ");
			sb.append("		cntr ");
			sb.append("	WHERE ");
			sb.append("		disc_vv_cd = :strvvcd ");
			sb.append("		AND purp_cd = 'TS' ");
			sb.append("		AND disc_gateway = 'J' ");
			sb.append("		AND load_gateway = 'P' ");
			sb.append("		AND txn_status <> 'D' )");

			sql0 = sb.toString();

			String sqllog = "SELECT MAX(TRANS_NBR) FROM VESSEL_ACT_DTTM_TRANS WHERE VV_CD= :strvvcd AND SHIFT_IND='1'";

			try {

				paramMap.put("strvvcd", strvvcd);
				log.info(" *** updateVesselActivity SQL *****" + sql0 + " paramMap " + paramMap.toString());
				rs0 = namedParameterJdbcTemplate.queryForRowSet(sql0, paramMap);
				while (rs0.next()) {
					strcntrcount = CommonUtility.deNull(rs0.getString(1));
				}

			} catch (Exception e) {
				log.error("Exception: updateVesselActivity ", e);
				throw new BusinessException("M1007");
			}
			log.info("Before updateVesselActivity 5555 #####################" + strcntrcount);
			if (strcntrcount.equalsIgnoreCase("0")) {
				StringBuffer sb1 = new StringBuffer();

				sb1.append("UPDATE BERTHING SET ");
				sb1.append("ATB_DTTM = to_date(:stratbdttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("ATU_DTTM = to_date(:stratudttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_COD_DTTM = to_date(:strcoddttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_COL_DTTM = to_date(:strcoldttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_BCOD_DTTM = to_date(:strbcoddttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_BCOL_DTTM = to_date(:strbcoldttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_FIRST_ACT_DTTM = to_date(:strdiscdttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_LAST_ACT_DTTM = to_date(:strloaddttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid,");
				sb1.append("GB_FIRST_CARGO_ACT_DTTM = to_date(:strfgcdttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("TOT_GEN_CARGO_ACT = :strtotgencargoactivity ");

				sb1.append(" WHERE VV_CD= :strvvcd AND SHIFT_IND = '1' ");
				sql1 = sb1.toString();
				paramMap = new HashMap<String, Object>();
				paramMap.put("stratbdttm", stratbdttm);
				paramMap.put("stratudttm", stratudttm);
				paramMap.put("strcoddttm", strcoddttm);
				paramMap.put("strcoldttm", strcoldttm);
				paramMap.put("strbcoddttm", strbcoddttm);
				paramMap.put("strbcoldttm", strbcoldttm);
				paramMap.put("strdiscdttm", strdiscdttm);
				paramMap.put("strloaddttm", strloaddttm);
				paramMap.put("struserid", struserid);
				paramMap.put("strfgcdttm", strfgcdttm);
				paramMap.put("strvvcd", strvvcd);
				paramMap.put("strtotgencargoactivity", strtotgencargoactivity);

			} else {

				StringBuffer sb1 = new StringBuffer();

				sb1.append("UPDATE BERTHING SET ");
				sb1.append("ATB_DTTM = to_date(:stratbdttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("ATU_DTTM = to_date(:stratudttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_COD_DTTM = to_date(:strcoddttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_COL_DTTM = to_date(:strcoldttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("COD_DTTM = to_date(:strcoddttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("COL_DTTM = to_date(:strcoldttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_BCOD_DTTM = to_date(:strbcoddttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_BCOL_DTTM = to_date(:strbcoldttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_FIRST_ACT_DTTM = to_date(:strdiscdttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("GB_LAST_ACT_DTTM = to_date(:strloaddttm,'dd-mm-yyyy hh24:mi'), ");
				sb1.append("LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid,");
				sb1.append("GB_FIRST_CARGO_ACT_DTTM = to_date(:strfgcdttm,'dd-mm-yyyy hh24:mi'),");
				sb1.append("TOT_GEN_CARGO_ACT = :strtotgencargoactivity ");
				sb1.append(" WHERE VV_CD= :strvvcd AND SHIFT_IND = '1' ");

				sql1 = sb1.toString();
				paramMap = new HashMap<String, Object>();

				paramMap.put("stratbdttm", stratbdttm);
				paramMap.put("stratudttm", stratudttm);
				paramMap.put("strcoddttm", strcoddttm);
				paramMap.put("strcoldttm", strcoldttm);
				paramMap.put("strbcoddttm", strbcoddttm);
				paramMap.put("strbcoldttm", strbcoldttm);
				paramMap.put("strdiscdttm", strdiscdttm);
				paramMap.put("strloaddttm", strloaddttm);
				paramMap.put("struserid", struserid);
				paramMap.put("strfgcdttm", strfgcdttm);
				paramMap.put("strvvcd", strvvcd);
				paramMap.put("strtotgencargoactivity", strtotgencargoactivity);

			}

			log.info(" *** updateVesselActivity SQL *****" + sql1 + " paramMap " + paramMap.toString());

			int count = namedParameterJdbcTemplate.update(sql1, paramMap);
			log.info("AFTER updateVesselActivity Query #####################");
			if (count == 0) {
				throw new BusinessException("M1007");
			}

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				try {
					paramMap = new HashMap<String, Object>();

					paramMap.put("strvvcd", strvvcd);
					log.info(" *** updateVesselActivity SQL *****" + sqllog + " paramMap " + paramMap.toString());
					rslog = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
					while (rslog.next()) {
						strtransnbr = CommonUtility.deNull(rslog.getString(1));
					}

					if (strtransnbr.equalsIgnoreCase("")) {
						strtransnbr = "0";
					} else {
						strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
					}
				} catch (Exception e) {
					log.error("Exception: updateVesselActivity ", e);
					throw new BusinessException("M1007");
				}
				if (strcntrcount.equalsIgnoreCase("0")) {

					StringBuffer sb1 = new StringBuffer();

					sb1.append("INSERT INTO VESSEL_ACT_DTTM_TRANS  ");
					sb1.append("(TRANS_NBR , VV_CD, ATB_DTTM, ATU_DTTM, GB_COD_DTTM,");
					sb1.append("GB_COL_DTTM, GB_BCOD_DTTM, GB_BCOL_DTTM,");
					sb1.append("GB_FIRST_ACT_DTTM, GB_LAST_ACT_DTTM,");
					sb1.append("LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID,SHIFT_IND) VALUES ");
					sb1.append("(:strtransnbr, :strvvcd, to_date(:stratbdttm,'dd-mm-yyyy hh24:mi'), ");
					sb1.append(
							"to_date(:stratudttm,'dd-mm-yyyy hh24:mi'), to_date(:strcoddttm,'dd-mm-yyyy hh24:mi'), to_date(:strcoldttm,'dd-mm-yyyy hh24:mi'),");
					sb1.append("  to_date(:strbcoddttm,'dd-mm-yyyy hh24:mi'), ");
					sb1.append(" to_date(:strbcoldttm,'dd-mm-yyyy hh24:mi'), ");
					sb1.append(
							" to_date(strdiscdttm,'dd-mm-yyyy hh24:mi') ,to_date(:strloaddttm,'dd-mm-yyyy hh24:mi'), ");
					sb1.append(" sysdate, :struserid, '1')");
					strUpdatetrans = sb1.toString();
					paramMap = new HashMap<String, Object>();
					paramMap.put("strtransnbr", strtransnbr);
					paramMap.put("strvvcd", strvvcd);
					paramMap.put("stratbdttm", stratbdttm);
					paramMap.put("stratudttm", stratudttm);
					paramMap.put("strcoddttm", strcoddttm);
					paramMap.put("strcoldttm", strcoldttm);
					paramMap.put("strbcoddttm", strbcoddttm);
					paramMap.put("strbcoldttm", strbcoldttm);
					paramMap.put("strdiscdttm", strdiscdttm);
					paramMap.put("strloaddttm", strloaddttm);
					paramMap.put("struserid", struserid);

				} else {
					StringBuffer sb1 = new StringBuffer();

					sb1.append("INSERT INTO VESSEL_ACT_DTTM_TRANS  ");
					sb1.append("(TRANS_NBR , VV_CD, ATB_DTTM, ATU_DTTM, GB_COD_DTTM,");
					sb1.append("GB_COL_DTTM, COD_DTTM, COL_DTTM,GB_BCOD_DTTM, GB_BCOL_DTTM,");
					sb1.append("GB_FIRST_ACT_DTTM, GB_LAST_ACT_DTTM,");
					sb1.append("LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID,SHIFT_IND) VALUES ");
					sb1.append("(:strtransnbr, :strvvcd, to_date(:stratbdttm,'dd-mm-yyyy hh24:mi'),");
					sb1.append(
							" to_date(:stratudttm,'dd-mm-yyyy hh24:mi'), to_date(:strcoddttm,'dd-mm-yyyy hh24:mi'), to_date(:strcoldttm,'dd-mm-yyyy hh24:mi'),");
					sb1.append(
							" to_date(:strcoddttm,'dd-mm-yyyy hh24:mi'), to_date(:strcoldttm,'dd-mm-yyyy hh24:mi'),");
					sb1.append(
							" to_date(:strbcoddttm,'dd-mm-yyyy hh24:mi'), to_date(:strbcoldttm,'dd-mm-yyyy hh24:mi'), to_date(:strdiscdttm,'dd-mm-yyyy hh24:mi'),");
					sb1.append(" to_date(:strloaddttm,'dd-mm-yyyy hh24:mi'), sysdate, :struserid, '1')");

					strUpdatetrans = sb1.toString();

					paramMap = new HashMap<String, Object>();
					paramMap.put("strtransnbr", strtransnbr);
					paramMap.put("strvvcd", strvvcd);
					paramMap.put("stratbdttm", stratbdttm);
					paramMap.put("stratudttm", stratudttm);
					paramMap.put("strcoddttm", strcoddttm);
					paramMap.put("strcoldttm", strcoldttm);
					paramMap.put("strbcoddttm", strbcoddttm);
					paramMap.put("strbcoldttm", strbcoldttm);
					paramMap.put("strdiscdttm", strdiscdttm);
					paramMap.put("strloaddttm", strloaddttm);
					paramMap.put("struserid", struserid);

				}

				log.info(" *** updateVesselActivity SQL *****" + strUpdatetrans + " paramMap " + paramMap.toString());

				int count1 = namedParameterJdbcTemplate.update(strUpdatetrans, paramMap);

				if (count1 == 0) {
					throw new BusinessException("M1007");
				}

			}
			if (!(strvvstatus.equalsIgnoreCase(""))) {
				updateVslActStatus(strvvstatus, strvvcd, struserid);
			}

		} catch (BusinessException e) {
			log.error("Exception: updateVesselActivity ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: updateVesselActivity ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: updateVesselActivity ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: updateVesselActivity  DAO  END");
		}

	}

	@Override
	public void updateVesselActivityShift(String strvvstatus, String[] stratbdttm, String[] stratudttm,
			String strcoddttm, String strcoldttm, String strbcoddttm, String strbcoldttm, String strdiscdttm,
			String strloaddttm, String struserid, String strvvcd, int intarrsize, String strfgcdttm,
			String strtotgencargoactivity) throws BusinessException {

		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> paramMap1 = new HashMap<String, Object>();

		SqlRowSet rsRemoveATUCheck = null;
		SqlRowSet rsLockedValueRetrieve = null;
		SqlRowSet rsstat = null;
		SqlRowSet rschk = null;
		SqlRowSet rs0 = null;
		SqlRowSet rslog = null;

		

		String strcntrcount="0";
		String sql1="";
		String strtransnbr="0";
		String sqllog="";
		String strUpdatetrans="";
		String strnewvvstatusind="";
		String strnewshiftind="";
		String sqlstat="";
		int j=2;

		try {
			
			log.info("START: updateVesselActivityShift  DAO  Start Obj " + "strvvstatus:" + CommonUtility.deNull(strvvstatus) + "stratbdttm:" + stratbdttm 
					+ "stratudttm:" + stratudttm + "strcoddttm:" + CommonUtility.deNull(strcoddttm) 
					+ "strcoldttm:" + CommonUtility.deNull(strcoldttm) + "strbcoddttm:" + CommonUtility.deNull(strbcoddttm)
					+ "strbcoldttm:" + CommonUtility.deNull(strbcoldttm) + "strdiscdttm:" + CommonUtility.deNull(strdiscdttm) 
					+ "strloaddttm:" + CommonUtility.deNull(strloaddttm) + "struserid:" + CommonUtility.deNull(struserid)
					+ "strvvcd:" + CommonUtility.deNull(strvvcd) + "intarrsize:" + intarrsize 
					+ "strfgcdttm" + CommonUtility.deNull(strfgcdttm) + "strtotgencargoactivity:" + CommonUtility.deNull(strtotgencargoactivity));
			
			String sql0 = "";
			sb.append("Select SUM(CNT) as count FROM( ");
			sb.append(" SELECT COUNT(VV_CD) as cnt FROM VV_CNTR WHERE VV_CD=:strvvcd");
			sb.append(" union");
			sb.append(" select count(disc_vv_cd) as cnt from cntr where disc_vv_cd =:strvvcd");
			sb.append(" and purp_cd = 'TS' and disc_gateway = 'J'  and load_gateway = 'P' and txn_status <> 'D'");
			sb.append(" )");
			sql0 = sb.toString();
			
			//con = DbConnectionFactory.getInstance().getConnection("GBMS");
			//con.setAutoCommit(false);

			//20181211 koktsing
			//Lock ATB, ATU, COD, COL, LAST ACTIVITY if ATU is removed and re-enter back
			//Audit requirement:
			//If any of the 5 values is not same as the previous entered values, not allow user to update
			//This is required to prevent the above 5 values being changed in the event that ATU was removed and re-enter back

			//Use case:
			//User can remove ATU in order to change the status from UB > BR.
			//By doing so, this will allow user to add GB Documentation (Manifest, Booking Reference, ESN)
			//After adding GB documentation, user will enter back the ATU, system will update the status BR > UB
			//With this enhancement, this will prevent user from update the wrong ATB, ATU, COD, COL, LAST ACTIVITY

			String sqlRemoveATUCheck="";
			sb.setLength(0);
			sb.append("SELECT A.VSL_NM, A.VV_CD, A.IN_VOY_NBR,A.OUT_VOY_NBR,  A.VV_STATUS_IND, ");
			sb.append(" to_char(A.LAST_ATU_DTTM, 'dd-mm-yyyy hh24:mi') AS LAST_ATU_DTTM, B.ATU_DTTM");
			sb.append(" FROM VESSEL_CALL A, BERTHING B");
			sb.append(" WHERE A.VV_CD=B.VV_CD AND A.VV_CD=:strvvcd");
			sb.append(" AND A.VV_STATUS_IND = 'BR'");
			sb.append(" AND A.CREATE_CUST_CD IS NOT NULL");
			sb.append(" AND A.LAST_ATU_DTTM IS NOT NULL");
			sb.append(" AND B.SHIFT_IND=(SELECT MAX(SHIFT_IND) FROM BERTHING WHERE VV_CD=:strvvcd)");

			sqlRemoveATUCheck = sb.toString();

			//sql to check the 5 locked values
			String sqlLockedValueRetrieve= "";
			//Due to multiple shift, the condition to retrieve the 5 locked value from audit_trail_berthing is when ATU_DTTM not null (latest) based on shift_ind = max shift ind
			//Below is the logic how to retrieve the lock values.
			//ATB_DTTM - can retrieve from audit_trail_berthing based on shift_ind = 1, cannot based one shift_ind=max shift ind.
			//ATU_DTTM - no need retrieve from audit_trail_berthing, just retrieve from VESSEL_CALL.LAST_ATU_DTTM will be easier,
			//GB_COD_DTTM, GB_COL_DTTM, LAST_ACT_DTTM - can retrieve from audit_trail_berthing, either shift ind=1 or max shift, they are same.
			//For simplity, GB_COD_DTTM, GB_COL_DTTM, LAST_ACT_DTTM, follow ATB_DTTM take based on shift_ind = 1.

			//Here is the possible audit_trail_berthing illustration where user remove ATU:
			//scenario: multiple shift, ATU_DTTM removed
			//system need to make sure the last ATU_DTTM is entered back
			//Shift Ind|ATB_DTTM         |ATU_DTTM      |GB_COD_DTTM      |GB_COL_DTTM      |GB_LAST_ACT_DTTM|
			//1        |aaa              |              |bbb              |ccc              |ddd             |
			//2        |                 |Empty(removed)|bbb              |ccc              |ddd             |< user remove ATU_DTTM here.
			//1        |[lockedvalue-aaa]|xxxxxxxxxxxxxx|[lockedvalue-bbb]|[lockedvalue-ccc]|[lockedvalue-ddd]< the locked values should retrieve in the last update before the ATU_DTTM remove happened
			//2        |yyyyyyyyyyyyy    |zzzzzzzzzzzzzz|[sameasabove-bbb]|[sameasabove-ccc]|[sameasabove-ddd]|

			//scenario: multiple shift, ATU_DTTM, GB_LAST_ACT_DTTM removed.
			//system need to make sure the last ATU_DTTM, GB_LAST_ACT_DTTM is entered back
			//Shift Ind|ATB_DTTM         |ATU_DTTM      |GB_COD_DTTM      |GB_COL_DTTM      |GB_LAST_ACT_DTTM|
			//1        |aaa              |xxxxxxxxxxxxxx|bbb              |ccc              |Empty(removed   |
			//2        |yyyyyyyyyyyyy    |Empty(removed)|bbb              |ccc              |Empty(removed)  |< user remove ATU_DTTM, GB_LAST_ACT_DTTM here.
			//1        |[lockedvalue-aaa]|xxxxxxxxxxxxxx|[lockedvalue-bbb]|[lockedvalue-ccc]|[lockedvalue-ddd]< the locked values should retrieve in the last update before the ATU_DTTM remove happened
			//2        |yyyyyyyyyyyyy    |zzzzzzzzzzzzzz|[sameasabove-bbb]|[sameasabove-ccc]|[sameasabove-ddd]   |

			//scenario: single shift, ATU_DTTM, GB_LAST_ACT_DTTM removed.
			//system need to make sure the last ATU_DTTM, GB_LAST_ACT_DTTM is entered back
			//Shift Ind|ATB_DTTM         |ATU_DTTM      |GB_COD_DTTM      |GB_COL_DTTM      |GB_LAST_ACT_DTTM|
			//1        |aaa              |Empty(removed)|bbb              |ccc              |Empty(removed   |< user remove ATU_DTTM, GB_LAST_ACT_DTTM here.
			//1        |[lockedvalue-aaa]|xxxxxxxxxxxxxx|[lockedvalue-bbb]|[lockedvalue-ccc]|[lockedvalue-ddd]< the locked values should retrieve in the last update before the ATU_DTTM remove happened

			sb.setLength(0);
			sb.append(" SELECT SHIFT_IND, to_char(ATB_DTTM, 'dd-mm-yyyy hh24:mi') AS ATB_DTTM,");
			sb.append(" to_char(GB_COD_DTTM, 'dd-mm-yyyy hh24:mi') AS GB_COD_DTTM,");
			sb.append(" to_char(GB_COL_DTTM, 'dd-mm-yyyy hh24:mi') AS GB_COL_DTTM,");
			sb.append(" to_char(GB_LAST_ACT_DTTM, 'dd-mm-yyyy hh24:mi') AS GB_LAST_ACT_DTTM,");
			sb.append(" AUDIT_DTTM, ATB_ATU_DTTM_NOT_NULL.* FROM AUDIT_TRAIL_BERTHING ATB1,");
			sb.append(" (");
			// Get Last GB_COD_DTTM, GB_COL_DTTM, GB_LAST_ACT_DTTM when ATU_DTTM is NOT null
			// based on the max shift_ind, this value need to be locked
			sb.append("   SELECT * FROM (");
			sb.append("     SELECT");
			sb.append("     SHIFT_IND AS MAX_SHIFT_IND,");
			sb.append("     LAST_MODIFY_USER_ID,");
			sb.append("     LAST_MODIFY_DTTM,");
			sb.append("     AUDIT_DTTM AS MAX_SHIFT_AUDIT_DTTM,");
			sb.append("     ATU_DTTM");
			sb.append("     FROM AUDIT_TRAIL_BERTHING");
			sb.append("     WHERE VV_CD = :strvvcd");
			sb.append("     AND SHIFT_IND = (SELECT MAX(B2.SHIFT_IND) FROM BERTHING B2 WHERE(B2.VV_CD=:strvvcd))");
			sb.append("     AND ATU_DTTM IS NOT NULL");
			sb.append("     ORDER BY AUDIT_DTTM DESC");
			sb.append("   ) WHERE ROWNUM = 1");
			sb.append(" ) ATB_ATU_DTTM_NOT_NULL");
			sb.append(" WHERE ATB1.VV_CD=:strvvcd");
			sb.append(" AND ATB1.SHIFT_IND='1'");
			sb.append(" AND ATB1.AUDIT_DTTM >= ATB_ATU_DTTM_NOT_NULL.MAX_SHIFT_AUDIT_DTTM");
			sb.append(" AND ATB1.LAST_MODIFY_USER_ID = ATB_ATU_DTTM_NOT_NULL.LAST_MODIFY_USER_ID");
			sb.append(" ORDER BY ATB1.AUDIT_DTTM"); // --incase more than one record return, take the ATB_DTTM closest
			// to the record when ATU_DTTM is removed.
			sqlLockedValueRetrieve = sb.toString();

			String inputatbdttm="";
            String inputatudttm="";
            String vvstatusind="";
            String lastatudttm="";
            String atudttm="";

            String lastatbdttm="";
            String lastcoddttm="";
            String lastcoldttm="";
            String lastactdttm="";

			try{

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** updateVesselActivityShift SQL sqlRemoveATUCheck*****" + sqlRemoveATUCheck);
				log.info(" *** updateVesselActivityShift paramMap ***** " + paramMap.toString());

				rsRemoveATUCheck = namedParameterJdbcTemplate.queryForRowSet(sqlRemoveATUCheck, paramMap);
				while(rsRemoveATUCheck.next()) {
                    vvstatusind=CommonUtility.deNull(rsRemoveATUCheck.getString("VV_STATUS_IND"));
                    lastatudttm=CommonUtility.deNull(rsRemoveATUCheck.getString("LAST_ATU_DTTM"));
                    atudttm=CommonUtility.deNull(rsRemoveATUCheck.getString("ATU_DTTM"));

					//koktsing 20Nov2018
					//if ATU now is empty, vv status is BR, last ATU is not empty
					//check the last ATB, ATU, COD, COL, LAST ACTIVITY if it is different previous value

					if ((atudttm == null || atudttm.trim().length() == 0)
							&& (vvstatusind != null && vvstatusind.trim().toUpperCase().equals("BR"))
							&& (lastatudttm != null && lastatudttm.trim().length() > 0))
					{
						//check the ATB, ATU, COD, COL, LAST ACTIVITY if it is different the previous value
						//The 5 values should be locked at the moment the ATU_DTTM is removed.
						//To compare if the 5 values changed, the values will be retrieve in audit_trail_berthing table
						//based on the condition when the ATU_DTTM is not empty (latest record) order by AUDIT_DTTM desc
						//                        Statement sqlstmtLockedValueRetrieve = con.createStatement();
						paramMap.put("strvvcd", strvvcd);

						log.info(" *** updateVesselActivityShift SQL sqlLockedValueRetrieve*****" + sqlLockedValueRetrieve);
						log.info(" *** updateVesselActivityShift paramMap ***** " + paramMap.toString());

						rsLockedValueRetrieve = namedParameterJdbcTemplate.queryForRowSet(sqlLockedValueRetrieve, paramMap);
						if(rsLockedValueRetrieve.next()) {
							lastatbdttm=CommonUtility.deNull(rsLockedValueRetrieve.getString("ATB_DTTM"));
							inputatbdttm=CommonUtility.deNull(stratbdttm[0]);
							if (!lastatbdttm.equals(inputatbdttm))
							{
								throw new BusinessException("Updating record failed. " + "ATB Date/Time is different from previous - '" + lastatbdttm + "'");
							}

							inputatudttm=CommonUtility.deNull(stratudttm[intarrsize-1]);
							if (!lastatudttm.equals(inputatudttm))
							{
								throw new BusinessException("Updating record failed. " + "ATU Date/Time is different from previous - '" + lastatudttm + "'");
							}

							lastcoddttm=CommonUtility.deNull(rsLockedValueRetrieve.getString("GB_COD_DTTM"));
							strcoddttm=CommonUtility.deNull(strcoddttm);
							if (!lastcoddttm.equals(strcoddttm))
							{
								throw new BusinessException("Updating record failed. " + "COD Date/Time is different from previous - '" + lastcoddttm + "'");
							}

							lastcoldttm=CommonUtility.deNull(rsLockedValueRetrieve.getString("GB_COL_DTTM"));
							strcoldttm=CommonUtility.deNull(strcoldttm);
							if (!lastcoldttm.equals(strcoldttm))
							{
								throw new BusinessException("Updating record failed. " + "COL Date/Time is different from previous - '" + lastcoldttm + "'");
							}

							lastactdttm=CommonUtility.deNull(rsLockedValueRetrieve.getString("GB_LAST_ACT_DTTM"));
							strloaddttm=CommonUtility.deNull(strloaddttm);
							if (!lastactdttm.equals(strloaddttm))
							{
								throw new BusinessException("Updating record failed. " + "Last Activity Date/Time is different from previous - '" + lastactdttm + "'");
							}

						}

					}

				}
			} catch (Exception e) {
				log.error("Exception: updateVesselActivityShift ", e);
				throw new BusinessException("M4201");
			}

			for( int i=1;i<intarrsize;i++)
			{
				try{
					sb.setLength(0);
					sqlstat="";
					sb.append("SELECT VV_STATUS_IND FROM VESSEL_CALL WHERE VV_CD=:strvvcd");
					sqlstat = sb.toString();
					paramMap.put("strvvcd", strvvcd);

					log.info(" *** updateVesselActivityShift SQL sqlstat*****" + sqlstat);
					log.info(" *** updateVesselActivityShift paramMap ***** " + paramMap.toString());

					rsstat = namedParameterJdbcTemplate.queryForRowSet(sqlstat, paramMap);
					while(rsstat.next()) {
						strnewvvstatusind=CommonUtility.deNull(rsstat.getString(1));
					}
					if (strnewvvstatusind.equalsIgnoreCase("CL"))
					{
						throw new BusinessException("M20850");
					}

				} catch (Exception e) {
					log.error("Exception: updateVesselActivityShift ", e);
					throw new BusinessException("M4201");
				}
				java.lang.String sqlchk="";
				try{
					sb.setLength(0);
					sqlchk="";
					sb.append("SELECT SHIFT_IND FROM BERTHING WHERE VV_CD=:strvvcd ");
					sb.append("AND  SHIFT_IND=:j");

					sqlchk =  sb.toString();
					paramMap.put("strvvcd", strvvcd);
					paramMap.put("j", j);

					log.info(" *** updateVesselActivityShift SQL sqlchk*****" + sqlchk);
					log.info(" *** updateVesselActivityShift paramMap ***** " + paramMap.toString());

					rschk = namedParameterJdbcTemplate.queryForRowSet(sqlchk, paramMap);
					while(rschk.next()) {
						strnewshiftind=CommonUtility.deNull(rschk.getString(1));
					}
				} catch (Exception e) {
					log.error("Exception: updateVesselActivityShift ", e);
					throw new BusinessException("M4201");
				}


				try{
					log.info(" *** updateVesselActivityShift SQL sql0*****" + sql0);
					log.info(" *** updateVesselActivityShift paramMap ***** " + paramMap.toString());
					rs0 = namedParameterJdbcTemplate.queryForRowSet(sql0, paramMap);
					while(rs0.next()) {
						strcntrcount =CommonUtility.deNull(rs0.getString(1));
					}
				} catch (Exception e) {
					log.error("Exception: updateVesselActivityShift ", e);
					throw new BusinessException("M4201");
				}
				log.info("Before updateVesselActivityShift Query #####################" + strcntrcount);
				if (strcntrcount.equalsIgnoreCase("0")){
					sql1 = "";
					sb.setLength(0);
					sb.append("UPDATE BERTHING SET ");
					sb.append("ATB_DTTM = to_date(:stratbdttmI");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("ATU_DTTM = to_date(:stratudttmI");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_COD_DTTM = to_date(:strcoddttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_COL_DTTM = to_date(:strcoldttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_BCOD_DTTM = to_date(:strbcoddttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_BCOL_DTTM = to_date(:strbcoldttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_FIRST_ACT_DTTM = to_date(:strdiscdttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_LAST_ACT_DTTM = to_date(:strloaddttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = ");
					sb.append(":struserid,");
					sb.append("GB_FIRST_CARGO_ACT_DTTM = to_date(:strfgcdttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					//added by tirumal, on 04-12-2007 for vessel activity CR
					// Start modified by madhu for Vessel Productivity Billing
					sb.append("TOT_GEN_CARGO_ACT = :strtotgencargoactivity");
					// End modiication here by madhu
					//+"GANG_NBR='"+strgangs+"',"
					//+"HATCH_NBR='"+strhatches+"',"
					//+"DELAY_RSN_CD='"+strdelayReason+"',"
					//+"REMARKS='"+strremarks+"'"
					sb.append(" WHERE VV_CD=:strvvcd AND SHIFT_IND=");
					sb.append(":j");

					sql1 = sb.toString();
					paramMap1.put("stratbdttmI", stratbdttm[i]);
					paramMap1.put("stratudttmI", stratudttm[i]);
					paramMap1.put("strcoddttm", strcoddttm);
					paramMap1.put("strcoldttm", strcoldttm);
					paramMap1.put("strbcoddttm", strbcoddttm);
					paramMap1.put("strbcoldttm", strbcoldttm);
					paramMap1.put("strdiscdttm", strdiscdttm);
					paramMap1.put("strloaddttm", strloaddttm);
					paramMap1.put("struserid", struserid);
					paramMap1.put("strfgcdttm", strfgcdttm);
					paramMap1.put("strtotgencargoactivity", strtotgencargoactivity);
					paramMap1.put("strvvcd", strvvcd);
					paramMap1.put("j", j);

				}else{
					sql1= "";
					sb.setLength(0);
					sb.append("UPDATE BERTHING SET ");
					sb.append("ATB_DTTM = to_date(:stratbdttmI");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("ATU_DTTM = to_date(:stratudttmI");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_COD_DTTM = to_date(:strcoddttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_COL_DTTM = to_date(:strcoldttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("COD_DTTM = to_date(:strcoddttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("COL_DTTM = to_date(:strcoldttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_BCOD_DTTM = to_date(:strbcoddttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_BCOL_DTTM = to_date(:strbcoldttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_FIRST_ACT_DTTM = to_date(:strdiscdttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("GB_LAST_ACT_DTTM = to_date(:strloaddttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					sb.append("LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = ");
					sb.append(":struserid,");
					sb.append("GB_FIRST_CARGO_ACT_DTTM = to_date(:strfgcdttm");
					sb.append(",'dd-mm-yyyy hh24:mi'), ");
					//added by tirumal, on 04-12-2007 for vessel activity CR
					// Start modified by madhu for Vessel Productivity Billing
					sb.append("TOT_GEN_CARGO_ACT = :strtotgencargoactivity ");
					// End modiication here by madhu
					//+"GANG_NBR='"+strgangs+"',"
					//+"HATCH_NBR='"+strhatches+"',"
					//+"DELAY_RSN_CD='"+strdelayReason+"',"
					//+"REMARKS='"+strremarks+"'"
					sb.append(" WHERE VV_CD=:strvvcd AND SHIFT_IND=");
					sb.append(":j");

					sql1 = sb.toString();
					paramMap1.put("stratbdttmI", stratbdttm[i]);
					paramMap1.put("stratudttmI", stratudttm[i]);
					paramMap1.put("strcoddttm", strcoddttm);
					paramMap1.put("strcoldttm", strcoldttm);
					paramMap1.put("strbcoddttm", strbcoddttm);
					paramMap1.put("strbcoldttm", strbcoldttm);
					paramMap1.put("strdiscdttm", strdiscdttm);
					paramMap1.put("strloaddttm", strloaddttm);
					paramMap1.put("struserid", struserid);
					paramMap1.put("strfgcdttm", strfgcdttm);
					paramMap1.put("strtotgencargoactivity", strtotgencargoactivity);
					paramMap1.put("strvvcd", strvvcd);
					paramMap1.put("j", j);
				}

				if (strnewshiftind.equalsIgnoreCase(String.valueOf(j)))
				{
					log.info(" *** updateVesselActivityShift SQL sql1*****" + sql1);
					log.info(" *** updateVesselActivityShift paramMap ***** " + paramMap1.toString());
					log.info("Before updateVesselActivityShift Query #####################");
					int count= namedParameterJdbcTemplate.update(sql1, paramMap1);
					if (count == 0) {
						//                            sessionContext.setRollbackOnly();
						throw new BusinessException("M1007");
					}

					if (logStatusGlobal.equalsIgnoreCase("Y"))
					{
						try{
							sb.setLength(0);
							sqllog = "";
							sb.append("SELECT MAX(TRANS_NBR) FROM VESSEL_ACT_DTTM_TRANS ");
							sb.append(" WHERE VV_CD=:strvvcd ");
							sb.append(" AND SHIFT_IND=:j");

							paramMap.put("strvvcd", strvvcd);
							paramMap.put("j", j);

							log.info(" *** updateVesselActivityShift SQL sqllog*****" + sqllog);
							log.info(" *** updateVesselActivityShift paramMap ***** " + paramMap.toString());

							rslog = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
							while(rslog.next()) {
								strtransnbr =CommonUtility.deNull(rslog.getString(1));
							}

							if (strtransnbr.equalsIgnoreCase(""))
							{
								strtransnbr="0";
							}else{
								strtransnbr=String.valueOf(Integer.parseInt(strtransnbr)+1);
							}
						} catch (Exception e) {
							log.error("Exception: updateVesselActivityShift ", e);
							throw new BusinessException("M4201");
						}
						sb.setLength(0);
						strUpdatetrans = "";
						sb.append("INSERT INTO VESSEL_ACT_DTTM_TRANS  ");
						sb.append("(TRANS_NBR , VV_CD, ATB_DTTM, ATU_DTTM,");
						sb.append("LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID, SHIFT_IND ) VALUES ");
						sb.append("(:strtransnbr, :strvvcd");
						sb.append(", to_date(:stratbdttmI");
						sb.append(",'dd-mm-yyyy hh24:mi'), to_date(");
						sb.append(" :stratudttmI,'dd-mm-yyyy hh24:mi'), to_date(");
						sb.append(" :strcoddttm,'dd-mm-yyyy hh24:mi'), to_date(");
						sb.append(" :strcoldttm,'dd-mm-yyyy hh24:mi'), to_date(");
						sb.append(" :strbcoddttm,'dd-mm-yyyy hh24:mi'), to_date(");
						sb.append(" :strbcoldttm,'dd-mm-yyyy hh24:mi'), to_date(");
						sb.append(" :strdiscdttm,'dd-mm-yyyy hh24:mi'), to_date(");
						sb.append(" :strloaddttm,'dd-mm-yyyy hh24:mi'), sysdate, ");
						sb.append(" :struserid,:j)");

						strUpdatetrans =  sb.toString();
						paramMap1.put("strtransnbr", strtransnbr);
						paramMap1.put("strvvcd", strvvcd);
						paramMap1.put("stratbdttmI", stratbdttm[i]);
						paramMap1.put("stratudttmI", stratudttm[i]);
						paramMap1.put("strcoddttm", strcoddttm);
						paramMap1.put("strcoldttm", strcoldttm);
						paramMap1.put("strbcoddttm", strbcoddttm);
						paramMap1.put("strbcoldttm", strbcoldttm);
						paramMap1.put("strdiscdttm", strdiscdttm);
						paramMap1.put("strloaddttm", strloaddttm);
						paramMap1.put("struserid", struserid);
						paramMap1.put("j", j);

						log.info(" *** updateVesselActivityShift SQL strUpdatetrans*****" + strUpdatetrans);
						log.info(" *** updateVesselActivityShift paramMap ***** " + paramMap1.toString());

						int count1=namedParameterJdbcTemplate.update(strUpdatetrans, paramMap1);
						if (count1 == 0) {
							//                                sessionContext.setRollbackOnly();
							throw new BusinessException("M1007");
						}
					}
				}
				j++;
			}
			updateVesselActivity(strvvstatus, stratbdttm[0], stratudttm[0],
					strcoddttm, strcoldttm, strbcoddttm, strbcoldttm,
					strdiscdttm, strloaddttm, struserid,strvvcd, strfgcdttm,strtotgencargoactivity);

		} catch (BusinessException e) {
			log.error("Exception: updateVesselActivityShift ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: updateVesselActivityShift ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: updateVesselActivityShift ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: updateVesselActivityShift  DAO  END");
		}
	}

	private void updateVslActStatus(String strvvstatus, String strvvcd, String struserid) throws BusinessException {
		String gbclosevslind = "N";
		String gbclosebjind = "Y";
		String gbcloseshpind = "Y";
		String strnewvvstatusind = "";
		String strtransaction = "O";
		String strTerminal = "";
		boolean chkSemiConVsl = false;
		String sql2 = "";
		String sql3 = "";
		if (strvvstatus.equalsIgnoreCase("CL")) {
			gbclosevslind = "Y";
		}
		StringBuffer sb = new StringBuffer();
		String strUpdatetrans = "";
		SqlRowSet rs2 = null;
		SqlRowSet rscnt = null;
		SqlRowSet rslog = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: updateVesselActStatus  DAO  Start Obj " + " strvvstatus: "
					+ CommonUtility.deNull(strvvstatus) + " strvvcd: " + CommonUtility.deNull(strvvcd) + " struserid: "
					+ CommonUtility.deNull(struserid));

			sql2 = "SELECT GB_CLOSE_BJ_IND, GB_CLOSE_SHP_IND, VV_STATUS_IND, TERMINAL, CNTR_VSL_IND FROM VESSEL_CALL WHERE VV_CD= :strvvcd";

			String strtransnbr = "0";
			String sqllog = "SELECT MAX(TRANS_NBR) FROM OPS_CLOSE_TRANS WHERE VV_CD= :strvvcd";

			paramMap.put("strvvcd", strvvcd);

			log.info(" *** updateVesselActStatus SQL *****" + sql2 + " paramMap " + paramMap.toString());
			rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);

			while (rs2.next()) {
				gbclosebjind = CommonUtility.deNull(rs2.getString(1));
				gbcloseshpind = CommonUtility.deNull(rs2.getString(2));
				strnewvvstatusind = CommonUtility.deNull(rs2.getString(3));
				strTerminal = CommonUtility.deNull(rs2.getString(4));
			}
			if (strnewvvstatusind.equalsIgnoreCase("CL")) {
				throw new BusinessException("M20850");
			}
			if (strTerminal.equalsIgnoreCase("CT")) {
				// in future to check
				// if ((strTerminal.equalsIgnoreCase("CT")) &&
				// (strVslCntrInd.equalsIgnoreCase("B")
				chkSemiConVsl = true;
			}
			/*
			 * if (gbclosevslind.equalsIgnoreCase("Y") && gbclosebjind.equalsIgnoreCase("Y")
			 * && gbcloseshpind.equalsIgnoreCase("Y") && (!chkSemiConVsl)) { sql3=
			 * "UPDATE VESSEL_CALL SET VV_STATUS_IND='"+strvvstatus
			 * +"' , GB_CLOSE_VSL_IND ='"+gbclosevslind
			 * +"', LAST_MODIFY_DTTM = sysdate,VV_CLOSE_DTTM=sysdate,"
			 * +" LAST_MODIFY_USER_ID = '"+struserid +"' WHERE VV_CD='"+strvvcd+"'"; }else{
			 * sql3= "UPDATE VESSEL_CALL SET GB_CLOSE_VSL_IND ='"+gbclosevslind
			 * +"', LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = '"+struserid
			 * +"' WHERE VV_CD='"+strvvcd+"'"; }
			 */
			/* update by swarna 16-08 */
			if (gbclosevslind.equalsIgnoreCase("Y")) {
				String mftcnt = "N", bkcnt = "N";
				String sqlcnt = "SELECT COUNT(*) FROM MANIFEST_DETAILS WHERE VAR_NBR = :strvvcd AND BL_STATUS = 'A'";
				String sqlcnt1 = "SELECT COUNT(*) FROM ESN WHERE OUT_VOY_VAR_NBR = :strvvcd AND ESN_STATUS = 'A' ";

				paramMap = new HashMap<String, Object>();
				paramMap.put("strvvcd", strvvcd);

				log.info(" *** updateVesselActStatus SQL *****" + sqlcnt + " paramMap " + paramMap.toString());
				rscnt = namedParameterJdbcTemplate.queryForRowSet(sqlcnt, paramMap);
				if (rscnt.next()) {
					mftcnt = rscnt.getString(1);
				}
				if (!(mftcnt.equalsIgnoreCase("0"))) {
					mftcnt = "N";
				}

				paramMap = new HashMap<String, Object>();
				paramMap.put("strvvcd", strvvcd);

				log.info(" *** updateVesselActStatus SQL *****" + sqlcnt1 + " paramMap " + paramMap.toString());

				rscnt = namedParameterJdbcTemplate.queryForRowSet(sqlcnt1, paramMap);
				if (rscnt.next()) {
					bkcnt = rscnt.getString(1);
				}
				if (!(bkcnt.equalsIgnoreCase("0"))) {
					bkcnt = "N";
				}

				// Old If Clause
				if (((gbclosevslind.equalsIgnoreCase("Y")) && (gbclosebjind.equalsIgnoreCase("Y"))
						&& (gbcloseshpind.equalsIgnoreCase("Y")))
						|| ((mftcnt.equalsIgnoreCase("0") && bkcnt.equalsIgnoreCase("0"))
								|| (mftcnt.equalsIgnoreCase("N") && bkcnt.equalsIgnoreCase("0")
										&& gbclosebjind.equalsIgnoreCase("Y"))
								|| (mftcnt.equalsIgnoreCase("0") && bkcnt.equalsIgnoreCase("N")
										&& gbcloseshpind.equalsIgnoreCase("Y")))) {
					if (!chkSemiConVsl) {

						sb = new StringBuffer();
						sb.append(
								"UPDATE VESSEL_CALL SET VV_STATUS_IND= :strvvstatus , GB_CLOSE_VSL_IND =:gbclosevslind, ");
						sb.append("LAST_MODIFY_DTTM = sysdate, VV_CLOSE_DTTM=sysdate,");
						sb.append(" LAST_MODIFY_USER_ID = :struserid WHERE VV_CD= :strvvcd");
						sql3 = sb.toString();
						paramMap.put("strvvstatus", strvvstatus);
						paramMap.put("gbclosevslind", gbclosevslind);
						paramMap.put("struserid", struserid);
						paramMap.put("strvvcd", strvvcd);
					} else {
						sb = new StringBuffer();
						sb.append("UPDATE VESSEL_CALL SET GB_CLOSE_VSL_IND =:gbclosevslind");
						sb.append(", LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid WHERE");
						sb.append(" VV_CD= :strvvcd ");

						sql3 = sb.toString();
						paramMap.put("gbclosevslind", gbclosevslind);
						paramMap.put("struserid", struserid);
						paramMap.put("strvvcd", strvvcd);
					}
				} else {

					sb.append("UPDATE VESSEL_CALL SET GB_CLOSE_VSL_IND =:gbclosevslind, ");
					sb.append("LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid");
					sb.append(" WHERE VV_CD= :strvvcd");

					sql3 = sb.toString();
					paramMap.put("gbclosevslind", gbclosevslind);
					paramMap.put("struserid", struserid);
					paramMap.put("strvvcd", strvvcd);
				}
			}
			/* end */

			if (gbclosevslind.equalsIgnoreCase("N")) {
				if (!chkSemiConVsl) {
					sb.append("UPDATE VESSEL_CALL SET VV_STATUS_IND= :strvvstatus ,");
					sb.append(" GB_CLOSE_VSL_IND =:gbclosevslind, ");
					sb.append("LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid");
					sb.append(" WHERE VV_CD= :strvvcd");

					sql3 = sb.toString();
					paramMap.put("strvvstatus", strvvstatus);
					paramMap.put("gbclosevslind", gbclosevslind);
					paramMap.put("struserid", struserid);
					paramMap.put("strvvcd", strvvcd);
				} else {
					sb.append("UPDATE VESSEL_CALL SET GB_CLOSE_VSL_IND = :gbclosevslind, ");
					sb.append("LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid");
					sb.append(" WHERE VV_CD= :strvvcd");

					sql3 = sb.toString();
					paramMap.put("gbclosevslind", gbclosevslind);
					paramMap.put("struserid", struserid);
					paramMap.put("strvvcd", strvvcd);

				}
			}

			log.info("private.updateVesselActStatus:" + sql3);
			log.info("**********************************");

			log.info(" *** updateVesselActStatus SQL *****" + sql3 + " paramMap " + paramMap.toString());

			int count = namedParameterJdbcTemplate.update(sql3, paramMap);

			// 04/08/2011 PCYAP To implement late arrival waiver approving workflow
			if (gbclosevslind != null && gbclosevslind.equalsIgnoreCase("Y")) {

				BigDecimal gbArrivalWaiverAmount = lateArrivalRepo.calculateGbArrivalWaiverAmount(strvvcd);

				lateArrivalRepo.updateGbArrivalWaiver(strvvcd, null, gbArrivalWaiverAmount, struserid);
			}

			if (count == 0) {
				throw new BusinessException("M1007");
			}

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				try {

					paramMap.put("strvvcd", strvvcd);
					log.info(" *** updateVesselActStatus SQL *****" + sqllog + " paramMap " + paramMap.toString());
					rslog = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
					while (rslog.next()) {
						strtransnbr = CommonUtility.deNull(rslog.getString(1));
					}

					if (strtransnbr.equalsIgnoreCase("")) {
						strtransnbr = "0";
					} else {
						strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
					}
				} catch (Exception e) {
					log.error("Exception: chkEdoCrgStatus ", e);
					throw new BusinessException("M204");
				}
				if (gbclosevslind.equalsIgnoreCase("Y")) {
					strtransaction = "C";
				}
				if (gbclosevslind.equalsIgnoreCase("N")) {
					strtransaction = "O";
				}

				StringBuffer sb1 = new StringBuffer();

				sb1.append("INSERT INTO OPS_CLOSE_TRANS (TRANS_NBR,VV_CD, TRANS_TYPE,TRANS_ACTION, LAST_MODIFY_DTTM,");
				sb1.append(
						"LAST_MODIFY_USER_ID) VALUES (:strtransnbr,:strvvcd,'V',:strtransaction,sysdate,:struserid) ");
				strUpdatetrans = sb1.toString();

				paramMap.put("strtransnbr", strtransnbr);
				paramMap.put("strvvcd", strvvcd);
				paramMap.put("strtransaction", strtransaction);
				paramMap.put("struserid", struserid);

				int count1 = namedParameterJdbcTemplate.update(strUpdatetrans, paramMap);

				if (count1 == 0) {
					throw new BusinessException("M1007");
				}

			}
		} catch (BusinessException e) {
			log.error("Exception: updateVesselActStatus ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: updateVesselActStatus ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: updateVesselActStatus ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateVesselActStatus  DAO  END");
		}

	}

	@Override
	public String getBillList(String strvvcd) throws BusinessException {

		SqlRowSet rs = null;
		String sql = "";
		sql = "SELECT GB_BERT_BILL_IND FROM VESSEL_CALL A WHERE VV_CD= :strvvcd ";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String billstring = "";
		try {
			log.info("START: getBillList  DAO  Start Obj ");

			paramMap.put("strvvcd", strvvcd);

			log.info(" *** getBillList SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				billstring = CommonUtility.deNull(rs.getString(1));
			}

			log.info("END: *** getBillList Result *****" + billstring.toString());
			return billstring;
		} catch (NullPointerException e) {
			log.error("Exception: getBillList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getBillList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBillList  DAO  END");
		}

	}

	@Override
	public String getCodColStatus(String strvvcd) throws BusinessException {

		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3 = null;
		SqlRowSet rs4 = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getCodColStatus  DAO  Start Obj " + " strvvcd:" + strvvcd);

			String sql1 = "SELECT COUNT(*) FROM MANIFEST_DETAILS WHERE  BL_STATUS='A' AND VAR_NBR= :strvvcd";
			String strsql1 = "0";
			String sql2 = "SELECT COUNT(*) FROM ESN WHERE OUT_VOY_VAR_NBR = :strvvcd AND ESN_STATUS = 'A' ";
			String strsql2 = "0";

			String sql3 = "Select count(*) from bulk_manifest_details where Var_nbr = :strvvcd and BL_status ='A'";
			String strsql3 = "0";
			String sql4 = "SELECT COUNT(*) FROM Bulk_ESN WHERE OUT_VOY_VAR_NBR = :strvvcd And ESN_STATUS = 'A'";
			String strsql4 = "0";

			try {

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** getCodColStatus SQL *****" + sql1 + " paramMap " + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);

				while (rs1.next()) {
					strsql1 = CommonUtility.deNull(rs1.getString(1));
				}

			} catch (Exception e) {
				log.error("Exception: getCodColStatus ", e);
				throw new BusinessException("M1004");
			}
			try {

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** getCodColStatus SQL *****" + sql2 + " paramMap " + paramMap.toString());
				rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);

				while (rs2.next()) {
					strsql2 = CommonUtility.deNull(rs2.getString(1));
				}

			} catch (Exception e) {
				log.error("Exception: getCodColStatus ", e);
				throw new BusinessException("M1004");
			}

			try {

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** getCodColStatus SQL *****" + sql3 + " paramMap " + paramMap.toString());
				rs3 = namedParameterJdbcTemplate.queryForRowSet(sql3, paramMap);

				while (rs3.next()) {
					strsql3 = CommonUtility.deNull(rs3.getString(1));
				}

			} catch (Exception e) {
				log.error("Exception: getCodColStatus ", e);
				throw new BusinessException("M1004");
			}

			try {

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** getCodColStatus SQL *****" + sql4 + " paramMap " + paramMap.toString());
				rs4 = namedParameterJdbcTemplate.queryForRowSet(sql4, paramMap);
				while (rs4.next()) {
					strsql4 = CommonUtility.deNull(rs4.getString(1));
				}

			} catch (Exception e) {
				log.error("Exception: getCodColStatus ", e);
				throw new BusinessException("M1004");
			}
			if (strsql1.equalsIgnoreCase("")) {
				strsql1 = "0";
			}
			if (strsql2.equalsIgnoreCase("")) {
				strsql2 = "0";
			}
			if (strsql3.equalsIgnoreCase("")) {
				strsql3 = "0";
			}
			if (strsql4.equalsIgnoreCase("")) {
				strsql4 = "0";
			}

			return strsql1 + "-" + strsql2 + "-" + strsql3 + "-" + strsql4;

		} catch (NullPointerException e) {
			log.error("Exception: getCodColStatus ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getCodColStatus ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCodColStatus  DAO  END");
		}

	}

	@Override
	public List<String> getWaiverList(String strvvcd, String strwaiverstatus) throws BusinessException {

		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<String> waivervector = new ArrayList<String>();
		String gb_arrival_waiver_cd = "";
		String gb_arrival_waiver_reason = "";
		String gb_departure_waiver_cd = "";
		String gb_departure_waiver_reason = "";
		String strnewvvstatusind = "";
		try {

			log.info("START: getWaiverList  DAO  Start Obj " + " strvvcd:" + strvvcd + " strwaiverstatus:"
					+ strwaiverstatus);

			String sql = "SELECT WAIVER_CD,WAIVER_NM FROM  WAIVER_CODE WHERE WAIVER_STATUS='A' ORDER BY WAIVER_CD";

			sb.append("SELECT GB_ARRIVAL_WAIVER_CD,GB_ARRIVAL_WAIVER_REASON,");
			sb.append(" GB_DEPARTURE_WAIVER_CD, GB_DEPARTURE_WAIVER_REASON, ");
			sb.append(" VV_STATUS_IND FROM VESSEL_CALL WHERE VV_CD=:strvvcd ");
			String sql1 = sb.toString();

			try {

				log.info(" *** getWaiverList SQL *****" + sql + " paramMap " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				while (rs.next()) {
					String waivercd = CommonUtility.deNull(rs.getString(1));
					String waivernm = CommonUtility.deNull(rs.getString(2));
					waivervector.add(waivercd);
					waivervector.add(waivernm);
				}

			} catch (Exception e) {
				log.error("Exception: getWaiverList ", e);
				throw new BusinessException("M1007");
			}
			try {

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** getWaiverList SQL *****" + sql1 + " paramMap " + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);

				while (rs1.next()) {
					gb_arrival_waiver_cd = CommonUtility.deNull(rs1.getString(1));
					gb_arrival_waiver_reason = CommonUtility.deNull(rs1.getString(2));
					gb_departure_waiver_cd = CommonUtility.deNull(rs1.getString(3));
					gb_departure_waiver_reason = CommonUtility.deNull(rs1.getString(4));
					strnewvvstatusind = CommonUtility.deNull(rs1.getString(5));
				}
				if (strnewvvstatusind.equalsIgnoreCase("CL")) {
					throw new BusinessException("M20850");
				}
				if (strwaiverstatus.equalsIgnoreCase("Late Arrival Waiver")) {
					waivervector.add(gb_arrival_waiver_cd);
					waivervector.add(gb_arrival_waiver_reason);
				}
				if (strwaiverstatus.equalsIgnoreCase("Late Departure Waiver")) {
					waivervector.add(gb_departure_waiver_cd);
					waivervector.add(gb_departure_waiver_reason);
				}

			} catch (Exception e) {
				log.error("Exception: getWaiverList ", e);
				throw new BusinessException("M1007");
			}

			log.info("END: *** getWaiverList Result *****" + waivervector.toString());
			return waivervector;
		} catch (BusinessException e) {
			log.error("Exception: getWaiverList ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: getWaiverList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getWaiverList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWaiverList  DAO  END");
		}

	}

	@Override
	public void updateBillDetails(String strbillcd, String struserid, String strvvcd) throws BusinessException {

		SqlRowSet rs1 = null;
		SqlRowSet rslog = null;
		String strUpdatetrans = "";

		String strnewvvstatusind = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: updateBillDetails  DAO  Start Obj " + " strbillcd:" + strbillcd + " struserid:" + struserid
					+ " strvvcd:" + strvvcd);

			String sql1 = "SELECT VV_STATUS_IND FROM VESSEL_CALL WHERE VV_CD= :strvvcd ";
			String sql2 = "";
			sb.append("UPDATE VESSEL_CALL SET ");
			sb.append("GB_BERT_BILL_IND = :strbillcd, LAST_MODIFY_DTTM = sysdate,");
			sb.append(" LAST_MODIFY_USER_ID = :struserid WHERE VV_CD= :strvvcd ");
			sql2 = sb.toString();
			String strtransnbr = "0";
			String sqllog = "SELECT MAX(TRANS_NBR) FROM VESSEL_ACT_BILL_TRANS WHERE VV_CD= :strvvcd";

			try {
				paramMap.put("strvvcd", strvvcd);
				log.info(" *** updateBillDetails SQL *****" + sql1 + " paramMap " + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
				while (rs1.next()) {
					strnewvvstatusind = CommonUtility.deNull(rs1.getString(1));
				}
				if (strnewvvstatusind.equalsIgnoreCase("CL")) {
					throw new BusinessException("M20850");
				}

			} catch (Exception e) {
				log.error("Exception: updateBillDetails ", e);
				throw new BusinessException("M1007");
			}

			paramMap.put("strbillcd", strbillcd);
			paramMap.put("struserid", struserid);
			paramMap.put("strvvcd", strvvcd);

			log.info(" *** updateBillDetails SQL *****" + sql2 + " paramMap " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sql2, paramMap);
			if (count == 0) {
				throw new BusinessException("M1007");
			}

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				try {

					paramMap = new HashMap<String, Object>();

					paramMap.put("strvvcd", strvvcd);

					log.info(" *** updateBillDetails SQL *****" + sqllog + " paramMap " + paramMap.toString());
					rslog = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
					while (rslog.next()) {
						strtransnbr = CommonUtility.deNull(rslog.getString(1));
					}

					if (strtransnbr.equalsIgnoreCase("")) {
						strtransnbr = "0";
					} else {
						strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
					}
				} catch (Exception e) {
					log.error("Exception: updateBillDetails ", e);
					throw new BusinessException("M1007");
				}

				StringBuffer sb1 = new StringBuffer();

				sb1.append("INSERT INTO VESSEL_ACT_BILL_TRANS ");
				sb1.append("(TRANS_NBR,VV_CD, GB_BERT_BILL_IND, LAST_MODIFY_DTTM,");
				sb1.append("LAST_MODIFY_USER_ID) VALUES (:strtransnbr,:strvvcd,:strbillcd, sysdate,:struserid) ");
				strUpdatetrans = sb1.toString();

				paramMap.put("strtransnbr", strtransnbr);
				paramMap.put("strvvcd", strvvcd);
				paramMap.put("strbillcd", strbillcd);
				paramMap.put("struserid", struserid);
				int count1 = namedParameterJdbcTemplate.update(strUpdatetrans, paramMap);
				if (count1 == 0) {
					throw new BusinessException("M1007");
				}

			}

			log.info("END: *** updateBillDetails Result *****");
		} catch (BusinessException e) {
			log.error("Exception: updateBillDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: updateBillDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: updateBillDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateBillDetails  DAO  END");
		}

	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateWaiverDetails(String strWaiverCd, String strWaiverReason, String strwaiverstatus,
			String struserid, String strvvcd) throws BusinessException {
		String sql2 = "";
		String strtranstype = "";
		String strtransnbr = "0";
		String strUpdatetrans = "";

		String strnewvvstatusind = "";

		SqlRowSet rs1 = null;
		SqlRowSet rslog = null;
		StringBuffer sb = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: updateWaiverDetails  DAO  Start Obj " + " strWaiverCd: "
					+ CommonUtility.deNull(strWaiverCd) + " strWaiverReason: " + CommonUtility.deNull(strWaiverReason)
					+ " strwaiverstatus: " + CommonUtility.deNull(strwaiverstatus) + " struserid: "
					+ CommonUtility.deNull(struserid) + " strvvcd: " + CommonUtility.deNull(strvvcd));

			String sql1 = "SELECT VV_STATUS_IND FROM VESSEL_CALL WHERE VV_CD= :strvvcd ";

			String sqllog = "SELECT MAX(TRANS_NBR) FROM VESSEL_ACT_WAIVER_TRANS WHERE VV_CD= :strvvcd ";

			if (strwaiverstatus.equalsIgnoreCase("Late Arrival Waiver")) {

				sb1 = new StringBuffer();
				sb1.append("UPDATE VESSEL_CALL SET ");
				sb1.append("GB_ARRIVAL_WAIVER_CD = :strWaiverCd, GB_ARRIVAL_WAIVER_REASON = :strWaiverReason, ");
				sb1.append("LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid WHERE VV_CD= :strvvcd");

				paramMap.put("strWaiverCd", strWaiverCd);
				paramMap.put("strWaiverReason", strWaiverReason);
				paramMap.put("struserid", struserid);
				paramMap.put("strvvcd", strvvcd);

				sql2 = sb1.toString();
				if (strWaiverCd == null || strWaiverCd.equals("")) {
					sb1 = new StringBuffer();
					sb1.append("UPDATE VESSEL_CALL SET ");
					sb1.append(
							"GB_ARRIVAL_WAIVER_CD = :strWaiverCd, GB_ARRIVAL_WAIVER_REASON = :strWaiverReason, GB_ARRIVAL_WAIVER_IND ='',");
					sb1.append(" LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid WHERE VV_CD= :strvvcd");
					sb1.append("   AND GB_ARRIVAL_WAIVER_IND != 'A'");
					sql2 = sb1.toString();

					paramMap.put("strWaiverCd", strWaiverCd);
					paramMap.put("strWaiverReason", strWaiverReason);
					paramMap.put("struserid", struserid);
					paramMap.put("strvvcd", strvvcd);
				}
				strtranstype = "A";
			}
			if (strwaiverstatus.equalsIgnoreCase("Late Departure Waiver")) {
				sb1 = new StringBuffer();
				sb1.append("UPDATE VESSEL_CALL SET ");
				sb1.append("GB_DEPARTURE_WAIVER_CD = :strWaiverCd, GB_DEPARTURE_WAIVER_REASON = :strWaiverReason,");
				sb1.append("LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid WHERE VV_CD= :strvvcd");
				strtranstype = "D";

				paramMap.put("strWaiverCd", strWaiverCd);
				paramMap.put("strWaiverReason", strWaiverReason);
				paramMap.put("struserid", struserid);
				paramMap.put("strvvcd", strvvcd);
			}

			try {

				paramMap.put("strvvcd", strvvcd);

				log.info(" *** updateWaiverDetails SQL *****" + sql1 + " paramMap " + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);

				while (rs1.next()) {
					strnewvvstatusind = CommonUtility.deNull(rs1.getString(1));
				}
				if (strnewvvstatusind.equalsIgnoreCase("CL")) {
					throw new BusinessException("M20850");
				}

			}

			catch (Exception e) {
				log.error("Exception: updateWaiverDetails ", e);
				throw new BusinessException("M1007");
			}

			log.info(" *** updateWaiverDetails SQL *****" + sql2 + " paramMap " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sql2, paramMap);

			if (count == 0) {
				throw new BusinessException("M1007");
			}

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				try {

					paramMap = new HashMap<String, Object>();
					paramMap.put("strvvcd", strvvcd);

					log.info(" *** updateWaiverDetails SQL *****" + sqllog + " paramMap " + paramMap.toString());
					rslog = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);

					while (rslog.next()) {
						strtransnbr = CommonUtility.deNull(rslog.getString(1));
					}

					if (strtransnbr.equalsIgnoreCase("")) {
						strtransnbr = "0";
					} else {
						strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
					}
				} catch (Exception e) {
					log.error("Exception: updateWaiverDetails ", e);
					throw new BusinessException("M1007");
				}

				sb.append("INSERT INTO VESSEL_ACT_WAIVER_TRANS ");
				sb.append("(TRANS_NBR,VV_CD, TRANS_TYPE, GB_WAIVER_CD,");
				sb.append(" GB_WAIVER_REASON, LAST_MODIFY_DTTM,");
				sb.append("LAST_MODIFY_USER_ID) VALUES (:strtransnbr,:strvvcd,");
				sb.append(":strtranstype,:strWaiverCd,:strWaiverReason, sysdate,:struserid) ");
				strUpdatetrans = sb.toString();

				paramMap.put("strtransnbr", strtransnbr);
				paramMap.put("strvvcd", strvvcd);
				paramMap.put("strtranstype", strtranstype);
				paramMap.put("strWaiverCd", strWaiverCd);
				paramMap.put("strWaiverReason", strWaiverReason);
				paramMap.put("struserid", struserid);

				log.info(" *** updateWaiverDetails SQL *****" + strUpdatetrans + " paramMap " + paramMap.toString());
				int count1 = namedParameterJdbcTemplate.update(strUpdatetrans, paramMap);
				if (count1 == 0) {
					throw new BusinessException("M1007");
				}

			}

			// 04/08/2011 PCYAP To implement late arrival waiver approving workflow
			if (strwaiverstatus.equalsIgnoreCase("Late Arrival Waiver")) {
				// the duty officer changes the value of Late Arrival Waiver to other values
				// than ï¿½NO WAIVERï¿½
				if (strWaiverCd != null && !strWaiverCd.equals(ConstantUtil.GB_ARRIVAL_WAIVER_IND_NO_WAIVER)) {

					GbArrivalWaiver gbArrivalWaiver = lateArrivalRepo.retrieveGbArrivalWaiver(strvvcd);

					// if the current value of Late Arrival Waiver status is ï¿½NO WAIVERï¿½
					if (gbArrivalWaiver.getGbArrivalWaiverInd() == null || gbArrivalWaiver.getGbArrivalWaiverInd()
							.equals(ConstantUtil.GB_ARRIVAL_WAIVER_IND_NO_WAIVER)) {
						BigDecimal gbArrivalWaiverAmount = lateArrivalRepo.calculateGbArrivalWaiverAmount(strvvcd);

						lateArrivalRepo.updateGbArrivalWaiver(strvvcd, ConstantUtil.GB_ARRIVAL_WAIVER_IND_PENDING,
								gbArrivalWaiverAmount, struserid);

						String[] approverEmail = lateArrivalRepo
								.retrieveApproverEmail(gbArrivalWaiverAmount.doubleValue());

						lateArrivalRepo.sendSubmissionAlert(strvvcd, approverEmail);
					}
				}
			}

			log.info("END: *** updateWaiverDetails Result *****");
		} catch (BusinessException e) {
			log.error("Exception: updateWaiverDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: updateWaiverDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: updateWaiverDetails ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: updateWaiverDetails  DAO  END");
		}
	}

	@Override
	public TableResult getVesselActList(String strCustCode, String strvslnm, String strvvcd, String atbFromTime,
			String atbToTime, String atuFromTime, String atuToTime, String schemdCd, Criteria criteria)
			throws BusinessException {
		log.info("----- In getVesselActList  : ");

		String sql = "";
		SqlRowSet delayRs = null;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<VesselActValueObject> vesselactlist = new ArrayList<VesselActValueObject>();
		StringBuffer sb = new StringBuffer();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();
		try {

			log.info("START: getVesselActList  DAO  Start Obj " + " strCustCode:" + strCustCode + " strvslnm:"
					+ strvslnm + " strvvcd:" + strvvcd + " atbFromTime:" + atbFromTime + " atbToTime:" + atbToTime
					+ " atuFromTime:" + atuFromTime + " atuToTime:" + atuToTime + " schemdCd:" + schemdCd);

			// START Code added by Tirumal to implement VesselActivity CR, dated 04-12-2007
			String delaySql = "select misc_type_cd, misc_type_nm from MISC_TYPE_CODE where cat_cd = 'WC_DELAY' and rec_status = 'A' order by misc_type_nm";
			// END Code added by Tirumal to implement VesselActivity CR, dated 04-12-2007

			if (strvslnm.equalsIgnoreCase("--Select All--")) {
				// sql="SELECT A.VV_CD, A.VSL_NM, A.IN_VOY_NBR,A.OUT_VOY_NBR,"
				// +" to_char(B.ATB_DTTM, 'DD/MM/YYYY/HH24/MI'),"
				// //+" to_char(B.ATU_DTTM, 'DD/MM/YYYY/HH24/MI'),"
				// //changed by balaji to display maximum of Atu
				// +" (SELECT TO_CHAR(B1.ATU_DTTM, 'DD/MM/YYYY/HH24/MI')"
				// +" FROM BERTHING B1 WHERE B1.VV_CD=B.VV_CD AND B1.SHIFT_IND "
				// +" =(SELECT MAX(B2.SHIFT_IND) FROM BERTHING B2"
				// +" WHERE(B2.VV_CD=B.VV_CD))) ATU,"
				// //changed by balaji
				// +" to_char(B.GB_COD_DTTM, 'DD/MM/YYYY/HH24/MI'),"
				// +" to_char(B.GB_COL_DTTM, 'DD/MM/YYYY/HH24/MI'),"
				// +" to_char(B.GB_BCOD_DTTM, 'DD/MM/YYYY/HH24/MI'),"
				// +" to_char(B.GB_BCOL_DTTM, 'DD/MM/YYYY/HH24/MI'),"
				// +" to_char(B.GB_FIRST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'),"
				// +" to_char(B.GB_LAST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'), "
				// +" A.GB_CLOSE_VSL_IND, A.VV_STATUS_IND,A.SCHEME,a.VSL_UNDER_TOW_IND, "
				// // Added by Tirumal, dated 04-12-2007
				// +" B.GANG_NBR, B.HATCH_NBR, B.DELAY_RSN_CD, B.REMARKS "
				// +" FROM VESSEL_CALL A, BERTHING B,VESSEL C "
				// +" WHERE A.VV_CD=B.VV_CD AND"
				// +" A.VV_STATUS_IND != 'CX' AND"
				// +" A.GB_CLOSE_VSL_IND != 'Y' AND"
				// //+" C.VSL_TYPE_CD != 'SC' AND" - updated by swarna on 16-08
				// +" A.VSL_NM= C.VSL_NM AND"
				// +" B.SHIFT_IND= '1' AND"
				// +" A.CREATE_CUST_CD IS NOT NULL " ;
				// if(!StringUtils.isEmpty(atbFromTime)){
				// sql += " AND trunc(B.ATB_DTTM) between to_date('"+atbFromTime+"','ddmmyyyy
				// hh24:mi') and to_date('"+atbToTime+"','ddmmyyyy hh24:mi')";
				// }
				// if(!StringUtils.isEmpty(atuFromTime)){
				// sql += " AND trunc(B.ATU_DTTM) between to_date('"+atuFromTime+"','ddmmyyyy
				// hh24:mi') and to_date('"+atuToTime+"','ddmmyyyy hh24:mi')";
				// }
				// if((!StringUtils.isEmpty(schemdCd))&&(!schemdCd.equals("--Select All--"))){
				// sql += " AND A.SCHEME = '"+schemdCd+"' ";
				// }
				// sql += " ORDER BY A.VV_CD DESC";

				if (StringUtils.isEmpty(atbFromTime) && StringUtils.isEmpty(atuFromTime)
						&& (schemdCd == null || schemdCd.equals("--Select All--"))) {

					sb.append("SELECT A.VV_CD, A.VSL_NM, A.IN_VOY_NBR,A.OUT_VOY_NBR,");
					sb.append(" to_char(B.ATB_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" (SELECT TO_CHAR(B1.ATU_DTTM, 'DD/MM/YYYY/HH24/MI')");
					sb.append(" FROM BERTHING B1 WHERE B1.VV_CD=B.VV_CD AND  B1.SHIFT_IND ");
					sb.append(" =(SELECT MAX(B2.SHIFT_IND) FROM BERTHING B2");
					sb.append(" WHERE(B2.VV_CD=B.VV_CD))) ATU,");
					sb.append(" to_char(B.GB_COD_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_COL_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_BCOD_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_BCOL_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_FIRST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_LAST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'), ");
					sb.append(
							" A.GB_CLOSE_VSL_IND, A.VV_STATUS_IND,CONCAT(A.SCHEME,CASE WHEN A.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(A.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME,a.VSL_UNDER_TOW_IND, ");
					sb.append(" B.GANG_NBR, B.HATCH_NBR, B.DELAY_RSN_CD, B.REMARKS ,A.TERMINAL ");
					sb.append(" FROM VESSEL_CALL A, BERTHING B,VESSEL C ");
					sb.append(" WHERE A.VV_CD=B.VV_CD AND");
					sb.append(" A.VV_STATUS_IND != 'CX' AND");
					sb.append(" A.GB_CLOSE_VSL_IND != 'Y' AND");
					sb.append(" A.VSL_NM= C.VSL_NM AND");
					sb.append(" B.SHIFT_IND= '1' AND");
					sb.append(" A.CREATE_CUST_CD IS NOT NULL ");

					sql = sb.toString();
				} else {
					sb.append("SELECT A.VV_CD, A.VSL_NM, A.IN_VOY_NBR,A.OUT_VOY_NBR,");
					sb.append(" to_char(B.ATB_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" (SELECT TO_CHAR(B1.ATU_DTTM, 'DD/MM/YYYY/HH24/MI')");
					sb.append(" FROM BERTHING B1 WHERE B1.VV_CD=B.VV_CD AND  B1.SHIFT_IND ");
					sb.append(" =(SELECT MAX(B2.SHIFT_IND) FROM BERTHING B2");
					sb.append(" WHERE(B2.VV_CD=B.VV_CD))) ATU,");
					sb.append(" to_char(B.GB_COD_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_COL_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_BCOD_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_BCOL_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_FIRST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_LAST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'), ");
					sb.append(
							" A.GB_CLOSE_VSL_IND, A.VV_STATUS_IND,CONCAT(A.SCHEME,CASE WHEN A.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(A.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME,a.VSL_UNDER_TOW_IND, ");
					sb.append(" B.GANG_NBR, B.HATCH_NBR, B.DELAY_RSN_CD, B.REMARKS ,A.TERMINAL ");
					sb.append(" FROM VESSEL_CALL A, BERTHING B,VESSEL C ");
					sb.append(" WHERE A.VV_CD=B.VV_CD AND");
					sb.append(" A.VV_STATUS_IND != 'CX' AND");
					sb.append(" A.VSL_NM= C.VSL_NM AND");
					sb.append(" B.SHIFT_IND= '1' AND");
					sb.append(" A.CREATE_CUST_CD IS NOT NULL ");

					sql = sb.toString();

					if (!StringUtils.isEmpty(atbFromTime)) {
						sql += " AND trunc(B.ATB_DTTM)  between to_date(:atbFromTime,'ddmmyyyy hh24:mi') and to_date(:atbToTime,'ddmmyyyy hh24:mi')";
						paramMap.put("atbFromTime", atbFromTime);
						paramMap.put("atbToTime", atbToTime);
					}
					if (!StringUtils.isEmpty(atuFromTime)) {
						sql += " AND trunc(B.ATU_DTTM)  between to_date(:atuFromTime,'ddmmyyyy hh24:mi') and to_date(:atuToTime,'ddmmyyyy hh24:mi')";
						paramMap.put("atuFromTime", atuFromTime);
						paramMap.put("atuToTime", atuToTime);

					}
					if ((!StringUtils.isEmpty(schemdCd)) && (!schemdCd.equals("--Select All--"))) {
						sql += " AND A.SCHEME = :schemdCd ";
						paramMap.put("schemdCd", schemdCd);
					}
				}
				sql += " ORDER BY A.VV_CD DESC";

			} else {
				if (!(strvslnm.equalsIgnoreCase(""))) {
					sb.append("SELECT A.VV_CD, A.VSL_NM, A.IN_VOY_NBR,A.OUT_VOY_NBR,");
					sb.append(" to_char(B.ATB_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" (SELECT TO_CHAR(B1.ATU_DTTM, 'DD/MM/YYYY/HH24/MI')");
					sb.append(" FROM BERTHING B1 WHERE B1.VV_CD=B.VV_CD AND  B1.SHIFT_IND ");
					sb.append(" =(SELECT MAX(B2.SHIFT_IND) FROM BERTHING B2");
					sb.append(" WHERE(B2.VV_CD=B.VV_CD))) ATU,");
					sb.append(" to_char(B.GB_COD_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_COL_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_BCOD_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_BCOL_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_FIRST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_LAST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'), ");
					sb.append(
							" A.GB_CLOSE_VSL_IND, A.VV_STATUS_IND, CONCAT(A.SCHEME,CASE WHEN A.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(A.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME,a.VSL_UNDER_TOW_IND, ");
					sb.append(" B.GANG_NBR, B.HATCH_NBR, B.DELAY_RSN_CD, B.REMARKS, A.TERMINAL ");
					sb.append(" FROM VESSEL_CALL A, BERTHING B,VESSEL C ");
					sb.append(" WHERE A.VV_CD=B.VV_CD AND");
					sb.append(" A.VV_STATUS_IND != 'CX' AND");
					sb.append(" A.VSL_NM= C.VSL_NM AND");
					sb.append(" B.SHIFT_IND= '1' AND");
					sb.append(" A.CREATE_CUST_CD IS NOT NULL");
					sb.append(" AND A.VSL_NM= :strvslnm ");
					paramMap.put("strvslnm", strvslnm);

					sql = sb.toString();
					if (!StringUtils.isEmpty(atbFromTime)) {
						sql += "AND trunc(B.ATB_DTTM)  between to_date(:atbFromTime,'ddmmyyyy hh24:mi') and to_date(:atbToTime,'ddmmyyyy hh24:mi')";
						paramMap.put("atbFromTime", atbFromTime);
						paramMap.put("atbToTime", atbToTime);

					}
					if (!StringUtils.isEmpty(atuFromTime)) {
						sql += "AND trunc(B.ATU_DTTM)  between to_date(:atuFromTime,'ddmmyyyy hh24:mi') and to_date(:atuToTime,'ddmmyyyy hh24:mi')";
						paramMap.put("atuFromTime", atuFromTime);
						paramMap.put("atuToTime", atuToTime);
					}
					if ((!StringUtils.isEmpty(schemdCd)) && (!schemdCd.equals("--Select All--"))) {
						sql += " AND A.SCHEME = :schemdCd ";
						paramMap.put("schemdCd", schemdCd);
					}
					sql += "ORDER BY A.VV_CD DESC";
				} else {
					sb.append("SELECT A.VV_CD, A.VSL_NM, A.IN_VOY_NBR,A.OUT_VOY_NBR,");
					sb.append(" to_char(B.ATB_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" (SELECT TO_CHAR(B1.ATU_DTTM, 'DD/MM/YYYY/HH24/MI')");
					sb.append(" FROM BERTHING B1 WHERE B1.VV_CD=B.VV_CD AND  B1.SHIFT_IND ");
					sb.append(" =(SELECT MAX(B2.SHIFT_IND) FROM BERTHING B2");
					sb.append(" WHERE(B2.VV_CD=B.VV_CD))) ATU,");
					sb.append(" to_char(B.GB_COD_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_COL_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_BCOD_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_BCOL_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_FIRST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(" to_char(B.GB_LAST_ACT_DTTM, 'DD/MM/YYYY/HH24/MI'),");
					sb.append(
							" A.GB_CLOSE_VSL_IND, A.VV_STATUS_IND, CONCAT(A.SCHEME,CASE WHEN A.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(A.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, A.VSL_UNDER_TOW_IND, ");
					sb.append(" B.GANG_NBR, B.HATCH_NBR, B.DELAY_RSN_CD, B.REMARKS,A.TERMINAL ");
					sb.append(" FROM VESSEL_CALL A, BERTHING B,VESSEL C ");
					sb.append(" WHERE A.VV_CD=B.VV_CD AND");
					sb.append(" A.VV_STATUS_IND != 'CX' AND");
					sb.append(" A.VSL_NM= C.VSL_NM AND");
					sb.append(" B.SHIFT_IND= '1' AND");
					sb.append(" A.CREATE_CUST_CD IS NOT NULL");
					sb.append(" AND A.VSL_NM= :strvslnm ");
					paramMap.put("strvslnm", strvslnm);
					sql = sb.toString();
					if (!StringUtils.isEmpty(atbFromTime)) {
						sql += "AND trunc(B.ATB_DTTM)  between to_date(:atbFromTime,'ddmmyyyy hh24:mi') and to_date(:atbToTime,'ddmmyyyy hh24:mi')";
						paramMap.put("atbFromTime", atbFromTime);
						paramMap.put("atbToTime", atbToTime);
					}
					if (!StringUtils.isEmpty(atuFromTime)) {
						sql += "AND trunc(B.ATU_DTTM)  between to_date(:atuFromTime,'ddmmyyyy hh24:mi') and to_date(:atuToTime,'ddmmyyyy hh24:mi')";
						paramMap.put("atuFromTime", atuFromTime);
						paramMap.put("atuToTime", atuToTime);
					}
					if ((!StringUtils.isEmpty(schemdCd)) && (!schemdCd.equals("--Select All--"))) {
						sql += " AND A.SCHEME = :schemdCd ";

						paramMap.put("schemdCd", schemdCd);
					}
					sql += "ORDER BY A.VV_CD DESC";
				}
			}

			String sql1 = sql;

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql1 + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}
			if (criteria.isPaginated()) {
				sql1 = CommonUtil.getPaginatedSql(sql1, criteria.getStart(), criteria.getLimit());
			}

			log.info(" *** getVesselActList SQL 1*****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);

			paramMap = new HashMap<String, Object>();
			log.info(" *** getVesselActList SQL 2*****" + delaySql + " paramMap " + paramMap.toString());
			delayRs = namedParameterJdbcTemplate.queryForRowSet(delaySql, paramMap);

			List<String> delayOptionList = new ArrayList<String>();

			while (delayRs.next()) {
				String delayOptionString = delayRs.getString(1) + "-" + delayRs.getString(2);
				delayOptionList.add(delayOptionString);
			}
			// END Code added by Tirumal to implement VesselActivity CR, dated 04-12-2007

			while (rs.next()) {
				VesselActValueObject vesselActValueObject = new VesselActValueObject();
				String vvcd = CommonUtility.deNull(rs.getString(1));
				String vslnm = CommonUtility.deNull(rs.getString(2));
				String invoynbr = CommonUtility.deNull(rs.getString(3));
				String outvoynbr = CommonUtility.deNull(rs.getString(4));
				String atbdttm = CommonUtility.deNull(rs.getString(5));
				String atudttm = CommonUtility.deNull(rs.getString(6));
				String coddttm = CommonUtility.deNull(rs.getString(7));
				String coldttm = CommonUtility.deNull(rs.getString(8));
				String bcoddttm = CommonUtility.deNull(rs.getString(9));
				String bcoldttm = CommonUtility.deNull(rs.getString(10));
				String firstactdttm = CommonUtility.deNull(rs.getString(11));
				String lastactdttm = CommonUtility.deNull(rs.getString(12));
				String gbclosevslind = CommonUtility.deNull(rs.getString(13));
				String vvstatusind = CommonUtility.deNull(rs.getString(14));
				String scheme = CommonUtility.deNull(rs.getString(15));
				String vesselUnderTowed = CommonUtility.deNull(rs.getString(16));

				// START Code added by Tirumal to implement VesselActivity CR, dated 04-12-2007
				String strGangs = CommonUtility.deNull(rs.getString(17));
				String strHatches = CommonUtility.deNull(rs.getString(18));
				String strDelayReason = CommonUtility.deNull(rs.getString(19));
				String strRemarks = CommonUtility.deNull(rs.getString(20));
				String terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				// END Code added by Tirumal to implement VesselActivity CR, dated 04-12-2007

				vesselActValueObject.setVarNbr(vvcd);
				vesselActValueObject.setVslNm(vslnm);
				vesselActValueObject.setInVoyNbr(invoynbr);
				vesselActValueObject.setOutVoyNbr(outvoynbr);
				vesselActValueObject.setAtbDttm(atbdttm);
				vesselActValueObject.setAtuDttm(atudttm);
				vesselActValueObject.setCodDttm(coddttm);
				vesselActValueObject.setColDttm(coldttm);
				vesselActValueObject.setBcodDttm(bcoddttm);
				vesselActValueObject.setBcolDttm(bcoldttm);
				vesselActValueObject.setFirstActDttm(firstactdttm);
				vesselActValueObject.setLastActDttm(lastactdttm);
				vesselActValueObject.setGbCloseVslInd(gbclosevslind);
				vesselActValueObject.setVvStatusInd(vvstatusind);
				vesselActValueObject.setScheme(scheme);
				vesselActValueObject.setTerminal(terminal);
				vesselActValueObject.setLineTowedVessel(vesselUnderTowed);

				// START Code added by Tirumal to implement VesselActivity CR, dated 04-12-2007
				vesselActValueObject.setNoOfGangsSupplied(strGangs);
				vesselActValueObject.setNoOfWorkableHatches(strHatches);
				vesselActValueObject.setReasonForDelay(strDelayReason);
				vesselActValueObject.setRemarks(strRemarks);
				vesselActValueObject.setTempList(delayOptionList);
				// END Code added by Tirumal to implement VesselActivity CR, dated 04-12-2007

				vesselactlist.add(vesselActValueObject);
				topsModel.put((Serializable) vesselActValueObject);

			}

			tableData.setListData(topsModel);
			tableResult.setData(tableData);

			log.info("END: *** getVesselActList Result *****" + vesselactlist.toString());
			return tableResult;
		} catch (NullPointerException e) {
			log.error("Exception: getVesselActList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getVesselActList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselActList  DAO  END");
		}

	}

	@Override
	public List<VesselActValueObject> getVesselList(String name) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";

		List<VesselActValueObject> vessel = new ArrayList<VesselActValueObject>();
		try {
			log.info("START: getVesselList  DAO  Start Obj ");

			sql = "SELECT DISTINCT(A.VSL_NM) FROM  VESSEL_CALL A WHERE A.CREATE_CUST_CD IS NOT NULL and A.VSL_NM LIKE upper(:vsl_nm) ORDER BY A.VSL_NM";

			paramMap.put("vsl_nm", name + "%");

			log.info(" *** getVesselList SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			log.info("---executed the sql stmt---");
			while (rs.next()) {
				VesselActValueObject vesselActValueObject = new VesselActValueObject();
				String vslnm = CommonUtility.deNull(rs.getString(1));
				vesselActValueObject.setVslNm(vslnm);
				vessel.add(vesselActValueObject);
			}

			log.info(" returning the List  ");

			log.info("END: *** getVesselList Result *****" + vessel.size());
			return vessel;
		} catch (NullPointerException e) {
			log.error("Exception: getVesselList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getVesselList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselList  DAO  END");
		}
	}

	@Override
	public Map<String, String> getVesselDataForKmf(String vvCd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, String> data = new HashMap<String, String>();

		try {
			log.info("START: getVesselDataForKmf  DAO  Start vvCd:" + CommonUtility.deNull(vvCd));
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT ");
			sb.append("	vc.TERMINAL AS TERMINAL, ");
			sb.append("	TO_CHAR(B.GB_FIRST_ACT_DTTM, 'DD-MM-YYYY HH24:MI')  AS FAT , ");
			sb.append("	TO_CHAR(B.GB_LAST_ACT_DTTM, 'DD-MM-YYYY HH24:MI') AS LAT ");
			sb.append("FROM ");
			sb.append("	TOPS.VESSEL_CALL vc ");
			sb.append("LEFT JOIN TOPS.BERTHING b ON ");
			sb.append("	VC.VV_CD = b.VV_CD ");
			sb.append("WHERE ");
			sb.append("	VC.VV_CD =:vvCd ");
			sb.append("	AND b.SHIFT_IND =( ");
			sb.append("	SELECT ");
			sb.append("		MAX(SHIFT_IND) ");
			sb.append("	FROM ");
			sb.append("		TOPS.BERTHING ");
			sb.append("	WHERE ");
			sb.append("		VV_CD=:vvCd  ) ");

			paramMap.put("vvCd", vvCd);

			log.info(" *** getVesselDataForKmf SQL *****" + sb.toString() + " paramMap " + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				data.put("terminal", rs.getString("TERMINAL"));
				data.put("fat", rs.getString("FAT"));
				data.put("lat", rs.getString("LAT"));
			}

			log.info("END: *** getVesselDataForKmf Result *****" + data);
		} catch (NullPointerException e) {
			log.error("Exception: getVesselDataForKmf ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getVesselDataForKmf ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselDataForKmf  DAO  END");
		}
		return data;
	}

	@Override
	public Timestamp getSysDate() throws BusinessException {

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer aSQL = new StringBuffer();
		Timestamp dttm = null;
		try {
			aSQL.append("SELECT SYSDATE FROM DUAL");
			log.info(" *** getSysDate SQL *****" + aSQL.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(aSQL.toString(), paramMap);
			if (rs.next()) {
				dttm = rs.getTimestamp("SYSDATE");
			}
			log.info("END: *** getSysDate Result dttm:*****" + dttm);
			return dttm;
		} catch (NullPointerException e) {
			log.error("Exception: getSysDate ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getSysDate ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSysDate  DAO  END");
		}
	}
	
		
	
	@Override
	public List<OSDReviewObject> getOsdReviewList(String vvCd) throws BusinessException {
	 
		StringBuffer sb = new StringBuffer();
	    Map<String, Object> paramMap = new HashMap<>();
	    SqlRowSet rs = null;
	 
	    List<OSDReviewObject> list = new ArrayList<>();
	 
	    try {
	    	
	    	log.info("start getOsdReviewList-------- vvcd: "+ vvCd);
	    	
	        sb.setLength(0);
	        sb.append("select ");
	        sb.append("    r.osd_review_id, ");
	        sb.append("    r.vv_cd as vv_cd, ");
	        sb.append("    r.osd_review_option, ");
	        sb.append("    r.late_arrival_review_option, ");
	        sb.append("    r.query_option, ");
	        sb.append("    r.query_remarks, ");
	        sb.append("    r.query_dttm, ");
	        sb.append("    r.submit_status, ");
	        sb.append("    r.submit_user_id, ");
	        sb.append("    r.submit_dttm, ");
	        sb.append("    r.approve_user_id, ");
	        sb.append("    r.approve_dttm, ");
	        sb.append("    r.create_dttm, ");
	        sb.append("    r.create_user_id, ");
	        sb.append("    e.exemption_type e_exemption_type, ");
	        sb.append("    e.exemption_code, ");
	        sb.append("    e.exemption_mins, ");
	        sb.append("    u.actual_file_nm, ");
	        sb.append("    u.assigned_file_nm, ");
	        sb.append("    u.exemption_type U_exemption_type ");
	        sb.append("from osd_review r ");
	        sb.append("left join osd_review_exemption e on r.osd_review_id = e.osd_review_id ");
	        sb.append("left join osd_review_file_upload u on u.osd_review_id = r.osd_review_id  and u.exemption_type = e.exemption_type ");
	        sb.append(" where r.vv_cd = :vvcd ");
	 
	        paramMap.put("vvcd", vvCd);
	 
	        log.info("VIEWOSD SQL: " + sb.toString() + " | param=" + paramMap);
	 
	        rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
	 
	        while (rs.next()) {
	 
	        	OSDReviewObject vo = new OSDReviewObject();
	 
	            vo.setOsdReviewId(rs.getLong("osd_review_id"));
	            vo.setVvcd(CommonUtility.deNull(rs.getString("vv_cd")));
	            vo.setOsdReviewOption(CommonUtility.deNull(rs.getString("osd_review_option")));
	            vo.setLateArrivalReviewOption(CommonUtility.deNull(rs.getString("late_arrival_review_option")));
	            vo.setQueryOption(CommonUtility.deNull(rs.getString("query_option")));
	            vo.setQueryRemarks(CommonUtility.deNull(rs.getString("query_remarks")));
	            vo.setQueryDttm(CommonUtility.deNull(rs.getString("query_dttm")));
	            vo.setSubmitStatus(CommonUtility.deNull(rs.getString("submit_status")));
	            vo.setSubmitUserId(CommonUtility.deNull(rs.getString("submit_user_id")));
	            vo.setSubmitDttm(CommonUtility.deNull(rs.getString("submit_dttm")));
	            vo.setApproveUserId(CommonUtility.deNull(rs.getString("approve_user_id")));
	            vo.setApproveDttm(CommonUtility.deNull(rs.getString("approve_dttm")));
	            vo.setCreateDttm(CommonUtility.deNull(rs.getString("create_dttm")));
	            vo.setCreateUserId(CommonUtility.deNull(rs.getString("create_user_id")));
	 
	            vo.setExemptionType(CommonUtility.deNull(rs.getString("e_exemption_type")));
	            vo.setExemptionCode(CommonUtility.deNull(rs.getString("exemption_code")));
	            vo.setExemptionMins(CommonUtility.deNull(rs.getString("exemption_mins")));
	 
	            vo.setActualFileName(CommonUtility.deNull(rs.getString("actual_file_nm")));
	            vo.setAssignedFileName(CommonUtility.deNull(rs.getString("assigned_file_nm")));
	            vo.setFileExemptionType(CommonUtility.deNull(rs.getString("u_exemption_type")));
	 
	            list.add(vo);
	        }
	 
	        log.info("getOsdReviewList total rows = " + list.size());
	        return list;
	 
	    } catch (Exception e) {
	        log.error("Error in getOsdReviewList()", e);
	        throw new BusinessException("M4201");
	    }
	    finally {
	    	log.info("End of getOsdReviewList DAO");
	    }
	 
	}
		
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateOsdReview(
	        String vvcd,
	        String osdExemptionCodeList,
	        String lateArrivalExemptionList,
	        String osdReviewOption,
	        String lateArrivalReviewOption,
	        String useraccount,
	        String submitInd,
	        String actualOsdFilesName,
	        String actuallateFilesName,
	        Map<String, String> uploadedFileMap
	) throws BusinessException {
	 
	    SqlRowSet rs;
	    Map<String, Object> param = new HashMap<>();
	    StringBuffer sb = new StringBuffer();
	    Long osdReviewId = null;
	    String status = null;
	 
	    // ADD: track old files for safe deletion after commit
	    List<String> oldAssignedFiles = new ArrayList<>();
	 
	    try {
	        log.info("START updateOsdReview | vvcd=" + vvcd + " submitInd=" + submitInd);
	 
	        /* ------------------------------------------------------------
	         * 1. CHECK EXISTING DRAFT
	         * ------------------------------------------------------------ */
	        sb.setLength(0);
	        sb.append("SELECT OSD_REVIEW_ID, SUBMIT_STATUS ");
	        sb.append("FROM TOPS.OSD_REVIEW ");
	        sb.append("WHERE VV_CD = :vvcd ");
	        param.clear();
	        param.put("vvcd", vvcd);
	 
	        rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), param);
	 
	        boolean draftExists = false;
	        boolean isSubmit = "YES".equalsIgnoreCase(submitInd);
	        String submitStatus = isSubmit ? "S" : "D";
	 
	        if (rs.next()) {
	            draftExists = true;
	            osdReviewId = rs.getLong("OSD_REVIEW_ID");
	            status = rs.getString("SUBMIT_STATUS");
	 
	            if ("S".equalsIgnoreCase(status)) {
	                throw new BusinessException("Review already submitted for this vessel voyage");
	            }
	            if ("A".equalsIgnoreCase(status)) {
	                throw new BusinessException("Review already approved for this vessel voyage");
	            }
	        }
	 
	        /* ------------------------------------------------------------
	         * 2. INSERT OR UPDATE HEADER
	         * ------------------------------------------------------------ */
	        if (!draftExists) {
	 
	            sb.setLength(0);
	            param.clear();
	            sb.append("INSERT INTO TOPS.OSD_REVIEW ( ");
	            sb.append("VV_CD, OSD_REVIEW_OPTION, LATE_ARRIVAL_REVIEW_OPTION, ");
	            sb.append("SUBMIT_STATUS, SUBMIT_USER_ID, SUBMIT_DTTM, ");
	            sb.append("CREATE_USER_ID, CREATE_DTTM, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM ");
	            sb.append(") VALUES ( ");
	            sb.append(":vvcd, :osdReviewOption, :lateArrivalReviewOption, ");
	            sb.append(":submitStatus, ");
	            sb.append(isSubmit ? ":useraccount, SYSDATE, " : "NULL, NULL, ");
	            sb.append(":useraccount, SYSDATE, :useraccount, SYSDATE )");
	 
	            param.put("vvcd", vvcd);
	            param.put("osdReviewOption", osdReviewOption);
	            param.put("lateArrivalReviewOption", lateArrivalReviewOption);
	            param.put("submitStatus", submitStatus);
	            param.put("useraccount", useraccount);
	 
	            namedParameterJdbcTemplate.update(sb.toString(), param);
	 
	            sb.setLength(0);
	            param.clear();
	            sb.append("SELECT MAX(OSD_REVIEW_ID) RID FROM TOPS.OSD_REVIEW WHERE VV_CD = :vvcd");
	            param.put("vvcd", vvcd);
	 
	            rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), param);
	            if (rs.next()) {
	                osdReviewId = rs.getLong("RID");
	            }
	 
	        } else {
	 
	            sb.setLength(0);
	            param.clear();
	            sb.append("UPDATE TOPS.OSD_REVIEW SET ");
	            sb.append(" OSD_REVIEW_OPTION = :osdReviewOption, ");
	            sb.append(" LATE_ARRIVAL_REVIEW_OPTION = :lateArrivalReviewOption, ");
	            sb.append(" SUBMIT_STATUS = :submitStatus, ");
	            sb.append(" LAST_MODIFY_USER_ID = :useraccount, ");
	            sb.append(" LAST_MODIFY_DTTM = SYSDATE ");
	 
	            if (isSubmit) {
	                sb.append(", SUBMIT_USER_ID = :useraccount, SUBMIT_DTTM = SYSDATE ");
	            }
	 
	            sb.append("WHERE OSD_REVIEW_ID = :rid");
	 
	            param.put("rid", osdReviewId);
	            param.put("osdReviewOption", osdReviewOption);
	            param.put("lateArrivalReviewOption", lateArrivalReviewOption);
	            param.put("submitStatus", submitStatus);
	            param.put("useraccount", useraccount);
	 
	            namedParameterJdbcTemplate.update(sb.toString(), param);
	        }
	 
	        /* ------------------------------------------------------------
	         * 3. DELETE OLD EXEMPTIONS & FILES
	         * ------------------------------------------------------------ */
	 
	        // ADD: capture existing assigned file names BEFORE deleting DB rows
	        sb.setLength(0);
	        param.clear();
	        sb.append("SELECT ASSIGNED_FILE_NM ");
	        sb.append("FROM TOPS.OSD_REVIEW_FILE_UPLOAD ");
	        sb.append("WHERE OSD_REVIEW_ID = :rid");
	        param.put("rid", osdReviewId);
	 
	        SqlRowSet oldFileRs =
	                namedParameterJdbcTemplate.queryForRowSet(sb.toString(), param);
	 
	        while (oldFileRs.next()) {
	            oldAssignedFiles.add(oldFileRs.getString("ASSIGNED_FILE_NM"));
	        }
	 
	        sb.setLength(0);
	        param.clear();
	        sb.append("DELETE FROM TOPS.OSD_REVIEW_EXEMPTION WHERE OSD_REVIEW_ID = :rid");
	        param.put("rid", osdReviewId);
	        namedParameterJdbcTemplate.update(sb.toString(), param);
	 
	        sb.setLength(0);
	        param.clear();
	        sb.append("DELETE FROM TOPS.OSD_REVIEW_FILE_UPLOAD WHERE OSD_REVIEW_ID = :rid");
	        param.put("rid", osdReviewId);
	        namedParameterJdbcTemplate.update(sb.toString(), param);
	 
	        /* ------------------------------------------------------------
	         * 4. INSERT OSD EXEMPTIONS
	         * ------------------------------------------------------------ */
	        if (osdExemptionCodeList != null && !osdExemptionCodeList.trim().isEmpty()) {
	            for (String item : osdExemptionCodeList.split(",")) {
	                if (item.trim().isEmpty()) continue;
	 
	                String[] p = item.split("-");
	                String code = p[0];
	                Integer mins = (p.length > 1) ? Integer.valueOf(p[1]) : null;
	 
	                sb.setLength(0);
	                param.clear();
	                sb.append("INSERT INTO TOPS.OSD_REVIEW_EXEMPTION ( ");
	                sb.append("OSD_REVIEW_ID, EXEMPTION_TYPE, EXEMPTION_CODE, EXEMPTION_MINS, ");
	                sb.append("CREATE_USER_ID, CREATE_DTTM ");
	                sb.append(") VALUES ( ");
	                sb.append(":rid, 'O', :code, :mins, :useraccount, SYSDATE )");
	 
	                param.put("rid", osdReviewId);
	                param.put("code", code);
	                param.put("mins", mins);
	                param.put("useraccount", useraccount);
	 
	                namedParameterJdbcTemplate.update(sb.toString(), param);
	            }
	        }
	 
	        /* ------------------------------------------------------------
	         * 5. INSERT LATE ARRIVAL EXEMPTIONS
	         * ------------------------------------------------------------ */
	        if (lateArrivalExemptionList != null && !lateArrivalExemptionList.trim().isEmpty()) {
	            for (String item : lateArrivalExemptionList.split(",")) {
	                if (item.trim().isEmpty()) continue;
	 
	                String[] p = item.split("-");
	                String code = p[0];
	                Integer mins = (p.length > 1) ? Integer.valueOf(p[1]) : null;
	 
	                sb.setLength(0);
	                param.clear();
	                sb.append("INSERT INTO TOPS.OSD_REVIEW_EXEMPTION ( ");
	                sb.append("OSD_REVIEW_ID, EXEMPTION_TYPE, EXEMPTION_CODE, EXEMPTION_MINS, ");
	                sb.append("CREATE_USER_ID, CREATE_DTTM ");
	                sb.append(") VALUES ( ");
	                sb.append(":rid, 'L', :code, :mins, :useraccount, SYSDATE )");
	 
	                param.put("rid", osdReviewId);
	                param.put("code", code);
	                param.put("mins", mins);
	                param.put("useraccount", useraccount);
	 
	                namedParameterJdbcTemplate.update(sb.toString(), param);
	            }
	        }
	 
	        /* ------------------------------------------------------------
	         * 6. INSERT OSD FILES
	         * ------------------------------------------------------------ */
	        if (actualOsdFilesName != null && !actualOsdFilesName.trim().isEmpty()) {
	            for (String actual : actualOsdFilesName.split(",")) {
	                actual = actual.trim();
	                if (actual.isEmpty()) continue;
	 
	                String assigned = uploadedFileMap.get(actual);
	                if (assigned == null) continue;
	 
	                sb.setLength(0);
	                param.clear();
	                sb.append("INSERT INTO TOPS.OSD_REVIEW_FILE_UPLOAD ( ");
	                sb.append("OSD_REVIEW_ID, EXEMPTION_TYPE, ACTUAL_FILE_NM, ASSIGNED_FILE_NM, ");
	                sb.append("CREATE_USER_ID, CREATE_DTTM ");
	                sb.append(") VALUES ( ");
	                sb.append(":rid, 'O', :actual, :assigned, :useraccount, SYSDATE )");
	 
	                param.put("rid", osdReviewId);
	                param.put("actual", actual);
	                param.put("assigned", assigned);
	                param.put("useraccount", useraccount);
	 
	                namedParameterJdbcTemplate.update(sb.toString(), param);
	            }
	        }
	 
	        /* ------------------------------------------------------------
	         * 7. INSERT LATE ARRIVAL FILES
	         * ------------------------------------------------------------ */
	        if (actuallateFilesName != null && !actuallateFilesName.trim().isEmpty()) {
	            for (String actual : actuallateFilesName.split(",")) {
	                actual = actual.trim();
	                if (actual.isEmpty()) continue;
	 
	                String assigned = uploadedFileMap.get(actual);
	                if (assigned == null) continue;
	 
	                sb.setLength(0);
	                param.clear();
	                sb.append("INSERT INTO TOPS.OSD_REVIEW_FILE_UPLOAD ( ");
	                sb.append("OSD_REVIEW_ID, EXEMPTION_TYPE, ACTUAL_FILE_NM, ASSIGNED_FILE_NM, ");
	                sb.append("CREATE_USER_ID, CREATE_DTTM ");
	                sb.append(") VALUES ( ");
	                sb.append(":rid, 'L', :actual, :assigned, :useraccount, SYSDATE )");
	 
	                param.put("rid", osdReviewId);
	                param.put("actual", actual);
	                param.put("assigned", assigned);
	                param.put("useraccount", useraccount);
	 
	                namedParameterJdbcTemplate.update(sb.toString(), param);
	            }
	        }
	 
	        // ADD: delete old physical files AFTER successful DB operations
	        for (String oldFile : oldAssignedFiles) {
	            if (oldFile != null && !oldFile.trim().isEmpty()) {
	                try {
	                    Files.deleteIfExists(Paths.get(fileUploadDirectory, oldFile));
	                } catch (Exception ex) {
	                    log.warn("Failed to delete old file: " + oldFile, ex);
	                }
	            }
	        }
	 
	        log.info("END updateOsdReview | OSD_REVIEW_ID=" + osdReviewId);
	        return osdReviewId.intValue();
	 
	    } catch (Exception e) {
	 
	        // ADD: rollback cleanup – delete newly uploaded files
	        if (uploadedFileMap != null) {
	            for (String newFile : uploadedFileMap.values()) {
	                try {
	                    Files.deleteIfExists(Paths.get(fileUploadDirectory, newFile));
	                } catch (Exception ignore) {
	                }
	            }
	        }
	 
	        log.error("Exception in updateOsdReview", e);
	        throw new BusinessException("M4201");
	    }
	}
	
	@Override
	 public Long queryOsd(String vesselCode,
              boolean isLateArrivalQuery,
              boolean isOsdQuery,
              String queryRemarks,
              String userAccount) throws BusinessException {
		 SqlRowSet rowSet = null;
	     Map<String, Object> paramMap = new HashMap<>();
	     StringBuilder sql = new StringBuilder();
	     Long reviewId = null;
	
	     try {
	         log.info("START: queryOsd | vesselCode=" + vesselCode);
	         sql.setLength(0);
	         paramMap.clear();
	
	         sql.append("SELECT OSD_REVIEW_ID ");
	         sql.append("  FROM TOPS.OSD_REVIEW ");
	         sql.append(" WHERE VV_CD = :vesselCode ");
	
	         paramMap.put("vesselCode", vesselCode);
	
	         log.info("SQL(fetchReviewId): " + sql + " param=" + paramMap);
	         rowSet = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
	
	         if (!rowSet.next()) {
	             throw new BusinessException("NO_REVIEW_FOUND");
	         }
	
	         reviewId = rowSet.getLong("OSD_REVIEW_ID");
	
	         String queryOption = "N";
	         if (isLateArrivalQuery && isOsdQuery) queryOption = "B";
	         else if (isLateArrivalQuery) queryOption = "L";
	         else if (isOsdQuery) queryOption = "O";
	
	         sql.setLength(0);
	         paramMap.clear();
	
	         sql.append("UPDATE TOPS.OSD_REVIEW SET ");
	         sql.append("   QUERY_OPTION = :queryOption, ");
	         sql.append("   QUERY_REMARKS = :queryRemarks, ");
	         sql.append("   QUERY_USER_ID = :userAccount, ");
	         sql.append("   QUERY_DTTM = SYSDATE, ");
	         sql.append("   SUBMIT_STATUS = 'Q', ");
	         sql.append("   LAST_MODIFY_USER_ID = :userAccount, ");
	         sql.append("   LAST_MODIFY_DTTM = SYSDATE ");
	         sql.append(" WHERE OSD_REVIEW_ID = :reviewId");
	
	         paramMap.put("queryOption", queryOption);
	         paramMap.put("queryRemarks", queryRemarks);
	         paramMap.put("userAccount", userAccount);
	         paramMap.put("reviewId", reviewId);
	
	         log.info("SQL(updateQuery): " + sql + " param=" + paramMap);
	         namedParameterJdbcTemplate.update(sql.toString(), paramMap);
	
	         log.info("END: queryOsd | reviewId=" + reviewId);
	         return reviewId;
	
	     } catch (BusinessException be) {
	         throw be;
	     } catch (Exception ex) {
	         log.error("Exception in queryOsd DAO", ex);
	         throw new BusinessException("M4201");
	     }
	}
	
	@Override
	public List<OSDExemptionClauses> getOsdExemptionList() throws BusinessException {
	 
	    List<OSDExemptionClauses> list = new ArrayList<>();
	    SqlRowSet rs = null;
	    Map<String, Object> paramMap = new HashMap<>();
	    StringBuffer sb = new StringBuffer();
	 
	    try {
	        sb.setLength(0);
	        paramMap.clear();
			
	        sb.append("SELECT misc_type_cd, misc_type_nm, remarks ");
	        sb.append(" FROM system_config ");
	        sb.append(" WHERE CAT_CD = 'OSD_EXEMPTION_CLAUSES' order by misc_type_cd ");
	 
	        log.info("SQL(getOsdExemptionList): " + sb.toString());
	 
	        rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			
			log.info("Result Set : "+rs.toString());
	 
	        while (rs.next()) {
	        	OSDExemptionClauses vo = new OSDExemptionClauses();
	 
	            vo.setMiscTypeCd(CommonUtility.deNull(rs.getString("misc_type_cd")));
	            vo.setMiscTypeNm(CommonUtility.deNull(rs.getString("misc_type_nm")));
	            vo.setRemarks(CommonUtility.deNull(rs.getString("remarks")));
	 
	            list.add(vo);
	        }
	 
	        return list;
	 
	    } catch (Exception e) {
	        log.error("Error in getOsdExemptionList()", e);
	        throw new BusinessException("M4201");
	    }
	}
	@Override
	public List<OSDExemptionClauses> getLateArrivalWaiverList() throws BusinessException {

		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<OSDExemptionClauses> waivervector = new ArrayList<>();
		try {

			log.info("START: getLateArrivalWaiverList  DAO  Start Obj");

			String sql = "SELECT WAIVER_CD,WAIVER_NM FROM  WAIVER_CODE WHERE WAIVER_STATUS='A' ORDER BY WAIVER_CD";
			
			try {

				log.info(" *** getLateArrivalWaiverList SQL *****" + sql + " paramMap " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				while (rs.next()) {
					
					OSDExemptionClauses wl = new OSDExemptionClauses();
					
					wl.setMiscTypeCd(CommonUtility.deNull(rs.getString(1)));
					wl.setMiscTypeNm(CommonUtility.deNull(rs.getString(2)));
					waivervector.add(wl);
				}

			} catch (Exception e) {
				log.error("Exception: getLateArrivalWaiverList ", e);
				throw new BusinessException("M1007");
			}

			log.info("END: *** getLateArrivalWaiverList Result *****" + waivervector.toString());
			return waivervector;
		} catch (BusinessException e) {
			log.error("Exception: getLateArrivalWaiverList ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: getLateArrivalWaiverList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getLateArrivalWaiverList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getLateArrivalWaiverList  DAO  END");
		}

	}
	public Map<String, String> uploadOsdFiles(MultipartHttpServletRequest request)
	        throws BusinessException,IOException {
		
		 Map<String, String> fileNames = new HashMap<>();
		 
		 try {
			log.info("Start ----- uploadOsdFiles ----- ");
		    Iterator<String> itr = request.getFileNames();
		    
		    
		 
		    while (itr.hasNext()) {
		        MultipartFile mpf = request.getFile(itr.next());
		        if (mpf == null || mpf.isEmpty()) {
		            continue;
		        }
		        String actualName = mpf.getOriginalFilename();
		        String extension = actualName.substring(actualName.lastIndexOf("."));
		        String assignedName = UUID.randomUUID().toString() + extension;
		        
		        log.info("assignedName: " +assignedName + " actualName: "+ actualName);
		 
		        Path dir = Paths.get(fileUploadDirectory);
		        if (!Files.exists(dir)) {
		            Files.createDirectories(dir);
		        }
		 
		        mpf.transferTo(new File(fileUploadDirectory + "/" + assignedName));
		
		        fileNames.put(actualName, assignedName);
		        
		        
		    }
		    
		    log.info("fileName:" + fileNames.toString() );
		    
		    log.info("---- End Of uploadOsdFiles In try----");
		    return fileNames;
		 }
		 catch(Exception e) {
			 log.info("---- Error Occured ----");
			 log.info(e.getMessage());
			 return fileNames;
		 }finally {
			 log.info("End of uploadOsdFiles in Finally");
		 }	 
	}
	@Override
	public Map<String, String> getLatestOsdFile(String vvcd,String actualFileName) throws BusinessException {
	 
	    SqlRowSet rs;
	    StringBuffer sb = new StringBuffer();
	    Map<String, Object> param = new HashMap<>();
	    Map<String, String> resultMap = new HashMap<>();
	 
	    Long osdReviewId = null;
	 
	    try {
	        log.info("START getLatestOsdFile | vvcd=" + vvcd + ", file=" + actualFileName);
	 
	        sb.setLength(0);
	        param.clear();
	 
	        sb.append("SELECT OSD_REVIEW_ID ");
	        sb.append(" FROM TOPS.OSD_REVIEW ");
	        sb.append(" WHERE VV_CD = :vvcd ");
	        sb.append(" ORDER BY CREATE_DTTM DESC");
	 
	        param.put("vvcd", vvcd);
	 
	        rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), param);
	 
	        if (!rs.next()) {
	            log.error("No OSD_REVIEW found for vvcd=" + vvcd);
	            throw new BusinessException("OSD_REVIEW_NOT_FOUND");
	        }
	 
	        osdReviewId = rs.getLong("OSD_REVIEW_ID");
	        log.info("Latest OSD_REVIEW_ID=" + osdReviewId);

	        sb.setLength(0);
	        param.clear();
	 
	        sb.append("SELECT ACTUAL_FILE_NM, ASSIGNED_FILE_NM ");
	        sb.append(" FROM TOPS.OSD_REVIEW_FILE_UPLOAD ");
	        sb.append(" WHERE OSD_REVIEW_ID = :reviewid ");
	        sb.append(" AND ACTUAL_FILE_NM = :actualFileName ");
	        sb.append(" ORDER BY CREATE_DTTM DESC");
	 
	        param.put("reviewid", osdReviewId);
	        param.put("actualFileName", actualFileName);
	 
	        rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), param);
	 
	        if (!rs.next()) {
	            log.error("File not found for reviewId=" + osdReviewId
	                    + ", actualFileName=" + actualFileName);
	            throw new BusinessException("FILE_NOT_FOUND");
	        }
	 
	        String actual = rs.getString("ACTUAL_FILE_NM");
	        String assigned = rs.getString("ASSIGNED_FILE_NM");
	 
	        resultMap.put(actual, assigned);
	 
	        log.info("END getLatestOsdFile | actual=" + actual + ", assigned=" + assigned);
	        return resultMap;
	 
	    } catch (BusinessException be) {
	        log.error("BusinessException in getLatestOsdFile", be);
	        throw be;
	    } catch (Exception e) {
	        log.error("Exception in getLatestOsdFile", e);
	        throw new BusinessException("M4201");
	    }
	}
	
	@Override
	public boolean sendMessage(IMessageValueObject mVO) throws BusinessException {
		// Is Email
		try {
			log.info("START: sendMessage mVO: " + mVO);
			if (mVO instanceof EmailValueObject) {
				EmailValueObject emailVO = (EmailValueObject) mVO;
				Email emailObj = new Email();
				emailObj.setFrom(emailVO.getSenderAddress());
				emailObj.setFromName(emailVO.getSenderAddress());
				emailObj.setToList(Arrays.asList(emailVO.getRecipientAddress()));
				emailObj.setSubject(emailVO.getSubject());
				emailObj.setContent(emailVO.getMessage());
				emailObj.setEmailSvcUrl(commonServiceUrl);
				emailObj.setContentType("text/html");
				log.info("***emailObj*******" + emailObj.toString());
				CommonUtil.sendEmail(emailObj);
			}
		} catch (NullPointerException e) {
			log.info("Exception: sendMessage ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: sendMessage ", e);
			throw new BusinessException("M4201");
		}

		log.info("END: sendMessage Result: true");

		return true;
	}
	
	@Override
	public boolean schemeAndVesselIndicator(String vvcd) throws BusinessException { 
		
		SqlRowSet rs;
	    StringBuffer sb = new StringBuffer();
	    Map<String, Object> paramMap = new HashMap<>();
	    boolean indicator = false;
	 
	    try {
	    	log.info("------- Start schemeAndVesselIndicator------ Vvcd :" +vvcd);
	    	sb.append("SELECT * FROM (SELECT ");
	    	sb.append(" V.VV_CD, ");
	    	sb.append(" V.VSL_NM, ");
	    	sb.append(" V.SCHEME, ");
	    	sb.append(" (CASE ");
	    	sb.append("   WHEN NVL(V.BUS_TYPE_CD, 'N') = 'RMC' THEN 'BC' ");
	    	sb.append("   ELSE ");
	    	sb.append("     (CASE V.SCHEME ");
	    	sb.append("        WHEN 'JWP' THEN 'J1 Basin' ");
	    	sb.append("        WHEN 'JCL' THEN 'LCT' ");
	    	sb.append("        WHEN 'JOL' THEN 'GC' ");
	    	sb.append("        WHEN 'JON' THEN 'GC' ");
	    	sb.append("        ELSE ");
	    	sb.append("          DECODE( ");
	    	sb.append("            V.TERMINAL, ");
	    	sb.append("            'CT', ");
	    	sb.append("              DECODE(NVL(COMBI_GC_OPS_IND, 'N'), 'Y', 'MP', 'MP'), ");
	    	sb.append("            CASE P.CC_CD ");
	    	sb.append("              WHEN 'BLNG' THEN 'BC' ");
	    	sb.append("              WHEN 'RMC' THEN 'BC' ");
	    	sb.append("              ELSE ");
	    	sb.append("                DECODE( ");
	    	sb.append("                  NVL(CEMENT_VSL_IND, 'N'), ");
	    	sb.append("                  'Y', 'BC', ");
	    	sb.append("                  DECODE(C.BULK_VSL_IND, 'Y', 'BC', 'GC') ");
	    	sb.append("                ) ");
	    	sb.append("            END ");
	    	sb.append("          ) ");
	    	sb.append("     END) ");
	    	sb.append(" END) AS TAB_FILTER ");
	    	sb.append("FROM VESSEL_CALL V ");
	    	sb.append("JOIN VESSEL_PRE_OPS P ON V.VV_CD = P.VV_CD ");
	    	sb.append("JOIN CARGO_CLIENT_CODE C ON P.CC_CD = C.CC_CD) RECS  WHERE RECS.scheme in ('JFS','JOL','JSC', 'JSW','JTL' ,'JLR', 'JNL' , 'JWP','JCL') and RECS.TAB_FILTER in ('GC', 'J1 Basin', 'LCT') AND RECS.VV_CD = :vvcd ");
	    	
	    	paramMap.put("vvcd",vvcd);
	    	
	    	rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
	    	
	    	if(rs.next()) {
	    		indicator = true;
	    	}
	    	log.info("Indicator in Scheme Indicator:" + indicator);
	    	return indicator;
	    }
	    catch (Exception e) {
	        log.error("Exception in getLatestOsdFile", e);
	        throw new BusinessException("M4201");
	    }
	}
	@Override
	public boolean osdSubmitindicator(String vvcd, boolean beforeApprove) throws BusinessException {
			
		SqlRowSet rs;
	    Map<String, Object> paramMap = new HashMap<>();
	    boolean indicator = false;
	 
	    try {
	    	
	    	String sb = "select submit_status from osd_review where vv_cd=:vvcd ";
	    	
	    	paramMap.put("vvcd",vvcd);
	    	
	    	rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
	    	
	    	if(rs.next()) {
	    		String status = CommonUtil.deNull(rs.getString("submit_status"));
	    		if(beforeApprove) {
	    			indicator = (status.equalsIgnoreCase("S") || status.equalsIgnoreCase("A"));
	    		}
	    		else {
	    			indicator =  status.equalsIgnoreCase("A");
	    		}
	    	}   	
			log.info("Indicator in Submit Indicator:" + indicator);
			log.info("--------End of Scheme changes --------");
			
	    	return indicator;
	    }
	    catch (Exception e) {
	        log.error("Exception in getLatestOsdFile", e);
	        throw new BusinessException("M4201");
	    }
		
	}
	@Override
	 public Long approveOsd(String vesselCode,String userAccount) throws BusinessException {
		 SqlRowSet rowSet = null;
	     Map<String, Object> paramMap = new HashMap<>();
	     StringBuilder sql = new StringBuilder();
	     Long reviewId = null;
	
	     try {
	         log.info("START: APPROVEOSD | vesselCode=" + vesselCode);
	         sql.setLength(0);
	         paramMap.clear();
	
	         sql.append("SELECT OSD_REVIEW_ID ");
	         sql.append("  FROM TOPS.OSD_REVIEW ");
	         sql.append(" WHERE VV_CD = :vesselCode ");
	
	         paramMap.put("vesselCode", vesselCode);
	
	         log.info("SQL(fetchReviewId): " + sql + " param=" + paramMap);
	         rowSet = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
	
	         if (!rowSet.next()) {
	             throw new BusinessException("NO_REVIEW_FOUND");
	         }
	
	         reviewId = rowSet.getLong("OSD_REVIEW_ID");
	
	         sql.setLength(0);
	         paramMap.clear();
	
	         sql.append("UPDATE TOPS.OSD_REVIEW SET ");
	         sql.append("   APPROVE_USER_ID = :userAccount, ");
	         sql.append("   APPROVE_DTTM = SYSDATE, ");
	         sql.append("   SUBMIT_STATUS = 'A', ");
	         sql.append("   LAST_MODIFY_USER_ID = :userAccount, ");
	         sql.append("   LAST_MODIFY_DTTM = SYSDATE ");
	         sql.append(" WHERE OSD_REVIEW_ID = :reviewId");
	
	         paramMap.put("userAccount", userAccount);
	         paramMap.put("reviewId", reviewId);
	
	         log.info("SQL(updateQuery): " + sql + " param=" + paramMap);
	         namedParameterJdbcTemplate.update(sql.toString(), paramMap);
	
	         log.info("END: APPROVEOSD | reviewId=" + reviewId);
	         return reviewId;
	
	     } catch (BusinessException be) {
	         throw be;
	     } catch (Exception ex) {
	         log.error("Exception in APPROVEOSD DAO", ex);
	         throw new BusinessException("M4201");
	     }
	}
	
	@Override
	 public int getSumOfExemptionMinutes(String vesselCode, String ExmeptionType) throws BusinessException {
		 SqlRowSet rowSet = null;
	     Map<String, Object> paramMap = new HashMap<>();
	     StringBuilder sql = new StringBuilder();
	     Long reviewId = null;
	     int sumValue = 0;
	
	     try {
	         log.info("START: getSumOfExemptionMinutes | vesselCode=" + vesselCode);
	         sql.setLength(0);
	         paramMap.clear();
	
	         sql.append("SELECT OSD_REVIEW_ID ");
	         sql.append("  FROM TOPS.OSD_REVIEW ");
	         sql.append(" WHERE VV_CD = :vesselCode ");
	
	         paramMap.put("vesselCode", vesselCode);
	
	         log.info("SQL(fetchReviewId): " + sql.toString() + " param=" + paramMap);
	         rowSet = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
	
	         if (!rowSet.next()) {
	             throw new BusinessException("NO_REVIEW_FOUND");
	         }
	
	         reviewId = rowSet.getLong("OSD_REVIEW_ID");
	
	         sql.setLength(0);
	         paramMap.clear();
	
	         String sumSql = "select sum(Exemption_MINS) SUMVALUE from osd_review_exemption where osd_review_id = :reviewId and Exemption_TYPE = :ExmeptionType group by OSD_REVIEW_ID,Exemption_type ";

	         paramMap.put("reviewId", reviewId);
	         paramMap.put("ExmeptionType", ExmeptionType);
	         log.info("SQL(Sum Query): " + sql + " param=" + paramMap);
	         rowSet = namedParameterJdbcTemplate.queryForRowSet(sumSql, paramMap);
	         if(rowSet.next()) {
	        	 sumValue =  rowSet.getInt("SUMVALUE");
	         }
	
	         log.info("END:  | reviewId=" + reviewId);
	         return sumValue;
	
	     } catch (BusinessException be) {
	         throw be;
	     } catch (Exception ex) {
	         log.error("Exception in APPROVEOSD DAO", ex);
	         throw new BusinessException("M4201");
	     }
	}

	//CH-7 trigger email functionality
	@Override
	public EmailValueObject getEmailContentForQueryOsd(String vvcd, boolean lateQ, boolean osdQ, String queryRemarks) throws BusinessException {
		EmailValueObject vo = new EmailValueObject();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<>();
		StringBuffer sb = new StringBuffer();

		try {
			sb.setLength(0);
			paramMap.clear();

			sb.append("SELECT misc_type_cd, misc_type_nm, remarks ");
			sb.append(" FROM system_config ");
			sb.append(" WHERE cat_cd = 'OSD_QUERY_EMAIL' ");

			log.info("SQL(getOsdExemptionList): " + sb.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			log.info("Result Set : " + rs.toString());

			while (rs.next()) {
				String miscTypeNm = CommonUtility.deNull(rs.getString("misc_type_nm"));
				String miscTypeCd = CommonUtility.deNull(rs.getString("misc_type_cd"));
				if ("RECEIVER".equals(miscTypeCd)) {
					String[] emailStr = miscTypeNm.split(",");
					vo.setRecipientAddress(emailStr);
				} else if ("SUBJECT".equals(miscTypeCd)) {
					vo.setSubject(miscTypeNm);
				}
			}

		} catch (Exception e) {
			log.error("Error in getOsdExemptionList()", e);
	        throw new BusinessException("M4201");
		}
		return vo;
	}

}
