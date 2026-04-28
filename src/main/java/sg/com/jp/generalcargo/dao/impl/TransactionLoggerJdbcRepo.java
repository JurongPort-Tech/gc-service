package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javassist.tools.rmi.RemoteException;
import sg.com.jp.generalcargo.dao.BillSupportInfoRepository;
import sg.com.jp.generalcargo.dao.CabUaExportServiceChargeRepo;
import sg.com.jp.generalcargo.dao.CabUaExportStoreRentChargeRepo;
import sg.com.jp.generalcargo.dao.CabUaExportWharfChargeRepo;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.TransactionLoggerRepo;
import sg.com.jp.generalcargo.domain.BillAdjParamCOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCTVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCVO;
import sg.com.jp.generalcargo.domain.BillAdjParamOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamTOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamTVO;
import sg.com.jp.generalcargo.domain.BillAdjustParam;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.CloseLctValueObject;
import sg.com.jp.generalcargo.domain.GbmsCabValueObject;
import sg.com.jp.generalcargo.domain.GbmsCargoBillingValueObject;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("TransactionLoggerRepo")
public class TransactionLoggerJdbcRepo implements TransactionLoggerRepo {

	private static final Log log = LogFactory.getLog(TransactionLoggerJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	public Timestamp txnDttm = null;
	@Autowired
	@Lazy
	private ProcessGBLogRepository processGBLogRepo;

	@Autowired
	private BillSupportInfoRepository billSupportInfoRepo;

	@Autowired
	private CabUaExportServiceChargeRepo cabUaExportServiceCharge;

	@Autowired
	private CabUaExportWharfChargeRepo cabUaExportWharfCharge;

	@Autowired
	private CabUaExportStoreRentChargeRepo cabUaExportStoreRentCharge;

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: TriggerDN()
	/**
	 * This method will prepare the data retrieved in CabDN and sent to CAB for
	 * logging
	 * 
	 * @param dnnbr     DN No
	 * @param struserid user id who created the DN
	 * @throws Exception
	 * @throws NamingException
	 */
	public String TriggerDN(GbmsCabValueObject gbmsCabValueObject) throws BusinessException {
		String updatestatus = "FALSE";

		try {
			log.info("gbmsCabValueObject : " + gbmsCabValueObject.toString());
			log.info("CALLED CAB IMPORT dn METHOD");
			String status = gbmsCabValueObject.getStatus();
			String bill_wharf_triggered_ind = gbmsCabValueObject.getBillWharfTriggeredInd();
			String bill_service_triggered_ind = gbmsCabValueObject.getBillServiceTriggeredInd();

			if (bill_wharf_triggered_ind == null) {
				bill_wharf_triggered_ind = "N";
			}
			if (bill_service_triggered_ind == null) {
				bill_service_triggered_ind = "N";
			}
			if (status.equalsIgnoreCase("TRUE")) {
				/*
				 * Statement stmt = gbmsconnection.createStatement();
				 * sql="UPDATE DN_DETAILS SET "
				 * +" BILL_WHARF_TRIGGERED_IND ='"+bill_wharf_triggered_ind
				 * +"', BILL_SERVICE_TRIGGERED_IND ='"+bill_service_triggered_ind
				 * +"' WHERE DN_NBR ='"+dn_nbr+"' AND DN_STATUS='A'"; int
				 * count=stmt.executeUpdate(sql); if (count == 0) {
				 * sessionContext.setRollbackOnly(); //throw new BusinessException("M1007");
				 * }else{ gbmsconnection.commit(); updatestatus="TRUE"; } stmt.close();
				 */
			}
			log.info("ALL OVER SENDS dn TRUE");
			log.info("END: *** TriggerDN Result *****" + CommonUtility.deNull(updatestatus));

		} catch (NullPointerException e) {
			log.info("Exception TriggerDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception TriggerDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: TriggerDN  DAO  END");
		}
		return updatestatus;
	} // trigger DN end

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: getSystemDate()
	/**
	 * Retrieves the current system date/time.
	 * 
	 * @param gbmsconnection database connection object for GBMS database
	 * @return Timestamp current system date/time
	 * @exception BusinessException
	 */
	public Timestamp getSystemDate() throws BusinessException {
		StringBuilder sql = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		Timestamp sdate = null;
		try {
			log.info("START: getSystemDate  DAO  Start Obj ");
			sql.append(" SELECT SYSDATE FROM DUAL ");

			log.info(" *** getSystemDate SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				sdate = (Timestamp) rs.getObject("SYSDATE");
			}
		} catch (NullPointerException e) {
			log.info("Exception getSystemDate :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSystemDate :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSystemDate  DAO  END  sdate: " + (sdate == null ? "null" : sdate));
		}
		return sdate;
	}// end of getSysdate

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: CabDN()
	/**
	 * This method retrieves all the necessary fields for logging for charges at
	 * Printing of DN
	 * 
	 * @param gbmsConnection database connection object for GBMS database
	 * @param dn_nbr         DN No
	 * @param struserid      user id who creates the DN
	 * @throws Exception
	 * @throws NamingException
	 */
	public Map<String, Object> CabDN(String dn_nbr, String struserid) throws BusinessException {

		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs4 = null;
		SqlRowSet rssrlr = null;
		SqlRowSet rsexpsch = null;
		SqlRowSet rssrnl = null;
		SqlRowSet rsdn = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		StringBuilder sqlserv = new StringBuilder();
		StringBuilder sqlwharf1 = new StringBuilder();
		StringBuilder sqlwharf3 = new StringBuilder();
		StringBuilder sqlSRLR1 = new StringBuilder();
		StringBuilder sqlSRNL1 = new StringBuilder();

		List<GeneralEventLogValueObject> GeneralEventLogArrayList = new ArrayList<GeneralEventLogValueObject>();
		// MCC for 1st DN creation
		List<GeneralEventLogValueObject> GeneralEventLogEDOArrayList = new ArrayList<GeneralEventLogValueObject>();
		String strvvcd = "";
		String transtype = "";
		String crgcat = "";

		String billWharfInd = "N";
		String billSvcChargeInd = "N";
		String billStoreInd = "N";
		String billProcessInd = "N";
		String bill_store_triggered_ind = "N";
		GbmsCabValueObject gbmsCabValueObject = new GbmsCabValueObject();
		int count1 = 0;
		int count2 = 0;
		int count4 = 0;
		int count5 = 0;
		int count6 = 0;
		try {
			log.info("START: CabDN DAO dn_nbr: " + CommonUtility.deNull(dn_nbr) + ", struserid: "
					+ CommonUtility.deNull(struserid));

			gbmsCabValueObject.setStatus("FALSE");

			/*
			 * <CFG> commented; updated queries // sql used for retrieving service charge
			 * for JNL/JBT String sqlserv =
			 * "select vc.vv_cd,dn.dn_nbr,dn.edo_asn_nbr,CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS scheme,edo.crg_status, "
			 * + "greatest(edo.nom_wt/1000, edo.nom_vol) as edo_bill_ton,edo.nbr_pkgs, " +
			 * "dn.nbr_pkgs as dn_nbr_pkgs,md.bl_nbr,edo.dis_type distype,md.cargo_category_cd,dn.TRANS_TYPE as transtype,dn.TRANS_DTTM as dndttm from manifest_details md, "
			 * + "vessel_call vc,gb_edo edo,dn_details dn where md.var_nbr= vc.vv_cd and " +
			 * "md.mft_seq_nbr = edo.mft_seq_nbr and dn.edo_asn_nbr = edo.edo_asn_nbr and "
			 * + "md.bl_status = 'A' and dn.dn_status = 'A' and edo.edo_status = 'A' and " +
			 * "dn.BILL_SERVICE_TRIGGERED_IND = 'N' and CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END in ('JNL','JBT') and "
			 * + "md.crg_type not in ('00','01','02','03') and " + "dn.dn_nbr='"+dn_nbr+"'";
			 * 
			 * // sql used for retrieving wharfage charge for JLR/AB Operator String
			 * sqlwharf1 =
			 * "select vc.vv_cd,dn.dn_nbr,dn.edo_asn_nbr, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS scheme,edo.crg_status, "
			 * +
			 * "greatest(edo.nom_wt/1000, edo.nom_vol) as edo_bill_ton,edo.nbr_pkgs,dn.nbr_pkgs as dn_nbr_pkgs, "
			 * +
			 * "md.bl_nbr,md.cargo_category_cd,dn.TRANS_TYPE as transtype,edo.dis_type distype,dn.TRANS_DTTM as dndttm from manifest_details md,vessel_call vc,vessel_scheme vs, "
			 * +
			 * "gb_edo edo,dn_details dn where md.var_nbr = vc.vv_cd and md.mft_seq_nbr = edo.mft_seq_nbr and "
			 * +
			 * "dn.edo_asn_nbr = edo.edo_asn_nbr and vc.scheme = vs.scheme_cd and md.bl_status = 'A' and "
			 * +
			 * "dn.dn_status ='A' and edo.edo_status = 'A' and nvl(edo.dis_type,' ') IN ('D','N') and "
			 * +
			 * "dn.BILL_WHARF_TRIGGERED_IND = 'N' and (CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = 'JLR' or vs.ab_cd is not null) and "
			 * + "md.crg_type not in ('00','01','02','03') and " + "dn.dn_nbr='"+dn_nbr+"'";
			 * 
			 * // SQL used for retrieving wharfage charge for JNL/JBT String sqlwharf3 =
			 * "select vc.vv_cd,dn.dn_nbr,dn.edo_asn_nbr, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS scheme,edo.crg_status, "
			 * + "greatest(edo.nom_wt/1000, edo.nom_vol) as edo_bill_ton,edo.nbr_pkgs, "+
			 * "dn.nbr_pkgs as dn_nbr_pkgs,md.bl_nbr,md.cargo_category_cd,dn.TRANS_TYPE as transtype,edo.dis_type distype,dn.TRANS_DTTM as dndttm from "
			 * +
			 * "manifest_details md,vessel_call vc, vessel_scheme vs,gb_edo edo,dn_details dn "
			 * + "where md.var_nbr = vc.vv_cd and md.mft_seq_nbr = edo.mft_seq_nbr and "+
			 * "dn.edo_asn_nbr = edo.edo_asn_nbr and vc.scheme=vs.scheme_cd and "+
			 * "md.bl_status  = 'A' and dn.dn_status = 'A' and edo.edo_status  = 'A' and "+
			 * "dn.BILL_WHARF_TRIGGERED_IND = 'N' and "+
			 * "md.crg_type not in ('00','01','02','03') and "+
			 * "(CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END IN('JNL','JBT')) and dn.dn_nbr = '"
			 * +dn_nbr+"'";
			 * 
			 */

			// <CFG> query updates
			// sql used for retrieving service charge for JNL/JBT
			sqlserv.append(" select vc.vv_cd,dn.dn_nbr,dn.edo_asn_nbr, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' ");
			sqlserv.append(" THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS scheme,edo.crg_status, ");
			sqlserv.append(" greatest(edo.nom_wt/1000, edo.nom_vol) as edo_bill_ton,edo.nbr_pkgs, ");
			sqlserv.append(" dn.nbr_pkgs as dn_nbr_pkgs,md.bl_nbr,edo.dis_type distype, ");
			sqlserv.append(" md.cargo_category_cd,dn.TRANS_TYPE as transtype,dn.TRANS_DTTM as dndttm ");
			sqlserv.append(" from GBMS.manifest_details md, TOPS.VESSEL_CALL vc,gb_edo edo,GBMS.dn_details dn ");
			sqlserv.append(" where md.var_nbr= vc.vv_cd and md.mft_seq_nbr = edo.mft_seq_nbr and ");
			sqlserv.append(" dn.edo_asn_nbr = edo.edo_asn_nbr and md.bl_status = 'A' and dn.dn_status = 'A' ");
			sqlserv.append(" and edo.edo_status = 'A' and ");
			// add new scheme for LCT, 15.feb.11 by hpeng
			sqlserv.append(" dn.BILL_SERVICE_TRIGGERED_IND = 'N' and ( VC.SCHEME in ");
			sqlserv.append(" ('JNL','JBT','").append(ProcessChargeConst.LCT_SCHEME).append("') OR ");
			sqlserv.append(" (UPPER(VC.COMBI_GC_OPS_IND) = 'Y' AND VC.COMBI_GC_SCHEME IN ('JNL'))) and ");
			sqlserv.append(" edo.BILL_SERVICE_TRIGGERED_IND = 'N' and md.crg_type not in ");
			sqlserv.append(" ('00','01','02','03') and dn.dn_nbr=:dnNbr ");

			// sql used for retrieving wharfage charge for JLR/AB Operator
			// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08>
			sqlwharf1.append(" SELECT /*WF for JLR/AB and JWP*/ vc.vv_cd,dn.dn_nbr,dn.edo_asn_nbr, ");
			sqlwharf1.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME ");
			sqlwharf1.append(" END AS scheme,edo.crg_status, greatest(edo.nom_wt/1000, edo.nom_vol) ");
			sqlwharf1.append(" as edo_bill_ton,edo.nbr_pkgs,dn.nbr_pkgs as dn_nbr_pkgs, md.bl_nbr, ");
			sqlwharf1.append(" md.cargo_category_cd,dn.TRANS_TYPE as transtype,edo.dis_type distype, ");
			sqlwharf1.append(" md.epc_ind, dn.TRANS_DTTM as dndttm from manifest_details md,vessel_call vc, ");
			sqlwharf1.append(" vessel_scheme vs, ");
			// MCC get EPC_IND
			sqlwharf1.append(" gb_edo edo,dn_details dn where md.var_nbr = vc.vv_cd and md.mft_seq_nbr = ");
			sqlwharf1.append(" edo.mft_seq_nbr and ");
			// "dn.edo_asn_nbr = edo.edo_asn_nbr and vc.scheme = vs.scheme_cd and
			// md.bl_status = 'A' and " +
			sqlwharf1.append(" dn.edo_asn_nbr = edo.edo_asn_nbr and CASE WHEN VC.COMBI_GC_OPS_IND ='Y' ");
			sqlwharf1.append(" THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and ");
			sqlwharf1.append(" md.bl_status = 'A' and dn.dn_status ='A' and edo.edo_status = 'A' ");
			sqlwharf1.append(" and nvl(edo.dis_type,' ') IN ('D','N') and ");
			// <cfg: commented 23.may.08>
			// sqlwharf1.append(" dn.BILL_WHARF_TRIGGERED_IND = 'N' and (vc.scheme = 'JLR'
			// or vs.ab_cd is not null) and ");
			sqlwharf1.append(" dn.BILL_WHARF_TRIGGERED_IND = 'N' and ");
			sqlwharf1.append(" (VC.SCHEME in ('JLR','JWP') OR (UPPER(VC.COMBI_GC_OPS_IND) = 'Y' ");
			sqlwharf1.append(" AND VC.COMBI_GC_SCHEME IN ('JLR')) or vs.ab_cd is not null) and ");
			// <cfg: 23.may.08/>
			sqlwharf1.append(" edo.BILL_WHARF_TRIGGERED_IND = 'N' and md.crg_type not in ('00','01','02','03') ");
			sqlwharf1.append(" and dn.dn_nbr=:dnNbr ");

			// SQL used for retrieving wharfage charge for JNL/JBT
			sqlwharf3.append(" select vc.vv_cd,dn.dn_nbr,dn.edo_asn_nbr, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' ");
			sqlwharf3.append(" THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS scheme,edo.crg_status, ");
			sqlwharf3.append(" greatest(edo.nom_wt/1000, edo.nom_vol) as edo_bill_ton,edo.nbr_pkgs, ");
			sqlwharf3.append(" dn.nbr_pkgs as dn_nbr_pkgs,md.bl_nbr,md.cargo_category_cd,dn.TRANS_TYPE ");
			sqlwharf3.append(" as transtype,edo.dis_type distype, md.epc_ind, dn.TRANS_DTTM as dndttm ");
			// MCC get EPC_IND
			sqlwharf3.append(" from GBMS.manifest_details md,TOPS.VESSEL_CALL vc, vessel_scheme vs,gb_edo edo, ");
			sqlwharf3.append(" GBMS.dn_details dn where md.var_nbr = vc.vv_cd and md.mft_seq_nbr = edo.mft_seq_nbr ");
			sqlwharf3.append(" and dn.edo_asn_nbr = edo.edo_asn_nbr and vc.scheme=vs.scheme_cd and ");
			sqlwharf3.append(" md.bl_status  = 'A' and dn.dn_status = 'A' and edo.edo_status  = 'A' and ");
			sqlwharf3.append(" dn.BILL_WHARF_TRIGGERED_IND = 'N' and edo.BILL_WHARF_TRIGGERED_IND = 'N' and ");
			sqlwharf3.append(" md.crg_type not in ('00','01','02','03') and ");
			// add new scheme for LCT, 15.feb.11 by hpeng
			sqlwharf3.append(" ( VC.SCHEME IN('JNL','JBT','").append(ProcessChargeConst.LCT_SCHEME).append("') OR ");
			sqlwharf3.append(" (UPPER(VC.COMBI_GC_OPS_IND) = 'Y' AND VC.COMBI_GC_SCHEME IN ('JNL'))) and ");
			sqlwharf3.append("  dn.dn_nbr =:dnNbr ");
			// <CFG> end: query updates

			// added for Store Rent by balaji/Lakshmi
			// SQL used for retrieving store rent charge for JLR/AB operator
			// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08>
			sqlSRLR1.append(" SELECT /*StoreRent for JLR/AB and JWP*/ vc.vv_cd,dn.dn_nbr,dn.edo_asn_nbr, ");
			sqlSRLR1.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END ");
			sqlSRLR1.append(" AS scheme,edo.crg_status, greatest(edo.nom_wt/1000, edo.nom_vol) as ");
			sqlSRLR1.append(" edo_bill_ton,edo.nbr_pkgs,dn.nbr_pkgs as dn_nbr_pkgs, md.bl_nbr, ");
			sqlSRLR1.append(" md.cargo_category_cd,dn.TRANS_TYPE as transtype,edo.dis_type distype, ");
			sqlSRLR1.append(" dn.TRANS_DTTM as dndttm,nvl(edo.FREE_STG_DAYS,0) fsdays from ");
			sqlSRLR1.append(" GBMS.manifest_details md,TOPS.VESSEL_CALL vc,vessel_scheme vs,TOPS.BERTHING b, ");
			sqlSRLR1.append(" gb_edo edo,GBMS.dn_details dn where md.var_nbr = vc.vv_cd and vc.vv_cd = ");
			sqlSRLR1.append(" b.vv_cd and b.shift_ind =1 and md.mft_seq_nbr = edo.mft_seq_nbr and ");
			sqlSRLR1.append(" dn.edo_asn_nbr = edo.edo_asn_nbr and CASE WHEN VC.COMBI_GC_OPS_IND ='Y' ");
			sqlSRLR1.append(" THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = vs.scheme_cd and md.bl_status ");
			sqlSRLR1.append(" = 'A' and dn.dn_status = 'A' and edo.edo_status = 'A' and nvl(edo.dis_type,' ') ");
			sqlSRLR1.append(" IN ('D','N') and ");
			// <cfg: reverted for unauthorised store rent 30.may.08>
			// sqlSRLR1.append(" dn.BILL_STORE_TRIGGERED_IND = 'N' and (vc.scheme in
			// ('JLR','JWP') or vs.ab_cd is not null) and ");
			// sqlSRLR1.append(" dn.BILL_STORE_TRIGGERED_IND = 'N' and (vc.scheme = 'JLR' or
			// vs.ab_cd is not null) and ");
			// add new scheme for LCT, 13.feb.11 by hpeng
			sqlSRLR1.append(" dn.BILL_STORE_TRIGGERED_IND = 'N' and ");
			sqlSRLR1.append(" (VC.SCHEME = 'JLR' OR (UPPER(VC.COMBI_GC_OPS_IND) = 'Y' AND VC.COMBI_GC_SCHEME ");
			sqlSRLR1.append(" IN ('JLR')) or vs.ab_cd is not null) and md.crg_type not ");
			sqlSRLR1.append(" in ('00','01','02','03') and edo.WH_IND <> 'Y' ");
			// Comented By balaji (10 April 2003)
			// Uncomment by Irene Tan on 16 August 2003 - GSL-2003-000076
			// comment by Irene Tan on 02 September 2003 - JPPL/IT/001/2001 - Phase 1b (ITH
			// Store Rent)
			// uncomment by Irene Tan on 26 February 2003 - to bloack ITH store rent
			sqlSRLR1.append(" and dn.Trans_type not in ('B') ");
			sqlSRLR1.append(" and nvl(md.epc_ind,'N') = 'N'  ");
			// MCC epc_ind cargos are not subject to storerent
			// end uncomment by Irene Tan on 26 February 2003
			// end comment by Irene Tan on 02 September 2003
			// end uncomment by Irene Tan on 16 August 2003
			// End Comented By balaji (10 April 2003)
			sqlSRLR1.append(" and dn.dn_nbr=:dnNbr ");

			// SQL used for retrieving the store rent charge for JNL/JBT
			sqlSRNL1.append(" select vc.vv_cd,dn.dn_nbr,dn.edo_asn_nbr, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' ");
			sqlSRNL1.append(" THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS scheme,edo.crg_status, ");
			sqlSRNL1.append(" greatest(edo.nom_wt/1000, edo.nom_vol) as edo_bill_ton,edo.nbr_pkgs, ");
			sqlSRNL1.append(" dn.nbr_pkgs as dn_nbr_pkgs,md.bl_nbr,md.cargo_category_cd,dn.TRANS_TYPE as ");
			sqlSRNL1.append(" transtype,edo.dis_type distype, b.gb_cod_dttm cod,dn.TRANS_DTTM as ");
			sqlSRNL1.append(" dndttm,nvl(round(b.gb_cod_dttm-dn.DN_CREATE_DTTM,2),0) srdatediff, ");
			sqlSRNL1.append(" nvl(edo.FREE_STG_DAYS,0) fsdays from GBMS.manifest_details md,TOPS.VESSEL_CALL vc, ");
			sqlSRNL1.append(" vessel_scheme vs,gb_edo edo,GBMS.dn_details dn,TOPS.BERTHING b where md.var_nbr = ");
			sqlSRNL1.append(" vc.vv_cd and vc.vv_cd = b.vv_cd and b.shift_ind =1 and md.mft_seq_nbr = ");
			sqlSRNL1.append(" edo.mft_seq_nbr and dn.edo_asn_nbr = edo.edo_asn_nbr and vc.scheme=vs.scheme_cd ");
			sqlSRNL1.append(" and md.bl_status  = 'A' and dn.dn_status = 'A' and edo.edo_status  = 'A' and ");
			sqlSRNL1.append(" dn.BILL_STORE_TRIGGERED_IND = 'N' and ");
			sqlSRNL1.append(" md.crg_type not in ('00','01','02','03') and edo.WH_IND <> 'Y' ");
			// Comented By balaji (10 April 2003)
			// Uncomment by Irene Tan on 16 August 2003 - GSL-2003-000076
			// comment by Irene Tan on 02 September 2003 - JPPL/IT/001/2001 - Phase 1b (ITH
			// Store Rent)
			// uncomment by Irene Tan on 26 February 2004 - to block ITH Store Rent
			sqlSRNL1.append(" and dn.Trans_type not in ('B') ");
			sqlSRNL1.append(" and nvl(md.epc_ind,'N') = 'N'  ");
			// MCC epc_ind cargos are not subject to storerent
			// end uncomment by Irene Tan on 26 February 2004
			// end comment by Irene Tan on 02 September 2003
			// end uncomment by Irene Tan on 16 August 2003
			// End Comented By balaji (10 April 2003)
			// <cfg: add new scheme for Wooden Craft: JWP, 30.may.08>
			// "and (vc.scheme = 'JNL' or vc.scheme= 'JBT') and dn.dn_nbr = '"+dn_nbr+"'";
			sqlSRNL1.append(" and ( VC.SCHEME  in ('JNL','JBT','JWP','").append(ProcessChargeConst.LCT_SCHEME);
			sqlSRNL1.append(" ') OR (UPPER(VC.COMBI_GC_OPS_IND) = 'Y' AND VC.COMBI_GC_SCHEME IN ('JNL'))) ");
			sqlSRNL1.append(" and dn.dn_nbr =:dnNbr ");
			// <cfg: add new scheme for Wooden Craft: JWP, 30.may.08/>

			// add new scheme for LCT, 16.mar.11 by hpeng

			txnDttm = getSystemDate();
			log.info("txnDttm : " + txnDttm);

			// add new scheme for LCT, 16.mar.11 by hpeng
			String vvCd = getVvCdByDnNbr(dn_nbr);
			String scheme = getSchemeName(vvCd);
			boolean isClosedLct = isClosedLct(vvCd);
			if (!ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(scheme) || isClosedLct) {

				// MCC check is this is first active dn for the EDO
				boolean isFirstDN = processGBLogRepo.isFirstDNForEDO(dn_nbr);
				log.info("isFirstActiveDN for EDO : " + isFirstDN);

				log.info("SQL >> sqlserv : " + sqlserv);

				paramMap.put("dnNbr", dn_nbr);
				log.info(" *** CabDN SQL *****" + sqlserv.toString() + " paramMap: " + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlserv.toString(), paramMap);
				while (rs1.next()) {
					strvvcd = CommonUtility.deNull(rs1.getString(1));
					String vvInd = ProcessChargeConst.DISC_VV_IND;
					String businessType = "G";
					String schemeCd = CommonUtility.deNull(rs1.getString(4));
					String tariffMainCatCd = "SC";
					String tariffSubCatCd = "GL";
					String type = deriveType(rs1.getString("CARGO_CATEGORY_CD"), "00");// CommonUtility.deNull(rs1.getString(11));
					String localLeg = "IM";
					// discgateway to be changed
					String discGateway = "J";
					String lastModifyUserId = struserid;
					// Timestamp lastModifyDttm=txnDttm;
					Timestamp lastModifyDttm = (Timestamp) rs1.getObject("dndttm");
					String mvmt = "";
					String edoNbr;
					String blNbr;
					String billAcctNbr = "";
					double billTonEdo;
					int tot_pack_edo = 0;
					int tot_pack_dn = 0;
					int cnt_unit = 0;
					String typeForRORO = "";
					transtype = CommonUtility.deNull(rs1.getString("transtype"));
					blNbr = CommonUtility.deNull(rs1.getString(9));
					edoNbr = CommonUtility.deNull(rs1.getString(3));
					String cargostatus = CommonUtility.deNull(rs1.getString(5));
					crgcat = CommonUtility.deNull(rs1.getString(11));
					int countUnit = Integer.parseInt(CommonUtility.deNull(rs1.getString("nbr_pkgs")));

					if (cargostatus.equalsIgnoreCase("T") || cargostatus.equalsIgnoreCase("R")) {
						mvmt = "TS";
					}
					if (cargostatus.equalsIgnoreCase("L")) {
						mvmt = "LL";
						crgcat = CommonUtility.deNull(rs1.getString(11));
					}

					// ducta1 start on 30/12/2008
					typeForRORO = rs1.getString("CARGO_CATEGORY_CD");
					if (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(typeForRORO)
							|| ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(typeForRORO)
							|| ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(typeForRORO)) {
						tariffSubCatCd = ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
						type = typeForRORO;
						if ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
							mvmt = ProcessChargeConst.MVMT_TRANSHIP;// = "TS";
						} else if ("L".equalsIgnoreCase(cargostatus)) {
							mvmt = ProcessChargeConst.MVMT_LOCAL; // = "LL";
							crgcat = CommonUtility.deNull(rs1.getString(11));
						} else {
							mvmt = ProcessChargeConst.MVMT_ITH; // "IT";
						}

					} // ducta1 end

					// Amended by Jade for CR-CAB-20130225-001
					// For service charge of LCT vessel, bill to shipping agent (berth applicant)
					if (ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(scheme)) {
						billAcctNbr = getBABillAcctNbr(strvvcd, edoNbr);
					} else {
						billAcctNbr = getEdoBillAcctNbr(edoNbr);
					}
					// End of amendment by Jade for CR-CAB-20130225-001

					billTonEdo = Double.parseDouble(CommonUtility.deNull(rs1.getString(6)));
					tot_pack_edo = rs1.getInt(7);
					tot_pack_dn = rs1.getInt(8);
					cnt_unit = rs1.getInt(8);

					// for load vvcd in the jpjp cases
					/*
					 * Commented by Jade for SL-CAB-20111221-01 String loadvvcd = ""; String
					 * lvvcdsql =
					 * "select esn.out_voy_var_nbr from esn, tesn_jp_jp where esn.esn_asn_nbr=tesn_jp_jp.esn_asn_nbr and tesn_jp_jp.edo_asn_nbr='"
					 * +edoNbr+"'"; Statement stmtlv = gbmsconnection.createStatement(); ResultSet
					 * rslv = stmtlv.executeQuery(lvvcdsql);
					 * 
					 * if (transtype.equals("A")) if(rslv.next()) loadvvcd = rslv.getString(1);
					 * 
					 * stmtlv.close(); rslv.close();
					 */
					// Added by Jade for SL-CAB-20111221-01
					// To retrieve loading vessel for transhipment cargo based on esn number instead
					// of edo number
					log.info("sqlserv--------------------This is a DN for Transhipment Cargo------------");
					String loadvvcd = getLoadVVCd(transtype, dn_nbr);
					log.info("sqlserv--------------------loading vv code = " + loadvvcd);
					// End of adding by Jade for SL-CAB-20111221-01

					// for load vvcd in the jpjp cases
					if (transtype.equals("T") || (crgcat.equals("LS") || crgcat.equals("WA"))) {
						// if Non Liner and transhipment Locally exit
					} else {
						if (transtype.equals("B"))
							mvmt = "IT";

						// if(!schemeCd.equals("JNL") && !schemeCd.equals("JBT") &&
						// !schemeCd.equals("JLR"))
						// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08>
						// add new scheme for LCT, 16.feb.11 by hpeng
						if (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") && !schemeCd.equals("JLR")
								&& !schemeCd.equals("JWP") && !schemeCd.equals(ProcessChargeConst.LCT_SCHEME))
							schemeCd = "JLR";
						// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/>

						GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
						log.info("**** DN SERVICE CHARGES ******");
						log.info("|loadvvcd : " + loadvvcd);
						log.info("|discVvCd : " + strvvcd);
						log.info("|vvInd : " + vvInd);
						log.info("|businessType : " + businessType);
						log.info("|schemeCd : " + schemeCd);
						log.info("|tariffMainCatCd : " + tariffMainCatCd);
						log.info("|tariffSubCatCd : " + tariffSubCatCd);
						log.info("|mvmt : " + mvmt);
						log.info("|type : " + type);
						log.info("|localLeg : " + localLeg);
						log.info("|discGateway : " + discGateway);
						log.info("|blNbr : " + blNbr);
						log.info("|dn_nbr : " + dn_nbr);
						log.info("|edoNbr : " + edoNbr);
						log.info("|billTonEdo : " + billTonEdo);
						log.info("|billAcctNbr : " + billAcctNbr);
						log.info("|tot_pack_dn : " + tot_pack_dn);
						log.info("|tot_pack_edo : " + tot_pack_edo);
						// added by Balaji
						log.info("|refInd : " + ProcessChargeConst.REF_IND_DN);
						// end added by Balaji
						log.info("|lastModifyUserId : " + lastModifyUserId);
						log.info("|lastModifyDttm : " + lastModifyDttm);
						log.info("==== END DN OF SERVICE CHARGES ====");

						generalEventLogValueObject.setDiscVvCd(strvvcd);
						generalEventLogValueObject.setLoadVvCd(loadvvcd);
						generalEventLogValueObject.setVvInd(vvInd);
						generalEventLogValueObject.setBusinessType(businessType);
						generalEventLogValueObject.setSchemeCd(schemeCd);
						generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
						generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
						generalEventLogValueObject.setMvmt(mvmt);
						generalEventLogValueObject.setType(type);
						generalEventLogValueObject.setLocalLeg(localLeg);
						generalEventLogValueObject.setDiscGateway(discGateway);
						generalEventLogValueObject.setBlNbr(blNbr);
						generalEventLogValueObject.setDnNbr(dn_nbr);
						generalEventLogValueObject.setEdoAsnNbr(edoNbr);
						generalEventLogValueObject.setCountUnit(countUnit);// ducta1 start on 24/12/2008

						if (!(transtype.equals("A") || transtype.equals("T") || transtype.equals("B"))
								&& (crgcat.equals("LS") || crgcat.equals("WA")))
							generalEventLogValueObject.setCountUnit(cnt_unit);
						if (!(transtype.equals("A") || transtype.equals("T") || transtype.equals("B"))
								&& (crgcat.equals("LS") || crgcat.equals("WA")))
							generalEventLogValueObject.setCargoType(crgcat);
						if (!crgcat.equals("LS") && !crgcat.equals("WA")) {
							generalEventLogValueObject.setBillTonEdo(billTonEdo);
							generalEventLogValueObject.setTotalPackDn(tot_pack_dn);
							generalEventLogValueObject.setTotalPackEdo(tot_pack_edo);
						}
						if (transtype.equals("A") || transtype.equals("B") || transtype.equals("T")) {
							generalEventLogValueObject.setBillTonEdo(billTonEdo);
							generalEventLogValueObject.setTotalPackDn(tot_pack_dn);
							generalEventLogValueObject.setTotalPackEdo(tot_pack_edo);
						}

						generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
						// added by Balaji
						generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_DN);
						// end added by Balaji
						generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
						// generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
						// add new scheme for LCT, 28.feb.11 by hpeng
						if (ProcessChargeConst.LCT_SCHEME.equals(schemeCd)) {
							generalEventLogValueObject.setLastModifyDttm(txnDttm);
						} else {
							generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
						}
						generalEventLogValueObject.setPrintDttm(lastModifyDttm);

						count1++;
						GeneralEventLogArrayList.add(generalEventLogValueObject);
						// MCC add EDO Service Charge event for 1st DN creation
						if (isFirstDN && mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_LOCAL)
								&& !schemeCd.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME)) { // for LCT charge with
							// billable ton Manifest
							GeneralEventLogEDOArrayList.add(generalEventLogValueObject);
						}
					} // end-if
				} // end-while

				if (count1 > 0) {
					billSvcChargeInd = "Y";
					gbmsCabValueObject.setBillServiceTriggeredInd("Y");
				}

				log.info("SQL >> sqlwharf1 : " + sqlwharf1);

				paramMap.put("dnNbr", dn_nbr);
				log.info(" *** CabDN SQL *****" + sqlwharf1.toString() + " paramMap: " + paramMap);
				rs2 = namedParameterJdbcTemplate.queryForRowSet(sqlwharf1.toString(), paramMap);
				while (rs2.next()) {
					strvvcd = CommonUtility.deNull(rs2.getString(1));
					String vvInd = ProcessChargeConst.DISC_VV_IND;
					String businessType = "G";
					String schemeCd = CommonUtility.deNull(rs2.getString(4));
					String tariffMainCatCd = "WF";
					String tariffSubCatCd = getTariffSubCat(CommonUtility.deNull(rs2.getString(10)));
					String localLeg = "IM";
					// discgateway to be changed
					String discGateway = "J";
					String lastModifyUserId = struserid;
					// Timestamp lastModifyDttm=txnDttm;
					Timestamp lastModifyDttm = (Timestamp) rs2.getObject("dndttm");
					String mvmt = "LL";
					String type = "00";// CommonUtility.deNull(rs1.getString(11));
					String edoNbr = "";
					String blNbr = "";
					String billAcctNbr = "";
					double billTonEdo = 0.0;
					transtype = CommonUtility.deNull(rs2.getString("transtype"));
					int tot_pack_edo = 0;
					int tot_pack_dn = 0;
					int cnt_unit = 0;
					transtype = CommonUtility.deNull(rs2.getString(11));
					crgcat = CommonUtility.deNull(rs2.getString(10));
					tot_pack_edo = rs2.getInt(7);
					tot_pack_dn = rs2.getInt(8);
					billTonEdo = Double.parseDouble(CommonUtility.deNull(rs2.getString(6)));
					cnt_unit = rs2.getInt(8);
					blNbr = CommonUtility.deNull(rs2.getString(9));
					edoNbr = CommonUtility.deNull(rs2.getString(3));
					String cargostatus = CommonUtility.deNull(rs2.getString(5));
					int countUnit = Integer.parseInt(CommonUtility.deNull(rs2.getString("nbr_pkgs")));// ducta1 start on
					// 24/12/2008

					String deliveryToEPC = CommonUtility.deNull(rs2.getString("epc_ind")); // MCC get epc_ind flag
					// boolean isEPCForTS = false; //local vessel with epc_ind true should be
					// considered for 1st DN creation. only exclude transshipment edo's for epc_ind
					// as true

					if (cargostatus.equalsIgnoreCase("T") || cargostatus.equalsIgnoreCase("R")) {
						mvmt = "TS";
					}
					if (cargostatus.equalsIgnoreCase("L")) {
						mvmt = "LL";
					}

					// ducta1 start on 24/12/2008
					String typeForRORO = rs2.getString("CARGO_CATEGORY_CD");
					if (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(typeForRORO)
							|| ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(typeForRORO)
							|| ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(typeForRORO)) {
						tariffSubCatCd = ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
						type = typeForRORO;
						if ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
							mvmt = ProcessChargeConst.MVMT_TRANSHIP;// = "TS";
						} else if ("L".equalsIgnoreCase(cargostatus)) {
							mvmt = ProcessChargeConst.MVMT_LOCAL; // = "LL";
						} else {
							mvmt = ProcessChargeConst.MVMT_ITH; // "IT";
						}
					}

					if ((!GbmsCommonUtility.isVehicleRORO(type, tariffSubCatCd))
							&& (crgcat.equals("01") || crgcat.equals("02") || crgcat.equals("03") || crgcat.equals("LS")
									|| crgcat.equals("WA"))) {
						mvmt = "00";
						type = crgcat;
					}

					if (transtype.equals("B") && (crgcat.equals("LS") || crgcat.equals("WA") || crgcat.equals("00")
							|| crgcat.equals(ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI))) {

						mvmt = "IT";
						type = "00";
						tariffSubCatCd = "GL";

					}
					// ThachPhung starts on 06Feb09
					// change value of mvmt to ITH for RORO with logic same as GL
					else if ("B".equals(transtype) && GbmsCommonUtility.isVehicleRORO(type, tariffSubCatCd)) {
						mvmt = "IT";
					}

					// change value of mvmt to TS for Wharfage
					if (transtype.equals("A") && (crgcat.equals("LS") || crgcat.equals("WA") || crgcat.equals("00")
							|| crgcat.equals(ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI))) {

						mvmt = "TS";
						type = "00";
						tariffSubCatCd = "GL";

					} else if ("A".equals(transtype) && GbmsCommonUtility.isVehicleRORO(type, tariffSubCatCd)) {
						mvmt = "TS";
					}

					if (transtype.equals("T") && (crgcat.equals("LS") || crgcat.equals("WA") || crgcat.equals("00")
							|| crgcat.equals(ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI) || crgcat.equals("01")
							|| crgcat.equals("02") || crgcat.equals("03"))) {

						mvmt = "TS";

						// if (!GbmsCommonUtility.isVehicleRORO(type, tariffSubCatCd)) { //sauwoon even
						// if its RORO, if its type TS (delivered locally) must bill penalty wharfage
						type = "TS";
						tariffSubCatCd = "GL";
						// }
					}

					// ThachPhung end
					// ducta1 end

					type = deriveType(rs2.getString("CARGO_CATEGORY_CD"), type);

					// MCC set mvmt as Local for Transhipment cargoes delivered locally if EPC_IND
					// is Y
					/*
					 * if(deliveryToEPC.equalsIgnoreCase("Y")){ log.
					 * debug("Delivery To EPC Area so charge local import rate for both Transhipment cargo with mvmt: "
					 * +mvmt); if(mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_TRANSHIP) ||
					 * mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_ITH)){ mvmt =
					 * ProcessChargeConst.MVMT_LOCAL; type = "00"; isEPCForTS = true; } }
					 */

					// For transhipment cargoes delivered locally, if EPC_IND is Y, change it to
					// local
					log.info("deliveryToEPC = " + deliveryToEPC);
					log.info("transtype = " + transtype);
					if (transtype.equals("T") && deliveryToEPC.equalsIgnoreCase("Y")) {
						log.info(
								"Transhipment cargo delivered locally. Cargo from EPC area, change from TS to local import status");
						mvmt = ProcessChargeConst.MVMT_LOCAL;
						type = "00";
						tariffSubCatCd = "GL";
					}

					billAcctNbr = getEdoBillAcctNbr(edoNbr);
					// for load vvcd in the jpjp cases
					/*
					 * Commented by Jade for SL-CAB-20111221-01 String loadvvcd = ""; String
					 * lvvcdsql =
					 * "select esn.out_voy_var_nbr from esn, tesn_jp_jp where esn.esn_asn_nbr=tesn_jp_jp.esn_asn_nbr and tesn_jp_jp.edo_asn_nbr='"
					 * +edoNbr+"'"; Statement stmtlv = gbmsconnection.createStatement(); ResultSet
					 * rslv = stmtlv.executeQuery(lvvcdsql);
					 * 
					 * if (transtype.equals("A")) if (rslv.next()) loadvvcd = rslv.getString(1);
					 * 
					 * stmtlv.close(); rslv.close();
					 */
					// Added by Jade for SL-CAB-20111221-01
					// To retrieve loading vessel for transhipment cargo based on esn number instead
					// of edo number
					log.info("sqlwharf1--------------------This is a DN for Transhipment Cargo------------");
					String loadvvcd = getLoadVVCd(transtype, dn_nbr);
					log.info("sqlwharf1--------------------loading vv code = " + loadvvcd);
					// End of adding by Jade for SL-CAB-20111221-01

					// for load vvcd in the jpjp cases
					// add new scheme for LCT, 20.feb.11 by hpeng
					// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08>
					if (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") && !schemeCd.equals("JLR")
							&& !schemeCd.equals("JWP") && !schemeCd.equals(ProcessChargeConst.LCT_SCHEME))
						schemeCd = "JLR";
					// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/>

					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();

					log.info("**** DN WHARF CHARGES ******");
					log.info("|discVvCd : " + strvvcd);
					log.info("|loadvvcd : " + loadvvcd);
					log.info("|vvInd : " + vvInd);
					log.info("|businessType : " + businessType);
					log.info("|schemeCd : " + schemeCd);
					log.info("|tariffMainCatCd : " + tariffMainCatCd);
					log.info("|tariffSubCatCd : " + tariffSubCatCd);
					log.info("|mvmt : " + mvmt);
					log.info("|type : " + crgcat);
					log.info("|localLeg : " + localLeg);
					log.info("|discGateway : " + discGateway);
					log.info("|blNbr : " + blNbr);
					log.info("|dn_nbr : " + dn_nbr);
					log.info("|edoNbr : " + edoNbr);
					log.info("|billTonEdo : " + billTonEdo);
					log.info("|billAcctNbr : " + billAcctNbr);
					log.info("|cnt_unit : " + cnt_unit);
					log.info("|tot_pack_dn : " + tot_pack_dn);
					log.info("|tot_pack_edo : " + tot_pack_edo);
					// added by Balaji
					log.info("|refInd : " + ProcessChargeConst.REF_IND_DN);
					// end added by Balaji
					log.info("|lastModifyUserId : " + lastModifyUserId);
					log.info("|lastModifyDttm : " + lastModifyDttm);
					log.info("==== END DN OF WHARF CHARGES ====");

					generalEventLogValueObject.setDiscVvCd(strvvcd);
					generalEventLogValueObject.setLoadVvCd(loadvvcd);
					generalEventLogValueObject.setVvInd(vvInd);
					generalEventLogValueObject.setBusinessType(businessType);
					generalEventLogValueObject.setSchemeCd(schemeCd);
					generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
					generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
					generalEventLogValueObject.setMvmt(mvmt);
					generalEventLogValueObject.setType(type);
					generalEventLogValueObject.setLocalLeg(localLeg);
					generalEventLogValueObject.setDiscGateway(discGateway);
					generalEventLogValueObject.setBlNbr(blNbr);
					generalEventLogValueObject.setDnNbr(dn_nbr);
					generalEventLogValueObject.setEdoAsnNbr(edoNbr);
					generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
					generalEventLogValueObject.setCountUnit(countUnit);

					// MCC added
					/*
					 * if(isEPCForTS){ generalEventLogValueObject.setIsEPCForTS("Y"); //MCC to
					 * ignore TS & IT for EPCIND is Y to charge upon 1st DN }
					 */

					if ((transtype.equals("A") || transtype.equals("B")) && crgcat.equals("LS") || crgcat.equals("WA")
							|| crgcat.equals("00")
							|| crgcat.equals(ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI)) {
						generalEventLogValueObject.setBillTonEdo(billTonEdo);
						generalEventLogValueObject.setTotalPackDn(tot_pack_dn);
						generalEventLogValueObject.setTotalPackEdo(tot_pack_edo);
					}

					if (!transtype.equals("T") && (crgcat.equals("01") || crgcat.equals("02") || crgcat.equals("03")))
						generalEventLogValueObject.setCountUnit(cnt_unit);

					// added on 27 09 2002 Friday as per Irene's instruction
					if (transtype.equals("L") && (crgcat.equals("LS") || crgcat.equals("WA")))
						generalEventLogValueObject.setCountUnit(cnt_unit);

					if (transtype.equals("T") && (crgcat.equals("LS") || crgcat.equals("WA") || crgcat.equals("00")
							|| crgcat.equals(ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI) || crgcat.equals("01")
							|| crgcat.equals("02") || crgcat.equals("03"))) {
						generalEventLogValueObject.setBillTonEdo(billTonEdo);
						generalEventLogValueObject.setTotalPackDn(tot_pack_dn);
						generalEventLogValueObject.setTotalPackEdo(tot_pack_edo);
					}

					// ThachPhung starts on 28-Dec-08
					// store value of billTonEdo, totalPackDn, totalPackEdo to support calculate
					// billTon for wharfage of RORO
					if (GbmsCommonUtility.isVehicleRORO(type, tariffSubCatCd)) {
						log.info("Process DN for RORO tot_pack_edo=" + tot_pack_edo);
						generalEventLogValueObject.setBillTonEdo(billTonEdo);
						generalEventLogValueObject.setTotalPackDn(tot_pack_dn);
						generalEventLogValueObject.setTotalPackEdo(tot_pack_edo);
					}
					// ThachPhung end

					// added by Balaji
					generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_DN);
					// end added by Balaji
					generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
					// generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
					// add new scheme for LCT, 28.feb.11 by hpeng
					if (ProcessChargeConst.LCT_SCHEME.equals(schemeCd)) {
						generalEventLogValueObject.setLastModifyDttm(txnDttm);
					} else {
						generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
					}
					generalEventLogValueObject.setPrintDttm(lastModifyDttm);
					count2++;
					GeneralEventLogArrayList.add(generalEventLogValueObject);

					// MCC add WF for EDO event for 1st DN creation
					if (isFirstDN && mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_LOCAL) // && !isEPCForTS
							&& !schemeCd.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME) // for LCT charge with billable
							// ton Manifest){
							&& !transtype.equalsIgnoreCase("T")) { // need to exclude DNs for transhipment cargo
						// delivered locally
						log.info("**Billing under 1st DN creation for edo=" + edoNbr);
						GeneralEventLogEDOArrayList.add(generalEventLogValueObject);
					}

					// ducta1 temporary
					log.info("ducta1 $$$$$$$$$$$$$$$$$$Test1$$$$$$$$$$$$$$$$$$$$$");
					// ducta1 end
				} // while

				if (count2 > 0) {
					billWharfInd = "Y";
					gbmsCabValueObject.setBillWharfTriggeredInd("Y");
				}

				// wharf 3
				log.info("SQL >> sqlwharf3 : " + sqlwharf3);

				paramMap.put("dnNbr", dn_nbr);
				log.info(" *** CabDN SQL *****" + sqlwharf3.toString() + " paramMap: " + paramMap);
				rs4 = namedParameterJdbcTemplate.queryForRowSet(sqlwharf3.toString(), paramMap);
				while (rs4.next()) {
					strvvcd = CommonUtility.deNull(rs4.getString(1));
					String vvInd = ProcessChargeConst.DISC_VV_IND;
					String businessType = ProcessChargeConst.GENERAL_BUSINESS;
					String schemeCd = CommonUtility.deNull(rs4.getString(4));
					String tariffMainCatCd = "WF";
					String tariffSubCatCd = getTariffSubCat(CommonUtility.deNull(rs4.getString(10)));
					String localLeg = "IM";
					// discgateway to be changed
					String discGateway = "J";
					String lastModifyUserId = struserid;
					// Timestamp lastModifyDttm=txnDttm;
					Timestamp lastModifyDttm = (Timestamp) rs4.getObject("dndttm");
					String mvmt = "LL";
					String edoNbr;
					String blNbr;
					String type = "00";
					String billAcctNbr = "";
					double billTonEdo;
					transtype = CommonUtility.deNull(rs4.getString("transtype"));
					crgcat = CommonUtility.deNull(rs4.getString(10));
					int tot_pack_edo = 0;
					int tot_pack_dn = 0;
					int cnt_unit = 0;
					// transtype = CommonUtility.deNull(rs4.getString(11);
					billTonEdo = Double.parseDouble(CommonUtility.deNull(rs4.getString(6)));
					blNbr = CommonUtility.deNull(rs4.getString(9));
					edoNbr = CommonUtility.deNull(rs4.getString(3));
					String cargostatus = CommonUtility.deNull(rs4.getString(5));

					String deliveryToEPC = CommonUtility.deNull(rs4.getString("epc_ind")); // MCC get epc_ind flag
					// boolean isEPCForTS = false;
					// for load vvcd in the jpjp cases
					/*
					 * Commented by Jade for SL-CAB-20111221-01 String loadvvcd = ""; String
					 * lvvcdsql =
					 * "select esn.out_voy_var_nbr from esn, tesn_jp_jp where esn.esn_asn_nbr=tesn_jp_jp.esn_asn_nbr and tesn_jp_jp.edo_asn_nbr='"
					 * +edoNbr+"'"; Statement stmtlv = gbmsconnection.createStatement(); ResultSet
					 * rslv = stmtlv.executeQuery(lvvcdsql);
					 * 
					 * if(transtype.equals("A")) if(rslv.next()) loadvvcd = rslv.getString(1);
					 * 
					 * stmtlv.close(); rslv.close();
					 */

					// Added by Jade for SL-CAB-20111221-01
					// To retrieve loading vessel for transhipment cargo based on esn number instead
					// of edo number
					log.info("sqlwharf3--------------------This is a DN for Transhipment Cargo------------");
					String loadvvcd = getLoadVVCd(transtype, dn_nbr);
					log.info("sqlwharf3--------------------loading vv code = " + loadvvcd);
					// End of adding by Jade for SL-CAB-20111221-01

					// ducta1 start on 12- jan - 2009
					String typeForRORO = rs4.getString("CARGO_CATEGORY_CD");
					if (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(typeForRORO)
							|| ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(typeForRORO)
							|| ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(typeForRORO)) {
						tariffSubCatCd = ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
						type = typeForRORO;
						if ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
							mvmt = ProcessChargeConst.MVMT_TRANSHIP;// = "TS";
						} else if ("L".equalsIgnoreCase(cargostatus)) {
							mvmt = ProcessChargeConst.MVMT_LOCAL; // = "LL";
						} else {
							mvmt = ProcessChargeConst.MVMT_ITH; // "IT";
						}
					} // ducta1 end

					// ducta1 start on 30/12/2008
					// for load vvcd in the jpjp cases
					if (cargostatus.equalsIgnoreCase("T") || cargostatus.equalsIgnoreCase("R")) {
						mvmt = "TS";
					}
					if (cargostatus.equalsIgnoreCase("L")) {
						mvmt = "LL";
					}
					// ducta1 end

					// Amended by Jade for CR-CAB-20130225-001
					// For wharfage of LCT vessel, bill to shipping agent (berth applicant)
					if (ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(scheme)) {
						billAcctNbr = getBABillAcctNbr(strvvcd, edoNbr);
					} else {
						billAcctNbr = getEdoBillAcctNbr(edoNbr);
					}
					// End of amendment by Jade for CR-CAB-20130225-001

					// ducta1 start on 30/12/2008
					if ((!GbmsCommonUtility.isVehicleRORO(type, tariffSubCatCd))
							&& (crgcat.equals("LS") || crgcat.equals("WA"))) {
						mvmt = "00";
						type = crgcat;
					}

					if (crgcat.equals("01") || crgcat.equals("02") || crgcat.equals("03")) {
						if (schemeCd.equals("JNL") || schemeCd.equals("JBT")
								|| schemeCd.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME)) {
							mvmt = "LL";
							// do not re-asign values with RORO
							if (!GbmsCommonUtility.isVehicleRORO(type, tariffSubCatCd)) {
								type = "00";
								tariffSubCatCd = "GL";
							}

						} else if (!GbmsCommonUtility.isVehicleRORO(type, tariffSubCatCd)) {
							mvmt = "00";
							type = crgcat;
						}
					}
					// ducta1 end

					tot_pack_edo = rs4.getInt(7);
					tot_pack_dn = rs4.getInt(8);
					cnt_unit = rs4.getInt(8);

					int countUnit = Integer.parseInt(CommonUtility.deNull(rs4.getString("nbr_pkgs")));

					if ((transtype.equals("A") || transtype.equals("B")) && (schemeCd.equals("JNL")
							|| schemeCd.equals("JBT") || schemeCd.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME))) {
						// if Non Liner and JP - JP transhipment exit
					} else {
						// ducta1 start on 30/12/2008
						if (transtype.equals("B")) {
							mvmt = ProcessChargeConst.MVMT_ITH;
						}
						if (transtype.equals("T")) {
							// do not re-asign values with RORO
							if (!GbmsCommonUtility.isVehicleRORO(type, tariffSubCatCd)) {
								type = ProcessChargeConst.MVMT_TRANSHIP;
							}
							mvmt = ProcessChargeConst.TYPE_TS_DELIVER_LOCALLY;
						}
						// ducta1 end

						// if(!schemeCd.equals(ProcessChargeConst.NON_LINER_SCHEME) &&
						// !schemeCd.equals(ProcessChargeConst.BARTER_TRADER_SCHEME) &&
						// !schemeCd.equals(ProcessChargeConst.LINER_SCHEME))
						// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08>
						if (!schemeCd.equals(ProcessChargeConst.NON_LINER_SCHEME)
								&& !schemeCd.equals(ProcessChargeConst.BARTER_TRADER_SCHEME)
								&& !schemeCd.equals(ProcessChargeConst.LINER_SCHEME)
								&& !schemeCd.equals(ProcessChargeConst.WOODEN_CRAFT_SCHEME)
								&& !schemeCd.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME))
							schemeCd = ProcessChargeConst.LINER_SCHEME;
						// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/>

						GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();

						// Add by Irene Tan on 6/9/2002
						if (!(transtype.equals("L"))) {
							// End Add

							// ducta1 start on 30/12/2008
							if ((!GbmsCommonUtility.isVehicleRORO(type, tariffSubCatCd))
									&& (tariffSubCatCd == "RO" || tariffSubCatCd == "AN"))
								tariffSubCatCd = "GL";
							// ducta1 end
						}

						type = deriveType(rs4.getString("CARGO_CATEGORY_CD"), type);

						// MCC set mvmt as Local for Transhipment cargoes if deliver to EPC is Y
						/*
						 * if(deliveryToEPC != null && deliveryToEPC.equalsIgnoreCase("Y")){ log.
						 * debug("Delivery To EPC Area so charge local import rate for both Transhipment mvmt: "
						 * +mvmt); if(mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_TRANSHIP) ||
						 * mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_ITH)){ mvmt =
						 * ProcessChargeConst.MVMT_LOCAL; type = "00"; isEPCForTS = true;
						 * 
						 * } }
						 */

						// For transhipment cargoes delivered locally, if EPC_IND is Y, change it to
						// local
						log.info("deliveryToEPC = " + deliveryToEPC);
						log.info("transtype = " + transtype);
						if (transtype.equals("T") && deliveryToEPC.equalsIgnoreCase("Y")) {
							log.info(
									"Transhipment cargo delivered locally. Cargo from EPC area, change from TS to local import status");
							mvmt = ProcessChargeConst.MVMT_LOCAL;
							type = "00";
							tariffSubCatCd = "GL";
						}

						log.info("**** DN WHARF CHARGES ******");
						log.info("|discVvCd : " + strvvcd);
						log.info("|loadvvcd : " + loadvvcd);
						log.info("|vvInd : " + vvInd);
						log.info("|businessType : " + businessType);
						log.info("|schemeCd : " + schemeCd);
						log.info("|tariffMainCatCd : " + tariffMainCatCd);
						log.info("|tariffSubCatCd : " + tariffSubCatCd);
						log.info("|mvmt : " + mvmt);
						log.info("|type : " + type);
						log.info("|localLeg : " + localLeg);
						log.info("|discGateway : " + discGateway);
						log.info("|blNbr : " + blNbr);
						log.info("|dn_nbr : " + dn_nbr);
						log.info("|edoNbr : " + edoNbr);
						log.info("|billTonEdo : " + billTonEdo);
						log.info("|billAcctNbr : " + billAcctNbr);
						log.info("|cnt_unit : " + cnt_unit);
						log.info("|tot_pack_dn : " + tot_pack_dn);
						log.info("|tot_pack_edo : " + tot_pack_edo);
						// added by Balaji
						log.info("|refInd : " + ProcessChargeConst.REF_IND_DN);
						// end added by Balaji
						log.info("|lastModifyUserId : " + lastModifyUserId);
						log.info("|lastModifyDttm : " + lastModifyDttm);
						log.info("==== END DN OF WHARF CHARGES ====");

						generalEventLogValueObject.setDiscVvCd(strvvcd);
						generalEventLogValueObject.setLoadVvCd(loadvvcd);
						generalEventLogValueObject.setVvInd(vvInd);
						generalEventLogValueObject.setBusinessType(businessType);
						generalEventLogValueObject.setSchemeCd(schemeCd);
						generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
						generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
						generalEventLogValueObject.setMvmt(mvmt);
						generalEventLogValueObject.setType(type);
						generalEventLogValueObject.setLocalLeg(localLeg);
						generalEventLogValueObject.setDiscGateway(discGateway);
						generalEventLogValueObject.setBlNbr(blNbr);
						generalEventLogValueObject.setDnNbr(dn_nbr);
						generalEventLogValueObject.setEdoAsnNbr(edoNbr);
						generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
						generalEventLogValueObject.setCountUnit(countUnit);// ducta1 start on 24/12/2008
						// MCC added
						/*
						 * if(isEPCForTS){ generalEventLogValueObject.setIsEPCForTS("Y"); //MCC to
						 * ignore TS & IT for EPCIND is Y to charge upon 1st DN }
						 */

						if (!transtype.equals("T") && (crgcat.equals("LS") || crgcat.equals("WA")))
							generalEventLogValueObject.setCountUnit(cnt_unit);

						if (transtype.equals("T") || crgcat.equals("01") || crgcat.equals("02") || crgcat.equals("03")
								|| crgcat.equals("00")
								|| crgcat.equals(ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI)) {
							generalEventLogValueObject.setBillTonEdo(billTonEdo);
							generalEventLogValueObject.setTotalPackDn(tot_pack_dn);
							generalEventLogValueObject.setTotalPackEdo(tot_pack_edo);
						}

						// ThachPhung starts on 28-Dec-08
						// store value of billTonEdo, totalPackDn, totalPackEdo to support calculate
						// billTon for wharfage of RORO
						if (GbmsCommonUtility.isVehicleRORO(type, tariffSubCatCd)) {
							log.info("Process DN for RORO tot_pack_edo=" + tot_pack_edo);
							generalEventLogValueObject.setBillTonEdo(billTonEdo);
							generalEventLogValueObject.setTotalPackDn(tot_pack_dn);
							generalEventLogValueObject.setTotalPackEdo(tot_pack_edo);
						}
						// ThachPhung end

						// added by Balaji
						generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_DN);
						// end added by Balaji
						generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
						// generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
						// add new scheme for LCT, 28.feb.11 by hpeng
						if (ProcessChargeConst.LCT_SCHEME.equals(schemeCd)) {
							generalEventLogValueObject.setLastModifyDttm(txnDttm);
						} else {
							generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
						}
						generalEventLogValueObject.setPrintDttm(lastModifyDttm);
						count4++;
						GeneralEventLogArrayList.add(generalEventLogValueObject);
						// MCC add WF for 1st DN creation
						if (isFirstDN && mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_LOCAL) // && !isEPCForTS
								&& !schemeCd.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME) // for LCT charge with
								// billable ton Manifest
								&& !transtype.equalsIgnoreCase("T")) { // Need to exlude DNs for transhipment cargo
							// delivered locally
							log.info("**Billing under 1st DN creation for edo=" + edoNbr);
							GeneralEventLogEDOArrayList.add(generalEventLogValueObject);
						}
					} // end-if scheme and transtype

					// ducta1 temporary
					log.info("ducta1 $$$$$$$$$$$$$$$$$$Test2$$$$$$$$$$$$$$$$$$$$$");
					// ducta1 end

				} // end-while rs4

				if (count4 > 0) {
					billWharfInd = "Y";
					gbmsCabValueObject.setBillWharfTriggeredInd("Y");
				}

				// add new scheme for LCT, 16.mar.11 by hpeng
			}
			// if (!ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(scheme) || !isClosedLct)
			// { //commented by Jade for SL-CAB-20141021-01: To trigger store rent upon DN
			// creation for LCT vessel even if LCT is closed

			/* STORE RENT START 02/10/2002 */
			// JLR/AB Operator Store Rent Charges
			log.info("SQL >> sqlSRLR1 : " + sqlSRLR1);

			paramMap.put("dnNbr", dn_nbr);
			log.info(" *** CabDN SQL *****" + sqlSRLR1.toString() + " paramMap: " + paramMap);
			rssrlr = namedParameterJdbcTemplate.queryForRowSet(sqlSRLR1.toString(), paramMap);
			while (rssrlr.next()) {
				strvvcd = CommonUtility.deNull(rssrlr.getString(1));
				String vvInd = ProcessChargeConst.DISC_VV_IND;
				String businessType = "G";
				String schemeCd = CommonUtility.deNull(rssrlr.getString(4));
				String tariffMainCatCd = "SR";
				String tariffSubCatCd = "GL";
				String localLeg = "IM";
				String discGateway = "J";
				String lastModifyUserId = struserid;
				Timestamp lastModifyDttm = (Timestamp) rssrlr.getObject("dndttm");
				String mvmt = "LL";
				String type = deriveType(rssrlr.getString(10), "TN");
				String edoNbr = CommonUtility.deNull(rssrlr.getString(3));
				String blNbr = CommonUtility.deNull(rssrlr.getString(9));
				String billAcctNbr = "";
				double billTonEdo = 0.0;
				transtype = CommonUtility.deNull(rssrlr.getString("transtype"));
				int tot_pack_edo = 0;
				int tot_pack_dn = 0;
				transtype = CommonUtility.deNull(rssrlr.getString(11));
				crgcat = CommonUtility.deNull(rssrlr.getString(10));
				tot_pack_edo = rssrlr.getInt(7);
				tot_pack_dn = rssrlr.getInt(8);
				billTonEdo = Double.parseDouble(CommonUtility.deNull(rssrlr.getString(6)));
				int fsdays = rssrlr.getInt("fsdays");
				billAcctNbr = getEdoBillAcctNbr(edoNbr);

				// for load vvcd in the jpjp cases
				/*
				 * Commented by Jade for SL-CAB-20111221-01 String loadvvcd = ""; String
				 * lvvcdsql =
				 * "select esn.out_voy_var_nbr from esn, tesn_jp_jp where esn.esn_asn_nbr=tesn_jp_jp.esn_asn_nbr and tesn_jp_jp.edo_asn_nbr='"
				 * +edoNbr+"'"; Statement stmtlv = gbmsconnection.createStatement(); ResultSet
				 * rslv = stmtlv.executeQuery(lvvcdsql);
				 * 
				 * if (transtype.equals("A")) if(rslv.next()) loadvvcd = rslv.getString(1);
				 * 
				 * stmtlv.close(); rslv.close();
				 */

				// Added by Jade for SL-CAB-20111221-01
				// To retrieve loading vessel for transhipment cargo based on esn number instead
				// of edo number
				log.info("sqlSRLR1--------------------This is a DN for Transhipment Cargo------------");
				String loadvvcd = getLoadVVCd(transtype, dn_nbr);
				log.info("sqlSRLR1--------------------loading vv code = " + loadvvcd);
				// End of adding by Jade for SL-CAB-20111221-01

				// for load vvcd in the jpjp cases
				// for ExpLeg scheme in jp jp cases
				String explegsch = "";
				String expschsql = "select CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  scheme from TOPS.VESSEL_CALL vc where vv_cd=:loadvvcd";

				paramMap.put("loadvvcd", loadvvcd);
				log.info(" *** CabDN SQL *****" + expschsql.toString() + " paramMap: " + paramMap);
				rsexpsch = namedParameterJdbcTemplate.queryForRowSet(expschsql.toString(), paramMap);
				if (transtype.equals("A"))
					if (rsexpsch.next())
						explegsch = rsexpsch.getString(1);

				if (transtype.equals("A") || transtype.equals("B"))
					mvmt = "TS";

				// for tenancy lease area check
				String satenind = getSATenInd(strvvcd);
				String sctenind = getSCTenInd(billAcctNbr);
				String codval = getCOD(strvvcd);

				// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08>
				// add new scheme for LCT, 20.feb.11 by hpeng
				// if(!schemeCd.equals("JNL") && !schemeCd.equals("JBT") &&
				// !schemeCd.equals("JLR"))
				if (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") && !schemeCd.equals("JLR")
						&& !schemeCd.equals("JWP") && !schemeCd.equals(ProcessChargeConst.LCT_SCHEME))
					schemeCd = "JLR";
				// if(transtype.equals("A") && !explegsch.equals("JNL") &&
				// !explegsch.equals("JBT") && !explegsch.equals("JLR"))
				// add new scheme for LCT, 20.feb.11 by hpeng
				if (transtype.equals("A") && !explegsch.equals("JNL") && !explegsch.equals("JBT")
						&& !explegsch.equals("JLR") && !explegsch.equals("JWP")
						&& !explegsch.equals(ProcessChargeConst.LCT_SCHEME))
					explegsch = "JLR";
				// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/>

				// ITH CASES added by BALAJI (10 April 2003)
				// Comment by Irene Tan on 16 August 2003 - GSL-2003-000076
				// uncomment by Irene Tan on 02 September 2003 - JPPL/IT/001/2001 - Phase 1b
				// (ITH Store Rent)
				// comment by Irene Tan on 26 February 2004 : to block ITH Store Rent
				/*
				 * if (transtype.equals("B")) { tariffMainCatCd="SR"; tariffSubCatCd="GL";
				 * mvmt="IT"; type="TN"; localLeg="IM"; discGateway="J"; }
				 */
				// end commented by Irene Tan on 26 February 2004
				// end uncomment by Irene Tan on 02 September 2003
				// end comment by Irene Tan on 16 August 2003
				// End ITH CASES added by BALAJI (10 April 2003)

				GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();

				log.info("**** START DN STORE RENT CHARGES ******");
				log.info("|discVvCd : " + strvvcd);
				log.info("|loadvvcd : " + loadvvcd);
				log.info("|vvInd : " + vvInd);
				log.info("|businessType : " + businessType);
				log.info("|schemeCd : " + schemeCd);
				log.info("|tariffMainCatCd : " + tariffMainCatCd);
				log.info("|tariffSubCatCd : " + tariffSubCatCd);
				log.info("|mvmt : " + mvmt);
				log.info("|type : " + type);
				log.info("|localLeg : " + localLeg);
				log.info("|discGateway : " + discGateway);
				log.info("|blNbr : " + blNbr);
				log.info("|dn_nbr : " + dn_nbr);
				log.info("|edoNbr : " + edoNbr);
				log.info("|billTonEdo : " + billTonEdo);
				log.info("|billAcctNbr : " + billAcctNbr);
				log.info("|tot_pack_dn : " + tot_pack_dn);
				log.info("|tot_pack_edo : " + tot_pack_edo);
				log.info("|lastModifyUserId : " + lastModifyUserId);
				log.info("|lastModifyDttm : " + lastModifyDttm);
				log.info("==== END DN STORE RENT CHARGES =====");
				log.info("SA Ten Ind " + satenind);
				log.info("SC Ten Ind " + sctenind);
				log.info("COD " + codval);
				log.info("Free store days " + fsdays);
				log.info("transtype -- " + transtype);

				generalEventLogValueObject.setDiscVvCd(strvvcd);
				generalEventLogValueObject.setLoadVvCd(loadvvcd);
				generalEventLogValueObject.setVvInd(vvInd);
				generalEventLogValueObject.setBusinessType(businessType);
				generalEventLogValueObject.setSchemeCd(schemeCd);
				generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
				generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
				generalEventLogValueObject.setMvmt(mvmt);
				generalEventLogValueObject.setType(type);
				generalEventLogValueObject.setLocalLeg(localLeg);
				generalEventLogValueObject.setDiscGateway(discGateway);
				generalEventLogValueObject.setBlNbr(blNbr);
				generalEventLogValueObject.setDnNbr(dn_nbr);
				generalEventLogValueObject.setEdoAsnNbr(edoNbr);
				generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
				// generalEventLogValueObject.setCountUnit(cnt_unit);
				generalEventLogValueObject.setBillTonEdo(billTonEdo);
				generalEventLogValueObject.setTotalPackDn(tot_pack_dn);
				generalEventLogValueObject.setTotalPackEdo(tot_pack_edo);
				generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
				// generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
				// add new scheme for LCT, 28.feb.11 by hpeng
				if (ProcessChargeConst.LCT_SCHEME.equals(schemeCd)) {
					generalEventLogValueObject.setLastModifyDttm(txnDttm);
				} else {
					generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
				}
				generalEventLogValueObject.setPrintDttm(lastModifyDttm);
				generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_DN);
				count5++;

				// for tenancy chk if both shipper/consignee and SA has no tenancy then trigger
				// Store Rent,
				// if cod is null while printing DN need not trigger Store Rent
				log.info(
						"checkAnyStoreRent returns " + processGBLogRepo.checkAnySR(generalEventLogValueObject, fsdays));

				if (satenind.equals("N") && transtype.equals("L")
						&& ((!billAcctNbr.equalsIgnoreCase("CASH") && sctenind.equals("N"))
								|| billAcctNbr.equalsIgnoreCase("CASH"))) {
					if (codval.equals("1") && processGBLogRepo.checkAnySR(generalEventLogValueObject, fsdays)) {
						GeneralEventLogArrayList.add(generalEventLogValueObject);
						billStoreInd = "Y";
					}
				} else if (!transtype.equals("L") && codval.equals("1")
						&& processGBLogRepo.checkAnySR(generalEventLogValueObject, fsdays)) {
					GeneralEventLogArrayList.add(generalEventLogValueObject);
					billStoreInd = "Y";
				}
				// add by hujun on 24/07/2011
				// generate the store rent billing for tenancy if the scheme is JLR
				// Amended by Jade for SL-CIM-20120112-01 to avoid duplicated event log
				// if (schemeCd.equals("JLR") && (satenind.equals("Y") ||
				// sctenind.equals("Y"))){

				// if (schemeCd.equals("JLR") && (satenind.equals("Y") || sctenind.equals("Y"))
				// && billStoreInd.equalsIgnoreCase("N") && codval.equals("1")){
				// addeby MC Consulting tenancy indicator has been removed for GC (it should
				// work for all regardless of tenancy indicator)
				if (schemeCd.equals("JLR") && billStoreInd.equalsIgnoreCase("N") && codval.equals("1")) {
					// End of amendments by Jade for SL-CIM-20120112-01
					if (processGBLogRepo.checkAnySR(generalEventLogValueObject, fsdays)) {
						GeneralEventLogArrayList.add(generalEventLogValueObject);
						billStoreInd = "Y";
					}
				}
				// add end
			} // while

			if (count5 > 0) {
				gbmsCabValueObject.setBill_storerent_triggered_ind("Y");
			}

			// JNL/JBT Store Rent charges
			log.info("SQL >> sqlSRNL1  : " + sqlSRNL1);

			paramMap.put("dnNbr", dn_nbr);
			log.info(" *** CabDN SQL *****" + sqlSRNL1.toString() + " paramMap: " + paramMap);
			rssrnl = namedParameterJdbcTemplate.queryForRowSet(sqlSRNL1.toString(), paramMap);
			while (rssrnl.next()) {

				strvvcd = CommonUtility.deNull(rssrnl.getString(1));
				String vvInd = ProcessChargeConst.DISC_VV_IND;
				String businessType = "G";
				String schemeCd = CommonUtility.deNull(rssrnl.getString(4));
				String tariffMainCatCd = "SR";
				String tariffSubCatCd = "US";
				String localLeg = "IM";
				String discGateway = "J";
				String lastModifyUserId = struserid;
				Timestamp lastModifyDttm = (java.sql.Timestamp) rssrnl.getObject("dndttm");
				String mvmt = "LL";
				String type = deriveType(rssrnl.getString(10), "TN");
				String edoNbr = CommonUtility.deNull(rssrnl.getString(3));
				String blNbr = CommonUtility.deNull(rssrnl.getString(9));
				String billAcctNbr = "";
				double billTonEdo = 0.0;
				/*
				 * double srdatediff =
				 * Double.parseDouble(CommonUtility.deNull(rssrnl.getString("srdatediff"))); int
				 * USchargedays = 0; BigDecimal bdUSd = new BigDecimal(srdatediff);
				 * 
				 * if((srdatediff*24)>2 && (srdatediff*24)<24) USchargedays = 1; else
				 * if((srdatediff*24)>24 ) USchargedays = bdUSd.intValue();
				 */

				// TODO: SETTING BILL-IND FOR SR US
				if (schemeCd.equalsIgnoreCase("JWP")) {
					String billInd = "N";
					log.info("**** cfg 0602 running unauth SR sql:  ******" + sqlSRNL1.toString());
					log.info("**** SETTING bill_ind TO N when JWP ( " + billInd + " )******");
					log.info("**** scheme_cd = " + schemeCd + "******");
				}

				int fsdays = rssrnl.getInt("fsdays");
				transtype = CommonUtility.deNull(rssrnl.getString("transtype"));
				int tot_pack_edo = 0;
				int tot_pack_dn = 0;
				transtype = CommonUtility.deNull(rssrnl.getString(11));
				crgcat = CommonUtility.deNull(rssrnl.getString(10));
				tot_pack_edo = rssrnl.getInt(7);
				tot_pack_dn = rssrnl.getInt(8);
				billTonEdo = Double.parseDouble(CommonUtility.deNull(rssrnl.getString(6)));
				String cargostatus = CommonUtility.deNull(rssrnl.getString(5));
				billAcctNbr = getEdoBillAcctNbr(edoNbr);
				if (cargostatus.equalsIgnoreCase("T") || cargostatus.equalsIgnoreCase("R")) {
					mvmt = "TS";
				}

				if (transtype.equals("T")) {
					mvmt = "LL";
					tariffSubCatCd = "GL";
				}

				// for load vvcd in the jpjp cases
				/*
				 * Commented by Jade for SL-CAB-20111221-01 String loadvvcd = ""; String
				 * lvvcdsql =
				 * "select esn.out_voy_var_nbr from esn, tesn_jp_jp where esn.esn_asn_nbr=tesn_jp_jp.esn_asn_nbr and tesn_jp_jp.edo_asn_nbr='"
				 * +edoNbr+"'"; Statement stmtlv = gbmsconnection.createStatement(); ResultSet
				 * rslv = stmtlv.executeQuery(lvvcdsql);
				 * 
				 * if(transtype.equals("A")) if(rslv.next()) loadvvcd = rslv.getString(1);
				 * 
				 * stmtlv.close(); rslv.close();
				 */
				// Added by Jade for SL-CAB-20111221-01
				// To retrieve loading vessel for transhipment cargo based on esn number instead
				// of edo number
				log.info("sqlSRNL1--------------------This is a DN for Transhipment Cargo------------");
				String loadvvcd = getLoadVVCd(transtype, dn_nbr);
				log.info("sqlSRNL1--------------------loading vv code = " + loadvvcd);
				// End of adding by Jade for SL-CAB-20111221-01

				// for load vvcd in the jpjp cases
				// for ExpLeg scheme in jp jp cases
				String explegsch = "";
				// mc consulting - Start - Change to get sub scheme
				// String expschsql = "select vc.scheme from vessel_call vc where
				// vv_cd='"+loadvvcd+"'";
				String expschsql = "select CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS scheme from TOPS.VESSEL_CALL vc where vv_cd=:loadvvcd";
				// mc consulting - End - Change to get sub scheme

				paramMap.put("loadvvcd", loadvvcd);
				log.info(" *** CabDN SQL *****" + expschsql.toString() + " paramMap: " + paramMap);
				rsexpsch = namedParameterJdbcTemplate.queryForRowSet(expschsql.toString(), paramMap);
				if (transtype.equals("A"))
					if (rsexpsch.next())
						explegsch = rsexpsch.getString(1);

				// for tenancy lease area check
				String satenind = getSATenInd(strvvcd);
				String sctenind = getSCTenInd(billAcctNbr);
				String codval = getCOD(strvvcd);

				// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08>
				// if(!schemeCd.equals("JNL") && !schemeCd.equals("JBT") &&
				// !schemeCd.equals("JLR"))
				// add new scheme for LCT, 13.feb.11 by hpeng
				if (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") && !schemeCd.equals("JLR")
						&& !schemeCd.equals("JWP") && !schemeCd.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME))
					schemeCd = "JLR";
				// if(transtype.equals("A") && !explegsch.equals("JNL") &&
				// !explegsch.equals("JBT") && !explegsch.equals("JLR"))
				if (transtype.equals("A") && !explegsch.equals("JNL") && !explegsch.equals("JBT")
						&& !explegsch.equals("JLR") && !explegsch.equals("JWP")
						&& !explegsch.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME))
					explegsch = "JLR";

				// if(transtype.equals("A") && (cargostatus.equals("T") ||
				// cargostatus.equals("R")) && (schemeCd.equals("JNL") ||
				// schemeCd.equals("JBT")) && explegsch.equals("JLR"))
				// add new scheme for LCT, 16.feb.11 by hpeng
				if (transtype.equals("A") && (cargostatus.equals("T") || cargostatus.equals("R"))
						&& (schemeCd.equals("JNL") || schemeCd.equals("JBT")
								|| schemeCd.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME))
						&& (explegsch.equals("JLR") || explegsch.equals("JWP")))
					tariffSubCatCd = "GL";
				// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/>

				// ITH CASES added by BALAJI (10 April 2003)
				// Comment by Irene Tan on 16 August 2003 - GSL-2003-000076
				// uncomment by Irene Tan on 02 September 2003 - JPPL/IT/001/2001 - Phase 1b
				// (ITH Store Rent)
				// commented by Irene Tan on 26 February 2004 - to bock ITH Store rent
				/*
				 * if (transtype.equals("B")) { tariffMainCatCd="SR"; tariffSubCatCd="GL";
				 * mvmt="IT"; type="TN"; localLeg="IM"; discGateway="J"; }
				 */
				// end comment by Irene Tan on 26 February 2004
				// end uncomment by Irene Ten on 02 September 2003
				// end comment by Irene Tan on 16 August 2003
				// End ITH CASES added by BALAJI (10 April 2003)

				GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();

				log.info("**** START DN STORE RENT CHARGES ******");
				log.info("|discVvCd : " + strvvcd);
				log.info("|loadvvcd : " + loadvvcd);
				log.info("|vvInd : " + vvInd);
				log.info("|businessType : " + businessType);
				log.info("|schemeCd : " + schemeCd);
				log.info("|tariffMainCatCd : " + tariffMainCatCd);
				log.info("|tariffSubCatCd : " + tariffSubCatCd);
				log.info("|mvmt : " + mvmt);
				log.info("|type : " + type);
				log.info("|localLeg : " + localLeg);
				log.info("|discGateway : " + discGateway);
				log.info("|blNbr : " + blNbr);
				log.info("|dn_nbr : " + dn_nbr);
				log.info("|edoNbr : " + edoNbr);
				log.info("|billTonEdo : " + billTonEdo);
				log.info("|billAcctNbr : " + billAcctNbr);
				// log.info("|cnt_unit : "+cnt_unit);
				log.info("|tot_pack_dn : " + tot_pack_dn);
				log.info("|tot_pack_edo : " + tot_pack_edo);
				log.info("|lastModifyUserId : " + lastModifyUserId);
				log.info("|lastModifyDttm : " + lastModifyDttm);
				log.info("==== END DN STORE RENT CHARGES =====");
				log.info("SA Ten Ind " + satenind);
				log.info("SC Ten Ind " + sctenind);
				log.info("COD " + codval);
				log.info("Free store days " + fsdays);

				generalEventLogValueObject.setDiscVvCd(strvvcd);
				generalEventLogValueObject.setLoadVvCd(loadvvcd);
				generalEventLogValueObject.setVvInd(vvInd);
				generalEventLogValueObject.setBusinessType(businessType);
				generalEventLogValueObject.setSchemeCd(schemeCd);
				generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
				generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
				generalEventLogValueObject.setMvmt(mvmt);
				generalEventLogValueObject.setType(type);
				generalEventLogValueObject.setLocalLeg(localLeg);
				generalEventLogValueObject.setDiscGateway(discGateway);
				generalEventLogValueObject.setBlNbr(blNbr);
				generalEventLogValueObject.setDnNbr(dn_nbr);
				generalEventLogValueObject.setEdoAsnNbr(edoNbr);
				generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
				// generalEventLogValueObject.setCountUnit(cnt_unit);
				generalEventLogValueObject.setBillTonEdo(billTonEdo);
				generalEventLogValueObject.setTotalPackDn(tot_pack_dn);
				generalEventLogValueObject.setTotalPackEdo(tot_pack_edo);
				generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
				// generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
				// add new scheme for LCT, 28.feb.11 by hpeng
				if (ProcessChargeConst.LCT_SCHEME.equals(schemeCd)) {
					generalEventLogValueObject.setLastModifyDttm(txnDttm);
				} else {
					generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
				}
				generalEventLogValueObject.setPrintDttm(lastModifyDttm);
				generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_DN);

				count6++;
				// if US (ie) imp leg sch is JNL or JBT and exp Leg sch is JNL or JBT
				// for tenancy chk if both shipper/consignee and SA has no tenancy then trigger
				// Store Rent,
				// if cod is null while printing DN need not trigger Store Rent
				log.info(
						"checkAnyStoreRent returns " + processGBLogRepo.checkAnySR(generalEventLogValueObject, fsdays));

				if (cargostatus.equals("L") && transtype.equals("L")) {
					if (satenind.equals("N") && codval.equals("1")
							&& ((!billAcctNbr.equalsIgnoreCase("CASH") && sctenind.equals("N"))
									|| billAcctNbr.equalsIgnoreCase("CASH")))
						if (processGBLogRepo.checkAnySR(generalEventLogValueObject, fsdays)) {
							GeneralEventLogArrayList.add(generalEventLogValueObject);
							billStoreInd = "Y";
						}
				} else if (transtype.equals("A")) {
					// TS JNL,JBT -> JNL,JBT,JLR
					if (codval.equals("1") && processGBLogRepo.checkAnySR(generalEventLogValueObject, fsdays)) {
						GeneralEventLogArrayList.add(generalEventLogValueObject);
						billStoreInd = "Y";
					}
				} else if (transtype.equals("T")) {
					// TS delivered locally, scheme can be JNL or JBT
					if (codval.equals("1") && processGBLogRepo.checkAnySR(generalEventLogValueObject, fsdays)) {
						GeneralEventLogArrayList.add(generalEventLogValueObject);
						billStoreInd = "Y";
					}
				} else if (transtype.equals("B")) {
					// JP-PSA CASE, scheme can be JNL or JBT
					if (codval.equals("1") && processGBLogRepo.checkAnySR(generalEventLogValueObject, fsdays)) {
						GeneralEventLogArrayList.add(generalEventLogValueObject);
						billStoreInd = "Y";
					}
				}

				// TODO: CHECKING BILL_IND SAVED
				log.info("**** cfg 0602 before vslTxnEventLogVO ******");
				log.info("**** GET ACTUAL VO.bill_ind = " + generalEventLogValueObject.getBillInd() + "******");
			}

			// while

			if (count6 > 0) {
				gbmsCabValueObject.setBill_storerent_triggered_ind("Y");
			}

			// Added MC consulting to avoid dupilcate storerent creation for DN
			String sql = "select bill_store_triggered_ind from GBMS.dn_details where dn_nbr=:dnNbr and dn_status='A'";

			paramMap.put("dnNbr", dn_nbr);
			log.info(" *** CabDN SQL *****" + sql + " paramMap: " + paramMap);
			rsdn = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rsdn.next()) {
				bill_store_triggered_ind = rsdn.getString("bill_store_triggered_ind");

			}
			// add new scheme for LCT, 16.mar.11 by hpeng
			// } //commented by Jade for SL-CAB-20141021-01: To trigger store rent upon DN
			// creation for LCT vessel even if LCT is closed

			/* STORE RENT END 02/10/2002 */
			log.info("count1 --from bean-----> " + count1);
			log.info("count2 --from bean-----> " + count2);
			log.info("count4 --from bean-----> " + count4);
			log.info("count5 --from bean SR-----> " + count5);
			log.info("count6 --from bean SR-----> " + count6);
			log.info("arraylist size----------------------------->" + GeneralEventLogArrayList.size());

		} catch (SQLException se) {
			log.info("Writing from TransactionLoggerEjb.CabDN");
			log.info("SQLException: " + se.getMessage());
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception CabDN :", e);
		} catch (NullPointerException e) {
			log.info("Exception CabDN :", e);
		} catch (Exception e) {
			log.info("Exception CabDN :", e);
		} finally {
			parameters.put("count1", count1);
			parameters.put("count2", count2);
			parameters.put("count4", count4);
			parameters.put("count5", count5);
			parameters.put("count6", count6);
			parameters.put("GeneralEventLogEDOArrayList", GeneralEventLogEDOArrayList);
			parameters.put("GeneralEventLogArrayList", GeneralEventLogArrayList);
			parameters.put("gbmsCabValueObject", gbmsCabValueObject);
			parameters.put("bill_store_triggered_ind", bill_store_triggered_ind);
			parameters.put("strvvcd", strvvcd);
			parameters.put("struserid", struserid);
			parameters.put("billStoreInd", billStoreInd);
			parameters.put("billWharfInd", billWharfInd);
			parameters.put("billSvcChargeInd", billSvcChargeInd);
			parameters.put("billProcessInd", billProcessInd);

			log.info("END: CabDN  DAO  END  ***** gbmsCabValueObject: "
					+ (gbmsCabValueObject == null ? "null" : gbmsCabValueObject.toString()));
		}
		return parameters;
	}// end of Cab DN billing

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: deriveType()
	// 06/06/2013 PCYAP To waive Empty Mafi wharfage charge
	public static String deriveType(String cargoCategoryCode, String defaultType) {

		log.info("START: deriveType DAO cargoCategoryCode: " + CommonUtility.deNull(cargoCategoryCode)
				+ ", defaultType: " + CommonUtility.deNull(defaultType));

		log.info("END: deriveType DAO : ");
		return (ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI.equalsIgnoreCase(cargoCategoryCode))
				? ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI
				: defaultType;
	}

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: getEdoBillAcctNbr()
	/**
	 * Gets Edo Billable account number.
	 * 
	 * @param gbmsconnection database connection object for GBMS database
	 * @param stredoasnnbr   EDO ASN No
	 * @return String EDO account no
	 * @exception BusinessException
	 */
	public String getEdoBillAcctNbr(String stredoasnnbr) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String billAcctNbr = "";
		String AcctNbr = "";
		String paymode = "";
		try {
			log.info("START: getEdoBillAcctNbr  DAO  Start Obj stredoasnnbr:" + CommonUtility.deNull(stredoasnnbr));
			sql.append(" SELECT EDO.ACCT_NBR, EDO.PAYMENT_MODE FROM GB_EDO EDO ");
			sql.append(" WHERE EDO.EDO_ASN_NBR=:stredoasnnbr ");

			paramMap.put("stredoasnnbr", stredoasnnbr);
			log.info(" *** getEdoBillAcctNbr SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				AcctNbr = CommonUtility.deNull(rs.getString("ACCT_NBR"));
				paymode = CommonUtility.deNull(rs.getString("PAYMENT_MODE"));
			}
			if (paymode.equalsIgnoreCase("C")) {
				billAcctNbr = "CASH";
			} else {
				billAcctNbr = AcctNbr;
			}
		} catch (NullPointerException e) {
			log.info("Exception getEdoBillAcctNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEdoBillAcctNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEdoBillAcctNbr  DAO  END  billAcctNbr: " + CommonUtility.deNull(billAcctNbr));
		}
		return billAcctNbr;
	}// end of Scheme Billable account number

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: getBABillAcctNbr()
	/**
	 * Added by Jade for CR-CAB-20130225-001 To get the billing account of berth
	 * applicant. If it's cash payment as specified in EDO, return CASH
	 * 
	 * @param con    DB connection
	 * @param vvCd   var code
	 * @param EdoNbr EDO number
	 * @return GB billing account of berth applicant
	 * @throws BusinessException
	 */
	private String getBABillAcctNbr(String vvCd, String EdoNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		String result = "";

		boolean isCashPayment = isCashPayment(EdoNbr);
		log.info("isCashPayment = " + isCashPayment);

		if (isCashPayment)
			return "CASH";

		try {
			log.info("START: getBABillAcctNbr  DAO  Start Obj vvCd:" + CommonUtility.deNull(vvCd) + "EdoNbr:"
					+ CommonUtility.deNull(EdoNbr));
			sql.append(" SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD =:vvCd ");

			paramMap.put("vvCd", vvCd);
			log.info(" *** getBABillAcctNbr SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				result = CommonUtility.deNull(rs.getString(1));
			}
			result = result == null ? "" : result.trim();
			log.info("result = " + result);
		} catch (NullPointerException e) {
			log.info("Exception getBABillAcctNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBABillAcctNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBABillAcctNbr  DAO  END **** result: " + CommonUtility.deNull(result));
		}
		return result;
	}

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: isShutoutCargoDN()
	// add by hujun on 2/8/2011 for shutout cargo billing
	public boolean isShutoutCargoDN(String dn_nbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		String shutoutInd = null;
		try {
			log.info("START: isShutoutCargoDN  DAO  Start Obj dn_nbr:" + CommonUtility.deNull(dn_nbr));

			sql.append(" SELECT edo.shutout_ind from gb_edo edo, GBMS.dn_details dn where dn.dn_nbr=:dnNbr ");
			sql.append(" and dn.edo_asn_nbr=edo.edo_asn_nbr ");

			paramMap.put("dnNbr", dn_nbr);
			log.info(" *** isShutoutCargoDN SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				shutoutInd = rs.getString("shutout_ind");
			}
			return (shutoutInd != null && shutoutInd.equalsIgnoreCase("Y"));
		} catch (NullPointerException e) {
			log.info("Exception isShutoutCargoDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isShutoutCargoDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isShutoutCargoDN  DAO  END *** shutoutInd: "
					+ (shutoutInd != null && shutoutInd.equalsIgnoreCase("Y")));
		}
	}

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: isClosedLct()
	private boolean isClosedLct(String vvCd) throws SQLException, BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		String closeLctInd = null;
		try {
			log.info("START: isClosedLct  DAO  Start Obj vvCd:" + CommonUtility.deNull(vvCd));
			sql.append(" SELECT gb_close_lct_ind FROM TOPS.VESSEL_CALL WHERE vv_cd=:vvCd ");

			paramMap.put("vvCd", vvCd);
			log.info(" *** isClosedLct SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				closeLctInd = rs.getString("gb_close_lct_ind");
			}
			return (closeLctInd != null && closeLctInd.equalsIgnoreCase("Y"));
		} catch (NullPointerException e) {
			log.info("Exception isClosedLct :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isClosedLct :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isClosedLct  DAO  END *** closeLctInd: "
					+ (closeLctInd != null && closeLctInd.equalsIgnoreCase("Y")));
		}
	}

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: getSchemeName()
	/**
	 * Retrieves the vessel scheme code of the vessel var code.
	 * 
	 * @param gbmsconnection database connection object for GBMS database
	 * @param strvvcd        vessel var code
	 * @return String vessel scheme name
	 * @exception BusinessException
	 */
	public String getSchemeName(String strvvcd) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		String actualscheme = "";
		String schemecd = "";
		String subScheme = "";
		String gcOperations = "";
		String abcd = "";

		try {
			log.info("START: getSchemeName  DAO  Start Obj strvvcd:" + CommonUtility.deNull(strvvcd));
			sql.append(" SELECT VESCALL.SCHEME, VESCALL.COMBI_GC_SCHEME, VESCALL.COMBI_GC_OPS_IND,VS.AB_CD FROM ");
			sql.append(" TOPS.VESSEL_CALL VESCALL, VESSEL_SCHEME VS WHERE VESCALL.SCHEME=VS.SCHEME_CD AND ");
			sql.append(" VESCALL.VV_CD=:strvvcd ");

			paramMap.put("strvvcd", strvvcd);
			log.info(" *** getSchemeName SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				schemecd = CommonUtility.deNull(rs.getString("SCHEME"));
				abcd = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				subScheme = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));
			}

			if (!(abcd.equalsIgnoreCase(""))) {
				if (schemecd.equalsIgnoreCase("JLR"))
					actualscheme = schemecd;
			}
			if (schemecd.equalsIgnoreCase("JNL")) {
				actualscheme = schemecd;
			}
			if (schemecd.equalsIgnoreCase("JBT")) {
				actualscheme = schemecd;
			}
			// <cfg: add new scheme for Wooden Craft: JWP, 02.jun.08>
			if (schemecd.equalsIgnoreCase("JWP")) {
				actualscheme = schemecd;
			} else {
				actualscheme = "00";
			}
			// add new scheme for LCT, 13.feb.11 by hpeng
			if (schemecd.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME)) {
				actualscheme = schemecd;
			}

			if (gcOperations.equalsIgnoreCase("Y")) {
				actualscheme = subScheme;
			}
			log.info("END: *** getSchemeName Result *****" + CommonUtility.deNull(actualscheme));

		} catch (NullPointerException e) {
			log.info("Exception getSchemeName :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSchemeName :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeName  DAO  END");
		}
		return actualscheme;
	}// end of getSchemeName

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: getVvCdByDnNbr()
	private String getVvCdByDnNbr(String dnNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		String vvCd = null;
		try {
			log.info("START: getVvCdByDnNbr  DAO  Start Obj dnNbr:" + CommonUtility.deNull(dnNbr));
			sql.append(" SELECT var_nbr FROM gb_edo e, GBMS.dn_details d WHERE e.edo_asn_nbr = d.edo_asn_nbr ");
			sql.append(" AND d.dn_nbr =:dnNbr ");

			paramMap.put("dnNbr", dnNbr);
			log.info(" *** getVvCdByDnNbr SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				vvCd = rs.getString("var_nbr");
			}
		} catch (NullPointerException e) {
			log.info("Exception getVvCdByDnNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVvCdByDnNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVvCdByDnNbr  DAO  END  vvCd: " + CommonUtility.deNull(vvCd));
		}
		return vvCd;
	}

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: triggerShutoutCargoDN()
	public Map<String, Object> triggerShutoutCargoDN(String dn_nbr, String userId) throws BusinessException {
		List<GeneralEventLogValueObject> GeneralEventLogArrayList = new ArrayList<GeneralEventLogValueObject>();
		// MCC for 1st DN creation
		List<GeneralEventLogValueObject> GeneralEventLogEDOArrayList = new ArrayList<GeneralEventLogValueObject>();
		String crgcat = "";

		String billWharfInd = "N";
		String billSvcChargeInd = "N";
		String billStoreInd = "N";
		String billProcessInd = "N";
		String vvCd = null;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		GbmsCabValueObject gbmsCabValueObject = new GbmsCabValueObject();

		try {
			log.info("START: triggerShutoutCargoDN DAO dn_nbr:" + CommonUtility.deNull(dn_nbr) + "userId:"
					+ CommonUtility.deNull(userId));
			gbmsCabValueObject.setStatus("FALSE");

			/* Wharfage bill */
			StringBuilder sb = new StringBuilder();
			sb.append(
					"SELECT edo.esn_asn_nbr, vc.vv_cd, dn.dn_nbr, dn.edo_asn_nbr, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS scheme,");
			sb.append("       edo.crg_status,");
			sb.append("       GREATEST (edo.nom_wt / 1000, edo.nom_vol) AS edo_bill_ton,");
			sb.append("       edo.nbr_pkgs, dn.nbr_pkgs AS dn_nbr_pkgs, dn.trans_type AS transtype,");
			sb.append("       edo.dis_type distype, dn.trans_dttm AS dndttm");
			sb.append("  FROM TOPS.VESSEL_CALL vc, vessel_scheme vs, gb_edo edo, GBMS.dn_details dn, esn es"); // MCC
																												// added
																												// for
			// EPC_IND
			sb.append(" WHERE dn.edo_asn_nbr = edo.edo_asn_nbr");
			sb.append("   AND edo.var_nbr = vc.vv_cd");
			sb.append("   AND edo.esn_asn_nbr = es.esn_asn_nbr");
			sb.append("   AND nvl(es.epc_ind,'N') = 'N' "); // for EPC_IND check
			sb.append("   AND vc.scheme = vs.scheme_cd");
			sb.append("   AND dn.dn_status = 'A'");
			sb.append("   AND edo.edo_status = 'A'");
			sb.append("   AND dn.bill_wharf_triggered_ind = 'N'");
			sb.append("   AND edo.bill_wharf_triggered_ind = 'N'");
			sb.append("   AND edo.shutout_ind = 'Y'");
			sb.append("   AND dn.dn_nbr =:dnNbr ");

			String sqlwharf1 = sb.toString();

			txnDttm = getSystemDate();

			vvCd = getVvCdByDnNbr(dn_nbr);

			// MCC check is this is first active shut-out dn for the shutout EDO
			boolean isFirstDN = processGBLogRepo.isFirstDNForEDO(dn_nbr);
			log.info("isFirstActiveDN for shutout EDO : " + isFirstDN);

			// process wharfage
			log.info(" *** triggerShutoutCargoDN SQL *****" + sqlwharf1);
			paramMap.put("dnNbr", dn_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlwharf1.toString(), paramMap);
			while (rs.next()) {
				String vvInd = ProcessChargeConst.DISC_VV_IND;
				String businessType = "G";
				String tariffMainCatCd = "WF";
				String tariffSubCatCd = "SO";
				String localLeg = "IM";
				String discGateway = "J";
				String lastModifyUserId = userId;
				String mvmt = "00";
				String type = "00";

				Timestamp lastModifyDttm = (Timestamp) rs.getObject("dndttm");
				double billTonEdo = Double.parseDouble(CommonUtility.deNull(rs.getString("EDO_BILL_TON")));
				String esnAsnNbr = CommonUtility.deNull(rs.getString("ESN_ASN_NBR"));
				String edoNbr = CommonUtility.deNull(rs.getString("EDO_ASN_NBR"));
				String scheme = CommonUtility.deNull(rs.getString("SCHEME"));

				// add on 13/9/2011
				if (ProcessChargeConst.NON_LINER_SCHEME.equalsIgnoreCase(scheme)
						|| ProcessChargeConst.BARTER_TRADER_SCHEME.equalsIgnoreCase(scheme)
						|| ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(scheme)
						|| ProcessChargeConst.LINER_SCHEME.equalsIgnoreCase(scheme)
						|| ProcessChargeConst.WOODEN_CRAFT_SCHEME.equalsIgnoreCase(scheme)) {
					// don't change if not customer scheme
				} else {
					scheme = ProcessChargeConst.LINER_SCHEME;
				}
				// add end

				int tot_pack_edo = rs.getInt("NBR_PKGS");
				int tot_pack_dn = rs.getInt("DN_NBR_PKGS");
				String billAcctNbr = getEdoBillAcctNbr(edoNbr);

				int countUnit = tot_pack_edo;

				GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();

				log.info("**** DN WHARF CHARGES ******");
				log.info("|discVvCd : " + vvCd);
				log.info("|loadvvcd : " + vvCd);
				log.info("|vvInd : " + vvInd);
				log.info("|businessType : " + businessType);
				log.info("|schemeCd : " + scheme);
				log.info("|tariffMainCatCd : " + tariffMainCatCd);
				log.info("|tariffSubCatCd : " + tariffSubCatCd);
				log.info("|mvmt : " + mvmt);
				log.info("|type : " + crgcat);
				log.info("|localLeg : " + localLeg);
				log.info("|discGateway : " + discGateway);
				log.info("|dn_nbr : " + dn_nbr);
				log.info("|edoNbr : " + edoNbr);
				log.info("|billTonEdo : " + billTonEdo);
				log.info("|billAcctNbr : " + billAcctNbr);
				log.info("|cnt_unit : " + countUnit);
				log.info("|tot_pack_dn : " + tot_pack_dn);
				log.info("|tot_pack_edo : " + tot_pack_edo);
				log.info("|refInd : " + ProcessChargeConst.REF_IND_DN);
				log.info("|lastModifyUserId : " + lastModifyUserId);
				log.info("|lastModifyDttm : " + lastModifyDttm);
				log.info("==== END DN OF WHARF CHARGES ====");

				generalEventLogValueObject.setDiscVvCd(vvCd);
				generalEventLogValueObject.setLoadVvCd(vvCd);
				generalEventLogValueObject.setVvInd(vvInd);
				generalEventLogValueObject.setBusinessType(businessType);
				generalEventLogValueObject.setSchemeCd(scheme);
				generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
				generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
				generalEventLogValueObject.setMvmt(mvmt);
				generalEventLogValueObject.setType(type);
				generalEventLogValueObject.setLocalLeg(localLeg);
				generalEventLogValueObject.setDiscGateway(discGateway);
				generalEventLogValueObject.setDnNbr(dn_nbr);
				generalEventLogValueObject.setEdoAsnNbr(edoNbr);
				generalEventLogValueObject.setEsnAsnNbr(esnAsnNbr);
				generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
				generalEventLogValueObject.setCountUnit(countUnit);
				generalEventLogValueObject.setBillTonEdo(billTonEdo);
				generalEventLogValueObject.setTotalPackDn(tot_pack_dn);
				generalEventLogValueObject.setTotalPackEdo(tot_pack_edo);
				generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_DN);
				generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
				generalEventLogValueObject.setPrintDttm(lastModifyDttm);
				if (ProcessChargeConst.LCT_SCHEME.equals(scheme)) {
					generalEventLogValueObject.setLastModifyDttm(txnDttm);
				} else {
					generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
				}

				GeneralEventLogArrayList.add(generalEventLogValueObject);

				// MCC add EDO Service Charge event for 1st shutout DN creation
				if (isFirstDN) {
					GeneralEventLogEDOArrayList.add(generalEventLogValueObject);
				}

				billWharfInd = "Y";
				gbmsCabValueObject.setBillWharfTriggeredInd("Y");
			}
			// process storerent
			sb = new StringBuilder();
			sb.append(
					"SELECT edo.esn_asn_nbr,vc.vv_cd, dn.dn_nbr, dn.edo_asn_nbr, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS scheme, edo.crg_status,");
			sb.append("       GREATEST (edo.nom_wt / 1000, edo.nom_vol) AS edo_bill_ton,");
			sb.append("       edo.nbr_pkgs, dn.nbr_pkgs AS dn_nbr_pkgs,");
			sb.append("       dn.trans_type AS transtype, edo.dis_type distype,");
			sb.append("       dn.trans_dttm AS dndttm, NVL (edo.free_stg_days, 0) fsdays");
			sb.append("  FROM TOPS.VESSEL_CALL vc,");
			sb.append("       vessel_scheme vs,");
			sb.append("       TOPS.BERTHING b,");
			sb.append("       gb_edo edo,");
			sb.append("       esn es,"); // MCC for EPC_IND check
			sb.append("       GBMS.dn_details dn");
			sb.append(" WHERE EDO.var_nbr = vc.vv_cd");
			sb.append("   AND vc.vv_cd = b.vv_cd");
			sb.append("   AND b.shift_ind = 1");
			sb.append("   AND dn.edo_asn_nbr = edo.edo_asn_nbr");
			sb.append("   AND edo.esn_asn_nbr = es.esn_asn_nbr");
			sb.append("   AND nvl(es.epc_ind,'N') = 'N' "); // for EPC_IND check
			sb.append("   AND vc.scheme = vs.scheme_cd");
			sb.append("   AND dn.dn_status = 'A'");
			sb.append("   AND edo.edo_status = 'A'");
			sb.append("   AND dn.bill_store_triggered_ind = 'N'");
			sb.append("   AND edo.wh_ind <> 'Y'");
			sb.append("   AND dn.trans_type NOT IN ('B')");
			sb.append("   AND dn.dn_nbr =:dnNbr ");

			String sqlStoreRent = sb.toString();

			log.info(" *** triggerShutoutCargoDN SQL *****" + sqlStoreRent);
			paramMap.put("dnNbr", dn_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlStoreRent.toString(), paramMap);
			while (rs.next()) {
				String vvInd = ProcessChargeConst.DISC_VV_IND;
				String businessType = "G";
				String tariffMainCatCd = "SR";
				String tariffSubCatCd = "SO";
				String localLeg = "IM";
				String discGateway = "J";
				String lastModifyUserId = userId;
				String mvmt = "LL";
				String type = "TN";

				String scheme = CommonUtility.deNull(rs.getString("SCHEME"));
				// add on 13/9/2011
				if (ProcessChargeConst.NON_LINER_SCHEME.equalsIgnoreCase(scheme)
						|| ProcessChargeConst.BARTER_TRADER_SCHEME.equalsIgnoreCase(scheme)
						|| ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(scheme)
						|| ProcessChargeConst.LINER_SCHEME.equalsIgnoreCase(scheme)
						|| ProcessChargeConst.WOODEN_CRAFT_SCHEME.equalsIgnoreCase(scheme)) {
					// don't change if not customer scheme
				} else {
					scheme = ProcessChargeConst.LINER_SCHEME;
				}
				// add end

				Timestamp lastModifyDttm = (Timestamp) rs.getObject("DNDTTM");
				double billTonEdo = Double.parseDouble(CommonUtility.deNull(rs.getString("EDO_BILL_TON")));
				String esnAsnNbr = CommonUtility.deNull(rs.getString("ESN_ASN_NBR"));
				String edoNbr = CommonUtility.deNull(rs.getString("EDO_ASN_NBR"));

				int tot_pack_edo = rs.getInt("NBR_PKGS");
				int tot_pack_dn = rs.getInt("DN_NBR_PKGS");
				String billAcctNbr = getEdoBillAcctNbr(edoNbr);
				int fsdays = rs.getInt("FSDAYS");

				int countUnit = tot_pack_dn;

				GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();

				log.info("**** DN STORERENT CHARGES ******");
				log.info("|discVvCd : " + vvCd);
				log.info("|loadvvcd : " + vvCd);
				log.info("|vvInd : " + vvInd);
				log.info("|businessType : " + businessType);
				log.info("|schemeCd : " + scheme);
				log.info("|tariffMainCatCd : " + tariffMainCatCd);
				log.info("|tariffSubCatCd : " + tariffSubCatCd);
				log.info("|mvmt : " + mvmt);
				log.info("|type : " + crgcat);
				log.info("|localLeg : " + localLeg);
				log.info("|discGateway : " + discGateway);
				log.info("|dn_nbr : " + dn_nbr);
				log.info("|edoNbr : " + edoNbr);
				log.info("|billTonEdo : " + billTonEdo);
				log.info("|billAcctNbr : " + billAcctNbr);
				log.info("|cnt_unit : " + countUnit);
				log.info("|tot_pack_dn : " + tot_pack_dn);
				log.info("|tot_pack_edo : " + tot_pack_edo);
				log.info("|refInd : " + ProcessChargeConst.REF_IND_DN);
				log.info("|lastModifyUserId : " + lastModifyUserId);
				log.info("|lastModifyDttm : " + lastModifyDttm);
				log.info("==== END DN OF STORERENT CHARGES ====");

				generalEventLogValueObject.setDiscVvCd(vvCd);
				generalEventLogValueObject.setLoadVvCd(vvCd);
				generalEventLogValueObject.setVvInd(vvInd);
				generalEventLogValueObject.setBusinessType(businessType);
				generalEventLogValueObject.setSchemeCd(scheme);
				generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
				generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
				generalEventLogValueObject.setMvmt(mvmt);
				generalEventLogValueObject.setType(type);
				generalEventLogValueObject.setLocalLeg(localLeg);
				generalEventLogValueObject.setDiscGateway(discGateway);
				generalEventLogValueObject.setDnNbr(dn_nbr);
				generalEventLogValueObject.setEdoAsnNbr(edoNbr);
				generalEventLogValueObject.setEsnAsnNbr(esnAsnNbr);
				generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
				generalEventLogValueObject.setCountUnit(countUnit);
				generalEventLogValueObject.setBillTonEdo(billTonEdo);
				generalEventLogValueObject.setTotalPackDn(tot_pack_dn);
				generalEventLogValueObject.setTotalPackEdo(tot_pack_edo);
				generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_DN);
				generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
				generalEventLogValueObject.setPrintDttm(lastModifyDttm);
				if (ProcessChargeConst.LCT_SCHEME.equals(scheme)) {
					generalEventLogValueObject.setLastModifyDttm(txnDttm);
				} else {
					generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
				}

				// New EVM ENhancement Sripriya 2nd April 2018 to not overwrite the business
				// type
				boolean checkAnyStoreRentResult = processGBLogRepo.checkAnySR(generalEventLogValueObject, fsdays);
				log.info("checkAnyStoreRent returns " + checkAnyStoreRentResult);

				if (checkAnyStoreRentResult) {
					GeneralEventLogArrayList.add(generalEventLogValueObject);
					billStoreInd = "Y";
				}
				gbmsCabValueObject.setBill_storerent_triggered_ind("Y");
			}

		} catch (BusinessException e) {
			log.info("Exception triggerShutoutCargoDN :", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception triggerShutoutCargoDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception triggerShutoutCargoDN :", e);
			throw new BusinessException("M4201");
		} finally {
			parameters.put("billStoreInd", billStoreInd);
			parameters.put("billWharfInd", billWharfInd);
			parameters.put("billSvcChargeInd", billSvcChargeInd);
			parameters.put("billProcessInd", billProcessInd);
			parameters.put("userId", userId);
			parameters.put("vvCd", vvCd);
			parameters.put("gbmsCabValueObject", gbmsCabValueObject);
			parameters.put("GeneralEventLogEDOArrayList", GeneralEventLogEDOArrayList);
			parameters.put("GeneralEventLogArrayList", GeneralEventLogArrayList);
			parameters.put("gbmsCabValueObject", gbmsCabValueObject);
			log.info("END: triggerShutoutCargoDN  DAO  END *** parameters: " + parameters.toString());
		}
		return parameters;
	}

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: TriggerUa()
	/**
	 * This method will log the extracted data for charges triggered at UA to CAB
	 * for billing.
	 * 
	 * @param strcode   UA No
	 * @param struserid user id who creates the UA
	 * @param vvcd      vessel var code
	 * @throws Exception
	 */
	@Transactional(rollbackFor = BusinessException.class)
	public String TriggerUa(String strcode, String struserid, String vvcd) throws BusinessException {
		String updatestatus = "FALSE";
		String billWharfInd = "N";
		String billSvcChargeInd = "N";
		String billStoreInd = "N";
		String billProcessInd = "N";
		int sizeuaexportsc = 0;
		int sizeuaexportwc = 0;
		int sizeuaexportsr = 0;

		try {
			log.info("START: TriggerUa DAO strcode:" + CommonUtility.deNull(strcode) + "struserid:"
					+ CommonUtility.deNull(struserid) + "vvcd:" + CommonUtility.deNull(vvcd));

			txnDttm = getSystemDate();
			List<GeneralEventLogValueObject> GeneralEventLogArrayList = new ArrayList<GeneralEventLogValueObject>();
			List<GeneralEventLogValueObject> GeneralEventLogArrayList1 = new ArrayList<GeneralEventLogValueObject>();
			List<GeneralEventLogValueObject> GeneralEventLogArrayList2 = new ArrayList<GeneralEventLogValueObject>();
			List<GeneralEventLogValueObject> GeneralEventLogArrayList3 = new ArrayList<GeneralEventLogValueObject>();

			// add new scheme for LCT, 13.feb.11 by hpeng
			if (!ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(getSchemeName(vvcd))) {

				log.info("CALLED CAB UA Service Charge METHOD ddddd");
				log.info(" GeneralEventLogArrayList1: " + sizeuaexportsc);
				log.info("ducta1 >>>>strcode>>>>>>>>>>>>>>>>>>>>>" + strcode);
				log.info("ducta1 >>>>struserid>>>>>>>>>>>>>>>>>>>>>" + struserid);

				GeneralEventLogArrayList1 = cabUaExportServiceCharge.processDetails(strcode, struserid);
				sizeuaexportsc = GeneralEventLogArrayList1.size();
				log.info("ducta1 >>>>>>>>>>>>>>>>>>>>>>>>>" + sizeuaexportsc);
				for (int i = 0; i < sizeuaexportsc; i++) {
					GeneralEventLogArrayList.add((GbmsCargoBillingValueObject) GeneralEventLogArrayList1.get(i));
				}
				log.info("CALLED CAB UA Service Charge METHOD");
				log.info("======================================");
				log.info(" GeneralEventLogArrayList1: " + sizeuaexportsc);
				GeneralEventLogArrayList2 = cabUaExportWharfCharge.processDetails(strcode, struserid);
				// cabUaExportServiceCharge.processDetails(gbmsconnection,strcode,struserid);

				sizeuaexportwc = GeneralEventLogArrayList2.size();
				for (int i = 0; i < sizeuaexportwc; i++) {
					GeneralEventLogArrayList.add((GbmsCargoBillingValueObject) GeneralEventLogArrayList2.get(i));
				}
				log.info("CALLED CAB UA Wharf Charge METHOD");
				log.info(" GeneralEventLogArrayList2: " + sizeuaexportwc);

			}

			// added for store rent start 02/10/2002
			log.info("CALLED CAB UA Store Rent METHOD");
			log.info(" GeneralEventLogArrayList3: " + sizeuaexportsr);

			GeneralEventLogArrayList3 = cabUaExportStoreRentCharge.processDetails(strcode, struserid);
			sizeuaexportsr = GeneralEventLogArrayList3.size();
			for (int i = 0; i < sizeuaexportsr; i++) {
				GeneralEventLogArrayList.add((GbmsCargoBillingValueObject) GeneralEventLogArrayList3.get(i));
			}
			log.info("CALLED CAB UA Store Rent METHOD");
			log.info("======================================");
			log.info(" GeneralEventLogArrayList3: " + sizeuaexportsr);

			// added for store rent end 02/10/2002
			if (sizeuaexportsc > 0) {
				billSvcChargeInd = "Y";
			}
			if (sizeuaexportwc > 0) {
				billWharfInd = "Y";
			}
			if (sizeuaexportsr > 0) {
				billStoreInd = "Y";
			}

			log.info("event log size from transaction logger ejb --->>>>--- >>>>>> " + GeneralEventLogArrayList.size());
			if (GeneralEventLogArrayList.size() > 0) {
				VesselTxnEventLogValueObject vesselTxnEventLogValueObject = new VesselTxnEventLogValueObject();
				vesselTxnEventLogValueObject.setVvCd(vvcd);
				vesselTxnEventLogValueObject.setTxnDttm(txnDttm);
				vesselTxnEventLogValueObject.setBillWharfInd(billWharfInd);
				vesselTxnEventLogValueObject.setBillSvcChargeInd(billSvcChargeInd);
				vesselTxnEventLogValueObject.setBillStoreInd(billStoreInd);
				vesselTxnEventLogValueObject.setBillProcessInd(billProcessInd);
				vesselTxnEventLogValueObject.setLastModifyUserId(struserid);
				vesselTxnEventLogValueObject.setLastModifyDttm(txnDttm);
				log.info("**** VESSEL TXN EVENT LOG FOR UA VOS ******");
				log.info("|strvvcd : " + vvcd);
				log.info("|txnDttm : " + txnDttm);
				log.info("|billWharfInd : " + billWharfInd);
				log.info("|billSvcChargeInd : " + billSvcChargeInd);
				log.info("|billStoreInd : " + billStoreInd);
				log.info("|billProcessInd : " + billProcessInd);
				log.info("|struserid : " + struserid);
				log.info("|txnDttm : " + txnDttm);
				log.info("==== END OF VESSEL TXN EVENT LOG FOR UA VOS ====");
				List<ChargeableBillValueObject> ChargeableBillArrayList = new ArrayList<ChargeableBillValueObject>();
				log.info("**** Process Log created for UA Before ExecuteBillCharge New method call******");
				// EVM Implementation to use the old method to not overwrite the business type
				ChargeableBillArrayList = processGBLogRepo.executeBillCharges(vesselTxnEventLogValueObject,
						GeneralEventLogArrayList, ProcessChargeConst.REF_IND_UA);
				// EVM Implementation to use the old method to not overwrite the business type
				log.info("**** END of  Log created for UA Added Store Indicator******"
						+ vesselTxnEventLogValueObject.getBillStoreInd());
				int i = ChargeableBillArrayList.size();
				log.info("ChargeableBillArrayList :" + i);
			}

			if (sizeuaexportsc > 0 || sizeuaexportwc > 0) {
				/*
				 * Statement stmt = gbmsconnection.createStatement();
				 * sql="UPDATE UA_DETAILS SET "
				 * +" BILL_WHARF_TRIGGERED_IND ='"+bill_wharf_triggered_ind
				 * +"', BILL_SERVICE_TRIGGERED_IND ='"+bill_service_triggered_ind
				 * +"', LAST_MODIFY_DTTM = sysdate," +" LAST_MODIFY_USER_ID = '"+struserid
				 * +"' WHERE UA_NBR ='"+strcode+"' AND UA_STATUS='A'"; int
				 * count=stmt.executeUpdate(sql); if (count == 0) {
				 * sessionContext.setRollbackOnly(); //throw new BusinessException("M1007");
				 * }else{ gbmsconnection.commit(); updatestatus="TRUE"; } stmt.close();
				 */
			}
			log.info("ALL OVER SENDS TRUE");
		} catch (NullPointerException e) {
			log.info("Exception TriggerUa :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception TriggerUa :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception TriggerUa :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: TriggerUa  DAO  END  updatestatus: " + updatestatus);
		}
		return updatestatus;
	}// end of Ua billing

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: getTariffSubCat()
	/**
	 * Gets Tariff sub cat methods returons different category
	 * 
	 * @param cargo_category_cd special cargo category code
	 * @return String tariff sub category code
	 */
	public String getTariffSubCat(String cargo_category_cd) {
		log.info("START getTariffSubCat :: cargo_category_cd: " + CommonUtility.deNull(cargo_category_cd));
		String tariff_sub_cat = "GL";
		if (cargo_category_cd.equalsIgnoreCase("00")) {
			tariff_sub_cat = "GL";
		}
		if (cargo_category_cd.equalsIgnoreCase("01") || cargo_category_cd.equalsIgnoreCase("02")
				|| cargo_category_cd.equalsIgnoreCase("03")) {
			tariff_sub_cat = "RO";
		}
		if (cargo_category_cd.equalsIgnoreCase("WA") || cargo_category_cd.equalsIgnoreCase("LS")) {
			tariff_sub_cat = "AN";
		}
		log.info("END: getTariffSubCat  DAO  tariff_sub_cat: " + CommonUtility.deNull(tariff_sub_cat));
		return tariff_sub_cat;
	}

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: getSATenInd()
	/**
	 * Gets Tenancy or lease Ind for the shipping agent
	 * 
	 * @param gbmsConnection database connection object for GBMS database
	 * @param vv_cd          vessel var code
	 * @return String tenancy indicator
	 * @exception BusinessException
	 */
	public String getSATenInd(String vv_cd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		StringBuilder sql = new StringBuilder();
		String sTenInd = "";

		try {
			log.info("START: getSATenInd  DAO  Start Obj vv_cd:" + CommonUtility.deNull(vv_cd));
			sql.append(" select cust.tenancy_ind from TOPS.VESSEL_CALL vc, ");
			sql.append(" TOPS.CUSTOMER cust,cust_acct cact where vc.bill_acct_nbr = cact.acct_nbr ");
			sql.append(" and cust.CUST_CD = cact.CUST_CD  and vc.vv_cd =:vvCd ");

			paramMap.put("vvCd", vv_cd);
			log.info(" *** getSATenInd SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				sTenInd = CommonUtility.deNull(rs.getString("tenancy_ind"));
			}
		} catch (NullPointerException e) {
			log.info("Exception getSATenInd :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSATenInd :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSATenInd  DAO  END  sTenInd: " + CommonUtility.deNull(sTenInd));
		}
		return sTenInd;
	}// end of getSATenInd

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: getSCTenInd()
	/**
	 * Gets Tenancy or lease Ind for Shipper or Consignee
	 * 
	 * @param gbmsConnection database connection object for GBMS database
	 * @param acctnbr        consignee/shipper account no
	 * @return String tenancy indicator for consignee/shipper
	 * @exception BusinessException
	 */
	public String getSCTenInd(String acctnbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		String sTenInd = "";

		try {
			log.info("START: getSCTenInd  DAO  Start Obj acctnbr:" + CommonUtility.deNull(acctnbr));
			sql.append(" select cust.tenancy_ind from ");
			sql.append(" TOPS.CUSTOMER cust,cust_acct cact where ");
			sql.append(" cust.CUST_CD = cact.CUST_CD  and cact.acct_nbr =:acctNbr ");

			paramMap.put("acctNbr", acctnbr);
			log.info(" *** getSCTenInd SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				sTenInd = CommonUtility.deNull(rs.getString("tenancy_ind"));
			}
		} catch (NullPointerException e) {
			log.info("Exception getSCTenInd :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSCTenInd :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSCTenInd  DAO  END  sTenInd: " + CommonUtility.deNull(sTenInd));
		}
		return sTenInd;
	}// end of getSCTenInd

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: getCOD()
	/**
	 * Gets GB_COD dttm from berthing
	 * 
	 * @param gbmsConnection database connection object for GBMS database
	 * @param vv_cd          vessel var code
	 * @return String tenancy indicator
	 * @exception BusinessException
	 */
	public String getCOD(String strvvcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		String codval = "0";
		try {
			log.info("START: getCOD  DAO  Start Obj strvvcd:" + CommonUtility.deNull(strvvcd));
			sql.append(" SELECT GB_COD_DTTM FROM TOPS.BERTHING B WHERE SHIFT_IND = 1 AND B.VV_CD=:strvvcd ");

			paramMap.put("strvvcd", strvvcd);
			log.info(" *** getCOD SQL *****" + sql.toString());
			log.info(" *** getCOD paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				codval = CommonUtility.deNull(rs.getString("GB_COD_DTTM"));
			}
			if (codval != null && !codval.equals(""))
				codval = "1";
		} catch (NullPointerException e) {
			log.info("Exception getCOD :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCOD :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCOD  DAO  END  codval: " + CommonUtility.deNull(codval));
		}
		return codval;
	}// end of get COD dttm

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: getLoadVVCd()
	// Added by Jade for SL-CAB-20111221-01
	// To retrieve loading vessel for transhipment cargo based on esn number instead
	// of edo number
	private String getLoadVVCd(String transType, String dnNbr) throws BusinessException {
		String result = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		if (transType.equalsIgnoreCase("A")) {
			try {
				log.info("START: getLoadVVCd  DAO  Start Obj transType:" + CommonUtility.deNull(transType) + "dnNbr:"
						+ CommonUtility.deNull(dnNbr));
				sql.append(" select out_voy_var_nbr from esn where esn_asn_nbr = (select tesn_asn_nbr ");
				sql.append(" from GBMS.dn_details where dn_nbr =:dnNbr ) ");

				paramMap.put("dnNbr", dnNbr);
				log.info(" *** getLoadVVCd SQL *****" + sql.toString() + " paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
				if (rs.next()) {
					result = rs.getString(1);
					result = result == null ? "" : result.trim();
				}
				log.info("--------------------Loading Vessel is " + CommonUtility.deNull(result) + "for DN "
						+ CommonUtility.deNull(dnNbr) + " --------------------");
			} catch (NullPointerException e) {
				log.info("Exception getLoadVVCd :", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getLoadVVCd :", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getLoadVVCd  DAO  END *** result: " + CommonUtility.deNull(result));
			}
		}
		return result;
	} // End of adding by Jade for SL-CAB-20111221-01

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: isCashPayment()
	/**
	 * Added by Jade for CR-CAB-20130225-001 To check if an EDO is to be paid by
	 * Cash or billing acct
	 * 
	 * @param con    DB connection
	 * @param EdoNbr EDO number
	 * @return true or false
	 * @throws BusinessException
	 */
	private boolean isCashPayment(String EdoNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		boolean result = false;
		String pmtMode = "";

		try {
			log.info("START: isCashPayment  DAO  Start Obj EdoNbr:" + CommonUtility.deNull(EdoNbr));
			sql.append(" SELECT PAYMENT_MODE FROM GB_EDO WHERE EDO_ASN_NBR =:EdoNbr ");

			paramMap.put("EdoNbr", EdoNbr);
			log.info(" *** isCashPayment SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				pmtMode = CommonUtility.deNull(rs.getString(1));
			}
			log.info("pmtMode = " + pmtMode);

			if (pmtMode != null && pmtMode.trim().equalsIgnoreCase("C")) {
				result = true;
			}
		} catch (NullPointerException e) {
			log.info("Exception isCashPayment :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isCashPayment :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isCashPayment  DAO  END  result: " + result);
		}
		return result;
	} // End of adding by Jade for CR-CAB-20130225-001

	// IProcessStoreRent
	public Timestamp getTimestamp(Object o, String s) throws BusinessException {
		return null;
	}

	// package: jp.src.cab.processCharges-->ProcessCommon
	// method: computeBillTon()
	public double computeBillTon(double edoBillTon, int edoPkgs, int dnPkgs) throws BusinessException {
		double billTon = 0.0;

		try {
			log.info("START:computeBillTon" + "edoBillTon" + CommonUtility.deNull(String.valueOf(edoBillTon))
					+ "edoPkgs:" + CommonUtility.deNull(String.valueOf(edoPkgs)) + "dnPkgs:"
					+ CommonUtility.deNull(String.valueOf(dnPkgs)));
			// billTon = (edoBillTon / edoPkgs) * dnPkgs;
			// log.info("billTon: " + billTon);
			// BigDecimal bigDec = new BigDecimal (billTon);
			// bigDec = bigDec.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			billTon = (new BigDecimal("" + (edoBillTon / edoPkgs * dnPkgs)).setScale(2, RoundingMode.HALF_UP)
					.doubleValue());
			log.info("billTon: " + billTon);
		} catch (Exception ex) {
			log.info("Exception computeBillTon :", ex);
			throw new BusinessException("[Exception] when calculating the billable tonnage: " + ex.getMessage());
		} finally {
			log.info("END: computeBillTon  DAO  END *** billTon: " + billTon);
		}
		return billTon;
	}

	// jp.src.valueObject.cab.billing-->BillAdjustParamFactory
	// method: create()
	public BillAdjustParam create(String tariffCode) throws RemoteException, BusinessException, Exception {

		int ind[] = null;
		try {
			log.info("START create DAO:: tariffCode:" + CommonUtility.deNull(tariffCode));
			ind = billSupportInfoRepo.getIndicator(tariffCode);
		} catch (BusinessException be) {
			log.info("Exception create :", be);
			String s = "[BillAdjustParamFactory] BusinessException: " + be.getMessage();
			log.info(s);
			throw be;
		} catch (Exception e) {
			log.info("Exception create :", e);
			String s = "[BillAdjustParamFactory] Exception: " + e.getMessage();
			log.info(s);
			throw e;
		} finally {
			log.info("END: create  DAO  END *** billTon: "
					+ (getParam(ind) == null ? "null" : getParam(ind).toString()));
		}
		return getParam(ind);
	}

	// jp.src.valueObject.cab.billing-->BillAdjustParamFactory
	// method: BillAdjustParam()
	private static BillAdjustParam getParam(int ind[]) {
		log.info("START: getParam  DAO  Start Obj ind:" + ind);
		if (ind == null)
			return null;

		BillAdjustParam retVal = null;
		if (ind[0] == 1 && ind[1] == 0 && ind[2] == 0) {
			retVal = new BillAdjParamCVO();
		} else if (ind[0] == 0 && ind[1] == 1 && ind[2] == 0) {
			retVal = new BillAdjParamTVO();
		} else if (ind[0] == 0 && ind[1] == 0 && ind[2] == 1) {
			retVal = new BillAdjParamOVO();
		} else if (ind[0] == 1 && ind[1] == 1 && ind[2] == 0) {
			retVal = new BillAdjParamCTVO();
		} else if (ind[0] == 1 && ind[1] == 0 && ind[2] == 1) {
			retVal = new BillAdjParamCOVO();
		} else if (ind[0] == 0 && ind[1] == 1 && ind[2] == 1) {
			retVal = new BillAdjParamTOVO();
		} else if (ind[0] == 1 && ind[1] == 1 && ind[2] == 1) {
			retVal = new BillAdjParamCVO();
		} else {
			retVal = null;
		}
		log.info("END: getParam  DAO  END *** billTon: " + (retVal == null ? "null" : retVal.toString()));
		return retVal;
	}

	// Commented the CabImportBj method for JPOM Migration Issue started
	/*
	 * // ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->CabImportBj() private
	 * GbmsCabValueObject CabImportBj(String strvvcd, String struserid) throws
	 * BusinessException { List<GeneralEventLogValueObject> GeneralEventLogArrayList
	 * = new ArrayList<GeneralEventLogValueObject>();
	 * 
	 * SqlRowSet rs1 = null; SqlRowSet rs2 = null; SqlRowSet rs3 = null; SqlRowSet
	 * rs4 = null; SqlRowSet rs5 = null;
	 * 
	 * StringBuilder sqlNLTS = new StringBuilder(); StringBuilder sql1 = new
	 * StringBuilder(); StringBuilder sql2 = new StringBuilder(); StringBuilder sql3
	 * = new StringBuilder(); StringBuilder sql5 = new StringBuilder();
	 * 
	 * // added condition 'AND MD.UNSTUFF_SEQ_NBR=0' in the queries to Supress
	 * Charges // for Containerised Cargo Manifest -- 17th Sept,03 Vani. // ducta1
	 * starts on 23/12/2008 // Modified by Ding Xijia(harbortek) 30-Jan-2011 : START
	 * sqlNLTS.
	 * append(" SELECT MD.CRG_STATUS, (GREATEST(GROSS_WT/1000,GROSS_VOL)/MD.NBR_PKGS) * "
	 * ); sqlNLTS.
	 * append(" (MD.NBR_PKGS-NBR_PKGS_IN_PORT) AS BL_BILL_TON, MD.BL_NBR BLNBR, ");
	 * sqlNLTS.
	 * append(" MD.CARGO_CATEGORY_CD,CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN ");
	 * sqlNLTS.
	 * append(" VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH,MD.MIXED_SCHEME_ACCT_NBR MACCTNO, "
	 * ); sqlNLTS.
	 * append(" VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND, MD.NBR_PKGS FROM "
	 * ); sqlNLTS.append(" MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS ");
	 * // SL-CAB-20090723-01 change from md.edo_nbr_pkgs to nbr_pkgs sqlNLTS.
	 * append(" WHERE MD.VAR_NBR = VC.VV_CD AND VC.SCHEME = VS.SCHEME_CD AND MD.BL_STATUS = 'A' "
	 * ); sqlNLTS.append(" AND MD.CRG_STATUS IN ('T','R') AND ");
	 * sqlNLTS.append(" MD.BILL_WHARF_TRIGGERED_IND = 'N' AND ");
	 * sqlNLTS.append(" MD.CRG_TYPE NOT IN ('00','01','02','03') AND ");
	 * sqlNLTS.append(
	 * " MD.GB_CLOSE_BJ_IND = 'Y' AND ( VC.SCHEME IN ('JNL','JBT', '" +
	 * ProcessChargeConst.LCT_SCHEME + "') "); sqlNLTS.
	 * append(" OR (UPPER(VC.COMBI_GC_OPS_IND) = 'Y' AND VC.COMBI_GC_SCHEME IN ('JNL'))) "
	 * ); sqlNLTS.append(" AND MD.VAR_NBR =:strvvcd AND MD.UNSTUFF_SEQ_NBR=0 ");
	 * 
	 * sql1.
	 * append(" SELECT EDO.EDO_ASN_NBR, EDO.CRG_STATUS, GREATEST(EDO.NOM_WT/1000, EDO.NOM_VOL) "
	 * ); sql1.
	 * append(" AS EDO_BILL_TON, EDO.NBR_PKGS, MD.BL_NBR	BLNBR, MD.CARGO_CATEGORY_CD,CASE WHEN "
	 * ); sql1.
	 * append(" VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS "
	 * ); sql1.
	 * append(" SCH,MD.MIXED_SCHEME_ACCT_NBR MACCTNO, VC.BILL_ACCT_NBR VACCTNO, ");
	 * sql1.
	 * append(" VC.MIXED_SCHEME_IND MSIND,EDO.ACCT_NBR EDOACCTNO, MD.EPC_IND EPCIND, "
	 * ); sql1.append(" EDO.DIS_TYPE EDODISTYPE, MD.NBR_PKGS "); //
	 * SL-CAB-20090723-01 change from md.edo_nbr_pkgs to nbr_pkgs //MCC get EPC_IND
	 * sql1.append(" FROM  MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS, "
	 * );
	 * sql1.append(" GB_EDO EDO WHERE MD.VAR_NBR = VC.VV_CD AND MD.MFT_SEQ_NBR = ");
	 * sql1.
	 * append(" EDO.MFT_SEQ_NBR AND VC.SCHEME = VS.SCHEME_CD AND MD.BL_STATUS = 'A' AND "
	 * ); sql1.
	 * append(" EDO.EDO_STATUS =	'A'	AND EDO.DIS_TYPE IN ('O')  AND EDO.PAYMENT_MODE "
	 * ); sql1.
	 * append(" = 'A' AND MD.BILL_WHARF_TRIGGERED_IND = 'N' AND MD.CRG_TYPE NOT ");
	 * sql1.
	 * append(" IN ('00','01','02','03') AND MD.GB_CLOSE_BJ_IND = 'Y' AND ( VC.SCHEME IN "
	 * ); sql1.append(" ('JNL','JBT','" + ProcessChargeConst.LCT_SCHEME +
	 * "') OR (UPPER(VC.COMBI_GC_OPS_IND) "); sql1.
	 * append(" = 'Y' AND VC.COMBI_GC_SCHEME IN ('JNL'))) AND MD.VAR_NBR =:strvvcd AND MD.UNSTUFF_SEQ_NBR=0 "
	 * );
	 * 
	 * sql2.
	 * append(" SELECT EDO.EDO_ASN_NBR, EDO.CRG_STATUS, GREATEST(EDO.NOM_WT/1000, EDO.NOM_VOL) "
	 * ); sql2.
	 * append(" AS EDO_BILL_TON, EDO.NBR_PKGS, MD.BL_NBR	BLNBR, MD.CARGO_CATEGORY_CD,CASE WHEN "
	 * ); sql2.
	 * append(" VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH, "
	 * ); sql2.
	 * append(" MD.MIXED_SCHEME_ACCT_NBR MACCTNO, VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND "
	 * ); sql2.
	 * append(" MSIND,EDO.ACCT_NBR EDOACCTNO, MD.EPC_IND EPCIND, MD.NBR_PKGS FROM "
	 * ); sql2.append(" MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS, ");
	 * // SL-CAB-20090723-01 change from md.edo_nbr_pkgs to nbr_pkgs //MCC get
	 * EPC_IND sql2.
	 * append(" GB_EDO EDO WHERE MD.VAR_NBR = VC.VV_CD AND MD.MFT_SEQ_NBR = EDO.MFT_SEQ_NBR AND "
	 * ); sql2.
	 * append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END = "
	 * ); sql2.
	 * append(" VS.SCHEME_CD AND MD.BL_STATUS = 'A' AND EDO.EDO_STATUS = 'A' AND EDO.DIS_TYPE "
	 * ); sql2.
	 * append(" IN ('O') AND EDO.PAYMENT_MODE = 'A' AND MD.BILL_WHARF_TRIGGERED_IND = 'N' AND "
	 * ); sql2.append(" MD.CRG_TYPE NOT IN ('00','01','02','03') AND "); // <cfg:
	 * add new scheme for Wooden Craft 'JWP', 23.may.08> // +
	 * " MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME = 'JLR' OR"
	 * sql2.append(" MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME IN ('JLR','JWP') OR ");
	 * sql2.
	 * append(" (UPPER(VC.COMBI_GC_OPS_IND) = 'Y' AND VC.COMBI_GC_SCHEME IN ('JLR')) OR "
	 * ); // <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/> sql2.
	 * append(" VS.AB_CD IS NOT NULL) AND MD.VAR_NBR =:strvvcd AND MD.UNSTUFF_SEQ_NBR=0 "
	 * );
	 * 
	 * sql3.
	 * append(" SELECT MD.CRG_STATUS, (GREATEST(GROSS_WT/1000,GROSS_VOL)/MD.NBR_PKGS) * "
	 * ); sql3.
	 * append(" (MD.NBR_PKGS-NBR_PKGS_IN_PORT) AS BL_BILL_TON, MD.BL_NBR BLNBR, ");
	 * sql3.
	 * append(" MD.CARGO_CATEGORY_CD AS CRGCAT,CASE WHEN VC.COMBI_GC_OPS_IND ='Y' "
	 * ); sql3.
	 * append(" THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH,MD.MIXED_SCHEME_ACCT_NBR MACCTNO, "
	 * ); // Amended on 28/08/2008 by Ai Lin - For Overside SC // sql3.append("
	 * VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND FROM // MANIFEST_DETAILS
	 * MD, VESSEL_CALL VC, VESSEL_SCHEME VS "); // sql3.append(" VC.BILL_ACCT_NBR
	 * VACCTNO, VC.MIXED_SCHEME_IND MSIND, // MD.DIS_TYPE FROM MANIFEST_DETAILS MD,
	 * VESSEL_CALL VC, VESSEL_SCHEME VS "); // //<cfg merged Ai Lin with Ducta
	 * changes 22.Apr.09> sql3.
	 * append(" VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND, MD.DIS_TYPE, MD.NBR_PKGS FROM "
	 * ); sql3.append(" MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS "); //
	 * SL-CAB-20090723-01 change from md.edo_nbr_pkgs to nbr_pkgs // End amended on
	 * 28/08/2008 by Ai Lin - For Overside SC sql3.
	 * append(" WHERE MD.VAR_NBR = VC.VV_CD AND CASE WHEN VC.COMBI_GC_OPS_IND ='Y' "
	 * ); sql3.
	 * append(" THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = VS.SCHEME_CD AND ");
	 * sql3.
	 * append(" MD.BL_STATUS = 'A' AND MD.BILL_SERVICE_TRIGGERED_IND = 'N' AND ");
	 * sql3.append(" MD.CRG_TYPE NOT IN ('00','01','02','03') AND "); // <cfg: add
	 * new scheme for Wooden Craft 'JWP', 23.may.08> //
	 * sql3.append(" MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME = 'JLR' OR "); //
	 * sql3.append(" MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME IN ('JLR','JWP') OR ");
	 * // //MCConsulting commented and added JCL below // MCConsulting add JCL
	 * scheme sql3.
	 * append(" MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME IN ('JLR','JWP','JCL') OR "
	 * ); sql3.
	 * append(" (UPPER(VC.COMBI_GC_OPS_IND) = 'Y' AND VC.COMBI_GC_SCHEME IN ('JLR')) OR "
	 * ); // <cfg: add new scheme for Wooden Craft 'JWP', 23.may.;08/>
	 * sql3.append(" VS.AB_CD IS NOT NULL) AND MD.VAR_NBR =:strvvcd AND ");
	 * sql3.append(" MD.UNSTUFF_SEQ_NBR=0 ORDER BY BLNBR ");
	 * 
	 * // MCConsulting add new query for WF for LCT to charge to manifest bill sql5.
	 * append(" SELECT MD.CRG_STATUS, (GREATEST(GROSS_WT/1000,GROSS_VOL)/MD.NBR_PKGS) * "
	 * ); sql5.
	 * append(" (MD.NBR_PKGS-NBR_PKGS_IN_PORT) AS BL_BILL_TON, MD.BL_NBR BLNBR, ");
	 * sql5.
	 * append(" MD.CARGO_CATEGORY_CD AS CRGCAT,CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN "
	 * ); sql5.
	 * append(" VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH,MD.MIXED_SCHEME_ACCT_NBR MACCTNO, "
	 * ); sql5.
	 * append(" VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND, MD.DIS_TYPE, MD.NBR_PKGS FROM "
	 * ); sql5.
	 * append(" MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS WHERE MD.VAR_NBR = VC.VV_CD "
	 * ); sql5.
	 * append(" AND VC.SCHEME = VS.SCHEME_CD AND MD.BL_STATUS = 'A' AND MD.CRG_STATUS IN ('L') AND "
	 * ); sql5.
	 * append(" MD.BILL_WHARF_TRIGGERED_IND = 'N' AND MD.CRG_TYPE NOT IN ('00','01','02','03') AND "
	 * ); sql5.
	 * append(" MD.GB_CLOSE_BJ_IND = 'Y' AND CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN "
	 * ); sql5.append(" VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END IN ('JCL') "); sql5.
	 * append(" AND MD.VAR_NBR =:strvvcd AND MD.UNSTUFF_SEQ_NBR=0 ORDER BY BLNBR ");
	 * 
	 * // Modified by Ding Xijia(harbortek) 30-Jan-2011 : END
	 * 
	 * String sql3 = "SELECT  " +
	 * " MD.CRG_STATUS, (GREATEST(GROSS_WT/1000,GROSS_VOL)/MD.NBR_PKGS) * (MD.NBR_PKGS-NBR_PKGS_IN_PORT) "
	 * +
	 * " AS BL_BILL_TON, MD.BL_NBR BLNBR, MD.CARGO_CATEGORY_CD AS CRGCAT,CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH,MD.MIXED_SCHEME_ACCT_NBR MACCTNO,"
	 * +
	 * " VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND FROM  MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS, GB_EDO EDO "
	 * + " WHERE MD.VAR_NBR = VC.VV_CD AND " +
	 * " VC.SCHEME = VS.SCHEME_CD AND MD.BL_STATUS = 'A' AND" +
	 * " MD.BILL_SERVICE_TRIGGERED_IND = 'N' AND" +
	 * " MD.CRG_TYPE NOT IN ('00','01','02','03') AND" //<cfg: add new scheme for
	 * Wooden Craft 'JWP', 23.may.08> //+
	 * " MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME = 'JLR' OR" +
	 * " MD.GB_CLOSE_BJ_IND = 'Y' AND (CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END IN ('JLR','JWP') OR"
	 * //<cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/> +
	 * " VS.AB_CD IS NOT NULL) AND MD.VAR_NBR = '"
	 * +strvvcd+"' AND MD.UNSTUFF_SEQ_NBR=0 " +
	 * " AND EDO.MFT_SEQ_NBR = MD.MFT_SEQ_NBR AND EDO.DIS_TYPE <> 'O' ORDER BY BLNBR"
	 * ; //<cfg: SC for overside Cargo, 07.aug.08>
	 * 
	 * String sql5 = "SELECT EDO.EDO_ASN_NBR, EDO.CRG_STATUS, " +
	 * " GREATEST(EDO.NOM_WT/1000, EDO.NOM_VOL) " +
	 * " AS EDO_BILL_TON, EDO.NBR_PKGS, MD.BL_NBR BLNBR, MD.CARGO_CATEGORY_CD AS CRGCAT, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH, MD.MIXED_SCHEME_ACCT_NBR MACCTNO,"
	 * +
	 * " VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND, EDO.ACCT_NBR EDOACCTNO FROM  MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS,"
	 * + " GB_EDO EDO WHERE MD.VAR_NBR = VC.VV_CD AND MD.MFT_SEQ_NBR =" +
	 * " EDO.MFT_SEQ_NBR AND" +
	 * " VC.SCHEME = VS.SCHEME_CD AND MD.BL_STATUS = 'A' AND" +
	 * " EDO.EDO_STATUS = 'A' AND EDO.DIS_TYPE = 'O' AND EDO.PAYMENT_MODE = 'A' " +
	 * " AND MD.BILL_SERVICE_TRIGGERED_IND = 'N' AND" +
	 * " MD.CRG_TYPE NOT IN ('00','01','02','03') AND" +
	 * " MD.GB_CLOSE_BJ_IND = 'Y' AND (CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END IN ('JLR','JWP') OR"
	 * + " VS.AB_CD IS NOT NULL) AND MD.VAR_NBR = '"
	 * +strvvcd+"' AND MD.UNSTUFF_SEQ_NBR=0";
	 * 
	 * // <cfg: SC for overside Cargo, 07.aug.08/> // ducta1 end String billWharfInd
	 * = "N"; String billSvcChargeInd = "N";
	 * 
	 * String billProcessInd = "N"; GbmsCabValueObject gbmsCabValueObject = new
	 * GbmsCabValueObject(); int count1 = 0; int count2 = 0; int count3 = 0; int
	 * count4 = 0; int count5 = 0; // MCC gbmsCabValueObject.setStatus("FALSE");
	 * Map<String, String> paramMap = new HashMap<String, String>(); try {
	 * log.info("START: CabImportBj  DAO  Start Obj strvvcd:" +
	 * CommonUtility.deNull(strvvcd) + " struserid:" +
	 * CommonUtility.deNull(struserid));
	 * 
	 * txnDttm = getSystemDate();
	 * 
	 * paramMap.put("strvvcd", strvvcd); log.info(" *** CabImportBj SQL *****" +
	 * sqlNLTS.toString()); log.info(" *** CabImportBj paramMap *****" +
	 * paramMap.toString()); rs4 =
	 * namedParameterJdbcTemplate.queryForRowSet(sqlNLTS.toString(), paramMap);
	 * List<String> tempBlV = new ArrayList<String>();
	 * 
	 * while (rs4.next()) {
	 * 
	 * String discVvCd = strvvcd; String vvInd = ProcessChargeConst.DISC_VV_IND;
	 * String businessType = "G"; String schemeCd = rs4.getString("SCH"); String
	 * tariffMainCatCd = "WF"; String tariffSubCatCd = "OV"; String type =
	 * deriveType(rs4.getString("CARGO_CATEGORY_CD"), "00"); String localLeg = "IM";
	 * // discgateway to be changed String discGateway = "J"; String
	 * lastModifyUserId = struserid; Timestamp lastModifyDttm = txnDttm; String mvmt
	 * = "00"; String blNbr;
	 * 
	 * String billAcctNbr = ""; double billTonBl;
	 * 
	 * // String vactno = rs4.getString("VACCTNO");
	 * 
	 * blNbr = CommonUtility.deNull(rs4.getString("BLNBR"));
	 * 
	 * String cargostatus = CommonUtility.deNull(rs4.getString(1)); billTonBl =
	 * Double.parseDouble(CommonUtility.deNull(rs4.getString(2)));
	 * 
	 * int countUnit =
	 * Integer.parseInt(CommonUtility.deNull(rs4.getString("NBR_PKGS")));// ducta1
	 * starts on // 23/12/2008 // // SL-CAB-20090723-01 // change from //
	 * md.edo_nbr_pkgs // to nbr_pkgs
	 * 
	 * // ducta1 start on 20/12/2008 if (cargostatus.equalsIgnoreCase("T") ||
	 * cargostatus.equalsIgnoreCase("R")) { // because the cargo status in this case
	 * is T or R mvmt = "TS"; tariffSubCatCd = "GL";
	 * 
	 * // use temp variable to avoid effect to old logic for general cargo String
	 * typeForRORO = rs4.getString("CARGO_CATEGORY_CD");// ducta1 starts on
	 * 23/12/2008 // process for RORO if
	 * (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(
	 * typeForRORO)) {
	 * 
	 * // MCC if there is TESN JP-JP for JNL as disc scheme then do not set type as
	 * // RORO
	 * 
	 * boolean isTESNJPJP = isTESNJPJP(blNbr); if(isTESNJPJP &&
	 * schemeCd.equalsIgnoreCase("JNL")){ LogManager.instance.
	 * logInfo("**The Bill has RORO cargoes delivered to TESN JP-JP vessel *** ");
	 * //do not set tariff sub cat as RORO }else {
	 * 
	 * 
	 * type = typeForRORO;// re-assign value of type for RORO
	 * 
	 * // value of sub tariff code is "RO" tariffSubCatCd =
	 * ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
	 * 
	 * // } }
	 * 
	 * // mvmt = "TS";
	 * 
	 * // tariffSubCatCd = "GL";//ducta1 starts on 23/12/2008 (comment) //
	 * -----------------
	 * 
	 * // Changed by Liu Foong(NCS) on 28/3/05: GBMS - Mixed scheme not catered for
	 * // non-liner // billAcctNbr = rs4.getString("VACCTNO"); String tempmact =
	 * rs4.getString("MACCTNO"); if (tempmact != null && !tempmact.equals("") &&
	 * !tempmact.equals("null")) { billAcctNbr = rs4.getString("MACCTNO"); } else {
	 * // MCC Commented the following line and included the isGcOperations due to
	 * wrong // CT Account logged instead of GB Account // billAcctNbr=
	 * rs4.getString("VACCTNO"); billAcctNbr = isGcOperations(strvvcd) ?
	 * getCustBillAcctNbr(strvvcd) : rs4.getString("VACCTNO"); } // End of Changed
	 * by Liu Foong }
	 * 
	 * if (!tempBlV.contains(blNbr)) { log.info("**** WHARF CHARGES ******");
	 * log.info("|blNbr : " + blNbr); log.info("|discVvCd : " + discVvCd);
	 * log.info("|vvInd : " + vvInd); log.info("|businessType : " + businessType);
	 * log.info("|schemeCd : " + schemeCd); log.info("|tariffMainCatCd : " +
	 * tariffMainCatCd); log.info("|tariffSubCatCd : " + tariffSubCatCd);
	 * log.info("|mvmt : " + mvmt); log.info("|type : " + type);
	 * log.info("|localLeg : " + localLeg); log.info("|discGateway : " +
	 * discGateway); log.info("|billTonBl : " + billTonBl);
	 * log.info("|billAcctNbr : " + billAcctNbr); // added by Balaji
	 * log.info("|refInd : " + ProcessChargeConst.REF_IND_BL); // end added by
	 * Balaji log.info("|lastModifyUserId : " + lastModifyUserId);
	 * log.info("|lastModifyDttm : " + lastModifyDttm);
	 * log.info("==== END OF WHARF CHARGES ====");
	 * 
	 * GeneralEventLogValueObject generalEventLogValueObject = new
	 * GeneralEventLogValueObject();
	 * generalEventLogValueObject.setDiscVvCd(discVvCd);
	 * generalEventLogValueObject.setVvInd(vvInd);
	 * generalEventLogValueObject.setBusinessType(businessType);
	 * generalEventLogValueObject.setSchemeCd(schemeCd);
	 * generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
	 * generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
	 * generalEventLogValueObject.setMvmt(mvmt);
	 * generalEventLogValueObject.setType(type);
	 * generalEventLogValueObject.setLocalLeg(localLeg);
	 * generalEventLogValueObject.setDiscGateway(discGateway);
	 * generalEventLogValueObject.setBlNbr(blNbr);
	 * generalEventLogValueObject.setBillTonBl(billTonBl);
	 * generalEventLogValueObject.setBillAcctNbr(billAcctNbr); // added by Balaji
	 * generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_BL); // end
	 * added by Balaji
	 * generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
	 * generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
	 * generalEventLogValueObject.setCountUnit(countUnit);// ducta1 starts on
	 * 23/12/2008 count4++;
	 * GeneralEventLogArrayList.add(generalEventLogValueObject); tempBlV.add(blNbr);
	 * } // if ! tempBL V } // end-while for rs4
	 * 
	 * if (count4 > 0) { billWharfInd = "Y";
	 * gbmsCabValueObject.setBillWharfTriggeredInd("Y"); }
	 * 
	 * txnDttm = getSystemDate(); log.info("txnDttm : " + txnDttm);
	 * 
	 * paramMap.put("strvvcd", strvvcd); log.info(" *** CabImportBj SQL *****" +
	 * sql1.toString()); log.info(" *** CabImportBj paramMap *****" +
	 * paramMap.toString()); rs1 =
	 * namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap); while
	 * (rs1.next()) { String discVvCd = strvvcd; String vvInd =
	 * ProcessChargeConst.DISC_VV_IND; String businessType = "G"; String schemeCd =
	 * rs1.getString("SCH"); String tariffMainCatCd = "WF"; String tariffSubCatCd =
	 * "OV"; String type = deriveType(rs1.getString("CARGO_CATEGORY_CD"), "00");
	 * String localLeg = "IM"; // discgateway to be changed String discGateway =
	 * "J"; String lastModifyUserId = struserid; Timestamp lastModifyDttm = txnDttm;
	 * String mvmt = "00"; String blNbr; String edoAsnNbr; String billAcctNbr = "";
	 * double billTonEdo;
	 * 
	 * // String vactno = rs1.getString("VACCTNO");
	 * 
	 * blNbr = CommonUtility.deNull(rs1.getString("BLNBR")); edoAsnNbr =
	 * CommonUtility.deNull(rs1.getString(1)); String cargostatus =
	 * CommonUtility.deNull(rs1.getString(2)); billTonEdo =
	 * Double.parseDouble(CommonUtility.deNull(rs1.getString(3))); billAcctNbr =
	 * rs1.getString("EDOACCTNO");
	 * 
	 * // String deliveryToEPC = CommonUtility.deNull(rs1.getString("EPCIND"));
	 * //MCC // get EPC_IND
	 * 
	 * int countUnit =
	 * Integer.parseInt(CommonUtility.deNull(rs1.getString("NBR_PKGS")));// ducta1
	 * starts on // 23/12/2008 // // SL-CAB-20090723-01 // change from //
	 * md.edo_nbr_pkgs // to nbr_pkgs
	 * 
	 * 
	 * if (cargostatus.equalsIgnoreCase("T") || cargostatus.equalsIgnoreCase("R")) {
	 * mvmt="TS"; tariffSubCatCd = "GL"; billAcctNbr = rs1.getString("VACCTNO"); }
	 * 
	 * String typeForRORO = rs1.getString("CARGO_CATEGORY_CD");
	 * 
	 * if ((rs1.getString("EDODISTYPE")).equals("O") &&
	 * (cargostatus.equalsIgnoreCase("T") || cargostatus.equalsIgnoreCase("R"))) {
	 * tariffSubCatCd = "OV"; mvmt = "00"; // ducta1 starts on 23/12/2008
	 * 
	 * if (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(
	 * typeForRORO)) { tariffSubCatCd =
	 * ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
	 * 
	 * type = typeForRORO;
	 * 
	 * mvmt = "TS"; } } else { if
	 * (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(
	 * typeForRORO)) { tariffSubCatCd =
	 * ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
	 * 
	 * type = typeForRORO;
	 * 
	 * if ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
	 * mvmt = "TS"; } else if ("L".equalsIgnoreCase(cargostatus)) { mvmt = "LL"; }
	 * else { mvmt = "IT"; } } }
	 * 
	 * // MCC set mvmt as Local for Transhipment cargoes if EPC_IND is Y
	 * 
	 * if(deliveryToEPC.equalsIgnoreCase("Y")){ LogManager.instance.
	 * logDebug("Delivery To EPC Area so charge local import rate for both Transhipment cargo with mvmt:"
	 * +mvmt); if(mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_TRANSHIP) ||
	 * mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_ITH)){ mvmt =
	 * ProcessChargeConst.MVMT_LOCAL; type = "00";
	 * 
	 * } }
	 * 
	 * 
	 * // ducta1 end log.info("**** WHARF CHARGES ******"); log.info("|blNbr : " +
	 * blNbr); log.info("|discVvCd : " + discVvCd); log.info("|vvInd : " + vvInd);
	 * log.info("|businessType : " + businessType); log.info("|schemeCd : " +
	 * schemeCd); log.info("|tariffMainCatCd : " + tariffMainCatCd);
	 * log.info("|tariffSubCatCd : " + tariffSubCatCd); log.info("|mvmt : " + mvmt);
	 * log.info("|type : " + type); log.info("|localLeg : " + localLeg);
	 * log.info("|discGateway : " + discGateway); log.info("|billTonEdo : " +
	 * billTonEdo); log.info("|billAcctNbr : " + billAcctNbr); // added by Balaji
	 * log.info("|refInd : " + ProcessChargeConst.REF_IND_BL); // end added by
	 * Balaji log.info("|lastModifyUserId : " + lastModifyUserId);
	 * log.info("|lastModifyDttm : " + lastModifyDttm); //
	 * log.info("|deliveryToEPC : "+deliveryToEPC);
	 * log.info("==== END OF WHARF CHARGES ====");
	 * 
	 * GeneralEventLogValueObject generalEventLogValueObject = new
	 * GeneralEventLogValueObject();
	 * generalEventLogValueObject.setDiscVvCd(discVvCd);
	 * generalEventLogValueObject.setVvInd(vvInd);
	 * generalEventLogValueObject.setBusinessType(businessType);
	 * generalEventLogValueObject.setSchemeCd(schemeCd);
	 * generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
	 * generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
	 * generalEventLogValueObject.setMvmt(mvmt);
	 * generalEventLogValueObject.setType(type);
	 * generalEventLogValueObject.setLocalLeg(localLeg);
	 * generalEventLogValueObject.setDiscGateway(discGateway);
	 * generalEventLogValueObject.setEdoAsnNbr(edoAsnNbr);
	 * generalEventLogValueObject.setBlNbr(blNbr);
	 * generalEventLogValueObject.setBillTonEdo(billTonEdo);
	 * generalEventLogValueObject.setBillAcctNbr(billAcctNbr); // added by Balaji
	 * generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_BL); // end
	 * added by Balaji
	 * generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
	 * generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
	 * generalEventLogValueObject.setCountUnit(countUnit);// ducta1 starts on
	 * 23/12/2008 count1++;
	 * GeneralEventLogArrayList.add(generalEventLogValueObject); } // end-while for
	 * rs1
	 * 
	 * if (count1 > 0) { billWharfInd = "Y";
	 * gbmsCabValueObject.setBillWharfTriggeredInd("Y"); }
	 * 
	 * paramMap.put("strvvcd", strvvcd); log.info(" *** CabImportBj SQL *****" +
	 * sql2.toString()); log.info(" *** CabImportBj paramMap *****" +
	 * paramMap.toString()); rs2 =
	 * namedParameterJdbcTemplate.queryForRowSet(sql2.toString(), paramMap); while
	 * (rs2.next()) { String discVvCd = strvvcd; String vvInd =
	 * ProcessChargeConst.DISC_VV_IND; String businessType = "G"; String schemeCd =
	 * rs2.getString("SCH"); String tariffMainCatCd = "WF"; String tariffSubCatCd =
	 * "OV"; String type = deriveType(rs2.getString("CARGO_CATEGORY_CD"), "00");
	 * String localLeg = "IM"; String discGateway = "J"; String lastModifyUserId =
	 * struserid; Timestamp lastModifyDttm = txnDttm; String mvmt = "00"; String
	 * edoAsnNbr; String blNbr; String billAcctNbr = ""; double billTonEdo;
	 * 
	 * // String vactno = rs2.getString("VACCTNO"); int countUnit =
	 * Integer.parseInt(CommonUtility.deNull(rs2.getString("NBR_PKGS")));// ducta1
	 * starts on // 23/12/2008 // // SL-CAB-20090723-01 // change from //
	 * md.edo_nbr_pkgs // to nbr_pkgs
	 * 
	 * // String deliveryToEPC = CommonUtility.deNull(rs2.getString("EPCIND"));
	 * //MCC // get EPC_IND
	 * 
	 * // <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08> // add new scheme
	 * for LCT, 20.feb.11 by hpeng // if(!schemeCd.equals("JNL") &&
	 * !schemeCd.equals("JBT") && // !schemeCd.equals("JLR")) if
	 * (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") &&
	 * !schemeCd.equals("JLR") && !schemeCd.equals("JWP") &&
	 * !schemeCd.equals(ProcessChargeConst.LCT_SCHEME)) schemeCd = "JLR"; // ducta1
	 * starts on 23/12/2008 String typeForRORO = rs2.getString("CARGO_CATEGORY_CD");
	 * String cargostatus = CommonUtility.deNull(rs2.getString(2)); if
	 * (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(
	 * typeForRORO)) { tariffSubCatCd =
	 * ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL; type = typeForRORO; if
	 * ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
	 * mvmt = "TS"; } else if ("L".equalsIgnoreCase(cargostatus)) { mvmt = "LL"; }
	 * else { mvmt = "IT"; }
	 * 
	 * }
	 * 
	 * // MCC set mvmt as Local for Transhipment cargoes if EPC_IND is Y
	 * 
	 * if(deliveryToEPC.equalsIgnoreCase("Y")){ LogManager.instance.
	 * logDebug("Delivery To EPC Area so charge local import rate for both Transhipment cargo with mvmt:"
	 * +mvmt); if(mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_TRANSHIP) ||
	 * mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_ITH)){ mvmt =
	 * ProcessChargeConst.MVMT_LOCAL; type = "00"; } }
	 * 
	 * 
	 * // ducta1 end // <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/>
	 * edoAsnNbr = CommonUtility.deNull(rs2.getString(1)); blNbr =
	 * CommonUtility.deNull(rs2.getString("BLNBR")); billTonEdo =
	 * Double.parseDouble(CommonUtility.deNull(rs2.getString(3))); billAcctNbr =
	 * rs2.getString("EDOACCTNO"); log.info("**** WHARF CHARGES ******");
	 * log.info("|discVvCd : " + discVvCd); log.info("|vvInd : " + vvInd);
	 * log.info("|businessType : " + businessType); log.info("|schemeCd : " +
	 * schemeCd); log.info("|tariffMainCatCd : " + tariffMainCatCd);
	 * log.info("|tariffSubCatCd : " + tariffSubCatCd); log.info("|mvmt : " + mvmt);
	 * log.info("|type : " + type); log.info("|localLeg : " + localLeg);
	 * log.info("|discGateway : " + discGateway); log.info("|blNbr : " + blNbr);
	 * log.info("|edoAsnNbr : " + edoAsnNbr); log.info("|billTonEdo : " +
	 * billTonEdo); log.info("|billAcctNbr : " + billAcctNbr); // added by Balaji
	 * log.info("|refInd : " + ProcessChargeConst.REF_IND_BL); // end added by
	 * Balaji log.info("|lastModifyUserId : " + lastModifyUserId);
	 * log.info("|lastModifyDttm : " + lastModifyDttm); //
	 * log.info("|deliveryToEPC : "+deliveryToEPC);
	 * log.info("==== END OF WHARF CHARGES ====");
	 * 
	 * GeneralEventLogValueObject generalEventLogValueObject = new
	 * GeneralEventLogValueObject();
	 * generalEventLogValueObject.setDiscVvCd(discVvCd);
	 * generalEventLogValueObject.setVvInd(vvInd);
	 * generalEventLogValueObject.setBusinessType(businessType);
	 * generalEventLogValueObject.setSchemeCd(schemeCd);
	 * generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
	 * generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
	 * generalEventLogValueObject.setMvmt(mvmt);
	 * generalEventLogValueObject.setType(type);
	 * generalEventLogValueObject.setLocalLeg(localLeg);
	 * generalEventLogValueObject.setDiscGateway(discGateway);
	 * generalEventLogValueObject.setBlNbr(blNbr);
	 * generalEventLogValueObject.setEdoAsnNbr(edoAsnNbr);
	 * generalEventLogValueObject.setBillTonEdo(billTonEdo);
	 * generalEventLogValueObject.setBillAcctNbr(billAcctNbr); // added by Balaji
	 * generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_BL); // end
	 * added by Balaji
	 * generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
	 * generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
	 * generalEventLogValueObject.setCountUnit(countUnit);// ducta1 starts on
	 * 23/12/2008 count2++;
	 * GeneralEventLogArrayList.add(generalEventLogValueObject); } // end-while for
	 * rs2 if (count2 > 0) { billWharfInd = "Y";
	 * gbmsCabValueObject.setBillWharfTriggeredInd("Y"); }
	 * 
	 * paramMap.put("strvvcd", strvvcd); log.info(" *** CabImportBj SQL *****" +
	 * sql3.toString()); log.info(" *** CabImportBj paramMap *****" +
	 * paramMap.toString()); rs3 =
	 * namedParameterJdbcTemplate.queryForRowSet(sql3.toString(), paramMap);
	 * List<String> tempBlVect = new ArrayList<String>(); while (rs3.next()) {
	 * String discVvCd = strvvcd; String vvInd = ProcessChargeConst.DISC_VV_IND;
	 * String businessType = "G"; String schemeCd = rs3.getString("SCH"); String
	 * tariffMainCatCd = "SC"; String tariffSubCatCd = "GL"; String type =
	 * deriveType(rs3.getString("CRGCAT"), "00");// ducta1 starts on 23/12/2008
	 * String localLeg = "IM"; String discGateway = "J"; String lastModifyUserId =
	 * struserid; Timestamp lastModifyDttm = txnDttm; String mvmt = "LL"; String
	 * edoAsnNbr = ""; String blNbr; String billAcctNbr = ""; double billTonBl;
	 * 
	 * // String vactno = rs3.getString("VACCTNO"); String crgcat =
	 * rs3.getString("CRGCAT");
	 * 
	 * // edoAsnNbr=CommonUtility.deNull(rs3.getString(1)); blNbr =
	 * CommonUtility.deNull(rs3.getString("BLNBR")); String cargostatus =
	 * CommonUtility.deNull(rs3.getString("CRG_STATUS")); String tempmact =
	 * rs3.getString("MACCTNO"); int countUnit =
	 * Integer.parseInt(CommonUtility.deNull(rs3.getString("NBR_PKGS")));// ducta1
	 * starts on // 23/12/2008 // // SL-CAB-20090723-01 // change from //
	 * md.edo_nbr_pkgs // to nbr_pkgs
	 * 
	 * if (tempmact != null && !tempmact.equals("") && !tempmact.equals("null")) {
	 * billAcctNbr = rs3.getString("MACCTNO"); } else { // <cfg: add new scheme for
	 * Wooden Craft 'JWP', 23.may.08> // if(!schemeCd.equals("JNL") &&
	 * !schemeCd.equals("JBT") && // !schemeCd.equals("JLR")) // add new scheme for
	 * LCT, 20.feb.11 by hpeng if (!schemeCd.equals("JNL") &&
	 * !schemeCd.equals("JBT") && !schemeCd.equals("JLR") && !schemeCd.equals("JWP")
	 * && !schemeCd.equals(ProcessChargeConst.LCT_SCHEME)) billAcctNbr =
	 * getABOpBillAcctNbr(strvvcd); else billAcctNbr = isGcOperations(strvvcd) ?
	 * getCustBillAcctNbr(strvvcd) : rs3.getString("VACCTNO"); }
	 * 
	 * // if(!schemeCd.equals("JNL") && !schemeCd.equals("JBT") && //
	 * !schemeCd.equals("JLR")) // add new scheme for LCT, 20.feb.11 by hpeng if
	 * (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") &&
	 * !schemeCd.equals("JLR") && !schemeCd.equals("JWP") &&
	 * !schemeCd.equals(ProcessChargeConst.LCT_SCHEME)) schemeCd = "JLR"; // <cfg:
	 * add new scheme for Wooden Craft 'JWP', 23.may.08/>
	 * 
	 * if (cargostatus.equalsIgnoreCase("T") || cargostatus.equalsIgnoreCase("R")) {
	 * mvmt = "TS"; } String typeForRORO = rs3.getString("CRGCAT"); // ducta1 starts
	 * on 23/12/2008 if
	 * (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(
	 * typeForRORO)) { tariffSubCatCd =
	 * ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL; type = typeForRORO; if
	 * ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
	 * mvmt = "TS"; } else if ("L".equalsIgnoreCase(cargostatus)) { mvmt = "LL"; }
	 * else { mvmt = "IT"; } }
	 * 
	 * // ducta1 end // <cfg merge 22.apr.09> // Added on 28/08/2008 by Ai Lin - For
	 * Overside SC String disType = rs3.getString("DIS_TYPE");
	 * 
	 * if (disType.equalsIgnoreCase("O")) { tariffSubCatCd = "OV"; mvmt = "00"; } //
	 * End added on 28/08/2008 by Ai Lin - For Overside SC
	 * 
	 * // <cfg merge 22.apr.09/> billTonBl = Double.parseDouble(rs3.getString(2));
	 * 
	 * if (!tempBlVect.contains(blNbr) && !(crgcat.equals("LS") ||
	 * crgcat.equals("WA"))) { log.info("**** Service CHARGES ******");
	 * log.info("|discVvCd : " + discVvCd); log.info("|vvInd : " + vvInd);
	 * log.info("|businessType : " + businessType); log.info("|schemeCd : " +
	 * schemeCd); log.info("|tariffMainCatCd : " + tariffMainCatCd);
	 * log.info("|tariffSubCatCd : " + tariffSubCatCd); log.info("|mvmt : " + mvmt);
	 * log.info("|type : " + type); log.info("|localLeg : " + localLeg);
	 * log.info("|discGateway : " + discGateway); log.info("|blNbr : " + blNbr);
	 * log.info("|edoAsnNbr : " + edoAsnNbr); log.info("|billTonBl : " + billTonBl);
	 * log.info("|billAcctNbr : " + billAcctNbr); // added by Balaji
	 * log.info("|refInd : " + ProcessChargeConst.REF_IND_BL); // end added by
	 * Balaji log.info("|lastModifyUserId : " + lastModifyUserId);
	 * log.info("|lastModifyDttm : " + lastModifyDttm);
	 * log.info("==== END OF Service CHARGES ====");
	 * 
	 * GeneralEventLogValueObject generalEventLogValueObject = new
	 * GeneralEventLogValueObject();
	 * generalEventLogValueObject.setDiscVvCd(discVvCd);
	 * generalEventLogValueObject.setVvInd(vvInd);
	 * generalEventLogValueObject.setBusinessType(businessType);
	 * generalEventLogValueObject.setSchemeCd(schemeCd);
	 * generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
	 * generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
	 * generalEventLogValueObject.setMvmt(mvmt);
	 * generalEventLogValueObject.setType(type);
	 * generalEventLogValueObject.setLocalLeg(localLeg);
	 * generalEventLogValueObject.setDiscGateway(discGateway);
	 * generalEventLogValueObject.setBlNbr(blNbr); //
	 * generalEventLogValueObject.setEdoAsnNbr(edoAsnNbr);
	 * generalEventLogValueObject.setBillTonBl(billTonBl);
	 * generalEventLogValueObject.setBillAcctNbr(billAcctNbr); // added by Balaji
	 * generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_BL); // end
	 * added by Balaji
	 * generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
	 * generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
	 * generalEventLogValueObject.setCountUnit(countUnit);// ducta1 starts on
	 * 23/12/2008 count3++;
	 * GeneralEventLogArrayList.add(generalEventLogValueObject);
	 * tempBlVect.add(blNbr); } // if ! tempBL Vect } // end-while for rs3
	 * 
	 * if (count3 > 0) { billSvcChargeInd = "Y";
	 * gbmsCabValueObject.setBillServiceTriggeredInd("Y"); }
	 * 
	 * // MCC begin for LCT scheme bill to manifest
	 * 
	 * paramMap.put("strvvcd", strvvcd); log.info(" *** CabImportBj SQL *****" +
	 * sql5.toString()); log.info(" *** CabImportBj paramMap *****" +
	 * paramMap.toString()); rs5 =
	 * namedParameterJdbcTemplate.queryForRowSet(sql5.toString(), paramMap);
	 * List<String> tempBlVect1 = new ArrayList<String>(); while (rs5.next()) {
	 * String discVvCd = strvvcd; String vvInd = ProcessChargeConst.DISC_VV_IND;
	 * String businessType = "G"; String schemeCd = rs5.getString("SCH"); String
	 * tariffMainCatCd = "WF"; String tariffSubCatCd = "GL"; String type =
	 * deriveType(rs5.getString("CRGCAT"), "00"); String localLeg = "IM"; String
	 * discGateway = "J"; String lastModifyUserId = struserid; Timestamp
	 * lastModifyDttm = txnDttm; String mvmt = "LL"; String edoAsnNbr = ""; String
	 * blNbr; String billAcctNbr = ""; double billTonBl; String crgcat =
	 * rs5.getString("CRGCAT");
	 * 
	 * blNbr = CommonUtility.deNull(rs5.getString("BLNBR")); String cargostatus =
	 * CommonUtility.deNull(rs5.getString("CRG_STATUS")); String tempmact =
	 * rs5.getString("MACCTNO"); int countUnit =
	 * Integer.parseInt(CommonUtility.deNull(rs5.getString("NBR_PKGS")));
	 * 
	 * if (tempmact != null && !tempmact.equals("") && !tempmact.equals("null")) {
	 * billAcctNbr = rs5.getString("MACCTNO"); } else {
	 * 
	 * if (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") &&
	 * !schemeCd.equals("JLR") && !schemeCd.equals("JWP") &&
	 * !schemeCd.equals(ProcessChargeConst.LCT_SCHEME)) billAcctNbr =
	 * getABOpBillAcctNbr(strvvcd); else billAcctNbr = isGcOperations(strvvcd) ?
	 * getCustBillAcctNbr(strvvcd) : rs3.getString("VACCTNO"); }
	 * 
	 * if (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") &&
	 * !schemeCd.equals("JLR") && !schemeCd.equals("JWP") &&
	 * !schemeCd.equals(ProcessChargeConst.LCT_SCHEME)) schemeCd = "JLR";
	 * 
	 * if (cargostatus.equalsIgnoreCase("T") || cargostatus.equalsIgnoreCase("R")) {
	 * mvmt = "TS"; } String typeForRORO = rs5.getString("CRGCAT"); if
	 * (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(
	 * typeForRORO) ||
	 * ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(
	 * typeForRORO)) { tariffSubCatCd =
	 * ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL; type = typeForRORO; if
	 * ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
	 * mvmt = "TS"; } else if ("L".equalsIgnoreCase(cargostatus)) { mvmt = "LL"; }
	 * else { mvmt = "IT"; } }
	 * 
	 * String disType = rs5.getString("DIS_TYPE");
	 * 
	 * if (disType.equalsIgnoreCase("O")) { tariffSubCatCd = "OV"; mvmt = "00"; }
	 * 
	 * billTonBl = Double.parseDouble(rs5.getString(2));
	 * 
	 * if (!tempBlVect1.contains(blNbr) && !(crgcat.equals("LS") ||
	 * crgcat.equals("WA"))) { log.info("**** Wharfage CHARGES ******");
	 * log.info("|discVvCd : " + discVvCd); log.info("|vvInd : " + vvInd);
	 * log.info("|businessType : " + businessType); log.info("|schemeCd : " +
	 * schemeCd); log.info("|tariffMainCatCd : " + tariffMainCatCd);
	 * log.info("|tariffSubCatCd : " + tariffSubCatCd); log.info("|mvmt : " + mvmt);
	 * log.info("|type : " + type); log.info("|localLeg : " + localLeg);
	 * log.info("|discGateway : " + discGateway); log.info("|blNbr : " + blNbr);
	 * log.info("|edoAsnNbr : " + edoAsnNbr); log.info("|billTonBl : " + billTonBl);
	 * log.info("|billAcctNbr : " + billAcctNbr); log.info("|refInd : " +
	 * ProcessChargeConst.REF_IND_BL); log.info("|lastModifyUserId : " +
	 * lastModifyUserId); log.info("|lastModifyDttm : " + lastModifyDttm);
	 * log.info("==== END OF Service CHARGES ====");
	 * 
	 * GeneralEventLogValueObject generalEventLogValueObject = new
	 * GeneralEventLogValueObject();
	 * generalEventLogValueObject.setDiscVvCd(discVvCd);
	 * generalEventLogValueObject.setVvInd(vvInd);
	 * generalEventLogValueObject.setBusinessType(businessType);
	 * generalEventLogValueObject.setSchemeCd(schemeCd);
	 * generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
	 * generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
	 * generalEventLogValueObject.setMvmt(mvmt);
	 * generalEventLogValueObject.setType(type);
	 * generalEventLogValueObject.setLocalLeg(localLeg);
	 * generalEventLogValueObject.setDiscGateway(discGateway);
	 * generalEventLogValueObject.setBlNbr(blNbr);
	 * generalEventLogValueObject.setBillTonBl(billTonBl);
	 * generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
	 * generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_BL);
	 * generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
	 * generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
	 * generalEventLogValueObject.setCountUnit(countUnit);// ducta1 starts on
	 * 23/12/2008 count5++;
	 * GeneralEventLogArrayList.add(generalEventLogValueObject);
	 * tempBlVect1.add(blNbr); } // if ! tempBL Vect } // end-while for rs5
	 * 
	 * if (count5 > 0) { billWharfInd = "Y";
	 * gbmsCabValueObject.setBillWharfTriggeredInd("Y"); }
	 * 
	 * // MCC End for LCT scheme bill to manifest
	 * 
	 * log.info("count1 --from bean-----> " + count1);
	 * log.info("count2 --from bean-----> " + count2);
	 * log.info("count3 --from bean-----> " + count3);
	 * log.info("count4 --from bean-----> " + count4);
	 * log.info("count5 --from bean-----> " + count5);
	 * 
	 * if (count1 > 0 || count2 > 0 || count3 > 0 || count4 > 0 || count5 > 0) {
	 * VesselTxnEventLogValueObject vesselTxnEventLogValueObject = new
	 * VesselTxnEventLogValueObject();
	 * 
	 * // log.info("txnDttm :"+txnDttm);
	 * vesselTxnEventLogValueObject.setVvCd(strvvcd);
	 * vesselTxnEventLogValueObject.setTxnDttm(txnDttm);
	 * vesselTxnEventLogValueObject.setBillWharfInd(billWharfInd);
	 * vesselTxnEventLogValueObject.setBillSvcChargeInd(billSvcChargeInd); //
	 * vesselTxnEventLogValueObject.setBillStoreInd(billStoreInd);
	 * vesselTxnEventLogValueObject.setBillProcessInd(billProcessInd);
	 * vesselTxnEventLogValueObject.setLastModifyUserId(struserid);
	 * vesselTxnEventLogValueObject.setLastModifyDttm(txnDttm);
	 * log.info("**** VESSEL TXN EVENT LOG VOS ******"); log.info("|strvvcd : " +
	 * strvvcd); log.info("|txnDttm : " + txnDttm); log.info("|billWharfInd : " +
	 * billWharfInd); log.info("|billSvcChargeInd : " + billSvcChargeInd); //
	 * log.info("|billStoreInd : "+billStoreInd); log.info("|billProcessInd : " +
	 * billProcessInd); log.info("|struserid : " + struserid);
	 * log.info("|txnDttm : " + txnDttm);
	 * log.info("==== END OF VESSEL TXN EVENT LOG VOS ====");
	 * 
	 * log.info("**** Process Log created ******");
	 * processGBLogRepo.executeGBCharges(vesselTxnEventLogValueObject,
	 * GeneralEventLogArrayList, ProcessChargeConst.REF_IND_BL);
	 * log.info("**** Process Log Execute GbCharges Method Called******");
	 * gbmsCabValueObject.setStatus("TRUE"); }
	 * 
	 * log.info("END: ** CabImportBj Result ****" + (gbmsCabValueObject == null ?
	 * "null" : gbmsCabValueObject.toString()));
	 * 
	 * } catch (BusinessException e) { log.info("Exception CabImportBj :", e); throw
	 * new BusinessException(e.getMessage()); } catch (NullPointerException e) {
	 * log.info("Exception CabImportBj :", e); throw new BusinessException("M4201");
	 * } catch (Exception e) { log.info("Exception CabImportBj :", e); throw new
	 * BusinessException("M4201"); } finally {
	 * log.info("END: CabImportBj  DAO  END **** gbmsCabValueObject: " +
	 * (gbmsCabValueObject == null ? "null" : gbmsCabValueObject.toString())); }
	 * return gbmsCabValueObject; }
	 */
	// Commented the CabImportBj method for JPOM Migration Issue Ended

	// Modified CabImportBj method for JPOM Migration Issue Added
	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->CabImportBj()
	private GbmsCabValueObject CabImportBj(String strvvcd, String struserid) throws BusinessException {
		List<GeneralEventLogValueObject> GeneralEventLogArrayList = new ArrayList<GeneralEventLogValueObject>();

		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3 = null;
		SqlRowSet rs4 = null;
		SqlRowSet rs5 = null;

		StringBuilder sqlNLTS = new StringBuilder();
		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();
		StringBuilder sql3 = new StringBuilder();
		StringBuilder sql5 = new StringBuilder();

		// added condition 'AND MD.UNSTUFF_SEQ_NBR=0' in the queries to Supress Charges
		// for Containerised Cargo Manifest -- 17th Sept,03 Vani.
		// ducta1 starts on 23/12/2008
		// Modified by Ding Xijia(harbortek) 30-Jan-2011 : START
		sqlNLTS.append(" SELECT MD.CRG_STATUS, (GREATEST(GROSS_WT/1000,GROSS_VOL)/MD.NBR_PKGS) * ");
		sqlNLTS.append(" (MD.NBR_PKGS-NBR_PKGS_IN_PORT) AS BL_BILL_TON, MD.BL_NBR BLNBR, ");
		sqlNLTS.append(" MD.CARGO_CATEGORY_CD,CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN ");
		sqlNLTS.append(" VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH,MD.MIXED_SCHEME_ACCT_NBR MACCTNO, ");
		sqlNLTS.append(" VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND, MD.NBR_PKGS FROM ");
		sqlNLTS.append(" MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS ");
		// SL-CAB-20090723-01 change from md.edo_nbr_pkgs to nbr_pkgs
		sqlNLTS.append(" WHERE MD.VAR_NBR = VC.VV_CD AND VC.SCHEME = VS.SCHEME_CD AND MD.BL_STATUS = 'A' ");
		sqlNLTS.append(" AND MD.CRG_STATUS IN ('T','R') AND ");
		sqlNLTS.append(" MD.BILL_WHARF_TRIGGERED_IND = 'N' AND ");
		sqlNLTS.append(" MD.CRG_TYPE NOT IN ('00','01','02','03') AND ");
		sqlNLTS.append(
				" MD.GB_CLOSE_BJ_IND = 'Y' AND ( VC.SCHEME IN ('JNL','JBT', '" + ProcessChargeConst.LCT_SCHEME + "') ");
		sqlNLTS.append(" OR (UPPER(VC.COMBI_GC_OPS_IND) = 'Y' AND VC.COMBI_GC_SCHEME IN ('JNL'))) ");
		sqlNLTS.append(" AND MD.VAR_NBR =:strvvcd AND MD.UNSTUFF_SEQ_NBR=0 ");

		sql1.append(" SELECT EDO.EDO_ASN_NBR, EDO.CRG_STATUS, GREATEST(EDO.NOM_WT/1000, EDO.NOM_VOL) ");
		sql1.append(" AS EDO_BILL_TON, EDO.NBR_PKGS, MD.BL_NBR	BLNBR, MD.CARGO_CATEGORY_CD,CASE WHEN ");
		sql1.append(" VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS ");
		sql1.append(" SCH,MD.MIXED_SCHEME_ACCT_NBR MACCTNO, VC.BILL_ACCT_NBR VACCTNO, ");
		sql1.append(" VC.MIXED_SCHEME_IND MSIND,EDO.ACCT_NBR EDOACCTNO, MD.EPC_IND EPCIND, ");
		sql1.append(" EDO.DIS_TYPE EDODISTYPE, MD.NBR_PKGS ");
		// SL-CAB-20090723-01 change from md.edo_nbr_pkgs to nbr_pkgs //MCC get EPC_IND
		sql1.append(" FROM  MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS, ");
		sql1.append(" GB_EDO EDO WHERE MD.VAR_NBR = VC.VV_CD AND MD.MFT_SEQ_NBR = ");
		sql1.append(" EDO.MFT_SEQ_NBR AND VC.SCHEME = VS.SCHEME_CD AND MD.BL_STATUS = 'A' AND ");
		sql1.append(" EDO.EDO_STATUS =	'A'	AND EDO.DIS_TYPE IN ('O')  AND EDO.PAYMENT_MODE ");
		sql1.append(" = 'A' AND MD.BILL_WHARF_TRIGGERED_IND = 'N' AND MD.CRG_TYPE NOT ");
		sql1.append(" IN ('00','01','02','03') AND MD.GB_CLOSE_BJ_IND = 'Y' AND ( VC.SCHEME IN ");
		sql1.append(" ('JNL','JBT','" + ProcessChargeConst.LCT_SCHEME + "') OR (UPPER(VC.COMBI_GC_OPS_IND) ");
		sql1.append(" = 'Y' AND VC.COMBI_GC_SCHEME IN ('JNL'))) AND MD.VAR_NBR =:strvvcd AND MD.UNSTUFF_SEQ_NBR=0 ");

		sql2.append(" SELECT EDO.EDO_ASN_NBR, EDO.CRG_STATUS, GREATEST(EDO.NOM_WT/1000, EDO.NOM_VOL) ");
		sql2.append(" AS EDO_BILL_TON, EDO.NBR_PKGS, MD.BL_NBR	BLNBR, MD.CARGO_CATEGORY_CD,CASE WHEN ");
		sql2.append(" VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH, ");
		sql2.append(" MD.MIXED_SCHEME_ACCT_NBR MACCTNO, VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND ");
		sql2.append(" MSIND,EDO.ACCT_NBR EDOACCTNO, MD.EPC_IND EPCIND, MD.NBR_PKGS FROM ");
		sql2.append(" MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS, ");
		// SL-CAB-20090723-01 change from md.edo_nbr_pkgs to nbr_pkgs //MCC get EPC_IND
		sql2.append(" GB_EDO EDO WHERE MD.VAR_NBR = VC.VV_CD AND MD.MFT_SEQ_NBR = EDO.MFT_SEQ_NBR AND ");
		sql2.append(" CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END = ");
		sql2.append(" VS.SCHEME_CD AND MD.BL_STATUS = 'A' AND EDO.EDO_STATUS = 'A' AND EDO.DIS_TYPE ");
		sql2.append(" IN ('O') AND EDO.PAYMENT_MODE = 'A' AND MD.BILL_WHARF_TRIGGERED_IND = 'N' AND ");
		sql2.append(" MD.CRG_TYPE NOT IN ('00','01','02','03') AND ");
		// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08>
		// + " MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME = 'JLR' OR"
		sql2.append(" MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME IN ('JLR','JWP') OR ");
		sql2.append(" (UPPER(VC.COMBI_GC_OPS_IND) = 'Y' AND VC.COMBI_GC_SCHEME IN ('JLR')) OR ");
		// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/>
		sql2.append(" VS.AB_CD IS NOT NULL) AND MD.VAR_NBR =:strvvcd AND MD.UNSTUFF_SEQ_NBR=0 ");

		sql3.append(" SELECT MD.CRG_STATUS, (GREATEST(GROSS_WT/1000,GROSS_VOL)/MD.NBR_PKGS) * ");
		sql3.append(" (MD.NBR_PKGS-NBR_PKGS_IN_PORT) AS BL_BILL_TON, MD.BL_NBR BLNBR, ");
		sql3.append(" MD.CARGO_CATEGORY_CD AS CRGCAT,CASE WHEN VC.COMBI_GC_OPS_IND ='Y' ");
		sql3.append(" THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH,MD.MIXED_SCHEME_ACCT_NBR MACCTNO, ");
		// Amended on 28/08/2008 by Ai Lin - For Overside SC
		// sql3.append(" VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND FROM
		// MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS ");
		// sql3.append(" VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND,
		// MD.DIS_TYPE FROM MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS ");
		// //<cfg merged Ai Lin with Ducta changes 22.Apr.09>
		sql3.append(" VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND, MD.DIS_TYPE, MD.NBR_PKGS FROM ");
		sql3.append(" MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS ");
		// SL-CAB-20090723-01 change from md.edo_nbr_pkgs to nbr_pkgs
		// End amended on 28/08/2008 by Ai Lin - For Overside SC
		sql3.append(" WHERE MD.VAR_NBR = VC.VV_CD AND CASE WHEN VC.COMBI_GC_OPS_IND ='Y' ");
		sql3.append(" THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END = VS.SCHEME_CD AND ");
		sql3.append(" MD.BL_STATUS = 'A' AND MD.BILL_SERVICE_TRIGGERED_IND = 'N' AND ");
		sql3.append(" MD.CRG_TYPE NOT IN ('00','01','02','03') AND ");
		// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08>
		// sql3.append(" MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME = 'JLR' OR ");
		// sql3.append(" MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME IN ('JLR','JWP') OR ");
		// //MCConsulting commented and added JCL below
		// MCConsulting add JCL scheme
		sql3.append(" MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME IN ('JLR','JWP','JCL') OR ");
		sql3.append(" (UPPER(VC.COMBI_GC_OPS_IND) = 'Y' AND VC.COMBI_GC_SCHEME IN ('JLR')) OR ");
		// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.;08/>
		sql3.append(" VS.AB_CD IS NOT NULL) AND MD.VAR_NBR =:strvvcd AND ");
		sql3.append(" MD.UNSTUFF_SEQ_NBR=0 ORDER BY BLNBR ");

		// MCConsulting add new query for WF for LCT to charge to manifest bill
		sql5.append(" SELECT MD.CRG_STATUS, (GREATEST(GROSS_WT/1000,GROSS_VOL)/MD.NBR_PKGS) * ");
		sql5.append(" (MD.NBR_PKGS-NBR_PKGS_IN_PORT) AS BL_BILL_TON, MD.BL_NBR BLNBR, ");
		sql5.append(" MD.CARGO_CATEGORY_CD AS CRGCAT,CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN ");
		sql5.append(" VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH,MD.MIXED_SCHEME_ACCT_NBR MACCTNO, ");
		sql5.append(" VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND, MD.DIS_TYPE, MD.NBR_PKGS FROM ");
		sql5.append(" MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS WHERE MD.VAR_NBR = VC.VV_CD ");
		sql5.append(" AND VC.SCHEME = VS.SCHEME_CD AND MD.BL_STATUS = 'A' AND MD.CRG_STATUS IN ('L') AND ");
		sql5.append(" MD.BILL_WHARF_TRIGGERED_IND = 'N' AND MD.CRG_TYPE NOT IN ('00','01','02','03') AND ");
		sql5.append(" MD.GB_CLOSE_BJ_IND = 'Y' AND CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN ");
		sql5.append(" VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END IN ('JCL') ");
		sql5.append(" AND MD.VAR_NBR =:strvvcd AND MD.UNSTUFF_SEQ_NBR=0 ORDER BY BLNBR ");

		// Modified by Ding Xijia(harbortek) 30-Jan-2011 : END
		/*
		 * String sql3 = "SELECT  " +
		 * " MD.CRG_STATUS, (GREATEST(GROSS_WT/1000,GROSS_VOL)/MD.NBR_PKGS) * (MD.NBR_PKGS-NBR_PKGS_IN_PORT) "
		 * +
		 * " AS BL_BILL_TON, MD.BL_NBR BLNBR, MD.CARGO_CATEGORY_CD AS CRGCAT,CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH,MD.MIXED_SCHEME_ACCT_NBR MACCTNO,"
		 * +
		 * " VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND FROM  MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS, GB_EDO EDO "
		 * + " WHERE MD.VAR_NBR = VC.VV_CD AND " +
		 * " VC.SCHEME = VS.SCHEME_CD AND MD.BL_STATUS = 'A' AND" +
		 * " MD.BILL_SERVICE_TRIGGERED_IND = 'N' AND" +
		 * " MD.CRG_TYPE NOT IN ('00','01','02','03') AND" //<cfg: add new scheme for
		 * Wooden Craft 'JWP', 23.may.08> //+
		 * " MD.GB_CLOSE_BJ_IND = 'Y' AND (VC.SCHEME = 'JLR' OR" +
		 * " MD.GB_CLOSE_BJ_IND = 'Y' AND (CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END IN ('JLR','JWP') OR"
		 * //<cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/> +
		 * " VS.AB_CD IS NOT NULL) AND MD.VAR_NBR = '"
		 * +strvvcd+"' AND MD.UNSTUFF_SEQ_NBR=0 " +
		 * " AND EDO.MFT_SEQ_NBR = MD.MFT_SEQ_NBR AND EDO.DIS_TYPE <> 'O' ORDER BY BLNBR"
		 * ; //<cfg: SC for overside Cargo, 07.aug.08>
		 * 
		 * String sql5 = "SELECT EDO.EDO_ASN_NBR, EDO.CRG_STATUS, " +
		 * " GREATEST(EDO.NOM_WT/1000, EDO.NOM_VOL) " +
		 * " AS EDO_BILL_TON, EDO.NBR_PKGS, MD.BL_NBR BLNBR, MD.CARGO_CATEGORY_CD AS CRGCAT, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS SCH, MD.MIXED_SCHEME_ACCT_NBR MACCTNO,"
		 * +
		 * " VC.BILL_ACCT_NBR VACCTNO, VC.MIXED_SCHEME_IND MSIND, EDO.ACCT_NBR EDOACCTNO FROM  MANIFEST_DETAILS MD, VESSEL_CALL VC, VESSEL_SCHEME VS,"
		 * + " GB_EDO EDO WHERE MD.VAR_NBR = VC.VV_CD AND MD.MFT_SEQ_NBR =" +
		 * " EDO.MFT_SEQ_NBR AND" +
		 * " VC.SCHEME = VS.SCHEME_CD AND MD.BL_STATUS = 'A' AND" +
		 * " EDO.EDO_STATUS = 'A' AND EDO.DIS_TYPE = 'O' AND EDO.PAYMENT_MODE = 'A' " +
		 * " AND MD.BILL_SERVICE_TRIGGERED_IND = 'N' AND" +
		 * " MD.CRG_TYPE NOT IN ('00','01','02','03') AND" +
		 * " MD.GB_CLOSE_BJ_IND = 'Y' AND (CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME  ELSE VC.SCHEME END IN ('JLR','JWP') OR"
		 * + " VS.AB_CD IS NOT NULL) AND MD.VAR_NBR = '"
		 * +strvvcd+"' AND MD.UNSTUFF_SEQ_NBR=0";
		 */
		// <cfg: SC for overside Cargo, 07.aug.08/>
		// ducta1 end
		String billWharfInd = "N";
		String billSvcChargeInd = "N";

		String billProcessInd = "N";
		GbmsCabValueObject gbmsCabValueObject = new GbmsCabValueObject();
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int count4 = 0;
		int count5 = 0; // MCC
		gbmsCabValueObject.setStatus("FALSE");
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: CabImportBj  DAO  Start Obj strvvcd:" + CommonUtility.deNull(strvvcd) + " struserid:"
					+ CommonUtility.deNull(struserid));

			txnDttm = getSystemDate();

			paramMap.put("strvvcd", strvvcd);
			log.info(" *** CabImportBj SQL *****" + sqlNLTS.toString());
			log.info(" *** CabImportBj paramMap *****" + paramMap.toString());
			rs4 = namedParameterJdbcTemplate.queryForRowSet(sqlNLTS.toString(), paramMap);
			List<String> tempBlV = new ArrayList<String>();

			while (rs4.next()) {
				String discVvCd = strvvcd;
				String vvInd = ProcessChargeConst.DISC_VV_IND;
				String businessType = "G";
				String schemeCd = rs4.getString("SCH");
				String tariffMainCatCd = "WF";
				String tariffSubCatCd = "OV";
				String cargoCategoryCd = rs4.getString("CARGO_CATEGORY_CD");
				String type = deriveType(cargoCategoryCd, "00");
				String localLeg = "IM";
				// discgateway to be changed
				String discGateway = "J";
				String lastModifyUserId = struserid;
				Timestamp lastModifyDttm = txnDttm;
				String mvmt = "00";
				String billAcctNbr = "";

				// String vactno = rs4.getString("VACCTNO");
				String blNbr = CommonUtility.deNull(rs4.getString("BLNBR"));
				String cargostatus = CommonUtility.deNull(rs4.getString(1));
				double billTonBl = Double.parseDouble(CommonUtility.deNull(rs4.getString(2)));
				int countUnit = Integer.parseInt(CommonUtility.deNull(rs4.getString("NBR_PKGS")));// ducta1 starts on
				// 23/12/2008 //
				// SL-CAB-20090723-01
				// change from
				// md.edo_nbr_pkgs
				// to nbr_pkgs

				// ducta1 start on 20/12/2008
				if (cargostatus.equalsIgnoreCase("T") || cargostatus.equalsIgnoreCase("R")) {
					// because the cargo status in this case is T or R
					mvmt = "TS";
					tariffSubCatCd = "GL";

					// process for RORO
					if (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(cargoCategoryCd)
							|| ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN
									.equalsIgnoreCase(cargoCategoryCd)
							|| ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(cargoCategoryCd)) {

						// MCC if there is TESN JP-JP for JNL as disc scheme then do not set type as
						// RORO
						/*
						 * boolean isTESNJPJP = isTESNJPJP(blNbr); if(isTESNJPJP &&
						 * schemeCd.equalsIgnoreCase("JNL")){ LogManager.instance.
						 * logInfo("**The Bill has RORO cargoes delivered to TESN JP-JP vessel *** ");
						 * //do not set tariff sub cat as RORO }else {
						 */

						type = cargoCategoryCd;// re-assign value of type for RORO
						// value of sub tariff code is "RO"
						tariffSubCatCd = ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
						// }
					}

					// mvmt = "TS";

					// tariffSubCatCd = "GL";//ducta1 starts on 23/12/2008 (comment)
					// -----------------

					// Changed by Liu Foong(NCS) on 28/3/05: GBMS - Mixed scheme not catered for
					// non-liner
					// billAcctNbr = rs4.getString("VACCTNO");
					String tempmact = rs4.getString("MACCTNO");
					if (tempmact != null && !tempmact.isEmpty() && !"null".equalsIgnoreCase(tempmact)) {
						billAcctNbr = tempmact;
					} else {
						// MCC Commented the following line and included the isGcOperations due to wrong
						// CT Account logged instead of GB Account
						// billAcctNbr= rs4.getString("VACCTNO");
						billAcctNbr = isGcOperations(strvvcd) ? getCustBillAcctNbr(strvvcd) : rs4.getString("VACCTNO");
					}
					// End of Changed by Liu Foong
				}

				if (!tempBlV.contains(blNbr)) {
					log.info("**** WHARF CHARGES ******");
					log.info("|blNbr : " + blNbr);
					log.info("|discVvCd : " + discVvCd);
					log.info("|vvInd : " + vvInd);
					log.info("|businessType : " + businessType);
					log.info("|schemeCd : " + schemeCd);
					log.info("|tariffMainCatCd : " + tariffMainCatCd);
					log.info("|tariffSubCatCd : " + tariffSubCatCd);
					log.info("|mvmt : " + mvmt);
					log.info("|type : " + type);
					log.info("|localLeg : " + localLeg);
					log.info("|discGateway : " + discGateway);
					log.info("|billTonBl : " + billTonBl);
					log.info("|billAcctNbr : " + billAcctNbr);
					// added by Balaji
					log.info("|refInd : " + ProcessChargeConst.REF_IND_BL);
					// end added by Balaji
					log.info("|lastModifyUserId : " + lastModifyUserId);
					log.info("|lastModifyDttm : " + lastModifyDttm);
					log.info("==== END OF WHARF CHARGES ====");

					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
					generalEventLogValueObject.setDiscVvCd(discVvCd);
					generalEventLogValueObject.setVvInd(vvInd);
					generalEventLogValueObject.setBusinessType(businessType);
					generalEventLogValueObject.setSchemeCd(schemeCd);
					generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
					generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
					generalEventLogValueObject.setMvmt(mvmt);
					generalEventLogValueObject.setType(type);
					generalEventLogValueObject.setLocalLeg(localLeg);
					generalEventLogValueObject.setDiscGateway(discGateway);
					generalEventLogValueObject.setBlNbr(blNbr);
					generalEventLogValueObject.setBillTonBl(billTonBl);
					generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
					// added by Balaji
					generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_BL);
					// end added by Balaji
					generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
					generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
					generalEventLogValueObject.setCountUnit(countUnit);// ducta1 starts on 23/12/2008

					count4++;
					GeneralEventLogArrayList.add(generalEventLogValueObject);
					tempBlV.add(blNbr);
				} // if ! tempBL V
			} // end-while for rs4

			if (count4 > 0) {
				billWharfInd = "Y";
				gbmsCabValueObject.setBillWharfTriggeredInd("Y");
			}

			txnDttm = getSystemDate();
			log.info("txnDttm : " + txnDttm);

			paramMap.put("strvvcd", strvvcd);
			log.info(" *** CabImportBj SQL *****" + sql1.toString());
			log.info(" *** CabImportBj paramMap *****" + paramMap.toString());
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
			while (rs1.next()) {
				String discVvCd = strvvcd;
				String vvInd = ProcessChargeConst.DISC_VV_IND;
				String businessType = "G";
				String schemeCd = rs1.getString("SCH");
				String tariffMainCatCd = "WF";
				String tariffSubCatCd = "OV";
				String cargoCategoryCd = rs1.getString("CARGO_CATEGORY_CD");
				String type = deriveType(cargoCategoryCd, "00");
				String localLeg = "IM";
				// discgateway to be changed
				String discGateway = "J";
				String lastModifyUserId = struserid;
				Timestamp lastModifyDttm = txnDttm;
				String mvmt = "00";

				// String vactno = rs1.getString("VACCTNO");

				String blNbr = CommonUtility.deNull(rs1.getString("BLNBR"));
				String edoAsnNbr = CommonUtility.deNull(rs1.getString(1));
				String cargostatus = CommonUtility.deNull(rs1.getString(2));
				double billTonEdo = Double.parseDouble(CommonUtility.deNull(rs1.getString(3)));
				String billAcctNbr = rs1.getString("EDOACCTNO");

				// String deliveryToEPC = CommonUtility.deNull(rs1.getString("EPCIND")); //MCC
				// get EPC_IND

				int countUnit = Integer.parseInt(CommonUtility.deNull(rs1.getString("NBR_PKGS")));// ducta1 starts on
				// 23/12/2008 //
				// SL-CAB-20090723-01
				// change from
				// md.edo_nbr_pkgs
				// to nbr_pkgs

				/*
				 * if (cargostatus.equalsIgnoreCase("T") || cargostatus.equalsIgnoreCase("R")) {
				 * mvmt="TS"; tariffSubCatCd = "GL"; billAcctNbr = rs1.getString("VACCTNO"); }
				 */

				boolean isRORO = ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(cargoCategoryCd)
						|| ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(cargoCategoryCd)
						|| ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(cargoCategoryCd);

				String edoDistType = rs1.getString("EDODISTYPE");

				if ("O".equalsIgnoreCase(edoDistType)
						&& ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus))) {
					tariffSubCatCd = "OV";
					mvmt = "00";
					// ducta1 starts on 23/12/2008

					if (isRORO) {
						tariffSubCatCd = ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
						type = cargoCategoryCd;
						mvmt = "TS";
					}
				} else {
					if (isRORO) {
						tariffSubCatCd = ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
						type = cargoCategoryCd;

						if ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
							mvmt = "TS";
						} else if ("L".equalsIgnoreCase(cargostatus)) {
							mvmt = "LL";
						} else {
							mvmt = "IT";
						}
					}
				}

				// MCC set mvmt as Local for Transhipment cargoes if EPC_IND is Y
				/*
				 * if(deliveryToEPC.equalsIgnoreCase("Y")){ LogManager.instance.
				 * logDebug("Delivery To EPC Area so charge local import rate for both Transhipment cargo with mvmt:"
				 * +mvmt); if(mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_TRANSHIP) ||
				 * mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_ITH)){ mvmt =
				 * ProcessChargeConst.MVMT_LOCAL; type = "00";
				 * 
				 * } }
				 */

				// ducta1 end
				log.info("**** WHARF CHARGES ******");
				log.info("|blNbr : " + blNbr);
				log.info("|discVvCd : " + discVvCd);
				log.info("|vvInd : " + vvInd);
				log.info("|businessType : " + businessType);
				log.info("|schemeCd : " + schemeCd);
				log.info("|tariffMainCatCd : " + tariffMainCatCd);
				log.info("|tariffSubCatCd : " + tariffSubCatCd);
				log.info("|mvmt : " + mvmt);
				log.info("|type : " + type);
				log.info("|localLeg : " + localLeg);
				log.info("|discGateway : " + discGateway);
				log.info("|billTonEdo : " + billTonEdo);
				log.info("|billAcctNbr : " + billAcctNbr);
				// added by Balaji
				log.info("|refInd : " + ProcessChargeConst.REF_IND_BL);
				// end added by Balaji
				log.info("|lastModifyUserId : " + lastModifyUserId);
				log.info("|lastModifyDttm : " + lastModifyDttm);
				// log.info("|deliveryToEPC : "+deliveryToEPC);
				log.info("==== END OF WHARF CHARGES ====");

				GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
				generalEventLogValueObject.setDiscVvCd(discVvCd);
				generalEventLogValueObject.setVvInd(vvInd);
				generalEventLogValueObject.setBusinessType(businessType);
				generalEventLogValueObject.setSchemeCd(schemeCd);
				generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
				generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
				generalEventLogValueObject.setMvmt(mvmt);
				generalEventLogValueObject.setType(type);
				generalEventLogValueObject.setLocalLeg(localLeg);
				generalEventLogValueObject.setDiscGateway(discGateway);
				generalEventLogValueObject.setEdoAsnNbr(edoAsnNbr);
				generalEventLogValueObject.setBlNbr(blNbr);
				generalEventLogValueObject.setBillTonEdo(billTonEdo);
				generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
				// added by Balaji
				generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_BL);
				// end added by Balaji
				generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
				generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
				generalEventLogValueObject.setCountUnit(countUnit);// ducta1 starts on 23/12/2008

				count1++;
				GeneralEventLogArrayList.add(generalEventLogValueObject);
			} // end-while for rs1

			if (count1 > 0) {
				billWharfInd = "Y";
				gbmsCabValueObject.setBillWharfTriggeredInd("Y");
			}

			paramMap.put("strvvcd", strvvcd);
			log.info(" *** CabImportBj SQL *****" + sql2.toString());
			log.info(" *** CabImportBj paramMap *****" + paramMap.toString());
			rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2.toString(), paramMap);
			while (rs2.next()) {
				String discVvCd = strvvcd;
				String vvInd = ProcessChargeConst.DISC_VV_IND;
				String businessType = "G";
				String schemeCd = rs2.getString("SCH");
				String tariffMainCatCd = "WF";
				String tariffSubCatCd = "OV";
				String cargoCategoryCd = rs2.getString("CARGO_CATEGORY_CD");
				String type = deriveType(cargoCategoryCd, "00");
				String localLeg = "IM";
				String discGateway = "J";
				String lastModifyUserId = struserid;
				Timestamp lastModifyDttm = txnDttm;
				String mvmt = "00";

				// String vactno = rs2.getString("VACCTNO");
				String cargostatus = CommonUtility.deNull(rs2.getString(2));
				String edoAsnNbr = CommonUtility.deNull(rs2.getString(1));
				String blNbr = CommonUtility.deNull(rs2.getString("BLNBR"));
				double billTonEdo = Double.parseDouble(CommonUtility.deNull(rs2.getString(3)));
				String billAcctNbr = rs2.getString("EDOACCTNO");
				int countUnit = Integer.parseInt(CommonUtility.deNull(rs2.getString("NBR_PKGS")));// ducta1 starts on
				// 23/12/2008 //
				// SL-CAB-20090723-01
				// change from
				// md.edo_nbr_pkgs
				// to nbr_pkgs

				// String deliveryToEPC = CommonUtility.deNull(rs2.getString("EPCIND")); //MCC
				// get EPC_IND

				// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08>
				// add new scheme for LCT, 20.feb.11 by hpeng
				// if(!schemeCd.equals("JNL") && !schemeCd.equals("JBT") &&
				// !schemeCd.equals("JLR"))
				if (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") && !schemeCd.equals("JLR")
						&& !schemeCd.equals("JWP") && !schemeCd.equals(ProcessChargeConst.LCT_SCHEME)) {
					schemeCd = "JLR";
				}
				// ducta1 starts on 23/12/2008

				boolean isRORO = ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(cargoCategoryCd)
						|| ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(cargoCategoryCd)
						|| ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(cargoCategoryCd);

				if (isRORO) {
					tariffSubCatCd = ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
					type = cargoCategoryCd;
					if ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
						mvmt = "TS";
					} else if ("L".equalsIgnoreCase(cargostatus)) {
						mvmt = "LL";
					} else {
						mvmt = "IT";
					}
				}

				// MCC set mvmt as Local for Transhipment cargoes if EPC_IND is Y
				/*
				 * if(deliveryToEPC.equalsIgnoreCase("Y")){ LogManager.instance.
				 * logDebug("Delivery To EPC Area so charge local import rate for both Transhipment cargo with mvmt:"
				 * +mvmt); if(mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_TRANSHIP) ||
				 * mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_ITH)){ mvmt =
				 * ProcessChargeConst.MVMT_LOCAL; type = "00"; } }
				 */

				// ducta1 end
				// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/>

				log.info("**** WHARF CHARGES ******");
				log.info("|discVvCd : " + discVvCd);
				log.info("|vvInd : " + vvInd);
				log.info("|businessType : " + businessType);
				log.info("|schemeCd : " + schemeCd);
				log.info("|tariffMainCatCd : " + tariffMainCatCd);
				log.info("|tariffSubCatCd : " + tariffSubCatCd);
				log.info("|mvmt : " + mvmt);
				log.info("|type : " + type);
				log.info("|localLeg : " + localLeg);
				log.info("|discGateway : " + discGateway);
				log.info("|blNbr : " + blNbr);
				log.info("|edoAsnNbr : " + edoAsnNbr);
				log.info("|billTonEdo : " + billTonEdo);
				log.info("|billAcctNbr : " + billAcctNbr);
				// added by Balaji
				log.info("|refInd : " + ProcessChargeConst.REF_IND_BL);
				// end added by Balaji
				log.info("|lastModifyUserId : " + lastModifyUserId);
				log.info("|lastModifyDttm : " + lastModifyDttm);
				// log.info("|deliveryToEPC : "+deliveryToEPC);
				log.info("==== END OF WHARF CHARGES ====");

				GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
				generalEventLogValueObject.setDiscVvCd(discVvCd);
				generalEventLogValueObject.setVvInd(vvInd);
				generalEventLogValueObject.setBusinessType(businessType);
				generalEventLogValueObject.setSchemeCd(schemeCd);
				generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
				generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
				generalEventLogValueObject.setMvmt(mvmt);
				generalEventLogValueObject.setType(type);
				generalEventLogValueObject.setLocalLeg(localLeg);
				generalEventLogValueObject.setDiscGateway(discGateway);
				generalEventLogValueObject.setBlNbr(blNbr);
				generalEventLogValueObject.setEdoAsnNbr(edoAsnNbr);
				generalEventLogValueObject.setBillTonEdo(billTonEdo);
				generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
				// added by Balaji
				generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_BL);
				// end added by Balaji
				generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
				generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
				generalEventLogValueObject.setCountUnit(countUnit);// ducta1 starts on 23/12/2008

				count2++;
				GeneralEventLogArrayList.add(generalEventLogValueObject);
			} // end-while for rs2
			if (count2 > 0) {
				billWharfInd = "Y";
				gbmsCabValueObject.setBillWharfTriggeredInd("Y");
			}

			paramMap.put("strvvcd", strvvcd);
			log.info(" *** CabImportBj SQL *****" + sql3.toString());
			log.info(" *** CabImportBj paramMap *****" + paramMap.toString());
			rs3 = namedParameterJdbcTemplate.queryForRowSet(sql3.toString(), paramMap);
			List<String> tempBlVect = new ArrayList<String>();
			while (rs3.next()) {
				String discVvCd = strvvcd;
				String vvInd = ProcessChargeConst.DISC_VV_IND;
				String businessType = "G";
				String schemeCd = rs3.getString("SCH");
				String tariffMainCatCd = "SC";
				String tariffSubCatCd = "GL";
				String crgcat = rs3.getString("CRGCAT");
				String type = deriveType(crgcat, "00");// ducta1 starts on 23/12/2008
				String localLeg = "IM";
				String discGateway = "J";
				String lastModifyUserId = struserid;
				Timestamp lastModifyDttm = txnDttm;
				String mvmt = "LL";
				String edoAsnNbr = "";
				String billAcctNbr = "";

				// String vactno = rs3.getString("VACCTNO");

				// edoAsnNbr=CommonUtility.deNull(rs3.getString(1));
				String blNbr = CommonUtility.deNull(rs3.getString("BLNBR"));
				String cargostatus = CommonUtility.deNull(rs3.getString("CRG_STATUS"));
				String tempmact = rs3.getString("MACCTNO");
				int countUnit = Integer.parseInt(CommonUtility.deNull(rs3.getString("NBR_PKGS")));// ducta1 starts on
				// 23/12/2008 //
				// SL-CAB-20090723-01
				// change from
				// md.edo_nbr_pkgs
				// to nbr_pkgs

				if (tempmact != null && !tempmact.equals("") && !tempmact.equals("null")) {
					billAcctNbr = tempmact;
				} else {
					// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08>
					// if(!schemeCd.equals("JNL") && !schemeCd.equals("JBT") &&
					// !schemeCd.equals("JLR"))
					// add new scheme for LCT, 20.feb.11 by hpeng
					if (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") && !schemeCd.equals("JLR")
							&& !schemeCd.equals("JWP") && !schemeCd.equals(ProcessChargeConst.LCT_SCHEME))
						billAcctNbr = getABOpBillAcctNbr(strvvcd);
					else
						billAcctNbr = isGcOperations(strvvcd) ? getCustBillAcctNbr(strvvcd) : rs3.getString("VACCTNO");
				}

				// if(!schemeCd.equals("JNL") && !schemeCd.equals("JBT") &&
				// !schemeCd.equals("JLR"))
				// add new scheme for LCT, 20.feb.11 by hpeng
				if (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") && !schemeCd.equals("JLR")
						&& !schemeCd.equals("JWP") && !schemeCd.equals(ProcessChargeConst.LCT_SCHEME))
					schemeCd = "JLR";
				// <cfg: add new scheme for Wooden Craft 'JWP', 23.may.08/>

				if ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
					mvmt = "TS";
				}

				// ducta1 starts on 23/12/2008
				if (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(crgcat)
						|| ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(crgcat)
						|| ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(crgcat)) {
					tariffSubCatCd = ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
					type = crgcat;
					if ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
						mvmt = "TS";
					} else if ("L".equalsIgnoreCase(cargostatus)) {
						mvmt = "LL";
					} else {
						mvmt = "IT";
					}
				}

				// ducta1 end
				// <cfg merge 22.apr.09>
				// Added on 28/08/2008 by Ai Lin - For Overside SC
				String disType = rs3.getString("DIS_TYPE");

				if ("O".equalsIgnoreCase(disType)) {
					tariffSubCatCd = "OV";
					mvmt = "00";
				}
				// End added on 28/08/2008 by Ai Lin - For Overside SC

				// <cfg merge 22.apr.09/>
				double billTonBl = Double.parseDouble(rs3.getString(2));

				if (!tempBlVect.contains(blNbr) && !(crgcat.equals("LS") || crgcat.equals("WA"))) {
					log.info("**** Service CHARGES ******");
					log.info("|discVvCd : " + discVvCd);
					log.info("|vvInd : " + vvInd);
					log.info("|businessType : " + businessType);
					log.info("|schemeCd : " + schemeCd);
					log.info("|tariffMainCatCd : " + tariffMainCatCd);
					log.info("|tariffSubCatCd : " + tariffSubCatCd);
					log.info("|mvmt : " + mvmt);
					log.info("|type : " + type);
					log.info("|localLeg : " + localLeg);
					log.info("|discGateway : " + discGateway);
					log.info("|blNbr : " + blNbr);
					log.info("|edoAsnNbr : " + edoAsnNbr);
					log.info("|billTonBl : " + billTonBl);
					log.info("|billAcctNbr : " + billAcctNbr);
					// added by Balaji
					log.info("|refInd : " + ProcessChargeConst.REF_IND_BL);
					// end added by Balaji
					log.info("|lastModifyUserId : " + lastModifyUserId);
					log.info("|lastModifyDttm : " + lastModifyDttm);
					log.info("==== END OF Service CHARGES ====");

					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
					generalEventLogValueObject.setDiscVvCd(discVvCd);
					generalEventLogValueObject.setVvInd(vvInd);
					generalEventLogValueObject.setBusinessType(businessType);
					generalEventLogValueObject.setSchemeCd(schemeCd);
					generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
					generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
					generalEventLogValueObject.setMvmt(mvmt);
					generalEventLogValueObject.setType(type);
					generalEventLogValueObject.setLocalLeg(localLeg);
					generalEventLogValueObject.setDiscGateway(discGateway);
					generalEventLogValueObject.setBlNbr(blNbr);
					// generalEventLogValueObject.setEdoAsnNbr(edoAsnNbr);
					generalEventLogValueObject.setBillTonBl(billTonBl);
					generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
					// added by Balaji
					generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_BL);
					// end added by Balaji
					generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
					generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
					generalEventLogValueObject.setCountUnit(countUnit);// ducta1 starts on 23/12/2008

					count3++;
					GeneralEventLogArrayList.add(generalEventLogValueObject);
					tempBlVect.add(blNbr);
				} // if ! tempBL Vect
			} // end-while for rs3

			if (count3 > 0) {
				billSvcChargeInd = "Y";
				gbmsCabValueObject.setBillServiceTriggeredInd("Y");
			}

			// MCC begin for LCT scheme bill to manifest

			paramMap.put("strvvcd", strvvcd);
			log.info(" *** CabImportBj SQL *****" + sql5.toString());
			log.info(" *** CabImportBj paramMap *****" + paramMap.toString());
			rs5 = namedParameterJdbcTemplate.queryForRowSet(sql5.toString(), paramMap);
			List<String> tempBlVect1 = new ArrayList<String>();
			while (rs5.next()) {
				String discVvCd = strvvcd;
				String vvInd = ProcessChargeConst.DISC_VV_IND;
				String businessType = "G";
				String schemeCd = rs5.getString("SCH");
				String tariffMainCatCd = "WF";
				String tariffSubCatCd = "GL";
				String crgcat = rs5.getString("CRGCAT");
				String type = deriveType(crgcat, "00");
				String localLeg = "IM";
				String discGateway = "J";
				String lastModifyUserId = struserid;
				Timestamp lastModifyDttm = txnDttm;
				String mvmt = "LL";
				String edoAsnNbr = "";
				String billAcctNbr = "";

				String blNbr = CommonUtility.deNull(rs5.getString("BLNBR"));
				String cargostatus = CommonUtility.deNull(rs5.getString("CRG_STATUS"));
				String tempmact = rs5.getString("MACCTNO");
				double billTonBl = Double.parseDouble(CommonUtility.deNull(rs5.getString(2)));
				int countUnit = Integer.parseInt(CommonUtility.deNull(rs5.getString("NBR_PKGS")));

				if (tempmact != null && !tempmact.equals("") && !tempmact.equals("null")) {
					billAcctNbr = tempmact;
				} else {
					if (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") && !schemeCd.equals("JLR")
							&& !schemeCd.equals("JWP") && !schemeCd.equals(ProcessChargeConst.LCT_SCHEME))
						billAcctNbr = getABOpBillAcctNbr(strvvcd);
					else
						billAcctNbr = isGcOperations(strvvcd) ? getCustBillAcctNbr(strvvcd) : rs5.getString("VACCTNO");
				}

				if (!schemeCd.equals("JNL") && !schemeCd.equals("JBT") && !schemeCd.equals("JLR")
						&& !schemeCd.equals("JWP") && !schemeCd.equals(ProcessChargeConst.LCT_SCHEME)) {
					schemeCd = "JLR";
				}

				if ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
					mvmt = "TS";
				}

				boolean isRORO = ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(crgcat)
						|| ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(crgcat)
						|| ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(crgcat);

				if (isRORO) {
					tariffSubCatCd = ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
					type = crgcat;
					if ("T".equalsIgnoreCase(cargostatus) || "R".equalsIgnoreCase(cargostatus)) {
						mvmt = "TS";
					} else if ("L".equalsIgnoreCase(cargostatus)) {
						mvmt = "LL";
					} else {
						mvmt = "IT";
					}
				}

				String disType = rs5.getString("DIS_TYPE");

				if ("O".equalsIgnoreCase(disType)) {
					tariffSubCatCd = "OV";
					mvmt = "00";
				}

				if (!tempBlVect1.contains(blNbr) && !(crgcat.equals("LS") || crgcat.equals("WA"))) {
					log.info("**** Wharfage CHARGES ******");
					log.info("|discVvCd : " + discVvCd);
					log.info("|vvInd : " + vvInd);
					log.info("|businessType : " + businessType);
					log.info("|schemeCd : " + schemeCd);
					log.info("|tariffMainCatCd : " + tariffMainCatCd);
					log.info("|tariffSubCatCd : " + tariffSubCatCd);
					log.info("|mvmt : " + mvmt);
					log.info("|type : " + type);
					log.info("|localLeg : " + localLeg);
					log.info("|discGateway : " + discGateway);
					log.info("|blNbr : " + blNbr);
					log.info("|edoAsnNbr : " + edoAsnNbr);
					log.info("|billTonBl : " + billTonBl);
					log.info("|billAcctNbr : " + billAcctNbr);
					log.info("|refInd : " + ProcessChargeConst.REF_IND_BL);
					log.info("|lastModifyUserId : " + lastModifyUserId);
					log.info("|lastModifyDttm : " + lastModifyDttm);
					log.info("==== END OF Service CHARGES ====");

					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
					generalEventLogValueObject.setDiscVvCd(discVvCd);
					generalEventLogValueObject.setVvInd(vvInd);
					generalEventLogValueObject.setBusinessType(businessType);
					generalEventLogValueObject.setSchemeCd(schemeCd);
					generalEventLogValueObject.setTariffMainCatCd(tariffMainCatCd);
					generalEventLogValueObject.setTariffSubCatCd(tariffSubCatCd);
					generalEventLogValueObject.setMvmt(mvmt);
					generalEventLogValueObject.setType(type);
					generalEventLogValueObject.setLocalLeg(localLeg);
					generalEventLogValueObject.setDiscGateway(discGateway);
					generalEventLogValueObject.setBlNbr(blNbr);
					generalEventLogValueObject.setBillTonBl(billTonBl);
					generalEventLogValueObject.setBillAcctNbr(billAcctNbr);
					generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_BL);
					generalEventLogValueObject.setLastModifyUserId(lastModifyUserId);
					generalEventLogValueObject.setLastModifyDttm(lastModifyDttm);
					generalEventLogValueObject.setCountUnit(countUnit);// ducta1 starts on 23/12/2008

					count5++;
					GeneralEventLogArrayList.add(generalEventLogValueObject);
					tempBlVect1.add(blNbr);
				} // if ! tempBL Vect
			} // end-while for rs5

			if (count5 > 0) {
				billWharfInd = "Y";
				gbmsCabValueObject.setBillWharfTriggeredInd("Y");
			}

			// MCC End for LCT scheme bill to manifest

			log.info("count1 --from bean-----> " + count1);
			log.info("count2 --from bean-----> " + count2);
			log.info("count3 --from bean-----> " + count3);
			log.info("count4 --from bean-----> " + count4);
			log.info("count5 --from bean-----> " + count5);

			if (count1 > 0 || count2 > 0 || count3 > 0 || count4 > 0 || count5 > 0) {
				VesselTxnEventLogValueObject vesselTxnEventLogValueObject = new VesselTxnEventLogValueObject();

				// log.info("txnDttm :"+txnDttm);
				vesselTxnEventLogValueObject.setVvCd(strvvcd);
				vesselTxnEventLogValueObject.setTxnDttm(txnDttm);
				vesselTxnEventLogValueObject.setBillWharfInd(billWharfInd);
				vesselTxnEventLogValueObject.setBillSvcChargeInd(billSvcChargeInd);
				// vesselTxnEventLogValueObject.setBillStoreInd(billStoreInd);
				vesselTxnEventLogValueObject.setBillProcessInd(billProcessInd);
				vesselTxnEventLogValueObject.setLastModifyUserId(struserid);
				vesselTxnEventLogValueObject.setLastModifyDttm(txnDttm);
				log.info("**** VESSEL TXN EVENT LOG VOS ******");
				log.info("|strvvcd : " + strvvcd);
				log.info("|txnDttm : " + txnDttm);
				log.info("|billWharfInd : " + billWharfInd);
				log.info("|billSvcChargeInd : " + billSvcChargeInd);
				// log.info("|billStoreInd : "+billStoreInd);
				log.info("|billProcessInd : " + billProcessInd);
				log.info("|struserid : " + struserid);
				log.info("|txnDttm : " + txnDttm);
				log.info("==== END OF VESSEL TXN EVENT LOG VOS ====");

				log.info("**** Process Log created ******");
				processGBLogRepo.executeGBCharges(vesselTxnEventLogValueObject, GeneralEventLogArrayList,
						ProcessChargeConst.REF_IND_BL);
				log.info("**** Process Log Execute GbCharges Method Called******");
				gbmsCabValueObject.setStatus("TRUE");
			}

			log.info("END: ** CabImportBj Result ****"
					+ (gbmsCabValueObject == null ? "null" : gbmsCabValueObject.toString()));

		} catch (BusinessException e) {
			log.info("Exception CabImportBj :", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception CabImportBj :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception CabImportBj :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: CabImportBj  DAO  END **** gbmsCabValueObject: "
					+ (gbmsCabValueObject == null ? "null" : gbmsCabValueObject.toString()));
		}
		return gbmsCabValueObject;
	}
	// Modified CabImportBj method for JPOM Migration Issue Ended
	
	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->isGcOperations()
	private boolean isGcOperations(String strvvcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		boolean gcOps = false;
		String gcOperations = "";

		try {
			log.info("START: isGcOperations  DAO  Start Obj strvvcd:" + CommonUtility.deNull(strvvcd));

			sb.append(" SELECT VESCALL.COMBI_GC_OPS_IND FROM VESSEL_CALL VESCALL ");
			sb.append(" WHERE VESCALL.VV_CD=:strvvcd ");

			log.info(" *** isGcOperations SQL *****" + sb.toString());

			paramMap.put("strvvcd", strvvcd);

			log.info(" *** isGcOperations paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));
				if (gcOperations.equalsIgnoreCase("Y")) {
					gcOps = true;
				}
			}

			log.info("END: ** isGcOperations Result ****" + gcOps);

		} catch (NullPointerException e) {
			log.info("Exception isGcOperations :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isGcOperations :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isGcOperations  DAO  END ***** gcOps: " + gcOps);
		}

		return gcOps;
	}

	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->getCustBillAcctNbr()
	private String getCustBillAcctNbr(String strvvcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		String billAcctNbr = "";

		try {
			log.info("START: getCustBillAcctNbr  DAO  Start Obj strvvcd:" + CommonUtility.deNull(strvvcd));

			sb.append(" SELECT ACCT_NBR FROM CUST_ACCT WHERE CUST_CD = (SELECT VC.CREATE_CUST_CD ");
			sb.append(" FROM VESSEL_CALL VC WHERE VC.VV_CD=:strvvcd ) AND BUSINESS_TYPE='G' ");

			log.info(" *** getCustBillAcctNbr SQL *****" + sb.toString());

			paramMap.put("strvvcd", strvvcd);

			log.info(" *** getCustBillAcctNbr paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				billAcctNbr = CommonUtility.deNull(rs.getString("ACCT_NBR"));
			}

			log.info("END: ** getCustBillAcctNbr Result ****" + CommonUtility.deNull(billAcctNbr));

		} catch (NullPointerException e) {
			log.info("Exception getCustBillAcctNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCustBillAcctNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCustBillAcctNbr  DAO  END **** billAcctNbr: " + CommonUtility.deNull(billAcctNbr));
		}

		return billAcctNbr;
	}

	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->getABOpBillAcctNbr()
	private String getABOpBillAcctNbr(String strvvcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		String billAcctNbr = "";

		try {
			log.info("START: getABOpBillAcctNbr  DAO  Start Obj strvvcd:" + CommonUtility.deNull(strvvcd));

			sb.append(" SELECT VS.ACCT_NBR FROM VESSEL_SCHEME VS, VESSEL_CALL ");
			sb.append(" VESCALL WHERE VS.SCHEME_CD= CASE WHEN VESCALL.COMBI_GC_OPS_IND ='Y' ");
			sb.append(" THEN VESCALL.COMBI_GC_SCHEME ELSE VESCALL.SCHEME END AND VESCALL.VV_CD=:strvvcd ");

			log.info(" *** getABOpBillAcctNbr SQL *****" + sb.toString());

			paramMap.put("strvvcd", strvvcd);

			log.info(" *** getABOpBillAcctNbr paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				billAcctNbr = CommonUtility.deNull(rs.getString("ACCT_NBR"));
			}

			log.info("END: ** getABOpBillAcctNbr Result ****" + CommonUtility.deNull(billAcctNbr));

		} catch (NullPointerException e) {
			log.info("Exception getABOpBillAcctNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getABOpBillAcctNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getABOpBillAcctNbr  DAO  END billAcctNbr: " + CommonUtility.deNull(billAcctNbr));
		}

		return billAcctNbr;
	}

	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->TriggerLct()
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void TriggerLct(CloseLctValueObject vo, String userId) throws BusinessException {
		String vvCd = vo.getVv_cd();
		String uId = userId;
		try {
			log.info("START: TriggerLct  DAO  Start vo:" + vo.toString() + " userId:" + userId);

			if (vo.getDnNbrs() != null && !vo.getDnNbrs().isEmpty()) {
				for (Iterator<String> it = vo.getDnNbrs().iterator(); it.hasNext();) {
					String dnNbr = (String) it.next();
					log.info("INSIDE Trigger DN CAB  METHOD");
					CabDN(dnNbr, uId);
					log.info("CALLED CAB DN METHOD");
				}
			}

			if (vo.getUaNbrs() != null && !vo.getUaNbrs().isEmpty()) {
				for (Iterator<String> it = vo.getUaNbrs().iterator(); it.hasNext();) {
					String uaNbr = (String) it.next();

					List<List<GeneralEventLogValueObject>> generalEventLogLists = new ArrayList<List<GeneralEventLogValueObject>>();

					List<GeneralEventLogValueObject> cabList = cabUaExportStoreRentCharge.processDetails(uaNbr, uId);
					List<GeneralEventLogValueObject> cabListExport = cabUaExportWharfCharge.processDetails(uaNbr, uId);

					generalEventLogLists.add(0, cabList);
					generalEventLogLists.add(1, cabListExport);

					Object[] ret = cabLctScAndWf(vvCd, uId, uaNbr, generalEventLogLists);

					log.info("**** Process Log created for UA******");
					if (ret[0] != null) {
						List<GeneralEventLogValueObject> generalEventLogValueObject = new ArrayList<GeneralEventLogValueObject>();
						Object listObj = ret[1];
						if (listObj instanceof List) {
							for (int j = 0; j < ((List<?>) listObj).size(); j++) {
								Object item = ((List<?>) listObj).get(j);
								if (item instanceof Object) {
									generalEventLogValueObject.add((GeneralEventLogValueObject) item);
								}
							}
						}
						processGBLogRepo.executeBillCharges((VesselTxnEventLogValueObject) ret[0],
								generalEventLogValueObject, ProcessChargeConst.REF_IND_UA);
					}
					log.info("**** After Cab Process Log ******");
				}
			}

			if ("Y".equals(vo.getClose_bj())) {
				log.info("INSIDE Trigger Bj CAB  METHOD");
				CabImportBj(vvCd, uId);
				log.info("CALLED CAB IMPORT BJ METHOD");
			}

			if ("Y".equals(vo.getClose_shipment())) {
				List<List<GeneralEventLogValueObject>> generalEventLogLists = new ArrayList<List<GeneralEventLogValueObject>>();

				List<GeneralEventLogValueObject> cabList = cabUaExportStoreRentCharge.processDetails(vvCd, uId);
				List<GeneralEventLogValueObject> cabListExport = cabUaExportWharfCharge.processDetails(vvCd, uId);

				generalEventLogLists.add(0, cabList);
				generalEventLogLists.add(1, cabListExport);

				Object[] ret = cabLctScAndWf(vvCd, uId, vvCd, generalEventLogLists);

				log.info("**** Process Log created for CS******");
				if (ret[0] != null) {
					List<GeneralEventLogValueObject> generalEventLogValueObject = new ArrayList<GeneralEventLogValueObject>();
					Object listObj = ret[1];
					if (listObj instanceof List) {
						for (int j = 0; j < ((List<?>) listObj).size(); j++) {
							Object item = ((List<?>) listObj).get(j);
							if (item instanceof Object) {
								generalEventLogValueObject.add((GeneralEventLogValueObject) item);
							}
						}
					}
					processGBLogRepo.executeGBCharges((VesselTxnEventLogValueObject) ret[0], generalEventLogValueObject,
							ProcessChargeConst.REF_IND_ESN);
				}
			}
		} catch (BusinessException e) {
			log.error("Exception: TriggerLct ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception: TriggerLct ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: TriggerLct  DAO");
		}
	}

	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->cabLctScAndWf()
	private Object[] cabLctScAndWf(String vvCd, String userId, String strcode,
			List<List<GeneralEventLogValueObject>> generalEventLogLists) throws Exception {
		String billWharfInd = "N";
		String billSvcChargeInd = "N";
		String billStoreInd = "N";
		String billProcessInd = "N";
		int sizeuaexportsc = 0;
		int sizeuaexportwc = 0;
		VesselTxnEventLogValueObject vesselTxnEventLogValueObject = null;
		List<GeneralEventLogValueObject> generalEventLogList = new ArrayList<GeneralEventLogValueObject>();
		try {
			log.info("START: TriggerLct  DAO  Start vvCd:" + vvCd + " userId:" + userId + " strcode:" + strcode
					+ " generalEventLogLists:" + generalEventLogLists);
			txnDttm = getSystemDate();

			sizeuaexportsc = generalEventLogLists.get(0).size();
			generalEventLogList.addAll(generalEventLogLists.get(0));

			sizeuaexportwc = generalEventLogLists.get(1).size();
			generalEventLogList.addAll(generalEventLogLists.get(1));

			if (sizeuaexportsc > 0) {
				billSvcChargeInd = "Y";
			}
			if (sizeuaexportwc > 0) {
				billWharfInd = "Y";
			}

			if (generalEventLogList.size() > 0) {
				vesselTxnEventLogValueObject = new VesselTxnEventLogValueObject();
				vesselTxnEventLogValueObject.setVvCd(vvCd);
				vesselTxnEventLogValueObject.setTxnDttm(txnDttm);
				vesselTxnEventLogValueObject.setBillWharfInd(billWharfInd);
				vesselTxnEventLogValueObject.setBillSvcChargeInd(billSvcChargeInd);
				vesselTxnEventLogValueObject.setBillStoreInd(billStoreInd);
				vesselTxnEventLogValueObject.setBillProcessInd(billProcessInd);
				vesselTxnEventLogValueObject.setLastModifyUserId(userId);
				vesselTxnEventLogValueObject.setLastModifyDttm(txnDttm);
				log.info("**** VESSEL TXN EVENT LOG FOR UA VOS ******");
				log.info("|strvvcd : " + vvCd);
				log.info("|txnDttm : " + txnDttm);
				log.info("|billWharfInd : " + billWharfInd);
				log.info("|billSvcChargeInd : " + billSvcChargeInd);
				log.info("|billStoreInd : " + billStoreInd);
				log.info("|billProcessInd : " + billProcessInd);
				log.info("|struserid : " + userId);
				log.info("|txnDttm : " + txnDttm);
				log.info("==== END OF VESSEL TXN EVENT LOG FOR UA VOS ====");
			}
		} catch (Exception e) {
			log.error("Exception: TriggerLct ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: TriggerLct  DAO vesselTxnEventLogValueObject:"
					+ (vesselTxnEventLogValueObject != null ? vesselTxnEventLogValueObject.toString() : "")
					+ ",generalEventLogList:" + generalEventLogList.size());
		}
		return new Object[] { vesselTxnEventLogValueObject, generalEventLogList };
	}
}
