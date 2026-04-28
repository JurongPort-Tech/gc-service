package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CabUaExportServiceChargeRepo;
import sg.com.jp.generalcargo.domain.GbmsCargoBillingValueObject;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("cabUaExportServiceChargeRepo")
public class CabUaExportServiceChargeJdbcRepo extends CabDataFetchJdbcRepo implements CabUaExportServiceChargeRepo {

	private static final Log log = LogFactory.getLog(CabUaExportServiceChargeJdbcRepo.class);

	/**
	 * This method is used to return sql string to fetch all possible conditions
	 * 
	 * @param String strcode
	 * @param String struserid
	 * @exception @return String
	 */
	protected Map<String, ArrayList<?>> getSqlDetails(String strcode) {
		log.info("START getSqlDetails DAO :: strcode: " + CommonUtility.deNull(strcode));
		StringBuffer query = new StringBuffer();
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		Map<String, String> paramMap1 = new HashMap<>();
		Map<String, String> paramMap2 = new HashMap<>();
		Map<String, String> paramMap3 = new HashMap<>();
		Map<String, String> paramMap4 = new HashMap<>();
		Map<String, ArrayList<?>> map = new HashMap<>();

		query.setLength(0);
		query.append("select e.cargo_category_cd  cargoCategory,");
		// +" vc.scheme vesselScheme,"
		// MC Consulting - To get the Subscheme as Scheme if combi indicator is Y
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  vesselScheme, ");
		query.append(" 'L'  cargoStatus, '' transStatus, ed.mixed_scheme_acct_nbr  mixedSchemeAcct,");
		// +" vc.bill_acct_nbr saAcct,"
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (SELECT cust_acct.ACCT_NBR FROM CUST_ACCT, vessel_call vctemp where CUST_ACCT.cust_cd = vctemp.create_Cust_cd and vctemp.vv_cd = vc.vv_cd and CUST_ACCT.business_type = 'G' and cust_acct.acct_status_cd = 'A') ELSE vc.bill_acct_nbr END as saAcct,");
		query.append(" '' abAcct,  ed.acct_nbr  cargoAcct, ed.payment_Mode  paymentMode, vs.ab_cd  abCd,");
		query.append(" 'E'  transType, v.vsl_type_cd  vesselType, e.in_voy_var_nbr  discVvCd,");
		query.append(" e.out_voy_var_nbr  loadVvCd, ed.esn_ops_ind  opsInd, 'L'  vvInd, '' businessType,");
		query.append(" '' schemeCd, '' tariffMainCatCd, '' tariffSubCatCd, '' mvmt,");
		query.append(" e.CARGO_CATEGORY_CD  type, '' cargoType, '' localLeg,  '' discGateway,");
		query.append(" '' refInd, '' billAcctNbr, TRANS_DTTM  printDttm, '' billInd, '' blNbr,");
		query.append("  '' edoAsnNbr, bk.bk_ref_nbr  bkRefNbr, e.esn_asn_nbr  esnAsnNbr, '' dnNbr,");
		query.append(" ua.ua_nbr  uaNbr, '' billTonBl, '' billTonEdo, '' billTonDn,");
		query.append(" greatest (ed.esn_wt/1000, ed.esn_vol)  billTonEsn, '' billTonBkg,");
		query.append(" greatest (ed.esn_wt/1000, ed.esn_vol)*(bk.actual_nbr_shipped)  loadTonCs, '' shutoutTonCs,");
		query.append(" ed.nbr_pkgs  countUnit, '' totalPackEdo, '' totalPackDn, sysdate  lastModifyDttm,");
		query.append(" 'SC' chval, vc.MIXED_SCHEME_IND MschInd, '' firstcarsch, '' edoacctnbr,");
		query.append(" '' SAtenind, '' SCtenind, '' whind, '' fsdays");
		query.append(
				" from bk_details bk, esn e, esn_details ed, vessel_call vc, vessel_scheme vs, vessel v,ua_details ua");
		query.append(" where ua.esn_asn_nbr = e.esn_asn_nbr and bk.bk_ref_nbr = e.bk_ref_nbr and");
		query.append(" e.esn_asn_nbr = ed.esn_asn_nbr and vc.vv_cd = e.out_voy_var_nbr and");
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and");
		query.append(" v.vsl_nm = vc.vsl_nm and bk.cargo_type not in ('00','01','02','03') and");
		// +" ed.ESN_OPS_IND not in ('O') and"
		query.append(" ua.bill_service_triggered_ind = 'N' and e.esn_status='A' and ua.ua_status='A' and");
		query.append(" bk.bk_status='A'and ua.ua_nbr = :strcode");
		sql1 = query.toString();
		paramMap1.put("strcode", strcode);

		query.setLength(0);
		query.append("select e.cargo_category_cd  cargoCategory,");
		// +" vc.scheme vesselScheme,"
		// MC Consulting - To get the Subscheme as Scheme if combi indicator is Y
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  vesselScheme, ");
		query.append(" 'T'  cargoStatus, 'TS'  transStatus, '' mixedSchemeAcct,");
		// +" vc.bill_acct_nbr saAcct,"
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (SELECT cust_acct.ACCT_NBR FROM CUST_ACCT, vessel_call vctemp where CUST_ACCT.cust_cd = vctemp.create_Cust_cd and vctemp.vv_cd = vc.vv_cd and CUST_ACCT.business_type = 'G' and cust_acct.acct_status_cd = 'A') ELSE vc.bill_acct_nbr END As saAcct,");
		query.append(" '' abAcct, ");
		// Amended by Jade for SL-CAB-20150303-01
		// +" ed.acct_nbr cargoAcct,"
		query.append(" decode(ed.payment_mode, 'C', 'CA', ed.acct_nbr) cargoAcct,");
		// End of amendment by Jade for SL-CAB-20150303-01
		query.append(" ed.payment_Mode  paymentMode, vs.ab_cd  abCd, 'A'  transType,");
		query.append(" v.vsl_type_cd  vesselType, e.in_voy_var_nbr  discVvCd, e.out_voy_var_nbr  loadVvCd,");
		query.append(" ed.ld_ind  opsInd, 'L'  vvInd, ''  businessType, ''  schemeCd,");
		query.append(" '' tariffMainCatCd, '' tariffSubCatCd, '' mvmt, e.CARGO_CATEGORY_CD  type,");
		query.append(" '' cargoType, '' localLeg, '' discGateway, '' refInd, '' billAcctNbr,");
		query.append(" TRANS_DTTM  printDttm, '' billInd, '' blNbr, '' edoAsnNbr,");
		query.append(" bk.bk_ref_nbr  bkRefNbr, e.esn_asn_nbr  esnAsnNbr, '' dnNbr, ua.ua_nbr  uaNbr,");
		query.append(" '' billTonBl, '' billTonEdo, '' billTonDn,");
		query.append(" greatest (ed.nom_wt/1000, ed.nom_vol)  billTonEsn, '' billTonBkg,");
		query.append(" greatest (ed.nom_wt/1000, ed.nom_vol)*(bk.actual_nbr_shipped)  loadTonCs, '' shutoutTonCs,");
		query.append(" ed.nbr_pkgs  countUnit, '' totalPackEdo, '' totalPackDn, sysdate  lastModifyDttm,");
		query.append(" 'SC' chval, vc.MIXED_SCHEME_IND MschInd,");
		query.append(
				" (select CASE WHEN vc1.COMBI_GC_OPS_IND ='Y' THEN vc1.COMBI_GC_SCHEME  ELSE vc1.SCHEME END from vessel_call vc1,gb_edo ge1 where ge1.edo_asn_nbr = ed.edo_asn_nbr and vc1.vv_cd = ge1.var_nbr and ge1.payment_mode in ('A')) firstcarsch,");
		query.append(
				" (select ge1.acct_nbr from gb_edo ge1 where ge1.edo_asn_nbr = ed.edo_asn_nbr and ge1.payment_mode in ('A')) edoacctnbr,");
		query.append(" '' SAtenind, '' SCtenind, '' whind, '' fsdays");
		query.append(
				" from bk_details bk, esn e, tesn_jp_jp ed, manifest_details md, gb_edo edo, vessel_call vc, vessel_scheme vs, vessel v, ua_details ua");
		query.append(" where " + " ua.esn_asn_nbr = e.esn_asn_nbr and bk.bk_ref_nbr = e.bk_ref_nbr and");
		query.append(" e.esn_asn_nbr = ed.esn_asn_nbr and vc.vv_cd = e.out_voy_var_nbr and");
		query.append(" v.vsl_nm = vc.vsl_nm and");
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and");
		query.append(" bk.cargo_type not in ('00','01','02','03') and");
		// +" ed.LD_IND not in ('O') and"
		query.append(" ua.bill_service_triggered_ind = 'N' and e.esn_status='A' and bk.bk_status='A' and");
		query.append(" ua.ua_status='A' and  ed.edo_asn_nbr = edo.edo_asn_nbr and");
		query.append(" md.mft_seq_nbr = edo.mft_seq_nbr  and ua.ua_nbr = :strcode");
		sql2 = query.toString();
		paramMap2.put("strcode", strcode);

		query.setLength(0);
		query.append("select e.cargo_category_cd  cargoCategory,");
		// +" vc.scheme vesselScheme,"
		// MC Consulting - To get the Subscheme as Scheme if combi indicator is Y
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  vesselScheme, ");
		query.append(" 'T'  cargoStatus," + " 'IT'  transStatus, '' mixedSchemeAcct,");
		// +" vc.bill_acct_nbr saAcct,"
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (SELECT cust_acct.ACCT_NBR FROM CUST_ACCT, vessel_call vctemp where CUST_ACCT.cust_cd = vctemp.create_Cust_cd and vctemp.vv_cd = vc.vv_cd and CUST_ACCT.business_type = 'G' and cust_acct.acct_status_cd = 'A') ELSE vc.bill_acct_nbr END as saAcct,");
		query.append(" '' abAcct,  ed.acct_nbr  cargoAcct, ed.payment_Mode  paymentMode, vs.ab_cd  abCd,");
		query.append(" v.vsl_type_cd  vesselType, 'C'  transType, e.in_voy_var_nbr  discVvCd,");
		query.append(" e.out_voy_var_nbr  loadVvCd, ed.ops_ind  opsInd, 'L'  vvInd, '' businessType,");
		query.append(" '' schemeCd, '' tariffMainCatCd, '' tariffSubCatCd, '' mvmt,");
		query.append(" e.CARGO_CATEGORY_CD  type, '' cargoType, '' localLeg, '' discGateway,");
		query.append("  '' refInd, '' billAcctNbr, TRANS_DTTM  printDttm, '' billInd, '' blNbr,");
		query.append(" '' edoAsnNbr, bk.bk_ref_nbr  bkRefNbr, e.esn_asn_nbr  esnAsnNbr, ''  dnNbr,");
		query.append(" ua.ua_nbr  uaNbr, ''  billTonBl, '' billTonEdo, '' billTonDn,");
		query.append(" greatest (ed.gross_wt/1000, ed.gross_vol)  billTonEsn, '' billTonBkg,");
		query.append(" greatest (ed.gross_wt/1000, ed.gross_vol)*(bk.actual_nbr_shipped)  loadTonCs,");
		query.append("  '' shutoutTonCs, ed.nbr_pkgs  countUnit, ''  totalPackEdo, ''  totalPackDn,");
		query.append(" sysdate  lastModifyDttm, 'SC' chval, vc.MIXED_SCHEME_IND MschInd, '' firstcarsch,");
		query.append(" '' edoacctnbr, '' SAtenind, '' SCtenind, '' whind, '' fsdays");
		query.append(
				" from bk_details bk, esn e, tesn_psa_jp ed, vessel_call vc, vessel_scheme vs,vessel v, ua_details ua");
		query.append(" where ua.esn_asn_nbr = e.esn_asn_nbr and bk.bk_ref_nbr = e.bk_ref_nbr and");
		query.append(" e.esn_asn_nbr = ed.esn_asn_nbr and v.vsl_nm = vc.vsl_nm and");
		query.append(" vc.vv_cd = e.out_voy_var_nbr and");
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and");
		query.append(" bk.cargo_type not in ('00','01','02','03') and");
		// +" ed.OPS_IND not in ('O') and"
		query.append(" ua.bill_service_triggered_ind = 'N' and e.esn_status='A' and bk.bk_status='A' and");
		query.append(" ua.ua_status='A' and ua.ua_nbr = :strcode");
		sql3 = query.toString();
		paramMap3.put("strcode", strcode);

		query.setLength(0);
		query.append(" select e.cargo_category_cd  cargoCategory,");
		// +" vc.scheme vesselScheme,"
		// MC Consulting - To get the Subscheme as Scheme if combi indicator is Y
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  vesselScheme, ");
		query.append(" 'L'  cargoStatus, ''  transStatus, '' mixedSchemeAcct,");
		// +" vc.bill_acct_nbr saAcct,"
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (SELECT cust_acct.ACCT_NBR FROM CUST_ACCT, vessel_call vctemp where CUST_ACCT.cust_cd = vctemp.create_Cust_cd and vctemp.vv_cd = vc.vv_cd and CUST_ACCT.business_type = 'G' and cust_acct.acct_status_cd = 'A') ELSE vc.bill_acct_nbr END as saAcct,");
		query.append(" '' abAcct, ed.acct_nbr  cargoAcct, ed.payment_Mode  paymentMode, vs.ab_cd  abCd,");
		query.append(" v.vsl_type_cd  vesselType, 'S'  transType, e.in_voy_var_nbr  discVvCd,");
		query.append(" e.out_voy_var_nbr  loadVvCd, 'N'  opsInd,'L'  vvInd, '' businessType,");
		query.append(" '' schemeCd, '' tariffMainCatCd, '' tariffSubCatCd, '' mvmt,");
		query.append(" e.CARGO_CATEGORY_CD  type, '' cargoType, '' localLeg, '' discGateway,");
		query.append(" '' refInd, '' billAcctNbr, TRANS_DTTM  printDttm, ''  billInd, ''  blNbr,");
		query.append(" '' edoAsnNbr, '' bkRefNbr, e.esn_asn_nbr  esnAsnNbr, ''  dnNbr,");
		query.append(" ua.ua_nbr  uaNbr, '' billTonBl, ''  billTonEdo, ''  billTonDn,");
		query.append(" greatest (ed.ss_wt/1000, ed.ss_vol)  billTonEsn, ''  billTonBkg, 0  loadTonCs,");
		query.append(" '' shutoutTonCs, ed.nbr_pkgs  countUnit, '' totalPackEdo, '' totalPackDn,");
		query.append(
				" sysdate  lastModifyDttm, 'SC' chval,vc.MIXED_SCHEME_IND MschInd, '' firstcarsch,");
		query.append(" '' edoacctnbr, '' SAtenind, '' SCtenind, '' whind, '' fsdays");
		query.append(
				" from esn e, ss_details ed, vessel_call vc, vessel_scheme vs, vessel v, ua_details ua where");
		query.append(" ua.esn_asn_nbr = e.esn_asn_nbr and e.esn_asn_nbr = ed.esn_asn_nbr and");
		query.append(" vc.vv_cd = e.out_voy_var_nbr and v.vsl_nm = vc.vsl_nm and");
		query.append(
				" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and");
		query.append(" ua.bill_service_triggered_ind = 'N' and e.esn_status='A' and ua.ua_status='A' ");
		query.append(" and ua.ua_nbr = :strcode");
		sql4 = query.toString();
		paramMap4.put("strcode", strcode);
		ArrayList<String> sqlarraylist = new ArrayList<String>();
		sqlarraylist.add(sql1);
		sqlarraylist.add(sql2);
		sqlarraylist.add(sql3);
		sqlarraylist.add(sql4);
		
		ArrayList<Map<String, String>> maparraylist = new ArrayList<>();
		maparraylist.add(paramMap1);
		maparraylist.add(paramMap2);
		maparraylist.add(paramMap3);
		maparraylist.add(paramMap4);
		
		map.put("sql", sqlarraylist);
		map.put("paramMap", maparraylist);
		log.info("END getSqlDetails  map: " + map);
		return map;
	}

//[Spr001
	/**
	 * This method is used to return sql string for particular condition
	 * 
	 * @param GbmsCargoBillingValueObject gbmsCargoBillingValueObject
	 * @exception @return String
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
		String zszMvmt = xGbmsCargoBillingValueObject.getMvmt();

		/*
		 * comment by ducta1 //added by Balaji (03rd jan 2003)
		 * if((zszScheme.equals("JNL") || zszScheme.equals("JBT")) && (
		 * zszType.equals("01") || zszType.equals("02") || zszType.equals("03"))) {
		 * zszType = "00"; zszSubCat = "GL"; } //end added by Balaji (03rd jan 2003)
		 */

		// ducta1 start on 26/12/2008
		if ((zszScheme.equals("JNL") || zszScheme.equals("JBT") || zszScheme.equals(ProcessChargeConst.LCT_SCHEME))
				&& (zszType.equals("01") || zszType.equals("02") || zszType.equals("03"))
				&& !zszSubCat.equalsIgnoreCase("RO")) {
			zszType = "00";
			zszSubCat = "GL";
		}
		if (zszOpsInd.equals("N") && (!(zszType.equals("LS") || zszType.equals("WA")))
				&& !((zszType.equals("01") || zszType.equals("02") || zszType.equals("03"))
						&& zszSubCat.equalsIgnoreCase("RO"))) {

			zszType = "00";
		}

		// ducta1 end
		// added by Balaji (28 march 2003)
		/*
		 * if((zszTransType.equals("A")|| zszTransType.equals("C")) &&
		 * (zszType.equals("LS") || zszType.equals("WA"))) { zszType = "00"; zszSubCat =
		 * "GL"; }
		 */

		/*
		 * comment by ducta1
		 * 
		 * if(zszOpsInd.equals("N") && (!(zszType.equals("LS") ||
		 * zszType.equals("WA")))) { zszType = "00"; }
		 */
		// end added by Balaji (28 march 2003)

		if (GbmsCommonUtility.isVehicleRORO(zszType, zszSubCat)) {
			// SQL for RORO vessel which has mvmt in search criteria
			zszType = deriveType(xGbmsCargoBillingValueObject.getType(), zszType);
			sb.append("SELECT  UA.DISC_VV_CD, UA.LOAD_VV_CD,");
			sb.append("UA.VV_IND, UA.BUSINESS_TYPE, UA.SCHEME_CD, UA.TARIFF_MAIN_CAT_CD,");
			sb.append("UA.TARIFF_SUB_CAT_CD, UA.MVMT, UA.TYPE, UA.CARGO_TYPE, UA.LOCAL_LEG,");
			sb.append("UA.DISC_GATEWAY, UA.REF_IND, UA.BL_NBR, UA.EDO_ASN_NBR,");
			sb.append("UA.BK_REF_NBR,  UA.ESN_ASN_NBR, UA.DN_NBR, UA.UA_NBR, UA.BILL_TON_BL,");
			sb.append("UA.BILL_TON_EDO, UA.BILL_TON_DN, UA.BILL_TON_ESN, UA.BILL_TON_BKG,");
			sb.append("UA.LOAD_TON_CS, UA.SHUTOUT_TON_CS, UA.COUNT_UNIT, UA.TOTAL_PACK_EDO,");
			sb.append("UA.TOTAL_PACK_DN, UA.BILL_ACCT_NBR FROM UA_SERVICE_SCENARIO_CODE UA WHERE SCHEME IN (");
			sb.append(":zszScheme, 'X') AND VESSEL_TYPE IN  (:zszVesselType, 'X') AND SUB_CAT IN (");
			sb.append(":zszSubCat, 'X') AND TRANS_TYPE LIKE :zszTransType AND PAYMENT IN (");
			sb.append(":zszPaymentMode, 'X') AND OPS_IND LIKE :zszOpsInd AND TYPE = :zszType ");
			sb.append(" AND MVMT = :zszMvmt");
			paramMap.put("zszScheme", zszScheme);
			paramMap.put("zszVesselType", zszVesselType);
			paramMap.put("zszSubCat", zszSubCat);
			paramMap.put("zszTransType", "%" + zszTransType + "%");
			paramMap.put("zszPaymentMode", zszPaymentMode);
			paramMap.put("zszOpsInd", "%" + zszOpsInd + "%");
			paramMap.put("zszType", zszType);
			paramMap.put("zszMvmt", zszMvmt);
			zszSqlCompare = sb.toString();
			map.put("sql", zszSqlCompare);
			map.put("paramMap", paramMap);

		} else {
			zszType = deriveType(xGbmsCargoBillingValueObject.getType(), zszType);
			sb.append("SELECT  UA.DISC_VV_CD, UA.LOAD_VV_CD, ");
			sb.append("UA.VV_IND, UA.BUSINESS_TYPE, UA.SCHEME_CD, UA.TARIFF_MAIN_CAT_CD, ");
			sb.append("UA.TARIFF_SUB_CAT_CD, UA.MVMT, UA.TYPE, UA.CARGO_TYPE, UA.LOCAL_LEG, ");
			sb.append("UA.DISC_GATEWAY, UA.REF_IND, UA.BL_NBR, UA.EDO_ASN_NBR, ");
			sb.append("UA.BK_REF_NBR,  UA.ESN_ASN_NBR, UA.DN_NBR, UA.UA_NBR, UA.BILL_TON_BL, ");
			sb.append("UA.BILL_TON_EDO, UA.BILL_TON_DN, UA.BILL_TON_ESN, UA.BILL_TON_BKG, ");
			sb.append("UA.LOAD_TON_CS, UA.SHUTOUT_TON_CS, UA.COUNT_UNIT, UA.TOTAL_PACK_EDO, ");
			sb.append("UA.TOTAL_PACK_DN, UA.BILL_ACCT_NBR FROM UA_SERVICE_SCENARIO_CODE UA ");
			sb.append("WHERE SCHEME IN (:zszScheme, 'X') AND VESSEL_TYPE IN (:zszVesselType ");
			sb.append(", 'X') AND SUB_CAT IN (:zszSubCat, 'X') AND TRANS_TYPE LIKE ");
			sb.append(":zszTransType AND PAYMENT IN (:zszPaymentMode, 'X') AND OPS_IND LIKE ");
			sb.append(" :zszOpsInd AND TYPE = :zszType");
			paramMap.put("zszScheme", zszScheme);
			paramMap.put("zszVesselType", zszVesselType);
			paramMap.put("zszSubCat", zszSubCat);
			paramMap.put("zszTransType", "%" + zszTransType + "%");
			paramMap.put("zszPaymentMode", zszPaymentMode);
			paramMap.put("zszOpsInd", "%" + zszOpsInd + "%");
			paramMap.put("zszType", zszType);
			zszSqlCompare = sb.toString();
			map.put("sql", zszSqlCompare);
			map.put("paramMap", paramMap);
		}


		log.info("zszSqlCompare>>>>>>>>>>>>>>>: " + zszSqlCompare);
		log.info("END compareSqlDetails DAO  map: " + map);
		return map;
	}
//Spr001]

	// util.gbms.cab.ua -->CabUaExportServiceCharge
}
