package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.UpdateEsnRequest;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.CargoAmendmentService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = GeneralCargoAmendController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GeneralCargoAmendController {
	public static final String ENDPOINT = "gc/cargoAmendment";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(GeneralCargoAmendController.class);
	private static boolean isAccountContain = false;
	private static boolean isUserAccountContain = false;
	@Autowired
	private CargoAmendmentService cargoAmendmentService;

	// StartRegion General Cargo Amendment
	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->getRecord()
	@PostMapping(value = "/getRecord")
	public ResponseEntity<?> getRecord(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		ManifestValueObject manifestValueObject = new ManifestValueObject();
		EsnListValueObject esnListValueObject = new EsnListValueObject();
		EsnListValueObject cntrValueObject = new EsnListValueObject();
		List<VesselVoyValueObject> esnSearch = new ArrayList<VesselVoyValueObject>();
		List<EsnListValueObject> esnDetails = new ArrayList<EsnListValueObject>();
		String msg = "";
		String vesselStatus = "";
		DPEUtil dpeUtil = new DPEUtil();
		DPEUtil dpeAccount = new DPEUtil();
		Criteria criteria = CommonUtil.getCriteria(request);
		boolean isJPStaff = StringUtils.equalsIgnoreCase(
				CommonUtility.deNull(criteria.getPredicates().get("companyCode")).trim(), "JP") ? Boolean.TRUE
						: Boolean.FALSE;
		boolean isCloseBJ = false;
		boolean isEdit = false;
		boolean bdnbrpkgs = false;
		boolean isBeforeDN = true;
		boolean isBeforeUA = true;
		boolean isMftClose = false;
		isUserAccountContain=false;
		isAccountContain=false;
		errorMessage="";
		try {
			log.info("** getRecord Start criteria :" + criteria.toString());
			String asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("asn_nbr")).trim();
			String custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode")).trim();
			dpeUtil = cargoAmendmentService.getDiscargingCargo(asn_nbr);
			map.put("companyCode", custCd);
			map.put("asn_nbr", asn_nbr);

			if (dpeUtil != null) {
				vesselStatus = dpeUtil.getVv_status_ind();
				if (!isJPStaff) {
					if (StringUtils.equalsIgnoreCase(vesselStatus, "CL")) {
						msg = ConstantUtil.ErrorMsg_Vessel_Was_Close;
						errorMessage= ConstantUtil.ErrorMsg_Vessel_Was_Close;
						map.put("msg", msg);
						throw new BusinessException(errorMessage);
					}
				}

				List<VesselVoyValueObject> listVessel = cargoAmendmentService.getVesselVoyList(custCd, dpeUtil.getVsl_nm(),
						dpeUtil.getIn_voy_nbr(), dpeUtil.getTerminal());
				if (listVessel == null || listVessel.size() == 0) {
					msg = ConstantUtil.ErrorMsg_Invalid_vsl_voy;
					map.put("msg", msg);
				} else {
					log.info("GeneralCargoAmendAction :: get manifestValueObject : START ");
					manifestValueObject = cargoAmendmentService.mftRetrieve(dpeUtil.getBl_nbr(), dpeUtil.getVar_nbr(),
							dpeUtil.getMft_seq_nbr());

					if (manifestValueObject != null) {
						isMftClose = cargoAmendmentService.isManClose(dpeUtil.getVar_nbr());
						manifestValueObject.setVarNbr(dpeUtil.getVar_nbr());
						manifestValueObject.setBlNo(dpeUtil.getBl_nbr());
						manifestValueObject.setCategory(dpeUtil.getCc_name());
						manifestValueObject.setSeqNo(dpeUtil.getMft_seq_nbr());
						isCloseBJ = StringUtils.equalsIgnoreCase("Y", dpeUtil.getGb_close_bj_ind());
						bdnbrpkgs = dpeUtil.getDn_nbr_pkgs() > 0;
						Double maxCargoTon = dpeUtil.getMaxCargoTon() * 1000;
						if (bdnbrpkgs) {
							isBeforeDN = false;
						} else {
							isBeforeDN = true;
						}
						if (isCloseBJ || (!isJPStaff && isMftClose) || !isBeforeDN) {
							isEdit = false;
						} else {
							isEdit = true;
						}
						List<DPEUtil> listAccount = (List<DPEUtil>) cargoAmendmentService
								.listAcount(dpeUtil.getVar_nbr());
						if (listAccount != null && listAccount.size() > 0) {
							dpeAccount.setAcct_nbr(listAccount.get(0).getAcct_nbr());
							dpeAccount.setCust_cd(listAccount.get(0).getCust_cd());
						}
						map.put("hiddenCrgCategory", false);
						map.put("dpeUtil", dpeUtil);
						map.put("maxCargoTon", maxCargoTon);
						map.put("manifest", manifestValueObject);
						map.put("isEdit", isEdit);
						map.put("isJPStaff", isJPStaff);
						map.put("dpeAccount", dpeAccount);
						map.put("vslType", dpeUtil.getVsl_type_cd());				
						
						List<HsCodeDetails> hscodeDetailsList = cargoAmendmentService.getHsCodeDetailList(dpeUtil.getMft_seq_nbr());
						map.put("hscodeDetailsList", hscodeDetailsList);
						
						log.info("GeneralCargoAmendAction :: get manifestValueObject : END ");
					}
				}
			} else {
				esnSearch = cargoAmendmentService.getVesselListSearch(custCd, asn_nbr);
				esnDetails = cargoAmendmentService.getEsnDetails(asn_nbr, custCd);
				
				List<HsCodeDetails> hscodeDetailsList = cargoAmendmentService.getHsCodeEsnDetailList(asn_nbr);
				map.put("hscodeDetailsList", hscodeDetailsList);

				if (esnSearch.size() == 0 || esnDetails.size() == 0) {
					msg = ConstantUtil.ErrorMsg_Esn_Not_Found;
					map.put("msg", msg);
					errorMessage= ConstantUtil.ErrorMsg_Esn_Not_Found;
					throw new BusinessException(errorMessage);
				}

				if (esnDetails != null && esnDetails.size() > 0) {
					esnListValueObject = (EsnListValueObject) esnDetails.get(0);
					dpeUtil = cargoAmendmentService.getEsnVessel(asn_nbr);
					vesselStatus = dpeUtil.getVv_status_ind();
					if (!isJPStaff) {
						if (StringUtils.equalsIgnoreCase(vesselStatus, "CL")) {
							msg = ConstantUtil.ErrorMsg_Vessel_Was_Close;
							map.put("msg", msg);
							errorMessage= ConstantUtil.ErrorMsg_Vessel_Was_Close;
							throw new BusinessException(errorMessage);
						}
					}
					isBeforeUA = !(esnListValueObject.getUaNoofPkgs() > 0);
					List<TruckerValueObject> truckerList = cargoAmendmentService.getTruckerList(asn_nbr);
					String hsSubCode = "";
					String hsSubCodeDesc = "";
					String hsSubCodefr = esnListValueObject.getHsSubCodeFr();
					if (StringUtils.isNotBlank(hsSubCodefr)) {
						hsSubCode = hsSubCodefr + "-" + esnListValueObject.getHsSubCodeTo();
					}
					if (StringUtils.isNotBlank(hsSubCode)) {
						DPEUtil util = (DPEUtil) cargoAmendmentService.getHsSubCodeDesc(esnListValueObject.getHsCode(),
								hsSubCode);
						if (util != null) {
							hsSubCodeDesc = util.getHs_sub_desc();
						}

					}
					esnListValueObject.setTerminal(dpeUtil.getTerminal());
					esnListValueObject.setScheme(dpeUtil.getScheme());
					dpeUtil.setEsn_asn_nbr(asn_nbr);
					dpeUtil.setHs_sub_desc(hsSubCodeDesc);
					List<EsnListValueObject> vector5 = cargoAmendmentService.getCntrDetails(asn_nbr);
					if (vector5 != null && vector5.size() > 0) {
						cntrValueObject = (EsnListValueObject) vector5.get(0);
					}
					String accountNo = esnListValueObject.getAccNo();
					String truckNo = esnListValueObject.getTruckerNo();
					String bookRefNo = esnListValueObject.getBookingRefNo();
					getListAccount(truckNo, bookRefNo, accountNo, custCd);
					boolean isOtherAccount = true;
					if (!isAccountContain && !isUserAccountContain || StringUtils.equals("CASH", accountNo)) {
						isOtherAccount = true;
					} else {
						isOtherAccount = false;
					}
					if (isJPStaff) {
						if (StringUtils.equalsIgnoreCase(vesselStatus, "UB")
								|| StringUtils.equalsIgnoreCase(vesselStatus, "CL")) {
							isEdit = false;
						} else {
							isEdit = true;
						}
					} else {
						if (StringUtils.equalsIgnoreCase("Y", dpeUtil.getGb_close_shp_ind()) || !isBeforeUA) {
							isEdit = false;
						} else {
							isEdit = true;
						}

					}
					List<BookingReferenceValueObject> BKDetails = cargoAmendmentService.fetchBKDetails(esnListValueObject.getBookingRefNo());
					BookingReferenceValueObject bookingReferenceValueObjectDis = new BookingReferenceValueObject();
					bookingReferenceValueObjectDis =  BKDetails.get(0);
					String cargoTypeCode = bookingReferenceValueObjectDis.getCargoType();
					String cargoTypeName = cargoAmendmentService.getCrgNm(cargoTypeCode);
					map.put("currentCrgType", cargoTypeCode);
					map.put("crgName", cargoTypeName);
					map.put("hiddenCrgCategory", false);
					map.put("isAccountContain", isAccountContain);
					map.put("isUserAccountContain", isUserAccountContain);
					map.put("isOtherAccount", isOtherAccount);
					map.put("esn", esnListValueObject);
					map.put("dpeUtil", dpeUtil);
					map.put("truckerList", truckerList);
					map.put("cntr", cntrValueObject);
					map.put("isEdit", isEdit);
					map.put("vslType", dpeUtil.getVsl_type_cd());
				} else {
					msg = ConstantUtil.ErrorMsg_Cannot_Get_Cargo;
					map.put("msg", msg);
					errorMessage= ConstantUtil.ErrorMsg_Cannot_Get_Cargo;
				}
			}
			
		} catch (BusinessException e) {
			log.info("Exception getRecord : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception getRecord : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setSuccess(true);
				result.setData(map);
			}
			log.info("getRecord Result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// ArasuController
	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->updateManifest()
	@PostMapping(value = "/updateManifest")
	public ResponseEntity<?> updateManifest(HttpServletRequest request) throws BusinessException {
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		Result result = new Result();
		DPEUtil dpeUtil = new DPEUtil();
		boolean isApplicableCargoCategory = false;
		String mftUpdateDetail;
		isUserAccountContain=false;
		isAccountContain=false;
		errorMessage="";
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** updateManifest Start criteria :" + criteria.toString());

			String usrid = CommonUtility.deNull(criteria.getPredicates().get("userAccount")).trim();
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode")).trim();
			String seqno = CommonUtility.deNull(criteria.getPredicates().get("seqno")).trim();
			String varno = CommonUtility.deNull(criteria.getPredicates().get("var_nbr")).trim();
			String blno = CommonUtility.deNull(criteria.getPredicates().get("blNo")).trim();
			String crgtyp = CommonUtility.deNull(criteria.getPredicates().get("crgType")).trim();
			String hscd = CommonUtility.deNull(criteria.getPredicates().get("hsCode")).trim();
			String crgdesc = CommonUtility.deNull(criteria.getPredicates().get("crgDesc")).trim();
			String mark = CommonUtility.deNull(criteria.getPredicates().get("crgMarking")).trim();
			String nopkgs = CommonUtility.deNull(criteria.getPredicates().get("noofPkgs")).trim();
			String gwt = CommonUtility.deNull(criteria.getPredicates().get("grWt")).trim();
			String gvol = CommonUtility.deNull(criteria.getPredicates().get("grMsmt")).trim();
			String crgstat = CommonUtility.deNull(criteria.getPredicates().get("crgStatus")).trim();
			String dgind = CommonUtility.deNull(criteria.getPredicates().get("dgInd")).trim();
			String stgind = CommonUtility.deNull(criteria.getPredicates().get("stgInd")).trim();
			String dop = CommonUtility.deNull(criteria.getPredicates().get("opInd")).trim();
			String pkgtyp = CommonUtility.deNull(criteria.getPredicates().get("pkgType")).trim();
			String coname = CommonUtility.deNull(criteria.getPredicates().get("consignee")).trim();
			String poL = CommonUtility.deNull(criteria.getPredicates().get("portL")).trim();
			String poD = CommonUtility.deNull(criteria.getPredicates().get("portD")).trim();
			String poFD = CommonUtility.deNull(criteria.getPredicates().get("portFD")).trim();
			String cntrtype = CommonUtility.deNull(criteria.getPredicates().get("cntrType")).trim();
			String cntrsize = CommonUtility.deNull(criteria.getPredicates().get("cntrSize")).trim();
			String cntr1 = CommonUtility.deNull(criteria.getPredicates().get("cntr1")).trim();
			String cntr2 = CommonUtility.deNull(criteria.getPredicates().get("cntr2")).trim();
			String cntr3 = CommonUtility.deNull(criteria.getPredicates().get("cntr3")).trim();
			String cntr4 = CommonUtility.deNull(criteria.getPredicates().get("cntr4")).trim();
			String autParty = CommonUtility.deNull(criteria.getPredicates().get("createCustCd")).trim();
			String adviseBy = CommonUtility.deNull(criteria.getPredicates().get("adviseBy")).trim();
			String adviseDate = CommonUtility.deNull(criteria.getPredicates().get("adviseDate")).trim();
			String adviseMode = CommonUtility.deNull(criteria.getPredicates().get("advisemode")).trim();
			String amendChargedTo = CommonUtility.deNull(criteria.getPredicates().get("amendChargedTo")).trim();
			String waiveCharge = CommonUtility.deNull(criteria.getPredicates().get("waiveCharge")).trim();
			String waiveReason = CommonUtility.deNull(criteria.getPredicates().get("waiveReason")).trim();
			String category = CommonUtility.deNull(criteria.getPredicates().get("category")).trim();
			String hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hs_sub_code_fr")).trim();
			String hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hs_sub_code_to")).trim();
			String consigneeCoyCode = CommonUtility.deNull(criteria.getPredicates().get("consigneeCoyCode")).trim();
			// BEGIN added by Maksym JCMS Smart CR 6.10
			String asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("asn_nbr")).trim();
			
			// START CR FTZ HSCODE - NS JULY 2024
			
			String customHsCode = CommonUtility.deNull(criteria.getPredicates().get("customHsCode")).trim();
			String conAddr = CommonUtility.deNull(criteria.getPredicates().get("conAddr")).trim();
			String shipperNm = CommonUtility.deNull(criteria.getPredicates().get("shipperNm")).trim();
			String shipperAddr = CommonUtility.deNull(criteria.getPredicates().get("shipperAddr")).trim();
			String notifyParty = CommonUtility.deNull(criteria.getPredicates().get("notifyParty")).trim();
			String notifyPartyAddr = CommonUtility.deNull(criteria.getPredicates().get("notifyPartyAddr")).trim();
			String placeofDelivery = CommonUtility.deNull(criteria.getPredicates().get("placeofDelivery")).trim();
			String placeofReceipt = CommonUtility.deNull(criteria.getPredicates().get("placeofReceipt")).trim();
			int hsCodeSize = Integer.valueOf( CommonUtility.deNull(criteria.getPredicates().get("hsCodeSize")));
			
			List<HsCodeDetails> multiHsCodeList = new ArrayList<HsCodeDetails>();
			HsCodeDetails hsCodeDetails = new HsCodeDetails();
			if (hsCodeSize > 0) {
				for (int X = 0; X < hsCodeSize; X++) {
					hsCodeDetails = new HsCodeDetails();
					hsCodeDetails.setCrgDes(CommonUtility.deNull(criteria.getPredicates().get("CrgDescArr" + X)));
					hsCodeDetails.setHsCode(CommonUtility.deNull(criteria.getPredicates().get("HsCodeArr" + X)));
					hsCodeDetails.setNbrPkgs(CommonUtility.deNull(criteria.getPredicates().get("NoOfPKgsArr" + X)));
					hsCodeDetails.setCustomHsCode(CommonUtility.deNull(criteria.getPredicates().get("customHsCodeArr" + X)));
					hsCodeDetails.setGrossWt(CommonUtility.deNull(criteria.getPredicates().get("gwtArr" + X)));
					if((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + X))).indexOf("-") == -1) {
						hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + X))));	
						hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + X))));	
					} else {
						hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + X))).split("-")[0]);
						hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + X))).split("-")[1]);
					}
					hsCodeDetails.setGrossVol(CommonUtility.deNull(criteria.getPredicates().get("mSmtArr" + X)));
					hsCodeDetails.setHsSubCodeDesc( CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDescArr" + X)));
					hsCodeDetails.setIsHsCodeChange(CommonUtility.deNull(criteria.getPredicates().get("isHsCodeChange" + X)));
					hsCodeDetails.setHscodeSeqNbr(CommonUtility.deNull(criteria.getPredicates().get("hscodeSeqNbr" + X)));
					multiHsCodeList.add(hsCodeDetails);
				}
				
			}
			
			// END CR FTZ HSCODE - NS JULY 2024

			dpeUtil = cargoAmendmentService.getDiscargingCargo(asn_nbr);
			if (StringUtils.equalsIgnoreCase("Y", dpeUtil.getGb_close_bj_ind())) {
				throw new BusinessException("M1000002");
			}
			log.info("GeneralCargoAmendAction 2222:: updateManifest : END ");

			if (!CommonUtility.deNull(crgtyp).equals("") && !CommonUtility.deNull(category).equals("")) {
				log.info("GeneralCargoAmendAction 2222:: updateManifest : insideSsss " + crgtyp + ":" + category);

				isApplicableCargoCategory = cargoAmendmentService.isApplicableCargoCategory(crgtyp, category, "other");
				if (!isApplicableCargoCategory) {
					log.info("GeneralCargoAmendAction 22244444442:: updateManifest : insideSsss " + crgtyp + ":"
							+ category);
					throw new BusinessException("M20222");
				}
			}

			log.info("GeneralCargoAmendAction 3333:: updateManifest : END ");
			// END added by Maksym JCMS Smart CR 6.10
			mftUpdateDetail = cargoAmendmentService.MftUpdationForDPE(usrid, coCd, seqno, varno, blno, crgtyp, hscd,
					hsSubCodeFr, hsSubCodeTo, crgdesc, mark, nopkgs, gwt, gvol, crgstat, dgind, stgind, dop, pkgtyp,
					coname, consigneeCoyCode, poL, poD, poFD, cntrtype, cntrsize, cntr1, cntr2, cntr3, cntr4, autParty,
					adviseBy, adviseDate, adviseMode, amendChargedTo, waiveCharge, waiveReason, category,
					customHsCode, conAddr, shipperNm, shipperAddr, notifyParty, notifyPartyAddr, placeofDelivery, placeofReceipt,multiHsCodeList);
			map.put("CargoInfo", dpeUtil);
			map.put("isApplicableCargoCategory", isApplicableCargoCategory);
			map.put("seqno", mftUpdateDetail);
		} catch (BusinessException e) {
			log.info("Exception updateManifest : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception updateManifest : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setSuccess(true);
				result.setData(map);
			}
			log.info("updateManifest Result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->listCrgType()
	@PostMapping(value = "/listCrgType")
	public Result listCrgType(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		List<DPEUtil> listCrgType = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: listCrgType: criteria : " + criteria.toString());
			String vslType = CommonUtility.deNull(criteria.getPredicates().get("vslType")).trim();
			listCrgType = cargoAmendmentService.listCrgType(vslType);
			data.put("data", listCrgType);
		} catch (BusinessException e) {
		 	log.info("Exception listCrgType : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception listCrgType : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: listCrgType result:" + result.toString());
		}
		return result;
	}

	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->getShipper()
	@PostMapping(value = "/getShipper")
	public Result getShipper(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		List<DPEUtil> recs = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getShipper :: criteria : " + criteria.toString());
			String name = CommonUtility.deNull(criteria.getPredicates().get("name")).trim();
			String shipperCode = CommonUtility.deNull(criteria.getPredicates().get("shipperCode")).trim();
			recs = cargoAmendmentService.getShipper(name, shipperCode, criteria);
		} catch (BusinessException e) {
		 	log.info("Exception getShipper : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception getShipper : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			data.put("data", recs);
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: getShipper result:" + result.toString());
		}
		return result;
	}

	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->currentListCargoCategory()
	@PostMapping(value = "/getCurrentListCargoCategory")
	public Result getCurrentListCargoCategory(HttpServletRequest request, HttpServletResponse response)
			throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		List<DPEUtil> currentListCargoCategory = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getCurrentListCargoCategory :: criteria : " + criteria.toString());
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode")).trim();
			String cargoTypeCode = CommonUtility.deNull(criteria.getPredicates().get("cargoTypeCode")).trim();
			currentListCargoCategory = cargoAmendmentService.currentListCargoCategory(cargoTypeCode, companyCode);
		} catch (BusinessException e) {
		 	log.info("Exception getCurrentListCargoCategory : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception getCurrentListCargoCategory : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			data.put("data", currentListCargoCategory);
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: getCurrentListCargoCategory result:" + result.toString());
		}
		return result;
	}

	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->listHsCode()
	@PostMapping(value = "/listHsCode")
	public Result listHsCode(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		List<HSCode> list = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: listHsCode criteria : " + criteria.toString());
			list = cargoAmendmentService.listHsCode("1");
			data.put("data", list);
		} catch (BusinessException e) {
		 	log.info("Exception listHsCode : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception listHsCode : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: listHsCode result:" + result.toString());
		}
		return result;
	}

	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->listHsSubCode()
	@PostMapping(value = "/listHsSubCode")
	public Result listHsSubCode(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		List<DPEUtil> listHsSubCode = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: listHsSubCode :: criteria : " + criteria.toString());
			String hsCode = CommonUtility.deNull(criteria.getPredicates().get("hsCode")).trim();
			listHsSubCode = cargoAmendmentService.listHsSubCode("1", hsCode);
		} catch (BusinessException e) {
		 	log.info("Exception listHsSubCode : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception listHsSubCode : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			data.put("data", listHsSubCode);
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: listHsSubCode result:" + result.toString());
		}
		return result;
	}

	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->getHsSubCodeDesc()
	@PostMapping(value = "/getHsSubCodeDesc")
	public Result getHsSubCodeDesc(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		DPEUtil listHsSubCode = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getHsSubCodeDesc :: criteria : " + criteria.toString());
			String hsCode = CommonUtility.deNull(criteria.getPredicates().get("hsCode")).trim();
			String hsSubCode = CommonUtility.deNull(criteria.getPredicates().get("hsSubCode")).trim();
			listHsSubCode = (DPEUtil) cargoAmendmentService.getHsSubCodeDesc(hsCode, hsSubCode);
			
		} catch (Exception e) {
			log.info("Exception getHsSubCodeDesc : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			data.put("data", listHsSubCode);
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: getHsSubCodeDesc result:" + result.toString());
		}
		return result;
	}

	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->getTruckerList()
	@PostMapping(value = "/getTruckerList")
	public Result getTruckerList(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		List<TruckerValueObject> truckerList = new ArrayList<TruckerValueObject>();
		errorMessage = null;
		isUserAccountContain=false;
		isAccountContain=false;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getTruckerList :: criteria : " + criteria.toString());
			String mode = CommonUtility.deNull(criteria.getPredicates().get("mode")).trim();
			if (StringUtils.equalsIgnoreCase("ADDONEROW", mode)) {
				String truckerItems = CommonUtility.deNull(criteria.getPredicates().get("truckerItems")).trim();
				JSONArray trkItems = null;
				trkItems = new JSONArray(truckerItems);
				JSONObject o = null;
				if (trkItems.length() > 0) {
					for (int i = 0; i < trkItems.length(); i++) {
						o = trkItems.getJSONObject(i);
						TruckerValueObject vo = new TruckerValueObject();
						vo.setTruckerIc(o.getString("truckerIc"));
						vo.setTruckerNm(o.getString("truckerNm"));
						vo.setTruckerCd(o.getString("truckerCd"));
						vo.setTruckerContact(o.getString("truckerContact"));
						vo.setTruckerPkgs(o.getString("truckerPkgs"));
						if (o.getBoolean("truckerChbx")) {
							vo.setTruckerChbx(o.getBoolean("truckerChbx"));
						} else {
							vo.setTruckerChbx(false);
						}
						truckerList.add(vo);
					}
				}
				TruckerValueObject addedRow = new TruckerValueObject();
				addedRow.setTruckerIc("");
				addedRow.setTruckerNm("");
				addedRow.setTruckerCd("");
				addedRow.setTruckerContact("");
				addedRow.setTruckerPkgs("");
				addedRow.setTruckerChbx(false);
				truckerList.add(addedRow);
			} else if (StringUtils.equalsIgnoreCase("REMOVEROW", mode)) {
				String truckerItems = CommonUtility.deNull(criteria.getPredicates().get("truckerItems")).trim();
				JSONArray trkItems = null;
				trkItems = new JSONArray(truckerItems);
				JSONObject o = null;

				if (trkItems.length() > 0) {
					for (int i = 0; i < trkItems.length(); i++) {
						o = trkItems.getJSONObject(i);
						if (!o.getBoolean("truckerChbx")) {
							TruckerValueObject vo = new TruckerValueObject();
							vo.setTruckerIc(o.getString("truckerIc"));
							vo.setTruckerNm(o.getString("truckerNm"));
							vo.setTruckerCd(o.getString("truckerCd"));
							vo.setTruckerContact(o.getString("truckerContact"));
							vo.setTruckerPkgs(o.getString("truckerPkgs"));
							truckerList.add(vo);
						}
					}
				}

			} else {
				String esn_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("esn_asn_nbr")).trim();
				String truckerNo = CommonUtility.deNull(criteria.getPredicates().get("truckerNo")).trim();
				String truckerName = CommonUtility.deNull(criteria.getPredicates().get("truckerName")).trim();
				String truckerContactNo = CommonUtility.deNull(criteria.getPredicates().get("truckerContactNo")).trim();
				String trucker_nbr_pkg = CommonUtility.deNull(criteria.getPredicates().get("trucker_nbr_pkg")).trim();
				String truckerCd = "";
				TruckerValueObject trkObj = new TruckerValueObject();
				trkObj = cargoAmendmentService.getTruckerDetails(truckerNo);
				if (trkObj != null) {
					truckerCd = trkObj.getTruckerCd();
				}

				TruckerValueObject mainObj = new TruckerValueObject();
				mainObj.setTruckerIc(truckerNo);
				mainObj.setTruckerNm(truckerName);
				mainObj.setTruckerContact(truckerContactNo);
				mainObj.setTruckerPkgs(trucker_nbr_pkg);
				mainObj.setTruckerChbx(false);
				mainObj.setTruckerCd(truckerCd);
				truckerList.add(mainObj);

				List<TruckerValueObject> truckerListTemp = new ArrayList<TruckerValueObject>();
				truckerListTemp = cargoAmendmentService.getTruckerList(esn_asn_nbr);
				truckerList.addAll(truckerListTemp);
			}
		} catch (BusinessException e) {
		 	log.info("Exception getTruckerList : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception getTruckerList : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			data.put("data", truckerList);
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: getTruckerList result:" + result.toString());
		}
		return result;
	}

	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->getTruckerDetails()
	@PostMapping(value = "/getTruckerDetails")
	public Result getTruckerDetails(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		TruckerValueObject truckerDetails = new TruckerValueObject();
		errorMessage = null;
		isUserAccountContain=false;
		isAccountContain=false;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getTruckerDetails :: criteria : " + criteria.toString());
			String truckerIc = CommonUtility.deNull(criteria.getPredicates().get("truckerIc")).trim();
			if (!StringUtils.isEmpty(truckerIc)) {
				boolean isValid = cargoAmendmentService.checkValidTrucker(truckerIc);
				if (isValid) {
					truckerDetails = cargoAmendmentService.getTruckerDetails(truckerIc);
				}
			}
		} catch (BusinessException e) {
		 	log.info("Exception getTruckerDetails : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception getTruckerDetails : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			data.put("data", truckerDetails);
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: getTruckerDetails result:" + result.toString());
		}
		return result;
	}

	// sg.com.jp.dpe.action-->GeneralCargoAmendAction-->lookupRef
	@PostMapping(value = "/lookupRef")
	public ResponseEntity<?> lookupRef(HttpServletRequest request) {
		List<DPEUtil> recs = null;
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: lookupRef :: criteria : " + criteria.toString());
			String type = CommonUtility.deNull(criteria.getPredicates().get("type")).trim();
			String name = CommonUtility.deNull(criteria.getPredicates().get("name")).trim();
			String vv_cd = CommonUtility.deNull(criteria.getPredicates().get("vv_cd"));

			if (StringUtils.equalsIgnoreCase("company", type)) {
				recs = cargoAmendmentService.listAuthorizedParty(name, vv_cd, criteria);
			} else if (StringUtils.equalsIgnoreCase("port", type)) {
				recs = cargoAmendmentService.listPort(name, criteria);
				return searchResponse(recs,recs.size());
			} else if (StringUtils.equalsIgnoreCase("packaging", type)) {
				recs = cargoAmendmentService.listPackaging(name, criteria);
			} else if (StringUtils.equalsIgnoreCase("listCompany", type)) {
				recs = cargoAmendmentService.listCompany(name, criteria);
			}
		} catch (BusinessException e) {
		 	log.info("Exception lookupRef : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception lookupRef : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			data.put("data", recs);
			data.put("total", recs == null ? 0 : recs.size());
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: lookupRef result:" + result.toString());
		}
		return ResponseEntityUtil.success(result);
	}
	
	
	private ResponseEntity<?> searchResponse(List<DPEUtil> list, int total) throws BusinessException {
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			log.info("START: searchResponse list:"+list.toString()+"total:"+total);
			data.put("data", list);
			response.put("data", data);
			response.put("total", list.size());
			log.info("list"+list.toString());
		} catch (Exception e) {
			log.info("Exception searchResponse : ", e);
			throw new BusinessException("M4201");
		}
		finally {
			log.info("END: searchResponse Search");
		}
		return ResponseEntityUtil.ok(response);
	}
	

	// sg.com.jp.dpe.action-->GeneralCargoAmendAction-->listAcount
	@PostMapping(value = "/listAcount")
	public Result listAcount(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		List<DPEUtil> listCrgType = null;
		errorMessage = null;
		isUserAccountContain=false;
		isAccountContain=false;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: listAcount :: criteria : " + criteria.toString());
			String vv_cd = CommonUtility.deNull(criteria.getPredicates().get("vv_cd")).trim();
			listCrgType = cargoAmendmentService.listAcount(vv_cd);
			data.put("data", listCrgType);
		} catch (BusinessException e) {
		 	log.info("Exception listAcount : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception listAcount : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: listAcount result:" + result.toString());
		}
		return result;
	}

	// sg.com.jp.dpe.action-->GeneralCargoAmendAction-->getAccountForLoad
	@PostMapping(value = "/getAccountForLoad")
	public Result getAccountForLoad(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		List<EsnListValueObject> list = new ArrayList<EsnListValueObject>();
		errorMessage = null;
		isUserAccountContain=false;
		isAccountContain=false;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getAccountForLoad :: criteria : " + criteria.toString());
			String truckerNo = CommonUtility.deNull(criteria.getPredicates().get("truckerCd")).trim();
			String bookingRefNo = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo")).trim();
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode")).trim();
			list = getListAccount(truckerNo, bookingRefNo, null, coCd);
		} catch (BusinessException e) {
		 	log.info("Exception getAccountForLoad : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception getAccountForLoad : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			data.put("data", list);
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: getAccountForLoad result:" + result.toString());
		}
		return result;
	}

	// sg.com.jp.dpe.action-->GeneralCargoAmendAction-->getListAccount
	@PostMapping(value = "/getListAccount")
	public List<EsnListValueObject> getListAccount(String truckerNo, String bkRefNo, String accountNo, String coCd)
			throws BusinessException {
		Result result = new Result();
		String accNo = "";
		List<EsnListValueObject> listAccount = null;
		List<EsnListValueObject> list = new ArrayList<EsnListValueObject>();
		isUserAccountContain=false;
		isAccountContain=false;
		try {
			log.info("START: getListAccount ");
			listAccount = cargoAmendmentService.getAccNo(truckerNo);
			if (listAccount == null || listAccount.size() == 0) {
				accNo = "No";
			} else {
				int size = listAccount.size();
				accNo = listAccount.get(size - 1).getAccNo();
				for (EsnListValueObject esnListValueObject : listAccount) {
					esnListValueObject.setCompanyName("Trucker - " + esnListValueObject.getAccNo());
					if (StringUtils.equalsIgnoreCase(esnListValueObject.getAccNo(), accountNo)) {
						isAccountContain = true;
					}
				}
			}
			List<EsnListValueObject> userAccNo = cargoAmendmentService.getUserAccNo(bkRefNo, coCd, accNo);
			if (userAccNo != null && userAccNo.size() > 0) {
				for (EsnListValueObject esnListValueObject : userAccNo) {
					esnListValueObject.setCompanyName("ESN Declarant - " + esnListValueObject.getAccNo());
					if (StringUtils.equalsIgnoreCase(esnListValueObject.getAccNo(), accountNo)) {
						isUserAccountContain = true;
					}
				}
			}
			list.addAll(listAccount);
			list.addAll(userAccNo);
			EsnListValueObject esnListValueObject = new EsnListValueObject();
			esnListValueObject.setAccNo("CA");
			esnListValueObject.setCompanyName("CASH PAYMENT");
			list.add(esnListValueObject);
		} catch (BusinessException e) {
		 	log.info("Exception getListAccount : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception getListAccount : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			log.info("END: getListAccount result:" + result.toString());
		}
		return list;
	}

	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->getShipperInformation()
	@PostMapping(value = "/getShipperInformation")
	public Result getShipperInformation(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		DPEUtil util = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getShipperInformation criteria : " + criteria.toString());
			String name = CommonUtility.deNull(criteria.getPredicates().get("shipperName")).trim();
			util = (DPEUtil) cargoAmendmentService.getShipperInformation(name);
		} catch (Exception e) {
			log.info("Exception getShipperInformation : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			data.put("data", util);
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				if (util == null) {
					result.setSuccess(false);
				} else {
				result.setSuccess(true);
			}
			}
			log.info("END: getShipperInformation result:" + result.toString());
		}
		return result;
	}

	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->updateEsn()
	@PostMapping(value = "/updateEsn")
	public Result updateEsn(@RequestBody UpdateEsnRequest updateEsnRequest, HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		isUserAccountContain=false;
		isAccountContain=false;
		try {
			log.info("START: updateEsn updateEsnRequest:"+updateEsnRequest.toString());
			String msg = "";
			Criteria criteria = CommonUtil.getCriteria(request);
			int noOfPkgs = updateEsnRequest.getNoOfPkgs();
			String bookingRefNo = CommonUtility.deNull(updateEsnRequest.getBookingRefNo());
			String hscd = CommonUtility.deNull(updateEsnRequest.getHscd());
			String pkgsType = CommonUtility.deNull(updateEsnRequest.getPkgsType());
			String mark = CommonUtility.deNull(updateEsnRequest.getMark());
			String lopInd = CommonUtility.deNull(updateEsnRequest.getLopInd());
			String dgInd = CommonUtility.deNull(updateEsnRequest.getDgInd());
			String stgInd = CommonUtility.deNull(updateEsnRequest.getStgInd());
			String loadingFrom = CommonUtility.deNull(updateEsnRequest.getLoadingFrom());
			String poD = CommonUtility.deNull(updateEsnRequest.getPoD());
			int noOfStorageDay = 0;
			String dutiInt = CommonUtility.deNull(updateEsnRequest.getDutiGI());
			String payMode = CommonUtility.deNull(updateEsnRequest.getPayMode());
			String otherAcct = CommonUtility.deNull(updateEsnRequest.getOtherAcct());
			String accNo = CommonUtility.deNull(updateEsnRequest.getAccNo());
			if (!otherAcct.equals("")) {
				accNo = otherAcct;
			}
			String esnNbr = CommonUtility.deNull(updateEsnRequest.getEsn_asn_nbr());
			String cargoDes = CommonUtility.deNull(updateEsnRequest.getCargoDes());
			double weight = updateEsnRequest.getWeight();
			double volume = updateEsnRequest.getVolume();
			String cntr1 = CommonUtility.deNull(updateEsnRequest.getCntr1());
			String cntr2 = CommonUtility.deNull(updateEsnRequest.getCntr2());
			String cntr3 = CommonUtility.deNull(updateEsnRequest.getCntr3());
			String cntr4 = CommonUtility.deNull(updateEsnRequest.getCntr4());
			String stfInd = CommonUtility.deNull(updateEsnRequest.getStfInd());
			String strUAFlag = CommonUtility.deNull(updateEsnRequest.getStrUAFlag());
			String strUserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount")).trim();
			String category = CommonUtility.deNull(updateEsnRequest.getCategory());
			String hsSubCodeFr = CommonUtility.deNull(updateEsnRequest.getHsSubCodeFr());
			String hsSubCodeTo = CommonUtility.deNull(updateEsnRequest.getHsSubCodeTo());
			String varNbr = CommonUtility.deNull(updateEsnRequest.getVarNbr());
			String isEditShipper = CommonUtility.deNull(updateEsnRequest.getIsEditShipper());
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode")).trim();
			String tdb_cr_nbr = CommonUtility.deNull(updateEsnRequest.getTdb_cr_nbr());
			String uen_nbr = CommonUtility.deNull(updateEsnRequest.getUen_nbr());
			String shipperNm = CommonUtility.deNull( updateEsnRequest.getCo_nm());
			String shipperAddress =  CommonUtility.deNull(updateEsnRequest.getAdd_l1());
			String asn_nbr = CommonUtility.deNull(updateEsnRequest.getAsn_nbr());
			String cargoTypeCode = CommonUtility.deNull(updateEsnRequest.getCrgType());
			
			// START CR FTZ HSCODE - NS NOV 2024			
			String customHsCode = CommonUtility.deNull(updateEsnRequest.getCustomHsCode());
			String conNm = CommonUtility.deNull(updateEsnRequest.getConNm());
			String conAddr = CommonUtility.deNull(updateEsnRequest.getConAddr());
			String shipperAddr = CommonUtility.deNull(updateEsnRequest.getShipperAddr());
			String notifyParty = CommonUtility.deNull(updateEsnRequest.getNotifyParty());
			String notifyPartyAddr = CommonUtility.deNull(updateEsnRequest.getNotifyPartyAddr());
			String placeofDelivery = CommonUtility.deNull(updateEsnRequest.getPlaceofDelivery());
			String placeofReceipt = CommonUtility.deNull(updateEsnRequest.getPlaceofReceipt());
			String blNbr = CommonUtility.deNull(updateEsnRequest.getBlNbr());
			
			List<HsCodeDetails> multiHsCodeList = updateEsnRequest.getMultiHsCodeList();
			// END CR FTZ HSCODE - NS NOV 2024
			
			String shipperNbr = "";
			if (StringUtils.isNotBlank(tdb_cr_nbr)) {
				shipperNbr = tdb_cr_nbr;
			} else if (StringUtils.isNotBlank(uen_nbr))
				shipperNbr = uen_nbr;
			String trucker_cd = "";
			String truckerCNo = "";
			String truckerName = "";
			String truckerNo = "";
			List<TruckerValueObject> truckerItems = updateEsnRequest.getTruckerItems();
			List<TruckerValueObject> truckerList = new ArrayList<TruckerValueObject>();

			int totalTruckerPakgs = 0;
			int mainTrkPkgs = 0;
			if (truckerItems.size() > TruckerValueObject.MAX_ADP_TRUCKER) {
				String[] tmpString = { TruckerValueObject.MAX_ADP_TRUCKER_STRING };
				errorMessage = CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Cannot_Add_More_Truckers,
						tmpString);
				msg = CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Cannot_Add_More_Truckers,
						tmpString);
				map.put("msg", msg);
				return result;
			}
			if (truckerItems.size() > 0) {
				for (int i = 0; i < truckerItems.size(); i++) {
					TruckerValueObject trkObj = new TruckerValueObject();
					trkObj = truckerItems.get(i);
					truckerList.add(trkObj);
					if (i == 0) {
						mainTrkPkgs = Integer.parseInt(trkObj.getTruckerPkgs());
						truckerName = trkObj.getTruckerNm();
						truckerNo = trkObj.getTruckerIc();
						truckerCNo = trkObj.getTruckerContact();
						trucker_cd = trkObj.getTruckerCd();
					}
					totalTruckerPakgs += Integer.parseInt(trkObj.getTruckerPkgs());
				}
			} else {
				msg = ConstantUtil.ErrorMsg_Atleast_One_Trucker;
				errorMessage= ConstantUtil.ErrorMsg_Atleast_One_Trucker;
				map.put("msg", msg);
				return result;
			}

			if (totalTruckerPakgs > noOfPkgs) {
				msg = ConstantUtil.ErrorMsg_Pkg_Not_Greater;
				errorMessage= ConstantUtil.ErrorMsg_Pkg_Not_Greater;
				map.put("msg", msg);
				return result;
			}

			DPEUtil dpeUtil = new DPEUtil();
			dpeUtil = cargoAmendmentService.getEsnVessel(asn_nbr);

			if (StringUtils.equalsIgnoreCase("Y", dpeUtil.getGb_close_shp_ind())) {
				throw new BusinessException("M1000002");
			}
			if (totalTruckerPakgs != 0 && totalTruckerPakgs != noOfPkgs) {
				msg = ConstantUtil.ErrorMsg_Package_Equal;
				errorMessage= ConstantUtil.ErrorMsg_Package_Equal;
				map.put("msg", msg);
				return result;
			}
			if (!cargoAmendmentService.chkNoOfPkgs(bookingRefNo, noOfPkgs)) {
				msg = ConstantUtil.ErrorMsg_Pkg_Not_Greater_Than_Declared;
				errorMessage= ConstantUtil.ErrorMsg_Pkg_Not_Greater_Than_Declared;
				map.put("msg", msg);
				return result;
			}
			if (StringUtils.equalsIgnoreCase(dgInd, "Y")) {
				if (!cargoAmendmentService.isOutWardPm(bookingRefNo, varNbr)) {
					msg = ConstantUtil.ErrorMsg_No_Aprroved_PM4;
					errorMessage= ConstantUtil.ErrorMsg_No_Aprroved_PM4;
					map.put("msg", msg);
					return result;
				}
			}
			if (!cargoAmendmentService.chkPkgsType(pkgsType)) {
				msg = ConstantUtil.ErrorMsg_Invalid_Pkg_Type;
				errorMessage= ConstantUtil.ErrorMsg_Invalid_Pkg_Type;
				map.put("msg", msg);
				return result;
			}
			if (!cargoAmendmentService.chkWeight(bookingRefNo, weight)) {
				msg = ConstantUtil.ErrorMsg_Weight_Not_Greater_Than_Declared;
				errorMessage = ConstantUtil.ErrorMsg_Weight_Not_Greater_Than_Declared;
				map.put("msg", msg);
				return result;

			}
			if (!cargoAmendmentService.chkVolume(bookingRefNo, volume)) {
				msg = ConstantUtil.ErrorMsg_Volume_Not_Greater_Than_Declared;
				errorMessage = ConstantUtil.ErrorMsg_Volume_Not_Greater_Than_Declared;;
				map.put("msg", msg);
				return result;
			}

			boolean flag5 = cargoAmendmentService.chkAccNo(accNo);
			if (!accNo.equals("CASH") && !accNo.equals("CA") && !flag5) {
				msg = ConstantUtil.ErrorMsg_Billable_Party_Not_Valid;
				errorMessage = ConstantUtil.ErrorMsg_Billable_Party_Not_Valid;
				map.put("msg", msg);
				return result;
			}
			cargoAmendmentService.updateCargoTypeCargoCategory(bookingRefNo, category, cargoTypeCode);
			cargoAmendmentService.esnUpdateForDPE(noOfPkgs, hscd, pkgsType, mark, truckerName, truckerNo, lopInd, dgInd, stgInd,
					loadingFrom, poD, noOfStorageDay, dutiInt, payMode, accNo, esnNbr, cargoDes, trucker_cd, weight,
					volume, truckerCNo, cntr1, cntr2, cntr3, cntr4, stfInd, strUAFlag, strUserID, category, hsSubCodeFr,
					hsSubCodeTo, truckerList, mainTrkPkgs, coCd, customHsCode, multiHsCodeList);
			if (StringUtils.equalsIgnoreCase(isEditShipper, "true")) {
				cargoAmendmentService.updateBkDetails(bookingRefNo, shipperNbr, shipperAddress, shipperNm, strUserID,conNm,conAddr,shipperAddr,notifyParty,notifyPartyAddr,placeofDelivery,placeofReceipt,blNbr);
			} else {
				cargoAmendmentService.updateBkDetails(bookingRefNo, null, shipperAddress, null, strUserID,conNm,conAddr,shipperAddr,notifyParty,notifyPartyAddr,placeofDelivery,placeofReceipt,blNbr);				
			}
		} catch (BusinessException e) {
		 	log.info("Exception updateEsn : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception updateEsn : ", e);
			errorMessage = ConstantUtil.AMEND_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: updateEsn result:" + result.toString());
		}
		return result;
	}
	// End Region
}
