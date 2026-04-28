package sg.com.jp.generalcargo.controller;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import sg.com.jp.generalcargo.domain.CargoEnquiryDetails;
import sg.com.jp.generalcargo.domain.CargoEnquiryMgmtAction;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.service.GeneralBulkEnquiryService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.DpeConstants;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = GeneralBulkEnquiryController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GeneralBulkEnquiryController {
	public static final String ENDPOINT = "gc/generalBulkEnquiry";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(GeneralBulkEnquiryController.class);
	
	@Autowired
	private GeneralBulkEnquiryService generalBulkEnquiryService;
	
	@PostMapping(value = "/lookupRef")
	public ResponseEntity<?> lookupRefGeneralBulk(HttpServletRequest request) throws BusinessException {
		List<DPEUtil> recs = null;
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		int total = 0;
		String name = "";
		String voy_nbr = "";
		String ind = "";
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: lookupRefGeneralBulk :: criteria : " + criteria.toString());
			String type = CommonUtility.deNull(criteria.getPredicates().get("type")).trim();
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			Integer limit = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("limit"))).equals("") ? "50" : "50"));
			Integer start = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("start"))).equals("") ? "0" : "0"));

			if (StringUtils.equalsIgnoreCase("vessel", type)) {
				name = CommonUtility.deNull(criteria.getPredicates().get("name")).trim();
				log.info("DPEUtilAction :: name : " + name);
				recs = generalBulkEnquiryService.listVesselByName(start, limit, name, coCd);
				total = generalBulkEnquiryService.countVesselByName(name, coCd);
			} else if (StringUtils.equalsIgnoreCase("inVoy", type)) {
				name = CommonUtility.deNull(criteria.getPredicates().get("vsl_nm")).trim();
				voy_nbr = CommonUtility.deNull(criteria.getPredicates().get("in_voy_nbr")).trim();
				ind = CommonUtility.deNull(criteria.getPredicates().get("ind"));
				log.info("DPEUtilAction :: name : " + name);
				log.info("DPEUtilAction :: in_voy_nbr : " + voy_nbr);
				recs = generalBulkEnquiryService.getInVoyageList(name, coCd, voy_nbr, ind);
			} else if (StringUtils.equalsIgnoreCase("outVoy", type)) {
				name = CommonUtility.deNull(criteria.getPredicates().get("vsl_nm")).trim();
				voy_nbr = CommonUtility.deNull(criteria.getPredicates().get("out_voy_nbr")).trim();
				ind = CommonUtility.deNull(criteria.getPredicates().get("ind"));
				log.info("DPEUtilAction :: name : " + name);
				log.info("DPEUtilAction :: out_voy_nbr : " + voy_nbr);
				recs = generalBulkEnquiryService.getOutVoyageList(name, coCd, voy_nbr, ind);
			} else if (StringUtils.equalsIgnoreCase("company_jp_staff", type)) {
				name = CommonUtility.deNull(criteria.getPredicates().get("name")).trim();
				log.info("DPEUtilAction :: name : " + name);
				recs = generalBulkEnquiryService.listCompanyByName(start, limit, name);
				total = generalBulkEnquiryService.countCompanyByName(name);
			}
			
		} catch (BusinessException be) {
			log.info("Exception lookupRefGeneralBulk: ", be);
			errorMessage = ConstantUtil.GENERAL_BULK_ENQUIRY_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception lookupRefGeneralBulk : ", e);
			errorMessage = ConstantUtil.GENERAL_BULK_ENQUIRY_MAP.get("M4201");
		} finally {
			data.put("data", recs);
			data.put("total", total);
			
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: lookupRefGeneralBulk result:" + result.toString());
		}

		return ResponseEntityUtil.success(result);

	}

	// sg.com.jp.dpe.action-->DPEUtilAction-->getVesselDetails
	@PostMapping(value = "/getVesselDetails")
	public Result getVesselDetails(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		DPEUtil recs = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getVesselDetails :: criteria : " + criteria.toString());
			String vv_cd = CommonUtility.deNull(criteria.getPredicates().get("vv_cd")).trim();
			recs = generalBulkEnquiryService.getVesselDetail(vv_cd);
			
		} catch (BusinessException be) {
			log.info("Exception getVesselDetails: ", be);
			errorMessage = ConstantUtil.GENERAL_BULK_ENQUIRY_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception getVesselDetails : ", e);
			errorMessage = ConstantUtil.GENERAL_BULK_ENQUIRY_MAP.get("M4201");
		} finally {
			data.put("data", recs);
			data.put("dateRange", DpeConstants.FROM_TO_RANGE);
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
	
	//CargoEnquiryMgmtAction ---listRecords
	@PostMapping(value = "/listRecords")
	public Result listRecords(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> filters = new HashMap<String, Object>();
		int total = 0;
		List<CargoEnquiryMgmtAction> recs = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: listRecords :: criteria : " + criteria.toString());
			Integer limit = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("limit"))).equals("") ? "50" : "50"));
			Integer start = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("start"))).equals("") ? "0" : "0"));
			String sort = CommonUtility.deNull(criteria.getPredicates().get("sort")).trim();
			String dir = CommonUtility.deNull(criteria.getPredicates().get("dir")).trim();
			String companyCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode")).trim();
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("f_vv_cd"));

			filters = generalBulkEnquiryService.getCargoEnquiryParamsMapByCustCd(companyCd);
			Enumeration<String> ee = request.getParameterNames();

			while (ee.hasMoreElements()) {
				String key = (String) ee.nextElement();
				String value = request.getParameter(key);
				log.info("DPEUtilAction :: CargoEnquiryMgmtAction :Enumeration : " + key + "/" + value);
				if( value != "") {
					filters.put(key, value);
				}
				
			}
			if (log.isInfoEnabled()) {
				log.info("ReqMap  Cargo EnquryAction: " + request.getParameterMap());
				log.info("Filters Cargo EnquryAction: " + filters);
			}
			filters.put("f_cust_cd", companyCd);
			String type = CommonUtility.deNull(criteria.getPredicates().get("rdoCargoType"));
			filters.put("type", type);
			filters.put("vvCd", vvCd);

			log.info("CargoEnquiryMgmtAction :: listRecords : start : " + start);
			log.info("CargoEnquiryMgmtAction :: listRecords : limit : " + limit);
			log.info("CargoEnquiryMgmtAction :: listRecords : sort : " + sort);
			log.info("CargoEnquiryMgmtAction :: listRecords : dir : " + dir);
			log.info("CargoEnquiryMgmtAction :: listRecords : companyCd : " + companyCd);
			log.info("CargoEnquiryMgmtAction :: listRecords : Cargo Type : " + type);

			total = generalBulkEnquiryService.countCargoRecords(filters);
			recs = generalBulkEnquiryService.listCargoRecords(start, limit, sort, dir, filters, criteria);
			log.info("CargoEnquiryMgmtAction :: listRecords : total records : " + total);
			
		} catch (BusinessException be) {
			log.info("Exception listRecords: " + be.getMessage());
			errorMessage = ConstantUtil.GENERAL_BULK_ENQUIRY_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception listRecords : ", e);
			errorMessage = ConstantUtil.GENERAL_BULK_ENQUIRY_MAP.get("M4201");
		} finally {
			data.put("data", recs);
			data.put("total", new Integer(total));
			if (errorMessage != null) {
				data.put("errorMessage", errorMessage);
				result.setErrors(data);
				result.setSuccess(false);
			} else {
				result.setData(data);
				result.setSuccess(true);
			}
			log.info("END: listRecords result:" + result.toString());
		}
		return result;
	}
	
	
	// sg.com.jp.dpe.action-->CargoEnquiryMgmtAction-->getRecord
		@ApiOperation(value = "getRecord", response = String.class)
		@PostMapping(value = "/getRecord")
		public ResponseEntity<?> getRecord(HttpServletRequest request) throws BusinessException {
			Criteria criteria = CommonUtil.getCriteria(request);
			Result result = new Result();
			Map<String, Object> map = new HashMap<>();
			CargoEnquiryDetails cargoEnq = null;
			try {
				log.info("** getRecord Start criteria :" + criteria.toString());
				String coCd = (CommonUtility.deNull(criteria.getPredicates().get("companyCode")));
				String edoNbr = (CommonUtility.deNull(criteria.getPredicates().get("edo_nbr")));
				String ESN = CommonUtility.deNull(criteria.getPredicates().get("esn_nbr"));
				Long esnNbr=Long.parseLong("0");
				if (!ESN.equalsIgnoreCase("")) {
					 esnNbr = Long.parseLong((CommonUtility.deNull(criteria.getPredicates().get("esn_nbr"
							+ ""))));
				}
				
				String type = CommonUtility.deNull(criteria.getPredicates().get("crg_type"));
				log.info("CargoEnquiryMgmtAction :: edo_asn_nbr : " + edoNbr);
				log.info("CargoEnquiryMgmtAction :: esn_asn_nbr : " + esnNbr);
				log.info("CargoEnquiryMgmtAction :: type : " + type);
				cargoEnq = generalBulkEnquiryService.getCargoEnquiryRecord(edoNbr, esnNbr, type);
				if(!coCd.equalsIgnoreCase("JP")) {
					cargoEnq.setIsUser(true);
				} else {
					cargoEnq.setIsUser(false);
				}
				
			} catch (BusinessException be) {
				log.info("Exception getRecord: ", be);
				errorMessage = ConstantUtil.GENERAL_BULK_ENQUIRY_MAP.get(CommonUtility.getExceptionMessage(be));
				if(errorMessage == null) {
					errorMessage = be.getMessage();
				}	
			} catch (Exception e) {
				log.info("Exception getRecord : ", e);
				errorMessage = ConstantUtil.GENERAL_BULK_ENQUIRY_MAP.get("M4201");
			} finally {
				if (errorMessage != null) {
					map.put("error", errorMessage);
					result = new Result();
					result.setError(errorMessage);
					result.setSuccess(false);
				} else {
					result = new Result();
					result.setData(cargoEnq);
					result.setSuccess(true);
					log.info("END: getRecord result: " + result.toString());
				}
			}
			
			return ResponseEntityUtil.success(result);
		}
			
		// End region General and bulk cargo enquiry
		
		@RequestMapping(value = "/getCompanyName", method = RequestMethod.POST)
		public ResponseEntity<?> getCompanyName(HttpServletRequest request) {
			Result result = new Result();
			Map<String, Object> map = new HashMap<>();
			errorMessage = null;
			try {
				log.info("START: get company name");
				Criteria criteria = CommonUtil.getCriteria(request);
				String cName = CommonUtility.deNull(criteria.getPredicates().get("cName"));
				String companyName;
				if (!cName.isEmpty()) {
					companyName = generalBulkEnquiryService.getCompanyName(cName);
				} else {
					companyName = generalBulkEnquiryService.getCompanyName(CommonUtility.deNull(criteria.getPredicates().get("companyCode")));
				}
				map.put("companyName", companyName);
				map.put("companyCode", CommonUtility.deNull(criteria.getPredicates().get("companyCode")));
				result.setSuccess(true);
				result.setData(map);
			} catch (BusinessException be) {
				log.info("Exception getCompanyName: ", be);
				errorMessage = ConstantUtil.GENERAL_BULK_ENQUIRY_MAP.get(CommonUtility.getExceptionMessage(be));
				if(errorMessage == null) {
					errorMessage = be.getMessage();
				}	
			} catch (Exception e) {
				log.info("Exception getCompanyName : ", e);
				errorMessage = ConstantUtil.GENERAL_BULK_ENQUIRY_MAP.get("M4201");
			} finally {
				log.info("END: get company name Result" + result.toString());
			}
			return ResponseEntityUtil.success(result.toString());
		}
	
}
