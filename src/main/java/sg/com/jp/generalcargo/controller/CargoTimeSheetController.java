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
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetAct;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetActId;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetEqrental;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetEqrentalId;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetId;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetVO;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.StevedoreCompany;
import sg.com.jp.generalcargo.domain.VesselCall;
import sg.com.jp.generalcargo.service.CargoTimeSheetService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = CargoTimeSheetController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class CargoTimeSheetController {

	public static final String ENDPOINT = "gc/cargoOperation";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private static final Log log = LogFactory.getLog(CargoTimeSheetController.class);
	String errorMessage = null;

	@Autowired
	private CargoTimeSheetService gbccServ;

	String controllerName = "cargoTimeSheetcontroller";

	//delegate.helper.gbcc.cargo.cargoTimeSheet-->cargoTimeSheetHandler-->perform()
	@PostMapping(value = "/cargoTimeSheet")
	public ResponseEntity<?> cargoTimeSheet(HttpServletRequest request) throws BusinessException {
		Map<String, Object> map = new HashMap<String, Object>();
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: cargoTimeSheet criteria:" + criteria.toString());

			String coyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String coyName = CommonUtility.deNull(criteria.getPredicates().get("coNm"));

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

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
			String stevCd = CommonUtility.deNull(criteria.getPredicates().get("stevCd"));
			String crDttmS = CommonUtility.deNull(criteria.getPredicates().get("crDttm"));
			Date crDttm = null;
			if (!crDttmS.trim().equalsIgnoreCase(""))
				crDttm = CommonUtil.convertStrToDate(crDttmS, ConstantUtil.DATEFORMAT_INPUT_LONG);

			log.info(controllerName + " - [Param] vvCd: " + vvCd);
			log.info(controllerName + " - [Param] stevCd: " + stevCd);
			log.info(controllerName + " - [Param] crDttm: " + crDttmS);

			GbccCargoTimesheetVO cVO = null;
			log.info(controllerName + " - getById");
			cVO = gbccServ.cc_getCargoTimeSheetById(vvCd, stevCd, crDttm);
			log.info(controllerName + " - getById: " + cVO);

			List<MiscTypeCode> lstWeather = gbccServ.cc_getMiscTypeCode(ConstantUtil.MISCTYPECD_WEATHER);
			List<MiscTypeCode> lstActivity = gbccServ.cc_getMiscTypeCode(ConstantUtil.MISCTYPECD_ACTIVITY);
			List<MiscTypeCode> lstEqType = gbccServ.cc_getMiscTypeCode(ConstantUtil.MISCTYPECD_EQTYPE);

			if (command != null && command.equalsIgnoreCase("save")) {
				String checkerNm = criteria.getPredicates().get("checkerNm");
				log.info(controllerName + " - [input] checker name: " + checkerNm);
				String checkerNbr = criteria.getPredicates().get("checkerNbr");
				log.info(controllerName + " - [input] checker mobile: " + checkerNbr);

				String workCommenceDttmS = criteria.getPredicates().get("workCommenceDttm");
				log.info(controllerName + " - [input] work commencement Dttm: " + workCommenceDttmS);
				Date workCommenceDttm = CommonUtil.convertStrToDate(workCommenceDttmS, ConstantUtil.DATEFORMAT_INPUT);
				String workCompleteDttmS = criteria.getPredicates().get("workCompleteDttm");
				log.info(controllerName + " - [input] work complete Dttm: " + workCompleteDttmS);
				Date workCompleteDttm = CommonUtil.convertStrToDate(workCompleteDttmS, ConstantUtil.DATEFORMAT_INPUT);

				GbccCargoTimesheetId id = cVO.getId();
				if (id == null) {
					id = new GbccCargoTimesheetId();
					id.setVvCd(vvCd);
					cVO.setId(id);
					cVO.setStevCoCd(stevCd);
				}

				cVO.setCheckerNm(checkerNm);
				cVO.setCheckerHp(checkerNbr);
				cVO.setWorkCommenceDttm(workCommenceDttm);
				cVO.setWorkCompleteDttm(workCompleteDttm);
				cVO.setLastModifyUserId(userId);
				// get activity array
				// String[] actValues =
				// CommonUtil.getRequiredStringParameters(request,"act_seqNo");
				String sizeAct = (String) criteria.getPredicates().get("sizeAct");

				int pSize = Integer.parseInt(sizeAct);

				log.info(controllerName + "- [input] Activity Seq Array Size " + pSize);

				String[] actValues = new String[pSize];
				String[] actFromDateV = new String[pSize];
				String[] actToDateV = new String[pSize];
				String[] actHatchNoV = new String[pSize];
				String[] actWeatherV = new String[pSize];
				String[] actActivityV = new String[pSize];
				String[] actRemarkV = new String[pSize];
				String[] actUpdateModeV = new String[pSize];

				log.info(controllerName + "- [input] Activity V Seq Array Size " + actFromDateV.length);

				// String[] actFromDateP =
				// CommonUtil.getRequiredStringParameters(request,"act_fromDttm");
				// String[] actToDateP =
				// CommonUtil.getRequiredStringParameters(request,"act_toDttm");
				// String[] actHatchNoP =
				// CommonUtil.getRequiredStringParameters(request,"act_hatchNo");
				// String[] actWeatherP =
				// CommonUtil.getRequiredStringParameters(request,"act_cobWeather");
				// String[] actActivityP =
				// CommonUtil.getRequiredStringParameters(request,"act_cobActivity");
				// String[] actRemarkP =
				// CommonUtil.getRequiredStringParameters(request,"act_remarks");
				// String[] actUpdateModeP =
				// CommonUtil.getRequiredStringParameters(request,"act_updatemode");
				//
				// GetRequestArrayValues(actFromDateP,actFromDateV);
				// GetRequestArrayValues(actToDateP,actToDateV);
				// GetRequestArrayValues(actHatchNoP,actHatchNoV);
				// GetRequestArrayValues(actWeatherP,actWeatherV);
				// GetRequestArrayValues(actActivityP,actActivityV);
				// GetRequestArrayValues(actRemarkP,actRemarkV);
				// GetRequestArrayValues(actUpdateModeP,actUpdateModeV);

				for (int i = 0; i < pSize; i++) {
					actValues[i] = (String) criteria.getPredicates().get("act_seqNo" + i);
					actFromDateV[i] = (String) criteria.getPredicates().get("act_fromDttm" + i);
					actToDateV[i] = (String) criteria.getPredicates().get("act_toDttm" + i);
					actHatchNoV[i] = (String) criteria.getPredicates().get("act_hatchNo" + i);
					actWeatherV[i] = (String) criteria.getPredicates().get("act_cobWeather" + i);
					actActivityV[i] = (String) criteria.getPredicates().get("act_cobActivity" + i);
					actRemarkV[i] = (String) criteria.getPredicates().get("act_remarks" + i);
					actUpdateModeV[i] = (String) criteria.getPredicates().get("act_updatemode" + i);
					log.info(controllerName + "- [input] activity seq " + i + " : " + actValues[i]);
					log.info(controllerName + "- [input] activity from date " + i + " : " + actFromDateV[i]);
					log.info(controllerName + "- [input] activity todate" + i + " : " + actToDateV[i]);
					log.info(controllerName + "- [input] activity hatch No " + i + " : " + actHatchNoV[i]);
					log.info(controllerName + "- [input] activity weather " + i + " : " + actWeatherV[i]);
					log.info(controllerName + "- [input] activity activity " + i + " : " + actActivityV[i]);
					log.info(controllerName + "- [input] activity remark " + i + " : " + actRemarkV[i]);
					log.info(controllerName + "- [input] activity updatemode " + i + " : " + actUpdateModeV[i]);
				}

				// String[] eqValues =
				// CommonUtil.getRequiredStringParameters(request,"eq_seqNo");
				String sizeEqp = (String) criteria.getPredicates().get("sizeEqp");

				int pSize1 = Integer.parseInt(sizeEqp);

				log.info(controllerName + "- [input] Eq Seq Array Size " + pSize1);

				String[] eqValues = new String[pSize1];
				String[] eqEqTypeCdV = new String[pSize1];
				String[] eqEqDescV = new String[pSize1];
				String[] eqTonV = new String[pSize1];
				String[] eqUnitV = new String[pSize1];
				String[] eqFromDateV = new String[pSize1];
				String[] eqToDateV = new String[pSize1];
				String[] eqUpdateModeV = new String[pSize1];

				// String[] eqEqTypeCdP =
				// CommonUtil.getRequiredStringParameters(request,"eq_cobEqCd");
				// log.info(controllerName + " - [input] xxx type cd: " + eqEqTypeCdP.length);
				// for (int i=0; i<eqEqTypeCdP.length; i++) {
				// log.info(controllerName + " - [input] xxx type cd " + i + ":" +
				// eqEqTypeCdP[i]);
				// }
				// log.info(controllerName + " - [input] xxx type cd: " + eqEqTypeCdP.length);
				// String[] eqEqDescP =
				// CommonUtil.getRequiredStringParameters(request,"eq_eqDesc");
				// String[] eqTonP = CommonUtil.getRequiredStringParameters(request,"eq_eqTon");
				// String[] eqUnitP =
				// CommonUtil.getRequiredStringParameters(request,"eq_eqUnit");
				// String[] eqFromDateP =
				// CommonUtil.getRequiredStringParameters(request,"eq_fromDttm");
				// String[] eqToDateP =
				// CommonUtil.getRequiredStringParameters(request,"eq_toDttm");
				// String[] eqUpdateModeP =
				// CommonUtil.getRequiredStringParameters(request,"eq_updatemode");

				// GetRequestArrayValues(eqEqTypeCdP,eqEqTypeCdV);
				// GetRequestArrayValues(eqEqDescP,eqEqDescV);
				// GetRequestArrayValues(eqTonP,eqTonV);
				// GetRequestArrayValues(eqUnitP,eqUnitV);
				// GetRequestArrayValues(eqFromDateP,eqFromDateV);
				// GetRequestArrayValues(eqToDateP,eqToDateV);
				// GetRequestArrayValues(eqUpdateModeP,eqUpdateModeV);

				log.info(controllerName + "- [input] Eq eqtype Array Size " + eqEqTypeCdV.length);

				for (int i = 0; i < pSize1; i++) {
					eqValues[i] = (String) criteria.getPredicates().get("eq_seqNo" + i);
					eqFromDateV[i] = (String) criteria.getPredicates().get("eq_fromDttm" + i);
					eqToDateV[i] = (String) criteria.getPredicates().get("eq_toDttm" + i);
					eqEqTypeCdV[i] = (String) criteria.getPredicates().get("eq_cobEqCd" + i);
					eqEqDescV[i] = (String) criteria.getPredicates().get("eq_eqDesc" + i);
					eqTonV[i] = (String) criteria.getPredicates().get("eq_eqTon" + i);
					eqUnitV[i] = (String) criteria.getPredicates().get("eq_eqUnit" + i);
					eqUpdateModeV[i] = (String) criteria.getPredicates().get("eq_updatemode" + i);
					log.info(controllerName + "- [input] eq seq " + i + " : " + eqValues[i]);
					log.info(controllerName + "- [input] eq from date " + i + " : " + eqFromDateV[i]);
					log.info(controllerName + "- [input] eq todate " + i + " : " + eqToDateV[i]);
					log.info(controllerName + "- [input] eq eq type cd " + i + " : " + eqEqTypeCdV[i]);
					log.info(controllerName + "- [input] eq eq desc " + i + " : " + eqEqDescV[i]);
					log.info(controllerName + "- [input] eq ton " + i + " : " + eqTonV[i]);
					log.info(controllerName + "- [input] eq unit " + i + " : " + eqUnitV[i]);
					log.info(controllerName + "- [input] eq updatemode " + i + " : " + eqUpdateModeV[i]);
				}

				// process activity
				List<GbccCargoTimesheetAct> cdVOs = cVO.getCargoTimesheetActVO();
				pSize = actValues.length;
				for (int i = 0; i < pSize; i++) {
					String seqNo = CommonUtility.deNull(actValues[i]);
					String updateMode = CommonUtility.deNull(actUpdateModeV[i]);
					String fromDateS = CommonUtility.deNull(actFromDateV[i]);
					Date fromDate = null;
					if (!fromDateS.equalsIgnoreCase("")) {
						fromDate = CommonUtil.convertStrToDate(fromDateS, ConstantUtil.DATEFORMAT_INPUT);
						String toDateS = CommonUtility.deNull(actToDateV[i]);
						Date toDate = null;
						if (!toDateS.equalsIgnoreCase(""))
							toDate = CommonUtil.convertStrToDate(toDateS, ConstantUtil.DATEFORMAT_INPUT);
						String hatchNoS = CommonUtility.deNull(actHatchNoV[i]);
						Integer hatchNo = null;
						if (!hatchNoS.equalsIgnoreCase(""))
							hatchNo = Integer.valueOf(Integer.parseInt(hatchNoS)) ;

						String weatherCd = CommonUtility.deNull(actWeatherV[i]);
						String activityCd = CommonUtility.deNull(actActivityV[i]);
						String remark = CommonUtility.deNull(actRemarkV[i]);

						if (seqNo.equalsIgnoreCase("0")
								&& !updateMode.equalsIgnoreCase(ConstantUtil.OBJ_UPDATEMODE_DELETE)) {
							GbccCargoTimesheetAct dVO = new GbccCargoTimesheetAct();
							GbccCargoTimesheetActId dId = new GbccCargoTimesheetActId();
							dId.setVvCd(vvCd);

							dVO.setId(dId);
							dVO.setFromDttm(fromDate);
							dVO.setToDttm(toDate);
							dVO.setHatchNbr(hatchNo);
							dVO.setWeatherCd(weatherCd);
							dVO.setActivityCd(activityCd);
							dVO.setRemarks(remark);
							dVO.setLastModifyUserId(userId);
							cdVOs.add(dVO);
							log.info(controllerName + " - activity add new ");
						} else if (!seqNo.equalsIgnoreCase("0")) {
							Integer recSeqNo = Integer.valueOf(Integer.parseInt(seqNo));

							for (int j = 0; j < cdVOs.size(); j++) {
								GbccCargoTimesheetAct dVO = (GbccCargoTimesheetAct) cdVOs.get(j);
								if (recSeqNo.intValue() == dVO.getId().getRecSeqNbr().intValue()) {
									if (updateMode.equalsIgnoreCase(ConstantUtil.OBJ_UPDATEMODE_DELETE)) {
										dVO.setObjUpdateMode(updateMode);
										dVO.setLastModifyUserId(userId);
										log.info(controllerName + " - activity delete ");
									} else {
										dVO.setFromDttm(fromDate);
										dVO.setToDttm(toDate);
										dVO.setHatchNbr(hatchNo);
										dVO.setWeatherCd(weatherCd);
										dVO.setActivityCd(activityCd);
										dVO.setRemarks(remark);
										dVO.setLastModifyUserId(userId);
										log.info(controllerName + " - activity update ");
									}
									break;
								}
							}
						}
					}
				}
				for (int i = 0; i < cVO.getCargoTimesheetActVO().size(); i++) {
					GbccCargoTimesheetAct dVO = (GbccCargoTimesheetAct) cVO.getCargoTimesheetActVO().get(i);
					log.info(controllerName + "- [act] weather cd " + i + " : " + dVO.getWeatherCd());
					log.info(controllerName + "- [act] create dttm " + i + " : " + dVO.getId().getCreateDttm());
				}

				// process eq rental
				List<GbccCargoTimesheetEqrental> cdEqVOs = cVO.getCargoTimesheetEqRentalVO();
				pSize1 = eqValues.length;
				for (int i = 0; i < pSize1; i++) {
					String seqNo = CommonUtility.deNull(eqValues[i]);
					String updateMode = CommonUtility.deNull(eqUpdateModeV[i]);

					String fromDateS = CommonUtility.deNull(eqFromDateV[i]);
					Date fromDate = null;
					if (!fromDateS.equalsIgnoreCase("")) {
						fromDate = CommonUtil.convertStrToDate(fromDateS, ConstantUtil.DATEFORMAT_INPUT);
						String toDateS = CommonUtility.deNull(eqToDateV[i]);
						Date toDate = null;
						if (!toDateS.equalsIgnoreCase(""))
							toDate = CommonUtil.convertStrToDate(toDateS, ConstantUtil.DATEFORMAT_INPUT);

						String eqTypeCd = CommonUtility.deNull(eqEqTypeCdV[i]);
						log.info(controllerName + " - eqrental code " + i + ":" + eqTypeCd);
						String eqDesc = CommonUtility.deNull(eqEqDescV[i]);
						String eqTonS = CommonUtility.deNull(eqTonV[i]);
						Integer eqTon = null;
						if (!eqTonS.equalsIgnoreCase(""))
							eqTon = Integer.valueOf(Integer.parseInt(eqTonS));

						String eqUnitS = CommonUtility.deNull(eqUnitV[i]);
						Integer eqUnit = null;
						if (!eqUnitS.equalsIgnoreCase(""))
							eqUnit = Integer.valueOf(Integer.parseInt(eqUnitS));

						if (seqNo.equalsIgnoreCase("0")
								&& !updateMode.equalsIgnoreCase(ConstantUtil.OBJ_UPDATEMODE_DELETE)) {
							GbccCargoTimesheetEqrental dVO = new GbccCargoTimesheetEqrental();
							GbccCargoTimesheetEqrentalId dId = new GbccCargoTimesheetEqrentalId();
							dId.setVvCd(vvCd);

							dVO.setId(dId);
							dVO.setFromDttm(fromDate);
							dVO.setToDttm(toDate);
							dVO.setEqTypeCd(eqTypeCd);
							dVO.setEqTypeDesc(eqDesc);
							dVO.setEqTon(eqTon);
							dVO.setEqUnit(eqUnit);
							dVO.setLastModifyUserId(userId);
							cdEqVOs.add(dVO);
							log.info(controllerName + " - eq rental add new ");
						} else if (!seqNo.equalsIgnoreCase("0")) {
							Integer recSeqNo = Integer.valueOf(Integer.parseInt(seqNo));

							for (int j = 0; j < cdVOs.size(); j++) {
								GbccCargoTimesheetEqrental dVO = (GbccCargoTimesheetEqrental) cdEqVOs.get(j);
								if (recSeqNo.intValue() == dVO.getId().getRecSeqNbr().intValue()) {
									if (updateMode.equalsIgnoreCase(ConstantUtil.OBJ_UPDATEMODE_DELETE)) {
										dVO.setObjUpdateMode(updateMode);
										dVO.setLastModifyUserId(userId);

										log.info(controllerName + " - eqrental delete");
									} else {
										dVO.setFromDttm(fromDate);
										dVO.setToDttm(toDate);
										dVO.setEqTypeCd(eqTypeCd);
										dVO.setEqTypeDesc(eqDesc);
										dVO.setEqTon(eqTon);
										dVO.setEqUnit(eqUnit);
										dVO.setLastModifyUserId(userId);

										log.info(controllerName + " - eqrental update ");
									}
									break;
								}
							}
						}
					}
				}

				gbccServ.cc_persistCargoTimeSheet(cVO);

				map.put("mode", "list");
				map.put("request", "CargoTimeSheetList");
				CargoTimeSheetList(request, map);

			}

			map.put("lstWeather", lstWeather);
			map.put("lstActivity", lstActivity);
			map.put("lstEqType", lstEqType);

			map.put("cVO", cVO);

			map.put("vvCd", vvCd);
			map.put("stevCd", stevCd);
			map.put("crDttm", CommonUtil.formatDateToStr(crDttm, ConstantUtil.DATEFORMAT_INPUT_LONG));

			map.put("CoyCode", coyCode);
			map.put("CoyName", coyName);
			map.put("mode", mode);

			map.put("request", "CargoTimeSheetAddEdit");

		} catch (BusinessException e) {
			log.info("Exception cargoTimeSheet : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception cargoTimeSheet : ", e);
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
				log.info("END: cargoTimeSheet result: " + result.toString());
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

	//delegate.helper.gbcc.cargo.cargoTimeSheet-->cargoTimeSheetListHandler-->perform()
	@PostMapping(value = "/cargoTimeSheetList")
	public ResponseEntity<?> CargoTimeSheetList(HttpServletRequest request, Map<String, Object> map)
			throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: CargoTimeSheetList criteria:" + criteria.toString());

			// For bypassing of EJB
			// ApplicationContext ctx = new
			// ClassPathXmlApplicationContext("plgbccBeanFactory.xml");
			// GBCCService gbccServ = (GBCCService) ctx.getBean("JPGBCCService");

			// session login data

			String coyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String coyName = CommonUtility.deNull(criteria.getPredicates().get("coNm"));

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			log.info(controllerName + " - [SessionData] coyCode: " + coyCode);
			log.info(controllerName + " - [SessionData] coyName: " + coyName);
			log.info(controllerName + " - [SessionData] userId: " + userId);

			// get param
			String mode = (String) criteria.getPredicates().get("mode");
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
			String stevCd = CommonUtility.deNull(criteria.getPredicates().get("stevCd"));
			String berthNo = CommonUtility.deNull(criteria.getPredicates().get("berthNo"));
			String sortby = "";

			String isExcel = CommonUtility.deNull(criteria.getPredicates().get("isExcel"));
			Boolean needAllData = false;
			if (!isExcel.isEmpty()) {
				needAllData = true;
			}

			log.info(controllerName + " - [Param] vvCd: " + vvCd);
			log.info(controllerName + " - [Param] stevCoCd: " + stevCd);
			log.info(controllerName + " - [Param] berthNo: " + berthNo);
			log.info(controllerName + " - [Param] sortBy: " + sortby);

			List<GbccCargoTimesheetVO> lstAll = new ArrayList<GbccCargoTimesheetVO>();
			List<VesselCall> lstVV = gbccServ.cc_getCargoVesselCall(coyCode);
			List<String> lstBerth = gbccServ.cc_getCargoBerth(coyCode);
			List<StevedoreCompany> lstStev = null;

			if ("".equalsIgnoreCase(coyCode) || "JP".equalsIgnoreCase(coyCode))
				lstStev = gbccServ.cc_getCargoStevedore();

			int count = 0;
			if (mode != null && mode.equalsIgnoreCase("list")) {
				log.info(controllerName + " - [getCargoTallySheet] ");
				lstAll = gbccServ.cc_getCargoTimeSheet(coyCode, vvCd, stevCd, berthNo, sortby, criteria, needAllData);
				if (!lstAll.isEmpty()) {
					count = lstAll.get(lstAll.size() - 1).getTotal();
					lstAll.remove(lstAll.size() - 1);
				}
				log.info(controllerName + " - [getCargoTimeSheet] Size of result: " + lstAll.size());
			}

			map.put("CoyCode", coyCode);
			map.put("CoyName", coyName);

			map.put("sortby", sortby);
			map.put("lstAll", lstAll);
			map.put("lstVV", lstVV);
			map.put("lstBerth", lstBerth);
			map.put("lstStev", lstStev);

			map.put("vvCd", vvCd);
			map.put("berthNo", berthNo);
			map.put("stevCd", stevCd);
			map.put("format", format);
			map.put("total", count);
			map.put("request", "CargoTimeSheetList");

		} catch (BusinessException e) {
			log.info("Exception CargoTimeSheetList : ", e);
			errorMessage = ConstantUtil.CARGOOPR_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception CargoTimeSheetList : ", e);
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
				log.info("END: CargoTimeSheetList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

}
