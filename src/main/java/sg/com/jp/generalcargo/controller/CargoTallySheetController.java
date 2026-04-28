package sg.com.jp.generalcargo.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoTallysheetDet;
import sg.com.jp.generalcargo.domain.GbccCargoTallysheetId;
import sg.com.jp.generalcargo.domain.GbccCargoTallysheetVO;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.StevedoreCompany;
import sg.com.jp.generalcargo.domain.VesselCall;
import sg.com.jp.generalcargo.service.CargoTallySheetService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = CargoTallySheetController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class CargoTallySheetController {
	public static final String ENDPOINT = "gc/cargoOperation";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private static final Log log = LogFactory.getLog(CargoTallySheetController.class);
	String errorMessage = null;
	String controllerName = "cargoTallySheetcontroller";

	@Autowired
	private CargoTallySheetService gbccServ;

	@PostMapping(value = "/cargoTallySheet")
	public ResponseEntity<?> cargoTallySheet(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: cargoTallySheet criteria:" + criteria.toString());

			// Calling through EJB
			log.info(controllerName + " - factory.lookUpHome");

			String coyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String coyName = CommonUtility.deNull(criteria.getPredicates().get("coNm"));

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			// coyCode = "HAISOON";

			log.info(controllerName + " - [SessionData] coyCode: " + coyCode);
			log.info(controllerName + " - [SessionData] coyName: " + coyName);
			log.info(controllerName + " - [SessionData] userId: " + userId);

			// get param
			String mode = (String) criteria.getPredicates().get("mode");
			if (mode == null)
				mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			else
				request.removeAttribute("mode");
			if (mode.equalsIgnoreCase(""))
				mode = "view";
			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));

			log.info(controllerName + " - [Param] mode: " + mode);
			log.info(controllerName + " - [Param] command: " + command);

			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String crDttmS = CommonUtility.deNull(criteria.getPredicates().get("crDttm"));
			Date crDttm = null;
			if (!crDttmS.trim().equalsIgnoreCase(""))
				crDttm = CommonUtil.convertStrToDate(crDttmS, ConstantUtil.DATEFORMAT_INPUT_LONG);

			String oprType = CommonUtility.deNull(criteria.getPredicates().get("oprType"));
			String hatchNoS = CommonUtility.deNull(criteria.getPredicates().get("hatchNo"));
			Integer hatchNo = null;
			if (hatchNoS != null)
				hatchNo = new Integer(Integer.parseInt(hatchNoS));

			String stevCd = CommonUtility.deNull(criteria.getPredicates().get("stevCd"));

			log.info(controllerName + " - [Param] vvCd: " + vvCd);
			log.info(controllerName + " - [Param] crDttm: " + crDttmS);
			log.info(controllerName + " - [Param] oprType: " + oprType);
			log.info(controllerName + " - [Param] hatchNbr: " + hatchNoS);
			log.info(controllerName + " - [Param] stevCd: " + stevCd);

			GbccCargoTallysheetVO cVO = null;
			log.info(controllerName + " - getById");
			cVO = gbccServ.cc_getCargoTallySheetById(coyCode, vvCd, crDttm, oprType, hatchNo, stevCd);
			log.info(controllerName + " - getById: " + cVO);

			// String errMsg = "";
			if (command.equalsIgnoreCase("save")) {
				String checkerNm = criteria.getPredicates().get("checkerNm");
				log.info(controllerName + " - [input] checker name: " + checkerNm);
				String checkerNbr = criteria.getPredicates().get("checkerNbr");
				log.info(controllerName + " - [input] checker mobile: " + checkerNbr);

				String workCommenceDttmS = criteria.getPredicates().get("workCommenceDttm");
				Date workCommenceDttm = CommonUtil.convertStrToDate(workCommenceDttmS, "ddMMyyyy HHmm");
				log.info(controllerName + " - [input] workCommenceDttm: " + workCommenceDttmS);
				String workCompleteDttmS = criteria.getPredicates().get("workCompleteDttm");
				Date workCompleteDttm = CommonUtil.convertStrToDate(workCompleteDttmS, "ddMMyyyy HHmm");
				log.info(controllerName + " - [input] workCommpleteDttm: " + workCompleteDttmS);
				String fromDttmS = criteria.getPredicates().get("fromDttm");
				Date fromDttm = CommonUtil.convertStrToDate(fromDttmS, "ddMMyyyy HHmm");
				log.info(controllerName + " - [input] fromDttm: " + fromDttmS);
				String toDttmS = criteria.getPredicates().get("toDttm");
				Date toDttm = CommonUtil.convertStrToDate(toDttmS, "ddMMyyyy HHmm");
				log.info(controllerName + " - [input] toDttm: " + toDttmS);

				GbccCargoTallysheetId id = cVO.getId();
				if (id == null) {
					log.info(controllerName + " - new record");
					id = new GbccCargoTallysheetId();
					id.setVvCd(vvCd);
					id.setHatchNbr(hatchNo);
					id.setOprType(oprType);
					cVO.setId(id);
//					cVO.setStevCoCd(cVO.getViewVesselStevedoreVO().getId().getStevCoCd());			
//					cVO.setOprType(oprType);
					// cVO.setStevCoCd("HSH");
				}
				if (id.getVvCd() == null || id.getHatchNbr() == null || id.getOprType() == null) {
					id.setHatchNbr(hatchNo);
					id.setOprType(oprType);
					cVO.setId(id);
				}

				cVO.setStevCoCd(stevCd);
				cVO.setCheckerNm(checkerNm);
				cVO.setCheckerHp(checkerNbr);
				cVO.setWorkCommenceDttm(workCommenceDttm);
				cVO.setWorkCompleteDttm(workCompleteDttm);
				cVO.setFromDttm(fromDttm);
				cVO.setToDttm(toDttm);
				cVO.setSubmittedDttm(new Date());
				cVO.setLastModifyUserId(userId);

				log.info(controllerName + " - getDetails");

				List<GbccCargoTallysheetDet> cdVOs = cVO.getCargoTallysheetDetVO();
				int lstSize = cdVOs.size();

				log.info(controllerName + " - det size: " + lstSize);

//				String[] detRefV = new String[lstSize];
//				String[] detQtyV = new String[lstSize];
//				String[] detRecvV = new String[lstSize];
//				
//				String[] detRefP = CommonUtil.getRequiredStringParameters(request,"blbkNo");
//				String[] detQtyP = CommonUtil.getRequiredStringParameters(request,"recvQty");
//				String[] detRecvP = CommonUtil.getRequiredStringParameters(request,"recvInd");
//				
//				log.info(controllerName + " - detQtyP size " + detQtyP.length);
//				
//				if (detRefP != null) {
//					log.info(controllerName + " - multilines");
//					log.info(controllerName + " - size of blbk: " + detRefP.length);
//					GetRequestArrayValues(detRefP, detRefV);
//					GetRequestArrayValues(detQtyP, detQtyV);
//					GetRequestArrayValues(detRecvP, detRecvV);
//				} else {
//					log.info(controllerName + " - one line");
//					detRefV[0] = criteria.getPredicates().get("blbkNo");
//					detQtyV[0] = criteria.getPredicates().get("recvQty");
//					detRecvV[0] = criteria.getPredicates().get("recvInd");
//				}
//				
//				log.info(controllerName + " - [input] BL/BK: "  + detRefV);
//				log.info(controllerName + " - [input] TransQty: "  + detQtyV);

				String[] detRefV = new String[lstSize];
				String[] detQtyV = new String[lstSize];
				String[] detRecvV = new String[lstSize];

				for (int i = 0; i < lstSize; i++) {
					detRefV[i] = (String) criteria.getPredicates().get("blbkNo" + i);
					detQtyV[i] = (String) criteria.getPredicates().get("recvQty" + i);
					detRecvV[i] = (String) criteria.getPredicates().get("recvInd" + i);
					log.info(controllerName + " - [input] BL/BK " + i + ": " + detRefV[i]);
					log.info(controllerName + " - [input] TransQty " + i + ": " + detQtyV[i]);
					log.info(controllerName + " - [input] Receive " + i + ": " + detRecvV[i]);
				}

				log.info(controllerName + " - Det " + lstSize);

				for (int i = 0; i < lstSize; i++) {
					GbccCargoTallysheetDet cdVO = (GbccCargoTallysheetDet) cdVOs.get(i);
					log.info(controllerName + " - List Det " + i + " Bl/Bk: " + cdVO.getId().getBlBkNbr());
					for (int j = 0; j < lstSize; j++) {
						String blbkNo = CommonUtility.deNull(detRefV[j]);
						String transQtyS = CommonUtility.deNull(detQtyV[j]);
						String recvInd = CommonUtility.deNull(detRecvV[j]);

						log.info(controllerName + " - Array Det " + j + " Bl/Bk: " + blbkNo);
						if (cdVO.getId().getBlBkNbr().equalsIgnoreCase(blbkNo)) {
							Integer transQty = new Integer(0);
							if (!transQtyS.equalsIgnoreCase(""))
								transQty = new Integer(transQtyS);

							BigDecimal totalTon = new BigDecimal(
									transQty.intValue() * cdVO.getTonPerPkgs().floatValue());
							cdVO.setHatchNbr(Integer.parseInt(hatchNoS));
							cdVO.setOprType(oprType);
							cdVO.setTransQty(transQty);
							cdVO.setTransTon(totalTon);
							cdVO.setRecvInd(recvInd);
							cdVO.setLastModifyUserId(userId);
							log.info(controllerName + " - Det " + i + " Qty: " + cdVO.getTransQty());
							log.info(controllerName + " - Det " + i + " Ton: " + cdVO.getTransTon());
							continue;
						}
					}
				}

				for (int i = 0; i < lstSize; i++) {
					GbccCargoTallysheetDet cdVO = (GbccCargoTallysheetDet) cdVOs.get(i);
					log.info(controllerName + " - Det " + i + " Qty: " + cdVO.getTransQty());
					log.info(controllerName + " - Det " + i + " Ton: " + cdVO.getTransTon());

				}

				if (gbccServ.cc_persistCargoTallySheet(cVO)) {
					log.info(controllerName + " - Save Successful");
					map.put("mode", "list");
					map.put("vvCd", "");
					map.put("berthNo", "");
					map.put("oprType", "");
					map.put("hatchNo", "");
					map.put("request", "CargoTallySheetList");
					CargoTallySheetList(request, map);
					result.setData(map);
					result.setSuccess(true);
					return ResponseEntityUtil.success(result.toString());
				} else {
					log.info(controllerName + " - Save Error");
					errorMessage = request + "save error";
				}
			} else {
				List<MiscTypeCode> lstOprType = gbccServ.cc_getMiscTypeCode(ConstantUtil.MISCTYPECD_OPERATION_TYPE);
				map.put("lstOprType", lstOprType);
			}

			map.put("CoyCode", coyCode);
			map.put("CoyName", coyName);

			map.put("cVO", cVO);

			map.put("vvCd", vvCd);
			map.put("crDttm", CommonUtil.formatDateToStr(crDttm, ConstantUtil.DATEFORMAT_INPUT_LONG));
			map.put("oprType", oprType);
			map.put("hatchNo", hatchNoS);
			map.put("stevCd", stevCd);

			map.put("mode", mode);

			map.put("request", "CargoTallySheetAddEdit");

		} catch (BusinessException e) {
			log.info("Exception cargoTallySheet : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception cargoTallySheet : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: cargoTallySheet result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	void GetRequestArrayValues(String[] inArr, String[] outArr) {
		log.info("START: GetRequestArrayValues "+" inArr:"+inArr +" outArr:"+outArr);

		int oSize = outArr.length;

		if (inArr != null) {
			int iSize = inArr.length;
			for (int i = 0; i < iSize; i++) {
				if (i < oSize) {
					outArr[i] = CommonUtility.deNull(inArr[i]);
				}
			}
		}
	}

	@PostMapping(value = "/cargoTallySheetList")
	public ResponseEntity<?> CargoTallySheetList(HttpServletRequest request, Map<String, Object> map)
			throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: CargoTallySheetList criteria:" + criteria.toString());

			String coyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String coyName = CommonUtility.deNull(criteria.getPredicates().get("coNm"));

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			// coyCode = "HAISOON";

			log.info(controllerName + " - [SessionData] coyCode: " + coyCode);
			log.info(controllerName + " - [SessionData] coyName: " + coyName);
			log.info(controllerName + " - [SessionData] userId: " + userId);

			// get param
			String mode = (String) criteria.getPredicates().get("mode");
			log.info(controllerName + " - [Param] session mode: " + mode);
			if (mode == null)
				mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			else
				request.removeAttribute("mode");

			// if (mode.equalsIgnoreCase("")) mode = "list";

			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));
			String format = CommonUtility.deNull(criteria.getPredicates().get("format"));
			if (format.equalsIgnoreCase(""))
				format = "html";

			log.info(controllerName + " - [Param] mode: " + mode);
			log.info(controllerName + " - [Param] command: " + command);
			log.info(controllerName + " - [Param] format: " + format);

			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String berthNo = CommonUtility.deNull(criteria.getPredicates().get("berthNo"));
			String oprType = CommonUtility.deNull(criteria.getPredicates().get("oprType"));
			String hatchNoS = CommonUtility.deNull(criteria.getPredicates().get("hatchNo"));
			Integer hatchNo = null;
			if (!hatchNoS.equalsIgnoreCase(""))
				hatchNo = new Integer(Integer.parseInt(hatchNoS));

			String sortby = "";
			String stevCd = CommonUtility.deNull(criteria.getPredicates().get("stevCd"));

			String isExcel = CommonUtility.deNull(criteria.getPredicates().get("isExcel"));
			Boolean needAllData = false;
			if (!isExcel.isEmpty()) {
				needAllData = true;
			}

			log.info(controllerName + " - [Param] vvCd: " + vvCd);
			log.info(controllerName + " - [Param] berthNo: " + berthNo);
			log.info(controllerName + " - [Param] oprType: " + oprType);
			log.info(controllerName + " - [Param] hatchNo: " + hatchNoS);
			log.info(controllerName + " - [Param] sortBy: " + sortby);
			log.info(controllerName + " - [Param] stevCd: " + stevCd);

			List<GbccCargoTallysheetVO> lstAll = new ArrayList<GbccCargoTallysheetVO>();
			List<VesselCall> lstVV = gbccServ.cc_getCargoOprVesselCall(coyCode);
			List<String> lstBerth = gbccServ.cc_getCargoOprBerth(coyCode);
			List<MiscTypeCode> lstOprType = gbccServ.cc_getMiscTypeCode(ConstantUtil.MISCTYPECD_OPERATION_TYPE);
			List<StevedoreCompany> lstStev = null;
			if (coyCode.equalsIgnoreCase("JP") || coyCode.equalsIgnoreCase("")) {
				lstStev = gbccServ.cc_getCargoOprStevedore();
			}

			boolean goAddPage = false;
			// String errorMessage = "";
			GbccCargoTallysheetVO oVO = null;

			if (mode.equalsIgnoreCase("add")) {
				String chkErrCode = gbccServ.cc_checkCargoTallysheetAdd(vvCd, stevCd, coyCode);
				if (!chkErrCode.equalsIgnoreCase("")) {
					errorMessage = getErrorMessage(chkErrCode);
				} else {
					oVO = gbccServ.cc_getCargoTallySheetById(coyCode, vvCd, null, oprType, hatchNo, stevCd);
					log.info(controllerName + " - [getCargoTallySheetById] " + oVO);
					goAddPage = checkAdd(oVO);
					log.info(controllerName + " - [err page] " + errorMessage);
				}
				if (!goAddPage)
					mode = "list";
			}
			int count = 0;
			if (mode != null && mode.equalsIgnoreCase("list")) {
				log.info(controllerName + " - [getCargoTallySheet] ");
				lstAll = gbccServ.cc_getCargoTallySheet(coyCode, vvCd, berthNo, oprType, hatchNo, sortby, criteria,
						needAllData);
				if (!lstAll.isEmpty()) {
					count = lstAll.get(lstAll.size() - 1).getTotal();
					lstAll.remove(lstAll.size() - 1);
				}
				log.info(controllerName + " - [getCargoTallySheet] Size of result: " + lstAll.size());

			}
			map.put("CoyCode", coyCode);
			map.put("CoyName", coyName);
			map.put("vvCd", vvCd);
			map.put("berthNo", berthNo);
			map.put("oprType", oprType);
			map.put("hatchNo", hatchNoS);
			map.put("mode", mode);
			map.put("lstOprType", lstOprType);
			map.put("stevCd", stevCd);

			if (goAddPage && mode.equalsIgnoreCase("add")) {
				log.info(controllerName + " - [add] ");
				map.put("cVO", oVO);
				map.put("request", "CargoTallySheetAddEdit");
			} else {
				log.info(controllerName + " - [list] ");

				map.put("errorMessage", errorMessage);
				map.put("format", format);
				map.put("sortby", sortby);
				map.put("lstAll", lstAll);
				map.put("lstVV", lstVV);
				map.put("lstBerth", lstBerth);
				map.put("lstStev", lstStev);
				map.put("total", count);
				map.put("request", "CargoTallySheetList");
			}
		} catch (BusinessException e) {
			log.info("Exception cargoTallySheet : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception cargoTallySheet : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
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
				log.info("END: CargoTallySheetList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	protected boolean checkAdd(GbccCargoTallysheetVO oVO) {
		log.info(controllerName + " - [getCargoTallySheetById] ");
		log.info(controllerName + " - [getCargoTallySheetById] " + oVO);
		if (oVO == null) {
			errorMessage = "Vessel not found";
			log.info(controllerName + " - [error] " + errorMessage);
			return false;
		}

		if (oVO.getId().getCreateDttm() != null) {
			errorMessage = "Tally Sheet already exist!";
			log.info(controllerName + " - [error] " + errorMessage);
			return false;
		}

		if (oVO.getCargoTallysheetDetVO().size() == 0) {
			errorMessage = "No BL/BK data found!";
			log.info(controllerName + " - [error] " + errorMessage);
			return false;
		}

		return true;
	}

	protected String getErrorMessage(String sErrCode) {
		log.info("START: getErrorMessage "+" sErrCode:"+CommonUtility.deNull(sErrCode) );

		if (sErrCode.equalsIgnoreCase(ConstantUtil.CargoOprErr_NOSTEV))
			return "Stevedore not assign to the Vessel.";
		else
			return "";
	}

}
