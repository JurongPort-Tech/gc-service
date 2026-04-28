package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.GBChargeRepository;
import sg.com.jp.generalcargo.dao.GBEventLogRepository;
import sg.com.jp.generalcargo.dao.GBMSTriggerIndRepository;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.ProcessGBStoreRepository;
import sg.com.jp.generalcargo.dao.ProcessGenericRepository;
import sg.com.jp.generalcargo.dao.TariffMainRepository;
import sg.com.jp.generalcargo.dao.TransactionLoggerRepo;
import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.domain.BerthRelatedValueObject;
import sg.com.jp.generalcargo.domain.BillAdjustParam;
import sg.com.jp.generalcargo.domain.BillErrorVO;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.TariffMainVO;
import sg.com.jp.generalcargo.domain.TariffTierVO;
import sg.com.jp.generalcargo.domain.UserTimestampVO;
import sg.com.jp.generalcargo.domain.VesselRelatedValueObject;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.DoubleMath;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;
import sg.com.jp.generalcargo.util.ProcessChargeException;

@Repository("ProcessGBLogRepository")
public class ProcessGBLogJdbcRepository implements ProcessGBLogRepository {

	private static final Log log = LogFactory.getLog(ProcessGBLogJdbcRepository.class);

	private final String BILL_IND_U = "U";
	private final double HOURS = 24;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	private GBChargeRepository gbChargeRepo;
	@Autowired
	private GBEventLogRepository gbEventLogRepo;
	@Autowired
	private GBMSTriggerIndRepository gbmsTriggerRepo;
	@Autowired
	private ProcessGBStoreRepository processGBStoreRepo;
	@Autowired
	private ProcessGenericRepository processGenericRepo;
	@Autowired
	private TransactionLoggerRepo transLogRepo;
	@Autowired
	private TariffMainRepository tariffMainRepo;

	@Autowired
	@Lazy
	private ProcessGBLogJdbcRepository processGBLogJdbcRepository;

	private static final String ERROR_MSG1 = "< RUN DATE : ";
	private static final String ERROR_MSG2 = " RUN IND : ";
	private static final String ERROR_MSG3 = " TARIFF MAIN CAT : ";
	private static final String ERROR_MSG4 = " TARIFF SUB CAT : ";
	private static final String ERROR_MSG5 = " REMARKS : ";
	private static final String ERROR_MSG6 = " >";

	// jp.src.ejb.sessionBeans.cab.gbEventLog-->ProcessGBLogEJB-->cancelStuffCharges()
	// Added by Irene Tan on 12 Nov 2003 : Add in new method for cancellation of
	// stuffing/unstuffing charge
	/**
	 * Cancel GB Bill charge - resulting from waive of Stuffing/unstuffing charge
	 *
	 * @param cntrSeqNo Container Sequence No
	 * @param refInd    reference indicator ('SU')
	 * @param vvCd      vessel call id
	 * @param vvInd     Vessel Indicator ('D' - Discharge Vessel, 'L' - Loading
	 *                  Vessel)
	 * @exception ProcessChargeException
	 */
	@Override
	public boolean cancelStuffCharges(int cntrSeqNo, String refInd, String vvCd, String vvInd)
			throws BusinessException {
		boolean canCancel = true;
		String tempVvCd = "";

		try {
			log.info("START: cancelStuffCharges  DAO  Start cntrSeqNo:"
					+ CommonUtility.deNull(String.valueOf(cntrSeqNo)) + "refInd:" + CommonUtility.deNull(refInd)
					+ "vvCd:" + CommonUtility.deNull(vvCd) + "vvInd:" + CommonUtility.deNull(vvInd));

			if (refInd.trim().equals(ProcessChargeConst.REF_IND_STUFF)) {
				GeneralEventLogValueObject[] generalEventLogList = gbEventLogRepo.getGBEventLog(cntrSeqNo + "", refInd);
				for (int i = 0; i < generalEventLogList.length; i++) {
					if (vvInd.trim().equals(ProcessChargeConst.DISC_VV_IND)) {
						tempVvCd = generalEventLogList[i].getDiscVvCd();
					} else if (vvInd.trim().equals(ProcessChargeConst.LOAD_VV_IND)) {
						tempVvCd = generalEventLogList[i].getLoadVvCd();
					}

					if (vvCd.trim().equals(tempVvCd)) {
						if (generalEventLogList[i].getBillInd().trim().equals("N")) {
							// do nothing
						} else {
							canCancel = false;
						}
					}
				}

				if (generalEventLogList.length > 0) {
					if (canCancel) {
						// Cancel the GB Event Log for stuffing/unstuffing
						gbEventLogRepo.cancelGBEventLog(cntrSeqNo, refInd, vvCd, vvInd);
					}
				}
			} else {
				throw new BusinessException("Only Cancellation of Stuffing/Unstuffing charge is allowed!!!");
			}
		} catch (BusinessException e) {
			log.info("Exception cancelStuffCharges : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception cancelStuffCharges : ", e);
			throw new BusinessException("M4201");

		} finally {
			log.info("END: cancelStuffCharges  DAO  END  canCancel: " + canCancel);
		}
		return canCancel;
	} // end added by Irene Tan on 12 Nov 2003

	// jp.src.ejb.sessionBeans.cab.gbEventLog-->ProcessGBLogEJB-->executeGBCharges()
	/**
	 * This method logs the GB charges at close shipment / BJ
	 *
	 * @param VesselTxnEventLogValueObject
	 * @param ArrayList
	 * @param String
	 * @throws Exception
	 */
	@Override
	public void executeGBCharges(VesselTxnEventLogValueObject vesselTxnEventLogValueObject,
			List<GeneralEventLogValueObject> chargeEventLogList, String refInd) throws Exception {

		try {
			log.info("START executeGBCharges DAO :: vesselTxnEventLogValueObject: "
					+ CommonUtility.deNull(vesselTxnEventLogValueObject.toString()) + " chargeEventLogList: "
					+ CommonUtility.deNull(chargeEventLogList.toString()) + " refInd: " + CommonUtility.deNull(refInd));
			// inserts the chargeable events into the event log

			gbEventLogRepo.logGBEvent(vesselTxnEventLogValueObject, chargeEventLogList, refInd);
			// Updating the GBMS Indicator
			for (int i = 0; i < chargeEventLogList.size(); i++) {
				GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
				generalEventLogValueObject = (GeneralEventLogValueObject) chargeEventLogList.get(i);

				// Change by Irene Tan on 10 Jan 2003 : To comment out the blocking of the
				// trigger ind for EDO for TS within JP
				// Change by Irene Tan on 19 Dec 2002 : To block off the updating of the trigger
				// indicator in EDO for TS within JP as gb_edo table does not has these fields
				// if
				// (!(generalEventLogValueObject.getRefInd().trim().equals(ProcessChargeConst.REF_IND_EDO)))
				// {
				// String refNbr = processCommon.determineRefNbr(generalEventLogValueObject,
				// refInd);
				String refNbr = determineRefNbr(generalEventLogValueObject, generalEventLogValueObject.getRefInd());
				// gbmsTriggerInd.updateGBMSInd(vesselTxnEventLogValueObject.getVvCd(), refNbr,
				// refInd, vesselTxnEventLogValueObject.getLastModifyUserId(),
				// vesselTxnEventLogValueObject.getBillWharfInd(),
				// vesselTxnEventLogValueObject.getBillSvcChargeInd());
				// gbmsTriggerInd.updateGBMSInd(vesselTxnEventLogValueObject.getVvCd(), refNbr,
				// generalEventLogValueObject.getRefInd(),
				// vesselTxnEventLogValueObject.getLastModifyUserId(),
				// vesselTxnEventLogValueObject.getBillWharfInd(),
				// vesselTxnEventLogValueObject.getBillSvcChargeInd());
				// Added by Irene Tan on 11 Apr 2003 : to crater for multiple TESN under 1 EDO
				if (CommonUtility.deNull(generalEventLogValueObject.getRefInd())
						.equals(ProcessChargeConst.REF_IND_EDO)) {
					refNbr = generalEventLogValueObject.getEsnAsnNbr();
				}
				// end Added by Irene Tan on 11 Apr 2003
				// changed by Irene Tan on 5 Jun 2003: to include the updating of
				// stuffing/unstuffing indicators
				if (CommonUtility.deNull(generalEventLogValueObject.getRefInd())
						.equals(ProcessChargeConst.REF_IND_STUFF)) {
					String otherRefNbr = "";
					// changed by Irene Tan on 17 September 2003 (JPPL/IT/001/2001 - Phase 2) : To
					// update based on cntr_seq_nbr
					/*
					 * if (CommonUtility.deNull(generalEventLogValueObject.getLocalLeg()).equals(
					 * ProcessChargeConst.PURP_CD_IMPORT)) { otherRefNbr =
					 * CommonUtility.deNull(generalEventLogValueObject.getBlNbr()); } else { }
					 */
					otherRefNbr = generalEventLogValueObject.getCntrSeqNbr() + "";
					// end changed by Irene Tan on 17 September 2003 (JPPL/IT/001/2001 - Phase 2)
					gbmsTriggerRepo.updateStuffUnstuffInd(vesselTxnEventLogValueObject.getVvCd(), refNbr,
							generalEventLogValueObject.getRefInd(), vesselTxnEventLogValueObject.getLastModifyUserId(),
							vesselTxnEventLogValueObject.getBillStuffInd(), generalEventLogValueObject.getLocalLeg(),
							otherRefNbr);
				} else {
					gbmsTriggerRepo.updateGBMSInd(vesselTxnEventLogValueObject.getVvCd(), refNbr,
							generalEventLogValueObject.getRefInd(), vesselTxnEventLogValueObject.getLastModifyUserId(),
							vesselTxnEventLogValueObject.getBillWharfInd(),
							vesselTxnEventLogValueObject.getBillSvcChargeInd(),
							vesselTxnEventLogValueObject.getBillStoreInd());
				}
				// end changed by Irene Tan on 5 Jun 2003
				// }
				// End Change by Irene Tan on 19 Dec 2002
				// End Change by Irene Tan on 10 Jan 2003
			}

		} catch (NullPointerException e) {
			log.info("Exception executeGBCharges : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception executeGBCharges : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception executeGBCharges : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: executeGBCharges  DAO  END");
		}
	}

	// StartRegion ProcessGBLogJdbcRepository
	@Override
	public void checkAndUpdateFirstDN(String edoAsnNo, String dnRefNo) throws BusinessException {
		boolean isFirstDN = true;
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {

			log.info("START: checkAndUpdateFirstDN  DAO  Start Obj " + "edoAsnNo" + CommonUtility.deNull(edoAsnNo)
					+ "dnRefNo" + CommonUtility.deNull(dnRefNo));
			isFirstDN = isFirstDNForEDO(dnRefNo);

			if (isFirstDN) {
				sb = new StringBuilder();

				// get subsequent active DN if available
				sb.append(" Select dn_nbr From dn_details where dn_status='A' ");
				sb.append(" and edo_asn_nbr = (select edo_asn_nbr from dn_details where dn_nbr= :dnRefNo )");
				sb.append(" order by dn_create_dttm asc");

				log.info("SQL" + sb.toString());

				log.info(" checkAndUpdateFirstDN  SQL: " + sb.toString());
				paramMap.put("dnRefNo", dnRefNo);

				log.info(" *** checkAndUpdateFirstDN params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				String secondDNNbr = null;
				int cnt = 0;
				if (rs != null) {
					while (rs.next()) {
						if (cnt == 1) {
							secondDNNbr = rs.getString(1);
							break;
						}
						cnt++;
					}
					log.info("[CAB] Second Active DN Retrieved :**" + secondDNNbr);
				}

				// subsequent active DN is available, so update billing event before cancelling
				// the first DN
				if (secondDNNbr != null) {

					boolean canCancel = true;

					List<ChargeableBillValueObject> gbBillChargeArrayList = gbChargeRepo.getGBCharge(dnRefNo, "DN");
					for (int i = 0; i < gbBillChargeArrayList.size(); i++) {
						ChargeableBillValueObject charge = (ChargeableBillValueObject) gbBillChargeArrayList.get(i);
						if ((charge.getBillInd().trim().equals("N")) || (charge.getBillInd().trim().equals("U"))) {
							// do nothing
						} else {
							canCancel = false;
						}
					}

					if (canCancel) {
						sb = new StringBuilder();

						// update billable events table with new dnnbr
						sb.append("update gb_charge_event_log set dn_nbr=:dnNbr, last_modify_dttm=sysdate");
						sb.append(" where ref_ind='ED' and edo_asn_nbr=:edoAsnNbr ");
						sb.append("and dn_nbr  in (select distinct dn_nbr from gb_charge_event_log ");
						sb.append(" where edo_asn_nbr=:edoAsnNbr and bill_ind='Y' and ref_ind='ED' and mvmt='LL')");

						log.info("[CAB] update 2nd active dn to edo " + sb.toString());

						paramMap.put("dnNbr", secondDNNbr);
						paramMap.put("edoAsnNbr", edoAsnNo);

						log.info("[CAB] update 2nd active dn to edo params " + paramMap.toString());

						int update_cnt = namedParameterJdbcTemplate.update(sb.toString(), paramMap);

						log.info("[CAB]" + "Updated EDO with Second Active DN : " + update_cnt);
					}

				} else {
					log.info("[CAB]" + "Cancel ED charges also for the first DN to be cancelled: ");
					cancelBillableCharges(edoAsnNo, "ED");
				}
			}

		} catch (BusinessException e) {
			log.info("Exception checkAndUpdateFirstDN : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception checkAndUpdateFirstDN : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkAndUpdateFirstDN  DAO  END");
		}

	}

	public boolean isFirstDNForEDO(String dnNbr) throws BusinessException {

		boolean isFirstDN = false;
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START: isFirstDNForEDO  DAO  Start Obj :: dnNbr : " + CommonUtility.deNull(dnNbr));

			sb.append(" Select dn_nbr dnnbr From dn_details where dn_status='A'  ");
			sb.append(" and edo_asn_nbr = (select edo_asn_nbr edoNbr from dn_details where dn_nbr=:dnNbr ) ");
			sb.append("  and trans_type <> 'T' order by dn_create_dttm asc");

			paramMap.put("dnNbr", dnNbr);
			log.info(" isFirstDNForEDO SQL: " + sb.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				isFirstDN = true;
			} else {
				isFirstDN = false;
			}

			log.info("isFirstDNForEDO : " + isFirstDN);
		} catch (Exception e) {
			log.info("Exception isFirstDNForEDO : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isFirstDNForEDO  DAO  END");
		}

		return isFirstDN;
	}

	@Override
	public boolean cancelBillableCharges(String refNo, String refInd) throws BusinessException {
		boolean canCancel = true;

		try {
			log.info("START: cancelBillableCharges  DAO  Start Obj " + " refNo:" + CommonUtility.deNull(refNo)
					+ " refInd:" + CommonUtility.deNull(refInd));
			if ((refInd.trim().equals(ProcessChargeConst.REF_IND_DN))
					|| (refInd.trim().equals(ProcessChargeConst.REF_IND_UA))) {
				List<ChargeableBillValueObject> gbBillChargeArrayList = gbChargeRepo.getGBCharge(refNo, refInd);
				for (int i = 0; i < gbBillChargeArrayList.size(); i++) {
					ChargeableBillValueObject charge = (ChargeableBillValueObject) gbBillChargeArrayList.get(i);
					if ((charge.getBillInd().trim().equals("N")) || (charge.getBillInd().trim().equals("U"))) {
						// do nothing
					} else {
						canCancel = false;
					}
				}

				if (gbBillChargeArrayList.size() > 0) {
					if (canCancel) {
						// Cancel the GB Bill Charges
						gbChargeRepo.cancelGBCharge(refNo, refInd);
						// Cancel the GB Event Log
						gbEventLogRepo.cancelGBEventLog(refNo, refInd);
					}
				}
			}
			// MCC added for 1st DN creation include EDO as ref indicator for 1st DN
			// creation for cancellingBillablecharges
			else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_EDO))) {

				List<ChargeableBillValueObject> gbBillChargeArrayList = gbChargeRepo.getGBCharge(refNo, refInd);
				for (int i = 0; i < gbBillChargeArrayList.size(); i++) {
					ChargeableBillValueObject charge = (ChargeableBillValueObject) gbBillChargeArrayList.get(i);

					boolean isFirstDN = false;
					// MCC get the EDO refId record for 1st DN creation
					GeneralEventLogValueObject[] edoEventLogList = gbEventLogRepo.getGBEventLog(refNo, "ED1DN");
					if (edoEventLogList != null && edoEventLogList.length > 0) {
						GeneralEventLogValueObject edoEventLog = (GeneralEventLogValueObject) edoEventLogList[0];
						if (edoEventLog != null && edoEventLog.getEdoAsnNbr() != edoEventLog.getDnNbr()
								&& (edoEventLog.getMvmt().equalsIgnoreCase("LL")
										|| edoEventLog.getMvmt().equalsIgnoreCase("00"))
								&& edoEventLog.getDnNbr().startsWith("D")) {
							log.info("**Cancel ED Ref No " + refNo
									+ " retrieved  is for 1st DN creation so cancel EDO for 1st DN scenario");

							isFirstDN = true;

						}

					}

					if (isFirstDN && (charge.getBillInd().trim().equals("N"))
							|| (charge.getBillInd().trim().equals("U"))) {
						canCancel = true;
						break;
					} else {
						canCancel = false;
					}

				}

				if (gbBillChargeArrayList.size() > 0) {
					if (canCancel) {
						// Cancel the GB Bill Charges
						gbChargeRepo.cancelGBCharge(refNo, refInd);
						// Cancel the GB Event Log
						gbEventLogRepo.cancelGBEventLog(refNo, refInd);
					}
				}

			} else {
				throw new BusinessException(ConstantUtil.ErrorMsg_Reprinting_Charge);
			}
		}

		catch (BusinessException ex) {
			log.info("Exception cancelBillableCharges : ", ex);
			throw new BusinessException(ex.getMessage());
		} catch (Exception ex) {
			log.info("Exception cancelBillableCharges : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cancelBillableCharges  DAO  END");

		}
		return canCancel;
	}

	@Override
	public List<ChargeableBillValueObject> getGBBillCharge(String refNo, String refInd) throws BusinessException {
		List<ChargeableBillValueObject> chargeableBillList = new ArrayList<ChargeableBillValueObject>();

		try {
			log.info("START: getGBBillCharge  DAO  Start Obj " + " refNo:" + CommonUtility.deNull(refNo) + " refInd:"
					+ CommonUtility.deNull(refInd));

			if ((refInd.trim().equals(ProcessChargeConst.REF_IND_DN))
					|| (refInd.trim().equals(ProcessChargeConst.REF_IND_UA))) {
				chargeableBillList = gbChargeRepo.getGBCharge(refNo, refInd);
			} else {
				throw new BusinessException("Reprinting of charges only allowed for DN / UA!!!");
			}

			log.info("END: *** getGBBillCharge Result *****" + chargeableBillList.toString());
		} catch (BusinessException e) {
			log.info("Exception getGBBillCharge :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getGBBillCharge :", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getGBBillCharge  DAO  END  chargeableBillList: " + chargeableBillList.toString());
		}

		return chargeableBillList;
	}

	// package: ejb.sessionBeans.cab.gbEventLogRepo-->ProcessGBLogEJB
	// method: checkAnySR()
	// Added to not overwrite business Type
	public boolean checkAnySR(GeneralEventLogValueObject generalEventLogValueObject, int freeStoreDays)
			throws Exception {
		boolean storeRentTrigger = false;
		double storeRentDays = 0.0;
		freeStoreDays *= HOURS;
		List<ChargeableBillValueObject> storeRentList = new ArrayList<ChargeableBillValueObject>(1);

		try {
			log.info("START checkAnySR :: generalEventLogValueObject: "
					+ CommonUtility.deNull(generalEventLogValueObject.toString()) + " freeStoreDays: "
					+ CommonUtility.deNull(String.valueOf(freeStoreDays)));
			// Checks whether Store Rent days falls within the normal free storage period
			// given JP
			storeRentList = calculateSRBillCharge(generalEventLogValueObject, generalEventLogValueObject.getRefInd());
			if (storeRentList.size() > 0) {
				// Store Rent days exceed extended Free Storage Period given by JP, Store Rent
				// Charges will be triggered
				storeRentDays = processGBStoreRepo.determineTimeUnit(generalEventLogValueObject,
						generalEventLogValueObject.getRefInd());

				// For TS cargo, the store rent days will takes from the total store rent
				// computed from each tier
				if ((generalEventLogValueObject.getMvmt().equals(ProcessChargeConst.MVMT_TRANSHIP))
						|| (generalEventLogValueObject.getMvmt().equals(ProcessChargeConst.MVMT_ITH))) {
					storeRentDays = 0;
					for (int i = 0; i < storeRentList.size(); i++) {
						ChargeableBillValueObject charge = (ChargeableBillValueObject) storeRentList.get(i);
						storeRentDays += charge.getNbrTimeUnit();
					}
					storeRentDays *= HOURS;
				}
				log.info("storeRentDays = " + storeRentDays + " freeStoreDays = " + freeStoreDays);

				if (storeRentDays > freeStoreDays) {
					storeRentTrigger = true;
				}
				/*
				 * Commented by Jade for SL-CAB-20110509-01 // add gb customized fsp, 6.mar.11
				 * by hpeng TariffMainHome tariffMainHome = (TariffMainHome)
				 * homeFactory.lookUpHome("TariffMain"); TariffMain tariffMain =
				 * tariffMainHome.create(); int customizedFspDays =
				 * tariffMain.getGeneralCargoCustFspDays(generalEventLogValueObject); if
				 * (customizedFspDays > 0 && storeRentTrigger) { customizedFspDays *= HOURS;
				 * storeRentTrigger = storeRentDays > customizedFspDays ? true : false; }
				 */
			}
		} catch (BusinessException e) {
			log.info("Exception checkAnySR :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {

			log.info("Exception checkAnySR :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END checkAnySR DAO  storeRentTrigger: " + storeRentTrigger);
		}
		return storeRentTrigger;
	}

	// package: ejb.sessionBeans.cab.gbEventLogRepo-->ProcessGBLogEJB
	// method: calculateSRBillCharge()
	private List<ChargeableBillValueObject> calculateSRBillCharge(GeneralEventLogValueObject generalEventLogValueObject,
			String refInd) throws Exception {

		List<ChargeableBillValueObject> chargeList = null;

		try {
			log.info("START: calculateSRBillCharge  DAO  Start Obj "+" generalEventLogValueObject:"+ generalEventLogValueObject.toString() 
			+" refInd:"+CommonUtility.deNull(refInd));

			// calculates the wharfage charges based on the event log and returns result
			// based on
			// published rates
			chargeList = processGBStoreRepo.calSRBillCharge(generalEventLogValueObject, refInd);
			log.info("Proces GBLOg calculateSRBillCharge new method to not overwrite businessTYpe**"
					+ chargeList.size());
		} catch (NullPointerException e) {
			log.info("Exception calculateSRBillCharge :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception calculateSRBillCharge :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception calculateSRBillCharge :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: calculateSRBillCharge  DAO  END  chargeList: " + chargeList.toString());
		}
		return chargeList;
	}

	// package: ejb.sessionBeans.cab.gbEventLogRepo-->ProcessGBLogEJB
	// method: executeBillCharges()
	/**
	 * New EVM Implementation Parse the value objects passed in and insert/update
	 * them into relevant event log after which the calculation of charges is
	 * executed based on the type of charges logged and return the charges to the
	 * front-end for printing
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
	public List<ChargeableBillValueObject> executeBillCharges(VesselTxnEventLogValueObject vesselTxnEventLogValueObject,
			List<GeneralEventLogValueObject> chargeEventLogList, String refInd)
			throws NamingException, SQLException, ProcessChargeException, Exception {
		List<ChargeableBillValueObject> chargeableBillList = new ArrayList<ChargeableBillValueObject>();
		List<ChargeableBillValueObject> storeRentChargeList = new ArrayList<ChargeableBillValueObject>(1);
//		Exception ex = null;

		GeneralEventLogValueObject generalEventLogValueObject = null;
		ChargeableBillValueObject chargeableBillValueObject = null;

		int xcnt = 0;
		boolean txnSuccess = true;
		String tariffMainCat = null;
		String tariffSubCat = null;
		String refNbr = null;
		String mvmt = null;
		String billAcctNbr = null;
		// added by swho on 17/10/2002 for blocking triggering of UA charges
		String type = null;
		String localLeg = null;
		String discGateway = null;
		// end add by swho
		// MCC
		String isFirstDN = null;
		String schemeCd = null;
		String isEPCForTS = null;

		try {
			log.info("START executeBillCharges DAO :: vesselTxnEventLogValueObject: "
					+ CommonUtility.deNull(vesselTxnEventLogValueObject.toString()) + " chargeEventLogList: "
					+ CommonUtility.deNull(chargeEventLogList.toString()) + " refInd: " + CommonUtility.deNull(refInd));
			txnSuccess = true;
			try {
				gbEventLogRepo.logGBEvent(vesselTxnEventLogValueObject, chargeEventLogList, refInd);
			} catch (Exception e) {
//				ex = new Exception("Logging into event log failed. Msg = " + e.getMessage());
				txnSuccess = false;
//				throw ex;
				throw new BusinessException("Logging into event log failed. Msg = " + e.getMessage());
			}
			// retrieve the first charge event log record to get the unique reference number
			// depending on whether its a DN/UA/BL/BR number
			if (!chargeEventLogList.isEmpty()) {
				generalEventLogValueObject = (GeneralEventLogValueObject) chargeEventLogList.get(xcnt);
				refNbr = determineRefNbr(generalEventLogValueObject, refInd);
				// MCC get isFirstDNForEDO flag
				isFirstDN = generalEventLogValueObject.getIsFirstDNForEDO();
				isEPCForTS = generalEventLogValueObject.getIsEPCForTS();
			}

			GeneralEventLogValueObject[] chargeEventLogList1 = gbEventLogRepo.getGBEventLog(refNbr, refInd);

			// loop thru the retrieved charge event log records to determine which charges
			// to invoke
			// for calculation depending on the tariff main and sub category
			for (xcnt = 0; xcnt < chargeEventLogList1.length; xcnt++) {
				// get the individual record of the charge event log record
				generalEventLogValueObject = new GeneralEventLogValueObject();

				generalEventLogValueObject = chargeEventLogList1[xcnt];

				tariffMainCat = generalEventLogValueObject.getTariffMainCatCd();
				tariffSubCat = generalEventLogValueObject.getTariffSubCatCd();

				mvmt = generalEventLogValueObject.getMvmt();
				// added by swho on 17/10/2002 for blocking triggering of UA charges
				type = generalEventLogValueObject.getType();
				localLeg = generalEventLogValueObject.getLocalLeg();
				discGateway = generalEventLogValueObject.getDiscGateway();
				// end add by swho

				billAcctNbr = generalEventLogValueObject.getBillAcctNbr();

				try {
					if (!(billAcctNbr.trim().equals(ProcessChargeConst.CASH_CUST_CD))) {
						// For Intra-Transhipment cases, the DN & UA charges will be generated @ close
						// shipment
						// For Local Export, the UA charges will be generated @ close shipment
						// amended by swho on 17/10/2002 for blocking triggering of UA charges
						// For ITH PSA-JP, the UA charges will be generated @ close shipment
						// if ((mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)) ||
						// (refInd.trim().equals(ProcessChargeConst.REF_IND_UA))) {
						if ((mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)
								&& type.trim().equals(ProcessChargeConst.TYPE_OTHER)) ||
						// added by Irene Tan on 10/03/2003 : to block the triggering of TS Store Rent
								(mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)
										&& type.trim().equals(ProcessChargeConst.TYPE_BY_TON))
								||
								// end added by Irene Tan on 10/03/2003
								(mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL)
										&& localLeg.trim().equals(ProcessChargeConst.PURP_CD_EXPORT))
								|| (mvmt.trim().equals(ProcessChargeConst.MVMT_ITH) && discGateway.trim().equals("P"))
								|| (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) ||
								// ThachPhung starts on 11Feb09 to support RORO processing
								(mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)
										&& ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equals(type.trim()))
								|| (mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)
										&& ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equals(type.trim()))
								|| (mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)
										&& ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equals(type.trim()))
						// ThachPhung end
						) {
							// end amend by swho
							generalEventLogValueObject.setBillInd(BILL_IND_U);
						}
					}

					// MCC reset isFirstDNForEDO flag
					generalEventLogValueObject.setIsFirstDNForEDO(isFirstDN);
					schemeCd = generalEventLogValueObject.getSchemeCd();

					// MCConsulting commented as part of RRS changes for 1st DN creation
					/*
					 * if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WHARFAGE)) {
					 * chargeableBillValueObject =
					 * calculateWharfageBillCharge(generalEventLogValueObject, refInd);
					 * chargeableBillList.add(chargeableBillValueObject); }
					 */

					// MCC for 1st DN creation charge for EDO billTon so update DN status as U to
					// prevent final bill for DN
					// Fixes for ANIMAL wharfage sub cat to bill- added condition to exclude to set
					// bill indicator as U
					// Added new logs
					log.info("**Fixes for Animal Category 1**:" + tariffMainCat + ":" + tariffSubCat);
					log.info("**Fixes for Animal Category 2**:" + generalEventLogValueObject.getBillInd());

					if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WHARFAGE)) {
						boolean isTSLLDN = isTSLocalDN(refInd, refNbr);
						log.info("Ref number " + refNbr + " is DN for transhipment cargo delivered locally? "
								+ isTSLLDN);
						// if(refInd.equalsIgnoreCase(ProcessChargeConst.REF_IND_DN)){
						if (refInd.equalsIgnoreCase(ProcessChargeConst.REF_IND_DN) && !isTSLLDN) { // Fixed. Only update
							// to Not To Bill
							// for DNs others
							// than transhipment
							// cargo delivered
							// locally.
							if ((mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_LOCAL)
									&& (isEPCForTS == null || !isEPCForTS.equals("Y")) // ignore EPCIND Y cases for TS
									// and IT cargoes
									&& ((schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME))
											|| (schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
											|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
											|| (schemeCd.trim().equals(ProcessChargeConst.WOODEN_CRAFT_SCHEME))
											|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) // for LCT
									// local to
									// charge
									// with
									// tonnage
									// in
									// manifest
									|| (mvmt.equalsIgnoreCase(ProcessChargeConst.NOT_APPLICABLE_MVMT) // for shut-out
											// cargo also
											&& ((schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME))
													|| (schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
													|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
													|| (schemeCd.trim().equals(ProcessChargeConst.WOODEN_CRAFT_SCHEME))
													|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME))))) {
								log.info("First DN Creation/Local LCT sheme. Update BillInd as U for DN WF for mvmt:"
										+ mvmt + " and scheme:" + schemeCd);
								generalEventLogValueObject.setBillInd(BILL_IND_U);
							}
						}

						log.info("**Fixes for Animal Category 3**:" + generalEventLogValueObject.getBillInd());
						chargeableBillValueObject = calculateWharfageBillCharge(generalEventLogValueObject, refInd);
						log.info("**After getting bill Ind**:" + chargeableBillValueObject.getBillInd());
						chargeableBillList.add(chargeableBillValueObject);
						log.info("**AfterInd adding in bill List :" + chargeableBillList.size());
					}

					// MCConsulting commented as part of RRS changes
					/*
					 * if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_SERVICE_CHARGE)) {
					 * chargeableBillValueObject =
					 * calculateServiceBillCharge(generalEventLogValueObject, refInd);
					 * chargeableBillList.add(chargeableBillValueObject); }
					 */

					// MCConsulting- Upon creation of 1st DN log one extra billable event with the
					// tonnage in EDO so that ServiceCharge can be triggered at one go at EDO level
					if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_SERVICE_CHARGE)) {
						if (refInd.equalsIgnoreCase(ProcessChargeConst.REF_IND_DN)
								&& mvmt.equalsIgnoreCase(ProcessChargeConst.MVMT_LOCAL)
								&& (isEPCForTS == null || !isEPCForTS.equals("Y")) // ignore EPCIND Y cases for TS and
								// IT cargoes
								&& ((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
										|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
										|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) { // for LCT local
							// to charge
							// with tonnage
							// in manifest
							log.info("First DN Creation/Local LCT sheme. Update BillInd as U for DN SC for mvmt:" + mvmt
									+ " and scheme:" + schemeCd);
							generalEventLogValueObject.setBillInd(BILL_IND_U);
						}
						chargeableBillValueObject = calculateServiceBillCharge(generalEventLogValueObject, refInd);
						chargeableBillList.add(chargeableBillValueObject);
					}

					if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_GB_STORE_RENT)) {
						generalEventLogValueObject.setRefInd(refInd); // MCC added fix to set refInd while calculating
						// store rent to avoid impact for periodic store
						// rent
						log.info("ProcesGBLog**executeBillCharges::" + refInd);
						storeRentChargeList = calculateSRBillCharge(generalEventLogValueObject, refInd);
						log.info("ProcesGBLog**executeBillCharges::storeRentChargeList::" + storeRentChargeList.size());
						for (int i = 0; i < storeRentChargeList.size(); i++) {
							chargeableBillValueObject = new ChargeableBillValueObject();
							chargeableBillValueObject = (ChargeableBillValueObject) storeRentChargeList.get(i);
							log.info("ProcesGBLog**executeBillCharges::charegeableBillValueObject::"
									+ chargeableBillValueObject.getBillInd());
							chargeableBillList.add(chargeableBillValueObject);
						}
					}
					// chargeableBillList.add(chargeableBillValueObject);
				} catch (Exception exception) {
//					ex = exception;
					txnSuccess = false;
					continue;
				}
			}

			if (txnSuccess) {
				gbChargeRepo.addGBCharge(chargeableBillList);

				for (int i = 0; i < chargeableBillList.size(); i++) {
					chargeableBillValueObject = (ChargeableBillValueObject) chargeableBillList.get(i);
					// gbEventLogRepo.updateProcessedGBEventLog(chargeableBillValueObject.getRefNbr(),
					// chargeableBillValueObject.getRefInd(),
					// chargeableBillValueObject.getTariffMainCatCd());
					gbEventLogRepo.updateProcessedGBEventLog(chargeableBillValueObject.getRefNbr(),
							chargeableBillValueObject.getRefInd(), chargeableBillValueObject.getTariffMainCatCd(),
							null);
					// gbmsTriggerInd.updateGBMSInd("", chargeableBillValueObject.getRefNbr(),
					// chargeableBillValueObject.getRefInd(),
					// chargeableBillValueObject.getLastModifyUserId(),
					// vesselTxnEventLogValueObject.getBillWharfInd(),
					// vesselTxnEventLogValueObject.getBillSvcChargeInd());
					gbmsTriggerRepo.updateGBMSInd("", chargeableBillValueObject.getRefNbr(),
							chargeableBillValueObject.getRefInd(), chargeableBillValueObject.getLastModifyUserId(),
							vesselTxnEventLogValueObject.getBillWharfInd(),
							vesselTxnEventLogValueObject.getBillSvcChargeInd(),
							vesselTxnEventLogValueObject.getBillStoreInd());
				}
				chargeableBillList = (ArrayList<ChargeableBillValueObject>) gbChargeRepo.getGBCharge(refNbr, refInd);
			}
		} catch (BusinessException e) {
			log.info("Exception executeBillableCharges :", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception executeBillableCharges :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception executeBillableCharges :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: executeBillableCharges  DAO  END  chargeableBillList: " + chargeableBillList.toString());
		}
		return chargeableBillList;
	}

	// package: ejb.sessionBeans.cab.gbEventLogRepo-->ProcessGBLogEJB
	// method: determineRefNbr()
	// added by swho on 12/06/2002 for GBMS General Cargo processing
	/**
	 * Determine the reference number based on the ref_ind passed in
	 * 
	 * @param GeneralEventLogValueObject, String
	 * @return String
	 * @throws Exception
	 */
	public String determineRefNbr(GeneralEventLogValueObject generalEventLogValueObject, String refInd)
			throws Exception {
		Exception ex = null;

		String refNbr = "";

		try {
			log.info("START determineRefNbr DAO :: generalEventLogValueObject: "
					+ CommonUtility.deNull(generalEventLogValueObject.toString()) + " refInd: "
					+ CommonUtility.deNull(refInd));
			if (refInd.equals(ProcessChargeConst.REF_IND_DN)) {
				refNbr = generalEventLogValueObject.getDnNbr();
			}

			if (refInd.equals(ProcessChargeConst.REF_IND_UA)) {
				refNbr = generalEventLogValueObject.getUaNbr();
			}

			if (refInd.equals(ProcessChargeConst.REF_IND_BL)) {
				refNbr = generalEventLogValueObject.getBlNbr();
			}

			if (refInd.equals(ProcessChargeConst.REF_IND_BR)) {
				refNbr = generalEventLogValueObject.getBkRefNbr();
			}

			// Added in by Irene Tan on 10/7/2002
			if (refInd.equals(ProcessChargeConst.REF_IND_EDO)) {
				refNbr = generalEventLogValueObject.getEdoAsnNbr();
			}

			if (refInd.equals(ProcessChargeConst.REF_IND_ESN)) {
				refNbr = generalEventLogValueObject.getEsnAsnNbr();
			}
			// End Added by Irene Tan on 10/7/2002
			// added by Irene Tan on 5 Jun 2003
			if (refInd.equals(ProcessChargeConst.REF_IND_STUFF)) {
				refNbr = generalEventLogValueObject.getCntrNbr();
			}
			// end added by Irene Tan on 5 Jun 2003
			if (refNbr.equals("")) {
				String[] tempString = { refInd };

				ex = new Exception(
						CommonUtil.getErrorMessage(ConstantUtil.Reference_number_empty_or_invalid, tempString));
				throw ex;
			}
		} catch (Exception e) {
			log.info("Exception occurred when determining reference number : " + e.getMessage());
			ex = e;
		}

		if (ex != null) {
			throw (Exception) ex;
		}
		log.info("END determineRefNbr DAO  refNbr: " + refNbr);
		return refNbr;
	} // end add by swho

	// package: ejb.sessionBeans.cab.gbEventLogRepo-->ProcessGBLogEJB
	// method: checkAnyStoreRent()
	/**
	 * Check whether there will be any store rent charges to be triggered after free
	 * store rent period has been given
	 *
	 * @param GeneralEventLogValueObject
	 * @param int
	 * @return <code>true</code>
	 * @exception NamingException
	 * @exception SQLException
	 * @exception ProcessChargeException
	 * @exception Exception
	 */
	public boolean checkAnyStoreRent(GeneralEventLogValueObject generalEventLogValueObject, int freeStoreDays)
			throws Exception {
		boolean storeRentTrigger = false;
		double storeRentDays = 0.0;
		freeStoreDays *= HOURS;
		List<ChargeableBillValueObject> storeRentList = new ArrayList<ChargeableBillValueObject>(1);

		try {
			log.info("START checkAnyStoreRent DAO :: generalEventLogValueObject: "
					+ CommonUtility.deNull(generalEventLogValueObject.toString()) + " freeStoreDays: "
					+ CommonUtility.deNull(String.valueOf(freeStoreDays)));
			// Checks whether Store Rent days falls within the normal free storage period
			// given JP
			storeRentList = calculateStoreRentBillCharge(generalEventLogValueObject,
					generalEventLogValueObject.getRefInd());
			if (storeRentList.size() > 0) {
				// Store Rent days exceed extended Free Storage Period given by JP, Store Rent
				// Charges will be triggered
				storeRentDays = processGBStoreRepo.determineTimeUnit(generalEventLogValueObject,
						generalEventLogValueObject.getRefInd());

				// For TS cargo, the store rent days will takes from the total store rent
				// computed from each tier
				if ((generalEventLogValueObject.getMvmt().equals(ProcessChargeConst.MVMT_TRANSHIP))
						|| (generalEventLogValueObject.getMvmt().equals(ProcessChargeConst.MVMT_ITH))) {
					storeRentDays = 0;
					for (int i = 0; i < storeRentList.size(); i++) {
						ChargeableBillValueObject charge = (ChargeableBillValueObject) storeRentList.get(i);
						storeRentDays += charge.getNbrTimeUnit();
					}
					storeRentDays *= HOURS;
				}
				log.info("storeRentDays = " + storeRentDays + " freeStoreDays = " + freeStoreDays);

				if (storeRentDays > freeStoreDays) {
					storeRentTrigger = true;
				}
				/*
				 * Commented by Jade for SL-CAB-20110509-01 // add gb customized fsp, 6.mar.11
				 * by hpeng TariffMainHome tariffMainHome = (TariffMainHome)
				 * homeFactory.lookUpHome("TariffMain"); TariffMain tariffMain =
				 * tariffMainHome.create(); int customizedFspDays =
				 * tariffMain.getGeneralCargoCustFspDays(generalEventLogValueObject); if
				 * (customizedFspDays > 0 && storeRentTrigger) { customizedFspDays *= HOURS;
				 * storeRentTrigger = storeRentDays > customizedFspDays ? true : false; }
				 */
			}
		} catch (BusinessException e) {
			log.info("Exception checkAnyStoreRent :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception checkAnyStoreRent :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END checkAnyStoreRent DAO  storeRentTrigger: " + storeRentTrigger);
		}
		return storeRentTrigger;
	}

	// package: ejb.sessionBeans.cab.gbEventLogRepo-->ProcessGBLogEJB
	// method: calculateStoreRentBillCharge()
	/**
	 * Calling method that calls the specific ProcessGBStoreEJB to do calculation of
	 * the store rent bill charge before insertion into the gb_bill_charge table
	 *
	 * @param GeneralEventLogValueObject
	 * @param String
	 * @return ArrayList
	 * @exception NamingException
	 * @exception SQLException
	 * @exception ProcessChargeException
	 * @exception Exception
	 */
	private List<ChargeableBillValueObject> calculateStoreRentBillCharge(
			GeneralEventLogValueObject generalEventLogValueObject, String refInd)
			throws NamingException, SQLException, ProcessChargeException, Exception {

		List<ChargeableBillValueObject> chargeList = null;

		try {
			log.info("START calculateStoreRentBillCharge DAO :: generalEventLogValueObject: "
					+ CommonUtility.deNull(generalEventLogValueObject.toString()) + " refInd: "
					+ CommonUtility.deNull(refInd));
			// calculates the wharfage charges based on the event log and returns result
			// based on
			// published rates
			chargeList = calculateStoreBillCharge(generalEventLogValueObject, refInd);
		} catch (NullPointerException e) {
			log.info("Exception calculateStoreRentBillCharge :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception calculateStoreRentBillCharge :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception calculateStoreRentBillCharge :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END calculateStoreRentBillCharge DAO  chargeList: " + chargeList.toString());
		}
		return chargeList;
	}

	// package: ejb.sessionBeans.cab.gbEventLogRepo-->ProcessGBLogEJB
	// method: calculateStoreBillCharge()
	/**
	 * Calculates the store rent charges based on the event log and returns result
	 * based on published rates
	 * 
	 * @param GeneralEventLogValueObject
	 * @param String
	 * @return ArrayList
	 * @exception NamingException
	 * @exception ProcessChargeException
	 * @exception SQLException
	 * @exception Exception
	 */
	public List<ChargeableBillValueObject> calculateStoreBillCharge(
			GeneralEventLogValueObject generalEventLogValueObject, String refInd)
			throws NamingException, SQLException, ProcessChargeException, Exception {

		BillErrorVO billErrorValueObject = null;
		ChargeableBillValueObject chargeableBillValueObject = new ChargeableBillValueObject();
		VesselRelatedValueObject vesselRelatedValueObject = null;
		BerthRelatedValueObject berthRelatedValueObject = null;
		TariffMainVO tariffMainValueObject = null;
		AccountValueObject accountValueObject = null;

		List<ChargeableBillValueObject> chargeableBillList = null;
		List<ChargeableBillValueObject> prevChargeableBillList = null;
		Timestamp varDttm = null;
		int versionNbr = 0;

		String refNbr = null;
		String discVvCd = null;
		String loadVvCd = null;
		String vvInd = null;
		String businessType = null;
		String schemeCd = null;
		String tariffMainCat = null;
		String tariffSubCat = null;
		String mvmt = null;
		String type = null;
		String localLeg = null;
		String billAcctNbr = null;
		String billInd = null;
		Timestamp lastModifyDttm = null;

		String varVvCd = null;
		double minTon = 0.0;
		double billTon = 0.0;
		double deriveTon = 0.0;
		double deriveTimePeriod = 0.0;
		double billTonEdo = 0.0;
		int totalPkgEdo = 0;
		int totalPkgDn = 0;
		double billTonEsn = 0.0;
		double loadTonCs = 0.0;

		// EVM Enhancements
		String edoEsnNbr = null;
		String blNbr = null;
		try {
			log.info("START calculateStoreBillCharge DAO :: generalEventLogValueObject: "
					+ CommonUtility.deNull(generalEventLogValueObject.toString()) + " refInd: "
					+ CommonUtility.deNull(refInd));
			refNbr = determineRefNbr(generalEventLogValueObject, refInd);
			discVvCd = generalEventLogValueObject.getDiscVvCd();
			loadVvCd = generalEventLogValueObject.getLoadVvCd();
			vvInd = generalEventLogValueObject.getVvInd();
			businessType = generalEventLogValueObject.getBusinessType();
			schemeCd = generalEventLogValueObject.getSchemeCd();
			tariffMainCat = generalEventLogValueObject.getTariffMainCatCd();
			tariffSubCat = generalEventLogValueObject.getTariffSubCatCd();
			mvmt = generalEventLogValueObject.getMvmt();
			type = generalEventLogValueObject.getType();
			localLeg = generalEventLogValueObject.getLocalLeg();
			billAcctNbr = generalEventLogValueObject.getBillAcctNbr();
			billInd = generalEventLogValueObject.getBillInd();
			lastModifyDttm = generalEventLogValueObject.getLastModifyDttm();
			billTonEdo = generalEventLogValueObject.getBillTonEdo();
			totalPkgEdo = generalEventLogValueObject.getTotalPackEdo();
			totalPkgDn = generalEventLogValueObject.getTotalPackDn();
			billTonEsn = generalEventLogValueObject.getBillTonEsn();
			loadTonCs = generalEventLogValueObject.getLoadTonCs();
			blNbr = generalEventLogValueObject.getBlNbr();

			// EVM Enhancements
			if (localLeg.equalsIgnoreCase("IM")) {
				edoEsnNbr = generalEventLogValueObject.getEdoAsnNbr();
			} else {
				edoEsnNbr = generalEventLogValueObject.getEsnAsnNbr();
			}

			/*
			 * if (vvInd.equals(ProcessChargeConst.DISC_VV_IND)) { varVvCd = discVvCd; }
			 */// <cfg amend, 12.aug.09>
			log.info("[CAB] CR64 [SR] <==========================================================");
			log.info("[CAB] CR64 [SR] vvInd = (" + vvInd + "), mvmt = (" + mvmt + ")," + tariffMainCat + "/"
					+ tariffSubCat);
			log.info("[CAB] CR64 [SR] schemeCd = (" + schemeCd + "), localLeg = (" + localLeg + "),disc:" + discVvCd
					+ "/load:" + loadVvCd);
			log.info("[CAB] CR64 [SR] vvInd = (" + vvInd + "), mvmt = (" + mvmt + ")," + tariffMainCat + "/"
					+ tariffSubCat);
			// TS && JLR && SRGL/SRUS
			if (vvInd.equals(ProcessChargeConst.DISC_VV_IND)) {
				if (mvmt.trim().equalsIgnoreCase(ProcessChargeConst.MVMT_TRANSHIP)
						&& schemeCd.equalsIgnoreCase(ProcessChargeConst.LINER_SCHEME) && // <cfg accdg to user,
						// 10.nov.09>
						(tariffMainCat.trim().equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_GB_STORE_RENT)
								&& (tariffSubCat.trim()
										.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_GENERAL)
										|| tariffSubCat.trim().equalsIgnoreCase(
												ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE)))) {
					log.info("[CAB] CR64 [SR] using loadVvCd: " + loadVvCd + " instead of discVvCd: " + discVvCd);
					varVvCd = loadVvCd;
				} else {
					log.info("[CAB] CR64 [SR] using discVvCd: " + discVvCd);
					varVvCd = discVvCd;
				}
			} // <cfg 12.aug.09/>

			else if (vvInd.equals(ProcessChargeConst.LOAD_VV_IND)) {
				varVvCd = loadVvCd;
			}
			log.info("[CAB] CR64 [SR] ==========================================================>");

			vesselRelatedValueObject = new VesselRelatedValueObject();
			vesselRelatedValueObject.setVvCd(varVvCd);

			// retrieve the atb timestamp for further processing
			try {
				berthRelatedValueObject = processGenericRepo.retrieveBerthDttm(vesselRelatedValueObject, 1,
						tariffMainCat);
				varDttm = berthRelatedValueObject.getAtbDttm();
				log.info("[CAB] atb_dttm = " + varDttm + "...");
			} catch (Exception e) {
				log.info("calculateStoreBillCharge Exception: ", e);
			}

			if (varDttm == null) {
				varDttm = UserTimestampVO.getCurrentTimestamp();
			}

			// retrieve the relevant tariff version number based on the timestamp passed in
			versionNbr = processGenericRepo.retrieveTariffVersion(varDttm, vesselRelatedValueObject, tariffMainCat);
			log.info("[CAB] version nbr = " + versionNbr + "...");

			// There's no differeniation of scheme code for Store Rent
			schemeCd = ProcessChargeConst.NOT_APPLICABLE_SCHEME;

			// add by hujun on 2/8/2011 for shutout cargo billing
			if (ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO.equalsIgnoreCase(tariffSubCat)) {
				if (ProcessChargeConst.NON_LINER_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					schemeCd = ProcessChargeConst.NON_LINER_SCHEME;
				} else if (ProcessChargeConst.BARTER_TRADER_SCHEME
						.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					schemeCd = ProcessChargeConst.BARTER_TRADER_SCHEME;
				} else if (ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					schemeCd = ProcessChargeConst.LCT_SCHEME;
				} else if (ProcessChargeConst.LINER_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					schemeCd = ProcessChargeConst.LINER_SCHEME;
				} else if (ProcessChargeConst.WOODEN_CRAFT_SCHEME
						.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					schemeCd = ProcessChargeConst.WOODEN_CRAFT_SCHEME;
				}
			}
			// add end

			// Set the mvmt to Not applicable for Unauthorised Store Rent
			if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE)) {
				mvmt = ProcessChargeConst.NOT_APPLICABLE_MVMT;
			}

			// add by hujun 2/8/2011 for shutout cargo
			if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO)) {
				if (!ProcessChargeConst.LINER_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					mvmt = ProcessChargeConst.NOT_APPLICABLE_MVMT;
				}
			}
			// add end

			// New EVM Enhancements Start Sripriya 14 Feb 2018 only required for General
			// Cargo Wharfage
			// New method as this is to cater for Steel & GC(Mixed) Steel to go to Steel &
			// GC to go to GC
			String hscode = processGenericRepo.getHSCode(edoEsnNbr, localLeg, blNbr, varVvCd);
			log.info("[CAB] PRocess GB STore calculateSToreBillCharge after retrieve hsCode" + hscode + ":" + edoEsnNbr
					+ ":" + localLeg + ":" + blNbr + ":" + varVvCd);
			if (hscode != null && !hscode.equalsIgnoreCase("")) {
				businessType = processGenericRepo.getBusTypeForWFSCSR(berthRelatedValueObject, hscode);
				// reset the business type back
				generalEventLogValueObject.setBusinessType(businessType); // Added to reset the business type back
				log.info("[CAB] PRocess GB Store calculateSToreBillCharge inside if after set business TYpe:"
						+ businessType + ":" + generalEventLogValueObject.getBusinessType());
			}
			log.info("[CAB] PRocess GB Store calculateSToreBillCharge outside if after set business TYpe:"
					+ businessType + ":" + generalEventLogValueObject.getBusinessType());
			// New EVM Enhancements Start Sripriya 14 Feb 2018

			// retrieve the published tariff
			// Change by Irene Tan on 10/12/2002 : CTCAB20020044 - To crater for GST change
			// w.e.f 1 Jan 2003
			// tariffMainValueObject = processGeneric.retrievePublishTariffDtls(versionNbr,
			// tariffMainCat, tariffSubCat, businessType, schemeCd, mvmt, type, "00", "00");
			tariffMainValueObject = processGenericRepo.retrievePublishTariffDtls(versionNbr, tariffMainCat,
					tariffSubCat, businessType, schemeCd, mvmt, type, "0", "0", varDttm);
			// End Change by Irene Tan

			// compute bill tonnage
			if (refInd.trim().equals(ProcessChargeConst.REF_IND_DN)) {
				billTon = transLogRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
			} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) {
				billTon = billTonEsn;
			} else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_ESN))
					|| (refInd.trim().equals(ProcessChargeConst.REF_IND_EDO))) {
				billTon = loadTonCs;
			}

			if (billAcctNbr.trim().equals(ProcessChargeConst.CASH_CUST_CD)) {
				minTon = tariffMainValueObject.getMinTonnage();
				if (billTon < minTon) {
					deriveTon = minTon;
				} else {
					deriveTon = billTon;
				}
			} else {
				deriveTon = billTon;
			}

			// MCConsulting min 1 billable ton for all general cargo charges
			log.info("[CAB] befor deriveTon " + deriveTon + "...");
			if (deriveTon < 1 && deriveTon > 0)
				deriveTon = 1;
			log.info("[CAB] after deriveTon " + deriveTon + "...");

			generalEventLogValueObject.setVarDttm(varDttm); // SL-CAB-20170518-01
			deriveTimePeriod = processGBStoreRepo.determineTimeUnit(generalEventLogValueObject, refInd);

			if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE)) {
				DoubleMath dm = new DoubleMath();
				dm.setRoundingMode(BigDecimal.ROUND_UP);

				int tierCnt = 0;
				String perHourType = "";
				for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
					TariffTierVO tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);
					perHourType = tariffTierValueObject.getPerHourType();
				}

				if (perHourType.equals("D")) {
					deriveTimePeriod = dm.divide(deriveTimePeriod, 24.0); // hr to day
				}
			}

			// add by hujun on 2/8/2011
			if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO)) {
				DoubleMath dm = new DoubleMath();
				dm.setRoundingMode(BigDecimal.ROUND_UP);

				int tierCnt = 0;
				String perHourType = "";
				for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
					TariffTierVO tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);
					perHourType = tariffTierValueObject.getPerHourType();
				}

				if (perHourType.equals("D")) {
					deriveTimePeriod = dm.divide(deriveTimePeriod, 24.0); // hr to day
				}
			}
			// add end

			log.info("[CAB] deriveTimePeriod:  " + deriveTimePeriod);

			chargeableBillValueObject.setRefNbr(refNbr);
			chargeableBillValueObject.setTxnDttm(lastModifyDttm);
			chargeableBillValueObject.setRefInd(refInd);
			chargeableBillValueObject.setTariffMainCatCd(tariffMainCat);
			chargeableBillValueObject.setTariffSubCatCd(tariffSubCat);
			chargeableBillValueObject.setVersionNbr(versionNbr);
			chargeableBillValueObject.setCustCd("");
			chargeableBillValueObject.setAcctNbr("");
			chargeableBillValueObject.setContractNbr("");
			chargeableBillValueObject.setContractualYr(0);
			chargeableBillValueObject.setNbrCntr(0);
			chargeableBillValueObject.setVvCd(varVvCd);
			chargeableBillValueObject.setBillInd(billInd);
			chargeableBillValueObject.setLocalLeg(localLeg);
			chargeableBillValueObject.setLastModifyUserId("SYSTEM");
			chargeableBillValueObject.setLastModifyDttm(lastModifyDttm);

			if (billAcctNbr.trim().equals(ProcessChargeConst.CASH_CUST_CD)) {
				accountValueObject = processGenericRepo.retrieveCashCustAcct();
			} else {
				accountValueObject = processGenericRepo.retrieveCustAcct(null, billAcctNbr);
			}
			chargeableBillValueObject.setAcctNbr(accountValueObject.getAccountNumber());
			chargeableBillValueObject.setCustCd(accountValueObject.getCustomerCode());

			// chargeableBillList = determineStoreRentBillable(chargeableBillValueObject,
			// tariffMainValueObject, deriveTon, deriveTimePeriod);
			// Added by Jade for SL-CAB-20110509-01
			int fsp = tariffMainRepo.getGeneralCargoCustFspDays(generalEventLogValueObject);
			chargeableBillList = processGBStoreRepo.determineStoreRentBillable(chargeableBillValueObject,
					tariffMainValueObject, deriveTon, deriveTimePeriod, fsp);
			// End of adding by Jade for SL-CAB-20110509-01

			// MCC check if last triggered dttm is available, if so calculate the storage
			// period for last triggered dttm
			// get ProcessGenericHome object
			Timestamp lastTriggeredDttm = null;
			if (generalEventLogValueObject.getRefInd().equals("DN")) {
				lastTriggeredDttm = processGenericRepo
						.retrievePeriodicSRLastTriggerDttm(generalEventLogValueObject.getEdoAsnNbr(), "ED", null);
			}
			if (lastTriggeredDttm != null) {
				log.info("[CAB] Periodic store rent lastTriggeredDttm:  " + lastTriggeredDttm);

				generalEventLogValueObject.setLastTriggerPSRDttm(lastTriggeredDttm);
				deriveTimePeriod = processGBStoreRepo.determineTimeUnit(generalEventLogValueObject, refInd);

				log.info("[CAB] Periodic store rent previous period deriveTimePeriod:  " + deriveTimePeriod);

				prevChargeableBillList = processGBStoreRepo.determineStoreRentBillable(chargeableBillValueObject,
						tariffMainValueObject, deriveTon, deriveTimePeriod, fsp);

				// loop through the original charge list and current periodic charge list and
				// minus the previoud charges
				List<ChargeableBillValueObject> finalChargeableBillList = new ArrayList<ChargeableBillValueObject>();
				for (int i = 0; i < chargeableBillList.size(); i++) {
					ChargeableBillValueObject finalChargeableBillVO = (ChargeableBillValueObject) chargeableBillList
							.get(i);
					ChargeableBillValueObject newFinalChargeableBillVO = new ChargeableBillValueObject(
							finalChargeableBillVO);

					for (int j = 0; j < prevChargeableBillList.size(); j++) {
						ChargeableBillValueObject lastPeriodicVO = (ChargeableBillValueObject) prevChargeableBillList
								.get(i);
						if (newFinalChargeableBillVO.getTierSeqNbr() == lastPeriodicVO.getTierSeqNbr()) {

							if (newFinalChargeableBillVO.getNbrTimeUnit() - lastPeriodicVO.getNbrTimeUnit() > 0) {
								double nbrTimeUnit = newFinalChargeableBillVO.getNbrTimeUnit()
										- lastPeriodicVO.getNbrTimeUnit();
								double totalChargeAmt = newFinalChargeableBillVO.getTotalChargeAmt()
										- lastPeriodicVO.getTotalChargeAmt();
								newFinalChargeableBillVO.setNbrTimeUnit(nbrTimeUnit);
								newFinalChargeableBillVO.setTotalChargeAmt(totalChargeAmt);
								log.info("[CAB] Periodic store rent adding new rates  nbrTimeUnit:" + nbrTimeUnit);
								finalChargeableBillList.add(newFinalChargeableBillVO);
							}
						}
					}
				}
				generalEventLogValueObject.setLastTriggerPSRDttm(null);
				log.info("END calculateStoreBillCharge DAO  finalChargeableBillList: "
						+ finalChargeableBillList.toString());
				return finalChargeableBillList;
			}
			log.info("END calculateStoreBillCharge DAO  chargeableBillList: " + chargeableBillList.toString());
			return chargeableBillList;
		} catch (BusinessException e) {
			log.info("calculateStoreBillCharge Exception " + e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			try {
				billErrorValueObject = new BillErrorVO();
				billErrorValueObject.setRunInd(billErrorValueObject.RUN_IND_CREATE_BILL);
				billErrorValueObject.setTariffMainCat(tariffMainCat);
				billErrorValueObject.setTariffSubCat(tariffSubCat);
				billErrorValueObject.setRemarks("Exception occurred for ~ref_nbr (" + refNbr + ") ~ref_ind (" + refInd
						+ ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = " + e.getMessage());
				// insert into Bill Error table
				insertBillError(billErrorValueObject);
				if (e instanceof ProcessChargeException) {
					throw e;
				} else {
					throw new Exception("Exception occurred for ~ref_nbr (" + refNbr + ") ~ref_ind (" + refInd
							+ ") ~Msg = " + e.getMessage());
				}
			} catch (Exception billErrorException) {
				log.info("[CAB] [ProcessGBStoreEJB Error] >> Inserting billError exception = "
						+ billErrorException.getMessage() + "Exception occurred for ~ref_nbr (" + refNbr
						+ ") ~ref_ind (" + refInd + ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = "
						+ e.getMessage());
				throw new Exception("[ProcessGBStoreEJB Error] >> Inserting billError exception = "
						+ billErrorException.getMessage() + "Exception occurred for ~ref_nbr (" + refNbr
						+ ") ~ref_ind (" + refInd + ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = "
						+ e.getMessage());
			}

		}
	}

	// package: ejb.sessionBeans.cab.gbEventLogRepo-->ProcessGBLogEJB
	// method: calculateWharfageBillCharge()
	/**
	 * Calling method that calls the specific ProcessWharfEJB to do calculation of
	 * the wharfage bill charge before insertion into the gb_bill_charge table
	 *
	 * @param GeneralEventLogValueObject
	 * @param String
	 * @return ChargeableBillValueObject
	 * @exception NamingException
	 * @exception SQLException
	 * @exception ProcessChargeException
	 * @exception Exception
	 */
	// replaced with ProcessWharfEJB ->calculateWharfageBillCharge
	private ChargeableBillValueObject calculateWharfageBillCharge(GeneralEventLogValueObject generalEventLogValueObject,
			String refInd) throws NamingException, SQLException, ProcessChargeException, Exception {
		ChargeableBillValueObject chargeableBillValueObject = null;

		BillErrorVO billErrorValueObject = null;
		VesselRelatedValueObject vesselRelatedValueObject = null;
		BerthRelatedValueObject berthRelatedValueObject = null;
		TariffMainVO tariffMainValueObject = null;
		AccountValueObject accountValueObject = null;

		Timestamp varDttm = null;
		int versionNbr = 0;

		String refNbr = null;
		String discVvCd = null;
		String loadVvCd = null;
		String vvInd = null;
		String businessType = null;
		String schemeCd = null;
		String tariffMainCat = null;
		String tariffSubCat = null;
		String mvmt = null;
		String type = null;
		String cargoType = null;
		String localLeg = null;
		String discGateway = null;
		String billInd = null;
		Timestamp lastModifyDttm = null;

		String varVvCd = null;
		String billAcctNbr = null;
		double minTon = 0.0;
		double billTon = 0.0;
		double deriveTon = 0.0;

		double billTonBl = 0.0;
		double billTonEdo = 0.0;
		double billTonDn = 0.0;
		double billTonEsn = 0.0;
		double loadTonCs = 0.0;
		double shutoutTonCs = 0.0;
		int countUnit = 0;
		int totalPkgEdo = 0;
		int totalPkgDn = 0;

		boolean isFirstDNForEDO = false;

		// EVM Enhancements
		String edoEsnNbr = null;
		String blNbr = null;

		try {
			log.info("START calculateWharfageBillCharge DAO :: generalEventLogValueObject: "
					+ CommonUtility.deNull(generalEventLogValueObject.toString()) + " refInd: "
					+ CommonUtility.deNull(refInd));
			// get the instance of ChargeableBillValueObject
			chargeableBillValueObject = new ChargeableBillValueObject();
			// get all the relevant fields for determining the tariff and
			// calculations
			refNbr = determineRefNbr(generalEventLogValueObject, refInd);
			discVvCd = generalEventLogValueObject.getDiscVvCd();
			loadVvCd = generalEventLogValueObject.getLoadVvCd();
			vvInd = generalEventLogValueObject.getVvInd();
			businessType = generalEventLogValueObject.getBusinessType();
			schemeCd = generalEventLogValueObject.getSchemeCd();
			tariffMainCat = generalEventLogValueObject.getTariffMainCatCd();
			tariffSubCat = generalEventLogValueObject.getTariffSubCatCd();
			mvmt = generalEventLogValueObject.getMvmt();
			type = generalEventLogValueObject.getType();
			cargoType = generalEventLogValueObject.getCargoType();
			log.info("cargoType = " + cargoType.toString());
			localLeg = generalEventLogValueObject.getLocalLeg();
			discGateway = generalEventLogValueObject.getDiscGateway();
			log.info("discGateway = " + discGateway.toString());
			billAcctNbr = generalEventLogValueObject.getBillAcctNbr();
			billInd = generalEventLogValueObject.getBillInd();
			lastModifyDttm = generalEventLogValueObject.getLastModifyDttm();
			blNbr = generalEventLogValueObject.getBlNbr();

			log.info("[CAB]" + "generalEventLogValueObject.getIsFirstDNForEDO(): "
					+ generalEventLogValueObject.getIsFirstDNForEDO());

			if (generalEventLogValueObject.getIsFirstDNForEDO() != null
					&& "Y".equals(generalEventLogValueObject.getIsFirstDNForEDO())) {
				isFirstDNForEDO = true;
				log.info("[CAB]" + "isFirstDNForEDO-1: " + isFirstDNForEDO);
			}

			log.info("[CAB]" + "isFirstDNForEDO-2: " + isFirstDNForEDO);

			/*
			 * if (vvInd.equals(ProcessChargeConst.DISC_VV_IND)) { varVvCd = discVvCd; }
			 */// <cfg amend, 05.aug.09>
			log.info("[CAB]" + "CR64 [WF] <==========================================================");
			log.info("[CAB]" + "CR64 [WF] vvInd = (" + vvInd + "), mvmt = (" + mvmt + ")," + tariffMainCat + "/"
					+ tariffSubCat);
			log.info("[CAB]" + "CR64 [WF] schemeCd = (" + schemeCd + "), localLeg = (" + localLeg + "),disc:" + discVvCd
					+ "/load:" + loadVvCd);
			log.info("[CAB]" + "SL97 [WFTS] <==========================================================");
			log.info("[CAB]" + "SL97 [WFTS] type" + type);

			log.info("[CAB]" + "refInd:" + refInd + ", schemeCd: " + schemeCd + " mvmt: " + mvmt);

			if (vvInd.equals(ProcessChargeConst.DISC_VV_IND)) {
				if (loadVvCd != null && !loadVvCd.equals("")) { // <cfg added, SL97, 16.apr.2010/>
					if (mvmt.trim().equalsIgnoreCase(ProcessChargeConst.MVMT_TRANSHIP)
							&& schemeCd.equalsIgnoreCase(ProcessChargeConst.LINER_SCHEME) && // <cfg accdg to user,
																								// 10.nov.09>
							(tariffMainCat.trim().equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_WHARFAGE)
									&& tariffSubCat.trim()
											.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WHARF_GENERAL))
							&& !type.trim().equalsIgnoreCase(ProcessChargeConst.TYPE_TS_DELIVER_LOCALLY)) {// <cfg
																											// added,
																											// SL97,
																											// 16.apr.2010/>
						// MCC if load vessel scheme is JCL then use discVvCd as varVVCd
						String loadVslScheme = determineLoadVslSch(loadVvCd);
						if (loadVslScheme != null && loadVslScheme.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME)) {
							log.info("[CAB]" + "CR64 [WF] using discVvCd for JLR to JCL TS: " + discVvCd);
							varVvCd = discVvCd;
							edoEsnNbr = generalEventLogValueObject.getEdoAsnNbr(); // EVM ENhancements
						} else {
							log.info("[CAB]" + "CR64 [WF] using loadVvCd: " + loadVvCd + " instead of discVvCd: "
									+ discVvCd);
							varVvCd = loadVvCd;
							edoEsnNbr = generalEventLogValueObject.getEsnAsnNbr(); // EVM ENhancements
						}
					} else {
						log.info("[CAB]" + "CR64 [WF] using discVvCd: " + discVvCd);
						varVvCd = discVvCd;
						edoEsnNbr = generalEventLogValueObject.getEdoAsnNbr(); // EVM ENhancements
					}
				} else { // <cfg added, SL97, 16.apr.2010>
					log.info("[CAB]" + "SL97 [WFTS]loadVvcd is null; using discVvCd: " + discVvCd);
					varVvCd = discVvCd;
					edoEsnNbr = generalEventLogValueObject.getEdoAsnNbr(); // EVM ENhancements
				}
				// </cfg 05.aug.09>
			} else if (vvInd.equals(ProcessChargeConst.LOAD_VV_IND)) {
				varVvCd = loadVvCd;
				edoEsnNbr = generalEventLogValueObject.getEsnAsnNbr(); // EVM ENhancements
			}
			// Added fixes for TESN JP JP case to get the hs code for edo esn and not esn
			// esn
			if (discVvCd != null && loadVvCd != null && vvInd.equals(ProcessChargeConst.DISC_VV_IND)) {
				edoEsnNbr = generalEventLogValueObject.getEdoAsnNbr();
				log.info("[CAB]" + "TESN JP JP Fixes Disc and Load Vv_cd is not null:" + edoEsnNbr);
			}
			log.info("[CAB]" + "CR64 [WF] ========================>" + discVvCd + ":" + loadVvCd + vvInd);

			// To set the scheme code to Not applicable for RORO Vessel & Animal
			// Wharfage
			if ((tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL))
					|| (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_WHARF_ANIMAL))) {
				schemeCd = ProcessChargeConst.NOT_APPLICABLE_SCHEME;
			}

			vesselRelatedValueObject = new VesselRelatedValueObject();
			vesselRelatedValueObject.setVvCd(varVvCd);

			// Added for New Combi EVM Enhancements Nov 2018
			vesselRelatedValueObject = processGBLogJdbcRepository.retrieveVesselCallDtl(varVvCd);

			// retrieve the atb timestamp for further processing
			try {
				berthRelatedValueObject = processGenericRepo.retrieveBerthDttm(vesselRelatedValueObject, 1,
						tariffMainCat);
				varDttm = berthRelatedValueObject.getAtbDttm();
				log.info("[CAB]" + "atb_dttm = " + varDttm + "...");
			} catch (Exception e) {
			}

			if (varDttm == null) {
				varDttm = UserTimestampVO.getCurrentTimestamp();
			}

			// retrieve the relevant tariff version number based on the
			// timestamp passed in
			versionNbr = processGenericRepo.retrieveTariffVersion(varDttm, vesselRelatedValueObject, tariffMainCat);
			log.info("[CAB]" + "version nbr = " + versionNbr + "...");

			// retrieve the published tariff
			// Change by Irene Tan on 10/12/2002 : CTCAB20020044 - To crater for
			// GST changes w.e.f 1 Jan 2003
			// tariffMainValueObject =
			// processGeneric.retrievePublishTariffDtls(versionNbr,
			// tariffMainCat, tariffSubCat, businessType, schemeCd, mvmt, type,
			// "0", "0");

			// New EVM Enhancements Start Sripriya 14 Feb 2018 only required for General
			// Cargo Wharfage
			// New method as this is to cater for Steel & GC(Mixed) Steel to go to Steel &
			// GC to go to GC
			String hscode = processGenericRepo.getHSCode(edoEsnNbr, localLeg, blNbr, varVvCd);
			log.info("[CAB]" + " PRocess Wharf calculateWharfageBillCharge after retrieve hsCode" + hscode + ":"
					+ edoEsnNbr + ":" + localLeg + ":" + blNbr + ":" + varVvCd);
			// New COmbi EVM Enhancements Nov 2018
			String terminal = vesselRelatedValueObject.getTerminal();
			if (terminal.equalsIgnoreCase(ProcessChargeConst.CONTAINER_TERMINAL)) {
				businessType = ProcessChargeConst.CONTAINER_BUSINESS;

			} else if (hscode != null && !hscode.equalsIgnoreCase("")) {
				// EVM 2019 To exclude JWP & JCL scheme to not classify under Steel
				log.info("[CAB]"
						+ "EVM 2019 Process Wharf before determine business type for Steel:schemecd: BusinessType"
						+ schemeCd + businessType);

				if (!(schemeCd.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME)
						|| schemeCd.equalsIgnoreCase(ProcessChargeConst.WOODEN_CRAFT_SCHEME))) {
					businessType = processGenericRepo.getBusTypeForWFSCSR(berthRelatedValueObject, hscode);
				} else {
					businessType = ProcessChargeConst.GENERAL_BUSINESS;
				}
				log.info("[CAB]" + "EVM 2019 Process Wharf A determine business type for Steel:schemecd: BusinessType"
						+ schemeCd + businessType);

			}
			// reset the business type back
			generalEventLogValueObject.setBusinessType(businessType); // Added to reset the business type back
			log.info("[CAB]" + " PRocess Wharf calculateWharfageBillCharge inside if after set business TYpe:"
					+ businessType + ":" + generalEventLogValueObject.getBusinessType());

			log.info("[CAB]" + " PRocess Wharf calculateWharfageBillCharge outside if after set business TYpe:"
					+ businessType + ":" + generalEventLogValueObject.getBusinessType());
			// New EVM Enhancements Start Sripriya 14 Feb 2018

			tariffMainValueObject = processGenericRepo.retrievePublishTariffDtls(versionNbr, tariffMainCat,
					tariffSubCat, businessType, schemeCd, mvmt, type, "0", "0", varDttm);
			log.info("[CAB]" + " PRocess wharf gb getTarrifCode:" + tariffMainValueObject.getCode());

			// End Change by Irene Tan
			billTonBl = generalEventLogValueObject.getBillTonBl();
			log.info("[CAB]" + "billTonBl:" + billTonBl);
			billTonEdo = generalEventLogValueObject.getBillTonEdo();
			log.info("[CAB]" + "billTonEdo:" + billTonEdo);
			billTonDn = generalEventLogValueObject.getBillTonDn();
			log.info("[CAB]" + "billTonDn:" + billTonDn);
			billTonEsn = generalEventLogValueObject.getBillTonEsn();
			log.info("[CAB]" + "billTonEsn:" + billTonEsn);
			loadTonCs = generalEventLogValueObject.getLoadTonCs();
			log.info("[CAB]" + "loadTonCs:" + loadTonCs);
			shutoutTonCs = generalEventLogValueObject.getShutoutTonCs();
			log.info("[CAB]" + "shutoutTonCs:" + shutoutTonCs);
			countUnit = generalEventLogValueObject.getCountUnit();
			log.info("[CAB]" + "countUnit:" + countUnit);
			totalPkgEdo = generalEventLogValueObject.getTotalPackEdo();
			log.info("[CAB]" + "totalPkgEdo:" + totalPkgEdo);
			totalPkgDn = generalEventLogValueObject.getTotalPackDn();
			log.info("[CAB]" + "totalPkgDn:" + totalPkgDn);

			schemeCd = generalEventLogValueObject.getSchemeCd();
			log.info("[CAB]" + "schemeCd:" + schemeCd);

			// compute billable tonnage
			if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_WHARF_GENERAL)) {

				log.info("[CAB]" + "Tariff Sub Cat:" + tariffSubCat);
				log.info("[CAB]" + "refInd:" + refInd);

				// <CFG> Automation of Warehouse Billing 21 Aug 2007
				if ((refInd.trim().equals(ProcessChargeConst.REF_IND_EDO)) && (generalEventLogValueObject.getDnNbr()
						.trim().equals(generalEventLogValueObject.getEdoAsnNbr().trim())) && // to cater for warehouse
																								// billing
																								// <cfg: add new scheme
																								// for Wooden Craft:
																								// JWP,
																								// 27.may.08>
																								// (schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)))
																								// {
						((schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)
								|| schemeCd.trim().equalsIgnoreCase(ProcessChargeConst.WOODEN_CRAFT_SCHEME)))) {
					// Printing of DN - Local Import / Transhipment / ITH for
					// Liner Vessel
					billTon = processGBStoreRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
					log.info("[CAB]" + "CFG: JLR/JWP billTon = " + billTon + "...");

				} else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_EDO)) && (generalEventLogValueObject
						.getDnNbr().trim().equals(generalEventLogValueObject.getEdoAsnNbr().trim())) && // to cater for
																										// warehouse
																										// billing
						((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME)) || (schemeCd.trim()
								// add new scheme for LCT, 17.feb.11 by hpeng
								.equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) {
					if ((mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))
							|| (mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)
									&& type.trim().equals(ProcessChargeConst.TYPE_TS_DELIVER_LOCALLY))) {
						// Printing of DN - Local Import for Non-Liner Vessel
						billTon = processGBStoreRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
						log.info("[CAB]" + "CFG: JNL/JBT billTon = " + billTon + "...");

					} else {

						billTon = billTonBl;
						log.info("[CAB]"
								+ "CFG: ***** Unknown scenario in ProcessWharfEJB for warehouse billing : billTon = "
								+ billTon + "...");
					}
				} else
				// </CFG> 21 Aug 2007

				if ((refInd.trim().equals(ProcessChargeConst.REF_IND_DN)) &&
				// <cfg: add new scheme for Wooden Craft: JWP,
				// 27.may.08>
				// (schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)))
				// {
						((schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)
								|| schemeCd.trim().equalsIgnoreCase(ProcessChargeConst.WOODEN_CRAFT_SCHEME)))) {
					// Printing of DN - Local Import / Transhipment / ITH for
					// Liner Vessel
					billTon = processGBStoreRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);

					log.info("[CAB]" + "Referencing DN for Liner Scheme BillTon:" + billTon);

				} else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_DN))
						&& ((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME)) || (schemeCd.trim()
								// add new scheme for LCT, 17.feb.11 by hpeng
								.equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) {
					// amended by swho on 24/10/2002 for catering for NL TS
					// delivered locally
					// if (mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL) ) {
					if ((mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))
							|| (mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)
									&& type.trim().equals(ProcessChargeConst.TYPE_TS_DELIVER_LOCALLY))) {
						// end amend by swho
						// Printing of DN - Local Import for Non-Liner Vessel
						billTon = processGBStoreRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
					} else {
						billTon = billTonBl;
					}

					log.info("[CAB]" + "Referencing DN for Non-Liner, Barter Trader, LCT Scheme BillTon:" + billTon);

				} // MCC adding for 1st DN
				else if (isFirstDNForEDO && refInd.trim().equals(ProcessChargeConst.REF_IND_EDO)
						&& ((schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.WOODEN_CRAFT_SCHEME)))) {
					if (mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL)) {
						// EDO billable event - Local Import
						billTon = billTonEdo;
					}

					log.info("[CAB]" + "isFirstDNForEDO:" + isFirstDNForEDO);
					log.info("[CAB]" + "Referencing EDO BillTon:" + billTon);
				}

				else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_BL))
						&& ((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME)) || (schemeCd.trim()
								// add new scheme for LCT, 17.feb.11 by hpeng
								.equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) {
					// At closing of BJ - Transhipment or ITH for Non-Liner
					// Vessel
					billTon = billTonBl;

					log.info("[CAB]" + "Referencing BL BillTon:" + billTon);

				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) {
					// At the printing of UA OF 1st UA
					billTon = billTonEsn;

					log.info("[CAB]" + "Referencing UA BillTon:" + billTon);
				}

				// else if (refInd.trim().equals(ProcessChargeConst.REF_IND_BR))
				// {
				else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_ESN))
						|| (refInd.trim().equals(ProcessChargeConst.REF_IND_EDO))) {
					// At Close Shipment
					billTon = loadTonCs;

					log.info("[CAB]" + "Referencing ESN BillTon:" + billTon);

				} else {
					// throw new ProcessChargeException ("Processing of General
					// Wharfage does not have any reference indicator met -
					// ~ref_ind = " + refInd + "!");
					throw new ProcessChargeException(
							"#Processing of General Wharfage does not have any reference indicator met#");
				}
			} else if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_WHARF_SHUTOUT)) {

				// MCC DN is the first DN for shut-out then charge for entire tonnage with EDO
				if ((refInd.trim().equals(ProcessChargeConst.REF_IND_EDO) && isFirstDNForEDO)) {
					billTon = billTonEdo;
				} else
				// To crater when the Shutout function is up
				// add by hujun on 4/8/2011
				if (refInd.trim().equals(ProcessChargeConst.REF_IND_DN)) {
					billTon = processGBStoreRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
				}

				log.info("[CAB]" + "In Shutout BillTon:" + billTon);
				// add end
			} else if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_WHARF_SHIP_STORE)) {
				if (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) {
					// At printing of UA
					billTon = billTonEsn;
					if (billTon < 1 && billTon > 0)
						billTon = 1;// Added by Jade for CR-CAB-20130225-001 minimum 1 ton for ship store
				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_ESN)) {
					// At Close Shipment
					billTon = loadTonCs;
					if (billTon < 1 && billTon > 0)
						billTon = 1;// Added by Jade for CR-CAB-20130225-001 minimum 1 ton for ship store
				} else {
					// throw new ProcessChargeException ("Processing of Ship
					// Stores Wharfage should only be at Printing of UA or Close
					// Shipment - ~ref_ind = " + refInd + "!");
					throw new ProcessChargeException(
							"#Processing of Ship Stores Wharfage should only be at Printing of UA or Close Shipment#");
				}
				log.info("[CAB]" + "Referencing ShipStore BillTon:" + billTon);
			} else if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_WHARF_OVERSIDE)) {
				// at closing of BJ for discharing carrier, at close shipment
				// for loading carrier
				if (mvmt.trim().equals(ProcessChargeConst.NOT_APPLICABLE_MVMT)) {
					if (vvInd.trim().equals(ProcessChargeConst.DISC_VV_IND)) {
						billTon = billTonEdo;
					} else if (vvInd.trim().equals(ProcessChargeConst.LOAD_VV_IND)) {
						// Changes by Irene Tan on 18 Dec 2002 : To crater
						// triggering of wharfage charge at printing of UA
						// billTon = loadTonCs;
						if (refInd.trim().equals(ProcessChargeConst.REF_IND_ESN)) {
							billTon = loadTonCs;
						} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) {
							billTon = billTonEsn;
						}
						// End Changes by Irene Tan
					}
				} else {
					throw new ProcessChargeException(
							"#Processing of Overside Wharfage should not have any movement indication!#");
				}
				log.info("[CAB]" + "Referencing Overside BillTon:" + billTon);
			} else if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL)) {
				System.out.println("Thachpn prcessswharfageEJB+++++++ totalPkgEdo=" + totalPkgEdo);
				System.out.println("Thachpn prcessswharfageEJB+++++++ totalPkgDn=" + totalPkgDn);
				// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08>
				// if (schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME))
				// {
				if (schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)
						|| schemeCd.trim().equalsIgnoreCase(ProcessChargeConst.WOODEN_CRAFT_SCHEME)
						|| schemeCd.trim().equalsIgnoreCase(ProcessChargeConst.NON_LINER_SCHEME)
						|| schemeCd.trim().equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME)) { // MCC add non-liner
																								// vessel
					// ThachPhung starts on 26-Dec-08
					// added new code to calculate by tone basis.
					billTon = calculateBillTonForSubROROVsl(generalEventLogValueObject, refInd, isFirstDNForEDO);
					// ThachPhung end
				} else {
					throw new ProcessChargeException("#Processing of RORO Wharfage is not allowed !#"); // MCC
				}

				log.info("[CAB]" + "Referencing RORO BillTon:" + billTon);
			} else if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_WHARF_ANIMAL)) {
				// if (mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL)) {
				billTon = countUnit;
				log.info("[CAB]" + "Referencing Animal BillTon:" + billTon);
				/*
				 * } else { throw new ProcessChargeException ("Processing of Animal Wharfage is
				 * not allow for Transhipment of Animals!"); }
				 */
			}
			// Added by Irene Tan on 6/1/2003: To crater for the processing of
			// special cargo category
			else if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_WHARF_EXPORT)) {
				if (refInd.trim().equals(ProcessChargeConst.REF_IND_ESN)) {
					billTon = loadTonCs;
				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) {
					billTon = billTonEsn;
				}

				log.info("[CAB]" + "Referencing Export BillTon:" + billTon);
			}
			// End Added by Irene Tan on 6/1/2003

			if (billAcctNbr.trim().equals(ProcessChargeConst.CASH_CUST_CD)) {
				minTon = tariffMainValueObject.getMinTonnage();
				log.info("[CAB]" + "minTon:" + minTon);
				if (billTon < minTon) {
					deriveTon = minTon;
				} else {
					deriveTon = billTon;
				}
			} else {
				deriveTon = billTon;
			}

			log.info("[CAB]" + "deriveTon:" + deriveTon);

			// MCConsulting min 1 billable ton for all general cargo charges
			if (deriveTon < 1 && deriveTon > 0)
				deriveTon = 1;

			// set the parameters for the ChargeableBillValueObject
			chargeableBillValueObject.setRefNbr(refNbr);
			chargeableBillValueObject.setTxnDttm(lastModifyDttm);
			chargeableBillValueObject.setRefInd(refInd);
			chargeableBillValueObject.setTariffMainCatCd(tariffMainCat);
			chargeableBillValueObject.setTariffSubCatCd(tariffSubCat);
			chargeableBillValueObject.setVersionNbr(versionNbr);
			chargeableBillValueObject.setCustCd("");
			chargeableBillValueObject.setAcctNbr("");
			chargeableBillValueObject.setContractNbr("");
			chargeableBillValueObject.setContractualYr(0);
			chargeableBillValueObject.setNbrCntr(0);
			chargeableBillValueObject.setVvCd(varVvCd);
			chargeableBillValueObject.setBillInd(billInd);
			chargeableBillValueObject.setLastModifyUserId("SYSTEM");
			chargeableBillValueObject.setLastModifyDttm(lastModifyDttm);
			chargeableBillValueObject.setLocalLeg(localLeg);

			chargeableBillValueObject = determineWharfBillable(chargeableBillValueObject, tariffMainValueObject,
					deriveTon);

			// get customer a/c & determine customer code
			if (billAcctNbr.trim().equals(ProcessChargeConst.CASH_CUST_CD)) {
				accountValueObject = processGenericRepo.retrieveCashCustAcct();
			} else {
				accountValueObject = processGenericRepo.retrieveCustAcct(null, billAcctNbr);
			}
			chargeableBillValueObject.setAcctNbr(accountValueObject.getAccountNumber());
			chargeableBillValueObject.setCustCd(accountValueObject.getCustomerCode());
		} catch (BusinessException e) {
			log.info("calculateWharfageBillCharge Exception " + e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			try {
				billErrorValueObject = new BillErrorVO();
				billErrorValueObject.setRunInd(billErrorValueObject.RUN_IND_CREATE_BILL);
				billErrorValueObject.setTariffMainCat(tariffMainCat);
				billErrorValueObject.setTariffSubCat(tariffSubCat);
				billErrorValueObject.setRemarks("Exception occurred for ~ref_nbr (" + refNbr + ") ~ref_ind (" + refInd
						+ ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = " + e.getMessage());
				// insert into Bill Error table
				insertBillError(billErrorValueObject);

				if (e instanceof ProcessChargeException) {
					throw e;
				} else {
					throw new Exception("Exception occurred for ~ref_nbr (" + refNbr + ") ~ref_ind (" + refInd
							+ ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = " + e.getMessage());
				}
			} catch (Exception billErrorException) {
				log.info("[CAB]" + "[ProcessWharfEJB Error] >> Inserting billError exception = "
						+ billErrorException.getMessage() + "Exception occurred for ~ref_nbr (" + refNbr
						+ ") ~ref_ind (" + refInd + ") ~Msg = " + e.getMessage());
				throw new Exception(
						"[ProcessWharfEJB Error] >> Inserting billError exception = " + billErrorException.getMessage()
								+ "Exception occurred for ~ref_nbr (" + refNbr + ") ~ref_ind (" + refInd + ") ~vv_cd ("
								+ varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = " + e.getMessage());
			}

		}
		return chargeableBillValueObject;
	}

	// package: ejb.sessionBeans.cab.gbEventLogRepo-->ProcessGBLogEJB
	// method: calculateServiceBillCharge()
	/**
	 * Calling method that calls the specific ProcessServiceEJB to do calculation of
	 * the service bill charge before insertion into the gb_bill_charge table
	 *
	 * @param GeneralEventLogValueObject
	 * @param String
	 * @return ChargeableBillValueObject
	 * @exception NamingException
	 * @exception SQLException
	 * @exception ProcessChargeException
	 * @exception Exception
	 */
	// Replaced with processserviceEjb -> calculateServiceBillCharge
	private ChargeableBillValueObject calculateServiceBillCharge(GeneralEventLogValueObject generalEventLogValueObject,
			String refInd) throws NamingException, SQLException, ProcessChargeException, Exception {
		ChargeableBillValueObject chargeableBillValueObject = null;
		BillErrorVO billErrorValueObject = null;

		VesselRelatedValueObject vesselRelatedValueObject = null;
		BerthRelatedValueObject berthRelatedValueObject = null;
		TariffMainVO tariffMainValueObject = null;
		AccountValueObject accountValueObject = null;
		Timestamp varDttm = null;
		int versionNbr = 0;

		String refNbr = null;
		String discVvCd = null;
		String loadVvCd = null;
		String vvInd = null;
		String businessType = null;
		String schemeCd = null;
		String tariffMainCat = null;
		String tariffSubCat = null;
		String mvmt = null;
		String type = null;
		String cargoType = null;
		String localLeg = null;
		String discGateway = null;

		String varVvCd = null;
		double minTon = 0.0;
		double billTon = 0.0;
		double deriveTon = 0.0;

		double billTonBl = 0.0;
		double billTonEdo = 0.0;
		double billTonEsn = 0.0;
		double loadTonCs = 0.0;
		int countUnit = 0;
		int totalPkgEdo = 0;
		int totalPkgDn = 0;
		String billAcctNbr = null;
		String billInd = null;
		Timestamp lastModifyDttm = null;

		boolean isFirstDNForEDO = false;

		// EVM Enhancements
		String edoEsnNbr = null;
		String blNbr = null;

		try {
			log.info("START calculateServiceBillCharge DAO :: generalEventLogValueObject: "
					+ CommonUtility.deNull(generalEventLogValueObject.toString()) + " refInd: "
					+ CommonUtility.deNull(refInd));
			// calculates the service charges based on the event log and returns result
			// based on
			// published rates
			// chargeableBillValueObject =
			// calculateServiceBillCharge(generalEventLogValueObject, refInd);

			log.info("[CAB]" + "calculateServiceBillCharge  starting.");
			// get the instance of ProcessCommon
			// processCommon = new ProcessCommon();
			// get the instance of ChargeableBillValueObject
			chargeableBillValueObject = new ChargeableBillValueObject();

			// get all the relevant fields for determining the tariff and calculations
			refNbr = determineRefNbr(generalEventLogValueObject, refInd);
			discVvCd = generalEventLogValueObject.getDiscVvCd();
			loadVvCd = generalEventLogValueObject.getLoadVvCd();
			vvInd = generalEventLogValueObject.getVvInd();
			businessType = generalEventLogValueObject.getBusinessType();
			schemeCd = generalEventLogValueObject.getSchemeCd();
			tariffMainCat = generalEventLogValueObject.getTariffMainCatCd();
			tariffSubCat = generalEventLogValueObject.getTariffSubCatCd();
			mvmt = generalEventLogValueObject.getMvmt();
			type = generalEventLogValueObject.getType();
			cargoType = generalEventLogValueObject.getCargoType();
			localLeg = generalEventLogValueObject.getLocalLeg();
			discGateway = generalEventLogValueObject.getDiscGateway();
			billTonBl = generalEventLogValueObject.getBillTonBl();
			billTonEdo = generalEventLogValueObject.getBillTonEdo();
			billTonEsn = generalEventLogValueObject.getBillTonEsn();
			loadTonCs = generalEventLogValueObject.getLoadTonCs();
			countUnit = generalEventLogValueObject.getCountUnit();
			totalPkgEdo = generalEventLogValueObject.getTotalPackEdo();
			totalPkgDn = generalEventLogValueObject.getTotalPackDn();
			billAcctNbr = generalEventLogValueObject.getBillAcctNbr();
			billInd = generalEventLogValueObject.getBillInd();
			lastModifyDttm = generalEventLogValueObject.getLastModifyDttm();
			blNbr = generalEventLogValueObject.getBlNbr();

			log.info("discGateway = " + discGateway.toString());

			// EVM Enhancements
			if (localLeg.equalsIgnoreCase("IM")) {
				edoEsnNbr = generalEventLogValueObject.getEdoAsnNbr();
			} else {
				edoEsnNbr = generalEventLogValueObject.getEsnAsnNbr();
			}
			if (generalEventLogValueObject.getIsFirstDNForEDO() != null
					&& "Y".equals(generalEventLogValueObject.getIsFirstDNForEDO())) {
				isFirstDNForEDO = true;
			}
			// MCC changed - as if condition failed
			/*
			 * if (vvInd.equals(ProcessChargeConst.DISC_VV_IND)) { varVvCd = discVvCd; }
			 * else if (vvInd.equals(ProcessChargeConst.LOAD_VV_IND)) { varVvCd = loadVvCd;
			 * }
			 */
			// Fixed by MCC Combi
			if (ProcessChargeConst.DISC_VV_IND.equals(vvInd)) {
				varVvCd = discVvCd;
			} else if (ProcessChargeConst.LOAD_VV_IND.equals(vvInd)) {
				varVvCd = loadVvCd;
			}
			log.info("[CAB]" + "calculateServiceBillCharge:varVvCd" + varVvCd);
			// Fixed by MCC Combi
			vesselRelatedValueObject = new VesselRelatedValueObject();
			vesselRelatedValueObject.setVvCd(varVvCd);

			// Added for New Combi EVM Enhancements Nov 2018
			vesselRelatedValueObject = retrieveVesselCallDtl(varVvCd);

			// retrieve the atb timestamp for further processing
			try {
				berthRelatedValueObject = processGenericRepo.retrieveBerthDttm(vesselRelatedValueObject, 1,
						tariffMainCat);
				varDttm = berthRelatedValueObject.getAtbDttm();
				log.info("[CAB]" + "atb_dttm = " + varDttm + "...");
			} catch (Exception e) {
				log.info("calculateServiceBillCharge Exception: ", e);
			}

			if (varDttm == null) {
				varDttm = UserTimestampVO.getCurrentTimestamp();
			}

			// retrieve the relevant tariff version number based on the timestamp passed in
			versionNbr = processGenericRepo.retrieveTariffVersion(varDttm, vesselRelatedValueObject, tariffMainCat);
			log.info("[CAB]" + "version nbr = " + versionNbr + "...");

			// ThachPhung starts on 27-Dec-08
			// To set the scheme code to Not applicable for RORO Vessel
			if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL)) {
				schemeCd = ProcessChargeConst.NOT_APPLICABLE_SCHEME;
			}
			// ThachPhung end
			// New EVM Enhancements Start Sripriya 14 Feb 2018 only required for General
			// Cargo Service Charge
			// New method as this is to cater for Steel & GC(Mixed) Steel to go to Steel &
			// GC to go to GC
			String hscode = processGenericRepo.getHSCode(edoEsnNbr, localLeg, blNbr, varVvCd);
			log.info("[CAB]" + " PRocess Service calculateService after retrieve hsCode" + hscode + ":" + edoEsnNbr
					+ ":" + localLeg + ":" + blNbr + ":" + varVvCd);
			// New COmbi EVM Enhancements Nov 2018
			String terminal = vesselRelatedValueObject.getTerminal();
			if (terminal.equalsIgnoreCase(ProcessChargeConst.CONTAINER_TERMINAL)) {
				businessType = ProcessChargeConst.CONTAINER_BUSINESS;
			} else {
				if (hscode != null && !hscode.equalsIgnoreCase("")) {
					// EVM 2019 To exclude JWP & JCL scheme to classify under Steel
					log.info("[CAB]"
							+ "EVM 2019 Process Wharf before determine business type for Steel:schemecd: BusinessType"
							+ schemeCd + businessType);

					if (!(schemeCd.equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME)
							|| schemeCd.equalsIgnoreCase(ProcessChargeConst.WOODEN_CRAFT_SCHEME))) {
						businessType = processGenericRepo.getBusTypeForWFSCSR(berthRelatedValueObject, hscode);
					} else {
						businessType = ProcessChargeConst.GENERAL_BUSINESS;
					}
					log.info("[CAB]"
							+ "EVM 2019 Process Wharf A determine business type for Steel:schemecd: BusinessType"
							+ schemeCd + businessType);
				}
			}

			// reset the business type back
			generalEventLogValueObject.setBusinessType(businessType); // Added to reset the business type back

			log.info("[CAB]" + " PRocess Service calculateService outside if after set business TYpe:" + businessType
					+ ":" + generalEventLogValueObject.getBusinessType());
			// New EVM Enhancements Start Sripriya 14 Feb 2018
			// retrieve the published tariff
			// Change by Irene Tan on 10/12/2002 : CTCAB20020044 - To crater for GST changes
			// w.e.f 10/12/2002
			// tariffMainValueObject = processGeneric.retrievePublishTariffDtls(versionNbr,
			// tariffMainCat, tariffSubCat, businessType, schemeCd, mvmt, type, "0", "0");
			tariffMainValueObject = processGenericRepo.retrievePublishTariffDtls(versionNbr, tariffMainCat,
					tariffSubCat, businessType, schemeCd, mvmt, type, "0", "0", varDttm);
			log.info("[CAB]" + " PRocess Service calculateService getTarrifCode:" + tariffMainValueObject.getCode());
			// End Change by Irene Tan
			schemeCd = generalEventLogValueObject.getSchemeCd();// ThachPhung starts on 27-Dec-08, restore schemeCd
			log.info("[CAB]" + "calculateServiceBillCharge:sub cat:" + tariffSubCat);
			// compute bill tonnage
			if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_SERVICE_CHARGE_GENERAL)) {
				log.info("[CAB]" + "calculateServiceBillCharge:sub cat is GL");

				// Added by CFG 21 Aug 2007 - for automation of warehouse billing
				if ((refInd.trim().equals(ProcessChargeConst.REF_IND_EDO)) && (generalEventLogValueObject.getDnNbr()
						.trim().equals(generalEventLogValueObject.getEdoAsnNbr().trim())) &&
				// add new scheme for LCT, 16.feb.11 by hpeng
						((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) {
					if ((mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)
							|| mvmt.trim().equals(ProcessChargeConst.MVMT_ITH)
							|| mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))
							&& (!cargoType.trim().equals(ProcessChargeConst.TYPE_LIVE_STOCK)
									&& !cargoType.trim().equals(ProcessChargeConst.TYPE_WILD_CAGED_ANIMAL))) {
						// Printing of DN - ITH / Local Import (not live stock / wild animals) for
						// Non-Liner vessel
						billTon = transLogRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
						// LogManager.instance.log("[CAB]", "CFG: billTon = " + billTon + "...");

					} else if ((mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))
							&& ((cargoType.trim().equals(ProcessChargeConst.TYPE_LIVE_STOCK))
									|| (cargoType.trim().equals(ProcessChargeConst.TYPE_WILD_CAGED_ANIMAL)))) {
						// Printing of DN - Local Import (live stock / wild animals) for Non-Liner
						// vessel
						billTon = countUnit;
						// LogManager.instance.log("[CAB]", "CFG: billTon (wild animals) = " + billTon +
						// "...");
					}
				} else
				// end CFG 21 Aug 2007
				// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08>
				// if ((refInd.trim().equals(ProcessChargeConst.REF_IND_BL)) &&
				// (schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME))) {
				// MCC include LCT for Service charge to charge in tonnage to manifest
				if ((refInd.trim().equals(ProcessChargeConst.REF_IND_BL))
						&& (schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)
								|| schemeCd.trim().equalsIgnoreCase(ProcessChargeConst.WOODEN_CRAFT_SCHEME)
								|| schemeCd.trim().equalsIgnoreCase(ProcessChargeConst.LCT_SCHEME))) {
					// Closing of BJ - Local Import / Transhipment / ITH for Liner vessel
					billTon = billTonBl;
				}
				// add new scheme for LCT, 16.feb.11 by hpeng
				else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_DN))
						&& ((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) {
					if ((mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP))
							|| (mvmt.trim().equals(ProcessChargeConst.MVMT_ITH))
							|| ((mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))
									&& (!cargoType.trim().equals(ProcessChargeConst.TYPE_LIVE_STOCK))
									&& (!cargoType.trim().equals(ProcessChargeConst.TYPE_WILD_CAGED_ANIMAL)))) {
						// Printing of DN - ITH / Local Import (not live stock / wild animals) for
						// Non-Liner vessel
						billTon = transLogRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
					} else if ((mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))
							&& ((cargoType.trim().equals(ProcessChargeConst.TYPE_LIVE_STOCK))
									|| (cargoType.trim().equals(ProcessChargeConst.TYPE_WILD_CAGED_ANIMAL)))) {
						// Printing of DN - Local Import (live stock / wild animals) for Non-Liner
						// vessel
						billTon = countUnit;
					}
				}
				// MCC adding for 1st DN
				else if (isFirstDNForEDO && refInd.trim().equals(ProcessChargeConst.REF_IND_EDO)
						&& ((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME)))) {
					if (((mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))
							&& (!cargoType.trim().equals(ProcessChargeConst.TYPE_LIVE_STOCK))
							&& (!cargoType.trim().equals(ProcessChargeConst.TYPE_WILD_CAGED_ANIMAL)))) {
						// // EDO billable event - Local Import - ITH / Local Import (not live stock /
						// wild animals) for Non-Liner vessel
						billTon = billTonEdo;
					} else if ((mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))
							&& ((cargoType.trim().equals(ProcessChargeConst.TYPE_LIVE_STOCK))
									|| (cargoType.trim().equals(ProcessChargeConst.TYPE_WILD_CAGED_ANIMAL)))) {
						// // EDO billable event - Local Import - Local Import (live stock / wild
						// animals) for Non-Liner vessel
						billTon = countUnit;
					}
				}
				// else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_BR)) &&
				// ((schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)) ||
				// (((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME)) ||
				// (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))) &&
				// ((mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL)) ||
				// (mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)))))) {
				// else if (refInd.trim().equals(ProcessChargeConst.REF_IND_BR)) {
				else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_ESN))
						|| (refInd.trim().equals(ProcessChargeConst.REF_IND_EDO))) {
					// Close Shipment - Local Import / Transhipment / ITH for Liner vessel; Local
					// Import / Transhipment for Non-Liner vessel
					billTon = loadTonCs;
				}
				// add new scheme for LCT, 16.feb.11 by hpeng
				else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_UA))
						&& ((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) {
					// Printing of UA - Local Import / Transhipment / ITH for Non-Liner vessel
					billTon = billTonEsn;
				}

				else {
					throw new ProcessChargeException(
							"#Processing of General Service Charge does not have any reference indicator met#");
				}
			} else if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_SERVICE_CHARGE_SHIP_STORE)) {
				// if ((refInd.trim().equals(ProcessChargeConst.REF_IND_BR)) &&
				// (mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))) {
				// if (refInd.trim().equals(ProcessChargeConst.REF_IND_BR)) {
				if (refInd.trim().equals(ProcessChargeConst.REF_IND_ESN)) {
					// Close Shipment - Local Import for Liner and Non-Liner vessel
					billTon = loadTonCs;
					if (billTon < 1 && billTon > 0)
						billTon = 1;// Added by Jade for CR-CAB-20130225-001 minimum 1 ton for ship store
				}
				// add new scheme for LCT, 16.feb.11 by hpeng
				else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_UA))
						&& ((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)))) { // MCC add liner scheme
																									// to generate
																									// service charge
																									// for ship store
																									// upon UA creation
					// Printing of UA - Local Import / Transhipment / ITH for Non-Liner vessel
					billTon = billTonEsn;
					if (billTon < 1 && billTon > 0)
						billTon = 1;// Added by Jade for CR-CAB-20130225-001 minimum 1 ton for ship store
				} else {
					throw new ProcessChargeException(
							"#Processing of Ship Store Service Charge should only be at Close Shipment or Printing of UA#");
				}
			} else if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_SERVICE_CHARGE_LAND_RESHIP)) {
				// if ((refInd.trim().equals(ProcessChargeConst.REF_IND_BR)) &&
				// (mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))) {
				// if (refInd.trim().equals(ProcessChargeConst.REF_IND_BR)) {
				if (refInd.trim().equals(ProcessChargeConst.REF_IND_ESN)) {
					// Close Shipment - Local Import for Liner and Non-Liner vessel
					billTon = loadTonCs;
				}
				// add new scheme for LCT, 16.feb.11 by hpeng
				else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_UA))
						&& ((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
								|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) {
					// Printing of UA - Local Import / Transhipment / ITH for Non-Liner vessel
					billTon = billTonEsn;
				} else {
					throw new ProcessChargeException(
							"#Processing of Land and Reship Service Charge should only be at Close Shipment or Printing of UA#");
				}
			}
			// <cfg, SC for Overside Cargo, 31.jul.08>
			else if (tariffSubCat.trim().equals(ProcessChargeConst.TARIFF_SUB_SERVICE_CHARGE_OVERSIDE)) {
				if (schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)
						|| schemeCd.trim().equalsIgnoreCase(ProcessChargeConst.WOODEN_CRAFT_SCHEME)) {
					if (refInd.trim().equals(ProcessChargeConst.REF_IND_BL)) {
						// Closing of BJ - Local Import / Transhipment / ITH for Liner vessel
						billTon = billTonBl;
						// billTon = billTonEdo;
					} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_ESN)) {
						// Close Shipment - Local Export / Transhipment / ITH for Liner vessel
						billTon = loadTonCs;
					}
				} else {
					throw new ProcessChargeException(
							"#Processing of Overside Service Charge should only be at Closing of BJ or Close Shipment for Liner vessel#");
				}
			}
			// ThachPhung starts on 26-Dec-2008
			// calculate service charge by unit basis for vehicle RORO.
			else if (GbmsCommonUtility.isVehicleRORO(type, tariffSubCat)) {
				billTon = countUnit;
			}
			// ThachPhung end
			// <cfg, SC for Overside Cargo, 31.jul.08/>
			deriveTon = billTon;

			// check minimum tonnage for cash customer
			if (billAcctNbr.trim().equals(ProcessChargeConst.CASH_CUST_CD)) {
				minTon = tariffMainValueObject.getMinTonnage();
				// LogManager.instance.log("[CAB]", "minTon = " + minTon);

				if (billTon < minTon) {
					deriveTon = minTon;
				}
			}
			// MCConsulting min 1 billable ton for all general cargo charges
			if (deriveTon < 1 && deriveTon > 0)
				deriveTon = 1;

			log.info("[CAB]" + "CFG 21 Aug 2007 deriveTon = " + deriveTon);

			// set the parameters for the ChargeableBillValueObject
			chargeableBillValueObject.setRefNbr(refNbr);
			chargeableBillValueObject.setTxnDttm(lastModifyDttm);
			chargeableBillValueObject.setRefInd(refInd);
			chargeableBillValueObject.setTariffMainCatCd(tariffMainCat);
			chargeableBillValueObject.setTariffSubCatCd(tariffSubCat);
			chargeableBillValueObject.setVersionNbr(versionNbr);
			chargeableBillValueObject.setCustCd("");
			chargeableBillValueObject.setAcctNbr("");
			chargeableBillValueObject.setContractNbr("");
			chargeableBillValueObject.setContractualYr(0);
			chargeableBillValueObject.setNbrCntr(0);
			chargeableBillValueObject.setVvCd(varVvCd);
			chargeableBillValueObject.setBillInd(billInd);
			chargeableBillValueObject.setLocalLeg(localLeg);
			chargeableBillValueObject.setLastModifyUserId("SYSTEM");
			chargeableBillValueObject.setLastModifyDttm(lastModifyDttm);

			chargeableBillValueObject = determineServiceBillable(chargeableBillValueObject, tariffMainValueObject,
					deriveTon);
			// determine customer code
			if (billAcctNbr.trim().equals(ProcessChargeConst.CASH_CUST_CD)) {
				accountValueObject = processGenericRepo.retrieveCashCustAcct();
			} else {
				accountValueObject = processGenericRepo.retrieveCustAcct(null, billAcctNbr);
			}
			chargeableBillValueObject.setAcctNbr(accountValueObject.getAccountNumber());
			chargeableBillValueObject.setCustCd(accountValueObject.getCustomerCode());
		} catch (BusinessException e) {
			log.info("calculateServiceBillCharge Exception " + e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			try {
				billErrorValueObject = new BillErrorVO();
				billErrorValueObject.setRunInd(BillErrorVO.RUN_IND_CREATE_BILL);
				billErrorValueObject.setTariffMainCat(tariffMainCat);
				billErrorValueObject.setTariffSubCat(tariffSubCat);
				billErrorValueObject.setRemarks("Exception occurred for ~ref_nbr (" + refNbr + ") ~ref_ind (" + refInd
						+ ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = " + e.getMessage());
				// insert into Bill Error table
				insertBillError(billErrorValueObject);
				if (e instanceof ProcessChargeException) {
					throw e;
				} else {
					throw new Exception("Exception occurred for ~ref_nbr (" + refNbr + ") ~ref_ind (" + refInd
							+ ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = " + e.getMessage());
				}
			} catch (ProcessChargeException pce) {
				throw pce;
			} catch (Exception billErrorException) {
				log.info("[CAB]" + "[ProcessServiceEJB Error] >> Inserting billError exception = "
						+ billErrorException.getMessage() + "Exception occurred for ~ref_nbr (" + refNbr
						+ ") ~ref_ind (" + refInd + ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = "
						+ e.getMessage());
				throw new Exception("[ProcessServiceEJB Error] >> Inserting billError exception = "
						+ billErrorException.getMessage() + "Exception occurred for ~ref_nbr (" + refNbr
						+ ") ~ref_ind (" + refInd + ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = "
						+ e.getMessage());
			}

		}
		return chargeableBillValueObject;
	}

	// package: ejb.sessionBeans.cab.gbEventLogRepo-->ProcessGBLogEJB
	// method: isTSLocalDN()
	/**
	 *
	 * @param refInd
	 * @param refNbr
	 * @return boolean. If refNbr is a DN for transhipment cargo delivered locally,
	 *         return true, otherwise false.
	 * @throws ProcessChargeException
	 */
	private boolean isTSLocalDN(String refInd, String refNbr) throws Exception {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		boolean result = false;

		if (!refInd.equalsIgnoreCase("DN")) {
			return false;
		}

		try {
			log.info("START: isTSLocalDN  DAO  Start Obj refInd:" + CommonUtility.deNull(refInd) + "refNbr" + CommonUtility.deNull(refNbr));

			String sql = "select dn_nbr from dn_details where dn_nbr =:refNbr and trans_type = 'T'";

			paramMap.put("refNbr", refNbr);
			log.info(" *** isTSLocalDN SQL *****" + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			String dnNbr = null;
			if (rs != null && rs.next()) {
				dnNbr = rs.getString("dn_nbr");
				log.info("dnNbr = " + dnNbr);
			}
			if (dnNbr != null) {
				result = true;
			}
		} catch (NullPointerException e) {
			log.info("Exception isTSLocalDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isTSLocalDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isTSLocalDN  DAO  END  result: " + result);
		}
		return result;
	}
	// EndRegion ProcessGBLogJdbcRepository

	/**
	 * Retrieves a vessel call record
	 * 
	 * @param vvCd - Vessel Voyage Code
	 * @return VesselRelatedValueObject - Vessel related value object that stores
	 *         the generic vessel information
	 * @exception SQLException - New EVM Enhancements added cement_vsl_ind
	 */
	// cab/processCharges/ProcessGenericEJB.java -> retrieveVesselCallDtl
	@Override
	public VesselRelatedValueObject retrieveVesselCallDtl(String vvCd) throws BusinessException {
		StringBuffer sqlQuery = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		String vslNm = null;
		String inVoyNbr = null;
		String outVoyNbr = null;
		String shpgSvcCd = null;
		String vslOprCd = null;
		String cementVslInd = null;
		String terminal = null;
		SqlRowSet rs = null;

		VesselRelatedValueObject vesselRelatedValueObject = null;

		try {
			log.info("START retrieveVesselCallDtl DAO :: vvCd: " + CommonUtility.deNull(vvCd));
			// formulate the sql query and execute the query
			sqlQuery = new StringBuffer();
			sqlQuery.append(
					"select vv_cd, vsl_nm, in_voy_nbr, out_voy_nbr, shpg_svc_cd, vsl_opr_cd,cement_vsl_ind,terminal  ");
			sqlQuery.append("from vessel_call ");
			sqlQuery.append("where vv_cd=:vvCd ");

			paramMap.put("vvCd", vvCd);
			log.info(" *** isTSLocalDN SQL *****" + sqlQuery.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);
			vesselRelatedValueObject = new VesselRelatedValueObject();

			if (rs.next()) {
				// get the database values
				// get the database values
				vvCd = rs.getString("vv_cd");
				vslNm = rs.getString("vsl_nm");
				inVoyNbr = rs.getString("in_voy_nbr");
				outVoyNbr = rs.getString("out_voy_nbr");
				shpgSvcCd = rs.getString("shpg_svc_cd");
				vslOprCd = rs.getString("vsl_opr_cd");
				cementVslInd = rs.getString("cement_vsl_ind");
				terminal = rs.getString("terminal");

				if (vvCd != null) {
					vvCd = vvCd.trim();
				}
				if (vslNm != null) {
					vslNm = vslNm.trim();
				}
				if (inVoyNbr != null) {
					inVoyNbr = inVoyNbr.trim();
				}
				if (outVoyNbr != null) {
					outVoyNbr = outVoyNbr.trim();
				}
				if (shpgSvcCd != null) {
					shpgSvcCd = shpgSvcCd.trim();
				}
				if (vslOprCd != null) {
					vslOprCd = vslOprCd.trim();
				}
				if (cementVslInd != null) {
					cementVslInd = cementVslInd.trim();
				}
				if (terminal != null) {
					terminal = terminal.trim();
				}

				vesselRelatedValueObject.setVvCd(vvCd);
				vesselRelatedValueObject.setVslNm(vslNm);
				vesselRelatedValueObject.setInVoyNbr(inVoyNbr);
				vesselRelatedValueObject.setOutVoyNbr(outVoyNbr);
				vesselRelatedValueObject.setShpgSvcCd(shpgSvcCd);
				vesselRelatedValueObject.setVslOprCd(vslOprCd);
				vesselRelatedValueObject.setCementVslInd(cementVslInd);
				vesselRelatedValueObject.setTerminal(terminal);
				// New EVM for Combi Enhancements Nov 2018
				if (terminal.equalsIgnoreCase("CT")) {
					vesselRelatedValueObject.setCntrVslInd("Y");
				} else {
					vesselRelatedValueObject.setCntrVslInd("N");
				}
			}
		} catch (Exception e) {
			log.info("[CAB]" + "Exception occurred while retrieving VesselCall details for vv cd = " + vvCd + " : "
					+ e.getMessage());
			throw new BusinessException("Exception occurred >> #Error while retrieving VesselCall details# for ~vv_cd ("
					+ vvCd + ") : " + e.getMessage());
		} finally {
			log.info("END retrieveVesselCallDtl DAO  vesselRelatedValueObject: " + vesselRelatedValueObject);
		}
		return vesselRelatedValueObject;
	}

	/**
	 * Determine the service charges to be billed based on the billable tonnage and
	 * tariff rates
	 * 
	 * @param ChargeableBillValueObject
	 * @param TariffMainVO
	 * @param double
	 * @return ChargeableBillValueObject
	 * @exception ProcessChargeException
	 */
	public ChargeableBillValueObject determineServiceBillable(ChargeableBillValueObject chargeableBillValueObject,
			TariffMainVO tariffMainValueObject, double deriveTon) throws ProcessChargeException {
		Exception ex = null;

		TariffTierVO tariffTierValueObject = null;
		BillAdjustParam billAdjParam = null;

		String tariffCd = null;
		String tariffType = null;
		Integer tierSeqNbr = null;
		String tariffDesc = null;
		double timeUnit = 0;
		double otherUnit = 0;
		double unitRate = 0;
		double gstPercent = 0;
		double itemGst = 0;
		double itemAmt = 0;

		double timeUnitBase = 0;
		double otherUnitBase = 0;
		double origTimeUnit = 0;
		double origOtherUnit = 0;
		String adjType = null;
		double adjAmt = 0;

		int tierCnt = 0;

		try {
			log.info("START determineServiceBillable DAO :: chargeableBillValueObject: " + CommonUtility.deNull(chargeableBillValueObject.toString())
					+ " tariffMainValueObject: " + CommonUtility.deNull(tariffMainValueObject.toString()) + " deriveTon: " + CommonUtility.deNull(String.valueOf(deriveTon)));
			tariffCd = tariffMainValueObject.getCode();
			tariffDesc = tariffMainValueObject.getDescription();
			tariffType = tariffMainValueObject.getTariffTypeInd();
			gstPercent = tariffMainValueObject.getGST();

			// loop thru the tariff tier records to determine what is the chargeable units
			for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
				// get the individual record of the tariff tier record
				tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);

				tierSeqNbr = new Integer(tierCnt + 1);

				// from tariffTierValueObject, get the details for processing
				timeUnitBase = tariffTierValueObject.getPerHour();
				log.info("[CAB]" + "determineServiceBillable:timeUnitBase:" + timeUnitBase);
				if (timeUnitBase < 0) {
					timeUnitBase = 1;
				}

				otherUnitBase = tariffTierValueObject.getPerUnit();
				if (otherUnitBase < 0) {
					otherUnitBase = 1;
				}

				// MCConsulting min 1 billable ton for all general cargo charges
				if (deriveTon < 1 && deriveTon > 0)
					deriveTon = 1;

				log.info("[CAB]" + "Min 1 billable deriveTon = " + deriveTon);

				origTimeUnit = 0;
				timeUnit = (origTimeUnit / timeUnitBase);

				origOtherUnit = deriveTon;
				otherUnit = (origOtherUnit / otherUnitBase);
			} // end for loop

			if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
				unitRate = tariffTierValueObject.getRate();
			} else {
				adjType = tariffTierValueObject.getAdjustmentType();
				adjAmt = tariffTierValueObject.getAdjustment();
				unitRate = tariffTierValueObject.getRate();
				log.info("[CAB]" + "determineServiceBillable:before unitRate:" + unitRate);
				if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_PERCENT)) {
					unitRate = unitRate + (adjAmt / 100 * unitRate);
				} else if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_AMOUNT)) {
					unitRate = unitRate + adjAmt;
				}
				log.info("[CAB]" + "determineServiceBillable:adjType:" + adjType);
				log.info("[CAB]" + "determineServiceBillable:adjAmt:" + adjAmt);
				log.info("[CAB]" + "determineServiceBillable:after unitRate:" + unitRate);
			}

			billAdjParam = processGenericRepo.create(tariffCd);

			if (billAdjParam == null) {
				// ex = new ProcessChargeException("Bill Adj Param is null for ~tariff_cd = " +
				// tariffCd);
				ex = new ProcessChargeException("~tariff_cd (" + tariffCd + ") #Bill Adj Param is null#");
				throw ex;
			}

			billAdjParam.setTotalContainer(chargeableBillValueObject.getNbrCntr());
			billAdjParam.setTotalTime(timeUnit);
			billAdjParam.setTotalOtherUnit(otherUnit);
			billAdjParam.setUnitRate(unitRate);
			billAdjParam.setGst(gstPercent);

			itemGst = billAdjParam.getGstAmount();
			itemAmt = billAdjParam.getTotalAmount();

			// set the parameters into the ChargeableBillValueObject
			chargeableBillValueObject.setTariffType(tariffType);
			chargeableBillValueObject.setTariffCd(tariffCd);
			chargeableBillValueObject.setTierSeqNbr(tierSeqNbr.intValue());
			chargeableBillValueObject.setTariffDesc(tariffDesc);
			chargeableBillValueObject.setNbrTimeUnit(timeUnit);
			chargeableBillValueObject.setNbrOtherUnit(otherUnit);
			chargeableBillValueObject.setUnitRate(unitRate);
			chargeableBillValueObject.setGstCharge(gstPercent);
			chargeableBillValueObject.setGstAmt(itemGst);
			chargeableBillValueObject.setTotalChargeAmt(itemAmt);
			// Added by Irene Tan on 10/12/2002 : CTCAB20020044 - To crater for GST changes
			// w.e.f 1 Jan 2003
			chargeableBillValueObject.setFmasGstCd(tariffMainValueObject.getGSTCode());
			// End Added by Irene Tan
		} catch (Exception e) {
			// ex = new Exception("Exception occured for ~tariff_type = " + tariffType + "
			// when determining chargeable bill units ~Msg = " + e.getMessage());
			ex = new Exception("~tariff_type (" + tariffType
					+ ") #Exception occured when determining chargeable bill units# ~Msg = " + e.getMessage());
		}
		if (ex != null) {
			throw new ProcessChargeException(ex);
		}
		log.info("END determineServiceBillable DAO  chargeableBillValueObject: " + chargeableBillValueObject.toString());
		return chargeableBillValueObject;
	}

	/**
	 * MCC add new method to fetch the vessel scheme for loading vessel
	 * 
	 * @return
	 */
	public String determineLoadVslSch(String vvCd) throws SQLException, NamingException, Exception {
		String vesselScheme = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START determineLoadVslSch DAO :: vvCd: " + CommonUtility.deNull(vvCd));
			String sql = " select scheme from vessel_call vc where vc.vv_cd= :vvCd  ";
			log.info("sql: " + sql.toString());
			paramMap.put("vvCd", vvCd);
			log.info("paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs != null) {
				while (rs.next()) {
					vesselScheme = rs.getString("scheme");
				}
				log.info("[CAB] Vessel scheme Retrieved :**" + vesselScheme);
			}
			log.info("END determineLoadVslSch DAO  vesselScheme: " + vesselScheme);
			return vesselScheme;
		} catch (Exception ex) {
			log.info("[Exception] ProcessGenericEJB - determineLoadVslSch(): ", ex);
			throw new Exception("[Exception] ProcessGenericEJB - determineLoadVslSch()" + ex.getMessage());
		}
	}

	/**
	 * Calculates the wharfage charges based on the event log and returns result
	 * based on published rates
	 *
	 * @param ChargeableBillValueObject
	 * @param TariffMainVO
	 * @param double
	 * @return ChargeableBillValueObject
	 * @exception ProcessChargeException
	 */
	// processWharfEJB -> determineWharfBillable
	public ChargeableBillValueObject determineWharfBillable(ChargeableBillValueObject chargeableBillValueObject,
			TariffMainVO tariffMainValueObject, double deriveTon) throws ProcessChargeException {

		Exception ex = null;

		TariffTierVO tariffTierValueObject = null;
		BillAdjustParam billAdjParam = null;

		String tariffCd = null;
		String tariffType = null;
		Integer tierSeqNbr = null;
		String tariffDesc = null;
		double timeUnit = 0;
		double otherUnit = 0;
		double unitRate = 0;
		double gstPercent = 0;
		double itemGst = 0;
		double itemAmt = 0;

		double timeUnitBase = 0;
		double otherUnitBase = 0;
		double origTimeUnit = 0;
		double origOtherUnit = 0;
		String adjType = null;
		double adjAmt = 0;

		int tierCnt = 0;

		try {
			log.info("START determineWharfBillable DAO :: chargeableBillValueObject: " + CommonUtility.deNull(chargeableBillValueObject.toString())
					+ " tariffMainValueObject: " + CommonUtility.deNull(tariffMainValueObject.toString()) + " deriveTon: " + CommonUtility.deNull(String.valueOf(deriveTon)));
			tariffCd = tariffMainValueObject.getCode();
			tariffDesc = tariffMainValueObject.getDescription();
			tariffType = tariffMainValueObject.getTariffTypeInd();
			gstPercent = tariffMainValueObject.getGST();

			// loop thru the tariff tier records to determine what is the
			// chargeable units
			for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
				// get the individual record of the tariff tier record
				tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);

				tierSeqNbr = new Integer(tierCnt + 1);

				// from tariffTierValueObject, get the details for processing
				timeUnitBase = tariffTierValueObject.getPerHour();
				if (timeUnitBase < 0) {
					timeUnitBase = 1;
				}

				otherUnitBase = tariffTierValueObject.getPerUnit();
				if (otherUnitBase < 0) {
					otherUnitBase = 1;
				}

				// MCConsulting min 1 billable ton for all general cargo charges
				if (deriveTon < 1 && deriveTon > 0)
					deriveTon = 1;

				log.info("[CAB]" + "Min 1 billable deriveTon = " + deriveTon);
				log.info("[CAB]" + "timeUnitBase:" + timeUnitBase);
				log.info("[CAB]" + "otherUnitBase:" + otherUnitBase);

				origTimeUnit = 0;
				timeUnit = (origTimeUnit / timeUnitBase);

				origOtherUnit = deriveTon;
				otherUnit = (origOtherUnit / otherUnitBase);
			} // end for loop

			if (tariffType != null && tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
				unitRate = tariffTierValueObject.getRate();
			} else {
				adjType = tariffTierValueObject.getAdjustmentType();
				adjAmt = tariffTierValueObject.getAdjustment();
				unitRate = tariffTierValueObject.getRate();
				if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_PERCENT)) {
					unitRate = unitRate + (adjAmt / 100 * unitRate);
				} else if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_AMOUNT)) {
					unitRate = unitRate + adjAmt;
				}
			}

			log.info("[CAB]" + "unitRate:" + unitRate);

			billAdjParam = processGenericRepo.create(tariffCd);

			if (billAdjParam == null) {
				// ex = new ProcessChargeException("Bill Adj Param is null for
				// ~tariff_cd (" + tariffCd + ")");
				ex = new ProcessChargeException("~tariff_cd (" + tariffCd + ") #Bill Adj Param is null#");
				throw ex;
			}

			billAdjParam.setTotalContainer(chargeableBillValueObject.getNbrCntr());
			billAdjParam.setTotalTime(timeUnit);
			billAdjParam.setTotalOtherUnit(otherUnit);
			billAdjParam.setUnitRate(unitRate);
			billAdjParam.setGst(gstPercent);

			itemGst = billAdjParam.getGstAmount();
			itemAmt = billAdjParam.getTotalAmount();

			// set the parameters into the ChargeableBillValueObject
			chargeableBillValueObject.setTariffType(tariffType);
			chargeableBillValueObject.setTariffCd(tariffCd);
			chargeableBillValueObject.setTierSeqNbr(tierSeqNbr.intValue());
			chargeableBillValueObject.setTariffDesc(tariffDesc);
			chargeableBillValueObject.setNbrTimeUnit(timeUnit);
			chargeableBillValueObject.setNbrOtherUnit(otherUnit);
			chargeableBillValueObject.setUnitRate(unitRate);
			chargeableBillValueObject.setGstCharge(gstPercent);
			chargeableBillValueObject.setGstAmt(itemGst);
			chargeableBillValueObject.setTotalChargeAmt(itemAmt);
			// Added by Irene Tan on 10/12/2002 : CTCAB20020044 - To crater for
			// GST changes w.e.f 1 Jan 2003
			chargeableBillValueObject.setFmasGstCd(tariffMainValueObject.getGSTCode());
			// End Added by Irene Tan
		} catch (Exception e) {
			log.info("Exception determineWharfBillable : ", e);
		}
		if (ex != null) {
			throw new ProcessChargeException(ex);
		}
		log.info("END determineWharfBillable DAO  chargeableBillValueObject: " + chargeableBillValueObject.toString());
		return chargeableBillValueObject;
	}

	/**
	 * ThachPhung starts on 28-Dec-08 Wrapper method is used to calculate billTon
	 * value base on logic of TARIFF_SUB_WHARFAGE_GENERAL
	 *
	 *
	 * @param generalEventLogValueObject
	 * @param processCommon
	 * @param refInd
	 * @return
	 * @throws ProcessChargeException
	 */
	//
	private double calculateBillTonForSubROROVsl(GeneralEventLogValueObject generalEventLogValueObject, String refInd,
			boolean isFirstDNForEDO) throws ProcessChargeException {
		double billTon = 0.0;
		try{
		log.info("START calculateBillTonForSubROROVsl DAO :: generalEventLogValueObject: " + CommonUtility.deNull(generalEventLogValueObject.toString())
		+ " refInd: " + CommonUtility.deNull(refInd) + " isFirstDNForEDO: " + CommonUtility.deNull(String.valueOf(isFirstDNForEDO)));
		int countUnit = generalEventLogValueObject.getCountUnit();
		log.info("countUnit = " + countUnit);
		double billTonBl = generalEventLogValueObject.getBillTonBl();
		double billTonEdo = generalEventLogValueObject.getBillTonEdo();
		double billTonEsn = generalEventLogValueObject.getBillTonEsn();
		double loadTonCs = generalEventLogValueObject.getLoadTonCs();
		int totalPkgEdo = generalEventLogValueObject.getTotalPackEdo();
		int totalPkgDn = generalEventLogValueObject.getTotalPackDn();
		String schemeCd = generalEventLogValueObject.getSchemeCd();
		String mvmt = generalEventLogValueObject.getMvmt();
		String cargoType = generalEventLogValueObject.getCargoType();
		String type = generalEventLogValueObject.getType();

		log.info("cargoType = " + cargoType.toString());

		// <CFG> Automation of Warehouse Billing 21 Aug 2007
		if ((refInd.trim().equals(ProcessChargeConst.REF_IND_EDO)) && (generalEventLogValueObject.getDnNbr().trim()
				.equals(generalEventLogValueObject.getEdoAsnNbr().trim())) && // to cater for warehouse billing
																				// <cfg: add new scheme for Wooden
																				// Craft: JWP, 27.may.08>
																				// (schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)))
																				// {
				((schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)
						|| schemeCd.trim().equalsIgnoreCase(ProcessChargeConst.WOODEN_CRAFT_SCHEME)))) {
			// Printing of DN - Local Import / Transhipment / ITH for Liner
			// Vessel
			billTon = transLogRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
			log.info("[CAB]" + "CFG: JLR/JWP billTon = " + billTon + "...");

		} else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_EDO)) && (generalEventLogValueObject.getDnNbr()
				.trim().equals(generalEventLogValueObject.getEdoAsnNbr().trim())) && // to cater for warehouse billing
																						// add new scheme for LCT,
																						// 17.feb.11 by hpeng
				((schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
						|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
						|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) {
			if ((mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))
					|| (mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)
							&& type.trim().equals(ProcessChargeConst.TYPE_TS_DELIVER_LOCALLY))) {
				// Printing of DN - Local Import for Non-Liner Vessel
				billTon = transLogRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
				log.info("[CAB]" + "CFG: JNL/JBT billTon = " + billTon + "...");

			} else {
				billTon = billTonBl;
				log.info("[CAB]" + "CFG: ***** Unknown scenario in ProcessWharfEJB for warehouse billing : billTon = "
						+ billTon + "...");
			}
		} else
		// </CFG> 21 Aug 2007

		if ((refInd.trim().equals(ProcessChargeConst.REF_IND_DN)) &&
		// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08>
		// (schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME))) {
				((schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME)
						|| schemeCd.trim().equalsIgnoreCase(ProcessChargeConst.WOODEN_CRAFT_SCHEME)))) {
			// Printing of DN - Local Import / Transhipment / ITH for Liner
			// Vessel
			billTon = transLogRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
		} else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_DN)) && ((schemeCd.trim()
				// add new scheme for LCT, 17.feb.11 by hpeng
				.equals(ProcessChargeConst.NON_LINER_SCHEME))
				|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
				|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) {
			// amended by swho on 24/10/2002 for catering for NL TS delivered
			// locally
			// if (mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL) ) {
			if ((mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL))
					|| (mvmt.trim().equals(ProcessChargeConst.MVMT_TRANSHIP)
							&& type.trim().equals(ProcessChargeConst.TYPE_TS_DELIVER_LOCALLY))) {
				// end amend by swho
				// Printing of DN - Local Import for Non-Liner Vessel
				billTon = transLogRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
			} else {
				billTon = billTonBl;
			}
		} // MCC adding for 1st DN
		else if (isFirstDNForEDO && refInd.trim().equals(ProcessChargeConst.REF_IND_EDO)
				&& ((schemeCd.trim().equals(ProcessChargeConst.LINER_SCHEME))
						|| (schemeCd.trim().equals(ProcessChargeConst.NON_LINER_SCHEME))
						|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
						|| (schemeCd.trim().equals(ProcessChargeConst.WOODEN_CRAFT_SCHEME)))) {
			if (mvmt.trim().equals(ProcessChargeConst.MVMT_LOCAL)) {
				// EDO billable event - Local Import
				billTon = billTonEdo;
			}
		} else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_BL)) && ((schemeCd.trim()
				// add new scheme for LCT, 17.feb.11 by hpeng
				.equals(ProcessChargeConst.NON_LINER_SCHEME))
				|| (schemeCd.trim().equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
				|| (schemeCd.trim().equals(ProcessChargeConst.LCT_SCHEME)))) {
			// At closing of BJ - Transhipment or ITH for Non-Liner Vessel
			billTon = billTonBl;
		} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) {
			// At the printing of UA OF 1st UA
			billTon = billTonEsn;
		}
		// else if (refInd.trim().equals(ProcessChargeConst.REF_IND_BR)) {
		else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_ESN))
				|| (refInd.trim().equals(ProcessChargeConst.REF_IND_EDO))) {
			// At Close Shipment
			billTon = loadTonCs;
		} else {
			// throw new ProcessChargeException ("Processing of General Wharfage
			// does not have any reference indicator met - ~ref_ind = " + refInd
			// + "!");
			throw new ProcessChargeException(
					"#Processing of General Wharfage does not have any reference indicator met#");
		}
		log.info("END calculateBillTonForSubROROVsl DAO  billTon: " + billTon);
		return billTon;
		} catch (Exception e){
			log.error("Error caught in calculateBillTonForSubROROVsl", e);
		}
		return billTon;
	}

	@Override
	public void insertBillError(BillErrorVO billErrorValueObject) throws BusinessException {

		int insertResult = 0;

		Timestamp runDttm = null;
		String runInd = null;
		String tariffMainCat = null;
		String tariffSubCat = null;
		String remarks = null;
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START insertBillError DAO billErrorValueObject: " + CommonUtility.deNull(billErrorValueObject.toString()));
			// get BillErrorVO details
			runDttm = BillErrorVO.getCurrentTimestamp();
			runInd = billErrorValueObject.getRunInd();
			tariffMainCat = billErrorValueObject.getTariffMainCat();
			tariffSubCat = billErrorValueObject.getTariffSubCat();
			remarks = billErrorValueObject.getRemarks();

			// formulate the sql query and execute the query
			sb.append("INSERT INTO bill_error (run_dttm, run_ind, tariff_main_cat_cd, tariff_sub_cat_cd, remarks)"
					+ " VALUES(:runDttm,:runInd,:tariffMainCat,:tariffSubCat,:remarks) ");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			if (runDttm != null)
				parameters.addValue("runDttm", runDttm);

			if (runInd != null)
				parameters.addValue("runInd", runInd);

			if (tariffMainCat != null)
				parameters.addValue("tariffMainCat", tariffMainCat);

			if (tariffSubCat != null)
				parameters.addValue("tariffSubCat", tariffSubCat);
			if (remarks != null) {

				// amended by guoqiao on 11-Jan-2006
				// to log original remarks into log file.
				log.info("Original remarks : " + remarks);

				int maxLength = 1500;

				// amended by guoqiao on 11-Jan-2006
				// truncate remarks longer than 1500 before inserting into db.
				if (remarks.length() > maxLength) {
					remarks = remarks.substring(0, maxLength);
				}

				parameters.addValue("remarks", remarks);
			}
			log.info("insertBillError SQL: " + sb.toString() + " parameters: " + parameters.getValues());
			insertResult = namedParameterJdbcTemplate.update(sb.toString(), parameters);
			log.info("result: " + insertResult);
			if (insertResult == 0) {
				throw new BusinessException(
						"Insert failed in BillError EJB" + ERROR_MSG1 + runDttm + ERROR_MSG2 + runInd + ERROR_MSG3
								+ tariffMainCat + ERROR_MSG4 + tariffSubCat + ERROR_MSG5 + remarks + ERROR_MSG6);
			}
		} catch (BusinessException e) {
			log.info("insertBillError Exception: ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception in insertBillError EJB : " + e.getMessage() + ERROR_MSG1 + runDttm + ERROR_MSG2 + runInd
					+ ERROR_MSG3 + tariffMainCat + ERROR_MSG4 + tariffSubCat + ERROR_MSG5 + remarks + ERROR_MSG6);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertBillError DAO");
		}
	}
}
