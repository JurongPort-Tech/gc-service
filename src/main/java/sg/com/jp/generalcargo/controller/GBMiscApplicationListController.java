package sg.com.jp.generalcargo.controller;

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
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import sg.com.jp.generalcargo.dao.TextParaRepository;
import sg.com.jp.generalcargo.domain.AttachmentFileValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EnquireQueryObject;
import sg.com.jp.generalcargo.domain.MiscAppParkingAreaObject;
import sg.com.jp.generalcargo.domain.MiscAppTpaApproveValueObject;
import sg.com.jp.generalcargo.domain.MiscAppValueObject;
import sg.com.jp.generalcargo.domain.MiscBargeValueObject;
import sg.com.jp.generalcargo.domain.MiscContractValueObject;
import sg.com.jp.generalcargo.domain.MiscCustValueObject;
import sg.com.jp.generalcargo.domain.MiscHotworkValueObject;
import sg.com.jp.generalcargo.domain.MiscParkMacBodyVO;
import sg.com.jp.generalcargo.domain.MiscParkMacValueObject;
import sg.com.jp.generalcargo.domain.MiscReeferValueObject;
import sg.com.jp.generalcargo.domain.MiscSpaceValueObject;
import sg.com.jp.generalcargo.domain.MiscSpreaderValueObject;
import sg.com.jp.generalcargo.domain.MiscVehValueObject;
import sg.com.jp.generalcargo.domain.OvrNghtPrkgVehValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.SmartInterfaceOutputVO;
import sg.com.jp.generalcargo.domain.StorageOrderValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TextParaVO;
import sg.com.jp.generalcargo.restclient.SmartServiceRestClient;
import sg.com.jp.generalcargo.service.GBMiscApplicationListService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.Constants;
import sg.com.jp.generalcargo.util.MiscAppCommonUtility;
import sg.com.jp.generalcargo.util.MiscAppConstValueObject;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;
import sg.com.jp.generalcargo.util.SmartInterfaceConstants;

@CrossOrigin
@RestController
@RequestMapping(value = GBMiscApplicationListController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class GBMiscApplicationListController {
	public static final String ENDPOINT = "gc/gbMiscApp";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private static final Log log = LogFactory.getLog(GBMiscApplicationListController.class);
	String errorMessage = null;
	
	@Value("${MiscApp.file.upload.path}")
	String folderPath;
	
	@Value("${jp.common.notificationProperties.email.SpaceAppApproved_from}")
	String SpaceAppApproved_from;
	
	@Value("${jp.common.notificationProperties.email.SpaceAppApproved_subject}")
	String SpaceAppApproved_subject;
	
	@Value("${jp.common.notificationProperties.email.SpaceAppApproved_body}")
	String SpaceAppApproved_body_template;
	
	@Value("${jp.common.notificationProperties.email.SpaceAppUpdated_from}")
	String SpaceAppUpdated_from;
	
	@Value("${jp.common.notificationProperties.email.SpaceAppUpdated_subject}")
	String SpaceAppUpdated_subject;
	
	@Value("${jp.common.notificationProperties.email.SpaceAppUpdated_body}")
	String SpaceAppUpdated_body_template;
		
	@Value("${jp.common.notificationProperties.email.SpaceAppVoid_from}")
	String SpaceAppVoid_from;
	
	@Value("${jp.common.notificationProperties.email.SpaceAppVoid_subject}")
	String SpaceAppVoid_subject;

	@Value("${jp.common.notificationProperties.email.SpaceAppVoid_body}")
	String SpaceAppVoid_body_template;
	
	@Value("${callSmartInterface}")
	private boolean callSmartInterface;
	
	@Value("${smart.rest.client.url}")
	private String smartBaseUrl;

	public static final String CARGO_TYPE_NORMAL = "N";
	public static final String CARGO_TYPE_DG = "D";
	public static final String CARGO_TYPE_OOG = "O";

	private final String[] cargoTypeValues = { "Empty/Normal", "DG", "OOG" };
	@Autowired
	private TextParaRepository textParaReo;

	@Autowired
	MiscAppCommonUtility miscAppCommonUtility;

	@Autowired
	private GBMiscApplicationListService gbMiscApplicationService;
	
	@Autowired
	private SmartServiceRestClient smartServiceRestClient;

	// jp.src.delegate.helper.gbms.miscApp-->MiscAppListHandler

	// StartRegion MiscAppListHandler

	String UserID = null;
	String coCd = null;
	List<MiscAppValueObject> appList = new ArrayList<MiscAppValueObject>();
	TableResult tableResult = new TableResult();
	String applyType = null;
	String refNo = null;
	String applyStatus = null;
	String payMode = null;
	String appFromDttm = null;
	String appToDttm = null;
	String userName = null;

	String appRecord = null;
	String appSeqNbr = null;
	String appType = null;
	String appTypeNm = null;
	String appStatus = null;
	// add by ZanFeng on 26/01/2011
	String companyCD = null;
	String machineNo = null;
	String vehicleNo = null;
	String containerNo = null;
	// end


	@ApiOperation(value = "miscAppLst", response = String.class)
	@RequestMapping(value = "/miscAppLst", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppLst(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> mapError = new HashMap<>();
		int totalAppListSize = 0;
		errorMessage = null;
		try {
			log.info("START: miscAppLst criteria:" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));
			String func = criteria.getPredicates().get("func");

			// HttpSession session = (HttpSession) request.getSession(false);

			/*
			 * log.info("User ID"+UserID); log.info("Co CD"+ coCd);
			 * log.info("~~~~ MiscAppListHandler ~~~~  command --> " + command);
			 * log.info("~~~~ MiscAppListHandler ~~~~  func --> " + func);
			 */
			map.put("coCd", coCd);

			/*--------  Start - Added by FPT/LongDH1 - (23-Nov-2011) - For New JP Online Enhancement Phase II- Dashboard  -------*/
			map.put("dashBoard", criteria.getPredicates().get("dashBoard"));
			/*--------  End - Added by FPT/LongDH1 - (23-Nov-2011) - For New JP Online Enhancement Phase II- Dashboard  -------*/

			// Amended by Punitha on 02/05/2008
			// if("LIST".equals(command)){
			log.info("command:" + command);

			if ("LIST".equals(command) || "DOWNLOAD".equals(command)) {
				String contentType = CommonUtility.deNull(criteria.getPredicates().get("contentType"));
				// End
				applyType = CommonUtility.deNull(criteria.getPredicates().get("applyType"));
				refNo = CommonUtility.deNull(criteria.getPredicates().get("refNo"));
				applyStatus = CommonUtility.deNull(criteria.getPredicates().get("applyStatus"));
				payMode = CommonUtility.deNull(criteria.getPredicates().get("payMode"));
				appFromDttm = CommonUtility.deNull(criteria.getPredicates().get("appFromDttm"));
				appToDttm = CommonUtility.deNull(criteria.getPredicates().get("appToDttm"));
				/*
				 * log.info("~~~~ MiscAppListHandler ~~~~  LIST --> " + applyType + refNo +
				 * applyStatus +appFromDttm +appToDttm); log.info("LIST~~~~ applyType ~~~~  " +
				 * applyType); log.info("LIST~~~~ refNo ~~~~  " + refNo);
				 * log.info("LIST~~~~ applyStatus ~~~~  " + applyStatus);
				 * log.info("LIST~~~~ userName ~~~~  " + userName);
				 */
				// Added by ZanFeng on 26/01/2011
				companyCD = CommonUtility.deNull(criteria.getPredicates().get("companyCD"));
				machineNo = CommonUtility.deNull(criteria.getPredicates().get("machineNo"));
				vehicleNo = CommonUtility.deNull(criteria.getPredicates().get("vehicleNo"));
				containerNo = CommonUtility.deNull(criteria.getPredicates().get("containerNo"));
				// end
				userName = gbMiscApplicationService.getUserName(UserID);
				log.info("userName=>" + userName);
				map.put("applyType", applyType);
				map.put("refNo", refNo);
				map.put("applyStatus", applyStatus);
				map.put("payMode", payMode);
				map.put("appFromDttm", appFromDttm);
				map.put("appToDttm", appToDttm);
				map.put("UserID", userName);
				// Added by Punitha on 02/05/2008
				map.put("contentType", contentType);
				// End
				if (companyCD != null)
					map.put("companyCD", companyCD);
				// paging
				// doPaging(criteria);
				// ArrayList getPageList = new ArrayList();
				MiscAppValueObject miscAppValueObject = new MiscAppValueObject();

				Map<String, Object> map1 = new HashMap<String, Object>();
				Boolean allData = false;
				if ("DOWNLOAD".equals(command)) {
					allData = true;
				}
				tableResult = gbMiscApplicationService.getApplicationList(coCd, applyType, refNo, applyStatus,
						appFromDttm, appToDttm, payMode, companyCD, machineNo, vehicleNo, containerNo, criteria,
						allData);

				totalAppListSize = tableResult.getData().getListData().getTopsModel().size();

				Object listObject = null;
				for (int i = 0; i < totalAppListSize; i++) {
					listObject = tableResult.getData().getListData().getTopsModel().get(i);
					appList.add((MiscAppValueObject) listObject);
					miscAppValueObject = (MiscAppValueObject) listObject;
					map1.put(miscAppValueObject.getAppSeqNbr(), String.valueOf(gbMiscApplicationService
							.checkApprovePermission(UserID, miscAppValueObject.getAppTypeCd())));

				}

				// for paging
				map.put("getPageList", tableResult.getData().getListData());
				// map.put("AppList", appList);
				// add by ZanFeng on 26/01/2011
				map.put("approveMap", map1);
				map.put("screen", "MiscAppList");
				map.put("total", tableResult.getData().getTotal());

			} else if ("APPROVE_BILL_SUBMIT".equals(command) || "CLOSE_BILL_SUBMIT".equals(command)
					|| "ADD_SUBMIT".equals(command) || "UPDATE_SUBMIT".equals(command) || "VOID_SUBMIT".equals(func)
					|| "VIEW_OK".equals(command) || "CANCEL".equals(command)) {
				applyType = CommonUtility.deNull(criteria.getPredicates().get("applyType"));
				refNo = CommonUtility.deNull(criteria.getPredicates().get("refNo"));
				applyStatus = CommonUtility.deNull(criteria.getPredicates().get("applyStatus"));
				payMode = CommonUtility.deNull(criteria.getPredicates().get("payMode"));
				appFromDttm = CommonUtility.deNull(criteria.getPredicates().get("appFromDttm"));
				appToDttm = CommonUtility.deNull(criteria.getPredicates().get("appToDttm"));
				/*
				 * log.info("~~~~ MiscAppListHandler ~~~~  "); log.info("~~~~ applyType ~~~~  "
				 * + applyType); log.info("~~~~ refNo ~~~~  " + refNo);
				 * log.info("~~~~ applyStatus ~~~~  " + applyStatus);
				 * log.info("~~~~ payMode ~~~~  " + payMode); log.info("~~~~ appFromDttm ~~~~  "
				 * + appFromDttm); log.info("~~~~ appToDttm ~~~~  " + appToDttm);
				 */
				// Start added for SMART CR by FPT on 24-Feb-2014
				map.remove("bayNbr");
				map.remove("areaUsed");
				map.remove("opsStartDttm");
				map.remove("opsEndDttm");
				map.remove("inSmartInd");

				map.remove("bayNbrList");
				map.remove("areaUsedList");
				map.remove("opsStartDttmList");
				map.remove("opsEndDttmList");
				map.remove("inSmartIndList");
				// End added for SMART CR by FPT on 24-Feb-2014

				// paging
				// doPaging(criteria);

				MiscAppValueObject miscAppValueObject = new MiscAppValueObject();
				// add by ZanFeng on 26/01/2011
				Map<String, Object> map1 = new HashMap<String, Object>();
				// end

				String miscAppNew = criteria.getPredicates().get("miscAppNew");
				// criteria.getPredicates().get("PageIndex"));

				Boolean allData = false;
				if ("DOWNLOAD".equals(command)) {
					allData = true;
				}

				if ((criteria.getPredicates().get("PageIndex") == null)
						|| ((criteria.getPredicates().get("PageIndex")).equals("null"))
						|| miscAppNew.equals("miscAppNew")) {

					tableResult = gbMiscApplicationService.getApplicationList(coCd, applyType, refNo, applyStatus,
							appFromDttm, appToDttm, payMode, companyCD, machineNo, vehicleNo, containerNo, criteria,
							allData);

					totalAppListSize = tableResult.getData().getListData().getTopsModel().size();

					Object listObject = null;
					for (int i = 0; i < totalAppListSize; i++) {
						listObject = tableResult.getData().getListData().getTopsModel().get(i);
						appList.add((MiscAppValueObject) listObject);
						map1.put(miscAppValueObject.getAppSeqNbr(), String.valueOf(gbMiscApplicationService
								.checkApprovePermission(UserID, miscAppValueObject.getAppTypeCd())));

					}

				}

				// for paging
				map.put("getPageList", tableResult.getData().getListData());
				map.put("AppList", appList);
				// add by ZanFeng on 26/01/2011
				map.put("approveMap", map1);
				map.put("screen", "MiscAppList");
				map.put("total", tableResult.getData().getTotal());

			} else if ("UPDATE".equals(command)) {
				processUpdate(request, criteria, map);
			} else if ("VIEW".equals(command)) {
				processView(criteria, map);
			} else if ("VOID".equals(command)) {
				processVoid(criteria, map);
			} else if ("APPROVE".equals(command) || "APPROVE_SUBMIT".equals(command)) {
				processApprove(request, criteria, map);
			} else if ("APPROVE_BILL".equals(command)) {
				processApproveBill(criteria, map);
			} else if ("CLOSE_BILL".equals(command)) {
				processCloseBill(request, criteria, map);
			} else if ("SUPPORT".equals(command)) {
				processSupport(request, criteria, map);
			} else if ("SUPPORT_SUBMIT".equals(command)) {
				processSupport(request, criteria, map);
			} else if ("GENRPT".equals(command)) { // added by Dong Sheng on 4/1/2011 for CR-OPS-20110110-09.

				String contentType = CommonUtility.deNull(criteria.getPredicates().get("contentType"));
				applyType = CommonUtility.deNull(criteria.getPredicates().get("applyType"));
				userName = gbMiscApplicationService.getUserName(UserID);
				map.put("applyType", applyType);
				map.put("contentType", contentType);

				try {
					List<OvrNghtPrkgVehValueObject> appList = new ArrayList<OvrNghtPrkgVehValueObject>();
					appList = gbMiscApplicationService.getOvernightParkingVehList(applyType);
					map.put("AppList", appList);
					log.info("~~~~~ Ended MiscAppListHandler - command is GENRPT ~~~~~");
					map.put("screen", "MiscAppGenRpt");

				} catch (Exception e) {
					errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
				} // end of code added by Dong Sheng on 4/1/2011

			}
				
			} catch (BusinessException e) {
				log.info("Exception miscAppLst : ", e);
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
				if (errorMessage == null) {
					errorMessage = CommonUtility.getExceptionMessage(e);
				}
			} catch (Exception e) {
				log.info("Exception miscAppLst : ", e);
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
			}  finally {
				if (errorMessage != null) {
					mapError.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(mapError);
					result.setSuccess(false);
					result.setData(map);
				} else {
					result = new Result();
					result.setData(map);
					result.setSuccess(true);
				}
				log.info("END: miscAppLst result: " + result.toString());
			}
		return ResponseEntityUtil.success(result.toString());

	}

	// method: setSelectedRecord()
	private void setSelectedRecord(Criteria criteria) {
		// Criteria criteria = CommonUtil.getCriteria(request);
		log.info("START: setSelectedRecord criteria: " + criteria.toString());
		appRecord = criteria.getPredicates().get("appCheck");
		StringTokenizer st = new StringTokenizer(appRecord, "|");
		while (st.hasMoreTokens()) {
			appSeqNbr = st.nextToken();
			appType = st.nextToken();
			appTypeNm = st.nextToken();
			appStatus = st.nextToken();
		}
		refNo = CommonUtility.deNull(criteria.getPredicates().get("refNo"));
		
		log.info("END: setSelectedRecord");
	}

	// method: processUpdate()
	private void processUpdate(HttpServletRequest request, Criteria criteria, Map<String, Object> map) {
		// log.info("~~~~~ Started MiscAppListHandler -
		// processUpdate() ~~~~~");
		setSelectedRecord(criteria);
		boolean update = false;
		// Map<String, Object> map = new HashMap<String, Object>();
		String coCd = criteria.getPredicates().get("companyCode");
		log.info("START: processUpdate criteria:" + criteria.toString());
		try {
			if ("JP".equals(coCd)) {

				// Amended on 14/06/2007 by Ai Lin - To allow JP update after billing for
				// Stationing of Forklift
				if (appType.equals(
						MiscAppConstValueObject.MISC_APP_STATIONING_OF_FORKLIFT_CNTRLIFT_WHEELLOADER_SHORECRANE)) {
					if (!("V".equals(appStatus))) {
						update = true;
					}
				} else {
					// End amended on 14/06/2007 by Ai Lin - To allow JP update after billing for
					// Stationing of Forklift
					// Amended on 28/05/2007 by Ai Lin - To disallow JP update after close for
					// billing
					// if(!("B".equals(appStatus) || "V".equals(appStatus))){
					if (!("C".equals(appStatus) || "B".equals(appStatus) || "V".equals(appStatus))) {
						update = true;
					}
				} // Added on 14/06/2007 by Ai Lin - To allow JP update after billing for
					// Stationing of Forklift
			} else {
				if ("D".equals(appStatus)) {
					update = true;
				}
			}

			if (update) {
				map.put("applyType", appType);
				map.put("appSeqNbr", appSeqNbr);
				map.put("applyTypeNm", appTypeNm);

				// log.info("~~~~ MiscAppListHandler ~~~~ applyType --> " +
				// appType);
				// log.info("~~~~ MiscAppListHandler ~~~~ appSeqNbr --> " +
				// appSeqNbr);

				if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_OVERNIGHT_PARKING_OF_VEHICLE)) {
					map.put("request", "MiscAppUpdateVeh");
					miscAppUpdateVeh(request, criteria, map);
					return;
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_TRAILER_PARKING_APPLICATION)) {
					map.put("request", "MiscAppUpdateTpa");
					miscAppUpdateTpa(request, criteria, map);
					return;

				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_PARKING_OF_LINE_TOW_BARGE)) {
					map.put("request", "MiscAppUpdateBarge");
					miscAppUpdateBarge(request, criteria, map);
					return;
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_REEFER_CONTAINER_POWER_OUTLET)) {
					map.put("request", "MiscAppUpdateReefer");
					miscAppUpdateReefer(request, criteria, map);
					return;
				} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_USE_OF_SPACE)) {
					map.put("request", "MiscAppUpdateSpace");
					miscAppUpdateSpace(request, criteria, map);
					return;
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_OVERNIGHT_PARKING_OF_FORKLIFT_SHORE_CRANE)) {
					map.put("request", "MiscAppUpdateParkMac");
					miscAppUpdateParkMac(request, criteria, map);
					return;
				} else if (appType != null && appType.equals(
						MiscAppConstValueObject.MISC_APP_STATIONING_OF_FORKLIFT_CNTRLIFT_WHEELLOADER_SHORECRANE)) {
					map.put("request", "MiscAppUpdateStationMac");
					miscAppUpdateStationMac(request, criteria, map);
					return;
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_HIRE_OF_WOODEN_STEEL_SPREADER)) {
					map.put("request", "MiscAppUpdateSpreader");
					miscAppUpdateSpreader(request, criteria, map);
					return;
				} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_CONTRACTOR_PERMIT)) {
					map.put("request", "MiscAppUpdateContract");
					miscAppUpdateContract(request, criteria, map);
					return;
				} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_HOT_WORK_PERMIT)) {
					map.put("request", "MiscAppUpdateHotwork");
					miscAppUpdateHotwork(request, criteria, map);
					return;
				}
			} else {
				throw new BusinessException(ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20102"));
			}
		} catch (BusinessException e) {
			log.info("Exception processUpdate : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception ex) {
			log.info("Exception processUpdate : ", ex);
			if ("JP".equals(coCd)) {
				if ("V".equals(appStatus)) {
					errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20111");
				} else {
					errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20102");
				}
			} else
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20103");
		} finally {
			
			log.info("END: processUpdate ");
		}
	}

	// method: processView()
	private void processView(Criteria criteria, Map<String, Object> map) throws BusinessException {

		// log.info("~~~~~ Started MiscAppListHandler - processView()
		// ~~~~~");
		setSelectedRecord(criteria);
		
		log.info("START: processView criteria:" + criteria.toString() + ", map: " + map.toString());
		// log.info("~~~~ VIEW ~~~~ appRecord --> " + appRecord);

		map.put("applyType", appType);
		map.put("appSeqNbr", appSeqNbr);
		map.put("applyTypeNm", appTypeNm);

		// log.info("~~~~ MiscAppListHandler ~~~~ applyType --> " +
		// appType);
		// log.info("~~~~ MiscAppListHandler ~~~~ appSeqNbr --> " +
		// appSeqNbr);

		if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_OVERNIGHT_PARKING_OF_VEHICLE)) {
			map.put("request", "MiscAppViewVeh");
			miscAppViewVeh(criteria, map);
			return;
		} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_TRAILER_PARKING_APPLICATION)) {
			map.put("request", "MiscAppViewTpa");
			miscAppViewTpa(criteria, map);
			return;

		} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_PARKING_OF_LINE_TOW_BARGE)) {
			map.put("request", "MiscAppViewBarge");
			miscAppViewBarge(criteria, map);
			return;
		} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_REEFER_CONTAINER_POWER_OUTLET)) {
			map.put("request", "MiscAppViewReefer");
			miscAppViewReefer(criteria, map);
			return;
		} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_USE_OF_SPACE)) {
			map.put("request", "MiscAppViewSpace");
			miscAppViewSpace(criteria, map);
			return;
		} else if (appType != null
				&& appType.equals(MiscAppConstValueObject.MISC_APP_OVERNIGHT_PARKING_OF_FORKLIFT_SHORE_CRANE)) {
			map.put("request", "MiscAppViewParkMac");
			miscAppViewParkMac(criteria, map);
			return;
		} else if (appType != null && appType
				.equals(MiscAppConstValueObject.MISC_APP_STATIONING_OF_FORKLIFT_CNTRLIFT_WHEELLOADER_SHORECRANE)) {
			map.put("request", "MiscAppViewStationMac");
			miscAppViewStationMac(criteria, map);
			return;
		} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_HIRE_OF_WOODEN_STEEL_SPREADER)) {
			map.put("request", "MiscAppViewSpreader");
			miscAppViewSpreader(criteria, map);
			return;
		} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_CONTRACTOR_PERMIT)) {
			map.put("request", "MiscAppViewContract");
			miscAppViewContract(criteria, map);
			return;
		} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_HOT_WORK_PERMIT)) {
			map.put("request", "MiscAppViewHotwork");
			miscAppViewHotwork(criteria, map);
			return;
		}
		
		log.info("END: processView : ");
	}

	// method: processVoid()
	private void processVoid(Criteria criteria, Map<String, Object> map) {
		// Map<String, Object> map = new HashMap<String, Object>();
		log.info("START: processVoid criteria:" + criteria.toString() + " map: " + map.toString());
		setSelectedRecord(criteria);
		boolean doVoid = false;
		try {
			if ("JP".equals(coCd)) {
				if (!"B".equals(appStatus)) {
					doVoid = true;
				}
			} else {
				if ("D".equals(appStatus)) {
					doVoid = true;
				}
			}
			if (doVoid) {
				map.put("applyType", appType);
				map.put("appSeqNbr", appSeqNbr);
				map.put("applyTypeNm", appTypeNm);

				map.put("request", "MiscAppVoid");
				return;
			} else {
				throw new BusinessException(ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20100"));
			}
		} catch (BusinessException e) {
			log.info("Exception processVoid : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception ex) {
			log.info("Exception processVoid : ", ex);
			if ("JP".equals(coCd))
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20100");
			else
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20101");
		} finally {
			
			log.info("END: processVoid : ");
		}
	}

	// method: processApprove()
	private void processApprove(HttpServletRequest request, Criteria criteria, Map<String, Object> map) {
		// Criteria criteria = CommonUtil.getCriteria(request);
		// Map<String, Object> map = new HashMap<String, Object>();
		log.info("START: processApprove criteria:" + criteria.toString() + " map: " + map.toString());
		// log.info("~~~~ APPROVE ~~~~ ");
		setSelectedRecord(criteria);
		// log.info("~~~~ APPROVE ~~~~ appRecord --> " + appRecord);
		boolean approve = false;
		try {
			// log.info("~~~~ APPROVE ~~~~ appStatus --> " + appStatus);
			if ((MiscAppConstValueObject.MISC_APP_STATIONING_OF_FORKLIFT_CNTRLIFT_WHEELLOADER_SHORECRANE)
					.equals(appType)) {
				if ("U".equals(appStatus) || "A".equals(appStatus) || "R".equals(appStatus)) {
					approve = true;
				}
			} else {
				if ("S".equals(appStatus) || "A".equals(appStatus) || "R".equals(appStatus)) {
					approve = true;
				}
			}

			if (approve) {
				map.put("applyType", appType);
				map.put("appSeqNbr", appSeqNbr);
				map.put("applyTypeNm", appTypeNm);

				// log.info("~~~~ MiscAppListHandler ~~~~ appType --> " +
				// appType);
				// log.info("~~~~ MiscAppListHandler ~~~~ appSeqNbr --> " +
				// appSeqNbr);

				if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_OVERNIGHT_PARKING_OF_VEHICLE)) {
					map.put("request", "MiscAppApproveVeh");
					miscAppApproveVeh(criteria, map);
					return;
					// START 02-Mar-2011 - TPA - Thanhnv2 added for approve Trailer Parking
					// Application.
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_TRAILER_PARKING_APPLICATION)) {
					map.put("request", "MiscAppApproveTpa");
					miscAppApproveTpa(request, criteria, map);
					return;
					// END 02-Mar-2011 - TPA - Thanhnv2 added for approve Trailer Parking
					// Application.
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_PARKING_OF_LINE_TOW_BARGE)) {
					map.put("request", "MiscAppApproveBarge");
					miscAppApproveBarge(criteria, map);
					return;
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_REEFER_CONTAINER_POWER_OUTLET)) {
					map.put("request", "MiscAppApproveReefer");
					miscAppApproveReefer(criteria, map);
					return;
				} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_USE_OF_SPACE)) {
					map.put("request", "MiscAppApproveSpace");
					miscAppApproveSpace(request, criteria, map);
					return;
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_OVERNIGHT_PARKING_OF_FORKLIFT_SHORE_CRANE)) {
					map.put("request", "MiscAppApproveParkMac");
					miscAppApproveParkMac(criteria, map);
					return;
				} else if (appType != null && appType.equals(
						MiscAppConstValueObject.MISC_APP_STATIONING_OF_FORKLIFT_CNTRLIFT_WHEELLOADER_SHORECRANE)) {
					map.put("request", "MiscAppApproveStationMac");
					miscAppApproveStationMac(criteria, map);
					return;
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_HIRE_OF_WOODEN_STEEL_SPREADER)) {
					map.put("request", "MiscAppApproveSpreader");
					miscAppApproveSpreader(criteria, map);
					return;
				} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_CONTRACTOR_PERMIT)) {
					map.put("request", "MiscAppApproveContract");
					miscAppApproveContract(criteria, map);
					return;
				} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_HOT_WORK_PERMIT)) {
					map.put("request", "MiscAppApproveHotwork");
					miscAppApproveHotwork(criteria, map);
					return;
				}
			} else {
				throw new BusinessException(ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20104"));
			}
		} catch (BusinessException e) {
			log.info("Exception processApprove : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception ex) {
			log.info("Exception processApprove : ", ex);
			if ((MiscAppConstValueObject.MISC_APP_STATIONING_OF_FORKLIFT_CNTRLIFT_WHEELLOADER_SHORECRANE)
					.equals(appType))
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20105");
			else
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20104");
		} finally {
			
			log.info("END: processApprove : ");
		}
		
	}

	// method: processApproveBill()
	private void processApproveBill(Criteria criteria, Map<String, Object> map) {
		// Map<String, Object> map = new HashMap<String, Object>();
		// Criteria criteria = CommonUtil.getCriteria(request);
		log.info("START: processApproveBill criteria:" + criteria.toString() + " map: " + map.toString());
		setSelectedRecord(criteria);
		String errorCode = null;
		boolean approveBill = false;
		try {
			// log.info("~~~~ APPROVE_BILL ~~~~ appStatus --> " +
			// appStatus);

			if (!(MiscAppConstValueObject.MISC_APP_HOT_WORK_PERMIT).equals(appType)) {
				errorCode = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20110");
				throw new BusinessException(errorCode);
			} else {
				if ("P".equals(appStatus)) {
					approveBill = true;
				}
				if (approveBill) {
					map.put("applyType", appType);
					map.put("appSeqNbr", appSeqNbr);
					map.put("applyTypeNm", appTypeNm);

					map.put("UserID", CommonUtility.deNull(criteria.getPredicates().get("userAccount")));
					map.put("request", "MiscAppAppvBillHotwork");
					return;
				} else {
					errorCode = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20106");
					throw new BusinessException(errorCode);
				}
			}
		 } catch (BusinessException e) {
			log.info("Exception processApproveBill : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception ex) {
			log.info("Exception processApproveBill : ", ex);
			if ("Application has not been closed for billing approval.".equals(errorCode))
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20106");
			if ("This function is not applicable for this Application.".equals(errorCode))
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20110");
		}finally {
			
			log.info("END: processApproveBill : ");
		}
	}

	// method: processCloseBill()
	public ResponseEntity<?> processCloseBill(HttpServletRequest request, Criteria criteria, Map<String, Object> map) {
		// Map<String, Object> map = new HashMap<String, Object>();
		// Criteria criteria = CommonUtil.getCriteria(request);
		// log.info("~~~~ CLOSE_BILL ~~~~ ");
		log.info("START: processCloseBill criteria:" + criteria.toString() + " map: " + map.toString() + ", request: " + request.toString());
		String errorCode = null;
		setSelectedRecord(criteria);
		Result result = new Result();
		map.put("UserID", CommonUtility.deNull(criteria.getPredicates().get("userAccount")));
		try {
			// log.info("~~~~ CLOSE_BILL ~~~~ appStatus --> " +
			// appStatus);

			if (!((MiscAppConstValueObject.MISC_APP_OVERNIGHT_PARKING_OF_FORKLIFT_SHORE_CRANE).equals(appType)
					|| (MiscAppConstValueObject.MISC_APP_STATIONING_OF_FORKLIFT_CNTRLIFT_WHEELLOADER_SHORECRANE)
							.equals(appType)
					|| (MiscAppConstValueObject.MISC_APP_CONTRACTOR_PERMIT).equals(appType))) {
				if ("A".equals(appStatus) || "C".equals(appStatus) || "P".equals(appStatus)) {
				}
				/*
				 * Commented by Punitha on 20/05/2008 if(closeBill){ //Added by Punitha on
				 * 05/05/2008. EJBHomeFactory homeFactory = EJBHomeFactory.getInstance();
				 * MiscAppHome miscAppHome = (MiscAppHome)homeFactory.lookUpHome("MiscApp");
				 * MiscApp miscAppEJB = miscAppHome.create();
				 * System.out.println("Ref NO :"+appSeqNbr); String approver =
				 * miscAppEJB.getApproverId(appSeqNbr); System.out.println("User ID :"+UserID);
				 * System.out.println("Approver :"+approver); if(UserID.equals(approver)){
				 * errorCode = "M22220"; throw new BusinessException(errorCode); } //Ended by
				 * Punitha
				 */
				map.put("applyType", appType);
				map.put("appSeqNbr", appSeqNbr);
				map.put("applyTypeNm", appTypeNm);
				// log.info("~~~~ MiscAppListHandler ~~~~ appType --> " +
				// appType);
				// log.info("~~~~ MiscAppListHandler ~~~~ appSeqNbr --> " +
				// appSeqNbr);
				// Added by Punitha on 05/05/2008. To validate the login user and the approver
				if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_OVERNIGHT_PARKING_OF_VEHICLE)) {
					map.put("request", "MiscAppCloseVeh");
					miscAppCloseVeh(criteria, map);
					// return;
					// START 02-Mar-2011 - TPA - Thanhnv2 added for close Trailer Parking
					// Application.
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_TRAILER_PARKING_APPLICATION)) {
					map.put("request", "MiscAppCloseTpa");
					miscAppCloseTpa(request, criteria, map);
					// return;
					// END 02-Mar-2011 - TPA - Thanhnv2 added for close Trailer Parking Application.
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_PARKING_OF_LINE_TOW_BARGE)) {
					map.put("request", "MiscAppCloseBarge");
					miscAppCloseBarge(criteria, map);
					// return;
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_REEFER_CONTAINER_POWER_OUTLET)) {
					map.put("request", "MiscAppCloseReefer");
					miscAppCloseReefer(request, criteria, map);
					// return;
				} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_USE_OF_SPACE)) {
					map.put("request", "MiscAppCloseSpace");
					miscAppCloseSpace(request, criteria, map);
					// return;
				} else if (appType != null
						&& appType.equals(MiscAppConstValueObject.MISC_APP_HIRE_OF_WOODEN_STEEL_SPREADER)) {
					map.put("request", "MiscAppCloseSpreader");
					miscAppCloseSpreader(criteria, map);
					// return;

				} else if (appType != null && appType.equals(MiscAppConstValueObject.MISC_APP_HOT_WORK_PERMIT)) {
					// Added by Punitha on 05/05/2008.
					if ("C".equals(appStatus)) {
						errorCode = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M22221");
						throw new BusinessException(errorCode);
					} else {
						// End
						map.put("request", "MiscAppCloseHotwork");
						miscAppCloseHotwork(request, criteria, map);
						// return;
					}
				}
				/*
				 * Commented by Punitha on 20/05/2008 } End by Punitha
				 */
				else {
					errorCode = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20107");
					throw new BusinessException(errorCode);
				}
			} else {
				errorCode = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20110");
				throw new BusinessException(errorCode);
			}
		} catch (BusinessException e) {
			log.info("Exception processCloseBill : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception ex) {
			log.info("Exception processCloseBill : ", ex);
			if ("Application is not accepted or has been billed.".equals(errorCode))
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20107");
			if ("This function is not applicable for this Application.".equals(errorCode))
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20110");
			if ("Close for Billing should not be done by the same person who approved this application."
					.equals(errorCode))
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M22220");
			if ("Close for Billing is not allowed when the application is pending billing.".equals(errorCode))
				errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M22221");
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
			log.info("END: processCloseBill result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// method: processSupport()
	public ResponseEntity<?> processSupport(HttpServletRequest request, Criteria criteria, Map<String, Object> map) {
		// Map<String, Object> map = new HashMap<String, Object>();
		// Criteria criteria = CommonUtil.getCriteria(request);
		String errorCode = null;
		Result result = new Result();
		try {
			log.info("START: processSupport **** criteria: " + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());
			String appRecord = criteria.getPredicates().get("appCheck");
			boolean support = false;

			StringTokenizer st = new StringTokenizer(appRecord, "|");
			while (st.hasMoreTokens()) {
				appSeqNbr = st.nextToken();
				appType = st.nextToken();
				appTypeNm = st.nextToken();
				appStatus = st.nextToken();
			}
			if ((MiscAppConstValueObject.MISC_APP_STATIONING_OF_FORKLIFT_CNTRLIFT_WHEELLOADER_SHORECRANE)
					.equals(appType)) {
				if ("S".equals(appStatus) || "U".equals(appStatus) || "N".equals(appStatus)) {
					support = true;
				}
				if (support) {
					map.put("applyType", appType);
					map.put("appSeqNbr", appSeqNbr);
					map.put("applyTypeNm", appTypeNm);
					miscAppSupportStationMacMiscList(criteria, map, request);

					result.setData(map);
					result.setSuccess(true);
					return ResponseEntityUtil.success(result.toString());
				} else {
					errorCode = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20108");
					throw new BusinessException(errorCode);
				}
			} else {
				errorCode = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M20110");
				throw new BusinessException(errorCode);
			}
		} catch (BusinessException e) {
			log.info("Exception processSupport : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception processSupport : ", e);
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
			log.info("END: processSupport result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// VIEW START

	// delegate.helper.gbms.miscApp -->MiscAppViewBargeHandler -->perform
	@RequestMapping(value = "/miscAppViewBarge", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppViewBarge(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppViewBarge criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String command = criteria.getPredicates().get("command");

			if ("VIEW".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getParkingOfLineTowBargeDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha

				map.put("details", list);
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppViewBarge : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppViewBarge : ", e);
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
			log.info("END: miscAppViewBarge result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppViewContractHandler -->perform
	@RequestMapping(value = "/miscAppViewContract", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppViewContract(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppViewContract criteria:" + criteria.toString() + ", map: " + map.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			if ("VIEW".equals(command)) {

				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getContractorPermitDetails(userId, applyType, appSeqNbr,
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
				map.put("coCd", coCd);
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppViewContract : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppViewContract : ", e);
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
			log.info("END: miscAppViewContract result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppViewSpaceHandler -->perform
	@RequestMapping(value = "/miscAppViewSpace", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppViewSpace(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppViewSpace criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String command = criteria.getPredicates().get("command");
			if ("VIEW".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getUseOfSpaceDetails(userId, applyType, appSeqNbr,
						applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);

					// Start added for SMART CR by FPT on 24-Feb-2014
					String appRefNbr = appObj.getAppRefNbr();
					log.info("***********Before SMART interface calling for fetch Operation Details (location info): Application reference number: " + appRefNbr);
					
					List<SmartInterfaceOutputVO> operationDetailsLst = null;
					try {
						MiscSpaceValueObject space = (MiscSpaceValueObject) list.get(2);
						if (callSmartInterface) {
							String url = smartBaseUrl + "/getJpAppStgLocByRefNbr" + "?planType=" + SmartInterfaceConstants.SPA_MICRO_PLAN_TYPE + "&appRefNbr=" + appRefNbr;
							log.info("[getJpAppStgLocByRefNbr] Calling SMART service URL= " + url);
							operationDetailsLst =  smartServiceRestClient.getJpAppStgLocByRefNbr(SmartInterfaceConstants.SPA_MICRO_PLAN_TYPE, appRefNbr);
							if (operationDetailsLst != null && operationDetailsLst.size() > 0) {
								List<String> bayNbr = new ArrayList<String>();
								List<String> areaUsed = new ArrayList<String>();
								List<String> opsStartDttm = new ArrayList<String>();
								List<String> opsEndDttm = new ArrayList<String>();
								for (int i = 0; i < operationDetailsLst.size(); i++) {
									SmartInterfaceOutputVO operationDetailsObj = (SmartInterfaceOutputVO) operationDetailsLst.get(i);
									bayNbr.add(CommonUtility.deNull(operationDetailsObj.getStgName()));
									areaUsed.add(String.valueOf(operationDetailsObj.getArea()));
									opsStartDttm.add(CommonUtility.deNull(operationDetailsObj.getOpsStartDttm()));
									opsEndDttm.add(CommonUtility.deNull(operationDetailsObj.getOpsEndDttm()));
								}
								space.setAreaUsed((String[]) areaUsed.toArray(new String[0]));
								space.setBayNbr((String[]) bayNbr.toArray(new String[0]));
								space.setOpsStartDttm((String[]) opsStartDttm.toArray(new String[0]));
								space.setOpsEndDttm((String[]) opsEndDttm.toArray(new String[0]));
								list.set(2, space);
							}
						}
					} catch (Exception ex) {
						log.info("Call SMART Interface to fetch Operation Details is not successfully: Application reference number = " + appRefNbr);
						log.info("Exception miscAppViewSpace : ", ex);
					}
					log.info("***********After SMART interface calling");
					// End added for SMART CR by FPT on 24-Feb-2014
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha

				map.put("details", list);
				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10
				String refNo = criteria.getPredicates().get("refNo");

				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  appSeqNbr --> " + appSeqNbr);
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  refNo --> " + refNo);

				List<StorageOrderValueObject> cntrList = gbMiscApplicationService.getMotCntrList(refNo, appSeqNbr);
				List<StorageOrderValueObject> cntrSummary = gbMiscApplicationService.getMotCntrSummary(refNo,
						appSeqNbr);

				log.info("=============== cntrList.size(): " + cntrList.size());
				log.info("=============== cntrSummary.size(): " + cntrSummary.size());

				map.put("cntrList", cntrList);
				map.put("cntrSummary", cntrSummary);

			}

			/*--------  Start - Added by FPT/LongDH1 - (23-Nov-2011) - For New JP Online Enhancement Phase II- Dashboard  -------*/
			map.put("dashBoard", criteria.getPredicates().get("dashBoard"));

		} catch (BusinessException e) {
			log.info("Exception miscAppViewSpace : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppViewSpace : ", e);
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
			log.info("END: miscAppViewSpace result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppViewHotworkHandler -->perform
	@RequestMapping(value = "/miscAppViewHotwork", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppViewHotwork(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppViewHotwork criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String command = criteria.getPredicates().get("command");

			if ("VIEW".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getHotworkDetails(userId, applyType, appSeqNbr,
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
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppViewHotwork : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppViewHotwork : ", e);
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
			log.info("END: miscAppViewHotwork result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppViewParkMacHandler -->perform
	@RequestMapping(value = "/miscAppViewParkMac", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppViewParkMac(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppViewParkMac criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String command = criteria.getPredicates().get("command");

			if ("VIEW".equals(command)) {

				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getParkingOfForkliftShorecrane(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha

				map.put("details", list);
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppViewParkMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppViewParkMac : ", e);
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
			log.info("END: miscAppViewParkMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppViewReeferHandler -->perform
	@RequestMapping(value = "/miscAppViewReefer", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppViewReefer(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppViewReefer criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			if ("VIEW".equals(command)) {

				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getReeferContainerPowerOutletDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha

				map.put("details", list);
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppViewReefer : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppViewReefer : ", e);
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
			log.info("END: miscAppViewReefer result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.miscApp --> MiscAppViewVehHandler -->perform
	@RequestMapping(value = "/miscAppViewVeh", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppViewVeh(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppViewVeh criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount") + ", map: " + map.toString());
			String command = criteria.getPredicates().get("command");
			if ("VIEW".equals(command)) {

				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getOvernightParkingVehicleDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha

				map.put("details", list);
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppViewVeh : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppViewVeh : ", e);
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
			log.info("END: miscAppViewVeh result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppViewSpreaderHandler -->perform
	@RequestMapping(value = "/miscAppViewSpreader", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppViewSpreader(Criteria criteria, Map<String, Object> map ) throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppViewSpreader criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String command = criteria.getPredicates().get("command");
			if ("VIEW".equals(command)) {

				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getSpreaderDetails(userId, applyType, appSeqNbr,
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
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppViewSpreader : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppViewSpreader : ", e);
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
			log.info("END: miscAppViewSpreader result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppViewTpaHandler -->perform
	@RequestMapping(value = "/miscAppViewTpa", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppViewTpa(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppViewTpa criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String command = criteria.getPredicates().get("command");
			if ("VIEW".equals(command)) {

				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getTrailerParkingApplicationDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);

				map.put("details", list);
				if (criteria.getPredicates().get("from") != null) {
					map.put("from", criteria.getPredicates().get("from"));
				}

			} else if ("ENQUIRE".equals(command)) {
				String startTime = null;
				String startDate = null;
				String endDate = null;
				String endTime = null;
				String areaCode = null;
				String slotTypeCode = null;
				startTime = criteria.getPredicates().get("startTime");
				startDate = criteria.getPredicates().get("startDate");
				endDate = criteria.getPredicates().get("endDate");
				endTime = criteria.getPredicates().get("endTime");
				areaCode = criteria.getPredicates().get("areaCode");
				slotTypeCode = criteria.getPredicates().get("slotType");

				map.put("startTime", startTime);
				map.put("startDate", startDate);
				map.put("endDate", endDate);
				map.put("endTime", endTime);
				map.put("areaCode", areaCode);
				map.put("slotType", slotTypeCode);
				map.put("from", "VIEW");
				// forwardHandler(request, "ListParkingSlot");
				// return;
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppViewTpa : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppViewTpa : ", e);
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
			log.info("END: miscAppViewTpa result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp.enquire --> ListParkingSlotHandler -->perform()
	@RequestMapping(value = "/listParkingSlot", method = RequestMethod.POST)
	public ResponseEntity<?> listParkingSlot(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: listParkingSlot criteria:" + criteria.toString());
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String command = criteria.getPredicates().get("command");
			String startTime = null;
			String startDate = null;
			String endDate = null;
			String endTime = null;
			String areaCode = null;
			String slotTypeCode = null;

			// start modified by thanhbtl6b on 20/09/13 for TPA enhancement
			String trailerTypeCode = null;
			int trailerSize = 0;
			// end modified by thanhbtl6b on 20/09/13 for TPA enhancement

			log.info("-----> inside perform-- ");
			if ("DOWNLOAD".equals(command)) {
				startTime = criteria.getPredicates().get("startTime");
				startDate = criteria.getPredicates().get("startDate");
				endDate = criteria.getPredicates().get("endDate");
				endTime = criteria.getPredicates().get("endTime");
				areaCode = criteria.getPredicates().get("areaCode");
				slotTypeCode = criteria.getPredicates().get("slotType");

				// start modified by thanhbtl6b on 20/09/13 for TPA enhancement
				if (criteria.getPredicates().get("trailerSize") != null) {
					trailerSize = Integer.parseInt(criteria.getPredicates().get("trailerSize"));
				}

				trailerTypeCode = criteria.getPredicates().get("trailerType");
				// end modified by thanhbtl6b on 20/09/13 for TPA enhancement

				EnquireQueryObject queryObj = new EnquireQueryObject();
				queryObj.setAreaCode(StringUtils.trimToNull(areaCode));
				queryObj.setSlotType(StringUtils.trimToNull(slotTypeCode));
				queryObj.setStartDate(StringUtils.trimToNull(startDate));
				queryObj.setStartTime(StringUtils.trimToNull(startTime));
				queryObj.setEndDate(StringUtils.trimToNull(endDate));
				queryObj.setEndTime(StringUtils.trimToNull(endTime));

				// start modified by thanhbtl6b on 20/09/13 for TPA enhancement
				queryObj.setTrailerSize(trailerSize);
				queryObj.setTrailerType(StringUtils.trimToNull(trailerTypeCode));
				// end modified by thanhbtl6b on 20/09/13 for TPA enhancement

				log.info("coCd:" + coCd);
				if ("JP".equalsIgnoreCase(coCd)) {
					queryObj.setStaff(true);
				} else {
					queryObj.setStaff(false);
				}
				queryObj.setDownload(true);

				List<Object> slotList = new ArrayList<Object>();
				int count = 0;
				if (queryObj.isDownload()) {
					slotList = gbMiscApplicationService.listParkingSlotForEnquire(queryObj, criteria, true);
					count = (int) slotList.get(slotList.size() - 1);
					slotList.remove(slotList.size() - 1);

					log.info("-----> slotList- " + slotList.size());
					map.put("getPageList", slotList);
					// Start modified by thanhbtl6b on 20/09/13 for TPA enhancement
					String[] avaiableColLabel = miscAppCommonUtility.calculateAvailableColLabel(
							criteria.getPredicates().get("startDate"), criteria.getPredicates().get("endDate"));
					map.put("avaiableColLabel", avaiableColLabel);
					// End modified by thanhbtl6b on 20/09/13 for TPA enhancement
				} else {
						slotList = gbMiscApplicationService.listParkingSlotForEnquire(queryObj, criteria, false);
						count = (int) slotList.get(slotList.size() - 1);
						slotList.remove(slotList.size() - 1);

						// Start modified by thanhbtl6b on 20/09/13 for TPA enhancement
						String[] avaiableColLabel = miscAppCommonUtility.calculateAvailableColLabel(
								criteria.getPredicates().get("startDate"), criteria.getPredicates().get("endDate"));
						map.put("avaiableColLabel", avaiableColLabel);

					// for paging
					map.put("getPageList", slotList);
					map.put("total", count);

				}
				// nextScreen(request, "EnquireListingSlot");
			} else if ("VIEW".equals(command)) {
				map.put("applyType", criteria.getPredicates().get("applyType"));
				map.put("appSeqNbr", criteria.getPredicates().get("appSeqNbr"));
				map.put("applyTypeNm", criteria.getPredicates().get("applyTypeNm"));

				startTime = criteria.getPredicates().get("startTime");
				startDate = criteria.getPredicates().get("startDate");
				endDate = criteria.getPredicates().get("endDate");
				endTime = criteria.getPredicates().get("endTime");
				areaCode = criteria.getPredicates().get("areaCode");
				slotTypeCode = criteria.getPredicates().get("slotType");

				map.put("startTime", startTime);
				map.put("startDate", startDate);
				map.put("endDate", endDate);
				map.put("endTime", endTime);
				map.put("areaCode", areaCode);
				map.put("slotType", slotTypeCode);

				map.put("from", "ENQUIRE");
				// forwardHandler(request, "MiscAppViewTpa");
				// return;
			} else {
				if (criteria.getPredicates().get("from") != null
						&& "VIEW".equalsIgnoreCase(criteria.getPredicates().get("from"))) {
					startTime = criteria.getPredicates().get("startTime");
					startDate = criteria.getPredicates().get("startDate");
					endDate = criteria.getPredicates().get("endDate");
					endTime = criteria.getPredicates().get("endTime");
					areaCode = criteria.getPredicates().get("areaCode");
					slotTypeCode = criteria.getPredicates().get("slotType");

					// start modified by thanhbtl6b on 20/09/13 for TPA enhancement
					if (criteria.getPredicates().get("trailerSize") != null) {
						trailerSize = Integer.parseInt(criteria.getPredicates().get("trailerSize"));
					}
					if (criteria.getPredicates().get("trailerType") != null) {
						trailerTypeCode = criteria.getPredicates().get("trailerType");
					}
					// end modified by thanhbtl6b on 20/09/13 for TPA enhancement

				} else {
					startTime = criteria.getPredicates().get("startTime");
					startDate = criteria.getPredicates().get("startDate");
					endDate = criteria.getPredicates().get("endDate");
					endTime = criteria.getPredicates().get("endTime");
					areaCode = criteria.getPredicates().get("areaCode");
					slotTypeCode = criteria.getPredicates().get("slotType");

					// start modified by thanhbtl6b on 20/09/13 for TPA enhancement
					if (criteria.getPredicates().get("trailerSize") != null) {
						trailerSize = Integer.parseInt(criteria.getPredicates().get("trailerSize"));
					}
					if (criteria.getPredicates().get("trailerType") != null) {
						trailerTypeCode = criteria.getPredicates().get("trailerType");
					}
					// end modified by thanhbtl6b on 20/09/13 for TPA enhancement
				}
				map.put("startTime", startTime);
				map.put("startDate", startDate);
				map.put("endDate", endDate);
				map.put("endTime", endTime);
				map.put("areaCode", areaCode);
				map.put("slotType", slotTypeCode);

				// start modified by thanhbtl6b on 20/09/13 for TPA enhancement
				map.put("trailerSize", trailerSize);
				map.put("trailerType", trailerTypeCode);
				// end modified by thanhbtl6b on 20/09/13 for TPA enhancement

				EnquireQueryObject queryObj = new EnquireQueryObject();
				queryObj.setAreaCode(StringUtils.trimToNull(areaCode));
				queryObj.setSlotType(StringUtils.trimToNull(slotTypeCode));
				queryObj.setStartDate(StringUtils.trimToNull(startDate));
				queryObj.setStartTime(StringUtils.trimToNull(startTime));
				queryObj.setEndDate(StringUtils.trimToNull(endDate));
				queryObj.setEndTime(StringUtils.trimToNull(endTime));

				// start modified by thanhbtl6b on 20/09/13 for TPA enhancement
				queryObj.setTrailerSize(trailerSize);
				queryObj.setTrailerType(StringUtils.trimToNull(trailerTypeCode));
				// end modified by thanhbtl6b on 20/09/13 for TPA enhancement

				log.info("coCd:" + coCd);
				if ("JP".equalsIgnoreCase(coCd)) {
					queryObj.setStaff(true);
				} else {
					queryObj.setStaff(false);
				}

				List<Object> slotList = new ArrayList<Object>();
				int count = 0;
				if (queryObj.isDownload()) {
					slotList = gbMiscApplicationService.listParkingSlotForEnquire(queryObj, criteria, true);
					count = (int) slotList.get(slotList.size() - 1);
					slotList.remove(slotList.size() - 1);

					log.info("-----> slotList- " + slotList.size());
					map.put("slotList", slotList);
					// Start modified by thanhbtl6b on 20/09/13 for TPA enhancement
					String[] avaiableColLabel = miscAppCommonUtility.calculateAvailableColLabel(
							criteria.getPredicates().get("startDate"), criteria.getPredicates().get("endDate"));
					map.put("avaiableColLabel", avaiableColLabel);
					// End modified by thanhbtl6b on 20/09/13 for TPA enhancement
				} else {
					slotList = gbMiscApplicationService.listParkingSlotForEnquire(queryObj, criteria, false);
					count = (int) slotList.get(slotList.size() - 1);
					slotList.remove(slotList.size() - 1);
					// Start modified by thanhbtl6b on 20/09/13 for TPA enhancement
					String[] avaiableColLabel = miscAppCommonUtility.calculateAvailableColLabel(
							criteria.getPredicates().get("startDate"), criteria.getPredicates().get("endDate"));
					map.put("avaiableColLabel", avaiableColLabel);
					map.put("getPageList", slotList);
					map.put("total", count);

				}

			}
		} catch (BusinessException e) {
			log.info("Exception listParkingSlot : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception listParkingSlot : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: listParkingSlot result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppViewStationMacHandler -->perform
	@RequestMapping(value = "/miscAppViewStationMac", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppViewStationMac(Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppViewStationMac criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String command = criteria.getPredicates().get("command");
			if ("VIEW".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
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
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppViewStationMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppViewStationMac : ", e);
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
			log.info("END: miscAppViewStationMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}
	// VIEW END

	// UPDATE START
	// delegate.helper.gbms.miscApp --> MiscAppUpdateBargeHandler -->perform
	public ResponseEntity<?> miscAppUpdateBarge(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppUpdateBarge criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Barge Details
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
			// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
			String conPerson = null;
			String conTel = null;
			// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel

			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;
			miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			coName = criteria.getPredicates().get("coName");
			appType = criteria.getPredicates().get("appTypeCd");
			account = criteria.getPredicates().get("account");
			appStatusCd = criteria.getPredicates().get("appStatusCd");

			if ("UPDATE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

				List<Object> list = gbMiscApplicationService.getParkingOfLineTowBargeDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);

				List<String> motherShipList = gbMiscApplicationService.getVesselDetails();
				map.put("MotherShipList", motherShipList);

				// Added on 15/06/2007 by Ai Lin - To retrieve application closing time
				String closingTime = gbMiscApplicationService.getClosingTime();
				map.put("closingTime", closingTime);
				// End added on 15/06/2007 by Ai Lin - To retrieve application closing time
			} else if ("UPDATE_SUBMIT".equals(command)) {
				if (agree == null) {
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
					appType = criteria.getPredicates().get("appTypeCd");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel

					if ("D".equals(status) || !("D".equals(appStatusCd))) {
						MiscBargeValueObject obj = new MiscBargeValueObject();
						obj.setBargeName(bargeName);
						obj.setBargeLOA(bargeLOA);
						obj.setDraft(draft);
						obj.setTugboat(tugboat);
						obj.setContactNo(contactNo);
						obj.setFromDate(fromDate);
						obj.setFromTime(fromTime);
						obj.setToDate(toDate);
						obj.setToTime(toTime);
						obj.setMotherShip(motherShip);
						obj.setBerthNo(berthNo);
						obj.setDg(dg);
						obj.setCargoType(cargoType);
						obj.setClassName(className);

						/*
						 * miscAppEjb.updateParkingOfLineTowBargeDetails(userId, miscSeqNbr, status,
						 * coName, appType, account, appStatusCd, obj);
						 */
						// Amended on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
						gbMiscApplicationService.updateParkingOfLineTowBargeDetails(userId, miscSeqNbr, status, coName,
								appType, account, appStatusCd, obj, conPerson, conTel);
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("status", status);
						map.put("bargeName", bargeName);
						map.put("bargeLOA", bargeLOA);
						map.put("draft", draft);
						map.put("tugboat", tugboat);
						map.put("contactNo", contactNo);
						map.put("fromDate", fromDate);
						map.put("toDate", toDate);
						map.put("fromTime", fromTime);
						map.put("toTime", toTime);
						map.put("motherShip", motherShip);
						map.put("berthNo", berthNo);
						map.put("dg", dg);
						map.put("cargoType", cargoType);
						map.put("className", className);
						// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
						map.put("conPerson", conPerson);
						map.put("conTel", conTel);
						// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					}
				} else if (agree != null && agree.equals("true")) {
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
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel

					MiscBargeValueObject obj = new MiscBargeValueObject();
					obj.setBargeName(bargeName);
					obj.setBargeLOA(bargeLOA);
					obj.setDraft(draft);
					obj.setTugboat(tugboat);
					obj.setContactNo(contactNo);
					obj.setFromDate(fromDate);
					obj.setFromTime(fromTime);
					obj.setToDate(toDate);
					obj.setToTime(toTime);
					obj.setMotherShip(motherShip);
					obj.setBerthNo(berthNo);
					obj.setDg(dg);
					obj.setCargoType(cargoType);
					obj.setClassName(className);

					/*
					 * miscAppEjb.updateParkingOfLineTowBargeDetails(userId, miscSeqNbr, status,
					 * coName, appType, account, appStatusCd, obj);
					 */
					// Amended on 14/06/2007 by Punitha.To add Contact Person and Contact Tel
					gbMiscApplicationService.updateParkingOfLineTowBargeDetails(userId, miscSeqNbr, status, coName,
							appType, account, appStatusCd, obj, conPerson, conTel);
					// Ended by Punitha

					// remove session attributes here
					map.remove("coName");
					map.remove("phone");
					map.remove("add1");
					map.remove("add2");
					map.remove("city");
					map.remove("pin");
					map.remove("account");

					// Application Details
					map.remove("miscSeqNbr");
					map.remove("appTypeCd");
					map.remove("appTypeName");
					map.remove("appStatusCd");
					map.remove("appStatusNm");
					map.remove("appDttm");
					map.remove("submitDttm");
					map.remove("submitBy");
					map.remove("approveDttm");
					map.remove("approveBy");
					map.remove("closeDttm");
					map.remove("closeBy");
					map.remove("remarks");
					map.remove("status");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("conPerson");
					map.remove("conTel");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					// Barge Details
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
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateBarge : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateBarge : ", e);
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
			log.info("END: miscAppUpdateBarge result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	@RequestMapping(value = "/miscAppUpdateBarge", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppUpdateBarge(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppUpdateBarge criteria:" + criteria.toString() );
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Barge Details
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
			// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
			String conPerson = null;
			String conTel = null;
			// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel

			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;
			miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			coName = criteria.getPredicates().get("coName");
			appType = criteria.getPredicates().get("appTypeCd");
			account = criteria.getPredicates().get("account");
			appStatusCd = criteria.getPredicates().get("appStatusCd");

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("UPDATE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getParkingOfLineTowBargeDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);

				List<String> motherShipList = gbMiscApplicationService.getVesselDetails();
				map.put("MotherShipList", motherShipList);

				// Added on 15/06/2007 by Ai Lin - To retrieve application closing time
				String closingTime = gbMiscApplicationService.getClosingTime();
				map.put("closingTime", closingTime);
				// End added on 15/06/2007 by Ai Lin - To retrieve application closing time
			} else if ("UPDATE_SUBMIT".equals(command)) {
				if (agree == null) {
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
					appType = criteria.getPredicates().get("appTypeCd");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel

					if ("D".equals(status) || !("D".equals(appStatusCd))) {
						MiscBargeValueObject obj = new MiscBargeValueObject();
						obj.setBargeName(bargeName);
						obj.setBargeLOA(bargeLOA);
						obj.setDraft(draft);
						obj.setTugboat(tugboat);
						obj.setContactNo(contactNo);
						obj.setFromDate(fromDate);
						obj.setFromTime(fromTime);
						obj.setToDate(toDate);
						obj.setToTime(toTime);
						obj.setMotherShip(motherShip);
						obj.setBerthNo(berthNo);
						obj.setDg(dg);
						obj.setCargoType(cargoType);
						obj.setClassName(className);

						/*
						 * miscAppEjb.updateParkingOfLineTowBargeDetails(userId, miscSeqNbr, status,
						 * coName, appType, account, appStatusCd, obj);
						 */
						// Amended on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
						gbMiscApplicationService.updateParkingOfLineTowBargeDetails(userId, miscSeqNbr, status, coName,
								appType, account, appStatusCd, obj, conPerson, conTel);
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("status", status);
						map.put("bargeName", bargeName);
						map.put("bargeLOA", bargeLOA);
						map.put("draft", draft);
						map.put("tugboat", tugboat);
						map.put("contactNo", contactNo);
						map.put("fromDate", fromDate);
						map.put("toDate", toDate);
						map.put("fromTime", fromTime);
						map.put("toTime", toTime);
						map.put("motherShip", motherShip);
						map.put("berthNo", berthNo);
						map.put("dg", dg);
						map.put("cargoType", cargoType);
						map.put("className", className);
						// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
						map.put("conPerson", conPerson);
						map.put("conTel", conTel);
						// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					}
				} else if (agree != null && agree.equals("true")) {
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
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel

					MiscBargeValueObject obj = new MiscBargeValueObject();
					obj.setBargeName(bargeName);
					obj.setBargeLOA(bargeLOA);
					obj.setDraft(draft);
					obj.setTugboat(tugboat);
					obj.setContactNo(contactNo);
					obj.setFromDate(fromDate);
					obj.setFromTime(fromTime);
					obj.setToDate(toDate);
					obj.setToTime(toTime);
					obj.setMotherShip(motherShip);
					obj.setBerthNo(berthNo);
					obj.setDg(dg);
					obj.setCargoType(cargoType);
					obj.setClassName(className);

					/*
					 * miscAppEjb.updateParkingOfLineTowBargeDetails(userId, miscSeqNbr, status,
					 * coName, appType, account, appStatusCd, obj);
					 */
					// Amended on 14/06/2007 by Punitha.To add Contact Person and Contact Tel
					gbMiscApplicationService.updateParkingOfLineTowBargeDetails(userId, miscSeqNbr, status, coName,
							appType, account, appStatusCd, obj, conPerson, conTel);
					// Ended by Punitha

					// remove session attributes here
					map.remove("coName");
					map.remove("phone");
					map.remove("add1");
					map.remove("add2");
					map.remove("city");
					map.remove("pin");
					map.remove("account");

					// Application Details
					map.remove("miscSeqNbr");
					map.remove("appTypeCd");
					map.remove("appTypeName");
					map.remove("appStatusCd");
					map.remove("appStatusNm");
					map.remove("appDttm");
					map.remove("submitDttm");
					map.remove("submitBy");
					map.remove("approveDttm");
					map.remove("approveBy");
					map.remove("closeDttm");
					map.remove("closeBy");
					map.remove("remarks");
					map.remove("status");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("conPerson");
					map.remove("conTel");
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
					// Barge Details
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
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateBarge : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateBarge : ", e);
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
			log.info("END: miscAppUpdateBarge result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateContractHandler -->perform
	public ResponseEntity<?> miscAppUpdateContract(HttpServletRequest request, Criteria criteria,
			Map<String, Object> map) throws BusinessException {

		// Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		// Map<String, Object> map = new HashMap<>();

		try {
			log.info("START: miscAppUpdateContract criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Contract Details
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
			// String docType = null;
			// String file = null;

			List<MiscAppValueObject> docTypeList = null;
			ArrayList<String> docNameList = null;
			ArrayList<String> docCdList = null;
			ArrayList<String> docUploadDttmList = null;
			ArrayList<String> docUploadByList = null;
			List<MiscAppValueObject> contractTypeList = null;

			String status = null;
			String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			String coName = criteria.getPredicates().get("coName");
			String appType = criteria.getPredicates().get("appTypeCd");
			String account = criteria.getPredicates().get("account");
			String appStatusCd = criteria.getPredicates().get("appStatusCd");
			map.put("coCd", coCd);
			if ("UPDATE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

				List<Object> list = gbMiscApplicationService.getContractorPermitDetails(userId, applyType, appSeqNbr,
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

				contractTypeList = gbMiscApplicationService.getContractTypeList();
				map.put("ContractTypeList", contractTypeList);
				docTypeList = gbMiscApplicationService.getContractUploadDocumentList();
				map.put("DocTypeList", docTypeList);
			} else if ("CANCEL".equals(command)) {
				// removeSessionAttributes(request);
				// forwardHandler(request, "MiscAppList");
				// return;
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

				status = criteria.getPredicates().get("status");
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
			} else { // if("UPDATE_PARKMAC".equals(command)){
				if (agree == null) {
					status = criteria.getPredicates().get("status");
					location = criteria.getPredicates().get("location");
					description = criteria.getPredicates().get("description");
					others = criteria.getPredicates().get("others");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					// licType = criteria.getPredicates().get("licType");
					// licNo = criteria.getPredicates().get("licNo");
					remarks = criteria.getPredicates().get("remarks");
					waiver = criteria.getPredicates().get("waiver");
					contCoNm = criteria.getPredicates().get("contCoNm");
					contCoAddr = criteria.getPredicates().get("contCoAddr");
					contactNm = criteria.getPredicates().get("contactNm");
					contactNric = criteria.getPredicates().get("contactNric");
					designation = criteria.getPredicates().get("designation");

					if (waiver == null)
						waiver = "N";
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
					if (docUploadDttmList == null) {
						docUploadDttmList = new ArrayList<String>();
						String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request,
								"docUploadDttmTemp"));

						if (temp != null && temp.length > 0) {
							for (int i = 0; i < temp.length; i++)
								docUploadDttmList.add(temp[i]);
						}
					}
					if (docUploadByList == null) {
						docUploadByList = new ArrayList<String>();
						String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request, "docUploadByTemp"));

						if (temp != null && temp.length > 0) {
							for (int i = 0; i < temp.length; i++)
								docUploadByList.add(temp[i]);
						}
					}
					
					if ("UPLOAD".equals(command)) {
						/*
						 * if (docTypeList == null) { docTypeList = new ArrayList();
						 * docTypeList.add(docType); map.put("docTypeList", docTypeList); } else {
						 * docTypeList.add(docType); } if (docNameList == null) { docNameList = new
						 * ArrayList(); docNameList.add(file); map.put("docNameList", docNameList); }
						 * else { docNameList.add(file); } miscSeqNbr = (String)
						 * criteria.getPredicates().get("miscSeqNbr"); byte[] fileContent = null; String
						 * assignedName = (String)miscAppEjb.uploadDocument(userId, miscSeqNbr, status,
						 * docType, file); //System.out.println("file -----> " + file);
						 * //System.out.println("assignedName -----> " + assignedName); fileContent =
						 * UploadDocument.getFileContent(file); UploadDocument.writeToFile(fileContent,
						 * assignedName, "CONTRACT");
						 * 
						 * ArrayList docDetailsList =
						 * miscAppEjb.getContractUploadDocumentDetails(miscSeqNbr);
						 * map.put("docDetailsList", docDetailsList);
						 * 
						 * map.put("status", status); map.put("location", location);
						 * map.put("description", description); map.put("others", others);
						 * map.put("fromDate", fromDate); map.put("fromTime", fromTime);
						 * map.put("toDate", toDate); map.put("toTime", toTime); map.put("licType",
						 * licType); map.put("licNo", licNo); map.put("remarks", remarks);
						 * map.put("waiver", waiver); map.put("contCoNm", contCoNm);
						 * map.put("contCoAddr", contCoAddr); map.put("contactNm", contactNm);
						 * map.put("contactNric", contactNric); map.put("designation", designation);
						 * 
						 * nextScreen(request, "MiscAppUpdateContract"); return;
						 */

						map.put("subDirName", "CONTRACT");
						map.put("nextScreen", "MiscAppUpdateContract");
					}
					
					if (docTypeList != null)
						map.put("docTypeTemp", docTypeList.toArray(new String[0]));
					if (docNameList != null)
						map.put("docNameTemp", docNameList.toArray(new String[0]));
					if (docCdList != null)
						map.put("docCdTemp", docCdList.toArray(new String[0]));
					if (docUploadDttmList != null)
						map.put("docUploadDttmTemp", docUploadDttmList.toArray(new String[0]));
					if (docUploadByList != null)
						map.put("docUploadByTemp", docUploadByList.toArray(new String[0]));
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

					if ("UPDATE_SUBMIT".equals(command) && ("D".equals(status) || !("D".equals(appStatusCd)))) {
						MiscContractValueObject obj = new MiscContractValueObject();
						obj.setLocation(location);
						obj.setDescription(description);
						obj.setOthers(others);
						obj.setFromDate(fromDate);
						obj.setFromTime(fromTime);
						obj.setToDate(toDate);
						obj.setToTime(toTime);
						obj.setLicType(licType);
						obj.setLicNo(licNo);
						obj.setRemarks(remarks);
						obj.setWaiver(waiver);
						obj.setContCoNm(contCoNm);
						obj.setContCoAddr(contCoAddr);
						obj.setContactNm(contactNm);
						obj.setContactNric(contactNric);
						obj.setDesignation(designation);

						if (docTypeList != null) {
							obj.setDocType((String[]) docTypeList.toArray(new String[0]));
						} 
						if (docNameList != null) {
							obj.setDocName((String[]) docNameList.toArray(new String[0]));
						} 
						gbMiscApplicationService.updateContractorPermitDetails(userId, miscSeqNbr, status, coName,
								appType, account, appStatusCd, obj);

						/*
						 * System.out.println("assignFileName -----> " + assignFileName); //write file
						 * into server byte[] fileContent = null; String[] fileName = obj.getDocName();
						 * if(fileName != null && assignFileName != null){
						 * //System.out.println("SAVE * assignFileName -----> " +
						 * assignFileName.size()); for(int i = 0; i < fileName.length; i++){
						 * //System.out.println("SAVE * fileName -----> " + fileName[i]); if(fileName[i]
						 * != null && fileName[i].indexOf("\\") != -1){ fileContent =
						 * UploadDocument.getFileContent(fileName[i]);
						 * UploadDocument.writeToFile(fileContent, (String)assignFileName.get(i),
						 * "CONTRACT"); } } }
						 */

						// remove session attributes here
						// removeSessionAttributes(session);
						// forwardHandler(request, "MiscAppList");
						// return;
					}
				} else if (agree != null && agree.equals("true")) {
					status = criteria.getPredicates().get("status");
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

					MiscContractValueObject obj = new MiscContractValueObject();
					obj.setLocation(location);
					obj.setDescription(description);
					obj.setOthers(others);
					obj.setFromDate(fromDate);
					obj.setFromTime(fromTime);
					obj.setToDate(toDate);
					obj.setToTime(toTime);
					obj.setLicType(licType);
					obj.setLicNo(licNo);
					obj.setRemarks(remarks);
					obj.setWaiver(waiver);
					obj.setContCoNm(contCoNm);
					obj.setContCoAddr(contCoAddr);
					obj.setContactNm(contactNm);
					obj.setContactNric(contactNric);
					obj.setDesignation(designation);

					if (docTypeList != null) {
						obj.setDocType((String[]) docTypeList.toArray(new String[0]));
					} 
					if (docNameList != null) {
						obj.setDocName((String[]) docNameList.toArray(new String[0]));
					}
					gbMiscApplicationService.updateContractorPermitDetails(userId, miscSeqNbr, status, coName, appType,
							account, appStatusCd, obj);

					/*
					 * System.out.println("assignFileName -----> " + assignFileName); //write file
					 * into server byte[] fileContent = null; String[] fileName = obj.getDocName();
					 * if(fileName != null && assignFileName != null){
					 * //System.out.println("SUBMIT * assignFileName -----> " +
					 * assignFileName.size()); for(int i = 0; i < fileName.length; i++){
					 * //System.out.println("SUBMIT * fileName -----> " + fileName[i]);
					 * if(fileName[i] != null && fileName[i].indexOf("\\") != -1){ fileContent =
					 * UploadDocument.getFileContent(fileName[i]);
					 * UploadDocument.writeToFile(fileContent, (String)assignFileName.get(i),
					 * "CONTRACT"); } } }
					 */

					// remove session attributes here
					// removeSessionAttributes(session);
					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateContract : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateContract : ", e);
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
			log.info("END: miscAppUpdateContract result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	@RequestMapping(value = "/miscAppUpdateContract", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppUpdateContract(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppUpdateContract criteria:" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Contract Details
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
			// String docType = null;
			// String file = null;

			List<MiscAppValueObject> docTypeList = null;
			List<String> docNameList = null;
			List<String> docCdList = null;
			List<String> docUploadDttmList = null;
			List<String> docUploadByList = null;
			List<MiscAppValueObject> contractTypeList = null;

			String status = null;
			String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			String coName = criteria.getPredicates().get("coName");
			String appType = criteria.getPredicates().get("appTypeCd");
			String account = criteria.getPredicates().get("account");
			String appStatusCd = criteria.getPredicates().get("appStatusCd");
			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			map.put("coCd", coCd);
			if ("UPDATE".equals(command)) {
				// String applyType = criteria.getPredicates().get("applyType");
				// String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				// String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

				List<Object> list = gbMiscApplicationService.getContractorPermitDetails(userId, applyType, appSeqNbr,
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

				contractTypeList = gbMiscApplicationService.getContractTypeList();
				map.put("ContractTypeList", contractTypeList);
				docTypeList = gbMiscApplicationService.getContractUploadDocumentList();
				map.put("DocTypeList", docTypeList);
			} else if ("CANCEL".equals(command)) {
				// removeSessionAttributes(request);
				// forwardHandler(request, "MiscAppList");
				// return;
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

				status = criteria.getPredicates().get("status");
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
			} else { // if("UPDATE_PARKMAC".equals(command)){
				if (agree == null) {
					status = criteria.getPredicates().get("status");
					location = criteria.getPredicates().get("location");
					description = criteria.getPredicates().get("description");
					others = criteria.getPredicates().get("others");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					// licType = criteria.getPredicates().get("licType");
					// licNo = criteria.getPredicates().get("licNo");
					remarks = criteria.getPredicates().get("remarks");
					waiver = criteria.getPredicates().get("waiver");
					contCoNm = criteria.getPredicates().get("contCoNm");
					contCoAddr = criteria.getPredicates().get("contCoAddr");
					contactNm = criteria.getPredicates().get("contactNm");
					contactNric = criteria.getPredicates().get("contactNric");
					designation = criteria.getPredicates().get("designation");

					if (waiver == null)
						waiver = "N";
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

					if (docUploadDttmList == null) {
						docUploadDttmList = new ArrayList<String>();
						String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request,
								"docUploadDttmTemp"));

						if (temp != null && temp.length > 0) {
							for (int i = 0; i < temp.length; i++)
								docUploadDttmList.add(temp[i]);
						}
					}
					if (docUploadByList == null) {
						docUploadByList = new ArrayList<String>();
						String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request, "docUploadByTemp"));

						if (temp != null && temp.length > 0) {
							for (int i = 0; i < temp.length; i++)
								docUploadByList.add(temp[i]);
						}
					}

					if ("UPLOAD".equals(command)) {
						/*
						 * if (docTypeList == null) { docTypeList = new ArrayList();
						 * docTypeList.add(docType); map.put("docTypeList", docTypeList); } else {
						 * docTypeList.add(docType); } if (docNameList == null) { docNameList = new
						 * ArrayList(); docNameList.add(file); map.put("docNameList", docNameList); }
						 * else { docNameList.add(file); } miscSeqNbr = (String)
						 * criteria.getPredicates().get("miscSeqNbr"); byte[] fileContent = null; String
						 * assignedName = (String)miscAppEjb.uploadDocument(userId, miscSeqNbr, status,
						 * docType, file); //System.out.println("file -----> " + file);
						 * //System.out.println("assignedName -----> " + assignedName); fileContent =
						 * UploadDocument.getFileContent(file); UploadDocument.writeToFile(fileContent,
						 * assignedName, "CONTRACT");
						 * 
						 * ArrayList docDetailsList =
						 * miscAppEjb.getContractUploadDocumentDetails(miscSeqNbr);
						 * map.put("docDetailsList", docDetailsList);
						 * 
						 * map.put("status", status); map.put("location", location);
						 * map.put("description", description); map.put("others", others);
						 * map.put("fromDate", fromDate); map.put("fromTime", fromTime);
						 * map.put("toDate", toDate); map.put("toTime", toTime); map.put("licType",
						 * licType); map.put("licNo", licNo); map.put("remarks", remarks);
						 * map.put("waiver", waiver); map.put("contCoNm", contCoNm);
						 * map.put("contCoAddr", contCoAddr); map.put("contactNm", contactNm);
						 * map.put("contactNric", contactNric); map.put("designation", designation);
						 * 
						 * nextScreen(request, "MiscAppUpdateContract"); return;
						 */

						map.put("subDirName", "CONTRACT");
						map.put("nextScreen", "MiscAppUpdateContract");
					} else if ("DELETE".equals(command)) {
						/*
						 * String[] delList = criteria.getPredicates().getValues("docCheck"); ArrayList
						 * removeTypeList = new ArrayList(); ArrayList removeNameList = new ArrayList();
						 * for (int i = 0; i < delList.length; i++) { removeTypeList.add((String)
						 * docTypeList.get(Integer.parseInt(delList[i]))); removeNameList.add((String)
						 * docNameList.get(Integer.parseInt(delList[i]))); } for (int i = 0; i <
						 * removeTypeList.size(); i++) { docTypeList.remove(removeTypeList.get(i)); }
						 * for (int i = 0; i < removeNameList.size(); i++) {
						 * docNameList.remove(removeNameList.get(i)); }
						 */
						/*
						 * String[] delList = criteria.getPredicates().getValues("docCheck");
						 * 
						 * //delete from server if(delList != null){
						 * //System.out.println("Going to delete files from serever..."); for(int i = 0;
						 * i < delList.length; i++){ //System.out.println("delList[" + i + "] -----> " +
						 * delList[i]); UploadDocument.deleteFile((String)delList[i], "CONTRACT"); } }
						 * miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
						 * miscAppEjb.deleteDocument(miscSeqNbr, delList);
						 * 
						 * ArrayList docDetailsList =
						 * miscAppEjb.getContractUploadDocumentDetails(miscSeqNbr);
						 * map.put("docDetailsList", docDetailsList);
						 * 
						 * map.put("status", status); map.put("location", location);
						 * map.put("description", description); map.put("others", others);
						 * map.put("fromDate", fromDate); map.put("fromTime", fromTime);
						 * map.put("toDate", toDate); map.put("toTime", toTime); map.put("licType",
						 * licType); map.put("licNo", licNo); map.put("remarks", remarks);
						 * map.put("waiver", waiver); map.put("contCoNm", contCoNm);
						 * map.put("contCoAddr", contCoAddr); map.put("contactNm", contactNm);
						 * map.put("contactNric", contactNric); map.put("designation", designation);
						 * 
						 * nextScreen(request, "MiscAppUpdateContract"); return;
						 */

						// String[] delList = (CommonUtil.getRequiredStringParameters(request,
						// "docCheck").split(","));
						String[] delList = criteria.getPredicates().get("docCheck").split(",");
						String[] assignedNameList = criteria.getPredicates().get("assignedName").split(",");

						List<MiscAppValueObject> removeTypeList = new ArrayList<MiscAppValueObject>();
						List<String> removeNameList = new ArrayList<String>();
						List<String> removeCdList = new ArrayList<String>();
						List<String> removeUploadDttmList = new ArrayList<String>();
						List<String> removeUploadByList = new ArrayList<String>();
						/*
						 * for (int i = 0; i < delList.length; i++) { removeTypeList.add((String)
						 * docTypeList.get(Integer.parseInt(delList[i]))); removeNameList.add((String)
						 * docNameList.get(Integer.parseInt(delList[i]))); }
						 */
						// String delFile = null;
						
						List<String> deletedDoc = gbMiscApplicationService.getDelFile(criteria.getPredicates().get("assignedName"), miscSeqNbr, "CONTRACT", "MISC_CDOC");
						
						for (int i = 0; i < delList.length; i++) {
							Path pathFile = Paths.get(deletedDoc.get(i)).toAbsolutePath().normalize(); 
							UploadDocument.deleteFile(pathFile.toString());
							gbMiscApplicationService.deleteFileData(userId, miscSeqNbr, assignedNameList[i]);
							if (docTypeList != null && docTypeList.size() > 0) {
								removeTypeList.add(docTypeList.get(Integer.parseInt(delList[i])));
								removeNameList.add(docNameList.get(Integer.parseInt(delList[i])));
								removeCdList.add(docCdList.get(Integer.parseInt(delList[i])));
								removeUploadDttmList.add(docUploadDttmList.get(Integer.parseInt(delList[i])));
								removeUploadByList.add(docUploadByList.get(Integer.parseInt(delList[i])));
							}
						}
						if (docTypeList != null && docTypeList.size() > 0) {
							for (int i = 0; i < removeTypeList.size(); i++) {
								docTypeList.remove(removeTypeList.get(i));
							}
							for (int i = 0; i < removeNameList.size(); i++) {
								docNameList.remove(removeNameList.get(i));
							}
							for (int i = 0; i < removeCdList.size(); i++) {
								docCdList.remove(removeCdList.get(i));
							}
							for (int i = 0; i < removeUploadDttmList.size(); i++) {
								docUploadDttmList.remove(removeUploadDttmList.get(i));
							}
							for (int i = 0; i < removeUploadByList.size(); i++) {
								docUploadByList.remove(removeUploadByList.get(i));
							}
						}
					}
					if (docTypeList != null)
						map.put("docTypeTemp", docTypeList.toArray(new String[0]));
					if (docNameList != null)
						map.put("docNameTemp", docNameList.toArray(new String[0]));
					if (docCdList != null)
						map.put("docCdTemp", docCdList.toArray(new String[0]));
					if (docUploadDttmList != null)
						map.put("docUploadDttmTemp", docUploadDttmList.toArray(new String[0]));
					if (docUploadByList != null)
						map.put("docUploadByTemp", docUploadByList.toArray(new String[0]));
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

					if ("UPDATE_SUBMIT".equals(command) && ("D".equals(status) || !("D".equals(appStatusCd)))) {
						MiscContractValueObject obj = new MiscContractValueObject();
						obj.setLocation(location);
						obj.setDescription(description);
						obj.setOthers(others);
						obj.setFromDate(fromDate);
						obj.setFromTime(fromTime);
						obj.setToDate(toDate);
						obj.setToTime(toTime);
						obj.setLicType(licType);
						obj.setLicNo(licNo);
						obj.setRemarks(remarks);
						obj.setWaiver(waiver);
						obj.setContCoNm(contCoNm);
						obj.setContCoAddr(contCoAddr);
						obj.setContactNm(contactNm);
						obj.setContactNric(contactNric);
						obj.setDesignation(designation);

						if (docTypeList != null) {
							obj.setDocType((String[]) docTypeList.toArray(new String[0]));
						} 
						if (docNameList != null) {
							obj.setDocName((String[]) docNameList.toArray(new String[0]));
						} 
						gbMiscApplicationService.updateContractorPermitDetails(userId, appSeqNbr, status, coName,
								appType, account, appStatusCd, obj);

						// remove session attributes here
						// removeSessionAttributes(session);
						// forwardHandler(request, "MiscAppList");
						// return;
					}
				} else if (agree != null && agree.equals("true")) {
					status = criteria.getPredicates().get("status");
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
					// criteria.getPredicates().get("docTypeTemp")).length);

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

					if (waiver == null)
						waiver = "N";

					MiscContractValueObject obj = new MiscContractValueObject();
					obj.setLocation(location);
					obj.setDescription(description);
					obj.setOthers(others);
					obj.setFromDate(fromDate);
					obj.setFromTime(fromTime);
					obj.setToDate(toDate);
					obj.setToTime(toTime);
					obj.setLicType(licType);
					obj.setLicNo(licNo);
					obj.setRemarks(remarks);
					obj.setWaiver(waiver);
					obj.setContCoNm(contCoNm);
					obj.setContCoAddr(contCoAddr);
					obj.setContactNm(contactNm);
					obj.setContactNric(contactNric);
					obj.setDesignation(designation);

					if (docTypeList != null) {
						obj.setDocType((String[]) docTypeList.toArray(new String[0]));
					} 
					if (docNameList != null) {
						obj.setDocName((String[]) docNameList.toArray(new String[0]));
					}
					gbMiscApplicationService.updateContractorPermitDetails(userId, miscSeqNbr, status, coName, appType,
							account, appStatusCd, obj);

					// remove session attributes here
					// removeSessionAttributes(session);
					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateContract : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateContract : ", e);
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
			log.info("END: miscAppUpdateContract result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateHotworkHandler -->perform
	@RequestMapping(value = "/miscAppUpdateHotwork", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppUpdateHotwork(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppUpdateHotwork criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Hotwork Details
			String location = null;
			String description = null;
			String fromDate = null;
			String fromTime = null;
			String toDate = null;
			String toTime = null;

			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;

			miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			coName = criteria.getPredicates().get("coName");
			appType = criteria.getPredicates().get("appTypeCd");
			account = criteria.getPredicates().get("account");
			appStatusCd = criteria.getPredicates().get("appStatusCd");

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("UPDATE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getHotworkDetails(userId, applyType, appSeqNbr,
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
			} else if ("UPDATE_SUBMIT".equals(command)) {
				if (agree == null) {
					status = criteria.getPredicates().get("status");
					location = criteria.getPredicates().get("location");
					description = criteria.getPredicates().get("description");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");

					if ("D".equals(status) || !("D".equals(appStatusCd))) {
						MiscHotworkValueObject obj = new MiscHotworkValueObject();
						obj.setLocation(location);
						obj.setDescription(description);
						obj.setFromDate(fromDate);
						obj.setFromTime(fromTime);
						obj.setToDate(toDate);
						obj.setToTime(toTime);

						gbMiscApplicationService.updateHotworkDetails(userId, miscSeqNbr, status, coName, appType,
								account, appStatusCd, obj);
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("status", criteria.getPredicates().get("status"));
						map.put("location", location);
						map.put("description", description);
						map.put("fromDate", fromDate);
						map.put("fromTime", fromTime);
						map.put("toDate", toDate);
						map.put("toTime", toTime);
					}
				} else if (agree != null && agree.equals("true")) {
					status = criteria.getPredicates().get("status");
					location = criteria.getPredicates().get("location");
					description = criteria.getPredicates().get("description");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");

					MiscHotworkValueObject obj = new MiscHotworkValueObject();
					obj.setLocation(location);
					obj.setDescription(description);
					obj.setFromDate(fromDate);
					obj.setFromTime(fromTime);
					obj.setToDate(toDate);
					obj.setToTime(toTime);

					gbMiscApplicationService.updateHotworkDetails(userId, miscSeqNbr, status, coName, appType, account,
							appStatusCd, obj);

					// remove session attributes here
					map.remove("coName");
					map.remove("phone");
					map.remove("add1");
					map.remove("add2");
					map.remove("city");
					map.remove("pin");
					map.remove("account");

					// Application Details
					map.remove("miscSeqNbr");
					map.remove("appTypeCd");
					map.remove("appTypeName");
					map.remove("appStatusCd");
					map.remove("appStatusNm");
					map.remove("appDttm");
					map.remove("submitDttm");
					map.remove("submitBy");
					map.remove("approveDttm");
					map.remove("approveBy");
					map.remove("closeDttm");
					map.remove("closeBy");
					map.remove("remarks");
					map.remove("status");
					// Added on 18/07/2007 by Punitha.To obtain the varcode
					map.remove("varcode");
					// Ended by Punitha
					// Space Details
					map.remove("location");
					map.remove("description");
					map.remove("fromDate");
					map.remove("fromTime");
					map.remove("toDate");
					map.remove("toTime");

					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateHotwork : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateHotwork : ", e);
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
			log.info("END: miscAppUpdateHotwork result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateHotworkHandler -->perform
	public ResponseEntity<?> miscAppUpdateHotwork(HttpServletRequest request, Criteria criteria,
			Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppUpdateHotwork criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Hotwork Details
			String location = null;
			String description = null;
			String fromDate = null;
			String fromTime = null;
			String toDate = null;
			String toTime = null;

			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;

			miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			coName = criteria.getPredicates().get("coName");
			appType = criteria.getPredicates().get("appTypeCd");
			account = criteria.getPredicates().get("account");
			appStatusCd = criteria.getPredicates().get("appStatusCd");


			if ("UPDATE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getHotworkDetails(userId, applyType, appSeqNbr,
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
			} else if ("UPDATE_SUBMIT".equals(command)) {
				if (agree == null) {
					status = criteria.getPredicates().get("status");
					location = criteria.getPredicates().get("location");
					description = criteria.getPredicates().get("description");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");

					if ("D".equals(status) || !("D".equals(appStatusCd))) {
						MiscHotworkValueObject obj = new MiscHotworkValueObject();
						obj.setLocation(location);
						obj.setDescription(description);
						obj.setFromDate(fromDate);
						obj.setFromTime(fromTime);
						obj.setToDate(toDate);
						obj.setToTime(toTime);

						gbMiscApplicationService.updateHotworkDetails(userId, miscSeqNbr, status, coName, appType,
								account, appStatusCd, obj);
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("status", criteria.getPredicates().get("status"));
						map.put("location", location);
						map.put("description", description);
						map.put("fromDate", fromDate);
						map.put("fromTime", fromTime);
						map.put("toDate", toDate);
						map.put("toTime", toTime);
					}
				} else if (agree != null && agree.equals("true")) {
					status = criteria.getPredicates().get("status");
					location = criteria.getPredicates().get("location");
					description = criteria.getPredicates().get("description");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");

					MiscHotworkValueObject obj = new MiscHotworkValueObject();
					obj.setLocation(location);
					obj.setDescription(description);
					obj.setFromDate(fromDate);
					obj.setFromTime(fromTime);
					obj.setToDate(toDate);
					obj.setToTime(toTime);

					gbMiscApplicationService.updateHotworkDetails(userId, miscSeqNbr, status, coName, appType, account,
							appStatusCd, obj);

					// remove session attributes here
					map.remove("coName");
					map.remove("phone");
					map.remove("add1");
					map.remove("add2");
					map.remove("city");
					map.remove("pin");
					map.remove("account");

					// Application Details
					map.remove("miscSeqNbr");
					map.remove("appTypeCd");
					map.remove("appTypeName");
					map.remove("appStatusCd");
					map.remove("appStatusNm");
					map.remove("appDttm");
					map.remove("submitDttm");
					map.remove("submitBy");
					map.remove("approveDttm");
					map.remove("approveBy");
					map.remove("closeDttm");
					map.remove("closeBy");
					map.remove("remarks");
					map.remove("status");
					// Added on 18/07/2007 by Punitha.To obtain the varcode
					map.remove("varcode");
					// Ended by Punitha
					// Space Details
					map.remove("location");
					map.remove("description");
					map.remove("fromDate");
					map.remove("fromTime");
					map.remove("toDate");
					map.remove("toTime");

					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateHotwork : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateHotwork : ", e);
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
			log.info("END: miscAppUpdateHotwork result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateParkMacHandler -->perform
	public ResponseEntity<?> miscAppUpdateParkMac(HttpServletRequest request, Criteria criteria,
			Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppUpdateParkMac criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			// String agree = criteria.getPredicates().get("agree");

			map.put("coCd", coCd);
			// Park Mac Details
			String macType = null;
			String fromDate = null;
			String fromTime = null;
			String toDate = null;
			String toTime = null;
			String remarks = null;
			// String docType = null;
			// String file = null;
			List<MiscAppValueObject> docTypeList = null;
			List<String> docNameList = null;
			List<String> docCdList = null;
			List<String> docUploadDttmList = null;
			List<String> docUploadByList = null;
			List<String> regNbrList = null;
			// Added by Punitha on 28/01/2008
			List<MiscParkMacValueObject> macDetList = new ArrayList<MiscParkMacValueObject>();
			MiscParkMacValueObject parkMac = new MiscParkMacValueObject();
			// End
			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;

			if ("UPDATE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

				List<Object> list = gbMiscApplicationService.getParkingOfForkliftShorecrane(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);

				docTypeList = gbMiscApplicationService.getUploadDocumentTypeList();
				map.put("DocTypeList", docTypeList);
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

				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				fromTime = criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate");
				toTime = criteria.getPredicates().get("toTime");
				remarks = criteria.getPredicates().get("remarks");

				//	if (regNbrList != null)
				//		map.put("regNbrTemp", regNbrList.toArray(new String[0]));
			} else if ("UPDATE_SUBMIT".equals(command)) {
				miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				status = criteria.getPredicates().get("status");
				coName = criteria.getPredicates().get("coName");
				appType = criteria.getPredicates().get("appTypeCd");
				account = criteria.getPredicates().get("account");
				appStatusCd = criteria.getPredicates().get("appStatusCd");
				// regNbrList = request.getAttribute("regNbrList");
				// Added by Punitha on 28/01/2008
				// macDetList = request.getAttribute("macDetList");

				Object listObject = null;
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

				listObject = request.getAttribute("macDetList");
				macDetList = new ArrayList<MiscParkMacValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							macDetList.add((MiscParkMacValueObject) item);
						}
					}
				}

				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				fromTime = criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate");
				toTime = criteria.getPredicates().get("toTime");
				remarks = criteria.getPredicates().get("remarks");

				MiscParkMacValueObject obj = new MiscParkMacValueObject();
				obj.setMacType(macType);
				obj.setFromDate(fromDate);
				obj.setFromTime(fromTime);
				obj.setToDate(toDate);
				obj.setToTime(toTime);
				obj.setRemarks(remarks);

				if (regNbrList != null) {
					obj.setRegNbr((String[]) regNbrList.toArray(new String[0]));
				} 
				if (docTypeList == null) {
					obj.setDocType((String[]) (CommonUtil.getRequiredStringParameters(request, "docTypeTemp")));

				} 
				if (docNameList == null) {
					obj.setDocName((String[]) (CommonUtil.getRequiredStringParameters(request, "docNameTemp")));
				} 

				// Added by Punitha on 28/01/2008
				if (macDetList != null) {
					obj.setMacDetList(macDetList);
				}
				gbMiscApplicationService.updateParkingOfForkliftShorecrane(userId, miscSeqNbr, status, coName, appType,
						account, appStatusCd, obj);

				// remove session attributes here
				// removeSessionAttributes(session);
				// forwardHandler(request, "MiscAppList");
				// return;
			} else if ("CANCEL".equals(command)) {
				// removeSessionAttributes(session);
				// forwardHandler(request, "MiscAppList");
				// return;
			} else {
				status = criteria.getPredicates().get("status");
				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				fromTime = criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate");
				toTime = criteria.getPredicates().get("toTime");
				remarks = criteria.getPredicates().get("remarks");
				// docType = criteria.getPredicates().get("docType");
				// file = criteria.getPredicates().get("file");

				Object listObject = null;
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

				if (docUploadDttmList == null) {
					docUploadDttmList = new ArrayList<String>();
					String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request, "docUploadDttmTemp"));

					if (temp != null && temp.length > 0) {
						for (int i = 0; i < temp.length; i++)
							docUploadDttmList.add(temp[i]);
					}
				}
				if (docUploadByList == null) {
					docUploadByList = new ArrayList<String>();
					String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request, "docUploadByTemp"));

					if (temp != null && temp.length > 0) {
						for (int i = 0; i < temp.length; i++)
							docUploadByList.add(temp[i]);
					}
				}

				if ("UPLOAD".equals(command)) {
					/*
					 * miscSeqNbr = session.getAttribute("miscSeqNbr"); byte[] fileContent = null;
					 * String assignedName = (String)miscAppEjb.uploadDocument(userId, miscSeqNbr,
					 * status, docType, file); //System.out.println("file -----> " + file);
					 * //System.out.println("assignedName -----> " + assignedName); fileContent =
					 * UploadDocument.getFileContent(file); UploadDocument.writeToFile(fileContent,
					 * assignedName, "MACHINE");
					 * 
					 * ArrayList docDetailsList = miscAppEjb.getUploadDocumentDetails(miscSeqNbr);
					 * map.put("docDetailsList", docDetailsList);
					 * 
					 * map.put("status", status); map.put("macType", macType); map.put("fromDate",
					 * fromDate); map.put("fromTime", fromTime); map.put("toDate", toDate);
					 * map.put("toTime", toTime); map.put("remarks", remarks);
					 * 
					 * nextScreen(request, "MiscAppUpdateParkMac"); return;
					 */

					map.put("subDirName", "MACHINE");
					map.put("nextScreen", "MiscAppUpdateParkMac");
				} else if ("DELETE".equals(command)) {
					/*
					 * String[] delList = criteria.getPredicates().getValues("docCheck");
					 * 
					 * //delete from server if(delList != null){
					 * //System.out.println("Going to delete files from serever..."); for(int i = 0;
					 * i < delList.length; i++){ //System.out.println("delList[" + i + "] -----> " +
					 * delList[i]); UploadDocument.deleteFile((String)delList[i], "MACHINE"); } }
					 * miscSeqNbr = session.getAttribute("miscSeqNbr");
					 * miscAppEjb.deleteDocument(miscSeqNbr, delList);
					 * 
					 * ArrayList docDetailsList = miscAppEjb.getUploadDocumentDetails(miscSeqNbr);
					 * map.put("docDetailsList", docDetailsList);
					 * 
					 * map.put("status", status); map.put("macType", macType); map.put("fromDate",
					 * fromDate); map.put("fromTime", fromTime); map.put("toDate", toDate);
					 * map.put("toTime", toTime); map.put("remarks", remarks);
					 * 
					 * nextScreen(request, "MiscAppUpdateParkMac"); return;
					 */
					String[] delList =  criteria.getPredicates().get("docCheck").split(",");

					List<MiscAppValueObject> removeTypeList = new ArrayList<MiscAppValueObject>();
					List<String> removeNameList = new ArrayList<String>();
					List<String> removeCdList = new ArrayList<String>();
					List<String> removeUploadDttmList = new ArrayList<String>();
					List<String> removeUploadByList = new ArrayList<String>();

					for (int i = 0; i < delList.length; i++) {
						UploadDocument.deleteFile(FilenameUtils.normalize(folderPath + delList[i]));
						if (docTypeList != null && docTypeList.size() > 0) {
							removeTypeList.add(docTypeList.get(Integer.parseInt(delList[i])));
							removeNameList.add(docNameList.get(Integer.parseInt(delList[i])));
							removeCdList.add(docCdList.get(Integer.parseInt(delList[i])));
							removeUploadDttmList.add(docUploadDttmList.get(Integer.parseInt(delList[i])));
							removeUploadByList.add(docUploadByList.get(Integer.parseInt(delList[i])));
						}
					}
					if (docTypeList != null && docTypeList.size() > 0) {
						for (int i = 0; i < removeTypeList.size(); i++) {
							docTypeList.remove(removeTypeList.get(i));
						}
						for (int i = 0; i < removeNameList.size(); i++) {
							docNameList.remove(removeNameList.get(i));
						}
						for (int i = 0; i < removeCdList.size(); i++) {
							docCdList.remove(removeCdList.get(i));
						}
						for (int i = 0; i < removeUploadDttmList.size(); i++) {
							docUploadDttmList.remove(removeUploadDttmList.get(i));
						}
						for (int i = 0; i < removeUploadByList.size(); i++) {
							docUploadByList.remove(removeUploadByList.get(i));
						}
					}
				} else if ("REGSUBMIT".equals(command)) {
					macType = criteria.getPredicates().get("macType");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					remarks = criteria.getPredicates().get("remarks");
					String regNbr = criteria.getPredicates().get("regNbr");
					// if (regNbrList == null) {
					// regNbrList = new ArrayList();
					// regNbrList.add(regNbr);
					// map.put("regNbrList", regNbrList);
					// } else {
					// regNbrList.add(regNbr);
					// }
					// Added by Punitha on 24/01/2008
					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}
					parkMac = new MiscParkMacValueObject();
					parkMac.setRegNbrValue(regNbr);
					if (macDetList != null) {
						macDetList.add(parkMac);
					}
					map.put("macDetList", macDetList);
					// Ended by Punitha
				} else if ("REGDELETE".equals(command)) {
					String[] delList = CommonUtil.getRequiredStringParameters(request, "regCheck");

					// ArrayList removeList = new ArrayList();
					// for (int i = 0; i < delList.length; i++) {
					// removeList.add(regNbrList.get(Integer.parseInt(delList[i])));
					// }
					// for (int i = 0; i < removeList.size(); i++) {
					// regNbrList.remove(removeList.get(i));
					// }
					// Added by Punitha on 24/01/2008

					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}

					List<MiscParkMacValueObject> macDetListDel = new ArrayList<MiscParkMacValueObject>();
					for (int i = 0; i < delList.length; i++) {
						log.info("Remove Item :" + delList[i]);
						macDetListDel.add((MiscParkMacValueObject) macDetList.get(Integer.parseInt(delList[i])));
					}
					for (int i = 0; i < macDetListDel.size(); i++) {
						macDetList.remove(macDetListDel.get(i));
					}
					map.put("macDetList", macDetList);

					// End
				}
				// String[] regNbrTemp = (String[]) CommonUtil.getRequiredStringParameters(request, "regNbrTemp");
				if (regNbrList != null)
					map.put("regNbrTemp", regNbrList.toArray(new String[0]));
				if (docTypeList != null)
					map.put("docTypeTemp", docTypeList.toArray(new String[0]));
				if (docNameList != null)
					map.put("docNameTemp", docNameList.toArray(new String[0]));
				if (docCdList != null)
					map.put("docCdTemp", docCdList.toArray(new String[0]));
				if (docUploadDttmList != null)
					map.put("docUploadDttmTemp", docUploadDttmList.toArray(new String[0]));
				if (docUploadByList != null)
					map.put("docUploadByTemp", docUploadByList.toArray(new String[0]));

				// System.out.println("^^^^^^^^6... ");
				map.put("status", status);
				map.put("macType", macType);
				map.put("fromDate", fromDate);
				map.put("fromTime", fromTime);
				map.put("toDate", toDate);
				map.put("toTime", toTime);
				map.put("remarks", remarks);
			}
			if ("REGADD".equals(command)) {
				// nextScreen(request, "MiscAppUpdateParkMac");
				// return;
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateParkMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateParkMac : ", e);
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
			log.info("END: miscAppUpdateParkMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	@RequestMapping(value = "/miscAppUpdateParkMac", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppUpdateParkMac(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppUpdateParkMac criteria:" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			// String agree = criteria.getPredicates().get("agree");

			map.put("coCd", coCd);
			// Park Mac Details
			String macType = null;
			String fromDate = null;
			String fromTime = null;
			String toDate = null;
			String toTime = null;
			String remarks = null;
			// String docType = null;
			// String file = null;
			// ArrayList docTypeList = null;
			// ArrayList docNameList = null;
			// ArrayList docCdList = null;
			List<String> docUploadDttmList = null;
			List<String> docUploadByList = null;
			// ArrayList regNbrList = null;
			List<MiscAppValueObject> docTypeList = new ArrayList<MiscAppValueObject>();
			List<String> docNameList = new ArrayList<String>();
			List<String> docCdList = new ArrayList<String>();
			List<String> regNbrList = new ArrayList<String>();

			// Added by Punitha on 28/01/2008
			List<MiscParkMacValueObject> macDetList = new ArrayList<MiscParkMacValueObject>();
			MiscParkMacValueObject parkMac = new MiscParkMacValueObject();
			// End
			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("UPDATE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getParkingOfForkliftShorecrane(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);

				docTypeList = gbMiscApplicationService.getUploadDocumentTypeList();
				map.put("DocTypeList", docTypeList);
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

				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				fromTime = criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate");
				toTime = criteria.getPredicates().get("toTime");
				remarks = criteria.getPredicates().get("remarks");

				if (regNbrList != null)
					map.put("regNbrTemp", regNbrList.toArray(new String[0]));
			} else if ("UPDATE_SUBMIT".equals(command)) {
				miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				status = criteria.getPredicates().get("status");
				coName = criteria.getPredicates().get("coName");
				appType = criteria.getPredicates().get("appTypeCd");
				account = criteria.getPredicates().get("account");
				appStatusCd = criteria.getPredicates().get("appStatusCd");
				// regNbrList = request.getAttribute("regNbrList");
				if (criteria.getPredicates().get("regNbrList") != ""
						&& criteria.getPredicates().get("regNbrList") != null) {
					String[] regNo = criteria.getPredicates().get("regNbrList").split(",");
					for (int i = 0; i < regNo.length; i++) {
						regNbrList.add(regNo[i]);
					}
				}
				// Added by Punitha on 28/01/2008
				Object listObject = null;
				listObject = request.getAttribute("macDetList");
				macDetList = new ArrayList<MiscParkMacValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							macDetList.add((MiscParkMacValueObject) item);
						}
					}
				}

				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				fromTime = criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate");
				toTime = criteria.getPredicates().get("toTime");
				remarks = criteria.getPredicates().get("remarks");

				MiscParkMacValueObject obj = new MiscParkMacValueObject();
				obj.setMacType(macType);
				obj.setFromDate(fromDate);
				obj.setFromTime(fromTime);
				obj.setToDate(toDate);
				obj.setToTime(toTime);
				obj.setRemarks(remarks);

				if (regNbrList != null) {
					obj.setRegNbr((String[]) regNbrList.toArray(new String[0]));
				} 
				if (docTypeList != null) {
					obj.setDocType((String[]) docTypeList.toArray(new String[0]));
				} 
				if (docNameList != null) {
					obj.setDocName((String[]) docNameList.toArray(new String[0]));
				} 

				// Added by Punitha on 28/01/2008
				if (macDetList != null) {
					obj.setMacDetList(macDetList);
				}
				gbMiscApplicationService.updateParkingOfForkliftShorecrane(userId, miscSeqNbr, status, coName, appType,
						account, appStatusCd, obj);

				// remove session attributes here
				// removeSessionAttributes(session);
				// forwardHandler(request, "MiscAppList");
				// return;
			} else if ("CANCEL".equals(command)) {
				// removeSessionAttributes(session);
				// forwardHandler(request, "MiscAppList");
				// return;
			} else {
				// System.out.println("^^^^^^^^1... ");
				status = criteria.getPredicates().get("status");
				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				fromTime = criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate");
				toTime = criteria.getPredicates().get("toTime");
				remarks = criteria.getPredicates().get("remarks");
				// docType = criteria.getPredicates().get("docType");
				// file = criteria.getPredicates().get("file");

				Object listObject = null;
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

				if (docUploadDttmList == null) {
					docUploadDttmList = new ArrayList<String>();
					String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request, "docUploadDttmTemp"));

					if (temp != null && temp.length > 0) {
						for (int i = 0; i < temp.length; i++)
							docUploadDttmList.add(temp[i]);
					}
				}
				if (docUploadByList == null) {
					docUploadByList = new ArrayList<String>();
					String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request, "docUploadByTemp"));

					if (temp != null && temp.length > 0) {
						for (int i = 0; i < temp.length; i++)
							docUploadByList.add(temp[i]);
					}
				}

				if ("UPLOAD".equals(command)) {
					map.put("subDirName", "MACHINE");
					map.put("nextScreen", "MiscAppUpdateParkMac");
				} else if ("DELETE".equals(command)) {
					String[] delList =  criteria.getPredicates().get("docCheck").split(",");

					List<MiscAppValueObject> removeTypeList = new ArrayList<MiscAppValueObject>();
					List<String> removeNameList = new ArrayList<String>();
					List<String> removeCdList = new ArrayList<String>();
					List<String> removeUploadDttmList = new ArrayList<String>();
					List<String> removeUploadByList = new ArrayList<String>();

					for (int i = 0; i < delList.length; i++) {
						UploadDocument.deleteFile(FilenameUtils.normalize(folderPath + delList[i]));
						if (docTypeList != null && docTypeList.size() > 0) {
							removeTypeList.add(docTypeList.get(Integer.parseInt(delList[i])));
							removeNameList.add(docNameList.get(Integer.parseInt(delList[i])));
							removeCdList.add(docCdList.get(Integer.parseInt(delList[i])));
							removeUploadDttmList.add(docUploadDttmList.get(Integer.parseInt(delList[i])));
							removeUploadByList.add(docUploadByList.get(Integer.parseInt(delList[i])));
						}
					}
					if (docTypeList != null && docTypeList.size() > 0) {
						for (int i = 0; i < removeTypeList.size(); i++) {
							docTypeList.remove(removeTypeList.get(i));
						}
						for (int i = 0; i < removeNameList.size(); i++) {
							docNameList.remove(removeNameList.get(i));
						}
						for (int i = 0; i < removeCdList.size(); i++) {
							docCdList.remove(removeCdList.get(i));
						}
						for (int i = 0; i < removeUploadDttmList.size(); i++) {
							docUploadDttmList.remove(removeUploadDttmList.get(i));
						}
						for (int i = 0; i < removeUploadByList.size(); i++) {
							docUploadByList.remove(removeUploadByList.get(i));
						}
					}
				} else if ("REGSUBMIT".equals(command)) {
					macType = criteria.getPredicates().get("macType");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					remarks = criteria.getPredicates().get("remarks");
					String regNbr = criteria.getPredicates().get("regNbr");
					// if (regNbrList == null) {
					// regNbrList = new ArrayList();
					// regNbrList.add(regNbr);
					// map.put("regNbrList", regNbrList);
					// } else {
					// regNbrList.add(regNbr);
					// }
					// Added by Punitha on 24/01/2008

					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}
					parkMac = new MiscParkMacValueObject();
					parkMac.setRegNbrValue(regNbr);
					if (macDetList != null) {
						macDetList.add(parkMac);
					}
					map.put("macDetList", macDetList);
					// Ended by Punitha
				} else if ("REGDELETE".equals(command)) {
					String[] delList = CommonUtil.getRequiredStringParameters(request, "regCheck");

					// ArrayList removeList = new ArrayList();
					// for (int i = 0; i < delList.length; i++) {
					// removeList.add(regNbrList.get(Integer.parseInt(delList[i])));
					// }
					// for (int i = 0; i < removeList.size(); i++) {
					// regNbrList.remove(removeList.get(i));
					// }
					// Added by Punitha on 24/01/2008
					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}
					List<MiscParkMacValueObject> macDetListDel = new ArrayList<MiscParkMacValueObject>();
					for (int i = 0; i < delList.length; i++) {
						log.info("Remove Item :" + delList[i]);
						macDetListDel.add((MiscParkMacValueObject) macDetList.get(Integer.parseInt(delList[i])));
					}
					for (int i = 0; i < macDetListDel.size(); i++) {
						macDetList.remove(macDetListDel.get(i));
					}
					map.put("macDetList", macDetList);

					// End
				}
				// String[] regNbrTemp = (String[]) CommonUtil.getRequiredStringParameters(request, "regNbrTemp");
				if (regNbrList != null)
					map.put("regNbrTemp", regNbrList.toArray(new String[0]));
				if (docTypeList != null)
					map.put("docTypeTemp", docTypeList.toArray(new String[0]));
				if (docNameList != null)
					map.put("docNameTemp", docNameList.toArray(new String[0]));
				if (docCdList != null)
					map.put("docCdTemp", docCdList.toArray(new String[0]));
				if (docUploadDttmList != null)
					map.put("docUploadDttmTemp", docUploadDttmList.toArray(new String[0]));
				if (docUploadByList != null)
					map.put("docUploadByTemp", docUploadByList.toArray(new String[0]));

				map.put("status", status);
				map.put("macType", macType);
				map.put("fromDate", fromDate);
				map.put("fromTime", fromTime);
				map.put("toDate", toDate);
				map.put("toTime", toTime);
				map.put("remarks", remarks);
			}
			if ("REGADD".equals(command)) {
				// nextScreen(request, "MiscAppUpdateParkMac");
				// return;
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateParkMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateParkMac : ", e);
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
			log.info("END: miscAppUpdateParkMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateSpaceHandler -->perform
	public ResponseEntity<?> miscAppUpdateSpace(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppUpdateSpace criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Space Details
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

			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;
			miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			coName = criteria.getPredicates().get("coName");
			appType = criteria.getPredicates().get("appTypeCd");
			account = criteria.getPredicates().get("account");
			appStatusCd = criteria.getPredicates().get("appStatusCd");

			if ("UPDATE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

				List<Object> list = gbMiscApplicationService.getUseOfSpaceDetails(userId, applyType, appSeqNbr,
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

				List<MiscAppValueObject> purposeList = gbMiscApplicationService.getPurposeList();
				map.put("PurposeList", purposeList);
			} else if ("UPDATE_SUBMIT".equals(command)) {

				if (agree == null) {
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

					if ("D".equals(status) || !("D".equals(appStatusCd))) {
						MiscSpaceValueObject obj = new MiscSpaceValueObject();
						obj.setSpaceType(spaceType);
						obj.setPurpose(purpose);
						obj.setFromDate(fromDate);
						obj.setFromTime(fromTime);
						obj.setToDate(toDate);
						obj.setToTime(toTime);
						obj.setReason(reason);
						obj.setBillNbr(billNbr);
						obj.setMarks(marks);
						obj.setPackages(packages);
						obj.setCargoDesc(cargoDesc);
						obj.setTonnage(tonnage);
						obj.setNewMarks(newMarks);
						obj.setNewPackages(newPackages);
						obj.setNewCargoDesc(newCargoDesc);
						obj.setNewTonnage(newTonnage);

						/*
						 * miscAppEjb.updateUseOfSpaceDetails(userId, miscSeqNbr, status, coName,
						 * appType, account, appStatusCd, obj);
						 */
						// Amended 0n 08/06/2007 by Punitha. To add Contact person and Contact Tel
						gbMiscApplicationService.updateUseOfSpaceDetails(userId, miscSeqNbr, status, coName, appType,
								account, appStatusCd, obj, conPerson, conTel);
						// Ended by Punitha
						// Start added for SMART CR by FPT on 25-Feb-2014:
						// Fetch valid plan from SMART system.
						String appRefNbr = criteria.getPredicates().get("appRefNbr");

						String newFromDate = criteria.getPredicates().get("fromDate");
						String oldFromDate = criteria.getPredicates().get("oldFromDate");
						String newFromTime = criteria.getPredicates().get("fromTime");
						String oldFromTime = criteria.getPredicates().get("oldFromTime");

						String newToDate = criteria.getPredicates().get("toDate");
						String oldToDate = criteria.getPredicates().get("oldToDate");
						String newToTime = criteria.getPredicates().get("toTime");
						String oldToTime = criteria.getPredicates().get("oldToTime");

						boolean isFromChange = false;
						boolean isToChange = false;

						log.info("Misc Application - From Date/Time. Previous: " + oldFromDate + " " + oldFromTime
								+ " - New : " + newFromDate + " " + newFromTime);
						log.info("Misc Application - To Date/Time. Previous: " + oldToDate + " " + oldToTime
								+ " - New : " + newToDate + " " + newToTime);

						if ((StringUtils.isNotBlank(newFromDate) || StringUtils.isNotBlank(oldFromDate))
								&& !StringUtils.equals(newFromDate + newFromTime, oldFromDate + oldFromTime)) {
							isFromChange = true;
							log.info("Misc Application - From Date/Time has changed.");
						}
						if ((StringUtils.isNotBlank(newToDate) || StringUtils.isNotBlank(oldToDate))
								&& !StringUtils.equals(newToDate + newToTime, oldToDate + oldToTime)) {
							isToChange = true;
							log.info("Misc Application - To Date/Time has changed.");
						}
						boolean hasValidPlan = false;
						if (isFromChange || isToChange) {
							log.info("***********Before SMART interface calling for fetch valid plan for Use of Space application: application reference number: " + appRefNbr);
							try {			
							     String url = smartBaseUrl + "/checkValidUseOfSpacePlan" + "?userId=" + userId + "&refNbr=" + appRefNbr + "&isInactivate=" + false;
								 log.info("[checkValidUseOfSpacePlan] Calling SMART service URL= " + url);
								 hasValidPlan = smartServiceRestClient.checkValidUseOfSpacePlan(userId, appRefNbr,false);
								log.info("Update application function got valid plan(s): " + hasValidPlan);
							} catch (Exception ex) {
								log.info("Call SMART Interface to fetch valid plan is not successfully: Application reference number = " + appRefNbr);
								log.info("Exception amendHSCode : ", ex);
							}
							log.info("***********After SMART interface calling");
						}

						if (hasValidPlan) {
							// Send email for JP staff: emailMessage, emailSubject get from email template.
							String sender = SpaceAppUpdated_from;
							String emailSubject = SpaceAppUpdated_subject;
							String fileNameSpaceUpdate = SpaceAppUpdated_body_template;
							String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), fileNameSpaceUpdate);
							Map<String, String> emailInputData = new HashMap<String, String>();
							
							emailSubject = StringUtils.replace(emailSubject, "<ApplicationNumber>", appRefNbr);
							emailInputData.put("ApplicationNumber", appRefNbr);
							emailInputData.put("Updater", userId);
							emailInputData.put("TimeofUpdating", getCurrentDate());
							emailInputData.put("PreviousFromDateTime",
									(StringUtils.isNotBlank(oldFromDate) && StringUtils.isNotBlank(oldFromTime))
											? oldFromDate + " " + oldFromTime
											: "");
							emailInputData.put("NewFromDateTime",
									(StringUtils.isNotBlank(newFromDate) && StringUtils.isNotBlank(newFromTime))
											? newFromDate + " " + newFromTime
											: "");
							emailInputData.put("PreviousToDateTime",
									(StringUtils.isNotBlank(oldToDate) && StringUtils.isNotBlank(oldToTime))
											? oldToDate + " " + oldToTime
											: "");
							emailInputData.put("NewToDateTime",
									(StringUtils.isNotBlank(newToDate) && StringUtils.isNotBlank(newToTime))
											? newToDate + " " + newToTime
											: "");

							String emailMessage = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
							
							log.info("Space App update, send email: email subject=\n" + emailSubject);
							log.info("Space App update, send email: email message=\n" + emailMessage);
							gbMiscApplicationService.sendFlexiAlert("MSU", "", emailMessage, emailSubject, sender,
									null);
						}
						// End added for SMART CR by FPT on 25-Feb-2014
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("status", criteria.getPredicates().get("status"));
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

					MiscSpaceValueObject obj = new MiscSpaceValueObject();
					obj.setSpaceType(spaceType);
					obj.setPurpose(purpose);
					obj.setFromDate(fromDate);
					obj.setFromTime(fromTime);
					obj.setToDate(toDate);
					obj.setToTime(toTime);
					obj.setReason(reason);
					obj.setBillNbr(billNbr);
					obj.setMarks(marks);
					obj.setPackages(packages);
					obj.setCargoDesc(cargoDesc);
					obj.setTonnage(tonnage);
					obj.setNewMarks(newMarks);
					obj.setNewPackages(newPackages);
					obj.setNewCargoDesc(newCargoDesc);
					obj.setNewTonnage(newTonnage);

					/*
					 * miscAppEjb.updateUseOfSpaceDetails(userId, miscSeqNbr, status, coName,
					 * appType, account, appStatusCd, obj);
					 */
					// Amended on 08/06/2007 by Punitha. To ad Contact Person and Contact Tel
					gbMiscApplicationService.updateUseOfSpaceDetails(userId, miscSeqNbr, status, coName, appType,
							account, appStatusCd, obj, conPerson, conTel);
					// Ended by Punitha

					// remove session attributes here
					map.remove("coName");
					map.remove("phone");
					map.remove("add1");
					map.remove("add2");
					map.remove("city");
					map.remove("pin");
					map.remove("account");

					// Application Details
					map.remove("miscSeqNbr");
					map.remove("appTypeCd");
					map.remove("appTypeName");
					map.remove("appStatusCd");
					map.remove("appStatusNm");
					map.remove("appDttm");
					map.remove("submitDttm");
					map.remove("submitBy");
					map.remove("approveDttm");
					map.remove("approveBy");
					map.remove("closeDttm");
					map.remove("closeBy");
					map.remove("remarks");
					map.remove("status");
					// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("conPerson");
					map.remove("conTel");
					// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
					// Space Details
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

					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}
		} catch (BusinessException e) {
			log.info("Exception amendHSCode : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception amendHSCode : ", e);
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
			log.info("END: miscAppUpdateSpace result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateSpaceHandler -->perform
	@RequestMapping(value = "/miscAppUpdateSpace", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppUpdateSpace(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppUpdateSpace criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Space Details
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

			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;
			miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			coName = criteria.getPredicates().get("coName");
			appType = criteria.getPredicates().get("appTypeCd");
			account = criteria.getPredicates().get("account");
			appStatusCd = criteria.getPredicates().get("appStatusCd");

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

			if ("UPDATE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getUseOfSpaceDetails(userId, applyType, appSeqNbr,
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

				List<MiscAppValueObject> purposeList = gbMiscApplicationService.getPurposeList();
				map.put("PurposeList", purposeList);
			} else if ("UPDATE_SUBMIT".equals(command)) {

				if (agree == null) {
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

					if ("D".equals(status) || !("D".equals(appStatusCd))) {
						MiscSpaceValueObject obj = new MiscSpaceValueObject();
						obj.setSpaceType(spaceType);
						obj.setPurpose(purpose);
						obj.setFromDate(fromDate);
						obj.setFromTime(fromTime);
						obj.setToDate(toDate);
						obj.setToTime(toTime);
						obj.setReason(reason);
						obj.setBillNbr(billNbr);
						obj.setMarks(marks);
						obj.setPackages(packages);
						obj.setCargoDesc(cargoDesc);
						obj.setTonnage(tonnage);
						obj.setNewMarks(newMarks);
						obj.setNewPackages(newPackages);
						obj.setNewCargoDesc(newCargoDesc);
						obj.setNewTonnage(newTonnage);

						/*
						 * miscAppEjb.updateUseOfSpaceDetails(userId, miscSeqNbr, status, coName,
						 * appType, account, appStatusCd, obj);
						 */
						// Amended 0n 08/06/2007 by Punitha. To add Contact person and Contact Tel
						gbMiscApplicationService.updateUseOfSpaceDetails(userId, miscSeqNbr, status, coName, appType,
								account, appStatusCd, obj, conPerson, conTel);
						// Ended by Punitha
						// Start added for SMART CR by FPT on 25-Feb-2014:
						// Fetch valid plan from SMART system.
						String appRefNbr = criteria.getPredicates().get("appRefNbr");

						String newFromDate = criteria.getPredicates().get("fromDate");
						String oldFromDate = criteria.getPredicates().get("oldFromDate");
						String newFromTime = criteria.getPredicates().get("fromTime");
						String oldFromTime = criteria.getPredicates().get("oldFromTime");

						String newToDate = criteria.getPredicates().get("toDate");
						String oldToDate = criteria.getPredicates().get("oldToDate");
						String newToTime = criteria.getPredicates().get("toTime");
						String oldToTime = criteria.getPredicates().get("oldToTime");

						boolean isFromChange = false;
						boolean isToChange = false;

						log.info("Misc Application - From Date/Time. Previous: " + oldFromDate + " " + oldFromTime
								+ " - New : " + newFromDate + " " + newFromTime);
						log.info("Misc Application - To Date/Time. Previous: " + oldToDate + " " + oldToTime
								+ " - New : " + newToDate + " " + newToTime);

						if ((StringUtils.isNotBlank(newFromDate) || StringUtils.isNotBlank(oldFromDate))
								&& !StringUtils.equals(newFromDate + newFromTime, oldFromDate + oldFromTime)) {
							isFromChange = true;
							log.info("Misc Application - From Date/Time has changed.");
						}
						if ((StringUtils.isNotBlank(newToDate) || StringUtils.isNotBlank(oldToDate))
								&& !StringUtils.equals(newToDate + newToTime, oldToDate + oldToTime)) {
							isToChange = true;
							log.info("Misc Application - To Date/Time has changed.");
						}
						boolean hasValidPlan = false;
						if (isFromChange || isToChange) {
							log.info("***********Before SMART interface calling for fetch valid plan for Use of Space application: application reference number: " + appRefNbr);
							try {
								 String url = smartBaseUrl + "/checkValidUseOfSpacePlan" + "?userId=" + userId + "&refNbr=" + appRefNbr + "&isInactivate=" + false;
								 log.info("[checkValidUseOfSpacePlan] Calling SMART service URL= " + url);
								 hasValidPlan = smartServiceRestClient.checkValidUseOfSpacePlan(userId, appRefNbr,false);
								log.info("Update application function ï¿½ got valid plan(s): " + hasValidPlan);
							} catch (Exception ex) {
								log.info("Call SMART Interface to fetch valid plan is not successfully: Application reference number = " + appRefNbr);
								log.info("Exception miscAppUpdateSpace : ", ex);
							}
							log.info("***********After SMART interface calling");
						}

						if (hasValidPlan) {
							// Send email for JP staff: emailMessage, emailSubject get from email template.
							String sender = SpaceAppUpdated_from;
							String emailSubject = SpaceAppUpdated_subject;
							String fileNameSpaceUpdate = SpaceAppUpdated_body_template;
							String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), fileNameSpaceUpdate);
							Map<String, String> emailInputData = new HashMap<String, String>();
							
							emailSubject = StringUtils.replace(emailSubject, "<ApplicationNumber>", appRefNbr);
							emailInputData.put("ApplicationNumber", appRefNbr);
							emailInputData.put("Updater", userId);
							emailInputData.put("TimeofUpdating", getCurrentDate());
							emailInputData.put("PreviousFromDateTime",
									(StringUtils.isNotBlank(oldFromDate) && StringUtils.isNotBlank(oldFromTime))
											? oldFromDate + " " + oldFromTime
											: "");
							emailInputData.put("NewFromDateTime",
									(StringUtils.isNotBlank(newFromDate) && StringUtils.isNotBlank(newFromTime))
											? newFromDate + " " + newFromTime
											: "");
							emailInputData.put("PreviousToDateTime",
									(StringUtils.isNotBlank(oldToDate) && StringUtils.isNotBlank(oldToTime))
											? oldToDate + " " + oldToTime
											: "");
							emailInputData.put("NewToDateTime",
									(StringUtils.isNotBlank(newToDate) && StringUtils.isNotBlank(newToTime))
											? newToDate + " " + newToTime
											: "");

							String emailMessage = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);

							log.info("Space App update, send email: email subject=\n" + emailSubject);
							log.info("Space App update, send email: email message=\n" + emailMessage);
							gbMiscApplicationService.sendFlexiAlert("MSU", "", emailMessage, emailSubject, sender,
									null);
						}
						// End added for SMART CR by FPT on 25-Feb-2014
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("status", criteria.getPredicates().get("status"));
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

					MiscSpaceValueObject obj = new MiscSpaceValueObject();
					obj.setSpaceType(spaceType);
					obj.setPurpose(purpose);
					obj.setFromDate(fromDate);
					obj.setFromTime(fromTime);
					obj.setToDate(toDate);
					obj.setToTime(toTime);
					obj.setReason(reason);
					obj.setBillNbr(billNbr);
					obj.setMarks(marks);
					obj.setPackages(packages);
					obj.setCargoDesc(cargoDesc);
					obj.setTonnage(tonnage);
					obj.setNewMarks(newMarks);
					obj.setNewPackages(newPackages);
					obj.setNewCargoDesc(newCargoDesc);
					obj.setNewTonnage(newTonnage);

					/*
					 * miscAppEjb.updateUseOfSpaceDetails(userId, miscSeqNbr, status, coName,
					 * appType, account, appStatusCd, obj);
					 */
					// Amended on 08/06/2007 by Punitha. To ad Contact Person and Contact Tel
					gbMiscApplicationService.updateUseOfSpaceDetails(userId, miscSeqNbr, status, coName, appType,
							account, appStatusCd, obj, conPerson, conTel);
					// Ended by Punitha

					// remove session attributes here
					map.remove("coName");
					map.remove("phone");
					map.remove("add1");
					map.remove("add2");
					map.remove("city");
					map.remove("pin");
					map.remove("account");

					// Application Details
					map.remove("miscSeqNbr");
					map.remove("appTypeCd");
					map.remove("appTypeName");
					map.remove("appStatusCd");
					map.remove("appStatusNm");
					map.remove("appDttm");
					map.remove("submitDttm");
					map.remove("submitBy");
					map.remove("approveDttm");
					map.remove("approveBy");
					map.remove("closeDttm");
					map.remove("closeBy");
					map.remove("remarks");
					map.remove("status");
					// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("conPerson");
					map.remove("conTel");
					// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
					// Space Details
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

					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}
		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateSpace : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateSpace : ", e);
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
			log.info("END: miscAppUpdateSpace result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateSpreaderHandler -->perform

	public ResponseEntity<?> miscAppUpdateSpreader(HttpServletRequest request, Criteria criteria,
			Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppUpdateSpreader criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Spreader Details
			String spreaderType = null;
			String fromDate = null;
			String fromTime = null;
			String toDate = null;
			String toTime = null;
			String remarks = null;

			String status = null;

			String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			String coName = criteria.getPredicates().get("coName");
			String appType = criteria.getPredicates().get("appTypeCd");
			String account = criteria.getPredicates().get("account");
			String appStatusCd = criteria.getPredicates().get("appStatusCd");

			if ("UPDATE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

				List<Object> list = gbMiscApplicationService.getSpreaderDetails(userId, applyType, appSeqNbr,
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
			} else if ("UPDATE_SUBMIT".equals(command)) {
				if (agree == null) {
					status = criteria.getPredicates().get("status");
					spreaderType = criteria.getPredicates().get("spreaderType");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					remarks = criteria.getPredicates().get("remarks");

					if ("D".equals(status) || !("D".equals(appStatusCd))) {
						MiscSpreaderValueObject obj = new MiscSpreaderValueObject();
						obj.setSpreaderType(spreaderType);
						obj.setFromDate(fromDate);
						obj.setFromTime(fromTime);
						obj.setToDate(toDate);
						obj.setToTime(toTime);
						obj.setRemarks(remarks);

						boolean successBooking = gbMiscApplicationService.updateSpreaderDetails(userId, miscSeqNbr,
								status, coName, appType, account, appStatusCd, obj);
						if (!successBooking) {
							errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M22224");
						}
						// else{
						// forwardHandler(request, "MiscAppList");
						// return;
						// }

					} else {
						map.put("status", criteria.getPredicates().get("status"));
						map.put("spreaderType", spreaderType);
						map.put("fromDate", fromDate);
						map.put("fromTime", fromTime);
						map.put("toDate", toDate);
						map.put("toTime", toTime);
						map.put("remarks", remarks);
					}
				} else if (agree != null && agree.equals("true")) {
					status = criteria.getPredicates().get("status");
					spreaderType = criteria.getPredicates().get("spreaderType");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					remarks = criteria.getPredicates().get("remarks");

					MiscSpreaderValueObject obj = new MiscSpreaderValueObject();
					obj.setSpreaderType(spreaderType);
					obj.setFromDate(fromDate);
					obj.setFromTime(fromTime);
					obj.setToDate(toDate);
					obj.setToTime(toTime);
					obj.setRemarks(remarks);

					boolean successBooking = gbMiscApplicationService.updateSpreaderDetails(userId, miscSeqNbr, status,
							coName, appType, account, appStatusCd, obj);

					if (!successBooking) {
						errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M22224");
					} else {
						// remove session attributes here
						map.remove("coName");
						map.remove("phone");
						map.remove("add1");
						map.remove("add2");
						map.remove("city");
						map.remove("pin");
						map.remove("account");

						// Application Details
						map.remove("miscSeqNbr");
						map.remove("appTypeCd");
						map.remove("appTypeName");
						map.remove("appStatusCd");
						map.remove("appStatusNm");
						map.remove("appDttm");
						map.remove("submitDttm");
						map.remove("submitBy");
						map.remove("approveDttm");
						map.remove("approveBy");
						map.remove("closeDttm");
						map.remove("closeBy");
						map.remove("remarks");
						map.remove("status");
						// Space Details
						map.remove("spreaderType");
						map.remove("fromDate");
						map.remove("fromTime");
						map.remove("toDate");
						map.remove("toTime");
						map.remove("remarks");

						// forwardHandler(request, "MiscAppList");
						// return;
					}
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateSpreader : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateSpreader : ", e);
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
			log.info("END: miscAppUpdateSpreader result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	@RequestMapping(value = "/miscAppUpdateSpreader", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppUpdateSpreader(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppUpdateSpreader criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Spreader Details
			String spreaderType = null;
			String fromDate = null;
			String fromTime = null;
			String toDate = null;
			String toTime = null;
			String remarks = null;

			String status = null;

			String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			String coName = criteria.getPredicates().get("coName");
			String appType = criteria.getPredicates().get("applyType");
			String account = criteria.getPredicates().get("account");
			String appStatusCd = criteria.getPredicates().get("appStatusCd");
			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

			if ("UPDATE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getSpreaderDetails(userId, applyType, appSeqNbr,
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
			} else if ("UPDATE_SUBMIT".equals(command)) {
				if (agree == null) {
					status = criteria.getPredicates().get("status");
					spreaderType = criteria.getPredicates().get("spreaderType");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					remarks = criteria.getPredicates().get("remarks");

					if ("D".equals(status) || !("D".equals(appStatusCd))) {
						MiscSpreaderValueObject obj = new MiscSpreaderValueObject();
						obj.setSpreaderType(spreaderType);
						obj.setFromDate(fromDate);
						obj.setFromTime(fromTime);
						obj.setToDate(toDate);
						obj.setToTime(toTime);
						obj.setRemarks(remarks);

						boolean successBooking = gbMiscApplicationService.updateSpreaderDetails(userId, miscSeqNbr,
								status, coName, appType, account, appStatusCd, obj);
						if (!successBooking) {
							errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M22224");
						}
						// else{
						// forwardHandler(request, "MiscAppList");
						// return;
						// }

					} else {
						map.put("status", criteria.getPredicates().get("status"));
						map.put("spreaderType", spreaderType);
						map.put("fromDate", fromDate);
						map.put("fromTime", fromTime);
						map.put("toDate", toDate);
						map.put("toTime", toTime);
						map.put("remarks", remarks);
					}
				} else if (agree != null && agree.equals("true")) {
					status = criteria.getPredicates().get("status");
					spreaderType = criteria.getPredicates().get("spreaderType");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					remarks = criteria.getPredicates().get("remarks");

					MiscSpreaderValueObject obj = new MiscSpreaderValueObject();
					obj.setSpreaderType(spreaderType);
					obj.setFromDate(fromDate);
					obj.setFromTime(fromTime);
					obj.setToDate(toDate);
					obj.setToTime(toTime);
					obj.setRemarks(remarks);

					boolean successBooking = gbMiscApplicationService.updateSpreaderDetails(userId, miscSeqNbr, status,
							coName, appType, account, appStatusCd, obj);

					if (!successBooking) {
						errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M22224");
					} else {
						// remove session attributes here
						map.remove("coName");
						map.remove("phone");
						map.remove("add1");
						map.remove("add2");
						map.remove("city");
						map.remove("pin");
						map.remove("account");

						// Application Details
						map.remove("miscSeqNbr");
						map.remove("appTypeCd");
						map.remove("appTypeName");
						map.remove("appStatusCd");
						map.remove("appStatusNm");
						map.remove("appDttm");
						map.remove("submitDttm");
						map.remove("submitBy");
						map.remove("approveDttm");
						map.remove("approveBy");
						map.remove("closeDttm");
						map.remove("closeBy");
						map.remove("remarks");
						map.remove("status");
						// Space Details
						map.remove("spreaderType");
						map.remove("fromDate");
						map.remove("fromTime");
						map.remove("toDate");
						map.remove("toTime");
						map.remove("remarks");

						// forwardHandler(request, "MiscAppList");
						// return;
					}
				}
			}

		} catch (BusinessException e) {
			log.info("Exception amendHSCode : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception amendHSCode : ", e);
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
			log.info("END: miscAppUpdateSpreader result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateStationMacHandler -->perform
	public ResponseEntity<?> miscAppUpdateStationMac(HttpServletRequest request, Criteria criteria,
			Map<String, Object> map) throws BusinessException {

		// Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		// Map<String, Object> map = new HashMap<>();

		try {
			log.info("START: miscAppUpdateStationMac criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			// String agree = criteria.getPredicates().get("agree");

			map.put("coCd", coCd);
			// Station Mac Details
			String macType = null;
			String fromDate = null;
			String toDate = null;
			// String docType = null;
			// String file = null;
			List<MiscAppValueObject> docTypeList = null;
			List<String> docNameList = null;
			List<String> docCdList = null;
			List<String> docUploadDttmList = null;
			List<String> docUploadByList = null;
			List<String> regNbrList = null;
			List<String> liftCapacityList = null;
			List<String> insuranceNbrList = null;
			List<String> insExpDttmList = null;
			List<String> phaseOutDtList = null;
			//
			// Added by Punitha on 24/01/2008
			List<MiscParkMacValueObject> macDetList = null;
			MiscParkMacValueObject parkMac = new MiscParkMacValueObject();
			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;

			if ("UPDATE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

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

				docTypeList = gbMiscApplicationService.getUploadDocumentTypeList();
				map.put("docTypeList", docTypeList);
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

				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");
			} else if ("UPDATE_SUBMIT".equals(command)) {
				miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				status = criteria.getPredicates().get("status");
				coName = criteria.getPredicates().get("coName");
				appType = criteria.getPredicates().get("appTypeCd");
				account = criteria.getPredicates().get("account");
				appStatusCd = criteria.getPredicates().get("appStatusCd");

				// docTypeList = session.getAttribute("docTypeList");
				// docNameList = session.getAttribute("docNameList");
				// regNbrList = request.getAttribute("regNbrList");
				// liftCapacityList = request.getAttribute("liftCapacityList");
				// insuranceNbrList = request.getAttribute("insuranceNbrList");
				// insExpDttmList = request.getAttribute("insExpDttmList");
				// phaseOutDtList = request.getAttribute("phaseOutDtList");

				// Added by Punitha on 24/01/2008
				Object listObject = null;
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
				listObject = request.getAttribute("phaseOutDtList");
				phaseOutDtList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							phaseOutDtList.add((String) item);
						}
					}
				}

				listObject = request.getAttribute("macDetList");
				macDetList = new ArrayList<MiscParkMacValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							macDetList.add((MiscParkMacValueObject) item);
						}
					}
				}

				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");

				MiscParkMacValueObject obj = new MiscParkMacValueObject();
				obj.setMacType(macType);
				obj.setFromDate(fromDate);
				obj.setToDate(toDate);

				if (docTypeList == null) {
					obj.setDocType((String[]) CommonUtil.getRequiredStringParameters(request, "docTypeTemp"));
				} 
				if (docNameList == null) {
					obj.setDocName((String[]) CommonUtil.getRequiredStringParameters(request, "docNameTemp"));

				} 
				// Amended by Punitha on 24/01/2008
				/*
				 * if(regNbrList == null){ obj.setRegNbr((String[])
				 * session.getAttribute("regNbr")); }else{ obj.setRegNbr((String[])
				 * regNbrList.toArray(new String[0])); } if(liftCapacityList == null){
				 * obj.setLiftCapacity((String[]) session.getAttribute("liftCapacity")); }else{
				 * obj.setLiftCapacity((String[]) liftCapacityList.toArray(new String[0])); }
				 * if(insuranceNbrList == null){ obj.setInsuranceNbr((String[])
				 * session.getAttribute("insuranceNbr")); }else{ obj.setInsuranceNbr((String[])
				 * insuranceNbrList.toArray(new String[0])); } if(insExpDttmList == null){
				 * obj.setInsExpDttm((String[]) session.getAttribute("insExpDttm")); }else{
				 * obj.setInsExpDttm((String[]) insExpDttmList.toArray(new String[0])); }
				 * if("JP".equals(coCd)){ if(phaseOutDtList == null){
				 * obj.setPhaseOutDt((String[]) session.getAttribute("phaseOutDt")); }else{
				 * obj.setPhaseOutDt((String[]) phaseOutDtList.toArray(new String[0])); } }else{
				 * obj.setPhaseOutDt(null); }
				 */
				// End by Punitha
				// //Added by Punitha on 24/01/2008

				if (macDetList != null) {
					obj.setMacDetList(macDetList);
				}
				// //End by Punitha

				gbMiscApplicationService.updateStationingOfMacDetails(userId, miscSeqNbr, status, coName, appType,
						account, appStatusCd, obj);

				/*
				 * System.out.println("assignFileName -----> " + assignFileName); //write file
				 * into server byte[] fileContent = null; String[] fileName = obj.getDocName();
				 * if(docNameList != null && assignFileName != null){
				 * //System.out.println("assignFileName -----> " + assignFileName.size());
				 * for(int i = 0; i < docNameList.size(); i++){ if(fileName[i] != null &&
				 * fileName[i].indexOf("\\") != -1){ fileContent =
				 * UploadDocument.getFileContent((String)docNameList.get(i));
				 * UploadDocument.writeToFile(fileContent, (String)assignFileName.get(i),
				 * "MACHINE"); } } }
				 */

				// remove session attributes here
				// removeSessionAttributes(session);
				// forwardHandler(request, "MiscAppList");
				// return;
			} else if ("CANCEL".equals(command)) {
				// removeSessionAttributes(session);
				// forwardHandler(request, "MiscAppList");
				// return;
			} else {
				status = criteria.getPredicates().get("status");
				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");
				// docType = criteria.getPredicates().get("docType");
				// file = criteria.getPredicates().get("file");
				
				// docTypeList = session.getAttribute("docTypeList");
				// docNameList = session.getAttribute("docNameList");
				// regNbrList = session.getAttribute("regNbrList");
				// liftCapacityList = session.getAttribute("liftCapacityList");
				// insuranceNbrList = session.getAttribute("insuranceNbrList");
				// insExpDttmList = session.getAttribute("insExpDttmList");
				// phaseOutDtList = session.getAttribute("phaseOutDtList");

				/*
				 * if(docTypeList == null){ //System.out.println("^^^^^^^^2... docTypeList " +
				 * ((String[] )session.getAttribute("docType")).length); docTypeList = new
				 * ArrayList(); String temp[] = (String[] )session.getAttribute("docType");
				 * for(int i=0; i< temp.length; i++) docTypeList.add(temp[i]); } if(docNameList
				 * == null){ docNameList = new ArrayList(); String temp[] = (String[]
				 * )session.getAttribute("docName"); for(int i=0; i< temp.length; i++)
				 * docNameList.add(temp[i]); }
				 */
				// if(regNbrList == null){
				// regNbrList = new ArrayList();
				// String temp[] = (String[] )session.getAttribute("regNbr");
				// for(int i=0; i< temp.length; i++)
				// regNbrList.add(temp[i]);
				// //System.out.println("^^^^^^^^2... regNbrList " + regNbrList.size());
				// }
				// if(liftCapacityList == null){
				// liftCapacityList = new ArrayList();
				// String temp[] = (String[] )session.getAttribute("liftCapacity");
				// for(int i=0; i< temp.length; i++)
				// liftCapacityList.add(temp[i]);
				// //System.out.println("^^^^^^^^2... liftCapacityList " +
				// liftCapacityList.size());
				// }
				// if(insuranceNbrList == null){
				// insuranceNbrList = new ArrayList();
				// String temp[] = (String[] )session.getAttribute("insuranceNbr");
				// for(int i=0; i< temp.length; i++)
				// insuranceNbrList.add(temp[i]);
				// //System.out.println("^^^^^^^^2... insuranceNbrList " +
				// insuranceNbrList.size());
				// }
				// if(insExpDttmList == null){
				// insExpDttmList = new ArrayList();
				// String temp[] = (String[] )session.getAttribute("insExpDttm");
				// for(int i=0; i< temp.length; i++)
				// insExpDttmList.add(temp[i]);
				// //System.out.println("^^^^^^^^2... insExpDttmList " + insExpDttmList.size());
				// }
				// if(phaseOutDtList == null){
				// phaseOutDtList = new ArrayList();
				// String temp[] = (String[] )session.getAttribute("phaseOutDt");
				// if(temp != null){
				// for(int i=0; i< temp.length; i++)
				// phaseOutDtList.add(temp[i]);
				// }
				// }
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

				if (docUploadDttmList == null) {
					docUploadDttmList = new ArrayList<String>();
					String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request, "docUploadDttmTemp"));

					if (temp != null && temp.length > 0) {
						for (int i = 0; i < temp.length; i++)
							docUploadDttmList.add(temp[i]);
					}
				}
				if (docUploadByList == null) {
					docUploadByList = new ArrayList<String>();
					String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request, "docUploadByTemp"));

					if (temp != null && temp.length > 0) {
						for (int i = 0; i < temp.length; i++)
							docUploadByList.add(temp[i]);
					}
				}

				if ("UPLOAD".equals(command)) {
					map.put("subDirName", "MACHINE");
					map.put("nextScreen", "MiscAppUpdateStationMac");
				} else if ("DELETE".equals(command)) {
					String[] delList =  criteria.getPredicates().get("docCheck").split(",");

					List<MiscAppValueObject> removeTypeList = new ArrayList<MiscAppValueObject>();
					List<String> removeNameList = new ArrayList<String>();
					List<String> removeCdList = new ArrayList<String>();
					List<String> removeUploadDttmList = new ArrayList<String>();
					List<String> removeUploadByList = new ArrayList<String>();

					for (int i = 0; i < delList.length; i++) {
						UploadDocument.deleteFile(FilenameUtils.normalize(folderPath + delList[i]));
						if (docTypeList != null && docTypeList.size() > 0) {
							removeTypeList.add(docTypeList.get(Integer.parseInt(delList[i])));
							removeNameList.add(docNameList.get(Integer.parseInt(delList[i])));
							removeCdList.add(docCdList.get(Integer.parseInt(delList[i])));
							removeUploadDttmList.add(docUploadDttmList.get(Integer.parseInt(delList[i])));
							removeUploadByList.add(docUploadByList.get(Integer.parseInt(delList[i])));
						}
					}
					if (docTypeList != null && docTypeList.size() > 0) {
						for (int i = 0; i < removeTypeList.size(); i++) {
							docTypeList.remove(removeTypeList.get(i));
						}
						for (int i = 0; i < removeNameList.size(); i++) {
							docNameList.remove(removeNameList.get(i));
						}
						for (int i = 0; i < removeCdList.size(); i++) {
							docCdList.remove(removeCdList.get(i));
						}
						for (int i = 0; i < removeUploadDttmList.size(); i++) {
							docUploadDttmList.remove(removeUploadDttmList.get(i));
						}
						for (int i = 0; i < removeUploadByList.size(); i++) {
							docUploadByList.remove(removeUploadByList.get(i));
						}
					}
				} else if ("MAC_SUBMIT".equals(command)) {
					macType = criteria.getPredicates().get("macType");
					fromDate = criteria.getPredicates().get("fromDate");
					toDate = criteria.getPredicates().get("toDate");
					String regNbrValue = criteria.getPredicates().get("regNbr");
					String liftCapacityValue = criteria.getPredicates().get("liftCapacity");
					String insuranceNbrValue = criteria.getPredicates().get("insuranceNbr");
					String insExpDttmValue = criteria.getPredicates().get("insExpDttm");
					// Added by Punitha on 24/01/2008

					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}
					parkMac = new MiscParkMacValueObject();
					parkMac.setRegNbrValue(regNbrValue);
					parkMac.setLiftCapacityValue(liftCapacityValue);
					parkMac.setInsuranceNbrValue(insuranceNbrValue);
					parkMac.setInsExpDttmValue(insExpDttmValue);
					if (macDetList != null) {
						macDetList.add(parkMac);
					} 
					map.put("macDetList", macDetList);
					// Ended by Punitha

					// if (regNbrList == null) {
					// regNbrList = new ArrayList();
					// regNbrList.add(regNbr);
					// map.put("regNbrList", regNbrList);
					// } else {
					// regNbrList.add(regNbr);
					// }
					//
					//
					// if (liftCapacityList == null) {
					// liftCapacityList = new ArrayList();
					// liftCapacityList.add(liftCapacity);
					// map.put("liftCapacityList", liftCapacityList);
					// } else {
					// liftCapacityList.add(liftCapacity);
					// }
					//
					//
					// if (insuranceNbrList == null) {
					// insuranceNbrList = new ArrayList();
					// insuranceNbrList.add(insuranceNbr);
					// map.put("insuranceNbrList", insuranceNbrList);
					// } else {
					// insuranceNbrList.add(insuranceNbr);
					// }
					//
					// if (insExpDttmList == null) {
					// insExpDttmList = new ArrayList();
					// insExpDttmList.add(insExpDttm);
					// map.put("insExpDttmList", insExpDttmList);
					// } else {
					// insExpDttmList.add(insExpDttm);
					// }
				} else if ("MAC_DELETE".equals(command)) {
					String[] delList = CommonUtil.getRequiredStringParameters(request, "macCheck");

					// Amended by Punitha on 24/01/2008
					// for (int i = 0; i < removeRegNbrList.size(); i++) {
					// regNbrList.remove(removeRegNbrList.get(i));
					// }
					// for (int i = 0; i < removeLiftCapacityList.size(); i++) {
					// liftCapacityList.remove(removeLiftCapacityList.get(i));
					// }
					// for (int i = 0; i < removeInsuranceNbrList.size(); i++) {
					// insuranceNbrList.remove(removeInsuranceNbrList.get(i));
					// }
					// for (int i = 0; i < removeInsuranceExpList.size(); i++) {
					// insExpDttmList.remove(removeInsuranceExpList.get(i));
					// }

					// ArrayList removeRegNbrList = new ArrayList();
					// ArrayList removeLiftCapacityList = new ArrayList();
					// ArrayList removeInsuranceNbrList = new ArrayList();
					// ArrayList removeInsuranceExpList = new ArrayList();
					// for (int i = 0; i < delList.length; i++) {
					// removeRegNbrList.add(regNbrList.get(Integer.parseInt(delList[i])));
					// removeLiftCapacityList.add((String)
					// liftCapacityList.get(Integer.parseInt(delList[i])));
					// removeInsuranceNbrList.add((String)
					// insuranceNbrList.get(Integer.parseInt(delList[i])));
					// removeInsuranceExpList.add((String)
					// insExpDttmList.get(Integer.parseInt(delList[i])));
					// }
					// for (int i = 0; i < removeRegNbrList.size(); i++) {
					// regNbrList.remove(removeRegNbrList.get(i));
					// }
					// for (int i = 0; i < removeLiftCapacityList.size(); i++) {
					// liftCapacityList.remove(removeLiftCapacityList.get(i));
					// }
					// for (int i = 0; i < removeInsuranceNbrList.size(); i++) {
					// insuranceNbrList.remove(removeInsuranceNbrList.get(i));
					// }
					// for (int i = 0; i < removeInsuranceExpList.size(); i++) {
					// insExpDttmList.remove(removeInsuranceExpList.get(i));
					// }
					// End

					// Added by Punitha on 24/01/2008
					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}
					List<MiscParkMacValueObject> macDetListDel = new ArrayList<MiscParkMacValueObject>();
					for (int i = 0; i < delList.length; i++) {
						log.info("Remove Item :" + delList[i]);
						macDetListDel.add((MiscParkMacValueObject) macDetList.get(Integer.parseInt(delList[i])));
					}
					for (int i = 0; i < macDetListDel.size(); i++) {
						macDetList.remove(macDetListDel.get(i));
					}
					map.put("macDetList", macDetList);

					// End
				} else if ("MAC_PHASE_OUT".equals(command)) {
					String phaseOutDt = criteria.getPredicates().get("phaseOutDt");
					/*
					 * Amended by Punitha on 24/01/2008 String[] checkList =
					 * criteria.getPredicates().getValues("macCheck"); ArrayList list = new
					 * ArrayList();
					 * 
					 * 
					 * if(checkList != null){ for(int i = 0; i < checkList.length; i++)
					 * list.add(checkList[i]); } for(int j=0;j<list.size();j++){
					 * System.out.println("Values in the list-------------- "+list.get(j)); }
					 * //System.out.println("^^^^^^^^list... " + list); if (phaseOutDtList == null)
					 * phaseOutDtList = new ArrayList();
					 * 
					 * if(list != null){ if(regNbrList != null){ for (int k = 0; k <
					 * regNbrList.size(); k++) {
					 * 
					 * if(list.contains(k+"")){ phaseOutDtList.add(k, phaseOutDt);
					 * 
					 * if(phaseOutDtList.size() > k+1){ phaseOutDtList.remove(k+1);
					 * 
					 * } }else{ if(phaseOutDtList.size() > k){ if(phaseOutDtList.get(k) == null){
					 * phaseOutDtList.add(k, null);
					 * 
					 * if(phaseOutDtList.size() > k+1){ phaseOutDtList.remove(k+1);
					 * 
					 * } } }else{ phaseOutDtList.add(k, null);
					 * 
					 * } } } } } map.put("phaseOutDtList", phaseOutDtList); End
					 */
					// Added by Punitha on 24/01/2008
					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}

					String checkValue = criteria.getPredicates().get("macCheck");
					parkMac = (MiscParkMacValueObject) macDetList.get(Integer.parseInt(checkValue));
					parkMac.setPhaseOutDtValue(phaseOutDt);
					map.put("macDetList", macDetList);
					// End

				}
				// Added by Punitha on 23/01/2008.
				else if ("MAC_INS_UPDATE".equals(command)) {
					String insuranceNbr = criteria.getPredicates().get("insuranceNbr");
					String insExpDttm = criteria.getPredicates().get("insExpDttm");
					String checkValue = criteria.getPredicates().get("macCheck");
					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}

					parkMac = (MiscParkMacValueObject) macDetList.get(Integer.parseInt(checkValue));
					parkMac.setInsuranceNbrValue(insuranceNbr);
					parkMac.setInsExpDttmValue(insExpDttm);
					map.put("macDetList", macDetList);

				}
				// End by Punitha

				// Added by Punitha on 20/11/2008.
				else if ("MAC_REG_UPDATE".equals(command)) {
					String regNo = criteria.getPredicates().get("regNo");
					String checkValue = criteria.getPredicates().get("macCheck");
					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}

					parkMac = (MiscParkMacValueObject) macDetList.get(Integer.parseInt(checkValue));
					parkMac.setRegNbrValue(regNo);
					map.put("macDetList", macDetList);

				}
				// End by Punitha
				if (docTypeList != null)
					map.put("docTypeTemp", docTypeList.toArray(new String[0]));
				if (docNameList != null)
					map.put("docNameTemp", docNameList.toArray(new String[0]));
				if (docCdList != null)
					map.put("docCdTemp", docCdList.toArray(new String[0]));
				if (docUploadDttmList != null)
					map.put("docUploadDttmTemp", docUploadDttmList.toArray(new String[0]));
				if (docUploadByList != null)
					map.put("docUploadByTemp", docUploadByList.toArray(new String[0]));

				map.put("status", status);
				map.put("macType", macType);
				map.put("fromDate", fromDate);
				map.put("toDate", toDate);
			}
			if ("MAC_ADD".equals(command)) {
				// nextScreen(request, "MiscAppUpdateStationMac");
				// return;
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateStationMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateStationMac : ", e);
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
			log.info("END: miscAppUpdateStationMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateStationMacHandler -->perform
	@RequestMapping(value = "/miscAppUpdateStationMacUpdate", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppUpdateStationMacUpdate(HttpServletRequest request,
			@RequestBody MiscParkMacBodyVO obj) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppUpdateStationMacUpdate criteria:" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = obj.getCommand();
			// String agree = criteria.getPredicates().get("agree");

			map.put("coCd", coCd);
			// Station Mac Details
			String macType = null;
			String fromDate = null;
			String toDate = null;
			// String docType = null;
			// String file = null;
			List<MiscAppValueObject> docTypeList = null;
			List<String> docNameList = null;
			List<String> docCdList = null;
			List<String> docUploadDttmList = null;
			List<String> docUploadByList = null;
			List<String> regNbrList = null;
			List<String> liftCapacityList = null;
			List<String> insuranceNbrList = null;
			List<String> insExpDttmList = null;
			List<String> phaseOutDtList = null;
			//
			// Added by Punitha on 24/01/2008
			List<MiscParkMacValueObject> macDetList = null;
			MiscParkMacValueObject parkMac = new MiscParkMacValueObject();
			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;

			if ("UPDATE_SUBMIT".equals(command)) {
				miscSeqNbr = obj.getMiscSeqNbr();
				status = obj.getStatus();
				coName = obj.getCoName();
				appType = obj.getAppTypeCd();
				account = obj.getAccount();
				appStatusCd = obj.getAppStatusCd();

				// docTypeList = session.getAttribute("docTypeList");
				// docNameList = session.getAttribute("docNameList");
				//	regNbrList =  request.getAttribute("regNbrList");
				//	liftCapacityList =  request.getAttribute("liftCapacityList");
				//	insuranceNbrList =  request.getAttribute("insuranceNbrList");
				//	insExpDttmList =  request.getAttribute("insExpDttmList");
				//	phaseOutDtList =  request.getAttribute("phaseOutDtList");
				Object listObject = null;
				listObject = obj.getRegNbrValue();
				regNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							regNbrList.add((String) item);
						}
					}
				}
				listObject = obj.getLiftCapacityValue();
				liftCapacityList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							liftCapacityList.add((String) item);
						}
					}
				}
				listObject = obj.getInsuranceNbr();
				insuranceNbrList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							insuranceNbrList.add((String) item);
						}
					}
				}
				listObject = obj.getInsExpDttm();
				insExpDttmList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							insExpDttmList.add((String) item);
						}
					}
				}
				listObject = obj.getPhaseOutDt();
				phaseOutDtList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							phaseOutDtList.add((String) item);
						}
					}
				}

				// Added by Punitha on 24/01/2008
				// macDetList = request.getAttribute("macDetList");
				macDetList = obj.getMacDetList();

				macType = obj.getMacType();
				fromDate = obj.getFromDate();
				toDate = obj.getToDate();

				MiscParkMacValueObject objVo = new MiscParkMacValueObject();
				objVo.setMacDetList(macDetList);
				objVo.setMacType(macType);
				objVo.setFromDate(fromDate);
				objVo.setToDate(toDate);

				if (docTypeList == null) {
					objVo.setDocType((String[]) CommonUtil.getRequiredStringParameters(request, "docTypeTemp"));

				}
				if (docNameList == null) {
					objVo.setDocName((String[]) CommonUtil.getRequiredStringParameters(request, "docNameTemp"));

				} 
				if (macDetList == null) {
					listObject = null;
					listObject =  obj.getMacDetList();
					List<MiscParkMacValueObject> list = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List){
						for(int j = 0; j < ((List<?>)listObject).size(); j++){
						Object item = ((List<?>) listObject).get(j);
						if(item instanceof Object){
							list.add((MiscParkMacValueObject) item);
						}
					    }
					}
					objVo.setMacDetList(list);
				} else {
					objVo.setMacDetList(macDetList);
				}

				gbMiscApplicationService.updateStationingOfMacDetails(userId, miscSeqNbr, status, coName, appType,
						account, appStatusCd, objVo);

			} else {
				status = obj.getStatus();
				macType = obj.getMacType();
				fromDate = obj.getFromDate();
				toDate = obj.getToDate();
				// docType = criteria.getPredicates().get("docType");
				// file = criteria.getPredicates().get("file");
			}
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

			if (docUploadDttmList == null) {
				docUploadDttmList = new ArrayList<String>();
				String temp[] = (String[]) CommonUtil.getRequiredStringParameters(request, "docUploadDttmTemp");

				if (temp != null && temp.length > 0) {
					for (int i = 0; i < temp.length; i++)
						docUploadDttmList.add(temp[i]);
				}
			}
			if (docUploadByList == null) {
				docUploadByList = new ArrayList<String>();
				String temp[] = (String[]) CommonUtil.getRequiredStringParameters(request, "docUploadByTemp");

				if (temp != null && temp.length > 0) {
					for (int i = 0; i < temp.length; i++)
						docUploadByList.add(temp[i]);
				}
			}

			 if ("MAC_SUBMIT".equals(command)) {
				macType = obj.getMacType();
				fromDate = obj.getFromDate();
				toDate = obj.getToDate();
				String regNbrValue = obj.getRegNbrValue();
				String liftCapacityValue = obj.getLiftCapacityValue();
				String insuranceNbrValue =  obj.getInsuranceNbrValue();
				String insExpDttmValue = obj.getInsExpDttmValue();
				// Added by Punitha on 24/01/2008
				// macDetList = request.getAttribute("macDetList");
				macDetList = obj.getMacDetList();
				parkMac = new MiscParkMacValueObject();
				parkMac.setRegNbrValue(regNbrValue);
				parkMac.setLiftCapacityValue(liftCapacityValue);
				parkMac.setInsuranceNbrValue(insuranceNbrValue);
				parkMac.setInsExpDttmValue(insExpDttmValue);
				if (macDetList == null) {
					macDetList = new ArrayList<MiscParkMacValueObject>();
					macDetList.add(parkMac);
				} else {
					macDetList.add(parkMac);
				}
				map.put("macDetList", macDetList);

			} else if ("MAC_DELETE".equals(command)) {
				String[] delList = CommonUtil.getRequiredStringParameters(request, "macCheck");

				// Added by Punitha on 24/01/2008
				listObject = null;
				listObject =  obj.getMacDetList();
				macDetList = new ArrayList<MiscParkMacValueObject>();
				if (listObject instanceof List){
					for(int j = 0; j < ((List<?>)listObject).size(); j++){
					Object item = ((List<?>) listObject).get(j);
					if(item instanceof Object){
						macDetList.add((MiscParkMacValueObject) item);
					}
				    }
				}
				List<MiscParkMacValueObject> macDetListDel = new ArrayList<MiscParkMacValueObject>();
				for (int i = 0; i < delList.length; i++) {
					log.info("Remove Item :" + delList[i]);
					macDetListDel.add((MiscParkMacValueObject) macDetList.get(Integer.parseInt(delList[i])));
				}
				for (int i = 0; i < macDetListDel.size(); i++) {
					macDetList.remove(macDetListDel.get(i));
				}
				map.put("macDetList", macDetList);

				// End
			} else if ("MAC_PHASE_OUT".equals(command)) {
				String phaseOutDt = criteria.getPredicates().get("phaseOutDt");

				// Added by Punitha on 24/01/2008
				listObject = null;
				listObject = obj.getMacDetList();
				macDetList = new ArrayList<MiscParkMacValueObject>();
				if (listObject instanceof List){
					for(int j = 0; j < ((List<?>)listObject).size(); j++){
					Object item = ((List<?>) listObject).get(j);
					if(item instanceof Object){
						macDetList.add((MiscParkMacValueObject) item);
					}
				    }
				}
				String checkValue = obj.getMacCheck();
				parkMac = (MiscParkMacValueObject) macDetList.get(Integer.parseInt(checkValue));
				parkMac.setPhaseOutDtValue(phaseOutDt);
				map.put("macDetList", macDetList);
				// End

			}
			// Added by Punitha on 23/01/2008.
			else if ("MAC_INS_UPDATE".equals(command)) {
				String insuranceNbr = obj.getInsuranceNbrValue();
				String insExpDttm = obj.getInsExpDttmValue();
				String checkValue = obj.getMacCheck();
				listObject = null;
				listObject = obj.getMacDetList();
				macDetList = new ArrayList<MiscParkMacValueObject>();
				if (listObject instanceof List){
					for(int j = 0; j < ((List<?>)listObject).size(); j++){
					Object item = ((List<?>) listObject).get(j);
					if(item instanceof Object){
						macDetList.add((MiscParkMacValueObject) item);
					}
				    }
				}
				parkMac = (MiscParkMacValueObject) macDetList.get(Integer.parseInt(checkValue));
				parkMac.setInsuranceNbrValue(insuranceNbr);
				parkMac.setInsExpDttmValue(insExpDttm);
				map.put("macDetList", macDetList);

			}
			// End by Punitha

			// Added by Punitha on 20/11/2008.
			else if ("MAC_REG_UPDATE".equals(command)) {
				String regNo = obj.getRegNbrValue();
				String checkValue = obj.getMacCheck();
				listObject = null;
				listObject = obj.getMacDetList();
				macDetList = new ArrayList<MiscParkMacValueObject>();
				if (listObject instanceof List){
					for(int j = 0; j < ((List<?>)listObject).size(); j++){
					Object item = ((List<?>) listObject).get(j);
					if(item instanceof Object){
						macDetList.add((MiscParkMacValueObject) item);
					}
				    }
				}
				parkMac = (MiscParkMacValueObject) macDetList.get(Integer.parseInt(checkValue));
				parkMac.setRegNbrValue(regNo);
				map.put("macDetList", macDetList);

			}  
			// End by Punitha
			if (regNbrList != null)
				map.put("regNbr", regNbrList.toArray(new String[0]));
			if (liftCapacityList != null)
				map.put("liftCapacity", liftCapacityList.toArray(new String[0]));
			if (insuranceNbrList != null)
				map.put("insuranceNbr", insuranceNbrList.toArray(new String[0]));
			if (insExpDttmList != null)
				map.put("insExpDttm", insExpDttmList.toArray(new String[0]));
			if (phaseOutDtList != null)
				map.put("phaseOutDt", phaseOutDtList.toArray(new String[0]));
			if (docTypeList != null)
				map.put("docTypeTemp", docTypeList.toArray(new String[0]));
			if (docNameList != null)
				map.put("docNameTemp", docNameList.toArray(new String[0]));
			if (docCdList != null)
				map.put("docCdTemp", docCdList.toArray(new String[0]));
			if (docUploadDttmList != null)
				map.put("docUploadDttmTemp", docUploadDttmList.toArray(new String[0]));
			if (docUploadByList != null)
				map.put("docUploadByTemp", docUploadByList.toArray(new String[0]));

			map.put("status", status);
			map.put("macType", macType);
			map.put("fromDate", fromDate);
			map.put("toDate", toDate);

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateStationMacUpdate : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateStationMacUpdate : ", e);
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
			log.info("END: miscAppUpdateStationMacUpdate result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateStationMacHandler -->perform
	@RequestMapping(value = "/miscAppUpdateStationMac", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppUpdateStationMac(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppUpdateStationMac criteria:" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			// String agree = criteria.getPredicates().get("agree");

			map.put("coCd", coCd);
			// Station Mac Details
			String macType = null;
			String fromDate = null;
			String toDate = null;
			// String docType = null;
			// String file = null;
			List<MiscAppValueObject> docTypeList = null;
			List<String> docNameList = null;
			List<String> docCdList = null;
			List<String> docUploadDttmList = null;
			List<String> docUploadByList = null;
			List<String> regNbrList = null;
			List<String> liftCapacityList = null;
			List<String> insuranceNbrList = null;
			List<String> insExpDttmList = null;
			List<String> phaseOutDtList = null;
			//
			// Added by Punitha on 24/01/2008
			List<MiscParkMacValueObject> macDetList = null;
			MiscParkMacValueObject parkMac = new MiscParkMacValueObject();
			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("UPDATE".equals(command)) {

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

				docTypeList = gbMiscApplicationService.getUploadDocumentTypeList();
				map.put("docTypeList", docTypeList);
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

				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");

			} else if ("UPDATE_SUBMIT".equals(command)) {
				miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				status = criteria.getPredicates().get("status");
				coName = criteria.getPredicates().get("coName");
				appType = criteria.getPredicates().get("appTypeCd");
				account = criteria.getPredicates().get("account");
				appStatusCd = criteria.getPredicates().get("appStatusCd");

				// docTypeList = session.getAttribute("docTypeList");
				// docNameList = session.getAttribute("docNameList");
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
				listObject = request.getAttribute("phaseOutDtList");
				phaseOutDtList = new ArrayList<String>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							phaseOutDtList.add((String) item);
						}
					}
				}
				// Added by Punitha on 24/01/2008
				listObject = request.getAttribute("macDetList");
				macDetList = new ArrayList<MiscParkMacValueObject>();
				if (listObject instanceof List) {
					for (int j = 0; j < ((List<?>) listObject).size(); j++) {
						Object item = ((List<?>) listObject).get(j);
						if (item instanceof Object) {
							macDetList.add((MiscParkMacValueObject) item);
						}
					}
				}

				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");

				MiscParkMacValueObject obj = new MiscParkMacValueObject();
				obj.setMacType(macType);
				obj.setFromDate(fromDate);
				obj.setToDate(toDate);

				if (docTypeList != null) {
					obj.setDocType((String[]) docTypeList.toArray(new String[0]));
				} 
				if (docNameList != null) {
					obj.setDocName((String[]) docNameList.toArray(new String[0]));
				} 
				// Amended by Punitha on 24/01/2008
				/*
				 * if(regNbrList == null){ obj.setRegNbr((String[])
				 * session.getAttribute("regNbr")); }else{ obj.setRegNbr((String[])
				 * regNbrList.toArray(new String[0])); } if(liftCapacityList == null){
				 * obj.setLiftCapacity((String[]) session.getAttribute("liftCapacity")); }else{
				 * obj.setLiftCapacity((String[]) liftCapacityList.toArray(new String[0])); }
				 * if(insuranceNbrList == null){ obj.setInsuranceNbr((String[])
				 * session.getAttribute("insuranceNbr")); }else{ obj.setInsuranceNbr((String[])
				 * insuranceNbrList.toArray(new String[0])); } if(insExpDttmList == null){
				 * obj.setInsExpDttm((String[]) session.getAttribute("insExpDttm")); }else{
				 * obj.setInsExpDttm((String[]) insExpDttmList.toArray(new String[0])); }
				 * if("JP".equals(coCd)){ if(phaseOutDtList == null){
				 * obj.setPhaseOutDt((String[]) session.getAttribute("phaseOutDt")); }else{
				 * obj.setPhaseOutDt((String[]) phaseOutDtList.toArray(new String[0])); } }else{
				 * obj.setPhaseOutDt(null); }
				 */
				// End by Punitha
				// //Added by Punitha on 24/01/2008

				if (macDetList != null) {
					obj.setMacDetList(macDetList);
				}
				// //End by Punitha

				gbMiscApplicationService.updateStationingOfMacDetails(userId, miscSeqNbr, status, coName, appType,
						account, appStatusCd, obj);

				/*
				 * System.out.println("assignFileName -----> " + assignFileName); //write file
				 * into server byte[] fileContent = null; String[] fileName = obj.getDocName();
				 * if(docNameList != null && assignFileName != null){
				 * //System.out.println("assignFileName -----> " + assignFileName.size());
				 * for(int i = 0; i < docNameList.size(); i++){ if(fileName[i] != null &&
				 * fileName[i].indexOf("\\") != -1){ fileContent =
				 * UploadDocument.getFileContent((String)docNameList.get(i));
				 * UploadDocument.writeToFile(fileContent, (String)assignFileName.get(i),
				 * "MACHINE"); } } }
				 */

				// remove session attributes here
				// removeSessionAttributes(session);
				// forwardHandler(request, "MiscAppList");
				// return;
			} else if ("CANCEL".equals(command)) {
				// removeSessionAttributes(session);
				// forwardHandler(request, "MiscAppList");
				// return;
			} else {
				status = criteria.getPredicates().get("status");
				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");
				// docType = criteria.getPredicates().get("docType");
				// file = criteria.getPredicates().get("file");
				// docTypeList = session.getAttribute("docTypeList");
				// docNameList = session.getAttribute("docNameList");
				// regNbrList = session.getAttribute("regNbrList");
				// liftCapacityList = session.getAttribute("liftCapacityList");
				// insuranceNbrList = session.getAttribute("insuranceNbrList");
				// insExpDttmList = session.getAttribute("insExpDttmList");
				// phaseOutDtList = session.getAttribute("phaseOutDtList");

				/*
				 * if(docTypeList == null){ //System.out.println("^^^^^^^^2... docTypeList " +
				 * ((String[] )session.getAttribute("docType")).length); docTypeList = new
				 * ArrayList(); String temp[] = (String[] )session.getAttribute("docType");
				 * for(int i=0; i< temp.length; i++) docTypeList.add(temp[i]); } if(docNameList
				 * == null){ docNameList = new ArrayList(); String temp[] = (String[]
				 * )session.getAttribute("docName"); for(int i=0; i< temp.length; i++)
				 * docNameList.add(temp[i]); }
				 */
				// if(regNbrList == null){
				// regNbrList = new ArrayList();
				// String temp[] = (String[] )session.getAttribute("regNbr");
				// for(int i=0; i< temp.length; i++)
				// regNbrList.add(temp[i]);
				// //System.out.println("^^^^^^^^2... regNbrList " + regNbrList.size());
				// }
				// if(liftCapacityList == null){
				// liftCapacityList = new ArrayList();
				// String temp[] = (String[] )session.getAttribute("liftCapacity");
				// for(int i=0; i< temp.length; i++)
				// liftCapacityList.add(temp[i]);
				// //System.out.println("^^^^^^^^2... liftCapacityList " +
				// liftCapacityList.size());
				// }
				// if(insuranceNbrList == null){
				// insuranceNbrList = new ArrayList();
				// String temp[] = (String[] )session.getAttribute("insuranceNbr");
				// for(int i=0; i< temp.length; i++)
				// insuranceNbrList.add(temp[i]);
				// //System.out.println("^^^^^^^^2... insuranceNbrList " +
				// insuranceNbrList.size());
				// }
				// if(insExpDttmList == null){
				// insExpDttmList = new ArrayList();
				// String temp[] = (String[] )session.getAttribute("insExpDttm");
				// for(int i=0; i< temp.length; i++)
				// insExpDttmList.add(temp[i]);
				// //System.out.println("^^^^^^^^2... insExpDttmList " + insExpDttmList.size());
				// }
				// if(phaseOutDtList == null){
				// phaseOutDtList = new ArrayList();
				// String temp[] = (String[] )session.getAttribute("phaseOutDt");
				// if(temp != null){
				// for(int i=0; i< temp.length; i++)
				// phaseOutDtList.add(temp[i]);
				// }
				// System.out.println("^^^^^^^^2... phaseOutDtList " + phaseOutDtList.size());
				// }
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

				if (docUploadDttmList == null) {
					docUploadDttmList = new ArrayList<String>();
					String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request, "docUploadDttmTemp"));

					if (temp != null && temp.length > 0) {
						for (int i = 0; i < temp.length; i++)
							docUploadDttmList.add(temp[i]);
					}
				}
				if (docUploadByList == null) {
					docUploadByList = new ArrayList<String>();
					String temp[] = (String[]) (CommonUtil.getRequiredStringParameters(request, "docUploadByTemp"));

					if (temp != null && temp.length > 0) {
						for (int i = 0; i < temp.length; i++)
							docUploadByList.add(temp[i]);
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
					/*
					 * miscSeqNbr = session.getAttribute("miscSeqNbr"); byte[] fileContent = null;
					 * String assignedName = (String)miscAppEjb.uploadDocument(userId, miscSeqNbr,
					 * status, docType, file); //System.out.println("file -----> " + file);
					 * //System.out.println("assignedName -----> " + assignedName); fileContent =
					 * UploadDocument.getFileContent(file); UploadDocument.writeToFile(fileContent,
					 * assignedName, "MACHINE");
					 * 
					 * ArrayList docDetailsList = miscAppEjb.getUploadDocumentDetails(miscSeqNbr);
					 * map.put("docDetailsList", docDetailsList);
					 * 
					 * map.put("status", status); map.put("macType", macType); map.put("fromDate",
					 * fromDate); map.put("toDate", toDate);
					 * 
					 * nextScreen(request, "MiscAppUpdateStationMac"); return;
					 */

					map.put("subDirName", "MACHINE");
					map.put("nextScreen", "MiscAppUpdateStationMac");
				} else if ("DELETE".equals(command)) {
					/*
					 * String[] delList = request.getParameterValues("docCheck"); for (int i = 0; i
					 * < delList.length; i++) { String ind = delList[i];
					 * docTypeList.remove(Integer.parseInt(ind));
					 * docNameList.remove(Integer.parseInt(ind)); }
					 */
					// String[] delList = request.getParameterValues("docCheck");

					// delete from server
					/*
					 * if(delList != null){
					 * //System.out.println("Going to delete files from serever..."); for(int i = 0;
					 * i < delList.length; i++){ //System.out.println("delList[" + i + "] -----> " +
					 * delList[i]); UploadDocument.deleteFile((String)delList[i], "MACHINE"); } }
					 * miscSeqNbr = session.getAttribute("miscSeqNbr");
					 * miscAppEjb.deleteDocument(miscSeqNbr, delList);
					 * 
					 * ArrayList docDetailsList = miscAppEjb.getUploadDocumentDetails(miscSeqNbr);
					 * map.put("docDetailsList", docDetailsList);
					 * 
					 * map.put("status", status); map.put("macType", macType); map.put("fromDate",
					 * fromDate); map.put("toDate", toDate);
					 * 
					 * nextScreen(request, "MiscAppUpdateStationMac"); return;
					 */
					String[] delList =  criteria.getPredicates().get("docCheck").split(",");

					List<MiscAppValueObject> removeTypeList = new ArrayList<MiscAppValueObject>();
					List<String> removeNameList = new ArrayList<String>();
					List<String> removeCdList = new ArrayList<String>();
					List<String> removeUploadDttmList = new ArrayList<String>();
					List<String> removeUploadByList = new ArrayList<String>();

					for (int i = 0; i < delList.length; i++) {
						UploadDocument.deleteFile(FilenameUtils.normalize(folderPath + delList[i]));
						if (docTypeList != null && docTypeList.size() > 0) {
							removeTypeList.add(docTypeList.get(Integer.parseInt(delList[i])));
							removeNameList.add(docNameList.get(Integer.parseInt(delList[i])));
							removeCdList.add(docCdList.get(Integer.parseInt(delList[i])));
							removeUploadDttmList.add(docUploadDttmList.get(Integer.parseInt(delList[i])));
							removeUploadByList.add(docUploadByList.get(Integer.parseInt(delList[i])));
						}
					}
					if (docTypeList != null && docTypeList.size() > 0) {
						for (int i = 0; i < removeTypeList.size(); i++) {
							docTypeList.remove(removeTypeList.get(i));
						}
						for (int i = 0; i < removeNameList.size(); i++) {
							docNameList.remove(removeNameList.get(i));
						}
						for (int i = 0; i < removeCdList.size(); i++) {
							docCdList.remove(removeCdList.get(i));
						}
						for (int i = 0; i < removeUploadDttmList.size(); i++) {
							docUploadDttmList.remove(removeUploadDttmList.get(i));
						}
						for (int i = 0; i < removeUploadByList.size(); i++) {
							docUploadByList.remove(removeUploadByList.get(i));
						}
					}
				} else if ("MAC_SUBMIT".equals(command)) {
					macType = criteria.getPredicates().get("macType");
					fromDate = criteria.getPredicates().get("fromDate");
					toDate = criteria.getPredicates().get("toDate");
					String regNbrValue = criteria.getPredicates().get("regNbr");
					String liftCapacityValue = criteria.getPredicates().get("liftCapacity");
					String insuranceNbrValue = criteria.getPredicates().get("insuranceNbr");
					String insExpDttmValue = criteria.getPredicates().get("insExpDttm");
					// Added by Punitha on 24/01/2008
					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}

					parkMac = new MiscParkMacValueObject();
					parkMac.setRegNbrValue(regNbrValue);
					parkMac.setLiftCapacityValue(liftCapacityValue);
					parkMac.setInsuranceNbrValue(insuranceNbrValue);
					parkMac.setInsExpDttmValue(insExpDttmValue);
					if (macDetList != null) {
						macDetList.add(parkMac);
					} 
					map.put("macDetList", macDetList);
					// Ended by Punitha

					// if (regNbrList == null) {
					// regNbrList = new ArrayList();
					// regNbrList.add(regNbr);
					// map.put("regNbrList", regNbrList);
					// } else {
					// regNbrList.add(regNbr);
					// }
					//
					//
					// if (liftCapacityList == null) {
					// liftCapacityList = new ArrayList();
					// liftCapacityList.add(liftCapacity);
					// map.put("liftCapacityList", liftCapacityList);
					// } else {
					// liftCapacityList.add(liftCapacity);
					// }
					//
					//
					// if (insuranceNbrList == null) {
					// insuranceNbrList = new ArrayList();
					// insuranceNbrList.add(insuranceNbr);
					// map.put("insuranceNbrList", insuranceNbrList);
					// } else {
					// insuranceNbrList.add(insuranceNbr);
					// }
					//
					// if (insExpDttmList == null) {
					// insExpDttmList = new ArrayList();
					// insExpDttmList.add(insExpDttm);
					// map.put("insExpDttmList", insExpDttmList);
					// } else {
					// insExpDttmList.add(insExpDttm);
					// }
				} else if ("MAC_DELETE".equals(command)) {
					String[] delList = CommonUtil.getRequiredStringParameters(request, "macCheck");

					// Amended by Punitha on 24/01/2008
					// for (int i = 0; i < removeRegNbrList.size(); i++) {
					// regNbrList.remove(removeRegNbrList.get(i));
					// }
					// for (int i = 0; i < removeLiftCapacityList.size(); i++) {
					// liftCapacityList.remove(removeLiftCapacityList.get(i));
					// }
					// for (int i = 0; i < removeInsuranceNbrList.size(); i++) {
					// insuranceNbrList.remove(removeInsuranceNbrList.get(i));
					// }
					// for (int i = 0; i < removeInsuranceExpList.size(); i++) {
					// insExpDttmList.remove(removeInsuranceExpList.get(i));
					// }

					// ArrayList removeRegNbrList = new ArrayList();
					// ArrayList removeLiftCapacityList = new ArrayList();
					// ArrayList removeInsuranceNbrList = new ArrayList();
					// ArrayList removeInsuranceExpList = new ArrayList();
					// for (int i = 0; i < delList.length; i++) {
					// removeRegNbrList.add(regNbrList.get(Integer.parseInt(delList[i])));
					// removeLiftCapacityList.add((String)
					// liftCapacityList.get(Integer.parseInt(delList[i])));
					// removeInsuranceNbrList.add((String)
					// insuranceNbrList.get(Integer.parseInt(delList[i])));
					// removeInsuranceExpList.add((String)
					// insExpDttmList.get(Integer.parseInt(delList[i])));
					// }
					// for (int i = 0; i < removeRegNbrList.size(); i++) {
					// regNbrList.remove(removeRegNbrList.get(i));
					// }
					// for (int i = 0; i < removeLiftCapacityList.size(); i++) {
					// liftCapacityList.remove(removeLiftCapacityList.get(i));
					// }
					// for (int i = 0; i < removeInsuranceNbrList.size(); i++) {
					// insuranceNbrList.remove(removeInsuranceNbrList.get(i));
					// }
					// for (int i = 0; i < removeInsuranceExpList.size(); i++) {
					// insExpDttmList.remove(removeInsuranceExpList.get(i));
					// }
					// End

					// Added by Punitha on 24/01/2008
					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}
					List<MiscParkMacValueObject> macDetListDel = new ArrayList<MiscParkMacValueObject>();
					for (int i = 0; i < delList.length; i++) {
						log.info("Remove Item :" + delList[i]);
						macDetListDel.add((MiscParkMacValueObject) macDetList.get(Integer.parseInt(delList[i])));
					}
					for (int i = 0; i < macDetListDel.size(); i++) {
						macDetList.remove(macDetListDel.get(i));
					}
					map.put("macDetList", macDetList);

					// End
				} else if ("MAC_PHASE_OUT".equals(command)) {
					String phaseOutDt = criteria.getPredicates().get("phaseOutDt");
					/*
					 * Amended by Punitha on 24/01/2008 String[] checkList =
					 * criteria.getPredicates().getValues("macCheck"); ArrayList list = new
					 * ArrayList();
					 * 
					 * 
					 * if(checkList != null){ for(int i = 0; i < checkList.length; i++)
					 * list.add(checkList[i]); } for(int j=0;j<list.size();j++){
					 * System.out.println("Values in the list-------------- "+list.get(j)); }
					 * //System.out.println("^^^^^^^^list... " + list); if (phaseOutDtList == null)
					 * phaseOutDtList = new ArrayList();
					 * 
					 * if(list != null){ if(regNbrList != null){ for (int k = 0; k <
					 * regNbrList.size(); k++) {
					 * 
					 * if(list.contains(k+"")){ phaseOutDtList.add(k, phaseOutDt);
					 * 
					 * if(phaseOutDtList.size() > k+1){ phaseOutDtList.remove(k+1);
					 * 
					 * } }else{ if(phaseOutDtList.size() > k){ if(phaseOutDtList.get(k) == null){
					 * phaseOutDtList.add(k, null);
					 * 
					 * if(phaseOutDtList.size() > k+1){ phaseOutDtList.remove(k+1);
					 * 
					 * } } }else{ phaseOutDtList.add(k, null);
					 * 
					 * } } } } } map.put("phaseOutDtList", phaseOutDtList); End
					 */
					// Added by Punitha on 24/01/2008
					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}

					String checkValue = criteria.getPredicates().get("macCheck");
					parkMac = (MiscParkMacValueObject) macDetList.get(Integer.parseInt(checkValue));
					parkMac.setPhaseOutDtValue(phaseOutDt);
					map.put("macDetList", macDetList);
					// End

				}
				// Added by Punitha on 23/01/2008.
				else if ("MAC_INS_UPDATE".equals(command)) {
					String insuranceNbr = criteria.getPredicates().get("insuranceNbr");
					String insExpDttm = criteria.getPredicates().get("insExpDttm");
					String checkValue = criteria.getPredicates().get("macCheck");
					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}

					parkMac = (MiscParkMacValueObject) macDetList.get(Integer.parseInt(checkValue));
					parkMac.setInsuranceNbrValue(insuranceNbr);
					parkMac.setInsExpDttmValue(insExpDttm);
					map.put("macDetList", macDetList);

				}
				// End by Punitha

				// Added by Punitha on 20/11/2008.
				else if ("MAC_REG_UPDATE".equals(command)) {
					String regNo = criteria.getPredicates().get("regNo");
					String checkValue = criteria.getPredicates().get("macCheck");
					listObject = request.getAttribute("macDetList");
					macDetList = new ArrayList<MiscParkMacValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof Object) {
								macDetList.add((MiscParkMacValueObject) item);
							}
						}
					}

					parkMac = (MiscParkMacValueObject) macDetList.get(Integer.parseInt(checkValue));
					parkMac.setRegNbrValue(regNo);
					map.put("macDetList", macDetList);

				}
				// End by Punitha
				if (regNbrList != null)
					map.put("regNbr", regNbrList.toArray(new String[0]));
				if (docTypeList != null)
					map.put("docTypeTemp", docTypeList.toArray(new String[0]));
				if (docNameList != null)
					map.put("docNameTemp", docNameList.toArray(new String[0]));
				if (docCdList != null)
					map.put("docCdTemp", docCdList.toArray(new String[0]));
				if (docUploadDttmList != null)
					map.put("docUploadDttmTemp", docUploadDttmList.toArray(new String[0]));
				if (docUploadByList != null)
					map.put("docUploadByTemp", docUploadByList.toArray(new String[0]));

				map.put("status", status);
				map.put("macType", macType);
				map.put("fromDate", fromDate);
				map.put("toDate", toDate);
			}
			if ("MAC_ADD".equals(command)) {
				// nextScreen(request, "MiscAppUpdateStationMac");
				// return;
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateStationMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateStationMac : ", e);
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
			log.info("END: miscAppUpdateStationMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateTpaHandler -->perform
	@RequestMapping(value = "/miscAppUpdateTpa", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppUpdateTpa(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		errorMessage = null;
		if (criteria.getPredicates().isEmpty()) {
			criteria = CommonUtil.getCriteria(request);
			map = new HashMap<>();
		}
		Result result = new Result();
		try {
			log.info("START: miscAppUpdateTpa criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String command = criteria.getPredicates().get("command");

			// Veh Details
			String fromDate = null;
			String toDate = null;
			String noHours = null;
			String parkReason = null;
//				String[] vehNo = null;
//				String[] cntNo = null;
//				String[] asnNo = null;
//				String[] remarks = null;
//				String[] preferredArea = null;

			String conPerson = null;
			String conTel = null;

			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;
			String reasonForApplication = null;
			String cargoType = null;

			String varcode = null;
			String cust = null;
			String appDate = null;

			miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			coName = criteria.getPredicates().get("coName");
			appType = criteria.getPredicates().get("appTypeCd");
			account = criteria.getPredicates().get("account");
			appStatusCd = criteria.getPredicates().get("appStatusCd");

			log.info("**********miscSeqNbr --------------> " + miscSeqNbr);
			log.info("~~~~~ appStatusCd: ~~~~~" + appStatusCd);
			log.info("~~~~~ status: ~~~~~" + criteria.getPredicates().get("status"));
			log.info("~~~~~ appType: ~~~~~" + appType);
			if ("UPDATE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				this.displayScreen(request, gbMiscApplicationService, userId, applyType, appSeqNbr, applyTypeNm, map);
				this.miscAppUpdateTpa(criteria, map);

			} else if ("UPDATE_SUBMIT".equals(command)) {
				String size = criteria.getPredicates().get("size");
				String[] vehNo = new String[Integer.parseInt(size)];
				String[] cntNo = new String[Integer.parseInt(size)];
				String[] asnNo = new String[Integer.parseInt(size)];
				String[] preferredArea = new String[Integer.parseInt(size)];
				String[] remarks = new String[Integer.parseInt(size)];

				status = criteria.getPredicates().get("status");
				fromDate = criteria.getPredicates().get("fromDate") + criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate") + criteria.getPredicates().get("toTime");
				noHours = criteria.getPredicates().get("noHours");
				parkReason = criteria.getPredicates().get("appRemarks");

				for (int i = 0; i < Integer.parseInt(size); i++) {
					vehNo[i] = CommonUtility.deNull(criteria.getPredicates().get("vehNo" + i));
					cntNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntNo" + i));
					asnNo[i] = CommonUtility.deNull(criteria.getPredicates().get("asnNo" + i));
					preferredArea[i] = CommonUtility.deNull(criteria.getPredicates().get("preferredArea" + i));
					remarks[i] = CommonUtility.deNull(criteria.getPredicates().get("remarks" + i));
				}

//					vehNo = CommonUtil.getRequiredStringParameters(request, "vehNo");
//					cntNo = CommonUtil.getRequiredStringParameters(request, "cntNo");
//					asnNo = CommonUtil.getRequiredStringParameters(request, "asnNo");
//					remarks = CommonUtil.getRequiredStringParameters(request, "remarks");
//					preferredArea = new String[5];
//					preferredArea[0] = criteria.getPredicates().get("preferredArea1");
//					preferredArea[1] = criteria.getPredicates().get("preferredArea2");
//					preferredArea[2] = criteria.getPredicates().get("preferredArea3");
//					preferredArea[3] = criteria.getPredicates().get("preferredArea4");
//					preferredArea[4] = criteria.getPredicates().get("preferredArea5");

				reasonForApplication = criteria.getPredicates().get("reasonForApp");
				cargoType = criteria.getPredicates().get("cargoType");

				conPerson = criteria.getPredicates().get("conPerson");
				conTel = criteria.getPredicates().get("conTel");

				if ("D".equals(status)) {

					gbMiscApplicationService.updateTrailerParkingApplicationDetails(userId, miscSeqNbr, status,
							fromDate, toDate, noHours, parkReason, vehNo, cntNo, asnNo, coName, appType, account,
							appStatusCd, conPerson, conTel, preferredArea, remarks, reasonForApplication, cargoType);

					// forwardHandler(request, "MiscAppList");
					// return;
				} else {
					String errorCode = this.validate(request, gbMiscApplicationService, vehNo, preferredArea);

					if (!"0".equalsIgnoreCase(errorCode)) {
						// return current page with error message
						log.info("Unable to submit as slot is not available. Error code " + errorCode);
						map.put("errorCode", errorCode);
						String applyType = criteria.getPredicates().get("applyType");
						String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
						String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
						this.displayScreen(request, gbMiscApplicationService, userId, applyType, appSeqNbr, applyTypeNm,
								map);
						// return;
					} else {
						if (!CARGO_TYPE_NORMAL.equalsIgnoreCase(cargoType)) {
							gbMiscApplicationService.updateTrailerParkingApplicationDetails(userId, miscSeqNbr, status,
									fromDate, toDate, noHours, parkReason, vehNo, cntNo, asnNo, coName, appType,
									account, appStatusCd, conPerson, conTel, preferredArea, remarks,
									reasonForApplication, cargoType);

							// forwardHandler(request, "MiscAppList");
							// return;
						} else {
							// Empty and normal then auto assign and approve
							String returnMiscSeqNbr = gbMiscApplicationService.updateForTpaEmptyNormal(userId, appType,
									status, cust, account, varcode, fromDate, toDate, noHours, parkReason, vehNo, cntNo,
									asnNo, coName, appDate, conPerson, conTel, preferredArea, remarks,
									reasonForApplication, cargoType, miscSeqNbr, appStatusCd);

							log.info("miscSeqNbr:" + miscSeqNbr);
							if (returnMiscSeqNbr == null || returnMiscSeqNbr.equals("")) {
								// Error
								log.info("Unable to submit as slot is not available.");
								List<Map<String, Object>> miscParkReasonList = gbMiscApplicationService
										.getParkingReasonList();
								map.put("MiscParkReasonList", miscParkReasonList);

								map.put("errorCode", "2");
								this.miscAppUpdateTpa(criteria, map);
								// nextScreen(request, "MiscAppUpdateTpa");
								// return;
							} else {
								// To get application, vehicle detail information and redirect to auto approve
								// page
								List<Object> list = gbMiscApplicationService.getTrailerParkingApplicationDetails(userId,
										appType, miscSeqNbr, "");

								map.put("details", list);
								map.put("autoApprove", "true");
								this.miscAppUpdateTpa(criteria, map);
								// nextScreen(request, "MiscAppUpdateTpa");
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
				List<Map<String, String>> calParkAreaList = new ArrayList<Map<String, String>>();
				Map<String, String> parkAreaValuesMap = new LinkedHashMap<String, String>();
				for (int i = 0; i < miscParkAreaList.size(); i++) {
					MiscAppParkingAreaObject miscAppParkingAreaObject = (MiscAppParkingAreaObject) miscParkAreaList
							.get(i);
					String description = miscAppParkingAreaObject.getAreaCode() + "("
							+ miscAppParkingAreaObject.getNoOfSlot() + ")";
					parkAreaValuesMap.put(miscAppParkingAreaObject.getAreaCode(), description);
				}
				calParkAreaList.add(parkAreaValuesMap);
				map.put("MiscParkAreaList", calParkAreaList);
				this.miscAppUpdateTpa(criteria, map);
				// nextScreen(request, "MiscAppUpdateTpa");

			} else if ("APPLICATION_ENQUIRE".equals(command)) {

				String fromDateEnq = criteria.getPredicates().get("fromDate");
				String fromTimeEnq = criteria.getPredicates().get("fromTime");
				String toDateEnq = criteria.getPredicates().get("toDate");
				String toTimeEnq = criteria.getPredicates().get("toTime");
				String cargoTypeEnq = criteria.getPredicates().get("cargoType");
				EnquireQueryObject queryObj = null;

				if (fromDateEnq != null && fromDateEnq.trim().length() > 0) {
					queryObj = new EnquireQueryObject();
					queryObj.setStartDate(fromDateEnq);
					queryObj.setStartTime(fromTimeEnq);
					queryObj.setAreaCode("ALL");
					queryObj.setSlotType(cargoTypeEnq);
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
				this.miscAppUpdateTpa(criteria, map);
				// nextScreen(request, "MiscAppUpdateTpa");
			} else if ("CANCEL".equals(command)) {
				this.miscAppUpdateTpa(criteria, map);
				// forwardHandler(request, "MiscAppList");
				// return;
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateTpa : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateTpa : ", e);
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
			log.info("END: miscAppUpdateTpa result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateTpaHandler -->displayScreen
	private Map<String, Object> displayScreen(HttpServletRequest request, GBMiscApplicationListService gbMiscApplicationService,
			String userId, String applyType, String appSeqNbr, String applyTypeNm, Map<String, Object> map) {
		try {
			log.info("START: displayScreen request:" + request.toString() + ", userId: " + CommonUtility.deNull(userId) + ", applyType: " + CommonUtility.deNull(applyType) + ", appSeqNbr: " + CommonUtility.deNull(appSeqNbr) + ", applyTypeNm: " + CommonUtility.deNull(applyTypeNm) + ", map: " + map.toString());
			List<Object> list = gbMiscApplicationService.getTrailerParkingApplicationDetails(userId, applyType,
					appSeqNbr, applyTypeNm);
			MiscAppValueObject appObj = null;
			if (list != null && list.size() > 0) {
				appObj = (MiscAppValueObject) list.get(0);
			}
			List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
			map.put("vesselList", vesselList);
			map.put("details", list);
			// get list of parking reasons
			List<Map<String, Object>> miscParkReasonList = gbMiscApplicationService.getParkingReasonList();
			map.put("MiscParkReasonList", miscParkReasonList);
			int dateRange = gbMiscApplicationService.getTpaDatePeriod();
			map.put("dateRange", dateRange + "");
			// nextScreen(request, "MiscAppUpdateTpa");

		} catch (Exception e) {
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		}
		
		log.info("END: displayScreen map:" + map.toString());
		return map;
	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateVehHandler -->perform
	@RequestMapping(value = "/miscAppUpdateVeh", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppUpdateVeh(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppUpdateVeh criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// BEGIN updated by ThangPV, on 02/10/2013: Add validation for TO_TIME <=
			// CUT_OFF_DATE
			// EJBHomeFactory homeFactory = EJBHomeFactory.getInstance();

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

			// Veh Details
			String fromDate = null;
			String toDate = null;
			String noNights = null;
			String parkReason = null;
			// String[] vehNo = null;
			// String[] cntNo = null;
			// String[] asnNo = null;
			String size = criteria.getPredicates().get("size");
			String[] vehNo = new String[Integer.parseInt(size)];
			String[] cntNo = new String[Integer.parseInt(size)];
			String[] asnNo = new String[Integer.parseInt(size)];
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
			String conPerson = null;
			String conTel = null;
			String conEmail = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;
			miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			coName = criteria.getPredicates().get("coName");
			appType = criteria.getPredicates().get("appTypeCd");
			account = criteria.getPredicates().get("account");
			appStatusCd = criteria.getPredicates().get("appStatusCd");

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("UPDATE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getOvernightParkingVehicleDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
			} else if ("UPDATE_SUBMIT".equals(command)) {
				if (agree == null) {
					status = criteria.getPredicates().get("status");
					fromDate = criteria.getPredicates().get("fromDate");
					toDate = criteria.getPredicates().get("toDate");
					noNights = criteria.getPredicates().get("noNights");
					parkReason = criteria.getPredicates().get("parkReason");
					appType = criteria.getPredicates().get("appTypeCd");
					// vehNo = CommonUtil.getRequiredStringParameters(request,"vehNo");
					// cntNo = CommonUtil.getRequiredStringParameters(request,"cntNo");
					// asnNo = CommonUtil.getRequiredStringParameters(request,"asnNo");
					for (int i = 0; i < Integer.parseInt(size); i++) {
						vehNo[i] = CommonUtility.deNull(criteria.getPredicates().get("vehNo" + i));
						cntNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntNo" + i));
						asnNo[i] = CommonUtility.deNull(criteria.getPredicates().get("asnNo" + i));
					}
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
						throw new BusinessException(errorMessage);
					}

					List<String> invalidContainer = gbMiscApplicationService.checkMiscAppOnvContainer(cntNo);
					if (invalidContainer.size() > 0) {
						errorMessage = Arrays.toString(invalidContainer.toArray())
								+ " are invalid container. Please check.";
						// return;
						throw new BusinessException(errorMessage);
					}
					// Added to not check character ASN
					String tmpAsn = "";

					// added by nasir flage for check asn
					boolean checkAsn = false;
					for (int i = 0; i < asnNo.length; i++) {
						if (asnNo[i] != null) {
							checkAsn = true;
						}
					}
					if (checkAsn) {
						for (int i = 0; i < asnNo.length; i++) {
							if (!StringUtils.isNumeric(asnNo[i])) {
								log.info("Inside asn***" + asnNo[i]);
								tmpAsn = tmpAsn + " , " + asnNo[i];
							}
						}
						if (tmpAsn != null && !tmpAsn.trim().equalsIgnoreCase("")) {
							errorMessage = tmpAsn + " are invalid Asn. Please check.";
							throw new BusinessException(errorMessage);
						}
						List<String> invalidAsn = gbMiscApplicationService.checkMiscAppOnvAsn(asnNo);
						if (invalidAsn.size() > 0) {
							errorMessage = Arrays.toString(invalidAsn.toArray()) + " are invalid Asn. Please check.";
							throw new BusinessException(errorMessage);
						}
					}

					if ("D".equals(status) || !("D".equals(appStatusCd))) {

						/*
						 * miscAppEjb.updateOvernightParkingVehicleDetails(userId, miscSeqNbr, status,
						 * fromDate, toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName, appType,
						 * account, appStatusCd);
						 */
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						gbMiscApplicationService.updateOvernightParkingVehicleDetails(userId, miscSeqNbr, status,
								fromDate, toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName, appType, account,
								appStatusCd, conPerson, conTel, conEmail);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
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
				} else if (agree != null && agree.equals("true")) {
					status = criteria.getPredicates().get("status");
					fromDate = criteria.getPredicates().get("fromDate");
					toDate = criteria.getPredicates().get("toDate");
					noNights = criteria.getPredicates().get("noNights");
					parkReason = criteria.getPredicates().get("parkReason");
					// vehNo = (String[]) CommonUtil.getRequiredStringParameters(request,"vehNo");
					// cntNo = (String[]) CommonUtil.getRequiredStringParameters(request,"cntNo");
					// asnNo = (String[]) CommonUtil.getRequiredStringParameters(request,"asnNo");
					for (int i = 0; i < Integer.parseInt(size); i++) {
						vehNo[i] = CommonUtility.deNull(criteria.getPredicates().get("vehNo" + i));
						cntNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntNo" + i));
						asnNo[i] = CommonUtility.deNull(criteria.getPredicates().get("asnNo" + i));
					}
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
						throw new BusinessException(errorMessage);
					}

					List<String> invalidContainer = gbMiscApplicationService.checkMiscAppOnvContainer(cntNo);
					if (invalidContainer.size() > 0) {
						errorMessage = Arrays.toString(invalidContainer.toArray())
								+ " are invalid container. Please check.";
						// return;
						throw new BusinessException(errorMessage);
					}
					// Added to not check character ASN
					String tmpAsn = "";

					// added by nasir flage for check asn
					boolean checkAsn = false;
					for (int i = 0; i < asnNo.length; i++) {
						if (asnNo[i] != null) {
							checkAsn = true;
						}
					}
					if (checkAsn) {
						for (int i = 0; i < asnNo.length; i++) {
							if (!StringUtils.isNumeric(asnNo[i])) {
								log.info("Inside asn***" + asnNo[i]);
								tmpAsn = tmpAsn + " , " + asnNo[i];
							}
						}
						if (tmpAsn != null && !tmpAsn.trim().equalsIgnoreCase("")) {
							errorMessage = tmpAsn + " are invalid Asn. Please check.";
							throw new BusinessException(errorMessage);
						}
						List<String> invalidAsn = gbMiscApplicationService.checkMiscAppOnvAsn(asnNo);
						if (invalidAsn.size() > 0) {
							errorMessage = Arrays.toString(invalidAsn.toArray()) + " are invalid Asn. Please check.";
							throw new BusinessException(errorMessage);
						}
					}

					/*
					 * miscAppEjb.updateOvernightParkingVehicleDetails(userId, miscSeqNbr, status,
					 * fromDate, toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName, appType,
					 * account, appStatusCd);
					 */
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					gbMiscApplicationService.updateOvernightParkingVehicleDetails(userId, miscSeqNbr, status, fromDate,
							toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName, appType, account, appStatusCd,
							conPerson, conTel, conEmail);
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					// remove session attributes here
					map.remove("coName");
					map.remove("phone");
					map.remove("add1");
					map.remove("add2");
					map.remove("city");
					map.remove("pin");
					map.remove("account");

					// Application Details
					map.remove("miscSeqNbr");
					map.remove("appTypeCd");
					map.remove("appTypeName");
					map.remove("appStatusCd");
					map.remove("appStatusNm");
					map.remove("appDttm");
					map.remove("submitDttm");
					map.remove("submitBy");
					map.remove("approveDttm");
					map.remove("approveBy");
					map.remove("closeDttm");
					map.remove("closeBy");
					map.remove("remarks");

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

					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateVeh : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateVeh : ", e);
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
			log.info("END: miscAppUpdateVeh result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	public ResponseEntity<?> miscAppUpdateVeh(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppUpdateVeh criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// BEGIN updated by ThangPV, on 02/10/2013: Add validation for TO_TIME <=
			// CUT_OFF_DATE
			// EJBHomeFactory homeFactory = EJBHomeFactory.getInstance();

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

			// Veh Details
			String fromDate = null;
			String toDate = null;
			String noNights = null;
			String parkReason = null;
			String[] vehNo = null;
			String[] cntNo = null;
			String[] asnNo = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
			String conPerson = null;
			String conTel = null;
			String conEmail = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;
			miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			coName = criteria.getPredicates().get("coName");
			appType = criteria.getPredicates().get("appTypeCd");
			account = criteria.getPredicates().get("account");
			appStatusCd = criteria.getPredicates().get("appStatusCd");

			if ("UPDATE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getOvernightParkingVehicleDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
			} else if ("UPDATE_SUBMIT".equals(command)) {
				if (agree == null) {
					status = criteria.getPredicates().get("status");
					fromDate = criteria.getPredicates().get("fromDate");
					toDate = criteria.getPredicates().get("toDate");
					noNights = criteria.getPredicates().get("noNights");
					parkReason = criteria.getPredicates().get("parkReason");
					appType = criteria.getPredicates().get("appTypeCd");
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

					if ("D".equals(status) || !("D".equals(appStatusCd))) {

						/*
						 * miscAppEjb.updateOvernightParkingVehicleDetails(userId, miscSeqNbr, status,
						 * fromDate, toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName, appType,
						 * account, appStatusCd);
						 */
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						gbMiscApplicationService.updateOvernightParkingVehicleDetails(userId, miscSeqNbr, status,
								fromDate, toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName, appType, account,
								appStatusCd, conPerson, conTel, conEmail);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
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
				} else if (agree != null && agree.equals("true")) {
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
					 * miscAppEjb.updateOvernightParkingVehicleDetails(userId, miscSeqNbr, status,
					 * fromDate, toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName, appType,
					 * account, appStatusCd);
					 */
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					gbMiscApplicationService.updateOvernightParkingVehicleDetails(userId, miscSeqNbr, status, fromDate,
							toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName, appType, account, appStatusCd,
							conPerson, conTel, conEmail);
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					// remove session attributes here
					map.remove("coName");
					map.remove("phone");
					map.remove("add1");
					map.remove("add2");
					map.remove("city");
					map.remove("pin");
					map.remove("account");

					// Application Details
					map.remove("miscSeqNbr");
					map.remove("appTypeCd");
					map.remove("appTypeName");
					map.remove("appStatusCd");
					map.remove("appStatusNm");
					map.remove("appDttm");
					map.remove("submitDttm");
					map.remove("submitBy");
					map.remove("approveDttm");
					map.remove("approveBy");
					map.remove("closeDttm");
					map.remove("closeBy");
					map.remove("remarks");

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

					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateVeh : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateVeh : ", e);
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
			log.info("END: miscAppUpdateVeh result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateReeferHandler -->perform
	public ResponseEntity<?> miscAppUpdateReefer(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppUpdateReefer criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount") + ", map: " + map.toString() + ", request: " + request.toString());

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Container Details
			String[] cntrNo = new String[12];
			String[] cntrSize = new String[12];
			String[] cntrStatus = new String[12];
			String[] plugInDt = null;
			String[] plugInTime = null;
			String[] plugOutDt = null;
			String[] plugOutTime = null;
			String[] deliveryDttm = null;
			String[] dnPoNbr = null;
			String[] remarks = null;

			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
			String conPerson = null;
			String conTel = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

			miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			coName = criteria.getPredicates().get("coName");
			appType = criteria.getPredicates().get("appTypeCd");
			account = criteria.getPredicates().get("account");
			appStatusCd = criteria.getPredicates().get("appStatusCd");

			if ("UPDATE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

				List<Object> list = gbMiscApplicationService.getReeferContainerPowerOutletDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);

			} else if ("UPDATE_SUBMIT".equals(command)) {
				if (agree == null) {
					status = criteria.getPredicates().get("status");
					cntrNo = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrNo");
					cntrSize = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrSize");
					cntrStatus = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrStatus");
					plugInDt = (String[]) CommonUtil.getRequiredStringParameters(request, "plugInDt");
					plugInTime = (String[]) CommonUtil.getRequiredStringParameters(request, "plugInTime");
					plugOutDt = (String[]) CommonUtil.getRequiredStringParameters(request, "plugOutDt");
					plugOutTime = (String[]) CommonUtil.getRequiredStringParameters(request, "plugOutTime");
					deliveryDttm = (String[]) CommonUtil.getRequiredStringParameters(request, "deliveryDttm");
					dnPoNbr = (String[]) CommonUtil.getRequiredStringParameters(request, "dnPoNbr");
					remarks = (String[]) CommonUtil.getRequiredStringParameters(request, "remarks");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");

					if ("D".equals(status) || !("D".equals(appStatusCd))) {
						MiscReeferValueObject obj = new MiscReeferValueObject();
						obj.setCntrNo(cntrNo);
						obj.setCntrSize(cntrSize);
						obj.setCntrStatus(cntrStatus);
						obj.setPlugInDt(plugInDt);
						obj.setPlugInTime(plugInTime);
						obj.setPlugOutDt(plugOutDt);
						obj.setPlugOutTime(plugOutTime);
						obj.setDeliveryDttm(deliveryDttm);
						obj.setDnPoNbr(dnPoNbr);
						obj.setRemarks(remarks);

						// miscAppEjb.updateReeferContainerPowerOutletDetails(userId, miscSeqNbr,
						// status, coName, appType, account, appStatusCd, obj);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						gbMiscApplicationService.updateReeferContainerPowerOutletDetails(userId, miscSeqNbr, status,
								coName, appType, account, appStatusCd, obj, conPerson, conTel);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("cntrNo", cntrNo);
						map.put("cntrSize", cntrSize);
						map.put("cntrStatus", cntrStatus);
						/*
						 * map.put("plugInTime", plugInTime); map.put("plugOutDt", plugOutDt);
						 * map.put("plugOutTime", plugOutTime); map.put("deliveryDttm", deliveryDttm);
						 * map.put("dnPoNbr", dnPoNbr); map.put("remarks", remarks);
						 */
						map.put("status", status);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						map.put("conPerson", conPerson);
						map.put("conTel", conTel);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					}
				} else if (agree != null && agree.equals("true")) {
					status = criteria.getPredicates().get("status");
					cntrNo = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrNo");
					cntrSize = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrSize");
					cntrStatus = (String[]) CommonUtil.getRequiredStringParameters(request, "cntrStatus");
					plugInDt = (String[]) CommonUtil.getRequiredStringParameters(request, "plugInDt");
					plugInTime = (String[]) CommonUtil.getRequiredStringParameters(request, "plugInTime");
					plugOutDt = (String[]) CommonUtil.getRequiredStringParameters(request, "plugOutDt");
					plugOutTime = (String[]) CommonUtil.getRequiredStringParameters(request, "plugOutTime");
					deliveryDttm = (String[]) CommonUtil.getRequiredStringParameters(request, "deliveryDttm");
					dnPoNbr = (String[]) CommonUtil.getRequiredStringParameters(request, "dnPoNbr");
					remarks = (String[]) CommonUtil.getRequiredStringParameters(request, "remarks");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					MiscReeferValueObject obj = new MiscReeferValueObject();
					obj.setCntrNo(cntrNo);
					obj.setCntrSize(cntrSize);
					obj.setCntrStatus(cntrStatus);
					obj.setPlugInDt(plugInDt);
					obj.setPlugInTime(plugInTime);
					obj.setPlugOutDt(plugOutDt);
					obj.setPlugOutTime(plugOutTime);
					obj.setDeliveryDttm(deliveryDttm);
					obj.setDnPoNbr(dnPoNbr);
					obj.setRemarks(remarks);

					gbMiscApplicationService.updateReeferContainerPowerOutletDetails(userId, miscSeqNbr, status, coName,
							appType, account, appStatusCd, obj, conPerson, conTel);

					// remove session attributes here
					map.remove("coName");
					map.remove("phone");
					map.remove("add1");
					map.remove("add2");
					map.remove("city");
					map.remove("pin");
					map.remove("account");

					// Application Details
					map.remove("miscSeqNbr");
					map.remove("appTypeCd");
					map.remove("appTypeName");
					map.remove("appStatusCd");
					map.remove("appStatusNm");
					map.remove("appDttm");
					map.remove("submitDttm");
					map.remove("submitBy");
					map.remove("approveDttm");
					map.remove("approveBy");
					map.remove("closeDttm");
					map.remove("closeBy");
					map.remove("remarks");
					map.remove("status");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("conPerson");
					map.remove("conTel");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					// Container Details
					map.remove("cntrNo");
					map.remove("cntrSize");
					map.remove("cntrStatus");
					map.remove("plugInDt");
					map.remove("plugInTime");
					map.remove("plugOutDt");
					map.remove("plugOutTime");
					map.remove("deliveryDttm");
					map.remove("dnPoNbr");
					map.remove("remarks");

					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateReefer : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateReefer : ", e);
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
			log.info("END: miscAppUpdateReefer result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppUpdateReeferHandler -->perform
	@RequestMapping(value = "/miscAppUpdateReefer", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppUpdateReefer(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppUpdateReefer criteria:" + criteria.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String agree = criteria.getPredicates().get("agree");

			// Container Details
			// String[] cntrNo = new String[12];
			// String[] cntrSize = new String[12];
			// String[] cntrStatus = new String[12];
			// String[] plugInDt = null;
			// String[] plugInTime = null;
			// String[] plugOutDt = null;
			// String[] plugOutTime = null;
			// String[] deliveryDttm = null;
			// String[] dnPoNbr = null;
			// String[] remarks = null;

			String size = criteria.getPredicates().get("size");
			String[] cntrNo = new String[Integer.parseInt(size)];
			String[] cntrSize = new String[Integer.parseInt(size)];
			String[] cntrStatus = new String[Integer.parseInt(size)];
			String[] plugInDt = new String[Integer.parseInt(size)];
			String[] plugInTime = new String[Integer.parseInt(size)];
			String[] plugOutDt = new String[Integer.parseInt(size)];
			String[] plugOutTime = new String[Integer.parseInt(size)];
			String[] deliveryDttm = new String[Integer.parseInt(size)];
			String[] dnPoNbr = new String[Integer.parseInt(size)];
			String[] remarks = new String[Integer.parseInt(size)];

			String status = null;
			String miscSeqNbr = null;
			String coName = null;
			String appType, account = null;
			String appStatusCd = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
			String conPerson = null;
			String conTel = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

			miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
			coName = criteria.getPredicates().get("coName");
			appType = criteria.getPredicates().get("appTypeCd");
			account = criteria.getPredicates().get("account");
			appStatusCd = criteria.getPredicates().get("appStatusCd");

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

			if ("UPDATE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getReeferContainerPowerOutletDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);

			} else if ("UPDATE_SUBMIT".equals(command)) {
				if (agree == null) {
					status = criteria.getPredicates().get("status");

					// cntrNo = (String[]) CommonUtil.getRequiredStringParameters(request,"cntrNo");
					// cntrSize = (String[])
					// CommonUtil.getRequiredStringParameters(request,"cntrSize");
					// cntrStatus = (String[])
					// CommonUtil.getRequiredStringParameters(request,"cntrStatus");
					// plugInDt = (String[])
					// CommonUtil.getRequiredStringParameters(request,"plugInDt");
					// plugInTime = (String[])
					// CommonUtil.getRequiredStringParameters(request,"plugInTime");
					// plugOutDt = (String[])
					// CommonUtil.getRequiredStringParameters(request,"plugOutDt");
					// plugOutTime = (String[])
					// CommonUtil.getRequiredStringParameters(request,"plugOutTime");
					// deliveryDttm = (String[])
					// CommonUtil.getRequiredStringParameters(request,"deliveryDttm");
					// dnPoNbr = (String[])
					// CommonUtil.getRequiredStringParameters(request,"dnPoNbr");
					// remarks = (String[])
					// CommonUtil.getRequiredStringParameters(request,"remarks");
					for (int i = 0; i < Integer.parseInt(size); i++) {
						cntrNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrNo" + i));
						cntrSize[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrSize" + i));
						cntrStatus[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrStatus" + i));
						plugInDt[i] = CommonUtility.deNull(criteria.getPredicates().get("plugInDt" + i));
						plugInTime[i] = CommonUtility.deNull(criteria.getPredicates().get("plugInTime" + i));
						plugOutDt[i] = CommonUtility.deNull(criteria.getPredicates().get("plugOutDt" + i));
						plugOutTime[i] = CommonUtility.deNull(criteria.getPredicates().get("plugOutTime" + i));
						deliveryDttm[i] = CommonUtility.deNull(criteria.getPredicates().get("deliveryDttm" + i));
						dnPoNbr[i] = CommonUtility.deNull(criteria.getPredicates().get("dnPoNbr" + i));
						remarks[i] = CommonUtility.deNull(criteria.getPredicates().get("remarks" + i));
					}
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");

					if ("D".equals(status) || !("D".equals(appStatusCd))) {
						MiscReeferValueObject obj = new MiscReeferValueObject();
						obj.setCntrNo(cntrNo);
						obj.setCntrSize(cntrSize);
						obj.setCntrStatus(cntrStatus);
						obj.setPlugInDt(plugInDt);
						obj.setPlugInTime(plugInTime);
						obj.setPlugOutDt(plugOutDt);
						obj.setPlugOutTime(plugOutTime);
						obj.setDeliveryDttm(deliveryDttm);
						obj.setDnPoNbr(dnPoNbr);
						obj.setRemarks(remarks);

						// miscAppEjb.updateReeferContainerPowerOutletDetails(userId, miscSeqNbr,
						// status, coName, appType, account, appStatusCd, obj);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						gbMiscApplicationService.updateReeferContainerPowerOutletDetails(userId, miscSeqNbr, status,
								coName, appType, account, appStatusCd, obj, conPerson, conTel);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						// forwardHandler(request, "MiscAppList");
						// return;
					} else {
						map.put("cntrNo", cntrNo);
						map.put("cntrSize", cntrSize);
						map.put("cntrStatus", cntrStatus);
						/*
						 * map.put("plugInTime", plugInTime); map.put("plugOutDt", plugOutDt);
						 * map.put("plugOutTime", plugOutTime); map.put("deliveryDttm", deliveryDttm);
						 * map.put("dnPoNbr", dnPoNbr); map.put("remarks", remarks);
						 */
						map.put("status", status);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
						map.put("conPerson", conPerson);
						map.put("conTel", conTel);
						// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					}
				} else if (agree != null && agree.equals("true")) {
					status = criteria.getPredicates().get("status");
					// cntrNo = (String[]) CommonUtil.getRequiredStringParameters(request,"cntrNo");
					// cntrSize = (String[])
					// CommonUtil.getRequiredStringParameters(request,"cntrSize");
					// cntrStatus = (String[])
					// CommonUtil.getRequiredStringParameters(request,"cntrStatus");
					// plugInDt = (String[])
					// CommonUtil.getRequiredStringParameters(request,"plugInDt");
					// plugInTime = (String[])
					// CommonUtil.getRequiredStringParameters(request,"plugInTime");
					// plugOutDt = (String[])
					// CommonUtil.getRequiredStringParameters(request,"plugOutDt");
					// plugOutTime = (String[])
					// CommonUtil.getRequiredStringParameters(request,"plugOutTime");
					// deliveryDttm = (String[])
					// CommonUtil.getRequiredStringParameters(request,"deliveryDttm");
					// dnPoNbr = (String[])
					// CommonUtil.getRequiredStringParameters(request,"dnPoNbr");
					// remarks = (String[])
					// CommonUtil.getRequiredStringParameters(request,"remarks");
					for (int i = 0; i < Integer.parseInt(size); i++) {
						cntrNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrNo" + i));
						cntrSize[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrSize" + i));
						cntrStatus[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrStatus" + i));
						plugInDt[i] = CommonUtility.deNull(criteria.getPredicates().get("plugInDt" + i));
						plugInTime[i] = CommonUtility.deNull(criteria.getPredicates().get("plugInTime" + i));
						plugOutDt[i] = CommonUtility.deNull(criteria.getPredicates().get("plugOutDt" + i));
						plugOutTime[i] = CommonUtility.deNull(criteria.getPredicates().get("plugOutTime" + i));
						deliveryDttm[i] = CommonUtility.deNull(criteria.getPredicates().get("deliveryDttm" + i));
						dnPoNbr[i] = CommonUtility.deNull(criteria.getPredicates().get("dnPoNbr" + i));
						remarks[i] = CommonUtility.deNull(criteria.getPredicates().get("remarks" + i));
					}
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					MiscReeferValueObject obj = new MiscReeferValueObject();
					obj.setCntrNo(cntrNo);
					obj.setCntrSize(cntrSize);
					obj.setCntrStatus(cntrStatus);
					obj.setPlugInDt(plugInDt);
					obj.setPlugInTime(plugInTime);
					obj.setPlugOutDt(plugOutDt);
					obj.setPlugOutTime(plugOutTime);
					obj.setDeliveryDttm(deliveryDttm);
					obj.setDnPoNbr(dnPoNbr);
					obj.setRemarks(remarks);

					gbMiscApplicationService.updateReeferContainerPowerOutletDetails(userId, miscSeqNbr, status, coName,
							appType, account, appStatusCd, obj, conPerson, conTel);

					// remove session attributes here
					map.remove("coName");
					map.remove("phone");
					map.remove("add1");
					map.remove("add2");
					map.remove("city");
					map.remove("pin");
					map.remove("account");

					// Application Details
					map.remove("miscSeqNbr");
					map.remove("appTypeCd");
					map.remove("appTypeName");
					map.remove("appStatusCd");
					map.remove("appStatusNm");
					map.remove("appDttm");
					map.remove("submitDttm");
					map.remove("submitBy");
					map.remove("approveDttm");
					map.remove("approveBy");
					map.remove("closeDttm");
					map.remove("closeBy");
					map.remove("remarks");
					map.remove("status");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					map.remove("conPerson");
					map.remove("conTel");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					// Container Details
					map.remove("cntrNo");
					map.remove("cntrSize");
					map.remove("cntrStatus");
					map.remove("plugInDt");
					map.remove("plugInTime");
					map.remove("plugOutDt");
					map.remove("plugOutTime");
					map.remove("deliveryDttm");
					map.remove("dnPoNbr");
					map.remove("remarks");

					// forwardHandler(request, "MiscAppList");
					// return;
				}
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppUpdateReefer : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppUpdateReefer : ", e);
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
			log.info("END: miscAppUpdateReefer result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// UPDATE END

	// process start
	// delegate.helper.gbms.miscApp --> MiscAppVoidHandler -->perform
	public ResponseEntity<?> miscAppVoid(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppVoid criteria:" + criteria.toString() + ", map: " + map.toString());

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
					log.info("Void Use of Space: application reference number= " + appRefNbr);
					// Inactive for valid plan from SMART system.
					String url = smartBaseUrl + "/checkValidUseOfSpacePlan" + "?userId=" + userId + "&refNbr=" + appRefNbr + "&isInactivate=" + true;
					log.info("[checkValidUseOfSpacePlan] Calling SMART service URL= " + url);
					smartServiceRestClient.checkValidUseOfSpacePlan(userId, appRefNbr,true);
				} catch (Exception ex) {
					log.info("Call SMART Interface to Inactive for valid plan is not successfully: Application reference number = " + appRefNbr);
					log.info("Exception miscAppVoid : ", ex);
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
			map.put("func", "VOID_SUBMIT");

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
	// process end

	// Start added for SMART CR by FPT on 25-Feb-2014
	/**
	 * Get the current date in dd/MM/yyyy HH:mm format for Email purpose.
	 */
	public static String getCurrentDate() {
		String currentDate = "";
		DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			currentDate = df1.format(new Date());
		} catch (Exception e) {
			log.info("Exception getCurrentDate : ", e);
		}
		return currentDate;
	}

	// START process Approve
	// delegate.helper.gbms.miscApp --> MiscAppApproveBargeHandler -->perform
	public ResponseEntity<?> miscAppApproveBarge(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppApproveBarge criteria:" + criteria.toString() + ", map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			if ("APPROVE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getParkingOfLineTowBargeDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppApproveBarge");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String berthNo = criteria.getPredicates().get("berthNo");
				String wharfMarkFr = criteria.getPredicates().get("wharfMarkFr");
				String wharfMarkTo = criteria.getPredicates().get("wharfMarkTo");

				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");

				MiscBargeValueObject obj = new MiscBargeValueObject();
				obj.setAllocBerthNo(berthNo);
				obj.setWharfMarkFr(wharfMarkFr);
				obj.setWharfMarkTo(wharfMarkTo);

				gbMiscApplicationService.approveBargeApplication(userId, applicationStatus, miscSeqNbr, remarks, obj,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveBarge : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveBarge : ", e);
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
			log.info("END: miscAppApproveBarge result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveBargeHandler -->perform
	@RequestMapping(value = "/miscAppApproveBarge", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppApproveBarge(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppApproveBarge criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

			if ("APPROVE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getParkingOfLineTowBargeDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppApproveBarge");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String berthNo = criteria.getPredicates().get("berthNo");
				String wharfMarkFr = criteria.getPredicates().get("wharfMarkFr");
				String wharfMarkTo = criteria.getPredicates().get("wharfMarkTo");

				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");

				MiscBargeValueObject obj = new MiscBargeValueObject();
				obj.setAllocBerthNo(berthNo);
				obj.setWharfMarkFr(wharfMarkFr);
				obj.setWharfMarkTo(wharfMarkTo);

				gbMiscApplicationService.approveBargeApplication(userId, applicationStatus, miscSeqNbr, remarks, obj,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveBarge : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveBarge : ", e);
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
			log.info("END: miscAppApproveBarge result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveContractHandler -->perform
	public ResponseEntity<?> miscAppApproveContract(Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppApproveContract criteria:" + criteria.toString() + ", map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			if ("APPROVE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getContractorPermitDetails(userId, applyType, appSeqNbr,
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
				// nextScreen(request, "MiscAppApproveContract");
			}
			if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				// Added by Punitha on 06/05/2008.
				String waivePermit = CommonUtility.deNull(criteria.getPredicates().get("waiver"));
				String reasonWaive = CommonUtility.deNull(criteria.getPredicates().get("reasonWaive"));
				log.info("Waiver Permit Indicator :" + waivePermit);
				log.info("Reason for Waive :" + reasonWaive);
				String waiveInd = "";
				if (waivePermit.equalsIgnoreCase("Y"))
					waiveInd = "Y";
				else
					waiveInd = "N";

				// miscAppEjb.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
				// approveDate);
				gbMiscApplicationService.approveContractApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate, waiveInd, reasonWaive);
				// End
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveContract : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveContract : ", e);
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
			log.info("END: miscAppApproveContract result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveContractHandler -->perform
	@RequestMapping(value = "/miscAppApproveContract", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppApproveContract(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppApproveContract criteria:" + criteria.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			if ("APPROVE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getContractorPermitDetails(userId, applyType, appSeqNbr,
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
				// nextScreen(request, "MiscAppApproveContract");
			}
			if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				// Added by Punitha on 06/05/2008.
				String waivePermit = CommonUtility.deNull(criteria.getPredicates().get("waiver"));
				String reasonWaive = CommonUtility.deNull(criteria.getPredicates().get("reasonWaive"));
				log.info("Waiver Permit Indicator :" + waivePermit);
				log.info("Reason for Waive :" + reasonWaive);
				String waiveInd = "";
				if (waivePermit.equalsIgnoreCase("Y"))
					waiveInd = "Y";
				else
					waiveInd = "N";

				// miscAppEjb.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
				// approveDate);
				gbMiscApplicationService.approveContractApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate, waiveInd, reasonWaive);
				// End
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveContract : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveContract : ", e);
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
			log.info("END: miscAppApproveContract result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveHotworkHandler -->perform
	public ResponseEntity<?> miscAppApproveHotwork(Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppApproveHotwork criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			if ("APPROVE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getHotworkDetails(userId, applyType, appSeqNbr,
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
				// nextScreen(request, "MiscAppApproveHotwork");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveHotwork : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveHotwork : ", e);
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
			log.info("END: miscAppApproveHotwork result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	@RequestMapping(value = "/miscAppApproveHotwork", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppApproveHotwork(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppApproveHotwork criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("APPROVE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getHotworkDetails(userId, applyType, appSeqNbr,
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
				// nextScreen(request, "MiscAppApproveHotwork");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveHotwork : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveHotwork : ", e);
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
			log.info("END: miscAppApproveHotwork result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveParkMacHandler -->perform
	public ResponseEntity<?> miscAppApproveParkMac(Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppApproveParkMac criteria:" + criteria.toString() + ", map: " + map.toString() );
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			if ("APPROVE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getParkingOfForkliftShorecrane(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppApproveParkMac");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveParkMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveParkMac : ", e);
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
			log.info("END: miscAppApproveParkMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	@RequestMapping(value = "/miscAppApproveParkMac", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppApproveParkMac(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppApproveParkMac criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("APPROVE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getParkingOfForkliftShorecrane(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppApproveParkMac");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveParkMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveParkMac : ", e);
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
			log.info("END: miscAppApproveParkMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveReeferHandler -->perform
	public ResponseEntity<?> miscAppApproveReefer(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppApproveReefer criteria:" + criteria.toString() + ", map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			if ("APPROVE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getReeferContainerPowerOutletDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppApproveReefer");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveReefer : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveReefer : ", e);
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
			log.info("END: miscAppApproveReefer result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveReeferHandler -->perform
	@RequestMapping(value = "/miscAppApproveReefer", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppApproveReefer(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppApproveReefer criteria:" + criteria.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

			if ("APPROVE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getReeferContainerPowerOutletDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppApproveReefer");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveReefer : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveReefer : ", e);
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
			log.info("END: miscAppApproveReefer result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveSpaceHandler -->perform
	public ResponseEntity<?> miscAppApproveSpace(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppApproveSpace criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			// Added by Punitha on 19/11/2009
			String bayNbrNew = null;
			String areaUsedNew = null;
			String fromDateNew = null;
			String fromTimeNew = null;
			String toDateNew = null;
			String toTimeNew = null;

			List<String> bayNbrList = null;
			List<String> areaUsedList = null;
			List<String> opsStartDttmList = null;
			List<String> opsEndDttmList = null;
			// Start added for SMART CR by FPT on 25-Feb-2014
			List<String> inSmartIndList = null;

			Object listObject = null;
			listObject = request.getAttribute("bayNbrList");
			bayNbrList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						bayNbrList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("areaUsedList");
			areaUsedList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						areaUsedList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("opsStartDttmList");
			opsStartDttmList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						opsStartDttmList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("opsEndDttmList");
			opsEndDttmList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						opsEndDttmList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("inSmartIndList");
			inSmartIndList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						inSmartIndList.add((String) item);
					}
				}
			}

			// End

			String locType = "";
			String berthNbr = "";
			String stgZone = "";
			String stgName = "";

			if ("APPROVE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				String refNo = criteria.getPredicates().get("refNo");
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  userId --> " + userId);
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  applyType --> " + applyType);
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  appSeqNbr --> " + appSeqNbr);
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  applyTypeNm --> " + applyTypeNm);

				List<Object> list = gbMiscApplicationService.getUseOfSpaceDetails(userId, applyType, appSeqNbr,
						applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
					log.info("~~~~ MiscAppApproveSpaceHandler ~~~~ list.size() --> " + list.size());

					// Start added for SMART CR by FPT on 24-Feb-2014
					String appRefNbr = appObj.getAppRefNbr();
					log.info("***********Before SMART interface calling for fetch Operation Details (location info): Application reference number : "+ appRefNbr);
					List<SmartInterfaceOutputVO> operationDetailsLst = null;
					try {
						MiscSpaceValueObject space = (MiscSpaceValueObject) list.get(2);
						String url = smartBaseUrl + "/getJpAppStgLocByRefNbr" + "?planType=" + SmartInterfaceConstants.SPA_MICRO_PLAN_TYPE + "&appRefNbr=" + appRefNbr;
						log.info("[getJpAppStgLocByRefNbr] Calling SMART service URL= " + url);
						operationDetailsLst =  smartServiceRestClient.getJpAppStgLocByRefNbr(SmartInterfaceConstants.SPA_MICRO_PLAN_TYPE, appRefNbr);
						bayNbrList = new ArrayList<String>();
						areaUsedList = new ArrayList<String>();
						opsStartDttmList = new ArrayList<String>();
						opsEndDttmList = new ArrayList<String>();
						inSmartIndList = new ArrayList<String>();
						if (operationDetailsLst != null && operationDetailsLst.size() > 0) {
							for (int i = 0; i < operationDetailsLst.size(); i++) {
								SmartInterfaceOutputVO operationDetailsObj = (SmartInterfaceOutputVO) operationDetailsLst
										.get(i);
								bayNbrList.add(CommonUtility.deNull(operationDetailsObj.getStgName()));
								areaUsedList.add(String.valueOf(operationDetailsObj.getArea()));
								opsStartDttmList.add(CommonUtility.deNull(operationDetailsObj.getOpsStartDttm()));
								opsEndDttmList.add(CommonUtility.deNull(operationDetailsObj.getOpsEndDttm()));
								inSmartIndList.add("1");
							}
							space.setAreaUsed((String[]) areaUsedList.toArray(new String[0]));
							space.setBayNbr((String[]) bayNbrList.toArray(new String[0]));
							space.setOpsStartDttm((String[]) opsStartDttmList.toArray(new String[0]));
							space.setOpsEndDttm((String[]) opsEndDttmList.toArray(new String[0]));
							list.set(2, space);
						} else {
							String bayNbrArr[] = space.getBayNbr();
							if (bayNbrArr != null && bayNbrArr.length > 0) {
								String areaUsedArr[] = space.getAreaUsed();
								String opsStartDttmArr[] = space.getOpsStartDttm();
								String opsEndDttmArr[] = space.getOpsEndDttm();
								for (int i = 0; i < bayNbrArr.length; i++) {
									bayNbrList.add(CommonUtility.deNull(bayNbrArr[i]));
									areaUsedList.add(CommonUtility.deNull(areaUsedArr[i]));
									opsStartDttmList.add(CommonUtility.deNull(opsStartDttmArr[i]));
									opsEndDttmList.add(CommonUtility.deNull(opsEndDttmArr[i]));
									inSmartIndList.add("0");
								}
							}

						}
					} catch (Exception ex) {
						log.info("Call SMART Interface to fetch Operation Details is not successfully: Application reference number = " + appRefNbr);
						log.info("Exception miscAppApproveSpace : ", ex);
					}
					log.info("***********After SMART interface calling");
					// End added for SMART CR by FPT on 24-Feb-2014

				}
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  appObj.getVarCode() --> " + appObj.getVarCode());

				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);

				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10

				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  appSeqNbr --> " + appSeqNbr);
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  refNo --> " + refNo);

				List<StorageOrderValueObject> cntrList = gbMiscApplicationService.getMotCntrList(refNo, appSeqNbr);
				List<StorageOrderValueObject> cntrSummary = gbMiscApplicationService.getMotCntrSummary(refNo,
						appSeqNbr);

				log.info("=============== cntrList.size(): " + cntrList.size());
				log.info("=============== cntrSummary.size(): " + cntrSummary.size());

				map.put("cntrList", cntrList);
				map.put("cntrSummary", cntrSummary);

			} else if ("GET_DETAILS".equals(command)) {
				locType = criteria.getPredicates().get("locType");
				log.info("=============== locType: " + locType);
				// Start modify for SMART CR by FPT on 23-Feb-2014
				List<?> detailsList = null;
				if ("B".equals(locType)) {
					// "Berth" will be fetch from CSPS. Because it does not exist in SMART system.
					detailsList = gbMiscApplicationService.getLocationListBasedOnLocationType(locType);
				} else {
					log.info("*********Before SMART interface calling for fetch Zone by Location Type: Location Type= " + locType);
					try {
						String url = smartBaseUrl + "/getLocListBasedOnLocType?locType=" + locType;
						log.info("[getLocListBasedOnLocType] Calling SMART service URL= " + url);
						detailsList = smartServiceRestClient.getLocListBasedOnLocType(locType);
					} catch (Exception ex) {
						log.info("Call SMART Interface for fetch Zone is not sucessfully: Location Type =" + locType);
						log.info("Exception miscAppApproveSpace : ", ex);
					}
					log.info("*********After SMART interface calling");
				}
				// End modify for SMART CR by FPT on 23-Feb-2014

				map.put("detailsList", detailsList);
				map.put("locType", locType);
			} else if ("GET_STAGE".equals(command)) {
				locType = criteria.getPredicates().get("locType");
				stgZone = request.getParameter("stgZone");
				log.info("=============== locType: " + locType);
				log.info("=============== stgZone: " + stgZone);
				// Start modified for SMART CR by FPT on 23-Feb-2014
				List<?> stageList = null;
				if ("B".equals(locType)) {
					stageList = gbMiscApplicationService.getAreaListBasedOnStorageZone(locType, stgZone);
				} else {
					log.info("Before SMART interface calling to fetch Storage Area by Location Type is [" + locType + "] and Zone is [" + stgZone + "]");
					try {
						String url = smartBaseUrl + "/getLocListBasedOnStorageZone" + "?stgType=" + locType + "&stgZone=" + stgZone;
						log.info("[getLocListBasedOnStorageZone] Calling SMART service URL= " + url);
						stageList = smartServiceRestClient.getLocListBasedOnStorageZone(locType, stgZone);
					} catch (Exception ex) {
						log.info("Call SMART Interface to fetch Storage Area is not successfully: Location Type is [" + locType + "] and Zone is [" + stgZone + "]");
						log.info("Exception miscAppApproveSpace : ", ex);
					}
					log.info("After SMART interface calling");
				}
				// End modified for SMART CR by FPT on 23-Feb-2014

				map.put("stageList", stageList);
				map.put("locType", locType);
				map.put("stgZone", stgZone);
				map.put("stgSelectZone", stgZone);
				// Added by Punitha on 19/11/2009
			} else if ("ADD_OPS_SUBMIT".equals(command)) {

				berthNbr = criteria.getPredicates().get("berthNbr");
				locType = criteria.getPredicates().get("locType");
				stgZone = criteria.getPredicates().get("stgZone");
				stgName = criteria.getPredicates().get("stgName");

				log.info("=============== berthNbr: " + berthNbr);
				log.info("=============== stgName: " + stgName);
				log.info("=============== locType: " + locType);
				log.info("=============== stgZone: " + stgZone);

				// Start modified for SMART CR by FPT on 23-Feb-2013
				boolean isFromSMART = false;
				inSmartIndList = new ArrayList<String>();
				Object inSmartInd = criteria.getPredicates().get("inSmartInd");
				if (inSmartInd != null) {
					String temp[] = (String[]) inSmartInd;
					for (int i = 0; i < temp.length; i++) {
						inSmartIndList.add(temp[i]);
						if ("1".equals(temp[i])) {
							isFromSMART = true;
						}

					}
				}
				if (!isFromSMART) {
					inSmartIndList.add("0");
					map.put("inSmartInd", inSmartIndList.toArray(new String[0]));
					if (locType.equalsIgnoreCase("B"))
						bayNbrNew = locType + "_" + berthNbr;
					else if (locType.equalsIgnoreCase("Y") || locType.equalsIgnoreCase("W"))
						bayNbrNew = locType + "_" + stgName;

					// bayNbrNew = request.getParameter("bayNbr");
					areaUsedNew = criteria.getPredicates().get("areaUsed");
					fromDateNew = criteria.getPredicates().get("fromDate");
					fromTimeNew = criteria.getPredicates().get("fromTime");
					toDateNew = criteria.getPredicates().get("toDate");
					toTimeNew = criteria.getPredicates().get("toTime");
					if (bayNbrList != null) {
						bayNbrList.add(bayNbrNew);
					}
					if (areaUsedList != null) {
						areaUsedList.add(areaUsedNew);
					} 
					if (opsStartDttmList != null) {
						opsStartDttmList.add(fromDateNew + " " + fromTimeNew);
					} 
					if (opsEndDttmList != null) {
						opsEndDttmList.add(toDateNew + " " + toTimeNew);
					} 
					// Added by Punitha on 19/11/2009
					if (bayNbrList != null)
						map.put("bayNbr", bayNbrList.toArray(new String[0]));
					if (areaUsedList != null)
						map.put("areaUsed", areaUsedList.toArray(new String[0]));
					if (opsStartDttmList != null)
						map.put("opsStartDttm", opsStartDttmList.toArray(new String[0]));
					if (opsEndDttmList != null)
						map.put("opsEndDttm", opsEndDttmList.toArray(new String[0]));
				}

				map.remove("stageList");
				map.remove("detailsList");
				map.remove("locType");
				map.remove("stgZone");
				map.remove("stgSelectZone");

				// End modified for SMART CR by FPT on 23-Feb-2013
			} else if ("DELETE_OPS".equals(command)) {
				String[] delList = CommonUtil.getRequiredStringParameters(request, "opsCheck");

				// Start added for SMART CR by FPT on 23-Jan-2014
				inSmartIndList = new ArrayList<String>();
				Object inSmartInd = criteria.getPredicates().get("inSmartInd");
				if (inSmartInd != null) {
					String temp[] = (String[]) inSmartInd;
					for (int i = 0; i < temp.length; i++) {
						inSmartIndList.add(temp[i]);
					}
				}
				// End added for SMART CR by FPT on 23-Jan-2014

				for (int i = 0; i < delList.length; i++) {
					String ind = delList[i];
					// Start added for SMART CR by FPT on 23-Jan-2014
					if ("1".equals(inSmartIndList.get(Integer.parseInt(ind)))) {
						errorMessage = "You should delete this Operation Details from SMART system.";
						// return;
					}
					inSmartIndList.remove(Integer.parseInt(ind));
					// End added for SMART CR by FPT on 23-Jan-2014

					bayNbrList.remove(Integer.parseInt(ind));
					areaUsedList.remove(Integer.parseInt(ind));
					opsStartDttmList.remove(Integer.parseInt(ind));
					opsEndDttmList.remove(Integer.parseInt(ind));
				}

			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				// Added by Punitha on 19/11/2009
				MiscSpaceValueObject obj = new MiscSpaceValueObject();
				obj.setBayNbr((String[]) CommonUtil.getRequiredStringParameters(request, "bayNbr"));

				obj.setAreaUsed((String[]) CommonUtil.getRequiredStringParameters(request, "areaUsed"));
				obj.setOpsStartDttm((String[]) CommonUtil.getRequiredStringParameters(request, "opsStartDttm"));
				obj.setOpsEndDttm((String[]) CommonUtil.getRequiredStringParameters(request, "opsEndDttm"));
				// miscAppEjb.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
				// approveDate);

				log.info("=============== userId: " + userId);
				log.info("=============== miscSeqNbr: " + miscSeqNbr);
				log.info("=============== closeDate: " + approveDate);

				gbMiscApplicationService.approveSpaceApplication(userId, applicationStatus, miscSeqNbr, remarks, obj,
						approveDate);
				// Start added for SMART CR by FPT on 25-Feb-2014
				String appRefNbr = criteria.getPredicates().get("appRefNbr");
				if ("R".equals(applicationStatus)) {
					log.info("**********Before SMART interface calling to Inactive for valid plan of application: application reference number: " + appRefNbr);
					try {
						// Inactive for valid plan from SMART system.
						String url = smartBaseUrl + "/checkValidUseOfSpacePlan" + "?userId=" + userId + "&refNbr=" + appRefNbr + "&isInactivate=" + true;
						log.info("[checkValidUseOfSpacePlan] Calling SMART service URL= " + url);
						smartServiceRestClient.checkValidUseOfSpacePlan(userId, appRefNbr,true);
					} catch (Exception ex) {
						log.info("Call SMART Interface to Inactive for valid plan is not successfully: Application reference number = " + appRefNbr);
						log.info("Exception miscAppApproveSpace : ", ex);
					}
					log.info("**********After SMART interface calling");
					// SpaceAppApproved

					// Send email for JP staff: emailMessage, emailSubject get from email template.
				
						String sender = SpaceAppApproved_from;
						String emailSubject = SpaceAppApproved_subject;
						String fileNameSpaceApproved = SpaceAppApproved_body_template;
						String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), fileNameSpaceApproved);
						Map<String, String> emailInputData = new HashMap<String, String>();
						
						emailSubject = StringUtils.replace(emailSubject, "<ApplicationNumber>", appRefNbr);
						emailInputData.put("ApplicationNumber", appRefNbr);
						emailInputData.put("Updater", userId);
						emailInputData.put("TimeofRejection", getCurrentDate());
	
						String emailMessage = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
						
						log.info("Space App Approve, send email: email subject=\n" + emailSubject);
						log.info("Space App Approve, send email: email message=\n" + emailMessage);
						gbMiscApplicationService.sendFlexiAlert("MSR", "", emailMessage, emailSubject, sender, null);
					
				}
				// End added for SMART CR by FPT on 25-Feb-2014

				// Application Details
				map.remove("miscSeqNbr");
				map.remove("appTypeCd");
				map.remove("appTypeName");
				map.remove("appStatusCd");
				map.remove("appStatusNm");
				map.remove("appDttm");
				map.remove("submitDttm");
				map.remove("submitBy");
				map.remove("approveDttm");
				map.remove("approveBy");
				map.remove("closeDttm");
				map.remove("closeBy");
				map.remove("remarks");
				map.remove("status");
				// Space Details
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
				map.remove("bayNbr");
				map.remove("areaUsed");
				map.remove("opsStartDttm");
				map.remove("opsEndDttm");
				map.remove("bayNbrList");
				map.remove("areaUsedList");
				map.remove("opsStartDttmList");
				map.remove("opsEndDttmList");
				// Added by Punitha on 24/08/2007 . To hold the vessel details in session
				map.remove("vslName");
				map.remove("varCode");
				map.remove("inVoyNbr");
				map.remove("outVoyNbr");
				map.remove("atbDttm");
				map.remove("atuDttm");

				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10

				map.remove("seqNo");
				map.remove("cntrSeqNo");
				map.remove("cntrNo");
				map.remove("status");
				map.remove("iso");
				map.remove("len");
				map.remove("ht");
				map.remove("wt");
				map.remove("impHaulier");
				map.remove("expHaulier");
				map.remove("impTruckNbr");
				map.remove("expTruckNbr");
				map.remove("impGateOut");
				map.remove("expGateIn");
				map.remove("miscAppNo");
				map.remove("arrvStat");
				map.remove("dateCreate");
				map.remove("remarks");
				map.remove("sumTotal");
				map.remove("sumLen");
				// End
				// forwardHandler(request, "MiscAppList");
				// return;
			}
			// Added by Punitha on 19/11/2009
			if (bayNbrList != null)
				map.put("bayNbr", bayNbrList.toArray(new String[0]));
			if (areaUsedList != null)
				map.put("areaUsed", areaUsedList.toArray(new String[0]));
			if (opsStartDttmList != null)
				map.put("opsStartDttm", opsStartDttmList.toArray(new String[0]));
			if (opsEndDttmList != null)
				map.put("opsEndDttm", opsEndDttmList.toArray(new String[0]));
			// Start added for SMART CR by FPT on 25-Feb-2014
			if (inSmartIndList != null)
				map.put("inSmartInd", inSmartIndList.toArray(new String[0]));
			// End added for SMART CR by FPT on 25-Feb-2014
			// End

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveSpace : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveSpace : ", e);
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
			log.info("END: miscAppApproveSpace result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveSpaceHandler -->perform
	@RequestMapping(value = "/miscAppApproveSpace", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppApproveSpace(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppApproveSpace criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			// Added by Punitha on 19/11/2009
			String bayNbrNew = null;
			String areaUsedNew = null;
			String fromDateNew = null;
			String fromTimeNew = null;
			String toDateNew = null;
			String toTimeNew = null;

			List<String> bayNbrList = null;
			List<String> areaUsedList = null;
			List<String> opsStartDttmList = null;
			List<String> opsEndDttmList = null;
			// Start added for SMART CR by FPT on 25-Feb-2014
			// ArrayList inSmartIndList = request.getAttribute("inSmartIndList")
			// ;
			List<String> inSmartIndList = null;

			if (criteria.getPredicates().get("inSmartIndList") != ""
					&& criteria.getPredicates().get("inSmartIndList") != null) {
				inSmartIndList = new ArrayList<String>();
				String[] tempInSmartIndList = criteria.getPredicates().get("inSmartIndList").split(",");
				for (int i = 0; i < tempInSmartIndList.length; i++) {
					inSmartIndList.add(tempInSmartIndList[i]);
				}
			}
			// End added for SMART CR by FPT on 25-Feb-2014

			if (criteria.getPredicates().get("bayNbrList") != ""
					&& criteria.getPredicates().get("bayNbrList") != null) {
				bayNbrList = new ArrayList<String>();
				String[] tempBayNbrList = criteria.getPredicates().get("bayNbrList").split(",");
				for (int i = 0; i < tempBayNbrList.length; i++) {
					bayNbrList.add(tempBayNbrList[i]);
				}
			}

			if (criteria.getPredicates().get("areaUsedList") != ""
					&& criteria.getPredicates().get("areaUsedList") != null) {
				areaUsedList = new ArrayList<String>();
				String[] tempAreaUsedList = criteria.getPredicates().get("areaUsedList").split(",");
				for (int i = 0; i < tempAreaUsedList.length; i++) {
					areaUsedList.add(tempAreaUsedList[i]);
				}
			}

			if (criteria.getPredicates().get("opsStartDttmList") != ""
					&& criteria.getPredicates().get("opsStartDttmList") != null) {
				opsStartDttmList = new ArrayList<String>();
				String[] tempOpsStartDttmList = criteria.getPredicates().get("opsStartDttmList").split(",");
				for (int i = 0; i < tempOpsStartDttmList.length; i++) {
					opsStartDttmList.add(tempOpsStartDttmList[i]);
				}
			}

			if (criteria.getPredicates().get("opsEndDttmList") != ""
					&& criteria.getPredicates().get("opsEndDttmList") != null) {
				opsEndDttmList = new ArrayList<String>();
				String[] tempOpsEndDttmList = criteria.getPredicates().get("opsEndDttmList").split(",");
				for (int i = 0; i < tempOpsEndDttmList.length; i++) {
					opsEndDttmList.add(tempOpsEndDttmList[i]);
				}
			}

			// End

			String locType = "";
			String berthNbr = "";
			String stgZone = "";
			String stgName = "";

			if ("APPROVE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				String refNo = criteria.getPredicates().get("refNo");
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  userId --> " + userId);
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  applyType --> " + applyType);
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  appSeqNbr --> " + appSeqNbr);
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  applyTypeNm --> " + applyTypeNm);

				List<Object> list = gbMiscApplicationService.getUseOfSpaceDetails(userId, applyType, appSeqNbr,
						applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
					log.info("~~~~ MiscAppApproveSpaceHandler ~~~~ list.size() --> " + list.size());

					// Start added for SMART CR by FPT on 24-Feb-2014
					String appRefNbr = appObj.getAppRefNbr();
					log.info("***********Before SMART interface calling for fetch Operation Details (location info): Application reference number: " + appRefNbr);
					List<SmartInterfaceOutputVO> operationDetailsLst = null;
					try {
						MiscSpaceValueObject space = (MiscSpaceValueObject) list.get(2);
						String url = smartBaseUrl + "/getJpAppStgLocByRefNbr" + "?planType=" + SmartInterfaceConstants.SPA_MICRO_PLAN_TYPE + "&appRefNbr=" + appRefNbr;
						log.info("[getJpAppStgLocByRefNbr] Calling SMART service URL= " + url);
						operationDetailsLst =  smartServiceRestClient.getJpAppStgLocByRefNbr(SmartInterfaceConstants.SPA_MICRO_PLAN_TYPE, appRefNbr);
						bayNbrList = new ArrayList<String>();
						areaUsedList = new ArrayList<String>();
						opsStartDttmList = new ArrayList<String>();
						opsEndDttmList = new ArrayList<String>();
						inSmartIndList = new ArrayList<String>();
						if (operationDetailsLst != null && operationDetailsLst.size() > 0) {
							for (int i = 0; i < operationDetailsLst.size(); i++) {
								SmartInterfaceOutputVO operationDetailsObj = (SmartInterfaceOutputVO) operationDetailsLst
										.get(i);
								bayNbrList.add(CommonUtility.deNull(operationDetailsObj.getStgName()));
								areaUsedList.add(String.valueOf(operationDetailsObj.getArea()));
								opsStartDttmList.add(CommonUtility.deNull(operationDetailsObj.getOpsStartDttm()));
								opsEndDttmList.add(CommonUtility.deNull(operationDetailsObj.getOpsEndDttm()));
								inSmartIndList.add("1");
							}
							space.setAreaUsed((String[]) areaUsedList.toArray(new String[0]));
							space.setBayNbr((String[]) bayNbrList.toArray(new String[0]));
							space.setOpsStartDttm((String[]) opsStartDttmList.toArray(new String[0]));
							space.setOpsEndDttm((String[]) opsEndDttmList.toArray(new String[0]));
							list.set(2, space);
						} else {
							String bayNbrArr[] = space.getBayNbr();
							if (bayNbrArr != null && bayNbrArr.length > 0) {
								String areaUsedArr[] = space.getAreaUsed();
								String opsStartDttmArr[] = space.getOpsStartDttm();
								String opsEndDttmArr[] = space.getOpsEndDttm();
								for (int i = 0; i < bayNbrArr.length; i++) {
									bayNbrList.add(CommonUtility.deNull(bayNbrArr[i]));
									areaUsedList.add(CommonUtility.deNull(areaUsedArr[i]));
									opsStartDttmList.add(CommonUtility.deNull(opsStartDttmArr[i]));
									opsEndDttmList.add(CommonUtility.deNull(opsEndDttmArr[i]));
									inSmartIndList.add("0");
								}
							}

						}
					} catch (Exception ex) {
						log.info("Call SMART Interface to fetch Operation Details is not successfully: Application reference number = " + appRefNbr);
						log.info("Exception miscAppApproveSpace : ", ex);
					}
					log.info("***********After SMART interface calling");
					// End added for SMART CR by FPT on 24-Feb-2014

				}
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  appObj.getVarCode() --> " + appObj.getVarCode());

				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);

				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10

				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  appSeqNbr --> " + appSeqNbr);
				log.info("~~~~ MiscAppApproveSpaceHandler ~~~~  refNo --> " + refNo);

				List<StorageOrderValueObject> cntrList = gbMiscApplicationService.getMotCntrList(refNo, appSeqNbr);
				List<StorageOrderValueObject> cntrSummary = gbMiscApplicationService.getMotCntrSummary(refNo,
						appSeqNbr);

				log.info("=============== cntrList.size(): " + cntrList.size());
				log.info("=============== cntrSummary.size(): " + cntrSummary.size());

				map.put("cntrList", cntrList);
				map.put("cntrSummary", cntrSummary);

			} else if ("GET_DETAILS".equals(command)) {
				locType = criteria.getPredicates().get("locType");
				log.info("=============== locType: " + locType);
				// Start modify for SMART CR by FPT on 23-Feb-2014
				List<?> detailsList = null;
				if ("B".equals(locType)) {
					// "Berth" will be fetch from CSPS. Because it does not exist in SMART system.
					detailsList = gbMiscApplicationService.getLocationListBasedOnLocationType(locType);
				} else {
					log.info("*********Before SMART interface calling for fetch Zone by Location Type: Location Type= " + locType);
					try {
						String url = smartBaseUrl + "/getLocListBasedOnLocType?locType=" + locType;
						log.info("[getLocListBasedOnLocType] Calling SMART service URL= " + url);
						detailsList = smartServiceRestClient.getLocListBasedOnLocType(locType);
					} catch (Exception ex) {
						log.info("Call SMART Interface for fetch Zone is not sucessfully: Location Type= " + locType);
						log.info("Exception miscAppApproveSpace : ", ex);
					}
					log.info("*********After SMART interface calling");
				}
				// End modify for SMART CR by FPT on 23-Feb-2014

				map.put("detailsList", detailsList);
				map.put("locType", locType);
			} else if ("GET_STAGE".equals(command)) {
				locType = criteria.getPredicates().get("locType");
				stgZone = request.getParameter("stgZone");
				log.info("=============== locType: " + locType);
				log.info("=============== stgZone: " + stgZone);
				// Start modified for SMART CR by FPT on 23-Feb-2014
				List<?> stageList = null;
				if ("B".equals(locType)) {
					stageList = gbMiscApplicationService.getAreaListBasedOnStorageZone(locType, stgZone);
				} else {
					log.info("Before SMART interface calling to fetch Storage Area by Location Type is [" + locType + "] and Zone is [" + stgZone + "]");
					try {
						String url = smartBaseUrl + "/getLocListBasedOnStorageZone" + "?stgType=" + locType + "&stgZone=" + stgZone;
						log.info("[getLocListBasedOnStorageZone] Calling SMART service URL= " + url);
						stageList = smartServiceRestClient.getLocListBasedOnStorageZone(locType, stgZone);
					} catch (Exception ex) {
						log.info("Call SMART Interface to fetch Storage Area is not successfully: Location Type is [" + locType + "] and Zone is [" + stgZone + "]");
						log.info("Exception miscAppApproveSpace : ", ex);
					}
					log.info("After SMART interface calling");
				}
				// End modified for SMART CR by FPT on 23-Feb-2014

				map.put("stageList", stageList);
				map.put("locType", locType);
				map.put("stgZone", stgZone);
				map.put("stgSelectZone", stgZone);
				// Added by Punitha on 19/11/2009
			} else if ("ADD_OPS_SUBMIT".equals(command)) {

				berthNbr = criteria.getPredicates().get("berthNbr");
				locType = criteria.getPredicates().get("locType");
				stgZone = criteria.getPredicates().get("stgZone");
				stgName = criteria.getPredicates().get("stgName");

				log.info("=============== berthNbr: " + berthNbr);
				log.info("=============== stgName: " + stgName);
				log.info("=============== locType: " + locType);
				log.info("=============== stgZone: " + stgZone);

				// Start modified for SMART CR by FPT on 23-Feb-2013
				boolean isFromSMART = false;
				// inSmartIndList = new ArrayList();
				// Object inSmartInd = criteria.getPredicates().get("inSmartInd");
				if (inSmartIndList != null) {
					for (int i = 0; i < inSmartIndList.size(); i++) {
						if ("1".equals(inSmartIndList.get(i))) {
							isFromSMART = true;
						}
					}
				}
				if (!isFromSMART) {
					// inSmartIndList.add("0");
					// map.put("inSmartInd", inSmartIndList.toArray(new String[0]));
					if (locType.equalsIgnoreCase("B"))
						bayNbrNew = locType + "_" + berthNbr;
					else if (locType.equalsIgnoreCase("Y") || locType.equalsIgnoreCase("W"))
						bayNbrNew = locType + "_" + stgName;

					// bayNbrNew = request.getParameter("bayNbr");
					areaUsedNew = criteria.getPredicates().get("areaUsed");
					fromDateNew = criteria.getPredicates().get("fromDate");
					fromTimeNew = criteria.getPredicates().get("fromTime");
					toDateNew = criteria.getPredicates().get("toDate");
					toTimeNew = criteria.getPredicates().get("toTime");
					if (bayNbrList == null) {
						bayNbrList = new ArrayList<String>();
						bayNbrList.add(bayNbrNew);
						map.put("bayNbrList", bayNbrList);
					} else {
						bayNbrList.add(bayNbrNew);
					}
					if (inSmartIndList == null) {
						inSmartIndList = new ArrayList<String>();
						inSmartIndList.add("0");
						map.put("inSmartInd", inSmartIndList);
					} else {
						inSmartIndList.add("0");
					}
					if (areaUsedList == null) {
						areaUsedList = new ArrayList<String>();
						areaUsedList.add(areaUsedNew);
						map.put("areaUsedList", areaUsedList);
					} else {
						areaUsedList.add(areaUsedNew);
					}
					if (opsStartDttmList == null) {
						opsStartDttmList = new ArrayList<String>();
						opsStartDttmList.add(fromDateNew + " " + fromTimeNew);
						map.put("opsStartDttmList", opsStartDttmList);
					} else {
						opsStartDttmList.add(fromDateNew + " " + fromTimeNew);
					}
					if (opsEndDttmList == null) {
						opsEndDttmList = new ArrayList<String>();
						opsEndDttmList.add(toDateNew + " " + toTimeNew);
						map.put("opsEndDttmList", opsEndDttmList);
					} else {
						opsEndDttmList.add(toDateNew + " " + toTimeNew);
					}
					// Added by Punitha on 19/11/2009
					if (bayNbrList != null)
						map.put("bayNbr", bayNbrList.toArray(new String[0]));
					if (areaUsedList != null)
						map.put("areaUsed", areaUsedList.toArray(new String[0]));
					if (opsStartDttmList != null)
						map.put("opsStartDttm", opsStartDttmList.toArray(new String[0]));
					if (opsEndDttmList != null)
						map.put("opsEndDttm", opsEndDttmList.toArray(new String[0]));
				}

				map.remove("stageList");
				map.remove("detailsList");
				map.remove("locType");
				map.remove("stgZone");
				map.remove("stgSelectZone");

				// End modified for SMART CR by FPT on 23-Feb-2013
			} else if ("DELETE_OPS".equals(command)) {
				String[] delList = CommonUtil.getRequiredStringParameters(request, "opsCheck");

				// Start added for SMART CR by FPT on 23-Jan-2014
				// inSmartIndList = new ArrayList();
//					Object inSmartInd = criteria.getPredicates().get("inSmartInd");
//					if (inSmartInd != null) {
//						String temp[] = (String[]) inSmartInd;
//						for (int i = 0; i < temp.length; i++) {
//							inSmartIndList.add(temp[i]);
//						}
//					}
				// End added for SMART CR by FPT on 23-Jan-2014

				for (int i = 0; i < delList.length; i++) {
					// String ind = delList[i];
					int ind = Integer.parseInt(criteria.getPredicates().get("opsCheck"));
					// Start added for SMART CR by FPT on 23-Jan-2014
					if (ind == 1) {
						errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M100020");
						// return;
					}
					inSmartIndList.remove(ind);
					// End added for SMART CR by FPT on 23-Jan-2014
					bayNbrList.remove(ind);
					areaUsedList.remove(ind);
					opsStartDttmList.remove(ind);
					opsEndDttmList.remove(ind);
					log.info("=============== bayNbrList: " + bayNbrList);
				}

			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				// Added by Punitha on 19/11/2009
				MiscSpaceValueObject obj = new MiscSpaceValueObject();
				obj.setBayNbr((String[]) CommonUtil.getRequiredStringParameters(request, "bayNbr"));

				obj.setAreaUsed((String[]) CommonUtil.getRequiredStringParameters(request, "areaUsed"));
				obj.setOpsStartDttm((String[]) CommonUtil.getRequiredStringParameters(request, "opsStartDttm"));
				obj.setOpsEndDttm((String[]) CommonUtil.getRequiredStringParameters(request, "opsEndDttm"));
				// miscAppEjb.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
				// approveDate);

				log.info("=============== userId: " + userId);
				log.info("=============== miscSeqNbr: " + miscSeqNbr);
				log.info("=============== closeDate: " + approveDate);

				gbMiscApplicationService.approveSpaceApplication(userId, applicationStatus, miscSeqNbr, remarks, obj,
						approveDate);
				// Start added for SMART CR by FPT on 25-Feb-2014
				String appRefNbr = criteria.getPredicates().get("appRefNbr");
				if ("R".equals(applicationStatus)) {
					log.info("**********Before SMART interface calling to Inactive for valid plan of application: application reference number: " + appRefNbr);
					try {
						// Inactive for valid plan from SMART system.
						String url = smartBaseUrl + "/checkValidUseOfSpacePlan" + "?userId=" + userId + "&refNbr=" + appRefNbr + "&isInactivate=" + true;
						log.info("[checkValidUseOfSpacePlan] Calling SMART service URL= " + url);
						smartServiceRestClient.checkValidUseOfSpacePlan(userId, appRefNbr,true);
					} catch (Exception ex) {
						log.info(
								"Call SMART Interface to Inactive for valid plan is not successfully: Application reference number ="
										+ appRefNbr);
						log.info("Exception: ", ex);
					}
					log.info("**********After SMART interface calling");
					// SpaceAppApproved

					// Send email for JP staff: emailMessage, emailSubject get from email template.
						String sender = SpaceAppApproved_from;
						String emailSubject = SpaceAppApproved_subject;
						String fileNameSpaceApproved = SpaceAppApproved_body_template;
						String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), fileNameSpaceApproved);
						Map<String, String> emailInputData = new HashMap<String, String>();
						
						emailSubject = StringUtils.replace(emailSubject, "<ApplicationNumber>", appRefNbr);
						emailInputData.put("ApplicationNumber", appRefNbr);
						emailInputData.put("Updater", userId);
						emailInputData.put("TimeofRejection", getCurrentDate());
	
						String emailMessage = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
	
						log.info("Space App Approve, send email: email subject=\n" + emailSubject);
						log.info("Space App Approve, send email: email message=\n" + emailMessage);
						gbMiscApplicationService.sendFlexiAlert("MSR", "", emailMessage, emailSubject, sender, null);
					
				}
				// End added for SMART CR by FPT on 25-Feb-2014

				// Application Details
				map.remove("miscSeqNbr");
				map.remove("appTypeCd");
				map.remove("appTypeName");
				map.remove("appStatusCd");
				map.remove("appStatusNm");
				map.remove("appDttm");
				map.remove("submitDttm");
				map.remove("submitBy");
				map.remove("approveDttm");
				map.remove("approveBy");
				map.remove("closeDttm");
				map.remove("closeBy");
				map.remove("remarks");
				map.remove("status");
				// Space Details
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
				map.remove("bayNbr");
				map.remove("areaUsed");
				map.remove("opsStartDttm");
				map.remove("opsEndDttm");
				map.remove("bayNbrList");
				map.remove("areaUsedList");
				map.remove("opsStartDttmList");
				map.remove("opsEndDttmList");
				// Added by Punitha on 24/08/2007 . To hold the vessel details in session
				map.remove("vslName");
				map.remove("varCode");
				map.remove("inVoyNbr");
				map.remove("outVoyNbr");
				map.remove("atbDttm");
				map.remove("atuDttm");

				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10

				map.remove("seqNo");
				map.remove("cntrSeqNo");
				map.remove("cntrNo");
				map.remove("status");
				map.remove("iso");
				map.remove("len");
				map.remove("ht");
				map.remove("wt");
				map.remove("impHaulier");
				map.remove("expHaulier");
				map.remove("impTruckNbr");
				map.remove("expTruckNbr");
				map.remove("impGateOut");
				map.remove("expGateIn");
				map.remove("miscAppNo");
				map.remove("arrvStat");
				map.remove("dateCreate");
				map.remove("remarks");
				map.remove("sumTotal");
				map.remove("sumLen");
				// End
				// forwardHandler(request, "MiscAppList");
				// return;
			}
			// Added by Punitha on 19/11/2009
			if (bayNbrList != null)
				map.put("bayNbr", bayNbrList.toArray(new String[0]));
			if (areaUsedList != null)
				map.put("areaUsed", areaUsedList.toArray(new String[0]));
			if (opsStartDttmList != null)
				map.put("opsStartDttm", opsStartDttmList.toArray(new String[0]));
			if (opsEndDttmList != null)
				map.put("opsEndDttm", opsEndDttmList.toArray(new String[0]));
			// Start added for SMART CR by FPT on 25-Feb-2014
			if (inSmartIndList != null)
				map.put("inSmartInd", inSmartIndList.toArray(new String[0]));
			// End added for SMART CR by FPT on 25-Feb-2014
			// End

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveSpace : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveSpace : ", e);
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
			log.info("END: miscAppApproveSpace result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveSpreaderHandler -->perform
	public ResponseEntity<?> miscAppApproveSpreader(Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppApproveSpreader criteria:" + criteria.toString() + ", map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

			if ("APPROVE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getSpreaderDetails(userId, applyType, appSeqNbr,
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
				// nextScreen(request, "MiscAppApproveSpreader");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveSpreader : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveSpreader : ", e);
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
			log.info("END: miscAppApproveSpreader result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveSpreaderHandler -->perform
	@RequestMapping(value = "/miscAppApproveSpreader", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppApproveSpreader(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppApproveSpreader criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

			if ("APPROVE".equals(command)) {

				List<Object> list = gbMiscApplicationService.getSpreaderDetails(userId, applyType, appSeqNbr,
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
				// nextScreen(request, "MiscAppApproveSpreader");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveSpreader : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveSpreader : ", e);
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
			log.info("END: miscAppApproveSpreader result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveStationMacHandler -->perform
	public ResponseEntity<?> miscAppApproveStationMac(Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppApproveStationMac criteria:" + criteria.toString() + ", map: " + map.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("APPROVE".equals(command)) {

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
				// nextScreen(request, "MiscAppApproveStationMac");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveStationMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveStationMac : ", e);
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
			log.info("END: miscAppApproveStationMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveStationMacHandler -->perform
	@RequestMapping(value = "/miscAppApproveStationMac", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppApproveStationMac(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppApproveStationMac criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = criteria.getPredicates().get("applyType");
			String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("APPROVE".equals(command)) {

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
				// nextScreen(request, "MiscAppApproveStationMac");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveStationMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveStationMac : ", e);
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
			log.info("END: miscAppApproveStationMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveTpaListHandler -->perform
	@RequestMapping(value = "/miscAppApproveTpaList", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppApproveTpaList(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		List<MiscAppTpaApproveValueObject> appList = null;
		String startDate = null;
		String toDate = null;
		errorMessage = null;
		TableResult tableresult = new TableResult();
		try {
			log.info("START: miscAppApproveTpaList criteria:" + criteria.toString());

			String command = criteria.getPredicates().get("command");
			if ("LIST".equals(command) || "DOWNLOAD".equals(command)) {
				startDate = CommonUtility.deNull(criteria.getPredicates().get("appFromDttm"));
				toDate = CommonUtility.deNull(criteria.getPredicates().get("appToDttm"));
				String contentType = CommonUtility.deNull(criteria.getPredicates().get("contentType"));
				map.put("appFromDttm", startDate);
				map.put("appToDttm", toDate);
				map.put("contentType", contentType);

				String blockOfHours = gbMiscApplicationService.getTpaBlockOfHours();

				tableresult = gbMiscApplicationService.getApproveTpaList(startDate, toDate, criteria); 
				Object listObject = null;
				listObject =  tableresult.getData().getListData().getTopsModel().get(0);
				appList = new ArrayList<MiscAppTpaApproveValueObject>();
				if (listObject instanceof List){
					for(int j = 0; j < ((List<?>)listObject).size(); j++){
					Object item = ((List<?>) listObject).get(j);
					if(item instanceof Object){
						appList.add((MiscAppTpaApproveValueObject) item);
					}
				    }
				}
				// for paging
				map.put("getPageList", appList);
				map.put("AppList", appList);
				map.put("blockOfHours", blockOfHours);
				map.put("total", appList.size());
			}
		} catch (BusinessException e) {
			log.info("Exception miscAppApproveTpaList : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveTpaList : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: miscAppApproveTpaList result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveTpaHandler -->perform
	@RequestMapping(value = "/miscAppApproveTpa", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppApproveTpa(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		if (criteria.getPredicates().isEmpty()) {
			criteria = CommonUtil.getCriteria(request);
			map = new HashMap<>();
		}
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppApproveTpa criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");
			String resetParkingSlot = criteria.getPredicates().get("resetParkingSlot");

			if ("APPROVE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");

				MiscAppParkingAreaObject miscAppParkingAreaObject = null;
				Map<Object, List<MiscAppParkingAreaObject>> assignParkingSlot = new HashMap<Object, List<MiscAppParkingAreaObject>>();
				List<MiscAppParkingAreaObject> slotList = null;
				MiscAppValueObject appObj = null;
				MiscVehValueObject vehObj = null;
				String[] areaCd = null;
				String[] slotCd = null;

				if ("RPS".equals(resetParkingSlot)) {
					applyType = criteria.getPredicates().get("applyType");
					appSeqNbr = criteria.getPredicates().get("appSeqNbr");
					applyTypeNm = criteria.getPredicates().get("applyTypeNm");
					map.put("applyType", applyType);
					map.put("appSeqNbr", appSeqNbr);
					map.put("applyTypeNm", applyTypeNm);
				}
				List<Object> list = gbMiscApplicationService.getTpaForApproveDetails(userId, applyType, appSeqNbr,
						applyTypeNm);

				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
					vehObj = (MiscVehValueObject) list.get(2);
				}

				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				map.put("details", list);
				List<MiscAppParkingAreaObject> parkingAreaList = gbMiscApplicationService.getParkingAreaList(
						vehObj.getCargoType(), vehObj.getFromDate() + " " + vehObj.getFromTime(),
						vehObj.getToDate() + " " + vehObj.getToTime());
				// To add assigned parking area into this list
				String[] assignedSlot = vehObj.getSlot();
				String[] assignedArea = vehObj.getArea();

				for (int i = 0; i < assignedArea.length; i++) {
					boolean exist = false;
					for (int j = 0; j < parkingAreaList.size(); j++) {
						MiscAppParkingAreaObject parkingAreaObject = (MiscAppParkingAreaObject) parkingAreaList.get(j);
						if (parkingAreaObject.getAreaCode().equalsIgnoreCase(assignedArea[i])) {
							exist = true;
							break;
						}
					}
					if (!exist && assignedArea[i] != null && assignedArea[i].trim().length() > 0) {
						MiscAppParkingAreaObject tempParkingAreaObject = new MiscAppParkingAreaObject();
						tempParkingAreaObject.setAreaCode(assignedArea[i]);
						parkingAreaList.add(tempParkingAreaObject);
					}
				}

				map.put("parkingAreaList", parkingAreaList);

				if ("RPS".equals(resetParkingSlot)) {
					areaCd = CommonUtil.getRequiredStringParameters(request, "applyParking");
					slotCd = CommonUtil.getRequiredStringParameters(request, "applySlot");
				} else {
					areaCd = assignedArea;
					slotCd = assignedSlot;
				}

				for (int i = 0; i < areaCd.length; i++) {
					slotList = new ArrayList<MiscAppParkingAreaObject>();
					if (areaCd[i] != null && !"".equals(areaCd[i])) {
						slotList = gbMiscApplicationService.getParkingAreaSlotAvailableList(areaCd[i],
								vehObj.getCargoType(), vehObj.getFromDate() + " " + vehObj.getFromTime(),
								vehObj.getToDate() + " " + vehObj.getToTime());
						// To add assigned slot to list
						for (int k = 0; k < assignedArea.length; k++) {
							if (areaCd[i].equalsIgnoreCase(assignedArea[k]) && assignedSlot[k] != null
									&& assignedSlot[k].trim().length() > 0) {
								MiscAppParkingAreaObject assignedParkingAreaObject = new MiscAppParkingAreaObject();
								assignedParkingAreaObject.setSlotNumber(assignedSlot[k]);
								assignedParkingAreaObject.setAreaCode(assignedArea[k]);
								slotList.add(assignedParkingAreaObject);
							}
						}

						// To recalculate list of slot
						for (int j = 0; j < areaCd.length; j++) {
							if (i != j && areaCd[i].equals(areaCd[j]) && !"".equals(slotCd[j])) {
								miscAppParkingAreaObject = new MiscAppParkingAreaObject();
								miscAppParkingAreaObject.setSlotNumber(slotCd[j]);
								slotList.remove(miscAppParkingAreaObject);
							}
						}
					}
					assignParkingSlot.put(Integer.valueOf(i), slotList);
				}
				map.put("assignParkingSlot", assignParkingSlot);
				map.put("areaCd", areaCd);
				map.put("slotCd", slotCd);

				// nextScreen(request, "MiscAppApproveTpa");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String[] areaCd = null;
				String[] slotCd = null;
				String area = criteria.getPredicates().get("applyParking");
				if (area != null) {
					areaCd = area.split(",");
				}
				String slot = criteria.getPredicates().get("applySlot");
				if (slot != null) {
					slotCd = slot.split(",");
				}
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveTpaApplication(areaCd, slotCd, userId, applicationStatus, miscSeqNbr,
						remarks, approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveTpa : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveTpa : ", e);
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
			log.info("END: miscAppApproveTpa result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveVehHandler -->perform
	public ResponseEntity<?> miscAppApproveVeh(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppApproveVeh criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			if ("APPROVE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getOvernightParkingVehicleDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppApproveVeh");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("appSeqNbr");
				gbMiscApplicationService.approveOnvApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveVeh : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveVeh : ", e);
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
			log.info("END: miscAppApproveVeh result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppApproveVehHandler -->perform
	@RequestMapping(value = "/miscAppApproveVeh", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppApproveVeh(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppApproveVeh criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String command = criteria.getPredicates().get("command");
			if ("APPROVE".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getOvernightParkingVehicleDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppApproveVeh");
			} else if ("APPROVE_SUBMIT".equals(command)) {
				String applicationStatus = criteria.getPredicates().get("applicationStatus");
				String remarks = criteria.getPredicates().get("remarks");
				String approveDate = criteria.getPredicates().get("approveDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.approveOnvApplication(userId, applicationStatus, miscSeqNbr, remarks,
						approveDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppApproveVeh : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppApproveVeh : ", e);
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
			log.info("END: miscAppApproveVeh result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAppvBillHotworkHandler -->perform
	@RequestMapping(value = "/miscAppAppvBillHotwork", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAppvBillHotwork(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAppvBillHotwork criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			if ("APPROVE_BILL".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getHotworkDetails(userId, applyType, appSeqNbr,
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
				// nextScreen(request, "MiscAppAppvBillHotwork");
			} else if ("APPROVE_BILL_SUBMIT".equals(command)) {
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				String approveBillDate = criteria.getPredicates().get("approveBillDate");
				gbMiscApplicationService.approveBillHotworkApplication(userId, miscSeqNbr, approveBillDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppAppvBillHotwork : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAppvBillHotwork : ", e);
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
			log.info("END: miscAppAppvBillHotwork result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}
	// END process Approve

	// START CLOSE
	// delegate.helper.gbms.miscApp --> MiscAppCloseHotworkHandler -->perform
	public ResponseEntity<?> miscAppCloseHotwork(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppCloseHotwork criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;

			String standbyFrDttm = null;
			String fireManNm = null;
			String standbyFrTime = null;
			String standbyToTime = null;
			String chargeTime = null;
			// String totStandbyHr = null;
			String closeDate = null;
			// Added on 30/05/2007 by Punitha. To add checkbox for no_fireman_ind
			// String inspectInd = null;
			// Added on 30/05/2007 by Punitha. To add checkbox for no_fireman_ind

			ArrayList<String> standbyFrDttmList = null;
			ArrayList<String> fireManNmList = null;
			ArrayList<String> standbyFrTimeList = null;
			ArrayList<String> standbyToTimeList = null;
			// Start added for SMART CR by FPT on 25-Feb-2014
			ArrayList<String> chargeTimeList = null;

			Object listObject = null;
			listObject = request.getAttribute("standbyFrDttmList");
			standbyFrDttmList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						standbyFrDttmList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("fireManNmList");
			fireManNmList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						fireManNmList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("standbyFrTimeList");
			standbyFrTimeList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						standbyFrTimeList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("standbyToTimeList");
			standbyToTimeList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						standbyToTimeList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("chargeTimeList");
			chargeTimeList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						chargeTimeList.add((String) item);
					}
				}
			}

			if (!"CLOSE_BILL".equals(command)) {
				map.put("applyType", CommonUtility.deNull(criteria.getPredicates().get("applyType")));
				map.put("refNo", CommonUtility.deNull(criteria.getPredicates().get("refNo")));
				map.put("applyStatus", CommonUtility.deNull(criteria.getPredicates().get("applyStatus")));
				map.put("appFromDttm", CommonUtility.deNull(criteria.getPredicates().get("appFromDttm")));
				map.put("appToDttm", CommonUtility.deNull(criteria.getPredicates().get("appToDttm")));
			}

			if ("CLOSE_BILL".equals(command)) {
				applyType = criteria.getPredicates().get("applyType");
				appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getHotworkDetails(userId, applyType, appSeqNbr,
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
			} else if ("ADD_FIRE_SUBMIT".equals(command)) {
				standbyFrDttm = criteria.getPredicates().get("standbyFrDttm");
				fireManNm = criteria.getPredicates().get("fireManNm");
				standbyFrTime = criteria.getPredicates().get("standbyFrTime");
				standbyToTime = criteria.getPredicates().get("standbyToTime");
				chargeTime = criteria.getPredicates().get("chargeTime");
				if (standbyFrDttmList != null) {
					standbyFrDttmList.add(standbyFrDttm);
				} 
				if (fireManNmList != null) {
					fireManNmList.add(fireManNm);
				}
				if (standbyFrTimeList != null) {
					standbyFrTimeList.add(standbyFrTime);
				}
				if (standbyToTimeList != null) {
					standbyToTimeList.add(standbyToTime);
				} 
				if (chargeTimeList != null) {
					chargeTimeList.add(chargeTime);
				} 
			} else if ("DELETE_FIRE".equals(command)) {
				String[] delList = CommonUtil.getRequiredStringParameters(request, "fireCheck");
				List<Object> removeList1 = new ArrayList<Object>();
				List<Object> removeList2 = new ArrayList<Object>();
				List<Object> removeList3 = new ArrayList<Object>();
				List<Object> removeList4 = new ArrayList<Object>();
				List<Object> removeList5 = new ArrayList<Object>();
				for (int i = 0; i < delList.length; i++) {
					removeList1.add(standbyFrDttmList.get(Integer.parseInt(delList[i])));
					removeList2.add(fireManNmList.get(Integer.parseInt(delList[i])));
					removeList3.add(standbyFrTimeList.get(Integer.parseInt(delList[i])));
					removeList4.add(standbyToTimeList.get(Integer.parseInt(delList[i])));
					removeList5.add(chargeTimeList.get(Integer.parseInt(delList[i])));
				}
				for (int i = 0; i < removeList1.size(); i++) {
					standbyFrDttmList.remove(removeList1.get(i));
				}
				for (int i = 0; i < removeList2.size(); i++) {
					fireManNmList.remove(removeList2.get(i));
				}
				for (int i = 0; i < removeList3.size(); i++) {
					standbyFrTimeList.remove(removeList3.get(i));
				}
				for (int i = 0; i < removeList4.size(); i++) {
					standbyToTimeList.remove(removeList4.get(i));
				}
				for (int i = 0; i < removeList5.size(); i++) {
					chargeTimeList.remove(removeList5.get(i));
				}
			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {
				closeDate = criteria.getPredicates().get("closeDate");
				MiscHotworkValueObject obj = new MiscHotworkValueObject();
				obj.setStandbyFrDttm((String[]) CommonUtil.getRequiredStringParameters(request, "standbyFrDttm"));
				obj.setFireManNm((String[]) CommonUtil.getRequiredStringParameters(request, "fireManNm"));
				obj.setStandbyFrTime((String[]) CommonUtil.getRequiredStringParameters(request, "standbyFrTime"));
				obj.setStandbyToTime((String[]) CommonUtil.getRequiredStringParameters(request, "standbyToTime"));
				obj.setChargeTime((String[]) CommonUtil.getRequiredStringParameters(request, "chargeTime"));
				obj.setTotStandbyHr(criteria.getPredicates().get("totStandbyHr"));
				// Added on 30/05/2005 by Punitha.To include checkbox for InspectInd
				obj.setInspectInd(criteria.getPredicates().get("inspectInd"));
				// Added on 30/05/2005 by Punitha.To include checkbox for InspectInd
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.closeBillHotworkDetails(userId, miscSeqNbr, obj, closeDate);

				// remove session attributes here
				map.remove("coName");
				map.remove("phone");
				map.remove("add1");
				map.remove("add2");
				map.remove("city");
				map.remove("pin");
				map.remove("account");

				// Application Details
				map.remove("miscSeqNbr");
				map.remove("appTypeCd");
				map.remove("appTypeName");
				map.remove("appStatusCd");
				map.remove("appStatusNm");
				map.remove("appDttm");
				map.remove("submitDttm");
				map.remove("submitBy");
				map.remove("approveDttm");
				map.remove("approveBy");
				map.remove("closeDttm");
				map.remove("closeBy");
				map.remove("remarks");
				map.remove("status");
				// Hotwork Details
				map.remove("location");
				map.remove("description");
				map.remove("fromDate");
				map.remove("fromTime");
				map.remove("toDate");
				map.remove("toTime");
				map.remove("totStandbyHr");
				map.remove("standbyFrDttm");
				map.remove("standbyFrTime");
				map.remove("standbyToTime");
				map.remove("chargeTime");
				map.remove("fireManNm");
				map.remove("standbyFrDttmList");
				map.remove("standbyFrTimeList");
				map.remove("standbyToTimeList");
				map.remove("chargeTimeList");
				map.remove("fireManNmList");
				// Added on 30/05/2005 by Punitha.To include checkbox for InspectInd
				map.remove("inspectInd");
				// Added on 30/05/2005 by Punitha.To include checkbox for InspectInd
				map.remove("vslName");
				map.remove("varCode");
				map.remove("inVoyNbr");
				map.remove("outVoyNbr");
				map.remove("atbDttm");
				map.remove("atuDttm");
				// forwardHandler(request, "MiscAppList");
				// return;
			}
			if (standbyFrDttmList != null)
				map.put("standbyFrDttm", standbyFrDttmList.toArray(new String[0]));
			if (standbyFrTimeList != null)
				map.put("standbyFrTime", standbyFrTimeList.toArray(new String[0]));
			if (standbyToTimeList != null)
				map.put("standbyToTime", standbyToTimeList.toArray(new String[0]));
			if (chargeTimeList != null)
				map.put("chargeTime", chargeTimeList.toArray(new String[0]));
			if (fireManNmList != null)
				map.put("fireManNm", fireManNmList.toArray(new String[0]));

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseHotwork : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseHotwork : ", e);
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
			log.info("END: miscAppCloseHotwork result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	@RequestMapping(value = "/miscAppCloseHotwork", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppCloseHotwork(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppCloseHotwork criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;

			String standbyFrDttm = null;
			String fireManNm = null;
			String standbyFrTime = null;
			String standbyToTime = null;
			String chargeTime = null;
			// String totStandbyHr = null;
			String closeDate = null;
			// Added on 30/05/2007 by Punitha. To add checkbox for no_fireman_ind
			// String inspectInd = null;

			List<String> standbyFrDttmList = null;
			List<String> fireManNmList = null;
			List<String> standbyFrTimeList = null;
			List<String> standbyToTimeList = null;
			List<String> chargeTimeList = null;

			if (criteria.getPredicates().get("standbyFrDttmList") != ""
					&& criteria.getPredicates().get("standbyFrDttmList") != null) {
				standbyFrDttmList = new ArrayList<String>();
				String[] tempStandbyFrDttmList = criteria.getPredicates().get("standbyFrDttmList").split(",");
				for (int i = 0; i < tempStandbyFrDttmList.length; i++) {
					standbyFrDttmList.add(tempStandbyFrDttmList[i]);
				}
			}

			if (criteria.getPredicates().get("fireManNmList") != ""
					&& criteria.getPredicates().get("fireManNmList") != null) {
				fireManNmList = new ArrayList<String>();
				String[] tempFireManNmList = criteria.getPredicates().get("fireManNmList").split(",");
				for (int i = 0; i < tempFireManNmList.length; i++) {
					fireManNmList.add(tempFireManNmList[i]);
				}
			}

			if (criteria.getPredicates().get("standbyFrTimeList") != ""
					&& criteria.getPredicates().get("standbyFrTimeList") != null) {
				standbyFrTimeList = new ArrayList<String>();
				String[] tempStandbyFrTimeList = criteria.getPredicates().get("standbyFrTimeList").split(",");
				for (int i = 0; i < tempStandbyFrTimeList.length; i++) {
					standbyFrTimeList.add(tempStandbyFrTimeList[i]);
				}
			}

			if (criteria.getPredicates().get("standbyToTimeList") != ""
					&& criteria.getPredicates().get("standbyToTimeList") != null) {
				standbyToTimeList = new ArrayList<String>();
				String[] tempStandbyToTimeList = criteria.getPredicates().get("standbyToTimeList").split(",");
				for (int i = 0; i < tempStandbyToTimeList.length; i++) {
					standbyToTimeList.add(tempStandbyToTimeList[i]);
				}
			}

			if (criteria.getPredicates().get("chargeTimeList") != ""
					&& criteria.getPredicates().get("chargeTimeList") != null) {
				chargeTimeList = new ArrayList<String>();
				String[] tempChargeTimeList = criteria.getPredicates().get("chargeTimeList").split(",");
				for (int i = 0; i < tempChargeTimeList.length; i++) {
					chargeTimeList.add(tempChargeTimeList[i]);
				}
			}

			if (!"CLOSE_BILL".equals(command)) {
				map.put("applyType", CommonUtility.deNull(criteria.getPredicates().get("applyType")));
				map.put("refNo", CommonUtility.deNull(criteria.getPredicates().get("refNo")));
				map.put("applyStatus", CommonUtility.deNull(criteria.getPredicates().get("applyStatus")));
				map.put("appFromDttm", CommonUtility.deNull(criteria.getPredicates().get("appFromDttm")));
				map.put("appToDttm", CommonUtility.deNull(criteria.getPredicates().get("appToDttm")));
			}
			applyType = criteria.getPredicates().get("applyType");
			appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("CLOSE_BILL".equals(command)) {

				List<Object> list = gbMiscApplicationService.getHotworkDetails(userId, applyType, appSeqNbr,
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
			} else if ("ADD_FIRE_SUBMIT".equals(command)) {
				standbyFrDttm = criteria.getPredicates().get("standbyFrDttm");
				fireManNm = criteria.getPredicates().get("fireManNm");
				standbyFrTime = criteria.getPredicates().get("standbyFrTime");
				standbyToTime = criteria.getPredicates().get("standbyToTime");
				chargeTime = criteria.getPredicates().get("chargeTime");
				if (standbyFrDttmList == null) {
					standbyFrDttmList = new ArrayList<String>();
					standbyFrDttmList.add(standbyFrDttm);
					map.put("standbyFrDttmList", standbyFrDttmList);
				} else {
					standbyFrDttmList.add(standbyFrDttm);
				}
				if (fireManNmList == null) {
					fireManNmList = new ArrayList<String>();
					fireManNmList.add(fireManNm);
					map.put("fireManNmList", fireManNmList);
				} else {
					fireManNmList.add(fireManNm);
				}
				if (standbyFrTimeList == null) {
					standbyFrTimeList = new ArrayList<String>();
					standbyFrTimeList.add(standbyFrTime);
					map.put("standbyFrTimeList", standbyFrTimeList);
				} else {
					standbyFrTimeList.add(standbyFrTime);
				}
				if (standbyToTimeList == null) {
					standbyToTimeList = new ArrayList<String>();
					standbyToTimeList.add(standbyToTime);
					map.put("standbyToTimeList", standbyToTimeList);
				} else {
					standbyToTimeList.add(standbyToTime);
				}
				if (chargeTimeList == null) {
					chargeTimeList = new ArrayList<String>();
					chargeTimeList.add(chargeTime);
					map.put("chargeTimeList", chargeTimeList);
				} else {
					chargeTimeList.add(chargeTime);
				}
			} else if ("DELETE_FIRE".equals(command)) {
				String[] delList = CommonUtil.getRequiredStringParameters(request, "fireCheck");
				List<Object> removeList1 = new ArrayList<Object>();
				List<Object> removeList2 = new ArrayList<Object>();
				List<Object> removeList3 = new ArrayList<Object>();
				List<Object> removeList4 = new ArrayList<Object>();
				List<Object> removeList5 = new ArrayList<Object>();
				for (int i = 0; i < delList.length; i++) {
					removeList1.add(standbyFrDttmList.get(Integer.parseInt(delList[i])));
					removeList2.add(fireManNmList.get(Integer.parseInt(delList[i])));
					removeList3.add(standbyFrTimeList.get(Integer.parseInt(delList[i])));
					removeList4.add(standbyToTimeList.get(Integer.parseInt(delList[i])));
					removeList5.add(chargeTimeList.get(Integer.parseInt(delList[i])));
				}
				for (int i = 0; i < removeList1.size(); i++) {
					standbyFrDttmList.remove(removeList1.get(i));
				}
				for (int i = 0; i < removeList2.size(); i++) {
					fireManNmList.remove(removeList2.get(i));
				}
				for (int i = 0; i < removeList3.size(); i++) {
					standbyFrTimeList.remove(removeList3.get(i));
				}
				for (int i = 0; i < removeList4.size(); i++) {
					standbyToTimeList.remove(removeList4.get(i));
				}
				for (int i = 0; i < removeList5.size(); i++) {
					chargeTimeList.remove(removeList5.get(i));
				}
			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {
				closeDate = criteria.getPredicates().get("closeDate");
				MiscHotworkValueObject obj = new MiscHotworkValueObject();
				obj.setStandbyFrDttm((String[]) CommonUtil.getRequiredStringParameters(request, "standbyFrDttm"));
				obj.setFireManNm((String[]) CommonUtil.getRequiredStringParameters(request, "fireManNm"));
				obj.setStandbyFrTime((String[]) CommonUtil.getRequiredStringParameters(request, "standbyFrTime"));
				obj.setStandbyToTime((String[]) CommonUtil.getRequiredStringParameters(request, "standbyToTime"));
				obj.setChargeTime((String[]) CommonUtil.getRequiredStringParameters(request, "chargeTime"));
				obj.setTotStandbyHr(criteria.getPredicates().get("totStandbyHr"));
				// Added on 30/05/2005 by Punitha.To include checkbox for InspectInd
				obj.setInspectInd(criteria.getPredicates().get("inspectInd"));
				// Added on 30/05/2005 by Punitha.To include checkbox for InspectInd
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.closeBillHotworkDetails(userId, miscSeqNbr, obj, closeDate);

				// remove session attributes here
				map.remove("coName");
				map.remove("phone");
				map.remove("add1");
				map.remove("add2");
				map.remove("city");
				map.remove("pin");
				map.remove("account");

				// Application Details
				map.remove("miscSeqNbr");
				map.remove("appTypeCd");
				map.remove("appTypeName");
				map.remove("appStatusCd");
				map.remove("appStatusNm");
				map.remove("appDttm");
				map.remove("submitDttm");
				map.remove("submitBy");
				map.remove("approveDttm");
				map.remove("approveBy");
				map.remove("closeDttm");
				map.remove("closeBy");
				map.remove("remarks");
				map.remove("status");
				// Hotwork Details
				map.remove("location");
				map.remove("description");
				map.remove("fromDate");
				map.remove("fromTime");
				map.remove("toDate");
				map.remove("toTime");
				map.remove("totStandbyHr");
				map.remove("standbyFrDttm");
				map.remove("standbyFrTime");
				map.remove("standbyToTime");
				map.remove("chargeTime");
				map.remove("fireManNm");
				map.remove("standbyFrDttmList");
				map.remove("standbyFrTimeList");
				map.remove("standbyToTimeList");
				map.remove("chargeTimeList");
				map.remove("fireManNmList");
				// Added on 30/05/2005 by Punitha.To include checkbox for InspectInd
				map.remove("inspectInd");
				// Added on 30/05/2005 by Punitha.To include checkbox for InspectInd
				map.remove("vslName");
				map.remove("varCode");
				map.remove("inVoyNbr");
				map.remove("outVoyNbr");
				map.remove("atbDttm");
				map.remove("atuDttm");
				// forwardHandler(request, "MiscAppList");
				// return;
			}
			if (standbyFrDttmList != null)
				map.put("standbyFrDttm", standbyFrDttmList.toArray(new String[0]));
			if (standbyFrTimeList != null)
				map.put("standbyFrTime", standbyFrTimeList.toArray(new String[0]));
			if (standbyToTimeList != null)
				map.put("standbyToTime", standbyToTimeList.toArray(new String[0]));
			if (chargeTimeList != null)
				map.put("chargeTime", chargeTimeList.toArray(new String[0]));
			if (fireManNmList != null)
				map.put("fireManNm", fireManNmList.toArray(new String[0]));

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseHotwork : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseHotwork : ", e);
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
			log.info("END: miscAppCloseHotwork result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppCloseVehHandler -->perform
	public ResponseEntity<?> miscAppCloseVeh(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppCloseVeh criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;

			String fromDate = null;
			String toDate = null;
			String noNights = null;
			String closeDate = null;

			if ("CLOSE_BILL".equals(command)) {
				applyType = criteria.getPredicates().get("applyType");
				appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getOvernightParkingVehicleDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppCloseVeh");
			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {
				closeDate = criteria.getPredicates().get("closeDate");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");
				noNights = criteria.getPredicates().get("actNoNights");
				MiscVehValueObject obj = new MiscVehValueObject();
				obj.setActFromDate(fromDate);
				obj.setActToDate(toDate);
				obj.setActNoNights(noNights);
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.closeBillOvernightParkingVehicleDetails(userId, miscSeqNbr, obj, closeDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseVeh : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseVeh : ", e);
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
			log.info("END: miscAppCloseVeh result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	@RequestMapping(value = "/miscAppCloseVeh", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppCloseVeh(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppCloseVeh criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;

			String fromDate = null;
			String toDate = null;
			String noNights = null;
			String closeDate = null;

			applyType = criteria.getPredicates().get("applyType");
			appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("CLOSE_BILL".equals(command)) {
				// applyType = criteria.getPredicates().get("applyType");
				// appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				// applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getOvernightParkingVehicleDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppCloseVeh");
			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {
				closeDate = criteria.getPredicates().get("closeDate");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");
				noNights = criteria.getPredicates().get("actNoNights");
				MiscVehValueObject obj = new MiscVehValueObject();
				obj.setActFromDate(fromDate);
				obj.setActToDate(toDate);
				obj.setActNoNights(noNights);
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.closeBillOvernightParkingVehicleDetails(userId, miscSeqNbr, obj, closeDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseVeh : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseVeh : ", e);
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
			log.info("END: miscAppCloseVeh result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppCloseBargeHandler -->perform
	public ResponseEntity<?> miscAppCloseBarge(Criteria criteria, Map<String, Object> map) throws BusinessException {

		// Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		// Map<String, Object> map = new HashMap<>();

		try {
			log.info("START: miscAppCloseBarge criteria:" + criteria.toString() + ", map: " + map.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;

			String bargeAtbDttm = null;
			String bargeAtbTime = null;
			String bargeAtuDttm = null;
			String bargeAtuTime = null;
			String closeDate = null;

			if ("CLOSE_BILL".equals(command)) {
				applyType = criteria.getPredicates().get("applyType");
				appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getParkingOfLineTowBargeDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				map.put("coCd", coCd);
				// nextScreen(request, "MiscAppCloseBarge");
			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {
				closeDate = criteria.getPredicates().get("closeDate");
				bargeAtbDttm = criteria.getPredicates().get("bargeAtbDttm");
				bargeAtbTime = criteria.getPredicates().get("bargeAtbTime");
				bargeAtuDttm = criteria.getPredicates().get("bargeAtuDttm");
				bargeAtuTime = criteria.getPredicates().get("bargeAtuTime");

				MiscBargeValueObject obj = new MiscBargeValueObject();
				obj.setBargeAtbDttm(bargeAtbDttm);
				obj.setBargeAtbTime(bargeAtbTime);
				obj.setBargeAtuDttm(bargeAtuDttm);
				obj.setBargeAtuTime(bargeAtuTime);

				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.closeBillParkingOfLineTowBargeDetails(userId, miscSeqNbr, obj, closeDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseBarge : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseBarge : ", e);
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
			log.info("END: miscAppCloseBarge result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppCloseBargeHandler -->perform
	@RequestMapping(value = "/miscAppCloseBarge", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppCloseBarge(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppCloseBarge criteria:" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;

			String bargeAtbDttm = null;
			String bargeAtbTime = null;
			String bargeAtuDttm = null;
			String bargeAtuTime = null;
			String closeDate = null;

			applyType = criteria.getPredicates().get("applyType");
			appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			applyTypeNm = criteria.getPredicates().get("applyTypeNm");

			if ("CLOSE_BILL".equals(command)) {

				List<Object> list = gbMiscApplicationService.getParkingOfLineTowBargeDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				map.put("coCd", coCd);
				// nextScreen(request, "MiscAppCloseBarge");
			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {
				closeDate = criteria.getPredicates().get("closeDate");
				bargeAtbDttm = criteria.getPredicates().get("bargeAtbDttm");
				bargeAtbTime = criteria.getPredicates().get("bargeAtbTime");
				bargeAtuDttm = criteria.getPredicates().get("bargeAtuDttm");
				bargeAtuTime = criteria.getPredicates().get("bargeAtuTime");

				MiscBargeValueObject obj = new MiscBargeValueObject();
				obj.setBargeAtbDttm(bargeAtbDttm);
				obj.setBargeAtbTime(bargeAtbTime);
				obj.setBargeAtuDttm(bargeAtuDttm);
				obj.setBargeAtuTime(bargeAtuTime);

				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.closeBillParkingOfLineTowBargeDetails(userId, miscSeqNbr, obj, closeDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseBarge : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseBarge : ", e);
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
			log.info("END: miscAppCloseBarge result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppCloseTpaHandler -->perform
	@RequestMapping(value = "/miscAppCloseTpa", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppCloseTpa(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {

		if (criteria.getPredicates().isEmpty()) {
			criteria = CommonUtil.getCriteria(request);
			map = new HashMap<>();
		}
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppCloseTpa criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;

			String fromDate = null;
			String fromHour = null;
			String toDate = null;
			String toHour = null;
			String noHours = null;
			String closeDate = null;

			if ("CLOSE_BILL".equals(command)) {
				applyType = criteria.getPredicates().get("applyType");
				appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getTrailerParkingApplicationDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				map.put("details", list);
				// nextScreen(request, "MiscAppCloseTpa");
			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {
				closeDate = criteria.getPredicates().get("closeDate");
				fromDate = criteria.getPredicates().get("fromDate");
				fromHour = criteria.getPredicates().get("fromHour");
				fromDate = (fromHour != null && !"".equals(fromHour)) ? fromDate + " " + fromHour : fromDate;

				toDate = criteria.getPredicates().get("toDate");
				toHour = criteria.getPredicates().get("toHour");
				toDate = (toHour != null && !"".equals(toHour)) ? toDate + " " + toHour : toDate;

				noHours = criteria.getPredicates().get("actNoHours");
				MiscVehValueObject obj = new MiscVehValueObject();
				obj.setActFromDate(fromDate);
				obj.setActToDate(toDate);
				obj.setActNoHours(noHours);

				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.closeBillTrailerParkingApplicationDetails(userId, miscSeqNbr, obj, closeDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseTpa : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseTpa : ", e);
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
			log.info("END: miscAppCloseTpa result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppCloseSpreaderHandler -->perform
	public ResponseEntity<?> miscAppCloseSpreader(Criteria criteria, Map<String, Object> map) throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppCloseSpreader criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;

			String issueDt = null;
			String issueTime = null;
			String issueByStaff = null;
			String receiveByCust = null;
			String receiveDt = null;
			String receiveTime = null;
			String receiveByStaff = null;
			String returnByCust = null;
			String closeDate = null;

			if ("CLOSE_BILL".equals(command)) {
				applyType = criteria.getPredicates().get("applyType");
				appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getSpreaderDetails(userId, applyType, appSeqNbr,
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
				// nextScreen(request, "MiscAppCloseSpreader");
			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {
				issueDt = criteria.getPredicates().get("issueDt");
				issueTime = criteria.getPredicates().get("issueTime");
				issueByStaff = criteria.getPredicates().get("issueByStaff");
				receiveByCust = criteria.getPredicates().get("receiveByCust");
				receiveDt = criteria.getPredicates().get("receiveDt");
				receiveTime = criteria.getPredicates().get("receiveTime");
				receiveByStaff = criteria.getPredicates().get("receiveByStaff");
				returnByCust = criteria.getPredicates().get("returnByCust");
				closeDate = criteria.getPredicates().get("closeDate");

				MiscSpreaderValueObject obj = new MiscSpreaderValueObject();
				obj.setIssueDt(issueDt);
				obj.setIssueTime(issueTime);
				obj.setIssueByStaff(issueByStaff);
				obj.setReceiveByCust(receiveByCust);
				obj.setReceiveDt(receiveDt);
				obj.setReceiveTime(receiveTime);
				obj.setReceiveByStaff(receiveByStaff);
				obj.setReturnByCust(returnByCust);

				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.closeBillSpreaderDetails(userId, miscSeqNbr, obj, closeDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseSpreader : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseSpreader : ", e);
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
			log.info("END: miscAppCloseSpreader result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppCloseSpreaderHandler -->perform
	@RequestMapping(value = "/miscAppCloseSpreader", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppCloseSpreader(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppCloseSpreader criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;

			String issueDt = null;
			String issueTime = null;
			String issueByStaff = null;
			String receiveByCust = null;
			String receiveDt = null;
			String receiveTime = null;
			String receiveByStaff = null;
			String returnByCust = null;
			String closeDate = null;

			applyType = criteria.getPredicates().get("applyType");
			appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			applyTypeNm = criteria.getPredicates().get("applyTypeNm");

			if ("CLOSE_BILL".equals(command)) {

				List<Object> list = gbMiscApplicationService.getSpreaderDetails(userId, applyType, appSeqNbr,
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
				// nextScreen(request, "MiscAppCloseSpreader");
			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {
				issueDt = criteria.getPredicates().get("issueDt");
				issueTime = criteria.getPredicates().get("issueTime");
				issueByStaff = criteria.getPredicates().get("issueByStaff");
				receiveByCust = criteria.getPredicates().get("receiveByCust");
				receiveDt = criteria.getPredicates().get("receiveDt");
				receiveTime = criteria.getPredicates().get("receiveTime");
				receiveByStaff = criteria.getPredicates().get("receiveByStaff");
				returnByCust = criteria.getPredicates().get("returnByCust");
				closeDate = criteria.getPredicates().get("closeDate");

				MiscSpreaderValueObject obj = new MiscSpreaderValueObject();
				obj.setIssueDt(issueDt);
				obj.setIssueTime(issueTime);
				obj.setIssueByStaff(issueByStaff);
				obj.setReceiveByCust(receiveByCust);
				obj.setReceiveDt(receiveDt);
				obj.setReceiveTime(receiveTime);
				obj.setReceiveByStaff(receiveByStaff);
				obj.setReturnByCust(returnByCust);

				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.closeBillSpreaderDetails(userId, miscSeqNbr, obj, closeDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseSpreader : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseSpreader : ", e);
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
			log.info("END: miscAppCloseSpreader result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppCloseSpaceHandler -->perform
	public ResponseEntity<?> miscAppCloseSpace(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {
		Result result = new Result();
		try {
			log.info("START: miscAppCloseSpace criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;
			String refNo = null;

			String bayNbrNew = null;
			String areaUsedNew = null;
			String fromDateNew = null;
			String fromTimeNew = null;
			String toDateNew = null;
			String toTimeNew = null;
			String closeDate = null;

			List<String> bayNbrList = null;
			List<String> areaUsedList = null;
			List<String> opsStartDttmList = null;
			List<String> opsEndDttmList = null;
			// Start added for SMART CR by FPT on 25-Feb-2014
			List<String> inSmartIndList = null;

			Object listObject = null;
			listObject = request.getAttribute("bayNbrList");
			bayNbrList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						bayNbrList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("areaUsedList");
			areaUsedList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						areaUsedList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("opsStartDttmList");
			opsStartDttmList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						opsStartDttmList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("opsEndDttmList");
			opsEndDttmList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						opsEndDttmList.add((String) item);
					}
				}
			}

			listObject = request.getAttribute("inSmartIndList");
			inSmartIndList = new ArrayList<String>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						inSmartIndList.add((String) item);
					}
				}
			}
			// End

			String locType = "";
			String berthNbr = "";
			String stgZone = "";
			String stgName = "";

			if (!"CLOSE_BILL".equals(command)) {
				map.put("applyType", CommonUtility.deNull(criteria.getPredicates().get("applyType")));
				map.put("refNo", CommonUtility.deNull(criteria.getPredicates().get("refNo")));
				map.put("applyStatus", CommonUtility.deNull(criteria.getPredicates().get("applyStatus")));
				map.put("appFromDttm", CommonUtility.deNull(criteria.getPredicates().get("appFromDttm")));
				map.put("appToDttm", CommonUtility.deNull(criteria.getPredicates().get("appToDttm")));
			}

			if ("CLOSE_BILL".equals(command)) {
				applyType = criteria.getPredicates().get("applyType");
				appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				applyTypeNm = criteria.getPredicates().get("applyTypeNm");

				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  userId --> " + userId);
				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  applyType --> " + applyType);
				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  appSeqNbr --> " + appSeqNbr);
				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  applyTypeNm --> " + applyTypeNm);

				List<Object> list = gbMiscApplicationService.getUseOfSpaceDetails(userId, applyType, appSeqNbr,
						applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
					log.info("~~~~ MiscAppCloseSpaceHandler ~~~~ list.size() --> " + list.size());

					// Start added for SMART CR by FPT on 24-Feb-2014
					String appRefNbr = appObj.getAppRefNbr();
					log.info("***********Before SMART interface calling for fetch Operation Details (location info): Application reference number: " + appRefNbr);
					List<SmartInterfaceOutputVO> operationDetailsLst = null;
					try {
						MiscSpaceValueObject space = (MiscSpaceValueObject) list.get(2);
						String url = smartBaseUrl + "/getJpAppStgLocByRefNbr" + "?planType=" + SmartInterfaceConstants.SPA_MICRO_PLAN_TYPE + "&appRefNbr=" + appRefNbr;
						log.info("[getJpAppStgLocByRefNbr] Calling SMART service URL= " + url);
						operationDetailsLst =  smartServiceRestClient.getJpAppStgLocByRefNbr(SmartInterfaceConstants.SPA_MICRO_PLAN_TYPE, appRefNbr);
						bayNbrList = new ArrayList<String>();
						areaUsedList = new ArrayList<String>();
						opsStartDttmList = new ArrayList<String>();
						opsEndDttmList = new ArrayList<String>();
						inSmartIndList = new ArrayList<String>();
						if (operationDetailsLst != null && operationDetailsLst.size() > 0) {
							for (int i = 0; i < operationDetailsLst.size(); i++) {
								SmartInterfaceOutputVO operationDetailsObj = (SmartInterfaceOutputVO) operationDetailsLst.get(i);
								bayNbrList.add(CommonUtility.deNull(operationDetailsObj.getStgName()));
								areaUsedList.add(String.valueOf(operationDetailsObj.getArea()));
								opsStartDttmList.add(CommonUtility.deNull(operationDetailsObj.getOpsStartDttm()));
								opsEndDttmList.add(CommonUtility.deNull(operationDetailsObj.getOpsEndDttm()));
								inSmartIndList.add("1");
							}
							space.setAreaUsed((String[]) areaUsedList.toArray(new String[0]));
							space.setBayNbr((String[]) bayNbrList.toArray(new String[0]));
							space.setOpsStartDttm((String[]) opsStartDttmList.toArray(new String[0]));
							space.setOpsEndDttm((String[]) opsEndDttmList.toArray(new String[0]));
							list.set(2, space);
						} else {
							String bayNbrArr[] = space.getBayNbr();
							if (bayNbrArr != null && bayNbrArr.length > 0) {
								String areaUsedArr[] = space.getAreaUsed();
								String opsStartDttmArr[] = space.getOpsStartDttm();
								String opsEndDttmArr[] = space.getOpsEndDttm();
								for (int i = 0; i < bayNbrArr.length; i++) {
									bayNbrList.add(CommonUtility.deNull(bayNbrArr[i]));
									areaUsedList.add(CommonUtility.deNull(areaUsedArr[i]));
									opsStartDttmList.add(CommonUtility.deNull(opsStartDttmArr[i]));
									opsEndDttmList.add(CommonUtility.deNull(opsEndDttmArr[i]));
									inSmartIndList.add("0");
								}
							}

						}
					} catch (Exception ex) {
						log.info("Call SMART Interface to fetch Operation Details is not successfully: Application reference number = " + appRefNbr);
						log.info("Exception miscAppCloseSpace : ", ex);
					}
					log.info("***********After SMART interface calling");
					// End added for SMART CR by FPT on 24-Feb-2014

				}
				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  appObj.getVarCode() --> " + appObj.getVarCode());

				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppCloseSpace");

				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10
				refNo = criteria.getPredicates().get("refNo");

				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  refNo --> " + refNo);

				List<StorageOrderValueObject> cntrList = gbMiscApplicationService.getMotCntrList(refNo, appSeqNbr);
				List<StorageOrderValueObject> cntrSummary = gbMiscApplicationService.getMotCntrSummary(refNo,
						appSeqNbr);

				log.info("=============== cntrList.size(): " + cntrList.size());
				log.info("=============== cntrSummary.size(): " + cntrSummary.size());

				map.put("cntrList", cntrList);
				map.put("cntrSummary", cntrSummary);

				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10

			} else if ("GET_DETAILS".equals(command)) {
				locType = criteria.getPredicates().get("locType");
				log.info("=============== locType: " + locType);

				// Start modify for SMART CR by FPT on 23-Feb-2014
				List<?> detailsList = null;
				if ("B".equals(locType)) {
					// "Berth" will be fetch from CSPS. Because it does not exist in SMART system.
					detailsList = gbMiscApplicationService.getLocationListBasedOnLocationType(locType);
				} else {
					log.info("*********Before SMART interface calling for fetch Zone by Location Type: Location Type= " + locType);
					try {
						String url = smartBaseUrl + "/getLocListBasedOnLocType?locType=" + locType;
						log.info("[getLocListBasedOnLocType] Calling SMART service URL= " + url);
						detailsList = smartServiceRestClient.getLocListBasedOnLocType(locType);
					} catch (Exception ex) {
						log.info("Call SMART Interface for fetch Zone is not sucessfully: Location Type= " + locType);
						log.info("Exception miscAppCloseSpace : ", ex);
					}
					log.info("*********After SMART interface calling");
				}
				// End modify for SMART CR by FPT on 23-Feb-2014
				map.put("detailsList", detailsList);
				map.put("locType", locType);
			} else if ("GET_STAGE".equals(command)) {
				locType = criteria.getPredicates().get("locType");
				stgZone = request.getParameter("stgZone");
				log.info("=============== locType: " + locType);
				log.info("=============== stgZone: " + stgZone);

				// Start modified for SMART CR by FPT on 23-Feb-2014
				List<?> stageList = null;
				if ("B".equals(locType)) {
					stageList = gbMiscApplicationService.getAreaListBasedOnStorageZone(locType, stgZone);
				} else {
					log.info("Before SMART interface calling to fetch Storage Area by Location Type is [" + locType + "] and Zone is [" + stgZone + "]");
					try {
						String url = smartBaseUrl + "/getLocListBasedOnStorageZone" + "?stgType=" + locType + "&stgZone=" + stgZone;
						log.info("[getLocListBasedOnStorageZone] Calling SMART service URL= " + url);
						stageList = smartServiceRestClient.getLocListBasedOnStorageZone(locType, stgZone);
					} catch (Exception ex) {
						log.info("Call SMART Interface to fetch Storage Area is not successfully: Location Type is [" + locType + "] and Zone is [" + stgZone + "]");
						log.info("Exception miscAppCloseSpace : ", ex);
					}
					log.info("After SMART interface calling");
				}
				// End modified for SMART CR by FPT on 23-Feb-2014

				map.put("stageList", stageList);
				map.put("locType", locType);
				map.put("stgZone", stgZone);
				map.put("stgSelectZone", stgZone);
				// Added by Punitha on 19/11/2009
			} else if ("ADD_OPS_SUBMIT".equals(command)) {

				berthNbr = criteria.getPredicates().get("berthNbr");
				locType = criteria.getPredicates().get("locType");
				stgZone = criteria.getPredicates().get("stgZone");
				stgName = criteria.getPredicates().get("stgName");

				log.info("=============== berthNbr: " + berthNbr);
				log.info("=============== stgName: " + stgName);
				log.info("=============== locType: " + locType);
				log.info("=============== stgZone: " + stgZone);
				// Start modified for SMART CR by FPT on 23-Feb-2013
				boolean isFromSMART = false;
				inSmartIndList = new ArrayList<String>();
				Object inSmartInd = criteria.getPredicates().get("inSmartInd");
				if (inSmartInd != null) {
					String temp[] = (String[]) inSmartInd;
					for (int i = 0; i < temp.length; i++) {
						inSmartIndList.add(temp[i]);
						if ("1".equals(temp[i])) {
							isFromSMART = true;
						}
					}
				}
				if (!isFromSMART) {
					inSmartIndList.add("0");
					map.put("inSmartInd", inSmartIndList.toArray(new String[0]));

					if (locType.equalsIgnoreCase("B"))
						bayNbrNew = locType + "_" + berthNbr;
					else if (locType.equalsIgnoreCase("Y") || locType.equalsIgnoreCase("W"))
						bayNbrNew = locType + "_" + stgName;

					// bayNbrNew = request.getParameter("bayNbr");
					areaUsedNew = criteria.getPredicates().get("areaUsed");
					fromDateNew = criteria.getPredicates().get("fromDate");
					fromTimeNew = criteria.getPredicates().get("fromTime");
					toDateNew = criteria.getPredicates().get("toDate");
					toTimeNew = criteria.getPredicates().get("toTime");
					if (bayNbrList != null) {
						bayNbrList.add(bayNbrNew);
					} 
					if (areaUsedList != null) {
						areaUsedList.add(areaUsedNew);
					} 
					if (opsStartDttmList != null) {
						opsStartDttmList.add(fromDateNew + " " + fromTimeNew);
					} 
					if (opsEndDttmList != null) {
						opsEndDttmList.add(toDateNew + " " + toTimeNew);
					}
					// Added by Punitha on 19/11/2009
					if (bayNbrList != null)
						map.put("bayNbr", bayNbrList.toArray(new String[0]));
					if (areaUsedList != null)
						map.put("areaUsed", areaUsedList.toArray(new String[0]));
					if (opsStartDttmList != null)
						map.put("opsStartDttm", opsStartDttmList.toArray(new String[0]));
					if (opsEndDttmList != null)
						map.put("opsEndDttm", opsEndDttmList.toArray(new String[0]));
					// End
				}

				map.remove("stageList");
				map.remove("detailsList");
				map.remove("locType");
				map.remove("stgZone");
				map.remove("stgSelectZone");

				// End modified for SMART CR by FPT on 23-Feb-2013
			} else if ("DELETE_OPS".equals(command)) {
				String[] delList = CommonUtil.getRequiredStringParameters(request, "opsCheck");
				// Start added for SMART CR by FPT on 23-Jan-2014
				inSmartIndList = new ArrayList<String>();
				Object inSmartInd = criteria.getPredicates().get("inSmartInd");
				if (inSmartInd != null) {
					String temp[] = (String[]) inSmartInd;
					for (int i = 0; i < temp.length; i++) {
						inSmartIndList.add(temp[i]);
					}
				}
				// End added for SMART CR by FPT on 23-Jan-2014
				for (int i = 0; i < delList.length; i++) {
					String ind = delList[i];
					// Start added for SMART CR by FPT on 23-Jan-2014
					if ("1".equals(inSmartIndList.get(Integer.parseInt(ind)))) {
						errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M100020");
						// return;
					}
					inSmartIndList.remove(Integer.parseInt(ind));
					// End added for SMART CR by FPT on 23-Jan-2014
					bayNbrList.remove(Integer.parseInt(ind));
					areaUsedList.remove(Integer.parseInt(ind));
					opsStartDttmList.remove(Integer.parseInt(ind));
					opsEndDttmList.remove(Integer.parseInt(ind));
				}

			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {

				closeDate = request.getParameter("closeDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");

				MiscSpaceValueObject obj = new MiscSpaceValueObject();
				obj.setBayNbr((String[]) CommonUtil.getRequiredStringParameters(request, "bayNbr"));
				obj.setAreaUsed((String[]) CommonUtil.getRequiredStringParameters(request, "areaUsed"));
				obj.setOpsStartDttm((String[]) CommonUtil.getRequiredStringParameters(request, "opsStartDttm"));
				obj.setOpsEndDttm((String[]) CommonUtil.getRequiredStringParameters(request, "opsEndDttm"));

				log.info("=============== userId: " + userId);
				log.info("=============== miscSeqNbr: " + miscSeqNbr);
				log.info("=============== closeDate: " + closeDate);

				gbMiscApplicationService.closeBillUseOfSpaceDetails(userId, miscSeqNbr, obj, closeDate);
				// map.remove("miscSeqNbr");

				// remove session attributes here
				map.remove("coName");
				map.remove("phone");
				map.remove("add1");
				map.remove("add2");
				map.remove("city");
				map.remove("pin");
				map.remove("account");

				// Application Details
				map.remove("miscSeqNbr");
				map.remove("appTypeCd");
				map.remove("appTypeName");
				map.remove("appStatusCd");
				map.remove("appStatusNm");
				map.remove("appDttm");
				map.remove("submitDttm");
				map.remove("submitBy");
				map.remove("approveDttm");
				map.remove("approveBy");
				map.remove("closeDttm");
				map.remove("closeBy");
				map.remove("remarks");
				map.remove("status");
				// Space Details
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
				map.remove("bayNbr");
				map.remove("areaUsed");
				map.remove("opsStartDttm");
				map.remove("opsEndDttm");
				map.remove("bayNbrList");
				map.remove("areaUsedList");
				map.remove("opsStartDttmList");
				map.remove("opsEndDttmList");
				// Added by Punitha on 24/08/2007 . To hold the vessel details in session
				map.remove("vslName");
				map.remove("varCode");
				map.remove("inVoyNbr");
				map.remove("outVoyNbr");
				map.remove("atbDttm");
				map.remove("atuDttm");
				// Ended by Punitha

				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10

				map.remove("seqNo");
				map.remove("cntrSeqNo");
				map.remove("cntrNo");
				map.remove("status");
				map.remove("iso");
				map.remove("len");
				map.remove("ht");
				map.remove("wt");
				map.remove("impHaulier");
				map.remove("expHaulier");
				map.remove("impTruckNbr");
				map.remove("expTruckNbr");
				map.remove("impGateOut");
				map.remove("expGateIn");
				map.remove("miscAppNo");
				map.remove("arrvStat");
				map.remove("dateCreate");
				map.remove("remarks");
				map.remove("sumTotal");
				map.remove("sumLen");

				// forwardHandler(request, "MiscAppList");
				// return;
			}
			if (bayNbrList != null)
				map.put("bayNbr", bayNbrList.toArray(new String[0]));
			if (areaUsedList != null)
				map.put("areaUsed", areaUsedList.toArray(new String[0]));
			if (opsStartDttmList != null)
				map.put("opsStartDttm", opsStartDttmList.toArray(new String[0]));
			if (opsEndDttmList != null)
				map.put("opsEndDttm", opsEndDttmList.toArray(new String[0]));
			// Start added for SMART CR by FPT on 25-Feb-2014
			if (inSmartIndList != null)
				map.put("inSmartInd", inSmartIndList.toArray(new String[0]));

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseSpace : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseSpace : ", e);
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
			log.info("END: miscAppCloseSpace result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppCloseSpaceHandler -->perform
	@RequestMapping(value = "/miscAppCloseSpace", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppCloseSpace(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppCloseSpace criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;
			String refNo = null;

			String bayNbrNew = null;
			String areaUsedNew = null;
			String fromDateNew = null;
			String fromTimeNew = null;
			String toDateNew = null;
			String toTimeNew = null;
			String closeDate = null;

			List<String> bayNbrList = null;
			List<String> areaUsedList = null;
			List<String> opsStartDttmList = null;
			List<String> opsEndDttmList = null;
			// Start added for SMART CR by FPT on 25-Feb-2014
			// ArrayList inSmartIndList = request.getAttribute("inSmartIndList")
			// ;
			ArrayList<Object> inSmartIndList = null;

//				if(inSmartIndList == null){
//					inSmartIndList = new ArrayList();
//					String temp[] = (String[]) CommonUtil.getRequiredStringParameters(request,"inSmartInd");
//					if(temp != null){
//						for(int i=0; i< temp.length; i++)
//							inSmartIndList.add(temp[i]);
//					}
//				}

			if (criteria.getPredicates().get("inSmartIndList") != ""
					&& criteria.getPredicates().get("inSmartIndList") != null) {
				inSmartIndList = new ArrayList<Object>();
				String[] tempInSmartIndList = criteria.getPredicates().get("inSmartIndList").split(",");
				for (int i = 0; i < tempInSmartIndList.length; i++) {
					inSmartIndList.add(tempInSmartIndList[i]);
				}
			}
			// End added for SMART CR by FPT on 25-Feb-2014

			if (criteria.getPredicates().get("bayNbrList") != ""
					&& criteria.getPredicates().get("bayNbrList") != null) {
				bayNbrList = new ArrayList<String>();
				String[] tempBayNbrList = criteria.getPredicates().get("bayNbrList").split(",");
				for (int i = 0; i < tempBayNbrList.length; i++) {
					bayNbrList.add(tempBayNbrList[i]);
				}
			}

			if (criteria.getPredicates().get("areaUsedList") != ""
					&& criteria.getPredicates().get("areaUsedList") != null) {
				areaUsedList = new ArrayList<String>();
				String[] tempAreaUsedList = criteria.getPredicates().get("areaUsedList").split(",");
				for (int i = 0; i < tempAreaUsedList.length; i++) {
					areaUsedList.add(tempAreaUsedList[i]);
				}
			}

			if (criteria.getPredicates().get("opsStartDttmList") != ""
					&& criteria.getPredicates().get("opsStartDttmList") != null) {
				opsStartDttmList = new ArrayList<String>();
				String[] tempOpsStartDttmList = criteria.getPredicates().get("opsStartDttmList").split(",");
				for (int i = 0; i < tempOpsStartDttmList.length; i++) {
					opsStartDttmList.add(tempOpsStartDttmList[i]);
				}
			}

			if (criteria.getPredicates().get("opsEndDttmList") != ""
					&& criteria.getPredicates().get("opsEndDttmList") != null) {
				opsEndDttmList = new ArrayList<String>();
				String[] tempOpsEndDttmList = criteria.getPredicates().get("opsEndDttmList").split(",");
				for (int i = 0; i < tempOpsEndDttmList.length; i++) {
					opsEndDttmList.add(tempOpsEndDttmList[i]);
				}
			}

			// End

			String locType = "";
			String berthNbr = "";
			String stgZone = "";
			String stgName = "";

			if (!"CLOSE_BILL".equals(command)) {
				map.put("applyType", CommonUtility.deNull(criteria.getPredicates().get("applyType")));
				map.put("refNo", CommonUtility.deNull(criteria.getPredicates().get("refNo")));
				map.put("applyStatus", CommonUtility.deNull(criteria.getPredicates().get("applyStatus")));
				map.put("appFromDttm", CommonUtility.deNull(criteria.getPredicates().get("appFromDttm")));
				map.put("appToDttm", CommonUtility.deNull(criteria.getPredicates().get("appToDttm")));
			}

			if ("CLOSE_BILL".equals(command)) {
				applyType = criteria.getPredicates().get("applyType");
				appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				applyTypeNm = criteria.getPredicates().get("applyTypeNm");

				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  userId --> " + userId);
				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  applyType --> " + applyType);
				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  appSeqNbr --> " + appSeqNbr);
				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  applyTypeNm --> " + applyTypeNm);

				List<Object> list = gbMiscApplicationService.getUseOfSpaceDetails(userId, applyType, appSeqNbr,
						applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
					log.info("~~~~ MiscAppCloseSpaceHandler ~~~~ list.size() --> " + list.size());

					// Start added for SMART CR by FPT on 24-Feb-2014
					String appRefNbr = appObj.getAppRefNbr();
					log.info("***********Before SMART interface calling for fetch Operation Details (location info): Application reference number: " + appRefNbr);
					List<SmartInterfaceOutputVO> operationDetailsLst = null;
					try {
						MiscSpaceValueObject space = (MiscSpaceValueObject) list.get(2);
						String url = smartBaseUrl + "/getJpAppStgLocByRefNbr" + "?planType=" + SmartInterfaceConstants.SPA_MICRO_PLAN_TYPE + "&appRefNbr=" + appRefNbr;
						log.info("[getJpAppStgLocByRefNbr] Calling SMART service URL= "+ url);
						operationDetailsLst =  smartServiceRestClient.getJpAppStgLocByRefNbr(SmartInterfaceConstants.SPA_MICRO_PLAN_TYPE, appRefNbr);
						bayNbrList = new ArrayList<String>();
						areaUsedList = new ArrayList<String>();
						opsStartDttmList = new ArrayList<String>();
						opsEndDttmList = new ArrayList<String>();
						inSmartIndList = new ArrayList<Object>();
						if (operationDetailsLst != null && operationDetailsLst.size() > 0) {
							for (int i = 0; i < operationDetailsLst.size(); i++) {
								SmartInterfaceOutputVO operationDetailsObj = (SmartInterfaceOutputVO) operationDetailsLst
										.get(i);
								bayNbrList.add(CommonUtility.deNull(operationDetailsObj.getStgName()));
								areaUsedList.add(String.valueOf(operationDetailsObj.getArea()));
								opsStartDttmList.add(CommonUtility.deNull(operationDetailsObj.getOpsStartDttm()));
								opsEndDttmList.add(CommonUtility.deNull(operationDetailsObj.getOpsEndDttm()));
								inSmartIndList.add("1");
							}
							space.setAreaUsed((String[]) areaUsedList.toArray(new String[0]));
							space.setBayNbr((String[]) bayNbrList.toArray(new String[0]));
							space.setOpsStartDttm((String[]) opsStartDttmList.toArray(new String[0]));
							space.setOpsEndDttm((String[]) opsEndDttmList.toArray(new String[0]));
							list.set(2, space);
						} else {
							String bayNbrArr[] = space.getBayNbr();
							if (bayNbrArr != null && bayNbrArr.length > 0) {
								String areaUsedArr[] = space.getAreaUsed();
								String opsStartDttmArr[] = space.getOpsStartDttm();
								String opsEndDttmArr[] = space.getOpsEndDttm();
								for (int i = 0; i < bayNbrArr.length; i++) {
									bayNbrList.add(CommonUtility.deNull(bayNbrArr[i]));
									areaUsedList.add(CommonUtility.deNull(areaUsedArr[i]));
									opsStartDttmList.add(CommonUtility.deNull(opsStartDttmArr[i]));
									opsEndDttmList.add(CommonUtility.deNull(opsEndDttmArr[i]));
									inSmartIndList.add("0");
								}
							}

						}
					} catch (Exception ex) {
						log.info(
								"Call SMART Interface to fetch Operation Details is not successfully: Application reference number ="
										+ appRefNbr);
						log.info("Exception miscAppCloseSpace : ", ex);
					}
					log.info("***********After SMART interface calling");
					// End added for SMART CR by FPT on 24-Feb-2014

				}
				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  appObj.getVarCode() --> " + appObj.getVarCode());

				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				// nextScreen(request, "MiscAppCloseSpace");

				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10
				refNo = criteria.getPredicates().get("refNo");

				log.info("~~~~ MiscAppCloseSpaceHandler ~~~~  refNo --> " + refNo);

				List<StorageOrderValueObject> cntrList = gbMiscApplicationService.getMotCntrList(refNo, appSeqNbr);
				List<StorageOrderValueObject> cntrSummary = gbMiscApplicationService.getMotCntrSummary(refNo,
						appSeqNbr);

				log.info("=============== cntrList.size(): " + cntrList.size());
				log.info("=============== cntrSummary.size(): " + cntrSummary.size());

				map.put("cntrList", cntrList);
				map.put("cntrSummary", cntrSummary);

				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10

			} else if ("GET_DETAILS".equals(command)) {
				locType = criteria.getPredicates().get("locType");
				log.info("=============== locType: " + locType);

				// Start modify for SMART CR by FPT on 23-Feb-2014
				List<?> detailsList = null;
				if ("B".equals(locType)) {
					// "Berth" will be fetch from CSPS. Because it does not exist in SMART system.
					detailsList = gbMiscApplicationService.getLocationListBasedOnLocationType(locType);
				} else {
					log.info("*********Before SMART interface calling for fetch Zone by Location Type: Location Type= " + locType);
					try {
						String url = smartBaseUrl + "/getLocListBasedOnLocType?locType=" + locType;
						log.info("[getLocListBasedOnLocType] Calling SMART service URL= " + url);
						detailsList = smartServiceRestClient.getLocListBasedOnLocType(locType);
					} catch (Exception ex) {
						log.info("Call SMART Interface for fetch Zone is not sucessfully: Location Type= " + locType);
						log.info("Exception miscAppCloseSpace : ", ex);
					}
					log.info("*********After SMART interface calling");
				}
				// End modify for SMART CR by FPT on 23-Feb-2014
				map.put("detailsList", detailsList);
				map.put("locType", locType);
			} else if ("GET_STAGE".equals(command)) {
				locType = criteria.getPredicates().get("locType");
				stgZone = request.getParameter("stgZone");
				log.info("=============== locType: " + locType);
				log.info("=============== stgZone: " + stgZone);

				// Start modified for SMART CR by FPT on 23-Feb-2014
				List<?> stageList = null;
				if ("B".equals(locType)) {
					stageList = gbMiscApplicationService.getAreaListBasedOnStorageZone(locType, stgZone);
				} else {
					log.info("Before SMART interface calling to fetch Storage Area by Location Type is [" + locType + "] and Zone is [" + stgZone + "]");
					try {
						String url = smartBaseUrl + "/getLocListBasedOnStorageZone" + "?stgType=" + locType + "&stgZone=" + stgZone;
						log.info("[getLocListBasedOnStorageZone] Calling SMART service URL= " + url);
						stageList = smartServiceRestClient.getLocListBasedOnStorageZone(locType, stgZone);
					} catch (Exception ex) {
						log.info("Call SMART Interface to fetch Storage Area is not successfully: Location Type is ["
								+ locType + "] and Zone is [" + stgZone + "]");
						log.info("Exception miscAppCloseSpace : ", ex);
					}
					log.info("After SMART interface calling");
				}
				// End modified for SMART CR by FPT on 23-Feb-2014

				map.put("stageList", stageList);
				map.put("locType", locType);
				map.put("stgZone", stgZone);
				map.put("stgSelectZone", stgZone);
				// Added by Punitha on 19/11/2009
			} else if ("ADD_OPS_SUBMIT".equals(command)) {

				berthNbr = criteria.getPredicates().get("berthNbr");
				locType = criteria.getPredicates().get("locType");
				stgZone = criteria.getPredicates().get("stgZone");
				stgName = criteria.getPredicates().get("stgName");

				log.info("=============== berthNbr: " + berthNbr);
				log.info("=============== stgName: " + stgName);
				log.info("=============== locType: " + locType);
				log.info("=============== stgZone: " + stgZone);
				// Start modified for SMART CR by FPT on 23-Feb-2013
				boolean isFromSMART = false;
				// inSmartIndList = new ArrayList();
				// Object inSmartInd = criteria.getPredicates().get("inSmartInd");
				if (inSmartIndList != null) {
					for (int i = 0; i < inSmartIndList.size(); i++) {
						if ("1".equals(inSmartIndList.get(i))) {
							isFromSMART = true;
						}
					}
				}

				if (!isFromSMART) {
					// inSmartIndList.add("0");
					// map.put("inSmartInd", inSmartIndList.toArray(new String[0]));

					if (locType.equalsIgnoreCase("B"))
						bayNbrNew = locType + "_" + berthNbr;
					else if (locType.equalsIgnoreCase("Y") || locType.equalsIgnoreCase("W"))
						bayNbrNew = locType + "_" + stgName;

					// bayNbrNew = request.getParameter("bayNbr");
					areaUsedNew = criteria.getPredicates().get("areaUsed");
					fromDateNew = criteria.getPredicates().get("fromDate");
					fromTimeNew = criteria.getPredicates().get("fromTime");
					toDateNew = criteria.getPredicates().get("toDate");
					toTimeNew = criteria.getPredicates().get("toTime");
					if (bayNbrList == null) {
						bayNbrList = new ArrayList<String>();
						bayNbrList.add(bayNbrNew);
						map.put("bayNbrList", bayNbrList);
					} else {
						bayNbrList.add(bayNbrNew);
					}
					if (inSmartIndList == null) {
						inSmartIndList = new ArrayList<Object>();
						inSmartIndList.add("0");
						map.put("inSmartInd", inSmartIndList);
					} else {
						inSmartIndList.add("0");
					}
					if (areaUsedList == null) {
						areaUsedList = new ArrayList<String>();
						areaUsedList.add(areaUsedNew);
						map.put("areaUsedList", areaUsedList);
					} else {
						areaUsedList.add(areaUsedNew);
					}
					if (opsStartDttmList == null) {
						opsStartDttmList = new ArrayList<String>();
						opsStartDttmList.add(fromDateNew + " " + fromTimeNew);
						map.put("opsStartDttmList", opsStartDttmList);
					} else {
						opsStartDttmList.add(fromDateNew + " " + fromTimeNew);
					}
					if (opsEndDttmList == null) {
						opsEndDttmList = new ArrayList<String>();
						opsEndDttmList.add(toDateNew + " " + toTimeNew);
						map.put("opsEndDttmList", opsEndDttmList);
					} else {
						opsEndDttmList.add(toDateNew + " " + toTimeNew);
					}
					// Added by Punitha on 19/11/2009
					if (bayNbrList != null)
						map.put("bayNbr", bayNbrList.toArray(new String[0]));
					if (areaUsedList != null)
						map.put("areaUsed", areaUsedList.toArray(new String[0]));
					if (opsStartDttmList != null)
						map.put("opsStartDttm", opsStartDttmList.toArray(new String[0]));
					if (opsEndDttmList != null)
						map.put("opsEndDttm", opsEndDttmList.toArray(new String[0]));
					// End
				}

				map.remove("stageList");
				map.remove("detailsList");
				map.remove("locType");
				map.remove("stgZone");
				map.remove("stgSelectZone");

				// End modified for SMART CR by FPT on 23-Feb-2013
			} else if ("DELETE_OPS".equals(command)) {
				String[] delList = CommonUtil.getRequiredStringParameters(request, "opsCheck");
				// Start added for SMART CR by FPT on 23-Jan-2014
				// inSmartIndList = new ArrayList();
//					Object inSmartInd = criteria.getPredicates().get("inSmartInd");
//					if (inSmartInd != null) {
//						String temp[] = (String[]) inSmartInd;
//						for (int i = 0; i < temp.length; i++) {
//							inSmartIndList.add(temp[i]);
//						}
//					}
				// End added for SMART CR by FPT on 23-Jan-2014
				for (int i = 0; i < delList.length; i++) {
					// String ind = delList[i];
					int ind = Integer.parseInt(criteria.getPredicates().get("opsCheck"));
					// Start added for SMART CR by FPT on 23-Jan-2014
					if (ind == 1) {
						errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M100020");
						// return;
					}
					inSmartIndList.remove(ind);
					// End added for SMART CR by FPT on 23-Jan-2014
					bayNbrList.remove(ind);
					areaUsedList.remove(ind);
					opsStartDttmList.remove(ind);
					opsEndDttmList.remove(ind);
					log.info("=============== bayNbrList: " + bayNbrList);
				}

			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {

				closeDate = request.getParameter("closeDate");
				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");

				MiscSpaceValueObject obj = new MiscSpaceValueObject();
				obj.setBayNbr((String[]) CommonUtil.getRequiredStringParameters(request, "bayNbr"));
				obj.setAreaUsed((String[]) CommonUtil.getRequiredStringParameters(request, "areaUsed"));
				obj.setOpsStartDttm((String[]) CommonUtil.getRequiredStringParameters(request, "opsStartDttm"));
				obj.setOpsEndDttm((String[]) CommonUtil.getRequiredStringParameters(request, "opsEndDttm"));

				log.info("=============== userId: " + userId);
				log.info("=============== miscSeqNbr: " + miscSeqNbr);
				log.info("=============== closeDate: " + closeDate);

				gbMiscApplicationService.closeBillUseOfSpaceDetails(userId, miscSeqNbr, obj, closeDate);
				// map.remove("miscSeqNbr");

				// remove session attributes here
				map.remove("coName");
				map.remove("phone");
				map.remove("add1");
				map.remove("add2");
				map.remove("city");
				map.remove("pin");
				map.remove("account");

				// Application Details
				map.remove("miscSeqNbr");
				map.remove("appTypeCd");
				map.remove("appTypeName");
				map.remove("appStatusCd");
				map.remove("appStatusNm");
				map.remove("appDttm");
				map.remove("submitDttm");
				map.remove("submitBy");
				map.remove("approveDttm");
				map.remove("approveBy");
				map.remove("closeDttm");
				map.remove("closeBy");
				map.remove("remarks");
				map.remove("status");
				// Space Details
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
				map.remove("bayNbr");
				map.remove("areaUsed");
				map.remove("opsStartDttm");
				map.remove("opsEndDttm");
				map.remove("bayNbrList");
				map.remove("areaUsedList");
				map.remove("opsStartDttmList");
				map.remove("opsEndDttmList");
				// Added by Punitha on 24/08/2007 . To hold the vessel details in session
				map.remove("vslName");
				map.remove("varCode");
				map.remove("inVoyNbr");
				map.remove("outVoyNbr");
				map.remove("atbDttm");
				map.remove("atuDttm");
				// Ended by Punitha

				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10

				map.remove("seqNo");
				map.remove("cntrSeqNo");
				map.remove("cntrNo");
				map.remove("status");
				map.remove("iso");
				map.remove("len");
				map.remove("ht");
				map.remove("wt");
				map.remove("impHaulier");
				map.remove("expHaulier");
				map.remove("impTruckNbr");
				map.remove("expTruckNbr");
				map.remove("impGateOut");
				map.remove("expGateIn");
				map.remove("miscAppNo");
				map.remove("arrvStat");
				map.remove("dateCreate");
				map.remove("remarks");
				map.remove("sumTotal");
				map.remove("sumLen");

				// forwardHandler(request, "MiscAppList");
				// return;
			}
			if (bayNbrList != null)
				map.put("bayNbr", bayNbrList.toArray(new String[0]));
			if (areaUsedList != null)
				map.put("areaUsed", areaUsedList.toArray(new String[0]));
			if (opsStartDttmList != null)
				map.put("opsStartDttm", opsStartDttmList.toArray(new String[0]));
			if (opsEndDttmList != null)
				map.put("opsEndDttm", opsEndDttmList.toArray(new String[0]));
			// Start added for SMART CR by FPT on 25-Feb-2014
			if (inSmartIndList != null)
				map.put("inSmartInd", inSmartIndList.toArray(new String[0]));

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseSpace : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseSpace : ", e);
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
			log.info("END: miscAppCloseSpace result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppCloseReeferHandler -->perform
	public ResponseEntity<?> miscAppCloseReefer(HttpServletRequest request, Criteria criteria, Map<String, Object> map)
			throws BusinessException {

		// Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		// Map<String, Object> map = new HashMap<>();

		try {
			log.info("START: miscAppCloseReefer criteria:" + criteria.toString() + ", map: " + map.toString() + ", request: " + request.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;
			String closeDate = null;

			String[] cntrNo = null;
			String[] cntrSize = null;
			String[] cntrStatus = null;
			String[] plugInDt = null;
			String[] plugInTime = null;
			String[] plugOutDt = null;
			String[] plugOutTime = null;
			String[] deliveryDttm = null;
			String[] dnPoNbr = null;
			String[] remarks = null;

			if ("CLOSE_BILL".equals(command)) {
				applyType = criteria.getPredicates().get("applyType");
				appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				applyTypeNm = criteria.getPredicates().get("applyTypeNm");
				List<Object> list = gbMiscApplicationService.getReeferContainerPowerOutletDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				map.put("coCd", coCd);
				// nextScreen(request, "MiscAppCloseReefer");
			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {
				plugInDt = CommonUtil.getRequiredStringParameters(request, "plugInDt");

				closeDate = criteria.getPredicates().get("closeDate");
				cntrNo = CommonUtil.getRequiredStringParameters(request, "cntrNo");
				cntrSize = CommonUtil.getRequiredStringParameters(request, "cntrSize");
				cntrStatus = CommonUtil.getRequiredStringParameters(request, "cntrStatus");
				plugInTime = CommonUtil.getRequiredStringParameters(request, "plugInTime");
				plugOutDt = CommonUtil.getRequiredStringParameters(request, "plugOutDt");
				plugOutTime = CommonUtil.getRequiredStringParameters(request, "plugOutTime");
				deliveryDttm = CommonUtil.getRequiredStringParameters(request, "deliveryDttm");
				dnPoNbr = CommonUtil.getRequiredStringParameters(request, "dnPoNbr");
				remarks = CommonUtil.getRequiredStringParameters(request, "remarks");

				MiscReeferValueObject obj = new MiscReeferValueObject();
				obj.setCntrNo(cntrNo);
				obj.setCntrSize(cntrSize);
				obj.setCntrStatus(cntrStatus);
				obj.setPlugInDt(plugInDt);
				obj.setPlugInTime(plugInTime);
				obj.setPlugOutDt(plugOutDt);
				obj.setPlugOutTime(plugOutTime);
				obj.setDeliveryDttm(deliveryDttm);
				obj.setDnPoNbr(dnPoNbr);
				obj.setRemarks(remarks);

				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.closeBillReeferContainerPowerOutletDetails(userId, miscSeqNbr, obj, closeDate);

				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseReefer : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseReefer : ", e);
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
			log.info("END: miscAppCloseReefer result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppCloseReeferHandler -->perform
	@RequestMapping(value = "/miscAppCloseReefer", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppCloseReefer(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppCloseReefer criteria:" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			String applyType = null;
			String appSeqNbr = null;
			String applyTypeNm = null;
			String closeDate = null;

			String size = criteria.getPredicates().get("size");

			String[] cntrNo = new String[Integer.parseInt(size)];
			String[] cntrSize = new String[Integer.parseInt(size)];
			String[] cntrStatus = new String[Integer.parseInt(size)];
			String[] plugInDt = new String[Integer.parseInt(size)];
			String[] plugInTime = new String[Integer.parseInt(size)];
			String[] plugOutDt = new String[Integer.parseInt(size)];
			String[] plugOutTime = new String[Integer.parseInt(size)];
			String[] deliveryDttm = new String[Integer.parseInt(size)];
			String[] dnPoNbr = new String[Integer.parseInt(size)];
			String[] remarks = new String[Integer.parseInt(size)];

			applyType = criteria.getPredicates().get("applyType");
			appSeqNbr = criteria.getPredicates().get("appSeqNbr");
			applyTypeNm = criteria.getPredicates().get("applyTypeNm");
			if ("CLOSE_BILL".equals(command)) {

				List<Object> list = gbMiscApplicationService.getReeferContainerPowerOutletDetails(userId, applyType,
						appSeqNbr, applyTypeNm);
				// Added on 20/07/2007 by Punitha.To display the vessel details
				MiscAppValueObject appObj = null;
				if (list != null && list.size() > 0) {
					appObj = (MiscAppValueObject) list.get(0);
				}
				List<MiscAppValueObject> vesselList = gbMiscApplicationService.getVesselDetails(appObj.getVarCode());
				map.put("vesselList", vesselList);
				// Ended by Punitha
				map.put("details", list);
				map.put("coCd", coCd);
				// nextScreen(request, "MiscAppCloseReefer");
			} else if ("CLOSE_BILL_SUBMIT".equals(command)) {
				// plugInDt = CommonUtil.getRequiredStringParameters(request, "plugInDt");

				closeDate = criteria.getPredicates().get("closeDate");
				for (int i = 0; i < Integer.parseInt(size); i++) {
					cntrNo[i] = criteria.getPredicates().get("cntrNo" + i);
					cntrSize[i] = criteria.getPredicates().get("cntrSize" + i);
					cntrStatus[i] = criteria.getPredicates().get("cntrStatus" + i);
					plugInDt[i] = criteria.getPredicates().get("plugInDt" + i);
					plugInTime[i] = criteria.getPredicates().get("plugInTime" + i);
					plugOutDt[i] = criteria.getPredicates().get("plugOutDt" + i);
					plugOutTime[i] = criteria.getPredicates().get("plugOutTime" + i);
					deliveryDttm[i] = criteria.getPredicates().get("deliveryDttm" + i);
					dnPoNbr[i] = criteria.getPredicates().get("dnPoNbr" + i);
					remarks[i] = criteria.getPredicates().get("remarks" + i);
				}
				// cntrNo = CommonUtil.getRequiredStringParameters(request,"cntrNo");
				// cntrSize = CommonUtil.getRequiredStringParameters(request,"cntrSize");
				// cntrStatus = CommonUtil.getRequiredStringParameters(request,"cntrStatus");
				// plugInDt = CommonUtil.getRequiredStringParameters(request,"plugInDt");
				// plugInTime = CommonUtil.getRequiredStringParameters(request,"plugInTime");
				// plugOutDt = CommonUtil.getRequiredStringParameters(request,"plugOutDt");
				// plugOutTime = CommonUtil.getRequiredStringParameters(request,"plugOutTime");
				// deliveryDttm =
				// CommonUtil.getRequiredStringParameters(request,"deliveryDttm");
				// dnPoNbr = CommonUtil.getRequiredStringParameters(request,"dnPoNbr");
				// remarks = CommonUtil.getRequiredStringParameters(request,"remarks");

				MiscReeferValueObject obj = new MiscReeferValueObject();
				obj.setCntrNo(cntrNo);
				obj.setCntrSize(cntrSize);
				obj.setCntrStatus(cntrStatus);
				obj.setPlugInDt(plugInDt);
				obj.setPlugInTime(plugInTime);
				obj.setPlugOutDt(plugOutDt);
				obj.setPlugOutTime(plugOutTime);
				obj.setDeliveryDttm(deliveryDttm);
				obj.setDnPoNbr(dnPoNbr);
				obj.setRemarks(remarks);

				String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
				gbMiscApplicationService.closeBillReeferContainerPowerOutletDetails(userId, miscSeqNbr, obj, closeDate);
				map.remove("miscSeqNbr");
				// forwardHandler(request, "MiscAppList");
			}

		} catch (BusinessException e) {
			log.info("Exception miscAppCloseReefer : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppCloseReefer : ", e);
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
			log.info("END: miscAppCloseReefer result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// END CLOSE

	// delegate.helper.gbms.miscApp --> MiscAppSupportStationMacHandler -->perform
	@RequestMapping(value = "/miscAppSupportStationMacMiscList", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppSupportStationMacMiscList(Criteria criteria, Map<String, Object> map, HttpServletRequest request)
			throws BusinessException {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: miscAppSupportStationMacMiscList criteria:" + criteria.toString() + ", map: " + map.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String command = criteria.getPredicates().get("command");

			if ("SUPPORT".equals(command)) {
				String applyType = criteria.getPredicates().get("applyType");
				String appSeqNbr = criteria.getPredicates().get("appSeqNbr");
				String applyTypeNm = criteria.getPredicates().get("applyTypeNm");
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
			log.info("Exception miscAppSupportStationMacMiscList : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppSupportStationMacMiscList : ", e);
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
			log.info("END: miscAppSupportStationMacMiscList result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// ADD Start
	// delegate.helper.gbms.miscApp --> MiscAppAddBargeHandler -->perform
	@RequestMapping(value = "/miscAppAddBarge", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddBarge(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddBarge criteria:" + criteria.toString());

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
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
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
						// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
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
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
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
					// Added on 14/06/2007 by Punitha. To add Contact Person and Contact Tel
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
			log.info("Exception miscAppAddBarge : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddBarge : ", e);
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
			log.info("END: miscAppAddBarge result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAddContractHandler -->perform
	@RequestMapping(value = "/miscAppAddContract", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddContract(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddContract criteria:" + criteria.toString());

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

			// String docType = null;
			// String file = null;
			List<MiscAppValueObject> docTypeList = null;
			List<String> docNameList = null;
			List<String> docCdList = null;
			List<MiscAppValueObject> contractTypeList = null;
			List<String> docMiscNumList = null;

			String applyType = criteria.getPredicates().get("applyType");
			// String applyStatus = criteria.getPredicates().get("applyStatus");
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
				applyStatus = criteria.getPredicates().get("applyStatus");
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
				// licType = (String)session.getAttribute("licType");
				// licNo = (String)session.getAttribute("licNo");
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

					if (criteria.getPredicates().get("docMiscNumList") != null) {
						docMiscNumList = new ArrayList<String>();
						docMiscNumList.add(criteria.getPredicates().get("docMiscNumList"));
					}

					if ("UPLOAD".equals(command)) {
						/*
						 * if(docTypeList == null) { docTypeList = new ArrayList();
						 * docTypeList.add(docType); map.put("docTypeList", docTypeList); }else {
						 * docTypeList.add(docType); } if (docNameList == null) { docNameList = new
						 * ArrayList(); docNameList.add(file); map.put("docNameList", docNameList);
						 * }else { docNameList.add(file); }
						 */
						docTypeList = gbMiscApplicationService.getContractUploadDocumentList();
						map.put("subDirName", "CONTRACT");
						map.put("nextScreen", "MiscAppAddContract");
					} else if ("DELETE".equals(command)) {
						String[] delList =  criteria.getPredicates().get("docCheck").split(",");
						List<MiscAppValueObject> removeTypeList = new ArrayList<MiscAppValueObject>();
						List<String> removeNameList = new ArrayList<String>();
						List<String> removeCdList = new ArrayList<String>();
						/*
						 * for (int i = 0; i < delList.length; i++) { removeTypeList.add((String)
						 * docTypeList.get(Integer.parseInt(delList[i]))); removeNameList.add((String)
						 * docNameList.get(Integer.parseInt(delList[i]))); }
						 */
						
						for (int i = 0; i < delList.length; i++) {
							UploadDocument.deleteFile(FilenameUtils.normalize(folderPath + delList[i]));
							removeTypeList.add(docTypeList.get(Integer.parseInt( delList[i])));
							removeNameList.add(docNameList.get(Integer.parseInt( delList[i])));
							removeCdList.add(docCdList.get(Integer.parseInt( delList[i])));
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
					}
					if (docTypeList != null)
						// map.put("docTypeTemp", docTypeList.toArray(new String[0]));
						map.put("docTypeTemp", docTypeList);
					map.put("DocTypeList", docTypeList);
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
						 * //fileContent = UploadDocument.getFileContent((String)docNameList.get(i));
						 * fileContent = (byte[])docContentList.get(i);
						 * UploadDocument.writeToFile(fileContent, (String)assignFileName.get(i),
						 * "CONTRACT"); } }
						 */
						String miscSeqNbr = gbMiscApplicationService.addContractorPermitDetails(userId, applyType,
								status, cust, account, varcode, coName, location, description, others, fromDate, toDate,
								licType, licNo, remarks, waiver, contCoNm, contCoAddr, contactNm, contactNric,
								designation, docTypeList, docMiscNumList, appDate);
						// remove session attributes
						// removeSessionAttributes(session);
						map.remove("coCd");
						map.put("miscSeqNbr", miscSeqNbr);
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
					applyStatus = criteria.getPredicates().get("applyStatus");
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

					if (waiver == null)
						waiver = "N";

					/*
					 * ArrayList assignFileName = miscAppEjb.addContractorPermitDetails(userId,
					 * applyType, status, cust, account, varcode, coName, location, description,
					 * others, fromDate, toDate, licType, licNo, remarks, waiver, contCoNm,
					 * contCoAddr, contactNm, contactNric, designation, docTypeList, docNameList,
					 * appDate);
					 * 
					 */
					// write file into server
					/*
					 * byte[] fileContent = null; if(docNameList != null && assignFileName != null){
					 * //System.out.println("SUBMIT * assignFileName -----> " +
					 * assignFileName.size()); for(int i = 0; i < docNameList.size(); i++){
					 * //fileContent = UploadDocument.getFileContent((String)docNameList.get(i));
					 * fileContent = (byte[])docContentList.get(i);
					 * UploadDocument.writeToFile(fileContent, (String)assignFileName.get(i),
					 * "CONTRACT"); } }
					 */
					String miscSeqNbr = gbMiscApplicationService.addContractorPermitDetails(userId, applyType, status,
							cust, account, varcode, coName, location, description, others, fromDate, toDate, licType,
							licNo, remarks, waiver, contCoNm, contCoAddr, contactNm, contactNric, designation,
							docTypeList, docNameList, appDate);

					// remove session attributes
					// removeSessionAttributes(session);
					map.remove("coCd");
					map.put("miscSeqNbr", miscSeqNbr);
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
	@RequestMapping(value = "/miscAppAddHotwork", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddHotwork(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddHotwork criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");

			String applyType = criteria.getPredicates().get("applyType");
			// String applyStatus = criteria.getPredicates().get("applyStatus");
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
			log.info("Exception miscAppAddHotwork : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddHotwork : ", e);
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
			log.info("END: miscAppAddHotwork result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAddParkMacHandler -->perform
	@RequestMapping(value = "/miscAppAddParkMac", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddParkMac(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddParkMac criteria:" + criteria.toString());

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
			// String docType = null;
			// String file = null;
			List<MiscAppValueObject> docTypeList = new ArrayList<MiscAppValueObject>();
			List<String> docNameList = new ArrayList<String>();
			List<String> docCdList = new ArrayList<String>();
			List<String> regNbrList = new ArrayList<String>();

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
				// docTypeList = request.getAttribute("docTypeList");
				// docNameList = request.getAttribute("docNameList");
				// docCdList = request.getAttribute("docCdList");
				// regNbrList = request.getAttribute("regNbrList");

				if (criteria.getPredicates().get("docTypeList") != ""
						&& criteria.getPredicates().get("docTypeList") != null) {
					String[] docTyp = criteria.getPredicates().get("docTypeList").split(",");
					MiscAppValueObject[] stringArray = Arrays.copyOf(docTyp, docTyp.length, MiscAppValueObject[].class);
					for (int i = 0; i < stringArray.length; i++) {
						docTypeList.add(stringArray[i]);
					}
				}

				if (criteria.getPredicates().get("docNameList") != ""
						&& criteria.getPredicates().get("docNameList") != null) {
					String[] docNm = criteria.getPredicates().get("docNameList").split(",");
					for (int i = 0; i < docNm.length; i++) {
						docNameList.add(docNm[i]);
					}
				}

				if (criteria.getPredicates().get("docCdList") != ""
						&& criteria.getPredicates().get("docCdList") != null) {
					String[] docCd = criteria.getPredicates().get("docCdList").split(",");
					for (int i = 0; i < docCd.length; i++) {
						docCdList.add(docCd[i]);
					}
				}

				if (criteria.getPredicates().get("regNbrList") != ""
						&& criteria.getPredicates().get("regNbrList") != null) {
					String[] regNo = criteria.getPredicates().get("regNbrList").split(",");
					for (int i = 0; i < regNo.length; i++) {
						regNbrList.add(regNo[i]);
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
				// docTypeList = request.getAttribute("docTypeList");
				// docNameList = request.getAttribute("docNameList");
				// docCdList = request.getAttribute("docCdList");
				// regNbrList = request.getAttribute("regNbrList");

				if (criteria.getPredicates().get("docTypeList") != ""
						&& criteria.getPredicates().get("docTypeList") != null) {
					String[] docTyp = criteria.getPredicates().get("docTypeList").split(",");
					MiscAppValueObject[] stringArray = Arrays.copyOf(docTyp, docTyp.length, MiscAppValueObject[].class);
					for (int i = 0; i < stringArray.length; i++) {
						docTypeList.add(stringArray[i]);
					}
				}

				if (criteria.getPredicates().get("docNameList") != ""
						&& criteria.getPredicates().get("docNameList") != null) {
					String[] docNm = criteria.getPredicates().get("docNameList").split(",");
					for (int i = 0; i < docNm.length; i++) {
						docNameList.add(docNm[i]);
					}
				}

				if (criteria.getPredicates().get("docCdList") != ""
						&& criteria.getPredicates().get("docCdList") != null) {
					String[] docCd = criteria.getPredicates().get("docCdList").split(",");
					for (int i = 0; i < docCd.length; i++) {
						docCdList.add(docCd[i]);
					}
				}

				if (criteria.getPredicates().get("regNbrList") != ""
						&& criteria.getPredicates().get("regNbrList") != null) {
					String[] regNo = criteria.getPredicates().get("regNbrList").split(",");
					for (int i = 0; i < regNo.length; i++) {
						regNbrList.add(regNo[i]);
					}
				}


				appDate = criteria.getPredicates().get("appDate");
				status = criteria.getPredicates().get("status");
				// Amended by Punitha on 01/08/2008
				/*
				 * macType = session.getAttribute("macType"); fromDate = (String)
				 * session.getAttribute("fromDate"); fromTime = (String)
				 * session.getAttribute("fromTime"); toDate = (String)
				 * session.getAttribute("toDate"); toTime = (String)
				 * session.getAttribute("toTime"); remarks = (String)
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

				String miscSeqNbr = gbMiscApplicationService.addParkingOfForkliftShorecrane(userId, applyType, status,
						cust, account, varcode, coName, macType, fromDate, toDate, remarks, docTypeList, docNameList,
						regNbrList, appDate);

				// write file into server
				/*
				 * byte[] fileContent = null; if(docNameList != null && assignFileName != null){
				 * //System.out.println("assignFileName -----> " + assignFileName.size());
				 * for(int i = 0; i < docNameList.size(); i++){ fileContent =
				 * UploadDocument.getFileContent((String)docNameList.get(i));
				 * UploadDocument.writeToFile(fileContent, (String)assignFileName.get(i),
				 * "MACHINE"); } }
				 */
				// remove session attributes
				// removeSessionAttributes(session);
				map.remove("coCd");
				map.put("miscSeqNbr", miscSeqNbr);
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
				// docType = criteria.getPredicates().get("docType");
				// file = criteria.getPredicates().get("file");

				// docTypeList = request.getAttribute("docTypeList");
				// docNameList = request.getAttribute("docNameList");
				// docCdList = request.getAttribute("docCdList");
				// regNbrList = request.getAttribute("regNbrList");

				if (criteria.getPredicates().get("docTypeList") != ""
						&& criteria.getPredicates().get("docTypeList") != null) {
					String[] docTyp = criteria.getPredicates().get("docTypeList").split(",");
					MiscAppValueObject[] stringArray = Arrays.copyOf(docTyp, docTyp.length, MiscAppValueObject[].class);
					for (int i = 0; i < stringArray.length; i++) {
						docTypeList.add(stringArray[i]);
					}
				}

				if (criteria.getPredicates().get("docNameList") != ""
						&& criteria.getPredicates().get("docNameList") != null) {
					String[] docNm = criteria.getPredicates().get("docNameList").split(",");
					for (int i = 0; i < docNm.length; i++) {
						docNameList.add(docNm[i]);
					}
				}

				if (criteria.getPredicates().get("docCdList") != ""
						&& criteria.getPredicates().get("docCdList") != null) {
					String[] docCd = criteria.getPredicates().get("docCdList").split(",");
					for (int i = 0; i < docCd.length; i++) {
						docCdList.add(docCd[i]);
					}
				}

				if (criteria.getPredicates().get("regNbrList") != ""
						&& criteria.getPredicates().get("regNbrList") != null) {
					String[] regNo = criteria.getPredicates().get("regNbrList").split(",");
					for (int i = 0; i < regNo.length; i++) {
						regNbrList.add(regNo[i]);
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
					docTypeList = gbMiscApplicationService.getUploadDocumentTypeList();
					map.put("subDirName", "MACHINE");
					map.put("nextScreen", "MiscAppAddParkMac");
				} else if ("DELETE".equals(command)) {
					/*
					 * String[] delList = request.getParameterValues("docCheck"); ArrayList
					 * removeTypeList = new ArrayList(); ArrayList removeNameList = new ArrayList();
					 * for (int i = 0; i < delList.length; i++) { removeTypeList.add((String)
					 * docTypeList.get(Integer.parseInt(delList[i]))); removeNameList.add((String)
					 * docNameList.get(Integer.parseInt(delList[i]))); } for (int i = 0; i <
					 * removeTypeList.size(); i++) { docTypeList.remove(removeTypeList.get(i)); }
					 * for (int i = 0; i < removeNameList.size(); i++) {
					 * docNameList.remove(removeNameList.get(i)); }
					 */
					String miscSeqNbr = criteria.getPredicates().get("miscSeqNbr");
					String[] delList = criteria.getPredicates().get("docCheck").split(",");
					String[] assignedNameList = criteria.getPredicates().get("assignedName").split(",");

					List<MiscAppValueObject> removeTypeList = new ArrayList<MiscAppValueObject>();
					List<String> removeNameList = new ArrayList<String>();
					List<String> removeCdList = new ArrayList<String>();
					/*
					 * for (int i = 0; i < delList.length; i++) { removeTypeList.add((String)
					 * docTypeList.get(Integer.parseInt(delList[i]))); removeNameList.add((String)
					 * docNameList.get(Integer.parseInt(delList[i]))); }
					 */
					
					for (int i = 0; i < delList.length; i++) {
						UploadDocument.deleteFile(FilenameUtils.normalize(folderPath + delList[i]));
						gbMiscApplicationService.deleteFileData(userId, miscSeqNbr, assignedNameList[i]);
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
				} else if ("REGSUBMIT".equals(command)) {
					macType = criteria.getPredicates().get("macType");
					fromDate = criteria.getPredicates().get("fromDate");
					fromTime = criteria.getPredicates().get("fromTime");
					toDate = criteria.getPredicates().get("toDate");
					toTime = criteria.getPredicates().get("toTime");
					remarks = criteria.getPredicates().get("remarks");
					String regNbr = criteria.getPredicates().get("regNbr");
					if (regNbrList != null) {
						regNbrList.add(regNbr);
					}
				} else if ("REGDELETE".equals(command)) {

					String[] delList = CommonUtil.getRequiredStringParameters(request, "regCheck");
					List<Object> removeList = new ArrayList<Object>();
					for (int i = 0; i < delList.length; i++) {
						removeList.add(regNbrList.get(Integer.parseInt(delList[i])));
						// removeList.add(delList[i]);
					}
					for (int i = 0; i < removeList.size(); i++) {
						regNbrList.remove(removeList.get(i));
					}
				}
				if (docTypeList != null)
					map.put("docTypeTemp", docTypeList);
				map.put("DocTypeList", docTypeList);
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
			log.info("Exception miscAppAddParkMac : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception miscAppAddParkMac : ", e);
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
			log.info("END: miscAppAddParkMac result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.miscApp --> MiscAppAddSpaceHandler -->perform
	@RequestMapping(value = "/miscAppAddSpace", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddSpace(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddSpace criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");

			String applyType = null;
			// String applyStatus = null;
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
			applyStatus = criteria.getPredicates().get("applyStatus");
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
					// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
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
	@RequestMapping(value = "/miscAppAddReefer", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddReefer(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddReefer criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");
			String applyType = null;
			// String applyStatus = null;
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

			// String[] cntrNo = new String[12];
			// String[] cntrSize = new String[12];
			// String[] cntrStatus = new String[12];

			String size = criteria.getPredicates().get("size");
			String[] cntrNo = new String[Integer.parseInt(size)];
			String[] cntrSize = new String[Integer.parseInt(size)];
			String[] cntrStatus = new String[Integer.parseInt(size)];

			applyType = criteria.getPredicates().get("applyType");
			applyStatus = criteria.getPredicates().get("applyStatus");
			cust = criteria.getPredicates().get("cust");
			account = CommonUtility.deNull(criteria.getPredicates().get("account"));
			varcode = criteria.getPredicates().get("varcode");
			coName = criteria.getPredicates().get("coName");

			if ("ADD_SUBMIT".equals(command)) {
				if (agree == null) {
					appDate = criteria.getPredicates().get("appDate");
					status = criteria.getPredicates().get("status");
					// cntrNo = (String[]) CommonUtil.getRequiredStringParameters(request,"cntrNo");
					// cntrSize = (String[])
					// CommonUtil.getRequiredStringParameters(request,"cntrSize");
					// cntrStatus = (String[])
					// CommonUtil.getRequiredStringParameters(request,"cntrStatus");
					for (int i = 0; i < Integer.parseInt(size); i++) {
						cntrNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrNo" + i));
						cntrSize[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrSize" + i));
						cntrStatus[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrStatus" + i));
					}

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
					// cntrNo = (String[]) CommonUtil.getRequiredStringParameters(request,"cntrNo");
					// cntrSize = (String[])
					// CommonUtil.getRequiredStringParameters(request,"cntrSize");
					// cntrStatus = (String[])
					// CommonUtil.getRequiredStringParameters(request,"cntrStatus");
					for (int i = 0; i < Integer.parseInt(size); i++) {
						cntrNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrNo" + i));
						cntrSize[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrSize" + i));
						cntrStatus[i] = CommonUtility.deNull(criteria.getPredicates().get("cntrStatus" + i));
					}
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
	@RequestMapping(value = "/miscAppAddSpreader", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddSpreader(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddSpreader criteria:" + criteria.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");

			String applyType = criteria.getPredicates().get("applyType");
			// String applyStatus = criteria.getPredicates().get("applyStatus");
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
						boolean successBooking = gbMiscApplicationService.addSpreaderDetails(userId, applyType, status,
								cust, account, varcode, coName, spreaderType, fromDate, toDate, remarks, appDate);
						if (!successBooking) {
							errorMessage = "Spreader for the chosen type is fully booked for the specified duration.";
						}
						// else{
						// forwardHandler(request, "MiscAppList");
						// return;
						// }
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

	// delegate.helper.gbms.miscApp --> MiscAppAddStationMacHandler -->perform
	public ResponseEntity<?> miscAppAddStationMac(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddStationMac criteria:" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String command = criteria.getPredicates().get("command");

			String applyType = null;
			// String applyStatus = null;
			String cust = null;
			String coName = null;
			String account = null;
			String varcode = null;
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
			List<String> regNbrList = null;
			List<String> liftCapacityList = null;
			List<String> insuranceNbrList = null;
			List<String> insExpDttmList = null;
			
			applyType = criteria.getPredicates().get("applyType");
			applyStatus = criteria.getPredicates().get("applyStatus");
			cust = criteria.getPredicates().get("cust");
			account = criteria.getPredicates().get("account");
			varcode = criteria.getPredicates().get("varcode");
			coName = criteria.getPredicates().get("coName");

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
				 * macType = session.getAttribute("macType"); fromDate = (String)
				 * session.getAttribute("fromDate"); toDate = (String)
				 * session.getAttribute("toDate");
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

				String miscSeqNbr = gbMiscApplicationService.addStationingOfMacDetails(userId, applyType, status, cust, account, varcode,
						coName, obj, appDate);

				// write file into server
				/*
				 * byte[] fileContent = null; if(docNameList != null && assignFileName != null){
				 * //System.out.println("assignFileName -----> " + assignFileName.size());
				 * for(int i = 0; i < docNameList.size(); i++){ fileContent =
				 * UploadDocument.getFileContent((String)docNameList.get(i));
				 * UploadDocument.writeToFile(fileContent, (String)assignFileName.get(i),
				 * "MACHINE"); } }
				 */

				// remove session attributes
				// removeSessionAttributes(session);
				map.remove("coCd");
				map.put("miscSeqNbr", miscSeqNbr);
				// forwardHandler(request, "MiscAppList");
				// return;
			} else {
				appDate = criteria.getPredicates().get("appDate");
				status = criteria.getPredicates().get("status");
				macType = criteria.getPredicates().get("macType");
				fromDate = criteria.getPredicates().get("fromDate");
				toDate = criteria.getPredicates().get("toDate");
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
					 * for (int i = 0; i < delList.length; i++) { removeTypeList.add((String)
					 * docTypeList.get(Integer.parseInt(delList[i]))); removeNameList.add((String)
					 * docNameList.get(Integer.parseInt(delList[i]))); } for (int i = 0; i <
					 * removeTypeList.size(); i++) { docTypeList.remove(removeTypeList.get(i)); }
					 * for (int i = 0; i < removeNameList.size(); i++) {
					 * docNameList.remove(removeNameList.get(i)); }
					 */

					String[] delList =  criteria.getPredicates().get("docCheck").split(",");
					List<MiscAppValueObject> removeTypeList = new ArrayList<MiscAppValueObject>();
					List<String> removeNameList = new ArrayList<String>();
					List<String> removeCdList = new ArrayList<String>();

					for (int i = 0; i < delList.length; i++) {
						UploadDocument.deleteFile(FilenameUtils.normalize(folderPath + delList[i]));
						removeTypeList.add(docTypeList.get(Integer.parseInt(delList[i])));
						removeNameList.add(docNameList.get(Integer.parseInt(delList[i])));
						removeCdList.add(docCdList.get(Integer.parseInt(delList[i])));
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
					macType = criteria.getPredicates().get("macType");
					fromDate = criteria.getPredicates().get("fromDate");
					toDate = criteria.getPredicates().get("toDate");

					String regNbr = criteria.getPredicates().get("regNbr");
					String liftCapacity = criteria.getPredicates().get("liftCapacity");
					String insuranceNbr = criteria.getPredicates().get("insuranceNbr");
					String insExpDttm = criteria.getPredicates().get("insExpDttm");
					if (regNbrList != null) {
						regNbrList.add(regNbr);
					} 
					if (liftCapacityList != null) {
						liftCapacityList.add(liftCapacity);
					}
					if (insuranceNbrList != null) {
						insuranceNbrList.add(insuranceNbr);
					}
					if (insExpDttmList != null) {
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

	// delegate.helper.gbms.miscApp --> MiscAppAddVehHandler -->perform
	// delegate.helper.gbms.miscApp --> MiscAppAddVehHandler -->perform
	@RequestMapping(value = "/miscAppAddVeh", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddVeh(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddVeh criteria:" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String applyType = null;
			applyType = criteria.getPredicates().get("applyType");
			String agree = criteria.getPredicates().get("agree");
			String command = criteria.getPredicates().get("command");
			
			// String applyStatus = null;
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
			// String[] vehNo = null;
			// String[] cntNo = null;
			// String[] asnNo = null;
			String size = criteria.getPredicates().get("size");
			String[] vehNo = new String[Integer.parseInt(size)];
			String[] cntNo = new String[Integer.parseInt(size)];
			String[] asnNo = new String[Integer.parseInt(size)];
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
			String conPerson = null;
			String conTel = null;
			String conEmail = null;
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

			applyType = criteria.getPredicates().get("applyType");
			applyStatus = criteria.getPredicates().get("applyStatus");
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

					for (int i = 0; i < Integer.parseInt(size); i++) {
						vehNo[i] = CommonUtility.deNull(criteria.getPredicates().get("vehNo" + i));
						cntNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntNo" + i));
						asnNo[i] = CommonUtility.deNull(criteria.getPredicates().get("asnNo" + i));
					}

					// vehNo = CommonUtil.getRequiredStringParameters(request,"vehNo");
					// cntNo = CommonUtil.getRequiredStringParameters(request,"cntNo");
					// asnNo = CommonUtil.getRequiredStringParameters(request,"asnNo");
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
						throw new BusinessException(errorMessage);
					}

					List<String> invalidContainer = gbMiscApplicationService.checkMiscAppOnvContainer(cntNo);
					if (invalidContainer.size() > 0) {
						errorMessage = Arrays.toString(invalidContainer.toArray())
								+ " are invalid container. Please check.";
						throw new BusinessException(errorMessage);
					}
					// Added to not check character ASN
					String tmpAsn = "";

					// added by nasir flage for check asn
					boolean checkAsn = false;
					for (int i = 0; i < asnNo.length; i++) {
						if (asnNo[i] != null || asnNo[i] != "") {
							checkAsn = true;
						}
					}
					if (checkAsn) {
						for (int i = 0; i < asnNo.length; i++) {
							if (!StringUtils.isNumeric(asnNo[i])) {
								log.info("Inside asn***" + asnNo[i]);
								tmpAsn = tmpAsn + " , " + asnNo[i];
							}
						}
						if (tmpAsn != null && !tmpAsn.trim().equalsIgnoreCase("")) {
							errorMessage = tmpAsn + " are invalid Asn. Please check.";
							throw new BusinessException(errorMessage);
						}
						List<String> invalidAsn = gbMiscApplicationService.checkMiscAppOnvAsn(asnNo);
						if (invalidAsn.size() > 0) {
							errorMessage = Arrays.toString(invalidAsn.toArray()) + " are invalid Asn. Please check.";
							throw new BusinessException(errorMessage);
						}
					}

					if ("D".equals(status)) {
						/*
						 * miscAppCustService.addOvernightParkingVehicleDetails(userId, applyType,
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
					// vehNo = (String[]) CommonUtil.getRequiredStringParameters(request,"vehNo");
					// cntNo = (String[]) CommonUtil.getRequiredStringParameters(request,"cntNo");
					// asnNo = (String[]) CommonUtil.getRequiredStringParameters(request,"asnNo");

					for (int i = 0; i < Integer.parseInt(size); i++) {
						vehNo[i] = CommonUtility.deNull(criteria.getPredicates().get("vehNo" + i));
						cntNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntNo" + i));
						asnNo[i] = CommonUtility.deNull(criteria.getPredicates().get("asnNo" + i));
					}

					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
					conPerson = criteria.getPredicates().get("conPerson");
					conTel = criteria.getPredicates().get("conTel");
					conEmail = criteria.getPredicates().get("conEmail");
					// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

					// Added by Nasir on 07/05/2021
					List<String> invalidChassis = gbMiscApplicationService.checkMiscAppOnvChassis(vehNo);
					if (invalidChassis.size() > 0) {
						errorMessage = Arrays.toString(invalidChassis.toArray())
								+ " does not registered in Vehicle Info Registration module. Please register first";
						// return;
						throw new BusinessException(errorMessage);
					}

					List<String> invalidContainer = gbMiscApplicationService.checkMiscAppOnvContainer(cntNo);
					if (invalidContainer.size() > 0) {
						errorMessage = Arrays.toString(invalidContainer.toArray())
								+ " are invalid container. Please check.";
						throw new BusinessException(errorMessage);
					}
					// Added to not check character ASN
					String tmpAsn = "";

					// added by nasir flage for check asn
					boolean checkAsn = false;
					for (int i = 0; i < asnNo.length; i++) {
						if (asnNo[i] != null) {
							checkAsn = true;
						}
					}
					if (checkAsn) {
						for (int i = 0; i < asnNo.length; i++) {
							if (!StringUtils.isNumeric(asnNo[i])) {
								log.info("Inside asn***" + asnNo[i]);
								tmpAsn = tmpAsn + " , " + asnNo[i];
							}
						}
						if (tmpAsn != null && !tmpAsn.trim().equalsIgnoreCase("")) {
							errorMessage = tmpAsn + " are invalid Asn. Please check.";
							throw new BusinessException(errorMessage);
						}
						List<String> invalidAsn = gbMiscApplicationService.checkMiscAppOnvAsn(asnNo);
						if (invalidAsn.size() > 0) {
							errorMessage = Arrays.toString(invalidAsn.toArray()) + " are invalid Asn. Please check.";
							throw new BusinessException(errorMessage);
						}
					}

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
	@RequestMapping(value = "/miscAppAddTpa", method = RequestMethod.POST)
	public ResponseEntity<?> miscAppAddTpa(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: miscAppAddTpa criteria:" + criteria.toString());
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
			String conPerson = null;
			String conTel = null;

			String reasonForApp = null;
			String noHours = null;
			String cargoType = null;

			applyType = criteria.getPredicates().get("applyType");
			applyStatus = criteria.getPredicates().get("applyStatus");
			cust = criteria.getPredicates().get("cust");
			account = criteria.getPredicates().get("account");
			varcode = criteria.getPredicates().get("varcode");
			coName = criteria.getPredicates().get("coName");

			if ("ADD_SUBMIT".equals(command)) {
				String size = criteria.getPredicates().get("size");
				String[] vehNo = new String[Integer.parseInt(size)];
				String[] cntNo = new String[Integer.parseInt(size)];
				String[] asnNo = new String[Integer.parseInt(size)];
				String[] preferredArea = new String[Integer.parseInt(size)];
				String[] remarks = new String[Integer.parseInt(size)];

				appDate = criteria.getPredicates().get("appDate");
				status = criteria.getPredicates().get("status");
				fromDate = criteria.getPredicates().get("fromDate") + " " + criteria.getPredicates().get("fromTime");
				toDate = criteria.getPredicates().get("toDate") + " " + criteria.getPredicates().get("toTime");
				conPerson = criteria.getPredicates().get("conPerson");
				conTel = criteria.getPredicates().get("conTel");
				reasonForApp = criteria.getPredicates().get("reasonForApp");
				cargoType = criteria.getPredicates().get("cargoType");
				noHours = criteria.getPredicates().get("noHours");
				appRemarks = criteria.getPredicates().get("appRemarks");

				for (int i = 0; i < Integer.parseInt(size); i++) {
					vehNo[i] = CommonUtility.deNull(criteria.getPredicates().get("vehNo" + i));
					cntNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntNo" + i));
					asnNo[i] = CommonUtility.deNull(criteria.getPredicates().get("asnNo" + i));
					preferredArea[i] = CommonUtility.deNull(criteria.getPredicates().get("preferredArea" + i));
					remarks[i] = CommonUtility.deNull(criteria.getPredicates().get("remarks" + i));
				}

//					preferredArea = new String[5];
//					preferredArea[0] = criteria.getPredicates().get("preferredArea1");
//					preferredArea[1] = criteria.getPredicates().get("preferredArea2");
//					preferredArea[2] = criteria.getPredicates().get("preferredArea3");
//					preferredArea[3] = criteria.getPredicates().get("preferredArea4");
//					preferredArea[4] = criteria.getPredicates().get("preferredArea5");
//					vehNo = request.getParameterValues("vehNo");
//					cntNo = request.getParameterValues("cntNo");
//					asnNo = request.getParameterValues("asnNo");
//					remarks = request.getParameterValues("remarks");

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
						this.miscAppAddTpa(criteria, map);
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
								this.miscAppAddTpa(criteria, map);
								// nextScreen(request, "MiscAppAddTpa");
								// return;
							} else {
								// To get application, vehicle detail information and redirect to auto approve
								// page
								List<Object> list = gbMiscApplicationService.getTrailerParkingApplicationDetails(userId,
										applyType, miscSeqNbr, "");

								map.put("details", list);
								map.put("autoApprove", "true");
								this.miscAppAddTpa(criteria, map);
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
				List<Map<String, String>> calParkAreaList= new ArrayList<Map<String, String>>();
				Map<String, String> parkAreaValuesMap = new LinkedHashMap<String, String>();
				for (int i = 0; i < miscParkAreaList.size(); i++) {
					MiscAppParkingAreaObject miscAppParkingAreaObject = (MiscAppParkingAreaObject) miscParkAreaList
							.get(i);
					String description = miscAppParkingAreaObject.getAreaCode() + "("
							+ miscAppParkingAreaObject.getNoOfSlot() + ")";
					parkAreaValuesMap.put(miscAppParkingAreaObject.getAreaCode(), description);
				}
				calParkAreaList.add(parkAreaValuesMap);
				map.put("MiscParkAreaList", calParkAreaList);
				this.miscAppAddTpa(criteria, map);
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
				this.miscAppAddTpa(criteria, map);
				// nextScreen(request, "MiscAppAddTpa");
			} else {
				// get list of parking reasons
				List<Map<String, Object>> miscParkReasonList = gbMiscApplicationService.getParkingReasonList();
				map.put("MiscParkReasonList", miscParkReasonList);
				int dateRange = gbMiscApplicationService.getTpaDatePeriod();
				map.put("dateRange", dateRange + "");

				// nextScreen(request, "MiscAppAddTpa");
				this.miscAppAddTpa(criteria, map);
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

	private Map<String, Object> miscAppAddTpa(Criteria criteria, Map<String, Object> map) {
		try {
			log.info("START: miscAppAddTpa criteria: " + criteria  + ", map: " + map.toString());
			String errorCode = CommonUtility.deNull(criteria.getPredicates().get("errorCode"));
			String autoApprove = CommonUtility.deNull(criteria.getPredicates().get("autoApprove"));
			// String msgInfo = "MiscAppAddTpaServlet -  : ";
			String redirectName = null;
			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));
			if ("APPLICATION_ENQUIRE".equals(command)) {
				log.info("Process request for enquire");
				//	redirectName  = "/JSP/gbms/miscApp/MiscAppEnquireSlot.jsp";
				//	redirect(request, response, redirectName);
				return null;
			}

			List<MiscAppValueObject> vesselList = null;
			Object listObject = null;
			listObject =  map.get("vesselList");
			vesselList = new ArrayList<MiscAppValueObject>();
			if (listObject instanceof List){
				for(int j = 0; j < ((List<?>)listObject).size(); j++){
				Object item = ((List<?>) listObject).get(j);
				if(item instanceof MiscAppValueObject){
					vesselList.add((MiscAppValueObject) item);
				}
			    }
			}
			String vslName = null;
			String varCode = null;
			String inVoyNbr = null;
			String outVoyNbr = null;
			String atbDttm = null;
			String atuDttm = null;
			if (vesselList != null) {
				for (int i = 0; i < vesselList.size(); i++) {
					MiscAppValueObject miscAppValueObject = (MiscAppValueObject) vesselList.get(i);
					vslName = (String) CommonUtility.deNull(miscAppValueObject.getVslName());
					varCode = (String) CommonUtility.deNull(miscAppValueObject.getVarCode());
					inVoyNbr = (String) CommonUtility.deNull(miscAppValueObject.getInVoyNbr());
					outVoyNbr = (String) CommonUtility.deNull(miscAppValueObject.getOutVoyNbr());
					atbDttm = (String) CommonUtility.deNull(miscAppValueObject.getAtbDttm());
					atuDttm = (String) CommonUtility.deNull(miscAppValueObject.getAtuDttm());
				}

			}
			log.info("vslName:" + vslName);
			log.info("varCode:" + varCode);

			map.put("vslName", vslName);
			map.put("varCode", varCode);
			map.put("inVoyNbr", inVoyNbr);
			map.put("outVoyNbr", outVoyNbr);
			map.put("atbDttm", atbDttm);
			map.put("atuDttm", atuDttm);
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
					List<MiscVehValueObject> lists = null;
					List<MiscAppValueObject> list = null;
					listObject = null;
					listObject =  map.get("details");
					lists = new ArrayList<MiscVehValueObject>();
					list = new ArrayList<MiscAppValueObject>();
					if (listObject instanceof List){
						for(int j = 0; j < ((List<?>)listObject).size(); j++){
						Object item = ((List<?>) listObject).get(j);
						if(item instanceof MiscVehValueObject){
							lists.add((MiscVehValueObject) item);
						} else if(item instanceof MiscAppValueObject){
							list.add((MiscAppValueObject) item);
						}
					    }
					}
					
					MiscAppValueObject appObj = null;
					MiscVehValueObject vehObj = null;
					// MiscCustValueObject custObj = null;

					if (list != null && list.size() > 0) {
						appObj = (MiscAppValueObject) list.get(0);
						// custObj = (MiscCustValueObject) list.get(1);
						vehObj = (MiscVehValueObject) lists.get(2);
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
					String size = criteria.getPredicates().get("size");
					String[] vehNo = new String[Integer.parseInt(size)];
					String[] cntNo = new String[Integer.parseInt(size)];
					String[] asnNo = new String[Integer.parseInt(size)];
					String[] preferredArea = new String[Integer.parseInt(size)];
					String[] remarks = new String[Integer.parseInt(size)];

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

					for (int i = 0; i < Integer.parseInt(size); i++) {
						vehNo[i] = CommonUtility.deNull(criteria.getPredicates().get("vehNo" + i));
						cntNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntNo" + i));
						asnNo[i] = CommonUtility.deNull(criteria.getPredicates().get("asnNo" + i));
						preferredArea[i] = CommonUtility.deNull(criteria.getPredicates().get("preferredArea" + i));
						remarks[i] = CommonUtility.deNull(criteria.getPredicates().get("remarks" + i));
					}
//	        			String vehNo = CommonUtility.deNull(criteria.getPredicates().get("vehNo"));
//	        			String cntNo = CommonUtility.deNull(criteria.getPredicates().get("cntNo"));
//	        			String asnNo = CommonUtility.deNull(criteria.getPredicates().get("asnNo"));
//	        			String remark = CommonUtility.deNull(criteria.getPredicates().get("remarks"));
//	        			String preferredArea1 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea1"));
//	        			String preferredArea2 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea2"));
//	        			String preferredArea3 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea3"));
//	        			String preferredArea4 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea4"));
//	        			String preferredArea5 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea5"));

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

//	    				String vehChasNbr = vehNo;
//	    				String cntrNbr = cntNo;
//	    				String asnNbr = asnNo;
//	    				String[] preferredArea = new String[5];
//	    				String remarks = remark;

//	    				preferredArea[0] = preferredArea1;
//	    				preferredArea[1] = preferredArea2;
//	    				preferredArea[2] = preferredArea3;
//	    				preferredArea[3] = preferredArea4;
//	    				preferredArea[4] = preferredArea5;

					map.put("vehChasNbr", vehNo);
					map.put("cntrNbr", cntNo);
					map.put("asnNbr", asnNo);
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
//	            redirect(request, response, redirectName);
		} catch (Exception e) {
			log.info("Exception miscAppAddTpa : ", e);
		}
		
		log.info("END: miscAppAddTpa ***** map: " + map.toString());
		
		return map;
	}

	private Map<String, Object> miscAppUpdateTpa(Criteria criteria, Map<String, Object> map) {
		try {
			log.info("START: miscAppUpdateTpa criteria: " + criteria + ", map: " + map.toString());
			String errorCode = CommonUtility.deNull(criteria.getPredicates().get("errorCode"));
			String autoApprove = CommonUtility.deNull(criteria.getPredicates().get("autoApprove"));
			// String msgInfo = "MiscAppAddTpaServlet -  : ";
			String command = CommonUtility.deNull(criteria.getPredicates().get("command"));
			if ("APPLICATION_ENQUIRE".equals(command)) {
				log.info("Process request for enquire");
				//	redirectName  = "/JSP/gbms/miscApp/MiscAppEnquireSlot.jsp";
				//	redirect(request, response, redirectName);
				return null;
			}

			try {
				if ("UPDATE".equals(command)) {

					List<MiscAppValueObject> list = null;
					List<MiscCustValueObject> custList = null;
					List<MiscVehValueObject> vehList = null;
					MiscAppValueObject appObj = null;
					MiscVehValueObject vehObj = null;
					MiscCustValueObject custObj = null;
					
					Object listObject = null;
					listObject =  map.get("details"); 
					
					list = new ArrayList<MiscAppValueObject>();
					custList = new ArrayList<MiscCustValueObject>();
					vehList = new ArrayList<MiscVehValueObject>();
					if (listObject instanceof List) {
						for (int j = 0; j < ((List<?>) listObject).size(); j++) {
							Object item = ((List<?>) listObject).get(j);
							if (item instanceof MiscAppValueObject) {
								list.add((MiscAppValueObject) item);
							} else if (item instanceof MiscCustValueObject) {
								custList.add((MiscCustValueObject) item);
							} else if (item instanceof MiscVehValueObject) {
								vehList.add((MiscVehValueObject) item);
							}
						}
					}

					if (list != null && list.size() > 0) {
						appObj = (MiscAppValueObject) list.get(0);
						custObj = (MiscCustValueObject) custList.get(0);
						vehObj = (MiscVehValueObject) vehList.get(0);
					}

					this.calAppDetails(map, appObj, custObj, criteria);

					String conPerson = appObj.getConPerson();
					String conTel = appObj.getConTel();

					// Veh Details
					String fromDate = vehObj.getFromDate();
					String toDate = vehObj.getToDate();
					String fromTime = vehObj.getFromTime();
					String toTime = vehObj.getToTime();

					String noHours = vehObj.getNoHours();
					String parkReason = vehObj.getParkReason();
					String reasonForApp = vehObj.getApplicationReason();
					String cargoType = vehObj.getCargoType();

					String[] vehChasNbr = vehObj.getVehChasNbr();
					String[] cntrNbr = vehObj.getCntrNbr();
					String[] asnNbr = vehObj.getAsnNbr();
					String[] prefArea = vehObj.getPreferredArea();
					String[] remarks = vehObj.getRemarks();

					map.put("conPerson", conPerson);
					map.put("conTel", conTel);
					map.put("fromDate", fromDate);
					map.put("toDate", toDate);
					map.put("fromTime", fromTime);
					map.put("toTime", toTime);
					map.put("noHours", noHours);
					map.put("appRemarks", parkReason);
					map.put("reasonForApp", reasonForApp);
					map.put("cargoType", cargoType);
					map.put("vehChasNbr", vehChasNbr);
					map.put("cntrNbr", cntrNbr);
					map.put("asnNbr", asnNbr);
					map.put("prefArea", prefArea);
					map.put("remarks", remarks);

					// redirectName = "/JSP/gbms/miscApp/MiscAppUpdateTpa.jsp";            
				} else if ("UPDATE_SUBMIT".equals(command)) {
					if ("true".equalsIgnoreCase(autoApprove)) {
						
						List<MiscAppValueObject> list = null;
						List<MiscVehValueObject> vehlist = null;
						Object listObject = null;
						listObject = map.get("details");
						list = new ArrayList<MiscAppValueObject>();
						vehlist = new ArrayList<MiscVehValueObject>();
						if (listObject instanceof List) {
							for (int j = 0; j < ((List<?>) listObject).size(); j++) {
								Object item = ((List<?>) listObject).get(j);
								if (item instanceof MiscAppValueObject) {
									list.add((MiscAppValueObject) item);
								} else if (item instanceof MiscVehValueObject) {
									vehlist.add((MiscVehValueObject) item);
								}
							}
						}

						MiscAppValueObject appObj = null;
						MiscVehValueObject vehObj = null;

						if (list != null && list.size() > 0) {
							appObj = (MiscAppValueObject) list.get(0);
							vehObj = (MiscVehValueObject) vehlist.get(0);
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

						map.put("appRefNbr", appRefNbr);
						map.put("vehChasNbr", vehChasNbr);
						map.put("cntrNbr", cntrNbr);
						map.put("asnNbr", asnNbr);
						map.put("prefArea", prefArea);
						map.put("remarks", remarks);
						map.put("area", area);
						map.put("slot", slot);

						// redirectName = "/JSP/gbms/miscApp/MiscAppAutoApproveTpa.jsp";
					} else if (errorCode != null && !"0".equalsIgnoreCase(errorCode)) {

						List<MiscAppValueObject> list = null;
						List<MiscCustValueObject> lists = null;
						Object listObject = null;
						listObject =  map.get("details");
						list = new ArrayList<MiscAppValueObject>();
						lists = new ArrayList<MiscCustValueObject>();
						if (listObject instanceof List){
							for(int j = 0; j < ((List<?>)listObject).size(); j++){
							Object item = ((List<?>) listObject).get(j);
							if(item instanceof MiscAppValueObject){
								list.add((MiscAppValueObject) item);
							} else if(item instanceof MiscCustValueObject){
								lists.add((MiscCustValueObject) item);
							}
						    }
						}
						
						MiscAppValueObject appObj = null;
						MiscCustValueObject custObj = null;

						if (list != null && list.size() > 0) {
							appObj = (MiscAppValueObject) list.get(0);
							custObj = (MiscCustValueObject) lists.get(0);
						}

						this.calAppDetails(map, appObj, custObj, criteria);

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
//	            			String vehNo = CommonUtility.deNull(criteria.getPredicates().get("vehNo"));
//	            			String cntNo = CommonUtility.deNull(criteria.getPredicates().get("cntNo"));
//	            			String asnNo = CommonUtility.deNull(criteria.getPredicates().get("asnNo"));
//	            			String remark = CommonUtility.deNull(criteria.getPredicates().get("remarks"));
//	            			String preferredArea1 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea1"));
//	            			String preferredArea2 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea2"));
//	            			String preferredArea3 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea3"));
//	            			String preferredArea4 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea4"));
//	            			String preferredArea5 = CommonUtility.deNull(criteria.getPredicates().get("preferredArea5"));

						String size = criteria.getPredicates().get("size");
						String[] vehNo = new String[Integer.parseInt(size)];
						String[] cntNo = new String[Integer.parseInt(size)];
						String[] asnNo = new String[Integer.parseInt(size)];
						String[] preferredArea = new String[Integer.parseInt(size)];
						String[] remarks = new String[Integer.parseInt(size)];

						for (int i = 0; i < Integer.parseInt(size); i++) {
							vehNo[i] = CommonUtility.deNull(criteria.getPredicates().get("vehNo" + i));
							cntNo[i] = CommonUtility.deNull(criteria.getPredicates().get("cntNo" + i));
							asnNo[i] = CommonUtility.deNull(criteria.getPredicates().get("asnNo" + i));
							preferredArea[i] = CommonUtility.deNull(criteria.getPredicates().get("preferredArea" + i));
							remarks[i] = CommonUtility.deNull(criteria.getPredicates().get("remarks" + i));
						}

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
//	        				
//	        				String vehChasNbr = vehNo;
//	        				String cntrNbr = cntNo;
//	        				String asnNbr = asnNo;
//	        				String[] preferredArea = new String[5];
//	        				String remarks = remark;
//	        	
//	        				preferredArea[0] = preferredArea1;
//	        				preferredArea[1] = preferredArea2;
//	        				preferredArea[2] = preferredArea3;
//	        				preferredArea[3] = preferredArea4;
//	        				preferredArea[4] = preferredArea5;

						map.put("vehChasNbr", vehNo);
						map.put("cntrNbr", cntNo);
						map.put("asnNbr", asnNo);
						map.put("prefArea", preferredArea);
						map.put("remarks", remarks);

						// redirectName = "/JSP/gbms/miscApp/MiscAppUpdateTpa.jsp";
					} else {
						// redirectName = "/JSP/gbms/miscApp/MiscAppList.jsp";
					}
				} else if ("POPUP".equals(command)) {
					// redirectName = "/JSP/gbms/miscApp/MiscAppAddTpaPopup.jsp";
				}
				//	redirect(request, response, redirectName);
			} catch (Exception e) {
				log.info("Exception miscAppUpdateTpa : ", e);
			}

		} catch (Exception e) {
			log.info("Exception miscAppUpdateTpa : ", e);
		}
		log.info("END: miscAppUpdateTpa ***** map: " + map.toString());
		return map;
	}

	private void calAppDetails(Map<String, Object> map, MiscAppValueObject appObj, MiscCustValueObject custObj,
			Criteria criteria) {
		
		log.info("START: calAppDetails ***** criteria: " + criteria.toString() + ", map: " + map.toString() + ", appObj: " + appObj.toString() + ", custObj: " + custObj.toString() );
		String vslName = null;
		String varCode = null;
		String inVoyNbr = null;
		String outVoyNbr = null;
		String atbDttm = null;
		String atuDttm = null;
		
		List<MiscAppValueObject> vesselList = null;
		Object listObject = null;
		listObject = map.get("details");
		vesselList = new ArrayList<MiscAppValueObject>();
		if (listObject instanceof List) {
			for (int j = 0; j < ((List<?>) listObject).size(); j++) {
				Object item = ((List<?>) listObject).get(j);
				if (item instanceof MiscAppValueObject) {
					vesselList.add((MiscAppValueObject) item);
				}
			}
		}  

		MiscAppValueObject appVal = null;
		if (vesselList != null && vesselList.size() > 0) {
			appVal = (MiscAppValueObject) vesselList.get(0);

			vslName = (String) CommonUtility.deNull(appVal.getVslName());
			varCode = (String) CommonUtility.deNull(appVal.getVarCode());
			inVoyNbr = (String) CommonUtility.deNull(appVal.getInVoyNbr());
			outVoyNbr = (String) CommonUtility.deNull(appVal.getOutVoyNbr());
			atbDttm = (String) CommonUtility.deNull(appVal.getAtbDttm());
			atuDttm = (String) CommonUtility.deNull(appVal.getAtuDttm());
		}

		// Customer Details
		String coName = custObj.getCoName();
		String phone = custObj.getContact1();
		String add1 = custObj.getAddress1();
		String add2 = custObj.getAddress2();
		String city = custObj.getCity();
		String pin = custObj.getPin();
		String account = custObj.getAcctNbr();

		// Application Details
		String appTypeCd = appObj.getAppTypeCd();
		String appTypeName = appObj.getAppTypeName();
		String appStatusCd = appObj.getAppStatusCd();
		String appStatusNm = appObj.getAppStatusName();
		String appDttm = appObj.getAppDttm();
		String submitDttm = appObj.getSubmitDttm();
		String submitBy = appObj.getSubmitBy();
		String miscSeqNbr = appObj.getAppSeqNbr();
		String appRefNbr = appObj.getAppRefNbr();

		map.put("coName", coName);
		map.put("phone", phone);
		map.put("add1", add1);
		map.put("add2", add2);
		map.put("city", city);
		map.put("pin", pin);
		map.put("account", account);
		map.put("vslName", vslName);
		map.put("varCode", varCode);
		map.put("inVoyNbr", inVoyNbr);
		map.put("outVoyNbr", outVoyNbr);
		map.put("atbDttm", atbDttm);
		map.put("atuDttm", atuDttm);

		// start by TungNQ1
		String[] cargoTypeNormal = { CARGO_TYPE_NORMAL, cargoTypeValues[0] };
		String[] cargoTypeDG = { CARGO_TYPE_DG, cargoTypeValues[1] };
		String[] cargoTypeOOG = { CARGO_TYPE_OOG, cargoTypeValues[2] };

		List<String[]> cargoTypeList = new ArrayList<String[]>();
		cargoTypeList.add(cargoTypeNormal);
		cargoTypeList.add(cargoTypeDG);
		cargoTypeList.add(cargoTypeOOG);

		map.put("hourList", Constants.HOUR_LIST);
		map.put("cargoTypeList", cargoTypeList);

		// Application Details
		map.put("miscSeqNbr", miscSeqNbr);
		map.put("appRefNbr", appRefNbr);
		map.put("appTypeCd", appTypeCd);
		map.put("appTypeName", appTypeName);
		map.put("appStatusCd", appStatusCd);
		map.put("appStatusNm", appStatusNm);
		map.put("appDttm", appDttm);
		map.put("submitDttm", submitDttm);
		map.put("submitBy", submitBy);

		String dateRange = criteria.getPredicates().get("dateRange");
		if (dateRange != null && !"".equals(dateRange) && Integer.parseInt(dateRange) > 0) {
			map.put("dateRange", dateRange);
		}
		
		log.info("END: calAppDetails");
	}

	private String validate(HttpServletRequest request, GBMiscApplicationListService gbMiscApplicationService,
			String[] vehNo, String[] preferredArea) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		log.info("START: validate ***** criteria: " + criteria.toString());
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();

		String errorCode = "0";
		// Submit - need to validate availability of parking slot
		String fromDate = criteria.getPredicates().get("fromDate") + " " + criteria.getPredicates().get("fromTime");
		String toDate = criteria.getPredicates().get("toDate") + " " + criteria.getPredicates().get("toTime");
		String cargoType = criteria.getPredicates().get("cargoType");

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
			log.info("END: validate errorCode: " + errorCode.toString());
		}

		return errorCode;
	}
	// ADD End

	// start file attachment and retrive

	@ApiOperation(value = "Get fileAttachments Details List", response = String.class)
	@PostMapping(value = "/getFileAttachmentsDetailList")
	public ResponseEntity<?> getFileAttachmentsDetailList(HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getFileAttachmentsDetailList ***** criteria: " + criteria.toString());
			List<AttachmentFileValueObject> fileAttachmentList = gbMiscApplicationService
					.getFileAttachmentList(criteria);
			result.setData(fileAttachmentList);
			log.info("result:" + result.toString());
			result.setSuccess(true);
		} catch (Exception e) {
			log.info("Exception getFileAttachmentsDetailList : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
			result.setSuccess(false);
		} finally {
			log.info("END: getFileAttachmentsDetailList ");
		}
		
		log.info("END: getFileAttachmentsDetailList result:" + result.toString());
		return ResponseEntityUtil.success(result.toString());
	}

	@ApiOperation(value = "fileAttachment", response = String.class)
	@RequestMapping(value = "/fileAttachment", method = RequestMethod.POST)
	public ResponseEntity<?> fileAttachment(@RequestParam("files") MultipartFile[] uploadingFiles,
			HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: fileAttachment ***** criteria: " + criteria.toString());
			result = gbMiscApplicationService.saveFileAttachment(criteria, uploadingFiles);
			log.info("result:" + result.toString());

			map.put("data", result.getData());
			result.setData(map);
			result.setSuccess(true);

		} catch (Exception ex) {
			log.info("Exception fileAttachment : ", ex);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M0010");
		}
		
		log.info("END: fileAttachment ***** result: " + result.toString());
		return ResponseEntityUtil.success(result.toString());
	}

	@ApiOperation(value = "downloadAttachment", response = String.class)
	@RequestMapping(value = "/downloadAttachment", method = RequestMethod.POST)
	public ResponseEntity<Resource> DownloadAttachment(HttpServletRequest request) {
		Resource resource = null;
		errorMessage = null;
		try {
			
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: downloadAttachment ***** criteria: " + criteria.toString());
			resource = gbMiscApplicationService.fileDownload(criteria);
			String contentType = null;
			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (Exception ex) {
				log.info("Could not determine file type.");
			}

			// Fallback to the default content type if type could not be determined
			if (contentType == null) {
				contentType = "application/octet-stream";
			}
			
			log.info("END: downloadAttachment *****");

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception ex) {
			log.info("Exception downloadAttachment: ", ex);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resource);

		}
	}

	// end file attachment and retrive
	
	
	@ApiOperation(value = "Get User Info", response = String.class)
	@RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
	public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> userInfo = new HashMap<String, String>();
		Result result = new Result();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** getUserInfo Start criteria :" + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			
			userInfo = gbMiscApplicationService.getUserInfo(userId);
			
			map.put("userInfo", userInfo);
			map.put("timestamp", CommonUtility.parseDateToFmtStr(CommonUtility.getSysDate(),"ddMMyyyy HHmm"));
			
		} catch (Exception e) {
			log.info("Exception getUserInfo : ", e);
			errorMessage = ConstantUtil.GB_MISCAPP_CONSTANT_MAP.get("M4201");
		} finally {
			result = new Result();
			result.setData(map);
			result.setSuccess(true);
			log.info("END: getUserInfo result:" + result.toString());

		}

		return ResponseEntityUtil.success(result.toString());
	}

	// EndRegion MiscAppListHandler
}
