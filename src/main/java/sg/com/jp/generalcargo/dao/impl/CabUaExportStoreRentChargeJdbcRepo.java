package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CabUaExportStoreRentChargeRepo;
import sg.com.jp.generalcargo.domain.GbmsCargoBillingValueObject;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("cabUaExportStoreRentChargeRepo")
public class CabUaExportStoreRentChargeJdbcRepo extends CabDataFetchJdbcRepo implements CabUaExportStoreRentChargeRepo {/**
	 * This method is used to return sql string to fetch all possible conditions
	 * 
	 * @param java.lang.String strcode
	 * @param java.lang.String struserid
	 * @exception @return java.lang.String
	 */
	
	private static final Log log = LogFactory.getLog(CabUaExportStoreRentChargeJdbcRepo.class);
	
	protected Map<String, ArrayList<?>> getSqlDetails(String strcode) {
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap1 = new HashMap<>();
		Map<String, String> paramMap2 = new HashMap<>();
		Map<String, String> paramMap3 = new HashMap<>();
		Map<String, ArrayList<?>> map = new HashMap<>();
		log.info("START getSqlDetails DAO :: strcode: " + CommonUtility.deNull(strcode));
		sb.append("select e.cargo_category_cd  cargoCategory,");
		// +" vc.scheme vesselScheme,"
		// MC Consulting - To get the Subscheme as Scheme if combi indicator is Y
		sb.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  vesselScheme, ");
		sb.append(" 'L'  cargoStatus, ''  transStatus, ed.mixed_scheme_acct_nbr  mixedSchemeAcct,");
		// +" vc.bill_acct_nbr saAcct,"
		sb.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (SELECT cust_acct.ACCT_NBR FROM CUST_ACCT, vessel_call vctemp where CUST_ACCT.cust_cd = vctemp.create_Cust_cd and vctemp.vv_cd = vc.vv_cd and CUST_ACCT.business_type = 'G' and cust_acct.acct_status_cd = 'A') ELSE vc.bill_acct_nbr END as saAcct,");
		sb.append(" ''  abAcct,  ed.acct_nbr  cargoAcct, ed.payment_Mode  paymentMode, vs.ab_cd  abCd,");
		sb.append(" 'E'  transType, v.vsl_type_cd  vesselType, e.in_voy_var_nbr  discVvCd,");
		sb.append(" e.out_voy_var_nbr  loadVvCd, ed.esn_ops_ind  opsInd, 'L'  vvInd, ''  businessType,");
		sb.append(" ''  schemeCd, ''  tariffMainCatCd, ''  tariffSubCatCd, ''  mvmt,");
		sb.append(" e.CARGO_CATEGORY_CD  type, ''  cargoType, ''  localLeg, ''  discGateway,");
		sb.append(" ''  refInd, ''  billAcctNbr, TRANS_DTTM  printDttm, ''  billInd, ''  blNbr,");
		sb.append(" ''  edoAsnNbr, bk.bk_ref_nbr  bkRefNbr, e.esn_asn_nbr  esnAsnNbr, ''  dnNbr,");
		sb.append(" ua.ua_nbr  uaNbr, ''  billTonBl, ''  billTonEdo, ''  billTonDn,");
		sb.append(" greatest (ed.esn_wt/1000, ed.esn_vol)  billTonEsn, ''  billTonBkg,");
		sb.append(" greatest (ed.esn_wt/1000, ed.esn_vol)*(bk.actual_nbr_shipped)  loadTonCs, ''  shutoutTonCs,");
		sb.append(" ed.nbr_pkgs  countUnit, ''  totalPackEdo, ''  totalPackDn, sysdate  lastModifyDttm,");
		sb.append(" 'SR' chval, vc.MIXED_SCHEME_IND MschInd, '' firstcarsch, '' edoacctnbr,");
		sb.append(
				" (select cust.tenancy_ind from vessel_call vc1,customer cust,cust_acct cact where vc1.bill_acct_nbr = cact.acct_nbr and cust.CUST_CD = cact.CUST_CD  and vc1.vv_cd=vc.vv_cd) SAtenind,");
		sb.append(
				" (select cust.tenancy_ind from customer cust,cust_acct cact where cust.CUST_CD = cact.CUST_CD  and cact.acct_nbr=ed.acct_nbr) SCtenind,");
		sb.append(" e.wh_ind whind, nvl(e.free_stg_days,0) fsdays");
		sb.append(
				" from bk_details bk, esn e, esn_details ed, vessel_call vc, vessel_scheme vs, vessel v,ua_details ua,berthing b");
		sb.append(" where  ua.esn_asn_nbr = e.esn_asn_nbr and bk.bk_ref_nbr = e.bk_ref_nbr and");
		sb.append(" e.esn_asn_nbr = ed.esn_asn_nbr and vc.vv_cd = e.out_voy_var_nbr and");
		sb.append(" vc.vv_cd = b.vv_cd and b.shift_ind = 1 and");
		sb.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and");
		sb.append(" v.vsl_nm = vc.vsl_nm and bk.cargo_type not in ('00','01','02','03') and");
		sb.append(" ed.ESN_OPS_IND not in ('O') and ua.BILL_STORE_TRIGGERED_IND = 'N' and");
		sb.append(" e.esn_status='A' and nvl(e.epc_ind,'N') = 'N' and "); // MCC Local ESN cargos are not subject to
																			// storerent if epc_ind is yes
		sb.append(" ua.ua_status='A' and bk.bk_status='A' and e.wh_ind <> 'Y' and");
		// +" round(b.atu_dttm-ed.first_trans_dttm,2) <= e.FREE_STG_DAYS and"
		sb.append(" ua.ua_nbr = :strcode");
		paramMap1.put("strcode", strcode);

		String sql1 = sb.toString();

		sb = new StringBuffer();
		sb.append("select  e.cargo_category_cd  cargoCategory,");
		// +" vc.scheme vesselScheme,"
		// MC Consulting - To get the Subscheme as Scheme if combi indicator is Y
		sb.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  vesselScheme, ");
		sb.append(" 'T'  cargoStatus, 'TS'  transStatus, ''  mixedSchemeAcct,");
		// +" vc.bill_acct_nbr saAcct,"
		sb.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (SELECT cust_acct.ACCT_NBR FROM CUST_ACCT, vessel_call vctemp where CUST_ACCT.cust_cd = vctemp.create_Cust_cd and vctemp.vv_cd = vc.vv_cd and CUST_ACCT.business_type = 'G' and cust_acct.acct_status_cd = 'A') ELSE vc.bill_acct_nbr END as saAcct,");
		sb.append(" ''  abAcct,  ed.acct_nbr  cargoAcct, ed.payment_Mode  paymentMode, vs.ab_cd  abCd,");
		sb.append(" 'A'  transType, v.vsl_type_cd  vesselType, e.in_voy_var_nbr  discVvCd,");
		sb.append(" e.out_voy_var_nbr  loadVvCd, ed.ld_ind  opsInd, 'L'  vvInd, ''  businessType,");
		sb.append(" ''  schemeCd, ''  tariffMainCatCd, ''  tariffSubCatCd, ''  mvmt,");
		sb.append(" e.CARGO_CATEGORY_CD  type, ''  cargoType, ''  localLeg, ''  discGateway,");
		sb.append(" ''  refInd, ''  billAcctNbr, TRANS_DTTM  printDttm, ''  billInd, ''  blNbr,");
		sb.append(" ''  edoAsnNbr, bk.bk_ref_nbr  bkRefNbr, e.esn_asn_nbr  esnAsnNbr, ''  dnNbr,");
		sb.append(" ua.ua_nbr  uaNbr, ''  billTonBl, ''  billTonEdo, ''  billTonDn,");
		sb.append(" greatest (ed.nom_wt/1000, ed.nom_vol)  billTonEsn, ''  billTonBkg,");
		sb.append(" greatest (ed.nom_wt/1000, ed.nom_vol)*(bk.actual_nbr_shipped)  loadTonCs, ''  shutoutTonCs,");
		sb.append(" ed.nbr_pkgs  countUnit, ''  totalPackEdo, ''  totalPackDn, sysdate  lastModifyDttm,");
		sb.append(" 'SR' chval, vc.MIXED_SCHEME_IND MschInd,");
		sb.append(
				" (select CASE WHEN vc1.COMBI_GC_OPS_IND ='Y' THEN vc1.COMBI_GC_SCHEME  ELSE vc1.SCHEME END from vessel_call vc1,gb_edo ge1 where ge1.edo_asn_nbr = ed.edo_asn_nbr and vc1.vv_cd = ge1.var_nbr and ge1.payment_mode in ('A')) firstcarsch,");
		sb.append(
				" (select ge1.acct_nbr from gb_edo ge1 where ge1.edo_asn_nbr = ed.edo_asn_nbr and ge1.payment_mode in ('A')) edoacctnbr,");
		sb.append(
				" (select cust.tenancy_ind from vessel_call vc1,customer cust,cust_acct cact where vc1.bill_acct_nbr = cact.acct_nbr and cust.CUST_CD = cact.CUST_CD  and vc1.vv_cd=vc.vv_cd) SAtenind,");
		sb.append(
				" (select cust.tenancy_ind from customer cust,cust_acct cact where cust.CUST_CD = cact.CUST_CD  and cact.acct_nbr=ed.acct_nbr) SCtenind,");
		sb.append(" e.wh_ind whind, nvl(e.free_stg_days,0) fsdays");
		sb.append(
				" from bk_details bk, esn e, tesn_jp_jp ed, manifest_details md, gb_edo edo, vessel_call vc, vessel_scheme vs, vessel v, ua_details ua, berthing b");
		sb.append(" where  ua.esn_asn_nbr = e.esn_asn_nbr and bk.bk_ref_nbr = e.bk_ref_nbr and");
		sb.append(" e.esn_asn_nbr = ed.esn_asn_nbr and vc.vv_cd = e.out_voy_var_nbr and");
		sb.append(" vc.vv_cd = b.vv_cd and b.shift_ind = 1 and v.vsl_nm = vc.vsl_nm and");
		sb.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and");
		sb.append(" bk.cargo_type not in ('00','01','02','03') and ed.LD_IND not in ('O') and");
		sb.append(" ua.BILL_STORE_TRIGGERED_IND = 'N' and e.esn_status='A' and bk.bk_status='A' and");
		sb.append(" ua.ua_status='A' and ed.edo_asn_nbr = edo.edo_asn_nbr and");
		sb.append(" md.mft_seq_nbr = edo.mft_seq_nbr and e.wh_ind <> 'Y' and");
		// +" round(b.atu_dttm-ed.first_trans_dttm,2) <= e.FREE_STG_DAYS and"
		sb.append(" ua.ua_nbr = :strcode ");
		paramMap2.put("strcode", strcode);
		String sql2 = sb.toString();

		sb = new StringBuffer();
		sb.append("select e.cargo_category_cd  cargoCategory,");
		// +" vc.scheme vesselScheme,"
		// MC Consulting - To get the Subscheme as Scheme if combi indicator is Y
		sb.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  vesselScheme, ");
		sb.append(" 'T'  cargoStatus, 'IT'  transStatus, ''  mixedSchemeAcct,");
		// +" vc.bill_acct_nbr saAcct,"
		sb.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (SELECT cust_acct.ACCT_NBR FROM CUST_ACCT, vessel_call vctemp where CUST_ACCT.cust_cd = vctemp.create_Cust_cd and vctemp.vv_cd = vc.vv_cd and CUST_ACCT.business_type = 'G' and cust_acct.acct_status_cd = 'A') ELSE vc.bill_acct_nbr END AS saAcct,");
		sb.append(" ''  abAcct,  ed.acct_nbr  cargoAcct, ed.payment_Mode  paymentMode, vs.ab_cd  abCd,");
		sb.append(" v.vsl_type_cd  vesselType, 'C'  transType, e.in_voy_var_nbr  discVvCd,");
		sb.append(" e.out_voy_var_nbr  loadVvCd, ed.ops_ind  opsInd, 'L'  vvInd, ''  businessType,");
		sb.append(" ''  schemeCd, ''  tariffMainCatCd, ''  tariffSubCatCd, ''  mvmt,");
		sb.append(" e.CARGO_CATEGORY_CD  type, ''  cargoType, ''  localLeg, ''  discGateway,");
		sb.append(" ''  refInd, ''  billAcctNbr, TRANS_DTTM  printDttm, ''  billInd, ''  blNbr,");
		sb.append(" ''  edoAsnNbr, bk.bk_ref_nbr  bkRefNbr, e.esn_asn_nbr  esnAsnNbr, ''  dnNbr,");
		sb.append(" ua.ua_nbr  uaNbr, ''  billTonBl, ''  billTonEdo, ''  billTonDn,");
		sb.append(" greatest (ed.gross_wt/1000, ed.gross_vol)  billTonEsn, ''  billTonBkg,");
		sb.append(" greatest (ed.gross_wt/1000, ed.gross_vol)*(bk.actual_nbr_shipped)  loadTonCs,");
		sb.append(" ''  shutoutTonCs, ed.nbr_pkgs  countUnit, ''  totalPackEdo, ''  totalPackDn,");
		sb.append(" sysdate  lastModifyDttm, 'SR' chval, vc.MIXED_SCHEME_IND MschInd, '' firstcarsch,");
		sb.append(" '' edoacctnbr,");
		sb.append(
				" (select cust.tenancy_ind from vessel_call vc1,customer cust,cust_acct cact where vc1.bill_acct_nbr = cact.acct_nbr and cust.CUST_CD = cact.CUST_CD  and vc1.vv_cd=vc.vv_cd) SAtenind,");
		sb.append(
				" (select cust.tenancy_ind from customer cust,cust_acct cact where cust.CUST_CD = cact.CUST_CD  and cact.acct_nbr=ed.acct_nbr) SCtenind,");
		sb.append(" e.wh_ind whind, nvl(e.free_stg_days,0) fsdays");
		sb.append(
				" from bk_details bk, esn e, tesn_psa_jp ed, vessel_call vc, vessel_scheme vs,vessel v, ua_details ua,berthing b");
		sb.append(" where ua.esn_asn_nbr = e.esn_asn_nbr and bk.bk_ref_nbr = e.bk_ref_nbr and");
		sb.append(" e.esn_asn_nbr = ed.esn_asn_nbr and v.vsl_nm = vc.vsl_nm and");
		sb.append(" vc.vv_cd = e.out_voy_var_nbr and vc.vv_cd = b.vv_cd and b.shift_ind = 1 and");
		sb.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and");
		sb.append(" bk.cargo_type not in ('00','01','02','03') and ed.OPS_IND not in ('O') and");
		sb.append(" ua.BILL_STORE_TRIGGERED_IND = 'N' and e.esn_status='A' and bk.bk_status='A' and");
		sb.append(" ua.ua_status='A' and e.wh_ind <> 'Y' and");
		// +" round(b.atu_dttm-ed.first_trans_dttm,2) <= e.FREE_STG_DAYS and"
		sb.append(" ua.ua_nbr = :strcode");
		paramMap3.put("strcode", strcode);
		String sql3 = sb.toString();

		/*
		 * java.lang.String sql4="select " +" e.cargo_category_cd  cargoCategory,"
		 * +" vc.scheme  vesselScheme," +" 'L'  cargoStatus," +" ''  transStatus,"
		 * +" ''  mixedSchemeAcct," +" vc.bill_acct_nbr  saAcct," +" ''  abAcct, "
		 * +" ed.acct_nbr  cargoAcct," +" ed.payment_Mode  paymentMode,"
		 * +" vs.ab_cd  abCd," +" v.vsl_type_cd  vesselType," +" 'S'  transType,"
		 * +" e.in_voy_var_nbr  discVvCd," +" e.out_voy_var_nbr  loadVvCd,"
		 * +" 'N'  opsInd," +" 'L'  vvInd," +" ''  businessType," +" ''  schemeCd,"
		 * +" ''  tariffMainCatCd," +" ''  tariffSubCatCd," +" ''  mvmt,"
		 * +" e.CARGO_CATEGORY_CD  type," +" ''  cargoType," +" ''  localLeg,"
		 * +" ''  discGateway," +" ''  refInd," +" ''  billAcctNbr,"
		 * +" TRANS_DTTM  printDttm," +" ''  billInd," +" ''  blNbr," +" ''  edoAsnNbr,"
		 * +" ''  bkRefNbr," +" e.esn_asn_nbr  esnAsnNbr," +" ''  dnNbr,"
		 * +" ua.ua_nbr  uaNbr," +" ''  billTonBl," +" ''  billTonEdo,"
		 * +" ''  billTonDn," +" greatest (ed.ss_wt/1000, ed.ss_vol)  billTonEsn,"
		 * +" ''  billTonBkg," +" 0  loadTonCs," +" ''  shutoutTonCs,"
		 * +" ed.nbr_pkgs  countUnit," +" ''  totalPackEdo," +" ''  totalPackDn,"
		 * +" sysdate  lastModifyDttm," +" 'SR' chval," +" vc.MIXED_SCHEME_IND MschInd,"
		 * +" '' firstcarsch," +" '' edoacctnbr,"
		 * +" (select cust.tenancy_ind from vessel_call vc1,customer cust,cust_acct cact where vc1.bill_acct_nbr = cact.acct_nbr and cust.CUST_CD = cact.CUST_CD  and vc1.vv_cd=vc.vv_cd) SAtenind,"
		 * +" (select cust.tenancy_ind from customer cust,cust_acct cact where cust.CUST_CD = cact.CUST_CD  and cact.acct_nbr=ed.acct_nbr) SCtenind,"
		 * +" e.wh_ind whind," +" nvl(e.free_stg_days,0) fsdays"
		 * +" from esn e, ss_details ed, vessel_call vc, vessel_scheme vs, vessel v, ua_details ua,berthing b"
		 * +" where" +" ua.esn_asn_nbr = e.esn_asn_nbr and"
		 * +" e.esn_asn_nbr = ed.esn_asn_nbr and" +" vc.vv_cd = e.out_voy_var_nbr and"
		 * +" vc.vv_cd = b.vv_cd and" +" b.shift_ind = 1 and"
		 * +" v.vsl_nm = vc.vsl_nm and" +" vc.scheme = vs.scheme_cd and"
		 * +" ua.BILL_STORE_TRIGGERED_IND = 'N' and" +" e.esn_status='A' and"
		 * +" ua.ua_status='A' and" //
		 * +" round(b.atu_dttm-ed.first_trans_dttm,2) <= e.FREE_STG_DAYS and"
		 * +" ua.ua_nbr = '"+strcode+"'";
		 */
		ArrayList<String> sqlarraylist = new ArrayList<>();
		sqlarraylist.add(sql1);
		sqlarraylist.add(sql2);
		sqlarraylist.add(sql3);
		// sqlarraylist.add(sql4);
		
		ArrayList<Map<String, String>> maparraylist = new ArrayList<>();
		maparraylist.add(paramMap1);
		maparraylist.add(paramMap2);
		maparraylist.add(paramMap3);
		
		map.put("sql", sqlarraylist);
		map.put("paramMap", maparraylist);
		log.info("END getSqlDetails DAO  map: " + map);
		return map;
	}

	// [Spr001
	/**
	 * This method is used to return sql string for particular condition
	 * 
	 * @param GbmsCargoBillingValueObject gbmsCargoBillingValueObject
	 * @exception @return java.util.Map
	 */
	protected Map<String, Object> compareSqlDetails(GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		log.info("START compareSqlDetails DAO :: strcode: " + CommonUtility.deNull(xGbmsCargoBillingValueObject.toString()));
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<>();
		Map<String, Object> map = new HashMap<>();
		String zszSqlCompare = "";
		String zszScheme = new String(xGbmsCargoBillingValueObject.getVesselScheme());
		String zszVesselType = new String(xGbmsCargoBillingValueObject.getVesselType());
		String zszSubCat = new String(xGbmsCargoBillingValueObject.getSubCat());
		String zszTransType = new String(xGbmsCargoBillingValueObject.getTransType());
		String zszPaymentMode = new String(xGbmsCargoBillingValueObject.getPaymentMode());
		String zszOpsInd = new String(xGbmsCargoBillingValueObject.getOpsInd());
		String zszType = new String(xGbmsCargoBillingValueObject.getType());

		if ((xGbmsCargoBillingValueObject.getChType()).equals("SR"))
			zszType = "TN";

		/*
		 * if((zszScheme.equals("JNL") || zszScheme.equals("JBT")) &&
		 * (zszType.equals("LS") || zszType.equals("WA") || zszType.equals("01") ||
		 * zszType.equals("02") || zszType.equals("03"))) { zszType = "00"; zszSubCat =
		 * "GL"; }
		 * 
		 * if((zszTransType.equals("A")|| zszTransType.equals("C")) &&
		 * (zszType.equals("LS") || zszType.equals("WA"))) { zszType = "00"; zszSubCat =
		 * "GL"; }
		 */

		// zszType = deriveType(xGbmsCargoBillingValueObject.getType(), zszType);

		sb.append("SELECT  UA.DISC_VV_CD, UA.LOAD_VV_CD, ");
		sb.append("UA.VV_IND, UA.BUSINESS_TYPE, UA.SCHEME_CD, UA.TARIFF_MAIN_CAT_CD, ");
		sb.append("UA.TARIFF_SUB_CAT_CD, UA.MVMT, UA.TYPE, UA.CARGO_TYPE, UA.LOCAL_LEG, ");
		sb.append("UA.DISC_GATEWAY, UA.REF_IND, UA.BL_NBR, UA.EDO_ASN_NBR, ");
		sb.append("UA.BK_REF_NBR,  UA.ESN_ASN_NBR, UA.DN_NBR, UA.UA_NBR, UA.BILL_TON_BL, ");
		sb.append("UA.BILL_TON_EDO, UA.BILL_TON_DN, UA.BILL_TON_ESN, UA.BILL_TON_BKG, ");
		sb.append("UA.LOAD_TON_CS, UA.SHUTOUT_TON_CS, UA.COUNT_UNIT, UA.TOTAL_PACK_EDO, ");
		sb.append("UA.TOTAL_PACK_DN, UA.BILL_ACCT_NBR FROM UA_STORE_SCENARIO_CODE UA WHERE SCHEME IN (:zszScheme");
		sb.append(", 'X') AND VESSEL_TYPE IN  (:zszVesselType, 'X') AND SUB_CAT IN (:zszSubCat");
		sb.append(", 'X') AND TRANS_TYPE LIKE :zszTransType AND PAYMENT IN (:zszPaymentMode");
		sb.append(", 'X') AND OPS_IND LIKE :zszOpsInd AND TYPE = :zszType");
		zszSqlCompare = sb.toString();
		paramMap.put("zszScheme", zszScheme);
		paramMap.put("zszVesselType", zszVesselType);
		paramMap.put("zszSubCat", zszSubCat);
		paramMap.put("zszTransType", "%" + zszTransType + "%");
		paramMap.put("zszPaymentMode", zszPaymentMode);
		paramMap.put("zszOpsInd", "%" + zszOpsInd + "%");
		paramMap.put("zszType", zszType);
		map.put("sql", zszSqlCompare);
		map.put("paramMap", paramMap);
		log.info("END compareSqlDetails DAO  map: " + map);
		return map;
	}
	// Spr001]
}
