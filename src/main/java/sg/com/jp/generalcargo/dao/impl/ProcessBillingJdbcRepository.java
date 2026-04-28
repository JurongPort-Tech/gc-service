package sg.com.jp.generalcargo.dao.impl;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javassist.tools.rmi.RemoteException;
import sg.com.jp.generalcargo.dao.BillSupportInfoRepository;
import sg.com.jp.generalcargo.dao.LWMSCommonUtilRepository;
import sg.com.jp.generalcargo.dao.LighterBillRepository;
import sg.com.jp.generalcargo.dao.ProcessBillingRepository;
import sg.com.jp.generalcargo.dao.ProcessGenericRepository;
import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.domain.BillAdjParamCOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCTOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCTVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCVO;
import sg.com.jp.generalcargo.domain.BillAdjParamOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamTOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamTVO;
import sg.com.jp.generalcargo.domain.BillAdjustParam;
import sg.com.jp.generalcargo.domain.BillErrorVO;
import sg.com.jp.generalcargo.domain.CargoDeclarationItemVO;
import sg.com.jp.generalcargo.domain.CargoDeclarationVO;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.CranageShiftVO;
import sg.com.jp.generalcargo.domain.CranageVO;
import sg.com.jp.generalcargo.domain.TariffMainVO;
import sg.com.jp.generalcargo.domain.TariffTierVO;
import sg.com.jp.generalcargo.domain.VesselRelatedValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.Constant;
import sg.com.jp.generalcargo.util.DateParser;
import sg.com.jp.generalcargo.util.ProcessChargeConst;
import sg.com.jp.generalcargo.util.ProcessChargeException;

@Repository("ProcessBillingJdbcRepository")
public class ProcessBillingJdbcRepository implements ProcessBillingRepository {

	private static final Log log = LogFactory.getLog(ProcessBillingJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	private BillAdjustParam billAdjParam = null;
	private LWMSCommonUtilRepository lwmsComUtilRepo;
	private ProcessGenericRepository processGenericRepo;
	private BillSupportInfoRepository billSupInfoRepo;
	private LighterBillRepository lighterBillRepo;

	// jp.src.ejb.sessionBeans.cab.lwms.ProcessCharges--->ProcessBillingEJB-->retrieveDSAChargableBillVOs()
	public List<CargoDeclarationVO> retrieveDSAChargableBillVOs(String dsaNo) throws BusinessException {
		try {
			log.info("START retrieveDSAChargableBillVOs DAO" + CommonUtility.deNull(dsaNo));
			List<CargoDeclarationVO> result = new ArrayList<CargoDeclarationVO> ();
			List<ChargeableBillValueObject> chargableBillList = new ArrayList<ChargeableBillValueObject>();			
			List<ChargeableBillValueObject> chargeableCraneOpBillList = new ArrayList<ChargeableBillValueObject>();
			List<CargoDeclarationVO> retrievePGChargableBill = retrievePGChargableBillVOsForDSA(dsaNo,chargableBillList, chargeableCraneOpBillList, true);

			result.addAll(retrievePGChargableBill);
//			result.add(chargableBillList);
//			result.add(chargeableCraneOpBillList);
			log.info("END: *** retrieveDSAChargableBillVOs Result *****" + result.size());

			return result;

		} catch (Exception ex) {
			log.info("Exception retrieveDSAChargableBillVOs : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END retrieveDSAChargableBillVOs DAO");
		}

	}

	// jp.src.ejb.sessionBeans.cab.lwms.ProcessCharges--->ProcessBillingEJB-->retrievePGChargableBillVOsForDSA()
	private List<CargoDeclarationVO> retrievePGChargableBillVOsForDSA(String dsaNo,
			List<ChargeableBillValueObject> chargableBillList,
			List<ChargeableBillValueObject> chargableBillCraneOperatorList, boolean forCashSales)
			throws BusinessException {
		List<CargoDeclarationVO> dsaBillVOs = new ArrayList<CargoDeclarationVO>();
		log.info("Starting dsa  . . .");
		try {
			log.info("START retrievePGChargableBillVOsForDSA DAO" +" dsaNo:"+CommonUtility.deNull(dsaNo) +" chargableBillList:"+chargableBillList.size() 
					+" chargableBillCraneOperatorList:"+chargableBillCraneOperatorList.size() +" forCashSales:"+forCashSales);
			List<CargoDeclarationVO> dsaList = new ArrayList<CargoDeclarationVO>();
			dsaList = lighterBillRepo.loadDSAForBillGen(dsaNo);
			CargoDeclarationVO vo = null;

			if (dsaList.size() == 0) {
				return new ArrayList<CargoDeclarationVO>();
			}

			log.info("dsa started  . . .");
			// List lstBillChargeVO = new ArrayList();

			for (int i = 0; i < dsaList.size(); i++) {
				boolean isSuccess = false;

				vo = (CargoDeclarationVO) dsaList.get(i);
				log.info("dsa no: " + vo.getDsa_nbr());

				if (forCashSales) {
					// get the cash sales account no
					AccountValueObject acctVo = getCashCustAccountNbr(vo.getTerminal_cd());
					// vo.setAcct_nbr(acctNbr);
					if (acctVo != null) {
						vo.setAcct_nbr(acctVo.getAccountNumber());
						// vo.setCust_cd(acctVo.getCustomerCode());
					}
				}

				List<ChargeableBillValueObject> lstBillChargeVO = new ArrayList<ChargeableBillValueObject>();
				// List lstBillChargeCrangeVO = new ArrayList();
				List<ChargeableBillValueObject> lstCraneOpBillChargeVO = new ArrayList<ChargeableBillValueObject>();

				// bill for Wharhage
				for (int j = 0; j < vo.getDsaItems().size(); j++) {
					CargoDeclarationItemVO itemVO = (CargoDeclarationItemVO) vo.getDsaItems().get(j);
					ChargeableBillValueObject ibillVO = processDsaItem(vo, itemVO);

					if (ibillVO != null) {
						lstBillChargeVO.add(ibillVO);
						isSuccess = true;
					}
				}

				// bill for Cranage
				if (vo.getDsaCranages() != null) {
					for (int j = 0; j < vo.getDsaCranages().size(); j++) {
						CranageVO cranageVO = (CranageVO) vo.getDsaCranages().get(j);

						// for normal lift
						if (cranageVO.getNorm_lift_nbr() > 0) {

							ChargeableBillValueObject ibillVO = processDsaCranage(vo, cranageVO,
									ProcessChargeConst.BILLREF_CRANAGE_LIFT_NORMAL);

							if (ibillVO != null) {
								lstBillChargeVO.add(ibillVO);
								isSuccess = true;
							}

							ChargeableBillValueObject iCraneOpbillVO = processDsaCraneOperator(vo, cranageVO,
									ProcessChargeConst.BILLREF_CRANAGE_LIFT_NORMAL);

							if (iCraneOpbillVO != null) {
								lstCraneOpBillChargeVO.add(iCraneOpbillVO);
								isSuccess = true;
							}
						}

						// for left on wharf
						if (cranageVO.getWharf_lift_nbr() > 0) {

							ChargeableBillValueObject ibillVO = processDsaCranage(vo, cranageVO,
									ProcessChargeConst.BILLREF_CRANAGE_LIFT_LEFTONWHARF);

							if (ibillVO != null) {
								lstBillChargeVO.add(ibillVO);
								isSuccess = true;
							}

							ChargeableBillValueObject iCraneOpbillVO = processDsaCraneOperator(vo, cranageVO,
									ProcessChargeConst.BILLREF_CRANAGE_LIFT_LEFTONWHARF);

							if (iCraneOpbillVO != null) {
								lstCraneOpBillChargeVO.add(iCraneOpbillVO);
								isSuccess = true;
							}
						}
					}
				}

				if (isSuccess) {
					ChargeableBillValueObject billChargeVO;
					// Collections.sort(lstBillChargeVO, new UtilityBillComparatorByRefNo());
					int PO_SORT_IND = chargableBillList.size();
					for (int k = 0; k < lstBillChargeVO.size(); k++) {
						billChargeVO = (ChargeableBillValueObject) lstBillChargeVO.get(k);
						// billChargeVO.setRefInd(String.valueOf(++PO_SORT_IND));

						String tmpCatCd = billChargeVO.getTariffMainCatCd();
						String refInd = "";
						log.info(tmpCatCd.substring(1));

						if (tmpCatCd.substring(1).equals(ProcessChargeConst.TARIFF_MAINCD_WHARFAGE_SUFFIX))
							refInd = ProcessChargeConst.REF_IND_LWMS_DSA_WHARHAGE;
						else
							refInd = ProcessChargeConst.REF_IND_LWMS_DSA_CRANAGE;

						// billChargeVO.setRefInd(ProcessChargeConst.REF_IND_LWMS_DSA);
						billChargeVO.setRefInd(refInd);
						billChargeVO.setItemNbr(++PO_SORT_IND);
					}
					chargableBillList.addAll(lstBillChargeVO);

					chargableBillCraneOperatorList.addAll(lstCraneOpBillChargeVO);

					dsaBillVOs.add(vo);
				}
			}

			// ChargeableBillValueObject billChargeVO;
			// Collections.sort(lstBillChargeVO, new UtilityBillComparatorByRefNo());
			// int PO_SORT_IND = chargableBillList.size();
			// for(int i=0;i<lstBillChargeVO.size();i++){
			// billChargeVO = (ChargeableBillValueObject) lstBillChargeVO.get(i);
			// billChargeVO.setRefInd(String.valueOf(++PO_SORT_IND));
			// }

			// chargableBillList.addAll(lstBillChargeVO);
			log.info("END: *** retrievePGChargableBillVOsForDSA Result *****" + dsaBillVOs.size());
			return dsaBillVOs;
		} catch (Exception ex) {
			log.info("Exception retrievePGChargableBillVOsForDSA : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END retrievePGChargableBillVOsForDSA DAO");
		}

	}

	// jp.src.ejb.sessionBeans.cab.lwms.ProcessCharges--->ProcessBillingEJB-->getCashCustAccountNbr()
	private AccountValueObject getCashCustAccountNbr(String terminalCd) throws Exception {
		try {
			log.info("START getCashCustAccountNbr DAO " + CommonUtility.deNull(terminalCd));
			String csType = lwmsComUtilRepo.getCsTypeByTerminal(terminalCd);
			if (csType == null || csType.equals(""))
				return null;

			AccountValueObject vo = lwmsComUtilRepo.retrieveCustAcct(csType);
			log.info("END: *** getCashCustAccountNbr Result *****" + vo);

			return vo;
		} catch (Exception e) {
			log.info("Exception getCashCustAccountNbr : ", e);
		} finally {
			log.info("END getCashCustAccountNbr DAO");
		}
		return null;
	}

	// jp.src.ejb.sessionBeans.cab.lwms.ProcessCharges--->ProcessBillingEJB-->processDsaItem()
	private ChargeableBillValueObject processDsaItem(CargoDeclarationVO vo, CargoDeclarationItemVO itemVO)
			throws Exception {
		String tariffMainCatCd = vo.getTerminal_cd() + ProcessChargeConst.TARIFF_MAINCD_WHARFAGE_SUFFIX;
		String subCode = ProcessChargeConst.TARIFF_SUB_WHARFAGE_GENERAL;

		String additionalRemark = "~dsa_nbr(" + vo.getDsa_nbr() + ") ~gatein_dttm("
				+ new Timestamp(vo.getGate_in_dttm().getTime()) + ") ~cust_cd (" + vo.getCust_cd() + ") ~acct_nbr("
				+ vo.getAcct_nbr() + ") ~Msg =";

		if (vo.getAcct_nbr() == null || "".equals(vo.getAcct_nbr())) {
			makeBillError(tariffMainCatCd, ProcessChargeConst.TARIFF_SUB_WHARFAGE_GENERAL,
					additionalRemark + "#Application without Account No.#");
			return null;
		}
		TariffMainVO tariffMainVO;

		try {
			log.info("START processDsaItem DAO"+" vo:"+vo+" itemVO:"+itemVO);
			Timestamp varDttm;
			varDttm = vo.getGate_in_dttm();

			// mongkey .. main cat cd is based on terminal code + constants

			tariffMainVO = getTariffMainVO(tariffMainCatCd, subCode, varDttm, vo.getTerminal_cd());

			TariffTierVO tariffTierVO = tariffMainVO.getTier(0);

			String tariffCd = tariffMainVO.getCode();
			log.info("tariff code=" + tariffCd);

			// String tariffDesc=tariffMainValueObject.getDescription();
			billAdjParam = create(tariffMainVO.getCode());
			if (billAdjParam == null) {
				Exception ex = new ProcessChargeException(
						"~tariff_cd (" + tariffMainVO.getCode() + ") ##");
				throw ex;
			}

			double rate = tariffTierVO.getRate();
			double gst = tariffMainVO.getGST();

			//
			double factorPerUnit = tariffTierVO.getPerUnit();
			double dd = Math.max(itemVO.getActual_wt_ton(), itemVO.getActual_vol_m3());

			// int decimalPlace = 2;
			// BigDecimal bd = new BigDecimal(dd);
			// dd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP).doubleValue();

			// long ll = (long) dd;
			// if (dd - ll > 0)
			// dd = ll +1;
			// else
			// dd = ll;

			double otherUnit = dd / factorPerUnit;
//				
			billAdjParam.setUnitRate(rate);
			billAdjParam.setGst(gst);
			billAdjParam.setTotalTime(0);
			billAdjParam.setTotalOtherUnit(otherUnit);

			ChargeableBillValueObject oBill = vo.buildWharhageChargeableBill(billAdjParam, tariffMainVO,
					tariffTierVO.getId(), rate, 0, itemVO);
			
			log.info("END: *** processDsaItem Result *****" + oBill.toString());
			return oBill;

			// chargeableBillVOs.add(vo.builChargeableBill(billAdjParam, tariffMainVO,
			// tariffTierVO.getId(), rate, 0));
		} catch (Exception e) {
			log.info("Exception processDsaItem : ", e);
			makeBillError(tariffMainCatCd, subCode, additionalRemark + e.getMessage());
		} finally {
			log.info("END processDsaItem DAO");
		}
		return null;
	}

	// jp.src.ejb.sessionBeans.cab.lwms.ProcessCharges--->ProcessBillingEJB-->processDsaCranage()
	private ChargeableBillValueObject processDsaCranage(CargoDeclarationVO vo, CranageVO itemVO, String liftType)
			throws Exception {

		// String tariffMainCatCd = vo.getTerminal_cd() +
		// ProcessChargeConstant.TARIFF_MAINCD_CRANE_SUFFIX;
		// Timestamp shiftAtb = vo.getDockageATB();

		String tariffMainCatCd = "";
		Timestamp shiftAtb = vo.getDockageATB();
		Timestamp timeRef = shiftAtb;
		String shiftNo = "";

		if (liftType.equals(ProcessChargeConst.BILLREF_CRANAGE_LIFT_NORMAL)) {

			tariffMainCatCd = vo.getTerminal_cd() + ProcessChargeConst.TARIFF_MAINCD_CRANE_SUFFIX;
			timeRef = vo.getDockageATB();
		} else {
			tariffMainCatCd = vo.getTerminal_cd() + ProcessChargeConst.TARIFF_MAINCD_CRANEWHARF_SUFFIX;
			timeRef = vo.getOutward_dttm();

		}

		CranageShiftVO shiftVO = getCranageShift(vo.getTerminal_cd(), timeRef);
		shiftAtb = shiftVO.getShiftAtb();
		shiftNo = shiftVO.getShiftNo();

		String subCode = itemVO.getCargo_type() + shiftNo;

		String additionalRemark = "~dsa_nbr(" + vo.getDsa_nbr() + ") ~gatein_dttm(" + vo.getGate_in_dttm()
				+ ") ~cust_cd (" + vo.getCust_cd() + ") ~acct_nbr(" + vo.getAcct_nbr() + ") ~Msg =";

		if (vo.getAcct_nbr() == null || "".equals(vo.getAcct_nbr())) {
			makeBillError(tariffMainCatCd, subCode, additionalRemark + "#Application without Account No.#");
			return null;
		}

		// java.util.Date gateInDate = vo.getGate_in_dttm();
		// Timestamp ts = new Timestamp(gateInDate.getTime());
		TariffMainVO tariffMainVO;

		try {
			log.info("START processDsaCranage DAO" +" vo:"+vo +" itemVO:"+itemVO +" liftType:"+liftType);
			Timestamp varDttm;
			varDttm = shiftAtb; // vo.getGate_in_dttm();

			tariffMainVO = getTariffMainVO(tariffMainCatCd, subCode, varDttm, vo.getTerminal_cd());

			double fromTon = itemVO.getFrom_ton();
			double toTon = itemVO.getTo_ton();

			TariffTierVO tariffTierVO = null;

			// check which tier
			for (int i = 0; i < tariffMainVO.getTierCount(); i++) {
				TariffTierVO tTierVO = tariffMainVO.getTier(i);

				if (fromTon >= tTierVO.getRangeFrom() && toTon <= tTierVO.getRangeTo()) {
					tariffTierVO = tariffMainVO.getTier(i);
					break;
				}
			}

			if (tariffTierVO == null) {
				Exception ex = new ProcessChargeException(
						"~tariff_cd (" + tariffMainVO.getCode() + ") #Bill Tier is null (not match)#");
				throw ex;
			}

			String tariffCd = tariffMainVO.getCode();
			log.info("tariff code=" + tariffCd);
			log.info("tariff tier range from: " + tariffTierVO.getRangeFrom() + " to : " + tariffTierVO.getRangeTo());

			// String tariffDesc=tariffMainValueObject.getDescription();
			billAdjParam = create(tariffMainVO.getCode());
			if (billAdjParam == null) {
				Exception ex = new ProcessChargeException(
						"~tariff_cd (" + tariffMainVO.getCode() + ") #Bill Adj Param is null#");
				throw ex;
			}

			double rate = tariffTierVO.getRate();
			double gst = tariffMainVO.getGST();

			//
			double factorPerUnit = tariffTierVO.getPerUnit();
			double dd = 0;
			if (liftType.equals(ProcessChargeConst.BILLREF_CRANAGE_LIFT_LEFTONWHARF))
				dd = itemVO.getWharf_lift_nbr();
			else
				dd = itemVO.getNorm_lift_nbr();

			double otherUnit = dd / factorPerUnit;

			billAdjParam.setUnitRate(rate);
			billAdjParam.setGst(gst);
			billAdjParam.setTotalTime(0);

			billAdjParam.setTotalOtherUnit(otherUnit);

			ChargeableBillValueObject oBill = vo.buildCranageChargeableBill(billAdjParam, tariffMainVO,
					tariffTierVO.getId(), rate, 0, itemVO, liftType);
			log.info("END: *** processDsaCranage Result *****" + oBill.toString());
			return oBill;

			// chargeableBillVOs.add(vo.builChargeableBill(billAdjParam, tariffMainVO,
			// tariffTierVO.getId(), rate, 0));
		} catch (Exception e) {
			log.info("Exception processDsaCranage : ", e);
			makeBillError(tariffMainCatCd, subCode, additionalRemark + e.getMessage());
		} finally {
			log.info("END processDsaCranage DAO");
		}

		return null;
	}

	// jp.src.ejb.sessionBeans.cab.lwms.ProcessCharges--->ProcessBillingEJB-->processDsaCraneOperator()
	private ChargeableBillValueObject processDsaCraneOperator(CargoDeclarationVO vo, CranageVO itemVO, String liftType)
			throws Exception {

		// String tariffMainCatCd = vo.getTerminal_cd() +
//			 ProcessChargeConstant.TARIFF_MAINCD_CRANEOPERATOR_SUFFIX;
		String tariffMainCatCd = "";
		Timestamp shiftAtb = vo.getDockageATB();
		Timestamp timeRef = shiftAtb;
		String shiftNo = "";

		if (liftType.equals(ProcessChargeConst.BILLREF_CRANAGE_LIFT_NORMAL)) {
			tariffMainCatCd = vo.getTerminal_cd() + ProcessChargeConst.TARIFF_MAINCD_CRANEOPERATOR_SUFFIX;
			timeRef = vo.getDockageATB();
		} else {
			tariffMainCatCd = vo.getTerminal_cd() + ProcessChargeConst.TARIFF_MAINCD_CRANEOPERATORWHARF_SUFFIX;
			timeRef = vo.getOutward_dttm();
		}

		CranageShiftVO shiftVO = getCranageShift(vo.getTerminal_cd(), timeRef);
		shiftAtb = shiftVO.getShiftAtb();
		shiftNo = shiftVO.getShiftNo();

		String subCode = itemVO.getCargo_type() + shiftNo;

		String additionalRemark = "~dsa_nbr(" + vo.getDsa_nbr() + ") ~gatein_dttm(" + vo.getGate_in_dttm()
				+ ") ~cust_cd (" + vo.getCust_cd() + ") ~acct_nbr(" + vo.getAcct_nbr() + ") ~Msg =";

		// if (vo.getAcct_nbr() == null || "".equals(vo.getAcct_nbr()) ) {
		// makeBillError(tariffMainCatCd, subCode, additionalRemark+ "#Application
		// without Account No.#");
		// return null;
		// }

		// java.util.Date gateInDate = vo.getGate_in_dttm();
		// Timestamp ts = new Timestamp(gateInDate.getTime());
		TariffMainVO tariffMainVO;

		try {
			log.info("START processDsaCraneOperator DAO" +" vo:"+vo +" itemVO:"+itemVO +" liftType:"+liftType);
			Timestamp varDttm;
			varDttm = shiftAtb; // vo.getGate_in_dttm();

			tariffMainVO = getTariffMainVO(tariffMainCatCd, subCode, varDttm, vo.getTerminal_cd());

			double fromTon = itemVO.getFrom_ton();
			double toTon = itemVO.getTo_ton();

			TariffTierVO tariffTierVO = null;

			// check which tier
			for (int i = 0; i < tariffMainVO.getTierCount(); i++) {
				TariffTierVO tTierVO = tariffMainVO.getTier(i);

				if (fromTon >= tTierVO.getRangeFrom() && toTon <= tTierVO.getRangeTo()) {
					tariffTierVO = tariffMainVO.getTier(i);
					break;
				}
			}

			if (tariffTierVO == null) {
				Exception ex = new ProcessChargeException(
						"~tariff_cd (" + tariffMainVO.getCode() + ") #Bill Tier is null (not match)#");
				throw ex;
			}

			String tariffCd = tariffMainVO.getCode();
			log.info("tariff code=" + tariffCd);
			log.info("tariff tier range from: " + tariffTierVO.getRangeFrom() + " to : " + tariffTierVO.getRangeTo());

//				String tariffDesc=tariffMainValueObject.getDescription();
			billAdjParam = create(tariffMainVO.getCode());
			if (billAdjParam == null) {
				Exception ex = new ProcessChargeException(
						"~tariff_cd (" + tariffMainVO.getCode() + ") #Bill Adj Param is null#");
				throw ex;
			}

			double rate = tariffTierVO.getRate();
			double gst = tariffMainVO.getGST();

			//
			double factorPerUnit = tariffTierVO.getPerUnit();
			double dd = itemVO.getNorm_lift_nbr() + itemVO.getWharf_lift_nbr();
			double otherUnit = dd / factorPerUnit;

			billAdjParam.setUnitRate(rate);
			billAdjParam.setGst(gst);
			billAdjParam.setTotalTime(0);

			billAdjParam.setTotalOtherUnit(otherUnit);
//				
			ChargeableBillValueObject oBill = vo.buildCraneOperatorChargeableBill(billAdjParam, tariffMainVO,
					tariffTierVO.getId(), rate, 0, itemVO, liftType);
			log.info("END: *** processDsaCraneOperator Result *****" + oBill.toString());
			return oBill;

			// chargeableBillVOs.add(vo.builChargeableBill(billAdjParam, tariffMainVO,
			// tariffTierVO.getId(), rate, 0));
		} catch (Exception e) {
			log.info("Exception processDsaCraneOperator : ", e);
			makeBillError(tariffMainCatCd, subCode, additionalRemark + e.getMessage());
		} finally {
			log.info("END processDsaCraneOperator DAO");
		}

		return null;
	}

	// jp.src.ejb.sessionBeans.cab.lwms.ProcessCharges--->ProcessBillingEJB-->makeBillError()
	private void makeBillError(String tariffMainCat, String tariffSubCat, String exceptionMessage)
			throws BusinessException {
		try {
			log.info("START makeBillError DAO" +" tariffMainCat:"+CommonUtility.deNull(tariffMainCat)  +" tariffSubCat:"+CommonUtility.deNull(tariffSubCat)  +" exceptionMessage:"+CommonUtility.deNull(exceptionMessage) );
			BillErrorVO billErrorVO = null;

			billErrorVO = new BillErrorVO();
			billErrorVO.setRunInd(BillErrorVO.RUN_IND_CREATE_BILL);
			billErrorVO.setTariffMainCat(tariffMainCat);
			billErrorVO.setTariffSubCat(tariffSubCat);
			// GiangNN: change on 05-May-08
			billErrorVO.setRemarks("Exception occurred for ProcessPTBilling: " + exceptionMessage);
			// GiangNN: END

			// insert into Bill Error table
//				billError.insertBillError(billErrorVO);
		} catch (Exception ex) {
			log.info("Exception makeBillError : ", ex);
			throw new BusinessException("M4201");

		} finally {
			log.info("END makeBillError DAO");
		}
	}

	// jp.src.ejb.sessionBeans.cab.lwms.ProcessCharges--->ProcessBillingEJB-->getTariffMainVO()
	private TariffMainVO getTariffMainVO(String tariffMainCat, String tariffSubCat, Timestamp varDttm, String busType)
			throws BusinessException {
		TariffMainVO tariffMainVO = new TariffMainVO();
		final String schemeCd = "000";
		try {
			log.info("START getTariffMainVO DAO" +" tariffMainCat:"+CommonUtility.deNull(tariffMainCat)  +" tariffSubCat:"+CommonUtility.deNull(tariffSubCat)  
			+" varDttm:"+varDttm +" busType:"+CommonUtility.deNull(busType) );
			VesselRelatedValueObject vesselRelatedValueObject = new VesselRelatedValueObject();
			int versionNbr = processGenericRepo.retrieveTariffVersion(varDttm, vesselRelatedValueObject, tariffMainCat);
			tariffMainVO = processGenericRepo.retrievePublishTariffDtls(versionNbr, tariffMainCat, tariffSubCat,
					schemeCd, busType, varDttm);
			tariffMainVO.setVersion(versionNbr);
			
			log.info("END: *** getTariffMainVO Result *****" + tariffMainVO);

		} catch (Exception ex) {
			log.info("Exception getTariffMainVO : ", ex);
			throw new BusinessException("M4201");

		} finally {
			log.info("END getTariffMainVO DAO");
		}
		return tariffMainVO;
	}

	// jp.src.ejb.sessionBeans.cab.lwms.ProcessCharges--->ProcessBillingEJB-->CranageShiftVO()
	private CranageShiftVO getCranageShift(String terminalCd, Timestamp atb) throws Exception {
		Map<String,Object> shiftMap = lwmsComUtilRepo.getMiscType(Constant.MISCTYPECD_SHIFT);
		CranageShiftVO shiftVO = new CranageShiftVO();
		try {
			log.info("START getCranageShift DAO"+" terminalCd:"+CommonUtility.deNull(terminalCd)  
			+" atb:"+atb);
			// Timestamp atb = vo.getDockageATB();
			Date d = new Date(atb.getTime());
			Date atbDate = DateParser.getTruncDate(d);

			NumberFormat nf = NumberFormat.getInstance(); // Get Instance of NumberFormat
			nf.setMinimumIntegerDigits(2); // The minimum Digits required is 5
			nf.setMaximumIntegerDigits(2); // The maximum Digits required is 5

			String t = DateParser.getHours(d) + "" + nf.format(DateParser.getMinutes(d));

			int atbShift = Integer.parseInt(t);
			String shiftNo = "";

			Iterator<String> iterator = shiftMap.keySet().iterator();

			while (iterator.hasNext()) {
				String shiftKey = (String) iterator.next();
				String shiftValue = (String) shiftMap.get(shiftKey);

				int shiftFrom = Integer.parseInt(shiftValue.substring(0, shiftValue.indexOf("-")));
				int shiftTo = Integer.parseInt(shiftValue.substring(shiftValue.indexOf("-") + 1));

				if (shiftTo > shiftFrom) {
					if (atbShift >= shiftFrom && atbShift <= shiftTo) {
						shiftNo = shiftKey;
						break;
					}
				} else {
					if ((atbShift >= shiftFrom && atbShift <= 2359) || (atbShift >= 0 && atbShift <= shiftTo)) {
						shiftNo = shiftKey;
						if (atbShift >= 0 && atbShift <= shiftTo) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(atbDate);
							cal.add(Calendar.DAY_OF_YEAR, -1);
							atbDate = cal.getTime();
						}
						break;
					}
				}
			}

			boolean isHoliday = isDateHoliday(atbDate);
			if (isHoliday) {
				shiftNo = Integer.parseInt(shiftNo) + shiftMap.size() + "";
			}
			shiftVO.setShiftAtb(new Timestamp(atbDate.getTime()));
			shiftVO.setShiftNo(shiftNo);
			
			log.info("END: *** getCranageShift Result *****" + shiftVO);
		} catch (Exception ex) {
			log.info("Exception getCranageShift : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCranageShift DAO");
		}
		return shiftVO;
	}

	// jp.src.ejb.sessionBeans.cab.lwms.ProcessCharges--->ProcessBillingEJB-->ChargeableBillValueObject()
	private boolean isDateHoliday(Date checkDate) throws RemoteException, BusinessException {
		String query = "SELECT holiday_dttm ";
		query += " FROM holiday ";
		query += " WHERE TRUNC(holiday_dttm) = :dateHoliday ";
		boolean isHoliday = false;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		log.info(query);
		try {
			log.info("START isDateHoliday DAO"+" checkDate:"+checkDate);
			paramMap.put("dateHoliday", new Timestamp(DateParser.getTruncDate(checkDate).getTime()));
			log.info(" *** isDateHoliday SQL *****" + query.toString());
			log.info(" *** isDateHoliday params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(query.toString(), paramMap);
			if (rs != null && rs.next()) {
				isHoliday = true;
			}
			log.info("END: *** isDateHoliday Result *****" + isHoliday);
		} catch (Exception e) {
			log.info("Exception isDateHoliday : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END isDateHoliday DAO");
		}
		return isHoliday;
	}

	// jp.src.valueObject.cab.billing--->BillAdjustParamFactory-->create()
	public BillAdjustParam create(String tariffCode) throws BusinessException, Exception {
		int ind[] = null;
		try {
			log.info("START create Process Billing DAO" +" tariffCode:"+CommonUtility.deNull(tariffCode));
			ind = billSupInfoRepo.getIndicator(tariffCode);
			log.info("END: *** create Result *****" + ind);
		} catch (BusinessException be) {
			log.info("Exception create Process Billing  : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception create Process Billing  : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END create Process Billing DAO");
		}
		return getParam(ind);
	}

	// jp.src.valueObject.cab.billing--->BillAdjustParamFactory-->getParam()
	private static BillAdjustParam getParam(int ind[]) throws BusinessException {
		log.info("START: getParam "+" ind:"+ind );
		BillAdjustParam retVal = null;
		try {
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
			log.info("END: *** getParam Result *****" + retVal);
		} catch (Exception e) {
			log.info("Exception getParam Process Billing : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getParam Process Billing DAO");
		}

		return retVal;
	}

	// jp.src.util.lwms--->UtilityBillComparatorByRefNo-->UtilityBillComparatorByRefNo()
	public class UtilityBillComparatorByRefNo implements Comparator {
		public int compare(Object arg0, Object arg1) {
			try {
				log.info("START UtilityBillComparatorByRefNo DAO");
				ChargeableBillValueObject vo1 = (ChargeableBillValueObject) arg0;
				ChargeableBillValueObject vo2 = (ChargeableBillValueObject) arg1;

				if (vo1.getRefNbr().compareTo(vo2.getRefNbr()) > 0) {
					return 1;
				} else
					return -1;
			} catch (Exception e) {
				return 0;
			} finally {
				log.info("END UtilityBillComparatorByRefNo DAO");
			}
		}

	}
	// EndRegion ProcessBillingJdbcRepository

}
