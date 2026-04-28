package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.ReExportRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ReExportValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Repository("reExportRepository")
public class ReExportJdbcRepository implements ReExportRepository {

	private static final Log log = LogFactory.getLog(ReExportJdbcRepository.class);
	public String logStatusGlobal = "N";

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	// ejb.sessionBeans.gbms.cargo.reexport --> ReExportEjb

	@Override
	public List<ReExportValueObject> getPortList() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
		List<ReExportValueObject> portList = new ArrayList<ReExportValueObject>();
		sql = "SELECT PORT_CD, PORT_NM FROM UN_PORT_CODE ORDER BY PORT_NM";

		try {
			log.info("START: getPortList  DAO  Start Obj ");
			log.info(" *** getPortList SQL *****" + sql);
			log.info(" *** getPortList params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				ReExportValueObject mvObj = new ReExportValueObject();
				mvObj.setPortL(CommonUtility.deNull(rs.getString("PORT_CD")));
				mvObj.setPortLn(CommonUtility.deNull(rs.getString("PORT_NM")));
				portList.add(mvObj);
			}

			log.info("END: *** getPortList Result *****" + portList.size());

		} catch (NullPointerException e) {
			log.info("Exception getPortList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getPortList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPortList  DAO  END");
		}

		return portList;
	}// end of getPortList method

	@Override
	public String updateReExportDetails(String edoasnnbr, String PortL, String struserid, String cocode)
			throws BusinessException {
		String newedoasnnbr = checkReExportStatus(edoasnnbr, cocode);
		String mftseqnbr = "";
		String mftcrgstatus = "L";
		java.util.StringTokenizer st1 = new java.util.StringTokenizer(newedoasnnbr, "|");
		newedoasnnbr = st1.nextToken();
		if (!(newedoasnnbr.equalsIgnoreCase(edoasnnbr))) {
			throw new BusinessException(ConstantUtil.ErrorMsg_Vessel_Closed);
		}

		String sql1 = "";
		String sql2 = "";

		/*
		 * SELECT (SELECT COUNT(*) FROM GB_EDO EDO1 WHERE EDO1.MFT_SEQ_NBR = (SELECT
		 * EDO2.MFT_SEQ_NBR FROM GB_EDO EDO2 WHERE EDO2.EDO_ASN_NBR='2070043') AND
		 * EDO1.CRG_STATUS='L') AS COUNTS , EDO3.MFT_SEQ_NBR FROM GB_EDO EDO3 WHERE
		 * EDO3.EDO_ASN_NBR='2070043'
		 */

		String strcrgstatuscount = "0";
		String strsqldate = "";
		String strtransnbr = "0";
		String sqllog = "";

		String strUpdatetrans = "";
		String strUpdatetrans2 = "";

		SqlRowSet rsdate = null;
		SqlRowSet rsref = null;
		SqlRowSet rscount = null;
		SqlRowSet rslog = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		String strrefnbr = "";

		try {

			log.info("START: updateReExportDetails  DAO  Start Obj " + " edoasnnbr:" + CommonUtility.deNull(edoasnnbr) + " PortL:" + CommonUtility.deNull(PortL)
					+ " struserid:" + CommonUtility.deNull(struserid) + " cocode:" + CommonUtility.deNull(cocode));

			String sqlref = "SELECT MAX(REEXP_REF_NBR) FROM GB_EDO";
			String sqldate = "SELECT TO_CHAR(SYSDATE,'YY/MM/DD') AS STRDATE FROM DUAL";

			sb.append("SELECT (SELECT COUNT(*) FROM GB_EDO EDO1 WHERE EDO1.MFT_SEQ_NBR = (SELECT EDO2.MFT_SEQ_NBR");
			sb.append(
					" FROM GB_EDO EDO2 WHERE EDO2.EDO_ASN_NBR=:edoasnnbr) AND EDO1.CRG_STATUS='L') AS COUNTS , EDO3.MFT_SEQ_NBR");
			sb.append(" FROM GB_EDO EDO3 WHERE EDO3.EDO_ASN_NBR=:edoasnnbr");
			String sqlcount = sb.toString();

			try {
				log.info(" *** updateReExportDetails SQL *****" + sqldate);
				log.info(" *** updateReExportDetails params *****" + paramMap.toString());
				
				rsdate = namedParameterJdbcTemplate.queryForRowSet(sqldate, paramMap);
				while (rsdate.next()) {
					strsqldate = CommonUtility.deNull(rsdate.getString(1));
				}
				log.info("END: *** updateReExportDetails Result *****" + CommonUtility.deNull(strsqldate));
			} catch (Exception se) {
				log.info("Exception updateReExportDetails: ", se);
				throw new BusinessException("M1004");
			}
			try {
				log.info(" *** updateReExportDetails SQL *****" + sqlref);
				log.info(" *** updateReExportDetails params *****" + paramMap.toString());
				rsref = namedParameterJdbcTemplate.queryForRowSet(sqlref, paramMap);

				while (rsref.next()) {
					strrefnbr = CommonUtility.deNull(rsref.getString(1));
				}
				log.info("END: *** updateReExportDetails Result *****" + CommonUtility.deNull(strrefnbr));
			} catch (Exception se) {
				log.info("Exception updateReExportDetails: ", se);
				throw new BusinessException("M1004");
			}
			// log.info("From DB Number "+strrefnbr);
			if (strrefnbr.equalsIgnoreCase("")) {
				strrefnbr = "R00000001";
			}
			if (strrefnbr.length() == 7) {
				// log.info("inside if 7");
				strrefnbr = "0".concat(strrefnbr);
			}
			if (strrefnbr.length() == 6) {
				// log.info("inside if 6");
				strrefnbr = "00".concat(strrefnbr);
			}
			if (strrefnbr.length() == 5) {
				// log.info("inside if 5");
				strrefnbr = "000".concat(strrefnbr);
			}

			// log.info("OLD Number "+strrefnbr);
			int intrefnbr = Integer.parseInt(strrefnbr.substring(5, 9));
			String strrefnbryy = strrefnbr.substring(1, 3);
			String strsqlyy = strsqldate.substring(0, 2);
			String strrefnbrmm = strrefnbr.substring(3, 5);
			String strsqlmm = strsqldate.substring(3, 5);
			if ((strrefnbryy.equalsIgnoreCase(strsqlyy)) && (strrefnbrmm.equalsIgnoreCase(strsqlmm))) {
				strrefnbr = (strrefnbryy).concat(strrefnbrmm);
				intrefnbr = intrefnbr + 2;
				String strtempnbr = Integer.toString(intrefnbr);
				if (strtempnbr.length() == 1) {
					strrefnbr = strrefnbr.concat("000");
					strrefnbr = strrefnbr.concat(strtempnbr);
				}
				if (strtempnbr.length() == 2) {
					strrefnbr = strrefnbr.concat("00");
					strrefnbr = strrefnbr.concat(strtempnbr);
				}
				if (strtempnbr.length() == 3) {
					strrefnbr = strrefnbr.concat("0");
					strrefnbr = strrefnbr.concat(strtempnbr);
				}
				if (strtempnbr.length() == 4) {
					strrefnbr = strrefnbr.concat(strtempnbr);
				}
			} else {
				strrefnbr = (strsqlyy).concat(strsqlmm);
				strrefnbr = strrefnbr.concat("0001");
			}
			strrefnbr = "R".concat(strrefnbr);
			log.info("strrefnbr" + strrefnbr);
			// End of new number Generation

			sb = new StringBuffer();
			sb.append("UPDATE GB_EDO SET REEXP_REF_NBR =:strrefnbr, CRG_STATUS='R',");
			sb.append(" DES_PORT =:PortL, REEXP_APPL_DTTM=sysdate, LAST_MODIFY_DTTM = sysdate,");
			sb.append(" LAST_MODIFY_USER_ID = :struserid WHERE EDO_ASN_NBR =:edoasnnbr");

			sql1 = sb.toString();

			paramMap.put("strrefnbr", strrefnbr);
			paramMap.put("PortL", PortL);
			paramMap.put("struserid", struserid);
			paramMap.put("edoasnnbr", edoasnnbr);
			log.info(" *** updateReExportDetails SQL *****" + sql1);
			log.info(" *** updateReExportDetails params *****" + paramMap.toString());
			
			int count = namedParameterJdbcTemplate.update(sql1, paramMap);

			if (count == 0) {
				throw new BusinessException(ConstantUtil.ErrorMsg_Update_Failed);
			}

			log.info("strcrgstatuscount :1 " + strcrgstatuscount);
			log.info("mftseqnbr :1 " + mftseqnbr);
			try {

				log.info(" *** updateReExportDetails SQL *****" + sqlcount);
				paramMap.put("edoasnnbr", edoasnnbr);
				log.info(" *** updateReExportDetails params *****" + paramMap.toString());
				
				rscount = namedParameterJdbcTemplate.queryForRowSet(sqlcount, paramMap);
				while (rscount.next()) {
					strcrgstatuscount = CommonUtility.deNull(rscount.getString(1));
					mftseqnbr = CommonUtility.deNull(rscount.getString(2));
				}
				log.info("END: *** updateReExportDetails Result *****" + CommonUtility.deNull(strcrgstatuscount));
				log.info("END: *** updateReExportDetails Result *****" + CommonUtility.deNull(mftseqnbr));
			} catch (Exception se) {
				log.info("Exception updateReExportDetails: ", se);
				throw new BusinessException("M1004");
			}
			log.info("strcrgstatuscount :2 " + strcrgstatuscount);
			log.info("mftseqnbr :2 " + mftseqnbr);
			log.info("sqlcount :2 " + sqlcount);

			sb = new StringBuffer();

			if (strcrgstatuscount.equalsIgnoreCase("0")) {
				mftcrgstatus = "R";

				sb.append("UPDATE MANIFEST_DETAILS SET ");
				sb.append(" CRG_STATUS=:mftcrgstatus,");
				sb.append(" DES_PORT=:PortL,");
				sb.append(" LAST_MODIFY_DTTM = sysdate,");
				sb.append(" LAST_MODIFY_USER_ID = :struserid WHERE MFT_SEQ_NBR =:mftseqnbr");

				sql2 = sb.toString();
			} else {
				mftcrgstatus = "L";

				sb.append("UPDATE MANIFEST_DETAILS SET ");
				sb.append(" CRG_STATUS=:mftcrgstatus,");
				sb.append(" LAST_MODIFY_DTTM = sysdate,");
				sb.append(" LAST_MODIFY_USER_ID = :struserid WHERE MFT_SEQ_NBR =:mftseqnbr");

				sql2 = sb.toString();
			}
			log.info(" *** updateReExportDetails SQL *****" + sql2);

			if (strcrgstatuscount.equalsIgnoreCase("0")) {
				paramMap.put("mftcrgstatus", mftcrgstatus);
				paramMap.put("PortL", PortL);
				paramMap.put("struserid", struserid);
				paramMap.put("mftseqnbr", mftseqnbr);
			} else {
				paramMap.put("mftcrgstatus", mftcrgstatus);
				paramMap.put("struserid", struserid);
				paramMap.put("mftseqnbr", mftseqnbr);
			}
			log.info(" *** updateReExportDetails params *****" + paramMap.toString());
			
			int countm = namedParameterJdbcTemplate.update(sql2, paramMap);
			log.info("query executed1");
			if (countm == 0) {
				throw new BusinessException(ConstantUtil.ErrorMsg_Update_Failed);
			}

			log.info("query executed2");
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				try {
					log.info(" *** updateReExportDetails SQL *****" + sqllog); // here sqllog is empty
					log.info(" *** updateReExportDetails params *****" + paramMap.toString());
					rslog = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
					while (rslog.next()) {
						strtransnbr = CommonUtility.deNull(rslog.getString(1));
					}
					log.info("END: *** updateReExportDetails strtransnbr Result *****" + CommonUtility.deNull(strtransnbr));
					
					if (strtransnbr.equalsIgnoreCase("")) {
						strtransnbr = "0";
					} else {
						strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
					}
				} catch (Exception se) {
					log.info("Exception updateReExportDetails: ", se);
					throw new BusinessException("M1004");
				}
				sb = new StringBuffer();
				sb.append("INSERT INTO GB_EDO_TRANS");
				sb.append(" (TRANS_NBR,REEXP_REF_NBR,CRG_STATUS, REEXP_APPL_DTTM,");
				sb.append(" LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID, DES_PORT) ");
				sb.append(
						"VALUES (:strtransnbr, :strrefnbr, 'R', sysdate, sysdate, :struserid,:PortL WHERE AND EDO_ASN_NBR =:edoasnnbr");

				strUpdatetrans = sb.toString();

				paramMap.put("strtransnbr", strtransnbr);
				paramMap.put("strrefnbr", strrefnbr);
				paramMap.put("struserid", struserid);
				paramMap.put("PortL", PortL);
				paramMap.put("edoasnnbr", edoasnnbr);
				log.info(" *** updateReExportDetails SQL *****" + strUpdatetrans);
				log.info(" *** updateReExportDetails params *****" + paramMap.toString());
				
				int count1 = namedParameterJdbcTemplate.update(strUpdatetrans, paramMap);

				if (count1 == 0) {
					throw new BusinessException(ConstantUtil.ErrorMsg_Update_Failed);
				}
				sb = new StringBuffer();

				sb.append("INSERT INTO MANIFEST_DETAILS_TRANS");
				sb.append(" (TRANS_NBR,CRG_STATUS,");
				sb.append(" LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID) ");
				sb.append(
						"VALUES (:strtransnbr, :mftcrgstatus,  sysdate, :struserid WHERE AND EDO_ASN_NBR =:edoasnnbr");

				strUpdatetrans2 = sb.toString();

				paramMap.put("strtransnbr", strtransnbr);
				paramMap.put("mftcrgstatus", mftcrgstatus);
				paramMap.put("struserid", struserid);
				paramMap.put("edoasnnbr", edoasnnbr);

				log.info(" *** updateReExportDetails SQL *****" + strUpdatetrans2);
				log.info(" *** updateReExportDetails params *****" + paramMap.toString());
				int count2 = namedParameterJdbcTemplate.update(strUpdatetrans2, paramMap);
				if (count2 == 0) {
					throw new BusinessException(ConstantUtil.ErrorMsg_Update_Failed);
				}

			}

			// con.commit();

			log.info("END: *** updateReExportDetails Result *****" + strrefnbr + "|" + strsqldate);
			return strrefnbr + "|" + strsqldate;

		} catch (NullPointerException e) {
			log.info("Exception updateReExportDetails : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception updateReExportDetails : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updateReExportDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateReExportDetails  DAO  END");
		}

	}

	@Override
	public boolean chkPortCode(String portcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
		boolean bpcd = false;

		try {
			log.info("START: chkPortCode  DAO  Start Obj " + " portcd:" + CommonUtility.deNull(portcd));

			sql = "SELECT PORT_CD FROM UN_PORT_CODE WHERE PORT_CD=:portcd";

			paramMap.put("portcd", portcd);
			
			log.info(" *** chkPortCode SQL *****" + sql);
			log.info(" *** chkPortCode params *****" + paramMap.toString());
			
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				bpcd = true;
			} else {
				bpcd = false;
			}

			log.info("END: *** chkPortCode Result *****" + bpcd);

		} catch (NullPointerException e) {
			log.info("Exception chkPortCode : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkPortCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkPortCode  DAO  END");
		}

		return bpcd;
	}// end of CHK PORTCODE method

	@Override
	public String checkReExportStatus(String edoasnnbr, String coCd) throws BusinessException {

		String sql = "";
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String atb_closing_time = "0";
		String cod_closing_time = "0";

		/*
		 * old Query "SELECT EDO.EDO_ASN_NBR, MD.BL_NBR, MD.CRG_DES,"
		 * +" MD.NBR_PKGS, EDO.NOM_WT, EDO.NOM_VOL, MD.EDO_NBR_PKGS,"
		 * +" EDO.CRG_STATUS,EDO.REEXP_REF_NBR,"
		 * +" TO_CHAR(MD.REEXP_APPL_DTTM,'DD-MM-YYYY HH:MI')"
		 * +" FROM VESSEL_CALL VC, MANIFEST_DETAILS MD, GB_EDO EDO,"
		 * +" BERTHING B WHERE MD.MFT_SEQ_NBR=EDO.MFT_SEQ_NBR AND"
		 * +" B.VV_CD=VC.VV_CD AND DN_NBR_PKGS = 0 AND B.SHIFT_IND='1' AND"
		 * +" EDO.EDO_ASN_NBR='"+edoasnnbr+"' AND EDO.EDO_STATUS='A' AND"
		 * +" TRANS_DN_NBR_PKGS = 0 AND MD.BL_STATUS='A' AND"
		 * +" MD.VAR_NBR = VC.VV_CD AND VC.GB_CLOSE_BJ_IND <>'Y' AND"
		 * +"((EDO.CRG_STATUS='R') OR ((EDO.CRG_STATUS='L') AND ("
		 * +"((VC.SCHEME = 'JNL')	AND ((NVL(ROUND(((ETB_DTTM-SYSDATE)*24)+"
		 * +"(TO_NUMBER(TO_CHAR(ETB_DTTM,'HH'))-"
		 * +"TO_NUMBER(TO_CHAR(SYSDATE,'HH')))),0))<16)) OR"
		 * +" ((VC.SCHEME NOT IN ('JNL','JBT','JCT')) AND"
		 * +" ((NVL(ROUND(((SYSDATE-COD_DTTM)*24)+"
		 * +"(TO_NUMBER(TO_CHAR(SYSDATE,'HH'))-"
		 * +"TO_NUMBER(TO_CHAR(COD_DTTM,'HH')))),0))<72)))))" +" ORDER BY MD.BL_NBR";
		 */

		String newedoasnnbr = "";
		String crgstatus = "";
		String reexpnbr = "";
		String reexpdttm = "";
		try {
			log.info("START: checkReExportStatus  DAO  Start Obj " + " edoasnnbr:" + CommonUtility.deNull(edoasnnbr) + " coCd:" + CommonUtility.deNull(coCd));

			String sql1 = "SELECT CODE_VALUE FROM GB_GLOBAL_DATA  WHERE CODE_STATUS='A' AND CODE_CD='REEXPATB'";
			String sql2 = "SELECT CODE_VALUE FROM GB_GLOBAL_DATA  WHERE CODE_STATUS='A' AND CODE_CD='REEXPCOD'";
			try {
				log.info(" *** checkReExportStatus SQL *****" + sql1);
				log.info(" *** checkReExportStatus params *****" + paramMap.toString());
				
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
				while (rs1.next()) {
					atb_closing_time = CommonUtility.deNull(rs1.getString(1));
				}
				log.info("END: *** checkReExportStatus atb_closing_time Result *****" + CommonUtility.deNull(atb_closing_time));
			} catch (Exception se) {
				log.info("Exception checkReExportStatus: ", se);
				throw new BusinessException("M1004");
			}
			try {
				log.info(" *** checkReExportStatus SQL *****" + sql2);
				log.info(" *** checkReExportStatus params *****" + paramMap.toString());
				
				rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
				while (rs2.next()) {
					cod_closing_time = CommonUtility.deNull(rs2.getString(1));
				}
				log.info("END: *** checkReExportStatus cod_closing_time Result *****" + CommonUtility.deNull(cod_closing_time));
			} catch (Exception se) {
				log.info("Exception checkReExportStatus: ", se);
				throw new BusinessException("M1004");
			}
			if (atb_closing_time == null || atb_closing_time.equalsIgnoreCase("")) {
				atb_closing_time = "0";
			}
			if (cod_closing_time == null || cod_closing_time.equalsIgnoreCase("")) {
				cod_closing_time = "0";
			}

			if (coCd.equalsIgnoreCase("JP")) {
				// CR-CIM- 0000109
				// Zak:- Must still be allowed to Reexport even after BJ closed
				// - Agent must Reexport Cargo 72 hours before GB_COD
				// - Same for all type of vessel (scheme)
				/*
				 * sql="SELECT UNIQUE(EDO.EDO_ASN_NBR), MD.BL_NBR, MD.CRG_DES,"
				 * +" MD.NBR_PKGS, EDO.NOM_WT, EDO.NOM_VOL, MD.EDO_NBR_PKGS,"
				 * +" EDO.CRG_STATUS,EDO.REEXP_REF_NBR,"
				 * +" TO_CHAR(MD.REEXP_APPL_DTTM,'DD-MM-YYYY HH24:MI')"
				 * +" FROM VESSEL_CALL VC, MANIFEST_DETAILS MD, GB_EDO EDO,"
				 * +" BERTHING B WHERE MD.MFT_SEQ_NBR=EDO.MFT_SEQ_NBR AND"
				 * +" B.VV_CD=VC.VV_CD AND DN_NBR_PKGS = 0 AND B.SHIFT_IND='1' AND"
				 * +" TRANS_DN_NBR_PKGS = 0 AND MD.BL_STATUS='A' AND"
				 * +" MD.VAR_NBR = VC.VV_CD AND VC.GB_CLOSE_BJ_IND <>'Y' "
				 * +" AND EDO.EDO_STATUS='A' AND EDO.EDO_ASN_NBR='"+edoasnnbr+"'"
				 * +" AND EDO.CRG_STATUS IN ('R','L')" +" ORDER BY MD.BL_NBR";
				 */
				sb.append("SELECT UNIQUE(EDO.EDO_ASN_NBR), MD.BL_NBR, MD.CRG_DES,");
				sb.append(" MD.NBR_PKGS, EDO.NOM_WT, EDO.NOM_VOL, MD.EDO_NBR_PKGS,");
				sb.append(" EDO.CRG_STATUS,EDO.REEXP_REF_NBR,");
				sb.append(" TO_CHAR(MD.REEXP_APPL_DTTM,'DD-MM-YYYY HH24:MI')");
				sb.append(" FROM VESSEL_CALL VC, MANIFEST_DETAILS MD, GB_EDO EDO,");
				sb.append(" BERTHING B WHERE MD.MFT_SEQ_NBR=EDO.MFT_SEQ_NBR AND");
				sb.append(" B.VV_CD=VC.VV_CD AND DN_NBR_PKGS = 0 AND B.SHIFT_IND='1' AND");
				sb.append(" TRANS_DN_NBR_PKGS = 0 AND MD.BL_STATUS='A' AND");
				sb.append(" MD.VAR_NBR = VC.VV_CD ");
				sb.append(" AND EDO.EDO_STATUS='A' AND EDO.EDO_ASN_NBR=:edoasnnbr");
				sb.append(" AND EDO.CRG_STATUS IN ('R','L')");
				sb.append(" ORDER BY MD.BL_NBR");
			} else {
				/*
				 * sql="SELECT UNIQUE(EDO.EDO_ASN_NBR), MD.BL_NBR, MD.CRG_DES,"
				 * +" MD.NBR_PKGS, EDO.NOM_WT, EDO.NOM_VOL, MD.EDO_NBR_PKGS,"
				 * +" EDO.CRG_STATUS,EDO.REEXP_REF_NBR,"
				 * +" TO_CHAR(EDO.REEXP_APPL_DTTM,'DD-MM-YYYY HH24:MI')"
				 * +" FROM VESSEL_CALL VC, MANIFEST_DETAILS MD, GB_EDO EDO,"
				 * +" BERTHING B WHERE MD.MFT_SEQ_NBR=EDO.MFT_SEQ_NBR AND"
				 * +" B.VV_CD=VC.VV_CD AND DN_NBR_PKGS = 0 AND B.SHIFT_IND='1' AND"
				 * +" TRANS_DN_NBR_PKGS = 0 AND MD.BL_STATUS='A' AND"
				 * +" MD.VAR_NBR = VC.VV_CD AND VC.GB_CLOSE_BJ_IND <>'Y' "
				 * +" AND EDO.EDO_STATUS='A' AND EDO.EDO_ASN_NBR='"+edoasnnbr+"'"
				 * +" AND ((EDO.CRG_STATUS='R') OR ((EDO.CRG_STATUS='L') AND ("
				 * +" ((VC.SCHEME IN('JNL','JBT')) AND"
				 * +" ((NVL(ROUND(((NVL(B.ATB_DTTM,NVL(B.ETB_DTTM,VC.VSL_BERTH_DTTM))-SYSDATE)*24)+"
				 * +" (TO_NUMBER(TO_CHAR(NVL(B.ATB_DTTM,NVL(B.ETB_DTTM,VC.VSL_BERTH_DTTM)),'HH'))-"
				 * +" TO_NUMBER(TO_CHAR(SYSDATE,'HH')))),0))<" +atb_closing_time+")) OR"
				 * +" ((VC.SCHEME NOT IN ('JNL','JBT','JCT')) AND"
				 * +" ((NVL(ROUND(((SYSDATE-B.GB_COD_DTTM)*24)+"
				 * +" (TO_NUMBER(TO_CHAR(SYSDATE,'HH'))-"
				 * +" TO_NUMBER(TO_CHAR(B.GB_COD_DTTM,'HH')))),0))<" +cod_closing_time+")))))"
				 * +" ORDER BY MD.BL_NBR";
				 */

				sb.append("SELECT UNIQUE(EDO.EDO_ASN_NBR), MD.BL_NBR, MD.CRG_DES,");
				sb.append(" MD.NBR_PKGS, EDO.NOM_WT, EDO.NOM_VOL, MD.EDO_NBR_PKGS,");
				sb.append(" EDO.CRG_STATUS,EDO.REEXP_REF_NBR,");
				sb.append(" TO_CHAR(EDO.REEXP_APPL_DTTM,'DD-MM-YYYY HH24:MI')");
				sb.append(" FROM VESSEL_CALL VC, MANIFEST_DETAILS MD, GB_EDO EDO,");
				sb.append(" BERTHING B WHERE MD.MFT_SEQ_NBR=EDO.MFT_SEQ_NBR AND");
				sb.append(" B.VV_CD=VC.VV_CD AND DN_NBR_PKGS = 0 AND B.SHIFT_IND='1' AND");
				sb.append(" MD.VAR_NBR = VC.VV_CD  ");
				sb.append(" AND TRANS_DN_NBR_PKGS = 0 AND MD.BL_STATUS='A' ");
				sb.append(" AND EDO.EDO_STATUS='A' AND EDO.EDO_ASN_NBR=:edoasnnbr");
				sb.append(" AND EDO.CRG_STATUS IN ('R', 'L') AND ");
				sb.append(
						" ( (B.ATU_DTTM IS NULL AND B.GB_COD_DTTM IS NULL )OR (B.ATU_DTTM IS  NULL  AND (SELECT SYSDATE-B.GB_COD_DTTM FROM DUAL)*24 < :cod_closing_time) OR ");
				sb.append(
						" (B.GB_COD_DTTM IS  NULL AND (SELECT SYSDATE-B.ATU_DTTM FROM DUAL)*24 < :cod_closing_time) ");
				sb.append(" OR (SELECT SYSDATE-B.GB_COD_DTTM FROM DUAL)*24 < :cod_closing_time )");
				// +" AND (VC.CREATE_CUST_CD='"+coCd+"' OR MD.MANIFEST_CREATE_CD ='"+coCd+"' OR
				// EDO.EDO_CREATE_CD='"+coCd+"')"
				sb.append(
						" AND (VC.CREATE_CUST_CD=:coCd OR MD.MANIFEST_CREATE_CD =:coCd OR EDO.EDO_CREATE_CD =:coCd OR EDO.ADP_CUST_CD=:coCd)");
				sb.append(" ORDER BY MD.BL_NBR");
			}
			sql = sb.toString();
			log.info(" *** checkReExportStatus SQL *****" + sql);

			if (coCd.equalsIgnoreCase("JP")) {
				paramMap.put("edoasnnbr", edoasnnbr);
			} else {
				paramMap.put("edoasnnbr", edoasnnbr);
				paramMap.put("cod_closing_time", cod_closing_time);
				paramMap.put("coCd", coCd);
			}
			
			log.info(" *** checkReExportStatus params *****" + paramMap.toString());
			
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			// log.info("the sql:"+sql);
			while (rs.next()) {
				newedoasnnbr = CommonUtility.deNull(rs.getString(1));
				crgstatus = CommonUtility.deNull(rs.getString(8));
				reexpnbr = CommonUtility.deNull(rs.getString(9));
				reexpdttm = CommonUtility.deNull(rs.getString(10));
			}
			if (newedoasnnbr.length() == 7) {
				newedoasnnbr = "0".concat(newedoasnnbr);
			}
			if (newedoasnnbr.length() == 6) {
				newedoasnnbr = "00".concat(newedoasnnbr);
			}
			if (newedoasnnbr.length() == 5) {
				newedoasnnbr = "000".concat(newedoasnnbr);
			}
			if (newedoasnnbr.length() == 4) {
				newedoasnnbr = "0000".concat(newedoasnnbr);
			}
			if (newedoasnnbr.length() == 3) {
				newedoasnnbr = "00000".concat(newedoasnnbr);
			}
			if (newedoasnnbr.length() == 2) {
				newedoasnnbr = "000000".concat(newedoasnnbr);
			}
			if (newedoasnnbr.length() == 1) {
				newedoasnnbr = "0000000".concat(newedoasnnbr);
			}

			log.info("END: *** checkReExportStatus Result *****" + newedoasnnbr + "|" + crgstatus + "|" + reexpnbr + "|"
					+ reexpdttm.toString());
			return newedoasnnbr + "|" + crgstatus + "|" + reexpnbr + "|" + reexpdttm;

		} catch (NullPointerException e) {
			log.info("Exception checkReExportStatus : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception checkReExportStatus : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception checkReExportStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkReExportStatus  DAO  END");
		}

	}

	@Override
	public List<ReExportValueObject> getVesselVoy(String cocode) throws BusinessException {

		String sql = "";
		SqlRowSet rs = null;
		SqlRowSet rs2 = null;
		StringBuffer sb = new StringBuffer();
		List<ReExportValueObject> voyList = new ArrayList<ReExportValueObject>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// SQL changed by Vietnd02::start

		// CR-CIM- 0000109

		String cod_closing_time = "0";
		try {
			log.info("START: getVesselVoy  DAO  Start Obj " + " cocode:" + CommonUtility.deNull(cocode));
			String sql2 = "SELECT CODE_VALUE FROM GB_GLOBAL_DATA  WHERE CODE_STATUS='A' AND CODE_CD='REEXPCOD'";

			log.info(" *** getVesselVoy SQL2 *****" + sql2);
			log.info(" *** getVesselVoy params2 *****" + paramMap.toString());
			rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
			while (rs2.next()) {
				cod_closing_time = CommonUtility.deNull(rs2.getString(1));
			}
			if (cod_closing_time == null || cod_closing_time.equalsIgnoreCase("")) {
				cod_closing_time = "0";
			}
			log.info("END: *** getVesselVoy Result *****" + CommonUtility.deNull(cod_closing_time));
		} catch (Exception se) {
			log.info("Exception getVesselVoy: ", se);
			throw new BusinessException("M1004");
		}

		if (cocode.equals("JP")) {
			// CR-CIM- 0000109
			// Zak:- Must still be allowed to Reexport even after BJ closed
			// - Agent must Reexport Cargo 72 hours before GB_COD
			// - Same for all type of vessel (scheme)
			/*
			 * sql = "SELECT IN_VOY_NBR,VSL_NM,VV_CD FROM VESSEL_CALL WHERE" sb.
			 * append(" (VV_STATUS_IND IN ('PR','AP','AL','BR','UB') AND TERMINAL='GB') AND GB_CLOSE_BJ_IND"
			 * sb.append(" <> 'Y' ORDER BY VSL_NM,IN_VOY_NBR";
			 */

			sb.append("SELECT V.IN_VOY_NBR,V.VSL_NM,V.VV_CD, V.TERMINAL FROM VESSEL_CALL V, BERTHING B WHERE");
			sb.append(" V.VV_STATUS_IND NOT IN ('CX' )");
			sb.append("  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT') ");
			// sb.append(" AND V.TERMINAL='GB' ");
			sb.append(
					" AND ( (B.ATU_DTTM IS NULL AND B.GB_COD_DTTM IS NULL )OR (B.ATU_DTTM IS  NULL  AND (SELECT SYSDATE-B.GB_COD_DTTM FROM DUAL)*24 < :cod_closing_time) OR ");
			sb.append(" (B.GB_COD_DTTM IS  NULL AND (SELECT SYSDATE-B.ATU_DTTM FROM DUAL)*24 < :cod_closing_time) ");
			sb.append(" OR (SELECT SYSDATE-B.GB_COD_DTTM FROM DUAL)*24 < :cod_closing_time )");
			sb.append("  AND V.VV_CD = B.VV_CD AND B.SHIFT_IND=1");
			sb.append("  ORDER BY V.TERMINAL DESC, V.VSL_NM,V.IN_VOY_NBR");

		} else {
			/*
			 * sql = "SELECT IN_VOY_NBR,VSL_NM,VV_CD FROM VESSEL_CALL WHERE" sb.
			 * append(" VV_STATUS_IND IN ('PR','AP','AL','BR') AND TERMINAL='GB' AND GB_CLOSE_BJ_IND"
			 * sb.append("<> 'Y' AND CREATE_CUST_CD='"+cocode
			 * sb.append("' ORDER BY VSL_NM,IN_VOY_NBR";
			 */
			sb.append(
					"SELECT DISTINCT V.IN_VOY_NBR,V.VSL_NM,V.VV_CD, V.TERMINAL FROM VESSEL_CALL V, BERTHING B, MANIFEST_DETAILS MD, GB_EDO VD WHERE");
			sb.append(" V.VV_STATUS_IND NOT IN ('CX' ) ");
			sb.append("  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT') ");
			// sb.append(" AND V.TERMINAL='GB' "
			sb.append(
					" AND ( (B.ATU_DTTM IS NULL AND B.GB_COD_DTTM IS NULL )OR (B.ATU_DTTM IS  NULL  AND (SELECT SYSDATE-B.GB_COD_DTTM FROM DUAL)*24 < :cod_closing_time) OR ");
			sb.append(" (B.GB_COD_DTTM IS  NULL AND (SELECT SYSDATE-B.ATU_DTTM FROM DUAL)*24 < :cod_closing_time) ");
			sb.append(" OR (SELECT SYSDATE-B.GB_COD_DTTM FROM DUAL)*24 < :cod_closing_time )");
			sb.append("  AND V.VV_CD = B.VV_CD AND B.SHIFT_IND=1 ");
			sb.append("  AND v.VV_CD = MD.VAR_NBR  ");
			// +" AND V.VV_CD = VD.VV_CD (+) AND ( V.CREATE_CUST_CD='"+cocode+"'OR
			// (VD.CUST_CD = '"+cocode+"' AND VD.STATUS = 'A' ))"
			sb.append(
					"  AND V.VV_CD = VD.VAR_NBR (+) AND ( V.CREATE_CUST_CD=:cocode OR (MD.MANIFEST_CREATE_CD =  :cocode AND MD.BL_STATUS = 'A') OR (VD.ADP_CUST_CD = :cocode  AND VD.EDO_STATUS = 'A' ))");
			sb.append("  ORDER BY V.TERMINAL DESC,V.VSL_NM,V.IN_VOY_NBR");
		}
		// Vietnd02::end

		sql = sb.toString();
		try {
			if (cocode.equals("JP")) {
				paramMap.put("cod_closing_time", cod_closing_time);
			} else {
				paramMap.put("cod_closing_time", cod_closing_time);
				paramMap.put("cocode", cocode);
			}
			log.info(" *** getVesselVoy SQL *****" + sql);
			log.info(" *** getVesselVoy params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			log.info("Writing from ReExportEjb.getVesselVoy " + sql);
			String voynbr = "";
			String vslName = "";
			String VV_CD = "";
			String terminal = "";

			while (rs.next()) {
				voynbr = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				vslName = CommonUtility.deNull(rs.getString("VSL_NM"));
				VV_CD = CommonUtility.deNull(rs.getString("VV_CD"));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));

				ReExportValueObject vvvObj = new ReExportValueObject();
				vvvObj.setVoyNo(voynbr);
				vvvObj.setVslName(vslName);
				vvvObj.setVarNbr(VV_CD);
				vvvObj.setTerminal(terminal);
				voyList.add(vvvObj);
			}
			log.info("END: *** getVesselVoy Result *****" + voyList.size());

		} catch (NullPointerException e) {
			log.info("Exception getVesselVoy : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVesselVoy : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselVoy  DAO  END");
		}
		return voyList;

	}// end of get vsl list

	/**
	 * Receives Manifest Details
	 **/
	@Override
	public List<ReExportValueObject> getManifestList(String vvcode, String coCd, Criteria criteria)
			throws BusinessException {

		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
		String blno = "";
		String crgdes = "";
		String nbrpkgs = "";
		String gwt = "";
		String gvol = "";
		String seqno = "";
		String crgstat = "";
		String numpkgs = "0";
		String reexpnbr = "";
		String terminal = "";
		String scheme = "";
		String subScheme = "";
		String gcOperations = "";
		List<ReExportValueObject> manifestList = new ArrayList<ReExportValueObject>();

		String atb_closing_time = "0";
		String cod_closing_time = "0";
		try {
			log.info("START: getManifestList  DAO  Start Obj vvcode:"+ CommonUtility.deNull(vvcode) + " coCd:" + CommonUtility.deNull(coCd) 
			+ " criteria:" + criteria.toString());

			String sql1 = "SELECT CODE_VALUE FROM GB_GLOBAL_DATA  WHERE CODE_STATUS='A' AND CODE_CD='REEXPATB'";
			String sql2 = "SELECT CODE_VALUE FROM GB_GLOBAL_DATA  WHERE CODE_STATUS='A' AND CODE_CD='REEXPCOD'";

			try {
				log.info(" *** getManifestList SQL1 *****" + sql1);
				log.info(" *** getManifestList params1 *****" + paramMap.toString());
				rs1 =  namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
				while (rs1.next()) {
					atb_closing_time = CommonUtility.deNull(rs1.getString(1));
				}
				log.info("END: *** getManifestList Result *****" + CommonUtility.deNull(atb_closing_time));
			} catch (Exception se) {
				log.info("Exception getManifestList: " ,se);
				throw new BusinessException("M1004");
			}
			try {
	
				log.info(" *** getManifestList SQL2 *****" + sql2);
				log.info(" *** getManifestList params2 *****" + paramMap.toString());
				rs2 =  namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
				while (rs2.next()) {
					cod_closing_time = CommonUtility.deNull(rs2.getString(1));
				}
				log.info("END: *** getManifestList Result *****" + CommonUtility.deNull(cod_closing_time));
			} catch (Exception se) {
				log.info("Exception getManifestList: ", se);
				throw new BusinessException("M1004");
			}
			// CR-CIM- 0000109
			// Zak:- Must still be allowed to Reexport even after BJ closed
			// - Agent must Reexport Cargo 72 hours before GB_COD
			// - Same for all type of vessel (scheme)
			if (coCd.equalsIgnoreCase("JP")) {
				sb.append("SELECT UNIQUE(EDO.EDO_ASN_NBR), MD.BL_NBR, MD.CRG_DES,");
				sb.append(" MD.NBR_PKGS AS MDNBR_PKGS, EDO.NOM_WT, EDO.NOM_VOL, ");
				// sb.append(" EDO.NBR_PKGS, ");
				sb.append(" EDO.NBR_PKGS AS EDONBR_PKGS , ");
				sb.append(
						" EDO.CRG_STATUS,EDO.REEXP_REF_NBR, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND ");
				sb.append(" FROM VESSEL_CALL VC, MANIFEST_DETAILS MD, GB_EDO EDO,");
				sb.append(" BERTHING B WHERE MD.MFT_SEQ_NBR=EDO.MFT_SEQ_NBR AND");
				sb.append(" B.VV_CD=VC.VV_CD AND DN_NBR_PKGS = 0 AND B.SHIFT_IND='1' AND");
				sb.append(" TRANS_DN_NBR_PKGS = 0 AND MD.BL_STATUS='A' AND");
				sb.append(" MD.VAR_NBR = VC.VV_CD ");
				sb.append(" AND EDO.EDO_STATUS='A' ");
				sb.append(" AND EDO.CRG_STATUS IN ('R','L')");
				sb.append(" AND MD.VAR_NBR=:vvcode");
				sb.append(" ORDER BY MD.BL_NBR");
				;
			} else {
				sb.append("SELECT UNIQUE(EDO.EDO_ASN_NBR), MD.BL_NBR, MD.CRG_DES,");
				sb.append(" MD.NBR_PKGS AS MDNBR_PKGS, EDO.NOM_WT, EDO.NOM_VOL, ");
//				sb.append("  EDO.NBR_PKGS,");
				sb.append(" EDO.NBR_PKGS AS EDONBR_PKGS , ");
				sb.append(
						" EDO.CRG_STATUS,EDO.REEXP_REF_NBR, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND");
				sb.append(" FROM VESSEL_CALL VC, MANIFEST_DETAILS MD, GB_EDO EDO,");
				sb.append(" BERTHING B WHERE MD.MFT_SEQ_NBR=EDO.MFT_SEQ_NBR AND");
				sb.append(" MD.VAR_NBR = VC.VV_CD AND ");
				sb.append(" B.VV_CD=VC.VV_CD AND DN_NBR_PKGS = 0 AND B.SHIFT_IND='1' AND");
				sb.append(" TRANS_DN_NBR_PKGS = 0 AND MD.BL_STATUS='A' AND");
				sb.append(" EDO.EDO_STATUS='A' ");
				sb.append(" AND MD.VAR_NBR=:vvcode ");
				sb.append(" AND EDO.CRG_STATUS IN ('R', 'L') AND ");
				sb.append(
						" ( (B.ATU_DTTM IS NULL AND B.GB_COD_DTTM IS NULL )OR (B.ATU_DTTM IS  NULL  AND (SELECT SYSDATE-B.GB_COD_DTTM FROM DUAL)*24 < :cod_closing_time) OR ");
				sb.append(
						" (B.GB_COD_DTTM IS  NULL AND (SELECT SYSDATE-B.ATU_DTTM FROM DUAL)*24 < :cod_closing_time) ");
				sb.append(" OR (SELECT SYSDATE-B.GB_COD_DTTM FROM DUAL)*24 < :cod_closing_time )");
				sb.append(
						" AND (VC.CREATE_CUST_CD=:coCd OR MD.MANIFEST_CREATE_CD =:coCd OR EDO.ADP_CUST_CD =:coCd OR EDO.EDO_CREATE_CD=:coCd)");
				sb.append(" ORDER BY MD.BL_NBR");
			}
			sql = sb.toString();
			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());			
				
				}
			
			log.info(" *** getManifestList SQL *****" + sql);

			if (coCd.equalsIgnoreCase("JP")) {
				paramMap.put("vvcode", vvcode);
			} else {
				paramMap.put("vvcode", vvcode);
				paramMap.put("cod_closing_time", cod_closing_time);
				paramMap.put("coCd", coCd);
			}
			
			log.info(" *** getManifestList SQL *****" + sql);
			log.info(" *** getManifestList params *****" + paramMap.toString());
			rs =  namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				seqno = CommonUtility.deNull(rs.getString(1));
				blno = CommonUtility.deNull(rs.getString(2));
				crgdes = CommonUtility.deNull(rs.getString(3));
				nbrpkgs = CommonUtility.deNull(rs.getString(4));
				gwt = CommonUtility.deNull(rs.getString(5));
				gvol = String.format("%.2f",
						Double.parseDouble(CommonUtility.deNull(rs.getString(6)).trim().equals("") ? "0"
								: CommonUtility.deNull(rs.getString(6))));
				numpkgs = CommonUtility.deNull(rs.getString(7));
				crgstat = CommonUtility.deNull(rs.getString(8));
				reexpnbr = CommonUtility.deNull(rs.getString(9));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				scheme = CommonUtility.deNull(rs.getString("SCHEME"));
				subScheme = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));
				ReExportValueObject mvObj = new ReExportValueObject();
				mvObj.setBlNo(blno);
				mvObj.setCrgDesc(crgdes);
				mvObj.setNoofPkgs(nbrpkgs);
				mvObj.setGrWt(gwt);
				mvObj.setGrMsmt(gvol);
				mvObj.setSeqNo(seqno);
				mvObj.setCrgStatus(crgstat);
				mvObj.setReExpNbr(reexpnbr);
				mvObj.setEdoNbrPkgs(numpkgs);
				mvObj.setTerminal(terminal);
				mvObj.setScheme(scheme);
				mvObj.setSubScheme(subScheme);
				mvObj.setGcOperations(gcOperations);
				manifestList.add(mvObj);
			}

			log.info("END: *** getManifestList Result *****" + manifestList.toString());
			
		} catch (NullPointerException e) {
			log.info("Exception getManifestList : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getManifestList : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getManifestList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getManifestList  DAO  END");
		}

		return manifestList;
	}

	public int getManifestListCount(String vvcode, String coCd, Criteria criteria) throws BusinessException {
	
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
		String atb_closing_time = "0";
		String cod_closing_time = "0";
		int count = 0;
		try {
			log.info("START: getManifestListCount  DAO  Start Obj vvcode:"+ CommonUtility.deNull(vvcode) + " coCd:" + CommonUtility.deNull(coCd) 
			+ " criteria:" + criteria.toString());
			String sql1 = "SELECT CODE_VALUE FROM GB_GLOBAL_DATA  WHERE CODE_STATUS='A' AND CODE_CD='REEXPATB'";
			String sql2 = "SELECT CODE_VALUE FROM GB_GLOBAL_DATA  WHERE CODE_STATUS='A' AND CODE_CD='REEXPCOD'";

			try {
				log.info(" *** getManifestListCount SQL1 *****" + sql1);
				log.info(" *** getManifestListCount params1 *****" + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
				while (rs1.next()) {
					atb_closing_time = CommonUtility.deNull(rs1.getString(1));
				}
				log.info("END: *** getManifestListCount Result *****" + CommonUtility.deNull(atb_closing_time));
			} catch (Exception se) {
				log.info("Exception getManifestListCount: ", se);
				throw new BusinessException("M1004");
			}
			try {
				log.info(" *** getManifestListCount SQL2 *****" + sql2);
				log.info(" *** getManifestListCount params2 *****" + paramMap.toString());
				rs2 =  namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
				while (rs2.next()) {
					cod_closing_time = CommonUtility.deNull(rs2.getString(1));
				}
				log.info("END: *** getManifestListCount Result *****" + CommonUtility.deNull(cod_closing_time));
			} catch (Exception se) {
				log.info("Exception getManifestListCount: ",se);
				throw new BusinessException("M1004");
			}
			// CR-CIM- 0000109
			// Zak:- Must still be allowed to Reexport even after BJ closed
			// - Agent must Reexport Cargo 72 hours before GB_COD
			// - Same for all type of vessel (scheme)
			if (coCd.equalsIgnoreCase("JP")) {
				sb.append("SELECT COUNT(*) ");
				sb.append(" FROM VESSEL_CALL VC, MANIFEST_DETAILS MD, GB_EDO EDO,");
				sb.append(" BERTHING B WHERE MD.MFT_SEQ_NBR=EDO.MFT_SEQ_NBR AND");
				sb.append(" B.VV_CD=VC.VV_CD AND DN_NBR_PKGS = 0 AND B.SHIFT_IND='1' AND");
				sb.append(" TRANS_DN_NBR_PKGS = 0 AND MD.BL_STATUS='A' AND");
				sb.append(" MD.VAR_NBR = VC.VV_CD ");
				sb.append(" AND EDO.EDO_STATUS='A' ");
				sb.append(" AND EDO.CRG_STATUS IN ('R','L')");
				sb.append(" AND MD.VAR_NBR=:vvcode");
				sb.append(" ORDER BY MD.BL_NBR");
				;
			} else {
				sb.append("SELECT COUNT(*) ");
				sb.append(" FROM VESSEL_CALL VC, MANIFEST_DETAILS MD, GB_EDO EDO,");
				sb.append(" BERTHING B WHERE MD.MFT_SEQ_NBR=EDO.MFT_SEQ_NBR AND");
				sb.append(" MD.VAR_NBR = VC.VV_CD AND ");
				sb.append(" B.VV_CD=VC.VV_CD AND DN_NBR_PKGS = 0 AND B.SHIFT_IND='1' AND");
				sb.append(" TRANS_DN_NBR_PKGS = 0 AND MD.BL_STATUS='A' AND");
				sb.append(" EDO.EDO_STATUS='A' ");
				sb.append(" AND MD.VAR_NBR=:vvcode ");
				sb.append(" AND EDO.CRG_STATUS IN ('R', 'L') AND ");
				sb.append(
						" ( (B.ATU_DTTM IS NULL AND B.GB_COD_DTTM IS NULL )OR (B.ATU_DTTM IS  NULL  AND (SELECT SYSDATE-B.GB_COD_DTTM FROM DUAL)*24 < :cod_closing_time) OR ");
				sb.append(
						" (B.GB_COD_DTTM IS  NULL AND (SELECT SYSDATE-B.ATU_DTTM FROM DUAL)*24 < :cod_closing_time) ");
				sb.append(" OR (SELECT SYSDATE-B.GB_COD_DTTM FROM DUAL)*24 < :cod_closing_time )");
				sb.append(
						" AND (VC.CREATE_CUST_CD=:coCd OR MD.MANIFEST_CREATE_CD =:coCd OR EDO.ADP_CUST_CD =:coCd OR EDO.EDO_CREATE_CD=:coCd)");
				sb.append(" ORDER BY MD.BL_NBR");
			}
			sql = sb.toString();

			log.info(" *** getManifestListCount SQL *****" + sql);

			if (coCd.equalsIgnoreCase("JP")) {
				paramMap.put("vvcode", vvcode);
			} else {
				paramMap.put("vvcode", vvcode);
				paramMap.put("cod_closing_time", cod_closing_time);
				paramMap.put("coCd", coCd);
			}
			log.info(" *** getManifestListCount params *****" + paramMap.toString());
			rs =  namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				count = rs.getInt(1);
			}
			log.info(" ***getManifestListCount result *****" + count);
		} catch (NullPointerException e) {
			log.info("Exception getManifestListCount : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getManifestListCount : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getManifestListCount : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getManifestListCount  DAO  END");
		}

		return count;
	}
}
