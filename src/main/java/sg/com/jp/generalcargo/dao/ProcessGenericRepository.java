package sg.com.jp.generalcargo.dao;

import java.sql.Timestamp;
import java.util.List;

import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.domain.BerthRelatedValueObject;
import sg.com.jp.generalcargo.domain.BillAdjustParam;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.CntrEventLogValueObject;
import sg.com.jp.generalcargo.domain.ContractSearchKeyValueObject;
import sg.com.jp.generalcargo.domain.ContractValueObject;
import sg.com.jp.generalcargo.domain.GstCodeValueObject;
import sg.com.jp.generalcargo.domain.TariffMainVO;
import sg.com.jp.generalcargo.domain.TariffTierBillPartyVO;
import sg.com.jp.generalcargo.domain.TempProcessChargeValueObject;
import sg.com.jp.generalcargo.domain.VesselRelatedValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.ProcessChargeException;

public interface ProcessGenericRepository {

	// StartRegion ProcessGenericRepository

	public AccountValueObject retrieveCustAcct(String custCd, String acctNbr) throws BusinessException;

	public ContractValueObject retrieveCustContract(String custCd, String acctNbr, String contractNbr,
			Timestamp varDttm) throws BusinessException;

	public int determineContractualYr(String acctNbr, String contractNbr, Timestamp varDttm) throws BusinessException;

	public ChargeableBillValueObject retrieveCustomizeChargeable(int versionNbr, String custCd, String acctNbr,
			String contractNbr, int contractualYr, ChargeableBillValueObject chargeableBillVOPubl)
			throws ProcessChargeException, BusinessException;

	public BerthRelatedValueObject retrieveBerthDttm(VesselRelatedValueObject vesselRelatedValueObject, int shiftInd,
			String tariffMainCat) throws BusinessException;

	public TariffMainVO retrieveCustomizeTariffDtls(int versionNbr, String custCd, String acctNbr, String contractNbr,
			int contractualYr, String tariffMainCatCd, String tariffSubCatCd, String tariffCd, int tierSeqNbr,
			Timestamp varDttm) throws ProcessChargeException, BusinessException;

	public int retrieveTariffVersion(Timestamp varDttm, VesselRelatedValueObject vesselRelatedValueObject,
			String tariffMainCat) throws BusinessException;

	public String getHSCode(String edoEsnAsnNbr, String type, String blNbr, String vvCode) throws BusinessException;

	public String getBusTypeForWFSCSR(BerthRelatedValueObject berthRelatedValueObject, String hsCode)
			throws BusinessException;

	public TariffMainVO retrievePublishTariffDtls(int versionNbr, String tariffMainCat, String tariffSubCat,
			String businessType, String schemeCd, String mvmt, String type, String cntrCat, String cntrSize,
			Timestamp varDttm) throws BusinessException;

	public AccountValueObject retrieveCashCustAcct() throws BusinessException;

	public Timestamp retrievePeriodicSRLastTriggerDttm(String refNbr, String refInd, String prevPSRBillDays)
			throws BusinessException;
	
	public TariffMainVO retrievePublishTariffDtls(int versionNbr, String tariffMainCat, String tariffSubCat,
			String schemeCd, String businessType, Timestamp varDttm) throws ProcessChargeException, BusinessException;

	public BillAdjustParam create(String tariffCode) throws BusinessException, Exception;
	
	public AccountValueObject retrieveCustAcct(String lineCd) throws BusinessException;

	public ContractSearchKeyValueObject retrieveCustContractExist(AccountValueObject accountValueObject,
			String idCodeInd, String idCode, Timestamp varDttm) throws BusinessException;

	public boolean determineSteelVsl(String vvCode) throws BusinessException;

	public String determineBusinessType(String cntrVslInd, BerthRelatedValueObject berthRelatedValueObject,
			String cementVslInd, boolean hasOnlySteel) throws BusinessException;

	public List<GstCodeValueObject> getGstCharge(String gstCode, Timestamp date, boolean listAll)
			throws BusinessException;

	public List<GstCodeValueObject> getGstCharge(String gstCode, Timestamp date) throws BusinessException;

	public String retrieveLineCode(String oprCd) throws BusinessException;

	public int retrieveCustTariffVersion(ContractSearchKeyValueObject contractSearchKeyValueObject, Timestamp atbDttm)
			throws BusinessException;

	public ContractValueObject retrieveCustContractByAcctNbr(AccountValueObject accountValueObject, Timestamp atbDttm)
			throws BusinessException;

	public AccountValueObject retrieveCustAcctByStatus(String custCd, String acctNbr, boolean isActiveAcct)
			throws Exception;

	public String getJ1Berths() throws BusinessException;

	public int retrieveMaxShiftInd(String vvCd) throws BusinessException;

	public boolean isCancelledVessel(String vvCd) throws BusinessException;

	public BerthRelatedValueObject getEtbBtr(String vvCd, int shiftInd) throws BusinessException;

	public List<TempProcessChargeValueObject> sortTempProcessCharge(
			List<TempProcessChargeValueObject> tempProcessChargeCollection, String tariffMainCat)
			throws BusinessException;

	public List<TariffTierBillPartyVO> retrieveTariffTierBillParty(String tariffMainCat, String tariffSubCat,
			String mvmtType, String businessType, Timestamp varDttm, String oprCd) throws BusinessException;

	public String determineVvCd(CntrEventLogValueObject cntrEventLogValueObject) throws BusinessException;

	public String retrieveDiscGateway(Integer cntrSeqNbr) throws BusinessException;

	public boolean isShutout(int cntrNbr) throws BusinessException;

	public String getLastLoadVvCd(int cntrSeqNbr) throws BusinessException;

	// EndRegion ProcessGenericRepository

}
