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

import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.ShipStoreValueObject;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.InwardCargoManifestService;
import sg.com.jp.generalcargo.service.OutwardCargoShipStoreService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = OutwardCargoShipStoreController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OutwardCargoShipStoreController {

	public static final String ENDPOINT = "gc/outwardcargo/shipstore";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(OutwardCargoShipStoreController.class);

	@Autowired
	private OutwardCargoShipStoreService shipStoreService;
	@Autowired
	private InwardCargoManifestService manifestService;
	private TopsModel topsModel = null;

	// delegate.helper.gbms.cargo.shipstore-->ShipStoreListHandler
	@PostMapping(value = "/shipStoreList")
	public ResponseEntity<?> shipStoreList(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** shipStoreList Start criteria :" + criteria.toString());
			topsModel = new TopsModel();
			String custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String selVoyno = "";
			
			/*
			 * String userID = "GSUSER"; String coCd = "GSL";
			 */
			// String = "C1";
			selVoyno = CommonUtility.deNull(criteria.getPredicates().get("vslNbr"));
			VesselVoyValueObject vesselVoyValueObject = null;
			ShipStoreValueObject shipStoreValueObject = null;
			List<VesselVoyValueObject> vesselSel = new ArrayList<VesselVoyValueObject>();
			List<ShipStoreValueObject> shpStrList = new ArrayList<ShipStoreValueObject>();
			List<String> terminalList = new ArrayList<String>();
			List<String> schemeList = new ArrayList<String>();
			List<String> subSchemeList = new ArrayList<String>();
			List<String> gcOperationsList = new ArrayList<String>();
			List<String> shpStrNbr = new ArrayList<String>();
			List<String> truckerNameList = new ArrayList<String>();
			List<String> crgDesc = new ArrayList<String>();
			List<String> noOfPkgs = new ArrayList<String>();
			List<String> weight = new ArrayList<String>();
			List<String> volume = new ArrayList<String>();
			vesselSel = shipStoreService.getVesselList(custCd);
			vesselVoyValueObject = new VesselVoyValueObject();
			for (int i = 0; i < vesselSel.size(); i++) {
				vesselVoyValueObject = (VesselVoyValueObject) vesselSel.get(i);
				topsModel.put(vesselVoyValueObject);
			}
			if (selVoyno != null && !selVoyno.equals("")) {

				
				shpStrList = shipStoreService.getshpStrList(selVoyno, custCd, criteria);

				int total = shipStoreService.getshpStrListCount(selVoyno, custCd, criteria);

				for (int i = 0; i < shpStrList.size(); i++) {
					shipStoreValueObject = new ShipStoreValueObject();
					shipStoreValueObject = (ShipStoreValueObject) shpStrList.get(i);
					terminalList.add(shipStoreValueObject.getTerminal());
					schemeList.add(shipStoreValueObject.getScheme());
					subSchemeList.add(shipStoreValueObject.getSubScheme());
					gcOperationsList.add(shipStoreValueObject.getGcOperations());
					shpStrNbr.add("" + shipStoreValueObject.getShpStrNbr());
					truckerNameList.add(shipStoreValueObject.getTruckerName());
					crgDesc.add((String) shipStoreValueObject.getCrgDesc());
					noOfPkgs.add("" + shipStoreValueObject.getNoofPkgs());
					weight.add("" + shipStoreValueObject.getGrWt());
					volume.add("" + shipStoreValueObject.getGrVolume());
				}

				map.put("total", total);
			} // if

			int maxCargoTon = 0;

			try {
				maxCargoTon = Integer.parseInt(shipStoreService.getValue("SSMXT"));
			} catch (Exception e) {
				log.info("Exception shipStoreList : ", e);
			}

			map.put("maxCargoTon", Integer.valueOf(maxCargoTon));
			map.put("terminalList", terminalList);
			map.put("schemeList", schemeList);
			map.put("subSchemeList", subSchemeList);
			map.put("gcOperationsList", gcOperationsList);
			map.put("truckerNameList", truckerNameList);
			map.put("selVoyno", selVoyno);
			map.put("noOfPkgs", noOfPkgs);
			map.put("shpStrNo", shpStrNbr);
			map.put("weight", weight);
			map.put("volume", volume);
			map.put("crgDesc", crgDesc);
			map.put("custCd", custCd);
			map.put("model", topsModel);
			log.info("After modelManager");

			int row = 0;
			selVoyno = "";
	
			selVoyno = criteria.getPredicates().get("vslNbr");

			if (topsModel == null) {
				row = 0;
			} else {
				row = topsModel.getSize();
			}
			List<String> vesselListName = new ArrayList<String>();
			List<String> vesselListVoyNo = new ArrayList<String>();
			List<String> vesselListVarNbr = new ArrayList<String>();
			List<String> vesselListTerminal = new ArrayList<String>();

			for (int i = 0; i < row; i++) {
				VesselVoyValueObject vesselVO = (VesselVoyValueObject) topsModel.get(i);
				String vname = "";
				String voyno = "";
				String varNbr = "";
				String terminal = "";
				varNbr = vesselVO.getVarNbr();
				vname = vesselVO.getVslName();
				voyno = vesselVO.getVoyNo();
				terminal = vesselVO.getTerminal();
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

		} catch (BusinessException e) {
			log.info("Exception shipStoreList : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception shipStoreList : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.clear();
				map.put("errorMessage", errorMessage);
				result.setErrors(map);
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: shipStoreList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/getPkgList")
	public ResponseEntity<?> getPkgList(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** getPkgList Start criteria :" + criteria.toString());
			String getText = CommonUtility.deNull(criteria.getPredicates().get("getText"));
			List<ShipStoreValueObject> pkglist = new ArrayList<ShipStoreValueObject>();
			List<String> pkgcdlist = new ArrayList<String>();
			List<String> pkgnmlist = new ArrayList<String>();

			if (getText != null && !getText.equals(""))
				pkglist = shipStoreService.getPkgList(getText);
			for (int i = 0; i < pkglist.size(); i++) {
				ShipStoreValueObject shipStoreValueObject = new ShipStoreValueObject();
				shipStoreValueObject = (ShipStoreValueObject) pkglist.get(i);
				pkgcdlist.add((String) shipStoreValueObject.getPkgType());
				pkgnmlist.add((String) shipStoreValueObject.getPkgDesc());
			}

			int listsize = 0;
			listsize = pkgcdlist.size();

			map.put("pkgcdlist", pkgcdlist);
			map.put("pkgnmlist", pkgnmlist);
			map.put("listsize", listsize);

		} catch (BusinessException e) {
			log.info("Exception getPkgList : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception getPkgList : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: getPkgList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/shipStoreAdd")
	public ResponseEntity<?> ShipStoreAdd(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** shipStoreAdd Start criteria :" + criteria.toString());
			topsModel = new TopsModel();
			String custCd = "";
			String mode = criteria.getPredicates().get("mode");
			String vslvoy = "";
			String varNbr = "";
			String vslName = "";
			String voyNo = "";
			if (StringUtils.equalsIgnoreCase("ADD", mode)) {
				vslvoy = CommonUtility.deNull(criteria.getPredicates().get("vslvoy"));
				varNbr = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));
				java.util.StringTokenizer vslVoyNo = null;
				vslVoyNo = new java.util.StringTokenizer(vslvoy, "~");
				vslName = vslVoyNo.nextToken().trim();
				voyNo = vslVoyNo.nextToken().trim();
				custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			} else {
				vslvoy = CommonUtility.deNull(criteria.getPredicates().get("vslvoy"));
				varNbr = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));
				java.util.StringTokenizer vslVoyNo = null;
				vslVoyNo = new java.util.StringTokenizer(vslvoy, "~");
				vslName = vslVoyNo.nextToken().trim();
				voyNo = vslVoyNo.nextToken().trim();
				custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			}

			String esnDeclarantCd = shipStoreService.getEsnDeclarantCd(custCd, varNbr);
			String esnDeclarantName = shipStoreService.getEsnDeclarantName(custCd, esnDeclarantCd);
			List<ManifestValueObject> cargoList = manifestService.getAddcrgList();
			int nbrPost = -1;
			String nbrPostStr = CommonUtility.deNull(criteria.getPredicates().get("nbrPost"));
			if (StringUtils.isNotBlank(nbrPostStr)) {
				try {
					nbrPost = Integer.parseInt(nbrPostStr);
				} catch (Exception e1) {
					nbrPost = -1;
				}
			}
			map.put("esnDeclarantCd", esnDeclarantCd);
			map.put("esnDeclarantName", esnDeclarantName);
			map.put("vslName", vslName);
			map.put("voyNo", voyNo);
			map.put("nbrPost", nbrPost);
			map.put("cargoList", cargoList);
		} catch (BusinessException e) {
			log.info("Exception shipStoreAdd : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception shipStoreAdd : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: shipStoreAdd result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.shipstore-->ShipStoreAddConfirmHandler

	@PostMapping(value = "/shipStoreAddConfirm")
	public ResponseEntity<?> shipStoreAddConfirm(HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** shipStoreAddConfirm Start criteria :" + criteria.toString());
			topsModel = new TopsModel();
			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			int noOfPkgs = 0;
			String hsCode = "";
			String crgType = "";
			String pkgsType = "";
			String pkgsDesc = "";
			double weight = 0;
			double volume = 0;
			String cargoDesc = "";
			String marking = "";
			String dgInd = "";
			String truckerIcNo = "";
			String truckerName = "";
			String truckerCNo = "";
			String dutyGoodInd = "";
			String accNo_I = "";
			String payMode = "";
			String insert = "";
			String varNbr = "";
			String truckerCd = "";
			int uaNoPkgs = 0;
			String clsShpInd = "";
			String shpStrNo = "";
			String shpradd = "";
			String shpStrRefNo = criteria.getPredicates().get("shpStrRefNo");
			shpradd = criteria.getPredicates().get("shpradd");
			boolean checkPkgsType;
			boolean checkAccNo;
			String adminFeeInd = "";
			String reasonForWaive = "";

			String noOf_pkgs = CommonUtility.deNull(criteria.getPredicates().get("noOfPkgs"));
			varNbr = criteria.getPredicates().get("varNbr");
			if (noOf_pkgs != null && !noOf_pkgs.equals(""))
				noOfPkgs = Integer.parseInt(noOf_pkgs);

			hsCode = criteria.getPredicates().get("hsCode");

			crgType = criteria.getPredicates().get("crgType");

			pkgsType = CommonUtility.deNull(criteria.getPredicates().get("pkgsType")).trim();

			String weight_s = CommonUtility.deNull(criteria.getPredicates().get("weight"));
			if (weight_s != null && !weight_s.equals(""))
				weight = Double.parseDouble(weight_s);

			String volume_s = CommonUtility.deNull(criteria.getPredicates().get("volume"));
			if (volume_s != null && !volume_s.equals(""))
				volume = Double.parseDouble(volume_s);

			cargoDesc = CommonUtility.deNull(criteria.getPredicates().get("cargoDesc")).trim();

			marking = CommonUtility.deNull(criteria.getPredicates().get("marking")).trim();

			dgInd = CommonUtility.deNull(criteria.getPredicates().get("dgInd"));

			dutyGoodInd = CommonUtility.deNull(criteria.getPredicates().get("dutyGoodInd"));

			truckerIcNo = CommonUtility.deNull(criteria.getPredicates().get("truckerIcNo")).trim();
			truckerName = CommonUtility.deNull(criteria.getPredicates().get("truckerName")).trim();
			truckerCNo = CommonUtility.deNull(criteria.getPredicates().get("truckerCNo")).trim();
			insert = criteria.getPredicates().get("insert");

			// HaiTTH1 added on 18/1/2014
			String populateTrucker = criteria.getPredicates().get("populateTrucker");
			String maxCargoTon_str = criteria.getPredicates().get("maxCargoTon");
			String vslvoy = criteria.getPredicates().get("vslvoy");

			if (hsCode == null || hsCode.trim().equalsIgnoreCase("")) {
				errorMessage = ConstantUtil.ErrorMsg_HS_Sub_Code_null;
				throw new BusinessException(errorMessage);
			}

			// Added by mc consulting
			adminFeeInd = criteria.getPredicates().get("adminfee");
			reasonForWaive = criteria.getPredicates().get("reasonwave");
			reasonForWaive = (reasonForWaive == null) ? "" : reasonForWaive;
			map.put("custCd", custCd);

			ShipStoreValueObject shipStoreValueObject = null;

			// HaiTTH1 added on 18/1/2014
			if (StringUtils.equalsIgnoreCase("TRUCKER", populateTrucker)) {
				TruckerValueObject truckerObj = shipStoreService.getTruckerDetails(truckerIcNo);
				String trucker_nm = "";
				String trucker_cnt = "";
				String trucker_add = "";
				if (truckerObj != null) {
					trucker_nm = truckerObj.getTruckerNm();
					trucker_cnt = truckerObj.getTruckerContact();
					trucker_add = truckerObj.getTruckerAdd();
				}

				map.put("trucker_nm", trucker_nm);
				map.put("trucker_cnt", trucker_cnt);
				map.put("trucker_add", trucker_add);
				map.put("trucker_ciNo", truckerIcNo);
				map.put("shpStrRefNo", shpStrRefNo);
				map.put("noOf_pkgs", "" + noOf_pkgs);
				map.put("hsCode", hsCode);
				map.put("weight", weight_s);
				map.put("volume", "" + volume_s);
				map.put("crgType", crgType);
				map.put("pkgsType", pkgsType);
				map.put("cargoDesc", cargoDesc);
				map.put("marking", marking);
				map.put("dgInd", "" + dgInd);
				map.put("dutyGoodInd", dutyGoodInd);
				map.put("custCd", custCd);
				map.put("maxCargoTon", maxCargoTon_str);
				map.put("vslvoy", "" + vslvoy);
				map.put("varNbr", varNbr);
				map.put("adminfee", adminFeeInd);
				map.put("reasonwave", reasonForWaive);

				map.put("topsModel", topsModel);

			} else {
				if (pkgsType != null && !pkgsType.equals("")) {
					checkPkgsType = shipStoreService.chkPkgsType(pkgsType);
					if (!checkPkgsType) {
						errorMessage = ConstantUtil.ErrorMsg_Pkg_Code_Not_Valid;
						throw new BusinessException(errorMessage);
					}
				}

				truckerCd = shipStoreService.getTruckerCd(truckerIcNo);

				if ( !truckerIcNo.equals("")) {
					if (StringUtils.isNotBlank(truckerCd)) {
						truckerName = shipStoreService.getTruckerName(custCd, truckerIcNo);
					} else {
						if (truckerName != null && !truckerName.equals("")) {
//							truckerName = truckerName;
						} else {
							errorMessage = ConstantUtil.ErrorMsg_Enter_Trucker_Name;
							throw new BusinessException(errorMessage);
						}
					}

				}

				List<ShipStoreValueObject> accNoList = new ArrayList<ShipStoreValueObject>();
				List<ShipStoreValueObject> UserAccNoList = new ArrayList<ShipStoreValueObject>();
				List<String> userAccNo = new ArrayList<String>();
				List<String> accNo = new ArrayList<String>();
				String getAccNo = "";
				List<ShipStoreValueObject> ssDetails = new ArrayList<ShipStoreValueObject>();
				accNoList = shipStoreService.getAccNo(truckerIcNo);

				if (accNoList.size() != 0) {
					for (int i = 0; i < accNoList.size(); i++) {
						shipStoreValueObject = new ShipStoreValueObject();
						shipStoreValueObject = (ShipStoreValueObject) accNoList.get(i);
						accNo.add(shipStoreValueObject.getAccNo());
					}
				}
				if (accNoList.size() != 0)
					getAccNo = shipStoreValueObject.getAccNo();
				else
					getAccNo = "No";

				UserAccNoList = shipStoreService.getUserAccNo(custCd, getAccNo);
				if (UserAccNoList.size() != 0) {
					for (int i = 0; i < UserAccNoList.size(); i++) {
						shipStoreValueObject = new ShipStoreValueObject();
						shipStoreValueObject = (ShipStoreValueObject) UserAccNoList.get(i);
						userAccNo.add(shipStoreValueObject.getAccNo());
					}
				}
				if (insert != null && insert.equals("insert")) {
					// crgTypeCd = esn.getCrgTypeCd(crgType);
					accNo_I = criteria.getPredicates().get("accNo");
					payMode = criteria.getPredicates().get("payMode");
					checkAccNo = shipStoreService.chkAccNo(accNo_I);
					if (accNo_I.equals("CASH") || accNo_I.equals("CA")) {
					} else {
						if (!checkAccNo) {
							errorMessage = ConstantUtil.ErrorMsg_Billable_Party_Not_Valid;
							throw new BusinessException(errorMessage);
						}
					}

					// Amended by VietNguyen 06/03/2014
					shpStrNo = (String) shipStoreService.insertSSDetailsForDPE(varNbr, custCd, truckerIcNo, truckerCNo,
							marking, dgInd, hsCode, dutyGoodInd, truckerName, pkgsType, noOfPkgs, weight, volume,
							accNo_I, payMode, cargoDesc, truckerCd, UserID, crgType, shpStrRefNo, shpradd, adminFeeInd,
							reasonForWaive);

					// MCC for oscar admin fee request
					if (custCd != null && custCd.equals("JP")) {
						if (adminFeeInd != null && adminFeeInd.equals("Y")) {
							int adviceId = 0;

							adviceId = shipStoreService.captureWaiverAdviceRequest(shpStrNo, UserID,
									ProcessChargeConst.SS_SSAD, false, null, null); // Fixed To pass vv_cd as null
							try {
								log.info(" Invoke oscar request for waiver approval: ");
								AdminFeeWaiverValueObject adminWaiverVO = shipStoreService.invokeOscarWaiverRequest(
										adviceId, shpStrNo, UserID, ProcessChargeConst.SS_SSAD);
								// SOAPClientUtilsForAdminFeeWaiver.callOscarWaiverWebService(waiveVO);
								boolean waiverRequestToOscarSucceed = shipStoreService
										.sendAdminWaiverRequestToOscar(adminWaiverVO);
								if (!waiverRequestToOscarSucceed) {
									log.info("OSCAR microservice request failed. Update Waiver msg status as Error");
									adminWaiverVO.setWaiverStatus("E");
									adminWaiverVO.setApprovalRemarks("Error in Oscar service call");

									shipStoreService.updateWaiverAdvice(adminWaiverVO, adminWaiverVO.getCreateUserId());
								}

							} catch (Exception e) {
								log.info("Exception shipStoreAddConfirm AdminFeeWaiverService : ", e);

							}
						} else if (adminFeeInd != null && adminFeeInd.equals("N")) {
							log.info("Generate billable events for admin fee created by JP staff: " + shpStrNo);
							shipStoreService.insertSSAdminFeeEvent(shpStrNo, UserID);
						}
					}

					if (shpStrNo != null && !shpStrNo.equals("")) {
						ssDetails = shipStoreService.getShpStrDetails(shpStrNo, custCd);
					}

				} else {
					// retrieve vessel scheme
					log.info("In add handler, Start to retrieve vessel scheme....");
					String vslScheme = shipStoreService.getVslScheme(varNbr);
					log.info("In add handler, vessel scheme for " + varNbr + " is " + vslScheme + " ...");

					map.put("vslScheme", vslScheme);
				}

				int maxCargoTon = 0;

				try {
					maxCargoTon = Integer.parseInt(shipStoreService.getValue("SSMXT"));
				} catch (Exception e) {
					log.info("Exception shipStoreAddConfirm : ", e);
				}

				map.put("maxCargoTon", Integer.valueOf(maxCargoTon));

				map.put("shpStrNo", shpStrNo);
				map.put("accNo", accNo);
				map.put("pkgsDesc", pkgsDesc);
				map.put("truckerName", truckerName);
				map.put("userAccNo", userAccNo);
				map.put("uaNoPkgs", "" + uaNoPkgs);
				map.put("clsShpInd", clsShpInd);
				map.put("adminFeeInd", adminFeeInd);
				map.put("reasonForWaive", reasonForWaive);
				// map.put("weight",""+weight);
				// map.put("volume",""+volume);
				map.put("shpStrDetails", ssDetails);
				map.put("topsModel", topsModel);

			}

		} catch (BusinessException e) {
			log.info("Exception shipStoreAddConfirm : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception shipStoreAddConfirm : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: shipStoreAddConfirm result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/shipStoreDetails")
	public ResponseEntity<?> shipStoreDetails(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		String esnCreatedCode = "";
		errorMessage = null;
		topsModel = new TopsModel();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** shipStoreDetails Start criteria :" + criteria.toString());
			String varNbr = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));
			String custCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			// String varNbr = "";
			String shpStrNo = criteria.getPredicates().get("shpStrNo");
			// varNbr = criteria.getPredicates().get("varNbr");
			ShipStoreValueObject shipStoreValueObject = null;

			List<ShipStoreValueObject> ssDetails = new ArrayList<ShipStoreValueObject>();
			if (shpStrNo != null && !shpStrNo.equals("")) {
				ssDetails = shipStoreService.getShpStrDetails(shpStrNo, custCode);

			}

			for (int i = 0; i < ssDetails.size(); i++) {
				shipStoreValueObject = (ShipStoreValueObject) ssDetails.get(i);
				esnCreatedCode = shipStoreValueObject.getCustId();
			}

			String esnDeclarantCd = shipStoreService.getEsnDeclarantCd(esnCreatedCode, varNbr);
			String esnDeclarantName = shipStoreService.getEsnDeclarantName(esnCreatedCode, esnDeclarantCd);

			map.put("esnDeclarantCd", esnDeclarantCd);
			map.put("esnDeclarantName", esnDeclarantName);

			map.put("shpStrNo", shpStrNo);
			map.put("shpStrDetails", ssDetails);
			map.put("topsModel", topsModel);

		} catch (BusinessException e) {
			log.info("Exception shipStoreDetails : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception shipStoreDetails : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: shipStoreDetails result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/shipStoreCancel")
	public ResponseEntity<?> shipStoreCancel(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		topsModel = new TopsModel();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);

			log.info("** START: shipStoreCancel Start criteria :" + criteria.toString());

			String shpStrNo =  CommonUtility.deNull(criteria.getPredicates().get("shpStrNo"));

			if ( !shpStrNo.equals(""))
				shipStoreService.shpStrCancel(shpStrNo);

			map.put("topsModel", topsModel);
		} catch (BusinessException e) {
			log.info("Exception shipStoreCancel : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception shipStoreCancel : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: shipStoreCancel result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/shipStoreAmend")
	public ResponseEntity<?> shipStoreAmend(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		topsModel = new TopsModel();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** shipStoreAmend Start criteria :" + criteria.toString());
			String custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String shpStrNo = criteria.getPredicates().get("shpStrNo");
			shpStrNo = criteria.getPredicates().get("shpStrNo");

			List<ShipStoreValueObject> ssDetails = new ArrayList<ShipStoreValueObject>();

			if (shpStrNo != null && !shpStrNo.equals("")) {
				ssDetails = shipStoreService.getShpStrDetails(shpStrNo, custCd);
			}

			int maxCargoTon = 0;

			try {
				maxCargoTon = Integer.parseInt(shipStoreService.getValue("SSMXT"));
			} catch (Exception e) {
				log.info("Exception shipStoreAmend : ", e);
			}

			map.put("maxCargoTon", Integer.valueOf(maxCargoTon));
			map.put("shpStrNo", shpStrNo);
			map.put("shpStrDetails", ssDetails);
			shipStoreAmendSetParams(request, map);
		} catch (BusinessException e) {
			log.info("Exception shipStoreAmend : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception shipStoreAmend : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: shipStoreAmend result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@SuppressWarnings("unchecked")
	private void shipStoreAmendSetParams(HttpServletRequest request, Map<String, Object> map) {

		String mode = "";
		String custCdParam = "";
		int maxCargoTon = 0;
		String usrtyp = "";
		String custCd = "";
		String vslvoy = "";
		String varNbr = "";
		String vslName = "";
		String voyNo = "";
		String shpStrRefNo = "";
		String shipperName = "";
		String pkgsDesc = "";
		String truckerName = "";
		String truckerNo = "";
		String truckerCNo = "";
		String marking = "";
		String hsCode = "";
		String pkgsType = "";
		String shprAdd = "";
		double weight = 0;
		double volume = 0;
		String crgDesc = "";
		String dgIn = "";
		String dutiGI = "";
		String BillAccNo = "";
		String payMode = "";
		int noOfPkgs = 0;
		String crgType = "";
		String crgTypeDesc = "";
		String esnCreatedCode = "";
		String esnDeclarantCd = "";
		String esnDeclarantName = "";
		String shpStrNo = "";
		String adminFeeInd = "";
		String reasonForWaive = "";
		List<ShipStoreValueObject> shpStrDetails = new ArrayList<ShipStoreValueObject>();
		List<ManifestValueObject> cargoList = new ArrayList<ManifestValueObject>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			custCdParam = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			log.info("START: shipStoreAmendSetParams criteria:" + criteria.toString());
			if (StringUtils.equalsIgnoreCase("AMEND", mode)) {
				maxCargoTon = 0;
				try {
					maxCargoTon = ((Integer) map.get("maxCargoTon")).intValue();
				} catch (Exception e) {
					log.info("Exception shipStoreAmendSetParams : ", e);
				}
				usrtyp = (String) custCdParam;
				varNbr = CommonUtility.deNull(criteria.getPredicates().get("varNbr"));
				vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
				voyNo = CommonUtility.deNull(criteria.getPredicates().get("voyNo"));

				shpStrNo = CommonUtility.deNull(criteria.getPredicates().get("shpStrNo"));

				shpStrDetails = (List<ShipStoreValueObject>) map.get("shpStrDetails");

				ShipStoreValueObject shipStoreValueObject = new ShipStoreValueObject();
				for (int i = 0; i < shpStrDetails.size(); i++) {
					shipStoreValueObject = (ShipStoreValueObject) shpStrDetails.get(i);
					shpStrRefNo = shipStoreValueObject.getBookingRefNo();
					crgDesc = shipStoreValueObject.getCrgDesc();
					crgType = shipStoreValueObject.getCrgType();
					crgTypeDesc = shipStoreValueObject.getCrgStatus();
					weight = shipStoreValueObject.getGrWt();
					volume = shipStoreValueObject.getGrVolume();
					noOfPkgs = shipStoreValueObject.getNoOfCntr();
					pkgsDesc = shipStoreValueObject.getPkgDesc();
					pkgsType = shipStoreValueObject.getPkgType();
					truckerNo = shipStoreValueObject.getTruckerNo();
					shipperName = shipStoreValueObject.getTruckerName();
					truckerCNo = shipStoreValueObject.getTruckerCNo();
					marking = shipStoreValueObject.getCrgMarking();
					hsCode = shipStoreValueObject.getHsCode();
					shprAdd = shipStoreValueObject.getShipperName();
					BillAccNo = shipStoreValueObject.getAccNo();
					payMode = shipStoreValueObject.getPayMode();
					// billPartyAccName = esnListValueObject.getBillPartyName();
					// loadingOpI = esnListValueObject.getOpInd();
					// loadFrom = esnListValueObject.getPortL();
					dgIn = shipStoreValueObject.getDgInd();
					dutiGI = shipStoreValueObject.getDutiGI();
					esnCreatedCode = shipStoreValueObject.getCustId();
					adminFeeInd = shipStoreValueObject.getAdminFeeInd();
					reasonForWaive = shipStoreValueObject.getReasonForWaive();
				}
			} else {
				try {
					maxCargoTon = Integer.parseInt((String) map.get("maxCargoTon"));
				} catch (Exception e) {
					log.info("Exception shipStoreAmendSetParams : ", e);
				}
				usrtyp = (String) custCd;
				vslvoy = (String) CommonUtility.deNull(criteria.getPredicates().get("vslvoy"));
				varNbr = (String) CommonUtility.deNull(criteria.getPredicates().get("varNbr"));
				voyNo = vslvoy;
				vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
				custCd = (String) custCdParam;
				usrtyp = (String) custCdParam;
				shipperName = (String) CommonUtility.deNull(criteria.getPredicates().get("trucker_nm"));
				truckerCNo = (String) CommonUtility.deNull(criteria.getPredicates().get("trucker_cnt"));
				shprAdd = (String) CommonUtility.deNull(criteria.getPredicates().get("trucker_add"));
				truckerNo = (String) CommonUtility.deNull(criteria.getPredicates().get("trucker_ciNo"));
				shpStrRefNo = (String) CommonUtility.deNull(criteria.getPredicates().get("shpStrRefNo"));
				noOfPkgs = Integer.parseInt((String) CommonUtility.deNull(criteria.getPredicates().get("noOf_pkgs")));
				hsCode = (String) CommonUtility.deNull(criteria.getPredicates().get("hsCode"));
				weight = Double.parseDouble((String) CommonUtility.deNull(criteria.getPredicates().get("weight")));
				volume = Double.parseDouble((String) CommonUtility.deNull(criteria.getPredicates().get("volume")));
				crgType = (String) CommonUtility.deNull(criteria.getPredicates().get("crgType"));
				pkgsType = (String) CommonUtility.deNull(criteria.getPredicates().get("pkgsType"));
				crgDesc = (String) CommonUtility.deNull(criteria.getPredicates().get("cargoDesc"));
				marking = (String) CommonUtility.deNull(criteria.getPredicates().get("marking"));
				dgIn = (String) CommonUtility.deNull(criteria.getPredicates().get("dgInd"));
				dutiGI = (String) CommonUtility.deNull(criteria.getPredicates().get("dutyGoodInd"));
				esnCreatedCode = (String) CommonUtility.deNull(criteria.getPredicates().get("esnCreatedCode"));
				shpStrNo = (String) CommonUtility.deNull(criteria.getPredicates().get("shpStrNo"));
				adminFeeInd = (String) CommonUtility.deNull(criteria.getPredicates().get("adminfee"));
				reasonForWaive = (String) CommonUtility.deNull(criteria.getPredicates().get("reasonwave"));
			}

			esnDeclarantCd = shipStoreService.getEsnDeclarantCd(esnCreatedCode, varNbr);
			esnDeclarantName = shipStoreService.getEsnDeclarantName(esnCreatedCode, esnDeclarantCd);

			cargoList = manifestService.getAddcrgList();

			// HaiTTH1 added on 19/3/2014
			int nbrPost = -1;
			String nbrPostStr = CommonUtility.deNull(criteria.getPredicates().get("nbrPost"));
			if (StringUtils.isNotBlank(nbrPostStr)) {
				try {
					nbrPost = Integer.parseInt(nbrPostStr);
				} catch (Exception e1) {
					nbrPost = -1;
				}
			}
			map.put("nbrPost", nbrPost);

			map.put("maxCargoTon", maxCargoTon);
			map.put("usrtyp", usrtyp);
			map.put("vslvoy", vslvoy);
			map.put("varNbr", varNbr);
			map.put("custCd", custCd);
			map.put("vslName", vslName);
			map.put("voyNo", voyNo);
			map.put("shpStrRefNo", shpStrRefNo);
			map.put("shipperName", shipperName);
			map.put("pkgsDesc", pkgsDesc);
			map.put("truckerName", truckerName);
			map.put("truckerNo", truckerNo);
			map.put("truckerCNo", truckerCNo);
			map.put("marking", marking);
			map.put("hsCode", hsCode);
			map.put("pkgsType", pkgsType);
			map.put("shprAdd", shprAdd);
			map.put("weight", weight);
			map.put("volume", volume);
			map.put("crgDesc", crgDesc);
			map.put("dgIn", dgIn);
			map.put("dutiGI", dutiGI);
			map.put("BillAccNo", BillAccNo);
			map.put("payMode", payMode);
			map.put("noOfPkgs", noOfPkgs);
			map.put("crgType", crgType);
			map.put("crgTypeDesc", crgTypeDesc);
			map.put("esnCreatedCode", esnCreatedCode);
			map.put("esnDeclarantCd", esnDeclarantCd);
			map.put("esnDeclarantName", esnDeclarantName);
			map.put("shpStrNo", shpStrNo);
			map.put("adminFeeInd", adminFeeInd);
			map.put("reasonForWaive", reasonForWaive);
			map.put("shpStrDetails", shpStrDetails);
			map.put("cargoList", cargoList);
		} catch (Exception e) {
			log.info("Exception shipStoreAmendSetParams : ", e);
		}
		log.info("END: shipStoreAmendSetParams ");
	}

	@PostMapping(value = "/shipStoreAmendConfirm")
	public ResponseEntity<?> shipStoreAmendConfirm(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		topsModel = new TopsModel();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** shipStoreAmendConfirm Start criteria :" + criteria.toString());

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			int noOfPkgs = 0;
			String hsCode = "";
			String crgType = "";
			String pkgsType = "";
			double weight = 0;
			double volume = 0;
			String cargoDesc = "";
			String marking = "";
			String dgInd = "";
			String truckerIcNo = "";
			String truckerName = "";
			String truckerCNo = "";
			String dutyGoodInd = "";
			String accNo_I = "";
			String payMode = "";
			String update = "";
			String varNbr = "";
			String truckerCd = "";
			String shpStrNo = criteria.getPredicates().get("shpStrNo");
			String shpradd = "";
			shpradd = criteria.getPredicates().get("shpradd");
			boolean checkPkgsType;
			boolean checkAccNo;
			String adminFeeInd = "";
			String reasonForWaive = "";

			String noOf_pkgs = criteria.getPredicates().get("noOfPkgs");
			varNbr = criteria.getPredicates().get("varNbr");

			if (noOf_pkgs != null && !noOf_pkgs.equals(""))
				noOfPkgs = Integer.parseInt(noOf_pkgs);

			hsCode = criteria.getPredicates().get("hsCode");

			crgType = criteria.getPredicates().get("crgType");

			pkgsType = criteria.getPredicates().get("pkgsType").trim();

			String weight_s = criteria.getPredicates().get("weight");
			if (weight_s != null && !weight_s.equals(""))
				weight = Double.parseDouble(weight_s);

			String volume_s = criteria.getPredicates().get("volume");
			if (volume_s != null && !volume_s.equals(""))
				volume = Double.parseDouble(volume_s);

			cargoDesc = criteria.getPredicates().get("cargoDesc").trim();

			marking = criteria.getPredicates().get("marking").trim();

			dgInd = criteria.getPredicates().get("dgInd");

			dutyGoodInd = criteria.getPredicates().get("dutyGoodInd");

			truckerIcNo = criteria.getPredicates().get("truckerIcNo").trim();
			truckerName = criteria.getPredicates().get("truckerName").trim();
			truckerCNo = criteria.getPredicates().get("truckerCNo").trim();
			update = criteria.getPredicates().get("update");
			String vslName = criteria.getPredicates().get("vslName");
			// Added by mc consulting
			adminFeeInd = criteria.getPredicates().get("adminfee");
			reasonForWaive = criteria.getPredicates().get("reasonwave");
			reasonForWaive = (reasonForWaive == null) ? "" : reasonForWaive;
			map.put("custCd", custCd);

			// HaiTTH1 added on 18/1/2014
			String populateTrucker = criteria.getPredicates().get("populateTrucker");
			String maxCargoTon_str = criteria.getPredicates().get("maxCargoTon");
			String vslvoy = criteria.getPredicates().get("vslvoy");
			String shpStrRefNo = criteria.getPredicates().get("shpStrRefNo");

			if (hsCode == null || hsCode.trim().equalsIgnoreCase("")) {
				errorMessage = ConstantUtil.ErrorMsg_HS_Sub_Code_null;
				throw new BusinessException(errorMessage);
			}
			ShipStoreValueObject shipStoreValueObject = null;

			// HaiTTH1 added on 18/1/2014
			if (StringUtils.equalsIgnoreCase("TRUCKER", populateTrucker)) {
				TruckerValueObject truckerObj = shipStoreService.getTruckerDetails(truckerIcNo);
				String trucker_nm = "";
				String trucker_cnt = "";
				String trucker_add = "";
				if (truckerObj != null) {
					trucker_nm = truckerObj.getTruckerNm();
					trucker_cnt = truckerObj.getTruckerContact();
					trucker_add = truckerObj.getTruckerAdd();
				}

				map.put("trucker_nm", trucker_nm);
				map.put("trucker_cnt", trucker_cnt);
				map.put("trucker_add", trucker_add);
				map.put("trucker_ciNo", truckerIcNo);
				map.put("shpStrRefNo", shpStrRefNo);
				map.put("noOf_pkgs", "" + noOf_pkgs);
				map.put("hsCode", hsCode);
				map.put("weight", weight_s);
				map.put("volume", "" + volume_s);
				map.put("crgType", crgType);
				map.put("pkgsType", pkgsType);
				map.put("cargoDesc", cargoDesc);
				map.put("marking", marking);
				map.put("dgInd", "" + dgInd);
				map.put("dutyGoodInd", dutyGoodInd);
				map.put("custCd", custCd);
				map.put("maxCargoTon", maxCargoTon_str);
				map.put("vslvoy", vslvoy);
				map.put("varNbr", varNbr);
				map.put("shpStrNo", shpStrNo);
				map.put("vslName", vslName);
				map.put("adminfee", adminFeeInd);
				map.put("reasonwave", reasonForWaive);

				map.put("topsModel", topsModel);

			} else {
				if (pkgsType != null && !pkgsType.equals("")) {
					checkPkgsType = shipStoreService.chkPkgsType(pkgsType);
					if (!checkPkgsType) {
						errorMessage = ConstantUtil.ErrorMsg_Pkg_Code_Not_Valid;
						throw new BusinessException(errorMessage);
					}
				}

				TruckerValueObject trkOnj = new TruckerValueObject();
				trkOnj = shipStoreService.getTruckerDetails(truckerIcNo);
				truckerCd = trkOnj.getTruckerCd();

				if ( !truckerIcNo.equals("")) {
					if (StringUtils.isNotBlank(truckerCd)) {

						truckerName = trkOnj.getTruckerNm();
					} else {
						if (truckerName != null && !truckerName.equals("")) {
//							truckerName = truckerName;
						} else {
							errorMessage = ConstantUtil.ErrorMsg_Enter_Trucker_Name;
							throw new BusinessException(errorMessage);
						}
					}

				}

				List<ShipStoreValueObject> accNoList = new ArrayList<ShipStoreValueObject>();
				List<ShipStoreValueObject> UserAccNoList = new ArrayList<ShipStoreValueObject>();
				List<String> userAccNo = new ArrayList<String>();
				List<String> accNo = new ArrayList<String>();
				String getAccNo = "";
				List<ShipStoreValueObject> ssDetails = new ArrayList<ShipStoreValueObject>();
				accNoList = shipStoreService.getAccNo(truckerIcNo);

				if (accNoList.size() != 0) {
					for (int i = 0; i < accNoList.size(); i++) {
						shipStoreValueObject = new ShipStoreValueObject();
						shipStoreValueObject = (ShipStoreValueObject) accNoList.get(i);
						accNo.add(shipStoreValueObject.getAccNo());
					}
				}
				if (accNoList.size() != 0)
					getAccNo = shipStoreValueObject.getAccNo();
				else
					getAccNo = "No";

				UserAccNoList = shipStoreService.getUserAccNo(custCd, getAccNo);
				if (UserAccNoList.size() != 0) {
					for (int i = 0; i < UserAccNoList.size(); i++) {
						shipStoreValueObject = new ShipStoreValueObject();
						shipStoreValueObject = (ShipStoreValueObject) UserAccNoList.get(i);
						userAccNo.add(shipStoreValueObject.getAccNo());
					}
				}
				if (update != null && update.equals("update")) {
					// crgTypeCd = esn.getCrgTypeCd(crgType);
					accNo_I = criteria.getPredicates().get("accNo");
					payMode = criteria.getPredicates().get("payMode");
					checkAccNo = shipStoreService.chkAccNo(accNo_I);

					if (accNo_I.equals("CASH") || accNo_I.equals("CA")) {
					} else {
						if (!checkAccNo) {
							errorMessage = ConstantUtil.ErrorMsg_Billable_Party_Not_Valid;
							throw new BusinessException(errorMessage);
						}
					}
					shipStoreService.updateSSDetails(shpStrNo, custCd, truckerIcNo, truckerCNo, marking, dgInd, hsCode,
							dutyGoodInd, truckerName, pkgsType, noOfPkgs, weight, volume, accNo_I, payMode, cargoDesc,
							truckerCd, UserID, crgType, shpradd, adminFeeInd, reasonForWaive);
					ssDetails = shipStoreService.getShpStrDetails(shpStrNo, custCd);
				} else {
					// retrieve vessel scheme
					log.info("In amend handler, Start to retrieve vessel scheme....");
					String vslScheme = shipStoreService.getVslScheme(varNbr);
					log.info("In amend , vessel scheme for " + varNbr + " is " + vslScheme + " ...");

					map.put("vslScheme", vslScheme);
				}

				// pkgsDesc = (String)esn.getPkgsDesc(esnNo);
				// uaNoPkgs = (int)esn.getUaNoPkgs(esnNo);
				// clsShpInd = (String)esn.getClsShipInd(varNbr);
				// billName = esn.getBillablePartyName(accNo_I);
				map.put("accNo", accNo);
				map.put("userAccNo", userAccNo);
				map.put("shpStrNo", shpStrNo);
				map.put("truckerName", truckerName);
				map.put("shpStrDetails", ssDetails);
				map.put("topsModel", topsModel);

			}

		} catch (BusinessException e) {
			log.info("Exception shipStoreAmendConfirm : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception shipStoreAmendConfirm : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: shipStoreAmendConfirm result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

}
