package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
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
import sg.com.jp.generalcargo.domain.GbccCargoOpenBalDet;
import sg.com.jp.generalcargo.domain.GbccCargoOpenBalDetId;
import sg.com.jp.generalcargo.domain.GbccCargoOpenBalId;
import sg.com.jp.generalcargo.domain.GbccCargoOpenBalVO;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanDet;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanVO;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.service.CargoOpenBalService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = CargoOpenBalController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class CargoOpenBalController {
	public static final String ENDPOINT = "gc/cargoOperation";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private static final Log log = LogFactory.getLog(CargoOpenBalController.class);
	String errorMessage = null;
	String ControllerName = "cargoOpenBalController";

	@Autowired
	private CargoOpenBalService gbccServ;

	@PostMapping(value = "/cargoOpnBal")
	public ResponseEntity<?> CargoOpnBal(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: CargoOpnBal criteria:" + criteria.toString());

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
				mode = "edit";
			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));

			String ATBFrom = CommonUtility.deNull(criteria.getPredicates().get("ATBFrom"));
			String ATBTo = CommonUtility.deNull(criteria.getPredicates().get("ATBTo"));
			String lstAllChk = CommonUtility.deNull(criteria.getPredicates().get("lstAllChk"));

			log.info(ControllerName + " - [Param] mode: " + mode);
			log.info(ControllerName + " - [Param] command: " + command);

			log.info(ControllerName + " - [Param] ATBFrom: " + ATBFrom);
			log.info(ControllerName + " - [Param] ATBTo: " + ATBTo);
			log.info(ControllerName + " - [Param] lstAllChk: " + lstAllChk);

			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String stevCd = CommonUtility.deNull(criteria.getPredicates().get("stevCd"));

			GbccCargoOpenBalVO cVO = null;

			log.info(ControllerName + " - getMiscType");
			List<MiscTypeCode> reasonVOs = gbccServ.cc_getMiscTypeCodeDelayReason();
			log.info(ControllerName + " - getMiscType count " + reasonVOs.size());

			// if (command != null && command.equalsIgnoreCase("view")) {
			log.info(ControllerName + " - findById");
			cVO = gbccServ.cc_getCargoOpenBalById(vvCd, stevCd);
			log.info(ControllerName + " - findBy Id: " + cVO);
			// }

			if (command != null && command.equalsIgnoreCase("save")) {
				log.info(ControllerName + "  - saving ");
				String checkerNm = criteria.getPredicates().get("checkerNm");
				log.info(ControllerName + "  - check checker name: " + checkerNm);
				String checkerOffNbr = criteria.getPredicates().get("checkerOffNbr");
				log.info(ControllerName + "  - check checker mobile: " + checkerOffNbr);
				String checkerHPNbr = criteria.getPredicates().get("checkerHPNbr");
				log.info(ControllerName + " - check checker mobile: " + checkerHPNbr);

				String workableHatch = criteria.getPredicates().get("workableHatch");
				log.info(ControllerName + " - check workable hatch: " + workableHatch);
				String controlHatch = criteria.getPredicates().get("controlHatch");
				log.info(ControllerName + " - check control hatch: " + controlHatch);
				String noOfGangs = criteria.getPredicates().get("noOfGangs");
				log.info(ControllerName + " - check noOfGangs: " + noOfGangs);

				String commenceDt = criteria.getPredicates().get("commenceDt");
				log.info(ControllerName + " - check commence date: " + commenceDt);
				// String commenceTm = criteria.getPredicates().get("commenceTm");
				// log.info("cargoOpenBalController - check commence time: " + commenceTm);

				String reason1 = criteria.getPredicates().get("reason1");
				log.info(ControllerName + " - check reason 1: " + reason1);
				String reason2 = criteria.getPredicates().get("reason2");
				log.info(ControllerName + " - check reason 2: " + reason2);

				String mobileCraneInd = criteria.getPredicates().get("mobileCraneInd");
				log.info(ControllerName + " - check mobile crane ind: " + mobileCraneInd);
				String heavyliftInd = criteria.getPredicates().get("heavyliftInd");
				log.info(ControllerName + " - check heavy lift ind: " + heavyliftInd);

				GbccCargoOpenBalId id = cVO.getId();
				if (id == null) {
					id = new GbccCargoOpenBalId();
					id.setVvCd(vvCd);
					id.setStevCoCd(stevCd);
					cVO.setId(id);
				}

				cVO.setCheckerNm(checkerNm);
				cVO.setCheckerHpNbr(checkerHPNbr);
				cVO.setCheckerOfficeNbr(checkerOffNbr);

				cVO.setWrkHatchNbr(Integer.valueOf(workableHatch));
				cVO.setCtrlHatchNbr(Integer.valueOf(controlHatch) );
				cVO.setGangsNbr(Integer.valueOf(noOfGangs));

				// commenceDt = commenceDt + " " + commenceTm;
				cVO.setWrkStartDttm(CommonUtil.convertStrToDate(commenceDt, ConstantUtil.DATEFORMAT_INPUT));

				cVO.setDelayRsn1Cd(reason1);
				cVO.setDelayRsn2Cd(reason2);

				cVO.setMobileCraneInd(mobileCraneInd);
				cVO.setHeavyLiftInd(heavyliftInd);
				cVO.setLastModifyUserId(userId);

				String[] discOpenBalV = { "0", "0", "0", "0", "0", "0", "0", "0", "0" };
				String[] discOversideV = { "", "", "", "", "", "", "", "", "" };
				String[] discRemarkV = { "", "", "", "", "", "", "", "", "" };

				String[] loadOpenBalV = { "0", "0", "0", "0", "0", "0", "0", "0", "0" };
				String[] loadOversideV = { "", "", "", "", "", "", "", "", "" };
				String[] loadRemarkV = { "", "", "", "", "", "", "", "", "" };

				// String[] discOpenBalValues =
				// CommonUtil.getRequiredStringParameters(request,"discOpenBal");
				// String[] discOversideValues =
				// CommonUtil.getRequiredStringParameters(request,"discOversideInd");
				// String[] discRemarkValues =
				// CommonUtil.getRequiredStringParameters(request,"discRemark");
				//
				// String[] loadOpenBalValues =
				// CommonUtil.getRequiredStringParameters(request,"loadOpenBal");
				// String[] loadOversideValues =
				// CommonUtil.getRequiredStringParameters(request,"loadOversideInd");
				// String[] loadRemarkValues =
				// CommonUtil.getRequiredStringParameters(request,"loadRemark");

				String[] discOpenBalValues = new String[9];
				String[] discOversideValues = new String[9];
				String[] discRemarkValues = new String[9];
				String[] loadOpenBalValues = new String[9];
				String[] loadOversideValues = new String[9];
				String[] loadRemarkValues = new String[9];

				for (int i = 0; i < 9; i++) {
					discOpenBalValues[i] = (String) criteria.getPredicates().get("discOpenBal" + i);
					discOversideValues[i] = (String) criteria.getPredicates().get("discOversideInd" + i);
					discRemarkValues[i] = (String) criteria.getPredicates().get("discRemark" + i);
					loadOpenBalValues[i] = (String) criteria.getPredicates().get("loadOpenBal" + i);
					loadOversideValues[i] = (String) criteria.getPredicates().get("loadOversideInd" + i);
					loadRemarkValues[i] = (String) criteria.getPredicates().get("loadRemark" + i);
					log.info(ControllerName + " - check Disc " + i + ": " + CommonUtility.deNull((discOpenBalV[i])));
					log.info(ControllerName + " - check Overside " + i + ": "
							+ CommonUtility.deNull((discOversideV[i])));
					log.info(ControllerName + " - check Remark " + i + ": " + CommonUtility.deNull((discRemarkV[i])));

					log.info(ControllerName + " - check Load " + i + ": " + CommonUtility.deNull((loadOpenBalV[i])));
					log.info(ControllerName + " - check Overside " + i + ": "
							+ CommonUtility.deNull((loadOversideV[i])));
					log.info(ControllerName + " - check Remark " + i + ": " + CommonUtility.deNull((loadRemarkV[i])));
				}

				GetRequestArrayValues(discOpenBalValues, discOpenBalV);
				GetRequestArrayValues(discOversideValues, discOversideV);
				GetRequestArrayValues(discRemarkValues, discRemarkV);

				GetRequestArrayValues(loadOpenBalValues, loadOpenBalV);
				GetRequestArrayValues(loadOversideValues, loadOversideV);
				GetRequestArrayValues(loadRemarkValues, loadRemarkV);

				// for (int i=0; i<9; i++)
				// {
				// log.info(ControllerName + " - check Disc " + i + ": " +
				// CommonUtility.deNull((discOpenBalV[i])));
				// log.info(ControllerName + " - check Overside " + i + ": " +
				// CommonUtility.deNull((discOversideV[i])));
				// log.info(ControllerName + " - check Remark " + i + ": " +
				// CommonUtility.deNull((discRemarkV[i])));
				//
				// log.info(ControllerName + " - check Load " + i + ": " +
				// CommonUtility.deNull((loadOpenBalV[i])));
				// log.info(ControllerName + " - check Overside " + i + ": " +
				// CommonUtility.deNull((loadOversideV[i])));
				// log.info(ControllerName + " - check Remark " + i + ": " +
				// CommonUtility.deNull((loadRemarkV[i])));
				// }

				int cTotalLoad = 0;
				int cTotalDisc = 0;
				int pSize = discOpenBalV.length;

				List<GbccCargoOpenBalDet> cdVOs = cVO.getCargoOpenBalDetVO();
				if (cdVOs.size() == 0) {
					cdVOs = new ArrayList<GbccCargoOpenBalDet>();
					for (int i = 0; i < pSize; i++) {
						String discOpenBal = CommonUtility.deNull(discOpenBalV[i]);
						String discOverside = CommonUtility.deNull(discOversideV[i]);
						String discRemark = CommonUtility.deNull(discRemarkV[i]);

						String loadOpenBal = CommonUtility.deNull(loadOpenBalV[i]);
						String loadOverside = CommonUtility.deNull(loadOversideV[i]);
						String loadRemark = CommonUtility.deNull(loadRemarkV[i]);

						Integer hDisc = 0;
						Integer hLoad = 0;

						if (!discOpenBal.equalsIgnoreCase(""))
							hDisc = Integer.valueOf(discOpenBal) ;
						if (!loadOpenBal.equalsIgnoreCase(""))
							hLoad = Integer.valueOf(loadOpenBal);

						GbccCargoOpenBalDet cdVO = new GbccCargoOpenBalDet();
						GbccCargoOpenBalDetId cdVOId = new GbccCargoOpenBalDetId();
						cdVOId.setVvCd(vvCd);
						cdVOId.setStevCoCd(stevCd);
						cdVOId.setHatchNbr(Integer.valueOf(i));
						cdVO.setId(cdVOId);

						cdVO.setDiscOpenBalTon(hDisc);
						cdVO.setDiscOversideInd(discOverside);
						cdVO.setDiscRemarks(discRemark);

						cdVO.setLoadOpenBalTon(hLoad);
						cdVO.setLoadOversideInd(loadOverside);
						cdVO.setLoadRemarks(loadRemark);

						cdVO.setLastModifyUserId(userId);
						cdVOs.add(cdVO);

						cTotalLoad += hLoad.intValue();
						cTotalDisc += hDisc.intValue();
					}
				} else {
					int lstSize = cdVOs.size();
					log.info(ControllerName + " - Det " + lstSize);

					for (int i = 0; i < lstSize; i++) {
						String discOpenBal = CommonUtility.deNull(discOpenBalV[i]);
						String discOverside = CommonUtility.deNull(discOversideV[i]);
						String discRemark = CommonUtility.deNull(discRemarkV[i]);

						String loadOpenBal = CommonUtility.deNull(loadOpenBalV[i]);
						String loadOverside = CommonUtility.deNull(loadOversideV[i]);
						String loadRemark = CommonUtility.deNull(loadRemarkV[i]);

						Integer hDisc = 0;
						Integer hLoad = 0;

						if (!discOpenBal.equalsIgnoreCase(""))
							hDisc = Integer.valueOf(discOpenBal);
						if (!loadOpenBal.equalsIgnoreCase(""))
							hLoad = Integer.valueOf(loadOpenBal);

						GbccCargoOpenBalDet cdVO = (GbccCargoOpenBalDet) cdVOs.get(i);
						// GbccCargoOprPlanDetId cdVOId = new GbccCargoOprPlanDetId();
						// cdVOId.setVvCd(vvCd);
						// cdVOId.setCreateDttm(cVO.getId().getCreateDttm());
						// cdVOs.setId(cdVOId);
						cdVO.setDiscOpenBalTon(hDisc);
						cdVO.setDiscOversideInd(discOverside);
						cdVO.setDiscRemarks(discRemark);

						cdVO.setLoadOpenBalTon(hLoad);
						cdVO.setLoadOversideInd(loadOverside);
						cdVO.setLoadRemarks(loadRemark);

						cdVO.setLastModifyUserId(userId);
						cdVOs.add(cdVO);

						cTotalLoad += hLoad.intValue();
						cTotalDisc += hDisc.intValue();
					}
				}

				cVO.setTotDiscTon(Integer.valueOf(cTotalDisc));
				cVO.setTotLoadTon(Integer.valueOf(cTotalLoad));

				cVO.setCargoOpenBalDetVO(cdVOs);
				gbccServ.cc_persistCargoOpenBal(cVO);

				map.put("mode", "list");
				cargoOpenBalList(request, map);
				// this.forwardController(request, "CargoOpenBalList");

			} else {
				if (cVO.getId() == null) {
					log.info(ControllerName + " - getOprPlan");
					GbccCargoOprPlanVO oprPlanVO = gbccServ.cc_getCargoOprPlanById(vvCd, stevCd, null);
					if (oprPlanVO != null) {
						log.info(ControllerName + " - OPR PLAN " + oprPlanVO);
						if (oprPlanVO.getId() != null) {
							log.info(ControllerName + " - OprPlan " + oprPlanVO.getCheckerNm());
							cVO.setCheckerHpNbr(oprPlanVO.getCheckerHpNbr());
							cVO.setCheckerNm(oprPlanVO.getCheckerNm());
							cVO.setWrkHatchNbr(oprPlanVO.getMaxWrkHatchNbr());
							cVO.setCtrlHatchNbr(oprPlanVO.getCtrlHatchNbr());
							cVO.setTotDiscTon(oprPlanVO.getTotalHatchDisc());
							cVO.setTotLoadTon(oprPlanVO.getTotalHatchLoad());
							cVO.setGangsNbr(oprPlanVO.getGangsNbr());

							List<GbccCargoOprPlanDet> oprPlanDetVOs = oprPlanVO.getCargoOprPlanDetVO();
							List<GbccCargoOpenBalDet> openBalDetVOs = new ArrayList<GbccCargoOpenBalDet>();

							if (oprPlanDetVOs != null) {
								for (int i = 0; i < oprPlanDetVOs.size(); i++) {
									GbccCargoOprPlanDet oprPlanDetVO = (GbccCargoOprPlanDet) oprPlanDetVOs.get(i);

									GbccCargoOpenBalDet detVO = new GbccCargoOpenBalDet();
									detVO.setId(new GbccCargoOpenBalDetId(vvCd, stevCd, Integer.valueOf(i)));
									detVO.setDiscOpenBalTon(oprPlanDetVO.getDiscTon());
									detVO.setLoadOpenBalTon(oprPlanDetVO.getLoadTon());

									openBalDetVOs.add(detVO);
								}
								cVO.setCargoOpenBalDetVO(openBalDetVOs);
							}

						}
					}

				}
			}
			map.put("cVO", cVO);
			map.put("vvCd", vvCd);
			map.put("stevCd", stevCd);
			map.put("reasonVOs", reasonVOs);

			map.put("CoyCode", coyCode);
			map.put("CoyName", coyName);

			map.put("ATBFrom", ATBFrom);
			map.put("ATBTo", ATBTo);
			map.put("lstAllChk", lstAllChk);

			map.put("request", "CargoOpenBalAddEdit");

		} catch (BusinessException e) {
			log.info("Exception CargoOpnBal : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception CargoOpnBal : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: CargoOpnBal result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	void GetRequestArrayValues(String[] inArr, String[] outArr) {
		log.info("START: GetRequestArrayValues inArr: " + inArr + " outArr: " + outArr);
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

	// delegate.helper.gbcc.cargo.cargoOpenBal-->cargoOpenBalListHandler
	@PostMapping(value = "/cargoOpenBalList")
	public ResponseEntity<?> cargoOpenBalList(HttpServletRequest request, Map<String, Object> data) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: cargoOpenBalList criteria:" + criteria.toString() + "data: " + data);

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
				mode = CommonUtility.deNull(request.getParameter("mode"));
			else
				request.removeAttribute("mode");

			// if (mode.equalsIgnoreCase("")) mode = "list";
			String command = CommonUtility.deNull(request.getParameter("command"));
			String format = CommonUtility.deNull(request.getParameter("format"));
			if (format.equalsIgnoreCase(""))
				format = "html";

			String ATBFrom = CommonUtility.deNull(request.getParameter("ATBFrom"));
			String ATBTo = CommonUtility.deNull(request.getParameter("ATBTo"));
			String lstAllChk = CommonUtility.deNull(request.getParameter("lstAllChk"));

			log.info(ControllerName + " - [Param] mode: " + mode);
			log.info(ControllerName + " - [Param] command: " + command);
			log.info(ControllerName + " - [Param] format: " + format);

			log.info(ControllerName + " - [Param] ATBFrom: " + ATBFrom);
			log.info(ControllerName + " - [Param] ATBTo: " + ATBTo);
			log.info(ControllerName + " - [Param] lstAllChk: " + lstAllChk);

			String sortby = CommonUtility.deNull(request.getParameter("sortby"));
			if (sortby.isEmpty()) {
				sortby = "br.ATB_DTTM";
			}

			String isExcel = CommonUtility.deNull(criteria.getPredicates().get("isExcel"));
			Boolean needAllData = false;
			if (!isExcel.isEmpty()) {
				needAllData = true;
			}

			log.info(ControllerName + " - [Param] sortby: " + sortby);

			List<GbccCargoOpenBalVO> lstAll = new ArrayList<GbccCargoOpenBalVO>();
			int count = 0;

			if (mode != null && mode.equalsIgnoreCase("list")) {
				log.info(ControllerName + " - findByFilter");
				if ("".equalsIgnoreCase(lstAllChk)) {
					lstAll = gbccServ.cc_getCargoOpenBal(coyCode, sortby, criteria, needAllData);
				} else {
					lstAll = gbccServ.cc_getCargoOpenBal(coyCode, sortby, criteria, needAllData, ATBFrom, ATBTo,
							lstAllChk);
				}
				if (!lstAll.isEmpty()) {
					count = (int) lstAll.get(lstAll.size() - 1).getTotal();
					lstAll.remove(lstAll.size() - 1);
				}
				log.info(ControllerName + " - Number of cargoOpenBal: " + lstAll.size());
			}

			map.put("format", format);
			map.put("CoyCode", coyCode);
			map.put("CoyName", coyName);

			map.put("sortby", sortby);
			map.put("lstAll", lstAll);

			map.put("ATBFrom", ATBFrom);
			map.put("ATBTo", ATBTo);
			map.put("lstAllChk", lstAllChk);
			map.put("total", count);
			map.put("request", "CargoOpenBalList");
			data.put("List", map);

		} catch (BusinessException e) {
			log.info("Exception cargoOpenBalList : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception cargoOpenBalList : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: cargoOpenBalList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

}
