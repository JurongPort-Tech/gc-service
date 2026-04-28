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
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnEsnListValueObject;
import sg.com.jp.generalcargo.domain.TesnJpJpValueObject;
import sg.com.jp.generalcargo.domain.TesnVesselVoyValueObject;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.service.TesnJpJpService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = TesnJpJpController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TesnJpJpController {

	public static final String ENDPOINT = "gc/renominateCargo/tesn";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(TesnJpJpController.class);

	@Autowired
	private TesnJpJpService tesnJpJpService;

	// delegate.helper.gbms.cargo.tesn.tesnjpjp --> TesnJpJpHandler
	@PostMapping(value = "/TesnJpJp")
	public ResponseEntity<?> TesnJpJp(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;

		TableResult tableResult = new TableResult();
		TopsModel topsModel = new TopsModel();

		try {
			log.info("START: TesnJpJp criteria:" + criteria.toString());

			// String UserID =
			// CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String strVoyno = "";

			/*
			 * >> Add by FPT.Thai - Oct 02 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			map.put("addObject", "FALSE");
			boolean isFetchmode = false;
			String fetchVesselName = null;
			String fetchVoyageNbr = null;
			if (criteria.getPredicates().get("fetchmode") != null) {
				String fetchmode = CommonUtility.deNull(criteria.getPredicates().get("fetchmode"));
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

					// Added vietnd02
					map.put("fetchVesselName", fetchVesselName); // Added by thanhnv2
					map.put("fetchVoyageNbr", fetchVoyageNbr); // Added by thanhnv2
					isFetch = "TRUE";
					map.put("isFetchmode", isFetch);
					// vietnd02::end
				} else {
					log.info("Invalid vessel voyage values.");
					errorMessage = ConstantUtil.ErrorMsg_Invalid_Voy;
				}
			}
			/*
			 * << Add by FPT.Thai - Oct 02 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */

			// boolean retrieveInd = false;
			try {
				if (criteria.getPredicates().get("vesselvoyageno") != null) {
					strVoyno = CommonUtility.deNull(criteria.getPredicates().get("vesselvoyageno"));
				} else {
					strVoyno = CommonUtility.deNull(criteria.getPredicates().get("vessel_number"));
				}

//				String vslnew = CommonUtility.deNull(criteria.getPredicates().get("vslnew")); // not used

			} catch (Exception e) {
				log.info("Exception TesnJpJp : ", e);
				throw new BusinessException("M4201");

			}

			List<TesnJpJpValueObject> tesnjpjplist = new ArrayList<TesnJpJpValueObject>();
			List<TesnJpJpValueObject> tesnjpjplist1 = new ArrayList<TesnJpJpValueObject>();

			List<TesnVesselVoyValueObject> vslList = tesnJpJpService.getVslList(coCd);
			/*
			 * if (vslList == null || vslList.size() == 0) {
			 * errorMessage(request,"No Vessel's Selected"); return; }
			 */

			topsModel = new TopsModel();

			for (int i = 0; i < vslList.size(); i++) {
				TesnVesselVoyValueObject vslObj = new TesnVesselVoyValueObject();
				vslObj = (TesnVesselVoyValueObject) vslList.get(i);
				topsModel.put(vslObj);
			}
			/*
			 * >> Add by FPT.Thai - Oct 02 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			if (isFetchmode) {
				TesnVesselVoyValueObject vslObj1 = new TesnVesselVoyValueObject();
				vslObj1 = tesnJpJpService.getVessel(fetchVesselName, fetchVoyageNbr, coCd);
				if (null != vslObj1) {
					// topsModel.put(vslObj1); //--Add more Vessel into topsModel to FW request
					// map.put("addObject", "TRUE");
					strVoyno = vslObj1.getVoyNo(); // -- Reset the strVoyno
				} else {
					log.info("Invalid vessel voyage values.");
					errorMessage = ConstantUtil.ErrorMsg_Invalid_Voy;
				}
			}
			/*
			 * << Add by FPT.Thai - Oct 02 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			map.put("strVoyno", strVoyno);

			int total = 0;

			log.info("strVoyn" + strVoyno);
			log.info("strVoyn req" + CommonUtility.deNull(criteria.getPredicates().get("vesselvoyageno")));
			if (strVoyno != null && !strVoyno.equals("")) {
				log.info("strVoyn" + strVoyno);
				tableResult = tesnJpJpService.getTesnJpJpList(strVoyno, criteria);
				tesnjpjplist = tableResult.getData().getListData().getTopsModel();
				total = tableResult.getData().getTotal();

				for (int i = 0; i < tesnjpjplist.size(); i++) {
					tesnjpjplist1.add(tesnjpjplist.get(i));

				}
			}

			map.put("tesnjpjplist", tesnjpjplist1);
			map.put("ListData", topsModel);
			map.put("total", total);

		} catch (BusinessException be) {
			log.info("Exception TesnJpJp: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnJpJp : ", e);
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
				log.info("END: TesnJpJp result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.tesn.tesnjpjp --> TesnJpJpViewHandler
	@PostMapping(value = "/TesnJpJpView")
	public ResponseEntity<?> TesnJpJpView(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: TesnJpJpView criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String s = UserID;
			String s1 = coCd;
			String s2 = CommonUtility.deNull(criteria.getPredicates().get("assignCargo"));
			String s3 = "";
			String s4 = "";
			String s5 = "";
			List<String> vector = new ArrayList<String>();
			List<TesnEsnListValueObject> vector1 = new ArrayList<TesnEsnListValueObject>();
			String s6 = "";
			String s7 = "";

			String edo_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("edo_asn_nbr"));
			try {
				s7 = CommonUtility.deNull(criteria.getPredicates().get("vesselvoyageno"));
				s6 = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr"));
			} catch (Exception e) {
				log.info("Exception TesnJpJpView : ", e);
				throw new BusinessException("M4201");
			}
			TopsModel topsmodel = new TopsModel();
			TesnJpJpValueObject tesnjpjpvalueobject = new TesnJpJpValueObject();
			String s8 = "0";
			int i = tesnJpJpService.tesnjpjpValidCheck(s6, s7);
			int j = tesnJpJpService.tesnjpjpValidCheck_status(s6, s7);
			int k = tesnJpJpService.tesnjpjpValidCheck_DN(s6, s7);
			if (i == 1 && j == 1 && k == 1)
				s8 = "1";
			else
				s8 = "0";
			map.put("delete_status", s8);
			tesnjpjpvalueobject = tesnJpJpService.tesnjpjpView(s7, s6, edo_asn_nbr);
			String s9 = tesnjpjpvalueobject.getBk_ref_nbr();
			map.put("tesnjpjpdisp", "Display");
			map.put("tesnjpjpobj", tesnjpjpvalueobject);
			map.put("tesn_nbr", s6);
			map.put("vesselvoyageno", s7);
			map.put("tesn_nbr", s6);
			topsmodel.put(tesnjpjpvalueobject);
			map.put("ListData", topsModel);
//			String s10 = CommonUtility.deNull(criteria.getPredicates().get("voyNo")); // not used
			vector = tesnJpJpService.getSAacctno(s7);

			// START: FPT-NhonBH GCR6 24-Feb-2010
			// Allow SA user to update Billable Party
			// vector1 = tesnJpJpService.getABacctno(s7);
			if ("JP".equals(coCd)) {
				vector1 = tesnJpJpService.getABacctno(s7);
			} else {
				vector1 = tesnJpJpService.getABacctnoForSA(s7);
			}
			// END: FPT-NhonBH GCR6 24-Feb-2010
			// Allow SA user to update Billable Party
			map.put("vsactno", vector);
			map.put("vabactno", vector1);
			s5 = tesnJpJpService.getBPacctnbr(s6, s7);
			log.info("sacctnbr" + s5);
			map.put("sacctnbr", s5);
			s3 = tesnJpJpService.getScheme(s7);
			s4 = tesnJpJpService.getSchemeInd(s7);
			map.put("schval", s3);
			map.put("schind", s4);
			String s11 = tesnJpJpService.getClsShipInd(s7);
			String s12 = tesnJpJpService.getClsShipInd_bkr(s9);
			map.put("custCd", s1);
			map.put("chkClsShpInd", s12);
			map.put("clsShipInd", s11);
			// added by Deng Zhengguo
			map.put("cotegoryView", tesnjpjpvalueobject.getCategoryView());
			// end add
			if (s2 != null && !s2.equals("") && s2.equals("Assign_Bill")) {
				String s13 = CommonUtility.deNull(criteria.getPredicates().get("billparty"));
				String s15 = "";
				String s17 = "";
				s15 = tesnJpJpService.getSchemeName(s7);
				if (s15.equals("JLR"))
					s17 = tesnJpJpService.getVCactnbr(s7);
				else
				// add new scheme for LCT, 20.feb.11 by hpeng
				if (!s15.equals("JLR") && !s15.equals("JNL") && !s15.equals("JBT")
						&& !s15.equals(ProcessChargeConst.LCT_SCHEME))
					s17 = tesnJpJpService.getABactnbr(s7);
				tesnJpJpService.EsnAssignBillUpdate(s13, s6, s);
				if (s17 != s13)
					tesnJpJpService.EsnAssignVslUpdate(s7, "Y", s);
				// nextScreen(httpservletrequest, "TesnjpjpView");
			} else if (s2 != null && !s2.equals("") && s2.equals("Assign")) {
				// nextScreen(httpservletrequest, "TesnjpjpAssignBill");
			} else if (s2 != null && !s2.equals("") && s2.equals("ASSIGNDB")) {
				String s14 = CommonUtility.deNull(criteria.getPredicates().get("cargocategory"));
				tesnJpJpService.AssignCrgvalUpdate(s14, s6, s);
				// nextScreen(httpservletrequest, "TesnjpjpView");
			} else if (s2 != null && !s2.equals("") && s2.equals("CRG")) {
				List<TesnEsnListValueObject> vector2 = new ArrayList<TesnEsnListValueObject>();
				String s16 = tesnJpJpService.AssignCrgvalCheck(s6);
				vector2 = tesnJpJpService.getAssignCargo();
				map.put("tesnCargo", vector2);
				map.put("tesnCrgChk", s16);
				// nextScreen(httpservletrequest, "TesnjpjpAssignCrg");
			} else {
				// nextScreen(httpservletrequest, "TesnjpjpView");
			}

		} catch (BusinessException be) {
			log.info("Exception TesnJpJpView: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnJpJpView : ", e);
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
				log.info("END: TesnJpJpView result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.tesn.tesnjpjp --> TesnJpJpAmendHandler
	@PostMapping(value = "/TesnJpJpAmend")
	public ResponseEntity<?> TesnJpJpAmend(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: TesnJpJpAmend criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String s = UserID;
			String s1 = coCd;

			List<Map<String, Object>> list = tesnJpJpService.getCategoryList();
			map.put("categoryList", list);
			String category = "00";
			if (!StringUtils.isEmpty(criteria.getPredicates().get("category"))) {
				category = CommonUtility.deNull(criteria.getPredicates().get("category"));
			}
			;
			map.put("category", category);
			String categoryView = tesnJpJpService.getCategoryValue(category);
			map.put("categoryView", categoryView);

			if (criteria.getPredicates().get("funct") != null
					&& criteria.getPredicates().get("funct").equals("Amend")) {
				String s2 = "";
				String s5 = "";
				String s7 = "";
				String s9 = "";
				String s11 = "";
				String s12 = "";
				String s13 = "";
				String s14 = "";
				String s15 = "";
				String s16 = "";
				String s17 = "";
				String s21 = "";
				String stuffind = new String();
				try {
					s5 = CommonUtility.deNull(criteria.getPredicates().get("vesselvoyageno"));
					s2 = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr"));
					s7 = CommonUtility.deNull(criteria.getPredicates().get("loadingInd"));
					s9 = CommonUtility.deNull(criteria.getPredicates().get("nbr_pkgs"));
					s11 = CommonUtility.deNull(criteria.getPredicates().get("pay_mode"));
					s12 = CommonUtility.deNull(criteria.getPredicates().get("acc_num"));
					s13 = CommonUtility.deNull(criteria.getPredicates().get("edo_asn_nbr"));
					s14 = CommonUtility.deNull(criteria.getPredicates().get("trans_pkgs"));
					s15 = CommonUtility.deNull(criteria.getPredicates().get("acc_check"));
					s16 = CommonUtility.deNull(criteria.getPredicates().get("edo_nbr_pkgs"));
					s17 = CommonUtility.deNull(criteria.getPredicates().get("prev_nbr_pkgs"));
					s21 = CommonUtility.deNull(criteria.getPredicates().get("bk_ref_nbr"));
					stuffind = CommonUtility.deNull(criteria.getPredicates().get("stuffind"));
				} catch (Exception e) {
					log.info("Exception TesnJpJpAmend : ", e);
					throw new BusinessException("M4201");
				}
				// Add by VietNguyen 08/01/2014: START
				String nomWt = CommonUtility.deNull(criteria.getPredicates().get("nom_wt"));
				String nomVol = CommonUtility.deNull(criteria.getPredicates().get("nom_vol"));
				// Add by VietNguyen 08/01/2014: END

				TopsModel topsmodel = new TopsModel();
				log.info("ent" + s15);
				if (s15.length() != 0 && s15.equals("Y")) {
					log.info("enters" + s15);
					tesnJpJpService.tesnVerify_accno(s12);
				}
				tesnJpJpService.tesnjpjpAmendForDPE(s5, s2, s7, s9, s11, s12, s14, s13, s, s16, s17, stuffind, category,
						nomWt, nomVol);
				TesnJpJpValueObject tesnjpjpvalueobject = new TesnJpJpValueObject();
				tesnjpjpvalueobject = tesnJpJpService.tesnjpjpView(s5, s2, s13);
				map.put("tesnjpjpobj", tesnjpjpvalueobject);
				map.put("tesn_nbr", s2);
				map.put("vesselvoyageno", s5);
				map.put("tesn_nbr", s2);
				String s18 = "0";
				int i = tesnJpJpService.tesnjpjpValidCheck(s2, s5);
				int j = tesnJpJpService.tesnjpjpValidCheck_status(s2, s5);
				int k = tesnJpJpService.tesnjpjpValidCheck_DN(s2, s5);
				if (i == 1 && j == 1 && k == 1)
					s18 = "1";
				else
					s18 = "0";
				map.put("delete_status", s18);

				String clsShipInd = tesnJpJpService.getClsShipInd(s5);
				String chkClsShpInd = tesnJpJpService.getClsShipInd_bkr(s21);

				map.put("custCd", s1);
				map.put("chkClsShpInd", chkClsShpInd);
				map.put("clsShipInd", clsShipInd);

				topsmodel.put(tesnjpjpvalueobject);
				map.put("ListData", topsModel);
				// nextScreen(httpservletrequest, "TesnjpjpView");
			}
			if (criteria.getPredicates().get("funct") != null
					&& criteria.getPredicates().get("funct").equals("Display")) {
				String s3 = CommonUtility.deNull(criteria.getPredicates().get("vesselvoyageno"));
				String s6 = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr"));
				String s13 = CommonUtility.deNull(criteria.getPredicates().get("edo_asn_nbr"));
				if (!s1.equals("JP"))
					tesnJpJpService.tesnVerify_amend(s6, s3);
				tesnJpJpService.tesnVerify_amend_status(s6, s3);
				String brno = CommonUtility.deNull(criteria.getPredicates().get("bk_ref_nbr"));
				String vslType = tesnJpJpService.getVesselType(brno);
				map.put("vslType", vslType);

				if (!"JP".equalsIgnoreCase(s1)) {
					boolean chkAtuDttm = tesnJpJpService.chkDttmOfSecondCarrierVsl(brno);
					if (!chkAtuDttm) {
						errorMessage = ConstantUtil.ErrorMsg_Unable_Amend_After_ATU;
						if (errorMessage != null) {
							map.put("errorMessage", errorMessage);				
							result = new Result();
							result.setErrors(map);
							result.setError(errorMessage);
							result.setSuccess(false);
							result.setData(map);

						} else {
							result.setData(map);
							result.setSuccess(true);
							log.info("END: TesnJpJpAmend result: " + result.toString());
						}

						return ResponseEntityUtil.success(result.toString());
					}
				}

				// Add by Ding Xijia(harbortek) 10-Mar-2011 : START
				TesnJpJpValueObject tesnjpjpvalueobject = tesnJpJpService.tesnjpjpView(s3, s6, s13);
				String bk_ref_nbr = tesnjpjpvalueobject.getBk_ref_nbr();
				String edo_asn_nbr = tesnjpjpvalueobject.getEdo_asn_nbr();
				String acctNo = tesnJpJpService.getEdoAcct(edo_asn_nbr, bk_ref_nbr, s1);
				if (StringUtils.isNotBlank(acctNo)) {
					map.put("edoAcctNo", acctNo);
				}
				map.put("tesnjpjpvalueobject", tesnjpjpvalueobject);
				map.put("custCd", s1);
				map.put("caCustCd", tesnjpjpvalueobject.getCaCustCd());
				// Add by Ding Xijia(harbortek) 10-Mar-2011 : END
				// nextScreen(httpservletrequest, "TesnJpJpAmendScreen");
			}
			if (criteria.getPredicates().get("funct") != null
					&& criteria.getPredicates().get("funct").equals("GetAccNo")) {
				String s4 = "";
				s4 = CommonUtility.deNull(criteria.getPredicates().get("vesselvoyageno"));
				List<String> vector = new ArrayList<String>();
				String s8 = CommonUtility.deNull(criteria.getPredicates().get("bk_ref_nbr"));
				// Modified by Ding Xijia(harbortek) 10-Mar-2011 : START
				String s12 = CommonUtility.deNull(criteria.getPredicates().get("acc_num"));
				if (StringUtils.isNotBlank(s12) && !"null".equals(s12)) {
					String s2 = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr"));
					String s7 = CommonUtility.deNull(criteria.getPredicates().get("loadingInd"));
					String s9 = CommonUtility.deNull(criteria.getPredicates().get("nbr_pkgs"));
					String s11 = CommonUtility.deNull(criteria.getPredicates().get("pay_mode"));

					String s13 = CommonUtility.deNull(criteria.getPredicates().get("edo_asn_nbr"));
					String s14 = CommonUtility.deNull(criteria.getPredicates().get("trans_pkgs"));
					String s16 = CommonUtility.deNull(criteria.getPredicates().get("edo_nbr_pkgs"));
					String s17 = CommonUtility.deNull(criteria.getPredicates().get("prev_nbr_pkgs"));
					String stuffind = CommonUtility.deNull(criteria.getPredicates().get("stuffind"));
					// Add by VietNguyen 08/01/2014: START
					String nomWt = CommonUtility.deNull(criteria.getPredicates().get("nom_wt"));
					String nomVol = CommonUtility.deNull(criteria.getPredicates().get("nom_vol"));
					// Add by VietNguyen 08/01/2014: END

					TopsModel topsmodel = new TopsModel();
					tesnJpJpService.tesnjpjpAmendForDPE(s4, s2, s7, s9, s11, s12, s14, s13, s, s16, s17, stuffind,
							category, nomWt, nomVol);
					TesnJpJpValueObject tesnjpjpvalueobject = new TesnJpJpValueObject();
					tesnjpjpvalueobject = tesnJpJpService.tesnjpjpView(s4, s2, s13);
					map.put("tesnjpjpobj", tesnjpjpvalueobject);
					map.put("tesn_nbr", s2);
					map.put("vesselvoyageno", s4);
					map.put("tesn_nbr", s2);
					String s18 = "0";
					int i = tesnJpJpService.tesnjpjpValidCheck(s2, s4);
					int j = tesnJpJpService.tesnjpjpValidCheck_status(s2, s4);
					int k = tesnJpJpService.tesnjpjpValidCheck_DN(s2, s4);
					if (i == 1 && j == 1 && k == 1)
						s18 = "1";
					else
						s18 = "0";
					map.put("delete_status", s18);

					String clsShipInd = tesnJpJpService.getClsShipInd(s4);
					String chkClsShpInd = tesnJpJpService.getClsShipInd_bkr(s8);

					map.put("custCd", s1);
					map.put("chkClsShpInd", chkClsShpInd);
					map.put("clsShipInd", clsShipInd);

					topsmodel.put(tesnjpjpvalueobject);
					map.put("ListData", topsModel);
					// nextScreen(httpservletrequest, "TesnjpjpView");
				} else {
					vector = tesnJpJpService.tesnjpjpGetAccNo(s1, s4, s8);
					map.put("GetAccNo", vector);
					// nextScreen(httpservletrequest, "TesnJpJpAmendContScreen");
				}
				// Modified by Ding Xijia(harbortek) 10-Mar-2011 : END
				map.put("custCd", s1);
			}

		} catch (BusinessException be) {
			log.info("Exception TesnJpJpAmend: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnJpJpAmend : ", e);
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
				log.info("END: TesnJpJpAmend result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.tesn.tesnjpjp --> TesnJpJpDeleteHandler
	@PostMapping(value = "/TesnJpJpDelete")
	public ResponseEntity<?> TesnJpJpDelete(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: TesnJpJpDelete criteria:" + criteria.toString());

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			// String coCd =
			// CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String tesn_nbr = "";
			String vesselvoyageno = "";
			String bk_ref_nbr = "";
			String nbr_pkgs = "";
			String edo_asn_nbr = "";
			try {
				vesselvoyageno = CommonUtility.deNull(criteria.getPredicates().get("vesselvoyageno"));
				tesn_nbr = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr"));
				nbr_pkgs = CommonUtility.deNull(criteria.getPredicates().get("nbr_pkgs"));
				edo_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("edo_asn_nbr"));
				bk_ref_nbr = CommonUtility.deNull(criteria.getPredicates().get("bk_ref_nbr"));

			} catch (Exception e) {
				log.info("Exception TesnJpJpDelete : ", e);
				throw new BusinessException("M4201");
			}

			tesnJpJpService.tesnVerify_delete(tesn_nbr, vesselvoyageno);
			tesnJpJpService.tesnVerify_delete_status(tesn_nbr, vesselvoyageno);
			tesnJpJpService.tesnVerify_delete_DN(tesn_nbr, vesselvoyageno);
			tesnJpJpService.tesnjpjpDelete(vesselvoyageno, tesn_nbr, nbr_pkgs, edo_asn_nbr, bk_ref_nbr, UserID);
			map.put("vesselvoyageno", vesselvoyageno);
			map.put("tesn_nbr", tesn_nbr);

		} catch (BusinessException be) {
			log.info("Exception TesnJpJpDelete: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnJpJpDelete : ", e);
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
				log.info("END: TesnJpJpDelete result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.tesn.tesnjpjp --> TesnJpJpAddHandler
	@PostMapping(value = "/TesnJpJpAdd")
	public ResponseEntity<?> TesnJpJpAdd(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: TesnJpJpAdd criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String s = UserID;
			String s1 = coCd;
			log.info("TesnJpJpAddHandler :s =" + s);
			log.info("TesnJpJpAddHandler :s1 =" + s1);

			List<Map<String, Object>> list = tesnJpJpService.getCategoryList();
			// log.info("TesnJpJpAddHandler :esn =" + esn);
			map.put("categoryList", list);

			log.info("TesnJpJpAddHandler :criteria.getPredicates().get(add_but) ="
					+ CommonUtility.deNull(criteria.getPredicates().get("add_but")));

			if (criteria.getPredicates().get("add_but") != null
					&& criteria.getPredicates().get("add_but").equals("AddBut")) {
				log.info("Enters Add Button ");
				String s2 = "";
				String s4 = "";
				String s7 = "";
				String s9 = "";
				String s10 = "";
				String s11 = "";
				String s12 = "";
				String s13 = "";
				String s14 = "";
				String s15 = "";
				String s16 = "";
				String category = "00";
				String stuffind = new String();
				String nomWt = "";
				String nomVol = "";
				try {
					s2 = CommonUtility.deNull(criteria.getPredicates().get("txt_edo_asn_nbr"));
					s4 = CommonUtility.deNull(criteria.getPredicates().get("txt_bk_ref_nbr"));
					s7 = CommonUtility.deNull(criteria.getPredicates().get("loadingInd"));
					s9 = CommonUtility.deNull(criteria.getPredicates().get("nbr_pkgs"));
					s12 = CommonUtility.deNull(criteria.getPredicates().get("pay_mode"));
					s13 = CommonUtility.deNull(criteria.getPredicates().get("acc_num"));
					s15 = CommonUtility.deNull(criteria.getPredicates().get("val_pkgs"));
					s11 = CommonUtility.deNull(criteria.getPredicates().get("out_voy_var_nbr"));
					s10 = CommonUtility.deNull(criteria.getPredicates().get("in_voy_var_nbr"));
					s14 = CommonUtility.deNull(criteria.getPredicates().get("edo_nbr_pkgs"));
					s16 = CommonUtility.deNull(criteria.getPredicates().get("acc_check"));

					// Amended by hujun 16/05/2011
					category = CommonUtility.deNull(criteria.getPredicates().get("category"));
					if (StringUtils.isEmpty(category)) {
						category = "00";
					}
					;
					// end
					stuffind = CommonUtility.deNull(criteria.getPredicates().get("stuffind"));

					// Add by VietNguyen 08/01/2014
					nomWt = CommonUtility.deNull(criteria.getPredicates().get("nom_wt"));
					nomVol = CommonUtility.deNull(criteria.getPredicates().get("nom_vol"));
					System.err.println("=======acc====" + nomWt);

				} catch (Exception e) {
					log.info("Exception TesnJpJpAdd : ", e);
					throw new BusinessException("M4201");
				}
				TopsModel topsmodel = new TopsModel();
				log.info("TesnJpJpAddHandler :s16 =" + s16);
				if (s16.length() != 0 && s16.equals("Y"))
					tesnJpJpService.tesnVerify_accno(s13);
				log.info("TesnJpJpAddHandler :s13 =" + s13);
				
				// START check balance package in EDO - NS March 2023
				tesnJpJpService.checkEdoPackage(s2);
				// END NS March 2023
				
				
				String s17 = tesnJpJpService.tesnjpjpAddForDPE(s2, s4, s7, s9, s12, s13, s15, s11, s10, s1, s, s14,
						stuffind, category, nomWt, nomVol);
				log.info("TesnJpJpAddHandler :s17 =" + s17);
				TesnJpJpValueObject tesnjpjpvalueobject1 = new TesnJpJpValueObject();
				tesnjpjpvalueobject1 = tesnJpJpService.tesnjpjpView(s11, s17, s2);
				log.info("TesnJpJpAddHandler :tesnjpjpvalueobject1 =" + tesnjpjpvalueobject1);
				log.info("TesnJpJpAddHandler :s11 =" + s11);
				map.put("tesnjpjpobj", tesnjpjpvalueobject1);
				map.put("tesn_nbr", s17);
				map.put("vesselvoyageno", s11);
				map.put("tesn_nbr", s17);
				String s18 = "0";
				int i = tesnJpJpService.tesnjpjpValidCheck(s17, s11);
				int j = tesnJpJpService.tesnjpjpValidCheck_status(s17, s11);
				int k = tesnJpJpService.tesnjpjpValidCheck_DN(s17, s11);
				log.info("TesnJpJpAddHandler :i =" + i);
				log.info("TesnJpJpAddHandler :j =" + j);
				log.info("TesnJpJpAddHandler :k =" + k);
				if (i == 1 && j == 1 && k == 1)
					s18 = "1";
				else
					s18 = "0";
				map.put("delete_status", s18);
				log.info("TesnJpJpAddHandler :s11 =" + s11);
				log.info("TesnJpJpAddHandler :s4 =" + s4);

				String clsShipInd = tesnJpJpService.getClsShipInd(s11);
				String chkClsShpInd = tesnJpJpService.getClsShipInd_bkr(s4);

				map.put("custCd", s1);
				map.put("chkClsShpInd", chkClsShpInd);
				map.put("clsShipInd", clsShipInd);

				topsmodel.put(tesnjpjpvalueobject1);
				map.put("ListData", topsModel);
				// nextScreen(httpservletrequest, "TesnjpjpView");
			}

			if (criteria.getPredicates().get("add_but") != null
					&& criteria.getPredicates().get("add_but").equals("1")) {
				// nextScreen(httpservletrequest, "TesnJpJpAddScreen");
			}

			if (criteria.getPredicates().get("add_but") != null
					&& criteria.getPredicates().get("add_but").equals("2")) {
				String s3 = CommonUtility.deNull(criteria.getPredicates().get("txt_edo_asn_nbr"));
				String s5 = CommonUtility.deNull(criteria.getPredicates().get("txt_bk_ref_nbr"));
				log.info("TesnJpJpAddHandler :s3 =" + s3);
				log.info("TesnJpJpAddHandler :s5 =" + s5);
				tesnJpJpService.tesnVerify_edo_bk(s3, s5, s1);
				TesnJpJpValueObject tesnjpjpvalueobject = new TesnJpJpValueObject();
				tesnjpjpvalueobject = tesnJpJpService.tesnjpjpAddView(s3, s5);
				log.info("tesnjpjpvalueobject: " + tesnjpjpvalueobject.toString());
				String acctNo = tesnJpJpService.getEdoAcct(s3, s5, s1);
				if (StringUtils.isNotBlank(acctNo)) {
					tesnjpjpvalueobject.setEdoAcctNo(acctNo);
				}

				BookingReferenceValueObject brvo = (BookingReferenceValueObject) tesnJpJpService.fetchBKDetails(s5)
						.get(0);
				String cargoCategoryCode = brvo.getCargoCategory();
				tesnjpjpvalueobject.setCategoryView(tesnJpJpService.getCategoryValue(cargoCategoryCode));
				tesnjpjpvalueobject.setCategory(cargoCategoryCode);
				map.put("tesnjpjpobj", tesnjpjpvalueobject);
				map.put("custCd", s1);
				map.put("caCustCd", tesnjpjpvalueobject.getCaCustCd());
				String vslType = tesnJpJpService.getVesselType(s5);
				map.put("vslType", vslType);
				int temp = 0;
				float tempwt = 0;
				float tempvol = 0;
				try {
					temp = Integer.parseInt(tesnjpjpvalueobject.getEdo_nbr_pkgs())
							- Integer.parseInt(tesnjpjpvalueobject.getDn_nbr_pkgs())
							- Integer.parseInt(tesnjpjpvalueobject.getTrns_nbr_pkgs())
							+ Integer.parseInt(tesnjpjpvalueobject.getNum_pkgs());
					tempwt = (new Float(temp) / Float.parseFloat(tesnjpjpvalueobject.getEdo_nbr_pkgs()))
							* Float.parseFloat(tesnjpjpvalueobject.getEdo_nom_wt());
					tempvol = (new Float(temp) / Float.parseFloat(tesnjpjpvalueobject.getEdo_nbr_pkgs()))
							* Float.parseFloat(tesnjpjpvalueobject.getEdo_nom_vol());
				} catch (Exception e) {
					log.info("Exception " + e.getMessage());
					errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
					throw new BusinessException(errorMessage);
				}
				map.put("tempwt", tempwt);
				map.put("tempvol", tempvol);
				map.put("tesnjpjpobj", tesnjpjpvalueobject);
				map.put("custCd", s1);
				map.put("caCustCd", tesnjpjpvalueobject.getCaCustCd());
				map.put("vslType", vslType);

				log.info("TesnJpJpAddHandler :vslType =" + vslType);
				// nextScreen(httpservletrequest, "TesnJpJpAddContScreen");
			}
			if (criteria.getPredicates().get("add_but") != null
					&& criteria.getPredicates().get("add_but").equals("GetAccNo")) {
				log.info("TesnJpJpAddHandler@@@");
				List<String> vector = new ArrayList<String>();
				String s6 = CommonUtility.deNull(criteria.getPredicates().get("out_voy_var_nbr"));
				String s8 = CommonUtility.deNull(criteria.getPredicates().get("txt_bk_ref_nbr"));
				String category = CommonUtility.deNull(criteria.getPredicates().get("category"));
				// Add by VietNguyen 08/01/2014
				String nomWt = CommonUtility.deNull(criteria.getPredicates().get("nom_wt"));
				String nomVol = CommonUtility.deNull(criteria.getPredicates().get("nom_vol"));
				// add by hujun 18/05/2011
				if (StringUtils.isEmpty(category)) {
					category = "00";
				}
				map.put("category", category);
				// end

				log.info("TesnJpJpAddHandler@ :s6 =" + s6);
				log.info("TesnJpJpAddHandler@ :s8 =" + s8);
				log.info("TesnJpJpAddHandler@ :nomWt =" + nomWt);
				log.info("TesnJpJpAddHandler@ :nomVol =" + nomVol);

				String s13 = CommonUtility.deNull(criteria.getPredicates().get("acc_num"));
				log.info("TesnJpJpAddHandler@ :s13 =" + s13);
				if (StringUtils.isNotBlank(s13) && !"null".equals(s13)) {
					String s2 = CommonUtility.deNull(criteria.getPredicates().get("txt_edo_asn_nbr"));
					String s7 = CommonUtility.deNull(criteria.getPredicates().get("loadingInd"));
					String s9 = CommonUtility.deNull(criteria.getPredicates().get("nbr_pkgs"));
					String s12 = CommonUtility.deNull(criteria.getPredicates().get("pay_mode"));
					String s15 = CommonUtility.deNull(criteria.getPredicates().get("val_pkgs"));
					String s10 = CommonUtility.deNull(criteria.getPredicates().get("in_voy_var_nbr"));
					String s14 = CommonUtility.deNull(criteria.getPredicates().get("edo_nbr_pkgs"));
					String stuffind = CommonUtility.deNull(criteria.getPredicates().get("stuffind"));
					// Add by VietNguyen 08/01/2014
					nomWt = CommonUtility.deNull(criteria.getPredicates().get("nom_wt"));
					nomVol = CommonUtility.deNull(criteria.getPredicates().get("nom_vol"));
					log.info("TesnJpJpAddHandler**** :nomWt =" + nomWt);
					TopsModel topsmodel = new TopsModel();
					String s17 = tesnJpJpService.tesnjpjpAddForDPE(s2, s8, s7, s9, s12, s13, s15, s6, s10, s1, s, s14,
							stuffind, category, nomWt, nomVol);
					TesnJpJpValueObject tesnjpjpvalueobject1 = new TesnJpJpValueObject();
					tesnjpjpvalueobject1 = tesnJpJpService.tesnjpjpView(s6, s17, s2);
					map.put("tesnjpjpobj", tesnjpjpvalueobject1);
					map.put("tesn_nbr", s17);
					map.put("vesselvoyageno", s6);
					map.put("tesn_nbr", s17);
					String s18 = "0";
					int i = tesnJpJpService.tesnjpjpValidCheck(s17, s6);
					int j = tesnJpJpService.tesnjpjpValidCheck_status(s17, s6);
					int k = tesnJpJpService.tesnjpjpValidCheck_DN(s17, s6);
					log.info("TesnJpJpAddHandler***** :i =" + i);
					log.info("TesnJpJpAddHandler***** :j =" + j);
					log.info("TesnJpJpAddHandler***** :k =" + k);
					if (i == 1 && j == 1 && k == 1)
						s18 = "1";
					else
						s18 = "0";
					map.put("delete_status", s18);

					String clsShipInd = tesnJpJpService.getClsShipInd(s6);
					String chkClsShpInd = tesnJpJpService.getClsShipInd_bkr(s8);

					map.put("custCd", s1);
					map.put("chkClsShpInd", chkClsShpInd);
					map.put("clsShipInd", clsShipInd);

					topsmodel.put(tesnjpjpvalueobject1);
					map.put("ListData", topsModel);
					// nextScreen(httpservletrequest, "TesnjpjpView");
				} else {
					vector = tesnJpJpService.tesnjpjpGetAccNo(s1, s6, s8);
					map.put("GetAccNo", vector);
					// nextScreen(httpservletrequest, "TesnJpJpAddCont1Screen");
				}
			}

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
				log.info("END: TesnJpJpAdd result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// api.gbms.cargo.tesn--> TesnJpJpVslList --> processRequest
	@PostMapping(value = "/TesnJpJpVslList")
	public ResponseEntity<?> TesnJpJpVslList(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START: TesnJpJpVslList criteria:" + criteria.toString());

			// String UserID =
			// CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			coCd = coCd.equalsIgnoreCase("") ? "JP" : coCd;

			List<TesnVesselVoyValueObject> vslList = tesnJpJpService.getVslList(coCd);
			int size = 0;

			for (int i = 0; i < vslList.size(); i++) {
				TesnVesselVoyValueObject vslObj = new TesnVesselVoyValueObject();
				vslObj = (TesnVesselVoyValueObject) vslList.get(i);
				topsModel.put(vslObj);
			}
			size = topsModel.getSize();
			if (size != 0) {
				ArrayList<String> vsllist = new ArrayList<String>();
				ArrayList<String> voylist = new ArrayList<String>();
				ArrayList<String> vvvoy = new ArrayList<String>();
				ArrayList<String> terminalList = new ArrayList<String>();
				log.info(topsModel.getSize());
				for (int i = 0; i < topsModel.getSize(); i++) {
					TesnVesselVoyValueObject vvobj = new TesnVesselVoyValueObject();
					String vname = "";
					String voyno = "";
					String vv_voy = "";
					String terminal = "";
					vvobj = (TesnVesselVoyValueObject) topsModel.get(i);
					vname = vvobj.getVslName();
					voyno = vvobj.getVoyNo();
					vv_voy = vvobj.getVvVoy();
					terminal = vvobj.getTerminal();
					vsllist.add(vname);
					voylist.add(voyno);
					vvvoy.add(vv_voy);
					terminalList.add(terminal);
				}
				map.put("VslName", vsllist);
				map.put("VoyNo", voylist);
				map.put("VvVoy", vvvoy);
				map.put("terminal", terminalList);
			} else {
				log.info("TopsModel return 0 results to servlet");
			}

		} catch (BusinessException be) {
			log.info("Exception TesnJpJpVslList: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnJpJpVslList : ", e);
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
				log.info("END: TesnJpJpVslList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
}
