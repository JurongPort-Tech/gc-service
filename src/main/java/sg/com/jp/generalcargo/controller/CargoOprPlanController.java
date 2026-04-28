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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanDet;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanDetId;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanId;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanVO;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.CargoOprPlanService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = CargoOprPlanController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class CargoOprPlanController {
	public static final String ENDPOINT = "gc/cargoOperation";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private static final Log log = LogFactory.getLog(CargoOprPlanController.class);
	String errorMessage = null;
	String ControllerName = "cargoOprPlanController";

	@Autowired
	private CargoOprPlanService cargoOprPlanServ;

	// delegate.helper.gbcc.cargo.cargoOprPlan-->cargoOprPlanController
	@PostMapping(value = "/cargoOprPlan")
	public ResponseEntity<?> cargoOpr(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: cargoOprPlan criteria:" + criteria.toString());
			String coyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String coyName = CommonUtility.deNull(criteria.getPredicates().get("coNm"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			log.info(ControllerName + " - [SessionData] coyCode: " + coyCode);
			log.info(ControllerName + " - [SessionData] coyName: " + coyName);
			log.info(ControllerName + " - [SessionData] userId: " + userId);

			// get param
			String mode = (String) CommonUtility.deNull(criteria.getPredicates().get("mode"));
			log.info(ControllerName + " - [Param] session mode: " + mode);

			if (mode == null)
				mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			else
				request.removeAttribute("mode");
			if (mode.equalsIgnoreCase(""))
				mode = "edit";
			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));

			log.info(ControllerName + " - [Param] mode: " + mode);
			log.info(ControllerName + " - [Param] command: " + command);

			String ETBFrom = CommonUtility.deNull(criteria.getPredicates().get("ETBFrom"));
			String ETBTo = CommonUtility.deNull(criteria.getPredicates().get("ETBTo"));
			String lstAllChk = CommonUtility.deNull(criteria.getPredicates().get("lstAllChk"));

			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String stevCd = CommonUtility.deNull(criteria.getPredicates().get("stevCd"));
			String crDttmS = CommonUtility.deNull(criteria.getPredicates().get("crDttm"));

			log.info(ControllerName + " - [Param] ETBFrom: " + ETBFrom);
			log.info(ControllerName + " - [Param] ETBTo: " + ETBTo);
			log.info(ControllerName + " - [Param] lstAllChk: " + lstAllChk);

			Date crDttm = null;
			if (!crDttmS.trim().equalsIgnoreCase(""))
				crDttm = CommonUtil.convertStrToDate(crDttmS, ConstantUtil.DATEFORMAT_INPUT_LONG);

			log.info(ControllerName + " - [Param] vvCd: " + vvCd);
			log.info(ControllerName + " - [Param] stevCd: " + stevCd);
			log.info(ControllerName + " - [Param] crDttm: " + crDttm);

			GbccCargoOprPlanVO cVO = null;
			// List vesselInfoList = new ArrayList();

			log.info("cargoOprPlanController - check mode " + mode);

			log.info("cargoOprPlanController - findById");
			cVO = cargoOprPlanServ.cc_getCargoOprPlanById(vvCd, stevCd, crDttm);
			log.info("findBy Id: " + cVO);

			if (command != null && command.equalsIgnoreCase("save")) {
				String checkerNm = CommonUtility.deNull(criteria.getPredicates().get("checkerNm"));
				log.info("cargoOprPlanController - check checker name: " + checkerNm);
				String checkerNbr = CommonUtility.deNull(criteria.getPredicates().get("checkerNbr"));
				log.info("cargoOprPlanController - check checker mobile: " + checkerNbr);
				String totalDisc = CommonUtility.deNull(criteria.getPredicates().get("totalDisc"));
				log.info("cargoOprPlanController - check total discharge: " + totalDisc);
				String totalLoad = CommonUtility.deNull(criteria.getPredicates().get("totalLoad"));
				log.info("cargoOprPlanController - check total load: " + totalLoad);

				// String hatchDisc =
				// CommonUtility.deNull(criteria.getPredicates().get("hatchDisc");
				// log.info("cargoOprPlanController - check discharge: " + hatchDisc);
				// String hatchLoad =
				// CommonUtility.deNull(criteria.getPredicates().get("hatchLoad");
				// log.info("cargoOprPlanController - check load: " + hatchLoad);

				String craneUnit = CommonUtility.deNull(criteria.getPredicates().get("craneUnit"));
				log.info("cargoOprPlanController - check crane unit: " + craneUnit);
				String craneTon = CommonUtility.deNull(criteria.getPredicates().get("craneTon"));
				log.info("cargoOprPlanController - check crane ton: " + craneTon);
				String mobileUnit = CommonUtility.deNull(criteria.getPredicates().get("mobileUnit"));
				log.info("cargoOprPlanController - check mobile unit: " + mobileUnit);
				String mobileTon = CommonUtility.deNull(criteria.getPredicates().get("mobileTon"));
				log.info("cargoOprPlanController - check mobile ton: " + mobileTon);
				String floatingUnit = CommonUtility.deNull(criteria.getPredicates().get("floatingUnit"));
				log.info("cargoOprPlanController - check floating unit: " + floatingUnit);
				String floatingTon = CommonUtility.deNull(criteria.getPredicates().get("floatingTon"));
				log.info("cargoOprPlanController - check floating ton: " + floatingTon);
				String heavyliftUnit = CommonUtility.deNull(criteria.getPredicates().get("heavyliftUnit"));
				log.info("cargoOprPlanController - check floating unit: " + heavyliftUnit);
				String heavyliftTon = CommonUtility.deNull(criteria.getPredicates().get("heavyliftTon"));
				log.info("cargoOprPlanController - check floating ton: " + heavyliftTon);
				String tandemLift = CommonUtility.deNull(criteria.getPredicates().get("tandemLift"));
				log.info("cargoOprPlanController - check tandem lift: " + tandemLift);
				String noOfGangs = CommonUtility.deNull(criteria.getPredicates().get("noOfGangs"));
				log.info("cargoOprPlanController - check noOfGangs: " + noOfGangs);
				String maxHatch = CommonUtility.deNull(criteria.getPredicates().get("maxHatch"));
				log.info("cargoOprPlanController - check maxHatch: " + maxHatch);
				String controlHatch = CommonUtility.deNull(criteria.getPredicates().get("controlHatch"));
				log.info("cargoOprPlanController - check controlHatch: " + controlHatch);
				String remark = CommonUtility.deNull(criteria.getPredicates().get("remark"));
				log.info("cargoOprPlanController - check remark: " + remark);

				GbccCargoOprPlanId id = cVO.getId();
				if (id == null) {
					id = new GbccCargoOprPlanId();
					id.setVvCd(vvCd);
					cVO.setId(id);
					cVO.setStevCoCd(stevCd);
				}

				cVO.setCheckerNm(checkerNm);
				cVO.setCheckerHpNbr(checkerNbr);
				cVO.setTotDiscTon(new Integer(totalDisc));
				cVO.setTotLoadTon(new Integer(totalLoad));
				cVO.setCraneOnboardNbr(new Integer(craneUnit));
				cVO.setCraneOnboardTon(new Integer(craneTon));
				cVO.setMobileCraneNbr(new Integer(mobileUnit));
				cVO.setMobileCraneTon(new Integer(mobileTon));

				cVO.setFloatingCraneNbr(new Integer(floatingUnit));
				cVO.setFloatingCraneTon(new Integer(floatingTon));
				cVO.setHeavyLiftNbr(new Integer(heavyliftUnit));
				cVO.setHeavyLiftTon(new Integer(heavyliftTon));

				cVO.setTandemLiftInd(tandemLift);
				cVO.setGangsNbr(new Integer(noOfGangs));
				cVO.setMaxWrkHatchNbr(new Integer(maxHatch));
				cVO.setCtrlHatchNbr(new Integer(controlHatch));
				cVO.setRemarks(remark);
				cVO.setLastModifyUserId(userId);

				// String[] hatchDiscValues = CommonUtil.getRequiredStringParameters(request,
				// "hatchDisc");
				// String[] hatchLoadValues = CommonUtil.getRequiredStringParameters(request,
				// "hatchLoad");

				String[] hatchDiscValues = new String[9];
				String[] hatchLoadValues = new String[9];

				for (int i = 0; i < 9; i++) {
					hatchDiscValues[i] = (String) criteria.getPredicates().get("hatchDisc" + i);
					hatchLoadValues[i] = (String) criteria.getPredicates().get("hatchLoad" + i);
					log.info("cargoOprPlanController - check Disc " + i + ": " + hatchDiscValues[i]);
					log.info("cargoOprPlanController - check Load " + i + ": " + hatchLoadValues[i]);
				}

				int pSize = hatchDiscValues.length;

				// for (int i = 0; i < pSize; i++) {
				// log.info("cargoOprPlanController - check Disc " + i + ": " +
				// hatchDiscValues[i]);
				// log.info("cargoOprPlanController - check Load " + i + ": " +
				// hatchLoadValues[i]);
				// }

				int cTotalLoad = 0;
				int cTotalDisc = 0;
				List<GbccCargoOprPlanDet> cdVOs = cVO.getCargoOprPlanDetVO();
				if (cdVOs == null) {
					cdVOs = new ArrayList<GbccCargoOprPlanDet>();
					for (int i = 0; i < pSize; i++) {
						String hatchDisc = hatchDiscValues[i];
						String hatchLoad = hatchLoadValues[i];

						Integer hDisc = new Integer(0);
						Integer hLoad = new Integer(0);

						if (hatchDisc != null) {
							hDisc = new Integer(hatchDisc);
						}
						if (hatchLoad != null) {
							hLoad = new Integer(hatchLoad);
						}

						GbccCargoOprPlanDet cdVO = new GbccCargoOprPlanDet();
						GbccCargoOprPlanDetId cdVOId = new GbccCargoOprPlanDetId();
						cdVOId.setVvCd(vvCd);
						cdVOId.setCreateDttm(cVO.getId().getCreateDttm());
						cdVOId.setHatchNbr(new Integer(i));
						cdVO.setId(cdVOId);
						cdVO.setDiscTon(hDisc);
						cdVO.setLastModifyUserId(userId);
						cdVO.setLoadTon(hLoad);
						cdVOs.add(cdVO);

						cTotalLoad += hLoad.intValue();
						cTotalDisc += hDisc.intValue();
					}
				} else {
					int lstSize = cdVOs.size();
					log.info(ControllerName + " - Det " + lstSize);

					for (int i = 0; i < lstSize; i++) {
						String hatchDisc = hatchDiscValues[i];
						String hatchLoad = hatchLoadValues[i];

						Integer hDisc = new Integer(0);
						Integer hLoad = new Integer(0);

						if (hatchDisc != null) {
							hDisc = new Integer(hatchDisc);
						}
						if (hatchLoad != null) {
							hLoad = new Integer(hatchLoad);
						}

						GbccCargoOprPlanDet cdVO = (GbccCargoOprPlanDet) cdVOs.get(i);
						// GbccCargoOprPlanDetId cdVOId = new GbccCargoOprPlanDetId();
						// cdVOId.setVvCd(vvCd);
						// cdVOId.setCreateDttm(cVO.getId().getCreateDttm());
						// cdVOs.setId(cdVOId);
						cdVO.setDiscTon(hDisc);
						cdVO.setLastModifyUserId(userId);
						cdVO.setLoadTon(hLoad);

						cTotalLoad += hLoad.intValue();
						cTotalDisc += hDisc.intValue();
					}
				}

				cVO.setTotDiscTon(new Integer(cTotalDisc));
				cVO.setTotLoadTon(new Integer(cTotalLoad));

				log.info(ControllerName + " - Disc Total " + cTotalDisc);
				log.info(ControllerName + " - Load Total " + cTotalLoad);

				cVO.setCargoOprPlanDetVO(cdVOs);
				cargoOprPlanServ.cc_persistCargoOprPlan(cVO);

				map.put("mode", "list");

				// return;
			}

			map.put("cVO", cVO);
			map.put("vvCd", vvCd);
			map.put("stevCd", stevCd);
			map.put("crDttm", CommonUtil.formatDateToStr(crDttm, ConstantUtil.DATEFORMAT_INPUT_LONG));

			map.put("CoyCode", coyCode);
			map.put("CoyName", coyName);

			map.put("ETBFrom", ETBFrom);
			map.put("ETBTo", ETBTo);
			map.put("lstAllChk", lstAllChk);

			map.put("request", "CargoOprPlanAddEdit");
		} catch (BusinessException e) {
			log.info("Exception cargoOprPlan : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception cargoOprPlan : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: cargoOprPlan result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/cargoOps")
	public ResponseEntity<?> cargoOps(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: cargoOps criteria:" + criteria.toString());

			String coyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String coyName = CommonUtility.deNull(criteria.getPredicates().get("coNm"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			log.info(ControllerName + " - [SessionData] coyCode: " + coyCode);
			log.info(ControllerName + " - [SessionData] coyName: " + coyName);
			log.info(ControllerName + " - [SessionData] userId: " + userId);

			// get param
			String mode = (String) CommonUtility.deNull(criteria.getPredicates().get("mode"));
			log.info(ControllerName + " - [Param] session mode: " + mode);
			if (mode == null)
				mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			else
				map.remove("mode");

			// if (mode.equalsIgnoreCase("")) mode = "list";
			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));
			String format = CommonUtility.deNull(criteria.getPredicates().get("format"));
			if (format.equalsIgnoreCase(""))
				format = "html";

			String ETBFrom = CommonUtility.deNull(criteria.getPredicates().get("ETBFrom"));
			String ETBTo = CommonUtility.deNull(criteria.getPredicates().get("ETBTo"));
			String lstAllChk = CommonUtility.deNull(criteria.getPredicates().get("lstAllChk"));

			log.info(ControllerName + " - [Param] mode: " + mode);
			log.info(ControllerName + " - [Param] command: " + command);
			log.info(ControllerName + " - [Param] format: " + format);

			log.info(ControllerName + " - [Param] ETBFrom: " + ETBFrom);
			log.info(ControllerName + " - [Param] ETBTo: " + ETBTo);
			log.info(ControllerName + " - [Param] lstAllChk: " + lstAllChk);

			String sortby = CommonUtility.deNull(criteria.getPredicates().get("sortby"));
			if (sortby == "")
				sortby = "br.ETB_DTTM";

			String isExcel = CommonUtility.deNull(criteria.getPredicates().get("isExcel"));
			Boolean needAllData = false;
			if (!isExcel.isEmpty()) {
				needAllData = true;
			}

			log.info(ControllerName + " - [Param] sortby: " + sortby);

			TableResult lstAll = new TableResult();

			if (mode != null && mode.equalsIgnoreCase("list")) {
				log.info(ControllerName + " - findByFilter");
				if ("".equalsIgnoreCase(lstAllChk)) {
					lstAll = cargoOprPlanServ.cc_getCargoOprPlan(coyCode, sortby, criteria, needAllData);
				} else {
					lstAll = cargoOprPlanServ.cc_getCargoOprPlan(coyCode, sortby, criteria, needAllData, ETBFrom, ETBTo,
							lstAllChk);
				}
				log.info(ControllerName + " - Number of cargoOpr: " + lstAll.getData().getTotal());
				map.put("lstAll", lstAll.getData().getListData().getTopsModel());
				map.put("total", lstAll.getData().getTotal());
			}

			map.put("format", format);
			map.put("CoyCode", coyCode);
			map.put("CoyName", coyName);
			map.put("sortby", sortby);

			map.put("ETBFrom", ETBFrom);
			map.put("ETBTo", ETBTo);
			map.put("lstAllChk", lstAllChk);

			map.put("request", "CargoOprPlanList");

		} catch (BusinessException e) {
			log.info("Exception cargoOps : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception cargoOps : ", e);
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
			}
			log.info("END: cargoOps result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@RequestMapping(value = "/getCompanyName", method = RequestMethod.POST)
	public ResponseEntity<?> getCompanyName(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START:getCompanyName");
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getCompanyName Start criteria "+" criteria:"+criteria.toString() );
			String userAcct = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String companyName = cargoOprPlanServ.getCompanyName(companyCode);
			map.put("userAcct", userAcct);
			map.put("companyName", companyName);
			map.put("companyCode", companyCode);
			result.setSuccess(true);
			result.setData(map);
		} catch (BusinessException e) {
			log.info("Exception getCompanyName : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception getCompanyName : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
		}
		log.info("END: getCompanyName result: " + result.toString());
		return ResponseEntityUtil.success(result.toString());
	}

}
