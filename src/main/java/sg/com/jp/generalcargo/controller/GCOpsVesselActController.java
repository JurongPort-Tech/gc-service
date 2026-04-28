package sg.com.jp.generalcargo.controller;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import sg.com.jp.eventmsg.EventMsgUtil;
import sg.com.jp.generalcargo.domain.BillVO;
import sg.com.jp.generalcargo.domain.BillingCodesVO;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EmailValueObject;
import sg.com.jp.generalcargo.domain.EventDetails;
import sg.com.jp.generalcargo.domain.EventDetails.VslEvent;
import sg.com.jp.generalcargo.domain.GroupVO;
import sg.com.jp.generalcargo.domain.LineTowedVesselValueObject;
import sg.com.jp.generalcargo.domain.OSDExemptionClauses;
import sg.com.jp.generalcargo.domain.OSDReviewObject;
import sg.com.jp.generalcargo.domain.OpsValueObject;
import sg.com.jp.generalcargo.domain.OverStayDockageValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.SmartInterfaceInputVO;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselActValueObject;
import sg.com.jp.generalcargo.domain.WaiverCodesVO;
import sg.com.jp.generalcargo.restclient.SmartServiceRestClient;
import sg.com.jp.generalcargo.service.GCOpsVesselActService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;
import sg.com.jp.generalcargo.util.SmartInterfaceConstants;

@CrossOrigin
@RestController
@RequestMapping(value = GCOpsVesselActController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class GCOpsVesselActController {

	public static final String ENDPOINT = "gc/gcOps";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";

	private static final Log log = LogFactory.getLog(GCOpsVesselActController.class);
	private String errorMessage = null;

	@Value("${JpGlassThreshold.TimeElapsedNotTrgAlert}")
	private String jpGlassThreshold;

	@Value("${kafka.event.log.url}")
	private String eventLogUrl;

	// CH-7 OSD ---------Start-------------------

	@Value("${review.osd.gcManager.sender.Email}")
	private String newGcManagerSender;

	@Value("${review.osd.gcManager.Recipient.Email}")
	private String RecipientEmail;

	@Value("${review.osd.file.attachment.path}")
	private String fileUploadDirectory;

	@Value("${review.osd.Message.Email.Template}")
	private String gcManagerTemplate;

	@Value("${review.osd.query.Template}")
	private String reviewOsdQueryTemplate;

	// CH-7 -------------- End ------------------

	@Value("${smart.rest.client.url}")
	private String smartBaseUrl;

	@Autowired
	private SmartServiceRestClient smartServiceRestClient;

	@Autowired
	private GCOpsVesselActService vesselActService;

	// delegate.helper.gbms.ops.vesselact -->VesselActListHandler
	@PostMapping(value = "/vesselActList")
	public ResponseEntity<?> vesselActList(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: vesselActList criteria:" + criteria.toString());

			String vslnm = CommonUtility.deNull(criteria.getPredicates().get("vslnm")).isEmpty() ? "--Select All--"
					: (String) CommonUtility.deNull(criteria.getPredicates().get("vslnm"));
			String vvcd = "";
			String selectstatus = CommonUtility.deNull(criteria.getPredicates().get("selectstatus")).isEmpty() ? "ALL"
					: CommonUtility.deNull(criteria.getPredicates().get("selectstatus"));

			String atbfromDate = CommonUtility.deNull(criteria.getPredicates().get("atbfromDate"));
			String atbfromTime = CommonUtility.deNull(criteria.getPredicates().get("atbfromTime"));
			String atbtoDate = CommonUtility.deNull(criteria.getPredicates().get("atbtoDate"));
			String atbtoTime = CommonUtility.deNull(criteria.getPredicates().get("atbtoTime"));
			String atufromDate = CommonUtility.deNull(criteria.getPredicates().get("atufromDate"));
			String atufromTime = CommonUtility.deNull(criteria.getPredicates().get("atufromTime"));
			String atutoDate = CommonUtility.deNull(criteria.getPredicates().get("atutoDate"));
			String atutoTime = CommonUtility.deNull(criteria.getPredicates().get("atutoTime"));
			String name = CommonUtility.deNull(criteria.getPredicates().get("name"));

			String atbFrom = "";
			String atbTo = "";
			String atuFrom = "";
			String atuTo = "";

			if (!StringUtils.isEmpty(atbfromDate)) {
				atbFrom = atbfromDate; // +" "+atbfromTime;
				atbTo = atbtoDate; // +" "+atbtoTime;
			}
			if (!StringUtils.isEmpty(atufromDate)) {
				atuFrom = atufromDate; // +" "+atufromTime;
				atuTo = atutoDate; // +" "+atutoTime;
			}

			String schemdCd = CommonUtility.deNull(criteria.getPredicates().get("vslSchemdCd"));

			map.put("atbfromDate", atbfromDate);
			map.put("atbfromTime", atbfromTime);
			map.put("atbtoDate", atbtoDate);
			map.put("atbtoTime", atbtoTime);
			map.put("atufromDate", atufromDate);
			map.put("atufromTime", atufromTime);
			map.put("atutoDate", atutoDate);
			map.put("atutoTime", atutoTime);
			map.put("schemdCd", schemdCd);

			log.info("vslnm=" + vslnm);
			log.info("selectstatus=" + selectstatus);
			/*
			 * String vslnm="--Select All--"; String selectstatus="ALL"; if (
			 * CommonUtility.deNull(criteria.getPredicates().get("selectstatus")!=null) {
			 * selectstatus=(String)
			 * CommonUtility.deNull(criteria.getPredicates().get("selectstatus"); }
			 */

			if (!name.isEmpty()) {
				List<VesselActValueObject> vesselvector = new ArrayList<VesselActValueObject>();
				vesselvector = vesselActService.getVesselList(name);
				// log.info("vesselvector"+vesselvector);
				map.put("data", vesselvector);
			}

			// log.info("selectstatus"+selectstatus);
			/*
			 * if (!(selectstatus.equalsIgnoreCase("ALL"))) {
			 */

			Map<String, Object> schemeMap = vesselActService.getVesselScheme();
			map.put("VslSchemeCdList", schemeMap.get("codeList"));
			map.put("VslSchemeNmList", schemeMap.get("nameList"));
			if (CommonUtility.deNull(criteria.getPredicates().get("vslnm")) != null) {
				vslnm = CommonUtility.deNull(criteria.getPredicates().get("vslnm"));
				log.info(" request.getparam vslnm=" + vslnm);
				map.put("vslnm", vslnm);
			} else if (vslnm == null || vslnm.equalsIgnoreCase("--Select All--"))
				map.put("vslnm", "--Select All--");

			log.info("  ===== handler vslnm =" + vslnm);
			/*
			 * } if (selectstatus.equalsIgnoreCase("ALL")) {
			 * map.put("vslnm","--Select All--"); }
			 */
			TopsModel topsModel = new TopsModel();
			List<VesselActValueObject> resultList = new ArrayList<VesselActValueObject>();
			String coCd = "";
			coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String changelist = "";
			if (CommonUtility.deNull(criteria.getPredicates().get("changelist")) != null) {
				changelist = CommonUtility.deNull(criteria.getPredicates().get("changelist"));
			}
			if ((changelist.equalsIgnoreCase("NEW"))) {
				TableResult tableResult = vesselActService.getVesselActList(coCd, vslnm, vvcd, atbFrom, atbTo, atuFrom,
						atuTo, schemdCd, criteria);

				map.put("total", tableResult.getData().getTotal());

				int size = tableResult.getData().getListData().getTopsModel().size();
				VesselActValueObject objList = null;
				for (int i = 0; i < size; i++) {
					objList = (VesselActValueObject) tableResult.getData().getListData().getTopsModel().get(i);
					resultList.add(objList);
					VesselActValueObject vsValObject = (VesselActValueObject) resultList.get(i);
					topsModel.put(vsValObject);
				}

				map.put("topsModel", topsModel);
				map.put("screen", "YardConfirmMount");
			}

		} catch (BusinessException e) {
			log.error("Exception: vesselActList ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception: vesselActList", e);
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
				log.info("END: vesselActList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.ops.vesselact -->VesselActUpdateHandler
	@PostMapping(value = "/vesselActUpdate")
	public ResponseEntity<?> vesselActUpdate(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: vesselActUpdate criteria:" + criteria.toString());

			boolean hasWaiverRequest = false;// Added by Jade for SL-CIM-20160211-01

			String scrmode = "";
			String vslnm = "";
			String vvcd = "";
			String selectstatus = "ALL";

			if (CommonUtility.deNull(criteria.getPredicates().get("selectstatus")) != null) {
				selectstatus = CommonUtility.deNull(criteria.getPredicates().get("selectstatus"));
				map.put("selectstatus", selectstatus);
			}

			if (CommonUtility.deNull(criteria.getPredicates().get("scrmode")) != null) {
				scrmode = CommonUtility.deNull(criteria.getPredicates().get("scrmode"));
			}
			log.info("Screen Mode in Handler-----" + scrmode);

			if (scrmode.equalsIgnoreCase("WAIVERUPDATE")) {
				if (CommonUtility.deNull(criteria.getPredicates().get("vvcd")) != null) {
					vvcd = CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
					map.put("vvcd", vvcd);
				}
				String strwaivercd = "";
				String strwaiverreason = "";
				String strwaiverstatus = "";

				if (CommonUtility.deNull(criteria.getPredicates().get("waivercd")) != null) {
					strwaivercd = CommonUtility.deNull(criteria.getPredicates().get("waivercd"));
				}
				if (CommonUtility.deNull(criteria.getPredicates().get("waiverreason")) != null) {
					strwaiverreason = CommonUtility.deNull(criteria.getPredicates().get("waiverreason"));
				}
				if (CommonUtility.deNull(criteria.getPredicates().get("waiverstatus")) != null) {
					strwaiverstatus = CommonUtility.deNull(criteria.getPredicates().get("waiverstatus"));
				}

				String struserids = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

				vesselActService.updateWaiverDetails(strwaivercd, strwaiverreason, strwaiverstatus, struserids, vvcd);
				scrmode = "LIST";
			}

			if (scrmode.equalsIgnoreCase("BILLUPDATE")) {
				if (CommonUtility.deNull(criteria.getPredicates().get("vvcd")) != null) {
					vvcd = CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
					map.put("vvcd", vvcd);
				}
				String strbillcd = "";
				if (CommonUtility.deNull(criteria.getPredicates().get("billcd")) != null) {
					strbillcd = CommonUtility.deNull(criteria.getPredicates().get("billcd"));
				}
				String struserids = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

				vesselActService.updateBillDetails(strbillcd, struserids, vvcd);
				scrmode = "LIST";
			}

			// CH-7 changes - QUERY OSD
			if ("QUERYOSD".equalsIgnoreCase(scrmode)) {

				scrmode = "VIEWOSD";
				log.info("START --- QUERYOSD ---");

				try {
					vvcd = CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
					map.put("vvcd", vvcd);

					String lateArrivalQuery = CommonUtility.deNull(criteria.getPredicates().get("lateArrivalQuery"));
					String osdQuery = CommonUtility.deNull(criteria.getPredicates().get("osdQuery"));
					String queryRemarks = CommonUtility.deNull(criteria.getPredicates().get("queryRemarks"));
					String userAccount = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
					boolean lateQ = "true".equalsIgnoreCase(lateArrivalQuery) || "1".equals(lateArrivalQuery);
					boolean osdQ = "true".equalsIgnoreCase(osdQuery) || "1".equals(osdQuery);

					Long reviewId = vesselActService.queryOsd(vvcd, lateQ, osdQ, queryRemarks, userAccount);

					map.put("reviewId", reviewId);
					map.put("submitInd", "Q");
					map.put("message", "OSD Query raised successfully");

					// Trigger email only on successful query
					triggerEmailToGcsl(vvcd, lateQ, osdQ, queryRemarks, userAccount);
					scrmode = "VIEWOSD";

				} catch (Exception e) {

					log.error("QUERYOSD error", e);
					map.put("error", "Unable to raise OSD Query.");

				}
				log.info("END --- QUERYOSD ---");
				// CH-7 changes - Query OSD end
				// CH-7 changes - Approve OSD
			} else if ("APPROVEOSD".equalsIgnoreCase(scrmode)) {

				log.info("START --- APRROVEOSD ---");
				try {
					vvcd = CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
					String userAccount = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
					if (StringUtils.isNotBlank(vvcd)) {
						log.info("Inside approveOsd flow");
						Long reviewId = vesselActService.approveOsd(vvcd, userAccount);
						map.put("reviewId", reviewId);
						map.put("submitInd", "A");
						scrmode = "CLOSE";
					} else {
						log.info("vvcd is empty");
						map.put("error", "Unable to raise OSD Approve.");
					}

					log.info("END --- APPROVEOSD ---");
				} catch (Exception e) {
					log.error("APPROVEOSD error", e);
					map.put("error", "Unable to raise OSD Approve.");

				}

				// CH-7 Approve OSD end
				// CH-7 changes View OSD start
			}
			if ("VIEWOSD".equalsIgnoreCase(scrmode)) {

				log.info("------Starting VIEWOSD----");
				vvcd = CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
				map.put("vvcd", vvcd);

				vslnm = CommonUtility.deNull(criteria.getPredicates().get("vsl_nm"));
				map.put("waivervector", vesselActService.getLateArrivalWaiverList());
				map.put("osdexemptionlist", vesselActService.getOsdExemptionList());
				List<OSDReviewObject> reviewOSDList = vesselActService.getOsdReviewList(vvcd);
				map.put("reviewosdvector", reviewOSDList);

				String submitInd = "No";
				if (!reviewOSDList.isEmpty() && reviewOSDList.get(0).getSubmitStatus() != null) {

					String status = reviewOSDList.get(0).getSubmitStatus();
					submitInd = ("S".equalsIgnoreCase(status) || "A".equalsIgnoreCase(status)) ? "Yes" : "No";
				}
				map.put("submitInd", submitInd);
				scrmode = "VIEW";

				log.info("----END VIEWOSD----");
			}
			// CH-7 VIEWOSD End

			if ("LIST".equalsIgnoreCase(scrmode)) {

				String atbfromDate = CommonUtility.deNull(criteria.getPredicates().get("atbfromDate"));
				String atbfromTime = CommonUtility.deNull(criteria.getPredicates().get("atbfromTime"));
				String atbtoDate = CommonUtility.deNull(criteria.getPredicates().get("atbtoDate"));
				String atbtoTime = CommonUtility.deNull(criteria.getPredicates().get("atbtoTime"));
				String atufromDate = CommonUtility.deNull(criteria.getPredicates().get("atufromDate"));
				String atufromTime = CommonUtility.deNull(criteria.getPredicates().get("atufromTime"));
				String atutoDate = CommonUtility.deNull(criteria.getPredicates().get("atutoDate"));
				String atutoTime = CommonUtility.deNull(criteria.getPredicates().get("atutoTime"));
				String schemdCd = CommonUtility.deNull(criteria.getPredicates().get("vslSchemdCd"));

				map.put("atbfromDate", atbfromDate);
				map.put("atbfromTime", atbfromTime);
				map.put("atbtoDate", atbtoDate);
				map.put("atbtoTime", atbtoTime);
				map.put("atufromDate", atufromDate);
				map.put("atufromTime", atufromTime);
				map.put("atutoDate", atutoDate);
				map.put("atutoTime", atutoTime);
				map.put("schemdCd", schemdCd);

				if (CommonUtility.deNull(criteria.getPredicates().get("vvcd")) != null) {
					vvcd = (String) CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
					map.put("vvcd", vvcd);
				}
				// Addded by Jitten (zensar) starts here
				OverStayDockageValueObject ovrstyVO = new OverStayDockageValueObject();
				log.info("scrmode.equalsIgnoreCase(LIST)--");
				log.info("(vvcd)--" + vvcd);
				ovrstyVO = vesselActService.getWaiverStatus(vvcd);
				log.info("after scrmode.equalsIgnoreCase(LIST)--" + ovrstyVO);
				// This will check for the waiver code for the closed vessel only.
				if (ovrstyVO != null) {
					String waiverNameFromVO = ovrstyVO.getWaiverName();
					String billingNameFromVO = ovrstyVO.getBillingName();
					String isRejectedFromVO = ovrstyVO.getISRejected();

					log.info("waiverNameFromVO--" + waiverNameFromVO + "   billingNameFromVO--" + billingNameFromVO);
					log.info("isRejectedFromVO--" + isRejectedFromVO);
					map.put("waiverNameFromVO", waiverNameFromVO);
					map.put("billingNameFromVO", billingNameFromVO);
					map.put("isRejectedFromVO", isRejectedFromVO);
				}
				// Addded by Jitten (zensar) ends here

				boolean submit_indicator = vesselActService.osdSubmitindicator(vvcd, true);
				map.put("submit_indicator", submit_indicator);

				boolean schemeAndVesselIndicator = vesselActService.schemeAndVesselIndicator(vvcd);
				map.put("schemeAndVesselIndicator", schemeAndVesselIndicator);

				getViewVesselAct(request, vslnm, vvcd, map);
			}

			if (scrmode.equalsIgnoreCase("UPDATE") || scrmode.equalsIgnoreCase("CLOSE")
					|| scrmode.equalsIgnoreCase("OPEN")) {
				if (CommonUtility.deNull(criteria.getPredicates().get("vvcd")) != null) {
					vvcd = (String) CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
					map.put("vvcd", vvcd);
				}

				if (scrmode.equalsIgnoreCase("OPEN")) {
					// call updateVesselActivity if scrmode is OPEN

					updateVesselActivity(request, vvcd, scrmode);
				}

				if ("UPDATE".equalsIgnoreCase(scrmode)) {
					// call updateVesselActivity if scrmode is UPDATE
					// Added BY NS FOR KMF NOTIFICATION TRIGGER - NOV 2023
					Map<String, String> vesselDataForKmf = vesselActService.getVesselDataForKmf(vvcd);
					boolean updateVesselActivity = false;
					try {
						updateVesselActivity = updateVesselActivity(request, vvcd, scrmode);
					} catch (BusinessException e) {
						throw new BusinessException(e.getMessage());
					}

					// Start added for SMART CR by FPT on 24-Jan-2014
					log.info("***********Before SMART interface calling");
					try {
						// Sept 2023: NS added trim string to fix FAT trigger notification with same old
						// and new fat date issue
						String newAtbDttm = CommonUtility.deNull(criteria.getPredicates().get("atbdttm")).trim();
						String oldAtbDttm = CommonUtility.deNull(criteria.getPredicates().get("oldAtbDttm")).trim();
						String newAtuDttm = CommonUtility.deNull(criteria.getPredicates().get("atudttm")).trim();
						String oldAtuDttm = CommonUtility.deNull(criteria.getPredicates().get("oldAtuDttm")).trim();
						// Added BY NS FOR KMF
						String newFatDttm = CommonUtility.deNull(criteria.getPredicates().get("disdttm")).trim();
						String newLatDttm = CommonUtility.deNull(criteria.getPredicates().get("loaddttm")).trim();
						boolean isFATChange = false;
						boolean isLATChange = false;

						String terminal = CommonUtility.deNull(vesselDataForKmf.get("terminal"));
						String oldFatDttm = CommonUtility.deNull(vesselDataForKmf.get("fat")).trim();
						String oldLatDttm = CommonUtility.deNull(vesselDataForKmf.get("lat")).trim();
						log.info("***********vesselDataForKmf" + vesselDataForKmf.toString());
						if (StringUtils.isNotBlank(newFatDttm) && !StringUtils.equals(newFatDttm, oldFatDttm)) {
							isFATChange = true;
							log.info("Vessel Activity FAT has changed. oldFatDttm: " + oldFatDttm + " - newFatDttm: "
									+ newFatDttm);
						}

						if (StringUtils.isNotBlank(newLatDttm) && !StringUtils.equals(newLatDttm, oldLatDttm)) {
							isLATChange = true;
							log.info("Vessel Activity LAT has changed. oldLatDttm: " + oldLatDttm + " - newLatDttm: "
									+ newLatDttm);
						}
						// END ADDED

						boolean isAtbChange = false;
						boolean isAtuChange = false;
						if (StringUtils.isNotBlank(newAtbDttm) && !StringUtils.equals(newAtbDttm, oldAtbDttm)) {
							isAtbChange = true;
							log.info("Vessel Activity ATB has changed. oldAtb: " + oldAtbDttm + " - newAtb: "
									+ newAtbDttm);
						}

						if (StringUtils.isNotBlank(newAtuDttm) && !StringUtils.equals(newAtuDttm, oldAtuDttm)) {
							isAtuChange = true;
							log.info("Vessel Activity ATU has changed. oldAtu: " + oldAtuDttm + " - newAtu: "
									+ newAtuDttm);
						}

						if (isAtbChange || isAtuChange) {
							String struserids = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

							InetAddress jpOnlineAddress = InetAddress.getLocalHost();
							String serverIp = null;
							String serverNm = null;
							if (jpOnlineAddress != null) {
								serverIp = jpOnlineAddress.getHostAddress();
								serverNm = jpOnlineAddress.getHostName();
							}

							log.info("Vessel Activity serverIp    = " + serverIp);
							log.info("Vessel Activity serverName = " + serverNm);
							log.info("Vessel Activity vvCd = " + vvcd);

							SmartInterfaceInputVO inputObj = new SmartInterfaceInputVO();
							inputObj.setUserId(struserids);
							inputObj.setVvCd(vvcd);
							inputObj.setOccSrcCd(SmartInterfaceConstants.SOURCE_JP);
							inputObj.setServerIp(serverIp);
							inputObj.setServerNm(serverNm);
							inputObj.setClassNm(this.getClass().getName());
							inputObj.setClassDesc("Vessel activity update handler class");

							DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
							df.setLenient(false);

							if (isAtbChange) {

								// Every time user creates or updates ATB, system should be able to call the
								// SMART interfaces to mark all the reserved locations of this vessel as
								// occupied
								log.info(
										"***********Before SMART interface calling for mark all the reserved locations of this vessel as occupied: Vessel Code ="
												+ vvcd);
								inputObj.setActionCd(SmartInterfaceConstants.ENTRY_ATB_EVENT_CD);

								Date atbDttm = df.parse(newAtbDttm);
								inputObj.setAtbDttm(atbDttm);
								String url = smartBaseUrl + "/markStorageOccupancy";
								log.info("[markStorageOccupancy] Calling SMART service URL= " + url);
								smartServiceRestClient.markStorageOccupancy(inputObj);
								log.info("***********After SMART interface calling markStorageOccupancy");
							}

							if (isAtuChange) {

								// Every time user creates or updates ATU, system should be able to call the
								// SMART interfaces to release all the occupied locations of this vessel
								log.info(
										"***********Before SMART interface calling for to release all the occupied locations of this vessel : Vessel Code="
												+ vvcd);
								inputObj.setActionCd(SmartInterfaceConstants.ENTRY_ATU_EVENT_CD);
								inputObj.setStayingInJP(false);

								Date atuDttm = df.parse(newAtuDttm);
								inputObj.setAtuDttm(atuDttm);
								String url = smartBaseUrl + "/releaseStorageOccupancy";
								log.info("[releaseStorageOccupancy] Calling SMART service URL= " + url);

								smartServiceRestClient.releaseStorageOccupancy(inputObj);
								log.info("***********After SMART interface calling releaseStorageOccupancy");
							}

						}
						// Added BY NS FOR KMF NOTIFICATION TRIGGER - NOV 2023
						// Added BY NS FOR KMF
						log.info("***********isAtbChange*****" + isAtbChange + "isAtuChange:" + isAtuChange);
						log.info("***********isFATChange*****" + isFATChange + "isLATChange:" + isLATChange);
						DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");

						String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
						log.info(
								"***********updateVesselActivity*****" + updateVesselActivity + "terminal:" + terminal);
						if (isAtbChange && updateVesselActivity) {
							log.info("***********KMF CALL on AtbChange *****");
							triggerKMFNotification(vvcd, df.parse(newAtbDttm), null, null, null, userId, vslnm);
						}
						if (isAtuChange && updateVesselActivity) {
							log.info("***********KMF CALL on AtuChange *****");
							triggerKMFNotification(vvcd, null, df.parse(newAtuDttm), null, null, userId, vslnm);
						}
						if (isFATChange && terminal.equalsIgnoreCase("GB") && updateVesselActivity) {
							log.info("***********KMF CALL on FATChange *****");
							triggerKMFNotification(vvcd, null, null, df.parse(newFatDttm), null, userId, vslnm);
						}

						if (isLATChange && terminal.equalsIgnoreCase("GB") && updateVesselActivity) {
							log.info("***********KMF CALL on LATChange *****");
							triggerKMFNotification(vvcd, null, null, null, df.parse(newLatDttm), userId, vslnm);
						}

						// END ADDED
					} catch (Exception ex) {
						log.error(
								"Call SMART Interface markStorageOccupancy/releaseStorageOccupancy exception: Vessel Code="
										+ vvcd);
						log.error("Exception: " + ex.getMessage());
					}
					log.info("***********After SMART interface calling");

				} // End If UPDATE
					// End added for SMART CR by FPT on 24-Jan-2014

				if (scrmode.equalsIgnoreCase("CLOSE")) {

					// Addded by Jitten (zensar) starts here
					OverStayDockageValueObject ovrstyVO = new OverStayDockageValueObject();
					log.info("scrmode.equalsIgnoreCase(UPDATE)--");
					log.info("(vvcd)--" + vvcd);
					ovrstyVO = vesselActService.getWaiverStatus(vvcd);
					log.info("after scrmode.equalsIgnoreCase(UPDATE)--" + ovrstyVO);
					// This will check for the waiver code for the closed vessel only.
					if (ovrstyVO != null) {
						hasWaiverRequest = true; // Added by Jade for SL-CIM-20160211-01: this is to log whether there
													// is already one waiver request raised
						log.info("There's one waiver request existing for " + vvcd);

						String waiverNameFromVO = ovrstyVO.getWaiverName();
						String billingNameFromVO = ovrstyVO.getBillingName();
						String isRejectedFromVO = ovrstyVO.getISRejected();

						log.info(
								"waiverNameFromVO--" + waiverNameFromVO + "   billingNameFromVO--" + billingNameFromVO);
						log.info("isRejectedFromVO--" + isRejectedFromVO);
						map.put("waiverNameFromVO", waiverNameFromVO);
						map.put("billingNameFromVO", billingNameFromVO);
						map.put("isRejectedFromVO", isRejectedFromVO);
					}
					// Addded by Jitten (zensar) ends here
					log.info("vv code: " + vvcd + "; hasWaiverRequest: " + hasWaiverRequest);

					// Added by Jitten (Zensar) on 23rd august 2007 starts here
					// if (scrmode.equals("CLOSE")) //Commented by Jade for SL-CIM-20160211-01
					if (!hasWaiverRequest) // Amended by Jade for SL-CIM-20160211-01: only proceed if it's eligible for
											// waiver (when there's no waiver request raised yet)
					{
						TopsModel topsModel = new TopsModel();

						OpsValueObject opsValueObject = new OpsValueObject();

						// Collection collection =
						// (Collection)processMar.determineOverStayAndAmount(vvcd);
						Map<String, Object> collection = vesselActService.determineOverStayAndAmount(vvcd);

						List<Object> billCollectionPubl = new ArrayList<Object>();
						List<Object> billCollectionCust = new ArrayList<Object>();

						Object listObj = collection.get("P");
						if (listObj instanceof List) {
							for (int j = 0; j < ((List<?>) listObj).size(); j++) {
								Object item = ((List<?>) listObj).get(j);
								if (item instanceof Object) {
									billCollectionPubl.add(item);
								}
							}
						}

						listObj = collection.get("C");
						if (listObj instanceof List) {
							for (int j = 0; j < ((List<?>) listObj).size(); j++) {
								Object item = ((List<?>) listObj).get(j);
								if (item instanceof Object) {
									billCollectionCust.add(item);
								}
							}
						}

						long overstayPeriod = ((Long) collection.get("OPeriod")).longValue();

						// Getting the publish rate starts here
						Object[] billArrayPublish = billCollectionPubl.toArray();
						BillVO billPublish = new BillVO();

						for (int xcnt = 0; xcnt < billArrayPublish.length; xcnt++) {
							billPublish = (BillVO) Array.get(billArrayPublish, xcnt);
						}
						double amountPublish = billPublish.getGstAmount() + billPublish.getTotalAmount();
						// Getting the publish rate ends here

						Object[] billArrayCust = billCollectionCust.toArray();
						BillVO bill = new BillVO();

						for (int xcnt = 0; xcnt < billArrayCust.length; xcnt++) {
							bill = (BillVO) Array.get(billArrayCust, xcnt);
						}

						// long overstayPeriod = processValueObjectElements.getOverStayPeriod();
						// long amount = processValueObjectElements.getAmount();
						double amount = bill.getGstAmount() + bill.getTotalAmount();
						log.info("amount--" + amount + "  amountPublish --" + amountPublish);

						log.info("amount--" + amount + "  amountPublish--" + amountPublish);

						// Need to make a check.
						// If the published overstay dockage amount is say $XXX.XX
						// but the customised amount is $0, then we should NOT show
						// the overstay dockage request waiver screen.

						if (amount != 0.0 && amountPublish > 0) {
							log.info("inside of if (amount != 0 && amountPublish > 0)");
							log.info("amount--" + amount + "  amountPublish--" + amountPublish);
							GroupVO groupVO = null;
							String strAmount = String.valueOf(amount);
							groupVO = vesselActService.getLevelId(strAmount);
							if (groupVO == null) {
								log.error("Could not find the Level ID for " + strAmount);
								map.put("overstayPeriod", new Long(overstayPeriod));
								map.put("amount", new Double(amount));
								map.put("groupName", "");
							} else {
								String groupName = groupVO.getGroupName();
								log.info("groupName--" + groupName + "  overstayPeriod" + overstayPeriod);
								log.info("amount" + amount);

								map.put("overstayPeriod", new Long(overstayPeriod));
								map.put("amount", new Double(amount));
								map.put("groupName", groupName);
							}
							List<WaiverCodesVO> waiverCodesList;
							List<BillingCodesVO> billingReasonsList;
							if (overstayPeriod > 0) {
								String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

								boolean hasAccess = vesselActService.hasAccesstoOSD(UserID);
								if (hasAccess) {
									opsValueObject = vesselActService.getVessels(opsValueObject);
									topsModel.put(opsValueObject);

									map.put("listData", topsModel);
									WaiverCodesVO waiverCodesVO = new WaiverCodesVO();
									waiverCodesVO.setVesselTypeCode("GB");
									// HashMap waiverCodeMap= new HashMap();
									waiverCodesList = vesselActService.getWaiverCodes(waiverCodesVO);
									// map.put("waiverCodeMap",waiverCodeMap);
									map.put("waiverCodesList", waiverCodesList);

									BillingCodesVO billingCodesVO = new BillingCodesVO();
									billingCodesVO.setVesselTypeCode("GB");
									billingReasonsList = vesselActService.getBillingReasons(billingCodesVO);
									// map.put("billingCodeMap", billingCodeMap);
									map.put("billingReasonsList", billingReasonsList);

									map.put("vesselType", "GB");
								} else {
									throw new BusinessException(
											ConstantUtil.ErrMsg_OverStay_Request_Vessel_Cannot_Be_Closed);
								}

							}
						}
						if (scrmode.equalsIgnoreCase("WAIVERVIEW")) {
							String strwaiverstatus = "";
							if (CommonUtility.deNull(criteria.getPredicates().get("vvcd")) != null) {
								vvcd = (String) CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
								map.put("vvcd", vvcd);
							}
							if (CommonUtility.deNull(criteria.getPredicates().get("waiverstatus")) != null) {
								strwaiverstatus = (String) CommonUtility
										.deNull(criteria.getPredicates().get("waiverstatus"));
							}
							List<String> waivervector = new ArrayList<String>();
							waivervector = vesselActService.getWaiverList(vvcd, strwaiverstatus);
							map.put("waivervector", waivervector);
						}
					}

					// call updateVesselActivity if scrmode is CLOSE
					log.info("vvcd--" + vvcd);
					log.info("scrmode--" + scrmode);
					updateVesselActivity(request, vvcd, scrmode);

				} // END if CLOSE
					// Added by Jitten (Zensar)one more screen mode to display the popup of Overstay
					// waiver

				getViewVesselAct(request, vslnm, vvcd, map);
				String codcolstatus = vesselActService.getCodColStatus(vvcd);
				map.put("codcolstatus", codcolstatus);

			} // End if UPDATE / OPEN / CLOSE

			log.info("before if (scrmode.equalsIgnoreCase(WAIVERVIEW)) ;...........");
			if (scrmode.equalsIgnoreCase("WAIVERVIEW")) {
				String strwaiverstatus = "";
				if (CommonUtility.deNull(criteria.getPredicates().get("vvcd")) != null) {
					vvcd = (String) CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
					map.put("vvcd", vvcd);
				}
				if (CommonUtility.deNull(criteria.getPredicates().get("waiverstatus")) != null) {
					strwaiverstatus = (String) CommonUtility.deNull(criteria.getPredicates().get("waiverstatus"));
				}
				List<String> waivervector = new ArrayList<String>();
				waivervector = vesselActService.getWaiverList(vvcd, strwaiverstatus);
				map.put("waivervector", waivervector);
			}
			log.info("before if (scrmode.equalsIgnoreCase(BILLVIEW)) ;...........");
			if (scrmode.equalsIgnoreCase("BILLVIEW")) {
				if (CommonUtility.deNull(criteria.getPredicates().get("vvcd")) != null) {
					vvcd = (String) CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
					map.put("vvcd", vvcd);
				}
				String billstring = "";
				billstring = vesselActService.getBillList(vvcd);
				map.put("billstring", billstring);
			}
			log.info("before if (scrmode.equalsIgnoreCase(VIEW)) ;...........");
			if (scrmode.equalsIgnoreCase("VIEW")) {
				if (CommonUtility.deNull(criteria.getPredicates().get("vvcd")) != null) {
					vvcd = (String) CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
					map.put("vvcd", vvcd);
				}
				getViewVesselAct(request, vslnm, vvcd, map);
			}
			log.info("before map.put(scrmode,scrmode); ;...........");
			map.put("scrmode", scrmode);
			// nextScreen(request, nextScreen);

			processRequest(request, map);
		} catch (BusinessException e) {
			log.error("Exception: vesselActUpdate ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception: vesselActUpdate", e);
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
				log.info("END: vesselActUpdate result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// CH-7 trigger email functionality

	private boolean triggerEmailToGcsl(String vvcd, boolean lateQ, boolean osdQ, String queryRemarks,
			String userAccount) throws BusinessException {
		log.info("Triggering Email For GCSL");
		EmailValueObject vo = new EmailValueObject();

		vo = vesselActService.getEmailContentForQueryOsd(vvcd, lateQ, osdQ, queryRemarks);
		if (vo == null || ObjectUtils.isEmpty(vo.getRecipientAddress()) || StringUtils.isEmpty(vo.getSubject())) {
			log.info("Value from DB is empty");
			return false;
		}
		Map<String, String> gcReplaceData = new HashMap<>();
		String sender = newGcManagerSender;
		log.info("sender" + newGcManagerSender);
		String message = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), reviewOsdQueryTemplate);
		gcReplaceData.put("out voyage no", vvcd);
		String queryOption = "";
		if (lateQ && osdQ) {
			queryOption = "Late Arrival & OSD";
		} else if (lateQ && !osdQ) {
			queryOption = "Late Arrival";
		} else {
			queryOption = "OSD";
		}
		gcReplaceData.put("queryRemarks", queryRemarks);
		gcReplaceData.put("queryOption", queryOption);
		String emailMessage = CommonUtil.replaceVariablesInHtml(message, gcReplaceData);
		vo.setMessage(emailMessage);
		vo.setSenderAddress(CommonUtility.deNull(sender));

		return vesselActService.sendMessage(vo);

	}
	// CH-7 trigger email functionality

	@PostMapping(value = "/uploadOsdFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> saveSubmitOsd(MultipartHttpServletRequest request, HttpServletResponse response)
			throws BusinessException {

		Result result = new Result();
		Map<String, Object> map = new HashMap<>();

		try {
			log.info("START uploadFile");

			String vvcd = request.getParameter("vvcd");
			String vslnm = request.getParameter("Vsl_nm");
			String osdReviewOption = request.getParameter("osdReviewOption");
			String lateArrivalReviewOption = request.getParameter("lateArrivalReviewOption");
			String osdExemptionCodeList = request.getParameter("osdExemptionCodeList");
			String lateArrivalExemptionList = request.getParameter("lateArrivalExemptionList");
			String actualOsdFilesName = request.getParameter("osdFileList");
			String actuallateFilesName = request.getParameter("lateArrivalFileList");
			String submitInd = request.getParameter("submitInd"); // D / S / YES
			String userAccount = request.getParameter("userAccount");
			String scrmode = request.getParameter("scrmode");

			log.info("VVcd : " + vvcd + " vslnm: " + vslnm + " osdReviewOption: " + osdReviewOption
					+ " lateArrivalReviewOption: " + lateArrivalReviewOption + " osdExemptionCodeList:"
					+ osdExemptionCodeList + " lateArrivalExemptionList: " + lateArrivalExemptionList
					+ " actualOsdFilesName : " + actualOsdFilesName + " actuallateFilesName: " + actuallateFilesName
					+ " submitInd: " + submitInd + " userAccount: " + userAccount + "scrmode: " + scrmode);

			if (vvcd == null || userAccount == null) {
				log.error("Mandatory parameters missing");
				throw new BusinessException("M4001");
			}

			Map<String, String> uploadedFileMap;
			try {
				uploadedFileMap = vesselActService.uploadOsdFiles(request);
			} catch (IOException ioe) {
				log.error("File upload failed", ioe);
				throw new BusinessException("M4123");
			}

			int reviewId = vesselActService.updateOsdReview(vvcd, osdExemptionCodeList, lateArrivalExemptionList,
					osdReviewOption, lateArrivalReviewOption, userAccount, submitInd, actualOsdFilesName,
					actuallateFilesName, uploadedFileMap);
			log.info("OSD review id: " + reviewId);

			List<OSDExemptionClauses> osdExemptionClauses = vesselActService.getOsdExemptionList();
			map.put("osdexemptionlist", osdExemptionClauses);

			List<OSDExemptionClauses> waivervector = vesselActService.getLateArrivalWaiverList();
			map.put("waivervector", waivervector);

			getViewVesselAct(request, vslnm, vvcd, map);
			processRequest(request, map);

			map.put("submitInd", submitInd);
			map.put("scrmode", scrmode);

			if ("SUBMITOSD".equalsIgnoreCase(scrmode)) {
				String gcManagerEMailSub = "Appointed Over Stay Dockage for {vessel name} / {out voyage no}";
				log.info("Triggering Email For GC Manager");
				String gcManagerMail = RecipientEmail;
				Map<String, String> gcReplaceData = new HashMap<>();
				String sender = newGcManagerSender;
				log.info("sender" + newGcManagerSender);
				String subjectEmail = gcManagerEMailSub;
				log.info("subjectEmail" + subjectEmail);
				subjectEmail = StringUtils.replace(subjectEmail, "{vessel name}", vslnm);
				subjectEmail = StringUtils.replace(subjectEmail, "{out voyage no}", vvcd);
				log.info(subjectEmail);
				String message = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), gcManagerTemplate);
				gcReplaceData.put("vessel name", vslnm);
				gcReplaceData.put("out voyage no", vvcd);
				String emailMessage = CommonUtil.replaceVariablesInHtml(message, gcReplaceData);

				EmailValueObject vo = new EmailValueObject();

				String[] emailStr = gcManagerMail.split(",");
				vo.setRecipientAddress(emailStr);
				vo.setSubject(subjectEmail);
				vo.setMessage(emailMessage);
				vo.setSenderAddress(CommonUtility.deNull(sender));

				vesselActService.sendMessage(vo);

				log.info(" ------ End of Email Sending -------");

			}

		} catch (BusinessException e) {
			log.error("Exception: uploadOsdFile ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception: uploadOsdFile", e);
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
				log.info("END: uploadOsdFile result: " + result.toString());
			}
		}

		log.info("END uploadOsdFile");
		return ResponseEntityUtil.success(result.toString());
	}

	@GetMapping("/downloadOsdFile")
	public void downloadOsdFile(@RequestParam("vvcd") String vvcd, @RequestParam("fileName") String fileName,
			HttpServletResponse response) {

		try {
			log.info("START downloadOsdFile vvcd=" + vvcd + ", fileName=" + fileName);

			Map<String, String> fileMap = vesselActService.getLatestOsdFile(vvcd, fileName);

			String assignedFileName = fileMap.get(fileName);

			if (assignedFileName == null) {
				throw new BusinessException("FILE_NOT_FOUND");
			}

			Path filePath = Paths.get(fileUploadDirectory, assignedFileName);

			if (!Files.exists(filePath)) {
				throw new BusinessException("FILE_NOT_FOUND");
			}

			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			Files.copy(filePath, response.getOutputStream());
			response.getOutputStream().flush();

			log.info("END downloadOsdFile");

		} catch (Exception e) {
			log.error("Error downloading OSD file", e);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	// Start Added BY NS FOR KMF NOTIFICATION TRIGGER - NOV 2023
	private void triggerKMFNotification(String vvcd, Date atb, Date atu, Date fat, Date lat, String userId,
			String vslnm) {

		try {
			log.info("START: triggerKMFNotification vvcd:" + vvcd + "atb:" + atb + "atu:" + atu + "fat:" + fat + "lat:"
					+ lat);

			Timestamp sysDate = vesselActService.getSysDate();
			Timestamp ts = null;
			if (atb != null) {
				ts = new Timestamp(atb.getTime());
			}
			if (atu != null) {
				ts = new Timestamp(atu.getTime());
			}
			if (fat != null) {
				ts = new Timestamp(fat.getTime());
			}
			if (lat != null) {
				ts = new Timestamp(lat.getTime());
			}
			long timeDifference = sysDate.getTime() - ts.getTime();
			int seconds = (int) timeDifference / 1000;

			int diffInMinutes = (seconds % 3600) / 60;
			String jpGlassThresholdValue = jpGlassThreshold;
			int jpGlassThreshold = Integer.parseInt(jpGlassThresholdValue);
			log.info("jpGlassThreshold:" + jpGlassThreshold);
			log.info("minutes:" + diffInMinutes);
			if (diffInMinutes < jpGlassThreshold) {
				log.info("TRIGGER KMF ");
				if (fat != null || lat != null) {
					log.info("***********SENDING FAT/LAT MESSAGE*********");
					log.info("vvcd:" + vvcd);
					log.info("fat:" + fat);
					log.info("lat:" + lat);
					boolean sendVslEvent = false;
					try {
						sendVslEvent = EventMsgUtil.sendVslCargoEvent(vvcd, fat, lat);
						log.info("*******KAFKA RESPONSE**** sendVslEvent*********" + sendVslEvent);
					} catch (Exception e) {
						log.info("Exception triggerKMFNotification: " + e.getMessage());
					}

					VslEvent vslEvent = new VslEvent();
					if (fat != null) {
						vslEvent.setFat(fat.toString());
					}
					if (lat != null) {
						vslEvent.setLat(lat.toString());
					}
					vslEvent.setVvCode(vvcd);
					if (ConstantUtil.EVENT_LOG_IND) {
						log.info(" ****addEventDetailsLog start *******");
						boolean eventLogInd = addEventDetailsLog(vvcd, userId,
								fat == null ? ConstantUtil.EVENT_TYPE_LAT_DTTM : ConstantUtil.EVENT_TYPE_FAT_DTTM,
								ConstantUtil.SOURCE_SYSTEM, sendVslEvent == true ? "S" : "F", vslEvent);
						log.info("eventLogInd " + eventLogInd);
					} else {
						log.info("EVENT_LOG_IND VALUE IS FALSE" + ConstantUtil.EVENT_LOG_IND);
					}
				} else {
					log.info("***********SENDING ATB/ATU MESSAGE*********");
					log.info("vvcd:" + vvcd);
					log.info("atb:" + atb);
					log.info("atu:" + atu);
					boolean sendVslEvent = false;
					try {
						sendVslEvent = EventMsgUtil.sendVslEvent(vvcd, null, null, atb, atu);
						log.info("sendVslEvent:" + sendVslEvent);
					} catch (Exception e) {
						log.info("Exception triggerKMFNotification: " + e.getMessage());
					}
					VslEvent vslEvent = new VslEvent();
					if (atb != null) {
						vslEvent.setAtb(atb.toString());
					}
					if (atu != null) {
						vslEvent.setAtu(atu.toString());
					}
					vslEvent.setVvCode(vvcd);
					if (ConstantUtil.EVENT_LOG_IND) {
						log.info(" ****addEventDetailsLog start *******");
						boolean eventLogInd = addEventDetailsLog(vvcd, userId,
								atu == null ? ConstantUtil.EVENT_TYPE_ATB_DTTM : ConstantUtil.EVENT_TYPE_ATU_DTTM,
								ConstantUtil.SOURCE_SYSTEM, sendVslEvent == true ? "S" : "F", vslEvent);
						log.info("eventLogInd " + eventLogInd);
					} else {
						log.info("EVENT_LOG_IND VALUE IS FALSE" + ConstantUtil.EVENT_LOG_IND);
					}
				}

			} else {
				log.info("EVENT TIME IS OUTDATED   ");
			}

		} catch (Exception e) {
			log.info("Exception triggerKMFNotification: " + e.getMessage());
		}

	}
	// End BY NS FOR KMF NOTIFICATION TRIGGER - NOV 2023

	// Start Added BY NS FOR KMF NOTIFICATION TRIGGER - NOV 2023
	private boolean addEventDetailsLog(String vvCd, String createUserId, String eventType, String sourceSystem,
			String kafkaRespStatus, Object kafkaMsg) {
		boolean sendEvent = false;
		try {
			log.info("START:addEventDetailsLog vvCd:" + vvCd + "createUserId:" + createUserId + "eventType:" + eventType
					+ "sourceSystem:" + sourceSystem + "kafkaRespStatus:" + kafkaRespStatus);
			log.info("kafkaMsg:" + kafkaMsg.toString());
			EventDetails details = new EventDetails();
			details.setVv_cd(vvCd);
			details.setCreate_user_id(createUserId);
			details.setEvent_type(eventType);
			details.setSource_system(ConstantUtil.SOURCE_SYSTEM);
			details.setKafka_resp_status(kafkaRespStatus);
			details.setKafka_msg(kafkaMsg);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<EventDetails> requestBody = new HttpEntity<>(details, headers);
			RestTemplate restTemplate = new RestTemplate();
			log.info("*******************eventLogUrl:*********************" + eventLogUrl + "requestBody:"
					+ requestBody.toString());
			Result response = restTemplate.postForObject(eventLogUrl, requestBody, Result.class);
			log.info("*************response:*****************" + response.toString());
			if (response.getSuccess()) {
				sendEvent = true;
			}
		} catch (Exception e) {
			log.info("addEventDetailsLog:" + e.toString());
		} finally {
			log.info("addEventDetailsLog sendEvent:" + sendEvent);
		}
		return sendEvent;
	}
	// End Added BY NS FOR KMF NOTIFICATION TRIGGER - NOV 2023

	private void processRequest(HttpServletRequest request, Map<String, Object> map) {
		Criteria criteria = CommonUtil.getCriteria(request);
		log.info("START: processRequest :: criteria : " + criteria.toString());
		String closestatusind = "N";
		String vvstatusind = "";
		String scrmode = "";
		String redirectName = "vesselAct_view";
		List<Object> vesselactvector = new ArrayList<>();
		if (CommonUtility.deNull(criteria.getPredicates().get("scrmode")) != null) {
			scrmode = (String) CommonUtility.deNull(criteria.getPredicates().get("scrmode"));
		}
		if (!("WAIVERVIEW".equalsIgnoreCase(scrmode)) && !("BILLVIEW".equalsIgnoreCase(scrmode))
				&& !("VIEWOSD".equalsIgnoreCase(scrmode)) && !("SUBMITOSD".equalsIgnoreCase(scrmode))
				&& !("SAVEOSD".equalsIgnoreCase(scrmode)) && !("QUERYOSD".equalsIgnoreCase(scrmode))
				&& !("APPROVEOSD".equalsIgnoreCase(scrmode))) {
			try {
				TopsModel topsModel = (TopsModel) map.get("listData");

				String lineTowedBargeInd = ""; // by YJ 6 Jan 04
				if (Objects.nonNull(topsModel)) {

					// modifed after may 02
					for (int i = 0; i < topsModel.getSize(); i++) {
						VesselActValueObject vesselActValueObject = (VesselActValueObject) topsModel.get(i);
						// VesselActValueObject vesselActValueObject =
						// (VesselActValueObject)topsModel.get(0);
						// end of modification after may 02
						vesselactvector.add(vesselActValueObject.getVarNbr());
						vesselactvector.add(vesselActValueObject.getVslNm());
						vesselactvector.add(vesselActValueObject.getInVoyNbr());
						vesselactvector.add(vesselActValueObject.getOutVoyNbr());
						vesselactvector.add(vesselActValueObject.getAtbDttm());
						vesselactvector.add(vesselActValueObject.getAtuDttm());
						vesselactvector.add(vesselActValueObject.getCodDttm());
						vesselactvector.add(vesselActValueObject.getColDttm());
						vesselactvector.add(vesselActValueObject.getBcodDttm());
						vesselactvector.add(vesselActValueObject.getBcolDttm());
						vesselactvector.add(vesselActValueObject.getFirstActDttm());
						vesselactvector.add(vesselActValueObject.getLastActDttm());
						/* Start fix Santosh */
						vesselactvector.add(vesselActValueObject.getFirstCargoActDttm());
						/* End fix Santosh */
						vesselactvector.add(vesselActValueObject.getGbCloseVslInd());
						vesselactvector.add(vesselActValueObject.getVvStatusInd());
						vesselactvector.add(vesselActValueObject.getScheme());
						// modifed after may 02
						vesselactvector.add(vesselActValueObject.getShiftInd());
						vesselactvector.add(vesselActValueObject.getMvtInd());
						vesselactvector.add(vesselActValueObject.getEtbDttm());
						vesselactvector.add(vesselActValueObject.getEtuDttm());
						vesselactvector.add(vesselActValueObject.getTodayDttm());

						// START Added By Tirumal to implement VesselActivity CR, dated 04-12-2007
						vesselactvector.add(vesselActValueObject.getNoOfGangsSupplied());
						vesselactvector.add(vesselActValueObject.getNoOfWorkableHatches());
						vesselactvector.add(vesselActValueObject.getReasonForDelay());
						vesselactvector.add(vesselActValueObject.getRemarks());
						vesselactvector.add(vesselActValueObject.getTempList());
						// END Added By Tirumal to implement VesselActivity CR, dated 04-12-2007

						// START Code added by Madhu for VesselProductivty Billing CR, dated 25-09-2008
						vesselactvector.add(vesselActValueObject.getTotGenCargoActivity());
						// END Code added by Madhu for VesselProductivty Billing CR, dated 25-09-2008
						closestatusind = vesselActValueObject.getGbCloseVslInd();
						vvstatusind = vesselActValueObject.getVvStatusInd();
						lineTowedBargeInd = vesselActValueObject.getLineTowedVessel();

					}
					// end of modification after may 02
					// System.out.println("vesselactvector"+vesselactvector);

				}

				map.put("vesselactvector", vesselactvector);
				map.put("lineTowedBarge", lineTowedBargeInd); // by YJ 6 Jan 04

				// Start CH-7 changes
				// For Review OSD button, late arrival and OSD duration visibility in FE
				if ("UPDATE".equalsIgnoreCase(scrmode) || "LIST".equalsIgnoreCase(scrmode)
						|| "OPEN".equalsIgnoreCase(scrmode)) {

					String vvcd = (String) CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
					map.put("vvcd", vvcd);

					boolean schemeAndVesselIndicator = false;
					if ("UPDATE".equalsIgnoreCase(scrmode) || "OPEN".equalsIgnoreCase(scrmode)) {
						schemeAndVesselIndicator = vesselActService.schemeAndVesselIndicator(vvcd);
						log.info("CH-7 schemeAndVesselIndicator: " + schemeAndVesselIndicator);
						map.put("schemeAndVesselIndicator", schemeAndVesselIndicator);
					}
					if (Objects.nonNull(topsModel)) {
						VesselActValueObject vesselActValueObject = (VesselActValueObject) topsModel.get(0);
						if (vesselActValueObject != null && Objects.nonNull(vesselActValueObject)) {
							lateArrivalDurationCalculation(vesselActValueObject, map, vvcd);
							osdDurationCalculation(vesselActValueObject, map, vvcd);
						}
					} else {
						map.put("latearrivalduration", 0);
						map.put("latearrivalInd", false);
					}

				}
				// End CH-7 changes
			} catch (Exception me) {
				log.error("ModelException: " + me.getMessage());
			}
		}
		// Start CH-7 changes
		// For Review OSD button, late arrival and OSD duration visibility in FE
		if ("VIEWOSD".equalsIgnoreCase(scrmode) || "QUERYOSD".equalsIgnoreCase(scrmode)
				|| "SUBMITOSD".equalsIgnoreCase(scrmode) || "SAVEOSD".equalsIgnoreCase(scrmode)) {
			try {
				String vvcd = "";
				if (criteria.getPredicates().get("vvcd") != null) {
					vvcd = (String) CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
					map.put("vvcd", vvcd);
				}
				log.info("start --- ScrMode :-" + scrmode + " --- start");
				TopsModel topsModel = (TopsModel) map.get("listData");
				Map<String, Object> osdDttmMap = new HashMap<>();

				String lineTowedBargeInd = "";
				if (Objects.nonNull(topsModel)) {
					for (int i = 0; i < topsModel.getSize(); i++) {
						VesselActValueObject vesselActValueObject = (VesselActValueObject) topsModel.get(i);

						vesselactvector.add(vesselActValueObject.getAtbDttm());
						vesselactvector.add(vesselActValueObject.getAtuDttm());
						vesselactvector.add(vesselActValueObject.getLastActDttm());
						vesselactvector.add(vesselActValueObject.getEtuDttm());

						osdDttmMap.put("atbdttm", formatDateString(vesselActValueObject.getAtbDttm()));
						osdDttmMap.put("atudttm", formatDateString(vesselActValueObject.getAtuDttm()));
						osdDttmMap.put("lastactdttm", formatDateString(vesselActValueObject.getLastActDttm()));
						osdDttmMap.put("etudttm", vesselActValueObject.getEtuDttm());

						vesselactvector.add(osdDttmMap);

						closestatusind = vesselActValueObject.getGbCloseVslInd();
						vvstatusind = vesselActValueObject.getVvStatusInd();
						lineTowedBargeInd = vesselActValueObject.getLineTowedVessel();

					}
				}

				map.put("osdDttmMap", osdDttmMap);
				map.put("vesselactvector", vesselactvector);
				map.put("lineTowedBarge", lineTowedBargeInd);

				if ("SUBMITOSD".equalsIgnoreCase(scrmode) || "SAVEOSD".equalsIgnoreCase(scrmode)) {
					List<OSDReviewObject> reviewOSDList = vesselActService.getOsdReviewList(vvcd);
					map.put("reviewOSDList", reviewOSDList);
				}
				if (Objects.nonNull(topsModel)) {
					VesselActValueObject vesselActValueObject = (VesselActValueObject) topsModel.get(0);
					if (vesselActValueObject != null && Objects.nonNull(vesselActValueObject)) {
						lateArrivalDurationCalculation(vesselActValueObject, map, vvcd);
						osdDurationCalculation(vesselActValueObject, map, vvcd);
					}
				}
				map.put("scrmode", scrmode);
				log.info("end --- ScrMode :- " + scrmode + " --- end");
			} catch (Exception me) {
				log.error("ModelException: " + me.getMessage());
			}
		}

		if ((scrmode.equalsIgnoreCase("LIST")) && (closestatusind.equalsIgnoreCase("N"))
				&& (vvstatusind.equalsIgnoreCase("AL") || vvstatusind.equalsIgnoreCase("BR")
						|| vvstatusind.equalsIgnoreCase("UB"))) {
			map.put("vesselactvector", vesselactvector);
			redirectName = "vesselAct_update";
		}
		if (scrmode.equalsIgnoreCase("WAIVERVIEW")) {
			redirectName = "vesselAct_waiver";
		}
		if (scrmode.equalsIgnoreCase("BILLVIEW")) {
			redirectName = "vesselAct_billstatus";
		}

		map.put("screen", redirectName);
	}

	private void osdDurationCalculation(VesselActValueObject vesselActValueObject, Map<String, Object> map,
			String vvcd) {
		try {
			log.info("Inside osdDurationCalculation");
			Date atuDttm = CommonUtil.convertStrToDate(vesselActValueObject.getAtuDttm(), ConstantUtil.OSD_DATE_FORMAT);
			Date latDttm = CommonUtil.convertStrToDate(vesselActValueObject.getLastActDttm(),
					ConstantUtil.OSD_DATE_FORMAT);
			if (atuDttm != null && latDttm != null) {
				long osdduration = TimeUnit.MILLISECONDS.toMinutes(atuDttm.getTime() - latDttm.getTime())
						- ConstantUtil.OSD_EXEMPTION_DURATION;

				if (vesselActService.osdSubmitindicator(vvcd, false)) {
					osdduration -= vesselActService.getSumOfExemptionMinutes(vvcd, ConstantUtil.OVERSTAY_DOCKAGE_IND);
				}
				osdduration = Math.max(osdduration, 0);
				boolean osdInd = osdduration > 0;
				map.put("osdduration", osdduration);
				map.put("osdInd", osdInd);
			}
		} catch (Exception ex) {
			log.error("Exception in osdDurationCalculation " + ex.getMessage());
		}

	}

	private void lateArrivalDurationCalculation(VesselActValueObject vesselActValueObject, Map<String, Object> map,
			String vvcd) {
		try {
			log.info("Inside lateArrivalDurationCalculation");
			Date atbDttm = CommonUtil.convertStrToDate(vesselActValueObject.getAtbDttm(), ConstantUtil.OSD_DATE_FORMAT);
			Date etbDttm = CommonUtil.convertStrToDate(vesselActValueObject.getEtbDttm(), ConstantUtil.OSD_ETB_FORMAT);
			if (atbDttm != null && etbDttm != null) {
				long latearrivalduration = TimeUnit.MILLISECONDS.toMinutes(atbDttm.getTime() - etbDttm.getTime())
						- ConstantUtil.LATE_EXEMPTION_DURATION;

				if (vesselActService.osdSubmitindicator(vvcd, false)) {
					latearrivalduration -= vesselActService.getSumOfExemptionMinutes(vvcd,
							ConstantUtil.LATE_ARRIVAL_IND);
				}
				latearrivalduration = Math.max(latearrivalduration, 0);
				boolean latearrivalInd = latearrivalduration > 0;

				map.put("latearrivalduration", latearrivalduration);
				map.put("latearrivalInd", latearrivalInd);
			} else {
				map.put("latearrivalduration", 0);
				map.put("latearrivalInd", false);
			}
		} catch (Exception ex) {
			log.error("Exception in lateArrivalDurationCalculation " + ex.getMessage());
		}
	}

	public ResponseEntity<?> getViewVesselAct(HttpServletRequest request, String vslnm, String vvcd,
			Map<String, Object> map) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		// Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: getViewVesselAct criteria:" + criteria.toString());
			TopsModel topsModel = new TopsModel();

			String coCd = "";
			coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			List<VesselActValueObject> vesselactvector = vesselActService.getVesselActShiftList(coCd, vvcd);

			String shipstore = (String) vesselActService.checkShipStore(vvcd);
			log.info("has ship store for this vv_cd " + vvcd + ":" + shipstore);
			int impCntr = (int) vesselActService.checkImportCntr(vvcd);
			log.info("has Import Cntr for this vv_cd " + vvcd + ":" + impCntr);
			int expCntr = (int) vesselActService.checkExportCntr(vvcd);
			log.info("has Export Cntr for this vv_cd " + vvcd + ":" + expCntr);
			String linetowedVesselCloseInd = ""; // added by YJ
			String atbStr = ""; // added by YJ for SL-GBMS-20040317-2
			String atuStr = ""; // added by YJ for SL-GBMS-20040317-2

			String gbArrivalWaiverInd = "";
			BigDecimal gbArrivalWaiverAmount = new BigDecimal(0);

			for (int i = 0; i < vesselactvector.size(); i++) {
				VesselActValueObject vesselActValueObject = new VesselActValueObject();
				vesselActValueObject = (VesselActValueObject) vesselactvector.get(i);
				/*
				 * Added by Yan Jun for Line-towed barge checking
				 */
				String linetowedInd = vesselActValueObject.getLineTowedVessel();

				gbArrivalWaiverInd = vesselActValueObject.getGbArrivalWaiverInd();
				gbArrivalWaiverAmount = vesselActValueObject.getGbArrivalWaiverAmount();

				if (linetowedInd != null && linetowedInd.equals("Y")) {
					if (i == 0) {
						atbStr = vesselActValueObject.getAtbDttm(); // added by YJ for SL-GBMS-20040317-2
						atuStr = vesselActValueObject.getAtuDttm(); // added by YJ for SL-GBMS-20040317-2
					}
					if (i == (vesselactvector.size() - 1)) // take the last berth atu
					{
						if (!vesselActValueObject.getAtuDttm().equals("")) {
							atuStr = vesselActValueObject.getAtuDttm(); // added by YJ for SL-GBMS-20040317-2
						}
						linetowedVesselCloseInd = checkLinetowed(vvcd, atbStr, atuStr);
					}
				}
				/*
				 * End added by Yan Jun for Line-towed barge checking
				 */

				topsModel.put(vesselActValueObject);
			}
			// end of modification after may 02

			map.put("listData", topsModel);
			List<String> waiverbillvector = new ArrayList<String>();
			waiverbillvector = vesselActService.getWaiverBillingList(vvcd);
			map.put("waiverbillvector", waiverbillvector);
			map.put("linetowedVesselCloseInd", linetowedVesselCloseInd);
			map.put("hasShipStore", shipstore);
			map.put("hasImpCntr", "" + impCntr);
			map.put("hasExpCntr", "" + expCntr);

			// 04/08/2011 PCYAP To implement late arrival waiver approving workflow
			map.put("gbArrivalWaiverInd", gbArrivalWaiverInd);
			map.put("gbArrivalWaiverAmount", gbArrivalWaiverAmount);
		} catch (BusinessException e) {
			log.error("Exception: getViewVesselAct ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception: getViewVesselAct", e);
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
				log.info("END: getViewVesselAct result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	public boolean updateVesselActivity(HttpServletRequest request, String vvcd, String scrmode)
			throws BusinessException {

		Enumeration<String> paramNames = request.getParameterNames();
		StringBuilder allParams = new StringBuilder("*****UPDATEVESSELACTIVITY RAW PARAMETERS:\n");
		while (paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			allParams.append(name).append(" = ").append(request.getParameter(name)).append("\n");
		}
		log.info(allParams.toString());

		Criteria criteria = CommonUtil.getCriteria(request);
		boolean updateInd = true;
		try {
			log.info("START: updateVesselActivity criteria:" + criteria.toString() + " vvcd:" + vvcd + " scrmode:"
					+ scrmode);

			String struserid = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String stratbdttm = "";
			String stratudttm = "";
			String strcoddttm = "";
			String strcoldttm = "";
			String strbcoddttm = "";
			String strbcoldttm = "";
			String strdiscdttm = "";
			String strloaddttm = "";
			String strvvstatus = "";
			/* Start fix Santosh */
			String strfgcdttm = "";
			/* End fix Santosh */
			/* Start Code changed by Madhu for VP Billing */
			String totalGenCargoAct = "";
			/* End here by Madhu */

			if (CommonUtility.deNull(criteria.getPredicates().get("atbdttm")) != null) {
				stratbdttm = (String) CommonUtility.deNull(criteria.getPredicates().get("atbdttm"));
			}
			if (CommonUtility.deNull(criteria.getPredicates().get("atudttm")) != null) {
				stratudttm = (String) CommonUtility.deNull(criteria.getPredicates().get("atudttm"));
			}
			if (CommonUtility.deNull(criteria.getPredicates().get("coddttm")) != null) {
				strcoddttm = (String) CommonUtility.deNull(criteria.getPredicates().get("coddttm"));
			}
			if (CommonUtility.deNull(criteria.getPredicates().get("coldttm")) != null) {
				strcoldttm = (String) CommonUtility.deNull(criteria.getPredicates().get("coldttm"));
			}
			if (CommonUtility.deNull(criteria.getPredicates().get("bcoddttm")) != null) {
				strbcoddttm = (String) CommonUtility.deNull(criteria.getPredicates().get("bcoddttm"));
			}
			if (CommonUtility.deNull(criteria.getPredicates().get("bcoldttm")) != null) {
				strbcoldttm = (String) CommonUtility.deNull(criteria.getPredicates().get("bcoldttm"));
			}
			if (CommonUtility.deNull(criteria.getPredicates().get("disdttm")) != null) {
				strdiscdttm = (String) CommonUtility.deNull(criteria.getPredicates().get("disdttm"));
			}
			if (CommonUtility.deNull(criteria.getPredicates().get("loaddttm")) != null) {
				strloaddttm = (String) CommonUtility.deNull(criteria.getPredicates().get("loaddttm"));
			}

			/* Start fix Santosh */
			if (CommonUtility.deNull(criteria.getPredicates().get("fgcdttm")) != null) {
				strfgcdttm = (String) CommonUtility.deNull(criteria.getPredicates().get("fgcdttm"));
			}
			/* End fix Santosh */
			/* Start Code changed by Madhu for Vessel Productivity Billing */
			if (CommonUtility.deNull(criteria.getPredicates().get("totGenCargoActivity")) != null) {
				totalGenCargoAct = (String) CommonUtility.deNull(criteria.getPredicates().get("totGenCargoActivity"));
			}
			/* End here by Madhu */

			/* added by YJ for linetowed vessel */
			/*
			 * String linetowedInd = criteria.getPredicates().get("lineTowedVesselInd");
			 * boolean checkLinetowed=false; if (linetowedInd != null &&
			 * linetowedInd.equals("Y")) {
			 * checkLinetowed=this.checkLinetowed(vvcd,stratbdttm,stratudttm); } if
			 * (scrmode.equalsIgnoreCase("CLOSE") && linetowedInd.equals("Y") &&
			 * checkLinetowed) { log.info("line towed vessel not updated."); throw new
			 * BusinessException("Please update linetowed vessel details"); }
			 */

			/* added by YJ for linetowed vessel */
			if (scrmode.equalsIgnoreCase("CLOSE")) {
				strvvstatus = "CL";
			}
			if (scrmode.equalsIgnoreCase("OPEN")) {
				strvvstatus = "UB";
			}
			if (scrmode.equalsIgnoreCase("UPDATE")) {
				if (!(stratbdttm.equalsIgnoreCase(""))) {
					strvvstatus = "BR";
				}
				if (!(stratbdttm.equalsIgnoreCase("")) && !(stratudttm.equalsIgnoreCase(""))) {
					strvvstatus = "UB";
				}
			}

			if (scrmode.equalsIgnoreCase("UPDATE")) {
				// START - OSD CR - NS NOV 2024
				if (!CommonUtility.deNull(strcoddttm).isEmpty()
						&& CommonUtility.deNull(criteria.getPredicates().get("isPos")).equalsIgnoreCase("true")) {
					Date date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(strcoddttm);
					Date currentDate4Hrs = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(4));
					if (date1.before(currentDate4Hrs)) {
						throw new BusinessException("COD Date Time cannot be 4 hours earlier than current Date Time");
					}
				}
				// END - OSD CR - NS NOV 2024

				String strarrsize = "0";
				if (CommonUtility.deNull(criteria.getPredicates().get("arrsize")) != null) {
					strarrsize = (String) CommonUtility.deNull(criteria.getPredicates().get("arrsize"));
				}
				if (strarrsize.equalsIgnoreCase("")) {
					strarrsize = "0";
				}

				int i = Integer.parseInt(strarrsize);
				int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));

				// Logging frontend info for debugging
				log.info("Frontend array size (arrsize): " + i);
				log.info("Frontend selected size (size): " + sizeSelected);
				log.info("Criteria predicates received: " + criteria.getPredicates().toString());

				List<String> stratbdttm1 = new ArrayList<String>();
				String atbdttm1;
				List<String> stratudttm1 = new ArrayList<String>();
				String atudttm1;
				List<String> shiftindList = new ArrayList<String>();
				String shiftind;

				if (sizeSelected != 0) {
					for (int m = 0; m < sizeSelected; m++) {
						atbdttm1 = (String) CommonUtility.deNull(criteria.getPredicates().get("atbdttm1" + m));
						stratbdttm1.add(atbdttm1);
						atudttm1 = (String) CommonUtility.deNull(criteria.getPredicates().get("atudttm1" + m));
						stratudttm1.add(atudttm1);
						shiftind = (String) CommonUtility.deNull(criteria.getPredicates().get("shiftind" + m));
						shiftindList.add(shiftind);
					}
				}

				// Log frontend list sizes and contents
				log.info("Frontend stratbdttm1 size: " + stratbdttm1.size() + " values: " + stratbdttm1.toString());
				log.info("Frontend stratudttm1 size: " + stratudttm1.size() + " values: " + stratudttm1.toString());
				log.info("Frontend shiftindList size: " + shiftindList.size() + " values: " + shiftindList.toString());

				if (stratbdttm1.size() != 0) {

					int shifts = stratbdttm1.size(); // number of shifts
					String[] stratbdttm2 = new String[shifts + 1];
					String[] stratudttm2 = new String[shifts + 1];

					// Berth row
					stratbdttm2[0] = stratbdttm; // Berth ATB
					stratudttm2[0] = stratudttm1.get(0); // Shift 2 ATU

					// Shift rows
					for (int s = 0; s < shifts; s++) {
						stratbdttm2[s + 1] = stratbdttm1.get(s); // Current shift's ATB

						if (s + 1 < shifts) {
							stratudttm2[s + 1] = stratudttm1.get(s + 1); // Next shift's ATU
						} else {
							stratudttm2[s + 1] = stratudttm; // Berth ATU
						}
					}

					// Log backend array sizes and contents
					log.info("Backend stratbdttm2 length: " + stratbdttm2.length + " values: "
							+ Arrays.toString(stratbdttm2));
					log.info("Backend stratudttm2 length: " + stratudttm2.length + " values: "
							+ Arrays.toString(stratudttm2));

					// Create validation arrays matching frontend view
					// Frontend view: Each movement has [ATB][ATU] in same row
					String[] frontendAtb = new String[shifts];
					String[] frontendAtu = new String[shifts];

					// Reconstruct frontend structure from backend arrays
					for (int s = 0; s < shifts; s++) {
						frontendAtb[s] = stratudttm2[s]; // Movement ATB from backend (left column)
						frontendAtu[s] = stratbdttm2[s + 1]; // Movement ATU from backend (right column)
					}

					log.info("Frontend validation ATB: " + Arrays.toString(frontendAtb));
					log.info("Frontend validation ATU: " + Arrays.toString(frontendAtu));

					// Validate using frontend structure
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

					// Chain: Movement 1 ATB > Berth ATB
					if (!stratbdttm.isEmpty() && !frontendAtb[0].isEmpty()) {
						if (!sdf.parse(frontendAtb[0]).after(sdf.parse(stratbdttm))) {
							throw new BusinessException("Movement 1 ATB must be greater than Berth ATB");
						}
					}

					// Validate movements
					for (int s = 0; s < shifts; s++) {
						String movementAtb = frontendAtb[s];
						String movementAtu = frontendAtu[s];

						// Within row: ATU > ATB
						if (!movementAtb.isEmpty() && !movementAtu.isEmpty()) {
							if (!sdf.parse(movementAtu).after(sdf.parse(movementAtb))) {
								throw new BusinessException("Movement " + (s + 1)
										+ " ATU must be greater than Movement " + (s + 1) + " ATB");
							}
						}

						// Chain: Next movement ATB > Current movement ATU
						if (s + 1 < shifts && !movementAtu.isEmpty() && !frontendAtb[s + 1].isEmpty()) {
							if (!sdf.parse(frontendAtb[s + 1]).after(sdf.parse(movementAtu))) {
								throw new BusinessException("Movement " + (s + 2)
										+ " ATB must be greater than Movement " + (s + 1) + " ATU");
							}
						}
					}

					// Chain: Berth ATU > Last movement ATU
					if (!stratudttm.isEmpty() && !frontendAtu[shifts - 1].isEmpty()) {
						if (!sdf.parse(stratudttm).after(sdf.parse(frontendAtu[shifts - 1]))) {
							throw new BusinessException("Berth ATU must be greater than Movement " + shifts + " ATU");
						}
					}

					vesselActService.updateVesselActivityShift(strvvstatus, stratbdttm2, stratudttm2, strcoddttm,
							strcoldttm, strbcoddttm, strbcoldttm, strdiscdttm, strloaddttm, struserid, vvcd, shifts + 1,
							strfgcdttm, totalGenCargoAct);
				} else {

					// Log simple update scenario
					log.info("No frontend shift arrays found; proceeding with single updateVesselActivity");
					log.info("Values - stratbdttm: " + stratbdttm + ", stratudttm: " + stratudttm + ", strcoddttm: "
							+ strcoddttm + ", strcoldttm: " + strcoldttm);
					log.info("User: " + struserid + ", VVCD: " + vvcd + ", Status: " + strvvstatus
							+ ", totalGenCargoAct: " + totalGenCargoAct);

					vesselActService.updateVesselActivity(strvvstatus, stratbdttm, stratudttm, strcoddttm, strcoldttm,
							strbcoddttm, strbcoldttm, strdiscdttm, strloaddttm, struserid, vvcd, strfgcdttm,
							totalGenCargoAct);
				}
			}

			log.info("updateVesselActivity completed for VVCD: " + vvcd + ", scrmode: " + scrmode);

			if (!scrmode.equalsIgnoreCase("UPDATE")) {
				vesselActService.updateVesselActStatus(strvvstatus, vvcd, struserid);
			}
		} catch (BusinessException e) {
			log.error("BusinessException in updateVesselActivity", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception in updateVesselActivity", e);
			throw new BusinessException("M4201");
		}
		return updateInd;
	}

	/**
	 * check if a towed vessel ready to be closed, if ok return false, otherwise
	 * true
	 * 
	 * @param vvcd
	 * @return true or false
	 * @throws Exception
	 */
	private String checkLinetowed(String vvcd, String atb, String atu) throws Exception {
		String result = "";
		try {

			List<LineTowedVesselValueObject> v = vesselActService.getDockageList(vvcd);
			java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("dd/MM/yyyy/HH/mm");
			if (v.size() > 0) {
				String localAtb = "";
				String localAtu = "";
				for (Iterator<LineTowedVesselValueObject> iterator = v.iterator(); iterator.hasNext();) {
					LineTowedVesselValueObject o = (LineTowedVesselValueObject) iterator.next();
					if (o.getTariffSubCatCode().equals(ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE)) {
						localAtb = sf.format(o.getStartTimestamp());
					}
					if (v.size() == 2
							&& o.getTariffSubCatCode().equals(ProcessChargeConst.TARIFF_SUB_MAR_SPECIAL_DOCKAGE)) {
						localAtu = sf.format(o.getEndTimestamp());
					}
					if (v.size() == 3 && o.getTariffSubCatCode().equals(ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY)) {
						localAtu = sf.format(o.getEndTimestamp());
					}
				}

				if (localAtb.equals(atb) && localAtu.equals(atu)) {
					result = "YES";
				} else
					result = "NO";

			} else {
				return "";
			}
		} catch (BusinessException e) {
			log.error("Exception: checkLinetowed ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception: checkLinetowed", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get("M4201");
		} finally {

		}
		return result;
	}

	// delegate.helper.gbms.ops.vesselact.linetowedvessel-->LineTowedVesselUpdateHandler
	@PostMapping(value = "/lineTowedVesselUpdate")
	public ResponseEntity<?> lineTowedVesselUpdate(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		TopsModel topsModel = new TopsModel();
		errorMessage = null;
		try {

			log.info("START: lineTowedVesselUpdate criteria:" + criteria.toString());

			String vvcd = "";
			String vslnm = "";
			String command = "";
			String fromScreen = "";

			vvcd = CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
			vslnm = CommonUtility.deNull(criteria.getPredicates().get("vslnm"));

			command = CommonUtility.deNull(criteria.getPredicates().get("scrmode"));
			fromScreen = CommonUtility.deNull(criteria.getPredicates().get("fromscreen"));
			map.put("fromscreen", fromScreen);

			VesselActValueObject vo1 = new VesselActValueObject();

			String struserids = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			if (command.equalsIgnoreCase("linetowed")) {
				// @todo update db here
				List<LineTowedVesselValueObject> ar = new ArrayList<LineTowedVesselValueObject>();
				int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
				List<String> dockageFromDttm = new ArrayList<String>();
				String dockageFrom;
				List<String> dockageToDttm = new ArrayList<String>();
				String dockageTo;

				if (sizeSelected != 0) {
					for (int i = 0; i < sizeSelected; i++) {
						dockageFrom = (String) criteria.getPredicates().get("dockageFromdttm" + i);
						dockageFromDttm.add(dockageFrom);
						dockageTo = (String) criteria.getPredicates().get("dockageTodttm" + i);
						dockageToDttm.add(dockageTo);
					}
				}
				log.info("====time array" + dockageFromDttm.get(0) + "  " + dockageFromDttm.get(1) + " to "
						+ dockageToDttm.get(0) + " " + dockageToDttm.get(1));
				SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm"); // by YJ for SL-GBMS-20040310-1 on
																				// 10/Mar/04
				for (int i = 0; i < 3; i++) {
					String mainCat = ProcessChargeConst.TARIFF_MAIN_WHARFAGE;
					String subCat = "";

					switch (i) {
					case 0:
						subCat = ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE;
						break;
					case 1:
						subCat = ProcessChargeConst.TARIFF_SUB_MAR_SPECIAL_DOCKAGE;
						break;
					case 2:
						subCat = ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY;
					default:
					}
					if (dockageFromDttm.get(i) != null || dockageToDttm.get(i) != null) {
						if (dockageFromDttm.get(i).length() == 16 && dockageToDttm.get(i).length() == 16) {
							Timestamp fromDttm = new Timestamp(sf.parse(dockageFromDttm.get(i)).getTime());
							Timestamp toDttm = new Timestamp(sf.parse(dockageToDttm.get(i)).getTime());
							LineTowedVesselValueObject vo = new LineTowedVesselValueObject();
							vo.setStartTimestamp(fromDttm);
							vo.setEndTimestamp(toDttm);
							vo.setTariffMainCatCode(mainCat);
							vo.setTariffSubCatCode(subCat);
							vo.setVvCode(vvcd);
							vo.setLastModifyUserId(struserids);
							ar.add(vo);
						}
					}

				}

				try {
					vesselActService.addDockage(vvcd, ar);
				} catch (BusinessException e) {
					List<VesselActValueObject> vesselactvector = vesselActService.getVesselActShiftList("", vvcd);
					for (int i = 0; i < vesselactvector.size(); i++) {
						VesselActValueObject o = (VesselActValueObject) vesselactvector.get(i);
						if (i == 0) {
							vo1.setVarNbr(o.getVarNbr());
							vo1.setVslNm(o.getVslNm());
							vo1.setInVoyNbr(o.getInVoyNbr());
							vo1.setOutVoyNbr(o.getOutVoyNbr());
							vo1.setVvStatusInd(o.getVvStatusInd());
							vo1.setScheme(o.getScheme());
							vo1.setAtbDttm(o.getAtbDttm());
							vo1.setAtuDttm(o.getAtuDttm());

							// 20171127 SSL-OPS-0000573: koktsing
							vo1.setLastActDttm(o.getLastActDttm());

							// 20190328 koktsing SSL-OPS-0000623
							// determine GB/CT last activity date based on later
							// date among COD_DTTM, COL_DTTM, GB_LAST_ACT_DTTM
							vo1.setCntrCodDttm(o.getCntrCodDttm());
							vo1.setCntrColDttm(o.getCntrColDttm());
							vo1.setTerminal(o.getTerminal());
							vo1.setCombiGcOpsInd(o.getCombiGcOpsInd());

						} else if (i == (vesselactvector.size() - 1)) {
							vo1.setAtuDttm(o.getAtuDttm());
						}
					}

					List<LineTowedVesselValueObject> col = vesselActService.getDockageList(vvcd);

					log.info("lineTowedVesselUpdate:" + vo1.getVarNbr() + vo1.getVslNm());
					map.put("vvcd", vvcd);
					map.put("lineTowedVessel", vo1);
					map.put("dockage", col);
					map.put("scrmode", command);
					errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
					if (errorMessage == null) {
						errorMessage = CommonUtility.getExceptionMessage(e);
					}
					map.put("errorMsg", errorMessage);
					map.put("dockage", ar);
				}

				log.info("Going back to vessel list screen");
				getViewVesselAct(request, map, criteria, vslnm, vvcd);
				map.put("scrmode", "LIST");
				if (fromScreen != null && fromScreen.equalsIgnoreCase("ClvsView")) {
					OpsValueObject opsValueObject = new OpsValueObject();

					opsValueObject = vesselActService.getVesselInfo(vvcd, opsValueObject);

					topsModel.put(opsValueObject);
					map.put("topsModel", topsModel);
					map.put("request", "ClvsViewVesselServlet");
				} else {
					map.put("request", "vesselActUpdateSer");
				}

			}

			else {
				List<VesselActValueObject> vesselactvector = vesselActService.getVesselActShiftList("", vvcd);
				for (int i = 0; i < vesselactvector.size(); i++) {
					VesselActValueObject o = (VesselActValueObject) vesselactvector.get(i);
					if (i == 0) {
						vo1.setVarNbr(o.getVarNbr());
						vo1.setVslNm(o.getVslNm());
						vo1.setInVoyNbr(o.getInVoyNbr());
						vo1.setOutVoyNbr(o.getOutVoyNbr());
						vo1.setVvStatusInd(o.getVvStatusInd());
						vo1.setScheme(o.getScheme());
						vo1.setAtbDttm(o.getAtbDttm());
						vo1.setAtuDttm(o.getAtuDttm());

						// 20171127 SSL-OPS-0000573: koktsing
						vo1.setLastActDttm(o.getLastActDttm());

						// 20190328 koktsing SSL-OPS-0000623
						// determine GB/CT last activity date based on later
						// date among COD_DTTM, COL_DTTM, GB_LAST_ACT_DTTM
						vo1.setCntrCodDttm(o.getCntrCodDttm());
						vo1.setCntrColDttm(o.getCntrColDttm());
						vo1.setTerminal(o.getTerminal());
						vo1.setCombiGcOpsInd(o.getCombiGcOpsInd());

					} else if (i == (vesselactvector.size() - 1)) {
						vo1.setAtuDttm(o.getAtuDttm());
					}
				}

				List<LineTowedVesselValueObject> col = vesselActService.getDockageList(vvcd);

				log.info(vo1.getVarNbr() + vo1.getVslNm());
				map.put("vvcd", vvcd);
				map.put("lineTowedVessel", vo1);
				map.put("dockage", col);
				map.put("scrmode", command);
				map.put("request", "vesselUnderTowedDetails");

			}
		} catch (BusinessException e) {
			log.error("Exception: lineTowedVesselUpdate ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception: lineTowedVesselUpdate", e);
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
				log.info("END: lineTowedVesselUpdate result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());

	}

	public void getViewVesselAct(javax.servlet.http.HttpServletRequest request, Map<String, Object> map,
			Criteria criteria, String vslnm, String vvcd) throws BusinessException {
		try {
			TopsModel topsModel = new TopsModel();

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			List<VesselActValueObject> vesselactvector = vesselActService.getVesselActShiftList(coCd, vvcd);
			for (int i = 0; i < vesselactvector.size(); i++) {
				VesselActValueObject vesselActValueObject = new VesselActValueObject();
				vesselActValueObject = (VesselActValueObject) vesselactvector.get(i);
				topsModel.put(vesselActValueObject);
			}

			map.put("topsModel", topsModel);
			List<String> waiverbillvector = new ArrayList<String>();
			waiverbillvector = vesselActService.getWaiverBillingList(vvcd);
			map.put("waiverbillvector", waiverbillvector);
		} catch (BusinessException e) {
			log.error("Exception: getViewVesselAct ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception: getViewVesselAct", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get("M4201");
		}
	}

	private String formatDateString(String input) throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy/HH/mm");
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		Date date = inputFormat.parse(input);
		String formatted = outputFormat.format(date);
		return formatted;
	}
}
