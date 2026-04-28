package sg.com.jp.generalcargo.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VslProductivityValueObject;
import sg.com.jp.generalcargo.service.GCOpsVesselProductivityService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = GCOpsVesselProductivityController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class GCOpsVesselProductivityController {

	public static final String ENDPOINT = "gc/gcOps";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";

	private static final Log log = LogFactory.getLog(GCOpsVesselProductivityController.class);
	String errorMessage = null;

	@Autowired
	private GCOpsVesselProductivityService vesselProdListService;

	// delegate.helper.reports-->VesselProdReportHandler
	@PostMapping(value = "/vesselProdReport")
	public ResponseEntity<?> vesselProdReport(HttpServletRequest request) throws BusinessException {
		TopsModel topsModel = new TopsModel();
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		TableResult tableresult = new TableResult();
		try {
			log.info("START: vesselProdReport criteria:" + criteria.toString());

			String fromDate = null;
			String toDate = null;
			String reportFormat = null;
			// String category = null;
			String tonnage = null;
			String displayView = null;
			String rainRecord = null;
			String category[] = null;
			// Added by Punitha on 03/09/2008
			String vesselType = null;
			// End
			// get the parameters
			fromDate = CommonUtility.deNull(criteria.getPredicates().get("fromDate"));
			toDate = CommonUtility.deNull(criteria.getPredicates().get("toDate"));
			displayView = CommonUtility.deNull(criteria.getPredicates().get("reportType"));
			// reportFormat -excel or html
			reportFormat = CommonUtility.deNull(criteria.getPredicates().get("viewType"));
			category = request.getParameterValues("category");
			tonnage = CommonUtility.deNull(criteria.getPredicates().get("tonnage"));
			rainRecord = CommonUtility.deNull(criteria.getPredicates().get("rainRecord"));
			// Added by Punitha on 03/09/2008
			vesselType = CommonUtility.deNull(criteria.getPredicates().get("vesselType"));
			// LogManager.instance.logDebug("tonnage :"+tonnage);
			// LogManager.instance.logDebug("Vessel Type :"+vesselType);
			// LogManager.instance.logDebug("Category :"+category);
			// send to jsp initially

			// MC Consulting
			String dateType = CommonUtility.deNull(criteria.getPredicates().get("dateType"));
			if (fromDate == null || toDate == null || reportFormat == null || displayView == null) {
				// nextScreen(request, "vesselProdReport");
				// return;
			}

			// set the selected view type-Management or Operations
			map.put("displayView", displayView);
			map.put("fromDate", fromDate);
			map.put("toDate", toDate);
			map.put("category", category);
			map.put("tonnage", tonnage);
			map.put("rainRecord", rainRecord);
			map.put("reportFormat", reportFormat);
			// Added by Punitha on 03/09/2008
			map.put("vesselType", vesselType);
			// End
			// MC Consulting
			map.put("dateType", dateType);
			String colNames = "";
			// coloumn names to be diplayed if- operations report type is selecte
			/*
			 * String colNames =
			 * "'VSL_VOY','AGENT','STEVEDORE','SCHEME','LINER','LOA','BERTH','GANG_NBR','HATCH_NBR','ATB','ATU','GB_FIRST_ACT_DTTM','GB_LAST_ACT_DTTM','TIME_AT_BERTH','CARGO_TON_DISC','CARGO_TON_LOAD','CARGO_TON','CNTR_TON','TOTAL_TON',"
			 * +
			 * "'BULK_TON','CARGO_CAT','BENCHMARK','GROSSTONSPERHOUR','TONSPERHOUR','TONSPERGANGHOUR','DAILYRATE','DLYRATEMTRLOA','ALLOC_WORKHR','ACT_WORKHR','ALLOC_LAST_CARGO_DTTM','ALLOC_LAST_CARGO_GRACE_DTTM','ACT_LAST_CARGO_DTTM','TIME_EXCEEDED','PROD_SURCHARGE','CARGO_TYPE','TIMEATBERTH','ACTWORKHOUR'";
			 */

			// LogManager.instance.logInfo("VesselProdReportHandler.perform()");
			List<VslProductivityValueObject> vesselProdReportList = null;

			java.util.Date fromDt = new SimpleDateFormat("dd/MM/yyyy").parse(fromDate);
			java.util.Date toDt = new SimpleDateFormat("dd/MM/yyyy").parse(toDate);

			if (displayView.equalsIgnoreCase("Operations")) {
				colNames = "'VSL_VOY','AGENT','STEV_CO_NM','SCHEME','LINER','LOA','BERTH','GANG_SUPPLIED','HATCHES_WORKED','ATB','ATU',"
						+ "'GB_FIRST_ACT_DTTM','GB_LAST_ACT_DTTM','TIME_AT_BERTH','CARGO_DISCHARGE','CARGO_LOAD','TOTALCARGO',"
						// Added by Punitha on 12/01/2010
						+ "'DISC_CNTR_TON','LOAD_CNTR_TON',"
						// End
						+ "'CNTR_TON','TOTAL_TON',"
						+ "'BULK_TON','CARGO_CAT','BENCHMARK','GROSSTONSPERHOUR','TONSPERHOUR','TONSPERGANGHOUR',"
						+ "'DAILY_RATE','DLYRATEMTRLOA','ALLOC_WORKHR','ACT_WORKHR','ALLOC_LAST_CARGO_DTTM','ALLOC_LAST_CARGO_GRACE_DTTM',"
						+ "'ACT_LAST_CARGO_DTTM','TIME_EXCEEDED','PROD_SURCHARGE','CARGO_TYPE','TIMEATBERTH',"
						// Added by Punitha on 11/01/2010
						+ "'TIMEATWORK',"
						// End
						+ "'ACTWORKHOUR',"
						// Added by Punitha on 11/01/2010
						// Added by Jacky on 13/07/2010 SL-OPS-20100713-01 Rain hours be included in the
						// Vsl Productivity Rpt
						+ "'FLOAT_CRANE_IND','HLIFT_OVERSIDE','VSL_TYPE','RAIN_HR'";
				// End
			} else if (displayView.equalsIgnoreCase("Management")) {
				colNames = vesselProdListService.getColNames();

			}
			map.put("colNames", colNames);
			// Report format =HTML
			if ("html".equals(reportFormat)) {
				// start pagination
				VslProductivityValueObject reportsVO = new VslProductivityValueObject();
				String mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
				log.info("html" + reportFormat);
				// Added by Punitha on 11/12/2008
				if (mode != null && "SUBMIT".equalsIgnoreCase(mode.trim())) {

					// LogManager.instance.logDebug("Vessel Type :"+vesselType);
					criteria.addPredicate("excel", "false");
					tableresult = vesselProdListService.getVesselProdRpt(fromDt, toDt, displayView, category, tonnage,
							rainRecord, vesselType, dateType, criteria);
					int size = tableresult.getData().getListData().getTopsModel().size();
					VslProductivityValueObject objBatchList = null;
					vesselProdReportList = new ArrayList<VslProductivityValueObject>();
					for (int i = 0; i < size; i++) {
						objBatchList = (VslProductivityValueObject) tableresult.getData().getListData().getTopsModel()
								.get(i);
						vesselProdReportList.add(objBatchList);
					}

					for (int i = 0; i < vesselProdReportList.size(); i++) {
						reportsVO = (VslProductivityValueObject) vesselProdReportList.get(i);
						topsModel.put(reportsVO);
					}
				}
				map.put("listData", topsModel);
				map.put("total", tableresult.getData().getTotal());

				// End
			} else if ("excel".equals(reportFormat)) {// Format = excel
				// List<VslProductivityValueObject> vesselProdListVector = new
				// ArrayList<VslProductivityValueObject>();
				// Get the reports data as an arraylist
				// Amended by Punitha on 03/09/2008.
				/*
				 * vesselProdReportList = (ArrayList) VesselProdEjb .getVesselProdRpt(fromDt,
				 * toDt, reportFormat, category, tonnage, rainRecord);
				 */
				criteria.addPredicate("excel", "true");
				tableresult = vesselProdListService.getVesselProdRpt(fromDt, toDt, reportFormat, category, tonnage,
						rainRecord, vesselType, dateType, criteria);

				int size = tableresult.getData().getListData().getTopsModel().size();
				VslProductivityValueObject objBatchList = null;
				vesselProdReportList = new ArrayList<VslProductivityValueObject>();
				for (int i = 0; i < size; i++) {
					objBatchList = (VslProductivityValueObject) tableresult.getData().getListData().getTopsModel()
							.get(i);
					vesselProdReportList.add(objBatchList);
				}

				map.put("vesselProdReportList", vesselProdReportList);
				map.put("printing", "YES");
				this.VesselProdReport(criteria, map, vesselProdReportList, topsModel);
				// End
			}
			this.VesselProdReport(criteria, map, vesselProdReportList, topsModel);

		} catch (BusinessException e) {
			log.error("Exception vesselProdReport : ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception vesselProdReport : ", e);
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
				log.info("END: vesselProdReport result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	private void VesselProdReport(Criteria criteria, Map<String, Object> map,
			List<VslProductivityValueObject> vesselProdReportList, TopsModel topsModel) throws BusinessException {
		String reportFormat = CommonUtility.deNull(criteria.getPredicates().get("viewType"));

		if (null == reportFormat || "".equals(reportFormat)) {
			return;
		} else if ("excel".equalsIgnoreCase(reportFormat.trim())) {
			try {
				// add the value object data to list and put it in the
				// vector and set
				// it in the request scope
				List<Object> vesselProdListVector = new ArrayList<Object>();

				for (int i = 0; i < vesselProdReportList.size(); i++) {
					// Get the VO
					VslProductivityValueObject reportsVO = (VslProductivityValueObject) vesselProdReportList.get(i);
					List<Object> vesselProdList = new ArrayList<Object>();
					vesselProdList.add(reportsVO.getVvCode());
					vesselProdList.add(reportsVO.getVesselName());
					vesselProdList.add(reportsVO.getAgent());
					vesselProdList.add(CommonUtility.deNull(reportsVO.getStevedore()));
					vesselProdList.add(reportsVO.getScheme());
					vesselProdList.add(reportsVO.getLiner());
					vesselProdList.add(reportsVO.getLOA());
					vesselProdList.add(reportsVO.getBerth());
					vesselProdList.add(new Integer(reportsVO.getGangsSupplied()));
					vesselProdList.add(new Integer(reportsVO.getHatchNbr()));
					vesselProdList.add(reportsVO.getATB());
					vesselProdList.add(reportsVO.getATU());
					// Added by Punitha on 21/04/2009
					vesselProdList.add(reportsVO.getGbFirstActDttm());
					vesselProdList.add(reportsVO.getGbLastActDttm());
					String berthHour = "";
					String timeAtBerth = "";
					// Added by Punitha on 11/01/2010
					String timeAtWork = "";
					// End
					String actWorkHr = "";
					String day = "";
					String hour = "";
					int days = 0;
					int hours = 0;
					double minutes = 0.0;
					String berthTiming = "";
					try {
						timeAtBerth = String.valueOf(roundTo(reportsVO.getBerthHr(), 2));
						// Added by Punitha on 11/01/2010
						timeAtWork = String.valueOf(roundTo(reportsVO.getTimeAtWork(), 2));
						// End
						day = timeAtBerth.substring(0, timeAtBerth.indexOf("."));
						hour = timeAtBerth.substring(timeAtBerth.indexOf("."));
						days = Integer.parseInt(day) / 24;
						hours = Integer.parseInt(day) % 24;
						minutes = Double.parseDouble(hour) * 60;
						berthTiming = String.valueOf(days) + ":" + String.valueOf(hours) + ":"
								+ String.valueOf((int) minutes);
					} catch (Exception e) {
					}

					vesselProdList.add(berthTiming);
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getCargoDischargeTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getCargoDischargeTonnage(), 2)));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getGenCargoLoadTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getGenCargoLoadTonnage(), 2)));
					}
					// vesselProdList.add(String.valueOf(reportsVO.getBerthHr()));
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getCargoTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getCargoTonnage(), 2)));
					}

					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getCntrTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getCntrTonnage(), 2)));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getTotalTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getTotalTonnage(), 2)));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getBulkTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getBulkTonnage(), 2)));
					}
					vesselProdList.add(reportsVO.getCargoCategory());

					vesselProdList.add(new Integer(reportsVO.getBenchmark()));

					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getGrossTonsPerHour(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getGrossTonsPerHour(), 2)));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getTonsPerHour(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getTonsPerHour(), 2)));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getTonsPerGangHour(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getTonsPerGangHour()));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getDailyRateGeneralCargo(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getDailyRateGeneralCargo(), 2)));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getDailyRateMtrLOA(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getDailyRateMtrLOA()));
					}
					berthHour = "";
					day = "";
					hour = "";
					days = 0;
					hours = 0;
					minutes = 0.0;
					String allocHr = "";
					try {
						// berthHour = String.valueOf(roundTo(reportsVO.getWorkHours(),2));
						berthHour = String.valueOf(reportsVO.getWorkHours());
						// LogManager.instance.logInfo("Handler work hr :"+berthHour);
						day = berthHour.substring(0, berthHour.indexOf("."));
						hour = berthHour.substring(berthHour.indexOf("."));
						days = Integer.parseInt(day) / 24;
						hours = Integer.parseInt(day) % 24;
						minutes = Double.parseDouble(hour) * 60;
						allocHr = String.valueOf(days) + ":" + String.valueOf(hours) + ":"
								+ String.valueOf((int) minutes);
						// LogManager.instance.logInfo("Tming :"+berthTiming);
					} catch (Exception e) {
					}

					vesselProdList.add(allocHr);

					berthHour = "";
					day = "";
					hour = "";
					days = 0;
					hours = 0;
					minutes = 0.0;
					String actHr = "";
					try {
						actWorkHr = String.valueOf(roundTo(reportsVO.getActualWorkHr(), 2));
						berthHour = String.valueOf(reportsVO.getActualWorkHr());
						day = actWorkHr.substring(0, actWorkHr.indexOf("."));
						hour = actWorkHr.substring(actWorkHr.indexOf("."));
						days = Integer.parseInt(day) / 24;
						hours = Integer.parseInt(day) % 24;
						minutes = Double.parseDouble(hour) * 60;
						actHr = String.valueOf(days) + ":" + String.valueOf(hours) + ":"
								+ String.valueOf((int) minutes);
						// LogManager.instance.logInfo("Tming :"+berthTiming);
					} catch (Exception e) {
					}

					vesselProdList.add(actHr);

					vesselProdList.add(reportsVO.getLastCargoDttm());
					vesselProdList.add(reportsVO.getLastCargoGraceDttm());
					vesselProdList.add(reportsVO.getActualCargoDttm());
//					Added by Punitha on 21/04/2009
					try {
						if ("Yes".equals(reportsVO.getProdSurcharge()))
							vesselProdList.add(String.valueOf(roundTo(reportsVO.getTimeExceeded(), 2)));
						else
							vesselProdList.add("");
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getTimeExceeded(), 2)));
					}
					vesselProdList.add(reportsVO.getProdSurcharge());
					vesselProdList.add(reportsVO.getCargoType());
					vesselProdList.add(timeAtBerth);
					// Added by Punitha on 11/01/2010
					vesselProdList.add(timeAtWork);
					// End
					vesselProdList.add(actWorkHr);
					// Added by Punitha on 11/01/2010
					vesselProdList.add(reportsVO.getFloatCraneInd());
					vesselProdList.add(reportsVO.getHeavyLiftOverside());
					vesselProdList.add(reportsVO.getVesselType());
					// End
					// Added by Jacky on 13/07/2010 SL-OPS-20100713-01 Rain hours be included in the
					// Vsl Productivity Rpt
					vesselProdList.add(String.valueOf(reportsVO.getRainHours()));
					// End
					// Added by Punitha on 12/01/2010
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getDiscCntrTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getDiscCntrTonnage(), 2)));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getLoadCntrTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getLoadCntrTonnage(), 2)));
					}
					// End
					vesselProdListVector.add(vesselProdList);
				}

				map.put("vesselProdListVector", vesselProdListVector);
			} catch (Exception e) {
				log.error("Exception VesselProdReport : ", e);
			}
			// String redirectName = "/JSP/reports/VesselProductivityReport.jsp";
			// response.setHeader("Content-disposition",
			// "attachment;filename=VesselProductivityReport.xls");
			// redirect(request, response, redirectName);

		} else if ("html".equalsIgnoreCase(reportFormat.trim())) {
			try {
				// add the value object data to list and put it in the
				// vector and set
				// it in the request scope
				List<Object> vesselProdListVector = new ArrayList<Object>();
				log.info("Tops Model size :" + topsModel.getSize());
				for (int i = 0; i < topsModel.getSize(); i++) {
					VslProductivityValueObject reportsVO = (VslProductivityValueObject) topsModel.get(i);
					List<Object> vesselProdList = new ArrayList<Object>();
					vesselProdList.add(reportsVO.getVvCode());
					vesselProdList.add(reportsVO.getVesselName());
					vesselProdList.add(reportsVO.getAgent());
					vesselProdList.add(CommonUtility.deNull(reportsVO.getStevedore()));
					vesselProdList.add(reportsVO.getScheme());
					vesselProdList.add(reportsVO.getLiner());
					vesselProdList.add(reportsVO.getLOA());
					vesselProdList.add(reportsVO.getBerth());
					vesselProdList.add(new Integer(reportsVO.getGangsSupplied()));
					vesselProdList.add(new Integer(reportsVO.getHatchNbr()));
					vesselProdList.add(reportsVO.getATB());
					vesselProdList.add(reportsVO.getATU());
					// Added by Punitha on 21/04/2009
					vesselProdList.add(reportsVO.getGbFirstActDttm());
					vesselProdList.add(reportsVO.getGbLastActDttm());
					String berthHour = "";
					String timeAtBerth = "";
					// Added by Punitha on 11/01/2010
					String timeAtWork = "";
					// End
					String actWorkHr = "";
					String day = "";
					String hour = "";
					int days = 0;
					int hours = 0;
					double minutes = 0.0;
					String berthTiming = "";
					try {
						timeAtBerth = String.valueOf(roundTo(reportsVO.getBerthHr(), 2));
						// Added by Punitha on 11/01/2010
						timeAtWork = String.valueOf(roundTo(reportsVO.getTimeAtWork(), 2));
						// End
						day = timeAtBerth.substring(0, timeAtBerth.indexOf("."));
						hour = timeAtBerth.substring(timeAtBerth.indexOf("."));
						days = Integer.parseInt(day) / 24;
						hours = Integer.parseInt(day) % 24;
						minutes = Double.parseDouble(hour) * 60;
						berthTiming = String.valueOf(days) + ":" + String.valueOf(hours) + ":"
								+ String.valueOf((int) minutes);
					} catch (Exception e) {
					}

					vesselProdList.add(berthTiming);

					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getCargoDischargeTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getCargoDischargeTonnage()));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getGenCargoLoadTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getGenCargoLoadTonnage()));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getCargoTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getCargoTonnage()));
					}

					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getCntrTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getCntrTonnage()));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getTotalTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getTotalTonnage()));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getBulkTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getBulkTonnage()));
					}
					vesselProdList.add(reportsVO.getCargoCategory());
					vesselProdList.add(new Integer(reportsVO.getBenchmark()));
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getGrossTonsPerHour(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getGrossTonsPerHour()));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getTonsPerHour(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getTonsPerHour()));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getTonsPerGangHour(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getTonsPerGangHour()));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getDailyRateGeneralCargo(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getDailyRateGeneralCargo()));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getDailyRateMtrLOA(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getDailyRateMtrLOA()));
					}
					berthHour = "";
					day = "";
					hour = "";
					days = 0;
					hours = 0;
					minutes = 0.0;
					String allocHr = "";
					try {
						// berthHour = String.valueOf(roundTo(reportsVO.getWorkHours(),2));
						berthHour = String.valueOf(reportsVO.getWorkHours());
						// LogManager.instance.logInfo("Handler work hr :"+berthHour);
						day = berthHour.substring(0, berthHour.indexOf("."));
						hour = berthHour.substring(berthHour.indexOf("."));
						days = Integer.parseInt(day) / 24;
						hours = Integer.parseInt(day) % 24;
						minutes = Double.parseDouble(hour) * 60;
						allocHr = String.valueOf(days) + ":" + String.valueOf(hours) + ":"
								+ String.valueOf((int) minutes);
						// LogManager.instance.logInfo("Tming :"+berthTiming);
					} catch (Exception e) {
					}

					vesselProdList.add(allocHr);

					berthHour = "";
					day = "";
					hour = "";
					days = 0;
					hours = 0;
					minutes = 0.0;
					String actHr = "";
					try {
						actWorkHr = String.valueOf(roundTo(reportsVO.getActualWorkHr(), 2));
						berthHour = String.valueOf(reportsVO.getActualWorkHr());
						day = berthHour.substring(0, berthHour.indexOf("."));
						hour = berthHour.substring(berthHour.indexOf("."));
						days = Integer.parseInt(day) / 24;
						hours = Integer.parseInt(day) % 24;
						minutes = Double.parseDouble(hour) * 60;
						actHr = String.valueOf(days) + ":" + String.valueOf(hours) + ":"
								+ String.valueOf((int) minutes);
						// LogManager.instance.logInfo("Tming :"+berthTiming);
					} catch (Exception e) {
					}

					vesselProdList.add(actHr);
					vesselProdList.add(reportsVO.getLastCargoDttm());
					vesselProdList.add(reportsVO.getLastCargoGraceDttm());
					vesselProdList.add(reportsVO.getActualCargoDttm());
					try {
						if ("Yes".equals(reportsVO.getProdSurcharge()))
							vesselProdList.add(String.valueOf(roundTo(reportsVO.getTimeExceeded(), 2)));
						else
							vesselProdList.add("");
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getTimeExceeded()));
					}
					vesselProdList.add(reportsVO.getProdSurcharge());
					vesselProdList.add(reportsVO.getCargoType());
					vesselProdList.add(timeAtBerth);
					// Added by Punitha on 11/01/2010
					vesselProdList.add(timeAtWork);
					// End
					vesselProdList.add(actWorkHr);
					// Added by Punitha on 11/01/2010
					vesselProdList.add(reportsVO.getFloatCraneInd());
					vesselProdList.add(reportsVO.getHeavyLiftOverside());
					vesselProdList.add(reportsVO.getVesselType());
					// End
					// Added rain hours. 29/6/2010.
					vesselProdList.add(String.valueOf(reportsVO.getRainHours()));
					// End
					// Added by Punitha on 12/01/2010
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getDiscCntrTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getDiscCntrTonnage()));
					}
					try {
						vesselProdList.add(String.valueOf(roundTo(reportsVO.getLoadCntrTonnage(), 2)));
					} catch (Exception e) {
						vesselProdList.add(String.valueOf(reportsVO.getLoadCntrTonnage()));
					}
					// End
					vesselProdListVector.add(vesselProdList);
				}
				map.put("vesselProdListVector", vesselProdListVector);

			} catch (Exception e) {
				log.error("Exception perform : ", e);
				throw new BusinessException("M4201");
			}
		}

	}

	private String roundTo(double val, int decimalPlaces) throws Exception {
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String value = decimalFormat.format(val);
		return value;

	}


}
