package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
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

import sg.com.jp.generalcargo.dao.CabUaExportServiceChargeRepo;
import sg.com.jp.generalcargo.dao.CabUaExportStoreRentChargeRepo;
import sg.com.jp.generalcargo.dao.CabUaExportWharfChargeRepo;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.TransactionLoggerRepository;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.GbmsCabValueObject;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("TransactionLoggerRepository")
public class TransactionLoggerJdbcRepository implements TransactionLoggerRepository {

	private static final Log log = LogFactory.getLog(TransactionLoggerJdbcRepository.class);
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private ProcessGBLogRepository processGBLogRepo;

	@Autowired
	private CabUaExportServiceChargeRepo cabUaExportServiceChargeRepo;

	@Autowired
	private CabUaExportWharfChargeRepo cabUaExportWharfChargeRepo;

	@Autowired
	private CabUaExportStoreRentChargeRepo cabUaExportStoreRentChargeRepo;
	
	public Timestamp txnDttm = null;

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: triggerShutoutCargoDN()
	@Override
	public String triggerShutoutCargoDN(String dn_nbr, String userId) throws BusinessException {
		List<GeneralEventLogValueObject> GeneralEventLogArrayList = new ArrayList<GeneralEventLogValueObject>();
		// MCC for 1st DN creation
		List<GeneralEventLogValueObject> GeneralEventLogEDOArrayList = new ArrayList<GeneralEventLogValueObject>();
		String crgcat = "";

		String billWharfInd = "N";
		String billSvcChargeInd = "N";
		String billStoreInd = "N";
		String billProcessInd = "N";

		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		GbmsCabValueObject gbmsCabValueObject = new GbmsCabValueObject();
		gbmsCabValueObject.setStatus("FALSE");

		/* Wharfage bill */
		StringBuilder sb = new StringBuilder();
		sb.append(
				"SELECT edo.esn_asn_nbr, vc.vv_cd, dn.dn_nbr, dn.edo_asn_nbr, CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS scheme,");
		sb.append("       edo.crg_status,");
		sb.append("       GREATEST (edo.nom_wt / 1000, edo.nom_vol) AS edo_bill_ton,");
		sb.append("       edo.nbr_pkgs, dn.nbr_pkgs AS dn_nbr_pkgs, dn.trans_type AS transtype,");
		sb.append("       edo.dis_type distype, dn.trans_dttm AS dndttm");
		sb.append("  FROM vessel_call vc, vessel_scheme vs, gb_edo edo, dn_details dn, esn es"); // MCC added for
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
		try {
			log.info("START: triggerShutoutCargoDN DAO dn_nbr:" + CommonUtility.deNull(dn_nbr) + "userId:" + CommonUtility.deNull(userId));


			txnDttm = getSystemDate();

			String vvCd = getVvCdByDnNbr( dn_nbr);

			// MCC check is this is first active shut-out dn for the shutout EDO
			boolean isFirstDN = processGBLogRepo.isFirstDNForEDO(dn_nbr);
			log.info("isFirstActiveDN for shutout EDO : " + isFirstDN);

			// process wharfage
			log.info(" *** triggerShutoutCargoDN SQL *****" + sqlwharf1);
			paramMap.put("dnNbr", dn_nbr);
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
			sb.append("  FROM vessel_call vc,");
			sb.append("       vessel_scheme vs,");
			sb.append("       berthing b,");
			sb.append("       gb_edo edo,");
			sb.append("       esn es,"); // MCC for EPC_IND check
			sb.append("       dn_details dn");
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
			if ("Y".equalsIgnoreCase(billStoreInd) || "Y".equalsIgnoreCase(billWharfInd)) {
				VesselTxnEventLogValueObject vesselTxnEventLogValueObject = new VesselTxnEventLogValueObject();

				vesselTxnEventLogValueObject.setVvCd(vvCd);
				vesselTxnEventLogValueObject.setTxnDttm(txnDttm);
				vesselTxnEventLogValueObject.setBillWharfInd(billWharfInd);
				vesselTxnEventLogValueObject.setBillSvcChargeInd(billSvcChargeInd);
				vesselTxnEventLogValueObject.setBillStoreInd(billStoreInd);
				vesselTxnEventLogValueObject.setBillProcessInd(billProcessInd);
				vesselTxnEventLogValueObject.setLastModifyUserId(userId);
				vesselTxnEventLogValueObject.setLastModifyDttm(txnDttm);
				log.info("**** VESSEL TXN EVENT LOG FOR DN VOS ******");
				log.info("|strvvcd : " + vvCd);
				log.info("|txnDttm : " + txnDttm);
				log.info("|billWharfInd : " + billWharfInd);
				log.info("|billSvcChargeInd : " + billSvcChargeInd);
				log.info("|billStoreInd : " + billStoreInd);
				log.info("|billProcessInd : " + billProcessInd);
				log.info("|struserid : " + userId);
				log.info("|txnDttm : " + txnDttm);
				log.info("==== END OF VESSEL TXN EVENT LOG FOR DN VOS ====");

				List<ChargeableBillValueObject> ChargeableBillArrayList = new ArrayList<ChargeableBillValueObject>();
				log.info("**** Process Log created for DN after not overwrite business type******");
				// New EVM ENhancement Sripriya 2nd April 2018 to not overwrite the business
				// type
				ChargeableBillArrayList = processGBLogRepo.executeBillCharges(vesselTxnEventLogValueObject,
						GeneralEventLogArrayList, ProcessChargeConst.REF_IND_DN);
				log.info("**** END Process Log created for DN after not overwrite business type******"
						+ ChargeableBillArrayList.size());
				int i = ChargeableBillArrayList.size();
				log.info("ChargeableBillArrayList : " + i);

				// MCC log additional event for shutout EDO for WF during 1st DN creation
				List<GeneralEventLogValueObject> GeneralEventLogEDOArrayList2 = new ArrayList<GeneralEventLogValueObject>();

				if (GeneralEventLogEDOArrayList.size() > 0) {

					for (int cnt = 0; cnt < GeneralEventLogEDOArrayList.size(); cnt++) {

						GeneralEventLogValueObject generalEventLogValueObject = (GeneralEventLogValueObject) GeneralEventLogEDOArrayList
								.get(cnt);
						generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_EDO);
						generalEventLogValueObject.setIsFirstDNForEDO("Y");
						GeneralEventLogEDOArrayList2.add(generalEventLogValueObject);
					}

					List<ChargeableBillValueObject> chargeableEDOBillArrayList = new ArrayList<ChargeableBillValueObject>();
					log.info("**** Process Log created for shutout EDO after not overwritting business type******");
					// New EVM ENhancement Sripriya 2nd April 2018 to not overwrite the business
					// type
					chargeableEDOBillArrayList = processGBLogRepo.executeBillCharges(vesselTxnEventLogValueObject,
							GeneralEventLogEDOArrayList2, ProcessChargeConst.REF_IND_EDO);
					log.info("**** END Process Log created for shutout EDO after not overwritting business type******"
							+ chargeableEDOBillArrayList.size());
					int edoBillCount = chargeableEDOBillArrayList.size();
					log.info("EDO bill ChargeableBillArrayList after not overwritting business type: " + edoBillCount);

				}

				gbmsCabValueObject.setStatus("TRUE");

			}
			log.info("end of CabShutoutCargoDN xxxxxxxx******");

		} catch (NullPointerException e) {
			log.info("Exception triggerShutoutCargoDN :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception triggerShutoutCargoDN :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: triggerShutoutCargoDN  DAO  END");
		}
		return gbmsCabValueObject.getStatus();
	}

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

			log.info(" *** getSystemDate SQL *****" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				sdate = (Timestamp) rs.getObject("SYSDATE");
			}
			log.info("END: *** getSystemDate *****" + sdate);

		} catch (NullPointerException e) {
			log.info("Exception getSystemDate :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSystemDate :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSystemDate  DAO  END");
		}
		return sdate;
	}// end of getSysdate

	// package: ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb
	// method: getVvCdByDnNbr()
	private String getVvCdByDnNbr(String dnNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		String vvCd = null;
		try {
			log.info("START: getVvCdByDnNbr  DAO  Start Obj dnNbr:" + CommonUtility.deNull(dnNbr));
			sql.append(" SELECT var_nbr FROM gb_edo e, dn_details d WHERE e.edo_asn_nbr = d.edo_asn_nbr ");
			sql.append(" AND d.dn_nbr =:dnNbr ");

			paramMap.put("dnNbr", dnNbr);
			log.info(" *** getVvCdByDnNbr SQL *****" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				vvCd = rs.getString("var_nbr");
			}
			log.info("END: *** getVvCdByDnNbr *****" + CommonUtility.deNull(vvCd));

		} catch (NullPointerException e) {
			log.info("Exception getVvCdByDnNbr :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVvCdByDnNbr :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVvCdByDnNbr  DAO  END");
		}
		return vvCd;
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
			log.info(" *** getEdoBillAcctNbr SQL *****" + sql.toString());
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
			log.info("END: *** getEdoBillAcctNbr *****" + CommonUtility.deNull(billAcctNbr));

		} catch (NullPointerException e) {
			log.info("Exception getEdoBillAcctNbr :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEdoBillAcctNbr :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEdoBillAcctNbr  DAO  END");
		}
		return billAcctNbr;
	}// end of Scheme Billable account number

	// ejb.sessionBeans.gbms.cab -->TransactionLoggerEjb

	@Override
	public String TriggerDN(String dnnbr, String struserid) throws BusinessException {
		String updatestatus = "FALSE";

		try {

			log.info("START: TriggerDN DAO dnnbr:" + CommonUtility.deNull(dnnbr) +" struserid:"+ CommonUtility.deNull(struserid) );

			// add by hujun on 15/8/2011
			if (isShutoutCargoDN(dnnbr)) {
				return triggerShutoutCargoDN(dnnbr, struserid);
			}
			// add end
			log.info("INSIDE Trigger CAB dn METHOD");
			GbmsCabValueObject gbmsCabValueObject = CabDN(dnnbr, struserid);
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
			log.info("END: *** retrieveTariffGBCustFSP *****" + CommonUtility.deNull(updatestatus));
		} catch (BusinessException e) {
			log.info("Exception retrieveTariffGBCustFSP :", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception retrieveTariffGBCustFSP :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception retrieveTariffGBCustFSP :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveTariffGBCustFSP  DAO  END");
		}
		return updatestatus;
	}

	private GbmsCabValueObject CabDN(String dn_nbr, String struserid) throws BusinessException {

		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs4 = null;
		SqlRowSet rssrlr = null;
		SqlRowSet rsexpsch = null;
		SqlRowSet rssrnl = null;
		SqlRowSet rsdn = null;
		Map<String, String> paramMap = new HashMap<String, String>();

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

		GbmsCabValueObject gbmsCabValueObject = new GbmsCabValueObject();
		int count1 = 0;
		int count2 = 0;
		int count4 = 0;
		int count5 = 0;
		int count6 = 0;

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
		sqlserv.append(" from manifest_details md, vessel_call vc,gb_edo edo,dn_details dn ");
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
		sqlwharf3.append(" from manifest_details md,vessel_call vc, vessel_scheme vs,gb_edo edo, ");
		sqlwharf3.append(" dn_details dn where md.var_nbr = vc.vv_cd and md.mft_seq_nbr = edo.mft_seq_nbr ");
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
		sqlSRLR1.append(" manifest_details md,vessel_call vc,vessel_scheme vs,berthing b, ");
		sqlSRLR1.append(" gb_edo edo,dn_details dn where md.var_nbr = vc.vv_cd and vc.vv_cd = ");
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
		sqlSRNL1.append(" nvl(edo.FREE_STG_DAYS,0) fsdays from manifest_details md,vessel_call vc, ");
		sqlSRNL1.append(" vessel_scheme vs,gb_edo edo,dn_details dn,berthing b where md.var_nbr = ");
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

		try {
			log.info("START: CabDN  DAO  Start Obj dn_nbr:" + CommonUtility.deNull(dn_nbr) + "struserid" + CommonUtility.deNull(struserid));
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
				log.info(" *** CabDN SQL *****" + sqlserv.toString());
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
				log.info(" *** CabDN SQL *****" + sqlwharf1.toString());
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
				log.info(" *** CabDN SQL *****" + sqlwharf3.toString());
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
			log.info(" *** CabDN SQL *****" + sqlSRLR1.toString());
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
				String expschsql = "select CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS  scheme from vessel_call vc where vv_cd=:loadvvcd";

				paramMap.put("loadvvcd", loadvvcd);
				log.info(" *** CabDN SQL *****" + expschsql.toString());
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
			log.info(" *** CabDN SQL *****" + sqlSRNL1.toString());
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
				String expschsql = "select CASE WHEN VC.COMBI_GC_OPS_IND ='Y' THEN VC.COMBI_GC_SCHEME ELSE VC.SCHEME END AS scheme from vessel_call vc where vv_cd=:loadvvcd";
				// mc consulting - End - Change to get sub scheme

				paramMap.put("loadvvcd", loadvvcd);
				log.info(" *** CabDN SQL *****" + expschsql.toString());
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
			String sql = "select bill_store_triggered_ind from dn_details where dn_nbr=:dnNbr and dn_status='A'";
			String bill_store_triggered_ind = "N";

			paramMap.put("dnNbr", dn_nbr);
			log.info(" *** CabDN SQL *****" + sql);
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
			if (count1 > 0 || count2 > 0 || count4 > 0 || count5 > 0 || count6 > 0) {
				VesselTxnEventLogValueObject vesselTxnEventLogValueObject = new VesselTxnEventLogValueObject();

				vesselTxnEventLogValueObject.setVvCd(strvvcd);
				vesselTxnEventLogValueObject.setTxnDttm(txnDttm);
				vesselTxnEventLogValueObject.setBillWharfInd(billWharfInd);
				vesselTxnEventLogValueObject.setBillSvcChargeInd(billSvcChargeInd);
				vesselTxnEventLogValueObject.setBillStoreInd(billStoreInd);
				if (bill_store_triggered_ind.equals("Y")) {
					vesselTxnEventLogValueObject.setBillStoreInd(bill_store_triggered_ind);
				}
				vesselTxnEventLogValueObject.setBillProcessInd(billProcessInd);
				vesselTxnEventLogValueObject.setLastModifyUserId(struserid);
				vesselTxnEventLogValueObject.setLastModifyDttm(txnDttm);
				log.info("**** VESSEL TXN EVENT LOG FOR DN VOS ******");
				log.info("|strvvcd : " + strvvcd);
				log.info("|txnDttm : " + txnDttm);
				log.info("|billWharfInd : " + billWharfInd);
				log.info("|billSvcChargeInd : " + billSvcChargeInd);
				log.info("|billStoreInd : " + billStoreInd);
				log.info("|billProcessInd : " + billProcessInd);
				log.info("|struserid : " + struserid);
				log.info("|txnDttm : " + txnDttm);
				log.info("==== END OF VESSEL TXN EVENT LOG FOR DN VOS ====");

				List<ChargeableBillValueObject> ChargeableBillArrayList = new ArrayList<ChargeableBillValueObject>();
				log.info("**** Process Log created for DN to not overwrite business type EVM******");
				ChargeableBillArrayList = processGBLogRepo.executeBillCharges(vesselTxnEventLogValueObject,
						GeneralEventLogArrayList, ProcessChargeConst.REF_IND_DN);
				log.info("**** END Process Log created for DN to not overwrite business type EVM******");
				int i = ChargeableBillArrayList.size();
				log.info("ChargeableBillArrayList : " + i);
				gbmsCabValueObject.setStatus("TRUE");

				// MCC log additional event for EDO for WF and SC during 1st DN creation
				List<GeneralEventLogValueObject> GeneralEventLogEDOArrayList2 = new ArrayList<GeneralEventLogValueObject>();
				if (GeneralEventLogEDOArrayList.size() > 0) {

					for (int cnt = 0; cnt < GeneralEventLogEDOArrayList.size(); cnt++) {

						GeneralEventLogValueObject generalEventLogValueObject = (GeneralEventLogValueObject) GeneralEventLogEDOArrayList
								.get(cnt);
						generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_EDO);
						generalEventLogValueObject.setIsFirstDNForEDO("Y");
						generalEventLogValueObject.setTotalPackDn(0);
						GeneralEventLogEDOArrayList2.add(generalEventLogValueObject);
					}

					List<ChargeableBillValueObject> chargeableEDOBillArrayList = new ArrayList<ChargeableBillValueObject>();
					log.info("**** Process Log created for EDO to not overwrite business type******");
					chargeableEDOBillArrayList = processGBLogRepo.executeBillCharges(vesselTxnEventLogValueObject,
							GeneralEventLogEDOArrayList2, ProcessChargeConst.REF_IND_EDO);
					log.info("**** END Process Log created for EDO to not overwrite business type******");
					int edoBillCount = chargeableEDOBillArrayList.size();
					log.info("EDO bill ChargeableBillArrayList : " + edoBillCount);
				}

			}
			log.info("INSIDE CAB DN end of this  METHOD");
		} catch (BusinessException e) {
			log.info("Exception CabDN :", e);
			//throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception CabDN :", e);
			//throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception CabDN :", e);
			//throw new BusinessException("M4201");
		} finally {
			log.info("END: CabDN  DAO  END");
		}
		return gbmsCabValueObject;
	}

	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->getSchemeName()
	private String getSchemeName(String strvvcd) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		String actualscheme = "";
		String schemecd = "";
		String subScheme = "";
		String gcOperations = "";
		String abcd = "";

		try {
			log.info("START: getSchemeName  DAO  Start Obj strvvcd:" + strvvcd);
			sql.append(" SELECT VESCALL.SCHEME, VESCALL.COMBI_GC_SCHEME, VESCALL.COMBI_GC_OPS_IND,VS.AB_CD FROM ");
			sql.append(" VESSEL_CALL VESCALL, VESSEL_SCHEME VS WHERE VESCALL.SCHEME=VS.SCHEME_CD AND ");
			sql.append(" VESCALL.VV_CD=:strvvcd ");

			paramMap.put("strvvcd", strvvcd);
			log.info(" *** getSchemeName SQL *****" + sql.toString());
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
	}

	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->isClosedLct()
	private boolean isClosedLct(String vvCd) throws SQLException, BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		String closeLctInd = null;
		try {
			log.info("START: isClosedLct  DAO  Start Obj vvCd:" + vvCd);
			sql.append(" SELECT gb_close_lct_ind FROM vessel_call WHERE vv_cd=:vvCd ");

			paramMap.put("vvCd", vvCd);
			log.info(" *** isClosedLct SQL *****" + sql.toString());
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
			log.info("END: isClosedLct  DAO  END");
		}
	}

	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->deriveType()
	public static String deriveType(String cargoCategoryCode, String defaultType) {
		return (ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI.equalsIgnoreCase(cargoCategoryCode))
				? ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI
				: defaultType;
	}

	
	private boolean isShutoutCargoDN(String dn_nbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		String shutoutInd = null;
		try {
			log.info("START: isShutoutCargoDN  DAO  Start Obj dn_nbr:" + dn_nbr);

			sql.append(" SELECT edo.shutout_ind from gb_edo edo, dn_details dn where dn.dn_nbr=:dnNbr ");
			sql.append(" and dn.edo_asn_nbr=edo.edo_asn_nbr ");

			paramMap.put("dnNbr", dn_nbr);
			log.info(" *** isShutoutCargoDN SQL *****" + sql.toString());
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
			log.info("END: isShutoutCargoDN  DAO  END");
		}
	}

	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->getBABillAcctNbr()
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
			log.info("START: getBABillAcctNbr  DAO  Start Obj vvCd:" + CommonUtility.deNull(vvCd) + "EdoNbr:" + CommonUtility.deNull(EdoNbr));
			sql.append(" SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD =:vvCd ");

			paramMap.put("vvCd", vvCd);
			log.info(" *** getBABillAcctNbr SQL *****" + sql.toString());
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
			log.info("END: getBABillAcctNbr  DAO  END");
		}
		return result;
	}

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
			log.info(" *** isCashPayment SQL *****" + sql.toString());
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
			log.info("END: isCashPayment  DAO  END");
		}
		return result;
	}

	private String getLoadVVCd(String transType, String dnNbr) throws BusinessException {
		String result = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		if (transType.equalsIgnoreCase("A")) {
			log.info("--------------------SQL in getLoadVVCd = " + sql + " --------------------");
			try {
				log.info("START: getLoadVVCd  DAO  Start Obj transType:" + CommonUtility.deNull(transType) + "dnNbr:" + CommonUtility.deNull(dnNbr));
				sql.append(" select out_voy_var_nbr from esn where esn_asn_nbr = (select tesn_asn_nbr ");
				sql.append(" from dn_details where dn_nbr =:dnNbr ) ");

				paramMap.put("dnNbr", dnNbr);
				log.info(" *** getLoadVVCd SQL *****" + sql.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
				if (rs.next()) {
					result = rs.getString(1);
					result = result == null ? "" : result.trim();
				}
				log.info("--------------------Loading Vessel is " + result + "for DN " + dnNbr
						+ " --------------------");
			} catch (NullPointerException e) {
				log.info("Exception getLoadVVCd :", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getLoadVVCd :", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getLoadVVCd  DAO  END");
			}
		}
		return result;
	}

	public String getTariffSubCat(String cargo_category_cd) {
		log.info("START: getTariffSubCat "+" cargo_category_cd:"+CommonUtility.deNull(cargo_category_cd));

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
		log.info("END: *** getTariffSubCat Result *****" + CommonUtility.deNull(tariff_sub_cat));
		return tariff_sub_cat;
	}

	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->getSATenInd()
	public String getSATenInd(String vv_cd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		StringBuilder sql = new StringBuilder();
		String sTenInd = "";

		try {
			log.info("START: getSATenInd  DAO  Start Obj vv_cd:" + CommonUtility.deNull(vv_cd));
			sql.append(" select cust.tenancy_ind from vessel_call vc, ");
			sql.append(" customer cust,cust_acct cact where vc.bill_acct_nbr = cact.acct_nbr ");
			sql.append(" and cust.CUST_CD = cact.CUST_CD  and vc.vv_cd =:vvCd ");

			paramMap.put("vvCd", vv_cd);
			log.info(" *** getSATenInd SQL *****" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				sTenInd = CommonUtility.deNull(rs.getString("tenancy_ind"));
			}
			
			log.info("END: *** getSATenInd Result *****" + CommonUtility.deNull(sTenInd));
		} catch (NullPointerException e) {
			log.info("Exception getSATenInd :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSATenInd :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSATenInd  DAO  END");
		}
		return sTenInd;
	}

	// ejb.sessionBeans.gbms.cab-->TransactionLoggerEjb-->getSCTenInd()
	public String getSCTenInd(String acctnbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		String sTenInd = "";

		try {
			log.info("START: getSCTenInd  DAO  Start Obj acctnbr:" + CommonUtility.deNull(acctnbr));
			sql.append(" select cust.tenancy_ind from ");
			sql.append(" customer cust,cust_acct cact where ");
			sql.append(" cust.CUST_CD = cact.CUST_CD  and cact.acct_nbr =:acctNbr ");

			paramMap.put("acctNbr", acctnbr);
			log.info(" *** getSCTenInd SQL *****" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				sTenInd = CommonUtility.deNull(rs.getString("tenancy_ind"));
			}
			log.info("END: *** getSCTenInd Result *****" + CommonUtility.deNull(sTenInd));
		} catch (NullPointerException e) {
			log.info("Exception getSCTenInd :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSCTenInd :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSCTenInd  DAO  END");
		}
		return sTenInd;
	}

	public String getCOD(String strvvcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		String codval = "0";
		try {
			log.info("START: getCOD  DAO  Start Obj strvvcd:" + CommonUtility.deNull(strvvcd));
			sql.append(" SELECT GB_COD_DTTM FROM BERTHING B WHERE SHIFT_IND = 1 AND B.VV_CD=:strvvcd ");

			paramMap.put("strvvcd", strvvcd);
			log.info(" *** getCOD SQL *****" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				codval = CommonUtility.deNull(rs.getString("GB_COD_DTTM"));
			}
			if (codval != null && !codval.equals(""))
				codval = "1";
			
			log.info("END: *** getCOD Result *****" + CommonUtility.deNull(codval));

		} catch (NullPointerException e) {
			log.info("Exception getCOD :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCOD :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCOD  DAO  END");
		}
		return codval;
	}

	// package: jp.src.cab.processCharges-->ProcessCommon
	// method: computeBillTon()
	public double computeBillTon(double edoBillTon, int edoPkgs, int dnPkgs) throws BusinessException {
		double billTon = 0.0;

		try {
			log.info("START:computeBillTon" + "edoBillTon" + edoBillTon + "edoPkgs:" + edoPkgs + "dnPkgs:" + dnPkgs);
			// billTon = (edoBillTon / edoPkgs) * dnPkgs;
			// log.info("billTon: " + billTon);
			// BigDecimal bigDec = new BigDecimal (billTon);
			// bigDec = bigDec.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			billTon = (new BigDecimal("" + (edoBillTon / edoPkgs * dnPkgs)).setScale(2, BigDecimal.ROUND_HALF_UP)
					.doubleValue());
			log.info("billTon: " + billTon);
		} catch (Exception ex) {
			throw new BusinessException("M4201");
		}
		return billTon;
	}

	@Override
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
			log.info("START: TriggerUa  DAO  Start Obj " + " strcode:" + CommonUtility.deNull(strcode) + " struserid:" + CommonUtility.deNull(struserid) + " vvcd:"
					+ CommonUtility.deNull(vvcd));
			log.info("INSIDE Trigger CAB  METHOD xxxxxxc");
			txnDttm = getSystemDate();
			List<GeneralEventLogValueObject> GeneralEventLogArrayList = new ArrayList<GeneralEventLogValueObject>();
			List<GeneralEventLogValueObject> GeneralEventLogArrayList1 = new ArrayList<GeneralEventLogValueObject>();
			List<GeneralEventLogValueObject> GeneralEventLogArrayList2 = new ArrayList<GeneralEventLogValueObject>();
			List<GeneralEventLogValueObject> GeneralEventLogArrayList3 = new ArrayList<GeneralEventLogValueObject>();

			// add new scheme for LCT, 13.feb.11 by hpeng
			if (!ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(getSchemeName(vvcd))) {

				log.info("CALLED CAB UA Service Charge METHOD ddddd");
				log.info(" GeneralEventLogArrayList1: " + sizeuaexportsc);
				// CabUaExportServiceCharge cabUaExportServiceCharge = new
				// CabUaExportServiceCharge();
				log.info("ducta1 >>>>strcode>>>>>>>>>>>>>>>>>>>>>" + strcode);
				log.info("ducta1 >>>>struserid>>>>>>>>>>>>>>>>>>>>>" + struserid);
				GeneralEventLogArrayList1 = cabUaExportServiceChargeRepo.processDetails(strcode, struserid);
				sizeuaexportsc = GeneralEventLogArrayList1.size();
				log.info("ducta1 >>>>>>>>>>>>>>>>>>>>>>>>>" + sizeuaexportsc);
				for (int i = 0; i < sizeuaexportsc; i++) {
					GeneralEventLogArrayList.add((GeneralEventLogValueObject) GeneralEventLogArrayList1.get(i));
				}
				log.info("CALLED CAB UA Service Charge METHOD");
				log.info("======================================");
				log.info(" GeneralEventLogArrayList1: " + sizeuaexportsc);
				// CabUaExportWharfCharge cabUaExportWharfCharge = new CabUaExportWharfCharge();
				GeneralEventLogArrayList2 = cabUaExportWharfChargeRepo.processDetails(strcode, struserid);
				// cabUaExportServiceCharge.processDetails(gbmsconnection,strcode,struserid);

				sizeuaexportwc = GeneralEventLogArrayList2.size();
				for (int i = 0; i < sizeuaexportwc; i++) {
					GeneralEventLogArrayList.add((GeneralEventLogValueObject) GeneralEventLogArrayList2.get(i));
				}
				log.info("CALLED CAB UA Wharf Charge METHOD");
				log.info(" GeneralEventLogArrayList2: " + sizeuaexportwc);

			}

			// added for store rent start 02/10/2002
			log.info("CALLED CAB UA Store Rent METHOD");
			log.info(" GeneralEventLogArrayList3: " + sizeuaexportsr);
			// CabUaExportStoreRentChargeRepo cabUaExportStoreRentCharge = new
			// CabUaExportStoreRentChargeRepo();
			GeneralEventLogArrayList3 = cabUaExportStoreRentChargeRepo.processDetails(strcode, struserid);
			sizeuaexportsr = GeneralEventLogArrayList3.size();
			for (int i = 0; i < sizeuaexportsr; i++) {
				GeneralEventLogArrayList.add((GeneralEventLogValueObject) GeneralEventLogArrayList3.get(i));
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

			// String bill_wharf_triggered_ind="N";
			// String bill_service_triggered_ind="N";
			// if (sizeuaexportwc >0) {
			// bill_wharf_triggered_ind="Y";
			// }
			// if (sizeuaexportsc >0) {
			// bill_service_triggered_ind="Y";
			// }
			// if (sizeuaexportsc >0 || sizeuaexportwc >0) {
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
			// }
			log.info("END: *** TriggerUa Result *****" + updatestatus.toString());
		} catch (BusinessException e) {
			log.info("Exception TriggerUa :", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception TriggerUa :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception TriggerUa :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: TriggerUa  DAO  END");
		}
		return updatestatus;
	}

}
