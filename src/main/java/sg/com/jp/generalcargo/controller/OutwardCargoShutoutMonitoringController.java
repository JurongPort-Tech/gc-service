package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import sg.com.jp.generalcargo.domain.DPECargo;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.EdoJpBilling;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.ShutOutCargoVo;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.OutwardShutoutCargoService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = OutwardCargoShutoutMonitoringController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OutwardCargoShutoutMonitoringController {
	public static final String ENDPOINT = "gc/outwardcargo/shutout";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(OutwardCargoShutoutMonitoringController.class);

	@Autowired
	private OutwardShutoutCargoService shutoutCargoService;

	// Region ShuntoutCargo Monitoring
	// ShuntoutCargoHandler

	// sg.com.jp.dpe.action-->DPEUtilAction-->lookupRef
	// vessel auto complete service
	@PostMapping(value = "/lookupRef")
	public ResponseEntity<?> lookupRef(HttpServletRequest request) {
		List<DPEUtil> recs = null;
		Result result = new Result();
		int total = 0;
		String name = "";
		String voy_nbr = "";
		String ind = "";
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: lookupRef :: criteria : " + criteria.toString());
			String type = CommonUtility.deNull(criteria.getPredicates().get("type")).trim();
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			if (StringUtils.equalsIgnoreCase("vessel4Monitoring", type)) {
				name = CommonUtility.deNull(criteria.getPredicates().get("name")).trim();
				log.info("DPEUtilAction :: name : " + name);
				recs = shutoutCargoService.listVesselByNameForMonitoring(name, coCd);
				total = shutoutCargoService.countVesselByNameForMonitoring(name, coCd);
			} else if (StringUtils.equalsIgnoreCase("outVoy4Transfer", type)) {
				name = CommonUtility.deNull(criteria.getPredicates().get("vsl_nm")).trim();
				voy_nbr = CommonUtility.deNull(criteria.getPredicates().get("out_voy_nbr")).trim();
				ind = CommonUtility.deNull(criteria.getPredicates().get("ind"));
				log.info("DPEUtilAction :: name : " + name);
				log.info("DPEUtilAction :: out_voy_nbr : " + voy_nbr);
				recs = shutoutCargoService.getOutVoyageList4Transfer(name, coCd, voy_nbr, ind);
			}

		} catch (BusinessException be) {
			log.info("Exception lookupRef: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception lookupRef : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setSuccess(false);
				result.setError(errorMessage);
				log.info("END: lookupRef result: " + result.toString());
				return ResponseEntityUtil.success(result.toString());
			}
		}
		return searchResponse(recs, total);
	}

	private ResponseEntity<?> searchResponse(List<DPEUtil> list, int total) {
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			log.info("START: searchResponse list:" + list.toString() + "total:" + total);
			data.put("data", list);
			response.put("data", data);
			response.put("total", list.size());
			log.info("list" + list.toString());			
		} catch (Exception e) {
			log.info("Exception searchResponse : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			log.info("END: searchResponse");
		}
		return ResponseEntityUtil.ok(response);
	}

	// End Autocomplete

	// List Service
	// sg.com.jp.dpe.action-->DPEUtilAction-->enquiryGeneralShutoutCargo
	@PostMapping(value = "/enquiryGeneralShutoutCargo")
	public ResponseEntity<?> enquiryGeneralShutoutCargo(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> filters = new HashMap<String, Object>();		
		TableResult result = null;
		try {
			log.info("DPEUtilAction :: enquiryGeneralShutoutCargo : START criteria : " + criteria.toString());
			String type = CommonUtility.deNull(criteria.getPredicates().get("type")).trim();

			Integer limit = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("limit"))).equals("") ? "50" : "50"));
			Integer start = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("start"))).equals("") ? "0" : "0"));

			String sort = ((CommonUtility.deNull(criteria.getPredicates().get("sort"))).equals("sort") ? "sort"
					: "esn_asn_nbr").trim();

			// String sort = CommonUtility.deNull(criteria.getPredicates().get( "sort",
			// "esn_asn_nbr").trim();

			String dir = ((CommonUtility.deNull(criteria.getPredicates().get("sort"))).equals("sort") ? "sort" : "ASC")
					.trim();

			String esn_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("esn_asn_nbr"));

			// String dir = CommonUtility.deNull(criteria.getPredicates().get( "dir",
			// "asc").trim();

			String companyId = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			log.info("enquiryGeneralShutoutCargo :: companyId : " + companyId);

			// BeanUtils.populate(filters, request.getParameterMap());
			Enumeration<String> ee = request.getParameterNames();
			while (ee.hasMoreElements()) {
				String key = (String) ee.nextElement();
				String value = request.getParameter(key);
				log.info("DPEUtilAction :: enquiryGeneralShutoutCargo :Enumeration : " + key + "/" + value);
				filters.put(key, value);
			}
			filters.put("f_companyId", companyId);
			if (log.isInfoEnabled()) {
				log.info("ReqMap : " + request.getParameterMap());
				log.info("Filters: " + filters);
			}

			if (StringUtils.equalsIgnoreCase("general_cargo", type)) {
				result = shutoutCargoService.listGeneralShutoutCargo(start, limit, sort, dir, filters, criteria);
				// total = shutoutCargoService.countGeneralShutoutCargo(filters);
			} else if (StringUtils.equalsIgnoreCase("shutout_cargo", type)) {
				result = shutoutCargoService.listShutoutCargoEDO(esn_asn_nbr, sort, dir, criteria);
			} else if (StringUtils.equalsIgnoreCase("transfer_cargo", type)) {
				result = shutoutCargoService.listTransferCargo(esn_asn_nbr, sort, dir, criteria);
			}

		} catch (BusinessException be) {
			log.info("Exception enquiryGeneralShutoutCargo: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception enquiryGeneralShutoutCargo : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			log.info("END: enquiryGeneralShutoutCargo result"+ result.toString());
		}
		return ResponseEntityUtil.success(result);
	}

	// End list
	// delegate.helper.gbms.cargo.generalcargo-->perform
	// ShutoutCargoHandler
	@PostMapping(value = "/getShutoutCargoList")
	public ResponseEntity<?> getShutoutCargoReport(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		Criteria criteria = CommonUtil.getCriteria(request);
		errorMessage = null;
		List<ShutOutCargoVo> list = new ArrayList<>();
		try {
			log.info("START:ShutoutCargoList criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String dateTime = CommonUtility.formatDateToStr(new Date(), "ddMMyyyy HHmm");
			String reqPage = CommonUtility.deNull(criteria.getPredicates().get("command"));

			// START update changes - NS May 2023
			String userName = shutoutCargoService.getUserNameMap(userId);
			// END update changes - NS May 2023
			
			log.info("Request Page : " + reqPage);
			if ("Top".equalsIgnoreCase(reqPage)) {

				CommonUtility.deNull(criteria.getPredicates().get("bkgRefNbr"));
				String dateFrom = CommonUtility.deNull(criteria.getPredicates().get("dateFrom"));
				String dateTo = CommonUtility.deNull(criteria.getPredicates().get("dateTo"));
				String vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
				String outVoyNbr = CommonUtility.deNull(criteria.getPredicates().get("outVoy"));
				String esnAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("esnEdo"));
				// Dpe Tungnm3 Start
//				String cargoType = request.getParameter("cargoType");
				String status = CommonUtility.deNull(criteria.getPredicates().get("status"));
				String terminal = CommonUtility.deNull(criteria.getPredicates().get("terminal"));
				String dwellDays = CommonUtility.deNull(criteria.getPredicates().get("dwellDays"));
				// Dpe Tungnm3 end

				map.put("dateFrom", dateFrom);
				map.put("dateTo", dateTo);
				map.put("vslName", vslName);
				map.put("outVoy", outVoyNbr);
				map.put("esnEdo", esnAsnNbr);
				map.put("status", status);
				map.put("terminal", terminal);
				map.put("dwellDays", dwellDays);
				// Dpe Tungnm3 Start
//				request.getSession().setAttribute("cargoType", cargoType);
				list = shutoutCargoService.getShutoutCargoMtgList(dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr,
						status, terminal, dwellDays);
				// removed pagnation
//				setRecordPaging(request, dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr, status,terminal,dwellDays);
//				list = shoutoutCargo.getShutoutCargoMtrgList(dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr, cargoType);
//				setRecordPaging(request, shoutoutCargo, dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr, cargoType);
				// Dpe Tungnm3 End
			}

			else if ("updateDeliveryStatus".equalsIgnoreCase(reqPage)) {
				String dateFrom = CommonUtility.deNull(criteria.getPredicates().get("dateFrom"));
				String dateTo = CommonUtility.deNull(criteria.getPredicates().get("dateTo"));
				String vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
				String outVoyNbr = CommonUtility.deNull(criteria.getPredicates().get("outVoy"));
				String esnAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("esnEdo"));
				String status = CommonUtility.deNull(criteria.getPredicates().get("status"));
				String terminal = CommonUtility.deNull(criteria.getPredicates().get("terminal"));
				String dwellDays = CommonUtility.deNull(criteria.getPredicates().get("dwellDays"));
				// String cargoType = CommonUtility.deNull(criteria.getPredicates().get("cargoType"));
				String bkgRefNbr = CommonUtility.deNull(criteria.getPredicates().get("bkgRefNbr"));
				String deliveredPackages = CommonUtility.deNull(criteria.getPredicates().get("deliveredPackages"));
				String deliveryRemarks = CommonUtility.deNull(criteria.getPredicates().get("deliveryRemarks"));
				int recordsUpdated = shutoutCargoService.updateDeliveryStatus(bkgRefNbr, deliveredPackages,
						deliveryRemarks, userId,userName, dateTime, status);
				log.info("No. of Records updated : " + recordsUpdated);
				list = shutoutCargoService.getShutoutCargoMtgList(dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr,
						status, terminal, dwellDays);
				// removed pagnation
				// setRecordPaging(request, dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr,
				// status,terminal, dwellDays);
			}

			else if ("downloadShutoutList".equalsIgnoreCase(reqPage)) {
				String dateFrom = CommonUtility.deNull(criteria.getPredicates().get("dateFrom"));
				String dateTo = CommonUtility.deNull(criteria.getPredicates().get("dateTo"));
				String vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
				String outVoyNbr = CommonUtility.deNull(criteria.getPredicates().get("outVoy"));
				String esnAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("esnEdo"));
				// String cargoType = CommonUtility.deNull(criteria.getPredicates().get("cargoType"));
				String status = CommonUtility.deNull(criteria.getPredicates().get("status"));
				String terminal = CommonUtility.deNull(criteria.getPredicates().get("terminal"));
				String dwellDays = CommonUtility.deNull(criteria.getPredicates().get("dwellDays"));
				list = shutoutCargoService.getShutoutCargoMtgList(dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr,
						status, terminal, dwellDays);
				map.put("ShutoutList", list);
			}

//			else if ("NextPage".equalsIgnoreCase(reqPage)) {
//				String dateFrom = CommonUtility.deNull(criteria.getPredicates().get("dateFrom"));
//				String dateTo = CommonUtility.deNull(criteria.getPredicates().get("dateTo"));
//				String vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
//				String outVoyNbr = CommonUtility.deNull(criteria.getPredicates().get("outVoy"));
//				String esnAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("esnEdo"));
//				String cargoType = CommonUtility.deNull(criteria.getPredicates().get("cargoType"));
//				String status = CommonUtility.deNull(criteria.getPredicates().get("status"));
//				String terminal = CommonUtility.deNull(criteria.getPredicates().get("terminal"));
//				String dwellDays = CommonUtility.deNull(criteria.getPredicates().get("dwellDays"));
//				// removed pagnation
//				// setRecordPaging(request, dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr,
//				// status,terminal,dwellDays);
//			}


		} catch (BusinessException be) {
			log.info("Exception getShutoutCargoReport: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception getShutoutCargoReport : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: getShutoutCargoReport result" + result.toString());

		}
		return ResponseEntityUtil.success(result.toString());
	}

	// Region start update shutout cargo

	// sg.com.jp.dpe.action-->DPEUtilAction-->shutoutCargo
	@PostMapping(value = "/shutoutCargo")
	public ResponseEntity<?> shutoutCargo(HttpServletRequest request) throws Exception {
		Result result = new Result();
		errorMessage = null;
		boolean res = false;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("DPEUtilAction :: shutoutCargo : START criteria:" + criteria.toString());
			String new_shutout_pkgs = CommonUtility.deNull(criteria.getPredicates().get("new_shutout_pkgs").trim());
			String ua_nbr_pkgs = CommonUtility.deNull(criteria.getPredicates().get("ua_nbr_pkgs").trim());
			String bk_ref_nbr = CommonUtility.deNull(criteria.getPredicates().get("bk_ref_nbr").trim());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			if (!StringUtils.isBlank(new_shutout_pkgs) && !StringUtils.isBlank(ua_nbr_pkgs)) {
				int result1 = shutoutCargoService.updateShutOutPkgBkDetail(new_shutout_pkgs + "", userId, bk_ref_nbr);
				if (result1 != 0) {
					res = true;
				} 
			}

		} catch (BusinessException be) {
			log.info("Exception shutoutCargo: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception shutoutCargo : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");

		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setSuccess(res);
			}
			log.info("DPEUtilAction :: shutoutCargo : END  result:" + result.toString());
		}
		return ResponseEntityUtil.success(result);
	}

	// end region update

	// start region add transfer to vessel
	@PostMapping(value = "/loadTransferCargo")
	public ResponseEntity<?> loadTransferCargo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("loadTransferCargo : : START criteria:" + criteria.toString());

			String esn_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("esn_asn_nbr").trim());
			DPECargo dpeCargo = shutoutCargoService.loadTransferCargo(esn_asn_nbr);

			int nbr_pkgs = 0;
			String first_trans_dttm = "";
			if (dpeCargo != null) {
				if (StringUtils.equalsIgnoreCase("E", dpeCargo.getTrans_type())) {
					nbr_pkgs = dpeCargo.getUa_nbr_pkgs();
					first_trans_dttm = dpeCargo.getEsn_first_trans_dttm();
				} else if (StringUtils.equalsIgnoreCase("A", dpeCargo.getTrans_type())) {
					nbr_pkgs = dpeCargo.getTesnjj_nbr_pkgs();
					first_trans_dttm = dpeCargo.getTesnjj_first_trans_dttm();
				} else if (StringUtils.equalsIgnoreCase("C", dpeCargo.getTrans_type())) {
					nbr_pkgs = dpeCargo.getTesnpj_nbr_pkgs();
					first_trans_dttm = dpeCargo.getTesnpj_first_trans_dttm();
				}
			} else {
				dpeCargo = new DPECargo();
			}

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			dpeCargo.setNbr_pkgs(nbr_pkgs);
			dpeCargo.setFirst_trans_dttm(first_trans_dttm);
			map.put("data", dpeCargo);
			map.put("isJp", coCd.equalsIgnoreCase("JP") ? true : false);


		} catch (BusinessException be) {
			log.info("Exception loadTransferCargo: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception loadTransferCargo : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setSuccess(true);
				result.setData(map);
			}
			log.info("DPEUtilAction :: loadTransferCargo : END  result:" + result.toString());
		}
		return ResponseEntityUtil.success(result);

	}

	// start Region vessel list
	@PostMapping(value = "/getVesselListForAddTransferOfCargo")
	public ResponseEntity<?> getVesselListForAddTransferOfCargo(HttpServletRequest request) {
		errorMessage = null;
		List<DPEUtil> recs = null;
		Result result = new Result();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("getVesselListForAddTransferOfCargo : : START criteria:" + criteria.toString());
			String name = "";
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode").trim());
			String type = CommonUtility.deNull(criteria.getPredicates().get("type").trim());
			
			if (StringUtils.equalsIgnoreCase("vessel", type)) {
				name = CommonUtility.deNull(criteria.getPredicates().get("name").trim());
				log.info("DPEUtilAction :: name : " + name);
				recs = shutoutCargoService.listVesselForAddTransferCargo(0, 0, name, coCd);
				log.info("recs:" + recs.toString());
			}

		} catch (BusinessException be) {
			log.info("Exception getVesselListForAddTransferOfCargo: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception getVesselListForAddTransferOfCargo : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setSuccess(true);
			}
			log.info("END: getVesselListForAddTransferOfCargo result:" + result.toString());
		}
		return searchResponse(recs, 0);
	}

	@PostMapping(value = "/createTransferCargo")
	public ResponseEntity<?> createTransferCargo(HttpServletRequest request) {
		errorMessage = null;
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("createTransferCargo : : START criteria:" + criteria.toString());
			String bk_ref_nbr = CommonUtility.deNull(criteria.getPredicates().get("t_bk_ref_nbr").trim());
			String new_bk_ref_nbr = CommonUtility.deNull(criteria.getPredicates().get("new_bk_ref_no").trim());
			String esn_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("t_esn_asn_nbr").trim());
			String trans_type = CommonUtility.deNull(criteria.getPredicates().get("t_trans_type").trim());
			String transfer_pkgs = CommonUtility.deNull(criteria.getPredicates().get("t_transfer_pkgs").trim());
			String shutout_pkgs = CommonUtility.deNull(criteria.getPredicates().get("t_shutout_pkgs").trim());

			// String actual_nbr_shipped =
			// CommonUtility.deNull(criteria.getPredicates().get(
			// "t_actual_nbr_shipped").trim();
			String actual_nbr_shipped = CommonUtility.deNull(criteria.getPredicates().get("t_shutout_pkgs").trim());

			String ua_nbr_pkg = CommonUtility.deNull(criteria.getPredicates().get("t_ua_nbr_pkgs").trim());
			String first_trans_dttm = CommonUtility.deNull(criteria.getPredicates().get("first_trans_dttm").trim());

			// String outvoynbr = CommonUtility.deNull(criteria.getPredicates().get(
			// "t_out_voy_nbr").trim();
			String outvoynbr = CommonUtility.deNull(criteria.getPredicates().get("out_voy_nbr").trim());
			String varnoF = CommonUtility.deNull(criteria.getPredicates().get("t_vv_cd").trim());
			String varnoT = CommonUtility.deNull(criteria.getPredicates().get("to_vv_cd").trim());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			// String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			boolean isDuplicateBK = shutoutCargoService.chkBkRefNo(new_bk_ref_nbr);
			if (isDuplicateBK) {
				errorMessage = "The Booking Reference code already exist";
			} else {
				try {
					shutoutCargoService.TransferCrgUpdateForDPE(new String[] { bk_ref_nbr },
							new String[] { new_bk_ref_nbr }, new String[] { esn_asn_nbr }, new String[] { trans_type },
							new String[] { transfer_pkgs }, new String[] { shutout_pkgs },
							new String[] { actual_nbr_shipped }, new String[] { ua_nbr_pkg },
							new String[] { first_trans_dttm }, outvoynbr, varnoF, varnoT, userId);
				} catch (BusinessException be) {
					errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
				}
			}


		} catch (BusinessException be) {
			log.info("Exception createTransferCargo: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception createTransferCargo : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setSuccess(false);
				result.setError(errorMessage);

			} else {
				result.setSuccess(true);
				result.setData(map);
			}
			log.info("DPEUtilAction :: createTransferCargo : END result:" + result.toString());

		}
		return ResponseEntityUtil.success(result);

	}
	// end region Add transfer

	// start region shutout cargo
	// sg.com.jp.dpe.action-->DPEUtilAction-->loadShutoutEDO
	@PostMapping(value = "/loadShutoutEDO")
	public ResponseEntity<?> loadShutoutEDO(HttpServletRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		Result result = new Result();
		DPECargo dpeCargo = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("loadShutoutEDO : START criteria:" + criteria.toString());
			String esn_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("esn_asn_nbr").trim());
			// String ref_nbr = RequestUtils.getStringParameter(request, "ref_nbr").trim();
			String trans_type = CommonUtility.deNull(criteria.getPredicates().get("trans_type").trim());
			String mode = CommonUtility.deNull(criteria.getPredicates().get("mode").trim());
			if (StringUtils.equalsIgnoreCase(mode, "create")) {
				dpeCargo = shutoutCargoService.loadGeneralShutoutCargoByESN(esn_asn_nbr, trans_type);
			} else {
				dpeCargo = shutoutCargoService.loadGeneralShutoutCargoByEDO(esn_asn_nbr, trans_type);
			}
			data.put("data", dpeCargo);

		} catch (BusinessException be) {
			log.info("Exception loadShutoutEDO: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception loadShutoutEDO : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("loadShutoutEDO : END " + result.toString());
		}

		return ResponseEntityUtil.success(result);
	}

	// sg.com.jp.dpe.action-->DPEUtilAction-->updateShutoutEDO
	@PostMapping(value = "/updateShutoutEDO")
	public ResponseEntity<?> updateShutoutEDO(HttpServletRequest request) throws BusinessException {

		Map<String, Object> data = new HashMap<String, Object>();
		Result result = new Result();
		EdoValueObjectCargo edoValueObject = populateDataFromRequest(request);
		String msg = "";
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("updateShutoutEDO : START " + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String acct_nbr = CommonUtility.deNull(criteria.getPredicates().get("acct").trim());
			if (!StringUtils.equalsIgnoreCase(acct_nbr, "cash")) {
				boolean isValidAcct = shutoutCargoService.checkAccountNbr(acct_nbr);
				if (!isValidAcct) {
					msg = ConstantUtil.ErrorMsg_Invalid_Number;
				}
			}
			if (msg.length() > 0) {
				data.put("msg", msg);

				result.setSuccess(false);
				result.setData(data);

			} else {
				String ref_nbr = CommonUtility.deNull(criteria.getPredicates().get("ref_nbr").trim());
				edoValueObject.setEdoAsnNbr(ref_nbr);
				shutoutCargoService.updateShutoutEdo(edoValueObject, userId);

			}

		} catch (BusinessException be) {
			log.info("Exception updateShutoutEDO: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception updateShutoutEDO : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setSuccess(true);
			}
			log.info("updateShutoutEDO : END " + result.toString());
		}
		return ResponseEntityUtil.success(result);
	}

	private EdoValueObjectCargo populateDataFromRequest(HttpServletRequest request) {
		log.info("DPEUtilAction :: populateDataFromRequest : START ");

		EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: populateDataFromRequest Start criteria "+" criteria:"+criteria.toString() );
			String var_nbr = CommonUtility.deNull(criteria.getPredicates().get("vv_cd").trim());
			String edo_nbr_pkgs = CommonUtility.deNull(criteria.getPredicates().get("edo_pkgs").trim());

			String nom_weight = CommonUtility.deNull(criteria.getPredicates().get("edo_pkgs_wt").trim());

			String nom_volume = CommonUtility.deNull(criteria.getPredicates().get("edo_pkgs_vol").trim());

			String esn_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("esn_asn_nbr").trim());

			String cons_nm = "JP";
			String delivery_to = CommonUtility.deNull(criteria.getPredicates().get("esn_delivery_to").trim());

			String adp_nbr = CommonUtility.deNull(criteria.getPredicates().get("edo_adp_nbr").trim());

			String adp_nm = CommonUtility.deNull(criteria.getPredicates().get("edo_adp_nm")).trim();
			String adp_cust_cd = "";
			if (!CommonUtility.deNull(criteria.getPredicates().get("adp_cust_cd")).equalsIgnoreCase("")) {
				adp_cust_cd = CommonUtility.deNull(criteria.getPredicates().get("adp_cust_cd").trim());
			}
			String acct_nbr = CommonUtility.deNull(criteria.getPredicates().get("acct").trim());

			String acct_type = "";
			if (StringUtils.equalsIgnoreCase(acct_nbr, "cash")) {
				acct_type = "C";
			} else {
				acct_type = "A";
			}

			edoValueObject.setVarNbr(CommonUtility.deNull(var_nbr));
			edoValueObject.setEdoNbrPkgs(CommonUtility.deNull(edo_nbr_pkgs));
			edoValueObject.setNomWeight(CommonUtility.deNull(nom_weight));
			edoValueObject.setNomVolume(CommonUtility.deNull(nom_volume));
			edoValueObject.setEsnAsnNbr(CommonUtility.deNull(esn_asn_nbr));
			edoValueObject.setPkgTypeCd(CommonUtility.deNull(acct_type));
			edoValueObject.setAcctNbr(CommonUtility.deNull(acct_nbr));
			edoValueObject.setConsNm(CommonUtility.deNull(cons_nm));
			edoValueObject.setDeliveryTo(CommonUtility.deNull(delivery_to));
			edoValueObject.setAdpNbr(CommonUtility.deNull(adp_nbr));
			edoValueObject.setAdpNm(CommonUtility.deNull(adp_nm));
			edoValueObject.setAdpCustCd(CommonUtility.deNull(adp_cust_cd));

		
		} catch (Exception e) {
			log.info("Exception populateDataFromRequest : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		}

		log.info("DPEUtilAction :: populateDataFromRequest : END ");
		return edoValueObject;
	}

	// sg.com.jp.dpe.action-->DPEUtilAction-->createCargoEDOShutOut
	@PostMapping(value = "/createCargoEDOShutOut")
	public ResponseEntity<?> createCargoEDOShutOut(HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("createCargoEDOShutOut : START " + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			EdoValueObjectCargo edoValueObject = populateDataFromRequest(request);
			shutoutCargoService.insertShutoutEdoForDPE(edoValueObject, userId);

		} catch (BusinessException be) {
			log.info("Exception createCargoEDOShutOut: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception createCargoEDOShutOut : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setSuccess(true);
			}
			log.info("createCargoEDOShutOut : END " + result.toString());
		}
		return ResponseEntityUtil.success(result);
	}

	// sg.com.jp.dpe.action-->DPEUtilAction-->loadJPBillAccount
	@PostMapping(value = "/loadJPBillAccount")
	public ResponseEntity<?> loadJPBillAccount(HttpServletRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		errorMessage = null;
		Result result = new Result();
		List<DPEUtil> retLst = new ArrayList<DPEUtil>();
		DPEUtil dpeUtil = null;
		
		List<EdoJpBilling> arraylist;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("loadJPBillAccount : START " + criteria.toString());
			String vv_cd = CommonUtility.deNull(criteria.getPredicates().get("vv_cd"));
			String edo_adp_nbr = CommonUtility.deNull(criteria.getPredicates().get("edo_adp_nbr"));
			if(!edo_adp_nbr.equals(""))
				edo_adp_nbr =  edo_adp_nbr.trim();
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			arraylist = shutoutCargoService.getEdoJpBillingNbr(edo_adp_nbr, companyCode, vv_cd);
			log.info(" JP Account Array List  : " + arraylist);
			if (arraylist != null & arraylist.size() > 0) {
				log.info(" List of JP Account  : " + arraylist.size());
				for (int i = 0; i < arraylist.size(); i++) {
					dpeUtil = new DPEUtil();
					// tmp = (java.lang.String) arraylist.get(i);
					// 2018 Jun 25 koktsing
					// to fix the problem in handling ~ after the change introduced in
					// SSL-CAB-0000263 Penetration Testing
					// startPos = StringUtils.indexOf(tmp, "--") + 2;
					// startPos = StringUtils.indexOf(tmp, "~") + 1;
					// endPos = startPos + 6;
					// populate value
					EdoJpBilling oTmpEdoJpBilling = (EdoJpBilling) arraylist.get(i);
					String adp = oTmpEdoJpBilling.getStrAdp();
					String shippingAgent = oTmpEdoJpBilling.getStrShippingAgent();
					String agent = "";
					if (adp != null && !adp.isEmpty()) {
						agent = adp;
					} else if (!shippingAgent.isEmpty()) {
						agent = shippingAgent;
					}
					dpeUtil.setValue(oTmpEdoJpBilling.getCoNm() + agent + oTmpEdoJpBilling.getStrjpbnbr());
					dpeUtil.setKey(oTmpEdoJpBilling.getStrjpbnbr());
					retLst.add(dpeUtil);
				}
			}
			// add more option cash payment
			dpeUtil = new DPEUtil();
			dpeUtil.setKey("cash");
			dpeUtil.setValue("Cash Payment");
			retLst.add(dpeUtil);
			log.info(" List of JP Account final (after add Cash payment)  : " + arraylist.size());
			data.put("data", retLst);

		} catch (BusinessException be) {
			log.info("Exception loadJPBillAccount: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception loadJPBillAccount : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("loadJPBillAccount : END " + result.toString());
		}

		return ResponseEntityUtil.success(result);
	}

	// end region create shutout edo
	// start Region removeShutoutEDO
	@PostMapping(value = "/removeShutoutEDO")
	public ResponseEntity<?> removeShutoutEDO(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("removeShutoutEDO : START " + criteria.toString());
			String edo_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("edo_asn_nbr").trim());
			String esn_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("esn_asn_nbr").trim());
			String shut_out_pkgs = CommonUtility.deNull(criteria.getPredicates().get("nbr_pkgs").trim());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			log.info("DPEUtilAction :: removeShutoutEDO : START values" + ":" + edo_asn_nbr + ":" + esn_asn_nbr + ":"
					+ shut_out_pkgs);
			int pkgs = Integer.parseInt(shut_out_pkgs);
			log.info("shutout to update5555 **:" + pkgs);
			String msg = shutoutCargoService.deleteShutoutEdoDetails(edo_asn_nbr, userId);
			if (StringUtils.equalsIgnoreCase("FALSE", msg)) {
				String[] tempString = {edo_asn_nbr};
				msg  = CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Cannot_Delete, tempString);
				throw new BusinessException(msg);
			} else {
				// to ensure shutoutdelivery pkgs is update correctly in BK_details

				shutoutCargoService.updateShutEdoQtyAfterCancel(esn_asn_nbr, pkgs, userId);
				log.info("After shutout **:" + pkgs);
			}
			data.put("msg", msg);

		} catch (BusinessException be) {
			log.info("Exception removeShutoutEDO: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception removeShutoutEDO : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setSuccess(true);
				result.setData(data);
			}
			log.info("END: removeShutoutEDO result:" + result.toString());
		}
		return ResponseEntityUtil.success(result);
	}
	// end region removeShutoutEDO

	// region getAdpNmByAdpIc
	@PostMapping(value = "/getAdpNmByAdpIc")
	public ResponseEntity<?> getAdpNmByAdpIc(HttpServletRequest request) throws Exception {
		log.info("DPEUtilAction :: getAdpNmByAdpIc : START ");
		Result result = new Result();
		DPEUtil apd = new DPEUtil();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("getAdpNmByAdpIc : START " + criteria.toString());
			String adpIc = CommonUtility.deNull(criteria.getPredicates().get("apdIc")).trim();
			log.info("DPEUtilAction :: adpIc : " + adpIc);
			apd = shutoutCargoService.getAdpNmByAdpIc(adpIc);

		} catch (BusinessException be) {
			log.info("Exception getAdpNmByAdpIc: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception getAdpNmByAdpIc : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setSuccess(true);
				result.setData(apd);
			}
			log.info("END: getAdpNmByAdpIc result:" + result.toString());
		}

		log.info("DPEUtilAction : getAdpNmByAdpIc : END ");
		return ResponseEntityUtil.success(result);
	}
	// end region

	// start Region View transfer
	@PostMapping(value = "/loadTransferCargoForView")
	public ResponseEntity<?> loadTransferCargoForView(HttpServletRequest request) {
		log.info("loadTransferCargoForView :: START ");
		Criteria criteria = CommonUtil.getCriteria(request);
		String esn_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("esn_asn_nbr").trim());
		Result result = new Result();
		DPECargo loadTransferCargoForView = null;
		errorMessage = null;
		log.info("loadTransferCargoForView :: adpIc : " + esn_asn_nbr);
		try {
			log.info("START: loadTransferCargoForView Start criteria "+" criteria:"+criteria.toString() );
			loadTransferCargoForView = shutoutCargoService.loadTransferCargoForView(esn_asn_nbr);

		} catch (BusinessException be) {
			log.info("Exception loadTransferCargoForView: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception loadTransferCargoForView : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setSuccess(true);
				result.setData(loadTransferCargoForView);
			}
			log.info("END: loadTransferCargoForView result:" + result.toString());
		}

		log.info("loadTransferCargoForView :  : END ");
		return ResponseEntityUtil.success(result);
	}
	
	@PostMapping(value = "/printShutoutCargoEDO")
	public ResponseEntity<?> printShutoutCargoEDO(HttpServletRequest request) {
		log.info("printShutoutCargoEDO :: START ");
		Criteria criteria = CommonUtil.getCriteria(request);
		String ref_nbr = CommonUtility.deNull(criteria.getPredicates().get("ref_nbr").trim());
		String trans_type = CommonUtility.deNull(criteria.getPredicates().get("trans_type").trim());
		Result result = new Result();
		errorMessage = null;
		DPECargo item = null;
		try {
			log.info("printShutoutCargoEDO : START " + criteria.toString());
			item = shutoutCargoService.loadGeneralShutoutCargoByEDO(ref_nbr, trans_type);

		} catch (BusinessException be) {
			log.info("Exception printShutoutCargoEDO: ", be);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception printShutoutCargoEDO : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setSuccess(true);
				result.setData(item);
			}
			log.info("END: printShutoutCargoEDO result:" + result.toString());
		}
		return ResponseEntityUtil.success(result);
	}
}
