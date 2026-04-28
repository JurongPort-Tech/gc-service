package sg.com.jp.generalcargo.dao.impl;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.BillSupportInfoRepository;
import sg.com.jp.generalcargo.dao.LWMSCommonUtilRepository;
import sg.com.jp.generalcargo.dao.ProcessGBGenericRepository;
import sg.com.jp.generalcargo.dao.ProcessGenericRepository;
import sg.com.jp.generalcargo.dao.TariffMainRepository;
import sg.com.jp.generalcargo.dao.TariffVersionRepository;
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
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.CntrEventLogValueObject;
import sg.com.jp.generalcargo.domain.ContractSearchKeyValueObject;
import sg.com.jp.generalcargo.domain.ContractValueObject;
import sg.com.jp.generalcargo.domain.GstCodeValueObject;
import sg.com.jp.generalcargo.domain.TariffMainVO;
import sg.com.jp.generalcargo.domain.TariffTierBillPartyVO;
import sg.com.jp.generalcargo.domain.TariffTierVO;
import sg.com.jp.generalcargo.domain.TempProcessChargeValueObject;
import sg.com.jp.generalcargo.domain.UserTimestampVO;
import sg.com.jp.generalcargo.domain.VesselRelatedValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("ProcessGenericJdbcRepository")
public class ProcessGenericJdbcRepository implements ProcessGenericRepository {

	private static final Log log = LogFactory.getLog(ProcessGenericJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;	
	@Autowired
	private TariffMainRepository tariffMainRepo;
	@Autowired
	private LWMSCommonUtilRepository lwmsComUtilRepo;
	@Autowired
	private BillSupportInfoRepository billSupInfoRepo;
	@Autowired
	private TariffVersionRepository tariffVerRepo;
	@Autowired
	@Lazy
	private ProcessGBGenericRepository processGBGenericRepo;
	// StartRegion ProcessGBGenericJdbcRepository

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->retrieveCustAcct()
	public AccountValueObject retrieveCustAcct(String custCd, String acctNbr) throws BusinessException{
		try {
			log.info("START: retrieveCustAcct "+" custCd:"+CommonUtility.deNull(custCd) +" acctNbr:"+CommonUtility.deNull(acctNbr) );
			return retrieveCustAcctByStatus(custCd, acctNbr, false);
		
		} catch (Exception e) {
			log.info("Exception retrieveCustAcct : ", e);
			throw new BusinessException("M4201");
		}
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->retrieveCustAcctByStatus()
	public AccountValueObject retrieveCustAcctByStatus(String custCd, String acctNbr, boolean isActiveAcct)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sqlQuery = null;
//		Exception ex = null;
		AccountValueObject accountValueObject = null;
		
		try {
			log.info("START retrieveCustAcctByStatus Dao :: custCd: " + CommonUtility.deNull(custCd) + " acctNbr: " + CommonUtility.deNull(acctNbr)
					+ " isActiveAcct: " + CommonUtility.deNull(String.valueOf(isActiveAcct)));
			sqlQuery = new StringBuffer();

			// formulate the sql query and execute the query
			sqlQuery.append("SELECT cust_cd, acct_nbr, acct_status_cd, business_type, currency ");
			sqlQuery.append("FROM cust_acct ");
			sqlQuery.append("WHERE ");
			if (custCd != null)
				sqlQuery.append("cust_cd=:custCd ");
			if ((custCd != null) && (acctNbr != null))
				sqlQuery.append("and ");
			if (acctNbr != null)
				sqlQuery.append("acct_nbr=:acctNbr ");

			// formulate the sql query and execute the query
			
			if (custCd != null)
				paramMap.put("custCd", custCd);
			if (acctNbr != null)
				paramMap.put("acctNbr", acctNbr);
			log.info("retrieveCustAcctByStatus SQL: " + sqlQuery.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);
			// initialize AccountValueObject
			accountValueObject = new AccountValueObject();

			if (rs.next()) {
				// set the retrieved database values into the AccountValueObject
				accountValueObject.setCustomerCode(rs.getString("cust_cd"));
				accountValueObject.setAccountNumber(rs.getString("acct_nbr"));
				accountValueObject.setStatusCode(rs.getString("acct_status_cd"));
				accountValueObject.setBusinessType(rs.getString("business_type"));
				accountValueObject.setCurrency(rs.getString("currency"));
			}

			if (!accountValueObject.isModified(accountValueObject)) {
				String[] tempString2 = {acctNbr};
				throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Cust_Not_Found, tempString2));
			}

			// Amended by Jade for CR-CAB-20130516-004 only check if account is
			// active when the parameter is true
			// if (accountValueObject.getStatusCode().equals("I")) {
			if (isActiveAcct && accountValueObject.getStatusCode().equals("I")) {
				log.info("Processing inactive billing account for company code: " + custCd + "; acct number: "
						+ acctNbr);
				// End of amendment by Jade for CR-CAB-20130516-004
				String[] tempString2 = {acctNbr};
				throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Cust_Inactive, tempString2));
			}

		} catch (BusinessException e) {
			log.info("Exception retrieveCustAcctByStatus : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception retrieveCustAcctByStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveCustAcctByStatus DAO  accountValueObject: " + accountValueObject.toString());
		}
		
		return accountValueObject;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->retrieveCustContract()
	public ContractValueObject retrieveCustContract(String custCd, String acctNbr, String contractNbr,
			Timestamp varDttm) throws BusinessException {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sqlQuery = null;
		ContractValueObject contractValueObject = null;

		try {
			log.info("START retrieveCustContract Dao :: custCd: " + CommonUtility.deNull(custCd) + " acctNbr: " + CommonUtility.deNull(acctNbr)
			+ " contractNbr: " + CommonUtility.deNull(contractNbr) +" varDttm:"+CommonUtility.deNull(varDttm.toString()) );
			// formulate the sql query and execute the query
			sqlQuery = new StringBuffer();

			// formulate the sql query and execute the query
			sqlQuery.append("SELECT cust_cd, acct_nbr, contract_nbr, business_type, cust_tariff_version_nbr ");
			sqlQuery.append("FROM cust_contract ");
			sqlQuery.append("WHERE ");
			// if cust_cd is not null, string cust_cd into query
			if (custCd != null)
				sqlQuery.append("cust_cd=:custCd ");
			// if cust_cd is not null and acct_nbr is not null, string "and"
			// into query
			if ((custCd != null) && (acctNbr != null))
				sqlQuery.append("and ");
			// if acct_nbr is not null, string acct_nbr into query
			if (acctNbr != null)
				sqlQuery.append("acct_nbr=:acctNbr ");
			// if either cust_cd or acct_nbr is not null and contract nbr is not
			// null, string "and" into query
			if (((custCd != null) || (acctNbr != null)) && (contractNbr != null))
				sqlQuery.append("and ");
			// if contract_nbr is not null, string contract_nbr into query
			if (contractNbr != null)
				sqlQuery.append("contract_nbr=:contractNbr ");
			// if either cust_cd, acct_nbr or contract_nbr is not null, string
			// "and" into query
			if ((custCd != null) || (acctNbr != null) || (contractNbr != null))
				sqlQuery.append("and ");
			sqlQuery.append("commence_dttm<=:varDttm and expiry_dttm>=:varDttm ");

			// formulate the sql query and execute the query
			if (custCd != null)
				paramMap.put("custCd", custCd);
			if (acctNbr != null)
				paramMap.put("acctNbr", acctNbr);
			if (contractNbr != null)
				paramMap.put("contractNbr", contractNbr);
			paramMap.put("varDttm", varDttm);
			log.info("retrieveCustContract SQL: " + sqlQuery.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

			// initialize ContractValueObject
			contractValueObject = new ContractValueObject();

			if (rs.next()) {
				// set the retrieved database values into the
				// ContractValueObject
				contractValueObject.setCustomerCode(rs.getString("cust_cd"));
				contractValueObject.setAccountNumber(rs.getString("acct_nbr"));
				contractValueObject.setContractNumber(rs.getString("contract_nbr"));
				contractValueObject.setBusinessType(rs.getString("business_type"));
				contractValueObject.setCustomerTariffVersion(rs.getInt("cust_tariff_version_nbr"));
			}
			
		} catch (Exception e) {
			log.info("Exception retrieveCustContract : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveCustContract DAO  contractValueObject: " + contractValueObject.toString());
		}
		return contractValueObject;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->determineContractualYr()
	public int determineContractualYr(String acctNbr, String contractNbr, Timestamp varDttm) throws BusinessException {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sqlQuery = null;
		int contractualYr = 0;

		try {
			log.info("START determineContractualYr Dao :: acctNbr: " + CommonUtility.deNull(acctNbr) + " contractNbr: " + CommonUtility.deNull(contractNbr)
					+ " varDttm: " + CommonUtility.deNull(varDttm.toString()));
			sqlQuery = new StringBuffer();
			// formulate the sql query and execute the query
			sqlQuery.append("SELECT contractual_yr ");
			sqlQuery.append("FROM cust_contract_period ");
			sqlQuery.append("WHERE acct_nbr=:acctNbr and contract_nbr=:contractNbr ");
			sqlQuery.append("and fr_dttm<=:varDttm and to_dttm>=:varDttm ");

			// formulate the sql query and execute the query
			paramMap.put("acctNbr", acctNbr);
			paramMap.put("contractNbr", contractNbr);
			paramMap.put("varDttm", varDttm);
			log.info("determineContractualYr SQL: " + sqlQuery.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);
			if (rs.next()) {
				// get the database values
				contractualYr = rs.getInt("contractual_yr");
			}
		} catch (Exception e) {
			log.info("Exception determineContractualYr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineContractualYr DAO  contractualYr: " + contractualYr);
		}
		return contractualYr;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBGenericEJB-->ChargeableBillValueObject()
	public ChargeableBillValueObject retrieveCustomizeChargeable(int versionNbr, String custCd, String acctNbr,
			String contractNbr, int contractualYr, ChargeableBillValueObject chargeableBillVOPubl)
			throws BusinessException {

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
			log.info("START retrieveCustomizeChargeable Dao :: versionNbr: " + CommonUtility.deNull(String.valueOf(versionNbr)) + " custCd: "
					+ CommonUtility.deNull(custCd) + " acctNbr:" + CommonUtility.deNull(acctNbr) + " contractNbr: " + CommonUtility.deNull(contractNbr)
					+ " contractualYr: " + CommonUtility.deNull(String.valueOf(contractualYr)) + " chargeableBillVOPubl: " + CommonUtility.deNull(chargeableBillVOPubl.toString()));
			// get the instance of ProcessCommon
//				processCommon = new ProcessCommon();

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
			tariffMainValueObject = retrieveCustomizeTariffDtls(versionNbr, custCd, acctNbr, contractNbr, contractualYr,
					tariffMainCatCd, tariffSubCatCd, tariffCd, tierSeqNbr, varDttm);
			// amended end
			// check if the bill is for Lighter Terminal
			String prefixMainCatCd = tariffMainCatCd.substring(0, 1);
			boolean isLT = lwmsComUtilRepo.isBusTypeLighterTerminal(prefixMainCatCd);
			log.info("ProcessGBGenericEJB.retrieveCustomizeChargeable isLT: " + prefixMainCatCd + " *** " + isLT);
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

					// HKM 23 Jul 07 Begin change for GB Warehouse Billing //
					// varDttm = retrieveGbCurrentTimestamp(vvCd, tariffMainCatCd);
					log.info("isLT=" + isLT);
					if (ProcessChargeConst.TARIFF_MAIN_GB_STORAGE.equalsIgnoreCase(tariffMainCatCd)) {
						varDttm = chargeableBillVOPubl.getVarDttm();
					}
					// Thangtv: Added a way to get varDttm for JO bill processing
					else if (ProcessChargeConst.TARIFF_MAIN_JPONLINE.equals(tariffMainCatCd)) {
						varDttm = UserTimestampVO.getCurrentTimestamp();
					}
					// Thangtv: End Added a way to get varDttm for JO bill processing
					// Begin Update by ThangPV, 31-07-2013: follow new logic to get varDttm,
					// approved by Cally
					// Cally Neo 10 Feb 2015 SL-LWMS-20141106-01: Add meaningful exceptional report
					// remarks.
					else if (isLT || ProcessChargeConst.TARIFF_MAIN_ADMIN_PEN.equals(tariffMainCatCd)
							|| ProcessChargeConst.TARIFF_MAIN_ADMIN_MARINA.equals(tariffMainCatCd)) {
						varDttm = chargeableBillVOPubl.getVarDttm();
					}
					// End Update by ThangPV, 31-07-2013: follow new logic to get varDttm, approved
					// by Cally
					else
						varDttm = processGBGenericRepo.retrieveGbCurrentTimestamp(vvCd, tariffMainCatCd);

					// HKM 23 Jul 07 End change //
					// retrieve the customer account details for further processing

					accountValueObject = retrieveCustAcct(null, billAcctNbr);

					if (accountValueObject == null)
						log.info("~account value object = " + accountValueObject + "...");

					// retrieve the customer contract and determine its existence
					billCustCd = accountValueObject.getCustomerCode();
					custCd = billCustCd;
					log.info("~second bill cust = " + billCustCd + "...");
					chargeableBillVOCust.setCustCd(billCustCd);
					chargeableBillVOCust.setAcctNbr(billAcctNbr);

					contractValueObject = retrieveCustContract(billCustCd, billAcctNbr, billContractNbr, varDttm);

					// contract exist, search for customize tariff
					if (contractValueObject.isModified(contractValueObject)) {
						log.info("second contract exist");
						// retrieve customer tariff version number
						custVersionNbr = contractValueObject.getCustomerTariffVersion();
						log.info("~second cust version = " + custVersionNbr + "...");

						// retrieve the customized tariff, when customize version number is valid
						if (custVersionNbr != 0) {
							billContractualYr = determineContractualYr(billAcctNbr, billContractNbr, varDttm);
							contractualYr = billContractualYr;
							log.info("~second contract yr = " + contractualYr + "...");
							// amended by hujun on 7/6/2011 for add tariff item effective date parameter
							tariffMainValueObject = retrieveCustomizeTariffDtls(custVersionNbr, billCustCd, billAcctNbr,
									billContractNbr, billContractualYr, tariffMainCatCd, tariffSubCatCd, tariffCd,
									tierSeqNbr, varDttm);
							// amended end
							log.info("`second ~tariff_cd (" + tariffMainValueObject.getCode() + "...");

							// if second customize tariff value object is found,
							// use second retrieved customized rates
							if (tariffMainValueObject.isModified(tariffMainValueObject)) {
								// get the individual record of the tariff tier record
								gstPercent = tariffMainValueObject.getGST();
								tariffTierValueObject = tariffMainValueObject.getTier(0);
								adjType = tariffTierValueObject.getAdjustmentType();
								adjAmt = tariffTierValueObject.getAdjustment();
								unitRate = tariffTierValueObject.getRate();
								if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_PERCENT)) {
									unitRate = unitRate + (adjAmt / 100 * unitRate);
								} else if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_AMOUNT)) {
									unitRate = unitRate + adjAmt;
								}
								log.info("~second unit rate = $" + unitRate + "...");

								billAdjParam = create(tariffCd);

								if (billAdjParam == null) {
									// ex = new ProcessChargeException("Bill Adj Param is null for ~tariff_cd (" +
									// tariffCd);
									String[] tempString2 = {tariffCd};
									throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Bill_Adj_Null, tempString2));
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

								// ThangTV: check negative number of total amount add at 10-Sep-2007
								if (chargeableBillVOPubl.getTotalChargeAmt() < 0) {
									chargeableBillVOCust.setGstAmt(-itemGst);
									chargeableBillVOCust.setTotalChargeAmt(-itemAmt);
								} else {
									// ThangTV: End check negative number of total amount add at 10-Sep-2007
									chargeableBillVOCust.setGstAmt(itemGst);
									chargeableBillVOCust.setTotalChargeAmt(itemAmt);
								}
							}
						} // end-if for customize version number = valid
					} // end-if for second contract exists
				} // end-if for bill account nbr and bill contract nbr is not null
					// no further customize bill party, use the first retrieved customize rates
				else {
					// get the individual record of the tariff tier record
					gstPercent = chargeableBillVOPubl.getGstCharge();
					// gstPercent = tariffMainValueObject.getGST(); //sauwoon SL-CAB-20090521-01
					tariffTierValueObject = tariffMainValueObject.getTier(0);
					adjType = tariffTierValueObject.getAdjustmentType();
					adjAmt = tariffTierValueObject.getAdjustment();
					unitRate = tariffTierValueObject.getRate();
					if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_PERCENT)) {
						unitRate = unitRate + (adjAmt / 100 * unitRate);
					} else if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_AMOUNT)) {
						unitRate = unitRate + adjAmt;
					}

					log.info("~first unit rate = $" + unitRate + "...");

					billAdjParam = create(tariffCd);

					if (billAdjParam == null) {
						// ex = new ProcessChargeException("Bill Adj Param is null for ~tariff_cd (" +
						// tariffCd);
						String[] tempString2 = {tariffCd};
						throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Bill_Adj_Null, tempString2));
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

					// ThangTV: check negative number of total amount add at 10-Sep-2007
					if (chargeableBillVOPubl.getTotalChargeAmt() < 0) {
						chargeableBillVOCust.setGstAmt(-itemGst);
						chargeableBillVOCust.setTotalChargeAmt(-itemAmt);
					} else {
						// ThangTV: End check negative number of total amount add at 10-Sep-2007
						chargeableBillVOCust.setGstAmt(itemGst);
						chargeableBillVOCust.setTotalChargeAmt(itemAmt);
					}
				}
			}
			log.info("END: *** retrieveCustomizeChargeable Result *****" + chargeableBillVOCust.toString());

		} catch (BusinessException e) {
			log.info("Exception retrieveCustomizeChargeable : ", e);
			throw new BusinessException(e.getMessage());
		
		} catch (Exception e) {
			log.info("Exception retrieveCustomizeChargeable : ", e);
			throw new BusinessException("M4201");
		}finally {
			log.info("END retrieveCustomizeChargeable DAO");
		}

		
		return chargeableBillVOCust;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGeneric--->retrieveBerthDttm()
	public BerthRelatedValueObject retrieveBerthDttm(VesselRelatedValueObject vesselRelatedValueObject, int shiftInd,
			String tariffMainCat) throws BusinessException {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sqlQuery = null;

		BerthRelatedValueObject berthRelatedValueObject = null;

		String vvCd = null;
		String vslNm = null;
		String inVoyNbr = null;
		String outVoyNbr = null;
		String shpgSvcCd = null;
		String vslOprCd = null;

		Timestamp atbDttm = null;
		Timestamp atuDttm = null;
		Timestamp codDttm = null;
		Timestamp colDttm = null;
		// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
		Timestamp etbDttm = null;
		Timestamp etuDttm = null;
		Timestamp gbFirstActDttm = null;
		Timestamp gbLastActDttm = null;
		Timestamp gbCodDttm = null;
		Timestamp gbColDttm = null;
		Timestamp gbBCodDttm = null;
		Timestamp gbBColDttm = null;
		// end add by swho

		// EVM Enhancements
		String berthNbr = null;
		String gcOperations = null;

		Timestamp btrDttm = null; // Added by Jade for SL-CAB-20150217-01

		try {
			log.info("START retrieveBerthDttm Dao :: vesselRelatedValueObject: " + CommonUtility.deNull(vesselRelatedValueObject.toString())
					+ " shiftInd: " + CommonUtility.deNull(String.valueOf(shiftInd)) + " tariffMainCat: " + CommonUtility.deNull(tariffMainCat));
			berthRelatedValueObject = new BerthRelatedValueObject();
			vvCd = vesselRelatedValueObject.getVvCd();

			// formulate the sql query and execute the query
			// sqlQuery = "select atb_dttm, atu_dttm, cod_dttm, col_dttm, etb_dttm,
			// etu_dttm, gb_first_act_dttm, gb_last_act_dttm, gb_cod_dttm, gb_col_dttm,
			// gb_bcod_dttm, gb_bcol_dttm from berthing where vv_cd=? and shift_ind=?";
			sqlQuery = "select a.atb_dttm, a.atu_dttm, a.cod_dttm, a.col_dttm, a.etb_dttm, a.etu_dttm, a.gb_first_act_dttm, a.gb_last_act_dttm, a.gb_cod_dttm, a.gb_col_dttm, a.gb_bcod_dttm, a.gb_bcol_dttm, b.vsl_berth_dttm, a.berth_nbr, b.combi_gc_ops_ind from berthing a, vessel_call b where a.vv_cd = b.vv_cd and a.vv_cd=:vvCd and a.shift_ind=:shiftInd";
			
			paramMap.put("vvCd", vvCd);
			paramMap.put("shiftInd", shiftInd);
			log.info("retrieveBerthDttm SQL: " + sqlQuery + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);


			if (rs.next()) {
				// get the database values
				atbDttm = rs.getTimestamp("atb_dttm");
				atuDttm = rs.getTimestamp("atu_dttm");
				codDttm = rs.getTimestamp("cod_dttm");
				colDttm = rs.getTimestamp("col_dttm");
				// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
				etbDttm = rs.getTimestamp("etb_dttm");
				etuDttm = rs.getTimestamp("etu_dttm");
				gbFirstActDttm = rs.getTimestamp("gb_first_act_dttm");
				gbLastActDttm = rs.getTimestamp("gb_last_act_dttm");
				gbCodDttm = rs.getTimestamp("gb_cod_dttm");
				gbColDttm = rs.getTimestamp("gb_col_dttm");
				gbBCodDttm = rs.getTimestamp("gb_bcod_dttm");
				gbBColDttm = rs.getTimestamp("gb_bcol_dttm");
				// end add by swho
				btrDttm = rs.getTimestamp("vsl_berth_dttm"); // Added by Jade for SL-CAB-20150217-01

				berthNbr = rs.getString("berth_nbr"); // New EVM Enhancements
				gcOperations = CommonUtility.deNull(rs.getString("combi_gc_ops_ind"));
				berthRelatedValueObject.setAtbDttm(atbDttm);
				berthRelatedValueObject.setAtuDttm(atuDttm);
				berthRelatedValueObject.setCodDttm(codDttm);
				berthRelatedValueObject.setColDttm(colDttm);
				// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
				berthRelatedValueObject.setEtbDttm(etbDttm);
				berthRelatedValueObject.setEtuDttm(etuDttm);
				berthRelatedValueObject.setGbFirstActDttm(gbFirstActDttm);
				berthRelatedValueObject.setGbLastActDttm(gbLastActDttm);
				berthRelatedValueObject.setGbCodDttm(gbCodDttm);
				berthRelatedValueObject.setGbColDttm(gbColDttm);
				berthRelatedValueObject.setGbBCodDttm(gbBCodDttm);
				berthRelatedValueObject.setGbBColDttm(gbBColDttm);
				berthRelatedValueObject.setGcOperations(gcOperations);
				berthRelatedValueObject.setBerthNbr(berthNbr); // New EVM Enhancements
				// end add by swho
				berthRelatedValueObject.setBtrDttm(btrDttm); // Added by Jade for SL-CAB-20150217-01

			}

			log.info("before isCancelledVessel() in retrieveBerthDttm() ");
			boolean cancelled = this.isCancelledVessel(vvCd);
			log.info("after isCancelledVessel() in retrieveBerthDttm() ");
			log.info("tariffMainCat =" + tariffMainCat);
			log.info("tariffMainCat.substring(0,1) =" + tariffMainCat.substring(0, 1));
			log.info("ProcessChargeConst.PENJURU_BUSINESS =" + ProcessChargeConst.PENJURU_BUSINESS
					+ "ProcessChargeConst.MARINA_SOUTH_BUSINESS=" + ProcessChargeConst.MARINA_SOUTH_BUSINESS);

			if (cancelled) {
				berthRelatedValueObject = this.getEtbBtr(vvCd, shiftInd);
			} else { /* Ended by UmaDevi.Y */
				if (!berthRelatedValueObject.isModified(berthRelatedValueObject)) {

					String prefixMainCatCd = tariffMainCat.substring(0, 1);
					boolean isLT = lwmsComUtilRepo.isBusTypeLighterTerminal(prefixMainCatCd);
					log.info("ProcessGenericEJB.retrieveBerthDttm isLT: " + prefixMainCatCd + " *** " + isLT);
					log.info("MainCatCode Prefix: " + prefixMainCatCd);
					log.info("isLT=" + isLT);
					if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STEVEDORAGE)
							|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE)
							|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_VESSEL)) {
						vslNm = vesselRelatedValueObject.getVslNm();
						inVoyNbr = vesselRelatedValueObject.getInVoyNbr();
						outVoyNbr = vesselRelatedValueObject.getOutVoyNbr();
						shpgSvcCd = vesselRelatedValueObject.getShpgSvcCd();
						vslOprCd = vesselRelatedValueObject.getVslOprCd();
						String shiftIn = String.valueOf(shiftInd);
						String[] tempString2 = { vvCd, vslNm, inVoyNbr, outVoyNbr, shpgSvcCd, vslOprCd, shiftIn };
						throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.Error_0013, tempString2));
					} // Cally Neo 10 Feb 2015 SL-LWMS-20141106-01: Add meaningful exceptional report
						// remarks.
					else if (isLT || tariffMainCat.substring(0, 1).equals(ProcessChargeConst.PENJURU_BUSINESS)
							|| tariffMainCat.substring(0, 1).equals(ProcessChargeConst.MARINA_SOUTH_BUSINESS)) {
						String[] tempString2 = {tariffMainCat};
						throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_LWMS_Not_Process, tempString2));
	
					} // replaced else statement as it was wrong in 1.5 version
					else {
						String[] tempString2 = {vvCd,Integer.toString(shiftInd)};
						throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Berth_Timestamp_NotFound, tempString2));
					}
				}
			}
		} catch (BusinessException e) {
			log.info("Exception retrieveBerthDttm : ", e);
			throw new BusinessException(e.getMessage());
		
		} catch (Exception e) {
			log.info("Exception retrieveBerthDttm : ", e);
			throw new BusinessException("M4201");
		
		} finally {
			log.info("END: retrieveBerthDttm Dao End  berthRelatedValueObject: " + berthRelatedValueObject.toString());
		}
		
		return berthRelatedValueObject;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->retrieveCustomizeTariffDtls()
	public TariffMainVO retrieveCustomizeTariffDtls(int versionNbr, String custCd, String acctNbr, String contractNbr,
			int contractualYr, String tariffMainCatCd, String tariffSubCatCd,
			// amended by hujun on 7/6/2011 for add tariff item effective date
			// parameter
			String tariffCd, int tierSeqNbr, Timestamp varDttm) throws BusinessException {
		// amended end
		TariffMainVO tariffMainValueObject = null;

		try {
			log.info("START retrieveCustomizeTariffDtls Dao :: versionNbr: " + CommonUtility.deNull(String.valueOf(versionNbr)) + " custCd: "
					+ CommonUtility.deNull(custCd) + " acctNbr:" + CommonUtility.deNull(acctNbr) + " contractNbr: " + CommonUtility.deNull(contractNbr)
					+ " contractualYr: " + CommonUtility.deNull(String.valueOf(contractualYr)) + " tariffMainCatCd: " + CommonUtility.deNull(tariffMainCatCd)
					+ " tariffSubCatCd: " + CommonUtility.deNull(tariffSubCatCd));
			
			tariffMainValueObject = tariffMainRepo.retrieveTariffByCdTierSeqNbr(versionNbr, custCd, acctNbr,
					contractNbr, contractualYr, tariffMainCatCd, tariffSubCatCd, tariffCd, tierSeqNbr, varDttm);
			// amended end
		} catch (BusinessException e) {
			log.info("Exception retrieveCustomizeTariffDtls : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception retrieveCustomizeTariffDtls : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END retrieveCustomizeTariffDtls DAO  tariffMainValueObject: " + tariffMainValueObject.toString());
		}
		return tariffMainValueObject;
	}

	// jp.src.valueObject.cab.billing--->BillAdjustParamFactory-->create()
 	@Override
	public BillAdjustParam create(String tariffCode) throws BusinessException, Exception {
		int ind[] = null;
		try {
			log.info("START create DAO :: tariffCode: " + CommonUtility.deNull(tariffCode));
			ind = billSupInfoRepo.getIndicator(tariffCode);
		} catch (BusinessException be) {
			log.info("Exception create : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception create : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END create DAO");
		}
		return getParam(ind);
	}

	// jp.src.valueObject.cab.billing--->BillAdjustParamFactory-->getParam()
	private static BillAdjustParam getParam(int ind[]) {
		log.info("START getParam :: ind: " + CommonUtility.deNull(String.valueOf(ind)));
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
			retVal = new BillAdjParamCTOVO();
		} else {
			retVal = null;
		}
		log.info("END getParam  retVal: " + retVal);
		return retVal;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGeneric--->isCancelledVessel()
	public boolean isCancelledVessel(String vvCd) throws BusinessException {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		boolean cancelled = false;

		try {
			log.info("START isCancelledVessel DAO :: vvCd: " + CommonUtility.deNull(vvCd));
			StringBuffer sqlSB = new StringBuffer();
			sqlSB.append("SELECT vv_status_ind FROM vessel_call ");
			sqlSB.append(" WHERE vv_cd =:vvCd AND vv_status_ind='CX'");
			log.info("isCancelledVessel() - sqlQuery with fix stmt.close(): " // kmho max cursor error
					+ sqlSB.toString());
			
			paramMap.put("vvCd", vvCd);
			log.info("paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlSB.toString(), paramMap);

			if (rs.next()) {
				cancelled = true;
			}
	
		} catch (Exception ex) {
			log.info("Exception isCancelledVessel : ", ex);
			throw new BusinessException("M4201");
//			throw new BusinessException("M0010-8"); // kmho max cursor error
		} finally {
			log.info("END: isCancelledVessel Dao End  cancelled: " + cancelled);
		}
		return cancelled;

	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGeneric--->getEtbBtr()
	public BerthRelatedValueObject getEtbBtr(String vvCd, int shiftInd) throws BusinessException {

		StringBuffer sqlQuery = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		BerthRelatedValueObject berthRelatedValueObject = new BerthRelatedValueObject();
		try {
			log.info("START getEtbBtr DAO :: vvCd: " + CommonUtility.deNull(vvCd) + " shiftInd: " + CommonUtility.deNull(String.valueOf(shiftInd)));
			sqlQuery.append("SELECT call.vsl_berth_dttm, berth.etb_dttm ");
			sqlQuery.append(" FROM berthing berth, vessel_call call ");
			sqlQuery.append(" WHERE call.vv_cd = :vvCd ");
			sqlQuery.append(" AND call.vv_cd = berth.vv_cd(+) ");
			sqlQuery.append(" AND berth.shift_ind = :shiftInd ");
			
			paramMap.put("vvCd", vvCd);
			paramMap.put("shiftInd", shiftInd);
			// ResultSet rs = stmt.executeQuery();
			log.info("getEtbBtr SQL: " + sqlQuery.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

			if (rs.next()) {
				berthRelatedValueObject.setBtrDttm(rs.getTimestamp("vsl_berth_dttm"));
				berthRelatedValueObject.setEtbDttm(rs.getTimestamp("etb_dttm"));
			}
			
		
		} catch (Exception ex) {
			log.info("Exception getEtbBtr : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEtbBtr DAO  berthRelatedValueObject: " + berthRelatedValueObject.toString());
		}
		return berthRelatedValueObject;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->retrieveTariffVersion()
	@Override
	public int retrieveTariffVersion(Timestamp varDttm, VesselRelatedValueObject vesselRelatedValueObject,
			String tariffMainCat) throws BusinessException {


		String vvCd = null;
		int versionNbr = 0;

		try {
			log.info("START retrieveTariffVersion :: varDttm: " + CommonUtility.deNull(varDttm.toString()) + " vesselRelatedValueObject: "
					+ CommonUtility.deNull(vesselRelatedValueObject.toString()) + " tariffMainCat: " + CommonUtility.deNull(tariffMainCat));
			versionNbr = tariffVerRepo.getCurrentVersion(varDttm);

			if (versionNbr <= 0) {
				if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STEVEDORAGE)
						|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_LOLO)
						|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE)
						|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_VESSEL)
						|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_FREQUENCY)
						|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_ADMIN)) {
					vvCd = vesselRelatedValueObject.getVvCd();
					//vslNm = vesselRelatedValueObject.getVslNm();
					//inVoyNbr = vesselRelatedValueObject.getInVoyNbr();
					//outVoyNbr = vesselRelatedValueObject.getOutVoyNbr();
					//shpgSvcCd = vesselRelatedValueObject.getShpgSvcCd();
					//vslOprCd = vesselRelatedValueObject.getVslOprCd();
					String[] tempString2 = {vvCd, varDttm.toString()};
					throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Tariff_Version, tempString2));
				} else {
					vvCd = vesselRelatedValueObject.getVvCd();
					String[] tempString2 = {vvCd, varDttm.toString()};
					throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Tariff_Version, tempString2));
				}
			}
		} catch (BusinessException e) {
			log.info("Exception retrieveTariffVersion : ", e);
			throw new BusinessException(e.getMessage());
		
		} catch (Exception e) {
			log.info("Exception retrieveTariffVersion : ", e);
			throw new BusinessException("M4201");
		}
		log.info("END retrieveTariffVersion DAO  versionNbr: " + versionNbr);
		return versionNbr;
	}

	@Override
	public String getHSCode(String edoEsnAsnNbr, String type, String blNbr, String vvCode)
			throws BusinessException {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sqlQuery = null;
		int count = 0;
		String hsCode = null;
		try {
			
			sqlQuery = new StringBuffer();
			log.info("START getHSCode DAO :: edoEsnAsnNbr: " + CommonUtility.deNull(edoEsnAsnNbr) + " type:" + CommonUtility.deNull(type)
					+ " blNbr: " + CommonUtility.deNull(blNbr) + " vvCode: " + CommonUtility.deNull(vvCode));
			// formulate the sql query and execute the query
			if (type.trim().equalsIgnoreCase("IM")) {
				if (edoEsnAsnNbr != null && !edoEsnAsnNbr.trim().equalsIgnoreCase("")) {
					sqlQuery.append("SELECT HS_CODE from manifest_details where mft_seq_nbr in ( ");
					sqlQuery.append(" select mft_seq_nbr from gb_edo where edo_asn_nbr = :edoEsnAsnNbr) ");
					count = 1;
				} else {
					sqlQuery.append(" select hs_code from manifest_details where bl_nbr = :blNbr and var_nbr = :vvCode");
					count = 2;
				}
			} else {
				sqlQuery.append(" SELECT ESN_HS_CODE from ESN_DETAILS where  esn_asn_nbr = :edoEsnAsnNbr ");
				sqlQuery.append(" UNION ");
				sqlQuery.append(" SELECT HS_CD from TESN_PSA_JP where esn_asn_nbr = :edoEsnAsnNbr");
				sqlQuery.append(" UNION ");
				sqlQuery.append(" select HS_CODE from manifest_details where mft_seq_nbr = ");
				sqlQuery.append(" (select mft_seq_nbr from gb_edo where edo_asn_nbr = (  ");
				sqlQuery.append(" select edo_asn_nbr From tesn_jp_jp where esn_asn_nbr =  :edoEsnAsnNbr ");
				sqlQuery.append(" ))");
				sqlQuery.append(" UNION ");
				sqlQuery.append(" select HS_CODE from manifest_details where mft_seq_nbr = ");
				sqlQuery.append(" (select mft_seq_nbr from gb_edo where edo_asn_nbr = (  ");
				sqlQuery.append(" select edo_asn_nbr From tesn_jp_psa where esn_asn_nbr =  :edoEsnAsnNbr ");
				sqlQuery.append(" ))");
				count = 3;
			}

			// Added to fix for TESN PSA JP
			if (count == 1) {
				paramMap.put("edoEsnAsnNbr", edoEsnAsnNbr);
			} else if (count == 2) {
				paramMap.put("blNbr", blNbr);
				paramMap.put("vvCode", vvCode);
			} else if (count == 3) {
				paramMap.put("edoEsnAsnNbr", edoEsnAsnNbr);
			}
			log.info("getHSCode SQL: " + sqlQuery.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

			if (rs.next()) {
				hsCode = rs.getString(1);
			}
			log.info("Determine HSCodes EDO:" + edoEsnAsnNbr + ":" + hsCode + ":" + type);
			
		
		} catch (Exception e) {
			log.info("Exception hsCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END hsCode DAO");
		}

		return hsCode;
	}

	@Override
	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->getBusTypeForWFSCSR()
	public String getBusTypeForWFSCSR(BerthRelatedValueObject berthRelatedValueObject, String hsCode)
			throws BusinessException {
		String businessType = null;

		try {
			log.info("START getBusTypeForWFSCSR :: berthRelatedValueObject: " + CommonUtility.deNull(berthRelatedValueObject.toString())
					+ " hsCode: " + CommonUtility.deNull(hsCode));
			// set param for container business

			log.info("determineBusinessType in ProcessGenericEJB getJ1Berths:" + getJ1Berths() + ":"
					+ berthRelatedValueObject.getBerthNbr());
			// Added not null for cancellation of berth charges to retrieve
			// tariff under G if without berth number
			if (berthRelatedValueObject.getBerthNbr() != null
					&& (getJ1Berths().indexOf(CommonUtility.deNull(berthRelatedValueObject.getBerthNbr())) >= 0)) { // J1(O)
																													// always
																													// takes
																													// precedence
				businessType = ProcessChargeConst.J1BASIN_BUSINESS;
				log.info("determineBusinessType inside J1  basin **:" + businessType);
			} else if (hsCode.equalsIgnoreCase("72") || hsCode.equalsIgnoreCase("73")) { // Steel (S)
				businessType = ProcessChargeConst.STEEL_BUSINESS;
				log.info("determineBusinessType inside Steel  **:" + businessType);
			} else { // GC(G)
				businessType = ProcessChargeConst.GENERAL_BUSINESS;
				log.info("determineBusinessType inside General **:" + businessType);
			}

			log.info("determineBusinessType in ProcessGenericEJB" + businessType);
		} catch (BusinessException e) {
			log.info("Exception getBusTypeForWFSCSR : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getBusTypeForWFSCSR : ", e);
			throw new BusinessException("M4201");
		}
		log.info("END getBusTypeForWFSCSR DAO  businessType: " + businessType);
		return businessType;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->getJ1Berths()
	public String getJ1Berths() throws BusinessException {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sqlQuery = null;
		String berthNbr = "";

		try {
			log.info("START getJ1Berths DAO");
						// formulate the sql query and execute the query
			sqlQuery = new StringBuffer();
			sqlQuery.append("select value ");
			sqlQuery.append("from user_para ");
			sqlQuery.append("where para_cd = 'J1_BUSINESS' and status_cd = 'A' ");
			log.info("getJ1Berths SQL: " + sqlQuery.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

			if (rs.next()) {
				// get the database values
				berthNbr = rs.getString("value");
			}
			
		} catch (Exception e) {
			log.info("Exception getJ1Berths : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getJ1Berths DAO  berthNbr: " + berthNbr);
		}
		return berthNbr;
	}

	@Override
	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->retrievePublishTariffDtls()
	public TariffMainVO retrievePublishTariffDtls(int versionNbr, String tariffMainCat, String tariffSubCat,
			String businessType, String schemeCd, String mvmt, String type, String cntrCat, String cntrSize,
			Timestamp varDttm) throws BusinessException {
		TariffMainVO tariffMainValueObject = null;

		try {
			log.info("START retrievePublishTariffDtls DAO :: versionNbr: " + CommonUtility.deNull(String.valueOf(versionNbr)) + " tariffMainCat: "
					+ CommonUtility.deNull(tariffMainCat) + " tariffSubCat: " + CommonUtility.deNull(tariffSubCat) + " businessType: "
					+ CommonUtility.deNull(businessType) + " schemeCd: " + CommonUtility.deNull(schemeCd) + " mvmt: "
					+ CommonUtility.deNull(mvmt) + " type: " + CommonUtility.deNull(type) + " cntrCat: " + CommonUtility.deNull(cntrCat)
					+ " cntrSize: " + CommonUtility.deNull(cntrSize) + " varDttm: " + CommonUtility.deNull(varDttm.toString()));
			tariffMainValueObject = tariffMainRepo.retrieveTariffMainTierGenChar(versionNbr, null, null, null, 0,
					tariffMainCat, tariffSubCat, businessType, schemeCd, mvmt, type, cntrCat, cntrSize, varDttm);
			if (!tariffMainValueObject.isModified(tariffMainValueObject)) { //
				String[] tempString2 = {Integer.toString(versionNbr), tariffMainCat, tariffSubCat, businessType,
						schemeCd, mvmt, type, cntrCat, cntrSize};
				throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Publish_Tariff, tempString2));

			} 
			List<GstCodeValueObject> gstCodeList = new ArrayList<GstCodeValueObject>(1);
			gstCodeList = getGstCharge(tariffMainValueObject.getGSTCode(), varDttm);

			if (gstCodeList.size() == 0) {
				String[] tempString2 = {tariffMainValueObject.getGSTCode(),varDttm.toString() };
				throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_GST_Rate, tempString2));

			}

			GstCodeValueObject gstCodeValueObject = (GstCodeValueObject) gstCodeList.get(0);

			tariffMainValueObject.setGSTCode(gstCodeValueObject.getCode());
			tariffMainValueObject.setGST(gstCodeValueObject.getGstCharge());
		} catch (BusinessException e) {
			log.info("Exception retrievePublishTariffDtls : ", e);
			throw new BusinessException(e.getMessage());
		
		} catch (Exception e) {
			log.info("Exception retrievePublishTariffDtls : ", e);
			throw new BusinessException("M4201");
		
		} finally {
			log.info("END: retrievePublishTariffDtls Dao End  tariffMainValueObject: " + tariffMainValueObject.toString());
		}
		
		return tariffMainValueObject;
	}

	@Override
	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->retrieveCashCustAcct()
	public AccountValueObject retrieveCashCustAcct() throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet prs = null; SqlRowSet prs1 = null;
		AccountValueObject accountValueObject = new AccountValueObject();
		
		try {
			log.info("START retrieveCashCustAcct DAO ");
			String cashCustCodeQuery = "select value from text_para where para_cd=:para_cd";
			String cashCustAcctQuery = "select value from text_para where para_cd=:accCash";
			
			paramMap.put("para_cd", ProcessChargeConst.CASH_CUST_PARA_CD);
			paramMap.put("accCash", ProcessChargeConst.CASH_CUST_ACCT_NBR);
			
			log.info("retrieveCashCustAcct SQL 1: " + cashCustCodeQuery.toString() + " paramMap: " + paramMap);
			prs =namedParameterJdbcTemplate.queryForRowSet(cashCustCodeQuery.toString(), paramMap);
			if (prs.next()) {
				accountValueObject.setCustomerCode(CommonUtility.deNull(prs.getString(1)));
				log.info("cashCustAcctQuery SQL 2: " + cashCustAcctQuery.toString() + " paramMap: " + paramMap);
				prs1 = namedParameterJdbcTemplate.queryForRowSet(cashCustAcctQuery.toString(), paramMap);
				if (prs1.next()) {
					accountValueObject.setAccountNumber(CommonUtility.deNull(prs1.getString(1)));
				}
			}
	
		} catch (Exception ex) {
			log.info("Exception retrieveCashCustAcct : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveCashCustAcct DAO ");
		}
		return accountValueObject;
	}

	@Override
	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->retrievePeriodicSRLastTriggerDttm()
	public Timestamp retrievePeriodicSRLastTriggerDttm(String refNbr, String refInd, String prevPSRBillDays)
			throws  BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		Timestamp ts = null;
		log.info("START retrievePeriodicSRLastTriggerDttm DAO :: refNbr: " + CommonUtility.deNull(refNbr) + " refInd: " + CommonUtility.deNull(refInd)
				+ " prevPSRBillDays: " + CommonUtility.deNull(prevPSRBillDays));
		log.info("before retrievePeriodicSRLastTriggerDttm()  ");
		try {
			String srSql = "select last_triggered_dttm from periodic_sr_bill_run_log where ref_nbr=:refNbr and ref_ind=:refInd and trans_nbr = (select max(trans_nbr) from periodic_sr_bill_run_log where ref_nbr=:refNbr and ref_ind=:refInd)";

			if (prevPSRBillDays != null && prevPSRBillDays.equals("Y")) {
				srSql = "select last_triggered_dttm from periodic_sr_bill_run_log where ref_nbr=:refNbr and ref_ind=:refInd and trans_nbr = (select max(trans_nbr-1) from periodic_sr_bill_run_log where ref_nbr=:refNbr and ref_ind=:refInd)";
			}
			
			paramMap.put("refNbr", refNbr);
			paramMap.put("refInd", refInd);
			log.info("retrievePeriodicSRLastTriggerDttm SQL: " + srSql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(srSql.toString(), paramMap);
			while (rs.next()) {
				ts = rs.getTimestamp("last_triggered_dttm");
			}

			log.info("after retrievePeriodicSRLastTriggerDttm()  ");

		
		} catch (Exception e) {
			log.info("Exception retrievePeriodicSRLastTriggerDttm : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrievePeriodicSRLastTriggerDttm DAO  ts: " + ts);
		}
		return ts;
	}

	@Override
	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGenericEJB--->retrievePublishTariffDtls()
	public TariffMainVO retrievePublishTariffDtls(int versionNbr, String tariffMainCat, String tariffSubCat,
			String schemeCd, String businessType, Timestamp varDttm) throws BusinessException {

		TariffMainVO tariffMainValueObject = null;

		try {
			log.info("START retrievePublishTariffDtls DAO :: versionNbr: " + CommonUtility.deNull(String.valueOf(versionNbr)) + " tariffMainCat: "
					+ CommonUtility.deNull(tariffMainCat) + " tariffSubCat: " + CommonUtility.deNull(tariffSubCat) + " schemeCd: " + CommonUtility.deNull(schemeCd)
					+  " businessType: " + CommonUtility.deNull(businessType) + " varDttm: " + CommonUtility.deNull(varDttm.toString()));
			tariffMainValueObject = tariffMainRepo.retrieveTariffMainTierAdm(versionNbr, null, null, null, 0,
					tariffMainCat, tariffSubCat, schemeCd, businessType, varDttm);
			// amended end

			// if tariff main value object (specific) is not found,
			// retrieve tariff main details (general)
			if (!tariffMainValueObject.isModified(tariffMainValueObject)) {
				String[] tempString2 = {Integer.toString(versionNbr)};
				throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Publish_Tariff, tempString2));
//				ex = new ProcessChargeException("#Publish Tariff cannot be found# for ~version_nbr (" + versionNbr
//						+ ") ~tariff_main_cat_cd (" + tariffMainCat + ") ~tariff_sub_cat_cd (" + tariffSubCat
//						+ ") ~business_type (" + businessType + ")");
//				throw ex;
			}

			// Obtain the new gst rate based on timestamp
			List<GstCodeValueObject> gstCodeList = new ArrayList<GstCodeValueObject>(1);
			gstCodeList = processGBGenericRepo.getGstCharge(tariffMainValueObject.getGSTCode(), varDttm);

			if (gstCodeList.size() == 0) {
				String[] tempString2 = {tariffMainValueObject.getGSTCode(),varDttm.toString() };
				throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_GST_Rate, tempString2));
			}

			GstCodeValueObject gstCodeValueObject = (GstCodeValueObject) gstCodeList.get(0);

			tariffMainValueObject.setGSTCode(gstCodeValueObject.getCode());
			tariffMainValueObject.setGST(gstCodeValueObject.getGstCharge());

		} catch (BusinessException e) {
			log.info("Exception retrievePublishTariffDtls : ", e);
			throw new BusinessException(e.getMessage());
		
		} catch (Exception e) {
			log.info("Exception retrievePublishTariffDtls : ", e);
			throw new BusinessException("M4201");
		
		} finally {
			log.info("END: retrievePublishTariffDtls Dao End  tariffMainValueObject: " + tariffMainValueObject.toString());
		}
		return tariffMainValueObject;
	}

	@Override
	public boolean determineSteelVsl(String vvCode) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		new StringBuffer();
		StringBuffer sqlQuery = null;
		int count = 0;
		boolean isSteelVsl = true;
		try {
			log.info("START: determineSteelVsl  DAO  Start Obj " + " vvCode:" + vvCode);
			if (getCargoRecords(vvCode) > 0) {

				// formulate the sql query and execute the query
				sqlQuery = new StringBuffer();
				sqlQuery.append("SELECT VAR_NBR  FROM MANIFEST_DETAILS WHERE (HS_CODE NOT IN ('72','73')");
				sqlQuery.append(" AND VAR_NBR = :vvCode) AND BL_STATUS  = 'A' UNION ");
				sqlQuery.append("SELECT ESN.OUT_VOY_VAR_NBR  FROM ESN_DETAILS,");
				sqlQuery.append(" ESN WHERE (ESN_HS_CODE NOT IN ('72','73')) ");
				sqlQuery.append(" AND ESN.ESN_ASN_NBR = ESN_DETAILS.ESN_ASN_NBR AND");
				sqlQuery.append(" ESN.OUT_VOY_VAR_NBR = :vvCode AND ESN.ESN_STATUS = 'A'");
				sqlQuery.append(" UNION SELECT ESN.OUT_VOY_VAR_NBR  FROM TESN_PSA_JP,");
				sqlQuery.append(" ESN WHERE ( HS_CD NOT IN ('72','73'))");
				sqlQuery.append("  AND ESN.ESN_ASN_NBR = TESN_PSA_JP.ESN_ASN_NBR ");
				sqlQuery.append("AND ESN.OUT_VOY_VAR_NBR = :vvCode  AND ESN.ESN_STATUS = 'A'");

				paramMap.put("vvCode", vvCode);

				log.info(" *** determineSteelVsl SQL *****" + sqlQuery.toString() + " paramMap " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);
				if (rs.next()) {
					count = 0; // mixed
				} else {
					count = 1; // pure
				}
				log.info("[CAB],Determine Steel vsl values in if:" + count);
			} else {
				count = 0; // no records in database
			}
			log.info("[CAB],Determine Steel vsl values outisde if:" + count);
			if (count == 0) {
				isSteelVsl = false;
			} else {
				isSteelVsl = true;
			}
		} catch (NullPointerException e) {
			log.error("Exception: determineSteelVsl ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: determineSteelVsl ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineSteelVsl Result:" + isSteelVsl);
		}
		return isSteelVsl;
	}

	@Override
	public String determineBusinessType(String cntrVslInd, BerthRelatedValueObject berthRelatedValueObject,
			String cementVslInd, boolean hasOnlySteel) throws BusinessException {
		String businessType = null;
		try {
			log.info("START: determineBusinessType  DAO cntrVslInd:" + cntrVslInd + ",berthRelatedValueObject:"
					+ (berthRelatedValueObject != null ? berthRelatedValueObject.toString() : null) + ",cementVslInd:"
					+ cementVslInd + ",hasOnlySteel:" + hasOnlySteel);
			// set param for container business
			if (CommonUtility.deNull(cntrVslInd).equals(ProcessChargeConst.CONTAINER_VSL)) {
				businessType = ProcessChargeConst.CONTAINER_BUSINESS; // Cntr(C)
			} else if (CommonUtility.deNull(cntrVslInd).equals(ProcessChargeConst.GENERAL_BULK_VSL)) {
				if (bulkCargoExist(berthRelatedValueObject)) {
					if (CommonUtility.deNull(cementVslInd).equalsIgnoreCase("Y")) {
						businessType = ProcessChargeConst.CEMENT_BUSINESS; // Cement (E)
					} else {
						businessType = ProcessChargeConst.BULK_BUSINESS; // Bulk (B)
					}
				} else { // to check and return business type in the below order
					log.info("[CAB],determineBusinessType in ProcessGenericEJB getJ1Berths:" + getJ1Berths() + ":"
							+ berthRelatedValueObject.getBerthNbr());
					// Added not null for cancellation of berth charges to retrieve tariff under G
					// if without berth number
					if (berthRelatedValueObject.getBerthNbr() != null && (getJ1Berths()
							.indexOf(CommonUtility.deNull(berthRelatedValueObject.getBerthNbr())) >= 0)) {
						// J1(O) always takes precedence
						businessType = ProcessChargeConst.J1BASIN_BUSINESS;
					} else if (hasOnlySteel) { // Steel (S)
						businessType = ProcessChargeConst.STEEL_BUSINESS;
					} else { // GC(G)
						businessType = ProcessChargeConst.GENERAL_BUSINESS;
					}
				}
			}
		} catch (Exception e) {
			log.error("Exception: determineBusinessType ", e);
			new BusinessException(ConstantUtil.Error_0016);
		} finally {
			log.info("END: determineBusinessType  DAO  END Result:" + businessType);
		}
		return businessType;
	}

	// ejb.sessionBeans.cab.processCharges--->ProcessGBGeneric-->getGstCharge()
	@Override
	public List<GstCodeValueObject> getGstCharge(String gstCode, Timestamp date) throws BusinessException {
		return getGstCharge(gstCode, date, false);
	}

	// ejb.sessionBeans.cab.processCharges-->ProcessGenericEJB-->getGstCharge()
	@Override
	public List<GstCodeValueObject> getGstCharge(String gstCode, Timestamp date, boolean listAll)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<GstCodeValueObject> arrayList = new ArrayList<GstCodeValueObject>();
		int ctr = 0;
		try {
			log.info("START: getGstCharge  DAO  Start gstCode:" + gstCode + " date:" + date + " listAll:" + listAll);

			sb.append(" select gst_charge, fmas_gst_cd, eff_start_dttm, eff_end_dttm, ");
			sb.append(" to_char(eff_start_dttm, 'dd/mm/yyyy'), to_char(eff_end_dttm, 'dd/mm/yyyy'), ");
			sb.append(" misc_type_nm from gst_para, misc_type_code where gst_para.rec_status='A' ");
			sb.append(" and misc_type_code.rec_status='A' and cat_cd='FMAS_GCODE' and fmas_gst_cd=misc_type_cd ");
			if (gstCode != null) {
				sb.append(" and tariff_gst_cd= :gstCode ");
				paramMap.put("gstCode", gstCode.trim());
			}
			if (date != null) {
				if (listAll) {
					sb.append(" and ( eff_start_dttm <=:date or eff_end_dttm <=:date ) ");
				} else {
					sb.append(" and eff_start_dttm<=:date and (eff_end_dttm>=:date or eff_end_dttm is null) ");
				}
			}
			sb.append(" order by tariff_gst_cd asc, eff_start_dttm desc");

			if (date != null) {
				paramMap.put("date", date);
			}

			log.info(" *** getGstCharge SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
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
		} catch (NullPointerException e) {
			log.error("Exception: getGstCharge ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getGstCharge ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getGstCharge DAO Result:" + arrayList.size());
		}
		return arrayList;
	}

	@Override
	public String retrieveLineCode(String oprCd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sqlQuery = null;
		String lineCd = null;
		try {
			log.info("START: retrieveLineCode  DAO  Start oprCd: " + oprCd);

			// formulate the sql query and execute the query
			// amended by swho on 261202 for checking of activated or inactivated records
			// sqlQuery = "select co1_cd from company_relation where lob_cd=? and co2_cd=?";
			sqlQuery = "select co1_cd from company_relation where lob_cd= :lob and co2_cd= :oprCd and rec_status= :rec ";

			paramMap.put("lob", "OPR");
			paramMap.put("oprCd", oprCd);
			paramMap.put("rec", "A");
			// end amend by swho
			log.info(" *** retrieveLineCode SQL *****" + sqlQuery + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, paramMap);

			if (rs.next()) {
				lineCd = rs.getString("co1_cd");
			}

			if (lineCd == null) {
				String[] tmp = { oprCd };
				throw new BusinessException(
						CommonUtil.getErrorMessage(ConstantUtil.ErrMsg_Company_Code_Not_Found_For_Opr_Cd, tmp));

			}
		} catch (NullPointerException e) {
			log.error("Exception: retrieveLineCode ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveLineCode ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveLineCode  DAO  END Result:" + lineCd);
		}
		return lineCd;
	}

	@Override
	public AccountValueObject retrieveCustAcct(String lineCd) throws BusinessException {
		return retrieveCustAcctByStatus(lineCd, false);
	}

	private AccountValueObject retrieveCustAcctByStatus(String lineCd, boolean isActiveAcct) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sqlQuery = null;
		AccountValueObject accountValueObject = null;
		String custCd = null;
		String acctNbr = null;
		String acctStatusCd = null;
		String currency = null;
		try {
			log.info("START: retrieveCustAcctByStatus  DAO  Start lineCd:" + lineCd + ",:isActiveAcct" + isActiveAcct);

			// formulate the sql query and execute the query
			sqlQuery = "select acct_nbr, acct_status_cd, currency from cust_acct where cust_cd= :lineCd and substr(business_type,1,1)= :type ";

			paramMap.put("lineCd", lineCd);
			paramMap.put("type", "C");

			log.info(" *** retrieveCustAcctByStatus SQL *****" + sqlQuery + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, paramMap);
			// initialize AccountValueObject
			accountValueObject = new AccountValueObject();

			if (rs.next()) {
				// get the database values
				custCd = lineCd;
				acctNbr = rs.getString("acct_nbr");
				acctStatusCd = rs.getString("acct_status_cd");
				currency = rs.getString("currency");

				// set the retrieved database values into the AccountValueObject
				accountValueObject.setCustomerCode(custCd);
				accountValueObject.setAccountNumber(acctNbr);
				accountValueObject.setStatusCode(acctStatusCd);
				accountValueObject.setCurrency(currency);
			}

			if (!accountValueObject.isModified(accountValueObject)) {
				String[] tmp = { lineCd };
				throw new BusinessException(
						CommonUtil.getErrorMessage(ConstantUtil.ErrMsg_Customer_Acc_Not_Found_For_Cust_Cd, tmp));

			}

			// Amended by Jade for CR-CAB-20130516-004 only check if account is active when
			// the parameter is true
			// if (accountValueObject.getStatusCode().equals("I")) {
			if (isActiveAcct && accountValueObject.getStatusCode().equals("I")) {
				log.info("Processing inactive billing account for company code:: " + lineCd);
				// End of amendment by Jade for CR-CAB-20130516-004
				String[] tmp = { lineCd };
				throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrMsg_Customer_Acc_Inactive, tmp));

			}
		} catch (NullPointerException e) {
			log.error("Exception: retrieveCustAcctByStatus ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveCustAcctByStatus ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveCustAcctByStatus  DAO  END Result:"
					+ (accountValueObject != null ? accountValueObject.toString() : null));
		}
		return accountValueObject;
	}

	@Override
	public ContractSearchKeyValueObject retrieveCustContractExist(AccountValueObject accountValueObject,
			String idCodeInd, String idCode, Timestamp varDttm) throws BusinessException {
		ContractSearchKeyValueObject contractSearchKeyValueObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sqlQuery = null;
		String custCd = null;
		String acctNbr = null;
		String contractNbr = null;
		String idCd = null;
		String idCdInd = null;
		try {
			log.info("START: retrieveCustContractExist  DAO  Start Obj " + " accountValueObject:" + accountValueObject
					+ " idCodeInd:" + idCodeInd + " idCode:" + idCode + " varDttm:" + varDttm);

			// formulate the sql query and execute the query
			// amended by swho on 080802 to cater for checking of the contract validity
			// period
			// sqlQuery = "select * from cust_contract_srch_key where cust_cd=? and
			// acct_nbr=? and id_code=? and id_code_ind=? ";
			sqlQuery = "select a.contract_nbr from cust_contract_srch_key a, cust_contract b where a.cust_cd=b.cust_cd and a.acct_nbr=b.acct_nbr and a.contract_nbr=b.contract_nbr and a.cust_cd= :custCd and a.acct_nbr= :acctNbr and a.id_code= :idCode and a.id_code_ind= :idCodeInd and commence_dttm<= :varDttm and expiry_dttm>=:varDttm ";

			custCd = accountValueObject.getCustomerCode();
			acctNbr = accountValueObject.getAccountNumber();
			paramMap.put("custCd", custCd);
			paramMap.put("acctNbr", acctNbr);
			paramMap.put("idCode", idCode);
			paramMap.put("idCodeInd", idCodeInd);
			paramMap.put("varDttm", varDttm);
			// end amend by swho
			log.info(" *** retrieveCustContractExist SQL *****" + sqlQuery + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, paramMap);

			// initialize ContractSearchKeyValueObject
			contractSearchKeyValueObject = new ContractSearchKeyValueObject();

			if (rs.next()) {
				// get the database values
				contractNbr = rs.getString("contract_nbr");
				idCd = idCode;
				idCdInd = idCodeInd;

				// set the retrieved database values into the ContractSearchKeyValueObject
				contractSearchKeyValueObject.setCustomerCode(custCd);
				contractSearchKeyValueObject.setAccountNumber(acctNbr);
				contractSearchKeyValueObject.setContractNumber(contractNbr);
				contractSearchKeyValueObject.setIDCode(idCd);
				contractSearchKeyValueObject.setIDCodeIndicator(idCdInd);
			}
		} catch (NullPointerException e) {
			log.error("Exception: retrieveCustContractExist ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveCustContractExist ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveCustContractExist  DAO  END Result:"
					+ (contractSearchKeyValueObject != null ? contractSearchKeyValueObject.toString() : null));
		}
		return contractSearchKeyValueObject;
	}

	@Override
	public int retrieveCustTariffVersion(ContractSearchKeyValueObject contractSearchKeyValueObject, Timestamp atbDttm)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		String contractNbr = null;
		SqlRowSet rs = null;
		String sqlQuery = null;
		String custCd = null;
		String acctNbr = null;
		int versionNbr = 0;
		try {
			log.info("START: retrieveCustTariffVersion  DAO  Start contractSearchKeyValueObject: "
					+ (contractSearchKeyValueObject != null ? contractSearchKeyValueObject.toString() : "-")
					+ ",atbDttm:" + atbDttm);

			sb.append("select cust_tariff_version_nbr from cust_contract where cust_cd=:custCd and acct_nbr=:acctNbr");
			sb.append(" and contract_nbr= :contractNbr and commence_dttm<= :atbDttm and expiry_dttm>= :atbDttm ");
			sqlQuery = sb.toString();

			custCd = contractSearchKeyValueObject.getCustomerCode();
			acctNbr = contractSearchKeyValueObject.getAccountNumber();
			contractNbr = contractSearchKeyValueObject.getContractNumber();
			paramMap.put("custCd", custCd);
			paramMap.put("acctNbr", acctNbr);
			paramMap.put("contractNbr", contractNbr);
			paramMap.put("atbDttm", atbDttm);

			log.info(" *** retrieveCustTariffVersion SQL *****" + sqlQuery + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, paramMap);

			if (rs.next()) {
				versionNbr = rs.getInt("cust_tariff_version_nbr");
			}
		} catch (NullPointerException e) {
			log.error("Exception: retrieveCustTariffVersion ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveCustTariffVersion ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveCustTariffVersion  DAO  END Result:" + versionNbr);
		}
		return versionNbr;
	}

	@Override
	public ContractValueObject retrieveCustContractByAcctNbr(AccountValueObject accountValueObject, Timestamp atbDttm)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		ContractValueObject contractValueObject = null;
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		String sqlQuery = null;
		String custCd = null;
		String acctNbr = null;
		try {
			log.info("START: retrieveCustContractByAcctNbr  DAO  Start Obj " + " accountValueObject:"
					+ (accountValueObject != null ? accountValueObject.toString() : "-") + " atbDttm:" + atbDttm);
			sb.append("select cust_cd, acct_nbr, contract_nbr, business_type,");
			sb.append("cust_tariff_version_nbr from cust_contract where cust_cd= :custCd");
			sb.append(" and acct_nbr= :acctNbr and commence_dttm <= :atbDttm and expiry_dttm>= :atbDttm");
			sqlQuery = sb.toString();

			custCd = accountValueObject.getCustomerCode();
			acctNbr = accountValueObject.getAccountNumber();
			paramMap.put("custCd", custCd);
			paramMap.put("acctNbr", acctNbr);
			paramMap.put("atbDttm", atbDttm);
			log.info(" *** retrieveCustContractByAcctNbr SQL *****" + sqlQuery + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, paramMap);

			contractValueObject = new ContractValueObject();
			if (rs.next()) {
				contractValueObject.setCustomerCode(rs.getString("cust_cd"));
				contractValueObject.setAccountNumber(rs.getString("acct_nbr"));
				contractValueObject.setContractNumber(rs.getString("contract_nbr"));
				contractValueObject.setBusinessType(rs.getString("business_type"));
				contractValueObject.setCustomerTariffVersion(rs.getInt("cust_tariff_version_nbr"));
			}
		} catch (NullPointerException e) {
			log.error("Exception: retrieveCustContractByAcctNbr ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveCustContractByAcctNbr ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveCustContractByAcctNbr DAO Result:" + contractValueObject.toString());
		}
		return contractValueObject;
	}

	private boolean bulkCargoExist(BerthRelatedValueObject berthRelatedValueObject) throws BusinessException {
		Timestamp codDttm = null;
		Timestamp colDttm = null;
		boolean existInd = false;
		try {
			log.info("START: bulkCargoExist  DAO berthRelatedValueObject: "
					+ (berthRelatedValueObject != null ? berthRelatedValueObject.toString() : "-"));
			codDttm = berthRelatedValueObject.getGbBCodDttm();
			colDttm = berthRelatedValueObject.getGbBColDttm();

			if (codDttm != null || colDttm != null) {
				existInd = true;
			} else {
				existInd = false;
			}

		} catch (Exception e) {
			log.error("Exception: bulkCargoExist ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: bulkCargoExist  DAO  END Result:" + existInd);
		}
		return existInd;
	}

	private int getCargoRecords(String vvCode) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sqlQuery = null;
		int count = 0;
		try {
			log.info("START: getCargoRecords  DAO  Start Obj " + " vvCode:" + vvCode);

			sqlQuery = new StringBuffer();
			sqlQuery.append("SELECT VAR_NBR  FROM MANIFEST_DETAILS WHERE   BL_STATUS  = 'A' AND VAR_NBR = :vvCode");
			sqlQuery.append(" UNION ");
			sqlQuery.append(" SELECT ESN.OUT_VOY_VAR_NBR  FROM ESN_DETAILS, ESN WHERE ESN.ESN_ASN_NBR = ");
			sqlQuery.append(" ESN_DETAILS.ESN_ASN_NBR AND ESN.OUT_VOY_VAR_NBR = :vvCode AND ESN.ESN_STATUS = 'A'");
			sqlQuery.append(" UNION");
			sqlQuery.append(" SELECT ESN.OUT_VOY_VAR_NBR  FROM TESN_PSA_JP, ESN WHERE ESN.ESN_ASN_NBR = ");
			sqlQuery.append(" TESN_PSA_JP.ESN_ASN_NBR AND ESN.OUT_VOY_VAR_NBR = :vvCode  AND ESN.ESN_STATUS = 'A' ");

			paramMap.put("vvCode", vvCode);

			log.info(" *** getCargoRecords SQL *****" + sqlQuery.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);
			while (rs.next()) {
				count = count + 1;
			}
		} catch (NullPointerException e) {
			log.error("Exception: getCargoRecords ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getCargoRecords ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoRecords  DAO  END Result:" + count);
		}
		return count;
	}

	@Override
	public int retrieveMaxShiftInd(String vvCd) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sqlQuery = null;
		int maxShiftInd = 0;
		try {
			log.info("START: retrieveMaxShiftInd  DAO vvCd:" + vvCd);
			sqlQuery = "select max(shift_ind) from berthing where vv_cd= :vvCd ";
			paramMap.put("vvCd", vvCd);
			log.info(" *** retrieveMaxShiftInd SQL *****" + sqlQuery + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, paramMap);
			if (rs.next()) {
				maxShiftInd = rs.getInt(1);
			} else {
				String[] tmp = { vvCd };
				throw new BusinessException(
						CommonUtil.getErrorMessage(ConstantUtil.ErrMsg_Berthing_Shift_Indicater_Not_Found, tmp));
			}
		} catch (NullPointerException e) {
			log.error("Exception: retrieveMaxShiftInd ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveMaxShiftInd ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveMaxShiftInd  DAO  Result:" + maxShiftInd);
		}
		return maxShiftInd;
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public List<TempProcessChargeValueObject> sortTempProcessCharge(
			List<TempProcessChargeValueObject> tempProcessChargeCollection, String tariffMainCat)
			throws BusinessException {
		Object[] tempProcessChargeArray = null;
		try {
			log.info("START: sortTempProcessCharge  DAO tempProcessChargeCollection:"
					+ tempProcessChargeCollection.size() + ",tariffMainCat:" + tariffMainCat);
			tempProcessChargeArray = tempProcessChargeCollection.toArray();

			insertTempProcessCharge(tempProcessChargeArray);

			tempProcessChargeCollection = retrieveSortedTempProcessCharge(tariffMainCat);

			deleteTempProcessCharge();
		} catch (BusinessException e) {
			log.error("Exception: sortTempProcessCharge ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception: sortTempProcessCharge ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: sortTempProcessCharge  DAO  Result:" + tempProcessChargeCollection);
		}
		return tempProcessChargeCollection;
	}

	private void insertTempProcessCharge(Object[] tempProcessChargeArray) throws BusinessException {
		TempProcessChargeValueObject tempProcessChargeValueObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sqlQuery = null;
		int insertResult = 0;
		int xcnt = 0;
		Integer versionNbr = 0;
		String tariffCd = "";
		Integer tierSeqNbr = 0;
		double nbrTimeUnit = 0;
		double nbrOtherUnit = 0;
		String custCd = "";
		String acctNbr = "";
		String contractNbr = "";
		String vvCd = "";
		String slotOprCd = "";
		String cntrOprCd = "";
		Integer cntrSeqNbr = 0;
		Timestamp txnDttm = null;
		String txnCd = "";
		Integer seqNbr = 0;
		try {
			log.info("START: insertTempProcessCharge DAO tempProcessChargeArray:" + tempProcessChargeArray.toString());

			sqlQuery = new StringBuffer();
			sqlQuery.append("insert into temp_process_charge (version_nbr, tariff_cd, tariff_tier_seq,");
			sqlQuery.append("nbr_time_unit, nbr_other_unit, cust_cd, acct_nbr, contract_nbr, ");
			sqlQuery.append("vv_cd, slot_opr_cd, cntr_opr_cd, cntr_seq_nbr, txn_dttm, ");
			sqlQuery.append("txn_cd, seq_nbr) ");
			sqlQuery.append("values(:versionNbr,:tariffCd,:tariffTierSeq,");
			sqlQuery.append(":nbrTimeUnit,:nbrOtherUnit,:custCd,:acctNbr,:contractNbr,");
			sqlQuery.append(":vvCd,:slotOprCd,:cntrSeqNbr,:cntrOprCd,:txnDttm,");
			sqlQuery.append(":txnCd,:seqNbr)");

			for (xcnt = 0; xcnt < tempProcessChargeArray.length; xcnt++) {
				tempProcessChargeValueObject = (TempProcessChargeValueObject) Array.get(tempProcessChargeArray, xcnt);

				versionNbr = tempProcessChargeValueObject.getVersionNbr();
				tariffCd = tempProcessChargeValueObject.getTariffCd();
				tierSeqNbr = tempProcessChargeValueObject.getTierSeqNbr();
				nbrTimeUnit = tempProcessChargeValueObject.getNbrTimeUnit();
				nbrOtherUnit = tempProcessChargeValueObject.getNbrOtherUnit();
				custCd = tempProcessChargeValueObject.getCustCd();
				acctNbr = tempProcessChargeValueObject.getAcctNbr();
				contractNbr = tempProcessChargeValueObject.getContractNbr();
				vvCd = tempProcessChargeValueObject.getVvCd();
				slotOprCd = tempProcessChargeValueObject.getSlotOprCd();
				cntrOprCd = tempProcessChargeValueObject.getCntrOprCd();
				cntrSeqNbr = tempProcessChargeValueObject.getCntrSeqNbr();
				txnDttm = tempProcessChargeValueObject.getTxnDttm();
				txnCd = tempProcessChargeValueObject.getTxnCd();
				seqNbr = tempProcessChargeValueObject.getSeqNbr();

				if (versionNbr != null)
					paramMap.put("versionNbr", versionNbr.intValue());
				else
					paramMap.put("versionNbr", null);

				if (tariffCd != null)
					paramMap.put("tariffCd", tariffCd);
				else
					paramMap.put("tariffCd", null);

				if (tierSeqNbr != null)
					paramMap.put("tariffTierSeq", tierSeqNbr.intValue());
				else
					paramMap.put("tariffTierSeq", null);

				paramMap.put("nbrTimeUnit", nbrTimeUnit);
				paramMap.put("nbrOtherUnit", nbrOtherUnit);

				if (custCd != null)
					paramMap.put("custCd", custCd);
				else
					paramMap.put("custCd", null);

				if (acctNbr != null)
					paramMap.put("acctNbr", acctNbr);
				else
					paramMap.put("acctNbr", null);

				if (contractNbr != null)
					paramMap.put("contractNbr", contractNbr);
				else
					paramMap.put("contractNbr", null);

				if (vvCd != null)
					paramMap.put("vvCd", vvCd);
				else
					paramMap.put("vvCd", null);

				if (slotOprCd != null)
					paramMap.put("slotOprCd", slotOprCd);
				else
					paramMap.put("slotOprCd", null);

				if (cntrOprCd != null)
					paramMap.put("cntrOprCd", cntrOprCd);
				else
					paramMap.put("cntrOprCd", null);

				if (cntrSeqNbr != null)
					paramMap.put("cntrSeqNbr", cntrSeqNbr.intValue());
				else
					paramMap.put("cntrSeqNbr", null);

				if (txnDttm != null)
					paramMap.put("txnDttm", txnDttm);
				else
					paramMap.put("txnDttm", null);

				if (txnCd != null)
					paramMap.put("txnCd", txnCd);
				else
					paramMap.put("txnCd", null);

				if (seqNbr != null)
					paramMap.put("seqNbr", seqNbr.intValue());
				else
					paramMap.put("seqNbr", null);

				log.info(" *** insertTempProcessCharge SQL *****" + sqlQuery.toString() + " paramMap "
						+ paramMap.toString());

				insertResult = namedParameterJdbcTemplate.update(sqlQuery.toString(), paramMap);
				if (insertResult == 0) {
					throw new BusinessException(ConstantUtil.ErrMsg_Insert_Failed);
				}
			}
		} catch (BusinessException e) {
			log.error("Exception: insertTempProcessCharge ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception: insertTempProcessCharge ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertTempProcessCharge  DAO Result:" + insertResult);
		}
	}

	private List<TempProcessChargeValueObject> retrieveSortedTempProcessCharge(String tariffMainCat)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sqlQuery = null;
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

		TempProcessChargeValueObject tempProcessChargeValueObject = null;
		List<TempProcessChargeValueObject> tempProcessChargeList = null;
		try {
			log.info("START: retrieveSortedTempProcessCharge  DAO  Start Obj " + " tariffMainCat:" + tariffMainCat);

			sqlQuery = new StringBuffer();
			sqlQuery.append("select * from temp_process_charge ");
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STEVEDORAGE)) {
				// VietNguyen add decode(substr(tariff_cd,4,2),'SP','A','Z') to split other
				// billing es :START
				sqlQuery.append("order by decode(substr(tariff_cd,4,2),'SP','A','Z'), cust_cd, acct_nbr,");
				sqlQuery.append(" vv_cd, slot_opr_cd, cntr_opr_cd, tariff_cd, txn_cd, nbr_other_unit");
				// VietNguyen add decode(substr(tariff_cd,4,2),'SP','A','Z') to split other
				// billing es :END
			}
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_STORE_RENT)) {
				// amended by swho on 200503 to break into new bill when change of vv cd and
				// tariff sub cat
				// sqlQuery.append("order by cust_cd, acct_nbr, vv_cd, slot_opr_cd, cntr_opr_cd,
				// tariff_cd, txn_cd, nbr_time_unit, nbr_other_unit");
				sqlQuery.append("order by cust_cd, acct_nbr, vv_cd, tariff_cd, slot_opr_cd, ");
				sqlQuery.append("cntr_opr_cd, txn_cd, nbr_time_unit, nbr_other_unit");
				// end amend by swho
			}
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_LOLO)) {
				// amended by swho on 200503 to break into new bill when change of vv cd and
				// tariff sub cat
				// sqlQuery.append("order by cust_cd, acct_nbr, vv_cd, slot_opr_cd, cntr_opr_cd,
				// tariff_cd, txn_cd");
				sqlQuery.append("order by cust_cd, acct_nbr, vv_cd, tariff_cd, slot_opr_cd,");
				sqlQuery.append(" cntr_opr_cd, txn_cd, tariff_tier_seq ");
				// end amend by swho
			}
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_MARINE)) {
				// sqlQuery.append("order by cust_cd, acct_nbr, tariff_cd, tariff_tier_seq");
				// comment off by YJ for version 1.34
				sqlQuery.append("order by cust_cd, acct_nbr, decode(substr(tariff_cd,4,2),'07','01A',");
				sqlQuery.append("substr(tariff_cd,4,2)), tariff_tier_seq ");
				// by YJ 9/Feb/04 for adding of special dockage in bill
			}
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_VESSEL)) {
				// amended by swho on 200503 to break into new bill when change of vv cd and
				// tariff sub cat
				// sqlQuery.append("order by cust_cd, acct_nbr, vv_cd, slot_opr_cd, cntr_opr_cd,
				// tariff_cd, txn_cd");
				sqlQuery.append("order by cust_cd, acct_nbr, vv_cd, tariff_cd, slot_opr_cd, cntr_opr_cd, txn_cd");
				// end amend by swho
			}
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OTHERS_BY_FREQUENCY)) {
				// amended by swho on 200503 to break into new bill when change of vv cd and
				// tariff sub cat
				// sqlQuery.append("order by cust_cd, acct_nbr, vv_cd, slot_opr_cd, cntr_opr_cd,
				// tariff_cd, txn_cd, nbr_time_unit, nbr_other_unit");
				sqlQuery.append("order by cust_cd, acct_nbr, vv_cd, tariff_cd, slot_opr_cd, cntr_opr_cd,");
				sqlQuery.append(" txn_cd, nbr_time_unit, nbr_other_unit, cntr_seq_nbr");
				// end amend by swho
			}
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_ADMIN)) {
				// amended by swho on 200503 to break into new bill when change of vv cd and
				// tariff sub cat
				// sqlQuery.append("order by cust_cd, acct_nbr, vv_cd, slot_opr_cd, cntr_opr_cd,
				// tariff_cd, txn_cd, nbr_time_unit, nbr_other_unit");
				sqlQuery.append("order by cust_cd, acct_nbr, vv_cd, tariff_cd, slot_opr_cd,");
				sqlQuery.append(" cntr_opr_cd, txn_cd, nbr_time_unit, nbr_other_unit");
				// end amend by swho
			}
			// amended by satish on 07012004 to break into new bill for Container bills
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_GC)) {
				sqlQuery.append("order by cust_cd, acct_nbr, vv_cd, tariff_cd, tariff_tier_seq,");
				sqlQuery.append(" txn_cd, nbr_time_unit, nbr_other_unit");
			}
			// end amend by satish
			// VietNguyen
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_GB_CONTAINER_STORE_RENT)) {
				sqlQuery.append("order by cust_cd, acct_nbr, cntr_seq_nbr, txn_dttm, txn_cd,");
				sqlQuery.append(" tariff_cd, tariff_tier_seq, txn_cd, nbr_time_unit, nbr_other_unit");
			}
			// end amend by satish
			// VietNguyen (FPT) added on 07-Dec-2011 for OMC : START
			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_DOCKAGE)) {
				sqlQuery.append("order by cust_cd, acct_nbr, tariff_cd, tariff_tier_seq ");
				// .append("order by cust_cd, acct_nbr ");
				log.info("[CAB],sortedTempProcessCharge records for tariff main cat : " + tariffMainCat);
			}

			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_WHARFAGE)) {
				sqlQuery.append("order by cust_cd, acct_nbr, tariff_cd, tariff_tier_seq, cntr_seq_nbr ");
				log.info("[CAB],sortedTempProcessCharge records for tariff main cat : " + tariffMainCat);
				// .append("order by cust_cd, acct_nbr ");
			}

			if (tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_EQUIPMENT_RENTAL)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_PLOT_USAGE)
					|| tariffMainCat.equals(ProcessChargeConst.TARIFF_MAIN_OMC_OTHER)) {
				sqlQuery.append("order by cust_cd, acct_nbr, tariff_cd, tariff_tier_seq, nbr_other_unit ");
				log.info("[CAB],sortedTempProcessCharge records for tariff main cat : " + tariffMainCat);
				// .append("order by cust_cd, acct_nbr ");
			}
			// VietNguyen (FPT) added on 02-Dec-2011 for OMC : END

			log.info(" *** retrieveSortedTempProcessCharge SQL *****" + sqlQuery.toString() + " paramMap "
					+ paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

			tempProcessChargeList = new ArrayList<TempProcessChargeValueObject>();
			while (rs.next()) {
				// get the database values
				versionNbr = new Integer(rs.getInt("version_nbr"));
				tariffCd = rs.getString("tariff_cd");
				tierSeqNbr = new Integer(rs.getInt("tariff_tier_seq"));
				nbrTimeUnit = rs.getDouble("nbr_time_unit");
				nbrOtherUnit = rs.getDouble("nbr_other_unit");
				custCd = rs.getString("cust_cd");
				acctNbr = rs.getString("acct_nbr");
				contractNbr = rs.getString("contract_nbr");
				vvCd = rs.getString("vv_cd");
				slotOprCd = rs.getString("slot_opr_cd");
				cntrOprCd = rs.getString("cntr_opr_cd");
				cntrSeqNbr = new Integer(rs.getInt("cntr_seq_nbr"));
				txnDttm = rs.getTimestamp("txn_dttm");
				txnCd = rs.getString("txn_cd");
				seqNbr = new Integer(rs.getInt("seq_nbr"));
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

				tempProcessChargeList.add(tempProcessChargeValueObject);
			}
		} catch (NullPointerException e) {
			log.error("Exception: retrieveSortedTempProcessCharge ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveSortedTempProcessCharge ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveSortedTempProcessCharge DAO Result:"
					+ (tempProcessChargeList != null ? tempProcessChargeList.size() : "-"));
		}
		return tempProcessChargeList;
	}

	private void deleteTempProcessCharge() throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sqlQuery = null;
		int deleteResult = 0;
		try {
			log.info("START: deleteTempProcessCharge DAO");

			sqlQuery = "delete from temp_process_charge";
			log.info(" *** deleteTempProcessCharge SQL *****" + sqlQuery);

			deleteResult = namedParameterJdbcTemplate.update(sqlQuery, paramMap);
			if (deleteResult == 0) {
				throw new SQLException(ConstantUtil.ErrMsg_Delete_Failed);
			}
		} catch (NullPointerException e) {
			log.error("Exception: deleteTempProcessCharge ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: deleteTempProcessCharge ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: deleteTempProcessCharge  DAO Result:" + deleteResult);
		}
	}

	@Override
	public List<TariffTierBillPartyVO> retrieveTariffTierBillParty(String tariffMainCat, String tariffSubCat,
			String mvmtType, String businessType, Timestamp varDttm, String oprCd) throws BusinessException {
		SqlRowSet rs = null;
		TariffTierBillPartyVO vo = null;
		StringBuffer sqlQuery = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<TariffTierBillPartyVO> tariffTierBillPartyList = new ArrayList<TariffTierBillPartyVO>();
		try {
			log.info("START: retrieveTariffTierBillParty DAO TariffMainCat:" + tariffMainCat + ", tariffSubCat= "
					+ tariffSubCat + ", mvmtType= " + mvmtType + ", businessType= " + businessType + ", varDttm = "
					+ varDttm + ", oprCd = " + oprCd);

			sqlQuery.setLength(0);
			sqlQuery.append("SELECT /*ProcessGenericEJB - retrieveTariffTierBillParty()*/ ");
			sqlQuery.append("EFF_START_DTTM, EFF_END_DTTM, CUT_OFF, ");
			sqlQuery.append("BILL_PARTY, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM ");
			sqlQuery.append("FROM TARIFF_TIER_BILL_PARTY ");
			sqlQuery.append("WHERE TARIFF_MAIN_CAT_CD  = :tariffMainCat ");
			sqlQuery.append("AND TARIFF_SUB_CAT_CD = :tariffSubCat ");
			sqlQuery.append("AND MVMT_TYPE = :mvmtType ");
			sqlQuery.append("AND BUSINESS_TYPE = :businessType ");
			sqlQuery.append("AND EFF_START_DTTM <  :varDttm AND (EFF_END_DTTM IS NULL OR EFF_END_DTTM > :varDttm)");
			// sauwoon CR-CAB-20060224-01
			sqlQuery.append("AND (CUST_CD = :oprCd or cust_cd is null) order by cust_cd");
			// CR-CAB-20100506-005 add oprCd, ordering will display alpha first then null

			paramMap.put("tariffMainCat", tariffMainCat);
			paramMap.put("tariffSubCat", tariffSubCat);
			paramMap.put("mvmtType", mvmtType);
			paramMap.put("businessType", businessType);
			paramMap.put("varDttm", varDttm); // sauwoon CR-CAB-20060224-01
			paramMap.put("oprCd", oprCd); // CR-CAB-20100506-005 sauwoon cater for cust cd

			log.info(" *** retrieveTariffTierBillParty SQL *****" + sqlQuery.toString() + " paramMap "
					+ paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);
			if (rs.next()) {
				// change from while to if. cos if 2 records (one with cust cd and one without)
				// are returned, we want the one with cust cd
				vo = new TariffTierBillPartyVO();
				vo.setTariffMainCat(tariffMainCat);
				vo.setTariffSubCat(tariffSubCat);
				vo.setMvmtType(mvmtType);
				vo.setBusinessType(businessType);
				vo.setEffStartDttm(rs.getTimestamp("EFF_START_DTTM"));
				vo.setEffEndDttm(rs.getTimestamp("EFF_END_DTTM"));
				vo.setCutOff(rs.getDouble("CUT_OFF"));
				vo.setBillParty(rs.getString("BILL_PARTY"));
				vo.setUser(rs.getString("LAST_MODIFY_USER_ID"));
				vo.setTimestamp(rs.getTimestamp("LAST_MODIFY_DTTM"));
				vo.setOprCd(oprCd); // CR-CAB-20100506-005
				tariffTierBillPartyList.add(vo);
			}
		} catch (NullPointerException e) {
			log.error("Exception: retrieveTariffTierBillParty ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveTariffTierBillParty ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveTariffTierBillParty  DAO Result:" + tariffTierBillPartyList.size());
		}
		return tariffTierBillPartyList;
	}

	@Override
	public String determineVvCd(CntrEventLogValueObject cntrEventLogValueObject) throws BusinessException {
		String vvCd = null;
		Integer cntrSeqNbr = null;
		Timestamp txnDttm = null;
		String txnCd = null;
		String purpCd = null;
		String intergatewayInd = null;
		String discGateway = null;
		String discVvCd = null;
		String loadVvCd = null;
		try {
			log.info("START: determineVvCd DAO cntrEventLogValueObject :"
					+ (cntrEventLogValueObject != null ? cntrEventLogValueObject.toString() : "-"));

			cntrSeqNbr = cntrEventLogValueObject.getCntrSeqNbr();
			txnDttm = cntrEventLogValueObject.getTxnDttm();
			txnCd = cntrEventLogValueObject.getTxnCd();
			purpCd = cntrEventLogValueObject.getPurpCd();
			intergatewayInd = cntrEventLogValueObject.getIntergatewayInd();
			discVvCd = cntrEventLogValueObject.getDiscVvCd();
			loadVvCd = cntrEventLogValueObject.getLdVvCd();

			// amended by swho on 120104 to cater for re-export mvmt for ITH LOLO Charge
			// if (purpCd.equals(ProcessChargeConst.PURP_CD_TRANSHIP)) {
			if (purpCd.equals(ProcessChargeConst.PURP_CD_TRANSHIP)
					|| purpCd.equals(ProcessChargeConst.PURP_CD_REEXPORT)) {
				// end amend by swho
				if (intergatewayInd != null) {
					if (intergatewayInd.equals("N")) {
						vvCd = discVvCd;
					} else {
						discGateway = retrieveDiscGateway(cntrSeqNbr);

						if (discGateway.equals("J")) {
							vvCd = discVvCd;
						} else {
							vvCd = loadVvCd;
						}
					}
				} else {
					vvCd = discVvCd;
				}
			}

			// amended by swho on 120104 to cater for re-export mvmt for ITH LOLO Charge
			// else if (purpCd.equals(ProcessChargeConst.PURP_CD_REEXPORT) ||
			// purpCd.equals(ProcessChargeConst.PURP_CD_IMPORT) ||
			// purpCd.equals(ProcessChargeConst.PURP_CD_LAND_RESHIP)) {
			else if (purpCd.equals(ProcessChargeConst.PURP_CD_IMPORT)
					|| purpCd.equals(ProcessChargeConst.PURP_CD_LAND_RESHIP)) {
				// end amend by swho
				vvCd = discVvCd;
			} else if (purpCd.equals(ProcessChargeConst.PURP_CD_RESHIPMENT)
					|| purpCd.equals(ProcessChargeConst.PURP_CD_EXPORT)) {
				vvCd = loadVvCd;
			} else if (purpCd.equals(ProcessChargeConst.PURP_CD_STORAGE)) {
				vvCd = null;
			}

			// VietNguyen added code to implment CR Uturn 15 Sep 2009 Start
			// Enhancement for Other by Frequency - CISM
			// Transaction is a CSIM event (transaction is for changing status to local
			// import)
			// There is a Shut event (which can be non-chargeable) prior to the CSIM event
			// Container is discharged from PSA
			// U-turn indicator can be Yes (ie, U turn back to PSA) or No (ie, not U turn
			// back to PSA)
			// Movement code in event log is Import
			// => get load vv_cd for billing
			String uturnIdn = getUturnIndicator(cntrSeqNbr.intValue());
			if ("".equalsIgnoreCase(uturnIdn) || uturnIdn == null) {
				uturnIdn = "";
			}
			if ("N".equalsIgnoreCase(intergatewayInd) && ProcessChargeConst.PURP_CD_IMPORT.equalsIgnoreCase(purpCd)
					&& ConstantUtil.TXN_CD_CHANGE_STATUS_IMPORT.equalsIgnoreCase(txnCd)
					&& "P".equalsIgnoreCase(retrieveDiscGateway(cntrSeqNbr))
					// CungTD 30/12/2009 Fix error IM CSIM not billed because vv_cd=null.
					// && (!"".equalsIgnoreCase(uturnIdn)) && isShutout(cntrSeqNbr.intValue())) {
					// SL-CAB-20110318-01 lsw to change from isShutout(?) to isShutout(?,?) which
					// will only check for shut that happen before txnDttm and without any
					// subsequent renom
					// && isShutout(cntrSeqNbr.intValue())) {
					&& isShutout(cntrSeqNbr.intValue(), txnDttm)) {
				// last loading vv cd indicated for this container shall be used instead
				vvCd = getLastLoadVvCd(cntrSeqNbr.intValue());
				log.info("Enhancement for Other by Frequency - CISM - last load_vv_cd for cntr " + cntrSeqNbr + " is "
						+ vvCd);
			}
			// VietNguyen added code to implment CR Uturn 15 Sep 2009 End
		} catch (NullPointerException e) {
			log.error("Exception: determineVvCd ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: determineVvCd ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineVvCd DAO Result:" + CommonUtil.deNull(vvCd));
		}
		return vvCd;
	}

	private boolean isShutout(int cntrSeqNbr, Timestamp otherTxnDttm) throws BusinessException {
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		boolean isShutout = false;
		Timestamp shutTxnDttm = null;
		Timestamp renomTxnDttm = null;
		StringBuffer sb = new StringBuffer();
		Timestamp shutLastModifyDttm = null;
		Timestamp renomLastModifyDttm = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: isEsn DAO cntrSeqNbr:" + cntrSeqNbr + " & otherTxnDttm : " + otherTxnDttm);

			sb.append("select cntr_seq_nbr, txn_dttm, last_modify_dttm from cntr_txn where txn_cd='SHUT' and ");
			sb.append("cntr_seq_nbr= :cntrSeqNbr and to_char(txn_dttm, 'YYYYMMDD HH24MISS') ");
			sb.append("<= to_char(:otherTxnDttm, 'YYYYMMDD HH24MISS') order by txn_dttm desc");
			String sql = sb.toString();
			// String sql1 = "select cntr_seq_nbr, txn_dttm from cntr_txn where
			// txn_cd='RENO' and cntr_seq_nbr=? and to_char(txn_dttm, 'YYYYMMDD HH24MISS')
			// >= to_char(?, 'YYYYMMDD HH24MISS') order by txn_dttm desc";

			sb = new StringBuffer();
			sb.append("select cntr_seq_nbr, txn_dttm, last_modify_dttm from cntr_txn where txn_cd='RENO' and");
			sb.append(" cntr_seq_nbr= :cntrSeqNbr and to_char(txn_dttm, 'YYYYMMDD HH24MI') >= ");
			sb.append("to_char(:shutTxnDttm, 'YYYYMMDD HH24MI') and to_char(last_modify_dttm,'YYYYMMDD HH24MISS') >=");
			sb.append(" to_char(:shutLastModifyDttm,'YYYYMMDD HH24MISS') ");
			sb.append("and to_char(last_modify_dttm,'YYYYMMDD HH24MISS')");
			sb.append(" <= to_char(:otherTxnDttm,'YYYYMMDD HH24MISS') order by txn_dttm desc");
			String sql1 = sb.toString();

			paramMap.put("cntrSeqNbr", cntrSeqNbr);
			paramMap.put("otherTxnDttm", otherTxnDttm);

			log.info(" *** isShutout SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				// there is a Shut done before the transaction (eg, MOUT). the SQL sorts by
				// last modify dttm desc so the last SHUT before the other txn
				// (eg MOUNT) is the 1st record returned.
				// Next, need to check if there is any RENOM between the SHUT and the other txn
				// (eg MOUNT)
				shutTxnDttm = rs.getTimestamp("txn_dttm");
				shutLastModifyDttm = rs.getTimestamp("last_modify_dttm");
				log.info("[CAB],isShutout:shut transacted at " + shutTxnDttm + " and last modified at "
						+ shutLastModifyDttm);
				isShutout = true;
			}

			if (isShutout) { // now to check if there is a renom after this last SHUT
				paramMap.put("cntrSeqNbr", cntrSeqNbr);
				// pstmt.setTimestamp(2, otherTxnDttm);
				// SL-CIM-20110114-01 bug. should be check against shut txn dttm not others (eg
				// mount) dttm
				// compare up to minute only. cos if shut and renom are done at PSA, its using
				// same function with same txn dttm
				// so JPOnline will randomise the seconds of the renom txndttm to prevent unique
				// constraint. so on top of that have to compare last modify dttm too
				paramMap.put("shutTxnDttm", shutTxnDttm);
				paramMap.put("shutLastModifyDttm", shutLastModifyDttm);
				// renom last modify dttm >= shut last modify dttm
				paramMap.put("otherTxnDttm", otherTxnDttm);
				// renom last modify dttm <= other (eg mount) txn dttm (ie, has cntr shut and
				// renom before the mount was done

				log.info(" *** isShutout SQL *****" + sql1 + " paramMap " + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
				if (rs1.next()) {
					// there is a Shut done after the transaction (eg, MOUT). Now to check if there
					// using if rs.next will just return the first record (ie, the latest SHUT).
					// now to check if there is a renom after this last SHUT
					renomTxnDttm = rs1.getTimestamp("txn_dttm");
					renomLastModifyDttm = rs1.getTimestamp("last_modify_dttm");
					log.info("[CAB],isShutout:there is a reno after shut transacted at " + renomTxnDttm
							+ " and last modified at " + renomLastModifyDttm);
					isShutout = false;
				}
			}
		} catch (NullPointerException e) {
			log.error("Exception: isShutout ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: isShutout ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isShutout  DAO Result:" + isShutout);
		}
		return isShutout;
	}

	private String getUturnIndicator(int cntrSeqNbr) throws BusinessException {
		String uturnInd = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getUturnIndicator DAO cntrSeqNbr :" + cntrSeqNbr);
			String sql = "select u_turn_ind from ith_cntr where cntr_seq_nbr = :cntrSeqNbr";

			paramMap.put("cntrSeqNbr", cntrSeqNbr);
			log.info(" *** getUturnIndicator SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				uturnInd = CommonUtility.deNull(rs.getString("u_turn_ind"));
			}
		} catch (NullPointerException e) {
			log.error("Exception: getUturnIndicator ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getUturnIndicator ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUturnIndicator DAO Result:" + uturnInd);
		}
		return uturnInd;
	}

	@Override
	public String retrieveDiscGateway(Integer cntrSeqNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sqlQuery = null;
		String discGateway = "";
		try {
			log.info("START: retrieveDiscGateway DAO cntrSeqNbr:" + cntrSeqNbr);

			sqlQuery = new StringBuffer();
			sqlQuery.append("select disc_gateway ");
			sqlQuery.append("from cntr ");
			sqlQuery.append("where cntr_seq_nbr= :cntrSeqNbr ");

			if (cntrSeqNbr != null)
				paramMap.put("cntrSeqNbr", cntrSeqNbr.intValue());
			else
				paramMap.put("cntrSeqNbr", null);

			log.info(" *** retrieveDiscGateway SQL *****" + sqlQuery.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

			if (rs.next()) {
				discGateway = rs.getString("disc_gateway");
				if (discGateway != null) {
					discGateway = discGateway.trim();
				}
			}
		} catch (NullPointerException e) {
			log.error("Exception: retrieveDiscGateway ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveDiscGateway ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveDiscGateway DAO Result:" + discGateway);
		}
		return discGateway;
	}

	@Override
	public boolean isShutout(int cntrNbr) throws BusinessException {
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		boolean isShutout = false;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: isShutout DAO cntrNbr: " + cntrNbr);

			String sql = "select cntr_seq_nbr from cntr where cntr_seq_nbr= :cntrNbr and shipment_status='SO'";

			paramMap.put("cntrNbr", cntrNbr);
			log.info(" *** isShutout SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				isShutout = true;
			}
			// if isShutout= false then check in event_log_cntr
			if (!isShutout) {
				String sql1 = "select * from cntr_event_log where txn_cd='SHUT' and cntr_seq_nbr= :cntrNbr";
				log.info("isShutout::: sql check in cntr_eventlog : " + sql1);

				paramMap.put("cntrNbr", cntrNbr);
				log.info(" *** isShutout SQL *****" + sql1 + " paramMap " + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
				if (rs1.next()) {
					isShutout = true;
				}
			}
			// CungTD 10/11/2009 if isisShutout= false then check in cntr_txn table
			if (!isShutout) {
				String sql2 = "select cntr_seq_nbr from cntr_txn where txn_cd='SHUT' and cntr_seq_nbr= :cntrNbr";

				paramMap.put("cntrNbr", cntrNbr);
				log.info(" *** isShutout SQL *****" + sql2 + " paramMap " + paramMap.toString());
				rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
				if (rs2.next()) {
					isShutout = true;
				}
			}
		} catch (NullPointerException e) {
			log.error("Exception: isShutout ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: isShutout ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isShutout  DAO Result:" + isShutout);
		}
		return isShutout;
	}

	@Override
	public String getLastLoadVvCd(int cntrSeqNbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String lastLdVvCd = null;
		try {
			log.info("START: getLastLoadVvCd DAO cntrNbr: "  + cntrSeqNbr);

			String sql = "select load_vv_cd from audit_trail_cntr where cntr_seq_nbr= :cntrSeqNbr and load_vv_cd is not null order by audit_dttm desc";

			paramMap.put("cntrSeqNbr", cntrSeqNbr);
			log.info(" *** getLastLoadVvCd SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				lastLdVvCd = rs.getString("load_vv_cd");
			}
		} catch (NullPointerException e) {
			log.error("Exception: getLastLoadVvCd ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getLastLoadVvCd ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getLastLoadVvCd DAO:" + lastLdVvCd);
		}
		return lastLdVvCd;
	}
	// EndRegion ProcessGenericJdbcRepository

}
