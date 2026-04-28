package sg.com.jp.generalcargo.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import sg.com.jp.generalcargo.dao.TextParaRepository;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EnquireQueryObject;
import sg.com.jp.generalcargo.domain.EnquireSummarySlotValueObject;
import sg.com.jp.generalcargo.domain.MiscAppParkingAreaObject;
import sg.com.jp.generalcargo.domain.MiscAppValueObject;
import sg.com.jp.generalcargo.domain.MiscCustValueObject;
import sg.com.jp.generalcargo.domain.MiscParkMacBodyVO;
import sg.com.jp.generalcargo.domain.MiscParkMacValueObject;
import sg.com.jp.generalcargo.domain.MiscVehValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TextParaVO;
import sg.com.jp.generalcargo.restclient.SmartServiceRestClient;
import sg.com.jp.generalcargo.service.GBMiscApplicationService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.Constants;
import sg.com.jp.generalcargo.util.MiscAppCommonUtility;
import sg.com.jp.generalcargo.util.MiscAppConstValueObject;
import sg.com.jp.generalcargo.util.RecordPaging;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = GBMiscApplicationController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class GBMiscApplicationController {
	public static final String ENDPOINT = "gc/gbMiscApp";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private static final Log log = LogFactory.getLog(GBMiscApplicationController.class);
	String errorMessage = null;
	@Value("${MiscApp.file.upload.path}")
	String folderPath;
	
	@Value("${jp.common.notificationProperties.email.SpaceAppVoid_from}")
	String SpaceAppVoid_from;
	
	@Value("${jp.common.notificationProperties.email.SpaceAppVoid_subject}")
	String SpaceAppVoid_subject;
	
	@Value("${jp.common.notificationProperties.email.SpaceAppVoid_body}")
	String SpaceAppVoid_body_template;

	public static final String CARGO_TYPE_NORMAL = "N";
	public static final String CARGO_TYPE_DG = "D";
	public static final String CARGO_TYPE_OOG = "O";

	private final String[] cargoTypeValues = { "Empty/Normal", "DG", "OOG" };
	@Autowired
	private TextParaRepository textParaReo;

	@Autowired
	MiscAppCommonUtility miscAppCommonUtility;

	@Autowired
	private GBMiscApplicationService gbMiscApplicationService;
	
	@Autowired
	private SmartServiceRestClient smartServiceRestClient;

	// method: perform()

	@ApiOperation(value = "miscAppAdd", response = String.class)
	@RequestMapping(value = "/miscAppAdd", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAdd(HttpServletRequest request) throws BusinessException {
		errorMessage = null;
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		try {
			log.info("START: miscAppAdd criteria:" + criteria.toString());
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String searchByText, custName = null;
			String command = criteria.getPredicates().get("command");
			String applyType = null;
			String cust = null;
			String account = null;

			if ("ADD".equals(command)) {

				CommonUtility.deNull(criteria.getPredicates().get("applyType"));
				CommonUtility.deNull(criteria.getPredicates().get("applyTypeName"));
				CommonUtility.deNull(criteria.getPredicates().get("refNo"));
				CommonUtility.deNull(criteria.getPredicates().get("applyStatus"));
				CommonUtility.deNull(criteria.getPredicates().get("appFromDttm"));
				CommonUtility.deNull(criteria.getPredicates().get("appToDttm"));

				if (!"JP".equalsIgnoreCase(coCd)) {
					map.put("externalCust", "true");
					cust = coCd;
					map.put("cust", coCd);
					processDetails(request, criteria, map, UserID, coCd, cust, account, null);
				}
			} else if ("GET_DETAILS".equals(command)) {
				searchByText = criteria.getPredicates().get("searchByText");
				cust = criteria.getPredicates().get("cust");
				account = criteria.getPredicates().get("account");
				if (searchByText != null && searchByText.equals("true")) {
					custName = criteria.getPredicates().get("customer");
					map.put("searchString", custName);
					map.put("selectedCust", cust);
				}
				if (cust == null)
					cust = coCd;
				map.put("cust", cust);
				map.put("account", criteria.getPredicates().get("account"));
				processDetails(request, criteria, map, UserID, coCd, cust, account, custName);
			} else if ("CUST_NEXT".equals(command) || "VSL_NEXT".equals(command)) {
				if ("CUST_NEXT".equals(command)) {
					map.put("account", criteria.getPredicates().get("account"));
				}
				try {
					String result1 = null;
					List<MiscAppValueObject> vesselList = null;
					if (criteria.getPredicates().get("varcode") != null) {

						result1 = gbMiscApplicationService.checkVarcode(criteria.getPredicates().get("varcode"));
						// Added on 20/07/2007 by Punitha.To display vessel details
						vesselList = gbMiscApplicationService.getVesselDetails(criteria.getPredicates().get("varcode"));

					}
					map.put("vesselList", vesselList);
					// Ended by Punitha
					if (result1 == null || "Valid".equalsIgnoreCase(result1)) {
						map.put("varcode", criteria.getPredicates().get("varcode"));
						applyType = criteria.getPredicates().get("applyType");

						map.put("coCd", coCd);
						if (applyType != null
								&& applyType.equals(MiscAppConstValueObject.MISC_APP_OVERNIGHT_PARKING_OF_VEHICLE)) {
							map.put("request", "MiscAppAddVeh");
							miscAppAddVeh(request, criteria, map);
							// return null;
						} else if (applyType != null
								&& applyType.equals(MiscAppConstValueObject.MISC_APP_PARKING_OF_LINE_TOW_BARGE)) {
							map.put("request", "MiscAppAddBarge");
							miscAppAddBargeMiscList(request, criteria, map);
							// return null;
						} else if (applyType != null
								&& applyType.equals(MiscAppConstValueObject.MISC_APP_REEFER_CONTAINER_POWER_OUTLET)) {
							map.put("request", "MiscAppAddReefer");
							miscAppAddReefer(request, criteria, map);
							// return null;
						} else if (applyType != null
								&& applyType.equals(MiscAppConstValueObject.MISC_APP_USE_OF_SPACE)) {
							map.put("request", "MiscAppAddSpace");
							miscAppAddSpace(request, criteria, map);
							// return null;
						} else if (applyType != null && applyType
								.equals(MiscAppConstValueObject.MISC_APP_OVERNIGHT_PARKING_OF_FORKLIFT_SHORE_CRANE)) {
							map.put("request", "MiscAppAddParkMac");
							miscAppAddParkMacMiscList(request, criteria, map);
							// return null;
						} else if (applyType != null && applyType.equals(
								MiscAppConstValueObject.MISC_APP_STATIONING_OF_FORKLIFT_CNTRLIFT_WHEELLOADER_SHORECRANE)) {
							map.put("request", "MiscAppAddStationMac");
							miscAppAddStationMac(request, criteria, map);
							// return null;
						} else if (applyType != null
								&& applyType.equals(MiscAppConstValueObject.MISC_APP_HIRE_OF_WOODEN_STEEL_SPREADER)) {
							map.put("request", "MiscAppAddSpreader");
							miscAppAddSpreader(request, criteria, map);
							// return null;
						} else if (applyType != null
								&& applyType.equals(MiscAppConstValueObject.MISC_APP_CONTRACTOR_PERMIT)) {
							map.put("request", "MiscAppAddContract");
							miscAppAddContract(request, criteria, map);
							// return null;
						} else if (applyType != null
								&& applyType.equals(MiscAppConstValueObject.MISC_APP_HOT_WORK_PERMIT)) {
							map.put("request", "MiscAppAddHotwork");
							miscAppAddHotworkMiscList(request, criteria, map);
							// return null;
						} else if (applyType != null
								&& applyType.equals(MiscAppConstValueObject.MISC_APP_TRAILER_PARKING_APPLICATION)) {
							map.put("request", "MiscAppAddTpa");
							miscAppAddTpa(request, criteria, map);
							// return null;
						}

					} else if ("InValid".equalsIgnoreCase(result1)) {
						throw new BusinessException("Invalid Varcode. Please re-enter.");
					}
				} catch (Exception ex) {
					errorMessage = "Invalid Varcode. Please re-enter.";
				}
			} else if ("VSL_SEL".equals(command)) {
				map.put("account", criteria.getPredicates().get("account"));
				miscAppVar(request);
				// return ;
			}
			// LogManager.instance.logInfo("~~~~~ Ended MiscAppCustHandler ~~~~~");
			map.put("screen", "MiscAppCust");

		} catch (BusinessException e) {
			log.info("Exception miscAppAdd : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAdd : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAdd result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// method: processDetails()
	public ResponseEntity<?> processDetails(HttpServletRequest request, Criteria criteria, Map<String, Object> map,
			String UserID, String coCd, String cust, String account, String custName) throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: processDetails criteria:" + criteria.toString() + ", map: " + map.toString() + ", UserID: " + CommonUtility.deNull(UserID) + ", coCd: " + CommonUtility.deNull(coCd) + 
					", cust: " + CommonUtility.deNull(cust) + ", account: " + CommonUtility.deNull(account) + ",custName : " + CommonUtility.deNull(custName));

			List<MiscAppValueObject> miscCustList = null;
			List<MiscAppValueObject> miscAcctList = null;
			if (custName != null) {
				miscCustList = gbMiscApplicationService.getCustomerList(UserID, coCd, custName);
			} else {
				miscCustList = gbMiscApplicationService.getCustomerList(UserID, coCd, null);
			}
			if (cust != null) {
				miscAcctList = gbMiscApplicationService.getAccountList(UserID, coCd, cust);
			} else {
				miscAcctList = gbMiscApplicationService.getAccountList(UserID, coCd, null);
			}
			List<MiscCustValueObject> custDetList = gbMiscApplicationService.getCustomerDetails(UserID, cust, account);
			map.put("CustDetList", custDetList);
			MiscCustValueObject obj = new MiscCustValueObject();
			String coName = null;
			String phone = null;
			String add1 = null;
			String add2 = null;
			String city = null;
			String pin = null;

			if (custDetList.size() > 0) {
				obj = (MiscCustValueObject) custDetList.get(0);
				coName = CommonUtility.deNull(obj.getCoName());
				phone = CommonUtility.deNull(obj.getContact1());
				add1 = CommonUtility.deNull(obj.getAddress1());
				add2 = CommonUtility.deNull(obj.getAddress2());
				city = CommonUtility.deNull(obj.getCity());
				pin = CommonUtility.deNull(obj.getPin());
				account = CommonUtility.deNull(obj.getAcctNbr());
			}
			
			map.put("coName", coName);
			map.put("phone", phone);
			map.put("add1", add1);
			map.put("add2", add2);
			map.put("city", city);
			map.put("pin", pin);
			map.put("account", account);

			map.put("miscCustList", miscCustList);
			map.put("miscAcctList", miscAcctList);
		} catch (BusinessException e) {
			log.info("Exception processDetails : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception processDetails : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: processDetails result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppSupportStationMacHandler -->perform
	@RequestMapping(value = "/miscAppSupportStationMac", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppSupportStationMac(HttpServletRequest request, Criteria criteria,
			Map<String, Object> map) throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		try {
			log.info("START: miscAppSupportStationMac criteria:" + criteria.toString() + ", map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String command = criteria.getPredicates().get("command");

			if ("SUPPORT".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				// MiscParkMacValueObject parkmac = new MiscParkMacValueObject();
				List<Object> list = gbMiscApplicationService.getStationingOfMacDetails(userId, applyType, appSeqNbr,
						applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppSupportStationMac");
			} else if ("SUPPORT_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String supportDate = criteria.getPredicates().get("supportDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.supportApplication(userId, applicationStatus, miscSeqNbr, remarks,
						supportDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppSupportStationMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppSupportStationMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppSupportStationMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// ADD Start
	// delegate.helper.gbms.miscApp --> MiscAppAddBargeHandler -->perform
	@RequestMapping(value = "/miscAppAddBargeMiscList", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddBargeMiscList(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		try {
			log.info("START: miscAppAddBargeMiscList criteria:" + criteria.toString() + ", map: " + map.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");

			log.info("Agree --------------> " + agree);
			log.info("command --------------> " + command);
			log.info("User ID --> " + userId);
			log.info("Co CD --> " + coCd);

			String bargeName = null;
			String bargeLOA = null;
			String draft = null;
			String tugboat = null;
			String contactNo = null;
			String fromDate = null;
			String toDate = null;
			String fromTime = null;
			String toTime = null;
			String motherShip = null;
			String berthNo = null;
			String dg = null;
			String cargoType = null;
			String className = null;
			String status = null;
			String appDate = null;
			// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
			String conPerson = null;
			String conTel = null;
			// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel

			String applyType = criteria.getPredicates().get("applyType");
			String applyStatus = criteria.getPredicates().get("applyStatus");
			String cust = criteria.getPredicates().get("cust");
			String account = criteria.getPredicates().get("account");
			String varcode = criteria.getPredicates().get("varcode");
			String coName = criteria.getPredicates().get("coName");
			log.info("^^^^^^^^  applyType : " + applyType);
			log.info("^^^^^^^^  applyStatus : " + applyStatus);
			log.info("^^^^^^^^  cust : " + cust);
			log.info("^^^^^^^^  account : " + account);
			log.info("^^^^^^^^  varcode : " + varcode);
			log.info("^^^^^^^^  coName : " + coName);
			log.info("````````````````````````````````````````````");

			if ("ADD_SUBMIT".equals(command)) {
				if (agree == null) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					bargeName = criteria.getPredicates().get("bargeName");
					bargeLOA = criteria.getPredicates().get("bargeLOA");
					draft = criteria.getPredicates().get("draft");
					tugboat = criteria.getPredicates().get("tugboat");
					contactNo = criteria.getPredicates().get("contactNo");
					fromDate = criteria.getPredicates().get("fromDate");
					toDate = criteria.getPredicates().get("toDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toTime = criteria.getPredicates().get("toTime");
					motherShip = criteria.getPredicates().get("motherShip");
					berthNo = criteria.getPredicates().get("berthNo");
					dg = criteria.getPredicates().get("dg");
					cargoType = criteria.getPredicates().get("cargoType");
					className = criteria.getPredicates().get("className");
//							Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel

					if ("D".equals(status)) {
						fromDate = fromDate + fromTime;
						toDate = toDate + toTime;

						/*
						 * miscAppEjb.addParkingOfLineTowBargeDetails(userId, applyType, status, cust,
						 * account, varcode, bargeName, bargeLOA, draft, tugboat, contactNo, fromDate,
						 * toDate, motherShip, berthNo, dg, cargoType, className, coName, appDate);
						 */
						// Amended on 14/06/2007 by Punitha. To add Contact Person and COntact Tel
						gbMiscApplicationService.addParkingOfLineTowBargeDetails(userId, applyType, status, cust,
								account, varcode, bargeName, bargeLOA, draft, tugboat, contactNo, fromDate, toDate,
								motherShip, berthNo, dg, cargoType, className, coName, appDate, conPerson, conTel);
						// Ended by Punitha
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("appDate", appDate);
						map.put("status", criteria.getPredicates().get("status"));
						map.put("bargeName", criteria.getPredicates().get("bargeName"));
						map.put("bargeLOA", criteria.getPredicates().get("bargeLOA"));
						map.put("draft", CommonUtility.deNull(criteria.getPredicates().get("draft")));
						map.put("tugboat", CommonUtility.deNull(criteria.getPredicates().get("tugboat")));
						map.put("contactNo", CommonUtility.deNull(criteria.getPredicates().get("contactNo")));
						map.put("fromDate", criteria.getPredicates().get("fromDate"));
						map.put("toDate", criteria.getPredicates().get("toDate"));
						map.put("fromTime", criteria.getPredicates().get("fromTime"));
						map.put("toTime", criteria.getPredicates().get("toTime"));
						map.put("motherShip", CommonUtility.deNull(criteria.getPredicates().get("motherShip")));
						map.put("berthNo", CommonUtility.deNull(criteria.getPredicates().get("berthNo")));
						map.put("dg", CommonUtility.deNull(criteria.getPredicates().get("dg")));
						map.put("cargoType", criteria.getPredicates().get("cargoType"));
						map.put("className", criteria.getPredicates().get("className"));
//								Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
						map.put("conPerson", conPerson);
						map.put("conTel", conTel);
						// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					}
				} else if (agree != null && agree.equals("true")) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					bargeName = criteria.getPredicates().get("bargeName");
					bargeLOA = criteria.getPredicates().get("bargeLOA");
					draft = criteria.getPredicates().get("draft");
					tugboat = criteria.getPredicates().get("tugboat");
					contactNo = criteria.getPredicates().get("contactNo");
					fromDate = criteria.getPredicates().get("fromDate");
					toDate = criteria.getPredicates().get("toDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toTime = criteria.getPredicates().get("toTime");
					motherShip = criteria.getPredicates().get("motherShip");
					berthNo = criteria.getPredicates().get("berthNo");
					dg = criteria.getPredicates().get("dg");
					cargoType = criteria.getPredicates().get("cargoType");
					className = criteria.getPredicates().get("className");
					fromDate = fromDate + fromTime;
					toDate = toDate + toTime;
//							Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel

					/*
					 * miscAppEjb.addParkingOfLineTowBargeDetails(userId, applyType, status, cust,
					 * account, varcode, bargeName, bargeLOA, draft, tugboat, contactNo, fromDate,
					 * toDate, motherShip, berthNo, dg, cargoType, className, coName, appDate);
					 */
					// Amended on 14/06/2007 by Punitha.To add Contact Person and COntact tel
					gbMiscApplicationService.addParkingOfLineTowBargeDetails(userId, applyType, status, cust, account,
							varcode, bargeName, bargeLOA, draft, tugboat, contactNo, fromDate, toDate, motherShip,
							berthNo, dg, cargoType, className, coName, appDate, conPerson, conTel);
					// Ended by Punitha

					// remove session attributes
					map.remove("appDate");
					map.remove("status");
					map.remove("bargeName");
					map.remove("bargeLOA");
					map.remove("draft");
					map.remove("tugboat");
					map.remove("contactNo");
					map.remove("fromDate");
					map.remove("toDate");
					map.remove("fromTime");
					map.remove("toTime");
					map.remove("motherShip");
					map.remove("berthNo");
					map.remove("dg");
					map.remove("cargoType");
					map.remove("className");
					// forwardHandler(request, "MiscAppList");
//							Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("conPerson");
					map.remove("conTel");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("vesselList");

					// return;
				}
			} else if ("CUST_NEXT".equals(command) || "VSL_NEXT".equals(command)) {
				List<String> motherShipList = gbMiscApplicationService.getVesselDetails();
				map.put("MotherShipList", motherShipList);

				// Added on 13/06/2007 by Punitha.To retrieve closing time parameter value
				String closingTime = gbMiscApplicationService.getClosingTime();
				map.put("closingTime", closingTime);
				// Ended by Punitha
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAddBargeMiscList : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddBargeMiscList : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAddBargeMiscList result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAddContractHandler -->perform
	public ResponseEntity<?> miscAppAddContract(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		try {
			log.info("START: miscAppAddContract criteria:" + criteria.toString() + "map: " + map);

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");

			log.info("command --------------> " + command);
			log.info("Agree --------------> " + agree);
			log.info("User ID --> " + userId);
			log.info("Co CD --> " + coCd);

			String status = null;
			String appDate = null;
			String location = null;
			String description = null;
			String others = null;
			String fromDate = null;
			String fromTime = null;
			String toDate = null;
			String toTime = null;
			String licType = null;
			String licNo = null;
			String remarks = null;
			String waiver = null;
			String contCoNm = null;
			String contCoAddr = null;
			String contactNm = null;
			String contactNric = null;
			String designation = null;

			List<MiscAppValueObject> docTypeList = null;
			List<String> docNameList = null;
			List<String> docCdList = null;
			List<MiscAppValueObject> contractTypeList = null;

			String applyType = criteria.getPredicates().get("applyType");
			String cust = criteria.getPredicates().get("cust");
			String account = criteria.getPredicates().get("account");
			String varcode = criteria.getPredicates().get("varcode");
			String coName = criteria.getPredicates().get("coName");

			map.put("coCd", coCd);
			if ("CUST_NEXT".equals(command) || "VSL_NEXT".equals(command)) {
				contractTypeList = gbMiscApplicationService.getContractTypeList();
				map.put("ContractTypeList", contractTypeList);
				docTypeList = gbMiscApplicationService.getContractUploadDocumentList();
				map.put("DocTypeList", docTypeList);
				// removeSessionAttributes(map);
			} else if ("UPLOAD_SUBMIT".equals(command)) {
				Object listObject = null;
				listObject = request.getAttribute("docTypeList");
				docTypeList = new ArrayList<MiscAppValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docTypeList.add((MiscAppValueObject) item);
						}
					}
				}

				listObject = request.getAttribute("docNameList");
				docNameList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docNameList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("docCdList");
				docCdList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docCdList.add((String) item);
						}
					}
				}

				appDate = criteria.getPredicates().get("appDate");
				applyType = criteria.getPredicates().get("applyType");
				status = criteria.getPredicates().get("status");
				coName = criteria.getPredicates().get("coName");
				cust = criteria.getPredicates().get("cust");
				account = criteria.getPredicates().get("account");
				varcode = criteria.getPredicates().get("varcode");
				location = criteria.getPredicates().get("location");
				description = criteria.getPredicates().get("description");
				others = criteria.getPredicates().get("others");
				fromDate = criteria.getPredicates().get("fromDate");
				fromTime = criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate");
				toTime = criteria.getPredicates().get("toTime");
				// licType = session.getAttribute("licType");
				// licNo = session.getAttribute("licNo");
				remarks = criteria.getPredicates().get("remarks");
				waiver = criteria.getPredicates().get("waiver");
				contCoNm = criteria.getPredicates().get("contCoNm");
				contCoAddr = criteria.getPredicates().get("contCoAddr");
				contactNm = criteria.getPredicates().get("contactNm");
				contactNric = criteria.getPredicates().get("contactNric");
				designation = criteria.getPredicates().get("designation");
			} else {
				if (agree == null) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					location = criteria.getPredicates().get("location");
					description = criteria.getPredicates().get("description");
					others = criteria.getPredicates().get("others");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					// licType = request.getParameter("licType");
					// licNo = request.getParameter("licNo");
					remarks = criteria.getPredicates().get("remarks");
					waiver = criteria.getPredicates().get("waiver");
					contCoNm = criteria.getPredicates().get("contCoNm");
					contCoAddr = criteria.getPredicates().get("contCoAddr");
					contactNm = criteria.getPredicates().get("contactNm");
					contactNric = criteria.getPredicates().get("contactNric");
					designation = criteria.getPredicates().get("designation");
					if (waiver == null)
						waiver = "N";
					// docType = request.getParameter("docType");
					// file = request.getParameter("file");

					Object listObject = null;
					listObject = request.getAttribute("docTypeList");
					docTypeList = new ArrayList<MiscAppValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								docTypeList.add((MiscAppValueObject) item);
							}
						}
					}

					listObject = request.getAttribute("docNameList");
					docNameList = new ArrayList<String>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								docNameList.add((String) item);
							}
						}
					}

					listObject = request.getAttribute("docCdList");
					docCdList = new ArrayList<String>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								docCdList.add((String) item);
							}
						}
					}
					if ("UPLOAD".equals(command)) {
						/*
						 * if(docTypeList == null) { docTypeList = new ArrayList();
						 * docTypeList.add(docType); map.put("docTypeList", docTypeList); }else {
						 * docTypeList.add(docType); } if (docNameList == null) { docNameList = new
						 * ArrayList(); docNameList.add(file); map.put("docNameList", docNameList);
						 * }else { docNameList.add(file); }
						 */
						map.put("subDirName", "CONTRACT");
						map.put("nextScreen", "MiscAppAddContract");
					} 
					if (docTypeList != null)
						map.put("docTypeTemp", docTypeList.toArray(new String[0]));
					if (docNameList != null)
						map.put("docNameTemp", docNameList.toArray(new String[0]));
					if (docCdList != null)
						map.put("docCdTemp", docCdList.toArray(new String[0]));

					map.put("appDate", appDate);
					map.put("status", status);
					map.put("location", location);
					map.put("description", description);
					map.put("others", others);
					map.put("fromDate", fromDate);
					map.put("fromTime", fromTime);
					map.put("toDate", toDate);
					map.put("toTime", toTime);
					map.put("licType", licType);
					map.put("licNo", licNo);
					map.put("remarks", remarks);
					map.put("waiver", waiver);
					map.put("contCoNm", contCoNm);
					map.put("contCoAddr", contCoAddr);
					map.put("contactNm", contactNm);
					map.put("contactNric", contactNric);
					map.put("designation", designation);
					fromDate = fromDate + fromTime;
					toDate = toDate + toTime;
					if ("D".equals(status)) {
						/*
						 * ArrayList assignFileName = miscAppEjb.addContractorPermitDetails(userId,
						 * applyType, status, cust, account, varcode, coName, location, description,
						 * others, fromDate, toDate, licType, licNo, remarks, waiver, contCoNm,
						 * contCoAddr, contactNm, contactNric, designation, docTypeList, docNameList,
						 * appDate);
						 * 
						 * //System.out.println("assignFileName -----> " + assignFileName);
						 */
						// write file into server
						/*
						 * byte[] fileContent = null; if(docNameList != null && assignFileName != null){
						 * //System.out.println("SAVE * assignFileName -----> " +
						 * assignFileName.size()); for(int i = 0; i < docNameList.size(); i++){
						 * //fileContent = UploadDocument.getFileContent(docNameList.get(i));
						 * fileContent = (byte[])docContentList.get(i);
						 * UploadDocument.writeToFile(fileContent, assignFileName.get(i), "CONTRACT"); }
						 * }
						 */
						gbMiscApplicationService.addContractorPermitDetails(userId, applyType, status, cust, account,
								varcode, coName, location, description, others, fromDate, toDate, licType, licNo,
								remarks, waiver, contCoNm, contCoAddr, contactNm, contactNric, designation, docTypeList,
								docNameList, appDate);
						// remove session attributes
						// removeSessionAttributes(session);
						map.remove("coCd");
						// forwardHandler(request, "MiscAppList");
						// return;
					}
				} else if (agree != null && agree.equals("true")) {

					Object listObject = null;
					listObject = request.getAttribute("docTypeList");
					docTypeList = new ArrayList<MiscAppValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								docTypeList.add((MiscAppValueObject) item);
							}
						}
					}

					listObject = request.getAttribute("docNameList");
					docNameList = new ArrayList<String>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								docNameList.add((String) item);
							}
						}
					}

					listObject = request.getAttribute("docCdList");
					docCdList = new ArrayList<String>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								docCdList.add((String) item);
							}
						}
					}

					appDate = criteria.getPredicates().get("appDate");
					applyType = criteria.getPredicates().get("applyType");
					status = criteria.getPredicates().get("status");
					coName = criteria.getPredicates().get("coName");
					cust = criteria.getPredicates().get("cust");
					account = criteria.getPredicates().get("account");
					varcode = criteria.getPredicates().get("varcode");
					location = criteria.getPredicates().get("location");
					description = criteria.getPredicates().get("description");
					others = criteria.getPredicates().get("others");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					licType = criteria.getPredicates().get("licType");
					licNo = criteria.getPredicates().get("licNo");
					remarks = criteria.getPredicates().get("remarks");
					waiver = criteria.getPredicates().get("waiver");
					contCoNm = criteria.getPredicates().get("contCoNm");
					contCoAddr = criteria.getPredicates().get("contCoAddr");
					contactNm = criteria.getPredicates().get("contactNm");
					contactNric = criteria.getPredicates().get("contactNric");
					designation = criteria.getPredicates().get("designation");
					fromDate = fromDate + fromTime;
					toDate = toDate + toTime;

					/*
					 * ArrayList assignFileName = miscAppEjb.addContractorPermitDetails(userId,
					 * applyType, status, cust, account, varcode, coName, location, description,
					 * others, fromDate, toDate, licType, licNo, remarks, waiver, contCoNm,
					 * contCoAddr, contactNm, contactNric, designation, docTypeList, docNameList,
					 * appDate);
					 * 
					 * //System.out.println("assignFileName -----> " + assignFileName);
					 */
					// write file into server
					/*
					 * byte[] fileContent = null; if(docNameList != null && assignFileName != null){
					 * //System.out.println("SUBMIT * assignFileName -----> " +
					 * assignFileName.size()); for(int i = 0; i < docNameList.size(); i++){
					 * //fileContent = UploadDocument.getFileContent(docNameList.get(i));
					 * fileContent = (byte[])docContentList.get(i);
					 * UploadDocument.writeToFile(fileContent, assignFileName.get(i), "CONTRACT"); }
					 * }
					 */
					gbMiscApplicationService.addContractorPermitDetails(userId, applyType, status, cust, account,
							varcode, coName, location, description, others, fromDate, toDate, licType, licNo, remarks,
							waiver, contCoNm, contCoAddr, contactNm, contactNric, designation, docTypeList, docNameList,
							appDate);

					// remove session attributes
					// removeSessionAttributes(session);
					map.remove("coCd");
					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAddContract : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddContract : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAddContract result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.miscApp --> MiscAppAddHotworkHandler -->perform
	@RequestMapping(value = "/miscAppAddHotworkMiscList", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddHotworkMiscList(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppAddHotworkMiscList criteria:" + criteria.toString() + ", map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");

			String applyType = criteria.getPredicates().get("applyType");
			String coName = criteria.getPredicates().get("coName");
			String cust = criteria.getPredicates().get("cust");
			String account = criteria.getPredicates().get("account");
			String varcode = criteria.getPredicates().get("varcode");
			String status = null;
			String location = null;
			String description = null;
			String fromDate = null;
			String fromTime = null;
			String toDate = null;
			String toTime = null;
			String appDate = null;

			if ("ADD_SUBMIT".equals(command)) {
				if (agree == null) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					location = criteria.getPredicates().get("location");
					description = criteria.getPredicates().get("description");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");

					if ("D".equals(status)) {
						fromDate = fromDate + fromTime;
						toDate = toDate + toTime;

						gbMiscApplicationService.addHotworkDetails(userId, applyType, status, cust, account, varcode,
								coName, location, description, fromDate, toDate, appDate);
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("appDate", appDate);
						map.put("status", request.getParameter("status"));
						map.put("location", location);
						map.put("description", description);
						map.put("fromDate", fromDate);
						map.put("fromTime", fromTime);
						map.put("toDate", toDate);
						map.put("toTime", toTime);
					}
				} else if (agree != null && agree.equals("true")) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					location = criteria.getPredicates().get("location");
					description = criteria.getPredicates().get("description");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					fromDate = fromDate + fromTime;
					toDate = toDate + toTime;

					gbMiscApplicationService.addHotworkDetails(userId, applyType, status, cust, account, varcode,
							coName, location, description, fromDate, toDate, appDate);

					// remove session attributes
					map.remove("appDate");
					map.remove("status");
					map.remove("location");
					map.remove("description");
					map.remove("fromDate");
					map.remove("fromTime");
					map.remove("toDate");
					map.remove("toTime");
					map.remove("vesselList");
					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAddHotworkMiscList : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddHotworkMiscList : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAddHotworkMiscList result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAddParkMacHandler -->perform
	@RequestMapping(value = "/miscAppAddParkMacMiscList", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddParkMacMiscList(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppAddParkMacMiscList criteria:" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String agree = criteria.getPredicates().get("agree");

			String command = criteria.getPredicates().get("command");
			log.info("command --------------> " + command);
			log.info("Agree --------------> " + agree);
			log.info("User ID --> " + userId);
			log.info("Co CD --> " + coCd);

			String appDate = null;
			String status = null;
			String macType = null;
			String fromDate = null;
			String fromTime = null;
			String toDate = null;
			String toTime = null;
			String remarks = null;
			List<MiscAppValueObject> docTypeList = null;
			List<String> docNameList = null;
			List<String> docCdList = null;
			List<String> regNbrList = null;

			log.info("````````````````````````````````````````````");
			String applyType = criteria.getPredicates().get("applyType");
			String applyStatus = criteria.getPredicates().get("applyStatus");
			String cust = criteria.getPredicates().get("cust");
			String account = criteria.getPredicates().get("account");
			String varcode = criteria.getPredicates().get("varcode");
			String coName = criteria.getPredicates().get("coName");
			log.info("^^^^^^^^  applyType : " + applyType);
			log.info("^^^^^^^^  applyStatus : " + applyStatus);
			log.info("^^^^^^^^  cust : " + cust);
			log.info("^^^^^^^^  account : " + account);
			log.info("^^^^^^^^  varcode : " + varcode);
			log.info("^^^^^^^^  coName : " + coName);
			log.info("````````````````````````````````````````````");

			map.put("coCd", coCd);
			if ("CUST_NEXT".equals(command) || "VSL_NEXT".equals(command)) {
				docTypeList = gbMiscApplicationService.getUploadDocumentTypeList();
				List<MiscAppValueObject> docList = gbMiscApplicationService.getUploadDocumentList();
				map.put("docTypeList", docTypeList);
				map.put("docList", docList);

				// removeSessionAttributes(session);
			} else if ("UPLOAD_SUBMIT".equals(command)) {
				Object listObject = null;
				listObject = request.getAttribute("docTypeList");
				docTypeList = new ArrayList<MiscAppValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docTypeList.add((MiscAppValueObject) item);
						}
					}
				}

				listObject = request.getAttribute("docNameList");
				docNameList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docNameList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("docCdList");
				docCdList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docCdList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("regNbrList");
				regNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							regNbrList.add((String) item);
						}
					}
				}

				appDate = criteria.getPredicates().get("appDate");
				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				fromTime = criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate");
				toTime = criteria.getPredicates().get("toTime");
				remarks = criteria.getPredicates().get("remarks");

				if (regNbrList != null)
					map.put("regNbr", regNbrList.toArray(new String[0]));
			} else if ("ADD_SUBMIT".equals(command)) {
				Object listObject = null;
				listObject = request.getAttribute("docTypeList");
				docTypeList = new ArrayList<MiscAppValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docTypeList.add((MiscAppValueObject) item);
						}
					}
				}

				listObject = request.getAttribute("docNameList");
				docNameList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docNameList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("docCdList");
				docCdList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docCdList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("regNbrList");
				regNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							regNbrList.add((String) item);
						}
					}
				}

				appDate = criteria.getPredicates().get("appDate");
				status = criteria.getPredicates().get("status");
				// Amended by Punitha on 01/08/2008
				/*
				 * macType = session.getAttribute("macType"); fromDate =
				 * session.getAttribute("fromDate"); fromTime =
				 * session.getAttribute("fromTime"); toDate = session.getAttribute("toDate");
				 * toTime = session.getAttribute("toTime"); remarks =
				 * session.getAttribute("remarks");
				 */

				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				fromTime = criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate");
				toTime = criteria.getPredicates().get("toTime");
				remarks = criteria.getPredicates().get("remarks");

				// End by Punitha
				fromDate = fromDate + fromTime;
				toDate = toDate + toTime;

				gbMiscApplicationService.addParkingOfForkliftShorecrane(userId, applyType, status, cust, account,
						varcode, coName, macType, fromDate, toDate, remarks, docTypeList, docNameList, regNbrList,
						appDate);

				// write file into server
				/*
				 * byte[] fileContent = null; if(docNameList != null && assignFileName != null){
				 * //System.out.println("assignFileName -----> " + assignFileName.size());
				 * for(int i = 0; i < docNameList.size(); i++){ fileContent =
				 * UploadDocument.getFileContent(docNameList.get(i));
				 * UploadDocument.writeToFile(fileContent, assignFileName.get(i), "MACHINE"); }
				 * }
				 */
				// remove session attributes
				// removeSessionAttributes(session);
				map.remove("coCd");
				// forwardHandler(request, "MiscAppList");
				// return;
			} else {
				appDate = criteria.getPredicates().get("appDate");
				status = criteria.getPredicates().get("status");
				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				fromTime = criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate");
				toTime = criteria.getPredicates().get("toTime");
				remarks = criteria.getPredicates().get("remarks");
				Object listObject = null;
				listObject = request.getAttribute("docTypeList");
				docTypeList = new ArrayList<MiscAppValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docTypeList.add((MiscAppValueObject) item);
						}
					}
				}

				listObject = request.getAttribute("docNameList");
				docNameList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docNameList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("docCdList");
				docCdList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docCdList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("regNbrList");
				regNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							regNbrList.add((String) item);
						}
					}
				}

				if ("UPLOAD".equals(command)) {
					/*
					 * if (docTypeList == null) { docTypeList = new ArrayList();
					 * docTypeList.add(docType); map.put("docTypeList", docTypeList); } else {
					 * docTypeList.add(docType); } if (docNameList == null) { docNameList = new
					 * ArrayList(); docNameList.add(file); map.put("docNameList", docNameList); }
					 * else { docNameList.add(file); }
					 */

					map.put("subDirName", "MACHINE");
					map.put("nextScreen", "MiscAppAddParkMac");
				
				} else if ("REGSUBMIT".equals(command)) {
					macType = criteria.getPredicates().get("macType");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					remarks = criteria.getPredicates().get("remarks");
					String regNbr = criteria.getPredicates().get("regNbr");
					if (regNbrList.size() == 0) {
						regNbrList = new ArrayList<String>();
						regNbrList.add(regNbr);
						map.put("regNbrList", regNbrList);
					} else {
						regNbrList.add(regNbr);
					}
				} else if ("REGDELETE".equals(command)) {
					String[] delList = CommonUtil.getRequiredStringParameters(request, "regCheck");
					List<String> removeList = new ArrayList<String>();
					for (int i = 0; i < delList.length; i++) {
						removeList.add(regNbrList.get(Integer.parseInt(delList[i])));
					}
					for (int i = 0; i < removeList.size(); i++) {
						regNbrList.remove(removeList.get(i));
					}
				}
				if (docTypeList != null)
					map.put("docTypeTemp", docTypeList.toArray(new String[0]));
				if (docNameList != null)
					map.put("docNameTemp", docNameList.toArray(new String[0]));
				if (docCdList != null)
					map.put("docCdTemp", docCdList.toArray(new String[0]));
				if (regNbrList != null)
					map.put("regNbr", regNbrList.toArray(new String[0]));

				map.put("appDate", appDate);
				map.put("status", status);
				map.put("macType", macType);
				map.put("fromDate", fromDate);
				map.put("fromTime", fromTime);
				map.put("toDate", toDate);
				map.put("toTime", toTime);
				map.put("remarks", remarks);
			}
			if ("REGADD".equals(command)) {
				// nextScreen(request, "MiscAppAddParkMac");
				// return;
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAddParkMacMiscList : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddParkMacMiscList : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAddParkMacMiscList result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAddSpaceHandler -->perform
	public ResponseEntity<?> miscAppAddSpace(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppAddSpace criteria:" + criteria.toString() + ", map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String status = null;
			String coName = null;
			String cust = null;
			String account = null;
			String varcode = null;
			String appDate = null;

			String spaceType = null;
			String purpose = null;
			String fromDate = null;
			String fromTime = null;
			String toDate = null;
			String toTime = null;
			String reason = null;
			String billNbr = null;
			String marks = null;
			String packages = null;
			String cargoDesc = null;
			String tonnage = null;
			String newMarks = null;
			String newPackages = null;
			String newCargoDesc = null;
			String newTonnage = null;
			// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
			String conPerson = null;
			String conTel = null;
			// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel

			applyType = criteria.getPredicates().get("applyType");
			coName = criteria.getPredicates().get("coName");
			cust = criteria.getPredicates().get("cust");
			account = criteria.getPredicates().get("account");
			varcode = criteria.getPredicates().get("varcode");

			if ("ADD_SUBMIT".equals(command)) {
				if (agree == null) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					spaceType = criteria.getPredicates().get("spaceType");
					purpose = criteria.getPredicates().get("purpose");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					reason = criteria.getPredicates().get("reason");
					billNbr = criteria.getPredicates().get("billNbr");
					marks = criteria.getPredicates().get("marks");
					packages = criteria.getPredicates().get("packages");
					cargoDesc = criteria.getPredicates().get("cargoDesc");
					tonnage = criteria.getPredicates().get("tonnage");
					newMarks = criteria.getPredicates().get("newMarks");
					newPackages = criteria.getPredicates().get("newPackages");
					newCargoDesc = criteria.getPredicates().get("newCargoDesc");
					newTonnage = criteria.getPredicates().get("newTonnage");
					// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel

					if ("D".equals(status)) {
						fromDate = fromDate + fromTime;
						toDate = toDate + toTime;

						gbMiscApplicationService.addUseOfSpaceDetails(userId, applyType, status, cust, account, varcode,
								coName, spaceType, purpose, fromDate, toDate, reason, billNbr, marks, packages,
								cargoDesc, tonnage, newMarks, newPackages, newCargoDesc, newTonnage, appDate, conPerson,
								conTel);
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("appDate", appDate);
						map.put("status", request.getParameter("status"));
						map.put("spaceType", spaceType);
						map.put("purpose", purpose);
						map.put("fromDate", fromDate);
						map.put("fromTime", fromTime);
						map.put("toDate", toDate);
						map.put("toTime", toTime);
						map.put("reason", reason);
						map.put("billNbr", billNbr);
						map.put("marks", marks);
						map.put("packages", packages);
						map.put("cargoDesc", cargoDesc);
						map.put("tonnage", tonnage);
						map.put("newMarks", newMarks);
						map.put("newPackages", newPackages);
						map.put("newCargoDesc", newCargoDesc);
						map.put("newTonnage", newTonnage);
						// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
						map.put("conPerson", conPerson);
						map.put("conTel", conTel);
						// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
					}
				} else if (agree != null && agree.equals("true")) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					spaceType = criteria.getPredicates().get("spaceType");
					purpose = criteria.getPredicates().get("purpose");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					reason = criteria.getPredicates().get("reason");
					billNbr = criteria.getPredicates().get("billNbr");
					marks = criteria.getPredicates().get("marks");
					packages = criteria.getPredicates().get("packages");
					cargoDesc = criteria.getPredicates().get("cargoDesc");
					tonnage = criteria.getPredicates().get("tonnage");
					newMarks = criteria.getPredicates().get("newMarks");
					newPackages = criteria.getPredicates().get("newPackages");
					newCargoDesc = criteria.getPredicates().get("newCargoDesc");
					newTonnage = criteria.getPredicates().get("newTonnage");
					// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel

					fromDate = fromDate + fromTime;
					toDate = toDate + toTime;

					gbMiscApplicationService.addUseOfSpaceDetails(userId, applyType, status, cust, account, varcode,
							coName, spaceType, purpose, fromDate, toDate, reason, billNbr, marks, packages, cargoDesc,
							tonnage, newMarks, newPackages, newCargoDesc, newTonnage, appDate, conPerson, conTel);

					// remove session attributes
					map.remove("appDate");
					map.remove("status");
					map.remove("spaceType");
					map.remove("purpose");
					map.remove("fromDate");
					map.remove("fromTime");
					map.remove("toDate");
					map.remove("toTime");
					map.remove("reason");
					map.remove("billNbr");
					map.remove("marks");
					map.remove("packages");
					map.remove("cargoDesc");
					map.remove("tonnage");
					map.remove("newMarks");
					map.remove("newPackages");
					map.remove("newCargoDesc");
					map.remove("newTonnage");
//							Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("conPerson");
					map.remove("conTel");
					// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("vesselList");
					// forwardHandler(request, "MiscAppList");
					// return;
				}
			} else if ("CUST_NEXT".equals(command) || "VSL_NEXT".equals(command)) {
				List<MiscAppValueObject> purposeList = gbMiscApplicationService.getPurposeList();
				map.put("PurposeList", purposeList);
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAddSpace : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddSpace : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAddSpace result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAddReeferHandler -->perform
	public ResponseEntity<?> miscAppAddReefer(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppAddReefer criteria:" + criteria.toString() + ", map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");
			String applyType = null;
			String status = null;
			String coName = null;
			String cust = null;
			String account = null;
			String varcode = null;
			String appDate = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
			String conPerson = null;
			String conTel = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

			String[] cntrNo = new String[12];
			String[] cntrSize = new String[12];
			String[] cntrStatus = new String[12];

			applyType = criteria.getPredicates().get("applyType");
			cust = criteria.getPredicates().get("cust");
			account = criteria.getPredicates().get("account");
			varcode = criteria.getPredicates().get("varcode");
			coName = criteria.getPredicates().get("coName");

			if ("ADD_SUBMIT".equals(command)) {
				if (agree == null) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					cntrNo = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrNo");
					cntrSize = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrSize");
					cntrStatus = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrStatus");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					if ("D".equals(status)) {
						/*
						 * miscAppEjb.addReeferContainerPowerOutletDetails(userId, applyType, status,
						 * cust, account, varcode, cntrNo, cntrSize, cntrStatus, coName, appDate);
						 */
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						gbMiscApplicationService.addReeferContainerPowerOutletDetails(userId, applyType, status, cust,
								account, varcode, cntrNo, cntrSize, cntrStatus, coName, appDate, conPerson, conTel);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("appDate", appDate);
						map.put("cntrNo", cntrNo);
						map.put("cntrSize", cntrSize);
						map.put("cntrStatus", cntrStatus);
						map.put("status", criteria.getPredicates().get("status"));
						// map.put("coName", request.getParameter("coName"));
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						map.put("conPerson", conPerson);
						map.put("conTel", conTel);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					}
				} else if (agree != null && agree.equals("true")) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					coName = criteria.getPredicates().get("coName");
					cntrNo = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrNo");
					cntrSize = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrSize");
					cntrStatus = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrStatus");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					/*
					 * miscAppEjb.addReeferContainerPowerOutletDetails(userId, applyType, status,
					 * cust, account, varcode, cntrNo, cntrSize, cntrStatus, coName, appDate);
					 */

					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					gbMiscApplicationService.addReeferContainerPowerOutletDetails(userId, applyType, status, cust,
							account, varcode, cntrNo, cntrSize, cntrStatus, coName, appDate, conPerson, conTel);
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					// remove session attributes
					map.remove("appDate");
					map.remove("cntrNo");
					map.remove("cntrSize");
					map.remove("cntrStatus");
					map.remove("status");
					map.remove("coName");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("conPerson");
					map.remove("conTel");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("vesselList");
					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAddReefer : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddReefer : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAddReefer result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAddSpreaderHandler -->perform
	public ResponseEntity<?> miscAppAddSpreader(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppAddSpreader criteria:" + criteria.toString() + ", map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");

			String applyType = criteria.getPredicates().get("applyType");
			String coName = criteria.getPredicates().get("coName");
			String cust = criteria.getPredicates().get("cust");
			String account = criteria.getPredicates().get("account");
			String varcode = criteria.getPredicates().get("varcode");
			String status = null;
			String spreaderType = null;
			String fromDate = null;
			String fromTime = null;
			String toDate = null;
			String toTime = null;
			String remarks = null;
			String appDate = null;

			if ("ADD_SUBMIT".equals(command)) {
				if (agree == null) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					spreaderType = criteria.getPredicates().get("spreaderType");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					remarks = criteria.getPredicates().get("remarks");

					if ("D".equals(status)) {
						fromDate = fromDate + fromTime;
						toDate = toDate + toTime;

						boolean successBooking = gbMiscApplicationService.addSpreaderDetails(userId, applyType, status,
								cust, account, varcode, coName, spreaderType, fromDate, toDate, remarks, appDate);
						if (!successBooking) {
							errorMessage = "Spreader for the chosen type is fully booked for the specified duration.";
						}
//								else{
//		    						forwardHandler(request, "MiscAppList");
//		    						return;
//								}
					} else {
						map.put("appDate", appDate);
						map.put("status", criteria.getPredicates().get("status"));
						map.put("spreaderType", spreaderType);
						map.put("fromDate", fromDate);
						map.put("fromTime", fromTime);
						map.put("toDate", toDate);
						map.put("toTime", toTime);
						map.put("remarks", remarks);
					}
				} else if (agree != null && agree.equals("true")) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					spreaderType = criteria.getPredicates().get("spreaderType");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					remarks = criteria.getPredicates().get("remarks");
					fromDate = fromDate + fromTime;
					toDate = toDate + toTime;

					boolean successBooking = gbMiscApplicationService.addSpreaderDetails(userId, applyType, status,
							cust, account, varcode, coName, spreaderType, fromDate, toDate, remarks, appDate);

					if (!successBooking) {
						errorMessage = "Spreader for the chosen type is fully booked for the specified duration.";
					} else {
						// remove session attributes
						map.remove("appDate");
						map.remove("status");
						map.remove("spreaderType");
						map.remove("fromDate");
						map.remove("fromTime");
						map.remove("toDate");
						map.remove("toTime");
						map.remove("remarks");
						map.remove("vesselList");
						// forwardHandler(request, "MiscAppList");
						// return;
					}
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAddSpreader : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddSpreader : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAddSpreader result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	public ResponseEntity<?> miscAppAddStationMac(HttpServletRequest request, Criteria criteria,
			Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppAddStationMac criteria:" + criteria.toString() + ", map: " + map.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String command = criteria.getPredicates().get("command");

			String status = null;
			String appDate = null;

			String macType = null;
			String fromDate = null;
			String toDate = null;
			List<MiscAppValueObject> docTypeList = null;
			List<String> docNameList = null;
			List<String> docCdList = null;
			List<String> regNbrList = new ArrayList<String>();
			List<String> liftCapacityList = new ArrayList<String>();
			List<String> insuranceNbrList = new ArrayList<String>();
			List<String> insExpDttmList = new ArrayList<String>();

			// String applyType = criteria.getPredicates().get("applyType");
			// String applyStatus = criteria.getPredicates().get("applyStatus");
			// String cust = criteria.getPredicates().get("cust");
			// String account = criteria.getPredicates().get("account");
			// String varcode = criteria.getPredicates().get("varcode");
			// String coName = criteria.getPredicates().get("coName");

			map.put("coCd", coCd);
			if ("CUST_NEXT".equals(command) || "VSL_NEXT".equals(command)) {
				docTypeList = gbMiscApplicationService.getUploadDocumentTypeList();
				List<MiscAppValueObject> docList = gbMiscApplicationService.getUploadDocumentList();
				map.put("docTypeList", docTypeList);
				map.put("docList", docList);

				// remove session attributes
				// removeSessionAttributes(session);
			} else if ("UPLOAD_SUBMIT".equals(command)) {
				Object listObject = null;
				listObject = request.getAttribute("docTypeList");
				docTypeList = new ArrayList<MiscAppValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docTypeList.add((MiscAppValueObject) item);
						}
					}
				}

				listObject = request.getAttribute("docNameList");
				docNameList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docNameList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("docCdList");
				docCdList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docCdList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("regNbrList");
				regNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							regNbrList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("liftCapacityList");
				liftCapacityList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							liftCapacityList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("insuranceNbrList");
				insuranceNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							insuranceNbrList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("insExpDttmList");
				insExpDttmList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							insExpDttmList.add((String) item);
						}
					}
				}

				appDate = criteria.getPredicates().get("appDate");
				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");

				if (regNbrList != null)
					map.put("regNbr", regNbrList.toArray(new String[0]));
				if (liftCapacityList != null)
					map.put("liftCapacity", liftCapacityList.toArray(new String[0]));
				if (insuranceNbrList != null)
					map.put("insuranceNbr", insuranceNbrList.toArray(new String[0]));
				if (insExpDttmList != null)
					map.put("insExpDttm", insExpDttmList.toArray(new String[0]));
			} else if ("ADD_SUBMIT".equals(command)) {
				Object listObject = null;
				listObject = request.getAttribute("docTypeList");
				docTypeList = new ArrayList<MiscAppValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docTypeList.add((MiscAppValueObject) item);
						}
					}
				}

				listObject = request.getAttribute("docNameList");
				docNameList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docNameList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("docCdList");
				docCdList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docCdList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("regNbrList");
				regNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							regNbrList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("liftCapacityList");
				liftCapacityList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							liftCapacityList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("insuranceNbrList");
				insuranceNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							insuranceNbrList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("insExpDttmList");
				insExpDttmList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							insExpDttmList.add((String) item);
						}
					}
				}

				appDate = criteria.getPredicates().get("appDate");
				status = criteria.getPredicates().get("status");
				// Amended by Punitha on 01/08/2008
				/*
				 * macType = session.getAttribute("macType"); fromDate =
				 * session.getAttribute("fromDate"); toDate = session.getAttribute("toDate");
				 */

				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");

				// End by Punitha

				MiscParkMacValueObject obj = new MiscParkMacValueObject();
				obj.setMacType(macType);
				obj.setFromDate(fromDate);
				obj.setToDate(toDate);
				if (docTypeList != null)
					obj.setDocType((String[]) docTypeList.toArray(new String[0]));
				if (docNameList != null)
					obj.setDocName((String[]) docNameList.toArray(new String[0]));
				if (regNbrList != null)
					obj.setRegNbr((String[]) regNbrList.toArray(new String[0]));
				if (liftCapacityList != null)
					obj.setLiftCapacity((String[]) liftCapacityList.toArray(new String[0]));
				if (insuranceNbrList != null)
					obj.setInsuranceNbr((String[]) insuranceNbrList.toArray(new String[0]));
				if (insExpDttmList != null)
					obj.setInsExpDttm((String[]) insExpDttmList.toArray(new String[0]));

				/*
				 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!! change
				 * gbMiscApplicationService.addStationingOfMacDetails(userId, applyType, status,
				 * cust, account, varcode, coName, obj, appDate);
				 */

				// write file into server
				/*
				 * byte[] fileContent = null; if(docNameList != null && assignFileName != null){
				 * //System.out.println("assignFileName -----> " + assignFileName.size());
				 * for(int i = 0; i < docNameList.size(); i++){ fileContent =
				 * UploadDocument.getFileContent(docNameList.get(i));
				 * UploadDocument.writeToFile(fileContent, assignFileName.get(i), "MACHINE"); }
				 * }
				 */

				// remove session attributes
				// removeSessionAttributes(session);
				map.remove("coCd");
				// forwardHandler(request, "MiscAppList");
				// return;
			} else {
				appDate = criteria.getPredicates().get("appDate");
				status = criteria.getPredicates().get("status");
				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");
				// String docType = criteria.getPredicates().get("docType");
				// String file = criteria.getPredicates().get("file");

				Object listObject = null;
				listObject = request.getAttribute("docTypeList");
				docTypeList = new ArrayList<MiscAppValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docTypeList.add((MiscAppValueObject) item);
						}
					}
				}

				docNameList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docNameList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("docCdList");
				docCdList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docCdList.add((String) item);
						}
					}
				}
				regNbrList.add(criteria.getPredicates().get("regNbrList"));
				liftCapacityList.add(criteria.getPredicates().get("liftCapacityList"));
				insuranceNbrList.add(criteria.getPredicates().get("insuranceNbrList"));
				insExpDttmList.add(criteria.getPredicates().get("insExpDttmList"));

				if ("UPLOAD".equals(command)) {
					/*
					 * if (docTypeList == null) { docTypeList = new ArrayList();
					 * docTypeList.add(docType); map.put("docTypeList", docTypeList); } else {
					 * docTypeList.add(docType); } if (docNameList == null) { docNameList = new
					 * ArrayList(); docNameList.add(file); map.put("docNameList", docNameList); }
					 * else { docNameList.add(file); }
					 */

					map.put("subDirName", "MACHINE");
					map.put("nextScreen", "MiscAppAddStationMac");
					
				} else if ("MAC_SUBMIT".equals(command)) {
					macType = criteria.getPredicates().get("macType");
					fromDate = criteria.getPredicates().get("fromDate");
					toDate = criteria.getPredicates().get("toDate");

					String regNbr = criteria.getPredicates().get("regNbr");
					String liftCapacity = criteria.getPredicates().get("liftCapacity");
					String insuranceNbr = criteria.getPredicates().get("insuranceNbr");
					String insExpDttm = criteria.getPredicates().get("insExpDttm");
					if (regNbrList.size() == 0) {
						regNbrList = new ArrayList<String>();
						regNbrList.add(regNbr);
						map.put("regNbrList", regNbrList);
					} else {
						regNbrList.add(regNbr);
					}
					if (liftCapacityList.size() == 0) {
						liftCapacityList = new ArrayList<String>();
						liftCapacityList.add(liftCapacity);
						map.put("liftCapacityList", liftCapacityList);
					} else {
						liftCapacityList.add(liftCapacity);
					}
					if (insuranceNbrList.size() == 0) {
						insuranceNbrList = new ArrayList<String>();
						insuranceNbrList.add(insuranceNbr);
						map.put("insuranceNbrList", insuranceNbrList);
					} else {
						insuranceNbrList.add(insuranceNbr);
					}
					if (insExpDttmList.size() == 0) {
						insExpDttmList = new ArrayList<String>();
						insExpDttmList.add(insExpDttm);
						map.put("insExpDttmList", insExpDttmList);
					} else {
						insExpDttmList.add(insExpDttm);
					}
				} else if ("MAC_DELETE".equals(command)) {
					String[] delList = CommonUtil.getRequiredStringParameters(request, "macCheck");
					List<String> removeRegNbrList = new ArrayList<String>();
					List<String> removeLiftCapacityList = new ArrayList<String>();
					List<String> removeInsuranceNbrList = new ArrayList<String>();
					List<String> removeInsuranceExpList = new ArrayList<String>();
					for (int i = 0; i < delList.length; i++) {
						removeRegNbrList.add(regNbrList.get(Integer.parseInt(delList[i])));
						removeLiftCapacityList.add(liftCapacityList.get(Integer.parseInt(delList[i])));
						removeInsuranceNbrList.add(insuranceNbrList.get(Integer.parseInt(delList[i])));
						removeInsuranceExpList.add(insExpDttmList.get(Integer.parseInt(delList[i])));
					}
					for (int i = 0; i < removeRegNbrList.size(); i++) {
						regNbrList.remove(removeRegNbrList.get(i));
					}
					for (int i = 0; i < removeLiftCapacityList.size(); i++) {
						liftCapacityList.remove(removeLiftCapacityList.get(i));
					}
					for (int i = 0; i < removeInsuranceNbrList.size(); i++) {
						insuranceNbrList.remove(removeInsuranceNbrList.get(i));
					}
					for (int i = 0; i < removeInsuranceExpList.size(); i++) {
						insExpDttmList.remove(removeInsuranceExpList.get(i));
					}
				}
				if (docTypeList != null)
					map.put("docTypeTemp", docTypeList.toArray(new String[0]));
				if (docNameList != null)
					map.put("docNameTemp", docNameList.toArray(new String[0]));
				if (docCdList != null)
					map.put("docCdTemp", docCdList.toArray(new String[0]));
				if (regNbrList != null)
					map.put("regNbr", regNbrList.toArray(new String[0]));
				if (liftCapacityList != null)
					map.put("liftCapacity", liftCapacityList.toArray(new String[0]));
				if (insuranceNbrList != null)
					map.put("insuranceNbr", insuranceNbrList.toArray(new String[0]));
				if (insExpDttmList != null)
					map.put("insExpDttm", insExpDttmList.toArray(new String[0]));

				map.put("appDate", appDate);
				map.put("status", status);
				map.put("macType", macType);
				map.put("fromDate", fromDate);
				map.put("toDate", toDate);
			}
			if ("MAC_ADD".equals(command)) {
				// nextScreen(request, "MiscAppAddStationMac");
				// return;
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAddStationMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddStationMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAddStationMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAddStationMacHandler -->perform
	@RequestMapping(value = "/miscAppAddStationMac", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddStationMac(HttpServletRequest request,
			@RequestBody MiscParkMacBodyVO objDettails) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddStationMac2 criteria:" + criteria.toString() + ", objDettails: " + objDettails.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String command = objDettails.getCommand();

			// String applyType = null;
			// String applyStatus = null;
			// String cust = null;
			// String coName = null;
			// String account = null;
			// String varcode = null;
			String status = null;
			String appDate = null;

			String macType = null;
			String fromDate = null;
			String toDate = null;
			// String docType = null;
			// String file = null;
			List<MiscAppValueObject> docTypeList = null;
			
			List<String> docNameList = null;
			List<String> docCdList = null;
			List<String> regNbrList = new ArrayList<String>();
			List<String> liftCapacityList = new ArrayList<String>();
			List<String> insuranceNbrList = new ArrayList<String>();
			List<String> insExpDttmList = new ArrayList<String>();
			
			//	applyType = criteria.getPredicates().get("applyType");
			//	applyStatus = criteria.getPredicates().get("applyStatus");
			//	cust = criteria.getPredicates().get("cust");
			//	account = criteria.getPredicates().get("account");
			//	varcode = criteria.getPredicates().get("varcode");
			//	coName = criteria.getPredicates().get("coName");

			map.put("coCd", coCd);
			if ("CUST_NEXT".equals(command) || "VSL_NEXT".equals(command)) {
				docTypeList = gbMiscApplicationService.getUploadDocumentTypeList();
				List<MiscAppValueObject> docList = gbMiscApplicationService.getUploadDocumentList();
				map.put("docTypeList", docTypeList);
				map.put("docList", docList);

				// remove session attributes
				// removeSessionAttributes(session);
			} else if ("UPLOAD_SUBMIT".equals(command)) {
				Object listObject = null;
				listObject = request.getAttribute("docTypeList");
				docTypeList = new ArrayList<MiscAppValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docTypeList.add((MiscAppValueObject) item);
						}
					}
				}

				listObject = request.getAttribute("docNameList");
				docNameList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docNameList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("docCdList");
				docCdList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docCdList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("regNbrList");
				regNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							regNbrList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("liftCapacityList");
				liftCapacityList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							liftCapacityList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("insuranceNbrList");
				insuranceNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							insuranceNbrList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("insExpDttmList");
				insExpDttmList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							insExpDttmList.add((String) item);
						}
					}
				}

				appDate = objDettails.getAppDate();
				macType = objDettails.getMacType();
				fromDate = objDettails.getFromDate();
				toDate = objDettails.getToDate();

				if (regNbrList != null)
					map.put("regNbr", regNbrList.toArray(new String[0]));
				if (liftCapacityList != null)
					map.put("liftCapacity", liftCapacityList.toArray(new String[0]));
				if (insuranceNbrList != null)
					map.put("insuranceNbr", insuranceNbrList.toArray(new String[0]));
				if (insExpDttmList != null)
					map.put("insExpDttm", insExpDttmList.toArray(new String[0]));
			} else if ("ADD_SUBMIT".equals(command)) {
				Object listObject = null;
				listObject = request.getAttribute("docTypeList");
				docTypeList = new ArrayList<MiscAppValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docTypeList.add((MiscAppValueObject) item);
						}
					}
				}

				listObject = request.getAttribute("docNameList");
				docNameList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docNameList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("docCdList");
				docCdList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docCdList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("regNbrList");
				regNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							regNbrList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("liftCapacityList");
				liftCapacityList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							liftCapacityList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("insuranceNbrList");
				insuranceNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							insuranceNbrList.add((String) item);
						}
					}
				}
				listObject = request.getAttribute("insExpDttmList");
				insExpDttmList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							insExpDttmList.add((String) item);
						}
					}
				}

				String cust = objDettails.getCust();
				String account = objDettails.getAccount();
				String varcode = objDettails.getVarcode();
				String userId = criteria.getPredicates().get("userAccount");
				String applyType = objDettails.getApplyType();
				String coName = objDettails.getCoName();
				appDate = objDettails.getAppDate();
				status = objDettails.getStatus();
				// Amended by Punitha on 01/08/2008
				/*
				 * macType = session.getAttribute("macType"); fromDate = (String)
				 * session.getAttribute("fromDate"); toDate = (String)
				 * session.getAttribute("toDate");
				 */

				macType = objDettails.getMacType();
				fromDate = objDettails.getFromDate();
				toDate = objDettails.getToDate();
			
				// End by Punitha
				MiscParkMacValueObject obj = objDettails;
				obj.getMacDetList();
				obj.setMacType(macType);
				obj.setFromDate(fromDate);
				obj.setToDate(toDate);
				if (docTypeList != null)
					obj.setDocType((String[]) docTypeList.toArray(new String[0]));
				if (docNameList != null)
					obj.setDocName((String[]) docNameList.toArray(new String[0]));
				if (regNbrList != null)
					obj.setRegNbr((String[]) regNbrList.toArray(new String[0]));
				if (liftCapacityList != null)
					obj.setLiftCapacity((String[]) liftCapacityList.toArray(new String[0]));
				if (insuranceNbrList != null)
					obj.setInsuranceNbr((String[]) insuranceNbrList.toArray(new String[0]));
				if (insExpDttmList != null)
					obj.setInsExpDttm((String[]) insExpDttmList.toArray(new String[0]));

				String miscSeqNbr =  gbMiscApplicationService.addStationingOfMacDetails(userId, applyType,
				 status, cust, account, varcode, coName, obj, appDate);

				// write file into server
				/*
				 * byte[] fileContent = null; if(docNameList != null && assignFileName != null){
				 * //System.out.println("assignFileName -----> " + assignFileName.size());
				 * for(int i = 0; i < docNameList.size(); i++){ fileContent =
				 * UploadDocument.getFileContent(docNameList.get(i));
				 * UploadDocument.writeToFile(fileContent, assignFileName.get(i), "MACHINE"); }
				 * }
				 */

				// remove session attributes
				// removeSessionAttributes(session);
				map.remove("coCd");
				map.put("miscSeqNbr", miscSeqNbr);
				// forwardHandler(request, "MiscAppList");
				// return;
			} else {
				appDate = objDettails.getAppDate();
				status = objDettails.getStatus();
				macType = objDettails.getMacType();
				fromDate = objDettails.getFromDate();
				toDate = objDettails.getToDate();
				// docType = criteria.getPredicates().get("docType");
				// file = criteria.getPredicates().get("file");

				Object listObject = null;
				listObject = request.getAttribute("docTypeList");
				docTypeList = new ArrayList<MiscAppValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docTypeList.add((MiscAppValueObject) item);
						}
					}
				}

				listObject = request.getAttribute("docNameList");
				docNameList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docNameList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("docCdList");
				docCdList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							docCdList.add((String) item);
						}
					}
				}
				regNbrList.add(criteria.getPredicates().get("regNbrList"));
				liftCapacityList.add(criteria.getPredicates().get("liftCapacityList"));
				insuranceNbrList.add(criteria.getPredicates().get("insuranceNbrList"));
				insExpDttmList.add(criteria.getPredicates().get("insExpDttmList"));

				if ("UPLOAD".equals(command)) {
					/*
					 * if (docTypeList == null) { docTypeList = new ArrayList();
					 * docTypeList.add(docType); map.put("docTypeList", docTypeList); } else {
					 * docTypeList.add(docType); } if (docNameList == null) { docNameList = new
					 * ArrayList(); docNameList.add(file); map.put("docNameList", docNameList); }
					 * else { docNameList.add(file); }
					 */
					docTypeList = gbMiscApplicationService.getUploadDocumentTypeList();
					map.put("subDirName", "MACHINE");
					map.put("nextScreen", "MiscAppAddStationMac");
				} else if ("DELETE".equals(command)) {
					/*
					 * String[] delList = request.getParameterValues("docCheck"); ArrayList
					 * removeTypeList = new ArrayList(); ArrayList removeNameList = new ArrayList();
					 * for (int i = 0; i < delList.length; i++) { removeTypeList.add(
					 * docTypeList.get(Integer.parseInt(delList[i]))); removeNameList.add(
					 * docNameList.get(Integer.parseInt(delList[i]))); } for (int i = 0; i <
					 * removeTypeList.size(); i++) { docTypeList.remove(removeTypeList.get(i)); }
					 * for (int i = 0; i < removeNameList.size(); i++) {
					 * docNameList.remove(removeNameList.get(i)); }
					 */

					String[] delList = objDettails.getDocCheck().split(",");
					String[] miscSeqNbrList = objDettails.getMiscSeqNbr().split(",");
					String[] assignedNameList = objDettails.getAssignedName().split(",");
					String userId = criteria.getPredicates().get("userAccount");
					List<MiscAppValueObject> removeTypeList = new ArrayList<MiscAppValueObject>();
					List<String> removeNameList = new ArrayList<String>();
					List<String> removeCdList = new ArrayList<String>();
					/*
					 * for (int i = 0; i < delList.length; i++) { removeTypeList.add(
					 * docTypeList.get(Integer.parseInt(delList[i]))); removeNameList.add(
					 * docNameList.get(Integer.parseInt(delList[i]))); }
					 */

					List<String> deletedDoc = gbMiscApplicationService.getDelFile(objDettails.getAssignedName(), objDettails.getMiscSeqNbr(), "MACHINE" , "MISC_MDOC");
					
					for (int i = 0; i < delList.length; i++) {
						String miscSeqNbr = miscSeqNbrList[i];
						Path pathFile = Paths.get(deletedDoc.get(i)).toAbsolutePath().normalize(); 
						UploadDocument.deleteFile(pathFile.toString());
						gbMiscApplicationService.deleteFileData(userId,miscSeqNbr,assignedNameList[i]);

						if (docTypeList != null && docTypeList.size() > 0) {
							removeTypeList.add(docTypeList.get(Integer.parseInt(delList[i])));
							removeNameList.add(docNameList.get(Integer.parseInt(delList[i])));
							removeCdList.add(docCdList.get(Integer.parseInt(delList[i])));
						}
					}

					for (int i = 0; i < removeTypeList.size(); i++) {
						docTypeList.remove(removeTypeList.get(i));
					}
					for (int i = 0; i < removeNameList.size(); i++) {
						docNameList.remove(removeNameList.get(i));
					}
					for (int i = 0; i < removeCdList.size(); i++) {
						docCdList.remove(removeCdList.get(i));
					}
				} else if ("MAC_SUBMIT".equals(command)) {
					listObject = request.getAttribute("regNbrList");
					regNbrList = new ArrayList<String>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								regNbrList.add((String) item);
							}
						}
					}
					listObject = request.getAttribute("liftCapacityList");
					liftCapacityList = new ArrayList<String>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								liftCapacityList.add((String) item);
							}
						}
					}
					listObject = request.getAttribute("insuranceNbrList");
					insuranceNbrList = new ArrayList<String>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								insuranceNbrList.add((String) item);
							}
						}
					}
					listObject = request.getAttribute("insExpDttmList");
					insExpDttmList = new ArrayList<String>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								insExpDttmList.add((String) item);
							}
						}
					}

					macType = objDettails.getMacType();
					fromDate = objDettails.getFromDate();
					toDate = objDettails.getToDate();

					String regNbr = objDettails.getRegNbrValue();
					String liftCapacity = objDettails.getLiftCapacityValue();
					String insuranceNbr = objDettails.getInsuranceNbrValue();
					String insExpDttm = objDettails.getInsExpDttmValue();
					if (regNbrList.size() == 0) {
						regNbrList = new ArrayList<String>();
						regNbrList.add(regNbr);
						map.put("regNbrList", regNbrList);
					} else {
						regNbrList.add(regNbr);
					}
					if (liftCapacityList.size() == 0) {
						liftCapacityList = new ArrayList<String>();
						liftCapacityList.add(liftCapacity);
						map.put("liftCapacityList", liftCapacityList);
					} else {
						liftCapacityList.add(liftCapacity);
					}
					if (insuranceNbrList.size() == 0) {
						insuranceNbrList = new ArrayList<String>();
						insuranceNbrList.add(insuranceNbr);
						map.put("insuranceNbrList", insuranceNbrList);
					} else {
						insuranceNbrList.add(insuranceNbr);
					}
					if (insExpDttmList.size() == 0) {
						insExpDttmList = new ArrayList<String>();
						insExpDttmList.add(insExpDttm);
						map.put("insExpDttmList", insExpDttmList);
					} else {
						insExpDttmList.add(insExpDttm);
					}
				} else if ("MAC_DELETE".equals(command)) {
					String[] delList = CommonUtil.getRequiredStringParameters(request, "macCheck");
					List<String> removeRegNbrList = new ArrayList<String>();
					List<String> removeLiftCapacityList = new ArrayList<String>();
					List<String> removeInsuranceNbrList = new ArrayList<String>();
					List<String> removeInsuranceExpList = new ArrayList<String>();
					for (int i = 0; i < delList.length; i++) {
						removeRegNbrList.add(regNbrList.get(Integer.parseInt(delList[i])));
						removeLiftCapacityList.add(liftCapacityList.get(Integer.parseInt(delList[i])));
						removeInsuranceNbrList.add(insuranceNbrList.get(Integer.parseInt(delList[i])));
						removeInsuranceExpList.add(insExpDttmList.get(Integer.parseInt(delList[i])));
					}
					for (int i = 0; i < removeRegNbrList.size(); i++) {
						regNbrList.remove(removeRegNbrList.get(i));
					}
					for (int i = 0; i < removeLiftCapacityList.size(); i++) {
						liftCapacityList.remove(removeLiftCapacityList.get(i));
					}
					for (int i = 0; i < removeInsuranceNbrList.size(); i++) {
						insuranceNbrList.remove(removeInsuranceNbrList.get(i));
					}
					for (int i = 0; i < removeInsuranceExpList.size(); i++) {
						insExpDttmList.remove(removeInsuranceExpList.get(i));
					}
				}
				if (docTypeList != null)
					map.put("docTypeTemp", docTypeList);
				if (docNameList != null)
					map.put("docNameTemp", docNameList.toArray(new String[0]));
				if (docCdList != null)
					map.put("docCdTemp", docCdList.toArray(new String[0]));
				if (regNbrList != null)
					map.put("regNbr", regNbrList.toArray(new String[0]));
				if (liftCapacityList != null)
					map.put("liftCapacity", liftCapacityList.toArray(new String[0]));
				if (insuranceNbrList != null)
					map.put("insuranceNbr", insuranceNbrList.toArray(new String[0]));
				if (insExpDttmList != null)
					map.put("insExpDttm", insExpDttmList.toArray(new String[0]));

				map.put("appDate", appDate);
				map.put("status", status);
				map.put("macType", macType);
				map.put("fromDate", fromDate);
				map.put("toDate", toDate);
			}
			if ("MAC_ADD".equals(command)) {
				// nextScreen(request, "MiscAppAddStationMac");
				// return;
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAddStationMac2 : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddStationMac2 : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAddStationMac2 result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAddVehHandler -->perform
	public ResponseEntity<?> miscAppAddVeh(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppAddVeh criteria:" + criteria.toString() + " map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String applyType = null;

			applyType = criteria.getPredicates().get("applyType");
			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");

			String appDate = null;
			String cust = null;
			String coName = null;
			String account = null;
			String varcode = null;
			String fromDate = null;
			String toDate = null;
			String noNights = null;
			String parkReason = null;
			String status = null;
			String[] vehNo = null;
			String[] cntNo = null;
			String[] asnNo = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
			String conPerson = null;
			String conTel = null;
			String conEmail = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

			applyType = criteria.getPredicates().get("applyType");
			cust = criteria.getPredicates().get("cust");
			account = criteria.getPredicates().get("account");
			varcode = criteria.getPredicates().get("varcode");
			coName = criteria.getPredicates().get("coName");

			TextParaVO textParaVO = new TextParaVO();
			textParaVO.setParaCode("TPA_CUTOFF");

			textParaVO = textParaReo.getParaCodeInfo(textParaVO);
			if (textParaVO != null) {
				map.put("cutOffDate", textParaVO.getValue());
			} else {
				map.put("cutOffDate", "");
			}
			// END updated by ThangPV, on 02/10/2013: Add validation for TO_TIME <=
			// CUT_OFF_DATE

			if ("ADD_SUBMIT".equals(command)) {
				if (agree == null) {

					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					fromDate = criteria.getPredicates().get("fromDate");
					toDate = criteria.getPredicates().get("toDate");
					noNights = criteria.getPredicates().get("noNights");
					parkReason = criteria.getPredicates().get("parkReason");

					vehNo = CommonUtil.getRequiredStringParameters(request, "vehNo");
					cntNo = CommonUtil.getRequiredStringParameters(request, "cntNo");
					asnNo = CommonUtil.getRequiredStringParameters(request, "asnNo");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					conEmail = criteria.getPredicates().get("conEmail");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					List<String> invalidChassis = gbMiscApplicationService.checkMiscAppOnvChassis(vehNo);
					if (invalidChassis.size() > 0) {
						errorMessage = Arrays.toString(invalidChassis.toArray())
								+ " does not registered in Vehicle Info Registration module. Please register first";
						// return;
					}

					List<String> invalidContainer = gbMiscApplicationService.checkMiscAppOnvContainer(cntNo);
					if (invalidContainer.size() > 0) {
						errorMessage = Arrays.toString(invalidContainer.toArray())
								+ " are invalid container. Please check.";
						// return;
					}
					// Added to not check character ASN
					String tmpAsn = "";
					for (int i = 0; i < asnNo.length; i++) {
						if (!StringUtils.isNumeric(asnNo[i])) {
							log.info("Inside asn***" + asnNo[i]);
							tmpAsn = tmpAsn + " , " + asnNo[i];
						}
					}
					if (tmpAsn != null && !tmpAsn.trim().equalsIgnoreCase("")) {
						errorMessage = tmpAsn + " are invalid Asn. Please check.";
						// return;
					}
					List<String> invalidAsn = gbMiscApplicationService.checkMiscAppOnvAsn(asnNo);
					if (invalidAsn.size() > 0) {
						errorMessage = Arrays.toString(invalidAsn.toArray()) + " are invalid Asn. Please check.";
						// return;
					}

					if ("D".equals(status)) {
						/*
						 * gbMiscApplicationService.addOvernightParkingVehicleDetails(userId, applyType,
						 * status, cust, account, varcode, fromDate, toDate, noNights, parkReason,
						 * vehNo, cntNo, asnNo, coName, appDate);
						 */
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						gbMiscApplicationService.addOvernightParkingVehicleDetails(userId, applyType, status, cust,
								account, varcode, fromDate, toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName,
								appDate, conPerson, conTel, conEmail);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("appDate", appDate);
						map.put("status", status);
						map.put("fromDate", fromDate);
						map.put("toDate", toDate);
						map.put("noNights", noNights);
						map.put("parkReason", parkReason);
						map.put("vehNo", vehNo);
						map.put("cntNo", cntNo);
						map.put("asnNo", asnNo);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						map.put("conPerson", conPerson);
						map.put("conTel", conTel);
						map.put("conEmail", conEmail);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					}
				} else if ("true".equals(agree)) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					fromDate = criteria.getPredicates().get("fromDate");
					toDate = criteria.getPredicates().get("toDate");
					noNights = criteria.getPredicates().get("noNights");
					parkReason = criteria.getPredicates().get("parkReason");
					vehNo = (String[]) CommonUtil.getRequiredStringParameters(request, "vehNo");
					cntNo = (String[]) CommonUtil.getRequiredStringParameters(request, "cntNo");
					asnNo = (String[]) CommonUtil.getRequiredStringParameters(request, "asnNo");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					conEmail = criteria.getPredicates().get("conEmail");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					/*
					 * miscAppEjb.addOvernightParkingVehicleDetails(userId, applyType, status, cust,
					 * account, varcode, fromDate, toDate, noNights, parkReason, vehNo, cntNo,
					 * asnNo, coName, appDate);
					 */
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					gbMiscApplicationService.addOvernightParkingVehicleDetails(userId, applyType, status, cust, account,
							varcode, fromDate, toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName, appDate,
							conPerson, conTel, conEmail);
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					// remove session attributes
					map.remove("appDate");
					map.remove("status");
					map.remove("fromDate");
					map.remove("toDate");
					map.remove("noNights");
					map.remove("parkReason");
					map.remove("vehNo");
					map.remove("cntNo");
					map.remove("asnNo");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("conPerson");
					map.remove("conTel");
					map.remove("conEmail");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("vesselList");
					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAddVeh : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddVeh : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAddVeh result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAddTpaHandler -->perform
	public ResponseEntity<?> miscAppAddTpa(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		Map<String, Object> miscAppMap = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddTpa criteria:" + criteria.toString() + ", map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String applyType = null;

			applyType = criteria.getPredicates().get("applyType");
			String command = criteria.getPredicates().get("command");

			String applyStatus = null;
			String appDate = null;
			String cust = null;
			String coName = null;
			String account = null;
			String varcode = null;
			String fromDate = null;
			String toDate = null;

			String appRemarks = null;
			String status = null;
			String[] vehNo = null;
			String[] cntNo = null;
			String[] asnNo = null;
			String conPerson = null;
			String conTel = null;

			String reasonForApp = null;
			String noHours = null;
			String cargoType = null;
			String[] preferredArea = null;
			String[] remarks = null;

			applyType = criteria.getPredicates().get("applyType");
			applyStatus = criteria.getPredicates().get("applyStatus");
			cust = criteria.getPredicates().get("cust");
			account = criteria.getPredicates().get("account");
			varcode = criteria.getPredicates().get("varcode");
			coName = criteria.getPredicates().get("coName");

			if ("ADD_SUBMIT".equals(command)) {
				appDate = criteria.getPredicates().get("appDate");
				status = criteria.getPredicates().get("status");
				fromDate = criteria.getPredicates().get("fromDate") + " " + criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate") + " " + criteria.getPredicates().get("toTime");
				vehNo = request.getParameterValues("vehNo");
				cntNo = request.getParameterValues("cntNo");
				asnNo = request.getParameterValues("asnNo");
				conPerson = criteria.getPredicates().get("conPerson");
				conTel = criteria.getPredicates().get("conTel");
				reasonForApp = criteria.getPredicates().get("reasonForApp");
				cargoType = criteria.getPredicates().get("cargoType");
				noHours = criteria.getPredicates().get("noHours");
				appRemarks = criteria.getPredicates().get("appRemarks");
				preferredArea = new String[5];
				preferredArea[0] = criteria.getPredicates().get("preferredArea1");
				preferredArea[1] = criteria.getPredicates().get("preferredArea2");
				preferredArea[2] = criteria.getPredicates().get("preferredArea3");
				preferredArea[3] = criteria.getPredicates().get("preferredArea4");
				preferredArea[4] = criteria.getPredicates().get("preferredArea5");

				remarks = request.getParameterValues("remarks");

				log.info("^^^^^^^^  applyType : " + applyType);
				log.info("^^^^^^^^  applyStatus : " + applyStatus);
				log.info("^^^^^^^^  cust : " + cust);
				log.info("^^^^^^^^  account : " + account);
				log.info("^^^^^^^^  varcode : " + varcode);
				log.info("^^^^^^^^  coName : " + coName);
				log.info("fromDate:" + criteria.getPredicates().get("fromDate"));
				log.info("toDate : " + criteria.getPredicates().get("toDate"));
				log.info("from time:" + criteria.getPredicates().get("fromTime"));
				log.info("to time : " + criteria.getPredicates().get("toTime"));

				log.info("TPA - appReasonList :" + reasonForApp);
				log.info("TPA - cargoType :" + cargoType);
				log.info("TPA - noHours :" + noHours);
				log.info("TPA - appRemarks :" + appRemarks);
				log.info("TPA - status :" + status);

				// Save - no need to validate available of parking slot
				if ("D".equals(status)) {

					gbMiscApplicationService.addTpaDetails(userId, applyType, status, cust, account, varcode, fromDate,
							toDate, noHours, appRemarks, vehNo, cntNo, asnNo, coName, appDate, conPerson, conTel,
							preferredArea, remarks, reasonForApp, cargoType);

					// forwardHandler(request, "MiscAppList");
					// return;
				} else {
					String errorCode = this.validate(request, gbMiscApplicationService, vehNo, preferredArea);

					if (!"0".equalsIgnoreCase(errorCode)) {
						// return current page with error message
						log.info("Unable to submit as slot is not available. Error code " + errorCode);
						List<Map<String, Object>> miscParkReasonList = gbMiscApplicationService.getParkingReasonList();
						map.put("MiscParkReasonList", miscParkReasonList);

						map.put("errorCode", errorCode);
						// nextScreen(request, "MiscAppAddTpa");
						// return;
					} else {
						if (!CARGO_TYPE_NORMAL.equalsIgnoreCase(cargoType)) {
							gbMiscApplicationService.addTpaDetails(userId, applyType, status, cust, account, varcode,
									fromDate, toDate, noHours, appRemarks, vehNo, cntNo, asnNo, coName, appDate,
									conPerson, conTel, preferredArea, remarks, reasonForApp, cargoType);
							// forwardHandler(request, "MiscAppList");
							// return;
						} else {
							// Empty and normal then auto assign and approve
							String miscSeqNbr = gbMiscApplicationService.processForTpaEmptyNormal(userId, applyType,
									status, cust, account, varcode, fromDate, toDate, noHours, appRemarks, vehNo, cntNo,
									asnNo, coName, appDate, conPerson, conTel, preferredArea, remarks, reasonForApp,
									cargoType);
							log.info("miscSeqNbr:" + miscSeqNbr);
							if (miscSeqNbr == null || miscSeqNbr.equals("")) {
								// Error
								log.info("Unable to submit as slot is not available.");
								List<Map<String, Object>> miscParkReasonList = gbMiscApplicationService
										.getParkingReasonList();
								map.put("MiscParkReasonList", miscParkReasonList);

								map.put("errorCode", "2");
								// nextScreen(request, "MiscAppAddTpa");
								// return;
							} else {
								// To get application, vehicle detail information and redirect to auto approve
								// page
								List<Object> list = gbMiscApplicationService.getTrailerParkingApplicationDetails(userId,
										applyType, miscSeqNbr, "");

								map.put("details", list);
								map.put("autoApprove", "true");
								// nextScreen(request, "MiscAppAddTpa");
							}
						}
					}
				}
			} else if ("POPUP".equals(command)) {
				String componentName = criteria.getPredicates().get("componentName");
				map.put("componentName", componentName);
				fromDate = criteria.getPredicates().get("fromDate") + " " + criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate") + " " + criteria.getPredicates().get("toTime");
				cargoType = criteria.getPredicates().get("cargoType");

				log.info("fromDate:" + criteria.getPredicates().get("fromDate"));
				log.info("toDate : " + criteria.getPredicates().get("toDate"));
				log.info("TPA - cargoType :" + cargoType);

				// get list of parking area
				List<MiscAppParkingAreaObject> miscParkAreaList = gbMiscApplicationService.getParkingAreaList(cargoType,
						fromDate, toDate);
				List<Map<String, Object>> calParkAreaList = new ArrayList<Map<String, Object>>();
				Map<String, Object> parkAreaValuesMap = new LinkedHashMap<String, Object>();
				for (int i = 0; i < miscParkAreaList.size(); i++) {
					MiscAppParkingAreaObject miscAppParkingAreaObject = (MiscAppParkingAreaObject) miscParkAreaList
							.get(i);
					String description = miscAppParkingAreaObject.getAreaCode() + "("
							+ miscAppParkingAreaObject.getNoOfSlot() + ")";
					parkAreaValuesMap.put(miscAppParkingAreaObject.getAreaCode(), description);
				}
				calParkAreaList.add(parkAreaValuesMap);
				map.put("MiscParkAreaList", calParkAreaList);

				// nextScreen(request, "MiscAppAddTpa");
			} else if ("CANCEL".equals(command)) {
				// forwardHandler(request, "MiscAppList");
				// return;
			} else if ("APPLICATION_ENQUIRE".equals(command)) {

				String fromDateEnq = criteria.getPredicates().get("fromDate");
				String fromTimeEnq = criteria.getPredicates().get("fromTime");
				String toDateEnq = criteria.getPredicates().get("toDate");
				String toTimeEnq = criteria.getPredicates().get("toTime");
				String cargoTypeEnq = criteria.getPredicates().get("cargoType");

				// BEGIN ThangPV edit for new TPA
				String trailerTypeEnq = criteria.getPredicates().get("trailerType");
				String trailerSizeEnqStr = criteria.getPredicates().get("trailerSize");
				int trailerSizeEnq = 0;
				if (trailerSizeEnqStr != null && !"".equals(trailerSizeEnqStr)) {
					trailerSizeEnq = Integer.parseInt(trailerSizeEnqStr);
				}
				// BEGIN ThangPV edit for new TPA

				EnquireQueryObject queryObj = null;

				if (fromDateEnq != null && fromDateEnq.trim().length() > 0) {
					queryObj = new EnquireQueryObject();
					queryObj.setStartDate(fromDateEnq);
					queryObj.setStartTime(fromTimeEnq);
					queryObj.setAreaCode("ALL");
					queryObj.setSlotType(cargoTypeEnq);

					// BEGIN ThangPV edit for new TPA
					queryObj.setTrailerSize(trailerSizeEnq);
					queryObj.setTrailerType(trailerTypeEnq);
					// BEGIN ThangPV edit for new TPA

					// To check range between start date and end date: More than 24 hours then end
					// date = start date + 24 hours
					Date fromDateTemp = miscAppCommonUtility.parseStrToDate(fromDateEnq + " " + fromTimeEnq,
							"ddMMyyyy HHmm");
					// To calculate hours
					Calendar fromCal = Calendar.getInstance();
					fromCal.setTime(fromDateTemp);

					if (toDateEnq == null || toDateEnq.trim().length() == 0) {
						fromCal.add(Calendar.HOUR, 24);
						String endDateStr = miscAppCommonUtility.parseDateToStr(fromCal.getTime(), "ddMMyyyy");
						String endTimeStr = miscAppCommonUtility.parseDateToStr(fromCal.getTime(), "HHmm");
						queryObj.setEndDate(endDateStr);
						queryObj.setEndTime(endTimeStr);
					} else {
						Date toDateTemp = miscAppCommonUtility.parseStrToDate(toDateEnq + " " + toTimeEnq,
								"ddMMyyyy HHmm");
						Calendar toCal = Calendar.getInstance();
						toCal.setTime(toDateTemp);
						double milliseconds1 = fromCal.getTimeInMillis();
						double milliseconds2 = toCal.getTimeInMillis();
						double hours = (milliseconds2 - milliseconds1) / (60 * 60 * 1000);
						if (hours > 24) {
							fromCal.add(Calendar.HOUR, 24);
							toCal.setTime(fromCal.getTime());
							String endDateStr = miscAppCommonUtility.parseDateToStr(toCal.getTime(), "ddMMyyyy");
							String endTimeStr = miscAppCommonUtility.parseDateToStr(toCal.getTime(), "HHmm");
							queryObj.setEndDate(endDateStr);
							queryObj.setEndTime(endTimeStr);

						} else {
							queryObj.setEndDate(toDateEnq);
							queryObj.setEndTime(toTimeEnq);
						}
					}
				}

				map.put("QueryObject", queryObj);
				// nextScreen(request, "MiscAppAddTpa");
			} else {
				// get list of parking reasons
				List<Map<String, Object>> miscParkReasonList = gbMiscApplicationService.getParkingReasonList();
				map.put("MiscParkReasonList", miscParkReasonList);
				int dateRange = gbMiscApplicationService.getTpaDatePeriod();
				map.put("dateRange", dateRange + "");

				// nextScreen(request, "MiscAppAddTpa");
				miscAppMap = this.miscAppAddTpa(criteria);
				map.put("miscAppMap", miscAppMap);
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAddTpa : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddTpa : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppAddTpa result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	private Map<String, Object> miscAppAddTpa(Criteria criteria) {
		Map<String, Object> map = new HashMap<>();
		try {
			log.info("START: miscAppAddTpa2 criteria: " + criteria.toString());
			String errorCode = CommonUtility.deNull(criteria.getPredicates().get("errorCode"));
			String autoApprove = CommonUtility.deNull(criteria.getPredicates().get("autoApprove"));
			String redirectName = null;
			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));
			if ("APPLICATION_ENQUIRE".equals(command)) {
				log.info("Process request for enquire");
//		            	redirectName  = "/JSP/gbms/miscApp/MiscAppEnquireSlot.jsp";
//		            	redirect(request, response, redirectName);
				return null;
			}
			List<List<String>> vslList = new ArrayList<List<String>>();
//		            String vesselList = CommonUtility.deNull(criteria.getPredicates().get("vesselList"));
//		        	String vslName = null;
//		        	String varCode = null;
//		        	String inVoyNbr = null;
//		        	String outVoyNbr = null;
//		        	String atbDttm = null;
//		        	String atuDttm = null;
//		        	if(vesselList != null){
//		        		for(int i=0; i < vesselLists.size(); i++){
//		                	vslName= CommonUtility.deNull(miscAppValueObject.getVslName());
//		                	varCode= CommonUtility.deNull(miscAppValueObject.getVarCode());
//		                	inVoyNbr = CommonUtility.deNull(miscAppValueObject.getInVoyNbr());
//		                	outVoyNbr = CommonUtility.deNull(miscAppValueObject.getOutVoyNbr());
//		                	atbDttm = CommonUtility.deNull(miscAppValueObject.getAtbDttm());
//		                	atuDttm = CommonUtility.deNull(miscAppValueObject.getAtuDttm());
//		                }
//		        		
//		        	}
//		        	
//		        	map.put("vslName", vslName);
//		        	map.put("varCode", varCode);
//		        	map.put("inVoyNbr", inVoyNbr);
//		        	map.put("outVoyNbr", outVoyNbr);
//		        	map.put("atbDttm", atbDttm);
//		        	map.put("atuDttm", atuDttm);
			String[] cargoTypeNormal = { CARGO_TYPE_NORMAL, cargoTypeValues[0] };
			String[] cargoTypeDG = { CARGO_TYPE_DG, cargoTypeValues[1] };
			String[] cargoTypeOOG = { CARGO_TYPE_OOG, cargoTypeValues[2] };

			ArrayList<String[]> cargoTypesList = new ArrayList<String[]>();
			cargoTypesList.add(cargoTypeNormal);
			cargoTypesList.add(cargoTypeDG);
			cargoTypesList.add(cargoTypeOOG);

			map.put("hourList", Constants.HOUR_LIST);
			map.put("cargoTypesList", cargoTypesList);
			String dateRange = CommonUtility.deNull(criteria.getPredicates().get("dateRange"));
			if (dateRange != null && !"".equals(dateRange) && Integer.parseInt(dateRange) > 0) {
				map.put("dateRange", dateRange);
			}
			if ("ADD_SUBMIT".equals(command)) {
				if ("true".equalsIgnoreCase(autoApprove)) {
					String list = CommonUtility.deNull(criteria.getPredicates().get("details"));
					List<String> myList = new ArrayList<String>(Arrays.asList(list.split(",")));
					for (int i = 0; i < myList.size(); i++) {
						vslList.add(myList);
					}
					MiscAppValueObject appObj = null;
					MiscVehValueObject vehObj = null;

					if (list != null && myList.size() > 0) {
						appObj = (MiscAppValueObject) vslList.get(0);
						vehObj = (MiscVehValueObject) vslList.get(2);
					}

					// Application Details
					String appRefNbr = appObj.getAppRefNbr();

					// Veh Details
					String[] vehChasNbr = vehObj.getVehChasNbr();
					String[] cntrNbr = vehObj.getCntrNbr();
					String[] asnNbr = vehObj.getAsnNbr();
					String[] prefArea = vehObj.getPreferredArea();
					String[] remarks = vehObj.getRemarks();
					String[] area = vehObj.getArea();
					String[] slot = vehObj.getSlot();

					// Application Details
					map.put("appRefNbr", appRefNbr);

					// Veh Details
					map.put("vehChasNbr", vehChasNbr);
					map.put("cntrNbr", cntrNbr);
					map.put("asnNbr", asnNbr);

					map.put("prefArea", prefArea);
					map.put("remarks", remarks);
					map.put("area", area);
					map.put("slot", slot);
					redirectName = "/JSP/gbms/miscApp/MiscAppAutoApproveTpa.jsp";
				} else if (errorCode != null && !"0".equalsIgnoreCase(errorCode)) {
					String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
					String conPerson = CommonUtility.deNull(criteria.getPredicates().get("conPerson"));
					String conTel = CommonUtility.deNull(criteria.getPredicates().get("conTel"));
					String fromDate = CommonUtility.deNull(criteria.getPredicates().get("fromDate"));
					String toDate = CommonUtility.deNull(criteria.getPredicates().get("toDate"));
					String fromTime = CommonUtility.deNull(criteria.getPredicates().get("fromTime"));
					String toTime = CommonUtility.deNull(criteria.getPredicates().get("toTime"));
					String noHours = CommonUtility.deNull(criteria.getPredicates().get("noHours"));
					String appRemarks = CommonUtility.deNull(criteria.getPredicates().get("appRemarks"));
					String reasonForApp = CommonUtility.deNull(criteria.getPredicates().get("reasonForApp"));
					String cargoType = CommonUtility.deNull(criteria.getPredicates().get("cargoType"));
					String vehNo = CommonUtility.deNull(criteria.getPredicates().get("vehNo"));
					String cntNo = CommonUtility.deNull(criteria.getPredicates().get("cntNo"));
					String asnNo = CommonUtility.deNull(criteria.getPredicates().get("asnNo"));
					String remark = CommonUtility.deNull(criteria.getPredicates().get("remarks"));
					String preferredArea1 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea1"));
					String preferredArea2 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea2"));
					String preferredArea3 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea3"));
					String preferredArea4 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea4"));
					String preferredArea5 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea5"));

					map.put("coCd", coCd);
					map.put("conPerson", conPerson);
					map.put("conTel", conTel);

					// Veh Details
					map.put("fromDate", fromDate);
					map.put("toDate", toDate);
					map.put("fromTime", fromTime);
					map.put("toTime", toTime);

					map.put("noHours", noHours);
					map.put("appRemarks", appRemarks);
					map.put("reasonForApp", reasonForApp);

					map.put("cargoType", cargoType);

					String vehChasNbr = vehNo;
					String cntrNbr = cntNo;
					String asnNbr = asnNo;
					String[] preferredArea = new String[5];
					String remarks = remark;

					preferredArea[0] = preferredArea1;
					preferredArea[1] = preferredArea2;
					preferredArea[2] = preferredArea3;
					preferredArea[3] = preferredArea4;
					preferredArea[4] = preferredArea5;

					map.put("vehChasNbr", vehChasNbr);
					map.put("cntrNbr", cntrNbr);
					map.put("asnNbr", asnNbr);
					map.put("prefArea", preferredArea);
					map.put("remarks", remarks);

					redirectName = "/JSP/gbms/miscApp/MiscAppAddTpa.jsp";
				} else {
					redirectName = "/JSP/gbms/miscApp/MiscAppList.jsp";
				}

			} else if ("POPUP".equals(command)) {
				redirectName = "/JSP/gbms/miscApp/MiscAppAddTpaPopup.jsp";
			} else {
				redirectName = "/JSP/gbms/miscApp/MiscAppAddTpa.jsp";
			}
			log.info("TraceServlet MiscAppAddTpaServlet...");
			log.info("Redirect URL: " + redirectName);
//		            redirect(request, response, redirectName);
		} catch (Exception e) {
			log.info("Exception miscAppAddTpa2 : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		}
		log.info("END miscAppAddTpa2 :: map: " + map);
		return map;
	}

	// delegate.helper.gbms.miscApp -->MiscAppVarHandler -->miscAppVar
	@RequestMapping(value = "/miscAppVar", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppVar(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppVar criteria:" + criteria.toString());

			String searchByText, vesselName = null;
			searchByText = criteria.getPredicates().get("searchByText");
			if (searchByText != null && searchByText.equals("true"))
				vesselName = criteria.getPredicates().get("vessel");

			String selVessel = criteria.getPredicates().get("vesselName");

			if (searchByText != null && searchByText.equals("true")) {
				List<String> miscVesselList = gbMiscApplicationService.getVesselList(vesselName);
				List<MiscAppValueObject> miscVoyList = gbMiscApplicationService.getVoyageList(selVessel);
				map.put("MiscVesselList", miscVesselList);
				map.put("MiscVoyList", miscVoyList);
				map.put("SearchString", vesselName);
				map.put("SelVessel", selVessel);
			}
		} catch (BusinessException e) {
			log.info("Exception miscAppVar : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppVar : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppVar result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}
	// EndRegion MiscAppCustHandler

	// start misc app top
	// method: perform()

	@ApiOperation(value = "miscAppTopLst", response = String.class)
	@RequestMapping(value = "/miscAppTopLst", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppTopLst(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppTopLst criteria:" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			List<MiscAppValueObject> miscAppTypeList = gbMiscApplicationService.getApplicationTypeList();
			List<MiscAppValueObject> miscAppStatusList = gbMiscApplicationService.getApplicationStatusList();
			// Added by ZanFeng on 26/01/2011
			List<CompanyValueObject> companyList = gbMiscApplicationService.getCompanyList1();
			String companyName = gbMiscApplicationService.getCompanyName(coCd);

			map.put("MiscAppTypeList", miscAppTypeList);
			map.put("MiscAppStatusList", miscAppStatusList);
			// Added by ZanFeng on 26/01/2011
			map.put("CompanyList", companyList);
			// end
			map.put("coCd", coCd);
			map.put("userAccount", UserID);
			map.put("companyName", companyName);

		} catch (BusinessException e) {
			log.info("Exception miscAppTopLst : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppTopLst : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppTopLst result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}
	// end misc app top

	// start misc app void
	// delegate.helper.gbms.miscApp -->MiscAppVoidHandler -->perform
	@RequestMapping(value = "/miscAppVoid", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppVoid(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppVoid criteria:" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			if ("VOID".equals(command)) {

				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				try {
					gbMiscApplicationService.voidApplication(userId, coCd, appSeqNbr);
				} catch (BusinessException e) {
					errorMessage = "Void is not allowed after closing time.";
					/*
					 * if (e.getMessage().equalsIgnoreCase("M22223")) { errorMessage(request,
					 * "M22223"); }
					 */
				}
				// Start added for SMART CR by FPT on 25-Feb-2014
				String appRefNbr = criteria.getPredicates().get("checkedAppRefNbr");
				log.info("**********Before SMART interface calling to Inactive for valid plan of application");
				
				try {
					log.info("Void Use of Space: application reference number=" + appRefNbr);

					// Inactive for valid plan from SMART system.
					 smartServiceRestClient.checkValidUseOfSpacePlan(userId, appRefNbr,true);
				} catch (Exception ex) {
					log.info(
							"Call SMART Interface to Inactive for valid plan is not successfully: Application reference number ="
									+ appRefNbr);
					log.info("Exception amendHSCode : ", ex);
				}
				log.info("**********After SMART interface calling");

				// Send email for JP staff: emailMessage, emailSubject get from email template.	
				String sender = SpaceAppVoid_from;
				String emailSubject = SpaceAppVoid_subject;
				String fileNameSpaceVoid = SpaceAppVoid_body_template;
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), fileNameSpaceVoid);
				Map<String, String> emailInputData = new HashMap<String, String>();
				
				emailSubject = StringUtils.replace(emailSubject, "<ApplicationNumber>", appRefNbr);
				emailInputData.put("ApplicationNumber", appRefNbr);
				emailInputData.put("Updater", userId);
				emailInputData.put("TimeofVoiding", getCurrentDate());
				
				String emailMessage = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);

				log.info("Space App Void, send email: email subject=" + emailSubject);
				log.info("Space App Void, send email: email message=" + emailMessage);
				gbMiscApplicationService.sendFlexiAlert("MSV", "", emailMessage, emailSubject, sender, null);
				// End added for SMART CR by FPT on 25-Feb-2014			
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppVoid : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppVoid : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppVoid result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// SUMMARRY PARKING SLOT CTRL
	private boolean isFirstTime = false;

	// jp.src.delegate.helper.gbms.miscApp.enquire-->SummaryParkingSlotHanlder-->perform()
	@PostMapping(value = "/summaryParkingSlot")
	public ResponseEntity<?> summaryParkingSlot(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: summaryParkingSlot criteria:" + criteria.toString());

			String command = criteria.getPredicates().get("command");
			log.info("-----> Inside SummaryParkingSlotHanlder-- ");
			if ("DOWNLOAD".equals(command)) {
				RecordPaging recPg = new RecordPaging();
				int numOfPage = recPg.getNumOfPages("Rows");

				List<Object> totalRows = new ArrayList<Object>();
				List<?> recs = null;
				for (int i = 1; i <= numOfPage; i++) {
					recs = recPg.getRecordsPage("Rows", i);
					totalRows.addAll(recs);
				}

				int[] avaiableCol = ((EnquireSummarySlotValueObject) totalRows.get(0)).getFlagColunm();
				map.put("avaiableCol", avaiableCol);
				map.put("Rows", totalRows);
				map.put("Type", "SUMMARY_EXPORT");
				map.put("total", totalRows.size());

				// Start modified by thanhbtl6b on 20/09/13 for TPA enhancement
				String[] avaiableColLabel = miscAppCommonUtility.calculateAvailableColLabel(
						criteria.getPredicates().get("startDate"), criteria.getPredicates().get("endDate"));
				map.put("avaiableColLabel", avaiableColLabel);
				// End modified by thanhbtl6b on 20/09/13 for TPA enhancement

				String contentType = criteria.getPredicates().get("contentType");
				map.put("contentType", contentType);
				map.put("screen", "EnquireExportExcel");

			} else if ("QUERY_SUMMARY".equals(command)) {
				doPaging(request, map);
				if (isFirstTime) {
					map.put("screen", "EnquireSummarySlot");
				} else {
					Map<String, Object> map1 = new HashMap<String, Object>();
					map1.put("Rows", criteria.getPredicates().get("Rows"));

					List<?> rows = (List<?>) map1.get("Rows");
					int[] avaiableCol = ((EnquireSummarySlotValueObject) rows.get(0)).getFlagColunm();
					map.put("avaiableCol", avaiableCol);
					map.put("screen", "EnquireSummarySlot");
					map.put("total", rows.size());
				}

			}

		} catch (Exception e) {
			log.info("Exception summaryParkingSlot : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: summaryParkingSlot :: result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// jp.src.delegate.helper.gbms.miscApp.enquire-->SummaryParkingSlotHanlder-->doPaging()
	private void doPaging(HttpServletRequest request, Map<String, Object> map) {
		Criteria criteria = CommonUtil.getCriteria(request);
		try {
			log.info("START: doPaging :: criteria: " + criteria + " , map: " + map);
			String[] blockTime = miscAppCommonUtility.getBlockTime();
			String[] timeList = miscAppCommonUtility.createTimeList(blockTime);
			int numberOfTimeSlot = timeList.length;
			List<EnquireSummarySlotValueObject> returnList = null;
			List<EnquireSummarySlotValueObject> getPageList = new ArrayList<EnquireSummarySlotValueObject>();
			EnquireSummarySlotValueObject row = null;
			List<?> recs;
			int noRowsPerPage = 10;
			int numOfPage = 0;
			int curPage = 1;
			boolean getInd = true;
			int trailerSize = 0;
			RecordPaging recPg = new RecordPaging();
			log.info("-----> if First timebegining-- ");
			isFirstTime = (criteria.getPredicates().get("PageIndex") == null)
					|| ((criteria.getPredicates().get("PageIndex")).equals("null"))
					|| "firstTime".equals(criteria.getPredicates().get("firstTime"));
			log.info("-----> if First-- " + isFirstTime);
			if (isFirstTime) {
				log.info("-----> reload list of slot from DB -- ");
				String startTime = criteria.getPredicates().get("startTime");
				String startDate = criteria.getPredicates().get("startDate");
				String endDate = criteria.getPredicates().get("endDate");
				String endTime = criteria.getPredicates().get("endTime");
				String areaCode = criteria.getPredicates().get("areaCode");
				String slotTypeCode = criteria.getPredicates().get("slotType");

				// start modified by thanhbtl6b on 20/09/13 for TPA enhancement
				if (criteria.getPredicates().get("trailerSize") != null) {
					trailerSize = Integer.parseInt(criteria.getPredicates().get("trailerSize"));
				}
				String trailerTypeCode = criteria.getPredicates().get("trailerType");
				// end modified by thanhbtl6b on 20/09/13 for TPA enhancement
				EnquireQueryObject queryObj = new EnquireQueryObject();
				queryObj.setAreaCode(StringUtils.trimToNull(areaCode));
				queryObj.setSlotType(StringUtils.trimToNull(slotTypeCode));
				queryObj.setStartDate(StringUtils.trimToNull(startDate));
				queryObj.setStartTime(StringUtils.trimToNull(startTime));
				queryObj.setEndDate(StringUtils.trimToNull(endDate));
				queryObj.setEndTime(StringUtils.trimToNull(endTime));

				// Start modified by thanhbtl6b on 20/09/13 for TPA enhancement
				queryObj.setTrailerSize((trailerSize));
				queryObj.setTrailerType(StringUtils.trimToNull(trailerTypeCode));
				// End modified by thanhbtl6b on 20 Sept 2013 for TPA enhancement

				returnList = gbMiscApplicationService.enquireSummaryParkingSlot(queryObj);
				map.put("avaiableCol", queryObj.getIntializeCountSlotNumber(numberOfTimeSlot));
				// Start modified by thanhbtl6b on 20/09/13 for TPA enhancement
				String[] avaiableColLabel = miscAppCommonUtility.calculateAvailableColLabel(
						criteria.getPredicates().get("startDate"), criteria.getPredicates().get("endDate"));

				map.put("avaiableColLabel", avaiableColLabel);
				// End modified by thanhbtl6b on 20/09/13 for TPA enhancement
				log.info("-----> returnList: -- " + returnList.size());

				if (returnList != null && returnList.size() > 0) {
					numOfPage = recPg.createRecordPagingCache("Rows", returnList, noRowsPerPage);
					if (!((criteria.getPredicates().get("PageIndex") == null)
							|| ((criteria.getPredicates().get("PageIndex")).equals("null")))) {
						curPage = Integer.valueOf(criteria.getPredicates().get("PageIndex")).intValue();
						if (curPage > numOfPage)
							curPage = numOfPage;
					}
				} else {
					getInd = false;
				}

				map.put("startTime", startTime);
				map.put("startDate", startDate);
				map.put("endDate", endDate);
				map.put("endTime", endTime);
				map.put("trailerSize", trailerSize);
				map.put("trailerType", trailerTypeCode);

			} else {
				log.info("-----> User list of slot from Session -- ");
				String startDate = criteria.getPredicates().get("startDate");
				String endDate = criteria.getPredicates().get("endDate");
				if ((startDate == null || "".equals(startDate)) && criteria.getPredicates().get("startDate") != null) {
					startDate = criteria.getPredicates().get("startDate");
				}
				if ((endDate == null || "".equals(endDate)) && criteria.getPredicates().get("endDate") != null) {
					endDate = criteria.getPredicates().get("endDate");
				}
				numOfPage = recPg.getNumOfPages("Rows");
				curPage = Integer.valueOf( criteria.getPredicates().get("PageIndex")).intValue();
				String[] avaiableColLabel = miscAppCommonUtility.calculateAvailableColLabel(startDate, endDate);
				map.put("avaiableColLabel", avaiableColLabel);
				map.put("startDate", startDate);
				map.put("endDate", endDate);

			}

			if (getInd) {
				// To get the Data for the first page
				recs = recPg.getRecordsPage("Rows", curPage);

				int size = recs.size();
				log.info("recs.size() = " + recs.size());
				int i = 0;
				for (i = 0; i < size; i++) {
					row = (EnquireSummarySlotValueObject) recs.get(i);
					getPageList.add(row);
				}
				map.put("totalPages", numOfPage + "");
				map.put("pageIndex", curPage + "");
			}
			// for paging
			map.put("Rows", getPageList);
			map.put("total", getPageList.size());
			log.info("END: doPaging :: map: " + map);
		} catch (Exception e) {
			log.info("Exception doPaging : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		}
	}
	// END

	// Start added for SMART CR by FPT on 25-Feb-2014
	/**
	 * Get the current date in dd/MM/yyyy HH:mm format for Email purpose.
	 */
	public static String getCurrentDate() {
		log.info("START: getCurrentDate");
		String currentDate = "";
		DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			currentDate = df1.format(new Date());
		} catch (Exception e) {
			log.info("Exception getCurrentDate : ", e);
		}
		log.info("END: getCurrentDate :: currentDate: " + currentDate);
		return currentDate;
	}

	private String validate(HttpServletRequest request, GBMiscApplicationService gbMiscApplicationService,
			String[] vehNo, String[] preferredArea) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();

		String errorCode = "0";
		// Submit - need to validate availability of parking slot
		String fromDate = criteria.getPredicates().get("fromDate") + " " + criteria.getPredicates().get("fromTime");
		String toDate = criteria.getPredicates().get("toDate") + " " + criteria.getPredicates().get("toTime");
		String cargoType = criteria.getPredicates().get("cargoType");
		
		log.info("START: validate :: criteria: " + criteria + " , vehNo: " + vehNo.toString() + " , preferredArea: " + preferredArea);
		log.info("fromDate:" + criteria.getPredicates().get("fromDate"));
		log.info("toDate : " + criteria.getPredicates().get("toDate"));
		log.info("TPA - cargoType :" + cargoType);

		// get list of parking area
		try {
			List<MiscAppParkingAreaObject> miscParkAreaList = gbMiscApplicationService
					.getAvailableParkingSlots(cargoType, fromDate, toDate);
			log.info("miscParkAreaList :" + miscParkAreaList.size());
			if (miscParkAreaList == null || miscParkAreaList.size() == 0) {
				errorCode = "1";
			} else if (miscParkAreaList.size() > 0) {
				int vehSize = 0;
				for (int i = 0; i < vehNo.length; i++) {
					if ((vehNo[i] != null && !vehNo[i].equals(""))) {
						vehSize++;
					}
				}
				if (vehSize > miscParkAreaList.size()) {
					errorCode = "2";
				} else {
					// Validate for each parking area
					for (int i = 0; i < vehNo.length; i++) {
						if ((vehNo[i] != null && !vehNo[i].equals(""))) {
							log.info("Vehicle:" + vehNo[i]);
							log.info("Preferred area:" + preferredArea[i]);

							if (preferredArea[i] != null && preferredArea[i] != "") {
								log.info("miscParkAreaList.size():" + miscParkAreaList.size());
								// for (int j =0; j < miscParkAreaList.size(); j++) {
								// MiscAppParkingAreaObject miscAppParkingAreaObject =
								// (MiscAppParkingAreaObject)miscParkAreaList.get(j);
								// log.info("miscAppParkingAreaObject.getAreaCode():" +
								// miscAppParkingAreaObject.getAreaCode());
								// }
								for (int j = 0; j < miscParkAreaList.size(); j++) {
									MiscAppParkingAreaObject miscAppParkingAreaObject = (MiscAppParkingAreaObject) miscParkAreaList
											.get(j);
									log.info("miscAppParkingAreaObject.getAreaCode():"
											+ miscAppParkingAreaObject.getAreaCode());
									log.info("miscAppParkingAreaObject.getSlotNumber():"
											+ miscAppParkingAreaObject.getSlotNumber());
									if (preferredArea[i].equalsIgnoreCase(miscAppParkingAreaObject.getAreaCode())) {
										log.info("Area exists.");
										miscParkAreaList.remove(j);
										errorCode = "0";
										break;
									} else {
										errorCode = "2";
									}
								}

								if ("2".equals(errorCode)) {
									log.info("This area is not sufficient");

									break;
								}
							}
						}

					}
				}
			}
		} catch (BusinessException e) {
			log.info("Exception validate : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception validate : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: validate :: errorCode: " + errorCode.toString());
		}

		return errorCode;
	}
	
	// jp.src.delegate.helper.gbms.miscApp.enquire-->EnquireParkingSlotHandler-->perform()
	@PostMapping(value = "/enquireParkingSlot")
	public ResponseEntity<?> enquireParkingSlot(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: enquireParkingSlot criteria:" + criteria.toString());
			String command = criteria.getPredicates().get("command");
			EnquireQueryObject queryObject = null;
			int numberOfDays = miscAppCommonUtility.getNumberOfDays();
			if ("SHOW_FROM_APPLY_APPLICATION".equals(command)) {
				queryObject = new EnquireQueryObject();
				queryObject.setStartDate(criteria.getPredicates().get("startDate"));
				queryObject.setStartTime(criteria.getPredicates().get("startTime"));
				queryObject.setEndDate(criteria.getPredicates().get("endDate"));
				queryObject.setEndTime(criteria.getPredicates().get("endTime"));
				queryObject.setAreaCode(criteria.getPredicates().get("areaCode"));
				queryObject.setSlotType(criteria.getPredicates().get("slotType"));

				// start modified by thanhbtl6b on 20/09/13 for TPA enhancement
				if (criteria.getPredicates().get("trailerSize") != null) {
					queryObject.setTrailerSize(Integer.parseInt(criteria.getPredicates().get("trailerSize")));
				}

				String trailerType = criteria.getPredicates().get("trailerType");
				queryObject.setTrailerType(trailerType);
				// end modified by thanhbtl6b on 20/09/13 for TPA enhancement

			} else {
				queryObject = new EnquireQueryObject();
				queryObject.setStartDate(MiscAppCommonUtility.getCurrentDateString());
				queryObject.setEndDate(MiscAppCommonUtility.getAfterCurrentDateString(1));
				queryObject.setStartTime(ConstantUtil.STRING_ZERO_TIME);
				queryObject.setEndTime(ConstantUtil.STRING_ZERO_TIME);
				queryObject.setAreaCode(ConstantUtil.DEFAULT_AREA_CODE);
				queryObject.setSlotType(ConstantUtil.DEFAULT_SLOT_TYPE);

				// start modified by thanhbtl6b on 20/09/13 for TPA enhancement
				queryObject.setTrailerSize(ConstantUtil.DEFAULT_TRAILER_SIZE);
				queryObject.setTrailerType(ConstantUtil.DEFAULT_TRAILER_TYPE_CODE);
				// end modified by thanhbtl6b on 20/09/13 for TPA enhancement
			}

			map.put("QueryObject", queryObject);
			map.put("numberOfDays", numberOfDays);
			log.info("Inside query object Handler:" + queryObject.getStartDate());

			List<String> areaList = gbMiscApplicationService.getParkingAreaList();

			map.put("AreaList", areaList);
			map.put("screen", "EnquireParkingSlot");

		} catch (BusinessException e) {
			log.info("Exception enquireParkingSlot : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception enquireParkingSlot : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
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
				log.info("END: enquireParkingSlot result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
}
