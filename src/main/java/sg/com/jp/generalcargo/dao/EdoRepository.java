package sg.com.jp.generalcargo.dao;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.AdpValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPECargo;
import sg.com.jp.generalcargo.domain.EdoDetails;
import sg.com.jp.generalcargo.domain.EdoJpBilling;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.domain.vesselVoyObjectValue;
import sg.com.jp.generalcargo.util.BusinessException;

public interface EdoRepository {
	List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd) throws BusinessException;

	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd, String vesselName,
			String voyageNumber) throws BusinessException;

	VesselVoyValueObject getVesselInfo(String vv_cd) throws BusinessException;

	public List<EdoValueObjectCargo> viewEdoDetails(String stredoasnnbr) throws BusinessException;

	public List<AdpValueObject> getAdpList(String edoNbr) throws BusinessException;

	public AdpValueObject getTaEndorserNmByUENNo(String uenNo) throws BusinessException;

	public AdpValueObject getAdpDetails(String adpIcTdbcrNbr) throws BusinessException;

	public String getCompanyName(String strNbr) throws BusinessException;

	public String getVesselScheme(String vvCd) throws BusinessException;

	public String getEdoNbrPkgs(String mftSeqNbr) throws BusinessException;

	public void updateVettedEdo(String stredoasnnbr, String stredostatus, String struserid) throws BusinessException;

	public List<EdoValueObjectCargo> getBLNbrList(String strVarNbr, String strScreen, String companyCode)
			throws BusinessException;

	public List<EdoValueObjectCargo> getBLDetails(String mftSeqNbr) throws BusinessException;

	public String getCustomerNbr(String strtDbNbr) throws BusinessException;

	public String updateEdoDetailsForDPE(String mftseqnbr, String varnbr, String adpnbr, String adpnm,
			String adpictdbcrnbr, String crgagtnbr, String crgagtnm, String agtattnbr, String agtattnm,
			String newnbrpkgs, String deliveryto, String jpbnbr, String paymode, String edostatus,
			String lastmodifyuserid, String stredoasnnbr, String caictdbcrnbr, String aaictdbcrnbr, String coCd,
			String strmodulecd, String distype, String wt, String vol, List<AdpValueObject> adpList, String taUenNo,
			String taCCode, String taNmByJP) throws BusinessException;

	public List<EdoJpBilling> getEdoJpBillingNbr(String strAdpNbr, String strcustcd, String strVslCd)
			throws BusinessException;

	public boolean getUserVesselEDO(String coCd, String asn) throws BusinessException;

	// ejb.sessionBeans.gbms.cargo.edo -->EdoEjb -->deleteBEdoDetails()
	public TableResult getEdoList(String coCd, String strvarnbr, String strmodulecd, Criteria criteria)
			throws BusinessException;

	public TableResult getEdoListTotal(String coCd, String strvarnbr, String strmodulecd) throws BusinessException;

	// ejb.sessionBeans.gbms.cargo.edo-->EdoEjb-->updateWHIndicator
	public void updateWHIndicator(String stredoasnnbr, String strWhInd, String strWhAggrNbr, String strWhRemarks,
			String strFreeStgDays, String struserid) throws BusinessException;

	public String getSearchDetails(String strCustCode, String stredoasnnbr) throws BusinessException;

	public AdpValueObject getTaEndorserNmByUENNo(Criteria criteria);

	public String getVslStatus(String varno) throws BusinessException;

	public DPECargo loadGeneralShutoutCargoByEDO(String edo_asn_nbr, String trans_type) throws BusinessException; //

	public boolean checkAccountNbr(String accNbr) throws BusinessException;

	public String updateShutoutEdo(EdoValueObjectCargo edo, String userId) throws BusinessException;

	public String insertShutoutEdoForDPE(EdoValueObjectCargo edo, String userId) throws BusinessException;

	public String deleteShutoutEdoDetails(String stredoasnnbr, String struserid) throws BusinessException;

	public List<String> getWHIndicator(String edoasnnbr) throws BusinessException;

	public EdoValueObjectCargo getUsedWeightVolume(String mftseqnbr) throws BusinessException;

	public String insertEdoDetailsForDPE(String mftseqnbr, String varnbr, String adpnbr, String adpnm,
			String adpictdbcrnbr, String crgagtnbr, String crgagtnm, String agtattnbr, String agtattnm,
			String newnbrpkgs, String deliveryto, String jpbnbr, String paymode, String edostatus,
			String lastmodifyuserid, String caictdbcrnbr, String aaictdbcrnbr, String edocreatecd, String distype,
			String weight, String volume, List<AdpValueObject> adpList, String taUenNo, String taCCode, String taNmByJP, 
			List<HsCodeDetails> multiHsCodeList)
			throws BusinessException;

	public boolean checkTesnExist(String edoEsnno) throws BusinessException;

	public boolean checkDeleteEdo(String edoEsnno) throws BusinessException;

	public String deleteEdoDetails(String stredoasnnbr, String struserid) throws BusinessException;

	public List<vesselVoyObjectValue> getVslVoyNbrList(String coCd, String strmodulecd, String search)
			throws BusinessException;

	public TableResult getEdoLst(String coCd, String strvarnbr, String strmodulecd, Criteria criteria)
			throws BusinessException;
	
	public List<EdoValueObjectCargo> getOutStandingList(String vvCd) throws BusinessException;
	
	public List<List<String>> getEsnList(String vvCd) throws BusinessException;
	
	public List<EdoValueObjectCargo> getShutoutAddDetail(String esnAsnNo) throws BusinessException;

	public List<EdoValueObjectCargo> getShutoutEdoDetail(String edoAsnNbr) throws BusinessException;

	public TableResult getShutoutEdoList(String strVarNbr, Criteria criteria) throws BusinessException;

	public List<EdoValueObjectCargo> getShutoutVesselList(String vesselName, String voyageNumber) throws BusinessException;

	public List<EdoValueObjectCargo> getShutoutVesselVoyageNbrList() throws BusinessException;

	public List<String> indicationStatus(String strvarnbr) throws BusinessException;

	// START CR FTZ HSCODE - NS JULY 2024
	public List<HsCodeDetails> getHsCodeDetails(String mftSeqNbr)throws BusinessException;

	public boolean ifHsCodeExist(String mftSeqNbr) throws BusinessException;

	public boolean correctMultiHsCode(String mftSeqNbr, List<HsCodeDetails> multiHsCodeList) throws BusinessException;
	
	public boolean isShowRemainder(String newNbrPkgs, List<HsCodeDetails> multiHsCodeList) throws BusinessException;
	
	public List<HsCodeDetails> getEdoHsCodeDetails(String edoAsnNbr) throws BusinessException;

	public List<Map<String, String>> getOptionHscodeExisting(String mftSeqNbr) throws BusinessException;

	public List<Map<String, String>> getAllEdoHsCodeDetails(String mftSeqNbr, String edoAsnNbr) throws BusinessException;

	public boolean isMultipleHs(String mftSeqNbr) throws BusinessException;

	public String checkIfExistMultiHsMft(String mftSeqNbr) throws BusinessException;

	//END CR FTZ HSCODE - NS JULY 2024
	
	// CH - 3 --> Winstar Changes Start
	public List<EdoValueObjectCargo> getEdoByVessel(String mftSeqNbr) throws BusinessException;
	
	public List<EdoDetails> viewEdoDetailsAsn(String stredoasnnbr) throws BusinessException;

	// CH - 3 --> Winstar Changes End
}
