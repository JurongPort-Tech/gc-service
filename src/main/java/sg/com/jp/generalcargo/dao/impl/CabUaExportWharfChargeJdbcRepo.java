package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CabUaExportWharfChargeRepo;
import sg.com.jp.generalcargo.domain.GbmsCargoBillingValueObject;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("cabUaExportWharfChargeRepo")
public class CabUaExportWharfChargeJdbcRepo extends CabDataFetchJdbcRepo implements CabUaExportWharfChargeRepo {

	private static final Log log = LogFactory.getLog(CabUaExportWharfChargeJdbcRepo.class);

	/**
	 * This method is used to return sql string to fetch all possible conditions
	 * 
	 * @param java.lang.String strcode
	 * @param java.lang.String struserid
	 * @exception @return java.lang.String
	 */
	protected Map<String, ArrayList<?>> getSqlDetails(String strcode) {
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();
		Map<String, String> paramMap1 = new HashMap<>();
		Map<String, String> paramMap2 = new HashMap<>();
		Map<String, String> paramMap3 = new HashMap<>();
		Map<String, String> paramMap4 = new HashMap<>();
		Map<String, ArrayList<?>> map = new HashMap<>();
		log.info("START getSqlDetails DAO :: strcode: " + CommonUtility.deNull(strcode));

		sb.append(" select e.cargo_category_cd  cargoCategory, ");
		sb.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  vesselScheme, ");
		sb.append(" 'L'  cargoStatus, ''  transStatus, ed.mixed_scheme_acct_nbr  mixedSchemeAcct, " );
		sb.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (SELECT cust_acct.ACCT_NBR FROM CUST_ACCT, vessel_call vctemp where CUST_ACCT.cust_cd = vctemp.create_Cust_cd ");
		sb.append(" and vctemp.vv_cd = vc.vv_cd and CUST_ACCT.business_type = 'G' and cust_acct.acct_status_cd = 'A') ELSE vc.bill_acct_nbr END as saAcct, ");
		sb.append(" '' abAcct, ed.acct_nbr  cargoAcct, ed.payment_Mode  paymentMode, vs.ab_cd  abCd, ");
		sb.append(" 'E' transType, v.vsl_type_cd vesselType, e.in_voy_var_nbr discVvCd, ");
		sb.append(" e.out_voy_var_nbr  loadVvCd, ed.esn_ops_ind opsInd, 'L' vvInd, '' businessType, ");
		sb.append(" ''  schemeCd, ''  tariffMainCatCd, ''  tariffSubCatCd, '' mvmt, ");
		sb.append(" e.CARGO_CATEGORY_CD  type, '' cargoType, '' localLeg, '' discGateway, ");
		sb.append(" '' refInd, ''  billAcctNbr, TRANS_DTTM  printDttm, ''  billInd, ''  blNbr, ");
		sb.append(" ''  edoAsnNbr, bk.bk_ref_nbr  bkRefNbr, e.esn_asn_nbr  esnAsnNbr, ''  dnNbr, ");
		sb.append(" ua.ua_nbr  uaNbr, ''  billTonBl,  ''  billTonEdo,  ''  billTonDn, ");
		sb.append(" greatest (ed.esn_wt/1000, ed.esn_vol)  billTonEsn, ''  billTonBkg, ");
		sb.append(" greatest (ed.esn_wt/1000, ed.esn_vol)*(bk.actual_nbr_shipped)  loadTonCs, ''  shutoutTonCs, ");
		sb.append(" ed.nbr_pkgs  countUnit, ''  totalPackEdo, ''  totalPackDn, sysdate  lastModifyDttm, ");
		sb.append(" 'WF' chval, vc.MIXED_SCHEME_IND MschInd, '' firstcarsch, '' edoacctnbr, ");
		sb.append(" '' SAtenind, '' SCtenind, '' whind, '' fsdays ");
		sb.append(" from bk_details bk, esn e, esn_details ed, vessel_call vc, vessel_scheme vs, vessel v,ua_details ua ");
		sb.append(" where ua.esn_asn_nbr = e.esn_asn_nbr and bk.bk_ref_nbr = e.bk_ref_nbr and ");
		sb.append(" e.esn_asn_nbr = ed.esn_asn_nbr and vc.vv_cd = e.out_voy_var_nbr and ");
		sb.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and ");
		sb.append(" v.vsl_nm = vc.vsl_nm and bk.cargo_type not in ('00','01','02','03') and ");
		// sb.append(" ed.ESN_OPS_IND not in ('O') and ");
		// added by Balaji (06th Jan 2003)
		sb.append(" ua.bill_wharf_triggered_ind = 'N' and ");
		// end added by Balaji (06th Jan 2003)		
		sb.append(" e.esn_status='A' and ua.ua_status='A' and bk.bk_status='A' and ua.ua_nbr = :strcode");
		paramMap1.put("strcode", strcode);
		String sql1 = sb.toString();

		sb1.append("select e.cargo_category_cd  cargoCategory,");
		// sb1.append(" vc.scheme vesselScheme, ");
		// MC Consulting - To get the Subscheme as Scheme if combi indicator is Y
		sb1.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  vesselScheme, ");
		sb1.append(" 'T'  cargoStatus, 'TS'  transStatus,  ''  mixedSchemeAcct,");
		// sb1.append(" vc.bill_acct_nbr saAcct, ")
		sb1.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (SELECT cust_acct.ACCT_NBR FROM CUST_ACCT, vessel_call vctemp where CUST_ACCT.cust_cd = vctemp.create_Cust_cd ");
		sb1.append(" and vctemp.vv_cd = vc.vv_cd and CUST_ACCT.business_type = 'G' and cust_acct.acct_status_cd = 'A') ELSE vc.bill_acct_nbr END as saAcct, ''  abAcct, ");
		// Amended by Jade for SL-CAB-20130730-01
		// sb1.append(" ed.acct_nbr cargoAcct, ");
		sb1.append(" decode(ed.payment_mode, 'C', 'CA', ed.acct_nbr) cargoAcct, ");
		// End of amendment by Jade for SL-CAB-20130730-01
		sb1.append(" ed.payment_Mode  paymentMode, vs.ab_cd  abCd, 'A'  transType,");
		sb1.append(" v.vsl_type_cd  vesselType, e.in_voy_var_nbr  discVvCd, e.out_voy_var_nbr  loadVvCd,");
		sb1.append(" ed.ld_ind  opsInd, 'L'  vvInd, ''  businessType, ''  schemeCd,");
		sb1.append(" ''  tariffMainCatCd, ''  tariffSubCatCd, ''  mvmt, e.CARGO_CATEGORY_CD  type,");
		sb1.append(" ''  cargoType, ''  localLeg, ''  discGateway, ''  refInd, ''  billAcctNbr,");
		sb1.append(" TRANS_DTTM  printDttm, ''  billInd, ''  blNbr, ''  edoAsnNbr,");
		sb1.append(" bk.bk_ref_nbr  bkRefNbr, e.esn_asn_nbr  esnAsnNbr, ''  dnNbr, ua.ua_nbr  uaNbr,");
		sb1.append(" ''  billTonBl, ''  billTonEdo, ''  billTonDn,");
		sb1.append(" greatest (ed.nom_wt/1000, ed.nom_vol)  billTonEsn, ''  billTonBkg,");
		sb1.append(" greatest (ed.nom_wt/1000, ed.nom_vol)*(bk.actual_nbr_shipped)  loadTonCs, ''  shutoutTonCs,");
		sb1.append(" ed.nbr_pkgs  countUnit, ''  totalPackEdo, ''  totalPackDn, sysdate  lastModifyDttm,");
		sb1.append(" 'WF' chval, vc.MIXED_SCHEME_IND MschInd,");
		sb1.append(" (select CASE WHEN vc1.COMBI_GC_OPS_IND ='Y' THEN vc1.COMBI_GC_SCHEME  ELSE vc1.SCHEME END from vessel_call vc1,gb_edo ge1 where ge1.edo_asn_nbr = ed.edo_asn_nbr");
		sb1.append(" and vc1.vv_cd = ge1.var_nbr and ge1.payment_mode in ('A')) firstcarsch,");
		sb1.append(" (select ge1.acct_nbr from gb_edo ge1 where ge1.edo_asn_nbr = ed.edo_asn_nbr and ge1.payment_mode in ('A')) edoacctnbr,");
		sb1.append(" '' SAtenind, '' SCtenind, '' whind, '' fsdays");
		sb1.append(" from bk_details bk, esn e, tesn_jp_jp ed, manifest_details md, gb_edo edo, vessel_call vc, vessel_scheme vs, vessel v, ua_details ua");
		sb1.append(" where  ua.esn_asn_nbr = e.esn_asn_nbr and bk.bk_ref_nbr = e.bk_ref_nbr and");
		sb1.append(" e.esn_asn_nbr = ed.esn_asn_nbr and vc.vv_cd = e.out_voy_var_nbr and");
		sb1.append(" v.vsl_nm = vc.vsl_nm and");
		sb1.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and");
		// sb1.append(" ed.LD_IND not in ('O') and "
		sb1.append(" bk.cargo_type not in ('00','01','02','03') and ");
		// added by Balaji (06th Jan 2003)
		sb1.append(" ua.bill_wharf_triggered_ind = 'N' and ");
		// sb1.append(" ua.bill_service_triggered_ind = 'N' and ");
		// end added by Balaji (06th Jan 2003)
		sb1.append(" e.esn_status='A' and bk.bk_status='A' and ua.ua_status='A' and" );
		sb1.append(" ed.edo_asn_nbr = edo.edo_asn_nbr and md.mft_seq_nbr = edo.mft_seq_nbr and ua.ua_nbr = :strcode" );
		paramMap2.put("strcode", strcode);
		String sql2 = sb1.toString();

		sb2.append("select e.cargo_category_cd  cargoCategory,");
		// +" vc.scheme vesselScheme,"
		// MC Consulting - To get the Subscheme as Scheme if combi indicator is Y
		sb2.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  vesselScheme, ");
		sb2.append(" 'T'  cargoStatus, 'IT'  transStatus, ''  mixedSchemeAcct, ");
		// sb2.append(" vc.bill_acct_nbr saAcct, ");
		sb2.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (SELECT cust_acct.ACCT_NBR FROM CUST_ACCT, vessel_call vctemp where CUST_ACCT.cust_cd = vctemp.create_Cust_cd and vctemp.vv_cd = vc.vv_cd ");
		sb2.append(" and CUST_ACCT.business_type = 'G' and cust_acct.acct_status_cd = 'A') ELSE vc.bill_acct_nbr END As saAcct, ");
		sb2.append(" ''  abAcct,  ed.acct_nbr  cargoAcct, ed.payment_Mode  paymentMode, vs.ab_cd  abCd, ");
		sb2.append(" v.vsl_type_cd  vesselType, 'C'  transType, e.in_voy_var_nbr  discVvCd, ");
		sb2.append(" e.out_voy_var_nbr  loadVvCd, ed.ops_ind  opsInd, 'L'  vvInd, ''  businessType, ");
		sb2.append(" ''  schemeCd, ''  tariffMainCatCd, ''  tariffSubCatCd, ''  mvmt, ");
		sb2.append(" e.CARGO_CATEGORY_CD  type, ''  cargoType, ''  localLeg, ''  discGateway, ");
		sb2.append(" ''  refInd, ''  billAcctNbr, TRANS_DTTM  printDttm, ''  billInd, ''  blNbr, ");
		sb2.append(" ''  edoAsnNbr, bk.bk_ref_nbr  bkRefNbr, e.esn_asn_nbr  esnAsnNbr, ''  dnNbr, ");
		sb2.append(" ua.ua_nbr  uaNbr, ''  billTonBl, ''  billTonEdo, ''  billTonDn, ");
		sb2.append(" greatest (ed.gross_wt/1000, ed.gross_vol)  billTonEsn, ''  billTonBkg, ");
		sb2.append(" greatest (ed.gross_wt/1000, ed.gross_vol)*(bk.actual_nbr_shipped)  loadTonCs, ");
		sb2.append(" ''  shutoutTonCs, ed.nbr_pkgs  countUnit, ''  totalPackEdo, ''  totalPackDn, ");
		sb2.append(" sysdate  lastModifyDttm, 'WF' chval, vc.MIXED_SCHEME_IND MschInd, '' firstcarsch, ");
		sb2.append(" '' edoacctnbr, '' SAtenind, '' SCtenind, '' whind, '' fsdays ");
		sb2.append(" from bk_details bk, esn e, tesn_psa_jp ed, vessel_call vc, vessel_scheme vs,vessel v, ua_details ua ");
		sb2.append(" where ua.esn_asn_nbr = e.esn_asn_nbr and bk.bk_ref_nbr = e.bk_ref_nbr and ");
		sb2.append(" e.esn_asn_nbr = ed.esn_asn_nbr and v.vsl_nm = vc.vsl_nm and ");
		sb2.append(" vc.vv_cd = e.out_voy_var_nbr and ");
		sb2.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and ");
		sb2.append(" bk.cargo_type not in ('00','01','02','03') and ");
		// sb2.append(" ed.OPS_IND not in ('O') and ");
		// added by Balaji (06th Jan 2003)
		sb2.append(" ua.bill_wharf_triggered_ind = 'N' and ");
		// sb2.append(" ua.bill_service_triggered_ind = 'N' and ");
		// end added by Balaji (06th Jan 2003)
		sb2.append(" e.esn_status='A' and bk.bk_status='A' and ua.ua_status='A'  and ua.ua_nbr = :strcode ");
		paramMap3.put("strcode", strcode);
		String sql3 = sb2.toString();

		sb3.append(" select  e.cargo_category_cd  cargoCategory, ");
		// sb3.append(" vc.scheme vesselScheme, ");
		// MC Consulting - To get the Subscheme as Scheme if combi indicator is Y
		sb3.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  vesselScheme, ");
		sb3.append(" 'L'  cargoStatus, ''  transStatus, ''  mixedSchemeAcct,");
		// sb3.append(" vc.bill_acct_nbr saAcct, ");
		sb3.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN (SELECT cust_acct.ACCT_NBR FROM CUST_ACCT, vessel_call vctemp where CUST_ACCT.cust_cd = vctemp.create_Cust_cd and vctemp.vv_cd = vc.vv_cd");
		sb3.append(" and CUST_ACCT.business_type = 'G' and cust_acct.acct_status_cd = 'A') ELSE vc.bill_acct_nbr END as saAcct,");
		sb3.append(" ''  abAcct,  ed.acct_nbr  cargoAcct, ed.payment_Mode  paymentMode, vs.ab_cd  abCd,");
		sb3.append(" v.vsl_type_cd  vesselType, 'S'  transType, e.in_voy_var_nbr  discVvCd,");
		sb3.append(" e.out_voy_var_nbr  loadVvCd, 'N'  opsInd, 'L'  vvInd, ''  businessType,");
		sb3.append(" ''  schemeCd, ''  tariffMainCatCd, ''  tariffSubCatCd, ''  mvmt,");
		sb3.append(" e.CARGO_CATEGORY_CD  type, ''  cargoType, ''  localLeg, ''  discGateway,");
		sb3.append(" ''  refInd, ''  billAcctNbr, TRANS_DTTM  printDttm, ''  billInd, ''  blNbr,");
		sb3.append(" ''  edoAsnNbr, ''  bkRefNbr, e.esn_asn_nbr  esnAsnNbr, ''  dnNbr,");
		sb3.append(" ua.ua_nbr  uaNbr, ''  billTonBl, ''  billTonEdo, ''  billTonDn,");
		sb3.append(" greatest (ed.ss_wt/1000, ed.ss_vol)  billTonEsn, ''  billTonBkg, 0  loadTonCs,");
		sb3.append(" ''  shutoutTonCs, ed.nbr_pkgs  countUnit, ''  totalPackEdo, ''  totalPackDn,");
		sb3.append(" sysdate  lastModifyDttm, 'WF' chval, vc.MIXED_SCHEME_IND MschInd, '' firstcarsch,");
		sb3.append(" '' edoacctnbr, '' SAtenind, '' SCtenind, '' whind, '' fsdays");
		sb3.append(" from esn e, ss_details ed, vessel_call vc, vessel_scheme vs, vessel v, ua_details ua where");
		sb3.append(" ua.esn_asn_nbr = e.esn_asn_nbr and e.esn_asn_nbr = ed.esn_asn_nbr and");
		sb3.append(" vc.vv_cd = e.out_voy_var_nbr and v.vsl_nm = vc.vsl_nm and");
		sb3.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and");
		// added by Balaji (06th Jan 2003)
		sb3.append(" ua.bill_wharf_triggered_ind = 'N' and");
		// sb3.append(" ua.bill_service_triggered_ind = 'N' and ");
		// end added by Balaji (06th Jan 2003)
		sb3.append(" e.esn_status='A' and ua.ua_status='A'  and ua.ua_nbr = :strcode ");
		String sql4 = sb3.toString();
		paramMap4.put("strcode", strcode);
		ArrayList<String> sqlarraylist = new ArrayList<>();
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
		log.info("END getSqlDetails DAO  map: " + map);
		return map;
	}

//[Spr001
	/**
	 * This method is used to return sql string for particular condition
	 * 
	 * @param GbmsCargoBillingValueObject gbmsCargoBillingValueObject
	 * @exception @return java.lang.String
	 */
	protected Map<String, Object> compareSqlDetails(GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		log.info("START compareSqlDetails DAO :: strcode: " + CommonUtility.deNull(xGbmsCargoBillingValueObject.toString()));
		StringBuilder sb = new StringBuilder();
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

		/// ducta1 start on 26/12/2008
		String zszTariffSubCat = xGbmsCargoBillingValueObject.getTariffSubCatCd();
		if (!GbmsCommonUtility.isVehicleRORO(zszType, zszTariffSubCat)) {
			if (zszType.equals("EX") && zszTransType.equals("E"))
				zszType = "00";
			if ((zszTransType.equals("A") || zszTransType.equals("C"))
					&& (zszType.equals("LS") || zszType.equals("WA"))) {
				zszType = "00";
				zszSubCat = "GL";
			} else if ((zszScheme.equals("JNL") || zszScheme.equals("JBT")
					|| zszScheme.equals(ProcessChargeConst.LCT_SCHEME)) && zszTransType.equals("E")
					&& (zszType.equals("LS") || zszType.equals("WA"))) {
				zszSubCat = "AN";
			} else if ((zszScheme.equals("JNL") || zszScheme.equals("JBT")
					|| zszScheme.equals(ProcessChargeConst.LCT_SCHEME))
					&& !(zszTransType == "E" || zszTransType == "A" || zszTransType == "C")
					&& (zszType.equals("LS") || zszType.equals("WA") || zszType.equals("01") || zszType.equals("02")
							|| zszType.equals("03"))) {
				zszType = "00";
				zszSubCat = "GL";
			}

			zszType = deriveType(xGbmsCargoBillingValueObject.getType(), zszType);
			sb.append(" SELECT  UA.DISC_VV_CD, UA.LOAD_VV_CD, ");
			sb.append(" UA.VV_IND, UA.BUSINESS_TYPE, UA.SCHEME_CD, UA.TARIFF_MAIN_CAT_CD, ");
			sb.append(" UA.TARIFF_SUB_CAT_CD, UA.MVMT, UA.TYPE, UA.CARGO_TYPE, UA.LOCAL_LEG, ");
			sb.append(" UA.DISC_GATEWAY, UA.REF_IND, UA.BL_NBR, UA.EDO_ASN_NBR, ");
			sb.append(" UA.BK_REF_NBR,  UA.ESN_ASN_NBR, UA.DN_NBR, UA.UA_NBR, UA.BILL_TON_BL, ");
			sb.append(" UA.BILL_TON_EDO, UA.BILL_TON_DN, UA.BILL_TON_ESN, UA.BILL_TON_BKG, ");
			sb.append(" UA.LOAD_TON_CS, UA.SHUTOUT_TON_CS, UA.COUNT_UNIT, UA.TOTAL_PACK_EDO, ");
			sb.append(" UA.TOTAL_PACK_DN, UA.BILL_ACCT_NBR FROM UA_WHARF_SCENARIO_CODE UA WHERE SCHEME IN (:zszScheme, 'X') ");
			sb.append(" AND VESSEL_TYPE IN  (:zszVesselType, 'X') AND SUB_CAT IN (:zszSubCat, 'X') ");
			sb.append(" AND TRANS_TYPE LIKE :zszTransType AND PAYMENT IN (:zszPaymentMode, 'X') ");
			sb.append(" AND OPS_IND LIKE :zszOpsInd AND TYPE = :zszType ");
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

		} else {
			// SQL for RORO vessel which has mvmt in search criteria
			zszType = deriveType(xGbmsCargoBillingValueObject.getType(), zszType);
			sb.append("SELECT  UA.DISC_VV_CD, UA.LOAD_VV_CD, ");
			sb.append( " UA.VV_IND, UA.BUSINESS_TYPE, UA.SCHEME_CD, UA.TARIFF_MAIN_CAT_CD, ");
			sb.append( " UA.TARIFF_SUB_CAT_CD, UA.MVMT, UA.TYPE, UA.CARGO_TYPE, UA.LOCAL_LEG, ");
			sb.append( " UA.DISC_GATEWAY, UA.REF_IND, UA.BL_NBR, UA.EDO_ASN_NBR, ");
			sb.append( " UA.BK_REF_NBR,  UA.ESN_ASN_NBR, UA.DN_NBR, UA.UA_NBR, UA.BILL_TON_BL, ");
			sb.append( " UA.BILL_TON_EDO, UA.BILL_TON_DN, UA.BILL_TON_ESN, UA.BILL_TON_BKG, ");
			sb.append( " UA.LOAD_TON_CS, UA.SHUTOUT_TON_CS, UA.COUNT_UNIT, UA.TOTAL_PACK_EDO, ");
			sb.append( " UA.TOTAL_PACK_DN, UA.BILL_ACCT_NBR FROM UA_WHARF_SCENARIO_CODE UA WHERE SCHEME IN (:zszScheme, 'X') ");
			sb.append( " AND VESSEL_TYPE IN  (:zszVesselType, 'X') AND SUB_CAT IN (:zszSubCat, 'X') ");
			sb.append( " AND TRANS_TYPE LIKE :zszTransType AND PAYMENT IN (:zszPaymentMode, 'X') ");
			sb.append( " AND OPS_IND LIKE :zszOpsInd AND TYPE = :zszType ");
			sb.append( " AND UA.MVMT = :zszMvmt ");
			zszSqlCompare = sb.toString();
			paramMap.put("zszScheme", zszScheme);
			paramMap.put("zszVesselType", zszVesselType);
			paramMap.put("zszSubCat", zszSubCat);
			paramMap.put("zszTransType", "%" + zszTransType + "%");
			paramMap.put("zszPaymentMode", zszPaymentMode);
			paramMap.put("zszOpsInd", "%" + zszOpsInd + "%");
			paramMap.put("zszType", zszType);
			paramMap.put("zszMvmt", zszMvmt);
			map.put("sql", zszSqlCompare);
			map.put("paramMap", paramMap);

		}
		// ducta1 end

		log.info("zszSqlCompare >>>>>>>>>>: " + zszSqlCompare);
		log.info("END compareSqlDetails DAO  map: " + map);
		return map;
	}
}
