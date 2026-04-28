package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnPsaJpEsnListValueObject;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaEsnListValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.TesnPsaJpService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = TesnPsaJpController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TesnPsaJpController {

	public static final String ENDPOINT = "gc/renominateCargo/tesn";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(TesnPsaJpController.class);

	@Autowired
	private TesnPsaJpService tesnPsaJpService;

	// delegate.helper.gbms.cargo.tesn.tesnpsajp --> TesnPsaJpListHandler
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/TesnPsaJpList")
	public ResponseEntity<?> TesnPsaJpList(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: TesnPsaJpList criteria:" + criteria.toString());
			TableResult tableResult = new TableResult();
			TopsModel topsModel = new TopsModel();

			// String UserID =
			// CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String custCd = coCd;
			// String custCode = accessUserValueObject.getCompanyCode()
			String selVoyno = "";
			String esnNo = "";
//			String vslnew = "";
			esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			/*
			 * String userID = "GSUSER"; String coCd = "GSL";
			 */
			// String = "C1";
			selVoyno = CommonUtility.deNull(criteria.getPredicates().get("vslNbr"));
			/*
			 * >> Add by FPT.Thai - Oct 02 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			boolean isFetchmode = false;
			String fetchVesselName = null;
			String fetchVoyageNbr = null;
			if (criteria.getPredicates().get("fetchmode") != null) {
				String fetchmode = (java.lang.String) CommonUtility.deNull(criteria.getPredicates().get("fetchmode"));
				if (fetchmode.equalsIgnoreCase("TRUE")) {
					isFetchmode = true;
				}
			}
			String isFetch = "FALSE";
			if (isFetchmode) {
				if (criteria.getPredicates().get("vesselName") != null
						&& criteria.getPredicates().get("voyageNbr") != null) {
					// koktsing 20180803
					// Trim the value, so that space will be removed.
					fetchVesselName = CommonUtility
							.deNull(criteria.getPredicates().get("vesselName").trim().toUpperCase());
					fetchVoyageNbr = CommonUtility
							.deNull(criteria.getPredicates().get("voyageNbr").trim().toUpperCase());
					// Added vietnd02
					map.put("fetchVesselName", fetchVesselName); // Added by thanhnv2
					map.put("fetchVoyageNbr", fetchVoyageNbr); // Added by thanhnv2
					isFetch = "TRUE";
					map.put("isFetchmode", isFetch);
					// vietnd02::end
				} else {
					log.info("Invalid vessel voyage values.");
					errorMessage = "Invalid vessel voyage values.";
				}
			}
			/*
			 * << Add by FPT.Thai - Oct 02 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */

			VesselVoyValueObject vesselVoyValueObject = null;
			TesnPsaJpEsnListValueObject esnListValueObject = null;

			List<VesselVoyValueObject> vesselSel = new ArrayList<VesselVoyValueObject>();
			List<TesnPsaJpEsnListValueObject> esnList = new ArrayList<TesnPsaJpEsnListValueObject>();
			List<String> esnNbr = new ArrayList<String>();
			List<String> firstCName = new ArrayList<String>();
			List<String> firstCNbr = new ArrayList<String>();
			List<String> crgDesc = new ArrayList<String>();
			List<String> noOfPkgs = new ArrayList<String>();
			List<String> weight = new ArrayList<String>();
			List<String> volume = new ArrayList<String>();
			List<String> stuffind = new ArrayList<String>();
			// ArrayList bookingRefNbr = new ArrayList();

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			List<String> hsCodeList = new ArrayList<String>();
			String hsCode = "";
			String hsSubCodeFr = "";
			String hsSubCodeTo = "";
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			// VietNguyen (FPT) Document Process Enhancement 06-Dec-2013 : START
			List<String> loadVslList = new ArrayList<String>();
			List<String> loadVoyNbrList = new ArrayList<String>();
			List<String> truckerNmList = new ArrayList<String>();
			List<String> createdByList = new ArrayList<String>();
			List<String> lastModifyDttmList = new ArrayList<String>();
			List<String> schemeList = new ArrayList<String>();
			// VietNguyen (FPT) Document Process Enhancement 06-Dec-2013 : END

			List<String> epcindlist = new ArrayList<String>(); // MCC for EPC IND
			List<String> subSchemeList = new ArrayList<String>();
			List<String> gcOperationsList = new ArrayList<String>();
			List<String> terminalList = new ArrayList<String>();

			vesselSel = tesnPsaJpService.getVesselList(custCd);

			vesselVoyValueObject = new VesselVoyValueObject();
			for (int i = 0; i < vesselSel.size(); i++) {
				vesselVoyValueObject = (VesselVoyValueObject) vesselSel.get(i);
				topsModel.put(vesselVoyValueObject);
			}
			/*
			 * >> Add by FPT.Thai - Oct 02 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			if (isFetchmode) {
				vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject = tesnPsaJpService.getVessel(fetchVesselName, fetchVoyageNbr, custCd);
				if (null != vesselVoyValueObject) {
					selVoyno = vesselVoyValueObject.getVarNbr();
				} else {
					log.info("Invalid vessel voyage values.");
					errorMessage = "Invalid vessel voyage values.";
				}
			}
			/*
			 * << Add by FPT.Thai - Oct 02 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			///////////////// for paging
			// Amended by Punitha on 13/05/2009

			if (selVoyno != null && !selVoyno.equals("")) {

//				String vslnew = CommonUtility.deNull(criteria.getPredicates().get("vslnew"));
				tableResult = tesnPsaJpService.getEsnList(selVoyno, custCd, criteria);
				esnList = tableResult.getData().getListData().getTopsModel();
				
				for (int i = 0; i < esnList.size(); i++) {
					esnListValueObject = new TesnPsaJpEsnListValueObject();
					esnListValueObject = (TesnPsaJpEsnListValueObject) esnList.get(i);
					esnNbr.add("" + esnListValueObject.getEsnNbr());
					firstCName.add((String) esnListValueObject.getFirstCName());
					crgDesc.add((String) esnListValueObject.getCrgDesc());
					noOfPkgs.add("" + esnListValueObject.getNoofPkgs());
					weight.add("" + esnListValueObject.getGrWt());
					volume.add("" + esnListValueObject.getGrVolume());
					firstCNbr.add((String) esnListValueObject.getInvoyageNo());
					stuffind.add(esnListValueObject.getStuffingIndicator());

					// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
					hsCode = esnListValueObject.getHsCode();
					hsSubCodeFr = esnListValueObject.getHsSubCodeFr();
					hsSubCodeTo = esnListValueObject.getHsSubCodeTo();

					if (hsSubCodeTo != null && !"".equalsIgnoreCase(hsSubCodeTo)) {
						hsSubCodeFr = hsSubCodeFr + " - " + hsSubCodeTo;
					}

					if (hsSubCodeFr != null && !"".equalsIgnoreCase(hsSubCodeFr)) {
						hsCode = hsCode + "(" + hsSubCodeFr + ")";
					}
					hsCodeList.add(hsCode);
					// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

					// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014 : START
					loadVslList.add(esnListValueObject.getLoadVsl());
					loadVoyNbrList.add(esnListValueObject.getLoadOutVoy());
					truckerNmList.add(esnListValueObject.getTruckerNm());
					createdByList.add(esnListValueObject.getCreatedBy());
					lastModifyDttmList.add(esnListValueObject.getLastModifyDttm());
					schemeList.add(esnListValueObject.getScheme());
					// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014 : END
					epcindlist.add(esnListValueObject.getDeliveryToEPC());
					subSchemeList.add(esnListValueObject.getSubScheme());
					gcOperationsList.add(esnListValueObject.getGcOperations());
					terminalList.add(esnListValueObject.getTerminal());
				}

				map.put("total", tableResult.getData().getTotal());
			} // if selvoy

			///////////////// for paging

			List<TesnPsaJpEsnListValueObject> esnDetails = new ArrayList<TesnPsaJpEsnListValueObject>();
			if (esnNo != null && !esnNo.equals("")) {
				esnDetails = tesnPsaJpService.getEsnDetails(esnNo, custCd);
			}
			map.put("selVoyno", selVoyno);
			map.put("firstCName", firstCName);
			map.put("noOfPkgs", noOfPkgs);
			map.put("esnNbr", esnNbr);
			map.put("firstCNbr", firstCNbr);
			map.put("weight", weight);
			map.put("volume", volume);
			map.put("stuffind", stuffind);
			map.put("crgDesc", crgDesc);
			map.put("esnDetails", esnDetails);
			map.put("custCd", custCd);
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			map.put("hsCodeList", hsCodeList);
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014 : START
			map.put("loadVslList", loadVslList);
			map.put("loadVoyNbrList", loadVoyNbrList);
			map.put("truckerNmList", truckerNmList);
			map.put("createdByList", createdByList);
			map.put("lastModifyDttmList", lastModifyDttmList);
			map.put("schemeList", schemeList);
			// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014 : END

			map.put("epcindlist", epcindlist); // MCC for EPC IND
			map.put("subSchemeList", subSchemeList);
			map.put("gcOperationsList", gcOperationsList);
			map.put("terminalList", terminalList);

			log.info("After modelManager");

			map.put("topsModel", topsModel);

		} catch (BusinessException be) {
			log.info("Exception TesnPsaJpList: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnPsaJpList : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: TesnPsaJpList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.tesn.tesnpsajp --> TesnPsaJpDetailHandler
	@PostMapping(value = "/TesnPsaJpDetail")
	public ResponseEntity<?> TesnPsaJpDetail(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: TesnPsaJpDetail criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String s = coCd;
			String s1 = "";
			String s2 = "";
			log.info("TesnPsaJpDetailHandler1");
			if (criteria.getPredicates().get("esnNo") != null
					&& !"".equalsIgnoreCase(criteria.getPredicates().get("esnNo"))) {
				s2 = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			} else {
				s2 = CommonUtility.deNull(criteria.getPredicates().get("TesnNo"));
			}
			s1 = CommonUtility.deNull(criteria.getPredicates().get("vslNbr"));
			String s6 = UserID;
			String s7 = "";
			String s8 = "";
			String s9 = "";
			List<String> vector = new ArrayList<String>();
			List<TesnPsaJpEsnListValueObject> vector1 = new ArrayList<TesnPsaJpEsnListValueObject>();
			String s10 = "";
			String s11 = "";
			String s12 = "";
//			if(criteria.getPredicates().get("assignCargo") != null && criteria.getPredicates().get("edi") != null
//					&& criteria.getPredicates().get("approve") != null) {

			s10 = CommonUtility.deNull(criteria.getPredicates().get("assignCargo"));
			s11 = CommonUtility.deNull(criteria.getPredicates().get("edi"));
			s12 = CommonUtility.deNull(criteria.getPredicates().get("approve"));
			log.info("TesnPsaJpDetailHandler3: " + s10 + " - " + s11 + " - " + s12);
//			}

			// VietNguyen added on 13 FEB 2015 to fix assign bill issues
			if (criteria.getPredicates().get("assignCargo") != null) {
				s10 = CommonUtility.deNull(criteria.getPredicates().get("assignCargo"));
			}

			log.info("TesnPsaJpDetailHandler3.1 ");

			List<VesselVoyValueObject> vector2 = new ArrayList<VesselVoyValueObject>();
			List<String> arraylist = new ArrayList<String>();
			List<String> arraylist1 = new ArrayList<String>();
			List<String> arraylist2 = new ArrayList<String>();
			List<String> arraylist3 = new ArrayList<String>();
			List<String> arraylist4 = new ArrayList<String>();
			List<String> arraylist5 = new ArrayList<String>();
			List<String> arraylist6 = new ArrayList<String>();
			vector2 = tesnPsaJpService.getVesselList(s);
			
			for (int i = 0; i < vector2.size(); i++) {
				VesselVoyValueObject vesselvoyvalueobject1 = vector2.get(i);
				topsModel.put(vesselvoyvalueobject1);
			}

		
			if (s1 != null && !s1.equals("")) {
//				String s5 = CommonUtility.deNull(criteria.getPredicates().get("vslnew"));

				TableResult tableResult = tesnPsaJpService.getEsnList(s1, s, criteria);
				@SuppressWarnings("unchecked")
				List<TesnPsaJpEsnListValueObject> vector4 = tableResult.getData().getListData().getTopsModel();

				for (int i1 = 0; i1 < vector4.size(); i1++) {
					TesnPsaJpEsnListValueObject esnlistvalueobject = new TesnPsaJpEsnListValueObject();
					esnlistvalueobject = (TesnPsaJpEsnListValueObject) vector4.get(i1);
					arraylist.add("" + esnlistvalueobject.getEsnNbr());
					arraylist1.add(esnlistvalueobject.getFirstCName());
					arraylist3.add(esnlistvalueobject.getCrgDesc());
					arraylist4.add("" + esnlistvalueobject.getNoofPkgs());
					arraylist5.add("" + esnlistvalueobject.getGrWt());
					arraylist6.add("" + esnlistvalueobject.getGrVolume());
					arraylist2.add(esnlistvalueobject.getInvoyageNo());
				}

			}
			vector = tesnPsaJpService.getSAacctno(s1);
			// START: FPT-NhonBH GCR6 24-Feb-2010
			// Allow SA user to update Billable Party
			// vector1 = tesnPsaJpService.getABacctno(s1);
			if ("JP".equals(coCd)) {
				vector1 = tesnPsaJpService.getABacctno(s1);
			} else {
				vector1 = tesnPsaJpService.getABacctnoForSA(s1);
			}
			// END: FPT-NhonBH GCR6 24-Feb-2010
			// Allow SA user to update Billable Party
			map.put("vsactno", vector);
			map.put("vabactno", vector1);
			s9 = tesnPsaJpService.getBPacctnbr(s2, s1);
			map.put("sacctnbr", s9);
			s7 = tesnPsaJpService.getScheme(s1);
			s8 = tesnPsaJpService.getSchemeInd(s1);
			map.put("schval", s7);
			map.put("schind", s8);
			List<TesnPsaJpEsnListValueObject> vector5 = new ArrayList<TesnPsaJpEsnListValueObject>();
			List<TesnPsaJpEsnListValueObject> vector6 = new ArrayList<TesnPsaJpEsnListValueObject>();
			if (s2 != null && !s2.equals("")) {
				vector5 = tesnPsaJpService.getEsnDetails(s2, s);
				vector6 = tesnPsaJpService.getCntrDetails(s2);
				String s13 = tesnPsaJpService.AssignWhindCheck(s2);
				String s16 = "";
				String s19 = "";
				String s25 = "";
				if (s13 != null && !s13.equals("") && s13.equals("Y")) {
					List<String> vector11 = new ArrayList<String>();
					vector11 = tesnPsaJpService.getWHDetails(s13, s2);
					s16 = (String) vector11.get(1);
					s19 = (String) vector11.get(0);
				} else {
					List<String> vector12 = new ArrayList<String>();
					vector12 = tesnPsaJpService.getWHDetails(s13, s2);
					s25 = (String) vector12.get(0);
				}
				// Added by VietNguyen 20/01/2014
				List<TruckerValueObject> truckerList = tesnPsaJpService.getTruckerList(s2);
				map.put("listTrucker", truckerList);
				// Added by VietNguyen 20/01/2014

				map.put("esnWhindcheck_disp", s13);
				map.put("nodays_disp", s25);
				map.put("whappnbr_disp", s16);
				map.put("remarks_disp", s19);
			}
			if (s12 != null && !s12.equals("") && s12.equals("Y")) {
				String s14 = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo"));
				String s17 = "A";
				String s20 = tesnPsaJpService.getEdiUpdate(s14, s17);
				log.info("s20: " + s20);
			}
			String s15 = "";
			String s18 = "";
			if (criteria.getPredicates().get("bookingRefNo") != null)
				s18 = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo"));
			if (vector5.size() > 0) {
				for (int j1 = 0; j1 < vector5.size(); j1++) {
					TesnPsaJpEsnListValueObject esnlistvalueobject1 = (TesnPsaJpEsnListValueObject) vector5.get(j1);
					s18 = esnlistvalueobject1.getBookingRefNo();
					s15 = tesnPsaJpService.getClsShipInd_bkr(s18);
				}

			} else {
				s15 = tesnPsaJpService.getClsShipInd_bkr(s18);
			}
			map.put("selVoyno", s1);
			map.put("firstCName", arraylist1);
			map.put("noOfPkgs", arraylist4);
			map.put("esnNbr", arraylist);
			map.put("firstCNbr", arraylist2);
			map.put("weight", arraylist5);
			map.put("volume", arraylist6);
			map.put("crgDesc", arraylist3);
			map.put("esnDetails", vector5);
			map.put("cntrDetails", vector6);
			map.put("custCd", s);
			map.put("chkClsShpInd", s15);
			map.put("esnNo", s2);
			map.put("ListData", topsModel);
			
			// Start CR FTZ HSCODE - NS JULY 2024
			List<HsCodeDetails> hscodeDetailsList = tesnPsaJpService.getHsCodeDetailList(s2);
			map.put("hscodeDetailsList", hscodeDetailsList);
			// End  CR FTZ HSCODE - NS JULY 2024
			
			log.info("After modelManager");
			if (s11 != null && !s11.equals("") && s11.equals("EDI") && !s10.equals("ASSIGNDB")
					&& !s10.equals("Assign_Bill") && !s10.equals("Assign") && !s10.equals("CRG")) {
				List<TesnPsaJpEsnListValueObject> vector7 = new ArrayList<TesnPsaJpEsnListValueObject>();
				String s26 = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo"));
				vector7 = tesnPsaJpService.getEdiDetails(s26);
				map.put("ediDetails", vector7);
				// nextScreen(httpservletrequest, "TesnPsaJpEDI");
				
			} else if (s10 != null && !s10.equals("") && s10.equals("CRG")) {
				List<TesnPsaJpEsnListValueObject> vector8 = new ArrayList<TesnPsaJpEsnListValueObject>();
				String s27 = tesnPsaJpService.AssignCrgvalCheck(s2);
				vector8 = tesnPsaJpService.getAssignCargo();
				map.put("tesnCargo", vector8);
				map.put("tesnCrgChk", s27);
				// nextScreen(httpservletrequest, "TesnPsaJpDetail");
				
			} else if (s10 != null && !s10.equals("") && s10.equals("ASSIGNDB")) {
				String s21 = CommonUtility.deNull(criteria.getPredicates().get("cargocategory"));
				tesnPsaJpService.AssignCrgvalUpdate(s21, s2, s6);
				String s28 = tesnPsaJpService.AssignWhindCheck(s2);
				String s31 = "";
				String s36 = "";
				String s39 = "";
				if (s28 != null && !s28.equals("") && s28.equals("Y")) {
					List<String> vector13 = new ArrayList<String>();
					vector13 = tesnPsaJpService.getWHDetails(s28, s2);
					s31 = (String) vector13.get(1);
					s36 = (String) vector13.get(0);
				} else {
					List<String> vector14 = new ArrayList<String>();
					vector14 = tesnPsaJpService.getWHDetails(s28, s2);
					s39 = (String) vector14.get(0);
				}
				map.put("esnWhindcheck_disp", s28);
				map.put("nodays_disp", s39);
				map.put("whappnbr_disp", s31);
				map.put("remarks_disp", s36);
				// nextScreen(httpservletrequest, "TesnPsaJpDetail");
				
			} else if (s10 != null && !s10.equals("") && s10.equals("Assign_Bill")) {
				String s22 = CommonUtility.deNull(criteria.getPredicates().get("billparty"));
				String s29 = "";
				String s32 = "";
				s29 = tesnPsaJpService.getSchemeName(s1);
				if (s29.equals("JLR"))
					s32 = tesnPsaJpService.getVCactnbr(s1);
				else
				// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08>
				// if(!s29.equals("JLR") && !s29.equals("JNL") && !s29.equals("JBT"))
				if (!(s29.equals("JLR") || s29.equals("JNL") || s29.equals("JBT") || s29.equals("JWP")))
					s32 = tesnPsaJpService.getABactnbr(s1);
				tesnPsaJpService.EsnAssignBillUpdate(s22, s2, s6);
				if (s32 != s22)
					tesnPsaJpService.EsnAssignVslUpdate(s1, "Y", s6);
				// nextScreen(httpservletrequest, "TesnPsaJpDetail");
				log.info("TesnPsaJpDetailHandler4: ");
			} else if (s10 != null && !s10.equals("") && s10.equals("Assign")) {
				// nextScreen(httpservletrequest, "TesnPsaJpDetail");
			}

			else if (s10 != null && !s10.equals("") && s10.equals("WHIND")) {
				String s23 = tesnPsaJpService.AssignWhindCheck(s2);
				if (s23 != null && !s23.equals("") && s23.equals("Y")) {
					List<String> vector9 = new ArrayList<String>();
					vector9 = tesnPsaJpService.getWHDetails(s23, s2);
					String s33 = (String) vector9.get(1);
					String s37 = (String) vector9.get(0);
					map.put("whappnbr", s33);
					map.put("remarks", s37);
				} else {
					List<String> vector10 = new ArrayList<String>();
					vector10 = tesnPsaJpService.getWHDetails(s23, s2);
					String s34 = (String) vector10.get(0);
					map.put("nodays", s34);
				}
				map.put("esnWhindcheck", s23);
				// nextScreen(httpservletrequest, "TesnPsaJpDetail");
			} else if (s10 != null && !s10.equals("") && s10.equals("ASSIGWHNDB")) {
				String s24 = CommonUtility.deNull(criteria.getPredicates().get("whind"));
				String s30 = CommonUtility.deNull(criteria.getPredicates().get("whappnbr"));
				String s35 = CommonUtility.deNull(criteria.getPredicates().get("remarks"));
				String s38 = CommonUtility.deNull(criteria.getPredicates().get("nodays"));
				tesnPsaJpService.AssignWhindUpdate(s24, s2, s30, s35, s38, s6);
				String s40 = tesnPsaJpService.AssignWhindCheck(s2);
				String s41 = "";
				String s42 = "";
				String s43 = "";
				if (s40 != null && !s40.equals("") && s40.equals("Y")) {
					List<String> vector15 = new ArrayList<String>();
					vector15 = tesnPsaJpService.getWHDetails(s40, s2);
					s41 = (String) vector15.get(1);
					s42 = (String) vector15.get(0);
				} else {
					List<String> vector16 = new ArrayList<String>();
					vector16 = tesnPsaJpService.getWHDetails(s40, s2);
					s43 = (String) vector16.get(0);
				}
				map.put("esnWhindcheck_disp", s40);
				map.put("nodays_disp", s43);
				map.put("whappnbr_disp", s41);
				map.put("remarks_disp", s42);
				// nextScreen(httpservletrequest, "TesnPsaJpDetail");
			} else {
				// nextScreen(httpservletrequest, "TesnPsaJpDetail");
			}

		} catch (BusinessException be) {
			log.info("Exception TesnPsaJpDetail: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnPsaJpDetail : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: TesnPsaJpDetail result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.tesn.tesnpsajp --> TesnPsaJpAmendHandler
	@PostMapping(value = "/TesnPsaJpAmend")
	public ResponseEntity<?> TesnPsaJpAmend(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: TesnPsaJpAmend criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			// String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String custCd = coCd;
			String edi = CommonUtility.deNull(criteria.getPredicates().get("edi"));
			String esnNo = "";
			esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			TesnPsaJpEsnListValueObject esnListValueObject = null;
			TesnPsaJpEsnListValueObject esnListValueObject1 = null;

			
			String bookingRefNo = "";
			String noOfCntr = "";
			bookingRefNo = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo"));

			String vslType = tesnPsaJpService.getVesselType(bookingRefNo);
			map.put("vslType", vslType);

			if (vslType.equals("CC")) {
				List<Map<String, Object>> list = tesnPsaJpService.getCategoryList();
				map.put("categoryList", list);
			}
			String category = CommonUtility.deNull(criteria.getPredicates().get("category"));
			map.put("category", category);

			List<TesnPsaJpEsnListValueObject> esnDetails = new ArrayList<TesnPsaJpEsnListValueObject>();
			List<TesnPsaJpEsnListValueObject> cntrDetails = new ArrayList<TesnPsaJpEsnListValueObject>();
			List<TesnPsaJpEsnListValueObject> bookingRefList = new ArrayList<TesnPsaJpEsnListValueObject>();
			if (esnNo != null && !esnNo.equals("")) {
				esnDetails = tesnPsaJpService.getEsnDetails(esnNo, custCd);
				cntrDetails = tesnPsaJpService.getCntrDetails(esnNo);
				bookingRefList = tesnPsaJpService.getBkRefNo(bookingRefNo, custCd);
			}
			esnListValueObject1 = new TesnPsaJpEsnListValueObject();
			for (int i = 0; i < bookingRefList.size(); i++) {
				esnListValueObject1 = (TesnPsaJpEsnListValueObject) bookingRefList.get(i);
				noOfCntr = "".concat(String.valueOf(String.valueOf(esnListValueObject1.getNoOfCntr())));
			}

			esnListValueObject = new TesnPsaJpEsnListValueObject();
			for (int i = 0; i < esnDetails.size(); i++) {
				esnListValueObject = (TesnPsaJpEsnListValueObject) esnDetails.get(i);
				topsModel.put(esnListValueObject);
			}

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			List<String> hsCodeList = new ArrayList<String>();

			List<ManifestValueObject> listManifestValueObject = tesnPsaJpService.getHSCodeList("1");
			if (listManifestValueObject != null && listManifestValueObject.size() > 0) {
				String hsCode = "";
				for (int i = 0; i < listManifestValueObject.size(); i++) {
					ManifestValueObject manifestValueObject = new ManifestValueObject();
					manifestValueObject = (ManifestValueObject) listManifestValueObject.get(i);
					hsCode = manifestValueObject.getHsCode();
					hsCodeList.add(hsCode);
				}
			}
			map.put("hsCodeList", hsCodeList);
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
			List<String> listTruckerIc =  new ArrayList<String>(); 
			String truckerIcNo;
			List<String> listTruckerNm =  new ArrayList<String>(); 
			String truckerNmNo;
			List<String> listTruckerContact =  new ArrayList<String>(); 
			String truckerContactNo;
			List<String> listTruckerPkgs =  new ArrayList<String>(); 
			String truckerPkgsNo;
			List<String> listTruckerCd =  new ArrayList<String>(); 
			String truckerCdNo;
			
			if (sizeSelected != 0) {
				for (int i = 0; i < sizeSelected; i++) {
					truckerIcNo = (String) criteria.getPredicates().get("truckerIc" + i);
					listTruckerIc.add(truckerIcNo);
					truckerNmNo = (String) criteria.getPredicates().get("truckerNm" + i);
					listTruckerNm.add(truckerNmNo);
					truckerContactNo = (String) criteria.getPredicates().get("truckerContact" + i);
					listTruckerContact.add(truckerContactNo);
					truckerPkgsNo = (String) criteria.getPredicates().get("truckerPkgs" + i);
					listTruckerPkgs.add(truckerPkgsNo);
					truckerCdNo = (String) criteria.getPredicates().get("truckerCd" + i);
					listTruckerCd.add(truckerCdNo);
				}
			}
			
			List<TruckerValueObject> truckerList = new ArrayList<TruckerValueObject>();
			TruckerValueObject truckerValueObject = null;
			if (listTruckerIc != null) {
				for (int i = 0; i < listTruckerIc.size(); i++) {
					truckerValueObject = new TruckerValueObject();
					truckerValueObject.setTruckerIc(listTruckerIc.get(i));
					truckerValueObject.setTruckerNm(listTruckerNm.get(i));
					truckerValueObject.setTruckerContact(listTruckerContact.get(i));
					truckerValueObject.setTruckerPkgs(listTruckerPkgs.get(i));
					truckerValueObject.setTruckerCd(listTruckerCd.get(i));
					truckerList.add(truckerValueObject);
				}
			}
			map.put("listTrucker", truckerList);
			String trucker = CommonUtility.deNull(criteria.getPredicates().get("trucker"));
			if (trucker != null && !trucker.equals("") && trucker.equals("trucker")) {
				String truckerIc = CommonUtility.deNull(criteria.getPredicates().get("truckerIcParam"));
				String truckerDelete = CommonUtility.deNull(criteria.getPredicates().get("truckerDelete"));
				if (!"delete".equalsIgnoreCase(truckerDelete)) {
					String truckerRow = CommonUtility.deNull(criteria.getPredicates().get("truckerRow"));
					int row = 0;
					if (truckerRow != null) {
						row = Integer.parseInt(truckerRow);
					}
					truckerValueObject = tesnPsaJpService.getTruckerDetails(truckerIc);
					String truckerPkgs = CommonUtility.deNull(criteria.getPredicates().get("truckerPkgsParam"));
					truckerValueObject.setTruckerPkgs(truckerPkgs);
					if (truckerList.size() < row) {
						for (int i = 0; i < row - 1; i++) {
							truckerList.add(new TruckerValueObject());
						}
						truckerList.add(truckerValueObject);
					} else {
						truckerList.set(row, truckerValueObject);
					}
				}
				map.put("listTrucker", truckerList);
				map.put("maxiTrucker", TruckerValueObject.MAX_ADP_TRUCKER);
				map.put("pkgsType", CommonUtility.deNull(criteria.getPredicates().get("pkgsType")));
				map.put("noOfPkgs1", "" + CommonUtility.deNull(criteria.getPredicates().get("noOfPkgs")));
				map.put("cargoDesc", CommonUtility.deNull(criteria.getPredicates().get("cargoDesc")));
				map.put("hsCode", CommonUtility.deNull(criteria.getPredicates().get("hsCode")));
				map.put("hsSubCode", CommonUtility.deNull(criteria.getPredicates().get("hsSubCode")));
				map.put("hsSubCodeDesc", CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDesc")));
				map.put("hsSubCodeFr", CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr")));
				map.put("hsSubCodeTo", CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo")));
				map.put("customHsCode", CommonUtility.deNull(criteria.getPredicates().get("customHsCode")));
				map.put("weightTrucker", "" + CommonUtility.deNull(criteria.getPredicates().get("weight")));
				map.put("volumeTrucker", "" + CommonUtility.deNull(criteria.getPredicates().get("volume")));
				map.put("marking", CommonUtility.deNull(criteria.getPredicates().get("marking")));
				map.put("dgInd", CommonUtility.deNull(criteria.getPredicates().get("dgInd")));
				map.put("lopInd", CommonUtility.deNull(criteria.getPredicates().get("lopInd")));
				map.put("storageInd", CommonUtility.deNull(criteria.getPredicates().get("storageInd")));
				map.put("noOfStorageDay", CommonUtility.deNull(criteria.getPredicates().get("noOfStorageDay")));
				map.put("stuffind", CommonUtility.deNull(criteria.getPredicates().get("stuffind")));
				map.put("category", CommonUtility.deNull(criteria.getPredicates().get("category")));
				map.put("amendtrucker", "amendtrucker");
				String noCntrs = CommonUtility.deNull(criteria.getPredicates().get("noOfContainer"));
				if (StringUtils.isNotBlank(noCntrs)) {
					Integer n = Integer.parseInt(noCntrs);
					for (int i = 1; i <= n; i++) {
						map.put("cntrNo" + i, CommonUtility.deNull(criteria.getPredicates().get("contNo")) + i);
					}
				}
				map.put("deliveryToEPC", CommonUtility.deNull(criteria.getPredicates().get("deliveryToEPC"))); // MCC
																												// for
																												// EPC
																												// IND

			}
			// VietNguyen (FPT) DPE 20-JAN-2014 : END
			map.put("maxiTrucker", TruckerValueObject.MAX_ADP_TRUCKER);
			map.put("esnDetails", esnDetails);
			map.put("cntrDetails", cntrDetails);
			map.put("noOfCntr", noOfCntr);
			map.put("ListData", topsModel);
			
			// START CR FTZ HSCODE - NS JULY 2024
			List<HsCodeDetails> hscodeDetailsList = tesnPsaJpService.getHsCodeDetailList(esnNo);
			map.put("hscodeDetailsList", hscodeDetailsList);
			// END CR FTZ HSCODE - NS JULY 2024

			log.info("After modelManager");
			if (edi != null && edi.equals("") && edi.equals("EDI"))
//				nextScreen(request, "TesnPsaJpEDI");
				log.info(edi);
			else
//				nextScreen(request, "TesnPsaJpAmend");
				log.info(edi);

		} catch (BusinessException be) {
			log.info("Exception TesnPsaJpAmend: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnPsaJpAmend : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: TesnPsaJpAmend result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.tesn.tesnpsajp --> TesnPsaJpAmendConfirmHandler
	@PostMapping(value = "/TesnPsaJpAmendConfirm")
	public ResponseEntity<?> TesnPsaJpAmendConfirm(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: TesnPsaJpAmendConfirm criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String custCd = coCd;

			int noOfPkgs = 0;
			String hsCode = "";
			String hsSubCode = "";
//			String hsSubCodeDesc = "";
			String hsSubCodeFr = "";
			String hsSubCodeTo = "";
			String pkgsType = "";
			String cargoDesc = "";
			String marking = "";
			String lopInd = "";
			String dgInd = "";
			String storageInd = "";
			int noOfStorageDay = 0;
			String portD = "";
			String accNo_I = "";
			String payMode = "";
			String bookingRefNo = "";
			String update = "";
			String esnNo = "";
			int uaNoPkgs = 0;
			String storageDay = "";
			String billName = "";
			String varNbr = "";
			String clsShpInd = "";
			double weight = 0;
			double volume = 0;
			String pkgsDesc = "";
			String cntr1 = "";
			String cntr2 = "";
			String cntr3 = "";
			String cntr4 = "";
			String deliveryToEPC = ""; // MCC for EPC IND
			varNbr = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));
			String category = "00";
			if (!StringUtils.isEmpty(criteria.getPredicates().get("category"))) {
				category = CommonUtility.deNull(criteria.getPredicates().get("category"));
			}
			;
			map.put("category", category);

			boolean checkNoOfPkgs;
			boolean checkPkgsType;
			boolean checkWeight;
			boolean checkVolume;
			boolean checkAccNo;

			esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			update = CommonUtility.deNull(criteria.getPredicates().get("update"));
			bookingRefNo = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo"));
			String noOf_pkgs = CommonUtility.deNull(criteria.getPredicates().get("noOfPkgs"));
			if (noOf_pkgs != null && !noOf_pkgs.equals(""))
				noOfPkgs = Integer.parseInt(noOf_pkgs);
			String weight_s = CommonUtility.deNull(criteria.getPredicates().get("weight"));
			if (weight_s != null && !weight_s.equals(""))
				weight = Double.parseDouble(weight_s);
			String volume_s = CommonUtility.deNull(criteria.getPredicates().get("volume"));
			if (volume_s != null && !volume_s.equals(""))
				volume = Double.parseDouble(volume_s);
			lopInd = CommonUtility.deNull(criteria.getPredicates().get("lopInd"));
			dgInd = CommonUtility.deNull(criteria.getPredicates().get("dgInd"));
			pkgsType = CommonUtility.deNull(criteria.getPredicates().get("pkgsType").trim());
			storageInd = CommonUtility.deNull(criteria.getPredicates().get("storageInd"));
			portD = CommonUtility.deNull(criteria.getPredicates().get("portD"));
			payMode = CommonUtility.deNull(criteria.getPredicates().get("payMode"));
			hsCode = CommonUtility.deNull(criteria.getPredicates().get("hsCode"));
			hsSubCode = CommonUtility.deNull(criteria.getPredicates().get("hsSubCode"));
//			hsSubCodeDesc = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDesc"));
			hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
			hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));
			marking = CommonUtility.deNull(criteria.getPredicates().get("marking"));
			cargoDesc = CommonUtility.deNull(criteria.getPredicates().get("cargoDesc"));

			// check cargo desc and markings to not exceed 200 characters

			if (cargoDesc != null && cargoDesc.length() > 4000) {
				String[] tmpStrings = { "Cargo Description" };
				errorMessage = CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_invalidLength, tmpStrings);
			}
			if (marking != null && marking.length() > 200) {
				errorMessage = "Cargo Markings cannot be more than 200 characters.";
			}
			// log.info("loadFrom"+loadFrom);
			// log.info("dgInd"+dgInd);
			storageDay = CommonUtility.deNull(criteria.getPredicates().get("noOfStorageDay").trim());
			if (storageDay != null && !storageDay.equals(""))
				noOfStorageDay = Integer.parseInt(storageDay);
			cntr1 = CommonUtility.deNull(criteria.getPredicates().get("cntr1"));
			cntr2 = CommonUtility.deNull(criteria.getPredicates().get("cntr2"));
			cntr3 = CommonUtility.deNull(criteria.getPredicates().get("cntr3"));
			cntr4 = CommonUtility.deNull(criteria.getPredicates().get("cntr4"));
			deliveryToEPC = CommonUtility.deNull(criteria.getPredicates().get("deliveryToEPC")); // MCC for EPC IND
			if (hsSubCode == null || hsSubCodeFr == null || hsSubCodeTo == null
					|| hsSubCodeFr.trim().equalsIgnoreCase("") || hsSubCode.trim().equalsIgnoreCase("")
					|| hsSubCodeTo.trim().equalsIgnoreCase("")) {
				errorMessage = ConstantUtil.ErrorMsg_HSsubCode_Null;
				if (errorMessage != null) {
					map.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(map);
					result.setSuccess(false);
					result.setData(map);

				} else {
					result.setData(map);
					result.setSuccess(true);
					log.info("END: TesnPsaJpAmendConfirm result: " + result.toString());
				}

				return ResponseEntityUtil.success(result.toString());
			}
			TesnPsaJpEsnListValueObject esnListValueObject = null;

			// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: START
			if (!"JP".equalsIgnoreCase(custCd)) {
				boolean checkAtuSecondCarrierVsl = tesnPsaJpService.chkDttmOfSecondCarrierVsl(varNbr);
				if (!checkAtuSecondCarrierVsl) {
					errorMessage = ConstantUtil.ErrorMsg_Unable_Create_After_ATU;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAmendConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}

			// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: END
			/*
			 * PM4 check done by karthi on 08/03/04
			 */
			if (CommonUtility.deNull(dgInd).equalsIgnoreCase("Y")) {
				if (!tesnPsaJpService.chkOutwardPM4(bookingRefNo, varNbr)) {
					errorMessage = ConstantUtil.ErrorMsg_ESN_Update_PM4_NotApproved;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAmendConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}

			if (noOf_pkgs != null && !noOf_pkgs.equals("")) {
				checkNoOfPkgs = tesnPsaJpService.chkNoOfPkgs(bookingRefNo, noOfPkgs);
				if (!checkNoOfPkgs) {
					errorMessage = ConstantUtil.ErrorMsg_Package_Greater;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAmendConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}

			if (pkgsType != null && !pkgsType.equals("")) {
				checkPkgsType = tesnPsaJpService.chkPkgsType(pkgsType);
				if (!checkPkgsType) {
					errorMessage = ConstantUtil.ErrorMsg_Invalid_PackageType;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAmendConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}
			if (weight_s != null && !weight_s.equals("")) {
				checkWeight = tesnPsaJpService.chkWeight(bookingRefNo, weight);
				if (!checkWeight) {
					errorMessage = ConstantUtil.ErrorMsg_Weight_Greater;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAmendConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}
			if (volume_s != null && !volume_s.equals("")) {
				checkVolume = tesnPsaJpService.chkVolume(bookingRefNo, volume);
				if (!checkVolume) {
					errorMessage = ConstantUtil.ErrorMsg_Volume_Greater;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAmendConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}

			// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: START
			List<TruckerValueObject> truckerList = new ArrayList<TruckerValueObject>();
			TruckerValueObject truckerValueObject = null;

			int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
			List<String> listTruckerIc =  new ArrayList<String>(); 
			String truckerIcNo;
			List<String> listTruckerNm =  new ArrayList<String>(); 
			String truckerNmNo;
			List<String> listTruckerContact =  new ArrayList<String>(); 
			String truckerContactNo;
			List<String> listTruckerPkgs =  new ArrayList<String>(); 
			String truckerPkgsNo;
			List<String> listTruckerCd =  new ArrayList<String>(); 
			String truckerCdNo;
			
			if (sizeSelected != 0) {
				for (int i = 0; i < sizeSelected; i++) {
					truckerIcNo = (String) criteria.getPredicates().get("truckerIc" + i);
					listTruckerIc.add(truckerIcNo);
					truckerNmNo = (String) criteria.getPredicates().get("truckerNm" + i);
					listTruckerNm.add(truckerNmNo);
					truckerContactNo = (String) criteria.getPredicates().get("truckerContact" + i);
					listTruckerContact.add(truckerContactNo);
					truckerPkgsNo = (String) criteria.getPredicates().get("truckerPkgs" + i);
					listTruckerPkgs.add(truckerPkgsNo);
					truckerCdNo = (String) criteria.getPredicates().get("truckerCd" + i);
					listTruckerCd.add(truckerCdNo);
				}
			}
			
			if (listTruckerIc != null) {
				for (int i = 0; i < listTruckerIc.size(); i++) {
					truckerValueObject = new TruckerValueObject();
					truckerValueObject.setTruckerIc(listTruckerIc.get(i));
					truckerValueObject.setTruckerNm(listTruckerNm.get(i));
					truckerValueObject.setTruckerContact(listTruckerContact.get(i));
					truckerValueObject.setTruckerPkgs(listTruckerPkgs.get(i));
					truckerList.add(truckerValueObject);
					// if(!tesnPsaJpService.chkValidTrucker(truckerValueObject)) {
					// errorMessage(request, "The Trucker Ic Number " +
					// truckerValueObject.getTruckerIc() + " invalid");
					// return;
					// }
				}
			}
			log.info("=========truckerList=====" + truckerList.size());
			// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: START

			List<TesnPsaJpEsnListValueObject> accNoList = new ArrayList<TesnPsaJpEsnListValueObject>();
			List<TesnPsaJpEsnListValueObject> UserAccNoList = new ArrayList<TesnPsaJpEsnListValueObject>();
			List<String> userAccNo = new ArrayList<String>();
			List<String> accNo = new ArrayList<String>();
			String getAccNo = "";
			accNoList = tesnPsaJpService.getAccNo(varNbr);
			// UserAccNoList =(Vector)tesnPsaJpService.getUserAccNo(custCd);
			if (accNoList.size() != 0) {
				for (int i = 0; i < accNoList.size(); i++) {
					esnListValueObject = new TesnPsaJpEsnListValueObject();
					esnListValueObject = (TesnPsaJpEsnListValueObject) accNoList.get(i);
					accNo.add(esnListValueObject.getAccNo());
				}
			}
			if (accNoList.size() != 0)
				getAccNo = esnListValueObject.getAccNo();
			else
				getAccNo = "No";
			UserAccNoList = tesnPsaJpService.getUserAccNo(bookingRefNo, custCd, getAccNo);
			if (UserAccNoList.size() != 0) {
				for (int i = 0; i < UserAccNoList.size(); i++) {
					esnListValueObject = new TesnPsaJpEsnListValueObject();
					esnListValueObject = UserAccNoList.get(i);
					userAccNo.add(esnListValueObject.getAccNo());
				}
			}
			if (update != null && !update.equals("")) {
				payMode = CommonUtility.deNull(criteria.getPredicates().get("payMode"));
				accNo_I = CommonUtility.deNull(criteria.getPredicates().get("accNo"));
				checkAccNo = tesnPsaJpService.chkAccNo(accNo_I);
				if (accNo_I.equals("CASH") || accNo_I.equals("CA")) {
				} else {
					if (!checkAccNo) {
						errorMessage = ConstantUtil.ErrorMsg_Invalid_Billable_AccNo;

						if (errorMessage != null) {
							map.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(map);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: TesnPsaJpAmendConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				}

				uaNoPkgs = (int) tesnPsaJpService.getUaNoPkgs(esnNo);
				clsShpInd = (String) tesnPsaJpService.getClsShipInd(varNbr);
				// Added UserID by Revathi
				// tesnPsaJpService.esnUpdate(noOfPkgs, hsCode, pkgsType, marking, lopInd,
				// dgInd, storageInd, portD, noOfStorageDay,
				// payMode, accNo_I, esnNo, cargoDesc, weight,
				// volume, cntr1, cntr2, cntr3, cntr4,
				// bookingRefNo,
				// CommonUtility.deNull(criteria.getPredicates().get("stuffind"),UserID,category
				// );
				
				// START CR FTZ HSCODE - NS JULY 2024
				
				String customHsCode = CommonUtility.deNull(criteria.getPredicates().get("customHsCode"));
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

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				tesnPsaJpService.esnUpdateForDPE(noOfPkgs, hsCode, hsSubCodeFr, hsSubCodeTo, pkgsType, marking, lopInd,
						dgInd, storageInd, portD, noOfStorageDay, payMode, accNo_I, esnNo, cargoDesc, weight, volume,
						cntr1, cntr2, cntr3, cntr4, bookingRefNo,
						CommonUtility.deNull(criteria.getPredicates().get("stuffind")), UserID, category, truckerList,
						deliveryToEPC, customHsCode, multiHsCodeList); // MCC For EPC IND
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

				String categoryValue = tesnPsaJpService.getCategoryValue(category);
				map.put("categoryValue", categoryValue);

				String Status = "N";
				String approved = tesnPsaJpService.getEdiUpdate(bookingRefNo, Status);
				pkgsDesc = tesnPsaJpService.getPkgsDesc(esnNo);
				billName = tesnPsaJpService.getBillablePartyName(accNo_I);
				// String k = createNomVesselPsaJp(firstCName,inVoyageNo ,UserID);
				map.put("approved", approved);
			}
			// --Thiru added oct 10
			String chkClsShpInd = tesnPsaJpService.getClsShipInd_bkr(bookingRefNo);

			map.put("billName", billName);
			map.put("custCd", custCd);
			map.put("accNo", accNo);
			map.put("userAccNo", userAccNo);
			map.put("uaNoPkgs", "" + uaNoPkgs);
			map.put("clsShpInd", clsShpInd);
			map.put("pkgsDesc", pkgsDesc);
			map.put("weight", "" + weight);
			map.put("volume", "" + volume);
			map.put("chkClsShpInd", chkClsShpInd);
			map.put("stuffind", CommonUtility.deNull(criteria.getPredicates().get("stuffind")));
			map.put("listTrucker", truckerList);
			map.put("ListData", topsModel);
			
			// START CR FTZ HSCODE - NS JULY 2024
			List<HsCodeDetails> hscodeDetailsList = tesnPsaJpService.getHsCodeDetailList(esnNo);
			map.put("hscodeDetailsList", hscodeDetailsList);
			// END CR FTZ HSCODE - NS JULY 2024

		} catch (BusinessException be) {
			log.info("Exception TesnPsaJpAmendConfirm: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnPsaJpAmendConfirm : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: TesnPsaJpAmendConfirm result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.tesn.tesnpsajp --> TesnPsaJpCancelHandler
	@PostMapping(value = "/TesnPsaJpCancel")
	public ResponseEntity<?> TesnPsaJpCancel(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: TesnPsaJpCancel criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			// String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String esnNo = "";
			String bookingRefNo = "";
			bookingRefNo = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo"));
			esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			/*
			 * String userID = "GSUSER"; String coCd = "GSL";
			 */
			// String custCd = "C1";
			TesnPsaJpEsnListValueObject esnListValueObject = null;

			// Added UserID by Revathi
			if (esnNo != null) {
				// Sripriya 15 July 2016 added to not delete if in shuout status
				if (tesnPsaJpService.isAsnShut(esnNo)) {
					errorMessage = ConstantUtil.ErrorMsg_Cancel_TESN_AFter_Shutout;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpCancel result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				tesnPsaJpService.esnCancel(esnNo, bookingRefNo, UserID);
			}
			esnListValueObject = new TesnPsaJpEsnListValueObject();
			topsModel.put(esnListValueObject);

			map.put("ListData", topsModel);

		} catch (BusinessException be) {
			log.info("Exception TesnPsaJpCancel: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnPsaJpCancel : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: TesnPsaJpCancel result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.tesn.tesnpsajp --> TesnPsaJpAddHandler
	@PostMapping(value = "/TesnPsaJpAdd")
	public ResponseEntity<?> TesnPsaJpAdd(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: TesnPsaJpAdd criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			//String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String bookingRefNo = "";
			// String coCd = "C1";
			bookingRefNo = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo").trim());
			TesnPsaJpEsnListValueObject esnListValueObject = null;

			String vslType = tesnPsaJpService.getVesselType(bookingRefNo);
			map.put("vslType", vslType);
			if (vslType.equals("CC")) {
				List<Map<String, Object>> list = tesnPsaJpService.getCategoryList();
				map.put("categoryList", list);
			}
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			List<String> hsCodeList = new ArrayList<String>();

			List<ManifestValueObject> listManifestValueObject = tesnPsaJpService.getHSCodeList("1");
			if (listManifestValueObject != null && listManifestValueObject.size() > 0) {
				String hsCode = "";
				for (int i = 0; i < listManifestValueObject.size(); i++) {
					ManifestValueObject manifestValueObject = new ManifestValueObject();
					manifestValueObject = (ManifestValueObject) listManifestValueObject.get(i);
					hsCode = manifestValueObject.getHsCode();
					hsCodeList.add(hsCode);
				}
			}
			map.put("hsCodeList", hsCodeList);
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			List<TesnPsaJpEsnListValueObject> bookingRefList = new ArrayList<TesnPsaJpEsnListValueObject>();
			bookingRefList = tesnPsaJpService.getBkRefNo(bookingRefNo, coCd);
			if (bookingRefList != null && bookingRefList.size() != 0) {
				String esnDeclared = tesnPsaJpService.getEsnDeclared(bookingRefNo);
				String bkStatus = tesnPsaJpService.getBkStatus(bookingRefNo);
				String esnDeclarentCode = tesnPsaJpService.getDeclarentCd(bookingRefNo);
				String vvStatus = tesnPsaJpService.getVvStatus(bookingRefNo);
				String closedVslInd = tesnPsaJpService.getClsVslInd(bookingRefNo);
				//String closedBjInd = tesnPsaJpService.getClsBjInd(bookingRefNo);

				/*
				 * if(bookingRefList == null) { errorMessage(request,
				 * "Invalid Booking Reference Number"); return; }
				 */
				/*
				 * if(bookingRefList.size() == 0) { errorMessage(request,
				 * "Invalid Booking Reference Number"); return; }
				 */
				if (esnDeclared.equals("Y")) {
					errorMessage = ConstantUtil.ErrorMsg_BkRef_Used;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				if (bkStatus.equals("X")) {
					errorMessage = "The Booking Reference with BR. No. " + bookingRefNo
							+ "has been canceled.  Please use a different BR. No.";
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				if (!coCd.equals("JP")) {
					if (vvStatus.equals("UB")) {
						errorMessage = ConstantUtil.ErrorMsg_ESN_Unberth;
						if (errorMessage != null) {
							map.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(map);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: TesnPsaJpAdd result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
					if (!esnDeclarentCode.equals(coCd)) {
						errorMessage = ConstantUtil.ErrorMsg_Not_Authorized_BrNbr;
						if (errorMessage != null) {
							map.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(map);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: TesnPsaJpAdd result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				}
				if (vvStatus.equals("CX")) {
					errorMessage = ConstantUtil.ErrorMsg_BerthApp_Cancel;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				} else if (vvStatus.equals("CL")) {
					errorMessage = ConstantUtil.ErrorMsg_BR_Vessel_Record_Closed;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				if (closedVslInd.equals("Y")) {
					errorMessage = ConstantUtil.ErrorMsg_Vessel_Record_Closed;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}

				/*
				 * if(closedBjInd.equals("Y")){ errorMessage(request,
				 * "The Record for this vessel is closed.  No more Records can be added");
				 * return; }
				 */
				// esnDeclared = tesnPsaJpService.getEsnDeclared(bookingRefNo);
			} else {
				errorMessage = ConstantUtil.ErrorMsg_BR_Cannot_Used;
				if (errorMessage != null) {
					map.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(map);
					result.setSuccess(false);
					result.setData(map);

				} else {
					result.setData(map);
					result.setSuccess(true);
					log.info("END: TesnPsaJpAdd result: " + result.toString());
				}

				return ResponseEntityUtil.success(result.toString());
			}

			BookingReferenceValueObject brvo = (BookingReferenceValueObject) tesnPsaJpService
					.fetchBKDetails(bookingRefNo).get(0);
			String cargoCategoryCode = brvo.getCargoCategory();
			esnListValueObject = new TesnPsaJpEsnListValueObject();
			for (int i = 0; i < bookingRefList.size(); i++) {
				esnListValueObject = (TesnPsaJpEsnListValueObject) bookingRefList.get(i);
				esnListValueObject.setCategoryValue(tesnPsaJpService.getCategoryValue(cargoCategoryCode));
				esnListValueObject.setCategory(cargoCategoryCode);
				topsModel.put(esnListValueObject);
			}
			// added on 30/08/2002 by thiru-----
			// String bookingRefNbr =
			// CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo");
			String edi = CommonUtility.deNull(criteria.getPredicates().get("edi"));
			List<TesnPsaJpEsnListValueObject> ediDetails = new ArrayList<TesnPsaJpEsnListValueObject>();
			if (edi != null && !edi.equals("") && edi.equals("edi")) {
				log.info("bookingRefNo--" + bookingRefNo);
				ediDetails = tesnPsaJpService.getEdiDetails(bookingRefNo);
			}
			map.put("ediDetails", ediDetails);
			//// ----------------
			// added on 13/01/2014 by VietNguyen-----: START
			String trucker = CommonUtility.deNull(criteria.getPredicates().get("trucker"));
			TruckerValueObject truckerValueObject = new TruckerValueObject();
			if (trucker != null && !trucker.equals("") && trucker.equals("trucker")) {
				String truckerIc = CommonUtility.deNull(criteria.getPredicates().get("truckerIcParam"));

				int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
				List<String> listTruckerIc =  new ArrayList<String>(); 
				String truckerIcNo;
				List<String> listTruckerNm =  new ArrayList<String>(); 
				String truckerNmNo;
				List<String> listTruckerContact =  new ArrayList<String>(); 
				String truckerContactNo;
				List<String> listTruckerPkgs =  new ArrayList<String>(); 
				String truckerPkgsNo;
				List<String> listTruckerCd =  new ArrayList<String>(); 
				String truckerCdNo;
				
				if (sizeSelected != 0) {
					for (int i = 0; i < sizeSelected; i++) {
						truckerIcNo = (String) criteria.getPredicates().get("truckerIc" + i);
						listTruckerIc.add(truckerIcNo);
						truckerNmNo = (String) criteria.getPredicates().get("truckerNm" + i);
						listTruckerNm.add(truckerNmNo);
						truckerContactNo = (String) criteria.getPredicates().get("truckerContact" + i);
						listTruckerContact.add(truckerContactNo);
						truckerPkgsNo = (String) criteria.getPredicates().get("truckerPkgs" + i);
						listTruckerPkgs.add(truckerPkgsNo);
						truckerCdNo = (String) criteria.getPredicates().get("truckerCd" + i);
						listTruckerCd.add(truckerCdNo);
					}
				}
				
				List<TruckerValueObject> truckerList = new ArrayList<TruckerValueObject>();
				if (listTruckerIc != null) {
					for (int i = 0; i < listTruckerIc.size(); i++) {
						truckerValueObject = new TruckerValueObject();
						truckerValueObject.setTruckerIc(listTruckerIc.get(i));
						truckerValueObject.setTruckerNm(listTruckerNm.get(i));
						truckerValueObject.setTruckerContact(listTruckerContact.get(i));
						truckerValueObject.setTruckerPkgs(listTruckerPkgs.get(i));
						truckerValueObject.setTruckerCd(listTruckerCd.get(i));
						truckerList.add(truckerValueObject);
					}
				}
				String truckerDelete = CommonUtility.deNull(criteria.getPredicates().get("truckerDelete"));
				if (!"delete".equalsIgnoreCase(truckerDelete)) {
					String truckerRow = CommonUtility.deNull(criteria.getPredicates().get("truckerRow"));
					int row = 0;
					if (truckerRow != null) {
						row = Integer.parseInt(truckerRow);
					}
					truckerValueObject = tesnPsaJpService.getTruckerDetails(truckerIc);
					String truckerPkgs = CommonUtility.deNull(criteria.getPredicates().get("truckerPkgsParam"));
					truckerValueObject.setTruckerPkgs(truckerPkgs);
					if (truckerList.size() < row) {
						for (int i = 0; i < row - 1; i++) {
							truckerList.add(new TruckerValueObject());
						}
						truckerList.add(truckerValueObject);
					} else {
						truckerList.set(row, truckerValueObject);
					}
				}
				map.put("listTrucker", truckerList);
				map.put("amendtrucker", "amendtrucker");
				map.put("firstCName", CommonUtility.deNull(criteria.getPredicates().get("firstCName")));
				map.put("inVoyageNo", CommonUtility.deNull(criteria.getPredicates().get("inVoyageNo")));
				map.put("pkgsType", CommonUtility.deNull(criteria.getPredicates().get("pkgsType")));
				map.put("noOfPkgs1", "" + CommonUtility.deNull(criteria.getPredicates().get("noOfPkgs")));
				map.put("cargoDesc", CommonUtility.deNull(criteria.getPredicates().get("cargoDesc")));
				map.put("hsCode", CommonUtility.deNull(criteria.getPredicates().get("hsCode")));
				map.put("hsSubCode", CommonUtility.deNull(criteria.getPredicates().get("hsSubCode")));
				map.put("hsSubCodeDesc", CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDesc")));
				map.put("hsSubCodeFr", CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr")));
				map.put("hsSubCodeTo", CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo")));
				map.put("customHsCode", CommonUtility.deNull(criteria.getPredicates().get("customHsCode")));
				map.put("weightTrucker", "" + CommonUtility.deNull(criteria.getPredicates().get("weight")));
				map.put("volumeTrucker", "" + CommonUtility.deNull(criteria.getPredicates().get("volume")));
				map.put("marking", CommonUtility.deNull(criteria.getPredicates().get("marking")));
				map.put("dgInd", CommonUtility.deNull(criteria.getPredicates().get("dgInd")));
				map.put("lopInd", CommonUtility.deNull(criteria.getPredicates().get("lopInd")));
				map.put("storageInd", CommonUtility.deNull(criteria.getPredicates().get("storageInd")));
				map.put("noOfStorageDay", CommonUtility.deNull(criteria.getPredicates().get("noOfStorageDay")));
				map.put("category", CommonUtility.deNull(criteria.getPredicates().get("category")));
				String noCntrs = CommonUtility.deNull(criteria.getPredicates().get("noOfContainer"));
				if (StringUtils.isNotBlank(noCntrs)) {
					Integer n = Integer.parseInt(noCntrs);
					for (int i = 1; i <= n; i++) {
						map.put("cntrNo" + i, CommonUtility.deNull(criteria.getPredicates().get("contNo")) + i);
					}
				}
				map.put("trucker", "trucker");
			}
			// added on 13/01/2014 by VietNguyen-----: END

			map.put("custCd", coCd);

			map.put("topsModel", topsModel);

		} catch (BusinessException be) {
			log.info("Exception TesnJpJpAdd: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnJpJpAdd : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: TesnPsaJpAdd result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.tesn.tesnpsajp --> TesnPsaJpAddConfirmHandler
	@PostMapping(value = "/TesnPsaJpAddConfirm")
	public ResponseEntity<?> TesnPsaJpAddConfirm(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: TesnPsaJpAddConfirm criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			/*
			 * String userID = "GSUSER"; String coCd = "GSL";
			 */
			String bookingRefNo = "";
			// String custCd = "";
//			String voyNo = "";
			int noOfPkgs = 0;
			String hsCode = "";
			String hsSubCode = "";
//			String hsSubCodeDesc = "";
			String hsSubCodeFr = "";
			String hsSubCodeTo = "";
			String customHsCode = "";
//			String crgType = "";
			String pkgsType = "";
			String pkgsDesc = "";
			double weight = 0;
			double volume = 0;
			String cargoDesc = "";
			String marking = "";
			String loadOperInd = "";
			// String loadFrom = "";
			String dgInd = "";
			String storageInd = "";
			int noOfStorageDay = 0;
			String firstCName = "";
			String inVoyageNo = "";
			String portD = "";
			// String dutyGoodInd = "";
			String accNo_I = "";
			String payMode = "";
			String insert = "";
//			String crgTypeCd = "";
			String varNbr = "";
			String esnNo = "";
			String billName = "";
			String cntr1 = "";
			String cntr2 = "";
			String cntr3 = "";
			String cntr4 = "";
			int uaNoPkgs = 0;
			String clsShpInd = "";
			String deliveryToEPC = "";

			boolean checkNoOfPkgs;
//			boolean checkHsCode;
			boolean checkPkgsType;
			boolean checkWeight;
			boolean checkVolume;
			boolean checkAccNo;
//			boolean checkTruckerName;
			// VietNguyen (FPT) Document Process Enhancement 06-Dec-2013: START
			boolean checkFirstCarrierVsl;
			// VietNguyen (FPT) Document Process Enhancement 06-Dec-2013: END

			String category = "00";
			if (!StringUtils.isEmpty(criteria.getPredicates().get("category"))) {
				category = CommonUtility.deNull(criteria.getPredicates().get("category"));
			}
			;
			map.put("category", category);

			varNbr = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));
			bookingRefNo = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo")).trim();
			// custCd = CommonUtility.deNull(criteria.getPredicates().get("custCd").trim();
//			voyNo = CommonUtility.deNull(criteria.getPredicates().get("inVoyageNo")).trim();
			String noOf_pkgs = CommonUtility.deNull(criteria.getPredicates().get("noOfPkgs"));
			String storageDay = CommonUtility.deNull(criteria.getPredicates().get("noOfStorageDay"));
			if (storageDay != null && !storageDay.equals(""))
				noOfStorageDay = Integer.parseInt(storageDay);
			portD = CommonUtility.deNull(criteria.getPredicates().get("portD"));
			if (noOf_pkgs != null && !noOf_pkgs.equals(""))
				noOfPkgs = Integer.parseInt(noOf_pkgs);
			hsCode = CommonUtility.deNull(criteria.getPredicates().get("hsCode"));

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			hsSubCode = CommonUtility.deNull(criteria.getPredicates().get("hsSubCode"));
//			hsSubCodeDesc = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDesc"));
			hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
			hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			customHsCode = CommonUtility.deNull(criteria.getPredicates().get("customHsCode"));

			if (hsSubCode == null || hsSubCodeFr == null || hsSubCodeTo == null
					|| hsSubCodeFr.trim().equalsIgnoreCase("") || hsSubCode.trim().equalsIgnoreCase("")
					|| hsSubCodeTo.trim().equalsIgnoreCase("")) {
				errorMessage = ConstantUtil.ErrorMsg_HSsubCode_Null;

				if (errorMessage != null) {
					map.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(map);
					result.setSuccess(false);
					result.setData(map);

				} else {
					result.setData(map);
					result.setSuccess(true);
					log.info("END: TesnPsaJpAddConfirm result: " + result.toString());
				}

				return ResponseEntityUtil.success(result.toString());
			}

//			crgType = CommonUtility.deNull(criteria.getPredicates().get("crgType"));
			// log.info("crgType"+crgType);
			pkgsType = CommonUtility.deNull(criteria.getPredicates().get("pkgsType")).trim();
			String weight_s = CommonUtility.deNull(criteria.getPredicates().get("weight"));
			if (weight_s != null && !weight_s.equals(""))
				weight = Double.parseDouble(weight_s);
			// log.info("weight "+weight);
			String volume_s = CommonUtility.deNull(criteria.getPredicates().get("volume"));
			if (volume_s != null && !volume_s.equals(""))
				volume = Double.parseDouble(volume_s);
			cargoDesc = CommonUtility.deNull(criteria.getPredicates().get("cargoDesc")).trim();

			// log.info("cargoDesc"+cargoDesc);
			marking = CommonUtility.deNull(criteria.getPredicates().get("marking")).trim();

			// check cargo desc and markings to not exceed 200 characters

			if (cargoDesc != null && cargoDesc.length() > 4000) {
				String[] tmpStrings = { "Cargo Description" };
				errorMessage = CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_invalidLength, tmpStrings);
			}
			if (marking != null && marking.length() > 200) {
				errorMessage = ConstantUtil.ErrorMsg_Cargo_Marking_Length;
			}
			// log.info("marking"+marking);
			loadOperInd = CommonUtility.deNull(criteria.getPredicates().get("lopInd"));
			// loadFrom=CommonUtility.deNull(criteria.getPredicates().get("loadFrom");
			// log.info("loadFrom"+loadFrom);
			dgInd = CommonUtility.deNull(criteria.getPredicates().get("dgInd"));
			// dutyGoodInd =
			// CommonUtility.deNull(criteria.getPredicates().get("dutyGoodInd");
			// log.info("dgInd"+dgInd);
			storageInd = CommonUtility.deNull(criteria.getPredicates().get("storageInd"));
			inVoyageNo = CommonUtility.deNull(criteria.getPredicates().get("inVoyageNo")).trim();
			firstCName = CommonUtility.deNull(criteria.getPredicates().get("firstCName")).trim();
			// truckerCNo =
			// CommonUtility.deNull(criteria.getPredicates().get("truckerCNo").trim();
			insert = CommonUtility.deNull(criteria.getPredicates().get("insert"));
			cntr1 = CommonUtility.deNull(criteria.getPredicates().get("cntr1"));
			cntr2 = CommonUtility.deNull(criteria.getPredicates().get("cntr2"));
			cntr3 = CommonUtility.deNull(criteria.getPredicates().get("cntr3"));
			cntr4 = CommonUtility.deNull(criteria.getPredicates().get("cntr4"));

			deliveryToEPC = CommonUtility.deNull(criteria.getPredicates().get("deliveryToEPC")); // MCC for EPC IND

			// START CR FTZ HSCODE - NS JULY 2024

			int hsCodeSize = Integer.valueOf(CommonUtility.deNull(criteria.getPredicates().get("hsCodeSize")));
			List<HsCodeDetails> multiHsCodeList = new ArrayList<HsCodeDetails>();
			HsCodeDetails hsCodeDetails = new HsCodeDetails();
			double totalWt = 0;
			double totalVol = 0;				
			if (hsCodeSize > 0) {
				for (int i = 0; i < hsCodeSize; i++) {
					hsCodeDetails = new HsCodeDetails();
					hsCodeDetails.setCrgDes(CommonUtility.deNull(criteria.getPredicates().get("CrgDescArr" + i)));
					hsCodeDetails.setHsCode(CommonUtility.deNull(criteria.getPredicates().get("HsCodeArr" + i)));
					hsCodeDetails.setNbrPkgs(CommonUtility.deNull(criteria.getPredicates().get("NoOfPKgsArr" + i)));
					hsCodeDetails.setCustomHsCode(CommonUtility.deNull(criteria.getPredicates().get("customHsCodeArr" + i)));
					hsCodeDetails.setGrossWt(CommonUtility.deNull(criteria.getPredicates().get("gwtArr" + i)));
					if ((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).indexOf("-") == -1) {
						hsCodeDetails.setHsSubCodeFr(
								(CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))));
						hsCodeDetails.setHsSubCodeTo(
								(CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))));
					} else {
						hsCodeDetails.setHsSubCodeFr(
								(CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).split("-")[0]);
						hsCodeDetails.setHsSubCodeTo(
								(CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).split("-")[1]);
					}
					hsCodeDetails.setGrossVol(CommonUtility.deNull(criteria.getPredicates().get("mSmtArr" + i)));
					hsCodeDetails.setHsSubCodeDesc(
							CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDescArr" + i)));
					multiHsCodeList.add(hsCodeDetails);
					

					totalWt += Double.valueOf(CommonUtility.deNull(criteria.getPredicates().get("gwtArr" + i)));
					totalVol += Double.valueOf(CommonUtility.deNull(criteria.getPredicates().get("mSmtArr" + i)));
				}
				
			}

			// END CR FTZ HSCODE - NS JULY 2024

			TesnPsaJpEsnListValueObject esnListValueObject = null;

			// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: START
			checkFirstCarrierVsl = tesnPsaJpService.chkFirstCarrierVsl(firstCName, inVoyageNo);
			if (!checkFirstCarrierVsl) {
				errorMessage = ConstantUtil.ErrorMsg_Voy_Vessel_Invalid_First;
				if (errorMessage != null) {
					map.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(map);
					result.setSuccess(false);
					result.setData(map);

				} else {
					result.setData(map);
					result.setSuccess(true);
					log.info("END: TesnPsaJpAddConfirm result: " + result.toString());
				}

				return ResponseEntityUtil.success(result.toString());
			}
			if (!"JP".equalsIgnoreCase(custCd)) {
				boolean checkAtuSecondCarrierVsl = tesnPsaJpService.chkDttmOfSecondCarrierVsl(varNbr);
				if (!checkAtuSecondCarrierVsl) {
					errorMessage = ConstantUtil.ErrorMsg_Unable_Create_After_ATU;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}
			// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: END

			/*
			 * PM$ check done by karthi on 08/03/04
			 */

			if (CommonUtility.deNull(dgInd).equalsIgnoreCase("Y")) {
				if (!tesnPsaJpService.chkOutwardPM4(bookingRefNo, varNbr)) {
					errorMessage = ConstantUtil.ErrorMsg_TESN_Creation_NoApproved_MP4;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}

			if (noOf_pkgs != null && !noOf_pkgs.equals("")) {
				checkNoOfPkgs = tesnPsaJpService.chkNoOfPkgs(bookingRefNo, noOfPkgs);
				if (!checkNoOfPkgs) {
					errorMessage = ConstantUtil.ErrorMsg_Package_Greater;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}

			if (pkgsType != null && !pkgsType.equals("")) {
				checkPkgsType = tesnPsaJpService.chkPkgsType(pkgsType);
				if (!checkPkgsType) {
					errorMessage = "Package code is not valid";
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}
			if (weight_s != null && !weight_s.equals("")) {
				checkWeight = tesnPsaJpService.chkWeight(bookingRefNo, weight);
				if (!checkWeight) {
					errorMessage = ConstantUtil.ErrorMsg_Weight_Greater;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}
			if (volume_s != null && !volume_s.equals("")) {
				checkVolume = tesnPsaJpService.chkVolume(bookingRefNo, volume);
				if (!checkVolume) {
					errorMessage = ConstantUtil.ErrorMsg_Volume_Greater;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: TesnPsaJpAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}

			// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: START
			List<TruckerValueObject> truckerList = new ArrayList<TruckerValueObject>();
			TruckerValueObject truckerValueObject = null;
			
			int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
			List<String> listTruckerIc =  new ArrayList<String>(); 
			String truckerIcNo;
			List<String> listTruckerNm =  new ArrayList<String>(); 
			String truckerNmNo;
			List<String> listTruckerContact =  new ArrayList<String>(); 
			String truckerContactNo;
			List<String> listTruckerPkgs =  new ArrayList<String>(); 
			String truckerPkgsNo;
			List<String> listTruckerCd =  new ArrayList<String>(); 
			String truckerCdNo;
			
			if (sizeSelected != 0) {
				for (int i = 0; i < sizeSelected; i++) {
					truckerIcNo = (String) criteria.getPredicates().get("truckerIc" + i);
					listTruckerIc.add(truckerIcNo);
					truckerNmNo = (String) criteria.getPredicates().get("truckerNm" + i);
					listTruckerNm.add(truckerNmNo);
					truckerContactNo = (String) criteria.getPredicates().get("truckerContact" + i);
					listTruckerContact.add(truckerContactNo);
					truckerPkgsNo = (String) criteria.getPredicates().get("truckerPkgs" + i);
					listTruckerPkgs.add(truckerPkgsNo);
					truckerCdNo = (String) criteria.getPredicates().get("truckerCd" + i);
					listTruckerCd.add(truckerCdNo);
				}
			}
			
			if (listTruckerIc != null) {
				for (int i = 0; i < listTruckerIc.size(); i++) {
					truckerValueObject = new TruckerValueObject();
					truckerValueObject.setTruckerIc(listTruckerIc.get(i));
					truckerValueObject.setTruckerNm(listTruckerNm.get(i));
					truckerValueObject.setTruckerContact(listTruckerContact.get(i));
					truckerValueObject.setTruckerPkgs(listTruckerPkgs.get(i));
					truckerValueObject.setTruckerCd(listTruckerCd.get(i));
					truckerList.add(truckerValueObject);
					// if(!tesnPsaJpService.chkValidTrucker(truckerValueObject)) {
					// errorMessage(request, "The Trucker Ic Number " +
					// truckerValueObject.getTruckerIc() + " invalid");
					// return;
					// }
				}
			}
			log.info("=========test=====" + truckerList.size());
			// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: START

			List<TesnPsaJpEsnListValueObject> accNoList = new ArrayList<TesnPsaJpEsnListValueObject>();
			List<TesnPsaJpEsnListValueObject> UserAccNoList = new ArrayList<TesnPsaJpEsnListValueObject>();
			List<String> userAccNo = new ArrayList<String>();
			List<String> accNo = new ArrayList<String>();
			String getAccNo = "";
			accNoList = tesnPsaJpService.getAccNo(varNbr);
			if (accNoList.size() != 0) {
				for (int i = 0; i < accNoList.size(); i++) {
					esnListValueObject = new TesnPsaJpEsnListValueObject();
					esnListValueObject = (TesnPsaJpEsnListValueObject) accNoList.get(i);
					accNo.add(esnListValueObject.getAccNo());
				}
			}
			if (accNoList.size() != 0)
				getAccNo = esnListValueObject.getAccNo();
			else
				getAccNo = "No";
			UserAccNoList = tesnPsaJpService.getUserAccNo(bookingRefNo, custCd, getAccNo);
			if (UserAccNoList.size() != 0) {
				for (int i = 0; i < UserAccNoList.size(); i++) {
					esnListValueObject = new TesnPsaJpEsnListValueObject();
					esnListValueObject = (TesnPsaJpEsnListValueObject) UserAccNoList.get(i);
					userAccNo.add(esnListValueObject.getAccNo());
				}
			}
			if (insert != null && insert.equals("insert")) {
				// crgTypeCd = tesnPsaJpService.getCrgTypeCd(crgType);
				// String crNo = tesnPsaJpService.getTdbCRNo(custCd);
				accNo_I = CommonUtility.deNull(criteria.getPredicates().get("accNo"));
				payMode = CommonUtility.deNull(criteria.getPredicates().get("payMode"));
				checkAccNo = tesnPsaJpService.chkAccNo(accNo_I);

				String categoryValue = tesnPsaJpService.getCategoryValue(category);
				map.put("categoryValue", categoryValue);
				if (accNo_I.equals("CASH") || accNo_I.equals("CA")) {
				} else {
					if (!checkAccNo) {
						errorMessage = ConstantUtil.ErrorMsg_Invalid_Billable_AccNo;
						if (errorMessage != null) {
							map.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(map);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: TesnPsaJpAddConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				}
				esnNo = (String) tesnPsaJpService.insertEsnDetailsForDPE(UserID, varNbr, custCd, bookingRefNo, marking,
						portD, loadOperInd, dgInd, hsCode, noOfStorageDay, storageInd, pkgsType, noOfPkgs, weight,
						volume, accNo_I, payMode, cargoDesc, cntr1, cntr2, cntr3, cntr4, firstCName, inVoyageNo,
						CommonUtility.deNull(criteria.getPredicates().get("stuffind")), category, hsSubCodeFr,
						hsSubCodeTo, truckerList, deliveryToEPC, customHsCode, multiHsCodeList); // MCC for EPC IND
				pkgsDesc = (String) tesnPsaJpService.getPkgsDesc(esnNo);
				uaNoPkgs = (int) tesnPsaJpService.getUaNoPkgs(esnNo);
				clsShpInd = (String) tesnPsaJpService.getClsShipInd(varNbr);
				billName = (String) tesnPsaJpService.getBillablePartyName(accNo_I);
				// Modified by thiru 06/11/2002
				String k = tesnPsaJpService.createNomVesselPsaJp(firstCName, inVoyageNo, UserID);
				log.info("k: " + k);
			}
			// --Thiru added oct 10
			String chkClsShpInd = tesnPsaJpService.getClsShipInd_bkr(bookingRefNo);

			map.put("billName", billName);
			map.put("esnNo", esnNo);
			map.put("custCd", custCd);
			map.put("accNo", accNo);
			map.put("pkgsDesc", pkgsDesc);
			map.put("userAccNo", userAccNo);
			map.put("uaNoPkgs", "" + uaNoPkgs);
			map.put("clsShpInd", clsShpInd);
			map.put("pkgsDesc", pkgsDesc);
			map.put("weight", "" + weight);
			map.put("volume", "" + volume);
			map.put("chkClsShpInd", chkClsShpInd);
			map.put("stuffind", CommonUtility.deNull(criteria.getPredicates().get("stuffind")));
			map.put("listTrucker", truckerList);
			map.put("deliveryToEPC", deliveryToEPC);

			map.put("ListData", topsModel);
			
			// START CR FTZ HSCODE - NS JULY 2024
			List<HsCodeDetails> hscodeDetailsList = tesnPsaJpService.getHsCodeDetailList(esnNo);
			map.put("hscodeDetailsList", hscodeDetailsList);
			// END CR FTZ HSCODE - NS JULY 2024

		} catch (BusinessException be) {
			log.info("Exception TesnPsaJpAddConfirm: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnPsaJpAddConfirm : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: TesnPsaJpAddConfirm result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.containerised.ua --> UAEsnSearch --> processRequest
	@PostMapping(value = "/UAEsnSearch")
	public ResponseEntity<?> UAEsnSearch(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;

		try {
			log.info("START: UAEsnSearch criteria:" + criteria.toString());

			//String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			//String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String disp = CommonUtility.deNull(criteria.getPredicates().get("disp"));
			String esn_No = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			String trans_type = CommonUtility.deNull(criteria.getPredicates().get("transtype"));
			String ftran_sdtm = CommonUtility.deNull(criteria.getPredicates().get("ftransdtm"));
			String esnasnnbr = CommonUtility.deNull(criteria.getPredicates().get("esnasnnbr"));

			String esn_asn_nbr = "";
			String sysdate = "";
			String dispval = disp;
			String esnNo = esn_No;
			String transtype = trans_type;
			String ftransdtm = ftran_sdtm;

			List<UaEsnDetValueObject> esnView = new ArrayList<UaEsnDetValueObject>();
			List<UaListObject> uaList = new ArrayList<UaListObject>();

			// lak added for updftrans 11/02/2003
			if (dispval != null && !dispval.equals("") && dispval.equals("Updftrans")) {
				tesnPsaJpService.updFtrans(esnNo, transtype, ftransdtm);
			}
			try {
				if (esnasnnbr != null) {
					esn_asn_nbr = esnasnnbr;
				}
			} catch (Exception e) {
				log.info("Exception UAEsnSearch : ", e);
				throw new BusinessException("M4201");
			}

			List<UaEsnListValueObject> esnlist = new ArrayList<UaEsnListValueObject>();
			esnlist = tesnPsaJpService.getEsnList(esn_asn_nbr);

			if (dispval != null && !dispval.equals("") && (dispval.equals("View") || dispval.equals("Updftrans"))) {
				esnView = tesnPsaJpService.getEsnView(esnNo, transtype);
				uaList = tesnPsaJpService.getUAList(esnNo);
				/*
				 * request.setAttribute("esnView", esnView); request.setAttribute("uaList",
				 * uaList);
				 */
				map.put("esnView", esnView);
				map.put("uaList", uaList);
			}

			// request.setAttribute("esnlist", esnlist);
			map.put("esnlist", esnlist);
			sysdate = tesnPsaJpService.getSysdate();
			// request.setAttribute("sysdate", sysdate);
			map.put("sysdate", sysdate);

			if (dispval != null && !dispval.equals("") && (dispval.equals("View") || dispval.equals("Updftrans"))) {
				// nextScreen(request, "CntrUAEsnView");
				map.put("screen", "CntrUAEsnView");
			} else {
				// nextScreen(request, "CntrUAFPSvlt");
				map.put("screen", "CntrUAFPSvlt");
			}

		} catch (BusinessException be) {
			log.info("Exception UAEsnSearch: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception UAEsnSearch : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: UAEsnSearch result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
}
