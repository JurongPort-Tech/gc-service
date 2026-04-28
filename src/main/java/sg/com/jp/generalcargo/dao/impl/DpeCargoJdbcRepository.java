package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.DpeCargoRepository;
import sg.com.jp.generalcargo.domain.AsnHistory;
import sg.com.jp.generalcargo.domain.CargoEnquiryDetails;
import sg.com.jp.generalcargo.domain.CargoEnquiryMgmtAction;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DNUADetail;
import sg.com.jp.generalcargo.domain.DPECargo;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("DpeCargoRepo")
public class DpeCargoJdbcRepository implements DpeCargoRepository {

	private static final Log log = LogFactory.getLog(DpeCargoJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->getCargoRecord()
	@Override
	public CargoEnquiryDetails getCargoRecord(String edoNbr, Long esnNbr, String type) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<>();
		CargoEnquiryDetails record = null;
		try {
			log.info("START:getCargoRecord DAO edoNbr" + CommonUtility.deNull(edoNbr) + "esnNbr:"
					+ CommonUtility.deNull(String.valueOf(esnNbr)) + "type:" + CommonUtility.deNull(type));

			params.put("edoNbr", edoNbr);
			params.put("esnNbr", esnNbr.toString());
			if ("GB_EDO".equalsIgnoreCase(type)) {
				sb.append(
						" SELECT GE.VAR_NBR DISC_VV_CD, GE.EDO_ASN_NBR, GE.BL_NBR, GE.NBR_PKGS EDO_ASN_PKGS, GE.NOM_WT WEIGHT, ");
				sb.append(
						" MD.HS_CODE, (MD.HS_SUB_CODE_FR || '-' || MD.HS_SUB_CODE_TO) HS_SUB_CODE, MD.CRG_DES, DECODE(MD.DG_IND, 'Y', 'Yes', 'N', 'No')");
				sb.append(
						" DG_IND, PM4.IMO_CL, DECODE(MD.STG_TYPE, 'O', 'Open', 'C', 'Covered') STG_TYPE, PM4.UCR_NBR, ");
				sb.append(
						" NULL ESN_ASN_NBR, NULL BK_REF_NBR, NULL ESN_ASN_PKGS, GE.NOM_VOL, DECODE(GE.SHUTOUT_IND, 'Y', 'Yes', 'N', 'No') SHUTOUT_IND, GE.WH_AGGR_NBR,");
				sb.append(" 'J' DISC_GATE_WAY, CONCAT(DVSL.SCHEME,CASE WHEN DVSL.COMBI_GC_OPS_IND ='Y' ");
				sb.append(
						" THEN  ' / ' ||(NVL(DVSL.COMBI_GC_SCHEME,'-')) ELSE '' END) DISC_SCHEME,DVSL.TERMINAL DISC_TERMINAL, ");
				sb.append(" (NVL(DVSL.VSL_NM, '-') || '/' || NVL(DVSL.IN_VOY_NBR, '-')) DISC_VSL,");
				sb.append(
						" DBE.ATB_DTTM DISC_ARRIVAL, DBE.BERTH_NBR DISC_BERTH, DECODE(GE.CRG_STATUS, 'L', 'Local', 'T', 'Transhipment', 'R', 'Re-export') ");
				sb.append(
						" DISC_STATUS, DECODE(GE.DIS_TYPE, 'N', 'Normal', 'D', 'Direct', 'O', 'Overside', 'L', 'Land/Reship') DISC_OPER, ");
				sb.append(" NULL JP_YARD_LOCATION,  DBE.COD_DTTM COMPLETETION_DISC, ");
				sb.append(" NULL DISC_FREE_STORE_RENT_EXPIRY, LPC.PORT_NM PORT_LOAD, NULL REMARKS, ");
				sb.append(
						" NULL LOAD_GATE_WAY, NULL LOAD_SCHEME, NULL LOAD_VSL, NULL LOAD_ARRIVAL, NULL LOAD_BERTH, NULL SHIPMENT_STATUS, NULL LOAD_OPER, ");
				sb.append(
						" NULL COMPLETETION_LOAD, NULL LOAD_FREE_STORE_RENT_EXPIRY, NULL PORT_DISC, GE.MFT_SEQ_NBR, GE.edo_status asn_status ");
				sb.append(" FROM  GB_EDO GE ");
				sb.append(
						" INNER JOIN MANIFEST_DETAILS MD ON GE.VAR_NBR = MD.VAR_NBR AND GE.MFT_SEQ_NBR = MD.MFT_SEQ_NBR ");
				sb.append(" INNER JOIN VESSEL_CALL DVSL ON GE.VAR_NBR =  DVSL.VV_CD ");
				sb.append(" INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append(" LEFT JOIN UN_PORT_CODE LPC ON MD.LD_PORT = LPC.PORT_CD ");
				sb.append(" LEFT JOIN PM4 PM4 ON GE.VAR_NBR =  PM4.VAR_NBR AND GE.BL_NBR = PM4.BL_NBR ");
				sb.append(" WHERE EDO_ASN_NBR = :edoNbr ");
			} else if ("ESN".equalsIgnoreCase(type)) {
				sb.append(
						" SELECT E.OUT_VOY_VAR_NBR LOAD_VV_CD, NULL EDO_ASN_NBR, NULL BL_NBR, NULL EDO_ASN_PKGS, SUM(ED.ESN_WT) WEIGHT, ");
				sb.append(
						" ED.ESN_HS_CODE HS_CODE, (ED.HS_SUB_CODE_FR || '-' || ED.HS_SUB_CODE_TO) SUB_HS_CODE, ED.CRG_DES, DECODE(ED.ESN_DG_IND, 'Y', 'Yes', 'N', 'No') ");
				sb.append(
						" DG_IND, PM4.IMO_CL, DECODE(ED.STG_IND, 'O', 'Open', 'C', 'Covered') STG_TYPE, PM4.UCR_NBR, ");
				sb.append(
						" E.ESN_ASN_NBR, E.BK_REF_NBR, ED.NBR_PKGS ESN_ASN_PKGS, ED.ESN_VOL NOM_VOL, NULL SHUTOUT_IND, E.WH_AGGR_NBR, ");
				sb.append(
						" NULL DISC_GATE_WAY, NULL DISC_SCHEME, NULL DISC_TERMINAL, NULL DISC_VSL, NULL DISC_ARRIVAL, ");
				sb.append(
						" NULL DISC_BERTH, NULL DISC_STATUS, NULL DISC_OPER, NULL JP_YARD_LOCATION,  NULL COMPLETETION_DISC, ");
				sb.append(" NULL DISC_FREE_STORE_RENT_EXPIRY, NULL PORT_LOAD, NULL REMARKS, ");
				sb.append(
						" 'J' LOAD_GATE_WAY, CONCAT(LVSL.SCHEME,CASE WHEN LVSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(LVSL.COMBI_GC_SCHEME,'-')) ELSE '' END) LOAD_SCHEME, ");
				sb.append(
						" LVSL.TERMINAL LOAD_TERMINAL,(NVL(LVSL.VSL_NM, '-') || '/' || NVL(LVSL.OUT_VOY_NBR, '-')) LOAD_VSL, ");
				sb.append(" LBE.ATB_DTTM LOAD_ARRIVAL, LBE.BERTH_NBR LOAD_BERTH, 'Local Export' SHIPMENT_STATUS, ");
				sb.append(
						" DECODE(ED.ESN_OPS_IND, 'N', 'Normal', 'D', 'Direct', 'O', 'Overside', 'L', 'Land/Reship') LOAD_OPER, ");
				sb.append(" LBE.COL_DTTM COMPLETETION_LOAD, NULL LOAD_FREE_STORE_RENT_EXPIRY, ");
				sb.append(" DPC.PORT_NM PORT_DISC, NULL MFT_SEQ_NBR, e.esn_status asn_status ");
				sb.append(" FROM  ESN E ");
				sb.append(" INNER JOIN ESN_DETAILS ED ON (E.ESN_ASN_NBR = ED.ESN_ASN_NBR) ");
				sb.append(" INNER JOIN VESSEL_CALL LVSL ON E.OUT_VOY_VAR_NBR =  LVSL.VV_CD ");
				sb.append(" INNER JOIN BERTHING LBE ON LVSL.VV_CD = LBE.VV_CD AND LBE.SHIFT_IND = 1 ");
				sb.append(" LEFT JOIN UN_PORT_CODE DPC ON ED.ESN_PORT_DIS = DPC.PORT_CD ");
				sb.append(" LEFT JOIN PM4 PM4 ON E.OUT_VOY_VAR_NBR  = PM4.VAR_NBR AND E.BK_REF_NBR = PM4.BL_NBR ");
				sb.append(" WHERE E.ESN_ASN_NBR = :esnNbr ");
				sb.append(
						" GROUP BY E.OUT_VOY_VAR_NBR, ED.ESN_HS_CODE, (ED.HS_SUB_CODE_FR || '-' || ED.HS_SUB_CODE_TO), ");
				sb.append(" ED.CRG_DES, ED.ESN_DG_IND, PM4.IMO_CL, ED.STG_IND, PM4.UCR_NBR, ");
				sb.append(
						" E.ESN_ASN_NBR, E.BK_REF_NBR, ED.NBR_PKGS, ED.ESN_VOL, E.WH_AGGR_NBR,LVSL.COMBI_GC_OPS_IND, ");
				sb.append(
						" LVSL.SCHEME, LVSL.COMBI_GC_SCHEME,LVSL.TERMINAL, LVSL.VSL_NM,LVSL.OUT_VOY_NBR,LBE.ATB_DTTM, LBE.BERTH_NBR, LBE.BERTH_NBR, ");
				sb.append(" ED.ESN_OPS_IND,  LBE.COL_DTTM, DPC.PORT_NM, e.esn_status ");
			} else if ("TESN_JP_JP".equalsIgnoreCase(type)) {
				sb.append(
						" SELECT GE.VAR_NBR DISC_VV_CD, E.OUT_VOY_VAR_NBR LOAD_VV_CD, TESN.EDO_ASN_NBR, GE.BL_NBR, GE.NBR_PKGS EDO_ASN_PKGS, SUM(TESN.NOM_WT) WEIGHT, ");
				sb.append(
						" MD.HS_CODE, (MD.HS_SUB_CODE_FR || '-' || MD.HS_SUB_CODE_TO) HS_SUB_CODE, MD.CRG_DES, DECODE(MD.DG_IND, 'Y', 'Yes', 'N', 'No') DG_IND, ");
				sb.append(" PM4.IMO_CL, DECODE(MD.STG_TYPE, 'O', 'Open', 'C', 'Covered') STG_TYPE, PM4.UCR_NBR, ");
				sb.append(
						" TESN.ESN_ASN_NBR, E.BK_REF_NBR, TESN.NBR_PKGS ESN_ASN_PKGS, TESN.NOM_VOL, DECODE(GE.SHUTOUT_IND, 'Y', 'Yes', 'N', 'No') SHUTOUT_IND, GE.WH_AGGR_NBR, ");
				sb.append(
						" 'J' DISC_GATE_WAY, CONCAT(DVSL.SCHEME,CASE WHEN DVSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(DVSL.COMBI_GC_SCHEME,'-')) ELSE '' END) DISC_SCHEME, ");
				sb.append(
						" DVSL.TERMINAL DISC_TERMINAL, (NVL(DVSL.VSL_NM, '-') || '/' || NVL(DVSL.IN_VOY_NBR, '-')) DISC_VSL, ");
				sb.append(" DBE.ATB_DTTM DISC_ARRIVAL, DBE.BERTH_NBR DISC_BERTH, ");
				sb.append(" DECODE(GE.CRG_STATUS, 'L', 'Local', 'T', 'Transhipment', 'R', 'Re-export') DISC_STATUS, ");
				sb.append(
						" DECODE(GE.DIS_TYPE, 'N', 'Normal', 'D', 'Direct', 'O', 'Overside', 'L', 'Land/Reship') DISC_OPER, ");
				sb.append(
						" NULL JP_YARD_LOCATION,  DBE.COD_DTTM COMPLETETION_DISC, NULL DISC_FREE_STORE_RENT_EXPIRY, ");
				sb.append(
						" LPC.PORT_NM PORT_LOAD, NULL REMARKS, 'J' LOAD_GATE_WAY, CONCAT(LVSL.SCHEME,CASE WHEN LVSL.COMBI_GC_OPS_IND ='Y' ");
				sb.append(
						" THEN  ' / ' ||(NVL(LVSL.COMBI_GC_SCHEME,'-')) ELSE '' END) LOAD_SCHEME, LVSL.TERMINAL LOAD_TERMINAL, ");
				sb.append(
						" (NVL(LVSL.VSL_NM, '-') || '/' || NVL(LVSL.OUT_VOY_NBR, '-')) LOAD_VSL, LBE.ATB_DTTM LOAD_ARRIVAL, ");
				sb.append(
						" LBE.BERTH_NBR LOAD_BERTH, 'JP to JP' SHIPMENT_STATUS, DECODE(TESN.LD_IND, 'N', 'Normal', 'D', 'Direct', 'O', 'Overside', 'L', 'Land/Reship') ");
				sb.append(" LOAD_OPER, LBE.COL_DTTM COMPLETETION_LOAD, NULL LOAD_FREE_STORE_RENT_EXPIRY, ");
				sb.append(" DPC.PORT_NM PORT_DISC, GE.MFT_SEQ_NBR, e.esn_status asn_status ");
				sb.append(" FROM ESN E INNER JOIN TESN_JP_JP TESN ON E.ESN_ASN_NBR = TESN.ESN_ASN_NBR ");
				sb.append(" LEFT JOIN GB_EDO GE ON TESN.EDO_ASN_NBR = GE.EDO_ASN_NBR ");
				sb.append(
						" LEFT JOIN MANIFEST_DETAILS MD ON GE.VAR_NBR = MD.VAR_NBR AND GE.MFT_SEQ_NBR = MD.MFT_SEQ_NBR ");
				sb.append(" LEFT JOIN PM4 PM4 ON GE.VAR_NBR =  PM4.VAR_NBR AND GE.BL_NBR = PM4.BL_NBR ");
				sb.append(" LEFT JOIN UN_PORT_CODE LPC ON MD.LD_PORT = LPC.PORT_CD ");
				sb.append(" LEFT JOIN UN_PORT_CODE DPC ON MD.LD_PORT = DPC.PORT_CD ");
				sb.append(" LEFT JOIN VESSEL_CALL DVSL ON E.IN_VOY_VAR_NBR =  DVSL.VV_CD ");
				sb.append(" LEFT JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append(" LEFT JOIN VESSEL_CALL LVSL ON E.OUT_VOY_VAR_NBR =  LVSL.VV_CD ");
				sb.append(" LEFT JOIN BERTHING LBE ON LVSL.VV_CD = LBE.VV_CD AND LBE.SHIFT_IND = 1 ");
				sb.append(" WHERE E.ESN_ASN_NBR = :esnNbr ");
				sb.append(" GROUP BY GE.VAR_NBR, E.OUT_VOY_VAR_NBR, TESN.EDO_ASN_NBR, GE.BL_NBR, GE.NBR_PKGS, ");
				sb.append(
						" MD.HS_CODE, MD.HS_SUB_CODE_FR || '-' || MD.HS_SUB_CODE_TO, MD.CRG_DES, MD.DG_IND, PM4.IMO_CL, MD.STG_TYPE, PM4.UCR_NBR, ");
				sb.append(
						" TESN.ESN_ASN_NBR, E.BK_REF_NBR, TESN.NBR_PKGS, TESN.NOM_VOL, GE.SHUTOUT_IND, GE.WH_AGGR_NBR, ");
				sb.append(
						" DVSL.SCHEME,DVSL.COMBI_GC_SCHEME,DVSL.COMBI_GC_OPS_IND,DVSL.TERMINAL, DVSL.VSL_NM, DVSL.IN_VOY_NBR,DBE.ATB_DTTM, DBE.BERTH_NBR, ");
				sb.append(" GE.CRG_STATUS, GE.DIS_TYPE, DBE.COD_DTTM, LPC.PORT_NM, ");
				sb.append(
						" LVSL.SCHEME, LVSL.COMBI_GC_SCHEME,LVSL.COMBI_GC_OPS_IND,LVSL.TERMINAL, LVSL.VSL_NM, LVSL.OUT_VOY_NBR,LBE.ATB_DTTM, ");
				sb.append(" LBE.BERTH_NBR, TESN.LD_IND, LBE.COL_DTTM, DPC.PORT_NM,  GE.MFT_SEQ_NBR, e.esn_status ");
			} else if ("TESN_JP_PSA".equalsIgnoreCase(type)) {
				sb.append(" SELECT GE.VAR_NBR DISC_VV_CD, E.IN_VOY_VAR_NBR LOAD_VV_CD, TESN.EDO_ASN_NBR, GE.BL_NBR, ");
				sb.append(" TESN.EDO_NBR_PKGS EDO_ASN_PKGS, SUM(TESN.NOM_WT) WEIGHT, ");
				sb.append(
						" MD.HS_CODE, (MD.HS_SUB_CODE_FR || '-' || MD.HS_SUB_CODE_TO) HS_SUB_CODE, MD.CRG_DES, DECODE(MD.DG_IND, 'Y', 'Yes', 'N', 'No') DG_IND, ");
				sb.append(" PM4.IMO_CL, DECODE(MD.STG_TYPE, 'O', 'Open', 'C', 'Covered') STG_TYPE, PM4.UCR_NBR, ");
				sb.append(" TESN.ESN_ASN_NBR, E.BK_REF_NBR, SUM(TESN.NBR_PKGS) ESN_ASN_PKGS, ");
				sb.append(" TESN.NOM_VOL, DECODE(GE.SHUTOUT_IND, 'Y', 'Yes', 'N', 'No') SHUTOUT_IND, GE.WH_AGGR_NBR, ");
				sb.append(
						" 'J' DISC_GATE_WAY, CONCAT(DVSL.SCHEME,CASE WHEN DVSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(DVSL.COMBI_GC_SCHEME,'-')) ELSE '' END) DISC_SCHEME, ");
				sb.append(
						" DVSL.TERMINAL DISC_TERMINAL, (NVL(DVSL.VSL_NM, '-') || '/' || NVL(DVSL.IN_VOY_NBR, '-')) DISC_VSL, ");
				sb.append(
						" DBE.ATB_DTTM DISC_ARRIVAL, DBE.BERTH_NBR DISC_BERTH, DECODE(GE.CRG_STATUS, 'L', 'Local', 'T', 'Transhipment', 'R', 'Re-export') DISC_STATUS, ");
				sb.append(
						" DECODE(GE.DIS_TYPE, 'N', 'Normal', 'D', 'Direct', 'O', 'Overside', 'L', 'Land/Reship') DISC_OPER, ");
				sb.append(
						" NULL JP_YARD_LOCATION,  DBE.COD_DTTM COMPLETETION_DISC, NULL DISC_FREE_STORE_RENT_EXPIRY, ");
				sb.append(
						" LPC.PORT_NM PORT_LOAD, NULL REMARKS, 'P' LOAD_GATE_WAY, NULL LOAD_SCHEME, NULL LOAD_TERMINAL , ");
				sb.append(
						" (TESN.SECOND_CAR_VES_NM || '/' || SECOND_CAR_VOY_NBR) LOAD_VSL,LVSL.ATB_DTTM LOAD_ARRIVAL, ");
				sb.append(" LVSL.BERTH_NBR LOAD_BERTH, 'JP to PSA' SHIPMENT_STATUS, NULL LOAD_OPER, ");
				sb.append(
						" LVSL.COL_DTTM COMPLETETION_LOAD, NULL LOAD_FREE_STORE_RENT_EXPIRY, DPC.PORT_NM PORT_DISC, GE.MFT_SEQ_NBR,e.esn_status asn_status ");
				sb.append(" FROM ESN E INNER JOIN TESN_JP_PSA TESN ON E.ESN_ASN_NBR = TESN.ESN_ASN_NBR ");
				sb.append(" LEFT JOIN GB_EDO GE ON TESN.EDO_ASN_NBR = GE.EDO_ASN_NBR ");
				sb.append(
						" LEFT JOIN MANIFEST_DETAILS MD ON GE.VAR_NBR = MD.VAR_NBR AND GE.MFT_SEQ_NBR = MD.MFT_SEQ_NBR ");
				sb.append(" LEFT JOIN UN_PORT_CODE LPC ON MD.LD_PORT = LPC.PORT_CD ");
				sb.append(" LEFT JOIN PM4 PM4 ON E.IN_VOY_VAR_NBR  = PM4.VAR_NBR AND E.BK_REF_NBR = PM4.BL_NBR ");
				sb.append(" LEFT JOIN VESSEL_CALL DVSL ON E.IN_VOY_VAR_NBR = DVSL.VV_CD ");
				sb.append(
						" LEFT JOIN NOMINATED_VSL LVSL ON TESN.SECOND_CAR_VES_NM =  LVSL.VSL_NM AND TESN.SECOND_CAR_VOY_NBR =  LVSL.OUT_VOY_NBR AND LVSL.REC_STATUS = 'A' ");
				sb.append(" LEFT JOIN BERTHING DBE ON DBE.VV_CD = DVSL.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append(" LEFT JOIN UN_PORT_CODE DPC ON TESN.DIS_PORT = DPC.PORT_CD ");
				sb.append(" WHERE E.ESN_ASN_NBR = :esnNbr ");
				sb.append(" GROUP BY GE.VAR_NBR, E.IN_VOY_VAR_NBR, TESN.EDO_ASN_NBR, GE.BL_NBR, TESN.EDO_NBR_PKGS, ");
				sb.append(
						" MD.HS_CODE, MD.HS_SUB_CODE_FR || '-' || MD.HS_SUB_CODE_TO, MD.CRG_DES, MD.DG_IND, PM4.IMO_CL, MD.STG_TYPE, PM4.UCR_NBR, ");
				sb.append(" TESN.ESN_ASN_NBR, E.BK_REF_NBR, TESN.NOM_VOL, GE.SHUTOUT_IND, GE.WH_AGGR_NBR, ");
				sb.append(
						" DVSL.SCHEME,DVSL.COMBI_GC_SCHEME,DVSL.COMBI_GC_OPS_IND,DVSL.TERMINAL, DVSL.VSL_NM, DVSL.IN_VOY_NBR,DBE.ATB_DTTM, DBE.BERTH_NBR, ");
				sb.append(" GE.CRG_STATUS, GE.DIS_TYPE, DBE.COD_DTTM, LPC.PORT_NM, DPC.PORT_NM ");
				sb.append(
						" ,LVSL.ETB_DTTM,LVSL.ATB_DTTM, SECOND_CAR_VES_NM, SECOND_CAR_VOY_NBR, LVSL.BERTH_NBR, LVSL.COL_DTTM, GE.MFT_SEQ_NBR, e.esn_status ");
			} else if ("TESN_PSA_JP".equalsIgnoreCase(type)) {
				sb.append(
						" SELECT E.OUT_VOY_VAR_NBR LOAD_VV_CD, NULL EDO_ASN_NBR, TESN.BL_NBR BL_NBR, NULL EDO_ASN_PKGS, SUM(TESN.GROSS_WT) WEIGHT, ");
				sb.append(
						" TESN.HS_CD HS_CODE, NULL HS_SUB_CODE, TESN.CRG_DES, DECODE(TESN.DG_IND, 'Y', 'Yes', 'N', 'No') DG_IND, PM4.IMO_CL, ");
				sb.append(" DECODE(TESN.STORAGE_IND, 'O', 'Open', 'C', 'Covered') STG_TYPE, PM4.UCR_NBR, ");
				sb.append(" TESN.ESN_ASN_NBR ESN_ASN_NBR, E.BK_REF_NBR BK_REF_NBR, ");
				sb.append(
						" SUM(TESN.NBR_PKGS) ESN_ASN_PKGS, TESN.GROSS_VOL NOM_VOL, NULL SHUTOUT_IND, NULL WH_AGGR_NBR, ");
				sb.append(
						" 'P' DISC_GATE_WAY, NULL DISC_SCHEME, (FIRST_CAR_VES_NM || '/' || FIRST_CAR_VOY_NBR) DISC_VSL,DVSL.ATB_DTTM DISC_ARRIVAL, ");
				sb.append(
						" DVSL.BERTH_NBR DISC_BERTH, NULL DISC_STATUS, NULL DISC_OPER, NULL JP_YARD_LOCATION,  DVSL.COD_DTTM COMPLETETION_DISC, ");
				sb.append(
						" NULL DISC_FREE_STORE_RENT_EXPIRY, NULL PORT_LOAD, NULL REMARKS, 'J' LOAD_GATE_WAY, CONCAT(LVSL.SCHEME,CASE WHEN LVSL.COMBI_GC_OPS_IND ='Y' ");
				sb.append(
						" THEN  ' / ' ||(NVL(LVSL.COMBI_GC_SCHEME,'-')) ELSE '' END) LOAD_SCHEME, LVSL.TERMINAL LOAD_TERMINAL, ");
				sb.append(" (NVL(LVSL.VSL_NM, '-') || '/' || NVL(LVSL.OUT_VOY_NBR, '-')) LOAD_VSL, ");
				sb.append(" LBE.ATB_DTTM LOAD_ARRIVAL, LBE.BERTH_NBR LOAD_BERTH, 'PSA to JP' SHIPMENT_STATUS, ");
				sb.append(
						" DECODE(TESN.OPS_IND, 'N', 'Normal', 'D', 'Direct', 'O', 'Overside', 'L', 'Land/Reship') LOAD_OPER, ");
				sb.append(" LBE.COL_DTTM COMPLETETION_LOAD, NULL LOAD_FREE_STORE_RENT_EXPIRY, ");
				sb.append(" DPC.PORT_NM PORT_DISC, NULL MFT_SEQ_NBR, e.esn_status asn_status FROM ESN E ");
				sb.append(" INNER JOIN TESN_PSA_JP TESN ON E.ESN_ASN_NBR = TESN.ESN_ASN_NBR ");
				sb.append(" LEFT JOIN UN_PORT_CODE DPC ON TESN.DIS_PORT = DPC.PORT_CD ");
				sb.append(" LEFT JOIN PM4 PM4 ON E.OUT_VOY_VAR_NBR  = PM4.VAR_NBR AND E.BK_REF_NBR = PM4.BL_NBR ");
				sb.append(" LEFT JOIN VESSEL_CALL LVSL ON E.OUT_VOY_VAR_NBR =  LVSL.VV_CD ");
				sb.append(
						" LEFT JOIN NOMINATED_VSL DVSL ON TESN.FIRST_CAR_VES_NM =  DVSL.VSL_NM AND TESN.FIRST_CAR_VOY_NBR =  DVSL.IN_VOY_NBR AND DVSL.REC_STATUS = 'A' ");
				sb.append(" LEFT JOIN BERTHING LBE ON LVSL.VV_CD = LBE.VV_CD AND LBE.SHIFT_IND = 1 ");
				sb.append(" WHERE TESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(
						" GROUP BY E.OUT_VOY_VAR_NBR, TESN.BL_NBR, TESN.HS_CD, TESN.CRG_DES, TESN.DG_IND, PM4.IMO_CL, TESN.STORAGE_IND, PM4.UCR_NBR, ");
				sb.append(" TESN.ESN_ASN_NBR, E.BK_REF_NBR, TESN.GROSS_VOL, LVSL.SCHEME, LVSL.COMBI_GC_SCHEME, ");
				sb.append(
						" LVSL.COMBI_GC_OPS_IND, LVSL.TERMINAL, LVSL.VSL_NM, LVSL.OUT_VOY_NBR,LBE.ATB_DTTM, LBE.BERTH_NBR, ");
				sb.append(
						" TESN.OPS_IND, LBE.COL_DTTM, DPC.PORT_NM,DVSL.ATB_DTTM, FIRST_CAR_VES_NM, FIRST_CAR_VOY_NBR, DVSL.BERTH_NBR, DVSL.COD_DTTM, e.esn_status ");
				// start Itsm 40244 - added order by and get the first result to fixed expected 1 get 2 results - NS May 2024
				sb.append(" ORDER BY CASE WHEN DVSL.ATB_DTTM IS NULL THEN 1 ELSE 0 END, DVSL.ATB_DTTM DESC  ");
				sb.append(" fetch first 1 rows only ");
				// End Itsm 40244 - added order by and get the first result to fixed expected 1 get 2 results - NS May 2024
			} else if ("SS_DETAILS".equalsIgnoreCase(type)) {
				sb.append(
						" SELECT E.OUT_VOY_VAR_NBR LOAD_VV_CD, NULL EDO_ASN_NBR, NULL BL_NBR, NULL EDO_ASN_PKGS, SD.SS_WT WEIGHT, ");
				sb.append(" SD.SS_HS_CODE HS_CODE, NULL HS_SUB_CODE, SD.CRG_DES, ");
				sb.append(
						" DECODE(SD.SS_DG_IND, 'Y', 'Yes', 'N', 'No') DG_IND, PM4.IMO_CL, NULL STG_TYPE, PM4.UCR_NBR, ");
				sb.append(
						" SD.ESN_ASN_NBR, SD.SS_REF_NBR BK_REF_NBR, SD.NBR_PKGS ESN_ASN_PKGS, SD.SS_VOL NOM_VOL, NULL SHUTOUT_IND, E.WH_AGGR_NBR WH_AGGR_NBR, ");
				sb.append(
						" NULL DISC_GATE_WAY, NULL DISC_SCHEME, NULL DISC_TERMINAL, NULL DISC_VSL, NULL DISC_ARRIVAL, ");
				sb.append(
						" NULL DISC_BERTH, NULL DISC_STATUS, NULL DISC_OPER, NULL JP_YARD_LOCATION,  NULL COMPLETETION_DISC, ");
				sb.append(" NULL DISC_FREE_STORE_RENT_EXPIRY, ");
				sb.append(" NULL PORT_LOAD, NULL REMARKS, ");
				sb.append(
						" 'J' LOAD_GATE_WAY, CONCAT(LVSL.SCHEME,CASE WHEN LVSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(LVSL.COMBI_GC_SCHEME,'-')) ");
				sb.append(
						" ELSE '' END) LOAD_SCHEME,LVSL.TERMINAL LOAD_TERMINAL, (NVL(LVSL.VSL_NM, '-') || '/' || NVL(LVSL.OUT_VOY_NBR, '-')) LOAD_VSL, ");
				sb.append(" LBE.ATB_DTTM LOAD_ARRIVAL, ");
				sb.append(" LBE.BERTH_NBR LOAD_BERTH, 'Ship Store' SHIPMENT_STATUS, NULL LOAD_OPER, ");
				sb.append(" LBE.COL_DTTM COMPLETETION_LOAD, ");
				sb.append(" NULL LOAD_FREE_STORE_RENT_EXPIRY, ");
				sb.append(" NULL PORT_DISC, NULL MFT_SEQ_NBR, e.esn_status asn_status ");
				sb.append(" FROM  ESN E LEFT JOIN SS_DETAILS SD ON (E.ESN_ASN_NBR = SD.ESN_ASN_NBR)");
				sb.append(" LEFT JOIN PM4 PM4 ON E.OUT_VOY_VAR_NBR  = PM4.VAR_NBR AND E.BK_REF_NBR = PM4.BL_NBR ");
				sb.append(" LEFT JOIN VESSEL_CALL LVSL ON E.OUT_VOY_VAR_NBR =  LVSL.VV_CD ");
				sb.append(" LEFT JOIN BERTHING LBE ON LVSL.VV_CD = LBE.VV_CD AND LBE.SHIFT_IND = 1 ");
				sb.append(" WHERE E.ESN_ASN_NBR = :esnNbr ");
			} else if ("BULK_GB_EDO".equalsIgnoreCase(type)) {
				sb.append(
						" SELECT GE.VAR_NBR DISC_VV_CD, GE.EDO_ASN_NBR, GE.BL_NBR, 1 EDO_ASN_PKGS, GE.NOM_WT WEIGHT, ");
				sb.append(
						" MD.HS_CODE, (MD.HS_SUB_CODE_FR || '-' || MD.HS_SUB_CODE_TO) HS_SUB_CODE, MD.CRG_DES, DECODE(MD.DG_IND, 'Y', 'Yes', 'N', 'No') DG_IND, ");
				sb.append(" PM4.IMO_CL, DECODE(MD.STG_TYPE, 'O', 'Open', 'C', 'Covered') STG_TYPE, PM4.UCR_NBR, ");
				sb.append(
						" NULL ESN_ASN_NBR, NULL BK_REF_NBR, NULL ESN_ASN_PKGS, NULL NOM_VOL, NULL SHUTOUT_IND, GE.WH_AGGR_NBR, ");
				sb.append(" 'J' DISC_GATE_WAY, CONCAT(DVSL.SCHEME,CASE WHEN DVSL.COMBI_GC_OPS_IND ='Y' ");
				sb.append(
						" THEN  ' / ' ||(NVL(DVSL.COMBI_GC_SCHEME,'-')) ELSE '' END) DISC_SCHEME, DVSL.TERMINAL DISC_TERMINAL, ");
				sb.append(" (NVL(DVSL.VSL_NM, '-') || '/' || NVL(DVSL.IN_VOY_NBR, '-')) DISC_VSL, ");
				sb.append(
						" DBE.ATB_DTTM DISC_ARRIVAL, DBE.BERTH_NBR DISC_BERTH, DECODE(GE.CRG_STATUS, 'L', 'Local', 'T', 'Transhipment', 'R', 'Re-export') DISC_STATUS, ");
				sb.append(
						" DECODE(GE.DIS_TYPE, 'N', 'Normal', 'D', 'Direct', 'O', 'Overside', 'L', 'Land/Reship') DISC_OPER, ");
				sb.append(" NULL JP_YARD_LOCATION,  DBE.COD_DTTM COMPLETETION_DISC, ");
				sb.append(" NULL DISC_FREE_STORE_RENT_EXPIRY, LPC.PORT_NM PORT_LOAD, NULL REMARKS, ");
				sb.append(
						" NULL LOAD_GATE_WAY, NULL LOAD_SCHEME, NULL LOAD_TERMINAL, NULL LOAD_VSL, NULL LOAD_ARRIVAL, NULL SHIPMENT_STATUS, NULL LOAD_OPER,  ");
				sb.append(
						" NULL LOAD_BERTH, NULL COMPLETETION_LOAD, NULL LOAD_FREE_STORE_RENT_EXPIRY, NULL PORT_DISC, GE.MFT_SEQ_NBR, ge.edo_status asn_status ");
				sb.append(" FROM  BULK_GB_EDO GE ");
				sb.append(
						" INNER JOIN BULK_MANIFEST_DETAILS MD ON GE.VAR_NBR = MD.VAR_NBR AND GE.MFT_SEQ_NBR = MD.MFT_SEQ_NBR ");
				sb.append(" INNER JOIN VESSEL_CALL DVSL ON GE.VAR_NBR =  DVSL.VV_CD ");
				sb.append(" INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append(" LEFT JOIN UN_PORT_CODE LPC ON MD.LD_PORT = LPC.PORT_CD ");
				sb.append(" LEFT JOIN PM4 PM4 ON GE.VAR_NBR =  PM4.VAR_NBR AND GE.BL_NBR = PM4.BL_NBR ");
				sb.append(" WHERE EDO_ASN_NBR = :edoNbr ");
			} else if ("BULK_ESN".equalsIgnoreCase(type)) {
				sb.append(
						" SELECT E.OUT_VOY_VAR_NBR LOAD_VV_CD, NULL EDO_ASN_NBR, NULL BL_NBR, NULL EDO_ASN_PKGS, ED.ESN_WT WEIGHT, ED.ESN_HS_CODE HS_CODE, ");
				sb.append(
						" (ED.HS_SUB_CODE_FR || '-' || ED.HS_SUB_CODE_TO) SUB_HS_CODE, ED.CRG_DES, DECODE(ED.ESN_DG_IND, 'Y', 'Yes', 'N', 'No') DG_IND, ");
				sb.append(" PM4.IMO_CL, DECODE(E.WH_IND, 'N', 'Open', 'Y', 'Covered') STG_TYPE, PM4.UCR_NBR, ");
				sb.append(" E.ESN_ASN_NBR, E.BK_REF_NBR, 1 ESN_PKGS, NULL NOM_VOL, NULL SHUTOUT_IND, E.WH_AGGR_NBR, ");
				sb.append(
						" NULL DISC_GATE_WAY, NULL DISC_SCHEME, NULL DISC_TERMINAL, NULL DISC_VSL, NULL DISC_ARRIVAL, ");
				sb.append(
						" NULL DISC_BERTH, NULL DISC_STATUS, NULL DISC_OPER, NULL JP_YARD_LOCATION,  NULL COMPLETETION_DISC, ");
				sb.append(" NULL DISC_FREE_STORE_RENT_EXPIRY, NULL PORT_LOAD, NULL REMARKS, 'J' LOAD_GATE_WAY, ");
				sb.append(
						" CONCAT(LVSL.SCHEME,CASE WHEN LVSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(LVSL.COMBI_GC_SCHEME,'-')) ");
				sb.append(
						" ELSE '' END) LOAD_SCHEME, LVSL.TERMINAL LOAD_TERMINAL, (NVL(LVSL.VSL_NM, '-') || '/' || NVL(LVSL.OUT_VOY_NBR, '-')) LOAD_VSL, ");
				sb.append(" LBE.ATB_DTTM LOAD_ARRIVAL, LBE.BERTH_NBR LOAD_BERTH, 'Local Export' SHIPMENT_STATUS, ");
				sb.append(
						" DECODE(ED.ESN_OPS_IND, 'N', 'Normal', 'D', 'Direct', 'O', 'Overside', 'L', 'Land/Reship') LOAD_OPER, ");
				sb.append(" LBE.COL_DTTM COMPLETETION_LOAD, NULL LOAD_FREE_STORE_RENT_EXPIRY, ");
				sb.append(" DPC.PORT_NM PORT_DISC, NULL MFT_SEQ_NBR, e.esn_status asn_status FROM  BULK_ESN E ");
				sb.append(" INNER JOIN BULK_ESN_DETAILS ED ON (E.ESN_ASN_NBR = ED.ESN_ASN_NBR) ");
				sb.append(" INNER JOIN VESSEL_CALL LVSL ON E.OUT_VOY_VAR_NBR =  LVSL.VV_CD ");
				sb.append(" INNER JOIN BERTHING LBE ON LVSL.VV_CD = LBE.VV_CD AND LBE.SHIFT_IND = 1 ");
				sb.append(" LEFT JOIN UN_PORT_CODE DPC ON ED.ESN_PORT_DIS = DPC.PORT_CD ");
				sb.append(" LEFT JOIN PM4 PM4 ON E.OUT_VOY_VAR_NBR  = PM4.VAR_NBR AND E.BK_REF_NBR = PM4.BL_NBR ");
				sb.append(" WHERE E.ESN_ASN_NBR = :esnNbr ");
//				sb.append(" GROUP BY ED.ESN_HS_CODE, ED.ESN_HS_SUB_CODE, ED.CRG_DES, ED.ESN_DG_IND, PM4.IMO_CL, E.WH_IND, PM4.UCR_NBR, ");
//				sb.append(" E.ESN_ASN_NBR, E.BK_REF_NBR, E.WH_AGGR_NBR, ");
//				sb.append(" LVSL.SCHEME, LVSL.VSL_NM, LVSL.VSL_BERTH_DTTM, LBE.ETB_DTTM, LBE.ATB_DTTM, LBE.BERTH_NBR, ");
//				sb.append(" ED.ESN_OPS_IND,  LBE.COL_DTTM, DPC.PORT_NM ");
			} else {
				sb.append(
						" SELECT GE.VAR_NBR DISC_VV_CD, E.OUT_VOY_VAR_NBR LOAD_VV_CD, TESN.EDO_ASN_NBR, GE.BL_NBR, 1 EDO_ASN_PKGS, TESN.NOM_WT WEIGHT, ");
				sb.append(
						" MD.HS_CODE, (MD.HS_SUB_CODE_FR || '-' || MD.HS_SUB_CODE_TO) HS_SUB_CODE, MD.CRG_DES, DECODE(MD.DG_IND, 'Y', 'Yes', 'N', 'No') DG_IND, ");
				sb.append(" PM4.IMO_CL, DECODE(MD.STG_TYPE, 'O', 'Open', 'C', 'Covered') STG_TYPE, PM4.UCR_NBR, ");
				sb.append(
						" TESN.ESN_ASN_NBR, E.BK_REF_NBR, 1 ESN_ASN_PKGS, NULL NOM_VOL, NULL SHUTOUT_IND, GE.WH_AGGR_NBR, ");
				sb.append(
						" 'J' DISC_GATE_WAY, CONCAT(DVSL.SCHEME,CASE WHEN DVSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(DVSL.COMBI_GC_SCHEME,'-')) ELSE '' END) DISC_SCHEME, ");
				sb.append(
						" DVSL.TERMINAL DISC_TERMINAL, (NVL(DVSL.VSL_NM, '-') || '/' || NVL(DVSL.IN_VOY_NBR, '-')) DISC_VSL,DBE.ATB_DTTM DISC_ARRIVAL, ");
				sb.append(
						" DBE.BERTH_NBR DISC_BERTH, DECODE(GE.CRG_STATUS, 'L', 'Local', 'T', 'Transhipment', 'R', 'Re-export') DISC_STATUS, ");
				sb.append(
						" DECODE(GE.DIS_TYPE, 'N', 'Normal', 'D', 'Direct', 'O', 'Overside', 'L', 'Land/Reship') DISC_OPER, ");
				sb.append(
						" NULL JP_YARD_LOCATION,  DBE.COD_DTTM COMPLETETION_DISC, NULL DISC_FREE_STORE_RENT_EXPIRY, ");
				sb.append(" LPC.PORT_NM PORT_LOAD, NULL REMARKS, 'J' LOAD_GATE_WAY, CONCAT(LVSL.SCHEME, ");
				sb.append(
						" CASE WHEN LVSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(LVSL.COMBI_GC_SCHEME,'-')) ELSE '' END) LOAD_SCHEME, ");
				sb.append(
						" LVSL.TERMINAL LOAD_TERMINAL, (NVL(LVSL.VSL_NM, '-') || '/' || NVL(LVSL.OUT_VOY_NBR, '-')) LOAD_VSL, ");
				sb.append(" LBE.ATB_DTTM LOAD_ARRIVAL, LBE.BERTH_NBR LOAD_BERTH, 'JP to JP' SHIPMENT_STATUS, ");
				sb.append(
						" DECODE(TESN.LD_IND, 'N', 'Normal', 'D', 'Direct', 'O', 'Overside', 'L', 'Land/Reship') LOAD_OPER, ");
				sb.append(" LBE.COL_DTTM COMPLETETION_LOAD, NULL LOAD_FREE_STORE_RENT_EXPIRY, ");
				sb.append(" DPC.PORT_NM PORT_DISC, GE.MFT_SEQ_NBR, e.esn_status asn_status ");
				sb.append(" FROM BULK_ESN E INNER JOIN BULK_TESN_JP_JP TESN ON E.ESN_ASN_NBR = TESN.ESN_ASN_NBR ");
				sb.append(" LEFT JOIN BULK_GB_EDO GE ON TESN.EDO_ASN_NBR = GE.EDO_ASN_NBR ");
				sb.append(
						" LEFT JOIN BULK_MANIFEST_DETAILS MD ON GE.VAR_NBR = MD.VAR_NBR AND GE.MFT_SEQ_NBR = MD.MFT_SEQ_NBR ");
				sb.append(" LEFT JOIN PM4 PM4 ON GE.VAR_NBR =  PM4.VAR_NBR AND GE.BL_NBR = PM4.BL_NBR ");
				sb.append(" LEFT JOIN UN_PORT_CODE LPC ON MD.LD_PORT = LPC.PORT_CD ");
				sb.append(" LEFT JOIN UN_PORT_CODE DPC ON MD.LD_PORT = DPC.PORT_CD ");
				sb.append(" LEFT JOIN VESSEL_CALL DVSL ON E.IN_VOY_VAR_NBR =  DVSL.VV_CD ");
				sb.append(" LEFT JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append(" LEFT JOIN VESSEL_CALL LVSL ON E.OUT_VOY_VAR_NBR =  LVSL.VV_CD ");
				sb.append(" LEFT JOIN BERTHING LBE ON LVSL.VV_CD = LBE.VV_CD AND LBE.SHIFT_IND = 1 ");
				sb.append(" WHERE E.ESN_ASN_NBR = :esnNbr ");
//				sb.append(" GROUP BY TESN.EDO_ASN_NBR, GE.BL_NBR, ");
//				sb.append(" MD.HS_CODE, MD.HS_SUB_CODE, MD.CRG_DES, MD.DG_IND, PM4.IMO_CL, MD.STG_TYPE, PM4.UCR_NBR, ");
//				sb.append(" TESN.ESN_ASN_NBR, E.BK_REF_NBR, GE.WH_AGGR_NBR, ");
//				sb.append(" DVSL.SCHEME, DVSL.VSL_NM, DVSL.VSL_BERTH_DTTM, DBE.ETB_DTTM, DBE.ATB_DTTM, DBE.BERTH_NBR, ");
//				sb.append(" GE.CRG_STATUS, GE.DIS_TYPE, DBE.COD_DTTM, LPC.PORT_NM, ");
//				sb.append(" LVSL.SCHEME, LVSL.VSL_NM, LVSL.VSL_BERTH_DTTM, LBE.ETB_DTTM, LBE.ATB_DTTM, LBE.BERTH_NBR, TESN.LD_IND, LBE.COL_DTTM, DPC.PORT_NM ");
			}
			log.info("SQL" + sb.toString() + "params:" + params.toString());
			record = (CargoEnquiryDetails) namedParameterJdbcTemplate.queryForObject(sb.toString(), params,
					new BeanPropertyRowMapper<CargoEnquiryDetails>(CargoEnquiryDetails.class));
			if (record != null) {
				if ("GB_EDO".equalsIgnoreCase(type)) {
					sb.setLength(0);
					sb.append(" SELECT NVL(MIN(GET.LAST_MODIFY_DTTM), GE.LAST_MODIFY_DTTM) ");
					sb.append(" FROM GB_EDO GE ");
					sb.append(" LEFT JOIN GB_EDO_TRANS GET ON GE.EDO_ASN_NBR = GET.EDO_ASN_NBR ");
					sb.append(" WHERE GE.EDO_ASN_NBR = :edoNbr ");
					sb.append(" GROUP BY GE.EDO_ASN_NBR, GE.LAST_MODIFY_DTTM ");
				} else if ("BULK_GB_EDO".equalsIgnoreCase(type)) {
					sb.setLength(0);
					sb.append(" SELECT NVL(MIN(GET.LAST_MODIFY_DTTM), GE.LAST_MODIFY_DTTM) ");
					sb.append(" FROM BULK_GB_EDO GE ");
					sb.append(" LEFT JOIN BULK_GB_EDO_TRANS GET ON GE.EDO_ASN_NBR = GET.EDO_ASN_NBR ");
					sb.append(" WHERE GE.EDO_ASN_NBR = :edoNbr ");
					sb.append(" GROUP BY GE.EDO_ASN_NBR, GE.LAST_MODIFY_DTTM ");
				} else if ("BULK_ESN".equalsIgnoreCase(type) || "BULK_TESN_JP_JP".equalsIgnoreCase(type)) {
					sb.setLength(0);
					sb.append(" SELECT NVL(MIN(ET.LAST_MODIFY_DTTM), E.LAST_MODIFY_DTTM) ");
					sb.append(" FROM BULK_ESN E ");
					sb.append(" LEFT JOIN BULK_ESN_TRANS ET ON E.ESN_ASN_NBR = ET.ESN_ASN_NBR ");
					sb.append(" WHERE E.ESN_ASN_NBR = :esnNbr ");
					sb.append(" GROUP BY E.ESN_ASN_NBR, E.LAST_MODIFY_DTTM ");
				} else {
					sb.setLength(0);
					sb.append(" SELECT NVL(MIN(ET.LAST_MODIFY_DTTM), E.LAST_MODIFY_DTTM) ");
					sb.append(" FROM ESN E ");
					sb.append(" LEFT JOIN ESN_TRANS ET ON E.ESN_ASN_NBR = ET.ESN_ASN_NBR ");
					sb.append(" WHERE E.ESN_ASN_NBR = :esnNbr ");
					sb.append(" GROUP BY E.ESN_ASN_NBR, E.LAST_MODIFY_DTTM ");
				}
				log.info("SQL" + sb.toString() + "params:" + params.toString());

				String creationDttm = namedParameterJdbcTemplate.queryForObject(sb.toString(), params, String.class);
				// record.setCreation_dttm(creationDttm);
				record.setCreation_dttm(CommonUtil.toDate(creationDttm, "yyyy-MM-dd HH:mm:ss"));
				record.setDnList(listDnUaRecordsByAsnNbr(edoNbr, esnNbr, true));
				record.setUaList(listDnUaRecordsByAsnNbr(edoNbr, esnNbr, false));
				if (record.getUaList() != null && !record.getUaList().isEmpty()) {
					sb.setLength(0);
					sb.append(" SELECT  MIN(UA.TRANS_DTTM) ");
					sb.append(" FROM UA_DETAILS UA ");
					sb.append(" WHERE UA.ESN_ASN_NBR = :esnNbr AND UA.UA_STATUS = 'A' ");
					sb.append(" GROUP BY UA.ESN_ASN_NBR ");
					log.info("SQL" + sb.toString() + "params:" + params.toString());
					String firstUa = namedParameterJdbcTemplate.queryForObject(sb.toString(), params, String.class);
					record.setFirstUa(CommonUtil.toDate(firstUa, "yyyy-MM-dd HH:mm:ss"));
				}
				List<AsnHistory> asnHistoryList = listAsnHistoryRecords(record, type);
				record.setAsnHistoryList(asnHistoryList);
				if ("ESN".equalsIgnoreCase(type)) {
					sb.setLength(0);
					sb.append(
							" SELECT  E1.ESN_ASN_NBR org_esn_nbr, NVL(ED1.NBR_PKGS,0) org_esn_pkgs,NVL(ED1.UA_NBR_PKGS, 0) org_ua_pkgs, ");
					sb.append(" (NVL(ED1.NBR_PKGS,0) - NVL(ED1.UA_NBR_PKGS, 0)) Org_balance_ua_pkgs ");
					sb.append(" FROM  ESN E ");
					sb.append(" INNER JOIN BK_DETAILS BK ON E.BK_REF_NBR  = BK.BK_REF_NBR ");
					sb.append(" LEFT JOIN ESN E1 ON E1.BK_REF_NBR  = BK.OLD_BK_REF AND E1.ESN_STATUS = 'A' ");
					sb.append(" LEFT JOIN ESN_DETAILS ED1 ON E1.ESN_ASN_NBR  = ED1.ESN_ASN_NBR ");
					sb.append(" WHERE E.ESN_ASN_NBR = :esnNbr ");
				} else if ("TESN_JP_JP".equalsIgnoreCase(type)) {
					sb.setLength(0);
					sb.append(
							" SELECT  E1.ESN_ASN_NBR org_esn_nbr, NVL(TESN.NBR_PKGS,0) org_esn_pkgs,NVL(TESN.UA_NBR_PKGS, 0) org_ua_pkgs, ");
					sb.append(" (NVL(TESN.NBR_PKGS,0) - NVL(TESN.UA_NBR_PKGS, 0)) Org_balance_ua_pkgs ");
					sb.append(" FROM  ESN E ");
					sb.append(" INNER JOIN BK_DETAILS BK ON E.BK_REF_NBR  = BK.BK_REF_NBR ");
					sb.append(" INNER JOIN ESN E1 ON E1.BK_REF_NBR  = BK.OLD_BK_REF ");
					sb.append(" INNER JOIN TESN_JP_JP TESN ON E1.ESN_ASN_NBR = TESN.ESN_ASN_NBR ");
					sb.append(" WHERE E.ESN_ASN_NBR = :esnNbr ");
				} else if ("TESN_PSA_JP".equalsIgnoreCase(type)) {
					sb.setLength(0);
					sb.append(
							" SELECT  E1.ESN_ASN_NBR org_esn_nbr, NVL(TESN.NBR_PKGS,0) org_esn_pkgs,NVL(TESN.UA_NBR_PKGS, 0) org_ua_pkgs, ");
					sb.append(" (NVL(TESN.NBR_PKGS,0) - NVL(TESN.UA_NBR_PKGS, 0)) Org_balance_ua_pkgs ");
					sb.append(" FROM  ESN E ");
					sb.append(" INNER JOIN BK_DETAILS BK ON E.BK_REF_NBR  = BK.BK_REF_NBR ");
					sb.append(" INNER JOIN ESN E1 ON E1.BK_REF_NBR  = BK.OLD_BK_REF ");
					sb.append(" INNER JOIN TESN_PSA_JP TESN ON E1.ESN_ASN_NBR = TESN.ESN_ASN_NBR ");
					sb.append(" WHERE E.ESN_ASN_NBR = :esnNbr ");
				}
//				CargoEnquiryDetails transferCargo = (CargoEnquiryDetails) namedParameterJdbcTemplate.queryForObject(sb.toString(), params, new BeanPropertyRowMapper(CargoEnquiryDetails.class));
//				if (transferCargo != null) {
//					record.setOrg_esn_nbr(transferCargo.getOrg_esn_nbr());
//					record.setOrg_esn_pkgs(transferCargo.getOrg_esn_pkgs());
//					record.setOrg_ua_pkgs(transferCargo.getOrg_ua_pkgs());
//					record.setOrg_balance_ua_pkgs(transferCargo.getOrg_balance_ua_pkgs());
//					if (transferCargo.getOrg_esn_nbr() != null) {
//						record.setOrgUAList(
//								listDnUaRecordsByAsnNbr(edoNbr, Long.parseLong((transferCargo.getOrg_esn_nbr())), false));
//					}
//				}
			}
		} catch (BusinessException e) {
			log.info("Exception END:getCargoRecord : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception END:getCargoRecord : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getCargoRecord DAO");
		}
		return record;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->listDnUaRecordsByAsnNbr()
	@Override
	public List<DNUADetail> listDnUaRecordsByAsnNbr(String edoNbr, Long esnNbr, boolean dn) throws BusinessException {
		String sql = "";
		Map<String, String> params = new HashMap<>();
		List<DNUADetail> result = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START Dao listDnUaRecordsByAsnNbr :: edoNbr: " + CommonUtility.deNull(edoNbr) + " esnNbr: "
					+ CommonUtility.deNull(String.valueOf(esnNbr)) + " dn: "
					+ CommonUtility.deNull(String.valueOf(dn)));
			if (dn) {
				sb.append(" SELECT CSI.CASH_RECEIPT_NBR, TRUCK_NBR, DN.DN_NBR, DN.NBR_PKGS, DECODE(DN.DN_STATUS, ");
				sb.append(" 'A', 'Active', 'X', 'Cancel') DN_STATUS, DN.TRANS_DTTM, DN.SST_MACHINE_NBR, ");
				sb.append(" DN.GATE_OUT_DTTM, DN.DN_CREATE_LOGIN FROM DN_DETAILS DN ");
				sb.append(" LEFT JOIN CASH_SALES_ITEM CSI ON DN.DN_NBR = CSI.REF_NBR ");
				sb.append(" WHERE DN.EDO_ASN_NBR = :asnNbr ");
				sql = sb.toString();
				params.put("asnNbr", edoNbr);
				if (esnNbr != 0) {
					sql = sql + " AND TESN_ASN_NBR = :esnNbr ";
					params.put("esnNbr", esnNbr.toString());
				}
				sql = sql + " ORDER BY DN.DN_NBR ";
			} else {
				sb.append(
						" SELECT UA.UA_NBR, UA.NBR_PKGS, DECODE(UA.UA_STATUS, 'A', 'Active', 'X', 'Cancel') UA_STATUS, ");
				sb.append(" UA.TRANS_DTTM, UA.SST_MACHINE_NBR, UA.GATE_OUT_DTTM ");
				sb.append(" FROM UA_DETAILS UA ");
				sb.append(" WHERE UA.ESN_ASN_NBR = :asnNbr ");
				sb.append(" ORDER BY UA.UA_NBR ");
				sql = sb.toString();
				params.put("asnNbr", esnNbr.toString());
			}
			log.info("listDnUaRecordsByAsnNbr SQL: " + sql + " params: " + params);
			result = namedParameterJdbcTemplate.query(sql, params,
					new BeanPropertyRowMapper<DNUADetail>(DNUADetail.class));
		} catch (NullPointerException e) {
			log.info("Exception listDnUaRecordsByAsnNbr : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception listDnUaRecordsByAsnNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("listDnUaRecordsByAsnNbr DAO END :: result: " + result.toString());
		}
		return result;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->listAsnHistoryRecords()
	public List<AsnHistory> listAsnHistoryRecords(CargoEnquiryDetails cargo, String type) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<>();
		List<AsnHistory> result = new ArrayList<>();
		try {
			log.info("START Dao listAsnHistoryRecords :: cargo: " + CommonUtility.deNull(cargo.toString()) + " type: "
					+ CommonUtility.deNull(type));
			params.put("edoNbr", cargo.getEdo_asn_nbr());
			params.put("esnNbr", cargo.getEsn_asn_nbr());
			params.put("bkRef", cargo.getBk_ref_nbr());
			params.put("blNbr", cargo.getBl_nbr());
			params.put("mftSeqNbr", cargo.getMft_seq_nbr());
			// Start #35975  - Add extra condition to show data based on disc_vv_cd - NS OCT 2023
			params.put("vv_cd", cargo.getDisc_vv_cd());
			// End #35975  - Add extra condition to show data based on disc_vv_cd - NS OCT 2023
			if ("GB_EDO".equalsIgnoreCase(type)) {
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'Manifest Create' ");
				sb.append(" WHEN BL_STATUS = 'X' THEN 'Manifest Cancel' ");
				sb.append(" ELSE 'Manifest Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM MANIFEST_DETAILS_TRANS TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE MFT_SEQ_NBR = :mftSeqNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN EDO_STATUS='X' THEN 'EDO Cancel' ELSE CASE WHEN TRANS_NBR = 0 THEN 'EDO Create' "); // added new query to fix ITSM #30701 - NS March 2023
				sb.append(" ELSE 'EDO Modify' END END EVENT, (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(EDO_ASN_NBR) EDO_ASN_NBR, '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM GB_EDO_TRANS TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD(+) ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE EDO_ASN_NBR = :edoNbr UNION ALL ");
				sb.append(" SELECT DISTINCT '1' event_code, 'EDO Cancel'  EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(EDO_ASN_NBR) EDO_ASN_NBR, '-' SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM GB_EDO TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE EDO_STATUS = 'X' AND EDO_ASN_NBR = :edoNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'DN Create' ");
				sb.append(" WHEN TB.DN_STATUS = 'X' THEN 'DN Cancel' ");
				sb.append(" ELSE 'DN Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" NVL(TO_CHAR(TB.EDO_ASN_NBR),TB.DN_NBR) EDO_ASN_NBR, ");
				sb.append(" '-' SECOND_CARRIER, TO_CHAR(TB.TESN_ASN_NBR) TESN_ASN_NBR, ");
				sb.append(" '-' IGW_DIRECTION, ");
				sb.append(" DECODE(TB.DN_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.DN_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.DN_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, ");
				sb.append(" DECODE(TB.DN_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM ");
				sb.append(" FROM DN_DETAILS DND ");
				sb.append(" LEFT OUTER JOIN DN_DETAILS_TRANS TB ON DND.DN_NBR = TB.DN_NBR ");
				sb.append(" LEFT OUTER JOIN GB_EDO EDO ON EDO.EDO_ASN_NBR = DND.EDO_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON EDO.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  DND.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON DND.DN_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE DND.EDO_ASN_NBR = :edoNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT '0' event_code, DECODE(COD.CUT_OFF_TYPE, 'A', 'Cutoff (Auction)', 'S', ");
				sb.append(" 'Cutoff (OverSide)', 'X', 'Cutoff (Ex-Vessel)')  EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" '-' SECOND_CARRIER, '-' TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" COD.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM Cut_off_details COD ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON COD.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  COD.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE EDO_ASN_NBR = :edoNbr AND TRANS_TYPE='E' ");
			} else if ("ESN".equalsIgnoreCase(type)) {
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN TRANS_NBR = 1 THEN 'Booking Create' ");
				sb.append(" WHEN TB.BK_STATUS = 'X' THEN 'Booking Cancel' ELSE 'Booking Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BK_DETAILS_TRANS TB JOIN ESN  on (TB.BK_REF_NBR = ESN.BK_REF_NBR and ");
				sb.append(" ESN.TRANS_TYPE not in  ('S' ,'B') and TB.VAR_NBR  = ESN.OUT_VOY_VAR_NBR) "); // change OUT_VOY_NBR to VAR_NBR - NS March 2023 
				sb.append(" LEFT OUTER JOIN BK_DETAILS BK ON TB.BK_REF_NBR = BK.BK_REF_NBR ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.BK_REF_NBR = :bkRef AND BK.TRANS_CRG <> 'Y' ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN TRANS_NBR = 0 THEN 'ESN Create' ");
				sb.append(" WHEN ESN_STATUS = 'X' THEN 'ESN Cancel' ELSE 'ESN Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" CASE WHEN TRANS_TYPE NOT IN ('E', 'S') THEN TO_CHAR(ESN_ASN_NBR) END TESN_ASN_NBR, ");
				sb.append(
						" DECODE(TRANS_TYPE, 'E', 'Local', 'A', 'JP-JP', 'B', 'JP-PSA', 'C', 'PSA-JP', 'S', 'ShipStore', 'R', 'Reship') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM ESN_TRANS TB ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON TB.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
//				sb.append(" WHERE ESN_ASN_NBR = :esnNbr and TRANS_NBR > 0 --AND TRANS_TYPE = :transType ");
				sb.append(" WHERE ESN_ASN_NBR = :esnNbr and TRANS_NBR > 0  ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN TRANS_NBR = 0 THEN 'ESN Create' ");
				sb.append(" ELSE 'ESN Modify' END EVENT, '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, 'Local' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM ESN_DETAILS_TRANS TB ");
				sb.append(
						" INNER JOIN ESN ESN ON TB.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND ESN_STATUS = 'A' AND TRANS_TYPE = 'E' ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'TESN J-J Create' ");
				sb.append(" ELSE 'TESN J-J Modify' END EVENT, ");
				sb.append(" (DVSL.VSL_NM || '/' || DVSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(TB.EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(TB.ESN_ASN_NBR) TESN_ASN_NBR, ");
				sb.append(" 'JP-JP' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM  ");
				sb.append(" FROM TESN_JP_JP_TRANS TB ");
				sb.append(" INNER JOIN ESN ESN ON TB.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND TRANS_TYPE = 'A' ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN GB_EDO GB ON GB.EDO_ASN_NBR = TB.EDO_ASN_NBR AND EDO_STATUS = 'A' ");
				sb.append(" LEFT JOIN VESSEL_CALL DVSL ON GB.VAR_NBR = DVSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ALL ");
				sb.append(" SELECT DISTINCT '0' EVENT_CODE,  ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'UA Create' ");
				sb.append(" WHEN TB.UA_STATUS = 'X' THEN 'UA Cancel' ");
				sb.append(" ELSE 'UA Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" NVL(TO_CHAR( UAD.ESN_ASN_NBR),UAD.UA_NBR) TESN_ASN_NBR, ");
				sb.append(" (CASE ");
				sb.append(" WHEN ESN.TRANS_TYPE ='E' THEN 'Local' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='A' THEN  'JP-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='C' THEN  'PSA-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='B' THEN  'JP-PSA' ");
				sb.append(" ELSE ' ' END) IGW_DIRECTION, ");
				sb.append(" DECODE(TB.UA_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.UA_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, DECODE(TB.UA_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM ");
				sb.append(" FROM UA_DETAILS UAD LEFT OUTER JOIN UA_DETAILS_TRANS TB ");
				sb.append(" ON UAD.UA_NBR  = TB.UA_NBR ");
				sb.append(" LEFT OUTER JOIN ESN ESN ON UAD.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  UAD.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON  UAD.UA_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE UAD.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '1' event_code, 'TESN J-J Cancel' EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(TESN.EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" (VSL1.VSL_NM || '/' || VSL1.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(TESN.ESN_ASN_NBR) TESN_ASN_NBR, ");
				sb.append(" 'JP-JP' IGW_DIRECTION, ");
				sb.append(" ESN.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM ESN ESN ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON ESN.IN_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" INNER JOIN VESSEL_CALL VSL1 ON ESN.OUT_VOY_VAR_NBR = VSL1.VV_CD ");
				sb.append(" INNER JOIN TESN_JP_JP TESN ON ESN.ESN_ASN_NBR = TESN.ESN_ASN_NBR ");
				sb.append(" LEFT JOIN GB_EDO GB ON GB.EDO_ASN_NBR = TESN.EDO_ASN_NBR AND EDO_STATUS = 'A' ");
				sb.append(" LEFT JOIN ADM_USER AU ON  ESN.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TRANS_TYPE = 'A' and ESN.ESN_STATUS='X' and ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT '0' event_code, 'Shutout EDO' EVENT, ");
				sb.append(" '-' FIRST_CARRIER, TO_CHAR(TB.EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN.ESN_ASN_NBR) ESN_ASN_NBR, ");
				sb.append(
						" DECODE(TRANS_TYPE, 'E', 'Local', 'A', 'JP-JP', 'B', 'JP-PSA', 'C', 'PSA-JP', 'S', 'ShipStore', 'R', 'Reship') IGW_DIRECTION, ");
				sb.append(" TB1.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM GB_EDO TB ");
				sb.append(" INNER JOIN ESN ESN ON ESN.ESN_ASN_NBR = TB.ESN_ASN_NBR ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" INNER JOIN GB_EDO_TRANS TB1 ON TB1.EDO_ASN_NBR = TB.EDO_ASN_NBR ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr AND TB1.TRANS_NBR = 0 ");
				sb.append(" UNION ");
				sb.append(" SELECT '0' event_code, 'Transfer Cargo' EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-'  EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN_NEW.ESN_ASN_NBR) ESN_ASN_NBR, ");
				sb.append(
						" DECODE(ESN.TRANS_TYPE, 'E', 'Local', 'A', 'JP-JP', 'B', 'JP-PSA', 'C', 'PSA-JP', 'S', 'ShipStore', 'R', 'Reship') IGW_DIRECTION, ");
				sb.append(" NEW_BK.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BK_DETAILS NEW_BK ");
				sb.append(" INNER JOIN BK_DETAILS_TRANS OLD_BK_TRANS ON OLD_BK_TRANS.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				sb.append(" INNER JOIN BK_DETAILS OLD_BK ON OLD_BK.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				sb.append(" INNER JOIN ESN ESN ON ESN.BK_REF_NBR = OLD_BK.BK_REF_NBR ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON NEW_BK.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  NEW_BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" INNER JOIN ESN ESN_NEW ON ESN_NEW.BK_REF_NBR = NEW_BK.BK_REF_NBR ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr  AND OLD_BK_TRANS.TRANS_NBR=1 ");
				sb.append(" UNION ");
				sb.append(" select '0' event_code, 'Booking Create (Transfer)'  EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" NEW_BK.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" from  BK_DETAILS NEW_BK ");
				sb.append(" INNER JOIN ESN ESN ON ESN.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				// Start added new query to fix ITSM #30701 - NS March 2023
				sb.append(" AND NEW_BK.BK_STATUS<>'X' AND NEW_BK.ESN_DECLARED<>'N' ");
				// END
				sb.append(" INNER JOIN VESSEL_CALL VSL ON NEW_BK.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  NEW_BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" select '0' event_code, 'Esn Create (Transfer)'  EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN_NEW.ESN_ASN_NBR) TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" NEW_BK.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" from  BK_DETAILS NEW_BK ");
				sb.append(" INNER JOIN ESN ESN_NEW ON ESN_NEW.BK_REF_NBR = NEW_BK.BK_REF_NBR ");
				sb.append(" INNER JOIN ESN ESN ON ESN.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON NEW_BK.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  NEW_BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT '0' event_code, 'Transfer to Vessel' EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-'  EDO_ASN_NBR,");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN.ESN_ASN_NBR) ESN_ASN_NBR, ");
				sb.append(" ('ex ASN ' || ESN_NEW.ESN_ASN_NBR) IGW_DIRECTION, ");
				sb.append(" BK.LAST_MODIFY_DTTM-0.00001, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM  ESN ESN ");
				sb.append(" INNER JOIN BK_DETAILS BK ON ESN.BK_REF_NBR = BK.BK_REF_NBR and BK.OLD_BK_REF IS NOT NULL ");
				sb.append(" LEFT JOIN ADM_USER AU ON  BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" INNER JOIN ESN ESN_NEW ON ESN_NEW.BK_REF_NBR = BK.OLD_BK_REF ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" select '0' event_code, 'Booking Create'  EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" NEW_BK.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" from  BK_DETAILS NEW_BK ");
				sb.append(" INNER JOIN ESN ESN ON ESN.BK_REF_NBR = NEW_BK.BK_REF_NBR ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON NEW_BK.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  NEW_BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr and NEW_BK.OLD_BK_REF IS NOT NULL ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' EVENT_CODE, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'ESN Create' ");
				sb.append(" WHEN ESN.ESN_STATUS = 'X' THEN 'ESN Cancel' ");
				sb.append(" ELSE 'ESN Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" CASE WHEN ESN.TRANS_TYPE NOT IN ('E', 'S') THEN TO_CHAR(ESN.ESN_ASN_NBR) ");
				sb.append(" ELSE TO_CHAR(ESN.ESN_ASN_NBR) END TESN_ASN_NBR, ");
				sb.append(
						" DECODE(ESN.TRANS_TYPE, 'E', 'Local', 'A', 'JP-JP', 'B', 'JP-PSA', 'C', 'PSA-JP', 'S', 'ShipStore', 'R', 'Reship') IGW_DIRECTION, ");
				sb.append(" ESN.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM ESN ESN ");
				sb.append(" LEFT OUTER JOIN ESN_TRANS TB ON TB.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON TB.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
//			sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr --and nvl(TRANS_NBR,0) > 0 ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" and trans_crg = 'Y' ");
				params.put("transType", "E");
			} else if ("TESN_JP_JP".equalsIgnoreCase(type)) {
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'Manifest Create' ");
				sb.append(" WHEN BL_STATUS = 'X' THEN 'Manifest Cancel' ");
				sb.append(" ELSE 'Manifest Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM MANIFEST_DETAILS_TRANS TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE MFT_SEQ_NBR = :mftSeqNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN EDO_STATUS='X' THEN 'EDO Cancel' ELSE "); // added new query to fix ITSM #30701 - NS March 2023
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'EDO Create' ");
				sb.append(" ELSE 'EDO Modify' END END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM GB_EDO_TRANS TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD(+) ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE EDO_ASN_NBR = :edoNbr ");
				sb.append(" UNION ALL ");
				sb.append(" SELECT DISTINCT '1' event_code, ");
				sb.append(" 'EDO Cancel'  EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM GB_EDO TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE EDO_STATUS = 'X' AND EDO_ASN_NBR = :edoNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'DN Create' ");
				sb.append(" WHEN TB.DN_STATUS = 'X' THEN 'DN Cancel' ");
				sb.append(" ELSE 'DN Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" NVL(TO_CHAR(TB.EDO_ASN_NBR),TB.DN_NBR) EDO_ASN_NBR, ");
				sb.append(" '-' SECOND_CARRIER, TO_CHAR(TB.TESN_ASN_NBR) TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" DECODE(TB.DN_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.DN_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.DN_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, ");
				sb.append(" DECODE(TB.DN_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM  ");
				sb.append(" FROM DN_DETAILS DND ");
				sb.append(" LEFT OUTER JOIN DN_DETAILS_TRANS TB ON DND.DN_NBR = TB.DN_NBR ");
				sb.append(" LEFT OUTER JOIN GB_EDO EDO ON EDO.EDO_ASN_NBR = DND.EDO_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON EDO.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  DND.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON DND.DN_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE DND.EDO_ASN_NBR = :edoNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 1 THEN 'Booking Create' ");
				sb.append(" WHEN TB.BK_STATUS = 'X' THEN 'Booking Cancel' ");
				sb.append(" ELSE 'Booking Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BK_DETAILS_TRANS TB JOIN ESN  on (TB.BK_REF_NBR = ESN.BK_REF_NBR and ");
				sb.append(" ESN.TRANS_TYPE not in  ('S' ,'B') and TB.VAR_NBR = ESN.OUT_VOY_VAR_NBR) "); // change OUT_VOY_NBR to VAR_NBR - NS March 2023 
				sb.append(" LEFT OUTER JOIN BK_DETAILS BK ON TB.BK_REF_NBR = BK.BK_REF_NBR ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.BK_REF_NBR = :bkRef AND BK.TRANS_CRG <> 'Y' ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'TESN J-J Create' ");
				sb.append(" ELSE 'TESN J-J Modify' END EVENT, ");
				sb.append(" (DVSL.VSL_NM || '/' || DVSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(TB.EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(TB.ESN_ASN_NBR) TESN_ASN_NBR, 'JP-JP' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM TESN_JP_JP_TRANS TB ");
				sb.append(" INNER JOIN ESN ESN ON TB.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND TRANS_TYPE = 'A' ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN GB_EDO GB ON GB.EDO_ASN_NBR = TB.EDO_ASN_NBR AND EDO_STATUS = 'A' ");
				sb.append(" LEFT JOIN VESSEL_CALL DVSL ON GB.VAR_NBR = DVSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ALL ");
				sb.append(" SELECT DISTINCT '1' event_code, 'TESN J-J Cancel' EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(TESN.EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" (VSL1.VSL_NM || '/' || VSL1.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(TESN.ESN_ASN_NBR) TESN_ASN_NBR, ");
				sb.append(" 'JP-JP' IGW_DIRECTION, ");
				sb.append(" ESN.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM ESN ESN ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON ESN.IN_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" INNER JOIN VESSEL_CALL VSL1 ON ESN.OUT_VOY_VAR_NBR = VSL1.VV_CD ");
				sb.append(" INNER JOIN TESN_JP_JP TESN ON ESN.ESN_ASN_NBR = TESN.ESN_ASN_NBR ");
				sb.append(" LEFT JOIN GB_EDO GB ON GB.EDO_ASN_NBR = TESN.EDO_ASN_NBR AND EDO_STATUS = 'A' ");
				sb.append(" LEFT JOIN ADM_USER AU ON  ESN.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TRANS_TYPE = 'A' and ESN.ESN_STATUS='X' and ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' EVENT_CODE, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'UA Create' ");
				sb.append(" WHEN TB.UA_STATUS = 'X' THEN 'UA Cancel' ");
				sb.append(" ELSE 'UA Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" NVL(TO_CHAR( UAD.ESN_ASN_NBR),UAD.UA_NBR) TESN_ASN_NBR, ");
				sb.append(" (CASE WHEN ESN.TRANS_TYPE ='E' THEN 'Local' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='A' THEN  'JP-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='C' THEN  'PSA-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='B' THEN  'JP-PSA' ");
				sb.append(" ELSE ' ' END) IGW_DIRECTION, ");
				sb.append(" DECODE(TB.UA_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.UA_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM ");
				sb.append(" FROM UA_DETAILS UAD LEFT OUTER JOIN UA_DETAILS_TRANS TB ");
				sb.append(" ON UAD.UA_NBR  = TB.UA_NBR ");
				sb.append(" LEFT OUTER JOIN ESN ESN ON UAD.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  UAD.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON  UAD.UA_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE UAD.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT '0' event_code, 'Shutout EDO' EVENT, ");
				sb.append(" '-' FIRST_CARRIER, ");
				sb.append(" TO_CHAR(TB.EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN.ESN_ASN_NBR) ESN_ASN_NBR, ");
				sb.append(
						" DECODE(TRANS_TYPE, 'E', 'Local', 'A', 'JP-JP', 'B', 'JP-PSA', 'C', 'PSA-JP', 'S', 'ShipStore', 'R', 'Reship') IGW_DIRECTION, ");
				sb.append(" TB1.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM GB_EDO TB ");
				sb.append(" INNER JOIN ESN ESN ON ESN.ESN_ASN_NBR = TB.ESN_ASN_NBR ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" INNER JOIN GB_EDO_TRANS TB1 ON TB1.EDO_ASN_NBR = TB.EDO_ASN_NBR ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr AND TB1.TRANS_NBR = 0 ");
				sb.append(" UNION ");
				sb.append(" SELECT '0' event_code, 'Transfer Cargo' EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-'  EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN_NEW.ESN_ASN_NBR) ESN_ASN_NBR, ");
				sb.append(
						" DECODE(ESN.TRANS_TYPE, 'E', 'Local', 'A', 'JP-JP', 'B', 'JP-PSA', 'C', 'PSA-JP', 'S', 'ShipStore', 'R', 'Reship') IGW_DIRECTION, ");
				sb.append(" NEW_BK.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BK_DETAILS NEW_BK ");
				sb.append(" INNER JOIN BK_DETAILS_TRANS OLD_BK_TRANS ON OLD_BK_TRANS.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				sb.append(" INNER JOIN BK_DETAILS OLD_BK ON OLD_BK.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				sb.append(" INNER JOIN ESN ESN ON ESN.BK_REF_NBR = OLD_BK.BK_REF_NBR ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON NEW_BK.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  NEW_BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" INNER JOIN ESN ESN_NEW ON ESN_NEW.BK_REF_NBR = NEW_BK.BK_REF_NBR ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr  AND OLD_BK_TRANS.TRANS_NBR=1 ");
				sb.append(" UNION ");
				sb.append(" select '0' event_code, 'Booking Create (Transfer)'  EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" NEW_BK.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" from  BK_DETAILS NEW_BK ");
				sb.append(" INNER JOIN ESN ESN ON ESN.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				// Start added new query to fix ITSM #30701 - NS March 2023
				sb.append(" AND NEW_BK.BK_STATUS<>'X' AND NEW_BK.ESN_DECLARED<>'N' ");
				// END
				sb.append(" INNER JOIN VESSEL_CALL VSL ON NEW_BK.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  NEW_BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" select '0' event_code, 'Esn Create (Transfer)'  EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN_NEW.ESN_ASN_NBR) TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" NEW_BK.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" from  BK_DETAILS NEW_BK ");
				sb.append(" INNER JOIN ESN ESN_NEW ON ESN_NEW.BK_REF_NBR = NEW_BK.BK_REF_NBR ");
				sb.append(" INNER JOIN ESN ESN ON ESN.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON NEW_BK.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  NEW_BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr ");
				params.put("transType", "A");
			} else if ("TESN_JP_PSA".equalsIgnoreCase(type)) {
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'Manifest Create' ");
				sb.append(" WHEN BL_STATUS = 'X' THEN 'Manifest Cancel' ");
				sb.append(" ELSE 'Manifest Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM MANIFEST_DETAILS_TRANS TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE MFT_SEQ_NBR = :mftSeqNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'Manifest Create' ");
				sb.append(" WHEN BL_STATUS = 'X' THEN 'Manifest Cancel' ");
				sb.append(" ELSE 'Manifest Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM MANIFEST_DETAILS_TRANS TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE MFT_SEQ_NBR = :mftSeqNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN EDO_STATUS='X' THEN 'EDO Cancel' ELSE "); // added new query to fix ITSM #30701 - NS March 2023
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'EDO Create' ");
				sb.append(" ELSE 'EDO Modify' END END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM GB_EDO_TRANS TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD(+) ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE EDO_ASN_NBR = :edoNbr ");
				sb.append(" UNION ALL ");
				sb.append(" SELECT DISTINCT '1' event_code, ");
				sb.append(" 'EDO Cancel'  EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM GB_EDO TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE EDO_STATUS = 'X' AND EDO_ASN_NBR = :edoNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'DN Create' ");
				sb.append(" WHEN TB.DN_STATUS = 'X' THEN 'DN Cancel' ");
				sb.append(" ELSE 'DN Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" NVL(TO_CHAR(TB.EDO_ASN_NBR),TB.DN_NBR) EDO_ASN_NBR, ");
				sb.append(" '-' SECOND_CARRIER, TO_CHAR(TB.TESN_ASN_NBR) TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" DECODE(TB.DN_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.DN_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.DN_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, ");
				sb.append(" DECODE(TB.DN_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM  ");
				sb.append(" FROM DN_DETAILS DND ");
				sb.append(" LEFT OUTER JOIN DN_DETAILS_TRANS TB ON DND.DN_NBR = TB.DN_NBR ");
				sb.append(" LEFT OUTER JOIN GB_EDO EDO ON EDO.EDO_ASN_NBR = DND.EDO_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON EDO.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  DND.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON DND.DN_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE DND.EDO_ASN_NBR = :edoNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 1 THEN 'Booking Create' ");
				sb.append(" WHEN TB.BK_STATUS = 'X' THEN 'Booking Cancel' ");
				sb.append(" ELSE 'Booking Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BK_DETAILS_TRANS TB JOIN ESN  on (TB.BK_REF_NBR = ESN.BK_REF_NBR and ");
				sb.append(" ESN.TRANS_TYPE not in  ('S' ,'B') and TB.VAR_NBR = ESN.OUT_VOY_VAR_NBR) "); // change OUT_VOY_NBR to VAR_NBR - NS March 2023 
				sb.append(" LEFT OUTER JOIN BK_DETAILS BK ON TB.BK_REF_NBR = BK.BK_REF_NBR ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.BK_REF_NBR = :bkRef AND BK.TRANS_CRG <> 'Y' ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'TESN J-P Create' ");
				sb.append(" ELSE 'TESN J-P Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(TB.EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" (TB.SECOND_CAR_VES_NM || '/' || TB.SECOND_CAR_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(TB.ESN_ASN_NBR) TESN_ASN_NBR, 'JP-PSA' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM TESN_JP_PSA_TRANS TB ");
				sb.append(" INNER JOIN ESN ESN ON TB.ESN_ASN_NBR = ESN.ESN_ASN_NBR  AND TRANS_TYPE = 'B' ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON ESN.IN_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr UNION ALL ");
				sb.append(" SELECT DISTINCT '1' event_code, ");
				sb.append(" 'TESN J-P Cancel'  EVENT, (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(TESN.EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" (TESN.SECOND_CAR_VES_NM || '/' || TESN.SECOND_CAR_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(TESN.ESN_ASN_NBR) TESN_ASN_NBR, 'JP-PSA' IGW_DIRECTION, ");
				sb.append(" ESN_TRANS.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM ESN ESN INNER JOIN ESN_TRANS ESN_TRANS ");
				sb.append(" ON ESN_TRANS.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND ESN_TRANS.esn_status = 'X' ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON ESN.IN_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" INNER JOIN TESN_JP_PSA TESN ON ESN.ESN_ASN_NBR = TESN.ESN_ASN_NBR ");
				sb.append(" LEFT JOIN ADM_USER AU ON  ESN.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE ESN.TRANS_TYPE = 'B' and ESN.esn_status='X' and ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' EVENT_CODE, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'UA Create' ");
				sb.append(" WHEN TB.UA_STATUS = 'X' THEN 'UA Cancel' ");
				sb.append(" ELSE 'UA Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" NVL(TO_CHAR( UAD.ESN_ASN_NBR),UAD.UA_NBR) TESN_ASN_NBR, ");
				sb.append(" (CASE WHEN ESN.TRANS_TYPE ='E' THEN 'Local' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='A' THEN  'JP-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='C' THEN  'PSA-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='B' THEN  'JP-PSA' ");
				sb.append(" ELSE ' ' END) IGW_DIRECTION, ");
				sb.append(" DECODE(TB.UA_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.UA_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM ");
				sb.append(" FROM UA_DETAILS UAD LEFT OUTER JOIN UA_DETAILS_TRANS TB ");
				sb.append(" ON UAD.UA_NBR  = TB.UA_NBR ");
				sb.append(" LEFT OUTER JOIN ESN ESN ON UAD.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  UAD.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON  UAD.UA_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE UAD.ESN_ASN_NBR = :esnNbr ");
				params.put("transType", "B");
			} else if ("TESN_PSA_JP".equalsIgnoreCase(type)) {
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 1 THEN 'Booking Create' ");
				sb.append(" WHEN TB.BK_STATUS = 'X' THEN 'Booking Cancel' ");
				sb.append(" ELSE 'Booking Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BK_DETAILS_TRANS TB JOIN ESN  on (TB.BK_REF_NBR = ESN.BK_REF_NBR and ");
				sb.append(" ESN.TRANS_TYPE not in  ('S' ,'B') and TB.VAR_NBR = ESN.OUT_VOY_VAR_NBR) "); // change OUT_VOY_NBR to VAR_NBR - NS March 2023 
				sb.append(" LEFT OUTER JOIN BK_DETAILS BK ON TB.BK_REF_NBR = BK.BK_REF_NBR ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.BK_REF_NBR = :bkRef AND BK.TRANS_CRG <> 'Y' ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'TESN P-J Create' ");
				sb.append(" ELSE 'TESN P-J Modify' END EVENT, ");
				sb.append(" (FIRST_CAR_VES_NM || '/' || FIRST_CAR_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(TB.ESN_ASN_NBR) TESN_ASN_NBR, ");
				sb.append(" 'PSA-JP' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM TESN_PSA_JP_TRANS TB ");
				sb.append(" INNER JOIN ESN ESN ON TB.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND TRANS_TYPE = 'C' ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr UNION ");
				sb.append(" SELECT DISTINCT '1' event_code,'TESN P-J Cancel' EVENT, ");
				sb.append(" (TESN.FIRST_CAR_VES_NM || '/' || TESN.FIRST_CAR_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN.ESN_ASN_NBR) TESN_ASN_NBR, 'PSA-JP' IGW_DIRECTION, ");
				sb.append(" ESN.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM ESN ESN INNER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" INNER JOIN TESN_PSA_JP TESN ON ESN.ESN_ASN_NBR = TESN.ESN_ASN_NBR ");
				sb.append(" LEFT JOIN ADM_USER AU ON  ESN.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TRANS_TYPE = 'C' AND ESN.ESN_STATUS='X' AND ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' EVENT_CODE, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'UA Create' ");
				sb.append(" WHEN TB.UA_STATUS = 'X' THEN 'UA Cancel' ");
				sb.append(" ELSE 'UA Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" NVL(TO_CHAR( UAD.ESN_ASN_NBR),UAD.UA_NBR) TESN_ASN_NBR, ");
				sb.append(" (CASE WHEN ESN.TRANS_TYPE ='E' THEN 'Local' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='A' THEN  'JP-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='C' THEN  'PSA-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='B' THEN  'JP-PSA' ");
				sb.append(" ELSE ' ' END) IGW_DIRECTION, ");
				sb.append(" DECODE(TB.UA_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.UA_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM ");
				sb.append(" FROM UA_DETAILS UAD LEFT OUTER JOIN UA_DETAILS_TRANS TB ");
				sb.append(" ON UAD.UA_NBR  = TB.UA_NBR ");
				sb.append(" LEFT OUTER JOIN ESN ESN ON UAD.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  UAD.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON  UAD.UA_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE UAD.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT '0' event_code, 'Shutout EDO' EVENT, ");
				sb.append(" '-' FIRST_CARRIER, ");
				sb.append(" TO_CHAR(TB.EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN.ESN_ASN_NBR) ESN_ASN_NBR, ");
				sb.append(
						" DECODE(TRANS_TYPE, 'E', 'Local', 'A', 'JP-JP', 'B', 'JP-PSA', 'C', 'PSA-JP', 'S', 'ShipStore', 'R', 'Reship') IGW_DIRECTION, ");
				sb.append(" TB1.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM GB_EDO TB ");
				sb.append(" INNER JOIN ESN ESN ON ESN.ESN_ASN_NBR = TB.ESN_ASN_NBR ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" INNER JOIN GB_EDO_TRANS TB1 ON TB1.EDO_ASN_NBR = TB.EDO_ASN_NBR ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr AND TB1.TRANS_NBR = 0 ");
				sb.append(" UNION ");
				sb.append(" SELECT '0' event_code, 'Transfer Cargo' EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-'  EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN_NEW.ESN_ASN_NBR) ESN_ASN_NBR, ");
				sb.append(
						" DECODE(ESN.TRANS_TYPE, 'E', 'Local', 'A', 'JP-JP', 'B', 'JP-PSA', 'C', 'PSA-JP', 'S', 'ShipStore', 'R', 'Reship') IGW_DIRECTION, ");
				sb.append(" NEW_BK.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BK_DETAILS NEW_BK ");
				sb.append(" INNER JOIN BK_DETAILS_TRANS OLD_BK_TRANS ON OLD_BK_TRANS.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				sb.append(" INNER JOIN BK_DETAILS OLD_BK ON OLD_BK.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				sb.append(" INNER JOIN ESN ESN ON ESN.BK_REF_NBR = OLD_BK.BK_REF_NBR ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON NEW_BK.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  NEW_BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" INNER JOIN ESN ESN_NEW ON ESN_NEW.BK_REF_NBR = NEW_BK.BK_REF_NBR ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr  AND OLD_BK_TRANS.TRANS_NBR=1 ");
				sb.append(" UNION ");
				sb.append(" select '0' event_code, 'Booking Create (Transfer)'  EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" NEW_BK.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" from  BK_DETAILS NEW_BK ");
				sb.append(" INNER JOIN ESN ESN ON ESN.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				// Start added new query to fix ITSM #30701 - NS March 2023
				sb.append(" AND NEW_BK.BK_STATUS<>'X' AND NEW_BK.ESN_DECLARED<>'N' ");
				// END
				sb.append(" INNER JOIN VESSEL_CALL VSL ON NEW_BK.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  NEW_BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" select '0' event_code, 'Esn Create (Transfer)'  EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN_NEW.ESN_ASN_NBR) TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" NEW_BK.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" from  BK_DETAILS NEW_BK ");
				sb.append(" INNER JOIN ESN ESN_NEW ON ESN_NEW.BK_REF_NBR = NEW_BK.BK_REF_NBR ");
				sb.append(" INNER JOIN ESN ESN ON ESN.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON NEW_BK.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  NEW_BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr ");
				params.put("transType", "C");
			} else if ("SS_DETAILS".equalsIgnoreCase(type)) {
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 1 THEN 'Booking Create' ");
				sb.append(" WHEN TB.BK_STATUS = 'X' THEN 'Booking Cancel' ");
				sb.append(" ELSE 'Booking Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BK_DETAILS_TRANS TB JOIN ESN  on (TB.BK_REF_NBR = ESN.BK_REF_NBR and ");
				sb.append(" ESN.TRANS_TYPE not in  ('S' ,'B') and TB.VAR_NBR = ESN.OUT_VOY_VAR_NBR) "); // change OUT_VOY_NBR to VAR_NBR - NS March 2023 
				sb.append(" LEFT OUTER JOIN BK_DETAILS BK ON TB.BK_REF_NBR = BK.BK_REF_NBR ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.BK_REF_NBR = :bkRef AND BK.TRANS_CRG <> 'Y' ");
				sb.append(" UNION ");
				sb.append(" select '0' event_code, 'Esn Create (Transfer)'  EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(ESN_NEW.ESN_ASN_NBR) TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" NEW_BK.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" from  BK_DETAILS NEW_BK ");
				sb.append(" INNER JOIN ESN ESN_NEW ON ESN_NEW.BK_REF_NBR = NEW_BK.BK_REF_NBR ");
				sb.append(" INNER JOIN ESN ESN ON ESN.BK_REF_NBR = NEW_BK.OLD_BK_REF ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON NEW_BK.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  NEW_BK.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" WHERE ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN TRANS_NBR = 1 THEN 'SS Create' ");
				sb.append(" ELSE 'SS Modify' END EVENT, '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR( TB.ESN_ASN_NBR || ' (' || SSD.SS_REF_NBR || ')') TESN_ASN_NBR, ");
				sb.append(" 'ShipStore' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM SS_DETAILS_TRANS TB JOIN SS_DETAILS SSD on TB.ESN_ASN_NBR = SSD.ESN_ASN_NBR ");
				sb.append(" LEFT JOIN ESN ESN ON TB.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND TRANS_TYPE = 'S' ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr UNION ALL ");
				sb.append(" SELECT DISTINCT '0' event_code, 'SS Create' EVENT, '-' FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR( TB.ESN_ASN_NBR) TESN_ASN_NBR, 'ShipStore' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
//			sb.append(
//					" FROM SS_DETAILS  TB --left join ss_details on (tb.esn_asn_nbr = TB.ESN_ASN_NBR and tb.trans_nbr = '1') ");
				sb.append(" FROM SS_DETAILS  TB ");
				sb.append(" LEFT JOIN ESN ESN ON TB.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND TRANS_TYPE = 'S' ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr ");
				sb.append(" AND NOT EXISTS ");
				sb.append(" ( SELECT DISTINCT '0' event_code, CASE WHEN TRANS_NBR = 1 THEN 'SS Create' ");
				sb.append(" ELSE 'SS Modify' END EVENT, '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR( TB.ESN_ASN_NBR || ' (' || SSD.SS_REF_NBR || ')') TESN_ASN_NBR, ");
				sb.append(" 'ShipStore' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM SS_DETAILS_TRANS TB JOIN SS_DETAILS SSD on TB.ESN_ASN_NBR = SSD.ESN_ASN_NBR ");
				sb.append(" LEFT JOIN ESN ESN ON TB.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND TRANS_TYPE = 'S' ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr ) ");
				sb.append(" UNION SELECT '0' event_code, 'SS Cancel' EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR( ESN.ESN_ASN_NBR || ' (' || ss_details.ss_ref_nbr || ')') TESN_ASN_NBR, ");
				sb.append(" 'ShipStore' IGW_DIRECTION, ");
				sb.append(" ESN.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM ESN ESN join ss_details on esn.esn_asn_nbr = ss_details.esn_asn_nbr ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  ESN.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" WHERE TRANS_TYPE = 'S' AND ESN.ESN_STATUS='X' AND ESN.ESN_ASN_NBR = :esnNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' EVENT_CODE, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'UA Create' ");
				sb.append(" WHEN TB.UA_STATUS = 'X' THEN 'UA Cancel' ");
				sb.append(" ELSE 'UA Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" NVL(TO_CHAR( UAD.ESN_ASN_NBR),UAD.UA_NBR) TESN_ASN_NBR, ");
				sb.append(" (CASE WHEN ESN.TRANS_TYPE ='E' THEN 'Local' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='A' THEN  'JP-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='C' THEN  'PSA-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='B' THEN  'JP-PSA' ");
				sb.append(" ELSE ' ' END) IGW_DIRECTION, ");
				sb.append(" DECODE(TB.UA_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.UA_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM ");
				sb.append(" FROM UA_DETAILS UAD LEFT OUTER JOIN UA_DETAILS_TRANS TB ");
				sb.append(" ON UAD.UA_NBR  = TB.UA_NBR ");
				sb.append(" LEFT OUTER JOIN ESN ESN ON UAD.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  UAD.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON  UAD.UA_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE UAD.ESN_ASN_NBR = :esnNbr ");
				params.put("transType", "S");
			} else if ("BULK_GB_EDO".equalsIgnoreCase(type)) {
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN TRANS_NBR = 0 THEN 'Manifest Create' ");
				sb.append(" WHEN BL_STATUS = 'X' THEN 'Manifest Cancel' ");
				sb.append(" ELSE 'Manifest Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID  CO_NM ");
				sb.append(" FROM BULK_MANIFEST_DETAILS_TRANS TB ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE BL_NBR = :blNbr ");
				// Start #35975  - Add extra condition to show data based on disc_vv_cd - NS OCT 2023
				sb.append(" and var_nbr = :vv_cd");
				// End #35975  - Add extra condition to show data based on disc_vv_cd - NS OCT 2023
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN EDO_STATUS='X' THEN 'EDO Cancel' ELSE CASE WHEN TRANS_NBR = 0 THEN 'EDO Create' "); // added new query to fix ITSM #30701 - NS March 2023
				sb.append(" ELSE 'EDO Modify' END END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(TB.EDO_ASN_NBR) EDO_ASN_NBR, '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BULK_GB_EDO_TRANS TB INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD(+) ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE EDO_ASN_NBR = :edoNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'DN Create' ");
				sb.append(" WHEN TB.DN_STATUS = 'X' THEN 'DN Cancel' ");
				sb.append(" ELSE 'DN Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" NVL(TO_CHAR(TB.EDO_ASN_NBR),TB.DN_NBR) EDO_ASN_NBR, ");
				sb.append(" '-' SECOND_CARRIER, TO_CHAR(TB.TESN_ASN_NBR) TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" DECODE(TB.DN_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.DN_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.DN_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, ");
				sb.append(" DECODE(TB.DN_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM  ");
				sb.append(" FROM DN_DETAILS DND ");
				sb.append(" LEFT OUTER JOIN DN_DETAILS_TRANS TB ON DND.DN_NBR = TB.DN_NBR ");
				sb.append(" LEFT OUTER JOIN GB_EDO EDO ON EDO.EDO_ASN_NBR = DND.EDO_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON EDO.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  DND.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON DND.DN_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE DND.EDO_ASN_NBR = :edoNbr ");
			} else if ("BULK_ESN".equalsIgnoreCase(type)) {
				sb.append("SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'Booking Create' ");
				sb.append(" WHEN BK_STATUS = 'X' THEN 'Booking Cancel' ");
				sb.append(" ELSE 'Booking Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, ");
				sb.append(" '-' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BULK_BK_DETAILS_TRANS TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE BK_REF_NBR = :bkRef");
				sb.append(" UNION ");
				sb.append("SELECT DISTINCT '0' event_code, ");
				sb.append("CASE WHEN TRANS_NBR = 0 THEN 'ESN Create' ");
				sb.append("WHEN ESN_STATUS = 'X' THEN 'ESN Cancel' ");
				sb.append("ELSE 'ESN Modify' END EVENT, ");
				sb.append("'-' FIRST_CARRIER, ");
				sb.append("'-' EDO_ASN_NBR, ");
				sb.append("(VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append("CASE WHEN TRANS_TYPE NOT IN ('E', 'S') THEN TO_CHAR(ESN_ASN_NBR) END TESN_ASN_NBR, ");
				sb.append(
						"DECODE(TRANS_TYPE, 'E', 'Local', 'A', 'JP-JP', 'B', 'JP-PSA', 'C', 'PSA-JP', 'S', 'ShipStore', 'R', 'Reship') IGW_DIRECTION, ");
				sb.append("TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append("FROM BULK_ESN_TRANS TB ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON TB.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//			sb.append("--LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append("WHERE ESN_ASN_NBR = :esnNbr AND TRANS_TYPE = :transType");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'ESN Create' ");
				sb.append(" ELSE 'ESN Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, ");
				sb.append(" 'Local' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM ESN_DETAILS_TRANS TB ");
				sb.append(
						" INNER JOIN ESN ESN ON TB.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND ESN_STATUS = 'A' AND TRANS_TYPE = 'E' ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//			sb.append(" --LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' EVENT_CODE, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'UA Create' ");
				sb.append(" WHEN TB.UA_STATUS = 'X' THEN 'UA Cancel' ");
				sb.append(" ELSE 'UA Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" NVL(TO_CHAR( UAD.ESN_ASN_NBR),UAD.UA_NBR) TESN_ASN_NBR, ");
				sb.append(" (CASE WHEN ESN.TRANS_TYPE ='E' THEN 'Local' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='A' THEN  'JP-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='C' THEN  'PSA-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='B' THEN  'JP-PSA' ");
				sb.append(" ELSE ' ' END) IGW_DIRECTION, ");
				sb.append(" DECODE(TB.UA_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.UA_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM ");
				sb.append(" FROM UA_DETAILS UAD LEFT OUTER JOIN UA_DETAILS_TRANS TB ");
				sb.append(" ON UAD.UA_NBR  = TB.UA_NBR ");
				sb.append(" LEFT OUTER JOIN ESN ESN ON UAD.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  UAD.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON  UAD.UA_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE UAD.ESN_ASN_NBR = :esnNbr ");
				params.put("transType", "E");
			} else {
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN TRANS_NBR = 0 THEN 'Manifest Create' ");
				sb.append(" WHEN BL_STATUS = 'X' THEN 'Manifest Cancel' ");
				sb.append(" ELSE 'Manifest Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID  CO_NM ");
				sb.append(" FROM BULK_MANIFEST_DETAILS_TRANS TB ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE BL_NBR = :blNbr ");
				// Start #35975  - Add extra condition to show data based on disc_vv_cd - NS OCT 2023
				sb.append(" and var_nbr = :vv_cd");
				// End #35975  - Add extra condition to show data based on disc_vv_cd - NS OCT 2023
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN TRANS_NBR = 0 THEN 'Manifest Create' ");
				sb.append(" WHEN BL_STATUS = 'X' THEN 'Manifest Cancel' ");
				sb.append(" ELSE 'Manifest Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID  CO_NM ");
				sb.append(" FROM BULK_MANIFEST_DETAILS_TRANS TB ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE BL_NBR = :blNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, CASE WHEN EDO_STATUS='X' THEN 'EDO Cancel' ELSE CASE WHEN TRANS_NBR = 0 THEN 'EDO Create' "); // added new query to fix ITSM #30701 - NS March 2023
				sb.append(" ELSE 'EDO Modify' END END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(TB.EDO_ASN_NBR) EDO_ASN_NBR, '-' SECOND_CARRIER, '-' TESN_ASN_NBR, ");
				sb.append(" DECODE(CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', 'R', 'Re-Export') IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BULK_GB_EDO_TRANS TB INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD(+) ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE EDO_ASN_NBR = :edoNbr ");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'DN Create' ");
				sb.append(" WHEN TB.DN_STATUS = 'X' THEN 'DN Cancel' ");
				sb.append(" ELSE 'DN Modify' END EVENT, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" NVL(TO_CHAR(TB.EDO_ASN_NBR),TB.DN_NBR) EDO_ASN_NBR, ");
				sb.append(" '-' SECOND_CARRIER, TO_CHAR(TB.TESN_ASN_NBR) TESN_ASN_NBR, '-' IGW_DIRECTION, ");
				sb.append(" DECODE(TB.DN_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.DN_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.DN_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, ");
				sb.append(" DECODE(TB.DN_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM  ");
				sb.append(" FROM DN_DETAILS DND ");
				sb.append(" LEFT OUTER JOIN DN_DETAILS_TRANS TB ON DND.DN_NBR = TB.DN_NBR ");
				sb.append(" LEFT OUTER JOIN GB_EDO EDO ON EDO.EDO_ASN_NBR = DND.EDO_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON EDO.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  DND.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON DND.DN_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE DND.EDO_ASN_NBR = :edoNbr ");
				sb.append(" UNION ");
				sb.append("SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'Booking Create' ");
				sb.append(" WHEN BK_STATUS = 'X' THEN 'Booking Cancel' ");
				sb.append(" ELSE 'Booking Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, ");
				sb.append(" '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" '-' TESN_ASN_NBR, ");
				sb.append(" '-' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BULK_BK_DETAILS_TRANS TB ");
				sb.append(" INNER JOIN VESSEL_CALL VSL ON TB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append(" LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE BK_REF_NBR = :bkRef");
				sb.append(" UNION ");
				sb.append("SELECT DISTINCT '0' event_code, ");
				sb.append("CASE WHEN TRANS_NBR = 0 THEN 'ESN Create' ");
				sb.append("WHEN ESN_STATUS = 'X' THEN 'ESN Cancel' ");
				sb.append("ELSE 'ESN Modify' END EVENT, ");
				sb.append("'-' FIRST_CARRIER, ");
				sb.append("'-' EDO_ASN_NBR, ");
				sb.append("(VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append("CASE WHEN TRANS_TYPE NOT IN ('E', 'S') THEN TO_CHAR(ESN_ASN_NBR) END TESN_ASN_NBR, ");
				sb.append(
						"DECODE(TRANS_TYPE, 'E', 'Local', 'A', 'JP-JP', 'B', 'JP-PSA', 'C', 'PSA-JP', 'S', 'ShipStore', 'R', 'Reship') IGW_DIRECTION, ");
				sb.append("TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append("FROM BULK_ESN_TRANS TB ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON TB.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//				sb.append("--LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append("WHERE ESN_ASN_NBR = :esnNbr AND TRANS_TYPE = :transType");
				sb.append(" UNION ");
				sb.append("SELECT DISTINCT '0' event_code, ");
				sb.append(" CASE WHEN TRANS_NBR = 0 THEN 'TESN J-J Create' ");
				sb.append(" ELSE 'TESN J-J Modify' END EVENT, ");
				sb.append(" (DVSL.VSL_NM || '/' || DVSL.IN_VOY_NBR) FIRST_CARRIER, ");
				sb.append(" TO_CHAR(TB.EDO_ASN_NBR) EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" TO_CHAR(TB.ESN_ASN_NBR) TESN_ASN_NBR, ");
				sb.append(" 'JP-JP' IGW_DIRECTION, ");
				sb.append(" TB.LAST_MODIFY_DTTM, AU.USER_NAME LAST_MODIFY_USER_ID, AU.COMPANY_ID CO_NM ");
				sb.append(" FROM BULK_TESN_JP_JP_TRANS TB ");
				sb.append(
						" LEFT JOIN BULK_ESN ESN ON TB.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND ESN_STATUS = 'A' AND TRANS_TYPE = 'A' ");
				sb.append(" LEFT JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN BULK_GB_EDO GB ON GB.EDO_ASN_NBR = TB.EDO_ASN_NBR AND EDO_STATUS = 'A' ");
				sb.append(" LEFT JOIN VESSEL_CALL DVSL ON GB.VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT JOIN ADM_USER AU ON  TB.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
//			sb.append(" --LEFT JOIN COMPANY_CODE CC ON  CC.CO_CD = AU.COMPANY_ID ");
				sb.append(" WHERE TB.ESN_ASN_NBR = :esnNbr");
				sb.append(" UNION ");
				sb.append(" SELECT DISTINCT '0' EVENT_CODE, ");
				sb.append(" CASE WHEN NVL(TRANS_NBR,0) = 0 THEN 'UA Create' ");
				sb.append(" WHEN TB.UA_STATUS = 'X' THEN 'UA Cancel' ");
				sb.append(" ELSE 'UA Modify' END EVENT, ");
				sb.append(" '-' FIRST_CARRIER, '-' EDO_ASN_NBR, ");
				sb.append(" (VSL.VSL_NM || '/' || VSL.IN_VOY_NBR) SECOND_CARRIER, ");
				sb.append(" NVL(TO_CHAR( UAD.ESN_ASN_NBR),UAD.UA_NBR) TESN_ASN_NBR, ");
				sb.append(" (CASE WHEN ESN.TRANS_TYPE ='E' THEN 'Local' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='A' THEN  'JP-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='C' THEN  'PSA-JP' ");
				sb.append(" WHEN ESN.TRANS_TYPE ='B' THEN  'JP-PSA' ");
				sb.append(" ELSE ' ' END) IGW_DIRECTION, ");
				sb.append(" DECODE(TB.UA_STATUS ,'X',TB.LAST_MODIFY_DTTM,TB.UA_CREATE_DTTM) LAST_MODIFY_DTTM, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.USER_NAME,AU1.USER_NAME) LAST_MODIFY_USER_ID, ");
				sb.append(" DECODE(TB.UA_STATUS,'X',AU.COMPANY_ID,AU1.COMPANY_ID)  CO_NM ");
				sb.append(" FROM UA_DETAILS UAD LEFT OUTER JOIN UA_DETAILS_TRANS TB ");
				sb.append(" ON UAD.UA_NBR  = TB.UA_NBR ");
				sb.append(" LEFT OUTER JOIN ESN ESN ON UAD.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append(" LEFT OUTER JOIN VESSEL_CALL VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU ON  UAD.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append(" LEFT OUTER JOIN ADM_USER AU1 ON  UAD.UA_CREATE_LOGIN = AU1.USER_ACCT ");
				sb.append(" WHERE UAD.ESN_ASN_NBR = :esnNbr ");
				params.put("transType", "A");
			}
			sb.append(" ORDER BY LAST_MODIFY_DTTM,event_code ");
			log.info("listAsnHistoryRecords SQL: " + sb.toString());
			log.info("listAsnHistoryRecords params: " + params);
			result = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<AsnHistory>(AsnHistory.class));
		} catch (Exception e) {
			log.info("Exception listAsnHistoryRecords : ", e);
			return null;
		} finally {
			log.info("END listAsnHistoryRecords DAO. result: " + result.toString());
		}
		return result;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->getGBEventLog()
	@Override
	public List<GeneralEventLogValueObject> getGBEventLog(Date modifyDate, String vvCd, String asnNbr, String vvInd)
			throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Map<String, String> params = new HashMap<String, String>();
		List<GeneralEventLogValueObject> list = null;

		try {
			log.info("START: getGBEventLog modifyDate:" + CommonUtility.deNull(modifyDate.toString()) + "vvCd:"
					+ CommonUtility.deNull(vvCd) + "asnNbr:" + CommonUtility.deNull(asnNbr) + "vvInd: "
					+ CommonUtility.deNull(vvInd));
			if (ProcessChargeConst.DISC_VV_IND.equalsIgnoreCase(vvInd)) {
				sb.append("SELECT disc_vv_cd, load_vv_cd, vv_ind, business_type, ");
				sb.append("  scheme_cd, tariff_main_cat_cd, tariff_sub_cat_cd, mvmt, ");
				sb.append("  type, cargo_type, local_leg, disc_gateway, bl_nbr, ");
				sb.append("  edo_asn_nbr, bk_ref_nbr, esn_asn_nbr, dn_nbr, ");
				sb.append("  ua_nbr, bill_ton_bl, bill_ton_edo, bill_ton_dn, ");
				sb.append("  bill_ton_esn, bill_ton_bkg, load_ton_cs, ");
				sb.append("  shutout_ton_cs, count_unit, total_pack_edo, ");
				sb.append("  total_pack_dn, bill_acct_nbr, print_dttm, ");
				sb.append("  ref_ind, last_modify_dttm, cntr_cat, cntr_size, ");
				sb.append("  cntr_nbr, cntr_seq_nbr FROM gb_charge_event_log ");
				sb.append("  WHERE bill_ind ='N' ");
				sb.append("  AND tariff_main_cat_cd = :tariffMainCd ");
				sb.append("  AND business_type ='G' ");
				sb.append(
						"  AND TO_CHAR(last_modify_dttm, 'yyyyMMdd')= TO_CHAR(to_timestamp( :modifyDate, 'YYYY-MM-DD HH24:MI:SS.FF'), 'yyyyMMdd') ");
				sb.append("  AND disc_vv_cd = :vvCd ");
				sb.append("  AND vv_ind = :vvInd ");
				sb.append("  AND edo_asn_nbr = :edoNbr ");
				sb.append("  ORDER BY tariff_main_cat_cd, tariff_sub_cat_cd, mvmt");
				params.put("tariffMainCd", ProcessChargeConst.TARIFF_MAIN_GB_STORE_RENT);
				params.put("vvCd", vvCd);
				params.put("vvInd", vvInd);
				params.put("edoNbr", asnNbr);
				params.put("modifyDate", modifyDate.toString());
			} else {
				sb.append("SELECT disc_vv_cd, load_vv_cd, vv_ind, business_type, scheme_cd, ");
				sb.append("  tariff_main_cat_cd, tariff_sub_cat_cd, mvmt, type, cargo_type, ");
				sb.append("  local_leg, disc_gateway, bl_nbr, edo_asn_nbr, bk_ref_nbr, ");
				sb.append("  esn_asn_nbr, dn_nbr, ua_nbr, bill_ton_bl, bill_ton_edo, ");
				sb.append("  bill_ton_dn, bill_ton_esn, bill_ton_bkg, load_ton_cs, shutout_ton_cs, ");
				sb.append("  count_unit, total_pack_edo, total_pack_dn, bill_acct_nbr, ");
				sb.append("  print_dttm, ref_ind, last_modify_dttm, cntr_cat, ");
				sb.append("  cntr_size, cntr_nbr, cntr_seq_nbr ");
				sb.append("  FROM gb_charge_event_log ");
				sb.append("  WHERE bill_ind ='N' ");
				sb.append("  AND tariff_main_cat_cd = :tariffMainCd ");
				sb.append("  AND business_type ='G' ");
				sb.append(
						"  AND TO_CHAR(last_modify_dttm, 'yyyyMMdd')=TO_CHAR(to_timestamp( :modifyDate, 'YYYY-MM-DD HH24:MI:SS.FF'), 'yyyyMMdd') ");
				sb.append("  AND load_vv_cd  = :vvCd ");
				sb.append("  AND vv_ind = :vvInd ");
				sb.append("  AND esn_asn_nbr = :esnNbr ");
				sb.append("  ORDER BY tariff_main_cat_cd, tariff_sub_cat_cd, mvmt");
				params.put("tariffMainCd", ProcessChargeConst.TARIFF_MAIN_GB_STORE_RENT);
				params.put("vvCd", vvCd);
				params.put("vvInd", vvInd);
				params.put("esnNbr", asnNbr);
				params.put("modifyDate", modifyDate.toString());
			}
			log.info(" Sql getGBEventLog : " + sb.toString() + " params: " + params);
			list = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<GeneralEventLogValueObject>(GeneralEventLogValueObject.class));
			log.info("list" + list.toString());
		} catch (Exception e) {
			log.info("Exception getGBEventLog : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getGBEventLog DAO");
		}
		return list;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->getUnprocessGBVesselTxnEventLog()
	@Override
	public List<VesselTxnEventLogValueObject> getUnprocessGBVesselTxnEventLog(String vvCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<>();
		List<VesselTxnEventLogValueObject> list = null;
		try {
			log.info("START: getUnprocessGBVesselTxnEventLog vvCd:" + CommonUtility.deNull(vvCd));
			sb.append(" SELECT vv_cd, txn_dttm, bill_wharf_ind, bill_svc_charge_ind, bill_store_ind, bill_stuff_ind ");
			sb.append(" FROM gb_vessel_txn_event_log ");
			sb.append(" WHERE bill_process_ind ='N' ");
			sb.append(" AND TO_CHAR(txn_dttm, 'YYYYMMDD') < TO_CHAR(sysDate, 'YYYYMMDD') ");
			sb.append(" AND VV_CD = :vvCd ");
			sb.append(" ORDER BY vv_cd, txn_dttm ");

			params.put("vvCd", vvCd);
			log.info(" Sql END:getUnprocessGBVesselTxnEventLog : " + sb.toString() + " params: " + params);
			list = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<VesselTxnEventLogValueObject>(VesselTxnEventLogValueObject.class));
			log.info("list" + list.toString());
		} catch (Exception e) {
			log.info("Exception END:getUnprocessGBVesselTxnEventLog : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getUnprocessGBVesselTxnEventLog");
		}
		return list;
	}

	@Override
	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->listCargoRecords()
	public List<CargoEnquiryMgmtAction> listCargoRecords(Integer start, Integer limit, String sort, String dir,
			Map<String, Object> filters, Criteria criteria) throws Exception {
		List<CargoEnquiryMgmtAction> listCargoRecords = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		String sql = "";
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START: listCargoRecords start:" + CommonUtility.deNull(String.valueOf(start)) + "limit:"
					+ CommonUtility.deNull(String.valueOf(limit)) + "sort:" + CommonUtility.deNull(sort) + "dir:"
					+ CommonUtility.deNull(dir) + "filters:" + CommonUtility.deNull(filters.toString()));

			String type = (String) filters.get("type");
			String vvCd = (String) filters.get("vvCd");
			log.info("Type :" + type);
			log.info("vvCd :" + vvCd);
			if ("GC".equalsIgnoreCase(type)) {
                // Remarks: Remove join table with Vessel Declarant to show only one record for each asn-nbr 18102022
				sb.append(
						"SELECT GE.VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, GE.EDO_ASN_NBR EDO_NBR, NULL ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, GE.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append(
						"DECODE(GE.CRG_STATUS, 'T', 'Transhipment', 'L', 'Local Import', 'R', 'Re-Export') CRG_STATUS, 'GB_EDO' CRG_TYPE, ");
				sb.append(
						"NVL(GE.NBR_PKGS, 0) EDO_PKGS, NVL(GE.DN_NBR_PKGS, 0) DELIVERD_PKGS, (NVL(GE.NBR_PKGS, 0) - NVL(GE.DN_NBR_PKGS, 0)) BALANCE_PKGS, ");
				sb.append(
						"0 ESN_PKGS, 0 ESN_RCVD, 0 ESN_LOAD, GE.ADP_NM MAIN_ADP_TRK,au.company_id,(VSL.VSL_NM || '/' || VSL.IN_VOY_NBR)  DISC_VSL, '' LOAD_VSL ");
				sb.append("FROM GB_EDO GE ");
				sb.append("LEFT OUTER JOIN TESN_JP_JP T1 ON GE.EDO_ASN_NBR = T1.EDO_ASN_NBR ");
				sb.append("LEFT OUTER JOIN TESN_JP_PSA T2 ON GE.EDO_ASN_NBR     = T2.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON GE.VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN ADM_USER AU ON  GE.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("WHERE ");
				sb.append("T1.EDO_ASN_NBR IS NULL AND T2.EDO_ASN_NBR   IS NULL ");
				sb.append("AND (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append(
						"SELECT E.OUT_VOY_VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, NULL EDO_NBR, E.ESN_ASN_NBR ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, E.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append("'Local Export' CRG_STATUS, 'ESN' CRG_TYPE, ");
				sb.append("0 EDO_PKGS, 0 DELIVERD_PKGS, 0 BALANCE_PKGS, ");
				sb.append(
						"NVL(ED.NBR_PKGS, 0) ESN_PKGS, NVL(ED.UA_NBR_PKGS, 0) ESN_RCVD, (NVL(ED.NBR_PKGS, 0) - NVL(ED.UA_NBR_PKGS, 0)) ESN_LOAD, ");
				sb.append(
						"ED.TRUCKER_NM MAIN_ADP_TRK,au.company_id,'' DISC_VSL, (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) LOAD_VSL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN ESN_DETAILS ED ON E.ESN_ASN_NBR = ED.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  E.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'E' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
				sb.append("	OR ED.TRUCKER_IC IN (:f_trucker_ic) ");
				sb.append("	OR E.ESN_ASN_NBR IN (:f_sub_trucker) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR ED.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append(
						"SELECT E.OUT_VOY_VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, TESN.EDO_ASN_NBR EDO_NBR, TESN.ESN_ASN_NBR ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, E.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append("'Transhipment' CRG_STATUS, 'TESN_JP_JP' CRG_TYPE, ");
				sb.append(
						"NVL(TESN.NBR_PKGS, 0) EDO_PKGS, NVL(TESN.DN_NBR_PKGS, 0) DELIVERD_PKGS, (NVL(TESN.NBR_PKGS, 0) - NVL(TESN.DN_NBR_PKGS, 0)) BALANCE_PKGS, ");
				sb.append(
						"NVL(TESN.NBR_PKGS, 0) ESN_PKGS, NVL(TESN.UA_NBR_PKGS, 0) ESN_RCVD, (NVL(TESN.NBR_PKGS, 0) - NVL(TESN.UA_NBR_PKGS, 0)) ESN_LOAD, ");
				sb.append(
						"GE.ADP_NM MAIN_ADP_TRK,au.company_id,(DVSL.VSL_NM || '/' || DVSL.IN_VOY_NBR) DISC_VSL, (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) LOAD_VSL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_JP_JP TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN GB_EDO GE ON TESN.EDO_ASN_NBR = GE.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN VESSEL_CALL DVSL ON GE.VAR_NBR = DVSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  E.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'A' ");
				sb.append("AND ( ");
				sb.append("  ( ");
				sb.append("  (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND DISC_FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
				// sb.append(" ----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append("  OR ");
				sb.append("  ( ");
				sb.append("  (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
				// sb.append(" ----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
//					sb.append("	--OR ED.TRUCKER_IC IN (:f_trucker_ic) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR TESN.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append(
						"SELECT E.OUT_VOY_VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, TESN.EDO_ASN_NBR EDO_NBR, TESN.ESN_ASN_NBR ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, E.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append("'Transhipment' CRG_STATUS, 'TESN_JP_PSA' CRG_TYPE, ");
				sb.append(
						"NVL(TESN.NBR_PKGS, 0) EDO_PKGS, NVL(TESN.DN_NBR_PKGS, 0) DELIVERD_PKGS, (NVL(TESN.NBR_PKGS, 0) - NVL(TESN.DN_NBR_PKGS, 0)) BALANCE_PKGS, ");
				sb.append(
						"0 ESN_PKGS, 0 ESN_RCVD, 0 ESN_LOAD, GE.ADP_NM MAIN_ADP_TRK,au.company_id,(VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) DISC_VSL, '' LOAD_VSL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_JP_PSA TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN GB_EDO GE ON TESN.EDO_ASN_NBR = GE.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.IN_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN VESSEL_CALL DVSL ON GE.VAR_NBR = DVSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  E.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'B' ");
				sb.append("AND ( ");
				sb.append("  ( ");
				sb.append("  (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND DISC_FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
				// sb.append(" ----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append("  OR ");
				sb.append("  ( ");
				sb.append("  (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (E.IN_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
				// sb.append(" ----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
//					sb.append("	--OR ED.TRUCKER_IC IN (:f_trucker_ic) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR TESN.SEC_ACCT_NBR in (:f_acc_nbr) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append(
						"SELECT E.OUT_VOY_VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, NULL EDO_NBR, TESN.ESN_ASN_NBR ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, E.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append("'Transhipment' CRG_STATUS, 'TESN_PSA_JP' CRG_TYPE, ");
				sb.append(
						"NVL(TESN.NBR_PKGS, 0) EDO_PKGS, NVL(TESN.DN_NBR_PKGS, 0) DELIVERD_PKGS, (NVL(TESN.NBR_PKGS, 0) - NVL(TESN.DN_NBR_PKGS, 0)) BALANCE_PKGS, ");
				sb.append(
						"NVL(TESN.NBR_PKGS, 0) ESN_PKGS, NVL(TESN.UA_NBR_PKGS, 0) ESN_RCVD, (NVL(TESN.NBR_PKGS, 0) - NVL(TESN.UA_NBR_PKGS, 0)) ESN_LOAD, ");
				sb.append(
						"TESN.TRUCKER_NM MAIN_ADP_TRK,au.company_id,'' DISC_VSL, (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) LOAD_VSL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_PSA_JP TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  E.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'C' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
				// sb.append(" ----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
				sb.append("	OR TESN.TRUCKER_IC IN (:f_trucker_ic) ");
				sb.append("	OR E.ESN_ASN_NBR IN (:f_sub_trucker) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR TESN.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append(
						"SELECT E.OUT_VOY_VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, NULL EDO_NBR, E.ESN_ASN_NBR ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, SS.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append("'Ship Store' CRG_STATUS, 'SS_DETAILS' CRG_TYPE, ");
				sb.append("0 EDO_PKGS, 0 DELIVERD_PKGS, 0 BALANCE_PKGS, ");
				sb.append(
						"NVL(SS.NBR_PKGS, 0) ESN_PKGS, NVL(SS.UA_NBR_PKGS, 0) ESN_RCVD, (NVL(SS.NBR_PKGS, 0) - NVL(SS.UA_NBR_PKGS, 0)) ESN_LOAD, ");
				sb.append(
						"SHIPPER_NM MAIN_ADP_TRK,au.company_id,'' DISC_VSL, (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) LOAD_VSL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN SS_DETAILS SS ON SS.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  SS.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'S' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
				// sb.append(" ----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
				sb.append("	OR SS.SHIPPER_CR_NBR IN (:f_trucker_ic) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR SS.ACCT_NBR in (:f_acc_nbr) ");
				sb.append(")");
			} else if ("BC".equalsIgnoreCase(type)) {
				sb.append("SELECT ");
				sb.append("    BGE.VAR_NBR       VV_CD, ");
				sb.append("    VSL.VSL_NM, ");
				sb.append("    VSL.IN_VOY_NBR, ");
				sb.append("    VSL.OUT_VOY_NBR, ");
				sb.append("    VSL.TERMINAL, ");
				sb.append("    CONCAT(VSL.SCHEME, ");
				sb.append("           CASE ");
				sb.append("               WHEN VSL.COMBI_GC_OPS_IND = 'Y' THEN ");
				sb.append("                   ' / ' ");
				sb.append("                   ||(NVL(VSL.COMBI_GC_SCHEME, '-')) ");
				sb.append("               ELSE ");
				sb.append("                   '' ");
				sb.append("           END ");
				sb.append("    ) AS SCHEME, ");
				sb.append("    BGE.EDO_ASN_NBR   EDO_NBR, ");
				sb.append("    NULL ESN_NBR, ");
				sb.append("    AU.USER_NAME      LAST_MODIFY_USER_ID, ");
				sb.append("    BGE.LAST_MODIFY_DTTM, ");
				sb.append("    'Bulk Cargo' TYPE, ");
				sb.append("    CC.CO_NM          SHIPPING_AGENT, ");
				sb.append("    DECODE(BGE.CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', ");
				sb.append("           'R', 'Re-Export'), ");
				sb.append("    'BULK_GB_EDO' CRG_TYPE, ");
				sb.append("    0 EDO_PKGS, ");
				sb.append("    0 DELIVERD_PKGS, ");
				sb.append("    0 BALANCE_PKGS, ");
				sb.append("    0 ESN_PKGS, ");
				sb.append("    0 ESN_RCVD, ");
				sb.append("    0 ESN_LOAD, ");
				sb.append("    BGE.ADP_NM        MAIN_ADP_TRK, ");
				sb.append("    AU.COMPANY_ID, ");
				sb.append("    ( VSL.VSL_NM ");
				sb.append("      || '/' ");
				sb.append("      || VSL.IN_VOY_NBR ) DISC_VSL, ");
				sb.append("    '' LOAD_VSL ");
				sb.append("FROM ");
				sb.append("    BULK_GB_EDO             BGE ");
				sb.append("    LEFT OUTER JOIN BULK_TESN_JP_JP         BT1 ON BGE.EDO_ASN_NBR = BT1.EDO_ASN_NBR ");
				sb.append("    INNER JOIN BULK_MANIFEST_DETAILS   BMF ON BGE.VAR_NBR = BMF.VAR_NBR ");
				sb.append("                                            AND BGE.MFT_SEQ_NBR = BMF.MFT_SEQ_NBR ");
				sb.append("    INNER JOIN VESSEL_CALL             VSL ON BGE.VAR_NBR = VSL.VV_CD ");
				sb.append("    INNER JOIN BERTHING                BE ON VSL.VV_CD = BE.VV_CD ");
				sb.append("                              AND BE.SHIFT_IND = 1 ");
				sb.append("    INNER JOIN COMPANY_CODE            CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("    LEFT JOIN VESSEL_DECLARANT        VD ON BGE.VAR_NBR = VD.VV_CD ");
				sb.append("    LEFT JOIN ADM_USER                AU ON BGE.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("    BGE.EDO_STATUS = 'A' ");
				sb.append("    AND BT1.EDO_ASN_NBR IS NULL ");
				sb.append("    AND ( BGE.EDO_ASN_NBR IN ( ");
				sb.append("        :f_asn_nbr ");
				sb.append("    ) ");
				sb.append("          OR 1 = :A ) ");
				sb.append("    AND ( BGE.VAR_NBR = :f_vv_cd ");
				sb.append("          OR 1 = :V ) ");
				sb.append("    AND ( BGE.EDO_CREATE_CD = :f_co_cd ");
				sb.append("          OR 1 = :C ) ");
				sb.append("    AND ( ");
				sb.append("	 ");
				sb.append("     1 = :JP ");
				sb.append("	 ");
				sb.append("          OR BGE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("          OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("          OR BGE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("          OR BMF.MIXED_SCHEME_ACCT_NBR IN ( ");
				sb.append("        :f_acc_nbr ");
				sb.append("    ) ");
				sb.append("	 ");
				sb.append("          OR VD.CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("          OR ( VD.CUST_CD IS NULL ");
				sb.append("               AND VSL.VSL_OPR_CD = :f_cust_cd ) ");
				sb.append("	 ");
				sb.append("          OR ( VD.CUST_CD IS NULL ");
				sb.append("               AND BGE.VAR_NBR IN ( ");
				sb.append("        :f_t_vv_cd ");
				sb.append("    ) ) ) ");
				sb.append("UNION ALL ");
				sb.append("SELECT ");
				sb.append("    ESN.OUT_VOY_VAR_NBR   VV_CD, ");
				sb.append("    VSL.VSL_NM, ");
				sb.append("    VSL.IN_VOY_NBR, ");
				sb.append("    VSL.OUT_VOY_NBR, ");
				sb.append("    VSL.TERMINAL, ");
				sb.append("    CONCAT(VSL.SCHEME, ");
				sb.append("           CASE ");
				sb.append("               WHEN VSL.COMBI_GC_OPS_IND = 'Y' THEN ");
				sb.append("                   ' / ' ");
				sb.append("                   ||(NVL(VSL.COMBI_GC_SCHEME, '-')) ");
				sb.append("               ELSE ");
				sb.append("                   '' ");
				sb.append("           END ");
				sb.append("    ) AS SCHEME, ");
				sb.append("    NULL EDO_NBR, ");
				sb.append("    ESN.ESN_ASN_NBR       ESN_NBR, ");
				sb.append("    AU.USER_NAME         LAST_MODIFY_USER_ID, ");
				sb.append("    BE.LAST_MODIFY_DTTM, ");
				sb.append("    'Bulk Cargo' TYPE, ");
				sb.append("    CC.CO_NM             SHIPPING_AGENT, ");
				sb.append("    'Export' CRG_STATUS, ");
				sb.append("    'BULK_ESN' CRG_TYPE, ");
				sb.append("    0 EDO_PKGS, ");
				sb.append("    0 DELIVERD_PKGS, ");
				sb.append("    0 BALANCE_PKGS, ");
				sb.append("    0 ESN_PKGS, ");
				sb.append("    0 ESN_RCVD, ");
				sb.append("    0 ESN_LOAD, ");
				sb.append("    BED.TRUCKER_NM       MAIN_ADP_TRK, ");
				sb.append("    AU.COMPANY_ID, ");
				sb.append("    '' DISC_VSL, ");
				sb.append("    ( VSL.VSL_NM ");
				sb.append("      || '/' ");
				sb.append("      || VSL.OUT_VOY_NBR ) LOAD_VSL ");
				sb.append("FROM ");
				sb.append("    BULK_ESN           ESN ");
				sb.append("    INNER JOIN BULK_ESN_DETAILS   BED ON ESN.ESN_ASN_NBR = BED.ESN_ASN_NBR ");
				sb.append("    INNER JOIN VESSEL_CALL        VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("    INNER JOIN BERTHING           BE ON VSL.VV_CD = BE.VV_CD ");
				sb.append("                              AND BE.SHIFT_IND = 1 ");
				sb.append("    INNER JOIN COMPANY_CODE       CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("    LEFT JOIN VESSEL_DECLARANT   VD ON ESN.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("    LEFT JOIN ADM_USER           AU ON BE.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("    ESN.ESN_STATUS = 'A' ");
				sb.append("    AND TRANS_TYPE = 'E' ");
				sb.append("    AND ( ESN.ESN_ASN_NBR IN ( ");
				sb.append("        :f_asn_nbr ");
				sb.append("    ) ");
				sb.append("          OR 1 = :A ) ");
				sb.append("    AND ( ESN.OUT_VOY_VAR_NBR = :f_vv_cd ");
				sb.append("          OR 1 = :V ) ");
				sb.append("    AND ( ESN.ESN_CREATE_CD = :f_co_cd ");
				sb.append("          OR 1 = :C ) ");
				sb.append("    AND ( ");
				sb.append("	 ");
				sb.append("     1 = :JP ");
				sb.append("	 ");
				sb.append("          OR ESN.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("          OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("          OR BED.TRUCKER_IC IN ( ");
				sb.append("        :f_trucker_ic ");
				sb.append("    ) ");
				sb.append("	 ");
				sb.append("          OR BED.MIXED_SCHEME_ACCT_NBR IN ( ");
				sb.append("        :f_acc_nbr ");
				sb.append("    ) ");
				sb.append("	 ");
				sb.append("          OR VD.CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("          OR ( VD.CUST_CD IS NULL ");
				sb.append("               AND VSL.VSL_OPR_CD = :f_cust_cd ) ");
				sb.append("	 ");
				sb.append("          OR ( VD.CUST_CD IS NULL ");
				sb.append("               AND ESN.OUT_VOY_VAR_NBR IN ( ");
				sb.append("        :f_t_vv_cd ");
				sb.append("    ) ) ) ");
				sb.append("UNION ALL ");
				sb.append("SELECT ");
				sb.append("    ESN.OUT_VOY_VAR_NBR   VV_CD, ");
				sb.append("    VSL.VSL_NM, ");
				sb.append("    VSL.IN_VOY_NBR, ");
				sb.append("    VSL.OUT_VOY_NBR, ");
				sb.append("    VSL.TERMINAL, ");
				sb.append("    CONCAT(VSL.SCHEME, ");
				sb.append("           CASE ");
				sb.append("               WHEN VSL.COMBI_GC_OPS_IND = 'Y' THEN ");
				sb.append("                   ' / ' ");
				sb.append("                   ||(NVL(VSL.COMBI_GC_SCHEME, '-')) ");
				sb.append("               ELSE ");
				sb.append("                   '' ");
				sb.append("           END ");
				sb.append("    ) AS SCHEME, ");
				sb.append("    BTESN.EDO_ASN_NBR    EDO_NBR, ");
				sb.append("    ESN.ESN_ASN_NBR       ESN_NBR, ");
				sb.append("    AU.USER_NAME         LAST_MODIFY_USER_ID, ");
				sb.append("    BE.LAST_MODIFY_DTTM, ");
				sb.append("    'Bulk Cargo' TYPE, ");
				sb.append("    CC.CO_NM             SHIPPING_AGENT, ");
				sb.append("    'Transhipment' CRG_STATUS, ");
				sb.append("    'BULK_TESN_JP_JP' CRG_TYPE, ");
				sb.append("    0 EDO_PKGS, ");
				sb.append("    0 DELIVERD_PKGS, ");
				sb.append("    0 BALANCE_PKGS, ");
				sb.append("    0 ESN_PKGS, ");
				sb.append("    0 ESN_RCVD, ");
				sb.append("    0 ESN_LOAD, ");
				sb.append("    NULL MAIN_ADP_TRK, ");
				sb.append("    AU.COMPANY_ID, ");
				sb.append("    ( DVSL.VSL_NM ");
				sb.append("      || '/' ");
				sb.append("      || DVSL.IN_VOY_NBR ) DISC_VSL, ");
				sb.append("    ( VSL.VSL_NM ");
				sb.append("      || '/' ");
				sb.append("      || VSL.OUT_VOY_NBR ) LOAD_VSL ");
				sb.append("FROM ");
				sb.append("    BULK_ESN                ESN ");
				sb.append("    INNER JOIN BULK_TESN_JP_JP         BTESN ON BTESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("    INNER JOIN BULK_GB_EDO             BGE ON BTESN.EDO_ASN_NBR = BGE.EDO_ASN_NBR ");
				sb.append("    INNER JOIN BULK_MANIFEST_DETAILS   BMF ON BGE.VAR_NBR = BMF.VAR_NBR ");
				sb.append("                                            AND BGE.MFT_SEQ_NBR = BMF.MFT_SEQ_NBR ");
				sb.append("    INNER JOIN VESSEL_CALL             VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("    INNER JOIN VESSEL_CALL             DVSL ON BGE.VAR_NBR = DVSL.VV_CD ");
				sb.append("    INNER JOIN BERTHING                BE ON VSL.VV_CD = BE.VV_CD ");
				sb.append("                              AND BE.SHIFT_IND = 1 ");
				sb.append("    INNER JOIN BERTHING                DBE ON DVSL.VV_CD = DBE.VV_CD ");
				sb.append("                               AND DBE.SHIFT_IND = 1 ");
				sb.append("    INNER JOIN COMPANY_CODE            CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("    LEFT JOIN VESSEL_DECLARANT        VD ON ESN.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("    LEFT JOIN VESSEL_DECLARANT        VDG ON BGE.VAR_NBR = VDG.VV_CD ");
				sb.append("    LEFT JOIN ADM_USER                AU ON BE.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("    ESN.ESN_STATUS = 'A' ");
				sb.append("    AND ESN.TRANS_TYPE = 'A' ");
				sb.append("    AND ( ( ( BGE.EDO_ASN_NBR IN ( ");
				sb.append("        :f_asn_nbr ");
				sb.append("    ) ");
				sb.append("              OR 1 = :A ) ");
				sb.append("            AND ( BGE.VAR_NBR = :f_vv_cd ");
				sb.append("                  OR 1 = :V ) ");
				sb.append("            AND ( BGE.EDO_CREATE_CD = :f_co_cd ");
				sb.append("                  OR 1 = :C ) ");
				sb.append("            AND ( ");
				sb.append("	 ");
				sb.append("             1 = :JP ");
				sb.append("	 ");
				sb.append("                  OR BGE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("                  OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("                  OR BGE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("                  OR BMF.MIXED_SCHEME_ACCT_NBR IN ( ");
				sb.append("        :f_acc_nbr ");
				sb.append("    ) ");
				sb.append("	 ");
				sb.append("                  OR VDG.CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("                  OR ( VDG.CUST_CD IS NULL ");
				sb.append("                       AND VSL.VSL_OPR_CD = :f_cust_cd ) ");
				sb.append("	 ");
				sb.append("                  OR ( VDG.CUST_CD IS NULL ");
				sb.append("                       AND BGE.VAR_NBR IN ( ");
				sb.append("        :f_t_vv_cd ");
				sb.append("    ) ) ) ) ");
				sb.append("          OR ( ( ESN.ESN_ASN_NBR IN ( ");
				sb.append("        :f_asn_nbr ");
				sb.append("    ) ");
				sb.append("                 OR 1 = :A ) ");
				sb.append("               AND ( ESN.OUT_VOY_VAR_NBR = :f_vv_cd ");
				sb.append("                     OR 1 = :V ) ");
				sb.append("               AND ( ESN.ESN_CREATE_CD = :f_co_cd ");
				sb.append("                     OR 1 = :C ) ");
				sb.append("               AND ( ");
				sb.append("	 ");
				sb.append("                1 = :JP ");
				sb.append("	 ");
				sb.append("                     OR ESN.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("                     OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("                     OR BTESN.MIXED_SCHEME_ACCT_NBR IN ( ");
				sb.append("        :f_acc_nbr ");
				sb.append("    ) ");
				sb.append("	 ");
				sb.append("                     OR VD.CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("                     OR ( VD.CUST_CD IS NULL ");
				sb.append("                          AND VSL.VSL_OPR_CD = :f_cust_cd ) ");
				sb.append(" ");
				sb.append("                     OR ( VD.CUST_CD IS NULL ");
				sb.append("                          AND ESN.OUT_VOY_VAR_NBR IN ( ");
				sb.append("        :f_t_vv_cd ");
				sb.append("    ) ) ) ) )");

			} else {
				sb.append(
						"SELECT GE.VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, GE.EDO_ASN_NBR EDO_NBR, NULL ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, GE.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append(
						"DECODE(GE.CRG_STATUS, 'T', 'Transhipment', 'L', 'Local Import', 'R', 'Re-Export') CRG_STATUS, 'GB_EDO' CRG_TYPE, ");
				sb.append(
						"NVL(GE.NBR_PKGS, 0) EDO_PKGS, NVL(GE.DN_NBR_PKGS, 0) DELIVERD_PKGS, (NVL(GE.NBR_PKGS, 0) - NVL(GE.DN_NBR_PKGS, 0)) BALANCE_PKGS, ");
				sb.append(
						"0 ESN_PKGS, 0 ESN_RCVD, 0 ESN_LOAD, GE.ADP_NM MAIN_ADP_TRK,au.company_id,(VSL.VSL_NM || '/' || VSL.IN_VOY_NBR)  DISC_VSL, '' LOAD_VSL ");
				sb.append("FROM GB_EDO GE ");
				sb.append("LEFT OUTER JOIN TESN_JP_JP T1 ON GE.EDO_ASN_NBR = T1.EDO_ASN_NBR ");
				sb.append("LEFT OUTER JOIN TESN_JP_PSA T2 ON GE.EDO_ASN_NBR     = T2.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON GE.VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON GE.VAR_NBR = VD.VV_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  GE.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("WHERE ");
				sb.append("T1.EDO_ASN_NBR IS NULL AND T2.EDO_ASN_NBR   IS NULL ");
				sb.append("AND (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND GE.VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append(
						"SELECT E.OUT_VOY_VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, NULL EDO_NBR, E.ESN_ASN_NBR ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, E.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append("'Local Export' CRG_STATUS, 'ESN' CRG_TYPE, ");
				sb.append("0 EDO_PKGS, 0 DELIVERD_PKGS, 0 BALANCE_PKGS, ");
				sb.append(
						"NVL(ED.NBR_PKGS, 0) ESN_PKGS, NVL(ED.UA_NBR_PKGS, 0) ESN_RCVD, (NVL(ED.NBR_PKGS, 0) - NVL(ED.UA_NBR_PKGS, 0)) ESN_LOAD, ");
				sb.append(
						"ED.TRUCKER_NM MAIN_ADP_TRK,au.company_id,'' DISC_VSL, (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) LOAD_VSL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN ESN_DETAILS ED ON E.ESN_ASN_NBR = ED.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON E.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  E.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'E' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
				sb.append("	OR ED.TRUCKER_IC IN (:f_trucker_ic) ");
				sb.append("	OR E.ESN_ASN_NBR IN (:f_sub_trucker) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR ED.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				// sb.append(" ----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
				// sb.append(" ----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
				// sb.append(" ----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND E.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append(
						"SELECT E.OUT_VOY_VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, TESN.EDO_ASN_NBR EDO_NBR, TESN.ESN_ASN_NBR ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, E.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append("'Transhipment' CRG_STATUS, 'TESN_JP_JP' CRG_TYPE, ");
				sb.append(
						"NVL(TESN.NBR_PKGS, 0) EDO_PKGS, NVL(TESN.DN_NBR_PKGS, 0) DELIVERD_PKGS, (NVL(TESN.NBR_PKGS, 0) - NVL(TESN.DN_NBR_PKGS, 0)) BALANCE_PKGS, ");
				sb.append(
						"NVL(TESN.NBR_PKGS, 0) ESN_PKGS, NVL(TESN.UA_NBR_PKGS, 0) ESN_RCVD, (NVL(TESN.NBR_PKGS, 0) - NVL(TESN.UA_NBR_PKGS, 0)) ESN_LOAD, ");
				sb.append(
						"GE.ADP_NM MAIN_ADP_TRK,au.company_id,(DVSL.VSL_NM || '/' || DVSL.IN_VOY_NBR) DISC_VSL, (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) LOAD_VSL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_JP_JP TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN GB_EDO GE ON TESN.EDO_ASN_NBR = GE.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN VESSEL_CALL DVSL ON GE.VAR_NBR = DVSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON E.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VDG ON GE.VAR_NBR = VDG.VV_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  E.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'A' ");
				sb.append("AND ( ");
				sb.append("  ( ");
				sb.append("  (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND DISC_FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				// sb.append(" ----- DOC SUB AUTHOR ");
				sb.append("	OR VDG.CUST_CD = :f_cust_cd ");
				// sb.append(" ----- GB SHIPPING LINE ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
				// sb.append(" ----- TA OPERATOR ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND GE.VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append("  OR ");
				sb.append("  ( ");
				sb.append("  (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
//					sb.append("	--OR ED.TRUCKER_IC IN (:f_trucker_ic) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR TESN.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				// sb.append(" ----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
				// sb.append(" ----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
				// sb.append(" ----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND E.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append(
						"SELECT E.OUT_VOY_VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, TESN.EDO_ASN_NBR EDO_NBR, TESN.ESN_ASN_NBR ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, E.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append("'Transhipment' CRG_STATUS, 'TESN_JP_PSA' CRG_TYPE, ");
				sb.append(
						"NVL(TESN.NBR_PKGS, 0) EDO_PKGS, NVL(TESN.DN_NBR_PKGS, 0) DELIVERD_PKGS, (NVL(TESN.NBR_PKGS, 0) - NVL(TESN.DN_NBR_PKGS, 0)) BALANCE_PKGS, ");
				sb.append(
						"0 ESN_PKGS, 0 ESN_RCVD, 0 ESN_LOAD, GE.ADP_NM MAIN_ADP_TRK,au.company_id,(VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) DISC_VSL, '' LOAD_VSL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_JP_PSA TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN GB_EDO GE ON TESN.EDO_ASN_NBR = GE.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.IN_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN VESSEL_CALL DVSL ON GE.VAR_NBR = DVSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON E.IN_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VDG ON GE.VAR_NBR = VDG.VV_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  E.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'B' ");
				sb.append("AND ( ");
				sb.append("  ( ");
				sb.append("  (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND DISC_FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				// sb.append(" ----- DOC SUB AUTHOR ");
				sb.append("	OR VDG.CUST_CD = :f_cust_cd ");
				// sb.append(" ----- GB SHIPPING LINE ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
				// sb.append(" ----- TA OPERATOR ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND GE.VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append("  OR ");
				sb.append("  ( ");
				sb.append("  (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (E.IN_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
				// sb.append(" --OR ED.TRUCKER_IC IN (:f_trucker_ic) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR TESN.SEC_ACCT_NBR in (:f_acc_nbr) ");
				// sb.append(" ----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
				// sb.append(" ----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
				// sb.append(" ----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND E.IN_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append(
						"SELECT E.OUT_VOY_VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, NULL EDO_NBR, TESN.ESN_ASN_NBR ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, E.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append("'Transhipment' CRG_STATUS, 'TESN_PSA_JP' CRG_TYPE, ");
				sb.append(
						"NVL(TESN.NBR_PKGS, 0) EDO_PKGS, NVL(TESN.DN_NBR_PKGS, 0) DELIVERD_PKGS, (NVL(TESN.NBR_PKGS, 0) - NVL(TESN.DN_NBR_PKGS, 0)) BALANCE_PKGS, ");
				sb.append(
						"NVL(TESN.NBR_PKGS, 0) ESN_PKGS, NVL(TESN.UA_NBR_PKGS, 0) ESN_RCVD, (NVL(TESN.NBR_PKGS, 0) - NVL(TESN.UA_NBR_PKGS, 0)) ESN_LOAD, ");
				sb.append(
						"TESN.TRUCKER_NM MAIN_ADP_TRK,au.company_id,'' DISC_VSL, (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) LOAD_VSL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_PSA_JP TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON E.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  E.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'C' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
				sb.append("	OR TESN.TRUCKER_IC IN (:f_trucker_ic) ");
				sb.append("	OR E.ESN_ASN_NBR IN (:f_sub_trucker) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR TESN.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				// sb.append(" ----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
				// sb.append(" ----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
				// sb.append(" ----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND E.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append(
						"SELECT E.OUT_VOY_VAR_NBR VV_CD, VSL.VSL_NM, VSL.IN_VOY_NBR, VSL.OUT_VOY_NBR, VSL.TERMINAL, CONCAT(VSL.SCHEME,CASE WHEN VSL.COMBI_GC_OPS_IND ='Y' THEN  ' / ' ||(NVL(VSL.COMBI_GC_SCHEME,'-')) ELSE '' END) AS SCHEME, NULL EDO_NBR, E.ESN_ASN_NBR ESN_NBR, AU.USER_NAME LAST_MODIFY_USER_ID, SS.LAST_MODIFY_DTTM, 'General Cargo' TYPE, CC.CO_NM SHIPPING_AGENT, ");
				sb.append("'Ship Store' CRG_STATUS, 'SS_DETAILS' CRG_TYPE, ");
				sb.append("0 EDO_PKGS, 0 DELIVERD_PKGS, 0 BALANCE_PKGS, ");
				sb.append(
						"NVL(SS.NBR_PKGS, 0) ESN_PKGS, NVL(SS.UA_NBR_PKGS, 0) ESN_RCVD, (NVL(SS.NBR_PKGS, 0) - NVL(SS.UA_NBR_PKGS, 0)) ESN_LOAD, ");
				sb.append(
						"SHIPPER_NM MAIN_ADP_TRK,au.company_id,'' DISC_VSL, (VSL.VSL_NM || '/' || VSL.OUT_VOY_NBR) LOAD_VSL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN SS_DETAILS SS ON SS.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN COMPANY_CODE CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON E.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("LEFT JOIN ADM_USER AU ON  SS.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'S' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				// sb.append(" ----- ADP/TRUCKER ");
				sb.append("	OR SS.SHIPPER_CR_NBR IN (:f_trucker_ic) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR SS.ACCT_NBR in (:f_acc_nbr) ");
				// sb.append(" ----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
				// sb.append(" ----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
				// sb.append(" ----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND E.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append(")");

				sb.append(" UNION ");

				sb.append("SELECT ");
				sb.append("    BGE.VAR_NBR       VV_CD, ");
				sb.append("    VSL.VSL_NM, ");
				sb.append("    VSL.IN_VOY_NBR, ");
				sb.append("    VSL.OUT_VOY_NBR, ");
				sb.append("    VSL.TERMINAL, ");
				sb.append("    CONCAT(VSL.SCHEME, ");
				sb.append("           CASE ");
				sb.append("               WHEN VSL.COMBI_GC_OPS_IND = 'Y' THEN ");
				sb.append("                   ' / ' ");
				sb.append("                   ||(NVL(VSL.COMBI_GC_SCHEME, '-')) ");
				sb.append("               ELSE ");
				sb.append("                   '' ");
				sb.append("           END ");
				sb.append("    ) AS SCHEME, ");
				sb.append("    BGE.EDO_ASN_NBR   EDO_NBR, ");
				sb.append("    NULL ESN_NBR, ");
				sb.append("    AU.USER_NAME      LAST_MODIFY_USER_ID, ");
				sb.append("    BGE.LAST_MODIFY_DTTM, ");
				sb.append("    'Bulk Cargo' TYPE, ");
				sb.append("    CC.CO_NM          SHIPPING_AGENT, ");
				sb.append("    DECODE(BGE.CRG_STATUS, 'T', 'Transhipment', 'L', 'Local', ");
				sb.append("           'R', 'Re-Export'), ");
				sb.append("    'BULK_GB_EDO' CRG_TYPE, ");
				sb.append("    0 EDO_PKGS, ");
				sb.append("    0 DELIVERD_PKGS, ");
				sb.append("    0 BALANCE_PKGS, ");
				sb.append("    0 ESN_PKGS, ");
				sb.append("    0 ESN_RCVD, ");
				sb.append("    0 ESN_LOAD, ");
				sb.append("    BGE.ADP_NM        MAIN_ADP_TRK, ");
				sb.append("    AU.COMPANY_ID, ");
				sb.append("    ( VSL.VSL_NM ");
				sb.append("      || '/' ");
				sb.append("      || VSL.IN_VOY_NBR ) DISC_VSL, ");
				sb.append("    '' LOAD_VSL ");
				sb.append("FROM ");
				sb.append("    BULK_GB_EDO             BGE ");
				sb.append("    LEFT OUTER JOIN BULK_TESN_JP_JP         BT1 ON BGE.EDO_ASN_NBR = BT1.EDO_ASN_NBR ");
				sb.append("    INNER JOIN BULK_MANIFEST_DETAILS   BMF ON BGE.VAR_NBR = BMF.VAR_NBR ");
				sb.append("                                            AND BGE.MFT_SEQ_NBR = BMF.MFT_SEQ_NBR ");
				sb.append("    INNER JOIN VESSEL_CALL             VSL ON BGE.VAR_NBR = VSL.VV_CD ");
				sb.append("    INNER JOIN BERTHING                BE ON VSL.VV_CD = BE.VV_CD ");
				sb.append("                              AND BE.SHIFT_IND = 1 ");
				sb.append("    INNER JOIN COMPANY_CODE            CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("    LEFT JOIN VESSEL_DECLARANT        VD ON BGE.VAR_NBR = VD.VV_CD ");
				sb.append("    LEFT JOIN ADM_USER                AU ON BGE.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("    BGE.EDO_STATUS = 'A' ");
				sb.append("    AND BT1.EDO_ASN_NBR IS NULL ");
				sb.append("    AND ( BGE.EDO_ASN_NBR IN ( ");
				sb.append("        :f_asn_nbr ");
				sb.append("    ) ");
				sb.append("          OR 1 = :A ) ");
				sb.append("    AND ( BGE.VAR_NBR = :f_vv_cd ");
				sb.append("          OR 1 = :V ) ");
				sb.append("    AND ( BGE.EDO_CREATE_CD = :f_co_cd ");
				sb.append("          OR 1 = :C ) ");
				sb.append("    AND ( ");
				sb.append("	 ");
				sb.append("     1 = :JP ");
				sb.append("	 ");
				sb.append("          OR BGE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("          OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("          OR BGE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("          OR BMF.MIXED_SCHEME_ACCT_NBR IN ( ");
				sb.append("        :f_acc_nbr ");
				sb.append("    ) ");
				sb.append("	 ");
				sb.append("          OR VD.CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("          OR ( VD.CUST_CD IS NULL ");
				sb.append("               AND VSL.VSL_OPR_CD = :f_cust_cd ) ");
				sb.append("	 ");
				sb.append("          OR ( VD.CUST_CD IS NULL ");
				sb.append("               AND BGE.VAR_NBR IN ( ");
				sb.append("        :f_t_vv_cd ");
				sb.append("    ) ) ) ");
				sb.append("UNION ALL ");
				sb.append("SELECT ");
				sb.append("    ESN.OUT_VOY_VAR_NBR   VV_CD, ");
				sb.append("    VSL.VSL_NM, ");
				sb.append("    VSL.IN_VOY_NBR, ");
				sb.append("    VSL.OUT_VOY_NBR, ");
				sb.append("    VSL.TERMINAL, ");
				sb.append("    CONCAT(VSL.SCHEME, ");
				sb.append("           CASE ");
				sb.append("               WHEN VSL.COMBI_GC_OPS_IND = 'Y' THEN ");
				sb.append("                   ' / ' ");
				sb.append("                   ||(NVL(VSL.COMBI_GC_SCHEME, '-')) ");
				sb.append("               ELSE ");
				sb.append("                   '' ");
				sb.append("           END ");
				sb.append("    ) AS SCHEME, ");
				sb.append("    NULL EDO_NBR, ");
				sb.append("    ESN.ESN_ASN_NBR       ESN_NBR, ");
				sb.append("    AU.USER_NAME         LAST_MODIFY_USER_ID, ");
				sb.append("    BE.LAST_MODIFY_DTTM, ");
				sb.append("    'Bulk Cargo' TYPE, ");
				sb.append("    CC.CO_NM             SHIPPING_AGENT, ");
				sb.append("    'Export' CRG_STATUS, ");
				sb.append("    'BULK_ESN' CRG_TYPE, ");
				sb.append("    0 EDO_PKGS, ");
				sb.append("    0 DELIVERD_PKGS, ");
				sb.append("    0 BALANCE_PKGS, ");
				sb.append("    0 ESN_PKGS, ");
				sb.append("    0 ESN_RCVD, ");
				sb.append("    0 ESN_LOAD, ");
				sb.append("    BED.TRUCKER_NM       MAIN_ADP_TRK, ");
				sb.append("    AU.COMPANY_ID, ");
				sb.append("    '' DISC_VSL, ");
				sb.append("    ( VSL.VSL_NM ");
				sb.append("      || '/' ");
				sb.append("      || VSL.OUT_VOY_NBR ) LOAD_VSL ");
				sb.append("FROM ");
				sb.append("    BULK_ESN           ESN ");
				sb.append("    INNER JOIN BULK_ESN_DETAILS   BED ON ESN.ESN_ASN_NBR = BED.ESN_ASN_NBR ");
				sb.append("    INNER JOIN VESSEL_CALL        VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("    INNER JOIN BERTHING           BE ON VSL.VV_CD = BE.VV_CD ");
				sb.append("                              AND BE.SHIFT_IND = 1 ");
				sb.append("    INNER JOIN COMPANY_CODE       CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("    LEFT JOIN VESSEL_DECLARANT   VD ON ESN.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("    LEFT JOIN ADM_USER           AU ON BE.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("    ESN.ESN_STATUS = 'A' ");
				sb.append("    AND TRANS_TYPE = 'E' ");
				sb.append("    AND ( ESN.ESN_ASN_NBR IN ( ");
				sb.append("        :f_asn_nbr ");
				sb.append("    ) ");
				sb.append("          OR 1 = :A ) ");
				sb.append("    AND ( ESN.OUT_VOY_VAR_NBR = :f_vv_cd ");
				sb.append("          OR 1 = :V ) ");
				sb.append("    AND ( ESN.ESN_CREATE_CD = :f_co_cd ");
				sb.append("          OR 1 = :C ) ");
				sb.append("    AND ( ");
				sb.append("	 ");
				sb.append("     1 = :JP ");
				sb.append("	 ");
				sb.append("          OR ESN.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("          OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("          OR BED.TRUCKER_IC IN ( ");
				sb.append("        :f_trucker_ic ");
				sb.append("    ) ");
				sb.append("	 ");
				sb.append("          OR BED.MIXED_SCHEME_ACCT_NBR IN ( ");
				sb.append("        :f_acc_nbr ");
				sb.append("    ) ");
				sb.append("	 ");
				sb.append("          OR VD.CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("          OR ( VD.CUST_CD IS NULL ");
				sb.append("               AND VSL.VSL_OPR_CD = :f_cust_cd ) ");
				sb.append("	 ");
				sb.append("          OR ( VD.CUST_CD IS NULL ");
				sb.append("               AND ESN.OUT_VOY_VAR_NBR IN ( ");
				sb.append("        :f_t_vv_cd ");
				sb.append("    ) ) ) ");
				sb.append("UNION ALL ");
				sb.append("SELECT ");
				sb.append("    ESN.OUT_VOY_VAR_NBR   VV_CD, ");
				sb.append("    VSL.VSL_NM, ");
				sb.append("    VSL.IN_VOY_NBR, ");
				sb.append("    VSL.OUT_VOY_NBR, ");
				sb.append("    VSL.TERMINAL, ");
				sb.append("    CONCAT(VSL.SCHEME, ");
				sb.append("           CASE ");
				sb.append("               WHEN VSL.COMBI_GC_OPS_IND = 'Y' THEN ");
				sb.append("                   ' / ' ");
				sb.append("                   ||(NVL(VSL.COMBI_GC_SCHEME, '-')) ");
				sb.append("               ELSE ");
				sb.append("                   '' ");
				sb.append("           END ");
				sb.append("    ) AS SCHEME, ");
				sb.append("    BTESN.EDO_ASN_NBR    EDO_NBR, ");
				sb.append("    ESN.ESN_ASN_NBR       ESN_NBR, ");
				sb.append("    AU.USER_NAME         LAST_MODIFY_USER_ID, ");
				sb.append("    BE.LAST_MODIFY_DTTM, ");
				sb.append("    'Bulk Cargo' TYPE, ");
				sb.append("    CC.CO_NM             SHIPPING_AGENT, ");
				sb.append("    'Transhipment' CRG_STATUS, ");
				sb.append("    'BULK_TESN_JP_JP' CRG_TYPE, ");
				sb.append("    0 EDO_PKGS, ");
				sb.append("    0 DELIVERD_PKGS, ");
				sb.append("    0 BALANCE_PKGS, ");
				sb.append("    0 ESN_PKGS, ");
				sb.append("    0 ESN_RCVD, ");
				sb.append("    0 ESN_LOAD, ");
				sb.append("    NULL MAIN_ADP_TRK, ");
				sb.append("    AU.COMPANY_ID, ");
				sb.append("    ( DVSL.VSL_NM ");
				sb.append("      || '/' ");
				sb.append("      || DVSL.IN_VOY_NBR ) DISC_VSL, ");
				sb.append("    ( VSL.VSL_NM ");
				sb.append("      || '/' ");
				sb.append("      || VSL.OUT_VOY_NBR ) LOAD_VSL ");
				sb.append("FROM ");
				sb.append("    BULK_ESN                ESN ");
				sb.append("    INNER JOIN BULK_TESN_JP_JP         BTESN ON BTESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("    INNER JOIN BULK_GB_EDO             BGE ON BTESN.EDO_ASN_NBR = BGE.EDO_ASN_NBR ");
				sb.append("    INNER JOIN BULK_MANIFEST_DETAILS   BMF ON BGE.VAR_NBR = BMF.VAR_NBR ");
				sb.append("                                            AND BGE.MFT_SEQ_NBR = BMF.MFT_SEQ_NBR ");
				sb.append("    INNER JOIN VESSEL_CALL             VSL ON ESN.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("    INNER JOIN VESSEL_CALL             DVSL ON BGE.VAR_NBR = DVSL.VV_CD ");
				sb.append("    INNER JOIN BERTHING                BE ON VSL.VV_CD = BE.VV_CD ");
				sb.append("                              AND BE.SHIFT_IND = 1 ");
				sb.append("    INNER JOIN BERTHING                DBE ON DVSL.VV_CD = DBE.VV_CD ");
				sb.append("                               AND DBE.SHIFT_IND = 1 ");
				sb.append("    INNER JOIN COMPANY_CODE            CC ON VSL.VSL_OPR_CD = CC.CO_CD ");
				sb.append("    LEFT JOIN VESSEL_DECLARANT        VD ON ESN.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("    LEFT JOIN VESSEL_DECLARANT        VDG ON BGE.VAR_NBR = VDG.VV_CD ");
				sb.append("    LEFT JOIN ADM_USER                AU ON BE.LAST_MODIFY_USER_ID = AU.USER_ACCT ");
				sb.append("WHERE ");
				sb.append("    ESN.ESN_STATUS = 'A' ");
				sb.append("    AND ESN.TRANS_TYPE = 'A' ");
				sb.append("    AND ( ( ( BGE.EDO_ASN_NBR IN ( ");
				sb.append("        :f_asn_nbr ");
				sb.append("    ) ");
				sb.append("              OR 1 = :A ) ");
				sb.append("            AND ( BGE.VAR_NBR = :f_vv_cd ");
				sb.append("                  OR 1 = :V ) ");
				sb.append("            AND ( BGE.EDO_CREATE_CD = :f_co_cd ");
				sb.append("                  OR 1 = :C ) ");
				sb.append("            AND ( ");
				sb.append("	 ");
				sb.append("             1 = :JP ");
				sb.append("	 ");
				sb.append("                  OR BGE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("                  OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("                  OR BGE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("                  OR BMF.MIXED_SCHEME_ACCT_NBR IN ( ");
				sb.append("        :f_acc_nbr ");
				sb.append("    ) ");
				sb.append("	 ");
				sb.append("                  OR VDG.CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("                  OR ( VDG.CUST_CD IS NULL ");
				sb.append("                       AND VSL.VSL_OPR_CD = :f_cust_cd ) ");
				sb.append("	 ");
				sb.append("                  OR ( VDG.CUST_CD IS NULL ");
				sb.append("                       AND BGE.VAR_NBR IN ( ");
				sb.append("        :f_t_vv_cd ");
				sb.append("    ) ) ) ) ");
				sb.append("          OR ( ( ESN.ESN_ASN_NBR IN ( ");
				sb.append("        :f_asn_nbr ");
				sb.append("    ) ");
				sb.append("                 OR 1 = :A ) ");
				sb.append("               AND ( ESN.OUT_VOY_VAR_NBR = :f_vv_cd ");
				sb.append("                     OR 1 = :V ) ");
				sb.append("               AND ( ESN.ESN_CREATE_CD = :f_co_cd ");
				sb.append("                     OR 1 = :C ) ");
				sb.append("               AND ( ");
				sb.append("	 ");
				sb.append("                1 = :JP ");
				sb.append("	 ");
				sb.append("                     OR ESN.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("                     OR VSL.CREATE_CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("                     OR BTESN.MIXED_SCHEME_ACCT_NBR IN ( ");
				sb.append("        :f_acc_nbr ");
				sb.append("    ) ");
				sb.append("	 ");
				sb.append("                     OR VD.CUST_CD = :f_cust_cd ");
				sb.append("	 ");
				sb.append("                     OR ( VD.CUST_CD IS NULL ");
				sb.append("                          AND VSL.VSL_OPR_CD = :f_cust_cd ) ");
				sb.append(" ");
				sb.append("                     OR ( VD.CUST_CD IS NULL ");
				sb.append("                          AND ESN.OUT_VOY_VAR_NBR IN ( ");
				sb.append("        :f_t_vv_cd ");
				sb.append("    ) ) ) ) )");

			}

			sql = sb.toString();

			sql = buildCargoEnquirySQL(sql, filters);
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			buildCargoEnquiryParams(filters, parameters);

			log.info("ListCargoRecords SQL :" + sql.toString() + ", ParamMap:" + parameters.getValues());
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						parameters, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}

			if (StringUtils.isNotBlank(sort) && StringUtils.isNotBlank(dir)) {
				sb.setLength(0);
				sb.append(" ORDER BY ");
				sb.append(sort.toUpperCase());
				sb.append(" ");
				sb.append(dir.toUpperCase());
//				sql += " ORDER BY " + sort.toUpperCase() + " " + dir.toUpperCase();
				sql += sb.toString();
			} else {
				sb.setLength(0);
				sb.append(" ORDER BY LAST_MODIFY_DTTM DESC, EDO_NBR, ESN_NBR ");
//				sql += " ORDER BY LAST_MODIFY_DTTM DESC, EDO_NBR, ESN_NBR ";
				sql += sb.toString();
			}
			log.info("SQL order by: " + sql);

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			log.info("parameters" + parameters.getValues());

			listCargoRecords = namedParameterJdbcTemplate.query(sql, parameters,
					new BeanPropertyRowMapper<CargoEnquiryMgmtAction>(CargoEnquiryMgmtAction.class));
			for (CargoEnquiryMgmtAction object : listCargoRecords) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);

			log.info("listCargoRecords Result: " + listCargoRecords.toString());
		} catch (BusinessException e) {
			log.info("Exception listCargoRecords : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception listCargoRecords : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: listCargoRecords  DAO  END");
		}

		return listCargoRecords;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->buildCargoEnquirySQL()
	private String buildCargoEnquirySQL(String sql, Map<String, Object> filters) throws BusinessException {
		try {
			log.info("START buildCargoEnquirySQL DAO :: sql: " + CommonUtility.deNull(sql) + " filters: "
					+ CommonUtility.deNull(filters.toString()));
			if (filters.containsKey("f_frm_dttm") && filters.containsKey("f_to_dttm")
					&& StringUtils.isNotBlank(filters.get("f_frm_dttm").toString())
					&& StringUtils.isNotBlank(filters.get("f_to_dttm").toString())) {
				StringBuffer frmToCondition = new StringBuffer();
				StringBuffer discFrmToCondition = new StringBuffer();

				frmToCondition.append(
						"AND CASE WHEN BE.ATB_DTTM IS NOT NULL THEN TRUNC(BE.ATB_DTTM) ELSE TRUNC(BE.ETB_DTTM) end <= TO_DATE(:f_to_dttm, 'DD/MM/YYYY') ");
				discFrmToCondition.append(
						"AND CASE WHEN BE.ATB_DTTM IS NOT NULL THEN TRUNC(BE.ATB_DTTM) ELSE TRUNC(BE.ETB_DTTM) end <= TO_DATE(:f_to_dttm, 'DD/MM/YYYY') ");
				frmToCondition.append(
						"AND CASE WHEN BE.ATB_DTTM IS NOT NULL THEN TRUNC(BE.ATB_DTTM) ELSE TRUNC(BE.ETB_DTTM) end >= TO_DATE(:f_frm_dttm, 'DD/MM/YYYY') ");
				discFrmToCondition.append(
						"AND CASE WHEN BE.ATB_DTTM IS NOT NULL THEN TRUNC(BE.ATB_DTTM) ELSE TRUNC(BE.ETB_DTTM) end >= TO_DATE(:f_frm_dttm, 'DD/MM/YYYY') ");

				sql = replace(sql, "AND FROM_TO_DTTM_CONDITION", frmToCondition.toString());
				sql = replace(sql, "AND DISC_FROM_TO_DTTM_CONDITION", discFrmToCondition.toString());
			} else {
				sql = replace(sql, "AND FROM_TO_DTTM_CONDITION", "");
				sql = replace(sql, "AND DISC_FROM_TO_DTTM_CONDITION", "");
				sql = replace(sql, "LEFT JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1", "");
				sql = replace(sql, "LEFT JOIN BERTHING DBE ON VSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1", "");
			}
		} catch (BusinessException e) {
			log.info("Exception buildCargoEnquirySQL : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception buildCargoEnquirySQL : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END buildCargoEnquirySQL DAO. sql: " + sql);
		}
		return sql;
	}

	// jp.src.org.lattice.util.common--->StringUtil-->replace()
	public static String replace(String source, String key, String value) throws BusinessException {
		return replace(new StringBuffer(source), key, value, true);
	}

	// jp.src.org.lattice.util.common--->StringUtil-->replace()
	public static String replace(StringBuffer source, String key, String value, boolean all) throws BusinessException {
		try {
			log.info("START replace DAO " + " source :" + CommonUtility.deNull(source.toString()) + " key: "
					+ CommonUtility.deNull(key) + " value : " + CommonUtility.deNull(value));
			int count = 0;
			for (int index = source.toString().indexOf(key, count); index >= 0;) {
				source.replace(index, index + key.length(), value);
				count = index + value.length();
				if (count > 0 && !all)
					break;
				index = source.toString().indexOf(key, count);
				if (log.isDebugEnabled())
					log.info("Index:" + index);
				if (log.isDebugEnabled())
					log.info("String:" + source.toString());
			}
		} catch (Exception e) {
			log.info("Exception replace : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END replace DAO. source: " + source.toString());
		}
		return source.toString();
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->buildCargoEnquiryParams()
	private void buildCargoEnquiryParams(Map<String, Object> filters, Map<String, Object> paramMap)
			throws BusinessException {
		try {
			log.info("START buildCargoEnquiryParams DAO :: filters: " + CommonUtility.deNull(filters.toString())
					+ " paramMap: " + CommonUtility.deNull(paramMap.toString()));
			if (filters.containsKey("f_co_cd")) {

				paramMap.put("f_co_cd", filters.get("f_co_cd").toString());
				paramMap.put("C", 0);
			} else {
				paramMap.put("f_co_cd", "");
				paramMap.put("C", 1);
			}
			if (filters.containsKey("f_asn_nbr")) {

				String[] asn = filters.get("f_asn_nbr").toString().split(",");

				for (int i = 0; i < asn.length; i++) {
					asn[i] = asn[i].trim();
				}
				if (asn != null) {
					paramMap.put("f_asn_nbr", Arrays.asList(asn));
				}
				paramMap.put("A", 0);
			} else {
				paramMap.put("A", 1);
				paramMap.put("f_asn_nbr", "");
			}
			if (filters.containsKey("f_vv_cd")) {
				paramMap.put("f_vv_cd", filters.get("f_vv_cd"));
				paramMap.put("V", 0);
			} else {
				paramMap.put("f_vv_cd", "");
				paramMap.put("V", 1);
			}

			if (filters.containsKey("f_frm_dttm") && filters.containsKey("f_to_dttm")) {
				paramMap.put("f_frm_dttm", filters.get("f_frm_dttm"));
				paramMap.put("f_to_dttm", filters.get("f_to_dttm"));
			}

			paramMap.put("f_cust_cd", "");
			paramMap.put("f_acc_nbr", "");
			paramMap.put("f_t_vv_cd", "");
			paramMap.put("f_trucker_ic", "");
			paramMap.put("f_sub_adp", "");
			paramMap.put("f_sub_trucker", "");
			String custCd = filters.get("f_cust_cd").toString();
			if ("JP".equalsIgnoreCase(custCd)) {
				paramMap.put("JP", 1);
			} else {
				// List<String> accNbrList = (List<String>) filters.get("f_acc_nbr");
				// List<String> truckerIcList = (List<String>) filters.get("f_trucker_ic");
				// List<String> vvCdList = (List<String>) filters.get("f_t_vv_cd");
				// List<String> edoList = (List<String>) filters.get("f_sub_adp");
				// List<String> esnList = (List<String>) filters.get("f_sub_trucker");
				paramMap.put("JP", 0);
				paramMap.put("f_cust_cd", custCd);

				String[] accNbrList = filters.get("f_acc_nbr").toString().split(",");
				for (int i = 0; i < accNbrList.length; i++) {
					accNbrList[i] = accNbrList[i].trim();
				}

				if (accNbrList != null && accNbrList.length > 0) {
					paramMap.put("f_acc_nbr", Arrays.asList(accNbrList));
				}
				String[] truckerIcList = filters.get("f_trucker_ic").toString().split(",");
				for (int i = 0; i < truckerIcList.length; i++) {
					truckerIcList[i] = truckerIcList[i].trim();
				}
				if (truckerIcList != null && truckerIcList.length > 0) {
					paramMap.put("f_trucker_ic", Arrays.asList(truckerIcList));
				}

				String[] vvCdList = filters.get("f_t_vv_cd").toString().split(",");
				for (int i = 0; i < vvCdList.length; i++) {
					vvCdList[i] = vvCdList[i].trim();
				}

				if (vvCdList != null && vvCdList.length > 0) {
					paramMap.put("f_t_vv_cd", Arrays.asList(vvCdList));
				}

				String[] edoList = filters.get("f_sub_adp").toString().split(",");
				for (int i = 0; i < edoList.length; i++) {
					edoList[i] = edoList[i].trim();
				}
				if (edoList != null && edoList.length > 0) {
					paramMap.put("f_sub_adp", Arrays.asList(edoList));
				}
				String[] esnList = filters.get("f_sub_trucker").toString().split(",");
				for (int i = 0; i < esnList.length; i++) {
					esnList[i] = esnList[i].trim();
				}
				if (esnList != null && esnList.length > 0) {
					paramMap.put("f_sub_trucker", Arrays.asList(esnList));
				}
			}
		} catch (Exception e) {
			log.info("Exception buildCargoEnquiryParams : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END buildCargoEnquiryParams DAO");
		}
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->buildCargoEnquiryParams()
	private void buildCargoEnquiryParams(Map<String, Object> filters, MapSqlParameterSource paramMap)
			throws BusinessException {
		try {
			log.info("START buildCargoEnquiryParams DAO :: filters: " + CommonUtility.deNull(filters.toString())
					+ " paramMap: " + CommonUtility.deNull(paramMap.getValues().toString()));
			if (filters.containsKey("f_co_cd")) {

				paramMap.addValue("f_co_cd", filters.get("f_co_cd").toString());
				paramMap.addValue("C", 0);
			} else {
				paramMap.addValue("f_co_cd", "");
				paramMap.addValue("C", 1);
			}
			if (filters.containsKey("f_asn_nbr")) {
				String[] asn = filters.get("f_asn_nbr").toString().split(",");
				for (int i = 0; i < asn.length; i++) {
					asn[i] = asn[i].trim();
				}
				if (asn != null) {
					paramMap.addValue("f_asn_nbr", Arrays.asList(asn));
				}
				paramMap.addValue("A", 0);
			} else {
				paramMap.addValue("A", 1);
				paramMap.addValue("f_asn_nbr", "");
			}
			if (filters.containsKey("f_vv_cd") && !filters.get("f_vv_cd").equals("")) {
				paramMap.addValue("f_vv_cd", filters.get("f_vv_cd"));
				paramMap.addValue("V", 0);
			} else {
				paramMap.addValue("f_vv_cd", "");
				paramMap.addValue("V", 1);
			}

			if (filters.containsKey("f_frm_dttm") && filters.containsKey("f_to_dttm")) {
				paramMap.addValue("f_frm_dttm", filters.get("f_frm_dttm"));
				paramMap.addValue("f_to_dttm", filters.get("f_to_dttm"));
			}

			paramMap.addValue("f_cust_cd", "");
			paramMap.addValue("f_acc_nbr", "");
			paramMap.addValue("f_t_vv_cd", "");
			paramMap.addValue("f_trucker_ic", "");
			paramMap.addValue("f_sub_adp", "");
			paramMap.addValue("f_sub_trucker", "");
			String custCd = filters.get("f_cust_cd").toString();
			if ("JP".equalsIgnoreCase(custCd)) {
				paramMap.addValue("JP", 1);
			} else {
				// List<String> accNbrList = (List<String>) filters.get("f_acc_nbr");
				// List<String> truckerIcList = (List<String>) filters.get("f_trucker_ic");
				// List<String> vvCdList = (List<String>) filters.get("f_t_vv_cd");
				// List<String> edoList = (List<String>) filters.get("f_sub_adp");
				// List<String> esnList = (List<String>) filters.get("f_sub_trucker");
				paramMap.addValue("JP", 0);
				paramMap.addValue("f_cust_cd", custCd);

				String[] accNbrList = filters.get("f_acc_nbr").toString().split(",");
				for (int i = 0; i < accNbrList.length; i++) {
					accNbrList[i] = accNbrList[i].trim();
				}

				if (accNbrList != null && accNbrList.length > 0) {
					paramMap.addValue("f_acc_nbr", Arrays.asList(accNbrList));
				}
				String[] truckerIcList = filters.get("f_trucker_ic").toString().split(",");
				for (int i = 0; i < truckerIcList.length; i++) {
					truckerIcList[i] = truckerIcList[i].trim();
				}
				if (truckerIcList != null && truckerIcList.length > 0) {
					paramMap.addValue("f_trucker_ic", Arrays.asList(truckerIcList));
				}

				String[] vvCdList = filters.get("f_t_vv_cd").toString().split(",");
				for (int i = 0; i < vvCdList.length; i++) {
					vvCdList[i] = vvCdList[i].trim();
				}

				if (vvCdList != null && vvCdList.length > 0) {
					paramMap.addValue("f_t_vv_cd", Arrays.asList(vvCdList));
				}

				String[] edoList = filters.get("f_sub_adp").toString().split(",");
				for (int i = 0; i < edoList.length; i++) {
					edoList[i] = edoList[i].trim();
				}
				if (edoList != null && edoList.length > 0) {
					paramMap.addValue("f_sub_adp", Arrays.asList(edoList));
				}
				String[] esnList = filters.get("f_sub_trucker").toString().split(",");
				for (int i = 0; i < esnList.length; i++) {
					esnList[i] = esnList[i].trim();
				}
				if (esnList != null && esnList.length > 0) {
					paramMap.addValue("f_sub_trucker", Arrays.asList(esnList));
				}
			}
		} catch (Exception e) {
			log.info("Exception buildCargoEnquiryParams : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END buildCargoEnquiryParams DAO");
		}
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->getAccountNbrByCustCd()
	@Override
	public List<String> getAccountNbrByCustCd(String custCd) throws BusinessException {

		List<String> accountNbrByCustCd = new ArrayList<>();
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getAccountNbrByCustCd  DAO  Start Obj :" + CommonUtility.deNull(custCd));

			sb.append("SELECT ACCT_NBR AS accNbr FROM CUST_ACCT ");
			sb.append("WHERE  CUST_CD = :custCd	and ACCT_STATUS_CD = 'A' ");

			paramMap.put("custCd", custCd);

			log.info("getAccountNbrByCustCd SQL : " + sb.toString() + "paramMap:" + paramMap.toString());

			accountNbrByCustCd = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));

			log.info("getAccountNbrBycustCd Result: " + accountNbrByCustCd.toString());
		} catch (Exception e) {
			log.info("Exception getAccountNbrByCustCd :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAccountNbrByCustCd  DAO  END");
		}
		return accountNbrByCustCd;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->getTruckerIcByCustCd()
	@Override
	public List<String> getTruckerIcByCustCd(String custCd) throws BusinessException {

		List<String> truckerIcByCustCd = new ArrayList<>();
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getTruckerIcByCustCd  DAO  Start Obj :" + CommonUtility.deNull(custCd));

			sb.append("SELECT TDB_CR_NBR AS tdbCrNbr FROM CUSTOMER ");
			sb.append("WHERE CUST_CD = :custCd	UNION ");
			sb.append("SELECT UEN_NBR AS uenNbr FROM CUSTOMER ");
			sb.append("WHERE CUST_CD = :custCd ");

			paramMap.put("custCd", custCd);

			log.info("getTruckerIcByCustCd SQL : " + sb.toString() + "paramMap:" + paramMap.toString());

			truckerIcByCustCd = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));

			log.info("getTruckerIcByCustCd Result: " + truckerIcByCustCd.toString());
		} catch (Exception e) {
			log.info("Exception getTruckerIcByCustCd :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTruckerIcByCustCd  DAO  END");
		}
		return truckerIcByCustCd;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->getVvCf_t_vv_cdCd()
	@Override
	public List<String> getVvCdByAbCd(String custCd) throws BusinessException {
		List<String> vvCdByAbCd = new ArrayList<>();
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getVvCdByAbCd  DAO  Start Obj :" + CommonUtility.deNull(custCd));

			sb.append("SELECT VV_CD  AS vvCd FROM NOMINATED_SCHEME NS ");
			sb.append("LEFT JOIN VESSEL_SCHEME VS ON NS.SCHEME_CD = VS.SCHEME_CD ");
			sb.append("WHERE VS.AB_CD = :custCd ");

			paramMap.put("custCd", custCd);

			log.info("getVvCdByAbCd SQL : " + sb.toString() + "paramMap:" + paramMap.toString());

			vvCdByAbCd = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));

			log.info("getVvCdByAbCd Result: " + vvCdByAbCd.toString());
		} catch (Exception e) {
			log.info("Exception getVvCdByAbCd :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVvCdByAbCd  DAO  END");
		}
		return vvCdByAbCd;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->getEdoNbrBySubAdp()
	@Override
	public List<String> getEdoNbrBySubAdp(String custCd) throws BusinessException {
		List<String> edoNbrBySubAdp = new ArrayList<>();
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getEdoNbrBySubAdp  DAO  Start Obj :" + CommonUtility.deNull(custCd));
			sb.append("SELECT distinct SUB.ESN_ASN_NBR esnNbr FROM SUB_ADP SUB ");
			sb.append("WHERE SUB.TRUCKER_CO_CD = :custCd  ");
			sb.append("AND STATUS_CD = 'A' AND EDO_ESN_IND = '1' ");
			sb.append("ORDER BY ESN_ASN_NBR ");

			paramMap.put("custCd", custCd);

			log.info("getEdoNbrBySubAdp SQL : " + sb.toString() + "paramMap:" + paramMap.toString());

			edoNbrBySubAdp = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));

			log.info("getEdoNbrBySubAdp Result: " + edoNbrBySubAdp.toString());
		} catch (Exception e) {
			log.info("Exception getEdoNbrBySubAdp :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEdoNbrBySubAdp  DAO  END");
		}
		return edoNbrBySubAdp;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->getEsnNbrBySubTrucker()
	@Override
	public List<String> getEsnNbrBySubTrucker(String custCd) throws BusinessException {

		List<String> esnNbrBySubTrucker = new ArrayList<>();
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getEsnNbrBySubTrucker  DAO  Start Obj :" + CommonUtility.deNull(custCd));

			sb.append("SELECT distinct SUB.ESN_ASN_NBR esnNbr FROM SUB_ADP SUB ");
			sb.append("WHERE SUB.TRUCKER_CO_CD = :custCd ");
			sb.append("AND STATUS_CD = 'A' ");
			sb.append("AND EDO_ESN_IND = '0' OR EDO_ESN_IND IS NULL  ");

			paramMap.put("custCd", custCd);

			log.info("getEsnNbrBySubTrucker SQL : " + sb.toString() + "paramMap:" + paramMap.toString());

			esnNbrBySubTrucker = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));

			log.info("getEsnNbrBySubTrucker Result: " + esnNbrBySubTrucker.toString());
		} catch (Exception e) {
			log.info("Exception getEsnNbrBySubTrucker :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnNbrBySubTrucker  DAO  END");
		}
		return esnNbrBySubTrucker;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeCargoJdbcDao-->countCargoRecords()
	@Override
	public int countCargoRecords(Map<String, Object> filters) throws Exception {
		String type = (String) filters.get("type");
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		buildCargoEnquiryParams(filters, paramMap);
		int countCargoRecords = 0;
		try {
			int countBulkCargoRecords = 0;
			int countGeneralCargoRecords = 0;
			log.info("START: countCargoRecords  DAO  Start Obj : " + CommonUtility.deNull(filters.toString()));
			// Start #35975  - Fix Bug: Fix pagination when passing multiple ASN No. - NS OCT 2023
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			buildCargoEnquiryParams(filters, parameters);
			// End #35975  - Fix Bug: Fix pagination when passing multiple ASN No. - NS OCT 2023
			if ("GC".equalsIgnoreCase(type)) {
                // Remarks: Remove join table with Vessel Declarant to show only one record for each asn-nbr 18102022
				sb.append("SELECT SUM(TTL) FROM ( ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM GB_EDO GE ");
				sb.append("LEFT OUTER JOIN TESN_JP_JP T1 ON GE.EDO_ASN_NBR = T1.EDO_ASN_NBR ");
				sb.append("LEFT OUTER JOIN TESN_JP_PSA T2 ON GE.EDO_ASN_NBR     = T2.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON GE.VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("WHERE ");
				sb.append("T1.EDO_ASN_NBR IS NULL AND T2.EDO_ASN_NBR   IS NULL ");
				sb.append("AND (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN ESN_DETAILS ED ON E.ESN_ASN_NBR = ED.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'E' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR ED.TRUCKER_IC IN (:f_trucker_ic) ");
				sb.append("	OR E.ESN_ASN_NBR IN (:f_sub_trucker) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR ED.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_JP_JP TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN GB_EDO GE ON TESN.EDO_ASN_NBR = GE.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN VESSEL_CALL DVSL ON GE.VAR_NBR = DVSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'A' ");
				sb.append("AND ( ");
				sb.append("  ( ");
				sb.append("  (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND DISC_FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append("  OR ");
				sb.append("  ( ");
				sb.append("  (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR TESN.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_JP_PSA TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN GB_EDO GE ON TESN.EDO_ASN_NBR = GE.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.IN_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN VESSEL_CALL DVSL ON GE.VAR_NBR = DVSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'B' ");
				sb.append("AND ( ");
				sb.append("  ( ");
				sb.append("  (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND DISC_FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append("  OR ");
				sb.append("  ( ");
				sb.append("  (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (E.IN_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR TESN.SEC_ACCT_NBR in (:f_acc_nbr) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_PSA_JP TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
//				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON E.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'C' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR TESN.TRUCKER_IC IN (:f_trucker_ic) ");
				sb.append("	OR E.ESN_ASN_NBR IN (:f_sub_trucker) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR TESN.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN SS_DETAILS SS ON SS.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'S' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR SS.SHIPPER_CR_NBR IN (:f_trucker_ic) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR SS.ACCT_NBR in (:f_acc_nbr) ");
				sb.append("	) ");
				sb.append(")");
			} else if ("BC".equalsIgnoreCase(type)) {
				sb.append("SELECT SUM(TTL) FROM ( ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM BULK_GB_EDO BGE ");
				sb.append("LEFT OUTER JOIN BULK_TESN_JP_JP BT1 ON BGE.EDO_ASN_NBR = BT1.EDO_ASN_NBR ");
				sb.append(
						"LEFT JOIN BULK_MANIFEST_DETAILS BMF ON BGE.VAR_NBR = BMF.VAR_NBR AND BGE.MFT_SEQ_NBR = BMF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON BGE.VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON BGE.VAR_NBR = VD.VV_CD ");
				sb.append("WHERE BGE.EDO_STATUS = 'A' AND BT1.EDO_ASN_NBR IS NULL ");
				sb.append("AND (BGE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (BGE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (BGE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR BGE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR BGE.ADP_CUST_CD = :f_cust_cd ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR BMF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND BGE.VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM BULK_ESN BE ");
				sb.append("INNER JOIN BULK_ESN_DETAILS BED ON BE.ESN_ASN_NBR = BED.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON BE.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON BE.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("WHERE BE.ESN_STATUS = 'A' AND TRANS_TYPE = 'E' ");
				sb.append("AND (BE.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (BE.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (BE.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR BE.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR BED.TRUCKER_IC IN (:f_trucker_ic) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR BED.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND BE.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM BULK_ESN BE ");
				sb.append("INNER JOIN BULK_TESN_JP_JP BTESN ON BTESN.ESN_ASN_NBR = BE.ESN_ASN_NBR ");
				sb.append("INNER JOIN BULK_GB_EDO BGE ON BTESN.EDO_ASN_NBR = BGE.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN BULK_MANIFEST_DETAILS BMF ON BGE.VAR_NBR = BMF.VAR_NBR AND BGE.MFT_SEQ_NBR = BMF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON BE.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN VESSEL_CALL DVSL ON BGE.VAR_NBR = DVSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON BE.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VDG ON BGE.VAR_NBR = VDG.VV_CD ");
				sb.append("WHERE BE.ESN_STATUS = 'A' AND BE.TRANS_TYPE = 'A' ");
				sb.append("AND ( ");
				sb.append("  ( ");
				sb.append("  (BGE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (BGE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (BGE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND DISC_FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR BGE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR BGE.ADP_CUST_CD = :f_cust_cd ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR BMF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VDG.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND BGE.VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append("  OR ");
				sb.append("  ( ");
				sb.append("  (BE.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (BE.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (BE.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR BE.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR BTESN.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND BE.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append(" ) ");
				sb.append(")");
			} else {

				sb.append("SELECT SUM(TTL) FROM ( ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM GB_EDO GE ");
				sb.append("LEFT OUTER JOIN TESN_JP_JP T1 ON GE.EDO_ASN_NBR = T1.EDO_ASN_NBR ");
				sb.append("LEFT OUTER JOIN TESN_JP_PSA T2 ON GE.EDO_ASN_NBR     = T2.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON GE.VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON GE.VAR_NBR = VD.VV_CD ");
				sb.append("WHERE ");
				sb.append("T1.EDO_ASN_NBR IS NULL AND T2.EDO_ASN_NBR   IS NULL ");
				sb.append("AND (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
				// sb.append(" ----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND GE.VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN ESN_DETAILS ED ON E.ESN_ASN_NBR = ED.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON E.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'E' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR ED.TRUCKER_IC IN (:f_trucker_ic) ");
				sb.append("	OR E.ESN_ASN_NBR IN (:f_sub_trucker) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR ED.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND E.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_JP_JP TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN GB_EDO GE ON TESN.EDO_ASN_NBR = GE.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN VESSEL_CALL DVSL ON GE.VAR_NBR = DVSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON E.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VDG ON GE.VAR_NBR = VDG.VV_CD ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'A' ");
				sb.append("AND ( ");
				sb.append("  ( ");
				sb.append("  (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND DISC_FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VDG.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND GE.VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append("  OR ");
				sb.append("  ( ");
				sb.append("  (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR TESN.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND E.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_JP_PSA TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN GB_EDO GE ON TESN.EDO_ASN_NBR = GE.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN MANIFEST_DETAILS MF ON GE.VAR_NBR = MF.VAR_NBR AND GE.MFT_SEQ_NBR = MF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.IN_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN VESSEL_CALL DVSL ON GE.VAR_NBR = DVSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON E.IN_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VDG ON GE.VAR_NBR = VDG.VV_CD ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'B' ");
				sb.append("AND ( ");
				sb.append("  ( ");
				sb.append("  (GE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (GE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (GE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND DISC_FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR GE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR GE.ADP_CUST_CD = :f_cust_cd ");
				sb.append("	OR GE.EDO_ASN_NBR IN (:f_sub_adp) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR MF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VDG.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND GE.VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append("  OR ");
				sb.append("  ( ");
				sb.append("  (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (E.IN_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR TESN.SEC_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND E.IN_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN TESN_PSA_JP TESN ON TESN.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON E.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'C' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR TESN.TRUCKER_IC IN (:f_trucker_ic) ");
				sb.append("	OR E.ESN_ASN_NBR IN (:f_sub_trucker) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR TESN.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND E.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM ESN E ");
				sb.append("INNER JOIN SS_DETAILS SS ON SS.ESN_ASN_NBR = E.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON E.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON E.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("WHERE ");
				sb.append("TRANS_TYPE = 'S' ");
				sb.append("AND (E.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (E.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (E.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR E.ESN_CREATE_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR SS.SHIPPER_CR_NBR IN (:f_trucker_ic) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR SS.ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND E.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append(")");
				sql = sb.toString();
				sql = buildCargoEnquirySQL(sql, filters);
				log.info("SQL: " + sql + " paramMap: " + paramMap);
				// Start #35975  - Fix Bug: Fix pagination when passing multiple ASN No. - NS OCT 2023
				log.info("parameters" + parameters.getValues());
				countGeneralCargoRecords = namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class);
				// End #35975  - Fix Bug: Fix pagination when passing multiple ASN No. - NS OCT 2023
				log.info("countGeneralCargoRecords: " + countGeneralCargoRecords);

				sb.setLength(0);
				sb.append("SELECT SUM(TTL) FROM ( ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM BULK_GB_EDO BGE ");
				sb.append("LEFT OUTER JOIN BULK_TESN_JP_JP BT1 ON BGE.EDO_ASN_NBR = BT1.EDO_ASN_NBR ");
				sb.append(
						"LEFT JOIN BULK_MANIFEST_DETAILS BMF ON BGE.VAR_NBR = BMF.VAR_NBR AND BGE.MFT_SEQ_NBR = BMF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON BGE.VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON BGE.VAR_NBR = VD.VV_CD ");
				sb.append("WHERE BGE.EDO_STATUS = 'A' AND BT1.EDO_ASN_NBR IS NULL ");
				sb.append("AND (BGE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (BGE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (BGE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR BGE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR BGE.ADP_CUST_CD = :f_cust_cd ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR BMF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND BGE.VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM BULK_ESN BE ");
				sb.append("INNER JOIN BULK_ESN_DETAILS BED ON BE.ESN_ASN_NBR = BED.ESN_ASN_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON BE.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON BE.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("WHERE BE.ESN_STATUS = 'A' AND TRANS_TYPE = 'E' ");
				sb.append("AND (BE.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("AND (BE.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("AND (BE.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("AND FROM_TO_DTTM_CONDITION ");
				sb.append("AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR BE.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR BED.TRUCKER_IC IN (:f_trucker_ic) ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR BED.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND BE.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("UNION ALL ");
				sb.append("SELECT COUNT (*) TTL ");
				sb.append("FROM BULK_ESN BE ");
				sb.append("INNER JOIN BULK_TESN_JP_JP BTESN ON BTESN.ESN_ASN_NBR = BE.ESN_ASN_NBR ");
				sb.append("INNER JOIN BULK_GB_EDO BGE ON BTESN.EDO_ASN_NBR = BGE.EDO_ASN_NBR ");
				sb.append(
						"INNER JOIN BULK_MANIFEST_DETAILS BMF ON BGE.VAR_NBR = BMF.VAR_NBR AND BGE.MFT_SEQ_NBR = BMF.MFT_SEQ_NBR ");
				sb.append("INNER JOIN VESSEL_CALL VSL ON BE.OUT_VOY_VAR_NBR = VSL.VV_CD ");
				sb.append("INNER JOIN VESSEL_CALL DVSL ON BGE.VAR_NBR = DVSL.VV_CD ");
				sb.append("INNER JOIN BERTHING BE ON VSL.VV_CD = BE.VV_CD AND BE.SHIFT_IND = 1 ");
				sb.append("INNER JOIN BERTHING DBE ON DVSL.VV_CD = DBE.VV_CD AND DBE.SHIFT_IND = 1 ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VD ON BE.OUT_VOY_VAR_NBR = VD.VV_CD ");
				sb.append("LEFT JOIN VESSEL_DECLARANT VDG ON BGE.VAR_NBR = VDG.VV_CD ");
				sb.append("WHERE BE.ESN_STATUS = 'A' AND BE.TRANS_TYPE = 'A' ");
				sb.append("AND ( ");
				sb.append("  ( ");
				sb.append("  (BGE.EDO_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (BGE.VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (BGE.EDO_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND DISC_FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR BGE.EDO_CREATE_CD = :f_cust_cd ");
				sb.append("	OR DVSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- ADP/TRUCKER ");
				sb.append("	OR BGE.ADP_CUST_CD = :f_cust_cd ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR BMF.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VDG.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VDG.CUST_CD IS NULL AND BGE.VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append("  OR ");
				sb.append("  ( ");
				sb.append("  (BE.ESN_ASN_NBR IN (:f_asn_nbr) OR 1 = :A) ");
				sb.append("  AND (BE.OUT_VOY_VAR_NBR = :f_vv_cd OR 1 = :V) ");
				sb.append("  AND (BE.ESN_CREATE_CD = :f_co_cd OR 1 = :C) ");
				sb.append("  AND FROM_TO_DTTM_CONDITION ");
				sb.append("  AND ( ");
//					sb.append("	----- JP USER ");
				sb.append("	1 = :JP ");
//					sb.append("	----- PORT USER ");
				sb.append("	OR BE.ESN_CREATE_CD = :f_cust_cd ");
				sb.append("	OR VSL.CREATE_CUST_CD = :f_cust_cd ");
//					sb.append("	----- AB OPERATOR ");
				sb.append("	OR BTESN.MIXED_SCHEME_ACCT_NBR in (:f_acc_nbr) ");
//					sb.append("	----- DOC SUB AUTHOR ");
				sb.append("	OR VD.CUST_CD = :f_cust_cd ");
//					sb.append("	----- GB SHIPPING LINE ");
				sb.append("	OR (VD.CUST_CD IS NULL AND VSL.VSL_OPR_CD = :f_cust_cd) ");
//					sb.append("	----- TA OPERATOR ");
				sb.append("	OR (VD.CUST_CD IS NULL AND BE.OUT_VOY_VAR_NBR IN (:f_t_vv_cd)) ");
				sb.append("	) ");
				sb.append("  ) ");
				sb.append(" ) ");
				sb.append(")");
				sql = sb.toString();

//					String subTruck = getSysConfigValue("EXCLUDE", "RAMP");
//					String[] f_sub_truckerVal = subTruck.split(",");
//					for (int i = 0; i < f_sub_truckerVal.length; i++) {
//						f_sub_truckerVal[i] = f_sub_truckerVal[i].trim();
//					}
//					paramMap.addValue("f_sub_trucker", Arrays.asList(f_sub_truckerVal));

				sql = buildCargoEnquirySQL(sql, filters);
				log.info("SQL: " + sql + " paramMap: " + paramMap);
				// Start #35975  - Fix Bug: Fix pagination when passing multiple ASN No. - NS OCT 2023
				log.info("parameters" + parameters.getValues());
				countBulkCargoRecords = namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class);
				// End #35975  - Fix Bug: Fix pagination when passing multiple ASN No. - NS OCT 2023
				
				int result = countGeneralCargoRecords + countBulkCargoRecords;
				log.info("result: " + result);
				return result;
			}
			log.info("countCargoRecords Not In Type: " + countGeneralCargoRecords + countBulkCargoRecords);
			sql = sb.toString();
			sql = buildCargoEnquirySQL(sql, filters);
			paramMap.put("type", type);
			log.info("SQL: " + sql + " paramMap: " + paramMap);
			// Start #35975  - Fix Bug: Fix pagination when passing multiple ASN No. - NS OCT 2023
			log.info("parameters" + parameters.getValues());
			countCargoRecords = namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class);
			// End #35975  - Fix Bug: Fix pagination when passing multiple ASN No. - NS OCT 2023
			log.info("countCargoRecords : " + countCargoRecords);

		} catch (BusinessException e) {
			log.info("Exception countCargoRecords : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception countCargoRecords : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END countCargoRecords DAO END");
		}
		return countCargoRecords;
	}

	// method : sg.com.jp.dpe.dao.DpeCargoJdbcDao-->listGeneralShutoutCargo
	@Override
	public TableResult listGeneralShutoutCargo(Integer start, Integer limit, String sort, String dir,
			Map<String, Object> filters, Criteria criteria) throws BusinessException {
		String sql = "";
		StringBuilder sb = new StringBuilder();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<DPECargo> listGeneralShutoutCargo = new ArrayList<>();
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();

		try {
			log.info("START: listGeneralShutoutCargo start:" + CommonUtility.deNull(String.valueOf(start)) + "limit:"
					+ CommonUtility.deNull(String.valueOf(limit)) + "sort:" + CommonUtility.deNull(sort) + "dir:"
					+ CommonUtility.deNull(dir) + "filters:" + CommonUtility.deNull(filters.toString()));
			sb.append(
					"SELECT DISTINCT VC.VSL_NM, VC.OUT_VOY_NBR, (VC.VSL_NM || '/ ' || VC.OUT_VOY_NBR) AS  vslNmOutVoy,   VC.VV_CD,VC.TERMINAL, VC.SCHEME, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN NVL(VC.COMBI_GC_SCHEME,'') ELSE '' END SUBSCHEME, ESN.ESN_ASN_NBR, ");
			sb.append("BK.BK_REF_NBR,  NVL(ESND.UA_NBR_PKGS, 0) UA_NBR_PKGS, ESND.ESN_HS_CODE, ");
			sb.append("(CASE ");
			sb.append("WHEN VC.GB_CLOSE_SHP_IND='N' THEN 0 ");
			sb.append("ELSE NVL(BK.ACTUAL_NBR_SHIPPED, 0) ");
			sb.append("END) ACTUAL_NBR_SHIPPED ");
			sb.append(", NVL(BK.SHUTOUT_QTY,0) SHUTOUT_PKGS, ");
			// --NVL((ESND.UA_NBR_PKGS - BK.ACTUAL_NBR_SHIPPED),0) SHUTOUT_PKGS,
			sb.append(
					"NVL(ESND.UA_NBR_PKGS - NVL(BK.ACTUAL_NBR_SHIPPED, 0) - BK.SHUTOUT_DELIVERY_PKGS, 0) AS BALANCE_TO_LOAD, NVL(BK.ACTUAL_NBR_SHIPPED, 0) SHUTOUT_DELIVERY_PKGS, ESN.TRANS_TYPE, ");
			sb.append("ESN.STUFF_IND, ESND.ESN_VOL, ESND.ESN_WT, NVL(ESND.NBR_PKGS, 0) NBR_PKGS, SHIPPER_NM, ");
			sb.append(
					"NVL(EDO.SHUTOUT_EDO_PKGS, 0) SHUTOUT_EDO_PKGS, NVL(TRANSFER.TRANSFER_PKGS, 0) TRANSFER_PKGS, (NVL(ESND.NBR_PKGS,0)-NVL(ESND.UA_NBR_PKGS,0)) SHORT_SHIP_PKGS, ");
			sb.append(
					"(NVL(BK.SHUTOUT_QTY,0) - NVL(TRANSFER.TRANSFER_PKGS,0) -NVL(EDO.SHUTOUT_EDO_PKGS,0)) BALANCE_PKGS, ");
			// sb.append("(NVL(ESND.UA_NBR_PKGS,0) - NVL(BK.ACTUAL_NBR_SHIPPED,0)-
			// NVL(TRANSFER.TRANSFER_PKGS,0) -NVL(EDO.SHUTOUT_EDO_PKGS,0)) BALANCE_PKGS, ");
			sb.append("(CASE ");
			sb.append("WHEN ESN.TRANS_TYPE ='E' THEN ");
			sb.append("(CASE ");
			sb.append("WHEN ESND.esn_ops_ind='O' THEN 'OS' ");
			sb.append("WHEN ESND.esn_ops_ind='D' THEN 'DD' ");
			sb.append("WHEN ESND.esn_ops_ind='L' THEN 'LR' ");
			sb.append("WHEN ESND.esn_ops_ind='N' THEN 'GD' ");
			sb.append("END) ");
			sb.append("WHEN ESN.TRANS_TYPE ='A' THEN  'JJ' ");
			sb.append("WHEN ESN.TRANS_TYPE ='C' THEN  'PJ' ");
			sb.append("ELSE 'X' END) CRG_TYPE_NM, VC.GB_CLOSE_SHP_IND, VC.vv_status_ind ");
			sb.append("FROM VESSEL_CALL VC, ESN ESN, BK_DETAILS BK, ESN_DETAILS ESND, ");
			// CRG_TYPE, CARGO_CATEGORY_CODE CODE,
			sb.append("(select sum(NBR_PKGS) SHUTOUT_EDO_PKGS , esn_asn_nbr from gb_edo ");
			sb.append("where EDO_STATUS ='A' and ESN_ASN_NBR is not null and shutout_ind  = 'Y' ");
			sb.append("group by ESN_ASN_NBR) EDO, ");
			sb.append("(SELECT SUM( ");
//						sb.append("(CASE ");
//						sb.append(" WHEN ESN_NEW.TRANS_TYPE ='E' THEN  NVL(ED.UA_NBR_PKGS,0) ");
//						sb.append(" WHEN ESN_NEW.TRANS_TYPE ='A' THEN  NVL(TESNJPJP.UA_NBR_PKGS,0) ");
//						sb.append(" WHEN ESN_NEW.TRANS_TYPE ='C' THEN  NVL(TESNPSAJP.UA_NBR_PKGS,0) ");
//						sb.append(" ELSE 0 END) ) TRANSFER_PKGS, ");
			sb.append("BK_NEW.bk_nbr_pkgs) TRANSFER_PKGS, ");
			sb.append("ESN_ORG.ESN_ASN_NBR ");
			sb.append("FROM ESN ESN_NEW, BK_DETAILS BK_NEW, ESN ESN_ORG, BK_DETAILS BK_OLD ");
			sb.append("WHERE ESN_NEW.BK_REF_NBR = BK_NEW.BK_REF_NBR ");
			sb.append("AND BK_NEW.OLD_BK_REF = BK_OLD.BK_REF_NBR ");
			sb.append("AND ESN_ORG.BK_REF_NBR = BK_OLD.BK_REF_NBR ");
			sb.append("AND BK_OLD.VAR_NBR = BK_NEW.BK_ORIGINAL_VAR_NBR ");
			sb.append("GROUP BY ESN_ORG.ESN_ASN_NBR) TRANSFER,VESSEL_DECLARANT VD ");
			sb.append(
					"WHERE VC.VV_CD = BK.VAR_NBR  AND BK.BK_REF_NBR = ESN.BK_REF_NBR AND ESND.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
			sb.append("AND EDO.esn_asn_nbr(+)= ESN.ESN_ASN_NBR AND TRANSFER.esn_asn_nbr (+)= ESN.ESN_ASN_NBR ");
			sb.append("AND ( ");
			sb.append("((ESND.UA_NBR_PKGS - NVL(BK.ACTUAL_NBR_SHIPPED, 0)) > 0 AND VC.GB_CLOSE_SHP_IND = 'Y') or ");
			sb.append("((ESND.UA_NBR_PKGS - NVL(BK.ACTUAL_NBR_SHIPPED, 0)) >= 0 AND VC.GB_CLOSE_SHP_IND = 'N') ");
			sb.append(")");
//						sb.append("AND BK.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD ");
//						sb.append("AND ESN.CARGO_CATEGORY_CD = CODE.CC_CD ");
			// commented by hem
			sb.append("AND (ESN.ESN_ASN_NBR IN (:esn_asn_nbr) OR 1 = :E) ");
			sb.append("AND ESN.esn_status ='A' ");
			sb.append("AND (VC.VV_CD =:vv_cd OR 1 = :V) ");
//						sb.append("AND VC.GB_CLOSE_SHP_IND != 'Y' ");
//						sb.append("AND VC.VV_STATUS_IND NOT IN ('CX') ");
			sb.append("AND (VC.VV_STATUS_IND != :S1 OR 1 = :V1) ");
			sb.append("AND VD.VV_CD(+) = VC.VV_CD ");
			sb.append("AND ( ");
			// ----- JP USER
			sb.append("	1 = :JP ");
			// ----- DOC SUB AUTHOR
			sb.append("OR VD.CUST_CD = :f_cust_cd ");
			// ----- GB SHIPPING LINE
			sb.append("OR VC.VSL_OPR_CD =:f_cust_cd ");
			sb.append(") ");
			sb.append("UNION ALL ");
			sb.append(
					"SELECT VC.VSL_NM,VC.OUT_VOY_NBR, (VC.VSL_NM || '/' || VC.OUT_VOY_NBR) AS vslNmOutVoy,  VC.VV_CD,VC.TERMINAL, VC.SCHEME, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN NVL(VC.COMBI_GC_SCHEME,'') ELSE '' END SUBSCHEME, ESN.ESN_ASN_NBR, ");
			sb.append("BK.BK_REF_NBR,  NVL(TESN.UA_NBR_PKGS, 0) UA_NBR_PKGS , TESN.HS_CD, ");
			sb.append("(CASE ");
			sb.append("WHEN VC.GB_CLOSE_SHP_IND='N' THEN 0 ");
			sb.append("ELSE NVL(BK.ACTUAL_NBR_SHIPPED, 0) ");
			sb.append("END) ACTUAL_NBR_SHIPPED , NVL(BK.SHUTOUT_QTY,0) SHUTOUT_PKGS, ");
			// NVL((TESN.UA_NBR_PKGS - BK.ACTUAL_NBR_SHIPPED),0) SHUTOUT_PKGS,
			sb.append(
					"NVL(TESN.UA_NBR_PKGS - NVL(BK.ACTUAL_NBR_SHIPPED, 0) - BK.SHUTOUT_DELIVERY_PKGS, 0) AS BALANCE_TO_LOAD, NVL(BK.SHUTOUT_DELIVERY_PKGS, 0) SHUTOUT_DELIVERY_PKGS, ESN.TRANS_TYPE, ");
			sb.append("ESN.STUFF_IND, TESN.GROSS_VOL, TESN.GROSS_WT, TESN.NBR_PKGS, TESN.SHIPPER_NM SHIPPER_NM, ");
			sb.append(
					"NVL(EDO.SHUTOUT_EDO_PKGS, 0) SHUTOUT_EDO_PKGS, NVL(TRANSFER.TRANSFER_PKGS, 0) TRANSFER_PKGS,(NVL(TESN.NBR_PKGS,0)-NVL(TESN.UA_NBR_PKGS,0)) SHORT_SHIP_PKGS, ");
			sb.append(
					"(NVL(BK.SHUTOUT_QTY,0) - NVL(TRANSFER.TRANSFER_PKGS,0) -NVL(EDO.SHUTOUT_EDO_PKGS,0)) BALANCE_PKGS, ");
			// sb.append(" (NVL(TESN.UA_NBR_PKGS,0) - NVL(BK.ACTUAL_NBR_SHIPPED,0)-
			// NVL(TRANSFER.TRANSFER_PKGS,0) -NVL(EDO.SHUTOUT_EDO_PKGS,0))--BALANCE_PKGS,
			// ");
			sb.append("(CASE ");
			sb.append("WHEN ESN.TRANS_TYPE ='A' THEN  'JJ' ");
			sb.append("WHEN ESN.TRANS_TYPE ='C' THEN  'PJ' ");
			sb.append("ELSE 'X' END) CRG_TYPE_NM, VC.GB_CLOSE_SHP_IND, VC.vv_status_ind ");
			sb.append("FROM VESSEL_CALL VC, ESN ESN, BK_DETAILS BK, TESN_PSA_JP TESN,  ");
			// CRG_TYPE, CARGO_CATEGORY_CODE CODE,
			sb.append("(select sum(NBR_PKGS) SHUTOUT_EDO_PKGS , esn_asn_nbr from gb_edo ");
			sb.append("where EDO_STATUS ='A' and ESN_ASN_NBR is not null and shutout_ind  = 'Y' ");
			sb.append("group by ESN_ASN_NBR) EDO, ");
			sb.append("(SELECT SUM( ");
//						sb.append("(CASE ");
//						sb.append(" WHEN ESN_NEW.TRANS_TYPE ='E' THEN  NVL(ED.UA_NBR_PKGS,0) ");
//						sb.append(" WHEN ESN_NEW.TRANS_TYPE ='A' THEN  NVL(TESNJPJP.UA_NBR_PKGS,0) ");
//						sb.append(" WHEN ESN_NEW.TRANS_TYPE ='C' THEN  NVL(TESNPSAJP.UA_NBR_PKGS,0) ");
//						sb.append(" ELSE 0 END) ) TRANSFER_PKGS, ");
			sb.append("BK_NEW.bk_nbr_pkgs) TRANSFER_PKGS, ");
			sb.append("ESN_ORG.ESN_ASN_NBR ");
			sb.append("FROM ESN ESN_NEW, BK_DETAILS BK_NEW, ESN ESN_ORG, BK_DETAILS BK_OLD ");
			sb.append("WHERE ESN_NEW.BK_REF_NBR = BK_NEW.BK_REF_NBR ");
			sb.append("AND BK_NEW.OLD_BK_REF = BK_OLD.BK_REF_NBR ");
			sb.append("AND ESN_ORG.BK_REF_NBR = BK_OLD.BK_REF_NBR ");
			sb.append("AND BK_OLD.VAR_NBR = BK_NEW.BK_ORIGINAL_VAR_NBR ");
			sb.append("GROUP BY ESN_ORG.ESN_ASN_NBR) TRANSFER,VESSEL_DECLARANT VD ");
			sb.append(
					"WHERE VC.VV_CD = BK.VAR_NBR  AND BK.BK_REF_NBR = ESN.BK_REF_NBR AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
			sb.append("AND EDO.esn_asn_nbr (+)= ESN.ESN_ASN_NBR AND TRANSFER.esn_asn_nbr (+)= ESN.ESN_ASN_NBR ");
			sb.append("AND ( ");
			sb.append("((TESN.UA_NBR_PKGS - NVL(BK.ACTUAL_NBR_SHIPPED, 0)) > 0 AND VC.GB_CLOSE_SHP_IND = 'Y') or ");
			sb.append("((TESN.UA_NBR_PKGS - NVL(BK.ACTUAL_NBR_SHIPPED, 0)) >= 0 AND VC.GB_CLOSE_SHP_IND = 'N') ");
			sb.append(") ");
			// sb.append("AND BK.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD ");
			// sb.append("AND ESN.CARGO_CATEGORY_CD = CODE.CC_CD ");
			// commeted by hem
			sb.append("AND (ESN.ESN_ASN_NBR IN (:esn_asn_nbr) OR 1 = :E) ");
			sb.append("AND ESN.esn_status ='A' ");
			sb.append("AND (VC.VV_CD =:vv_cd OR 1 = :V) ");
			// sb.append("AND VC.GB_CLOSE_SHP_IND != 'Y' ");
			// sb.append("AND VC.VV_STATUS_IND NOT IN ('CX') ");
			sb.append("AND (VC.VV_STATUS_IND != :S1 OR 1 = :V1) ");
			sb.append("AND VD.VV_CD(+) = VC.VV_CD ");
			sb.append("AND (");
			// ----- JP USER
			sb.append("1 = :JP ");
			// DOC SUB AUTHOR
			sb.append("OR VD.CUST_CD = :f_cust_cd ");
			// ----- GB SHIPPING LINE
			sb.append("OR VC.VSL_OPR_CD = :f_cust_cd ");
			sb.append(")");
			sb.append(" ORDER BY  ");
			sb.append(sort.toUpperCase());
			sb.append(" " + dir.toUpperCase());

			buildParamsGeneralShutoutCargo(filters, paramMap);
			sql = sb.toString();
			log.info("SQl : " + sql + " paramMap: " + paramMap);
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}

			if (criteria.isPaginated() && CommonUtility.deNull(criteria.getPredicates().get("isExcel")).isEmpty()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
			}

			log.info("listGeneralShutoutCargo SQL" + sql + ",ParamMap" + paramMap.toString());

			listGeneralShutoutCargo = namedParameterJdbcTemplate.query(sql, paramMap,
					new BeanPropertyRowMapper<DPECargo>(DPECargo.class));
			for (DPECargo object : listGeneralShutoutCargo) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);

			log.info("listGeneralShutoutCargo Result: " + listGeneralShutoutCargo.size());
		} catch (Exception e) {
			log.info("Exception listVesselByName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO listGeneralShutoutCargo");
		}
		return tableResult;
	}

	// method : sg.com.jp.dpe.dao.DpeCargoJdbcDao-->buildParamsGeneralShutoutCargo
	private void buildParamsGeneralShutoutCargo(Map<String, Object> filters, Map<String, Object> params) {
		log.info("******* START: buildParamsGeneralShutoutCargo :: filters: " + CommonUtility.deNull(filters.toString())
				+ " params: " + CommonUtility.deNull(params.toString()) + " *******");
		if (filters.containsKey("f_esn_asn_nbr") && StringUtils.isNotBlank(filters.get("f_esn_asn_nbr").toString())) {
			String esn_asn_nbrs = filters.get("f_esn_asn_nbr").toString();
			esn_asn_nbrs = StringUtils.replace(esn_asn_nbrs, " ", "");
			List<String> esnAsnList = Arrays.asList(esn_asn_nbrs.split(","));
			if (esnAsnList != null) {
				params.put("esn_asn_nbr", esnAsnList);
			}

			params.put("E", 0);
		} else {
			params.put("E", 1);
			params.put("esn_asn_nbr", "");
		}

		if (filters.containsKey("f_vv_cd") && StringUtils.isNotBlank(filters.get("f_vv_cd").toString())) {
			params.put("vv_cd", filters.get("f_vv_cd").toString());
			params.put("V", 0);
		} else {
			params.put("V", 1);
			params.put("vv_cd", "");
		}

		String companyId = "";
		if (filters.containsKey("f_companyId")) {
			companyId = filters.get("f_companyId").toString();
		}

		String excludesStt;
		if (StringUtils.equalsIgnoreCase("JP", companyId)) {
			// select vessel or input asn_nbr -> allow to search cancelled vessel
			if ((filters.containsKey("f_vv_cd") && StringUtils.isNotBlank(filters.get("f_vv_cd").toString()))
					|| (filters.containsKey("f_esn_asn_nbr")
							&& StringUtils.isNotBlank(filters.get("f_esn_asn_nbr").toString()))) {
				excludesStt = "";
				params.put("S1", excludesStt);
				params.put("V1", 1);
			} else {
				// not allow to search cancelled vessel
				excludesStt = "CX";
				params.put("S1", excludesStt);
				params.put("V1", 0);
			}
		} else {
			if ((filters.containsKey("f_vv_cd") && StringUtils.isNotBlank(filters.get("f_vv_cd").toString()))
					|| (filters.containsKey("f_esn_asn_nbr")
							&& StringUtils.isNotBlank(filters.get("f_esn_asn_nbr").toString()))) {
				excludesStt = "";
				params.put("S1", excludesStt);
				params.put("V1", 1);
			} else {
				// not allow to search cancelled vessel
				excludesStt = "CX";
				params.put("S1", excludesStt);
				params.put("V1", 0);
			}
			// excludesStt = "UB";
			// params.put("S1", excludesStt);
		}

		params.put("f_cust_cd", companyId);
		if (StringUtils.equalsIgnoreCase("JP", companyId)) {
			params.put("JP", 1);
		} else {
			params.put("JP", 0);
		}
		log.info("******* END buildParamsGeneralShutoutCargo *******");
	}

	// method : sg.com.jp.dpe.dao.DpeCargoJdbcDao-->updateShutOutPkgBkDetail
	@Override
	public int updateShutOutPkgBkDetail(String shut_qty, String userId, String bk_ref_nbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		int count = 0;
		try {
			log.info("START: updateShutOutPkgBkDetail shut_qty: " + CommonUtility.deNull(shut_qty) + "userId: "
					+ CommonUtility.deNull(userId) + " bk_ref_nbr: " + CommonUtility.deNull(bk_ref_nbr));
			sb.append(
					" UPDATE BK_DETAILS SET SHUTOUT_QTY = :shutQty, LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :userId ");
			sb.append(" WHERE BK_REF_NBR = :bkRefNbr AND BK_STATUS = 'A' ");
			paramMap.put("shutQty", shut_qty);
			paramMap.put("userId", userId);
			paramMap.put("bkRefNbr", bk_ref_nbr);
			log.info("countGeneralShutoutCargo SQL " + sb.toString() + ",ParamMap" + paramMap.toString());
			count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("count:" + count);
		} catch (Exception e) {
			log.info("Exception updateShutOutPkgBkDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO updateShutOutPkgBkDetail  count: " + count);
		}
		return count;
	}

	// sg.com.jp.dpe.action-->DpeCargoJdbcDao
	// -->loadTransferCargo()
	@Override
	public DPECargo loadTransferCargo(String esn_asn_nbr) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		DPECargo dpeCargo = new DPECargo();
		try {
			log.info("START: loadTransferCargo  DAO  Start Obj :" + CommonUtility.deNull(esn_asn_nbr));

			sb.append(
					"SELECT E.ESN_ASN_NBR esn_asn_nbr ,BK.BK_REF_NBR bk_ref_nbr,NVL(BK.ACTUAL_NBR_SHIPPED,0) actual_nbr_shipped, NVL(BK.SHUTOUT_QTY,0) AS shutout_pkgs, ");
			sb.append(
					"NVL(BK.TRANSFER_PKGS,0) AS transfer_nbr_pkgs, E.TRANS_TYPE trans_type,  VC.VV_CD vv_cd, VC.VSL_NM vsl_nm, VC.OUT_VOY_NBR out_voy_nbr, ");
			sb.append("NVL(ED.UA_NBR_PKGS,0) AS ua_nbr_pkgs, NVL(TESNJPJP.UA_NBR_PKGS,0) AS tesnjj_nbr_pkgs, ");
			sb.append("NVL(TESNPSAJP.UA_NBR_PKGS,0) AS tesnpj_nbr_pkgs,E.ESN_ASN_NBR esn_asn_nbr, ");
			sb.append("TO_CHAR(ED.FIRST_TRANS_DTTM,'DD/MM/YYYY HH24:MI') AS esn_first_trans_dttm, ");
			sb.append("TO_CHAR(TESNJPJP.FIRST_TRANS_DTTM,'DD/MM/YYYY HH24:MI') AS tesnjj_first_trans_dttm, ");
			sb.append("TO_CHAR(TESNPSAJP.FIRST_TRANS_DTTM,'DD/MM/YYYY HH24:MI') AS tesnpjp_first_trans_dttm ");
			sb.append(
					"FROM BK_DETAILS BK,ESN E,ESN_DETAILS ED,TESN_JP_JP TESNJPJP,TESN_PSA_JP TESNPSAJP, VESSEL_CALL VC ");
			sb.append(
					"WHERE BK.BK_REF_NBR = E.BK_REF_NBR AND E.ESN_ASN_NBR = ED.ESN_ASN_NBR(+) AND TESNJPJP.ESN_ASN_NBR(+)= E.ESN_ASN_NBR ");
			sb.append("AND TESNPSAJP.ESN_ASN_NBR(+)= E.ESN_ASN_NBR AND BK.BK_STATUS = 'A' AND E.ESN_STATUS = 'A' ");
			sb.append("AND E.TRANS_TYPE IN('A','E','C') ");
			sb.append("AND VC.VV_CD = E.OUT_VOY_VAR_NBR AND E.ESN_ASN_NBR=:esn_asn_nbr ");

			paramMap.put("esn_asn_nbr", esn_asn_nbr);
			log.info("*** loadTransferCargo SQL *****" + sb.toString() + " paramMap: " + paramMap);
			try {
				dpeCargo = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
						new BeanPropertyRowMapper<DPECargo>(DPECargo.class));

			} catch (EmptyResultDataAccessException e) {
				throw new BusinessException("M4201");
			}

			log.info("END: *** loadTransferCargo  Result*****" + dpeCargo.toString());
		} catch (BusinessException e) {
			log.info("Exception loadTransferCargo : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception loadTransferCargo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: loadTransferCargo  DAO  END");
		}
		return dpeCargo;
	}

	// sg.com.jp.dpe.action -->DpeCargoJdbcDao -->chkBkRefNo()
	@Override
	public boolean chkBkRefNo(String bk_ref_nbr) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		int ret = 0;
		boolean result = false;
		try {
			log.info("START: chkBkRefNo  DAO  Start Obj :" + CommonUtility.deNull(bk_ref_nbr));
			sql = "SELECT count(*) FROM BK_DETAILS WHERE BK_STATUS = 'A' AND BK_REF_NBR = :bk_ref_nbr";
			paramMap.put("bk_ref_nbr", bk_ref_nbr);
			log.info("*** chkBkRefNo SQL *****" + sql.toString() + "paramMap:" + paramMap.toString());
			ret = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
			log.info("ret:" + ret);
			if (ret > 0) {
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			log.info("Exception chkBkRefNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkBkRefNo  DAO  END  result: " + result);
		}

		return result;
	}

	// sg.com.jp.dpe.action -->DpeCargoJdbcDao -->loadGeneralShutoutCargoByESN()
	@Override
	public DPECargo loadGeneralShutoutCargoByESN(String esn_asn_nbr, String trans_type) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Map<String, String> param = new HashMap<String, String>();
		DPECargo dpeCargo = new DPECargo();
		String sql = "";
		try {
			log.info("START: loadGeneralShutoutCargoByESN esn_asn_nbr:" + CommonUtility.deNull(esn_asn_nbr)
					+ "trans_type:" + CommonUtility.deNull(trans_type));
			sb.append("SELECT ");
			sb.append("	VSL_NM, ");
			sb.append("	VV_CD, ");
			sb.append("	OUT_VOY_NBR, ");
			sb.append("	IN_VOY_NBR, ");
			sb.append("	ESN_ASN_NBR, ");
			sb.append("	(CARGO_TYPE || '--' || CRG_TYPE_NM) CARGO_TYPE, ");
			sb.append("	ESN_HS_CODE, ");
			sb.append("	CRG_DES, ");
			sb.append("	MARKINGS, ");
			sb.append("	DECODE(ESN_DG_IND, 'N', 'No', 'Y', 'Yes') ESN_DG_IND, ");
			sb.append("	DECODE(STG_IND, 'O', 'Open', 'C', 'Covered') STG_IND, ");
			sb.append("	PKG_TYPE, ");
			sb.append("	PKG_DESC, ");
			sb.append("	NVL(ESN_PKG, 0) ESN_PKG, ");
			sb.append("	NVL(SHUTOUT_PKG, 0) SHUTOUT_PKG, ");
			sb.append("	NVL(OUTSTANDING_PKG, 0) OUTSTANDING_PKG, ");
			sb.append("	NVL(MAX_EDO_PKG, 0) MAX_EDO_PKG, ");
			sb.append("	ROUND(ESN_PKG*ESN_WT / NBR_PKGS, 2) AS ESNPKG_WT, ");
			sb.append("	ROUND(ESN_PKG*ESN_VOL / NBR_PKGS, 2) AS ESNPKG_VOL, ");
			sb.append("	NVL(ROUND(SHUTOUT_PKG*ESN_WT / NBR_PKGS, 2), 0) AS SHUTOUTPKG_WT, ");
			sb.append("	NVL(ROUND(SHUTOUT_PKG*ESN_VOL / NBR_PKGS, 2), 0) AS SHUTOUTPKG_VOL, ");
			sb.append("	NVL(ROUND(OUTSTANDING_PKG*ESN_WT / NBR_PKGS, 2), 0) AS OUTSTANDING_WT, ");
			sb.append("	NVL(ROUND(OUTSTANDING_PKG*ESN_VOL / NBR_PKGS, 2), 0) AS OUTSTANDING_VOL, ");
			sb.append("	NVL(ROUND(MAX_EDO_PKG*ESN_WT / NBR_PKGS, 2), 0) AS MAX_EDO_PKG_WT, ");
			sb.append("	NVL(ROUND(MAX_EDO_PKG*ESN_VOL / NBR_PKGS, 2), 0) AS MAX_EDO_PKG_VOL ");
			sb.append("FROM ");
			sb.append("	( ");
			sb.append("	SELECT ");
			sb.append(
					"		VC.VSL_NM, VC.VV_CD, VC.OUT_VOY_NBR, VC.IN_VOY_NBR, ESN.ESN_ASN_NBR, BKD.CARGO_TYPE, CGR.CRG_TYPE_NM, ESND.ESN_HS_CODE, ESND.CRG_DES, ESNM.MARKINGS, ESND.ESN_DG_IND, ESND.STG_IND, ESND.PKG_TYPE, PKG.PKG_DESC, ESND.NBR_PKGS, ESND.ESN_WT, ESND.ESN_VOL, ESND.UA_NBR_PKGS AS ESN_PKG, BKD.SHUTOUT_QTY SHUTOUT_PKG, ");
			// sb.append(" ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED AS SHUTOUT_PKG, ");
			// sb.append(" ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED -
			// BKD.SHUTOUT_DELIVERY_PKGS AS OUTSTANDING_PKG, ");
			sb.append(" NVL(BKD.SHUTOUT_QTY, 0)- BKD.SHUTOUT_DELIVERY_PKGS AS OUTSTANDING_PKG, ");
			// sb.append(" ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED -(select
			// NVL(sum(NBR_PKGS),0) FROM GB_EDO WHERE ESN_ASN_NBR=:esn_asn_nbr AND
			// EDO_STATUS <> 'X') AS MAX_EDO_PKG ");
			sb.append(" NVL(BKD.SHUTOUT_QTY, 0) -( ");
			sb.append("		SELECT ");
			sb.append("			NVL(sum(NBR_PKGS), 0) ");
			sb.append("		FROM ");
			sb.append("			GB_EDO ");
			sb.append("		WHERE ");
			sb.append("			ESN_ASN_NBR =:esn_asn_nbr ");
			sb.append("			AND EDO_STATUS <> 'X') AS MAX_EDO_PKG ");
			sb.append("	FROM ");
			sb.append(
					"		VESSEL_CALL VC, ESN ESN, BK_DETAILS BKD, ESN_DETAILS ESND, ESN_MARKINGS ESNM, CRG_TYPE CGR, PKG_TYPES PKG ");
			sb.append("	WHERE ");
			sb.append("		VC.VV_CD = BKD.VAR_NBR ");
			sb.append("		AND CGR.CRG_TYPE_CD = BKD.CARGO_TYPE ");
			sb.append("		AND BKD.BK_REF_NBR = ESN.BK_REF_NBR ");
			sb.append("		AND ESND.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
			sb.append("		AND ESND.PKG_TYPE = PKG.PKG_TYPE_CD ");
			sb.append("		AND ESNM.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
			sb.append("		AND ESN.ESN_ASN_NBR =:esn_asn_nbr )");

			if (StringUtils.equalsIgnoreCase(trans_type, "C")) {
				sb.append("SELECT ");
				sb.append("	VSL_NM, ");
				sb.append("	VV_CD, ");
				sb.append("	IN_VOY_NBR, ");
				sb.append("	OUT_VOY_NBR, ");
				sb.append("	ESN_ASN_NBR, ");
				sb.append("	(CARGO_TYPE || '--' || CRG_TYPE_NM) CARGO_TYPE, ");
				sb.append("	ESN_HS_CODE, ");
				sb.append("	CRG_DES, ");
				sb.append("	MARKINGS, ");
				sb.append("	DECODE(ESN_DG_IND, 'N', 'No', 'Y', 'Yes') ESN_DG_IND, ");
				sb.append("	DECODE(STG_IND, 'O', 'Open', 'C', 'Covered') STG_IND, ");
				sb.append("	PKG_TYPE, ");
				sb.append("	PKG_DESC, ");
				sb.append("	NVL(ESN_PKG, 0) ESN_PKG, ");
				sb.append("	NVL(SHUTOUT_PKG, 0) SHUTOUT_PKG, ");
				sb.append("	NVL(OUTSTANDING_PKG, 0) OUTSTANDING_PKG, ");
				sb.append("	NVL(MAX_EDO_PKG, 0) MAX_EDO_PKG, ");
				sb.append("	NVL(ROUND(ESN_PKG*ESN_WT / NBR_PKGS, 2), 0) AS ESNPKG_WT, ");
				sb.append("	NVL(ROUND(ESN_PKG*ESN_VOL / NBR_PKGS, 2), 0) AS ESNPKG_VOL, ");
				sb.append("	NVL(ROUND(SHUTOUT_PKG*ESN_WT / NBR_PKGS, 2), 0) AS SHUTOUTPKG_WT, ");
				sb.append("	NVL(ROUND(SHUTOUT_PKG*ESN_VOL / NBR_PKGS, 2), 0) AS SHUTOUTPKG_VOL, ");
				sb.append("	NVL(ROUND(OUTSTANDING_PKG*ESN_WT / NBR_PKGS, 2), 0) AS OUTSTANDING_WT, ");
				sb.append("	NVL(ROUND(OUTSTANDING_PKG*ESN_VOL / NBR_PKGS, 2), 0) AS OUTSTANDING_VOL, ");
				sb.append("	NVL(ROUND(MAX_EDO_PKG*ESN_WT / NBR_PKGS, 2), 0) AS MAX_EDO_PKG_WT, ");
				sb.append("	NVL(ROUND(MAX_EDO_PKG*ESN_VOL / NBR_PKGS, 2), 0) AS MAX_EDO_PKG_VOL ");
				sb.append("FROM ");
				sb.append("	( ");
				sb.append("	SELECT ");
				sb.append(
						"		VC.VSL_NM, VC.VV_CD, VC.OUT_VOY_NBR, VC.IN_VOY_NBR, ESN.ESN_ASN_NBR, BKD.CARGO_TYPE, CGR.CRG_TYPE_NM, TESN.HS_CD AS ESN_HS_CODE, TESN.CRG_DES, ESNM.MARKINGS, TESN.DG_IND AS ESN_DG_IND, TESN.STORAGE_IND AS STG_IND, TESN.PKG_TYPE, PKG.PKG_DESC, TESN.NBR_PKGS, TESN.GROSS_WT AS ESN_WT, TESN.GROSS_VOL AS ESN_VOL, TESN.UA_NBR_PKGS AS ESN_PKG, ");
				// sb.append(" TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED AS SHUTOUT_PKG,
				// TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS AS
				// OUTSTANDING_PKG, ");
				sb.append(
						" NVL(BKD.SHUTOUT_QTY, 0) SHUTOUT_PKG, NVL(BKD.SHUTOUT_QTY, 0) - BKD.SHUTOUT_DELIVERY_PKGS AS OUTSTANDING_PKG, ");
				// sb.append(" TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED -(select
				// NVL(sum(NBR_PKGS),0) FROM GB_EDO WHERE ESN_ASN_NBR=:esn_asn_nbr AND
				// EDO_STATUS <> 'X') AS MAX_EDO_PKG ");
				sb.append(" NVL(BKD.SHUTOUT_QTY, 0) -( ");
				sb.append("		SELECT ");
				sb.append("			NVL(sum(NBR_PKGS), 0) ");
				sb.append("		FROM ");
				sb.append("			GB_EDO ");
				sb.append("		WHERE ");
				sb.append("			ESN_ASN_NBR =:esn_asn_nbr ");
				sb.append("			AND EDO_STATUS <> 'X') AS MAX_EDO_PKG ");
				sb.append("	FROM ");
				sb.append(
						"		VESSEL_CALL VC, ESN ESN, BK_DETAILS BKD, TESN_PSA_JP TESN, ESN_MARKINGS ESNM, CRG_TYPE CGR, PKG_TYPES PKG ");
				sb.append("	WHERE ");
				sb.append("		VC.VV_CD = BKD.VAR_NBR ");
				sb.append("		AND CGR.CRG_TYPE_CD = BKD.CARGO_TYPE ");
				sb.append("		AND BKD.BK_REF_NBR = ESN.BK_REF_NBR ");
				sb.append("		AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("		AND TESN.PKG_TYPE = PKG.PKG_TYPE_CD ");
				sb.append("		AND ESNM.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("		AND ESN.ESN_ASN_NBR =:esn_asn_nbr )");
			} else if (StringUtils.equalsIgnoreCase(trans_type, "A")) {

				sb.append("SELECT ");
				sb.append("	VSL_NM, ");
				sb.append("	VV_CD, ");
				sb.append("	IN_VOY_NBR, ");
				sb.append("	OUT_VOY_NBR, ");
				sb.append("	ESN_ASN_NBR, ");
				sb.append("	(CARGO_TYPE || '--' || CRG_TYPE_NM) CARGO_TYPE, ");
				sb.append("	ESN_HS_CODE, ");
				sb.append("	CRG_DES, ");
				sb.append("	MARKINGS, ");
				sb.append("	DECODE(ESN_DG_IND, 'N', 'No', 'Y', 'Yes') ESN_DG_IND, ");
				sb.append("	DECODE(STG_IND, 'O', 'Open', 'C', 'Covered') STG_IND, ");
				sb.append("	PKG_TYPE, ");
				sb.append("	'' PKG_DESC, ");
				sb.append("	NVL(ESN_PKG, 0) ESN_PKG, ");
				sb.append("	NVL(SHUTOUT_PKG, 0) SHUTOUT_PKG, ");
				sb.append("	NVL(OUTSTANDING_PKG, 0) OUTSTANDING_PKG, ");
				sb.append("	NVL(MAX_EDO_PKG, 0) MAX_EDO_PKG, ");
				sb.append("	NVL(ROUND(ESN_PKG*ESN_WT / NBR_PKGS, 2), 0) AS ESNPKG_WT, ");
				sb.append("	NVL(ROUND(ESN_PKG*ESN_VOL / NBR_PKGS, 2), 0) AS ESNPKG_VOL, ");
				sb.append("	NVL(ROUND(SHUTOUT_PKG*ESN_WT / NBR_PKGS, 2), 0) AS SHUTOUTPKG_WT, ");
				sb.append("	NVL(ROUND(SHUTOUT_PKG*ESN_VOL / NBR_PKGS, 2), 0) AS SHUTOUTPKG_VOL, ");
				sb.append("	NVL(ROUND(OUTSTANDING_PKG*ESN_WT / NBR_PKGS, 2), 0) AS OUTSTANDING_WT, ");
				sb.append("	NVL(ROUND(OUTSTANDING_PKG*ESN_VOL / NBR_PKGS, 2), 0) AS OUTSTANDING_VOL, ");
				sb.append("	NVL(ROUND(MAX_EDO_PKG*ESN_WT / NBR_PKGS, 2), 0) AS MAX_EDO_PKG_WT, ");
				sb.append("	NVL(ROUND(MAX_EDO_PKG*ESN_VOL / NBR_PKGS, 2), 0) AS MAX_EDO_PKG_VOL ");
				sb.append("FROM ");
				sb.append("	( ");
				sb.append("	SELECT ");
				sb.append(
						"		VC.VSL_NM, VC.VV_CD, VC.OUT_VOY_NBR, VC.IN_VOY_NBR, ESN.ESN_ASN_NBR, BKD.CARGO_TYPE, CGR.CRG_TYPE_NM, '' AS ESN_HS_CODE, '' CRG_DES, '' MARKINGS, '' ESN_DG_IND, '' STG_IND, '' PKG_TYPE, TESN.NBR_PKGS, TESN.NOM_WT AS ESN_WT, TESN.NOM_VOL AS ESN_VOL, TESN.UA_NBR_PKGS AS ESN_PKG, ");
				// sb.append(" TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED AS SHUTOUT_PKG,
				// TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS AS
				// OUTSTANDING_PKG, ");
				sb.append(
						" NVL(BKD.SHUTOUT_QTY, 0) AS SHUTOUT_PKG, NVL(BKD.SHUTOUT_QTY, 0) - BKD.SHUTOUT_DELIVERY_PKGS AS OUTSTANDING_PKG, ");
				// sb.append(" TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED -(select
				// NVL(sum(NBR_PKGS),0) FROM GB_EDO WHERE ESN_ASN_NBR=:esn_asn_nbr AND
				// EDO_STATUS <> 'X') AS MAX_EDO_PKG ");
				sb.append(" NVL(BKD.SHUTOUT_QTY, 0) -( ");
				sb.append("		SELECT ");
				sb.append("			NVL(sum(NBR_PKGS), 0) ");
				sb.append("		FROM ");
				sb.append("			GB_EDO ");
				sb.append("		WHERE ");
				sb.append("			ESN_ASN_NBR =:esn_asn_nbr ");
				sb.append("			AND EDO_STATUS <> 'X') AS MAX_EDO_PKG ");
				sb.append("	FROM ");
				sb.append("		VESSEL_CALL VC, ESN ESN, BK_DETAILS BKD, TESN_JP_JP TESN, CRG_TYPE CGR ");
				sb.append("	WHERE ");
				sb.append("		VC.VV_CD = BKD.VAR_NBR ");
				sb.append("		AND CGR.CRG_TYPE_CD = BKD.CARGO_TYPE ");
				sb.append("		AND BKD.BK_REF_NBR = ESN.BK_REF_NBR ");
				sb.append("		AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("		AND ESN.ESN_ASN_NBR =:esn_asn_nbr )");

			}
			sql = sb.toString();
			param.put("esn_asn_nbr", esn_asn_nbr);
			log.info("loadGeneralShutoutCargoByESN SQL" + sql + " ,ParamMap" + param.toString());
			try {
				dpeCargo = namedParameterJdbcTemplate.queryForObject(sql, param,
						new BeanPropertyRowMapper<DPECargo>(DPECargo.class));
			} catch (EmptyResultDataAccessException e) {
				return dpeCargo;
			}
			log.info("loadGeneralShutoutCargoByESN Result" + dpeCargo.toString());
		} catch (Exception e) {
			log.info("Exception loadGeneralShutoutCargoByESN : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO loadGeneralShutoutCargoByESN");
		}

		return dpeCargo;
	}

	// sg.com.jp.dpe.action -->DpeCargoJdbcDao -->getAdpByAdpIc()
	@Override
	public DPEUtil getAdpNmByAdpIc(String adpIc) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		DPEUtil result = new DPEUtil();

		try {
			log.info("START: DAO getAdpNmByAdpIc name:" + CommonUtility.deNull(adpIc));
			sb.append("SELECT distinct cust.CUST_CD CO_CD, CO_NM FROM CUSTOMER cust");
			sb.append(" LEFT JOIN COMPANY_CODE cc on cust.cust_cd = cc.co_cd");
			sb.append(" WHERE TDB_CR_NBR = :adpIc OR UEN_NBR = :adpIc");
			params.put("adpIc", adpIc);

			log.info("SQL: " + sb.toString() + " params: " + params.toString());

			List<DPEUtil> tmp = null;
			tmp = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			log.info("tmp" + tmp.toString());

			if (tmp != null) {
				if (tmp.size() > 0) {
//					return (DPEUtil) tmp.get(0);
					result = (DPEUtil) tmp.get(0);
				}
			}
			
		} catch (Exception e) {
			log.info("Exception getAdpNmByAdpIc : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("End getAdpNmByAdpIc dao  result: " + result.toString());
		}

//		return new DPEUtil();
		return result;
	}

	// sg.com.jp.dpe.action -->DpeCargoJdbcDao -->listShutoutCargoEDO()
	@Override
	public TableResult listShutoutCargoEDO(String esn_asn_nbr, String sort, String dir, Criteria criteria)
			throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		List<DPECargo> listShutoutCargo = null;
		try {
			log.info("START: DAO listShutoutCargoEDO esn_asn_nbr: " + CommonUtility.deNull(esn_asn_nbr) + ",sort: "
					+ CommonUtility.deNull(sort) + ",dir: " + CommonUtility.deNull(dir) + ",criteria: "
					+ CommonUtility.deNull(criteria.toString()));

			sb.append(
					"SELECT (VC.VSL_NM || '/' || VC.OUT_VOY_NBR) VSL_NM,VC.TERMINAL, VC.SCHEME, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN NVL(VC.COMBI_GC_SCHEME,'') ELSE '' END SUBSCHEME,edo.esn_asn_nbr, ");
			sb.append("  edo.edo_asn_nbr, ");
			sb.append("  edo.nbr_pkgs AS nbr_pkgs, ");
			sb.append("  edo.crg_status, ");
			sb.append("  EDO.ADP_NM, ");
			sb.append("  NVL(edo.dn_nbr_pkgs, 0)       AS dn_nbr_pkgs, ");
			sb.append("  NVL(edo.trans_dn_nbr_pkgs, 0) AS trans_dn_nbr_pkgs, ");
			sb.append("  NVL(edo.trans_nbr_pkgs, 0)    AS trans_nbr_pkgs, ");
			sb.append("  /*NVL(esnd.ua_nbr_pkgs - bkd.actual_nbr_shipped, 0) AS shutout_pkgs,*/ ");
			sb.append("  NVL(BKD.SHUTOUT_QTY,0) AS shutout_pkgs, ");
			sb.append(
					"  NVL(esnd.ua_nbr_pkgs - bkd.actual_nbr_shipped - bkd.shutout_delivery_pkgs, 0) AS balance_to_load, ");
			sb.append("  0 AS loaded_pkgs, ");
			sb.append("  esnd.crg_des, ");
			sb.append("  bkd.cargo_type, ");
			sb.append("  esnd.trucker_nm, ");
			sb.append("  edo.edo_status, ");
			sb.append("  esn.trans_type ");
			sb.append("FROM gb_edo edo, ");
			sb.append("  esn, ");
			sb.append("  esn_details esnd, ");
			sb.append("  bk_details bkd, ");
			sb.append("  vessel_call vc ");
			sb.append("WHERE edo.edo_status = 'A' ");
			sb.append("AND edo.esn_asn_nbr IS NOT NULL ");
			sb.append("AND edo.shutout_ind  = 'Y' ");
			sb.append("AND edo.esn_asn_nbr = esn.esn_asn_nbr ");
			sb.append("AND bkd.bk_ref_nbr = esn.bk_ref_nbr ");
			sb.append("AND esnd.esn_asn_nbr = edo.esn_asn_nbr ");
			sb.append("AND edo.var_nbr      = vc.vv_cd ");
			sb.append("AND edo.esn_asn_nbr = :esn_asn_nbr ");
			sb.append(" ");
			sb.append("UNION ALL ");
			sb.append(
					"SELECT (VC.VSL_NM || '/' || VC.OUT_VOY_NBR) VSL_NM,VC.TERMINAL, VC.SCHEME, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN NVL(VC.COMBI_GC_SCHEME,'') ELSE '' END SUBSCHEME, edo.esn_asn_nbr, ");
			sb.append("  edo.edo_asn_nbr, ");
			sb.append("  edo.nbr_pkgs AS nbr_pkgs, ");
			sb.append("  edo.crg_status, ");
			sb.append("  EDO.ADP_NM, ");
			sb.append("  NVL(edo.dn_nbr_pkgs, 0)       AS dn_nbr_pkgs, ");
			sb.append("  NVL(edo.trans_dn_nbr_pkgs, 0) AS trans_dn_nbr_pkgs, ");
			sb.append("  NVL(edo.trans_nbr_pkgs, 0)    AS trans_nbr_pkgs, ");
			sb.append("  /*NVL(esnd.ua_nbr_pkgs - bkd.actual_nbr_shipped, 0) AS shutout_pkgs,*/ ");
			sb.append("  NVL(BKD.SHUTOUT_QTY,0) AS shutout_pkgs, ");
			sb.append(
					"  /*NVL(esnd.ua_nbr_pkgs - bkd.actual_nbr_shipped - bkd.shutout_delivery_pkgs, 0) AS balance_to_load,*/ ");
			sb.append("  (NVL(BKD.SHUTOUT_QTY,0) - NVL(bkd.shutout_delivery_pkgs, 0)) AS balance_to_load, ");
			sb.append("  0 AS loaded_pkgs, ");
			sb.append("  '' crg_des, ");
			sb.append("  bkd.cargo_type, ");
			sb.append("  '' trucker_nm, ");
			sb.append("  edo.edo_status, ");
			sb.append("  esn.trans_type ");
			sb.append("FROM gb_edo edo, ");
			sb.append("  esn, ");
			sb.append("  tesn_jp_jp esnd, ");
			sb.append("  bk_details bkd, ");
			sb.append("  vessel_call vc ");
			sb.append("WHERE edo.edo_status = 'A' ");
			sb.append("AND edo.esn_asn_nbr IS NOT NULL ");
			sb.append("AND edo.shutout_ind  = 'Y' ");
			sb.append("AND edo.esn_asn_nbr = esn.esn_asn_nbr ");
			sb.append("AND bkd.bk_ref_nbr = esn.bk_ref_nbr ");
			sb.append("AND esnd.esn_asn_nbr = edo.esn_asn_nbr ");
			sb.append("AND edo.var_nbr      = vc.vv_cd ");
			sb.append("AND edo.esn_asn_nbr =:esn_asn_nbr ");
			sb.append(" ");
			sb.append("UNION ALL ");
			sb.append(
					"SELECT (VC.VSL_NM || '/' || VC.OUT_VOY_NBR) VSL_NM,VC.TERMINAL, VC.SCHEME, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN NVL(VC.COMBI_GC_SCHEME,'') ELSE '' END SUBSCHEME, edo.esn_asn_nbr, ");
			sb.append("  edo.edo_asn_nbr, ");
			sb.append("  edo.nbr_pkgs AS nbr_pkgs, ");
			sb.append("  edo.crg_status, ");
			sb.append("  EDO.ADP_NM, ");
			sb.append("  NVL(edo.dn_nbr_pkgs, 0)       AS dn_nbr_pkgs, ");
			sb.append("  NVL(edo.trans_dn_nbr_pkgs, 0) AS trans_dn_nbr_pkgs, ");
			sb.append("  NVL(edo.trans_nbr_pkgs, 0)    AS trans_nbr_pkgs, ");
			sb.append("  /*NVL(esnd.ua_nbr_pkgs - bkd.actual_nbr_shipped, 0) AS shutout_pkgs,*/ ");
			sb.append("  NVL(BKD.SHUTOUT_QTY,0) AS shutout_pkgs, ");
			sb.append(
					"  /*NVL(esnd.ua_nbr_pkgs - bkd.actual_nbr_shipped - bkd.shutout_delivery_pkgs, 0) AS balance_to_load,*/ ");
			sb.append("  (NVL(BKD.SHUTOUT_QTY,0) - NVL(bkd.shutout_delivery_pkgs, 0)) AS balance_to_load, ");
			sb.append("  0 AS loaded_pkgs, ");
			sb.append("  esnd.crg_des, ");
			sb.append("  bkd.cargo_type, ");
			sb.append("  esnd.trucker_nm, ");
			sb.append("  edo.edo_status, ");
			sb.append("  esn.trans_type ");
			sb.append("FROM gb_edo edo, ");
			sb.append("  esn, ");
			sb.append("  tesn_psa_jp esnd, ");
			sb.append("  bk_details bkd, ");
			sb.append("  vessel_call vc ");
			sb.append("WHERE edo.edo_status = 'A' ");
			sb.append("AND edo.esn_asn_nbr IS NOT NULL ");
			sb.append("AND edo.shutout_ind  = 'Y' ");
			sb.append("AND edo.esn_asn_nbr = esn.esn_asn_nbr ");
			sb.append("AND bkd.bk_ref_nbr = esn.bk_ref_nbr ");
			sb.append("AND esnd.esn_asn_nbr = edo.esn_asn_nbr ");
			sb.append("AND edo.var_nbr      = vc.vv_cd ");
			sb.append("AND edo.esn_asn_nbr =:esn_asn_nbr");
			params.put("esn_asn_nbr", esn_asn_nbr);
			log.info("SQL: " + sb.toString() + " params: " + params.toString());
			String sql = sb.toString();
			/*
			 * if (criteria.isPaginated()) { tableData.setTotal(namedParameterJdbcTemplate.
			 * queryForObject("SELECT COUNT(*) FROM (" + sql + ")", params, Integer.class));
			 * log.info("filter.total=" + tableData.getTotal()); }
			 */

			tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")", params,
					Integer.class));
			log.info("filter.total=" + tableData.getTotal());

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
			}
			log.info("listShutoutCargoEDO sql : " + sql + " params: " + params);
			listShutoutCargo = namedParameterJdbcTemplate.query(sql, params,
					new BeanPropertyRowMapper<DPECargo>(DPECargo.class));

			for (DPECargo object : listShutoutCargo) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			log.info("listShutoutCargoEDO Result: " + listShutoutCargo.size());
		} catch (Exception e) {
			log.info("Exception listShutoutCargoEDO : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("listShutoutCargoEDO DAO END  tableResult: " + tableResult.toString());
		}

		return tableResult;
	}

	// sg.com.jp.dpe.action -->DpeCargoJdbcDao -->listTransferCargo()
	@Override
	public TableResult listTransferCargo(String esn_asn_nbr, String sort, String dir, Criteria criteria)
			throws BusinessException {

		StringBuilder sb = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		List<DPECargo> listShutoutCargo = null;
		try {
			log.info("START: DAO listTransferCargo esn_asn_nbr: " + CommonUtility.deNull(esn_asn_nbr) + ",sort:"
					+ CommonUtility.deNull(sort) + ",dir:" + CommonUtility.deNull(dir) + ",criteria:"
					+ CommonUtility.deNull(criteria.toString()));

			sb.append("/* Formatted on 05-11-2020 15:36:51 (QP5 v5.336) */ ");
			sb.append("SELECT DISTINCT ");
			sb.append("       (VC_OLD.VSL_NM || ' / ' || VC_OLD.OUT_VOY_NBR)    cc_name, ");
			sb.append("       VC_OLD.TERMINAL                                   AS fromTerminal, ");
			sb.append("       CASE ");
			sb.append("           WHEN VC_OLD.COMBI_GC_OPS_IND = 'Y' ");
			sb.append("           THEN ");
			sb.append("               (VC_OLD.SCHEME || '/' || NVL (VC_OLD.COMBI_GC_SCHEME, '')) ");
			sb.append("           ELSE ");
			sb.append("               VC_OLD.SCHEME ");
			sb.append("       END                                               AS fromScheme, ");
			sb.append("       ESN_NEW.ESN_ASN_NBR, ");
			sb.append("       BK_NEW.BK_REF_NBR, ");
			sb.append("       NVL (BK_NEW.ACTUAL_NBR_SHIPPED, 0), ");
			sb.append("       NVL (BK_NEW.SHUTOUT_QTY, 0)                       AS SHUTOUT_PKGS, ");
			sb.append("       ESN_NEW.TRANS_TYPE, ");
			sb.append("       (VC_NEW.VSL_NM || '/' || VC_NEW.OUT_VOY_NBR)      VSL_NM, ");
//			sb.append("       /*(CASE ");
//			sb.append("                --  WHEN ESN_NEW.TRANS_TYPE ='E' THEN  NVL(ED.UA_NBR_PKGS,0) ");
//			sb.append("                --  WHEN ESN_NEW.TRANS_TYPE ='A' THEN  NVL(TESNJPJP.UA_NBR_PKGS,0) ");
//			sb.append("                --  WHEN ESN_NEW.TRANS_TYPE ='C' THEN  NVL(TESNPSAJP.UA_NBR_PKGS,0) ");
//			sb.append("                --  ELSE 0 END)  NBR_PKGS , */ ");
			sb.append("       NVL (BK_NEW.bk_nbr_pkgs, 0)                       AS NBR_PKGS, ");
			sb.append("       (CASE ");
			sb.append("            WHEN ESN_NEW.TRANS_TYPE = 'E' ");
			sb.append("            THEN ");
			sb.append("                TO_CHAR (ED.FIRST_TRANS_DTTM, 'DD/MM/YYYY HH24:MI') ");
			sb.append("            WHEN ESN_NEW.TRANS_TYPE = 'A' ");
			sb.append("            THEN ");
			sb.append("                TO_CHAR (TESNJPJP.FIRST_TRANS_DTTM, 'DD/MM/YYYY HH24:MI') ");
			sb.append("            WHEN ESN_NEW.TRANS_TYPE = 'C' ");
			sb.append("            THEN ");
			sb.append("                TO_CHAR (TESNPSAJP.FIRST_TRANS_DTTM, 'DD/MM/YYYY HH24:MI') ");
			sb.append("            ELSE ");
			sb.append("                NULL ");
			sb.append("        END)                                             FIRST_TRANS_DTTM, ");
			sb.append("       VC_NEW.TERMINAL                                   AS toTerminal, ");
			sb.append("       CASE ");
			sb.append("           WHEN VC_NEW.COMBI_GC_OPS_IND = 'Y' ");
			sb.append("           THEN ");
			sb.append("               (VC_NEW.SCHEME || '/' || NVL (VC_NEW.COMBI_GC_SCHEME, '')) ");
			sb.append("           ELSE ");
			sb.append("               VC_NEW.SCHEME ");
			sb.append("       END                                               AS toScheme ");
			sb.append("  FROM ESN          ESN_NEW, ");
			sb.append("       BK_DETAILS   BK_NEW, ");
			sb.append("       ESN          ESN_ORG, ");
			sb.append("       BK_DETAILS   BK_OLD, ");
			sb.append("       VESSEL_CALL  VC_NEW, ");
			sb.append("       ESN_DETAILS  ED, ");
			sb.append("       TESN_JP_JP   TESNJPJP, ");
			sb.append("       TESN_PSA_JP  TESNPSAJP, ");
			sb.append("       VESSEL_CALL  VC_OLD ");
			sb.append(" WHERE     ESN_NEW.BK_REF_NBR = BK_NEW.BK_REF_NBR ");
			sb.append("       AND BK_NEW.OLD_BK_REF = BK_OLD.BK_REF_NBR ");
			sb.append("       AND ESN_ORG.BK_REF_NBR = BK_OLD.BK_REF_NBR ");
			sb.append("       AND VC_NEW.VV_CD = ESN_NEW.OUT_VOY_VAR_NBR ");
			sb.append("       AND BK_OLD.VAR_NBR = BK_NEW.BK_ORIGINAL_VAR_NBR ");
			sb.append("       AND VC_OLD.VV_CD = ESN_ORG.OUT_VOY_VAR_NBR ");
			sb.append("       AND ESN_NEW.ESN_ASN_NBR = ED.ESN_ASN_NBR(+) ");
			sb.append("       AND TESNJPJP.ESN_ASN_NBR(+) = ESN_NEW.ESN_ASN_NBR ");
			sb.append("       AND TESNPSAJP.ESN_ASN_NBR(+) = ESN_NEW.ESN_ASN_NBR ");
			sb.append("       AND ESN_ORG.ESN_ASN_NBR = :esn_asn_nbr");

			params.put("esn_asn_nbr", esn_asn_nbr);
			String sql = sb.toString();
			log.info("SQL: " + sql + " params: " + params.toString());

			tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")", params,
					Integer.class));
			log.info("filter.total=" + tableData.getTotal());

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
			}

			listShutoutCargo = namedParameterJdbcTemplate.query(sql, params,
					new BeanPropertyRowMapper<DPECargo>(DPECargo.class));
			log.info("listShutoutCargo: " + listShutoutCargo);
			for (DPECargo object : listShutoutCargo) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
		} catch (Exception e) {
			log.info("Exception listTransferCargo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("listTransferCargo DAO END  tableResult: " + tableResult.toString());
		}

		return tableResult;
	}

	// sg.com.jp.dpe.action -->DpeCargoJdbcDao -->loadTransferCargoForView()
	@Override
	public DPECargo loadTransferCargoForView(String esn_asn_nbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		DPECargo dpeCargo = null;
		try {
			log.info("START: DAO loadTransferCargoForView esn_asn_nbr:" + CommonUtility.deNull(esn_asn_nbr));
			sb.append(
					"SELECT E.ESN_ASN_NBR,BK.BK_REF_NBR, BK.OLD_BK_REF, NVL(BK.ACTUAL_NBR_SHIPPED,0) ACTUAL_NBR_SHIPPED, NVL(BK.SHUTOUT_QTY,0) AS SHUTOUT_PKGS, NVL(BK.TRANSFER_PKGS,0) AS TRANSFER_NBR_PKGS, E.TRANS_TYPE, ");
			sb.append(
					"VC.VV_CD, VC.VSL_NM, VC.OUT_VOY_NBR, E.ESN_ASN_NBR, VC_OLD.VSL_NM FR_VSL_NM, VC_OLD.OUT_VOY_NBR FR_OUT_VOY_NBR, BK.BK_NBR_PKGS, ");
			sb.append("(CASE WHEN E.TRANS_TYPE ='A' ");
			sb.append("	THEN NVL(TESNJPJP.UA_NBR_PKGS,0) ");
			sb.append("	WHEN E.TRANS_TYPE = 'E' ");
			sb.append("	THEN NVL(ED.UA_NBR_PKGS,0) ");
			sb.append("	WHEN E.TRANS_TYPE = 'C' ");
			sb.append("	THEN NVL(TESNPSAJP.UA_NBR_PKGS,0) END) AS UA_NBR_PKGS, ");
			sb.append("(CASE WHEN E.TRANS_TYPE ='A' ");
			sb.append("	THEN TO_CHAR(TESNJPJP.FIRST_TRANS_DTTM,'DD/MM/YYYY HH24:MI') ");
			sb.append("	WHEN E.TRANS_TYPE = 'E' ");
			sb.append("	THEN TO_CHAR(ED.FIRST_TRANS_DTTM,'DD/MM/YYYY HH24:MI') ");
			sb.append("	WHEN E.TRANS_TYPE = 'C' ");
			sb.append("	THEN TO_CHAR(TESNPSAJP.FIRST_TRANS_DTTM,'DD/MM/YYYY HH24:MI') END) AS FIRST_TRANS_DTTM, ");
			sb.append(
					"	VC_OLD.TERMINAL AS fromTerminal,CASE WHEN VC_OLD.COMBI_GC_OPS_IND ='Y' THEN (VC_OLD.SCHEME ||'/' || NVL(VC_OLD.COMBI_GC_SCHEME,'')) ELSE VC_OLD.SCHEME END AS fromScheme, ");
			sb.append(
					"	VC.TERMINAL AS toTerminal, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (VC.SCHEME ||'/' || NVL(VC.COMBI_GC_SCHEME,'')) ELSE VC.SCHEME END AS toScheme ");
			sb.append(
					"FROM BK_DETAILS BK,ESN E,ESN_DETAILS ED,TESN_JP_JP TESNJPJP,TESN_PSA_JP TESNPSAJP, VESSEL_CALL VC, ");
			sb.append("VESSEL_CALL VC_OLD ");
			sb.append(
					"WHERE BK.BK_REF_NBR = E.BK_REF_NBR AND E.ESN_ASN_NBR = ED.ESN_ASN_NBR(+) AND TESNJPJP.ESN_ASN_NBR(+)= E.ESN_ASN_NBR ");
			sb.append(
					"AND TESNPSAJP.ESN_ASN_NBR(+)= E.ESN_ASN_NBR AND BK.BK_STATUS = 'A' AND E.ESN_STATUS = 'A' AND E.TRANS_TYPE IN('A','E','C') ");
			sb.append("AND BK.BK_ORIGINAL_VAR_NBR(+) = VC_OLD.VV_CD ");
			sb.append("AND VC.VV_CD = E.OUT_VOY_VAR_NBR AND E.ESN_ASN_NBR=:esn_asn_nbr");
			paramMap.put("esn_asn_nbr", esn_asn_nbr);
			log.info("SQL" + sb.toString() + "paramMap:" + paramMap.toString());
			dpeCargo = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
					new BeanPropertyRowMapper<DPECargo>(DPECargo.class));
		} catch (Exception e) {
			log.info("Exception loadTransferCargoForView :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:loadTransferCargoForView DAO  dpeCargo: " + dpeCargo.toString());
		}

		return dpeCargo;
	}

	// added by syazwani on 27/04/2021
	@Override
	public String getCompanyName(String coCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		String companyName = "";
		try {
			log.info("START: getCompanyName coCd:" + CommonUtility.deNull(coCd));
			sb.append(" SELECT CO_NM AS coNm FROM tops.COMPANY_CODE WHERE CO_CD LIKE :coCd ");
			paramMap.put("coCd", coCd);
			log.info("SQL" + sb.toString() + " paramMap : " + paramMap);
			companyName = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);

		} catch (Exception e) {
			log.info("Exception getCompanyName :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCompanyName  companyName: " + companyName);
		}
		return companyName;
	}

}
