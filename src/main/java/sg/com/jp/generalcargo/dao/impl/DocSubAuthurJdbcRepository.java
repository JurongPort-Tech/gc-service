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

import sg.com.jp.generalcargo.dao.DocSubAuthurDao;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DocSubAuthorValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Repository("DocSubAuthurJdbcRepository")
public class DocSubAuthurJdbcRepository implements DocSubAuthurDao {

	private static final Log log = LogFactory.getLog(DocSubAuthurJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public List<DocSubAuthorValueObject> getVesselVoy(String cocode) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<DocSubAuthorValueObject> voyList = new ArrayList<DocSubAuthorValueObject>();
		long gcd = 0; // time limit for manifest
		/*
		 * SELECT VESCALL.VV_CD, VESCALL.VSL_NM,, VESCALL.IN_VOY_NBR,
		 * VESCALL.CREATE_CUST_CD FROM VESSEL_CALL VESCALL WHERE VV_STATUS_IND IN
		 * ('AP','AL') AND GB_CLOSE_BJ_IND <> 'Y' AND (NVL((SELECT COUNT(*) FROM
		 * MANIFEST_DETAILS M WHERE M.VAR_NBR=VESCALL.VV_CD AND BL_STATUS='A'),0)) = 0
		 * AND (NVL((SELECT COUNT(*) FROM BK_DETAILS B WHERE B.VAR_NBR=VESCALL.VV_CD AND
		 * BK_STATUS='A'),0)) = 0 AND VESCALL.CREATE_CUST_CD='GSL' ORDER BY
		 * VESCALL.VV_CD DESC, VESCALL.VSL_NM;
		 */
		// SQL changed by Vietnd02::start for GB CR
		if (cocode.equals("JP")) {
			sb.append("SELECT VESCALL.VV_CD, VESCALL.VSL_NM, VESCALL.IN_VOY_NBR,VESCALL.TERMINAL ");
			sb.append(" FROM VESSEL_CALL VESCALL WHERE VESCALL.VV_STATUS_IND IN ('PR','AP','AL','BR','UB')  ");
			sb.append(" AND ((VESCALL.TERMINAL IN 'CT' AND VESCALL.COMBI_GC_OPS_IND IN('Y',null)) ");
			sb.append(" OR VESCALL.TERMINAL NOT IN 'CT') AND");
			sb.append(" VESCALL.GB_CLOSE_BJ_IND <> 'Y' AND VESCALL.GB_CLOSE_SHP_IND <> 'Y'");
			// +" AND (NVL((SELECT COUNT(*) FROM MANIFEST_DETAILS M WHERE" +"
			// M.VAR_NBR=VESCALL.VV_CD AND BL_STATUS='A'),0)) = 0 AND (NVL((SELECT COUNT(*)"
			// +" FROM BK_DETAILS B WHERE B.VAR_NBR=VESCALL.VV_CD AND BK_STATUS='A'),0)) =
			// 0"
			sb.append(" ORDER BY TERMINAL DESC,VESCALL.VSL_NM");
			sql = sb.toString();
		} else {
			/*
			 * sql = "SELECT VESCALL.VV_CD, VESCALL.VSL_NM, VESCALL.IN_VOY_NBR"
			 * +" FROM VESSEL_CALL VESCALL WHERE"
			 * +" VESCALL.VV_STATUS_IND IN ('AP','AL') AND TERMINAL='GB' AND"
			 * +" VESCALL.GB_CLOSE_BJ_IND <> 'Y' AND VESCALL.GB_CLOSE_SHP_IND <> 'Y'"
			 * //+" AND (NVL((SELECT COUNT(*) FROM MANIFEST_DETAILS M WHERE" //
			 * +" M.VAR_NBR=VESCALL.VV_CD  AND BL_STATUS='A'),0)) = 0 AND (NVL((SELECT COUNT(*)"
			 * //
			 * +" FROM BK_DETAILS B WHERE B.VAR_NBR=VESCALL.VV_CD AND BK_STATUS='A'),0)) = 0"
			 * +" AND VESCALL.CREATE_CUST_CD='"+cocode +"' ORDER BY VESCALL.VSL_NM"; sql =
			 * "SELECT IN_VOY_NBR,VSL_NM,VV_CD FROM VESSEL_CALL WHERE VV_STATUS_IND IN ('PR','AP','AL','BR') AND TERMINAL='GB' AND nvl(GB_CLOSE_BJ_IND,'N') <> 'Y' AND nvl(DECLARANT_CUST_CD,CREATE_CUST_CD)='"
			 * + cocode + "' ORDER BY VSL_NM,IN_VOY_NBR";
			 */
			String sql2 = "SELECT CLOSING_TIME FROM CLOSING_TIME WHERE TYPE_CD = 'GCD'";

			try {
				log.info("getVesselVoy sql: " + sql2);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
				// /check in Regional Neighbouring port
				while (rs.next()) {
					gcd = rs.getLong(1);
				}
			} catch (Exception e) {
				log.info("Exception getVesselVoy : ", e);
			}

			// Added vietnd02::start 20-11-09
			gcd = gcd * 1000 * 60 * 60;// convert to milion

			/*
			 * sql = " SELECT DISTINCT IN_VOY_NBR,VSL_NM,VC.VV_CD"
			 * +" FROM VESSEL_CALL VC LEFT OUTER JOIN VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A')"
			 * +" WHERE VV_STATUS_IND IN ('PR','AP','AL') AND TERMINAL='GB' AND nvl(VC.GB_CLOSE_BJ_IND,'N') <> 'Y'"
			 * +" AND (VD.CUST_CD = '" + cocode + "' OR VC.CREATE_CUST_CD = '" + cocode +
			 * "')" +" UNION"
			 */

			sb.setLength(0);
			sb.append("SELECT DISTINCT VC.VV_CD, VC.VSL_NM, VC.IN_VOY_NBR, VC.TERMINAL ");
			sb.append(" FROM BA_CLOSING_TIME BA, VESSEL_CALL VC");
			sb.append(" LEFT OUTER JOIN VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A')");
			sb.append(" LEFT OUTER JOIN BERTHING BT ON (BT.VV_CD = VC.VV_CD)");
			sb.append(
					" WHERE VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB')  AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) ");
			sb.append(" OR VC.TERMINAL NOT IN 'CT') ");
			sb.append(" AND VC.GB_CLOSE_BJ_IND <> 'Y' AND VC.GB_CLOSE_SHP_IND <> 'Y'");
			// 16/06/2011 PCYAP To disable DocSubAuthor for sub operator
			// +" AND VC.VSL_OPR_CD='" + cocode + "'"
			sb.append(" AND ((VC.CREATE_CUST_CD=:cocode  OR VC.VSL_OPR_CD=:cocode) OR VD.CUST_CD = :cocode)");
			sb.append(" AND ( case" + " when BT.ATB_DTTM is null then '0'");
			sb.append(" when (BT.ATU_DTTM is null AND  VC.PORT_FR = BA.PORT_CD AND BT.ATB_DTTM + :gcd");
			sb.append("/(24*60*60*1000) > sysdate) then '0'");
			sb.append(" when (BT.ATU_DTTM is null AND  VC.PORT_FR != BA.PORT_CD AND BT.ATB_DTTM > sysdate) then '0'");
			sb.append(" when (BT.ATU_DTTM is not null AND  VC.PORT_FR = BA.PORT_CD  AND  BT.ATB_DTTM + :gcd");
			sb.append("/(24*60*60*1000) > BT.ATU_DTTM AND BT.ATU_DTTM > sysdate) then '0'");
			sb.append(" when (BT.ATU_DTTM is not null AND  VC.PORT_FR = BA.PORT_CD  AND  BT.ATB_DTTM + :gcd");
			sb.append("/(24*60*60*1000) < BT.ATU_DTTM AND BT.ATB_DTTM + :gcd/(24*60*60*1000) > sysdate) then '0'");
			sb.append(
					" when (BT.ATU_DTTM is not null AND  VC.PORT_FR != BA.PORT_CD AND BT.ATB_DTTM > sysdate) then '0' else '1' end)");
			sb.append(" = '0' ORDER BY VC.TERMINAL DESC,VC.VSL_NM");
			sql = sb.toString();
		}
		// Vietnd02::end
		try {
			log.info("START: getVesselVoy  DAO  Start cocode" + cocode);
			if (!cocode.equals("JP")) {
				paramMap.put("cocode", cocode);
				paramMap.put("gcd", gcd);
			}

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			log.info("getVesselVoy sql: " + sql + " paramMap: " + paramMap);
			String vslName = "";
			String vvCd = "";
			String invoynbr = "";
			String terminal = "";

			while (rs.next()) {
				vvCd = CommonUtility.deNull(rs.getString(1));
				vslName = CommonUtility.deNull(rs.getString(2));
				invoynbr = CommonUtility.deNull(rs.getString(3));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));

				DocSubAuthorValueObject docSubAuthorValueObject = new DocSubAuthorValueObject();
				docSubAuthorValueObject.setVvCd(vvCd);
				docSubAuthorValueObject.setVslName(vslName);
				docSubAuthorValueObject.setInVoyNbr(invoynbr);
				docSubAuthorValueObject.setTerminal(terminal);
				voyList.add(docSubAuthorValueObject);
			}
			log.info("getVesselVoy result: " + voyList.size());
		} catch (Exception e) {
			log.info("Exception getVesselVoy : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselVoy  DAO  END");
		}
		return voyList;
	}

	@Override
	public List<DocSubAuthorValueObject> getVesselList(String vvcode, Criteria criteria) throws BusinessException {
		String sql = "";
		String vvcd = "";
		String vslnm = "";
		String invoynbr = "";
		String outvoynbr = "";
		String deccustcd = "";
		String agentName = "";
		String terminal = "";
		String scheme = "";
		String subScheme = "";
		String gcOperations = "";
		String status = "";
		String userId = "";
		String createDttm = "";
		String agentFullName = "";
		StringBuffer sb = new StringBuffer();
		List<DocSubAuthorValueObject> vesselList = new ArrayList<DocSubAuthorValueObject>();
		DocSubAuthorValueObject docSubAuthorValueObject = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		/*
		 * SELECT VESCALL.VV_CD, VESCALL.VSL_NM,VESCALL_IN_VOY_NBR, VESCALL_OUT_VOY_NBR,
		 * VESCALL.DECLARANT_CUST_CD FROM VESSEL_CALL VESCALL WHERE VV_STATUS_IND IN
		 * ('AP','AL') AND GB_CLOSE_BJ_IND <> 'Y'"
		 */

		sb.append("SELECT VESCALL.VV_CD, VESCALL.VSL_NM,VESCALL.IN_VOY_NBR,");
		sb.append(" VESCALL.OUT_VOY_NBR, VESCALL.DECLARANT_CUST_CD, VESCALL.TERMINAL, VESCALL.SCHEME,");
		sb.append(" VESCALL.COMBI_GC_SCHEME, VESCALL.COMBI_GC_OPS_IND,VD.CUST_CD AS AGTNM, ");
		sb.append(" U.USER_NAME AS USERNAME, VD.CREATE_USER_ID AS USERID, VD.CREATE_DTTM AS CREATEDTTM, VD.STATUS AS STATUS, C.CO_NM AS AGENTNAME FROM ");
		sb.append(" VESSEL_CALL VESCALL LEFT JOIN VESSEL_DECLARANT VD ON (VD.VV_CD= VESCALL.VV_CD) ");
		sb.append(" LEFT JOIN COMPANY_CODE C ON (C.CO_CD = VD.CUST_CD) ");
		sb.append(" LEFT JOIN ADM_USER U ON (U.USER_ACCT = VD.CREATE_USER_ID)  WHERE VESCALL.VV_STATUS_IND IN ");
		// +" ('AP','AL') AND VESCALL.GB_CLOSE_BJ_IND <> 'Y' AND" Changed by Vani -- 25
		// Jan 2010
		sb.append(" ('PR','AP','AL','BR','UB') AND VESCALL.GB_CLOSE_BJ_IND <> 'Y' AND");
		sb.append(" VESCALL.VV_CD=:vvcode ORDER BY VESCALL.VV_CD DESC");
		sql = sb.toString();
		try {
			log.info("START: getVesselList  DAO  Start vvcode" + vvcode);
			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql.toString(), criteria.getStart(), criteria.getLimit());
			}
			paramMap.put("vvcode", vvcode);
			log.info("getVesselList Sql: " + sql + " paramMap: " + paramMap.toString());
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				vvcd = CommonUtility.deNull(rs.getString(1));
				vslnm = CommonUtility.deNull(rs.getString(2));
				invoynbr = CommonUtility.deNull(rs.getString(3));
				outvoynbr = CommonUtility.deNull(rs.getString(4));
				deccustcd = CommonUtility.deNull(rs.getString(5));
				agentName = CommonUtility.deNull(rs.getString("AGTNM"));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				scheme = CommonUtility.deNull(rs.getString("SCHEME"));
				subScheme = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));
				status = CommonUtility.deNull(rs.getString("STATUS"));
				userId = CommonUtility.deNull(rs.getString("USERNAME"));
				createDttm = CommonUtility.deNull(rs.getString("CREATEDTTM"));
				agentFullName = CommonUtility.deNull(rs.getString("AGENTNAME"));
				docSubAuthorValueObject = new DocSubAuthorValueObject();
				docSubAuthorValueObject.setVvCd(vvcd);
				docSubAuthorValueObject.setVslName(vslnm);
				docSubAuthorValueObject.setInVoyNbr(invoynbr);
				docSubAuthorValueObject.setOutVoyNbr(outvoynbr);
				docSubAuthorValueObject.setDocSubAuthor(deccustcd);
				docSubAuthorValueObject.setAgtNm(agentName);
				docSubAuthorValueObject.setTerminal(terminal);
				docSubAuthorValueObject.setScheme(scheme);
				docSubAuthorValueObject.setSubScheme(subScheme);
				docSubAuthorValueObject.setGcOperations(gcOperations);
				docSubAuthorValueObject.setStatus(status);
				docSubAuthorValueObject.setUserId(userId);;
				docSubAuthorValueObject.setCreateDttm(createDttm);;
				docSubAuthorValueObject.setAgentFullName(agentFullName);
				vesselList.add(docSubAuthorValueObject);
			}
			log.info("getVesselList Result: " + vesselList.toString());
		} catch (Exception e) {
			log.info("Exception getVesselList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO  getVesselList;");
		}
		return vesselList;
	}// end of get vessel list method

	// Add by Vietnd02 - to get AuthorParty:: start
	@Override
	public String getAuthorParty(String strvvcd) throws BusinessException {
		String sql = "";
		String result = "";
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getAuthorParty  DAO  Start strvvcd: " + strvvcd);
			sb.append(
					"SELECT (SELECT CCODE.CO_NM FROM COMPANY_CODE CCODE WHERE CCODE.CO_CD = VD.CUST_CD) DOCSUBAUTHOR");
			sb.append(" FROM VESSEL_DECLARANT VD WHERE VD.VV_CD =:strvvcd AND VD.STATUS = 'A'");
			sql = sb.toString();
			paramMap.put("strvvcd", strvvcd);
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			log.info("getAuthorParty sql: " + sql + "paramMap: " + paramMap);
			int i = 0;
			while (rs.next()) {
				i++;
				if (result.length() != 0) {
					String br = "";
					if (i % 2 == 0) {
						br = "<br>";
					}
					result = result + ",&nbsp;&nbsp;&nbsp;&nbsp;" + CommonUtility.deNull(rs.getString("DOCSUBAUTHOR"))
							+ br;
				} else
					result = CommonUtility.deNull(rs.getString("DOCSUBAUTHOR"));
			}
			log.info("getAuthorParty Result: " + result.toString());
		} catch (Exception e) {
			log.info("Exception getAuthorParty : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO  getAuthorParty;");
		}
		return result;
	}

	@Override
	public String checkVesselStatus(String vvcd) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "SELECT VESCALL.VV_STATUS_IND,VESCALL.GB_CLOSE_BJ_IND, VESCALL.GB_CLOSE_SHP_IND FROM VESSEL_CALL VESCALL WHERE  VESCALL.VV_CD=:vvcd";
		// ++ 19.10.2009 changed by vietnd02 for GB CR - don't need to check mft and bk
		/*
		 * sql1="SELECT (NVL((SELECT COUNT(*) FROM MANIFEST_DETAILS M WHERE"
		 * +" M.VAR_NBR=VESCALL.VV_CD AND BL_STATUS='A'),0)) MFTCOUNT,"
		 * +" (NVL((SELECT COUNT(*) FROM BK_DETAILS B WHERE"
		 * +" B.VAR_NBR=VESCALL.VV_CD AND BK_STATUS='A'),0)) BKCOUNT"
		 * +" FROM  VESSEL_CALL VESCALL WHERE VESCALL.VV_CD='"+vvcd+"'";
		 */
		String strnewvvstatusind = "";
		String gb_close_bj_ind = "";
		String gb_close_shp_ind = "";
		// String mft_count="0"; // String bk_count="0";
		try {
			log.info("START: checkVesselStatus  DAO  Start vvcd" + vvcd);
			paramMap.put("vvcd", vvcd);

			log.info("checkVesselStatus Sql: " + sql + "paramMap: " + paramMap);
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				strnewvvstatusind = CommonUtility.deNull(rs.getString("VV_STATUS_IND"));
				gb_close_bj_ind = CommonUtility.deNull(rs.getString("GB_CLOSE_BJ_IND"));
				gb_close_shp_ind = CommonUtility.deNull(rs.getString("GB_CLOSE_SHP_IND"));
			}
			if (strnewvvstatusind.equalsIgnoreCase("CL")) {
				throw new BusinessException("M20850");
			}
			if (strnewvvstatusind.equalsIgnoreCase("CX")) {
				throw new BusinessException("M20855");
			}
			/*
			 * Statement sqlstmt1 = con.createStatement(); ResultSet rs1 =
			 * sqlstmt1.executeQuery(sql1); while(rs1.next()) { mft_count =
			 * CommonUtility.deNull(rs1.getString(1)); bk_count =
			 * CommonUtility.deNull(rs1.getString(2)); } System.out.println("**********2" +
			 * mft_count + ": " + bk_count); if (((mft_count.trim()).equalsIgnoreCase(""))||
			 * mft_count==null ) { mft_count="0"; } if
			 * (((bk_count.trim()).equalsIgnoreCase(""))|| bk_count==null ) { bk_count="0";
			 * } if (!(mft_count.equalsIgnoreCase("0"))) { throw new
			 * BusinessException("M20865"); } if (!(bk_count.equalsIgnoreCase("0"))) { throw
			 * new BusinessException("M20870"); }
			 */
			// -- 19.10.2009 changed by vietnd02 for GB CR - don't need to check mft and bk
			log.info("checkVesselStatus Result: " + strnewvvstatusind + "|" + gb_close_bj_ind + "|" + gb_close_shp_ind);
		} catch (Exception e) {
			log.info("Exception checkVesselStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO  checkVesselStatus;");
		}
		return strnewvvstatusind + "|" + gb_close_bj_ind + "|" + gb_close_shp_ind;

	}

	/**
	 * Returns Customer Code For given TDB CR Number
	 */
	@Override
	public String getCustomerNbr(String strtdbnbr) throws BusinessException {
		String sql = "SELECT CUST_CD FROM CUSTOMER WHERE (UPPER(TDB_CR_NBR)=UPPER(:strtdbnbr) OR UPPER(UEN_NBR)=UPPER(:strtdbnbr))";
		String strnewnbr = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCustomerNbr  DAO  Start strtdbnbr: " + strtdbnbr);
			paramMap.put("strtdbnbr", strtdbnbr);
			log.info(" *** getCustomerNbr SQL *****" + sql + " paramMap: " + paramMap);
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				strnewnbr = CommonUtility.deNull(rs.getString("CUST_CD"));
			}
			log.info("getCustomerNbr Result: " + strnewnbr);
		} catch (Exception e) {
			log.info("Exception getCustomerNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO  getCustomerNbr;");
		}
		return strnewnbr;
	}

	@Override
	// changed vietnd02 16/10 - add two parameters (- String vsName, String
	// inVoyNbr)
	public void updateADSDetails(String strcustcd, String struserid, String strvvcd, List<String> custcdlist,
			String vsName, String inVoyNbr) throws BusinessException {
		String sql2 = "";
		String logStatusGlobal = "N";
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		// -- Comment by FPT.Thai
		// sql2= "UPDATE VESSEL_CALL SET " +"DECLARANT_CUST_CD ='"+strcustcd +"',
		// LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = '"+struserid +"' WHERE
		// VV_CD='"+strvvcd+"'";
		sql2 = "UPDATE VESSEL_CALL SET LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :struserid WHERE VV_CD=:strvvcd";
		String strtransnbr = "0";
		String sqllog = "SELECT MAX(TRANS_NBR) FROM ADS_TRANS  WHERE VV_CD=:strvvcd";
		String strUpdatetrans = "";
		try {
			log.info("START: updateADSDetails  DAO  Start strcustcd: " + strcustcd +" strvvcd:"+strvvcd +" custcdlist:"+custcdlist 
					+" vsName:"+vsName +" inVoyNbr:"+inVoyNbr);
			paramMap.put("strvvcd", strvvcd);
			paramMap.put("struserid", struserid);
			log.info("updateADSDetails Sql: " + sql2 + " paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(sql2, paramMap);
			if (count == 0) {
				throw new BusinessException("M1007");
			}
			// -- Add by FPT.Thai
			if (custcdlist == null || custcdlist.isEmpty()) {
				insertDeclarant(strvvcd, strcustcd, struserid);
			} else {
				delDocSubAuthor(strvvcd, struserid, custcdlist, vsName, inVoyNbr);// vietnd02
			}
			// -- End of Add by FPT.Thai

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				try {
					log.info("updateADSDetails Sql: " + sqllog + " paramMap: " + paramMap);
					SqlRowSet rslog = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
					while (rslog.next()) {
						strtransnbr = CommonUtility.deNull(rslog.getString(1));
					}
					if (strtransnbr.equalsIgnoreCase("")) {
						strtransnbr = "0";
					} else {
						strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
					}
				} catch (Exception e) {
					log.info("Exception updateADSDetails : ", e);
					throw new BusinessException("M1004");
				}

				sb.append("INSERT INTO ADS_TRANS (TRANS_NBR,VV_CD, DECLARANT_CUST_CD , LAST_MODIFY_DTTM,");
				sb.append("LAST_MODIFY_USER_ID) VALUES (:strtransnbr,:strvvcd,:strcustcd, sysdate,:struserid) ");
				strUpdatetrans = sb.toString();
				paramMap.put("strtransnbr", strtransnbr);
				paramMap.put("strvvcd", strvvcd);
				paramMap.put("strcustcd", strcustcd);
				paramMap.put("struserid", struserid);
				log.info("updateADSDetails Sql: " + strUpdatetrans + " paramMap: " + paramMap);
				int count1 = namedParameterJdbcTemplate.update(strUpdatetrans, paramMap);
				if (count1 == 0) {
					throw new BusinessException("M1007");
				}
			}

			log.info("updateADSDetails Result: " + count);
		} catch (BusinessException be) {
			log.info("Exception updateADSDetails : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception updateADSDetails : ", e);
			throw new BusinessException("Updating record failed.");
		} finally {
			log.info("END: DAO  updateADSDetails;");
		}
	}

	private void insertDeclarant(String vvcd, String custcd, String struserid) throws BusinessException {
		// --Check for existing Declarant
		if (isExistDeclarant(vvcd, custcd)) {
			// -- Declarant is existing.
			throw new BusinessException(ConstantUtil.ErrorMsg_Authorized_Parties_is_existing);
		}
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		sb.append(
				"INSERT INTO VESSEL_DECLARANT (VV_CD, CUST_CD, STATUS, CREATE_DTTM, CREATE_USER_ID, LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID) ");
		sb.append("VALUES (:vvcd, :custcd, 'A', sysdate, :struserid , sysdate, :struserid)");
		String sql = sb.toString();
		try {
			log.info("START: insertDeclarant  DAO  Start vvcd: " + vvcd + ",custcd: " + custcd + ",struserid: "
					+ struserid);
			paramMap.put("vvcd", vvcd);
			paramMap.put("custcd", custcd);
			paramMap.put("struserid", struserid);
			log.info("insertDeclarant Sql: " + sql + " paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(sql, paramMap);
			if (count == 0) {
				throw new BusinessException(ConstantUtil.ErrorMsg_Inserting_record_failed);
			}
			log.info("insertDeclarant Result: " + count);
		} catch (BusinessException be) {
			log.info("Exception insertDeclarant : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception insertDeclarant : ", e);
			throw new BusinessException(ConstantUtil.ErrorMsg_Inserting_record_failed);
		} finally {
			log.info("END: DAO  insertDeclarant;");
		}

	}

	private boolean isExistDeclarant(String vvcd, String customercode) throws BusinessException {
		boolean ret = true;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "SELECT VV_CD FROM VESSEL_DECLARANT WHERE VV_CD = :vvcd AND CUST_CD =:customercode AND STATUS = 'A'";
		try {
			log.info("START: isExistDeclarant  DAO  Start vvcd: " + vvcd + ",customercode: " + customercode);
			paramMap.put("vvcd", vvcd);
			paramMap.put("customercode", customercode);
			log.info("isExistDeclarant Sql: " + sql + " paramMap: " + paramMap);
			// Get information from VESSEL_DECLARANT
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				log.info("Declarant is existing.");
				ret = true;
			} else {
				ret = false;
			}
			log.info("isExistDeclarant Result: " + ret);
		} catch (Exception e) {
			log.info("Exception isExistDeclarant : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO  isExistDeclarant;");
		}
		return ret;
	}

	// -- Add by FPT.Thai
	// changed vietnd02 16/10 - add two parameters (- String vsName, String
	// inVoyNbr)
	private void delDocSubAuthor(String vvcd, String userid, List<String> custcdlist, String vsName, String inVoyNbr)
			throws BusinessException {
		int numOfDsa = custcdlist.size();
		log.info("Num of DocSubAuthor: " + numOfDsa);
		// -- Set of cuscd
		String custcdlist_str = "";
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		// -- Check for Delete list of DocSubAuthor then Init Set of custcd
		for (int i = 0; i < numOfDsa; i++) {
			String cuscd = (String) custcdlist.get(i);
			log.info("cuscd: " + cuscd);
			// -- Check DocSubAuthor Del Allowed
			// vietnd02
			isDocSubAuthorDelAllowed(vvcd, cuscd, vsName, inVoyNbr); // -- If check OK, not throw exception
			// -- Init Set of cuscd
			custcdlist_str = custcdlist_str + "";
			custcdlist_str = custcdlist_str + cuscd;
			log.info("DocSubAuthor: " + custcdlist_str);
			if (i < numOfDsa - 1) {
				custcdlist_str = custcdlist_str + ", ";
			}
			// else {
			// custcdlist_str = custcdlist_str + "')";
			// }
		}

		sb.append("UPDATE VESSEL_DECLARANT SET STATUS = 'I',");
		sb.append(" LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID =:userid ");
		sb.append(" WHERE VV_CD=:vvcd AND STATUS = 'A'");
		sb.append(" AND CUST_CD IN ( ");
		for (int k = 0; k < numOfDsa; k++) {
			if (k != (numOfDsa - 1)) {
				sb.append(":custcdlist_str" + k + ",");
			}
		}
		sb.append(":custcdlist_str )");
		String sql2 = sb.toString();
		log.info("delDocSubAuthor Sql: " + sql2 + " paramMap: " + paramMap);
		try {
			log.info("START: delDocSubAuthor  DAO  Start vvcd:" + vvcd + ",userid:" + userid + ",custcdlist:"
					+ custcdlist + ",vsName:" + vsName + ",inVoyNbr:" + inVoyNbr);
			paramMap.put("vvcd", vvcd);
			for (int k = 0; k < numOfDsa; k++) {
				if (k != (numOfDsa - 1)) {
					paramMap.put("custcdlist_str" + k, (String) custcdlist.get(k));
				}
				paramMap.put("custcdlist_str", (String) custcdlist.get(k));
			}
			paramMap.put("userid", userid);
			log.info("delDocSubAuthor Sql: " + sql2 + " paramMap: " + paramMap);
			// -- Delete list of DocSubAuthor from VESSEL_DECLARANT table
			int count2 = namedParameterJdbcTemplate.update(sql2, paramMap);
			if (count2 == 0) {
				throw new BusinessException(ConstantUtil.ErrorMsg_Delete_declarant_failed);
			}
			log.info("delDocSubAuthor Result: " + count2);
		} catch (BusinessException be) {
			log.info("Exception delDocSubAuthor : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception delDocSubAuthor : ", e);
			throw new BusinessException(ConstantUtil.ErrorMsg_Delete_record_failed);
		} finally {
			log.info("END: DAO  delDocSubAuthor;");
		}

	}

	// -- Check for DocSubAuthor del allowed
	// changed vietnd02 16/10 - add two parameters (- String vsName, String
	// inVoyNbr)
	private void isDocSubAuthorDelAllowed(String vvcd, String custcd, String vsName, String inVoyNbr)
			throws BusinessException {
		try {
			log.info("START: isDocSubAuthorDelAllowed  DAO  Start vvcd: " + vvcd + ",custcd: " + custcd + ",vsName: "
					+ vsName + ",inVoyNbr:" + inVoyNbr);
			// -- Check for existing manifest
			if (chkManifest(custcd, vsName, inVoyNbr)) {
				throw new BusinessException(ConstantUtil.ErrorMsg_Authorised_Party_Cannot_deleted_manifest);
			}
			// -- Check for existing booking ref
			if (chkBkref(vvcd, custcd)) {
				throw new BusinessException(ConstantUtil.ErrorMsg_Authorised_Party_Cannot_deleted_bookref);
			}
		} catch (BusinessException be) {
			log.info("Exception isDocSubAuthorDelAllowed : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception isDocSubAuthorDelAllowed : ", e);
			throw new BusinessException(ConstantUtil.ErrorMsg_Delete_record_failed);
		} finally {
			log.info("END: DAO  isDocSubAuthorDelAllowed;");
		}
		return;
	}

	// -- Check for existing manifest
	// changed by vietnd02 16/10 - to check manifest exist, add two parameters (-
	// String vsName, String inVoyNbr)
	// remove String vvcd
	private boolean chkManifest(String cuscd, String vsName, String inVoyNbr) throws BusinessException {
		log.info("chkManifest");
		boolean ret = false;// changed vietnd02 16/10
		String sql = "";
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		/*
		 * sql = "SELECT LAST_MODIFY_USER_ID FROM MANIFEST_DETAILS WHERE" +
		 * " VAR_NBR ='" + vvcd + "'" + " AND LAST_MODIFY_USER_ID = '" + cuscd + "'";
		 */
		try {
			log.info(
					"START: chkManifest  DAO  Start cuscd: " + cuscd + ",vsName: " + vsName + ",inVoyNbr: " + inVoyNbr);
			sb.append("SELECT CUST_CD FROM LOGON_ACCT");
			sb.append(" WHERE LOGIN_ID IN (SELECT b.LAST_MODIFY_USER_ID");
			sb.append(" FROM VESSEL_CALL a, MANIFEST_DETAILS b WHERE a.VV_CD = b.VAR_NBR and a.VSL_NM =:vsName ");
			sb.append(" and a.IN_VOY_NBR = :inVoyNbr)");
			sql = sb.toString();
			paramMap.put("vsName", vsName);
			paramMap.put("inVoyNbr", inVoyNbr);
			log.info("chkManifest Sql: " + sql + " paramMap: " + paramMap);
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				String custCD = rs.getString("CUST_CD");
				if (custCD.equals(cuscd)) {
					ret = true;
					break;
				}
			}
			log.info("chkManifest Result: " + ret);
		} catch (Exception e) {
			log.info("Exception chkManifest : ", e);
		} finally {
			log.info("END: DAO  chkManifest;");
		}
		return ret;
	}

	// -- Check for existing booking ref
	private boolean chkBkref(String vvcd, String cuscd) throws BusinessException {
		log.info("chkBkref");
		boolean ret = true;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "SELECT BK_CREATE_CD FROM BK_DETAILS WHERE  VAR_NBR =:vvcd AND BK_CREATE_CD = :cuscd ";
		try {
			log.info("START: chkBkref  DAO  Start cuscd:" + cuscd + ",vvcd:" + vvcd);
			paramMap.put("vvcd", vvcd);
			paramMap.put("cuscd", cuscd);
			log.info("chkBkref Sql: " + sql + " paramMap: " + paramMap);
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				ret = true;
			} else {
				ret = false;
			}
			log.info("chkBkref Result: " + ret);
		} catch (Exception e) {
			log.info("Exception chkBkref : ", e);
		} finally {
			log.info("END: DAO  chkBkref;");
		}
		return ret;
	}

	@Override
	public List<DocSubAuthorValueObject> getVesselDetails(String strvvcd) throws BusinessException {
		String sql = "";
		String sql1 = "";
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		/*
		 * Add comment by FPT.Thai - Sep 29 2009 CR.BPR and WWL Documentation
		 * Enhancement URS_Clarification
		 */
		// sql="SELECT VESCALL.VV_CD, VESCALL.VSL_NM,
		// VESCALL.IN_VOY_NBR,VESCALL.OUT_VOY_NBR,"
		// +" to_char(VESCALL.VSL_ETD_DTTM, 'DD/MM/YYYY HH24MI'),"
		// +" to_char(VESCALL.VSL_BERTH_DTTM, 'DD/MM/YYYY HH24MI'),"
		// +" (SELECT CCODE.CO_NM FROM COMPANY_CODE CCODE WHERE"
		// +" CCODE.CO_CD=VESCALL.DECLARANT_CUST_CD) AGTNM, (SELECT"
		// +" CCODE.CO_NM FROM COMPANY_CODE CCODE WHERE CCODE.CO_CD= "
		// +" VESCALL.CREATE_CUST_CD) AUTHORNM, (SELECT
		// DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) "
		// +" FROM CUSTOMER WHERE CUST_CD=VESCALL.CREATE_CUST_CD) AUTHORNBR,(SELECT"
		// +" CCODE.CO_NM FROM COMPANY_CODE CCODE WHERE CCODE.CO_CD= "
		// +" VESCALL.DECLARANT_CUST_CD) DOCSUBAUTHOR, (SELECT
		// DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) "
		// +" FROM CUSTOMER WHERE CUST_CD=VESCALL.DECLARANT_CUST_CD) DOCSUBAUTHORNBR"
		// +" FROM VESSEL_CALL VESCALL WHERE VESCALL.VV_STATUS_IND IN"
		// +" ('AP','AL') AND VESCALL.GB_CLOSE_BJ_IND <> 'Y' AND"
		// +" VESCALL.VV_CD='"+strvvcd+"'";
		sb.append("SELECT VESCALL.VV_CD, VESCALL.VSL_NM, VESCALL.IN_VOY_NBR,VESCALL.OUT_VOY_NBR,");
		sb.append(" to_char(VESCALL.VSL_ETD_DTTM, 'DD/MM/YYYY HH24MI'),");
		sb.append(" to_char(VESCALL.VSL_BERTH_DTTM, 'DD/MM/YYYY HH24MI'),");
		sb.append(" (SELECT CCODE.CO_NM FROM COMPANY_CODE CCODE WHERE CCODE.CO_CD=VESCALL.CREATE_CUST_CD) AUTHORNM,");
		sb.append(" (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR)  ");
		sb.append(" FROM CUSTOMER WHERE CUST_CD=VESCALL.CREATE_CUST_CD) AUTHORNBR,");
		sb.append(" VESCALL.TERMINAL, VESCALL.SCHEME,VESCALL.COMBI_GC_SCHEME, VESCALL.COMBI_GC_OPS_IND ");
		// + " FROM VESSEL_CALL VESCALL WHERE VESCALL.VV_STATUS_IND IN ('AP','AL')"
		// Changed by Vani - 25 Jan 2010
		sb.append(" FROM VESSEL_CALL VESCALL WHERE VESCALL.VV_STATUS_IND IN ('PR','AP','AL','BR','UB')");
		sb.append(" AND VESCALL.GB_CLOSE_BJ_IND <> 'Y'");
		sb.append(" AND VESCALL.VV_CD=:strvvcd ");
		sql = sb.toString();

		sb.setLength(0);
		sb.append("SELECT  (SELECT VD.CUST_CD FROM COMPANY_CODE CCODE WHERE CCODE.CO_CD = VD.CUST_CD) AGTNM,");// corect
																												// by
																												// VietNd02
		sb.append(" (SELECT CCODE.CO_NM FROM COMPANY_CODE CCODE WHERE CCODE.CO_CD = VD.CUST_CD) DOCSUBAUTHOR,");
		sb.append(" (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) FROM CUSTOMER ");
		sb.append(" WHERE CUST_CD = VD.CUST_CD) DOCSUBAUTHORNBR");
		sb.append(" FROM VESSEL_DECLARANT VD  WHERE VD.VV_CD =:strvvcd AND VD.STATUS = 'A'");
		sql1 = sb.toString();

		/* End of Add comment by FPT.Thai - Sep 29 2009 */
		List<DocSubAuthorValueObject> docsubauthorvector = new ArrayList<DocSubAuthorValueObject>();
		try {
			log.info("START: getVesselDetails  DAO  Start strvvcd:" + strvvcd);
			paramMap.put("strvvcd", strvvcd);
			log.info("getVesselDetails Sql: " + sql + " paramMap: " + paramMap);
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				DocSubAuthorValueObject docSubAuthorValueObject = new DocSubAuthorValueObject();
				String vvcd = CommonUtility.deNull(rs.getString(1));
				String vslnm = CommonUtility.deNull(rs.getString(2));
				String invoynbr = CommonUtility.deNull(rs.getString(3));
				String outvoynbr = CommonUtility.deNull(rs.getString(4));
				String etudttm = CommonUtility.deNull(rs.getString(5));
				String btrdttm = CommonUtility.deNull(rs.getString(6));
				// String agtnm=CommonUtility.deNull(rs.getString(7));//-- Add comment
				// by FPT.Thai - Sep 29 2009
				String authornm = CommonUtility.deNull(rs.getString(7)); // -- Updated by FPT.Thai - Sep 29 2009
				String authornbr = CommonUtility.deNull(rs.getString(8)); // -- Updated by FPT.Thai - Sep 29 2009
				// String docsubauthor=CommonUtility.deNull(rs.getString(10));//-- Add
				// comment by FPT.Thai - Sep 29 2009
				// String docsubauthornbr=CommonUtility.deNull(rs.getString(11));//--
				// Add comment by FPT.Thai - Sep 29 2009
				String terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				String scheme = CommonUtility.deNull(rs.getString("SCHEME"));
				String subScheme = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				String gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));
				docSubAuthorValueObject.setTerminal(terminal);
				docSubAuthorValueObject.setScheme(scheme);
				docSubAuthorValueObject.setSubScheme(subScheme);
				docSubAuthorValueObject.setGcOperations(gcOperations);

				docSubAuthorValueObject.setVvCd(vvcd);
				docSubAuthorValueObject.setVslName(vslnm);
				docSubAuthorValueObject.setInVoyNbr(invoynbr);
				docSubAuthorValueObject.setOutVoyNbr(outvoynbr);
				docSubAuthorValueObject.setEtuDttm(etudttm);
				docSubAuthorValueObject.setBtrDttm(btrdttm);
				// docSubAuthorValueObject.setAgtNm(agtnm); //-- Add comment by FPT.Thai - Sep
				// 29 2009
				docSubAuthorValueObject.setAuthorNm(authornm);
				docSubAuthorValueObject.setAuthorNbr(authornbr);
				// docSubAuthorValueObject.setDocSubAuthor(docsubauthor); //-- Add comment by
				// FPT.Thai - Sep 29 2009
				// docSubAuthorValueObject.setDocSubAuthorNbr(docsubauthornbr); //-- Add comment
				// by FPT.Thai - Sep 29 2009

				docsubauthorvector.add(docSubAuthorValueObject); // -- Added by FPT.Thai - Sep 29 2009
			}
			// -- Add by FPT.Thai
			paramMap.put("strvvcd", strvvcd);
			SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
			log.info("getVesselDetails Sql: " + sql1 + " paramMap: " + paramMap);
			while (rs1.next()) {
				DocSubAuthorValueObject docSubAuthorValueObject = new DocSubAuthorValueObject();
				String agtnm = CommonUtility.deNull(rs1.getString("AGTNM"));
				String docsubauthor = CommonUtility.deNull(rs1.getString("DOCSUBAUTHOR"));
				String docsubauthornbr = CommonUtility.deNull(rs1.getString("DOCSUBAUTHORNBR"));

				// -- Set data into object
				docSubAuthorValueObject.setAgtNm(agtnm);
				docSubAuthorValueObject.setDocSubAuthor(docsubauthor);
				docSubAuthorValueObject.setDocSubAuthorNbr(docsubauthornbr);

				docsubauthorvector.add(docSubAuthorValueObject);
			}
			log.info("getVesselDetails Result: " + docsubauthorvector.size());
		} catch (Exception e) {
			log.info("Exception getVesselDetails : ", e);
			throw new BusinessException("M1004");
		} finally {
			log.info("END: DAO  getVesselDetails;");
		}
		return docsubauthorvector;
	}

	@Override
	public int getVesselListCount(String vvcode, Criteria criteria) throws BusinessException {
		String sql = "";
		int total = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(*) FROM VESSEL_CALL VESCALL LEFT JOIN VESSEL_DECLARANT VD");
		sb.append("  ON (VD.VV_CD= VESCALL.VV_CD) WHERE VESCALL.VV_STATUS_IND IN");
		sb.append(" ('PR','AP','AL','BR','UB') AND VESCALL.GB_CLOSE_BJ_IND <> 'Y' AND");
		sb.append(" VESCALL.VV_CD=:vvcode ORDER BY VESCALL.VV_CD DESC");
		sql = sb.toString();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getVesselListCount  DAO  Start vvcode:" + vvcode + ",criteria:" + criteria.toString());
			paramMap.put("vvcode", vvcode);
			log.info("getVesselListCount Sql: " + sql + " paramMap: " + paramMap);
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				total = rs.getInt(1);
			}
			log.info("getVesselListCount Result: " + total);
		} catch (Exception e) {
			log.info("Exception getVesselListCount : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO  getVesselListCount");
		}
		return total;
	}// end of get vessel list method

}
