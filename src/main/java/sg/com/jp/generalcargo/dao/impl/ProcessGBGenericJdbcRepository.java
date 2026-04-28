package sg.com.jp.generalcargo.dao.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.BillSupportInfoRepository;
import sg.com.jp.generalcargo.dao.LWMSCommonUtilRepository;
import sg.com.jp.generalcargo.dao.LighterTerminalRepository;
import sg.com.jp.generalcargo.dao.ProcessBillingRepository;
import sg.com.jp.generalcargo.dao.ProcessGBGenericRepository;
import sg.com.jp.generalcargo.dao.ProcessGenericRepository;
import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.domain.BerthRelatedValueObject;
import sg.com.jp.generalcargo.domain.BillAdjParamCOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCTOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCTVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCVO;
import sg.com.jp.generalcargo.domain.BillAdjParamOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamTOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamTVO;
import sg.com.jp.generalcargo.domain.BillAdjustParam;
import sg.com.jp.generalcargo.domain.BillErrorVO;
import sg.com.jp.generalcargo.domain.CargoDeclarationVO;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.ContractValueObject;
import sg.com.jp.generalcargo.domain.CranageVO;
import sg.com.jp.generalcargo.domain.GstCodeValueObject;
import sg.com.jp.generalcargo.domain.ProcessGBValueObject;
import sg.com.jp.generalcargo.domain.TariffMainVO;
import sg.com.jp.generalcargo.domain.TariffTierVO;
import sg.com.jp.generalcargo.domain.UserTimestampVO;
import sg.com.jp.generalcargo.domain.VesselRelatedValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;
import sg.com.jp.generalcargo.util.ProcessChargeException;

@Repository("ProcessGBGenericJdbcRepository")
public class ProcessGBGenericJdbcRepository implements ProcessGBGenericRepository {

	private static final Log log = LogFactory.getLog(ProcessGBGenericJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	private LWMSCommonUtilRepository lwmsComUtilRepo;
	@Autowired
	private ProcessGenericRepository processGenericRepository;
	@Autowired
	private ProcessBillingRepository processBillRepo;
	@Autowired
	private LighterTerminalRepository lighterTermRepo;
	@Autowired
	private BillSupportInfoRepository billSupInfoRepo;
	// StartRegion ProcessGBGenericJdbcRepository

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBGenericEJB-->retrieveBillable()
	public List<ProcessGBValueObject> retrieveBillable(List<ChargeableBillValueObject> chargeableBillList) throws BusinessException {
		
		List<ProcessGBValueObject> processGBList = null;
		ProcessGBValueObject processGBValueObject = null;
		ChargeableBillValueObject chargeableBillVOPubl = null;
		ChargeableBillValueObject chargeableBillVOCust = null;
		AccountValueObject accountValueObject = null;
		ContractValueObject contractValueObject = null;
		BillErrorVO billErrorValueObject = null;
		int xcnt = 0;
		String refNbr = null;
		Timestamp txnDttm = null;
		String refInd = null;
		String tariffCd = null;
		int tierSeqNbr = 0;
		String localLeg = null;
		String tariffMainCatCd = null;
		String tariffSubCatCd = null;
		String vvCd = null;
		Timestamp varDttm = null;
		int custVersionNbr = 0;
		String custCd = null;
		String acctNbr = null;
		String contractNbr = null;
		int contractualYr = 0;
		try {
			log.info("START retrieveBillable DAO :: chargeableBillList: " + CommonUtility.deNull(chargeableBillList.toString()));
			// initialize the process gb list to populate all the related publish and
			// customize info based on per chargeable publish bill basis
			processGBList = new ArrayList<ProcessGBValueObject>();

			// loop the chargeable bill list
			for (xcnt = 0; xcnt < chargeableBillList.size(); xcnt++) {
				try {
					// get the individual record of the chargeable bill record
					processGBValueObject = new ProcessGBValueObject();
					chargeableBillVOPubl = (ChargeableBillValueObject) chargeableBillList.get(xcnt);
					chargeableBillVOCust = new ChargeableBillValueObject(chargeableBillVOPubl);
					chargeableBillVOCust.setTariffType(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE);

					refNbr = chargeableBillVOPubl.getRefNbr();
					txnDttm = chargeableBillVOPubl.getTxnDttm();
					refInd = chargeableBillVOPubl.getRefInd();
					tariffCd = chargeableBillVOPubl.getTariffCd();
					tierSeqNbr = chargeableBillVOPubl.getTierSeqNbr();
					localLeg = chargeableBillVOPubl.getLocalLeg();
					tariffMainCatCd = chargeableBillVOPubl.getTariffMainCatCd();
					tariffSubCatCd = chargeableBillVOPubl.getTariffSubCatCd();
					acctNbr = chargeableBillVOPubl.getAcctNbr();
					vvCd = chargeableBillVOPubl.getVvCd();

					// mongkey Dec 2011
					// check if the bill is for Lighter Terminal
					String prefixMainCatCd = tariffMainCatCd.substring(0, 1);
//						LWMSCommonUtilHome commonUtilHome = (LWMSCommonUtilHome) homeFactory
//								.lookUpHome(Constant.LWMS_EJB_CommonUtil);
//						LWMSCommonUtil commonUtil = (LWMSCommonUtil) commonUtilHome.create();
					boolean isLT = lwmsComUtilRepo.isBusTypeLighterTerminal(prefixMainCatCd);
					log.info("ProcessGBGenericEJB.retrieveBillable isLT: " + prefixMainCatCd + " *** " + isLT);
					log.info("MainCatCode Prefix: " + prefixMainCatCd);

					// START: Automation of GB Misc Charges - Added by Valli
					if ((ProcessChargeConst.TARIFF_MAIN_ADMIN.equalsIgnoreCase(tariffMainCatCd)
							&& (ProcessChargeConst.TARIFF_SUB_ADMIN_OVERNIGHT_PARK_AUTH.equalsIgnoreCase(tariffSubCatCd)
									// START 25-Feb-2011 - TPA ThangNC added for trailer parking applications
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_TPA_AUTH.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_TPA_AUTH_NORMAL_CARGO
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_TPA_AUTH_DG_CARGO
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_TPA_AUTH_OOG_CARGO
											.equalsIgnoreCase(tariffSubCatCd)
									// END 25-Feb-2011 - TPA ThangNC added for trailer parking applications
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_SUPPLY_ELEC_20
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_SUPPLY_ELEC_40
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_CLOSE_SPACE.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_CLOSE_SPACE_GST
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_OPEN_SPACE.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_OPEN_SPACE_GST
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_SPREADER.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_SPREADER_GST.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_STEEL_SPREADER
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_STEEL_SPREADER_GST
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_HOTWORK_INSP.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_HOTWORK_INSP_GST
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_FIREMAN.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_FIREMAN_GST.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_CONTRACTOR_PERMIT
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_CONTRACTOR_PERMIT_GST
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_OVERNIGHT_PARK_AUTH
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_FORKLIFT_BELOW_10
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_FORKLIFT_BELOW_10_GST
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_FORKLIFT_ABOVE_10
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_FORKLIFT_ABOVE_10_GST
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_FORKLIFT_ABOVE_10_GST
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_CNTR_LIFTER.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_CNTR_LIFTER_GST
											.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_WHEEL_LOADER.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_ADMIN_MOBILE_CRANE
											.equalsIgnoreCase(tariffSubCatCd)))// START: Automation of GB Warehouse -
																				// CFG
							|| (ProcessChargeConst.TARIFF_MAIN_GB_STORAGE.equalsIgnoreCase(tariffMainCatCd)
									&& (ProcessChargeConst.TARIFF_SUB_GB_STORAGE_TRANSIT_WAREHOUSE
											.equalsIgnoreCase(tariffSubCatCd)
											|| ProcessChargeConst.TARIFF_SUB_GB_STORAGE_BACKUP_WAREHOUSE
													.equalsIgnoreCase(tariffSubCatCd)
											|| ProcessChargeConst.TARIFF_SUB_GB_STORAGE_OPEN_SPACE
													.equalsIgnoreCase(tariffSubCatCd)
											|| ProcessChargeConst.TARIFF_SUB_GB_STORAGE_CNTR_YARD
													.equalsIgnoreCase(tariffSubCatCd)
											|| ProcessChargeConst.TARIFF_SUB_GB_STORAGE_TRANSIT_WAREHOUSE_GST
													.equalsIgnoreCase(tariffSubCatCd)
											|| ProcessChargeConst.TARIFF_SUB_GB_STORAGE_BACKUP_WAREHOUSE_GST
													.equalsIgnoreCase(tariffSubCatCd)
											|| ProcessChargeConst.TARIFF_SUB_GB_STORAGE_OPEN_SPACE_GST
													.equalsIgnoreCase(tariffSubCatCd)
											|| ProcessChargeConst.TARIFF_SUB_GB_STORAGE_CNTR_YARD_GST
													.equalsIgnoreCase(tariffSubCatCd))
									|| ProcessChargeConst.TARIFF_MAIN_MARINE.equalsIgnoreCase(tariffMainCatCd)
											&& ProcessChargeConst.TARIFF_SUB_MAR_SPECIAL_DOCKAGE
													.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY.equalsIgnoreCase(tariffSubCatCd)
									|| ProcessChargeConst.TARIFF_SUB_MAR_BERTH_UNBERTH.equalsIgnoreCase(tariffSubCatCd))
							// Thangtv: Added for JO bill processing
							|| ProcessChargeConst.TARIFF_MAIN_JPONLINE.equalsIgnoreCase(tariffMainCatCd)
							// Thangtv: End Add
							|| ProcessChargeConst.TARIFF_MAIN_RENTAL.equalsIgnoreCase(tariffMainCatCd) // GiangNN: Added
																										// for Penjuru
																										// bill
																										// processing
							|| ProcessChargeConst.TARIFF_MAIN_DOCKAGE.equalsIgnoreCase(tariffMainCatCd) // GiangNN:
																										// Added for
																										// Penjuru bill
																										// processing
							|| ProcessChargeConst.TARIFF_MAIN_ADMIN_PEN.equalsIgnoreCase(tariffMainCatCd) // GiangNN:
																											// Added for
																											// Penjuru
																											// bill
																											// processing
							|| ProcessChargeConst.TARIFF_MAIN_UTIL.equalsIgnoreCase(tariffMainCatCd) // GiangNN: Added
																										// for Penjuru
																										// bill
																										// processing
							|| ProcessChargeConst.TARIFF_MAIN_WHARFAGE_PEN.equalsIgnoreCase(tariffMainCatCd) // GiangNN:
																												// Added
																												// for
																												// Penjuru
																												// bill
																												// processing
							|| ProcessChargeConst.TARIFF_MAIN_OMC_RENTAL.equalsIgnoreCase(tariffMainCatCd) // VietNguyen:
																											// Added for
																											// OMC
																											// Penjuru
																											// Canteen
																											// bill
																											// processing
							|| ProcessChargeConst.TARIFF_MAIN_OMC_UTIL.equalsIgnoreCase(tariffMainCatCd) // VietNguyen:
																											// Added for
																											// OMC
																											// Penjuru
																											// Utility
																											// bill
																											// processing
							|| (isLT && (tariffMainCatCd
									.equals(prefixMainCatCd + ProcessChargeConst.TARIFF_MAINCD_RENTAL_SUFFIX) // added
																												// for
																												// Lighter
																												// Terminal
																												// bill
																												// processing
									|| tariffMainCatCd
											.equals(prefixMainCatCd + ProcessChargeConst.TARIFF_MAINCD_ADMIN_SUFFIX) // added
																														// for
																														// Lighter
																														// Terminal
																														// bill
																														// processing
									|| tariffMainCatCd
											.equals(prefixMainCatCd + ProcessChargeConst.TARIFF_MAINCD_UTIL_SUFFIX) // added
																													// for
																													// Lighter
																													// Terminal
																													// bill
																													// processing
									|| tariffMainCatCd
											.equals(prefixMainCatCd + ProcessChargeConst.TARIFF_MAINCD_WHARFAGE_SUFFIX) // added
																														// for
																														// Lighter
																														// Terminal
																														// bill
																														// processing
									|| tariffMainCatCd
											.equals(prefixMainCatCd + ProcessChargeConst.TARIFF_MAINCD_DOCKAGE_SUFFIX) // added
																														// for
																														// Lighter
																														// Terminal
																														// bill
																														// processing
									|| tariffMainCatCd
											.equals(prefixMainCatCd + ProcessChargeConst.TARIFF_MAINCD_CRANE_SUFFIX) // added
																														// for
																														// Lighter
																														// Terminal
																														// bill
																														// processing
									|| tariffMainCatCd.equals(
											prefixMainCatCd + ProcessChargeConst.TARIFF_MAINCD_CRANEWHARF_SUFFIX) // added
																													// for
																													// Lighter
																													// Terminal
																													// bill
																													// processing
									|| tariffMainCatCd.equals(
											prefixMainCatCd + ProcessChargeConst.TARIFF_MAINCD_SPECIAL_CARGO_SUFFIX) // added
																														// for
																														// Lighter
																														// Terminal
																														// bill
																														// processing
							))) {// END: Automation of GB Warehouse - CFG
						varDttm = chargeableBillVOPubl.getVarDttm();
						log.info("GBGeneric, CFG var dttm = " + varDttm + "...");
					} else { // END: Automation of GB Misc Charges - Added by Valli
						varDttm = retrieveGbCurrentTimestamp(vvCd, tariffMainCatCd);
						log.info("GBGeneric, CFG else var dttm = " + varDttm + "...");
					}

					log.info("*****");
					log.info("~ref_nbr = " + refNbr + "...");
					log.info("~ref_ind (" + refInd + "...");
					log.info("~acct_nbr (" + acctNbr + "...");
					log.info("~var dttm = " + varDttm + "...");
					// retrieve the customer account details for further processing
					accountValueObject = processGenericRepository.retrieveCustAcct(null, acctNbr);

					if (accountValueObject == null)
						log.info("~account value object = " + accountValueObject + "...");

					// retrieve the customer contract
					custCd = accountValueObject.getCustomerCode();
					acctNbr = accountValueObject.getAccountNumber();
					log.info("~cust_cd ( " + custCd + ")...");

					// start change HKM - for warehouse biling - 20 Jul 2007

					contractValueObject = processGenericRepository.retrieveCustContract(custCd, acctNbr, null, varDttm);

					if (ProcessChargeConst.TARIFF_MAIN_GB_STORAGE.equalsIgnoreCase(tariffMainCatCd)) {
						contractValueObject = processGenericRepository.retrieveCustContract(custCd, acctNbr,
								chargeableBillVOCust.getContractNbr(), varDttm);
						log.info("GBGeneric, Retrieve contractVO via contractNbr ="
								+ chargeableBillVOCust.getContractNbr());
					}
					// Thangtv: Added if block for JO bill processing
					else if (ProcessChargeConst.TARIFF_MAIN_JPONLINE.equals(tariffMainCatCd)) {
						contractValueObject = processGenericRepository.retrieveCustContract(custCd, acctNbr,
								chargeableBillVOPubl.getContractNbr(), varDttm);
						// Thangtv: End Added if block for JO bill processing
					} else {
						contractValueObject = processGenericRepository.retrieveCustContract(custCd, acctNbr, null, varDttm);
						log.info("GBGeneric, Retrieve contractVO via contractNbr = null");
					}
					// end change HKM - for warehouse biling - 20 Jul 2007

					log.info("cust contract returned");

					// contract exist, search for customize tariff
					if (contractValueObject.isModified(contractValueObject)) {
						log.info("contract exist");
						// retrieve customer tariff version number
						custVersionNbr = contractValueObject.getCustomerTariffVersion();
						log.info("~cust version nbr = " + custVersionNbr + "...");

						// retrieve the customized tariff, when customize version number is valid
						if (custVersionNbr != 0) {
							contractNbr = contractValueObject.getContractNumber();
							log.info("~contract nbr = " + contractNbr + "...");
							contractualYr = processGenericRepository.determineContractualYr(acctNbr, contractNbr, varDttm);
							log.info("~contract yr = " + contractualYr + "...");

							// edit to get customized charge for Crane/ Crane left on Wharf/ Crane Operator
							// main category - 17/04/2013
							// Add by ThangPV, 21-05-2013: LWMS - exclude customized tariff for special
							// cargo
							if (!isChargeForSpecialCargo(chargeableBillVOPubl)) {
								if (Arrays.asList(ProcessChargeConst.PENJURU_MARINA_CRANE).contains(tariffMainCatCd)) {
									chargeableBillVOCust = retrieveCustomizeChargeableForCrane(custVersionNbr, custCd,
											acctNbr, contractNbr, contractualYr, chargeableBillVOPubl);
								} else {
									chargeableBillVOCust = processGenericRepository.retrieveCustomizeChargeable(
											custVersionNbr, custCd, acctNbr, contractNbr, contractualYr,
											chargeableBillVOPubl);
								}
							}
							// END Add by ThangPV, 21-05-2013: LWMS - exclude customized tariff for special
							// cargo
							// edit end

						} // end-if for customize version number = valid
					} // end-if for existence of contract

					// Combine both the publish and customize ProcessGBValueObject
					processGBValueObject.setCustCd(chargeableBillVOCust.getCustCd());
					processGBValueObject.setAcctNbr(chargeableBillVOCust.getAcctNbr());
					processGBValueObject.setVvCd(chargeableBillVOCust.getVvCd());
					processGBValueObject.setRefInd(chargeableBillVOCust.getRefInd());
					processGBValueObject.setRefNbr(chargeableBillVOCust.getRefNbr());
					processGBValueObject.setTariffCd(chargeableBillVOCust.getTariffCd());
					processGBValueObject.setContractualYr(chargeableBillVOCust.getContractualYr());
					processGBValueObject.setTierSeqNbr(chargeableBillVOCust.getTierSeqNbr());
					processGBValueObject.setTariffMainCatCd(chargeableBillVOCust.getTariffMainCatCd());
					processGBValueObject.setTariffSubCatCd(chargeableBillVOCust.getTariffSubCatCd());

					processGBValueObject.addCharge(chargeableBillVOPubl);
					log.info("add publ chargeable");
					processGBValueObject.addCharge(chargeableBillVOCust);
					log.info("add cust chargeable");

					processGBList.add(processGBValueObject);
				} catch (ProcessChargeException processChargeException) {
//						if (billErrorHome == null || billError == null) {
//							log.info(
//									"[ProcessGBGenericEJB Error] >> Invoking BillErrorEJB billError exception. BillErrorHome = "
//											+ billErrorHome + " BillError = " + billError
//											+ " ProcessChargeException occurred for ~ref_nbr (" + refNbr + ") ~txn_dttm ("
//											+ txnDttm + ") ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd
//											+ ") ~tier_seq_nbr (" + tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = "
//											+ processChargeException.getMessage());
//						} else {
//							try {
//								billErrorValueObject = new BillErrorVO();
//								billErrorValueObject.setRunInd(billErrorValueObject.RUN_IND_CREATE_BILL);
//								billErrorValueObject.setTariffMainCat(tariffMainCatCd);
//								billErrorValueObject.setTariffSubCat(tariffSubCatCd);
//								// Cally Neo 10 Feb 2015 SL-LWMS-20141106-01: Add meaningful exceptional report
//								// remarks.
//								if (refNbr.contains("~")) {
//									// Split it.
//									refNbr = refNbr.split("~")[0];
//								}
					//
//								billErrorValueObject.setRemarks("ProcessChargeException occurred for ~ref_nbr (" + refNbr
//										+ ") ~txn_dttm (" + txnDttm + ") ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd
//										+ ") ~tier_seq_nbr ( " + tierSeqNbr + ") ~local_leg ( " + localLeg + ") ~Msg = "
//										+ processChargeException.getMessage());
					//
//								// insert into Bill Error table
//								billError.insertBillError(billErrorValueObject);
//							} catch (Exception billErrorException) {
//								log.info("[ProcessGBGenericEJB Error] >> Inserting billError exception = "
//										+ billErrorException.getMessage()
//										+ " ProcessChargeException occurred for ~ref_nbr (" + refNbr + ") ~txn_dttm ("
//										+ txnDttm + ") ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd
//										+ ") ~tier_seq_nbr (" + tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = "
//										+ processChargeException.getMessage());
//							}
//						}
					continue;
				} catch (Exception exception) {
//						if (billErrorHome == null || billError == null) {
//							log.info(
//									"[ProcessGBGenericEJB Error] >> Invoking BillErrorEJB billError exception. BillErrorHome = "
//											+ billErrorHome + " BillError = " + billError
//											+ " Exception occurred for ~ref_nbr (" + refNbr + ") ~txn_dttm (" + txnDttm
//											+ ") ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd + ") ~tier_seq_nbr ("
//											+ tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = "
//											+ exception.getMessage());
//						} 
//						else {
					try {
						billErrorValueObject = new BillErrorVO();
						billErrorValueObject.setRunInd(BillErrorVO.RUN_IND_CREATE_BILL);
						billErrorValueObject.setTariffMainCat(tariffMainCatCd);
						billErrorValueObject.setTariffSubCat(tariffSubCatCd);
						// Cally Neo 10 Feb 2015 SL-LWMS-20141106-01: Add meaningful exceptional report
						// remarks.
						if (refNbr.contains("~")) {
							// Split it.
							refNbr = refNbr.split("~")[0];
						}

						billErrorValueObject.setRemarks("Exception occurred for ~ref_nbr (" + refNbr + ") ~txn_dttm ("
								+ txnDttm + ") ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd + ") ~tier_seq_nbr ("
								+ tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = " + exception.getMessage());

						// insert into Bill Error table
//								billError.insertBillError(billErrorValueObject);
					} catch (Exception billErrorException) {
						log.info("[ProcessGBGenericEJB Error] >> Inserting billError exception = "
								+ billErrorException.getMessage() + " Exception occurred for ref_nbr (" + refNbr
								+ ") ~txn_dttm (" + txnDttm + ") ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd
								+ ") ~tier_seq_nbr (" + tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = "
								+ exception.getMessage());
					}
//						}
					continue;
				}
			} // end for loop
		} catch (Exception e) {
//				if (billErrorHome != null && billError != null) {
//					try {
//						billErrorValueObject = new BillErrorVO();
//						billErrorValueObject.setRunInd(billErrorValueObject.RUN_IND_CREATE_BILL);
//						billErrorValueObject.setTariffMainCat(tariffMainCatCd);
//						billErrorValueObject.setTariffSubCat(tariffSubCatCd);
//						// Cally Neo 10 Feb 2015 SL-LWMS-20141106-01: Add meaningful exceptional report
//						// remarks.
//						if (refNbr.contains("~")) {
//							// Split it.
//							refNbr = refNbr.split("~")[0];
//						}
			//
//						billErrorValueObject.setRemarks("Exception occurred when populating tariff for ~ref_nbr (" + refNbr
//								+ ") ~txn_dttm (" + txnDttm + ") ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd
//								+ ") ~tier_seq_nbr (" + tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = "
//								+ e.getMessage());
//						// insert into Bill Error table
//						billError.insertBillError(billErrorValueObject);
//						ex = new Exception("Exception occurred when populating tariff for ~ref_nbr (" + refNbr
//								+ ") ~txn_dttm (" + txnDttm + ") ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd
//								+ ") ~tier_seq_nbr (" + tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = "
//								+ e.getMessage());
//					} catch (Exception billErrorException) {
//						log.info("[ProcessGBGenericEJB Error] >> Inserting billError exception = "
//								+ billErrorException.getMessage()
//								+ "Exception occurred when populating tariff for ref_nbr (" + refNbr + ") ~txn_dttm ("
//								+ txnDttm + ") ~ref_ind (" + refInd + " ~tariff_cd (" + tariffCd + " ~tier_seq_nbr ("
//								+ tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = " + e.getMessage());
//						ex = new Exception("[ProcessGBGenericEJB Error] >> Inserting billError exception = "
//								+ billErrorException.getMessage()
//								+ "Exception occurred when populating tariff for ~ref_nbr (" + refNbr + ") ~txn_dttm ("
//								+ txnDttm + ") ~ref_ind (" + refInd + " ~tariff_cd (" + tariffCd + " ~tier_seq_nbr ("
//								+ tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = " + e.getMessage());
//					}
//				} else {
//					log.info("[ProcessGBGenericEJB Error] >> Invoking BillErrorEJB billError exception. BillErrorHome = "
//							+ billErrorHome + " BillError = " + billError
//							+ " Exception occurred when populating tariff for ~ref_nbr = " + refNbr + " ~txn_dttm ("
//							+ txnDttm + " ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd + ") ~tier_seq_nbr ("
//							+ tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = " + e.getMessage());
//					if (ex instanceof NamingException) {
//						ex = new NamingException(
//								"[ProcessGBGenericEJB Error] >> Invoking BillErrorEJB billError exception. BillErrorHome = "
//										+ billErrorHome + " BillError = " + billError
//										+ " Exception occurred when populating tariff for ~ref_nbr = " + refNbr
//										+ " ~txn_dttm (" + txnDttm + " ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd
//										+ ") ~tier_seq_nbr (" + tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = "
//										+ e.getMessage());
//					} else if (ex instanceof SQLException) {
//						ex = new SQLException(
//								"[ProcessGBGenericEJB Error] >> Invoking BillErrorEJB billError exception. BillErrorHome = "
//										+ billErrorHome + " BillError = " + billError
//										+ " Exception occurred when populating tariff for ~ref_nbr = " + refNbr
//										+ " ~txn_dttm (" + txnDttm + " ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd
//										+ ") ~tier_seq_nbr (" + tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = "
//										+ e.getMessage());
//					} else {
//						ex = new Exception(
//								"[ProcessGBGenericEJB Error] >> Invoking BillErrorEJB billError exception. BillErrorHome = "
//										+ billErrorHome + " BillError = " + billError
//										+ " Exception occurred when populating tariff for ~ref_nbr = " + refNbr
//										+ " ~txn_dttm (" + txnDttm + " ~ref_ind (" + refInd + ") ~tariff_cd (" + tariffCd
//										+ ") ~tier_seq_nbr (" + tierSeqNbr + ") ~local_leg (" + localLeg + ") ~Msg = "
//										+ e.getMessage());
//					}
//				}
			log.info("Exception retrieveBillable : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END retrieveBillable DAO");
		}
		
		return processGBList;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBGenericEJB-->retrieveGbCurrentTimestamp()
	@Override
	public Timestamp retrieveGbCurrentTimestamp(String vvCd, String tariffMainCatCd) throws BusinessException {
		VesselRelatedValueObject vesselRelatedValueObject = null;
		BerthRelatedValueObject berthRelatedValueObject = null;

		Timestamp varDttm = null;

		try {
			log.info("START retrieveGbCurrentTimestamp DAO :: vvCd: " + CommonUtility.deNull(vvCd) + " tariffMainCatCd: "
					+ CommonUtility.deNull(tariffMainCatCd));
			vesselRelatedValueObject = new VesselRelatedValueObject();
			vesselRelatedValueObject.setVvCd(vvCd);

			// retrieve the atb timestamp for further processing
			berthRelatedValueObject = processGenericRepository.retrieveBerthDttm(vesselRelatedValueObject, 1,
					tariffMainCatCd);
			varDttm = berthRelatedValueObject.getAtbDttm();
			log.info(" retrieveGbCurrentTimestamp, 1 CFG varDttm: (" + varDttm + ")");

			if (varDttm == null) {
				varDttm = UserTimestampVO.getCurrentTimestamp();
			}

	
		} catch (BusinessException e) {
			log.info("Exception retrieveGbCurrentTimestamp : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception retrieveGbCurrentTimestamp : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END retrieveGbCurrentTimestamp DAO  varDttm: " + varDttm.toString());
		}

		
		return varDttm;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBGenericEJB-->isChargeForSpecialCargo()
	private boolean isChargeForSpecialCargo(ChargeableBillValueObject chargeableBillVOPubl) throws Exception {
		boolean result = false;
		try {
			log.info("START isChargeForSpecialCargo DAO :: chargeableBillVOPubl: " + CommonUtility.deNull(chargeableBillVOPubl.toString()));
			// get DSA-No
			String dsaNbr = chargeableBillVOPubl.getRefNbr().split("~")[0];

			// get DSA from DSA-no
			CargoDeclarationVO cargoDeclarationVO = lighterTermRepo.getCargoDeclarationByDsaNbr(dsaNbr);

			if (cargoDeclarationVO != null && "Special Cargo".equals(cargoDeclarationVO.getCargo_type())) {
				result = true;
			}
		} catch (BusinessException e) {
			log.info("Exception isChargeForSpecialCargo : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception isChargeForSpecialCargo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END isChargeForSpecialCargo DAO  result: " + result);
		}

		return result;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBGenericEJB-->ChargeableBillValueObject()
	public ChargeableBillValueObject retrieveCustomizeChargeableForCrane(int versionNbr, String custCd, String acctNbr,
			String contractNbr, int contractualYr, ChargeableBillValueObject chargeableBillVOPubl)
			throws BusinessException {
		Exception ex = null;

		TariffMainVO tariffMainValueObject = null;
		TariffTierVO tariffTierValueObject = null;
		ChargeableBillValueObject chargeableBillVOCust = null;
		AccountValueObject accountValueObject = null;
		ContractValueObject contractValueObject = null;
		BillAdjustParam billAdjParam = null;

		String tariffMainCatCd = null;
		String tariffSubCatCd = null;
		String vvCd = null;
		Timestamp varDttm = null;
		int custVersionNbr = 0;
		String billCustCd = null;
		String billAcctNbr = null;
		String billContractNbr = null;
		int billContractualYr = 0;

		String tariffCd = null;
		int tierSeqNbr = 0;
		double unitRate = 0;
		double gstPercent = 0;
		double itemGst = 0;
		double itemAmt = 0;
		String adjType = null;
		double adjAmt = 0;

		try {
			log.info("START retrieveCustomizeChargeableForCrane DAO :: versionNbr: " + CommonUtility.deNull(String.valueOf(versionNbr)) + " custCd: "
					+ CommonUtility.deNull(custCd) + " acctNbr: " + CommonUtility.deNull(acctNbr) + " contractNbr: " + CommonUtility.deNull(contractNbr)
					+ " contractualYr: " + CommonUtility.deNull(String.valueOf(contractualYr)) + " chargeableBillVOPubl: "
					+ CommonUtility.deNull(chargeableBillVOPubl.toString()));
			// initialize customize chargeable bill value object to be equal to the publish
			// one
			chargeableBillVOCust = new ChargeableBillValueObject(chargeableBillVOPubl);
			chargeableBillVOCust.setTariffType(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE);

			// retrieve the first customize rates
			tariffMainCatCd = chargeableBillVOPubl.getTariffMainCatCd();
			tariffSubCatCd = chargeableBillVOPubl.getTariffSubCatCd();
			tariffCd = chargeableBillVOPubl.getTariffCd();
			log.info("~tariff_cd (" + tariffCd + "...");
			tierSeqNbr = chargeableBillVOPubl.getTierSeqNbr();
			log.info("~tier_seq_nbr ( " + tierSeqNbr + "...");
			// amended by hujun on 7/6/2011 for add tariff item effective date parameter
			tariffMainValueObject = processGenericRepository.retrieveCustomizeTariffDtls(versionNbr, custCd, acctNbr,
					contractNbr, contractualYr, tariffMainCatCd, tariffSubCatCd, tariffCd, tierSeqNbr, varDttm);
			// amended end

			// Cally Neo 15 Jun 2015 CR-LWMS-20150514-001: Billing Logic change in LWMS and
			// system enhancement for Cash Sales
			// check if the bill is for Lighter Terminal
			String prefixMainCatCd = tariffMainCatCd.substring(0, 1);
			boolean isLT = lwmsComUtilRepo.isBusTypeLighterTerminal(prefixMainCatCd);
			log.info("ProcessGBGenericEJB.retrieveCustomizeChargeableForCrane isLT: " + prefixMainCatCd + " *** "
					+ isLT);
			log.info("MainCatCode Prefix: " + prefixMainCatCd);

			// if first customize tariff value object is found,
			// get the bill account and contract number to see whether there is further
			// customize tariff rates to retrieve
			if (tariffMainValueObject.isModified(tariffMainValueObject)) {
				log.info("first customize tariff found");
				billAcctNbr = tariffMainValueObject.getBillAccount();
				billContractNbr = tariffMainValueObject.getBillContract();
				log.info("~second bill acct = " + billAcctNbr + "...");
				log.info("~second bill contract = " + billContractNbr + "...");

				if (billAcctNbr != null) {
					billAcctNbr = billAcctNbr.trim();
					acctNbr = billAcctNbr.trim();
				}

				if (billContractNbr != null) {
					billContractNbr = billContractNbr.trim();
					contractNbr = billContractNbr.trim();
				}

				// based on the retrieved bill account and contract number,
				// check to see whether customized tariff rates exist
				if ((billAcctNbr != null) && (billContractNbr != null)) {
					vvCd = chargeableBillVOPubl.getVvCd();
					log.info("vvCd" + vvCd.toString());
					// HKM 23 Jul 07 Begin change for GB Warehouse Billing //
					// varDttm = retrieveGbCurrentTimestamp(vvCd, tariffMainCatCd);

					if (ProcessChargeConst.TARIFF_MAIN_GB_STORAGE.equalsIgnoreCase(tariffMainCatCd)) {
						varDttm = chargeableBillVOPubl.getVarDttm();
					}
					// Thangtv: Added a way to get varDttm for JO bill processing
					else if (ProcessChargeConst.TARIFF_MAIN_JPONLINE.equals(tariffMainCatCd)) {
						varDttm = UserTimestampVO.getCurrentTimestamp();
					} // Cally Neo 15 Jun 2015 CR-LWMS-20150514-001: Billing Logic change in LWMS and
						// system enhancement for Cash Sales
					else if (isLT || ProcessChargeConst.TARIFF_MAIN_ADMIN_PEN.equals(tariffMainCatCd)
							|| ProcessChargeConst.TARIFF_MAIN_ADMIN_MARINA.equals(tariffMainCatCd)) {
						varDttm = chargeableBillVOPubl.getVarDttm();
					}
					// Thangtv: End Added a way to get varDttm for JO bill processing
					else
					
						// varDttm = retrieveGbCurrentTimestamp(vvCd, tariffMainCatCd);
						varDttm = UserTimestampVO.getCurrentTimestamp();

					// HKM 23 Jul 07 End change //
					// retrieve the customer account details for further processing

					accountValueObject = processGenericRepository.retrieveCustAcct(null, billAcctNbr);

					if (accountValueObject == null)
						log.info("~account value object = " + accountValueObject + "...");

					// retrieve the customer contract and determine its existence
					billCustCd = accountValueObject.getCustomerCode();
					custCd = billCustCd;
					log.info("~second bill cust = " + billCustCd + "...");
					chargeableBillVOCust.setCustCd(billCustCd);
					chargeableBillVOCust.setAcctNbr(billAcctNbr);

					contractValueObject = processGenericRepository.retrieveCustContract(billCustCd, billAcctNbr,
							billContractNbr, varDttm);

					// contract exist, search for customize tariff
					if (contractValueObject.isModified(contractValueObject)) {
						log.info("second contract exist");
						// retrieve customer tariff version number
						custVersionNbr = contractValueObject.getCustomerTariffVersion();
						log.info("~second cust version = " + custVersionNbr + "...");

						// retrieve the customized tariff, when customize
						// version number is valid
						if (custVersionNbr != 0) {
							billContractualYr = processGenericRepository.determineContractualYr(billAcctNbr, billContractNbr,
									varDttm);
							contractualYr = billContractualYr;
							log.info("~second contract yr = " + contractualYr + "...");
							// amended by hujun on 7/6/2011 for add tariff item
							// effective date parameter
							tariffMainValueObject = processGenericRepository.retrieveCustomizeTariffDtls(custVersionNbr,
									billCustCd, billAcctNbr, billContractNbr, billContractualYr, tariffMainCatCd,
									tariffSubCatCd, tariffCd, tierSeqNbr, varDttm);
							// amended end
							log.info("`second ~tariff_cd (" + tariffMainValueObject.getCode() + "...");

							// if second customize tariff value object is found,
							// use second retrieved customized rates
							if (tariffMainValueObject.isModified(tariffMainValueObject)) {
								// get the individual record of the tariff tier
								// record
								gstPercent = tariffMainValueObject.getGST();
								// new logic: get tarfiff tier first matchs conditions, not get first tariff
								// tier
								tariffTierValueObject = getCustomizedTariffTierForCrane(tariffMainValueObject,
										chargeableBillVOPubl);

								if (tariffTierValueObject != null) {
									adjType = tariffTierValueObject.getAdjustmentType();
									adjAmt = tariffTierValueObject.getAdjustment();
									unitRate = tariffTierValueObject.getRate();
									log.info("~second unit rate = $" + unitRate + "...");
									log.info("adjType" + adjType.toString());
									log.info("adjAmt" + adjAmt);
									
									billAdjParam = create(tariffCd);

									if (billAdjParam == null) {
										// ex = new
										// ProcessChargeException("Bill Adj Param is null for ~tariff_cd ("
										// + tariffCd);
										ex = new ProcessChargeException(
												"~tariff_cd (" + tariffCd + ") #Bill Adj Param is null#");
										throw ex;
									}

									billAdjParam.setTotalContainer(chargeableBillVOPubl.getNbrCntr());
									billAdjParam.setTotalTime(chargeableBillVOPubl.getNbrTimeUnit());
									billAdjParam.setTotalOtherUnit(chargeableBillVOPubl.getNbrOtherUnit());
									billAdjParam.setUnitRate(unitRate);
									billAdjParam.setGst(gstPercent);

									itemGst = billAdjParam.getGstAmount();
									itemAmt = billAdjParam.getTotalAmount();

									chargeableBillVOCust.setVersionNbr(custVersionNbr);
									chargeableBillVOCust.setTariffCd(tariffCd);
									chargeableBillVOCust.setTierSeqNbr(tierSeqNbr);
									chargeableBillVOCust.setCustCd(billCustCd);
									chargeableBillVOCust.setAcctNbr(billAcctNbr);
									chargeableBillVOCust.setContractNbr(billContractNbr);
									chargeableBillVOCust.setContractualYr(billContractualYr);
									chargeableBillVOCust.setTariffType(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE);
									chargeableBillVOCust.setUnitRate(unitRate);

									// ThangTV: check negative number of total
									// amount add at 10-Sep-2007
									if (chargeableBillVOPubl.getTotalChargeAmt() < 0) {
										chargeableBillVOCust.setGstAmt(-itemGst);
										chargeableBillVOCust.setTotalChargeAmt(-itemAmt);
									} else {
										// ThangTV: End check negative number of
										// total amount add at 10-Sep-2007
										chargeableBillVOCust.setGstAmt(itemGst);
										chargeableBillVOCust.setTotalChargeAmt(itemAmt);
									}
								}

							}
						} // end-if for customize version number = valid
					} // end-if for second contract exists
				} // end-if for bill account nbr and bill contract nbr is not null
					// no further customize bill party, use the first retrieved customize rates
				else {
					// get the individual record of the tariff tier record
					gstPercent = chargeableBillVOPubl.getGstCharge();
					// new logic: get tarfiff tier first matchs conditions, not get first tariff
					// tier
					tariffTierValueObject = getCustomizedTariffTierForCrane(tariffMainValueObject,
							chargeableBillVOPubl);

					if (tariffTierValueObject != null) {
						adjType = tariffTierValueObject.getAdjustmentType();
						adjAmt = tariffTierValueObject.getAdjustment();
						unitRate = tariffTierValueObject.getRate();

						log.info("~first unit rate = $" + unitRate + "...");

						billAdjParam = create(tariffCd);

						if (billAdjParam == null) {
							// ex = new
							// ProcessChargeException("Bill Adj Param is null for ~tariff_cd ("
							// + tariffCd);
							ex = new ProcessChargeException("~tariff_cd (" + tariffCd + ") #Bill Adj Param is null#");
							throw ex;
						}

						billAdjParam.setTotalContainer(chargeableBillVOPubl.getNbrCntr());
						billAdjParam.setTotalTime(chargeableBillVOPubl.getNbrTimeUnit());
						billAdjParam.setTotalOtherUnit(chargeableBillVOPubl.getNbrOtherUnit());
						billAdjParam.setUnitRate(unitRate);
						billAdjParam.setGst(gstPercent);

						itemGst = billAdjParam.getGstAmount();
						itemAmt = billAdjParam.getTotalAmount();

						chargeableBillVOCust.setVersionNbr(versionNbr);
						chargeableBillVOCust.setTariffCd(tariffCd);
						chargeableBillVOCust.setTierSeqNbr(tierSeqNbr);
						chargeableBillVOCust.setCustCd(custCd);
						chargeableBillVOCust.setAcctNbr(acctNbr);
						chargeableBillVOCust.setContractNbr(contractNbr);
						chargeableBillVOCust.setContractualYr(contractualYr);
						chargeableBillVOCust.setTariffType(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE);
						chargeableBillVOCust.setUnitRate(unitRate);

						// ThangTV: check negative number of total amount add at
						// 10-Sep-2007
						if (chargeableBillVOPubl.getTotalChargeAmt() < 0) {
							chargeableBillVOCust.setGstAmt(-itemGst);
							chargeableBillVOCust.setTotalChargeAmt(-itemAmt);
						} else {
							// ThangTV: End check negative number of total amount
							// add at 10-Sep-2007
							chargeableBillVOCust.setGstAmt(itemGst);
							chargeableBillVOCust.setTotalChargeAmt(itemAmt);
						}
					}
				}
			}
	
		} catch (BusinessException e) {
			log.info("Exception retrieveCustomizeChargeableForCrane : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception retrieveCustomizeChargeableForCrane : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END retrieveCustomizeChargeableForCrane DAO");
		}
		
		return chargeableBillVOCust;
	}

	// jp.src.valueObject.cab.billing--->BillAdjustParamFactory-->create()
	public BillAdjustParam create(String tariffCode) throws BusinessException, Exception {
		int ind[] = null;
		try {
			log.info("START create Process GB Generic DAO :: tariffCode: " + CommonUtility.deNull(tariffCode));
			ind = billSupInfoRepo.getIndicator(tariffCode);
		} catch (BusinessException be) {
			log.info("Exception create : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception create : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END create Process GB Generic DAO");
		}
		return getParam(ind);
	}

	// jp.src.valueObject.cab.billing--->BillAdjustParamFactory-->getParam()
	private static BillAdjustParam getParam(int ind[]) throws BusinessException {
		BillAdjustParam retVal = null;
		try {
			log.info("START BillAdjustParam DAO :: ind: " + CommonUtility.deNull(String.valueOf(ind)));
			if (ind == null)
				return null;
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
				retVal = new BillAdjParamCTOVO();
			} else {
				retVal = null;
			}
		} catch (Exception e) {
			log.info("Exception getParam : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END BillAdjustParam DAO  retVal: " + retVal);
		}
		return retVal;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBGenericEJB-->getCustomizedTariffTierForCrane()
	private TariffTierVO getCustomizedTariffTierForCrane(TariffMainVO tariffMainValueObject,
			ChargeableBillValueObject chargeableBillVOPubl) throws BusinessException, SQLException, Exception {

		TariffTierVO tariffTierVO = null;
		double tonFrom = 0;
		double tonTo = 0;

		String dsaNo = chargeableBillVOPubl.getRefNbr().split("~")[0];
		String craneLineNo = chargeableBillVOPubl.getRefNbr().split("~")[1];
		List<CargoDeclarationVO> processList = new ArrayList<>();
//		List<CargoDeclarationVO> processList = processBillRepo.retrieveDSAChargableBillVOs(dsaNo);
		CargoDeclarationVO cargoDeclarationVO = null;
		CranageVO cranageVO = null;

		try {
			log.info("START getCustomizedTariffTierForCrane DAO :: tariffMainValueObject: " + CommonUtility.deNull(tariffMainValueObject.toString())
					+ " chargeableBillVOPubl: " + CommonUtility.deNull(chargeableBillVOPubl.toString()));
			processList = processBillRepo.retrieveDSAChargableBillVOs(dsaNo);
			for (Object o : processList) {
				cargoDeclarationVO = (CargoDeclarationVO) o;

				// check equality of DSA_NO
				if (cargoDeclarationVO.getDsa_nbr().equals(dsaNo)) {
					List<CranageVO> dsaCranages = cargoDeclarationVO.getDsaCranages();
					if (dsaCranages != null && dsaCranages.size() > 0) {
						for (Object curCranageVO : dsaCranages) {
							cranageVO = (CranageVO) curCranageVO;

							// check equality of CRANAGE_LINE_NBR
							if (cranageVO.getCranage_line_nbr() == Long.valueOf(craneLineNo)) {
								tonFrom = cranageVO.getFrom_ton();
								tonTo = cranageVO.getTo_ton();
								log.info("tonFrom" + tonFrom);
								log.info("tonTo" + tonTo);
								break;
							}
						}
					}
				}
			}
		} catch (BusinessException e) {
			log.info("Exception getCustomizedTariffTierForCrane : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getCustomizedTariffTierForCrane : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCustomizedTariffTierForCrane DAO  tariffTierVO: " + tariffTierVO);
		}
		return tariffTierVO;
	}

	@Override
	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBGeneric--->getGstCharge()
	public List<GstCodeValueObject> getGstCharge(String gstCode, Timestamp date) throws SQLException, Exception {
		return getGstCharge(gstCode, date, false);
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBGenericEJB--->getGstCharge()
	public List<GstCodeValueObject> getGstCharge(String gstCode, Timestamp date, boolean listAll) throws SQLException, Exception {
		
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer buffer = new StringBuffer().append(
				"select gst_charge, fmas_gst_cd, eff_start_dttm, eff_end_dttm, to_char(eff_start_dttm, 'dd/mm/yyyy'), to_char(eff_end_dttm, 'dd/mm/yyyy'), misc_type_nm from gst_para, misc_type_code where gst_para.rec_status='A' and misc_type_code.rec_status='A' and cat_cd='FMAS_GCODE' and fmas_gst_cd=misc_type_cd ");
		if (gstCode != null) {
			buffer.append(" and tariff_gst_cd='").append(gstCode.trim()).append("' ");
		}
		if (date != null) {
			if (listAll) {
				buffer.append(" and ( eff_start_dttm <= :date or eff_end_dttm <= :date ) ");
			} else {
				buffer.append(" and eff_start_dttm<= :date and (eff_end_dttm>= :date or eff_end_dttm is null) ");
			}
		}
		buffer.append(" order by tariff_gst_cd asc, eff_start_dttm desc");
		String sql = buffer.toString();
		// log.info( "ProcessGBGeneric sql: " + sql);
		List<GstCodeValueObject> arrayList = new ArrayList<GstCodeValueObject>(1);
		int ctr = 0;
		
		try {
			log.info("START getGstCharge DAO :: gstCode: " + CommonUtility.deNull(gstCode) + " date: " + CommonUtility.deNull(date.toString())
					+ " listAll: " + CommonUtility.deNull(String.valueOf(listAll)));

			if (date != null) {
				paramMap.put("date", date);
			}
			log.info("getGstCharge SQL: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				GstCodeValueObject gstCodeValueObject = new GstCodeValueObject();
				gstCodeValueObject.setGstCharge(rs.getDouble(1));
				gstCodeValueObject.setCode(CommonUtility.deNull(rs.getString(2)));
				gstCodeValueObject.setStartDate(rs.getTimestamp(3));
				gstCodeValueObject.setEndDate(rs.getTimestamp(4));
				gstCodeValueObject.setDescription(CommonUtility.deNull(rs.getString(7)) + " "
						+ CommonUtility.deNull(rs.getString(5)) + " - " + CommonUtility.deNull(rs.getString(6)));
				arrayList.add(ctr++, gstCodeValueObject);
			}
		
		} catch (Exception e) {
			log.info("Exception getGstCharge : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getGstCharge DAO  arrayList: " + arrayList.size());
			
		}
		return arrayList;
	}

	// EndRegion ProcessGBGenericJdbcRepository

}
