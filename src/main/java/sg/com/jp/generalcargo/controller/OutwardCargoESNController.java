package sg.com.jp.generalcargo.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaEsnListValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoESNService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = OutwardCargoESNController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OutwardCargoESNController {

	public static final String ENDPOINT = "gc/outwardcargo/ESN";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(OutwardCargoESNController.class);
	public final int MAX_ADP_TRUCKER = 5; 
	
	@Autowired
	private OutwardCargoESNService eSNService;

	// delegate.helper.gbms.cargo.esn --> EsnListHandler
	@PostMapping(value = "/EsnList")
	public ResponseEntity<?> EsnList(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: EsnList criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			// String UserID =
			// CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			// String UserID = accessUserValueObject.getLogonID();
			// String custCd = accessUserValueObject.getCompanyCode();
			log.info("custCd----" + custCd);
			// String custCode = accessUserValueObject.getCompanyCode()
			String selVoyno = "";
			String esnNo = "";
			esnNo = criteria.getPredicates().get("esnNo");
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
			String fetchVesselName = null;// Added by thanhnv2
			String fetchVoyageNbr = null;// Added by thanhnv2
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
					fetchVesselName = CommonUtility.deNull(criteria.getPredicates().get("vesselName")).toUpperCase();
					fetchVoyageNbr = CommonUtility.deNull(criteria.getPredicates().get("voyageNbr")).toUpperCase();
					map.put("fetchVesselName", fetchVesselName); // Added by thanhnv2
					map.put("fetchVoyageNbr", fetchVoyageNbr); // Added by thanhnv2
					// Added vietnd02
					isFetch = "TRUE";
					map.put("isFetchmode", isFetch);
					// vietnd02::end
				} else {
					// log.info("Invalid vessel voyage values.");
					// errorMessage(request, "M4204");
				}
			}
			/*
			 * << Add by FPT.Thai - Oct 02 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */

			VesselVoyValueObject vesselVoyValueObject = null;
			EsnListValueObject esnListValueObject = null;

			List<VesselVoyValueObject> vesselSel = new ArrayList<VesselVoyValueObject>();
			List<EsnListValueObject> esnList = new ArrayList<EsnListValueObject>();
			List<String> esnNbr = new ArrayList<String>();
			List<String> shipperName = new ArrayList<String>();
			List<String> crgDesc = new ArrayList<String>();
			List<String> noOfPkgs = new ArrayList<String>();
			List<String> weight = new ArrayList<String>();
			List<String> volume = new ArrayList<String>();
			List<String> bookingRefNbr = new ArrayList<String>();
			List<String> stfInd = new ArrayList<String>();
			// add by Zhenguo Deng on 14/02/2011 for Cargo Category
			List<String> categoryList = new ArrayList<String>();
			List<String> crgType = new ArrayList<String>();
			// end add
			// HaiTTH1 added on 19/3/2014
			List<String> scheme = new ArrayList<String>();
			// HaiTTH1 ended on 19/3/2014
			// MCC for EPC_IND
			List<String> epcindlist = new ArrayList<String>();
			List<String> terminalList = new ArrayList<String>();
			List<String> subSchemeList = new ArrayList<String>();
			List<String> gcOperationsList = new ArrayList<String>();

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			List<String> hsCodeList = new ArrayList<String>();
			String hsCode = "";
			String hsSubCodeFr = "";
			String hsSubCodeTo = "";
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			vesselSel = eSNService.getVesselList(custCd);

			vesselVoyValueObject = new VesselVoyValueObject();
			for (int i = 0; i < vesselSel.size(); i++) {
				vesselVoyValueObject = (VesselVoyValueObject) vesselSel.get(i);
				topsModel.put(vesselVoyValueObject);
			}

			List<String> vesselListName = new ArrayList<String>();
			List<String> vesselListVoyNo = new ArrayList<String>();
			List<String> vesselListVarNbr = new ArrayList<String>();
			List<String> vesselListTerminal = new ArrayList<String>();

			for (int i = 0; i < topsModel.getSize(); i++) {
				vesselVoyValueObject = (VesselVoyValueObject) topsModel.get(i);
				String vname = "";
				String voyno = "";
				String varNbr = "";
				String terminal = "";
				varNbr = vesselVoyValueObject.getVarNbr();
				vname = vesselVoyValueObject.getVslName();
				voyno = vesselVoyValueObject.getVoyNo();
				terminal = vesselVoyValueObject.getTerminal();
				vesselListName.add(vname);
				vesselListVoyNo.add(voyno);
				vesselListVarNbr.add(varNbr);
				vesselListTerminal.add(terminal);

			}

			map.put("VslName", vesselListName);
			map.put("VoyNo", vesselListVoyNo);
			map.put("VarNbr", vesselListVarNbr);
			map.put("varNbr", selVoyno);
			map.put("terminal", vesselListTerminal);

			/*
			 * >> Add by FPT.Thai - Oct 02 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */

			// HaiTTH1 added on 5/1/2014
			String arrival = "";
			String departure = "";
			String col = "";
			String etb = "";
			if (isFetchmode) {
				vesselVoyValueObject = eSNService.getVessel(fetchVesselName, fetchVoyageNbr, custCd);
				if (null != vesselVoyValueObject) {
					// topsModel.put(vesselVoyValueObject); //--Add more Vessel into topsModel to FW
					// request
					selVoyno = vesselVoyValueObject.getVarNbr();
					// HaiTTH1 added on 5/1/2014
					arrival = vesselVoyValueObject.getArrival();
					departure = vesselVoyValueObject.getDeparture();
					col = vesselVoyValueObject.getCol_dttm();
					etb = vesselVoyValueObject.getEtb_dttm();
					
					VesselVoyValueObject veslObj1 = eSNService.getVesselInfo(selVoyno);
					String agent = veslObj1.getAgent();

					map.put("agent", agent);
				} else {
					log.info("Vietnd02=================Invalid vessel voyage values.");
					errorMessage = ConstantUtil.ErrorMsg_Invalid_Voy;
				}
				map.put("vsl_nm", fetchVesselName);
				map.put("voy_nbr", fetchVoyageNbr);

			} else if (selVoyno != null && !selVoyno.equals("")) {
				// HaiTTH1 added on 5/1/2014
				VesselVoyValueObject veslObj1 = eSNService.getVesselInfo(selVoyno);
				String vsl_nm = veslObj1.getVslName();
				String voy_nbr = veslObj1.getVoyNo();
				String agent = veslObj1.getAgent();

				map.put("agent", agent);
				map.put("vsl_nm", vsl_nm);
				map.put("voy_nbr", voy_nbr);

				VesselVoyValueObject vesObj2 = eSNService.getVessel(vsl_nm, voy_nbr, custCd);
				if (null != vesObj2) {
					arrival = vesObj2.getArrival();
					departure = vesObj2.getDeparture();
					col = vesObj2.getCol_dttm();
					etb = vesObj2.getEtb_dttm();
				}
			}

			map.put("arrival", arrival);
			map.put("departure", departure);
			map.put("col", col);
			map.put("etb", etb);

			/*
			 * << Add by FPT.Thai - Oct 02 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */

			if (selVoyno != null && !selVoyno.equals("")) {
				log.info("selVoyno" + selVoyno);
				esnList = eSNService.getEsnList(selVoyno, custCd, criteria);
				int totalCount = eSNService.getEsnListCount(selVoyno, custCd, criteria);
				map.put("total", totalCount);

				for (int i = 0; i < esnList.size(); i++) {
					esnListValueObject = new EsnListValueObject();
					esnListValueObject = esnList.get(i);
					esnNbr.add("" + esnListValueObject.getEsnNbr());
					shipperName.add((String) esnListValueObject.getShipperName());
					crgDesc.add((String) esnListValueObject.getCrgDesc());
					// added by Deng Zhengguo 18/5/2011
					crgType.add((String) esnListValueObject.getCrgType());
					// end add
					noOfPkgs.add("" + esnListValueObject.getNoofPkgs());
					log.info("no of packages ----" + esnListValueObject.getNoofPkgs());
					weight.add("" + esnListValueObject.getGrWt());
					volume.add("" + esnListValueObject.getGrVolume());
					bookingRefNbr.add((String) esnListValueObject.getBookingRefNo());
					stfInd.add(esnListValueObject.getStfInd());// added by vani
					categoryList.add(esnListValueObject.getCategory());// added by Zhenguo Deng(harbor)

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

					// HaiTTH1 added on 19/3/2014
					scheme.add((String) esnListValueObject.getScheme());
					// HaiTTH1 added on 19/3/2014
					// MCC for EPC_IND
					epcindlist.add(esnListValueObject.getDeliveryToEPC());
					terminalList.add((String) esnListValueObject.getTerminal());
					subSchemeList.add((String) esnListValueObject.getSubScheme());
					gcOperationsList.add((String) esnListValueObject.getGcOperations());
				}
			}

			TopsModel esnLists = new TopsModel();

			for (EsnListValueObject object : esnList) {
				esnLists.put(object);
			}
			map.put("esnList", esnLists);

			List<EsnListValueObject> esnDetails = new ArrayList<EsnListValueObject>();
			if (esnNo != null && !esnNo.equals("")) {
				esnDetails = eSNService.getEsnDetails(esnNo, custCd);
				log.info("esnDetails---" + esnDetails.size());
			}
			
			List<String> indicationStatus = eSNService.indicationStatus(selVoyno);
			map.put("indicationStatus", indicationStatus);
			
			map.put("selVoyno", selVoyno);
			map.put("shipperName", shipperName);
			map.put("noOfPkgs", noOfPkgs);
			map.put("esnNbr", esnNbr);
			map.put("weight", weight);
			map.put("volume", volume);
			map.put("stuffInd", stfInd);
			map.put("shipperName", shipperName);
			map.put("bookingRefNbr", bookingRefNbr);
			map.put("crgDesc", crgDesc);
			// added by Deng Zhengguo 18/5/2011
			map.put("crgType", crgType);
			// end add

			map.put("esnDetails", esnDetails);
			map.put("custCd", custCd);
			map.put("categoryList", categoryList);// added by Zhenguo Deng(harbor)
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			map.put("hsCodeList", hsCodeList);
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			// HaiTTH1 added on 19/3/2014
			map.put("schemeList", scheme);
			// HaiTTH1 added on 19/3/2014
			map.put("epcindlist", epcindlist); // MCC for EPC_IND
			map.put("terminalList", terminalList);
			map.put("subSchemeList", subSchemeList);
			map.put("gcOperationsList", gcOperationsList);

			map.put("ListData", topsModel);

		} catch (BusinessException be) {
			log.info("Exception EsnList: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception EsnList : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(mapError);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: EsnList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.esn --> EsnDetailHandler
	@PostMapping(value = "/EsnDetail")
	public ResponseEntity<?> EsnDetail(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: EsnDetail criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			String s1 = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String s = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			s=s.equalsIgnoreCase("")?"JP":s;
			String s2 = "";
			s2 = CommonUtility.deNull(criteria.getPredicates().get("assignCargo"));
			String s3 = "";
			String s4 = "";
			String s5 = "";

			s5 = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			String s8 = "";
			String s9 = "";
			s4 = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));// Changer by thanhnv2

			List<VesselVoyValueObject> vector = new ArrayList<VesselVoyValueObject>();
			List<String> arraylist = new ArrayList<String>();
			List<String> arraylist1 = new ArrayList<String>();
			List<String> arraylist2 = new ArrayList<String>();
			List<String> arraylist3 = new ArrayList<String>();
			List<String> arraylist4 = new ArrayList<String>();
			List<String> arraylist5 = new ArrayList<String>();
			List<String> arraylist6 = new ArrayList<String>();

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			ArrayList<String> hsCodeList = new ArrayList<String>();

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			// previously in SIT added by vani now(3 dec 2003) added by vinayak -- (*)
			// overwritten the production copy to SIT & new contents of SIT are added to
			// this copy
			List<String> stfInd = new ArrayList<String>();
			List<String> epcindlist = new ArrayList<String>();
			vector = eSNService.getVesselList(s);

			for (int i = 0; i < vector.size(); i++) {
				VesselVoyValueObject vesselvoyvalueobject1 = vector.get(i);
				topsModel.put(vesselvoyvalueobject1);
			}

			if (s4 != null && !s4.equals("")) {
				String s10 = CommonUtility.deNull(criteria.getPredicates().get("vslnew")); // s5 instead of s10

				if (criteria.getPredicates().get("PageIndex") == null
						|| criteria.getPredicates().get("PageIndex").equals("null") || s10.equals("VSLNEW")) {
					List<EsnListValueObject> vector3 = eSNService.getEsnList(s4, s, criteria);
					int totalCount = eSNService.getEsnListCount(s4, s, criteria);
					map.put("total", totalCount);
					int l = vector3.size();

					for (int j1 = 0; j1 < l; j1++) {
						EsnListValueObject esnlistvalueobject1 = new EsnListValueObject();
						esnlistvalueobject1 = (EsnListValueObject) vector3.get(j1);
						arraylist.add("" + esnlistvalueobject1.getEsnNbr());
						arraylist1.add(esnlistvalueobject1.getShipperName());
						arraylist2.add(esnlistvalueobject1.getCrgDesc());
						arraylist3.add("" + esnlistvalueobject1.getNoofPkgs());
						arraylist4.add("" + esnlistvalueobject1.getGrWt());
						arraylist5.add("" + esnlistvalueobject1.getGrVolume());
						arraylist6.add(esnlistvalueobject1.getBookingRefNo());

						hsCodeList.add(esnlistvalueobject1.getHsCode()); // added by VietNguyen
						// previously in SIT added by vani now(3 dec 2003) added by vinayak -- (*)
						stfInd.add(esnlistvalueobject1.getStfInd());// added by vani
						epcindlist.add(esnlistvalueobject1.getDeliveryToEPC()); // MCC for EPC_IND
					}

				}
			}
			List<EsnListValueObject> vector2 = new ArrayList<EsnListValueObject>();
			List<EsnListValueObject> vector4 = new ArrayList<EsnListValueObject>();
			List<TruckerValueObject> vectorTruckerList = new ArrayList<TruckerValueObject>();
			if (s5 != null && !s5.equals("")) {
				vector2 = eSNService.getEsnDetails(s5, s);
				vector4 = eSNService.getCntrDetails(s5);
				// HaiTTH1 added on 18/1/2014
				vectorTruckerList = eSNService.getTruckerList(s5);
			}
			s8 = eSNService.getScheme(s4);
			s9 = eSNService.getSchemeInd(s4);

			if (vector2.size() > 0) {
				for (int i1 = 0; i1 < vector2.size(); i1++) {
					EsnListValueObject esnlistvalueobject = vector2.get(i1);
					String s12 = esnlistvalueobject.getBookingRefNo();
					s3 = eSNService.getClsShipInd_bkr(s12);
				}

			}
			map.put("schval", s8);
			map.put("schind", s9);
			map.put("selVoyno", s4);
			map.put("shipperName", arraylist1);
			map.put("noOfPkgs", arraylist3);
			map.put("esnNbr", arraylist);
			map.put("weight", arraylist4);
			map.put("volume", arraylist5);
			map.put("shipperName", arraylist1);
			map.put("bookingRefNbr", arraylist6);
			map.put("hsCodeList", hsCodeList); // added by VietNguyen
			map.put("stuffInd", stfInd); // added by vani
			map.put("crgDesc", arraylist2);
			map.put("esnDetails", vector2);
			map.put("cntrDetails", vector4);
			map.put("custCd", s);
			map.put("chkClsShpInd", s3);
			// HaiTTH1 added on 18/1/2014
			map.put("truckerList", vectorTruckerList);
			map.put("epcindlist", epcindlist); // MCC for EPC_IND
			
			// START CR FTZ HSCODE - NS JULY 2024
			List<HsCodeDetails> hscodeDetailsList = eSNService.getHsCodeDetailList(s5);
			map.put("hscodeDetailsList", hscodeDetailsList);
			// END CR FTZ HSCODE - NS JULY 2024

			map.put("vesselvoyvalueobject1", topsModel);
			if (s2 != null && !s2.equals("") && s2.equals("ASSIGNDB")) {
				String s13 = CommonUtility.deNull(criteria.getPredicates().get("cargocategory"));
				eSNService.AssignCrgvalUpdate(s13, s5, s1);
				// previously in SIT it was there now(3 dec 2003) added by vinayak -- (*)
				// nextScreen(httpservletrequest, "EsnDetailReterview"); // added by vinayak
				String s17 = eSNService.AssignWhindCheck(s5);
				String s21 = "";
				String s26 = "";
				String s30 = "";
				if (s17 != null && !s17.equals("") && s17.equals("Y")) {
					List<String> vector10 = new ArrayList<String>();
					vector10 = eSNService.getWHDetails(s17, s5);
					s21 = (String) vector10.get(1);
					s26 = (String) vector10.get(0);
				} else {
					List<String> vector11 = new ArrayList<String>();
					vector11 = eSNService.getWHDetails(s17, s5);
					s30 = (String) vector11.get(0);
				}
				map.put("esnWhindcheck_disp", s17);
				map.put("nodays_disp", s30);
				map.put("whappnbr_disp", s21);
				map.put("remarks_disp", s26);
				// nextScreen(httpservletrequest, "EsnDetailReterview");

			} else if (s2 != null && !s2.equals("") && s2.equals("CRG")) {
				List<EsnListValueObject> vector5 = new ArrayList<EsnListValueObject>();
				String s18 = eSNService.AssignCrgvalCheck(s5);
				vector5 = eSNService.getAssignCargo();
				map.put("esnCargo", vector5);
				map.put("esncrgcheck", s18);
				// nextScreen(httpservletrequest, "EsnAssignCargo");

			} else if (s2 != null && !s2.equals("") && s2.equals("WHIND")) {
				String s14 = eSNService.AssignWhindCheck(s5);
				if (s14 != null && !s14.equals("") && s14.equals("Y")) {
					List<String> vector6 = new ArrayList<String>();
					vector6 = eSNService.getWHDetails(s14, s5);
					String s22 = (String) vector6.get(1);
					String s27 = (String) vector6.get(0);
					map.put("whappnbr", s22);
					map.put("remarks", s27);
				} else {
					List<String> vector7 = new ArrayList<String>();
					vector7 = eSNService.getWHDetails(s14, s5);
					String s23 = (String) vector7.get(0);
					map.put("nodays", s23);
				}
				map.put("esnWhindcheck", s14);
				// nextScreen(httpservletrequest, "EsnAssignWhind");
			} else if (s2 != null && !s2.equals("") && s2.equals("ASSIGWHNDB")) {
				String s15 = CommonUtility.deNull(criteria.getPredicates().get("whind"));
				String s19 = CommonUtility.deNull(criteria.getPredicates().get("whappnbr"));
				String s24 = CommonUtility.deNull(criteria.getPredicates().get("remarks"));
				String s28 = CommonUtility.deNull(criteria.getPredicates().get("nodays"));
				eSNService.AssignWhindUpdate(s15, s5, s19, s24, s28, s1);
				String s31 = eSNService.AssignWhindCheck(s5);
				String s32 = "";
				String s33 = "";
				String s34 = "";
				if (s31 != null && !s31.equals("") && s31.equals("Y")) {
					List<String> vector12 = new ArrayList<String>();
					vector12 = eSNService.getWHDetails(s31, s5);
					s32 = (String) vector12.get(1);
					s33 = (String) vector12.get(0);
				} else {
					List<String> vector13 = new ArrayList<String>();
					vector13 = eSNService.getWHDetails(s31, s5);
					s34 = (String) vector13.get(0);
				}
				map.put("esnWhindcheck_disp", s31);
				map.put("nodays_disp", s34);
				map.put("whappnbr_disp", s32);
				map.put("remarks_disp", s33);
				// nextScreen(httpservletrequest, "EsnDetailReterview");
			} else {
				String s16 = eSNService.AssignWhindCheck(s5);
				String s20 = "";
				String s25 = "";
				String s29 = "";
				if (s16 != null && !s16.equals("") && s16.equals("Y")) {
					List<String> vector8 = new ArrayList<String>();
					vector8 = eSNService.getWHDetails(s16, s5);
					s20 = (String) vector8.get(1);
					s25 = (String) vector8.get(0);
				} else {
					List<String> vector9 = new ArrayList<String>();
					vector9 = eSNService.getWHDetails(s16, s5);
					s29 = (String) vector9.get(0);
				}

				// ++ VietND02
				String existSubAdp = "False";
				if (eSNService.checkExistSubAdp(s5))
					existSubAdp = "True";
				// --

				// 16/06/2011 PCYAP To add/update only own ESN

				boolean isEsnCreator = false;

				try {

					String companyCode = s;
					String esnAsnNbr = (String) CommonUtility.deNull(criteria.getPredicates().get("esnNo"));

					// EJBHomeFactory ejbHomeFactory = EJBHomeFactory.getInstance();
					// EsnHome esnHome = (EsnHome) ejbHomeFactory.lookUpHome("EsnEJB");
					// Esn esn = esnHome.create();

					isEsnCreator = eSNService.isEsnCreator(esnAsnNbr, companyCode);
				} catch (Exception e) {
					log.info("Exception EsnDetail: ", e);
				}

				map.put("isEsnCreator", Boolean.valueOf(isEsnCreator));

				map.put("existSubAdp", existSubAdp);
				map.put("esnWhindcheck_disp", s16);
				map.put("nodays_disp", s29);
				map.put("whappnbr_disp", s20);
				map.put("remarks_disp", s25);

			}
		} catch (BusinessException be) {
			log.info("Exception EsnDetail: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception EsnDetail : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(mapError);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: EsnDetail result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.esn --> EsnAmendHandler
	@PostMapping(value = "/EsnAmend")
	public ResponseEntity<?> EsnAmend(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: EsnAmend criteria:" + criteria.toString());
			TopsModel topsModel = new TopsModel();
			String custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String esnNo = "";
			esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			EsnListValueObject esnListValueObject = null;
			EsnListValueObject esnListValueObject1 = null;

			map.put("co_cd", custCd);
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));
			boolean checkResult = eSNService.checkDisbaleOverSideFroDPE(vvCd);
			String hiddenOverSide = "FALSE";
			if (!StringUtils.equalsIgnoreCase("JP", custCd) && checkResult) {
				hiddenOverSide = "TRUE";
			}
			map.put("hiddenOverSide", hiddenOverSide);

			String noOfCntr = "";
			String bookingRefNo = "";
			bookingRefNo = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo"));

			List<EsnListValueObject> esnDetails = new ArrayList<EsnListValueObject>();
			List<EsnListValueObject> cntrDetails = new ArrayList<EsnListValueObject>();
			List<EsnListValueObject> bookingRefList = new ArrayList<EsnListValueObject>();

			if (esnNo != null && !esnNo.equals("")) {
				esnDetails = eSNService.getEsnDetails(esnNo, custCd, criteria);
				cntrDetails = eSNService.getCntrDetails(esnNo);
				bookingRefList = eSNService.getBkRefNo(bookingRefNo, custCd);
			}
			esnListValueObject1 = new EsnListValueObject();
			for (int i = 0; i < bookingRefList.size(); i++) {
				esnListValueObject1 = (EsnListValueObject) bookingRefList.get(i);
				noOfCntr = "".concat(String.valueOf(String.valueOf(esnListValueObject1.getNoOfCntr())));
			}

			esnListValueObject = new EsnListValueObject();
			for (int i = 0; i < esnDetails.size(); i++) {
				esnListValueObject = (EsnListValueObject) esnDetails.get(i);
				String payMode = esnListValueObject.getPayMode();
				topsModel.put(esnListValueObject);

				map.put("payMode", payMode);
			}

			// add by Zhenguo Deng on 14/02/2011 for Cargo Category
			String vslType = eSNService.getVesselType(bookingRefNo);
			map.put("vslType", vslType);

			if (vslType.equals("CC")) {
				List<Map<String, Object>> list = eSNService.getCategoryList();
				map.put("categoryList", list);
			}
			String category = CommonUtility.deNull(criteria.getPredicates().get("category"));
			map.put("category", category);
			// end add

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			List<String> hsCodeList = new ArrayList<String>();

			List<ManifestValueObject> listManifestValueObject = eSNService.getHSCodeList("1");
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
			//START CR FTZ HSCODE - NS JULY 2024
			List<HsCodeDetails> hscodeDetailsList = eSNService.getHsCodeDetailList(esnNo);
			map.put("hscodeDetailsList", hscodeDetailsList);
			//END CR FTZ HSCODE - NS JULY 2024

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			map.put("noOfCntr", noOfCntr);
			map.put("esnDetails", esnDetails);
			map.put("cntrDetails", cntrDetails);
			map.put("esnListValueObject", topsModel);

			log.info("After modelManager");

			String existSubAdp = CommonUtility.deNull(criteria.getPredicates().get("existSubAdp"));
			map.put("existSubAdp", existSubAdp);
			log.info("======existSubAdp in ESN Amend:: " + existSubAdp);

			// HaiTTH1 added on 24/1/2014
			String populateTrucker = CommonUtility.deNull(criteria.getPredicates().get("trucker"));
			List<TruckerValueObject> truckerVector = new ArrayList<TruckerValueObject>();

			if (StringUtils.equalsIgnoreCase("TRUCKER", populateTrucker)) {
//				String[] listValueTruckerIcNo = new String[0];
//				if (request.getParameterValues("truckerIc") != null) {
//					listValueTruckerIcNo = (String[]) request.getParameterValues("truckerIc");
//				}
//				String[] listValueTruckerName = new String[0];
//				if (request.getParameterValues("truckerNm") != null) {
//					listValueTruckerName = (String[]) request.getParameterValues("truckerNm");
//				}
//				String[] listValueTruckerCNo = new String[0];
//				if (request.getParameterValues("truckerContact") != null) {
//					listValueTruckerCNo = (String[]) request.getParameterValues("truckerContact");
//				}
//				String[] listValueNbrpkg = new String[0];
//				if (request.getParameterValues("truckerPkgs") != null) {
//					listValueNbrpkg = (String[]) request.getParameterValues("truckerPkgs");
//				}
                // Added 29112022
				List<String> listValueTruckerIcNo =  new ArrayList<String>(); 
				String truckerIcNo;
				List<String> listValueTruckerName =  new ArrayList<String>(); 
				String truckerNmNo;
				List<String> listValueTruckerCNo =  new ArrayList<String>(); 
				String truckerContactNo;
				List<String> listValueNbrpkg =  new ArrayList<String>(); 
				String truckerPkgsNo;
				String noOfPkgs = CommonUtility.deNull(criteria.getPredicates().get("noOfPkgs"));
				int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
				
				if (sizeSelected != 0) {
					for (int i = 0; i < sizeSelected; i++) {
						truckerIcNo = (String) criteria.getPredicates().get("truckerIc" + i);
						listValueTruckerIcNo.add(truckerIcNo);
						truckerNmNo = (String) criteria.getPredicates().get("truckerNm" + i);
						listValueTruckerName.add(truckerNmNo);
						truckerContactNo = (String) criteria.getPredicates().get("truckerContact" + i);
						listValueTruckerCNo.add(truckerContactNo);
						truckerPkgsNo = (String) criteria.getPredicates().get("truckerPkgs" + i);
						listValueNbrpkg.add(truckerPkgsNo);
					}
				}
				
				String truckerDelete = CommonUtility.deNull(criteria.getPredicates().get("truckerDelete"));
				if (!"delete".equalsIgnoreCase(truckerDelete)) {
					int seq = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("truckerRow")));
					if (listValueTruckerIcNo.size() >= seq) {
						String trucker_ciNo = listValueTruckerIcNo.get(seq - 1);
						log.info("trucker_ciNo: " + trucker_ciNo);
						TruckerValueObject truckerObj = new TruckerValueObject();
						boolean isValid = true;
						// if (!StringUtils.isEmpty(trucker_ciNo)) {
						// isValid = esn.checkValidTrucker(trucker_ciNo);
						// }
						if (isValid) {
							truckerObj = eSNService.getTruckerDetails(trucker_ciNo);
							listValueTruckerName.set(seq - 1, truckerObj.getTruckerNm());
							listValueTruckerCNo.set(seq - 1, truckerObj.getTruckerContact());
						} else {
							listValueTruckerName.set(seq - 1, "");
							listValueTruckerCNo.set(seq - 1, "");
						}

						for (int x = 0; x < listValueTruckerIcNo.size(); x++) {
							TruckerValueObject trk_obj = new TruckerValueObject();
							trk_obj.setTruckerIc(listValueTruckerIcNo.get(x));
							trk_obj.setTruckerNm(listValueTruckerName.get(x));
							trk_obj.setTruckerContact(listValueTruckerCNo.get(x));
							trk_obj.setTruckerPkgs(listValueNbrpkg.get(x));
							truckerVector.add(trk_obj);
						}

					}
				} else {
					int number = 0;
					if (listValueTruckerIcNo.size() > 0) {
						for (int y = 0; y < listValueTruckerIcNo.size(); y++) {
							TruckerValueObject trk_obj = new TruckerValueObject();
							trk_obj.setTruckerIc(listValueTruckerIcNo.get(y));
							trk_obj.setTruckerNm(listValueTruckerName.get(y));
							trk_obj.setTruckerContact(listValueTruckerCNo.get(y));
							trk_obj.setTruckerPkgs(listValueNbrpkg.get(y));
							truckerVector.add(trk_obj);
							if (listValueNbrpkg.get(y) != null && !StringUtils.isEmpty(listValueNbrpkg.get(y))
									&& StringUtils.isNumeric(listValueNbrpkg.get(y))) {
								number = number + Integer.parseInt(listValueNbrpkg.get(y));
							}
						}
					}
					noOfPkgs = String.valueOf(number);
				}

				String cntr1 = CommonUtility.deNull(criteria.getPredicates().get("cntr1"));
				String cntr2 = CommonUtility.deNull(criteria.getPredicates().get("cntr2"));
				String cntr3 = CommonUtility.deNull(criteria.getPredicates().get("cntr3"));
				String cntr4 = CommonUtility.deNull(criteria.getPredicates().get("cntr4"));

				String hsSubCode = CommonUtility.deNull(criteria.getPredicates().get("hsSubCode"));
				String hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
				String hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));
				// Added back end validation 12 Aug 2015 for hs code sub code not null
				if (hsSubCode == null || hsSubCodeFr == null || hsSubCodeTo == null
						|| hsSubCodeFr.trim().equalsIgnoreCase("") || hsSubCode.trim().equalsIgnoreCase("")
						|| hsSubCodeTo.trim().equalsIgnoreCase("")) {
					errorMessage = ConstantUtil.ErrorMsg_HSsubCode_Null;

					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAmend result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				} else {
					log.info("HS Code value ***" + hsSubCode + ":" + hsSubCodeFr + ":" + hsSubCodeTo);
				}
				// End back end validation 12 Aug 2015
				String hsSubCodeDesc = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDesc"));
				bookingRefNo = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo"));
				esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				String vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
				String voyNo = CommonUtility.deNull(criteria.getPredicates().get("voyNo"));
				String varNbr = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));
				String pkg_dec = CommonUtility.deNull(criteria.getPredicates().get("pkg_dec"));
				String pkg_var = CommonUtility.deNull(criteria.getPredicates().get("pkg_var"));

				String pkgsType = CommonUtility.deNull(criteria.getPredicates().get("pkgsType").trim());
				String weight_dec = CommonUtility.deNull(criteria.getPredicates().get("weight_dec"));
				String weight_var = CommonUtility.deNull(criteria.getPredicates().get("weight_var"));
				String vol_dec = CommonUtility.deNull(criteria.getPredicates().get("vol_dec"));
				String vol_var = CommonUtility.deNull(criteria.getPredicates().get("vol_var"));
				String weight = CommonUtility.deNull(criteria.getPredicates().get("weight"));
				String volume = CommonUtility.deNull(criteria.getPredicates().get("volume"));
				String payMode = CommonUtility.deNull(criteria.getPredicates().get("payMode"));
				String hsCode = CommonUtility.deNull(criteria.getPredicates().get("hsCode"));
				String marking = CommonUtility.deNull(criteria.getPredicates().get("marking"));
				String cargoDesc = CommonUtility.deNull(criteria.getPredicates().get("cargoDesc"));
				String category_ = CommonUtility.deNull(criteria.getPredicates().get("category"));
				String shipperName = CommonUtility.deNull(criteria.getPredicates().get("shipperName"));
				String lopInd = CommonUtility.deNull(criteria.getPredicates().get("lopInd"));
				String dgInd = CommonUtility.deNull(criteria.getPredicates().get("dgInd"));
				String loadFrom = CommonUtility.deNull(criteria.getPredicates().get("loadFrom"));
				String storageInd = CommonUtility.deNull(criteria.getPredicates().get("storageInd"));
				String noOfStorageDay = CommonUtility.deNull(criteria.getPredicates().get("noOfStorageDay"));
				String portDesc = CommonUtility.deNull(criteria.getPredicates().get("portDesc"));
				String dutyGoodInd = CommonUtility.deNull(criteria.getPredicates().get("dutyGoodInd"));
				String stfInd = CommonUtility.deNull(criteria.getPredicates().get("stfInd"));
				String portD = CommonUtility.deNull(criteria.getPredicates().get("portD"));
				String cntrtype = CommonUtility.deNull(criteria.getPredicates().get("cntrtype"));
				String cntrsize = CommonUtility.deNull(criteria.getPredicates().get("cntrsize"));
				String accNo = CommonUtility.deNull(criteria.getPredicates().get("accNo"));
				String customHsCode = CommonUtility.deNull(criteria.getPredicates().get("customHsCode"));

				map.put("customHsCode", customHsCode);
				map.put("hsSubCode", hsSubCode);
				map.put("hsSubCodeFr", hsSubCodeFr);
				map.put("hsSubCodeTo", hsSubCodeTo);
				map.put("hsSubCodeDesc", hsSubCodeDesc);
				map.put("bookingRefNo", bookingRefNo);
				map.put("esnNo", esnNo);
				map.put("vslName", vslName);
				map.put("voyNo", voyNo);
				map.put("varNbr", varNbr);
				map.put("pkg_var", pkg_var);
				map.put("pkg_dec", pkg_dec);
				map.put("noOfPkgs", noOfPkgs);
				map.put("pkgsType", pkgsType);
				map.put("weight_dec", weight_dec);
				map.put("weight_var", weight_var);
				map.put("vol_dec", vol_dec);
				map.put("vol_var", vol_var);
				map.put("weight", weight);
				map.put("volume", volume);
				map.put("payMode", payMode);
				map.put("hsCode", hsCode);
				map.put("marking", marking);
				map.put("cargoDesc", cargoDesc);
				map.put("category", category_);
				map.put("dutyGoodInd", dutyGoodInd);
				map.put("lopInd", lopInd);
				map.put("dgInd", dgInd);
				map.put("loadFrom", loadFrom);
				map.put("storageInd", storageInd);
				map.put("noOfStorageDay", noOfStorageDay);
				map.put("portDesc", portDesc);
				map.put("shipperName", shipperName);
				map.put("stfInd", stfInd);
				map.put("portD", portD);
				map.put("cntrtype", cntrtype);
				map.put("cntrsize", cntrsize);
				map.put("cntr1", cntr1);
				map.put("cntr2", cntr2);
				map.put("cntr3", cntr3);
				map.put("cntr4", cntr4);
				map.put("accNo", accNo);
				map.put("truckerList", truckerVector);

				// HaiTTH1 added on 8/4/2014
				String mainTruckerIc = CommonUtility.deNull(criteria.getPredicates().get("mainTruckerIc"));
				map.put("mainTruckerIc", mainTruckerIc);
				// HaiTTH1 ended on 8/4/2014

				// MCC added for EPC
				String deliveryToEPC = CommonUtility.deNull(criteria.getPredicates().get("deliveryToEPC"));
				map.put("deliveryToEPC", deliveryToEPC);

			} else {
				truckerVector = eSNService.getTruckerList(esnNo);
				map.put("truckerList", truckerVector);
			}
			map.put("maxiTrucker", MAX_ADP_TRUCKER);
			map.put("truckerMode", populateTrucker);
		} catch (BusinessException be) {
			log.info("Exception EsnAmend: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception EsnAmend : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(mapError);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: EsnAmend result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.eSNService --> EsnAmendConfirmHandler
	@PostMapping(value = "/EsnAmendConfirm")
	public ResponseEntity<?> EsnAmendConfirm(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: EsnAmendConfirm criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			String s = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String s1 = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			int i = 0;
			int j = 0;
			String s19 = "";
			String s20 = "";
			String s32 = "";
			String s34 = "";
			String s35 = "";
			boolean flag = false;
			String s41 = "";
			int k = 0;
			String s42 = "";
			double d = 0.0D;
			double d1 = 0.0D;
			String s43 = "";
			String s44 = "";
			String s53 = "";
			String s54 = "";
			String s55 = "";

			String cntrNbr = "";
			String appNo = "";
			String loadVVCd = "";
			String cntrSeqNbr = "";

			String strUAFlag = "";// added by vani -- 20th Oct,03
			List<String> vector = new ArrayList<String>();
			List<EsnListValueObject> vector1 = new ArrayList<EsnListValueObject>();
			List<EsnListValueObject> vector2 = new ArrayList<EsnListValueObject>();

			String stfInd = ""; // added by vani
			String trucker_ic = "";
			String trucker_nm = "";
			String trucker_ct = "";
			int trucker_pkg = 0;
			String trucker_cd = "";
			EsnListValueObject esnlistvalueobject = null;
			// add by Zhenguo Deng on 14/02/2011 for Cargo Category
			String category = "00";
			if (!StringUtils.isEmpty(criteria.getPredicates().get("category"))) {
				category = CommonUtility.deNull(criteria.getPredicates().get("category"));
			}
			map.put("category", category);
			// end add

			// add by Zhenguo Deng on 14/02/2011 for Cargo Category
			if (!StringUtils.isEmpty(category)) {
				String categoryValue = eSNService.getCategoryValue(category);
				map.put("categoryValue", categoryValue);
			}
			// end add
			s34 = CommonUtility.deNull(criteria.getPredicates().get("update"));
			s41 = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));
			s35 = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));

			if (s34 != null && !s34.equals("") && !s34.equals("Assign") && !s34.equals("Assign_Bill")) {
				strUAFlag = CommonUtility.deNull(criteria.getPredicates().get("UAflag"));

				s32 = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo"));
				String s56 = CommonUtility.deNull(criteria.getPredicates().get("noOfPkgs"));
				if (s56 != null && !s56.equals(""))
					i = Integer.parseInt(s56);
				String s58 = CommonUtility.deNull(criteria.getPredicates().get("weight"));
				if (s58 != null && !s58.equals(""))
					d = Double.parseDouble(s58);
				String s61 = CommonUtility.deNull(criteria.getPredicates().get("volume"));
				if (s61 != null && !s61.equals(""))
					d1 = Double.parseDouble(s61);
				String s11 = CommonUtility.deNull(criteria.getPredicates().get("lopInd"));
				String s15 = CommonUtility.deNull(criteria.getPredicates().get("dgInd"));
				String s5 = CommonUtility.deNull(criteria.getPredicates().get("pkgsType").trim());
				String s17 = CommonUtility.deNull(criteria.getPredicates().get("storageInd"));
				String s26 = CommonUtility.deNull(criteria.getPredicates().get("dutyGoodInd"));
				stfInd = CommonUtility.deNull(criteria.getPredicates().get("stfInd")); // added by vani
				String s13 = CommonUtility.deNull(criteria.getPredicates().get("loadFrom"));
				String s24 = CommonUtility.deNull(criteria.getPredicates().get("portD"));

				String s3 = CommonUtility.deNull(criteria.getPredicates().get("hsCode"));
				String s9 = CommonUtility.deNull(criteria.getPredicates().get("marking"));
				String s7 = CommonUtility.deNull(criteria.getPredicates().get("cargoDesc"));
				String s38 = CommonUtility.deNull(criteria.getPredicates().get("noOfStorageDay"));
				if (s38 != null && !s38.equals(""))
					j = Integer.parseInt(s38);
				s19 = ""; // request.getParameter("truckerIcNo").trim();
				// s20 = request.getParameter("truckerName").trim();
				// request.getParameter("truckerCNo").trim();
				String s46 = CommonUtility.deNull(criteria.getPredicates().get("cntr1"));
				String s48 = CommonUtility.deNull(criteria.getPredicates().get("cntr2"));
				String s50 = CommonUtility.deNull(criteria.getPredicates().get("cntr3"));
				String s52 = CommonUtility.deNull(criteria.getPredicates().get("cntr4"));

				String deliveryToEPC = CommonUtility.deNull(criteria.getPredicates().get("deliveryToEPC"));

				String hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
				String hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));

				if (hsSubCodeFr == null || hsSubCodeTo == null || hsSubCodeFr.trim().equalsIgnoreCase("")
						|| hsSubCodeTo.trim().equalsIgnoreCase("")) {
					errorMessage = ConstantUtil.ErrorMsg_HSsubCode_Null;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAmendConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				} else {
					log.info("HS Code value ***" + hsSubCodeFr + ":" + hsSubCodeTo);
				}

				// to check hs subcode, hssubcodefrom , hssubcodeto 3 Nov 2016 Start
				String hssubcodeDesc = eSNService.getHSSubCodeDes(s3, hsSubCodeFr, hsSubCodeTo);
				log.info("hssubcodeDesc validity check***" + hssubcodeDesc);
				if (hssubcodeDesc == null || hssubcodeDesc.equalsIgnoreCase("")) {
					errorMessage = ConstantUtil.ErrorMsg_HSsubCode_Invalid;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAmendConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				
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

				String isDisableJPBillingNo = "";
				if ("O".equalsIgnoreCase(s11) && !StringUtils.equalsIgnoreCase("JP", s1)) {
					isDisableJPBillingNo = "true";
				} else {
					isDisableJPBillingNo = "false";
				}
				// HaiTTH1 added on 7/1/2014
				map.put("isDisableJPBillingNo", isDisableJPBillingNo);
				// HaiTTH1 ended on 7/1/2014
				// HaiTTH1 added on 20/1/2014
//				String[] listValueTruckerIcNo = new String[0];
//				if (request.getParameterValues("truckerIc") != null) {
//					listValueTruckerIcNo = (String[]) request.getParameterValues("truckerIc");
//				}
//				String[] listValueTruckerName = new String[0];
//				if (request.getParameterValues("truckerNm") != null) {
//					listValueTruckerName = (String[]) request.getParameterValues("truckerNm");
//				}
//				String[] listValueTruckerCNo = new String[0];
//				if (request.getParameterValues("truckerContact") != null) {
//					listValueTruckerCNo = (String[]) request.getParameterValues("truckerContact");
//				}
//				String[] listValueNbrpkg = new String[0];
//				if (request.getParameterValues("truckerPkgs") != null) {
//					listValueNbrpkg = (String[]) request.getParameterValues("truckerPkgs");
//				}

				//Add by NS 25112022 : Change request array parameter value.
//				List<TruckerValueObject> truckerVector = new ArrayList<TruckerValueObject>();
				int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
				List<String> listValueTruckerIcNo =  new ArrayList<String>(); 
				String truckerIcNo;
				List<String> listValueTruckerName =  new ArrayList<String>(); 
				String truckerNmNo;
				List<String> listValueTruckerCNo =  new ArrayList<String>(); 
				String truckerContactNo;
				List<String> listValueNbrpkg =  new ArrayList<String>(); 
				String truckerPkgsNo;
				
				if (sizeSelected != 0) {
					for (int idx = 0; idx < sizeSelected; idx++) {
						truckerIcNo = (String) criteria.getPredicates().get("truckerIc" + idx);
						listValueTruckerIcNo.add(truckerIcNo);
						truckerNmNo = (String) criteria.getPredicates().get("truckerNm" + idx);
						listValueTruckerName.add(truckerNmNo);
						truckerContactNo = (String) criteria.getPredicates().get("truckerContact" + idx);
						listValueTruckerCNo.add(truckerContactNo);
						truckerPkgsNo = (String) criteria.getPredicates().get("truckerPkgs" + idx);
						listValueNbrpkg.add(truckerPkgsNo);
					}
				}
				
				if (stfInd.equalsIgnoreCase("Y")) {
					cntrNbr = CommonUtility.deNull(criteria.getPredicates().get("cntrNbr"));
					appNo = CommonUtility.deNull(criteria.getPredicates().get("appNo"));
					loadVVCd = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));

					if (!eSNService.validateGCStuffIndicatorCntr(loadVVCd, cntrNbr)) {
						errorMessage = ConstantUtil.ErrorMsg_Invalid_CntrNbr;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAmendConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					} else {
						ContainerValueObject containerVO = eSNService.getContainerInformation(cntrNbr);
						if (containerVO != null) {
							cntrSeqNbr = "" + containerVO.getContainerSeqNo();
						} else {
							errorMessage = ConstantUtil.ErrorMsg_NotFound_CntrNbr;
							if (errorMessage != null) {
								mapError.put("errorMessage", errorMessage);
								result = new Result();
								result.setErrors(mapError);
								result.setSuccess(false);
								result.setData(map);

							} else {
								result.setData(map);
								result.setSuccess(true);
								log.info("END: EsnAmendConfirm result: " + result.toString());
							}

							return ResponseEntityUtil.success(result.toString());
						}
					}

					/*
					 * if(!miscAppEjb.validateGCStuffIndicatorMiscApp(appNo)){ errorMessage(request,
					 * "Invalid Misc Application Number "); return; }
					 */
				}

				String totalPkgs_s = CommonUtility.deNull(criteria.getPredicates().get("noOfPkgs"));
				List<TruckerValueObject> truckerVector = new ArrayList<TruckerValueObject>();
				int totalPkgs = Integer.parseInt(totalPkgs_s);
				int totalNbrAdpPkg = 0;
				if (listValueTruckerIcNo.size() > 0) {
					for (int x = 0; x < listValueTruckerIcNo.size(); x++) {
						TruckerValueObject trk_obj = new TruckerValueObject();
						// if ("UA".equals(strUAFlag)) {
						TruckerValueObject trkObj = new TruckerValueObject();
						trkObj = eSNService.getTruckerDetails(listValueTruckerIcNo.get(x).trim());
						String _trucker_cd = trkObj.getTruckerCd(); // eSNService.getTruckerCd(listValueTruckerIcNo[x].trim());
						trk_obj.setTruckerCd(_trucker_cd);
						trk_obj.setTruckerIc(listValueTruckerIcNo.get(x));
						trk_obj.setTruckerNm(listValueTruckerName.get(x));
						trk_obj.setTruckerContact(listValueTruckerCNo.get(x));
						trk_obj.setTruckerPkgs(listValueNbrpkg.get(x));
						truckerVector.add(trk_obj);
						int nbrPkgs = Integer.parseInt(listValueNbrpkg.get(x));
						totalNbrAdpPkg = totalNbrAdpPkg + nbrPkgs;
						if (x == 0) {
							trucker_ic = listValueTruckerIcNo.get(0).trim();
							trucker_nm = listValueTruckerName.get(0).trim();
							trucker_ct = listValueTruckerCNo.get(0).trim();
							trucker_pkg = Integer.parseInt(listValueNbrpkg.get(0));
							trucker_cd = _trucker_cd;
						}
						// } else {
						// invalidApd = invalidApd + listValueTruckerIcNo[x] + ", ";
						// errorMessage(request, "Trucker Ic. No. " + listValueTruckerIcNo[x] + " is not
						// valid.");
						// return;
						// }

					}
					// if (StringUtils.isNotBlank(invalidApd)) {
					// map.put("invalidApd", "These Trucker number " + invalidApd + "are invalid");
					// }
				} else {
					errorMessage = ConstantUtil.ErrorMsg_Need_Trucker;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAmendConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				map.put("truckerList", truckerVector);

				if (totalNbrAdpPkg != 0 && totalNbrAdpPkg != totalPkgs) {
					errorMessage = ConstantUtil.ErrorMsg_Package_Equal;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAmendConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}

				// HaiTTH1 added on 10/2/2014
				s19 = trucker_ic;
				s20 = trucker_nm;
				//s22 = trucker_ct;

				if (s56 != null && !s56.equals("")) {
					boolean flag1 = eSNService.chkNoOfPkgs(s32, i);
					if (!flag1) {
						errorMessage = ConstantUtil.ErrorMsg_Package_Greater;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAmendConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				}
				if (s15 != null && s15.compareTo("Y") == 0) {

					flag = eSNService.isOutWardPm(s32, s41);

					if (!flag) {
						// errorMessage(request, "ESN creation not allowed");
						errorMessage = ConstantUtil.ErrorMsg_ESN_Update_PM4_NotApproved;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAmendConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				}

				if (s5 != null && !s5.equals("")) {
					boolean flag2 = eSNService.chkPkgsType(s5);
					if (!flag2) {
						errorMessage = ConstantUtil.ErrorMsg_Invalid_PackageType;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAmendConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				}
				if (s58 != null && !s58.equals("")) {
					boolean flag3 = eSNService.chkWeight(s32, d);
					if (!flag3) {
						errorMessage = ConstantUtil.ErrorMsg_Weight_Greater;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAmendConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				}
				if (s61 != null && !s61.equals("")) {
					boolean flag4 = eSNService.chkVolume(s32, d1);
					if (!flag4) {
						errorMessage = ConstantUtil.ErrorMsg_Volume_Greater;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAmendConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				} 
				List<EsnListValueObject> vector7 = new ArrayList<EsnListValueObject>();
				List<EsnListValueObject> vector8 = new ArrayList<EsnListValueObject>();
				List<String> arraylist = new ArrayList<String>();
				List<String> arraylist1 = new ArrayList<String>();
				String s73 = "";
				vector7 = eSNService.getAccNo(s19);
				if (vector7.size() != 0) {
					for (int i1 = 0; i1 < vector7.size(); i1++) {
						esnlistvalueobject = new EsnListValueObject();
						esnlistvalueobject = vector7.get(i1);
						arraylist1.add(esnlistvalueobject.getAccNo());
					}

				}
				if (vector7.size() != 0)
					s73 = esnlistvalueobject.getAccNo();
				else
					s73 = "No";
				vector8 = eSNService.getUserAccNo(s32, s1, s73);
				if (vector8.size() != 0) {
					for (int j1 = 0; j1 < vector8.size(); j1++) {
						EsnListValueObject esnlistvalueobject1 = new EsnListValueObject();
						esnlistvalueobject1 = vector8.get(j1);
						arraylist.add(esnlistvalueobject1.getAccNo());
					}

				}
				if (s34 != null && !s34.equals("") && s34.equals("update")) {
					String s31 = CommonUtility.deNull(criteria.getPredicates().get("payMode"));
					String s28 = CommonUtility.deNull(criteria.getPredicates().get("accNo1"));
					boolean flag5 = eSNService.chkAccNo(s28);
					if (!s28.equals("CASH") && !s28.equals("CA") && !flag5) {
						errorMessage = ConstantUtil.ErrorMsg_Invalid_Billable_AccNo;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAmendConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
					k = eSNService.getUaNoPkgs(s35);
					s42 = eSNService.getClsShipInd(s41);

					if ("UA".equals(strUAFlag) && eSNService.isBillChargesRaised(s35)) {
						errorMessage = ConstantUtil.ErrorMsg_Bill_Raised_UA_Amend;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAmendConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					} // add by senthil 13 feb 2004

					eSNService.esnUpdateForDPE(i, s3, s5, s9, trucker_nm, trucker_ic, s11, s15, s17, s13, s24, j, s26,
							s31, s28, s35, s7, trucker_cd, d, d1, trucker_ct, s46, s48, s50, s52, stfInd, strUAFlag, s,
							category, hsSubCodeFr, hsSubCodeTo, truckerVector, trucker_pkg, s1, deliveryToEPC,
							cntrSeqNbr, appNo, customHsCode, multiHsCodeList);// stfInd,strUAFlag added by vani s- userId added by Revathi --
												// hsSubCodeFr, hsSubCodeTo added by VietNguyen MCC for EPC IND
					s43 = eSNService.getPkgsDesc(s35);
					s44 = eSNService.getBillablePartyName(s28);
				}
				map.put("accNo", arraylist1);
				map.put("userAccNo", arraylist);
			}
			String s57 = CommonUtility.deNull(criteria.getPredicates().get("voyNo"));
			vector = eSNService.getSAacctno(s41);
			// Modified by SONLT3 on 22/02/2010
			if ("JP".equals(s1)) {
				vector1 = eSNService.getABacctno(s41);
			} else {
				vector1 = eSNService.getABacctnoForSA(s41);
			}
			// End
			map.put("voyNo", s57);
			map.put("vsactno", vector);
			map.put("vabactno", vector1);
			s55 = eSNService.getBPacctnbr(s35, s41);
			map.put("sacctnbr", s55);
			s53 = eSNService.getScheme(s41);
			s54 = eSNService.getSchemeInd(s41);
			map.put("schval", s53);
			map.put("schind", s54);
			if (s34 != null && !s34.equals("") && s34.equals("Assign_Bill")) {
				String s59 = CommonUtility.deNull(criteria.getPredicates().get("billparty"));
				String s62 = "";
				String s65 = "";
				s62 = eSNService.getSchemeName(s41);
				if (s62.equals("JLR"))
					s65 = eSNService.getVCactnbr(s41);
				else if (!(s62.equals("JLR") || s62.equals("JNL") || s62.equals("JBT") || s62.equals("JWP")
						|| s62.equals(ProcessChargeConst.LCT_SCHEME)))
					s65 = eSNService.getABactnbr(s41);
				eSNService.EsnAssignBillUpdate(s59, s35, s);
				if (s65 != s59)
					eSNService.EsnAssignVslUpdate(s41, "Y", s);
				if (s35 != null && !s35.equals("")) {
					vector2 = eSNService.getEsnDetails(s35, s1);
					List<EsnListValueObject> vector5 = eSNService.getCntrDetails(s35);
					map.put("esnDetails", vector2);
					map.put("cntrDetails", vector5);
					String s67 = eSNService.AssignWhindCheck(s35);
					String s69 = "";
					String s71 = "";
					String s74 = "";
					if (s67 != null && !s67.equals("") && s67.equals("Y")) {
						List<String> vector11 = new ArrayList<String>();
						vector11 = eSNService.getWHDetails(s67, s35);
						s69 = (String) vector11.get(1);
						s71 = (String) vector11.get(0);
					} else {
						List<String> vector12 = new ArrayList<String>();
						vector12 = eSNService.getWHDetails(s67, s35);
						s74 = (String) vector12.get(0);
					}
					map.put("esnWhindcheck_disp", s67);
					map.put("nodays_disp", s74);
					map.put("whappnbr_disp", s69);
					map.put("remarks_disp", s71);
				}
				k = eSNService.getUaNoPkgs(s35);
				s42 = eSNService.getClsShipInd(s41);
			}
			String s60 = "";

			if (vector2.size() > 0) {
				for (int l = 0; l < vector2.size(); l++) {
					EsnListValueObject esnlistvalueobject2 = vector2.get(l);
					String s64 = esnlistvalueobject2.getBookingRefNo();
					s60 = eSNService.getClsShipInd_bkr(s64);
				}

			} else {
				s60 = eSNService.getClsShipInd_bkr(s32);
			}
			if (s35 != null && !s35.equals("")) {
				List<EsnListValueObject> vector3 = eSNService.getEsnDetails(s35, s1);
				List<EsnListValueObject> vector6 = eSNService.getCntrDetails(s35);
				map.put("esnDetails", vector3);
				map.put("cntrDetails", vector6);
				String s66 = eSNService.AssignWhindCheck(s35);
				String s68 = "";
				String s70 = "";
				String s72 = "";
				if (s66 != null && !s66.equals("") && s66.equals("Y")) {
					List<String> vector9 = new ArrayList<String>();
					vector9 = eSNService.getWHDetails(s66, s35);
					s68 = (String) vector9.get(1);
					s70 = (String) vector9.get(0);
				} else {
					List<String> vector10 = new ArrayList<String>();
					vector10 = eSNService.getWHDetails(s66, s35);
					s72 = (String) vector10.get(0);
				}
				map.put("esnWhindcheck_disp", s66);
				map.put("nodays_disp", s72);
				map.put("whappnbr_disp", s68);
				map.put("remarks_disp", s70);
			}
			map.put("billName", s44);
			map.put("custCd", s1);
			map.put("truckerIcNo", s19);
			map.put("truckerName", s20);
			map.put("uaNoPkgs", "" + k);
			map.put("clsShpInd", s42);
			map.put("pkgsDesc", s43);
			map.put("weight", "" + d);
			map.put("volume", "" + d1);
			map.put("chkClsShpInd", s60);

			map.put("cntrNbr", cntrNbr);
			map.put("appNo", appNo);
			map.put("varNbr", loadVVCd);
			map.put("cntrSeqNbr", cntrSeqNbr);

			String scheme = eSNService.getVesselScheme(s41);
			map.put("scheme", scheme);
			// Add by Ding Xijia(harbortek) 09-Feb-2011 : END

			// HaiTTH1 added on 11/2/2014
			map.put("truckerCNo", trucker_ct);
			map.put("truckerNbrPkgs", trucker_pkg);

			map.put("ListData", topsModel);
			map.put("data", topsModel); 

			// START CR FTZ HSCODE - NS JULY 2024
			List<HsCodeDetails> hscodeDetailsList = eSNService.getHsCodeDetailList(s35);
			map.put("hscodeDetailsList", hscodeDetailsList);
			// END CR FTZ HSCODE - NS JULY 2024
			
		} catch (BusinessException be) {
			log.info("Exception EsnAmendConfirm: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception EsnAmendConfirm : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(mapError);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: EsnAmendConfirm result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.eSNService --> EsnCancelHandler
	@PostMapping(value = "/EsnCancel")
	public ResponseEntity<?> EsnCancel(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: EsnCancel criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			String strUserId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String esnNo = "";
			String bookingRefNo = "";
			String strUAFlag = "";

			bookingRefNo = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo"));
			esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			strUAFlag = CommonUtility.deNull(criteria.getPredicates().get("UAflag"));
			EsnListValueObject esnListValueObject = null;

			if (esnNo != null && !esnNo.equals("")) {
				boolean isDelete = eSNService.checkDeleteEsn(esnNo);
				if (!isDelete) {
					errorMessage = ConstantUtil.ErrorMsg_ASN_Tagged;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnCancel result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}

				if ("UA".equals(strUAFlag) && eSNService.isBillChargesRaised(esnNo)) {
					errorMessage = ConstantUtil.ErrorMsg_Bill_Raised_UA_Delete;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnCancel result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				} 
				if (eSNService.isAsnShut(esnNo)) {
					errorMessage = ConstantUtil.ErrorMsg_ESN_After_Shutout;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnCancel result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				eSNService.esnCancel(esnNo, bookingRefNo, strUAFlag, strUserId);
			}
			esnListValueObject = new EsnListValueObject();
			topsModel.put(esnListValueObject);
			map.put("esnList", topsModel);
			map.put("ListData", topsModel);

		} catch (BusinessException be) {
			log.info("Exception EsnCancel: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception EsnCancel : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(mapError);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: EsnCancel result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.ops.dnua.ua-->UAEsnSearchHandler
	@PostMapping(value = "/UAEsnSearch")
	public ResponseEntity<?> UAEsnSearch(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;

		try {
			log.info("START: UAEsnSearch criteria:" + criteria.toString());
			List<UaEsnDetValueObject> vector1 = null;
			List<UaListObject> vector3 = null;
			
			
			String s = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			// String s1 =
			// CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String s2 = "";
			String s3 = "";
			String s4 = CommonUtility.deNull(criteria.getPredicates().get("disp"));
			String s5 = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			String s6 = CommonUtility.deNull(criteria.getPredicates().get("transtype"));
			String s7 = CommonUtility.deNull(criteria.getPredicates().get("ftransdtm"));
			String esnasnnbr =  CommonUtility.deNull(criteria.getPredicates().get("esnasnnbr"));
			String userId = s;
			map.put("userId", userId);
			/*
			 * >> Add by FPT.Thai - Oct 03 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			String linkmode = CommonUtility.deNull(criteria.getPredicates().get("linkmode"));
			/*
			 * << Add by FPT.Thai - Oct 03 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */

			if (s4 != null && !s4.equals("") && s4.equals("Updftrans")) {
				SimpleDateFormat dFmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				Timestamp uAtransDttm = null;
            	Timestamp opsDate = null;
            	String uAtransDttmExist = "";
            	try {
            		opsDate = CommonUtility.toTimestamp(dFmt.parse(s7));
            		uAtransDttmExist = eSNService.getUAtransDttm(s5);
            		uAtransDttm = CommonUtility.toTimestamp(dFmt.parse(uAtransDttmExist));
            	} catch (Exception e) {
            		log.info("Exception UAEsnSearch: ", e);
            	}
            	if (uAtransDttm.equals(opsDate)) {
            		eSNService.updFtrans(s5, s6, s7, userId);
            	} else {
            		log.info("Exception UAEsnSearch: Trans Dttm Already Exist: " + uAtransDttmExist.toString());
            		throw new BusinessException("M43183");
            	}
			}
				
			try {
				if (esnasnnbr != null)
					s2 = esnasnnbr;
			} catch (Exception exception) {
				log.info("Exception EsnCancel : ", exception);
				throw new BusinessException("M4201");
			}
			List<UaEsnListValueObject> vector4 = new ArrayList<UaEsnListValueObject>();
			log.info("esn number in search handler **:" + s2);
			vector4 = eSNService.getEsnList(s2);
			log.info("GetESNLIst ***" + vector4.size());
			// Nothing found by esn then could be in transferred cargo
			if (vector4.size() == 0) {
				List<UaEsnListValueObject> transferredCargo = eSNService.getTransferredCargo(s2);
				map.put("esnTransCargoList", transferredCargo);
				log.info("GetENSLIst if part***" + transferredCargo.size());
			}

			if (s4 != null && !s4.equals("") && (s4.equals("View") || s4.equals("Updftrans"))) {
            	
				vector1 = eSNService.getEsnView(s5, s6);
				vector3 = eSNService.getUAList(s5);
				// Check esn whether it associate with cntr
				if (eSNService.checkESNCntr(s5))
					map.put("esncntr", "YES");
				else
					map.put("esncntr", "NO");

				map.put("esnView", vector1);
				map.put("uaList", vector3);
			}

			map.put("esnlist", vector4);
			s3 = eSNService.getSysdate();
			map.put("sysdate", s3);

			// Added By chua 25 JUL 2008 BEGIN
			// CashSalesHome cshome = (CashSalesHome)
			// ejbhomefactory.lookUpHome("CashSales");
			// CashSales csRemote = cshome.create();
			// map.put("csRemote", csRemote);
			// Added By chua 25 JUL 2008 END
			/*
			 * >> Add by FPT.Thai - Oct 03 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			map.put("linkmode", linkmode);
			/*
			 * << Add by FPT.Thai - Oct 03 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			if (s4 != null && !s4.equals("") && (s4.equals("View") || s4.equals("Updftrans"))) {
				// nextScreen(httpservletrequest, "UAEsnView");
			} else {
				// nextScreen(httpservletrequest, "UAFPSvlt");
			}
			
			setParametersFormServlet(map,vector1,vector3);
			
		} catch (BusinessException be) {
			log.info("Exception UAEsnSearch: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception UAEsnSearch : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(mapError);
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

	private void setParametersFormServlet(Map<String, Object> map, List<UaEsnDetValueObject> esnViewMap, List<UaListObject> uaListMap) throws BusinessException {
		String esnno = "";
		String vslnm = "";
		String invoy = "";
		String outvoy = "";
		String bkref = "";
		String paymode = "";
		String bpname = "";
		String dcpkgs = "";
		String balpkgs = "";
		String storepkgs = "";
		String wt = "";
		String vol = "";
		String truknm = "";
		String truckic = "";
		String truckphno = "";
		String ftrans = "";
		String crgdesc = "";
		String markings = "";
		String transtype = "";
		String actnbr = "";
		String sysdate = "";
		String whInd = "";
		String whRemarks = "";
		String whAggrNbr = "";
		String isTesnJPJP = "";
		String terminal = "";
		String scheme = "";
		String subScheme = "";
		String gcOperations = "";

		sysdate = (String) map.get("sysdate");
		// added by HoaBT2: get userId

		int listsize = 0;

		List<UaEsnDetValueObject> esnView = new ArrayList<UaEsnDetValueObject>();
		List<UaListObject> uaList = new ArrayList<UaListObject>();

		esnView =   esnViewMap;
		uaList =  uaListMap;

		log.info("START: setParametersFormServlet. map: " + map + " , esnViewMap: " + esnViewMap + " , uaListMap " + uaListMap);
		
		for (int i = 0; i < esnView.size(); i++) {
			UaEsnDetValueObject esnObj = new UaEsnDetValueObject();
			esnObj = esnView.get(i);

			esnno = esnObj.getEsn_asn_nbr();
			vslnm = esnObj.getVessel_name();
			invoy = esnObj.getIn_voy_nbr();
			outvoy = esnObj.getOut_voy_nbr();
			bkref = esnObj.getBk_ref_nbr();
			paymode = (esnObj.getPay_mode()).equals("C") ? "Cash" : "Account";
			bpname = esnObj.getBill_party();
			dcpkgs = esnObj.getDecl_pkg();
			balpkgs = esnObj.getBal_pkg();
			storepkgs = esnObj.getPkg_stored();
			wt = esnObj.getWeight();
			vol = esnObj.getVolume();
			truknm = esnObj.getTrucker_name();
			truckphno = esnObj.getTrucker_cont_no();
			truckic = esnObj.getTrucker_ic();
			ftrans = esnObj.getFirst_trans();
			crgdesc = esnObj.getCargo_desc();
			markings = esnObj.getCargo_markings();
			transtype = esnObj.getTrans_type();
			whInd = esnObj.getWhInd();
			whAggrNbr = esnObj.getWhAggrNbr();
			whRemarks = esnObj.getWhRemarks();
			terminal = esnObj.getTerminal();
			scheme = esnObj.getScheme();
			subScheme = esnObj.getSubScheme();
			gcOperations = esnObj.getGcOperations();
			actnbr = esnObj.getAct_no();
			if (actnbr != null && !actnbr.equals("") && (actnbr.equals("CA") || actnbr.equals("CASH")))
				actnbr = "CASH";
		}

		if (uaList == null)
			listsize = 0;
		else
			listsize = uaList.size();

		// Added By chua 25 JUL 2008 END

		// Added By Vietnv 25/03/2014
		if (StringUtils.equalsIgnoreCase(transtype, "A")) {
			isTesnJPJP = "disabled";
		}

		//

		List<String> listCashReceiptNbr = new ArrayList<String>();
		try {
			for (int i = 0; i < listsize; i++) {
				UaListObject uaObj = new UaListObject();
				uaObj = uaList.get(i);

					String CashReceiptNbr = CommonUtility.deNull((eSNService.getCashSales(uaObj.getUa_nbr())).getCashReceiptNbr().toString());
					listCashReceiptNbr.add(CashReceiptNbr);
				
			}
		} catch (Exception e) {
			log.info("Exception setParametersFormServlet : ", e);
			throw new BusinessException("M4201");
		}

		map.put("esnno", esnno);
		map.put("vslnm", vslnm);
		map.put("invoy", invoy);
		map.put("outvoy", outvoy);
		map.put("bkref", bkref);
		map.put("paymode", paymode);
		map.put("bpname", bpname);
		map.put("dcpkgs", dcpkgs);
		map.put("balpkgs", balpkgs);
		map.put("storepkgs", storepkgs);
		map.put("wt", wt);
		map.put("vol", vol);
		map.put("truknm", truknm);
		map.put("truckic", truckic);
		map.put("truckphno", truckphno);
		map.put("ftrans", ftrans);
		map.put("crgdesc", crgdesc);
		map.put("markings", markings);
		map.put("transtype", transtype);
		map.put("actnbr", actnbr);
		map.put("sysdate", sysdate);
		map.put("whInd", whInd);
		map.put("whRemarks", whRemarks);
		map.put("whAggrNbr", whAggrNbr);
		map.put("isTesnJPJP", isTesnJPJP);
		map.put("terminal", terminal);
		map.put("scheme", scheme);
		map.put("subScheme", subScheme);
		map.put("gcOperations", gcOperations);

		map.put("listCashReceiptNbr", listCashReceiptNbr);
	}

	
	// delegate.helper.gbms.cargo.esn --> SearchEsnDetailHandler
	@PostMapping(value = "/SearchEsnDetail")
	public ResponseEntity<?> SearchEsnDetail(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START: SearchEsnDetail criteria:" + criteria.toString());

			String s = "";
			String s1 = "";
			// s=CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			s = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			s1 = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));

			log.info("BEFORE CALLING GETVESSELLISTSEARCH");
			List<VesselVoyValueObject> vector = new ArrayList<VesselVoyValueObject>();
			vector = eSNService.getVesselListSearch(s, s1);
			log.info("AFTER CALLING GETVESSELLISTSEARCH" + vector.size());

			for (int i = 0; i < vector.size(); i++) {
				VesselVoyValueObject vesselvoyvalueobject1 = vector.get(i);
				topsModel.put(vesselvoyvalueobject1);
			}

			List<EsnListValueObject> vector1 = new ArrayList<EsnListValueObject>();
			List<EsnListValueObject> vector2 = new ArrayList<EsnListValueObject>();
			vector1 = eSNService.getEsnDetails(s1, s);
			log.info("AFTER CALLING getEsnDetails" + vector1.size());
			vector2 = eSNService.getCntrDetails(s1);
			log.info("AFTER CALLING getCntrDetails" + vector2.size());

			if (vector.size() == 0 || vector1.size() == 0) {
				errorMessage = ConstantUtil.ErrorMsg_ESN_Cannot_Found;
				if (errorMessage != null) {
					mapError.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(mapError);
					result.setSuccess(false);
					result.setData(map);

				} else {
					result.setData(map);
					result.setSuccess(true);
					log.info("END: SearchEsnDetail result: " + result.toString());
				}

				return ResponseEntityUtil.success(result.toString());
			}

			String s4 = "";
			if (vector1.size() > 0) {
				for (int j = 0; j < vector1.size(); j++) {
					EsnListValueObject esnlistvalueobject = vector1.get(j);
					String s3 = esnlistvalueobject.getBookingRefNo();
					s4 = eSNService.getClsShipInd_bkr(s3);
				}

			}
			String s5 = eSNService.AssignWhindCheck(s1);
			String s6 = "";
			String s7 = "";
			String s8 = "";
			if (s5 != null && !s5.equals("") && s5.equals("Y")) {
				List<String> vector3 = new ArrayList<String>();
				vector3 = eSNService.getWHDetails(s5, s1);
				if (vector3.size() == 1) {
					s7 = (String) vector3.get(0);
				} else if (vector3.size() == 2) {
					s7 = (String) vector3.get(0);
					s6 = (String) vector3.get(1);
				}
			} else {
				List<String> vector4 = new ArrayList<String>();
				vector4 = eSNService.getWHDetails(s5, s1);
				if (vector4.size() > 0) {
					s8 = (String) vector4.get(0);
				}
			}

			// ++ VietND02
			String existSubAdp = "False";
			if (eSNService.checkExistSubAdp(s1))
				existSubAdp = "True";
			// --
			map.put("existSubAdp", existSubAdp);

			map.put("esnWhindcheck_disp", s5);
			map.put("nodays_disp", s8);
			map.put("whappnbr_disp", s6);
			map.put("remarks_disp", s7);
			map.put("esnDetails", vector1);
			map.put("cntrDetails", vector2);
			map.put("custCd", s);
			map.put("chkClsShpInd", s4);

			map.put("vesselvoyvalueobject1", topsModel);
			
			
			// START CR FTZ HSCODE - NS DEC 2024
			List<HsCodeDetails> hscodeDetailsList = eSNService.getHsCodeDetailList(s1);
			map.put("hscodeDetailsList", hscodeDetailsList);
			// END CR FTZ HSCODE - NS DEC 2024

		} catch (BusinessException be) {
			log.info("Exception SearchEsnDetail: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception SearchEsnDetail : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(mapError);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: SearchEsnDetail result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.esn --> EsnAddHandler
	@PostMapping(value = "/EsnAdd")
	public ResponseEntity<?> EsnAdd(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START: EsnAdd criteria:" + criteria.toString());

			// String s = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			coCd=coCd.equals("")?"JP":coCd;
			String bookingRefNo = "";
			bookingRefNo = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo").trim());
			EsnListValueObject esnListValueObject = null;

			// add by Zhenguo Deng on 14/02/2011 for Cargo Category
			String vslType = eSNService.getVesselType(bookingRefNo);
			map.put("vslType", vslType);
			if (vslType.equals("CC")) {
				List<Map<String, Object>> list = eSNService.getCategoryList();
				map.put("categoryList", list);
			}
			// end add

			List<EsnListValueObject> bookingRefList = new ArrayList<EsnListValueObject>();
			bookingRefList = eSNService.getBkRefNo(bookingRefNo, coCd);
			if (bookingRefList != null && bookingRefList.size() != 0) {

				String esnDeclared = eSNService.getEsnDeclared(bookingRefNo);
				String bkStatus = eSNService.getBkStatus(bookingRefNo);
				String esnDeclarentCode = eSNService.getDeclarentCd(bookingRefNo);
				String vvStatus = eSNService.getVvStatus(bookingRefNo);
				Boolean vesselATUDttm = eSNService.getVesselATUDttm(bookingRefNo);
				/**
				 * Revision History ---------------- Author Request Number Description of Change
				 * Version Date Released SRINI added getTerminal() 1.2 09 sept 2003 changed
				 * getVarno()
				 */

				/**
				 * Added by srini on 9 sept 2003 to get the Terminal from vessel_call table.
				 * This method is added to prompt the user by checking the vessel status along
				 * with the terminal .
				 */
				String terminal = eSNService.getTerminal(bookingRefNo);
				String closedVslInd = eSNService.getClsVslInd(bookingRefNo);
//				String closedBjInd = eSNService.getClsBjInd(bookingRefNo); // not used

				if (esnDeclared.equals("Y")) {
					errorMessage = ConstantUtil.ErrorMsg_BkRef_Used;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				if (bkStatus.equals("X")) {
					errorMessage = String.valueOf(String
							.valueOf((new StringBuffer("The Booking Reference with BR. No. ")).append(bookingRefNo)
									.append("has been canceled.  Please use a different BR. No.")));
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				if (!coCd.equals("JP")) {
					if (vvStatus.equals("UB")) {
						errorMessage = ConstantUtil.ErrorMsg_ESN_Unberth;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAdd result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
					if (!esnDeclarentCode.equals(coCd)) {
						errorMessage = ConstantUtil.ErrorMsg_Not_Authorized_BrNbr;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAdd result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				}
				if (vvStatus.equals("CX")) {
					errorMessage = ConstantUtil.ErrorMsg_BerthApp_Cancel;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				/*
				 * Added by srini on 9 sept 2003 to check the terminal from vessel_call table.
				 */
				if (vvStatus.equals("CL") && terminal.equals("GB")) {
					errorMessage = ConstantUtil.ErrorMsg_BR_Vessel_Record_Closed;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				if (closedVslInd.equals("Y")) {
					errorMessage = ConstantUtil.ErrorMsg_Vessel_Record_Closed;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				if(vesselATUDttm) {
					errorMessage = ConstantUtil.ErrorMsg_Vessel_ATU;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			} else {
				// Begin ThanhPT6, JCMS CR
				// errorMessage(request, "BR No cannot be used");
				// return;
				List<EsnListValueObject> bookingRefList2 = new ArrayList<EsnListValueObject>();
				bookingRefList2 = eSNService.getBkRefNo(bookingRefNo, "JP");

				if (bookingRefList2 != null && bookingRefList2.size() != 0 && !"JP".equals(coCd)) {
					errorMessage = ConstantUtil.ErrorMsg_BR_ESN_Declarant;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				} else {
					errorMessage = ConstantUtil.ErrorMsg_BR_Not_Found;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}

				// End ThanhPT6, JCMS CR
			}
			esnListValueObject = new EsnListValueObject();
			for (int i = 0; i < bookingRefList.size(); i++) {
				esnListValueObject = bookingRefList.get(i);
				topsModel.put(esnListValueObject);
			}
			String vvCd = "";
			if (esnListValueObject != null) {
				vvCd = esnListValueObject.getVarNbr();
			}
			boolean checkResult = eSNService.checkDisbaleOverSideFroDPE(vvCd);
			String hiddenOverSide = "FALSE";
			if (!StringUtils.equalsIgnoreCase("JP", coCd) && checkResult) {
				hiddenOverSide = "TRUE";
			}
			map.put("hiddenOverSide", hiddenOverSide);

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			List<String> hsCodeList = new ArrayList<String>();

			List<ManifestValueObject> listManifestValueObject = eSNService.getHSCodeList("1");
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

			String mode = "";
			String noOfPackage = "";
			String packageType = "";
			String weight = "";
			String volume = "";
			String cargoDesc = "";
			String hsCode = "";
			String hsSubCode = "";
			String hsSubCodeFr = "";
			String hsSubCodeTo = "";
			String hsSubCodeDesc = "";
			String marking = "";
			String lopInd = "";
			String loadFrom = "";
			String dgInd = "";
			String storageInd = "";
			String dutyGoodInd = "";
			String stuffInd = "";
			String noOfStorageDay = "";
			String deliveryToEPC = "";
			String customHsCode = "";
//			String[] listValueTruckerIcNo = new String[0];
//			if (request.getParameterValues("truckerIcNo") != null) {
//				listValueTruckerIcNo = (String[]) request.getParameterValues("truckerIcNo");
//			}
//			String[] listValueTruckerName = new String[0];
//			ArrayList<String> trk_nm_list = new ArrayList<String>();
//			if (request.getParameterValues("truckerName") != null) {
//				listValueTruckerName = (String[]) request.getParameterValues("truckerName");
//				trk_nm_list = new ArrayList<String>(Arrays.asList(listValueTruckerName));
//			}
//			map.put("listTruckerName", trk_nm_list);
//			String[] listValueTruckerCNo = new String[0];
//			ArrayList<String> trk_cnt_list = new ArrayList<String>();
//			if (request.getParameterValues("truckerCNo") != null) {
//				listValueTruckerCNo = (String[]) request.getParameterValues("truckerCNo");
//				trk_cnt_list = new ArrayList<String>(Arrays.asList(listValueTruckerCNo));
//			}
//			map.put("listTruckerCNo", trk_cnt_list);
//			String[] listValueNbrpkg = new String[0];
//			if (request.getParameterValues("nbrpkg") != null) {
//				listValueNbrpkg = (String[]) request.getParameterValues("nbrpkg");
//			}

			mode = CommonUtility.deNull(criteria.getPredicates().get("addMode"));
			// ArrayList trucker_list = new ArrayList();
			
			//Add by NS 25112022 : Change request array parameter value.
			List<TruckerValueObject> truckerVector = new ArrayList<TruckerValueObject>();
			List<String> listValueTruckerIcNo =  new ArrayList<String>(); 
			String truckerIcNo;
			List<String> listValueTruckerName =  new ArrayList<String>(); 
			String truckerNmNo;
			List<String> listValueTruckerCNo =  new ArrayList<String>(); 
			String truckerContactNo;
			List<String> listValueNbrpkg =  new ArrayList<String>(); 
			String truckerPkgsNo;
			
			if (StringUtils.equalsIgnoreCase("TRUCKER", mode)) {
				int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
			
				if (sizeSelected != 0) {
					for (int i = 0; i < sizeSelected; i++) {
						truckerIcNo = (String) criteria.getPredicates().get("truckerIc" + i);
						listValueTruckerIcNo.add(truckerIcNo);
						truckerNmNo = (String) criteria.getPredicates().get("truckerNm" + i);
						listValueTruckerName.add(truckerNmNo);
						truckerContactNo = (String) criteria.getPredicates().get("truckerContact" + i);
						listValueTruckerCNo.add(truckerContactNo);
						truckerPkgsNo = (String) criteria.getPredicates().get("truckerPkgs" + i);
						listValueNbrpkg.add(truckerPkgsNo);
					}
				}
			
			
				String truckerDelete = CommonUtility.deNull(criteria.getPredicates().get("truckerDelete"));
				if (!StringUtils.equalsIgnoreCase("delete", truckerDelete)) {
					int seq = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("TrkSeq")));
					if (sizeSelected != 0) {
//						listValueTruckerIcNo = (String[]) request.getParameterValues("truckerIcNo");
						if (listValueTruckerIcNo.size() >= seq) {
							String trucker_ciNo = listValueTruckerIcNo.get(seq - 1);
							log.info("trucker_ciNo: " + trucker_ciNo);
//							String trucker_cd = "";
							String trucker_nm = "";
							String trucker_contact = "";
							// boolean isValid = false;
							// if (!StringUtils.isEmpty(trucker_ciNo)) {
							// isValid = eSNService.checkValidTrucker(trucker_ciNo);
							// }
							// if (isValid) {
							TruckerValueObject trkObj = new TruckerValueObject();
							trkObj = eSNService.getTruckerDetails(trucker_ciNo);
//							trucker_cd = trkObj.getTruckerCd(); // eSNService.getTruckerCd(trucker_ciNo);
							trucker_nm = trkObj.getTruckerNm(); // eSNService.getTruckerName(coCd, trucker_ciNo);
							trucker_contact = trkObj.getTruckerContact(); // eSNService.getTruckerContact(trucker_cd);
							listValueTruckerName.set(seq - 1, trucker_nm);
							listValueTruckerCNo.set(seq - 1, trucker_contact);
							// } else {
							// listValueTruckerName[seq-1] = "";
							// listValueTruckerCNo[seq-1] = "";
							// }
							
							for (int i = 0; i < listValueTruckerIcNo.size(); i++) {
								TruckerValueObject trk_obj = new TruckerValueObject();
								trk_obj.setTruckerIc(listValueTruckerIcNo.get(i));
								trk_obj.setTruckerNm(listValueTruckerName.get(i));
								trk_obj.setTruckerContact(listValueTruckerCNo.get(i));
								trk_obj.setTruckerPkgs(listValueNbrpkg.get(i));
								truckerVector.add(trk_obj);
							}
						}

					}

					noOfPackage = CommonUtility.deNull(criteria.getPredicates().get("noOfPkgs"));
				} else {
					if (listValueNbrpkg.size() > 0) {
						int number = 0;
						for (int i = 0; i < listValueNbrpkg.size(); i++) {
							if (listValueNbrpkg.get(i) != null && !StringUtils.isEmpty(listValueNbrpkg.get(i))
									&& StringUtils.isNumeric(listValueNbrpkg.get(i))) {
								number = number + Integer.parseInt(listValueNbrpkg.get(i));
							}

						}
						noOfPackage = String.valueOf(number);
					} else {
						noOfPackage = "0";
					}
				}

				packageType = CommonUtility.deNull(criteria.getPredicates().get("pkgsType"));
				weight = CommonUtility.deNull(criteria.getPredicates().get("weight"));
				volume = CommonUtility.deNull(criteria.getPredicates().get("volume"));
				cargoDesc = CommonUtility.deNull(criteria.getPredicates().get("cargoDesc"));
				hsCode = CommonUtility.deNull(criteria.getPredicates().get("hsCode"));
				hsSubCode = CommonUtility.deNull(criteria.getPredicates().get("hsSubCode"));
				hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
				hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));
				customHsCode = CommonUtility.deNull(criteria.getPredicates().get("customHsCode"));

				// Added back end validation 12 Aug 2015 for hs code sub code not null
				if (hsSubCode == null || hsSubCodeFr == null || hsSubCodeTo == null
						|| hsSubCodeFr.trim().equalsIgnoreCase("") || hsSubCode.trim().equalsIgnoreCase("")
						|| hsSubCodeTo.trim().equalsIgnoreCase("")) {
					errorMessage = ConstantUtil.ErrorMsg_HSsubCode_Null;

					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				} else {
					log.info("HS Code value ***" + hsSubCode + ":" + hsSubCodeFr + ":" + hsSubCodeTo);
				}

				// to check hs subcode, hssubcodefrom , hssubcodeto 3 Nov 2016 Start
				/*
				 * String hssubcodeDesc = mftrem.getHSSubCodeDes(hsSubCode, hsSubCodeFr,
				 * hsSubCodeTo); if (hssubcodeDesc == null ||
				 * hssubcodeDesc.equalsIgnoreCase("")){ errorMessage(
				 * request,"HS Sub code From & To is not valid for the selected HS Sub Code");
				 * return; }
				 */ // shifted to Esn Add Confirm Handler
				// to check hs subcode, hssubcodefrom , hssubcodeto 3 Nov 2016 End

				hsSubCodeDesc = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDesc"));
				marking = CommonUtility.deNull(criteria.getPredicates().get("marking"));
				lopInd = CommonUtility.deNull(criteria.getPredicates().get("lopInd"));
				loadFrom = CommonUtility.deNull(criteria.getPredicates().get("loadFrom"));
				dgInd = CommonUtility.deNull(criteria.getPredicates().get("dgInd"));
				storageInd = CommonUtility.deNull(criteria.getPredicates().get("storageInd"));
				dutyGoodInd = CommonUtility.deNull(criteria.getPredicates().get("dutyGoodInd"));
				stuffInd = CommonUtility.deNull(criteria.getPredicates().get("stuffInd"));
				noOfStorageDay = CommonUtility.deNull(criteria.getPredicates().get("noOfStorageDay"));
				deliveryToEPC = CommonUtility.deNull(criteria.getPredicates().get("deliveryToEPC"));
			} else {
				listValueTruckerIcNo = new ArrayList<String>(); 
				listValueTruckerName = new ArrayList<String>(); 
				listValueTruckerCNo = new ArrayList<String>(); 
				listValueNbrpkg = new ArrayList<String>(); 
			}

			map.put("truckerList", truckerVector);
			map.put("mode", mode);
			map.put("noOfPackageValue", noOfPackage);
			map.put("packageTypeValue", packageType);
			map.put("weightValue", weight);
			map.put("volumeValue", volume);
			map.put("cargoDescValue", cargoDesc);
			map.put("hsCodeValue", hsCode);
			map.put("hsSubCodeValue", hsSubCode);
			map.put("hsSubCodeFrValue", hsSubCodeFr);
			map.put("hsSubCodeToValue", hsSubCodeTo);
			map.put("hsSubCodeDescValue", hsSubCodeDesc);
			map.put("markingValue", marking);
			map.put("lopIndValue", lopInd);
			map.put("loadFromValue", loadFrom);
			map.put("dgIndValue", dgInd);
			map.put("storageIndValue", storageInd);
			map.put("dutyGoodIndValue", dutyGoodInd);
			map.put("stuffIndValue", stuffInd);
			map.put("noOfStorageDayValue", noOfStorageDay);
			map.put("listValueTruckerIcNo", listValueTruckerIcNo);
			map.put("listValueTruckerName", listValueTruckerName);
			map.put("listValueTruckerCNo", listValueTruckerCNo);
			map.put("listValueNbrpkg", listValueNbrpkg);
			map.put("deliveryToEPC", deliveryToEPC);

			map.put("custCd", coCd);
			map.put("maxiTrucker",MAX_ADP_TRUCKER);
			map.put("model", topsModel);
			esnAddsetParameters(map, topsModel);
			
			map.put("customHsCode", customHsCode);

		} catch (BusinessException be) {
			log.info("Exception EsnAdd: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception EsnAdd : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(mapError);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: EsnAdd result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	public void esnAddsetParameters(Map<String, Object> map, TopsModel topsModel) {

		int row = 0;

		String bookingRefNo = "";
		String voyNo = "";
		String vslName = "";
		String varNbr = "";
		String crgType = "";
		String crgCategoryCode = "";
		String crgCategoryName = "";
		String shipperName = "";
		String portD = "";
		String portDesc = "";
		String contType = "";
		String contSize = "";
		double weight = 0;
		double volume = 0;
		int noOfPkgs = 0;
		double varWt = 0;
		double varVl = 0;
		double varNp = 0;
		// HaiTTH1 added on 10/1/2014
		String scheme = "";

		try {
			log.info("START: esnAddsetParameters. map: " + map + " , topsModel: " + topsModel);
			if (topsModel == null) {
				row = 0;
			} else {
				row = topsModel.getSize();
			}
			Map<String, String> cargoCategoryCode_cargoCategoryName = eSNService
					.getCargoCategoryCode_CargoCategoryName();

			int noOfContr = 0;
			for (int i = 0; i < row; i++) {
				EsnListValueObject esnListValueObject = (EsnListValueObject) topsModel.get(i);

				vslName = esnListValueObject.getVslName();
				voyNo = esnListValueObject.getVoyNo();
				varNbr = esnListValueObject.getVarNbr();
				bookingRefNo = esnListValueObject.getBookingRefNo();
				crgType = esnListValueObject.getCrgType();
				crgCategoryCode = esnListValueObject.getCategory();
				crgCategoryName = cargoCategoryCode_cargoCategoryName.get(crgCategoryCode);
				shipperName = esnListValueObject.getShipperName();
				portD = esnListValueObject.getPortD();
				portDesc = esnListValueObject.getPortL();
				contType = esnListValueObject.getCntrType();
				contSize = esnListValueObject.getCntrSize();
				noOfContr = esnListValueObject.getNoOfCntr();
				weight = esnListValueObject.getGrWt();
				volume = esnListValueObject.getGrVolume();
				noOfPkgs = esnListValueObject.getBNoofPkgs();
				varWt = esnListValueObject.getVarGrWt();
				varVl = esnListValueObject.getVarGrVolume();
				varNp = esnListValueObject.getVarNoofPakgs();
				// HaiTTH1 added on 10/1/2014
				scheme = esnListValueObject.getScheme();
			}

			map.put("vslName", vslName);
			map.put("voyNo", voyNo);
			map.put("varNbr", varNbr);
			map.put("bookingRefNo", bookingRefNo);
			map.put("crgType", crgType);
			map.put("category", crgCategoryCode);
			map.put("crgCategoryName", crgCategoryName);
			map.put("shipperName", shipperName);
			map.put("portD", portD);
			map.put("portDesc", portDesc);
			map.put("contType", contType);
			map.put("contSize", contSize);
			map.put("noOfContr", "" + noOfContr);
			map.put("weight", "" + weight);
			map.put("volume", "" + volume);
			map.put("varNp", "" + varNp);
			map.put("varWt", "" + varWt);
			map.put("varVl", "" + varVl);
			map.put("noOfPkgs", "" + noOfPkgs);
			// HaiTTH1 added on 10/1/2014
			map.put("scheme", "" + scheme);

		} catch (BusinessException be) {
			log.info("Exception esnAddsetParameters: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception esnAddsetParameters : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			log.info("END: esnAddsetParameters");
		}

	}

	// delegate.helper.gbms.cargo.esn --> EsnAddConfirmHandler
	@PostMapping(value = "/EsnAddConfirm")
	public ResponseEntity<?> EsnAddConfirm(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START: EsnAddConfirm criteria:" + criteria.toString());

			String UserID = "";
			String custCd = "";
			UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String bookingRefNo = "";
			int noOfPkgs = 0;
			String hsCode = "";
			String pkgsType = "";
			String pkgsDesc = "";
			double weight = 0;
			double volume = 0;
			String cargoDesc = "";
			String marking = "";
			String loadOperInd = "";
			String loadFrom = "";
			String dgInd = "";
			String storageInd = "";
			int noOfStorageDay = 0;
			String truckerIcNo = "";
			String truckerName = "";
			String truckerCNo = "";
			int truckerPkg = 0;
			String portD = "";
			String dutyGoodInd = "";
			String accNo_I = "";
			String payMode = "";
			String insert = "";
			String varNbr = "";
			String esnNo = "";
			String truckerCd = "";
			String cntr1 = "";
			String cntr2 = "";
			String cntr3 = "";
			String cntr4 = "";
			String strStfInd = "";// vani
			int uaNoPkgs = 0;
			String clsShpInd = "";
			String billName = "";
			String deliveryToEPC = ""; // MCC for EPC_IND

			String cntrNbr = "";
			String appNo = "";
			String loadVVCd = "";
			String cntrSeqNbr = "";

			boolean checkNoOfPkgs;
			boolean checkPkgsType;
			boolean checkWeight;
			boolean checkVolume;
			boolean checkAccNo;

			// HaiTTH1 added on 7/1/2014
			insert = CommonUtility.deNull(criteria.getPredicates().get("insert"));
			String isDisableJPBillingNo = "";
			int totalNbrPkgs = 0;

//			String[] listValueTruckerIcNo = new String[0];
//			String[] listValueTruckerName = new String[0];
//			String[] listValueTruckerCNo = new String[0];
//			String[] listValueNbrpkg = new String[0];
			
			List<String> listValueTruckerIcNo =  new ArrayList<String>(); 
			String trckerIcNo;
			List<String> listValueTruckerName =  new ArrayList<String>(); 
			String truckerNmNo;
			List<String> listValueTruckerCNo =  new ArrayList<String>(); 
			String truckerContactNo;
			List<String> listValueNbrpkg =  new ArrayList<String>(); 
			String truckerPkgsNo;
			
			EsnListValueObject esnListValueObject = null;

			int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
			if (insert != null && insert.equals("insert")) {
				for (int i = 0; i < sizeSelected; i++) {
					trckerIcNo = (String) criteria.getPredicates().get("truckerIcNo_hidden" + i);
					listValueTruckerIcNo.add(trckerIcNo);
					truckerNmNo = (String) criteria.getPredicates().get("truckerName_hidden" + i);
					listValueTruckerName.add(truckerNmNo);
					truckerContactNo = (String) criteria.getPredicates().get("truckerCNo_hidden" + i);
					listValueTruckerCNo.add(truckerContactNo);
					truckerPkgsNo = (String) criteria.getPredicates().get("nbrpkg_hidden" + i);
					listValueNbrpkg.add(truckerPkgsNo);
				}
//				if (criteria.getPredicates().get("truckerIcNo_hidden") != null) {
//					listValueTruckerIcNo = (String[]) request.getParameterValues("truckerIcNo_hidden");
//				}
//				if (criteria.getPredicates().get("truckerName_hidden") != null) {
//					listValueTruckerName = (String[]) request.getParameterValues("truckerName_hidden");
//				}
//				if (request.getParameterValues("truckerCNo_hidden") != null) {
//					listValueTruckerCNo = (String[]) request.getParameterValues("truckerCNo_hidden");
//				}
//				if (request.getParameterValues("nbrpkg_hidden") != null) {
//					listValueNbrpkg = (String[]) request.getParameterValues("nbrpkg_hidden");
//				}
			} else {
				for (int i = 0; i < sizeSelected; i++) {
					truckerIcNo = (String) criteria.getPredicates().get("truckerIcNo" + i);
					listValueTruckerIcNo.add(truckerIcNo);
					truckerNmNo = (String) criteria.getPredicates().get("truckerName" + i);
					listValueTruckerName.add(truckerNmNo);
					truckerContactNo = (String) criteria.getPredicates().get("truckerCNo" + i);
					listValueTruckerCNo.add(truckerContactNo);
					truckerPkgsNo = (String) criteria.getPredicates().get("nbrpkg" + i);
					listValueNbrpkg.add(truckerPkgsNo);
				}
//				if (request.getParameterValues("truckerIcNo") != null) {
//					listValueTruckerIcNo = (String[]) request.getParameterValues("truckerIcNo");
//				}
//				if (request.getParameterValues("truckerName") != null) {
//					listValueTruckerName = (String[]) request.getParameterValues("truckerName");
//				}
//				if (request.getParameterValues("truckerCNo") != null) {
//					listValueTruckerCNo = (String[]) request.getParameterValues("truckerCNo");
//				}
//				if (request.getParameterValues("nbrpkg") != null) {
//					listValueNbrpkg = (String[]) request.getParameterValues("nbrpkg");
//				}

				List<String> vector1 = new ArrayList<String>();
				if (listValueTruckerIcNo.size() > 0) {
					for (int i = 0; i < listValueTruckerIcNo.size(); i++) {
						vector1.add(listValueTruckerIcNo.get(i));
					}
				}
				List<String> vector2 = new ArrayList<String>();
				if (listValueTruckerName.size() > 0) {
					for (int i = 0; i < listValueTruckerName.size(); i++) {
						vector2.add(listValueTruckerName.get(i));
					}
				}
				List<String> vector3 = new ArrayList<String>();
				if (listValueTruckerCNo.size() > 0) {
					for (int i = 0; i < listValueTruckerCNo.size(); i++) {
						vector3.add(listValueTruckerCNo.get(i));
					}
				}
				List<String> vector4 = new ArrayList<String>();
				if (listValueNbrpkg.size() > 0) {
					for (int i = 0; i < listValueNbrpkg.size(); i++) {
						vector4.add(listValueNbrpkg.get(i));
						totalNbrPkgs = totalNbrPkgs + Integer.parseInt(listValueNbrpkg.get(i));
					}
				}
			}
			List<String> vector1 = new ArrayList<String>();
			if (listValueTruckerIcNo.size() > 0) {
				for (int i = 0; i < listValueTruckerIcNo.size(); i++) {
					vector1.add(listValueTruckerIcNo.get(i));
				}
			}
			List<String> vector2 = new ArrayList<String>();
			if (listValueTruckerName.size() > 0) {
				for (int i = 0; i < listValueTruckerName.size(); i++) {
					vector2.add(listValueTruckerName.get(i));
				}
			}
			List<String> vector3 = new ArrayList<String>();
			if (listValueTruckerCNo.size() > 0) {
				for (int i = 0; i < listValueTruckerCNo.size(); i++) {
					vector3.add(listValueTruckerCNo.get(i));
				}
			}
			List<String> vector4 = new ArrayList<String>();
			if (listValueNbrpkg.size() > 0) {
				for (int i = 0; i < listValueNbrpkg.size(); i++) {
					vector4.add(listValueNbrpkg.get(i));
				}
			}
			map.put("listValueTruckerIcNo_cf", vector1);
			map.put("listValueTruckerName_cf", vector2);
			map.put("listValueTruckerCNo_cf", vector3);
			map.put("listValueNbrpkg_cf", vector4);


			// for assign bill laks 8 jul

			String schval = "";
			String schind = "";
			// for assign bill laks 8 jul

			varNbr = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));
			bookingRefNo = CommonUtility.deNull(criteria.getPredicates().get("bookingRefNo").trim());
			String noOf_pkgs = CommonUtility.deNull(criteria.getPredicates().get("noOfPkgs"));
			String storageDay = CommonUtility.deNull(criteria.getPredicates().get("noOfStorageDay"));
			if (storageDay != null && !storageDay.equals(""))
				noOfStorageDay = Integer.parseInt(storageDay);
			portD = CommonUtility.deNull(criteria.getPredicates().get("portD"));
			if (noOf_pkgs != null && !noOf_pkgs.equals(""))
				noOfPkgs = Integer.parseInt(noOf_pkgs);
			hsCode = CommonUtility.deNull(criteria.getPredicates().get("hsCode"));
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			String hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
			String hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));

			if (hsSubCodeFr == null || hsSubCodeTo == null || hsSubCodeFr.trim().equalsIgnoreCase("")
					|| hsSubCodeTo.trim().equalsIgnoreCase("")) {
				errorMessage = ConstantUtil.ErrorMsg_HSsubCode_Null;
				if (errorMessage != null) {
					mapError.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(mapError);
					result.setSuccess(false);
					result.setData(map);

				} else {
					result.setData(map);
					result.setSuccess(true);
					log.info("END: EsnAddConfirm result: " + result.toString());
				}

				return ResponseEntityUtil.success(result.toString());
			} else {
				log.info("HS Code value ***" + hsSubCodeFr + ":" + hsSubCodeTo);
			}

			// to check hs subcode, hssubcodefrom , hssubcodeto 3 Nov 2016 Start
			String hssubcodeDesc = eSNService.getHSSubCodeDes(hsCode, hsSubCodeFr, hsSubCodeTo);
			log.info("hssubcodeDesc validity check***" + hssubcodeDesc);
			if (hssubcodeDesc == null || hssubcodeDesc.equalsIgnoreCase("")) {
				errorMessage = ConstantUtil.ErrorMsg_HSsubCode_Invalid;
				if (errorMessage != null) {
					mapError.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(mapError);
					result.setSuccess(false);
					result.setData(map);

				} else {
					result.setData(map);
					result.setSuccess(true);
					log.info("END: EsnAddConfirm result: " + result.toString());
				}

				return ResponseEntityUtil.success(result.toString());
			}
			// log.info("crgType"+crgType);
			pkgsType = CommonUtility.deNull(criteria.getPredicates().get("pkgsType")).trim();
			String weight_s = CommonUtility.deNull(criteria.getPredicates().get("weight"));
			if (weight_s != null && !weight_s.equals(""))
				weight = Double.parseDouble(weight_s);
			// log.info("weight "+weight);
			String volume_s = CommonUtility.deNull(criteria.getPredicates().get("volume"));
			if (volume_s != null && !volume_s.equals(""))
				volume = Double.parseDouble(volume_s);
			cargoDesc = CommonUtility.deNull(criteria.getPredicates().get("cargoDesc").trim());
			// log.info("cargoDesc"+cargoDesc);
			marking = CommonUtility.deNull(criteria.getPredicates().get("marking").trim());
			// log.info("marking"+marking);
			loadOperInd = CommonUtility.deNull(criteria.getPredicates().get("lopInd"));
			loadFrom = CommonUtility.deNull(criteria.getPredicates().get("loadFrom"));
			// log.info("loadFrom"+loadFrom);
			dgInd = CommonUtility.deNull(criteria.getPredicates().get("dgInd"));
			dutyGoodInd = CommonUtility.deNull(criteria.getPredicates().get("dutyGoodInd"));
			// log.info("dgInd"+dgInd);
			storageInd = CommonUtility.deNull(criteria.getPredicates().get("storageInd"));
			cntr1 = CommonUtility.deNull(criteria.getPredicates().get("cntr1"));
			cntr2 = CommonUtility.deNull(criteria.getPredicates().get("cntr2"));
			cntr3 = CommonUtility.deNull(criteria.getPredicates().get("cntr3"));
			cntr4 = CommonUtility.deNull(criteria.getPredicates().get("cntr4"));
			// strStfInd = CommonUtility.deNull(criteria.getPredicates().get("stuffInd");
			strStfInd = CommonUtility.deNull(criteria.getPredicates().get("stfInd"));
			deliveryToEPC = CommonUtility.deNull(criteria.getPredicates().get("deliveryToEPC"));
			
			// START CR FTZ HSCODE - NS JULY 2024

			String customHsCode = CommonUtility.deNull(criteria.getPredicates().get("customHsCode"));
			int hsCodeSize = Integer.valueOf( CommonUtility.deNull(criteria.getPredicates().get("hsCodeSize")));
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
					if((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).indexOf("-") == -1) {
						hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))));					
						hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))));	
					} else {
						hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).split("-")[0]);
						hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).split("-")[1]);
					}
					hsCodeDetails.setGrossVol(CommonUtility.deNull(criteria.getPredicates().get("mSmtArr" + i)));
					hsCodeDetails.setHsSubCodeDesc( CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDescArr" + i)));
					multiHsCodeList.add(hsCodeDetails);
					
					totalWt += Double.valueOf(CommonUtility.deNull(criteria.getPredicates().get("gwtArr" + i)));
					totalVol += Double.valueOf(CommonUtility.deNull(criteria.getPredicates().get("mSmtArr" + i)));
				}
			}
						
			// END CR FTZ HSCODE - NS JULY 2024
			
			log.info("FROM HANDLER EsnAddConfirmHandler strStfInd == " + strStfInd);

			if ("O".equalsIgnoreCase(loadOperInd) && !StringUtils.equalsIgnoreCase("JP", custCd)) {
				isDisableJPBillingNo = "true";
			} else {
				isDisableJPBillingNo = "false";
			}

			// add by Zhenguo Deng on 14/02/2011 for Cargo Category
			String category = "00";
			if (!StringUtils.isEmpty(criteria.getPredicates().get("category"))) {
				category = CommonUtility.deNull(criteria.getPredicates().get("category"));
			}
			;
			// end add

			if (noOf_pkgs != null && !noOf_pkgs.equals("")) {
				checkNoOfPkgs = eSNService.chkNoOfPkgs(bookingRefNo, noOfPkgs);

				if (!checkNoOfPkgs) {
					errorMessage = ConstantUtil.ErrorMsg_Package_Greater;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}

				// haiTTH1 added on 24/1/2014
				if (totalNbrPkgs != 0 && totalNbrPkgs != noOfPkgs) {
					errorMessage = ConstantUtil.ErrorMsg_Package_Equal;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}

			}
			if (dgInd != null && dgInd.compareTo("Y") == 0) {

				boolean flag = eSNService.isOutWardPm(bookingRefNo, varNbr);

				if (!flag) {
					errorMessage = ConstantUtil.ErrorMsg_ESN_Update_PM4_NotApproved;
					// errorMessage(request, "ESN creation not allowed");
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}

			if (pkgsType != null && !pkgsType.equals("")) {
				checkPkgsType = eSNService.chkPkgsType(pkgsType);
				if (!checkPkgsType) {
					errorMessage = ConstantUtil.ErrorMsg_Invalid_PackageType;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}

			if (weight_s != null && !weight_s.equals("")) {
				checkWeight = eSNService.chkWeight(bookingRefNo, weight);
				if (!checkWeight) {
					errorMessage = ConstantUtil.ErrorMsg_Weight_Greater;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}

			if (volume_s != null && !volume_s.equals("")) {
				checkVolume = eSNService.chkVolume(bookingRefNo, volume);
				if (!checkVolume) {
					errorMessage = ConstantUtil.ErrorMsg_Volume_Greater;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			}

			if (strStfInd != null && strStfInd.equalsIgnoreCase("Y")) {
				cntrNbr = CommonUtility.deNull(criteria.getPredicates().get("cntrNbr"));
				appNo = CommonUtility.deNull(criteria.getPredicates().get("appNo"));
				loadVVCd = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));

				if (!eSNService.validateGCStuffIndicatorCntr(loadVVCd, cntrNbr)) {
					errorMessage = ConstantUtil.ErrorMsg_Invalid_CntrNbr;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				} else {
					ContainerValueObject containerVO = eSNService.getContainerInformation(cntrNbr);
					if (containerVO != null) {
						cntrSeqNbr = "" + containerVO.getContainerSeqNo();
					} else {
						errorMessage = ConstantUtil.ErrorMsg_NotFound_CntrNbr;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAddConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				}

				/*
				 * if(!miscAppEjb.validateGCStuffIndicatorMiscApp(appNo)){ errorMessage(request,
				 * "Invalid Misc Application Number "); return; }
				 */
			}

			// HaiTTH1 added on 13/1/2014

			List<String> temp2_ciNo = new ArrayList<String>();
			List<String> temp3_nm = new ArrayList<String>();
			List<String> temp4_cont = new ArrayList<String>();
			List<Integer> temp5_pkg = new ArrayList<Integer>();
			List<String> temp6_cd = new ArrayList<String>();

			if (listValueTruckerIcNo.size() > 0) {
				truckerIcNo = listValueTruckerIcNo.get(0);
				truckerName = listValueTruckerName.get(0);
				truckerCNo = listValueTruckerCNo.get(0);
				truckerPkg = Integer.parseInt(listValueNbrpkg.get(0));

				for (int i = 0; i < listValueTruckerIcNo.size(); i++) {
					if (StringUtils.equalsIgnoreCase(truckerIcNo, listValueTruckerIcNo.get(i))) {
						listValueTruckerIcNo.set(i, null);
						listValueTruckerName.set(i, null);
						listValueTruckerCNo.set(i, null);
						listValueNbrpkg.set(i, null);
					} else {
						temp2_ciNo.add(listValueTruckerIcNo.get(i));
						temp3_nm.add(listValueTruckerName.get(i));
						temp4_cont.add(listValueTruckerCNo.get(i));
						temp5_pkg.add(Integer.parseInt(listValueNbrpkg.get(i)));
						TruckerValueObject temp = new TruckerValueObject();
						temp = eSNService.getTruckerDetails(listValueTruckerIcNo.get(i).trim());
						String trucker_cd = temp.getTruckerCd(); // eSNService.getTruckerCd(listValueTruckerIcNo.get(i));
						if (trucker_cd == null) {
							trucker_cd = "";
						}
						temp6_cd.add(trucker_cd);
					}
				}
				// map.put("listValueTruckerIcNo_cf", listValueTruckerIcNo);
			} else {
				errorMessage = ConstantUtil.ErrorMsg_Need_Trucker;
				if (errorMessage != null) {
					mapError.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(mapError);
					result.setSuccess(false);
					result.setData(map);

				} else {
					result.setData(map);
					result.setSuccess(true);
					log.info("END: EsnAddConfirm result: " + result.toString());
				}

				return ResponseEntityUtil.success(result.toString());
			}

			TruckerValueObject trkObj = new TruckerValueObject();
			trkObj = eSNService.getTruckerDetails(truckerIcNo);
			truckerCd = trkObj.getTruckerCd();

			List<EsnListValueObject> accNoList = new ArrayList<EsnListValueObject>();
			List<EsnListValueObject> UserAccNoList = new ArrayList<EsnListValueObject>();
			List<EsnListValueObject> userAccNo = new ArrayList<EsnListValueObject>();
			List<EsnListValueObject> accNo = new ArrayList<EsnListValueObject>();
			String getAccNo = "";
			accNoList = eSNService.getAccNo(truckerIcNo);
			// UserAccNoList =(Vector)eSNService.getUserAccNo(bookingRefNo,custCd);
			if (accNoList.size() != 0) {
				for (int i = 0; i < accNoList.size(); i++) {
					esnListValueObject = new EsnListValueObject();
					esnListValueObject = accNoList.get(i);
					// Modify by Ding Xijia(harbortek) 30-Jan-2011 : START
					// accNo.add(esnListValueObject.getAccNo());
					accNo.add(esnListValueObject);
					// Modify by Ding Xijia(harbortek) 30-Jan-2011 : END
				}
			}
			if (accNoList.size() != 0)
				getAccNo = esnListValueObject.getAccNo();
			else
				getAccNo = "No";
			UserAccNoList = eSNService.getUserAccNo(bookingRefNo, custCd, getAccNo);
			if (UserAccNoList.size() != 0) {
				for (int i = 0; i < UserAccNoList.size(); i++) {
					esnListValueObject = new EsnListValueObject();
					esnListValueObject = UserAccNoList.get(i);
					// Modify by Ding Xijia(harbortek) 30-Jan-2011 : START
					// userAccNo.add(esnListValueObject.getAccNo());
					userAccNo.add(esnListValueObject);
					// Modify by Ding Xijia(harbortek) 30-Jan-2011 : END
				}
			}
			if (insert != null && insert.equals("insert")) {
				accNo_I = CommonUtility.deNull(criteria.getPredicates().get("accNo"));
				payMode = CommonUtility.deNull(criteria.getPredicates().get("payMode"));
				checkAccNo = eSNService.chkAccNo(accNo_I);

				if (accNo_I.equals("CASH") || accNo_I.equals("CA")) {
				} else {
					if (!checkAccNo) {
						errorMessage = ConstantUtil.ErrorMsg_Invalid_Billable_AccNo;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAddConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				}
				// To check the duplicate BR number submission

				List<EsnListValueObject> bookingRefList = new ArrayList<EsnListValueObject>();
				bookingRefList = eSNService.getBkRefNo(bookingRefNo, custCd);
				if (bookingRefList != null && bookingRefList.size() != 0) {

					String esnDeclared = eSNService.getEsnDeclared(bookingRefNo);
					String bkStatus = eSNService.getBkStatus(bookingRefNo);
					String esnDeclarentCode = eSNService.getDeclarentCd(bookingRefNo);
					String vvStatus = eSNService.getVvStatus(bookingRefNo);
					/**
					 * Revision History ---------------- Author Request Number Description of Change
					 * Version Date Released SRINI added getTerminal() 1.2 09 sept 2003 changed
					 * getVarno()
					 */

					/**
					 * Added by srini on 9 sept 2003 to get the Terminal from vessel_call table.
					 * This method is added to prompt the user by checking the vessel status along
					 * with the terminal .
					 */
					String terminal = eSNService.getTerminal(bookingRefNo);
					String closedVslInd = eSNService.getClsVslInd(bookingRefNo);
//					String closedBjInd = eSNService.getClsBjInd(bookingRefNo); // not used
					if (esnDeclared.equals("Y")) {
						errorMessage = ConstantUtil.ErrorMsg_BkRef_Used;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAddConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
					if (bkStatus.equals("X")) {
						errorMessage = String.valueOf(String
								.valueOf((new StringBuffer("The Booking Reference with BR. No. ")).append(bookingRefNo)
										.append("has been canceled.  Please use a different BR. No.")));
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAddConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
					if (!custCd.equals("JP")) {
						if (vvStatus.equals("UB")) {
							errorMessage = ConstantUtil.ErrorMsg_ESN_Unberth;
							if (errorMessage != null) {
								mapError.put("errorMessage", errorMessage);
								result = new Result();
								result.setErrors(mapError);
								result.setSuccess(false);
								result.setData(map);

							} else {
								result.setData(map);
								result.setSuccess(true);
								log.info("END: EsnAddConfirm result: " + result.toString());
							}

							return ResponseEntityUtil.success(result.toString());
						}
						if (!esnDeclarentCode.equals(custCd)) {
							errorMessage = ConstantUtil.ErrorMsg_Not_Authorized_BrNbr;
							if (errorMessage != null) {
								mapError.put("errorMessage", errorMessage);
								result = new Result();
								result.setErrors(mapError);
								result.setSuccess(false);
								result.setData(map);

							} else {
								result.setData(map);
								result.setSuccess(true);
								log.info("END: EsnAddConfirm result: " + result.toString());
							}

							return ResponseEntityUtil.success(result.toString());
						}
					}
					if (vvStatus.equals("CX")) {
						errorMessage = ConstantUtil.ErrorMsg_BerthApp_Cancel;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAddConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
					/*
					 * Added by srini on 9 sept 2003 to check the terminal from vessel_call table.
					 */
					if (vvStatus.equals("CL") && terminal.equals("GB")) {
						errorMessage = ConstantUtil.ErrorMsg_BR_Vessel_Record_Closed;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAddConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
					if (closedVslInd.equals("Y")) {
						errorMessage = ConstantUtil.ErrorMsg_Vessel_Record_Closed;
						if (errorMessage != null) {
							mapError.put("errorMessage", errorMessage);
							result = new Result();
							result.setErrors(mapError);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: EsnAddConfirm result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				} else {
					errorMessage = ConstantUtil.ErrorMsg_BR_Cannot_Used;
					if (errorMessage != null) {
						mapError.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(mapError);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: EsnAddConfirm result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				// To check the duplicate BR number submission 24/10/2015

				esnNo =  eSNService.insertEsnDetailsForDPE(varNbr, truckerIcNo, truckerCNo, custCd,
						bookingRefNo, marking, portD, loadOperInd, loadFrom, dgInd, hsCode, dutyGoodInd, truckerName,
						noOfStorageDay, storageInd, pkgsType, noOfPkgs, weight, volume, accNo_I, payMode, cargoDesc,
						truckerCd, cntr1, cntr2, cntr3, cntr4, UserID, strStfInd, category, hsSubCodeFr, hsSubCodeTo,
						truckerPkg, deliveryToEPC, cntrSeqNbr, appNo, customHsCode, multiHsCodeList);// added strStfInd,vani,category, MCC for EPC_IND
				pkgsDesc = eSNService.getPkgsDesc(esnNo);
				uaNoPkgs =  eSNService.getUaNoPkgs(esnNo);
				clsShpInd = eSNService.getClsShipInd(varNbr);
				billName = eSNService.getBillablePartyName(accNo_I);
				int trkListSz = temp2_ciNo.size();
				if (trkListSz > 0) {
					for (int i = 0; i < trkListSz; i++) {
						eSNService.insertTruckerInfor(temp2_ciNo.get(i).toString(), temp3_nm.get(i).toString(),
								temp4_cont.get(i).toString(), Integer.parseInt(temp5_pkg.get(i).toString()),
								temp6_cd.get(i).toString(), esnNo, "A", 0, UserID);
					}
				}
			}
			// added laks 8 jul
			schval = eSNService.getScheme(varNbr);
			schind = eSNService.getSchemeInd(varNbr);
			map.put("schval", schval);
			map.put("schind", schind);
			// added laks 8 jul
			// added by Zhenguo Deng(harbor) on 15/02/2011 for cargo category
			String categoryValue = eSNService.getCategoryValue(category);
			map.put("categoryValue", categoryValue);
			// end add
			// --Thiru added oct 10
			String chkClsShpInd = eSNService.getClsShipInd_bkr(bookingRefNo);
			map.put("billName", billName);
			map.put("esnNo", esnNo);
			map.put("custCd", custCd);
			map.put("accNo", accNo);
			map.put("pkgsDesc", pkgsDesc);
			map.put("truckerName", truckerName);
			map.put("userAccNo", userAccNo);
			map.put("uaNoPkgs", "" + uaNoPkgs);
			map.put("clsShpInd", clsShpInd);
			map.put("weight", "" + weight);
			map.put("volume", "" + volume);
			map.put("stuffInd", strStfInd);

			map.put("cntrNbr", cntrNbr);
			map.put("appNo", appNo);
			map.put("varNbr", varNbr);
			map.put("cntrSeqNbr", cntrSeqNbr);

			String scheme = eSNService.getVesselScheme(varNbr);
			map.put("scheme", scheme);
			// Add by Ding Xijia(harbortek) 09-Feb-2011 : END
			map.put("chkClsShpInd", chkClsShpInd);
			// add by Zhenguo Deng on 14/02/2011 for Cargo Category
			map.put("category", category);
			// end add

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			List<String> hsCodeList = new ArrayList<String>();

			List<ManifestValueObject> listManifestValueObject = eSNService.getHSCodeList("1");
			if (listManifestValueObject != null && listManifestValueObject.size() > 0) {
				String hsCodeTmp = "";
				for (int i = 0; i < listManifestValueObject.size(); i++) {
					ManifestValueObject manifestValueObject = new ManifestValueObject();
					manifestValueObject = (ManifestValueObject) listManifestValueObject.get(i);
					hsCodeTmp = manifestValueObject.getHsCode();
					hsCodeList.add(hsCode);
					map.put("hsCodeTmp", hsCodeTmp);
				}
			}
			// START CR FTZ HSCODE - NS JULY 2024
			List<HsCodeDetails> hscodeDetailsList = eSNService.getHsCodeDetailList(esnNo);
			map.put("hscodeDetailsList", hscodeDetailsList);
			// END CR FTZ HSCODE - NS JULY 2024

			map.put("hsCodeList", hsCodeList);
			map.put("isDisableJPBillingNo", isDisableJPBillingNo);
			map.put("Data", topsModel);
			map.put("ListData", topsModel);

		} catch (BusinessException be) {
			log.info("Exception EsnAddConfirm: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception EsnAddConfirm : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(mapError);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);

			}
			log.info("END: EsnAddConfirm result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}
	
	//FTZ - amend only Custom details - NS Sept 2024
	@PostMapping(value = "/AmendCustomDetail")
	public ResponseEntity<?> AmendCustomDetail(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: AmendCustomDetail criteria:" + criteria.toString());
			String esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			
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
					hsCodeDetails.setIsHsCodeChange(CommonUtility.deNull(criteria.getPredicates().get("isHsCodeMain" + X)));
					hsCodeDetails.setHscodeSeqNbr(CommonUtility.deNull(criteria.getPredicates().get("hscodeSeqNbr" + X)));
					multiHsCodeList.add(hsCodeDetails);
				}
			}
			boolean isUpdated = eSNService.updateCustomDetail(multiHsCodeList, esnNo, userId);
			log.info(isUpdated);
			map.put("message", ConstantUtil.Msg_Custom_Success);
			
			
		} catch (BusinessException be) {
			log.info("Exception AmendCustomDetail: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception AmendCustomDetail : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("message", errorMessage);
				result = new Result();
				result.setErrors(errorMessage);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: AmendCustomDetail result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
	
}
