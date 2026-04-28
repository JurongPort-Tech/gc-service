package sg.com.jp.generalcargo.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

import sg.com.jp.generalcargo.domain.BerthUtilisationValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.service.GCOpsBerthProductivityReportService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = GCOpsBerthProductivityReportController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class GCOpsBerthProductivityReportController {

	public static final String ENDPOINT = "gc/gcOps";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";

	private static final Log log = LogFactory.getLog(GCOpsBerthProductivityReportController.class);
	String errorMessage = null;

	@Autowired
	private GCOpsBerthProductivityReportService berthUtilizeService;

	private static final String dateFormat = "dd/MM/yyyy";

	/**
	 * This function rounds the value to the desired no.of decimal places used in
	 * html
	 * 
	 * @param val
	 * @param decimalPlaces
	 * @return String
	 */
	private String roundTo(double val, int decimalPlaces) {
		try {
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			String str = decimalFormat.format(val);
			return str;
		} catch (Exception e) {
			return "str";
		}
	}

	// delegate.helper.reports-->BerthUtilisationReportHandler

	@PostMapping(value = "/berthUtilisationReport")
	public ResponseEntity<?> berthUtilisationReport(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		TableResult tableResult = new TableResult();
		TopsModel topsModel = new TopsModel();
		String fromDate = null;
		String toDate = null;
		String tonnage = null;
		String reportType = null;
		Integer noOfRowsPerPage = 0;
		try {
			log.info("START: perform criteria:" + criteria.toString());
			fromDate = CommonUtility.deNull(criteria.getPredicates().get("fromDate"));
			toDate = CommonUtility.deNull(criteria.getPredicates().get("toDate"));
			tonnage = CommonUtility.deNull(criteria.getPredicates().get("tonnage"));
			reportType = CommonUtility.deNull(criteria.getPredicates().get("reportType"));

			// Storing The Objects in The Request Object
			map.put("fromDate", fromDate);
			map.put("toDate", toDate);
			map.put("tonnage", tonnage);
			map.put("reportType", reportType);

			if (fromDate == null || toDate == null || reportType == null) {
				this.BerthUtilisationReport(criteria, topsModel, map);

			}
			Date fromDt = new SimpleDateFormat(dateFormat).parse(fromDate);
			Date toDt = new SimpleDateFormat(dateFormat).parse(toDate);

			log.info("noOfRowsPerPage" + noOfRowsPerPage);
			if (criteria.getPredicates().get("noRowsPerPage") == null) {
				noOfRowsPerPage = berthUtilizeService.getPaginationRecordsNumber();
				map.put("noRowsPerPage", noOfRowsPerPage);
			} else {
				noOfRowsPerPage = (Integer) request.getAttribute("noRowsPerPage");
			}
			java.util.List<BerthUtilisationValueObject> berthUtilList = new java.util.ArrayList<BerthUtilisationValueObject>();

			log.info("noOfRowsPerPage" + noOfRowsPerPage);
			if ("html".equals(reportType)) {
				topsModel = new TopsModel();

				String mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));

				// About to call EJB
				if ((mode != null && "SUBMIT".equalsIgnoreCase(mode.trim()))) {
					criteria.addPredicate("excel", "false");
					tableResult = berthUtilizeService.getBerthUtilisationRpt(fromDt, toDt, tonnage, criteria); // retrieve
																												// all
																												// the
																												// data

					int size = tableResult.getData().getListData().getTopsModel().size();
					BerthUtilisationValueObject objList = null;
					for (int i = 0; i < size; i++) {
						objList = (BerthUtilisationValueObject) tableResult.getData().getListData().getTopsModel()
								.get(i);
						berthUtilList.add(objList);
						BerthUtilisationValueObject berthValObject = (BerthUtilisationValueObject) berthUtilList.get(i);
						topsModel.put(berthValObject);
					}

				}

				map.put("reportType", reportType);
				map.put("topsModel", topsModel);
				map.put("total", tableResult.getData().getTotal());
				// report type is excel
			} else if ("excel".equals(reportType)) {
				List<List<String>> berthUtilListvector = new ArrayList<List<String>>();

				criteria.addPredicate("excel", "true");
				tableResult = berthUtilizeService.getBerthUtilisationRpt(fromDt, toDt, tonnage, criteria); // retrieve
																											// all the
																											// data

				int size = tableResult.getData().getListData().getTopsModel().size();
				BerthUtilisationValueObject objList = null;
				for (int i = 0; i < size; i++) {
					objList = (BerthUtilisationValueObject) tableResult.getData().getListData().getTopsModel().get(i);
					berthUtilList.add(objList);

				}

				// retrieve all the data
				// Got List from EJB: excel
				for (int i = 0; i < berthUtilList.size(); i++) {
					BerthUtilisationValueObject reportsVO = (BerthUtilisationValueObject) berthUtilList.get(i);
					// get all the Objects from the VO and add objects to the
					// Vector
					List<String> berthUtilReportList = new ArrayList<String>();

					berthUtilReportList.add(reportsVO.getBerthNumber());
					berthUtilReportList.add(String.valueOf(reportsVO.getBerthLength()));
					berthUtilReportList.add(String.valueOf(roundTo(reportsVO.getTonnageGC(), 2)));
					berthUtilReportList.add(String.valueOf(roundTo(reportsVO.getTonnageBC(), 2)));
					berthUtilReportList.add(String.valueOf(roundTo(reportsVO.getTonnageCNTR(), 2)));
					berthUtilReportList.add(String.valueOf(roundTo(reportsVO.getTotalTonnage(), 2)));
					berthUtilReportList.add(String.valueOf(reportsVO.getVesselCount()));
					berthUtilReportList.add(String.valueOf(roundTo(reportsVO.getTotalDurationOfVesselStay(), 2)));
					berthUtilReportList.add(String.valueOf(roundTo(reportsVO.getAverageTonnageHandledPerHour(), 2)));
					berthUtilReportList.add(String.valueOf(roundTo(reportsVO.getAverageTonnageHandledPerDay(), 2)));
					berthUtilReportList.add(String.valueOf(roundTo(reportsVO.getAverageTonnageHandledPerVessel(), 2)));

					berthUtilListvector.add(berthUtilReportList);
				}
				// Set the lists to the request
				map.put("berthUtilListvector", berthUtilListvector);
				map.put("printing", "YES");
			}
			// end pagination
			this.BerthUtilisationReport(criteria, topsModel, map);
		} catch (BusinessException e) {
			log.error("Exception: berthUtilisationReport ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		}catch (Exception e) {
			log.error("Exception: berthUtilisationReport",e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: berthUtilisationReport result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	private void BerthUtilisationReport(Criteria criteria, TopsModel topsModel, Map<String, Object> map) {
		String reportType = "";
		try {
			log.info("START: BerthUtilisationReport criteria:" + criteria.toString());
			reportType = CommonUtility.deNull(criteria.getPredicates().get("reportType"));
			if (null == reportType || "".equals(reportType)) {
				return;
			} else {
				// if report type is excel
				if ("excel".equalsIgnoreCase(reportType.trim())) {
					return;
				} else if ("html".equalsIgnoreCase(reportType.trim())) {
					// is report type is html
					List<List<String>> berthUtilListvector = new ArrayList<List<String>>();
					// Got List from EJB: html
					for (int i = 0; i < topsModel.getSize(); i++) {
						BerthUtilisationValueObject reportsVO = (BerthUtilisationValueObject) topsModel.get(i);
						List<String> berthUtilList = new ArrayList<String>();

						// rounding function appling to values objects
						// add List elements in a Vector
						berthUtilList.add(reportsVO.getBerthNumber());
						berthUtilList.add(String.valueOf(reportsVO.getBerthLength()));
						berthUtilList.add(String.valueOf(roundTo(reportsVO.getTonnageGC(), 2)));
						berthUtilList.add(String.valueOf(roundTo(reportsVO.getTonnageBC(), 2)));
						berthUtilList.add(String.valueOf(roundTo(reportsVO.getTonnageCNTR(), 2)));
						berthUtilList.add(String.valueOf(roundTo(reportsVO.getTotalTonnage(), 2)));
						berthUtilList.add(String.valueOf(reportsVO.getVesselCount()));
						berthUtilList.add(String.valueOf(roundTo(reportsVO.getTotalDurationOfVesselStay(), 2)));
						berthUtilList.add(String.valueOf(roundTo(reportsVO.getAverageTonnageHandledPerHour(), 2)));
						berthUtilList.add(String.valueOf(roundTo(reportsVO.getAverageTonnageHandledPerDay(), 2)));
						berthUtilList.add(String.valueOf(roundTo(reportsVO.getAverageTonnageHandledPerVessel(), 2)));
						berthUtilListvector.add(berthUtilList);
					}
					map.put("berthUtilListvector", berthUtilListvector);
				}

			}

		} catch (Exception e) {
			log.error("Exception: BerthUtilisationReport", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			log.info("END: BerthUtilisationReport ");
		}

	}

}
