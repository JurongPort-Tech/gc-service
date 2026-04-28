package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.util.NumberUtils;

import sg.com.jp.generalcargo.dao.ManifestRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.TextParaVO;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;

@Repository("ManifestRepo")
public class ManifestJdbcRepository implements ManifestRepository {

	private static final Log log = LogFactory.getLog(ManifestJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	// Region - MIGRATED-JPONLINE

	final static String TEXT_PARA_GC_VIEW_MANIFEST = "GC_V_MFST";

	private static String GC = "GC-General Carrier";
	private static String BC = "BC-Bulk Carrier";

	public String logStatusGlobal = "Y";

	private static final String SQL_INSERT_MISC_EVENT_LOG = "INSERT INTO " + ConstantUtil.MISC_EVENT_LOG
			+ " VALUES (:misNo, sysdate, :type, :haulCd, :varno, :billInd, :blno, :coCd, sysdate, :cntrSeqNbr, :pdisc1)";

	private static final String MDEL = "MDEL";// delete

	private TextParaVO getParaCodeInfo(TextParaVO tpvo) throws BusinessException
	{
		SqlRowSet rs = null;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TOPS.TEXT_PARA ");
		sql.append("WHERE PARA_CD =:paraCd ");
		TextParaVO tpo = new TextParaVO();
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START getParaCodeInfo Dao Start tpvo:" + tpvo.toString());
			paramMap.put("paraCd", tpvo.getParaCode());
			log.info("getParaCodeInfo SQL: " + sql.toString() + " paramMap " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				tpo.setParaCode(rs.getString("PARA_CD"));
				tpo.setValue(rs.getString("VALUE"));
				tpo.setParaDesc(CommonUtility.deNull(rs.getString("PARA_DESC")));
				tpo.setUser(rs.getString("LAST_MODIFY_USER_ID"));
				tpo.setTimestamp(rs.getTimestamp("LAST_MODIFY_DTTM"));
			}
			log.info("END: getParaCodeInfo DAO End result:" + tpo.toString());
		}
		catch (Exception e) {
			log.info("Exception getParaCodeInfo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParaCodeInfo DAO End");
		}
		return tpo;
	}

	public boolean isShowManifestInfo(String companyCode, TextParaVO result) throws BusinessException {
		try {
			log.info("START: DAO isShowManifestInfo companyCode:" + CommonUtility.deNull(companyCode) + "result:" + result.toString());
			if (result != null && result.getValue() != null && !"".equals(result.getValue())) {
				String[] textArr = result.getValue().split("/");
				String text = "";
				if (textArr != null && textArr.length > 0) {
					for (int i = 0; i < textArr.length; i++) {
						text = textArr[i];
						if (text != null && text.equals(companyCode)) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			log.info("Exception isShowManifestInfo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isShowManifestInfo DAO");
		}
		return false;
	}


	@Override
	public List<VesselVoyValueObject> getVesselVoyList(String cocode, String vesselName, String voyageNumber, String terminal) throws BusinessException {
		boolean isShowManifestInfo = false;
		List<VesselVoyValueObject> voyList = new ArrayList<VesselVoyValueObject>();
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getVesselVoyList Dao START cocode:" + CommonUtility.deNull(cocode) + "vesselName:" + CommonUtility.deNull(vesselName) + "voyageNumber:"
					+ CommonUtility.deNull(voyageNumber) + "terminal" + CommonUtility.deNull(terminal));
			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_MANIFEST);
			TextParaVO result = getParaCodeInfo(code);
			isShowManifestInfo = isShowManifestInfo(cocode, result);

			if (isShowManifestInfo) {
				/*
				 * sb.append("SELECT V.IN_VOY_NBR, V.VSL_NM, V.VV_CD,  "); sb.
				 * append(" TO_CHAR(DECODE(SIGN(DECODE(SIGN(V.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, V.VSL_BERTH_DTTM,B.ETB_DTTM )- NVL(B.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(V.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, V.VSL_BERTH_DTTM,B.ETB_DTTM ),B.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL,  "
				 * ); sb.
				 * append(" TO_CHAR(DECODE(SIGN(DECODE(SIGN(V.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, V.VSL_ETD_DTTM,B.ETU_DTTM )- NVL(B.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(V.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, V.VSL_ETD_DTTM,B.ETU_DTTM ),B.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE,   "
				 * ); sb.append(" TO_CHAR(B.COD_DTTM,'dd/mm/yyyy HH24MI') as COD_DTTM, ");
				 * sb.append(" TO_CHAR(B.GB_COD_DTTM,'dd/mm/yyyy HH24MI') as GB_COD_DTTM, ");
				 * sb.append(" TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM, ");
				 * sb.append(" V.TERMINAL "); sb.
				 * append(" FROM TOPS.VESSEL_CALL V LEFT JOIN TOPS.BERTHING B ON (V.VV_CD = B.VV_CD AND B.SHIFT_IND = 1) "
				 * ); sb.append(" LEFT JOIN TOPS.VESSEL VS ON (V.VSL_NM = VS.VSL_NM) "); sb.
				 * append(" WHERE (V.VV_STATUS_IND <> 'CX' AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')) AND (V.VSL_NM =:vesselName "
				 * ); sb.append(" OR VS.VSL_FULL_NM =:vesselName)  ");
				 * sb.append(" AND V.IN_VOY_NBR =:voyageNumber  ORDER BY V.VSL_NM, V.IN_VOY_NBR"
				 * );
				 */
				// Added BY NS
				sb.append(" SELECT ");
				sb.append("	IN_VOY_NBR, ");
				sb.append("	VSL_NM, ");
				sb.append("	VV_CD, ");
				sb.append("	TERMINAL, ");
				sb.append("	ARRIVAL, ");
				sb.append("	COD_DTTM, ");
				sb.append("	GB_COD_DTTM, ");
				sb.append("	ETB_DTTM, ");
				sb.append("	DEPARTURE, ");
				sb.append("	ETU_DTTM, ");
				sb.append("	ATU_DTTM, ");
				sb.append(" CEMENT_VSL_IND ,");
				sb.append("	( ");
				sb.append("	SELECT ");
				sb.append("		CASE ");
				sb.append("			WHEN b.ATB_DTTM IN(TO_DATE(ARRIVAL, 'dd/mm/yyyy HH24MI')) THEN 'ATB' ");
				sb.append("			ELSE 'ETB' ");
				sb.append("		END ");
				sb.append("	FROM ");
				sb.append("		TOPS.VESSEL_CALL V ");
				sb.append("	LEFT JOIN TOPS.BERTHING B ON ");
				sb.append("		(V.VV_CD = B.VV_CD ");
				sb.append("		AND B.SHIFT_IND = 1) ");
				sb.append("	LEFT JOIN TOPS.VESSEL VS ON ");
				sb.append("		(V.VSL_NM = VS.VSL_NM) ");
				sb.append("	WHERE ");
				sb.append("		(V.VV_STATUS_IND <> 'CX' ");
				sb.append("		AND ((TERMINAL IN 'CT' ");
				sb.append("		AND COMBI_GC_OPS_IND IN('Y', ");
				sb.append("		NULL)) ");
				sb.append("		OR TERMINAL NOT IN 'CT')) ");
				sb.append("		AND (V.VSL_NM =:vesselName ");
				sb.append("		OR VS.VSL_FULL_NM =:vesselName) ");
				sb.append("		AND V.IN_VOY_NBR =:voyageNumber ) indicationOfArrival, ");
				sb.append("	( ");
				sb.append("	SELECT ");
				sb.append("		CASE ");
				sb.append("			WHEN b.ATU_DTTM IN(TO_DATE(DEPARTURE, 'dd/mm/yyyy HH24MI')) THEN 'ATU' ");
				sb.append("			ELSE 'ETU' ");
				sb.append("		END ");
				sb.append("	FROM ");
				sb.append("		TOPS.VESSEL_CALL V ");
				sb.append("	LEFT JOIN TOPS.BERTHING B ON ");
				sb.append("		(V.VV_CD = B.VV_CD ");
				sb.append("		AND B.SHIFT_IND = 1) ");
				sb.append("	LEFT JOIN TOPS.VESSEL VS ON ");
				sb.append("		(V.VSL_NM = VS.VSL_NM) ");
				sb.append("	WHERE ");
				sb.append("		(V.VV_STATUS_IND <> 'CX' ");
				sb.append("		AND ((TERMINAL IN 'CT' ");
				sb.append("		AND COMBI_GC_OPS_IND IN('Y', ");
				sb.append("		NULL)) ");
				sb.append("		OR TERMINAL NOT IN 'CT')) ");
				sb.append("		AND (V.VSL_NM =:vesselName ");
				sb.append("		OR VS.VSL_FULL_NM =:vesselName) ");
				sb.append("		AND V.IN_VOY_NBR =:voyageNumber ) indicationOfDeparture ");

				sb.append("FROM ");
				sb.append("	( ");
				sb.append("	SELECT ");
				sb.append("		V.IN_VOY_NBR, ");
				sb.append("		V.VSL_NM, ");
				sb.append("		V.VV_CD, ");
				sb.append("		B.BERTH_NBR, ");
				sb.append(
						"		TO_CHAR(DECODE(SIGN(DECODE(SIGN(V.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, SYSDATE - 9000)), 1, V.VSL_BERTH_DTTM, B.ETB_DTTM )- NVL(B.ATB_DTTM, SYSDATE - 9000)), 1, DECODE(SIGN(V.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, SYSDATE - 9000)), 1, V.VSL_BERTH_DTTM, B.ETB_DTTM ), B.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
				sb.append(
						"		TO_CHAR(DECODE(SIGN(DECODE(SIGN(V.VSL_ETD_DTTM- NVL(B.ETU_DTTM, SYSDATE - 9000)), 1, V.VSL_ETD_DTTM, B.ETU_DTTM )- NVL(B.ATU_DTTM, SYSDATE - 9000)), 1, DECODE(SIGN(V.VSL_ETD_DTTM- NVL(B.ETU_DTTM, SYSDATE - 9000)), 1, V.VSL_ETD_DTTM, B.ETU_DTTM ), B.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
				sb.append("		TO_CHAR(B.COD_DTTM, 'dd/mm/yyyy HH24MI') AS COD_DTTM, ");
				sb.append("		TO_CHAR(B.GB_COD_DTTM, 'dd/mm/yyyy HH24MI') AS GB_COD_DTTM, ");
				sb.append("		TO_CHAR(B.ETB_DTTM, 'dd/mm/yyyy HH24MI') AS ETB_DTTM, ");
				sb.append("		TO_CHAR(B.ETU_DTTM, 'dd/mm/yyyy HH24MI') AS ETU_DTTM, ");
				sb.append("		TO_CHAR(B.ATU_DTTM, 'dd/mm/yyyy HH24MI') AS ATU_DTTM, ");
				sb.append("		V.TERMINAL,V.CEMENT_VSL_IND ");
				sb.append("	FROM ");
				sb.append("		TOPS.VESSEL_CALL V ");
				sb.append("	LEFT JOIN TOPS.BERTHING B ON ");
				sb.append("		(V.VV_CD = B.VV_CD ");
				sb.append("		AND B.SHIFT_IND = 1) ");
				sb.append("	LEFT JOIN TOPS.VESSEL VS ON ");
				sb.append("		(V.VSL_NM = VS.VSL_NM) ");
				sb.append("	WHERE ");
				sb.append("		(V.VV_STATUS_IND <> 'CX' ");
				sb.append("		AND ((TERMINAL IN 'CT' ");
				sb.append("		AND COMBI_GC_OPS_IND IN('Y', ");
				sb.append("		NULL)) ");
				sb.append("		OR TERMINAL NOT IN 'CT')) ");
				sb.append("		AND (V.VSL_NM =:vesselName ");
				sb.append("		OR VS.VSL_FULL_NM =:vesselName) ");
				sb.append("		AND V.IN_VOY_NBR =:voyageNumber ");
				sb.append("	ORDER BY ");
				sb.append("		V.VSL_NM, ");
				sb.append("		V.IN_VOY_NBR )");

			} else {
				/*
				 * sb.append(" SELECT DISTINCT VC.IN_VOY_NBR,VC.VSL_NM,VC.VV_CD,  "); sb.
				 * append(" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM )- NVL(B.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM ),B.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, "
				 * ); sb.
				 * append(" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM )- NVL(B.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM ),B.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, "
				 * ); sb.append(" TO_CHAR(B.COD_DTTM,'dd/mm/yyyy HH24MI') as COD_DTTM, ");
				 * sb.append(" TO_CHAR(B.GB_COD_DTTM,'dd/mm/yyyy HH24MI') as GB_COD_DTTM, ");
				 * sb.
				 * append(" TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM, VC.TERMINAL ");
				 * sb.
				 * append(" FROM TOPS.VESSEL_CALL VC LEFT OUTER JOIN GBMS.VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A') "
				 * ); sb.
				 * append(" LEFT OUTER JOIN TOPS.BERTHING B ON (VC.VV_CD = B.VV_CD AND B.SHIFT_IND = 1) "
				 * ); sb.append(" LEFT OUTER JOIN TOPS.VESSEL V ON (VC.VSL_NM = V.VSL_NM)  ");
				 * sb.
				 * append(" WHERE VV_STATUS_IND <> 'CX' AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT') "
				 * ); sb.append(" AND (VD.CUST_CD =:coCode  OR VC.CREATE_CUST_CD =:coCode ) ");
				 * sb.
				 * append(" AND (VC.VSL_NM =:vesselName  OR V.VSL_FULL_NM =:vesselName) AND VC.IN_VOY_NBR =:voyageNumber  "
				 * ); sb.append(" ORDER BY VSL_NM,IN_VOY_NBR ");
				 */

				sb.append("SELECT ");
				sb.append("	IN_VOY_NBR, ");
				sb.append("	VSL_NM, ");
				sb.append("	VV_CD, ");
				sb.append("	TERMINAL, ");
				sb.append("	ARRIVAL, ");
				sb.append("	COD_DTTM, ");
				sb.append("	GB_COD_DTTM, ");
				sb.append("	ETB_DTTM, ");
				sb.append("	DEPARTURE, ");
				//sb.append("	ETU_DTTM, ");
				//sb.append("	ATU_DTTM , ");
				sb.append(" CEMENT_VSL_IND, ");
				sb.append("	( ");
				sb.append("	SELECT ");
				sb.append("		CASE ");
				sb.append("			WHEN b.ATB_DTTM IN(TO_DATE(ARRIVAL, 'dd/mm/yyyy HH24MI')) THEN 'ATB' ");
				sb.append("			ELSE 'ETB' ");
				sb.append("		END ");
				sb.append("	FROM ");
				sb.append("		TOPS.VESSEL_CALL VC ");
				sb.append("	LEFT OUTER JOIN GBMS.VESSEL_DECLARANT VD ON ");
				sb.append("		(VD.VV_CD = VC.VV_CD ");
				sb.append("		AND VD.STATUS = 'A') ");
				sb.append("	LEFT OUTER JOIN TOPS.BERTHING B ON ");
				sb.append("		(VC.VV_CD = B.VV_CD ");
				sb.append("		AND B.SHIFT_IND = 1) ");
				sb.append("	LEFT OUTER JOIN TOPS.VESSEL V ON ");
				sb.append("		(VC.VSL_NM = V.VSL_NM) ");
				sb.append("	WHERE ");
				sb.append("		VV_STATUS_IND <> 'CX' ");
				sb.append("		AND ((TERMINAL IN 'CT' ");
				sb.append("		AND COMBI_GC_OPS_IND IN('Y', ");
				sb.append("		NULL)) ");
				sb.append("		OR TERMINAL NOT IN 'CT') ");
				sb.append("		AND (VD.CUST_CD =:coCode ");
				sb.append("		OR VC.CREATE_CUST_CD =:coCode ) ");
				sb.append("		AND (VC.VSL_NM =:vesselName ");
				sb.append("		OR V.VSL_FULL_NM =:vesselName) ");
				sb.append("		AND VC.IN_VOY_NBR =:voyageNumber ) indicationOfArrival, ");
				sb.append("	( ");
				sb.append("	SELECT ");
				sb.append("		CASE ");
				sb.append("			WHEN b.ATU_DTTM IN(TO_DATE(DEPARTURE, 'dd/mm/yyyy HH24MI')) THEN 'ATU' ");
				sb.append("			ELSE 'ETU' ");
				sb.append("		END ");
				sb.append("	FROM ");
				sb.append("		TOPS.VESSEL_CALL VC ");
				sb.append("	LEFT OUTER JOIN GBMS.VESSEL_DECLARANT VD ON ");
				sb.append("		(VD.VV_CD = VC.VV_CD ");
				sb.append("		AND VD.STATUS = 'A') ");
				sb.append("	LEFT OUTER JOIN TOPS.BERTHING B ON ");
				sb.append("		(VC.VV_CD = B.VV_CD ");
				sb.append("		AND B.SHIFT_IND = 1) ");
				sb.append("	LEFT OUTER JOIN TOPS.VESSEL V ON ");
				sb.append("		(VC.VSL_NM = V.VSL_NM) ");
				sb.append("	WHERE ");
				sb.append("		VV_STATUS_IND <> 'CX' ");
				sb.append("		AND ((TERMINAL IN 'CT' ");
				sb.append("		AND COMBI_GC_OPS_IND IN('Y', ");
				sb.append("		NULL)) ");
				sb.append("		OR TERMINAL NOT IN 'CT') ");
				sb.append("		AND (VD.CUST_CD =:coCode ");
				sb.append("		OR VC.CREATE_CUST_CD =:coCode ) ");
				sb.append("		AND (VC.VSL_NM =:vesselName ");
				sb.append("		OR V.VSL_FULL_NM =:vesselName) ");
				sb.append("		AND VC.IN_VOY_NBR =:voyageNumber ) indicationOfDeparture ");
				sb.append("FROM ");
				sb.append("	( ");
				sb.append("	SELECT ");
				sb.append("		DISTINCT VC.IN_VOY_NBR, ");
				sb.append("		VC.VSL_NM, ");
				sb.append("		VC.VV_CD, ");
				sb.append(
						"		TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, SYSDATE - 9000)), 1, VC.VSL_BERTH_DTTM, B.ETB_DTTM )- NVL(B.ATB_DTTM, SYSDATE - 9000)), 1, DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, SYSDATE - 9000)), 1, VC.VSL_BERTH_DTTM, B.ETB_DTTM ), B.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
				sb.append(
						"		TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, SYSDATE - 9000)), 1, VC.VSL_ETD_DTTM, B.ETU_DTTM )- NVL(B.ATU_DTTM, SYSDATE - 9000)), 1, DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, SYSDATE - 9000)), 1, VC.VSL_ETD_DTTM, B.ETU_DTTM ), B.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
				sb.append("		TO_CHAR(B.COD_DTTM, 'dd/mm/yyyy HH24MI') AS COD_DTTM, ");
				sb.append("		TO_CHAR(B.GB_COD_DTTM, 'dd/mm/yyyy HH24MI') AS GB_COD_DTTM, ");
				sb.append("		TO_CHAR(B.ETB_DTTM, 'dd/mm/yyyy HH24MI') AS ETB_DTTM, ");
				sb.append("		VC.TERMINAL,VC.CEMENT_VSL_IND ");
				sb.append("	FROM ");
				sb.append("		TOPS.VESSEL_CALL VC ");
				sb.append("	LEFT OUTER JOIN GBMS.VESSEL_DECLARANT VD ON ");
				sb.append("		(VD.VV_CD = VC.VV_CD ");
				sb.append("		AND VD.STATUS = 'A') ");
				sb.append("	LEFT OUTER JOIN TOPS.BERTHING B ON ");
				sb.append("		(VC.VV_CD = B.VV_CD ");
				sb.append("		AND B.SHIFT_IND = 1) ");
				sb.append("	LEFT OUTER JOIN TOPS.VESSEL V ON ");
				sb.append("		(VC.VSL_NM = V.VSL_NM) ");
				sb.append("	WHERE ");
				sb.append("		VV_STATUS_IND <> 'CX' ");
				sb.append("		AND ((TERMINAL IN 'CT' ");
				sb.append("		AND COMBI_GC_OPS_IND IN('Y', ");
				sb.append("		NULL)) ");
				sb.append("		OR TERMINAL NOT IN 'CT') ");
				sb.append("		AND (VD.CUST_CD =:coCode ");
				sb.append("		OR VC.CREATE_CUST_CD =:coCode ) ");
				sb.append("		AND (VC.VSL_NM =:vesselName ");
				sb.append("		OR V.VSL_FULL_NM =:vesselName) ");
				sb.append("		AND VC.IN_VOY_NBR =:voyageNumber ");
				sb.append("	ORDER BY ");
				sb.append("		VSL_NM, ");
				sb.append("		IN_VOY_NBR )");

			}

			String voynbr = "";
			String vslName = "";
			String VV_CD = "";
			String arrival = "";
			String departure = "";
			String cod_dttm = "";
			String etb_dttm = "";
			String vTerminal = "";
			String indicationOfArrival = "";
			String indicationOfDeparture = "";
			String vsltype = "";
			
			if (!isShowManifestInfo) {
				paramMap.put("coCode", cocode);
			}

			paramMap.put("vesselName", vesselName);
			paramMap.put("voyageNumber", voyageNumber);
			log.info("getVesselVoyList SQL: " + sb.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				voynbr = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				vslName = CommonUtility.deNull(rs.getString("VSL_NM"));
				VV_CD = CommonUtility.deNull(rs.getString("VV_CD"));
				arrival = CommonUtility.deNull(rs.getString("ARRIVAL"));
				departure = CommonUtility.deNull(rs.getString("DEPARTURE"));
				cod_dttm = CommonUtility.deNull(rs.getString("COD_DTTM"));
				if (StringUtils.isBlank(cod_dttm)) {
					cod_dttm = CommonUtility.deNull(rs.getString("GB_COD_DTTM"));
				}
				etb_dttm = CommonUtility.deNull(rs.getString("ETB_DTTM"));
				vTerminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				vTerminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				String vesselltype = CommonUtility.deNull(rs.getString("CEMENT_VSL_IND"));
				if (vesselltype != null && vesselltype.equalsIgnoreCase("N")) {
					vsltype = GC;
				} else {
					vsltype = BC;
				}
				indicationOfArrival = CommonUtility.deNull(rs.getString("indicationOfArrival"));
				indicationOfDeparture = CommonUtility.deNull(rs.getString("indicationOfDeparture"));
				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj.setVoyNo(voynbr);
				vvvObj.setVslName(vslName);
				vvvObj.setVarNbr(VV_CD);
				vvvObj.setArrival(arrival);
				vvvObj.setDepartural(departure);
				vvvObj.setCod_dttm(cod_dttm);
				vvvObj.setEtb_dttm(etb_dttm);
				vvvObj.setTerminal(vTerminal);
				vvvObj.setTerminal(vTerminal);
				vvvObj.setBerthNo(getLastBerthNo(VV_CD));
				vvvObj.setIndicationOfArrival(indicationOfArrival);
				vvvObj.setIndicationOfDeparture(indicationOfDeparture);
				vvvObj.setVesselType(vsltype);
				voyList.add(vvvObj);
			}
			log.info("END: getVesselVoyList DAO END:voyList" + voyList.toString());
		} catch (Exception e) {
			log.info("Exception getVesselVoyList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselVoyList DAO END" );
		}
		return voyList;
	}

	public String getLastBerthNo(String vvCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		String berthNo = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getLastBerthNo vvCd:" + CommonUtility.deNull(vvCd));
			sb.append("SELECT ");
			sb.append("		B.BERTH_NBR ");
			sb.append("	FROM ");
			sb.append("		TOPS.BERTHING B ");
			sb.append("	WHERE ");
			sb.append("		B.VV_CD =:vvCd ");
			sb.append("		AND SHIFT_IND = ( ");
			sb.append("		SELECT ");
			sb.append("			MAX (bb.SHIFT_IND) ");
			sb.append("		FROM ");
			sb.append("			TOPS.BERTHING bb ");
			sb.append("		WHERE ");
			sb.append("			bb.VV_CD =:vvCd ) ");
			paramMap.put("vvCd", vvCd);
			log.info("getLastBerthNo SQL: " + sb.toString() + " paramMap:" + paramMap.toString());
			try {
				berthNo = (String) namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			} catch (EmptyResultDataAccessException e) {
				log.info(e);
			}
			log.info("END: DAO getLastBerthNo berthNo:" + CommonUtility.deNull(berthNo));
		} catch (Exception e) {
			log.info("Exception getLastBerthNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getLastBerthNo");
		}
		return berthNo;

	}

	@Override
	public boolean chkVslStat(String varno) throws BusinessException {
		String sql = "";
		boolean bvslind = false;
		Map<String,String> paramMap = new HashMap<String,String>();
		sql = "SELECT GB_CLOSE_BJ_IND FROM TOPS.VESSEL_CALL WHERE GB_CLOSE_BJ_IND='Y' AND VV_CD=:varno ";
		SqlRowSet rs = null;
		try {
			log.info("START: DAO chkVslStat:" + CommonUtility.deNull(varno));
			paramMap.put("varno", varno);
			log.info("chkVslStat SQL: " + sql + " paramMap " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				bvslind = true;
			} else {
				bvslind = false;
			}
			log.info("END: chkVslStat DAO bvslind:" + bvslind);
		} catch (Exception e) {
			log.info("Exception chkVslStat : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkVslStat DAO");
		}
		return bvslind;
	}

	// start::Added by vietnd02 to check manifest status
	// true = close, false = not close
	@Override
	public boolean isManClose(String vesselCd) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		boolean chk = false;
		Map<String,String> paramMap = new HashMap<String,String>();
		sql = "SELECT VV_CD FROM TOPS.VESSEL_CALL WHERE VV_CD =:vesselCd  AND VV_STATUS_IND in ('UB', 'CL') ";
		try {
			log.info("START: isManClose Dao vesselCd:" + CommonUtility.deNull(vesselCd));
			paramMap.put("vesselCd", vesselCd);
			log.info("isManClose SQL: " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				chk = true;
			}
			log.info("END: DAO isManClose chk:" + chk);
		} catch (Exception e) {
			log.info("Exception isManClose : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO isManClose ");
		}
		return chk;
	}

	private boolean chkPkgtype(String pkgcd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		boolean bpkgcd = false;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: chkPkgtype DAO" + "pkgcd:" + CommonUtility.deNull(pkgcd));
			sb.append("  SELECT PKG_TYPE_CD FROM PKG_TYPES WHERE PKG_TYPE_CD=:pkgcd and REC_STATUS='A' ");
			paramMap.put("pkgcd", pkgcd);
			log.info("chkPkgtype SQL: " + sb.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bpkgcd = true;
			} else {
				bpkgcd = false;
			}
			log.info("END: chkPkgtype DAO bpkgcd" + bpkgcd);
		} catch (Exception e) {
			log.info("Exception functionName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkPkgtype DAO END" );
		}
		return bpkgcd;
	}

	private boolean chkPortCode(String portcd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		boolean bpcd = false;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: chkPortCode DAO" + "portcd:" + CommonUtility.deNull(portcd));
			sb.append("  SELECT PORT_CD FROM UN_PORT_CODE WHERE REC_STATUS = 'A' AND PORT_CD=:portcd ");
			paramMap.put("portcd", portcd);
			log.info("chkPortCode SQL: " + sb.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bpcd = true;
			} else {
				bpcd = false;
			}
			log.info("END: chkPortCode bpcd" + bpcd);
		} catch (Exception e) {
			log.info("Exception chkPortCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkPortCode END");
		}
		return bpcd;
	}

	private boolean chkDGInd(String blno, String varno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean dgInd = false;
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: chkDGInd blno:" + CommonUtility.deNull(blno) + "varno:" + CommonUtility.deNull(varno));
			sql.append(" SELECT MPA_APPV_STATUS,JP_APPV_STATUS FROM PM4 WHERE  BL_NBR=:blno ");
			sql.append(" AND VV_CD=:varno ");
			sql.append(" AND (OPR_TYPE IN('D','T')) AND (RECORD_TYPE <> 'D') ");
			paramMap.put("blno", blno);
			paramMap.put("varno", varno);
			log.info("chkDGInd SQL: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				if ((!rs.getString("MPA_APPV_STATUS").equals("A")) || (!rs.getString("JP_APPV_STATUS").equals("A"))) {
					dgInd = false;
					break;
				} else {
					dgInd = true;
				}
			}
			log.info("END: chkDGInd dgInd:" + dgInd);
		} catch (Exception e) {
			log.info("Exception chkDGInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkDGInd DAO END");
		}
		return dgInd;
	}

	public String getHSSubCodeDes(String hsCode, String hsSubCodeFr, String hsSubCodeTo) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		String desc = "";
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getHSSubCodeDes DAO hsCode:" + CommonUtility.deNull(hsCode) + "hsSubCodeFr:" + CommonUtility.deNull(hsSubCodeFr) + "hsSubCodeTo:"
					+ CommonUtility.deNull(hsSubCodeTo));
			sb.append(" SELECT HS_SUB_DESC FROM HS_SUB_CODE WHERE HS_CODE=:hsCode  AND HS_SUB_CODE_FR=:hsSubCodeFr ");
			if (hsSubCodeTo != null && !"".equalsIgnoreCase(hsSubCodeTo)) {
				sb.append(" AND HS_SUB_CODE_TO =:hsSubCodeTo  ");
			}
			paramMap.put("hsCode", hsCode);
			paramMap.put("hsSubCodeFr", hsSubCodeFr);
			paramMap.put("hsSubCodeTo", hsSubCodeTo);
			log.info("getHSSubCodeDes SQL: " + sb.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				desc = CommonUtility.deNull(rs.getString("HS_SUB_DESC"));
			}
			log.info("END: getHSSubCodeDes desc:" +  CommonUtility.deNull(desc));
		} catch (Exception e) {
			log.info("Exception getHSSubCodeDes : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getHSSubCodeDes DAO END");
		}
		return desc;
	}

	private String insertMiscEvtLog(String type, String varno, String blno, String coCd) throws BusinessException {
		String result = "";
		Map<String,Object> paramMap = new HashMap<String,Object>();
		try {
			log.info("START: insertMiscEvtLog type:" + type + "varno:" + varno + "blno:" + blno + "coCd:" + coCd);
			long misNo = getMisNo();
			paramMap.put("misNo", misNo);
			paramMap.put("type", type); // for Manifest Delete
			paramMap.put("haulCd", "");
			paramMap.put("varno", varno);
			// VietNguyen added 02 March 2010 CR-CIM-20091203-34 : START
			// to waive charge when delete manifest by JP
			if (MDEL.equalsIgnoreCase(type)) {
				paramMap.put("billInd", "X");
			} else {
				paramMap.put("billInd", "N");
			}
			// VietNguyen added 02 March 2010 CR-CIM-20091203-34 : START
			paramMap.put("blno", blno);
			paramMap.put("coCd", coCd);
			paramMap.put("cntrSeqNbr", "");
			paramMap.put("pdisc1", "");
			log.info("insertMiscEvtLog SQL: " + SQL_INSERT_MISC_EVENT_LOG + " paramMap: " + paramMap);
			int count1 = namedParameterJdbcTemplate.update(SQL_INSERT_MISC_EVENT_LOG, paramMap);

			if (count1 == 0) {
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			} else {
				result = misNo + "";
			}
			log.info("END: insertMiscEvtLog result:" + CommonUtility.deNull(result));
		} catch (BusinessException e) {
			log.info("Exception insertMiscEvtLog : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception insertMiscEvtLog : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertMiscEvtLog DAO END");
		}
		return result;

	}

	private long getMisNo() throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		long nextValue = 0;
		Long nextMiscSeqNbr = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getMisNo DAo");
			sql = "SELECT MISC_EVENT_LOG_SEQ_NBR.nextVal FROM DUAL";
			log.info("getMisNo SQL: " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				nextValue = rs.getLong("NEXTVAL");
				nextMiscSeqNbr = new Long(nextValue);
			}
			log.info("END: getMisNo DAO nextMiscSeqNbr:" + nextMiscSeqNbr.longValue());
		} catch (Exception e) {
			log.info("Exception getMisNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMisNo DAO END");
		}
		return nextMiscSeqNbr.longValue();
	}

	private String getPortName(String portcd) throws BusinessException {
		String sql = "";
		String portnm = "";
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:getPortName portcd:" + portcd);
			sql = "SELECT PORT_NM FROM UN_PORT_CODE WHERE PORT_CD=:portcd";
			paramMap.put("portcd", portcd);
			log.info("getPortName SQL" + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				portnm = rs.getString("PORT_NM");
			}
			log.info("getPortName  Result portnm" + portnm);
		} catch (Exception e) {
			log.info("Exception getPortName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPortName DAO END");
		}
		return portnm;

	}

	private String getPkgName(String pkgtype) throws BusinessException {
		String sql = "";
		String pkgname = "";
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getPkgName pkgtype:" + pkgtype);
			sql = "SELECT PKG_DESC FROM PKG_TYPES WHERE PKG_TYPE_CD=:pkgtype";
			paramMap.put("pkgtype", pkgtype);
			log.info("getPkgName SQL: " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				pkgname = rs.getString("PKG_DESC");
			}
			log.info("END: getPkgName pkgtype:" + CommonUtility.deNull(pkgtype));
		} catch (Exception e) {
			log.info("Exception getPkgName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPkgName DAO END");
		}
		return pkgname;
	}

	private boolean chkEdonbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean bvslind = false;
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:chkEdonbrPkgs seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT EDO_NBR_PKGS FROM MANIFEST_DETAILS WHERE EDO_NBR_PKGS>0 AND MFT_SEQ_NBR=:seqno  ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");

			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("chkEdonbrPkgs SQL: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				int edoNbrPkgs = rs.getInt("EDO_NBR_PKGS");
				if (edoNbrPkgs == 0) {
					bvslind = false;
				} else {
					bvslind = true;
				}
			}
			log.info("END: chkEdonbrPkgs bvslind:" + bvslind);
		} catch (Exception e) {
			log.info("Exception chkEdonbrPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkEdonbrPkgs DAO END");
		}
		return bvslind;
	}

	private boolean chkDNnbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean dnexist = false;
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("chkDNnbrPkgs seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT DN_NBR_PKGS FROM GB_EDO WHERE DN_NBR_PKGS>0 AND MFT_SEQ_NBR=:seqno  ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("chkDNnbrPkgs SQL: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				dnexist = true;
			} else {
				dnexist = false;
			}
			log.info("END: chkDNnbrPkgs bvslind:" + dnexist);
		} catch (Exception e) {
			log.info("Exception chkDNnbrPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkDNnbrPkgs DAO END");
		}
		return dnexist;
	}

	public boolean chkTnbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean texist = false;
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("chkTnbrPkgs seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT TRANS_NBR_PKGS FROM GB_EDO WHERE TRANS_NBR_PKGS>0 AND MFT_SEQ_NBR=:seqno  ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");

			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("chkTnbrPkgs SQL: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				texist = true;
			} else {
				texist = false;
			}
			log.info("END:chkTnbrPkgs Result texist:" + texist);
		} catch (Exception e) {
			log.info("Exception chkTnbrPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:chkTnbrPkgs DAO END");
		}
		return texist;
	}

	public boolean chkTDNnbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean chkTDNnbrPkgs = false;
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("chkTDNnbrPkgs seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT TRANS_DN_NBR_PKGS FROM GB_EDO WHERE TRANS_DN_NBR_PKGS>0 AND MFT_SEQ_NBR=:seqno  ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");

			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("chkTDNnbrPkgs SQL: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				chkTDNnbrPkgs = true;
			} else {
				chkTDNnbrPkgs = false;
			}
			log.info("END:chkTDNnbrPkgs bvslind:" + chkTDNnbrPkgs);
		} catch (Exception e) {
			log.info("Exception chkTDNnbrPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:chkTDNnbrPkgs DAO END");
		}
		return chkTDNnbrPkgs;
	}

	@Override
	public ManifestValueObject mftRetrieve(String blno, String varno, String seqno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlmark = new StringBuilder();
		StringBuilder sqlcntr = new StringBuilder();
		int mftseqno = 0;
		SqlRowSet rs = null;
		String pkgnm = "";
		String crgnm = "";
		String spol = "";
		String spod = "";
		String spofd = "";
		boolean bvslstat = false;
		String vslstat = "";
		boolean bdnbrpkgs = false;
		String dnbrpkgs = "";
		boolean btnbrpkgs = false;
		String tnbrpkgs = "";
		boolean btdnbrpkgs = false;
		String tdnbrpkgs = "";
		ManifestValueObject mftdispobj = new ManifestValueObject();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		try {
			log.info("START: mftRetrieve blno:" + blno + "varno:" + varno + "seqno:" + seqno);
			sql.append(
					" SELECT * FROM MANIFEST_DETAILS mft,cc_unstuff_manifest unstf, HS_SUB_CODE hs WHERE unstf.active_status(+)='A' and unstf.unstuff_seq_nbr(+)=mft.unstuff_seq_nbr and HS.HS_CODE(+)= mft.HS_CODE AND HS.HS_SUB_CODE_FR(+)= mft.HS_SUB_CODE_FR AND HS.HS_SUB_CODE_TO(+)= mft.HS_SUB_CODE_TO and BL_NBR=:blno ");
			sql.append(" AND mft.VAR_NBR=:varno  AND MFT_SEQ_NBR=:seqno ");

			paramMap.put("blno", blno);
			paramMap.put("varno", varno);
			paramMap.put("seqno", seqno);
			log.info("mftRetrieve SQL 1: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				mftseqno = rs.getInt("MFT_SEQ_NBR");
				bvslstat = chkVslStat(varno);
				bdnbrpkgs = chkDNnbrPkgs(seqno, varno, blno);
				btnbrpkgs = chkTnbrPkgs(seqno, varno, blno);
				btdnbrpkgs = chkTDNnbrPkgs(seqno, varno, blno);
				if (bvslstat)
					vslstat = "closed";
				else
					vslstat = "notclosed";

				if (bdnbrpkgs)
					dnbrpkgs = "1";
				else
					dnbrpkgs = "0";

				if (btnbrpkgs)
					tnbrpkgs = "1";
				else
					tnbrpkgs = "0";

				if (btdnbrpkgs)
					tdnbrpkgs = "1";
				else
					tdnbrpkgs = "0";

				String strUnStfSeq = CommonUtility.deNull(rs.getString("UNSTUFF_SEQ_NBR"));

				if (strUnStfSeq.equals("0"))
					mftdispobj.setUnStfInd("No");
				else
					mftdispobj.setUnStfInd("Yes");

				mftdispobj.setDnstat(dnbrpkgs);
				mftdispobj.setTnstat(tnbrpkgs);
				mftdispobj.setTdnstat(tdnbrpkgs);
				mftdispobj.setVslstat(vslstat);
				crgnm = getCrgNm(rs.getString("CRG_TYPE"));
				mftdispobj.setCrgn(crgnm);

				// Add by thanhnv2::Start
				mftdispobj.setAdviseBy(CommonUtility.deNull(rs.getString("ADVISE_BY")));
				mftdispobj.setAdviseDate(CommonUtility.parseDateToFmtStr(rs.getTimestamp("ADVISE_DATE")));
				mftdispobj.setAdviseMode(CommonUtility.deNull(rs.getString("ADVISE_MODE")));
				mftdispobj.setAmendCharged(CommonUtility.deNull(rs.getString("AMEND_CHARGED_TO")));
				mftdispobj.setWaiveCharge(CommonUtility.deNull(rs.getString("WAIVE_CHARGED")));
				mftdispobj.setWaiveReason(CommonUtility.deNull(rs.getString("WAIVE_REASON")));
				// Add by thanhnv2::End
				mftdispobj.setCrgType(CommonUtility.deNull(rs.getString("CRG_TYPE")));
				mftdispobj.setHsCode(CommonUtility.deNull(rs.getString("HS_CODE")));
				mftdispobj.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_DES")));
				mftdispobj.setNoofPkgs(CommonUtility.deNull(rs.getString("NBR_PKGS")));
				mftdispobj.setGrWt(CommonUtility.deNull(rs.getString("GROSS_WT")));
				mftdispobj.setGrMsmt(String.format("%.2f",
						Double.parseDouble(CommonUtility.deNull(rs.getString("GROSS_VOL")).trim().equals("") ? "0"
								: CommonUtility.deNull(rs.getString("GROSS_VOL")))));
				mftdispobj.setCrgStatus(CommonUtility.deNull(rs.getString("CRG_STATUS")));
				mftdispobj.setDgInd(CommonUtility.deNull(rs.getString("DG_IND")));
				mftdispobj.setStgInd(CommonUtility.deNull(rs.getString("STG_TYPE")));
				mftdispobj.setOpInd(CommonUtility.deNull(rs.getString("DIS_TYPE")));
				pkgnm = getPkgName(rs.getString("PKG_TYPE"));
				mftdispobj.setPkgn(pkgnm);
				mftdispobj.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE")));
				mftdispobj.setConsignee(CommonUtility.deNull(rs.getString("CONS_NM")));
				mftdispobj.setConsigneeCoyCode(CommonUtility.deNull(rs.getString("CONS_CO_CD")));
				mftdispobj.setPortL(CommonUtility.deNull(rs.getString("LD_PORT")));
				mftdispobj.setPortD(CommonUtility.deNull(rs.getString("DIS_PORT")));
				mftdispobj.setPortFD(CommonUtility.deNull(rs.getString("DES_PORT")));
				spol = getPortName(CommonUtility.deNull(rs.getString("LD_PORT")));
				mftdispobj.setPortLn(spol);
				spod = getPortName(CommonUtility.deNull(rs.getString("DIS_PORT")));
				mftdispobj.setPortDn(spod);
				spofd = getPortName(CommonUtility.deNull(rs.getString("DES_PORT")));
				mftdispobj.setPortFDn(spofd);
				mftdispobj.setCntrType(CommonUtility.deNull(rs.getString("CNTR_TYPE")));
				mftdispobj.setCntrSize(CommonUtility.deNull(rs.getString("CNTR_SIZE")));
				mftdispobj.setSeqNo(CommonUtility.deNull(rs.getString("MFT_SEQ_NBR")));
				mftdispobj.setCreateCustCd(CommonUtility.deNull(rs.getString("MANIFEST_CREATE_CD"))); // Added by
																										// thanhnv2
				mftdispobj.setEdonbrpkgs(CommonUtility.deNull(rs.getString("EDO_NBR_PKGS")));
				mftdispobj.setUnStuffCloseStatus(CommonUtility.deNull(rs.getString("unstuff_closed")));

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				mftdispobj.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				mftdispobj.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				mftdispobj.setHsSubCodeDesc(CommonUtility.deNull(rs.getString("HS_SUB_DESC")));
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END
				// MCC set EPC_IND
				String deliveryToEPC = CommonUtility.deNull(rs.getString("EPC_IND"));
				deliveryToEPC = (deliveryToEPC == null || deliveryToEPC.isEmpty() || deliveryToEPC.equalsIgnoreCase("")
						|| deliveryToEPC.trim().length() == 0) ? "N" : deliveryToEPC;
				mftdispobj.setDeliveryToEPC(deliveryToEPC);
				// BEGIN added by Maksym JCMS Smart CR 6.10
				mftdispobj.setCategory(CommonUtility.deNull(rs.getString("CARGO_CATEGORY_CD")));
				mftdispobj.setCloseBJInd(CommonUtility.deNull(rs.getString("GB_CLOSE_BJ_IND")));
				mftdispobj.setCustomHsCode(CommonUtility.deNull(rs.getString("CUSTOM_HS_CODE")));
				mftdispobj.setConsigneeAddr(rs.getString("CONSIGNEE_ADDR"));
				mftdispobj.setShipperNm(rs.getString("SHIPPER_NM"));
				mftdispobj.setShipperAddr(rs.getString("SHIPPER_ADDR"));
				mftdispobj.setNotifyParty(rs.getString("NOTIFY_PARTY"));
				mftdispobj.setNotifyPartyAddr(rs.getString("NOTIFY_PARTY_ADDR"));
				mftdispobj.setPlaceOfDelivery(rs.getString("PLACE_OF_DELIVERY"));
				mftdispobj.setPlaceOfReceipt(rs.getString("PLACE_OF_RECEIPT"));
			}

			sqlmark.append(" SELECT * FROM MFT_MARKINGS WHERE MFT_SQ_NBR =:mftseqno  ");
			sqlcntr.append(" SELECT * FROM BL_CNTR_DETAILS WHERE MFT_SEQ_NBR=:mftseqno ");

			if (mftdispobj.getConsigneeCoyCode() != null && !"OTHERS".equalsIgnoreCase(mftdispobj.getConsigneeCoyCode())
					&& !"".equalsIgnoreCase(mftdispobj.getConsigneeCoyCode())) {
				String sqlConsName = "SELECT co_nm FROM company_code WHERE co_cd=:consigneeCoyCode";
				paramMap.put("consigneeCoyCode", mftdispobj.getConsigneeCoyCode());
				log.info("mftRetrieve SQL 2 (sqlConsName): " + sqlConsName + " paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqlConsName, paramMap);
				if (rs.next()) {
					mftdispobj.setConsignee(CommonUtility.deNull(rs.getString("co_nm")));
				}
				rs = null;
			}

			paramMap.put("mftseqno", mftseqno);
			log.info("mftRetrieve SQL 3 (sqlmark): " + sqlmark.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlmark.toString(), paramMap);
			if (rs.next()) {
				mftdispobj.setCrgMarking(CommonUtility.deNull(rs.getString("MFT_MARKINGS")));
			}
			rs = null;

			paramMap.put("mftseqno", mftseqno);
			log.info("mftRetrieve SQL 4 (sqlcntr): " + sqlcntr.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlcntr.toString(), paramMap);
			int cntrsqno = 0;
			while (rs.next()) {
				cntrsqno = rs.getInt("CNTR_BL_SEQ");
				if (cntrsqno == 1)
					mftdispobj.setCntr1(CommonUtility.deNull(rs.getString("CNTR_NBR")));
				if (cntrsqno == 2)
					mftdispobj.setCntr2(CommonUtility.deNull(rs.getString("CNTR_NBR")));
				if (cntrsqno == 3)
					mftdispobj.setCntr3(CommonUtility.deNull(rs.getString("CNTR_NBR")));
				if (cntrsqno == 4)
					mftdispobj.setCntr4(CommonUtility.deNull(rs.getString("CNTR_NBR")));
			}
			// Added by NS 0n 25-09-20
			StringBuilder hsSubDescCd = new StringBuilder();
			hsSubDescCd.append(" SELECT ");
			hsSubDescCd.append("	CONFIG.MISC_TYPE_NM, ");
			hsSubDescCd.append("	EXT.HS_SUB_DESC_CD ");
			hsSubDescCd.append(" FROM ");
			hsSubDescCd.append("	TOPS.SYSTEM_CONFIG config ");
			hsSubDescCd.append(" LEFT JOIN GBMS.MANIFEST_DETAILS_EXT EXT ON ");
			hsSubDescCd.append("	EXT.HS_SUB_DESC_CD = config.MISC_TYPE_CD ");
			hsSubDescCd.append("	AND config.REC_STATUS = 'A' ");
			hsSubDescCd.append(" WHERE ");
			hsSubDescCd.append("	EXT.MFT_SEQ_NBR =:mftseqno ");
			paramMap.put("mftseqno", mftseqno);
			log.info("mftRetrieve SQL 5 (hsSubDescCd): " + hsSubDescCd.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(hsSubDescCd.toString(), paramMap);
			if (rs.next()) {
				mftdispobj.setSelectedCargo((CommonUtility.deNull(rs.getString("HS_SUB_DESC_CD"))));
				mftdispobj.setSelectedCargoValue((CommonUtility.deNull(rs.getString("MISC_TYPE_NM"))));
			}
			// end
			log.info("END: DAO END Result mftdispobj " + mftdispobj.toString());
		} catch (Exception e) {
			log.info("Exception mftRetrieve : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO END mftdispobj DAO END");
		}
		return mftdispobj;
	}

	@Override
	public String getCrgNm(String crgtyp) throws BusinessException {
		String sql = "";
		String crgnm = "";
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("STAR DAO getCrgNm crgtyp:" + crgtyp);
			sql = "SELECT CRG_TYPE_NM FROM CRG_TYPE WHERE CRG_TYPE_CD=:crgtyp";
			paramMap.put("crgtyp", crgtyp);
			log.info("getCrgNm SQL: " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				crgnm = rs.getString("CRG_TYPE_NM");
			}
			log.info("END:getCrgNm crgnm:" +  CommonUtility.deNull(crgnm));
		} catch (Exception e) {
			log.info("Exception getCrgNm : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getCrgNm DAO END");
		}
		return crgnm;

	}

	private boolean chknbrpkgs(String seqno, String varno, String blno, String nopkgs) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean texist = false;
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: chknbrpkgs DAO seqno:" + seqno + "varno;" + varno + "blno:" + blno + "nopkgs:" + nopkgs);
			sql.append(" SELECT TRANS_NBR_PKGS FROM GB_EDO WHERE TRANS_NBR_PKGS>0 AND MFT_SEQ_NBR=:seqno ");
			sql.append("  AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("chknbrpkgs SQL: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				texist = true;
			} else {
				texist = false;
			}
			log.info("END:chknbrpkgs DAO texist:" + texist);
		} catch (Exception e) {
			log.info("Exception chknbrpkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:chknbrpkgs DAO END");
		}
		return texist;
	}

	private boolean chkBlStatus(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean blstat = false;
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: chkBlStatus DAO seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT BL_STATUS FROM MANIFEST_DETAILS WHERE BL_STATUS='X' AND MFT_SEQ_NBR=:seqno ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("chkBlStatus SQL: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				blstat = true;
			} else {
				blstat = false;
			}
			log.info("END:chkBlStatus blstat:" + blstat);
		} catch (Exception e) {
			log.info("Exception chkBlStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:chkBlStatus DAO END");
		}
		return blstat;
	}

	private String checkEdoCrgStatus(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		String scrgstatus = ""; // int count =0;
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: checkEdoCrgStatus DAO seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT CRG_STATUS FROM MANIFEST_DETAILS WHERE   MFT_SEQ_NBR=:seqno ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("checkEdoCrgStatus SQL: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				scrgstatus = rs.getString("CRG_STATUS");
			}
			log.info("END:checkEdoCrgStatus scrgstatus:" +  CommonUtility.deNull(scrgstatus));
		} catch (Exception e) {
			log.info("Exception checkEdoCrgStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:checkEdoCrgStatus DAO END");
		}
		return scrgstatus;
	}

	private boolean chkEdoCrgStatus(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean bcrgstatus = false;
		int count = 0;
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: chkEdoCrgStatus DAO seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT CRG_STATUS FROM GB_EDO WHERE MFT_SEQ_NBR=:seqno ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("chkEdoCrgStatus SQL: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				if ((rs.getString("CRG_STATUS")).equals("R"))
					count = count + 1;
			}
			if (count > 0)
				bcrgstatus = true;
			
			log.info("END:chkEdoCrgStatus blstat:" + bcrgstatus);
		} catch (Exception e) {
			log.info("Exception chkEdoCrgStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:chkEdoCrgStatus DAO END");
		}
		return bcrgstatus;
	}

	// EndRegion
	// ejb.sessionBeans.gbms.cargo.manifest -->ManifestEJB-->MftUpdationForDPE()
	@Override
	public String MftUpdationForDPE(String usrid, String coCd, String seqno, String varno, String blno, String crgtyp,
			String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc, String mark, String nopkgs, String gwt,
			String gvol, String crgstat, String dgind, String stgind, String dop, String pkgtyp, String coname,
			String consigneeCoyCode, String poL, String poD, String poFD, String cntrtype, String cntrsize,
			String cntr1, String cntr2, String cntr3, String cntr4, String autParty, String adviseBy, String adviseDate,
			String adviseMode, String amendChargedTo, String waiveCharge, String waiveReason, String category,
			String customHsCode, String conAddr, String shipperNm, String shipperAddr, String notifyParty,
			String notifyPartyAddr, String placeofDelivery, String placeofReceipt, List<HsCodeDetails> multiHsCodeList)
			throws BusinessException {
		String MNFA = "MNFA";
		String sqltlog = "";
		SqlRowSet rs = null;
		String strMark = new String();
		String strUpdate = new String();
		String strCntr1 = new String();
		String strCntr2 = new String();
		String strCntr3 = new String();
		String strCntr4 = new String();

		String strMark_trans = new String();
		String strUpdate_trans = new String();
		String strCntr1_trans = new String();
		String strCntr2_trans = new String();
		String strCntr3_trans = new String();
		String strCntr4_trans = new String();

		String edoupd = "";
		String edosql1 = "";

		SqlRowSet rs1 = null;
		SqlRowSet rsEdoTrans = null;

		StringBuffer sb1 = new StringBuffer();
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		try {
			log.info("START: MftUpdationForDPE DAO :" + " coCd: " + coCd + " varno:" + varno + " blno:" + blno
					+ " crgtyp:" + crgtyp + " hscd:" + hscd + " crgdesc:" + crgdesc + " mark:" + mark + " nopkgs:"
					+ nopkgs + " gwt:" + gwt + " gvol" + gvol + " crgstat:" + crgstat + " dgind:" + dgind + " stgind:"
					+ stgind + " dop" + dop + " pkgtyp:" + pkgtyp + " coname:" + coname + " poL:" + poL + " poD:" + poD
					+ " poFD:" + poFD + " cntrtype:" + cntrtype + " cntrsize:" + cntrsize + " cntr1:" + cntr1
					+ " cntr2:" + cntr2 + " cntr3:" + cntr3 + " cntr4:" + cntr4 + " autParty:" + autParty + " usrid:"
					+ usrid + " seqno:" + seqno + " hsSubCodeFr:" + hsSubCodeFr + " hsSubCodeTo:" + hsSubCodeTo
					+ " consigneeCoyCode:" + consigneeCoyCode + " adviseBy:" + adviseBy + " adviseDate:" + adviseDate
					+ " adviseMode:" + adviseMode + " amendChargedTo:" + amendChargedTo + " waiveCharge:" + waiveCharge
					+ " waiveReason:" + waiveReason + " category:" + category + " customHsCode:"
					+ CommonUtility.deNull(customHsCode) + " conAddr:" + CommonUtility.deNull(conAddr) + " notifyParty:"
					+ CommonUtility.deNull(notifyParty) + " notifyPartyAddr:" + CommonUtility.deNull(notifyPartyAddr)
					+ " placeofDelivery:" + CommonUtility.deNull(placeofDelivery) + " placeofReceipt:"
					+ CommonUtility.deNull(placeofReceipt) + " shipperNm:" + CommonUtility.deNull(shipperNm)
					+ " shipperAddr:" + CommonUtility.deNull(shipperAddr) + ", multiHsCodeList : " + multiHsCodeList.toString());

			boolean manStatus = isManClose(varno);
			boolean checkLog = false;
			// HaiTTH1 added on 3/4/2014
			boolean byPassValidationConsineeNm = false;
			// HaiTTH1 ended on 3/4/2014
			try {
				String sqlCheck = "SELECT * FROM MANIFEST_DETAILS  WHERE VAR_NBR=:varno AND BL_NBR=:blno  AND MFT_SEQ_NBR=:seqno";
				paramMap.put("varno", varno);
				paramMap.put("blno", blno);
				paramMap.put("seqno", seqno);
				log.info("MftUpdationForDPE SQL 1 (sqlCheck): " + sqlCheck + " paramMap: " + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlCheck, paramMap);
				int mftseqno = 0;

				while (rs1.next()) {
					// HaiTTH1 added on 3/4/2014
					if (coname != null && !"".equalsIgnoreCase(coname)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CONS_NM")))
							&& !coname.equalsIgnoreCase(rs1.getString("CONS_NM"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = true;
					}
					// HaiTTH1 ended on 3/4/2014

					log.info("900:: ");
					mftseqno = rs1.getInt("MFT_SEQ_NBR");
					log.info("901::" + crgtyp + ":");
					log.info("901.1::" + CommonUtility.deNull(rs1.getString("CRG_TYPE")) + ":");
					if (crgtyp != null && !"".equalsIgnoreCase(crgtyp)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CRG_TYPE")))
							&& !crgtyp.equalsIgnoreCase(rs1.getString("CRG_TYPE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}

					log.info("902:: ");
					if (hscd != null && !"".equalsIgnoreCase(hscd)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("HS_CODE")))
							&& !hscd.equalsIgnoreCase(rs1.getString("HS_CODE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("903:: ");
					if (nopkgs != null && !"".equalsIgnoreCase(nopkgs)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("NBR_PKGS")))
							&& !nopkgs.equalsIgnoreCase(rs1.getString("NBR_PKGS"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("904:: ");
					if (gwt != null && !"".equalsIgnoreCase(gwt)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("GROSS_WT")))
							&& !gwt.equalsIgnoreCase(rs1.getString("GROSS_WT"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("905:: ");
					if (gvol != null && !"".equalsIgnoreCase(gvol)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("GROSS_VOL")))
							&& !gvol.equalsIgnoreCase(rs1.getString("GROSS_VOL"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("906:: ");
					if (crgstat != null && !"".equalsIgnoreCase(crgstat)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CRG_STATUS")))
							&& !crgstat.equalsIgnoreCase(rs1.getString("CRG_STATUS"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("907:: ");
					if (dgind != null && !"".equalsIgnoreCase(dgind)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("DG_IND")))
							&& !dgind.equalsIgnoreCase(rs1.getString("DG_IND"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("908:: ");
					if (stgind != null && !"".equalsIgnoreCase(stgind)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("STG_TYPE")))
							&& !stgind.equalsIgnoreCase(rs1.getString("STG_TYPE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("909:: ");
					if (dop != null && !"".equalsIgnoreCase(dop)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("DIS_TYPE")))
							&& !dop.equalsIgnoreCase(rs1.getString("DIS_TYPE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("910:: ");
					if (pkgtyp != null && !"".equalsIgnoreCase(pkgtyp)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("PKG_TYPE")))
							&& !pkgtyp.equalsIgnoreCase(rs1.getString("PKG_TYPE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("911:: ");
					// if(coname != null && !"".equalsIgnoreCase(coname)
					// && !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CONS_NM"))) && !
					// coname.equalsIgnoreCase(rs1.getString("CONS_NM"))) {
					// if (manStatus) checkLog = true;
					// byPassValidationConsineeNm = false;
					// }
					log.info("912:: ");
					if (poL != null && !"".equalsIgnoreCase(poL)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("LD_PORT")))
							&& !poL.equalsIgnoreCase(rs1.getString("LD_PORT"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("913:: ");
					if (poD != null && !"".equalsIgnoreCase(poD)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("DIS_PORT")))
							&& !poD.equalsIgnoreCase(rs1.getString("DIS_PORT"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("914:: ");
					if (poFD != null && !"".equalsIgnoreCase(poFD)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("DES_PORT")))
							&& !poFD.equalsIgnoreCase(rs1.getString("DES_PORT"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("915:: ");
					if (cntrtype != null && !"".equalsIgnoreCase(cntrtype)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CNTR_TYPE")))
							&& !cntrtype.equalsIgnoreCase(rs1.getString("CNTR_TYPE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("916:: ");
					if (cntrsize != null && !"".equalsIgnoreCase(cntrsize)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CNTR_SIZE")))
							&& !cntrsize.equalsIgnoreCase(rs1.getString("CNTR_SIZE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("917:: ");

					if (autParty != null && !"".equalsIgnoreCase(autParty)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("MANIFEST_CREATE_CD")))
							&& !autParty.equalsIgnoreCase(rs1.getString("MANIFEST_CREATE_CD"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
					}
					log.info("918:: ");

					log.info("vvv2:: " + checkLog + "\n");

					String sqlcntr = "SELECT * FROM BL_CNTR_DETAILS WHERE MFT_SEQ_NBR=:mftseqno";
					log.info("vvv3:: " + checkLog);
					paramMap.put("mftseqno", mftseqno);
					log.info("MftUpdationForDPE SQL 2 (sqlcntr): " + sqlcntr + " paramMap: " + paramMap);
					SqlRowSet rsSqlcntr = namedParameterJdbcTemplate.queryForRowSet(sqlcntr, paramMap);

					int cntrsqno = 0;
					while (rsSqlcntr.next()) {
						log.info("919:: ");
						cntrsqno = rsSqlcntr.getInt("CNTR_BL_SEQ");
						log.info("920:: ");
						if (cntrsqno == 1)
							if (cntr1 != null && !"".equalsIgnoreCase(cntr1)
									&& !"".equalsIgnoreCase(CommonUtility.deNull(rsSqlcntr.getString("CNTR_NBR")))
									&& !cntr1.equalsIgnoreCase(rsSqlcntr.getString("CNTR_NBR"))) {
								if (manStatus)
									checkLog = true;
								byPassValidationConsineeNm = false;
							}
						log.info("921:: ");
						if (cntrsqno == 2)
							if (cntr2 != null && !"".equalsIgnoreCase(cntr2)
									&& !"".equalsIgnoreCase(CommonUtility.deNull(rsSqlcntr.getString("CNTR_NBR")))
									&& !cntr2.equalsIgnoreCase(rsSqlcntr.getString("CNTR_NBR"))) {
								if (manStatus)
									checkLog = true;
								byPassValidationConsineeNm = false;
							}
						log.info("922:: ");
						if (cntrsqno == 3)
							if (cntr3 != null && !"".equalsIgnoreCase(cntr3)
									&& !"".equalsIgnoreCase(CommonUtility.deNull(rsSqlcntr.getString("CNTR_NBR")))
									&& !cntr3.equalsIgnoreCase(rsSqlcntr.getString("CNTR_NBR"))) {
								if (manStatus)
									checkLog = true;
								byPassValidationConsineeNm = false;
							}
						log.info("923:: ");
						if (cntrsqno == 4)
							if (cntr4 != null && !"".equalsIgnoreCase(cntr4)
									&& !"".equalsIgnoreCase(CommonUtility.deNull(rsSqlcntr.getString("CNTR_NBR")))
									&& !cntr4.equalsIgnoreCase(rsSqlcntr.getString("CNTR_NBR"))) {
								if (manStatus)
									checkLog = true;
								byPassValidationConsineeNm = false;
							}
						log.info("924:: ");
					}

					log.info("vvv4:: " + checkLog + "\n");
				}

			} catch (NullPointerException e) {
				log.info("Exception MftUpdationForDPE : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception MftUpdationForDPE : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: *** MftUpdationForDPE END *****");
			}
			chkVslStat(varno);
			// if (vslstat) {
			// log.info("Writing from ManifestEJB.MftUpdation");
			// log.info("Vessel Status is closed cannot Amend" +
			// varno);
			// throw new BusinessException("M21605");
			// }
			boolean edostat = chkEdonbrPkgs(seqno, varno, blno);
			if (edostat && !coCd.equals("JP") && !byPassValidationConsineeNm) {
				log.info("Writing from ManifestEJB.MftUpdation");
				log.info("Edo Created cannot Amend " + blno);
				throw new BusinessException("M20204");
			}
			boolean dnstat = chkDNnbrPkgs(seqno, varno, blno);
			log.info("\n8888 usrid from MFTEJB.Updation coCd" + coCd);
			log.info("======== CASE 1: " + dnstat + " - " + coCd);
			log.info("======== INSIDE CASE 2");
			if (dnstat && !coCd.equals("JP") && !byPassValidationConsineeNm) {
				log.info("Writing from ManifestEJB.MftUpdation");
				log.info("DN Printed cannot Amend" + blno);
				throw new BusinessException("M20206");
			}
			boolean tnstat = chkTnbrPkgs(seqno, varno, blno);
			if (tnstat && !coCd.equals("JP") && !byPassValidationConsineeNm) {
				log.info("Writing from ManifestEJB.MftUpdation");
				log.info("Transhipment done cannot Amend" + blno);
				throw new BusinessException("M20207");
			}
			boolean tdnstat = chkTDNnbrPkgs(seqno, varno, blno);
			if (tdnstat && coCd.equals("JP") && !byPassValidationConsineeNm) {
				log.info("Writing from ManifestEJB.MftUpdation");
				log.info("Transhipment done cannot Amend" + blno);
				throw new BusinessException("M20207");
			}
			boolean bnbrpkgs = chknbrpkgs(seqno, varno, blno, nopkgs);
			if (bnbrpkgs && !byPassValidationConsineeNm) {
				log.info("Writing from ManifestEJB.MftUpdation");
				log.info("Number of packages Less than EDO Nbr pkgs" + nopkgs);
				throw new BusinessException("M20205");
			}
			boolean blstat = chkBlStatus(seqno, varno, blno);
			if (blstat && !byPassValidationConsineeNm) {
				log.info("Writing from ManifestEJB.MftUpdation");
				log.info("BL canceled cannot Amend" + blno);
				throw new BusinessException("M20203");
			}
			String man_crgstatus = checkEdoCrgStatus(seqno, varno, blno);
			if (!crgstat.equals(man_crgstatus) && !byPassValidationConsineeNm) {
				if (chkEdoCrgStatus(seqno, varno, blno)) {
					log.info("Writing from ManifestEJB.MftUpdation");
					log.info("Edo with ReExport cargo cannot Amend crgstatus" + blno);
					throw new BusinessException("M20210");
				}
			}

			boolean Pkgtyp = chkPkgtype(pkgtyp);
			if (!Pkgtyp) {
				log.info("Writing from ManifestEJB.MftUpdation");
				log.info("Invalid Packaging type " + pkgtyp);
				throw new BusinessException("M21604");
			}

			boolean portcdl = chkPortCode(poL);
			if (!portcdl) {
				log.info("Writing from ManifestEJB.MftUpdation");
				log.info("Invalid Port Code " + poL);
				throw new BusinessException("M21601");
			}

			boolean portcdd = chkPortCode(poD);
			if (!portcdd) {
				log.info("Writing from ManifestEJB.MftUpdation");
				log.info("Invalid Port Code " + poD);
				throw new BusinessException("M21602");
			}

			boolean portcfd = chkPortCode(poFD);
			if (poFD != null && !poFD.equals("")) {
				if (!portcfd) {
					log.info("Writing from ManifestEJB.MftUpdation");
					log.info("Invalid Final Destination Port Code " + poFD);
					throw new BusinessException("M21603");
				} // end of if !port..
			} // end of if poFD

			if (dgind.equalsIgnoreCase("Y")) {
				boolean chkDGInd = chkDGInd(blno, varno);
				if (!chkDGInd) {
					log.info("Writing from ManifestEJB.chkDGInd");
					log.info("Manifest creation not allowed ");
					throw new BusinessException("M20212");
				}
			}
			if (dop == null) {
				dop = "N";
			}

			sqltlog = "SELECT MAX(TRANS_NBR) AS trans FROM MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:seqno";

			StringBuilder strUpdte = new StringBuilder();
			strUpdte.append("UPDATE MANIFEST_DETAILS SET CRG_TYPE=:crgtyp,CNTR_SIZE=:cntrsize, ");
			strUpdte.append("CNTR_TYPE=:cntrtype,STG_TYPE=:stgind, ");
			strUpdte.append("DIS_TYPE=:dop,CRG_STATUS=:crgstat,PKG_TYPE=:pkgtyp,NBR_PKGS=:nopkgs, ");
			strUpdte.append("CRG_DES=:crgdesc,GROSS_WT=:gwt,GROSS_VOL=:gvol,DG_IND=:dgind, ");
			strUpdte.append(
					"HS_CODE=:hscd,HS_SUB_CODE_FR=:hsSubCodeFr,HS_SUB_CODE_TO=:hsSubCodeTo,LD_PORT=:poL,DIS_PORT=:poD, ");
			strUpdte.append("DES_PORT=:poFD,LAST_MODIFY_USER_ID=:usrid,LAST_MODIFY_DTTM=SYSDATE,CONS_NM=:coname, ");
			strUpdte.append("CONS_CO_CD=:consigneeCoyCode,CARGO_CATEGORY_CD=:category ,");
			// START CR FTZ - NS Nov 2024
			strUpdte.append(" CUSTOM_HS_CODE=:customHsCode, CONSIGNEE_ADDR=:conAddr, SHIPPER_NM=:shipperNm, ");
			strUpdte.append(" SHIPPER_ADDR=:shipperAddr, NOTIFY_PARTY=:notifyParty, NOTIFY_PARTY_ADDR=:notifyPartyAddr,");
			strUpdte.append(" PLACE_OF_DELIVERY=:placeofDelivery, PLACE_OF_RECEIPT=:placeofReceipt");
			// END CR FTZ - NS Nov 2024
			if ("JP".equals(coCd)) {

				strUpdte.append(", MANIFEST_CREATE_CD=:autParty, ADVISE_BY=:adviseBy, ");
				strUpdte.append("ADVISE_DATE=TO_DATE(:adviseDate,'DD/MM/YYYY HH24:MI:SS'), ADVISE_MODE=:adviseMode, ");
				strUpdte.append(
						"AMEND_CHARGED_TO=:amendChargedTo, WAIVE_CHARGED=:waiveCharge,WAIVE_REASON=:waiveReason");

			} else {
				// strUpdate = strUpdate + ",
				// ADVISE_BY='',ADVISE_DATE=TO_DATE('','DDMMYYYYHH24MI'),
				// ADVISE_MODE='',AMEND_CHARGED_TO='', WAIVE_CHARGED='',WAIVE_REASON='' ";
				// //TODO

			}

			strUpdte.append(" WHERE VAR_NBR=:varno AND BL_NBR=:blno AND MFT_SEQ_NBR=:seqno ");

			sb1.append("SELECT EDO_ASN_NBR,NBR_PKGS,BL_NBR,ADP_CUST_CD,ADP_IC_TDBCR_NBR, ");
			sb1.append("ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR, ");
			sb1.append("AA_NM,ACCT_NBR,PAYMENT_MODE,EDO_DELIVERY_TO,EDO_CREATE_CD,EDO_STATUS, ");
			sb1.append("DN_NBR_PKGS,TRANS_NBR_PKGS,TRANS_DN_NBR_PKGS,RELEASE_NBR_PKGS,APPOINTED_ADP_CUST_CD,  ");
			sb1.append("APPOINTED_ADP_IC_TDBCR_NBR,APPOINTED_ADP_NM, DIS_TYPE FROM GB_EDO  ");
			sb1.append("WHERE VAR_NBR=:varno AND MFT_SEQ_NBR=:seqno and edo_status <> 'X' ");
			edosql1 = sb1.toString();

			log.info("======== BEFORE LOG WHEN UPDATION: ");
			String miscNo = "";
			if ("JP".equalsIgnoreCase(coCd) || (checkLog && manStatus)) {
				miscNo = insertMiscEvtLog(MNFA, varno, blno, usrid);
			}
			log.info("======== AFTER LOG UPDATION");

			String edonbr = "";
			int edonbrpkgs = 0;
			double nom_wt = 0.0;
			double nom_vol = 0.0;
			String strBlNbr = "";
			String strAdpCustCd = "";
			String strAdpIcTdbcrNbr = "";
			String strAdpNm = "";
			String strCaCustCd = "";
			String strCaIcTdbcrNbr = "";
			String strCaNm = "";
			String strAaCustCd = "";
			String strAaIcTdbcrNbr = "";
			String strAaNm = "";
			String strAcctNbr = "";
			String strPaymentMd = "";
			String strEdoDeliveryTo = "";
			String strEdoCreateCd = "";
			String strEdoStatus = "";
			String strDnNbrPkgs = "";
			String strTransNbrPkgs = "";
			String strTransDnNbrPkgs = "";
			String strReleaseNbrPkgs = "";
			String strApptAdpCustCd = "";
			String strApptAdpIcTdbcrNbr = "";
			String strApptAdpNm = "";
			String strDisType = "";

			paramMap.put("varno", varno);
			paramMap.put("seqno", seqno);
			log.info("END: *** MftUpdationForDPE SQL 3 (edosql1): " + edosql1 + " paramMap: " + paramMap);
			rs1 = namedParameterJdbcTemplate.queryForRowSet(edosql1, paramMap);

			while (rs1.next()) {
				edonbr = rs1.getString(1);
				edonbrpkgs = rs1.getInt(2);

				strBlNbr = CommonUtility.deNull(rs1.getString("BL_NBR"));
				strAdpCustCd = CommonUtility.deNull(rs1.getString("ADP_CUST_CD"));
				strAdpIcTdbcrNbr = CommonUtility.deNull(rs1.getString("ADP_IC_TDBCR_NBR"));
				strAdpNm = CommonUtility.deNull(rs1.getString("ADP_NM"));
				strCaCustCd = CommonUtility.deNull(rs1.getString("CA_CUST_CD"));
				strCaIcTdbcrNbr = CommonUtility.deNull(rs1.getString("CA_IC_TDBCR_NBR"));
				strCaNm = CommonUtility.deNull(rs1.getString("CA_NM"));
				strAaCustCd = CommonUtility.deNull(rs1.getString("AA_CUST_CD"));
				strAaIcTdbcrNbr = CommonUtility.deNull(rs1.getString("AA_IC_TDBCR_NBR"));
				strAaNm = CommonUtility.deNull(rs1.getString("AA_NM"));
				strAcctNbr = CommonUtility.deNull(rs1.getString("ACCT_NBR"));
				strPaymentMd = CommonUtility.deNull(rs1.getString("PAYMENT_MODE"));
				strEdoDeliveryTo = CommonUtility.deNull(rs1.getString("EDO_DELIVERY_TO"));
				strEdoCreateCd = CommonUtility.deNull(rs1.getString("EDO_CREATE_CD"));
				strEdoStatus = CommonUtility.deNull(rs1.getString("EDO_STATUS"));
				strDnNbrPkgs = CommonUtility.deNull(rs1.getString("DN_NBR_PKGS"));
				strTransNbrPkgs = CommonUtility.deNull(rs1.getString("TRANS_NBR_PKGS"));
				strTransDnNbrPkgs = CommonUtility.deNull(rs1.getString("TRANS_DN_NBR_PKGS"));
				strReleaseNbrPkgs = CommonUtility.deNull(rs1.getString("RELEASE_NBR_PKGS"));
				strApptAdpCustCd = CommonUtility.deNull(rs1.getString("APPOINTED_ADP_CUST_CD"));
				strApptAdpIcTdbcrNbr = CommonUtility.deNull(rs1.getString("APPOINTED_ADP_IC_TDBCR_NBR"));
				strApptAdpNm = CommonUtility.deNull(rs1.getString("APPOINTED_ADP_NM"));
				strDisType = CommonUtility.deNull(rs1.getString("DIS_TYPE"));

//				nom_wt = ((double) edonbrpkgs / Double.parseDouble(nopkgs)) * Double.parseDouble(gwt);
//				nom_vol = ((double) edonbrpkgs / Double.parseDouble(nopkgs)) * Double.parseDouble(gvol);
				
				// update for multihscode puprose - NS JAN 2025
				String getEdoHScode = "SELECT NBR_PKGS,GROSS_WT,GROSS_VOL FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE EDO_ASN_NBR = :edonbr";
				paramMap.put("edonbr", edonbr);
				SqlRowSet rsEdoHs = namedParameterJdbcTemplate.queryForRowSet(getEdoHScode, paramMap);
				if(rsEdoHs.next()) {
					String nopkgshs = rsEdoHs.getString("NBR_PKGS");
					String gwths = rsEdoHs.getString("GROSS_WT");
					String gvolhs = rsEdoHs.getString("GROSS_VOL");						
 
					nom_wt = ((double) edonbrpkgs / Double.parseDouble(nopkgshs)) * Double.parseDouble(gwths);
					nom_vol = ((double) edonbrpkgs / Double.parseDouble(nopkgshs)) * Double.parseDouble(gvolhs);
				}else {
					nom_wt = ((double) edonbrpkgs / Double.parseDouble(nopkgs)) * Double.parseDouble(gwt);
					nom_vol = ((double) edonbrpkgs / Double.parseDouble(nopkgs)) * Double.parseDouble(gvol);
				}
 

				java.math.BigDecimal bdWt = new java.math.BigDecimal(nom_wt).setScale(2,
						java.math.BigDecimal.ROUND_HALF_UP);
				java.math.BigDecimal bdVol = new java.math.BigDecimal(nom_vol).setScale(2,
						java.math.BigDecimal.ROUND_HALF_UP);

				edoupd = "UPDATE GB_EDO SET CRG_STATUS=:crgstat, NOM_WT=:bdWt,NOM_VOL=:bdVol WHERE EDO_ASN_NBR =:edonbr AND MFT_SEQ_NBR =:seqno AND VAR_NBR=:varno ";

				String sqlEdoTransNbr = "SELECT MAX(TRANS_NBR) trans FROM GB_EDO_TRANS WHERE EDO_ASN_NBR=:edonbr";

				int iEdoTransNo = 0;
				if (logStatusGlobal.equalsIgnoreCase("Y")) {

					paramMap.put("edonbr", edonbr);
					log.info("END: *** MftUpdationForDPE SQL 4 (sqlEdoTransNbr): " + sqlEdoTransNbr.toString() + " paramMap: " + paramMap);
					rsEdoTrans = namedParameterJdbcTemplate.queryForRowSet(sqlEdoTransNbr, paramMap);

					if (rsEdoTrans.next()) {
						iEdoTransNo = (rsEdoTrans.getInt(1)) + 1;
					} else {
						iEdoTransNo = 0;
					}
				}

				paramMap.put("crgstat", crgstat);
				paramMap.put("bdWt", bdWt);
				paramMap.put("bdVol", bdVol);
				paramMap.put("edonbr", edonbr);
				paramMap.put("seqno", seqno);
				paramMap.put("varno", varno);
				log.info("END: *** MftUpdationForDPE SQL 5 (edoupd): " + edoupd.toString() + " paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(edoupd, paramMap);
;
				String strEdoTransSql = "";
				StringBuffer strEdoTrans = new StringBuffer();
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					strEdoTrans.append("INSERT INTO GB_EDO_TRANS(EDO_ASN_NBR,TRANS_NBR,VAR_NBR,MFT_SEQ_NBR,  ");
					strEdoTrans.append("BL_NBR,NBR_PKGS,NOM_WT,NOM_VOL,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,  ");
					strEdoTrans.append("CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,AA_NM, ");
					strEdoTrans.append("ACCT_NBR,PAYMENT_MODE,EDO_DELIVERY_TO,EDO_CREATE_CD,EDO_STATUS,");
					strEdoTrans.append("DN_NBR_PKGS,TRANS_NBR_PKGS,TRANS_DN_NBR_PKGS,LAST_MODIFY_USER_ID, ");
					strEdoTrans.append("LAST_MODIFY_DTTM,RELEASE_NBR_PKGS,APPOINTED_ADP_CUST_CD, ");
					strEdoTrans.append("APPOINTED_ADP_IC_TDBCR_NBR,APPOINTED_ADP_NM, DIS_TYPE)");
					strEdoTrans
							.append(" VALUES(:edonbr,:iEdoTransNo,:varno,:seqno,:strBlNbr,:edonbrpkgs,:bdWt,:bdVol, ");
					strEdoTrans.append(
							":strAdpCustCd,:strAdpIcTdbcrNbr,:strAdpNm,:strCaCustCd,:strCaIcTdbcrNbr,:strCaNm, ");
					strEdoTrans.append(
							":strAaCustCd,:strAaIcTdbcrNbr,:strAaNm,:strAcctNbr,:strPaymentMd,:strEdoDeliveryTo, ");
					strEdoTrans.append(
							":strEdoCreateCd,:strEdoStatus,:strDnNbrPkgs,:strTransNbrPkgs,:strTransDnNbrPkgs, ");
					strEdoTrans.append(
							":usrid,SYSDATE,:strReleaseNbrPkgs,:strApptAdpCustCd,:strApptAdpIcTdbcrNbr,:strApptAdpNm,:strDisType )");

					strEdoTransSql = strEdoTrans.toString();
					paramMap.put("edonbr", edonbr);
					paramMap.put("iEdoTransNo", iEdoTransNo);
					paramMap.put("varno", varno);
					paramMap.put("seqno", seqno);
					paramMap.put("strBlNbr", strBlNbr);
					paramMap.put("edonbrpkgs", edonbrpkgs);
					paramMap.put("bdWt", bdWt);
					paramMap.put("bdVol", bdVol);
					paramMap.put("strAdpCustCd", strAdpCustCd);
					paramMap.put("strAdpIcTdbcrNbr", strAdpIcTdbcrNbr);
					paramMap.put("strAdpNm", strAdpNm);
					paramMap.put("strCaCustCd", strCaCustCd);
					paramMap.put("strCaIcTdbcrNbr", strCaIcTdbcrNbr);
					paramMap.put("strCaNm", strCaNm);
					paramMap.put("strAaCustCd", strAaCustCd);
					paramMap.put("strAaIcTdbcrNbr", strAaIcTdbcrNbr);
					paramMap.put("strAaNm", strAaNm);
					paramMap.put("strAcctNbr", strAcctNbr);
					paramMap.put("strPaymentMd", strPaymentMd);
					paramMap.put("strEdoDeliveryTo", strEdoDeliveryTo);
					paramMap.put("strEdoCreateCd", strEdoCreateCd);
					paramMap.put("strEdoStatus", strEdoStatus);
					paramMap.put("strDnNbrPkgs", strDnNbrPkgs);
					paramMap.put("strTransNbrPkgs", strTransNbrPkgs);
					paramMap.put("strTransDnNbrPkgs", strTransDnNbrPkgs);
					paramMap.put("usrid", usrid);
					paramMap.put("strReleaseNbrPkgs", strReleaseNbrPkgs);
					paramMap.put("strApptAdpCustCd", strApptAdpCustCd);
					paramMap.put("strApptAdpIcTdbcrNbr", strApptAdpIcTdbcrNbr);
					paramMap.put("strApptAdpNm", strApptAdpNm);
					paramMap.put("strDisType", strDisType);
					log.info("END: *** MftUpdationForDPE SQL 6 (strEdoTransSql): *****" + strEdoTransSql + " paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strEdoTransSql, paramMap);

				}
			}

			strMark = strMark
					+ "UPDATE MFT_MARKINGS SET MFT_MARKINGS=:mark ,LAST_MODIFY_USER_ID=:usrid,LAST_MODIFY_DTTM=SYSDATE WHERE MFT_SQ_NBR=:seqno";

			int stransno = 0;

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				paramMap.put("seqno", seqno);
				log.info("Params:seqno = " + seqno + "*** MftUpdationForDPE SQL 6 (sqltlog) *****" + sqltlog);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}
			strUpdate = strUpdte.toString();
			log.info("Before MftUpdation: SQL :" + " - " + strUpdate);
			paramMap.put("crgtyp", crgtyp);
			paramMap.put("cntrsize", CommonUtility.deNull(cntrsize));
			paramMap.put("cntrtype", CommonUtility.deNull(cntrtype));
			paramMap.put("stgind", CommonUtility.deNull(stgind));
			paramMap.put("dop", CommonUtility.deNull(dop));
			paramMap.put("crgstat", crgstat);
			paramMap.put("pkgtyp", pkgtyp);
			paramMap.put("nopkgs", nopkgs);
			paramMap.put("crgdesc", GbmsCommonUtility.addApostr(crgdesc));
			paramMap.put("gwt", gwt);
			paramMap.put("gvol", gvol);
			paramMap.put("dgind", CommonUtility.deNull(dgind));
			paramMap.put("hscd", hscd);
			paramMap.put("hsSubCodeFr", hsSubCodeFr);
			paramMap.put("hsSubCodeTo", hsSubCodeTo);
			paramMap.put("poL", poL);
			paramMap.put("poD", poD);
			paramMap.put("poFD", CommonUtility.deNull(poFD));
			paramMap.put("usrid", usrid);
			paramMap.put("coname", GbmsCommonUtility.addApostr(coname));
			paramMap.put("consigneeCoyCode", consigneeCoyCode);
			paramMap.put("category", CommonUtility.deNull(category));
			paramMap.put("autParty", CommonUtility.deNull(autParty));
			paramMap.put("adviseBy", adviseBy);
			paramMap.put("adviseDate", adviseDate);
			paramMap.put("adviseMode", adviseMode);
			paramMap.put("amendChargedTo", amendChargedTo);
			paramMap.put("waiveCharge", waiveCharge);
			paramMap.put("waiveReason", waiveReason);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			paramMap.put("seqno", seqno);
			// START CR FTZ - NS NOV 2024
			paramMap.put("conAddr", CommonUtility.deNull(conAddr));
			paramMap.put("notifyParty", CommonUtility.deNull(notifyParty));
			paramMap.put("notifyPartyAddr", CommonUtility.deNull(notifyPartyAddr));
			paramMap.put("placeofReceipt", CommonUtility.deNull(placeofReceipt));
			paramMap.put("placeofDelivery", CommonUtility.deNull(placeofDelivery));
			paramMap.put("shipperNm", CommonUtility.deNull(shipperNm));
			paramMap.put("shipperAddr", CommonUtility.deNull(shipperAddr));
			paramMap.put("customHsCode", CommonUtility.deNull(customHsCode));
			// START CR FTZ - NS NOV 2024

			paramMap.put("EDO_ASN_NBR", edonbr);			
			log.info("MftUpdationForDPE SQL 7: " + strUpdate + " paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(strUpdate, paramMap);
			if(count > 0) {
				// Start CR FTZ HSCODE - NS JULY 2024
				StringBuffer sb = new StringBuffer();
				for (HsCodeDetails hsCodeObj : multiHsCodeList) {
					
					java.math.BigDecimal bdWt = new java.math.BigDecimal(nom_wt).setScale(2,
							java.math.BigDecimal.ROUND_HALF_UP);
					java.math.BigDecimal bdVol = new java.math.BigDecimal(nom_vol).setScale(2,
							java.math.BigDecimal.ROUND_HALF_UP);
					
					paramMap.put("MFT_SEQ_NBR", seqno);
					paramMap.put("HS_CODE",hsCodeObj.getHsCode());
					paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
					paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
					paramMap.put("NBR_PKGS",edonbrpkgs);
					paramMap.put("GROSS_WT",bdWt);
					paramMap.put("GROSS_VOL",bdVol);
					paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
					paramMap.put("CRG_DES",hsCodeObj.getCrgDes());	
					paramMap.put("HS_SUB_CODE_DESC",hsCodeObj.getHsSubCodeDesc());	
					paramMap.put("userId", usrid);
				
					
					if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("A")) { // Add
						sb.setLength(0);
						sb.append(" SELECT COUNT(*) FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ");
						paramMap.put("MFT_SEQ_NBR", seqno);
						log.info("SQL" + sb.toString());
						log.info("params: " + paramMap.toString());						
						int countEdo = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
						
						if(multiHsCodeList.size() > 1 && countEdo > 1) {
							throw new BusinessException("Unable to add any HS Code details. Please delete EDO first.");	
						} else {
							// if no GB_EDO_HSCODE_DETAILS, means old data, inserted into GB_EDO_HSCODE_DETAILS. 
							// get EDO_HSCODE_SEQ_NBR 
							StringBuilder sbSeq = new StringBuilder();
							sbSeq.append("SELECT GBMS.SEQ_EDO_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
							Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
							BigDecimal seqValue = (BigDecimal) results.get("seqVal");
							log.info("seqValue" + seqValue);
							// end
							
							// GET MFT_HSCODE_SEQ_NBR, ELSE INSERT NEW ONE
							sb.setLength(0);
							sb.append(" SELECT MFT_HSCODE_SEQ_NBR FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR AND  ");
							sb.append(" HS_CODE = :HS_CODE AND  HS_SUB_CODE_FR = :HS_SUB_CODE_FR AND HS_SUB_CODE_TO = :HS_SUB_CODE_TO ");
							paramMap.put("MFT_SEQ_NBR", seqno);
							paramMap.put("HS_CODE",hsCodeObj.getHsCode());
							paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
							paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
							log.info("SQL" + sb.toString());
							log.info("params: " + paramMap.toString());
							SqlRowSet rsMft = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
							if (!rsMft.next()) {
								// manifest_hsc-de_details also empty
								// get MFT_HSCODE_SEQ_NBR 
								StringBuilder sbSeq2 = new StringBuilder();
								sbSeq2.append("SELECT GBMS.SEQ_MFT_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
								Map<String, Object> results2 = namedParameterJdbcTemplate.queryForMap(sbSeq2.toString(), new HashMap<String, String>());
								BigDecimal seqValue2 = (BigDecimal) results2.get("seqVal");
								log.info("seqValue : "+ seqValue2);
								// end
								
								sb.setLength(0);
								sb.append(" INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS  ");
								sb.append(" (MFT_SEQ_NBR,MFT_HSCODE_SEQ_NBR,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
								sb.append(" VALUES(:MFT_SEQ_NBR,:MFT_HSCODE_SEQ_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:MFT_NBR_PKGS,:MFT_GROSS_WT,:MFT_GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
								
								
								paramMap.put("MFT_HSCODE_SEQ_NBR", seqValue2);
								paramMap.put("REC_STATUS", "A");
								paramMap.put("MFT_NBR_PKGS", hsCodeObj.getNbrPkgs());
								paramMap.put("MFT_GROSS_WT", hsCodeObj.getGrossWt());
								paramMap.put("MFT_GROSS_VOL", hsCodeObj.getGrossVol());
								log.info("SQL" + sb.toString());
								log.info("params: " + paramMap.toString());
								int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
								log.info("counths : " + counths);
								
							}else {
								paramMap.put("MFT_HSCODE_SEQ_NBR", rsMft.getString("MFT_HSCODE_SEQ_NBR"));
							}
							
							sb.setLength(0);
							sb.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS  ");
							sb.append(" (MFT_SEQ_NBR,EDO_HSCODE_SEQ_NBR, MFT_HSCODE_SEQ_NBR,EDO_ASN_NBR,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,CUSTOM_HS_CODE,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL) ");
							sb.append(" VALUES(:MFT_SEQ_NBR,:EDO_HSCODE_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, :EDO_ASN_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:CUSTOM_HS_CODE,:userId,SYSDATE, :NBR_PKGS, :GROSS_WT, :GROSS_VOL) ");
							
							paramMap.put("EDO_HSCODE_SEQ_NBR", seqValue);
							paramMap.put("EDO_ASN_NBR", edonbr);
							log.info("SQL" + sb.toString());
							log.info("params: " + paramMap.toString());
							int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
							log.info("counths : " + counths);
							
							sb.setLength(0);
							sb.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS_TRANS  ");
							sb.append(" (MFT_SEQ_NBR,EDO_HSCODE_SEQ_NBR, MFT_HSCODE_SEQ_NBR,EDO_ASN_NBR, AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,CUSTOM_HS_CODE,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL) ");
							sb.append(" VALUES(:MFT_SEQ_NBR,:EDO_HSCODE_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, :EDO_ASN_NBR, SYSDATE,'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:CUSTOM_HS_CODE,:userId,SYSDATE, :NBR_PKGS, :GROSS_WT, :GROSS_VOL) ");
							log.info("SQL" + sb.toString());
							log.info("paramMap" + paramMap.toString());
							int counthsAudit = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
							log.info("counthsAudit : " + counthsAudit);
						}
					}else if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("E")) { // Edit
						
						sb.setLength(0);
						sb.append(" SELECT * FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR AND EDO_ASN_NBR = :EDO_ASN_NBR ");
						paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
						paramMap.put("EDO_ASN_NBR", edonbr);
						log.info("SQL" + sb.toString());
						log.info("params: " + paramMap.toString());
						SqlRowSet rsEdo = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
						if(rsEdo.next()) {

							// compare if only custom hscode is change
							Integer pkgS =  NumberUtils.parseNumber(rsEdo.getString("NBR_PKGS"), Integer.class);
							BigDecimal wtS = NumberUtils.parseNumber(rsEdo.getString("GROSS_WT"), BigDecimal.class);
							BigDecimal volS = NumberUtils.parseNumber(rsEdo.getString("GROSS_VOL"), BigDecimal.class);
							
							log.info(rsEdo.getString("HS_CODE") + "=" + (hsCodeObj.getHsCode()) + ","
									+ rsEdo.getString("HS_SUB_CODE_FR") + "=" + (hsCodeObj.getHsSubCodeFr()) + ","
									+ rsEdo.getString("HS_SUB_CODE_TO") + "=" + (hsCodeObj.getHsSubCodeTo()) + ","
									+ rsEdo.getString("CUSTOM_HS_CODE") + "=" + (hsCodeObj.getCustomHsCode()) + ","
									+ String.valueOf(pkgS) + "=" + (String.valueOf(edonbrpkgs)) + ","
									+ String.valueOf(wtS) + "=" + (String.valueOf(bdWt)) + ","
									+ String.valueOf(volS) + "=" + (String.valueOf(bdVol)));
							if (rsEdo.getString("HS_CODE").equalsIgnoreCase(hsCodeObj.getHsCode())
									&& rsEdo.getString("HS_SUB_CODE_FR").equalsIgnoreCase(hsCodeObj.getHsSubCodeFr())
									&& rsEdo.getString("HS_SUB_CODE_TO").equalsIgnoreCase(hsCodeObj.getHsSubCodeTo())
									&& pkgS.compareTo(edonbrpkgs) == 0
									&& wtS.compareTo(bdWt) == 0
									&& volS.compareTo(bdVol) == 0
									&& !CommonUtil.deNull(rsEdo.getString("CUSTOM_HS_CODE")).equalsIgnoreCase(hsCodeObj.getCustomHsCode())) {

								log.info("EDO created. Only update customHSCode");
								sb.setLength(0);
								sb.append(" UPDATE GBMS.GB_EDO_HSCODE_DETAILS SET ");
								sb.append(" CUSTOM_HS_CODE=:CUSTOM_HS_CODE, ");
								sb.append(" LAST_MODIFY_USER_ID=:userId, LAST_MODIFY_DTTM=SYSDATE WHERE");
								sb.append(" MFT_SEQ_NBR = :MFT_SEQ_NBR AND MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");

								paramMap.put("MFT_SEQ_NBR", seqno);
								log.info("SQL" + sb.toString());
								log.info("params: " + paramMap.toString());
								int countEDO = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
								log.info("countEDO : " + countEDO);

								sb.setLength(0);
								sb.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS_TRANS  ");
								sb.append(" (MFT_SEQ_NBR,EDO_HSCODE_SEQ_NBR, MFT_HSCODE_SEQ_NBR,EDO_ASN_NBR, AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,CUSTOM_HS_CODE,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL) ");
								sb.append(" VALUES(:MFT_SEQ_NBR,:EDO_HSCODE_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, :EDO_ASN_NBR, SYSDATE,'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:CUSTOM_HS_CODE,:userId,SYSDATE, :NBR_PKGS, :GROSS_WT, :GROSS_VOL) ");
								paramMap.put("EDO_HSCODE_SEQ_NBR", rsEdo.getString("EDO_HSCODE_SEQ_NBR"));
								paramMap.put("EDO_ASN_NBR", rsEdo.getString("EDO_ASN_NBR"));
								log.info("SQL" + sb.toString());
								log.info("paramMap" + paramMap.toString());
								int counthsAudit = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
								log.info("counthsAuditEdo : " + counthsAudit);
								
							} else {
								// If other than custom hs code changes, check if its single or multiple hs sub code
								sb.setLength(0);
								sb.append("SELECT COUNT(*) FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_SEQ_NBR = :MFT_SEQ_NBR ");
								paramMap.put("MFT_SEQ_NBR", seqno);
								int countIsMultiple = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
								if (countIsMultiple > 1) { // If multiple hs sub code, dont allow
									throw new BusinessException(
											"Unable to change any HS Code details. Please delete EDO first.");
								} else {
									sb.setLength(0);
									sb.append(" SELECT COUNT(*) FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ");
									paramMap.put("MFT_SEQ_NBR", seqno);
									log.info("SQL" + sb.toString());
									log.info("params: " + paramMap.toString());						
									int countEdo = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
									
									if(multiHsCodeList.size() > 1 && countEdo>  1) { // If single hs sub code but adding new sub hs code, dont allow
										throw new BusinessException("Unable to add any HS Code details. Please delete EDO first.");	
									}
									log.info("Can update all");
									sb.setLength(0);
									sb.append(" UPDATE GBMS.GB_EDO_HSCODE_DETAILS SET ");
									sb.append(" HS_CODE=:HS_CODE, HS_SUB_CODE_FR=:HS_SUB_CODE_FR,HS_SUB_CODE_TO=:HS_SUB_CODE_TO,");
									sb.append("	NBR_PKGS=:NBR_PKGS, GROSS_WT=:GROSS_WT, GROSS_VOL=:GROSS_VOL,  ");
									sb.append(" CUSTOM_HS_CODE=:CUSTOM_HS_CODE, ");
									sb.append(" LAST_MODIFY_USER_ID=:userId, LAST_MODIFY_DTTM=SYSDATE WHERE");
									sb.append(" MFT_SEQ_NBR = :MFT_SEQ_NBR AND MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");

									paramMap.put("MFT_SEQ_NBR", seqno);
									log.info("SQL" + sb.toString());
									log.info("params: " + paramMap.toString());
									int countEDO = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
									log.info("countEDO : " + countEDO);

									sb.setLength(0);
									sb.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS_TRANS  ");
									sb.append(" (MFT_SEQ_NBR,EDO_HSCODE_SEQ_NBR, MFT_HSCODE_SEQ_NBR,EDO_ASN_NBR, AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,CUSTOM_HS_CODE,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL) ");
									sb.append(" VALUES(:MFT_SEQ_NBR,:EDO_HSCODE_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, :EDO_ASN_NBR, SYSDATE,'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:CUSTOM_HS_CODE,:userId,SYSDATE, :NBR_PKGS, :GROSS_WT, :GROSS_VOL) ");
									paramMap.put("EDO_HSCODE_SEQ_NBR", rsEdo.getString("EDO_HSCODE_SEQ_NBR"));
									paramMap.put("EDO_ASN_NBR", rsEdo.getString("EDO_ASN_NBR"));
									log.info("SQL" + sb.toString());
									log.info("paramMap" + paramMap.toString());
									int counthsAudit = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
									log.info("counthsAuditEdo : " + counthsAudit);
								}
							}
						} else {
							// if manifest hscode not yet created in gb_edo but the asn already in gb_edo ,
							// means it is multiplehscode
							// if only custom change, can update
							// else return error
							// compare if only custom hscode is change

							sb.setLength(0);
							sb.append(
									" SELECT * FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
							paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
							log.info("SQL" + sb.toString());
							log.info("params: " + paramMap.toString());
							SqlRowSet rsMft = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
							if (rsMft.next()) {
								log.info(rsMft.getString("HS_CODE") + "=" + (hsCodeObj.getHsCode()) + ","
										+ rsMft.getString("HS_SUB_CODE_FR") + "=" + (hsCodeObj.getHsSubCodeFr()) + ","
										+ rsMft.getString("HS_SUB_CODE_TO") + "=" + (hsCodeObj.getHsSubCodeTo()) + ","
										+ rsMft.getString("CUSTOM_HS_CODE") + "=" + (hsCodeObj.getCustomHsCode()) + ","
										+ rsMft.getString("NBR_PKGS") + "=" + (hsCodeObj.getNbrPkgs()) + ","
										+ rsMft.getString("GROSS_WT") + "=" + (hsCodeObj.getGrossWt()) + ","
										+ rsMft.getString("GROSS_VOL") + "=" + (hsCodeObj.getGrossVol()));
								if (!rsMft.getString("HS_CODE").equalsIgnoreCase(hsCodeObj.getHsCode())
										|| !rsMft.getString("HS_SUB_CODE_FR").equalsIgnoreCase(hsCodeObj.getHsSubCodeFr())
										|| !rsMft.getString("HS_SUB_CODE_TO").equalsIgnoreCase(hsCodeObj.getHsSubCodeTo())
										|| !rsMft.getString("CRG_DES").equalsIgnoreCase(hsCodeObj.getCrgDes())
										|| !rsMft.getString("NBR_PKGS").equalsIgnoreCase(hsCodeObj.getNbrPkgs())
										|| !rsMft.getString("GROSS_WT").equalsIgnoreCase(hsCodeObj.getGrossWt())
										|| !rsMft.getString("GROSS_VOL").equalsIgnoreCase(hsCodeObj.getGrossVol())) {

									throw new BusinessException(
											"Unable to change any HS Code details. Please delete EDO first.");

								}
							}
							
							
							// old data that does not have gb_edo_hscode_details, auto create
							// get EDO_HSCODE_SEQ_NBR 					

							sb.setLength(0);
							sb.append(" SELECT EDO_ASN_NBR FROM GBMS.GB_EDO WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ");
							paramMap.put("MFT_SEQ_NBR", seqno);
							SqlRowSet countCheck = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
							if (countCheck.next()) {
								
								// check if some is exist in hscode table, thus not old data
								sb.setLength(0);
								sb.append(" SELECT * FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE EDO_ASN_NBR=:EDO_ASN_NBR ");
								paramMap.put("EDO_ASN_NBR", edonbr);
								log.info("SQL" + sb.toString());
								log.info("params: " + paramMap.toString());
								SqlRowSet rsEdoChk = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
								if(!rsEdoChk.next()) {
								
									StringBuilder sbSeq = new StringBuilder();
									sbSeq.append("SELECT GBMS.SEQ_EDO_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
									Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
									BigDecimal seqValue = (BigDecimal) results.get("seqVal");
									log.info("seqValue" + seqValue);
									// end
									
									
									sb.setLength(0);
									sb.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS  ");
									sb.append(" (MFT_SEQ_NBR,EDO_HSCODE_SEQ_NBR, MFT_HSCODE_SEQ_NBR,EDO_ASN_NBR,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,CUSTOM_HS_CODE,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL) ");
									sb.append(" VALUES(:MFT_SEQ_NBR,:EDO_HSCODE_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, :EDO_ASN_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:CUSTOM_HS_CODE,:userId,SYSDATE, :NBR_PKGS, :GROSS_WT, :GROSS_VOL) ");
									paramMap.put("EDO_HSCODE_SEQ_NBR", seqValue);
									paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
									paramMap.put("EDO_ASN_NBR", Integer.valueOf(countCheck.getString("EDO_ASN_NBR")));
									log.info("SQL" + sb.toString());
									log.info("paramMap" + paramMap.toString());
									int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
									log.info("counths : " + counths);
									
									sb.setLength(0);
									sb.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS_TRANS  ");
									sb.append(" (MFT_SEQ_NBR,EDO_HSCODE_SEQ_NBR, MFT_HSCODE_SEQ_NBR,EDO_ASN_NBR, AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,CUSTOM_HS_CODE,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL) ");
									sb.append(" VALUES(:MFT_SEQ_NBR,:EDO_HSCODE_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, :EDO_ASN_NBR, SYSDATE,'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:CUSTOM_HS_CODE,:userId,SYSDATE, :NBR_PKGS, :GROSS_WT, :GROSS_VOL) ");
									log.info("SQL" + sb.toString());
									log.info("paramMap" + paramMap.toString());
									int counthsAudit = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
									log.info("counthsAudit : " + counthsAudit);
									}
							}
							
						}
						
						sb.setLength(0);
						sb.append(" SELECT * FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ");
						paramMap.put("MFT_SEQ_NBR", seqno);
						log.info("SQL" + sb.toString());
						log.info("params: " + paramMap.toString());
						SqlRowSet rsMft = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
						if(rsMft.next()) {
							sb.setLength(0);
							sb.append(" UPDATE GBMS.MANIFEST_HSCODE_DETAILS SET HS_CODE=:HS_CODE, HS_SUB_CODE_FR=:HS_SUB_CODE_FR, HS_SUB_CODE_TO=:HS_SUB_CODE_TO,");
							sb.append(" NBR_PKGS=:MFT_NBR_PKGS, GROSS_WT=:MFT_GROSS_WT, GROSS_VOL=:MFT_GROSS_VOL,CUSTOM_HS_CODE=:CUSTOM_HS_CODE, CRG_DES=:CRG_DES, ");
							sb.append(" HS_SUB_CODE_DESC=:HS_SUB_CODE_DESC, LAST_MODIFY_USER_ID=:userId, LAST_MODIFY_DTTM=SYSDATE WHERE");
							sb.append(" MFT_SEQ_NBR = :MFT_SEQ_NBR AND MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
							
							paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
							paramMap.put("MFT_NBR_PKGS", hsCodeObj.getNbrPkgs());
							paramMap.put("MFT_GROSS_WT", hsCodeObj.getGrossWt());
							paramMap.put("MFT_GROSS_VOL", hsCodeObj.getGrossVol());
							paramMap.put("REC_STATUS", "A");
							
							log.info("SQL" + sb.toString());
							log.info("params: " + paramMap.toString());
							int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
							log.info("counths : " + counths);
							
						} else {
							// get MFT_HSCODE_SEQ_NBR 
							StringBuilder sbSeq = new StringBuilder();
							sbSeq.append("SELECT GBMS.SEQ_MFT_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
							Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
							BigDecimal seqValue = (BigDecimal) results.get("seqVal");
							log.info("seqValue : "+ seqValue);
							// end
							
							sb.setLength(0);
							sb.append(" INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS  ");
							sb.append(" (MFT_SEQ_NBR,MFT_HSCODE_SEQ_NBR,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
							sb.append(" VALUES(:MFT_SEQ_NBR,:MFT_HSCODE_SEQ_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:MFT_NBR_PKGS,:MFT_GROSS_WT,:MFT_GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
							
							
							paramMap.put("MFT_HSCODE_SEQ_NBR", seqValue);
							paramMap.put("REC_STATUS", "A");
							paramMap.put("MFT_NBR_PKGS", hsCodeObj.getNbrPkgs());
							paramMap.put("MFT_GROSS_WT", hsCodeObj.getGrossWt());
							paramMap.put("MFT_GROSS_VOL", hsCodeObj.getGrossVol());
							log.info("SQL" + sb.toString());
							log.info("params: " + paramMap.toString());
							int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
							log.info("counths : " + counths);
							
						}
						
						sb.setLength(0);
						sb.append(" INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS  ");
						sb.append(" (MFT_SEQ_NBR,MFT_HSCODE_SEQ_NBR,AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
						sb.append(" VALUES(:MFT_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, SYSDATE, :REC_STATUS,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:MFT_NBR_PKGS,:MFT_GROSS_WT,:MFT_GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");

						log.info("SQL" + sb.toString());
						log.info("params: " + paramMap.toString());
						int counthsAudit = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						log.info("counths : " + counthsAudit);
						
						
					}else if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("D")) {
						throw new BusinessException("Unable to change any HS Code details. Please delete EDO first.");
					}
			
				}
				
				// END CR FTZ HSCODE - NS JULY 2024
			}

			log.info("After MftUpdation: " + count + " - " + strUpdate.toString());
			paramMap.put("usrid", usrid);
			paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
			paramMap.put("seqno", seqno);
			log.info("END: *** MftUpdationForDPE SQL 8 (strMark) *****" + strMark + " paramMap: " + paramMap);
			int cntmark = namedParameterJdbcTemplate.update(strMark, paramMap);

			StringBuffer strUpdateTrans = new StringBuffer();
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				strUpdateTrans.append(
						"INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,VAR_NBR,BL_NBR,CRG_TYPE,DIS_TYPE, ");
				strUpdateTrans.append("CNTR_SIZE,CNTR_TYPE,STG_TYPE,CRG_STATUS,PKG_TYPE,NBR_PKGS,CRG_DES, ");
				strUpdateTrans.append(
						"GROSS_WT,GROSS_VOL,DG_IND,HS_CODE,LD_PORT,DIS_PORT,DES_PORT,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,CONS_NM, MISC_SEQ_NBR,CARGO_CATEGORY_CD "); 
				// added by VietNguyen 02 March 2010 for CR-CIM-20091203-34
				if ("JP".equals(coCd)) {
					strUpdateTrans.append(
							", ADVISE_BY,ADVISE_DATE,ADVISE_MODE,AMEND_CHARGED_TO,WAIVE_CHARGED,WAIVE_REASON )");
				} else {
					strUpdate_trans = strUpdate_trans + ") ";
				}
				strUpdateTrans.append(" VALUES(:stransno,:seqno,:varno,:blno,:crgtyp,:dop, ");
				strUpdateTrans.append(" :cntrsize,:cntrtype,:stgind,:crgstat,:pkgtyp, ");
				strUpdateTrans.append(" :nopkgs,:crgdesc,:gwt,:gvol,:dgind,:hscd, ");
				strUpdateTrans.append(" :poL,:poD,:poFD,:usrid,sysdate,:coname, ");
				strUpdateTrans.append(" :miscNo,:category "); 
				// added by VietNguyen 02 March 2010 for CR-CIM-20091203-34

				if ("JP".equals(coCd)) {
					strUpdateTrans.append(
							",:adviseBy,TO_DATE(:adviseDate,'DD/MM/YYYY HH24:MI:SS'),:adviseMode,:amendChargedTo,:waiveCharge,:waiveReason)");

				} else {
					strUpdateTrans.append(" )");
				}
				strUpdate_trans = strUpdateTrans.toString();

				strMark_trans = "INSERT INTO MFT_MARKINGS_TRANS(MFT_SQ_NBR,TRANS_NBR,MFT_MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) VALUES(:seqno,:stransno,:mark,:usrid,sysdate)";

			}
			int count_trans = 0;
			int cntmark_trans = 0;

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				paramMap.put("stransno", stransno);
				paramMap.put("seqno", seqno);
				paramMap.put("varno", varno);
				paramMap.put("blno", blno.trim());
				paramMap.put("crgtyp", crgtyp);
				paramMap.put("dop", CommonUtility.deNull(dop));
				paramMap.put("cntrsize", CommonUtility.deNull(cntrsize));
				paramMap.put("cntrtype", CommonUtility.deNull(cntrtype));
				paramMap.put("stgind", stgind);
				paramMap.put("crgstat", crgstat);
				paramMap.put("pkgtyp", pkgtyp);
				paramMap.put("nopkgs", nopkgs);
				paramMap.put("crgdesc", GbmsCommonUtility.addApostr(crgdesc));
				paramMap.put("gwt", gwt);
				paramMap.put("gvol", gvol);
				paramMap.put("dgind", CommonUtility.deNull(dgind));
				paramMap.put("hscd", hscd);
				paramMap.put("poL", poL);
				paramMap.put("poD", poD);
				paramMap.put("poFD", CommonUtility.deNull(poFD));
				paramMap.put("usrid", usrid);
				paramMap.put("coname", GbmsCommonUtility.addApostr(coname));
				paramMap.put("miscNo", miscNo);
				paramMap.put("category", category);
				paramMap.put("adviseBy", CommonUtility.deNull(adviseBy));
				paramMap.put("adviseDate", adviseDate);
				paramMap.put("adviseMode", CommonUtility.deNull(adviseMode));
				paramMap.put("amendChargedTo", CommonUtility.deNull(amendChargedTo));
				paramMap.put("waiveCharge", CommonUtility.deNull(waiveCharge));
				paramMap.put("waiveReason", CommonUtility.deNull(waiveReason));
				log.info("*** MftUpdationForDPE Sql 9 (strUpdate_trans): " + strUpdate_trans + " paramMap: " + paramMap);
				count_trans = namedParameterJdbcTemplate.update(strUpdate_trans, paramMap);

				log.info("END: *** MftUpdationForDPE SQL 10 (strMark_trans) *****" + strMark_trans);
				paramMap.put("seqno", seqno);
				paramMap.put("stransno", stransno);
				paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
				paramMap.put("usrid", usrid);
				log.info("ParamMap Value :" + paramMap);
				cntmark_trans = namedParameterJdbcTemplate.update(strMark_trans, paramMap);

			}
			strCntr1 = "UPDATE BL_CNTR_DETAILS SET CNTR_NBR=:cntr1 WHERE MFT_SEQ_NBR=:seqno AND CNTR_BL_SEQ=1";
			paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
			paramMap.put("seqno", seqno);
			log.info("END: *** MftUpdationForDPE UPDATE SQL 11 (strCntr1) *****" + strCntr1 + " paramMap: " + paramMap);
			int cntcntr1 = namedParameterJdbcTemplate.update(strCntr1, paramMap);

			if (cntcntr1 == 0 && cntr1 != "") {
				strCntr1 = "INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(1,:seqno,:cntr1)";
				paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
				paramMap.put("seqno", seqno);
				log.info("END: *** MftUpdationForDPE INSERT SQL 12 (strCntr1)*****" + strCntr1 + " paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr1, paramMap);

			}

			strCntr2 = "UPDATE BL_CNTR_DETAILS SET CNTR_NBR=:cntr2  WHERE MFT_SEQ_NBR=:seqno AND CNTR_BL_SEQ=2";
			log.info("END: *** strCntr2 UPDATE SQL 13 *****" + strCntr2);
			paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
			paramMap.put("seqno", seqno);
			log.info("ParamMap Value :" + paramMap);
			int cntcntr2 = namedParameterJdbcTemplate.update(strCntr2, paramMap);

			if (cntcntr2 == 0 && cntr2 != "") {
				strCntr2 = "INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(2,:seqno,:cntr2)";
				log.info("END: *** strCntr2 INSERT SQL 14 *****" + strCntr2);
				paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
				paramMap.put("seqno", seqno);
				log.info("ParamMap Value :" + paramMap);
				namedParameterJdbcTemplate.update(strCntr2, paramMap);

			}

			strCntr3 = "UPDATE BL_CNTR_DETAILS SET CNTR_NBR=:cntr3  WHERE MFT_SEQ_NBR=:seqno AND CNTR_BL_SEQ=3";
			log.info("END: *** strCntr3 UPDATE SQL 15 *****" + strCntr3);
			paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
			paramMap.put("seqno", seqno);
			log.info("ParamMap Value :" + paramMap);
			int cntcntr3 = namedParameterJdbcTemplate.update(strCntr3, paramMap);

			if (cntcntr3 == 0 && cntr3 != "") {

				strCntr3 = "INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(3,:seqno,:cntr3)";
				log.info("END: *** strCntr3 INSERT SQL 16 *****" + strCntr3);
				paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
				paramMap.put("seqno", seqno);
				log.info("ParamMap Value :" + paramMap);
				namedParameterJdbcTemplate.update(strCntr3, paramMap);

			}
			strCntr4 = "UPDATE BL_CNTR_DETAILS SET CNTR_NBR=:cntr4  WHERE MFT_SEQ_NBR=:seqno AND CNTR_BL_SEQ=4";
			log.info("END: *** strCntr4 UPDATE SQL 17 *****" + strCntr4);
			paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
			paramMap.put("seqno", seqno);
			log.info("ParamMap Value :" + paramMap);
			int cntcntr4 = namedParameterJdbcTemplate.update(strCntr4, paramMap);

			if (cntcntr4 == 0 && cntr4 != "") {
				strCntr4 = "INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(4,:seqno,:cntr4)";
				log.info("END: *** strCntr4 INSERT SQL 18 *****" + strCntr4);
				paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
				paramMap.put("seqno", seqno);
				log.info("ParamMap Value :" + paramMap);
				namedParameterJdbcTemplate.update(strCntr4, paramMap);
			}
			strCntr1_trans = "INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(:stransno,1,:seqno,:cntr1)";

			strCntr2_trans = "INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(:stransno,2,:seqno,:cntr2)";

			strCntr3_trans = "INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(:stransno,3,:seqno,:cntr3)";

			strCntr4_trans = "INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(:stransno,4,:seqno,:cntr4)";

			if (cntr1 != null && !cntr1.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info("END: *** strCntr1_trans SQL 19 *****" + strCntr1_trans);
					paramMap.put("stransno", stransno);
					paramMap.put("seqno", seqno);
					paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
					log.info("ParamMap Value :" + paramMap);
					namedParameterJdbcTemplate.update(strCntr1_trans, paramMap);

				}
			}
			if (cntr2 != null && !cntr2.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info("END: *** strCntr2_trans SQL 20 *****" + strCntr2_trans);
					paramMap.put("stransno", stransno);
					paramMap.put("seqno", seqno);
					paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
					log.info("ParamMap Value :" + paramMap);
					namedParameterJdbcTemplate.update(strCntr2_trans, paramMap);
				}
			}
			if (cntr3 != null && !cntr3.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info("END: *** strCntr3_trans SQL 21 *****" + strCntr3_trans);
					paramMap.put("stransno", stransno);
					paramMap.put("seqno", seqno);
					paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
					log.info("ParamMap Value :" + paramMap);
					namedParameterJdbcTemplate.update(strCntr3_trans, paramMap);
				}
			}
			if (cntr4 != null && !cntr4.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info("END: *** strCntr4_trans SQL 22 *****" + strCntr4_trans);
					paramMap.put("stransno", stransno);
					paramMap.put("seqno", seqno);
					paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
					log.info("ParamMap Value :" + paramMap);
					namedParameterJdbcTemplate.update(strCntr4_trans, paramMap);

				}
			}

			if (count == 0 || cntmark == 0) {
				log.info("Writing from ManifestEJB.MftUpdation");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				if (count_trans == 0 || cntmark_trans == 0) {
					log.info("Writing from ManifestEJB.MftUpdation");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201"); 
				}
			}
			log.info("======== END WHEN UPDATION: ");
			log.info("END: *** MftUpdationForDPE Result *****" + seqno.toString());
		} catch (NullPointerException e) {
			log.info("Exception MftUpdationForDPE : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception MftUpdationForDPE : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftUpdationForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** MftUpdationForDPE  END *****");
		}
		return seqno;
	}

	// EndRegion
	
	//Transfer of Manifest
	@Override
	public List<Object> transMftUpdate(String userID, String varnof, String varnot,
			List<ManifestValueObject> seqblno) throws BusinessException {
		String sql = "";
		String numpkgs = "";
		String seqno = "";
		String blno = "";
		int count = 0;
		String sql1 = "";
		List<Object> retvect = new ArrayList<Object>();
		String crgdes = "";
		String gwt = "";
		String gvol = "";
		String crgstat = "";
		SqlRowSet rs = null;
		String edostat = "";
		List<ManifestValueObject> vect1 = new ArrayList<ManifestValueObject>();
		List<ManifestValueObject> vect2 = new ArrayList<ManifestValueObject>();
		int balnumpkgs = 0;
		String sqltlog = "";
		String strInsert_trans = "";
		int stransno = 0;
		int count_trans = 0;
		StringBuffer sb = new StringBuffer();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		try {
			log.info("START: transMftUpdate  DAO  Start Obj "+" userID:"+userID+" varnof:"+varnof+" varnot:"+varnot
					+" seqblno:"+seqblno);

			for (int i = 0; i < seqblno.size(); i++) {
				ManifestValueObject mftvObj = new ManifestValueObject();
				mftvObj = (ManifestValueObject) seqblno.get(i);
				seqno = mftvObj.getSeqNo();
				blno = mftvObj.getBlNo();

				sb = new StringBuffer();
				sb.append("UPDATE MANIFEST_DETAILS SET VAR_NBR=:varnot");
				sb.append(",MAN_ORIGINAL_VAR_NBR=:varnof ");
				sb.append(",LAST_MODIFY_USER_ID=:userID");
				sb.append(",MAN_TRANSFER_DTTM=sysdate WHERE MFT_SEQ_NBR=:seqno ");
				sb.append("AND VAR_NBR=:varnof AND BL_NBR=:blno AND EDO_NBR_PKGS=0");
				sql = sb.toString();
				log.info(" *** transMftUpdate SQL 1 *****" + sql);

				paramMap.put("varnot", varnot);		
				paramMap.put("varnof", varnof);		
				paramMap.put("userID", userID);		
				paramMap.put("seqno", seqno);		
				paramMap.put("blno", blno);		
				log.info("ParamMap Value :" + paramMap);
				count = namedParameterJdbcTemplate.update(sql, paramMap);

				sqltlog = "SELECT MAX(TRANS_NBR) FROM MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:seqno ";

				if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
					log.info(" *** transMftUpdate SQL 2 *****" + sqltlog);
					paramMap.put("seqno", seqno);		
					log.info("ParamMap Value :" + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
					if (rs.next()) {
						stransno = (rs.getInt(1)) + 1;
					} else {
						stransno = 0;
					}
				}
				sb = new StringBuffer();	

				sb.append("INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,VAR_NBR,MAN_ORIGINAL_VAR_NBR,MAN_TRANSFER_DTTM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)");
				sb.append(" VALUES(:stransno,:seqno,:varnot,:varnof,sysdate,:userID,sysdate)");
				strInsert_trans = sb.toString();

				strInsert_trans = strInsert_trans.trim();
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info(" *** transMftUpdate SQL 3 *****" + strInsert_trans);
					
					paramMap.put("stransno", stransno);		
					paramMap.put("seqno", seqno);	
					paramMap.put("varnot", varnot);		
					paramMap.put("userID", userID);	
					paramMap.put("varnof", varnof);		
					log.info("ParamMap Value :" + paramMap);
					count_trans = namedParameterJdbcTemplate.update(strInsert_trans, paramMap);
				}
				sql1 = "SELECT BL_NBR,CRG_TYPE,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_STATUS,EDO_NBR_PKGS FROM MANIFEST_DETAILS WHERE BL_NBR=:blno AND MFT_SEQ_NBR=:seqno ";
				log.info(" *** transMftUpdate SQL 4 *****" + sql1);
				
				paramMap.put("blno", blno);	
				paramMap.put("seqno", seqno);
				log.info("ParamMap Value :" + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);

				if (rs.next()) {
					blno = CommonUtility.deNull(rs.getString("BL_NBR"));
					crgdes = CommonUtility.deNull(rs.getString("CRG_TYPE"));
					gwt = CommonUtility.deNull(rs.getString("GROSS_WT"));
					gvol = String.format( "%.2f", Double.parseDouble(CommonUtility.deNull(rs.getString("GROSS_VOL")).trim().equals("")?"0":CommonUtility.deNull(rs.getString("GROSS_VOL"))));
					numpkgs = CommonUtility.deNull(rs.getString("NBR_PKGS"));
					crgstat = CommonUtility.deNull(rs.getString("CRG_STATUS"));
					balnumpkgs = rs.getInt("EDO_NBR_PKGS");

					if (balnumpkgs == 0) {
						edostat = "N";
					} else {
						edostat = "Y";
					}

					if (count == 1) {
						ManifestValueObject mvObj = new ManifestValueObject();
						mvObj.setBlNo(blno);
						mvObj.setNoofPkgs(numpkgs);
						mvObj.setCrgType(crgdes);
						mvObj.setGrWt(gwt);
						mvObj.setGrMsmt(gvol);
						mvObj.setCrgStatus(crgstat);
						mvObj.setEdostat(edostat);
						vect1.add(mvObj);
					} else {
						ManifestValueObject mvObj1 = new ManifestValueObject();
						mvObj1.setBlNo(blno);
						mvObj1.setNoofPkgs(numpkgs);
						mvObj1.setCrgType(crgdes);
						mvObj1.setGrWt(gwt);
						mvObj1.setGrMsmt(gvol);
						mvObj1.setCrgStatus(crgstat);
						mvObj1.setEdostat(edostat);
						vect2.add(mvObj1);
					}
				} // if rs next
			} // for int i
			retvect.add(vect1);
			retvect.add(vect2);
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
				if (count_trans == 0) {
					log.info("Writing from ManifestEJB.transMftUpdate");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}

			log.info("END: *** transMftUpdate Result *****" + retvect.toString());
		} catch (BusinessException e) {
			log.info("Exception transMftUpdate : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception transMftUpdate : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception transMftUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** transMftUpdate  END *****");
		}
		return retvect;
	} 
	
	public int getManifestListCount(String vvcode, String coCode,Criteria criteria) throws BusinessException { // Added by Vietnd02
		SqlRowSet rs = null;
		boolean isShowManifestInfo = false;
		int count=0;
		String sql="";
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: DAO getManifestListCount VVcd" + vvcode + "coCode:" + coCode + "criteria" + criteria.toString());
			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_MANIFEST);
			TextParaVO result = getParaCodeInfo(code);
			isShowManifestInfo = isShowManifestInfo(coCode, result);
			StringBuilder sb = new StringBuilder();

			if (isShowManifestInfo) {
				sb.append(	"SELECT COUNT(*)  ");
				sb.append(" FROM MANIFEST_DETAILS MD ");
				sb.append("LEFT JOIN VESSEL_CALL VC ON MD.VAR_NBR = VC.VV_CD ");
				sb.append("LEFT JOIN VESSEL_SCHEME VS ON MD.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR ");
				sb.append("WHERE MD.BL_STATUS='A' AND MD.VAR_NBR=:vvcode  ");
			} else {
				sb.append(	" SELECT COUNT(*)  ");
				sb.append("  FROM MANIFEST_DETAILS MD ");
				sb.append(" LEFT JOIN TOPS.VESSEL_CALL VC ON MD.VAR_NBR = VC.VV_CD ");
				sb.append(" LEFT JOIN GBMS.VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A') ");
				sb.append(" LEFT JOIN TOPS.VESSEL_SCHEME VS ON MD.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR ");
				sb.append(" WHERE MD.BL_STATUS='A' AND MD.VAR_NBR=:vvcode  ");
				sb.append(
						" AND (VC.CREATE_CUST_CD=:coCode OR (VD.CUST_CD=:coCode  AND MD.MANIFEST_CREATE_CD =:coCode)) ");
			
			}
			sql=sb.toString();
			paramMap.put("vvcode", vvcode);
			if (!isShowManifestInfo) {
				paramMap.put("coCode", coCode);
			}
			log.info("getManifestListCount SQL" + sb + "paramMap:" + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				count =rs.getInt(1);
			}
			log.info("END: *** getManifestListCount Result *****" + count);

		} catch (NullPointerException e) {
			log.info("Exception getManifestListCount : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getManifestListCount : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getManifestListCount  END *****");
		}
		return count;
	}
	// End Transfer Of Manifest

	
	@Override
	public List<ManifestValueObject> getHSCodeList(String status) throws BusinessException {

		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<ManifestValueObject> hsCodeList = new ArrayList<ManifestValueObject>();
		sql = "SELECT HS_CODE FROM HS_CODE WHERE REC_STATUS ='" + status + "' ORDER BY HS_CODE ";

		try {
			log.info("START: getHSCodeList  DAO  Start Obj " + " status:" + status);

			log.info(" *** getHSCodeList SQL *****" + sql + " paramMap " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String hsCode = "";
			ManifestValueObject mvObj = null;
			while (rs.next()) {
				hsCode = CommonUtility.deNull(rs.getString("HS_CODE"));
				mvObj = new ManifestValueObject();
				mvObj.setHsCode(hsCode);
				hsCodeList.add(mvObj);
			}
			log.info("END: *** getHSCodeList Result *****" + hsCodeList.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getHSCodeList : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getHSCodeList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getHSCodeList  DAO  END");
		}

		return hsCodeList;
	}
	
	// ejb.sessionBeans.gbms.cargo.manifest -->ManifestEJB -->getHSSubCodeList()
	@Override
	public List<HSCode> getHSSubCodeList(String hsCode) throws BusinessException {
		SqlRowSet rs = null;
		List<HSCode> hsCodeLs = new ArrayList<HSCode>();
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * ");
		sql.append(" FROM HS_SUB_CODE ");
		sql.append(" WHERE REC_STATUS = '1' AND HS_CODE =:hsCode");
		sql.append(" ORDER BY HS_SUB_CODE_FR ");
		try {
			log.info("START DAO getHSSubCodeList: hsCode:" + hsCode);
			paramMap.put("hsCode", hsCode);
			log.info("getHSSubCodeList SQL*************** " + sql.toString() + " paramMap " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				HSCode hs = new HSCode();
				hs.setHsCode(CommonUtility.deNull(rs.getString("HS_CODE")));
				hs.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				hs.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				hs.setHsSubDesc(CommonUtility.deNull(rs.getString("HS_SUB_DESC")));
				hsCodeLs.add(hs);
			}
			log.info("END: *** getHSSubCodeList Result *****" + hsCodeLs.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getHSSubCodeList : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getHSSubCodeList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getHSSubCodeList  DAO  END. ");
		}
		return hsCodeLs;
	}
}
