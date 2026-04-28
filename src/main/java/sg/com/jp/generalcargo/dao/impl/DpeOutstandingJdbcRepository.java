package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.DpeOutstandingRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.OutstandingVO;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;

@Repository("DpeOutstandingJdbcRepository")
public class DpeOutstandingJdbcRepository implements DpeOutstandingRepository {

	private static final Log log = LogFactory.getLog(DpeOutstandingJdbcRepository.class);

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	final static String TEXT_PARA_GC_VIEW_MANIFEST = "GC_V_MFST";

	// jp.src.sg.com.jp.dpe.dao--->DpeOutstandingJdbcDao-->listRecords()
	public List<OutstandingVO> listRecords(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters,
			Criteria criteria, Boolean needAllData) throws BusinessException {
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		StringBuilder sb = new StringBuilder();
		String sql = "";
		String type = (String) filters.get("type");
		Map<String, Object> params = new HashMap<String, Object>();
		List<OutstandingVO> haulierList = new ArrayList<>();
		try {
			log.info("START listRecords DAO:");
			if (StringUtils.equals(type, "outstandingEDO")) {
				sb.append("SELECT * FROM ( ");
				sb.append(
						"  SELECT EDO.EDO_ASN_NBR, VC.VV_CD,VC.TERMINAL,CASE WHEN VC.COMBI_GC_OPS_IND='Y' THEN (VC.SCHEME ||'/' || NVL(VC.COMBI_GC_SCHEME,'')) ELSE VC.SCHEME END AS SCHEME, EDO.EDO_CREATE_CD, ");
				sb.append("    MF.BL_NBR, ");
				sb.append("    NVL(MF.NBR_PKGS,0) BL_PKGS, ");
				sb.append("    NVL(EDO.NBR_PKGS,0) PKGS, ");
				sb.append("    NVL(DN.DELIVERED_PKGS, 0) DELIVERED_PKGS, ");
				sb.append("    NVL(EDO.NBR_PKGS, 0) - NVL(DN.DELIVERED_PKGS,0) BAL_PKGS, ");
				sb.append("    VC.VSL_NM, ");
				sb.append("    VC.IN_VOY_NBR, ");
				sb.append("    B.ATB_DTTM, ");
				sb.append("    B.COD_DTTM, ");
				sb.append("    EDO.ADP_NM ADP, ");
				sb.append("    DECODE(EDO.CRG_STATUS, 'L', 'Local', 'I', 'Import', 'T', 'Transshipment') STATUS, ");
				sb.append("    'Import' MOVEMENT, ");
				sb.append("    'GB_EDO' CRG_TYPE, ");
				sb.append("     MF.MFT_SEQ_NBR, ");
				sb.append("     EDO.VAR_NBR, ");
				sb.append("     EDO.CRG_STATUS ");
				sb.append("  FROM MANIFEST_DETAILS MF, ");
				sb.append("    GB_EDO EDO, ");
				sb.append("    VESSEL_CALL VC, ");
				sb.append("    BERTHING B, ");
				sb.append("    ( ");
				sb.append("      SELECT SUM(NBR_PKGS) DELIVERED_PKGS, EDO_ASN_NBR ");
				sb.append("      FROM DN_DETAILS ");
				sb.append("      WHERE DN_DETAILS.DN_STATUS     = 'A' ");
				sb.append("      GROUP BY EDO_ASN_NBR ");
				sb.append("    ) DN ");
				sb.append("  WHERE MF.BL_STATUS = 'A' ");
				sb.append("  AND MF.MFT_SEQ_NBR = EDO.MFT_SEQ_NBR ");
				sb.append("  AND MF.VAR_NBR     = VC.VV_CD ");
				sb.append("  AND VC.VV_CD       = B.VV_CD ");
				sb.append(
						"  AND((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
				sb.append("  AND B.SHIFT_IND    = 1 ");
				sb.append("  AND EDO.EDO_ASN_NBR            = DN.EDO_ASN_NBR ");
				sb.append("  AND EDO.EDO_STATUS             = 'A' ");
				sb.append("  GROUP BY EDO.EDO_ASN_NBR, ");
				sb.append("    MF.BL_NBR, ");
				sb.append("    MF.NBR_PKGS, ");
				sb.append("    EDO.NBR_PKGS, ");
				sb.append("    EDO.DN_NBR_PKGS, ");
				sb.append("    DN.DELIVERED_PKGS, ");
				sb.append("    VC.VSL_NM, ");
				sb.append("    VC.IN_VOY_NBR, ");
				sb.append("    B.ATB_DTTM, ");
				sb.append("    B.COD_DTTM, ");
				sb.append("    EDO.ADP_NM, ");
				sb.append("    EDO.CRG_STATUS, ");
				sb.append(
						"    VC.VV_CD, EDO.EDO_CREATE_CD, MF.MFT_SEQ_NBR,  EDO.VAR_NBR, EDO.CRG_STATUS,VC.TERMINAL,VC.SCHEME,VC.COMBI_GC_SCHEME,VC.COMBI_GC_OPS_IND ");
				sb.append("  UNION ");
				sb.append(
						"  SELECT TCTS_ASN.EDO_ASN_NBR, VC.VV_CD,VC.TERMINAL,CASE WHEN VC.COMBI_GC_OPS_IND='Y' THEN (VC.SCHEME ||'/' || NVL(VC.COMBI_GC_SCHEME,'')) ELSE VC.SCHEME END AS SCHEME, EDO.EDO_CREATE_CD, ");
				sb.append("    MF.BL_NBR, ");
				sb.append("    NVL(MF.NBR_PKGS,0) BL_PKGS, ");
				sb.append("    NVL(EDO.NBR_PKGS,0) PKGS, ");
				sb.append("    NVL(DN.DELIVERED_PKGS,0) DELIVERED_PKGS, ");
				sb.append("    NVL(EDO.NBR_PKGS, 0) - NVL(DN.DELIVERED_PKGS, 0) BAL_PKGS, ");
				sb.append("    VC.VSL_NM, ");
				sb.append("    VC.IN_VOY_NBR, ");
				sb.append("    B.ATB_DTTM, ");
				sb.append("    B.COD_DTTM, ");
				sb.append("    EDO.ADP_NM ADP, ");
				sb.append("    'Local' STATUS, ");
				sb.append("    'TCTS' MOVEMENT, ");
				sb.append("    'GB_EDO' CRG_TYPE, ");
				sb.append("     MF.MFT_SEQ_NBR, ");
				sb.append("     EDO.VAR_NBR, ");
				sb.append("     EDO.CRG_STATUS ");
				sb.append("  FROM MANIFEST_DETAILS MF, ");
				sb.append("    GB_EDO EDO, ");
				sb.append("    VESSEL_CALL VC, ");
				sb.append("    BERTHING B, ");
				sb.append("    ( ");
				sb.append("      SELECT SUM(NBR_PKGS) DELIVERED_PKGS, EDO_ASN_NBR ");
				sb.append("      FROM DN_DETAILS ");
				sb.append("      WHERE DN_DETAILS.DN_STATUS     = 'A' ");
				sb.append("      GROUP BY EDO_ASN_NBR ");
				sb.append("    ) DN, ");
				sb.append("    TCTS_ASN ");
				sb.append("  WHERE MF.BL_STATUS       = 'A' ");
				sb.append("  AND TCTS_ASN.EDO_ASN_NBR = EDO.EDO_ASN_NBR ");
				sb.append("  AND TCTS_ASN.STATUS_IND  = 'A' ");
				sb.append("  AND MF.MFT_SEQ_NBR       = EDO.MFT_SEQ_NBR ");
				sb.append("  AND MF.VAR_NBR           = VC.VV_CD ");
				sb.append("  AND VC.VV_CD             = B.VV_CD ");
				sb.append(
						"  AND((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
				sb.append("  AND B.SHIFT_IND          = 1 ");
				sb.append("  AND EDO.EDO_ASN_NBR      = DN.EDO_ASN_NBR ");
				sb.append("  AND EDO.EDO_STATUS       = 'A' ");
				sb.append("  GROUP BY TCTS_ASN.EDO_ASN_NBR, ");
				sb.append("    MF.BL_NBR, ");
				sb.append("    MF.NBR_PKGS, ");
				sb.append("    EDO.NBR_PKGS, ");
				sb.append("    EDO.DN_NBR_PKGS, ");
				sb.append("    DN.DELIVERED_PKGS, ");
				sb.append("    VC.VSL_NM, ");
				sb.append("    VC.IN_VOY_NBR, ");
				sb.append("    B.ATB_DTTM, ");
				sb.append("    B.COD_DTTM, ");
				sb.append("    EDO.ADP_NM, ");
				sb.append(
						"    VC.VV_CD, EDO.EDO_CREATE_CD, MF.MFT_SEQ_NBR,  EDO.VAR_NBR, EDO.CRG_STATUS,VC.TERMINAL,VC.SCHEME,VC.COMBI_GC_SCHEME,VC.COMBI_GC_OPS_IND ");
				sb.append(" ");
				sb.append(")");
			} else {
				sb.append("SELECT * FROM ( ");
				sb.append("	SELECT ESN.ESN_ASN_NBR, ");
				sb.append("	  BK.BK_REF_NBR, ");
				sb.append("      NVL(BK.BK_NBR_PKGS, 0) BK_REF_PKGS, ");
				sb.append("	  NVL(ESND.NBR_PKGS, 0) PKGS, ");
				sb.append("	  NVL(UA.STORED_PKGS, 0) STORED_PKGS, ");
				sb.append("      (NVL(ESND.NBR_PKGS, 0) -  NVL(UA.STORED_PKGS, 0)) BAL_PKGS, ");
				sb.append("	  VC.VSL_NM, ");
				sb.append("	  VC.OUT_VOY_NBR, ");
				sb.append("	  B.ETB_DTTM, ");
				sb.append("	  B.ATB_DTTM, ");
				sb.append("	  ESND.FIRST_TRANS_DTTM FIRST_UA, ");
				sb.append("	  ESND.TRUCKER_NM TRUCKER, ");
				sb.append(
						"	  VC.VV_CD,VC.TERMINAL,CASE WHEN VC.COMBI_GC_OPS_IND='Y' THEN (VC.SCHEME ||'/' || NVL(VC.COMBI_GC_SCHEME,'')) ELSE VC.SCHEME END AS SCHEME, ");
				sb.append("	  DECODE(ESN.TRANS_TYPE, 'A', 'JP - JP', 'C', 'PSA - JP', 'E', 'Local') STATUS, ");
				sb.append("	  'Export' MOVEMENT, ");
				sb.append("	   VC.IN_VOY_NBR, ");
				sb.append("	   ESN.ESN_CREATE_CD, ");
				sb.append("	   'ESN' CRG_TYPE, ");
				sb.append(
						"	   TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24:MI') || ' ' || TO_CHAR(B.ATB_DTTM,'dd/mm/yyyy HH24:MI') ETB_ATB ");
				sb.append(" ");
				sb.append("	FROM BK_DETAILS BK, ");
				sb.append("	    ESN, ");
				sb.append("	    ESN_DETAILS ESND, ");
				sb.append("	    VESSEL_CALL VC, ");
				sb.append("	    BERTHING B, ");
				sb.append("	    ( ");
				sb.append("        SELECT SUM(NBR_PKGS) STORED_PKGS, ESN_ASN_NBR ");
				sb.append("        FROM  UA_DETAILS ");
				sb.append("        WHERE  UA_STATUS    = 'A' ");
				sb.append("        GROUP BY ESN_ASN_NBR ");
				sb.append("      ) UA ");
				sb.append("	WHERE B.VV_CD       = VC.VV_CD ");
				sb.append("	AND VC.VV_CD        = ESN.OUT_VOY_VAR_NBR ");
				sb.append("	AND ESN.BK_REF_NBR  = BK.BK_REF_NBR ");
				sb.append("	AND ESN.ESN_ASN_NBR = ESND.ESN_ASN_NBR ");
				sb.append("	AND ESN.ESN_STATUS  = 'A' ");
				sb.append(
						"	AND((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
				sb.append("	AND ESN.ESN_ASN_NBR = UA.ESN_ASN_NBR ");
				sb.append("	GROUP BY VC.VSL_NM, ");
				sb.append("	  VC.OUT_VOY_NBR, ");
				sb.append("	  VC.VV_CD, ");
				sb.append("	  BK.BK_REF_NBR, ");
				sb.append("      BK.BK_NBR_PKGS, ");
				sb.append("	  ESN.ESN_ASN_NBR, ");
				sb.append("	  ESND.UA_NBR_PKGS, ");
				sb.append("	  ESND.NBR_PKGS, ");
				sb.append("      STORED_PKGS, ");
				sb.append("	  ESND.FIRST_TRANS_DTTM, ");
				sb.append("	  ESND.TRUCKER_NM, ");
				sb.append("	  B.ETB_DTTM, ");
				sb.append("	  B.ATB_DTTM, ");
				sb.append("	  VC.IN_VOY_NBR, ");
				sb.append(
						"	  ESN.ESN_CREATE_CD, ESN.TRANS_TYPE,VC.TERMINAL,VC.SCHEME,VC.COMBI_GC_SCHEME,VC.COMBI_GC_OPS_IND ");
				sb.append(" ");
				sb.append("	UNION ");
				sb.append("	SELECT TCTS_ESN.ESN_ASN_NBR, ");
				sb.append("	  BK.BK_REF_NBR, ");
				sb.append("      NVL(BK.BK_NBR_PKGS, 0) BK_REF_PKGS, ");
				sb.append("	  NVL(ESND.NBR_PKGS, 0) PKGS, ");
				sb.append("	  NVL(UA.STORED_PKGS, 0) STORED_PKGS, ");
				sb.append("      (NVL(ESND.NBR_PKGS, 0) -  NVL(UA.STORED_PKGS, 0)) BAL_PKGS, ");
				sb.append("	  VC.VSL_NM, ");
				sb.append("	  VC.OUT_VOY_NBR, ");
				sb.append("	  B.ETB_DTTM, ");
				sb.append("	  B.ATB_DTTM, ");
				sb.append("	  ESND.FIRST_TRANS_DTTM FIRST_UA, ");
				sb.append("	  ESND.TRUCKER_NM TRUCKER, ");
				sb.append(
						"	  VC.VV_CD,VC.TERMINAL,CASE WHEN VC.COMBI_GC_OPS_IND='Y' THEN (VC.SCHEME ||'/' || NVL(VC.COMBI_GC_SCHEME,'')) ELSE VC.SCHEME END AS SCHEME, ");
				sb.append("	  'Local' STATUS, ");
				sb.append("	  'TCTS' MOVEMENT, ");
				sb.append("	  VC.IN_VOY_NBR, ");
				sb.append("	  ESN.ESN_CREATE_CD, ");
				sb.append("	  'ESN' CRG_TYPE, ");
				sb.append(
						"	   TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24:MI') || ' ' || TO_CHAR(B.ATB_DTTM,'dd/mm/yyyy HH24:MI') ETB_ATB ");
				sb.append("	FROM BK_DETAILS BK, ");
				sb.append("	    ESN, ");
				sb.append("	    ESN_DETAILS ESND, ");
				sb.append("	    VESSEL_CALL VC, ");
				sb.append("	    BERTHING B, ");
				sb.append("	   ( ");
				sb.append("        SELECT SUM(NBR_PKGS) STORED_PKGS, ESN_ASN_NBR ");
				sb.append("        FROM  UA_DETAILS ");
				sb.append("        WHERE  UA_STATUS    = 'A' ");
				sb.append("        GROUP BY ESN_ASN_NBR ");
				sb.append("      ) UA, ");
				sb.append("	    TCTS_ESN_MAPPING TCTS_ESN ");
				sb.append("	WHERE B.VV_CD       = VC.VV_CD ");
				sb.append("	AND TCTS_ESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("	AND TCTS_ESN.REC_IND = 'A' ");
				sb.append("	AND VC.VV_CD        = ESN.OUT_VOY_VAR_NBR ");
				sb.append("	AND ESN.BK_REF_NBR  = BK.BK_REF_NBR ");
				sb.append("	AND ESN.ESN_ASN_NBR = ESND.ESN_ASN_NBR ");
				sb.append("	AND ESN.ESN_STATUS  = 'A' ");
				sb.append(
						"	AND((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
				sb.append("	AND ESN.ESN_ASN_NBR = UA.ESN_ASN_NBR ");
				sb.append("	GROUP BY ");
				sb.append("	 TCTS_ESN.ESN_ASN_NBR, ");
				sb.append("	 VC.VSL_NM, ");
				sb.append("	 VC.OUT_VOY_NBR, ");
				sb.append("	 VC.VV_CD, ");
				sb.append("	 BK.BK_REF_NBR, ");
				sb.append("     BK.BK_NBR_PKGS, ");
				sb.append("	 ESND.UA_NBR_PKGS, ");
				sb.append("	 ESND.NBR_PKGS, ");
				sb.append("     STORED_PKGS, ");
				sb.append("	 ESND.FIRST_TRANS_DTTM, ");
				sb.append("	 ESND.TRUCKER_NM, ");
				sb.append("	 B.ETB_DTTM, ");
				sb.append("	 B.ATB_DTTM, ");
				sb.append("	 VC.IN_VOY_NBR, ");
				sb.append(
						"	 ESN.ESN_CREATE_CD, ESN.TRANS_TYPE,VC.TERMINAL,VC.SCHEME,VC.COMBI_GC_SCHEME,VC.COMBI_GC_OPS_IND ");
				sb.append(" ");
				sb.append("  UNION ");
				sb.append("   SELECT TESN.ESN_ASN_NBR, ");
				sb.append("	  BK.BK_REF_NBR, ");
				sb.append("      NVL(BK.BK_NBR_PKGS, 0) BK_REF_PKGS, ");
				sb.append("	  NVL(TESN.NBR_PKGS, 0) PKGS, ");
				sb.append("	  NVL(TESN.UA_NBR_PKGS, 0) STORED_PKGS, ");
				sb.append("      (NVL(TESN.NBR_PKGS, 0) -  NVL(TESN.UA_NBR_PKGS, 0)) BAL_PKGS, ");
				sb.append("	  VC.VSL_NM, ");
				sb.append("	  VC.OUT_VOY_NBR, ");
				sb.append("	  B.ETB_DTTM, ");
				sb.append("	  B.ATB_DTTM, ");
				sb.append("	  TESN.FIRST_TRANS_DTTM FIRST_UA, ");
				sb.append("	  '' TRUCKER, ");
				sb.append(
						"	  VC.VV_CD,VC.TERMINAL,CASE WHEN VC.COMBI_GC_OPS_IND='Y' THEN (VC.SCHEME ||'/' || NVL(VC.COMBI_GC_SCHEME,'')) ELSE VC.SCHEME END AS SCHEME, ");
				sb.append("	  DECODE(ESN.TRANS_TYPE, 'A', 'JP - JP', 'C', 'PSA - JP', 'E', 'Local') STATUS, ");
				sb.append("	  'Export' MOVEMENT, ");
				sb.append("	  VC.IN_VOY_NBR, ");
				sb.append("	  ESN.ESN_CREATE_CD, ");
				sb.append("	  'TESN_JP_JP' CRG_TYPE, ");
				sb.append(
						"	  TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24:MI') || ' ' || TO_CHAR(B.ATB_DTTM,'dd/mm/yyyy HH24:MI') ETB_ATB ");
				sb.append(" ");
				sb.append("	FROM BK_DETAILS BK, ");
				sb.append("	    ESN, ");
				sb.append("        TESN_JP_JP TESN, ");
				sb.append("	    VESSEL_CALL VC, ");
				sb.append("	    BERTHING B ");
				sb.append("	WHERE B.VV_CD       = VC.VV_CD ");
				sb.append("	AND VC.VV_CD        = ESN.OUT_VOY_VAR_NBR ");
				sb.append("	AND ESN.BK_REF_NBR  = BK.BK_REF_NBR ");
				sb.append("	AND ESN.ESN_STATUS  = 'A' ");
				sb.append(
						"	AND((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
				sb.append("    AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("	GROUP BY VC.VSL_NM, ");
				sb.append("	  VC.OUT_VOY_NBR, ");
				sb.append("	  VC.VV_CD, ");
				sb.append("	  BK.BK_REF_NBR, ");
				sb.append("      BK.BK_NBR_PKGS, ");
				sb.append("	  TESN.ESN_ASN_NBR, ");
				sb.append("      TESN.NBR_PKGS, ");
				sb.append("      TESN.UA_NBR_PKGS, ");
				sb.append("	  TESN.FIRST_TRANS_DTTM, ");
				sb.append("	   ");
				sb.append("	  B.ETB_DTTM, ");
				sb.append("	  B.ATB_DTTM, ");
				sb.append("	  VC.IN_VOY_NBR, ");
				sb.append(
						"	  ESN.ESN_CREATE_CD, ESN.TRANS_TYPE,VC.TERMINAL,VC.SCHEME,VC.COMBI_GC_SCHEME,VC.COMBI_GC_OPS_IND ");
				sb.append("	UNION ");
				sb.append("   SELECT TESN.ESN_ASN_NBR, ");
				sb.append("	  BK.BK_REF_NBR, ");
				sb.append("      NVL(BK.BK_NBR_PKGS, 0) BK_REF_PKGS, ");
				sb.append("	  NVL(TESN.NBR_PKGS, 0) PKGS, ");
				sb.append("	  NVL(TESN.UA_NBR_PKGS, 0) STORED_PKGS, ");
				sb.append("      (NVL(TESN.NBR_PKGS, 0) -  NVL(TESN.UA_NBR_PKGS, 0)) BAL_PKGS, ");
				sb.append("	  VC.VSL_NM, ");
				sb.append("	  VC.OUT_VOY_NBR, ");
				sb.append("	  B.ETB_DTTM, ");
				sb.append("	  B.ATB_DTTM, ");
				sb.append("	  TESN.FIRST_TRANS_DTTM FIRST_UA, ");
				sb.append("	  TESN.TRUCKER_NM TRUCKER, ");
				sb.append(
						"	  VC.VV_CD,VC.TERMINAL,CASE WHEN VC.COMBI_GC_OPS_IND='Y' THEN (VC.SCHEME ||'/' || NVL(VC.COMBI_GC_SCHEME,'')) ELSE VC.SCHEME END AS SCHEME, ");
				sb.append("	  DECODE(ESN.TRANS_TYPE, 'A', 'JP - JP', 'C', 'PSA - JP', 'E', 'Local') STATUS, ");
				sb.append("	  'Export' MOVEMENT, ");
				sb.append("	   VC.IN_VOY_NBR, ");
				sb.append("	   ESN.ESN_CREATE_CD, ");
				sb.append("	  'TESN_PSA_JP' CRG_TYPE, ");
				sb.append(
						"	   TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24:MI') || ' ' || TO_CHAR(B.ATB_DTTM,'dd/mm/yyyy HH24:MI') ETB_ATB ");
				sb.append(" ");
				sb.append("	FROM BK_DETAILS BK, ");
				sb.append("	    ESN, ");
				sb.append("        TESN_PSA_JP TESN, ");
				sb.append("	     ");
				sb.append("	    VESSEL_CALL VC, ");
				sb.append("	    BERTHING B ");
				sb.append("	WHERE B.VV_CD       = VC.VV_CD ");
				sb.append("	AND VC.VV_CD        = ESN.OUT_VOY_VAR_NBR ");
				sb.append("	AND ESN.BK_REF_NBR  = BK.BK_REF_NBR ");
				sb.append("	 ");
				sb.append("	AND ESN.ESN_STATUS  = 'A' ");
				sb.append(
						"	AND((UPPER(VC.TERMINAL) IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR UPPER(VC.TERMINAL) NOT IN 'CT') ");
				sb.append("    AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("	GROUP BY VC.VSL_NM, ");
				sb.append("	  VC.OUT_VOY_NBR, ");
				sb.append("	  VC.VV_CD, ");
				sb.append("	  BK.BK_REF_NBR, ");
				sb.append("      BK.BK_NBR_PKGS, ");
				sb.append("	  TESN.ESN_ASN_NBR, ");
				sb.append("      TESN.NBR_PKGS, ");
				sb.append("	  TESN.UA_NBR_PKGS, ");
				sb.append("	  TESN.FIRST_TRANS_DTTM, ");
				sb.append("	  TESN.TRUCKER_NM, ");
				sb.append("	  B.ETB_DTTM, ");
				sb.append("	  B.ATB_DTTM, ");
				sb.append("	  VC.IN_VOY_NBR, ");
				sb.append(
						"	  ESN.ESN_CREATE_CD, ESN.TRANS_TYPE,VC.TERMINAL,VC.SCHEME,VC.COMBI_GC_SCHEME,VC.COMBI_GC_OPS_IND ");
				sb.append(" ");
				sb.append("  )");
			}
			
			buildQueryParameter(filters, params, sb);
			if (!"".equalsIgnoreCase(sort.trim())) {
				sb.append(" ORDER BY :sort ");
				params.put("sort",sort.toUpperCase().trim());
			}
			if (!"".equalsIgnoreCase(dir.trim())) {
				sb.append(" :dir ");
				params.put("dir",dir.toUpperCase().trim());
			}
 
			if(!needAllData) {
				if (criteria.isPaginated()) {
					sql = CommonUtil.getPaginatedSql(sb.toString(), criteria.getStart(), criteria.getLimit());
	
				} 
			} else {
				sql = sb.toString();
			}
			
			log.info("listRecords SQL " + sql.toString() + ", paramMap = " + params.toString());
			haulierList = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<OutstandingVO>(OutstandingVO.class));
			for (OutstandingVO object : haulierList) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			
			log.info("END: *** listRecords Result *****" + haulierList.size());
		} catch (Exception e) {
			log.info("Exception listRecords : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END listRecords DAO");
		}
		return haulierList;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeOutstandingJdbcDao-->buildQueryParameter()
	private String buildQueryParameter(Map<String, Object> filters, Map<String, Object>  params, StringBuilder queryString) throws BusinessException {
		String type = (String) filters.get("type");
		try {
			log.info("START buildQueryParameter DAO");
			queryString.append(" WHERE (1=1)");
			if (filters.get("vv_cd") != null && StringUtils.isNotBlank(filters.get("vv_cd").toString())) {
				queryString.append(" AND VSL_NM =:vv_cd ");
				params.put("vv_cd", filters.get("vv_cd").toString());
			}
			if (filters.get("in_voy_nbr") != null && StringUtils.isNotBlank(filters.get("in_voy_nbr").toString())) {
				queryString.append(" AND VV_CD =:in_voy_nbr ");
				params.put("in_voy_nbr", filters.get("in_voy_nbr").toString());
			}
			if (filters.get("company_code") != null && StringUtils.isNotBlank(filters.get("company_code").toString())) {
				if (StringUtils.equals(type, "outstandingEDO")) {
					queryString.append(" AND EDO_CREATE_CD =:company_code");
				} else {
					queryString.append(" AND ESN_CREATE_CD =:company_code");
				}
				params.put("company_code", filters.get("company_code").toString());
			}
		} catch (Exception e) {
			log.info("Exception buildQueryParameter : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END buildQueryParameter DAO");
		}
		return queryString.toString();
	}
	
	// jp.src.sg.com.jp.dpe.dao--->DpeOutstandingJdbcDao-->countRecords()
		@Override
		public int countRecords(Map<String, Object> filters) throws BusinessException {
			StringBuilder sb = new StringBuilder();
			Map<String, Object> params = new HashMap<String, Object>();
			String type = (String) filters.get("type");
			int countHaulier = 0;
			try {
				log.info("START countRecords DAO filters = " + filters.toString());
				if (StringUtils.equals(type, "outstandingEDO")) {
					sb.append("SELECT COUNT(*) FROM ( ");
					sb.append("  SELECT EDO.EDO_ASN_NBR, VC.VV_CD, EDO.EDO_CREATE_CD, ");
					sb.append("    MF.BL_NBR, ");
					sb.append("    NVL(MF.NBR_PKGS, 0) BL_PKGS, ");
					sb.append("    NVL(EDO.NBR_PKGS, 0) PKGS, ");
					sb.append("    NVL(DN.DELIVERED_PKGS, 0) DELIVERED_PKGS, ");
					sb.append("    NVL(EDO.NBR_PKGS, 0) - NVL(DN.DELIVERED_PKGS, 0) BAL_PKGS, ");
					sb.append("    VC.VSL_NM, ");
					sb.append("    VC.IN_VOY_NBR, ");
					sb.append("    B.ATB_DTTM, ");
					sb.append("    B.COD_DTTM, ");
					sb.append("    EDO.ADP_NM ADP, ");
					sb.append("    DECODE(EDO.CRG_STATUS, 'L', 'Local', 'I', 'Import', 'T', 'Transshipment') STATUS, ");
					sb.append("    'Import' MOVEMENT, ");
					sb.append("    'GB_EDO' CRG_TYPE, ");
					sb.append("     MF.MFT_SEQ_NBR, ");
					sb.append("     EDO.VAR_NBR, ");
					sb.append("     EDO.CRG_STATUS ");
					sb.append("  FROM MANIFEST_DETAILS MF, ");
					sb.append("    GB_EDO EDO, ");
					sb.append("    VESSEL_CALL VC, ");
					sb.append("    BERTHING B, ");
					sb.append("    ( ");
					sb.append("      SELECT SUM(NBR_PKGS) DELIVERED_PKGS, EDO_ASN_NBR ");
					sb.append("      FROM DN_DETAILS ");
					sb.append("      WHERE DN_DETAILS.DN_STATUS     = 'A' ");
					sb.append("      GROUP BY EDO_ASN_NBR ");
					sb.append("    ) DN ");
					sb.append("  WHERE MF.BL_STATUS = 'A' ");
					sb.append("  AND MF.MFT_SEQ_NBR = EDO.MFT_SEQ_NBR ");
					sb.append("  AND MF.VAR_NBR     = VC.VV_CD ");
					sb.append("  AND VC.VV_CD       = B.VV_CD ");
					sb.append(
							"  AND((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
					sb.append("  AND B.SHIFT_IND    = 1 ");
					sb.append("  AND EDO.EDO_ASN_NBR            = DN.EDO_ASN_NBR ");
					sb.append("  AND EDO.EDO_STATUS             = 'A' ");
					sb.append("  GROUP BY EDO.EDO_ASN_NBR, ");
					sb.append("    MF.BL_NBR, ");
					sb.append("    MF.NBR_PKGS, ");
					sb.append("    EDO.NBR_PKGS, ");
					sb.append("    EDO.DN_NBR_PKGS, ");
					sb.append("    DN.DELIVERED_PKGS, ");
					sb.append("    VC.VSL_NM, ");
					sb.append("    VC.IN_VOY_NBR, ");
					sb.append("    B.ATB_DTTM, ");
					sb.append("    B.COD_DTTM, ");
					sb.append("    EDO.ADP_NM, ");
					sb.append("    EDO.CRG_STATUS, ");
					sb.append("    VC.VV_CD, EDO.EDO_CREATE_CD, MF.MFT_SEQ_NBR,  EDO.VAR_NBR, EDO.CRG_STATUS ");
					sb.append("  UNION ");
					sb.append("  SELECT TCTS_ASN.EDO_ASN_NBR, VC.VV_CD, EDO.EDO_CREATE_CD, ");
					sb.append("    MF.BL_NBR, ");
					sb.append("    NVL(MF.NBR_PKGS, 0) BL_PKGS, ");
					sb.append("    NVL(EDO.NBR_PKGS, 0) PKGS, ");
					sb.append("    NVL(DN.DELIVERED_PKGS, 0) DELIVERED_PKGS, ");
					sb.append("    NVL(EDO.NBR_PKGS, 0) - NVL(DN.DELIVERED_PKGS, 0) BAL_PKGS, ");
					sb.append("    VC.VSL_NM, ");
					sb.append("    VC.IN_VOY_NBR, ");
					sb.append("    B.ATB_DTTM, ");
					sb.append("    B.COD_DTTM, ");
					sb.append("    EDO.ADP_NM ADP, ");
					sb.append("    'Local' STATUS, ");
					sb.append("    'TCTS' MOVEMENT, ");
					sb.append("    'GB_EDO' CRG_TYPE, ");
					sb.append("     MF.MFT_SEQ_NBR, ");
					sb.append("     EDO.VAR_NBR, ");
					sb.append("     EDO.CRG_STATUS ");
					sb.append("  FROM MANIFEST_DETAILS MF, ");
					sb.append("    GB_EDO EDO, ");
					sb.append("    VESSEL_CALL VC, ");
					sb.append("    BERTHING B, ");
					sb.append("    ( ");
					sb.append("      SELECT SUM(NBR_PKGS) DELIVERED_PKGS, EDO_ASN_NBR ");
					sb.append("      FROM DN_DETAILS ");
					sb.append("      WHERE DN_DETAILS.DN_STATUS     = 'A' ");
					sb.append("      GROUP BY EDO_ASN_NBR ");
					sb.append("    ) DN, ");
					sb.append("    TCTS_ASN ");
					sb.append("  WHERE MF.BL_STATUS       = 'A' ");
					sb.append("  AND TCTS_ASN.EDO_ASN_NBR = EDO.EDO_ASN_NBR ");
					sb.append("  AND TCTS_ASN.STATUS_IND  = 'A' ");
					sb.append("  AND MF.MFT_SEQ_NBR       = EDO.MFT_SEQ_NBR ");
					sb.append("  AND MF.VAR_NBR           = VC.VV_CD ");
					sb.append("  AND VC.VV_CD             = B.VV_CD ");
					sb.append(
							"  AND((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
					sb.append("  AND B.SHIFT_IND          = 1 ");
					sb.append("  AND EDO.EDO_ASN_NBR      = DN.EDO_ASN_NBR ");
					sb.append("  AND EDO.EDO_STATUS       = 'A' ");
					sb.append("  GROUP BY TCTS_ASN.EDO_ASN_NBR, ");
					sb.append("    MF.BL_NBR, ");
					sb.append("    MF.NBR_PKGS, ");
					sb.append("    EDO.NBR_PKGS, ");
					sb.append("    EDO.DN_NBR_PKGS, ");
					sb.append("    DN.DELIVERED_PKGS, ");
					sb.append("    VC.VSL_NM, ");
					sb.append("    VC.IN_VOY_NBR, ");
					sb.append("    B.ATB_DTTM, ");
					sb.append("    B.COD_DTTM, ");
					sb.append("    EDO.ADP_NM, ");
					sb.append("    VC.VV_CD, EDO.EDO_CREATE_CD, MF.MFT_SEQ_NBR,  EDO.VAR_NBR, EDO.CRG_STATUS ");
					sb.append(")");
				} else {
					sb.append("SELECT COUNT(*) FROM ( ");
					sb.append("	SELECT ESN.ESN_ASN_NBR, ");
					sb.append("	  BK.BK_REF_NBR, ");
					sb.append("      NVL(BK.BK_NBR_PKGS, 0) BK_REF_PKGS, ");
					sb.append("	  NVL(ESND.NBR_PKGS, 0) PKGS, ");
					sb.append("	  NVL(UA.STORED_PKGS, 0) STORED_PKGS, ");
					sb.append("      (NVL(ESND.NBR_PKGS, 0) -  NVL(UA.STORED_PKGS, 0)) BAL_PKGS, ");
					sb.append("	  VC.VSL_NM, ");
					sb.append("	  VC.OUT_VOY_NBR, ");
					sb.append("	  B.ETB_DTTM, ");
					sb.append("	  B.ATB_DTTM, ");
					sb.append("	  ESND.FIRST_TRANS_DTTM FIRST_UA, ");
					sb.append("	  ESND.TRUCKER_NM TRUCKER, ");
					sb.append("	  VC.VV_CD, ");
					sb.append("	  DECODE(ESN.TRANS_TYPE, 'A', 'JP - JP', 'C', 'PSA - JP', 'E', 'Local') STATUS, ");
					sb.append("	  'Export' MOVEMENT, ");
					sb.append("	   VC.IN_VOY_NBR, ");
					sb.append("	   ESN.ESN_CREATE_CD, ");
					sb.append("	   'ESN' CRG_TYPE, ");
					sb.append(
							"	   TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24:MI') || ' ' || TO_CHAR(B.ATB_DTTM,'dd/mm/yyyy HH24:MI') ETB_ATB ");
					sb.append(" ");
					sb.append("	FROM BK_DETAILS BK, ");
					sb.append("	    ESN, ");
					sb.append("	    ESN_DETAILS ESND, ");
					sb.append("	    VESSEL_CALL VC, ");
					sb.append("	    BERTHING B, ");
					sb.append("	    ( ");
					sb.append("        SELECT SUM(NBR_PKGS) STORED_PKGS, ESN_ASN_NBR ");
					sb.append("        FROM  UA_DETAILS ");
					sb.append("        WHERE  UA_STATUS    = 'A' ");
					sb.append("        GROUP BY ESN_ASN_NBR ");
					sb.append("      ) UA ");
					sb.append("	WHERE B.VV_CD       = VC.VV_CD ");
					sb.append("	AND VC.VV_CD        = ESN.OUT_VOY_VAR_NBR ");
					sb.append("	AND ESN.BK_REF_NBR  = BK.BK_REF_NBR ");
					sb.append("	AND ESN.ESN_ASN_NBR = ESND.ESN_ASN_NBR ");
					sb.append("	AND ESN.ESN_STATUS  = 'A' ");
					sb.append(
							"	AND((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
					sb.append("	AND ESN.ESN_ASN_NBR = UA.ESN_ASN_NBR ");
					sb.append("	GROUP BY VC.VSL_NM, ");
					sb.append("	  VC.OUT_VOY_NBR, ");
					sb.append("	  VC.VV_CD, ");
					sb.append("	  BK.BK_REF_NBR, ");
					sb.append("      BK.BK_NBR_PKGS, ");
					sb.append("	  ESN.ESN_ASN_NBR, ");
					sb.append("	  ESND.UA_NBR_PKGS, ");
					sb.append("	  ESND.NBR_PKGS, ");
					sb.append("      STORED_PKGS, ");
					sb.append("	  ESND.FIRST_TRANS_DTTM, ");
					sb.append("	  ESND.TRUCKER_NM, ");
					sb.append("	  B.ETB_DTTM, ");
					sb.append("	  B.ATB_DTTM, ");
					sb.append("	  VC.IN_VOY_NBR, ");
					sb.append("	  ESN.ESN_CREATE_CD, ESN.TRANS_TYPE ");
					sb.append(" ");
					sb.append("	UNION ");
					sb.append("	SELECT TCTS_ESN.ESN_ASN_NBR, ");
					sb.append("	  BK.BK_REF_NBR, ");
					sb.append("      NVL(BK.BK_NBR_PKGS, 0) BK_REF_PKGS, ");
					sb.append("	  NVL(ESND.NBR_PKGS, 0) PKGS, ");
					sb.append("	  NVL(UA.STORED_PKGS, 0) STORED_PKGS, ");
					sb.append("      (NVL(ESND.NBR_PKGS, 0) -  NVL(UA.STORED_PKGS, 0)) BAL_PKGS, ");
					sb.append("	  VC.VSL_NM, ");
					sb.append("	  VC.OUT_VOY_NBR, ");
					sb.append("	  B.ETB_DTTM, ");
					sb.append("	  B.ATB_DTTM, ");
					sb.append("	  ESND.FIRST_TRANS_DTTM FIRST_UA, ");
					sb.append("	  ESND.TRUCKER_NM TRUCKER, ");
					sb.append("	  VC.VV_CD, ");
					sb.append("	  'Local' STATUS, ");
					sb.append("	  'TCTS' MOVEMENT, ");
					sb.append("	  VC.IN_VOY_NBR, ");
					sb.append("	  ESN.ESN_CREATE_CD, ");
					sb.append("	  'ESN' CRG_TYPE, ");
					sb.append(
							"	   TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24:MI') || ' ' || TO_CHAR(B.ATB_DTTM,'dd/mm/yyyy HH24:MI') ETB_ATB ");
					sb.append("	FROM BK_DETAILS BK, ");
					sb.append("	    ESN, ");
					sb.append("	    ESN_DETAILS ESND, ");
					sb.append("	    VESSEL_CALL VC, ");
					sb.append("	    BERTHING B, ");
					sb.append("	    ( ");
					sb.append("        SELECT SUM(NBR_PKGS) STORED_PKGS, ESN_ASN_NBR ");
					sb.append("        FROM  UA_DETAILS ");
					sb.append("        WHERE  UA_STATUS    = 'A' ");
					sb.append("        GROUP BY ESN_ASN_NBR ");
					sb.append("      ) UA, ");
					sb.append("	    TCTS_ESN_MAPPING TCTS_ESN ");
					sb.append("	WHERE B.VV_CD       = VC.VV_CD ");
					sb.append("	AND TCTS_ESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
					sb.append("	AND TCTS_ESN.REC_IND = 'A' ");
					sb.append("	AND VC.VV_CD        = ESN.OUT_VOY_VAR_NBR ");
					sb.append("	AND ESN.BK_REF_NBR  = BK.BK_REF_NBR ");
					sb.append("	AND ESN.ESN_ASN_NBR = ESND.ESN_ASN_NBR ");
					sb.append("	AND ESN.ESN_STATUS  = 'A' ");
					sb.append(
							"	AND((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
					sb.append("	AND ESN.ESN_ASN_NBR = UA.ESN_ASN_NBR ");
					sb.append("	GROUP BY ");
					sb.append("	 TCTS_ESN.ESN_ASN_NBR, ");
					sb.append("	 VC.VSL_NM, ");
					sb.append("	 VC.OUT_VOY_NBR, ");
					sb.append("	 VC.VV_CD, ");
					sb.append("	 BK.BK_REF_NBR, ");
					sb.append("     BK.BK_NBR_PKGS, ");
					sb.append("	 ESND.UA_NBR_PKGS, ");
					sb.append("	 ESND.NBR_PKGS, ");
					sb.append("     STORED_PKGS, ");
					sb.append("	 ESND.FIRST_TRANS_DTTM, ");
					sb.append("	 ESND.TRUCKER_NM, ");
					sb.append("	 B.ETB_DTTM, ");
					sb.append("	 B.ATB_DTTM, ");
					sb.append("	 VC.IN_VOY_NBR, ");
					sb.append("	 ESN.ESN_CREATE_CD, ESN.TRANS_TYPE ");
					sb.append(" ");
					sb.append("   UNION ");
					sb.append("   SELECT TESN.ESN_ASN_NBR, ");
					sb.append("	  BK.BK_REF_NBR, ");
					sb.append("      NVL(BK.BK_NBR_PKGS, 0) BK_REF_PKGS, ");
					sb.append("	  NVL(TESN.NBR_PKGS, 0) PKGS, ");
					sb.append("	  NVL(TESN.UA_NBR_PKGS, 0) STORED_PKGS, ");
					sb.append("      (NVL(TESN.NBR_PKGS, 0) -  NVL(TESN.UA_NBR_PKGS, 0)) BAL_PKGS, ");
					sb.append("	  VC.VSL_NM, ");
					sb.append("	  VC.OUT_VOY_NBR, ");
					sb.append("	  B.ETB_DTTM, ");
					sb.append("	  B.ATB_DTTM, ");
					sb.append("	  TESN.FIRST_TRANS_DTTM FIRST_UA, ");
					sb.append("	  '' TRUCKER, ");
					sb.append("	  VC.VV_CD, ");
					sb.append("	  DECODE(ESN.TRANS_TYPE, 'A', 'JP - JP', 'C', 'PSA - JP', 'E', 'Local') STATUS, ");
					sb.append("	  'Export' MOVEMENT, ");
					sb.append("	  VC.IN_VOY_NBR, ");
					sb.append("	  ESN.ESN_CREATE_CD, ");
					sb.append("	  'TESN_JP_JP' CRG_TYPE, ");
					sb.append(
							"	  TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24:MI') || ' ' || TO_CHAR(B.ATB_DTTM,'dd/mm/yyyy HH24:MI') ETB_ATB ");
					sb.append(" ");
					sb.append("	FROM BK_DETAILS BK, ");
					sb.append("	    ESN, ");
					sb.append("        TESN_JP_JP TESN, ");
					sb.append("	     ");
					sb.append("	    VESSEL_CALL VC, ");
					sb.append("	    BERTHING B ");
					sb.append("	WHERE B.VV_CD       = VC.VV_CD ");
					sb.append("	AND VC.VV_CD        = ESN.OUT_VOY_VAR_NBR ");
					sb.append("	AND ESN.BK_REF_NBR  = BK.BK_REF_NBR ");
					sb.append("	AND ESN.ESN_STATUS  = 'A' ");
					sb.append(
							"	AND((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
					sb.append("    AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
					sb.append("	GROUP BY VC.VSL_NM, ");
					sb.append("	  VC.OUT_VOY_NBR, ");
					sb.append("	  VC.VV_CD, ");
					sb.append("	  BK.BK_REF_NBR, ");
					sb.append("      BK.BK_NBR_PKGS, ");
					sb.append("	  TESN.ESN_ASN_NBR, ");
					sb.append("      TESN.NBR_PKGS, ");
					sb.append("      TESN.UA_NBR_PKGS, ");
					sb.append("	  TESN.FIRST_TRANS_DTTM, ");
					sb.append("	  B.ETB_DTTM, ");
					sb.append("	  B.ATB_DTTM, ");
					sb.append("	  VC.IN_VOY_NBR, ");
					sb.append("	  ESN.ESN_CREATE_CD, ESN.TRANS_TYPE ");
					sb.append(" ");
					sb.append("   UNION ");
					sb.append("   SELECT TESN.ESN_ASN_NBR, ");
					sb.append("	  BK.BK_REF_NBR, ");
					sb.append("      NVL(BK.BK_NBR_PKGS, 0) BK_REF_PKGS, ");
					sb.append("	  NVL(TESN.NBR_PKGS, 0) PKGS, ");
					sb.append("	  NVL(TESN.UA_NBR_PKGS, 0) STORED_PKGS, ");
					sb.append("      (NVL(TESN.NBR_PKGS, 0) -  NVL(TESN.UA_NBR_PKGS, 0)) BAL_PKGS, ");
					sb.append("	  VC.VSL_NM, ");
					sb.append("	  VC.OUT_VOY_NBR, ");
					sb.append("	  B.ETB_DTTM, ");
					sb.append("	  B.ATB_DTTM, ");
					sb.append("	  TESN.FIRST_TRANS_DTTM FIRST_UA, ");
					sb.append("	  TESN.TRUCKER_NM TRUCKER, ");
					sb.append("	  VC.VV_CD, ");
					sb.append("	  DECODE(ESN.TRANS_TYPE, 'A', 'JP - JP', 'C', 'PSA - JP', 'E', 'Local') STATUS, ");
					sb.append("	  'Export' MOVEMENT, ");
					sb.append("	   VC.IN_VOY_NBR, ");
					sb.append("	   ESN.ESN_CREATE_CD, ");
					sb.append("	  'TESN_PSA_JP' CRG_TYPE, ");
					sb.append(
							"	   TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24:MI') || ' ' || TO_CHAR(B.ATB_DTTM,'dd/mm/yyyy HH24:MI') ETB_ATB ");
					sb.append(" ");
					sb.append("	FROM BK_DETAILS BK, ");
					sb.append("	    ESN, ");
					sb.append("        TESN_PSA_JP TESN, ");
					sb.append("	     ");
					sb.append("	    VESSEL_CALL VC, ");
					sb.append("	    BERTHING B ");
					sb.append("	WHERE B.VV_CD       = VC.VV_CD ");
					sb.append("	AND VC.VV_CD        = ESN.OUT_VOY_VAR_NBR ");
					sb.append("	AND ESN.BK_REF_NBR  = BK.BK_REF_NBR ");
					sb.append("	AND ESN.ESN_STATUS  = 'A' ");
					sb.append(
							"	AND((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
					sb.append("    AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
					sb.append("	GROUP BY VC.VSL_NM, ");
					sb.append("	  VC.OUT_VOY_NBR, ");
					sb.append("	  VC.VV_CD, ");
					sb.append("	  BK.BK_REF_NBR, ");
					sb.append("      BK.BK_NBR_PKGS, ");
					sb.append("	  TESN.ESN_ASN_NBR, ");
					sb.append("      TESN.NBR_PKGS, ");
					sb.append("	  TESN.UA_NBR_PKGS, ");
					sb.append("	  TESN.FIRST_TRANS_DTTM, ");
					sb.append("	  TESN.TRUCKER_NM, ");
					sb.append("	  B.ETB_DTTM, ");
					sb.append("	  B.ATB_DTTM, ");
					sb.append("	  VC.IN_VOY_NBR, ");
					sb.append("	  ESN.ESN_CREATE_CD, ESN.TRANS_TYPE ");
					sb.append(" ");
					sb.append(")");
				}
				buildQueryParameter(filters, params, sb);
				log.info("countRecords SQL " + sb.toString() + ", paramMap = " + params.toString());
				countHaulier = namedParameterJdbcTemplate.queryForObject(sb.toString(), params, Integer.class);
				
				log.info("END: *** countRecords Result *****" + countHaulier);
				
			} catch (Exception e) {
				log.info("Exception countRecords : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END countRecords DAO");
			}
			return countHaulier;
		}

}
