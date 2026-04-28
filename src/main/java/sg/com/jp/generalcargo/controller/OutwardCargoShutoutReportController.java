package sg.com.jp.generalcargo.controller;

import java.util.Date;
import java.util.HashMap;
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

import io.swagger.annotations.ApiOperation;
import sg.com.jp.generalcargo.domain.CargoEnquiryDetails;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.OutwardCargoShutoutReportService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = OutwardCargoShutoutReportController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OutwardCargoShutoutReportController {
	public static final String ENDPOINT = "gc/outwardcargo/shutoutCargoReport";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(OutwardCargoShutoutReportController.class);

	@Autowired
	private OutwardCargoShutoutReportService shutoutCargoReportService;

	// delegate.helper.gbms.cargo.generalcargo-->perform
	// ShutoutCargoHandler
	@PostMapping(value = "/processShutoutCargoReport")
	public ResponseEntity<?> getShutoutCargoReport(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		Criteria criteria = CommonUtil.getCriteria(request);
		errorMessage = null;
		TableResult shutoutCargoMtrgList = null;
		try {
			log.info("START:getShutoutCargoReport criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String reqPage = CommonUtility.deNull(criteria.getPredicates().get("command"));
			// START update changes - NS May 2023
			String userName = shutoutCargoReportService.getUserNameMap(userId);
			// END update changes - NS May 2023
			log.info("Request Page : " + reqPage);
			if ("Top".equalsIgnoreCase(reqPage)) {
				String dateFrom = CommonUtility.deNull(criteria.getPredicates().get("dateFrom"));
				String dateTo = CommonUtility.deNull(criteria.getPredicates().get("dateTo"));
				String vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
				String outVoyNbr = CommonUtility.deNull(criteria.getPredicates().get("outVoy"));
				String esnAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("esnEdo"));
				// Dpe Tungnm3 Start
//					String cargoType = request.getParameter("cargoType");
				String status = CommonUtility.deNull(criteria.getPredicates().get("status"));
				String terminal = CommonUtility.deNull(criteria.getPredicates().get("terminal"));
				String dwellDays = CommonUtility.deNull(criteria.getPredicates().get("dwellDays"));
				String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
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
//					request.getSession().setAttribute("cargoType", cargoType);
				shutoutCargoMtrgList = shutoutCargoReportService.getShutoutCargoMtrgList(dateFrom, dateTo, vslName,
						outVoyNbr, esnAsnNbr, status, terminal, dwellDays, coCd, criteria);
				map.put("total", shutoutCargoMtrgList.getData().getTotal());
				// removed pagnation
//					setRecordPaging(request, dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr, status,terminal,dwellDays);
//					list = shoutoutCargo.getShutoutCargoMtrgList(dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr, cargoType);
//					setRecordPaging(request, shoutoutCargo, dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr, cargoType);
				// Dpe Tungnm3 End
			}

			else if ("updateDeliveryStatus".equalsIgnoreCase(reqPage)) {
				String dateTime = CommonUtility.formatDateToStr(new Date(), "ddMMyyyy HHmm");
//				String dateFrom = CommonUtility.deNull(criteria.getPredicates().get("dateFrom"));
//				String dateTo = CommonUtility.deNull(criteria.getPredicates().get("dateTo"));
//				String vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
//				String outVoyNbr = CommonUtility.deNull(criteria.getPredicates().get("outVoy"));
//				String esnAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("esnEdo"));
				String status = CommonUtility.deNull(criteria.getPredicates().get("status"));
//				String terminal = CommonUtility.deNull(criteria.getPredicates().get("terminal"));
//				String dwellDays = CommonUtility.deNull(criteria.getPredicates().get("dwellDays"));
//				String cargoType = CommonUtility.deNull(criteria.getPredicates().get("cargoType"));
				String bkgRefNbr = CommonUtility.deNull(criteria.getPredicates().get("bkgRefNbr"));
				String deliveredPackages = CommonUtility.deNull(criteria.getPredicates().get("deliveredPackages"));
				String deliveryRemarks = CommonUtility.deNull(criteria.getPredicates().get("deliveryRemarks"));
//				String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				int recordsUpdated = shutoutCargoReportService.updateDeliveryStatus(bkgRefNbr, deliveredPackages,
						deliveryRemarks, userId,userName, dateTime, status);
				log.info("No. of Records updated : " + recordsUpdated);
				/*
				 * shutoutCargoMtrgList =
				 * shutoutCargoReportService.getShutoutCargoMtrgList(dateFrom, dateTo, vslName,
				 * outVoyNbr, esnAsnNbr, status, terminal, dwellDays, coCd, criteria);
				 */
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
//				String cargoType = CommonUtility.deNull(criteria.getPredicates().get("cargoType"));
				String status = CommonUtility.deNull(criteria.getPredicates().get("status"));
				String terminal = CommonUtility.deNull(criteria.getPredicates().get("terminal"));
				String dwellDays = CommonUtility.deNull(criteria.getPredicates().get("dwellDays"));
				String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				shutoutCargoMtrgList = shutoutCargoReportService.getShutoutCargoMtrgList(dateFrom, dateTo, vslName,
						outVoyNbr, esnAsnNbr, status, terminal, dwellDays, coCd, criteria);
				map.put("total", shutoutCargoMtrgList.getData().getTotal());
			}

			else if ("NextPage".equalsIgnoreCase(reqPage)) {
//				String dateFrom = CommonUtility.deNull(criteria.getPredicates().get("dateFrom"));
//				String dateTo = CommonUtility.deNull(criteria.getPredicates().get("dateTo"));
//				String vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
//				String outVoyNbr = CommonUtility.deNull(criteria.getPredicates().get("outVoy"));
//				String esnAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("esnEdo"));
//				String cargoType = CommonUtility.deNull(criteria.getPredicates().get("cargoType"));
//				String status = CommonUtility.deNull(criteria.getPredicates().get("status"));
//				String terminal = CommonUtility.deNull(criteria.getPredicates().get("terminal"));
//				String dwellDays = CommonUtility.deNull(criteria.getPredicates().get("dwellDays"));
				// removed pagnation
				// setRecordPaging(request, dateFrom, dateTo, vslName, outVoyNbr, esnAsnNbr,
				// status,terminal,dwellDays);
			}

			map.put("ShutoutList", shutoutCargoMtrgList);
		
			
		} catch (BusinessException be) {
			log.info("Exception enquiryGeneralShutoutCargo: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception enquiryGeneralShutoutCargo : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.clear();
				map.put("errorMessage", errorMessage);
				result.setError(errorMessage);
				result.setErrors(map);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: shutoutCargoReport result" + result.toString());

		}
		return ResponseEntityUtil.success(result.toString());
	}

	/*
	 * private TableResult getTableResult(List<ShutOutCargoVo> list) { TableResult
	 * tableResult = new TableResult(); TableData tableData = new TableData();
	 * TopsModel topsModel = new TopsModel(); try {
	 * log.info("START getTableResult "); for (ShutOutCargoVo object : list) {
	 * topsModel.put(object); }
	 * 
	 * tableData.setListData(topsModel); tableResult.setData(tableData); } catch
	 * (Exception e) { e.printStackTrace(); log.info("EXCEPTION IN PAGINATION"); }
	 * finally { log.info("END getTableResult "); } return tableResult; }
	 */
	
	
	// sg.com.jp.dpe.action-->CargoEnquiryMgmtAction-->getRecord
	@ApiOperation(value = "getRecord", response = String.class)
	@PostMapping(value = "/getRecord")
	public ResponseEntity<?> getRecord(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		CargoEnquiryDetails cargoEnq = new CargoEnquiryDetails();
		try {
			log.info("** getRecord Start criteria :" + criteria.toString());
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
			cargoEnq = shutoutCargoReportService.getCargoEnquiryRecord(edoNbr, esnNbr, type);
			
		} catch (BusinessException be) {
			log.info("Exception getRecord: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception getRecord : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(cargoEnq);
				result.setSuccess(true);
			}
			log.info("END: getRecord result" + result.toString());

		}
		return ResponseEntityUtil.success(result);
	}
		
	// Added by Hariz 12-08-2022
	@ApiOperation(value = "Get Company", response = String.class)
	@RequestMapping(value = "/getCompanyCode", method = RequestMethod.POST)
	public ResponseEntity<?> getCompanyCode(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** getCompanyCode Start criteria :" + criteria.toString());
			String cocode = CommonUtility.deNull(criteria.getPredicates().get("companyCode")).trim().toUpperCase();
			map.put("coCd", cocode);
		}catch (Exception e) {
			log.info("Exception getCompanyCode : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			log.info("END: getCompanyCode result:" + result.toString());
			
		}

		return ResponseEntityUtil.success(result.toString());
	}
}
