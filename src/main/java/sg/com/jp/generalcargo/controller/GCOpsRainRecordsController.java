package sg.com.jp.generalcargo.controller;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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
import sg.com.jp.generalcargo.domain.RainRecordsValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.service.GCOpsRainRecordsService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = GCOpsRainRecordsController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class GCOpsRainRecordsController {

	public static final String ENDPOINT = "gc/gcOps";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";

	private static final Log log = LogFactory.getLog(GCOpsRainRecordsController.class);
	String errorMessage = null;

	@Autowired
	private GCOpsRainRecordsService rainRecordsService;	

	//delegate.helper.ops.RainRecords -->RainRecordsHandler
	@PostMapping(value = "/rainRecords")
	public ResponseEntity<?> rainRecords(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START: rainRecords criteria:" + criteria.toString());

			String currUser = "";
			currUser=CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String location = ( CommonUtility.deNull(criteria.getPredicates().get("location")) != null) ? (String) request
					.getParameter("location") : "";

			String selectedRainLocation = ( CommonUtility.deNull(criteria.getPredicates().get("rainLocation")) != null) ? (String) request
					.getParameter("rainLocation") : "";

			String mode = ( CommonUtility.deNull(criteria.getPredicates().get("mode")) != null) ?  CommonUtility.deNull(criteria.getPredicates().get("mode")) : "";

			String modeValue = ( CommonUtility.deNull(criteria.getPredicates().get("modeValue")) != null) ?  CommonUtility.deNull(criteria.getPredicates().get("modeValue"))
					: "";

			String listFromDate = ( CommonUtility.deNull(criteria.getPredicates().get("viewFromDate") )!= null) ?  CommonUtility.deNull(criteria.getPredicates().get("viewFromDate"))
					: "";
			String listToDate = ( CommonUtility.deNull(criteria.getPredicates().get("viewToDate")) != null) ?  CommonUtility.deNull(criteria.getPredicates().get("viewToDate"))
					: "";

			if (listFromDate == null || listFromDate.equals("")) {
				listFromDate = ( CommonUtility.deNull(criteria.getPredicates().get("listFromDate")) != null) ?  CommonUtility.deNull(criteria.getPredicates().get("listFromDate"))
						: "";
			}
			if (listToDate == null || listToDate.equals("")) {
				listToDate = ( CommonUtility.deNull(criteria.getPredicates().get("listToDate")) != null) ?  CommonUtility.deNull(criteria.getPredicates().get("listToDate"))
						: "";
			}
			// userId =
			//  CommonUtility.deNull(criteria.getPredicates().get("selectedUserId")!=null? CommonUtility.deNull(criteria.getPredicates().get("selectedUserId"):"";
			log.info("In RainRecords Handler: Mode : " + mode
					+ "ModeValue : " + modeValue + " listFromDate:" + listFromDate
					+ " listToDate :" + listToDate);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdfTime = new SimpleDateFormat("kkmm");

			String currentDate = sdf.format(new Date(System.currentTimeMillis()));
			String currentTime = sdfTime
					.format(new Date(System.currentTimeMillis()));


			if (mode == null || mode.equals("")) {
				if (listFromDate == null || listFromDate.equals("")) {
					listFromDate = currentDate;
				}
			}
			if ( CommonUtility.deNull(criteria.getPredicates().get("mode")) != null) {
				mode = (String)  CommonUtility.deNull(criteria.getPredicates().get("mode"));
				log.info("mode from request attribute: " + mode);
				// session.removeAttribute("mode");
				// map.put("mode", "list");

			}
			log.info("listFromDate: " + listFromDate);
			map.put("listFromDate", listFromDate);
			log.info("listToDate: " + listToDate);
			map.put("listToDate", listToDate);

			String contentType =  CommonUtility.deNull(criteria.getPredicates().get("contentType"));
			map.put("contentType", contentType);

			map.put("location", location);

			map.put("currentDate", currentDate);
			map.put("currentTime", currentTime);
			map.put("mode", mode);
			log.info("mode: " + mode);
			map.put("modeValue", modeValue);

			log.info("RainRecords Handler currentTime: "
					+ currentTime);
			log.info("RainRecords Handler currentDate: "
					+ currentDate);

			Integer noOfRowPerPage = null;
			Integer editableDays = null;
			Integer totRainRec = null;
			int noRowsPerPage = 0;

			map.put("USERID", currUser);
			map.put("UserID", currUser);

			String pgIndex = CommonUtility.deNull(criteria.getPredicates().get("PageIndex"))!=null?  CommonUtility.deNull(criteria.getPredicates().get("PageIndex")):"";
			map.put("PageIndex", pgIndex);
			boolean bRefresh = false;
			String[] editDays = null;


			// RecordPaging
			


			if ( criteria.getPredicates().get("noOfRowPerPage") == null) {
				// Number of records to be displayed in the page is fetched from
				// the database here
				noOfRowPerPage = rainRecordsService.getPaginationRecordCount();
				map.put("noOfRowPerPage", noOfRowPerPage);
				noRowsPerPage = noOfRowPerPage.intValue();
			} else {
				noRowsPerPage =new Integer( criteria.getPredicates().get("noOfRowPerPage")).intValue();
				//noRowsPerPage = ((Integer) session.getAttribute("noOfRowPerPage")).intValue();
			}

			log.info("noRowsPerPage--------------------:"
					+ noRowsPerPage);

			// Number of days will be fetched from database to check whether a
			// record is updatable or not
			if (  CommonUtility.deNull(criteria.getPredicates().get("editableDays")).isEmpty()) {
				editableDays = rainRecordsService.getRainRecordEditableDays();
				map.put("editableDays", editableDays);
			} else {
				editableDays = new Integer(criteria.getPredicates().get("editableDays"));

			}
			// Total records count in the Raion_records table with Status_CD='A'
			// is fetched
			if ( CommonUtility.deNull(criteria.getPredicates().get("TOTRECCOUNT")).isEmpty()) {
				totRainRec = rainRecordsService.getAllRainRecordsCount();
				map.put("TOTRECCOUNT", totRainRec);
			} else {
				totRainRec =Integer.parseInt(criteria.getPredicates().get("TOTRECCOUNT"));

			}
			String editDay = "" + editableDays.intValue();
			editDays = new String[] { editDay };
			log.info("Editable Days in Handler---"
					+ editDays[0]);

			List<RainRecordsValueObject> rainRecordsListByDate = new ArrayList<RainRecordsValueObject>();

			/**
			 * When the user clicks on the List button getRainRecordsByDate
			 * method is called and all the records between the From and To
			 * dates are fetched from the database and are shown
			 */
			Map<String, String> rainRecCatList = rainRecordsService.getRainCategories();
			log.info("RainRecCatList : " + rainRecCatList);
			map.put("rainRecCatList", rainRecCatList);

			Map<String, String> rainRecLocationList = rainRecordsService.getRainLocations();
			log.info("rainRecLocationList : " + rainRecLocationList);
			map.put("rainRecLocationList", rainRecLocationList);


			//To export record as excel
			if(contentType!= null && contentType.equalsIgnoreCase("Excel")){
			
				
                TableResult tableResult = rainRecordsService.getRainRecordsByDate(listFromDate, listToDate, location, criteria);
				
				map.put("total", tableResult.getData().getTotal());
				int size = tableResult.getData().getListData().getTopsModel().size();
				RainRecordsValueObject objList = null; 
				for (int i = 0; i < size; i++) {
					objList = (RainRecordsValueObject) tableResult.getData().getListData().getTopsModel().get(i);
					rainRecordsListByDate.add(objList);
					RainRecordsValueObject rsValObject = (RainRecordsValueObject) rainRecordsListByDate.get(i);
					topsModel.put(rsValObject);
				}
				map.put("listData", topsModel);
				result.setData(map);
				result.setSuccess(true);
				log.info("END: perform result: " + result.toString());
				
				return ResponseEntityUtil.success(result.toString());
			}

			log.info("PageIndex : "
					+  CommonUtility.deNull(criteria.getPredicates().get("PageIndex")) + " pageIndex : "
					+  CommonUtility.deNull(criteria.getPredicates().get("pageIndex")));

			if (( criteria.getPredicates().get("PageIndex") == null)
					&& (modeValue == null || "".equals(modeValue))) {
				if (mode == null || "".equals(mode)
						|| "addNew".equalsIgnoreCase(mode)) {

					result.setData(map);
					result.setSuccess(true);
					log.info("END: perform result: " + result.toString());
					
					return ResponseEntityUtil.success(result.toString());
				}
			}
			if ("list".equalsIgnoreCase(mode)) {

				bRefresh = true;
			}
			/**
			 * When the user selects a rain record and clicks on the Update
			 * button isEditableRainRecord method is called which returns TRUE
			 * if the record is editable else throws BussinessException
			 */
			else if ("updateRecord".equalsIgnoreCase(mode)) {
				String rainCntr =  CommonUtility.deNull(criteria.getPredicates().get("selectedRainCNT"));

				boolean updateEditable = rainRecordsService.isEditableRainRecord(
						rainCntr, mode);

				if (updateEditable == false) {
					// throws the message that the user cant update this record
					log.info("Update********************************************************: "
							+ editDays);
					// errorMessage(request, "M50011", "request=rainRecords", ""
					// , editDays);getRainRecordsByDate

					map.put("mode", "list");
					map.put("PINDEX", "PINDEX");

					log.info("setting mode before exception"
							+  CommonUtility.deNull(criteria.getPredicates().get("mode")));
				
					throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get("M50011"), editDays));
				}

			}
			/**
			 * After the user enteres the data in Add/Update JSP and submits the
			 * form modeValue is set to 'add'. In this block addRainRecord EJB
			 * method is called to insert the record in the Database
			 *
			 */

			else if ("add".equalsIgnoreCase(modeValue)) {
				log.info("Inside add RainRecordsHandler");
				String startDate =  CommonUtility.deNull(criteria.getPredicates().get("fromDate")) != null ?  CommonUtility.deNull(criteria.getPredicates().get("fromDate"))
						: "";
				String startTime =  CommonUtility.deNull(criteria.getPredicates().get("TimeFrom")) != null ?  CommonUtility.deNull(criteria.getPredicates().get("TimeFrom"))
						: "";
				String endDate =  CommonUtility.deNull(criteria.getPredicates().get("toDate")) != null ?  CommonUtility.deNull(criteria.getPredicates().get("toDate"))
						: "";
				String endTime =  CommonUtility.deNull(criteria.getPredicates().get("TimeTo")) != null ?  CommonUtility.deNull(criteria.getPredicates().get("TimeTo"))
						: "";
				listFromDate =  CommonUtility.deNull(criteria.getPredicates().get("listFromDate"));
				listToDate =  CommonUtility.deNull(criteria.getPredicates().get("listToDate"));
				String rainCategoryCd =  CommonUtility.deNull(criteria.getPredicates().get("rainCategory")) != null ?  CommonUtility.deNull(criteria.getPredicates().get("rainCategory"))
						: "";

				map.put("listFromDate", listFromDate);
				map.put("listToDate", listToDate);
				map.put("location", selectedRainLocation);

				/**
				 * If any of the expected values are coming as null we will
				 * throw a business exception(UnExpected Exception)
				 */
				if (startDate == null || startDate.equals("")
						|| startTime == null || startTime.equals("")
						|| endDate == null || endDate.equals("")
						|| endTime == null || endTime.equals("")
						|| rainCategoryCd == null || rainCategoryCd.equals("")
						|| currUser == null || currUser.equals("")) {

					throw new BusinessException("M4101");
				}
				//Rain location parameter passed from selected drop down add/update screen
				boolean insert = rainRecordsService.addRainRecord(startDate,
						startTime, endDate, endTime, rainCategoryCd, currUser, selectedRainLocation);
				mode="list";
				bRefresh = true;
				if(insert){
					map.put("actionSuccess", "The record is inserted successfully");
				}
			} else if ("update".equalsIgnoreCase(modeValue)) {
				String startDate =  CommonUtility.deNull(criteria.getPredicates().get("hiddenfromDate")) != null ? (String)  CommonUtility.deNull(criteria.getPredicates().get("hiddenfromDate"))
						: "";
				String startTime =  CommonUtility.deNull(criteria.getPredicates().get("hiddenTimeFrom")) != null ? (String)  CommonUtility.deNull(criteria.getPredicates().get("hiddenTimeFrom"))
						: "";
				String endDate =  CommonUtility.deNull(criteria.getPredicates().get("hiddentoDate")) != null ? (String)  CommonUtility.deNull(criteria.getPredicates().get("hiddentoDate"))
						: "";
				String endTime =  CommonUtility.deNull(criteria.getPredicates().get("hiddenTimeTo")) != null ? (String)  CommonUtility.deNull(criteria.getPredicates().get("hiddenTimeTo"))
						: "";
				String rainCategoryCd =  CommonUtility.deNull(criteria.getPredicates().get("hiddenrainCategory")) != null ? (String)  CommonUtility.deNull(criteria.getPredicates().get("hiddenrainCategory"))
						: "";
				String rainCntrTxt =  CommonUtility.deNull(criteria.getPredicates().get("selectedRainCNT")) != null ? (String)  CommonUtility.deNull(criteria.getPredicates().get("selectedRainCNT"))
						: "";
				listFromDate =  CommonUtility.deNull(criteria.getPredicates().get("listFromDate"));
				listToDate =  CommonUtility.deNull(criteria.getPredicates().get("listToDate"));
				map.put("listFromDate", listFromDate);
				map.put("listToDate", listToDate);
				map.put("location", selectedRainLocation);

				log.info("startDate---in RainRecordsHandler (modeValue=update) block: "
						+ startDate);
				log.info("startTime---in RainRecordsHandler (modeValue=update) block: "
						+ startTime);
				log.info("endDate---in RainRecordsHandler (modeValue=update) block: "
						+ endDate);
				log.info("endTime---in RainRecordsHandler (modeValue=update) block: "
						+ endTime);
				log.info("rainCategoryCd---in RainRecordsHandler (modeValue=update) block: "
						+ rainCategoryCd);
				log.info("rainCntrTxt---in RainRecordsHandler (modeValue=update) block: "
						+ rainCntrTxt);
				log.info("listFromDate---in RainRecordsHandler (modeValue=update) block: "
						+ listFromDate);
				log.info("listToDate---in RainRecordsHandler (modeValue=update) block: "
						+ listToDate);

				/**
				 * If any of the expected values are coming as null we will
				 * throw a business exception(UnExpected Exception)
				 */

				if (startDate == null || startDate.equals("")
						|| startTime == null || startTime.equals("")
						|| endDate == null || endDate.equals("")
						|| endTime == null || endTime.equals("")
						|| rainCategoryCd == null || rainCategoryCd.equals("")
						|| rainCntrTxt == null || rainCntrTxt.equals("")
						|| currUser == null || currUser.equals("")) {

					throw new BusinessException("M4101");
				}
				String rainCntr = rainCntrTxt;

				boolean updateEditable = rainRecordsService.isEditableRainRecord(
						rainCntr, mode);
				log.info("updateEditable value in RainRecordsHandler for update(mode)------------------"
						+ updateEditable);
				String statusCd = "A";
				if (updateEditable == false) {
					// throws the message that the user cant update this record
					log.info("Update********************************************************: "
							+ editDays);
					map.put("mode", "list");
					map.put("PINDEX", "PINDEX");
					log.info("setting mode before exception"
							+  CommonUtility.deNull(criteria.getPredicates().get("mode")));
					throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get("M50011"), editDays));
				} else {
					//Rain location parameter passed from selected drop down add/update screen
					boolean update = rainRecordsService.updateRainRecord(rainCntr,
							startDate, startTime, endDate, endTime,
							rainCategoryCd, currUser, statusCd, selectedRainLocation);
					if(update){
						map.put("actionSuccess", "The record is updated successfully");

					}
				}
				mode="list";
				bRefresh = true;
			} else if ("delete".equalsIgnoreCase(mode)) {

				log.info("RainRecordsHandler.perform(-------mode(delete)-------)"
						+  CommonUtility.deNull(criteria.getPredicates().get("selectedFromDate")));
				log.info("RainRecordsHandler.perform(-------mode(delete)--------)"
						+  CommonUtility.deNull(criteria.getPredicates().get("selectedFromTime")));
				log.info("RainRecordsHandler.perform(-------mode(delete)--------)"
						+  CommonUtility.deNull(criteria.getPredicates().get("selectedToDate")));
				log.info("RainRecordsHandler.perform(-------mode(delete)--------)"
						+  CommonUtility.deNull(criteria.getPredicates().get("selectedToTime")));
				log.info("RainRecordsHandler.perform(--------mode(delete)-------)"
						+  CommonUtility.deNull(criteria.getPredicates().get("selectedRainCategoryText")));
				log.info("RainRecordsHandler.perform(-------mode(delete)--------)"
						+  CommonUtility.deNull(criteria.getPredicates().get("selectedRainCNT")));

				String startDate = ( CommonUtility.deNull(criteria.getPredicates().get("selectedFromDate")) != null) ?  CommonUtility.deNull(criteria.getPredicates().get("selectedFromDate"))
						: "";
				String startTime = ( CommonUtility.deNull(criteria.getPredicates().get("selectedFromTime")) != null) ?  CommonUtility.deNull(criteria.getPredicates().get("selectedFromTime"))
						: "";
				String endDate = ( CommonUtility.deNull(criteria.getPredicates().get("selectedToDate")) != null) ?  CommonUtility.deNull(criteria.getPredicates().get("selectedToDate"))
						: "";
				String endTime = ( CommonUtility.deNull(criteria.getPredicates().get("selectedToTime")) != null) ?  CommonUtility.deNull(criteria.getPredicates().get("selectedToTime"))
						: "";
				String rainCategoryCd = (request
						.getParameter("selectedRainCategoryCode") != null) ?  CommonUtility.deNull(criteria.getPredicates().get("selectedRainCategoryCode"))
								: "";
				String rainCntrTxt =  CommonUtility.deNull(criteria.getPredicates().get("selectedRainCNT")) != null ?  CommonUtility.deNull(criteria.getPredicates().get("selectedRainCNT"))
						: "";
				// listFromDate= ( CommonUtility.deNull(criteria.getPredicates().get("listFromDate") !=
				// null)?(String) CommonUtility.deNull(criteria.getPredicates().get("listFromDate") : "" ;
				// listToDate=( CommonUtility.deNull(criteria.getPredicates().get("listToDate") !=
				// null)?(String) CommonUtility.deNull(criteria.getPredicates().get("listToDate") :"";
				map.put("listFromDate", listFromDate);
				map.put("listToDate", listToDate);

				/**
				 * If any of the expected values are coming as null we will
				 * throw a business exception(UnExpected Exception)
				 */

				if (startDate == null || startDate.equals("")
						|| startTime == null || startTime.equals("")
						|| endDate == null || endDate.equals("")
						|| endTime == null || endTime.equals("")
						|| rainCategoryCd == null || rainCategoryCd.equals("")
						|| rainCntrTxt == null || rainCntrTxt.equals("")) {

					throw new BusinessException("M4101");
					// errorMessage(request,"M4101");
				}
				String rainCntr = rainCntrTxt;

				boolean updateEditable = rainRecordsService.isEditableRainRecord(
						rainCntr, mode);
				log.info("updateEditable value in RainRecordsHandler------------------"
						+ updateEditable);
				if (updateEditable == true) {
					String statusCd = "I";

					//rain location parameter is passed from listing screen
					boolean update = rainRecordsService.updateRainRecord(rainCntr,
							startDate, startTime, endDate, endTime,
							rainCategoryCd, currUser, statusCd,  CommonUtility.deNull(criteria.getPredicates().get("selectedRainLocationCode")));
					if (update == false) {
						// throw a message "Delete record failed"
						// errorMessage(request, "M4975", "request=rainRecords",
						// "" , editDays);
						throw new BusinessException("M4975");
					}else{
						map.put("actionSuccess", "The record is deleted successfully");
					}
				} else {
					map.put("mode", "list");
					map.put("PINDEX", "PINDEX");
					log.info("setting mode before exception"
							+  CommonUtility.deNull(criteria.getPredicates().get("mode")));
					
					
					throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get("M50010"), editDays));
					// errorMessage(request, "M50010", "screen=rainRecords", ""
					// , editDays);
				}
				mode="list";
				bRefresh = true;
				log.info("bRefresh block value in delete block------------------"
						+ bRefresh);
			} else if ("cancelUpdate".equalsIgnoreCase(modeValue)) {
				mode="list";
				listFromDate =  CommonUtility.deNull(criteria.getPredicates().get("listFromDate"));
				listToDate =  CommonUtility.deNull(criteria.getPredicates().get("listToDate"));
				log.info(" RainRecordsHandler cancelUpdate block listFromDate:"
						+ listFromDate);
				log.info(" RainRecordsHandler cancelUpdate block listToDate:"
						+ listToDate);
				map.put("listFromDate", listFromDate);
				map.put("listToDate", listToDate);


				if(!pgIndex.equals("")){
					map.put("PINDEX", "PINDEX");
				}
				// nextScreen(request, "rainRecords");
				// return;
				bRefresh = true;
			}

			// start pagination
			log.info("RainRec Handlr before Pgnation mode: "+mode);
			log.info("RainRec Handlr before Pgnation PageIndex: "+pgIndex);
			log.info("RainRec Handlr before Pgnation PINDEX: "+ CommonUtility.deNull(criteria.getPredicates().get("PINDEX")));
			log.info("RainRec Handlr before Pgnation PINDEX from Para: "+ CommonUtility.deNull(criteria.getPredicates().get("PINDEX")));
			log.info("RainRec Handlr before Pgnation PageIndex from Para: "+ CommonUtility.deNull(criteria.getPredicates().get("PageIndex")));

			//This is to determine what location to be used for listing and selected in the listing page
			String finalLocation = "";

			if(!StringUtils.isEmpty(location)){
				finalLocation = location;
			}else if(StringUtils.isEmpty( CommonUtility.deNull(criteria.getPredicates().get("rainLocation")))){
				finalLocation ="A"; // hardcoded to A which old rain record (BCO + GCO)
			}else{
				finalLocation =  CommonUtility.deNull(criteria.getPredicates().get("rainLocation"));
			}
			List<RainRecordsValueObject> resultList = new ArrayList<RainRecordsValueObject>();

			log.info("inside RainRecordsHandler if block for PageIndex");
			TableResult tableResult = rainRecordsService.getRainRecordsByDate(listFromDate, listToDate, finalLocation,criteria); // retrieve all the data
			
			map.put("total", tableResult.getData().getTotal());
			int size = tableResult.getData().getListData().getTopsModel().size();
			RainRecordsValueObject objList = null; 
			for (int i = 0; i < size; i++) {
				objList = (RainRecordsValueObject) tableResult.getData().getListData().getTopsModel().get(i);
				resultList.add(objList);
				RainRecordsValueObject rsValObject = (RainRecordsValueObject) resultList.get(i);
				topsModel.put(rsValObject);
			}

			map.put("location", finalLocation);
				
			map.put("listData", topsModel);

		} catch (BusinessException e) {
			log.error("Exception: rainRecords ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		}catch (Exception e) {
			log.error("Exception: rainRecords",e);
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
				log.info("END: rainRecords result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
}
