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

import sg.com.jp.generalcargo.dao.ClvsOpsRepo;
import sg.com.jp.generalcargo.domain.OpsValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("clvsOpsJdbcRepo")
public class ClvsOpsJdbcRepo implements ClvsOpsRepo {

	private static final Log log = LogFactory.getLog(ClvsOpsJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// ejb.sessionBeans.ops.Clvs-->ClvsOpsEJB
	@Override
	public OpsValueObject getVessels(OpsValueObject opsValueObject) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getVessels  DAO  Start Obj OpsValueObject"
					+ (opsValueObject != null ? opsValueObject.toString() : null));

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	DISTINCT RECS.*, ");
			sb.append("CASE ");
			sb.append("		WHEN RECS.GB_CLOSE_SHP_IND = 'N' THEN ");
			sb.append("	CASE ");
			sb.append("			WHEN ( ");
			sb.append("			SELECT ");
			sb.append("				COUNT(*) ");
			sb.append("			FROM ");
			sb.append("				ESN E ");
			sb.append("			WHERE ");
			sb.append("				E.OUT_VOY_VAR_NBR = RECS.VV_CD ");
			sb.append("				AND E.ESN_STATUS = 'A' ");
			sb.append("				AND E.TRANS_TYPE IN ('A', 'E', 'S', 'C')) = 0 THEN 'NA' ");
			sb.append("			ELSE 'N' ");
			sb.append("		END ");
			sb.append("		ELSE 'Y' ");
			sb.append("	END AS CLOSE_SHIPMENT, ");
			sb.append("CASE ");
			sb.append("		WHEN RECS.GB_CLOSE_BJ_IND = 'N' THEN ");
			sb.append("	CASE ");
			sb.append("			WHEN ( ");
			sb.append("			SELECT ");
			sb.append("				COUNT(*) ");
			sb.append("			FROM ");
			sb.append("				MANIFEST_DETAILS MD ");
			sb.append("			WHERE ");
			sb.append("				MD.VAR_NBR = RECS.VV_CD ");
			sb.append("				AND MD.BL_STATUS = 'A') = 0 THEN 'NA' ");
			sb.append("			ELSE 'N' ");
			sb.append("		END ");
			sb.append("		ELSE 'Y' ");
			sb.append("	END AS CLOSE_BJ ");
			sb.append("FROM ");
			sb.append("	( ");
			sb.append("	SELECT ");
			sb.append("		V.VV_CD, ");
			sb.append("		V.VSL_NM, ");
			sb.append("		V.OUT_VOY_NBR, ");
			sb.append("		B.ATU_DTTM, ");
			sb.append("		B.BERTH_NBR, ");
			sb.append("		V.VV_CLOSE_DTTM, ");
			sb.append("		V.TERMINAL, ");
			sb.append("	CASE ");
			sb.append("			WHEN V.COMBI_GC_OPS_IND = 'Y' THEN (V.SCHEME || '/' || NVL(V.COMBI_GC_SCHEME, '')) ");
			sb.append("			ELSE V.SCHEME ");
			sb.append("		END AS SCHEME, ");
			sb.append("		V.GB_CLOSE_BJ_IND, ");
			sb.append("		V.GB_CLOSE_SHP_IND ");
			sb.append("	FROM ");
			sb.append("		VESSEL_CALL V, ");
			sb.append("		BERTHING B ");
			sb.append("	WHERE ");
			sb.append("		(V.VV_STATUS_IND IN ('UB', 'BR') ");
			sb.append("		OR (V.VV_STATUS_IND = 'CL' ");
			sb.append("		AND V.VV_CLOSE_DTTM > SYSDATE-5)) ");
			sb.append("		AND V.TERMINAL = 'CT' ");
			sb.append("		AND V.VV_CD = B.VV_CD ");
			sb.append("	ORDER BY ");
			sb.append("		B.SHIFT_IND DESC) RECS ");
			sb.append("ORDER BY ");
			sb.append("	TERMINAL DESC, ");
			sb.append("	VSL_NM, ");
			sb.append("	ATU_DTTM");

			log.info(" *** getVessels SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			List<String[]> dataRow = new ArrayList<String[]>();
			while (rs.next()) {
				String[] rec = new String[10];
				rec[0] = "";
				rec[1] = CommonUtility.deNull(rs.getString("VSL_NM"));
				rec[2] = CommonUtility.deNull(rs.getString("OUT_VOY_NBR"));
				rec[3] = CommonUtility.deNull(rs.getString("SCHEME"));
				rec[4] = CommonUtility.deNull(rs.getString("CLOSE_SHIPMENT"));
				rec[5] = CommonUtility.deNull(rs.getString("CLOSE_BJ"));
				rec[6] = CommonUtility.deNull(CommonUtility.parseDateToFmtStr(rs.getTimestamp("ATU_DTTM")));
				rec[7] = CommonUtility.deNull(CommonUtility.parseDateToFmtStr(rs.getTimestamp("VV_CLOSE_DTTM")));
				rec[8] = CommonUtility.deNull(rs.getString("BERTH_NBR"));
				rec[9] = CommonUtility.deNull(rs.getString("VV_CD"));
				dataRow.add(rec);
			}
			if (dataRow.isEmpty()) {
				opsValueObject.setTableData(null);
			} else {
				opsValueObject.setTableData((String[][]) dataRow.toArray(new String[0][0]));
			}
		} catch (NullPointerException e) {
			log.error("Exception: getVessels ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getVessels ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVessels  DAO Result" + (opsValueObject != null ? opsValueObject.toString() : null));
		}
		return opsValueObject;
	}

	// ejb.sessionBeans.ops.Clvs-->ClvsOpsEJB
	@Override
	public OpsValueObject getVesselInfo(String vvCode, OpsValueObject opsValueObject) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		java.sql.Timestamp atu = null, atb = null, firstDisc = null, lastDisc = null, firstLoad = null, lastLoad = null;
		String vslStatus = "";
		java.sql.Timestamp colDisc = null, colLoad = null, initLastDisc = null, initLastLoad = null;
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: getVesselInfo  DAO  Start vvCode " + vvCode + " opsValueObject"
					+ (opsValueObject != null ? opsValueObject.toString() : null));

			sb.append("SELECT ");
			sb.append("	V.VV_STATUS_IND, ");
			sb.append("	B.COD_DTTM, ");
			sb.append("	B.COL_DTTM ");
			sb.append("FROM ");
			sb.append("	VESSEL_CALL V, ");
			sb.append("	BERTHING B ");
			sb.append("WHERE ");
			sb.append("	V.VV_CD = :vvCode ");
			sb.append("	AND V.VV_CD = B.VV_CD ");
			sb.append("	AND B.SHIFT_IND = '1'");
			paramMap.put("vvCode", vvCode);

			log.info(" *** getVesselInfo SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				vslStatus = rs.getString("VV_STATUS_IND");
				colDisc = rs.getTimestamp("COD_DTTM");
				colLoad = rs.getTimestamp("COL_DTTM");
			}

			if (vslStatus.equals("UB")) {
				if (colDisc == null) {

					sb = new StringBuffer();
					sb.append("SELECT ");
					sb.append("	MAX(JOB_COMPLETE_DTTM) LAST_DISC ");
					sb.append("FROM ");
					sb.append("	JOB_SEQ ");
					sb.append("WHERE ");
					sb.append("	VV_CD = :vvCode ");
					sb.append("	AND JOB_STATUS = 'X' ");
					sb.append("	AND HDL_MODE IN ('1', '4')");

					paramMap.put("vvCode", vvCode);

					log.info(" *** getVesselInfo SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					if (rs.next()) {
						initLastDisc = rs.getTimestamp("LAST_DISC");
					}
				}

				// log.info("Retrieving last discharge time sqlGetLastDisc1*********
				// :"+initLastDisc);
				if (colLoad == null) {
					log.info("Retrieving last discharge time sqlGetLastLoad*********");
					sb = new StringBuffer();
					sb.append("SELECT ");
					sb.append("	MAX(JOB_COMPLETE_DTTM) LAST_LOAD ");
					sb.append("FROM ");
					sb.append("	JOB_SEQ ");
					sb.append("WHERE ");
					sb.append("	VV_CD = :vvCode ");
					sb.append("	AND JOB_STATUS = 'X' ");
					sb.append("	AND HDL_MODE IN ('2', '5')");

					paramMap.put("vvCode", vvCode);

					log.info(" *** getVesselInfo SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					if (rs.next()) {
						initLastLoad = rs.getTimestamp("LAST_LOAD");
					}
				}

				// log.info("Retrieving last discharge time sqlGetLastLoad*********
				// :"+initLastLoad);
				if (initLastDisc != null || initLastLoad != null) {
					// log.info("Updating the last disc and last load sqlUpdateDiscLoad*********");
					if (colDisc != null) {
						initLastDisc = colDisc;
					}

					if (colLoad != null) {
						initLastLoad = colLoad;
					}

					sb = new StringBuffer();
					sb.append("UPDATE ");
					sb.append("	BERTHING ");
					sb.append("SET ");
					sb.append("	COD_DTTM = :initLastDisc , ");
					sb.append("	COL_DTTM = :initLastLoad ");
					sb.append("WHERE ");
					sb.append("	VV_CD = :vvCode ");
					sb.append("	AND SHIFT_IND = '1'");

					paramMap.put("initLastDisc", initLastDisc);
					paramMap.put("initLastLoad", initLastLoad);
					paramMap.put("vvCode", vvCode);
					log.info(" *** getVesselInfo SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				}
			}
			sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	DISTINCT RECS.*, ");
			sb.append("CASE ");
			sb.append("		WHEN RECS.GB_CLOSE_SHP_IND = 'N' THEN ");
			sb.append("	CASE ");
			sb.append("			WHEN ( ");
			sb.append("			SELECT ");
			sb.append("				COUNT(*) ");
			sb.append("			FROM ");
			sb.append("				ESN E ");
			sb.append("			WHERE ");
			sb.append("				E.OUT_VOY_VAR_NBR = RECS.VV_CD ");
			sb.append("				AND E.ESN_STATUS = 'A' ");
			sb.append("				AND E.TRANS_TYPE IN ('A', 'E', 'S', 'C')) = 0 THEN 'NA' ");
			sb.append("			ELSE 'N' ");
			sb.append("		END ");
			sb.append("		ELSE 'Y' ");
			sb.append("	END AS CLOSE_SHIPMENT, ");
			sb.append("CASE ");
			sb.append("		WHEN RECS.GB_CLOSE_BJ_IND = 'N' THEN ");
			sb.append("	CASE ");
			sb.append("			WHEN ( ");
			sb.append("			SELECT ");
			sb.append("				COUNT(*) ");
			sb.append("			FROM ");
			sb.append("				MANIFEST_DETAILS MD ");
			sb.append("			WHERE ");
			sb.append("				MD.VAR_NBR = RECS.VV_CD ");
			sb.append("				AND MD.BL_STATUS = 'A') = 0 THEN 'NA' ");
			sb.append("			ELSE 'N' ");
			sb.append("		END ");
			sb.append("		ELSE 'Y' ");
			sb.append("	END AS CLOSE_BJ ");
			sb.append("FROM ");
			sb.append("	( ");
			sb.append("	SELECT ");
			sb.append("		V.VSL_NM || '/' || V.IN_VOY_NBR VESSEL_IN_VOY, ");
			sb.append("		V.VSL_NM || '/' || V.OUT_VOY_NBR VESSEL_OUT_VOY, ");
			sb.append("		V.VV_CD, ");
			sb.append("		V.VSL_NM, ");
			sb.append("		B.BERTH_NBR, ");
			sb.append("		V.IN_VOY_NBR, ");
			sb.append("		V.OUT_VOY_NBR, ");
			sb.append("		V.TERMINAL, ");
			sb.append("	CASE ");
			sb.append("			WHEN V.COMBI_GC_OPS_IND = 'Y' THEN (V.SCHEME || '/' || NVL(V.COMBI_GC_SCHEME, '')) ");
			sb.append("			ELSE V.SCHEME ");
			sb.append("		END AS SCHEME, ");
			sb.append("		V.GB_CLOSE_BJ_IND, ");
			sb.append("		V.GB_CLOSE_SHP_IND, ");
			sb.append("		V.VSL_UNDER_TOW_IND, ");
			sb.append("		TO_CHAR(B.ATB_DTTM, 'dd-mm-yyyy hh24:mi') AS ATB, ");
			sb.append("		( ");
			sb.append("		SELECT ");
			sb.append("			TO_CHAR(BB.ATU_DTTM, 'dd-mm-yyyy hh24:mi') ");
			sb.append("		FROM ");
			sb.append("			BERTHING BB ");
			sb.append("		WHERE ");
			sb.append("			BB.SHIFT_IND = ( ");
			sb.append("			SELECT ");
			sb.append("				MAX(SHIFT_IND) ");
			sb.append("			FROM ");
			sb.append("				BERTHING ");
			sb.append("			WHERE ");
			sb.append("				VV_CD = BB.VV_CD) ");
			sb.append("			AND BB.VV_CD = B.VV_CD) AS ATN, ");
			sb.append("		TO_CHAR(B.GB_COD_DTTM, 'dd-mm-yyyy hh24:mi') AS COD, ");
			sb.append("		TO_CHAR(B.GB_COL_DTTM, 'dd-mm-yyyy hh24:mi') AS COL, ");
			sb.append("		B.COD_DTTM, ");
			sb.append("		B.COL_DTTM, ");
			sb.append("		B.FIRST_DISC_DTTM, ");
			sb.append("		B.FIRST_LOAD_DTTM, ");
			sb.append("		GB_COD_DTTM, ");
			sb.append("		GB_COL_DTTM, ");
			sb.append("		GB_FIRST_ACT_DTTM, ");
			sb.append("		GB_LAST_ACT_DTTM, ");
			sb.append("		GB_FIRST_CARGO_ACT_DTTM, ");
			sb.append("		B.ATB_DTTM, ");
			sb.append("		B.ATU_DTTM, ");
			sb.append("		V.LAST_MODIFY_USER_ID, ");
			sb.append("		V.LAST_MODIFY_DTTM, ");
			sb.append("		V.VV_STATUS_IND ");
			sb.append("	FROM ");
			sb.append("		VESSEL_CALL V, ");
			sb.append("		BERTHING B ");
			sb.append("	WHERE ");
			sb.append("		V.VV_CD = :vvCode ");
			sb.append("		AND V.VV_CD = B.VV_CD ) RECS");

			paramMap.put("vvCode", vvCode);

			log.info(" *** getVesselInfo SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			List<String[]> dataRow = new ArrayList<String[]>();

			if (rs.next()) {
				String[] rec = new String[30];
				rec[0] = "";
				rec[1] = rs.getString("VSL_NM");
				rec[2] = rs.getString("BERTH_NBR");
				rec[3] = rs.getString("IN_VOY_NBR");
				rec[4] = rs.getString("OUT_VOY_NBR");
				atb = rs.getTimestamp("ATB_DTTM");
				atu = rs.getTimestamp("ATU_DTTM");
				rec[9] = CommonUtility.deNull(CommonUtility.parseDateToFmtStr(atb));
				rec[10] = CommonUtility.deNull(CommonUtility.parseDateToFmtStr(atu));
				rec[11] = "" + rs.getTimestamp("LAST_MODIFY_DTTM").getTime();
				rec[12] = rs.getString("LAST_MODIFY_USER_ID");
				rec[13] = CommonUtility.deNull(rs.getString("VV_STATUS_IND"));
				rec[14] = CommonUtility.deNull(rs.getString("TERMINAL"));
				rec[15] = CommonUtility.deNull(rs.getString("SCHEME"));
				rec[16] = CommonUtility.deNull(rs.getString("CLOSE_SHIPMENT"));
				rec[17] = CommonUtility.deNull(rs.getString("CLOSE_BJ"));
				rec[18] = CommonUtility.deNull(rs.getString("VSL_UNDER_TOW_IND"));
				rec[19] = CommonUtility.deNull(rs.getString("VESSEL_IN_VOY"));
				rec[20] = CommonUtility.deNull(rs.getString("VESSEL_OUT_VOY"));
				rec[21] = CommonUtility.deNull(rs.getString("ATB"));
				rec[22] = CommonUtility.deNull(rs.getString("ATN"));
				rec[23] = CommonUtility.deNull(rs.getString("COL"));
				rec[24] = CommonUtility.deNull(rs.getString("COD"));
				rec[25] = CommonUtility.deNull(rs.getString("GB_COD_DTTM"));
				rec[26] = CommonUtility.deNull(rs.getString("GB_COL_DTTM"));
				rec[27] = CommonUtility.deNull(rs.getString("GB_FIRST_ACT_DTTM"));
				rec[28] = CommonUtility.deNull(rs.getString("GB_LAST_ACT_DTTM"));
				rec[29] = CommonUtility.deNull(rs.getString("GB_FIRST_CARGO_ACT_DTTM"));

				do {
					if (firstLoad == null && rs.getTimestamp("FIRST_LOAD_DTTM") != null) {
						firstLoad = rs.getTimestamp("FIRST_LOAD_DTTM");
					}
					if (firstDisc == null && rs.getTimestamp("FIRST_DISC_DTTM") != null) {
						firstDisc = rs.getTimestamp("FIRST_DISC_DTTM");
					}
					if (rs.getTimestamp("COL_DTTM") != null) {
						lastLoad = rs.getTimestamp("COL_DTTM");
					}
					if (rs.getTimestamp("COD_DTTM") != null) {
						lastDisc = rs.getTimestamp("COD_DTTM");
					}
				} while (rs.next());

				rec[6] = CommonUtility.deNull(CommonUtility.parseDateToFmtStr(lastDisc));
				rec[8] = CommonUtility.deNull(CommonUtility.parseDateToFmtStr(lastLoad));
				rec[5] = CommonUtility.deNull(CommonUtility.parseDateToFmtStr(firstDisc));
				rec[7] = CommonUtility.deNull(CommonUtility.parseDateToFmtStr(firstLoad));
				dataRow.add(rec);
			}
			if (dataRow.isEmpty()) {
				opsValueObject.setTableData(null);
			} else {
				/*
				 * //calculate whether dates are valid java.sql.Timestamp curr = new
				 * Timestamp(java.util.Calendar.getInstance().getTime().getTime()); if (
				 * (firstDisc!=null) && (atb.getTime()>firstDisc.getTime()) ) {
				 * opsValueObject.setSecondTableData(null); } else if ( (firstLoad!=null) &&
				 * (atb.getTime()>firstLoad.getTime()) ) {
				 * opsValueObject.setSecondTableData(null); } else if ( (lastDisc!=null &&
				 * firstDisc!=null) && (lastDisc.getTime()<firstDisc.getTime()) ) {
				 * opsValueObject.setSecondTableData(null); } else if ( (lastLoad!=null &&
				 * firstLoad!=null) && (lastLoad.getTime()<firstLoad.getTime()) ) {
				 * opsValueObject.setSecondTableData(null); } else if ( (lastDisc!=null) &&
				 * (atu.getTime()<lastDisc.getTime()) ) {
				 * opsValueObject.setSecondTableData(null); } else if ( (lastLoad!=null) &&
				 * (atu.getTime()<lastLoad.getTime()) ) {
				 * opsValueObject.setSecondTableData(null); } else if (
				 * (curr.getTime()<atu.getTime()) ) { opsValueObject.setSecondTableData(null); }
				 * else { opsValueObject.setSecondTableData(new String[0][0]); }
				 */
				opsValueObject.setTableData((String[][]) dataRow.toArray(new String[0][0]));
			}
		} catch (NullPointerException e) {
			log.error("Exception: getVesselInfo ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getVesselInfo ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselInfo  DAO  Result:" + (opsValueObject != null ? opsValueObject.toString() : null));
		}
		return opsValueObject;
	}

}
