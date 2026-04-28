package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



//Added on 22-10-2025]
import java.util.stream.Stream;
import java.util.function.*;

import java.util.Set;
import java.util.*;
import java.util.stream.*;
//by  me

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.InwardCargoManifestRepository;
import sg.com.jp.generalcargo.domain.AccessCompanyValueObject;
import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.CargoDimensionDeclaration;
import sg.com.jp.generalcargo.domain.CargoDimensionDetails;
import sg.com.jp.generalcargo.domain.CargoDimensionDetails.CargoDimension;
import sg.com.jp.generalcargo.domain.CargoManifest;
import sg.com.jp.generalcargo.domain.CargoManifestFileUploadDetails;
import sg.com.jp.generalcargo.domain.CargoSelection;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.HatchBreakDownPageDetail;
import sg.com.jp.generalcargo.domain.HatchDetails;
import sg.com.jp.generalcargo.domain.HatchWisePackageDetail;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestActionTrail;
import sg.com.jp.generalcargo.domain.ManifestActionTrailDetails;
import sg.com.jp.generalcargo.domain.ManifestCargoValueObject;
import sg.com.jp.generalcargo.domain.ManifestDetails;
import sg.com.jp.generalcargo.domain.ManifestPkgDimDetails;
import sg.com.jp.generalcargo.domain.ManifestUploadConfig;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.MiscDetail;
import sg.com.jp.generalcargo.domain.PackageDimension;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.SystemConfigList;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.Template;
import sg.com.jp.generalcargo.domain.TextParaVO;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("cargoManifestRepo")
public class InwardCargoManifestJdbcRepostory implements InwardCargoManifestRepository {

	private static final Log log = LogFactory.getLog(InwardCargoManifestJdbcRepostory.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// Region - MIGRATED-JPONLINE

	final static String TEXT_PARA_GC_VIEW_MANIFEST = "GC_V_MFST";

	public String logStatusGlobal = "Y";
	private final String dateFormat2 = "dd/MM/yyyy";
	private final String dateFormat = "dd/MM/yyyy HH:mm";

	private static final String SQL_INSERT_MISC_EVENT_LOG = "INSERT INTO " + ConstantUtil.MISC_EVENT_LOG
			+ " VALUES (:misNo, sysdate, :type, :haulCd, :varno, :billInd, :blno, :coCd, sysdate, :cntrSeqNbr, :pdisc1)";

	private static final String MDEL = "MDEL";// delete

	@Override
	public List<VesselVoyValueObject> getVesselVoy(String cocode) throws BusinessException {
		SqlRowSet rs = null;
		List<VesselVoyValueObject> voyList = new ArrayList<VesselVoyValueObject>();
		boolean isShowManifestInfo = false;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getVesselVoy DAO START cocode:" + cocode);
			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_MANIFEST);
			TextParaVO result = getParaCodeInfo(code);
			isShowManifestInfo = isShowManifestInfo(cocode, result);
			log.info("isShowManifestInfo :" + isShowManifestInfo);
			if (isShowManifestInfo) {
				sb.append(
						"SELECT IN_VOY_NBR,OUT_VOY_NBR,VSL_NM,VV_CD,TERMINAL FROM TOPS.VESSEL_CALL WHERE VV_STATUS_IND IN ('PR','AP','AL','BR','UB')");
				sb.append(" AND nvl(GB_CLOSE_BJ_IND,'N') <> 'Y' ");
				sb.append(" AND((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT') ");
				sb.append("ORDER BY TERMINAL DESC,VSL_NM,IN_VOY_NBR");
			} else {
				sb.append("SELECT DISTINCT IN_VOY_NBR, ");
				sb.append("			          VSL_NM,  ");
				sb.append("			         VC.VV_CD, TERMINAL, OUT_VOY_NBR  ");
				sb.append("			        FROM TOPS.VESSEL_CALL VC   ");
				sb.append("			        LEFT OUTER JOIN GBMS.VESSEL_DECLARANT VD   ");
				sb.append("			        ON (VD.VV_CD                     = VC.VV_CD   ");
				sb.append("			        AND VD.STATUS                    = 'A')   ");
				sb.append("			        WHERE VV_STATUS_IND             IN ('PR','AP','AL','BR')   ");
//				sb.append("			        AND TERMINAL                     ='GB' ");
				sb.append(
						"			        AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')  ");
				sb.append("			        AND NVL(VC.GB_CLOSE_BJ_IND,'N') <> 'Y' ");
				sb.append("			        AND (VD.CUST_CD                  =:coCode   ");
				sb.append("			        OR VC.CREATE_CUST_CD             =:coCode  )   ");
				sb.append("			        ORDER BY TERMINAL DESC, VSL_NM, ");
				sb.append("			         IN_VOY_NBR");
			}

			if (!isShowManifestInfo) {
				paramMap.put("coCode", cocode);
			}

			log.info("***** getVesselVoy SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			String voynbr = "";
			String inVoyNo = "";
			String outVoyNo = "";
			String vslName = "";
			String VV_CD = "";
			String terminal = "";
			while (rs.next()) {
				voynbr = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				vslName = CommonUtility.deNull(rs.getString("VSL_NM"));
				VV_CD = CommonUtility.deNull(rs.getString("VV_CD"));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				inVoyNo = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				outVoyNo = CommonUtility.deNull(rs.getString("OUT_VOY_NBR")); //Added

				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj.setVoyNo(voynbr);
				vvvObj.setVslName(vslName);
				vvvObj.setVarNbr(VV_CD);
				vvvObj.setTerminal(terminal);
				vvvObj.setOutVoyNo(outVoyNo);
				vvvObj.setInVoyNo(inVoyNo);
				voyList.add(vvvObj);
			}
			log.info("voyList: getVesselVoy" + voyList.toString());
		} catch (Exception e) {
			log.info("Exception getVesselVoy : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getVesselVoy Result ***** " + voyList.toString() );
		}
		return voyList;		
	}

	@Override
	public TextParaVO getParaCodeInfo(TextParaVO tpvo) throws BusinessException
	{
		
		SqlRowSet rs = null;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TOPS.TEXT_PARA ");
		sql.append("WHERE PARA_CD =:paraCd ");
		TextParaVO tpo = new TextParaVO();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START getParaCodeInfo DAO Start tpvo:" + tpvo);
			paramMap.put("paraCd", tpvo.getParaCode());
			log.info("***** getParaCodeInfo SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				tpo.setParaCode(rs.getString("PARA_CD"));
				tpo.setValue(rs.getString("VALUE"));
				tpo.setParaDesc(CommonUtility.deNull(rs.getString("PARA_DESC")));
				tpo.setUser(rs.getString("LAST_MODIFY_USER_ID"));
				tpo.setTimestamp(rs.getTimestamp("LAST_MODIFY_DTTM"));
			}

		} catch (Exception e) {
			log.info("Exception getParaCodeInfo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: ***getParaCodeInfo Result *****" + tpo.toString());
		}
		return tpo;
	}

	@Override
	public boolean isShowManifestInfo(String companyCode, TextParaVO result) throws BusinessException {
		try {
			log.info("START: DAO isShowManifestInfo companyCode:" + companyCode + "result:" + result.toString());
			if (result != null && result.getValue() != null && !"".equals(result.getValue())) {
				String[] textArr = result.getValue().split("/");
				String text = "";
				if (textArr != null && textArr.length > 0) {
					for (int i = 0; i < textArr.length; i++) {
						text = textArr[i];
						if (text != null && text.equals(companyCode)) {
							log.info("END:*** isShowManifestInfo Result ***** True");
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
		log.info("END:*** isShowManifestInfo Result ***** False");
		return false;
	}

	@Override
	public List<VesselVoyValueObject> getVsNmVoy(String varNbr) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sql = new StringBuilder();
		List<VesselVoyValueObject> voyList = new ArrayList<VesselVoyValueObject>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getVsNmVoy DAO START varNbr:" + varNbr);
			sql.append(
					"  SELECT A.VSL_NM, A.IN_VOY_NBR, A.OUT_VOY_NBR, A.VV_CD, TO_CHAR(B.ATB_DTTM,'DDMMYYYY HH24MI') ATB_DTTM, TO_CHAR(B.ATU_DTTM,'DDMMYYYY HH24MI') ATU_DTTM  ");
			sql.append(" FROM TOPS.VESSEL_CALL A, TOPS.BERTHING B  WHERE A.VV_CD =:varNbr  AND A.VV_CD = B.VV_CD  ");
			paramMap.put("varNbr", varNbr);
			log.info("***** getVsNmVoy SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			String voynbr = "";
			String vslName = "";
			String atb_dttm = "";
			String atu_dttm = "";

			while (rs.next()) {
				voynbr = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				vslName = CommonUtility.deNull(rs.getString("VSL_NM"));
				atb_dttm = CommonUtility.deNull(rs.getString("ATB_DTTM"));
				atu_dttm = CommonUtility.deNull(rs.getString("ATU_DTTM"));

				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj.setVoyNo(voynbr);
				vvvObj.setVslName(vslName);
				vvvObj.setAtb_dttm(atb_dttm);
				vvvObj.setAtu_dttm(atu_dttm);
				voyList.add(vvvObj);
			}

		} catch (Exception e) {
			log.info("Exception getVsNmVoy : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getVsNmVoy Result ***** voyList:" + voyList.toString());
		}
		return voyList;
	}

	@Override
	public List<VesselVoyValueObject> getVesselVoyList(String cocode, String vesselName, String voyageNumber, String terminal, String varNo) throws BusinessException {
		boolean isShowManifestInfo = false;
		List<VesselVoyValueObject> voyList = new ArrayList<VesselVoyValueObject>();
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getVesselVoyList Dao START cocode:" + cocode + "vesselName:" + vesselName + "voyageNumber:"
					+ voyageNumber + "terminal" + terminal + "varNo" + varNo);
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
				sb.append(" VSL_TYPE_NM, ");
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
				sb.append("		AND V.IN_VOY_NBR =:voyageNumber ");
				// Added by NearShore 28092022 
				if(!varNo.isEmpty()) {
					sb.append("AND V.VV_CD =:varNo ");
				}
				sb.append( ") indicationOfArrival, ");
				
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
				sb.append("		AND V.IN_VOY_NBR =:voyageNumber ");
				// Added by NearShore 28092022 
				if(!varNo.isEmpty()) {
					sb.append("AND V.VV_CD =:varNo ");
				}		
				sb.append(" ) indicationOfDeparture ");
				
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
				sb.append("		V.TERMINAL,vsc.VSL_TYPE_NM,V.CEMENT_VSL_IND ");
				sb.append("	FROM ");
				sb.append("		TOPS.VESSEL_CALL V ");
				sb.append("	LEFT JOIN TOPS.BERTHING B ON ");
				sb.append("		(V.VV_CD = B.VV_CD ");
				sb.append("		AND B.SHIFT_IND = 1) ");
				sb.append("	LEFT JOIN TOPS.VESSEL VS ON ");
				sb.append("		(V.VSL_NM = VS.VSL_NM) ");
				sb.append(" LEFT JOIN VESSEL_TYPE_CODE vsc ON VS.VSL_TYPE_CD= vsc.VSL_TYPE_CD ");
				sb.append("	WHERE ");
				sb.append("		(V.VV_STATUS_IND <> 'CX' ");
				sb.append("		AND ((TERMINAL IN 'CT' ");
				sb.append("		AND COMBI_GC_OPS_IND IN('Y', ");
				sb.append("		NULL)) ");
				sb.append("		OR TERMINAL NOT IN 'CT')) ");
				sb.append("		AND (V.VSL_NM =:vesselName ");
				sb.append("		OR VS.VSL_FULL_NM =:vesselName) ");
				sb.append("		AND V.IN_VOY_NBR =:voyageNumber ");
				// Added by NearShore 28092022 
				if(!varNo.isEmpty()) {
					sb.append("AND V.VV_CD =:varNo ");
				}
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

				sb.append("SELECT");
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
				sb.append("	ATU_DTTM , ");
				sb.append(" VSL_TYPE_NM, ");
				sb.append(" CEMENT_VSL_IND, ");
				sb.append("	( ");
				sb.append("	SELECT DISTINCT ");
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
				sb.append("		AND VC.IN_VOY_NBR =:voyageNumber ");
				// Added by NearShore 28092022 
				if(!varNo.isEmpty()) {
					sb.append("AND VC.VV_CD =:varNo ");
				}
				sb.append( ") indicationOfArrival, ");
				sb.append("	( ");
				sb.append("	SELECT DISTINCT ");
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
				sb.append("		AND VC.IN_VOY_NBR =:voyageNumber " );
				// Added by NearShore 28092022 
				if(!varNo.isEmpty()) {
					sb.append("AND VC.VV_CD =:varNo ");
				}
				sb.append( ") indicationOfDeparture ");
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
				sb.append("		TO_CHAR(B.ETU_DTTM, 'dd/mm/yyyy HH24MI') AS ETU_DTTM, ");
				sb.append("		TO_CHAR(B.ATU_DTTM, 'dd/mm/yyyy HH24MI') AS ATU_DTTM, ");
				sb.append("		VC.TERMINAL,vsc.VSL_TYPE_NM,VC.CEMENT_VSL_IND ");
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
				sb.append(" LEFT JOIN VESSEL_TYPE_CODE vsc ON V.VSL_TYPE_CD= vsc.VSL_TYPE_CD ");
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
				// Added by NearShore 28092022 
				if(!varNo.isEmpty()) {
					sb.append("AND VC.VV_CD =:varNo ");
				}
				sb.append("	ORDER BY ");
				sb.append("		VSL_NM, ");
				sb.append("		IN_VOY_NBR )");

			}

			String voynbr = "";
			String vslName = "";
			String VV_CD = "";
			String cod_dttm = "";
			String etb_dttm = "";
			String vTerminal = "";
			String vsltype = "";
			String arrival = "";
			String departure = "";
			
			if (!isShowManifestInfo) {
				paramMap.put("coCode", cocode);
			}
			if(!varNo.isEmpty()) {
				paramMap.put("varNo", varNo);
			}
			paramMap.put("vesselName", vesselName);
			paramMap.put("voyageNumber", voyageNumber);
			log.info("***** getVesselVoyList SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				voynbr = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				vslName = CommonUtility.deNull(rs.getString("VSL_NM"));
				VV_CD = CommonUtility.deNull(rs.getString("VV_CD"));
				cod_dttm = CommonUtility.deNull(rs.getString("COD_DTTM"));
				if (StringUtils.isBlank(cod_dttm)) {
					cod_dttm = CommonUtility.deNull(rs.getString("GB_COD_DTTM"));
				}
				etb_dttm = CommonUtility.deNull(rs.getString("ETB_DTTM"));
				vTerminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				vTerminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				// String vesselltype = CommonUtility.deNull(rs.getString("CEMENT_VSL_IND"));
				vsltype = CommonUtility.deNull(rs.getString("VSL_TYPE_NM"));
				arrival = CommonUtility.deNull(rs.getString("ARRIVAL"));
				departure = CommonUtility.deNull(rs.getString("DEPARTURE"));
				/*
				 * if (vesselltype != null && vesselltype.equalsIgnoreCase("N")) { vsltype = GC;
				 * } else { vsltype = BC; }
				 */
				// setting atb New code
				paramMap.put("vvCd",VV_CD);
				sb= new StringBuilder();
				sb.append(" SELECT VC.VV_CD varNbr,TO_CHAR(a.arrival,'dd/mm/yyyy HH24MI') arrival,a.indicationOfArrival,TO_CHAR(d.departural,'dd/mm/yyyy HH24MI') departural,d.indicationOfDeparture ");
				sb.append(" FROM  TOPS.VESSeL_CALL VC ");
				sb.append(" JOIN (SELECT VV_CD, CASE WHEN ATB_DTTM is null then ETB_DTTM ELSE ATB_DTTM END arrival , CASE WHEN ATB_DTTM is null then 'ETB' ELSE 'ATB' END indicationOfArrival ");
				sb.append(" FROM BERTHING WHERE SHIFT_IND=1 ) a ON a.VV_CD=VC.VV_CD ");
				sb.append(" JOIN (SELECT B.VV_CD, CASE WHEN ATU_DTTM is null then ETU_DTTM ELSE ATU_DTTM END departural, CASE WHEN ATU_DTTM is null then 'ETU' ELSE 'ATU' END indicationOfDeparture ");
				sb.append(" FROM BERTHING B WHERE SHIFT_IND= (select MAX(Shift_ind) from BERTHING c where c.vv_cd=B.VV_CD) ) d ");
				sb.append(" ON d.VV_CD=VC.VV_CD ");
				sb.append(" WHERE VC.VV_CD= :vvCd ");
				
				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				
				log.info("***** getVesselVoyList SQL *****" + sb.toString());
				log.info("params: " + paramMap.toString());
				
				
				try {
						vvvObj = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
						new BeanPropertyRowMapper<VesselVoyValueObject>(VesselVoyValueObject.class));
						
						vvvObj.setVoyNo(voynbr);
						vvvObj.setVslName(vslName);
						vvvObj.setVarNbr(VV_CD);
						vvvObj.setCod_dttm(cod_dttm);
						vvvObj.setEtb_dttm(etb_dttm);
						vvvObj.setTerminal(vTerminal);
						vvvObj.setTerminal(vTerminal);
						vvvObj.setBerthNo(getLastBerthNo(VV_CD));
						vvvObj.setVesselType(vsltype);
						// ITSM #32060 - START Take arrival & departure date from same query as indicationOfArrival - NS NOV 2023
						// vvvObj.setArrival(arrival);
						// vvvObj.setDepartural(departure);
						// ITSM #32060 - END Take arrival & departure date from same query as indicationOfArrival - NS NOV 2023
						log.info (" vvvObj :"+ vvvObj.toString());
						voyList.add(vvvObj);
						
				}catch (EmptyResultDataAccessException e) {
					return voyList;
				}
				
				
			}

		} catch (Exception e) {
			log.info("Exception getVesselVoyList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:*** getVesselVoyList Result *****:voyList" + voyList);
		}
		return voyList;
	}

	private String getLastBerthNo(String vvCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		String berthNo = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getLastBerthNo vvCd:" + vvCd);
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
			log.info("***** getLastBerthNo SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			try {
				berthNo = (String) namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			} catch (EmptyResultDataAccessException e) {
				log.info(e);
			}

		} catch (Exception e) {
			log.info("Exception getLastBerthNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:*** getLastBerthNo Result: ***** berthNo:" + berthNo);
		}
		return berthNo;

	}

	@Override
	public boolean chkVslStat(String varno) throws BusinessException {
		String sql = "";
		boolean bvslind = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "SELECT GB_CLOSE_BJ_IND FROM TOPS.VESSEL_CALL WHERE GB_CLOSE_BJ_IND='Y' AND VV_CD=:varno ";
		SqlRowSet rs = null;
		try {
			log.info("START: DAO chkVslStat:" + varno);
			paramMap.put("varno", varno);
			log.info("***** chkVslStat SQL *****" + sql.toString());
			log.info("chkVslStat params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				bvslind = true;
			} else {
				bvslind = false;
			}
			log.info("END:*** chkVslStat Result: ***** bvslind:" + bvslind);
		} catch (Exception e) {
			log.info("Exception chkVslStat : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:*** chkVslStat DAO END*****");
		}
		return bvslind;
	}

	// start::Added by vietnd02 to check manifest status
	// true = close, false = not close
	@Override
	public boolean isManClose(String vesselCd) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String sql = "";
		boolean chk = false;
		sql = "SELECT VV_CD FROM TOPS.VESSEL_CALL WHERE VV_CD =:vesselCd  AND VV_STATUS_IND in ('UB', 'CL') ";
		try {
			log.info("START: isManClose Dao vesselCd:" + vesselCd);
			paramMap.put("vesselCd", vesselCd);
			log.info("***** isManClose SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				chk = true;
			}
		} catch (Exception e) {
			log.info("Exception isManClose : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:*** isManClose Result: ***** chk:" + chk);
		}
		return chk;
	}

	@Override
	public List<ManifestValueObject> getManifestList(String vvcode, String coCode, Criteria criteria) throws BusinessException { // Added by Vietnd02
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String edostat = "";
		String blno = "";
		String crgdes = "";
		String nbrpkgs = "";
		String gwt = "";
		String gvol = "";
		String seqno = "";
		String crgstat = "";
		int numpkgs = 0;
		int balnumpkgs = 0;
		String crgtype = "";
		String strUnStfInd = "";
		String dgInd = "N";
		String opInd = "N";
		String stgInd = "O";

		// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
		String hsCode = "";
		String hsSubCodeFr = "";
		String hsSubCodeTo = "";
		// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

		// MCC for EPC_IND
		String deliveryToEPC = "";

		String subScheme = "";
		String gcOperations = "";
		String terminal = "";

		List<ManifestValueObject> manifestList = new ArrayList<ManifestValueObject>();

		boolean isShowManifestInfo = false;
		try {
			log.info("START: DAO getManifestList:vvcode" + vvcode + "coCode:" + coCode);
			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_MANIFEST);
			TextParaVO result = getParaCodeInfo(code);
			isShowManifestInfo = isShowManifestInfo(coCode, result);
			StringBuilder sb = new StringBuilder();
			String sql = "";

			if (isShowManifestInfo) {
				sb.append(
						"SELECT MD.MFT_SEQ_NBR,MD.BL_NBR,MD.CRG_DES,MD.CRG_TYPE,MD.NBR_PKGS,MD.GROSS_WT,MD.GROSS_VOL,MD.EDO_NBR_PKGS, ");
				sb.append(
						"MD.CRG_STATUS,DECODE(MD.UNSTUFF_SEQ_NBR,0,'N','Y') UNSTF_IND,MD.CARGO_CATEGORY_CD, MD.DG_IND, ");
				sb.append(
						"MD.DIS_TYPE, MD.STG_TYPE,  MD.HS_CODE, MD.HS_SUB_CODE_FR, MD.HS_SUB_CODE_TO, nvl(VS.SCHEME_CD, VC.SCHEME) SCHEME, EPC_IND, nvl(VS.SCHEME_CD, VC.COMBI_GC_SCHEME) COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND, VC.TERMINAL ");
				sb.append("FROM MANIFEST_DETAILS MD ");
				sb.append("LEFT JOIN VESSEL_CALL VC ON MD.VAR_NBR = VC.VV_CD ");
				sb.append("LEFT JOIN VESSEL_SCHEME VS ON MD.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR ");
				sb.append("WHERE MD.BL_STATUS='A' AND MD.VAR_NBR=:vvCode  ORDER BY MD.BL_NBR");

			} else {
				sb.append(
						" SELECT DiSTINCT MD.MFT_SEQ_NBR, MD.BL_NBR, MD.CRG_DES,CRG_TYPE, MD.NBR_PKGS, MD.GROSS_WT, MD.GROSS_VOL,MD.EDO_NBR_PKGS, ");
				sb.append(
						" MD.CRG_STATUS,DECODE(MD.UNSTUFF_SEQ_NBR,0,'N','Y') UNSTF_IND,MD.CARGO_CATEGORY_CD, MD.DG_IND, MD.DIS_TYPE, MD.STG_TYPE, MD.HS_CODE, MD.HS_SUB_CODE_FR, MD.HS_SUB_CODE_TO, ");
				sb.append(
						" nvl(VS.SCHEME_CD, VC.SCHEME) SCHEME, EPC_IND, nvl(VS.SCHEME_CD, VC.COMBI_GC_SCHEME) COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND, VC.TERMINAL  ");
				sb.append("  FROM MANIFEST_DETAILS MD ");
				sb.append(" LEFT JOIN TOPS.VESSEL_CALL VC ON MD.VAR_NBR = VC.VV_CD ");
				sb.append(" LEFT JOIN GBMS.VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A') ");
				sb.append(" LEFT JOIN TOPS.VESSEL_SCHEME VS ON MD.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR ");
				sb.append(" WHERE MD.BL_STATUS='A' AND MD.VAR_NBR=:vvCode  ");
				sb.append(
						" AND (VC.CREATE_CUST_CD=:coCode OR (VD.CUST_CD=:coCode  AND MD.MANIFEST_CREATE_CD =:coCode)) ");
				sb.append(" ORDER BY BL_NBR ");
			}
			if (criteria != null && criteria.isPaginated()) {
				sql=CommonUtil.getPaginatedSql(sb.toString(), criteria.getStart(), criteria.getLimit());
			}else {
				sql=sb.toString();
			}
			paramMap.put("vvCode", vvcode);
			if (!isShowManifestInfo) {
				paramMap.put("coCode", coCode);
			}

			log.info("***** getManifestList SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				seqno = CommonUtility.deNull(rs.getString("MFT_SEQ_NBR"));
				blno = CommonUtility.deNull(rs.getString("BL_NBR"));
				crgdes = CommonUtility.deNull(rs.getString("CRG_DES"));
				nbrpkgs = CommonUtility.deNull(rs.getString("NBR_PKGS"));
				gwt = CommonUtility.deNull(rs.getString("GROSS_WT"));
				// HaiTTH1 modified to format Volume on 12/2/2014
				gvol = String.format("%.2f",
						Double.parseDouble(CommonUtility.deNull(rs.getString("GROSS_VOL")).trim().equals("") ? "0"
								: CommonUtility.deNull(rs.getString("GROSS_VOL"))));

				numpkgs = rs.getInt("NBR_PKGS");
				balnumpkgs = rs.getInt("EDO_NBR_PKGS");
				crgstat = rs.getString("CRG_STATUS");
				crgtype = rs.getString("CRG_TYPE");
				strUnStfInd = rs.getString("UNSTF_IND"); // added by vani -- 30th Oct,03

				// CR-CIM- 0000108
				dgInd = CommonUtility.deNull(rs.getString("DG_IND"));
				opInd = CommonUtility.deNull(rs.getString("DIS_TYPE"));
				stgInd = CommonUtility.deNull(rs.getString("STG_TYPE"));
				// CR-CIM- 0000108
				if (balnumpkgs == 0) {
					edostat = "N";
				} else if (balnumpkgs < numpkgs) {
					edostat = "P";
				} else if (balnumpkgs == numpkgs) {
					edostat = "Y";
				}

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				hsCode = CommonUtility.deNull(rs.getString("HS_CODE"));
				hsSubCodeFr = CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR"));
				hsSubCodeTo = CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO"));
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

				// HaiTTH1 added on 10/1/2014
				String scheme = CommonUtility.deNull(rs.getString("SCHEME"));

				deliveryToEPC = CommonUtility.deNull(rs.getString("EPC_IND"));

				subScheme = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));

				ManifestValueObject mvObj = new ManifestValueObject();
				mvObj.setBlNo(blno);
				mvObj.setCrgDesc(crgdes);
				mvObj.setNoofPkgs(nbrpkgs);
				mvObj.setGrWt(gwt);
				mvObj.setGrMsmt(gvol);
				mvObj.setEdostat(edostat);
				mvObj.setSeqNo(seqno);
				mvObj.setCrgStatus(crgstat);
				mvObj.setCrgType(crgtype);
				mvObj.setUnStfInd(strUnStfInd);
				mvObj.setCategory(rs.getString("CARGO_CATEGORY_CD"));
				// CR-CIM- 0000108
				mvObj.setDgInd(dgInd);
				mvObj.setOpInd(opInd);
				mvObj.setStgInd(stgInd);
				// CR-CIM- 0000108

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				mvObj.setHsCode(hsCode);
				mvObj.setHsSubCodeFr(hsSubCodeFr);
				mvObj.setHsSubCodeTo(hsSubCodeTo);
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

				// HaiTTH1 added on 10/1/2014
				mvObj.setScheme(scheme);

				// MCC for EPC_IND
				mvObj.setDeliveryToEPC(deliveryToEPC); // MCC
				mvObj.setSubScheme(subScheme);
				mvObj.setGcOperations(gcOperations);
				mvObj.setTerminal(terminal);
				manifestList.add(mvObj);
			}

		} catch (Exception e) {
			log.info("Exception getManifestList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:*** getManifestList Result ***** manifestList:" + manifestList.toString());
		}
		return manifestList;
	}

	@Override
	public VesselVoyValueObject getVesselInfo(String vv_cd) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		VesselVoyValueObject vessel = new VesselVoyValueObject();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: DAO getVesselInfo:vv_cd" + vv_cd);
			sql = " select * from TOPS.VESSEL_CALL where vv_cd =:vv_cd ";
			paramMap.put("vv_cd", vv_cd);
			log.info("***** getVesselInfo SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				String vsl_nm = CommonUtility.deNull(rs.getString("VSL_NM"));
				String in_voy_nbr = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				vessel.setVslName(vsl_nm);
				vessel.setVoyNo(in_voy_nbr);
			}
		} catch (Exception e) {
			log.info("Exception getVesselInfo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:*** getVesselInfo Result ***** vessel:" + vessel);
		}
		return vessel;
	}

	@Override
	public String getCategoryValue(String ccCd) throws BusinessException {
		String ccName = "";
		String sql = "SELECT CC_NAME FROM GBMS.CARGO_CATEGORY_CODE WHERE CC_CD =:ccCd ";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getCategoryValue DAO ccCd" + ccCd);
			paramMap.put("ccCd", ccCd);
			log.info("***** getCategoryValue SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				ccName = rs.getString(1);
			}
		} catch (Exception e) {
			log.info("Exception getCategoryValue : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:*** getCategoryValue Result: ***** ccName" + ccName);
		}
		return ccName;
	}

	@Override
	public boolean checkAddManifest(String varno, String coCd) throws BusinessException {
		boolean checkResult = true;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: checkAddManifest varno:" + varno + "coCd:" + coCd);
			sb.append(" SELECT count(*) as COUNT FROM  TOPS.VESSEL_CALL VC ");
			sb.append(" WHERE VC.CREATE_CUST_CD   =:coCd AND VC.VV_CD =:varno ");
			sb.append(" AND (VC.VV_STATUS_IND = 'UB' OR nvl(VC.GB_CLOSE_BJ_IND,'N') = 'Y') ");
			String sql = sb.toString();
			
			paramMap.put("varno", varno);
			paramMap.put("coCd", coCd);
			log.info("***** checkAddManifest SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				int count = Integer.parseInt(CommonUtility.deNull(rs.getString("COUNT")));
				if (count > 0) {
					checkResult = false;
				}
			}
		} catch (Exception e) {
			log.info("Exception checkAddManifest : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** checkAddManifest Result: ****** checkResult:" + checkResult);
		}
		return checkResult;
	}

	@Override
	public List<Map<String, String>> getCategoryList() throws BusinessException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getCategoryList Dao ");
			sb.append(" SELECT CC_NAME, CC_CD FROM CARGO_CATEGORY_CODE ");
			sb.append(" WHERE INSTR((SELECT VALUE FROM TEXT_PARA WHERE PARA_CD='VEH_CARGO'),CC_CD)>0 ");
			sb.append(" AND CC_STATUS='A' ORDER BY CC_NAME ");
			String sql = sb.toString();

			log.info("***** getCategoryList SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				Map<String,String> map = new HashMap<String,String>();
				map.put("ccCd", rs.getString("CC_CD"));
				map.put("ccName", rs.getString("CC_NAME"));
				list.add(map);
			}
		} catch (Exception e) {
			log.info("Exception getCategoryList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getCategoryList Result ***** list " + list.toString());
		}
		return list;
	}

	@Override
	public Map<String, String> getCargoCategoryCode_CargoCategoryName() throws BusinessException {
		Map<String, String> cc_cn = new HashMap<String, String>();
		String sql = "SELECT cc_cd, cc_name FROM cargo_category_code";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getCargoCategoryCode_CargoCategoryName DAO");
			log.info("***** getCargoCategoryCode_CargoCategoryName SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String cargoCategoryCode = "";
			String cargoCategoryName = "";
			while (rs.next()) {
				cargoCategoryCode = rs.getString("cc_cd");
				cargoCategoryName = rs.getString("cc_name");
				cc_cn.put(cargoCategoryCode, cargoCategoryName);
			}
		} catch (Exception e) {
			log.info("Exception getCargoCategoryCode_CargoCategoryName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getCargoCategoryCode_CargoCategoryName Result ***** cc_cn: " + cc_cn);
		}
		return cc_cn;
	}

	@Override
	public List<BookingReferenceValueObject> getBRVOList(String module) throws BusinessException {
		List<BookingReferenceValueObject> brvoList = new ArrayList<BookingReferenceValueObject>();
		try {
			log.info("START: getBRVOList DAO module:" + module);
			String cargoType_cargoCategoryString = getParaCargoTypeCode_CargoCategoryCode(module);
			String[] cargoTypeCargoCategory = cargoType_cargoCategoryString.split(",");
			for (int i = 0; i < cargoTypeCargoCategory.length; i++) {
				String[] oneCtCc = cargoTypeCargoCategory[i].split("-");
				BookingReferenceValueObject bookingReferenceVO = new BookingReferenceValueObject();
				bookingReferenceVO.setCargoType(oneCtCc[0]);
				bookingReferenceVO.setCargoCategory(formatApplicableCargoCategoryList(oneCtCc[1]));
				brvoList.add(bookingReferenceVO);
			}
		} catch (Exception e) {
			log.info("Exception getBRVOList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBRVOList DAO brvoList;" + brvoList.toString());
		}
		return brvoList;
	}

	private String getParaCargoTypeCode_CargoCategoryCode(String module) throws BusinessException {
		String ct_cc = "";
		String sql = "select * from text_para where PARA_CD = 'CTACC'";
		if (module.equals("AssignCargoCategory")) {
			sql = "select * from text_para where PARA_CD = 'CTACC_ACC'";
		}
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getParaCargoTypeCode_CargoCategoryCode DAO module:" + module);
			log.info("***** getParaCargoTypeCode_CargoCategoryCode SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				ct_cc = rs.getString("VALUE");
			}
		} catch (Exception e) {
			log.info("Exception getParaCargoTypeCode_CargoCategoryCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getParaCargoTypeCode_CargoCategoryCode Result ***** ct_cc:" + ct_cc);
		}
		return ct_cc;

	}

	private String formatApplicableCargoCategoryList(String cargoCategoryCodes) throws BusinessException {
		Map<String, String> cargoCode_cargoName = null;
		StringBuilder cargoCategoryName = new StringBuilder();
		try {
			log.info("START: formatApplicableCargoCategoryList DAO cargoCategoryCodes:" + cargoCategoryCodes);
			cargoCode_cargoName = getCargoCategoryCode_CargoCategoryName();
			String[] applicableCargoCategoryCode = cargoCategoryCodes.split("/");

			for (int i = 0; i < applicableCargoCategoryCode.length; i++) {
				cargoCategoryName.append(cargoCode_cargoName.get(applicableCargoCategoryCode[i])).append("=")
						.append(applicableCargoCategoryCode[i]).append(",");
			}
			cargoCategoryName.deleteCharAt(cargoCategoryName.length() - 1);
		} catch (Exception e) {
			log.info("Exception formatApplicableCargoCategoryList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** formatApplicableCargoCategoryList Result ***** cargoCategoryName:"
					+ cargoCategoryName.toString());
		}
		return cargoCategoryName.toString();
	}

	@Override
	public boolean isShowAllCargoCategoryCode(String companyCode) throws BusinessException {
		try {
			log.info("START: isShowAllCargoCategoryCode DAO companyCode:" + companyCode);
			String companyCodeAllCargoCategory = getCompanyCodeAllCargoCategory();
			String[] applicableCompanyCodes = companyCodeAllCargoCategory.split(",");
			for (String cc : applicableCompanyCodes) {
				if (CommonUtility.trimString(cc).equalsIgnoreCase(CommonUtility.trimString(companyCode))) {
					log.info("END: *** isShowAllCargoCategoryCode Result ****** result: true");
					return true;
				}
			}
		} catch (Exception e) {
			log.info("Exception isShowAllCargoCategoryCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO isShowAllCargoCategoryCode");
		}
		log.info("END: *** isShowAllCargoCategoryCode Result ****** result: false");
		return false;
	}

	@Override
	public String getCompanyCodeAllCargoCategory() throws BusinessException {
		String companyCode = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String sql = "select VALUE from text_para where PARA_CD = 'CMP_CD_CC'";
		try {
			log.info("START getCompanyCodeAllCargoCategory DAO:");
			log.info("***** getCompanyCodeAllCargoCategory SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				companyCode = rs.getString("VALUE");
			}
		} catch (Exception e) {
			log.info("Exception getCompanyCodeAllCargoCategory : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END ***getCompanyCodeAllCargoCategory DAO companyCode: ****" + companyCode);
		}
		return companyCode;
	}

	@Override
	public String getNotShowCargoCategoryCode() throws BusinessException {
		String cargoCategoryCode = "";
		String sql = "select VALUE from text_para where PARA_CD = 'CC_NOTSHOW'";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getNotShowCargoCategoryCode DAO");
			log.info("***** getNotShowCargoCategoryCode SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				cargoCategoryCode = rs.getString("VALUE");
			}
		} catch (Exception e) {
			log.info("Exception getNotShowCargoCategoryCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: ***getNotShowCargoCategoryCode DAO *****cargoCategoryCode:" + cargoCategoryCode);
		}

		return cargoCategoryCode;
	}

	@Override
	public String getCarCarrierVesselCode() throws BusinessException {
		String getCarCarrierVesselCode = "";
		String sql = "select VALUE from text_para where para_cd = 'VSL_CC'";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START getCarCarrierVesselCode DAO");
			log.info("***** getCarCarrierVesselCode SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				getCarCarrierVesselCode = rs.getString("VALUE");
			}
		} catch (Exception e) {
			log.info("Exception getCarCarrierVesselCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getCarCarrierVesselCode Result ***** getCarCarrierVesselCode: " + getCarCarrierVesselCode.toString());
		}
		return getCarCarrierVesselCode;
	}

	@Override
	public String getDefaultCargoCategoryCode() throws BusinessException {
		String defaultCargoCategoryCode = "";
		String sql = "select VALUE from text_para where para_cd = 'DEF_CC'";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getDefaultCargoCategoryCode DAO");
			log.info("***** getDefaultCargoCategoryCode SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				defaultCargoCategoryCode = rs.getString("VALUE");
			}
		} catch (Exception e) {
			log.info("Exception getDefaultCargoCategoryCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getDefaultCargoCategoryCode DAO ***** defaultCargoCategoryCode: " + defaultCargoCategoryCode.toString());
		}
		return defaultCargoCategoryCode;

	}

	@Override
	public String getCargoTypeNotShow() throws BusinessException {
		String cargoType = "";
		String sql = "select VALUE from text_para where para_cd = 'CT_NOTSHOW'";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getDefaultCargoCategoryCode DAO");
			log.info("***** getDefaultCargoCategoryCode SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				cargoType = rs.getString("VALUE");
			}
		} catch (Exception e) {
			log.info("Exception getDefaultCargoCategoryCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:*** getDefaultCargoCategoryCode DAO ***** cargoType:" + cargoType.toString());
		}
		return cargoType;

	}

	@Override
	public CompanyValueObject getCompanyInfo(String companyCode) throws BusinessException {
		String queryString = new String();
		CompanyValueObject companyValueObj = new CompanyValueObject();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getCompanyInfo DAO companyCode: " + companyCode);
			queryString = "SELECT * FROM company_code WHERE co_cd =:co_cd ";
			paramMap.put("co_cd", companyCode);
			log.info("***** getCompanyInfo SQL *****" + queryString.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(queryString, paramMap);
			if (rs.next()) {
				// For the Company Code selected from the database,
				// set all its attributes into the Company Code's Value Object
				companyValueObj.setCompanyCode(CommonUtility.deNull(rs.getString("co_cd")));
				companyValueObj.setPsaCompanyCode(CommonUtility.deNull(rs.getString("psa_co_cd")));
				companyValueObj.setCompanyName(CommonUtility.deNull(rs.getString("co_nm")));
				companyValueObj.setLOB(CommonUtility.deNull(rs.getString("lob_cd")));
				companyValueObj.setCustRelationInd(CommonUtility.deNull(rs.getString("cust_relation_ind")));
				companyValueObj.setStatus(CommonUtility.deNull(rs.getString("rec_status")));
				companyValueObj.setLastModifiedBy(CommonUtility.deNull(rs.getString("last_modify_user_id")));
				companyValueObj.setLastModifiedDate(
						CommonUtility.deNull(CommonUtil.parseDBDateToStr(rs.getTimestamp("last_modify_dttm"))));

				/// tuanta10 add at 07/08/2007
				/// to update 5 new fields added to company_code table

				companyValueObj.setAllowJPOnline(CommonUtility.deNull(rs.getString("allow_jponline")));
				companyValueObj.setRegFeeChargeStatus(CommonUtility.deNull(rs.getString("reg_fee_charge_sts")));
				companyValueObj.setSubFeeChargeStatus(CommonUtility.deNull(rs.getString("sub_fee_charge_sts")));
				companyValueObj.setAcToBill(CommonUtility.deNull(rs.getString("ac_to_bill")));
				companyValueObj.setContractNumber(CommonUtility.deNull(rs.getString("contract_nbr")));
				///

				// Added by Jade for CR-FMAS-20120202-001
				companyValueObj.setBgSdAmt(rs.getDouble("BG_SD_AMT") + "");
				companyValueObj.setCreditControlInd(rs.getString("CREDIT_CONTROL_IND"));
				companyValueObj.setCreditControlStatus(rs.getString("CREDIT_CONTROL_ST"));
				companyValueObj.setUnpaidPercent(rs.getDouble("UNPAID_PCT") + "");
				companyValueObj.setUnpaid60Amt(rs.getDouble("UNPAID_60D_AMT") + "");
				companyValueObj.setUnpaid90Amt(rs.getDouble("UNPAID_90D_AMT") + "");
				// End of adding by Jade for CR-FMAS-20120202-001
			}
		} catch (Exception e) {
			log.info("Exception getCompanyInfo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getCompanyInfo DAO ***** companyValueObj:" + companyValueObj.toString());
		}
		return companyValueObj;

	}

	@Override
	public String MftInsertionForEnhancementHSCode(String distype, String addval, String coCd, String varno,
			String blno, String crgtyp, String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc,
			String mark, String nopkgs, String gwt, String gvol, String crgstat, String dgind, String stgind,
			String dop, String pkgtyp, String coname, String consigneeCoyCode, String poL, String poD, String poFD,
			String cntrtype, String cntrsize, String cntr1, String cntr2, String cntr3, String cntr4, String autParty,
			String adviseBy, String adviseDate, String adviseMode, String amendChargedTo, String waiveCharge,
			String waiveReason, String category, String deliveryToEPC, String userId, String selectedCargo, 
			String conAddr, String notifyParty, String notifyPartyAddr, String placeDel, String placeReceipt,
			String shipperNm, String shipperAdd, String customHsCode, List<HsCodeDetails> multiHsCodeList, String blNoRoot, boolean isSplitBl)
			throws BusinessException // MCC add EPC_IND to Manifest
	{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		String strInsert = "";
		StringBuffer strMark = new StringBuffer();
		StringBuffer strCntr1 = new StringBuffer();
		StringBuffer strCntr2 = new StringBuffer();
		StringBuffer strCntr3 = new StringBuffer();
		StringBuffer strCntr4 = new StringBuffer();
		String streturn = "";
		StringBuffer strInsert_trans = new StringBuffer();
		StringBuffer strMark_trans = new StringBuffer();
		StringBuffer strCntr1_trans = new StringBuffer();
		StringBuffer strCntr2_trans = new StringBuffer();
		StringBuffer strCntr3_trans = new StringBuffer();
		StringBuffer strCntr4_trans = new StringBuffer();
		try {
			log.info("START: MftInsertionForEnhancementHSCode DAO distype:" + distype + "addval:" + addval + "coCd:"
					+ coCd + "varno:" + varno + "blno:" + blno + "crgtyp:" + crgtyp + "hscd:" + hscd + "hsSubCodeFr:"
					+ hsSubCodeFr + "hsSubCodeTo:" + hsSubCodeTo + "crgdesc:" + crgdesc + "mark:" + mark + "nopkgs:"
					+ nopkgs + "gwt:" + gwt + "gvol:" + gvol + "crgstat:" + crgstat + "dgind:" + dgind + "stgind:"
					+ stgind + "dop:" + dop + "pkgtyp:" + pkgtyp + "coname:" + coname + "consigneeCoyCode:"
					+ consigneeCoyCode + "poL:" + poL + "poD:" + poD + "poFD:" + poFD + "cntrtype:" + cntrtype
					+ "cntrsize:" + cntrsize + "cntr1:" + cntr1 + "cntr2:" + cntr2 + "cntr3:" + cntr3 + "cntr4:" + cntr4
					+ "autParty:" + autParty + "waiveReason" + waiveReason + "category" + category + "deliveryToEPC"
					+ deliveryToEPC + ",conAddr:" + conAddr + ",notifyParty:" + notifyParty + ",notifyPartyAddr:"
					+ notifyPartyAddr + ",placeDel:" + placeDel + ",shipperNm:" + shipperNm + ",shipperAdd:"
					+ shipperAdd + ",customHsCode:" + customHsCode + ", multiHsCodeList : " + multiHsCodeList.toString()
					+ "blNoRoot:" + blNoRoot + ", isSplitBl:" + isSplitBl);

			boolean chkBlNo = chkBlNo(blno, varno);
			if (chkBlNo) {
				throw new BusinessException("M20201");
			}
			boolean Pkgtyp = chkPkgtype(pkgtyp);
			if (!Pkgtyp) {
				throw new BusinessException("M21604");
			}

			boolean portcdl = chkPortCode(poL);
			if (!portcdl) {
				throw new BusinessException("M21601");

			}
			boolean portcdd = chkPortCode(poD);
			if (!portcdd) {
				throw new BusinessException("M21602");
			}
			boolean portcfd = chkPortCode(poFD);
			if (poFD != null && !poFD.equals("")) {
				if (!portcfd) {
					throw new BusinessException("M21603");
				} // end of if !port..
			} // end of if poFD
				// To check for DG Indicator
			if (dgind.equalsIgnoreCase("Y")) {
				boolean chkDGInd = chkDGInd(blno, varno);
				if (!chkDGInd) {
					throw new BusinessException("M20211");
				}
			}
			String hssubcodeDesc = getHSSubCodeDes(hscd, hsSubCodeFr, hsSubCodeTo);
			if (hssubcodeDesc == null || hssubcodeDesc.equalsIgnoreCase("")) {
				throw new BusinessException("M20223");
			}

			Map<String,String> mapErrorLength = this.checkLegthValidation(crgdesc, mark, conAddr, notifyParty, notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd);
			if(mapErrorLength.size() > 0) {
				String errorFields = "";
				for ( Entry<String, String> entry : mapErrorLength.entrySet()) {
				    String key = entry.getKey();
				    if(errorFields.length() == 0 ) {
				    	errorFields = key;
				    }else {
				    	errorFields = errorFields + "," + key;
				    }
				    
				}
				String[] tmpStrings = {errorFields};
				String errorMessage = CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_invalidLength, tmpStrings);
				throw new BusinessException(errorMessage);
			}
			
			String sblno = "";
			String sups = "";
			if (addval.equalsIgnoreCase("AddOl")) {
				sblno = "" + getOlBlNo();
				sups = "O";
			} else {
				sblno = blno;
			}
			int mftseqno = getMftNo();
			
			int splitId = 0;
			if(isSplitBl) {
				splitId = getMaxSplitId(blNoRoot);
			}

			// Added by MCconsulting , deliveryToEPC set to 'N' if deliveryToEPC is null or
			// empty.
			deliveryToEPC = (deliveryToEPC == null || deliveryToEPC.equalsIgnoreCase("null")
					|| deliveryToEPC.trim().length() == 0) ? "N" : deliveryToEPC;

			Timestamp adviceDttm = CommonUtility.toTimestamp(adviseDate);
			// Insert to MANIFEST_DETAILS
			sb.setLength(0);
			sb.append("INSERT INTO MANIFEST_DETAILS(");
			sb.append("MFT_SEQ_NBR, VAR_NBR, BL_NBR, BL_STATUS, CRG_TYPE, ");
			sb.append("CNTR_SIZE, CNTR_TYPE, STG_TYPE, CRG_STATUS, PKG_TYPE, ");
			sb.append("NBR_PKGS, EDO_NBR_PKGS, CRG_DES, GROSS_WT, GROSS_VOL, ");
			sb.append("DG_IND, HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, LD_PORT, ");
			sb.append("DIS_PORT, DES_PORT, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, CONS_NM, ");
			sb.append("CONS_CO_CD, SUPP_STATUS, NBR_PKGS_IN_PORT, DIS_TYPE, ADVISE_BY, ");
			sb.append("ADVISE_DATE, ADVISE_MODE, AMEND_CHARGED_TO, WAIVE_CHARGED, WAIVE_REASON, ");
			sb.append("MANIFEST_CREATE_CD, CARGO_CATEGORY_CD, EPC_IND,  ");
			sb.append("CONSIGNEE_ADDR, SHIPPER_NM, SHIPPER_ADDR,  ");
			sb.append("NOTIFY_PARTY, NOTIFY_PARTY_ADDR, PLACE_OF_DELIVERY,  ");
			sb.append("PLACE_OF_RECEIPT, CUSTOM_HS_CODE ");
			// if isSplitBl
			if(isSplitBl) {
				sb.append(", SPLIT_MAIN_BL, SPLIT_ID ");
			}
			sb.append(") VALUES ( :mftseqno, :varno, :sblno, :sblStatus, :crgtyp, ");
			sb.append(":cntrsize, :cntrtype, :stgind, :crgstat, :pkgtyp, ");
			sb.append(":nopkgs, :edoNbrPkgs, :crgdesc, :gwt, :gvol, ");
			sb.append(":dgind, :hscd, :hsSubCodeFr, :hsSubCodeTo, :poL,");
			sb.append(":poD, :poFD, :coCd, SYSDATE, :coname,");
			sb.append(":consigneeCoyCode, :sups, :nbrPkgs, :distype, :adviseBy,");
			sb.append(":adviceDttm, :adviseMode, :amendChargedTo, :waiveCharge, :waiveReason,");
			sb.append(":autParty, :category, :deliveryToEPC, ");
			// START CR FTZ - NS JUNE 2024
			sb.append(":conAddr, :shipperNm, :shipperAdd, :notifyParty, :notifyPartyAddr, :placeDel, ");
			sb.append(":placeReceipt, :customHsCode ");
			// END CR FTZ - NS JUNE 2024
			
			if(isSplitBl) {
				sb.append(", :blNoRoot, :splitId ");
				paramMap.put("blNoRoot", blNoRoot);	
				paramMap.put("splitId", String.valueOf(splitId));	
			}
			
			paramMap.put("mftseqno", mftseqno);
			paramMap.put("varno", varno);
			paramMap.put("sblno", sblno.trim());
			paramMap.put("sblStatus", "A");
			paramMap.put("crgtyp", crgtyp);
			paramMap.put("cntrsize", CommonUtility.deNull(cntrsize));
			paramMap.put("cntrtype", CommonUtility.deNull(cntrtype));
			paramMap.put("stgind", stgind);
			paramMap.put("crgstat", crgstat);
			paramMap.put("pkgtyp", pkgtyp);
			paramMap.put("nopkgs", nopkgs);
			paramMap.put("edoNbrPkgs", "0");
			paramMap.put("crgdesc", GbmsCommonUtility.addApostr(crgdesc));
			paramMap.put("gwt", gwt);
			paramMap.put("gvol", gvol);
			paramMap.put("dgind", CommonUtility.deNull(dgind));
			paramMap.put("hscd", hscd);
			paramMap.put("hsSubCodeFr", CommonUtility.deNull(hsSubCodeFr));
			paramMap.put("hsSubCodeTo", CommonUtility.deNull(hsSubCodeTo));
			paramMap.put("poL", poL);
			paramMap.put("poD", poD);
			paramMap.put("poFD", CommonUtility.deNull(poFD));
			paramMap.put("coCd", coCd);
			paramMap.put("coname", GbmsCommonUtility.addApostr(coname));
			paramMap.put("consigneeCoyCode", consigneeCoyCode);
			paramMap.put("sups", sups);
			paramMap.put("nbrPkgs", "0");
			paramMap.put("distype", CommonUtility.deNull(distype));
			paramMap.put("adviseBy", adviseBy);
			paramMap.put("adviceDttm", adviceDttm);
			paramMap.put("adviseMode", adviseMode);
			paramMap.put("amendChargedTo", amendChargedTo);
			paramMap.put("waiveCharge", waiveCharge);
			paramMap.put("waiveReason", waiveReason);
			paramMap.put("autParty", autParty);
			paramMap.put("category", category);
			paramMap.put("deliveryToEPC", CommonUtility.deNull(deliveryToEPC));
			// START CR FTZ HSCODE - NS JULY 2024
			paramMap.put("conAddr", CommonUtility.deNull(conAddr));
			paramMap.put("notifyParty", CommonUtility.deNull(notifyParty));
			paramMap.put("notifyPartyAddr", CommonUtility.deNull(notifyPartyAddr));
			paramMap.put("placeDel", CommonUtility.deNull(placeDel));
			paramMap.put("placeReceipt", CommonUtility.deNull(placeReceipt));
			paramMap.put("shipperNm", CommonUtility.deNull(shipperNm));
			paramMap.put("shipperAdd", CommonUtility.deNull(shipperAdd));
			paramMap.put("customHsCode", CommonUtility.deNull(customHsCode));
			// END CR FTZ HSCODE - NS JULY 2024
			sb.append(") ");
			strInsert = sb.toString();
			log.info("***** MftInsertionForEnhancementHSCode SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(strInsert, paramMap);
			// Added By NS on 25-09-20 to save cargo selection
			if (count != 0 ) {
				if( !StringUtils.isEmpty(selectedCargo)) {
					sb.setLength(0);
					sb.append(" INSERT INTO GBMS.MANIFEST_DETAILS_EXT  ");
					sb.append(" (MFT_SEQ_NBR,HS_SUB_DESC_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
					sb.append(" VALUES(:MFT_SEQ_NBR,:HS_SUB_DESC_CD,:userId,SYSDATE) ");
					paramMap.put("MFT_SEQ_NBR", mftseqno);
					paramMap.put("HS_SUB_DESC_CD", selectedCargo);
					paramMap.put("userId", userId);
					log.info("SQL" + sb.toString());
					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				}
			
				// START CR FTZ HSCODE - NS JULY 2024
				for (HsCodeDetails hsCodeObj : multiHsCodeList) {					

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
					sb.append(" VALUES(:MFT_SEQ_NBR,:MFT_HSCODE_SEQ_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
					paramMap.put("MFT_SEQ_NBR", mftseqno);
					paramMap.put("MFT_HSCODE_SEQ_NBR", seqValue);
					paramMap.put("HS_CODE",hsCodeObj.getHsCode());
					paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
					paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
					paramMap.put("NBR_PKGS",hsCodeObj.getNbrPkgs());
					paramMap.put("GROSS_WT",hsCodeObj.getGrossWt());
					paramMap.put("GROSS_VOL",hsCodeObj.getGrossVol());
					paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
					paramMap.put("CRG_DES",hsCodeObj.getCrgDes());	
					paramMap.put("HS_SUB_CODE_DESC",hsCodeObj.getHsSubCodeDesc());	
					paramMap.put("userId", userId);
					log.info("SQL" + sb.toString());
					log.info("params: " + paramMap.toString());
					int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
					log.info("counths : " + counths);
					
					sb.setLength(0);
					sb.append(" INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS  ");
					sb.append(" (MFT_SEQ_NBR, MFT_HSCODE_SEQ_NBR, AUDIT_DTTM, REC_STATUS, HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
					sb.append(" VALUES(:MFT_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, SYSDATE, 'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
					paramMap.put("MFT_SEQ_NBR", mftseqno);
					paramMap.put("MFT_HSCODE_SEQ_NBR", seqValue);
					paramMap.put("HS_CODE",hsCodeObj.getHsCode());
					paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
					paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
					paramMap.put("NBR_PKGS",hsCodeObj.getNbrPkgs());
					paramMap.put("GROSS_WT",hsCodeObj.getGrossWt());
					paramMap.put("GROSS_VOL",hsCodeObj.getGrossVol());
					paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
					paramMap.put("CRG_DES",hsCodeObj.getCrgDes());	
					paramMap.put("HS_SUB_CODE_DESC",hsCodeObj.getHsSubCodeDesc());	
					paramMap.put("userId", userId);
					log.info("SQL" + sb.toString());
					log.info("params: " + paramMap.toString());
					int counthsAudit = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
					log.info("counths : " + counthsAudit);
				}
				// END CR FTZ HSCODE - NS JULY 2024
			}
			log.info("count:" + count);
			String miscNo = "0";

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				strInsert_trans.setLength(0);
				strInsert_trans
						.append("INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,VAR_NBR,BL_NBR,CRG_TYPE,")
						.append("CNTR_SIZE,CNTR_TYPE,STG_TYPE,CRG_STATUS,PKG_TYPE,NBR_PKGS,CRG_DES,")
						.append("GROSS_WT,GROSS_VOL,DG_IND,HS_CODE,LD_PORT,DIS_PORT,DES_PORT,")
						.append("LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,CONS_NM,SUPP_STATUS, DIS_TYPE, ADVISE_BY, ADVISE_DATE, ADVISE_MODE, AMEND_CHARGED_TO, WAIVE_CHARGED, WAIVE_REASON, MISC_SEQ_NBR,CARGO_CATEGORY_CD,EPC_IND, ") 
						.append("CONSIGNEE_ADDR, SHIPPER_NM, SHIPPER_ADDR,  ")
						.append("NOTIFY_PARTY, NOTIFY_PARTY_ADDR, PLACE_OF_DELIVERY,  ")
						.append("PLACE_OF_RECEIPT, CUSTOM_HS_CODE) ")
						// MCC Add EPC_IND
						// VietNguyen added to update value for misc_seq_nbr
						.append("VALUES('0',:mftseqno,:varno,:sblno,:crgtyp,	")
						.append(":cntrsize,:cntrtype,:stgind,	").append(":crgstat,:pkgtyp,:nopkgs,:crgdesc,	")
						.append(":gwt,:gvol,:dgind,:hscd,	").append(":poL,:poD,:poFD,:coCd,SYSDATE,	")
						.append(":coname,:sups,:distype,	")
						.append(":adviseBy,:adviceDttm,:adviseMode,:amendChargedTo,:waiveCharge,:waiveReason,:miscNo,:category,:deliveryToEPC,	")
						.append(":conAddr, :shipperNm, :shipperAdd, :notifyParty,:notifyPartyAddr, ")
						.append(":placeDel, :placeReceipt,:customHsCode)");
				strMark_trans.setLength(0);
				strMark_trans.append(
						"INSERT INTO MFT_MARKINGS_TRANS(TRANS_NBR,MFT_SQ_NBR,MFT_MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ")
						.append("VALUES('0',:mftseqno,:mark,:coCd,sysdate)");

			}
			// Transaction Log Table Insertion
			strMark.setLength(0);
			strMark.append("INSERT INTO MFT_MARKINGS(MFT_SQ_NBR,MFT_MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ")
					.append("VALUES(:mftseqno,:mark,:coCd,SYSDATE)");
			paramMap.put("mftseqno", mftseqno);
			paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
			paramMap.put("coCd", coCd);
			log.info("***** MftInsertionForEnhancementHSCode SQL *****" + strMark.toString());
			log.info("params: " + paramMap.toString());
			int cntmark = namedParameterJdbcTemplate.update(strMark.toString(), paramMap);
			int count_trans = 0;
			int cnt_mark_trans = 0;

			// Transaction Log Table Insertion
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				paramMap.put("adviceDttm", adviceDttm);
				paramMap.put("mftseqno", mftseqno);
				paramMap.put("varno", varno);
				paramMap.put("sblno", sblno.trim());
				paramMap.put("crgtyp", crgtyp);
				paramMap.put("cntrsize", CommonUtility.deNull(cntrsize));
				paramMap.put("cntrtype", CommonUtility.deNull(cntrtype));
				paramMap.put("stgind", stgind);
				paramMap.put("crgstat", crgstat);
				paramMap.put("stgind", stgind);
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
				paramMap.put("coCd", coCd);
//				paramMap.put("poFD", CommonUtility.deNull(poFD));
				paramMap.put("coname", GbmsCommonUtility.addApostr(coname));
				paramMap.put("sups", sups);
				paramMap.put("distype", CommonUtility.deNull(distype));
				paramMap.put("adviseBy", adviseBy);
				paramMap.put("adviseMode", adviseMode);
				paramMap.put("amendChargedTo", amendChargedTo);
				paramMap.put("waiveCharge", waiveCharge);
				paramMap.put("waiveReason", waiveReason);
				paramMap.put("miscNo", miscNo);
				paramMap.put("category", category);
				paramMap.put("deliveryToEPC", CommonUtility.deNull(deliveryToEPC));
				// START CR FTZ HSCODE - NS JULY 2024
				paramMap.put("conAddr", CommonUtility.deNull(conAddr));
				paramMap.put("notifyParty", CommonUtility.deNull(notifyParty));
				paramMap.put("notifyPartyAddr", CommonUtility.deNull(notifyPartyAddr));
				paramMap.put("placeDel", CommonUtility.deNull(placeDel));
				paramMap.put("placeReceipt", CommonUtility.deNull(placeReceipt));
				paramMap.put("shipperNm", CommonUtility.deNull(shipperNm));
				paramMap.put("shipperAdd", CommonUtility.deNull(shipperAdd));
				paramMap.put("customHsCode", CommonUtility.deNull(customHsCode));
				// END CR FTZ HSCODE - NS JULY 2024
				log.info("***** MftInsertionForEnhancementHSCode SQL *****" + strInsert_trans.toString());
				log.info("params: " + paramMap.toString());
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans.toString(), paramMap);
				
				paramMap.clear();
				paramMap.put("mftseqno", mftseqno);
				paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
				paramMap.put("coCd", coCd);
				log.info("***** MftInsertionForEnhancementHSCode SQL *****" + strMark_trans.toString());
				log.info("params: " + paramMap.toString());
				cnt_mark_trans = namedParameterJdbcTemplate.update(strMark_trans.toString(), paramMap);
				strCntr1_trans.append("INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) ")
						.append("VALUES('0',1,:mftseqno ,:cntr1)");
				strCntr2_trans.append("INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) ")
						.append("VALUES('0',2,:mftseqno,:cntr2)");
				strCntr3_trans.append("INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) ")
						.append("VALUES('0',3,:mftseqno,:cntr3)");
				strCntr4_trans.append("INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) ")
						.append("VALUES('0',4,:mftseqno,:cntr4)");
			}
			strCntr1.append("INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) ")
					.append("VALUES(1,:mftseqno,:cntr1)");
			strCntr2.append("INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) ")
					.append("VALUES(2,:mftseqno,:cntr2) ");
			strCntr3.append("INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) ")
					.append("VALUES(3,:mftseqno,:cntr3)");
			strCntr4.append("INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) ")
					.append("VALUES(4,:mftseqno,:cntr4)");
			if (cntrtype != null && !cntrtype.equals("") && cntrsize != null && !cntrsize.equals("")) {
				if (cntr1 != null && !cntr1.equals("")) {
					paramMap.put("mftseqno", mftseqno);
					paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
					log.info("***** MftInsertionForEnhancementHSCode SQL *****" + strCntr1.toString());
					log.info("params: " + paramMap.toString());
					namedParameterJdbcTemplate.update(strCntr1.toString(), paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion
						paramMap.put("mftseqno", mftseqno);
						paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
						log.info("***** MftInsertionForEnhancementHSCode SQL *****" + strCntr1_trans.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(strCntr1_trans.toString(), paramMap);
					}
				}
				if (cntr2 != null && !cntr2.equals("")) {
					paramMap.put("mftseqno", mftseqno);
					paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
					log.info("***** MftInsertionForEnhancementHSCode SQL *****" + strCntr2.toString());
					log.info("params: " + paramMap.toString());
					namedParameterJdbcTemplate.update(strCntr2.toString(), paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion
						paramMap.put("mftseqno", mftseqno);
						paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
						log.info("***** MftInsertionForEnhancementHSCode SQL *****" + strCntr2_trans.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(strCntr2_trans.toString(), paramMap);
					}
				}
				if (cntr3 != null && !cntr3.equals("")) {
					paramMap.put("mftseqno", mftseqno);
					paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
					log.info("***** MftInsertionForEnhancementHSCode SQL *****" + strCntr3.toString());
					log.info("params: " + paramMap.toString());
					namedParameterJdbcTemplate.update(strCntr3.toString(), paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion
						paramMap.put("mftseqno", mftseqno);
						paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
						log.info("***** MftInsertionForEnhancementHSCode SQL *****" + strCntr3_trans.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(strCntr3_trans.toString(), paramMap);
					}
				}
				if (cntr4 != null && !cntr4.equals("")) {
					paramMap.put("mftseqno", mftseqno);
					paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
					log.info("***** MftInsertionForEnhancementHSCode SQL *****" + strCntr4.toString());
					log.info("params: " + paramMap.toString());
					namedParameterJdbcTemplate.update(strCntr4.toString(), paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion
						paramMap.put("mftseqno", mftseqno);
						paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
						log.info("***** MftInsertionForEnhancementHSCode SQL *****" + strCntr4_trans.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(strCntr4_trans.toString(), paramMap);
					}
				}
			} 

			if (count == 0 || cntmark == 0) {
				throw new BusinessException("M4201");
			}

			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion
				if (count_trans == 0 || cnt_mark_trans == 0) {
					throw new BusinessException("M4201");
				}
			}
			streturn = mftseqno + "-" + sblno;

		}  catch (NullPointerException e) {
			log.info("Exception MftInsertionForEnhancementHSCode : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception MftInsertionForEnhancementHSCode : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftInsertionForEnhancementHSCode : ", e);
			throw new BusinessException("M4201");
		}  finally {
			log.info("END: **** MftInsertionForEnhancementHSCode DAO ***** streturn:" + streturn);
		}
		return streturn;
	}

	private Map<String, String> checkLegthValidation(String crgdesc, String mark, String conAddr, String notifyParty,
			String notifyPartyAddr, String placeDel, String placeReceipt, String shipperNm, String shipperAdd) throws BusinessException {
		Map<String, String> mapErrorLength = new HashMap<String, String>();
		try {
			log.info("START: checkLegthValidation DAO crgdesc" + crgdesc + "mark:" + mark+ "conAddr:" + conAddr
					+ "notifyParty:" + notifyParty+ "notifyPartyAddr:" + notifyPartyAddr
					+ "placeDel:" + placeDel+ "placeReceipt:" + placeReceipt+ "shipperNm:" + shipperNm+ "shipperAdd:" + shipperAdd);
			
			if(!CommonUtil.deNull(crgdesc).isEmpty() && crgdesc.length() > 4000) {
				mapErrorLength.put("Cargo Description", String.valueOf(crgdesc.length()));
			}
			if(!CommonUtil.deNull(mark).isEmpty() && mark.length() > 200) {
				mapErrorLength.put("Marking", String.valueOf(mark.length()));
			}
			if(!CommonUtil.deNull(notifyParty).isEmpty() && notifyParty.length() > 70) {
				mapErrorLength.put("Notify party", String.valueOf(notifyParty.length()));
			}
			if(!CommonUtil.deNull(placeDel).isEmpty() && placeDel.length() > 70) {
				mapErrorLength.put("Place of Delivery", String.valueOf(placeDel.length()));
			}
			if(!CommonUtil.deNull(placeReceipt).isEmpty() && placeReceipt.length() > 70) {
				mapErrorLength.put("Place of Receipt", String.valueOf(placeReceipt.length()));
			}
			if(!CommonUtil.deNull(shipperNm).isEmpty() && shipperNm.length() > 70) {
				mapErrorLength.put("Shipper Name", String.valueOf(shipperNm.length()));
			}
			if(!CommonUtil.deNull(conAddr).isEmpty() && conAddr.length() > 500) {
				mapErrorLength.put("Consignee Address", String.valueOf(conAddr.length()));
			}
			if(!CommonUtil.deNull(shipperAdd).isEmpty() && shipperAdd.length() > 500) {
				mapErrorLength.put("Shipper Address", String.valueOf(shipperAdd.length()));
			}
			if(!CommonUtil.deNull(notifyPartyAddr).isEmpty() && notifyPartyAddr.length() > 500) {
				mapErrorLength.put("Notify party Address", String.valueOf(notifyPartyAddr.length()));
			}
			
		} catch (Exception e) {
			log.info("Exception checkLegthValidation : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** checkLegthValidation  DAO  **** bb1no: " + mapErrorLength.toString());
		}
		return mapErrorLength;
	}

	private boolean chkBlNo(String blno, String varno) throws BusinessException {
		boolean bblno = false;
		blno = blno.trim();
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: chkBlNo DAO" + blno + "varno:" + varno);
			sb.append(" SELECT BL_NBR FROM MANIFEST_DETAILS WHERE BL_STATUS='A' AND BL_NBR=:blno ");
			sb.append(" AND VAR_NBR=:varno ");
			paramMap.put("blno", blno);
			paramMap.put("varno", varno);
			log.info("***** chkBlNo SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bblno = true;
			} else {
				bblno = false;
			}
		} catch (Exception e) {
			log.info("Exception chkBlNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** chkBlNo  DAO  **** bb1no: " + bblno);
		}
		return bblno;
	}

	private boolean chkPkgtype(String pkgcd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		boolean bpkgcd = false;
		try {
			log.info("START: chkPkgtype DAO" + "pkgcd:" + pkgcd);
			sb.append("  SELECT PKG_TYPE_CD FROM PKG_TYPES WHERE PKG_TYPE_CD=:pkgcd and REC_STATUS='A' ");
			paramMap.put("pkgcd", pkgcd);
			log.info("***** chkPkgtype SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bpkgcd = true;
			} else {
				bpkgcd = false;
			}
		} catch (Exception e) {
			log.info("Exception chkPkgtype : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** chkPkgtype Result **** bpkgcd: " + bpkgcd);
		}
		return bpkgcd;
	}

	private boolean chkPortCode(String portcd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		boolean bpcd = false;
		try {
			log.info("START: chkPortCode DAO" + "portcd:" + portcd);
			sb.append("  SELECT PORT_CD FROM UN_PORT_CODE WHERE REC_STATUS = 'A' AND PORT_CD=:portcd ");
			paramMap.put("portcd", portcd);
			log.info("***** chkPortCode SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bpcd = true;
			} else {
				bpcd = false;
			}
		} catch (Exception e) {
			log.info("Exception chkPortCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** chkPortCode Result *** bpcd: " + bpcd);
		}
		return bpcd;
	}

	private String getHSSubCodeDes(String hsCode, String hsSubCodeFr, String hsSubCodeTo) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String desc = "";
		try {
			log.info("START: getHSSubCodeDes DAO hsCode:" + hsCode + "hsSubCodeFr:" + hsSubCodeFr + "hsSubCodeTo:"
					+ hsSubCodeTo);
			sb.append(" SELECT HS_SUB_DESC FROM HS_SUB_CODE WHERE HS_CODE=:hsCode  AND HS_SUB_CODE_FR=:hsSubCodeFr ");
			if (hsSubCodeTo != null && !"".equalsIgnoreCase(hsSubCodeTo)) {
				sb.append(" AND HS_SUB_CODE_TO =:hsSubCodeTo  ");
			}
			paramMap.put("hsCode", hsCode);
			paramMap.put("hsSubCodeFr", hsSubCodeFr);
			paramMap.put("hsSubCodeTo", hsSubCodeTo);
			log.info("***** getHSSubCodeDes SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				desc = CommonUtility.deNull(rs.getString("HS_SUB_DESC"));
			}
		} catch (Exception e) {
			log.info("Exception getHSSubCodeDes : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getHSSubCodeDes Result ***** desc: " + desc);
		}
		return desc;
	}

	private String getOlBlNo() throws BusinessException {
		String OlBlNo = "";
		int count = 0;
		String sql = "";
		String sysdate = "";
		String sqldate = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getOlBlNo DAO");
			sqldate = "SELECT TO_CHAR(SYSDATE,'YYMM') FROM DUAL";
			log.info("***** getOlBlNo SQL *****" + sqldate.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqldate, paramMap);
			if (rs.next())
				sysdate = rs.getString(1);
			
			sql = "SELECT COUNT(BL_NBR) FROM MANIFEST_DETAILS WHERE BL_NBR LIKE :OLsysdate";
			paramMap.put("OLsysdate", "OL" + sysdate + "%");
			log.info("***** getOlBlNo SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				count = rs.getInt("COUNT(BL_NBR)");
			}
			if (count == 0) {
				OlBlNo = "OL" + sysdate + "0000";
			} else {
				sb.append(
						" SELECT 'OL'||:sysdate ||TRIM(NVL(TRIM(TO_CHAR(MAX(TO_NUMBER(SUBSTR(BL_NBR,7)))+1,'0000')),'0000')) FROM MANIFEST_DETAILS WHERE BL_NBR LIKE :OLsysdate");
				paramMap.put("OLsysdate", "OL" + sysdate + "%");
				paramMap.put("sysdate", sysdate);
				log.info("***** getOlBlNo SQL *****" + sb.toString());
				log.info("params: " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					OlBlNo = rs.getString(1);
				}
			}
		} catch (Exception e) {
			log.info("Exception getOlBlNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getOlBlNo Result ***** desc: " + OlBlNo);
		}
		return OlBlNo;
	}

	private int getMftNo() throws BusinessException {
		int MftSeqNo = 0;
		int count = 0;
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getMftNo DAO ");
			sql = "SELECT COUNT(MFT_SEQ_NBR) FROM MANIFEST_DETAILS";
			log.info("***** getMftNo SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				count = rs.getInt("COUNT(MFT_SEQ_NBR)");
			}

			if (count == 0) {
				MftSeqNo = 1;
			} else {
				sql = "SELECT MAX(TO_NUMBER(MFT_SEQ_NBR)) FROM MANIFEST_DETAILS";
				log.info("***** getMftNo SQL *****" + sql.toString());
				log.info("params: " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					MftSeqNo = rs.getInt("MAX(TO_NUMBER(MFT_SEQ_NBR))");
					MftSeqNo = MftSeqNo + 1;
				}
			}
		} catch (Exception e) {
			log.info("Exception getMftNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getMftNo Result ***** MftSeqNo: " + MftSeqNo);
		}
		return MftSeqNo;
	}
	
	
	private int getMaxSplitId(String blNoRoot) throws BusinessException {
		int splitId = 0;
		int count = 0;
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getMaxSplitId DAO ");
			sql = "SELECT COUNT(SPLIT_ID) FROM MANIFEST_DETAILS WHERE SPLIT_MAIN_BL = :blNoRoot ";
			paramMap.put("blNoRoot", blNoRoot);
			log.info("***** getMaxSplitId SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				count = rs.getInt("COUNT(SPLIT_ID)");
			}

			if (count == 0) {
				splitId = 0;
			} else {
				sql = "SELECT MAX(SPLIT_ID) FROM MANIFEST_DETAILS WHERE SPLIT_MAIN_BL = :blNoRoot";
				log.info("***** getMaxSplitId SQL *****" + sql.toString());
				log.info("params: " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					splitId = rs.getInt("MAX(SPLIT_ID)");
					splitId = splitId + 1;
				}
			}
		} catch (Exception e) {
			log.info("Exception getMaxSplitId : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getMaxSplitId Result ***** splitId: " + splitId);
		}
		return splitId;
	}

	@Override
	public int captureWaiverAdviceRequest(String waiverRefNo, String userID, String waiverRefType, boolean resendReq,
			String adviceIdStr, String vvCd, String waiveReason) throws BusinessException {
		int adviceId = 0;
		try {
			log.info("START:captureWaiverAdviceRequest DAO waiverRefNo:" + waiverRefNo + "userID:" + userID
					+ "waiverRefType:" + waiverRefType + "resendReq:" + resendReq + "adviceIdStr:" + adviceIdStr
					+ "vvCd" + vvCd + "waiveReason:" + CommonUtility.deNull(waiveReason));
			if (resendReq) {
				adviceId = Integer.valueOf(adviceIdStr);
			} else {
				adviceId = insertWaiverAdvice(waiverRefNo, userID, waiverRefType, vvCd, waiveReason); // Fixed. to pass vv_cd
			}
		} catch (Exception e) {
			log.info("Exception captureWaiverAdviceRequest : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** captureWaiverAdviceRequest Result ***** adviceId: " + adviceId);
		}
		return adviceId;

	}

	private int insertWaiverAdvice(String refNbr, String userId, String waiverRefType, String vvCd, String waiveReason) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		int adviceId = 0;
		StringBuilder sqlWaiIns = new StringBuilder();
		try {
			log.info("START:insertWaiverAdvice refNbr:" + refNbr + "userId:" + userId + "waiverRefType:" + waiverRefType
					+ "vvCd:" + vvCd + "waiveReason:" + CommonUtility.deNull(waiveReason));
			adviceId = getWaiverAdviseNumber();
			SimpleDateFormat formatter = new SimpleDateFormat("yyMM");
			String today = formatter.format(new java.util.Date());
			String adviceNbr = "WAN-" + today + "-" + adviceId;
			sqlWaiIns.append(
					"INSERT INTO WAIVER_ADVICE (ADVICE_ID, ADVICE_NBR, REF_NBR, REF_TYPE, STATUS,REQUESTED_BY,REQUESTED_AT,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, VV_CD, WAIVE_REASON) ");
			sqlWaiIns.append(
					" VALUES ( :adviceId,:adviceNbr,:refNbr,:waiverRefType,'P',:userId,SYSDATE,:userId,SYSDATE,:varCode,:waiveReason)");
			String varCode = (vvCd == null) ? "" : vvCd.trim();

			paramMap.put("adviceId", String.valueOf(adviceId));
			paramMap.put("adviceNbr", adviceNbr);
			paramMap.put("refNbr", refNbr);
			paramMap.put("waiverRefType", waiverRefType);
			paramMap.put("userId", userId);
			paramMap.put("userId", userId);
			paramMap.put("varCode", varCode);
			paramMap.put("waiveReason", waiveReason);
			
			log.info("***** START:insertWaiverAdvice SQL *****" + sqlWaiIns.toString());
			log.info("params: " + paramMap.toString());
			namedParameterJdbcTemplate.update(sqlWaiIns.toString(), paramMap);
		} catch (Exception e) {
			log.info("Exception insertWaiverAdvice : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** insertWaiverAdvice Result ***** adviceId:" + adviceId);
		}
		return adviceId;

	}

	private int getWaiverAdviseNumber() throws BusinessException {
		int waiverSeqNo = 0;
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getWaiverAdviseNumber");
			sql = " SELECT WAIVER_ADVICE_SEQ.NEXTVAL COUNT FROM DUAL ";
			log.info("***** getWaiverAdviseNumber SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				waiverSeqNo = rs.getInt("COUNT");
			}
		} catch (Exception e) {
			log.info("Exception getWaiverAdviseNumber : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getWaiverAdviseNumber Result ***** waiverSeqNo: " + waiverSeqNo);
		}
		return waiverSeqNo;
	}

	@Override
	public AdminFeeWaiverValueObject invokeOscarWaiverRequest(int adviceId, String waiverRefNo, String userID,
			String waiverRefType) throws BusinessException {
		try {
			log.info("START: invokeOscarWaiverRequest DAO adviceId:" + adviceId + "waiverRefNo:" + waiverRefNo
					+ "userID:" + userID + "waiverRefType:" + waiverRefType);
			if (adviceId != 0) {
				log.info("END:  *** invokeOscarWaiverRequest Result **** result: " + oscarWebserviceCall(adviceId, waiverRefNo, waiverRefType, userID));
				return oscarWebserviceCall(adviceId, waiverRefNo, waiverRefType, userID);
			}
		} catch (Exception e) {
			log.info("Exception invokeOscarWaiverRequest : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: invokeOscarWaiverRequest ");
		} 
		log.info("END:  *** invokeOscarWaiverRequest Result **** result: null ");
		return null;
	}

	private AdminFeeWaiverValueObject oscarWebserviceCall(int adviceId, String waiverRefNo, String waiverRefType,
			String userID) throws BusinessException {
		AdminFeeWaiverValueObject adminFeeWaiverVO = null;
		try {
			log.info("START: oscarWebserviceCall:adviceId" + adviceId + "waiverRefNo:" + waiverRefNo + "waiverRefType:"
					+ waiverRefType);
			// prepare advice data to raise oscar request for shipstore
			if (waiverRefType != null && waiverRefType.equals(ProcessChargeConst.SS_SSAD)) {
				// prepare waiver request
				adminFeeWaiverVO = captureSSAdminWaiverRequest(adviceId, waiverRefNo, userID, waiverRefType);
			} else if (waiverRefType != null && (waiverRefType.equals(ProcessChargeConst.MF_MADD)
					|| waiverRefType.equals(ProcessChargeConst.MF_MADM))) {
				// prepare waiver request
				adminFeeWaiverVO = captureMFAdminWaiverRequest(adviceId, waiverRefNo, userID, waiverRefType);
			}
			log.info(" adminFeeWaiverVO " + adminFeeWaiverVO.toString());
		} catch (Exception e) {
			log.info("Exception oscarWebserviceCall : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** oscarWebserviceCall Dao ***** aminFeeWaiverVo: " + adminFeeWaiverVO);
		}
		return adminFeeWaiverVO;
	}

	private AdminFeeWaiverValueObject captureSSAdminWaiverRequest(int adviceId, String ssEsnNbr, String userID,
			String waiverRefType) throws BusinessException {
		StringBuilder sqlSSRef = null;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		AdminFeeWaiverValueObject adminFeeVO = null;
		try {
			log.info("START: captureSSAdminWaiverRequest DAO adviceId:" + adviceId + "ssEsnNbr:" + ssEsnNbr + "userID:"
					+ userID + "waiverRefType" + waiverRefType);
			sqlSSRef = new StringBuilder();
			sqlSSRef.append(
					" SELECT WA.ADVICE_ID, WA.ADVICE_NBR, WA.REF_NBR, WA.REF_TYPE, WA.REQUESTED_BY, WA.REQUESTED_AT, SS.SS_REF_NBR, SS.SHIPPER_CD, SS.SHIPPER_NM, SS.SHIPPER_ADDR, ");
			sqlSSRef.append(
					" SS.ACCT_NBR, SS.ADMIN_FEE_WAIVER_REASON, ES.OUT_VOY_VAR_NBR, VC.VSL_NM, VC.IN_VOY_NBR, VC.OUT_VOY_NBR, B.ATB_DTTM, B.ETB_DTTM, VC.VSL_BERTH_DTTM BTR_DTTM ");
			sqlSSRef.append(
					"FROM WAIVER_ADVICE WA, SS_DETAILS SS, ESN ES, VESSEL_CALL VC, BERTHING B WHERE WA.REF_NBR=TO_CHAR(SS.ESN_ASN_NBR) AND SS.ESN_ASN_NBR = ES.ESN_ASN_NBR  ");
			sqlSSRef.append(
					" AND ES.OUT_VOY_VAR_NBR = VC.VV_CD AND VC.VV_CD=B.VV_CD AND B.SHIFT_IND=(SELECT MAX(SHIFT_IND) FROM BERTHING WHERE VV_CD=ES.OUT_VOY_VAR_NBR) AND WA.ADVICE_ID=:ADVICE_ID AND WA.REF_TYPE=:REF_TYPE  ");
			paramMap.put("ADVICE_ID", String.valueOf(adviceId));
			paramMap.put("REF_TYPE", waiverRefType);
			log.info("***** captureSSAdminWaiverRequest SQL *****" + sqlSSRef.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlSSRef.toString(), paramMap);
			adminFeeVO = new AdminFeeWaiverValueObject();
			Date requestedAt = null;
			Timestamp etb = null;
			Timestamp atb = null;
			Timestamp btr = null;
			Timestamp varDttm = null;

			if (rs.next()) {
				adminFeeVO.setAdviceId(CommonUtility.deNull(rs.getString("ADVICE_ID")));
				adminFeeVO.setWanAdviceNbr(CommonUtility.deNull(rs.getString("ADVICE_NBR")));
				adminFeeVO.setAsnNumber(CommonUtility.deNull(rs.getString("REF_NBR")));
				adminFeeVO.setRequestedBy(CommonUtility.deNull(rs.getString("REQUESTED_BY")));
				requestedAt = rs.getDate("REQUESTED_AT");
				adminFeeVO.setRequestedAt(CommonUtility.formatDateToStr(requestedAt, this.dateFormat2));
				adminFeeVO.setRefNumber(CommonUtility.deNull(rs.getString("SS_REF_NBR")));
				adminFeeVO.setWaiverCompany(CommonUtility.deNull(rs.getString("SHIPPER_NM")));
				adminFeeVO.setCompanyAddress(CommonUtility.deNull(rs.getString("SHIPPER_ADDR")));
				adminFeeVO.setCompanyAccount(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				adminFeeVO.setWaiverReasons(CommonUtility.deNull(rs.getString("ADMIN_FEE_WAIVER_REASON")));
				adminFeeVO.setVarCode(CommonUtility.deNull(rs.getString("OUT_VOY_VAR_NBR")));
				adminFeeVO.setVesselVoy(CommonUtility.deNull(rs.getString("VSL_NM")) + " / "
						+ CommonUtility.deNull(rs.getString("IN_VOY_NBR")) + " / "
						+ CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				atb = rs.getTimestamp("ATB_DTTM");
				etb = rs.getTimestamp("ETB_DTTM");
				btr = rs.getTimestamp("BTR_DTTM");
			}

			if (atb != null) {
				varDttm = atb;
			} else if (etb != null) {
				varDttm = etb;
			} else if (btr != null) {
				varDttm = btr;
			} else {
				varDttm = new Timestamp(System.currentTimeMillis());
			}
			adminFeeVO.setAtbEtbBtr(CommonUtility.formatDateToStr(varDttm, this.dateFormat));
			adminFeeVO = prepareTariffInformation(adminFeeVO, ProcessChargeConst.TARIFF_MAIN_ADMIN,
					ProcessChargeConst.TARIFF_SUB_SS_ADMIN_CHARGE, varDttm);
			adminFeeVO.setCreateUserId(userID);

		} catch (Exception e) {
			log.info("Exception captureSSAdminWaiverRequest : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** captureSSAdminWaiverRequest ***** adminFeeVo: " + adminFeeVO);
		}
		return adminFeeVO;
	}

	private AdminFeeWaiverValueObject prepareTariffInformation(AdminFeeWaiverValueObject adminFeeVO,
			String tariffMainCat, String tariffSubCat, Timestamp atb) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = null;
		try {
			log.info("START:prepareTariffInformation  adminFeeVO:" + adminFeeVO.toString() + "tariffMainCat:"
					+ tariffMainCat + "tariffSubCat:" + tariffSubCat + "atb" + atb);
			DecimalFormat decFormat = new DecimalFormat("0.00");
			sb = new StringBuilder();
			sb.append(
					" SELECT TARIFF_DESC, AMT_CHARGE, GST_CD FROM TARIFF_MAIN TM, TARIFF_TIER TT WHERE TM.TARIFF_CD = TT.TARIFF_CD AND TM.VERSION_NBR=TT.VERSION_NBR AND ");
			sb.append(
					" TM.CUST_CD IS NULL AND TT.CUST_CD IS NULL AND TM.TARIFF_MAIN_CAT_CD =:tariffMainCat AND TM.TARIFF_SUB_CAT_CD=:tariffSubCat AND TM.VERSION_NBR=(SELECT MAX(VERSION_NBR) FROM TARIFF_VERSION WHERE :atb >= EFF_START_DTTM) ");

			paramMap.put("tariffMainCat", tariffMainCat);
			paramMap.put("tariffSubCat", tariffSubCat);
			paramMap.put("atb", atb);
			log.info("***** prepareTariffInformation SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			String tariffDesc = null;
			Double unitRate = 0.00;
			String gstCd = null;
			while (rs.next()) {
				tariffDesc = CommonUtility.deNull(rs.getString("TARIFF_DESC"));
				unitRate = rs.getDouble("AMT_CHARGE");
				gstCd = CommonUtility.deNull(rs.getString("GST_CD"));
			}
			if (tariffSubCat.equals(ProcessChargeConst.TARIFF_MAIN_MANIFEST_AMENDMENT)) {
				adminFeeVO.setTariffDesc(tariffDesc + "(BL Nbr : " + adminFeeVO.getRefNumber() + ")");
			} else if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_SS_ADMIN_CHARGE)) {
				adminFeeVO.setTariffDesc(tariffDesc + "( Ship Store Ref Nbr : " + adminFeeVO.getRefNumber()
						+ "; ESN Nbr : " + adminFeeVO.getAsnNumber() + ")");
			}
			adminFeeVO.setUnitNbr("1");
			adminFeeVO.setUnitRate(decFormat.format(unitRate));
			sb.setLength(0);
			sb.append(" SELECT GST_CHARGE ");
			sb.append(
					" FROM GST_PARA, MISC_TYPE_CODE WHERE GST_PARA.REC_STATUS='A' AND MISC_TYPE_CODE.REC_STATUS='A' AND CAT_CD='FMAS_GCODE' AND FMAS_GST_CD=MISC_TYPE_CD ");
			sb.append(
					" AND TARIFF_GST_CD=:gstCd AND EFF_START_DTTM<=:effStart AND (EFF_END_DTTM>=:effEnd OR EFF_END_DTTM IS NULL) ");

			paramMap.put("gstCd", gstCd);
			paramMap.put("effStart", atb);
			paramMap.put("effEnd", atb);
			log.info("***** prepareTariffInformation SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			Double gstCharge = 0.00;
			while (rs.next()) {
				gstCharge = rs.getDouble("GST_CHARGE");
			}
			Double totalAmount = 0.00;
			totalAmount = unitRate + unitRate * gstCharge / 100;
			adminFeeVO.setGst(decFormat.format(gstCharge));
			adminFeeVO.setTotalAmount(decFormat.format(totalAmount));

		} catch (Exception e) {
			log.info("Exception prepareTariffInformation : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** prepareTariffInformation ***** adminFeeVo: " + adminFeeVO);
		}
		return adminFeeVO;
	}

	private AdminFeeWaiverValueObject captureMFAdminWaiverRequest(int adviceId, String mfBlNbr, String userID,
			String waiverRefType) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = null;
		AdminFeeWaiverValueObject adminFeeVO = null;
		try {
			log.info("START: DAO captureMFAdminWaiverRequest adviceId:" + adviceId + "mfBlNbr:" + mfBlNbr + "userID:"
					+ userID + "waiverRefType:" + waiverRefType);
			sb = new StringBuilder();
			sb.append(
					" SELECT WA.ADVICE_ID, WA.ADVICE_NBR, WA.REF_NBR, WA.REF_TYPE, WA.REQUESTED_BY, WA.REQUESTED_AT, MF.BL_NBR, MF.WAIVE_REASON, ");
			sb.append(
					" MF.AMEND_CHARGED_TO, MF.MANIFEST_CREATE_CD, CO.CO_NM, CA.ADD_L1 || ',' || CA.ADD_L2 CUST_ADDR,  ");
			sb.append(
					" MF.VAR_NBR, VC.VSL_NM, VC.IN_VOY_NBR, VC.OUT_VOY_NBR, B.ATB_DTTM, B.ETB_DTTM, VC.VSL_BERTH_DTTM BTR_DTTM ");
			sb.append(
					" FROM WAIVER_ADVICE WA, MANIFEST_DETAILS MF, VESSEL_CALL VC, BERTHING B, CUST_ACCT CU, COMPANY_CODE CO, CUST_ADDRESS CA ");
			sb.append(
					" WHERE WA.REF_NBR=MF.BL_NBR AND MF.AMEND_CHARGED_TO=CU.ACCT_NBR AND CO.CO_CD=CU.CUST_CD AND CO.CO_CD=CA.CUST_CD  ");
			sb.append(
					" AND MF.VAR_NBR = VC.VV_CD AND VC.VV_CD=B.VV_CD AND B.SHIFT_IND=(SELECT MAX(SHIFT_IND) FROM BERTHING WHERE VV_CD=VC.VV_CD) ");
			sb.append("  AND MF.VAR_NBR = WA.VV_CD AND WA.ADVICE_ID=:adviceId ");

			adminFeeVO = new AdminFeeWaiverValueObject();
			Date requestedAt = null;
			Timestamp etb = null;
			Timestamp atb = null;
			Timestamp btr = null;
			Timestamp varDttm = null;

			paramMap.put("adviceId", String.valueOf(adviceId));
			log.info("***** captureMFAdminWaiverRequest SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				adminFeeVO.setAdviceId(CommonUtility.deNull(rs.getString("ADVICE_ID")));
				adminFeeVO.setWanAdviceNbr(CommonUtility.deNull(rs.getString("ADVICE_NBR")));
				adminFeeVO.setAsnNumber(CommonUtility.deNull(rs.getString("REF_NBR")));
				adminFeeVO.setRequestedBy(CommonUtility.deNull(rs.getString("REQUESTED_BY")));
				requestedAt = rs.getDate("REQUESTED_AT");
				adminFeeVO.setRequestedAt(CommonUtility.formatDateToStr(requestedAt, this.dateFormat));
				adminFeeVO.setRefNumber(CommonUtility.deNull(rs.getString("BL_NBR")));
				adminFeeVO.setWaiverCompany(CommonUtility.deNull(rs.getString("CO_NM")));
				adminFeeVO.setCompanyAddress(CommonUtility.deNull(rs.getString("CUST_ADDR")));
				adminFeeVO.setCompanyAccount(CommonUtility.deNull(rs.getString("AMEND_CHARGED_TO")));
				adminFeeVO.setWaiverReasons(CommonUtility.deNull(rs.getString("WAIVE_REASON")));
				adminFeeVO.setVarCode(CommonUtility.deNull(rs.getString("VAR_NBR")));
				adminFeeVO.setVesselVoy(CommonUtility.deNull(rs.getString("VSL_NM")) + " / "
						+ CommonUtility.deNull(rs.getString("IN_VOY_NBR")) + " / "
						+ CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				atb = rs.getTimestamp("ATB_DTTM");
				etb = rs.getTimestamp("ETB_DTTM");
				btr = rs.getTimestamp("BTR_DTTM");
			}

			if (atb != null) {
				varDttm = atb;
			} else if (etb != null) {
				varDttm = etb;
			} else if (btr != null) {
				varDttm = btr;
			} else {
				varDttm = new Timestamp(System.currentTimeMillis());
			}

			adminFeeVO.setAtbEtbBtr(CommonUtility.formatDateToStr(varDttm, this.dateFormat));

			adminFeeVO = prepareTariffInformation(adminFeeVO, ProcessChargeConst.TARIFF_MAIN_ADMIN,
					ProcessChargeConst.TARIFF_MAIN_MANIFEST_AMENDMENT, varDttm);
			adminFeeVO.setCreateUserId(userID);

		} catch (Exception e) {
			log.info("Exception captureMFAdminWaiverRequest : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** captureMFAdminWaiverRequest Result ***** adminFeeVO: " + adminFeeVO);
		}
		return adminFeeVO;
	}

	@Override
	public String insertMiscEvtLog(String type, String varno, String blno, String coCd) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String result = "";
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
			log.info("***** insertMiscEvtLog SQL *****" + SQL_INSERT_MISC_EVENT_LOG);
			log.info("params: " + paramMap.toString());
			int count1 = namedParameterJdbcTemplate.update(SQL_INSERT_MISC_EVENT_LOG, paramMap);

			if (count1 == 0) {
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			} else {
				result = misNo + "";
			}
		} catch (BusinessException e) {
			log.info("Exception insertMiscEvtLog : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception insertMiscEvtLog : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** insertMiscEvtLog ***** result:" + result);
		}
		return result;

	}

	private long getMisNo() throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		long nextValue = 0;
		Long nextMiscSeqNbr = null;
		try {
			log.info("START: getMisNo DAo");
			sql = "SELECT MISC_EVENT_LOG_SEQ_NBR.nextVal FROM DUAL";
			log.info("***** getMisNo SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				nextValue = rs.getLong("NEXTVAL");
				nextMiscSeqNbr = Long.valueOf(nextValue);
			}
		} catch (Exception e) {
			log.info("Exception nextMiscSeqNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getMisNo DAO ***** nextMiscSeqNbr:" + nextMiscSeqNbr.longValue());
		}
		return nextMiscSeqNbr.longValue();
	}

	public String MftInsertion(String distype, String addval, String coCd, String varno, String blno, String crgtyp,
			String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc, String mark, String nopkgs, String gwt,
			String gvol, String crgstat, String dgind, String stgind, String dop, String pkgtyp, String coname,
			String consigneeCoyCode, String poL, String poD, String poFD, String cntrtype, String cntrsize,
			String cntr1, String cntr2, String cntr3, String cntr4, String autParty, String crg_category,
			String deliveryToEPC, String UserID, String cargoSelected, String conAddr, String notifyParty,
			String notifyPartyAddr, String placeDel, String placeReceipt, String shipperNm, String shipperAdd,
			String customHsCode, List<HsCodeDetails> multiHsCodeList, String blNoRoot, boolean isSplitBl) throws BusinessException {
		String strInsert = "";
		StringBuffer strMark = new StringBuffer();
		StringBuffer strCntr1 = new StringBuffer();
		StringBuffer strCntr2 = new StringBuffer();
		StringBuffer strCntr3 = new StringBuffer();
		StringBuffer strCntr4 = new StringBuffer();
		String streturn = "";
		StringBuffer strInsertTxn = new StringBuffer();
		StringBuffer strMark_trans = new StringBuffer();
		StringBuffer strCntr1_trans = new StringBuffer();
		StringBuffer strCntr2_trans = new StringBuffer();
		StringBuffer strCntr3_trans = new StringBuffer();
		StringBuffer strCntr4_trans = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			//TODO test
			log.info("START: MftInsertion DAO addval:" + addval + "coCd:" + coCd + "varno:" + varno + "blno:" + blno
					+ "crgtyp:" + crgtyp + "hscd:" + hscd + "crgdesc:" + crgdesc + "mark:" + mark + "nopkgs:" + nopkgs
					+ "gwt:" + gwt + "gvol" + gvol + "crgstat:" + crgstat + "dgind:" + dgind + "stgind:" + stgind
					+ "dop" + dop + "pkgtyp:" + pkgtyp + "coname:" + coname + "poL:" + poL + "poD:" + poD + "poFD:"
					+ poFD + "cntrtype:" + cntrtype + "cntrsize:" + cntrsize + "cntr1:" + cntr1 + "cntr2:" + cntr2
					+ "cntr3:" + cntr3 + "cntr4:" + cntr4 + "autParty:" + autParty + "crg_category:" + crg_category
					+ "deliveryToEPC:" + deliveryToEPC + "UserID:" + UserID + "cargoSelected:" + cargoSelected
					+ ",conAddr:" + conAddr + ",notifyParty:" + notifyParty + ",notifyPartyAddr:" + notifyPartyAddr
					+ ",placeDel:" + placeDel + ",shipperNm:" + shipperNm + ",shipperAdd:" + shipperAdd + ",customHsCode:"
					+ customHsCode + ", multiHsCodeList : " + multiHsCodeList.toString()
					+ "blNoRoot:" + blNoRoot + ", isSplitBl:" + isSplitBl);

			boolean chkBlNo = chkBlNo(blno, varno);
			if (chkBlNo) {
				log.info("MftInsertion with Cargo_category - Invalid Bl No " + blno);
				throw new BusinessException("M20201");
			}
			boolean Pkgtyp = chkPkgtype(pkgtyp);
			if (!Pkgtyp) {
				log.info("MftInsertion with Cargo_category - Invalid Packaging type " + pkgtyp);
				throw new BusinessException("M21604");
			}

			boolean portcdl = chkPortCode(poL);
			if (!portcdl) {
				log.info("MftInsertion with Cargo_category - Invalid Port Code " + poL);
				throw new BusinessException("M21601");
			}
			boolean portcdd = chkPortCode(poD);
			if (!portcdd) {
				log.info("MftInsertion with Cargo_category - Invalid Port Code " + poD);
				throw new BusinessException("M21602");
			}

			boolean portcfd = chkPortCode(poFD);
			if (poFD != null && !poFD.equals("")) {
				if (!portcfd) {
					log.info("MftInsertion with Cargo_category - Invalid Final Destination Port Code " + poFD);
					throw new BusinessException("M21603");
				} // end of if !port..
			} // end of if poFD

			// To check for DG Indicator
			if (dgind.equalsIgnoreCase("Y")) {
				boolean chkDGInd = chkDGInd(blno, varno);
				if (!chkDGInd) {
					log.info("Writing from ManifestEJB.chkDGInd - Manifest creation not allowed ");
					throw new BusinessException("M20211");
				}
			}
			
			Map<String,String> mapErrorLength = this.checkLegthValidation(crgdesc, mark, conAddr, notifyParty, notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd);
			if(mapErrorLength.size() > 0) {
				String[] tmpString = new String[mapErrorLength.size()];
				int count = 0;
				for ( Entry<String, String> entry : mapErrorLength.entrySet()) {
				    String key = entry.getKey();
				    tmpString[count]= key;
				    count++;
				}
				String errorMessage = CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_invalidLength,tmpString);
				throw new BusinessException(errorMessage);
			}

			// Added by MCconsulting , deliveryToEPC set to 'N' if deliveryToEPC is null or
			// empty.
			deliveryToEPC = (deliveryToEPC == null || deliveryToEPC.equalsIgnoreCase("null")
					|| deliveryToEPC.trim().length() == 0) ? "N" : deliveryToEPC;

			String sblno = "";
			String sups = "";
			if (addval.equals("AddOl")) {
				sblno = "" + getOlBlNo();
				sups = "O";
			} else {
				sblno = blno;
			}
			int mftseqno = getMftNo();
			
			int splitId = 0;
			if(isSplitBl) {
				splitId = getMaxSplitId(blNoRoot);
			}
			
			sb.setLength(0);
			sb.append("INSERT INTO MANIFEST_DETAILS (");
			sb.append("mft_seq_nbr, var_nbr, bl_nbr, bl_status, crg_type, ");
			sb.append("cntr_size, cntr_type, stg_type, crg_status, pkg_type, ");
			sb.append("nbr_pkgs, edo_nbr_pkgs, crg_des, gross_wt, gross_vol, ");
			sb.append("dg_ind, hs_code, hs_sub_code_fr, hs_sub_code_to, ld_port, ");
			sb.append("dis_port, des_port, last_modify_user_id, last_modify_dttm, cons_nm, ");
			sb.append("cons_co_cd, supp_status, nbr_pkgs_in_port, dis_type, manifest_create_cd, ");
			sb.append("cargo_category_cd, epc_ind, ");
			sb.append("CONSIGNEE_ADDR, SHIPPER_NM, SHIPPER_ADDR,  ");
			sb.append("NOTIFY_PARTY, NOTIFY_PARTY_ADDR, PLACE_OF_DELIVERY,  ");
			sb.append("PLACE_OF_RECEIPT, CUSTOM_HS_CODE ");
			// if isSplitBl
			if(isSplitBl) {
				sb.append(", SPLIT_MAIN_BL, SPLIT_ID ");
			}
			sb.append(") VALUES (");
			sb.append("  :mftseqno");
			sb.append(", :varno");
			sb.append(", :sblno");
			sb.append(", 'A'");
			sb.append(", :crgtyp");
			sb.append(", :cntrsize");
			sb.append(", :cntrtype");
			sb.append(", :stgind");
			sb.append(", :crgstat");
			sb.append(", :pkgtyp");
			sb.append(",  :nopkgs");
			sb.append(", '0' ");
			sb.append(", :crgdesc");
			sb.append(", :gwt ");
			sb.append(", :gvol ");
			sb.append(", :dgind");
			sb.append(", :hscd");
			sb.append(", :hsSubCodeFr");
			sb.append(", :hsSubCodeTo");
			sb.append(", :poL");
			sb.append(", :poD");
			sb.append(", :poFD");
			sb.append(", :coCd");
			sb.append(", SYSDATE ");
			sb.append(", :coname");
			sb.append(", :consigneeCoyCode");
			sb.append(", :sups");
			sb.append(", '0' ");
			sb.append(", :distype");
			sb.append(", :autParty");
			sb.append(", :crg_category");
			sb.append(", :deliveryToEPC,"); // MCC Add EPC_IND
			// STAR CR FTZ HSCODE - NS JULY 2024
			sb.append(":conAddr, :shipperNm, :shipperAdd, :notifyParty, :notifyPartyAddr, :placeDel, ");
			sb.append(":placeReceipt, :customHsCode ");
			// END CR FTZ HSCODE - NS JULY 2024
			if(isSplitBl) {
				sb.append(", :blNoRoot, :splitId ");
				paramMap.put("blNoRoot", blNoRoot);	
				paramMap.put("splitId", String.valueOf(splitId));	
			}
			sb.append(") ");
			strInsert = sb.toString();
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				strInsertTxn
						.append(" INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,VAR_NBR,BL_NBR,CRG_TYPE, ");
				strInsertTxn.append(" CNTR_SIZE,CNTR_TYPE,STG_TYPE,CRG_STATUS,PKG_TYPE,NBR_PKGS,CRG_DES, ");
				strInsertTxn.append(" GROSS_WT,GROSS_VOL,DG_IND,HS_CODE,LD_PORT,DIS_PORT,DES_PORT,  ");
				strInsertTxn.append(
						" LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,CONS_NM,SUPP_STATUS, DIS_TYPE,MANIFEST_CREATE_CD, CARGO_CATEGORY_CD, ");
				strInsertTxn.append("CONSIGNEE_ADDR, SHIPPER_NM, SHIPPER_ADDR,  ");
				strInsertTxn.append("NOTIFY_PARTY, NOTIFY_PARTY_ADDR, PLACE_OF_DELIVERY,  ");
				strInsertTxn.append("PLACE_OF_RECEIPT, CUSTOM_HS_CODE ");
				// if isSplitBl
				if(isSplitBl) {
					strInsertTxn.append(", SPLIT_MAIN_BL, SPLIT_ID ");
				}
				strInsertTxn.append(")");
				strInsertTxn.append(
						" VALUES('0',:mftseqno,:varno,:sblno,:crgtyp,:cntrsize,:cntrtype,:stgind,:crgstat,:pkgtyp,:nopkgs,:crgdesc, ");
				strInsertTxn.append(
						" :gwt,:gvol,:dgind,:hscd,:poL,:poD,:poFD,:coCd,sysdate,:coname,:sups,:distype,:autParty,:crg_category, ");
				// START CR FTZ HSCODE - NS JULY 2024
				strInsertTxn.append(":conAddr, :shipperNm, :shipperAdd, :notifyParty, :notifyPartyAddr, :placeDel, ");
				strInsertTxn.append(":placeReceipt, :customHsCode  ");
				// END CR FTZ HSCODE - NS JULY 2024
				if(isSplitBl) {
					strInsertTxn.append(", :blNoRoot, :splitId ");
					paramMap.put("blNoRoot", blNoRoot);	
					paramMap.put("splitId", String.valueOf(splitId));	
				}
				strInsertTxn.append(")");
				
				strMark_trans.append(
						" INSERT INTO MFT_MARKINGS_TRANS(TRANS_NBR,MFT_SQ_NBR,MFT_MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) VALUES('0', ");
				strMark_trans.append(" :mftseqno,:mark,:coCd,SYSDATE) ");
			}
			strMark.append(
					" INSERT INTO MFT_MARKINGS(MFT_SQ_NBR,MFT_MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) VALUES( ");
			strMark.append(" :mftseqno,:mark,:coCd,SYSDATE) ");
			paramMap.put("mftseqno", mftseqno);
			paramMap.put("varno", varno);
			paramMap.put("sblno", sblno.trim());
			paramMap.put("crgtyp", crgtyp);
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
			paramMap.put("hsSubCodeFr", hsSubCodeFr);
			paramMap.put("hsSubCodeTo", hsSubCodeTo);
			paramMap.put("poL", poL);
			paramMap.put("poD", poD);
			paramMap.put("poFD", CommonUtility.deNull(poFD));
			paramMap.put("coCd", coCd);
			paramMap.put("coname", GbmsCommonUtility.addApostr(coname));
			paramMap.put("consigneeCoyCode", consigneeCoyCode);
			paramMap.put("sups", sups);
			paramMap.put("distype", CommonUtility.deNull(distype));
			paramMap.put("autParty", CommonUtility.deNull(autParty));
			paramMap.put("crg_category", crg_category);
			paramMap.put("deliveryToEPC", CommonUtility.deNull(deliveryToEPC));
			// START CR FTZ HSCODE - NS JULY 2024
			paramMap.put("conAddr", CommonUtility.deNull(conAddr));
			paramMap.put("notifyParty", CommonUtility.deNull(notifyParty));
			paramMap.put("notifyPartyAddr", CommonUtility.deNull(notifyPartyAddr));
			paramMap.put("placeDel", CommonUtility.deNull(placeDel));
			paramMap.put("placeReceipt", CommonUtility.deNull(placeReceipt));
			paramMap.put("shipperNm", CommonUtility.deNull(shipperNm));
			paramMap.put("shipperAdd", CommonUtility.deNull(shipperAdd));
			paramMap.put("customHsCode", CommonUtility.deNull(customHsCode));
			// END CR FTZ HSCODE - NS JULY 2024
			log.info("***** MftInsertion SQL *****" + strInsert.toString());
			log.info("params: " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(strInsert.toString(), paramMap);
			// Added By NS on 25-09-20
			if (count != 0) {
				sb.setLength(0);
				sb.append(" INSERT INTO GBMS.MANIFEST_DETAILS_EXT  ");
				sb.append(" (MFT_SEQ_NBR,HS_SUB_DESC_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				sb.append(" VALUES(:MFT_SEQ_NBR,:HS_SUB_DESC_CD,:userId,SYSDATE) ");
				paramMap.put("MFT_SEQ_NBR", mftseqno);
				paramMap.put("HS_SUB_DESC_CD", cargoSelected);
				paramMap.put("userId", UserID);
				log.info("***** MftInsertion SQL *****" + sb.toString());
				log.info("params: " + paramMap.toString());
				namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				
				// START CR FTZ HSCODE - NS JULY 2024
				for (HsCodeDetails hsCodeObj : multiHsCodeList) {
					
					// get MFT_HSCODE_SEQ_NBR 
					StringBuilder sbSeq = new StringBuilder();
					sbSeq.append("SELECT GBMS.SEQ_MFT_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
					Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
					BigDecimal seqValue = (BigDecimal) results.get("seqVal");
					log.info("seqValue : " + seqValue);
					// end
					
					
					sb.setLength(0);
					sb.append(" INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS  ");
					sb.append(" (MFT_SEQ_NBR,MFT_HSCODE_SEQ_NBR,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
					sb.append(" VALUES(:MFT_SEQ_NBR,:MFT_HSCODE_SEQ_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
					paramMap.put("MFT_SEQ_NBR", mftseqno);
					paramMap.put("MFT_HSCODE_SEQ_NBR", seqValue);
					paramMap.put("HS_CODE",hsCodeObj.getHsCode());
					paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
					paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
					paramMap.put("NBR_PKGS",hsCodeObj.getNbrPkgs());
					paramMap.put("GROSS_WT",hsCodeObj.getGrossWt());
					paramMap.put("GROSS_VOL",hsCodeObj.getGrossVol());
					paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
					paramMap.put("CRG_DES",hsCodeObj.getCrgDes());	
					paramMap.put("HS_SUB_CODE_DESC",hsCodeObj.getHsSubCodeDesc());	
					paramMap.put("userId", UserID);
					log.info("SQL" + sb.toString());
					log.info("params: " + paramMap.toString());
					int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
					log.info("counths : " + counths);
					
					sb.setLength(0);
					sb.append(" INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS  ");
					sb.append(" (MFT_SEQ_NBR,MFT_HSCODE_SEQ_NBR,AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
					sb.append(" VALUES(:MFT_SEQ_NBR,:MFT_HSCODE_SEQ_NBR,SYSDATE,'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
					paramMap.put("MFT_SEQ_NBR", mftseqno);
					paramMap.put("MFT_HSCODE_SEQ_NBR", seqValue);
					paramMap.put("HS_CODE",hsCodeObj.getHsCode());
					paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
					paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
					paramMap.put("NBR_PKGS",hsCodeObj.getNbrPkgs());
					paramMap.put("GROSS_WT",hsCodeObj.getGrossWt());
					paramMap.put("GROSS_VOL",hsCodeObj.getGrossVol());
					paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
					paramMap.put("CRG_DES",hsCodeObj.getCrgDes());	
					paramMap.put("HS_SUB_CODE_DESC",hsCodeObj.getHsSubCodeDesc());	
					paramMap.put("userId", UserID);
					log.info("SQL" + sb.toString());
					log.info("params: " + paramMap.toString());
					int counthsAudit = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
					log.info("counths : " + counthsAudit);
				}
				// END CR FTZ HSCODE - NS JULY 2024
				
			}

			paramMap.put("mftseqno", mftseqno);
			paramMap.put("mark", mark);
			paramMap.put("coCd", coCd);
			log.info("***** MftInsertion SQL *****" + strMark.toString());
			log.info("params: " + paramMap.toString());
			int cntmark = namedParameterJdbcTemplate.update(strMark.toString(), paramMap);
			int count_trans = 0;
			int cnt_mark_trans = 0;
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				paramMap.put("mftseqno", mftseqno);
				paramMap.put("varno", varno);
				paramMap.put("sblno", sblno.trim());
				paramMap.put("crgtyp", crgtyp);
				paramMap.put("cntrsize", cntrsize);
				paramMap.put("cntrtype", cntrtype);
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
				paramMap.put("coCd", coCd);
				paramMap.put("coname", GbmsCommonUtility.addApostr(coname));
				paramMap.put("sups", sups);
				paramMap.put("distype", CommonUtility.deNull(distype));
				paramMap.put("autParty", CommonUtility.deNull(autParty));
				paramMap.put("crg_category", crg_category);
				log.info("***** MftInsertion SQL *****" + strInsertTxn.toString());
				log.info("params: " + paramMap.toString());
				count_trans = namedParameterJdbcTemplate.update(strInsertTxn.toString(), paramMap);
				paramMap.put("mftseqno", mftseqno);
				paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
				paramMap.put("coCd", coCd);
				cnt_mark_trans=namedParameterJdbcTemplate.update(strMark_trans.toString(), paramMap);
				strCntr1_trans.append(
						" INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES('0',1, ");
				strCntr1_trans.append(" :mftseqno , :cntr1) ");
				strCntr2_trans.append(
						" INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES('0',2, ");
				strCntr2_trans.append(" :mftseqno , :cntr2) ");
				strCntr3_trans.append(
						" INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES('0',3, ");
				strCntr3_trans.append(" :mftseqno , :cntr3) ");
				strCntr4_trans.append(
						" INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES('0',4, ");
				strCntr4_trans.append(" :mftseqno , :cntr4) ");
			}
			strCntr1.append(
					" INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(1,:mftseqno,:cntr1) ");
			strCntr2.append(
					" INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(2,:mftseqno,:cntr2) ");
			strCntr3.append(
					" INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(3,:mftseqno,:cntr3) ");
			strCntr4.append(
					" INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(4,:mftseqno,:cntr4) ");

			if (cntrtype != null && !cntrtype.equals("") && cntrsize != null && !cntrsize.equals("")) {
				if (cntr1 != null && !cntr1.equals("")) {
					// int cntcntr1 = sqlstmt.executeUpdate(strCntr1);
					paramMap.put("mftseqno", mftseqno);
					paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
					namedParameterJdbcTemplate.update(strCntr1.toString(), paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
						paramMap.put("mftseqno", mftseqno);
						paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
						log.info("***** MftInsertion SQL *****" + strCntr1_trans.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(strCntr1_trans.toString(), paramMap);
					}
				}
				if (cntr2 != null && !cntr2.equals("")) {
					paramMap.put("mftseqno", mftseqno);
					paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
					log.info("SQL" + strCntr1.toString());
					namedParameterJdbcTemplate.update(strCntr1.toString(), paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
						paramMap.put("mftseqno", mftseqno);
						paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
						log.info("***** MftInsertion SQL *****" + strCntr2_trans.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(strCntr2_trans.toString(), paramMap);
					}
				}

				if (cntr3 != null && !cntr3.equals("")) {
					paramMap.put("mftseqno", mftseqno);
					paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
					log.info("SQL" + strCntr3.toString());
					namedParameterJdbcTemplate.update(strCntr3.toString(), paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
						paramMap.put("mftseqno", mftseqno);
						paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
						log.info("***** MftInsertion SQL *****" + strCntr3_trans.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(strCntr3_trans.toString(), paramMap);
					}
				}

				if (cntr4 != null && !cntr4.equals("")) {
					paramMap.put("mftseqno", mftseqno);
					paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
					log.info("SQL" + strCntr4.toString());
					namedParameterJdbcTemplate.update(strCntr4.toString(), paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
						paramMap.put("mftseqno", mftseqno);
						paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
						log.info("***** MftInsertion SQL *****" + strCntr4_trans.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(strCntr4_trans.toString(), paramMap);
					}
				}

				// if cntrtype cntrsize
				if (count == 0 || cntmark == 0) {
					throw new BusinessException("M4201");
				}
				if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
					if (count_trans == 0 || cnt_mark_trans == 0) {
						throw new BusinessException("M4201");
					}
				}
			}
			streturn = mftseqno + "-" + sblno;
		} catch (BusinessException e) {
			log.info("Exception MftInsertion : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception MftInsertion : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception MftInsertion : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** MftInsertion DAO ***** streturn:" + streturn);
		}
		return streturn;
	}

	@Override
	public String getPortName(String portcd) throws BusinessException {
		String sql = "";
		String portnm = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START:getPortName portcd:" + portcd);
			sql = "SELECT PORT_NM FROM UN_PORT_CODE WHERE PORT_CD=:portcd";
			paramMap.put("portcd", portcd);
			log.info("***** getPortName SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				portnm = rs.getString("PORT_NM");
			}

			log.info("portnm" + portnm);
		} catch (Exception e) {
			log.info("Exception portcd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getPortName Result ***** portcd:" + portcd);
		}
		return portnm;

	}

	@Override
	public String getPkgName(String pkgtype) throws BusinessException {
		String sql = "";
		String pkgname = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getPkgName pkgtype:" + pkgtype);
			sql = "SELECT PKG_DESC FROM PKG_TYPES WHERE PKG_TYPE_CD=:pkgtype";
			paramMap.put("pkgtype", pkgtype);
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				pkgname = rs.getString("PKG_DESC");
			}

			log.info("pkgtype" + pkgtype);
		} catch (Exception e) {
			log.info("Exception getPkgName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPkgName pkgtype:" + pkgtype);
		}
		return pkgname;
	}

	@Override
	public boolean chkEdonbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean bvslind = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkEdonbrPkgs seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT EDO_NBR_PKGS FROM MANIFEST_DETAILS WHERE EDO_NBR_PKGS>0 AND MFT_SEQ_NBR=:seqno  ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("***** chkEdonbrPkgs SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				int edoNbrPkgs = rs.getInt("EDO_NBR_PKGS");
				if (edoNbrPkgs == 0) {
					bvslind = false;
				} else {
					bvslind = true;
				}
			}

		} catch (Exception e) {
			log.info("Exception chkEdonbrPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** chkEdonbrPkgs Result ***** bvslind:" + bvslind);
		}
		return bvslind;
	}

	@Override
	public boolean chkNbrEdopkgs(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean bnbrpkgs = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkNbrEdopkgs seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT NBR_PKGS,EDO_NBR_PKGS FROM MANIFEST_DETAILS WHERE MFT_SEQ_NBR=:seqno  ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("***** chkEdonbrPkgs SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				if ((rs.getInt("NBR_PKGS")) > (rs.getInt("EDO_NBR_PKGS")))
					bnbrpkgs = true;
				else
					bnbrpkgs = false;
			}

		} catch (Exception e) {
			log.info("Exception chkNbrEdopkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** chkNbrEdopkgs Result ***** bnbrpkgs:" + bnbrpkgs);
		}
		return bnbrpkgs;
	}

	@Override
	public boolean chkDNnbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean dnexist = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkDNnbrPkgs seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT DN_NBR_PKGS FROM GB_EDO WHERE DN_NBR_PKGS>0 AND MFT_SEQ_NBR=:seqno  ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("***** chkDNnbrPkgs SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				dnexist = true;
			} else {
				dnexist = false;
			}
		} catch (Exception e) {
			log.info("Exception chkDNnbrPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** chkDNnbrPkgs ***** dnsexist:" + dnexist);
		}
		return dnexist;
	}

	@Override
	public boolean chkTnbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean texist = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkTnbrPkgs seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT TRANS_NBR_PKGS FROM GB_EDO WHERE TRANS_NBR_PKGS>0 AND MFT_SEQ_NBR=:seqno  ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("***** chkTnbrPkgs SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				texist = true;
			} else {
				texist = false;
			}

		} catch (Exception e) {
			log.info("Exception chkTnbrPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** chkTnbrPkgs Result ***** texist:" + texist);
		}
		return texist;
	}

	@Override
	public boolean chkTDNnbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean chkTDNnbrPkgs = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkTDNnbrPkgs seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT TRANS_DN_NBR_PKGS FROM GB_EDO WHERE TRANS_DN_NBR_PKGS>0 AND MFT_SEQ_NBR=:seqno  ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("***** chkTDNnbrPkgs SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				chkTDNnbrPkgs = true;
			} else {
				chkTDNnbrPkgs = false;
			}

		} catch (Exception e) {
			log.info("Exception chkTDNnbrPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** chkTDNnbrPkgs **** chkTDNnbrPkgs:" + chkTDNnbrPkgs);
		}
		return chkTDNnbrPkgs;
	}

	@Override
	public String getClBjInd(String seqnbr) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		String clbjind = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info(" START: getClBjInd seqbnr:" + seqnbr);
			sql.append(" SELECT GB_CLOSE_BJ_IND FROM MANIFEST_DETAILS WHERE MFT_SEQ_NBR=:seqnbr  ");
			paramMap.put("seqnbr", seqnbr);
			log.info("***** getClBjInd SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				clbjind = rs.getString("GB_CLOSE_BJ_IND");
			}

		} catch (Exception e) {
			log.info("Exception getClBjInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getClBjInd ***** clbjind:" + clbjind);
		}
		return clbjind;
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
		Map<String, Object> paramMap = new HashMap<String, Object>();
		ManifestValueObject mftdispobj = new ManifestValueObject();
		try {
			log.info("START: mftRetrieve blno:" + blno + "varno:" + varno + "seqno:" + seqno);
			sql.append(
					" SELECT * FROM MANIFEST_DETAILS mft,cc_unstuff_manifest unstf, HS_SUB_CODE hs WHERE unstf.active_status(+)='A' and unstf.unstuff_seq_nbr(+)=mft.unstuff_seq_nbr and HS.HS_CODE(+)= mft.HS_CODE AND HS.HS_SUB_CODE_FR(+)= mft.HS_SUB_CODE_FR AND HS.HS_SUB_CODE_TO(+)= mft.HS_SUB_CODE_TO and BL_NBR=:blno ");
			sql.append(" AND mft.VAR_NBR=:varno  AND MFT_SEQ_NBR=:seqno ");
			paramMap.put("blno", blno);
			paramMap.put("varno", varno);
			paramMap.put("seqno", seqno);
			log.info("***** mftRetrieve SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
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
				mftdispobj.setScheme(getSchemeName(varno));
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				mftdispobj.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				mftdispobj.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				mftdispobj.setHsSubCodeDesc(CommonUtility.deNull(rs.getString("HS_SUB_DESC")));
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END
				// MCC set EPC_IND
				if(rs.getString("EPC_IND") != null) {
					mftdispobj.setDeliveryToEPC(CommonUtility.deNull(rs.getString("EPC_IND")));
				}
				else {
					mftdispobj.setDeliveryToEPC("N");
				}

				// START CR FTZ HSCODE - NS JULY 2024
				mftdispobj.setCategory(CommonUtility.deNull(rs.getString("CARGO_CATEGORY_CD")));
				mftdispobj.setCloseBJInd(CommonUtility.deNull(rs.getString("GB_CLOSE_BJ_IND")));
				mftdispobj.setCustomHsCode(rs.getString("CUSTOM_HS_CODE"));
				mftdispobj.setConsigneeAddr(rs.getString("CONSIGNEE_ADDR"));
				mftdispobj.setShipperNm(rs.getString("SHIPPER_NM"));
				mftdispobj.setShipperAddr(rs.getString("SHIPPER_ADDR"));
				mftdispobj.setNotifyParty(rs.getString("NOTIFY_PARTY"));
				mftdispobj.setNotifyPartyAddr(rs.getString("NOTIFY_PARTY_ADDR"));
				mftdispobj.setPlaceOfDelivery(rs.getString("PLACE_OF_DELIVERY"));
				mftdispobj.setPlaceOfReceipt(rs.getString("PLACE_OF_RECEIPT"));
				// END  CR FTZ HSCODE - NS JULY 2024
			}

			sqlmark.append(" SELECT * FROM MFT_MARKINGS WHERE MFT_SQ_NBR =:mftseqno  ");
			sqlcntr.append(" SELECT * FROM BL_CNTR_DETAILS WHERE MFT_SEQ_NBR=:mftseqno ");

			if (mftdispobj.getConsigneeCoyCode() != null && !"OTHERS".equalsIgnoreCase(mftdispobj.getConsigneeCoyCode())
					&& !"".equalsIgnoreCase(mftdispobj.getConsigneeCoyCode())) {
				String sqlConsName = "SELECT co_nm FROM company_code WHERE co_cd=:consigneeCoyCode";
				paramMap.put("consigneeCoyCode", mftdispobj.getConsigneeCoyCode());
				log.info("***** mftRetrieve SQL *****" + sqlConsName.toString());
				log.info("params: " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sqlConsName, paramMap);
				if (rs.next()) {
					mftdispobj.setConsignee(CommonUtility.deNull(rs.getString("co_nm")));
				}
				rs = null;
			}

			paramMap.put("mftseqno", mftseqno);
			log.info("***** mftRetrieve SQL *****" + sqlmark.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlmark.toString(), paramMap);
			if (rs.next()) {
				mftdispobj.setCrgMarking(CommonUtility.deNull(rs.getString("MFT_MARKINGS")));
			}
			paramMap.put("mftseqno", mftseqno);
			log.info("***** mftRetrieve SQL *****" + sqlcntr.toString());
			log.info("params: " + paramMap.toString());
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
			StringBuilder hsSubDescCd = new StringBuilder(); //Not found in old
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
			log.info("***** mftRetrieve SQL *****" + hsSubDescCd.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(hsSubDescCd.toString(), paramMap);
			if (rs.next()) {
				mftdispobj.setSelectedCargo((CommonUtility.deNull(rs.getString("HS_SUB_DESC_CD"))));
				mftdispobj.setSelectedCargoValue((CommonUtility.deNull(rs.getString("MISC_TYPE_NM"))));
			}
			// end
		} catch (Exception e) {
			log.info("Exception mftRetrieve : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** mftRetrieve Result ****** mftdispobj " + mftdispobj);
		}
		return mftdispobj;
	}

	private String getCrgNm(String crgtyp) throws BusinessException {
		String sql = "";
		String crgnm = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: DAO getCrgNm crgtyp:" + crgtyp);
			sql = "SELECT CRG_TYPE_NM FROM CRG_TYPE WHERE CRG_TYPE_CD=:crgtyp";
			paramMap.put("crgtyp", crgtyp);
			log.info("***** getCrgNm SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				crgnm = rs.getString("CRG_TYPE_NM");
			}

		} catch (Exception e) {
			log.info("Exception getCrgNm : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getCrgNm ***** crgnm:" + crgnm);
		}
		return crgnm;

	}

	@Override
	public List<ManifestValueObject> getAddcrgList() throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		List<ManifestValueObject> addcrglist = new ArrayList<ManifestValueObject>();
		try {
			log.info("START:  getAddcrgList ");
			// TVS - Added 02 and 03 codes in where clause-24-09-2003
			sql = "SELECT DISTINCT CRG_TYPE_CD,CRG_TYPE_NM FROM CRG_TYPE WHERE CRG_TYPE_CD NOT IN ('00','01','02','03') and REC_STATUS='A'";
			// added "and REC_STATUS='A'" by vani -- 8th Oct,03
			log.info("***** getAddcrgList SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			String crgCd = "";
			String crgName = "";

			while (rs.next()) {
				crgCd = CommonUtility.deNull(rs.getString("CRG_TYPE_CD"));
				crgName = CommonUtility.deNull(rs.getString("CRG_TYPE_NM"));

				ManifestValueObject admftvobj = new ManifestValueObject();
				admftvobj.setCrgType(crgCd);
				admftvobj.setCrgDesc(crgName);
				addcrglist.add(admftvobj);
			}

		} catch (Exception e) {
			log.info("Exception getAddcrgList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getAddcrgList Result: addcrglist: " + addcrglist);
		}
		return addcrglist;
	}

	@Override
	public List<ManifestValueObject> getHSCodeList(String status, String query) throws BusinessException {
		String sql = "";
		List<ManifestValueObject> hsCodeList = new ArrayList<ManifestValueObject>();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		sql = "";
		String hsCode = "";
		try {
			log.info("START: getHSCodeList DAO END status:" + status);
			ManifestValueObject mvObj = null;
			sb.append("SELECT HS_CODE FROM HS_CODE WHERE REC_STATUS =:status");
			if (!query.isEmpty()) {
				sb.append(" AND HS_CODE LIKE :hs_code ");
			}
			sb.append(" ORDER BY HS_CODE ");
			paramMap.put("status", status);
			paramMap.put("hs_code", "%" + query + "%");
			sql = sb.toString();
			log.info("***** getHSCodeList SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				hsCode = CommonUtility.deNull(rs.getString("HS_CODE"));
				mvObj = new ManifestValueObject();
				mvObj.setHsCode(hsCode);
				hsCodeList.add(mvObj);
			}
		} catch (Exception e) {
			log.info("Exception getHSCodeList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getHSCodeList **** hsCodeList: " + hsCodeList);
		}
		return hsCodeList;

	}

	@Override
	public String getVesselTypeByVslNm(String vslNm) throws BusinessException {
		String vslType = "";
		String sql = "select Distinct C.VSL_TYPE_CD from TOPS.vessel_call a ,TOPS.vessel c where  c.vsl_nm = A.vsl_nm and a.vsl_nm=:vslNm";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: DAO getVesselTypeByVslNm vslNm:" + vslNm);
			paramMap.put("vslNm", vslNm);
			log.info("***** getVesselTypeByVslNm SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vslType = rs.getString(1);
			}
		} catch (Exception e) {
			log.info("Exception getVesselTypeByVslNm : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:DAO getVesselTypeByVslNm vslType:" + vslType);
		}
		return vslType;
	}

	@Override
	public boolean getUserAdminVessel(String coCd, String vesselNameLogin, String voyLogin, String blnum) throws BusinessException {
		boolean result = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getUserAdminVessel DAO coCd" + coCd + "vesselNameLogin:" + vesselNameLogin + "voyLogin:"
					+ voyLogin + "blnum:" + blnum);

			sb = new StringBuilder();
			sb.append(" SELECT DISTINCT CUST_CD FROM  LOGON_ACCT WHERE CUST_CD  IN ( ");
			sb.append(" SELECT b.MANIFEST_CREATE_CD FROM VESSEL_CALL a, MANIFEST_DETAILS b ");
			sb.append(
					" WHERE a.VV_CD = b.VAR_NBR  and a.VSL_NM =:vesselNameLogin and a.IN_VOY_NBR =:voyLogin and b.BL_NBR =:blnum ) ");

			paramMap.put("vesselNameLogin", vesselNameLogin);
			paramMap.put("voyLogin", voyLogin);
			paramMap.put("blnum", blnum);
			log.info("***** getUserAdminVessel SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				String custCD = rs.getString("CUST_CD");
				if (custCD.equals(coCd)) {
					result = true;
					break;
				}
			}

		} catch (Exception e) {
			log.info("Exception getUserAdminVessel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUserAdminVessel DAO ");
		}
		
		log.info("END: getUSerAdminVessel Result ***** result: " + result ) ;
		return result;
	}

	@Override
	public List<AccessCompanyValueObject> getAutPartyListOfVessel(String vvcode) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		String coCd = "";
		String coNm = "";
		List<AccessCompanyValueObject> autPartyList = new ArrayList<AccessCompanyValueObject>();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String coCdsCall = "";
		String coCdsDec = "";
		try { //diff sql,  method
			log.info("START: getAutPartyListOfVessel DAO vvcode" + vvcode);
			sql.append(" select distinct cc.co_cd, cc.co_nm from company_code cc");
			sql.append(" where cc.co_cd in (select vc.create_cust_cd from vessel_call vc where vc.vv_cd=:vvcode )");
			sql.append(" or cc.co_cd in (select vd.cust_cd from vessel_declarant vd where vd.vv_cd=:vvcode");
			sql.append(" and vd.status='A') order by cc.co_nm"); 
			paramMap.put("vvcode", vvcode);
			log.info("***** getAutPartyListOfVessel SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				AccessCompanyValueObject comvalobj = new AccessCompanyValueObject();
				coCd = CommonUtility.deNull(rs.getString("CO_CD"));
				coNm = CommonUtility.deNull(rs.getString("CO_NM"));
				comvalobj.setCompanyCode(coCd);
				comvalobj.setCompanyName(coNm);
				autPartyList.add(comvalobj);
			} 
			//end method in old
			sql.setLength(0);
			sql.append("  select vc.create_cust_cd from vessel_call vc where vc.vv_cd=:vvCode ");
			paramMap.put("vvCode", vvcode);
			log.info("***** getAutPartyListOfVessel SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			String coCdsCallLst = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramMap, String.class);
			log.info(" getStevedoreList SQL: " + sql + "paramMap" + paramMap.toString() + "coCdLst:" + coCdsCallLst);
			if (coCdsCallLst != null && !coCdsCallLst.equalsIgnoreCase("null")) {
				String coCdLstList[] = coCdsCallLst.split(",");

				for (int i = 0; i < coCdLstList.length; i++) {

					String stev_co_cd_t = coCdLstList[i];

					if (coCdLstList.length - 1 != i) {

						coCdsCall += "'" + stev_co_cd_t + "',";
					} else {
						coCdsCall += "'" + stev_co_cd_t + "'";
					}

				}
			}
			sql.setLength(0);

			sql = new StringBuilder();
			sql.append(" select count(*) from vessel_declarant vd where vd.vv_cd=:vvCode and vd.status='A' ");
			log.info("***** getAutPartyListOfVessel SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			int count = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class);

			sql = new StringBuilder();
			sql.append(" select vd.cust_cd from vessel_declarant vd where vd.vv_cd=:vvCode and vd.status='A' ");
			String coCdsDecLst = "";
			if (count > 0) {
				try {
					log.info("***** getAutPartyListOfVessel SQL *****" + sql.toString());
					log.info("params: " + paramMap.toString());
					coCdsDecLst = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramMap, String.class);
				} catch (Exception e) {
					log.info("Exception getAutPartyListOfVessel : ", e);
				}
			}
			log.info(" getStevedoreList SQL: " + sql + "paramMap" + paramMap.toString() + "coCdLst:" + coCdsCallLst);
			if (coCdsDecLst != null && !coCdsDecLst.equalsIgnoreCase("null")) {
				String coCdDecList[] = coCdsDecLst.split(",");

				for (int i = 0; i < coCdDecList.length; i++) {

					String stev_co_cd_t = coCdDecList[i];

					if (coCdDecList.length - 1 != i) {

						coCdsDec += "'" + stev_co_cd_t + "',";
					} else {
						coCdsDec += "'" + stev_co_cd_t + "'";
					}

				}
			}
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("coCdsCall", Arrays.asList(coCdsCall.replaceAll("'", "").split(",")));
			parameters.addValue("coCdsDec", Arrays.asList(coCdsDec.replaceAll("'", "").split(",")));
			sql.setLength(0);
			sql.append(" select distinct cc.co_cd AS companyCode, cc.co_nm AS companyName from company_code cc ");
			sql.append(" where cc.co_cd in(:coCdsCall) or cc.co_cd in (:coCdsDec) ");
			log.info("***** getAutPartyListOfVessel SQL *****" + sql.toString());
			log.info("params: " + parameters.getValues().toString());
			autPartyList = namedParameterJdbcTemplate.query(sql.toString(), parameters,
					new BeanPropertyRowMapper<AccessCompanyValueObject>(AccessCompanyValueObject.class));
			log.info("END: *** getAutPartyListOfVessel **** autPartyList: " + autPartyList.toString());
		} catch (Exception e) {
			log.info("Exception getAutPartyListOfVessel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getAutPartyListOfVessel **** ");
		}
		return autPartyList;
	}

	@Override
	public List<AccountValueObject> getListAmendmentChargedTo(String varno) throws BusinessException {
		List<AccountValueObject> result = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: DAO getListAmendmentChargedTo varno:" + varno);
			result = new ArrayList<AccountValueObject>();
			sb.append(" SELECT acct_nbr,cust_cd FROM company_code, cust_acct, vessel_call WHERE cust_cd = co_cd ");
			sb.append(
					" AND acct_status_cd = 'A' AND business_type LIKE '%G%' AND trial_ind = 'N' AND acct_nbr IS NOT NULL ");
			sb.append(" AND co_cd = create_cust_cd  AND vv_cd =:varno ");
			paramMap.put("varno", varno);
			log.info("***** getListAmendmentChargedTo SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			// Map map = null;
			String acct_nbr_main = "";

			while (rs.next()) {
				// map = new HashMap();
				// String userName = rs.getString("USER_NM");
				// map.put("userName", userName);
				// result.add(map);
				AccountValueObject oj = new AccountValueObject();

				String acct_nbr = CommonUtility.deNull(rs.getString("acct_nbr"));
				String cust_cd = CommonUtility.deNull(rs.getString("cust_cd"));

				oj.setAccountNumber(acct_nbr);
				oj.setCustomerCode(cust_cd);

				acct_nbr_main = acct_nbr;
				if (acct_nbr != null && cust_cd != null)
					result.add(oj);
			}

			sb.setLength(0);
			sb.append(" SELECT DISTINCT ca.acct_nbr, ca.cust_cd FROM cust_acct ca, company_code cc ");
			sb.append(" WHERE ca.business_type LIKE '%G%' AND ca.acct_nbr IS NOT NULL ");
			sb.append(" AND ca.acct_status_cd='A' AND ca.trial_ind = 'N' ");
			sb.append("AND ca.cust_cd = cc.co_cd AND cc.rec_status = 'A'  ");
			sb.append(
					" AND cc.co_cd IN (SELECT cust_cd FROM vessel_declarant WHERE vv_cd =:varno) ORDER BY ca.acct_nbr ");
			paramMap.put("varno", varno);
			log.info("***** getListAmendmentChargedTo SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				// map = new HashMap();
				// String userName = rs.getString("USER_NM");
				// map.put("userName", userName);
				// result.add(map);
				AccountValueObject oj = new AccountValueObject();

				String acct_nbr = CommonUtility.deNull(rs.getString("acct_nbr"));
				String cust_cd = CommonUtility.deNull(rs.getString("cust_cd"));
				if (!acct_nbr.equalsIgnoreCase(acct_nbr_main)) {
					oj.setAccountNumber(acct_nbr);
					oj.setCustomerCode(cust_cd);
				}

				if (acct_nbr != null && cust_cd != null)
					result.add(oj);
			}
		} catch (Exception e) {
			log.info("Exception getListAmendmentChargedTo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getListAmendmentChargedTo ***** varno:" + varno);
		}
		return result;
	}

	@Override
	public String getCreateCustCdOfVessel(String vvcode) throws BusinessException {
		String sql = "";
		String coCd = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		sql = " SELECT VC.CREATE_CUST_CD  FROM VESSEL_CALL VC WHERE VC.VV_CD=:vvcode";

		try {
			log.info("START: DAO getCreateCustCdOfVessel vvcode:" + vvcode);
			log.info("SQL" + sql);
			paramMap.put("vvcode", vvcode);
			log.info("***** getCreateCustCdOfVessel SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				coCd = CommonUtility.deNull(rs.getString("CREATE_CUST_CD"));
			}
			log.info("END: *** getCreateCustCdOfVessel ***** Result coCd:" + CommonUtility.deNull(coCd));
		} catch (Exception e) {
			log.info("Exception getCreateCustCdOfVessel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getCreateCustCdOfVessel *****");
		}
		return coCd;
	}

	@Override
	public boolean checkDisbaleOverSideFroDPE(String varno) throws BusinessException {
		String sql = "";
		boolean checkResult = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		sql = "SELECT COUNT(*) C FROM VESSEL_CALL WHERE SCHEME IN ('JBT', 'JCL', 'JWP') AND  VV_CD =:varno ";
		try {
			log.info("START: DAO checkDisbaleOverSideFroDPE checkResult:" + checkResult);
			paramMap.put("varno", varno);
			log.info("***** checkDisbaleOverSideFroDPE SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			rs.next();
			if (rs.getInt("C") > 0) {
				checkResult = true;
			} else {
				checkResult = false;
			}
		} catch (Exception e) {
			log.info("Exception checkDisbaleOverSideFroDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** checkDisbaleOverSideFroDPE ***** checkResult:" + checkResult);
		}
		return checkResult;

	}

	@Override
	public int retrieveMaxCargoTon(String vvCd) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		int maxCargoTon = 0;
		try {
			log.info("START: retrieveMaxCargoTon vvCd:" + vvCd);
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT MAX_CARGO_TON");
			sql.append("   FROM VESSEL_SCHEME");
			sql.append("  WHERE SCHEME_CD = (");
			sql.append("    SELECT SCHEME");
			sql.append("      FROM VESSEL_CALL");
			sql.append("     WHERE VV_CD = :vvCd");
			sql.append("  )");
			paramMap.put("vvCd", vvCd);
			log.info("***** retrieveMaxCargoTon SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				try {
					maxCargoTon = Integer.parseInt(rs.getString("MAX_CARGO_TON"));
				} catch (NumberFormatException e) {

				}
			}
		} catch (Exception e) {
			log.info("Exception retrieveMaxCargoTon : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** retrieveMaxCargoTon ***** maxCargoTon:" + maxCargoTon);
		}
		return maxCargoTon;
	}

	@Override
	public List<AccessCompanyValueObject> listCompanyStart(String keyword, Integer start, Integer limit) throws BusinessException {
		Map<String,String> paramMap = new HashMap<String,String>();
		StringBuilder sql = new StringBuilder();
		List<AccessCompanyValueObject> list = null;
		try {
			log.info("START: listCompanyStart DAO keyword:" + keyword + "start:" + start + "limit:" + limit);

			sql.append("SELECT co_cd as companyCode, co_nm || ' (' || co_cd || ')' as companyName ");
			sql.append("FROM tops.company_code ");
			sql.append("WHERE (co_nm LIKE :company OR co_cd LIKE :companyCode) AND rec_status='A'");
//			--20190412 koktsing
//		    --this is used to populate the consignee for Manifest, or shipper for Booking reference.
//		    --allow_jponline is not required here.
//		    --AND allow_jponline='Y'
			sql.append("ORDER BY co_nm");
			
			//sql.append("	AND ALLOW_JPONLINE='Y' ");
			paramMap.put("company", keyword.toUpperCase() + "%");
			paramMap.put("companyCode", keyword.toUpperCase() + "%");
			log.info("***** listCompanyStart SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			list = namedParameterJdbcTemplate.query(sql.toString(), paramMap,
					new BeanPropertyRowMapper<AccessCompanyValueObject>(AccessCompanyValueObject.class));
			log.info("END: *** listCompanyStart DAO ***** Result list:" + list.toString());
		} catch (Exception e) {
			log.info("Exception listCompanyStart : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** listCompanyStart DAO ***** ");
		}

		return list;
	}

	@Override
	public List<AccessCompanyValueObject> listCompany(String keyword, Integer start, Integer limit) throws BusinessException {
		Map<String,String> paramMap = new HashMap<String,String>();
		StringBuilder sql = new StringBuilder();
		List<AccessCompanyValueObject> list = null;
		try {
			log.info("START: listCompany DAO keyword:" + keyword + "start:" + start + "limit:" + limit);
			sql.append("SELECT ");
			sql.append("	co_cd, co_nm || ' (' || co_cd || ')' as co_nm ");
			sql.append("FROM ");
			sql.append("	tops.company_code ");
			sql.append("WHERE ");
			sql.append("	co_nm LIKE :company ");
			sql.append("	AND rec_status='A' ");
			sql.append("	AND allow_jponline='Y' ");
			sql.append("	ORDER BY co_nm ");
			paramMap.put("company",  "%"+keyword.toUpperCase() + "%");
			log.info("***** listCompany SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			list = namedParameterJdbcTemplate.query(sql.toString(), paramMap,
					new BeanPropertyRowMapper<AccessCompanyValueObject>(AccessCompanyValueObject.class));
			log.info("END: *** listCompany DAO ***** list:" + list.toString());
		} catch (Exception e) {
			log.info("Exception listCompany : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** listCompany DAO *****");
		}

		return list;
	}

	@Override
	public List<HSCode> getHSSubCodeList(String hsCd) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		List<HSCode> hsCodeLs = new ArrayList<HSCode>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * ");
		sql.append(" FROM HS_SUB_CODE ");
		sql.append(" WHERE REC_STATUS = '1' AND HS_CODE =:hsCd");
		sql.append(" ORDER BY HS_SUB_CODE_FR ");
		try {
			log.info("START Test DAO getHSSubCodeList: hsCd:" + hsCd);
			paramMap.put("hsCd", hsCd);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			log.info("***** getHSSubCodeList SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			while (rs.next()) {
				HSCode hs = new HSCode();
				hs.setHsCode(CommonUtility.deNull(rs.getString("HS_CODE")));
				hs.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				hs.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				hs.setHsSubDesc(CommonUtility.deNull(rs.getString("HS_SUB_DESC")));
				hsCodeLs.add(hs);
			}
			
			String filterValue =  getHSCodesFiler();
			log.info("filterValue in getHSSubCodeList method " + filterValue);
			
			if(filterValue.equalsIgnoreCase("YES"))
			{
				log.info("the filter value is YES, apply filtering of HS code/subcodes");			
				List<String> filteringCodes = getHSCodesFilerValues();
				log.info("filtering Code List :"+ filteringCodes);				
				Set<String> codesToRemove = filteringCodes.stream().flatMap(s -> Arrays.stream(s.split(","))).map(s->s.trim()).collect(Collectors.toSet());
				log.info("Filtering Codes To Remove"+ codesToRemove.toString());				
				hsCodeLs.removeIf(hs -> codesToRemove.contains(hs.getHsCode() + "-" + hs.getHsSubCodeFr() + "-" + hs.getHsSubCodeTo()));
				log.info("END: *** getHSSubCodeList Result After Filtering *****" + hsCodeLs.toString());						
				
				
			}
			else if (filterValue.equalsIgnoreCase("NO"))
			{
				log.info("the filter value is NO, do not apply filter and execute existing flow");			
		    }
			else
			{
				log.info("there is no value specific defined for Filter - do Nothing");
			}

		} catch (Exception e) {
			log.info("Exception getHSSubCodeList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getHSSubCodeList Result: ***** hsCodeLs: " + hsCodeLs);
		}
		return hsCodeLs;
	}
	
	//Added for Steel Billets NOM2 CR -- starts
	public String getHSCodesFiler() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		String filterValue = null;
		log.info("START: getHSCodesFiler  DAO  :");
		Map<String, String> paramMap = new HashMap<String, String>();
		try 
		{
			sb.append(
					"SELECT MISC_TYPE_NM FROM TOPS.SYSTEM_CONFIG sc WHERE CAT_CD='HS_SUBCODES_FILTER' AND MISC_TYPE_CD='HS_SUBCODES_FILTER' AND REC_STATUS='A'");
			filterValue = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			log.info("filterValue in getHSCodesFiler method :" + filterValue);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			log.info("Exception in getHSCodesFiler : ", e);
			throw new BusinessException("M4201");
		} 
		finally 
		{
			log.info("END: getHSCodesFiler ");
		}
		return filterValue;
	}
	
	public List<String> getHSCodesFilerValues() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		
		log.info("START: getHSCodesFiler  DAO  :");
		Map<String, String> paramMap = new HashMap<String, String>();
		List<String> filterValues = null;
		try 
		{
			sb.append(
					"SELECT MISC_TYPE_NM FROM TOPS.SYSTEM_CONFIG sc WHERE CAT_CD='HS_SUBCODES_FILTER' AND MISC_TYPE_CD='SUBCODES_VALUES' AND REC_STATUS='A'");
			//List<String> filterValues = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
			filterValues = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
			log.info("getHSCodesFilerValues() size:" + filterValues.size());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			log.info("Exception in getHSCodesFiler : ", e);
			throw new BusinessException("M4201");
		} 
		finally 
		{
			log.info("END: getHSCodesFiler ");
		}
		return filterValues;
	}
	//Added for Steel Billets NOM2 CR -- ends

	@Override
	public List<ManifestValueObject> getPkgList() throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		List<ManifestValueObject> pkgList = new ArrayList<ManifestValueObject>();
		String sql = "";
		sql = "SELECT PKG_TYPE_CD,PKG_DESC FROM PKG_TYPES WHERE REC_STATUS='A' ORDER BY PKG_DESC"; // addded
		try {
			log.info("START : getPkgList DAO START");
			log.info("***** getHSSubCodeList SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				ManifestValueObject mvObj = new ManifestValueObject();
				mvObj.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE_CD")));
				mvObj.setPkgn(CommonUtility.deNull(rs.getString("PKG_DESC")));
				pkgList.add(mvObj);
			}
		} catch (Exception e) {
			log.info("Exception getPkgList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getPkgList DAO ***** pkhList: " + pkgList);
		}
		return pkgList;
	}

	@Override
	public List<EsnListValueObject> getPkgList(String text) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String sql = "";
		String pkgsText = text;
		if (pkgsText.equals("ALL"))
			// Changed by Linus on 8 Oct 2003
			sql = "select * from PKG_TYPES WHERE REC_STATUS='A' ORDER BY PKG_TYPE_CD ";
		else
			sql = "select * from PKG_TYPES WHERE PKG_TYPE_CD LIKE :pkgsText  AND REC_STATUS='A' ORDER BY PKG_TYPE_CD";
		// Before
		/*
		 * sql = "select * from PKG_TYPES ORDER BY PKG_TYPE_CD"; else sql =
		 * "select * from PKG_TYPES WHERE PKG_TYPE_CD LIKE'"
		 * +pkgsText+"%' ORDER BY PKG_TYPE_CD";
		 */
		// End Change
		List<EsnListValueObject> pkgsList = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = null;
		try {
			log.info("START : getPkgList DAO START text: " + text);
			if (!pkgsText.equals("ALL")) {
				paramMap.put("pkgsText", pkgsText + "%");
			}
			log.info("***** getHSSubCodeList SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				esnListValueObject = new EsnListValueObject();
				esnListValueObject.setPkgDesc(CommonUtility.deNull(rs.getString("PKG_DESC")));
				esnListValueObject.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE_CD")));
				pkgsList.add(esnListValueObject);
			}
		} catch (Exception e) {
			log.info("Exception getPkgList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getPkgList DAO ***** pkgsList: " + pkgsList);
		}
		return pkgsList;
	}

	@Override
	public List<ManifestValueObject> getPortList() throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		List<ManifestValueObject> portList = new ArrayList<ManifestValueObject>();
		String sql = "";
		sql = "SELECT PORT_CD, PORT_NM FROM UN_PORT_CODE WHERE REC_STATUS = 'A' ORDER BY PORT_NM";
		try {
			log.info("START : getPortList DAO START");
			log.info("***** getPortList SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				ManifestValueObject mvObj = new ManifestValueObject();
				mvObj.setPortL(CommonUtility.deNull(rs.getString("PORT_CD")));
				mvObj.setPortLn(CommonUtility.deNull(rs.getString("PORT_NM")));
				portList.add(mvObj);
			}
			log.info("END: *** getPortList ***** portList: " + portList.size());
		} catch (Exception e) {
			log.info("Exception getPortList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getPortList EDO ******");
		}
		return portList;
	}

	@Override
	public List<ManifestValueObject> getPortList(String portCd, String portName) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		List<ManifestValueObject> portList = new ArrayList<ManifestValueObject>();
		String sql = "";
		sql = "SELECT PORT_CD, PORT_NM FROM UN_PORT_CODE WHERE REC_STATUS = 'A' AND PORT_CD LIKE :portCd  AND PORT_NM LIKE:portName  ORDER BY PORT_NM";
		try {
			log.info("START : getPkgList DAO " + ", portCd :" + portCd + ", portName : " + portName);
			paramMap.put("portCd", portCd + "%");
			paramMap.put("portName", portName + "%");
			log.info("***** getPortList SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				ManifestValueObject mvObj = new ManifestValueObject();
				mvObj.setPortL(CommonUtility.deNull(rs.getString("PORT_CD")));
				mvObj.setPortLn(CommonUtility.deNull(rs.getString("PORT_NM")));
				portList.add(mvObj);
			}
			log.info("END: *** getPortList ***** portList: " + portList.size());
		} catch (Exception e) {
			log.info("Exception getPortList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getPortList EDO ******");
		}
		return portList;
	}

	@Override
	public String MftUpdationWhenClosedDPE(String usrid, String coCd, String seqno, String varno, String blno,
			String crgdesc, String mark, String adviseBy, String adviseDate, String adviseMode, String amendChargedTo,
			String waiveCharge, String waiveReason, String hscd, String hsSubCodeFr, String hsSubCodeTo, String coname,
			String consigneeCoyCode, String selectedCargo, String conAddr, String notifyParty, String notifyPartyAddr,
			String placeDel, String placeReceipt, String shipperNm, String shipperAdd, String customHsCode,
			List<HsCodeDetails> multiHsCodeList) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder strUpdate = new StringBuilder();
		StringBuilder strUpdateTran = new StringBuilder();
		StringBuilder strMark = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		int seqtransno = 0;
		try {
			//TODO test
			log.info("START: MftUpdationWhenClosedDPE DAO usrid:" + usrid + "coCd:" + coCd + "seqno:" + seqno + "varno:"
					+ varno);
			log.info("blno:" + blno + "crgdesc:" + crgdesc + "mark:" + mark + "adviseBy:" + adviseBy + "adviseDate:"
					+ adviseDate + "adviseMode:" + adviseMode);
			log.info("amendChargedTo: " + amendChargedTo + "waiveCharge:" + waiveCharge + "waiveReason:" + waiveReason
					+ "hscd:" + hscd);
			log.info("hsSubCodeFr:" + hsSubCodeFr + "hsSubCodeTo:" + hsSubCodeTo + "coname:" + coname
					+ "consigneeCoyCode:" + consigneeCoyCode + "selectedCargo:" + selectedCargo + ",conAddr:" + conAddr
					+ ",notifyParty:" + notifyParty + ",notifyPartyAddr:" + notifyPartyAddr + ",placeDel:" + placeDel
					+ ",shipperNm:" + shipperNm + ",shipperAdd:" + shipperAdd + ",customHsCode:" + customHsCode
					+ ", multiHsCodeList : " + multiHsCodeList.toString());

			Map<String,String> mapErrorLength = this.checkLegthValidation(crgdesc, mark, conAddr, notifyParty, notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd);
			if(mapErrorLength.size() > 0) {
				String[] tmpString = new String[mapErrorLength.size()];
				int count = 0;
				for ( Entry<String, String> entry : mapErrorLength.entrySet()) {
				    String key = entry.getKey();
				    tmpString[count]= key;
				    count++;
				}
				String errorMessage = CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_invalidLength,tmpString);
				throw new BusinessException(errorMessage);
			}
			
			strUpdate.append(" UPDATE MANIFEST_DETAILS SET CONS_CO_CD=:consigneeCoyCode  ");
			strUpdate.append(" ,HS_CODE=:hscd ,HS_SUB_CODE_FR=:hsSubCodeFr ,HS_SUB_CODE_TO=:hsSubCodeTo ");
			strUpdate.append(" ,CONS_NM=:coname ,LAST_MODIFY_DTTM=SYSDATE,  ");
			
			// START CR FTZ HSCODE - NS JULY 2024
			strUpdate.append(" CUSTOM_HS_CODE=:customHsCode, CONSIGNEE_ADDR=:conAddr, SHIPPER_NM=:shipperNm, ");
			strUpdate.append(" SHIPPER_ADDR=:shipperAdd, NOTIFY_PARTY=:notifyParty, NOTIFY_PARTY_ADDR=:notifyPartyAddr,");
			strUpdate.append(" PLACE_OF_DELIVERY=:placeDel, PLACE_OF_RECEIPT=:placeReceipt");
			// END CR FTZ HSCODE - NS JULY 2024
			
			if (!"JP".equalsIgnoreCase(coCd)) {
				strUpdate.append(" ,LAST_MODIFY_USER_ID=:usrid ");
			}
			if (!"JP".equalsIgnoreCase(coCd) && isManClose(varno)) {
				strUpdate.append(" , ADVISE_BY='',ADVISE_DATE=to_timestamp('','DDMMYYYYHH24MI') ");
				strUpdate.append(" , ADVISE_MODE='',AMEND_CHARGED_TO='', WAIVE_CHARGED='', WAIVE_REASON='' ");
			}
			if ("JP".equalsIgnoreCase(coCd) && isManClose(varno)) {
				strUpdate.append(" , ADVISE_BY=:adviseBy ,ADVISE_DATE=to_timestamp(:adviseDate,'DDMMYYYYHH24MI') ");
				strUpdate.append(" , ADVISE_MODE=:adviseMode,AMEND_CHARGED_TO=:amendChargedTo ");
				strUpdate.append(" , WAIVE_CHARGED=:waiveCharge,WAIVE_REASON=:waiveReason ");
				strUpdate.append(" , CRG_DES=:crgdesc ");
				String sqltlog = "SELECT MAX(TRANS_NBR) FROM MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:seqno";
				paramMap.put("seqno", seqno);
				log.info("***** MftUpdationWhenClosedDPE SQL *****" + sqltlog.toString());
				log.info("params: " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);
				if (rs.next()) {
					seqtransno = (rs.getInt(1)) + 1;
				} else {
					seqtransno = 0;
				}
				strUpdateTran.append(
						" INSERT INTO MANIFEST_DETAILS_TRANS (MFT_SEQ_NBR, VAR_NBR, BL_NBR, BL_STATUS, CNTR_SIZE, CNTR_TYPE, DIS_TYPE, CRG_TYPE, CRG_DES,  ");
				strUpdateTran.append(
						"  STG_TYPE, CRG_STATUS, PKG_TYPE, NBR_PKGS, GROSS_WT, GROSS_VOL, EDO_NBR_PKGS, DG_IND, QTY_CRG, HS_CODE, LD_PORT, DIS_PORT, DES_PORT, ");
				strUpdateTran.append(
						" CONS_NM, NBR_PKGS_IN_PORT, LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM, SUPP_STATUS, GB_CLOSE_BJ_IND, MAN_TRANSFER_DTTM, MAN_ORIGINAL_VAR_NBR, ");
				strUpdateTran.append(
						" MIXED_SCHEME_ACCT_NBR, MIXED_SCHEME_BILL_PARTY, BILL_SERVICE_TRIGGERED_IND, BILL_WHARF_TRIGGERED_IND, CARGO_CATEGORY_CD, CUT_OFF_NBR_PKGS, ");
				strUpdateTran.append(
						" GB_CLOSE_BJ_DTTM, BILL_STORE_TRIGGERED_IND, REEXP_APPL_DTTM, REEXP_REF_NBR, MANIFEST_CREATE_CD, ADVISE_BY,  ");
				strUpdateTran.append(" ADVISE_DATE, ADVISE_MODE, AMEND_CHARGED_TO, WAIVE_CHARGED, WAIVE_REASON, ");
				strUpdateTran.append("  MISC_SEQ_NBR, TRANS_NBR, REMARKS, ");
				// START CR FTZ HSCODE - NS JULY 2024
				strUpdateTran.append("CONSIGNEE_ADDR, SHIPPER_NM, SHIPPER_ADDR,  ");
				strUpdateTran.append("NOTIFY_PARTY, NOTIFY_PARTY_ADDR, PLACE_OF_DELIVERY,  ");
				strUpdateTran.append("PLACE_OF_RECEIPT, CUSTOM_HS_CODE) ");
				// END CR FTZ HSCODE - NS JULY 2024
				strUpdateTran.append(
						" SELECT MFT_SEQ_NBR, VAR_NBR, BL_NBR, BL_STATUS, CNTR_SIZE, CNTR_TYPE, DIS_TYPE, CRG_TYPE, ");
				strUpdateTran.append(
						" CRG_DES, STG_TYPE, CRG_STATUS, PKG_TYPE, NBR_PKGS, GROSS_WT, GROSS_VOL, EDO_NBR_PKGS, DG_IND, QTY_CRG, HS_CODE, ");
				strUpdateTran.append("  LD_PORT, DIS_PORT, DES_PORT, CONS_NM, NBR_PKGS_IN_PORT, LAST_MODIFY_USER_ID, ");
				strUpdateTran.append(
						" SYSDATE, SUPP_STATUS, GB_CLOSE_BJ_IND, MAN_TRANSFER_DTTM, MAN_ORIGINAL_VAR_NBR, MIXED_SCHEME_ACCT_NBR, ");
				strUpdateTran.append(
						"  MIXED_SCHEME_BILL_PARTY, BILL_SERVICE_TRIGGERED_IND, BILL_WHARF_TRIGGERED_IND, CARGO_CATEGORY_CD, CUT_OFF_NBR_PKGS, ");
				strUpdateTran.append(
						" GB_CLOSE_BJ_DTTM, BILL_STORE_TRIGGERED_IND, REEXP_APPL_DTTM, REEXP_REF_NBR, MANIFEST_CREATE_CD, ADVISE_BY, ");
				strUpdateTran.append(
						" SYSDATE, ADVISE_MODE, AMEND_CHARGED_TO, WAIVE_CHARGED, WAIVE_REASON,:seqno,:seqtransno,null, ");
				// START CR FTZ HSCODE - NS JULY 2024
				strUpdateTran.append("CONSIGNEE_ADDR, SHIPPER_NM, SHIPPER_ADDR,  ");
				strUpdateTran.append("NOTIFY_PARTY, NOTIFY_PARTY_ADDR, PLACE_OF_DELIVERY,  ");
				strUpdateTran.append("PLACE_OF_RECEIPT, CUSTOM_HS_CODE ");
				// END CR FTZ HSCODE - NS JULY 2024
				strUpdateTran.append(" FROM MANIFEST_DETAILS  WHERE VAR_NBR =:varno AND BL_NBR =:blno ");
			}
			// strUpdate.append(strUpdate.toString());
			strUpdate.append(" WHERE VAR_NBR=:varno  AND BL_NBR=:blno AND MFT_SEQ_NBR=:seqno ");

			if ("JP".equalsIgnoreCase(coCd)) {
				strMark.append(" UPDATE MFT_MARKINGS SET MFT_MARKINGS=:mark ,LAST_MODIFY_DTTM=SYSDATE  ");
				strMark.append(" ,LAST_MODIFY_USER_ID=:usrid WHERE MFT_SQ_NBR=:seqno  ");
				paramMap.put("seqno", seqno);
				paramMap.put("mark", GbmsCommonUtility.addApostr(CommonUtility.deNull(mark)));
				paramMap.put("usrid", usrid);
				log.info("***** MftUpdationWhenClosedDPE SQL *****" + strMark.toString());
				log.info("params: " + paramMap.toString());
				namedParameterJdbcTemplate.update(strMark.toString(), paramMap);
			}

			log.info("SQL *** :" + strUpdate.toString());
			paramMap.put("consigneeCoyCode", consigneeCoyCode);
			paramMap.put("hscd", CommonUtility.deNull(hscd));
			paramMap.put("hsSubCodeFr", CommonUtility.deNull(hsSubCodeFr));
			paramMap.put("hsSubCodeTo", CommonUtility.deNull(hsSubCodeTo));
			paramMap.put("coname", GbmsCommonUtility.addApostr(CommonUtility.deNull(coname)));
			// START CR FTZ HSCODE - NS JULY 2024
			paramMap.put("conAddr", CommonUtility.deNull(conAddr));
			paramMap.put("notifyParty", CommonUtility.deNull(notifyParty));
			paramMap.put("notifyPartyAddr", CommonUtility.deNull(notifyPartyAddr));
			paramMap.put("placeDel", CommonUtility.deNull(placeDel));
			paramMap.put("placeReceipt", CommonUtility.deNull(placeReceipt));
			paramMap.put("shipperNm", CommonUtility.deNull(shipperNm));
			paramMap.put("shipperAdd", CommonUtility.deNull(shipperAdd));
			paramMap.put("customHsCode", CommonUtility.deNull(customHsCode));
			// END  CR FTZ HSCODE - NS JULY 2024
			if (!"JP".equalsIgnoreCase(coCd)) {
				paramMap.put("usrid", usrid);
			}
			if ("JP".equalsIgnoreCase(coCd) && isManClose(varno)) {
				paramMap.put("adviseBy", adviseBy);
				paramMap.put("adviseDate", adviseDate);
				paramMap.put("adviseMode", adviseMode);
				paramMap.put("amendChargedTo", amendChargedTo);
				paramMap.put("waiveCharge", waiveCharge);
				paramMap.put("waiveReason", waiveReason);
				paramMap.put("crgdesc", crgdesc);
			}
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			paramMap.put("seqno", seqno);
			log.info("***** MftUpdationWhenClosedDPE SQL *****" + strUpdate.toString());
			log.info("params: " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(strUpdate.toString(), paramMap);
			if (count != 0) { // not found in old code - ManifestEJB
				String manifestExtCount = "SELECT * FROM GBMS.MANIFEST_DETAILS_EXT WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ";
				paramMap.put("MFT_SEQ_NBR", seqno);
				log.info("***** MftUpdationWhenClosedDPE SQL *****" + manifestExtCount.toString());
				log.info("params: " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(manifestExtCount, paramMap);
				if (rs.next()) {
					if (selectedCargo == null || selectedCargo.equalsIgnoreCase("")) {
						log.info("******************************************* delete");
						String manifestExt = "DELETE FROM GBMS.MANIFEST_DETAILS_EXT WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ";
						paramMap.put("MFT_SEQ_NBR", seqno);
						log.info("***** MftUpdationWhenClosedDPE SQL *****" + manifestExt.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(manifestExt, paramMap);
					} else {
						log.info("******************************************* update");
						String manifestExt = "UPDATE GBMS.MANIFEST_DETAILS_EXT SET HS_SUB_DESC_CD=:HS_SUB_DESC_CD,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM=SYSDATE WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ";
						paramMap.put("MFT_SEQ_NBR", seqno);
						paramMap.put("HS_SUB_DESC_CD", selectedCargo);
						paramMap.put("userId", usrid);
						log.info("***** MftUpdationWhenClosedDPE SQL *****" + manifestExt.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(manifestExt, paramMap);
					}
				} else {
					StringBuilder sb2 = new StringBuilder();
					sb2.append(" INSERT INTO GBMS.MANIFEST_DETAILS_EXT  ");
					sb2.append(" (MFT_SEQ_NBR,HS_SUB_DESC_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
					sb2.append(" VALUES(:MFT_SEQ_NBR,:HS_SUB_DESC_CD,:userId,SYSDATE) ");

					paramMap.put("MFT_SEQ_NBR", seqno);
					paramMap.put("HS_SUB_DESC_CD", selectedCargo);
					paramMap.put("userId", usrid);
					log.info("***** MftUpdationWhenClosedDPE SQL *****" + sb2.toString());
					log.info("params: " + paramMap.toString());
					namedParameterJdbcTemplate.update(sb2.toString(), paramMap);
				}

				// Start CR FTZ HSCODE - NS JULY 2024
				
				for (HsCodeDetails hsCodeObj : multiHsCodeList) {
					paramMap.put("MFT_SEQ_NBR", seqno);
					paramMap.put("HS_CODE",hsCodeObj.getHsCode());
					paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
					paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
					paramMap.put("NBR_PKGS",hsCodeObj.getNbrPkgs());
					paramMap.put("GROSS_WT",hsCodeObj.getGrossWt());
					paramMap.put("GROSS_VOL",hsCodeObj.getGrossVol());
					paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
					paramMap.put("CRG_DES",hsCodeObj.getCrgDes());	
					paramMap.put("HS_SUB_CODE_DESC",hsCodeObj.getHsSubCodeDesc());	
					paramMap.put("userId", usrid);
				
					
					if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("A")) { // Add
						sb.setLength(0);
						sb.append(" SELECT * FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ");
						paramMap.put("MFT_SEQ_NBR", seqno);
						log.info("SQLA" + sb.toString());
						log.info("params: " + paramMap.toString());
						SqlRowSet rsEdo = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
						if(rsEdo.next()) {
							throw new BusinessException("EDO already created with the BL number. Please delete EDO first if required any change.");								
						}
						
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
						sb.append(" VALUES(:MFT_SEQ_NBR,:MFT_HSCODE_SEQ_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
						
						
						paramMap.put("MFT_HSCODE_SEQ_NBR", seqValue);
						paramMap.put("REC_STATUS", "A");
						log.info("SQL" + sb.toString());
						log.info("params: " + paramMap.toString());
						int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						log.info("counths : " + counths);
						
					}else if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("E")) { // Edit
						
						sb.setLength(0);
						sb.append(" SELECT EDO_ASN_NBR FROM GBMS.GB_EDO WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR and EDO_STATUS = 'A' ");
						paramMap.put("MFT_SEQ_NBR", seqno);
						SqlRowSet countCheck = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
						if (countCheck.next()) {
							
							sb.setLength(0);
							sb.append(" SELECT * FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
							paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
							log.info("SQLE" + sb.toString());
							log.info("params: " + paramMap.toString());
							SqlRowSet rsEdo = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
							if(rsEdo.next()) {

								// compare if only hscode is change
								log.info(rsEdo.getString("HS_CODE")+"="+(hsCodeObj.getHsCode())
										+","+ rsEdo.getString("HS_SUB_CODE_FR")+"="+(hsCodeObj.getHsSubCodeFr())
										+","+ rsEdo.getString("HS_SUB_CODE_TO")+"="+(hsCodeObj.getHsSubCodeTo())
										+","+ rsEdo.getString("CUSTOM_HS_CODE")+"="+(hsCodeObj.getCustomHsCode()));
								if (rsEdo.getString("HS_CODE").equalsIgnoreCase(hsCodeObj.getHsCode())
										&& rsEdo.getString("HS_SUB_CODE_FR").equalsIgnoreCase(hsCodeObj.getHsSubCodeFr())
										&& rsEdo.getString("HS_SUB_CODE_TO").equalsIgnoreCase(hsCodeObj.getHsSubCodeTo())
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
									
								}  else {
									throw new BusinessException(
											"EDO already created with edited HS Code. Please delete EDO first if required any change. Only Custom HS Code can be edited.");
								}
							} else {
								// old data that does not have gb_edo_hscode_details, auto create
								// get EDO_HSCODE_SEQ_NBR 
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
						
						
						
						sb.setLength(0);
						sb.append(" UPDATE GBMS.MANIFEST_HSCODE_DETAILS SET HS_CODE=:HS_CODE, HS_SUB_CODE_FR=:HS_SUB_CODE_FR, HS_SUB_CODE_TO=:HS_SUB_CODE_TO,");
						sb.append(" NBR_PKGS=:NBR_PKGS, GROSS_WT=:GROSS_WT, GROSS_VOL=:GROSS_VOL,CUSTOM_HS_CODE=:CUSTOM_HS_CODE, CRG_DES=:CRG_DES, ");
						sb.append(" HS_SUB_CODE_DESC=:HS_SUB_CODE_DESC, LAST_MODIFY_USER_ID=:userId, LAST_MODIFY_DTTM=SYSDATE WHERE");
						sb.append(" MFT_SEQ_NBR = :MFT_SEQ_NBR AND MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
						

						paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
						paramMap.put("REC_STATUS", "A");
						
						log.info("SQL" + sb.toString());
						log.info("params: " + paramMap.toString());
						int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						log.info("counths : " + counths);
						
						
					}else if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("D")) {
						sb.setLength(0);
						sb.append(" SELECT * FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ");
						paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
						log.info("SQL" + sb.toString());
						log.info("params: " + paramMap.toString());
						SqlRowSet rsEdo = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
						if(rsEdo.next()) {
							throw new BusinessException("EDO already created with deleted HS Code. Please delete EDO first.");
						}
						
						sb.setLength(0);
						sb.append(" DELETE FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_SEQ_NBR = :MFT_SEQ_NBR AND MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
					
						paramMap.put("REC_STATUS", "I");
						paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
						log.info("SQL" + sb.toString());
						log.info("params: " + paramMap.toString());
						int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						log.info("counths : " + counths);
						
						
					}
					
					sb.setLength(0);
					sb.append(" INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS  ");
					sb.append(" (MFT_SEQ_NBR,MFT_HSCODE_SEQ_NBR,AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
					sb.append(" VALUES(:MFT_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, SYSDATE, :REC_STATUS,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");

					log.info("SQL" + sb.toString());
					log.info("params: " + paramMap.toString());
					int counthsAudit = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
					log.info("counths : " + counthsAudit);
				
				}
				
				// END CR FTZ HSCODE - NS JULY 2024
			}

			if (count == 0) {
				throw new BusinessException("M4201");
			}

			if (strUpdateTran != null && !"".equalsIgnoreCase(strUpdateTran.toString())) {
				paramMap.put("seqno", seqno);
				paramMap.put("seqtransno", seqtransno);
				paramMap.put("varno", varno);
				paramMap.put("blno", blno);
				log.info("***** MftUpdationWhenClosedDPE SQL *****" + strUpdateTran.toString());
				log.info("params: " + paramMap.toString());
				namedParameterJdbcTemplate.update(strUpdateTran.toString(), paramMap);
			}

		} catch (NullPointerException e) {
			log.info("Exception MftUpdationWhenClosedDPE : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception MftUpdationWhenClosedDPE : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftUpdationWhenClosedDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END: MftUpdationWhenClosedDPE DAO seqno:" + seqno);
		}
		return seqno;

	}

	@Override
	public String MftUpdationForEnhancementHSCode(String usrid, String coCd, String seqno, String varno, String blno,
			String crgtyp, String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc, String mark,
			String nopkgs, String gwt, String gvol, String crgstat, String dgind, String stgind, String dop,
			String pkgtyp, String coname, String consigneeCoyCode, String poL, String poD, String poFD, String cntrtype,
			String cntrsize, String cntr1, String cntr2, String cntr3, String cntr4, String autParty, String adviseBy,
			String adviseDate, String adviseMode, String amendChargedTo, String waiveCharge, String waiveReason,
			String category, String deliveryToEPC, String selectedCargo, String conAddr, String notifyParty,
			String notifyPartyAddr, String placeDel, String placeReceipt, String shipperNm, String shipperAdd,
			String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder strMark = new StringBuilder();
		StringBuilder strUpdate = new StringBuilder();
		StringBuilder strCntr1 = new StringBuilder();
		StringBuilder strCntr2 = new StringBuilder();
		StringBuilder strCntr3 = new StringBuilder();
		StringBuilder strCntr4 = new StringBuilder();
		StringBuilder strMark_trans = new StringBuilder();
		StringBuilder strUpdate_trans = new StringBuilder();
		StringBuilder strCntr1_trans = new StringBuilder();
		StringBuilder strCntr2_trans = new StringBuilder();
		StringBuilder strCntr3_trans = new StringBuilder();
		StringBuilder strCntr4_trans = new StringBuilder();
		StringBuilder edoupd = new StringBuilder();
		StringBuilder edosql1 = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		String sqltlog = "";
		//TODO Test
		try {
			log.info("START: MftUpdationForEnhancementHSCode usrid:" + usrid + "coCd:" + coCd + "seqno:" + seqno
					+ "varno:" + varno);
			log.info("blno:" + blno + "crgtyp:" + crgtyp + "hscd:" + hscd + "hsSubCodeFr:" + hsSubCodeFr
					+ "hsSubCodeTo:" + hsSubCodeTo);
			log.info("crgdesc:" + crgdesc + "mark:" + mark + "nopkgs:" + nopkgs + "gwt:" + gwt + "gvol:" + gvol
					+ "crgstat:" + crgstat + "dgind:" + dgind);
			log.info("stgind:  " + stgind + "dop:" + dop + "pkgtyp:" + pkgtyp + "coname:" + coname + "consigneeCoyCode:"
					+ consigneeCoyCode + "poL:" + poL);
			log.info(" poD: " + poD + "cntrtype;" + cntrtype + "cntrsize:" + cntrsize + "cntr1:" + cntr1 + "cntr2:"
					+ cntr2 + "cntr3:" + cntr3 + "cntr4:" + cntr4);
			log.info(" autParty; " + autParty + "adviseBy:" + adviseBy + "adviseDate:" + adviseDate + "adviseMode:"
					+ adviseMode + "amendChargedTo:" + amendChargedTo);
			log.info(" waiveCharge: " + waiveCharge + "waiveReason:" + waiveReason + "category:" + category
					+ "deliveryToEPC:" + deliveryToEPC + ",conAddr:" + conAddr
					+ ",notifyParty:" + notifyParty + ",notifyPartyAddr:" + notifyPartyAddr + ",placeDel:" + placeDel
					+ ",shipperNm:" + shipperNm + ",shipperAdd:" + shipperAdd + ",customHsCode:" + customHsCode
					+ ", multiHsCodeList : " + multiHsCodeList.toString());
			boolean manStatus = isManClose(varno);
			boolean checkLog = false;
			// HaiTTH1 added on 3/4/2014
			boolean byPassValidationConsineeNm = false;
			boolean byPassValidationCustomField = false;
			StringBuilder sqlCheck = new StringBuilder();
			sqlCheck.append(" SELECT * FROM Manifest_Details WHERE VAR_NBR=:varno ");
			sqlCheck.append(" AND BL_NBR=:blno AND MFT_SEQ_NBR=:seqno ");
			try {
				paramMap.put("varno", varno);
				paramMap.put("blno", blno);
				paramMap.put("seqno", seqno);
				log.info("***** MftUpdationForEnhancementHSCode SQL *****" + sqlCheck.toString());
				log.info("params: " + paramMap.toString());
				SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlCheck.toString(), paramMap);
				int mftseqno = 0;
				if (rs1.next()) { // if in new while in old - ManifestEJB
					if (coname != null && !"".equalsIgnoreCase(coname)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CONS_NM")))
							&& !coname.equalsIgnoreCase(rs1.getString("CONS_NM"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = true;
					}
					
					// Custom Field					
					if (!conAddr.equalsIgnoreCase(rs1.getString("CONSIGNEE_ADDR"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationCustomField = true;
					}
					if (!notifyParty.equalsIgnoreCase(rs1.getString("NOTIFY_PARTY"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationCustomField = true;
					}
					if (!notifyPartyAddr.equalsIgnoreCase(rs1.getString("NOTIFY_PARTY_ADDR"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationCustomField = true;
					}
					if (!placeDel.equalsIgnoreCase(rs1.getString("PLACE_OF_DELIVERY"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationCustomField = true;
					}
					if (!placeReceipt.equalsIgnoreCase(rs1.getString("PLACE_OF_RECEIPT"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationCustomField = true;
					}
					if (!shipperNm.equalsIgnoreCase(rs1.getString("SHIPPER_NM"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationCustomField = true;
					}
					if (!shipperAdd.equalsIgnoreCase(rs1.getString("SHIPPER_ADDR"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationCustomField = true;
					}
					if (!customHsCode.equalsIgnoreCase(rs1.getString("CUSTOM_HS_CODE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationCustomField = true;
					}
					// custom fields
					mftseqno = rs1.getInt("MFT_SEQ_NBR");
					if (crgtyp != null && !"".equalsIgnoreCase(crgtyp)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CRG_TYPE")))
							&& !crgtyp.equalsIgnoreCase(rs1.getString("CRG_TYPE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (hscd != null && !"".equalsIgnoreCase(hscd)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("HS_CODE")))
							&& !hscd.equalsIgnoreCase(rs1.getString("HS_CODE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (nopkgs != null && !"".equalsIgnoreCase(nopkgs)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("NBR_PKGS")))
							&& !nopkgs.equalsIgnoreCase(rs1.getString("NBR_PKGS"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (gwt != null && !"".equalsIgnoreCase(gwt)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("GROSS_WT")))
							&& Double.parseDouble(gwt)!=  Double.parseDouble(rs1.getString("GROSS_WT"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (gvol != null && !"".equalsIgnoreCase(gvol)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("GROSS_VOL")))
							&&  Double.parseDouble(gvol)!= Double.parseDouble(rs1.getString("GROSS_VOL"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (crgstat != null && !"".equalsIgnoreCase(crgstat)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CRG_STATUS")))
							&& !crgstat.equalsIgnoreCase(rs1.getString("CRG_STATUS"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (dgind != null && !"".equalsIgnoreCase(dgind)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("DG_IND")))
							&& !dgind.equalsIgnoreCase(rs1.getString("DG_IND"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (stgind != null && !"".equalsIgnoreCase(stgind)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("STG_TYPE")))
							&& !stgind.equalsIgnoreCase(rs1.getString("STG_TYPE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (dop != null && !"".equalsIgnoreCase(dop)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("DIS_TYPE")))
							&& !dop.equalsIgnoreCase(rs1.getString("DIS_TYPE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (pkgtyp != null && !"".equalsIgnoreCase(pkgtyp)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("PKG_TYPE")))
							&& !pkgtyp.equalsIgnoreCase(rs1.getString("PKG_TYPE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (poL != null && !"".equalsIgnoreCase(poL)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("LD_PORT")))
							&& !poL.equalsIgnoreCase(rs1.getString("LD_PORT"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (poD != null && !"".equalsIgnoreCase(poD)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("DIS_PORT")))
							&& !poD.equalsIgnoreCase(rs1.getString("DIS_PORT"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (poFD != null && !"".equalsIgnoreCase(poFD)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("DES_PORT")))
							&& !poFD.equalsIgnoreCase(rs1.getString("DES_PORT"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (cntrtype != null && !"".equalsIgnoreCase(cntrtype)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CNTR_TYPE")))
							&& !cntrtype.equalsIgnoreCase(rs1.getString("CNTR_TYPE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					if (cntrsize != null && !"".equalsIgnoreCase(cntrsize)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CNTR_SIZE")))
							&& !cntrsize.equalsIgnoreCase(rs1.getString("CNTR_SIZE"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}

					if (autParty != null && !"".equalsIgnoreCase(autParty)
							&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("MANIFEST_CREATE_CD")))
							&& !autParty.equalsIgnoreCase(rs1.getString("MANIFEST_CREATE_CD"))) {
						if (manStatus)
							checkLog = true;
						byPassValidationConsineeNm = false;
						byPassValidationCustomField = false;
					}
					
					
					String sqlcntr = "SELECT * FROM BL_CNTR_DETAILS WHERE MFT_SEQ_NBR=:mftseqno";
					paramMap.put("mftseqno", mftseqno);
					log.info("***** MftUpdationForEnhancementHSCode SQL *****" + sqlcntr.toString());
					log.info("params: " + paramMap.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlcntr, paramMap);
					int cntrsqno = 0;
					while (rs1.next()) {
						cntrsqno = rs1.getInt("CNTR_BL_SEQ");
						if (cntrsqno == 1)
							if (cntr1 != null && !"".equalsIgnoreCase(cntr1)
									&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CNTR_NBR")))
									&& !cntr1.equalsIgnoreCase(rs1.getString("CNTR_NBR"))) {
								if (manStatus)
									checkLog = true;
								byPassValidationConsineeNm = false;
								byPassValidationCustomField = false;
							}
						if (cntrsqno == 2)
							if (cntr2 != null && !"".equalsIgnoreCase(cntr2)
									&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CNTR_NBR")))
									&& !cntr2.equalsIgnoreCase(rs1.getString("CNTR_NBR"))) {
								if (manStatus)
									checkLog = true;
								byPassValidationConsineeNm = false;
								byPassValidationCustomField = false;
							}
						if (cntrsqno == 3)
							if (cntr3 != null && !"".equalsIgnoreCase(cntr3)
									&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CNTR_NBR")))
									&& !cntr3.equalsIgnoreCase(rs1.getString("CNTR_NBR"))) {
								if (manStatus)
									checkLog = true;
								byPassValidationConsineeNm = false;
								byPassValidationCustomField = false;
							}
						if (cntrsqno == 4)
							if (cntr4 != null && !"".equalsIgnoreCase(cntr4)
									&& !"".equalsIgnoreCase(CommonUtility.deNull(rs1.getString("CNTR_NBR")))
									&& !cntr4.equalsIgnoreCase(rs1.getString("CNTR_NBR"))) {
								if (manStatus)
									checkLog = true;
								byPassValidationConsineeNm = false;
								byPassValidationCustomField = false;
							}
					}
				}
			} catch (Exception e) {
				log.info("Exception MftUpdationForEnhancementHSCode : ", e);
			} finally {
			}
			
			Map<String,String> mapErrorLength = this.checkLegthValidation(crgdesc, mark, conAddr, notifyParty, notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd);
			if(mapErrorLength.size() > 0) {
				String[] tmpString = new String[mapErrorLength.size()];
				int count = 0;
				for ( Entry<String, String> entry : mapErrorLength.entrySet()) {
				    String key = entry.getKey();
				    tmpString[count]= key;
				    count++;
				}
				String errorMessage = CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_invalidLength,tmpString);
				throw new BusinessException(errorMessage);
			}
			
			try {
				// added 23/01
				boolean vslstat = chkVslStat(varno);
				if (vslstat && !byPassValidationConsineeNm && !byPassValidationCustomField) {
					throw new BusinessException("M21605");
				}
				String hssubcodeDesc = getHSSubCodeDes(hscd, hsSubCodeFr, hsSubCodeTo);
				if (hssubcodeDesc == null || hssubcodeDesc.equalsIgnoreCase("")) {
					throw new BusinessException("M20223");
				}
				boolean edostat = chkEdonbrPkgs(seqno, varno, blno);
				if (edostat && !coCd.equalsIgnoreCase("JP") && !byPassValidationConsineeNm && !byPassValidationCustomField) {
					throw new BusinessException("M20204");
				}
				// added 07/03 start
				boolean dnstat = chkDNnbrPkgs(seqno, varno, blno);
				// added by vani start-- 17th OCt
				if (dnstat && coCd.equalsIgnoreCase("JP")) {
					strMark.append(" UPDATE MFT_MARKINGS SET MFT_MARKINGS=:mark  ,LAST_MODIFY_USER_ID=:usrid ");
					strMark.append(" ,LAST_MODIFY_DTTM=SYSDATE WHERE MFT_SQ_NBR=:seqno ");
					paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
					paramMap.put("usrid", usrid);
					paramMap.put("seqno", seqno);
					log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strMark.toString());
					log.info("params: " + paramMap.toString());
					int cntmark = namedParameterJdbcTemplate.update(strMark.toString(), paramMap);
					if (cntmark == 0) {
						log.info("Writing from ManifestEJB.MftUpdation");
						log.info("Record Cannot be added to Database");
						throw new BusinessException("M4201");
					}
					if (byPassValidationConsineeNm) {
						int seqtransno = 0;
						StringBuilder strManifest = new StringBuilder();
						strManifest.append(" UPDATE MANIFEST_DETAILS SET CONS_CO_CD=:consigneeCoyCode ");
						strManifest.append(" ,CONS_NM=:coname ,LAST_MODIFY_DTTM=SYSDATE , ADVISE_BY=:adviseBy ");
						strManifest.append(
								" ,ADVISE_DATE=TO_DATE(:adviseDate,'DDMMYYYYHH24MI'), ADVISE_MODE=:adviseMode ");
						strManifest.append(" ,AMEND_CHARGED_TO=:amendChargedTo, WAIVE_CHARGED=:waiveCharge ");
						strManifest.append(" ,WAIVE_REASON=:waiveReason WHERE VAR_NBR=:varno AND BL_NBR=:blno ");
						strManifest.append(" AND MFT_SEQ_NBR=:seqno ");

						sqltlog = "SELECT MAX(TRANS_NBR) FROM MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:seqno";
						paramMap.put("seqno", seqno);
						log.info("***** MftUpdationForEnhancementHSCode SQL *****" + sqltlog.toString());
						log.info("params: " + paramMap.toString());
						SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
						if (rs1.next()) {
							seqtransno = (rs1.getInt(1)) + 1;
						} else {
							seqtransno = 0;
						}
						StringBuilder strUpdateTran = new StringBuilder();
						strUpdateTran.append(
								" INSERT INTO MANIFEST_DETAILS_TRANS (MFT_SEQ_NBR, VAR_NBR, BL_NBR, BL_STATUS, CNTR_SIZE, CNTR_TYPE, DIS_TYPE, CRG_TYPE, CRG_DES, ");
						strUpdateTran.append(
								" STG_TYPE, CRG_STATUS, PKG_TYPE, NBR_PKGS, GROSS_WT, GROSS_VOL, EDO_NBR_PKGS, DG_IND, QTY_CRG, HS_CODE, LD_PORT, DIS_PORT, DES_PORT, ");
						strUpdateTran.append(
								" CONS_NM, NBR_PKGS_IN_PORT, LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM, SUPP_STATUS, GB_CLOSE_BJ_IND, MAN_TRANSFER_DTTM, MAN_ORIGINAL_VAR_NBR, ");
						strUpdateTran.append(
								" MIXED_SCHEME_ACCT_NBR, MIXED_SCHEME_BILL_PARTY, BILL_SERVICE_TRIGGERED_IND, BILL_WHARF_TRIGGERED_IND, CARGO_CATEGORY_CD, CUT_OFF_NBR_PKGS, ");
						strUpdateTran.append(
								" GB_CLOSE_BJ_DTTM, BILL_STORE_TRIGGERED_IND, REEXP_APPL_DTTM, REEXP_REF_NBR, MANIFEST_CREATE_CD, ADVISE_BY, ");
						strUpdateTran
								.append(" ADVISE_DATE, ADVISE_MODE, AMEND_CHARGED_TO, WAIVE_CHARGED, WAIVE_REASON, ");
						strUpdateTran.append(" MISC_SEQ_NBR, TRANS_NBR, REMARKS, CONSIGNEE_ADDR, SHIPPER_NM, SHIPPER_ADDR, ");
						strUpdateTran.append(" NOTIFY_PARTY, NOTIFY_PARTY_ADDR, PLACE_OF_DELIVERY, ");
						strUpdateTran.append(" PLACE_OF_RECEIPT, CUSTOM_HS_CODE) ");
						
						strUpdateTran.append(
								" SELECT MFT_SEQ_NBR, VAR_NBR, BL_NBR, BL_STATUS, CNTR_SIZE, CNTR_TYPE, DIS_TYPE, CRG_TYPE,  ");
						strUpdateTran.append(
								" CRG_DES, STG_TYPE, CRG_STATUS, PKG_TYPE, NBR_PKGS, GROSS_WT, GROSS_VOL, EDO_NBR_PKGS, DG_IND, QTY_CRG, HS_CODE, ");
						strUpdateTran.append(
								" LD_PORT, DIS_PORT, DES_PORT, CONS_NM, NBR_PKGS_IN_PORT, LAST_MODIFY_USER_ID, ");
						strUpdateTran.append(
								" SYSDATE, SUPP_STATUS, GB_CLOSE_BJ_IND, MAN_TRANSFER_DTTM, MAN_ORIGINAL_VAR_NBR, MIXED_SCHEME_ACCT_NBR, ");
						strUpdateTran.append(
								" MIXED_SCHEME_BILL_PARTY, BILL_SERVICE_TRIGGERED_IND, BILL_WHARF_TRIGGERED_IND, CARGO_CATEGORY_CD, CUT_OFF_NBR_PKGS, ");
						strUpdateTran.append(
								" GB_CLOSE_BJ_DTTM, BILL_STORE_TRIGGERED_IND, REEXP_APPL_DTTM, REEXP_REF_NBR, MANIFEST_CREATE_CD, ADVISE_BY, ");
						strUpdateTran.append(
								" SYSDATE, ADVISE_MODE, AMEND_CHARGED_TO, WAIVE_CHARGED, WAIVE_REASON,:seqno,:seqtransno,'', ");
						strUpdateTran.append(" CONSIGNEE_ADDR, SHIPPER_NM, SHIPPER_ADDR, NOTIFY_PARTY, NOTIFY_PARTY_ADDR, PLACE_OF_DELIVERY,  PLACE_OF_RECEIPT, CUSTOM_HS_CODE ");
						strUpdateTran.append("  FROM MANIFEST_DETAILS  WHERE VAR_NBR =:varno AND BL_NBR =:blno  ");
						
						
						paramMap.put("consigneeCoyCode", consigneeCoyCode);
						paramMap.put("coname", GbmsCommonUtility.addApostr(CommonUtility.deNull(coname)));
						paramMap.put("adviseBy", adviseBy);
						paramMap.put("adviseDate", adviseDate);
						paramMap.put("adviseMode", adviseMode);
						paramMap.put("amendChargedTo", amendChargedTo);
						paramMap.put("waiveCharge", waiveCharge);
						paramMap.put("waiveReason", waiveReason);
						paramMap.put("varno", varno);
						paramMap.put("blno", blno);
						paramMap.put("seqno", seqno);
						
						log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strManifest.toString());
						log.info("params: " + paramMap.toString());
						int count1 = namedParameterJdbcTemplate.update(strManifest.toString(), paramMap);
						log.info("MftUpdationForEnhancementHSCode  selectedCargo :" + selectedCargo);
						if (count1 != 0) { // Not found in old
							String manifestExtCount = "SELECT * FROM GBMS.MANIFEST_DETAILS_EXT WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ";
							paramMap.put("MFT_SEQ_NBR", seqno);
							rs = namedParameterJdbcTemplate.queryForRowSet(manifestExtCount, paramMap);
							if (rs.next()) {

								if (selectedCargo == null || selectedCargo.equalsIgnoreCase("")) {
									log.info("******************************************* delete");
									String manifestExt = "DELETE FROM GBMS.MANIFEST_DETAILS_EXT WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ";
									paramMap.put("MFT_SEQ_NBR", seqno);
									log.info("***** MftUpdationForEnhancementHSCode SQL *****" + manifestExt.toString());
									log.info("params: " + paramMap.toString());
									namedParameterJdbcTemplate.update(manifestExt, paramMap);
								} else {
									log.info("******************************************* update");
									String manifestExt = "UPDATE GBMS.MANIFEST_DETAILS_EXT SET HS_SUB_DESC_CD=:HS_SUB_DESC_CD,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM=SYSDATE WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ";
									paramMap.put("MFT_SEQ_NBR", seqno);
									paramMap.put("HS_SUB_DESC_CD", selectedCargo);
									paramMap.put("userId", usrid);
									log.info("***** MftUpdationForEnhancementHSCode SQL *****" + manifestExt.toString());
									log.info("params: " + paramMap.toString());
									namedParameterJdbcTemplate.update(manifestExt, paramMap);
								}

							} else {
								sb = new StringBuilder();
								sb.append(" INSERT INTO GBMS.MANIFEST_DETAILS_EXT  ");
								sb.append(" (MFT_SEQ_NBR,HS_SUB_DESC_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
								sb.append(" VALUES(:MFT_SEQ_NBR,:HS_SUB_DESC_CD,:userId,SYSDATE) ");
								paramMap.put("MFT_SEQ_NBR", seqno);
								paramMap.put("HS_SUB_DESC_CD", selectedCargo);
								paramMap.put("userId", usrid);
								log.info("***** MftUpdationForEnhancementHSCode SQL *****" + sb.toString());
								log.info("params: " + paramMap.toString());
								namedParameterJdbcTemplate.update(sb.toString(), paramMap);
							}
						}
						if (count1 == 0) {
							log.info("Writing from ManifestEJB.MftUpdationWithHS Code");
							log.info("Record Cannot be added to Database");
							throw new BusinessException("M4201");
						}
						if (strUpdateTran != null && !"".equalsIgnoreCase(strUpdateTran.toString())) {
							paramMap.put("seqno", seqno);
							paramMap.put("seqtransno", seqtransno);
							paramMap.put("varno", varno);
							paramMap.put("blno", blno);
							log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strUpdateTran.toString());
							log.info("params: " + paramMap.toString());
							namedParameterJdbcTemplate.update(strUpdateTran.toString(), paramMap);
						}
					}
				} else { // added by vani end-- 17th OCt
					log.info("======== INSIDE CASE 2 , byPassValidationCustomField : "+ byPassValidationCustomField +", byPassValidationConsineeNm : " + byPassValidationConsineeNm);
					if (dnstat && !coCd.equalsIgnoreCase("JP") && !byPassValidationConsineeNm  && !byPassValidationCustomField) {
						log.info("Writing from ManifestEJB.MftUpdation");
						log.info("DN Printed cannot Amend" + blno);
						throw new BusinessException("M20206");
					}
					boolean tnstat = chkTnbrPkgs(seqno, varno, blno);
					// changed by Irene Tan on 19 Feb 2004 : to allow JP user to
					// update markings
					// if (tnstat) {
					if (tnstat && !coCd.equalsIgnoreCase("JP") && !byPassValidationConsineeNm && !byPassValidationCustomField) {
						log.info("Writing from ManifestEJB.MftUpdation");
						log.info("Transhipment done cannot Amend" + blno);
						throw new BusinessException("M20207");
					}
					boolean tdnstat = chkTDNnbrPkgs(seqno, varno, blno);
					// if (tdnstat) {
					if (tdnstat && coCd.equalsIgnoreCase("JP") && !byPassValidationConsineeNm && !byPassValidationCustomField) {
						// end changed by Irene Tan on 19 Feb 2004
						log.info("Writing from ManifestEJB.MftUpdation");
						log.info("Transhipment done cannot Amend" + blno);
						throw new BusinessException("M20207");
					}
					// added 07/03 end
					boolean bnbrpkgs = chknbrpkgs(seqno, varno, blno, nopkgs);
					if (bnbrpkgs && !byPassValidationConsineeNm && !byPassValidationCustomField) {
						log.info("Writing from ManifestEJB.MftUpdation");
						log.info("Number of packages Less than EDO Nbr pkgs" + nopkgs);
						throw new BusinessException("M20205");
					}
					boolean blstat = chkBlStatus(seqno, varno, blno);
					if (blstat && !byPassValidationConsineeNm && !byPassValidationCustomField) {
						log.info("Writing from ManifestEJB.MftUpdation");
						log.info("BL canceled cannot Amend" + blno);
						throw new BusinessException("M20203");
					}
					// added 23/01
					String man_crgstatus = checkEdoCrgStatus(seqno, varno, blno);
					if (!crgstat.equalsIgnoreCase(man_crgstatus) && !byPassValidationConsineeNm && !byPassValidationCustomField) {
						if (chkEdoCrgStatus(seqno, varno, blno)) {
							String vslStatus = getVslStatus(varno);
							if (!(vslStatus.equalsIgnoreCase("AP") || vslStatus.equalsIgnoreCase("AL"))) {
								log.info("Writing from ManifestEJB.MftUpdation");
								log.info("Edo with ReExport cargo cannot Amend crgstatus" + blno);
								throw new BusinessException("M20210");
							}
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

					// Added by csathesh
					// To check for DG Indicator
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
					} // Lukman 12 Nov 2008 dis_type
					sqltlog = "SELECT MAX(TRANS_NBR) FROM MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:seqno";
					strUpdate.append(" UPDATE MANIFEST_DETAILS SET CRG_TYPE=:crgtyp  ");
					strUpdate.append(" ,CNTR_SIZE=:cntrsize ,CNTR_TYPE=:cntrtype,STG_TYPE=:stgind ");
					strUpdate.append(" ,DIS_TYPE=:dop,CRG_STATUS=:crgstat,PKG_TYPE=:pkgtyp ");
					strUpdate.append(" ,NBR_PKGS =:nopkgs,CRG_DES=:crgdesc,GROSS_WT=:gwt ");
					strUpdate.append(" ,GROSS_VOL=:gvol,DG_IND=:dgind ");
					strUpdate.append(" ,HS_CODE=:hscd,HS_SUB_CODE_FR=:hsSubCodeFr,HS_SUB_CODE_TO=:hsSubCodeTo ");
					strUpdate.append(" ,LD_PORT=:poL, DIS_PORT=:poD,DES_PORT=:poFD ");
					strUpdate.append(" ,LAST_MODIFY_USER_ID=:usrid,LAST_MODIFY_DTTM=SYSDATE,CONS_NM=:coname ");
					strUpdate.append(" ,CONS_CO_CD=:consigneeCoyCode ,CARGO_CATEGORY_CD=:category, ");
				
					// STAR CR FTZ HSCODE - NS JULY 2024
					strUpdate.append(" CONSIGNEE_ADDR = :conAddr, SHIPPER_NM = :shipperNm, SHIPPER_ADDR = :shipperAdd, ");
					strUpdate.append(" NOTIFY_PARTY = :notifyParty, NOTIFY_PARTY_ADDR = :notifyPartyAddr, PLACE_OF_DELIVERY= :placeDel, ");
					strUpdate.append(" PLACE_OF_RECEIPT = :placeReceipt, CUSTOM_HS_CODE = :customHsCode ");
					
					paramMap.put("conAddr", CommonUtility.deNull(conAddr));
					paramMap.put("notifyParty", CommonUtility.deNull(notifyParty));
					paramMap.put("notifyPartyAddr", CommonUtility.deNull(notifyPartyAddr));
					paramMap.put("placeDel", CommonUtility.deNull(placeDel));
					paramMap.put("placeReceipt", CommonUtility.deNull(placeReceipt));
					paramMap.put("shipperNm", CommonUtility.deNull(shipperNm));
					paramMap.put("shipperAdd", CommonUtility.deNull(shipperAdd));
					paramMap.put("customHsCode", CommonUtility.deNull(customHsCode));
					// END CR FTZ HSCODE - NS JULY 2024
					
					if (deliveryToEPC != null && !deliveryToEPC.equalsIgnoreCase("null")
							&& deliveryToEPC.trim().length() > 0) {
						strUpdate.append(" ,EPC_IND=:deliveryToEPC "); // MCC For EPC_IND
					}
					// Added by thanhnv2::Start
					if ("JP".equalsIgnoreCase(coCd)) {
						strUpdate.append(" ,MANIFEST_CREATE_CD=:autParty, ADVISE_BY=:adviseBy ");
						strUpdate.append(
								" ,ADVISE_DATE=TO_DATE(:adviseDate,'DDMMYYYYHH24MI') , ADVISE_MODE=:adviseMode ");
						strUpdate.append(
								" ,AMEND_CHARGED_TO=:amendChargedTo, WAIVE_CHARGED=:waiveCharge,WAIVE_REASON=:waiveReason ");
					} else {
					}
					strUpdate.append(" WHERE VAR_NBR=:varno  AND BL_NBR=:blno AND MFT_SEQ_NBR=:seqno ");
					edosql1.append(" SELECT EDO_ASN_NBR,NBR_PKGS,BL_NBR,ADP_CUST_CD,ADP_IC_TDBCR_NBR, ");
					edosql1.append(" ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR, ");
					edosql1.append(" AA_NM,ACCT_NBR,PAYMENT_MODE,EDO_DELIVERY_TO,EDO_CREATE_CD,EDO_STATUS, ");
					edosql1.append(
							" DN_NBR_PKGS,TRANS_NBR_PKGS,TRANS_DN_NBR_PKGS,RELEASE_NBR_PKGS,APPOINTED_ADP_CUST_CD, ");
					edosql1.append(
							" APPOINTED_ADP_IC_TDBCR_NBR,APPOINTED_ADP_NM, DIS_TYPE FROM GB_EDO WHERE VAR_NBR=:varno AND MFT_SEQ_NBR=:seqno ");
					String miscNo = ""; // VietNguyen added 02 March 2010 for
					// CR-CIM-20091203-34
					if ("JP".equalsIgnoreCase(coCd) || (checkLog && manStatus)) {
						miscNo = "0"; // Todo
					}
					SqlRowSet rs1 = null;
					String edonbr = "";
					int edonbrpkgs = 0;
					double nom_wt = 0.0;
					double nom_vol = 0.0;
					// start.added by vani -- 7th Oct,03
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
					log.info("***** MftUpdationForEnhancementHSCode SQL *****" + edosql1.toString());
					log.info("params: " + paramMap.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(edosql1.toString(), paramMap);
					while (rs1.next()) {
						log.info("rs1 :" + rs1.toString());
						edonbr = rs1.getString(1);
						edonbrpkgs = rs1.getInt(2);
						// start.added by vani -- 7th Oct,03
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
						// end.added by vani -- 7th Oct,03
						nom_wt = ((double) edonbrpkgs / Double.parseDouble(nopkgs)) * Double.parseDouble(gwt);
						nom_vol = ((double) edonbrpkgs / Double.parseDouble(nopkgs)) * Double.parseDouble(gvol);
						BigDecimal bdWt = new BigDecimal(nom_wt).setScale(2, RoundingMode.HALF_UP);
						BigDecimal bdVol = new BigDecimal(nom_vol).setScale(2, RoundingMode.HALF_UP);
						edoupd = new StringBuilder();
						edoupd.append(" UPDATE GB_EDO SET CRG_STATUS=:crgstat , NOM_WT=:bdWt ");
						edoupd.append(" ,NOM_VOL=:bdVol WHERE EDO_ASN_NBR =:edonbr AND MFT_SEQ_NBR =:seqno ");
						edoupd.append(" AND VAR_NBR=:varno ");
						String sqlEdoTransNbr = "SELECT MAX(TRANS_NBR) FROM GB_EDO_TRANS WHERE EDO_ASN_NBR=:edonbr";
						int iEdoTransNo = 0;
						if (logStatusGlobal.equalsIgnoreCase("Y")) {
							paramMap.put("edonbr", edonbr);
							log.info("***** MftUpdationForEnhancementHSCode SQL *****" + sqlEdoTransNbr.toString());
							log.info("params: " + paramMap.toString());
							SqlRowSet rsEdoTrans = namedParameterJdbcTemplate.queryForRowSet(sqlEdoTransNbr.toString(), paramMap);
							if (rsEdoTrans.next()) {
								iEdoTransNo = (rsEdoTrans.getInt(1)) + 1;
							} else {
								iEdoTransNo = 0;
							}
						}
						paramMap.put("crgstat", crgstat);
						paramMap.put("bdWt", bdWt.toString());
						paramMap.put("bdVol", bdVol.toString());
						paramMap.put("edonbr", edonbr);
						paramMap.put("seqno", seqno);
						paramMap.put("varno", varno);
						log.info("***** MftUpdationForEnhancementHSCode SQL *****" + edoupd.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(edoupd.toString(), paramMap);
						StringBuilder strEdoTransSql = new StringBuilder();
						if (logStatusGlobal.equalsIgnoreCase("Y")) {
							strEdoTransSql
									.append(" INSERT INTO GB_EDO_TRANS(EDO_ASN_NBR,TRANS_NBR,VAR_NBR,MFT_SEQ_NBR, ");
							strEdoTransSql
									.append(" BL_NBR,NBR_PKGS,NOM_WT,NOM_VOL,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM, ");
							strEdoTransSql
									.append(" CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,AA_NM, ");
							strEdoTransSql.append(" ACCT_NBR,PAYMENT_MODE,EDO_DELIVERY_TO,EDO_CREATE_CD,EDO_STATUS, ");
							strEdoTransSql
									.append(" DN_NBR_PKGS,TRANS_NBR_PKGS,TRANS_DN_NBR_PKGS,LAST_MODIFY_USER_ID, ");
							strEdoTransSql.append(" LAST_MODIFY_DTTM,RELEASE_NBR_PKGS,APPOINTED_ADP_CUST_CD, ");
							strEdoTransSql.append(" APPOINTED_ADP_IC_TDBCR_NBR,APPOINTED_ADP_NM, DIS_TYPE) VALUES( ");
							strEdoTransSql.append(":edonbr,:iEdoTransNo,:varno,:seqno,:strBlNbr,:edonbrpkgs  ");
							strEdoTransSql.append(" ,:bdWt,:bdVol,:strAdpCustCd,:strAdpIcTdbcrNbr,:strAdpNm ");
							strEdoTransSql
									.append(" ,:strCaCustCd,:strCaIcTdbcrNbr,:strCaNm,:strAaCustCd,:strAaIcTdbcrNbr  ");
							strEdoTransSql
									.append(" ,:strAaNm,:strAcctNbr,:strPaymentMd,:strEdoDeliveryTo,:strEdoCreateCd ");
							strEdoTransSql.append(
									" ,:strEdoStatus,:strDnNbrPkgs,:strTransNbrPkgs,:strTransDnNbrPkgs,:usrid ");
							strEdoTransSql
									.append(" ,SYSDATE,:strReleaseNbrPkgs,:strApptAdpCustCd,:strApptAdpIcTdbcrNbr ");
							strEdoTransSql.append(" ,:strApptAdpNm,:strDisType) ");
							paramMap.put("edonbr", edonbr);
							paramMap.put("iEdoTransNo", iEdoTransNo);
							paramMap.put("varno", varno);
							paramMap.put("seqno", seqno);
							paramMap.put("strBlNbr", strBlNbr);
							paramMap.put("edonbrpkgs", edonbrpkgs);
							paramMap.put("bdWt", bdWt.toString());
							paramMap.put("bdVol", bdVol.toString());
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
							log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strEdoTransSql.toString());
							log.info("params: " + paramMap.toString());
							namedParameterJdbcTemplate.update(strEdoTransSql.toString(), paramMap);
						}
					}
					strMark.append(" UPDATE MFT_MARKINGS SET MFT_MARKINGS=:mark,LAST_MODIFY_USER_ID=:usrid ");
					strMark.append(" ,LAST_MODIFY_DTTM=SYSDATE WHERE MFT_SQ_NBR=:seqno ");
					int stransno = 0;
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
						paramMap.put("seqno", seqno);
						log.info("***** MftUpdationForEnhancementHSCode SQL *****" + sqltlog.toString());
						log.info("params: " + paramMap.toString());
						rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);
						if (rs.next()) {
							stransno = (rs.getInt(1)) + 1;
						} else {
							stransno = 0;
						}
					}
					log.info("strUpdate :"+ strUpdate.toString());
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
					if (deliveryToEPC != null && !deliveryToEPC.equalsIgnoreCase("null")
							&& deliveryToEPC.trim().length() > 0) {
						paramMap.put("deliveryToEPC", CommonUtility.deNull(deliveryToEPC));
					}
					if ("JP".equalsIgnoreCase(coCd)) {
						paramMap.put("autParty", CommonUtility.deNull(autParty));
						paramMap.put("adviseBy", adviseBy);
						paramMap.put("adviseDate", adviseDate);
						paramMap.put("adviseMode", adviseMode);
						paramMap.put("amendChargedTo", amendChargedTo);
						paramMap.put("waiveCharge", waiveCharge);
						paramMap.put("waiveReason", waiveReason);
					} else {

					}
					paramMap.put("varno", varno);
					paramMap.put("blno", blno);
					paramMap.put("seqno", seqno);
					log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strUpdate.toString());
					log.info("params: " + paramMap.toString());
					int count = namedParameterJdbcTemplate.update(strUpdate.toString(), paramMap);
					log.info("MftUpdationForEnhancementHSCode selectedCargo :"+ selectedCargo);
					if (count != 0) { //not found in old
						String manifestExtCount = "SELECT * FROM GBMS.MANIFEST_DETAILS_EXT WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ";
						paramMap.put("MFT_SEQ_NBR", seqno);
						log.info("***** MftUpdationForEnhancementHSCode SQL *****" + manifestExtCount.toString());
						log.info("params: " + paramMap.toString());
						rs = namedParameterJdbcTemplate.queryForRowSet(manifestExtCount, paramMap);
						if (rs.next()) {

							if (selectedCargo == null || selectedCargo.equalsIgnoreCase("")) {
								log.info("******************************************* delete");
								String manifestExt = "DELETE FROM GBMS.MANIFEST_DETAILS_EXT WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ";
								paramMap.put("MFT_SEQ_NBR", seqno);
								log.info("***** MftUpdationForEnhancementHSCode SQL *****" + manifestExt.toString());
								log.info("params: " + paramMap.toString());
								namedParameterJdbcTemplate.update(manifestExt, paramMap);
							} else {
								log.info("******************************************* update");
								String manifestExt = "UPDATE GBMS.MANIFEST_DETAILS_EXT SET HS_SUB_DESC_CD=:HS_SUB_DESC_CD,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM=SYSDATE WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ";
								paramMap.put("MFT_SEQ_NBR", seqno);
								paramMap.put("HS_SUB_DESC_CD", selectedCargo);
								paramMap.put("userId", usrid);
								log.info("***** MftUpdationForEnhancementHSCode SQL *****" + manifestExt.toString());
								log.info("params: " + paramMap.toString());
								namedParameterJdbcTemplate.update(manifestExt, paramMap);
							}

						} else {
							sb = new StringBuilder();
							sb.append(" INSERT INTO GBMS.MANIFEST_DETAILS_EXT  ");
							sb.append(" (MFT_SEQ_NBR,HS_SUB_DESC_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
							sb.append(" VALUES(:MFT_SEQ_NBR,:HS_SUB_DESC_CD,:userId,SYSDATE) ");
							paramMap.put("MFT_SEQ_NBR", seqno);
							paramMap.put("HS_SUB_DESC_CD", selectedCargo);
							paramMap.put("userId", usrid);
							log.info("***** MftUpdationForEnhancementHSCode SQL *****" + sb.toString());
							log.info("params: " + paramMap.toString());
							namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						}

						// Start CR FTZ HSCODE - NS JULY 2024
					
						for (HsCodeDetails hsCodeObj : multiHsCodeList) {
							paramMap.put("MFT_SEQ_NBR", seqno);
							paramMap.put("HS_CODE",hsCodeObj.getHsCode());
							paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
							paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
							paramMap.put("NBR_PKGS",hsCodeObj.getNbrPkgs());
							paramMap.put("GROSS_WT",hsCodeObj.getGrossWt());
							paramMap.put("GROSS_VOL",hsCodeObj.getGrossVol());
							paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
							paramMap.put("CRG_DES",hsCodeObj.getCrgDes());	
							paramMap.put("HS_SUB_CODE_DESC",hsCodeObj.getHsSubCodeDesc());	
							paramMap.put("userId", usrid);
						
							
							if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("A")) { // Add
								sb.setLength(0);
								sb.append(" SELECT * FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ");
								paramMap.put("MFT_SEQ_NBR", seqno);
								log.info("SQL A : " + sb.toString());
								log.info("params A : " + paramMap.toString());
								SqlRowSet rsEdo = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
								if(rsEdo.next()) {
									throw new BusinessException("EDO already created with the BL number. Please delete EDO first if required any change.");								
								}
								
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
								sb.append(" VALUES(:MFT_SEQ_NBR,:MFT_HSCODE_SEQ_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
								
								
								paramMap.put("MFT_HSCODE_SEQ_NBR", seqValue);
								paramMap.put("REC_STATUS", "A");
								log.info("SQL" + sb.toString());
								log.info("params: " + paramMap.toString());
								int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
								log.info("counths : " + counths);
								
							}else if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("E")) { // Edit
								
								sb.setLength(0);
								sb.append(" SELECT EDO_ASN_NBR FROM GBMS.GB_EDO WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR and EDO_STATUS = 'A' ");
								paramMap.put("MFT_SEQ_NBR", seqno);
								log.info("SQL E : " + sb.toString());
								log.info("params E : " + paramMap.toString());
								SqlRowSet countCheck = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
								if (countCheck.next()) {
									
									sb.setLength(0);
									sb.append(" SELECT * FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
									paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
									log.info("SQL H : " + sb.toString());
									log.info("params H : " + paramMap.toString());
									SqlRowSet rsEdo = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
									if(rsEdo.next()) {

										// compare if only hscode is change
										log.info(rsEdo.getString("HS_CODE")+"="+(hsCodeObj.getHsCode())
												+","+ rsEdo.getString("HS_SUB_CODE_FR")+"="+(hsCodeObj.getHsSubCodeFr())
												+","+ rsEdo.getString("HS_SUB_CODE_TO")+"="+(hsCodeObj.getHsSubCodeTo())
												+","+ rsEdo.getString("CUSTOM_HS_CODE")+"="+(hsCodeObj.getCustomHsCode()));
										if (rsEdo.getString("HS_CODE").equalsIgnoreCase(hsCodeObj.getHsCode())
												&& rsEdo.getString("HS_SUB_CODE_FR").equalsIgnoreCase(hsCodeObj.getHsSubCodeFr())
												&& rsEdo.getString("HS_SUB_CODE_TO").equalsIgnoreCase(hsCodeObj.getHsSubCodeTo())
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
											
										}  else {
											throw new BusinessException(
													"EDO already created with edited HS Code. Please delete EDO first if required any change. Only Custom HS Code can be edited.");
										}
									} else {
										// old data that does not have gb_edo_hscode_details, auto create
										// get EDO_HSCODE_SEQ_NBR 
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
								
								
								
								sb.setLength(0);
								sb.append(" UPDATE GBMS.MANIFEST_HSCODE_DETAILS SET HS_CODE=:HS_CODE, HS_SUB_CODE_FR=:HS_SUB_CODE_FR, HS_SUB_CODE_TO=:HS_SUB_CODE_TO,");
								sb.append(" NBR_PKGS=:NBR_PKGS, GROSS_WT=:GROSS_WT, GROSS_VOL=:GROSS_VOL,CUSTOM_HS_CODE=:CUSTOM_HS_CODE, CRG_DES=:CRG_DES, ");
								sb.append(" HS_SUB_CODE_DESC=:HS_SUB_CODE_DESC, LAST_MODIFY_USER_ID=:userId, LAST_MODIFY_DTTM=SYSDATE WHERE");
								sb.append(" MFT_SEQ_NBR = :MFT_SEQ_NBR AND MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
								

								paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
								paramMap.put("REC_STATUS", "A");
								
								log.info("SQL" + sb.toString());
								log.info("params: " + paramMap.toString());
								int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
								log.info("counths : " + counths);
								
								
							}else if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("D")) {
								
								sb.setLength(0);
								sb.append(" SELECT * FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
								paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
								log.info("SQL D : " + sb.toString());
								log.info("params D : " + paramMap.toString());
								SqlRowSet rsEdo = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
								if(rsEdo.next()) {
									throw new BusinessException("EDO already created with deleted HS Code. Please delete EDO first.");
								}

								sb.setLength(0);
								sb.append(" DELETE FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
								paramMap.put("REC_STATUS", "I");
								paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
								log.info("SQL : " + sb.toString());
								log.info("params : " + paramMap.toString());
								int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
								log.info("counths : " + counths);
							}
							
							sb.setLength(0);
							sb.append(" INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS  ");
							sb.append(" (MFT_SEQ_NBR,MFT_HSCODE_SEQ_NBR,AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
							sb.append(" VALUES(:MFT_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, SYSDATE, :REC_STATUS,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
				
							log.info("SQL" + sb.toString());
							log.info("params: " + paramMap.toString());
							int counthsAudit = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
							log.info("counths : " + counthsAudit);
						
						}
						
						// END CR FTZ HSCODE - NS JULY 2024
					}
					paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
					paramMap.put("usrid", usrid);
					paramMap.put("seqno", seqno);
					log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strMark.toString());
					log.info("params: " + paramMap.toString());
					int cntmark = namedParameterJdbcTemplate.update(strMark.toString(), paramMap);

					// Added by MCconsulting , deliveryToEPC set to 'N' if deliveryToEPC is null or
					// empty.
					deliveryToEPC = (deliveryToEPC == null || deliveryToEPC.equalsIgnoreCase("null")
							|| deliveryToEPC.trim().length() == 0) ? "N" : deliveryToEPC;
					if (logStatusGlobal.equalsIgnoreCase("Y")) {
						strUpdate_trans.append(
								"  INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,VAR_NBR,BL_NBR,CRG_TYPE,DIS_TYPE, ");
						strUpdate_trans.append(" CNTR_SIZE,CNTR_TYPE,STG_TYPE,CRG_STATUS,PKG_TYPE,NBR_PKGS,CRG_DES, ");
						strUpdate_trans.append(
								" GROSS_WT,GROSS_VOL,DG_IND,HS_CODE,LD_PORT,DIS_PORT,DES_PORT,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,CONS_NM, MISC_SEQ_NBR,CARGO_CATEGORY_CD,EPC_IND ");
						if ("JP".equalsIgnoreCase(coCd)) {
							strUpdate_trans.append(
									" ,ADVISE_BY,ADVISE_DATE,ADVISE_MODE,AMEND_CHARGED_TO,WAIVE_CHARGED,WAIVE_REASON ) ");
						} else {
							strUpdate_trans.append(" ) ");
						}
						strUpdate_trans.append(" VALUES(:stransno, :seqno,:varno,:blno,:crgtyp,:dop,:cntrsize ");
						strUpdate_trans.append(" ,:cntrtype,:stgind,:crgstat,:pkgtyp,:nopkgs,:crgdesc, ");
						strUpdate_trans.append(
								" :gwt,:gvol,:dgind,:hscd,:poL,:poD,:poFD,:usrid,SYSDATE,:coname,:miscNo,:category, ");
						strUpdate_trans.append(" :deliveryToEPC ");
						if ("JP".equalsIgnoreCase(coCd)) {
							strUpdate_trans.append(
									",:adviseBy,TO_DATE(:adviseDate,'DDMMYYYYHH24MI'),:adviseMode,:amendChargedTo,  ");
							strUpdate_trans.append(" :waiveCharge,:waiveReason) ");
						} else {
							strUpdate_trans.append(" ) ");
						}
						strMark_trans.append(
								" INSERT INTO MFT_MARKINGS_TRANS(TRANS_NBR,MFT_SQ_NBR,MFT_MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) VALUES(:stransno ");
						strMark_trans.append(" ,:seqno,:mark,:usrid,SYSDATE )");
					}

					int count_trans = 0;
					int cntmark_trans = 0;
					// Transaction Log Table Insertion 23/5/2002
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
						paramMap.put("poFD", CommonUtility.deNull(poFD));
						paramMap.put("poL", poL);
						paramMap.put("poD", poD);
						paramMap.put("usrid", usrid);
						paramMap.put("coname", GbmsCommonUtility.addApostr(coname));
						paramMap.put("miscNo", miscNo);
						paramMap.put("category", category);
						paramMap.put("deliveryToEPC", CommonUtility.deNull(deliveryToEPC));
						if ("JP".equalsIgnoreCase(coCd)) {
							paramMap.put("adviseBy", CommonUtility.deNull(adviseBy));
							paramMap.put("adviseDate", adviseDate);
							paramMap.put("adviseMode", CommonUtility.deNull(adviseMode));
							paramMap.put("amendChargedTo", CommonUtility.deNull(amendChargedTo));
							paramMap.put("waiveCharge", CommonUtility.deNull(waiveCharge));
							paramMap.put("waiveReason", CommonUtility.deNull(waiveReason));
						} else {

						}
						log.info("SQL" + strUpdate_trans.toString());
						count_trans = namedParameterJdbcTemplate.update(strUpdate_trans.toString(), paramMap);
						paramMap.put("stransno", stransno);
						paramMap.put("seqno", seqno);
						paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
						paramMap.put("usrid", usrid);
						log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strMark_trans.toString());
						log.info("params: " + paramMap.toString());
						cntmark_trans = namedParameterJdbcTemplate.update(strMark_trans.toString(), paramMap);
					}
					strCntr1.append(
							" UPDATE BL_CNTR_DETAILS SET CNTR_NBR=:cntr1 WHERE MFT_SEQ_NBR=:seqno AND CNTR_BL_SEQ=1 ");
					paramMap.put("cntr1", cntr1);
					paramMap.put("seqno", seqno);
					log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strCntr1.toString());
					log.info("params: " + paramMap.toString());
					int cntcntr1 = namedParameterJdbcTemplate.update(strCntr1.toString(), paramMap);
					if (cntcntr1 == 0 && cntr1 != "") {
						strCntr1.setLength(0);
						strCntr1.append(
								" INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(1,:seqno,:cntr1) ");
						paramMap.put("seqno", seqno);
						paramMap.put("cntr1", cntr1);
						log.info("SQL" + strCntr1.toString());
						namedParameterJdbcTemplate.update(strCntr1.toString(), paramMap);
					}
					strCntr2.append(" UPDATE BL_CNTR_DETAILS SET CNTR_NBR=:cntr2 ");
					strCntr2.append(" WHERE MFT_SEQ_NBR=:seqno  AND CNTR_BL_SEQ=2 ");
					paramMap.put("cntr2", cntr2);
					paramMap.put("seqno", seqno);
					log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strCntr2.toString());
					log.info("params: " + paramMap.toString());
					int cntcntr2 = namedParameterJdbcTemplate.update(strCntr2.toString(), paramMap);
					if (cntcntr2 == 0 && cntr2 != "") {
						strCntr2.setLength(0);
						strCntr2.append(" INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(2, ");
						strCntr2.append(" :seqno,:cntr2) ");
						paramMap.put("seqno", seqno);
						paramMap.put("cntr2", cntr2);
						log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strCntr2.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(strCntr2.toString(), paramMap);
					}

					strCntr3.append(" UPDATE BL_CNTR_DETAILS SET CNTR_NBR=:cntr3 WHERE MFT_SEQ_NBR=:seqno ");
					strCntr3.append(" AND CNTR_BL_SEQ=3 ");
					paramMap.put("seqno", seqno);
					paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
					log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strCntr3.toString());
					log.info("params: " + paramMap.toString());
					int cntcntr3 = namedParameterJdbcTemplate.update(strCntr3.toString(), paramMap);
					if (cntcntr3 == 0 && cntr3 != "") {
						strCntr3.setLength(0);
						strCntr3.append(
								"  INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(3,:seqno,:cntr3) ");
						paramMap.put("seqno", seqno);
						paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
						log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strCntr3.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(strCntr3.toString(), paramMap);
					}
					strCntr4.append(
							" UPDATE BL_CNTR_DETAILS SET CNTR_NBR=:cntr4 WHERE MFT_SEQ_NBR=:seqno AND CNTR_BL_SEQ=4 ");
					paramMap.put("seqno", seqno);
					paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
					log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strCntr4.toString());
					log.info("params: " + paramMap.toString());
					int cntcntr4 = namedParameterJdbcTemplate.update(strCntr4.toString(), paramMap);
					if (cntcntr4 == 0 && cntr4 != "") {
						strCntr4.setLength(0);
						strCntr4.append(
								" INSERT INTO BL_CNTR_DETAILS(CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES(4,:seqno,:cntr4) ");
						paramMap.put("seqno", seqno);
						paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
						log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strCntr4.toString());
						log.info("params: " + paramMap.toString());
						namedParameterJdbcTemplate.update(strCntr4.toString(), paramMap);
					}
					strCntr1_trans.append(
							" INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES( ");
					strCntr1_trans.append(" :stransno,'1',:seqno,:cntr1) ");
					strCntr2_trans.append(
							" INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES( ");
					strCntr2_trans.append(" :stransno,'2',:seqno,:cntr2) ");
					strCntr3_trans.append(
							" INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES( ");
					strCntr3_trans.append(" :stransno,'3',:seqno,:cntr3) ");
					strCntr4_trans.append(
							" INSERT INTO BL_CNTR_DETAILS_TRANS(TRANS_NBR,CNTR_BL_SEQ,MFT_SEQ_NBR,CNTR_NBR) VALUES( ");
					strCntr4_trans.append(" :stransno,'4',:seqno,:cntr4 ");
					if (cntr1 != null && !cntr1.equals("")) {
						if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
							paramMap.put("stransno", stransno);
							paramMap.put("seqno", seqno);
							paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
							log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strCntr1_trans.toString());
							log.info("params: " + paramMap.toString());
							namedParameterJdbcTemplate.update(strCntr1_trans.toString(), paramMap);
						}
					}
					if (cntr2 != null && !cntr2.equals("")) {
						if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
							paramMap.put("stransno", stransno);
							paramMap.put("seqno", seqno);
							paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
							log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strCntr2_trans.toString());
							log.info("params: " + paramMap.toString());
							namedParameterJdbcTemplate.update(strCntr2_trans.toString(), paramMap);
						}
					}
					if (cntr3 != null && !cntr3.equals("")) {
						if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
							paramMap.put("stransno", stransno);
							paramMap.put("seqno", seqno);
							paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
							log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strCntr3_trans.toString());
							log.info("params: " + paramMap.toString());
							namedParameterJdbcTemplate.update(strCntr3_trans.toString(), paramMap);
						}
					}
					if (cntr4 != null && !cntr4.equals("")) {
						if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
							paramMap.put("stransno", stransno);
							paramMap.put("seqno", seqno);
							paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
							log.info("***** MftUpdationForEnhancementHSCode SQL *****" + strCntr4_trans.toString());
							log.info("params: " + paramMap.toString());
							namedParameterJdbcTemplate.update(strCntr4_trans.toString(), paramMap);
						}
					}
					if (count == 0 || cntmark == 0) {
						throw new BusinessException("M4201");
					}
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
						if (count_trans == 0 || cntmark_trans == 0) {
							throw new BusinessException("M4201");
						}
					}
				}
			} catch (BusinessException e) {
				log.info("Exception MftUpdationForEnhancementHSCode : ", e);
				throw new BusinessException(e.getMessage());
			} catch (Exception e) {
				log.info("Exception MftUpdationForEnhancementHSCode : ", e);
				throw new BusinessException("M4201");
			}
		} catch (NullPointerException e) {
			log.info("Exception MftUpdationForEnhancementHSCode : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception MftUpdationForEnhancementHSCode : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftUpdationForEnhancementHSCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** MftUpdationForEnhancementHSCode DAO ***** seqno:" + seqno);
		}
		return seqno;

	}

	private boolean chknbrpkgs(String seqno, String varno, String blno, String nopkgs) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean texist = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: chknbrpkgs DAO seqno:" + seqno + "varno;" + varno + "blno:" + blno + "nopkgs:" + nopkgs);
			
			sql.append(" SELECT TRANS_NBR_PKGS FROM GB_EDO WHERE TRANS_NBR_PKGS>0 AND MFT_SEQ_NBR=:seqno ");
			sql.append("  AND VAR_NBR=:varno AND BL_NBR=:blno "); //diff sql in old
			//this sql in old
//			sql = "SELECT EDO_NBR_PKGS FROM MANIFEST_DETAILS WHERE MFT_SEQ_NBR='"
//					+ seqno + "' AND VAR_NBR='" + varno + "' AND BL_NBR='" + blno
//					+ "'"; 
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("***** chknbrpkgs SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				texist = true;
			} else {
				texist = false;
			}
		} catch (Exception e) {
			log.info("Exception chknbrpkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** chknbrpkgs DAO ***** texist:" + texist);
		}
		return texist;
	}

	private boolean chkBlStatus(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean blstat = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: chkBlStatus DAO seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT BL_STATUS FROM MANIFEST_DETAILS WHERE BL_STATUS='X' AND MFT_SEQ_NBR=:seqno ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("***** chkBlStatus SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				blstat = true;
			} else {
				blstat = false;
			}
		} catch (Exception e) {
			log.info("Exception chkBlStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkBlStatus blstat:" + blstat);
		}
		return blstat;
	}

	private String checkEdoCrgStatus(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		String scrgstatus = ""; // int count =0;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: checkEdoCrgStatus DAO seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT CRG_STATUS FROM MANIFEST_DETAILS WHERE   MFT_SEQ_NBR=:seqno ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("***** checkEdoCrgStatus SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				scrgstatus = rs.getString("CRG_STATUS");
			}
		} catch (Exception e) {
			log.info("Exception checkEdoCrgStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: ***checkEdoCrgStatus ***** scrgstatus:" + scrgstatus);
		}
		return scrgstatus;
	}

	private boolean chkEdoCrgStatus(String seqno, String varno, String blno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean bcrgstatus = false;
		int count = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: chkEdoCrgStatus DAO seqno:" + seqno + "varno:" + varno + "blno:" + blno);
			sql.append(" SELECT CRG_STATUS FROM GB_EDO WHERE MFT_SEQ_NBR=:seqno ");
			sql.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("***** checkEdoCrgStatus SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				if ((rs.getString("CRG_STATUS")).equals("R"))
					count = count + 1;
			}
			if (count > 0)
				bcrgstatus = true;
		} catch (Exception e) {
			log.info("Exception chkEdoCrgStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** chkEdoCrgStatus ***** blstat:" + bcrgstatus);
		}
		return bcrgstatus;

	}

	private String getVslStatus(String varno) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		String vsl_status = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getVslStatus DAO varno:" + varno);
			sql.append(" SELECT VV_STATUS_IND from VESSEL_CALL where  VV_CD=:varno ");
			paramMap.put("varno", varno);
			log.info("***** getVslStatus SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				vsl_status = rs.getString("VV_STATUS_IND");
			}

		} catch (Exception e) {
			log.info("Exception getVslStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getVslStatus ***** vsl_status:" + vsl_status);
		}
		return vsl_status;
	}

	@Override
	public AdminFeeWaiverValueObject updateWaiverAdvice(AdminFeeWaiverValueObject adminFeeWaiverVO, String userID) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String errSql = "";
		String apprSql = "";
		try {
			log.info("START:updateWaiverAdvice DAO adminFeeWaiverVO:" + adminFeeWaiverVO.toString() + "userID:"
					+ userID);
			
			errSql = "UPDATE WAIVER_ADVICE SET STATUS =:status WHERE ADVICE_ID=:adviceId ";
			apprSql = "  UPDATE WAIVER_ADVICE SET STATUS =:status, REMARKS =:remarks, APPROVED_BY=:approvedBy, APPROVED_AT=SYSDATE WHERE ADVICE_NBR=:adviceNbr";

			if (adminFeeWaiverVO.getWaiverStatus().equalsIgnoreCase("E") || adminFeeWaiverVO.getWaiverStatus().equalsIgnoreCase("P")) {
				log.info("Logging Error for Oscar call: " + adminFeeWaiverVO.getAdviceId());
				// Update status as E when there is error in calling webservice
				paramMap.put("status", adminFeeWaiverVO.getWaiverStatus());
				paramMap.put("adviceId", adminFeeWaiverVO.getAdviceId());
				log.info("***** START: updateWaiverAdvice SQL *****" + errSql.toString());
				log.info("params: " + paramMap.toString());
				namedParameterJdbcTemplate.update(errSql.toString(), paramMap);
			} else {
				paramMap.put("status", adminFeeWaiverVO.getWaiverStatus());
				paramMap.put("remarks", adminFeeWaiverVO.getApprovalRemarks());
				paramMap.put("approvedBy", adminFeeWaiverVO.getApprovedBy());
				paramMap.put("adviceNbr", adminFeeWaiverVO.getWanAdviceNbr());
				log.info("***** START: updateWaiverAdvice SQL *****" + apprSql.toString());
				log.info("params: " + paramMap.toString());
				namedParameterJdbcTemplate.update(apprSql.toString(), paramMap);
			}

			String sqlSS = "SELECT REF_NBR, REF_TYPE, REQUESTED_BY, VV_CD FROM WAIVER_ADVICE WHERE ADVICE_NBR=:adviceNbr";
			// Fixed. To fetch vv_cd too
			paramMap.put("adviceNbr", adminFeeWaiverVO.getWanAdviceNbr());
			log.info("SQL" + sqlSS);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlSS, paramMap);

			while (rs.next()) {
				adminFeeWaiverVO.setRefNumber(CommonUtility.deNull(rs.getString("REF_NBR")));
				adminFeeWaiverVO.setWaiverType(CommonUtility.deNull(rs.getString("REF_TYPE")));
				adminFeeWaiverVO.setRequestedBy((rs.getString("REQUESTED_BY")));
				String vvCd = rs.getString("VV_CD");
				vvCd = (vvCd == null) ? "" : vvCd.trim();
				adminFeeWaiverVO.setVvCd(vvCd);
			}

		} catch (Exception e) {
			log.info("Exception updateWaiverAdvice : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateWaiverAdvice DAO END");
		}
		return adminFeeWaiverVO;
	}

	@Override
	public String getScheme(String voy_nbr) throws BusinessException {
		String sql = "";
		String msch = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getScheme DAO voy_nbr:" + voy_nbr);
			sql = "SELECT AB_CD FROM VESSEL_CALL VC, VESSEL_SCHEME VS WHERE VC.SCHEME=VS.SCHEME_CD AND VC.VV_CD=:voyNbr";
			paramMap.put("voyNbr", voy_nbr);
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				msch = rs.getString(1);
			}
		} catch (Exception e) {
			log.info("Exception getScheme : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getScheme DAO END msch:" + msch);
		}
		return msch;
	}

	@Override
	public String getSchemeInd(String voy_nbr) throws BusinessException {
		String sql = "";
		String msch = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getSchemeInd DAO voy_nbr:" + voy_nbr);
			sql = "SELECT MIXED_SCHEME_IND FROM VESSEL_CALL WHERE VV_CD=:voyNbr";
			paramMap.put("voyNbr", voy_nbr);
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				msch = rs.getString(1);
			}
		} catch (Exception e) {
			log.info("Exception getSchemeInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeInd DAO END msch:" + msch);
		}
		return msch;
	}

	@Override
	public List<ManifestValueObject> getCargoType() throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		List<ManifestValueObject> cargoTypeRowVect = new ArrayList<ManifestValueObject>();
		ManifestValueObject manifestObj = new ManifestValueObject();
		try {
			log.info("START: getCargoType DAO ");
			sql = "SELECT CRG_TYPE_CD,CRG_TYPE_NM from crg_type where rec_status='A' and crg_type_cd not in ('00','01','02','03') ORDER BY CRG_TYPE_CD";
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			for (; rs.next();) {
				manifestObj = new ManifestValueObject();
				manifestObj.setCrgType(CommonUtility.deNull(rs.getString("CRG_TYPE_CD")));
				manifestObj.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_TYPE_NM")));
				cargoTypeRowVect.add(manifestObj);
			}
		} catch (Exception e) {
			log.info("Exception getCargoType : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getCargoType DAO END");
		}
		return cargoTypeRowVect;
	}

	@Override
	public List<String> getSAacctno(String voy_nbr) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		List<String> vacctno = new ArrayList<String>();
		try {
			log.info("START: getSAacctno DAO voy_nbr:" + voy_nbr);
			sql = "SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD =:voyNbr";
			paramMap.put("voyNbr", voy_nbr);
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vacctno.add("" + rs.getString(1));
			}
		} catch (Exception e) {
			log.info("Exception getSAacctno : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getSAacctno DAO END");
		}
		return vacctno;
	}

	@Override
	public String getBPacctnbr(String voy_nbr, String seqno) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String scheme = "";
		String acctnbr = "";
		try {
			log.info("START: getBPacctnbr DAO START voy_nbr:" + voy_nbr + "seqno:" + seqno);
			sql = "SELECT MIXED_SCHEME_ACCT_NBR FROM MANIFEST_DETAILS WHERE VAR_NBR=:voyNbr AND MFT_SEQ_NBR=:seqno";
			paramMap.put("voyNbr", voy_nbr);
			paramMap.put("seqno", seqno);
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				acctnbr = rs.getString(1);
			}
			if (acctnbr != null && !acctnbr.equals("") && !acctnbr.equals("null")) {
//				acctnbr = acctnbr;
			} else {
				scheme = getSchemeName(voy_nbr);
				if (scheme.equalsIgnoreCase("JLR")) {
					acctnbr = getVCactnbr(voy_nbr);
				}
				// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08>
				// else if (!scheme.equals("JLR") && !scheme.equals("JNL") &&
				// !scheme.equals("JBT")) {
				// add new scheme for LCT, 20.feb.11 by hpeng
				else if (!scheme.equalsIgnoreCase("JLR") && !scheme.equalsIgnoreCase("JNL") && !scheme.equalsIgnoreCase("JBT")
						&& !scheme.equalsIgnoreCase("JWP") && !scheme.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME)) {
					acctnbr = getABactnbr(voy_nbr);
				}
			}

		} catch (Exception e) {
			log.info("Exception getBPacctnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getBPacctnbr DAO END");
		}
		return acctnbr;
	}

	@Override
	public String getSchemeName(String voy_nbr) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String sql = "";
		String sch = "";
		try {
			log.info("START: getSchemeName DAO voy_nbr: " + voy_nbr);
			sql = "SELECT SCHEME FROM VESSEL_CALL WHERE VV_CD=:voyNbr";
			paramMap.put("voyNbr", voy_nbr);
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				sch = rs.getString(1);
			}

		} catch (Exception e) {
			log.info("Exception getSchemeName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getSchemeName DAO END");
		}
		return sch;
	}

	@Override
	public String getVCactnbr(String voy_nbr) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String sql = "";
		String bactnbr = "";
		try {
			log.info("START:getVCactnbr DAO voy_nbr:" + voy_nbr);
			sql = "SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD=:voyNbr ";
			paramMap.put("voyNbr", voy_nbr);
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				bactnbr = rs.getString(1);
			}
		} catch (Exception e) {
			log.info("Exception getVCactnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getVCactnbr DAO END");
		}
		return bactnbr;
	}

	@Override
	public String getABactnbr(String voy_nbr) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String sql = "";
		String bactnbr = "";
		try {
			log.info("START:getABactnbr DAO voy_nbr:" + voy_nbr);
			sql = "SELECT VS.ACCT_NBR FROM VESSEL_CALL VC,VESSEL_SCHEME VS WHERE VC.SCHEME=VS.SCHEME_CD AND VC.VV_CD =:voyNbr";
			paramMap.put("voyNbr", voy_nbr);
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				bactnbr = rs.getString(1);
			}
		} catch (Exception e) {
			log.info("Exception getABactnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getABactnbr DAO END");
		}
		return bactnbr;
	}

	@Override
	public List<ManifestValueObject> getABacctno(String voy_nbr) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String sql = "";
		List<ManifestValueObject> vacctno = new ArrayList<ManifestValueObject>();
		try {
			log.info("START: getABacctno DAO START");
			sql = "SELECT SCHEME_CD,ACCT_NBR FROM VESSEL_SCHEME WHERE AB_CD IS NOT NULL AND REC_STATUS ='A'";
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				ManifestValueObject mftvobj = new ManifestValueObject();
				mftvobj.setCrgType("" + rs.getString(1));
				mftvobj.setCrgDesc("" + rs.getString(2));
				vacctno.add(mftvobj);
			}
		} catch (Exception e) {
			log.info("Exception getABacctno : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getABacctno DAO END");
		}
		return vacctno;
	}

	@Override
	public List<ManifestValueObject> getABacctnoForSA(String voy_nbr) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuilder sql = new StringBuilder();
		List<ManifestValueObject> vacctno = new ArrayList<ManifestValueObject>();
		try {
			log.info("START: getABacctnoForSA DAO voy_nbr:" + voy_nbr);
			sql.append(
					"SELECT VSL.SCHEME_CD, VSL.ACCT_NBR FROM VESSEL_SCHEME VSL, NOMINATED_SCHEME NOM WHERE VSL.SCHEME_CD = NOM.SCHEME_CD");
			sql.append("   AND NOMINATE_STATUS = 'APP' AND AB_CD IS NOT NULL AND NOM.VV_CD=:voyNbr  ");
			sql.append(" AND VSL.REC_STATUS ='A'");
			paramMap.put("voyNbr", voy_nbr);
			log.info("SQL" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				ManifestValueObject mftvobj = new ManifestValueObject();
				mftvobj.setCrgType("" + rs.getString(1));
				mftvobj.setCrgDesc("" + rs.getString(2));

				vacctno.add(mftvobj);
			}
		} catch (Exception e) {
			log.info("Exception getABacctnoForSA : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getABacctnoForSA DAO END");
		}
		return vacctno;
	}

	@Override
	public void MftAssignBillUpdate(String voy_nbr, String acctnbr, String seqno, String userid) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sql = new StringBuilder();
		StringBuilder sqltlog = new StringBuilder();
		StringBuilder strInsert_trans = new StringBuilder();
		int stransno = 0;
		int count_trans = 0;
		try {
			log.info("START: MftAssignBillUpdate DAO voy_nbr:" + voy_nbr + "acctnbr:" + acctnbr + "seqno:" + seqno
					+ "userid:" + userid);
			sql.append(" UPDATE manifest_details SET MIXED_SCHEME_ACCT_NBR=:acctnbr, ");
			sql.append("  LAST_MODIFY_DTTM =SYSDATE ,LAST_MODIFY_USER_ID=:userid ");
			sql.append(" WHERE VAR_NBR=:voyNbr AND mft_seq_nbr=:seqno ");
			
			boolean bactnbr = checkAccountNbr(acctnbr);
			if (!bactnbr) {
				log.info("Writing from ManifestEJB.MftAssignBillUpdate");
				log.info("Invalid Account Nbr" + acctnbr);
				throw new BusinessException("M20801");
			}
			paramMap.put("acctnbr", acctnbr);
			paramMap.put("userid", userid);
			paramMap.put("voyNbr", voy_nbr);
			paramMap.put("seqno", seqno);
			log.info("SQL" + sql.toString());
			int count = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			sqltlog.append(" SELECT MAX(TRANS_NBR) FROM MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:seqno ");
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
				paramMap.put("seqno", seqno);
				log.info("SQL" + sqltlog.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}
			strInsert_trans.append(" INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,VAR_NBR, ");
			strInsert_trans.append(" MIXED_SCHEME_ACCT_NBR,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)  ");
			strInsert_trans.append(" VALUES(:stransno,:seqno,:voy_nbr,:acctnbr,:userid,SYSDATE) ");
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				paramMap.put("stransno", stransno);
				paramMap.put("seqno", seqno);
				paramMap.put("voy_nbr", voy_nbr);
				paramMap.put("acctnbr", acctnbr);
				paramMap.put("userid", userid);
				log.info("SQL" + strInsert_trans.toString());
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans.toString(), paramMap);
			}

			if (count == 0) {
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
				if (count_trans == 0) {
					log.info("Writing from ManifestEJB.MftAssign Bill Party");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}

		} catch (BusinessException e) {
			log.info("Exception MftAssignBillUpdate : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftAssignBillUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: MftAssignBillUpdate DAO");
		}

	}

	private boolean checkAccountNbr(String accnbr) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		String straccnbrcount = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		int intaccnbrcount = 0;
		try {
			log.info("START: checkAccountNbr DAO accnbr" + accnbr);
			sql.append(" SELECT COUNT(*) FROM CUST_ACCT A, COMPANY_CODE B, ");
			sql.append(" CUSTOMER C  WHERE A.BUSINESS_TYPE LIKE '%G%' AND ");
			sql.append(" A.ACCT_NBR IS NOT NULL AND B.CO_CD=C.CUST_CD  AND A.ACCT_STATUS_CD='A' AND ");
			sql.append(" UPPER(A.ACCT_NBR)=UPPER(:accnbr) ");
			paramMap.put("accnbr", accnbr);
			log.info("SQL" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				straccnbrcount = CommonUtility.deNull(rs.getString(1));
			}
			if (((straccnbrcount).trim().equalsIgnoreCase("")) || straccnbrcount == null) {
				straccnbrcount = "0";
			}
			intaccnbrcount = Integer.parseInt(straccnbrcount);

		} catch (Exception e) {
			log.info("Exception checkAccountNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("checkAccountNbr DAO END");
		}
		if (intaccnbrcount > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void MftAssignVslUpdate(String voy_nbr, String status, String userid) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		try {
			log.info("START:MftAssignVslUpdate DAO voy_nbr:" + voy_nbr + "status:" + status + "userid:" + userid);
			sql.append("  UPDATE vessel_call SET mixed_scheme_ind=:status, ");
			sql.append(" LAST_MODIFY_DTTM=SYSDATE ,LAST_MODIFY_USER_ID=:userid ");
			sql.append(" WHERE VV_CD=:voy_nbr ");
			paramMap.put("status", status);
			paramMap.put("userid", userid);
			paramMap.put("voy_nbr", voy_nbr);
			log.info("SQL" + sql.toString());
			int count = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			if (count == 0) {
				log.info("Writing from ManifestEJB.MftAssignVesselUpdate");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
		} catch (BusinessException e) {
			log.info("Exception MftAssignVslUpdate : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftAssignVslUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:MftAssignVslUpdate");
		}
	}

	@Override
	public List<ManifestCargoValueObject> getMftAssignCargo() throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String cc_cd = null;
		String cicos_cd = null;
		String cc_name = null;
		List<ManifestCargoValueObject> maniveclist = new ArrayList<ManifestCargoValueObject>();
		String sql = "";
		try {
			log.info("START:getMftAssignCargo DAO");
			sql = " SELECT cc_cd, cicos_cd,cc_name FROM cargo_category_code where cc_status='A' ";
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				ManifestCargoValueObject mftcrg = new ManifestCargoValueObject();
				cc_cd = rs.getString(1);
				cicos_cd = rs.getString(2);
				if (cc_cd.equals("00")) {
					cicos_cd = "G";
				}
				cc_name = rs.getString(3);

				mftcrg.setCc_cd(cc_cd);
				mftcrg.setCc_name(cc_name);
				mftcrg.setCicos_cd(cicos_cd);

				maniveclist.add(mftcrg);
			}
		} catch (Exception e) {
			log.info("Exception getMftAssignCargo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMftAssignCargo DAO");
		}
		return maniveclist;
	}

	@Override
	public String MftAssignCrgvalCheck(String voy_nbr, String seqno) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuilder sql = new StringBuilder();
		String crgCd = "";
		try {
			log.info("START: MftAssignCrgvalCheck DAO voy_nbr:" + voy_nbr + "seqno:" + seqno);
			sql.append(
					" SELECT CARGO_CATEGORY_CD FROM MANIFEST_DETAILS WHERE VAR_NBR=:voy_nbr and MFT_SEQ_NBR =:seqno ");
			paramMap.put("voy_nbr", voy_nbr);
			paramMap.put("seqno", seqno);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				crgCd = rs.getString("CARGO_CATEGORY_CD");
			}
		} catch (Exception e) {
			log.info("Exception MftAssignCrgvalCheck : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: MftAssignCrgvalCheck DAO");
		}
		return crgCd;
	}

	@Override
	public void MftAssignCargoCategoryCargoTypeUpdate(String voy_nbr, String cargoCategory, String cargoType,
			String seqno, String userid) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sql = new StringBuilder();
		String sqltlog = "";
		StringBuilder strInsert_trans = new StringBuilder();
		int stransno = 0;
		int count_trans = 0;
		try {
			log.info("START:MftAssignCargoCategoryCargoTypeUpdate voy_nbr:" + voy_nbr + "cargoCategory:" + cargoCategory
					+ "cargoType:" + cargoType + "seqno:" + seqno + "userid:" + userid);
			sql.append(" UPDATE manifest_details SET CARGO_CATEGORY_CD =:cargoCategory ");
			sql.append(
					" , CRG_TYPE =:cargoType , LAST_MODIFY_DTTM =SYSDATE ,LAST_MODIFY_USER_ID=:userid WHERE VAR_NBR=:voy_nbr AND  mft_seq_nbr =:seqno");
			paramMap.put("cargoCategory", cargoCategory);
			paramMap.put("cargoType", cargoType);
			paramMap.put("userid", userid);
			paramMap.put("voy_nbr", voy_nbr);
			paramMap.put("seqno", seqno);
			int count = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			sqltlog = "SELECT MAX(TRANS_NBR) FROM MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:seqno";
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 23/5/2002
				paramMap.put("seqno", seqno);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);
				log.info("SQL" + sqltlog.toString());
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
				strInsert_trans.append(" INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,VAR_NBR, ");
				strInsert_trans.append(" CARGO_CATEGORY_CD, CRG_TYPE, LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				strInsert_trans.append(" VALUES(:stransno,:seqno,:voy_nbr,:cargoCategory,:cargoType,:userid,SYSDATE) ");

				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					paramMap.put("stransno", stransno);
					paramMap.put("seqno", seqno);
					paramMap.put("voy_nbr", voy_nbr);
					paramMap.put("cargoCategory", cargoCategory);
					paramMap.put("cargoType", cargoType);
					paramMap.put("userid", userid);
					log.info("SQL" + strInsert_trans.toString());
					count_trans = namedParameterJdbcTemplate.update(strInsert_trans.toString(), paramMap);
				}
				if (count == 0) {
					throw new BusinessException("M4201");

				}
				if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
					if (count_trans == 0) {
						throw new BusinessException("M4201");
					}
				}
			}
		} catch (BusinessException e) {
			log.info("Exception MftAssignCargoCategoryCargoTypeUpdate : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftAssignCargoCategoryCargoTypeUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("MftAssignCargoCategoryCargoTypeUpdate DAO");
		}

	}
	// EndRegion

	@Override
	public List<MiscDetail> getCargoSelectionList(Criteria criteria) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<MiscDetail> list = null;
		try {
			log.info(" START:getCargoSelectionList DAO  criteria:" + criteria.toString());
			String hsCde = criteria.getPredicates().get("hsCde");
			String hsSubCdeTo = criteria.getPredicates().get("hsSubCdeTo");
			String hsSubCdeFm = criteria.getPredicates().get("hsSubCdeFrm");
			sb.append(" SELECT ");
			sb.append("	MISC_TYPE_CD AS typeCode, ");
			sb.append("	MISC_TYPE_NM  AS typeValue ");
			sb.append(" FROM ");
			sb.append("	TOPS.SYSTEM_CONFIG ");
			sb.append(" WHERE ");
			sb.append("	CAT_CD = 'CARGO_SELECTION' ");
			sb.append("	AND TRIM(REGEXP_SUBSTR(MISC_TYPE_CD, '[^.]+$')) =:hsCde");
			paramMap.put("hsCde", hsCde + hsSubCdeFm + hsSubCdeTo);
			log.info("SQL" + sb.toString() + "paramMap:" + paramMap.toString());
			list = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<MiscDetail>(MiscDetail.class));
			log.info("list:" + list.toString());
		} catch (Exception e) {
			log.info("Exception getCargoSelectionList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoSelectionList DAO");
		}
		return list;

	}

	// EndRegion

	// Region NEW FEATURES
	// NEW Features Addition BY MCC
	@Override
	public String getTimeStamp() throws BusinessException {
		String ts = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info(" START getTimeStamp() ###");
			String sql = "select TO_CHAR(SYSDATE, 'dd-mm-yyyy hh24:mi:ss') from dual";
			ts = namedParameterJdbcTemplate.queryForObject(sql, paramMap, String.class);
			log.info("getTimeStamp :" + ts);
		} catch (Exception e) {
			log.info("Exception getCargoSelectionList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getTimeStamp");
		}
		return ts;
	}

	@Override
	public CargoManifestFileUploadDetails getCargoManifestFileUploadDetails(String seq_id) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		CargoManifestFileUploadDetails fileDetails = null;
		try {
			log.info("START getCargoManifestFileUploadDetails :" + seq_id);
			Map<String, String> paramMap = new HashMap<String, String>();
			sb.append("SELECT ");
			sb.append(" VV_CD as vv_cd, ");
			sb.append(" ASSIGNED_FILE_NM as assigned_file_name, ");
			sb.append("	ACTUAL_FILE_NM as actual_file_name, ");
			sb.append(" PROCESSED_FILE_NM as output_file_name ");
			sb.append(" FROM gbms.MANIFEST_UPLOAD_DETAILS ");
			sb.append(" WHERE MFT_UPLOAD_SEQ_NBR = :seq_id");
			paramMap.put("seq_id", seq_id);
			log.info("getCargoManifestFileUploadDetails :" + "SQL:" + sb.toString() + ", paramap: "
					+ paramMap.toString());
			fileDetails = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
					new BeanPropertyRowMapper<CargoManifestFileUploadDetails>(CargoManifestFileUploadDetails.class));
			log.info("getCargoManifestFileUploadDetails :" + fileDetails.toString());
		} catch (Exception e) {
			log.info("Exception getCargoManifestFileUploadDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCargoManifestFileUploadDetails ");
		}
		return fileDetails;
	}

	@Override
	public Long insertManifestExcelDetails(CargoManifestFileUploadDetails cargoManifestFileUploadDetails) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		String seqNbr = "0L";
		try {

			sb.append("INSERT INTO GBMS.MANIFEST_UPLOAD_DETAILS ");
			sb.append(
					"( ACTUAL_FILE_NM, VV_CD, ASSIGNED_FILE_NM,TYPE_CD, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM,SUMMARY_DESC)");
			sb.append("VALUES( :actual_file_name, :vv_cd, :assigned_file_name,:updateTypeCd, ");
			sb.append(
					":last_modified_user_id, TO_TIMESTAMP(:last_modified_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'),:remarks)");
			KeyHolder keyHolder = new GeneratedKeyHolder();
			namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(cargoManifestFileUploadDetails), keyHolder,
					new String[] { "MFT_UPLOAD_SEQ_NBR" });
			seqNbr = keyHolder.getKey() != null ? keyHolder.getKey().toString() : "0";
			log.info("insertManifestDetails " + cargoManifestFileUploadDetails.toString());
		} catch (Exception e) {
			log.info("Exception insertManifestExcelDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertManifestExcelDetails ");
		}
		return Long.parseLong(seqNbr);
	}

	@Override
	public boolean updateManifestExcelDetails(Long seq_id, String output_file_name) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		boolean update = false;
		try {
			log.info("START updateManifestExcelDetails : " + "seq_id:" + seq_id + "output_file_name:"
					+ output_file_name);
			sb.append("UPDATE GBMS.MANIFEST_UPLOAD_DETAILS ");
			sb.append("SET ");
			sb.append("PROCESSED_FILE_NM =:output_file_name ");
			sb.append("WHERE MFT_UPLOAD_SEQ_NBR=:seq_id");
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("output_file_name", output_file_name);
			paramMap.put("seq_id", seq_id);
			log.info("updateManifestExcelDetails: " + "SQL:" + sb.toString() + "Params:" + paramMap.toString());
			int rows = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("updateManifestExcelDetails:rows " + rows);
			update = true;
		} catch (Exception e) {
			log.info("Exception updateManifestExcelDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END updateManifestExcelDetails");
		}
		return update;
	}

	@Override
	public List<ManifestUploadConfig> getTemplateHeader() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<ManifestUploadConfig> manifestUploadConfig = null;
		try {
			log.info("START getTemplateHeader");
			sb.append(
					"SELECT MFT_UPLOAD_CONFIG_ID,ATTR_NM attr_name,ATTR_DESC,INPUT_TYPE,DISPLAY_SEQ,MANDATORY_IND,LOOKUP_TABLE,");
			sb.append("LOOKUP_CAT_CD,COLUMN_NM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM ");
			sb.append("FROM GBMS.MANIFEST_UPLOAD_CONFIG WHERE TYPE_CD ='M' ORDER BY DISPLAY_SEQ ASC");
			log.info("getTemplateHeader :" + "SQL:" + sb.toString());
			manifestUploadConfig = namedParameterJdbcTemplate.query(sb.toString(),
					new BeanPropertyRowMapper<ManifestUploadConfig>(ManifestUploadConfig.class));
			log.info("getTemplateHeader : size:" + manifestUploadConfig.size());
		} catch (Exception e) {
			log.info("Exception getTemplateHeader : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getTemplateHeader");
		}
		return manifestUploadConfig;
	}

	@Override
	public List<ManifestUploadConfig> getHatchTemplate() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<ManifestUploadConfig> manifestUploadConfig = null;
		try {
			log.info("START getHatchTemplate");
			sb.append(
					"SELECT MFT_UPLOAD_CONFIG_ID,ATTR_NM attr_name,ATTR_DESC,INPUT_TYPE,DISPLAY_SEQ,MANDATORY_IND,LOOKUP_TABLE,");
			sb.append("LOOKUP_CAT_CD,COLUMN_NM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM ");
			sb.append("FROM GBMS.MANIFEST_UPLOAD_CONFIG WHERE COLUMN_NM = 'AD' ORDER BY DISPLAY_SEQ ASC");
			log.info("getHatchTemplate :" + "SQL:" + sb.toString());
			manifestUploadConfig = namedParameterJdbcTemplate.query(sb.toString(),
					new BeanPropertyRowMapper<ManifestUploadConfig>(ManifestUploadConfig.class));
			log.info("getHatchTemplate :Size:" + manifestUploadConfig.size());
		} catch (Exception e) {
			log.info("Exception getHatchTemplate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getHatchTemplate ");
		}
		return manifestUploadConfig;
	}

	@Override
	public PageDetails getVesselCallDetails(String vvCd) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		PageDetails vesselCallDetails = null; 
		try {
			log.info("START getVesselCallDetails" + "Params:" + vvCd);
			sb.append(
					"SELECT VSL_NM AS vesselName ,VV_CD AS vvCd,IN_VOY_NBR AS inwardVoyNo, OUT_VOY_NBR as outVoyNo, IN_VOY_NBR ||'-'||OUT_VOY_NBR as voyageNo  FROM tops.VESSEL_CALL WHERE VV_CD=:vvCd");
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("vvCd", vvCd);
			log.info("getVesselCallDetails :" + "SQL:" + sb.toString() + "Param:" + paramMap.toString());
			vesselCallDetails = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
					new BeanPropertyRowMapper<PageDetails>(PageDetails.class));
			log.info("getVesselCallDetails : " + vesselCallDetails.toString());
		} catch (Exception e) {
			log.info("Exception getVesselCallDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getVesselCallDetails ");
		}
		return vesselCallDetails;
	}

	@Override
	public List<CargoManifest> getManifestDetails(String vvCd) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<CargoManifest> cargoManifestData = null;
		try {
			log.info("START getManifestDetails: Vvcd: " + vvCd);
			sb.append("	SELECT '");
			sb.append(ConstantUtil.action_NA + "' as action,");
			sb.append("	md.MFT_SEQ_NBR as mft_seq_nbr,");
			sb.append("			 'Add' AS actionType, ");
			sb.append("			 MD.BL_NBR AS bills_of_landing_no, ");
			sb.append(
					"			 (SELECT CRG_TYPE_CD||'-'||CRG_TYPE_NM FROM GBMS.CRG_TYPE ct WHERE ct.CRG_TYPE_CD = md.CRG_TYPE) AS cargoType, ");
			sb.append("			 MD.CRG_DES AS cargo_description, ");
			sb.append(
					"			 (SELECT mm.MFT_MARKINGS FROM GBMS.MFT_MARKINGS mm WHERE mm.MFT_SQ_NBR = md.MFT_SEQ_NBR) AS cargo_marking,");
			sb.append("			 CASE ");
			sb.append("			 WHEN md.HS_CODE IS NOT NULL");
			sb.append("			 AND md.HS_SUB_CODE_FR IS NOT NULL");
			sb.append("			 AND md.HS_SUB_CODE_TO IS NOT NULL THEN (");
			sb.append(
					"			 	SELECT  hsc.HS_CODE || '(' || hsc.HS_SUB_CODE_FR || '-' || hsc.HS_SUB_CODE_TO || ')'|| hsc.HS_SUB_DESC ");
			sb.append("			 FROM ");
			sb.append("			 	gbms.HS_SUB_CODE hsc");
			sb.append("			 WHERE ");
			sb.append("			 	hsc.HS_CODE = md.HS_CODE");
			sb.append("			 		AND hsc.HS_SUB_CODE_FR = md.HS_SUB_CODE_FR");
			sb.append("			 		AND hsc.HS_SUB_CODE_TO = md.HS_SUB_CODE_TO) ");
			sb.append("			 WHEN md.HS_CODE IS NOT NULL ");
			sb.append("			 AND md.HS_SUB_CODE_FR IS NULL ");
			sb.append("			 AND md.HS_SUB_CODE_TO IS NULL THEN md.HS_CODE");
			sb.append("			 END AS hs_code_sub_code,");

			sb.append("			( SELECT MISC_TYPE_NM  FROM GBMS.MANIFEST_DETAILS_EXT ext  ");
			sb.append(
					"			LEFT JOIN TOPS.SYSTEM_CONFIG conf ON  conf.CAT_CD='CARGO_SELECTION' AND conf.REC_STATUS ='A' AND ext.HS_SUB_DESC_CD= conf.MISC_TYPE_CD ");
			sb.append("			WHERE MFT_SEQ_NBR =MD.MFT_SEQ_NBR ) cargo_selection, ");

			sb.append("			 MD.NBR_PKGS AS number_of_packages, ");
			sb.append("			 MD.GROSS_WT AS gross_weight_kg, ");
			sb.append("			 MD.GROSS_VOL AS gross_measurement_m3, ");
			sb.append("			 CASE  ");
			sb.append("			 	WHEN MD.CRG_STATUS = 'L' THEN 'Local' ");
			sb.append("			 	WHEN MD.CRG_STATUS = 'T' THEN 'Transhipment' ");
			sb.append("			 END AS cargo_status, ");
			sb.append("			 MD.DG_IND AS dg_indicator, ");
			sb.append("			 CASE  ");
			sb.append("			 	WHEN MD.STG_TYPE = 'O' THEN 'Open' ");
			sb.append("			 	WHEN MD.STG_TYPE = 'C' THEN 'Covered' ");
			sb.append("			 END AS storage_indicator, ");
			sb.append(
					"			 (SELECT PKG_TYPE_CD||'-'||PKG_DESC FROM GBMS.PKG_TYPES pt WHERE pt.PKG_TYPE_CD =  md.PKG_TYPE) AS packing_type, ");
			sb.append("			  CASE  ");
			sb.append("			 	WHEN MD.DIS_TYPE = 'N' THEN 'Normal' ");
			sb.append("			 	WHEN MD.DIS_TYPE = 'D' THEN 'Direct' ");
			sb.append("			 	WHEN MD.DIS_TYPE = 'O' THEN 'Overside' ");
			sb.append("			 END AS discharge_operation_indicator, ");
			sb.append("			CASE WHEN MD.CONS_CO_CD = 'OTHERS' THEN ");
			sb.append("			'OTHERS' ELSE   ");
			sb.append("			(SELECT co_nm || ' (' || co_cd || ')' as co_nm FROM  tops.company_code WHERE  ");
			sb.append("			co_cd = MD.CONS_CO_CD ) END AS consignee , ");
			sb.append("			 MD.CONS_NM AS consignee_others, ");
			sb.append(
					"			 (SELECT PORT_CD FROM UN_PORT_CODE WHERE PORT_CD = MD.LD_PORT ) AS port_of_loading, ");
			sb.append(
					"			 (SELECT PORT_CD FROM UN_PORT_CODE WHERE PORT_CD = MD.DIS_PORT ) AS port_of_discharge, ");
			sb.append(
					"			 (SELECT PORT_CD FROM UN_PORT_CODE WHERE PORT_CD = MD.DES_PORT) AS port_of_final_destination ");
			sb.append(", md.CUSTOM_HS_CODE custom_hs_code, md.CONSIGNEE_ADDR consignee_addr, md.SHIPPER_NM shipper_nm");
			sb.append(", md.SHIPPER_ADDR shipper_addr, md.NOTIFY_PARTY notify_party, md.NOTIFY_PARTY_ADDR notify_party_addr");
			sb.append(", md.PLACE_OF_DELIVERY place_of_delivery, md.PLACE_OF_RECEIPT place_of_receipt,  ");
			
			// START SPLIT BL - NS Jan 2025
			sb.append("			 CASE  ");
			sb.append("			 WHEN md.SPLIT_ID IS NULL OR md.SPLIT_ID = 0 THEN '' ");
			sb.append("			 WHEN md.SPLIT_ID IS NOT NULL AND md.SPLIT_ID > 0 THEN md.SPLIT_MAIN_BL ");
			sb.append("			 END AS split_main_bl, ");
			sb.append("			 CASE  ");
			sb.append("			 WHEN md.SPLIT_ID IS NULL OR md.SPLIT_ID = 0 THEN 'No' ");
			sb.append("			 WHEN md.SPLIT_ID IS NOT NULL AND md.SPLIT_ID > 0 THEN 'Yes' ");
			sb.append("			 END AS split_bl_ind ");
			// END SPLIT BL - NS Jan 2025
			sb.append("		FROM gbms.MANIFEST_DETAILS MD WHERE VAR_NBR = :vvCd AND BL_STATUS!='X'");
			sb.append("		ORDER BY MD.BL_NBR ");

			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("vvCd", vvCd);
			log.info("getManifestDetails : SQL:" + sb.toString() + "Params:" + paramMap.toString());
			
			cargoManifestData = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<CargoManifest>(CargoManifest.class));
			log.info("getManifestDetails : size" + cargoManifestData.size());
		} catch (Exception e) {
			log.info("Exception getManifestDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getManifestDetails ");
		}
		return cargoManifestData;
	}

	@Override
	public List<String> getCargoTypeDropDown() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<String> cargoType = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START getCargoTypeDropDown");
			sb.append(
					"SELECT CRG_TYPE_CD||'-'||CRG_TYPE_NM FROM GBMS.CRG_TYPE WHERE CRG_TYPE_CD NOT IN ('00','01','02','03','06') and REC_STATUS='A'");
			log.info("getCargoTypeDropDown SQL: " + sb.toString());
			cargoType = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
			log.info("getCargoTypeDropDown : size" + cargoType.size());
		} catch (Exception e) {
			log.info("Exception getCargoTypeDropDown : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getCargoTypeDropDown ");
		}
		return cargoType;
	}

	@Override
	public List<String> getPackagingTypeDropDown() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<String> packagingType = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START getPackagingTypeDropDown");
			sb.append(
					"SELECT PKG_TYPE_CD||'-'||PKG_DESC FROM GBMS.PKG_TYPES WHERE REC_STATUS='A' ORDER BY PKG_TYPE_CD asc");
			log.info("getCargoTypeDropDown SQL: " + sb.toString());
			packagingType = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
			log.info("getPackagingTypeDropDown : size" + packagingType.size());
		} catch (Exception e) {
			log.info("Exception getPackagingTypeDropDown : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getPackagingTypeDropDown ");
		}
		return packagingType;
	}

	@Override
	public List<String> getDischargeTypeIndicatorDropdown() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<String> dischargeTypeIndicator = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START getDischargeTypeIndicatorDropdown");
			sb.append("SELECT DISTINCT DIS_TYPE FROM gbms.MANIFEST_DETAILS WHERE DIS_TYPE IS NOT NULL");
			log.info("getDischargeTypeIndicatorDropdown SQL: " + sb.toString());
			dischargeTypeIndicator = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
			log.info("getDischargeTypeIndicatorDropdown : size" + dischargeTypeIndicator.size());
		} catch (Exception e) {
			log.info("Exception getDischargeTypeIndicatorDropdown : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getDischargeTypeIndicatorDropdown ");
		}
		return dischargeTypeIndicator;
	}

	@Override
	public List<String> getHs_code_sub_code() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<>();
		List<HSCode> hsTempList = new ArrayList<>();
		List<String> hsCodeSubCode = new ArrayList<>();
		try {
			log.info("START getHs_code_sub_code");
			
			sb.append(" SELECT HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, HS_SUB_DESC ");
			sb.append(" FROM GBMS.HS_SUB_CODE ");
			sb.append(" WHERE REC_STATUS = '1' ");
			sb.append(" ORDER BY HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO");
			log.info("getHs_code_sub_code SQL : " + sb.toString());
			
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				HSCode hs = new HSCode();
				hs.setHsCode(CommonUtility.deNull(rs.getString("HS_CODE")));
				hs.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				hs.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				hs.setHsSubDesc(CommonUtility.deNull(rs.getString("HS_SUB_DESC")));
				hsTempList.add(hs);
			}
			log.info("Total HS records fetched Size : " + hsCodeSubCode.size() + " | HSCode : " + hsTempList);
			
			String filterValue = getHSCodesFiler();
			log.info("filterValue in getHs_code_sub_code: " + filterValue);
			
			if ("YES".equalsIgnoreCase(filterValue)) {
				List<String> filteringCodes = getHSCodesFilerValues();
				log.info("Filtering Code List: " + filteringCodes);
				
				Set<String> codesToRemove = filteringCodes.stream()
                    .flatMap(s -> Arrays.stream(s.split(",")))
                    .map(String::trim)
                    .collect(Collectors.toSet());
				log.info("Codes To Remove: " + codesToRemove);
				
				hsTempList.removeIf(hs ->
                    codesToRemove.contains(
                            hs.getHsCode() + "-"
                            + hs.getHsSubCodeFr() + "-"
                            + hs.getHsSubCodeTo()
                    )
				);
				log.info("HS records after filtering: " + hsTempList.size());
			}
			
			for (HSCode hs : hsTempList) {
				String formatted = hs.getHsCode() + "(" + hs.getHsSubCodeFr() + "-" + hs.getHsSubCodeTo() + ")" + hs.getHsSubDesc();
				hsCodeSubCode.add(formatted);
			}
		} catch (Exception e) {
			log.error("Exception in getHs_code_sub_code: ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getHs_code_sub_code | Size : " + hsCodeSubCode.size() + " | HSCode : " + hsCodeSubCode);
		}
		return hsCodeSubCode;
	}

	@Override
	public List<String> getConsigneee() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<String> consigneeeList = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START getConsigneee");
			sb.append(
					"SELECT co_nm || ' (' || co_cd || ')' as co_nm FROM  tops.company_code WHERE  rec_status='A'   ORDER BY co_nm  "); // AND allow_jponline='Y'
			consigneeeList = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
		} catch (Exception e) {
			log.info("Exception getConsigneee : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getConsigneee ");
		}
		return consigneeeList;
	}

	@Override
	public List<String> getPortListForExcelProcessing(boolean withName) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<String> getPortList = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START getPortList withName:" + withName);
			sb.append("SELECT ");
			if(withName) {
				sb.append("PORT_CD||'-'||PORT_NM");
			} else {
				sb.append("PORT_CD");
			}
			sb.append(" FROM UN_PORT_CODE WHERE REC_STATUS='A' ORDER BY PORT_CD ASC"); // rownum <= 5
			getPortList = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
		} catch (Exception e) {
			log.info("Exception getPortList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getPortList ");
		}
		return getPortList;
	}

	@Override
	public int noOfHatchesOnPageLoad(String varCode) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		int noOfHatch = 0;
		try {
			log.info(" START:*** noOfHatchesOnPageLoad DAO Start varCode: *****" + varCode);
			sb.append(" SELECT  ");
			sb.append(" NVL((SELECT E.CARGO_HOLD_NUM FROM TOPS.VESSEL_EXT E WHERE E.VSL_NM=C.VSL_NM),0) noOfHatches ");
			sb.append(" FROM TOPS.VESSEL_CALL C ");
			sb.append(" WHERE C.VV_CD=:varCode ");
			log.info(" noOfHatchesOnPageLoad SQL  " + sb.toString() + "Params" + paramMap.toString());
			paramMap.put("varCode", varCode);
			noOfHatch = (Integer) namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
			log.info("noOfHatchesOnPageLoad : count:" + noOfHatch);
		} catch (Exception e) {
			log.info("Exception noOfHatchesOnPageLoad : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: ****** noOfHatchesOnPageLoad End result: ********** " + noOfHatch);
		}
		return noOfHatch;

	}

	@Override
	public List<HatchDetails> getManifestHatchDetails(String vvCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		List<HatchDetails> manifestHatchDtlList = new ArrayList<HatchDetails>();
		try {

			sb.append("SELECT ");
			sb.append(" DET.var_nbr,");
			sb.append("	H.MFT_HATCH_SEQ_NBR AS mft_hatch_seq_nbr, ");
			sb.append("	H.MFT_SEQ_NBR AS mft_seq_nbr, ");
			sb.append("	H.HATCH_CD AS hatch_cd, ");
			sb.append("	H.NBR_PKGS AS nbr_pkgs, ");
			sb.append("	H.GROSS_WT AS gross_wt, ");
			sb.append("	H.GROSS_VOL AS gross_vol, ");
			sb.append("	DET.BL_NBR AS billNo, ");
			sb.append(" DET.CRG_DES AS cargoDesc ");
			sb.append("FROM ");
			sb.append("	GBMS.MANIFEST_HATCH_DETAILS H ");
			sb.append("LEFT JOIN GBMS.MANIFEST_DETAILS DET ON ");
			sb.append("	H.MFT_SEQ_NBR = DET.MFT_SEQ_NBR ");
			sb.append("WHERE ");
			sb.append("	DET.VAR_NBR =:vvCd ");
			sb.append("		ORDER BY DET.BL_NBR ");
			paramMap.put("vvCd", vvCd);
			log.info("getManifestHatchDetails SQL" + sb.toString() + "parammap" + paramMap.toString());
			manifestHatchDtlList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<HatchDetails>(HatchDetails.class));
			log.info("getManifestHatchDetails response " + manifestHatchDtlList.toString());
		} catch (Exception e) {
			log.info("Exception getManifestHatchDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getManifestHatchDetails");
		}
		return manifestHatchDtlList;
	}

	@Override
	public List<MiscDetail> getCargoSelectionDropdown() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<>();
		List<MiscDetail> cargoSelection = null;
		try {
			log.info("START : getCargoSelectionDropdown");
			sb.append(
					"SELECT MISC_TYPE_CD typeCode, MISC_TYPE_NM typeValue from TOPS.SYSTEM_CONFIG where CAT_CD='CARGO_SELECTION' AND REC_STATUS ='A'");
			log.info("getCargoSelectionDropdown SQL:" + sb.toString());
			cargoSelection = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<MiscDetail>(MiscDetail.class));
			log.info("getCargoSelectionDropdown size : " + cargoSelection.size());
		} catch (Exception e) {
			log.info("Exception getCargoSelectionDropdown : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCargoSelectionDropdown");
		}
		return cargoSelection;
	}

	@Override
	public PageDetails manifestUploadDetail(String vvCd) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		PageDetails pageDetails = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START getPageDetails : VVCd:" + vvCd);
			pageDetails = getVesselCallDetails(vvCd);
			sb.append(
					"SELECT  REMARKS FROM tops.SYSTEM_CONFIG sc WHERE CAT_CD ='MANIFEST_UPLOAD_INST' AND REC_STATUS ='A'");
			log.info("getPageDetails   : SQL:" + sb.toString() + "Params:" + vvCd);
			List<String> instructions = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
			log.info("getPageDetails size:" + instructions.size());
			pageDetails.setInstructions(instructions);
			Template template = new Template();
			template.setFileName(ConstantUtil.manifest + ConstantUtil.file_ext);
			template.setRefId(vvCd);
			template.setRefType(ConstantUtil.manifest_type_cd);
			template.setIsSplitBL(false);
			List<Template> templateDet = new ArrayList<Template>();
			templateDet.add(template);
			
			// Add split BL template - NS Jan 2025
			template = new Template();
			template.setFileName(ConstantUtil.splitBl + ConstantUtil.file_ext);
			template.setRefId(vvCd);
			template.setRefType(ConstantUtil.manifest_type_cd);
			template.setIsSplitBL(true);
			templateDet.add(template);
			
			
	        // START CR Remove Packaging template - NS NOV 2023 
			//	Template template_pkg = new Template();
			//	template_pkg.setFileName(ConstantUtil.packaging_download_filename + ConstantUtil.file_ext);
			//	template_pkg.setRefId(vvCd);
			//	template_pkg.setRefType(ConstantUtil.packaging_type_cd);
	        // END CR Remove Packaging template - NS NOV 2023 

			
			// templateDet.add(template_pkg);
			pageDetails.setTemplate(templateDet);
		} catch (Exception e) {
			log.info("Exception getPageDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getPageDetails");
		}
		return pageDetails;
	}

	@Override
	public boolean insertManifest_action_trial(String vvCd, String typeCd, String summary, String lastTimestamp,
			String userId) throws BusinessException {
		StringBuffer sb_insert = new StringBuffer();
		ManifestActionTrail manifestActionTrl = new ManifestActionTrail();
		boolean insert = false;
		try {
			log.info("START insertManifest_action_trial :  vvCd:" + vvCd + "typeCd:" + typeCd + "summary:" + summary
					+ "lastTimestamp:" + lastTimestamp + "userId:" + userId);

			String last_modified_user_id = userId;
			sb_insert.append("INSERT INTO GBMS.MANIFEST_ACT_TRL");
			sb_insert.append("( VV_CD, TYPE_CD, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, REMARKS)");
			sb_insert.append(
					"VALUES( :vv_cd, :type_cd, :last_modify_user_id,  TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'),:remarks)");

			// manifestActionTrl.setMft_act_trl_id(mft_act_trl_id);
			manifestActionTrl.setVv_cd(vvCd);
			manifestActionTrl.setType_cd(typeCd);
			manifestActionTrl.setLast_modify_user_id(last_modified_user_id);
			manifestActionTrl.setLast_modify_dttm(lastTimestamp);
			manifestActionTrl.setRemarks(summary);
			log.info(
					"insertManifest_action_trial:SQL" + manifestActionTrl.toString() + "SQL : " + sb_insert.toString());
			int rows = namedParameterJdbcTemplate.update(sb_insert.toString(),
					new BeanPropertySqlParameterSource(manifestActionTrl));
			log.info("insertManifest_action_trial:" + rows);
			insert = true;
		} catch (Exception e) {
			log.info("Exception insertManifest_action_trial : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertManifest_action_trial");
		}
		return insert;
	}

	@Override
	public TableResult getManifestActionTrial(Criteria criteria) throws BusinessException {
		TableResult result = new TableResult();
		StringBuffer sb = new StringBuffer();
		String sql = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<ManifestActionTrail> info = new ArrayList<ManifestActionTrail>();
		try {

			String vvCd = criteria.getPredicates().get("vvCd");
			String type = criteria.getPredicates().get("type"); // actionTrailType actionTrailTypeExcelUpload = "EU"; //
																// actionTrailTypeHBD = "HBD";
			String typeCd = criteria.getPredicates().get("typeCd");
			String transDate = criteria.getPredicates().get("transDate");
			String actionBy = criteria.getPredicates().get("actionBy");
			String summary = criteria.getPredicates().get("summary");
			int start = criteria.getStart();
			int limit = criteria.getLimit();

			log.info("START getManifestActionTrial:" + "vvCd:" + vvCd + "transDate:" + transDate + "actionBy:"
					+ actionBy + "summary:" + summary + "start:" + start + "limit:" + limit);
			TableData tableData = new TableData();
			sb.append("SELECT trl.MFT_ACT_TRL_ID, trl.VV_CD, trl.TYPE_CD, ");
			// sb.append(" ac.login_id userid ,");
			sb.append(
					"CASE WHEN ac.user_nm IS NOT NULL THEN  CONCAT (CONCAT (ac.user_nm, ' - '), ac.CUST_CD) ELSE '' END last_modify_user_id , ");
			// sb.append(" ac.user_nm last_modify_user_id ,");
			sb.append(
					" TO_CHAR(trl.LAST_MODIFY_DTTM,'DD-MM-YYYY HH24:MI') LAST_MODIFY_DTTM, trl.REMARKS, CASE WHEN trl.TYPE_CD='M' Then 'Manifest' ");
			sb.append(" WHEN trl.TYPE_CD='P' Then 'Packaging' WHEN trl.TYPE_CD='S' Then 'Manifest Split BL'  else '' END TYPE ");
			sb.append("FROM GBMS.MANIFEST_ACT_TRL trl  ");
			//sb.append(" LEFT JOIN TOPS.logon_acct ac ON ac.login_id= trl.LAST_MODIFY_USER_ID  ");
			sb.append(" LEFT JOIN TOPS.logon_acct ac ON ac.login_id= SUBSTR(trl.LAST_MODIFY_USER_ID, INSTR( trl.LAST_MODIFY_USER_ID, '/', -1 ) + 1 )   ");
			if (type.equalsIgnoreCase("EU")) {
				sb.append("JOIN GBMS.MANIFEST_UPLOAD_DETAILS mfd ON  mfd.LAST_MODIFY_DTTM = trl.LAST_MODIFY_DTTM ");
			}
			sb.append(" WHERE trl.VV_CD = :vvCd  ");
			if (type.equalsIgnoreCase("HBD")) {
				sb.append(" AND trl.TYPE_CD= :typeCd ");
			}
			sb.append("ORDER BY trl.LAST_MODIFY_DTTM DESC");

			paramMap.put("vvCd", vvCd);
			paramMap.put("typeCd", typeCd);
			sql = sb.toString();
			tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")", paramMap,
					Integer.class));
			sql = CommonUtil.getPaginatedSql(sql, start, limit);
			info = namedParameterJdbcTemplate.query(sql, paramMap,
					new BeanPropertyRowMapper<ManifestActionTrail>(ManifestActionTrail.class));
			log.info("getManifestActionTrial SQL" + sql.toString() + "Params:" + paramMap.toString());
			TopsModel topsModel = new TopsModel();
			for (ManifestActionTrail object : info) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			result.setSuccess(true);
			result.setData(tableData);
		} catch (Exception e) {
			log.info("Exception getManifestActionTrial : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getManifestActionTrial");
		}
		return result;
	}

	@Override
	public ManifestActionTrailDetails getManifestActionTrialDetail(String mft_act_trl_id) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		ManifestActionTrailDetails manifest_trail_details = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START getManifestActionTrialDetail " + "Params:" + mft_act_trl_id);
			sb.append("	SELECT ");
			sb.append("		mfd.MFT_UPLOAD_SEQ_NBR as seq_id, ");
			sb.append("		mfd.ACTUAL_FILE_NM as actual_file_name, ");
			sb.append("		mfd.ASSIGNED_FILE_NM as assigned_file_name, ");
			sb.append("		mfd.VV_CD as vv_cd, ");
			sb.append("		mfd.PROCESSED_FILE_NM AS output_file_name, ");
			// sb.append(" mhat.LAST_MODIFY_DTTM as last_modified_dttm, ");
			sb.append("		TO_CHAR(mhat.LAST_MODIFY_DTTM, 'DD-MM-YYYY HH24:MI') last_modified_dttm, ");
			sb.append(
					" CASE WHEN ac.user_nm IS NOT NULL THEN  CONCAT (CONCAT (ac.user_nm, ' - '), ac.CUST_CD) ELSE '' END last_modified_user_id , ");
			// sb.append(" ac.user_nm last_modify_user_id ,");
			// sb.append(" as last_modified_user_id, ");
			sb.append("		mhat.REMARKS  as remarks");
			sb.append("	FROM ");
			sb.append("		gbms.MANIFEST_ACT_TRL mhat ");
			sb.append(
					" LEFT JOIN gbms.MANIFEST_UPLOAD_DETAILS  mfd ON mhat.LAST_MODIFY_DTTM = mfd.LAST_MODIFY_DTTM AND mhat.VV_CD = mfd.VV_CD  ");
			sb.append(" LEFT JOIN TOPS.logon_acct ac ON ac.login_id= SUBSTR(mhat.LAST_MODIFY_USER_ID, INSTR( mhat.LAST_MODIFY_USER_ID, '/', -1 ) + 1 ) ");
			sb.append("	WHERE ");
			sb.append("		mhat.MFT_ACT_TRL_ID = :mft_act_trl_id  ");
			paramMap.put("mft_act_trl_id", mft_act_trl_id);
			log.info("getManifestActionTrialDetail SQL:" + sb.toString() + "param:" + mft_act_trl_id);
			manifest_trail_details = namedParameterJdbcTemplate.queryForObject(sb.toString(),
					paramMap, new BeanPropertyRowMapper<ManifestActionTrailDetails>(ManifestActionTrailDetails.class));
			Pattern p = Pattern.compile("-?\\d+");
			Matcher m = p.matcher(manifest_trail_details.getRemarks());
			int mIndex = 0;
			while (m.find()) {
				if (mIndex == 0) {
					manifest_trail_details.setTotalLineProcessed(m.group());
				} else if (mIndex == 1) {
					manifest_trail_details.setTotalSuccess(m.group());
				} else if (mIndex == 2) {
					manifest_trail_details.setTotalFail(m.group());
				}
				mIndex++;
			}
		} catch (Exception e) {
			log.info("Exception getManifestActionTrialDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getManifestActionTrialDetail");
		}
		return manifest_trail_details;
	}

	// EndRegion

	// Region Excel for Manifest Details
	@Override
	public int mainfestDetailIsExist(String bl_nbr, String vvcd) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		try {
			log.info("START mainfestDetailIsExist for vvcd :" + vvcd + " , bl_nbr :" + bl_nbr);
			sb.append(
					"SELECT COUNT(MFT_SEQ_NBR) FROM GBMS.MANIFEST_DETAILS md WHERE VAR_NBR=:vvcd AND BL_NBR = :bl_nbr AND BL_STATUS ='A'");
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("bl_nbr", bl_nbr);
			paramMap.put("vvcd", vvcd);
			log.info("mainfestDetailIsExist " + sb.toString() + ", paramap :" + paramMap.toString());
			count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
		} catch (Exception e) {
			log.info("Exception mainfestDetailIsExist : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END mainfestDetailIsExist for vvcd :" + vvcd + " result :" + count);
		}
		return count;
	}
	
	@Override
	public CargoManifest getMainfestDetails(String bl_nbr, String vvcd) throws BusinessException {
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		CargoManifest cm = null;
		try {
			log.info("START getMainfestDetails for vvcd :" + vvcd + " , bl_nbr :" + bl_nbr);
			sb.append("SELECT MFT_SEQ_NBR mft_seq_nbr, NBR_PKGS number_of_packages, GROSS_VOL gross_measurement_m3, DG_IND,");
			sb.append("(SELECT mm.MFT_MARKINGS FROM GBMS.MFT_MARKINGS mm WHERE mm.MFT_SQ_NBR = md.MFT_SEQ_NBR) AS cargo_marking,");
			sb.append("( SELECT MISC_TYPE_NM  FROM GBMS.MANIFEST_DETAILS_EXT ext  ");
			sb.append(" LEFT JOIN TOPS.SYSTEM_CONFIG conf ON  conf.CAT_CD='CARGO_SELECTION'");
			sb.append(" AND conf.REC_STATUS ='A' AND ext.HS_SUB_DESC_CD= conf.MISC_TYPE_CD ");
			sb.append(" WHERE MFT_SEQ_NBR =MD.MFT_SEQ_NBR ) cargo_selection, ");
			sb.append("HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, CUSTOM_HS_CODE, CRG_DES, BL_NBR,");
			sb.append("(HS_CODE ||'-'|| HS_SUB_CODE_FR || '-' || HS_SUB_CODE_TO) HS_CODE_SUB_CODE, ");
			sb.append("DIS_TYPE, CRG_TYPE,CRG_DES,STG_TYPE,CRG_STATUS,PKG_TYPE,");
			sb.append("LD_PORT,DIS_PORT,DES_PORT,CONS_NM,CONS_CO_CD,");
			sb.append("CONSIGNEE_ADDR,SHIPPER_NM,SHIPPER_ADDR,NOTIFY_PARTY,");
			sb.append("NOTIFY_PARTY_ADDR,PLACE_OF_DELIVERY,PLACE_OF_RECEIPT,");
			sb.append("GROSS_WT gross_weight_kg FROM GBMS.MANIFEST_DETAILS md WHERE VAR_NBR=:vvcd ");
			sb.append("AND BL_NBR = :bl_nbr AND BL_STATUS ='A'");
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("bl_nbr", bl_nbr);
			paramMap.put("vvcd", vvcd);
			log.info("getMainfestDetails " + sb.toString() + ", paramap :" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if(rs.next()) {
				cm = new CargoManifest();
				cm.setNumber_of_packages(rs.getString("number_of_packages"));
				cm.setGross_measurement_m3(rs.getString("gross_measurement_m3"));
				cm.setGross_weight_kg(rs.getString("gross_weight_kg"));
				cm.setMft_seq_nbr(rs.getString("mft_seq_nbr"));
				cm.setHs_code_sub_code(rs.getString("HS_CODE_SUB_CODE").trim());
				cm.setHs_code(rs.getString("HS_CODE"));
				cm.setHs_sub_code_fr(rs.getString("HS_SUB_CODE_FR"));
				cm.setHs_sub_code_to(rs.getString("HS_SUB_CODE_TO"));
				cm.setCustom_hs_code(rs.getString("CUSTOM_HS_CODE"));
				cm.setCargo_description(rs.getString("CRG_DES"));
				cm.setBills_of_landing_no(rs.getString("BL_NBR"));
				cm.setDg_indicator(rs.getString("DG_IND"));
				cm.setDischarge_operation_indicator(rs.getString("DIS_TYPE"));
				cm.setCargoType(rs.getString("CRG_TYPE"));
				cm.setCargo_description(rs.getString("CRG_DES"));
				cm.setStorage_indicator(rs.getString("STG_TYPE"));
				cm.setCargo_status(rs.getString("CRG_STATUS"));
				cm.setPacking_type(rs.getString("PKG_TYPE"));
				cm.setPort_of_loading(rs.getString("LD_PORT"));
				cm.setPort_of_discharge(rs.getString("DIS_PORT"));
				cm.setPort_of_final_destination(rs.getString("DES_PORT"));
				cm.setConsignee(rs.getString("CONS_CO_CD"));
				cm.setConsignee_others(rs.getString("CONS_NM"));
				cm.setConsignee_addr(rs.getString("CONSIGNEE_ADDR"));
				cm.setShipper_nm(rs.getString("SHIPPER_NM"));
				cm.setShipper_addr(rs.getString("SHIPPER_ADDR"));
				cm.setNotify_party(rs.getString("NOTIFY_PARTY"));
				cm.setNotify_party_addr(rs.getString("NOTIFY_PARTY_ADDR"));
				cm.setPlace_of_delivery(rs.getString("PLACE_OF_DELIVERY"));
				cm.setPlace_of_receipt(rs.getString("PLACE_OF_RECEIPT"));
				cm.setCargo_marking(rs.getString("cargo_marking"));
				cm.setCargo_selection(rs.getString("cargo_selection"));
			}
		} catch (Exception e) {
			log.info("Exception getMainfestDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getMainfestDetails for vvcd :" + vvcd + " , bl_nbr :" + bl_nbr + "  result :" + (cm!= null ? cm.toString() : null));
		}
		return cm;
	}
	
	@Override
	public boolean getMainfestHSDetails(String mftSeq, String hsCode, String hsCodeFr, String hsCodeTo) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		try {
			log.info("START getMainfestHSDetails for mftSeq :" + mftSeq);
			sb.append("SELECT COUNT(MFT_HSCODE_SEQ_NBR) ");
			sb.append("FROM GBMS.MANIFEST_HSCODE_DETAILS md WHERE ");
			sb.append("HS_CODE != :hsCode AND HS_SUB_CODE_FR != :hsCodeFr ");
			sb.append("AND HS_SUB_CODE_TO!= :hsCodeTo AND MFT_SEQ_NBR = :mftSeq");
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("mftSeq", mftSeq);
			paramMap.put("hsCode", hsCode);
			paramMap.put("hsCodeFr", hsCodeFr);
			paramMap.put("hsCodeTo", hsCodeTo);
			log.info("getMainfestHSDetails " + sb.toString() + ", paramap :" + paramMap.toString());
			count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
		} catch (Exception e) {
			log.info("Exception getMainfestHSDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getMainfestHSDetails for mftSeq :" + mftSeq + "  result :" + count);
		}
		return count > 0;
	}
	
	@Override
	public boolean checkMultipleMainfestHSDetails(String mftSeq) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		try {
			log.info("START getMainfestHSDetails for mftSeq :" + mftSeq);
			sb.append("SELECT COUNT(MFT_HSCODE_SEQ_NBR) ");
			sb.append("FROM GBMS.MANIFEST_HSCODE_DETAILS md WHERE ");
			sb.append("MFT_SEQ_NBR = :mftSeq");
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("mftSeq", mftSeq);
			log.info("getMainfestHSDetails " + sb.toString() + ", paramap :" + paramMap.toString());
			count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
		} catch (Exception e) {
			log.info("Exception getMainfestHSDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getMainfestHSDetails for mftSeq :" + mftSeq + "  result :" + count);
		}
		return count > 1;
	}
	
	private boolean subHSDetailsExist(String mftSeq) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		try {
			log.info("START subHSDetailsExist for mftSeq :" + mftSeq);
			sb.append("SELECT COUNT(MFT_HSCODE_SEQ_NBR) ");
			sb.append("FROM GBMS.MANIFEST_HSCODE_DETAILS md WHERE ");
			sb.append("MFT_SEQ_NBR = :mftSeq");
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("mftSeq", mftSeq);
			log.info("subHSDetailsExist " + sb.toString() + ", paramap :" + paramMap.toString());
			count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
		} catch (Exception e) {
			log.info("Exception subHSDetailsExist : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END subHSDetailsExist for mftSeq :" + mftSeq + "  result :" + count);
		}
		return count > 0;
	}
	
	@Override
	public List<String> getMainfestHSCode(String mftSeq) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<String> hsCodes = new ArrayList<String>();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START getMainfestHSCode for mftSeq :" + mftSeq);
			sb.append("SELECT (HS_CODE ||'-'|| HS_SUB_CODE_FR || '-' || HS_SUB_CODE_TO) hsdtl ");
			sb.append("FROM GBMS.MANIFEST_HSCODE_DETAILS md WHERE ");
			sb.append("MFT_SEQ_NBR = :mftSeq");
			paramMap.put("mftSeq", mftSeq);
			log.info("getMainfestHSDetails " + sb.toString() + ", paramap :" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while(rs.next()) {
				hsCodes.add(rs.getString("hsdtl"));
			}
		} catch (Exception e) {
			log.info("Exception getMainfestHSCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getMainfestHSCode for mftSeq :" + mftSeq + "  result :" + hsCodes.size());
		}
		return hsCodes;
	}
	
	@Override
	public List<CargoManifest> getMainfestHSDetail(CargoManifest cmn) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<CargoManifest> hsDtl = new ArrayList<CargoManifest>();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START getMainfestHSDetail for mftSeq :" + cmn.toString());
			sb.append("SELECT MFT_HSCODE_SEQ_NBR, NBR_PKGS, GROSS_WT, GROSS_VOL, CRG_DES, CUSTOM_HS_CODE ");
			sb.append(", HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, MFT_SEQ_NBR ");
			sb.append(", (HS_CODE ||'('|| HS_SUB_CODE_FR || '-' || HS_SUB_CODE_TO ||')' || HS_SUB_CODE_DESC) HS_CODE_SUB_CODE ");
			sb.append(", (SELECT BL_NBR FROM GBMS.MANIFEST_DETAILS WHERE MFT_SEQ_NBR = :mftSeq) BILL_NBR ");
			sb.append("FROM GBMS.MANIFEST_HSCODE_DETAILS md WHERE ");
			sb.append("MFT_SEQ_NBR = :mftSeq");
			if(cmn.getMft_hscode_seq_nbr() != null) {
				sb.append(" AND MFT_HSCODE_SEQ_NBR = :mft_hscode_seq_nbr");
				paramMap.put("mft_hscode_seq_nbr", String.valueOf(cmn.getMft_hscode_seq_nbr()));
			}
			paramMap.put("mftSeq", cmn.getMft_seq_nbr());
			log.info("getMainfestHSDetails " + sb.toString() + ", paramap :" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while(rs.next()) {
				CargoManifest cm = new CargoManifest();
				cm.setMft_hscode_seq_nbr(rs.getBigDecimal("MFT_HSCODE_SEQ_NBR"));
				cm.setNumber_of_packages(rs.getString("NBR_PKGS"));
				cm.setGross_weight_kg(rs.getString("GROSS_WT"));
				cm.setGross_measurement_m3(rs.getString("GROSS_VOL"));
				cm.setCargo_description(rs.getString("CRG_DES"));
				cm.setCustom_hs_code(rs.getString("CUSTOM_HS_CODE"));
				cm.setHs_code(rs.getString("HS_CODE"));
				cm.setHs_sub_code_fr(rs.getString("HS_SUB_CODE_FR"));
				cm.setHs_sub_code_to(rs.getString("HS_SUB_CODE_TO"));
				cm.setMft_seq_nbr(rs.getString("MFT_SEQ_NBR"));
				cm.setBills_of_landing_no(rs.getString("BILL_NBR"));
				cm.setHs_code_sub_code(rs.getString("HS_CODE_SUB_CODE").trim());
				hsDtl.add(cm);
			}
		} catch (Exception e) {
			log.info("Exception getMainfestHSDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getMainfestHSDetail result :" + hsDtl.size());
		}
		return hsDtl;
	}
	

	@Override
	public List<CargoManifest> insertManifestData(List<CargoManifest> manifestRecords, String vvCd,
			String userId, String companyCode, List<String> mainHaveError, List<String> subHaveError, boolean issplitBL) throws BusinessException {
		try {
			log.info("START insertManifestData " + " vvCd :" + vvCd + "userId :" + userId
					+ ", manifestRecords :" + manifestRecords.size() 
					+ "mainHaveError:" + mainHaveError.toString() + "subHaveError:" + subHaveError.toString() + ", issplitBL : " + issplitBL);
			boolean processsResponse = false;
			Integer noOfPkg = 0;
			Double grossWght = 0.0;
			Double grossVolume = 0.0;
			Double totalPkg = 0.0;
			Double totalWgt = 0.0;
			Double totalVol = 0.0;
			Map<String, String> splitMap = new HashMap<String, String>();
			for (CargoManifest cargoManifest : manifestRecords) {

				try {
					processsResponse = false;
					cargoManifest.setVar_nbr(vvCd);
					cargoManifest.setBill_status(ConstantUtil.bill_status);
					cargoManifest.setUnStuff_seq_nbr(0);
					cargoManifest.setLast_modify_user_id(userId);

//					log.info(" Excel processExcelMainfestDetails iteration starts " + " for  cargoManifest :"
//							+ cargoManifest.toString());
					noOfPkg = 0;
					grossWght = 0.0;
					grossVolume = 0.0;
					totalPkg = 0.0;
					totalWgt = 0.0;
					totalVol = 0.0;

					// process hatch details fro check total pkg,wght and volume
					// Validate PKG,Weight and Volume
					// Pkg
					if (cargoManifest.getAction() != null
							&& !cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_NA)
							&& !cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_delete)
							&& cargoManifest.getErrorInfo().size() == 0) {

						if (!cargoManifest.getEdo_created()) {
							noOfPkg = Integer.parseInt(cargoManifest.getNumber_of_packages());
							grossWght = Double.parseDouble(cargoManifest.getGross_weight_kg());
							grossVolume = Double.parseDouble(cargoManifest.getGross_measurement_m3());
							if (cargoManifest.getHatchList() != null) {
								for (HatchDetails hatch : cargoManifest.getHatchList()) {
									if (!CommonUtility.deNull(hatch.getNbr_pkgs()).isEmpty()) {
										totalPkg = totalPkg + Double.parseDouble(hatch.getNbr_pkgs());
									}
									if (!CommonUtility.deNull(hatch.getGross_wt()).isEmpty()) {
										totalWgt = totalWgt + Double.parseDouble(hatch.getGross_wt());
										BigDecimal bdWgt = new BigDecimal(totalWgt).setScale(2, RoundingMode.HALF_DOWN);
										totalWgt = bdWgt.doubleValue();
									}
									if (!CommonUtility.deNull(hatch.getGross_vol()).isEmpty()) {
										totalVol = totalVol + Double.parseDouble(hatch.getGross_vol());
										BigDecimal bdVol = new BigDecimal(totalVol).setScale(2, RoundingMode.HALF_DOWN);
										totalVol = bdVol.doubleValue();
									}
								}
							}

//						log.info(" ************* Excel processExcelMainfestDetails :totalPkg :" + totalPkg
//								+ ", noOfPkg:" + noOfPkg);
//						log.info(" Excel processExcelMainfestDetails :totalWgt :" + totalWgt + ", grossWght:"
//								+ grossWght);
//						log.info(" Excel processExcelMainfestDetails :totalVol :" + totalPkg + ", totalPkg:"
//								+ grossVolume);
							if (totalPkg > noOfPkg) {
								log.info(" Excel processExcelMainfestDetails :" + ConstantUtil.ErrorMsg_BlNoNotExist
										+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
										+ cargoManifest.toString());
								cargoManifest.setMessage(ConstantUtil.ErrorMsg_TotalPkgExceeds);
								continue;
							}
							if (totalPkg > noOfPkg) {
								log.info(" Excel processExcelMainfestDetails :" + ConstantUtil.ErrorMsg_TotalPkgExceeds
										+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
										+ cargoManifest.toString());
								cargoManifest.setMessage(ConstantUtil.ErrorMsg_TotalPkgExceeds);
								continue;
							}
							if (totalWgt > grossWght) {
								log.info(" Excel processExcelMainfestDetails :" + ConstantUtil.ErrorMsg_TotalWtExceeds
										+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
										+ cargoManifest.toString());
								cargoManifest.setMessage(ConstantUtil.ErrorMsg_TotalWtExceeds);
								continue;
							}
							if (totalVol > grossVolume) {
								log.info(" Excel processExcelMainfestDetails :" + ConstantUtil.ErrorMsg_TotalVolExceeds
										+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
										+ cargoManifest.toString());
								cargoManifest.setMessage(ConstantUtil.ErrorMsg_TotalVolExceeds);
								continue;
							}
						}
						
						// Should not proceed to with others if one have error
						String blNbr = cargoManifest.getBills_of_landing_no();
						long mainFailed = mainHaveError.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
						long subFailed = subHaveError.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
						if (mainFailed > 0) {
							if(!CommonUtil.deNull(cargoManifest.getOldHSCode()).isEmpty() && !cargoManifest.getValueChanges() && !cargoManifest.getMainSub()) {
								// Only changing hs code, should pass
							} else {
								if(!cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_add)) {
									cargoManifest.setMessage(ConstantUtil.ErrorMsg_MainAddFailed);
									continue;
								}
							}
						} else if (subFailed > 0) {
							if (!CommonUtil.deNull(cargoManifest.getOldHSCode()).isEmpty()
									&& !cargoManifest.getValueChanges() && !cargoManifest.getMainSub()
									&& !cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_add)
									&& !cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
								// Only changing hs code, should pass
							} else {
								cargoManifest.setMessage(ConstantUtil.ErrorMsg_SubFailed);
								continue;
							}
						}
					}

					// process hatch details fro check total pkg,wght and volume

					// Action - Insert and success row validated records
					if (cargoManifest.getAction() != null
							&& cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_delete)
							&& cargoManifest.getMessage().equalsIgnoreCase(ConstantUtil.success)) {
						log.info("delete ManifestData :" + cargoManifest.toString());
						String mft_seq_nbr = getMftSeqNbr(cargoManifest.getBills_of_landing_no(), vvCd);
						if (mft_seq_nbr == null || mft_seq_nbr == "" || mft_seq_nbr == "0") {
							log.info(" Excel processExcelMainfestDetails :" + ConstantUtil.ErrorMsg_BlNoNotExist
									+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
									+ cargoManifest.toString());
							cargoManifest.setMessage(ConstantUtil.ErrorMsg_BlNoNotExist);
							continue;
						}

						boolean isCargoDeleted = deleteCargoDetails(userId, mft_seq_nbr,cargoManifest.getBills_of_landing_no(),vvCd);
						// No need to delete sub hs detail - NS JULY 2024
						log.info("isCargoDeleted : " + isCargoDeleted);
					} else if (cargoManifest.getMessage().equalsIgnoreCase(ConstantUtil.success)) {

						log.info("insert/update ManifestData :" + cargoManifest.toString());

						if (cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_add)) {
							// checking for duplicate record
							if(!CommonUtil.deNull(cargoManifest.getSplit_bl_ind()).equalsIgnoreCase("Yes")) {
								Integer nbrOfBlnoExist = mainfestDetailIsExist(cargoManifest.getBills_of_landing_no(),
										vvCd);
								if (nbrOfBlnoExist > 0) {
									log.info(" Excel processExcelMainfestDetails Duplicate  :"
											+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
											+ cargoManifest.toString());
									cargoManifest.setMessage(ConstantUtil.Error_M20201);
									continue;
								}
							}
							// START CR TO DISABLE VOLUME - NS FEB 2024
							Criteria criteria = new Criteria();
							criteria.addPredicate("checkType", "All");
							criteria.addPredicate("companyCode", companyCode);
							criteria.addPredicate("hsCd", cargoManifest.getHs_code());
							criteria.addPredicate("hsSubCd",
									cargoManifest.getHs_sub_code_fr()
											+ (!CommonUtil.deNull(cargoManifest.getHs_sub_code_to()).isEmpty()
													? ("-" + cargoManifest.getHs_sub_code_to())
													: ""));
							criteria.addPredicate("scheme", getSchemeName(cargoManifest.getVar_nbr()));
							criteria.addPredicate("status", cargoManifest.getCargo_status().equalsIgnoreCase("L") ? "Local" : "Transhipment");
							criteria.addPredicate("consigneeCode", cargoManifest.getConsignee());
							criteria.addPredicate("consigneeOthers", cargoManifest.getConsignee_others());
							if (isDisabledVolume(criteria)) {
					            // START REQUEST TO CHANGE TO 0 - NS MAY 2024
								int gross_measurement_m3 = 0;
					            // END REQUEST TO CHANGE TO 0 - NS MAY 2024
								cargoManifest.setGross_measurement_m3(String.valueOf(gross_measurement_m3));
							}
							// END CR TO DISABLE VOLUME - NS FEB 2024
							cargoManifest.setMft_seq_nbr(getNextMftSeqNbr());
							cargoManifest.setManifest_create_cd(companyCode);
							// inserting records to manifest details
							// START SPLIT BILL - NS Jan 2025
							if(CommonUtil.deNull(cargoManifest.getSplit_bl_ind()).equalsIgnoreCase("Yes") && issplitBL) {
								String newBL = generateSplitBl(cargoManifest.getSplit_main_bl(), cargoManifest.getVar_nbr());
								if(newBL== null || newBL.isEmpty()) {
									log.info("Main BL : "+cargoManifest.getSplit_main_bl());
									cargoManifest.setMessage(ConstantUtil.ErrorMsg_MainBlNoNotExist);									
									continue;
								} else if(newBL.equalsIgnoreCase("EXISTED")) {
									log.info("Main BL : "+cargoManifest.getSplit_main_bl());
									cargoManifest.setMessage(ConstantUtil.ErrorMsg_MainBlNoAlreadyExist);									
									continue;
								}
								splitMap.put(cargoManifest.getBills_of_landing_no(),newBL);
								cargoManifest.setBills_of_landing_no(newBL);
							}
							// END SPLIT BILL - NS Jan 2025
							processsResponse = insertManifestDetails(cargoManifest, vvCd, userId);
						} else if (cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_update)){
							String mft_seq_nbr = getMftSeqNbr(cargoManifest.getBills_of_landing_no(), vvCd);
							if (mft_seq_nbr == null || mft_seq_nbr == "" || mft_seq_nbr == "0") {
								log.info(" Excel processExcelMainfestDetails :" + ConstantUtil.ErrorMsg_BlNoNotExist
										+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
										+ cargoManifest.toString());
								cargoManifest.setMessage(ConstantUtil.ErrorMsg_BlNoNotExist);
								continue;
							}
							if (cargoManifest.getEdo_created()) {
								CargoManifest mftTmp = cargoManifest;
								cargoManifest = getMainfestDetails(cargoManifest.getBills_of_landing_no(), vvCd);
								cargoManifest.setCustom_hs_code(mftTmp.getCustom_hs_code());
								cargoManifest.setVar_nbr(vvCd);
								cargoManifest.setBill_status(ConstantUtil.bill_status);
								cargoManifest.setUnStuff_seq_nbr(0);
								cargoManifest.setLast_modify_user_id(userId);
								cargoManifest.setLast_modify_dttm(mftTmp.getLast_modify_dttm());
								cargoManifest.setAction(mftTmp.getAction());
								cargoManifest.setSubHSUpdate(mftTmp.getSubHSUpdate());
								cargoManifest.setConsignee_addr(mftTmp.getConsignee_addr());
								cargoManifest.setShipper_nm(mftTmp.getShipper_nm());
								cargoManifest.setShipper_addr(mftTmp.getShipper_addr());
								cargoManifest.setNotify_party(mftTmp.getNotify_party());
								cargoManifest.setNotify_party_addr(mftTmp.getNotify_party_addr());
								cargoManifest.setPlace_of_delivery(mftTmp.getPlace_of_delivery());
								cargoManifest.setPlace_of_receipt(mftTmp.getPlace_of_receipt());
								cargoManifest.setEdo_created(mftTmp.getEdo_created());
								processsResponse = updateCustomOnly(cargoManifest, true); // New field included
							} else {// START CR TO DISABLE VOLUME - NS FEB 2024
								Criteria criteria = new Criteria();
								criteria.addPredicate("checkType", "All");
								criteria.addPredicate("companyCode", companyCode);
								criteria.addPredicate("hsCd", cargoManifest.getHs_code());
								criteria.addPredicate("hsSubCd",
										cargoManifest.getHs_sub_code_fr()
												+ (!CommonUtil.deNull(cargoManifest.getHs_sub_code_to()).isEmpty()
														? ("-" + cargoManifest.getHs_sub_code_to())
														: ""));
								criteria.addPredicate("scheme", getSchemeName(cargoManifest.getVar_nbr()));
								criteria.addPredicate("status",
										cargoManifest.getCargo_status().equalsIgnoreCase("L") ? "Local"
												: "Transhipment");
								criteria.addPredicate("consigneeCode", cargoManifest.getConsignee());
								criteria.addPredicate("consigneeOthers", cargoManifest.getConsignee_others());
								if (isDisabledVolume(criteria)) {
									// START REQUEST TO CHANGE TO 0 - NS MAY 2024
									int gross_measurement_m3 = 0;
									// END REQUEST TO CHANGE TO 0 - NS MAY 2024
									cargoManifest.setGross_measurement_m3(String.valueOf(gross_measurement_m3));
								}
								// END CR TO DISABLE VOLUME - NS FEB 2024
								cargoManifest.setMft_seq_nbr(mft_seq_nbr);
								processsResponse = updateManifestDetails(cargoManifest);
							}
						// START FTZ CR CHECK DUPLICATE DETAILS & ALLOW MULTIPLE HS CODE - NS JUNE 2024
						} else if (cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_addHS)) {
							int nbrOfBlnoExist = mainfestDetailHSExist(cargoManifest);
							if (nbrOfBlnoExist > 0) {
								log.info(" Excel processExcelMainfestDetails Duplicate  :"
										+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
										+ cargoManifest.toString());
								cargoManifest.setMessage(ConstantUtil.Error_M20201);
								continue;
							}
							if(CommonUtil.deNull(cargoManifest.getSplit_bl_ind()).equalsIgnoreCase("Yes")) {
								if(splitMap.containsKey(cargoManifest.getBills_of_landing_no())) {
									cargoManifest.setBills_of_landing_no(splitMap.get(cargoManifest.getBills_of_landing_no()));
								} else if(CommonUtil.deNull(cargoManifest.getBills_of_landing_no()).isEmpty()) {
									cargoManifest.setMessage(ConstantUtil.ErrorMsg_Common);
									continue;
								}
							}
							String mftseqNbr = manifestBLNbrExist(cargoManifest.getBills_of_landing_no(), vvCd);
							if(Integer.parseInt(mftseqNbr) > 0) {
								cargoManifest.setMft_seq_nbr(mftseqNbr);
							} else {
								cargoManifest.setMft_seq_nbr(getNextMftSeqNbrHs(cargoManifest.getBills_of_landing_no()));
							}
							// END SPLIT BILL - NS Jan 2025
							processsResponse = insertManifestHSDetails(cargoManifest);
						} else if (cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_updateHS)){
							String mft_seq_nbr = getMftSeqNbr(cargoManifest.getBills_of_landing_no(), vvCd);
							cargoManifest.setMft_seq_nbr(mft_seq_nbr);
							if(cargoManifest.getEdo_created()) {
								String customTemp = cargoManifest.getCustom_hs_code();
								String ts = cargoManifest.getLast_modify_dttm();
								String action = cargoManifest.getAction();
								List<CargoManifest> tempList = getMainfestHSDetail(cargoManifest);
								cargoManifest = tempList.size() > 0 ? tempList.get(0) : null;
								if(cargoManifest != null) {
									cargoManifest.setVar_nbr(vvCd);
									cargoManifest.setBill_status(ConstantUtil.bill_status);
									cargoManifest.setUnStuff_seq_nbr(0);
									cargoManifest.setLast_modify_user_id(userId);
									cargoManifest.setCustom_hs_code(customTemp);
									cargoManifest.setLast_modify_dttm(ts);
									cargoManifest.setAction(action);
								}
								processsResponse = updateCustomOnly(cargoManifest, false);
							} else {
								processsResponse = updateManifestHSDetails(cargoManifest);
							}
						} else if (cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_deleteHS)){
							String mft_seq_nbr = getMftSeqNbr(cargoManifest.getBills_of_landing_no(), vvCd);
							cargoManifest.setMft_seq_nbr(mft_seq_nbr);
							processsResponse = deleteHSDetails(cargoManifest, userId);
						}
						// END FTZ CR CHECK DUPLICATE DETAILS & ALLOW MULTIPLE HS CODE - NS JUNE 2024

						if (!processsResponse) {
							log.info(" Excel processExcelMainfestDetails :"
									+ ConstantUtil.ErrorMsg_ManifestDetailsProcess
									+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
									+ cargoManifest.toString());
							cargoManifest.setMessage(ConstantUtil.ErrorMsg_ManifestDetailsProcess);
							continue;
						}
						// process cargo marking
						if (!cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_addHS)
								&& !cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_updateHS)
								&& !cargoManifest.getAction().equalsIgnoreCase(ConstantUtil.action_deleteHS)
								&& !cargoManifest.getEdo_created()) {
							processsResponse = processCargoMarking(cargoManifest);
							if (!processsResponse) {
								log.info(" Excel processExcelMainfestDetails :" + ConstantUtil.ErrorMsg_CargoMarkingProcess
										+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
										+ cargoManifest.toString());
								cargoManifest.setMessage(ConstantUtil.ErrorMsg_CargoMarkingProcess);
								continue;
							}

							// cargo selection should set the cargoselection code
							// if (cargoManifest.getCargo_selection() != null &&
							// cargoManifest.getCargo_selection() != "") {
							processsResponse = processCargoSelection(cargoManifest);
							if (!processsResponse) {
								log.info(
										" Excel processExcelMainfestDetails :" + ConstantUtil.ErrorMsg_CargoSelectionProcess
												+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
												+ cargoManifest.toString());
								cargoManifest.setMessage(ConstantUtil.ErrorMsg_CargoSelectionProcess);
								continue;
							}
							// }
							processsResponse = processHatchDetails(cargoManifest);
							if (!processsResponse) {
								log.info(" Excel processExcelMainfestDetails :" + ConstantUtil.ErrorMsg_HatchProcess
										+ cargoManifest.getBills_of_landing_no() + ", manifest record is :"
										+ cargoManifest.toString());
								cargoManifest.setMessage(ConstantUtil.ErrorMsg_HatchProcess);
								continue;
							}
						}
					}
				} catch (BusinessException e) {
					log.info("Exception insertManifestData : ", e);
					log.info(" Exception in Excel processExcelMainfestDetails iteration  " + " for  cargoManifest :"
							+ cargoManifest.toString() + ", excpetion " + e.toString());
					throw new BusinessException("M4201");
				} catch (Exception e) {
					log.info("Exception insertManifestData : ", e);
					log.info(" Exception in Excel processExcelMainfestDetails iteration  " + " for  cargoManifest :"
							+ cargoManifest.toString() + ", excpetion " + e.toString());
					cargoManifest.setMessage(ConstantUtil.ErrorMsg_Common);
				}
			}
		} catch (BusinessException e) {
			log.info("Exception insertManifestData : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertManifestData : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertManifestData ");
		}
		return manifestRecords;
	}
	
	private boolean updateCustomOnly(CargoManifest cargoManifest, boolean main) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb_update = new StringBuffer();
		boolean update = false;
		try {
			log.info("START updateCustomOnly :custom_hs_code" + cargoManifest.toString() + ",main:" + main);

			paramMap.put("custom_hs_code", cargoManifest.getCustom_hs_code());
			if (main) {
				sb_update.append("UPDATE GBMS.MANIFEST_DETAILS SET CUSTOM_HS_CODE = :custom_hs_code, ");
				sb_update.append(" CONSIGNEE_ADDR = :consignee_addr,");
				sb_update.append(" SHIPPER_NM = :shipper_nm,");
				sb_update.append(" SHIPPER_ADDR = :shipper_addr,");
				sb_update.append(" NOTIFY_PARTY = :notify_party,");
				sb_update.append(" NOTIFY_PARTY_ADDR = :notify_party_addr,");
				sb_update.append(" PLACE_OF_DELIVERY = :place_of_delivery,");
				sb_update.append(" PLACE_OF_RECEIPT = :place_of_receipt ");
				sb_update.append("WHERE  MFT_SEQ_NBR =:mft_seq_nbr");

				log.info(" updateManifestDetails   :" + sb_update.toString());
				int rows = namedParameterJdbcTemplate.update(sb_update.toString(), new BeanPropertySqlParameterSource(cargoManifest));
				
				// Trans_log
				sb_update = new StringBuffer();
				sb_update.append(
						"SELECT NVL(MAX(TRANS_NBR),0) FROM GBMS.MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:mftSeqNbr");
				paramMap.put("mftSeqNbr", cargoManifest.getMft_seq_nbr());
				log.info(" updateManifestDetails   :" + sb_update.toString() + ", paramap :" + paramMap.toString());
				int trans_nbr = namedParameterJdbcTemplate.queryForObject(sb_update.toString(), paramMap, Integer.class);
				trans_nbr = trans_nbr + 1;
				cargoManifest.setTrans_nbr(trans_nbr);

				sb_update = new StringBuffer();
				sb_update.append("INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,BL_NBR,VAR_NBR,BL_STATUS,");
				sb_update.append("CRG_TYPE,CRG_DES,HS_CODE,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_STATUS,DG_IND,STG_TYPE,");
				sb_update.append("PKG_TYPE,DIS_TYPE,CONS_CO_CD,");
				sb_update.append("CONS_NM,LD_PORT,DIS_PORT,DES_PORT,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,");
				sb_update.append("EDO_NBR_PKGS,NBR_PKGS_IN_PORT,CUSTOM_HS_CODE,CONSIGNEE_ADDR,SHIPPER_NM,");
				sb_update.append("SHIPPER_ADDR,NOTIFY_PARTY,NOTIFY_PARTY_ADDR,PLACE_OF_DELIVERY,");
				sb_update.append("PLACE_OF_RECEIPT,MANIFEST_CREATE_CD");
				sb_update.append(")VALUES(");
				sb_update.append(":trans_nbr,:mft_seq_nbr,UPPER(:bills_of_landing_no),:var_nbr,:bill_status,");
				sb_update.append(":cargoType,:cargo_description,:hs_code,:number_of_packages,");
				sb_update.append(":gross_weight_kg,:gross_measurement_m3,:cargo_status,:dg_indicator,");
				sb_update.append(":storage_indicator,:packing_type,:discharge_operation_indicator,");
				sb_update.append(":consignee,:consignee_others,:port_of_loading,:port_of_discharge,");
				sb_update.append(":port_of_final_destination,:last_modify_user_id,");
				sb_update.append("TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'),0,0,");
				sb_update.append(":custom_hs_code,:consignee_addr,:shipper_nm,:shipper_addr,:notify_party,");
				sb_update.append(":notify_party_addr,:place_of_delivery,:place_of_receipt,:manifest_create_cd)");

				log.info(" updateManifestDetails   :" + sb_update.toString());
				namedParameterJdbcTemplate.update(sb_update.toString(), new BeanPropertySqlParameterSource(cargoManifest));
				
				if (rows > 0) {
					// Old data that doesnt have MANIFEST_HSCODE_DETAILS data yet
					if(!subHSDetailsExist(cargoManifest.getMft_seq_nbr()) && !cargoManifest.getHasAddProcess()) {
						log.info("Old data that doesnt have MANIFEST_HSCODE_DETAILS data yet");
						StringBuilder sbSeq = new StringBuilder();
						sbSeq.append("SELECT GBMS.SEQ_MFT_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
						Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
						BigDecimal seqValue = (BigDecimal) results.get("seqVal");
						log.info("mfthsseqNbr:" + seqValue);
						cargoManifest.setMft_hscode_seq_nbr(seqValue);

						StringBuffer sb = new StringBuffer();
						sb.append("INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS(MFT_HSCODE_SEQ_NBR,");
						sb.append("MFT_SEQ_NBR,HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,HS_SUB_CODE_DESC,");
						sb.append("NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
						sb.append("(:mft_hscode_seq_nbr,:mft_seq_nbr,:hs_code,:custom_hs_code,");
						sb.append(":hs_sub_code_fr,:hs_sub_code_to,");
						sb.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
						sb.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
						sb.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
						sb.append(":cargo_description,:last_modify_user_id,");
						sb.append("TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

						log.info(" updateManifestDetails   :" + sb.toString());
						int insert_result = namedParameterJdbcTemplate.update(sb.toString(),
								new BeanPropertySqlParameterSource(cargoManifest));
						
						log.info("inserted:" + insert_result);

						sb = new StringBuffer();
						sb.append("INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS(MFT_HSCODE_SEQ_NBR,MFT_SEQ_NBR,AUDIT_DTTM,REC_STATUS,");
						sb.append("HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,");
						sb.append(
								"HS_SUB_CODE_DESC,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
						sb.append("(:mft_hscode_seq_nbr,:mft_seq_nbr,SYSDATE, 'A',:hs_code,:custom_hs_code,:hs_sub_code_fr,:hs_sub_code_to,");
						sb.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
						sb.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
						sb.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
						sb.append(
								":cargo_description,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

						log.info(" updateManifestDetails   :" + sb.toString());
						namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(cargoManifest));
						
						// START -- CHECK FIRST IF GB_EDO_HSCODE_DETAILS EXIST --
						sb_update.setLength(0);
						sb_update.append(" SELECT * FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
						
						paramMap.put("MFT_HSCODE_SEQ_NBR", String.valueOf(cargoManifest.getMft_hscode_seq_nbr()));
						
						log.info("SQL" + sb_update.toString());
						log.info("params: " + paramMap.toString());
						SqlRowSet rsEdo = namedParameterJdbcTemplate.queryForRowSet(sb_update.toString(), paramMap);
						if(!rsEdo.next()) {
							// Get data from GB_EDO to create data for GB_EDO_HSCODE_DETAILS
							sb_update.setLength(0);
							sb_update.append(" SELECT * FROM GBMS.GB_EDO WHERE MFT_SEQ_NBR=:MFT_SEQ_NBR ");
							
							paramMap.put("MFT_SEQ_NBR", String.valueOf(cargoManifest.getMft_seq_nbr()));
							
							log.info("SQL" + sb_update.toString());
							log.info("params: " + paramMap.toString());
							rsEdo = namedParameterJdbcTemplate.queryForRowSet(sb_update.toString(), paramMap);
							
							if (rsEdo.next()) {
								log.info((cargoManifest.getHs_code()) + "," + (cargoManifest.getHs_sub_code_fr()) + ","
										+ (cargoManifest.getHs_sub_code_to()));

								log.info("Create new GB_EDO_HSCODE_DETAILS data");
								// GET EDO_HSCODE_SEQ_NBR 
								sbSeq.setLength(0);
								sbSeq.append("SELECT GBMS.SEQ_EDO_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
								results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
								seqValue = (BigDecimal) results.get("seqVal");
								log.info("seqValue" + seqValue);
								
								// START -- CREATE GB_EDO_HSCODE_DETAILS DATA --
								sb.setLength(0);
								sb.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS  ");
								sb.append(" (MFT_SEQ_NBR,EDO_HSCODE_SEQ_NBR, MFT_HSCODE_SEQ_NBR,EDO_ASN_NBR,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,CUSTOM_HS_CODE,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL) ");
								sb.append(" VALUES(:MFT_SEQ_NBR,:EDO_HSCODE_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, :EDO_ASN_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:CUSTOM_HS_CODE,:userId,SYSDATE, :NBR_PKGS, :GROSS_WT, :GROSS_VOL) ");
								paramMap.put("EDO_HSCODE_SEQ_NBR", String.valueOf(seqValue));
								paramMap.put("EDO_ASN_NBR", rsEdo.getString("EDO_ASN_NBR"));
								paramMap.put("HS_CODE", cargoManifest.getHs_code());
								paramMap.put("HS_SUB_CODE_FR", cargoManifest.getHs_sub_code_fr());
								paramMap.put("HS_SUB_CODE_TO", cargoManifest.getHs_sub_code_to());
								paramMap.put("CUSTOM_HS_CODE", cargoManifest.getCustom_hs_code());
								paramMap.put("NBR_PKGS", rsEdo.getString("NBR_PKGS"));
								paramMap.put("GROSS_WT", rsEdo.getString("NOM_WT"));
								paramMap.put("GROSS_VOL", rsEdo.getString("NOM_VOL"));
								paramMap.put("userId", cargoManifest.getLast_modify_user_id());
								
								log.info("SQL" + sb.toString());
								log.info("params: " + paramMap.toString());
								int countEDO = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
								log.info("countEDO : " + countEDO);

								sb_update.setLength(0);
								sb_update.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS_TRANS  ");
								sb_update.append(" (MFT_SEQ_NBR,EDO_HSCODE_SEQ_NBR, MFT_HSCODE_SEQ_NBR,EDO_ASN_NBR, AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,CUSTOM_HS_CODE,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL) ");
								sb_update.append(" VALUES(:MFT_SEQ_NBR,:EDO_HSCODE_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, :EDO_ASN_NBR, SYSDATE,'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:CUSTOM_HS_CODE,:userId,SYSDATE, :NBR_PKGS, :GROSS_WT, :GROSS_VOL) ");
																
								log.info("SQL" + sb_update.toString());
								log.info("paramMap" + paramMap.toString());
								int counthsAudit = namedParameterJdbcTemplate.update(sb_update.toString(), paramMap);
								log.info("counthsAuditEdo : " + counthsAudit);
							}
						}
						// END -- CREATE GB_EDO_HSCODE_DETAILS DATA --
					// Single data w/o updating sub in excel
					} else if(!checkMultipleMainfestHSDetails(cargoManifest.getMft_seq_nbr()) && !cargoManifest.getHasAddProcess() && !cargoManifest.getSubHSUpdate()) {
						sb_update = new StringBuffer();
						sb_update.append("	UPDATE ");
						sb_update.append("		GBMS.MANIFEST_HSCODE_DETAILS ");
						sb_update.append("	SET ");
						sb_update.append("		CUSTOM_HS_CODE = :custom_hs_code,");
						sb_update.append("		LAST_MODIFY_USER_ID =:last_modify_user_id, ");
						sb_update.append("		LAST_MODIFY_DTTM = TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3') ");
						sb_update.append("	WHERE ");
						sb_update.append("		 MFT_SEQ_NBR =:mft_seq_nbr");

						log.info(" updateManifestDetails   :" + sb_update.toString());
						rows = namedParameterJdbcTemplate.update(sb_update.toString(),
								new BeanPropertySqlParameterSource(cargoManifest));
						
						sb_update = new StringBuffer();
						paramMap.put("mft_seq_nbr", cargoManifest.getMft_seq_nbr());
						BigDecimal mfthsseqNbr = namedParameterJdbcTemplate.queryForObject("SELECT MFT_HSCODE_SEQ_NBR FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_SEQ_NBR =:mft_seq_nbr", paramMap, BigDecimal.class);
						log.info("mfthsseqNbr:" + mfthsseqNbr);
						cargoManifest.setMft_hscode_seq_nbr(mfthsseqNbr);
						
						sb_update = new StringBuffer();
						sb_update.append("INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS(MFT_HSCODE_SEQ_NBR,");
						sb_update.append("MFT_SEQ_NBR,AUDIT_DTTM,REC_STATUS,HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,");
						sb_update.append("HS_SUB_CODE_TO,HS_SUB_CODE_DESC,NBR_PKGS,");
						sb_update.append("GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
						sb_update.append("(:mft_hscode_seq_nbr, :mft_seq_nbr,SYSDATE, 'A',:hs_code,:custom_hs_code,");
						sb_update.append(":hs_sub_code_fr,:hs_sub_code_to,");
						sb_update.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
						sb_update.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
						sb_update.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
						sb_update.append(":cargo_description,:last_modify_user_id,");
						sb_update.append("TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

						log.info(" updateManifestDetails   :" + sb_update.toString());
						namedParameterJdbcTemplate.update(sb_update.toString(), new BeanPropertySqlParameterSource(cargoManifest));
						
						// START -- UPDATE EDO TABLE --
						sb_update.setLength(0);
						sb_update.append(" SELECT * FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
						
						paramMap.put("MFT_HSCODE_SEQ_NBR", String.valueOf(cargoManifest.getMft_hscode_seq_nbr()));
						
						log.info("SQL" + sb_update.toString());
						log.info("params: " + paramMap.toString());
						SqlRowSet rsEdo = namedParameterJdbcTemplate.queryForRowSet(sb_update.toString(), paramMap);
						if(rsEdo.next()) {
							log.info(rsEdo.getString("HS_CODE")+"="+(cargoManifest.getHs_code())
									+","+ rsEdo.getString("HS_SUB_CODE_FR")+"="+(cargoManifest.getHs_sub_code_fr())
									+","+ rsEdo.getString("HS_SUB_CODE_TO")+"="+(cargoManifest.getHs_sub_code_to())
									+","+ rsEdo.getString("CUSTOM_HS_CODE")+"="+(cargoManifest.getCustom_hs_code()));


							log.info("EDO created. Only update customHSCode");
							sb_update.setLength(0);
							sb_update.append(" UPDATE GBMS.GB_EDO_HSCODE_DETAILS SET ");
							sb_update.append(" CUSTOM_HS_CODE=:CUSTOM_HS_CODE, ");
							sb_update.append(" LAST_MODIFY_USER_ID=:userId, LAST_MODIFY_DTTM=SYSDATE WHERE");
							sb_update.append(" MFT_SEQ_NBR = :MFT_SEQ_NBR AND MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");

							paramMap.put("CUSTOM_HS_CODE", cargoManifest.getCustom_hs_code());
							paramMap.put("userId", cargoManifest.getLast_modify_user_id());
							paramMap.put("MFT_SEQ_NBR", cargoManifest.getMft_seq_nbr());
							
							log.info("SQL" + sb_update.toString());
							log.info("params: " + paramMap.toString());
							int countEDO = namedParameterJdbcTemplate.update(sb_update.toString(), paramMap);
							log.info("countEDO : " + countEDO);

							sb_update.setLength(0);
							sb_update.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS_TRANS  ");
							sb_update.append(" (MFT_SEQ_NBR,EDO_HSCODE_SEQ_NBR, MFT_HSCODE_SEQ_NBR,EDO_ASN_NBR, AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,CUSTOM_HS_CODE,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL) ");
							sb_update.append(" VALUES(:MFT_SEQ_NBR,:EDO_HSCODE_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, :EDO_ASN_NBR, SYSDATE,'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:CUSTOM_HS_CODE,:userId,SYSDATE, :NBR_PKGS, :GROSS_WT, :GROSS_VOL) ");
							
							paramMap.put("EDO_HSCODE_SEQ_NBR", rsEdo.getString("EDO_HSCODE_SEQ_NBR"));
							paramMap.put("EDO_ASN_NBR", rsEdo.getString("EDO_ASN_NBR"));
							paramMap.put("HS_CODE", rsEdo.getString("HS_CODE"));
							paramMap.put("HS_SUB_CODE_FR", rsEdo.getString("HS_SUB_CODE_FR"));
							paramMap.put("HS_SUB_CODE_TO", rsEdo.getString("HS_SUB_CODE_TO"));
							paramMap.put("NBR_PKGS", rsEdo.getString("NBR_PKGS"));
							paramMap.put("GROSS_WT", rsEdo.getString("GROSS_WT"));
							paramMap.put("GROSS_VOL", rsEdo.getString("GROSS_VOL"));
							
							log.info("SQL" + sb_update.toString());
							log.info("paramMap" + paramMap.toString());
							int counthsAudit = namedParameterJdbcTemplate.update(sb_update.toString(), paramMap);
							log.info("counthsAuditEdo : " + counthsAudit);
						}
						// END -- UPDATE EDO TABLE --
					} 
				}
			} else {
				sb_update.append("	UPDATE ");
				sb_update.append("		GBMS.MANIFEST_HSCODE_DETAILS ");
				sb_update.append("	SET ");
				sb_update.append("		CUSTOM_HS_CODE =:custom_hs_code, ");
				sb_update.append("		LAST_MODIFY_USER_ID =:last_modify_user_id, ");
				sb_update.append("		LAST_MODIFY_DTTM = TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3') ");
				sb_update.append("	WHERE ");
				sb_update.append("		 MFT_HSCODE_SEQ_NBR =:mft_hscode_seq_nbr");

				log.info(" updateManifestDetails   :" + sb_update.toString());
				int rows = namedParameterJdbcTemplate.update(sb_update.toString(),
						new BeanPropertySqlParameterSource(cargoManifest));
				
				sb_update.setLength(0);
				sb_update.append("INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS(MFT_HSCODE_SEQ_NBR, MFT_SEQ_NBR,AUDIT_DTTM,REC_STATUS,");
				sb_update.append("HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,");
				sb_update.append("HS_SUB_CODE_DESC,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
				sb_update.append("(:mft_hscode_seq_nbr,:mft_seq_nbr,SYSDATE, 'A',:hs_code,:custom_hs_code,:hs_sub_code_fr,:hs_sub_code_to,");
				sb_update.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
				sb_update.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
				sb_update.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
				sb_update.append(":cargo_description,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

				namedParameterJdbcTemplate.update(sb_update.toString(), new BeanPropertySqlParameterSource(cargoManifest));
				
				log.info(rows + "cargoManifest : " + sb_update.toString());
				
				// START -- UPDATE EDO TABLE --
				sb_update.setLength(0);
				sb_update.append(" SELECT * FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");
				
				paramMap.put("MFT_HSCODE_SEQ_NBR", String.valueOf(cargoManifest.getMft_hscode_seq_nbr()));
				
				log.info("SQL" + sb_update.toString());
				log.info("params: " + paramMap.toString());
				SqlRowSet rsEdo = namedParameterJdbcTemplate.queryForRowSet(sb_update.toString(), paramMap);
				if(rsEdo.next()) {
					log.info(rsEdo.getString("HS_CODE")+"="+(cargoManifest.getHs_code())
							+","+ rsEdo.getString("HS_SUB_CODE_FR")+"="+(cargoManifest.getHs_sub_code_fr())
							+","+ rsEdo.getString("HS_SUB_CODE_TO")+"="+(cargoManifest.getHs_sub_code_to())
							+","+ rsEdo.getString("CUSTOM_HS_CODE")+"="+(cargoManifest.getCustom_hs_code()));

					log.info("EDO created. Only update customHSCode");
					sb_update.setLength(0);
					sb_update.append(" UPDATE GBMS.GB_EDO_HSCODE_DETAILS SET ");
					sb_update.append(" CUSTOM_HS_CODE=:CUSTOM_HS_CODE, ");
					sb_update.append(" LAST_MODIFY_USER_ID=:userId, LAST_MODIFY_DTTM=SYSDATE WHERE");
					sb_update.append(" MFT_SEQ_NBR = :MFT_SEQ_NBR AND MFT_HSCODE_SEQ_NBR=:MFT_HSCODE_SEQ_NBR ");

					paramMap.put("CUSTOM_HS_CODE", cargoManifest.getCustom_hs_code());
					paramMap.put("userId", cargoManifest.getLast_modify_user_id());
					paramMap.put("MFT_SEQ_NBR", cargoManifest.getMft_seq_nbr());
					
					log.info("SQL" + sb_update.toString());
					log.info("params: " + paramMap.toString());
					int countEDO = namedParameterJdbcTemplate.update(sb_update.toString(), paramMap);
					log.info("countEDO : " + countEDO);

					sb_update.setLength(0);
					sb_update.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS_TRANS  ");
					sb_update.append(" (MFT_SEQ_NBR,EDO_HSCODE_SEQ_NBR, MFT_HSCODE_SEQ_NBR,EDO_ASN_NBR, AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,CUSTOM_HS_CODE,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL) ");
					sb_update.append(" VALUES(:MFT_SEQ_NBR,:EDO_HSCODE_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, :EDO_ASN_NBR, SYSDATE,'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:CUSTOM_HS_CODE,:userId,SYSDATE, :NBR_PKGS, :GROSS_WT, :GROSS_VOL) ");
					
					paramMap.put("EDO_HSCODE_SEQ_NBR", rsEdo.getString("EDO_HSCODE_SEQ_NBR"));
					paramMap.put("EDO_ASN_NBR", rsEdo.getString("EDO_ASN_NBR"));
					paramMap.put("HS_CODE", rsEdo.getString("HS_CODE"));
					paramMap.put("HS_SUB_CODE_FR", rsEdo.getString("HS_SUB_CODE_FR"));
					paramMap.put("HS_SUB_CODE_TO", rsEdo.getString("HS_SUB_CODE_TO"));
					paramMap.put("NBR_PKGS", rsEdo.getString("NBR_PKGS"));
					paramMap.put("GROSS_WT", rsEdo.getString("GROSS_WT"));
					paramMap.put("GROSS_VOL", rsEdo.getString("GROSS_VOL"));
					
					log.info("SQL" + sb_update.toString());
					log.info("paramMap" + paramMap.toString());
					int counthsAudit = namedParameterJdbcTemplate.update(sb_update.toString(), paramMap);
					log.info("counthsAuditEdo : " + counthsAudit);
				}
				// END -- UPDATE EDO TABLE --
			}
			update = true;
		} catch (Exception e) {
			log.info("Exception updateCustomOnly : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END updateCustomOnly " + update);
		}
		return update;
	}

	private String manifestBLNbrExist(String bills_of_landing_no, String vvCd) throws BusinessException {
		String mftseqNbr = "0";
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START manifestBLNbrExist for bills_of_landing_no:" + bills_of_landing_no);
			sb.append("SELECT MFT_SEQ_NBR FROM GBMS.MANIFEST_DETAILS md ");
			sb.append("WHERE md.BL_NBR = :bills_of_landing_no ");
			sb.append("AND md.BL_STATUS ='A' AND VAR_NBR=:vvCd");
			paramMap.put("bills_of_landing_no", bills_of_landing_no);
			paramMap.put("vvCd", vvCd);
			log.info("mainfestDetailIsExist " + sb.toString() + ", paramap :" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if(rs.next()) {
				mftseqNbr = rs.getString("MFT_SEQ_NBR");
			}
		} catch (Exception e) {
			log.info("Exception manifestBLNbrExist : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END manifestBLNbrExist for mftseqNbr" + mftseqNbr);
		}
		return mftseqNbr;
	}

	private int mainfestDetailHSExist(CargoManifest cargoManifest) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		try {
			log.info("START mainfestDetailIsExist for cm:"+ cargoManifest.toString());
			sb.append("SELECT COUNT(MFT_SEQ_NBR) FROM GBMS.MANIFEST_HSCODE_DETAILS md ");
			sb.append("WHERE MFT_SEQ_NBR = :mftseqNbr AND HS_CODE = :hsCode ");
			sb.append("AND HS_SUB_CODE_FR = :hsCodeFr AND HS_SUB_CODE_TO=:hsCodeTo ");
			sb.append("AND CUSTOM_HS_CODE = :customHSCode "); // Restrict same hs code & same custom - NS SEPT 2024
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("mftseqNbr", cargoManifest.getMft_seq_nbr());
			paramMap.put("hsCode", cargoManifest.getHs_code());
			paramMap.put("hsCodeFr", cargoManifest.getHs_sub_code_fr());
			paramMap.put("hsCodeTo", cargoManifest.getHs_sub_code_to());
			paramMap.put("customHSCode", cargoManifest.getCustom_hs_code());
			log.info("mainfestDetailIsExist " + sb.toString() + ", paramap :" + paramMap.toString());
			count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
		} catch (Exception e) {
			log.info("Exception mainfestDetailIsExist : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END mainfestDetailIsExist for count" + count);
		}
		return count;
	}
	// END FTZ CR CHECK DUPLICATE DETAILS & ALLOW MULTIPLE HS CODE - NS JUNE 2024

	private String getNextMftSeqNbr() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		String mft_seq_nbr = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			sb.append("SELECT MAX(TO_NUMBER(MFT_SEQ_NBR))+1 FROM GBMS.MANIFEST_DETAILS");
			mft_seq_nbr = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			log.info("getNextMftSeqNbr : " + mft_seq_nbr);
		} catch (Exception e) {
			log.info("Exception getCargoManifestFileUploadDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCargoManifestFileUploadDetails ");
		}
		return mft_seq_nbr;
	}
	
	private String getNextMftSeqNbrHs(String blNbr) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		String mft_seq_nbr = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START getNextMftSeqNbrHs blNbr" + blNbr);
			sb.append("SELECT MAX(TO_NUMBER(MFT_SEQ_NBR)) FROM GBMS.MANIFEST_DETAILS");
			mft_seq_nbr = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			log.info("getNextMftSeqNbrHs : " + mft_seq_nbr);
		} catch (Exception e) {
			log.info("Exception getCargoManifestFileUploadDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCargoManifestFileUploadDetails ");
		}
		return mft_seq_nbr;
	}

	private boolean insertManifestDetails(CargoManifest cargoManifest, String vvCd, String userId) throws BusinessException {
		try {
			log.info("START insertManifestDetails :cargoManifest" + cargoManifest.toString());
			
			if(CommonUtil.deNull(cargoManifest.getSplit_bl_ind()).equalsIgnoreCase("Yes")) {
				cargoManifest.setSplit_id(getMaxSplitId(cargoManifest.getSplit_main_bl()));
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append(
					"INSERT INTO GBMS.MANIFEST_DETAILS(MFT_SEQ_NBR,BL_NBR,VAR_NBR,BL_STATUS,CRG_TYPE,CRG_DES,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,");
			sb.append(
					"CRG_STATUS,DG_IND,STG_TYPE,PKG_TYPE,DIS_TYPE,CONS_CO_CD,CONS_NM,LD_PORT,DIS_PORT,DES_PORT,UNSTUFF_SEQ_NBR,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,EDO_NBR_PKGS,NBR_PKGS_IN_PORT");
			sb.append(",CUSTOM_HS_CODE,CONSIGNEE_ADDR,SHIPPER_NM,SHIPPER_ADDR,NOTIFY_PARTY,NOTIFY_PARTY_ADDR,PLACE_OF_DELIVERY,PLACE_OF_RECEIPT,MANIFEST_CREATE_CD"); //Fix added manifest create cd for edo creation
			// SPLIT BL - NS Jan 2025
			if(CommonUtil.deNull(cargoManifest.getSplit_bl_ind()).equalsIgnoreCase("Yes")) {
				sb.append(", SPLIT_MAIN_BL, SPLIT_ID ");
			}
			sb.append(")VALUES");
			sb.append(
					"(:mft_seq_nbr,UPPER(:bills_of_landing_no),:var_nbr,:bill_status,:cargoType,:cargo_description,:hs_code,:hs_sub_code_fr,:hs_sub_code_to,:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
			sb.append(
					":cargo_status,:dg_indicator,:storage_indicator,:packing_type,:discharge_operation_indicator,:consignee,:consignee_others,");
			sb.append(
					":port_of_loading,:port_of_discharge,:port_of_final_destination,:unStuff_seq_nbr,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'),0,0,");
			sb.append(":custom_hs_code,:consignee_addr,:shipper_nm,:shipper_addr,:notify_party,:notify_party_addr,:place_of_delivery,:place_of_receipt,:manifest_create_cd");
			
			if(CommonUtil.deNull(cargoManifest.getSplit_bl_ind()).equalsIgnoreCase("Yes")) {
				sb.append(", :split_main_bl, :split_id ");
			}
			sb.append(")");
			int insert_result = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(cargoManifest));
			// MANIFEST_DETAILS_TRANS
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
	
				
				sb= new StringBuffer();
				sb.append(
						"INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,BL_NBR,VAR_NBR,BL_STATUS,CRG_TYPE,CRG_DES,HS_CODE,NBR_PKGS,GROSS_WT,GROSS_VOL,");
				sb.append(
						"CRG_STATUS,DG_IND,STG_TYPE,PKG_TYPE,DIS_TYPE,CONS_CO_CD,CONS_NM,LD_PORT,DIS_PORT,DES_PORT,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,EDO_NBR_PKGS,NBR_PKGS_IN_PORT");
				sb.append(",CUSTOM_HS_CODE,CONSIGNEE_ADDR,SHIPPER_NM,SHIPPER_ADDR,NOTIFY_PARTY,NOTIFY_PARTY_ADDR,PLACE_OF_DELIVERY,PLACE_OF_RECEIPT,MANIFEST_CREATE_CD");
				// SPLIT BL - NS Jan 2025
				if(CommonUtil.deNull(cargoManifest.getSplit_bl_ind()).equalsIgnoreCase("Yes")) {
					sb.append(", SPLIT_MAIN_BL, SPLIT_ID ");
				}
				sb.append(")VALUES");
				sb.append(
						"('0',:mft_seq_nbr,UPPER(:bills_of_landing_no),:var_nbr,:bill_status,:cargoType,:cargo_description,:hs_code,:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
				sb.append(
						":cargo_status,:dg_indicator,:storage_indicator,:packing_type,:discharge_operation_indicator,:consignee,:consignee_others,");
				sb.append(
						":port_of_loading,:port_of_discharge,:port_of_final_destination,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'),0,0,");
				sb.append(":custom_hs_code,:consignee_addr,:shipper_nm,:shipper_addr,:notify_party,:notify_party_addr,:place_of_delivery,:place_of_receipt,:manifest_create_cd ");
				if(CommonUtil.deNull(cargoManifest.getSplit_bl_ind()).equalsIgnoreCase("Yes")) {
					sb.append(", :split_main_bl, :split_id ");
				}
				sb.append(")");
				namedParameterJdbcTemplate.update(sb.toString(),
						new BeanPropertySqlParameterSource(cargoManifest));
			}

			if (insert_result > 0) {
				if (!cargoManifest.getMultipleHsCode()) {
					StringBuilder sbSeq = new StringBuilder();
					sbSeq.append("SELECT GBMS.SEQ_MFT_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
					Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
					BigDecimal seqValue = (BigDecimal) results.get("seqVal");
					log.info("seqValue : "+ seqValue);
					cargoManifest.setMft_hscode_seq_nbr(seqValue);

					sb = new StringBuffer();
					sb.append(
							"INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS(MFT_HSCODE_SEQ_NBR,MFT_SEQ_NBR,HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,");
					sb.append(
							"HS_SUB_CODE_DESC,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
					sb.append("(:mft_hscode_seq_nbr,:mft_seq_nbr,:hs_code,:custom_hs_code,:hs_sub_code_fr,:hs_sub_code_to,");
					sb.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
					sb.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
					sb.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
					sb.append(
							":cargo_description,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

					insert_result = namedParameterJdbcTemplate.update(sb.toString(),
							new BeanPropertySqlParameterSource(cargoManifest));

					sb = new StringBuffer();
					sb.append("INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS(MFT_HSCODE_SEQ_NBR,MFT_SEQ_NBR,AUDIT_DTTM,REC_STATUS,");
					sb.append("HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,");
					sb.append(
							"HS_SUB_CODE_DESC,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
					sb.append("(:mft_hscode_seq_nbr,:mft_seq_nbr,SYSDATE, 'A',:hs_code,:custom_hs_code,:hs_sub_code_fr,:hs_sub_code_to,");
					sb.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
					sb.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
					sb.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
					sb.append(
							":cargo_description,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

					namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(cargoManifest));
				}
				return true;
			}
		} catch (Exception e) {
			log.info("Exception insertManifestDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertManifestDetails " + cargoManifest.toString());
		}
		return false;
	}

	private boolean insertManifestHSDetails(CargoManifest cargoManifest)
			throws BusinessException {
		StringBuffer sb = new StringBuffer();
		int insert_result = 0;
		try {
			log.info("START insertManifestHSDetails :cargoManifest" + cargoManifest.toString());
			
			StringBuilder sbSeq = new StringBuilder();
			sbSeq.append("SELECT GBMS.SEQ_MFT_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
			Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
			BigDecimal seqValue = (BigDecimal) results.get("seqVal");
			log.info("seqValue : "+ seqValue);
			cargoManifest.setMft_hscode_seq_nbr(seqValue);

			sb.append(
					"INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS(MFT_HSCODE_SEQ_NBR,MFT_SEQ_NBR,HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,");
			sb.append(
					"HS_SUB_CODE_DESC,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
			sb.append("(:mft_hscode_seq_nbr,:mft_seq_nbr,:hs_code,:custom_hs_code,:hs_sub_code_fr,:hs_sub_code_to,");
			sb.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
			sb.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
			sb.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
			sb.append(
					":cargo_description,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

			insert_result = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(cargoManifest));

			sb = new StringBuffer();
			sb.append("INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS(MFT_HSCODE_SEQ_NBR,MFT_SEQ_NBR,AUDIT_DTTM,REC_STATUS,");
			sb.append("HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,");
			sb.append(
					"HS_SUB_CODE_DESC,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
			sb.append("(:mft_hscode_seq_nbr,:mft_seq_nbr,SYSDATE, 'A',:hs_code,:custom_hs_code,:hs_sub_code_fr,:hs_sub_code_to,");
			sb.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
			sb.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
			sb.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
			sb.append(
					":cargo_description,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

			namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(cargoManifest));

			if (insert_result > 0) {
				return true;
			}
		} catch (Exception e) {
			log.info("Exception insertManifestHSDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertManifestHSDetails " + cargoManifest.toString());
		}
		return false;
	}

	private boolean updateManifestDetails(CargoManifest cargoManifest) throws BusinessException {
		StringBuffer sb_update = new StringBuffer();
		boolean update = false;
		try {
			log.info("START updateManifestDetails :cargoManifest" + cargoManifest.toString());

			sb_update.append("	UPDATE ");
			sb_update.append("		GBMS.MANIFEST_DETAILS ");
			sb_update.append("	SET ");
			sb_update.append("		CRG_TYPE =:cargoType, ");
			sb_update.append("		CRG_DES =:cargo_description, ");
			sb_update.append("		HS_CODE =:hs_code, ");
			sb_update.append("		HS_SUB_CODE_FR =:hs_sub_code_fr, ");
			sb_update.append("		HS_SUB_CODE_TO =:hs_sub_code_to, ");
			sb_update.append("		NBR_PKGS =:number_of_packages, ");
			sb_update.append("		GROSS_WT =:gross_weight_kg, ");
			sb_update.append("		GROSS_VOL =:gross_measurement_m3, ");
			sb_update.append("		CRG_STATUS =:cargo_status , ");
			sb_update.append("		DG_IND =:dg_indicator, ");
			sb_update.append("		STG_TYPE =:storage_indicator , ");
			sb_update.append("		PKG_TYPE =:packing_type, ");
			sb_update.append("		DIS_TYPE =:discharge_operation_indicator , ");
			sb_update.append("		CONS_CO_CD =:consignee, ");
			sb_update.append("		CONS_NM =:consignee_others, ");
			sb_update.append("		LD_PORT =:port_of_loading , ");
			sb_update.append("		DIS_PORT =:port_of_discharge, ");
			sb_update.append("		DES_PORT =:port_of_final_destination, ");
			sb_update.append("		UNSTUFF_SEQ_NBR =:unStuff_seq_nbr, ");
			sb_update.append("		LAST_MODIFY_USER_ID =:last_modify_user_id, ");
			sb_update.append("		LAST_MODIFY_DTTM = TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'), ");
			sb_update.append("		CUSTOM_HS_CODE = :custom_hs_code,");
			sb_update.append("		CONSIGNEE_ADDR = :consignee_addr,");
			sb_update.append("		SHIPPER_NM = :shipper_nm,");
			sb_update.append("		SHIPPER_ADDR = :shipper_addr,");
			sb_update.append("		NOTIFY_PARTY = :notify_party,");
			sb_update.append("		NOTIFY_PARTY_ADDR = :notify_party_addr,");
			sb_update.append("		PLACE_OF_DELIVERY = :place_of_delivery,");
			sb_update.append("		PLACE_OF_RECEIPT = :place_of_receipt");
			sb_update.append("	WHERE ");
			sb_update.append("		 MFT_SEQ_NBR =:mft_seq_nbr");

			log.info(" updateManifestDetails   :" + sb_update.toString());
			int rows = namedParameterJdbcTemplate.update(sb_update.toString(),
					new BeanPropertySqlParameterSource(cargoManifest));
			
			// MANIFEST_DETAILS_TRANS
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				
				//Trans_log
				sb_update = new StringBuffer();
				sb_update.append("SELECT NVL(MAX(TRANS_NBR),0) FROM GBMS.MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:mftSeqNbr");
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("mftSeqNbr", cargoManifest.getMft_seq_nbr());
				log.info(" updateManifestDetails   :" + sb_update.toString() + ", paramap :" + paramMap.toString());
				int trans_nbr = namedParameterJdbcTemplate.queryForObject(sb_update.toString(), paramMap, Integer.class);
				trans_nbr=trans_nbr+1;
				cargoManifest.setTrans_nbr(trans_nbr);
				
				sb_update= new StringBuffer();
				sb_update.append(
						"INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,BL_NBR,VAR_NBR,BL_STATUS,CRG_TYPE,CRG_DES,HS_CODE,NBR_PKGS,GROSS_WT,GROSS_VOL,");
				sb_update.append(
						"CRG_STATUS,DG_IND,STG_TYPE,PKG_TYPE,DIS_TYPE,CONS_CO_CD,CONS_NM,LD_PORT,DIS_PORT,DES_PORT,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,EDO_NBR_PKGS");
				sb_update.append(",CUSTOM_HS_CODE,CONSIGNEE_ADDR,SHIPPER_NM,SHIPPER_ADDR,NOTIFY_PARTY,NOTIFY_PARTY_ADDR,PLACE_OF_DELIVERY,PLACE_OF_RECEIPT");
				sb_update.append(")VALUES");
				sb_update.append(
						"(:trans_nbr,:mft_seq_nbr,UPPER(:bills_of_landing_no),:var_nbr,:bill_status,:cargoType,:cargo_description,:hs_code,:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
				sb_update.append(
						":cargo_status,:dg_indicator,:storage_indicator,:packing_type,:discharge_operation_indicator,:consignee,:consignee_others,");
				sb_update.append(
						":port_of_loading,:port_of_discharge,:port_of_final_destination,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'),0,");
				sb_update.append(":custom_hs_code,:consignee_addr,:shipper_nm,:shipper_addr,:notify_party,:notify_party_addr,:place_of_delivery,:place_of_receipt)");

				log.info(" updateManifestDetails   :" + sb_update.toString());
				namedParameterJdbcTemplate.update(sb_update.toString(),
						new BeanPropertySqlParameterSource(cargoManifest));
			}
			
			if (rows > 0) {
				if(!subHSDetailsExist(cargoManifest.getMft_seq_nbr()) && !cargoManifest.getHasAddProcess()) {
					StringBuilder sbSeq = new StringBuilder();
					sbSeq.append("SELECT GBMS.SEQ_MFT_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
					Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
					BigDecimal seqValue = (BigDecimal) results.get("seqVal");
					log.info("mfthsseqNbr:" + seqValue);
					cargoManifest.setMft_hscode_seq_nbr(seqValue);

					StringBuffer sb = new StringBuffer();
					sb.append(
							"INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS(MFT_HSCODE_SEQ_NBR,MFT_SEQ_NBR,HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,");
					sb.append(
							"HS_SUB_CODE_DESC,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
					sb.append("(:mft_hscode_seq_nbr,:mft_seq_nbr,:hs_code,:custom_hs_code,:hs_sub_code_fr,:hs_sub_code_to,");
					sb.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
					sb.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
					sb.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
					sb.append(
							":cargo_description,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

					log.info(" updateManifestDetails   :" + sb.toString());
					int insert_result = namedParameterJdbcTemplate.update(sb.toString(),
							new BeanPropertySqlParameterSource(cargoManifest));
					
					log.info("inserted:" + insert_result);

					sb = new StringBuffer();
					sb.append("INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS(MFT_HSCODE_SEQ_NBR,MFT_SEQ_NBR,AUDIT_DTTM,REC_STATUS,");
					sb.append("HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,");
					sb.append(
							"HS_SUB_CODE_DESC,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
					sb.append("(:mft_hscode_seq_nbr,:mft_seq_nbr,SYSDATE, 'A',:hs_code,:custom_hs_code,:hs_sub_code_fr,:hs_sub_code_to,");
					sb.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
					sb.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
					sb.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
					sb.append(
							":cargo_description,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

					log.info(" updateManifestDetails   :" + sb.toString());
					namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(cargoManifest));
				} else if(!checkMultipleMainfestHSDetails(cargoManifest.getMft_seq_nbr()) && !cargoManifest.getHasAddProcess() && !cargoManifest.getSubHSUpdate()) {
					sb_update = new StringBuffer();
					sb_update.append("	UPDATE ");
					sb_update.append("		GBMS.MANIFEST_HSCODE_DETAILS ");
					sb_update.append("	SET ");
					sb_update.append("		HS_CODE =:hs_code, ");
					sb_update.append("		CUSTOM_HS_CODE = :custom_hs_code,");
					sb_update.append("		HS_SUB_CODE_FR =:hs_sub_code_fr, ");
					sb_update.append("		HS_SUB_CODE_TO =:hs_sub_code_to, ");
					sb_update.append("		HS_SUB_CODE_DESC = ");
					sb_update.append("		(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
					sb_update.append("		HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to),");
					sb_update.append("		NBR_PKGS =:number_of_packages, ");
					sb_update.append("		GROSS_WT =:gross_weight_kg, ");
					sb_update.append("		GROSS_VOL =:gross_measurement_m3, ");
					sb_update.append("		CRG_DES =:cargo_description, ");
					sb_update.append("		LAST_MODIFY_USER_ID =:last_modify_user_id, ");
					sb_update.append("		LAST_MODIFY_DTTM = TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3') ");
					sb_update.append("	WHERE ");
					sb_update.append("		 MFT_SEQ_NBR =:mft_seq_nbr");

					log.info(" updateManifestDetails   :" + sb_update.toString());
					rows = namedParameterJdbcTemplate.update(sb_update.toString(),
							new BeanPropertySqlParameterSource(cargoManifest));
					
					sb_update = new StringBuffer();
					Map<String,String> paramMap = new HashMap<>();
					paramMap.put("mft_seq_nbr", cargoManifest.getMft_seq_nbr());
					BigDecimal mfthsseqNbr = namedParameterJdbcTemplate.queryForObject("SELECT MFT_HSCODE_SEQ_NBR FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_SEQ_NBR =:mft_seq_nbr", paramMap, BigDecimal.class);
					log.info("mfthsseqNbr:" + mfthsseqNbr);
					cargoManifest.setMft_hscode_seq_nbr(mfthsseqNbr);
					
					sb_update = new StringBuffer();
					sb_update.append("INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS(MFT_HSCODE_SEQ_NBR, MFT_SEQ_NBR,AUDIT_DTTM,REC_STATUS,");
					sb_update.append("HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,");
					sb_update.append(
							"HS_SUB_CODE_DESC,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
					sb_update.append("(:mft_hscode_seq_nbr, :mft_seq_nbr,SYSDATE, 'A',:hs_code,:custom_hs_code,:hs_sub_code_fr,:hs_sub_code_to,");
					sb_update.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
					sb_update.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
					sb_update.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
					sb_update.append(
							":cargo_description,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

					log.info(" updateManifestDetails   :" + sb_update.toString());
					namedParameterJdbcTemplate.update(sb_update.toString(), new BeanPropertySqlParameterSource(cargoManifest));
				}
			}
			
			
			log.info(rows + "cargoManifest : " + sb_update.toString());
			update = true;
		} catch (Exception e) {
			log.info("Exception updateManifestDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END updateManifestDetails " + cargoManifest.toString());
		}
		return update;
	}

	private boolean updateManifestHSDetails(CargoManifest cargoManifest) throws BusinessException {
		StringBuffer sb_update = new StringBuffer();
		boolean update = false;
		try {
			log.info("START updateManifestHSDetails :" + cargoManifest.toString());
			sb_update.append("	UPDATE ");
			sb_update.append("		GBMS.MANIFEST_HSCODE_DETAILS ");
			sb_update.append("	SET ");
			sb_update.append("		HS_CODE =:hs_code, ");
			sb_update.append("		HS_SUB_CODE_FR =:hs_sub_code_fr, ");
			sb_update.append("		HS_SUB_CODE_TO =:hs_sub_code_to, ");
			sb_update.append("		CUSTOM_HS_CODE =:custom_hs_code, ");
			sb_update.append("		NBR_PKGS =:number_of_packages, ");
			sb_update.append("		GROSS_WT =:gross_weight_kg, ");
			sb_update.append("		GROSS_VOL =:gross_measurement_m3, ");
			sb_update.append("		CRG_DES =:cargo_description, ");
			sb_update.append("		HS_SUB_CODE_DESC = ");
			sb_update.append("		(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
			sb_update.append("		HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to),");
			sb_update.append("		LAST_MODIFY_USER_ID =:last_modify_user_id, ");
			sb_update.append("		LAST_MODIFY_DTTM = TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3') ");
			sb_update.append("	WHERE ");
			sb_update.append("		 MFT_HSCODE_SEQ_NBR =:mft_hscode_seq_nbr");

			int rows = namedParameterJdbcTemplate.update(sb_update.toString(),
					new BeanPropertySqlParameterSource(cargoManifest));
			
			sb_update.setLength(0);
			sb_update.append("INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS(MFT_HSCODE_SEQ_NBR, MFT_SEQ_NBR,AUDIT_DTTM,REC_STATUS,");
			sb_update.append("HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,");
			sb_update.append("HS_SUB_CODE_DESC,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
			sb_update.append("(:mft_hscode_seq_nbr,:mft_seq_nbr,SYSDATE, 'A',:hs_code,:custom_hs_code,:hs_sub_code_fr,:hs_sub_code_to,");
			sb_update.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
			sb_update.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
			sb_update.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
			sb_update.append(":cargo_description,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

			namedParameterJdbcTemplate.update(sb_update.toString(), new BeanPropertySqlParameterSource(cargoManifest));
			
			log.info(rows + "cargoManifest : " + sb_update.toString());
			if(rows > 0) {
				if(cargoManifest.getSubHSUpdate()) {
					sb_update = new StringBuffer();
					sb_update.append("	UPDATE ");
					sb_update.append("		GBMS.MANIFEST_DETAILS ");
					sb_update.append("	SET ");
					sb_update.append("		HS_CODE =:hs_code, ");
					sb_update.append("		HS_SUB_CODE_FR =:hs_sub_code_fr, ");
					sb_update.append("		HS_SUB_CODE_TO =:hs_sub_code_to, ");
					sb_update.append("		CUSTOM_HS_CODE =:custom_hs_code, ");
					sb_update.append("		CRG_DES =:cargo_description ");
					sb_update.append("	WHERE ");
					sb_update.append("		 MFT_SEQ_NBR =:mft_seq_nbr");
					sb_update.append("		 AND HS_CODE =:oldHSCode ");
					sb_update.append("		 AND HS_SUB_CODE_FR =:oldHSCode_fr ");
					sb_update.append("		 AND HS_SUB_CODE_TO =:oldHSCode_to ");
					

					rows = namedParameterJdbcTemplate.update(sb_update.toString(), new BeanPropertySqlParameterSource(cargoManifest));
					
					if(rows > 0 ) {
						sb_update = new StringBuffer();
						sb_update.append("SELECT NVL(MAX(TRANS_NBR),0) FROM GBMS.MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:mftSeqNbr");
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("mftSeqNbr", cargoManifest.getMft_seq_nbr());
						log.info(" updateManifestDetails   :" + sb_update.toString() + ", paramap :" + paramMap.toString());
						int trans_nbr = namedParameterJdbcTemplate.queryForObject(sb_update.toString(), paramMap, Integer.class);
						trans_nbr=trans_nbr+1;
						cargoManifest.setTrans_nbr(trans_nbr);
						
						sb_update= new StringBuffer();
						sb_update.append(
								"INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,BL_NBR,VAR_NBR,BL_STATUS,CRG_TYPE,CRG_DES,HS_CODE,NBR_PKGS,GROSS_WT,GROSS_VOL,");
						sb_update.append(
								"CRG_STATUS,DG_IND,STG_TYPE,PKG_TYPE,DIS_TYPE,CONS_CO_CD,CONS_NM,LD_PORT,DIS_PORT,DES_PORT,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,EDO_NBR_PKGS");
						sb_update.append(",CUSTOM_HS_CODE,CONSIGNEE_ADDR,SHIPPER_NM,SHIPPER_ADDR,NOTIFY_PARTY,NOTIFY_PARTY_ADDR,PLACE_OF_DELIVERY,PLACE_OF_RECEIPT");
						sb_update.append(")VALUES");
						sb_update.append(
								"(:trans_nbr,:mft_seq_nbr,UPPER(:bills_of_landing_no),:var_nbr,:bill_status,:cargoType,:cargo_description,:hs_code,:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
						sb_update.append(
								":cargo_status,:dg_indicator,:storage_indicator,:packing_type,:discharge_operation_indicator,:consignee,:consignee_others,");
						sb_update.append(
								":port_of_loading,:port_of_discharge,:port_of_final_destination,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'),0,");
						sb_update.append(":custom_hs_code,:consignee_addr,:shipper_nm,:shipper_addr,:notify_party,:notify_party_addr,:place_of_delivery,:place_of_receipt)");
						namedParameterJdbcTemplate.update(sb_update.toString(),
								new BeanPropertySqlParameterSource(cargoManifest));
					}
				}
			}
			update = true;
		} catch (Exception e) {
			log.info("Exception updateManifestHSDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END updateManifestHSDetails " + cargoManifest.toString());
		}
		return update;
	}

	private boolean processCargoMarking(CargoManifest cargoManifest) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		boolean processed = false;
		try {
			log.info("START processCargoMarking :" + cargoManifest.toString());

			sb.append("SELECT COUNT(MFT_SQ_NBR) FROM GBMS.MFT_MARKINGS WHERE MFT_SQ_NBR=:mftSeqNbr");
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("mftSeqNbr", cargoManifest.getMft_seq_nbr());
			log.info("processCargoMarking " + sb.toString() + ", paramap :" + paramMap.toString());
			int count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);

			if (count == 0) { // insert
				sb = new StringBuffer();
				sb.append(
						"INSERT INTO GBMS.MFT_MARKINGS (MFT_SQ_NBR, MFT_MARKINGS, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
				sb.append(
						" VALUES(:mft_seq_nbr, :cargo_marking, :last_modify_user_id, TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3')) ");
				namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(cargoManifest));
				
				//Trans_log
				sb = new StringBuffer();
				sb.append("INSERT INTO MFT_MARKINGS_TRANS(TRANS_NBR,MFT_SQ_NBR,MFT_MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				sb.append("VALUES('0',:mft_seq_nbr,:cargo_marking,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");
				namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(cargoManifest));
				
				return true;
			} else {
				// update
				sb = new StringBuffer();
				sb.append("UPDATE GBMS.MFT_MARKINGS  ");
				sb.append(
						" SET MFT_MARKINGS=:cargo_marking,  LAST_MODIFY_USER_ID=:last_modify_user_id, LAST_MODIFY_DTTM=TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3')  ");
				sb.append(" WHERE MFT_SQ_NBR=:mft_seq_nbr ");
				namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(cargoManifest));
				
				//Trans_log
				sb = new StringBuffer();
				sb.append("SELECT NVL(MAX(TRANS_NBR),0) FROM GBMS.MFT_MARKINGS_TRANS WHERE MFT_SQ_NBR=:mftSeqNbr");
				paramMap = new HashMap<String, String>();
				paramMap.put("mftSeqNbr", cargoManifest.getMft_seq_nbr());
				log.info(" processCargoMarking MFT_MARKINGS_TRANS  :" + sb.toString() + ", paramap :" + paramMap.toString());
				int trans_nbr = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
				trans_nbr=trans_nbr+1;
				cargoManifest.setTrans_nbr(trans_nbr);
				sb = new StringBuffer();
				sb.append("INSERT INTO MFT_MARKINGS_TRANS(TRANS_NBR,MFT_SQ_NBR,MFT_MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				sb.append("VALUES(:trans_nbr,:mft_seq_nbr,:cargo_marking,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");
				namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(cargoManifest));
		
				
				processed = true;
			}
		} catch (Exception e) {
			log.info("Exception processCargoMarking : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END processCargoMarking ");
		}
		return processed;
	}

	private boolean processCargoSelection(CargoManifest cargoManifest) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		boolean processCargo = true;
		try {

			log.info("START processCargoSelection :" + cargoManifest.toString());

			Map<String, String> paramMap = new HashMap<String, String>();
			sb = new StringBuffer();
			sb.append("SELECT COUNT(*) FROM GBMS.MANIFEST_DETAILS_EXT WHERE MFT_SEQ_NBR=:mft_seq_nbr ");
			// sb.append("AND HS_SUB_DESC_CD =:cargo_selection");
			paramMap.put("mft_seq_nbr", cargoManifest.getMft_seq_nbr());
			// paramMap.put("cargo_selection", cargoManifest.getCargo_selection());
			log.info("processCargoSelection " + sb.toString() + ", paramap :" + paramMap.toString());
			int count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
			// manifest ext check - based on billno , cargo selection cd , status

			CargoSelection cs = new CargoSelection();
			cs.setMft_seq_nbr(cargoManifest.getMft_seq_nbr());
			cs.setCargo_selection_cd(cargoManifest.getCargo_selection());
			cs.setLast_modify_user_id(cargoManifest.getLast_modify_user_id());
			cs.setLast_modify_dttm(cargoManifest.getLast_modify_dttm());

			if (count == 0 && cargoManifest.getCargo_selection() != null
					&& !cargoManifest.getCargo_selection().isEmpty()) {

				sb = new StringBuffer();
				sb.append(
						"INSERT INTO GBMS.MANIFEST_DETAILS_EXT( MFT_SEQ_NBR, HS_SUB_DESC_CD, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) VALUES (:mft_seq_nbr,:cargo_selection_cd, :last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

				log.info("processCargoSelection :" + "SQL:" + sb.toString() + ", paramap: " + paramMap.toString());
				namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(cs));

			} else {
				sb = new StringBuffer();
				sb.append("	UPDATE ");
				sb.append("		GBMS.MANIFEST_DETAILS_EXT ");
				sb.append("	SET ");
				sb.append("		HS_SUB_DESC_CD = :cargo_selection_cd, ");
				sb.append("		LAST_MODIFY_USER_ID = :last_modify_user_id, ");
				sb.append("		LAST_MODIFY_DTTM = TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3') ");
				sb.append("	WHERE ");
				sb.append("		MFT_SEQ_NBR =:mft_seq_nbr");
				log.info("processCargoSelection :" + "SQL:" + sb.toString() + ", paramap: " + paramMap.toString());
				namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(cs));

			}

		} catch (Exception e) {
			log.info("Exception processCargoSelection : ", e);
			processCargo = false;
			throw new BusinessException("M4201");
		} finally {
			log.info("END processCargoSelection ");
		}
		return processCargo;
	}

	@Override
	public CargoSelection CargoSelectionData(String cargoSelection) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		CargoSelection obj = null;
		try {
			// check exist or not
			Map<String, String> paramMap = new HashMap<String, String>();
			sb = new StringBuffer();
			sb.append("SELECT count(MISC_TYPE_CD) ");
			sb.append(" FROM TOPS.SYSTEM_CONFIG ");
			sb.append(" WHERE MISC_TYPE_NM =:cargo_selection AND CAT_CD='CARGO_SELECTION' AND REC_STATUS ='A' ");
			paramMap.put("cargo_selection", cargoSelection);
			log.info("CargoSelectionData " + sb.toString() + ", paramap :" + paramMap.toString());
			int count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
			if (count > 0) {
				sb = new StringBuffer();
				sb.append("SELECT MISC_TYPE_CD as cargo_selection_cd,MISC_TYPE_NM AS cargo_selection ");
				sb.append(" FROM TOPS.SYSTEM_CONFIG");
				sb.append(" WHERE MISC_TYPE_NM =:cargo_selection AND CAT_CD='CARGO_SELECTION' AND REC_STATUS ='A' ");
				obj = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
						new BeanPropertyRowMapper<CargoSelection>(CargoSelection.class));
			}
		} catch (Exception e) {
			log.info("Exception CargoSelectionData : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END CargoSelectionData ");
		}
		return obj;
	}

	private boolean processHatchDetails(CargoManifest cargoManifest) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		boolean result = true;
		try {
			log.info("START processHatchDetails :" + cargoManifest.toString());
			if (cargoManifest.getHatchList() != null && cargoManifest.getHatchList().size() > 0) {
				for (HatchDetails hatchBreakdown : cargoManifest.getHatchList()) {

					// record exists
					sb = new StringBuffer();
					sb.append(
							"SELECT COUNT(*) FROM GBMS.MANIFEST_HATCH_DETAILS mhd WHERE MFT_SEQ_NBR =:mft_seq_nbr AND HATCH_CD =:hatchCd");
					log.info("hatchDetailsDuplicateCheck " + sb.toString());
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mft_seq_nbr", cargoManifest.getMft_seq_nbr());
					paramMap.put("hatchCd", hatchBreakdown.getHatch_cd());
					int cnt = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);

					hatchBreakdown.setMft_seq_nbr(cargoManifest.getMft_seq_nbr());
					hatchBreakdown.setLast_modify_user_id(cargoManifest.getLast_modify_user_id());
					hatchBreakdown.setLast_modify_dttm(cargoManifest.getLast_modify_dttm());
					// Hatch details insert
					if (cnt == 0) {

						sb = new StringBuffer();
						sb.append("INSERT INTO GBMS.MANIFEST_HATCH_DETAILS( ");
						sb.append("MFT_SEQ_NBR, HATCH_CD,  ");
						sb.append("NBR_PKGS, GROSS_WT, GROSS_VOL, LAST_MODIFY_USER_ID, ");
						sb.append("LAST_MODIFY_DTTM)VALUES(");
						sb.append(
								":mft_seq_nbr, :hatch_cd, :nbr_pkgs, :gross_wt, :gross_vol, :last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3')) ");
						log.info("processHatchDetails :" + "SQL:" + sb.toString() + ", hatchBreakdown: "
								+ hatchBreakdown.toString());
						namedParameterJdbcTemplate.update(sb.toString(),
								new BeanPropertySqlParameterSource(hatchBreakdown));

					} else {
						// update
						sb = new StringBuffer();
						sb.append("UPDATE ");
						sb.append("	GBMS.MANIFEST_HATCH_DETAILS ");
						sb.append("SET ");
						sb.append("	NBR_PKGS = :nbr_pkgs, ");
						sb.append("	GROSS_WT = :gross_wt, ");
						sb.append("	GROSS_VOL = :gross_vol, ");
						sb.append("	LAST_MODIFY_USER_ID = :last_modify_user_id, ");
						sb.append("	LAST_MODIFY_DTTM = TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3') ");
						sb.append("WHERE ");
						sb.append("	MFT_SEQ_NBR =:mft_seq_nbr and HATCH_CD =:hatch_cd ");
						log.info("processHatchDetails :" + "SQL:" + sb.toString() + ", hatchBreakdown: "
								+ hatchBreakdown.toString());
						namedParameterJdbcTemplate.update(sb.toString(),
								new BeanPropertySqlParameterSource(hatchBreakdown));
					}
				}
			}
		} catch (Exception e) {
			log.info("Exception processHatchDetails : ", e);
			result = false;
			throw new BusinessException("M4201");
		} finally {
			log.info("END processHatchDetails ");
		}
		return result;

	}

	private String getMftSeqNbr(String bl_nbr, String vvCd) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		String mft_seq_nbr = "0";
		try {
			log.info("START getMftSeqNbr :bl_nbr :" + bl_nbr + ", vvCd :" + vvCd);
			Map<String, String> paramMap = new HashMap<String, String>();
			sb = new StringBuffer();
			sb.append("SELECT count(mft_seq_nbr) ");
			sb.append(" FROM GBMS.MANIFEST_DETAILS ");
			sb.append(" WHERE VAR_NBR=:vvCd AND bl_nbr =:bl_nbr and bl_status='A' ");
			paramMap.put("bl_nbr", bl_nbr);
			paramMap.put("vvCd", vvCd);
			log.info("getMftSeqNbr " + sb.toString() + ", paramap :" + paramMap.toString());
			int count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
			if (count > 0) {
				sb = new StringBuffer();
				sb.append(
						"SELECT mft_seq_nbr FROM GBMS.MANIFEST_DETAILS WHERE VAR_NBR=:vvCd AND bl_nbr =:bl_nbr and bl_status='A'");
				mft_seq_nbr = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
				log.info("getMftSeqNbr mft_seq_nbr " + mft_seq_nbr);
			}
		} catch (Exception e) {
			log.info("Exception getMftSeqNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("START getMftSeqNbr :bl_nbr :" + bl_nbr + ", vvCd :" + vvCd + ", mft_seq_nbr :" + mft_seq_nbr);
		}
		return mft_seq_nbr;
	}

	private boolean deleteCargoDetails(String userId, String mft_seq_nbr, String blNo,String vvCd) throws BusinessException {
		StringBuffer sb_delete = new StringBuffer();
		boolean deleted = true;
		try {
			log.info("START deleteCargoDetails :" + mft_seq_nbr);
			sb_delete.append("		UPDATE");
			sb_delete.append("		GBMS.MANIFEST_DETAILS");
			sb_delete.append("	SET");
			sb_delete.append("		BL_STATUS ='X',	 ");
			sb_delete.append("		LAST_MODIFY_USER_ID =:last_modify_user_id,");
			sb_delete.append("		LAST_MODIFY_DTTM = SYSDATE");
			sb_delete.append("	WHERE");
			sb_delete.append("		 MFT_SEQ_NBR =:mft_seq_nbr ");
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("mft_seq_nbr", mft_seq_nbr);
			paramMap.put("last_modify_user_id", userId);
			int rows = namedParameterJdbcTemplate.update(sb_delete.toString(), paramMap);
			log.info(rows);
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				//Trans_log
				sb_delete = new StringBuffer();
				sb_delete.append("SELECT NVL(MAX(TRANS_NBR),0) FROM GBMS.MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:mftSeqNbr");
				paramMap = new HashMap<String, String>();
				paramMap.put("mftSeqNbr",mft_seq_nbr);
				log.info(" deleteCargoDetails   :" + sb_delete.toString() + ", paramap :" + paramMap.toString());
				Integer trans_nbr = namedParameterJdbcTemplate.queryForObject(sb_delete.toString(), paramMap, Integer.class);
				trans_nbr=trans_nbr+1;
				
				sb_delete= new StringBuffer();
				sb_delete.append(" INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,VAR_NBR,BL_NBR,BL_STATUS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM ) ");
				sb_delete.append(" VALUES(:trans_nbr,:mftSeqNbr,:vvCd,:blNo,'X',:userId, SYSDATE) ");
				paramMap = new HashMap<String, String>();
				paramMap.put("trans_nbr",trans_nbr.toString());
				paramMap.put("mftSeqNbr",mft_seq_nbr);
				paramMap.put("blNo",blNo);
				paramMap.put("vvCd",vvCd);
				paramMap.put("userId",userId);
				namedParameterJdbcTemplate.update(sb_delete.toString(), paramMap);
			}
		} catch (Exception e) {
			log.info("Exception deleteCargoDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END deleteCargoDetails ");
		}
		return deleted;
	}
	
	private boolean deleteHSDetails(CargoManifest cargoManifest, String userId) throws BusinessException {
		StringBuffer sb_delete = new StringBuffer();
		boolean deleted = true;
		try {
			log.info("START deleteCargoDetails :" + cargoManifest.toString());
			sb_delete.append("DELETE FROM GBMS.MANIFEST_HSCODE_DETAILS ");
			sb_delete.append("WHERE MFT_SEQ_NBR =:mft_seq_nbr ");
			sb_delete.append("AND MFT_HSCODE_SEQ_NBR=:mft_hscode_seq_nbr");
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("mft_seq_nbr", cargoManifest.getMft_seq_nbr());
			paramMap.put("mft_hscode_seq_nbr", cargoManifest.getMft_hscode_seq_nbr().toString());
			paramMap.put("last_modify_user_id", userId);
			int rows = namedParameterJdbcTemplate.update(sb_delete.toString(), paramMap);
			log.info(rows);

			sb_delete = new StringBuffer();
			sb_delete.append("INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS_TRANS(MFT_HSCODE_SEQ_NBR,MFT_SEQ_NBR,AUDIT_DTTM,REC_STATUS,");
			sb_delete.append("HS_CODE,CUSTOM_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,");
			sb_delete.append(
					"HS_SUB_CODE_DESC,NBR_PKGS,GROSS_WT,GROSS_VOL,CRG_DES,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES");
			sb_delete.append("(:mft_hscode_seq_nbr,:mft_seq_nbr,SYSDATE, 'I',:hs_code,:custom_hs_code,:hs_sub_code_fr,:hs_sub_code_to,");
			sb_delete.append("(SELECT HS_SUB_DESC FROM GBMS.HS_SUB_CODE WHERE HS_CODE=:hs_code AND ");
			sb_delete.append("HS_SUB_CODE_FR = :hs_sub_code_fr AND HS_SUB_CODE_TO = :hs_sub_code_to)");
			sb_delete.append(",:number_of_packages,:gross_weight_kg,:gross_measurement_m3,");
			sb_delete.append(
					":cargo_description,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

			namedParameterJdbcTemplate.update(sb_delete.toString(), new BeanPropertySqlParameterSource(cargoManifest));

		} catch (Exception e) {
			log.info("Exception deleteCargoDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END deleteCargoDetails ");
		}
		return deleted;
	}

	// EndRegion

	// Region HatchBD

	@Override
	public List<ManifestDetails> getManifestDetails(Criteria criteria) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		List<ManifestDetails> manifestList = null;
		try {
			log.info("getManifestDetails DAO criteria:" + criteria.toString());
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			sb.append("SELECT ");
			sb.append("	DET.VAR_NBR AS vvCd, ");
			sb.append("	C.VSL_NM AS vesselName, ");
			sb.append("	(C.IN_VOY_NBR || '-' || C.OUT_VOY_NBR) voyageNo, ");
			sb.append("	DET.MFT_SEQ_NBR mftSeq, ");
			sb.append("	DET.BL_NBR AS billNo, ");
			sb.append("	DET.CRG_DES AS cargoDes, ");
			sb.append("	DET.GROSS_WT AS grossWt, ");
			sb.append("	DET.NBR_PKGS AS nbrPkgs, ");
			sb.append("	DET.GROSS_VOL AS grossVol, ");
			sb.append("	DET.HS_CODE AS hsCode ");
			sb.append("FROM ");
			sb.append("	GBMS.MANIFEST_DETAILS DET ");
			sb.append("LEFT JOIN TOPS.VESSEL_CALL c ON ");
			sb.append("	DET.VAR_NBR = c.VV_CD ");
			sb.append("WHERE ");
			sb.append("	DET.VAR_NBR =:vvCd");
			sb.append(" AND DET.BL_STATUS='A' ");
			sb.append("		ORDER BY DET.BL_NBR ");
			paramMap.put("vvCd", vvCd);
			log.info("getManifestDetails SQl" + sb.toString() + "paramap" + paramMap.toString());
			manifestList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<ManifestDetails>(ManifestDetails.class));
			log.info("getManifestDetails manifestList " + manifestList.toString());

		} catch (Exception e) {
			log.info("Exception getManifestDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getManifestDetails DAO");
		}

		return manifestList;

	}

	@Override
	public boolean saveManifestHatchDetails(HatchWisePackageDetail saveManifestHatchDet) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		boolean saved = true;
		try {
			log.info("START: saveManifestHatchDetails DAO saveManifestHatchDet:" + saveManifestHatchDet.toString());

			String last_modify_dttm = getTimeStamp();

			List<HatchDetails> hatchBreakDownInsertList = new ArrayList<HatchDetails>();
			List<HatchDetails> hatchBreakDownUpdateList = new ArrayList<HatchDetails>();
			HatchDetails hatchObj = null;
			for (HatchBreakDownPageDetail.HatchDetail hatchDetailObj : saveManifestHatchDet.getHatchDetail()) {
				for (HatchBreakDownPageDetail.HatchInfo hatchInfoObj : hatchDetailObj.getHatchInfo()) {
					if (!hatchInfoObj.getHatchNo().equalsIgnoreCase(ConstantUtil.noHatchCode)) {
						if (hatchInfoObj.getMftHatchSeqNbr().equalsIgnoreCase("0")) {
							hatchObj = new HatchDetails();
							hatchObj.setMft_seq_nbr(hatchDetailObj.getMftSeqNbr());
							hatchObj.setHatch_cd(ConstantUtil.hatchCode + hatchInfoObj.getHatchNo());
							hatchObj.setNbr_pkgs(hatchInfoObj.getPackages());
							hatchObj.setGross_vol(hatchInfoObj.getVolume());
							hatchObj.setGross_wt(hatchInfoObj.getWeight());
							hatchObj.setLast_modify_user_id(saveManifestHatchDet.getUserId());
							hatchObj.setLast_modify_dttm(last_modify_dttm);
							hatchBreakDownInsertList.add(hatchObj);
						} else {
							hatchObj = new HatchDetails();
							hatchObj.setMft_hatch_seq_nbr(Long.parseLong(hatchInfoObj.getMftHatchSeqNbr()));
							hatchObj.setMft_seq_nbr(hatchDetailObj.getMftSeqNbr());
							hatchObj.setHatch_cd(ConstantUtil.hatchCode + hatchInfoObj.getHatchNo());
							hatchObj.setNbr_pkgs(hatchInfoObj.getPackages());
							hatchObj.setGross_vol(hatchInfoObj.getVolume());
							hatchObj.setGross_wt(hatchInfoObj.getWeight());
							hatchObj.setLast_modify_user_id(saveManifestHatchDet.getUserId());
							hatchObj.setLast_modify_dttm(last_modify_dttm);
							hatchBreakDownUpdateList.add(hatchObj);
						}

					}
				}
			}

			sb = new StringBuilder();
			sb.append(" INSERT INTO GBMS.MANIFEST_HATCH_DETAILS ");
			sb.append(" (MFT_SEQ_NBR,HATCH_CD,NBR_PKGS,GROSS_WT,");
			sb.append(" GROSS_VOL,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
			sb.append(
					" VALUES(:mft_seq_nbr,:hatch_cd,:nbr_pkgs,:gross_wt,:gross_vol,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3')) ");

			SqlParameterSource[] insertInput = SqlParameterSourceUtils.createBatch(hatchBreakDownInsertList.toArray());
			log.info("SQL" + sb.toString() + "input:" + insertInput.toString());
			namedParameterJdbcTemplate.batchUpdate(sb.toString(), insertInput);

			sb = new StringBuilder();
			sb.append(
					" UPDATE  GBMS.MANIFEST_HATCH_DETAILS SET NBR_PKGS=:nbr_pkgs,GROSS_WT=:gross_wt,GROSS_VOL=:gross_vol, ");
			sb.append(
					" LAST_MODIFY_USER_ID=:last_modify_user_id,LAST_MODIFY_DTTM=TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3')  WHERE MFT_HATCH_SEQ_NBR=:mft_hatch_seq_nbr ");

			SqlParameterSource[] updateInput = SqlParameterSourceUtils.createBatch(hatchBreakDownUpdateList.toArray());
			log.info("SQL" + sb.toString() + "input:" + updateInput.toString());
			namedParameterJdbcTemplate.batchUpdate(sb.toString(), updateInput);

			// Action trail insert
			sb = new StringBuilder();
			sb.append(" INSERT INTO GBMS.MANIFEST_ACT_TRL (VV_CD,TYPE_CD,REMARKS,LAST_MODIFY_USER_ID, ");
			sb.append(" LAST_MODIFY_DTTM) VALUES ");
			sb.append(
					" (:vv_cd,:type_cd,:remarks,:last_modify_user_id,TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3')) ");
			ManifestActionTrail manifestActionTrl = new ManifestActionTrail();
			manifestActionTrl.setRemarks(saveManifestHatchDet.getRemarks());
			manifestActionTrl.setVv_cd(saveManifestHatchDet.getVvCd());
			manifestActionTrl.setType_cd(ConstantUtil.manifest_type_cd);
			manifestActionTrl.setLast_modify_user_id(saveManifestHatchDet.getUserId());
			manifestActionTrl.setLast_modify_dttm(last_modify_dttm);
			log.info("manifestActionTrl SQL" + sb.toString() + "manifestActionTrl:" + manifestActionTrl.toString());
			namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(manifestActionTrl));

		} catch (Exception e) {
			log.info("Exception saveManifestHatchDetails : ", e);
			saved = false;
			throw new BusinessException("M4201");
		} finally {
			log.info("END: saveManifestHatchDetails DAO END");
		}
		return saved;
	}

	@Override
	public List<ManifestDetails> getManifestHistoryDetailsForHBD(Criteria criteria) throws BusinessException {

		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		List<ManifestDetails> manifestList = null;
		try {
			log.info("getManifestHistoryDetailsForHBD DAO criteria:" + criteria.toString());

			String actTrlId = CommonUtility.deNull(criteria.getPredicates().get("mftActTrlId"));

			sb.append("SELECT DISTINCT ");
			// sb.append(" DET.VAR_NBR AS vvCd, ");
			// sb.append(" C.VSL_NM AS vesselName, ");
			// sb.append(" (C.IN_VOY_NBR || '-' || C.OUT_VOY_NBR) voyageNo, ");
			sb.append("	DET.MFT_SEQ_NBR mftSeq, ");
			sb.append("	DET.BL_NBR AS billNo, ");
			sb.append("	DET.CRG_DES AS cargoDes, ");
			sb.append("	DET.GROSS_WT AS grossWt, ");
			sb.append("	DET.NBR_PKGS AS nbrPkgs, ");
			sb.append("	DET.GROSS_VOL AS grossVol, ");
			sb.append("	DET.HS_CODE AS hsCode ");
			sb.append("FROM ");
			sb.append("	GBMS.MANIFEST_DETAILS DET ");
			sb.append("	JOIN GBMS.AUDIT_MANIFEST_HATCH_DETAILS H ON DET.MFT_SEQ_NBR = H.MFT_SEQ_NBR ");
			sb.append(" JOIN GBMS.MANIFEST_ACT_TRL  adt on adt.LAST_MODIFY_DTTM=h.LAST_MODIFY_DTTM ");
			// sb.append("LEFT JOIN TOPS.VESSEL_CALL c ON ");
			// sb.append(" DET.VAR_NBR = c.VV_CD ");
			sb.append("WHERE 1=1  ");
			sb.append("	AND adt.MFT_ACT_TRL_ID =:actTrlId ");
			sb.append("		ORDER BY DET.BL_NBR ");
			paramMap.put("actTrlId", actTrlId);

			log.info("getManifestHistoryDetailsForHBD SQl" + sb.toString() + "paramap" + paramMap.toString());
			manifestList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<ManifestDetails>(ManifestDetails.class));
			log.info("getManifestHistoryDetailsForHBD manifestList " + manifestList.toString());

		} catch (Exception e) {
			log.info("Exception getManifestHistoryDetailsForHBD : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getManifestHistoryDetailsForHBD DAO");
		}
		return manifestList;
	}

	@Override
	public List<HatchDetails> getManifestHatchBDHistoryDetails(Criteria criteria) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		List<HatchDetails> manifestHatchDtlList = null;
		try {
			log.info("START: getManifestHatchBDHistoryDetails DAO Start criteria:" + criteria.toString());
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String actTrlId = CommonUtility.deNull(criteria.getPredicates().get("mftActTrlId"));
			sb.append("SELECT ");
			sb.append("	H.MFT_HATCH_SEQ_NBR AS mft_hatch_seq_nbr, ");
			sb.append("	H.MFT_SEQ_NBR AS mft_seq_nbr, ");
			sb.append("	H.HATCH_CD AS hatch_cd, ");
			sb.append("	H.NBR_PKGS AS nbr_pkgs, ");
			sb.append("	H.GROSS_WT AS gross_wt, ");
			sb.append("	H.GROSS_VOL AS gross_vol, ");
			sb.append("	DET.BL_NBR AS billNo, ");
			sb.append("	DET.HS_CODE AS hsCode, ");
			sb.append(" DET.CRG_DES AS cargoDesc ");
			sb.append("FROM ");
			sb.append("	GBMS.AUDIT_MANIFEST_HATCH_DETAILS H ");
			sb.append(" JOIN GBMS.MANIFEST_ACT_TRL  adt on adt.LAST_MODIFY_DTTM=h.LAST_MODIFY_DTTM ");
			sb.append("LEFT JOIN GBMS.MANIFEST_DETAILS DET ON ");
			sb.append("	H.MFT_SEQ_NBR = DET.MFT_SEQ_NBR ");
			sb.append("WHERE 1=1 ");
			// sb.append(" AND DET.VAR_NBR =:vvCd ");
			sb.append("	AND adt.MFT_ACT_TRL_ID =:actTrlId ");
			sb.append("	ORDER BY DET.BL_NBR ");
			paramMap.put("vvCd", vvCd);
			paramMap.put("actTrlId", actTrlId);
			log.info("getManifestHatchBDHistoryDetails SQL" + sb.toString() + "parammap" + paramMap.toString());
			manifestHatchDtlList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<HatchDetails>(HatchDetails.class));
			log.info("getManifestHatchBDHistoryDetails response " + manifestHatchDtlList.toString());
		} catch (Exception e) {
			log.info("Exception getManifestHatchBDHistoryDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getManifestHatchBDHistoryDetails DAO ");
		}
		return manifestHatchDtlList;

	}

	// EndRegion

	// Region CargoDimensionDeclaration

	@Override
	public List<CargoDimensionDeclaration> getCargoDimensionDeclarationInfo(String vvCd, String userId) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		try {
			log.info("START: getCargoDimensionDeclarationInfo  DAO  Start Obj :" + vvCd.toString());
			String vvCdCount = " SELECT COUNT(*) from GBMS.MANIFEST_DIM_DECLARATION   WHERE VV_CD =:vvCd ";
			paramMap.put("vvCd", vvCd);
			log.info("COUNT SQL" + vvCdCount + "paramMap:" + paramMap.toString());
			int count = namedParameterJdbcTemplate.queryForObject(vvCdCount.toString(), paramMap, Integer.class);
			log.info("COUNT :" + count);
			if (count > 0) {
				List<CargoDimensionDeclaration> cargoDimensionDetailList = new ArrayList<CargoDimensionDeclaration>();
				sb.append(
						" SELECT MDD.MFT_DIM_DECLR_SEQ_NBR dimDeclarSeqNum,MDD.ANSWER_TXT answer, MDD.HS_CODE hsCode,");
				sb.append(
						" MDD.HS_SUB_CODE_FR hsSubCodeFrom,MDD.HS_SUB_CODE_TO hsSubCodeTo,MDD.QUESTION_DESC question, ");
				sb.append(
						" MDD.OPTION_TXT optionsString,ac.login_id userId,ac.user_nm userName,TO_CHAR(MDD.LAST_MODIFY_DTTM, 'dd/mm/yyyy HH24:MI:SS') inputTime, HS.HS_SUB_DESC hsSubDesc ");
				sb.append(" from GBMS.MANIFEST_DIM_DECLARATION MDD  ");
				sb.append(" LEFT JOIN GBMS.HS_SUB_CODE HS ON ");
				sb.append(" hs.HS_CODE = mdd.HS_CODE ");
				sb.append(" AND HS.HS_SUB_CODE_FR = MDD.HS_SUB_CODE_FR ");
				sb.append(" AND HS.HS_SUB_CODE_TO = MDD.HS_SUB_CODE_TO ");
				sb.append(" LEFT JOIN TOPS.logon_acct ac ON ac.login_id= SUBSTR(MDD.LAST_MODIFY_USER_ID, INSTR( MDD.LAST_MODIFY_USER_ID, '/', -1 ) + 1 )    ");
				sb.append(" WHERE MDD.VV_CD =:vvCd ORDER BY MDD.MFT_DIM_DECLR_SEQ_NBR ASC ");
				paramMap.put("vvCd", vvCd);
				
				log.info("COUNT SQL" + sb.toString());
				cargoDimensionDetailList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
						new BeanPropertyRowMapper<CargoDimensionDeclaration>(CargoDimensionDeclaration.class));
				
				log.info(" GetCargoDimensionDeclarationInfo End" + cargoDimensionDetailList.toString());
				return cargoDimensionDetailList;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.info("Exception getCargoDimensionDeclarationInfo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoDimensionDeclarationInfo  DAO  END");
		}
	}

	@Override
	public List<CargoDimensionDeclaration> getHsCodeInfo() throws BusinessException {
		StringBuilder sb = new StringBuilder();
		List<CargoDimensionDeclaration> hsCodeDetailsList = new ArrayList<CargoDimensionDeclaration>();
		try {
			log.info("START: getHsCodeInfo  DAO  Start Obj ");
			sb.append(
					" SELECT HS_CODE hsCode, HS_SUB_CODE_FR hsSubCodeFrom, HS_SUB_CODE_TO hsSubCodeTo, HS_SUB_DESC hsSubDesc from GBMS.HS_SUB_CODE ");
			log.info("List SQL" + sb.toString());
			hsCodeDetailsList = namedParameterJdbcTemplate.query(sb.toString(),
					new BeanPropertyRowMapper<CargoDimensionDeclaration>(CargoDimensionDeclaration.class));
			log.info(" getHsCodeInfo End" + hsCodeDetailsList.toString());
		} catch (Exception e) {
			log.info("Exception getHsCodeInfo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getHsCodeInfo  DAO  END");
		}
		return hsCodeDetailsList;
	}

	@Override
	public List<SystemConfigList> getCargoDimensionDeclarationInfo(String userId) throws BusinessException {
		List<SystemConfigList> systemConfigList = new ArrayList<SystemConfigList>();
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		try {
			log.info("START: getCargoDimensionDeclarationInfo  DAO  Start Obj ");

			sb.append("SELECT REMARKS remarks ");
			sb.append("FROM TOPS.SYSTEM_CONFIG  ");
			// sb.append(" LEFT JOIN TOPS.logon_acct ac ON ac.login_id= :userId ");
			sb.append("  where CAT_CD='MFT_DIM_DECLARATION' ORDER BY MISC_TYPE_NM ");
			paramMap.put("userId", userId);
			log.info(" SQL" + sb.toString() + ", paramMap :" + paramMap.toString());
			systemConfigList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<SystemConfigList>(SystemConfigList.class));
			log.info(" GetCargoDimensionDeclarationInfo End" + systemConfigList.toString());
		} catch (Exception e) {
			log.info("Exception getCargoDimensionDeclarationInfo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoDimensionDeclarationInfo  DAO  END");
		}
		return systemConfigList;
	}

	@Override
	public Result saveCargoDimensionDeclaration(List<CargoDimensionDeclaration> info,String userAcct) throws BusinessException {
		StringBuilder sb = null;
		Result result = new Result();
		Map<String, String> paramMap = new HashMap<>();
		if (info.isEmpty()) {
			return null;
		}
		try {
			result.setSuccess(true);
			log.info("START: saveCargoDimensionDeclaration  DAO  :" + info.toString());

			for (CargoDimensionDeclaration obj : info) {
				obj.setUserId(userAcct);
				sb = new StringBuilder();
				sb.append("SELECT count(*) ");
				sb.append(" FROM  GBMS.MANIFEST_DIM_DECLARATION ");
				sb.append(
						" WHERE VV_CD =:vvCd AND HS_CODE=:hsCode AND HS_SUB_CODE_FR=:hsSubCodeFrom AND HS_SUB_CODE_TO=:hsSubCodeTo");

				paramMap.put("vvCd", obj.getVvCd());
				paramMap.put("hsCode", obj.getHsCode());
				paramMap.put("hsSubCodeFrom", obj.getHsSubCodeFrom());
				paramMap.put("hsSubCodeTo", obj.getHsSubCodeTo());
				int count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
				log.info("SQL :"+ sb.toString() +", params "+ paramMap.toString() +", Count :"+ count);
				if (obj.getDimDeclarSeqNum() == 0) {
					sb = new StringBuilder();
					sb.append(" INSERT INTO GBMS.MANIFEST_DIM_DECLARATION");
					sb.append("  ( ");
					sb.append(" 	HS_CODE,");
					sb.append("    	HS_SUB_CODE_FR, ");
					sb.append("    	HS_SUB_CODE_TO, ");
					sb.append("    	QUESTION_DESC, ");
					sb.append("    	ANSWER_TXT, ");
					sb.append("    	LAST_MODIFY_USER_ID, ");
					sb.append("    	LAST_MODIFY_DTTM, ");
					sb.append("    	VV_CD,  OPTION_TXT");
					sb.append("  ) ");
					sb.append("  VALUES ");
					sb.append("  ( ");
					sb.append("    :hsCode, ");
					sb.append("    :hsSubCodeFrom, ");
					sb.append("    :hsSubCodeTo,  ");
					sb.append("    :question, ");
					sb.append("	   :answer ,");
					sb.append("    :userId , ");
					sb.append("    SYSDATE , ");
					sb.append("    :vvCd, :optionsString ");
					sb.append("  )");
					
					paramMap.put("hsCode", obj.getHsCode());
					paramMap.put("hsSubCodeFrom", obj.getHsSubCodeFrom());
					paramMap.put("hsSubCodeTo", obj.getHsSubCodeTo());
					paramMap.put("question", obj.getQuestion());
					paramMap.put("answer", obj.getAnswer());
					paramMap.put("userId", obj.getUserId());
					paramMap.put("vvCd", obj.getVvCd());
					paramMap.put("optionsString", obj.getOptionsString());
					log.info("insert SQL" + sb.toString() + " , paramMap :" + paramMap.toString());
					namedParameterJdbcTemplate.update(sb.toString(), paramMap);

				} else {

					/*if (obj.getDimDeclarSeqNum() == 0) {
						sb = new StringBuilder();
						sb.append("SELECT MFT_DIM_DECLR_SEQ_NBR ");
						sb.append(" FROM  GBMS.MANIFEST_DIM_DECLARATION ");
						sb.append(
								" WHERE VV_CD =:vvCd AND HS_CODE=:hsCode AND HS_SUB_CODE_FR=:hsSubCodeFrom AND HS_SUB_CODE_TO=:hsSubCodeTo");
						log.info("SQL :"+ sb.toString() +", params "+ paramMap.toString());
						long dimDeclarSeqNum = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
								new BeanPropertyRowMapper<Long>(Long.class));
						obj.setDimDeclarSeqNum(dimDeclarSeqNum);
					}*/
					sb = new StringBuilder();
					sb.append("UPDATE GBMS.MANIFEST_DIM_DECLARATION SET ");
					sb.append("    HS_CODE =:hsCode , ");
					sb.append("    HS_SUB_CODE_FR =:hsSubCodeFrom , ");
					sb.append("    HS_SUB_CODE_TO =:hsSubCodeTo , ");
					sb.append("    QUESTION_DESC =:question , ");
					sb.append("   ANSWER_TXT =:answer, ");
					sb.append("    LAST_MODIFY_USER_ID =:userId, ");
					sb.append("    LAST_MODIFY_DTTM =SYSDATE ");
					sb.append("  WHERE  VV_CD=:vvCd AND MFT_DIM_DECLR_SEQ_NBR=:dimDeclarSeqNum ");
					
					paramMap.put("hsCode", obj.getHsCode());
					paramMap.put("hsSubCodeFrom", obj.getHsSubCodeFrom());
					paramMap.put("hsSubCodeTo", obj.getHsSubCodeTo());
					paramMap.put("question", obj.getQuestion());
					paramMap.put("answer", obj.getAnswer());
					paramMap.put("userId", obj.getUserId());
					paramMap.put("vvCd", obj.getVvCd());
					paramMap.put("dimDeclarSeqNum",  String.valueOf(obj.getDimDeclarSeqNum()));
					
					log.info("Update SQL" + sb.toString() + ", paramMap :" + paramMap.toString());
					namedParameterJdbcTemplate.update(sb.toString(), paramMap);

				}
			}
		} catch (Exception e) {
			log.info("Exception saveAttachemnts : ", e);
			result.setSuccess(false);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: saveAttachemnts  DAO  END");
		}
		return result;
	}

	// EndRegion

	// Region Cd

	@Override
	public TableResult getCargoDimensionList(Criteria criteria) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		Map<String, String> paramMap = new HashMap<>();
		String sql = "";
		try {
			log.info("START: getCargoDimensionList DAO criteria:" + criteria.toString());

			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String searchKey = CommonUtility.deNull(criteria.getPredicates().get("searchKey"));
			searchKey = "%" + searchKey + "%";

			sb.append(" SELECT ");
			sb.append("	MD.BL_NBR AS billNo, ");
			sb.append("	MD.VAR_NBR AS vvCd, ");
			sb.append("	MD.CRG_DES AS cargoDes, ");
			sb.append("	HS.HS_DESC AS hsCode, ");
			sb.append("	MD.NBR_PKGS AS nbrPkgs , ");
			sb.append("	MD.GROSS_WT AS grossWt, ");
			sb.append("	MD.GROSS_VOL AS grossVol, ");
			sb.append("	MD.MFT_SEQ_NBR AS mftSeq ");
			sb.append("FROM ");
			sb.append("	GBMS.MANIFEST_DETAILS MD ");
			sb.append("LEFT JOIN GBMS.HS_CODE HS ON ");
			sb.append("	HS.HS_CODE = MD.HS_CODE ");
			sb.append(" WHERE 1=1 ");
			sb.append(" AND MD.BL_STATUS ='A'" );
			sb.append(" AND	MD.VAR_NBR =:vvCd ");

			paramMap.put("vvCd", vvCd);
			if (CommonUtil.deNull(searchKey) != "") {
				sb.append(
						" AND	( LOWER(MD.BL_NBR)  LIKE :searchKey OR LOWER(MD.CRG_DES) LIKE :searchKey OR LOWER(HS.HS_DESC) LIKE :searchKey ) ");
				paramMap.put("searchKey", searchKey.toLowerCase());
			}
			sql = sb.toString();

			log.info("filter.sql=" + sql + "\n params :" + paramMap.toString());
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}
			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
			}
			log.info("sql" + sql + "parmas:" + paramMap.toString());
			List<ManifestDetails> billList = namedParameterJdbcTemplate.query(sql, paramMap,
					new BeanPropertyRowMapper<ManifestDetails>(ManifestDetails.class));
			log.info("billList:" + billList.toString());
			for (ManifestDetails object : billList) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
		} catch (Exception e) {
			log.info("Exception getCargoDimensionList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoDimensionList tableResult:" + tableResult.toString());
		}
		return tableResult;
	}

	@Override
	public List<ManifestPkgDimDetails> getCargoDimensionDetails(Criteria criteria) throws BusinessException {
		List<ManifestPkgDimDetails> list = null;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		try {
			log.info("START: getCargoDimensionDetails DAO criteria:" + criteria.toString());

			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String searchKey = CommonUtility.deNull(criteria.getPredicates().get("searchKey"));
			if (CommonUtil.deNull(searchKey) != "" || !searchKey.isEmpty())
				searchKey = "%" + searchKey + "%";

			sb.append(" SELECT ");
			sb.append("	PKG.MFT_SEQ_NBR AS mftSeqNbr, ");
			sb.append("	PKG.NBR_PKGS AS nbrPkgs, ");
			sb.append("	PKG.PKG_WT AS weight, ");
			sb.append("	PKG.LENGTH AS LENGTH, ");
			sb.append("	PKG.BREADTH AS breadth, ");
			sb.append("	PKG.HEIGHT AS height, ");
			sb.append("	MD.BL_NBR AS billNo ");
			sb.append(" FROM ");
			sb.append("	GBMS.MANIFEST_PKG_DIM_DETAILS PKG ");
			sb.append(" LEFT JOIN GBMS.MANIFEST_DETAILS MD ON ");
			sb.append("	MD.MFT_SEQ_NBR = PKG.MFT_SEQ_NBR ");
			sb.append("LEFT JOIN GBMS.HS_CODE HS ON ");
			sb.append("	HS.HS_CODE = MD.HS_CODE ");

			sb.append(" WHERE ");
			sb.append("	MD.VAR_NBR =:vvCd ");
			sb.append(" AND MD.BL_STATUS ='A'" );

			if (CommonUtil.deNull(searchKey) != "" || !searchKey.isEmpty()) {
				sb.append(
						" AND	( LOWER(MD.BL_NBR)  LIKE :searchKey OR LOWER(MD.CRG_DES) LIKE :searchKey OR LOWER(HS.HS_DESC) LIKE :searchKey ) ");
				paramMap.put("searchKey", searchKey.toLowerCase());
			}
			sb.append(" ORDER BY MD.BL_NBR ");
			paramMap.put("vvCd", vvCd);

			log.info("SQL: getCargoDimensionDetails DAO :" + sb + " param: " + paramMap);
			list = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<ManifestPkgDimDetails>(ManifestPkgDimDetails.class));
			log.info("LIST" + list.toString());
		} catch (Exception e) {
			log.info("Exception getCargoDimensionDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getCargoDimensionDetails DAO");
		}
		return list;
	}

	@Override
	public boolean saveCargoDimensionDetails(CargoDimensionDetails saveCargoDimensionDetails) throws BusinessException {
		StringBuilder sbInsert = new StringBuilder();
		ManifestPkgDimDetails manifest_Pkg_Dim_Details = null;
		ManifestActionTrail manifestActionTrl = null;
		Map<String, String> paramMap = new HashMap<>();
		boolean response= false;
		try {

			log.info("START: saveCargoDimensionDetails DAO saveCargoDimensionDetails:"
					+ saveCargoDimensionDetails.toString());
			manifestActionTrl = new ManifestActionTrail();
			sbInsert.append(" INSERT INTO GBMS.MANIFEST_ACT_TRL (VV_CD,TYPE_CD,REMARKS,LAST_MODIFY_USER_ID, ");
			sbInsert.append(" LAST_MODIFY_DTTM) VALUES ");
			sbInsert.append(" (:vv_cd,:type_cd,:remarks,:last_modify_user_id,SYSDATE) ");
			manifestActionTrl.setRemarks(saveCargoDimensionDetails.getRemarks());
			manifestActionTrl.setVv_cd(saveCargoDimensionDetails.getVvCd());
			manifestActionTrl.setType_cd(ConstantUtil.packaging_type_cd);
			manifestActionTrl.setLast_modify_user_id(saveCargoDimensionDetails.getUserId());
			
			paramMap.put("vv_cd",manifestActionTrl.getVv_cd() );
			paramMap.put("type_cd",manifestActionTrl.getType_cd() );
			paramMap.put("remarks",manifestActionTrl.getRemarks() );
			paramMap.put("last_modify_user_id",manifestActionTrl.getLast_modify_user_id() );
			
			log.info("manifestActionTrl SQL" + sbInsert.toString() + "manifestActionTrl:"
					+ manifestActionTrl.toString());
			namedParameterJdbcTemplate.update(sbInsert.toString(),paramMap);
			
			
			
			sbInsert.setLength(0);
			sbInsert.append(" INSERT INTO GBMS.MANIFEST_PKG_DIM_DETAILS ");
			sbInsert.append(
					" (MFT_SEQ_NBR,NBR_PKGS,PKG_WT,LENGTH,BREADTH,HEIGHT,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
			sbInsert.append(" VALUES(:mftSeqNbr,:nbrPkgs,:weight,:length,:breadth,:height,:modifyUserid,SYSDATE) ");
			
			List<ManifestPkgDimDetails> pkgList = new ArrayList<ManifestPkgDimDetails>();

			if (saveCargoDimensionDetails.getCargoDimensionInfo() != null && saveCargoDimensionDetails.getCargoDimensionInfo().size()>0) {
				List<CargoDimension> cargoDimensionInfo = saveCargoDimensionDetails.getCargoDimensionInfo();
				for (CargoDimension cargoDimension : cargoDimensionInfo) {
					manifest_Pkg_Dim_Details = new ManifestPkgDimDetails();
					manifest_Pkg_Dim_Details.setMftSeqNbr(cargoDimension.getMftSeqNbr());
					manifest_Pkg_Dim_Details.setNbrPkgs(cargoDimension.getNbrPkgs());
					manifest_Pkg_Dim_Details.setWeight(cargoDimension.getWeight());
					manifest_Pkg_Dim_Details.setLength(cargoDimension.getLength());
					manifest_Pkg_Dim_Details.setBreadth(cargoDimension.getBreadth());
					manifest_Pkg_Dim_Details.setHeight(cargoDimension.getHeight());
					manifest_Pkg_Dim_Details.setModifyUserid(saveCargoDimensionDetails.getUserId());
					// manifest_Pkg_Dim_Details.setMftDimSeqNbr(namedParameterJdbcTemplate.queryForObject(sql,
					// Long.class));
					pkgList.add(manifest_Pkg_Dim_Details);
				}
			}
			boolean isVesselExist=false;
			String vessel_query = "SELECT count(*) FROM GBMS.MANIFEST_DETAILS md WHERE VAR_NBR =:vvCd";			
			paramMap = new HashMap<>();
			paramMap.put("vvCd", saveCargoDimensionDetails.getVvCd());
			log.info("insertPackagingData:vessel exist:SQL:" + vessel_query + "paramMap" + paramMap.toString());
			int vsl_count = namedParameterJdbcTemplate.queryForObject(vessel_query, paramMap, Integer.class);
			if (vsl_count > 0) {
				isVesselExist = true;
			}
			log.info("vsl_count: " + vsl_count + " isVesselExist:" + isVesselExist);
			log.info("insertPackagingData:  Vessel existence check process END");

			if (isVesselExist) {
				log.info("insertPackagingData:  delete pkg dim data process START");
				StringBuffer sb_delete = new StringBuffer();
				sb_delete.append("DELETE FROM GBMS.MANIFEST_PKG_DIM_DETAILS mpdd ");
				sb_delete.append("WHERE MFT_SEQ_NBR IN ( SELECT MFT_SEQ_NBR FROM GBMS.MANIFEST_DETAILS md ");
				sb_delete.append("WHERE VAR_NBR = :vvCd)");
				
				Map<String, String> paramMap_del = new HashMap<>();
				paramMap_del.put("vvCd", saveCargoDimensionDetails.getVvCd());
				log.info("insertPackagingData:delete pkg dim data:SQL:" + sb_delete.toString() + "Param:vvCd" + paramMap_del.toString());
				int del_count = namedParameterJdbcTemplate.update(sb_delete.toString(), paramMap_del);
				log.info("del_count: " + del_count);
			}
			
			if (pkgList != null && pkgList.size() > 0) {
				SqlParameterSource[] input = SqlParameterSourceUtils.createBatch(pkgList.toArray());
				int[] result = null;
				result = namedParameterJdbcTemplate.batchUpdate(sbInsert.toString(), input);
				log.info("Final Insert  result" + result.length);
				
			}
			response= true;
		} catch (Exception e) {
			log.info("Exception saveCargoDimensionDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: saveCargoDimensionDetails DAO");
		}
		
		return response;
	}

	// EndRegion

	// region packaging
	@Override
	public List<ManifestUploadConfig> getPackagingTemplate() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<ManifestUploadConfig> manifestUploadConfig = null;
		try {
			log.info("START getPackagingTemplate");
			sb.append(
					"SELECT MFT_UPLOAD_CONFIG_ID,ATTR_NM AS attr_name,ATTR_DESC,INPUT_TYPE,DISPLAY_SEQ,MANDATORY_IND,LOOKUP_TABLE,");
			sb.append("LOOKUP_CAT_CD,COLUMN_NM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM ");
			sb.append("FROM GBMS.MANIFEST_UPLOAD_CONFIG WHERE TYPE_CD = 'P' ORDER BY DISPLAY_SEQ ASC");
			log.info("getPackagingTemplate :" + "SQL:" + sb.toString());
			manifestUploadConfig = namedParameterJdbcTemplate.query(sb.toString(),
					new BeanPropertyRowMapper<ManifestUploadConfig>(ManifestUploadConfig.class));
			log.info("getPackagingTemplate :" + manifestUploadConfig.size());
		} catch (Exception e) {
			log.info("Exception getPackagingTemplate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getPackagingTemplate");
		}
		return manifestUploadConfig;
	}

	@Override
	public List<PackageDimension> getPackageDimensionDetails(String varCode) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<PackageDimension> packageDimensionList = null;
		try {
			log.info("START getPackageDimensionDetails" + "Params:" + varCode);
			sb.append("	SELECT ");
			sb.append("		MD.MFT_SEQ_NBR as mft_seq_nbr,  ");
			sb.append("		MD.VAR_NBR as var_nbr,  ");
			sb.append("		MD.BL_NBR as bl_nbr,  ");
			sb.append("		MD.CRG_DES as cargo_desc,  ");
			sb.append("		MD.NBR_PKGS as total_pkg,  ");
			sb.append("		MD.GROSS_WT as gross_wt,  ");
			sb.append("		mpdd.NBR_PKGS as nbr_of_pkg,  ");
			sb.append("		MPDD.PKG_WT total_pkg_wt_kg, ");
			sb.append("		mpdd.LENGTH  as length_pkg,  ");
			sb.append("		mpdd.BREADTH as breadth,  ");
			sb.append("		mpdd.HEIGHT as height  ");
			sb.append("	FROM  ");
			sb.append("		GBMS.MANIFEST_DETAILS md  ");
			sb.append(" LEFT	JOIN GBMS.MANIFEST_PKG_DIM_DETAILS mpdd ON  ");
			sb.append("		MD.MFT_SEQ_NBR = MPDD.MFT_SEQ_NBR  ");
			sb.append("	WHERE  ");
			sb.append("		VAR_NBR = :varCode and BL_STATUS !='X'  ORDER BY BL_NBR  ");

			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("varCode", varCode);
			log.info("getPackageDimensionDetails : SQL:" + sb.toString() + "Params:" + paramMap.toString());
			packageDimensionList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<PackageDimension>(PackageDimension.class));
			log.info("getPackageDimensionDetails : size:" + packageDimensionList.size());
		} catch (Exception e) {
			log.info("Exception getPackageDimensionDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getPackageDimensionDetails");
		}
		return packageDimensionList;
	}

	@Override
	public List<PackageDimension> insertPackagingData(List<PackageDimension> packageDimensionsRecords, String vvCd,
			String userId) throws BusinessException {
		List<PackageDimension> errorList = new ArrayList<PackageDimension>();
		try {
			log.info("START insertPackagingData" + packageDimensionsRecords.size() + "vvCd:" + vvCd + "userId:"
					+ userId);
			// vessel existence check
			boolean isVesselExist = false;
			log.info("insertPackagingData:  Vessel existence check process START");
			String vessel_query = "SELECT count(*) FROM GBMS.MANIFEST_DETAILS md WHERE VAR_NBR =:vvCd";
			log.info("insertPackagingData:vessel exist:SQL:" + vessel_query + "Param:vvCd" + vvCd);
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("vvCd", vvCd);
			int vsl_count = namedParameterJdbcTemplate.queryForObject(vessel_query, paramMap, Integer.class);
			if (vsl_count > 0) {
				isVesselExist = true;
			}
			log.info("vsl_count: " + vsl_count + " isVesselExist:" + isVesselExist);
			log.info("insertPackagingData:  Vessel existence check process END");

			if (isVesselExist) {
				log.info("insertPackagingData:  delete pkg dim data process START");
				StringBuffer sb_delete = new StringBuffer();
				sb_delete.append("DELETE FROM GBMS.MANIFEST_PKG_DIM_DETAILS mpdd ");
				sb_delete.append("WHERE MFT_SEQ_NBR IN ( SELECT MFT_SEQ_NBR FROM GBMS.MANIFEST_DETAILS md ");
				sb_delete.append("WHERE VAR_NBR = :vvCd)");
				log.info("insertPackagingData:delete pkg dim data:SQL:" + sb_delete.toString() + "Param:vvCd" + vvCd);
				Map<String, String> paramMap_del = new HashMap<>();
				paramMap_del.put("vvCd", vvCd);
				int del_count = namedParameterJdbcTemplate.update(sb_delete.toString(), paramMap_del);

				log.info("del_count: " + del_count);
				log.info("insertPackagingData:  delete pkg dim data process END");

				if (packageDimensionsRecords.size() > 0) {
					for (PackageDimension packageDimension : packageDimensionsRecords) {
						try {
							String mft_seq_nbr_query = "SELECT MFT_SEQ_NBR FROM GBMS.MANIFEST_DETAILS md WHERE VAR_NBR =:vvCd AND BL_NBR=:bl_nbr AND BL_STATUS='A'";
							log.info("insertPackagingData:get_mft_seq_nbr_query:SQL:" + mft_seq_nbr_query + "Param:vvCd"
									+ vvCd + "bl_nbr:" + packageDimension.getBl_nbr());
							Map<String, String> paramMap1 = new HashMap<>();
							paramMap1.put("vvCd", vvCd);
							paramMap1.put("bl_nbr", packageDimension.getBl_nbr());
							String mft_seq_nbr = namedParameterJdbcTemplate.queryForObject(mft_seq_nbr_query, paramMap1,
									String.class);
							log.info("insertPackagingData:get_mft_seq_nbr_query:SQL:" + mft_seq_nbr);

							log.info("insertPackagingData:  INSERT pkg dim data process START");
							// String sql = "SELECT gbms.MFT_DIM_SEQ_NBR_SEQ.NEXTVAL FROM dual";
							// packageDimension.setMft_dim_seq_nbr(namedParameterJdbcTemplate.queryForObject(sql,
							// Long.class));
							packageDimension.setUserId(userId);
							packageDimension.setMft_seq_nbr(mft_seq_nbr);

							StringBuffer sb_insert = new StringBuffer();
							sb_insert.append("INSERT INTO GBMS.MANIFEST_PKG_DIM_DETAILS ( MFT_SEQ_NBR, NBR_PKGS, ");
							sb_insert.append("PKG_WT, LENGTH, BREADTH, HEIGHT, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM)");
							sb_insert.append(
									"VALUES( :mft_seq_nbr, :nbr_of_pkg, :total_pkg_wt_kg, :length_pkg, :breadth, :height, :userId, SYSDATE)");
							log.info("insertPackagingData : SQL:" + sb_insert.toString() + "Params:"
									+ packageDimensionsRecords.toString());
							int rows = namedParameterJdbcTemplate.update(sb_insert.toString(),
									new BeanPropertySqlParameterSource(packageDimension));
							log.info("insertPackagingData : rows inserted" + rows);
							log.info("insertPackagingData:  INSERT pkg dim data process END");

						} catch (Exception e) {
							log.info("Exception getPackageDimensionDetails : ", e);
							packageDimension.setMessage(ConstantUtil.ErrorMsg_Common);
							errorList.add(packageDimension);
						}

					}
				}
			}
		} catch (Exception e) {
			log.info("Exception insertPackagingData : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertPackagingData");
		}
		return packageDimensionsRecords;
	}

	@Override
	public List<CargoManifest> getManifestDetailsForPackage(String vvCd) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<CargoManifest> cargoManifestData = null;
		try {
			log.info("START getManifestDetailsForPackage" + "Params:" + vvCd);
			sb.append("SELECT ");
			sb.append("		md.BL_NBR as  bills_of_landing_no, ");
			sb.append("		md.NBR_PKGS as number_of_packages, ");
			sb.append("		md.GROSS_WT as gross_weight_kg");
			sb.append("	FROM ");
			sb.append("		gbms.MANIFEST_DETAILS md ");
			sb.append("	WHERE ");
			sb.append("		md.VAR_NBR =:vvCd  AND BL_STATUS != 'X'  ORDER BY BL_NBR desc ");
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("vvCd", vvCd);
			log.info("getManifestDetailsForPackage SQL : " + sb.toString() + "parammap" + paramMap.toString());
			cargoManifestData = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<CargoManifest>(CargoManifest.class));
			log.info("getManifestDetailsForPackage : " + sb.toString() + "parammap" + paramMap.toString());
		} catch (Exception e) {
			log.info("Exception getManifestDetailsForPackage : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getManifestDetailsForPackage");
		}
		return cargoManifestData;
	}
	// endregion packaging

	public Boolean isManifestSubmissionAllowed(Criteria criteria) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Boolean result = true;
		try {
			log.info("START isManifestSubmissionAllowed criteria: " + criteria.toString());
			log.info("isManifestSubmissionAllowed DAO criteria:" + criteria.toString());
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			Map<String, String> paramMap = new HashMap<>();
			int count = 0;
			paramMap = new HashMap<>();
			paramMap.put("vvCd", vvCd);
			if (coCd.equalsIgnoreCase("JP")) {
				sb = new StringBuffer();
				sb.append("select count(*) from TOPS.VESSEL_CALL WHERE VV_CD=:vvCd AND GB_CLOSE_BJ_IND='Y'  ");
				log.info("isSubmissionAllowed SQL : " + sb.toString() + "parammap" + paramMap.toString());
				count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
			} else {
				sb = new StringBuffer();
				sb.append("select count(*) from TOPS.VESSEL_CALL WHERE VV_CD=:vvCd AND GB_CLOSE_BJ_IND='Y'  ");
				log.info("isSubmissionAllowed SQL : " + sb.toString() + "parammap" + paramMap.toString());
				count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
				if (count == 0) {
					sb = new StringBuffer();
					sb.append(
							"select count(*) from TOPS.AUDIT_TRAIL_VESSEL_CALL WHERE VV_CD=:vvCd AND GB_CLOSE_BJ_IND='Y' ");
					log.info("isSubmissionAllowed SQL : " + sb.toString() + "parammap" + paramMap.toString());
					count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
				}
			}
			if (count > 0)
				result = false;
		} catch (Exception e) {
			log.info("Exception isManifestSubmissionAllowed : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END isManifestSubmissionAllowed : " + result);
		}
		return result;
	}

	public Result validateTransferofManifest(Criteria criteria) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Result result = new Result();
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START validateTransferofManifest criteria: " + criteria.toString());

			result.setSuccess(true);

			String vvCdFrom = CommonUtility.deNull(criteria.getPredicates().get("vvCdFrom"));
			String vvCdTo = CommonUtility.deNull(criteria.getPredicates().get("vvCdTo"));

			sb = new StringBuffer();
			sb.append(" Select count(*) from GBMS.MANIFEST_HATCH_DETAILS hatch ");
			sb.append(" JOIN GBMS.MANIFEST_DETAILS det ON hatch.MFT_SEQ_NBR=det.MFT_SEQ_NBR ");
			sb.append(" WHERE det.VAR_NBR=:vvCd ");
			paramMap = new HashMap<>();
			paramMap.put("vvCd", vvCdFrom);
			log.info("validateTransferofManifest SQL from : " + sb.toString() + "parammap" + paramMap.toString());
			int fromCount = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);

			sb = new StringBuffer();
			sb.append(" Select count(*) from GBMS.MANIFEST_HATCH_DETAILS hatch ");
			sb.append(" JOIN GBMS.MANIFEST_DETAILS det ON hatch.MFT_SEQ_NBR=det.MFT_SEQ_NBR ");
			sb.append(" WHERE det.VAR_NBR=:vvCd ");
			paramMap = new HashMap<>();
			paramMap.put("vvCd", vvCdTo);
			log.info("validateTransferofManifest SQL to : " + sb.toString() + "parammap" + paramMap.toString());
			int toCount = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);

			log.info("count from :" + fromCount + ", toCount :" + toCount);

			if (fromCount > 0 || toCount > 0) {
				sb = new StringBuffer();
				sb.append(
						" SELECT  NVL((SELECT E.CARGO_HOLD_NUM FROM TOPS.VESSEL_EXT E WHERE E.VSL_NM=C.VSL_NM),0) hatchcount ");
				sb.append("  FROM  TOPS.VESSEL_CALL C  ");
				sb.append("WHERE C.VV_CD =:vvCd ");
				paramMap = new HashMap<>();
				paramMap.put("vvCd", vvCdFrom);
				log.info("validateTransferofManifest SQL to : " + sb.toString() + "parammap" + paramMap.toString());
				int fromHatchCount = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);

				sb = new StringBuffer();
				sb.append(
						" SELECT  NVL((SELECT E.CARGO_HOLD_NUM FROM TOPS.VESSEL_EXT E WHERE E.VSL_NM=C.VSL_NM),0) hatchcount ");
				sb.append("  FROM  TOPS.VESSEL_CALL C  ");
				sb.append(" WHERE C.VV_CD =:vvCd ");
				paramMap = new HashMap<>();
				paramMap.put("vvCd", vvCdTo);
				log.info("validateTransferofManifest SQL to : " + sb.toString() + "parammap" + paramMap.toString());
				int toHatchCount = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);

				log.info("count from :" + fromHatchCount + ", toCount :" + toHatchCount);

				if (fromCount != toHatchCount) {
					result.setSuccess(false);
					result.setError(ConstantUtil.Error_TransferHatch);
				}
			}
		} catch (Exception e) {
			log.info("Exception validateTransferofManifest : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END validateTransferofManifest : " + result);
		}
		return result;
	}

	@Override
	public Result removeHatchBreakDownDetails(Criteria criteria) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Result result = new Result();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START removeHatchBreakDownDetails criteria: " + criteria.toString());
			result.setSuccess(true);
			//Result transferManifestResult = validateTransferofManifest(criteria);
			String vvCdFrom = CommonUtility.deNull(criteria.getPredicates().get("vvCdFrom"));
			String blNo = CommonUtility.deNull(criteria.getPredicates().get("blNo"));
			//if (!transferManifestResult.getSuccess()) {
				sb = new StringBuffer();
				sb.append("Delete from GBMS.MANIFEST_HATCH_DETAILS WHERE MFT_SEQ_NBR IN(  ");
				sb.append(" SELECT hatch.MFT_SEQ_NBR from GBMS.MANIFEST_HATCH_DETAILS hatch ");
				sb.append(" JOIN GBMS.MANIFEST_DETAILS det ON hatch.MFT_SEQ_NBR=det.MFT_SEQ_NBR ");
				sb.append(" WHERE det.VAR_NBR=:vvCd AND  BL_NBR=:blNo ) ");
				paramMap = new HashMap<>();
				paramMap.put("vvCd", vvCdFrom);
				paramMap.put("blNo", blNo);
				log.info("removeHatchBreakDownDetails SQL from : " + sb.toString() + "parammap" + paramMap.toString());
				namedParameterJdbcTemplate.update(sb.toString(), paramMap);

				// Action trail insert
				/*sb = new StringBuffer();
				sb.append(" INSERT INTO GBMS.MANIFEST_ACT_TRL (VV_CD,TYPE_CD,REMARKS,LAST_MODIFY_USER_ID, ");
				sb.append(" LAST_MODIFY_DTTM) VALUES ");
				sb.append(" (:vv_cd,:type_cd,:remarks,:last_modify_user_id,SYSTIMESTAMP) ");
				ManifestActionTrail manifestActionTrl = new ManifestActionTrail();
				manifestActionTrl.setRemarks("Transfer of manifest");
				manifestActionTrl.setVv_cd(vvCdFrom);
				manifestActionTrl.setType_cd(ConstantUtil.manifest_type_cd);
				manifestActionTrl.setLast_modify_user_id(userId);
				log.info("manifestActionTrl SQL" + sb.toString() + "manifestActionTrl:" + manifestActionTrl.toString());
				namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(manifestActionTrl));
				*/
			//}
		} catch (Exception e) {
			log.info("Exception removeHatchBreakDownDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END removeHatchBreakDownDetails : " + result);
		}

		return result;
	}

	@Override
	public String getTemplateVersionNo(String type) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		String versionNo = null;
		log.info("START: getTemplateVersionNo  DAO  :");
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			if(type==ConstantUtil.manifest_type_cd)
			{
			sb.append("SELECT MISC_TYPE_NM FROM TOPS.SYSTEM_CONFIG sc WHERE CAT_CD='MFT_TEMPLATE_VERSION' AND MISC_TYPE_CD='MFT_TEMPLATE_MANIFEST' AND REC_STATUS='A'");
			} // START CR FTZ - NS JULY 2024
			else if(type==ConstantUtil.bk_type_cd)
			{
				sb.append("SELECT MISC_TYPE_NM FROM TOPS.SYSTEM_CONFIG sc WHERE CAT_CD='BK_TEMPLATE_VERSION' AND MISC_TYPE_CD='BK_TEMPLATE' AND REC_STATUS='A'");
				
			} // END CR FTZ - NS JULY 2024
			// Start Split BL - NS Jan 2025
			else if (type==ConstantUtil.manifest_split_bl_type_cd){
				sb.append("SELECT MISC_TYPE_NM FROM TOPS.SYSTEM_CONFIG sc WHERE CAT_CD='SPLIT_BL_TEMPLATE_VERSION' AND MISC_TYPE_CD='SPLIT_BL_TEMPLATE' AND REC_STATUS='A'");				
			}// End Split BL - NS Jan 2025
			else
			{
				sb.append("SELECT MISC_TYPE_NM FROM TOPS.SYSTEM_CONFIG sc WHERE CAT_CD='MFT_TEMPLATE_VERSION' AND MISC_TYPE_CD='MFT_TEMPLATE_PACKAGE' AND REC_STATUS='A'");
				
			}
			versionNo = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,  String.class);
			log.info("getTemplateVersionNo:" + versionNo);
		} catch (Exception e) {
			log.info("Exception getTemplateVersionNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTemplateVersionNo ");
		}
		return versionNo;
	}

	@Override
	public List<ManifestPkgDimDetails> getCargoDimensionAuditDetails(Criteria criteria) throws BusinessException {
		List<ManifestPkgDimDetails> list = null;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		try {
			log.info("START: getCargoDimensionAuditDetails DAO criteria:" + criteria.toString());
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String actTrlId = CommonUtility.deNull(criteria.getPredicates().get("mft_act_trl_id"));
			sb.append(" SELECT ");
			sb.append("	PKG.MFT_SEQ_NBR AS mftSeqNbr, ");
			sb.append("	PKG.NBR_PKGS AS nbrPkgs, ");
			sb.append("	PKG.PKG_WT AS weight, ");
			sb.append("	PKG.LENGTH AS LENGTH, ");
			sb.append("	PKG.BREADTH AS breadth, ");
			sb.append("	PKG.HEIGHT AS height, ");
			sb.append("	MD.BL_NBR AS billNo ");
			sb.append(" FROM ");
			sb.append("	GBMS.AUDIT_MANIFEST_PKG_DIM_DETAILS PKG ");
			sb.append(" JOIN GBMS.MANIFEST_ACT_TRL trl ON PKG.LAST_MODIFY_DTTM=trl.LAST_MODIFY_DTTM ");
			sb.append(" LEFT JOIN GBMS.MANIFEST_DETAILS MD ON ");
			sb.append("	MD.MFT_SEQ_NBR = PKG.MFT_SEQ_NBR ");
			sb.append("LEFT JOIN GBMS.HS_CODE HS ON ");
			sb.append("	HS.HS_CODE = MD.HS_CODE ");

			sb.append(" WHERE 1=1 ");
			sb.append("	AND MD.VAR_NBR =:vvCd ");
			// sb.append(" AND TO_CHAR(PKG.AUDIT_DTTM, 'DD-MM-YYYY HH24:MI')=:transDate ");
			sb.append(" AND trl.MFT_ACT_TRL_ID=:actTrlId ");
			sb.append(" ORDER BY MD.BL_NBR ");
			paramMap.put("vvCd", vvCd);
			paramMap.put("actTrlId", actTrlId);
			log.info("SQL: getCargoDimensionAuditDetails DAO :" + sb + " param: " + paramMap);
			list = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<ManifestPkgDimDetails>(ManifestPkgDimDetails.class));
			log.info("LIST" + list.toString());
		} catch (Exception e) {
			log.info("Exception getCargoDimensionAuditDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getCargoDimensionAuditDetails DAO");
		}
		return list;
	}

	// This is method is for manifest upload from excel
	@Override
	public boolean chkDGInd(String blno, String vvCd) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		boolean dgInd = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: chkDGInd blno:" + blno + "varno:" + vvCd);
			sql.append(" SELECT MPA_APPV_STATUS,JP_APPV_STATUS FROM PM4 WHERE  BL_NBR=upper(:blno) ");
			sql.append(" AND VV_CD=:varno ");
			sql.append(" AND (OPR_TYPE IN('D','T')) AND (RECORD_TYPE <> 'D') ");
			paramMap.put("blno", blno);
			paramMap.put("varno", vvCd);
			log.info(" Excel upload chkDGInd SQL" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				if ((!rs.getString("MPA_APPV_STATUS").equals("A")) || (!rs.getString("JP_APPV_STATUS").equals("A"))) {
					dgInd = false;
					break;
				} else {
					dgInd = true;
				}
			}
		} catch (Exception e) {
			log.info("Exception chkDGInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkDGInd dgInd:" + dgInd);
		}
		return dgInd;

	}

	@Override
	public String getMftSeqNoForDelete(String vvCd, String blNo) throws BusinessException {
		StringBuffer sql = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getMftSeqNoForDelete vvCd, Blno" + vvCd + "," + blNo);
			sql.append("SELECT MFT_SEQ_NBR FROM MANIFEST_DETAILS WHERE ");
			sql.append("bl_nbr =:bl_nbr AND VAR_NBR =:vvCd and bl_status='A' ");
			paramMap.put("bl_nbr", blNo);
			paramMap.put("vvCd", vvCd);
			log.info("getMftSeqNoForDelete SQL" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				return rs.getString("MFT_SEQ_NBR");
			}
		} catch (Exception e) {
			log.info("Exception getMftSeqNoForDelete : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("End getMftSeqNoForDelete");
		}
		return null;
	}
	
	@Override
	public String getVesselCreatedCustomerCode(String vvCd) throws BusinessException
	{
		StringBuffer sb = new StringBuffer();
		String createdCustCode = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		log.info("START: getTemplateVersionNo  DAO  :vvCd : " + vvCd);
		try {
			sb.append("	SELECT ");
			sb.append(" V.CREATE_CUST_CD createdCustCode ");
			sb.append("	FROM ");
			sb.append("		TOPS.VESSEL_CALL V ");
			sb.append("	WHERE ");
			sb.append("		 v.VV_CD =:vvCd ");
			paramMap.put("vvCd", vvCd);
			createdCustCode =(String) namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,  String.class);
			log.info("getVesselCreatedCustomerCode::vvCd :"+ vvCd +", createdCustCode:"+ createdCustCode);
		} catch (Exception e) {
			log.info("Exception getVesselCreatedCustomerCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselCreatedCustomerCode ");
		}
		return createdCustCode;
	}
	
	@Override
	public Boolean vesselDeclarantExists(String vvCd) throws BusinessException
	{
		StringBuffer sb = new StringBuffer();
		Integer count = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		Boolean response=false;
		log.info("START: vesselDeclarantExists  DAO  :vvCd : " + vvCd);
		try {
			sb.append("	SELECT count(*) ");
			sb.append(" ");
			sb.append("	FROM ");
			sb.append("		GBMS.VESSEL_DECLARANT ");
			sb.append("	WHERE ");
			sb.append("	VV_CD =:vvCd AND STATUS='A'");
			paramMap.put("vvCd", vvCd);
			count = (Integer) namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
			
			log.info("vesselDeclarantExists::vvCd :"+ vvCd +", count :"+ count);
			
			if(count>0)
				response=true;
			
		} catch (Exception e) {
			log.info("Exception vesselDeclarantExists : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: vesselDeclarantExists response : " + response);
		}
		return response;
	}

	@Override
	public boolean mftCancel(String userID, String seqno, String varno, String blno) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Boolean response = false;
		String sql = "";
		String sqltlog = "";
		String strInsert_trans = "";
		int stransno = 0;
		int count_trans = 0;
		StringBuffer strLog = new StringBuffer();
		try {
			log.info("START: mftCancel  DAO  :userID : " + userID + "seqno: " + seqno + "varno: " + varno + "blno: " + blno);
			strLog.append("UPDATE MANIFEST_DETAILS SET BL_STATUS='X',LAST_MODIFY_USER_ID=:userID ");
			strLog.append(",LAST_MODIFY_DTTM=sysdate WHERE MFT_SEQ_NBR=:seqno");
			strLog.append(" AND VAR_NBR=:varno AND BL_NBR=:blno ");
			sql = strLog.toString();
			
			paramMap.put("userID", userID);
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			
			boolean vslstat = chkVslStat(varno);
			if (vslstat) {
				log.info("Vessel Status is closed cannot Delete" + varno);
				throw new BusinessException("M21605");
			}
			boolean edostat = chkEdonbrPkgs(seqno, varno, blno);
			if (edostat) {
				log.info("Edo Created cannot Delete" + blno);
				throw new BusinessException("M20202");
			}
			
			boolean dnstat = chkDNnbrPkgs(seqno, varno, blno);
			if (dnstat) {
				log.info("DN Printed cannot Delete" + blno);
				throw new BusinessException("M20208");
			}
			boolean tnstat = chkTnbrPkgs(seqno, varno, blno);
			if (tnstat) {
				log.info("Transhipment done cannot Delete"
						+ blno);
				throw new BusinessException("M20209");
			}
			boolean tdnstat = chkTDNnbrPkgs(seqno, varno, blno);
			if (tdnstat) {
				log.info("Transhipment done cannot Delete" + blno);
				throw new BusinessException("M20209");
			}
			
			int count = namedParameterJdbcTemplate.update(sql, paramMap);
			String companyCode = getCoCode(userID);
			String miscNo = ""; 
			if ("JP".equalsIgnoreCase(companyCode) || isManClose(varno))
				miscNo = insertMiscEvtLog(MDEL, varno, blno, userID);
			
			sqltlog = "SELECT MAX(TRANS_NBR) FROM MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:seqno ";
			paramMap.clear();
			paramMap.put("seqno", seqno);
			
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}

			strLog.setLength(0);
			strLog.append("INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,VAR_NBR,");
			strLog.append("BL_NBR,BL_STATUS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,MISC_SEQ_NBR) ");
			strLog.append("VALUES( :stransno, :seqno, :varno, :blno, 'X', :userID, sysdate, :miscNo)");
			strInsert_trans = strLog.toString();
			
			paramMap.clear();
			paramMap.put("stransno", stransno);
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno.trim());
			paramMap.put("userID", userID);
			paramMap.put("miscNo", miscNo);
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans, paramMap);
			}

			if (count == 0) {
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
				if (count_trans == 0) {
					throw new BusinessException("M4201");
				}
			}
		response = true;
		} catch (BusinessException e) {
			log.info("Exception mftCancel : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception mftCancel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: mftCancel response : " + response);
		}
		return response;
	}
	
	public String getCoCode(String userId) throws BusinessException{
		Map<String, String> paramMap = new HashMap<String, String>();
		String coCode = "";
		String sql = "";
		sql = "SELECT CUST_CD FROM LOGON_ACCT WHERE LOGIN_ID = :userId";
		try {
			log.info("START: getCoCode  DAO  :userId : " + userId);
			paramMap.put("userId", userId);
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				coCode = rs.getString("CUST_CD");
			}
		} catch (Exception e) {
			log.info("Exception getCoCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCoCode " + CommonUtility.deNull(coCode));
		}
		return coCode;
	}

	@Override
	public List<VesselVoyValueObject> getVesselVoyTo(String cocode) throws BusinessException {
		SqlRowSet rs = null;
		List<VesselVoyValueObject> voyList = new ArrayList<VesselVoyValueObject>();
		boolean isShowManifestInfo = false;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getVesselVoyTo DAO START cocode:" + cocode);
			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_MANIFEST);
			TextParaVO result = getParaCodeInfo(code);
			isShowManifestInfo = isShowManifestInfo(cocode, result);
			log.info("isShowManifestInfo :" + isShowManifestInfo);
			if (isShowManifestInfo) {
				sb.append("SELECT IN_VOY_NBR, OUT_VOY_NBR, VSL_NM,VV_CD,TERMINAL FROM VESSEL_CALL WHERE VV_STATUS_IND IN ('PR','AP','AL','BR','UB') ");
				sb.append("AND NVL(GB_CLOSE_SHP_IND,'N') <> 'Y' ");
				sb.append("AND((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',NULL)) OR TERMINAL NOT IN 'CT') ");
				sb.append("ORDER BY TERMINAL DESC,VSL_NM,IN_VOY_NBR ");
			} else {
				sb.append("SELECT DISTINCT IN_VOY_NBR, OUT_VOY_NBR, VSL_NM,  VC.VV_CD, TERMINAL ");
				sb.append("FROM VESSEL_CALL VC  LEFT OUTER JOIN VESSEL_DECLARANT VD ");
				sb.append("ON (VD.VV_CD  = VC.VV_CD  AND VD.STATUS = 'A') ");
				sb.append("WHERE VV_STATUS_IND IN ('PR','AP','AL','BR') ");
				sb.append("AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',NULL)) OR TERMINAL NOT IN 'CT') ");
				sb.append("AND NVL(VC.GB_CLOSE_SHP_IND,'N') <> 'Y' ");
				sb.append("AND (VD.CUST_CD   = :cocode ");
				sb.append("OR VC.CREATE_CUST_CD    = :cocode ) ");
				sb.append("ORDER BY TERMINAL DESC, VSL_NM, IN_VOY_NBR ");
			}

			if (!isShowManifestInfo) {
				paramMap.put("coCode", cocode);
			}

			log.info("***** getVesselVoyTo SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			String voynbr = "";
			String inVoyNo = "";
			String outVoyNo = "";
			String vslName = "";
			String VV_CD = "";
			String terminal = "";
			while (rs.next()) {
				voynbr = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				vslName = CommonUtility.deNull(rs.getString("VSL_NM"));
				VV_CD = CommonUtility.deNull(rs.getString("VV_CD"));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				inVoyNo = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				outVoyNo = CommonUtility.deNull(rs.getString("OUT_VOY_NBR")); //Added

				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj.setVoyNo(voynbr);
				vvvObj.setVslName(vslName);
				vvvObj.setVarNbr(VV_CD);
				vvvObj.setTerminal(terminal);
				vvvObj.setOutVoyNo(outVoyNo);
				vvvObj.setInVoyNo(inVoyNo);
				voyList.add(vvvObj);
			}
			log.info("voyList: getVesselVoyTo" + voyList.size());
		} catch (Exception e) {
			log.info("Exception getVesselVoyTo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getVesselVoyTo Result ***** " + voyList.size());
		}
		return voyList;
	}

	// START CR TO DISABLE VOLUME - NS FEB 2024
	@Override
	public boolean isDisabledVolume(Criteria criteria) throws BusinessException {
		SqlRowSet rs = null;
		String conf = null;
		boolean allIncluded = false, companyCodeIncluded = false, notExpired = false, schemeIncluded = false,
				hsCodeIncluded = false, hsSubCodeIncluded = false, cargoStatusIncluded = false, consigneeCodeIncluded = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: isHsValueIncluded DAO START criteria:" + criteria.toString());
			String hsCd = CommonUtility.deNull(criteria.getPredicates().get("hsCd"));
			String scheme = CommonUtility.deNull(criteria.getPredicates().get("scheme"));
			String status = CommonUtility.deNull(criteria.getPredicates().get("status"));
			String hsSubCd = CommonUtility.deNull(criteria.getPredicates().get("hsSubCd"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String checkType = CommonUtility.deNull(criteria.getPredicates().get("checkType"));
			String consigneeCode = CommonUtility.deNull(criteria.getPredicates().get("consigneeCode"));
			String consigneeOthers = CommonUtility.deNull(criteria.getPredicates().get("consigneeOthers")).trim();

			if (checkType.equalsIgnoreCase("All")) {
				// Conf 1 : Check Company Code
				String sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = 'GC_VOL_CONFIG' AND MISC_TYPE_CD = :typeCd";
				paramMap.put("typeCd", ConstantUtil.VOL_CONFIG + coCd);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					conf = rs.getString("MISC_TYPE_NM");
					if (Jsoup.parse(coCd).text().equalsIgnoreCase(conf)) {
						companyCodeIncluded = true;
					}
				}

				if (companyCodeIncluded) {
					// Conf 2 : Check Expiry Date
					sql = "SELECT REMARKS FROM SYSTEM_CONFIG WHERE CAT_CD = 'GC_VOL_CONFIG' AND MISC_TYPE_CD = :typeCd";
					paramMap.put("typeCd", ConstantUtil.VOL_CONFIG + coCd);
					rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
					if (rs.next()) {
						conf = rs.getString("REMARKS");
						if (!conf.isEmpty()) {
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
							java.util.Date expiryDate = simpleDateFormat.parse(conf);
							java.util.Date currentDate = new java.util.Date();
							if (expiryDate.compareTo(currentDate) > 0) {
								notExpired = true;
							}
						} else {
							notExpired = true;
						}
					}

					if (notExpired) {
						// Conf 3 : Check Scheme
						sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = :catCd AND MISC_TYPE_CD = :typeCd";
						paramMap.put("catCd", ConstantUtil.VOL_CONFIG + coCd);
						paramMap.put("typeCd", ConstantUtil.VOL_CONFIG + scheme);
						rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
						if (rs.next()) {
							conf = rs.getString("MISC_TYPE_NM");
							if (scheme.equalsIgnoreCase(conf)) {
								schemeIncluded = true;
							}
						}

						// Conf 4 : Check HS Code
						sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = :catCd AND MISC_TYPE_CD = :typeCd";
						paramMap.put("catCd", ConstantUtil.VOL_CONFIG + coCd);
						paramMap.put("typeCd", ConstantUtil.CONF_HS_CODE + hsCd);
						rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
						if (rs.next()) {
							conf = rs.getString("MISC_TYPE_NM");
							if (hsCd.equalsIgnoreCase(conf)) {
								hsCodeIncluded = true;
							}
						}

						// Conf 5 : Check HS Sub Code
						sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = :catCd AND MISC_TYPE_CD = :typeCd";
						paramMap.put("catCd", ConstantUtil.CONF_HS_CODE + hsCd);
						paramMap.put("typeCd", ConstantUtil.CONF_HSSUBCODE + hsSubCd);
						rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
						if (rs.next()) {
							conf = rs.getString("MISC_TYPE_NM");
							if (hsSubCd.equalsIgnoreCase(conf)) {
								hsSubCodeIncluded = true;
							}
						}

						// Conf 6 : Check Cargo Status
						sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = :catCd AND MISC_TYPE_CD = 'VOL_CONFIG_CARGO_STATUS'";
						paramMap.put("catCd", ConstantUtil.VOL_CONFIG + coCd);
						rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
						if (rs.next()) {
							conf = rs.getString("MISC_TYPE_NM");
							if (status.equalsIgnoreCase(conf)) {
								cargoStatusIncluded = true;
							}
						}
						
						// Conf 7 : Check Consignee Name
						sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = :catCd AND MISC_TYPE_CD = :typeCd";
						paramMap.put("catCd", ConstantUtil.VOL_CONFIG + coCd);
						paramMap.put("typeCd", ConstantUtil.VOL_CONFIG + consigneeCode);
						rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
						if (rs.next()) {
							conf = rs.getString("MISC_TYPE_NM");
							if (Jsoup.parse(consigneeOthers).text().equalsIgnoreCase(CommonUtil.deNull(conf).trim())) {
								consigneeCodeIncluded = true;
							}
						}
					}
				}

				if (companyCodeIncluded && notExpired && schemeIncluded && hsCodeIncluded && hsSubCodeIncluded
						&& cargoStatusIncluded & consigneeCodeIncluded) {
					allIncluded = true;
				}
			} else if (checkType.equalsIgnoreCase("companyCode")) {
				// Conf 1 : Check Company Code
				String sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = 'GC_VOL_CONFIG' AND MISC_TYPE_CD = :typeCd";
				paramMap.put("typeCd", ConstantUtil.VOL_CONFIG + coCd);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					conf = rs.getString("MISC_TYPE_NM");
					if (Jsoup.parse(coCd).text().equalsIgnoreCase(conf)) {
						companyCodeIncluded = true;
					}
				}
				if (companyCodeIncluded) {
					// Conf 2 : Check Expiry Date
					sql = "SELECT REMARKS FROM SYSTEM_CONFIG WHERE CAT_CD = 'GC_VOL_CONFIG' AND MISC_TYPE_CD = :typeCd";
					paramMap.put("typeCd", ConstantUtil.VOL_CONFIG + coCd);
					rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
					if (rs.next()) {
						conf = rs.getString("REMARKS");
						if (!conf.isEmpty()) {
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
							java.util.Date expiryDate = simpleDateFormat.parse(conf);
							java.util.Date currentDate = new java.util.Date();
							if (expiryDate.compareTo(currentDate) > 0) {
								notExpired = true;
							}
						} else {
							notExpired = true;
						}
					}

					if (notExpired) {
						// Conf 3 : Check Scheme
						sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = :catCd AND MISC_TYPE_CD = :typeCd";
						paramMap.put("catCd", ConstantUtil.VOL_CONFIG + coCd);
						paramMap.put("typeCd", ConstantUtil.VOL_CONFIG + scheme);
						rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
						if (rs.next()) {
							conf = rs.getString("MISC_TYPE_NM");
							if (scheme.equalsIgnoreCase(conf)) {
								allIncluded = true;
							}
						}
					}
				}
			} else if (checkType.equalsIgnoreCase("hsCode")) {
				// Conf 4 : Check HS Code
				String sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = :catCd AND MISC_TYPE_CD = :typeCd";
				paramMap.put("catCd", ConstantUtil.VOL_CONFIG + coCd);
				paramMap.put("typeCd", ConstantUtil.CONF_HS_CODE + hsCd);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					conf = rs.getString("MISC_TYPE_NM");
					if (hsCd.equalsIgnoreCase(conf)) {
						allIncluded = true;
					}
				}
			} else if (checkType.equalsIgnoreCase("hsSubCode")) {
				// Conf 5 : Check HS Sub Code
				String sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = :catCd AND MISC_TYPE_CD = :typeCd";
				paramMap.put("catCd", ConstantUtil.CONF_HS_CODE + hsCd);
				paramMap.put("typeCd", ConstantUtil.CONF_HSSUBCODE + hsSubCd);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					conf = rs.getString("MISC_TYPE_NM");
					if (hsSubCd.equalsIgnoreCase(conf)) {
						allIncluded = true;
					}
				}
			} else if (checkType.equalsIgnoreCase("cargoStatus")) {
				// Conf 6 : Check Cargo Status
				String sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = :catCd AND MISC_TYPE_CD = 'VOL_CONFIG_CARGO_STATUS'";
				paramMap.put("catCd", ConstantUtil.VOL_CONFIG + coCd);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					conf = rs.getString("MISC_TYPE_NM");
					if (status.equalsIgnoreCase(conf)) {
						allIncluded = true;
					}
				}
			} else if (checkType.equalsIgnoreCase("consigneeCode")) {
				// Conf 7 : Check Consignee Name
				String sql = "SELECT MISC_TYPE_NM FROM SYSTEM_CONFIG WHERE CAT_CD = :catCd AND MISC_TYPE_CD = :typeCd";
				paramMap.put("catCd", ConstantUtil.VOL_CONFIG + coCd);
				paramMap.put("typeCd", ConstantUtil.VOL_CONFIG + consigneeCode);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					conf = rs.getString("MISC_TYPE_NM");
					if (Jsoup.parse(consigneeOthers).text().equalsIgnoreCase(CommonUtil.deNull(conf).trim())) {
						allIncluded = true;
					}
				}
			}
		} catch (Exception e) {
			log.info("Exception isDisabledVolume : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isDisabledVolume Result: " + allIncluded);
		}
		return allIncluded;
	}
	// END CR TO DISABLE VOLUME - NS FEB 2024

	// START - #39699 : CR TO VALIDATE CLOSE LCT - NS JUNE 2024
	@Override
	public boolean checkCloseLCT(String vvcd) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		String clLctind = "";
		boolean isCloseLCT = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info(" START: checkCloseLCT vvcd:" + vvcd);
			sql.append("SELECT gb_close_lct_ind FROM vessel_call WHERE vv_cd=:vvcd");
			paramMap.put("vvcd", vvcd);
			log.info("***** checkCloseLCT SQL *****" + sql.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				clLctind = rs.getString("gb_close_lct_ind");
			}
			isCloseLCT = CommonUtil.deNull(clLctind).equalsIgnoreCase("Y") ? true : false;
		} catch (Exception e) {
			log.info("Exception getClBjInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** checkCloseLCT ***** isCloseLCT:" + isCloseLCT);
		}
		return isCloseLCT;
	}
	// END - #39699 : CR TO VALIDATE CLOSE LCT - NS JUNE 2024

	// START CR FTZ HSCODE - NS JULY 2024
	@Override
	public List<MiscDetail> loadHSSubCode(String query, String hsCode) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null; 
		List<MiscDetail> hsCodeLs = new ArrayList<MiscDetail>(); 

		try {
			log.info("START: loadHSSubCode  DAO query:" + query + ",hsCode:" + hsCode);
			 sb.append(" SELECT * FROM ( ");
			 sb.append(" SELECT HS_SUB_CODE_FR || '-' || HS_SUB_CODE_TO typeCode,  ");
			 // Start to get only HS_SUB_CODE_FR if HS_SUB_CODE_FR and HS_SUB_CODE_TO is same - NS June 2024
			 sb.append(" CASE  ");
			 sb.append("    WHEN HS_SUB_CODE_FR = HS_SUB_CODE_TO THEN HS_SUB_CODE_FR  ");
			 sb.append("    ELSE HS_SUB_CODE_FR || '-' || HS_SUB_CODE_TO  ");
			 sb.append("  END AS typeView, ");
			 sb.append(" CASE  ");
			 sb.append("    WHEN HS_SUB_CODE_FR = HS_SUB_CODE_TO THEN HS_SUB_CODE_FR || '(' || HS_SUB_DESC || ')'  ");
			 sb.append("	ELSE HS_SUB_CODE_FR || '-' || HS_SUB_CODE_TO || '(' || HS_SUB_DESC || ')'  ");
			 sb.append(" END AS typeViewDesc, ");
			 // End to get only HS_SUB_CODE_FR if HS_SUB_CODE_FR and HS_SUB_CODE_TO is same - NS June 2024
			 sb.append(" HS_SUB_CODE_FR || '-' || HS_SUB_CODE_TO || '(' || HS_SUB_DESC || ')' typeValue,  ");
			 sb.append(" HS_SUB_DESC category FROM GBMS.HS_SUB_CODE  ");
			 sb.append(" WHERE REC_STATUS = '1' AND HS_CODE = :hs_code  ");
			 sb.append(" ORDER BY HS_SUB_CODE_FR, HS_SUB_CODE_TO ASC )");
			 
			 if (!query.isEmpty()) {
			 sb.append(" WHERE ( ");
			 sb.append(" UPPER(typeCode) LIKE UPPER(:query) ");
			 sb.append(" OR UPPER(typeViewDesc) LIKE UPPER(:query)) ");
			 }
					
			paramMap.put("hs_code", hsCode);
			paramMap.put("query", "%" + query + "%");
			log.info(" ***loadHSSubCode SQL *****" + sb.toString());
			log.info(" ***loadHSSubCode paramMap *****" + paramMap.toString());
			
			
			try 
			{
				
				//Commented for Steel Billets NOM2 CR -- starts
				//return namedParameterJdbcTemplate.query(sb.toString(), paramMap,
						//new BeanPropertyRowMapper<MiscDetail>(MiscDetail.class));
				//Commented for Steel Billets NOM2 CR -- ends
				
				//Added for Steel Billets NOM2 CR -- starts
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				log.info("***** loadHSSubCode SQL *****" + sb.toString());
				log.info("params: " + paramMap.toString());
				while (rs.next()) 
				{
					MiscDetail hs = new MiscDetail();
					hs.setTypeCode(CommonUtility.deNull(rs.getString("TYPECODE")));
					hs.setTypeView(CommonUtility.deNull(rs.getString("TYPEVIEW")));
					hs.setTypeViewDesc(CommonUtility.deNull(rs.getString("TYPEVIEWDESC")));
					hs.setTypeValue(CommonUtility.deNull(rs.getString("TYPEVALUE")));
					hs.setCategory(CommonUtility.deNull(rs.getString("CATEGORY")));
					hsCodeLs.add(hs);
				}				
				String filterValue =  getHSCodesFiler();
				log.info("filterValue in loadHSSubCode method " + filterValue);
				if(filterValue.equalsIgnoreCase("YES"))
				{
					log.info("the filter value is YES, apply filtering of HS code/subcodes");			
					List<String> filteringCodes = getHSCodesFilerValues();
					log.info("filtering Code List :"+ filteringCodes);				
					Set<String> codesToRemove = filteringCodes.stream().flatMap(s -> Arrays.stream(s.split(","))).map(s->s.substring(3).trim()).collect(Collectors.toSet());
					log.info("Filtering Codes To Remove-"+ codesToRemove.toString());
					log.info("hsCodeLs list retrived from DB --"+ hsCodeLs.toString());
					hsCodeLs.removeIf(hs -> codesToRemove.contains(hs.getTypeCode().substring(0, 2) + "-" + hs.getTypeCode().substring(3, 5)));
					log.info("END: *** loadHSSubCode Result After Filtering *****" + hsCodeLs.toString());										
				}
				else if (filterValue.equalsIgnoreCase("NO"))
				{
					log.info("the filter value is NO, do not apply filter and execute existing flow");			
			    }
				else
				{
					log.info("there is no value specific defined for Filter - do Nothing");
				}
		
			return hsCodeLs;
			//Added for Steel Billets NOM2 CR -- ends
			} 
			catch (EmptyResultDataAccessException e)
			{
				return null;
			}
		} 
		catch (Exception e)
		{
			log.info("Exception loadHSSubCode : ", e);
		} 
		finally 
		{
			log.info("END: loadHSSubCode  DAO ");
		}
		return null;
	}

	@Override
	public List<HsCodeDetails> getHsCodeDetailList(String seqno) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getHsCodeDetailList  DAO seqno:" + seqno );
			sb.append(" SELECT MFT_HSCODE_SEQ_NBR HSCODE_SEQ_NBR, MFT_SEQ_NBR, HS_CODE, CUSTOM_HS_CODE, HS_SUB_CODE_FR,");
			sb.append("HS_SUB_CODE_TO, HS_SUB_CODE_DESC, NBR_PKGS, GROSS_WT, GROSS_VOL, CRG_DES, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM");
			sb.append(" FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_SEQ_NBR = :seqno ");		
			paramMap.put("seqno", seqno);
			log.info(" ***getHsCodeDetailList SQL *****" + sb.toString());
			log.info(" ***getHsCodeDetailList paramMap *****" + paramMap.toString());
			try {
				return namedParameterJdbcTemplate.query(sb.toString(), paramMap,
						new BeanPropertyRowMapper<HsCodeDetails>(HsCodeDetails.class));
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
		} catch (NullPointerException e) {
			log.info("Exception loadHSSubCode : ", e);
		} catch (Exception e) {
			log.info("Exception loadHSSubCode : ", e);
		} finally {
			log.info("END: loadHSSubCode  DAO ");
		}
		return null;
	}

	// START FTZ CR - Get Manifest HSCode Detail for download excel - NS JULY 2024 
	@Override
	public List<CargoManifest> getManifestHSDetails(String mft_seq_nbr) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<CargoManifest> cargoManifestHSData = null;
		try {
			log.info("START getManifestHSDetails: mft_seq_nbr: " + mft_seq_nbr);
			sb.append("	SELECT '");
			sb.append(ConstantUtil.action_NA + "' as action,");
			sb.append("	(SELECT M.BL_NBR AS FROM GBMS.MANIFEST_DETAILS M WHERE M.MFT_SEQ_NBR=md.MFT_SEQ_NBR");
			sb.append("	AND BL_STATUS!='X')  bills_of_landing_no, ");
			sb.append("	md.MFT_HSCODE_SEQ_NBR as mft_hscode_seq_nbr,");
			sb.append("	md.MFT_SEQ_NBR as mft_seq_nbr,");
			sb.append("	'Add' AS actionType, ");
			sb.append("	MD.CRG_DES AS cargo_description, ");
			sb.append("	CASE ");
			sb.append("	WHEN md.HS_CODE IS NOT NULL");
			sb.append("	AND md.HS_SUB_CODE_FR IS NOT NULL");
			sb.append("	AND md.HS_SUB_CODE_TO IS NOT NULL THEN (");
			sb.append("	SELECT  hsc.HS_CODE || '(' || hsc.HS_SUB_CODE_FR ");
			sb.append("	|| '-' || hsc.HS_SUB_CODE_TO || ')'|| hsc.HS_SUB_DESC ");
			sb.append("	FROM ");
			sb.append("	gbms.HS_SUB_CODE hsc");
			sb.append("	WHERE ");
			sb.append("	hsc.HS_CODE = md.HS_CODE");
			sb.append("	AND hsc.HS_SUB_CODE_FR = md.HS_SUB_CODE_FR");
			sb.append("	AND hsc.HS_SUB_CODE_TO = md.HS_SUB_CODE_TO) ");
			sb.append("	WHEN md.HS_CODE IS NOT NULL ");
			sb.append("	AND md.HS_SUB_CODE_FR IS NULL ");
			sb.append("	AND md.HS_SUB_CODE_TO IS NULL THEN md.HS_CODE");
			sb.append("	END AS hs_code_sub_code,");
			sb.append("	MD.NBR_PKGS AS number_of_packages, ");
			sb.append("	MD.GROSS_WT AS gross_weight_kg, ");
			sb.append("	MD.GROSS_VOL AS gross_measurement_m3, ");
			sb.append("	MD.CRG_DES AS cargo_description, ");
			sb.append("	MD.CUSTOM_HS_CODE AS custom_hs_code ");
			
			// START SPLIT BL - NS Jan 2025
			sb.append("	 ,(SELECT CASE  ");
			sb.append("			 WHEN m.SPLIT_ID IS NULL OR m.SPLIT_ID = 0 THEN '' ");
			sb.append("			 WHEN m.SPLIT_ID IS NOT NULL AND m.SPLIT_ID > 0 THEN M.SPLIT_MAIN_BL ");
			sb.append("	 END FROM GBMS.MANIFEST_DETAILS M WHERE M.MFT_SEQ_NBR=md.MFT_SEQ_NBR AND BL_STATUS!='X') AS split_main_bl, ");
			sb.append("	 (SELECT CASE  ");
			sb.append("			 WHEN m.SPLIT_ID IS NULL OR m.SPLIT_ID = 0 THEN 'No' ");
			sb.append("			 WHEN m.SPLIT_ID IS NOT NULL AND m.SPLIT_ID > 0 THEN 'Yes' ");
			sb.append("	 END FROM GBMS.MANIFEST_DETAILS M WHERE M.MFT_SEQ_NBR=md.MFT_SEQ_NBR AND BL_STATUS!='X') AS split_bl_ind");
			// END SPLIT BL - NS Jan 2025
			
			sb.append("	FROM gbms.MANIFEST_HSCODE_DETAILS MD WHERE MFT_SEQ_NBR = :mft_seq_nbr");

			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("mft_seq_nbr", mft_seq_nbr);
			log.info("getManifestDetails : SQL:" + sb.toString() + "Params:" + paramMap.toString());
			
			cargoManifestHSData = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<CargoManifest>(CargoManifest.class));
		} catch (Exception e) {
			log.info("Exception getManifestDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getManifestDetails size" + cargoManifestHSData.size());
		}
		return cargoManifestHSData;
	}
	// END FTZ CR - Get Manifest HSCode Detail for download excel - NS JULY 2024 
	// END CR FTZ HSCODE - NS JULY 2024

	@Override
	public String generateSplitBl(String blRoot, String vvcd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		String newBl = "";
		try {
			log.info("START generateSplitBl: blRoot: " + blRoot);
			// check if blroot exist in manifest details as split_id 0, else update first to 0
			sb.setLength(0);
			sb.append("SELECT BL_NBR, SPLIT_ID, SPLIT_MAIN_BL FROM MANIFEST_DETAILS WHERE BL_NBR = :blRoot and BL_STATUS = 'A' AND VAR_NBR = :vvcd");			
			paramMap.put("blRoot", blRoot);
			paramMap.put("vvcd", vvcd);
			log.info(" ***generateSplitBl SQL *****" + sb.toString());
			log.info(" ***generateSplitBl paramMap *****" + paramMap.toString());
			
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if(rs.next()) {
				String splitId = rs.getString("SPLIT_ID");
				String splitmain = rs.getString("SPLIT_MAIN_BL"); 
				
				if(splitId == null) {
					// update
					sb.setLength(0);
					sb.append("UPDATE MANIFEST_DETAILS SET SPLIT_ID = 0, SPLIT_MAIN_BL = :blRoot WHERE BL_NBR = :blRoot and BL_STATUS = 'A' AND VAR_NBR = :vvcd");			
					paramMap.put("blRoot", blRoot);
					paramMap.put("vvcd", vvcd);
					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				}
			}
			
			// get bl_nbr of the latest id
			sb.setLength(0);
			sb.append("SELECT BL_NBR FROM MANIFEST_DETAILS WHERE SPLIT_MAIN_BL = :blRoot AND SPLIT_ID =  ");
			sb.append(" (SELECT MAX(SPLIT_ID) FROM MANIFEST_DETAILS WHERE SPLIT_MAIN_BL = :blRoot )  AND VAR_NBR = :vvcd");
			paramMap.put("blRoot", blRoot);
			paramMap.put("vvcd", vvcd);
			log.info(" ***generateSplitBl SQL *****" + sb.toString());
			log.info(" ***generateSplitBl paramMap *****" + paramMap.toString());
			
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if(rs.next()) {
				String last_bl = rs.getString("BL_NBR");
				log.info(last_bl);
				String removeRoot = (last_bl.replace(blRoot,""));
				char last_alpha = (char) (removeRoot.isEmpty() ? 'A' : (removeRoot.charAt(0) + 1)) ;
				newBl = blRoot+""+(last_alpha);
			} else {
				// check if main split bl same with split bl number for another main bl
				sb.setLength(0);
				sb.append("SELECT SPLIT_MAIN_BL FROM MANIFEST_DETAILS WHERE BL_NBR = :blRoot AND SPLIT_ID > 0  ");
				sb.append("  AND VAR_NBR = :vvcd");
				paramMap.put("blRoot", blRoot);
				paramMap.put("vvcd", vvcd);
				log.info(" ***generateSplitBl SQL *****" + sb.toString());
				log.info(" ***generateSplitBl paramMap *****" + paramMap.toString());
				
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if(rs.next()) {
					newBl = "EXISTED";
				} 
				
			}
		} catch (Exception e) {
			log.info("Exception generateSplitBl : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END generateSplitBl newBl" + newBl);
		}
		return newBl;
	}

	@Override
	public List<ManifestUploadConfig> getSplitBlTemplateHeader() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<ManifestUploadConfig> manifestUploadConfig = null;
		try {
			log.info("START getSplitBlTemplateHeader");
			sb.append(
					"SELECT MFT_UPLOAD_CONFIG_ID,ATTR_NM attr_name,ATTR_DESC,INPUT_TYPE,DISPLAY_SEQ,MANDATORY_IND,LOOKUP_TABLE,");
			sb.append("LOOKUP_CAT_CD,COLUMN_NM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM ");
			sb.append("FROM GBMS.MANIFEST_UPLOAD_CONFIG WHERE TYPE_CD ='S' ORDER BY DISPLAY_SEQ ASC");
			log.info("getTemplateHeader :" + "SQL:" + sb.toString());
			manifestUploadConfig = namedParameterJdbcTemplate.query(sb.toString(),
					new BeanPropertyRowMapper<ManifestUploadConfig>(ManifestUploadConfig.class));
			log.info("getSplitBlTemplateHeader : size:" + manifestUploadConfig.size());
		} catch (Exception e) {
			log.info("Exception getSplitBlTemplateHeader : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getSplitBlTemplateHeader");
		}
		return manifestUploadConfig;
	}

	@Override
	public List<ManifestUploadConfig> getSplitBlHatchTemplate() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<ManifestUploadConfig> manifestUploadConfig = null;
		try {
			log.info("START getHatchTemplate");
			sb.append(
					"SELECT MFT_UPLOAD_CONFIG_ID,ATTR_NM attr_name,ATTR_DESC,INPUT_TYPE,DISPLAY_SEQ,MANDATORY_IND,LOOKUP_TABLE,");
			sb.append("LOOKUP_CAT_CD,COLUMN_NM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM ");
			sb.append("FROM GBMS.MANIFEST_UPLOAD_CONFIG WHERE COLUMN_NM = 'AF' AND TYPE_CD ='S'  ORDER BY DISPLAY_SEQ ASC");
			log.info("getHatchTemplate :" + "SQL:" + sb.toString());
			manifestUploadConfig = namedParameterJdbcTemplate.query(sb.toString(),
					new BeanPropertyRowMapper<ManifestUploadConfig>(ManifestUploadConfig.class));
			log.info("getHatchTemplate :Size:" + manifestUploadConfig.size());
		} catch (Exception e) {
			log.info("Exception getHatchTemplate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getHatchTemplate ");
		}
		return manifestUploadConfig;
	}
	
	@Override
	public boolean isMainandBLExist(String mainBL, String BLNbr) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		boolean exist = false;
		SqlRowSet rs = null;
		try {
			log.info("START isMainandBLExist mainBL:" + mainBL +",BLNbr:" + BLNbr);
			sb.append("SELECT COUNT(*) COUNT FROM GBMS.MANIFEST_DETAILS ");
			sb.append("WHERE SPLIT_MAIN_BL = :mainBL ");
			sb.append("AND BL_NBR =:BLNbr");
			paramMap.put("mainBL", mainBL);
			paramMap.put("BLNbr", BLNbr);
			log.info("getHatchTemplate :" + "SQL:" + sb.toString() + ",paramMap:" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if(rs.next()) {
				if(rs.getInt("COUNT") > 0) {
					exist = true;
				}
			}
		} catch (Exception e) {
			log.info("Exception isMainandBLExist : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END isMainandBLExist Result" + exist);
		}
		return exist;
	}
}
