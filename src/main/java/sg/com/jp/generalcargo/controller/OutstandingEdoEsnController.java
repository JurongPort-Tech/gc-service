package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import io.swagger.annotations.ApiOperation;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.CargoEnquiryDetails;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.OutstandingVO;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.service.OutstandingEdoEsnService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.DpeConstants;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = OutstandingEdoEsnController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OutstandingEdoEsnController {
	public static final String ENDPOINT = "gc/outEdoEsn";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(OutstandingEdoEsnController.class);
	@Autowired
	private OutstandingEdoEsnService outstandingEdoEsnService;

	// StartRegion General Cargo Outstanding EDO/ESN for haulier
	// OutwardCargoShutoutMonitoringController
	// sg.com.jp.dpe.action-->DPEUtilAction-->lookupRef
	@PostMapping(value = "/lookupRefEdoEsn")
	public ResponseEntity<?> lookupRefEdoEsn(HttpServletRequest request) {
		List<DPEUtil> recs = null;
		Result result = new Result();
		int total = 0;
		String name = "";
		String voy_nbr = "";
		String ind = "";
		String coCd = "";
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: lookupRefEdoEsn :: criteria : " + criteria.toString());
			String type = CommonUtility.deNull(criteria.getPredicates().get("type")).trim();
			Integer limit = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("limit"))).equals("") ? "10" : "10"));
			Integer start = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("start"))).equals("") ? "0" : "0"));

			if (StringUtils.equalsIgnoreCase("vessel", type)) {
				name = CommonUtility.deNull(criteria.getPredicates().get("name")).trim();
				coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				log.info("DPEUtilAction :: name : " + name);
				recs = outstandingEdoEsnService.listVesselByName(start, limit, name, coCd);
				total = outstandingEdoEsnService.countVesselByName(name, coCd);
			} else if (StringUtils.equalsIgnoreCase("inVoy", type)) {
				name = CommonUtility.deNull(criteria.getPredicates().get("vsl_nm")).trim();
				voy_nbr = CommonUtility.deNull(criteria.getPredicates().get("in_voy_nbr")).trim();
				ind = CommonUtility.deNull(criteria.getPredicates().get("ind"));
				coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				log.info("DPEUtilAction :: name : " + name);
				log.info("DPEUtilAction :: in_voy_nbr : " + voy_nbr);
				recs = outstandingEdoEsnService.getInVoyageList(name, coCd, voy_nbr, ind);
			} else if (StringUtils.equalsIgnoreCase("outVoy", type)) {
				name = CommonUtility.deNull(criteria.getPredicates().get("vsl_nm")).trim();
				voy_nbr = CommonUtility.deNull(criteria.getPredicates().get("out_voy_nbr")).trim();
				ind = CommonUtility.deNull(criteria.getPredicates().get("ind"));
				log.info("DPEUtilAction :: name : " + name);
				log.info("DPEUtilAction :: out_voy_nbr : " + voy_nbr);
				recs = outstandingEdoEsnService.getOutVoyageList(name, coCd, voy_nbr, ind);
			} else if (StringUtils.equalsIgnoreCase("haulier_company", type)) {
				name = CommonUtility.deNull(criteria.getPredicates().get("name")).trim();
				log.info("DPEUtilAction :: name : " + name);
				recs = outstandingEdoEsnService.listHaulierCompanyByName(start, limit, name);
				total = outstandingEdoEsnService.countHaulierCompanyByName(name);
			}
		
		} catch (BusinessException be) {
			log.info("Exception lookupRefEdoEsn: ", be);
			errorMessage = ConstantUtil.OUTSTANDING_EDO_ESN_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception lookupRefEdoEsn : ", e);
			errorMessage = ConstantUtil.OUTSTANDING_EDO_ESN_MAP.get("M4201");
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setSuccess(false);
				result.setError(errorMessage);
			}
			return ResponseEntityUtil.success(result.toString());

		} finally {
			log.info("END:lookupRefEdoEsn ");
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
			errorMessage = ConstantUtil.OUTSTANDING_EDO_ESN_MAP.get("M4201");
		} finally {
			log.info("END: searchResponse");
		}
		return ResponseEntityUtil.ok(response);
	}
	
	@PostMapping(value = "/listRecordsEdoEsnExcel")
	public Result listRecordsEdoEsnExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.listRecordsEdoEsn(request, response);
	}

	// sg.com.jp.dpe.action -->DpeOutstandingAction -->listRecords()
	@PostMapping(value = "/listRecordsEdoEsn")
	public Result listRecordsEdoEsn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Result result = new Result();
		Map<String, Object> filters = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<OutstandingVO> outstandingList = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: listRecordsEdoEsn :: criteria : " + criteria.toString());
			String type = CommonUtility.deNull(criteria.getPredicates().get("type")).trim();
			String sort = CommonUtility.deNull(criteria.getSort()).trim();
			String dir = CommonUtility.deNull(criteria.getDir()).trim();
			Integer limit = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("limit"))).equals("") ? "10" : "10"));
			Integer start = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("start"))).equals("") ? "0" : "0"));
			Enumeration<String> ee = request.getParameterNames();
			while (ee.hasMoreElements()) {
				String key = (String) ee.nextElement();
				String value = request.getParameter(key);
				log.info("DPEOutStandingAction :: listDPEExceptionUtility :Enumeration : " + key + "/" + value);
				filters.put(key, value);
			}
			if (log.isInfoEnabled()) {
				log.info("OUtStanding ReqMap : " + request.getParameterMap());
				log.info("Outastanding Filters: " + filters);
			}

			log.info("DpeOutstandingAction :: type : " + type);
			log.info("DpeOutstandingAction :: sort : " + sort);
			log.info("DpeOutstandingAction :: dir : " + dir);
			log.info("DpeOutstandingAction :: start : " + start);
			log.info("DpeOutstandingAction :: limit : " + limit);

			int total = outstandingEdoEsnService.countRecords(filters);
			log.info("DpeOutstandingAction :: total records returned : " + total);
			
			String isExcel = CommonUtility.deNull(criteria.getPredicates().get("isExcel"));
			Boolean needAllData = false;
			if(!isExcel.isEmpty()) {
				needAllData = true;
			}
			
			if (total > 0) {
				outstandingList = outstandingEdoEsnService.listRecords(start, limit, sort, dir, filters, criteria, needAllData);
			} else {
				outstandingList = new ArrayList<OutstandingVO>();
			}
			if (StringUtils.equalsIgnoreCase(DpeConstants.OUTSTANDING_EDO_TYPE, type)) {
				data.put("dataEDO", outstandingList);
				data.put("total", total);
			} else if (StringUtils.equalsIgnoreCase(DpeConstants.OUTSTANDING_ESN_TYPE, type)) {
				data.put("dataESN", outstandingList);
				data.put("total", total);
			}

		} catch (BusinessException be) {
			log.info("Exception listRecordsEdoEsn: ", be);
			errorMessage = ConstantUtil.OUTSTANDING_EDO_ESN_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception listRecordsEdoEsn : ", e);
			errorMessage = ConstantUtil.OUTSTANDING_EDO_ESN_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				data.put("errorMessage", errorMessage);
				result.setError(errorMessage);
				result.setErrors(data);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: listRecordsEdoEsn result:" + result.toString());
		}
		return result;
	}

	// OutwardCargoShutoutReportController
	// sg.com.jp.dpe.action-->CargoEnquiryMgmtAction-->getRecord
	@ApiOperation(value = "getRecordEdoEsn", response = String.class)
	@PostMapping(value = "/getRecordEdoEsn")
	public ResponseEntity<?> getRecordEdoEsn(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		try {
			log.info("** getRecord Start criteria :" + criteria.toString());
			String edoNbr = (CommonUtility.deNull(criteria.getPredicates().get("edo_nbr")));
			String ESN = CommonUtility.deNull(criteria.getPredicates().get("esn_nbr"));
			Long esnNbr = Long.parseLong("0");
			if (!ESN.equalsIgnoreCase("")) {
				esnNbr = Long.parseLong((CommonUtility.deNull(criteria.getPredicates().get("esn_nbr" + ""))));
			}

			String type = CommonUtility.deNull(criteria.getPredicates().get("crg_type"));
			log.info("CargoEnquiryMgmtAction :: edo_asn_nbr : " + edoNbr);
			log.info("CargoEnquiryMgmtAction :: esn_asn_nbr : " + esnNbr);
			log.info("CargoEnquiryMgmtAction :: type : " + type);
			CargoEnquiryDetails cargoEnq = outstandingEdoEsnService.getCargoEnquiryRecord(edoNbr, esnNbr, type);
			result.setData(cargoEnq);
		
		} catch (BusinessException be) {
			log.info("Exception getRecordEdoEsn: ", be);
			errorMessage = ConstantUtil.OUTSTANDING_EDO_ESN_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception getRecordEdoEsn : ", e);
			errorMessage = ConstantUtil.OUTSTANDING_EDO_ESN_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				
				result.setSuccess(true);
			}
			log.info("END: getRecordEdoEsn result:" + result.toString());
		}
		
		return ResponseEntityUtil.success(result);
	}

	// sg.com.jp.dpe.action-->DPEUtilAction-->getVesselDetails
	@PostMapping(value = "/getVesselDetails")
	public Result getVesselDetails(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		DPEUtil recs = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getVesselDetails :: criteria : " + criteria.toString());
			String vv_cd = CommonUtility.deNull(criteria.getPredicates().get("vv_cd")).trim();
			recs = outstandingEdoEsnService.getVesselDetail(vv_cd);
			
		} catch (BusinessException be) {
			log.info("Exception getVesselDetails: ", be);
			errorMessage = ConstantUtil.OUTSTANDING_EDO_ESN_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception getVesselDetails : ", e);
			errorMessage = ConstantUtil.OUTSTANDING_EDO_ESN_MAP.get("M4201");
		} finally {
			data.put("data", recs);
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: getVesselDetails result:" + result.toString());
		}
		return result;
	}

	//jp.src.delegate.helper.gbms.cargo.bookingReference-->BRUpdateHandler-->perform()
	@PostMapping(value = "/showListBk")
	public Result showListBk(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		DPEUtil recs = null;
		errorMessage = null;
	
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: showListBk :: criteria : " + criteria.toString());
			String mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			if (mode.isEmpty()) {
				mode = "";
			}
			map.put("showCancel", "Y");
			map.put("showAmend", "Y");
			String disabled = CommonUtility.deNull(criteria.getPredicates().get("disabled"));
			BookingReferenceValueObject brvo = new BookingReferenceValueObject();
			// String userId = CommonUtility.deNull(criteria.getPredicates().get("userId"));
			String userCoyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			map.put("usrtyp", userCoyCode);
			List<List<String>> vector = outstandingEdoEsnService.getCargoType();
			map.put("cargoTypeVect", vector);
			Map<String, String> cargoCategoryCode_cargoCategoryName = outstandingEdoEsnService.getCargoCategoryCode_CargoCategoryName();
			map.put("cargoCategoryCode_cargoCategoryName", cargoCategoryCode_cargoCategoryName);
			String brno = CommonUtility.deNull(criteria.getPredicates().get("brno"));
			if (mode.equals("showlist")) {
				List<BookingReferenceValueObject> vector1 = outstandingEdoEsnService.fetchBKDetails(brno);
				brvo =  vector1.get(0);
                String fromESN = CommonUtility.deNull(criteria.getPredicates().get("fromESN"));
                map.put("fromESN", fromESN);
				String checkCancelAmendResultC = outstandingEdoEsnService.chkCancelAmend(brno, userCoyCode, "C");
				String checkCancelAmendResultA = outstandingEdoEsnService.chkCancelAmend(brno, userCoyCode, "A");
				String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

				boolean userDBBookingRefer = outstandingEdoEsnService.getCheckUserBookingReference(coCd, brno);
				log.info("user DB Booking Refer ==== "+ userDBBookingRefer);

				String userIdDBBookingRefe = "FALSE";
				if (userDBBookingRefer == true) {
					userIdDBBookingRefe = "TRUE";
				}
				map.put("USERIDBOOKINGREFER", userIdDBBookingRefe);
				map.put("coCd", coCd);
				if (!checkCancelAmendResultC.equals("N")) {
					request.setAttribute("showCancel", "N");
				} else {
					request.setAttribute("showCancel", "Y");
				}
				if (!checkCancelAmendResultA.equals("N") || StringUtils.equals("disabled", disabled)) {
					request.setAttribute("showAmend", "N");
				} else {
					request.setAttribute("showAmend", "Y");
				}
			}
			String varno = CommonUtility.deNull(criteria.getPredicates().get("varno"));
			int maxCargoTon = outstandingEdoEsnService.retrieveMaxCargoTon(varno);
			request.setAttribute("maxCargoTon", Integer.valueOf(maxCargoTon));
			map.put("topsModel", brvo);
			
		} catch (BusinessException be) {
			log.info("Exception showListBk: ", be);
			errorMessage = ConstantUtil.OUTSTANDING_EDO_ESN_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception showListBk : ", e);
			errorMessage = ConstantUtil.OUTSTANDING_EDO_ESN_MAP.get("M4201");
		} finally {
			map.put("data", recs);
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: showListBk result:" + result.toString());
		}
		return result;
	}

	// End Region
}
