package sg.com.jp.generalcargo.dao.impl;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.BillAdjustParamFactoryRepo;
import sg.com.jp.generalcargo.dao.ProcessCommonRepo;
import sg.com.jp.generalcargo.dao.ProcessGenericRepository;
import sg.com.jp.generalcargo.dao.ProcessOMCGenericRepo;
import sg.com.jp.generalcargo.dao.TariffMainRepository;
import sg.com.jp.generalcargo.domain.BerthRelatedValueObject;
import sg.com.jp.generalcargo.domain.BillAccountVO;
import sg.com.jp.generalcargo.domain.BillAdjustParam;
import sg.com.jp.generalcargo.domain.BillContainerVO;
import sg.com.jp.generalcargo.domain.BillItemVO;
import sg.com.jp.generalcargo.domain.BillJobVO;
import sg.com.jp.generalcargo.domain.BillMiscVO;
import sg.com.jp.generalcargo.domain.BillVO;
import sg.com.jp.generalcargo.domain.CntrEventLogValueObject;
import sg.com.jp.generalcargo.domain.ProcessValueObject;
import sg.com.jp.generalcargo.domain.TariffMainVO;
import sg.com.jp.generalcargo.domain.TariffTierBillPartyVO;
import sg.com.jp.generalcargo.domain.TariffTierVO;
import sg.com.jp.generalcargo.domain.TempProcessChargeValueObject;
import sg.com.jp.generalcargo.domain.VesselRelatedValueObject;
import sg.com.jp.generalcargo.domain.VtsbValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("processCommonRepo")
public class ProcessCommonJdbcRepo implements ProcessCommonRepo {

	private static final Log log = LogFactory.getLog(ProcessCommonJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private ProcessGenericRepository processGenericRepo;

	@Autowired
	private TariffMainRepository tariffMainRepo;

	@Autowired
	private ProcessOMCGenericRepo processOMCGenericRepo;

	@Autowired
	private BillAdjustParamFactoryRepo billAdjustParamFactoryRepo;

	private Hashtable<Integer, ProcessValueObject> processHash;

	static Hashtable<?, ?> custList;

	@Override
	public List<Object> processBill(Object[] cntrEventDtlArray, List<Object> billCollection, String tariffType)
			throws BusinessException {
		return processBill(cntrEventDtlArray, billCollection, tariffType, false, false);
	}

	public List<Object> processBill(Object[] cntrEventDtlArray, List<Object> billCollection, String tariffType,
			boolean isLoloProcess, boolean isRebatesProcess) throws BusinessException {
		List<Object> billList = null;
		// Valli change
		Hashtable<String, String> cntrListHash = null;
		List<Object> processedAndUnprocessedList = null;
		BillVO billValueObject = null;
		BillAccountVO billAccountValueObject = null;
		BillItemVO billItemValueObject = null;
		BillItemVO billItemValueObjectCurr = null;
		ProcessValueObject processValueObject = null;
		TariffMainVO tariffMainValueObject = null;
		int xcnt = 0;
		String prevCust = null;
		String prevAcct = null;
		String prevVvCd = null;
		String prevSlotOprCd = null;
		String prevCntrOprCd = null;
		String prevTxnCd = null;
		String prevTariff = null;
		double prevTime = 0;
		double prevOther = 0;
		double prevUnitRate = 0;
		// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
		String prevTariffDesc = null;
		// end add by swho
		// added by swho on 200503 to break into new bill when change of vv cd and
		// tariff sub cat
		String prevTariffSubCat = null;
		// end add by swho
		boolean firstTime = true;
		String billNbr = null;
		Timestamp billDttm = null;
		String currency = null;
		double billTotalGst = 0;
		double billTotalAmt = 0;
		int pageCnt = 0;
		int itemCnt = 0;
		String printInd = "N";
		String postInd = "N";
		Timestamp postDttm = null;
		double acctTotalGst = 0;
		double acctTotalAmt = 0;
		double itemTotalGst = 0;
		double itemTotalAmt = 0;
		double itemGst = 0;
		double itemAmt = 0;
		String custCd = null;
		String acctNbr = null;
		String oprCd = null;
		String agentAcct = null;
		String vvCd = null;
		String slotOprCd = null;
		String cntrOprCd = null;
		String txnCd = null;
		Integer versionNbr = null;
		// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
		String businessType = null;
		// end add by swho
		String tariffCd = null;
		String tariffMainCat = null;
		String tariffSubCat = null;
		double totalTime = 0;
		double totalOther = 0;
		double unitRate = 0;
		// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
		String tariffDesc = null;
		// end add by swho
		Object[] billNbrArray = null;
		String typeInd = null;
		String cntrSeqNbr = null;
		String jobNbrRef = null;
		String preJobNbrRef = null;
		// added by swho on 17/10/2002 for suppression of zeroes
		int billCnt = 0;
		// end add by swho
		// CR-CAB-20050823 TT billing changes - Valli
		boolean isTTShortFall = false;
		// CR-CAB-20050518-1 - Added by Valli
		boolean isAdminCharge = false;
		// START: CR-CAB-20050518-1 15 June 2006
		boolean isOtherFreqExtraMovement = false;
		// END: CR-CAB-20050518-1 15 June 2006
		boolean isTTProcess = false;
		List<ProcessValueObject> cntrEventRebatesList = new ArrayList<ProcessValueObject>();
		// Added by Anandhi for CR-CAB-20060822-01 on 20-Nov-2006
		Hashtable<String, String> custList = getListForSplitBills();
		// ended by Anandhi
		try {
			log.info("START: processBill DAO cntrEventDtlArray:" + cntrEventDtlArray.length + ",billCollection:"
					+ billCollection.size() + ",tariffType:" + tariffType + ",isLoloProcess:" + isLoloProcess
					+ ",isRebatesProcess:" + isRebatesProcess);

			log.info("[CAB],Start processBill " + cntrEventDtlArray.length + ", Rebates ---" + isRebatesProcess);
			billList = new ArrayList<Object>();
			// Valli change
			cntrListHash = new Hashtable<String, String>();
			// initialize the counters and before image of the fields to be compared
			itemCnt = 0;
			prevCust = "";
			prevAcct = "";
			prevVvCd = "";
			prevSlotOprCd = "";
			prevCntrOprCd = "";
			prevTxnCd = "";
			prevTariff = "";
			prevTime = 0;
			prevOther = 0;
			prevUnitRate = 0;
			// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
			prevTariffDesc = "";
			// end add by swho
			// added by swho on 200503 to break into new bill when change of vv cd and
			// tariff sub cat
			prevTariffSubCat = "";
			// end add by swho
			// loop thru the list of container event log for bill processing
			for (xcnt = 0; xcnt < cntrEventDtlArray.length; xcnt++) {

				tariffMainValueObject = new TariffMainVO();
				processValueObject = (ProcessValueObject) Array.get(cntrEventDtlArray, xcnt);
				log.info("jobNbrRef : " + jobNbrRef);
				// from processValueObject, get the details for processing
				custCd = processValueObject.getCustCd();
				acctNbr = processValueObject.getAcctNbr();
				slotOprCd = processValueObject.getSlotOprCd();
				cntrOprCd = processValueObject.getCntrOprCd();
				oprCd = processValueObject.getOprCd();
				txnCd = (processValueObject.getCntrEventLogValueObject()).getTxnCd();
				jobNbrRef = processValueObject.getJobOrderRef();
				if ((processValueObject.getCntrEventLogValueObject()).getCntrSeqNbr() != null) {
					// Bug fix (NullPointerEx) by Valli -05-Jul-2006
					cntrSeqNbr = (processValueObject.getCntrEventLogValueObject()).getCntrSeqNbr().toString();
				}
				log.info("cntrSeqNbr = " + cntrSeqNbr + "...");

				// from processValueObject, get the TariffMain details for processing
				if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
					typeInd = tariffType;
					versionNbr = processValueObject.getVersionNbrPubl();
					log.info("versionNbrPubl = " + versionNbr + "...");
					if (!versionNbr.equals(new Integer(0)) && versionNbr != null) {
						tariffMainValueObject = getTariffMainByTariffType(processValueObject, typeInd);
					}
				}

				if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE)) {
					typeInd = tariffType;
					versionNbr = processValueObject.getVersionNbrCust();
					log.info("versionNbrCust = " + versionNbr + "...");
					if (!versionNbr.equals(new Integer(0)) && versionNbr != null) {
						tariffMainValueObject = getTariffMainByTariffType(processValueObject, typeInd);
					}

					if (!tariffMainValueObject.isModified(tariffMainValueObject)) {
						versionNbr = processValueObject.getVersionNbrPubl();
						log.info("versionNbrPubl = " + versionNbr + "...");
						if (!versionNbr.equals(new Integer(0)) && versionNbr != null) {
							typeInd = ProcessChargeConst.TARIFF_TYPE_PUBLISH;
							tariffMainValueObject = getTariffMainByTariffType(processValueObject, typeInd);
						}
					}
				}

				tariffCd = tariffMainValueObject.getCode();
				tariffMainCat = tariffMainValueObject.getMainCategory();
				tariffSubCat = tariffMainValueObject.getSubCategory();
				// CR-CAB-20050823 TT billing changes - Valli
				if (ProcessChargeConst.TARIFF_MAIN_LOLO.equals(tariffMainCat)
						&& ProcessChargeConst.TARIFF_SUB_LOLO_TT_SHORTFALL_CHARGE.equals(tariffSubCat)) {
					isTTShortFall = true;
				} else {
					isTTShortFall = false;
				}
				// START: CR-CAB-20050518-1 - Added by Valli
				if (ProcessChargeConst.TARIFF_MAIN_ADMIN.equals(tariffMainCat)
						&& ProcessChargeConst.TARIFF_SUB_ADMIN_CHANGES_AFTER_CLOSING.equals(tariffSubCat)) {
					isAdminCharge = true;
				} else {
					isAdminCharge = false;
				}
				// END: CR-CAB-20050518-1 - Added by Valli
				// START : CR-CAB-20050518-1 15 June 2006
				if (ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_FREQUENCY.equals(tariffMainCat)
						&& ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_EXTRA_MVT.equals(tariffSubCat)) {
					isOtherFreqExtraMovement = true;
				} else {
					isOtherFreqExtraMovement = false;
				}
				log.info("isTTShortFall = " + isTTShortFall + "..." + ", isAdminCharge = " + isAdminCharge
						+ ", isOtherFreqExtraMovement = " + isOtherFreqExtraMovement);
				if (isTTShortFall || isAdminCharge || (isOtherFreqExtraMovement)) { // CR-CAB-20050518-1 - Amended by
																					// Valli
					vvCd = processValueObject.getCntrEventLogValueObject().getVvCd();
				} else {
					vvCd = determineVvCd(processValueObject, tariffMainCat);
				}
				currency = processValueObject.getCurrency();

				log.info("custCd = " + custCd + "...");
				log.info("acctNbr = " + acctNbr + "...");
				log.info("slotOprCd = " + slotOprCd + "...");
				log.info("cntrOprCd = " + cntrOprCd + "...");
				log.info("txnCd = " + txnCd + "...");
				log.info("tariffCd = " + tariffCd + "...");
				log.info("totalTime = " + totalTime + "...");
				log.info("totalOther = " + totalOther + "...");
				log.info("unitRate = " + unitRate + "...");
				log.info("tariffMainCat = " + tariffMainCat + "...");
				log.info("tariffSubCat = " + tariffSubCat + "...");
				log.info("vvCd = " + vvCd + "...");
				log.info("currency = " + currency + "...");

				if (custCd == null) {
					custCd = "";
				}
				if (acctNbr == null) {
					acctNbr = "";
				}
				if (vvCd == null) {
					vvCd = "";
				}
				if (slotOprCd == null) {
					slotOprCd = "";
				}
				if (cntrOprCd == null) {
					cntrOprCd = "";
				}
				if (txnCd == null) {
					txnCd = "";
				}
				if (tariffCd == null) {
					tariffCd = "";
				}
				// added by swho on 200503 to break into new bill when change of vv cd and
				// tariff sub cat
				if (tariffSubCat == null) {
					tariffSubCat = "";
				}
				// end add by swho

				// Start Lolo rebates Changes - Valli
				//
				if (ProcessChargeConst.TARIFF_MAIN_LOLO.equalsIgnoreCase(tariffMainCat) && !isRebatesProcess
						&& (cntrSeqNbr != null && !cntrSeqNbr.equals("0"))) {
					if (!cntrListHash.containsKey(cntrSeqNbr)) {
						// sauwoon 20110504
						if (ProcessChargeConst.TARIFF_SUB_LOLO.equalsIgnoreCase(tariffSubCat))
						/*
						 * if (ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_PEAK.equalsIgnoreCase(
						 * tariffSubCat) ||
						 * ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_OFF_PEAK.equalsIgnoreCase(
						 * tariffSubCat) ||
						 * ProcessChargeConst.TARIFF_SUB_LOLO.equalsIgnoreCase(tariffSubCat))
						 */
						{
							cntrListHash.put(cntrSeqNbr, custCd);
						}
						if (ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_PEAK.equalsIgnoreCase(tariffSubCat)
								|| ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_OFF_PEAK.equalsIgnoreCase(tariffSubCat)) {
							cntrEventRebatesList.add(processValueObject);
							log.info("[CAB],Rebates with different cust cd:- " + cntrSeqNbr);
							continue;
						}
					} else {
						// sauwoon 20110504
						if ((ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_PEAK.equalsIgnoreCase(tariffSubCat)
								|| ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_OFF_PEAK.equalsIgnoreCase(tariffSubCat))
								&& !cntrListHash.get(cntrSeqNbr).equals(custCd)) {
							// cntrListHash.put(cntrSeqNbr, custCd);
							cntrEventRebatesList.add(processValueObject);
							log.info("[CAB],Rebates with same cust cd:- " + cntrSeqNbr);
							continue;
						}
					}
				}

				// End Lolo rebates Changes - Valli
				if (ProcessChargeConst.TARIFF_MAIN_LOLO.equals(tariffMainCat)
						&& (ProcessChargeConst.TARIFF_SUB_LOLO_TT_CANCELLATION_CHARGE.equals(tariffSubCat)
								|| ProcessChargeConst.TARIFF_SUB_LOLO_TT_NO_SHOW.equals(tariffSubCat)
								|| ProcessChargeConst.TARIFF_SUB_LOLO_TT_SHORTFALL_CHARGE.equals(tariffSubCat)
								|| ProcessChargeConst.TARIFF_SUB_LOLO_TT_SUPP_BOOKING.equals(tariffSubCat))) {
					isTTProcess = true;
				} else {
					isTTProcess = false;
				}
				log.info(" isTTProcess =  " + isTTProcess);

				// Added by Anandhi for CR-CAB-20060822-01 on 14-Nov-2006
				boolean splitBill = false;
				if (ProcessChargeConst.TARIFF_MAIN_STEVEDORAGE.equals(tariffMainCat)
						&& (!prevSlotOprCd.equals(slotOprCd))) {
					if (custList.containsValue(custCd)) { // lsw move this part down cos custList will be null if
															// SV=false as custList is populated via SV logic
						splitBill = true;
					}
				}
				// Empty Scheme split bill for special handing sub cat, Added for empty scheme
				// start
				if (ProcessChargeConst.TARIFF_MAIN_STEVEDORAGE.equals(tariffMainCat)
						&& ProcessChargeConst.TARIFF_SUB_STE_SP.equals(prevTariffSubCat)
						&& !prevTariffSubCat.equals(tariffSubCat)) {
					splitBill = true;
				}
				// Added for empty scheme end

				// VietNguyen added for OMC
				if (ProcessChargeConst.TARIFF_MAIN_OMC_EQUIPMENT_RENTAL.equals(tariffMainCat)
						|| ProcessChargeConst.TARIFF_MAIN_OMC_OTHER.equals(tariffMainCat)
						|| ProcessChargeConst.TARIFF_MAIN_OMC_PLOT_USAGE.equals(tariffMainCat)) {
					splitBill = true;
				}

				log.info("In ProcessCommon splitBill =  " + splitBill);
				// ended by Anandhi CR-CAB-20060822-01

				// compare if there is a change in customer code,
				// if yes, retrieve the next bill number to be generated
				// amended by swho on 200503 to break into new bill when change of vv cd and
				// tariff sub cat
				// if (! prevCust.equals(custCd)) {
				if ((!prevCust.equals(custCd)) || ((isRebatesProcess || isTTProcess) && !prevVvCd.equals(vvCd))
						|| ((tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_VESSEL)
								|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_LOLO)
										&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_LOLO))
								|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STORE_RENT)
								|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_FREQUENCY))
								&& ((!prevVvCd.equals(vvCd)) || (!prevTariffSubCat.equals(tariffSubCat))))
						|| (splitBill)// Added by Anandhi CR-CAB-20060822-01 - 20-Nov-2006
				) {
					// end amend by swho
					log.info(" ** PREV CUST != CUST CD ** ");
					if (!firstTime) {
						log.info(" ** PREV CUST != null ** ");
						// set the amount fields into BillItem value object
						billItemValueObject.setGstAmount(itemTotalGst);
						billItemValueObject.setTotalAmount(itemTotalAmt);

						acctTotalAmt += itemTotalAmt;
						acctTotalGst += itemTotalGst;

						// set the amount fields into BillAcct value object
						billAccountValueObject.setGstAmount(acctTotalGst);
						billAccountValueObject.setTotalAmount(acctTotalAmt);

						billTotalAmt += acctTotalAmt;
						billTotalGst += acctTotalGst;

						// set the amount fields into Bill value object
						billValueObject.setGstAmount(billTotalGst);
						billValueObject.setTotalAmount(billTotalAmt);

						// add into BillAcct value object
						log.info(" ** add item into bill account ** ");
						billAccountValueObject.addItem(billItemValueObject);
						// add into Bill value object
						log.info(" ** add account into bill ** ");
						billValueObject.addAccount(billAccountValueObject);
						// add into BillList
						log.info(" ** add bill into job ** ");
						billValueObject.setJobOrderRef(preJobNbrRef);
						billList.add(billValueObject);

						// initialize the before image
						prevCust = "";
						prevAcct = "";
						prevVvCd = "";
						prevSlotOprCd = "";
						prevCntrOprCd = "";
						prevTxnCd = "";
						prevTariff = "";
						prevTime = 0;
						prevOther = 0;
						prevUnitRate = 0;
						preJobNbrRef = null;
						// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
						prevTariffDesc = "";
						// end add by swho
						// added by swho on 200503 to break into new bill when change of vv cd and
						// tariff sub cat
						prevTariffSubCat = "";
						// end add by swho
						itemCnt = 0;

						billTotalGst = 0;
						billTotalAmt = 0;
						acctTotalGst = 0;
						acctTotalAmt = 0;
						itemTotalGst = 0;
						itemTotalAmt = 0;
						billValueObject = new BillVO();
						billAccountValueObject = new BillAccountVO();
						billItemValueObject = new BillItemVO();
					}

					if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {

						// retrieve the next bill number to be generated
						// amended by swho on 22/04/2002 for extraction of GBMS vessels for marine
						// charges
						// billSeqNbr = processGeneric.retrieveNextBillNbr();
						businessType = (processGenericRepo.retrieveCustAcct(custCd, acctNbr)).getBusinessType();

						// VietNguyen (FPT) added on 07-Dec-2011 for OMC : START
						if (tariffMainCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_OMC_DOCKAGE)
								|| tariffMainCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFAGE)
								|| tariffMainCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFSIDE)
								|| tariffMainCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_OMC_EQUIPMENT_RENTAL)
								|| tariffMainCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_OMC_OTHER)
								|| tariffMainCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_OMC_PLOT_USAGE)) {
							businessType = "J";
						}
						// VietNguyen (FPT) added on 07-Dec-2011 for OMC : END

						// amended by swho on 17/10/2002 for suppression of zeroes
						// billNbr = processGeneric.retrieveNextBillNbr(businessType);
						if (isRebatesProcess) {
							billNbr = businessType + (++billCnt) + ProcessChargeConst.LOLO_REBATES_BILL_NBR_SUFFIX;
							// Added for Lolo Rebates
						} else {
							billNbr = businessType + (++billCnt);
						}
						// end amend by swho

						// billSeqNbrString = String.valueOf(billSeqNbr);
						// billSeqNbrPadZero = billSeqNbrLength - billSeqNbrString.length();
						// for (billSeqNbrCnt=0; billSeqNbrCnt<billSeqNbrPadZero; billSeqNbrCnt++) {
						// billSeqNbrString= "0" + billSeqNbrString;
						// }
						billDttm = getCurrentTimestamp();
						// billNbr = ProcessChargeConst.CONTAINER_TERMINAL +
						// (billDttm.toString()).substring(2,4) + billSeqNbrString;
						// end amend by swho
					}

					if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE)) {
						if (ProcessChargeConst.TARIFF_MAIN_OMC_EQUIPMENT_RENTAL.equals(tariffMainCat)
								|| ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER.equals(tariffMainCat)
								|| ProcessChargeConst.TARIFF_MAIN_OMC_OTHER.equals(tariffMainCat)
								|| ProcessChargeConst.TARIFF_MAIN_OMC_PLOT_USAGE.equals(tariffMainCat)) {
							billNbrArray = (getPublBillNbrForQuotation(billCollection, custCd, jobNbrRef)).toArray();
						} else {
							billNbrArray = (getPublBillNbr(billCollection, custCd)).toArray();
						}

						log.info("Testing......");
						billNbr = (String) Array.get(billNbrArray, 0);
						billDttm = (Timestamp) Array.get(billNbrArray, 1);
					}
					log.info("BILL NBR = " + billNbr + "...");

					// set first time flag to true for a new bill
					firstTime = true;

					// set Bill value object
					billValueObject = new BillVO();
					billValueObject.setBillNumber(billNbr);
					billValueObject.setBillDate(billDttm);
					// amended by swho on 17/10/2002 for suppression of zeroes
					// billValueObject.setTariffType(tariffType);
					if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
						billValueObject.setTariffType("A");
					} else {
						billValueObject.setTariffType("B");
					}
					// end amend by swho
					billValueObject.setMainCategory(tariffMainCat);
					billValueObject.setOperatorCode(oprCd);
					billValueObject.setAgentAccount(agentAcct);
					// amended by swho on 200503 to break into new bill when change of vv cd and
					// tariff sub cat
					// if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STEVEDORAGE) ||
					// tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE) ||
					// tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_VESSEL) ) {
					// billValueObject.setVesselVoyageCode(vvCd);
					// }
					// else {
					// billValueObject.setVesselVoyageCode("");
					// }
					billValueObject.setVesselVoyageCode(vvCd);
					// end amend

					billValueObject.setGstAmount(billTotalGst);
					billValueObject.setTotalAmount(billTotalAmt);
					billValueObject.setCurrency(currency);
					billValueObject.setTotalPage(pageCnt);
					billValueObject.setPrintIndicator(printInd);
					billValueObject.setPostIndicator(postInd);
					billValueObject.setPostDate(postDttm);
					billValueObject.setUser(ConstantUtil.userid);
				}

				if (!prevAcct.equals(acctNbr)) {
					log.info(" ** PREV ACCT != ACCT NBR ** ");
					if (!firstTime) {
						log.info(" ** PREV ACCT != null ** ");
						// set the amount fields into BillItem value object
						billItemValueObject.setGstAmount(itemTotalGst);
						billItemValueObject.setTotalAmount(itemTotalAmt);

						acctTotalAmt += itemTotalAmt;
						acctTotalGst += itemTotalGst;

						// set the amount fields into BillAcct value object
						billAccountValueObject.setGstAmount(acctTotalGst);
						billAccountValueObject.setTotalAmount(acctTotalAmt);

						billTotalAmt += acctTotalAmt;
						billTotalGst += acctTotalGst;

						// add into BillAcct value object
						log.info(" ** add item into bill acct ** ");
						billAccountValueObject.addItem(billItemValueObject);

						// add into Bill value object
						log.info(" ** add account into bill ** ");
						billValueObject.addAccount(billAccountValueObject);

						// initialize the before image
						prevAcct = "";
						prevVvCd = "";
						prevSlotOprCd = "";
						prevCntrOprCd = "";
						prevTxnCd = "";
						prevTariff = "";
						prevTime = 0;
						prevOther = 0;
						prevUnitRate = 0;
						// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
						prevTariffDesc = "";
						// end add by swho
						// added by swho on 200503 to break into new bill when change of vv cd and
						// tariff sub cat
						prevTariffSubCat = "";
						// end add by swho
						itemCnt = 0;

						acctTotalGst = 0;
						acctTotalAmt = 0;

						itemTotalGst = 0;
						itemTotalAmt = 0;

						billAccountValueObject = new BillAccountVO();
						billItemValueObject = new BillItemVO();
					}
					// set BillAccount value object
					billAccountValueObject = new BillAccountVO();
					billAccountValueObject.setCustomer(custCd);
					billAccountValueObject.setAccount(acctNbr);
					billAccountValueObject.setGstAmount(acctTotalGst);
					billAccountValueObject.setTotalAmount(acctTotalAmt);
				}

				// process Bill Item and Bill Container details
				billItemValueObjectCurr = new BillItemVO();
				billItemValueObjectCurr = processBillItem(processValueObject, billItemValueObjectCurr,
						tariffMainValueObject, itemCnt, typeInd);

				totalTime = billItemValueObjectCurr.getTotalTime();
				totalOther = billItemValueObjectCurr.getTotalOtherUnit();
				log.info("totalOther = " + totalOther + "...");
				unitRate = billItemValueObjectCurr.getUnitRate();
				// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
				tariffDesc = billItemValueObjectCurr.getTariffDescription();
				// end add by swho

				// compare if there is a change in the vessel voyage code, slot operator code,
				// container operator code, transaction code and tariff code
				// if yes, add 1 to the itemCnt
				// amended by swho on 200503 to break into new bill when change of vv cd and
				// tariff sub cat
				// if ((! prevVvCd.equals(vvCd)) || (! prevSlotOprCd.equals(slotOprCd)) ||
				if ((!prevSlotOprCd.equals(slotOprCd)) ||
				// end amend by swho
						(!prevCntrOprCd.equals(cntrOprCd)) || (!prevTxnCd.equals(txnCd))
						|| (!prevTariff.equals(tariffCd))) {
					log.info(" ** PREV IMAGE != CURRENT IMAGE ** ");

					if (!firstTime) {

						log.info(" ** PREV IMAGE != null ** ");

						// set the amount fields into BillItem value object
						billItemValueObject.setGstAmount(itemTotalGst);
						billItemValueObject.setTotalAmount(itemTotalAmt);

						acctTotalAmt += itemTotalAmt;
						acctTotalGst += itemTotalGst;

						// set value object for details
						log.info(" ** add item into bill acct ** ");
						billAccountValueObject.addItem(billItemValueObject);

						// initialize the before image
						prevVvCd = "";
						prevSlotOprCd = "";
						prevCntrOprCd = "";
						prevTxnCd = "";
						prevTariff = "";
						prevTime = 0;
						prevOther = 0;
						prevUnitRate = 0;
						// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
						prevTariffDesc = "";
						// end add by swho
						// added by swho on 200503 to break into new bill when change of vv cd and
						// tariff sub cat
						prevTariffSubCat = "";
						// end add by swho
						itemTotalGst = 0;
						itemTotalAmt = 0;

						billItemValueObject = new BillItemVO();
					}
					itemCnt++;
					log.info("ITEM NBR = " + itemCnt + "...");
					billItemValueObject = new BillItemVO();

				} // Unit rate check added by valli for lolo rebates
				else if ((prevTime != totalTime) || (prevOther != totalOther) || ((prevUnitRate != unitRate)
						&& (tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_PEAK)
								|| tariffSubCat
										.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_OFF_PEAK)))) {
					log.info("** PREV UNIT != CURR UNIT **");
					if (!firstTime) {
						log.info(" ** PREV UNIT != 0 **");

						// set the amount fields into BillItem value object
						billItemValueObject.setGstAmount(itemTotalGst);
						billItemValueObject.setTotalAmount(itemTotalAmt);

						acctTotalAmt += itemTotalAmt;
						acctTotalGst += itemTotalGst;

						// set value object for details
						billAccountValueObject.addItem(billItemValueObject);
						log.info(" ** add item  into bill acct ** ");

						// initialize the before image
						prevVvCd = "";
						prevSlotOprCd = "";
						prevCntrOprCd = "";
						prevTxnCd = "";
						prevTariff = "";
						prevTime = 0;
						prevOther = 0;
						prevUnitRate = 0;
						// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
						prevTariffDesc = "";
						// end add by swho
						// added by swho on 200503 to break into new bill when change of vv cd and
						// tariff sub cat
						prevTariffSubCat = "";
						// end add by swho
						itemTotalGst = 0;
						itemTotalAmt = 0;

						billItemValueObject = new BillItemVO();
					}
					itemCnt++;
					log.info("ITEM NBR = " + itemCnt + "...");
					billItemValueObject = new BillItemVO();
				}
				// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
				else if (!prevTariffDesc.equals(tariffDesc)) {
					log.info("** PREV TARIFF DESC != CURR TARIFF DESC **");
					if (!firstTime) {
						log.info(" ** PREV TARIFF DESC != NULL **");

						// set the amount fields into BillItem value object
						billItemValueObject.setGstAmount(itemTotalGst);
						billItemValueObject.setTotalAmount(itemTotalAmt);

						acctTotalAmt += itemTotalAmt;
						acctTotalGst += itemTotalGst;

						// set value object for details
						billAccountValueObject.addItem(billItemValueObject);
						log.info(" ** add item into bill acct ** ");

						// initialize the before image
						prevVvCd = "";
						prevSlotOprCd = "";
						prevCntrOprCd = "";
						prevTxnCd = "";
						prevTariff = "";
						prevTime = 0;
						prevOther = 0;
						prevUnitRate = 0;
						prevTariffDesc = "";
						// added by swho on 200503 to break into new bill when change of vv cd and
						// tariff sub cat
						prevTariffSubCat = "";
						// end add by swho
						itemTotalGst = 0;
						itemTotalAmt = 0;

						billItemValueObject = new BillItemVO();
					}
					itemCnt++;
					log.info("ITEM NBR = " + itemCnt + "...");
					billItemValueObject = new BillItemVO();
				}
				// end add by swho

				// process Bill Item and Bill Container details
				billItemValueObject = processBillItem(processValueObject, billItemValueObject, tariffMainValueObject,
						itemCnt, typeInd);

				itemGst = billItemValueObject.getGstAmount();
				itemAmt = billItemValueObject.getTotalAmount();

				itemTotalGst = itemGst;
				itemTotalAmt = itemAmt;

				prevCust = custCd;
				prevAcct = acctNbr;
				prevVvCd = vvCd;
				prevSlotOprCd = slotOprCd;
				prevCntrOprCd = cntrOprCd;
				prevTxnCd = txnCd;
				prevTariff = tariffCd;
				prevTime = totalTime;
				prevOther = totalOther;
				prevUnitRate = unitRate;
				preJobNbrRef = jobNbrRef;
				// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
				prevTariffDesc = tariffDesc;
				// end add by swho
				// added by swho on 200503 to break into new bill when change of vv cd and
				// tariff sub cat
				prevTariffSubCat = tariffSubCat;
				// end add by swho
				firstTime = false;

			} // end for loop

			// set the amount fields into BillItem value object
			billItemValueObject.setGstAmount(itemTotalGst);
			billItemValueObject.setTotalAmount(itemTotalAmt);

			acctTotalAmt += itemTotalAmt;
			acctTotalGst += itemTotalGst;

			// set the amount fields into BillAcct value object
			billAccountValueObject.setGstAmount(acctTotalGst);
			billAccountValueObject.setTotalAmount(acctTotalAmt);

			billTotalAmt += acctTotalAmt;
			billTotalGst += acctTotalGst;

			// set the amount fields into Bill value object
			billValueObject.setGstAmount(billTotalGst);
			billValueObject.setTotalAmount(billTotalAmt);

			// add into BillAcct value object
			billAccountValueObject.addItem(billItemValueObject);
			// add into Bill value object
			billValueObject.addAccount(billAccountValueObject);
			billValueObject.setJobOrderRef(jobNbrRef);
			// add into BillList
			billList.add(billValueObject);
			log.info(" ** bill complete ** ");

			if (isLoloProcess) {
				// Start Lolo rebates changes -- Valli 24-Apr-2006
				processedAndUnprocessedList = new ArrayList<Object>();
				processedAndUnprocessedList.add(billList);
				processedAndUnprocessedList.add(cntrEventRebatesList);
				log.info(" ** bill complete 2** " + billList);
				log.info(" ** bill complete 3** " + cntrEventRebatesList);
			}
		} catch (Exception e) {
			log.error("Exception: processBill ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: processBill  DAO Result:"
					+ (processedAndUnprocessedList != null ? processedAndUnprocessedList.toString() : "") + ",billList:"
					+ (billList != null ? billList.toString() : ""));
		}
		if (isLoloProcess) {
			return processedAndUnprocessedList;
		} else {
			return billList;
		}
	}

	private BillItemVO processBillItem(ProcessValueObject processValueObject, BillItemVO billItemValueObject,
			TariffMainVO tariffMainValueObject, int itemNbr, String tariffType) throws BusinessException {
		BillContainerVO billContainerValueObject = null;
		BillMiscVO billMiscValueObject = null;
		CntrEventLogValueObject cntrEventLogValueObject = null;
		TariffTierVO tariffTierValueObject = null;
		BillAdjustParam billAdjParam = null;
		String coaAcctNbr = null;
		String tariffMainCat = null;
		String tariffSubCat = null;
		Integer versionNbr = new Integer(0);
		String tariffCd = null;
		Integer tierSeqNbr = null;
		String tariffDesc = null;
		String contractNbr = null;
		// added by swho on 070802 for addition of contractual year
		int contractualYr = 0;
		// end add by swho
		double timeUnit = 0;
		double otherUnit = 0;
		double unitRate = 0;
		double gstPercent = 0;
		double gstAmt = 0;
		double totalAmt = 0;
		String vvCd = null;
		String slotOprCd = null;
		String cntrOprCd = null;
		String txnCd = null;
		String postInd = "N";
		Timestamp postDttm = null;
		long cntrSeqNbr = 0;
		Timestamp startDttm = null;
		Timestamp endDttm = null;
		String startDttmTypeCd = null;
		String endDttmTypeCd = null;
		Timestamp txnDttm = null;
		String cntrMvmt = null;
		// added by swho on 181202 to cater for GST change wef 1 Jan 2003
		String gstCd = null;
		// end add by swho
		// START - CR-CAB-20050823 TT billing changes - Valli
		boolean isTTShortFall = false;
		long miscSeqNbr = 0;
		String refNbr = null;
		// END - CR-CAB-20050823 TT billing changes - Valli
		// CR-CAB-20050518-1 - Added by Valli
		boolean isAdminCharge = false;
		// START : CR-CAB-20050518-1 15 June 2006
		boolean isOtherFreqExtraMovement = false;
		// END : CR-CAB-20050518-1 15 June 2006
		// START: CAB-CR-20060224-01 - Added by Valli
		List<TariffTierBillPartyVO> tariffTierBillPartyList = null;
		Timestamp unplugDttm = null;
		Timestamp pluginDttm = null;
		double tempPeriodQuot = 0;
		double tempPeriodMod = 0;
		long unplugDttmLong = 0;
		long pluginDttmLong = 0;
		double period = 0;
		double cutOff = 0;
		Timestamp varDttm = null;
		BerthRelatedValueObject berthRelatedValueObject = null;
		VesselRelatedValueObject vesselRelatedValueObject = null;
		String reeferOriginalBillParty = null;// CR-CAB-20100506-005
		String reeferOriginalBillCustCd = null; // CR-CAB-20100506-005
		// END: CAB-CR-20060424-01 - Added by Valli
		try {
			log.info("START: processBillItem DAO processValueObject:" + processValueObject.toString()
					+ ",billItemValueObject:" + billItemValueObject.toString() + ",tariffMainValueObject:"
					+ tariffMainValueObject.toString() + ",itemNbr:" + itemNbr + ",tariffType:" + tariffType);
			billContainerValueObject = new BillContainerVO();
			billMiscValueObject = new BillMiscVO();

			tariffMainCat = tariffMainValueObject.getMainCategory();
			tariffSubCat = tariffMainValueObject.getSubCategory();

			// START - CR-CAB-20050823 TT billing changes - Valli
			if (ProcessChargeConst.TARIFF_MAIN_LOLO.equals(tariffMainCat)
					&& ProcessChargeConst.TARIFF_SUB_LOLO_TT_SHORTFALL_CHARGE.equals(tariffSubCat)) {
				isTTShortFall = true;
			} else {
				isTTShortFall = false;
			}
			// END - CR-CAB-20050823 TT billing changes - Valli
			// START: CR-CAB-20050518-1 - Added by Valli
			if (ProcessChargeConst.TARIFF_MAIN_ADMIN.equals(tariffMainCat)
					&& ProcessChargeConst.TARIFF_SUB_ADMIN_CHANGES_AFTER_CLOSING.equals(tariffSubCat)) {
				isAdminCharge = true;
			} else {
				isAdminCharge = false;
			}
			// END: CR-CAB-20050518-1 - Added by Valli
			// START :
			if (ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_FREQUENCY.equals(tariffMainCat)
					&& ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_EXTRA_MVT.equals(tariffSubCat)) {
				isOtherFreqExtraMovement = true;
			} else {
				isOtherFreqExtraMovement = false;
			}
			// END :
			log.info("isTTShortFall = " + isTTShortFall + "..." + ", isAdminCharge = " + isAdminCharge
					+ ", isOtherFreqExtraMovement = " + isOtherFreqExtraMovement);
			if (!tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE)
					|| !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_DOCKAGE)
					|| !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_BERTH_UNBERTH)
					|| !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFAGE)
					|| !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_FM)
					|| !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFSIDE)
					|| !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_EQUIPMENT_RENTAL)
					|| !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_OTHER)
					|| !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_PLOT_USAGE)
					|| !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER)) {
				// from processValueObject, get the details for processing
				// set the data into temp variables for further processing
				cntrEventLogValueObject = processValueObject.getCntrEventLogValueObject();
				// CR-CAB-20050823 TT billing changes - Valli
				if (!isTTShortFall) {
					if (cntrEventLogValueObject.getCntrSeqNbr() != null) {
						cntrSeqNbr = cntrEventLogValueObject.getCntrSeqNbr().longValue();
					}
				} else {
					if (cntrEventLogValueObject.getMiscSeqNbr() != null) {
						miscSeqNbr = cntrEventLogValueObject.getMiscSeqNbr().longValue();
						refNbr = cntrEventLogValueObject.getRefNbr();
					}
				}
				txnDttm = cntrEventLogValueObject.getTxnDttm();
				txnCd = cntrEventLogValueObject.getTxnCd();
				cntrMvmt = cntrEventLogValueObject.getPurpCd(); // CR-CAB-20060224-01
			}

			coaAcctNbr = tariffMainValueObject.getCOA();
			contractNbr = processValueObject.getContractNbr();
			tariffCd = tariffMainValueObject.getCode();
			tierSeqNbr = processValueObject.getChargeTier();
			tariffDesc = tariffMainValueObject.getDescription();
			gstPercent = tariffMainValueObject.getGST();
			// CR-CAB-20050823 TT billing changes - Valli
			// Changed for Water Order
			if (isTTShortFall || isAdminCharge || (isOtherFreqExtraMovement)
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_SUPPLY))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_GOVT_TAX))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_WATERBORNE_FEE))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_LATE_FEE))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_SUPPLY))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_GOVT_TAX))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE) && tariffSubCat
							.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_WATERBORNE_FEE))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_LATE_FEE))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S2_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_BULK_CARGO_S2_SUPPLY))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S3P_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_BULK_CARGO_S3P_SUPPLY))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S3M_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_BULK_CARGO_S3M_SUPPLY))) {
				// CR-CAB-20050518-1 - Amended by Valli
				vvCd = processValueObject.getCntrEventLogValueObject().getVvCd();
			} else {
				vvCd = determineVvCd(processValueObject, tariffMainCat);
			}
			slotOprCd = processValueObject.getSlotOprCd();
			cntrOprCd = processValueObject.getCntrOprCd();
			startDttm = processValueObject.getStartDttm();
			startDttmTypeCd = processValueObject.getStartDttmTypeCd();
			endDttm = processValueObject.getEndDttm();
			endDttmTypeCd = processValueObject.getEndDttmTypeCd();
			// added by swho on 181202 to cater for GST change wef 1 Jan 2003
			gstCd = tariffMainValueObject.getGSTCode();
			// end add by swho

			if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
				versionNbr = processValueObject.getVersionNbrPubl();
				contractualYr = 0;
			} else if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE)) {
				versionNbr = processValueObject.getVersionNbrCust();
				contractualYr = 1;
			}

			// set the details into the BillItem value object
			billItemValueObject.setId(itemNbr);
			billItemValueObject.setCoa(coaAcctNbr);
			billItemValueObject.setMainCategory(tariffMainCat);
			billItemValueObject.setSubCategory(tariffSubCat);
			billItemValueObject.setVersion(versionNbr.intValue());
			billItemValueObject.setTariffCode(tariffCd);
			billItemValueObject.setTierSequenceNumber(tierSeqNbr.intValue());
			billItemValueObject.setTariffDescription(tariffDesc);
			billItemValueObject.setContract(contractNbr);
			billItemValueObject.setTotalTime(timeUnit);
			billItemValueObject.setTotalOtherUnit(otherUnit);
			billItemValueObject.setUnitRate(unitRate);
			billItemValueObject.setGst(gstPercent);
			billItemValueObject.setGstAmount(gstAmt);
			billItemValueObject.setTotalAmount(totalAmt);
			billItemValueObject.setVesselVoyageCode(vvCd);
			billItemValueObject.setSlotOperatorCode(slotOprCd);
			billItemValueObject.setContainerOperatorCode(cntrOprCd);
			billItemValueObject.setTransactionCode(txnCd);
			billItemValueObject.setPostIndicator(postInd);
			billItemValueObject.setPostTime(postDttm);
			billItemValueObject.setContractYear(contractualYr);
			// added by swho on 181202 to cater for GST change wef 1 Jan 2003
			billItemValueObject.setFmasGstCode(gstCd);
			// end add by swho

			// Start: For lolo rebates - By Valli - 18-May-2006
			if (tariffMainCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_LOLO)
					&& (tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_PEAK)
							|| tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_OFF_PEAK))) {
				tariffTierValueObject = tariffMainValueObject.getTier(0);
			} else {
				tariffTierValueObject = tariffMainValueObject.getTier((tierSeqNbr.intValue()) - 1);
			}
			// End: For lolo rebates - By Valli - 18-May-2006 -

			// include additional description in tariff for charges related to
			// Marine Dockage Charges
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE)
					&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE)) {
				if (tierSeqNbr.intValue() == 1) {
					tariffDesc = tariffDesc + " - for 1st " + tariffTierValueObject.getRangeTo() + "m";
				} else {
					tariffDesc = tariffDesc + " - in excess of " + tariffTierValueObject.getRangeFrom() + "m";
				}
				log.info("tariff desc = " + tariffDesc + "...");
				billItemValueObject.setTariffDescription(tariffDesc);
			}

			// Added on 07/05/2002 by Ai Lin - For Late Arrival
			// Include additional description in tariff for charges related to
			// Marine Late Arrival Charges
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE)
					&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_LATE_ARRIVAL)) {
				if (tierSeqNbr.intValue() == 1) {
					tariffDesc = tariffDesc + " - for 1st " + tariffTierValueObject.getRangeTo() + "minutes";
				} else {
					tariffDesc = tariffDesc + " - in excess of " + tariffTierValueObject.getRangeFrom() + "minutes";
				}
				log.info("Late Arrival tariff desc = " + tariffDesc + "...");
				billItemValueObject.setTariffDescription(tariffDesc);
			}
			// End added on 07/05/2002 by Ai Lin - For Late Arrival

			// Marine Special Dockage by YJ 15 Jan 04
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE)
					&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_SPECIAL_DOCKAGE)) {
				if (tierSeqNbr.intValue() == 1) {
					tariffDesc = tariffDesc + " - for 1st " + tariffTierValueObject.getRangeTo() + "m";
				} else {
					tariffDesc = tariffDesc + " - in excess of " + tariffTierValueObject.getRangeFrom() + "m";
				}
				log.info("Special Dockage tariff desc = " + tariffDesc + "...");
				billItemValueObject.setTariffDescription(tariffDesc);
			}
			// End added on 15 Jan 04 by YJ

			// START: CAB-CR-20060224-01 - Added by Valli
			// recoding by sauwoon for CAB-CR-20060224-01
			if (ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_ELEC_SUPPLY.equals(tariffSubCat)
					&& txnCd.equals(ConstantUtil.TXN_CD_REEFER_UNPLUG)) {
				// check all the bills under UPLG to check if they are split
				// ie tariff tier vo's time unit (set in processOthFreqEJB.determineTierUnits)
				// is different from computation of unplug - plug dttm
				// if there is a difference, find var dttm and call retrieveTarifFTierBillparty
				// to find cuttoff then amend tariff desc accrodingly.
				unplugDttm = cntrEventLogValueObject.getUnplugDttm();
				pluginDttm = cntrEventLogValueObject.getPluginDttm();
				unplugDttmLong = unplugDttm.getTime();
				pluginDttmLong = pluginDttm.getTime();
				period = (unplugDttmLong - pluginDttmLong) / 3600000.0;
				log.info("ProcessBillItem: - Inside UPLG...period1 = " + period + "...");

				tempPeriodQuot = (unplugDttmLong - pluginDttmLong) / 3600000;
				tempPeriodMod = period - tempPeriodQuot;

				if (tempPeriodMod != 0) {
					period = tempPeriodQuot + 1;
				}
				log.info("ProcessBillItem: - Inside UPLG...period2 = " + period + "...");
				// get processVO.getMain.getTier.getTimeUnit. can just get getMain(0) cos
				// published
				// and customised tariff main should both have the same cut off. can get
				// getTier(0) cos
				// reefer only has 1 tier (hardcoded! thats why need to split tariff tier lah!)
				log.info("processValueObject.getMain(0).getTier(0).getTimeUnit() is "
						+ processValueObject.getMain(0).getTier(0).getTimeUnit());
				if (period != processValueObject.getMain(0).getTier(0).getTimeUnit()) { // there is splitting
					boolean cancelledVsl = processGenericRepo.isCancelledVessel(vvCd);
					berthRelatedValueObject = new BerthRelatedValueObject();
					vesselRelatedValueObject = new VesselRelatedValueObject();
					vesselRelatedValueObject.setVvCd(vvCd);
					if (cancelledVsl) {
						int shiftInd = 1;
						berthRelatedValueObject = processGenericRepo.getEtbBtr(vvCd, shiftInd);
						if (berthRelatedValueObject.getEtbDttm() != null) {
							varDttm = berthRelatedValueObject.getEtbDttm();
						} else {
							varDttm = berthRelatedValueObject.getBtrDttm();
						}
					} else {
						berthRelatedValueObject = processGenericRepo.retrieveBerthDttm(vesselRelatedValueObject, 1,
								ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_FREQUENCY);
						varDttm = berthRelatedValueObject.getAtbDttm();
					}

					reeferOriginalBillParty = tariffMainRepo.retrieveBillPartyByTariffCd(
							processValueObject.getVersionNbrPubl().intValue(), tariffMainValueObject.getCode());
					// so far bill party is only for CO and VO for COFRE.
					if (reeferOriginalBillParty.equalsIgnoreCase(ProcessChargeConst.BILL_PARTY_CNTR_OPR)) {
						reeferOriginalBillCustCd = cntrEventLogValueObject.getCntrOprCd();
					} else if (reeferOriginalBillParty.equalsIgnoreCase(ProcessChargeConst.BILL_PARTY_HAUL_OPR)) {
						reeferOriginalBillCustCd = cntrEventLogValueObject.getHaulCd();
					} else if (reeferOriginalBillParty.equalsIgnoreCase(ProcessChargeConst.BILL_PARTY_SLOT_OPR)) {
						// this part of the logic is copied from
						// processGenericEJB.populateForCustomizeTariff, though not word by word
						determineSlotOprCd(cntrEventLogValueObject, tariffMainCat);
						if ((cntrMvmt.equals(ProcessChargeConst.PURP_CD_TRANSHIP)
								|| cntrMvmt.equals(ProcessChargeConst.PURP_CD_REEXPORT)) && slotOprCd == null) {
							if (processGenericRepo.retrieveDiscGateway(cntrEventLogValueObject.getCntrSeqNbr())
									.equals("J")) {
								reeferOriginalBillCustCd = cntrEventLogValueObject.getDiscSlotOprCd();
							} else {
								reeferOriginalBillCustCd = cntrEventLogValueObject.getLoadSlotOprCd();
							}
						}
					} else if (reeferOriginalBillParty.equalsIgnoreCase(ProcessChargeConst.BILL_PARTY_VESL_OPR)) {
						reeferOriginalBillCustCd = processGenericRepo.determineVvCd(cntrEventLogValueObject);
					}
					// only retrieve operator line code when its not haulier. This part of the logic
					// is also copied from processGenericEJB.populateForCustomizeTariff, but not
					// word by word
					if (!reeferOriginalBillParty.equals(ProcessChargeConst.BILL_PARTY_HAUL_OPR)) {
						reeferOriginalBillCustCd = processGenericRepo.retrieveLineCode(reeferOriginalBillCustCd);
					}
					log.info("ProcessCommon.processBillItem --> original bill cust cd/operator is "
							+ reeferOriginalBillCustCd);
					// end 'only retrieve operator line code when its not haulier'

					tariffTierBillPartyList = processGenericRepo.retrieveTariffTierBillParty(tariffMainCat,
							tariffSubCat, cntrMvmt, ProcessChargeConst.CONTAINER_BUSINESS, varDttm,
							reeferOriginalBillCustCd);
					if (tariffTierBillPartyList != null && tariffTierBillPartyList.size() > 0) {
						cutOff = ((TariffTierBillPartyVO) tariffTierBillPartyList.get(0)).getCutOff();
						// compare billParty from tariffTierBillPartyList with the real one. if same,
						// change bill party
						if (processValueObject.getMain(0).getBillParty()
								.equals(((TariffTierBillPartyVO) tariffTierBillPartyList.get(0)).getBillParty())) {
							tariffDesc = tariffDesc + "- in excess of " + cutOff + " hrs"; // billItemVO already set
																							// with the cutoff bill
																							// party
						} else {
							tariffDesc = tariffDesc + "- for 1st " + cutOff + " hrs";
						}
						log.info("Reefer tariff desc = " + tariffDesc + "...");
						billItemValueObject.setTariffDescription(tariffDesc);
					}
				} // end if there is splitting . there is no else for this if statement
			} // end if UPLG

			/*
			 * valli's original coding if
			 * (ProcessChargeConst.PURP_CD_IMPORT.equals(cntrMvmt) &&
			 * ProcessChargeConst.STATUS_FULL.equals(cntrStatus) &&
			 * ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_ELEC_SUPPLY.equals(tariffSubCat) &&
			 * txnCd.equals(ConstantUtil.TXN_CD_REEFER_UNPLUG)) {
			 * log.info("ProcessBillItem: - Inside OFRE IM F UPLG...");
			 * 
			 * processGenericHome = getProcessGenericHome(); // get ProcessGeneric object
			 * processGeneric = (ProcessGeneric)processGenericHome.create();
			 * 
			 * tariffTierBillPartyList =
			 * processGeneric.retrieveTariffTierBillParty(tariffMainCat, tariffSubCat,
			 * cntrMvmt, ProcessChargeConst.CONTAINER_BUSINESS,); tariffTierBillPartyVO =
			 * null; if (tariffTierBillPartyList != null) { tariffTierBillPartyVO =
			 * (TariffTierBillPartyVO) tariffTierBillPartyList.get(0); } // END:
			 * CR-CAB-20060424-01 - Added by Valli cutOff =
			 * tariffTierBillPartyVO.getCutOff();
			 * log.info("ProcessBillItem: - Inside OFRE IM F UPLG...cutoff = " + cutOff);
			 * unplugDttm = cntrEventLogValueObject.getUnplugDttm(); pluginDttm =
			 * cntrEventLogValueObject.getPluginDttm(); unplugDttmLong =
			 * unplugDttm.getTime(); pluginDttmLong = pluginDttm.getTime();
			 * log.info("ProcessBillItem: - Inside OFRE IM F UPLG...unplug dttm long = " +
			 * unplugDttmLong + "...");
			 * log.info("ProcessBillItem: - Inside OFRE IM F UPLG...plugin dttm long = " +
			 * pluginDttmLong + "...");
			 * 
			 * // compute total period in hours period = (unplugDttmLong - pluginDttmLong) /
			 * 3600000.0; log.info("ProcessBillItem: - Inside OFRE IM F UPLG...period = " +
			 * period + "...");
			 * 
			 * tempPeriodQuot = (unplugDttmLong - pluginDttmLong) / 3600000; tempPeriodMod =
			 * period - tempPeriodQuot;
			 * 
			 * if (tempPeriodMod != 0) { period = tempPeriodQuot + 1; } if (period > cutOff)
			 * { if((tariffTierValueObject.getTimeUnit() *
			 * tariffTierValueObject.getPerHour()) == cutOff){
			 * log.info("ProcessBillItem: - Inside OFRE IM F UPLG... tier 1 "); tariffDesc =
			 * tariffDesc + "- for 1st " + cutOff +" Hrs"; } else {
			 * log.info("ProcessBillItem: - Inside OFRE IM F UPLG... tier 2 "); tariffDesc =
			 * tariffDesc + "- in excess of " + cutOff + " Hrs"; } }
			 * billItemValueObject.setTariffDescription(tariffDesc);
			 * 
			 * }
			 */
			// END: CAB-CR-20060224-01 - Added by Valli

			// Added on 17/08/2007 by Ruchika - For Overstay Dockage
			// Include additional description in tariff for charges related to
			// Marine Overstay Dockage Charges
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE)
					&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY_DOCKAGE)) {
				if (tierSeqNbr.intValue() == 1) {
					tariffDesc = tariffDesc + " - for 1st " + tariffTierValueObject.getRangeTo() + " hours";
				} else {
					tariffDesc = tariffDesc + " - in excess of " + tariffTierValueObject.getRangeFrom() + " hours";
				}
				billItemValueObject.setTariffDescription(tariffDesc);
			}
			// End added on 17/08/2007 by Ruchika - For Overstay Dockage

			// Changed for Water Order
			if (!tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE) && !isTTShortFall
					&& !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_DOCKAGE)
					&& !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFAGE)
					&& !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFSIDE)
					&& !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_EQUIPMENT_RENTAL)
					&& !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_OTHER)
					&& !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_PLOT_USAGE)
					&& !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_VTSB)
					// Added by MC Consulting for LWMS KCP
					&& !tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_LWMS_KCP)
					// End of addition by MC Consulting for LWMS KCP
					&& !(tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S2_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_BULK_CARGO_S2_SUPPLY))
					&& !(tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S3M_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_BULK_CARGO_S3M_SUPPLY))
					&& !(tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S3P_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_BULK_CARGO_S3P_SUPPLY))
					&& !(tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_SUPPLY))
					&& !(tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_GOVT_TAX))
					&& !(tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_WATERBORNE_FEE))
					&& !(tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_LATE_FEE))
					&& !(tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_SUPPLY))
					&& !(tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_GOVT_TAX))
					&& !(tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE) && tariffSubCat
							.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_WATERBORNE_FEE))
					&& !(tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_LATE_FEE))) {
				billContainerValueObject = new BillContainerVO();
				billContainerValueObject.setContainerSequenceNumber(cntrSeqNbr);
				billContainerValueObject.setStartTime(startDttm);
				billContainerValueObject.setEndTime(endDttm);
				billContainerValueObject.setStartDttmTypeCd(startDttmTypeCd);
				billContainerValueObject.setEndDttmTypeCd(endDttmTypeCd);
				billContainerValueObject.setTransactionTime(txnDttm);
				billContainerValueObject.setTransactionCode(txnCd);

				// add into BillItemValueObject
				billItemValueObject.addContainer(billContainerValueObject);
				log.info("");
				log.info("** CNTR SEQ NBR = " + cntrSeqNbr + "... **");
			} else if (isTTShortFall) { // CR-CAB-20050823 TT billing changes - Valli
				billMiscValueObject = new BillMiscVO();
				billMiscValueObject.setMiscSeqNbr(miscSeqNbr);
				billMiscValueObject.setRefNbr(refNbr);
				billMiscValueObject.setTransactionTime(txnDttm);
				billMiscValueObject.setTransactionCode(txnCd);

				// add into BillItemValueObject
				billItemValueObject.addMisc(billMiscValueObject);
				log.info("** Ref Nbr = " + refNbr);
				log.info("** MISC SEQ NBR = " + miscSeqNbr + "... **");
			} else if ((tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFSIDE)
					&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_WHARFSIDE))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFAGE)
							&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_WHARFAGE))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_STEVEDORAGE)
							&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_STEVEDORAGE))
					|| ((tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_MPA)
							&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_MPA)))
					|| ((tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_EQUIPMENT_RENTAL)
							&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_EQUIPMENT_RENTAL)))
					|| ((tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_PLOT_USAGE)
							&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_PLOT_USAGE)))
					|| ((tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_OTHER)
							&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_OTHER)))) {

				if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFSIDE)
						&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_WHARFSIDE)) {

					List<BillJobVO> jobOrderDetailCollection = processOMCGenericRepo
							.retriveveOMCJobOrderDetail(processValueObject.getJobId());
					Object[] jobOrderDetailArray = jobOrderDetailCollection.toArray();
					BillJobVO billJobValueObject = null;
					for (int ycnt = 0; ycnt < jobOrderDetailArray.length; ycnt++) {
						billJobValueObject = (BillJobVO) Array.get(jobOrderDetailArray, ycnt);
						billJobValueObject.setJobRefNbr(processValueObject.getJobOrderRef());
						if (billJobValueObject.getSubServiceCd() != null
								&& billJobValueObject.getSubServiceCd().length() > 30) {
							billJobValueObject.setSubServiceCd(billJobValueObject.getSubServiceCd().substring(0, 29));
						}
						billJobValueObject.setJobType(processValueObject.getJobType());
						log.info("Add job Detail : " + billJobValueObject.getJobDetId());
						// add into BillItemValueObject
						billItemValueObject.addJob(billJobValueObject);
					}
				}

				if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_EQUIPMENT_RENTAL)
						&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_EQUIPMENT_RENTAL)
						|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_OTHER)
								&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_OTHER)
						|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_PLOT_USAGE)
								&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_PLOT_USAGE)) {

					List<BillJobVO> jobOrderDetailCollection = processOMCGenericRepo
							.retriveveOMCJobOrderDetail(processValueObject.getJobId());
					Object[] jobOrderDetailArray = jobOrderDetailCollection.toArray();
					BillJobVO billJobValueObject = null;
					for (int ycnt = 0; ycnt < jobOrderDetailArray.length; ycnt++) {
						billJobValueObject = (BillJobVO) Array.get(jobOrderDetailArray, ycnt);
						billJobValueObject.setJobRefNbr(processValueObject.getJobOrderRef());
						billJobValueObject.setJobType(processValueObject.getJobType());
						if (billJobValueObject.getSubServiceCd() != null
								&& billJobValueObject.getSubServiceCd().length() > 30) {
							billJobValueObject.setSubServiceCd(billJobValueObject.getSubServiceCd().substring(0, 29));
						}
						log.info("Add job Detail : " + billJobValueObject.getJobDetId());
						// add into BillItemValueObject
						billItemValueObject.addJob(billJobValueObject);
					}
				}

				String tariffDes = billItemValueObject.getTariffDescription();

				String addTariffDes = " , Job Execution Date: " + processValueObject.getJobExeDttm();
				if (processValueObject.getPoNum() != null) {
					addTariffDes = addTariffDes + " , PO Number: " + processValueObject.getPoNum();
				}

				if ("WS".equals(processValueObject.getJobType())) {
					// tariffDes = tariffDes + " - Job Number Reference : " +
					// processValueObject.getJobOrderRef();
					if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFSIDE)
							&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_WHARFSIDE)) {
						tariffDes = tariffDes + " - Job Number Reference : " + processValueObject.getJobOrderRef()
								+ addTariffDes;
					}
				} else if ("EL".equals(processValueObject.getJobType())) {
					// tariffDes = tariffDes + " - Job Number Reference : " +
					// processValueObject.getJobOrderRef();
					tariffDes = tariffDes + " - Job Number Reference : " + processValueObject.getJobOrderRef()
							+ addTariffDes;
				} else if ("AS".equals(processValueObject.getJobType())) {
					// tariffDes = tariffDes + " - Job Number Reference : " +
					// processValueObject.getJobOrderRef();
					tariffDes = tariffDes + " - Job Number Reference : " + processValueObject.getJobOrderRef()
							+ addTariffDes;
				}

				billItemValueObject.setTariffDescription(tariffDes);
			}

			// Added for Vehicle Booking
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_VTSB)) {
				List<VtsbValueObject> supportDtlList = processValueObject.getVbSupportList();
				for (int loop = 0; loop < supportDtlList.size(); loop++) {
					VtsbValueObject vtsbValueObject = (VtsbValueObject) supportDtlList.get(loop);
					billItemValueObject.addVb(vtsbValueObject);
				}
			}

			// Added by MC Consulting for LWMS KCP
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_LWMS_KCP)) {
				billItemValueObject.setRemarks(processValueObject.getJobOrderRef());
			}
			// End of addition by MC Consulting for LWMS KCP

			// Added for water order
			if ((tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
					&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_SUPPLY))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_GOVT_TAX))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_WATERBORNE_FEE))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_WATER_ORDER_LATE_FEE))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_SUPPLY))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_GOVT_TAX))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE) && tariffSubCat
							.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_WATERBORNE_FEE))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_OMC_WATER_ORDER_LATE_FEE))) {

				// 21/03/2014 PCYAP Move Water Order support information from
				// BILL_ITEM.TARIFF_DESC to BILL_ITEM.REMARKS field
				// tariffDesc = tariffDesc + " - Water Order Reference : " +
				// processValueObject.getJobOrderRef().split("-")[0] + ", Voucher : " +
				// processValueObject.getJobOrderRef().split("-")[1];
				// billItemValueObject.setTariffDescription(tariffDesc);
				billItemValueObject
						.setRemarks("Water Order Reference : " + processValueObject.getJobOrderRef().split("-")[0]
								+ ", Voucher : " + processValueObject.getJobOrderRef().split("-")[1]);
			}

			// Added for Bulk Cargo
			if ((tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S2_CHARGE)
					&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_BULK_CARGO_S2_SUPPLY))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S3M_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_BULK_CARGO_S3M_SUPPLY))
					|| (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S3P_CHARGE)
							&& tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_BULK_CARGO_S3P_SUPPLY))) {
				// tariffDesc = tariffDesc + processValueObject.getJobOrderRef();
				// billItemValueObject.setTariffDescription(tariffDesc);
				billItemValueObject.setRemarks(processValueObject.getJobOrderRef());
			}

			// get the base unit computed unit rates for bill charges related to
			// (1) Stevedorage Charges
			// (2) Store Rent Charges
			// (3) LOLO Charges
			// (4) Marine Charges
			// (5) Other Vessel Charges
			// (6) Other Frequency Charges
			billItemValueObject = determineBillable(billItemValueObject, tariffTierValueObject, tariffMainCat,
					tariffType);
			if (isTTShortFall) { // CR-CAB-20050823 TT billing changes - Valli
				billItemValueObject.addOtherUnit(billItemValueObject.getTotalOtherUnit());
				billItemValueObject.setTotalOtherUnit(billItemValueObject.getOtherUnitCount());
			}
			timeUnit = billItemValueObject.getTotalTime();
			otherUnit = billItemValueObject.getTotalOtherUnit();
			unitRate = billItemValueObject.getUnitRate();

			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_DOCKAGE)
					&& tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_OMC_DOCKAGE)) {
				if (tariffMainValueObject.getTier(0) != null) {
					unitRate = unitRate / tariffMainValueObject.getTier(0).getPerHour();
				}
			}
			billAdjParam = billAdjustParamFactoryRepo.create(tariffCd);

			if (billAdjParam == null) {
				throw new BusinessException("Bill Adj Param is null for <cntr_seq_nbr(" + cntrSeqNbr + ") txn_dttm("
						+ txnDttm + ") txn_cd(" + txnCd + ")");

			}

			billAdjParam.setTotalContainer(billItemValueObject.getContainerCount());
			billAdjParam.setTotalTime(timeUnit);
			billAdjParam.setTotalOtherUnit(otherUnit);
			billAdjParam.setUnitRate(unitRate);
			billAdjParam.setGst(gstPercent);

			gstAmt = billAdjParam.getGstAmount();
			totalAmt = billAdjParam.getTotalAmount();

			billItemValueObject.setGstAmount(gstAmt);
			billItemValueObject.setTotalAmount(totalAmt);
		} catch (Exception e) {
			log.error("Exception: processBill ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: processBill  DAO Result:"
					+ (billItemValueObject != null ? billItemValueObject.toString() : ""));
		}
		return billItemValueObject;
	}

	public List<Object> getPublBillNbr(List<Object> billCollection, String custCd) throws BusinessException {
		BillVO billValueObject = null;
		Object[] billArray = null;
		List<Object> billList = null;
		int xcnt = 0;
		String custCdPubl = null;
		String billNbr = null;
		Timestamp billDttm = null;
		billList = new ArrayList<Object>();
		try {
			log.info("START: getPublBillNbr DAO billCollection:" + billCollection.size() + ",custCd:" + custCd);
			billArray = billCollection.toArray();
			for (xcnt = 0; xcnt < billArray.length; xcnt++) {
				billValueObject = (BillVO) Array.get(billArray, xcnt);

				custCdPubl = billValueObject.getCustomerCode();
				log.info("cust cd publ = " + custCdPubl + "...");
				log.info("cust cd cust = " + custCd + "...");

				if (custCdPubl.equals(custCd)) {
					billNbr = billValueObject.getBillNumber();
					log.info("publ bill nbr = " + billNbr + "...");
					billDttm = billValueObject.getBillDate();
					log.info("publ bill dttm = " + billDttm + "...");
				}
			}

			if (billNbr != null && billDttm != null) {
				billList.add(billNbr);
				billList.add(billDttm);
			}
		} catch (Exception e) {
			log.error("Exception: getPublBillNbr ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPublBillNbr  DAO Result:" + (billList != null ? billList.size() : ""));
		}
		return billList;
	}

	public List<Object> getPublBillNbrForQuotation(List<Object> billCollection, String custCd, String jobNbrRef)
			throws BusinessException {
		BillVO billValueObject = null;
		Object[] billArray = null;
		List<Object> billList = null;
		int xcnt = 0;
		String custCdPubl = null;
		String jobNbrRefPubl = null;
		String billNbr = null;
		Timestamp billDttm = null;
		billList = new ArrayList<Object>();
		try {
			log.info("START: getPublBillNbrForQuotation DAO billCollection:" + billCollection.size() + ",custCd:"
					+ custCd + ",jobNbrRef:" + jobNbrRef);
			billArray = billCollection.toArray();
			for (xcnt = 0; xcnt < billArray.length; xcnt++) {
				billValueObject = (BillVO) Array.get(billArray, xcnt);

				custCdPubl = billValueObject.getCustomerCode();
				jobNbrRefPubl = billValueObject.getJobOrderRef();
				log.info("cust cd publ = " + custCdPubl + "...");
				log.info("cust cd cust = " + custCd + "...");
				log.info("job ref cd publ = " + jobNbrRefPubl + "...");
				log.info("job ref = " + jobNbrRef + "...");
				if (jobNbrRefPubl != null) {
					if (custCdPubl.equals(custCd) && jobNbrRefPubl.equals(jobNbrRef)) {
						billNbr = billValueObject.getBillNumber();
						log.info("publ bill nbr = " + billNbr + "...");
						billDttm = billValueObject.getBillDate();
						log.info("publ bill dttm = " + billDttm + "...");
					}
				} else {
					if (custCdPubl.equals(custCd)) {
						billNbr = billValueObject.getBillNumber();
						log.info("publ bill nbr = " + billNbr + "...");
						billDttm = billValueObject.getBillDate();
						log.info("publ bill dttm = " + billDttm + "...");
					}
				}

			}

			if (billNbr != null && billDttm != null) {
				billList.add(billNbr);
				billList.add(billDttm);
			}
		} catch (Exception e) {
			log.error("Exception: getPublBillNbrForQuotation ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPublBillNbrForQuotation  DAO Result:" + (billList != null ? billList.size() : ""));
		}
		return billList;
	}

	@Override
	public List<ProcessValueObject> sortCntrEventDtl(Object[] cntrEventDtlArray, String tariffType,
			String tariffMainCat) throws BusinessException {
		TempProcessChargeValueObject tempProcessChargeValueObject = null;
		ProcessValueObject processValueObject = null;
		List<TempProcessChargeValueObject> tempProcessChargeCollection;
		Object[] tempProcessChargeArray;
		List<ProcessValueObject> processList = null;
		int xcnt = 0;
		Integer seqNbr = null;
		Integer tierSeqNbr = null;
		try {
			log.info("START: sortCntrEventDtl DAO cntrEventDtlArray:" + cntrEventDtlArray.length + ",tariffType:"
					+ tariffType + ",tariffMainCat:" + tariffMainCat);
			// initialize process list
			processList = new ArrayList<ProcessValueObject>();

			// populate the TempProcessChargeValueObject into a collection
			tempProcessChargeCollection = populateTempProcessCharge(cntrEventDtlArray, tariffType);

			// sort the TempProcessCharge collection
			tempProcessChargeCollection = processGenericRepo.sortTempProcessCharge(tempProcessChargeCollection,
					tariffMainCat);
			tempProcessChargeArray = tempProcessChargeCollection.toArray();

			log.info("tempProcessChargeArray = " + tempProcessChargeArray.length + "...");

			// loop the returned TempProcessCharge collection and
			// populate the ProcessValueObject into a sorted ArrayList
			for (xcnt = 0; xcnt < tempProcessChargeArray.length; xcnt++) {
				// get the TempProcessChargeValueObject and determine the seqNbr
				tempProcessChargeValueObject = (TempProcessChargeValueObject) Array.get(tempProcessChargeArray, xcnt);
				seqNbr = tempProcessChargeValueObject.getSeqNbr();
				log.info("seq number after sorting = " + seqNbr + "...");

				// get the ProcessValueObject from the ProcessHash
				// using the seqNbr in the TempProcessChargeValueObject
				processValueObject = (ProcessValueObject) processHash.get(seqNbr);

				// get the chargeable tier sequence number and set into the corresponding
				// ProcessValueObject
				tierSeqNbr = tempProcessChargeValueObject.getTierSeqNbr();
				processValueObject.setChargeTier(tierSeqNbr);

				// add into the array list
				processList.add(processValueObject);
			}
		} catch (BusinessException e) {
			log.error("Exception: sortCntrEventDtl ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception: sortCntrEventDtl ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: sortCntrEventDtl  DAO Result:" + (processList != null ? processList.size() : ""));
		}
		return processList;
	}

	private List<TempProcessChargeValueObject> populateTempProcessCharge(Object[] cntrEventDtlArray, String tariffType)
			throws BusinessException {
		ProcessValueObject processValueObject = null;
		TariffMainVO tariffMainValueObject = null;
		TariffTierVO tariffTierValueObject = null;
		TempProcessChargeValueObject tempProcessChargeValueObject = null;
		List<TempProcessChargeValueObject> tempProcessChargeList = null;
		int xcnt = 0;
		int tierCnt = 0;
		int hashCnt = 0;
		String tariffMainCat = null;
		String tariffSubCat = null;
		Integer versionNbr = null;
		String tariffCd = null;
		Integer tierSeqNbr = null;
		double nbrTimeUnit = 0;
		double nbrOtherUnit = 0;
		String custCd = null;
		String acctNbr = null;
		String contractNbr = null;
		String vvCd = null;
		String slotOprCd = null;
		String cntrOprCd = null;
		Integer cntrSeqNbr = null;
		Timestamp txnDttm = null;
		String txnCd = null;
		Integer seqNbr = null;
		// CR-CAB-20050823 TT billing changes - Valli
		boolean isTTShortFall = false;
		// CR-CAB-20050518-1 - added by Valli
		boolean isAdminCharge = false;
		// added by mike
		boolean isOtherFreqExtraMovement = false;
		// added by mike
		try {
			log.info("START: populateTempProcessCharge DAO cntrEventDtlArray:" + cntrEventDtlArray.length
					+ ",tariffType:" + tariffType);
			processHash = new Hashtable<Integer, ProcessValueObject>();
			tempProcessChargeList = new ArrayList<TempProcessChargeValueObject>();

			// loop thru the list of container event log and prepare for sorting
			for (xcnt = 0; xcnt < cntrEventDtlArray.length; xcnt++) {
				tariffMainValueObject = new TariffMainVO();
				processValueObject = (ProcessValueObject) Array.get(cntrEventDtlArray, xcnt);

				// from processValueObject, get the details
				// to populate the temp table for sorting
				if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
					versionNbr = processValueObject.getVersionNbrPubl();
					log.info("versionNbrPubl = " + versionNbr + "...");
					if (!versionNbr.equals(new Integer(0)) && versionNbr != null) {
						tariffMainValueObject = getTariffMainByTariffType(processValueObject, tariffType);
					}
				}

				if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE)) {
					versionNbr = processValueObject.getVersionNbrCust();
					log.info("versionNbrCust = " + versionNbr + "...");
					if (!versionNbr.equals(new Integer(0)) && versionNbr != null) {
						tariffMainValueObject = getTariffMainByTariffType(processValueObject, tariffType);
					}

					if (!tariffMainValueObject.isModified(tariffMainValueObject)) {
						versionNbr = processValueObject.getVersionNbrPubl();
						log.info("versionNbrCust not found get versionNbrPubl= " + versionNbr + "...");
						if (!versionNbr.equals(new Integer(0)) && versionNbr != null) {
							tariffMainValueObject = getTariffMainByTariffType(processValueObject,
									ProcessChargeConst.TARIFF_TYPE_PUBLISH);
						}
					}
				}

				if ((!versionNbr.equals(new Integer(0))) && versionNbr != null
						&& (tariffMainValueObject.isModified(tariffMainValueObject))) {
					tariffCd = tariffMainValueObject.getCode();
					tariffMainCat = tariffMainValueObject.getMainCategory();
					tariffSubCat = tariffMainValueObject.getSubCategory();
					log.info("tariffCd = " + tariffCd + "...");
					log.info("tariffMainCat= " + tariffMainCat + "...");

					// CR-CAB-20050823 TT billing changes - Valli
					if (ProcessChargeConst.TARIFF_MAIN_LOLO.equals(tariffMainCat)
							&& ProcessChargeConst.TARIFF_SUB_LOLO_TT_SHORTFALL_CHARGE.equals(tariffSubCat)) {
						isTTShortFall = true;
					} else {
						isTTShortFall = false;
					}
					// START: CR-CAB-20050518-1 - Added by Valli
					if (ProcessChargeConst.TARIFF_MAIN_ADMIN.equals(tariffMainCat)
							&& ProcessChargeConst.TARIFF_SUB_ADMIN_CHANGES_AFTER_CLOSING.equals(tariffSubCat)) {
						isAdminCharge = true;
					} else {
						isAdminCharge = false;
					}
					// END: CR-CAB-20050518-1 - Added by Valli
					if (ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_FREQUENCY.equals(tariffMainCat)
							&& ProcessChargeConst.TARIFF_SUB_OTHERS_FREQ_EXTRA_MVT.equals(tariffSubCat)) {
						isOtherFreqExtraMovement = true;
					} else {
						isOtherFreqExtraMovement = false;
					}
					log.info("isTTShortFall = " + isTTShortFall + "..." + ", isAdminCharge = " + isAdminCharge
							+ ", isOtherFreqExtraMovement = " + isOtherFreqExtraMovement);
					custCd = processValueObject.getCustCd();
					log.info("custCd = " + custCd + "...");
					acctNbr = processValueObject.getAcctNbr();
					log.info("acctNbr = " + acctNbr + "...");
					contractNbr = processValueObject.getContractNbr();
					log.info("contractNbr = " + contractNbr + "...");
					// CR-CAB-20050823 TT billing changes - Valli
					if (isTTShortFall || isAdminCharge || (isOtherFreqExtraMovement)) { // Amended by Valli
																						// CR-CAB-20050518-1
						vvCd = processValueObject.getCntrEventLogValueObject().getVvCd();
					} else {
						vvCd = determineVvCd(processValueObject, tariffMainCat);
					}
					log.info("vvCd = " + vvCd + "...");
					slotOprCd = processValueObject.getSlotOprCd();
					log.info("slotOprCd = " + slotOprCd + "...");
					cntrOprCd = processValueObject.getCntrOprCd();
					log.info("cntrOprCd = " + cntrOprCd + "...");
					cntrSeqNbr = (processValueObject.getCntrEventLogValueObject()).getCntrSeqNbr();
					log.info("cntrSeqNbr = " + cntrSeqNbr + "...");
					txnDttm = (processValueObject.getCntrEventLogValueObject()).getTxnDttm();
					log.info("txnDttm = " + txnDttm + "...");
					txnCd = (processValueObject.getCntrEventLogValueObject()).getTxnCd();
					log.info("txnCd = " + txnCd + "...");

					// loop thru the tariff tier records
					for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
						// get the individual record of the tariff tier record
						tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);
						// Start: Lolo Rebates changes - By Valli - 18-May-2006
						if (tariffMainCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_LOLO)
								&& (tariffSubCat.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_PEAK)
										|| tariffSubCat.equalsIgnoreCase(
												ProcessChargeConst.TARIFF_SUB_LOLO_REBATES_OFF_PEAK))) {
							tierSeqNbr = new Integer(tariffTierValueObject.getId());
						} else {
							tierSeqNbr = new Integer(tierCnt + 1);
						}
						// End: Lolo Rebates changes - By Valli - 18-May-2006
						nbrTimeUnit = tariffTierValueObject.getTimeUnit();
						nbrOtherUnit = tariffTierValueObject.getOtherUnit();
						seqNbr = new Integer(hashCnt);
						log.info("seqNbr = " + seqNbr + "...");

						// populate the TempProcessChargeValueObject
						tempProcessChargeValueObject = new TempProcessChargeValueObject();
						tempProcessChargeValueObject.setVersionNbr(versionNbr);
						tempProcessChargeValueObject.setTariffCd(tariffCd);
						tempProcessChargeValueObject.setTierSeqNbr(tierSeqNbr);
						tempProcessChargeValueObject.setNbrTimeUnit(nbrTimeUnit);
						tempProcessChargeValueObject.setNbrOtherUnit(nbrOtherUnit);
						tempProcessChargeValueObject.setCustCd(custCd);
						tempProcessChargeValueObject.setAcctNbr(acctNbr);
						tempProcessChargeValueObject.setContractNbr(contractNbr);
						tempProcessChargeValueObject.setVvCd(vvCd);
						tempProcessChargeValueObject.setSlotOprCd(slotOprCd);
						tempProcessChargeValueObject.setCntrOprCd(cntrOprCd);
						tempProcessChargeValueObject.setCntrSeqNbr(cntrSeqNbr);
						tempProcessChargeValueObject.setTxnDttm(txnDttm);
						tempProcessChargeValueObject.setTxnCd(txnCd);
						tempProcessChargeValueObject.setSeqNbr(seqNbr);

						ProcessValueObject processValueObjectNew = new ProcessValueObject(processValueObject);

						if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
							versionNbr = processValueObjectNew.getVersionNbrPubl();
							log.info("versionNbrPubl = " + versionNbr + "...");
							if (!versionNbr.equals(new Integer(0)) && versionNbr != null) {
								tariffMainValueObject = getTariffMainByTariffType(processValueObjectNew, tariffType);
							}
						}

						if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE)) {
							versionNbr = processValueObjectNew.getVersionNbrCust();
							log.info("versionNbrCust = " + versionNbr + "...");
							if (!versionNbr.equals(new Integer(0)) && versionNbr != null) {
								tariffMainValueObject = getTariffMainByTariffType(processValueObjectNew, tariffType);
							}

							if (!tariffMainValueObject.isModified(tariffMainValueObject)) {
								versionNbr = processValueObjectNew.getVersionNbrPubl();
								log.info("versionNbrCust not found get versionNbrPubl= " + versionNbr + "...");
								if (!versionNbr.equals(new Integer(0)) && versionNbr != null) {
									tariffMainValueObject = getTariffMainByTariffType(processValueObjectNew,
											ProcessChargeConst.TARIFF_TYPE_PUBLISH);
								}
							}
						}

						// add into ProcessHash
						processHash.put(seqNbr, processValueObjectNew);
						// add into TempProcessChargeList
						tempProcessChargeList.add(tempProcessChargeValueObject);
						hashCnt++;
					}
				}
			} // end for loop
		} catch (Exception e) {
			log.error("Exception: populateTempProcessCharge ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: populateTempProcessCharge  DAO Result:"
					+ (tempProcessChargeList != null ? tempProcessChargeList.size() : ""));
		}
		return tempProcessChargeList;
	}

	public TariffMainVO getTariffMainByTariffType(ProcessValueObject processValueObject, String tariffType)
			throws BusinessException {
		TariffMainVO tariffMainValueObject = null;
		int mainCnt = 0;
		try {
			log.info("START: getTariffMainByTariffType DAO processValueObject:" + processValueObject.toString()
					+ ",tariffType:" + tariffType);

			mainCnt = processValueObject.getMainCount();

			if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
				if (mainCnt != 0) {
					tariffMainValueObject = processValueObject.getMain(0);
				}
			}

			if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE)) {
				if (mainCnt == 2) {
					tariffMainValueObject = processValueObject.getMain(1);
				}
			}
		} catch (Exception e) {
			log.error("Exception: getTariffMainByTariffType ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTariffMainByTariffType  DAO Result:"
					+ (tariffMainValueObject != null ? tariffMainValueObject.toString() : ""));
		}
		return tariffMainValueObject;
	}

	private String determineVvCd(ProcessValueObject processValueObject, String tariffMainCat) throws Exception {
		Integer cntrSeqNbr = null;
		String txnCd = null;
		String purpCd = null;
		String intergatewayInd = null;
		String vvCd = null;
		// added by swho on 15/04/2003 for psa link store rent
		String discGateway = null;
		// end add by swho
		// START: FPT implement CR-CAB-20080805-37 Uturn 26-Sep-2009
		// Added by CungTD
		// Use for billing enhancement for ITH -> Shut containers/Storage - EXIT
		boolean isShutEvent = false;
		// END: FPT implement CR-CAB-20080805-37 Uturn 26-Sep-2009
		try {
			log.info("START: determineVvCd DAO processValueObject:" + processValueObject.toString() + ",tariffMainCat:"
					+ tariffMainCat);
			cntrSeqNbr = (processValueObject.getCntrEventLogValueObject()).getCntrSeqNbr();
			txnCd = (processValueObject.getCntrEventLogValueObject()).getTxnCd();
			purpCd = (processValueObject.getCntrEventLogValueObject()).getPurpCd();
			intergatewayInd = (processValueObject.getCntrEventLogValueObject()).getIntergatewayInd();
			log.info("==================");
			log.info(cntrSeqNbr);
			log.info(txnCd);
			log.info(purpCd);
			log.info(intergatewayInd);
			// for Stevedorage Charges
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STEVEDORAGE)) {
				if (txnCd.equals(ConstantUtil.TXN_CD_DISCHARGE) || txnCd.equals(ConstantUtil.TXN_CD_LAND)
						|| txnCd.equals(ConstantUtil.TXN_CD_SHIFTING)) {
					vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
				} else if (txnCd.equals(ConstantUtil.TXN_CD_LOAD)) {
					vvCd = (processValueObject.getCntrEventLogValueObject()).getLdVvCd();
				}
			}

			// for Store Rent Charges
			// lsw 20101228 ST should be calling determineVvCd(processVO,
			// tariffMainCat,isShut) instead of this method
			// but codes still remain here just in case anyone call it. Good to have any
			// changes to the new determineVvCd replicate
			// here too just in case
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STORE_RENT)) {
				if (purpCd.equals(ProcessChargeConst.PURP_CD_TRANSHIP)
						|| purpCd.equals(ProcessChargeConst.PURP_CD_REEXPORT)) {
					// for ITH Store Rent
					// amended by swho on 15/04/2003 for psa link store rent
					// if (purpCd.equals(ProcessChargeConst.PURP_CD_TRANSHIP) &&
					// intergatewayInd.equals("Y")) {
					//// amended by swho on 080503 to bill against JP vsl for PSA-JP scenarion (bug
					// fix)
					/*
					 * // get ProcessGenericHome object processGenericHome =
					 * getProcessGenericHome(); // get ProcessGeneric object processGeneric =
					 * (ProcessGeneric)processGenericHome.create(); vvCd =
					 * processGeneric.retrieveNomDiscVvCd(cntrSeqNbr);
					 */
					// vvCd = (processValueObject.getCntrEventLogValueObject()).getLdVvCd();
					// end amend by swho
					// }
					// // for Normal TS/Reexport Store Rent
					// else {
					// vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
					// }

					// for ITH Store Rent
					if ((purpCd.equals(ProcessChargeConst.PURP_CD_TRANSHIP) && intergatewayInd.equals("Y"))
							|| (purpCd.equals(ProcessChargeConst.PURP_CD_REEXPORT) && intergatewayInd.equals("Y"))) {

						discGateway = processGenericRepo.retrieveDiscGateway(cntrSeqNbr);
						// for PSA-JP leg
						if (discGateway.equals("P")) {
							vvCd = (processValueObject.getCntrEventLogValueObject()).getLdVvCd();
						}
						// for JP-PSA leg
						else {
							vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
						}
					}
					// for Normal TS/Reexport Store Rent
					else {
						vvCd = (processValueObject.getCntrEventLogValueObject()).getLdVvCd();
					}
					// end amend by swho
				}
				// for Import Store Rent
				else if (purpCd.equals(ProcessChargeConst.PURP_CD_IMPORT)) {
					vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();

					if (vvCd == null) {
						// uturnIndicator = processGeneric.getUturnIndicator(cntrSeqNbr.intValue());
						isShutEvent = processGenericRepo.isShutout(cntrSeqNbr.intValue());

						// log.info("[CAB]","ProcessCommon::determineVvCd::uturnIndicator:" +
						// uturnIndicator);
						log.info("[CAB],ProcessCommon::determineVvCd::isShutEvent:" + isShutEvent);

						if ("EXIT".equals(txnCd) && isShutEvent) {
							vvCd = processGenericRepo.getLastLoadVvCd(
									(processValueObject.getCntrEventLogValueObject()).getCntrSeqNbr().intValue());
						}
					}
					// END: FPT implement CR-CAB-20080805-37 Uturn 26-Sep-2009
				}
				// for Export/Reshipment Store Rent
				else if (purpCd.equals(ProcessChargeConst.PURP_CD_EXPORT)
						|| purpCd.equals(ProcessChargeConst.PURP_CD_RESHIPMENT)) {
					vvCd = (processValueObject.getCntrEventLogValueObject()).getLdVvCd();
				}
				// for Storage Container
				else if (purpCd.equals(ProcessChargeConst.PURP_CD_STORAGE)) {
					vvCd = null;
				}
			}

			// for LOLO Charges
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_LOLO)) {
				if (purpCd.equals(ProcessChargeConst.PURP_CD_IMPORT)) {
					vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
				} else if ((purpCd.equals(ProcessChargeConst.PURP_CD_EXPORT))
						|| (purpCd.equals(ProcessChargeConst.PURP_CD_RESHIPMENT))) {
					vvCd = (processValueObject.getCntrEventLogValueObject()).getLdVvCd();
				}
				// START: FPT implement CR-CAB-20080805-37 Uturn 12-Nov-2009
				// Add by CungTD get vvCd for TS container
				else if (purpCd.equals(ProcessChargeConst.PURP_CD_TRANSHIP)
						|| purpCd.equals(ProcessChargeConst.PURP_CD_REEXPORT)) {

					if (intergatewayInd != null) {
						if (intergatewayInd.equals("N")) {
							vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
						} else {
							discGateway = processGenericRepo.retrieveDiscGateway(cntrSeqNbr);

							if (discGateway.equals("J")) {
								vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
							} else {
								vvCd = (processValueObject.getCntrEventLogValueObject()).getLdVvCd();
							}
						}
					} else {
						vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
					}

				}
				// END: FPT implement CR-CAB-20080805-37 Uturn 12-Nov-2009
				else if (purpCd.equals(ProcessChargeConst.PURP_CD_STORAGE)) {
					vvCd = null;
				}
			}

			// for Marine Charges
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE)) {
				vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
			}

			// VietNguyen (FPT) added on 07-Dec-2011 for OMC : START
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_DOCKAGE)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFAGE)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFSIDE)) {
				vvCd = (processValueObject.getCntrEventLogValueObject()).getVvCd();
			}
			// VietNguyen (FPT) added on 02-Dec-2011 for OMC : END

			// for Others By Vessel Charges
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_VESSEL)) {
				vvCd = (processValueObject.getCntrEventLogValueObject()).getLdVvCd();
			}

			// for Others By Frequency Charges
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_FREQUENCY)) {

				vvCd = processGenericRepo.determineVvCd(processValueObject.getCntrEventLogValueObject());
			}
			// Added By Alicia for containerised Cargo on 17 Dec 2003
			// for containerised store rent
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_GB_CONTAINER_STORE_RENT)) {
				if ((purpCd.equals(ProcessChargeConst.PURP_CD_IMPORT))
						|| (purpCd.equals(ProcessChargeConst.PURP_CD_LAND_RESHIP))) {
					vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
				}
				if ((purpCd.equals(ProcessChargeConst.PURP_CD_EXPORT))
						|| (purpCd.equals(ProcessChargeConst.PURP_CD_RESHIPMENT))) {
					vvCd = (processValueObject.getCntrEventLogValueObject()).getLdVvCd();
				}
				// for ITH Store Rent
				if ((purpCd.equals(ProcessChargeConst.PURP_CD_TRANSHIP)
						|| (purpCd.equals(ProcessChargeConst.PURP_CD_REEXPORT)))) {
					if (intergatewayInd.equals("Y")) {
						// get ProcessGenericHome object

						// retrieve discharge gateway
						discGateway = processGenericRepo.retrieveDiscGateway(cntrSeqNbr);
						if (discGateway.equals("P")) {
							vvCd = (processValueObject.getCntrEventLogValueObject()).getLdVvCd();
						}
						// for JP-PSA leg
						else {
							vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
						}
					} else {
						vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
					}
				}
			}
			// for containerised service
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_GB_CONTAINER_SERVICE_CHARGE)) {
				/*
				 * if(purpCd.equals(ProcessChargeConst.PURP_CD_IMPORT)) { vvCd =
				 * (processValueObject.getCntrEventLogValueObject()).getDiscVvCd(); }
				 * if((purpCd.equals(ProcessChargeConst.PURP_CD_EXPORT)) ||
				 * (purpCd.equals(ProcessChargeConst.PURP_CD_RESHIPMENT))) { vvCd =
				 * (processValueObject.getCntrEventLogValueObject()).getLdVvCd(); } // for ITH
				 * Store Rent if ((purpCd.equals(ProcessChargeConst.PURP_CD_TRANSHIP) ||
				 * (purpCd.equals(ProcessChargeConst.PURP_CD_REEXPORT)))) { if(
				 * intergatewayInd.equals("Y")){ // get ProcessGenericHome object
				 * processGenericHome = getProcessGenericHome(); // get ProcessGeneric object
				 * processGeneric = (ProcessGeneric)processGenericHome.create(); // retrieve
				 * discharge gateway discGateway =
				 * processGeneric.retrieveDiscGateway(cntrSeqNbr);
				 * 
				 * if (discGateway.equals("P")) { vvCd =
				 * (processValueObject.getCntrEventLogValueObject()).getLdVvCd(); } // for
				 * JP-PSA leg else { vvCd =
				 * (processValueObject.getCntrEventLogValueObject()).getDiscVvCd(); } } else {
				 * vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd(); } }
				 */
				if (txnCd.equals(ConstantUtil.TXN_CD_GB_DISCHARGE)) {
					vvCd = (processValueObject.getCntrEventLogValueObject()).getDiscVvCd();
				} else if (txnCd.equals(ConstantUtil.TXN_CD_GB_LOAD)) {
					vvCd = (processValueObject.getCntrEventLogValueObject()).getLdVvCd();
				}
			}
			// end end for containerised Cargo on 17 Dec 2003
		} catch (Exception e) {
			log.error("Exception: determineVvCd ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineVvCd  DAO Result:" + vvCd);
		}
		return vvCd;
	}

	static public Timestamp getCurrentTimestamp() throws BusinessException {
		Timestamp ts = null;
		try {
			log.info("START: getCurrentTimestamp DAO");
			ts = new Timestamp(System.currentTimeMillis());
		} catch (Exception e) {
			log.error("Exception: determineVvCd ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineVvCd  DAO Result:" + ts.toString());
		}
		return ts;
	}

	private BillItemVO determineBillable(BillItemVO billItemValueObject, TariffTierVO tariffTierValueObject,
			String tariffMainCat, String tariffType) throws BusinessException {
		double timeUnitBase = 0;
		double otherUnitBase = 0;
		double origTimeUnit = 0;
		double origOtherUnit = 0;
		double timeUnit = 0;
		double otherUnit = 0;
		double unitRate = 0;
		String adjType = null;
		double adjAmt = 0;
		try {
			log.info("START: determineBillable DAO billItemValueObject:" + billItemValueObject.toString()
					+ ",tariffTierValueObject:" + tariffTierValueObject.toString() + ",tariffMainCat:" + tariffMainCat
					+ ",tariffType:" + tariffType);
			// from tariffTierValueObject, get the details for processing
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STEVEDORAGE)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_LOLO)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_VESSEL)) {
				timeUnitBase = tariffTierValueObject.getPerHour();

				if (timeUnitBase < 0) {
					timeUnitBase = 1;
				}

				otherUnitBase = tariffTierValueObject.getPerUnit();
				if (otherUnitBase < 0) {
					otherUnitBase = 1;
				}

				origTimeUnit = tariffTierValueObject.getTimeUnit();
				timeUnit = (origTimeUnit / timeUnitBase);

				origOtherUnit = tariffTierValueObject.getOtherUnit();
				otherUnit = (origOtherUnit / otherUnitBase);
				log.info("other unit = " + otherUnit + "...");
			} else if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STORE_RENT)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_FREQUENCY)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_ADMIN)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WATER_ORDER)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_VTSB) ||
					// Added by MC Consulting for LWMS KCP
					tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_LWMS_KCP) ||
					// End of addition by MC Consulting for LWMS KCP
					tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S2_CHARGE)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S3M_CHARGE)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_BULK_CARGO_S3P_CHARGE) ||
					// VietNguyen (FPT) added on 07-Dec-2011 for OMC
					tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_DOCKAGE)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFAGE)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFSIDE)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_EQUIPMENT_RENTAL)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_PLOT_USAGE)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_OTHER)) { // CR-CAB-20050518-1 - Amended
																							// by Valli
				timeUnit = tariffTierValueObject.getTimeUnit();
				log.info("time unit = " + timeUnit + "...");
				otherUnit = tariffTierValueObject.getOtherUnit();
				log.info("other unit = " + otherUnit + "...");
			}

			if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
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
			log.info("unit rate = " + unitRate + "...");

			// set the time unit and other unit into the BillItem value object
			billItemValueObject.setTotalTime(timeUnit);
			billItemValueObject.setTotalOtherUnit(otherUnit);
			billItemValueObject.setUnitRate(unitRate);
		} catch (Exception e) {
			log.error("Exception: determineBillable ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineBillable  DAO Result:"
					+ (billItemValueObject != null ? billItemValueObject.toString() : ""));
		}
		return billItemValueObject;
	}

	public String determineSlotOprCd(CntrEventLogValueObject cntrEventLogValueObject, String tariffMainCat)
			throws BusinessException {
		String discGateway = null;
		String txnCd = null;
		String purpCd = null;
		String intergatewayInd = null;
		String slotOprCd = null;
		try {
			log.info("START: determineSlotOprCd DAO cntrEventLogValueObject:" + cntrEventLogValueObject.toString()
					+ ",tariffMainCat:" + tariffMainCat);
			txnCd = cntrEventLogValueObject.getTxnCd();
			purpCd = cntrEventLogValueObject.getPurpCd();
			intergatewayInd = cntrEventLogValueObject.getIntergatewayInd();

			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STEVEDORAGE)) {
				if (txnCd.equals(ConstantUtil.TXN_CD_DISCHARGE) || txnCd.equals(ConstantUtil.TXN_CD_LAND)
						|| txnCd.equals(ConstantUtil.TXN_CD_SHIFTING)) {
					slotOprCd = cntrEventLogValueObject.getDiscSlotOprCd();
				} else if (txnCd.equals(ConstantUtil.TXN_CD_LOAD)) {
					slotOprCd = cntrEventLogValueObject.getLoadSlotOprCd();
				}
			}

			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STORE_RENT)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_ADMIN)) { // CR-CAB-20050518-1 - Amended by
																						// Valli
				if (purpCd.equals(ProcessChargeConst.PURP_CD_IMPORT)
						|| purpCd.equals(ProcessChargeConst.PURP_CD_TRANSHIP)
						|| purpCd.equals(ProcessChargeConst.PURP_CD_REEXPORT)) {
					// added by swho on 040302 for ITH Store Rent
					if (purpCd.equals(ProcessChargeConst.PURP_CD_TRANSHIP) && intergatewayInd.equals("Y")) {
						// amended by swho on 200503 to break into new bill when change of vv cd and
						// tariff sub cat
						// slotOprCd = "";
						try {

							discGateway = processGenericRepo
									.retrieveDiscGateway(cntrEventLogValueObject.getCntrSeqNbr());
							if (discGateway.equals("J")) {
								slotOprCd = cntrEventLogValueObject.getDiscSlotOprCd();
							} else {
								slotOprCd = cntrEventLogValueObject.getLoadSlotOprCd();
							}
							// slotOprCd = cntrEventLogValueObject.getLoadSlotOprCd();
							// end amend by swho

						} catch (Exception e) {

							log.error("Exception: determineSlotOprCd ", e);
							throw new BusinessException("M4201");
						}

					} else {
						// end add by swho
						slotOprCd = cntrEventLogValueObject.getDiscSlotOprCd();
					}
				} else if (purpCd.equals(ProcessChargeConst.PURP_CD_EXPORT)
						|| purpCd.equals(ProcessChargeConst.PURP_CD_RESHIPMENT)) {
					slotOprCd = cntrEventLogValueObject.getLoadSlotOprCd();
				}
			}

			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_LOLO)) {
				if (purpCd.equals(ProcessChargeConst.PURP_CD_IMPORT)) {
					slotOprCd = cntrEventLogValueObject.getDiscSlotOprCd();
				} /*
					 * Added or statement to get slotOprCd for Re-ship containers - Manohar Request
					 * Number CR-CAB-20040922-4
					 */
				else if (purpCd.equals(ProcessChargeConst.PURP_CD_EXPORT)
						|| purpCd.equals(ProcessChargeConst.PURP_CD_RESHIPMENT)) {
					slotOprCd = cntrEventLogValueObject.getLoadSlotOprCd();
				}
			}

			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_VESSEL)) {
				slotOprCd = cntrEventLogValueObject.getLoadSlotOprCd();
			}

			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_FREQUENCY)) {
				// if (txnCd.equals(ConstantUtil.TXN_CD_RENOM)) {
				// slotOprCd = cntrEventLogValueObject.getRenomSlotOprCd();
				// }
				// else
				// amended by swho on 200503 to break into new bill when change of vv cd and
				// tariff sub cat
				// if (purpCd.equals(ProcessChargeConst.PURP_CD_REEXPORT) ||
				// purpCd.equals(ProcessChargeConst.PURP_CD_IMPORT)) {
				if (purpCd.equals(ProcessChargeConst.PURP_CD_REEXPORT)
						|| purpCd.equals(ProcessChargeConst.PURP_CD_IMPORT)
						|| purpCd.equals(ProcessChargeConst.PURP_CD_LAND_RESHIP)) {
					// end amend by swho
					slotOprCd = cntrEventLogValueObject.getDiscSlotOprCd();
				} else if (purpCd.equals(ProcessChargeConst.PURP_CD_RESHIPMENT)
						|| purpCd.equals(ProcessChargeConst.PURP_CD_EXPORT)) {
					slotOprCd = cntrEventLogValueObject.getLoadSlotOprCd();
				} else if (purpCd.equals(ProcessChargeConst.PURP_CD_TRANSHIP)) {
					if (intergatewayInd != null) {
						if (intergatewayInd.equals("N")) {
							slotOprCd = cntrEventLogValueObject.getDiscSlotOprCd();
						}
						// added by swho on 200503 to break into new bill when change of vv cd and
						// tariff sub cat
						else {
							// continue processing in ProcessGeneric populateCustomize() for ITH cntrs
							slotOprCd = null;
						}
						// end add by swho
					} else {
						slotOprCd = cntrEventLogValueObject.getDiscSlotOprCd();
					}
				}
			}
		} catch (Exception e) {
			log.error("Exception: determineSlotOprCd ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineSlotOprCd  DAO Result:" + slotOprCd);
		}
		return slotOprCd;
	}

	// /ejb.sessionBeans.cab.processCharges -->getListForSplitBills
	public Hashtable<String, String> getListForSplitBills() throws BusinessException {
		Hashtable<String, String> custCdList = new Hashtable<String, String>();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rsResult = null;
		try {
			log.info("START: getListForSplitBills  DAO ");

			String getCustListSQL = " select cust_cd from customer where split_sv_bill_ind = 'Y' ";

			log.info(" *** getListForSplitBills SQL *****" + getCustListSQL);

			rsResult = namedParameterJdbcTemplate.queryForRowSet(getCustListSQL, paramMap);

			while (rsResult.next()) {
				log.info(" cust_cd : " + rsResult.getString("cust_cd"));
				custCdList.put(rsResult.getString("cust_cd"), rsResult.getString("cust_cd"));
			}
			log.info("END: *** getListForSplitBills Result *****" + custCdList.toString());
		} catch (NullPointerException e) {
			log.error("exception: getListForSplitBills " + e.toString());
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("exception: getListForSplitBills " + e.toString());
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getListForSplitBills  DAO  END");
		}
		return custCdList;
	}

	public int[] getIndicator(String tariffCode) throws BusinessException {
		int[] ind = {};
		StringBuffer mainCat = new StringBuffer();
		StringBuffer subCat = new StringBuffer();
		StringBuffer cntrCat = new StringBuffer();
		StringBuffer bType = new StringBuffer();
		try {
			log.info("START: getIndicator DAO tariffCode:" + tariffCode);

			bType.append(tariffCode.charAt(0));
			mainCat.append(tariffCode.charAt(1));
			mainCat.append(tariffCode.charAt(2));
			subCat.append(tariffCode.charAt(3));
			subCat.append(tariffCode.charAt(4));

			// get the container category
			if (bType.toString().equals(ProcessChargeConst.CONTAINER_BUSINESS)) {
				cntrCat.setLength(0);
				cntrCat.append(tariffCode.charAt(6));
				cntrCat.append(tariffCode.charAt(7));
				if (!cntrCat.toString().equals("UC")) { // for uc cntr
					cntrCat.setLength(0);
					cntrCat.append('~');
				}
			} else if (bType.toString().equals(ProcessChargeConst.GENERAL_BUSINESS)
					|| bType.toString().equals(ProcessChargeConst.BULK_BUSINESS)) {
				if (mainCat.toString().equals(ProcessChargeConst.TARIFF_MAIN_GB_CONTAINER_SERVICE_CHARGE)
						|| mainCat.toString().equals(ProcessChargeConst.TARIFF_MAIN_GB_CONTAINER_STORE_RENT)) {
					// follow CT for containerised cargo
					cntrCat.setLength(0);
					cntrCat.append(tariffCode.charAt(6));
					cntrCat.append(tariffCode.charAt(7));
					if (!cntrCat.toString().equals("UC")) { // for uc cntr
						cntrCat.setLength(0);
						cntrCat.append('~');
					}
				} else {
					cntrCat.setLength(0);
					cntrCat.append('~');
				}

			} else {
				cntrCat.append('~');
			}
			ind = getIndicator(bType.toString(), mainCat.toString(), subCat.toString(), cntrCat.toString());
		} catch (Exception e) {
			log.error("exception: getIndicator " + e.toString());
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getIndicator  DAO Result:" + ind.length);
		}
		return ind;
	}

	public int[] getIndicator(String businessType, String mainCategory, String subCategory, String containerCategory)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int retVal[] = { -1, -1, -1 };
		try {
			log.info("START: getIndicator  DAO businessType:" + businessType + ",mainCategory:" + mainCategory
					+ ",subCategory:" + subCategory + ",containerCategory:" + containerCategory);

			String sql = "SELECT cntr_unit, time_unit, other_unit FROM tariff_billable WHERE business_type= :businessType AND main_cat_cd= :mainCategory AND sub_cat_cd= :subCategory AND cntr_cat_cd= :containerCategory ";

			paramMap.put("businessType", businessType);
			paramMap.put("mainCategory", mainCategory);
			paramMap.put("subCategory", subCategory);
			paramMap.put("containerCategory", containerCategory);

			log.info(" *** getIndicator SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String s[] = { "", "", "" };
			if (rs != null && rs.next()) {
				s[0] = rs.getString("cntr_unit");
				s[1] = rs.getString("time_unit");
				s[2] = rs.getString("other_unit");

				rs = null;
			}

			for (int i = 0; i < s.length; i++) {
				if (s[i] != null) {
					if (s[i].equals("Y")) {
						retVal[i] = 1;
					} else if (s[i].equals("N")) {
						retVal[i] = 0;
					} else {
						retVal[i] = -1;
					}
				} else {
					retVal[i] = -1;
				}
			}
			log.info("END: *** getIndicator Result *****" + retVal);
		} catch (NullPointerException e) {
			log.error("Exception: getIndicator ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getIndicator ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getIndicator  DAO  END");
		}
		return retVal;
	}
}
