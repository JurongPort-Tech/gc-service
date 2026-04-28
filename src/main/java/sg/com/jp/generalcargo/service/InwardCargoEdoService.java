package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.AdpValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EdoJpBilling;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManiFestObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface InwardCargoEdoService {

	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd) throws BusinessException;

	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd, String vesselName,
			String voyageNumber) throws BusinessException;

	public VesselVoyValueObject getVesselInfo(String vv_cd) throws BusinessException;

	public TableResult getEdoList(String coCd, String strvarnbr, String strmodulecd, Criteria criteria) throws BusinessException;
	
	public TableResult getEdoListTotal(String coCd, String strvarnbr, String strmodulecd) throws BusinessException;

	public AdpValueObject getTaEndorserNmByUENNo(String uenNo) throws BusinessException; // ShutOutCargoJdbcRepository

	public AdpValueObject getAdpDetails(String adpIcTdbcrNbr) throws BusinessException;

	public List<AdpValueObject> getAdpList(String edoNbr) throws BusinessException;

	public String getCompanyName(String strNbr) throws BusinessException;

	public List<EdoJpBilling> getEdoJpBillingNbr(String strAdpNbr, String strcustcd, String strVslCd) throws BusinessException;

	public String getVesselScheme(String vvCd) throws BusinessException;

	public String getEdoNbrPkgs(String mftSeqNbr) throws BusinessException;

	public void updateVettedEdo(String stredoasnnbr, String stredostatus, String struserid) throws BusinessException;

	public List<EdoValueObjectCargo> getBLNbrList(String strVarNbr, String strScreen, String companyCode) throws BusinessException;

	public List<EdoValueObjectCargo> getBLDetails(String mftSeqNbr) throws BusinessException;

	public List<EdoValueObjectCargo> viewEdoDetails(String stredoasnnbr) throws BusinessException;

	public String getCustomerNbr(String strtDbNbr) throws BusinessException;

	public String updateEdoDetailsForDPE(String mftseqnbr, String varnbr, String adpnbr, String adpnm,
			String adpictdbcrnbr, String crgagtnbr, String crgagtnm, String agtattnbr, String agtattnm,
			String newnbrpkgs, String deliveryto, String jpbnbr, String paymode, String edostatus,
			String lastmodifyuserid, String stredoasnnbr, String caictdbcrnbr, String aaictdbcrnbr, String coCd,
			String strmodulecd, String distype, String wt, String vol, List<AdpValueObject> adpList, String taUenNo,
			String taCCode, String taNmByJP) throws BusinessException;

	public boolean getUserVesselEDO(String coCd, String asn) throws BusinessException;

	public List<String> getWHIndicator(String s2) throws BusinessException;

	public void updateWHIndicator(String s3, String s7, String s12, String s16, String s22, String s35) throws BusinessException;

	public String getSearchDetails(String s17, String s23) throws BusinessException;

	public EdoValueObjectCargo getUsedWeightVolume(String s20) throws BusinessException;

	public String insertEdoDetailsForDPE(String s1, String s2, String s6, String s8, String s7, String s10, String s11,
			String s12, String s13, String s9, String s14, String s16, String s17, String s19, String s20, String s22,
			String s25, String s18, String s27, String newWt, String newVol, List<AdpValueObject> adpList,
			String taUenNo, String taCCode, String taNmByJP, List<HsCodeDetails> multiHsCodeList) throws BusinessException;

	public boolean checkTesnExist(String stredoasnnbr) throws BusinessException;

	public boolean checkDeleteEdo(String stredoasnnbr) throws BusinessException;

	public String deleteEdoDetails(String stredoasnnbr, String userId) throws BusinessException;

	public List<String> indicationStatus(String strvarnbr) throws BusinessException;

	// START CR FTZ HSCODE - NS JULY 2024
	public List<HsCodeDetails> getHsCodeDetails(String mftSeqNbr) throws BusinessException;

	public boolean ifHsCodeExist(String mftSeqNbr)throws BusinessException;

	public boolean correctMultiHsCode(String mftSeqNbr, List<HsCodeDetails> multiHsCodeList)throws BusinessException;
	
	public boolean isShowRemainder(String newNbrPkgs, List<HsCodeDetails> multiHsCodeList) throws BusinessException;

	public List<HsCodeDetails> getEdoHsCodeDetails(String edoAsnNbr) throws BusinessException;

	public List<Map<String, String>> getOptionHscodeExisting(String mftSeqNbr) throws BusinessException;

	public List<Map<String, String>> getAllEdoHsCodeDetails(String mftSeqNbr, String edoAsnNbr) throws BusinessException;

	public boolean isMultipleHs(String mftSeqNbr) throws BusinessException;

	public String checkIfExistMultiHsMft(String mftSeqNbr) throws BusinessException;
	
	// END CR FTZ HSCODE - NS JULY 2024
	// CH - 3 --> Winstar Changes Start
	public List<EdoValueObjectCargo> getEdoByVessel(String mftSeqNbr) throws BusinessException;
	
	public Result processEdoManifestEntries(ManiFestObject request)throws BusinessException;

	// CH - 3 --> Winstar Changes End
}
