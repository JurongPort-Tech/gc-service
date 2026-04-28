package sg.com.jp.generalcargo.dao.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.GBEventLogRepository;
import sg.com.jp.generalcargo.dao.GBMSTriggerIndRepository;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;
import sg.com.jp.generalcargo.util.ProcessChargeException;

@Repository("GBEventLogRepository")
public class GBEventLogJdbcRepository implements GBEventLogRepository {

	private static final Log log = LogFactory.getLog(GBEventLogJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	private GBMSTriggerIndRepository GBMSTriggerIndRepo;

	// StartRegion GBEventLogJdbcRepository
	

	// jp.src.ejb.sessionBeans.cab.gbEventLog-->GBEventLogEJB-->cancelGBEventLog()
	// Added by Irene Tan on 11 Nov 2003 : Add in new method for cancellation of
	// Stuffing/Unstuffing charges
	/**
	 * This method cancels the stuffing/unstuffing charge if the bill has not been
	 * triggered
	 * 
	 * @param cntrSeqNo Container Sequence No
	 * @param refInd    reference indicator (SU - Stuffing/Unstuffing)
	 * @param vvCd      Vessel Call ID
	 * @param vvInd     reference indicator (SU - Stuffing/Unstuffing)
	 * @throws BusinessException
	 * 
	 */
	@Override
	public void cancelGBEventLog(int cntrSeqNo, String refInd, String vvCd, String vvInd) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: cancelGBEventLog  DAO  Start cntrSeqNo:" + CommonUtility.deNull(String.valueOf(cntrSeqNo)) + "refInd:" + CommonUtility.deNull(refInd)
					+ "vvCd:" + CommonUtility.deNull(vvCd) + "vvInd:" + CommonUtility.deNull(vvInd));
			

			sb.append(" update gb_charge_event_log set bill_ind='D' where ");
			sb.append(" cntr_seq_nbr=:cntrSeqNo  and ref_ind=:refInd and ");
			// end added by Irene Tan on 11 Nov 2003

			if (vvInd.trim().equals(ProcessChargeConst.DISC_VV_IND)) {
				sb.append(" disc_vv_cd=:vvCd ");
			} else if (vvInd.trim().equals(ProcessChargeConst.LOAD_VV_IND)) {
				sb.append(" load_vv_cd=:vvCd ");
			}

			log.info(" ***cancelGBEventLog SQL *****" + sb.toString());

			paramMap.put("cntrSeqNo", cntrSeqNo);
			paramMap.put("refInd", refInd);
			paramMap.put("vvCd", vvCd);
			log.info("paramMap: " + paramMap);
			int ctr = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("ctr: " + ctr);
			if (ctr <= 0) {
				throw new BusinessException("Unable to update gb_charge_event_log table!");
			}
			
		} catch (NullPointerException e) {
			log.info("Exception cancelGBEventLog : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception cancelGBEventLog : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception cancelGBEventLog : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cancelGBEventLog  DAO  END");
		}
	}

	public void cancelGBEventLog(String refNbr, String refInd) throws BusinessException {
		int ctr = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: cancelGBEventLog  DAO  Start Obj :: refNbr: " + CommonUtility.deNull(refNbr) + "refInd:" + CommonUtility.deNull(refInd));

			StringBuffer buffer = new StringBuffer();

			buffer.append("update gb_charge_event_log set bill_ind='D' where ref_ind=:refInd");
			if (refInd.trim().equals("DN")) {
				buffer.append(" and dn_nbr=:refNbr ");
			} else if (refInd.trim().equals("UA")) {
				buffer.append(" and ua_nbr=:refNbr ");
			} else if (refInd.trim().equals("BL")) {
				buffer.append(" and bl_nbr=:refNbr ");
			} else if (refInd.trim().equals("BR")) {
				buffer.append(" and bk_ref_nbr=:refNbr ");
			} else if (refInd.trim().equals("ES")) {
				buffer.append(" and esn_asn_nbr=:refNbr ");
			}
			// MCC to cancel ED billing events for first DN
			else if (refInd.trim().equals("ED")) {
				buffer.append(" and edo_asn_nbr=:refNbr ");
			}
			String sql = buffer.toString();
			paramMap.put("refInd", refInd.trim());
			paramMap.put("refNbr", refNbr.trim());
			log.info("sql: " + sql);
			log.info("paramMap: " + paramMap);
			ctr = namedParameterJdbcTemplate.update(sql, paramMap);
			log.info("ctr: " + ctr);
			if (ctr <= 0) {
				String[] tempString = { refNbr, refInd };
				throw new BusinessException(
						CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Cannot_Add_GB_Event_Table, tempString));

			}
		} catch (BusinessException ex) {
			log.info("Exception cancelGBEventLog : ", ex);
			throw new BusinessException(ex.getMessage());
		} catch (Exception ex) {
			log.info("Exception cancelGBEventLog : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("** END cancelGBEventLog() ** ");
		}
	}

	public GeneralEventLogValueObject[] getGBEventLog(String refNo, String refInd) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<GeneralEventLogValueObject> gbEventLogArrayList = new ArrayList<>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getGBEventLog  DAO  Start Obj :: refNo: " + CommonUtility.deNull(refNo) + "refInd:" + CommonUtility.deNull(refInd));
			if (refInd.trim().equals("DN")) {
				sb.append(
						"select disc_vv_cd, load_vv_cd, vv_ind, business_type, scheme_cd, tariff_main_cat_cd, tariff_sub_cat_cd, mvmt, ");
				sb.append(
						"  type, cargo_type, local_leg, disc_gateway, bl_nbr, edo_asn_nbr, bk_ref_nbr, esn_asn_nbr, dn_nbr, ua_nbr, ");
				sb.append(
						" bill_ton_bl, bill_ton_edo, bill_ton_dn, bill_ton_esn, bill_ton_bkg, load_ton_cs, shutout_ton_cs, ");
				sb.append(
						" count_unit, total_pack_edo, total_pack_dn, bill_acct_nbr, print_dttm, last_modify_dttm, cntr_nbr,");
				sb.append(
						" cntr_seq_nbr, cntr_size, cntr_cat, bill_ind from gb_charge_event_log where bill_ind='N' and ref_ind='DN' and dn_nbr=:refNo ");

			} else if (refInd.trim().equals("UA")) {
				sb.append(
						"select disc_vv_cd, load_vv_cd, vv_ind, business_type, scheme_cd, tariff_main_cat_cd, tariff_sub_cat_cd, mvmt, ");
				sb.append(
						" type, cargo_type, local_leg, disc_gateway, bl_nbr, edo_asn_nbr, bk_ref_nbr, esn_asn_nbr, dn_nbr,");
				sb.append(
						"  ua_nbr, bill_ton_bl, bill_ton_edo, bill_ton_dn, bill_ton_esn, bill_ton_bkg, load_ton_cs, shutout_ton_cs,");
				sb.append("  count_unit, total_pack_edo, total_pack_dn, bill_acct_nbr, print_dttm, last_modify_dttm,");
				sb.append(
						"  cntr_nbr, cntr_seq_nbr, cntr_size, cntr_cat, bill_ind from gb_charge_event_log where bill_ind='N' and ref_ind='UA' and ua_nbr=:refNo");

			} else if (refInd.trim().equals("ED")) {
				sb.append(
						"select disc_vv_cd, load_vv_cd, vv_ind, business_type, scheme_cd, tariff_main_cat_cd, tariff_sub_cat_cd, mvmt, ");
				sb.append(
						" type, cargo_type, local_leg, disc_gateway, bl_nbr, edo_asn_nbr, bk_ref_nbr, esn_asn_nbr, dn_nbr, ");
				sb.append(" ua_nbr, bill_ton_bl, bill_ton_edo, bill_ton_dn, bill_ton_esn, bill_ton_bkg, load_ton_cs, ");
				sb.append(
						" shutout_ton_cs, count_unit, total_pack_edo, total_pack_dn, bill_acct_nbr, print_dttm, last_modify_dttm, ");
				sb.append(
						" cntr_nbr, cntr_seq_nbr, cntr_size, cntr_cat, bill_ind from gb_charge_event_log where bill_ind='N' and ref_ind='ED' and mvmt in ('LL','00') and edo_asn_nbr != dn_nbr and dn_nbr like 'D%' and edo_asn_nbr=:refNo");

			} else if (refInd.trim().equals("ED1DN")) {
				sb.append(
						"select disc_vv_cd, load_vv_cd, vv_ind, business_type, scheme_cd, tariff_main_cat_cd, tariff_sub_cat_cd, mvmt, ");
				sb.append(
						" type, cargo_type, local_leg, disc_gateway, bl_nbr, edo_asn_nbr, bk_ref_nbr, esn_asn_nbr, dn_nbr, ua_nbr,  ");
				sb.append(
						"  bill_ton_bl, bill_ton_edo, bill_ton_dn, bill_ton_esn, bill_ton_bkg, load_ton_cs, shutout_ton_cs, count_unit, ");
				sb.append(
						" total_pack_edo, total_pack_dn, bill_acct_nbr, print_dttm, last_modify_dttm, cntr_nbr, cntr_seq_nbr, cntr_size, cntr_cat, ");
				sb.append(
						"   bill_ind from gb_charge_event_log where ref_ind='ED' and mvmt in ('LL','00') and edo_asn_nbr != dn_nbr and dn_nbr like 'D%' and edo_asn_nbr=:refNo");

			} else if (refInd.trim().equals("SU")) {
				sb.append(
						"select disc_vv_cd, load_vv_cd, vv_ind, business_type, scheme_cd, tariff_main_cat_cd, tariff_sub_cat_cd, mvmt, ");
				sb.append(
						" type, cargo_type, local_leg, disc_gateway, bl_nbr, edo_asn_nbr, bk_ref_nbr, esn_asn_nbr, dn_nbr, ua_nbr, bill_ton_bl, bill_ton_edo, ");
				sb.append(
						"  bill_ton_dn, bill_ton_esn, bill_ton_bkg, load_ton_cs, shutout_ton_cs, count_unit, total_pack_edo, total_pack_dn, ");
				sb.append(
						"  bill_acct_nbr, print_dttm, last_modify_dttm, cntr_nbr, cntr_seq_nbr, cntr_size, cntr_cat, bill_ind from gb_charge_event_log ");
				sb.append("  where ref_ind='SU' and cntr_seq_nbr=:refNo ");
				sb.append(" ");

			} else {
				throw new Exception(ConstantUtil.ErrorMsg_RefID_Type);
			}

			paramMap.put("refNo", refNo);
			log.info("SQL: " + sb.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
				generalEventLogValueObject.setDiscVvCd(CommonUtility.deNull(rs.getString(1)));
				generalEventLogValueObject.setLoadVvCd(CommonUtility.deNull(rs.getString(2)));
				generalEventLogValueObject.setVvInd(CommonUtility.deNull(rs.getString(3)));
				generalEventLogValueObject.setBusinessType(CommonUtility.deNull(rs.getString(4)));
				generalEventLogValueObject.setSchemeCd(CommonUtility.deNull(rs.getString(5)));
				generalEventLogValueObject.setTariffMainCatCd(CommonUtility.deNull(rs.getString(6)));
				generalEventLogValueObject.setTariffSubCatCd(CommonUtility.deNull(rs.getString(7)));
				generalEventLogValueObject.setMvmt(CommonUtility.deNull(rs.getString(8)));
				generalEventLogValueObject.setType(CommonUtility.deNull(rs.getString(9)));
				generalEventLogValueObject.setCargoType(CommonUtility.deNull(rs.getString(10)));
				generalEventLogValueObject.setLocalLeg(CommonUtility.deNull(rs.getString(11)));
				generalEventLogValueObject.setDiscGateway(CommonUtility.deNull(rs.getString(12)));
				generalEventLogValueObject.setBlNbr(CommonUtility.deNull(rs.getString(13)));
				generalEventLogValueObject.setEdoAsnNbr(CommonUtility.deNull(rs.getString(14)));
				generalEventLogValueObject.setBkRefNbr(CommonUtility.deNull(rs.getString(15)));
				generalEventLogValueObject.setEsnAsnNbr(CommonUtility.deNull(rs.getString(16)));
				generalEventLogValueObject.setDnNbr(CommonUtility.deNull(rs.getString(17)));
				generalEventLogValueObject.setUaNbr(CommonUtility.deNull(rs.getString(18)));
				generalEventLogValueObject.setBillTonBl(rs.getDouble(19));
				generalEventLogValueObject.setBillTonEdo(rs.getDouble(20));
				generalEventLogValueObject.setBillTonDn(rs.getDouble(21));
				generalEventLogValueObject.setBillTonEsn(rs.getDouble(22));
				generalEventLogValueObject.setBillTonBkg(rs.getDouble(23));
				generalEventLogValueObject.setLoadTonCs(rs.getDouble(24));
				generalEventLogValueObject.setShutoutTonCs(rs.getDouble(25));
				generalEventLogValueObject.setCountUnit(rs.getInt(26));
				generalEventLogValueObject.setTotalPackEdo(rs.getInt(27));
				generalEventLogValueObject.setTotalPackDn(rs.getInt(28));
				generalEventLogValueObject.setBillAcctNbr(CommonUtility.deNull(rs.getString(29)));
				generalEventLogValueObject.setPrintDttm(rs.getTimestamp(30));
				generalEventLogValueObject.setLastModifyDttm(rs.getTimestamp(31));
				generalEventLogValueObject.setCntrNbr(CommonUtility.deNull(rs.getString(32)));
				generalEventLogValueObject.setCntrSeqNbr(rs.getInt(33));
				generalEventLogValueObject.setCntrSize(CommonUtility.deNull(rs.getString(34)));
				generalEventLogValueObject.setCntrCat(CommonUtility.deNull(rs.getString(35)));
				generalEventLogValueObject.setBillInd(CommonUtility.deNull(rs.getString(36)));
				gbEventLogArrayList.add(generalEventLogValueObject);
			}

		} catch (BusinessException ex) {
			log.info("Exception getGBEventLog : ", ex);
			throw new BusinessException(ex.getMessage());
		} catch (Exception ex) {
			log.info("Exception getGBEventLog : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getGBEventLog()  DAO  END");
		}
		GeneralEventLogValueObject gbEventLogList[] = (GeneralEventLogValueObject[]) gbEventLogArrayList
				.toArray(new GeneralEventLogValueObject[0]);
		log.info("gbEventLogList: " + gbEventLogList.toString());
		return gbEventLogList;
	}

	// ejb.sessionBeans.cab.gbEventLog-->GBEventLogEJB
	// method: executeBillableWarehouseCharges()
	/**
	 * Parse the value objects passed in and insert/update them into relevant event
	 * log after which the calculation of charges is executed based on the type of
	 * charges logged and return the charges to the front-end for printing
	 *
	 * @param VesselTxnEventLogValueObject
	 * @param ArrayList
	 * @param String
	 * @return ArrayList
	 * @exception NamingException
	 * @exception SQLException
	 * @exception ProcessChargeException
	 * @exception Exception
	 */
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public List<String> executeBillableWarehouseCharges(VesselTxnEventLogValueObject vesselTxnEventLogValueObject,
			List<GeneralEventLogValueObject> chargeEventLogList, String refInd) throws BusinessException {
		List<String> chargeableBillList = new ArrayList<String>();

		GeneralEventLogValueObject generalEventLogValueObject = null;

		boolean txnSuccess = true;

		try {
			log.info("START: executeBillableWarehouseCharges  DAO  Start vesselTxnEventLogValueObject:" + CommonUtility.deNull(vesselTxnEventLogValueObject.toString())
			+ "chargeEventLogList:" + CommonUtility.deNull(chargeEventLogList.toString()) + "refInd:" + CommonUtility.deNull(refInd));
			// initialization of fields;
			txnSuccess = true;

			// inserts the chargeable events into the event log
			try {
				logGBEvent(vesselTxnEventLogValueObject, chargeEventLogList, refInd);
			} catch (Exception e) {
				// context.setRollbackOnly();
				txnSuccess = false;
				throw new BusinessException("Logging into event log failed.");
			}

			// updates gb_edo with the wharfage and service charges indexes

//			            log.info("[CAB]","WarehouseCharges retrieving generalEventLogValueObject, size="+chargeEventLogList.size());
			generalEventLogValueObject = (GeneralEventLogValueObject) chargeEventLogList.get(0);

			if (generalEventLogValueObject != null) {
//			            	log.info("[CAB]","WarehouseCharges edo = "+generalEventLogValueObject.getEdoAsnNbr());
//			            	log.info("[CAB]","WarehouseCharges userId = "+generalEventLogValueObject.getLastModifyUserId());
//			            	log.info("[CAB]","WarehouseCharges wharfInd = "+vesselTxnEventLogValueObject.getBillWharfInd());
//			            			log.info("[CAB]","WarehouseCharges svcChargeInd = "+vesselTxnEventLogValueObject.getBillSvcChargeInd());

				if (txnSuccess) {
					// log.info("[CAB]","WarehouseCharges calling
					// updateWarehouseEDOInd method...");
					GBMSTriggerIndRepo.updateWarehouseEDOInd(generalEventLogValueObject.getEdoAsnNbr(),
							generalEventLogValueObject.getLastModifyUserId(),
							vesselTxnEventLogValueObject.getBillWharfInd(),
							vesselTxnEventLogValueObject.getBillSvcChargeInd());
				}

			} else {
				// log.info("[CAB]","WarehouseCharges generalEventLogValueObject
				// = "+generalEventLogValueObject);
			}
			// log.info("[CAB]","WarehouseCharges after updateWarehouseEDOInd
			// method...");

		} catch (BusinessException e) {
			log.info("Exception executeBillableWarehouseCharges :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception executeBillableWarehouseCharges :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: executeBillableWarehouseCharges  DAO  END  chargeableBillList: " + chargeableBillList.toString());
		}
		return chargeableBillList;
	}

	// ejb.sessionBeans.cab.gbEventLog-->GBEventLogEJB
	// method: logGBEvent()
	/**
	 * This method logs the GB Charges into the gb_charge_event_log &
	 * gb_vessel_txn_event_log for bill processing
	 *
	 * @param VesselTxnEventLogValueObject
	 * @param ArrayList
	 * @throws Exception
	 *
	 */
	public void logGBEvent(VesselTxnEventLogValueObject vesselTxnEventLogValueObject,
			List<GeneralEventLogValueObject> chargeEventLogList, String ind) throws Exception {
		log.info("START logGBEvent :: vesselTxnEventLogValueObject : " + CommonUtility.deNull(vesselTxnEventLogValueObject.toString())
				+ " chargeEventLogList: " + CommonUtility.deNull(chargeEventLogList.toString()) + " ind: " + CommonUtility.deNull(ind));
		log.info("bef log Vessel...");
		logVesselTxnEvent(vesselTxnEventLogValueObject);
		log.info("aft log vessel ...");
		logChargeEvent(chargeEventLogList, ind);
		log.info("aft log charge ...");
		log.info("END logGBEvent");
	}

	// ejb.sessionBeans.cab.gbEventLog-->GBEventLogEJB
	// method: logVesselTxnEvent()
	/**
	 * This method log the create a new record in the gb_vessel_txn_event_log for
	 * each vessel call for each day. If existing vessel call exist in the
	 * gb_vessel_txn_event_log, the method will just update the relevant indicators
	 *
	 * @param VesselTxnEventLogValueObject
	 * @throws Exception
	 * @see VesselTxnEventLogValueObject
	 */
	@Transactional(rollbackFor = BusinessException.class)
	public void logVesselTxnEvent(VesselTxnEventLogValueObject vesselTxnEventLogValueObject) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder addVslTxnEventLog = new StringBuilder();
		StringBuilder updateVslTxnEventLog = new StringBuilder();
		int ctr = 0;

		addVslTxnEventLog.append(
				" insert into gb_vessel_txn_event_log (vv_cd, txn_dttm, bill_wharf_ind, bill_svc_charge_ind, bill_store_ind, ");
		addVslTxnEventLog.append(
				" bill_process_ind, last_modify_user_id, last_modify_dttm, bill_bulk_ind, bill_bulk_process_ind, bill_standby_ind, ");
		addVslTxnEventLog.append(
				" bill_standby_process_ind, bill_stuff_ind, bill_cntr_svc_ind, bill_cntr_store_ind ,bill_cntr_svc_process_ind, bill_cntr_store_process_ind) values ( ");
		addVslTxnEventLog.append(
				" :vvCd,:txnDttm,:billWharfInd,:billSvcChargeInd,:billStoreInd,'N',:lastModifyUserId,:lastModifyDttm,:billBulkInd, ");
		addVslTxnEventLog
				.append(" :billBulkProcessInd,:billStandbyInd,:billStandbyProcessInd,:billStuffInd,:billCntrSvcInd, ");
		addVslTxnEventLog.append(" :billCntrStoreInd,:billCntrSvcProcessInd,:billCntrStoreProcessInd) ");

		updateVslTxnEventLog.append(
				" update gb_vessel_txn_event_log set bill_wharf_ind = :billWharfInd, bill_svc_charge_ind =:billSvcChargeInd, ");
		updateVslTxnEventLog.append(
				" bill_store_ind =:billStoreInd, last_modify_user_id=:lastModifyUserId, last_modify_dttm =:lastModifyDttm, bill_bulk_ind =:billBulkInd, ");
		updateVslTxnEventLog.append(
				" bill_standby_ind =:billStandbyInd, bill_stuff_ind =:billStuffInd, bill_cntr_svc_ind =:billCntrSvcInd, bill_cntr_store_ind =:billCntrStoreInd ");
		updateVslTxnEventLog.append(" where vv_cd =:vvCd and txn_dttm =:txnDttm and bill_process_ind= 'N' ");

		// log.info("inside logVesselTxnEvent...");
		try {
			log.info("START: logVesselTxnEvent  DAO  Start Obj vesselTxnEventLogValueObject:"
					+ CommonUtility.deNull(vesselTxnEventLogValueObject.toString()));

			if (vesselTxnEventLogValueObject == null || vesselTxnEventLogValueObject.getClass() == null) {
				log.info("vesselTxnEventLogValueObject received is null!!!");
			} else {
				if (vesselTxnEventLogValueObject.getVvCd() == null)
					log.info("vesselTxnEventLogValueObject.getVvCd() received is null!!!");

				if (vesselTxnEventLogValueObject.getTxnDttm() == null)
					log.info("vesselTxnEventLogValueObject.getTxnDttm() received is null!!!");

				if (vesselTxnEventLogValueObject.getBillWharfInd() == null)

					log.info("vesselTxnEventLogValueObject.getBillWharfInd() received is null!!!");

				if (vesselTxnEventLogValueObject.getBillSvcChargeInd() == null)
					log.info("vesselTxnEventLogValueObject.getBillSvcChargeInd() received is null!!!");

				if (vesselTxnEventLogValueObject.getBillStoreInd() == null)
					log.info("vesselTxnEventLogValueObject.getBillStoreInd() received is null!!!");

				if (vesselTxnEventLogValueObject.getLastModifyUserId() == null)
					log.info("vesselTxnEventLogValueObject.getLastModifyUserId() received is null!!!");

				if (vesselTxnEventLogValueObject.getLastModifyDttm() == null)
					log.info("vesselTxnEventLogValueObject.getLastModifyDttm() received is null!!!");
			}

			VesselTxnEventLogValueObject vslTxnEventLogValueObject = getGBVesselTxnEventLog(
					vesselTxnEventLogValueObject.getVvCd(), vesselTxnEventLogValueObject.getTxnDttm());
			// log.info("vslTxnEventLogValueObject: " +
			// vslTxnEventLogValueObject.getBillBulkInd() + " vesselTxnEventLogValueObject:
			// " + vesselTxnEventLogValueObject.getBillBulkInd());
			if (vslTxnEventLogValueObject == null) {
				log.info("new vessel txn ");
				// Add new GB Vessel Transaction Event Log for the day

				paramMap.put("vvCd", vesselTxnEventLogValueObject.getVvCd());
				paramMap.put("txnDttm", vesselTxnEventLogValueObject.getTxnDttm());
				paramMap.put("billWharfInd", vesselTxnEventLogValueObject.getBillWharfInd());
				paramMap.put("billSvcChargeInd", vesselTxnEventLogValueObject.getBillSvcChargeInd());
				paramMap.put("billStoreInd", vesselTxnEventLogValueObject.getBillStoreInd());
				paramMap.put("lastModifyUserId", vesselTxnEventLogValueObject.getLastModifyUserId());
				paramMap.put("lastModifyDttm", vesselTxnEventLogValueObject.getLastModifyDttm());
				// added by Irene Tan on 15 Apr 2003 : to add additional fields for
				// gb_vessel_txn_event_log table
				paramMap.put("billBulkInd", vesselTxnEventLogValueObject.getBillBulkInd());
				paramMap.put("billBulkProcessInd", vesselTxnEventLogValueObject.getBillBulkProcessInd());
				paramMap.put("billStandbyInd", vesselTxnEventLogValueObject.getBillStandbyInd());
				paramMap.put("billStandbyProcessInd", vesselTxnEventLogValueObject.getBillStandbyProcessInd());
				// end added by Irene Tan on 15 Apr 2003
				// changed by Irene Tan on 03 Jun 2003 : to add additional fields for
				// stuffing/unstuffing
				paramMap.put("billStuffInd", vesselTxnEventLogValueObject.getBillStuffInd());
				// end changed by Irene Tan on 03 June 2003
				// changed by Alicia on 27 Nov 2003 : to add additional fields for containerised
				// cargo
				paramMap.put("billCntrSvcInd", vesselTxnEventLogValueObject.getBillCntrSvcInd());
				paramMap.put("billCntrStoreInd", vesselTxnEventLogValueObject.getBillCntrStoreInd());
				paramMap.put("billCntrSvcProcessInd", vesselTxnEventLogValueObject.getBillCntrSvcProcessInd());
				paramMap.put("billCntrStoreProcessInd", vesselTxnEventLogValueObject.getBillCntrStoreProcessInd());
				// end changed by Alicia on 27 Nov 2003
				log.info(" *** logVesselTxnEvent SQL *****" + addVslTxnEventLog.toString() + " paramMap: " + paramMap);
				ctr = namedParameterJdbcTemplate.update(addVslTxnEventLog.toString(), paramMap);

			} else {
				log.info("existing vsl txn");
				// Update the existing GB Vessel Transaction Event Log for the day

				if (vesselTxnEventLogValueObject.getBillWharfInd().trim().equals("Y")) {
					paramMap.put("billWharfInd", vesselTxnEventLogValueObject.getBillWharfInd());
				} else {
					paramMap.put("billWharfInd", vslTxnEventLogValueObject.getBillWharfInd());
				}

				if (vesselTxnEventLogValueObject.getBillSvcChargeInd().trim().equals("Y")) {
					paramMap.put("billSvcChargeInd", vesselTxnEventLogValueObject.getBillSvcChargeInd());
				} else {
					paramMap.put("billSvcChargeInd", vslTxnEventLogValueObject.getBillSvcChargeInd());
				}
				if (vesselTxnEventLogValueObject.getBillStoreInd().trim().equals("Y")) {
					paramMap.put("billStoreInd", vesselTxnEventLogValueObject.getBillStoreInd());
				} else {
					paramMap.put("billStoreInd", vslTxnEventLogValueObject.getBillStoreInd());
				}
				paramMap.put("lastModifyUserId", vesselTxnEventLogValueObject.getLastModifyUserId());
				paramMap.put("lastModifyDttm", vesselTxnEventLogValueObject.getLastModifyDttm());
				// added by Irene Tan on 15 Apr 2003 : to add additional fields for
				// gb_vessel_txn_event_log table
				// pstmt.setString(6, vesselTxnEventLogValueObject.getVvCd());
				// pstmt.setTimestamp(7, vslTxnEventLogValueObject.getTxnDttm());
				if (vesselTxnEventLogValueObject.getBillBulkInd().trim().equals("Y")) {
					paramMap.put("billBulkInd", vesselTxnEventLogValueObject.getBillBulkInd());
				} else {
					paramMap.put("billBulkInd", vslTxnEventLogValueObject.getBillBulkInd());
				}
				if (vesselTxnEventLogValueObject.getBillStandbyInd().trim().equals("Y")) {
					paramMap.put("billStandbyInd", vesselTxnEventLogValueObject.getBillStandbyInd());
				} else {
					paramMap.put("billStandbyInd", vslTxnEventLogValueObject.getBillStandbyInd());
				}
				// changed by Irene Tan on 03 Jun 2003 : to add additional fields for
				// stuffing/unstuffing
				// pstmt.setString(8, vesselTxnEventLogValueObject.getVvCd());
				// pstmt.setTimestamp(9, vslTxnEventLogValueObject.getTxnDttm());
				if (vesselTxnEventLogValueObject.getBillStuffInd().trim().equals("Y")) {
					paramMap.put("billStuffInd", vesselTxnEventLogValueObject.getBillStuffInd());
				} else {
					paramMap.put("billStuffInd", vslTxnEventLogValueObject.getBillStuffInd());
				}
				// change by Alicia on 27 Nov 2003
				if (vesselTxnEventLogValueObject.getBillCntrSvcInd().trim().equals("Y")) {
					paramMap.put("billCntrSvcInd", vesselTxnEventLogValueObject.getBillCntrSvcInd());
				} else {
					paramMap.put("billCntrSvcInd", vslTxnEventLogValueObject.getBillCntrSvcInd());
				}

				if (vesselTxnEventLogValueObject.getBillCntrStoreInd().trim().equals("Y")) {
					paramMap.put("billCntrStoreInd", vesselTxnEventLogValueObject.getBillCntrStoreInd());
				} else {
					paramMap.put("billCntrStoreInd", vslTxnEventLogValueObject.getBillCntrStoreInd());
				}
				paramMap.put("vvCd", vesselTxnEventLogValueObject.getVvCd());
				paramMap.put("txnDttm", vslTxnEventLogValueObject.getTxnDttm());

				// end change by Alicia on 27 Nov 2003
				// pstmt.setString(9, vesselTxnEventLogValueObject.getVvCd());
				// pstmt.setTimestamp(10, vslTxnEventLogValueObject.getTxnDttm());
				// end added by Irene Tan on 15 Apr 2003
				// end changed by Irene Tan on 03 June 2003
				log.info(" *** logVesselTxnEvent SQL *****" + updateVslTxnEventLog.toString() + " paramMap: " + paramMap);
				ctr = namedParameterJdbcTemplate.update(updateVslTxnEventLog.toString(), paramMap);
			}

			log.info("vsl ctr: " + ctr);
			if (ctr <= 0) {
				// context.setRollbackOnly();
				throw new BusinessException(ConstantUtil.ErrorMsg_Cannot_Cancel_GB_Vessel);
			}

		} catch (NullPointerException e) {
			log.info("Exception logVesselTxnEvent :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception logVesselTxnEvent :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception logVesselTxnEvent :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: logVesselTxnEvent  DAO  END");
		}
	}

	// ejb.sessionBeans.cab.gbEventLog-->GBEventLogEJB
	// method: logChargeEvent()
	/**
	 * This method log the Wharfage, Service Charge & Store Rent charges into the
	 * gb_charge_event_log for bill processing
	 *
	 * @param ArrayList
	 * @param String
	 * @exception SQLException
	 * @exception Exception
	 */
	@Transactional(rollbackFor = BusinessException.class)
	public void logChargeEvent(List<GeneralEventLogValueObject> chargeEventLogList, String ind) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder();

		try {
			log.info("START: logChargeEvent  DAO  Start Obj chargeEventLogArrayList:" + CommonUtility.deNull(chargeEventLogList.toString()) + "ind:"
					+ CommonUtility.deNull(ind));

			sql.append(" ");
			sql.append(" insert into gb_charge_event_log (disc_vv_cd, load_vv_cd, vv_ind, ");
			sql.append(" business_type, scheme_cd, tariff_main_cat_cd, tariff_sub_cat_cd, mvmt, ");
			sql.append(" type, cargo_type, local_leg, disc_gateway, ref_ind, bl_nbr, edo_asn_nbr, ");
			sql.append(" bk_ref_nbr, esn_asn_nbr, dn_nbr, ua_nbr, bill_ton_bl, bill_ton_edo, bill_ton_dn, ");
			sql.append(" bill_ton_esn, bill_ton_bkg, load_ton_cs, shutout_ton_cs, count_unit, ");
			sql.append(" total_pack_edo, total_pack_dn, bill_acct_nbr, print_dttm, bill_ind, ");
			sql.append(" last_modify_user_id, last_modify_dttm, cntr_cat, cntr_size, cntr_nbr, ");
			sql.append(" cntr_seq_nbr, ss_ref_nbr) values (:discVvCd,:loadVvCd,:vvInd,:businessType, ");
			sql.append(" :schemeCd,:tariffMainCatCd,:tariffSubCatCd,:mvmt,:type,:cargoType,:localLeg,:discGateway, ");
			sql.append(" :refInd,:blNbr,:edoAsnNbr,:bkRefNbr,:esnAsnNbr,:dnNbr,:uaNbr,:billTonBl, ");
			sql.append(
					" :billTonEdo,:billTonDn,:billTonEsn,:billTonBkg,:loadTonCs,:shutoutTonCs,:countUnit,:totalPackEdo, ");
			sql.append(
					" :totalPackDn,:billAcctNbr,:printDttm,:billInd,:lastModifyUserId,:lastModifyDttm,:cntrCat,:cntrSize, ");
			sql.append(" :cntrNbr,:cntrSeqNbr,:ssRefNbr) ");

			for (int i = 0; i < chargeEventLogList.size(); i++) {
				GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
				generalEventLogValueObject = (GeneralEventLogValueObject) chargeEventLogList.get(i);
				// pstmt.setString(1, generalEventLogValueObject.getVvCd());
				paramMap.put("discVvCd", generalEventLogValueObject.getDiscVvCd());
				paramMap.put("loadVvCd", generalEventLogValueObject.getLoadVvCd());
				paramMap.put("vvInd", generalEventLogValueObject.getVvInd());
				paramMap.put("businessType", generalEventLogValueObject.getBusinessType());
				String vv_cd = (generalEventLogValueObject.getVvInd().equalsIgnoreCase("D"))
						? generalEventLogValueObject.getDiscVvCd()
						: generalEventLogValueObject.getLoadVvCd();
				log.info("Writing from  GBEventLog.logChargeEvent" + vv_cd);
				if (isCombiScheme(vv_cd)) {
					paramMap.put("schemeCd", getSubSchemeName(vv_cd));
				} else {
					paramMap.put("schemeCd", generalEventLogValueObject.getSchemeCd());
				}
				paramMap.put("tariffMainCatCd", generalEventLogValueObject.getTariffMainCatCd());
				// log.info("add main cat: "
				// +generalEventLogValueObject.getTariffMainCatCd());
				paramMap.put("tariffSubCatCd", generalEventLogValueObject.getTariffSubCatCd());
				paramMap.put("mvmt", generalEventLogValueObject.getMvmt());
				paramMap.put("type", generalEventLogValueObject.getType());
				// log.info("add type: "+generalEventLogValueObject.getType());
				paramMap.put("cargoType", generalEventLogValueObject.getCargoType());
				paramMap.put("localLeg", generalEventLogValueObject.getLocalLeg());
				paramMap.put("discGateway", generalEventLogValueObject.getDiscGateway());
				// Change by Irene Tan on 20 Dec 2002
				// paramMap.put(13, ind);
				paramMap.put("refInd", generalEventLogValueObject.getRefInd());
				// End Change by Irene Tan
				paramMap.put("blNbr", generalEventLogValueObject.getBlNbr());
				paramMap.put("edoAsnNbr", generalEventLogValueObject.getEdoAsnNbr());
				paramMap.put("bkRefNbr", generalEventLogValueObject.getBkRefNbr());
				paramMap.put("esnAsnNbr", generalEventLogValueObject.getEsnAsnNbr());
				paramMap.put("dnNbr", generalEventLogValueObject.getDnNbr());
				paramMap.put("uaNbr", generalEventLogValueObject.getUaNbr());
				paramMap.put("billTonBl", generalEventLogValueObject.getBillTonBl());
				paramMap.put("billTonEdo", generalEventLogValueObject.getBillTonEdo());
				// log.info("billTonEdo: " +
				// generalEventLogValueObject.getBillTonEdo());
				paramMap.put("billTonDn", generalEventLogValueObject.getBillTonDn());
				// log.info("billTonEdo: " +
				// generalEventLogValueObject.getBillTonDn());
				paramMap.put("billTonEsn", generalEventLogValueObject.getBillTonEsn());
				paramMap.put("billTonBkg", generalEventLogValueObject.getBillTonBkg());
				paramMap.put("loadTonCs", generalEventLogValueObject.getLoadTonCs());
				paramMap.put("shutoutTonCs", generalEventLogValueObject.getShutoutTonCs());
				paramMap.put("countUnit", generalEventLogValueObject.getCountUnit());
				paramMap.put("totalPackEdo", generalEventLogValueObject.getTotalPackEdo());
				// log.info("billTonEdo: " +
				// generalEventLogValueObject.getTotalPackEdo());
				paramMap.put("totalPackDn", generalEventLogValueObject.getTotalPackDn());
				// log.info("billTonEdo: " +
				// generalEventLogValueObject.getTotalPackDn());
				paramMap.put("billAcctNbr", generalEventLogValueObject.getBillAcctNbr());
				paramMap.put("printDttm", generalEventLogValueObject.getPrintDttm());
				paramMap.put("billInd", generalEventLogValueObject.getBillInd());
				paramMap.put("lastModifyUserId", generalEventLogValueObject.getLastModifyUserId());
				paramMap.put("lastModifyDttm", generalEventLogValueObject.getLastModifyDttm());
				// changed by Irene Tan on 03 Jun 2003 : to add additional fields for
				// stuffing/unstuffing
				paramMap.put("cntrCat", generalEventLogValueObject.getCntrCat());
				paramMap.put("cntrSize", generalEventLogValueObject.getCntrSize());
				paramMap.put("cntrNbr", generalEventLogValueObject.getCntrNbr());
				paramMap.put("cntrSeqNbr", generalEventLogValueObject.getCntrSeqNbr());
				// end changed by Irene Tan on 03 Jun 2003

				// <cfg Gb bill display enhancements>
				log.info("cfg ssRefNbr = " + generalEventLogValueObject.getSsRefNbr());
				paramMap.put("ssRefNbr", generalEventLogValueObject.getSsRefNbr());
				// <cfg Gb bill display enhancements/>

				log.info(" *** logChargeEvent SQL *****" + sql.toString() + " paramMap: " + paramMap);
				int ctr = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
				log.info("ctr: " + ctr);
				if (ctr <= 0) {
					throw new BusinessException(ConstantUtil.ErrorMsg_Cannot_Cancel_GB_Charge_Event);
				}
			}

		} catch (NullPointerException e) {
			log.info("Exception logChargeEvent :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception logChargeEvent :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception logChargeEvent :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: logChargeEvent  DAO  END");
		}
	}

	// ejb.sessionBeans.cab.gbEventLog-->GBEventLogEJB
	// method: getGBVesselTxnEventLog()
	/**
	 * This method retrieves the vessel call transaction for the day
	 *
	 * @param String    Vessel Call Id
	 * @param Timestamp txnDate
	 * @return VesselTxnEventLogValueObject
	 * @throws BusinessException
	 */
	public VesselTxnEventLogValueObject getGBVesselTxnEventLog(String vvCd, Timestamp txnDate)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder();

		VesselTxnEventLogValueObject vslTxnEventLogValueObject = null;

		try {
			log.info("START: getGBVesselTxnEventLog  DAO  Start Obj vvCd:" + CommonUtility.deNull(vvCd) + "txnDate:" + CommonUtility.deNull(txnDate.toString()));

			sql.append(" select txn_dttm, bill_wharf_ind, bill_svc_charge_ind, bill_store_ind, ");
			sql.append(" bill_bulk_ind, bill_bulk_process_ind, bill_standby_ind, bill_standby_process_ind, ");
			sql.append(" bill_stuff_ind , bill_cntr_svc_ind, bill_cntr_store_ind ,bill_cntr_svc_process_ind, ");
			sql.append(" bill_cntr_store_process_ind from gb_vessel_txn_event_log where ");
			sql.append(" vv_cd=:vvCd and to_char(txn_dttm, 'yyyymmdd')=to_char(:txnDate, 'yyyymmdd') ");
			sql.append(" and bill_process_ind='N' ");

			paramMap.put("vvCd", vvCd);
			paramMap.put("txnDate", txnDate);
			log.info(" *** getGBVesselTxnEventLog SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				vslTxnEventLogValueObject = new VesselTxnEventLogValueObject();
				vslTxnEventLogValueObject.setTxnDttm(rs.getTimestamp("txn_dttm"));
				vslTxnEventLogValueObject.setBillWharfInd(CommonUtility.deNull(rs.getString("bill_wharf_ind")));
				vslTxnEventLogValueObject
						.setBillSvcChargeInd(CommonUtility.deNull(rs.getString("bill_svc_charge_ind")));
				vslTxnEventLogValueObject.setBillStoreInd(CommonUtility.deNull(rs.getString("bill_store_ind")));
				// added by Irene Tan on 15 Apr 2003 : to add additional fields for
				// gb_vessel_txn_event_log table
				vslTxnEventLogValueObject.setBillBulkInd(CommonUtility.deNull(rs.getString("bill_bulk_ind")));
				vslTxnEventLogValueObject
						.setBillBulkProcessInd(CommonUtility.deNull(rs.getString("bill_bulk_process_ind")));
				vslTxnEventLogValueObject.setBillStandbyInd(CommonUtility.deNull(rs.getString("bill_standby_ind")));
				vslTxnEventLogValueObject
						.setBillStandbyProcessInd(CommonUtility.deNull(rs.getString("bill_standby_process_ind")));
				// end added by Irene Tan on 15 Apr 2003
				// changed by Irene Tan on 03 Jun 2003 : to add additional fields for
				// stuffing/unstuffing
				vslTxnEventLogValueObject.setBillStuffInd(CommonUtility.deNull(rs.getString("bill_stuff_ind")));
				// end changed by Irene Tan on 03 Jun 2003
				// changed by Alicia on 28 Nov 2003
				vslTxnEventLogValueObject.setBillCntrSvcInd(CommonUtility.deNull(rs.getString("bill_cntr_svc_ind")));
				vslTxnEventLogValueObject
						.setBillCntrStoreInd(CommonUtility.deNull(rs.getString("bill_cntr_store_ind")));
				vslTxnEventLogValueObject
						.setBillCntrSvcProcessInd(CommonUtility.deNull(rs.getString("bill_cntr_svc_process_ind")));
				vslTxnEventLogValueObject
						.setBillCntrStoreProcessInd(CommonUtility.deNull(rs.getString("bill_cntr_store_process_ind")));
				// end changed by Alicia
			}
			log.info("END: getGBVesselTxnEventLog  DAO  END  vslTxnEventLogValueObject: " + (vslTxnEventLogValueObject == null ? null : vslTxnEventLogValueObject.toString()));
		} catch (NullPointerException e) {
			log.info("Exception getGBVesselTxnEventLog :", e);
		} catch (Exception e) {
			log.info("Exception getGBVesselTxnEventLog :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getGBVesselTxnEventLog  DAO  END");
		}
		return vslTxnEventLogValueObject;
	}

	// ejb.sessionBeans.cab.gbEventLog-->GBEventLogEJB
	// method: isCombiScheme()
	private boolean isCombiScheme(String strvvcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder();
		String gcOperations = "";
		boolean combiScheme = false;

		try {
			log.info("START: isCombiScheme  DAO  Start Obj strvvcd:" + CommonUtility.deNull(strvvcd));
			sql.append(" SELECT VESCALL.COMBI_GC_OPS_IND FROM VESSEL_CALL VESCALL WHERE VESCALL.VV_CD=:strvvcd ");

			paramMap.put("strvvcd", strvvcd);
			log.info(" *** isCombiScheme SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));
			}

			if (gcOperations.equalsIgnoreCase("Y")) {
				combiScheme = true;
			} else {
				combiScheme = false;
			}

		} catch (NullPointerException e) {
			log.info("Exception isCombiScheme :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isCombiScheme :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isCombiScheme  DAO  END  combiScheme: " + combiScheme);
		}
		return combiScheme;
	}

	// ejb.sessionBeans.cab.gbEventLog-->GBEventLogEJB
	// method: getSubSchemeName()
	private String getSubSchemeName(String strvvcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder();
		String subScheme = "";

		try {
			log.info("START: getSubSchemeName  DAO  Start Obj strvvcd:" + CommonUtility.deNull(strvvcd));
			sql.append(" SELECT VESCALL.COMBI_GC_SCHEME, VESCALL.COMBI_GC_OPS_IND FROM VESSEL_CALL ");
			sql.append(" VESCALL WHERE VESCALL.VV_CD=:strvvcd ");

			paramMap.put("strvvcd", strvvcd);
			log.info(" *** isCombiScheme SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				subScheme = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
			}

		} catch (NullPointerException e) {
			log.info("Exception getSubSchemeName :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSubSchemeName :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSubSchemeName  DAO  END  subScheme: " + subScheme);
		}
		return subScheme;
	}
	// EndRegion GBEventLogJdbcRepository

	// package: ejb.sessionBeans.cab.gbEventLog-->GBEventLogEJB
	// method: updateProcessedGBEventLog()
	/**
	 * This method update those gb_charge_event_log records which have been
	 * processed successfully.
	 *
	 * @param String reference No
	 * @param String reference indicator
	 * @param String tariff main category code
	 * @param String vessel call id
	 * @exception SQLException
	 * @exception NamingException
	 * @exception Exception
	 */
	// public void updateProcessedGBEventLog(String refNo, String refInd, String
	// tariffMainCat) throws SQLException, NamingException, Exception {
	public void updateProcessedGBEventLog(String refNo, String refInd, String tariffMainCat, String vvCd)
			throws SQLException, NamingException, Exception {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		int ctr = 0;
		StringBuilder buffer = new StringBuilder();
		buffer.append(" update gb_charge_event_log set bill_ind='Y' where ref_ind= ");
		// if (refInd.trim().equals("DN")) {
		if (refInd.trim().equals(ProcessChargeConst.REF_IND_DN)) {
			buffer.append("'").append(refInd.trim()).append("' and dn_nbr='").append(refNo.trim())
					.append("' and tariff_main_cat_cd='").append(tariffMainCat.trim()).append("'");
		}
		// else if (refInd.trim().equals("UA")) {
		else if (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) {
			buffer.append("'").append(refInd.trim()).append("' and ua_nbr='").append(refNo.trim())
					.append("' and tariff_main_cat_cd='").append(tariffMainCat.trim()).append("'");
		}
		// else if (refInd.trim().equals("BL")) {
		else if (refInd.trim().equals(ProcessChargeConst.REF_IND_BL)) {
			// buffer.append("'").append(refInd.trim()).append("' and
			// bl_nbr='").append(refNo.trim()).append("' and dn_nbr is null and edo_asn_nbr
			// is null and tariff_main_cat_cd='").append(tariffMainCat.trim()).append("'");
			// buffer.append("'").append(refInd.trim()).append("' and
			// bl_nbr='").append(refNo.trim()).append("' and (dn_nbr is null or dn_nbr='' or
			// dn_nbr=' ') and
			// tariff_main_cat_cd='").append(tariffMainCat.trim()).append("'");
			buffer.append("'").append(refInd.trim()).append("' and bl_nbr='").append(refNo.trim())
					.append("' and (dn_nbr is null or dn_nbr='' or dn_nbr=' ') and tariff_main_cat_cd='")
					.append(tariffMainCat.trim()).append("' and disc_vv_cd='").append(vvCd.trim()).append("'");
		}
		// else if (refInd.trim().equals("BR")) {
		else if (refInd.trim().equals(ProcessChargeConst.REF_IND_BR)) {
			buffer.append("'").append(refInd.trim()).append("' and bk_ref_nbr='").append(refNo.trim()).append(
					"' and (ua_nbr is null or ua_nbr='' or ua_nbr=' ') and (esn_asn_nbr is null or esn_asn_nbr='' or esn_asn_nbr=' ') and tariff_main_cat_cd='")
					.append(tariffMainCat.trim()).append("'");
		}
		// else if (refInd.trim().equals("ES")) {
		else if (refInd.trim().equals(ProcessChargeConst.REF_IND_ESN)) {
			// For Ship Stores & At Close Shipment
			// <cfg amend GB Bill Display Enhancements 03.jul.09>
			// buffer.append("'").append(refInd.trim()).append("' and (bk_ref_nbr is null or
			// bk_ref_nbr='' or bk_ref_nbr=' ') and
			// esn_asn_nbr='").append(refNo.trim()).append("' and
			// tariff_main_cat_cd='").append(tariffMainCat.trim()).append("'");
			buffer.append("'").append(refInd.trim()).append("' and esn_asn_nbr='").append(refNo.trim())
					.append("' and tariff_main_cat_cd='").append(tariffMainCat.trim()).append("'");
		}
		// Added by Irene Tan on 27 Feb 2003 : to update the EDO transactions logged in
		// GBEventLog
		else if (refInd.trim().equals(ProcessChargeConst.REF_IND_EDO)) {
			// buffer.append("'").append(refInd.trim()).append("' and (bl_nbr is null or
			// bl_nbr='' or bl_nbr=' ') and (dn_nbr is null or dn_nbr='' or dn_nbr=' ') and
			// edo_asn_nbr='").append(refNo.trim()).append("' and
			// tariff_main_cat_cd='").append(tariffMainCat.trim()).append("'");
			// modify by CFG on 21 Aug 2007 to cater for warehouse billing automation

			// MCC commenting to also include EDO 1st DN creation merged and added the below
			// condition in else part of isFirstDN flag
			// buffer.append("'").append(refInd.trim()).append("' and (bl_nbr is null or
			// bl_nbr='' or bl_nbr=' ') and (dn_nbr is null or dn_nbr='' or dn_nbr=' ' or
			// dn_nbr ='").append(refNo.trim()).append("') and
			// edo_asn_nbr='").append(refNo.trim()).append("' and
			// tariff_main_cat_cd='").append(tariffMainCat.trim()).append("'");

			boolean isEDOForFirstDN = false;
			// MCC get the correct EDO record for 1st DN creation
			// begin
			List<GeneralEventLogValueObject> edoEventLogList = new ArrayList<GeneralEventLogValueObject>();
			String edoSql = null;
			try {
				log.info("START: updateProcessedGBEventLog  DAO  Start Obj refNo:" + CommonUtility.deNull(refNo) + "refInd" + CommonUtility.deNull(refInd)
						+ "tariffMainCat:" + CommonUtility.deNull(tariffMainCat) + "vvCd:" + CommonUtility.deNull(vvCd));

				edoSql = "select mvmt, edo_asn_nbr, dn_nbr, bill_ind from gb_charge_event_log where  ref_ind='ED' and mvmt in ('LL','00') and edo_asn_nbr != dn_nbr and bill_ind!='D' and dn_nbr like 'D%' and edo_asn_nbr=:refNo";
				// MCC Add for ED and look for only 1st DN creation events created
				if (refInd.trim().equals("ED")) {

					log.info(" *** isTSLocalDN SQL *****" + edoSql);
				}
				paramMap.put("refNo", refNo);
				log.info("paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(edoSql.toString(), paramMap);
				while (rs.next()) {
					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
					generalEventLogValueObject.setMvmt(CommonUtility.deNull(rs.getString("mvmt")));
					generalEventLogValueObject.setEdoAsnNbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
					generalEventLogValueObject.setDnNbr(CommonUtility.deNull(rs.getString("dn_nbr")));

					edoEventLogList.add(generalEventLogValueObject);
				}
			} catch (Exception se) {

				log.info("[SQLException] GBEventLogEJB - updateProcessedGBEventLog() -> EDO excute Query: " + edoSql);
				log.info("[SQLException] GBEventLogEJB - updateProcessedGBEventLog(): -> EDO excute Query:" + se);
				throw new SQLException(
						"[SQLException] GBEventLogEJB - updateProcessedGBEventLog() -> EDO excute Query:" + se);
			}
			// end

			for (int i = 0; i < edoEventLogList.size(); i++) {
				GeneralEventLogValueObject edoEventLog = (GeneralEventLogValueObject) edoEventLogList.get(i);
				if (edoEventLog != null && edoEventLog.getEdoAsnNbr() != edoEventLog.getDnNbr()
						&& (edoEventLog.getMvmt().equalsIgnoreCase("LL")
								|| edoEventLog.getMvmt().equalsIgnoreCase("00"))
						&& edoEventLog.getDnNbr().startsWith("D")) {
					log.info("**ED Ref No " + refNo
							+ " retrieved  is for 1st DN creation so update EDO for 1st DN scenario");
					isEDOForFirstDN = true;
					break;
				}

			}

			if (isEDOForFirstDN) {
				buffer.append("'").append(refInd.trim()).append("' and edo_asn_nbr= :refNo ")
						.append(" and tariff_main_cat_cd= :tariffMainCat ")
						.append(" and mvmt in ('LL','00') and edo_asn_nbr != dn_nbr and dn_nbr like 'D%' and bill_ind!='D' ");
				paramMap.put("tariffMainCat", tariffMainCat.trim());
				paramMap.put("refNo", refNo.trim());
			} else {
				log.info("**ED Ref No " + refNo
						+ " retrieved  is for warehouse updates where EDO number is same as DN number");
				buffer.append(
						" :refInd and (bl_nbr is null or bl_nbr='' or bl_nbr=' ') and (dn_nbr is null or dn_nbr='' or dn_nbr=' ' or dn_nbr = :refNo ")
						.append(") and edo_asn_nbr= :refNo ").append(" and tariff_main_cat_cd= :tariffMainCat ");
				paramMap.put("tariffMainCat", tariffMainCat.trim());
				paramMap.put("refNo", refNo.trim());
				paramMap.put("refInd", refInd.trim());
			}
		}
		// End Added by Irene Tan on 27 Feb 2003
		// added by Irene Tan on 3 Jun 2003 : for stuffing/unstuffing
		else if (refInd.trim().equals(ProcessChargeConst.REF_IND_STUFF)) {
			buffer.append(
					":refInd and cntr_nbr= :refNo and tariff_main_cat_cd= :tariffMainCat and (disc_vv_cd= :vvCd or load_vv_cd= :vvCd)");

			paramMap.put("vvCd", vvCd.trim());
			paramMap.put("tariffMainCat", tariffMainCat.trim());
			paramMap.put("refNo", refNo.trim());
			paramMap.put("refInd", refInd.trim());
		}
		// end Added by Irene Tan on 3 Jun 2003
		String sql = buffer.toString();

		try {
			log.info("START: updateProcessedGBEventLog  DAO  Start Obj refNo:" + CommonUtility.deNull(refNo) + "refInd" + CommonUtility.deNull(refInd)
					+ "tariffMainCat:" + CommonUtility.deNull(tariffMainCat) + "vvCd:" + CommonUtility.deNull(vvCd));

			log.info(" *** isTSLocalDN SQL *****" + sql + " paramMap: " + paramMap);
			ctr = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			log.info("ctr: " + ctr);
			if (ctr <= 0) {

				throw new SQLException(
						"[SQLException] GBEventLogEJB - updateProcessedGBEventLog(): Unable to update gb_charge_event_log table!\nRef No: "
								+ refNo + " ref Ind: " + refInd);
			}
		} catch (NullPointerException e) {
			log.info("Exception updateProcessedGBEventLog :", e);
			throw new BusinessException("M4201");
		} catch (SQLException e) {
			log.info("Exception updateProcessedGBEventLog :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updateProcessedGBEventLog :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateProcessedGBEventLog  DAO  END");
		}
	}

}
