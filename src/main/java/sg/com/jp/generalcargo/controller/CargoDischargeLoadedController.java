package sg.com.jp.generalcargo.controller;

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
import sg.com.jp.generalcargo.domain.GbccCargoOprDet;
import sg.com.jp.generalcargo.domain.GbccCargoOprId;
import sg.com.jp.generalcargo.domain.GbccCargoOprVO;
import sg.com.jp.generalcargo.domain.GbccVslProd;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.StevedoreCompany;
import sg.com.jp.generalcargo.domain.VesselCall;
import sg.com.jp.generalcargo.service.CargoDischargeLoadedService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = CargoDischargeLoadedController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class CargoDischargeLoadedController {
	public static final String ENDPOINT = "gc/cargoOperation";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private static final Log log = LogFactory.getLog(CargoDischargeLoadedController.class);
	String errorMessage = null;
	protected String ControllerName = "cargoDischargeLoadedController";

	@Autowired
	private CargoDischargeLoadedService gbccServ;

	//delegate.helper.gbcc.cargo.cargoOpr-->cargoOprListHandler-->perform()
	@PostMapping(value = "/cargoOprList")
	public ResponseEntity<?> CargoOprList(HttpServletRequest request, Map<String, Object> map)
			throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: CargoOprList criteria:" + criteria.toString());

			// session login data
			String coyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String coyName = CommonUtility.deNull(criteria.getPredicates().get("coNm"));

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			// coyCode="J&JMAR";

			log.info(ControllerName + " - [SessionData] coyCode: " + coyCode);
			log.info(ControllerName + " - [SessionData] coyName: " + coyName);
			log.info(ControllerName + " - [SessionData] userId: " + userId);

			// get param
			String mode = (String) criteria.getPredicates().get("mode");
			log.info(ControllerName + " - [Param] session mode: " + mode);
			if (mode == null)
				mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			else
				request.removeAttribute("mode");

			// if (mode.equalsIgnoreCase("")) mode = "list";

			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));
			String format = CommonUtility.deNull(criteria.getPredicates().get("format"));
			if (format.equalsIgnoreCase(""))
				format = "html";

			log.info(ControllerName + " - [Param] mode: " + mode);
			log.info(ControllerName + " - [Param] command: " + command);
			log.info(ControllerName + " - [Param] format: " + format);

			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String berthNo = CommonUtility.deNull(criteria.getPredicates().get("berthNo"));
			String shiftDateS = CommonUtility.deNull(criteria.getPredicates().get("shiftDttm"));
			Date shiftDate = null;
			if (!shiftDateS.equalsIgnoreCase(""))
				shiftDate = CommonUtil.convertStrToDate(shiftDateS, ConstantUtil.DATEFORMAT_INPUT_SHORT);
			String shiftCd = CommonUtility.deNull(criteria.getPredicates().get("shiftCd"));
			String stevCd = CommonUtility.deNull(criteria.getPredicates().get("stevCd"));

			String isExcel = CommonUtility.deNull(criteria.getPredicates().get("isExcel"));
			Boolean needAllData = false;
			if (!isExcel.isEmpty()) {
				needAllData = true;
			}

			Date crDttm = null;
			// String sortby = CommonUtility.deNull(criteria.getPredicates().get("sortby"));
			String sortby = "co.vv_Cd, co.stev_co_cd, co.shift_dttm, co.shift_cd";

			log.info(ControllerName + " - [Param] vvCd: " + vvCd);
			log.info(ControllerName + " - [Param] stevCd: " + stevCd);
			log.info(ControllerName + " - [Param] berthNo: " + berthNo);
			log.info(ControllerName + " - [Param] shiftDate: " + shiftDateS);
			log.info(ControllerName + " - [Param] shiftCd: " + shiftCd);
			log.info(ControllerName + " - [Param] sortBy: " + sortby);

			List<GbccCargoOprVO> lstAll = new ArrayList<GbccCargoOprVO>();
			List<VesselCall> lstVV = gbccServ.cc_getCargoOprVesselCall(coyCode);
			List<String> lstBerth = gbccServ.cc_getCargoOprBerth(coyCode);
			List<MiscTypeCode> lstShiftCd = gbccServ.cc_getMiscTypeCode(ConstantUtil.MISCTYPECD_SHIFT_STEV);
			List<StevedoreCompany> lstStev = null;
			if (coyCode.equalsIgnoreCase("JP") || coyCode.equalsIgnoreCase("")) {
				lstStev = gbccServ.cc_getCargoOprStevedore();
			}

			boolean goAddPage = false;
			// String errorMessage = "";
			GbccCargoOprVO oVO = null;
			if (mode.equalsIgnoreCase("add")) {

				String chkErrCode = gbccServ.cc_checkCargoOprAdd(vvCd, stevCd, coyCode, shiftCd, shiftDate);

				if (!chkErrCode.equalsIgnoreCase("")) {
					errorMessage = getErrorMessage(chkErrCode);
					if (errorMessage != null) {
						throw new BusinessException(errorMessage);
					}
					return ResponseEntityUtil.success(result.toString());
				} else {
					oVO = gbccServ.cc_getCargoOprById(vvCd, stevCd, crDttm, shiftCd, shiftDate, coyCode);
					goAddPage = checkAdd(oVO);
					stevCd = oVO.getStevCoCd();
					if (goAddPage) {
						List<MiscTypeCode> shiftVOs = gbccServ.cc_getMiscTypeCode(ConstantUtil.MISCTYPECD_SHIFT_STEV);
						map.put("shiftVOs", shiftVOs);
					}
				}

				if (!goAddPage) {
					log.info(ControllerName + " - [error for adding] " + errorMessage);
					mode = "list";
				}

			}
			int count = 0;
			if (mode != null && mode.equalsIgnoreCase("list")) {
				log.info(ControllerName + " - [getByFilter] ");
				lstAll = gbccServ.cc_getCargoOpr(vvCd, berthNo, coyCode, shiftCd, shiftDate, sortby, criteria,
						needAllData);
				if (!lstAll.isEmpty()) {
					count = (int) lstAll.get(lstAll.size() - 1).getTotal();
					lstAll.remove(lstAll.size() - 1);
				}
				log.info(ControllerName + " - [getByFilter] Size of result: " + lstAll.size());
			}
			map.put("format", format);
			map.put("CoyCode", coyCode);
			map.put("CoyName", coyName);

			map.put("vvCd", vvCd);
			map.put("stevCd", stevCd);
			map.put("berthNo", berthNo);
			map.put("shiftDttm", CommonUtil.formatDateToStr(shiftDate, ConstantUtil.DATEFORMAT_INPUT_SHORT));
			map.put("shiftCd", shiftCd);
			map.put("mode", mode);
			map.put("lstShiftCd", lstShiftCd);

			map.put("lstStev", lstStev);
			if (goAddPage && mode.equalsIgnoreCase("add")) {
				map.put("cVO", oVO);
				log.info(ControllerName + " - nextScreen: CargoOprAddEdit");
				// nextScreen(request, "CargoOprAddEdit");
			} else {
				map.put("format", format);
				map.put("sortby", sortby);
				map.put("lstAll", lstAll);
				map.put("lstVV", lstVV);
				map.put("lstBerth", lstBerth);
				map.put("errorMessage", errorMessage);
				map.put("total", count);
				log.info(ControllerName + " - nextScreen: CargoOprList");
				// nextScreen(request, "CargoOprList");
			}

		} catch (BusinessException e) {
			log.info("Exception CargoOprList : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception CargoOprList : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.clear();
				map.put("errorMessage", errorMessage);
				result.setSuccess(false);
				result.setErrors(map);
			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: CargoOprList result: " + result.toString());
			}
			
		}
		return ResponseEntityUtil.success(result.toString());
	}

	protected boolean checkAdd(GbccCargoOprVO oVO) {
		log.info(ControllerName + " - [check adding] " + oVO);

		if (oVO == null) {
			errorMessage = "Vessel not found!";
			return false;
		}

		if (oVO.getId() != null) {
			errorMessage = "Cargo Discharge/Load already exist!";
			return false;
		}

		return true;
	}

	protected String getErrorMessage(String sErrCode) {
		log.info("START: getErrorMessage Start Obj "+" sErrCode:"+ CommonUtility.deNull(sErrCode));
		if (sErrCode.equalsIgnoreCase(ConstantUtil.CargoOprErr_NOSTEV))
			return "Stevedore not assign to the Vessel.";
		else if (sErrCode.equalsIgnoreCase(ConstantUtil.CargoOprErr_NOOPENBAL))
			return "No opening balance submitted";
		else if (sErrCode.equalsIgnoreCase(ConstantUtil.CargoOprErr_NOFIRSTACTIVITY))
			return "No first activity.";
		else if (sErrCode.equalsIgnoreCase(ConstantUtil.CargoOprErr_SHIFTLESSTHANFIRSTACT))
			return "Selected Shift Date and Time is earlier than first activity.";
		else if (sErrCode.equalsIgnoreCase(ConstantUtil.CargoOprErr_SHIFTGREATERTHANLASTACT))
			return "Selected Shift Date and Time is later than last activity.";
		else if (sErrCode.equalsIgnoreCase(ConstantUtil.CargoOprErr_SHIFTGREATERTHANCURRENTTIME))
			return "Selected Shift Date and Time is later than current time.";
		else if (sErrCode.equalsIgnoreCase(ConstantUtil.CargoOprErr_NOPREVSHIFT))
			return "Previous shift has not been keyed in.";
		else if (sErrCode.equalsIgnoreCase(ConstantUtil.CargoOprErr_NOSHIFT))
			return "Shift Time not found.";
		else
			return "";
	}

	//delegate.helper.gbcc.cargo.cargoOpr-->cargoOprHandler-->perform()
	@PostMapping(value = "/cargoOpr")
	public ResponseEntity<?> cargoOpr(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: cargoOpr criteria:" + criteria.toString());
			// Calling through EJB
			log.info(ControllerName + " - factory.lookUpHome");

			// For bypassing of EJB
			// ApplicationContext ctx = new
			// ClassPathXmlApplicationContext("plgbccBeanFactory.xml");
			// GBCCService gbccServ = (GBCCService) ctx.getBean("JPGBCCService");

			// session login data
			String coyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String coyName = CommonUtility.deNull(criteria.getPredicates().get("coNm"));

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			log.info(ControllerName + " - [SessionData] coyCode: " + coyCode);
			log.info(ControllerName + " - [SessionData] coyName: " + coyName);
			log.info(ControllerName + " - [SessionData] userId: " + userId);

			// get param
			String mode = (String) criteria.getPredicates().get("mode");
			log.info(ControllerName + " - [Param] session mode: " + mode);
			if (mode == null)
				mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			else
				request.removeAttribute("mode");
			if (mode.equalsIgnoreCase(""))
				mode = "view";
			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));

			log.info(ControllerName + " - [Param] mode: " + mode);
			log.info(ControllerName + " - [Param] command: " + command);

			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String stevCd = CommonUtility.deNull(criteria.getPredicates().get("stevCd"));
			String crDttmS = CommonUtility.deNull(criteria.getPredicates().get("crDttm"));
			Date crDttm = null;
			if (!crDttmS.trim().equalsIgnoreCase(""))
				crDttm = CommonUtil.convertStrToDate(crDttmS, ConstantUtil.DATEFORMAT_INPUT_LONG);

			String shiftCd = CommonUtility.deNull(criteria.getPredicates().get("shiftCd"));
			String shiftDttmS = CommonUtility.deNull(criteria.getPredicates().get("shiftDttm"));
			Date shiftDttm = null;
			if (!shiftDttmS.equalsIgnoreCase(""))
				shiftDttm = CommonUtil.convertStrToDate(shiftDttmS, ConstantUtil.DATEFORMAT_INPUT_SHORT);

			log.info(ControllerName + " - [Param] vvCd: " + vvCd);
			log.info(ControllerName + " - [Param] stevCd: " + stevCd);
			log.info(ControllerName + " - [Param] crDttm: " + crDttmS);
			log.info(ControllerName + " - [Param] shiftDate: " + shiftDttmS);
			log.info(ControllerName + " - [Param] shiftCd: " + shiftCd);

			GbccCargoOprVO cVO = null;
			log.info(ControllerName + " - getById");
			if (mode.equalsIgnoreCase("add")) {
				if ("".equalsIgnoreCase(coyCode) || "JP".equalsIgnoreCase(coyCode))
					cVO = gbccServ.cc_getCargoOprById(vvCd, stevCd, null);
				else
					cVO = gbccServ.cc_getCargoOprByVvCdCustCd(vvCd, coyCode);
				log.info(ControllerName + " - getById for Adding");
			} else {
				cVO = gbccServ.cc_getCargoOprById(vvCd, stevCd, crDttm);
				log.info(ControllerName + " - getById for Updating");
//				log.info(ControllerName + " - crDttm: " + cVO.getCreateDttm());
			}
			log.info(ControllerName + " - getById: " + cVO);
			List<MiscTypeCode> shiftVOs = gbccServ.cc_getMiscTypeCode(ConstantUtil.MISCTYPECD_SHIFT_STEV);

			boolean goProd = false;
			if (command != null && command.equalsIgnoreCase("save")) {
				String checkerNm = criteria.getPredicates().get("checkerNm");
				log.info(ControllerName + "- check checker name: " + checkerNm);
				String checkerNbr = criteria.getPredicates().get("checkerNbr");
				log.info(ControllerName + " - check checker mobile: " + checkerNbr);
				String noOfGangs = criteria.getPredicates().get("noOfGangs");
				log.info(ControllerName + " - check noOfGangs: " + noOfGangs);

				String totalDisc = criteria.getPredicates().get("totDisc");
				log.info(ControllerName + "- check total discharge: " + totalDisc);
				String totalLoad = criteria.getPredicates().get("totLoad");
				log.info(ControllerName + "- check total load: " + totalLoad);

				String discCommenceDttmS = CommonUtility.deNull(criteria.getPredicates().get("discCommenceDttm"));
				Date discCommenceDttm = CommonUtil.convertStrToDate(discCommenceDttmS, ConstantUtil.DATEFORMAT_INPUT);
				log.info(ControllerName + "- check disc commence date: " + discCommenceDttmS);
				log.info(ControllerName + "- check disc commence date: " + discCommenceDttm);

				String discCompleteDttmS = CommonUtility.deNull(criteria.getPredicates().get("discCompleteDttm"));
				log.info(ControllerName + "- check disc complete date: " + discCompleteDttmS);
				Date discCompleteDttm = null;
				if (!discCompleteDttmS.equalsIgnoreCase(""))
					discCompleteDttm = CommonUtil.convertStrToDate(discCompleteDttmS, ConstantUtil.DATEFORMAT_INPUT);
				log.info(ControllerName + "- check disc complete date: " + discCompleteDttm);

				String loadCommenceDttmS = CommonUtility.deNull(criteria.getPredicates().get("loadCommenceDttm"));
				Date loadCommenceDttm = CommonUtil.convertStrToDate(loadCommenceDttmS, ConstantUtil.DATEFORMAT_INPUT);
				log.info(ControllerName + "- check load commence date: " + loadCommenceDttmS);
				log.info(ControllerName + "- check load commence date: " + loadCommenceDttm);

				String loadCompleteDttmS = CommonUtility.deNull(criteria.getPredicates().get("loadCompleteDttm"));
				log.info(ControllerName + "- check load complete date: " + loadCompleteDttmS);
				Date loadCompleteDttm = null;
				if (!loadCompleteDttmS.equalsIgnoreCase(""))
					loadCompleteDttm = CommonUtil.convertStrToDate(loadCompleteDttmS, ConstantUtil.DATEFORMAT_INPUT);
				log.info(ControllerName + "- check load complete date: " + loadCompleteDttm);
				if (cVO.getShiftDttm() == null)
					cVO.setShiftCd(shiftCd);
				if (cVO.getShiftCd() == null)
					cVO.setShiftDttm(shiftDttm);
				GbccCargoOprId id = cVO.getId();
				if (id == null) {
					stevCd = cVO.getStevCoCd();
					id = new GbccCargoOprId();
					id.setVvCd(vvCd);
					id.setStevCoCd(stevCd);
					cVO.setId(id);

					cVO.setShiftCd(shiftCd);
					cVO.setShiftDttm(shiftDttm);
					log.info(ControllerName + "- new id ");
					log.info(ControllerName + "- stevedore cd:  " + stevCd);
				}

				cVO.setCheckerNm(checkerNm);
				cVO.setCheckerHpNbr(checkerNbr);
				cVO.setGangsNbr(new Integer(noOfGangs));
				cVO.setLastModifyUserId(userId);
				cVO.setDiscStartDttm(discCommenceDttm);
				cVO.setLoadStartDttm(loadCommenceDttm);
				cVO.setDiscEndDttm(discCompleteDttm);
				cVO.setLoadEndDttm(loadCompleteDttm);

				// String[] hatchDiscValues =
				// CommonUtil.getRequiredStringParameters(request,"discTotal");
				// String[] hatchLoadValues =
				// CommonUtil.getRequiredStringParameters(request,"loadTotal");
				// String[] detDiscRemarkV =
				// CommonUtil.getRequiredStringParameters(request,"discRemark");
				// String[] detLoadRemarkV =
				// CommonUtil.getRequiredStringParameters(request,"loadRemark");

				// int pSize = hatchDiscValues.length;
				// for (int i=0; i<pSize; i++)
				// {
				// log.info(ControllerName + " - check Disc " + i + ": " + hatchDiscValues[i]);
				// log.info(ControllerName + " - check Load " + i + ": " + hatchLoadValues[i]);
				// }

				String[] hatchDiscValues = new String[9];
				String[] hatchLoadValues = new String[9];
				String[] detDiscRemarkV = new String[9];
				String[] detLoadRemarkV = new String[9];

				for (int i = 0; i < 9; i++) {
					hatchDiscValues[i] = (String) criteria.getPredicates().get("discTotal" + i);
					hatchLoadValues[i] = (String) criteria.getPredicates().get("loadTotal" + i);
					detDiscRemarkV[i] = (String) criteria.getPredicates().get("discRemark" + i);
					detLoadRemarkV[i] = (String) criteria.getPredicates().get("loadRemark" + i);
					log.info(ControllerName + " - check Disc " + i + ": " + hatchDiscValues[i]);
					log.info(ControllerName + " - check Load " + i + ": " + hatchLoadValues[i]);
				}

				List<GbccCargoOprDet> cdVOs = cVO.getCargoOprDetVO();

				int lstSize = cdVOs.size();
				log.info(ControllerName + " - Det " + lstSize);

				GbccCargoOprDet cdVO = null;

				for (int i = 0; i < lstSize; i++) {
					String hatchDisc = hatchDiscValues[i];
					String hatchLoad = hatchLoadValues[i];
					String discRemark = CommonUtility.deNull(detDiscRemarkV[i]);
					String loadRemark = CommonUtility.deNull(detLoadRemarkV[i]);

					Integer hDisc = new Integer(0);
					Integer hLoad = new Integer(0);

					if (!hatchDisc.equalsIgnoreCase(""))
						hDisc = new Integer(hatchDisc);
					if (!hatchLoad.equalsIgnoreCase(""))
						hLoad = new Integer(hatchLoad);

					cdVO = (GbccCargoOprDet) cdVOs.get(i);
					// GbccCargoOprPlanDetId cdVOId = new GbccCargoOprPlanDetId();
					// cdVOId.setVvCd(vvCd);
					// cdVOId.setCreateDttm(cVO.getId().getCreateDttm());
					// cdVOs.setId(cdVOId);
					cdVO.setVvCd(vvCd);
					cdVO.setHatchNbr(new Integer(i));
					cdVO.setDiscCompletedTon(hDisc);
					cdVO.setLoadCompletedTon(hLoad);
					cdVO.setDiscRemarks(discRemark);
					cdVO.setLoadRemarks(loadRemark);

					cdVO.setLastModifyUserId(userId);

				}

				if (gbccServ.cc_persistCargoOpr(cVO)) {
					crDttm = cVO.getCreateDttm();
					log.info(ControllerName + " - crDttm " + crDttm);
					log.info(ControllerName + " - vvCd " + vvCd);
					log.info(ControllerName + " - stevCd " + stevCd);
					GbccVslProd prodVO = new GbccVslProd();
					log.info(ControllerName + " - vslProd getById");

					prodVO = gbccServ.cc_getVslProdById(vvCd, stevCd, shiftCd, shiftDttm);
					List<MiscTypeCode> lstReason = gbccServ
							.cc_getMiscTypeCode(ConstantUtil.MISCTYPECD_UNDERPERF_REASON);
					log.info(ControllerName + " - vslProd " + prodVO);
					goProd = true;
					map.put("vvCd", vvCd);
					map.put("stevCd", stevCd);
					// map.put("crDttm", CommonUtil.formatDateToStr(crDttm,
					// ConstantUtil.DATEFORMAT_INPUT_LONG));
					log.info(prodVO.getId().getCreateDttm());
					map.put("crDttm",
							CommonUtil.formatDateToStr(prodVO.getId().getCreateDttm(), ConstantUtil.DATEFORMAT_INPUT_LONG));
					map.put("cVO", prodVO);
					map.put("lstReason", lstReason);
				} else {
				}

				// session.setAttribute("mode", "list");
				// this.forwardController(request, "CargoOprList");
				// return;
			}

			map.put("CoyCode", coyCode);
			map.put("CoyName", coyName);
			map.put("mode", mode);
			map.put("shiftCd", shiftCd);
			map.put("shiftDttm", CommonUtil.formatDateToStr(shiftDttm, ConstantUtil.DATEFORMAT_INPUT_SHORT));

			map.put("vvCd", vvCd);
			map.put("stevCd", stevCd);
			map.put("shiftVOs", shiftVOs);

			if (goProd) {
				log.info(ControllerName + " - goto productivity page");
				// nextScreen(request, "CargoVslProd");
			} else {
				log.info(ControllerName + " - goto load/discharge page");
				map.put("cVO", cVO);

				map.put("crDttm", CommonUtil.formatDateToStr(crDttm, ConstantUtil.DATEFORMAT_INPUT_LONG));

				// nextScreen(request, "CargoOprAddEdit");
			}

		} catch (BusinessException e) {
			log.info("Exception cargoOpr : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception cargoOpr : ", e);
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
				log.info("END: cargoOpr. Result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	//delegate.helper.gbcc.cargo.cargoOpr-->cargoVslProdHandler-->perform()
	@PostMapping(value = "/cargoVslProd")
	public ResponseEntity<?> cargoVslProd(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: cargoVslProd criteria:" + criteria.toString());

			// For bypassing of EJB
			// ApplicationContext ctx = new
			// ClassPathXmlApplicationContext("plgbccBeanFactory.xml");
			// GBCCService gbccServ = (GBCCService) ctx.getBean("JPGBCCService");

			// session login data
			String coyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String coyName = CommonUtility.deNull(criteria.getPredicates().get("coNm"));

			String userId = CommonUtility.deNull(criteria.getPredicates().get("loginId"));

			log.info(ControllerName + " - [SessionData] coyCode: " + coyCode);
			log.info(ControllerName + " - [SessionData] coyName: " + coyName);
			log.info(ControllerName + " - [SessionData] userId: " + userId);

			// get param
			String mode = (String) criteria.getPredicates().get("mode");
			log.info(ControllerName + " - [Param] session mode: " + mode);

			if (mode == null)
				mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			else
				request.removeAttribute("mode");
			if (mode.equalsIgnoreCase(""))
				mode = "view";
			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));

			log.info(ControllerName + " - [Param] mode: " + mode);
			log.info(ControllerName + " - [Param] command: " + command);

			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String stevCd = CommonUtility.deNull(criteria.getPredicates().get("stevCd"));
			String crDttmS = CommonUtility.deNull(criteria.getPredicates().get("crDttm"));
			Date crDttm = null;
			if (!crDttmS.trim().equalsIgnoreCase(""))
				crDttm = CommonUtil.convertStrToDate(crDttmS, ConstantUtil.DATEFORMAT_INPUT_LONG);

			log.info(ControllerName + " - [Param] vvCd: " + vvCd);
			log.info(ControllerName + " - [Param] stevCd: " + stevCd);
			log.info(ControllerName + " - [Param] crDttm: " + crDttmS);

			GbccVslProd cVO = null;
			log.info(ControllerName + " - getById");
			cVO = gbccServ.cc_getVslProdById(vvCd, stevCd, crDttm);
			log.info(ControllerName + " - getById for Updating");
			log.info(ControllerName + " - getById: " + cVO);

			List<MiscTypeCode> lstReason = gbccServ.cc_getMiscTypeCode(ConstantUtil.MISCTYPECD_UNDERPERF_REASON);

			if (mode.equalsIgnoreCase("edit") && command.equalsIgnoreCase("save")) {
				String reasonCd = criteria.getPredicates().get("cobUnderPerfCd");
				String reasonRemark = criteria.getPredicates().get("underPerfRemark");

				log.info(ControllerName + "- under performance cd : " + reasonCd);
				log.info(ControllerName + "- under performance remark : " + reasonRemark);

				cVO.setUnderPerfReasonCd(reasonCd);
				cVO.setUnderPerfRemarks(reasonRemark);
				cVO.setLastModifyUserId(userId);

				gbccServ.cc_persistVslProd(cVO);
				map.put("mode", "list");
				CargoOprList(request, map);
				result.setData(map);
				return ResponseEntityUtil.success(result.toString());
			}

			map.put("cVO", cVO);
			map.put("lstReason", lstReason);

			map.put("vvCd", vvCd);
			map.put("stevCd", stevCd);
			map.put("crDttm", CommonUtil.formatDateToStr(crDttm, ConstantUtil.DATEFORMAT_INPUT_LONG));

			map.put("CoyCode", coyCode);
			map.put("CoyName", coyName);
			map.put("mode", mode);

			map.put("request", "CargoVslProd");

		} catch (BusinessException e) {
			log.info("Exception cargoVslProd : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception cargoVslProd : ", e);
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
				log.info("END: cargoVslProd Result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

}
