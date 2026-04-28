package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.EdoRepository;
import sg.com.jp.generalcargo.domain.AdpValueObject;
import sg.com.jp.generalcargo.domain.ContainerDetails;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CutoffValueObject;
import sg.com.jp.generalcargo.domain.DPECargo;
import sg.com.jp.generalcargo.domain.EdoDetails;
import sg.com.jp.generalcargo.domain.EdoJpBilling;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TextParaVO;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.domain.vesselVoyObjectValue;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.DpeCommonUtil;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;

@Repository
public class EdoJdbcRepository implements EdoRepository {
	final static String TEXT_PARA_GC_VIEW_EDO = "GC_V_EDO";
	public String logStatusGlobal = "Y";
	private static final Log log = LogFactory.getLog(EdoJdbcRepository.class);
	private static final String param = "  paramMap = ";

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private boolean isShowEdoInfo(String companyCode, TextParaVO result) {
		log.info("START: isShowEdoInfo  DAO  Start Obj "+" companyCode:"+companyCode +" result:"+result);
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
		return false;
	}
	
	@Override
	public List<EdoValueObjectCargo> getOutStandingList(String vvCd) throws BusinessException{
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		StringBuffer sb = new StringBuffer();
		List<EdoValueObjectCargo> edolist = new ArrayList<EdoValueObjectCargo>();
		try {
			log.info("START: getOutStandingList  DAO  Start Obj "+" vvCd:"+vvCd );

			sb.append(" SELECT VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND, vc.vsl_nm, vc.out_voy_nbr, esn.esn_asn_nbr,");
			sb.append("       bkd.bk_ref_nbr, bkd.shipper_nm, esnd.crg_des, esnd.ua_nbr_pkgs,");
			sb.append("       esnd.esn_wt, esnd.esn_vol, esn.stuff_ind,");
			sb.append("       esnd.ua_nbr_pkgs - bkd.actual_nbr_shipped AS shutoutpkg,");
			sb.append("       esnd.ua_nbr_pkgs - bkd.actual_nbr_shipped - bkd.shutout_delivery_pkgs AS outstandingpkg");
			sb.append("  FROM vessel_call vc,");
			sb.append("       esn esn,");
			sb.append("       bk_details bkd,");
			sb.append("       esn_details esnd,");
			sb.append("       esn_markings esnm");
			sb.append(" WHERE vc.vv_cd = bkd.var_nbr");
			sb.append("   AND bkd.bk_ref_nbr = esn.bk_ref_nbr");
			sb.append("   AND esnd.esn_asn_nbr = esn.esn_asn_nbr");
			sb.append("   AND esnm.esn_asn_nbr = esn.esn_asn_nbr");
			sb.append("   AND (esnd.ua_nbr_pkgs - bkd.actual_nbr_shipped - bkd.shutout_delivery_pkgs)>0");
			sb.append("   AND vc.vv_cd = :vvCd");
			sb.append(" UNION ");
			sb.append(" SELECT VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND, vc.vsl_nm, vc.out_voy_nbr, esn.esn_asn_nbr,");
			sb.append("       bkd.bk_ref_nbr, bkd.shipper_nm, tesn.crg_des, tesn.ua_nbr_pkgs,");
			sb.append("       tesn.gross_wt AS esn_wt, tesn.gross_vol AS esn_vol, esn.stuff_ind,");
			sb.append("       tesn.ua_nbr_pkgs - bkd.actual_nbr_shipped AS shutoutpkg,");
			sb.append("       tesn.ua_nbr_pkgs - bkd.actual_nbr_shipped - bkd.shutout_delivery_pkgs AS outstandingpkg");
			sb.append("  FROM vessel_call vc,");
			sb.append("       esn esn,");
			sb.append("       bk_details bkd,");
			sb.append("       tesn_psa_jp tesn,");
			sb.append("       esn_markings esnm");
			sb.append(" WHERE vc.vv_cd = bkd.var_nbr");
			sb.append("   AND bkd.bk_ref_nbr = esn.bk_ref_nbr");
			sb.append("   AND tesn.esn_asn_nbr = esn.esn_asn_nbr");
			sb.append("   AND esnm.esn_asn_nbr = esn.esn_asn_nbr");
			sb.append("   AND (tesn.ua_nbr_pkgs - bkd.actual_nbr_shipped - bkd.shutout_delivery_pkgs)>0");
			sb.append("   AND vc.vv_cd = :vvCd");

			paramMap.put("vvCd", vvCd);
			log.info(" *** getOutStandingList params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
				//String edoasnnbr = CommonUtility.deNull(""+rs.getInt("EDO_ASN_NBR"));
				String terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				String scheme = CommonUtility.deNull(rs.getString("SCHEME"));
				String subScheme = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				String gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));
				String vcNm = CommonUtility.deNull(rs.getString("VSL_NM"));
				String outVoyNbr = CommonUtility.deNull(rs.getString("OUT_VOY_NBR"));
				String esnAsnNbr = CommonUtility.deNull(rs.getString("ESN_ASN_NBR"));
				String bkNbr = CommonUtility.deNull(rs.getString("BK_REF_NBR"));
				String shipperNm = CommonUtility.deNull(rs.getString("SHIPPER_NM"));
				String crgDes = CommonUtility.deNull(rs.getString("CRG_DES"));
				String esnPkgs = CommonUtility.deNull(rs.getString("UA_NBR_PKGS"));
				String esnWt = CommonUtility.deNull(rs.getString("ESN_WT"));
				BigDecimal esnWtB = new BigDecimal(esnWt).setScale(2,RoundingMode.HALF_UP);
				String esnVol = CommonUtility.deNull(rs.getString("ESN_VOL"));
				BigDecimal esnVolB = new BigDecimal(esnVol).setScale(2,RoundingMode.HALF_UP);
				String shutoutPkgs = CommonUtility.deNull(rs.getString("SHUTOUTPKG"));
				String outStandingPkgs = CommonUtility.deNull(rs.getString("OUTSTANDINGPKG"));
				String stuffInd = CommonUtility.deNull(rs.getString("STUFF_IND"));

				//edoValueObject.setEdoAsnNbr(edoasnnbr);
				edoValueObject.setTerminal(terminal);
				edoValueObject.setScheme(scheme);
				edoValueObject.setSubScheme(subScheme);
				edoValueObject.setGcOperations(gcOperations);
				edoValueObject.setVslNm(vcNm);
				edoValueObject.setOutVoyNbr(outVoyNbr);
				edoValueObject.setEsnAsnNbr(esnAsnNbr);
				edoValueObject.setBkNbr(bkNbr);
				edoValueObject.setShipperNm(shipperNm);
				edoValueObject.setCrgDes(crgDes);
				edoValueObject.setEsnpkgs(esnPkgs);
				edoValueObject.setEsnpkgs_wt(String.valueOf(esnWtB));
				edoValueObject.setEsnpkgs_vol(String.valueOf(esnVolB));
				edoValueObject.setShutoutpkgs(shutoutPkgs);
				edoValueObject.setOutstandingpkgs(outStandingPkgs);
				edoValueObject.setStuffInd(stuffInd);

				edolist.add(edoValueObject);
			}

			log.info("END: *** getOutStandingList Result *****" + edolist.toString());
		} catch (NullPointerException e) {
			log.info("Exception getOutStandingList :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getOutStandingList :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getOutStandingList  DAO  END");
		}
		return edolist;
	};

	@Override
	public List<List<String>> getEsnList(String vvCd) throws BusinessException{
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<String> mapEsn = new ArrayList<String>();

		List<List<String>> esnVector = new ArrayList<List<String>>();
		try {
			log.info("START: getEsnList  DAO  Start Obj "+" vvCd:"+vvCd );


			sb.append(" SELECT vc.vsl_nm, vc.out_voy_nbr, esn.esn_asn_nbr");
			sb.append("  FROM vessel_call vc,");
			sb.append("       esn esn,");
			sb.append("       bk_details bkd,");
			sb.append("       esn_details esnd,");
			sb.append("       esn_markings esnm");
			sb.append(" WHERE vc.vv_cd = bkd.var_nbr");
			sb.append("   AND bkd.bk_ref_nbr = esn.bk_ref_nbr");
			sb.append("   AND esnd.esn_asn_nbr = esn.esn_asn_nbr");
			sb.append("   AND esnm.esn_asn_nbr = esn.esn_asn_nbr");
			sb.append("   AND (esnd.ua_nbr_pkgs - bkd.actual_nbr_shipped - bkd.shutout_delivery_pkgs)>0");
			sb.append("   AND vc.vv_cd = :vvCd");
			sb.append(" UNION ");
			sb.append(" SELECT vc.vsl_nm, vc.out_voy_nbr, esn.esn_asn_nbr");
			sb.append("  FROM vessel_call vc,");
			sb.append("       esn esn,");
			sb.append("       bk_details bkd,");
			sb.append("       tesn_psa_jp tesn,");
			sb.append("       esn_markings esnm");
			sb.append(" WHERE vc.vv_cd = bkd.var_nbr");
			sb.append("   AND bkd.bk_ref_nbr = esn.bk_ref_nbr");
			sb.append("   AND tesn.esn_asn_nbr = esn.esn_asn_nbr");
			sb.append("   AND esnm.esn_asn_nbr = esn.esn_asn_nbr");
			sb.append("   AND (tesn.ua_nbr_pkgs - bkd.actual_nbr_shipped - bkd.shutout_delivery_pkgs)>0");
			sb.append("   AND vc.vv_cd = :vvCd");

			paramMap.put("vvCd", vvCd);

			log.info(" *** getEsnList SQL *****" + sb.toString());

			log.info(" *** getEsnList params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				String esnNo = CommonUtility.deNull(rs.getString("ESN_ASN_NBR"));
				String vslNm = CommonUtility.deNull(rs.getString("VSL_NM"));
				mapEsn.add(esnNo);
				mapEsn.add(vslNm);
				esnVector.add(mapEsn);

			}
			log.info("END: *** getEsnList Result *****" + esnVector.toString());
			
		} catch (NullPointerException e) {
			log.info("Exception getEsnList :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnList :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnList  DAO  END");
		}
		return esnVector;
	}

	@Override
	public List<EdoValueObjectCargo> getShutoutAddDetail(String esnAsnNo) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<EdoValueObjectCargo> v = new ArrayList<EdoValueObjectCargo>();

		try {
			log.info("START: getShutoutAddDetail  DAO  Start Obj "+" esnAsnNo:"+esnAsnNo );

			sb.append(" SELECT VSL_NM, OUT_VOY_NBR, ESN_ASN_NBR, CARGO_TYPE, CRG_TYPE_NM, ESN_HS_CODE, CRG_DES, MARKINGS, ESN_DG_IND, STG_IND,");
			sb.append(" PKG_TYPE, PKG_DESC, ESNPKG, SHUTOUTPKG, OUTSTANDINGPKG,MAXEDOPKG, ");
			sb.append(" ROUND(ESNPKG*ESN_WT/NBR_PKGS,2) AS ESNPKG_WT, ROUND(ESNPKG*ESN_VOL/NBR_PKGS,2) AS ESNPKG_VOL,");
			sb.append(" ROUND(SHUTOUTPKG*ESN_WT/NBR_PKGS,2) AS SHUTOUTPKG_WT, ROUND(SHUTOUTPKG*ESN_VOL/NBR_PKGS,2) AS SHUTOUTPKG_VOL,");
			sb.append(" ROUND(OUTSTANDINGPKG*ESN_WT/NBR_PKGS,2) AS OUTSTANDINGPKG_WT, ROUND(OUTSTANDINGPKG*ESN_VOL/NBR_PKGS,2) AS OUTSTANDINGPKG_VOL,");
			sb.append(" ROUND(MAXEDOPKG*ESN_WT/NBR_PKGS,2) AS MAXEDOPKG_WT, ROUND(MAXEDOPKG*ESN_VOL/NBR_PKGS,2) AS MAXEDOPKG_VOL");

			if (isEsn(esnAsnNo,null)) {
				sb.append(" FROM(SELECT VC.VSL_NM,VC.OUT_VOY_NBR,ESN.ESN_ASN_NBR,BKD.CARGO_TYPE,CGR.CRG_TYPE_NM,ESND.ESN_HS_CODE,ESND.CRG_DES,");
				sb.append(" ESNM.MARKINGS, ESND.ESN_DG_IND,ESND.STG_IND,ESND.PKG_TYPE,PKG.PKG_DESC,ESND.NBR_PKGS,ESND.ESN_WT,");
				sb.append(" ESND.ESN_VOL,ESND.UA_NBR_PKGS AS ESNPKG,ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED AS SHUTOUTPKG,");
				sb.append(" ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS AS OUTSTANDINGPKG,");
				sb.append(" ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED -(select NVL(sum(NBR_PKGS),0) FROM GB_EDO WHERE ESN_ASN_NBR=:esnAsnNo AND EDO_STATUS <> 'X') AS MAXEDOPKG");
				sb.append(" FROM VESSEL_CALL VC,ESN ESN, BK_DETAILS BKD, ESN_DETAILS ESND,ESN_MARKINGS ESNM,CRG_TYPE CGR,PKG_TYPES PKG ");
				sb.append(" WHERE VC.VV_CD = BKD.VAR_NBR AND CGR.CRG_TYPE_CD=BKD.CARGO_TYPE AND BKD.BK_REF_NBR = ESN.BK_REF_NBR AND ESND.ESN_ASN_NBR = ESN.ESN_ASN_NBR");
				sb.append(" AND ESND.PKG_TYPE=PKG.PKG_TYPE_CD AND ESNM.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND ESN.ESN_ASN_NBR=:esnAsnNo)");
			} else {
				sb.append(" FROM(SELECT  VC.VSL_NM, VC.OUT_VOY_NBR, ESN.ESN_ASN_NBR, BKD.CARGO_TYPE,CGR.CRG_TYPE_NM, TESN.HS_CD AS ESN_HS_CODE, ");
				sb.append(" TESN.CRG_DES, ESNM.MARKINGS, TESN.DG_IND AS ESN_DG_IND, TESN.STORAGE_IND AS STG_IND, TESN.PKG_TYPE,PKG.PKG_DESC, ");
				sb.append(" TESN.NBR_PKGS, TESN.GROSS_WT AS ESN_WT, TESN.GROSS_VOL AS ESN_VOL, TESN.UA_NBR_PKGS AS ESNPKG,");
				sb.append(" TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED AS SHUTOUTPKG, TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS AS OUTSTANDINGPKG,");
				sb.append(" TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED -(select NVL(sum(NBR_PKGS),0) FROM GB_EDO WHERE ESN_ASN_NBR=:esnAsnNo AND EDO_STATUS <> 'X') AS MAXEDOPKG");
				sb.append(" FROM  VESSEL_CALL VC,ESN ESN, BK_DETAILS BKD, TESN_PSA_JP TESN,ESN_MARKINGS ESNM,CRG_TYPE CGR,PKG_TYPES PKG ");
				sb.append(" WHERE  VC.VV_CD = BKD.VAR_NBR AND CGR.CRG_TYPE_CD=BKD.CARGO_TYPE AND BKD.BK_REF_NBR = ESN.BK_REF_NBR AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append(" AND TESN.PKG_TYPE=PKG.PKG_TYPE_CD AND ESNM.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND ESN.ESN_ASN_NBR=:esnAsnNo)");
			}

			paramMap.put("esnAsnNo", esnAsnNo);
			log.info(" *** getShutoutAddDetail SQL *****" + sb.toString());
			log.info(" *** getShutoutAddDetail params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
			while (rs.next()) {
				edoValueObject.setVslNm(CommonUtility.deNull(rs
						.getString("VSL_NM")));
				edoValueObject.setVarNbr(CommonUtility.deNull(rs
						.getString("OUT_VOY_NBR")));
				edoValueObject.setEsnAsnNbr(CommonUtility.deNull(rs
						.getString("ESN_ASN_NBR")));
				edoValueObject.setCrgTypeCd(CommonUtility.deNull(rs
						.getString("CARGO_TYPE"))+"--"+CommonUtility.deNull(rs
								.getString("CRG_TYPE_NM")));
				edoValueObject.setHsCode(CommonUtility.deNull(rs
						.getString("ESN_HS_CODE")));
				edoValueObject.setCrgDes(CommonUtility.deNull(rs
						.getString("CRG_DES")));
				edoValueObject.setMftMarkings(CommonUtility.deNull(rs
						.getString("MARKINGS")));
				edoValueObject.setEsnDgInd(CommonUtility.deNull(rs
						.getString("ESN_DG_IND")));
				edoValueObject.setStgInd(CommonUtility.deNull(rs
						.getString("STG_IND")));
				edoValueObject.setPkgTypeCd(CommonUtility.deNull(rs
						.getString("PKG_TYPE"))+"--"+CommonUtility.deNull(rs
								.getString("PKG_DESC")));
				edoValueObject.setEsnpkgs(CommonUtility.deNull(rs
						.getString("ESNPKG")));
				edoValueObject.setEsnpkgs_wt(CommonUtility.deNull(rs
						.getString("ESNPKG_WT")));
				edoValueObject.setEsnpkgs_vol(CommonUtility.deNull(rs
						.getString("ESNPKG_VOL")));
				edoValueObject.setShutoutpkgs(CommonUtility.deNull(rs
						.getString("SHUTOUTPKG")));
				edoValueObject.setShutoutpkgs_wt(CommonUtility.deNull(rs
						.getString("SHUTOUTPKG_WT")));
				edoValueObject.setShutoutpkgs_vol(CommonUtility.deNull(rs
						.getString("SHUTOUTPKG_VOL")));
				edoValueObject.setOutstandingpkgs(CommonUtility.deNull(rs
						.getString("OUTSTANDINGPKG")));
				edoValueObject.setOutstandingpkgs_wt(CommonUtility.deNull(rs
						.getString("OUTSTANDINGPKG_WT")));
				edoValueObject.setOutstandingpkgs_vol(CommonUtility.deNull(rs
						.getString("OUTSTANDINGPKG_VOL")));
				edoValueObject.setMaxEdoPkgs(String.valueOf(CommonUtility.formatNumToScale( rs.getDouble("MAXEDOPKG"),0)) );
				edoValueObject.setMaxEdoPkgs_vol(String.valueOf(CommonUtility.formatNumToScale(rs.getDouble("MAXEDOPKG_VOL"),2)));
				edoValueObject.setMaxEdoPkgs_wt(String.valueOf(CommonUtility.formatNumToScale( rs.getDouble("MAXEDOPKG_WT"),2)));
				v.add(edoValueObject);
			}

			log.info("END: *** getShutoutAddDetail Result *****" + v.toString());
		} catch (NullPointerException e) {
			log.info("Exception getShutoutAddDetail :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getShutoutAddDetail :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getShutoutAddDetail  DAO  END");
		}
		return v;
	}

	private boolean isEsn(String esnAsnNO,String edoAsnNbr)throws BusinessException{
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		boolean isEsn = false;
		String type = "C";
		try {
			log.info("START: isEsn  DAO  Start Obj "+" esnAsnNO:"+esnAsnNO+" edoAsnNbr:"+edoAsnNbr );

			paramMap.put("esnAsnNO", esnAsnNO);
			paramMap.put("edoAsnNbr", edoAsnNbr);

			if(esnAsnNO != null){
				sb.append("SELECT TRANS_TYPE FROM ESN WHERE ESN_ASN_NBR=:esnAsnNO");
			}else{
				sb.append("SELECT ESN.TRANS_TYPE FROM ESN ESN,GB_EDO EDO WHERE ESN.ESN_ASN_NBR=EDO.ESN_ASN_NBR AND EDO.EDO_ASN_NBR=:edoAsnNbr");
			}

			log.info(" *** isEsn SQL *****" + sb.toString() + param + paramMap);
			log.info(" *** isEsn params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				type = CommonUtility.deNull(rs.getString("TRANS_TYPE"));
			}
			
			log.info("END: *** isEsn Result *****" + CommonUtility.deNull(type));
			if(type.equals("E"))
				isEsn = true;

			log.info("END: *** isEsn Result *****" + isEsn);
		} catch (NullPointerException e) {
			log.info("Exception isEsn :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isEsn :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isEsn  DAO  END");
		}
		return isEsn;

	}

	@Override
	public List<EdoValueObjectCargo> getShutoutEdoDetail(String edoAsnNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<EdoValueObjectCargo> v = new ArrayList<EdoValueObjectCargo>();


		try {
			log.info("START: getShutoutEdoDetail  DAO  Start Obj " );

			sb.append(" SELECT TERMINAL, SCHEME, COMBI_GC_SCHEME, COMBI_GC_OPS_IND, VV_CD,VSL_NM, OUT_VOY_NBR, ESN_ASN_NBR, CARGO_TYPE, CRG_TYPE_NM, ESN_HS_CODE, CRG_DES, MARKINGS, ESN_DG_IND, STG_IND, FREE_STG_DAYS, ");
			sb.append(" PKG_TYPE, PKG_DESC, ESNPKG, SHUTOUTPKG, OUTSTANDINGPKG,ADP_IC_TDBCR_NBR,ADP_NM,EDOPKGS,NOM_WT,NOM_VOL,ACCT_NBR,EDO_DELIVERY_TO,WH_IND,WH_AGGR_NBR,WH_REMARKS,EDO_ASN_NBR,");
			sb.append(" ROUND(ESNPKG*ESN_WT/NBR_PKGS,2) AS ESNPKG_WT, ROUND(ESNPKG*ESN_VOL/NBR_PKGS,2) AS ESNPKG_VOL,");
			sb.append(" ROUND(SHUTOUTPKG*ESN_WT/NBR_PKGS,2) AS SHUTOUTPKG_WT, ROUND(SHUTOUTPKG*ESN_VOL/NBR_PKGS,2) AS SHUTOUTPKG_VOL,");
			sb.append(" ROUND(OUTSTANDINGPKG*ESN_WT/NBR_PKGS,2) AS OUTSTANDINGPKG_WT, ROUND(OUTSTANDINGPKG*ESN_VOL/NBR_PKGS,2) AS OUTSTANDINGPKG_VOL");

			if (isEsn(null, edoAsnNbr)) {

				sb.append(" FROM(SELECT VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND, VC.VV_CD,VC.VSL_NM,VC.OUT_VOY_NBR,ESN.ESN_ASN_NBR,BKD.CARGO_TYPE,CGR.CRG_TYPE_NM, ESND.ESN_HS_CODE,ESND.CRG_DES,");
				sb.append(" ESNM.MARKINGS, ESND.ESN_DG_IND,ESND.STG_IND,ESND.PKG_TYPE,PKG.PKG_DESC,ESND.NBR_PKGS,ESND.ESN_WT,");
				sb.append(" ESND.ESN_VOL,ESND.UA_NBR_PKGS AS ESNPKG,ESND.UA_NBR_PKGS - NVL(BKD.ACTUAL_NBR_SHIPPED,0) AS SHUTOUTPKG,");
				sb.append(" ESND.UA_NBR_PKGS - NVL(BKD.ACTUAL_NBR_SHIPPED,0) - NVL(BKD.SHUTOUT_DELIVERY_PKGS,0) AS OUTSTANDINGPKG,");
				sb.append(" EDO.ADP_IC_TDBCR_NBR,EDO.ADP_NM,EDO.NBR_PKGS AS EDOPKGS,EDO.NOM_WT,EDO.NOM_VOL,EDO.ACCT_NBR,EDO.EDO_DELIVERY_TO,");
				sb.append(" EDO.WH_IND,EDO.WH_AGGR_NBR,EDO.WH_REMARKS,EDO.EDO_ASN_NBR,EDO.FREE_STG_DAYS ");
				sb.append(" FROM VESSEL_CALL VC,ESN ESN, BK_DETAILS BKD, ESN_DETAILS ESND,ESN_MARKINGS ESNM,GB_EDO EDO,CRG_TYPE CGR,PKG_TYPES PKG");
				sb.append(" WHERE VC.VV_CD = BKD.VAR_NBR AND CGR.CRG_TYPE_CD=BKD.CARGO_TYPE AND BKD.BK_REF_NBR = ESN.BK_REF_NBR AND ESND.ESN_ASN_NBR = ESN.ESN_ASN_NBR");
				sb.append(" AND EDO.EDO_STATUS='A' ");
				sb.append(" AND ESND.PKG_TYPE=PKG.PKG_TYPE_CD AND ESNM.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND EDO.ESN_ASN_NBR =ESN.ESN_ASN_NBR AND EDO.EDO_ASN_NBR=:edoAsnNbr)");
			} else {
				sb.append(" FROM(SELECT  VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND, VC.VV_CD,VC.VSL_NM, VC.OUT_VOY_NBR, ESN.ESN_ASN_NBR, BKD.CARGO_TYPE,CGR.CRG_TYPE_NM, TESN.HS_CD AS ESN_HS_CODE,");
				sb.append(" TESN.CRG_DES, ESNM.MARKINGS, TESN.DG_IND AS ESN_DG_IND, TESN.STORAGE_IND AS STG_IND, TESN.PKG_TYPE, PKG.PKG_DESC, ");
				sb.append(" TESN.NBR_PKGS, TESN.GROSS_WT AS ESN_WT, TESN.GROSS_VOL AS ESN_VOL, TESN.UA_NBR_PKGS AS ESNPKG,");
				sb.append(" TESN.UA_NBR_PKGS - NVL(BKD.ACTUAL_NBR_SHIPPED,0) AS SHUTOUTPKG, TESN.UA_NBR_PKGS - NVL(BKD.ACTUAL_NBR_SHIPPED,0) - BKD.SHUTOUT_DELIVERY_PKGS AS OUTSTANDINGPKG,");
				sb.append(" EDO.ADP_IC_TDBCR_NBR,EDO.ADP_NM,EDO.NBR_PKGS AS EDOPKGS,EDO.NOM_WT,EDO.NOM_VOL,EDO.ACCT_NBR,EDO.EDO_DELIVERY_TO,");
				sb.append(" EDO.WH_IND,EDO.WH_AGGR_NBR,EDO.WH_REMARKS,EDO.EDO_ASN_NBR,EDO.FREE_STG_DAYS ");
				sb.append(" FROM  VESSEL_CALL VC,ESN ESN, BK_DETAILS BKD, TESN_PSA_JP TESN,ESN_MARKINGS ESNM,GB_EDO EDO,CRG_TYPE CGR,PKG_TYPES PKG ");
				sb.append(" WHERE  VC.VV_CD = BKD.VAR_NBR AND CGR.CRG_TYPE_CD=BKD.CARGO_TYPE AND BKD.BK_REF_NBR = ESN.BK_REF_NBR AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append(" AND EDO.EDO_STATUS='A' ");
				sb.append(" AND TESN.PKG_TYPE=PKG.PKG_TYPE_CD AND ESNM.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND EDO.ESN_ASN_NBR =ESN.ESN_ASN_NBR AND EDO.EDO_ASN_NBR=:edoAsnNbr )");

			}


			paramMap.put("edoAsnNbr", edoAsnNbr);
			log.info(" *** getShutoutEdoDetail SQL *****" + sb.toString());
			log.info(" *** getShutoutEdoDetail params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
			while (rs.next()) {
				edoValueObject.setVarNbr(CommonUtility.deNull(rs.getString("VV_CD")));
				edoValueObject.setVslNm(CommonUtility.deNull(rs
						.getString("VSL_NM")));
				edoValueObject.setOutVoyNbr(CommonUtility.deNull(rs
						.getString("OUT_VOY_NBR")));
				edoValueObject.setEsnAsnNbr(CommonUtility.deNull(rs
						.getString("ESN_ASN_NBR")));
				edoValueObject.setCrgTypeCd(CommonUtility.deNull(rs
						.getString("CARGO_TYPE"))+"--"+CommonUtility.deNull(rs
								.getString("CRG_TYPE_NM")));
				edoValueObject.setHsCode(CommonUtility.deNull(rs
						.getString("ESN_HS_CODE")));
				edoValueObject.setCrgDes(CommonUtility.deNull(rs
						.getString("CRG_DES")));
				edoValueObject.setMftMarkings(CommonUtility.deNull(rs
						.getString("MARKINGS")));
				edoValueObject.setEsnDgInd(CommonUtility.deNull(rs
						.getString("ESN_DG_IND")));
				edoValueObject.setStgInd(CommonUtility.deNull(rs
						.getString("STG_IND")));
				edoValueObject.setPkgTypeCd(CommonUtility.deNull(rs
						.getString("PKG_TYPE"))+"--"+CommonUtility.deNull(rs
								.getString("PKG_DESC")));
				edoValueObject.setEsnpkgs(CommonUtility.deNull(rs
						.getString("ESNPKG")));
				edoValueObject.setEsnpkgs_wt(CommonUtility.deNull(rs
						.getString("ESNPKG_WT")));
				edoValueObject.setEsnpkgs_vol(CommonUtility.deNull(rs
						.getString("ESNPKG_VOL")));
				edoValueObject.setShutoutpkgs(CommonUtility.deNull(rs
						.getString("SHUTOUTPKG")));
				edoValueObject.setShutoutpkgs_wt(CommonUtility.deNull(rs
						.getString("SHUTOUTPKG_WT")));
				edoValueObject.setShutoutpkgs_vol(CommonUtility.deNull(rs
						.getString("SHUTOUTPKG_VOL")));
				edoValueObject.setOutstandingpkgs(CommonUtility.deNull(rs
						.getString("OUTSTANDINGPKG")));
				edoValueObject.setOutstandingpkgs_wt(CommonUtility.deNull(rs
						.getString("OUTSTANDINGPKG_WT")));
				edoValueObject.setOutstandingpkgs_vol(CommonUtility.deNull(rs
						.getString("OUTSTANDINGPKG_VOL")));
				edoValueObject.setAdpNbr(CommonUtility.deNull(rs
						.getString("ADP_IC_TDBCR_NBR")));
				edoValueObject.setAdpNm(CommonUtility.deNull(rs
						.getString("ADP_NM")));
				edoValueObject.setNbrPkgs(CommonUtility.deNull(rs
						.getString("EDOPKGS")));
				edoValueObject.setNomWeight(CommonUtility.deNull(rs
						.getString("NOM_WT")));
				edoValueObject.setNomVolume(CommonUtility.deNull(rs
						.getString("NOM_VOL")));
				edoValueObject.setAcctNbr(CommonUtility.deNull(rs
						.getString("ACCT_NBR")));
				edoValueObject.setDeliveryTo(CommonUtility.deNull(rs
						.getString("EDO_DELIVERY_TO")));
				edoValueObject.setWhInd(CommonUtility.deNull(rs
						.getString("WH_IND")));
				edoValueObject.setWhAggrNbr(CommonUtility.deNull(rs
						.getString("WH_AGGR_NBR")));
				edoValueObject.setWhRemarks(CommonUtility.deNull(rs
						.getString("WH_REMARKS")));
				edoValueObject.setEdoAsnNbr(CommonUtility.deNull(rs
						.getString("EDO_ASN_NBR")));
				edoValueObject.setFreeStgDays(CommonUtility.deNull(rs.getString("FREE_STG_DAYS")));
				edoValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				edoValueObject.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				edoValueObject.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				edoValueObject.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				v.add(edoValueObject);
			}

			log.info("END: *** getShutoutEdoDetail Result *****" + v.toString());
		} catch (NullPointerException e) {
			log.info("Exception getShutoutEdoDetail :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getShutoutEdoDetail :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getShutoutEdoDetail  DAO  END");
		}

		return v;
	}

	@Override
	public TableResult getShutoutEdoList(String strVarNbr, Criteria criteria) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		String sql = "";
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		List<EdoValueObjectCargo> edolist = new ArrayList<EdoValueObjectCargo>();
		try {
			log.info("START: getShutoutEdoList  DAO  Start Obj "+" strVarNbr:"+strVarNbr +" criteria:"+ criteria.toString());

			sb.append(" SELECT VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND, EDO.EDO_ASN_NBR, EDO.ESN_ASN_NBR, ESND.CRG_DES, BKD.CARGO_TYPE, EDO.ADP_NM, ESND.UA_NBR_PKGS,");
			sb.append(" (ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED) SHUTOUTPKGS, (ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS) OUTSTANDINGPKGS, EDO.NBR_PKGS, EDO.EDO_STATUS ");
			sb.append(" FROM VESSEL_CALL VC ,GB_EDO EDO,BK_DETAILS BKD ,ESN ESN ,ESN_DETAILS ESND ");
			sb.append(" WHERE VC.VV_CD = EDO.VAR_NBR AND EDO.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND BKD.BK_REF_NBR = ESN.BK_REF_NBR AND ESND.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND EDO.EDO_STATUS IN('A','V') AND EDO.VAR_NBR='"+ strVarNbr + "'");
			sb.append(" UNION ");
			sb.append(" SELECT VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND, EDO.EDO_ASN_NBR, EDO.ESN_ASN_NBR, TESN.CRG_DES, BKD.CARGO_TYPE, EDO.ADP_NM, TESN.UA_NBR_PKGS,");
			sb.append(" (TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED) SHUTOUTPKGS, (TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS) OUTSTANDINGPKGS, EDO.NBR_PKGS, EDO.EDO_STATUS ");
			sb.append(" FROM VESSEL_CALL VC ,MANIFEST_DETAILS MD ,GB_EDO EDO,BK_DETAILS BKD ,ESN ESN  ,TESN_PSA_JP TESN ");
			sb.append(" WHERE VC.VV_CD = EDO.VAR_NBR AND EDO.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND BKD.BK_REF_NBR = ESN.BK_REF_NBR AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND EDO.EDO_STATUS IN('A','V') AND EDO.VAR_NBR='"+ strVarNbr + "'");

			sql = sb.toString();
			paramMap.put("strVarNbr", strVarNbr);
			log.info(" *** getShutoutEdoList SQL *****" + sb.toString() + param + paramMap);
			log.info(" *** getShutoutEdoList params *****" + paramMap.toString());
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
						
				sql = CommonUtil.getPaginatedSql(sb.toString(), criteria.getStart(), criteria.getLimit());
			}
			log.info(" *** getShutoutEdoList SQL *****" + sb.toString());
			log.info(" *** getShutoutEdoList params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
				String edoasnnbr = CommonUtility.deNull(""+rs.getInt("EDO_ASN_NBR"));
				String esnasnnbr = CommonUtility.deNull(""+rs.getInt("ESN_ASN_NBR"));
				String crgdes = CommonUtility.deNull(rs.getString("CRG_DES"));
				String crgtypnm = CommonUtility.deNull(rs.getString("CARGO_TYPE"));
				String adpnm = CommonUtility.deNull(rs.getString("ADP_NM"));
				String esnpkgs = CommonUtility.deNull(rs.getString("UA_NBR_PKGS"));
				String shutoutpkgs = CommonUtility.deNull(rs.getString("SHUTOUTPKGS"));
				String outstandingpkgs = CommonUtility.deNull(rs.getString("OUTSTANDINGPKGS"));
				String edopkgs = CommonUtility.deNull(rs.getString("NBR_PKGS"));
				String edostatus = CommonUtility.deNull(rs.getString("EDO_STATUS"));
				String terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				String scheme = CommonUtility.deNull(rs.getString("SCHEME"));
				String subScheme = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				String gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));

				edoValueObject.setEdoAsnNbr(edoasnnbr);
				edoValueObject.setMftSeqNbr(esnasnnbr);
				edoValueObject.setCrgDes(crgdes);
				edoValueObject.setCrgTypeNm(crgtypnm);
				edoValueObject.setAdpNm(adpnm);
				edoValueObject.setEsnpkgs(esnpkgs);
				edoValueObject.setShutoutpkgs(shutoutpkgs);
				edoValueObject.setOutstandingpkgs(outstandingpkgs);
				edoValueObject.setEdoNbrPkgs(edopkgs);
				edoValueObject.setEdoStatus(edostatus);
				edoValueObject.setTerminal(terminal);
				edoValueObject.setScheme(scheme);
				edoValueObject.setSubScheme(subScheme);
				edoValueObject.setGcOperations(gcOperations);
				edolist.add(edoValueObject);
			}

			for (EdoValueObjectCargo object : edolist) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			
			log.info("END: *** getShutoutEdoList Result *****" + edolist.toString());
		} catch (NullPointerException e) {
			log.info("Exception getShutoutEdoList :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getShutoutEdoList :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getShutoutEdoList  DAO  END");
		}
		return tableResult;

	};

	@Override
	public List<EdoValueObjectCargo> getShutoutVesselList(String vesselName, String voyageNumber) 	throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();


		List<EdoValueObjectCargo> vesselvoyage = new ArrayList<EdoValueObjectCargo>();

		try {
			log.info("START: getShutoutVesselList  DAO  Start Obj "+" vesselName:"+vesselName+" voyageNumber:"+voyageNumber );

			sb.append("SELECT VC.VV_CD, VC.VSL_NM, VC.OUT_VOY_NBR,VC.TERMINAL,VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND FROM VESSEL_CALL VC,BK_DETAILS BKD,ESN ESN,ESN_DETAILS ESND ");
			sb.append(" WHERE VC.VV_CD = BKD.VAR_NBR AND BKD.BK_REF_NBR = ESN.BK_REF_NBR AND ESND.ESN_ASN_NBR = ESN.ESN_ASN_NBR  AND VC.VV_STATUS_IND <> 'CX' AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT')  AND VC.VSL_NM = :vesselName AND VC.OUT_VOY_NBR = :voyageNumber");

			sb.append(" AND ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS > 0");

			sb.append(" UNION");
			sb.append(" SELECT VC.VV_CD, VC.VSL_NM, VC.OUT_VOY_NBR,VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND FROM VESSEL_CALL VC,BK_DETAILS BKD,ESN ESN,TESN_PSA_JP TESN");
			sb.append(" WHERE VC.VV_CD = BKD.VAR_NBR AND BKD.BK_REF_NBR = ESN.BK_REF_NBR AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND VC.VV_STATUS_IND <> 'CX' AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') AND TESN.UA_NBR_PKGS -  BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS > 0 AND VC.VSL_NM = :vesselName AND VC.OUT_VOY_NBR = :voyageNumber ");

			paramMap.put("vesselName", vesselName);
			paramMap.put("voyageNumber", voyageNumber);
			log.info(" *** getShutoutVesselList SQL *****" + sb.toString());
			log.info(" *** getShutoutVesselList params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
				String varnbr = CommonUtility.deNull(rs.getString("VV_CD"));
				String vslnm = CommonUtility.deNull(rs.getString("VSL_NM"));
				String outvoynbr = CommonUtility.deNull(rs.getString("OUT_VOY_NBR"));
				String terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				String scheme = CommonUtility.deNull(rs.getString("SCHEME"));
				String subScheme = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				String gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));

				edoValueObject.setVarNbr(varnbr);
				edoValueObject.setVslNm(vslnm);
				edoValueObject.setOutVoyNbr(outvoynbr);
				edoValueObject.setTerminal(terminal);
				edoValueObject.setScheme(scheme);
				edoValueObject.setSubScheme(subScheme);
				edoValueObject.setGcOperations(gcOperations);
				vesselvoyage.add(edoValueObject);
			}


			log.info("END: *** getShutoutVesselList Result *****" + vesselvoyage.toString());
		} catch (NullPointerException e) {
			log.info("Exception getShutoutVesselList :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getShutoutVesselList :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getShutoutVesselList  DAO  END");
		}

		return vesselvoyage;
	}

	@Override
	public List<EdoValueObjectCargo> getShutoutVesselVoyageNbrList() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		List<EdoValueObjectCargo> vesselvoyage = new ArrayList<EdoValueObjectCargo>();
		try {
			log.info("START: getShutoutVesselVoyageNbrList  DAO  Start Obj " );

			sb.append("SELECT VC.VV_CD, VC.VSL_NM, VC.OUT_VOY_NBR,VC.TERMINAL FROM VESSEL_CALL VC,BK_DETAILS BKD,ESN ESN,ESN_DETAILS ESND ");
			sb.append(" WHERE VC.VV_CD = BKD.VAR_NBR AND BKD.BK_REF_NBR = ESN.BK_REF_NBR AND ESND.ESN_ASN_NBR = ESN.ESN_ASN_NBR AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT')");

			sb.append(" AND ESN.TRANS_TYPE = 'E' AND ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS > 0 ");

			sb.append(" UNION");
			sb.append(" SELECT VC.VV_CD, VC.VSL_NM, VC.OUT_VOY_NBR,VC.TERMINAL FROM VESSEL_CALL VC,BK_DETAILS BKD,ESN ESN,TESN_PSA_JP TESN");
			sb.append(" WHERE VC.VV_CD = BKD.VAR_NBR AND BKD.BK_REF_NBR = ESN.BK_REF_NBR AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR  AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') AND ESN.TRANS_TYPE = 'C' AND TESN.UA_NBR_PKGS -  BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS > 0 ");
			//Added by Jade for SL-CIM-20111129-02
			sb.append(" ORDER BY TERMINAL DESC,VSL_NM ");
			//End of adding by Jade for SL-CIM-20111129-02

			log.info(" *** getShutoutVesselVoyageNbrList SQL *****" + sb.toString());
			log.info(" *** getShutoutVesselVoyageNbrList params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
				String varnbr = CommonUtility.deNull(rs.getString("VV_CD"));
				String vslnm = CommonUtility.deNull(rs.getString("VSL_NM"));
				String outvoynbr = CommonUtility.deNull(rs.getString("OUT_VOY_NBR"));
				String terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				edoValueObject.setVarNbr(varnbr);
				edoValueObject.setVslNm(vslnm);
				edoValueObject.setOutVoyNbr(outvoynbr);
				edoValueObject.setTerminal(terminal);
				vesselvoyage.add(edoValueObject);
			}
			log.info("END: *** getShutoutVesselVoyageNbrList Result *****" + vesselvoyage.toString());
		} catch (NullPointerException e) {
			log.info("Exception getShutoutVesselVoyageNbrList :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getShutoutVesselVoyageNbrList :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getShutoutVesselVoyageNbrList  DAO  END");
		}

		return vesselvoyage;
	};


	@Override
	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd) throws BusinessException {
		boolean isShowEdoInfo = false;
		StringBuffer sb;
		Map<String, String> paramMap = new HashMap<>();
		List<EdoValueObjectCargo> edoValueList = new ArrayList<EdoValueObjectCargo>();
		try {
			log.info("getVesselVoyageNbrList STARTS: Params:" + "strCustCode: " + strCustCode + " strmodulecd:"
					+ strmodulecd);
			try {
				TextParaVO code = new TextParaVO();
				code.setParaCode(TEXT_PARA_GC_VIEW_EDO);
				TextParaVO result = getParaCodeInfo(code);
				isShowEdoInfo = isShowEdoInfo(strCustCode, result);
				
				log.info(" *** getVesselVoyageNbrList result *****" + isShowEdoInfo);
			} catch (Exception e) {
				log.info("Exception getVesselVoyageNbrList Retriving text para : ", e);
				throw new BusinessException("M4201");
			}
			// ++ 19.10.2009 Changed by LongDh09::for GB CR
			// Sripriya To fix Vessel Listing for ADP Renom 2 Apr 2012
			if (isShowEdoInfo) {
				if (strmodulecd.equalsIgnoreCase("ADPRENOM")) {
					// +" AND VC.TERMINAL='GB') "
					// +" nvl(VC.GB_CLOSE_BJ_IND,'N') = 'N' AND
					// EDO.APPOINTED_ADP_CUST_CD='"+strCustCode
					// BEGIN FPT modify to allow ADP renom even when Close BJ
					// +" nvl(VC.GB_CLOSE_BJ_IND,'N') = 'N' AND "
					// END FPT modify to allow ADP renom even when Close BJ
					// +"' AND VC.VV_CD=EDO.VAR_NBR ORDER BY VC.VSL_NM,"

					sb = new StringBuffer();
					sb.append(" SELECT DISTINCT VC.VV_CD AS var_nbr, VC.VSL_NM, VC.IN_VOY_NBR, VC.TERMINAL ");
					sb.append(" FROM VESSEL_CALL VC,GB_EDO EDO WHERE ");
					sb.append(" (VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB') ");
					sb.append(" AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT'))");
					sb.append("  AND VC.VV_CD=EDO.VAR_NBR ORDER BY VC.TERMINAL DESC, VC.VSL_NM,");
					sb.append(" VC.IN_VOY_NBR ");
				} else {
					sb = new StringBuffer();
					sb.append(" SELECT VC.VV_CD AS var_nbr ,VC.VSL_NM,VC.IN_VOY_NBR, VC.TERMINAL  FROM ");
					sb.append(" VESSEL_CALL VC WHERE ");
					sb.append(" (VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB') ");
					sb.append("  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')) ");
					sb.append(
							"  AND nvl(VC.GB_CLOSE_BJ_IND,'N') = 'N' ");
					
					sb.append(" ORDER BY VC.TERMINAL DESC, VC.VSL_NM, VC.IN_VOY_NBR ");
					
				
					// +" AND VC.TERMINAL='GB' )"
				}
			} else {
				if (strmodulecd.equalsIgnoreCase("ADPRENOM")) {
					sb = new StringBuffer();
					sb.append(" SELECT DISTINCT VC.VV_CD AS var_nbr, VC.VSL_NM, VC.IN_VOY_NBR,VC.TERMINAL ");
					sb.append(" FROM VESSEL_CALL VC,GB_EDO EDO WHERE ");
					sb.append(" (VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB')");
					sb.append("  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')) ");
					sb.append(" AND EDO.APPOINTED_ADP_CUST_CD=:strCustCode");
					sb.append("  AND VC.VV_CD=EDO.VAR_NBR ORDER BY  VC.TERMINAL DESC, VC.VSL_NM,");
					sb.append(" VC.IN_VOY_NBR ");
				} else {
					if (strmodulecd.equalsIgnoreCase("EDOADPVIEW")) {
						sb = new StringBuffer();
						sb.append(" SELECT DISTINCT VC.VV_CD AS var_nbr, VC.VSL_NM, VC.IN_VOY_NBR, VC.TERMINAL ");
						sb.append(" FROM VESSEL_CALL VC,GB_EDO EDO WHERE ");
						sb.append(" (VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB')");
						sb.append(
								"  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')) ");
						sb.append(" AND VC.VV_CD=EDO.VAR_NBR ");
						// sb.append(" AND (EDO.ADP_CUST_CD=:strCustCode");param
						// sb.append(" OR EDO.CA_CUST_CD=:strCustCode");param
						sb.append(" ORDER BY  VC.TERMINAL DESC, VC.VSL_NM, VC.IN_VOY_NBR ");

					} else {
						sb = new StringBuffer();
						/*
						 * sql="SELECT VC.VV_CD,VC.VSL_NM,VC.IN_VOY_NBR  FROM" +" VESSEL_CALL VC WHERE "
						 * +" (VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB')"
						 * +" AND VC.TERMINAL='GB') AND"
						 * +" nvl(VC.GB_CLOSE_BJ_IND,'N') = 'N' AND nvl(VC.DECLARANT_CUST_CD,VC.CREATE_CUST_CD)='"
						 * +strCustCode+"' ORDER BY VC.VSL_NM, VC.IN_VOY_NBR ";
						 */
						sb.append(" SELECT DISTINCT IN_VOY_NBR,VSL_NM,VC.VV_CD AS var_nbr,VC.TERMINAL ");
						sb.append(
								" FROM VESSEL_CALL VC LEFT OUTER JOIN VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A')");
						sb.append(" WHERE VV_STATUS_IND IN ('PR','AP','AL','BR','UB') ");
						sb.append(
								"  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT') ");
						sb.append(" AND nvl(VC.GB_CLOSE_BJ_IND,'N') = 'N'");
						sb.append(" AND (VD.CUST_CD = :strCustCode OR VC.CREATE_CUST_CD = :strCustCode)");
						sb.append(" ORDER BY VC.TERMINAL DESC, VSL_NM,IN_VOY_NBR");
					}
				}

			}
			
			// -- 19.10.2009 Changed by LongDh09::for GB CR
			paramMap.put("strCustCode", strCustCode);
			log.info(strCustCode + ":" + strmodulecd + "::Vessel List SQL-vietnd-EDO:: " + sb.toString() + param + paramMap);
			edoValueList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<EdoValueObjectCargo>(EdoValueObjectCargo.class));
			
			log.info("END: *** getVesselVoyageNbrList Result *****" + edoValueList.size());
		} catch (BusinessException e) {
			log.info("Exception getVesselVoyageNbrList : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getVesselVoyageNbrList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getVesselVoyageNbrList DAO  END *****");
		}
		return edoValueList;
	}

	private TextParaVO getParaCodeInfo(TextParaVO tpvo) throws BusinessException {
		SqlRowSet rs = null;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TOPS.TEXT_PARA ");
		sql.append("WHERE PARA_CD =:paraCd ");
		TextParaVO tpo = new TextParaVO();
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START getParaCodeInfo Dao Start tpvo:" + tpvo);
			paramMap.put("paraCd", tpvo.getParaCode());
			log.info(" *** getParaCodeInfo SQL *****" + sql.toString());
			log.info(" *** getParaCodeInfo params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				tpo.setParaCode(rs.getString("PARA_CD"));
				tpo.setValue(rs.getString("VALUE"));
				tpo.setParaDesc(CommonUtility.deNull(rs.getString("PARA_DESC")));
				tpo.setUser(rs.getString("LAST_MODIFY_USER_ID"));
				tpo.setTimestamp(rs.getTimestamp("LAST_MODIFY_DTTM"));
			}
			log.info("END: getParaCodeInfo DAO End result:" + tpo.toString());
		} catch (Exception e) {
			log.info("Exception getParaCodeInfo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getParaCodeInfo DAO  END *****");
		}
		return tpo;
	}

	@Override
	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd, String vesselName,
			String voyageNumber) throws BusinessException {
		log.info("getVesselVoyageNbrList 4 params STARTS" + strCustCode + " " + strmodulecd + " " + vesselName + " "
				+ voyageNumber);
		String sql = "";
		boolean isShowEdoInfo = false;
		StringBuffer sb = null;
		Map<String, String> paramMap = new HashMap<>();
		List<EdoValueObjectCargo> vesselvoyageList = new ArrayList<EdoValueObjectCargo>();
		try {
			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_EDO);
			TextParaVO result = getParaCodeInfo(code);
			isShowEdoInfo = isShowEdoInfo(strCustCode, result);
			
			log.info(" *** getVesselVoyageNbrList isShowEdoInfo *****" + isShowEdoInfo);
		} catch (Exception e) {
			log.info("Exception getVesselVoyageNbrList Retriving text para : ", e);
			throw new BusinessException("M4201");
		}
		try {
			if (isShowEdoInfo) {
				if (strmodulecd.equalsIgnoreCase("ADPRENOM")) {
					log.info("strmodulecd" + strmodulecd);
					sb = new StringBuffer();
					sb.append(" SELECT DISTINCT VC.VV_CD, VC.VSL_NM, VC.IN_VOY_NBR, ");
					sb.append(
							"TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM )- NVL(B.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM ),B.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
					sb.append(
							"TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM )- NVL(B.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM ),B.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
					sb.append(" TO_CHAR(B.COD_DTTM,'dd/mm/yyyy HH24MI') as COD_DTTM, ");
					sb.append(" TO_CHAR(B.GB_COD_DTTM,'dd/mm/yyyy HH24MI') as GB_COD_DTTM, ");
					sb.append(" TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM, VC.TERMINAL ");
					sb.append(" FROM VESSEL_CALL VC");
					sb.append(" INNER JOIN GB_EDO EDO ON VC.VV_CD=EDO.VAR_NBR ");
					sb.append(" LEFT JOIN BERTHING B ON (VC.VV_CD = B.VV_CD AND B.SHIFT_IND = 1) WHERE");
					sb.append(" ((VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB','CL') ) ");
					sb.append("  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')) ");
					sb.append(" AND VC.VSL_NM = :vesselName AND VC.IN_VOY_NBR = :voyageNumber ");
					sb.append(" ORDER BY  VC.TERMINAL DESC, VC.VSL_NM, VC.IN_VOY_NBR");

					// +" TO_CHAR(B.ATB_DTTM, 'dd/mm/yyyy HH24MI') as ARRIVAL ,"
					// +" TO_CHAR(B.ATU_DTTM, 'dd/mm/yyyy HH24MI') AS DEPARTURE, "

					// +" INNER JOIN GB_EDO EDO ON VC.VV_CD=EDO.VAR_NBR AND
					// EDO.APPOINTED_ADP_CUST_CD='"+strCustCode +"' "

					// +" AND VC.TERMINAL='GB')"
				} else {
					sb = new StringBuffer();
					sb.append(" SELECT VC.VV_CD, VC.VV_CD VAR_NBR ,VC.VSL_NM,VC.IN_VOY_NBR,  ");
					sb.append(
							"TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM )- NVL(B.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM ),B.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
					sb.append(
							"TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM )- NVL(B.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM ),B.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
					sb.append(" TO_CHAR(B.COD_DTTM,'dd/mm/yyyy HH24MI') as COD_DTTM, ");
					sb.append(" TO_CHAR(B.GB_COD_DTTM,'dd/mm/yyyy HH24MI') as GB_COD_DTTM, ");
					sb.append(" TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM, VC.TERMINAL ");
					sb.append(" FROM VESSEL_CALL VC ");
					sb.append(" LEFT JOIN BERTHING B ON (VC.VV_CD = B.VV_CD AND B.SHIFT_IND = 1) ");
					sb.append(" WHERE ");
					sb.append(" ((VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB','CL') ) ");
					sb.append("  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')) ");
					sb.append(" AND VC.VSL_NM = :vesselName AND VC.IN_VOY_NBR = :voyageNumber");
					sb.append(" ORDER BY VC.TERMINAL DESC, VC.VSL_NM, VC.IN_VOY_NBR ");

					// +" TO_CHAR(B.ATB_DTTM, 'dd/mm/yyyy HH24MI') as ARRIVAL ,"
					// +" TO_CHAR(B.ATU_DTTM, 'dd/mm/yyyy HH24MI') AS DEPARTURE, "

					// +" AND VC.TERMINAL='GB')"
				}
			} else {
				if (strmodulecd.equalsIgnoreCase("ADPRENOM")) {
					log.info("strmodulecd:" + strmodulecd);
					sb = new StringBuffer();
					sb.append("SELECT DISTINCT VC.VV_CD, VC.VSL_NM, VC.IN_VOY_NBR, VC.VV_CD VAR_NBR,");
					// +" TO_CHAR(B.ATB_DTTM, 'dd/mm/yyyy HH24MI') as ARRIVAL ,"
					// +" TO_CHAR(B.ATU_DTTM, 'dd/mm/yyyy HH24MI') AS DEPARTURE, "
					sb.append(
							" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM )- NVL(B.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM ),B.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
					sb.append(
							" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM )- NVL(B.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM ),B.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
					sb.append("  TO_CHAR(B.COD_DTTM,'dd/mm/yyyy HH24MI') as COD_DTTM, ");
					sb.append("  TO_CHAR(B.GB_COD_DTTM,'dd/mm/yyyy HH24MI') as GB_COD_DTTM, ");
					sb.append("  TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM, VC.TERMINAL ");
					sb.append("  FROM VESSEL_CALL VC ");
					sb.append(
							"  INNER JOIN GB_EDO EDO ON VC.VV_CD=EDO.VAR_NBR AND EDO.APPOINTED_ADP_CUST_CD= :strCustCode");
					sb.append("  LEFT JOIN BERTHING B ON (VC.VV_CD = B.VV_CD AND B.SHIFT_IND = 1)");
					sb.append("  WHERE ");
					sb.append("  ((VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB','CL') ) ");
					sb.append(
							"   AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')) ");
					// +" AND VC.TERMINAL='GB')"
					sb.append("  AND VC.VSL_NM = :vesselName AND VC.IN_VOY_NBR = :voyageNumber ");
					sb.append("  ORDER BY  VC.TERMINAL DESC, VC.VSL_NM, ");
					sb.append("  VC.IN_VOY_NBR");

//                        +" ((VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB','CL') ) "
//                        +" AND ON(VC.VV_CD = B.VV_CD AND B.SHIFT_IND = 1) "
//                        +" AND VC.TERMINAL='GB') "
//                        +" AND EDO.APPOINTED_ADP_CUST_CD='"+strCustCode
//                        +"' AND VC.VV_CD=EDO.VAR_NBR AND VC.VSL_NM = '" + vesselName + "' AND VC.IN_VOY_NBR = '" + voyageNumber + "'"
//                        +" ORDER BY  VC.VSL_NM,"
//                        +" VC.IN_VOY_NBR";
				} else {
					if (strmodulecd.equalsIgnoreCase("EDOADPVIEW")) {
						sb = new StringBuffer();
						log.info("strmodulecd" + strmodulecd);
						sb.append("SELECT DISTINCT VC.VV_CD, VC.VSL_NM, VC.IN_VOY_NBR, VC.VV_CD VAR_NBR, ");
						// +" TO_CHAR(B.ATB_DTTM, 'dd/mm/yyyy HH24MI') as ARRIVAL ,");
						// +" TO_CHAR(B.ATU_DTTM, 'dd/mm/yyyy HH24MI') AS DEPARTURE, ");
						sb.append(
								"	TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM )- NVL(B.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM ),B.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
						sb.append(
								"	TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM )- NVL(B.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM ),B.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
						sb.append("	TO_CHAR(B.COD_DTTM,'dd/mm/yyyy HH24MI') as COD_DTTM, ");
						sb.append("	TO_CHAR(B.GB_COD_DTTM,'dd/mm/yyyy HH24MI') as GB_COD_DTTM, ");
						sb.append("	TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM, VC.TERMINAL ");
						sb.append("	FROM VESSEL_CALL VC,GB_EDO EDO, BERTHING B WHERE");
						sb.append("	((VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB','CL') ) ");
						sb.append("	AND (VC.VV_CD = B.VV_CD AND B.SHIFT_IND = 1) ");
						sb.append(
								"	 AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')) ");
						// +" AND VC.TERMINAL='GB')"
						sb.append("  AND VC.VV_CD=EDO.VAR_NBR");
						sb.append("  AND (EDO.ADP_CUST_CD=:strCustCode ");
						sb.append("  OR EDO.CA_CUST_CD=:strCustCode");
						sb.append(" ) AND VC.VSL_NM = :vesselName AND VC.IN_VOY_NBR = :voyageNumber ");
						sb.append("  ORDER BY  VC.TERMINAL DESC, VC.VSL_NM, VC.IN_VOY_NBR");
					} else {
						sb = new StringBuffer();
						/*
						 * sql="SELECT VC.VV_CD,VC.VSL_NM,VC.IN_VOY_NBR  FROM" +" VESSEL_CALL VC WHERE "
						 * +" ((VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB') ) "
						 * +" AND VC.TERMINAL='GB')"
						 * +" AND nvl(VC.DECLARANT_CUST_CD,VC.CREATE_CUST_CD)='" +
						 * strCustCode+"' AND VC.VSL_NM = '" + vesselName + "' AND VC.IN_VOY_NBR = '" +
						 * voyageNumber + "'" +" ORDER BY VC.VSL_NM, VC.IN_VOY_NBR ";
						 */
						sb.append("   SELECT DISTINCT IN_VOY_NBR,VSL_NM,VC.VV_CD, VC.VV_CD VAR_NBR, ");
						// +" TO_CHAR(B.ATB_DTTM, 'dd/mm/yyyy HH24MI') as ARRIVAL ,"
						// +" TO_CHAR(B.ATU_DTTM, 'dd/mm/yyyy HH24MI') AS DEPARTURE, "
						sb.append(
								" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM )- NVL(B.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,B.ETB_DTTM ),B.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
						sb.append(
								" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM )- NVL(B.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,B.ETU_DTTM ),B.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
						sb.append(" TO_CHAR(B.COD_DTTM,'dd/mm/yyyy HH24MI') as COD_DTTM, ");
						sb.append(" TO_CHAR(B.GB_COD_DTTM,'dd/mm/yyyy HH24MI') as GB_COD_DTTM, ");
						sb.append(" TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM, VC.TERMINAL ");
						sb.append(
								" FROM VESSEL_CALL VC LEFT OUTER JOIN VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A') ");
						sb.append(" LEFT JOIN BERTHING B ON (VC.VV_CD = B.VV_CD AND B.SHIFT_IND = 1) ");
						sb.append(" WHERE VV_STATUS_IND IN ('PR','AP','AL','BR','UB','CL') ");
						sb.append(
								"  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT') ");
						// +"AND TERMINAL='GB'");
						sb.append("  AND (VD.CUST_CD = :strCustCode  OR VC.CREATE_CUST_CD = :strCustCode)");
						sb.append("  AND VC.VSL_NM = :vesselName AND VC.IN_VOY_NBR = :voyageNumber");
						sb.append("  ORDER BY VC.TERMINAL DESC, VSL_NM,IN_VOY_NBR");
					}
				}
			}
			log.info(strCustCode + ":" + strmodulecd + ":" + vesselName + ":" + voyageNumber
					+ "::Vessel List SQL-2--vietnd-EDO:: " + sql);
			paramMap.put("strCustCode", strCustCode);
			paramMap.put("vesselName", vesselName);
			paramMap.put("voyageNumber", voyageNumber);
			
			log.info(" *** getVesselVoyageNbrList SQL *****" + sb.toString());
			log.info(" *** getVesselVoyageNbrList params *****" + paramMap.toString());

			vesselvoyageList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<EdoValueObjectCargo>(EdoValueObjectCargo.class));
			
			for (EdoValueObjectCargo edoValueObject : vesselvoyageList) {
				if(StringUtils.isBlank(edoValueObject.getCod_dttm())) {
                 	edoValueObject.setCod_dttm(CommonUtility.deNull(edoValueObject.getGb_cod_dttm()));
				}
			} 
			log.info("END: *** getVesselVoyageNbrList Result *****" + vesselvoyageList.toString());
		} catch (Exception e) {
			log.info("Exception getVesselVoyageNbrList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getVesselVoyageNbrList  4 args END");
		}
		return vesselvoyageList;
	}

	@Override
	public VesselVoyValueObject getVesselInfo(String vv_cd) throws BusinessException {
		VesselVoyValueObject vessel = new VesselVoyValueObject();
		StringBuffer sb = new StringBuffer();
		List<VesselVoyValueObject> vesselvoyageList = new ArrayList<>();
		Map<String, String> paramMap = new HashMap<>();
		try {
			log.info("START: getVesselInfo vv_cd:" + vv_cd);
			sb.append("select VSL_NM as vslName,IN_VOY_NBR as voyNo from vessel_call where vv_cd =:vv_cd ");
			paramMap.put("vv_cd", vv_cd);
			log.info("query:" + sb.toString()+ "param:" + paramMap.toString());
			vesselvoyageList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<VesselVoyValueObject>(VesselVoyValueObject.class));
			if (vesselvoyageList.size() > 0) {
				return vesselvoyageList.get(0);
			}
			log.info("END: *** getVesselInfo Result *****" + vessel);
		} catch (Exception e) {
			log.info("Exception getVesselInfo : ", e);
			throw new BusinessException("M20804");
		} finally {
			log.info("getVesselInfo Ends");
		}
		return vessel;
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateWHIndicator(String stredoasnnbr, String strWhInd, String strWhAggrNbr, String strWhRemarks,
			String strFreeStgDays, String struserid) throws BusinessException {
		String strtransnbr = "0";
		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();
		StringBuilder strUpdatetrans = new StringBuilder();
		SqlRowSet rs1 = null;
		SqlRowSet rs3 = null;
		String stredostatus = "A";
		Map<String, String> paramMap = new HashMap<String, String>();
		sql1.append("SELECT EDO_STATUS FROM GB_EDO WHERE EDO_ASN_NBR=:stredoasnnbr");
		try {
			try {
				log.info("START: updateWHIndicator stredoasnnbr:" + stredoasnnbr + "strWhInd:" + strWhInd
						+ "strWhAggrNbr:" + strWhAggrNbr + "strWhRemarks:" + strWhRemarks + "strFreeStgDays:"
						+ strFreeStgDays + "struserid:" + struserid);
				paramMap.put("stredoasnnbr", stredoasnnbr);
				log.info("SQL" + sql1.toString() + "pstmt:" + param + paramMap);
				log.info(" *** updateWHIndicator params *****" + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
				while (rs1.next()) {
					stredostatus = CommonUtility.deNull(rs1.getString(1));
				}
				log.info("END: *** updateWHIndicator Result *****" + CommonUtility.deNull(stredostatus));
			} catch (Exception se) {
				log.info("Exception updateWHIndicator: " + se);
			}
			if (!(stredostatus.equalsIgnoreCase("A"))) {
				log.info("W/H Indicator cannot be updated since EDO_ASN_NBR " + stredoasnnbr + " already deleted");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				String sql3 = "SELECT MAX(TRANS_NBR) FROM GB_EDO_TRANS WHERE EDO_ASN_NBR=:stredoasnnbr";
				paramMap.put("stredoasnnbr", stredoasnnbr);
				log.info("SQL" + sql3.toString() + "pstmt:" + param + paramMap);
				log.info(" *** updateWHIndicator params *****" + paramMap.toString());
				
				rs3 = namedParameterJdbcTemplate.queryForRowSet(sql3.toString(), paramMap);
				while (rs3.next()) {
					strtransnbr = CommonUtility.deNull(rs3.getString(1));
				}
				
				if (strtransnbr.equalsIgnoreCase("") || strtransnbr == null) {
					strtransnbr = "0";
				} else {
					strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
				}
			}
			sql2.append(" UPDATE GB_EDO SET WH_IND=:strWhInd, WH_AGGR_NBR=:strWhAggrNbr, WH_REMARKS=:strWhRemarks, ");
			sql2.append(" FREE_STG_DAYS=:strFreeStgDays, LAST_MODIFY_USER_ID=:struserid, LAST_MODIFY_DTTM = sysdate ");
			sql2.append(" WHERE EDO_ASN_NBR=:stredoasnnbr ");
			log.info("SQL" + sql2.toString() + "pstmt:" + param + paramMap);
			paramMap.put("strWhInd", strWhInd);
			paramMap.put("strWhAggrNbr", strWhAggrNbr);
			paramMap.put("strWhRemarks", strWhRemarks);
			paramMap.put("strFreeStgDays", strFreeStgDays);
			paramMap.put("struserid", struserid);
			paramMap.put("stredoasnnbr", stredoasnnbr);
			int count1 = namedParameterJdbcTemplate.update(sql2.toString(), paramMap);
			log.info("SQL" + sql2.toString() + "pstmt:" + param + paramMap);
			log.info(" *** updateWHIndicator params *****" + paramMap.toString());
			
			if (count1 == 0) {
				throw new BusinessException("M1007");
			}

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				strUpdatetrans.append(" INSERT INTO GB_EDO_TRANS (TRANS_NBR, EDO_ASN_NBR, WH_IND, ");
				strUpdatetrans.append(" WH_AGGR_NBR, WH_REMARKS, FREE_STG_DAYS, LAST_MODIFY_USER_ID, ");
				strUpdatetrans.append(" LAST_MODIFY_DTTM) VALUES (:strtransnbr, :stredoasnnbr, :strWhInd,");
				strUpdatetrans.append(" :strWhAggrNbr, :strWhRemarks, :strFreeStgDays, :struserid, sysdate)");
				log.info("SQL" + strUpdatetrans.toString() + "pstmt:");
				paramMap.put("strtransnbr", strtransnbr);
				paramMap.put("stredoasnnbr", stredoasnnbr);
				paramMap.put("strWhInd", strWhInd);
				paramMap.put("strWhAggrNbr", strWhAggrNbr);
				paramMap.put("strWhRemarks", strWhRemarks);
				paramMap.put("strFreeStgDays", strFreeStgDays);
				paramMap.put("struserid", struserid);
				log.info(" *** updateWHIndicator params *****" + paramMap.toString());
				int count2 = namedParameterJdbcTemplate.update(strUpdatetrans.toString(), paramMap);
				if (count2 == 0) {
					throw new BusinessException("M20802");
				}
			}
		} catch (BusinessException e) {
			log.info("Exception updateWHIndicator : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updateWHIndicator : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO updateWHIndicator");
		}
	}

	@Override
	public String getSearchDetails(String strCustCode, String stredoasnnbr) throws BusinessException {
		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		boolean isShowEdoInfo = false;
		Map<String,String> paramMap = new HashMap<String,String>();
		sql1.append("SELECT * FROM GB_EDO WHERE EDO_ASN_NBR=:stredoasnnbr AND EDO_STATUS='A'");
		try {
			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_EDO);
			TextParaVO result = getParaCodeInfo(code);
			isShowEdoInfo = isShowEdoInfo(strCustCode, result);
			
			log.info(" *** getSearchDetails result *****" + isShowEdoInfo);
		} catch (Exception e) {
			log.info("Exception getSearchDetails Retriving text para error: ", e);
			throw new BusinessException("M4201");
		}

		if (isShowEdoInfo) {
			sql2.append(" SELECT V.VV_CD, V.VSL_NM, V.IN_VOY_NBR,V.TERMINAL FROM VESSEL_CALL V, GB_EDO E WHERE ");
			sql2.append(" V.VV_STATUS_IND IN ('PR','AP','AL','BR','UB') AND nvl(V.GB_CLOSE_BJ_IND,'N') = 'N' ");
			sql2.append(" AND V.VV_CD=E.VAR_NBR AND E.EDO_ASN_NBR=:stredoasnnbr ");
		} else {
			/*
			 * sql2="SELECT V.VV_CD, V.VSL_NM, V.IN_VOY_NBR  FROM "
			 * +"VESSEL_CALL V, GB_EDO E WHERE  "
			 * +"V.VV_STATUS_IND IN ('PR','AP','AL','BR','UB') AND "
			 * +"nvl(V.GB_CLOSE_BJ_IND,'N') = 'N' AND V.VV_CD=E.VAR_NBR AND "
			 * +"E.EDO_ASN_NBR='"+stredoasnnbr+"'"; //changed by vietnd02, get 3 values to
			 * fetch /*+"E.EDO_ASN_NBR='"+
			 * stredoasnnbr+"' AND nvl(V.DECLARANT_CUST_CD,V.CREATE_CUST_CD)='"
			 * +strCustCode+"'";
			 */
			sql2.append(" SELECT DISTINCT VC.VV_CD, VC.VSL_NM, VC.IN_VOY_NBR, VC.TERMINAL ");
			sql2.append(
					" FROM GB_EDO E, VESSEL_CALL VC LEFT OUTER JOIN VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A') ");
			sql2.append(" WHERE VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB') AND ");
			sql2.append(" nvl(VC.GB_CLOSE_BJ_IND,'N') = 'N' AND VC.VV_CD=E.VAR_NBR AND E.EDO_ASN_NBR =:stredoasnnbr ");
			sql2.append(" AND (VC.CREATE_CUST_CD =:strCustCode OR E.EDO_CREATE_CD =:strCustCode )");
		}
		String strsearchstring = "";
		String varnbr = "";
		String vslnm = "";
		String invoynbr = "";
		try {
			log.info("START: getSearchDetails strCustCode:" + strCustCode + "stredoasnnbr:" + stredoasnnbr);
			try {
				paramMap.put("stredoasnnbr", stredoasnnbr);
				log.info("SQL" + sql1.toString() + "pstmt:" + param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
				int recordcount = 0;
				while (rs1.next()) {
					recordcount++;
				}
				if (recordcount == 0) {
					throw new BusinessException("M20811");
				}
			} catch (BusinessException e) {
				log.info("Exception getSearchDetails: ", e);
				throw new BusinessException(e.getMessage());
			} catch (Exception e) {
				log.info("Exception getSearchDetails: ", e);
				throw new BusinessException("M4201");
			}

			try {
				paramMap.put("strCustCode", strCustCode);
				paramMap.put("stredoasnnbr", stredoasnnbr);
				log.info("SQL" + sql2.toString() + "pstmt:" + param + paramMap);
				rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2.toString(), paramMap);
				while (rs2.next()) {
					varnbr = CommonUtility.deNull(rs2.getString(1));
					vslnm = CommonUtility.deNull(rs2.getString(2));
					invoynbr = CommonUtility.deNull(rs2.getString(3));
				}
			} catch (Exception e) {
				log.info("Exception getSearchDetails: ", e);
				throw new BusinessException("M4201");
			}
			strsearchstring = varnbr + ";" + vslnm + "--" + invoynbr;
			if (strsearchstring.equalsIgnoreCase(";--")) {
				throw new BusinessException("M20810");
			}
			
			log.info("END: *** getSearchDetails Result *****" + CommonUtility.deNull(strsearchstring));
		} catch (BusinessException e) {
			log.info("Exception getSearchDetails: ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getSearchDetails: ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getSearchDetails");
		}
		return strsearchstring;
	}

	@Override
	public AdpValueObject getTaEndorserNmByUENNo(Criteria criteria) {
		String sql = "";
		AdpValueObject adpValueObject = new AdpValueObject();
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		try {
			log.info("START:  *** getTaEndorserNmByUENNo Dao Start criteria : *** " + criteria.toString());
			String uenNo = CommonUtil.deNull(criteria.getPredicates().get("uenNo"));
			paramMap.put("uenNo", uenNo);
			sb.append(" SELECT CT.CUST_CD AS AdpCustCd, co.CO_NM AS AdpNm");
			sb.append(" SELECT CT.CUST_CD AS AdpCustCd, co.CO_NM AS AdpNm");
			sb.append(" FROM company_code co, ");
			sb.append("   customer ct  ");
			sb.append("WHERE CO.CO_CD = ct.cust_cd ");
			sb.append(" AND co.rec_status    ='A'");
			sb.append(" AND co.allow_jponline='Y' ");
			sb.append(" AND ct.uen_nbr = :uenNo ");
			sb.append(" AND INSTR( ");
			sb.append("(SELECT VALUE FROM TEXT_PARA WHERE PARA_CD ='JP_CO_XCLD'");
			sb.append("   ), CT.CUST_CD ) <= 0 ");
			sql = sb.toString();
			adpValueObject = namedParameterJdbcTemplate.queryForObject(sql, paramMap,
					new BeanPropertyRowMapper<AdpValueObject>(AdpValueObject.class));
			log.info("END: *** getTaEndorserNmByUENNo SQL *****" + sql + param + paramMap);
			log.info("END: *** getTaEndorserNmByUENNo Result *****" + adpValueObject.toString());
		} catch (Exception e) {
			log.info("getTaEndorserNmByUENNo Exception" ,e);

		} finally {
			log.info("END: *** getTaEndorserNmByUENNo  END *****");
		}
		return adpValueObject;
	}

	@Override
	public String getVslStatus(String varno) throws BusinessException {
		String  sql = "";
		String vsl_status = "";
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getVslStatus DAO varno:" + varno);
			sql = " SELECT VV_STATUS_IND from VESSEL_CALL where  VV_CD=:varno ";
			paramMap.put("varno", varno);

			log.info("SQL" + sql + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				vsl_status = rs.getString("VV_STATUS_IND");
			}
			log.info("END: getVslStatus vsl_status:" + vsl_status);
		} catch (Exception e) {
			log.info("Exception getVslStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVslStatus ");
		}
		return vsl_status;
	}

	@Override
	public List<EdoValueObjectCargo> viewEdoDetails(String stredoasnnbr) throws BusinessException {
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();
		List<EdoValueObjectCargo> viewEdoDetails = new ArrayList<EdoValueObjectCargo>();
		Map<String,String> paramMap = new HashMap<String,String>();
		// MCC get EPC_IND from Manifest instead of EDO table
		sql1.append(" SELECT B.CRG_TYPE_CD, B.CRG_TYPE_NM, A.HS_CODE, A.CRG_DES, M.MFT_MARKINGS, ");
		sql1.append(" E.CRG_STATUS, A.DG_IND, A.STG_TYPE, C.PKG_TYPE_CD, ");
		sql1.append(" C.PKG_DESC, A.NBR_PKGS, A.CONS_NM, E.NOM_WT, E.NOM_VOL, A.EPC_IND");
		sql1.append(" FROM MANIFEST_DETAILS A, CRG_TYPE B, ");
		sql1.append(" PKG_TYPES C, GB_EDO E, MFT_MARKINGS M WHERE A.CRG_TYPE=B.CRG_TYPE_CD AND ");
		sql1.append(" A.PKG_TYPE=C.PKG_TYPE_CD AND A.MFT_SEQ_NBR=E.MFT_SEQ_NBR AND");
		sql1.append(" A.MFT_SEQ_NBR=M.MFT_SQ_NBR AND E.EDO_ASN_NBR=:stredoasnnbr");
		log.info("viewEdoDetails() sql1 :" + sql1);
		// SQL2 ammended by THANHPT6 for CR of JPOnline&SMART Enhancement. FPT,
		// 2016-01-15.
		sql2.append(" SELECT D.*, CC.CO_NM TA_CUST_NM FROM ( SELECT CC.CO_NM, G.* FROM GB_EDO G ");
		sql2.append(" LEFT JOIN CUST_ACCT C ON G.ACCT_NBR = C.ACCT_NBR ");
		sql2.append(" LEFT JOIN COMPANY_CODE CC ON C.CUST_CD = CC.CO_CD WHERE EDO_ASN_NBR=:stredoasnnbr ");
		sql2.append(" ) D LEFT JOIN COMPANY_CODE CC ON D.TA_CUST_CD = CC.CO_CD ");
		log.info("viewEdoDetails() sql2 :" + sql2);
		// Vector edoviewlistvector= new Vector();
		String crg_status = "";
		try {
			log.info("START: viewEdoDetails stredoasnnbr:" + stredoasnnbr);
			paramMap.put("stredoasnnbr", stredoasnnbr);
			log.info("SQL" + sql1.toString() + "pstmt:" + param + paramMap);
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
			EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
			while (rs1.next()) {
				String edo_asn_nbr = stredoasnnbr;
				String crg_type_cd = CommonUtility.deNull(rs1.getString("CRG_TYPE_CD"));
				String crg_type_nm = CommonUtility.deNull(rs1.getString("CRG_TYPE_NM"));
				String hs_code = CommonUtility.deNull(rs1.getString("HS_CODE"));
				String crg_des = CommonUtility.deNull(rs1.getString("CRG_DES"));
				String mft_markings = CommonUtility.deNull(rs1.getString("MFT_MARKINGS"));
				crg_status = CommonUtility.deNull(rs1.getString("CRG_STATUS"));
				String stg_type = CommonUtility.deNull(rs1.getString("STG_TYPE"));
				String dg_ind = CommonUtility.deNull(rs1.getString("DG_IND"));
				String pkg_type_cd = CommonUtility.deNull(rs1.getString("PKG_TYPE_CD"));
				String pkg_type_desc = CommonUtility.deNull(rs1.getString("PKG_DESC"));
				String nbr_pkgs = CommonUtility.deNull(rs1.getString("NBR_PKGS"));
				String cons_nm = CommonUtility.deNull(rs1.getString("CONS_NM"));
				String gross_wt = CommonUtility.deNull(rs1.getString("NOM_WT"));
				String gross_vol = CommonUtility.deNull(rs1.getString("NOM_VOL"));
				String deliveryToEPC = CommonUtility.deNull(rs1.getString("EPC_IND"));
				edoValueObject.setEdoAsnNbr(edo_asn_nbr);
				edoValueObject.setCrgTypeCd(crg_type_cd);
				edoValueObject.setCrgTypeNm(crg_type_nm);
				edoValueObject.setHsCode(hs_code);
				edoValueObject.setCrgDes(crg_des);
				edoValueObject.setMftMarkings(mft_markings);
				edoValueObject.setCrgStatus(crg_status);
				edoValueObject.setStgType(stg_type);
				edoValueObject.setDgInd(dg_ind);
				edoValueObject.setPkgTypeCd(pkg_type_cd);
				edoValueObject.setPkgTypeDesc(pkg_type_desc);
				edoValueObject.setNbrPkgs(nbr_pkgs);
				edoValueObject.setConsNm(cons_nm);
				edoValueObject.setNomWeight(gross_wt);
				edoValueObject.setNomVolume(gross_vol);
				// MCC get deliveryToEPC value from Manifest
				edoValueObject.setDeliveryToEPC(deliveryToEPC);
				viewEdoDetails.add(edoValueObject);

			}
			paramMap.put("stredoasnnbr", stredoasnnbr);
			log.info("SQL" + sql2.toString() + param + paramMap);
			rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2.toString(), paramMap);
			while (rs2.next()) {
				edoValueObject.setBlNbr(CommonUtility.deNull(rs2.getString("BL_NBR")));
				// String adpcustcd=CommonUtility.deNull(rs2.getString("ADP_CUST_CD"));
				String adpictdbcrnbr = CommonUtility.deNull(rs2.getString("ADP_IC_TDBCR_NBR"));
				/*
				 * if (!(adpcustcd.equals(""))) { edoValueObject.setAdpNbr(adpcustcd); } if
				 * (!(adpictdbcrnbr.equals(""))){}
				 */

				edoValueObject.setAdpNbr(adpictdbcrnbr);
				edoValueObject.setAdpNm(CommonUtility.deNull(rs2.getString("ADP_NM")));
				edoValueObject.setEdoNbrPkgs(CommonUtility.deNull(rs2.getString("NBR_PKGS")));
				// String cacustcd=CommonUtility.deNull(rs2.getString("CA_CUST_CD"));
				String caictdbcrnbr = CommonUtility.deNull(rs2.getString("CA_IC_TDBCR_NBR"));

				/*
				 * if (!(cacustcd.equals(""))){ edoValueObject.setCrgAgtNbr(cacustcd); } if
				 * (!(caictdbcrnbr.equals(""))) {}
				 */
				if(caictdbcrnbr.isEmpty()) {
					caictdbcrnbr = getUENNo(CommonUtility.deNull(rs2.getString("CA_NM")));
				}

				edoValueObject.setCrgAgtNbr(caictdbcrnbr);
				edoValueObject.setCrgAgtNm(CommonUtility.deNull(rs2.getString("CA_NM")));
				// String aacustcd=CommonUtility.deNull(rs2.getString("AA_CUST_CD"));
				String aaictdbcrnbr = CommonUtility.deNull(rs2.getString("AA_IC_TDBCR_NBR"));
				/*
				 * if (!(aacustcd.equals(""))){ edoValueObject.setAgtAttNbr(aacustcd); } if
				 * (!(aaictdbcrnbr.equals(""))) {}
				 */
				edoValueObject.setAgtAttNbr(aaictdbcrnbr);
				edoValueObject.setAgtAttNm(CommonUtility.deNull(rs2.getString("AA_NM")));
				edoValueObject.setDeliveryTo(CommonUtility.deNull(rs2.getString("EDO_DELIVERY_TO")));
				edoValueObject.setAcctNbr(CommonUtility.deNull(rs2.getString("ACCT_NBR")));
				edoValueObject.setMftSeqNbr(CommonUtility.deNull(rs2.getString("MFT_SEQ_NBR")));
				String dnnbrpkgs = CommonUtility.deNull(rs2.getString("DN_NBR_PKGS"));
				String transnbrpkgs = CommonUtility.deNull(rs2.getString("TRANS_NBR_PKGS"));
				String transdnnbrpkgs = CommonUtility.deNull(rs2.getString("TRANS_DN_NBR_PKGS"));
				String dis_type = CommonUtility.deNull(rs2.getString("DIS_TYPE"));
				edoValueObject.setDisType(dis_type);
				/*
				 * String strStfInd = CommonUtility.deNull(rs2.getString("STUFF_IND"));//added
				 * by vani -- 10th Oct,03, removed by vinayak
				 * edoValueObject.setStfInd(strStfInd); added by vani -- 10th Oct,03
				 */
				/*
				 * String amountdelivered="0"; if (crg_status.equalsIgnoreCase("L")) {
				 * amountdelivered=dnnbrpkgs; } if (crg_status.equalsIgnoreCase("T")){ if
				 * (!(transnbrpkgs.equalsIgnoreCase("0"))) { amountdelivered=transnbrpkgs;
				 * }else{ if (!(transdnnbrpkgs.equalsIgnoreCase("0"))){
				 * amountdelivered=transdnnbrpkgs; } } }
				 */
				edoValueObject.setDnNbrPkgs(dnnbrpkgs);
				edoValueObject.setTransNbrPkgs(transnbrpkgs);
				edoValueObject.setTransDnNbrPkgs(transdnnbrpkgs);
				String edostatus = CommonUtility.deNull(rs2.getString("EDO_STATUS"));
				edoValueObject.setEdoStatus(edostatus);
				String appointedadpcustcd = CommonUtility.deNull(rs2.getString("APPOINTED_ADP_CUST_CD"));
				edoValueObject.setAppointedAdpCustCd(appointedadpcustcd);
				String appointedadpictdbcrnbr = CommonUtility.deNull(rs2.getString("APPOINTED_ADP_IC_TDBCR_NBR"));
				edoValueObject.setAppointedAdpIcTdbcrNbr(appointedadpictdbcrnbr);
				String appointedadpnm = CommonUtility.deNull(rs2.getString("APPOINTED_ADP_NM"));
				edoValueObject.setAppointedAdpNm(appointedadpnm);
				String whInd = CommonUtility.deNull(rs2.getString("WH_IND"));
				edoValueObject.setWhInd(whInd);
				String freeStgDays = CommonUtility.deNull(rs2.getString("FREE_STG_DAYS"));
				edoValueObject.setFreeStgDays(freeStgDays);
				String whRemarks = CommonUtility.deNull(rs2.getString("WH_REMARKS"));
				edoValueObject.setWhRemarks(whRemarks);
				String whAggrNbr = CommonUtility.deNull(rs2.getString("WH_AGGR_NBR"));
				edoValueObject.setWhAggrNbr(whAggrNbr);

				// HaiTTH1 added on 16/1/2014
				String cust_nm = CommonUtility.deNull(rs2.getString("CO_NM"));
				edoValueObject.setAcct_nm(cust_nm);
				edoValueObject.setAdp_nbr_pkgs(CommonUtility.deNull(rs2.getString("ADP_NBR_PKGS")));

				// Begin ThanhPT6, CR of JPOnline&SMART Enhancement, 06/01/2016
				String taUenNo = CommonUtility.deNull(rs2.getString("TA_CR_UEN_NBR"));
				String taCCode = CommonUtility.deNull(rs2.getString("TA_CUST_NM"));
				String taNmByJP = CommonUtility.deNull(rs2.getString("TA_NAME"));
				edoValueObject.setTaUenNo(taUenNo);
				edoValueObject.setTaNmByJP(taNmByJP);
				edoValueObject.setTaCCode(taCCode);
				// End ThanhPT6
				// MCC get deliveryToEPC value
				// edoValueObject.setDeliveryToEPC(CommonUtility.deNull(rs2.getString("EPC_IND")));
				// //get EPC_IND from Manifest
			}

			List<ContainerDetails> containervector = new ArrayList<ContainerDetails>();
			containervector = getContinerDetailsVector("", stredoasnnbr);
			edoValueObject.setContinerDetails(containervector);
			viewEdoDetails.add(edoValueObject);
			
			log.info("END: *** viewEdoDetails Result *****" + viewEdoDetails.size());
		} catch (Exception e) {
			log.info("Exception viewEdoDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO viewEdoDetails");
		}
		return viewEdoDetails;
	}

	private String getUENNo(String cargoAgent) throws BusinessException {

		String sql = "";
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:  *** getUENNo Dao Start : *** " + cargoAgent);

			sb.append("SELECT C.CO_NM, C.CO_CD, CUST.UEN_NBR FROM COMPANY_CODE C ");
			sb.append(" LEFT JOIN CUSTOMER CUST on CUST.CUST_CD = C.CO_CD ");
			sb.append(" WHERE C.CO_NM = :cargoAgent");

			sql = sb.toString();

			paramMap.put("cargoAgent", cargoAgent);
			
			log.info(" *** getUENNo SQL *****" + sql);
			log.info(" *** getUENNo params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				log.info(" *** getUENNo result *****" + rs.getString("UEN_NBR"));
				return rs.getString("UEN_NBR");
			}
		} catch (NullPointerException ne) {
			log.info("Exception getUENNo : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUENNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getUENNo  END *****");
		}
		return "";
	}

	private List<ContainerDetails> getContinerDetailsVector(String mftseqnbr, String stredoasnnbr) throws BusinessException {
		List<ContainerDetails> containervector = new ArrayList<ContainerDetails>();
		StringBuilder sql = new StringBuilder();
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		if (mftseqnbr.equalsIgnoreCase("")) {
			sql.append(" SELECT UNIQUE A.CNTR_TYPE,A.CNTR_SIZE,B.CNTR_NBR, B.CNTR_BL_SEQ ");
			sql.append(" FROM MANIFEST_DETAILS A, BL_CNTR_DETAILS B, GB_EDO E WHERE ");
			sql.append(" A.MFT_SEQ_NBR=B.MFT_SEQ_NBR AND A.MFT_SEQ_NBR= ");
			sql.append(" E.MFT_SEQ_NBR AND A.CRG_TYPE IN ('02','03') AND EDO_ASN_NBR=:stredoasnnbr ");
			sql.append(" ORDER BY B.CNTR_BL_SEQ ");
		} else {
			sql.append(" SELECT A.CNTR_TYPE,A.CNTR_SIZE,B.CNTR_NBR, B.CNTR_BL_SEQ FROM ");
			sql.append(" MANIFEST_DETAILS A, BL_CNTR_DETAILS B WHERE ");
			sql.append(" A.MFT_SEQ_NBR=B.MFT_SEQ_NBR AND A.CRG_TYPE IN ('02','03') AND A.MFT_SEQ_NBR=:mftseqnbr ");
			sql.append(" ORDER BY B.CNTR_BL_SEQ ");
		}

		try {
			log.info("START: getContinerDetailsVector mftseqnbr:" + mftseqnbr + "stredoasnnbr:" + stredoasnnbr);
			if (mftseqnbr.equalsIgnoreCase("")) {
				paramMap.put("stredoasnnbr", stredoasnnbr);
			} else {
				paramMap.put("mftseqnbr", mftseqnbr);
			}
			log.info("SQL" + sql.toString() + "pstmt:" + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				ContainerDetails containerDetails = new ContainerDetails();
				containerDetails.setCntrType(CommonUtility.deNull(rs.getString(1)));
				containerDetails.setCntrSize(CommonUtility.deNull(rs.getString(2)));
				containerDetails.setCntrNbr(CommonUtility.deNull(rs.getString(3)));
				containervector.add(containerDetails);
			}
			log.info("END: *** getContinerDetailsVector Result *****" + containervector.size());
			
		} catch (Exception e) {
			log.info("Exception getContinerDetailsVector : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getContinerDetailsVector");
		}
		return containervector;
	}

	@Override
	public List<AdpValueObject> getAdpList(String edoNbr) throws BusinessException {
		List<AdpValueObject> AdpValList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String sql;
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		AdpValueObject adpValueObject = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:  *** getAdpList Dao Start edoNbr : *** " + edoNbr);
			sb.append("select ge.ADP_CUST_CD , ge.ADP_IC_TDBCR_NBR ,");
			sb.append(" ge.ADP_NM , ge.ADP_NBR_PKGS , ge.ADP_CONTACT_NBR  from gb_edo ge ");
			sb.append("where ge.EDO_ASN_NBR =:edoNbr and ge.ADP_IC_TDBCR_NBR is not null ");
			sql = sb.toString();
			paramMap.put("edoNbr", edoNbr);
			
			log.info(" *** getAdpList SQL *****" + sql);
			log.info(" *** getAdpList params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				adpValueObject = new AdpValueObject();
				adpValueObject.setAdpCustCd(CommonUtility.deNull(rs.getString("ADP_CUST_CD")));
				adpValueObject.setAdpIcTdbcrNbr(CommonUtility.deNull(rs.getString("ADP_IC_TDBCR_NBR")));
				adpValueObject.setAdpNm(CommonUtility.deNull(rs.getString("ADP_NM")));
				adpValueObject.setAdpNbrPkgs(CommonUtility.deNull(rs.getString("ADP_NBR_PKGS")));
				adpValueObject.setAdpContact(CommonUtility.deNull(rs.getString("ADP_CONTACT_NBR")));
				AdpValList.add(adpValueObject);
			}
			sb1.append("select sub.TRUCKER_IC  , sub.TRUCKER_NM ,");
			sb1.append(" sub.TRUCKER_NBR_PKGS , sub.TRUCKER_CO_CD ,");
			sb1.append(" sub.TRUCKER_CONTACT_NBR  from sub_adp sub ");
			sb1.append(
					"where sub.ESN_ASN_NBR =:edoNbr and STATUS_CD = 'A' and EDO_ESN_IND = '1' order by SUB_ADP_NBR ");
			paramMap.put("edoNbr", edoNbr);
			
			log.info(" *** getAdpList SQL *****" + sb1.toString());
			log.info(" *** getAdpList params *****" + paramMap.toString());
			
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			while (rs1.next()) {
				adpValueObject = new AdpValueObject();
				adpValueObject.setAdpCustCd(CommonUtility.deNull(rs1.getString("TRUCKER_CO_CD")));
				adpValueObject.setAdpIcTdbcrNbr(CommonUtility.deNull(rs1.getString("TRUCKER_IC")));
				adpValueObject.setAdpNm(CommonUtility.deNull(rs1.getString("TRUCKER_NM")));
				adpValueObject.setAdpNbrPkgs(CommonUtility.deNull(rs1.getString("TRUCKER_NBR_PKGS")));
				adpValueObject.setAdpContact(CommonUtility.deNull(rs1.getString("TRUCKER_CONTACT_NBR")));
				AdpValList.add(adpValueObject);
			}
			log.info("END: *** getAdpList  END AdpValList *****" + AdpValList.toString());
		} catch (Exception e) {
			log.info("Exception getAdpList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getAdpList DAO END AdpValList *****");
		}
		return AdpValList;
	}

	// ejb.sessionBeans.gbms.cargo.edo--->EdoEjb-->getTaEndorserNmByUENNo()
	@Override
	public AdpValueObject getTaEndorserNmByUENNo(String uenNo) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		AdpValueObject adpValueObject = new AdpValueObject();
		StringBuilder sb = new StringBuilder();
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:  *** getTaEndorserNmByUENNo Dao Start criteria : *** " + uenNo);
			paramMap.put("uenNo", uenNo);
			sb.append(" SELECT CT.CUST_CD, co.CO_NM");
			sb.append(" FROM COMPANY_CODE co,  ");
			sb.append("   CUSTOMER ct     ");
			sb.append("WHERE CO.CO_CD = ct.cust_cd ");
			sb.append(" AND co.rec_status    ='A'");
			sb.append(" AND co.allow_jponline='Y' ");
			sb.append(" AND ct.uen_nbr = :uenNo ");
			sb.append(" AND INSTR( ");
			sb.append(" (SELECT VALUE FROM TEXT_PARA WHERE PARA_CD ='JP_CO_XCLD'");
			sb.append("   ), CT.CUST_CD ) <= 0 ");
			sql = sb.toString();

			log.info("SQL" + sql + param + paramMap);
			paramMap.put("uenNo", uenNo);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				adpValueObject.setAdpCustCd(rs.getString("CUST_CD"));
				adpValueObject.setAdpNm(rs.getString("CO_NM"));
			}
			log.info("END: *** getTaEndorserNmByUENNo  *****adpValueObject:" + adpValueObject.toString());
		} catch (NullPointerException e) {
			log.info("Exception getTaEndorserNmByUENNo : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTaEndorserNmByUENNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getTaEndorserNmByUENNo  END *****");
		}
		return adpValueObject;
	}

	// ejb.sessionBeans.gbms.cargo.edo--->EdoEjb-->getAdpDetails()
	@Override
	public AdpValueObject getAdpDetails(String adpIcTdbcrNbr) throws BusinessException {
		if (StringUtils.isBlank(adpIcTdbcrNbr)) {
			return new AdpValueObject();
		}
		String sql = "";
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();
		AdpValueObject adpValueObject = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:  *** getAdpDetails Dao Start : *** " + adpIcTdbcrNbr);

			sb.append("SELECT CO_NM, CO_CD, NVL(PHONE1_NBR, PHONE2_NBR) PHONE1_NBR FROM customer cust ");
			sb.append(" LEFT JOIN company_code cc on cust.cust_cd = cc.co_cd ");
			sb.append(" LEFT JOIN cust_contact ct on cust.cust_cd = ct.CUST_CD ");
			sb.append(" WHERE TDB_CR_NBR = :adpIcTdbcrNbr or UEN_NBR = :adpIcTdbcrNbr");
			sb.append(" and cc.rec_status = 'A'");

			sql = sb.toString();

			sb1.append("SELECT DISTINCT(JC.CUST_NAME) CO_NM, ");
			sb1.append(" JC.CUST_CD CO_CD, ");
			sb1.append(" NVL(CT.PHONE1_NBR, CT.PHONE2_NBR) PHONE1_NBR ");
			sb1.append(" FROM JC_CARDDTL JC  LEFT JOIN CUST_CONTACT CT ");
			sb1.append(" ON JC.CUST_CD  = CT.CUST_CD ");
			sb1.append(
					" WHERE UPPER(jc.passport_no) = :adpIcTdbcrNbr OR UPPER(jc.FIN_NO)=:adpIcTdbcrNbr OR UPPER(jc.NRIC_NO )= :adpIcTdbcrNbr");

			String sql2 = sb1.toString();
			paramMap.put("adpIcTdbcrNbr", adpIcTdbcrNbr);
			
			log.info(" *** getAdpDetails SQL *****" + sql);
			log.info(" *** getAdpDetails params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			adpValueObject = new AdpValueObject();
			adpValueObject.setAdpIcTdbcrNbr(adpIcTdbcrNbr);
			if (rs.next()) {
				adpValueObject.setAdpCustCd(CommonUtility.deNull(rs.getString("CO_CD")));
				adpValueObject.setAdpNm(CommonUtility.deNull(rs.getString("CO_NM")));
				adpValueObject.setAdpContact(CommonUtility.deNull(rs.getString("PHONE1_NBR")));
			} else {
				paramMap.put("adpIcTdbcrNbr", adpIcTdbcrNbr);
				
				log.info(" *** getAdpDetails SQL *****" + sql2);
				log.info(" *** getAdpDetails params *****" + paramMap.toString());

				
				rs = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
				if (rs.next()) {
					adpValueObject.setAdpCustCd(CommonUtility.deNull(rs.getString("CO_CD")));
					adpValueObject.setAdpNm(CommonUtility.deNull(rs.getString("CO_NM")));
					adpValueObject.setAdpContact(CommonUtility.deNull(rs.getString("PHONE1_NBR")));
				} else {
					adpValueObject.setAdpCustCd("");
					adpValueObject.setAdpNm("");
					adpValueObject.setAdpContact("");
				}
			}
			
			log.info(" *** getAdpDetails result *****" + adpValueObject.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getAdpDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getAdpDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getAdpDetails  END *****");
		}
		return adpValueObject;
	}

	// ejb.sessionBeans.gbms.cargo.edo--->EdoEjb-->getCompanyName()
	@Override
	public String getCompanyName(String strNbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		String sql;
		String strNewNm = "";
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:  *** getCompanyName Dao Start : *** " + strNbr);
			sb.append("SELECT A.CO_NM FROM COMPANY_CODE A, CUSTOMER B WHERE ");
			sb.append("A.CO_CD=B.CUST_CD AND (UPPER(B.TDB_CR_NBR)=UPPER(:strNbr)");
			sb.append("OR UPPER(B.UEN_NBR)=UPPER(:strNbr))");
			sql = sb.toString();
			log.info("END: *** getCompanyName SQL *****" + sql);
			paramMap.put("strNbr", strNbr.toUpperCase());
			log.info(" *** getCompanyName params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				strNewNm = CommonUtil.deNull(rs.getString("CO_NM"));
			}
			log.info("END: *** getCompanyName *****" + CommonUtility.deNull(strNewNm));
		} catch (NullPointerException e) {
			log.info("Exception getCompanyName : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCompanyName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getCompanyName  END *****");
		}

		return strNewNm;
	}

	// ejb.sessionBeans.gbms.cargo.edo--->EdoEjb-->getVesselScheme()
	@Override
	public String getVesselScheme(String vvCd) throws BusinessException {
		SqlRowSet rs = null;
		String scheme = " ";
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:  *** getVesselScheme Dao Start criteria : *** " + vvCd);
			String sql = "SELECT SCHEME FROM VESSEL_CALL WHERE VV_CD =:vvCd";
			paramMap.put("vvCd", vvCd);
			
			log.info(" *** getVesselScheme SQL *****" + sql);
			log.info(" *** getVesselScheme params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				scheme = CommonUtil.deNull(rs.getString("SCHEME"));
			}

			log.info("END: *** getVesselScheme Result *****" + CommonUtility.deNull(scheme));
		} catch (Exception e) {
			log.info("Exception getVesselScheme : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getVesselScheme  END *****");
		}
		return scheme;
	}

	// ejb.sessionBeans.gbms.cargo.edo--->EdoEjb-->getEdoNbrPkgs()
	@Override
	public String getEdoNbrPkgs(String mftSeqNbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		String sql = "";
		String strNbrPkgs = "";
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:  *** getEdoNbrPkgs Dao Start : *** " + mftSeqNbr);
			sb.append("SELECT NBR_PKGS-(EDO_NBR_PKGS+NVl(CUT_OFF_NBR_PKGS,0)+ ");
			sb.append("NVL(NBR_PKGS_IN_PORT,0))  NBR_PKG FROM MANIFEST_DETAILS  ");
			sb.append("WHERE MFT_SEQ_NBR= :mftSeqNbr");
			sql = sb.toString();
			paramMap.put("mftSeqNbr", mftSeqNbr);
			
			log.info(" *** getEdoNbrPkgs SQL *****" + sql);
			log.info(" *** getEdoNbrPkgs params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				strNbrPkgs = CommonUtil.deNull(rs.getString("NBR_PKG"));
			}
			log.info("END: *** getEdoNbrPkgs Result *****" + CommonUtility.deNull(strNbrPkgs));
		} catch (NullPointerException e) {
			log.info("Exception getEdoNbrPkgs : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEdoNbrPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getEdoNbrPkgs  END *****");
		}
		return strNbrPkgs;
	}

	// ejb.sessionBeans.gbms.cargo.edo -->EdoEjb -->updateVettedEdo()
	@Override
	public void updateVettedEdo(String stredoasnnbr, String stredostatus, String struserid) throws BusinessException {
		SqlRowSet rslog = null;
		SqlRowSet rs1 = null;
		StringBuffer strUpdatetrans = new StringBuffer();

		String strnewedostatus = "";
		String strdnnbrpkgs = "0";
		String strtransnbrpkgs = "0";
		String strtransdnnbrpkgs = "0";
		String sql1 = "";
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: updateVettedEdo  DAO  Start Obj stredoasnnbr:" + stredoasnnbr + " stredostatus: " + stredostatus + " struserid " + struserid);
			sql1 = "SELECT EDO_STATUS, DN_NBR_PKGS, TRANS_NBR_PKGS,TRANS_DN_NBR_PKGS, MFT_SEQ_NBR FROM GB_EDO WHERE EDO_ASN_NBR=:stredoasnnbr ";

			String sql2 = "UPDATE GB_EDO SET EDO_STATUS =:stredostatus', LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID =:struserid WHERE EDO_ASN_NBR=:stredoasnnbr";
			String strtransnbr = "0";
			String sqllog = "SELECT MAX(TRANS_NBR) FROM GB_EDO_TRANS  WHERE EDO_ASN_NBR=:stredoasnnbr";

			log.info("updateVettedEdo SQL" + sql1);

			paramMap.put("stredoasnnbr", stredoasnnbr);
			
			log.info(" *** updateVettedEdo params *****" + paramMap.toString());

			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);

			while (rs1.next()) {
				strnewedostatus = CommonUtility.deNull(rs1.getString("EDO_STATUS"));
				strdnnbrpkgs = CommonUtility.deNull(rs1.getString("DN_NBR_PKGS"));
				strtransnbrpkgs = CommonUtility.deNull(rs1.getString("TRANS_NBR_PKGS"));
				strtransdnnbrpkgs = CommonUtility.deNull(rs1.getString("TRANS_DN_NBR_PKGS"));
			}
			if (strnewedostatus.equalsIgnoreCase("X")) {
				throw new BusinessException("M20814");
			}
			if (stredostatus.equalsIgnoreCase("V")) {
				checkPackageAmendDelete(stredoasnnbr, strdnnbrpkgs, strtransnbrpkgs, strtransdnnbrpkgs, "Vetting");
			}
			log.info("updateVettedEdo SQL" + sql2);

			paramMap.put("stredostatus", stredostatus);
			paramMap.put("struserid", struserid);
			paramMap.put("stredoasnnbr", stredoasnnbr);

			log.info(" *** updateVettedEdo params *****" + paramMap.toString());
			
			int count = namedParameterJdbcTemplate.update(sql2, paramMap);
			if (count == 0) {
				throw new BusinessException("M1007");
			}

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				try {
					paramMap.put("stredoasnnbr", stredoasnnbr);
					
					log.info("updateVettedEdo SQL" + sqllog);
					log.info(" *** updateVettedEdo params *****" + paramMap.toString());
					
					rslog = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);

					while (rslog.next()) {
						strtransnbr = CommonUtility.deNull(rslog.getString(1));
					}
					if (strtransnbr.equalsIgnoreCase("") || strtransnbr == null) {
						strtransnbr = "0";
					} else {
						strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
					}
				} catch (NullPointerException e) {
					log.info("Exception updateVettedEdo : ", e);
					throw new BusinessException("M4201");
				} catch (Exception e) {
					log.info("Exception updateVettedEdo : ", e);
					throw new BusinessException("M4201");
				} finally {
					log.info("END: updateVettedEdo  DAO  END");
				}

				strUpdatetrans.append(
						"INSERT INTO GB_EDO_TRANS (TRANS_NBR,EDO_ASN_NBR, EDO_STATUS, LAST_MODIFY_DTTM,LAST_MODIFY_USER_ID) VALUES");
				strUpdatetrans.append(":strtransnbr,:stredoasnnbr,:stredostatus, sysdate,:struserid");

				paramMap.put("strtransnbr", strtransnbr);
				paramMap.put("stredoasnnbr", stredoasnnbr);
				paramMap.put("stredostatus", stredostatus);
				paramMap.put("struserid", struserid);

				log.info("updateVettedEdo SQL" + strUpdatetrans.toString());
				log.info(" *** updateVettedEdo params *****" + paramMap.toString());
				
				int count1 = namedParameterJdbcTemplate.update(strUpdatetrans.toString(), paramMap);
				if (count1 == 0) {
					throw new BusinessException("M1007");
				}
			}

		} catch (NullPointerException e) {
			log.info("Exception updateVettedEdo : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception updateVettedEdo : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updateVettedEdo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateVettedEdo  DAO  END");
		}
	}

	// ejb.sessionBeans.gbms.cargo.edo -->EdoEjb -->checkPackageAmendDelete()
	public void checkPackageAmendDelete(String stredoasnnbr, String strdnnbrpkgs, String strtransnbrpkgs,
			String strtransdnnbrpkgs, String strmethod) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		String strcrgstat = "";
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: checkPackageAmendDelete  DAO  Start Obj " + "stredoasnnbr: " + stredoasnnbr + "strdnnbrpkgs: " + strdnnbrpkgs 
					+ "strtransnbrpkgs: " + strtransnbrpkgs + "strtransdnnbrpkgs: " + strtransdnnbrpkgs+ "strmethod: " + strmethod);
			
			sql = "SELECT CRG_STATUS FROM GB_EDO WHERE EDO_ASN_NBR=:stredoasnnbr ";
			log.info("checkPackageAmendDelete SQL" + sql);
			paramMap.put("stredoasnnbr", stredoasnnbr);
			log.info(" *** checkPackageAmendDelete params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				strcrgstat = CommonUtility.deNull(rs.getString(1));
			}

			int intdnnbrpkgs = 0;
			int inttransnbrpkgs = 0;
			int inttransdnnbrpkgs = 0;
			if (!(strdnnbrpkgs.equalsIgnoreCase(""))) {
				intdnnbrpkgs = Integer.parseInt(strdnnbrpkgs);
			}
			if (!(strtransnbrpkgs.equalsIgnoreCase(""))) {
				inttransnbrpkgs = Integer.parseInt(strtransnbrpkgs);
			}
			if (!(strtransdnnbrpkgs.equalsIgnoreCase(""))) {
				inttransdnnbrpkgs = Integer.parseInt(strtransdnnbrpkgs);
			}
			if (strcrgstat.equalsIgnoreCase("L")) {
				if (!(intdnnbrpkgs == 0)) {
					log.info("Writing from Writing from EdoEjb.checkPackageAmendDelete");
					log.info("Dn printed Edo cannot be " + strmethod);
					if (strmethod.equalsIgnoreCase("amended")) {
						throw new BusinessException("M20806");
					}
					if (strmethod.equalsIgnoreCase("deleted")) {
						throw new BusinessException("M20808");
					}
					if (strmethod.equalsIgnoreCase("vetting")) {
						throw new BusinessException("M20812");
					}
				}
			}
			if (strcrgstat.equalsIgnoreCase("T")) {
				if (!(inttransnbrpkgs == 0)) {
					log.info("Writing from Writing from EdoEjb.checkPackageAmendDelete");
					log.info("Transhipment done Edo cannot be " + strmethod);
					if (strmethod.equalsIgnoreCase("amended")) {
						throw new BusinessException("M20807");
					}
					if (strmethod.equalsIgnoreCase("deleted")) {
						throw new BusinessException("M20809");
					}
					if (strmethod.equalsIgnoreCase("vetting")) {
						throw new BusinessException("M20813");
					}
				}
				if (!(inttransdnnbrpkgs == 0)) {
					log.info("Writing from Writing from EdoEjb.checkPackageAmendDelete");
					log.info("Transhipment done Edo cannot be " + strmethod);
					if (strmethod.equalsIgnoreCase("amended")) {
						throw new BusinessException("M20807");
					}
					if (strmethod.equalsIgnoreCase("deleted")) {
						throw new BusinessException("M20809");
					}
					if (strmethod.equalsIgnoreCase("vetting")) {
						throw new BusinessException("M20813");
					}
				}
			}
			if (strcrgstat.equalsIgnoreCase("R")) {
				/*
				 * if (strmethod.equalsIgnoreCase("deleted")) { throw new
				 * BusinessException("M20809"); }
				 */
				if (strmethod.equalsIgnoreCase("vetting")) {
					throw new BusinessException("M20813");
				}
			}
		} catch (NullPointerException e) {
			log.info("Exception checkPackageAmendDelete : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception checkPackageAmendDelete : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception checkPackageAmendDelete : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkPackageAmendDelete  DAO  END");

		}
	}

	// ejb.sessionBeans.gbms.cargo.edo--->EdoEjb-->getBLNbrList()
	@Override
	public List<EdoValueObjectCargo> getBLNbrList(String strVarNbr, String strScreen, String companyCode) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		List<EdoValueObjectCargo> blNbrList = new ArrayList<EdoValueObjectCargo>();
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getBLNbrList  DAO  Start Obj strVarNbr:" + strVarNbr + "strScreen: " +  strScreen + "companyCode:"+ companyCode);

			// 16/06/2011 PCYAP To add/update only own EDO
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT UNIQUE(BL_NBR),");
			sb.append("        MFT_SEQ_NBR");
			sb.append("   FROM MANIFEST_DETAILS");
			sb.append("  WHERE 0 = 0");
			// if(companyCode != null && !companyCode.equals("JP")) {
			// sb.append(" AND MANIFEST_CREATE_CD = '" + companyCode + "'");
			// }
			sb.append("    AND BL_STATUS = 'A'");
			sb.append("    AND VAR_NBR = :strVarNbr");
			
			// Added by NearShore; Date:27092022; 
			// Add to select BLNbr by companyCode
			// Commented for 54738
			//if (!companyCode.equalsIgnoreCase("JP") && companyCode != null) {
			//	sb.append(" AND MANIFEST_CREATE_CD = :companyCode");
			//}
			// End by NearShore;Date:27092022;
			
			if (strScreen.equalsIgnoreCase("ADD")) {
				sb.append("    AND NBR_PKGS > EDO_NBR_PKGS");
			}
			sb.append(" ORDER BY MFT_SEQ_NBR");
			
			sql = sb.toString();
			log.info("END: *** getBLNbrList SQL *****" + sql);
			// if (strscreen.equalsIgnoreCase("AMEND")){
			// sql= "SELECT UNIQUE(BL_NBR), MFT_SEQ_NBR FROM MANIFEST_DETAILS WHERE "
			// +"BL_STATUS='A' AND VAR_NBR='"+strvarnbr+"' ORDER BY MFT_SEQ_NBR";
			// }
			// else {
			// sql= "SELECT UNIQUE(BL_NBR), MFT_SEQ_NBR FROM MANIFEST_DETAILS WHERE "
			// +"BL_STATUS='A' AND NBR_PKGS>EDO_NBR_PKGS AND VAR_NBR='"+strvarnbr
			// +"' ORDER BY MFT_SEQ_NBR";
			// }
			paramMap.put("strVarNbr", strVarNbr);
			
			// Added by NearShore; Date:27092022; 
			// Add to select BLNbr by companyCode
			// Commented for 54738
			//if (!companyCode.equalsIgnoreCase("JP") && companyCode != null) {
			//	paramMap.put("companyCode", companyCode);
			//}
			// End by NearShore; Date:27092022;
			
			log.info(" *** getBLNbrList params *****" + paramMap.toString());
			
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
				String bl_nbr = CommonUtility.deNull(rs.getString("BL_NBR"));
				String mft_seq_nbr = CommonUtility.deNull(rs.getString("MFT_SEQ_NBR"));
				edoValueObject.setBlNbr(bl_nbr);
				edoValueObject.setMftSeqNbr(mft_seq_nbr);
				blNbrList.add(edoValueObject);
			}
			log.info("END: *** getBLNbrList Result *****" + blNbrList);

		} catch (NullPointerException e) {
			log.info("Exception getBLNbrList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBLNbrList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getBLNbrList  END *****");
		}
		return blNbrList;

	}

	// ejb.sessionBeans.gbms.cargo.edo--->EdoEjb-->getBLDetails()
	@Override
	public List<EdoValueObjectCargo> getBLDetails(String mftSeqNbr) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		StringBuilder sb = new StringBuilder();
		List<EdoValueObjectCargo> bldetailsList = new ArrayList<EdoValueObjectCargo>();
		Map<String,String> paramMap = new HashMap<String,String>();
			log.info("START:  *** getBLDetails Dao Start : *** " + mftSeqNbr);
			try {
			// ++ 19.10.2009 FPT added for GB CR
			// 16.01.02
			/*
			 * sql="SELECT B.CRG_TYPE_CD, B.CRG_TYPE_NM, A.HS_CODE, A.CRG_DES, "
			 * +"A.CRG_STATUS, A.DG_IND, A.STG_TYPE, A.DIS_TYPE, C.PKG_TYPE_CD, "
			 * +"C.PKG_DESC, A.NBR_PKGS-(A.EDO_NBR_PKGS+NVl(A.CUT_OFF_NBR_PKGS,0)) AS NBR_PKG, M.MFT_MARKINGS,"
			 * +"A.CONS_NM FROM MANIFEST_DETAILS A, MFT_MARKINGS M,  "
			 * +"CRG_TYPE B, PKG_TYPES C WHERE A.CRG_TYPE=B.CRG_TYPE_CD AND "
			 * +"A.PKG_TYPE=C.PKG_TYPE_CD AND A.MFT_SEQ_NBR=M.MFT_SQ_NBR AND "
			 * +"A.MFT_SEQ_NBR='"+strMftSeqNbr+"'";
			 */

			sb.append("SELECT x.CRG_TYPE_CD, x.CRG_TYPE_NM, x.HS_CODE, x.CRG_DES, ");
			// START - CR To remove validation for volume - NS MAY 2024
			sb.append("x.HS_SUB_CODE_FR, x.HS_SUB_CODE_TO, x.CONS_CO_CD,");
			// END - CR To remove validation for volume - NS MAY 2024
			sb.append("x.CRG_STATUS, x.DG_IND, x.STG_TYPE, x.DIS_TYPE, x.PKG_TYPE_CD,");
			sb.append("x.PKG_DESC,x.NBR_PKG, x.MFT_MARKINGS, ");
			sb.append("x.CONS_NM,(x.gross_wt - nvl(y.sum1,0)) wt,(x.gross_vol - nvl(y.sum2,0)) vol, EPC_IND FROM ");
			sb.append("(SELECT  A.MFT_SEQ_NBR, B.CRG_TYPE_CD, B.CRG_TYPE_NM, A.HS_CODE, A.CRG_DES, ");
			// START - CR To remove validation for volume - NS MAY 2024
			sb.append("A.HS_SUB_CODE_FR, A.HS_SUB_CODE_TO, A.CONS_CO_CD,");
			// END - CR To remove validation for volume - NS MAY 2024
			sb.append("A.CRG_STATUS, A.DG_IND, A.STG_TYPE, A.DIS_TYPE, C.PKG_TYPE_CD,");
			sb.append("C.PKG_DESC,A.NBR_PKGS-(A.EDO_NBR_PKGS+NVl(A.CUT_OFF_NBR_PKGS,0)) AS NBR_PKG, M.MFT_MARKINGS, ");
			sb.append("A.CONS_NM, A.GROSS_WT,A.GROSS_VOL, A.EPC_IND ");
			sb.append("FROM MANIFEST_DETAILS A, MFT_MARKINGS M, ");
			sb.append("CRG_TYPE B, PKG_TYPES C ");
			sb.append("WHERE    A.CRG_TYPE=B.CRG_TYPE_CD AND ");
			sb.append("A.PKG_TYPE=C.PKG_TYPE_CD ");
			sb.append("AND A.MFT_SEQ_NBR=M.MFT_SQ_NBR ");
			sb.append(") x LEFT OUTER JOIN  ");
			sb.append(
					"(SELECT edo.mft_seq_nbr, SUM(edo.nom_wt) sum1, sum(edo.nom_vol) sum2 FROM GB_EDO edo WHERE EDO_STATUS = 'A' GROUP BY edo.mft_seq_nbr) y ");
			sb.append("ON x.mft_seq_nbr = y.mft_seq_nbr ");
			sb.append("WHERE x.MFT_SEQ_NBR = :mftSeqNbr");
			sql = sb.toString();

			log.info("END: *** getBLDetails SQL *****" + sql);
			paramMap.put("mftSeqNbr", mftSeqNbr);
			
			log.info(" *** getBLDetails params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
			while (rs.next()) {
				String crg_type_cd = CommonUtility.deNull(rs.getString("CRG_TYPE_CD"));
				String crg_type_nm = CommonUtility.deNull(rs.getString("CRG_TYPE_NM"));
				String hs_code = CommonUtility.deNull(rs.getString("HS_CODE"));
				String crg_des = CommonUtility.deNull(rs.getString("CRG_DES"));
				String mft_markings = CommonUtility.deNull(rs.getString("MFT_MARKINGS"));
				String cons_nm = CommonUtility.deNull(rs.getString("CONS_NM"));
				String crg_status = CommonUtility.deNull(rs.getString("CRG_STATUS"));
				String dg_ind = CommonUtility.deNull(rs.getString("DG_IND"));
				String stg_type = CommonUtility.deNull(rs.getString("STG_TYPE"));
				String dis_type = CommonUtility.deNull(rs.getString("DIS_TYPE"));
				String pkg_type_cd = CommonUtility.deNull(rs.getString("PKG_TYPE_CD"));
				String pkg_type_desc = CommonUtility.deNull(rs.getString("PKG_DESC"));
				String nbr_pkgs = CommonUtility.deNull(rs.getString("NBR_PKG"));
				String wt = CommonUtility.deNull(rs.getString("wt"));// Added by Thanhnv2
				String vol = CommonUtility.deNull(rs.getString("vol"));// Added by Thanhnv2
				log.info("===============EDO.BLDetail: " + wt + " - " + vol);
				String deliveryToEPC = CommonUtility.deNull(rs.getString("EPC_IND"));
				edoValueObject.setCrgTypeCd(crg_type_cd);
				edoValueObject.setCrgTypeNm(crg_type_nm);
				edoValueObject.setHsCode(hs_code);
				edoValueObject.setCrgDes(crg_des);
				edoValueObject.setMftMarkings(mft_markings);
				edoValueObject.setCrgStatus(crg_status);
				edoValueObject.setDgInd(dg_ind);
				edoValueObject.setStgType(stg_type);
				edoValueObject.setDisType(dis_type);
				edoValueObject.setPkgTypeCd(pkg_type_cd);
				edoValueObject.setPkgTypeDesc(pkg_type_desc);
				edoValueObject.setNbrPkgs(nbr_pkgs);
				edoValueObject.setCrgStatus(crg_status);
				edoValueObject.setConsNm(cons_nm);
				edoValueObject.setNomWeight(wt);// Added by Thanhnv2 for GB CR
				edoValueObject.setNomVolume(vol);// Added by Thanhnv2 for GB CR
				edoValueObject.setDeliveryToEPC(deliveryToEPC); // MCC get EPC_IND
				// START - CR To remove validation for volume - NS MAY 2024
				edoValueObject.setHsCodeFrom(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				edoValueObject.setHsCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				edoValueObject.setConsignee(CommonUtility.deNull(rs.getString("CONS_CO_CD")));
				// END - CR To remove validation for volume - NS MAY 2024
			}
			List<ContainerDetails> containerList = new ArrayList<ContainerDetails>();
			containerList = getContinerDetailsList(mftSeqNbr, "");
			edoValueObject.setContinerDetails(containerList);
			bldetailsList.add(edoValueObject);

			log.info("END: *** getBLDetails Result *****" + bldetailsList.toString());
		} catch (NullPointerException e) {
			log.info("Exception getBLDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBLDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getBLDetails  END *****");
		}
		return bldetailsList;
	}

	// ejb.sessionBeans.gbms.containerised.dn-->dnBean-->getContinerDetailsList
	private List<ContainerDetails> getContinerDetailsList(String mftSeqNbr, String edoAsnNbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		List<ContainerDetails> containerList = new ArrayList<ContainerDetails>();
		ContainerDetails containerDetails = new ContainerDetails();
		StringBuilder sb = new StringBuilder();
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:  *** getContinerDetailsList Dao Start : *** " + mftSeqNbr + edoAsnNbr);
			if (mftSeqNbr.equalsIgnoreCase("")) {
				sb.append("SELECT UNIQUE A.CNTR_TYPE,A.CNTR_SIZE,B.CNTR_NBR, B.CNTR_BL_SEQ ");
				sb.append(" FROM MANIFEST_DETAILS A, BL_CNTR_DETAILS B, GB_EDO E WHERE ");
				sb.append("A.MFT_SEQ_NBR=B.MFT_SEQ_NBR AND A.MFT_SEQ_NBR=E.MFT_SEQ_NBR ");
				sb.append("AND A.CRG_TYPE IN ('02','03') AND EDO_ASN_NBR=:edoAsnNbr ");
				sb.append("ORDER BY B.CNTR_BL_SEQ");

			} else {
				sb.append("SELECT A.CNTR_TYPE,A.CNTR_SIZE,B.CNTR_NBR, B.CNTR_BL_SEQ FROM ");
				sb.append("MANIFEST_DETAILS A, BL_CNTR_DETAILS B WHERE ");
				sb.append("A.MFT_SEQ_NBR=B.MFT_SEQ_NBR AND A.CRG_TYPE IN ('02','03') AND A.MFT_SEQ_NBR=:mftSeqNbr ");
				sb.append("  ORDER BY B.CNTR_BL_SEQ");
			}
			sql = sb.toString();
			log.info("END: *** getContinerDetailsList SQL *****" + sql);

			if (mftSeqNbr.equalsIgnoreCase(""))
				paramMap.put("edoAsnNbr", edoAsnNbr);
			paramMap.put("mftSeqNbr", mftSeqNbr);
			
			log.info(" *** getContinerDetailsList params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				containerDetails.setCntrType(CommonUtil.deNull(rs.getString("CNTR_TYPE")));
				containerDetails.setCntrSize(CommonUtil.deNull(rs.getString("CNTR_SIZE")));
				containerDetails.setCntrNbr(CommonUtil.deNull(rs.getString("CNTR_NBR")));
				containerList.add(containerDetails);
			}
			log.info("END: *** getContinerDetailsList Result *****" + containerList.toString());

		} catch (NullPointerException e) {
			log.info("Exception getContinerDetailsList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getContinerDetailsList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getContinerDetailsList  END *****");
		}

		return containerList;
	}

	// ejb.sessionBeans.gbms.cargo.edo--->EdoEjb-->getCustomerNbr()
	@Override
	public String getCustomerNbr(String strtDbNbr) throws BusinessException {
		String sql = "";
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		String strnewnbr = "";
		try {
			log.info("START:  *** getCustomerNbr Dao Start : *** " + strtDbNbr);
			sb.append(
					"SELECT CUST_CD FROM CUSTOMER , COMPANY_CODE WHERE CUSTOMER.CUST_CD = COMPANY_CODE.CO_CD and COMPANY_CODE.REC_STATUS = 'A'");
			sb.append(" and (UPPER(TDB_CR_NBR)=UPPER(:strtDbNbr) OR UPPER(UEN_NBR)=UPPER(:strtDbNbr))");
			sql = sb.toString();

			paramMap.put("strtDbNbr", strtDbNbr.toUpperCase());
			
			log.info(" *** getCustomerNbr SQL *****" + sql);
			log.info(" *** getCustomerNbr params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				strnewnbr = CommonUtility.deNull(rs.getString("CUST_CD"));
			}
			log.info("END: *** getCustomerNbr Result *****" + CommonUtility.deNull(strnewnbr));

		} catch (NullPointerException ne) {
			log.info("Exception getCustomerNbr : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCustomerNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getCustomerNbr  END *****");
		}
		return strnewnbr;
	}

	// ejb:jp\src\ejb\sessionBeans\gbms\cargo\edo\EdoEjb.java
	@Override
	public String updateEdoDetailsForDPE(String mftseqnbr, String varnbr, String adpnbr, String adpnm,
			String adpictdbcrnbr, String crgagtnbr, String crgagtnm, String agtattnbr, String agtattnm,
			String newnbrpkgs, String deliveryto, String jpbnbr, String paymode, String edostatus,
			String lastmodifyuserid, String stredoasnnbr, String caictdbcrnbr, String aaictdbcrnbr, String coCd,
			String strmodulecd, String distype, String wt, String vol, List<AdpValueObject> adpList, String taUenNo,
			String taCCode, String taNmByJP) throws BusinessException {
		// added 'strStfInd' by vani -- 10th Oct,03) ,String strStfInd removed by
		// vinayak 19/12/2003
		log.info("1473 ln inside updateEdoDetailsForDPE() :" + coCd);
		String strinsertedoasnnbr = "false";
		String blnbr = "";
		boolean chkAccountNbr = true;
		if (!(strmodulecd.equalsIgnoreCase("ADPRENOM"))) {
			if (!(jpbnbr.equalsIgnoreCase("cash"))) {
				chkAccountNbr = checkAccountNbr(jpbnbr);
			}
			if (!chkAccountNbr) {
				log.info("Writing from Writing from EdoEjb.updateEdoDetailsForDPE");
				log.info("Invalid Account Number " + jpbnbr);
				throw new BusinessException("M20801");
			}
		}
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3 = null;
		SqlRowSet rsclbj = null;
		// HaiTTH1 added on 20/1/2014
		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();
		StringBuilder sql3 = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		StringBuilder strUpdate = new StringBuilder();
		StringBuilder strUpdatetrans = new StringBuilder();
		StringBuilder strUpdatetrans1 = new StringBuilder();
		StringBuilder strUpdate1 = new StringBuilder();
		StringBuilder sqlclbj = new StringBuilder();
		StringBuilder sqllog = new StringBuilder();
		StringBuilder strDeleteAdpTxn = new StringBuilder();
		StringBuilder strSubAdp = new StringBuilder();
		// HaiTTH1 added on 20/1/2014
		StringBuilder strDeleteADP = new StringBuilder();
		Map<String,Object> paramMap = new HashMap<String,Object>();

		String stredonbrpkgs = "0";
		String strdnnbrpkgs = "0";
		String strtransnbrpkgs = "0";
		String strtransdnnbrpkgs = "0";
		String strtransnbr = "0";
		String strtransnbrManifest = "0";
		String edocreatecd = "";
		String releasenbrpkgs = "0";
		String strnewedostatus = "";
		String appointed_adp_cust_cd = "";
		String appointed_adp_ic_tdbcr_nbr = "";
		String appointed_adp_nm = "";

		try {
			log.info("START: updateEdoDetailsForDPE mftseqnbr:" + mftseqnbr + "varnbr:" + varnbr + "adpnbr:" + adpnbr
					+ "adpnm:" + adpnm + "adpictdbcrnbr:" + adpictdbcrnbr + "crgagtnbr:" + crgagtnbr + "crgagtnm:"
					+ crgagtnm + "agtattnbr:" + agtattnbr + "agtattnm:" + agtattnm + "newnbrpkgs:" + newnbrpkgs
					+ "deliveryto:" + deliveryto + "jpbnbr:" + jpbnbr + "paymode:" + paymode + "edostatus:"
					+ "lastmodifyuserid:" + lastmodifyuserid + "stredoasnnbr:" + stredoasnnbr + "caictdbcrnbr:"
					+ caictdbcrnbr + "aaictdbcrnbr:" + aaictdbcrnbr + "coCd:" + coCd + "strmodulecd:" + strmodulecd
					+ "distype:" + distype + "wt:" + "vol:" + vol + "adpList:" + adpList.toString() + "taUenNo:"
					+ taUenNo + "taCCode:" + taCCode + "taNmByJP:" + taNmByJP);
			
			sql3.append("SELECT MAX(TRANS_NBR) FROM GB_EDO_TRANS WHERE EDO_ASN_NBR=:stredoasnnbr");

			sql1.append("SELECT BL_NBR FROM MANIFEST_DETAILS WHERE  MFT_SEQ_NBR=:mftseqnbr");

			sql2.append("SELECT NBR_PKGS, DN_NBR_PKGS, TRANS_NBR_PKGS, TRANS_DN_NBR_PKGS,EDO_CREATE_CD, EDO_STATUS,");
			sql2.append(
					"APPOINTED_ADP_CUST_CD,APPOINTED_ADP_IC_TDBCR_NBR, APPOINTED_ADP_NM FROM GB_EDO WHERE  EDO_ASN_NBR=:stredoasnnbr ");

			paramMap.put("mftseqnbr", mftseqnbr);
			
			log.info(" updateEdoDetailsForDPE SQL :" + sql1.toString() + "paramMap:" + paramMap);
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
			while (rs1.next()) {
				blnbr = CommonUtility.deNull(rs1.getString(1));
			}
			
			paramMap.put("stredoasnnbr", stredoasnnbr);
			
			log.info("updateEdoDetailsForDPE SQL :" + sql2.toString() + "paramMap:" + paramMap);
			rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2.toString(), paramMap);
			while (rs2.next()) {
				stredonbrpkgs = CommonUtility.deNull(rs2.getString(1));
				strdnnbrpkgs = CommonUtility.deNull(rs2.getString("DN_NBR_PKGS"));
				strtransnbrpkgs = CommonUtility.deNull(rs2.getString("TRANS_NBR_PKGS"));
				strtransdnnbrpkgs = CommonUtility.deNull(rs2.getString("TRANS_DN_NBR_PKGS"));
				edocreatecd = CommonUtility.deNull(rs2.getString("EDO_CREATE_CD"));
				strnewedostatus = CommonUtility.deNull(rs2.getString("EDO_STATUS"));
				appointed_adp_cust_cd = CommonUtility.deNull(rs2.getString("APPOINTED_ADP_CUST_CD"));
				appointed_adp_ic_tdbcr_nbr = CommonUtility.deNull(rs2.getString("APPOINTED_ADP_IC_TDBCR_NBR"));
				appointed_adp_nm = CommonUtility.deNull(rs2.getString("APPOINTED_ADP_NM"));
			}
			if (strnewedostatus.equalsIgnoreCase("X")) {
				throw new BusinessException("M20814");
			}
			String closeBjInd = "N";
			sqlclbj.append("SELECT GB_CLOSE_BJ_IND FROM VESSEL_CALL WHERE VV_CD=:varnbr");
			
			paramMap.put("varnbr", varnbr);
			log.info("updateEdoDetailsForDPE SQL :" + sqlclbj.toString() + "paramMap:" + paramMap);
			rsclbj = namedParameterJdbcTemplate.queryForRowSet(sqlclbj.toString(), paramMap);
			while (rsclbj.next()) {
				closeBjInd = CommonUtility.deNull(rsclbj.getString(1));
			}
			log.info("closeBjInd" + closeBjInd.toString());
			/*
			 * if (!(closeBjInd.equalsIgnoreCase("N"))) { throw new
			 * BusinessException("M21605"); }
			 */

			// Calculating Nominated weight and Nominated volume
			EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
			edoValueObject = getNominatedWeightVolume(mftseqnbr, newnbrpkgs);
			String nomvolume = edoValueObject.getNomVolume();
			String nomweight = edoValueObject.getNomWeight();
			// End of Nominated weight and Nominated volume Calculation

			// Checking for Apostrophe
			adpnm = GbmsCommonUtility.addApostr(adpnm);
			crgagtnm = GbmsCommonUtility.addApostr(crgagtnm);
			agtattnm = GbmsCommonUtility.addApostr(agtattnm);

			// checking before amend added by vinayak on 29/12/2003
			if (!coCd.equalsIgnoreCase("JP")) {
				checkPackageAmendDelete(stredoasnnbr, strdnnbrpkgs, strtransnbrpkgs, strtransdnnbrpkgs, "amended");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				
				paramMap.put("stredoasnnbr", stredoasnnbr);
				log.info("updateEdoDetailsForDPE SQL :" + sql3.toString() + "paramMap:" + paramMap);
				rs3 = namedParameterJdbcTemplate.queryForRowSet(sql3.toString(), paramMap);
				while (rs3.next()) {
					strtransnbr = CommonUtility.deNull(rs3.getString(1));
				}
				if (strtransnbr.equalsIgnoreCase("") || strtransnbr == null) {
					strtransnbr = "0";
				} else {
					strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
				}
			}

			// TO INSERT RELEASE_NBR_PKGS
			if (!(agtattnm.equalsIgnoreCase(""))) {
				releasenbrpkgs = newnbrpkgs;
			}

			// Added by VietNguyen
			String adpNbrPkgs = null;
			String adpContactNbr = "";
			if (adpList != null && adpList.size() > 0) {
				AdpValueObject adp = adpList.get(0);
				if (adp != null) {
					adpnbr = adp.getAdpCustCd();
					adpictdbcrnbr = adp.getAdpIcTdbcrNbr();
					adpnm = adp.getAdpNm();
					adpNbrPkgs = adp.getAdpNbrPkgs();
					adpContactNbr = adp.getAdpContact();
				}
			}

			if (strmodulecd.equalsIgnoreCase("ADPRENOM")) {
				appointed_adp_cust_cd = adpnbr;
				appointed_adp_ic_tdbcr_nbr = adpictdbcrnbr;
				appointed_adp_nm = adpnm;
				strUpdate.append(" UPDATE GB_EDO SET LAST_MODIFY_USER_ID =:lastmodifyuserid, ");
				strUpdate.append(" LAST_MODIFY_DTTM = sysdate,ADP_CUST_CD =:appointed_adp_cust_cd, ");
				strUpdate.append(" ADP_IC_TDBCR_NBR =:appointed_adp_ic_tdbcr_nbr, ADP_NM=:appointed_adp_nm, ");
				strUpdate.append(" ADP_NBR_PKGS =:adpNbrPkgs,ADP_CONTACT_NBR =:adpContactNbr ");
				strUpdate.append(" WHERE  EDO_ASN_NBR =:stredoasnnbr ");
				/*
				 * +" ADP_CUST_CD = '"+adpnbr+"'," +" ADP_NM = '"+adpnm+"',"
				 * +" ADP_IC_TDBCR_NBR = '"+adpictdbcrnbr+"',"
				 */
				paramMap.put("lastmodifyuserid", lastmodifyuserid);
				paramMap.put("appointed_adp_cust_cd", appointed_adp_cust_cd);
				paramMap.put("appointed_adp_ic_tdbcr_nbr", appointed_adp_ic_tdbcr_nbr);
				paramMap.put("appointed_adp_nm", appointed_adp_nm);
				paramMap.put("adpNbrPkgs", adpNbrPkgs);
				paramMap.put("adpContactNbr", adpContactNbr);
				paramMap.put("stredoasnnbr", stredoasnnbr);
				
				log.info("updateEdoDetailsForDPE SQL :" + strUpdate.toString() + "paramMap:" + paramMap);
				int count = namedParameterJdbcTemplate.update(strUpdate.toString(), paramMap);

				if (count == 0) {
					throw new BusinessException("M20802");
				} 

			} else {
				if (coCd.equalsIgnoreCase("JP")) {
					appointed_adp_cust_cd = adpnbr;
					appointed_adp_ic_tdbcr_nbr = adpictdbcrnbr;
					appointed_adp_nm = adpnm;
				} else {
					if (!(appointed_adp_ic_tdbcr_nbr.equalsIgnoreCase(adpictdbcrnbr))) {
						appointed_adp_cust_cd = adpnbr;
						appointed_adp_ic_tdbcr_nbr = adpictdbcrnbr;
						appointed_adp_nm = adpnm;
					}
				}
				strUpdate.append("UPDATE GB_EDO SET MFT_SEQ_NBR=:mftseqnbr, VAR_NBR =:varnbr,  BL_NBR =:blnbr, ");
				// added 'STUFF_IND = '"+strStfInd+"'' by vani -- 10th Oct,03 STUFF_IND =
				// '"+strStfInd+"', removed by vinayak 19/12/2003
				strUpdate.append(" ADP_CUST_CD =:adpnbr, ADP_NM =:adpnm, ADP_IC_TDBCR_NBR = :adpictdbcrnbr, ");
				strUpdate.append(
						" ADP_NBR_PKGS =:adpNbrPkgs, CA_CUST_CD =:crgagtnbr, CA_NM =:crgagtnm, AA_CUST_CD =:agtattnbr, AA_NM =:agtattnm, NBR_PKGS =:newnbrpkgs, ");
				strUpdate.append(
						" EDO_DELIVERY_TO =:deliveryto, ACCT_NBR =:jpbnbr, PAYMENT_MODE = :paymode, EDO_STATUS =:edostatus, ");
				// +" EPC_IND = '"+deliveryToEPC+"'," //MCC added EPC_IND, moved to Manifest
				strUpdate.append(" LAST_MODIFY_USER_ID =:lastmodifyuserid, LAST_MODIFY_DTTM = sysdate, ");
				strUpdate.append(" CA_IC_TDBCR_NBR =:caictdbcrnbr, AA_IC_TDBCR_NBR =:aaictdbcrnbr, NOM_VOL =:vol, ");// Changed
				// by
				// Thanhnv2
				// for
				// GB
				// CR
				strUpdate.append(
						" NOM_WT =:wt, RELEASE_NBR_PKGS =:releasenbrpkgs, APPOINTED_ADP_CUST_CD =:appointed_adp_cust_cd, APPOINTED_ADP_IC_TDBCR_NBR =:appointed_adp_ic_tdbcr_nbr, ");
				strUpdate.append(
						" APPOINTED_ADP_NM =:appointed_adp_nm, DIS_TYPE=:distype, ADP_CONTACT_NBR =:adpContactNbr, ");// added
				// by
				// HaiTTH1
				// for
				// DPE
				// CR
				// Begin ThanhPT6 JCMS FPT, 2016-01-15
				strUpdate.append(" TA_CR_UEN_NBR =:taUenNo, TA_CUST_CD =:taCCode, TA_NAME =:taNmByJP ");
				// End ThanhPT6 JCMS
				strUpdate.append(" WHERE  EDO_ASN_NBR =:stredoasnnbr ");
				
				paramMap.put("mftseqnbr", mftseqnbr);
				paramMap.put("varnbr", varnbr);
				paramMap.put("blnbr", blnbr);
				paramMap.put("adpnbr", adpnbr);
				paramMap.put("adpnm", adpnm);
				paramMap.put("adpictdbcrnbr", adpictdbcrnbr);
				paramMap.put("adpNbrPkgs", adpNbrPkgs);
				paramMap.put("crgagtnbr", crgagtnbr);
				paramMap.put("crgagtnm", crgagtnm);
				paramMap.put("agtattnbr", agtattnbr);
				paramMap.put("agtattnm", agtattnm);
				paramMap.put("newnbrpkgs", newnbrpkgs);
				paramMap.put("deliveryto", deliveryto);
				paramMap.put("jpbnbr", jpbnbr);
				paramMap.put("paymode", paymode);
				paramMap.put("edostatus", edostatus);
				paramMap.put("lastmodifyuserid", lastmodifyuserid);
				paramMap.put("caictdbcrnbr", caictdbcrnbr);
				paramMap.put("aaictdbcrnbr", aaictdbcrnbr);
				paramMap.put("vol", vol);
				paramMap.put("wt", wt);
				paramMap.put("releasenbrpkgs", releasenbrpkgs);
				paramMap.put("appointed_adp_cust_cd", appointed_adp_cust_cd);
				paramMap.put("appointed_adp_ic_tdbcr_nbr", appointed_adp_ic_tdbcr_nbr);
				paramMap.put("appointed_adp_nm", appointed_adp_nm);
				paramMap.put("distype", distype);
				paramMap.put("adpContactNbr", adpContactNbr);
				paramMap.put("taUenNo", taUenNo);
				paramMap.put("taCCode", taCCode);
				paramMap.put("taNmByJP", taNmByJP);
				paramMap.put("stredoasnnbr", stredoasnnbr);
				log.info("updateEdoDetailsForDPE SQL :" + strUpdate.toString() + "paramMap:" + paramMap);
				int count = namedParameterJdbcTemplate.update(strUpdate.toString(), paramMap);

				if (count == 0) {
					throw new BusinessException("M20802");
				}else {
					  // START FTZ HSCode  -NS July 2024
                    
                    sb.setLength(0);
					sb.append(" UPDATE GBMS.GB_EDO_HSCODE_DETAILS SET ");
					sb.append(" NBR_PKGS=:NBR_PKGS, GROSS_WT=:GROSS_WT, GROSS_VOL=:GROSS_VOL, ");
					sb.append(" LAST_MODIFY_USER_ID=:userId, LAST_MODIFY_DTTM=SYSDATE WHERE");
					sb.append(" EDO_ASN_NBR=:EDO_ASN_NBR ");
					

					paramMap.put("NBR_PKGS", newnbrpkgs);
					paramMap.put("GROSS_WT", vol);
					paramMap.put("GROSS_VOL", wt);
					paramMap.put("userId", lastmodifyuserid);
					paramMap.put("EDO_ASN_NBR", stredoasnnbr);
					paramMap.put("REC_STATUS", "A");
					
					log.info("SQL" + sb.toString());
					log.info("ParamMap" + paramMap.toString());
					int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
					log.info("counths : " + counths);
					
					sb.setLength(0);
					sb.append(" SELECT EDO_HSCODE_SEQ_NBR, MFT_SEQ_NBR, MFT_HSCODE_SEQ_NBR, HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, LAST_MODIFY_DTTM FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE EDO_ASN_NBR = :stredoasnnbr ");
					log.info("SQL" + sb.toString());
					log.info("ParamMap" + paramMap.toString());
					SqlRowSet rs4 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					while(rs4.next()) {
						
						sb.setLength(0);
						sb.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS_TRANS  ");
						sb.append(" (EDO_HSCODE_SEQ_NBR, MFT_SEQ_NBR, MFT_HSCODE_SEQ_NBR, EDO_ASN_NBR, AUDIT_DTTM, REC_STATUS, NBR_PKGS, GROSS_WT, GROSS_VOL, HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID) ");
						sb.append(" VALUES(:EDO_HSCODE_SEQ_NBR,:MFT_SEQ_NBR, :MFT_HSCODE_SEQ_NBR, :stredoasnnbr, SYSDATE, 'A', :NBR_PKGS, :GROSS_WT, :GROSS_VOL, :HS_CODE, :HS_SUB_CODE_FR, :HS_SUB_CODE_TO, SYSDATE, :lastmodifyuserid) ");
			
						paramMap.put("EDO_HSCODE_SEQ_NBR", rs4.getString("EDO_HSCODE_SEQ_NBR"));
						paramMap.put("MFT_SEQ_NBR", rs4.getString("MFT_SEQ_NBR"));
						paramMap.put("MFT_HSCODE_SEQ_NBR", rs4.getString("MFT_HSCODE_SEQ_NBR"));
						paramMap.put("NBR_PKGS", newnbrpkgs);
						paramMap.put("GROSS_WT", vol);
						paramMap.put("GROSS_VOL", wt);
						paramMap.put("HS_CODE", rs4.getString("HS_CODE"));
						paramMap.put("HS_SUB_CODE_FR", rs4.getString("HS_SUB_CODE_FR"));
						paramMap.put("HS_SUB_CODE_TO", rs4.getString("HS_SUB_CODE_TO"));
						paramMap.put("lastmodifyuserid", lastmodifyuserid);
						
						
						log.info("SQL" + sb.toString());
						log.info("ParamMap" + paramMap.toString());
						int counthst = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						log.info("counthst : " + counthst);
					}
//				
//                    
                    // END FTZ HSCode  -NS July 2024
                	
				}
			}

			// Added by VietNguyen 20/02/2014
			strDeleteADP.append(
					" UPDATE SUB_ADP SET STATUS_CD = 'X',LAST_MODIFY_DTTM=SYSDATE, LAST_MODIFY_USER_ID=:lastmodifyuserid ");
			strDeleteADP.append(" WHERE STATUS_CD= 'A' AND ESN_ASN_NBR =:stredoasnnbr AND EDO_ESN_IND = 1 ");
			paramMap.put("lastmodifyuserid", lastmodifyuserid);
			paramMap.put("stredoasnnbr", stredoasnnbr);
			log.info("updateEdoDetailsForDPE SQL :" + strDeleteADP.toString() + "paramMap:" + paramMap);
			namedParameterJdbcTemplate.update(strDeleteADP.toString(), paramMap);

			strDeleteAdpTxn.append(
					" UPDATE (SELECT txn.status_cd status, txn_dttm, txn_user_id  from sub_adp_txn txn inner join sub_adp sub on txn.sub_adp_nbr = sub.sub_adp_nbr ");
			strDeleteAdpTxn.append(
					" where txn.status_cd = 'A' and sub.edo_esn_ind = '1' and txn.edo_esn_ind = '1' and esn_asn_nbr =:stredoasnnbr ");
			strDeleteAdpTxn.append(" ) t set t.status='X', txn_dttm=SYSDATE, txn_user_id=:lastmodifyuserid ");
			paramMap.put("stredoasnnbr", stredoasnnbr);
			paramMap.put("lastmodifyuserid", lastmodifyuserid);
			log.info("updateEdoDetailsForDPE SQL :" + strDeleteAdpTxn.toString() + "paramMap:" + paramMap);
			namedParameterJdbcTemplate.update(strDeleteAdpTxn.toString(), paramMap);

			Integer adpSeq = 1;
			int cntAdp = 1;
			if (adpList != null && adpList.size() > 1) {
				sqllog.append("SELECT MAX(SUB_ADP_NBR) FROM sub_adp");
				log.info("updateEdoDetailsForDPE SQL :" + sqllog.toString() + "paramMap:" + paramMap);
				rs3 = namedParameterJdbcTemplate.queryForRowSet(sqllog.toString(), paramMap);
				while (rs3.next()) {
					adpSeq = rs3.getInt(1);
				}
				if (adpSeq != null) {
					adpSeq = adpSeq + 1;
				}
				for (int i = 1; i < adpList.size(); i++) {
					AdpValueObject adp = adpList.get(i);
					if (adp != null) {
						strSubAdp.setLength(0);
						strSubAdp.append(" INSERT INTO sub_adp(SUB_ADP_NBR, STATUS_CD, ESN_ASN_NBR, ");
						strSubAdp.append(" TRUCKER_IC,TRUCKER_NM,TRUCKER_CO_CD,TRUCKER_NBR_PKGS,EDO_ESN_IND, ");
						strSubAdp.append(
								" CREATE_USER_ID,CREATE_DTTM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM, TRUCKER_CONTACT_NBR) ");
						strSubAdp.append(
								" VALUES(:adpSeq, 'A', :stredoasnnbr, :adpIcTdbcrNbr, :adpNm, :adpCustCd, :adpNbrPkgs,");
						strSubAdp
								.append(" '1', :lastmodifyuserid, SYSDATE, :lastmodifyuserid, SYSDATE, :adpContact ) ");
						log.info("updateEdoDetailsForDPE SQL :" + strSubAdp.toString() + "pstmt:");
						paramMap.put("adpSeq", adpSeq);
						paramMap.put("stredoasnnbr", stredoasnnbr);
						paramMap.put("adpIcTdbcrNbr", adp.getAdpIcTdbcrNbr());
						paramMap.put("adpNm", adp.getAdpNm());
						paramMap.put("adpCustCd", adp.getAdpCustCd());
						paramMap.put("adpNbrPkgs", adp.getAdpNbrPkgs());
						paramMap.put("lastmodifyuserid", lastmodifyuserid);
						paramMap.put("adpContact", adp.getAdpContact());
						log.info("updateEdoDetailsForDPE SQL :" + strSubAdp.toString() + "paramMap:" + paramMap);
						cntAdp = namedParameterJdbcTemplate.update(strSubAdp.toString(), paramMap);
						if (cntAdp == 0) {
							break;
						} else {
							insertSubAdpTxn(adpSeq, lastmodifyuserid, adp);
						}
						adpSeq++;
					}
				}
			}

			if (!(strmodulecd.equalsIgnoreCase("ADPRENOM"))) {
				strUpdate1.append(" UPDATE MANIFEST_DETAILS SET EDO_NBR_PKGS=EDO_NBR_PKGS-:stredonbrpkgs + :newnbrpkgs  ");
				strUpdate1.append("  , LAST_MODIFY_USER_ID =:lastmodifyuserid ");
				strUpdate1.append(" , LAST_MODIFY_DTTM=SYSDATE WHERE MFT_SEQ_NBR=:mftseqnbr ");
				log.info("updateEdoDetailsForDPE SQL :" + strUpdate1.toString() + "pstmt:");
				paramMap.put("stredonbrpkgs", Integer.parseInt(stredonbrpkgs));
				paramMap.put("newnbrpkgs", Integer.parseInt(newnbrpkgs));
				paramMap.put("newnbrpkgs", newnbrpkgs);
				paramMap.put("lastmodifyuserid", lastmodifyuserid);
				paramMap.put("mftseqnbr", mftseqnbr);
				log.info("updateEdoDetailsForDPE SQL :" + strUpdate1.toString() + "paramMap:" + paramMap);
				int count1 = namedParameterJdbcTemplate.update(strUpdate1.toString(), paramMap);
				if (count1 == 0) {
					throw new BusinessException("M20802");
				}
			}

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				if (strmodulecd.equalsIgnoreCase("ADPRENOM")) {
					strUpdatetrans.append(
							"INSERT INTO GB_EDO_TRANS (TRANS_NBR, EDO_ASN_NBR, LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM, ");
					strUpdatetrans.append(" APPOINTED_ADP_CUST_CD, APPOINTED_ADP_IC_TDBCR_NBR,  APPOINTED_ADP_NM, ");
					// +" ADP_CONTACT_NBR," Added by HaiTTH1 20/02/2014
					strUpdatetrans.append(" ADP_NBR_PKGS) "); // Added by VietNguyen 20/01/2014
					strUpdatetrans.append(" VALUES (:strtransnbr, :stredoasnnbr, :lastmodifyuserid, ");
					strUpdatetrans.append(" sysdate, :appointed_adp_cust_cd, :appointed_adp_ic_tdbcr_nbr, ");
					strUpdatetrans.append(" :appointed_adp_nm, :adpNbrPkgs) ");
					/*
					 * sqlstmtupdate.setString("strtransnbr", strtransnbr);
					 * sqlstmtupdate.setString("stredoasnnbr", stredoasnnbr);
					 * sqlstmtupdate.setString("lastmodifyuserid", lastmodifyuserid);
					 * sqlstmtupdate.setString("appointed_adp_cust_cd", appointed_adp_cust_cd);
					 * sqlstmtupdate.setString("appointed_adp_ic_tdbcr_nbr",
					 * appointed_adp_ic_tdbcr_nbr); sqlstmtupdate.setString("appointed_adp_nm",
					 * appointed_adp_nm); sqlstmtupdate.setString("adpNbrPkgs", adpNbrPkgs);
					 */
				} else {
					strUpdatetrans.append(" INSERT INTO GB_EDO_TRANS (TRANS_NBR, EDO_ASN_NBR, MFT_SEQ_NBR, VAR_NBR , ");
					strUpdatetrans
							.append(" BL_NBR, ADP_CUST_CD, ADP_NM, ADP_IC_TDBCR_NBR, ADP_NBR_PKGS, ADP_CONTACT_NBR, ");
					strUpdatetrans.append(
							" CA_CUST_CD, CA_NM, AA_CUST_CD, AA_NM, NBR_PKGS, EDO_DELIVERY_TO, ACCT_NBR, PAYMENT_MODE, ");
					strUpdatetrans.append(
							" EDO_STATUS, LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,CA_IC_TDBCR_NBR, AA_IC_TDBCR_NBR, ");
					strUpdatetrans.append(
							" TRANS_NBR_PKGS, TRANS_DN_NBR_PKGS, DN_NBR_PKGS, EDO_CREATE_CD, NOM_VOL,NOM_WT, RELEASE_NBR_PKGS, ");
					strUpdatetrans
							.append(" APPOINTED_ADP_CUST_CD, APPOINTED_ADP_IC_TDBCR_NBR, APPOINTED_ADP_NM, DIS_TYPE) ");
					// MCC added EPC_IND. moved to manifest
					strUpdatetrans.append(
							" VALUES (:strtransnbr,:stredoasnnbr,:mftseqnbr,:varnbr,:blnbr,:adpnbr,:adpnm,:adpictdbcrnbr,:adpNbrPkgs ");
					strUpdatetrans.append(
							" ,:adpContactNbr,:crgagtnbr,:crgagtnm,:agtattnbr,:agtattnm,:newnbrpkgs,:deliveryto,:jpbnbr,:paymode ");
					strUpdatetrans.append(
							" ,:edostatus,:lastmodifyuserid,sysdate,:caictdbcrnbr,:aaictdbcrnbr,:strtransnbrpkgs, ");
					// MCC added EPC_IND, moved to manifest
					strUpdatetrans.append(
							" :strtransdnnbrpkgs,:strdnnbrpkgs,:edocreatecd,:nomvolume,:nomweight,:releasenbrpkgs, ");
					strUpdatetrans.append(
							" :appointed_adp_cust_cd,:appointed_adp_ic_tdbcr_nbr,:appointed_adp_nm, :distype) ");

					if (logStatusGlobal.equalsIgnoreCase("Y")) {
						String sqltransnbr = "SELECT MAX(TRANS_NBR) FROM MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:mftseqnbr";
						paramMap.put("mftseqnbr", mftseqnbr);
						log.info("updateEdoDetailsForDPE SQL :" + sqltransnbr.toString() + "paramMap:" + paramMap);
						SqlRowSet rstransnbr = namedParameterJdbcTemplate.queryForRowSet(sqltransnbr.toString(), paramMap);
						while (rstransnbr.next()) {
							strtransnbrManifest = CommonUtility.deNull(rstransnbr.getString(1));
						}
						if (strtransnbrManifest.equalsIgnoreCase("") || strtransnbrManifest == null) {
							strtransnbrManifest = "0";
						} else {
							strtransnbrManifest = String.valueOf(Integer.parseInt(strtransnbrManifest) + 1);
						}
					}
					strUpdatetrans1
							.append(" INSERT INTO MANIFEST_DETAILS_TRANS (TRANS_NBR , MFT_SEQ_NBR, EDO_NBR_PKGS, ");
					strUpdatetrans1.append(" LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM,REMARKS) VALUES (:strtransnbr,");
					strUpdatetrans1.append(" :mftseqnbr,:newnbrpkgs,:lastmodifyuserid ,SYSDATE, :remarks ");
					strUpdatetrans1.append(" ) ");
				}

				log.info("updateEdoDetailsForDPE SQL :" + strUpdatetrans.toString() + "pstmt:");
				if (strmodulecd.equalsIgnoreCase("ADPRENOM")) {
					paramMap.put("strtransnbr", strtransnbr);
					paramMap.put("stredoasnnbr", stredoasnnbr);
					paramMap.put("lastmodifyuserid", lastmodifyuserid);
					paramMap.put("appointed_adp_cust_cd", appointed_adp_cust_cd);
					paramMap.put("appointed_adp_ic_tdbcr_nbr", appointed_adp_ic_tdbcr_nbr);
					paramMap.put("appointed_adp_nm", appointed_adp_nm);
					paramMap.put("adpNbrPkgs", adpNbrPkgs);

				} else {
					paramMap.put("strtransnbr", strtransnbr);
					paramMap.put("stredoasnnbr", stredoasnnbr);
					paramMap.put("mftseqnbr", mftseqnbr);
					paramMap.put("varnbr", varnbr);
					paramMap.put("blnbr", blnbr);
					paramMap.put("adpnbr", adpnbr);
					paramMap.put("adpnm", adpnm);
					paramMap.put("adpictdbcrnbr", adpictdbcrnbr);
					paramMap.put("adpNbrPkgs", adpNbrPkgs);
					paramMap.put("adpContactNbr", adpContactNbr);
					paramMap.put("crgagtnbr", crgagtnbr);
					paramMap.put("crgagtnm", crgagtnm);
					paramMap.put("agtattnbr", agtattnbr);
					paramMap.put("agtattnm", agtattnm);
					paramMap.put("newnbrpkgs", newnbrpkgs);
					paramMap.put("deliveryto", deliveryto);
					paramMap.put("jpbnbr", jpbnbr);
					paramMap.put("paymode", paymode);
					paramMap.put("edostatus", edostatus);
					paramMap.put("lastmodifyuserid", lastmodifyuserid);
					paramMap.put("caictdbcrnbr", caictdbcrnbr);
					paramMap.put("aaictdbcrnbr", aaictdbcrnbr);
					paramMap.put("strtransnbrpkgs", strtransnbrpkgs);
					paramMap.put("strtransdnnbrpkgs", strtransdnnbrpkgs);
					paramMap.put("strdnnbrpkgs", strdnnbrpkgs);
					paramMap.put("edocreatecd", edocreatecd);
					paramMap.put("nomvolume", nomvolume);
					paramMap.put("nomweight", nomweight);
					paramMap.put("releasenbrpkgs", releasenbrpkgs);
					paramMap.put("appointed_adp_cust_cd", appointed_adp_cust_cd);
					paramMap.put("appointed_adp_ic_tdbcr_nbr", appointed_adp_ic_tdbcr_nbr);
					paramMap.put("appointed_adp_nm", appointed_adp_nm);
					paramMap.put("distype", distype);
					paramMap.put("adpContactNbr", adpContactNbr);
					paramMap.put("taUenNo", taUenNo);
					paramMap.put("taCCode", taCCode);
					paramMap.put("taNmByJP", taNmByJP);
					paramMap.put("stredoasnnbr", stredoasnnbr);

				}
				log.info("updateEdoDetailsForDPE SQL :" + strUpdatetrans.toString() + "paramMap:" + paramMap);
				int count2 = namedParameterJdbcTemplate.update(strUpdatetrans.toString(), paramMap);
				if (count2 == 0) {
					throw new BusinessException("M20802");
				}
				if (!(strmodulecd.equalsIgnoreCase("ADPRENOM"))) {
					log.info("updateEdoDetailsForDPE SQL :" + strUpdatetrans1.toString() + "pstmt:");
					paramMap.put("mftseqnbr", mftseqnbr);
					paramMap.put("strtransnbr", strtransnbrManifest);
					paramMap.put("newnbrpkgs", newnbrpkgs);
					paramMap.put("lastmodifyuserid", lastmodifyuserid);
					paramMap.put("remarks",
							"Sub" + stredonbrpkgs + "&Add " + newnbrpkgs + "With EDO_NBR_PKGS");
					log.info("updateEdoDetailsForDPE SQL :" + strUpdatetrans1.toString() + "paramMap:" + paramMap);
					int count3 = namedParameterJdbcTemplate.update(strUpdatetrans1.toString(), paramMap);
					if (count3 == 0) {
						throw new BusinessException("M20802");
					}
				}
			}
			strinsertedoasnnbr = stredoasnnbr;
			log.info("updateEdoDetailsForDPE  Result:" + CommonUtility.deNull(strinsertedoasnnbr));
			// end
		} catch (BusinessException e) {
			log.info("Exception updateEdoDetailsForDPE : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception updateEdoDetailsForDPE : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateEdoDetailsForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO updateEdoDetailsForDPE ***");
		}
		return strinsertedoasnnbr;
	}

	// ejb.sessionBeans.gbms.cargo.edo --> EdoEjb --> checkAccountNbr()
	@Override
	public boolean checkAccountNbr(String accNbr) throws BusinessException {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		String straccnbrcount = "";
		

		try {
			log.info("START: checkAccountNbr  DAO  Start Obj " + accNbr);

			sb.append(" SELECT COUNT(*) FROM CUST_ACCT A, COMPANY_CODE B,  ");
			sb.append("CUSTOMER C  WHERE A.BUSINESS_TYPE LIKE '%G%' AND");
			sb.append(" A.ACCT_NBR IS NOT NULL AND A.ACCT_STATUS_CD='A' AND ");
			sb.append(" A.TRIAL_IND='N' AND B.CO_CD=C.CUST_CD  AND UPPER(A.ACCT_NBR)=UPPER(:accNbr) ");

			paramMap.put("accNbr", accNbr.toUpperCase());
			log.info("checkAccountNbr SQL " + sb.toString() + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet( sb.toString(), paramMap);
			while (rs.next()) {
				straccnbrcount = CommonUtility.deNull(rs.getString(1));
			}

			if (((straccnbrcount).trim().equalsIgnoreCase("")) || straccnbrcount == null) {
				straccnbrcount = "0";
			}
			int intaccnbrcount = Integer.parseInt(straccnbrcount);

			if (intaccnbrcount > 0) {
				return true;
			} else {
				return false;
			}
	
		} catch (NullPointerException e) {
			log.info("Exception checkAccountNbr : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkAccountNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkAccountNbr  DAO  END");
		}
	}

	// ejb.sessionBeans.gbms.cargo.edo --> EdoEjb --> getNominatedWeightVolume()
	private EdoValueObjectCargo getNominatedWeightVolume(String mftSeqNbr, String newNbrPkgs)
			throws BusinessException {
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
		String strmannbrpkg = "";
		String strmangrosswt = "";
		String strmangrossvol = "";

		try {
			log.info("START: getNominatedWeightVolume  DAO  Start Obj " + mftSeqNbr + newNbrPkgs);

			sb.append(" SELECT GROSS_WT, GROSS_VOL, NBR_PKGS FROM MANIFEST_DETAILS ");
			sb.append(" WHERE BL_STATUS='A' AND MFT_SEQ_NBR=:mftSeqNbr  ");

			paramMap.put("mftSeqNbr", mftSeqNbr);
			log.info("getNominatedWeightVolume SQL" + sb.toString() + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				strmannbrpkg = CommonUtility.deNull(rs.getString("NBR_PKGS"));
				strmangrosswt = CommonUtility.deNull(rs.getString("GROSS_WT"));
				strmangrossvol = CommonUtility.deNull(rs.getString("GROSS_VOL"));
			}

			float edonbrpkg = 0;
			float mannbrpkg = 0;
			float mangrosswt = 0;
			float mangrossvol = 0;
			float nomwt = 0;
			float nomvol = 0;
			edonbrpkg = Float.parseFloat(newNbrPkgs);
			mannbrpkg = Float.parseFloat(strmannbrpkg);
			mangrosswt = Float.parseFloat(strmangrosswt);
			mangrossvol = Float.parseFloat(strmangrossvol);
			nomwt = edonbrpkg / mannbrpkg * mangrosswt;
			nomvol = edonbrpkg / mannbrpkg * mangrossvol;
			BigDecimal bdnomwt = null;
			BigDecimal bdnomvol = null;
			bdnomwt = new BigDecimal(nomwt);
			bdnomwt = bdnomwt.setScale(2, RoundingMode.HALF_UP);
			bdnomvol = new BigDecimal(nomvol);
			bdnomvol = bdnomvol.setScale(2, RoundingMode.HALF_UP);
			edoValueObject.setNomWeight(String.valueOf(bdnomwt));
			edoValueObject.setNomVolume(String.valueOf(bdnomvol));
			log.info("getNominatedWeightVolume Result" + edoValueObject);
		} catch (NullPointerException e) {
			log.info("Exception getNominatedWeightVolume : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getNominatedWeightVolume : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getNominatedWeightVolume  DAO  END");
		}
		return edoValueObject;
	}

	// ejb:jp\src\ejb\sessionBeans\gbms\cargo\edo\EdoEjb.java
	private void insertSubAdpTxn(int subAdpNbr, String creat_userID, AdpValueObject adpObj)
			throws BusinessException {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: insertSubAdpTxn subAdpNbr:" + subAdpNbr + "creat_userID:" + creat_userID + "adpObj:"
					+ adpObj.toString());
			sb.append(" INSERT INTO SUB_ADP_TXN(SUB_ADP_NBR, TXN_DTTM, TXN_USER_ID, STATUS_CD,TRUCKER_CO_CD, ");
			sb.append(" TRUCKER_NM,TRUCKER_IC,TRUCKER_CONTACT_NBR,EDO_ESN_IND,TRUCKER_NBR_PKGS) ");
			sb.append(
					" values(:subAdpNbr, sysdate, :txnUserId, :statusCd, :truckerCoCd, :truckerNm, :truckerIc, :truckerContactNbr, :edoEsnInd, :truckerNbrPkgs) ");

			paramMap.put("subAdpNbr", subAdpNbr);
			paramMap.put("txnUserId", creat_userID);
			paramMap.put("statusCd", "A");
			paramMap.put("truckerCoCd", adpObj.getAdpCustCd());
			paramMap.put("truckerNm", adpObj.getAdpNm());
			paramMap.put("truckerIc", adpObj.getAdpIcTdbcrNbr());
			paramMap.put("truckerContactNbr", (adpObj.getAdpContact() == null || (adpObj.getAdpContact()).isEmpty() ? "  " : adpObj.getAdpContact()));
			paramMap.put("edoEsnInd", "1");
			paramMap.put("truckerNbrPkgs", Integer.parseInt(adpObj.getAdpNbrPkgs()));
			log.info("insertSubAdpTxn SQL" + sb.toString() + param + paramMap);
			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (NullPointerException e) {
			log.info("Exception insertSubAdpTxn : ", e);
		//	throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertSubAdpTxn : ", e);
		//	throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO insertSubAdpTxn");

		}
	}

	@Override
	public String deleteShutoutEdoDetails(String stredoasnnbr, String struserid) throws BusinessException {
		
		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();
		String strstatus = "FALSE";
		String strdnnbrpkgs = "0";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		try {
			log.info("START: deleteShutoutEdoDetails stredoasnnbr:" + stredoasnnbr + "struserid:" + struserid);
			sql1.append(" SELECT NBR_PKGS, MFT_SEQ_NBR,EDO_STATUS, DN_NBR_PKGS, ");
			sql1.append(" TRANS_NBR_PKGS, TRANS_DN_NBR_PKGS FROM GB_EDO  ");
			sql1.append(" WHERE EDO_ASN_NBR=:stredoasnnbr ");

			sql2.append("  UPDATE  GB_EDO SET EDO_STATUS = 'X', LAST_MODIFY_USER_ID =:struserid ");
			sql2.append(" ,LAST_MODIFY_DTTM =sysdate WHERE EDO_ASN_NBR=:stredoasnnbr ");

			log.info("getNominatedWeightVolume SQL" + sql1.toString());
			paramMap.put("stredoasnnbr", stredoasnnbr);
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
			while (rs1.next()) {
				strdnnbrpkgs = CommonUtility.deNull(rs1.getString("DN_NBR_PKGS"));
			}
			int dnPkgs = Integer.parseInt(strdnnbrpkgs);
			if (dnPkgs == 0) {
				paramMap.put("struserid", struserid);
				paramMap.put("stredoasnnbr", stredoasnnbr);
				log.info("SQl" + sql2.toString());
				int y = namedParameterJdbcTemplate.update(sql2.toString(), paramMap);

				if (y == 1) {
					strstatus = "TRUE";
				} else {
					log.info("Writing from Writing from EdoEjb.deleteEdoDetails");
					log.info("Error in Deleting Edo Number " + stredoasnnbr);
					throw new BusinessException("M20804");
				}
	
			}
		
		} catch (NullPointerException e) {
			log.info("Exception deleteShutoutEdoDetails : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception deleteShutoutEdoDetails : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception deleteShutoutEdoDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO deleteShutoutEdoDetails strstatus:" + strstatus);
		}
		return strstatus;

	}

	// ejb.sessionBeans.gbms.cargo.edo--->EdoEjb-->getEdoJpBillingNbr()
	@Override
	public List<EdoJpBilling> getEdoJpBillingNbr(String strAdpNbr, String strcustcd, String strVslCd) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet  rs = null;
		SqlRowSet  rs1 = null;
		List<EdoJpBilling> jpbnbrArrayList = new ArrayList<EdoJpBilling>();
		EdoJpBilling edoJpBilling =  new EdoJpBilling();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String sql;
		String subsql;
		String strjpbnbr = "";
		try {
			log.info("START:  *** getEdoJpBillingNbr Dao Start criteria : *** " + "strAdpNbr: "+ strAdpNbr + " strcustcd: "+ strcustcd + " strVslCd: "+ strVslCd);


			sb.append("SELECT A.ACCT_NBR,B.CO_NM FROM CUST_ACCT A, COMPANY_CODE B, ");
			sb.append("CUSTOMER C  WHERE A.BUSINESS_TYPE ");
			sb.append("LIKE '%G%' AND A.ACCT_NBR IS NOT NULL AND A.ACCT_STATUS_CD='A' AND A.TRIAL_IND='N' AND");
			sb.append(" B.CO_CD=C.CUST_CD AND C.CUST_CD=A.CUST_CD AND ( UPPER(C.TDB_CR_NBR)=UPPER( ");
			sb.append(":strAdpNbr ) OR UPPER(C.UEN_NBR)=UPPER(:strAdpNbr)) ORDER BY A.ACCT_NBR");
			sql = sb.toString();

			// this was commented on 11.03.2002 after lakshmi wants particular shipping
			// agent for login customer
			/*
			 * if (strcustcd.equalsIgnoreCase("JP")) { sql1=
			 * "SELECT A.PREV_ACCT_NBR FROM CUST_ACCT A, COMPANY_CODE B, CUSTOMER C"
			 * +" WHERE A.BUSINESS_TYPE LIKE '%G%' AND A.PREV_ACCT_NBR IS NOT NULL"
			 * +" AND A.ACCT_STATUS_CD='A' AND A.TRIAL_IND='N' AND"
			 * +" A.CUST_CD=B.CO_CD  AND B.CO_CD=C.CUST_CD" +" ORDER BY A.PREV_ACCT_NBR";
			 * }else{ sql1=
			 * "SELECT A.PREV_ACCT_NBR FROM CUST_ACCT A, COMPANY_CODE B, CUSTOMER C"
			 * +" WHERE A.BUSINESS_TYPE LIKE '%G%' AND A.PREV_ACCT_NBR IS NOT NULL"
			 * +" AND A.ACCT_STATUS_CD='A' AND A.TRIAL_IND='N' AND"
			 * +" A.CUST_CD=B.CO_CD  AND B.CO_CD=C.CUST_CD AND"
			 * +" A.CUST_CD='"+strcustcd+"' ORDER BY A.ACCT_NBR"; }
			 */

			// ++ 19.10.2009 FPT added for GB CR
			/*
			 * sql1= "SELECT ACCT_NBR FROM COMPANY_CODE,CUST_ACCT, "
			 * +"VESSEL_CALL WHERE CUST_CD = CO_CD AND ACCT_STATUS_CD='A' "
			 * +" AND BUSINESS_TYPE LIKE '%G%' AND TRIAL_IND='N' AND "
			 * +" ACCT_NBR IS NOT NULL  AND CO_CD = nvl(DECLARANT_CUST_CD,CREATE_CUST_CD) "
			 * +" AND VV_CD='"+strvslcd+"'";
			 */

			sb1.append("SELECT DISTINCT CA.ACCT_NBR,B.CO_NM ");
			sb1.append("FROM CUST_ACCT CA, VESSEL_CALL VC, COMPANY_CODE B, CUSTOMER C");
			sb1.append(
					" WHERE CA.ACCT_STATUS_CD='A' AND CA.BUSINESS_TYPE LIKE '%G%' AND CA.TRIAL_IND='N' AND CA.ACCT_NBR IS NOT NULL");
			sb1.append(" AND VC.VV_CD= :strVslCd AND B.CO_CD=C.CUST_CD AND C.CUST_CD=CA.CUST_CD AND ");
			sb1.append("(CA.CUST_CD = VC.CREATE_CUST_CD OR CA.CUST_CD IN ");
			sb1.append(" (SELECT VD.CUST_CD FROM VESSEL_CALL VC");
			sb1.append(" LEFT OUTER JOIN VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A')");
			sb1.append(" WHERE VC.VV_CD= :strVslCd ))");
			subsql = sb1.toString();
			paramMap.put("strAdpNbr", strAdpNbr);
			paramMap.put("strVslCd", strVslCd);
			log.info("getEdoJpBillingNbr  SQL " + sql + ",paramMap: " + paramMap.toString());
			log.info(" *** getEdoJpBillingNbr params *****" + paramMap.toString());
			
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				edoJpBilling = new EdoJpBilling();
				strjpbnbr = CommonUtility.deNull(rs.getString("ACCT_NBR"));
				String coNm = CommonUtility.deNull(rs.getString("CO_NM"));
				edoJpBilling.setCoNm(coNm);
				edoJpBilling.setStrAdp("(ADP)~");
				edoJpBilling.setStrjpbnbr(strjpbnbr);
				edoJpBilling.setFullNm(coNm+"(ADP)~"+strjpbnbr);
				if (!(strAdpNbr.equalsIgnoreCase(""))) {
					jpbnbrArrayList.add(edoJpBilling);
				}
			}

			log.info("getEdoJpBillingNbr  SQL" + subsql + ",paramMap:" + paramMap.toString());
			log.info(" *** getEdoJpBillingNbr params *****" + paramMap.toString());
			
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			while (rs1.next()) {
				edoJpBilling = new EdoJpBilling();
				strjpbnbr = CommonUtility.deNull(rs1.getString("ACCT_NBR"));
				String comName = CommonUtility.deNull(rs1.getString("CO_NM"));
				edoJpBilling.setCoNm(comName);
				edoJpBilling.setStrShippingAgent("(Shipping Agent)~");
				edoJpBilling.setStrjpbnbr(strjpbnbr);
				edoJpBilling.setFullNm(comName+"(Shipping Agent)~"+strjpbnbr);
				if (!(strAdpNbr.equalsIgnoreCase(""))) {
					jpbnbrArrayList.add(edoJpBilling);
				}
			}
			log.info("END: *** getEdoJpBillingNbr result  *****" + jpbnbrArrayList.size());
		} catch (Exception e) {
			log.info("Exception getEdoJpBillingNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getEdoJpBillingNbr  END ***** Result: " + jpbnbrArrayList.size());
		}

		return jpbnbrArrayList;
	}

	@Override
	public boolean getUserVesselEDO(String coCd, String asn) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sql = new StringBuilder();
		boolean result = false;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getUserVesselEDO DAO coCd:" + coCd + "asn:" + asn);
			sql.append(" SELECT EDO_CREATE_CD FROM GB_EDO  WHERE EDO_ASN_NBR =:asn ");
			paramMap.put("asn", asn);
			
			log.info(" *** getUserVesselEDO SQL *****" + sql);
			log.info(" *** getUserVesselEDO params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				String custCD = rs.getString("EDO_CREATE_CD");
				if (custCD.equals(coCd)) {
					result = true;
				}
			}
			log.info("END:getUserVesselEDO result:" + result);
		} catch (Exception e) {
			log.info("Exception getUserVesselEDO : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getUserVesselEDO DAO END:");
		}
		return result;
	}

	@Override
	public TableResult getEdoList(String strCustCode, String strVarNbr, String strmodulecd, Criteria criteria)
			throws BusinessException {
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		StringBuffer sb = null;
		List<EdoValueObjectCargo> edolistvector = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("getEdoList STARTS strCustCode:" + strCustCode + ",strVarNbr:" +strVarNbr + ",strmodulecd:" + strmodulecd + "criteria: " +criteria.toString());
			// 16.01.02
			// ++ 19.10.2009 - changed by vietnd02 for GB CR - set view for vessel operator
			// and third party
			if (strmodulecd.equalsIgnoreCase("ADPRENOM")) {
				/*
				 * sql="SELECT A.EDO_ASN_NBR, B.CRG_DES,B.CRG_TYPE, A.ADP_NM, A.NBR_PKGS"
				 * +", A.BL_NBR, B.NBR_PKGS, A.DN_NBR_PKGS,A.TRANS_DN_NBR_PKGS, "
				 * +"A.CRG_STATUS, A.EDO_STATUS, nvl(A.CUT_OFF_NBR_PKGS,0), B.GROSS_WT, B.GROSS_VOL FROM GB_EDO A, MANIFEST_DETAILS B"
				 * //added 'STUFF_IND' by vani -- 10th Oct,03 ,A.STUFF_IND removed by vinayak
				 * 19/12/2003 +" WHERE A.MFT_SEQ_NBR=B.MFT_SEQ_NBR " // added by Irene Tan on 15
				 * March 2003 : +" AND a.APPOINTED_ADP_CUST_CD='" + strCustCode + "'" // end
				 * added by Irene Tan on 15 March 2003
				 * +" AND A.EDO_STATUS ='A'  AND A.VAR_NBR='"+strVarNbr
				 * +"' ORDER BY A.EDO_ASN_NBR";
				 */
				sb = new StringBuffer();
				sb.append(
						"	SELECT A.EDO_ASN_NBR, B.CRG_DES,B.CRG_TYPE, A.ADP_NM, A.NBR_PKGS edonbrpkgs, A.BL_NBR, B.NBR_PKGS, A.DN_NBR_PKGS,");
				sb.append(
						"	 A.TRANS_DN_NBR_PKGS, A.CRG_STATUS, A.EDO_STATUS, nvl(A.CUT_OFF_NBR_PKGS,0), B.GROSS_WT, B.GROSS_VOL ");
				sb.append(
						"	 , DECODE(B.UNSTUFF_SEQ_NBR,0,'N','Y') UNSTF_IND,B.CARGO_CATEGORY_CD, B.DG_IND, A.DIS_TYPE, A.WH_IND ");
				sb.append(
						"	 , CC.CC_NAME, NVL(VS.SCHEME_CD, VC.SCHEME) SCHEME, B.NBR_PKGS_IN_PORT, VC.TERMINAL, NVL(VS.SCHEME_CD, VC.COMBI_GC_SCHEME) COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND ");
				sb.append("	 FROM GB_EDO A  ");
				sb.append("	 INNER JOIN  MANIFEST_DETAILS B ON A.MFT_SEQ_NBR   =B.MFT_SEQ_NBR ");
				sb.append("	 INNER JOIN  VESSEL_CALL VC ON B.VAR_NBR= VC.VV_CD  ");
				sb.append("	 INNER JOIN CARGO_CATEGORY_CODE CC ON B.CARGO_CATEGORY_CD = CC.CC_CD ");
				sb.append("	 LEFT JOIN VESSEL_SCHEME VS ON B.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR  ");
				sb.append("	 WHERE A.VAR_NBR=:strVarNbr ");
				sb.append("	 AND A.EDO_STATUS ='A'");
				if (!"JP".equalsIgnoreCase(strCustCode)) {
					sb.append(" AND (VC.CREATE_CUST_CD=:strCustCode OR A.EDO_CREATE_CD = :strCustCode ");
					sb.append("OR A.APPOINTED_ADP_CUST_CD=:strCustCode)");
				}

				sb.append(" ORDER BY A.BL_NBR");
				if (!"JP".equalsIgnoreCase(strCustCode)) {
					paramMap.put("strCustCode", strCustCode);
				}
				paramMap.put("strVarNbr", strVarNbr);
			} else {
				/*
				 * sql="SELECT A.EDO_ASN_NBR, B.CRG_DES,B.CRG_TYPE, A.ADP_NM, A.NBR_PKGS"
				 * +", A.BL_NBR, B.NBR_PKGS, A.DN_NBR_PKGS,A.TRANS_DN_NBR_PKGS, "
				 * +"A.CRG_STATUS, A.EDO_STATUS, nvl(A.CUT_OFF_NBR_PKGS,0), B.GROSS_WT, B.GROSS_VOL FROM GB_EDO A, MANIFEST_DETAILS B"
				 * //added 'STUFF_IND' by vani -- 10th Oct,03 ,A.STUFF_IND removed by vinayak
				 * 19/12/2003 +" WHERE A.MFT_SEQ_NBR=B.MFT_SEQ_NBR "
				 * +" AND A.EDO_STATUS IN('A','V')  AND A.VAR_NBR='"+strVarNbr
				 * +"' ORDER BY A.EDO_ASN_NBR";
				 */
				boolean isShowEdoInfo = false;

				try {
					TextParaVO code = new TextParaVO();
					code.setParaCode(TEXT_PARA_GC_VIEW_EDO);
					TextParaVO result = getParaCodeInfo(code);
					isShowEdoInfo = isShowEdoInfo(strCustCode, result);
				} catch (Exception e) {
					log.info("Exception getEdoList Retriving text para : ", e);
					throw new BusinessException("M4201");
				}
				sb = new StringBuffer();
				if (isShowEdoInfo) {
					sb.append(
							"  SELECT A.EDO_ASN_NBR, B.CRG_DES,B.CRG_TYPE, A.ADP_NM, A.NBR_PKGS edonbrpkgs, A.BL_NBR, B.NBR_PKGS, A.DN_NBR_PKGS,");
					sb.append(
							"  A.TRANS_DN_NBR_PKGS, A.CRG_STATUS, A.EDO_STATUS, nvl(A.CUT_OFF_NBR_PKGS,0), B.GROSS_WT, B.GROSS_VOL");
					sb.append(
							"  , DECODE(B.UNSTUFF_SEQ_NBR,0,'N','Y') UNSTF_IND,B.CARGO_CATEGORY_CD, B.DG_IND, A.DIS_TYPE,  A.WH_IND ");
					sb.append(
							"  , CC.CC_NAME, NVL(VS.SCHEME_CD, VC.SCHEME) SCHEME, B.NBR_PKGS_IN_PORT, VC.TERMINAL, NVL(VS.SCHEME_CD, VC.COMBI_GC_SCHEME) COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND ");
					sb.append("  FROM GB_EDO A  ");
					sb.append("  INNER JOIN  MANIFEST_DETAILS B ON A.MFT_SEQ_NBR   =B.MFT_SEQ_NBR ");
					sb.append("  INNER JOIN   VESSEL_CALL VC ON B.VAR_NBR= VC.VV_CD  ");
					sb.append("  INNER JOIN CARGO_CATEGORY_CODE CC ON B.CARGO_CATEGORY_CD = CC.CC_CD ");
					sb.append("  LEFT JOIN VESSEL_SCHEME VS ON B.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR ");
					sb.append("  WHERE A.EDO_STATUS IN('A')  AND A.VAR_NBR=:strVarNbr  ");
					sb.append("  ORDER BY A.BL_NBR");
					paramMap.put("strVarNbr", strVarNbr);
				} else {
					sb.append(
							"SELECT A.EDO_ASN_NBR, B.CRG_DES,B.CRG_TYPE, A.ADP_NM, A.NBR_PKGS edonbrpkgs, A.BL_NBR, B.NBR_PKGS, A.DN_NBR_PKGS,");
					sb.append(
							" A.TRANS_DN_NBR_PKGS, A.CRG_STATUS, A.EDO_STATUS, nvl(A.CUT_OFF_NBR_PKGS,0), B.GROSS_WT, B.GROSS_VOL");
					sb.append(
							" , DECODE(B.UNSTUFF_SEQ_NBR,0,'N','Y') UNSTF_IND,B.CARGO_CATEGORY_CD, B.DG_IND, A.DIS_TYPE,  A.WH_IND ");
					sb.append(
							" , CC.CC_NAME, NVL(VS.SCHEME_CD, VC.SCHEME) SCHEME, B.NBR_PKGS_IN_PORT, VC.TERMINAL, NVL(VS.SCHEME_CD, VC.COMBI_GC_SCHEME) COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND ");
					sb.append(" FROM GB_EDO A  ");
					sb.append(" INNER JOIN  MANIFEST_DETAILS B ON A.MFT_SEQ_NBR   =B.MFT_SEQ_NBR ");
					sb.append(" INNER JOIN   VESSEL_CALL VC ON B.VAR_NBR= VC.VV_CD  ");
					sb.append(" INNER JOIN CARGO_CATEGORY_CODE CC ON B.CARGO_CATEGORY_CD = CC.CC_CD ");
					sb.append(" LEFT JOIN VESSEL_SCHEME VS ON B.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR ");
					sb.append(" WHERE A.EDO_STATUS IN('A')  AND A.VAR_NBR=:strVarNbr ");
					sb.append(" AND (VC.CREATE_CUST_CD=:strCustCode  OR A.EDO_CREATE_CD = :strCustCode )");
					sb.append(" ORDER BY A.BL_NBR");
					paramMap.put("strCustCode", strCustCode);
					paramMap.put("strVarNbr", strVarNbr);
				}
			}

			if (strmodulecd.equalsIgnoreCase("EDOADPVIEW")) {
				/*
				 * sql="SELECT A.EDO_ASN_NBR,B.CRG_DES,B.CRG_TYPE, A.ADP_NM, A.NBR_PKGS"
				 * +", A.BL_NBR, B.NBR_PKGS, A.DN_NBR_PKGS,A.TRANS_DN_NBR_PKGS, "
				 * +"A.CRG_STATUS, A.EDO_STATUS, nvl(A.CUT_OFF_NBR_PKGS,0), B.GROSS_WT, B.GROSS_VOL FROM GB_EDO A, MANIFEST_DETAILS B"
				 * //added 'STUFF_IND' by vani -- 10th Oct,03 ,A.STUFF_IND removed by vinayak
				 * 19/12/2003 +" WHERE A.MFT_SEQ_NBR=B.MFT_SEQ_NBR "
				 * +" AND A.EDO_STATUS IN('A','V')  AND A.VAR_NBR='"+strVarNbr
				 * +"' AND (A.ADP_CUST_CD='"+strCustCode +"' OR A.CA_CUST_CD='"+strCustCode
				 * +"') ORDER BY A.EDO_ASN_NBR";
				 */
				sb = new StringBuffer();
				sb.append(
						"	SELECT A.EDO_ASN_NBR,B.CRG_DES,B.CRG_TYPE, A.ADP_NM, A.NBR_PKGS edonbrpkgs, A.BL_NBR, B.NBR_PKGS, A.DN_NBR_PKGS,");
				sb.append(
						"	 A.TRANS_DN_NBR_PKGS, A.CRG_STATUS, A.EDO_STATUS, nvl(A.CUT_OFF_NBR_PKGS,0), B.GROSS_WT, B.GROSS_VOL");
				sb.append(
						"	 , DECODE(B.UNSTUFF_SEQ_NBR,0,'N','Y') UNSTF_IND,B.CARGO_CATEGORY_CD, B.DG_IND, A.DIS_TYPE,  A.WH_IND ");
				sb.append(
						"	 , CC.CC_NAME, NVL(VS.SCHEME_CD, VC.SCHEME) SCHEME, B.NBR_PKGS_IN_PORT,VC.TERMINAL, NVL(VS.SCHEME_CD, VC.COMBI_GC_SCHEME) COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND ");
				sb.append("	 FROM GB_EDO A  ");
				sb.append("	 INNER JOIN  MANIFEST_DETAILS B ON A.MFT_SEQ_NBR   =B.MFT_SEQ_NBR ");
				sb.append("	 INNER JOIN   VESSEL_CALL VC ON B.VAR_NBR= VC.VV_CD  ");
				sb.append("	 INNER JOIN CARGO_CATEGORY_CODE CC ON B.CARGO_CATEGORY_CD = CC.CC_CD ");
				sb.append("	 LEFT JOIN VESSEL_SCHEME VS ON B.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR ");
				sb.append("	 WHERE A.EDO_STATUS IN('A')");
				sb.append("	 AND A.VAR_NBR=:strVarNbr ");
				sb.append("	 AND (VC.CREATE_CUST_CD=:strCustCode OR A.EDO_CREATE_CD = :strCustCode");
				sb.append("	 OR A.ADP_CUST_CD=:strCustCode OR A.CA_CUST_CD=:strCustCode)");
				sb.append("	 ORDER BY A.BL_NBR ");
				paramMap.put("strCustCode", strCustCode);
				paramMap.put("strVarNbr", strVarNbr);
			}
			
			log.info("EDOEJB.paramMap + " + paramMap.toString());
			// -- 19.10.2009 - changed by vietnd02 for GB CR - set view for vessel operator
			// and third party

			edolistvector = new ArrayList<>();

			String sql = sb.toString();
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			log.info("SQL" + sql + param + paramMap);
			
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
				String edoasnnbr = CommonUtility.deNull(rs.getString(1));
				String crgdes = CommonUtility.deNull(rs.getString(2));
				String crgtypnm = CommonUtility.deNull(rs.getString(3));
				String adpnm = CommonUtility.deNull(rs.getString(4));
				String edonbrpkgs = CommonUtility.deNull(rs.getString(5));
				String blnbr = CommonUtility.deNull(rs.getString(6));
				String nbrpkgs = CommonUtility.deNull(rs.getString(7));
				String dnnbrpkgs = CommonUtility.deNull(rs.getString(8));
				String transdnnbrpkgs = CommonUtility.deNull(rs.getString(9));
				String crgstatus = CommonUtility.deNull(rs.getString(10));
				String edostatus = CommonUtility.deNull(rs.getString(11));
				String cutoffamountdelivered = CommonUtility.deNull(rs.getString(12));
				String weight = CommonUtility.deNull(rs.getString(13)); // Added by ThanhNV2
				String volume = CommonUtility.deNull(rs.getString(14));// Added by ThanhNV2
				// CR-CIM- 0000108
				String unstuffInd = CommonUtility.deNull(rs.getString(15));
				String crgCategoryCd = CommonUtility.deNull(rs.getString(16));
				String dgInd = CommonUtility.deNull(rs.getString(17));
				String opInd = CommonUtility.deNull(rs.getString(18));
				String whInd = CommonUtility.deNull(rs.getString(19));
				String crgCategoryName = CommonUtility.deNull(rs.getString(20));
				// CR-CIM- 0000108
				// String strStfInd = CommonUtility.deNull(rs.getString(13)); added by vani --
				// 10th Oct,03 ,A.STUFF_IND removed by vinayak 19/12/2003
				int amountdelivered = 0;
				if (edoasnnbr.length() == 7)
					edoasnnbr = "0".concat(edoasnnbr);
				if (edoasnnbr.length() == 6)
					edoasnnbr = "00".concat(edoasnnbr);
				if (edoasnnbr.length() == 5)
					edoasnnbr = "000".concat(edoasnnbr);
				if (edoasnnbr.length() == 4)
					edoasnnbr = "0000".concat(edoasnnbr);

				if (dnnbrpkgs == null) {
					dnnbrpkgs = "0";
				} else {
					if (dnnbrpkgs.equalsIgnoreCase("")) {
						dnnbrpkgs = "0";
					}
				}
				if (transdnnbrpkgs == null) {
					transdnnbrpkgs = "0";
				} else {
					if (transdnnbrpkgs.equalsIgnoreCase("")) {
						transdnnbrpkgs = "0";
					}
				}

				if (cutoffamountdelivered == null) {
					cutoffamountdelivered = "0";
				} else {
					if (cutoffamountdelivered.equalsIgnoreCase("")) {
						cutoffamountdelivered = "0";
					}
				}
				amountdelivered = Integer.parseInt(dnnbrpkgs) + Integer.parseInt(transdnnbrpkgs);
				int cutamtdel = Integer.parseInt(cutoffamountdelivered);
				int tempamtdel = amountdelivered + cutamtdel;
				amountdelivered = tempamtdel;

				// HaiTTH1 added on 10/1/2014
				String scheme = CommonUtility.deNull(rs.getString("SCHEME"));
				String subScheme = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				String gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));
				String terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				String short_landed_pkgs = CommonUtility.deNull(rs.getString("NBR_PKGS_IN_PORT"));
				// String dn_nbr = CommonUtility.deNull(rs.getString("DN_NBR"));

				edoValueObject.setEdoAsnNbr(edoasnnbr);
				edoValueObject.setCrgDes(crgdes);
				edoValueObject.setCrgTypeNm(crgtypnm);
				edoValueObject.setAdpNm(adpnm);
				edoValueObject.setEdoNbrPkgs(edonbrpkgs);
				edoValueObject.setBlNbr(blnbr);
				edoValueObject.setNbrPkgs(nbrpkgs);
				edoValueObject.setDnNbrPkgs(String.valueOf(amountdelivered));
				edoValueObject.setEdoStatus(edostatus);
				edoValueObject.setNomWeight(weight);// Added by ThanhNV2
				edoValueObject.setNomVolume(volume); // Added by ThanhNV2
				// CR-CIM- 0000108
				edoValueObject.setUnstuffInd(unstuffInd);
				edoValueObject.setCrgCategoryCd(crgCategoryCd);
				edoValueObject.setDgInd(dgInd);
				edoValueObject.setDisOprInd(opInd);
				edoValueObject.setWhInd(whInd);
				edoValueObject.setCrgCategoryName(crgCategoryName);
				// HaiTTH1 added on 10/1/2014
				edoValueObject.setScheme(scheme);
				edoValueObject.setSubScheme(subScheme);
				edoValueObject.setGcOperations(gcOperations);
				edoValueObject.setTerminal(terminal);
				edoValueObject.setShort_landed_pkgs(short_landed_pkgs);
				// edoValueObject.setDn_nbr(dn_nbr);
				edoValueObject.setCrgStatus(crgstatus);

				// CR-CIM- 0000108
				// edoValueObject.setStfInd(strStfInd);//added by vani -- 10th Oct,03 removed by
				// 19/12/2003
				edolistvector.add(edoValueObject);
			}
			for (EdoValueObjectCargo object : edolistvector) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			
			log.info("END: *** getEdoList Result *****" + edolistvector.size());
		} catch (BusinessException e) {
			log.info("Exception getEdoList : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getEdoList : ", e);
			throw new BusinessException("M1004");
		} finally {
			log.info("END: **** getEdoList EDO ******");
		}
		return tableResult;
	}
	
	@Override
	public TableResult getEdoListTotal(String strCustCode, String strVarNbr, String strmodulecd) throws BusinessException {
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		StringBuffer sb = null;
		List<EdoValueObjectCargo> edolistvector = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("getEdoListTotal STARTS strCustCode:" + strCustCode + ",strVarNbr:" +strVarNbr + ",strmodulecd:" + strmodulecd);
			if (strmodulecd.equalsIgnoreCase("ADPRENOM")) {
				sb = new StringBuffer();
				sb.append("SELECT A.NBR_PKGS edonbrpkgs, A.BL_NBR, B.NBR_PKGS, A.DN_NBR_PKGS,A.TRANS_DN_NBR_PKGS,");
				sb.append("nvl(A.CUT_OFF_NBR_PKGS,0) cutOffAmount, B.GROSS_WT, B.GROSS_VOL, B.NBR_PKGS_IN_PORT ");
				sb.append("	 FROM GB_EDO A  ");
				sb.append("	 INNER JOIN  MANIFEST_DETAILS B ON A.MFT_SEQ_NBR   =B.MFT_SEQ_NBR ");
				sb.append("	 INNER JOIN  VESSEL_CALL VC ON B.VAR_NBR= VC.VV_CD  ");
				sb.append("	 INNER JOIN CARGO_CATEGORY_CODE CC ON B.CARGO_CATEGORY_CD = CC.CC_CD ");
				sb.append("	 LEFT JOIN VESSEL_SCHEME VS ON B.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR  ");
				sb.append("	 WHERE A.VAR_NBR=:strVarNbr ");
				sb.append("	 AND A.EDO_STATUS ='A'");
				if (!"JP".equalsIgnoreCase(strCustCode)) {
					sb.append(" AND (VC.CREATE_CUST_CD=:strCustCode OR A.EDO_CREATE_CD = :strCustCode ");
					sb.append("OR A.APPOINTED_ADP_CUST_CD=:strCustCode)");
				}
				sb.append(" ORDER BY A.BL_NBR");
				if (!"JP".equalsIgnoreCase(strCustCode)) {
					paramMap.put("strCustCode", strCustCode);
				}
				paramMap.put("strVarNbr", strVarNbr);
			} else {
				boolean isShowEdoInfo = false;
				try {
					TextParaVO code = new TextParaVO();
					code.setParaCode(TEXT_PARA_GC_VIEW_EDO);
					TextParaVO result = getParaCodeInfo(code);
					isShowEdoInfo = isShowEdoInfo(strCustCode, result);
					
					log.info(" *** getEdoListTotal result *****" + isShowEdoInfo);
				} catch (Exception e) {
					log.info("Exception getEdoListTotal Retriving text para : ", e);
					throw new BusinessException("M4201");
				}
				sb = new StringBuffer();
				if (isShowEdoInfo) {
					sb.append(" SELECT A.NBR_PKGS edonbrpkgs, B.NBR_PKGS, A.DN_NBR_PKGS, A.TRANS_DN_NBR_PKGS,");
					sb.append("nvl(A.CUT_OFF_NBR_PKGS,0) cutOffAmount, B.GROSS_WT, B.GROSS_VOL, B.NBR_PKGS_IN_PORT");
					sb.append("  FROM GB_EDO A  ");
					sb.append("  INNER JOIN  MANIFEST_DETAILS B ON A.MFT_SEQ_NBR   =B.MFT_SEQ_NBR ");
					sb.append("  INNER JOIN   VESSEL_CALL VC ON B.VAR_NBR= VC.VV_CD  ");
					sb.append("  INNER JOIN CARGO_CATEGORY_CODE CC ON B.CARGO_CATEGORY_CD = CC.CC_CD ");
					sb.append("  LEFT JOIN VESSEL_SCHEME VS ON B.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR ");
					sb.append("  WHERE A.EDO_STATUS IN('A')  AND A.VAR_NBR=:strVarNbr  ");
					sb.append("  ORDER BY A.BL_NBR");
					paramMap.put("strVarNbr", strVarNbr);
				} else {
					sb.append( "SELECT A.NBR_PKGS edonbrpkgs, B.NBR_PKGS, A.DN_NBR_PKGS, A.TRANS_DN_NBR_PKGS,");
					sb.append("nvl(A.CUT_OFF_NBR_PKGS,0) cutOffAmount, B.GROSS_WT, B.GROSS_VOL, B.NBR_PKGS_IN_PORT");
					sb.append(" FROM GB_EDO A  ");
					sb.append(" INNER JOIN  MANIFEST_DETAILS B ON A.MFT_SEQ_NBR   =B.MFT_SEQ_NBR ");
					sb.append(" INNER JOIN   VESSEL_CALL VC ON B.VAR_NBR= VC.VV_CD  ");
					sb.append(" INNER JOIN CARGO_CATEGORY_CODE CC ON B.CARGO_CATEGORY_CD = CC.CC_CD ");
					sb.append(" LEFT JOIN VESSEL_SCHEME VS ON B.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR ");
					sb.append(" WHERE A.EDO_STATUS IN('A')  AND A.VAR_NBR=:strVarNbr ");
					sb.append(" AND (VC.CREATE_CUST_CD=:strCustCode  OR A.EDO_CREATE_CD = :strCustCode )");
					sb.append(" ORDER BY A.BL_NBR");
					paramMap.put("strVarNbr", strVarNbr);
					paramMap.put("strCustCode", strCustCode);
				}
			}

			if (strmodulecd.equalsIgnoreCase("EDOADPVIEW")) {
				sb = new StringBuffer();
				sb.append( "SELECT A.NBR_PKGS edonbrpkgs, B.NBR_PKGS, A.DN_NBR_PKGS, A.TRANS_DN_NBR_PKGS,");
				sb.append(" nvl(A.CUT_OFF_NBR_PKGS,0) cutOffAmount, B.GROSS_WT, B.GROSS_VOL, B.NBR_PKGS_IN_PORT");
				sb.append("	 FROM GB_EDO A  ");
				sb.append("	 INNER JOIN  MANIFEST_DETAILS B ON A.MFT_SEQ_NBR   =B.MFT_SEQ_NBR ");
				sb.append("	 INNER JOIN   VESSEL_CALL VC ON B.VAR_NBR= VC.VV_CD  ");
				sb.append("	 INNER JOIN CARGO_CATEGORY_CODE CC ON B.CARGO_CATEGORY_CD = CC.CC_CD ");
				sb.append("	 LEFT JOIN VESSEL_SCHEME VS ON B.MIXED_SCHEME_ACCT_NBR = VS.ACCT_NBR ");
				sb.append("	 WHERE A.EDO_STATUS IN('A')");
				sb.append("	 AND A.VAR_NBR=:strVarNbr ");
				sb.append("	 AND (VC.CREATE_CUST_CD=:strCustCode OR A.EDO_CREATE_CD = :strCustCode");
				sb.append("	 OR A.ADP_CUST_CD=:strCustCode OR A.CA_CUST_CD=:strCustCode)");
				sb.append("	 ORDER BY A.BL_NBR ");
				paramMap.put("strCustCode", strCustCode);
				paramMap.put("strVarNbr", strVarNbr);
			}
			log.info("EDOEJB.getEdoListTotal + " + sb.toString() + param + paramMap);
			edolistvector = new ArrayList<>();
			String sql = sb.toString();
			log.info("SQL" + sql + param + paramMap);
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
				String edonbrpkgs = CommonUtility.deNull(rs.getString("EDONBRPKGS")); 
				String nbrpkgs = CommonUtility.deNull(rs.getString("NBR_PKGS")); 
				String dnnbrpkgs = CommonUtility.deNull(rs.getString("DN_NBR_PKGS")); 
				String transdnnbrpkgs = CommonUtility.deNull(rs.getString("TRANS_DN_NBR_PKGS")); 
				String cutoffamountdelivered = CommonUtility.deNull(rs.getString("cutOffAmount")); 
				String weight = CommonUtility.deNull(rs.getString("GROSS_WT")); 
				String volume = CommonUtility.deNull(rs.getString("GROSS_VOL"));
				
				int amountdelivered = 0;

				if (dnnbrpkgs == null) {
					dnnbrpkgs = "0";
				} else {
					if (dnnbrpkgs.equalsIgnoreCase("")) {
						dnnbrpkgs = "0";
					}
				}
				if (transdnnbrpkgs == null) {
					transdnnbrpkgs = "0";
				} else {
					if (transdnnbrpkgs.equalsIgnoreCase("")) {
						transdnnbrpkgs = "0";
					}
				}

				if (cutoffamountdelivered == null) {
					cutoffamountdelivered = "0";
				} else {
					if (cutoffamountdelivered.equalsIgnoreCase("")) {
						cutoffamountdelivered = "0";
					}
				}
				amountdelivered = Integer.parseInt(dnnbrpkgs) + Integer.parseInt(transdnnbrpkgs);
				int cutamtdel = Integer.parseInt(cutoffamountdelivered);
				int tempamtdel = amountdelivered + cutamtdel;
				amountdelivered = tempamtdel;

				String short_landed_pkgs = CommonUtility.deNull(rs.getString("NBR_PKGS_IN_PORT"));

				edoValueObject.setEdoNbrPkgs(edonbrpkgs); 
				edoValueObject.setNbrPkgs(nbrpkgs); 
				edoValueObject.setDnNbrPkgs(String.valueOf(amountdelivered)); 
				edoValueObject.setNomWeight(weight); 
				edoValueObject.setNomVolume(volume);  
				edoValueObject.setShort_landed_pkgs(short_landed_pkgs); 
				edolistvector.add(edoValueObject);
			}
			for (EdoValueObjectCargo object : edolistvector) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			
			log.info("END: *** getEdoListTotal Result *****" + tableResult);
		} catch (BusinessException e) {
			log.info("Exception getEdoListTotal : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getEdoListTotal : ", e);
			throw new BusinessException("M1004");
		} finally {
			log.info("getEdoListTotal 2 params ENDS");
		}
		return tableResult;
	}

	// method : sg.com.jp.dpe.dao.DpeCargoJdbcDao-->loadGeneralShutoutCargoByEDO
	@Override
	public DPECargo loadGeneralShutoutCargoByEDO(String edo_asn_nbr, String trans_type) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Map<String, String> param = new HashMap<String, String>();
		DPECargo dpeCargo = new DPECargo();
		String sql = "";

		try {
			log.info("START: loadGeneralShutoutCargoByESN edo_asn_nbr:" + edo_asn_nbr + "trans_type:" + trans_type);
			sb.append("SELECT ");
			sb.append("	VV_CD, ");
			sb.append("	VSL_NM, ");
			sb.append("	OUT_VOY_NBR, ");
			sb.append("	ESN_ASN_NBR, ");
			sb.append("	(CARGO_TYPE || '--' || CRG_TYPE_NM) CARGO_TYPE, ");
			sb.append("	ESN_HS_CODE, ");
			sb.append("	CRG_DES, ");
			sb.append("	MARKINGS, ");
			sb.append("	DECODE(ESN_DG_IND, 'N', 'No', 'Y', 'Yes') ESN_DG_IND, ");
			sb.append("	DECODE(STG_IND, 'O', 'Open', 'C', 'Covered') STG_IND, ");
			sb.append("	FREE_STG_DAYS, ");
			sb.append("	PKG_TYPE, ");
			sb.append("	PKG_DESC, ");
			sb.append("	NVL(ESN_PKG, 0) ESN_PKG, ");
			sb.append("	NVL(SHUTOUT_PKG, 0) SHUTOUT_PKG, ");
			sb.append("	OUTSTANDING_PKG, ");
			sb.append("	ADP_IC_TDBCR_NBR, ");
			sb.append("	ADP_NM, ");
			sb.append("	NVL(EDO_PKGS, 0) EDO_PKGS, ");
			sb.append("	NOM_WT, ");
			sb.append("	NOM_VOL, ");
			sb.append("	ACCT_NBR, ");
			sb.append("	EDO_DELIVERY_TO, ");
			sb.append("	WH_IND, ");
			sb.append("	WH_AGGR_NBR, ");
			sb.append("	WH_REMARKS, ");
			sb.append("	EDO_ASN_NBR, ");
			sb.append("	ROUND(ESN_PKG*ESN_WT / NBR_PKGS, 2) AS ESNPKG_WT, ");
			sb.append("	ROUND(ESN_PKG*ESN_VOL / NBR_PKGS, 2) AS ESNPKG_VOL, ");
			sb.append("	ROUND(SHUTOUT_PKG*ESN_WT / NBR_PKGS, 2) AS SHUTOUTPKG_WT, ");
			sb.append("	ROUND(SHUTOUT_PKG*ESN_VOL / NBR_PKGS, 2) AS SHUTOUTPKG_VOL, ");
			sb.append("	ROUND(OUTSTANDING_PKG*ESN_WT / NBR_PKGS, 2) AS OUTSTANDING_WT, ");
			sb.append("	ROUND(OUTSTANDING_PKG*ESN_VOL / NBR_PKGS, 2) AS OUTSTANDING_VOL, ");
			sb.append("	DN_NBR_PKGS ");
			sb.append("FROM ");
			sb.append("	( ");
			sb.append("	SELECT ");
			sb.append(
					"		VC.VV_CD, VC.VSL_NM, VC.OUT_VOY_NBR, ESN.ESN_ASN_NBR, BKD.CARGO_TYPE, CGR.CRG_TYPE_NM, ESND.ESN_HS_CODE, ESND.CRG_DES, ESNM.MARKINGS, ESND.ESN_DG_IND, ESND.STG_IND, ESND.PKG_TYPE, PKG.PKG_DESC, ESND.NBR_PKGS, ESND.ESN_WT, ESND.ESN_VOL, ESND.UA_NBR_PKGS AS ESN_PKG, NVL(BKD.SHUTOUT_QTY, 0) AS SHUTOUT_PKG, ");
			// sb.append(" ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED AS SHUTOUT_PKG, ");
			// sb.append(" ESND.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED -
			// BKD.SHUTOUT_DELIVERY_PKGS AS OUTSTANDING_PKG, ");
			sb.append(
					" NVL(BKD.SHUTOUT_QTY, 0) - BKD.SHUTOUT_DELIVERY_PKGS AS OUTSTANDING_PKG, EDO.ADP_IC_TDBCR_NBR, EDO.ADP_NM, EDO.NBR_PKGS AS EDO_PKGS, EDO.NOM_WT, EDO.NOM_VOL, EDO.ACCT_NBR, EDO.EDO_DELIVERY_TO, EDO.WH_IND, EDO.WH_AGGR_NBR, EDO.WH_REMARKS, EDO.EDO_ASN_NBR, EDO.FREE_STG_DAYS, EDO.DN_NBR_PKGS ");
			sb.append("	FROM ");
			sb.append(
					"		VESSEL_CALL VC, ESN ESN, BK_DETAILS BKD, ESN_DETAILS ESND, ESN_MARKINGS ESNM, GB_EDO EDO, CRG_TYPE CGR, PKG_TYPES PKG ");
			sb.append("	WHERE ");
			sb.append("		VC.VV_CD = BKD.VAR_NBR ");
			sb.append("		AND CGR.CRG_TYPE_CD = BKD.CARGO_TYPE ");
			sb.append("		AND BKD.BK_REF_NBR = ESN.BK_REF_NBR ");
			sb.append("		AND ESND.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
			sb.append("		AND EDO.EDO_STATUS = 'A' ");
			sb.append("		AND ESND.PKG_TYPE = PKG.PKG_TYPE_CD ");
			sb.append("		AND ESNM.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
			sb.append("		AND EDO.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
			sb.append("		AND EDO.EDO_ASN_NBR =:edo_asn_nbr )");

			if (StringUtils.equalsIgnoreCase(trans_type, "C")) {
				sb.setLength(0);
				sb.append("SELECT ");
				sb.append("	VV_CD, ");
				sb.append("	VSL_NM, ");
				sb.append("	OUT_VOY_NBR, ");
				sb.append("	ESN_ASN_NBR, ");
				sb.append("	(CARGO_TYPE || '--' || CRG_TYPE_NM) CARGO_TYPE, ");
				sb.append("	ESN_HS_CODE, ");
				sb.append("	CRG_DES, ");
				sb.append("	MARKINGS, ");
				sb.append("	DECODE(ESN_DG_IND, 'N', 'No', 'Y', 'Yes') ESN_DG_IND, ");
				sb.append("	DECODE(STG_IND, 'O', 'Open', 'C', 'Covered') STG_IND, ");
				sb.append("	FREE_STG_DAYS, ");
				sb.append("	PKG_TYPE, ");
				sb.append("	PKG_DESC, ");
				sb.append("	ESN_PKG, ");
				sb.append("	NVL(SHUTOUT_PKG, 0) SHUTOUT_PKG, ");
				sb.append("	NVL(OUTSTANDING_PKG, 0 ) OUTSTANDING_PKG, ");
				sb.append("	ADP_IC_TDBCR_NBR, ");
				sb.append("	ADP_NM, ");
				sb.append("	EDO_PKGS, ");
				sb.append("	NOM_WT, ");
				sb.append("	NOM_VOL, ");
				sb.append("	ACCT_NBR, ");
				sb.append("	EDO_DELIVERY_TO, ");
				sb.append("	WH_IND, ");
				sb.append("	WH_AGGR_NBR, ");
				sb.append("	WH_REMARKS, ");
				sb.append("	EDO_ASN_NBR, ");
				sb.append("	ROUND(ESN_PKG*ESN_WT / NBR_PKGS, 2) AS ESNPKG_WT, ");
				sb.append("	ROUND(ESN_PKG*ESN_VOL / NBR_PKGS, 2) AS ESNPKG_VOL, ");
				sb.append("	ROUND(SHUTOUT_PKG*ESN_WT / NBR_PKGS, 2) AS SHUTOUTPKG_WT, ");
				sb.append("	ROUND(SHUTOUT_PKG*ESN_VOL / NBR_PKGS, 2) AS SHUTOUTPKG_VOL, ");
				sb.append("	ROUND(OUTSTANDING_PKG*ESN_WT / NBR_PKGS, 2) AS OUTSTANDING_WT, ");
				sb.append("	ROUND(OUTSTANDING_PKG*ESN_VOL / NBR_PKGS, 2) AS OUTSTANDING_VOL ");
				sb.append("FROM ");
				sb.append("	( ");
				sb.append("	SELECT ");
				sb.append(
						"		VC.VV_CD, VC.VSL_NM, VC.OUT_VOY_NBR, ESN.ESN_ASN_NBR, BKD.CARGO_TYPE, CGR.CRG_TYPE_NM, TESN.HS_CD AS ESN_HS_CODE, TESN.CRG_DES, ESNM.MARKINGS, TESN.DG_IND AS ESN_DG_IND, TESN.STORAGE_IND AS STG_IND, TESN.PKG_TYPE, PKG.PKG_DESC, TESN.NBR_PKGS, TESN.GROSS_WT AS ESN_WT, TESN.GROSS_VOL AS ESN_VOL, TESN.UA_NBR_PKGS AS ESN_PKG, ");
				// sb.append(" TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED AS SHUTOUT_PKG,
				// TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS AS
				// OUTSTANDING_PKG, ");
				sb.append(
						" NVL(BKD.SHUTOUT_QTY, 0) AS SHUTOUT_PKG, NVL(BKD.SHUTOUT_QTY, 0) - BKD.SHUTOUT_DELIVERY_PKGS AS OUTSTANDING_PKG, EDO.ADP_IC_TDBCR_NBR, EDO.ADP_NM, EDO.NBR_PKGS AS EDO_PKGS, EDO.NOM_WT, EDO.NOM_VOL, EDO.ACCT_NBR, EDO.EDO_DELIVERY_TO, EDO.WH_IND, EDO.WH_AGGR_NBR, EDO.WH_REMARKS, EDO.EDO_ASN_NBR, EDO.FREE_STG_DAYS ");
				sb.append("	FROM ");
				sb.append(
						"		VESSEL_CALL VC, ESN ESN, BK_DETAILS BKD, TESN_PSA_JP TESN, ESN_MARKINGS ESNM, GB_EDO EDO, CRG_TYPE CGR, PKG_TYPES PKG ");
				sb.append("	WHERE ");
				sb.append("		VC.VV_CD = BKD.VAR_NBR ");
				sb.append("		AND CGR.CRG_TYPE_CD = BKD.CARGO_TYPE ");
				sb.append("		AND BKD.BK_REF_NBR = ESN.BK_REF_NBR ");
				sb.append("		AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("		AND EDO.EDO_STATUS = 'A' ");
				sb.append("		AND TESN.PKG_TYPE = PKG.PKG_TYPE_CD ");
				sb.append("		AND ESNM.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("		AND EDO.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("		AND EDO.EDO_ASN_NBR =:edo_asn_nbr )");
			} else if (StringUtils.equalsIgnoreCase(trans_type, "A")) {
				sb.setLength(0);
				sb.append("SELECT ");
				sb.append("	VV_CD, ");
				sb.append("	VSL_NM, ");
				sb.append("	OUT_VOY_NBR, ");
				sb.append("	ESN_ASN_NBR, ");
				sb.append("	(CARGO_TYPE || '--' || CRG_TYPE_NM) CARGO_TYPE, ");
				sb.append("	ESN_HS_CODE, ");
				sb.append("	CRG_DES, ");
				sb.append("	MARKINGS, ");
				sb.append("	DECODE(ESN_DG_IND, 'N', 'No', 'Y', 'Yes') ESN_DG_IND, ");
				sb.append("	DECODE(STG_IND, 'O', 'Open', 'C', 'Covered') STG_IND, ");
				sb.append("	FREE_STG_DAYS, ");
				sb.append("	PKG_TYPE, ");
				sb.append("	PKG_DESC, ");
				sb.append("	ESN_PKG, ");
				sb.append("	NVL(SHUTOUT_PKG, 0) SHUTOUT_PKG, ");
				sb.append("	NVL(OUTSTANDING_PKG, 0 ) OUTSTANDING_PKG, ");
				sb.append("	ADP_IC_TDBCR_NBR, ");
				sb.append("	ADP_NM, ");
				sb.append("	EDO_PKGS, ");
				sb.append("	NOM_WT, ");
				sb.append("	NOM_VOL, ");
				sb.append("	ACCT_NBR, ");
				sb.append("	EDO_DELIVERY_TO, ");
				sb.append("	WH_IND, ");
				sb.append("	WH_AGGR_NBR, ");
				sb.append("	WH_REMARKS, ");
				sb.append("	EDO_ASN_NBR, ");
				sb.append("	ROUND(ESN_PKG*ESN_WT / NBR_PKGS, 2) AS ESNPKG_WT, ");
				sb.append("	ROUND(ESN_PKG*ESN_VOL / NBR_PKGS, 2) AS ESNPKG_VOL, ");
				sb.append("	ROUND(SHUTOUT_PKG*ESN_WT / NBR_PKGS, 2) AS SHUTOUTPKG_WT, ");
				sb.append("	ROUND(SHUTOUT_PKG*ESN_VOL / NBR_PKGS, 2) AS SHUTOUTPKG_VOL, ");
				sb.append("	ROUND(OUTSTANDING_PKG*ESN_WT / NBR_PKGS, 2) AS OUTSTANDING_WT, ");
				sb.append("	ROUND(OUTSTANDING_PKG*ESN_VOL / NBR_PKGS, 2) AS OUTSTANDING_VOL ");
				sb.append("FROM ");
				sb.append("	( ");
				sb.append("	SELECT ");
				sb.append(
						"		VC.VV_CD, VC.VSL_NM, VC.OUT_VOY_NBR, ESN.ESN_ASN_NBR, BKD.CARGO_TYPE, CGR.CRG_TYPE_NM, '' ESN_HS_CODE, '' CRG_DES, '' MARKINGS, '' AS ESN_DG_IND, '' STG_IND, '' PKG_TYPE, '' PKG_DESC, TESN.NBR_PKGS, TESN.NOM_WT AS ESN_WT, TESN.NOM_VOL AS ESN_VOL, TESN.UA_NBR_PKGS AS ESN_PKG, ");
				// sb.append(" TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED AS SHUTOUT_PKG,
				// TESN.UA_NBR_PKGS - BKD.ACTUAL_NBR_SHIPPED - BKD.SHUTOUT_DELIVERY_PKGS AS
				// OUTSTANDING_PKG, ");
				sb.append(
						" NVL(BKD.SHUTOUT_QTY, 0) AS SHUTOUT_PKG, NVL(BKD.SHUTOUT_QTY, 0) - BKD.SHUTOUT_DELIVERY_PKGS AS OUTSTANDING_PKG, EDO.ADP_IC_TDBCR_NBR, EDO.ADP_NM, EDO.NBR_PKGS AS EDO_PKGS, EDO.NOM_WT, EDO.NOM_VOL, EDO.ACCT_NBR, EDO.EDO_DELIVERY_TO, EDO.WH_IND, EDO.WH_AGGR_NBR, EDO.WH_REMARKS, EDO.EDO_ASN_NBR, EDO.FREE_STG_DAYS ");
				sb.append("	FROM ");
				sb.append("		VESSEL_CALL VC, ESN ESN, BK_DETAILS BKD, TESN_JP_JP TESN, GB_EDO EDO, CRG_TYPE CGR ");
				sb.append("	WHERE ");
				sb.append("		VC.VV_CD = BKD.VAR_NBR ");
				sb.append("		AND CGR.CRG_TYPE_CD = BKD.CARGO_TYPE ");
				sb.append("		AND BKD.BK_REF_NBR = ESN.BK_REF_NBR ");
				sb.append("		AND TESN.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("		AND EDO.EDO_STATUS = 'A' ");
				sb.append("		AND EDO.ESN_ASN_NBR = ESN.ESN_ASN_NBR ");
				sb.append("		AND EDO.EDO_ASN_NBR =:edo_asn_nbr )");
			}
			sql = sb.toString();
			param.put("edo_asn_nbr", edo_asn_nbr);
			log.info("loadGeneralShutoutCargoByEDO SQL" + sql + ",ParamMap" + param.toString());
			dpeCargo = namedParameterJdbcTemplate.queryForObject(sql, param,
					new BeanPropertyRowMapper<DPECargo>(DPECargo.class));
			log.info("loadGeneralShutoutCargoByEDO Result" + dpeCargo.toString());

		
		} catch (Exception e) {
			log.info("Exception loadGeneralShutoutCargoByEDO : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO loadGeneralShutoutCargoByEDO");
		}

		return dpeCargo;
	}

	@Override
	public String updateShutoutEdo(EdoValueObjectCargo edo, String userId) throws BusinessException {
		String sql = "";
		String insertSql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String transNbr = "";

		StringBuffer sb = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();
		try {
			log.info("START: updateShutoutEdo  DAO  Start  " + "edo" + edo + "userId" + userId);
			sql = "SELECT MAX(TRANS_NBR) AS transNbr FROM GB_EDO_TRANS WHERE EDO_ASN_NBR= :edoAsnbr";

			paramMap.put("edoAsnbr", edo.getEdoAsnNbr());
			log.info("updateShutoutEdo SQL" + sql);
			log.info(" *** updateShutoutEdo params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				transNbr = CommonUtility.deNull(rs.getString("transNbr"));
			}
			if(!transNbr.isEmpty() && transNbr != null) {
				log.info("transNbr" + transNbr.toString());
			} else {
				transNbr = "0";
			}
			transNbr = String.valueOf(Integer.parseInt(transNbr) + 1);
			sb.append("UPDATE ");
			sb.append("	GB_EDO ");
			sb.append("SET ");
			sb.append("	VAR_NBR = :varNbr, ");
			sb.append("	NBR_PKGS = :edoNbrPkgs, ");
			sb.append("	NOM_WT = :nomWt , ");
			sb.append("	NOM_VOL = :nomVol , ");
			sb.append("	LAST_MODIFY_USER_ID = :userId, ");
			sb.append("	LAST_MODIFY_DTTM =( ");
			sb.append("	SELECT ");
			sb.append("		SYSDATE ");
			sb.append("	FROM ");
			sb.append("		DUAL), ");
			sb.append("	ESN_ASN_NBR = :esnAsnNbr, ");
			sb.append("	SHUTOUT_IND = 'Y', ");
			sb.append("	PAYMENT_MODE = :pkgTypeCd, ");
			sb.append("	ACCT_NBR = :acctNbr, ");
			sb.append("	EDO_CREATE_CD =:consNm, ");
			sb.append("	EDO_DELIVERY_TO = :deliveryTo, ");
			sb.append("	ADP_IC_TDBCR_NBR = :adpNbr, ");
			sb.append("	ADP_NM = :adpNm, ");
			sb.append("	ADP_CUST_CD = :adpCustCd ");
			sb.append("WHERE ");
			sb.append("	EDO_ASN_NBR =:edoAsnbr");

			insertSql = sb.toString();
			sb1.append("INSERT ");
			sb1.append("	INTO ");
			sb1.append(
					"	GB_EDO_TRANS (EDO_ASN_NBR, TRANS_NBR, VAR_NBR, NBR_PKGS, NOM_WT, NOM_VOL, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, ESN_ASN_NBR, SHUTOUT_IND, PAYMENT_MODE, ACCT_NBR, EDO_CREATE_CD, EDO_DELIVERY_TO, ADP_IC_TDBCR_NBR, ADP_NM, ADP_CUST_CD) ");
			sb1.append("VALUES(:edoAsnbr,:transNbr,:varNbr,:edoNbrPkgs,:nomWt,:nomVol,:userId,( ");
			sb1.append("SELECT ");
			sb1.append("	SYSDATE ");
			sb1.append("FROM ");
			sb1.append("	DUAL),:esnAsnNbr, 'Y',:pkgTypeCd,:acctNbr,:consNm,:deliveryTo,:adpNbr,:adpNm,:adpCustCd)");

			String insertTransSql = sb1.toString();

			paramMap.put("varNbr", edo.getVarNbr());
			paramMap.put("edoNbrPkgs", Double.parseDouble(edo.getEdoNbrPkgs()));
			paramMap.put("nomWt", Double.parseDouble(edo.getNomWeight()));
			paramMap.put("nomVol", Double.parseDouble(edo.getNomVolume()));
			paramMap.put("userId", userId);
			paramMap.put("esnAsnNbr", Integer.parseInt(edo.getEsnAsnNbr()));
			paramMap.put("pkgTypeCd", edo.getPkgTypeCd());
			paramMap.put("acctNbr", edo.getAcctNbr());
			paramMap.put("consNm", edo.getConsNm());
			paramMap.put("deliveryTo", edo.getDeliveryTo());
			paramMap.put("adpNbr", edo.getAdpNbr());
			paramMap.put("adpNm", edo.getAdpNm());
			paramMap.put("adpCustCd", edo.getAdpCustCd());
			paramMap.put("edoAsnbr", edo.getEdoAsnNbr());
			log.info("updateShutoutEdo update SQL" + insertSql);
			log.info(" *** updateShutoutEdo params *****" + paramMap.toString());
			namedParameterJdbcTemplate.update(insertSql, paramMap);

			paramMap.put("transNbr", transNbr);
			paramMap.put("varNbr", edo.getVarNbr());
			paramMap.put("edoNbrPkgs", Double.parseDouble(edo.getEdoNbrPkgs()));
			paramMap.put("nomWt", Double.parseDouble(edo.getNomWeight()));
			paramMap.put("nomVol", Double.parseDouble(edo.getNomVolume()));
			paramMap.put("userId", userId);
			paramMap.put("esnAsnNbr", Integer.parseInt(edo.getEsnAsnNbr()));
			paramMap.put("pkgTypeCd", edo.getPkgTypeCd());
			paramMap.put("acctNbr", edo.getAcctNbr());
			paramMap.put("consNm", edo.getConsNm());
			paramMap.put("deliveryTo", edo.getDeliveryTo());
			paramMap.put("adpNbr", edo.getAdpNbr());
			paramMap.put("adpNm", edo.getAdpNm());
			paramMap.put("adpCustCd", edo.getAdpCustCd());
			paramMap.put("edoAsnbr", edo.getEdoAsnNbr());
			log.info("updateShutoutEdo insert SQL" + insertTransSql);
			log.info(" *** updateShutoutEdo params *****" + paramMap.toString());
			namedParameterJdbcTemplate.update(insertTransSql, paramMap);

		
		} catch (NullPointerException ne) {
			log.info("Exception updateShutoutEdo : ", ne);
			throw new BusinessException("M1004");
		} catch (Exception e) {
			log.info("Exception updateShutoutEdo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateShutoutEdo  DAO  END");
		}
		return transNbr;
	}

	// ejb.sessionBeans.gbms.cargo.edo --> EdoEjb --> insertShutoutEdoForDPE()
	@Override
	public String insertShutoutEdoForDPE(EdoValueObjectCargo edo, String userId) throws BusinessException {

		SqlRowSet rsDate = null;
		SqlRowSet rs = null;
		SqlRowSet rsasn = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String stredoasnnbr = "";
		String strsqldate = "";
		int update = 0;
		StringBuffer sb = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();
		try {

			log.info("START: insertShutoutEdoForDPE  DAO  Start  " + "edo: " + edo + "userId: " + userId);
			String sql = "SELECT MAX(EDO_ASN_NBR) FROM GB_EDO";
			String sqldate = "SELECT TO_CHAR(SYSDATE,'Y/MM/DD') AS STRDATE FROM DUAL";


			log.info("insertShutoutEdoForDPE Date SQL" + sqldate);
			rsDate = namedParameterJdbcTemplate.queryForRowSet(sqldate, paramMap);

			while (rsDate.next()) {
				strsqldate = CommonUtility.deNull(rsDate.getString("STRDATE"));
			}

			String strsqlyy = strsqldate.substring(0, 1);
			String strsqlmm = strsqldate.substring(2, 4);

			if ((strsqlyy + strsqlmm.substring(0, 1)).equals("00")
					|| (strsqlyy + strsqlmm.substring(0, 1)).equals("01")) {
				sql = "SELECT MAX(EDO_ASN_NBR) FROM GB_EDO WHERE EDO_ASN_NBR < 1300000";
			} else {

				sql = "SELECT MAX(EDO_ASN_NBR) FROM GB_EDO WHERE EDO_ASN_NBR BETWEEN (SELECT TO_NUMBER(TO_CHAR(SYSDATE,'Y')) * 10000000 FROM DUAL) AND (SELECT ((TO_NUMBER(TO_CHAR(SYSDATE,'Y'))+1) * 10000000)-1 FROM DUAL)";

			}
			
			log.info("insertShutoutEdoForDPE Date SQL " + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				stredoasnnbr = CommonUtility.deNull(rs.getString("MAX(EDO_ASN_NBR)"));
			}

			if (stredoasnnbr.equalsIgnoreCase("")) {
				stredoasnnbr = "00100001";
			}
			if (stredoasnnbr.length() == 7) {
				stredoasnnbr = "0".concat(stredoasnnbr);
			}
			if (stredoasnnbr.length() == 6) {
				stredoasnnbr = "00".concat(stredoasnnbr);
			}
			if (stredoasnnbr.length() == 5) {
				stredoasnnbr = "000".concat(stredoasnnbr);
			}
			int intedoasnnbr = Integer.parseInt(stredoasnnbr.substring(3, 8));
			String stredoasnnbryy = stredoasnnbr.substring(0, 1);
			String stredoasnnbrmm = stredoasnnbr.substring(1, 3);

			if ((stredoasnnbryy.equalsIgnoreCase(strsqlyy)) && (stredoasnnbrmm.equalsIgnoreCase(strsqlmm))) {
				stredoasnnbr = (stredoasnnbryy).concat(stredoasnnbrmm);
				intedoasnnbr = intedoasnnbr + 2;
				//	String strtempnbr = Integer.toString(intedoasnnbr);

				// Added by Babatunde on Jan., 2014 : START
				boolean isValid = false;
				String randomAsnNbr = null;
				String dbAsnNbr = null;
				String sqlasn;

				ArrayList<String> asnNbrs;

				while (!isValid) {
					asnNbrs = new ArrayList<String>();

					for (int i = 0; i <= 19; i++) {
						randomAsnNbr = stredoasnnbr.concat(CommonUtility.generateRandomNumber(5, false));
						asnNbrs.add(randomAsnNbr);
					}

					String asnStr = "'";
					for (int i = 0; i <= asnNbrs.size() - 1; i++) {

						if (i <= 18) {
							asnStr = asnStr + asnNbrs.get(i) + "', '";
						}

						if (i == 19) {
							asnStr = asnStr + asnNbrs.get(1) + "'";
						}

					}
					
					sqlasn = "select EDO_ASN_NBR from GB_EDO where EDO_ASN_NBR in (:asnStr)";
					ArrayList<String> existAsnNbrs = new ArrayList<String>();

					MapSqlParameterSource parameters = new MapSqlParameterSource();
					parameters.addValue("asnStr", Arrays.asList(asnStr.replaceAll("'", "").trim().split(",")));

					log.info("insertShutoutEdoForDPE SQL" + sqlasn + ", param:" + parameters.getValues() );
					rsasn = namedParameterJdbcTemplate.queryForRowSet(sqlasn, parameters);
					while (rsasn.next()) {
						dbAsnNbr = CommonUtility.deNull(rsasn.getString("EDO_ASN_NBR"));
						existAsnNbrs.add(dbAsnNbr);
						log.info("Resultset = " + dbAsnNbr);
					}
					asnNbrs.removeAll(existAsnNbrs);

					if (asnNbrs.size() > 0) {
						stredoasnnbr = asnNbrs.get(0);
						isValid = true;
					}
				}
				// Added by Babatunde on Jan., 2014 : END

//			        commented by Babatunde on Jan., 2014 : START
//					if (strtempnbr.length() == 1) {
//						stredoasnnbr = stredoasnnbr.concat("0000");
//						stredoasnnbr = stredoasnnbr.concat(strtempnbr);
//					}
//					if (strtempnbr.length() == 2) {
//						stredoasnnbr = stredoasnnbr.concat("000");
//						stredoasnnbr = stredoasnnbr.concat(strtempnbr);
//					}
//					if (strtempnbr.length() == 3) {
//						stredoasnnbr = stredoasnnbr.concat("00");
//						stredoasnnbr = stredoasnnbr.concat(strtempnbr);
//					}
//					if (strtempnbr.length() == 4) {
//						stredoasnnbr = stredoasnnbr.concat("0");
//						stredoasnnbr = stredoasnnbr.concat(strtempnbr);
//					}
//					if (strtempnbr.length() == 5) {
//						stredoasnnbr = stredoasnnbr.concat(strtempnbr);
//					}
//			        commented by Babatunde on Jan., 2014 : END
			} else {
				stredoasnnbr = (strsqlyy).concat(strsqlmm);
				stredoasnnbr = stredoasnnbr.concat("00002"); //00002
			}
			sb.append("INSERT ");
			sb.append("	INTO ");
			sb.append(
					"	GB_EDO (EDO_ASN_NBR, VAR_NBR, NBR_PKGS, NOM_WT, NOM_VOL, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, ESN_ASN_NBR, SHUTOUT_IND, PAYMENT_MODE, ACCT_NBR, EDO_CREATE_CD, EDO_DELIVERY_TO, ADP_IC_TDBCR_NBR, ADP_NM, CRG_STATUS, ADP_CUST_CD, ADP_NBR_PKGS ) ");
			sb.append("VALUES(:edoAsnbr,:varNbr,:edoNbrPkgs,:nomWt,:nomVol,:userId,( ");
			sb.append("SELECT ");
			sb.append("	SYSDATE ");
			sb.append("FROM ");
			sb.append(
					"	DUAL),:esnAsnNbr, 'Y',:pkgTypeCd,:acctNbr,:consNm,:deliveryTo,:adpNbr,:adpNm, 'L',:adpCustCd,:edoNbrPkgs)");
			String insertSql = sb.toString();

			sb1.append("INSERT ");
			sb1.append("	INTO ");
			sb1.append(
					"	GB_EDO_TRANS (EDO_ASN_NBR, TRANS_NBR, VAR_NBR, NBR_PKGS, NOM_WT, NOM_VOL, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, ESN_ASN_NBR, SHUTOUT_IND, PAYMENT_MODE, ACCT_NBR, EDO_CREATE_CD, EDO_DELIVERY_TO, ADP_IC_TDBCR_NBR, ADP_NM, CRG_STATUS, ADP_CUST_CD, ADP_NBR_PKGS) ");
			sb1.append("VALUES(:edoAsnbr, 0,:varNbr,:edoNbrPkgs,:nomWt,:nomVol,:userId,( ");
			sb1.append("SELECT ");
			sb1.append("	SYSDATE ");
			sb1.append("FROM ");
			sb1.append(
					"	DUAL),:esnAsnNbr, 'Y',:pkgTypeCd,:acctNbr,:consNm,:deliveryTo,:adpNbr,:adpNm, 'L',:adpCustCd,:edoNbrPkgs)");

			String insertTransSql = sb1.toString();
			paramMap.put("edoAsnbr", Integer.parseInt(stredoasnnbr));
			paramMap.put("varNbr", edo.getVarNbr());
			paramMap.put("edoNbrPkgs", Integer.parseInt(edo.getEdoNbrPkgs()));
			paramMap.put("nomWt", Double.parseDouble(edo.getNomWeight()));
			paramMap.put("nomVol", Double.parseDouble(edo.getNomVolume()));
			paramMap.put("userId", userId);
			paramMap.put("esnAsnNbr", Integer.parseInt(edo.getEsnAsnNbr()));
			paramMap.put("pkgTypeCd", edo.getPkgTypeCd());
			paramMap.put("acctNbr", edo.getAcctNbr());
			paramMap.put("consNm", edo.getConsNm());
			paramMap.put("deliveryTo", edo.getDeliveryTo());
			paramMap.put("adpNbr", edo.getAdpNbr());
			paramMap.put("adpNm", edo.getAdpNm());
			paramMap.put("adpCustCd", edo.getAdpCustCd());

			log.info("insertShutoutEdoForDPE Insert SQL" + insertSql + ",paramMap:" + paramMap.toString());
			update = namedParameterJdbcTemplate.update(insertSql, paramMap);
			log.info("update:" + update);

			paramMap.put("edoAsnbr", Integer.parseInt(stredoasnnbr));
			paramMap.put("varNbr", edo.getVarNbr());
			paramMap.put("edoNbrPkgs", Integer.parseInt(edo.getEdoNbrPkgs()));
			paramMap.put("nomWt", Double.parseDouble(edo.getNomWeight()));
			paramMap.put("nomVol", Double.parseDouble(edo.getNomVolume()));
			paramMap.put("userId", userId);
			paramMap.put("esnAsnNbr", Integer.parseInt(edo.getEsnAsnNbr()));
			paramMap.put("pkgTypeCd", edo.getPkgTypeCd());
			paramMap.put("acctNbr", edo.getAcctNbr());
			paramMap.put("consNm", edo.getConsNm());
			paramMap.put("deliveryTo", edo.getDeliveryTo());
			paramMap.put("adpNbr", edo.getAdpNbr());
			paramMap.put("adpNm", edo.getAdpNm());
			paramMap.put("adpCustCd", edo.getAdpCustCd());
			log.info("insertShutoutEdoForDPE Insert SQL" + insertTransSql + ",paramMap:" + paramMap.toString());
			update = namedParameterJdbcTemplate.update(insertTransSql, paramMap);
			log.info("update1:" + update);
			updateBK(edo, userId);

		} catch (BusinessException be) {
			log.info("Exception insertShutoutEdoForDPE : ", be);
			throw new BusinessException(be.getMessage());
		} catch (NullPointerException ne) {
			log.info("Exception insertShutoutEdoForDPE : ", ne);
			throw new BusinessException("M1004");
		} catch (Exception e) {
			log.info("Exception insertShutoutEdoForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertShutoutEdoForDPE  DAO  END");
		}
		return stredoasnnbr;
	}

	// ejb.sessionBeans.gbms.cargo.edo --> EdoEjb --> updateBK()
	private void updateBK(EdoValueObjectCargo edo, String userId) throws BusinessException {
		int bkupdcnt = 0;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String esnSQL = "";
		String bkupdsql = "";
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: updateBK  DAO  Start  " + "edo" + edo + "userId" + userId);

			int shutoutPkgs = Integer.parseInt(edo.getEdoNbrPkgs());
			String remark = "Shutout EDO";
			String esnNbr = edo.getEsnAsnNbr();
			String bkRefNbr = "";
			esnSQL = "select bk_ref_nbr from esn where ESN_ASN_NBR = :esnNbr";
			paramMap.put("esnNbr", esnNbr);
			log.info("updateBK  SQL" + esnSQL + ",paramMap:" + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(esnSQL, paramMap);

			while (rs.next()) {
				bkRefNbr = CommonUtility.deNull(rs.getString("bk_ref_nbr"));
			}

			if (!"".equalsIgnoreCase(bkRefNbr)) {
				// Added By NS
				//	String shutOutDeliveryPkgsSql = "SELECT NVL(SHUTOUT_DELIVERY_PKGS, 0) AS SHUTOUT_DELIVERY_PKGS FROM  BK_DETAILS WHERE BK_REF_NBR = :bkRefNbr ";
				//	paramMap.put("bkRefNbr", bkRefNbr);
				//	rs = namedParameterJdbcTemplate.queryForRowSet(shutOutDeliveryPkgsSql, paramMap);
				//	while (rs.next()) {
				//	shutoutDeliveryPkgs = rs.getInt("SHUTOUT_DELIVERY_PKGS");
				//	}
				// end added

				sb.append("UPDATE ");
				sb.append("	BK_DETAILS ");
				sb.append(" SET ");
				sb.append("	SHUTOUT_DELIVERY_PKGS = NVL(SHUTOUT_DELIVERY_PKGS,0) + :shutoutPkgs , ");
				sb.append("	SHUTOUT_DELIVERY_REMARKS = :remark, ");
				sb.append("	SHUTOUT_UPDATE_USER_ID = :userId, ");
				sb.append("	SHUTOUT_UPDATE_DTTM = SYSDATE ");
				sb.append("WHERE ");
				sb.append("	BK_REF_NBR = :bkRefNbr");
				bkupdsql = sb.toString();
				paramMap.put("shutoutPkgs", shutoutPkgs);
				paramMap.put("remark", remark);
				paramMap.put("userId", userId);
				paramMap.put("bkRefNbr", bkRefNbr);
				log.info("updateBK  SQL" + bkupdsql);
				bkupdcnt = namedParameterJdbcTemplate.update(bkupdsql, paramMap);
				if ((bkupdcnt == 0)) {
					log.info("EDO.updateBK bkupdsql : Record Cannot be updated to Database");
					throw new BusinessException("M1004");
				}
			}
		} catch (NullPointerException ne) {
			log.info("Exception updateBK : ", ne);
			throw new BusinessException("M1004");
		} catch (BusinessException e) {
			log.info("Exception updateBK : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updateBK : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateBK  DAO  END Result:" + bkupdcnt);

		}
	}
	
	 /**
     *  This method retrieves the information related to the Adhoc Warehouse/Free Storage Period module.
     *  @param  edoasnnbr   EDO ASN No
     *  @return Vector      Warehouse/FSP Information of the EDO
     *  @exception  BusinessException, RemoteException
     */
	@Override
    public List<String> getWHIndicator(String edoasnnbr) throws BusinessException{
        String sql = "";
        sql= "SELECT WH_IND,WH_AGGR_NBR,WH_REMARKS,FREE_STG_DAYS FROM GB_EDO WHERE EDO_ASN_NBR=:edoasnnbr";
        String whInd="";
        String whAggrNbr="";
        String whRemarks="";
        String freeStgDays="";
        List<String> whvector= new ArrayList<String>();
        Map<String,String> paramMap = new HashMap<String,String>();
        try {
        	log.info("START: getWHIndicator  DAO  Start edoasnnbr" + edoasnnbr);
        	paramMap.put("edoasnnbr", edoasnnbr);
        	
        	log.info(" *** getWHIndicator SQL *****" + sql);
			log.info(" *** getWHIndicator params *****" + paramMap.toString());

            SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
            while(rs.next()) {
                whInd =CommonUtility.deNull(rs.getString(1));
                whAggrNbr =CommonUtility.deNull(rs.getString(2));
                whRemarks =CommonUtility.deNull(rs.getString(3));
                freeStgDays =CommonUtility.deNull(rs.getString(4));
            }
            whvector.add(whInd);
            whvector.add(whAggrNbr);
            whvector.add(whRemarks);
            whvector.add(freeStgDays);
            
            log.info("END: *** getWHIndicator Result *****" + whvector);
        }catch (NullPointerException ne) {
			log.info("Exception getWHIndicator : ", ne);
			throw new BusinessException("M1004");
		} catch (Exception e) {
			log.info("Exception getWHIndicator : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWHIndicator  DAO  END");
		}
        return whvector;
    }
	
	@Override
	public EdoValueObjectCargo getUsedWeightVolume(String mftseqnbr) throws BusinessException{
        String sql = "";
        String vol="";
        String wt="";
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT (x.gross_wt - nvl(y.sum1,0)) usedWeight,(x.gross_vol - nvl(y.sum2,0)) usedVolume FROM ");
        sb.append(" (SELECT  A.MFT_SEQ_NBR, A.GROSS_WT,A.GROSS_VOL ");
        sb.append(" FROM MANIFEST_DETAILS A ");
        sb.append(" ) x LEFT OUTER JOIN ");
        sb.append(" (SELECT edo.mft_seq_nbr, SUM(edo.nom_wt) sum1, sum(edo.nom_vol) sum2 FROM GB_EDO edo WHERE edo.EDO_STATUS = 'A'");
        sb.append(" GROUP BY edo.mft_seq_nbr) y");
        sb.append(" ON x.mft_seq_nbr = y.mft_seq_nbr ");
        sb.append(" WHERE x.MFT_SEQ_NBR =:mftseqnbr ");
        sql= sb.toString();
        Map<String,String> paramMap = new HashMap<String,String>();

        EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();

        try {
        	log.info("START: getUsedWeightVolume  DAO  Start mftseqnbr" + mftseqnbr);
        	paramMap.put("mftseqnbr", mftseqnbr);
        	
        	log.info(" *** getUsedWeightVolume SQL *****" + sql);
			log.info(" *** getUsedWeightVolume params *****" + paramMap.toString());

			
            SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
            while(rs.next()) {
                wt =CommonUtility.deNull(rs.getString("usedWeight"));
                vol =CommonUtility.deNull(rs.getString("usedVolume"));

            }
            edoValueObject.setNomWeight(String.valueOf(wt));
            edoValueObject.setNomVolume(String.valueOf(vol));
            
            log.info(" END: *** getUsedWeightVolume result *****" + edoValueObject.toString());
        } catch (Exception e) {
        	log.info("Exception getUsedWeightVolume : ", e);
        	throw new BusinessException("M4201");
        } finally {
			log.info("END: getUsedWeightVolume  DAO  END");
		}
        return edoValueObject;
    }
	
	@Override
	public String insertEdoDetailsForDPE(String mftseqnbr,
			String varnbr,
			String adpnbr,
			String adpnm,
			String adpictdbcrnbr,
			String crgagtnbr,
			String crgagtnm,
			String agtattnbr,
			String agtattnm,
			String newnbrpkgs,
			String deliveryto,
			String jpbnbr,
			String paymode,
			String edostatus,
			String lastmodifyuserid,
			String caictdbcrnbr,
			String aaictdbcrnbr,
			String edocreatecd,
			//++ 19.10.2009 FPT added for GB CR
			String distype,
			String weight,
			String volume,
			//-- 19.10.2009 FPT added for GB CR
			//++ 11.02.2014 FPT added for DPE CR
			List<AdpValueObject> adpList,
			//++ 11.02.2014 FPT added for DPE CR
			//Begin ThanhPT6, CR of JPOnline&SMART Enhancement, 06/01/2016
			String taUenNo,
			String taCCode,
			String taNmByJP,
			//End ThanhPT6, CR of JPOnline&SMART Enhancement, 06/01/2016
			List<HsCodeDetails> multiHsCodeList
			) throws BusinessException{

		//added 'strStfInd' by vani -- 10th Oct,03 ,String strStfInd removed by vinayak 19/12/2003
		String strinsertedoasnnbr="false";
		String blnbr="";
		String mftCrgStatus= "L";
		String edoCrgStatus= "L";
		String strtransnbr="0";
		boolean chkAccountNbr=true;
		Map<String,Object> paramMap = new HashMap<String,Object>();
		if (!(jpbnbr.equalsIgnoreCase("cash"))) {
			chkAccountNbr=checkAccountNbr(jpbnbr);
		}
		if (!chkAccountNbr) {
			log.info("Writing from Writing from EdoEjb.insertEdoDetailsForDPE");
			log.info("Invalid Account Number " + jpbnbr);
			throw new BusinessException("M20801");
		}
		String sql = "";
		String sql1 = "";
		String sqldate="";
		String strUpdate = new String();
		String strUpdate1 = new String();
		String strUpdatetrans = new String();
		String strUpdatetrans1 = new String();

		sql= "SELECT MAX(EDO_ASN_NBR) FROM GB_EDO ";
		sqldate="SELECT TO_CHAR(SYSDATE,'Y/MM/DD') AS STRDATE FROM DUAL";
		sql1= "SELECT BL_NBR, CRG_STATUS FROM MANIFEST_DETAILS WHERE  MFT_SEQ_NBR=:mftseqnbr";

		String stredoasnnbr="";
		String strsqldate="";
		String releasenbrpkgs="0";

		//Added by VietNguyen
		String adpNbrPkgs = null;
		String adpContactNbr = "";
		if (adpList != null && adpList.size() > 0) {
			AdpValueObject adp = adpList.get(0);
			if (adp != null) {
				adpnbr = adp.getAdpCustCd();
				adpictdbcrnbr = adp.getAdpIcTdbcrNbr();
				adpnm = adp.getAdpNm();
				adpNbrPkgs = adp.getAdpNbrPkgs();
				adpContactNbr = adp.getAdpContact();
			}
		}


		try {
			log.info("START: insertEdoDetailsForDPE  DAO  START" + " mftseqnbr:" + mftseqnbr + " varnbr:" + varnbr 
					+ " adpnbr:" + adpnbr + " adpnm:" + adpnm + " adpictdbcrnbr:" + adpictdbcrnbr
					+ " crgagtnbr:" + crgagtnbr + " crgagtnm:" + crgagtnm + " agtattnbr:"+ agtattnbr
					+ " agtattnm:" + agtattnm + " newnbrpkgs:" + newnbrpkgs + " deliveryto:" + deliveryto
					+ " jpbnbr:"+ jpbnbr + " paymode:" + paymode + " edostatus:" + edostatus 
					+ " lastmodifyuserid:" + lastmodifyuserid + " caictdbcrnbr:" + caictdbcrnbr 
					+ " aaictdbcrnbr:" + aaictdbcrnbr + " edocreatecd:" + edocreatecd + " distype:" + distype 
					+ " weight:" + weight+ " volume:" + volume + " taUenNo:" + taUenNo + " taCCode:" + taCCode );
			//Calculating  Nominated weight and Nominated volume
			EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
			edoValueObject =getNominatedWeightVolume(mftseqnbr, newnbrpkgs) ;
			String nomvolume=edoValueObject.getNomVolume();
			String nomweight=edoValueObject.getNomWeight();
			log.info("nomvolume" + nomvolume.toString());
			log.info("nomweight" + nomweight.toString());
			//End of Nominated weight and Nominated volume Calculation

			log.info(" *** insertEdoDetailsForDPE SQL *****" + sql1);
			log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());

			paramMap.put("mftseqnbr",mftseqnbr);
			SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
			while(rs1.next()) {
				blnbr =CommonUtility.deNull(rs1.getString(1));
				mftCrgStatus=CommonUtility.deNull(rs1.getString(2));
			}

			//Checking for Apostrophe
			adpnm=GbmsCommonUtility.addApostr(adpnm);
			crgagtnm=GbmsCommonUtility.addApostr(crgagtnm);
			agtattnm=GbmsCommonUtility.addApostr(agtattnm);

			// selecting edocreatecd
			if (edocreatecd.equalsIgnoreCase("jp")) {
				//String sqljp="SELECT nvl(DECLARANT_CUST_CD,CREATE_CUST_CD) FROM VESSEL_CALL WHERE VV_CD='"+varnbr+"'";
				//changed by vietnd02 - assign for vessel operator
				//String sqljp="SELECT CREATE_CUST_CD FROM VESSEL_CALL WHERE VV_CD='"+varnbr+"'"; // Changed based on Lukman's comment
				String sqljp="SELECT MANIFEST_CREATE_CD FROM MANIFEST_DETAILS WHERE MFT_SEQ_NBR=:mftseqnbr";

				SqlRowSet rsjp = namedParameterJdbcTemplate.queryForRowSet(sqljp, paramMap);
				while(rsjp.next()) {
					edocreatecd =CommonUtility.deNull(rsjp.getString(1));
				}
			}

			//TO INSERT RELEASE_NBR_PKGS
			if (!(agtattnm.equalsIgnoreCase(""))) {
				releasenbrpkgs=newnbrpkgs;
			}

			if (mftCrgStatus.equalsIgnoreCase("T")) {
				edoCrgStatus="T";
			}

			if (mftCrgStatus.equalsIgnoreCase("L")) {
				edoCrgStatus="L";
			}

			if (mftCrgStatus.equalsIgnoreCase("R")) {
				edoCrgStatus="L";
				mftCrgStatus="L";
			}

			// To check close bj
			String closeBjInd ="N";
			String sqlclbj = "SELECT GB_CLOSE_BJ_IND FROM VESSEL_CALL WHERE VV_CD=:varnbr";
			paramMap.put("varnbr", varnbr);
			
			log.info(" *** insertEdoDetailsForDPE SQL *****" + sqlclbj);
			log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());
			
			SqlRowSet rsclbj = namedParameterJdbcTemplate.queryForRowSet(sqlclbj, paramMap);
			while(rsclbj.next()) {
				closeBjInd =CommonUtility.deNull(rsclbj.getString(1));
			}
			log.info("closeBjInd" + closeBjInd.toString());
			/*
			if (!(closeBjInd.equalsIgnoreCase("N"))) {
			throw new BusinessException("M21605");
			}
			 */
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				String sqltransnbr="SELECT MAX(TRANS_NBR) FROM MANIFEST_DETAILS_TRANS WHERE"+" MFT_SEQ_NBR=:mftseqnbr";
				
				log.info(" *** insertEdoDetailsForDPE SQL *****" + sqltransnbr);
				log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());
				
				SqlRowSet rstransnbr = namedParameterJdbcTemplate.queryForRowSet(sqltransnbr, paramMap);
				while(rstransnbr.next()) {
					strtransnbr =CommonUtility.deNull(rstransnbr.getString(1));
				}
				if (strtransnbr.equalsIgnoreCase("")|| strtransnbr == null) {
					strtransnbr="0";
				}
				else{
					strtransnbr=String.valueOf(Integer.parseInt(strtransnbr)+1);
				}
			}
			
			log.info(" *** insertEdoDetailsForDPE SQL *****" + sqldate);
			log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());
			
			SqlRowSet rsdate = namedParameterJdbcTemplate.queryForRowSet(sqldate, paramMap);
			while(rsdate.next()) {
				strsqldate =CommonUtility.deNull(rsdate.getString(1));
			}
			log.info(" strsqldate: " + strsqldate);
			String strsqlyy=strsqldate.substring(0,1);
			String strsqlmm=strsqldate.substring(2,4);

			log.info(" strsqlyy: " + strsqlyy);
			log.info(" strsqlmm: " + strsqlmm);
			
			if((strsqlyy+strsqlmm.substring(0,1)).equals("00")// Bhuvana 15/09/2010
					||(strsqlyy+strsqlmm.substring(0,1)).equals("01")) { // For year ends with 0.  ie. 2010, 2020, etc.
				sql= "SELECT MAX(EDO_ASN_NBR) FROM GB_EDO WHERE EDO_ASN_NBR < 1300000";
			} else {
				//sql= "SELECT MAX(EDO_ASN_NBR) FROM GB_EDO ";
				// eg. For 2011: Retrieve the max EDO No between EDO No 10000001 and 19999999.
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT MAX(EDO_ASN_NBR) FROM GB_EDO WHERE EDO_ASN_NBR BETWEEN (SELECT TO_NUMBER(TO_CHAR(SYSDATE,'Y')) * 10000000 FROM DUAL) ");
				sb.append("AND (SELECT ((TO_NUMBER(TO_CHAR(SYSDATE,'Y'))+1) * 10000000)-1 FROM DUAL)");
				sql = sb.toString();
			}

			log.info(" *** insertEdoDetailsForDPE SQL *****" + sql);
			log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());
			
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while(rs.next()) {
				stredoasnnbr =CommonUtility.deNull(rs.getString(1));
			}
			log.info(" stredoasnnbr:  " + stredoasnnbr);
			/*Statement sqlstmtdate = con.createStatement();
			ResultSet rsdate = sqlstmtdate.executeQuery(sqldate);
			while(rsdate.next()) {
			strsqldate =CommonUtility.deNull(rsdate.getString(1));
			}
			rsdate.close();
			sqlstmtdate.close();*/
			if (stredoasnnbr.equalsIgnoreCase("")) {
				//stredoasnnbr="00000001";
				stredoasnnbr = "00100001";
			}
			if (stredoasnnbr.length()==7) {
				stredoasnnbr="0".concat(stredoasnnbr);
			}
			if (stredoasnnbr.length()==6) {
				stredoasnnbr="00".concat(stredoasnnbr);
			}
			if (stredoasnnbr.length()==5) {
				stredoasnnbr="000".concat(stredoasnnbr);
			}

			int intedoasnnbr= Integer.parseInt(stredoasnnbr.substring(3,8));
			log.info(" intedoasnnbr: " + intedoasnnbr);
			String stredoasnnbryy=stredoasnnbr.substring(0,1);
			String stredoasnnbrmm=stredoasnnbr.substring(1,3);
			
			log.info(" stredoasnnbryy: " + stredoasnnbryy);
			log.info(" stredoasnnbrmm: " + stredoasnnbrmm);
			
			if ((stredoasnnbryy.equalsIgnoreCase(strsqlyy)) && (stredoasnnbrmm.equalsIgnoreCase(strsqlmm))) {
				stredoasnnbr= (stredoasnnbryy).concat(stredoasnnbrmm);
				intedoasnnbr=intedoasnnbr+2;
				String strtempnbr=Integer.toString(intedoasnnbr);
				log.info("strtempnbr" + strtempnbr.toString());

				//Added by Babatunde on Jan., 2014 : START
				boolean isValid = false;
				String randomAsnNbr = null;
				String dbAsnNbr = null;
				String sqlasn;

				ArrayList<String> asnNbrs;

				while (!isValid) {
					asnNbrs = new ArrayList<String>();

					for (int i = 0; i <= 19; i++) {
						randomAsnNbr = stredoasnnbr.concat(DpeCommonUtil.generateRandomNumber(5, false));
						asnNbrs.add(randomAsnNbr);
					}

					String asnStr = "'";
					for (int i = 0; i <= asnNbrs.size()-1; i++) {

						if(i<=18){
							asnStr = asnStr + asnNbrs.get(i) + "', '";
						}

						if(i==19){
							asnStr = asnStr + asnNbrs.get(1) + "'";
						}
						
					}
					sqlasn = "select EDO_ASN_NBR from gbms.GB_EDO where EDO_ASN_NBR in (:asnStr)";
					ArrayList<String> existAsnNbrs = new ArrayList<String>();
  
					MapSqlParameterSource parameters = new MapSqlParameterSource();
					parameters.addValue("asnStr", Arrays.asList(asnStr.replaceAll("'", "").trim().split(",")));

					log.info(" *** insertEdoDetailsForDPE SQL *****" + sqlasn);
					log.info(" *** insertEdoDetailsForDPE params *****" + parameters.toString());
					
   					SqlRowSet rsasn =  namedParameterJdbcTemplate.queryForRowSet(sqlasn,parameters);
					while (rsasn.next()) {
						dbAsnNbr = CommonUtility.deNull(rsasn.getString(1));
						//Wanyi added on Jan 2020 to cater for 6 or 7 digit ASN numbers
						if (dbAsnNbr.length() == 6){
							dbAsnNbr = "00" + dbAsnNbr;
						}else if (dbAsnNbr.length() == 7){
							dbAsnNbr = "0" + dbAsnNbr;
						}
						existAsnNbrs.add(dbAsnNbr);
					}
					log.info("insertEdoDetailsForDPE: asnNbrs before removeAll() = " + asnNbrs); //Wanyi added
					log.info("insertEdoDetailsForDPE: existAsnNbrs to be remove = " + existAsnNbrs); //Wanyi added
					asnNbrs.removeAll(existAsnNbrs);
					log.info("insertEdoDetailsForDPE: asnNbrs after removeAll() = " + asnNbrs); //Wanyi added

					if (asnNbrs.size() > 0) {
						stredoasnnbr = asnNbrs.get(0);
						isValid = true;
					}
				}
				//Added by Babatunde on Jan., 2014 : END

				//commented out by Babatunde on jan., 2014
				//if (strtempnbr.length() ==1){
				//stredoasnnbr=stredoasnnbr.concat("0000");
				//stredoasnnbr=stredoasnnbr.concat(strtempnbr);
				//}
				//if (strtempnbr.length() ==2){
				//stredoasnnbr=stredoasnnbr.concat("000");
				//stredoasnnbr=stredoasnnbr.concat(strtempnbr);
				//}
				//if (strtempnbr.length() ==3){
				//stredoasnnbr=stredoasnnbr.concat("00");
				//stredoasnnbr=stredoasnnbr.concat(strtempnbr);
				//}
				//if (strtempnbr.length() ==4){
				//stredoasnnbr=stredoasnnbr.concat("0");
				//stredoasnnbr=stredoasnnbr.concat(strtempnbr);
				//}
				//if (strtempnbr.length() ==5){
				//stredoasnnbr=stredoasnnbr.concat(strtempnbr);
				//}
				//commented out by Babatunde on jan., 2014
			}
			else {
				stredoasnnbr= (strsqlyy).concat(strsqlmm);
				log.info("else stredoasnnbr:  " + stredoasnnbr);
				stredoasnnbr=stredoasnnbr.concat("00001");
				String sqlRamdom="SELECT TRUNC(2*(ROUND(dbms_random.value(199,299)/2,0))) FROM dual "; // NS Update the SQL on AUG 2024 to cater for flaw creating even number edo number
				
				log.info(" *** insertEdoDetailsForDPE SQL *****" + sqlRamdom);
				log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());
				SqlRowSet rsasn =  namedParameterJdbcTemplate.queryForRowSet(sqlRamdom, paramMap);
				Integer esnNub =0;
				while (rsasn.next()) {
					esnNub =(rsasn.getInt(1));
				}
				Integer esnNm=Integer.parseInt(stredoasnnbr);
				log.info("else esnNm:  " + esnNm);
				log.info("else esnNub:  " + esnNub);
				esnNm=esnNm+esnNub;
				stredoasnnbr=esnNm.toString();
				log.info("Final stredoasnnbr:  " + stredoasnnbr);
			}
			log.info("New Number generated by new logic "+stredoasnnbr);
			//new number generated

			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO GB_EDO (EDO_ASN_NBR, MFT_SEQ_NBR, VAR_NBR , ");
			sb.append(" BL_NBR, ADP_CUST_CD, ADP_NM, ADP_IC_TDBCR_NBR, ");
			sb.append(" CA_CUST_CD, CA_NM, AA_CUST_CD, AA_NM, NBR_PKGS, ");
			sb.append(" EDO_DELIVERY_TO, ACCT_NBR, PAYMENT_MODE,  ");
			sb.append(" EDO_STATUS, LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,CA_IC_TDBCR_NBR, ");
			sb.append(" AA_IC_TDBCR_NBR,TRANS_NBR_PKGS,TRANS_DN_NBR_PKGS,");
			sb.append(" EDO_CREATE_CD,NOM_VOL,NOM_WT,RELEASE_NBR_PKGS,");
			sb.append(" APPOINTED_ADP_CUST_CD,APPOINTED_ADP_IC_TDBCR_NBR,");
			sb.append(" APPOINTED_ADP_NM, DIS_TYPE, CRG_STATUS, ");
			sb.append(" ADP_NBR_PKGS, ADP_CONTACT_NBR, ");
			sb.append(" TA_CR_UEN_NBR, TA_CUST_CD, TA_NAME) " );
			sb.append(" VALUES (:stredoasnnbr,:mftseqnbr,:varnbr,");
			sb.append(" :blnbr,:adpnbr,:adpnm,:adpictdbcrnbr,");
			sb.append(" :crgagtnbr,:crgagtnm,:agtattnbr,:agtattnm,:newnbrpkgs,");
			sb.append(" :deliveryto,:jpbnbr,:paymode,");
			sb.append(" :edostatus,:lastmodifyuserid,sysdate,:caictdbcrnbr,");
			sb.append(" :aaictdbcrnbr,0,0,");
			sb.append(" :edocreatecd,:volume,:weight,:releasenbrpkgs,");
			sb.append(" :adpnbr,:adpictdbcrnbr,");
			sb.append(" :adpnm,:distype,:edoCrgStatus,");
			sb.append(" :adpNbrPkgs,:adpContactNbr,");
			sb.append(" :taUenNo,:taCCode,:taNmByJP )");
			//MCC add new column EPC_IND
			strUpdate = sb.toString(); 
			log.info("edoEjb.insertEdoDetailsForDPE() qry: "+strUpdate);
			
			paramMap.put("stredoasnnbr", stredoasnnbr);
			paramMap.put("mftseqnbr", mftseqnbr);
			paramMap.put("varnbr", varnbr);
			
			paramMap.put("blnbr", blnbr);
			paramMap.put("adpnbr", adpnbr);
			paramMap.put("adpnm", adpnm);
			paramMap.put("adpictdbcrnbr", adpictdbcrnbr);
			
			paramMap.put("crgagtnbr", crgagtnbr);
			paramMap.put("crgagtnm", crgagtnm);
			paramMap.put("agtattnbr", agtattnbr);
			paramMap.put("agtattnm", agtattnm);
			paramMap.put("newnbrpkgs", newnbrpkgs);

			paramMap.put("deliveryto", deliveryto);
			paramMap.put("jpbnbr", jpbnbr);
			paramMap.put("paymode", paymode);

			paramMap.put("edostatus", edostatus);
			paramMap.put("lastmodifyuserid", lastmodifyuserid);
			paramMap.put("caictdbcrnbr", caictdbcrnbr);
			
			paramMap.put("aaictdbcrnbr", aaictdbcrnbr);
			
			paramMap.put("edocreatecd", edocreatecd);
			paramMap.put("volume", volume);
			paramMap.put("weight", weight);
			paramMap.put("releasenbrpkgs", releasenbrpkgs);

			paramMap.put("adpnbr", adpnbr);
			paramMap.put("adpictdbcrnbr", adpictdbcrnbr);

			paramMap.put("adpnm", adpnm);
			paramMap.put("distype", distype);
			paramMap.put("edoCrgStatus", edoCrgStatus);

			paramMap.put("adpNbrPkgs", adpNbrPkgs);
			paramMap.put("adpContactNbr", adpContactNbr);

			paramMap.put("taUenNo", taUenNo);
			paramMap.put("taCCode", taCCode);
			paramMap.put("taNmByJP", taNmByJP);

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				//MCC add new column EPC_IND
				sb.setLength(0);
				sb.append("INSERT INTO GB_EDO_TRANS ");
				sb.append("(TRANS_NBR, EDO_ASN_NBR, MFT_SEQ_NBR, VAR_NBR , ");
				sb.append(" BL_NBR, ADP_CUST_CD, ADP_NM, ADP_IC_TDBCR_NBR, ");
				sb.append(" CA_CUST_CD, CA_NM, AA_CUST_CD, AA_NM, NBR_PKGS, ");
				sb.append(" EDO_DELIVERY_TO, ACCT_NBR, PAYMENT_MODE,  ");
				sb.append(" EDO_STATUS, LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,CA_IC_TDBCR_NBR, ");
				sb.append(" AA_IC_TDBCR_NBR,TRANS_NBR_PKGS,TRANS_DN_NBR_PKGS,");
				sb.append(" EDO_CREATE_CD,NOM_VOL,NOM_WT,RELEASE_NBR_PKGS,");
				sb.append(" APPOINTED_ADP_CUST_CD,APPOINTED_ADP_IC_TDBCR_NBR,");
				sb.append(" APPOINTED_ADP_NM, DIS_TYPE, CRG_STATUS, ");
				sb.append(" ADP_NBR_PKGS, ADP_CONTACT_NBR) VALUES ");
				sb.append(" ('0',:stredoasnnbr,:mftseqnbr,:varnbr,");
				sb.append(" :blnbr,:adpnbr,:adpnm,:adpictdbcrnbr,");
				sb.append(" :crgagtnbr,:crgagtnm,:agtattnbr,:agtattnm,:newnbrpkgs,");
				sb.append(" :deliveryto,:jpbnbr,:paymode,");
				sb.append(" :edostatus,:lastmodifyuserid,sysdate,:caictdbcrnbr,");
				sb.append(" :aaictdbcrnbr,0,0,");
				sb.append(" :edocreatecd,:volume,:weight,:releasenbrpkgs,");
				sb.append(" :adpnbr,:adpictdbcrnbr,");
				sb.append(" :adpnm,:distype,:edoCrgStatus,");
				sb.append(" :adpNbrPkgs,:adpContactNbr )");
				strUpdatetrans = sb.toString();
				
				paramMap.put("stredoasnnbr", stredoasnnbr);
				paramMap.put("mftseqnbr", mftseqnbr);
				paramMap.put("varnbr", varnbr);
				paramMap.put("blnbr", blnbr);
				paramMap.put("adpnbr", adpnbr);
				paramMap.put("adpnm", adpnm);
				paramMap.put("adpictdbcrnbr", adpictdbcrnbr);
				paramMap.put("crgagtnbr", crgagtnbr);
				paramMap.put("crgagtnm", crgagtnm);
				paramMap.put("agtattnbr", agtattnbr);
				paramMap.put("agtattnm", agtattnm);
				paramMap.put("newnbrpkgs", newnbrpkgs);
				paramMap.put("deliveryto", deliveryto);
				paramMap.put("jpbnbr", jpbnbr);
				paramMap.put("paymode", paymode);
				paramMap.put("edostatus", edostatus);
				paramMap.put("lastmodifyuserid", lastmodifyuserid);
				paramMap.put("caictdbcrnbr", caictdbcrnbr);
				paramMap.put("aaictdbcrnbr", aaictdbcrnbr);
				paramMap.put("edocreatecd", edocreatecd);
				paramMap.put("volume", volume);
				paramMap.put("weight", weight);
				paramMap.put("releasenbrpkgs", releasenbrpkgs);
				paramMap.put("adpnbr", adpnbr);
				paramMap.put("adpictdbcrnbr", adpictdbcrnbr);
				paramMap.put("adpnm", adpnm);
				paramMap.put("distype", distype);
				paramMap.put("edoCrgStatus", edoCrgStatus);
				paramMap.put("adpNbrPkgs", adpNbrPkgs);
				paramMap.put("adpContactNbr", adpContactNbr);

				sb.setLength(0);
				sb.append("INSERT INTO MANIFEST_DETAILS_TRANS");
				sb.append(" (TRANS_NBR , MFT_SEQ_NBR, EDO_NBR_PKGS, CRG_STATUS,");
				sb.append(" LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM,REMARKS) VALUES (");
				sb.append(" :strtransnbr,:mftseqnbr,:newnbrpkgs,:mftCrgStatus,");
				sb.append(" :lastmodifyuserid,sysdate,:remarks)");
				strUpdatetrans1 = sb.toString();

				paramMap.put("strtransnbr", strtransnbr);
				paramMap.put("mftseqnbr", mftseqnbr);
				paramMap.put("newnbrpkgs", newnbrpkgs);
				paramMap.put("mftCrgStatus", mftCrgStatus);
				paramMap.put("lastmodifyuserid", lastmodifyuserid);
				paramMap.put("remarks", "Add "+newnbrpkgs+" With EDO_NBR_PKGS");
			}
			sb.setLength(0);
			sb.append("UPDATE MANIFEST_DETAILS SET EDO_NBR_PKGS=EDO_NBR_PKGS+:newnbrpkgs");
			sb.append(", CRG_STATUS =:mftCrgStatus, LAST_MODIFY_USER_ID =:lastmodifyuserid");
			sb.append(", LAST_MODIFY_DTTM=sysdate WHERE MFT_SEQ_NBR=:mftseqnbr");
			strUpdate1= sb.toString();
			
			paramMap.put("newnbrpkgs", newnbrpkgs);
			paramMap.put("mftCrgStatus", mftCrgStatus);
			paramMap.put("lastmodifyuserid", lastmodifyuserid);
			paramMap.put("mftseqnbr", mftseqnbr);

			log.info(" *** insertEdoDetailsForDPE SQL *****" + strUpdate);
			log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());
			
			log.info(" *** insertEdoDetailsForDPE SQL *****" + strUpdate1);
			log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());
			
			int count = namedParameterJdbcTemplate.update(strUpdate, paramMap);
			int count1= namedParameterJdbcTemplate.update(strUpdate1, paramMap);
			if (count == 0) {
				throw new BusinessException("M20802");
			} else {
				// START CR FTZ HSCODE - NS JULY 2024

				int usedPkg = Integer.valueOf(newnbrpkgs);
				Double usedWt = Double.valueOf(weight);
				Double usedVol = Double.valueOf(volume);
				
				for (HsCodeDetails hsCodeObj : multiHsCodeList) {
					
					// get EDO_HSCODE_SEQ_NBR 
					StringBuilder sbSeq = new StringBuilder();
					sbSeq.append("SELECT GBMS.SEQ_EDO_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
					Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
					BigDecimal seqValue = (BigDecimal) results.get("seqVal");
					log.info("seqValue" + seqValue);
					// end
					
					//JPOM Migration Issue Fix Start
					String customHs = hsCodeObj.getCustomHsCode();
					if (customHs == null || customHs.trim().isEmpty() || customHs.equalsIgnoreCase("NULL")) {
						customHs = "";
					}
					//JPOM Migration Issue Fix End
					
					sb.setLength(0);
					sb.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS  ");
					sb.append(" (MFT_SEQ_NBR,EDO_HSCODE_SEQ_NBR, MFT_HSCODE_SEQ_NBR,EDO_ASN_NBR,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,CUSTOM_HS_CODE,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL) ");
					sb.append(" VALUES(:MFT_SEQ_NBR,:EDO_HSCODE_SEQ_NBR,:MFT_HSCODE_SEQ_NBR, :EDO_ASN_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:CUSTOM_HS_CODE,:userId,SYSDATE, :NBR_PKGS, :GROSS_WT, :GROSS_VOL) ");
					paramMap.put("EDO_HSCODE_SEQ_NBR", seqValue);
					paramMap.put("MFT_SEQ_NBR", mftseqnbr);
					paramMap.put("MFT_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
					paramMap.put("EDO_ASN_NBR", Integer.valueOf(stredoasnnbr));
					paramMap.put("HS_CODE",hsCodeObj.getHsCode());
					paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
					paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
					paramMap.put("CUSTOM_HS_CODE",customHs);
					paramMap.put("NBR_PKGS",newnbrpkgs);
					paramMap.put("GROSS_WT",weight);
					paramMap.put("GROSS_VOL",volume);
				
					
					paramMap.put("userId", lastmodifyuserid);
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
				// END CR FTZ HSCODE - NS JULY 2024
			}
			
			if (count1 == 0) {
				throw new BusinessException("M20802");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				
				log.info(" *** insertEdoDetailsForDPE SQL *****" + strUpdatetrans);
				log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());
				int count2 = namedParameterJdbcTemplate.update(strUpdatetrans, paramMap);
				
				if (count2 == 0) {
					throw new BusinessException("M20802");
				}
				
				log.info(" *** insertEdoDetailsForDPE SQL *****" + strUpdatetrans1);
				log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());
				
				int count3 = namedParameterJdbcTemplate.update(strUpdatetrans1, paramMap);
				if (count3 == 0) {
					throw new BusinessException("M20802");
				}
			}

			//Added by VietNguyen 20/02/2014
			String strSubAdp = "";
			Integer adpSeq = 1;
			int cntAdp = 1;
			if (adpList != null && adpList.size() > 1) {
				String sqllog ="SELECT MAX(SUB_ADP_NBR) FROM sub_adp";
				
				log.info(" *** insertEdoDetailsForDPE SQL *****" + sqllog);
				log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());
				
				SqlRowSet rs2 = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs2.next()) {
					adpSeq = rs2.getInt(1);
				}
				if (adpSeq != null) {
					adpSeq = adpSeq + 1;
				}
				for (int i = 1; i < adpList.size(); i++) {
					AdpValueObject adp = adpList.get(i);
					if (adp != null) {
						sb.setLength(0);
						sb.append("INSERT INTO sub_adp(SUB_ADP_NBR, STATUS_CD, ESN_ASN_NBR,TRUCKER_IC,TRUCKER_NM");
						sb.append(" ,TRUCKER_CO_CD,TRUCKER_NBR_PKGS,EDO_ESN_IND,CREATE_USER_ID,CREATE_DTTM");
						sb.append(" ,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM, TRUCKER_CONTACT_NBR) ");
						sb.append("	VALUES(:adpSeq, 'A', :stredoasnnbr,:truckerIc,:truckerNm,");
						sb.append(" :truckerCocd,:truckerNbrPkgs, '1',:lastmodifyuserid, sysdate,");
						sb.append(" :lastmodifyuserid, sysdate,:truckerCntctNbr)");
						strSubAdp = sb.toString();
						
						paramMap.put("adpSeq", adpSeq);
						paramMap.put("stredoasnnbr", stredoasnnbr);
						paramMap.put("truckerIc", adp.getAdpIcTdbcrNbr());
						paramMap.put("truckerNm", adp.getAdpNm());
						paramMap.put("truckerCocd", adp.getAdpCustCd());
						paramMap.put("truckerNbrPkgs", adp.getAdpNbrPkgs());
						paramMap.put("lastmodifyuserid", lastmodifyuserid);
						paramMap.put("truckerCntctNbr", adp.getAdpContact());

						log.info(" *** insertEdoDetailsForDPE SQL *****" + strSubAdp);
						log.info(" *** insertEdoDetailsForDPE params *****" + paramMap.toString());
						
						cntAdp = namedParameterJdbcTemplate.update(strSubAdp, paramMap);
						if (cntAdp == 0) {
							break;
						} else {
							insertSubAdpTxn(adpSeq, lastmodifyuserid, adp);
						}
						adpSeq ++;
					}
				}
			}


			strinsertedoasnnbr=stredoasnnbr;
			
			log.info("END: *** insertEdoDetailsForDPE Result *****" + CommonUtility.deNull(strinsertedoasnnbr));

		} catch (BusinessException e) {
			if (logStatusGlobal.equalsIgnoreCase("N")) {
				log.info(strUpdate);
				log.info(strUpdate1);
			}
			else{
				log.info(strUpdate);
				log.info(strUpdate1);
				log.info(strUpdatetrans);
				log.info(strUpdatetrans1);
			}
			log.info("Exception insertEdoDetailsForDPE : ", e);
			throw new BusinessException(e.getMessage());
		}
		catch (Exception e) {
			if (logStatusGlobal.equalsIgnoreCase("N")) {
				log.info(strUpdate);
				log.info(strUpdate1);
			}
			else{
				log.info(strUpdate);
				log.info(strUpdate1);
				log.info(strUpdatetrans);
				log.info(strUpdatetrans1);
			}
			log.info("Exception insertEdoDetailsForDPE : ", e);
			throw new BusinessException("M20802");
		} finally {
			log.info("END: insertEdoDetailsForDPE  DAO  END");
				if (logStatusGlobal.equalsIgnoreCase("N")) {
					log.info(strUpdate);
					log.info(strUpdate1);
				}
				else {
					log.info(strUpdate);
					log.info(strUpdate1);
					log.info(strUpdatetrans);
					log.info(strUpdatetrans1);
				}
	
			}
		return strinsertedoasnnbr;
		}
	
	@Override
	public boolean checkTesnExist(String edoEsnno) throws BusinessException {
        String sql = "";
        boolean result = false;
	    SqlRowSet rs = null;
	    StringBuffer sb = new StringBuffer();
	    Map<String,String> paramMap = new HashMap<String,String>();
        try {
        	 log.info("START: checkTesnExist  DAO  START" + " edoEsnno:" + edoEsnno);
        	 sb.append("select  count(*) from esn where esn_status = 'A' and esn_asn_nbr in");
        	 sb.append(" (select esn_asn_nbr From tesn_jp_jp where edo_asn_nbr = :edoEsnno)");
        	 sql =  sb.toString(); //Wanyi added on Jan 2020
        	 paramMap.put("edoEsnno", edoEsnno);
        	 
	         rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
	         
	         log.info(" *** checkTesnExist SQL *****" + sql);
 			 log.info(" *** checkTesnExist params *****" + paramMap.toString());
 			 
	          if (rs.next()) {
	             int count = rs.getInt(1);
	             if (count > 0) {
	                result = true;
	             }else{
	                 result = false;
	             }
	          }
	          log.info("END: *** checkTesnExist Result *****" + result);
	        } catch (Exception e) {
	        	log.info("Exception checkTesnExist : ", e);
	        	throw new BusinessException("M4201");
	        } finally {
				log.info("END: checkTesnExist  DAO  END");
			}
        return result;
    }
	
	@Override
	public boolean checkDeleteEdo(String edoEsnno) throws BusinessException {
        String sql = "";
        boolean result = true;
        StringBuffer sb = new StringBuffer();
        SqlRowSet rs = null;
	    Map<String,String> paramMap = new HashMap<String,String>();
        try {
        	 log.info("START: checkDeleteEdo  DAO  START" + " edoEsnno:" + edoEsnno);
        	 sb.append(" select count(*) from wa_appln_details where edo_esn_nbr =:edoEsnno ");
        	 sb.append(" and purp_cd='IM'  and rec_status not in ('R','X') ");
             sql = sb.toString();
             
        	 paramMap.put("edoEsnno", edoEsnno); 
        	 
        	 log.info(" *** checkDeleteEdo SQL *****" + sql);
 			 log.info(" *** checkDeleteEdo params *****" + paramMap.toString());
 			 
	         rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
	
	          if (rs.next()) {
	         	 int count = rs.getInt(1);
	         	 if (count > 0) {
	         		result = false;
	         	 }
	          } else {
	        	  result = true;
	          }
	          
	          log.info("END: *** checkDeleteEdo Result *****" + result);
	        } catch (Exception e) {
	        	log.info("Exception checkDeleteEdo : ", e);
	        	throw new BusinessException("M4201");
	        } finally {
				log.info("END: checkDeleteEdo  DAO  END");
			}
        return result;
    }
	
	@Override
	 public String deleteEdoDetails(String stredoasnnbr,String struserid) throws BusinessException{
	        String sql1 = "";
	        String sql2="";
	        String sql3="";

	        String strstatus="";
	        String strnbrpkgs="0";
	        String strmftseqnbr="";
	        String stredostatus="";
	        String strdnnbrpkgs="0";
	        String strtransnbrpkgs="0";
	        String strtransdnnbrpkgs="0";
	        String crgstatus="";
	        String varnbr = "";
	        String vslStatus = "";
	        Map<String,String> paramMap = new HashMap<String,String>();

	        StringBuffer sb = new StringBuffer();
	        StringBuffer sb2 = new StringBuffer();
	        
	        sb.append("SELECT NBR_PKGS, MFT_SEQ_NBR,EDO_STATUS, DN_NBR_PKGS, ");
	        sb.append(" TRANS_NBR_PKGS, TRANS_DN_NBR_PKGS, CRG_STATUS, VAR_NBR FROM GB_EDO ");
	        sb.append(" WHERE EDO_ASN_NBR=:stredoasnnbr");
	        sql1= sb.toString();
	        
	        paramMap.put("stredoasnnbr", stredoasnnbr);

	        sb.setLength(0);
	        sb.append("UPDATE  GB_EDO SET EDO_STATUS = 'X', LAST_MODIFY_USER_ID =:struserid");
	        sb.append(", LAST_MODIFY_DTTM =sysdate WHERE EDO_ASN_NBR=:stredoasnnbr");
	        sql2= sb.toString();
	        
	        paramMap.put("struserid", struserid);

	        try {
	        	log.info("START: deleteEdoDetails  DAO  START Obj" + " stredoasnnbr:" + stredoasnnbr + " struserid:" + struserid);
	        	log.info(" *** deleteEdoDetails SQL *****" + sql1);
				log.info(" *** deleteEdoDetails params *****" + paramMap.toString());

	            SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
	            while(rs1.next()) {
	                strnbrpkgs = CommonUtility.deNull(rs1.getString("NBR_PKGS"));
	                strmftseqnbr = CommonUtility.deNull(rs1.getString("MFT_SEQ_NBR"));
	                stredostatus = CommonUtility.deNull(rs1.getString("EDO_STATUS"));
	                strdnnbrpkgs =CommonUtility.deNull(rs1.getString("DN_NBR_PKGS"));
	                strtransnbrpkgs =CommonUtility.deNull(rs1.getString("TRANS_NBR_PKGS"));
	                strtransdnnbrpkgs =CommonUtility.deNull(rs1.getString("TRANS_DN_NBR_PKGS"));
	                crgstatus =CommonUtility.deNull(rs1.getString("CRG_STATUS"));
	                varnbr =CommonUtility.deNull(rs1.getString("VAR_NBR"));
	            }
	            
	            if (crgstatus.equalsIgnoreCase("R")){
	            	try{
	            	vslStatus = getVslStatus(varnbr);
	            	if (!(vslStatus.equalsIgnoreCase("AP") || vslStatus.equalsIgnoreCase("AL"))){
	            		 throw new BusinessException("M4599");
	            		}
	            	} catch(Exception re){
	            		 log.info("Writing from EDOEJB while getting status for Re-export");
	            		 throw new BusinessException("M4599");
	            	}
	            }
	            //checking before delete
	            checkPackageAmendDelete(stredoasnnbr,strdnnbrpkgs,strtransnbrpkgs,strtransdnnbrpkgs,"deleted");

	            //check close bj
	            //Commented to allow delete even after Close BJ
	            /*String closeBjInd ="N";
	            String sqlclbj = "SELECT VESCALL.GB_CLOSE_BJ_IND FROM"
	                            +" VESSEL_CALL VESCALL, GB_EDO EDO WHERE"
	                            +" VESCALL.VV_CD=EDO.VAR_NBR AND"
	                            +" EDO.EDO_ASN_NBR='"+stredoasnnbr+"'";
	            Statement sqlstmtclbj = con.createStatement();
	            ResultSet rsclbj = sqlstmtclbj.executeQuery(sqlclbj);
	            while(rsclbj.next()) {
	                closeBjInd =CommonUtility.deNull(rsclbj.getString(1));
	            }
	            rsclbj.close();
	            sqlstmtclbj.close();

	            if (!(closeBjInd.equalsIgnoreCase("N"))) {
	                throw new BusinessException("M21605");
	            } */

	            if (!(stredostatus.equalsIgnoreCase("X"))) {
	            	log.info(" *** deleteEdoDetails SQL2 *****" + sql2);
					log.info(" *** deleteEdoDetails params *****" + paramMap.toString());
	                int y= namedParameterJdbcTemplate.update(sql2, paramMap);
	                if (y == 1){
	                    strstatus="true";
	                    
	                    // START FTZ Delete EDO MultiHsCode to reset Back the total pkg  -NS July 2024
	                    
	                    sb2.setLength(0);
						sb2.append(" DELETE FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE EDO_ASN_NBR = :stredoasnnbr ");
					
						paramMap.put("stredoasnnbr", stredoasnnbr);
						log.info("SQL" + sb2.toString());
						log.info("ParamMap" + paramMap.toString());
						int counths = namedParameterJdbcTemplate.update(sb2.toString(), paramMap);
						log.info("counths : " + counths);
						
						sb2.setLength(0);
						sb2.append(" SELECT EDO_HSCODE_SEQ_NBR , MFT_SEQ_NBR, MFT_HSCODE_SEQ_NBR, NBR_PKGS, GROSS_WT, GROSS_VOL, HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, LAST_MODIFY_DTTM FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE EDO_ASN_NBR = :stredoasnnbr ");
						log.info("SQL" + sb2.toString());
						SqlRowSet rs2 = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);
						while(rs2.next()) {
							
							sb2.setLength(0);
							sb2.append(" INSERT INTO GBMS.GB_EDO_HSCODE_DETAILS_TRANS  ");
							sb2.append(" (EDO_HSCODE_SEQ_NBR, MFT_SEQ_NBR, MFT_HSCODE_SEQ_NBR, EDO_ASN_NBR, AUDIT_DTTM, REC_STATUS, NBR_PKGS, GROSS_WT, GROSS_VOL, HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID) ");
							sb2.append(" VALUES(:EDO_HSCODE_SEQ_NBR,:MFT_SEQ_NBR, :MFT_HSCODE_SEQ_NBR, :stredoasnnbr, SYSDATE, 'I', :NBR_PKGS, :GROSS_WT, :GROSS_VOL, :HS_CODE, :HS_SUB_CODE_FR, :HS_SUB_CODE_TO, SYSDATE :LAST_MODIFY_USER_ID ) ");
				
							paramMap.put("EDO_HSCODE_SEQ_NBR", rs2.getString("EDO_HSCODE_SEQ_NBR"));
							paramMap.put("MFT_SEQ_NBR", rs2.getString("MFT_SEQ_NBR"));
							paramMap.put("MFT_HSCODE_SEQ_NBR", rs2.getString("MFT_HSCODE_SEQ_NBR"));
							paramMap.put("NBR_PKGS", rs2.getString("NBR_PKGS"));
							paramMap.put("GROSS_WT", rs2.getString("GROSS_WT"));
							paramMap.put("GROSS_VOL", rs2.getString("GROSS_VOL"));
							paramMap.put("HS_CODE", rs2.getString("HS_CODE"));
							paramMap.put("HS_SUB_CODE_FR", rs2.getString("HS_SUB_CODE_FR"));
							paramMap.put("HS_SUB_CODE_TO", rs2.getString("HS_SUB_CODE_TO"));
							paramMap.put("LAST_MODIFY_USER_ID", struserid);
							
							log.info("SQL" + sb2.toString());
							log.info("ParamMap" + paramMap.toString());
							int counthst = namedParameterJdbcTemplate.update(sb2.toString(), paramMap);
							log.info("counthst : " + counthst);
						}
						                    
	                    // END FTZ Delete EDO MultiHsCode to reset Back the total pkg  -NS July 2024
	                }
	                else {
	                    log.info("Writing from Writing from EdoEjb.deleteEdoDetails");
	                    log.info("Error in Deleting Edo Number " +stredoasnnbr);
	                    throw new BusinessException("M20804");
	                }

	                sb.setLength(0);
	                sb.append("UPDATE  MANIFEST_DETAILS SET EDO_NBR_PKGS=EDO_NBR_PKGS-:strnbrpkgs");
	                sb.append(", LAST_MODIFY_USER_ID =:struserid");
	                sb.append(", LAST_MODIFY_DTTM=sysdate");
	                sb.append(" WHERE MFT_SEQ_NBR=:strmftseqnbr");
	                sql3= sb.toString();

	    	        paramMap.put("strmftseqnbr", strmftseqnbr);
	    	        paramMap.put("strnbrpkgs", strnbrpkgs);
	    	        
	    	        log.info(" *** deleteEdoDetails SQL3 *****" + sql3);
					log.info(" *** deleteEdoDetails params *****" + paramMap.toString());
					
	                int z= namedParameterJdbcTemplate.update(sql3, paramMap);
	                if (z == 1) {
	                    strstatus="true";
	                }
	                else {
	                    log.info("Writing from Writing from EdoEjb.deleteEdoDetails");
	                    log.info("Error in Deleting Edo Number " +stredoasnnbr);
	                    throw new BusinessException("M20804");
	                }
	            }
	            else {
	                strstatus="true";
	            }
	            log.info("END: *** deleteEdoDetails Result *****" + CommonUtility.deNull(strstatus));
	            return strstatus;
	        } catch (BusinessException e) {
	        	log.info("Exception deleteEdoDetails : ", e);
	        	throw new BusinessException(e.getMessage());
	        } catch (Exception e) {
	        	log.info("Exception deleteEdoDetails : ", e);
	        	throw new BusinessException("M20804");
	        } finally {
				log.info("END: deleteEdoDetails  DAO  END");
			}
	    }
	
	@Override
	public List<vesselVoyObjectValue> getVslVoyNbrList(String coCd, String strmodulecd, String search) throws BusinessException{
		String sql = "";		
		StringBuffer sb=new StringBuffer();		
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		String strCustCode=coCd;
		boolean isShowEdoInfo = false;
		List<vesselVoyObjectValue> vesselvoyagevector=new ArrayList<vesselVoyObjectValue>();
		try {
			log.info("getVslVoyNbrList STARTS: Params:" + "coCd:" + strCustCode + "strmodulecd:"
					+ strmodulecd  + "search:"
					+ search);

			try {
				TextParaVO code = new TextParaVO();
				code.setParaCode(TEXT_PARA_GC_VIEW_EDO);
				TextParaVO result = getParaCodeInfo(code);
				isShowEdoInfo = isShowEdoInfo(strCustCode, result);
				
				log.info(" *** getVslVoyNbrList isShowEdoInfo *****" + isShowEdoInfo);
			} catch (Exception e) {
				log.info("Exception getVslVoyNbrList: " ,e);
				throw new BusinessException("M4201");
			}
			// ++ 19.10.2009 Changed by LongDh09::for GB CR
			// Sripriya To fix Vessel Listing for ADP Renom 2 Apr 2012
			if (isShowEdoInfo) {
				if (strmodulecd.equalsIgnoreCase("ADPRENOM")) {
					// +" AND VC.TERMINAL='GB') "
					// +" nvl(VC.GB_CLOSE_BJ_IND,'N') = 'N' AND
					// EDO.APPOINTED_ADP_CUST_CD='"+strCustCode
					// BEGIN FPT modify to allow ADP renom even when Close BJ
					// +" nvl(VC.GB_CLOSE_BJ_IND,'N') = 'N' AND "
					// END FPT modify to allow ADP renom even when Close BJ
					// +"' AND VC.VV_CD=EDO.VAR_NBR ORDER BY VC.VSL_NM,"

					sb = new StringBuffer();
					sb.append(" SELECT DISTINCT VC.VV_CD AS var_nbr, VC.VSL_NM, VC.IN_VOY_NBR, VC.TERMINAL ");
					sb.append(" FROM VESSEL_CALL VC,GB_EDO EDO WHERE ");
					sb.append(" (VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB') ");
					sb.append(" AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT'))");
					sb.append("  AND VC.VV_CD=EDO.VAR_NBR ORDER BY VC.TERMINAL DESC, VC.VSL_NM,");
					sb.append(" VC.IN_VOY_NBR");
				} else {
					
					sb = new StringBuffer();
					sb.append("SELECT VC.VV_CD  ,VC.VSL_NM,VC.IN_VOY_NBR, VC.TERMINAL  FROM ");
					sb.append(" VESSEL_CALL VC WHERE ");
					sb.append(" (VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB') ");
					sb.append("  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')) ");
					sb.append(
							"  AND nvl(VC.GB_CLOSE_BJ_IND,'N') = 'N' ");
					
					if(!search.isEmpty()) {
						sb.append(" AND (VC.VV_CD like :search OR");
						sb.append(" VC.VSL_NM like :search OR");
						sb.append(" VC.IN_VOY_NBR like :search) ");
						
						paramMap.put("search", '%' +search +'%');
					}
					sb.append(" ORDER BY VC.TERMINAL DESC, VC.VSL_NM, VC.IN_VOY_NBR ");
					// +" AND VC.TERMINAL='GB' )"
				}
			} else {
				if (strmodulecd.equalsIgnoreCase("ADPRENOM")) {
					sb = new StringBuffer();
					sb.append("SELECT DISTINCT VC.VV_CD , VC.VSL_NM, VC.IN_VOY_NBR,VC.TERMINAL");
					sb.append(" FROM VESSEL_CALL VC,GB_EDO EDO WHERE");
					sb.append(" (VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB')");
					sb.append("  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')) ");
					sb.append(" AND EDO.APPOINTED_ADP_CUST_CD=:strCustCode");
					sb.append("  AND VC.VV_CD=EDO.VAR_NBR ORDER BY  VC.TERMINAL DESC, VC.VSL_NM,");
					sb.append(" VC.IN_VOY_NBR");
				} else {
					if (strmodulecd.equalsIgnoreCase("EDOADPVIEW")) {
						sb = new StringBuffer();
						sb.append("SELECT DISTINCT VC.VV_CD , VC.VSL_NM, VC.IN_VOY_NBR, VC.TERMINAL ");
						sb.append(" FROM VESSEL_CALL VC,GB_EDO EDO WHERE");
						sb.append(" (VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB')");
						sb.append(
								"  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')) ");
						sb.append(" AND VC.VV_CD=EDO.VAR_NBR");
						// sb.append(" AND (EDO.ADP_CUST_CD=:strCustCode");param
						// sb.append(" OR EDO.CA_CUST_CD=:strCustCode");param
						sb.append(") ORDER BY  VC.TERMINAL DESC, VC.VSL_NM, VC.IN_VOY_NBR");

					} else {
						sb = new StringBuffer();
						/*
						 * sql="SELECT VC.VV_CD,VC.VSL_NM,VC.IN_VOY_NBR  FROM" +" VESSEL_CALL VC WHERE "
						 * +" (VC.VV_STATUS_IND IN ('PR','AP','AL','BR','UB')"
						 * +" AND VC.TERMINAL='GB') AND"
						 * +" nvl(VC.GB_CLOSE_BJ_IND,'N') = 'N' AND nvl(VC.DECLARANT_CUST_CD,VC.CREATE_CUST_CD)='"
						 * +strCustCode+"' ORDER BY VC.VSL_NM, VC.IN_VOY_NBR ";
						 */
						sb.append(" SELECT DISTINCT  VC.VV_CD , VC.VSL_NM, VC.IN_VOY_NBR, VC.TERMINAL ");
						sb.append(
								" FROM VESSEL_CALL VC LEFT OUTER JOIN VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A')");
						sb.append(" WHERE VV_STATUS_IND IN ('PR','AP','AL','BR','UB') ");
						sb.append(
								"  AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT') ");
						sb.append(" AND nvl(VC.GB_CLOSE_BJ_IND,'N') = 'N'");
						sb.append(" AND (VD.CUST_CD = :strCustCode OR VC.CREATE_CUST_CD = :strCustCode) ");
						if(!search.isEmpty()) {
							sb.append(" AND (VC.VV_CD like :search OR");
							sb.append(" VC.VSL_NM like :search OR");
							sb.append(" VC.IN_VOY_NBR like :search) ");
							
							paramMap.put("search", '%' +search +'%');
						}
						sb.append(" ORDER BY VC.TERMINAL DESC, VSL_NM,IN_VOY_NBR");
						paramMap.put("strCustCode", strCustCode);
					}

				}
			}

			
			sql=sb.toString();
			log.info(strCustCode + ":" + strmodulecd + "::Vessel List SQL-vietnd-EDO:: " + sb.toString() + param + paramMap);
			// -- 19.10.2009 Changed by LongDh09::for GB CR
			// System.out.println("INSIDE BEAN TRY SQL:"+sql);
			// System.out.println("inside bean con");
			// System.out.println("inside bean sta");


			if (strmodulecd.equalsIgnoreCase("ADPRENOM")){
				paramMap.put("strCustCode", strCustCode);
			}
			if (strmodulecd.equalsIgnoreCase("EDOADPVIEW")) {
				paramMap.put("strCustCode", strCustCode);
			}

			log.info(" *** getVslVoyNbrList params *****" + paramMap.toString());

			rs= namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while(rs.next()) {
				vesselVoyObjectValue edoValueObject = new vesselVoyObjectValue();
				String varnbr=CommonUtility.deNull(rs.getString("VV_CD"));
				String vslnm=CommonUtility.deNull(rs.getString("VSL_NM"));
				String invoynbr=CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				String terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				edoValueObject.setVarNbr(varnbr);
				edoValueObject.setVslNm(vslnm);
				edoValueObject.setInVoyNbr(invoynbr);
				edoValueObject.setTerminal(terminal);
				vesselvoyagevector.add(edoValueObject);
			}

			log.info("getVslVoyNbrList Result " + vesselvoyagevector.toString());
			return vesselvoyagevector;
	
		} catch (NullPointerException e) {
			log.info("Exception getVslVoyNbrList :" ,e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getVslVoyNbrList :" ,e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getVslVoyNbrList :" ,e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVslVoyNbrList  DAO  END");
		}

	}
	
	@Override
	public TableResult getEdoLst(String strCustCode, String strVarNbr, String strmodulecd, Criteria criteria)
			throws BusinessException {
		// System.out.println("inside bean"+strCustCode+" nbr"+strVarNbr);

		log.info("getEdoLst 2 params STARTS" + strCustCode + " " + strmodulecd);
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();
		Map<String, String> paramMap = new HashMap<>();
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		String sql = "";
		// 16.01.02
		// ++ 19.10.2009 - changed by vietnd02 for GB CR - set view for vessel operator
		// and third party
		try {
			log.info("getEdoLst start:"+"strCustCode :"+strCustCode+"strVarNbr :"+strVarNbr+"strmodulecd :"+strmodulecd + " criteria:" + criteria.toString());

			sb.append("SELECT A.CUT_OFF_NBR CUTOFF_PKGS, A.EDO_ASN_NBR EDOASN_NBR,");
			sb.append(" A.CUT_OFF_TYPE CUT_OFF_TYPE, A.CUT_OFF_NBR_PKGS CUTOFF_QTY,");
			sb.append(" B.BL_NBR BL_NBR, B.NBR_PKGS TOTAL_PKGS_BL,NVL(C.NBR_PKGS,0) TOTAL_PKGS_EDO, VC.TERMINAL,VC.SCHEME,VC.COMBI_GC_SCHEME,VC.COMBI_GC_OPS_IND");
			sb.append(" FROM CUT_OFF_DETAILS A, MANIFEST_DETAILS B,GB_EDO C, VESSEL_CALL VC ");
			sb.append(" WHERE A.MFT_SEQ_NBR=B.MFT_SEQ_NBR AND C.EDO_ASN_NBR(+) = A.EDO_ASN_NBR");
			sb.append(" AND A.CUT_OFF_STATUS = 'A' AND A.VAR_NBR=:strVarNbr ");
			sb.append(" AND A.VAR_NBR = VC.VV_CD ORDER BY CUTOFF_PKGS ASC");
		
			
			sql = sb.toString();
			paramMap.put("strVarNbr", strVarNbr);
			
			if (criteria.isPaginated()) {
		        tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
		            paramMap, Integer.class));
			}
			
			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			
			log.info("EDOEJB.getEdoLst + " + sb.toString() + param + paramMap);
			log.info(" *** getEdoLst params *****" + paramMap.toString());
			
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while(rs.next()) {

				CutoffValueObject cutoffValueObject = new CutoffValueObject();
				String cutoffnbr=CommonUtility.deNull(rs.getString(1));
				String edoasnnbr=CommonUtility.deNull(rs.getString(2));
				String cutofftype=CommonUtility.deNull(rs.getString(3));
				String cutoffqty=CommonUtility.deNull(rs.getString(4));
				String blnbr=CommonUtility.deNull(rs.getString(5));
				String totalpkgs=CommonUtility.deNull(rs.getString(6));
				String totalpkgs_edo=CommonUtility.deNull(rs.getString(7));
				String terminal=CommonUtility.deNull(rs.getString("TERMINAL"));
				String scheme=CommonUtility.deNull(rs.getString("SCHEME"));
				String subScheme=CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				String gcOperations=CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));

				cutoffValueObject.setCutoffNbr(cutoffnbr);
				//cutoffValueObject.setEdoAsnNbr(edoasnnbr);
				if(edoasnnbr==null || edoasnnbr.equalsIgnoreCase(""))
				{
					cutoffValueObject.setEdoAsnNbr("-NA-");
				}
				else
				{cutoffValueObject.setEdoAsnNbr(edoasnnbr);}
				
				cutoffValueObject.setCutoffType(cutofftype);
				cutoffValueObject.setCutoffQty(cutoffqty);
				cutoffValueObject.setBlNbr(blnbr);
				cutoffValueObject.setTotalPkgs(totalpkgs);
				cutoffValueObject.setPkgType(totalpkgs_edo);
				cutoffValueObject.setTerminal(terminal);
				cutoffValueObject.setScheme(scheme);
				cutoffValueObject.setSubScheme(subScheme);
				cutoffValueObject.setGcOperations(gcOperations);
				topsModel.put(cutoffValueObject);
			}

			log.info(" getEdoLst  DAO  Result: "+ tableResult.toString());
			return tableResult;
		
		} catch (NullPointerException e) {
			log.info("Exception getEdoLst: " ,e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEdoLst: " ,e);
			throw new BusinessException("M4201");
		} finally {
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			log.info("END: getEdoLst  DAO  END");
		}

	}

	@Override
	public List<String> indicationStatus(String vvCd) throws BusinessException {
		List<String> voyList = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		
		String indicationOfArrival = "";
		String indicationOfDeparture = "";
		
		try {
			log.info("START: indicationStatus vvCd:" + vvCd);
			sb= new StringBuilder();
			sb.append(" SELECT VC.VV_CD varNbr,TO_CHAR(a.arrival,'dd/mm/yyyy HH24MI') arrival,a.indicationOfArrival,TO_CHAR(d.departural,'dd/mm/yyyy HH24MI') departural,d.indicationOfDeparture ");
			sb.append(" FROM  TOPS.VESSeL_CALL VC ");
			sb.append(" JOIN (SELECT VV_CD, CASE WHEN ATB_DTTM is null then ETB_DTTM ELSE ATB_DTTM END arrival , CASE WHEN ATB_DTTM is null then 'ETB' ELSE 'ATB' END indicationOfArrival ");
			sb.append(" FROM BERTHING WHERE SHIFT_IND=1 ) a ON a.VV_CD=VC.VV_CD ");
			sb.append(" JOIN (SELECT B.VV_CD, CASE WHEN ATU_DTTM is null then ETU_DTTM ELSE ATU_DTTM END departural, CASE WHEN ATU_DTTM is null then 'ETU' ELSE 'ATU' END indicationOfDeparture ");
			sb.append(" FROM BERTHING B WHERE SHIFT_IND= (select MAX(Shift_ind) from BERTHING c where c.vv_cd=B.VV_CD) ) d ");
			sb.append(" ON d.VV_CD=VC.VV_CD ");
			sb.append(" WHERE VC.VV_CD= :vvCd ");
			
			paramMap.put("vvCd",vvCd);
			
			log.info("***** indicationStatus SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				indicationOfArrival = CommonUtility.deNull(rs.getString("INDICATIONOFARRIVAL"));
				indicationOfDeparture = CommonUtility.deNull(rs.getString("INDICATIONOFDEPARTURE"));
				voyList.add(indicationOfArrival);
				voyList.add(indicationOfDeparture);

			}
			log.info("END:*** indicationStatus Result *****:voyList" + voyList);
		} catch (Exception e) {
			log.info("Exception indicationStatus : ", e);
			throw new BusinessException("M4201");
		}  finally {
			log.info("END:*** indicationStatus END *****");
		}
		return voyList;
	}

	// START CR FTZ HSCODE - NS JULY 2024
	@Override
	public List<HsCodeDetails> getHsCodeDetails(String mftSeqNbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		
		try {
			log.info("START: getHsCodeDetails mftSeqNbrd:" + mftSeqNbr);
			sb= new StringBuilder();
			sb.append(" SELECT MFT_HSCODE_SEQ_NBR HSCODE_SEQ_NBR, MFT_SEQ_NBR, HS_CODE, CUSTOM_HS_CODE, HS_SUB_CODE_FR,");
			sb.append("HS_SUB_CODE_TO, HS_SUB_CODE_DESC, NBR_PKGS, GROSS_WT, GROSS_VOL, CRG_DES, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM");
			sb.append(" FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_SEQ_NBR = :mftSeqNbr ");	
			
			paramMap.put("mftSeqNbr",mftSeqNbr);
			
			log.info("***** getHsCodeDetails SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			
			try {
				return namedParameterJdbcTemplate.query(sb.toString(), paramMap,
						new BeanPropertyRowMapper<HsCodeDetails>(HsCodeDetails.class));
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
		} catch (NullPointerException e) {
			log.info("Exception getHsCodeDetails : ", e);
		} catch (Exception e) {
			log.info("Exception getHsCodeDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getHsCodeDetails  DAO ");
		}
		return null;
	}
	

	@Override
	public boolean ifHsCodeExist(String mftSeqNbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		boolean exist = false;
		try {
			log.info("START: ifHsCodeExist mftSeqNbrd:" + mftSeqNbr);
			sb= new StringBuilder();
			
			sb.append(" SELECT COUNT(*) FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_SEQ_NBR = :mftSeqNbr  ");
			paramMap.put("mftSeqNbr",mftSeqNbr);
			
			log.info("***** getHsCodeDetails SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				exist = (rs.getInt(1)>0) ? true : false;
			}
			
		} catch (NullPointerException e) {
			log.info("Exception ifHsCodeExist : ", e);
		} catch (Exception e) {
			log.info("Exception ifHsCodeExist : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: ifHsCodeExist  DAO : exist = " + exist);
		}
		return exist;
	}

	@Override
	public boolean correctMultiHsCode(String mftSeqNbr, List<HsCodeDetails> multiHsCodeList) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource(); 
		SqlRowSet rs = null;
		boolean exist = true;
		List<String> listDbSeqNbr = new ArrayList<String>();
		List<String>  hscodeList = new ArrayList<String>();
		
		try {
			log.info("START: correctMultiHsCode mftSeqNbrd:" + mftSeqNbr + ", multiHsCodeList : "
					+ multiHsCodeList.toString());
			for (HsCodeDetails hsCodeObj : multiHsCodeList) {
				hscodeList.add(hsCodeObj.getHscodeSeqNbr());					
			}
			
			sb = new StringBuilder();
			sb.append(" SELECT EDO_ASN_NBR ");
			sb.append(" FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_HSCODE_SEQ_NBR in (:HsSeqNbr) and MFT_SEQ_NBR = :mftSeqNbr");
			
			paramMap.addValue("HsSeqNbr", hscodeList);
			paramMap.addValue("mftSeqNbr", mftSeqNbr);
			log.info("***** getHsCodeDetails SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) { // hscode already used
					
				// check if previously its combined with other hscode
				String edoAsnNbr = rs.getString("EDO_ASN_NBR");
				sb = new StringBuilder();
				sb.append(" SELECT MFT_HSCODE_SEQ_NBR ");
				sb.append(" FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE EDO_ASN_NBR = :edoAsnNbr");

				
				paramMap.addValue("edoAsnNbr", edoAsnNbr);
				log.info("***** getHsCodeDetails SQL *****" + sb.toString());
				log.info("params: " + paramMap.toString());
				
				SqlRowSet rs2 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				while(rs2.next()) {
					listDbSeqNbr.add(rs2.getString("MFT_HSCODE_SEQ_NBR"));
				}
				

				// compare result EDO_HSCODE_SEQ_NBR same with hscodeList
				if(!(listDbSeqNbr.containsAll(hscodeList) && hscodeList.containsAll(listDbSeqNbr))) {
					exist = false;
				}
					
				 
			}
			

		} catch (NullPointerException e) {
			log.info("Exception correctMultiHsCode : ", e);
		} catch (Exception e) {
			log.info("Exception correctMultiHsCode : ", e);
			throw new BusinessException("M4201"); 
		} finally {
			log.info("END: correctMultiHsCode  DAO : exist = " + exist);
		}
		return exist;
	}
	
	@Override
	public boolean isShowRemainder(String newNbrPkgs, List<HsCodeDetails> multiHsCodeList) throws BusinessException {
		boolean isShow = false;
		int totalPkgs = 0;
		
		try {
			log.info("START: isShowRemainder newNbrPkgs:" + newNbrPkgs + ", multiHsCodeList : " + multiHsCodeList.toString());
			for (HsCodeDetails hsCodeObj : multiHsCodeList) {
				totalPkgs += Integer.parseInt(hsCodeObj.getNbrPkgs());
			}
			
			if (newNbrPkgs == null || newNbrPkgs.trim().isEmpty()) {
				throw new BusinessException("Invalid package count");
			}
			
			int edoNbrPkgs=Integer.parseInt(newNbrPkgs.trim());
			
			log.info("totalPkgs : " + totalPkgs + "edoNbrPkg : " + edoNbrPkgs);
			
			isShow = (edoNbrPkgs != totalPkgs);

		} catch (NullPointerException e) {
			log.info("Exception isShowRemainder : ", e);
		} catch (Exception e) {
			log.info("Exception isShowRemainder : ", e);
		} finally {
			log.info("END: isShowRemainder  DAO : isShow = " + isShow);
		}
		return isShow;
	}

	@Override
	public List<HsCodeDetails> getEdoHsCodeDetails(String edoAsnNbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		
		try {
			log.info("START: getEdoHsCodeDetails edoAsnNbr:" + edoAsnNbr);
			sb= new StringBuilder();
			sb.append(" SELECT COUNT(*) COUNT FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE EDO_ASN_NBR = :edoAsnNbr");
			paramMap.put("edoAsnNbr",edoAsnNbr);
			int countCheck = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
			if (countCheck == 0) {
				// old data - get from gb_edo
				sb= new StringBuilder();
				sb.append("SELECT edo.*, mft.HS_CODE, mft.CUSTOM_HS_CODE, mft.HS_SUB_CODE_FR, mft.HS_SUB_CODE_TO ");
				sb.append("FROM MANIFEST_DETAILS mft, (SELECT MFT_SEQ_NBR,	LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, NOM_WT GROSS_WT, NOM_VOL GROSS_VOL ");
				sb.append("FROM GBMS.GB_EDO WHERE EDO_ASN_NBR = :edoAsnNbr) edo WHERE mft.MFT_SEQ_NBR = ( SELECT DISTINCT MFT_SEQ_NBR FROM ");
				sb.append("GBMS.GB_EDO WHERE EDO_ASN_NBR = :edoAsnNbr )");
				
				paramMap.put("edoAsnNbr",edoAsnNbr);
				
				log.info("***** getEdoHsCodeDetails SQL *****" + sb.toString());
				log.info("params: " + paramMap.toString());
				try {
					return namedParameterJdbcTemplate.query(sb.toString(), paramMap,
							new BeanPropertyRowMapper<HsCodeDetails>(HsCodeDetails.class));
				} catch (EmptyResultDataAccessException e) {
					return null;
				}
				
			} else {
				sb= new StringBuilder();
				sb.append(" SELECT EDO_HSCODE_SEQ_NBR HSCODE_SEQ_NBR, MFT_SEQ_NBR, HS_CODE, CUSTOM_HS_CODE, HS_SUB_CODE_FR,");
				sb.append(" HS_SUB_CODE_TO, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, NBR_PKGS, GROSS_WT, GROSS_VOL");
				sb.append(" FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE EDO_ASN_NBR = :edoAsnNbr ");	
				
				log.info("***** getEdoHsCodeDetails SQL *****" + sb.toString());
				log.info("params: " + paramMap.toString());
				
				try {
					return namedParameterJdbcTemplate.query(sb.toString(), paramMap,
							new BeanPropertyRowMapper<HsCodeDetails>(HsCodeDetails.class));
				} catch (EmptyResultDataAccessException e) {
					return null;
				}
			}
			
		} catch (NullPointerException e) {
			log.info("Exception getEdoHsCodeDetails : ", e);
		} catch (Exception e) {
			log.info("Exception getEdoHsCodeDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEdoHsCodeDetails  DAO ");
		}
		return null;
	}

	@Override
	public List<Map<String, String>> getOptionHscodeExisting(String mftSeqNbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		SqlRowSet rs = null;
		List<Map<String, String>> optionHscodeExistingList = new ArrayList<Map<String, String>>();
		Map<String, String> optionHscodeExisting = new HashMap<String, String>();
		List<String> edoMftHsList = new ArrayList<>();
		try {
			log.info("START: getOptionHscodeExisting mftSeqNbr:" + mftSeqNbr);
			
			sb= new StringBuilder();
			sb.append(" SELECT comb.combine_mftHsSeqNbr, comb.combine_hs, comb.combine_customHsCode, SUM(comb.NBR_PKGS) NBR_PKGS, SUM(comb.gross_wt) gross_wt, SUM(comb.gross_vol) gross_vol FROM ( ");
			sb.append(" SELECT LISTAGG(edo.MFT_HSCODE_SEQ_NBR, ',') WITHIN GROUP (ORDER BY edo.HS_CODE) AS combine_mftHsSeqNbr, ");
			sb.append(" LISTAGG(NVL(edo.CUSTOM_HS_CODE, 'NULL'), ',') WITHIN GROUP (ORDER BY edo.HS_CODE) AS combine_customHsCode,  ");
			sb.append(" LISTAGG(edo.HS_CODE || '(' || edo.HS_SUB_CODE_FR || '-' || edo.HS_SUB_CODE_TO || ') ' || NVL2(edo.CUSTOM_HS_CODE, '~'||edo.CUSTOM_HS_CODE, ''), ', ') WITHIN GROUP (ORDER BY edo.HS_CODE) AS combine_hs,   ");
			sb.append(" edo.NBR_PKGS, edo.gross_wt,edo.gross_vol  , edo.EDO_ASN_NBR ");
			sb.append(" FROM (SELECT hscode.EDO_ASN_NBR, hscode.MFT_HSCODE_SEQ_NBR, hscode.HS_CODE, hscode.HS_SUB_CODE_FR, hscode.HS_SUB_CODE_TO, hscode.CUSTOM_HS_CODE, SUM(hscode.NBR_PKGS) nbr_pkgs, SUM(hscode.GROSS_WT) gross_wt, SUM(hscode.GROSS_VOL) gross_vol, hscode.MFT_SEQ_NBR   ");
			sb.append(" FROM GBMS.GB_EDO_HSCODE_DETAILS hscode JOIN gbms.gb_edo e on hscode.MFT_SEQ_NBR=e.MFT_SEQ_NBR AND hscode.EDO_ASN_NBR = e.EDO_ASN_NBR WHERE hscode.MFT_SEQ_NBR = :mftSeqNbr and e.edo_status = 'A' GROUP BY hscode.EDO_ASN_NBR, hscode.MFT_HSCODE_SEQ_NBR, hscode.HS_CODE, hscode.MFT_SEQ_NBR, hscode.HS_SUB_CODE_FR, hscode.HS_SUB_CODE_TO, hscode.CUSTOM_HS_CODE) edo   ");
			sb.append(" WHERE edo.MFT_SEQ_NBR = :mftSeqNbr GROUP BY edo.EDO_ASN_NBR, edo.NBR_PKGS, edo.gross_wt, edo.gross_vol  ) comb ");
			sb.append(" GROUP BY comb.combine_mftHsSeqNbr, comb.combine_hs,  comb.combine_customHsCode ");
			paramMap.addValue("mftSeqNbr",mftSeqNbr);
			
			log.info("***** getEdoHsCodeDetails SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while(rs.next()) {

				int totalPkgs = 0;
				Double totalWt = 0.0;
				Double totalVol = 0.0;
				
				optionHscodeExisting = new HashMap<String, String>();
				String combine_mftSeqNbr = rs.getString("combine_mftHsSeqNbr");
				String[] indSeqNbr = combine_mftSeqNbr.split(",");
				for (int i = 0; i < indSeqNbr.length; i++) {
					//get total from manifest
					sb= new StringBuilder();
					sb.append(" SELECT NBR_PKGS, GROSS_WT, GROSS_VOL ");
					sb.append(" FROM GBMS.MANIFEST_HSCODE_DETAILS mhd ");
					sb.append(" WHERE mhd.MFT_HSCODE_SEQ_NBR  = :mftHsSeqNbr ");
					paramMap.addValue("mftHsSeqNbr",indSeqNbr[i]);
					
					edoMftHsList.add(indSeqNbr[i]);
					
					log.info("***** getEdoHsCodeDetails SQL *****" + sb.toString());
					log.info("params: " + paramMap.toString());
					SqlRowSet rs2 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					while(rs2.next()) {
						totalPkgs += rs2.getInt("NBR_PKGS");
						totalWt += Double.valueOf(rs2.getString("GROSS_WT"));
						totalVol += Double.valueOf(rs2.getString("GROSS_VOL"));
					}
					
				}
				optionHscodeExisting.put("combine_mftHsSeqNbr", rs.getString("combine_mftHsSeqNbr")); 	
				optionHscodeExisting.put("combine_hs", rs.getString("combine_hs")); 
				optionHscodeExisting.put("combine_customHsCode", rs.getString("combine_customHsCode")); 
				optionHscodeExisting.put("balance_nbr_pkg", String.valueOf(totalPkgs - rs.getInt("NBR_PKGS")));
				optionHscodeExisting.put("balance_gross_wt", String.format("%.2f",totalWt - Double.valueOf(rs.getString("gross_wt"))));
				optionHscodeExisting.put("balance_gross_vol", String.format("%.2f",totalVol - Double.valueOf(rs.getString("gross_vol"))));
				optionHscodeExistingList.add(optionHscodeExisting);
			}
			
			// get left out option from manifest
			sb= new StringBuilder();
			sb.append(" SELECT *  ");
			sb.append(" FROM GBMS.MANIFEST_HSCODE_DETAILS  ");
			sb.append(" WHERE MFT_HSCODE_SEQ_NBR NOT IN (:edoMftHsList) AND MFT_SEQ_NBR = :mftSeqNbr ");
			paramMap.addValue("edoMftHsList", edoMftHsList.isEmpty() ? null : edoMftHsList);
			paramMap.addValue("mftSeqNbr",mftSeqNbr);
			
			log.info("***** getEdoHsCodeDetails SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while(rs.next()) {
				optionHscodeExisting = new HashMap<String, String>();
				optionHscodeExisting.put("combine_mftHsSeqNbr", rs.getString("MFT_HSCODE_SEQ_NBR")); 	
				optionHscodeExisting.put("combine_hs", rs.getString("HS_CODE")+"("+ rs.getString("HS_SUB_CODE_FR")+"-"+ rs.getString("HS_SUB_CODE_TO") +") " + (rs.getString("CUSTOM_HS_CODE") == null ? "" : "~"+ rs.getString("CUSTOM_HS_CODE") )); 
				optionHscodeExisting.put("combine_customHsCode", rs.getString("CUSTOM_HS_CODE")); 	
				optionHscodeExisting.put("balance_nbr_pkg", String.valueOf(rs.getInt("NBR_PKGS")));
				optionHscodeExisting.put("balance_gross_wt", String.format("%.2f",Double.valueOf(rs.getString("gross_wt"))));
				optionHscodeExisting.put("balance_gross_vol", String.format("%.2f",Double.valueOf(rs.getString("gross_vol"))));
				optionHscodeExistingList.add(optionHscodeExisting);
			}
		
			
			
		} catch (NullPointerException e) {
			log.info("Exception getOptionHscodeExisting : ", e);
		} catch (Exception e) {
			log.info("Exception getOptionHscodeExisting : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getOptionHscodeExisting  DAO " + optionHscodeExistingList);
		}
		return optionHscodeExistingList;
	}

	@Override
	public List<Map<String, String>> getAllEdoHsCodeDetails(String mftSeqNbr, String edoAsnNbr) throws BusinessException {

		StringBuilder sb0 = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		Map<String, String> map = new HashMap<String, String>(); 
		List<Map<String, String>> result =  new ArrayList<>();
		String hscode = "";
		String hssubcdfr = "";
		String hssubcdto = "";
		String custom = "";
		try {
			log.info("START: getAllEdoHsCodeDetails mftSeqNbr:" + mftSeqNbr + ", edoAsnNbr :" + edoAsnNbr);
			sb= new StringBuilder();
			sb.append(" SELECT COUNT(*) COUNT FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE EDO_ASN_NBR = :edoAsnNbr");
			paramMap.put("edoAsnNbr",edoAsnNbr);
			int countCheck = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
			if (countCheck == 0) {
				// old data - get from gb_edo
				sb= new StringBuilder();
				sb.append(" SELECT EDO_ASN_NBR, NBR_PKGS, NOM_WT, NOM_VOL ");
				sb.append(" FROM GBMS.GB_EDO WHERE MFT_SEQ_NBR = :mftSeqNbr ");
				sb.append(" GROUP BY EDO_ASN_NBR, NBR_PKGS, NOM_WT, NOM_VOL ");
				
				paramMap.put("mftSeqNbr",mftSeqNbr);
				
				log.info("***** getEdoHsCodeDetails SQL *****" + sb.toString());
				log.info("params: " + paramMap.toString());
				
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				while(rs.next()) {
					 map = new HashMap<String, String>(); 
					 map.put("EDO_ASN_NBR", rs.getString("EDO_ASN_NBR"));
					 map.put("NBR_PKGS", rs.getString("NBR_PKGS"));
					 map.put("GROSS_WT", rs.getString("NOM_WT"));
					 map.put("GROSS_VOL", rs.getString("NOM_VOL"));
					 result.add(map);
				}
				
			} else {
				sb0= new StringBuilder();
				sb0.append(" SELECT HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, CUSTOM_HS_CODE ");
				sb0.append(" FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE EDO_ASN_NBR = :edoAsnNbr ");
				paramMap.put("edoAsnNbr",edoAsnNbr);
				rs = namedParameterJdbcTemplate.queryForRowSet(sb0.toString(), paramMap);
				if(rs.next()) {
					 map = new HashMap<String, String>(); 
					 hscode =  rs.getString("HS_CODE");
					 hssubcdfr =  rs.getString("HS_SUB_CODE_FR");
					 hssubcdto =  rs.getString("HS_SUB_CODE_TO");
					 custom =  rs.getString("CUSTOM_HS_CODE");
				}
				
				
				sb= new StringBuilder();
				sb.append(" SELECT EDO_ASN_NBR, NBR_PKGS, GROSS_WT, GROSS_VOL ");
				sb.append(" FROM GBMS.GB_EDO_HSCODE_DETAILS WHERE MFT_SEQ_NBR = :mftSeqNbr ");
				sb.append("  AND HS_CODE = :hscode AND HS_SUB_CODE_FR = :hssubcdfr AND HS_SUB_CODE_TO = :hssubcdto AND CUSTOM_HS_CODE = :custom ");
				sb.append(" GROUP BY EDO_ASN_NBR, NBR_PKGS, GROSS_WT, GROSS_VOL ");
				
				paramMap.put("mftSeqNbr",mftSeqNbr);
				paramMap.put("hscode",hscode);
				paramMap.put("hssubcdfr",hssubcdfr);
				paramMap.put("hssubcdto",hssubcdto);
				paramMap.put("custom",custom);
				
				log.info("***** getEdoHsCodeDetails SQL *****" + sb.toString());
				log.info("params: " + paramMap.toString());
				
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				while(rs.next()) {
					 map = new HashMap<String, String>(); 
					 map.put("EDO_ASN_NBR", rs.getString("EDO_ASN_NBR"));
					 map.put("NBR_PKGS", rs.getString("NBR_PKGS"));
					 map.put("GROSS_WT", rs.getString("GROSS_WT"));
					 map.put("GROSS_VOL", rs.getString("GROSS_VOL"));
					 result.add(map);
				}
			}
			
			
		} catch (NullPointerException e) {
			log.info("Exception getAllEdoHsCodeDetails : ", e);
		} catch (Exception e) {
			log.info("Exception getAllEdoHsCodeDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAllEdoHsCodeDetails  DAO ");
		}
		return result;
	}

	@Override
	public boolean isMultipleHs(String mftSeqNbr) throws BusinessException {
        String sql = "";
        boolean result = false;
        StringBuffer sb = new StringBuffer();
        SqlRowSet rs = null;
	    Map<String,String> paramMap = new HashMap<String,String>();
        try {
        	 log.info("START: isMultipleHs  DAO  START" + " mftSeqNbr:" + mftSeqNbr);
        	 sb.append("SELECT count(*) count FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_SEQ_NBR = :mftSeqNbr ");
             sql = sb.toString();
             
        	 paramMap.put("mftSeqNbr", mftSeqNbr); 
        	 
        	 log.info(" *** isMultipleHs SQL *****" + sql);
 			 log.info(" *** isMultipleHs params *****" + paramMap.toString());
 			 
	         rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
	
	          if (rs.next()) {
	         	 int count = rs.getInt(1);
	         	 if (count > 1) {
	         		result = true;
	         	 }
	          } else {
	        	  result = false;
	          }
	          
	          log.info("END: *** isMultipleHs Result *****" + result);
	        } catch (Exception e) {
	        	log.info("Exception isMultipleHs : ", e);
	        	throw new BusinessException("M4201");
	        } finally {
				log.info("END: isMultipleHs  DAO  END");
			}
        return result;
    
	}

	@Override
	public String checkIfExistMultiHsMft(String mftSeqNbr) throws BusinessException {
		 StringBuffer sb = new StringBuffer();
		 Map<String,Object> paramMap = new HashMap<String,Object>();
		try {
			log.info("START: checkIfExistMultiHsMft  DAO  START mftSeqNbr:" + mftSeqNbr);
			// check if manifest_hscode_details exist or not
			StringBuilder sbCheck = new StringBuilder();
			sbCheck.append("SELECT count(*) count FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_SEQ_NBR = :MFT_SEQ_NBR");
			paramMap.put("MFT_SEQ_NBR", mftSeqNbr);
			int countCheck = namedParameterJdbcTemplate.queryForObject(sbCheck.toString(), paramMap, Integer.class);
			if (countCheck == 0) {
				
				// get MFT_HSCODE_SEQ_NBR
				StringBuilder sbSeq = new StringBuilder();
				sbSeq.append("SELECT GBMS.SEQ_MFT_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
				Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(),new HashMap<String, String>());
				BigDecimal seqValueMft = (BigDecimal) results.get("seqVal");
				log.info("seqValueMft : " + seqValueMft);
				// end

				sb.setLength(0);
				sb.append(" INSERT INTO GBMS.MANIFEST_HSCODE_DETAILS ");
				sb.append(" (MFT_HSCODE_SEQ_NBR, MFT_SEQ_NBR, HS_CODE, CUSTOM_HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, HS_SUB_CODE_DESC, NBR_PKGS, GROSS_WT, GROSS_VOL, CRG_DES, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
				sb.append(" (SELECT :seqValueMft, m.MFT_SEQ_NBR, m.HS_CODE, m.CUSTOM_HS_CODE, m.HS_SUB_CODE_FR, m.HS_SUB_CODE_TO, h.HS_SUB_DESC, ");
				sb.append(" m.NBR_PKGS, m.GROSS_WT, m.GROSS_VOL, m.CRG_DES, m.LAST_MODIFY_USER_ID, SYSDATE ");
				sb.append(" FROM MANIFEST_DETAILS m, HS_SUB_CODE h WHERE m.MFT_SEQ_NBR = :MFT_SEQ_NBR AND h.HS_CODE(+)= m.HS_CODE AND h.HS_SUB_CODE_FR(+)= m.HS_SUB_CODE_FR AND h.HS_SUB_CODE_TO(+)= m.HS_SUB_CODE_TO )");
				paramMap.put("MFT_SEQ_NBR", mftSeqNbr);
				paramMap.put("seqValueMft", seqValueMft);
				log.info("SQL" + sb.toString());
				log.info("paramMap" + paramMap.toString());
				int countInsert = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				log.info("countInsert" + countInsert);
				
				return String.valueOf(seqValueMft);
			}
		} catch (Exception e) {
			log.info("Exception checkIfExistMultiHsMft : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkIfExistMultiHsMft  DAO  END");
		}
		return null;

	}
	
	// END CR FTZ HSCODE - NS JULY 2024
	
	// CH - 3 --> Winstar Changes Start
	@Override
	public List<EdoValueObjectCargo> getEdoByVessel(String mftSeqNbr) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		StringBuilder sb = new StringBuilder();
		List<EdoValueObjectCargo> bldetailsList = new ArrayList<EdoValueObjectCargo>();
		Map<String, String> paramMap = new HashMap<String, String>();
		log.info("START:  *** getEdoByVessel Dao Start : *** " + mftSeqNbr);
		try {
			// ++ 19.10.2009 FPT added for GB CR
			// 16.01.02

			sb.append("SELECT x.BL_NBR, x.MFT_SEQ_NBR, x.CRG_TYPE_CD, x.CRG_TYPE_NM, x.HS_CODE, x.CRG_DES, ");
			// START - CR To remove validation for volume - NS MAY 2024
			sb.append("x.HS_SUB_CODE_FR, x.HS_SUB_CODE_TO, x.CONS_CO_CD,");
			// END - CR To remove validation for volume - NS MAY 2024
			sb.append("x.CRG_STATUS, x.DG_IND, x.STG_TYPE, x.DIS_TYPE, x.PKG_TYPE_CD,");
			sb.append("x.PKG_DESC,x.NBR_PKG, x.MFT_MARKINGS, ");
			sb.append("x.CONS_NM,(x.gross_wt - nvl(y.sum1,0)) wt,(x.gross_vol - nvl(y.sum2,0)) vol, EPC_IND FROM ");
			sb.append("(SELECT  A.BL_NBR, A.MFT_SEQ_NBR, B.CRG_TYPE_CD, B.CRG_TYPE_NM, A.HS_CODE, A.CRG_DES, ");
			// START - CR To remove validation for volume - NS MAY 2024
			sb.append("D.HS_SUB_CODE_FR, D.HS_SUB_CODE_TO, A.CONS_CO_CD,");
			// END - CR To remove validation for volume - NS MAY 2024
			sb.append("A.CRG_STATUS, A.DG_IND, A.STG_TYPE, A.DIS_TYPE, C.PKG_TYPE_CD,");
			sb.append("C.PKG_DESC,A.NBR_PKGS-(A.EDO_NBR_PKGS+NVl(A.CUT_OFF_NBR_PKGS,0)) AS NBR_PKG, M.MFT_MARKINGS, ");
			sb.append("A.CONS_NM, A.GROSS_WT,A.GROSS_VOL, A.EPC_IND ");
			sb.append("FROM MANIFEST_DETAILS A, MFT_MARKINGS M, ");
			sb.append("CRG_TYPE B, PKG_TYPES C, MANIFEST_HSCODE_DETAILS D ");
			sb.append("WHERE    A.CRG_TYPE=B.CRG_TYPE_CD AND ");
			sb.append("A.PKG_TYPE=C.PKG_TYPE_CD ");
			sb.append("AND A.MFT_SEQ_NBR=M.MFT_SQ_NBR AND A.MFT_SEQ_NBR=D.MFT_SEQ_NBR");
			sb.append(") x LEFT OUTER JOIN  ");
			sb.append(
					"(SELECT edo.mft_seq_nbr, SUM(edo.nom_wt) sum1, sum(edo.nom_vol) sum2 FROM GB_EDO edo WHERE EDO_STATUS = 'A' GROUP BY edo.mft_seq_nbr) y ");
			sb.append("ON x.mft_seq_nbr = y.mft_seq_nbr ");
			sb.append("WHERE x.MFT_SEQ_NBR = :mftSeqNbr");
			sql = sb.toString();

			log.info("END: *** getBLDetails SQL *****" + sql);
			paramMap.put("mftSeqNbr", mftSeqNbr);

			log.info(" *** getBLDetails params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
			while (rs.next()) {
				String blnbr = CommonUtility.deNull(rs.getString("BL_NBR"));
				String mftseqnbr = CommonUtility.deNull(rs.getString("MFT_SEQ_NBR"));
				String crg_type_cd = CommonUtility.deNull(rs.getString("CRG_TYPE_CD"));
				String crg_type_nm = CommonUtility.deNull(rs.getString("CRG_TYPE_NM"));
				String hs_code = CommonUtility.deNull(rs.getString("HS_CODE"));
				String crg_des = CommonUtility.deNull(rs.getString("CRG_DES"));
				String mft_markings = CommonUtility.deNull(rs.getString("MFT_MARKINGS"));
				String cons_nm = CommonUtility.deNull(rs.getString("CONS_NM"));
				String crg_status = CommonUtility.deNull(rs.getString("CRG_STATUS"));
				String dg_ind = CommonUtility.deNull(rs.getString("DG_IND"));
				String stg_type = CommonUtility.deNull(rs.getString("STG_TYPE"));
				String dis_type = CommonUtility.deNull(rs.getString("DIS_TYPE"));
				String pkg_type_cd = CommonUtility.deNull(rs.getString("PKG_TYPE_CD"));
				String pkg_type_desc = CommonUtility.deNull(rs.getString("PKG_DESC"));
				String nbr_pkgs = CommonUtility.deNull(rs.getString("NBR_PKG"));
				String wt = CommonUtility.deNull(rs.getString("wt"));// Added by Thanhnv2
				String vol = CommonUtility.deNull(rs.getString("vol"));// Added by Thanhnv2
				log.info("===============EDO.BLDetail: " + wt + " - " + vol);
				String deliveryToEPC = CommonUtility.deNull(rs.getString("EPC_IND"));
				edoValueObject.setBlNbr(blnbr);
				edoValueObject.setMftSeqNbr(mftseqnbr);
				edoValueObject.setCrgTypeCd(crg_type_cd);
				edoValueObject.setCrgTypeNm(crg_type_nm);
				edoValueObject.setHsCode(hs_code);
				edoValueObject.setCrgDes(crg_des);
				edoValueObject.setMftMarkings(mft_markings);
				edoValueObject.setCrgStatus(crg_status);
				edoValueObject.setDgInd(dg_ind);
				edoValueObject.setStgType(stg_type);
				edoValueObject.setDisType(dis_type);
				edoValueObject.setPkgTypeCd(pkg_type_cd);
				edoValueObject.setPkgTypeDesc(pkg_type_desc);
				edoValueObject.setNbrPkgs(nbr_pkgs);
				edoValueObject.setCrgStatus(crg_status);
				edoValueObject.setConsNm(cons_nm);
				edoValueObject.setNomWeight(wt);// Added by Thanhnv2 for GB CR
				edoValueObject.setNomVolume(vol);// Added by Thanhnv2 for GB CR
				edoValueObject.setDeliveryToEPC(deliveryToEPC); // MCC get EPC_IND
				// START - CR To remove validation for volume - NS MAY 2024
				edoValueObject.setHsCodeFrom(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				edoValueObject.setHsCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				edoValueObject.setConsignee(CommonUtility.deNull(rs.getString("CONS_CO_CD")));
				// END - CR To remove validation for volume - NS MAY 2024
			}
			boolean ifHsCodeExist = ifHsCodeExist(mftSeqNbr);
			edoValueObject.setifHsCodeExist(ifHsCodeExist);
			List<HsCodeDetails> hsCodeDetailList = new ArrayList<HsCodeDetails>();
			hsCodeDetailList = getHsCodeDetails(mftSeqNbr);
			edoValueObject.setHsCodeDetails(hsCodeDetailList);
			if (ifHsCodeExist) {
				List<Map<String, String>> hscodeExistingList = getOptionHscodeExisting(mftSeqNbr);
				edoValueObject.sethscodeExistingList(hscodeExistingList);
			}

			bldetailsList.add(edoValueObject);

			log.info("END: *** getBLDetails Result *****" + bldetailsList.toString());
		} catch (NullPointerException e) {
			log.info("Exception getBLDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBLDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getBLDetails  END *****");
		}
		return bldetailsList;
	}

	@Override
    public List<EdoDetails> viewEdoDetailsAsn(String stredoasnnbr) throws BusinessException {
 
        List<EdoDetails> edoDetailsList = new ArrayList<>();
        Map<String, Object> paramMap = new HashMap<>();
 
        String sql = " SELECT A.BL_NBR, A.NBR_PKGS, B.NOM_WT, B.NOM_VOL, A.EPC_IND, B.DIS_TYPE FROM MANIFEST_DETAILS A, GB_EDO B "
                + "WHERE A.MFT_SEQ_NBR=B.MFT_SEQ_NBR AND B.EDO_ASN_NBR=:stredoasnnbr";
 
        log.info("viewEdoDetailsAsn() sql1 :" + sql);
 
        try {
            log.info("START: viewEdoDetailsAsn | ASN: {} :"+ stredoasnnbr);
 
            paramMap.put("stredoasnnbr", stredoasnnbr);
 
            SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
 
            while (rs.next()) {
                EdoDetails edo = new EdoDetails();
 
                edo.setAsnNbr(stredoasnnbr);
                edo.setBlnbr(CommonUtility.deNull(rs.getString("BL_NBR")));
                edo.setEdoPkg(CommonUtility.deNull(rs.getString("NBR_PKGS")));
                edo.setNominatedWt(CommonUtility.deNull(rs.getString("NOM_WT")));
                edo.setNominatedVol(CommonUtility.deNull(rs.getString("NOM_VOL")));
                edo.setDeliveryToEPC(CommonUtility.deNull(rs.getString("EPC_IND")));
                edo.setDistype(CommonUtility.deNull(rs.getString("DIS_TYPE")));
 
                edoDetailsList.add(edo);
            }
 
            log.info("END: viewEdoDetailsAsn | Result Count: "+ edoDetailsList.size());
 
        } catch (DataAccessException dae) {
            log.error("Database error in viewEdoDetailsAsn | ASN: "+ stredoasnnbr +" "+dae.getMessage());
            throw new BusinessException("M4201");
        } catch (Exception e) {
            log.error("Unexpected error in viewEdoDetailsAsn "+ e.getMessage());
            throw new BusinessException("M4201");
        }
 
        return edoDetailsList;
	// CH - 3 --> Winstar Changes End
	}
}
