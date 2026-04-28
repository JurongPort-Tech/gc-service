package sg.com.jp.generalcargo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.EdoRepository;
import sg.com.jp.generalcargo.domain.AdpValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EdoDetails;
import sg.com.jp.generalcargo.domain.EdoJpBilling;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.EdoblnbrStatus;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManiFestObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.InwardCargoEdoService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.ConstantUtil;
@Service
public class InwardCargoEdoServiceImpl implements InwardCargoEdoService {
	
	private static final Log log = LogFactory.getLog(InwardCargoEdoServiceImpl.class);
	private String errorMessage = null;
	
	@Autowired
	EdoRepository edoRepo;

	@Override
	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd) throws BusinessException {
		return edoRepo.getVesselVoyageNbrList(strCustCode, strmodulecd);
	}

	@Override
	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd, String vesselName,
			String voyageNumber) throws BusinessException {
		return edoRepo.getVesselVoyageNbrList(strCustCode, strmodulecd, vesselName, voyageNumber);
	}

	@Override
	public VesselVoyValueObject getVesselInfo(String vv_cd) throws BusinessException {
		return edoRepo.getVesselInfo(vv_cd);
	}

	@Override
	public TableResult getEdoList(String coCd, String strvarnbr, String strmodulecd, Criteria criteria) throws BusinessException {
		return edoRepo.getEdoList(coCd, strvarnbr, strmodulecd, criteria);
	}
	
	@Override
	public TableResult getEdoListTotal(String coCd, String strvarnbr, String strmodulecd) throws BusinessException {
		return edoRepo.getEdoListTotal(coCd, strvarnbr, strmodulecd);
	}
	
	public AdpValueObject getTaEndorserNmByUENNo(String uenNo) throws BusinessException{
		return edoRepo.getTaEndorserNmByUENNo(uenNo);
	}
	
	public AdpValueObject getAdpDetails(String adpIcTdbcrNbr) throws BusinessException{
		return edoRepo.getAdpDetails(adpIcTdbcrNbr);
	}
	
	public List<AdpValueObject> getAdpList(String  edoNbr) throws BusinessException{
		return edoRepo.getAdpList(edoNbr);
	}
	public String getCompanyName(String strNbr) throws BusinessException{
		return edoRepo.getCompanyName(strNbr);
	}
	public List<EdoJpBilling> getEdoJpBillingNbr(String strAdpNbr, String strcustcd, String strVslCd) throws BusinessException{
		return edoRepo.getEdoJpBillingNbr(strAdpNbr, strcustcd, strVslCd);
	}
	public String getVesselScheme(String vvCd) throws BusinessException{
		return edoRepo.getVesselScheme(vvCd);
	}
	public String getEdoNbrPkgs(String mftSeqNbr) throws BusinessException{
		return edoRepo.getEdoNbrPkgs(mftSeqNbr);
	}
	
	@Transactional(rollbackFor = BusinessException.class)
	public void updateVettedEdo(String stredoasnnbr, String stredostatus, String struserid) throws BusinessException{
		 edoRepo.updateVettedEdo(stredoasnnbr, stredostatus, struserid);
	}
	
	public List<EdoValueObjectCargo> getBLNbrList(String strVarNbr,String strScreen, String companyCode) throws BusinessException{
		return	edoRepo.getBLNbrList(strVarNbr, strScreen, companyCode);
	}
	
	public List<EdoValueObjectCargo> getBLDetails(String mftSeqNbr)  throws BusinessException{
		return	edoRepo.getBLDetails(mftSeqNbr);
	}
	
	public List<EdoValueObjectCargo> viewEdoDetails(String stredoasnnbr) throws BusinessException{
		return	edoRepo.viewEdoDetails(stredoasnnbr);
	}
	
	public String getCustomerNbr(String strtDbNbr) throws BusinessException {
		return	edoRepo.getCustomerNbr(strtDbNbr);
	}
	
	@Transactional(rollbackFor = BusinessException.class)
	public String updateEdoDetailsForDPE(String mftseqnbr, String varnbr, String adpnbr, String adpnm,
			String adpictdbcrnbr, String crgagtnbr, String crgagtnm, String agtattnbr, String agtattnm,
			String newnbrpkgs, String deliveryto, String jpbnbr, String paymode, String edostatus,
			String lastmodifyuserid, String stredoasnnbr, String caictdbcrnbr, String aaictdbcrnbr, String coCd,
			String strmodulecd, String distype, String wt, String vol, List<AdpValueObject> adpList, String taUenNo,
			String taCCode, String taNmByJP) throws BusinessException {
		return edoRepo.updateEdoDetailsForDPE(mftseqnbr, varnbr, adpnbr, adpnm, adpictdbcrnbr, crgagtnbr, crgagtnm,
				agtattnbr, agtattnm, newnbrpkgs, deliveryto, jpbnbr, paymode, edostatus, lastmodifyuserid, stredoasnnbr,
				caictdbcrnbr, aaictdbcrnbr, coCd, strmodulecd, distype, wt, vol, adpList, taUenNo, taCCode, taNmByJP);
	}
	
	public boolean getUserVesselEDO(String coCd, String asn) throws BusinessException {
		return edoRepo.getUserVesselEDO(coCd, asn);
	}

	@Override
	public List<String> getWHIndicator(String s2) throws BusinessException {
		return edoRepo.getWHIndicator(s2);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateWHIndicator(String s3, String s7, String s12, String s16, String s22, String s35) throws BusinessException {
		edoRepo.updateWHIndicator(s3, s7, s12, s16, s22, s35);
	}

	@Override
	public String getSearchDetails(String strCustCode, String stredoasnnbr) throws BusinessException {
		return edoRepo.getSearchDetails(strCustCode, stredoasnnbr);
	}

	@Override
	public EdoValueObjectCargo getUsedWeightVolume(String s20) throws BusinessException {
		return edoRepo.getUsedWeightVolume(s20);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String insertEdoDetailsForDPE(String mftseqnbr, String varnbr, String adpnbr, String adpnm,
			String adpictdbcrnbr, String crgagtnbr, String crgagtnm, String agtattnbr, String agtattnm,
			String newnbrpkgs, String deliveryto, String jpbnbr, String paymode, String edostatus,
			String lastmodifyuserid, String caictdbcrnbr, String aaictdbcrnbr, String edocreatecd, String distype,
			String weight, String volume, List<AdpValueObject> adpList, String taUenNo, String taCCode, String taNmByJP,
			List<HsCodeDetails> multiHsCodeList) throws BusinessException {
		return edoRepo.insertEdoDetailsForDPE(mftseqnbr, varnbr, adpnbr, adpnm, adpictdbcrnbr, crgagtnbr, crgagtnm,
				agtattnbr, agtattnm, newnbrpkgs, deliveryto, jpbnbr, paymode, edostatus, lastmodifyuserid, caictdbcrnbr, aaictdbcrnbr, 
				edocreatecd, distype, weight, volume, adpList, taUenNo, taCCode, taNmByJP, multiHsCodeList);
	}

	@Override
	public boolean checkTesnExist(String stredoasnnbr) throws BusinessException {
		return edoRepo.checkTesnExist(stredoasnnbr);
	}

	@Override
	public boolean checkDeleteEdo(String stredoasnnbr) throws BusinessException {
		return edoRepo.checkDeleteEdo(stredoasnnbr);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String deleteEdoDetails(String stredoasnnbr, String userId) throws BusinessException {
		return edoRepo.deleteEdoDetails(stredoasnnbr, userId);
	}

	@Override
	public List<String> indicationStatus(String strvarnbr) throws BusinessException {
		return edoRepo.indicationStatus(strvarnbr);
	}

	@Override
	public List<HsCodeDetails> getHsCodeDetails(String mftSeqNbr) throws BusinessException {
		return edoRepo.getHsCodeDetails(mftSeqNbr);
	}

	@Override
	public boolean ifHsCodeExist(String mftSeqNbr) throws BusinessException {
		return edoRepo.ifHsCodeExist(mftSeqNbr);
	}

	@Override
	public boolean correctMultiHsCode(String mftSeqNbr, List<HsCodeDetails> multiHsCodeList) throws BusinessException {
		return edoRepo.correctMultiHsCode(mftSeqNbr, multiHsCodeList);
	}
	
	@Override
	public boolean isShowRemainder(String newNbrPkgs, List<HsCodeDetails> multiHsCodeList) throws BusinessException {
		return edoRepo.isShowRemainder(newNbrPkgs, multiHsCodeList);
	}

	@Override
	public List<HsCodeDetails> getEdoHsCodeDetails(String edoAsnNbr) throws BusinessException {
		return edoRepo.getEdoHsCodeDetails(edoAsnNbr);
	}

	@Override
	public List<Map<String, String>> getOptionHscodeExisting(String mftSeqNbr) throws BusinessException {
		return edoRepo.getOptionHscodeExisting(mftSeqNbr);
	}

	@Override
	public List<Map<String, String>> getAllEdoHsCodeDetails(String mftSeqNbr, String edoAsnNbr) throws BusinessException {
		return edoRepo.getAllEdoHsCodeDetails(mftSeqNbr, edoAsnNbr);
	}

	@Override
	public boolean isMultipleHs(String mftSeqNbr) throws BusinessException {
		return edoRepo.isMultipleHs(mftSeqNbr);
	}

	@Override
	public String checkIfExistMultiHsMft(String mftSeqNbr) throws BusinessException {
		return edoRepo.checkIfExistMultiHsMft(mftSeqNbr);
		
	}
	
	// CH - 3 --> Winstar Changes Start
	public List<EdoValueObjectCargo> getEdoByVessel(String mftSeqNbr) throws BusinessException {
		return edoRepo.getEdoByVessel(mftSeqNbr);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public Result processEdoManifestEntries(ManiFestObject request) throws BusinessException {
		errorMessage = null;
		Map<String, Object> responseMap = new HashMap<>();
		Result result = new Result();
		List<EdoDetails>          collectedEdoDetails = new ArrayList<>();
		List<EdoValueObjectCargo> cargoAsnList        = new ArrayList<>();
		List<AdpValueObject>      adpList             = new ArrayList<>();
		AdpValueObject            adpSummary          = new AdpValueObject();
		EdoValueObjectCargo       cargoSummary        = new EdoValueObjectCargo();
		int                       totalNbrPkgs        = 0;

		String action = request.getAction();
		String vslNm  = request.getVslNm();
		String vslVoy = request.getVslVoy();

		try {
			log.info("Inside insertEdoDetailEdoManifestData");

			List<EdoblnbrStatus> statusList = request.getEdoblnbrStatus();
			if (statusList == null || statusList.isEmpty()) {
				errorMessage = ConstantUtil.ErrorMsg_EdoManifestJson;
			} else {
				for (EdoblnbrStatus entry : statusList) {

					// NPE-safe ADP package count accumulation
					AdpValueObject adpVO = entry.getAdpValueObject();
					if (adpVO != null && isNumeric(adpVO.getAdpNbrPkgs())) {
						totalNbrPkgs += Integer.parseInt(adpVO.getAdpNbrPkgs());
					}

					boolean hasValidationError = validateEdoEntry(entry);
					if (hasValidationError) break;

					List<AdpValueObject> singleAdpList = buildSingleAdpList(entry);
					List<HsCodeDetails>  hsCodeList    = buildHsCodeList(entry);

					String insertedEdoId = insertEdoDetailData(entry, singleAdpList, new HashMap<>(), hsCodeList);
					if ("false".equalsIgnoreCase(insertedEdoId)) {
						errorMessage = ConstantUtil.ErrorMsg_EdoManifest;
					} else {
						List<EdoValueObjectCargo> fetched = edoRepo.viewEdoDetails(insertedEdoId);
						if (fetched != null) cargoAsnList = fetched;

						List<EdoDetails> edoDetailsAsn = edoRepo.viewEdoDetailsAsn(insertedEdoId);
						if (edoDetailsAsn != null && !edoDetailsAsn.isEmpty()) {
							collectedEdoDetails.addAll(edoDetailsAsn);
						}

						List<AdpValueObject> fetchedAdp = edoRepo.getAdpList(insertedEdoId);
						if (fetchedAdp != null) adpList = fetchedAdp;
					}
				}
			}

			if (!adpList.isEmpty()) {
				adpSummary = buildAdpSummary(adpList, totalNbrPkgs);
			}
			if (!cargoAsnList.isEmpty()) {
				cargoSummary = buildCargoSummary(cargoAsnList);
			}

			responseMap.put("adpObject",        adpSummary);
			responseMap.put("edoDetailsStatus", cargoSummary);
			responseMap.put("edoDetails",       collectedEdoDetails);
			responseMap.put("action",           action);
			responseMap.put("vslNm",            vslNm);
			responseMap.put("vslVoy",           vslVoy);
			responseMap.put("strmsg",           "Successfully added");

		} catch (Exception e) {
			log.info("Exception in insertEdoDetailEdoManifestData: ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
			throw new BusinessException(errorMessage);
		} finally {
			if (errorMessage != null && !errorMessage.isEmpty()) {
				responseMap.put("errorMessage", errorMessage);
				result.setError(errorMessage);
				result.setErrors(responseMap);
				result.setSuccess(false);
			} else {
				result.setData(responseMap);
				result.setSuccess(true);
			}
			log.info("End insertEdoDetailEdoManifestData result: {}" + result);
		}
		return result;
	}

// ─── Validation ────────────────────────────────────────────────────────────────

	private boolean validateEdoEntry(EdoblnbrStatus entry) throws BusinessException {

		// NPE: getNewnbrpkgs() null or non-numeric
		String rawNbrPkgs = entry.getNewnbrpkgs();
		if (!isNumeric(rawNbrPkgs) || Integer.parseInt(rawNbrPkgs) == 0) {
			errorMessage = ConstantUtil.ErrorMsg_Edo_003;
			return true;
		}

		// NPE: getNewWt() null or non-numeric
		String rawWt = entry.getNewWt();
		if (!isNumeric(rawWt) || Double.parseDouble(rawWt) == 0) {
			errorMessage = ConstantUtil.ErrorMsg_Edo_004;
			return true;
		}

		// NPE: getNewVol() null or non-numeric
		String rawVol = entry.getNewVol();
		if (!isNumeric(rawVol) || Double.parseDouble(rawVol) == 0) {
			errorMessage = ConstantUtil.ErrorMsg_Edo_005;
			return true;
		}

		String blNbr = entry.getBlnbr();
		if (blNbr == null || blNbr.trim().isEmpty()) {
			errorMessage = ConstantUtil.ErrorMsg_Edo_003;
			return true;
		}

		// NPE: repo may return null string for getEdoNbrPkgs
		String rawAvailablePkgs = edoRepo.getEdoNbrPkgs(blNbr);
		if (!isNumeric(rawAvailablePkgs)) {
			errorMessage = ConstantUtil.ErrorMsg_Edo_003;
			return true;
		}
		int availablePkgs = Integer.parseInt(rawAvailablePkgs);

		// NPE: repo may return null object or null fields
		EdoValueObjectCargo usedWeightVol = edoRepo.getUsedWeightVolume(blNbr);
		if (usedWeightVol == null
				|| !isNumeric(usedWeightVol.getNomVolume())
				|| !isNumeric(usedWeightVol.getNomWeight())) {
			errorMessage = ConstantUtil.ErrorMsg_Edo_006 + "N/A";
			return true;
		}

		double inputVol  = Double.parseDouble(rawVol);
		double inputWt   = Double.parseDouble(rawWt);
		double maxVol    = Double.parseDouble(usedWeightVol.getNomVolume());
		double maxWt     = Double.parseDouble(usedWeightVol.getNomWeight());
		int    nbrPkgs   = Integer.parseInt(rawNbrPkgs);

		log.info("Validating — inputVol:{} maxVol:{} inputWt:{} maxWt:{}"+ inputVol +  maxVol +  inputWt +  maxWt);

		if (inputVol > maxVol)       { errorMessage = ConstantUtil.ErrorMsg_Edo_006 + maxVol; return true; }
		if (inputWt  > maxWt)        { errorMessage = ConstantUtil.ErrorMsg_Edo_007 + maxWt;  return true; }
		if (availablePkgs < nbrPkgs) { errorMessage = ConstantUtil.ErrorMsg_Edo_003;          return true; }

		return false;
	}

// ─── List Builders ─────────────────────────────────────────────────────────────

	private List<AdpValueObject> buildSingleAdpList(EdoblnbrStatus entry) {
		AdpValueObject adp = new AdpValueObject();
		// NPE: getAdpValueObject() could be null
		AdpValueObject src = entry.getAdpValueObject();
		if (src != null) {
			adp.setAdpNm(src.getAdpNm());
			adp.setAdpIcTdbcrNbr(src.getAdpIcTdbcrNbr());
			adp.setAdpContact(src.getAdpContact());
			adp.setAdpCustCd(src.getAdpCustCd());
			adp.setAdpNbrPkgs(src.getAdpNbrPkgs());
		}
		return Collections.singletonList(adp);
	}

	private List<HsCodeDetails> buildHsCodeList(EdoblnbrStatus entry) throws BusinessException {
		// NPE: getHsCodeSize() null or non-numeric
		if (!isNumeric(entry.getHsCodeSize())) return Collections.emptyList();
		int hsCodeSize = Integer.parseInt(entry.getHsCodeSize());
		if (hsCodeSize == 0) return Collections.emptyList();

		// NPE: getHsCodeDetails() could be null
		List<HsCodeDetails> srcList = entry.getHsCodeDetails();
		if (srcList == null || srcList.isEmpty()) return Collections.emptyList();

		String blNbr = entry.getBlnbr(); // already validated non-null before this is called
		List<HsCodeDetails> result = new ArrayList<>(hsCodeSize);

		for (int i = 0; i < Math.min(hsCodeSize, srcList.size()); i++) {
			HsCodeDetails src  = srcList.get(i);
			if (src == null) continue;                          // NPE: skip null entries in list
			HsCodeDetails dest = new HsCodeDetails();
			dest.setHsCode(src.getHsCode());
			dest.setNbrPkgs(src.getNbrPkgs());
			dest.setGrossWt(src.getGrossWt());
			dest.setHsSubCodeFr(src.getHsSubCodeFr());
			dest.setHsSubCodeTo(src.getHsSubCodeTo());
			dest.setGrossVol(src.getGrossVol());
			dest.setHscodeSeqNbr(src.getHscodeSeqNbr() != null
					? src.getHscodeSeqNbr()
					: edoRepo.checkIfExistMultiHsMft(blNbr));
			dest.setCustomHsCode(src.getCustomHsCode());
			result.add(dest);
		}
		return result;
	}

// ─── Summary Builders ──────────────────────────────────────────────────────────

	private AdpValueObject buildAdpSummary(List<AdpValueObject> adpList, int totalNbrPkgs) {
		AdpValueObject summary = new AdpValueObject();
		AdpValueObject first   = adpList.get(0);      // caller guarantees non-empty
		summary.setAdpContact(first.getAdpContact());
		summary.setAdpCustCd(first.getAdpCustCd());
		summary.setAdpNbrPkgs(String.valueOf(totalNbrPkgs));
		summary.setAdpIcTdbcrNbr(first.getAdpIcTdbcrNbr());
		summary.setAdpNm(first.getAdpNm());
		return summary;
	}

	private EdoValueObjectCargo buildCargoSummary(List<EdoValueObjectCargo> cargoList) {
		EdoValueObjectCargo summary = new EdoValueObjectCargo();
		EdoValueObjectCargo first   = cargoList.get(0); // caller guarantees non-empty
		summary.setAcctNbr(first.getAcctNbr());
		summary.setAcct_nm(first.getAcct_nm());
		summary.setMftSeqNbr(first.getMftSeqNbr());
		summary.setCrgAgtNm(first.getCrgAgtNm());
		summary.setAgtAttNm(first.getAgtAttNm());
		summary.setCrgAgtNbr(first.getCrgAgtNbr());
		summary.setAgtAttNbr(first.getAgtAttNbr());
		summary.setDeliveryTo(first.getDeliveryTo());
		summary.setDisOprInd(first.getDisOprInd());
		summary.setTaUenNo(first.getTaUenNo());
		summary.setTaNmByJP(first.getTaNmByJP());
		summary.setTaCCode(first.getTaCCode());
		return summary;
	}

// ─── insertEdoDetailData ───────────────────────────────────────────────────────

	public String insertEdoDetailData(EdoblnbrStatus entry, List<AdpValueObject> adpList,
									  Map<String, Object> map, List<HsCodeDetails> hsCodeList)
			throws BusinessException {
		String resultEdoId = "false";
		try {
			log.info("Start insertEdoDetailData — entry:{} adpList:{}" +  entry + " " + adpList);

			String blNbr          = nullToEmpty(entry.getBlnbr());
			String varNbr         = nullToEmpty(entry.getVarnbr());
			String newWt          = nullToEmpty(entry.getNewWt());
			String newVol         = nullToEmpty(entry.getNewVol());
			String adpNbr         = nullToEmpty(entry.getAdpnbr());
			String adpNmStatus    = nullToEmpty(entry.getAdpnmstatus());
			String adpNm          = nullToEmpty(entry.getAdpnm());
			String nbrPkgs        = nullToEmpty(entry.getNewnbrpkgs());
			String crgAgtNm       = nullToEmpty(entry.getCrgagtnm());
			String agtAttNm       = nullToEmpty(entry.getAgtattnm());
			String deliveryTo     = nullToEmpty(entry.getDeliveryto(), "O");
			String jpbNbr         = nullToEmpty(entry.getJpbnbr());
			String edoStatus      = nullToEmpty(entry.getEdostatus());
			String crgAgtNbr      = nullToEmpty(entry.getCrgagtnbr());
			String crgAgtNmStatus = nullToEmpty(entry.getCrgagtnmstatus());
			String agtAttNbr      = nullToEmpty(entry.getAgtattnbr());
			String agtAttNmStatus = nullToEmpty(entry.getAgtattnmstatus());
			String disType        = nullToEmpty(entry.getDistype());

			// NPE: getCompanyCode()/getUserAccount() null -> .isEmpty() would throw
			String companyCode = nullToEmpty(entry.getCompanyCode());
			String userAccount = nullToEmpty(entry.getUserAccount());
			companyCode = companyCode.isEmpty() ? "JP"     : companyCode;
			userAccount = userAccount.isEmpty() ? "SYSTEM" : userAccount;

			// NPE: log before use — safe now because varNbr is already null-safe
			log.info("varNbr resolved: {}"+  varNbr);

			// Resolve ADP numbers (NEW = keep as-is, OLD = fetch from DB)
			String resolvedAdpNbr = "";
			String newAdpNbr      = "";
			if ("NEW".equalsIgnoreCase(adpNmStatus)) {
				newAdpNbr = adpNbr;
			} else if ("OLD".equalsIgnoreCase(adpNmStatus)) {
				newAdpNbr     = adpNbr;
				resolvedAdpNbr = nullToEmpty(edoRepo.getCustomerNbr(adpNbr)); // NPE: repo may return null
				adpNbr         = resolvedAdpNbr;
			}

			// Resolve cargo agent numbers
			String resolvedCrgAgtNbr = "";
			String newCrgAgtNbr      = "";
			if ("NEW".equalsIgnoreCase(crgAgtNmStatus)) {
				newCrgAgtNbr = crgAgtNbr;
			} else if ("OLD".equalsIgnoreCase(crgAgtNmStatus)) {
				newCrgAgtNbr      = crgAgtNbr;
				resolvedCrgAgtNbr = nullToEmpty(edoRepo.getCustomerNbr(crgAgtNbr)); // NPE: repo may return null
				crgAgtNbr          = resolvedCrgAgtNbr;
			}

			// Resolve agent attendant numbers
			String resolvedAgtAttNbr = "";
			String newAgtAttNbr      = "";
			if ("NEW".equalsIgnoreCase(agtAttNmStatus)) {
				newAgtAttNbr = agtAttNbr;
			} else if ("OLD".equalsIgnoreCase(agtAttNmStatus)) {
				newAgtAttNbr      = agtAttNbr;
				resolvedAgtAttNbr = nullToEmpty(edoRepo.getCustomerNbr(agtAttNbr)); // NPE: repo may return null
				agtAttNbr          = resolvedAgtAttNbr;
			}

			String paymentType = "cash".equalsIgnoreCase(jpbNbr) ? "C" : "A";
			String taUenNo     = nullToEmpty(entry.getTaUenNo());
			String taCCode     = nullToEmpty(entry.getTaCCode());
			String taNmByJP    = nullToEmpty(entry.getTaEndorser());

			resultEdoId = edoRepo.insertEdoDetailsForDPE(
					blNbr, varNbr, resolvedAdpNbr, adpNm, newAdpNbr,
					resolvedCrgAgtNbr, crgAgtNm, resolvedAgtAttNbr, agtAttNm,
					nbrPkgs, deliveryTo, jpbNbr, paymentType, edoStatus,
					userAccount, newCrgAgtNbr, newAgtAttNbr, companyCode,
					disType, newWt, newVol, adpList, taUenNo, taCCode, taNmByJP, hsCodeList
			);

		} catch (BusinessException e) {
			log.info("BusinessException in insertEdoDetailData: "+ e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception in insertEdoDetailData: "+ e);
		} finally {
			log.info("End insertEdoDetailData, resultEdoId: {}"+ resultEdoId);
		}
		return resultEdoId;
	}

// ─── Utilities ─────────────────────────────────────────────────────────────────

	/** Null-safe empty string fallback. */
	private String nullToEmpty(String value) {
		return value != null ? value : "";
	}

	/** Null-safe empty string fallback with explicit default. */
	private String nullToEmpty(String value, String defaultValue) {
		return (value != null && !value.isEmpty()) ? value : defaultValue;
	}

	/** Returns true only if the string is non-null, non-empty, and parseable as a number. */
	private boolean isNumeric(String value) {
		if (value == null || value.trim().isEmpty()) return false;
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	// CH - 3 --> Winstar Changes End

	
}
