package sg.com.jp.generalcargo.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import io.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EdoJpBilling;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.domain.EmailValueObject;
import sg.com.jp.generalcargo.domain.GBWareHouseAplnVO;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.ShutoutEdoDnReport;
import sg.com.jp.generalcargo.domain.SmartInterfaceInputVO;
import sg.com.jp.generalcargo.domain.Sms;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.service.OutwardShutoutCargoEdoService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.RecordPaging;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;
import sg.com.jp.generalcargo.util.SmartInterfaceConstants;

@CrossOrigin
@RestController
@RequestMapping(value = OutwardShutoutCargoEdoController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OutwardShutoutCargoEdoController {

	public static final String ENDPOINT = "gc/outwardcargo/shutoutEdo";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(OutwardShutoutCargoEdoController.class);

	@Autowired
	private OutwardShutoutCargoEdoService outwardShutoutCargoEdoService;
	
	@Value("${jp.common.notificationProperties.emailFromAddress}")
	private String emailWarehouseFrom;
	
	@Value("${jp.common.notificationProperties.VM.DNCreated.subject}")
	private String emailCreateDnSubject;
	
	@Value("${jp.common.notificationProperties.VM.DNCreated.body}")
	private String emailCreateDnBody;
	
	@Value("${pdfDir.downloadfile.path}")
	private String temporaryReportWorkingDir;
	
	@Value("${pdfDir.downloadfile.path}")
	private String printingBeanPdf;
	
	// delegate.helper.gbms.ops.dnua.dn --> dnCreateHandler
	@ApiOperation(value = "dnCreate", response = String.class)
	@PostMapping(value = "/dnCreate")
	public ResponseEntity<?> dnCreate(HttpServletRequest request) throws BusinessException {

		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** dnCreate Start criteria :" + criteria.toString());

			String s = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String s1 = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			List<EdoValueObjectOps> vector = new ArrayList<EdoValueObjectOps>();
			List<EdoValueObjectOps> vector1 = new ArrayList<EdoValueObjectOps>();
			String s2 = "";
			String s3 = "";
			String s4 = "";
			String s5 = "";
			String s6 = "";
			String s7 = "";
			String s8 = "";
			String s9 = "";
			String s10 = "";
			String s11 = "";
			String s12 = "";
			String s17 = "";
			String s18 = "";
			s18 = CommonUtility.deNull(criteria.getPredicates().get("searchcrg"));
			String s19 = "";
			s19 = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr"));
			s3 = CommonUtility.deNull(criteria.getPredicates().get("edoNbr"));
			s4 = CommonUtility.deNull(criteria.getPredicates().get("transtype"));
			s5 = CommonUtility.deNull(criteria.getPredicates().get("edo_Nbr_Pkgs"));
			s6 = CommonUtility.deNull(criteria.getPredicates().get("NomWt"));
			s7 = CommonUtility.deNull(criteria.getPredicates().get("NomVol"));
			s9 = CommonUtility.deNull(criteria.getPredicates().get("transQty"));
			s10 = CommonUtility.deNull(criteria.getPredicates().get("nric_no"));
			s17 = CommonUtility.deNull(criteria.getPredicates().get("ictype"));
			s11 = CommonUtility.deNull(criteria.getPredicates().get("dpname"));
			s12 = CommonUtility.deNull(criteria.getPredicates().get("veh1")).toUpperCase();
			/*
			 * s13 = CommonUtility.deNull(criteria.getPredicates().get("veh2"); s14 =
			 * CommonUtility.deNull(criteria.getPredicates().get("veh3"); s15 =
			 * CommonUtility.deNull(criteria.getPredicates().get("veh4"); s16 =
			 * CommonUtility.deNull(criteria.getPredicates().get("veh5");
			 */
			s8 = CommonUtility.deNull(criteria.getPredicates().get("transDate"));
			/* ++ Added by VietND 12-12-09 - to get Cargo Destination */
			String cargoDes = "";
			cargoDes = CommonUtility.deNull(criteria.getPredicates().get("CargoDes"));
			/*-- VietND */
			log.info("cargoDes:: " + cargoDes);

			log.info("Start logic validation DN ..");
			log.info("nric_no : " + s10);
			log.info("ictype : " + s17);
			log.info("edoNbr : " + s3);

			if (!"S".equalsIgnoreCase(s17)) {
				int totalCusCd = 0;
				totalCusCd = outwardShutoutCargoEdoService.getTotalCustCdByIcNumber(s10, s17);
				if (totalCusCd == 0) {
					log.info("Invalid IC number");
//					throw new BusinessException("M100011");
					throw new BusinessException("Invalid IC number");
				}
			}
			// BEGIN FPT modify for Warehouse Management CR, 10/02/2014.

			String edoNbr = CommonUtility.deNull(criteria.getPredicates().get("edoNbr"));

			String errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M80008");
			log.info(errorMessage);
 			if (!"Y".equalsIgnoreCase(CommonUtility.deNull(criteria.getPredicates().get("forceSubmit")))) {
				if (outwardShutoutCargoEdoService.isExistWarehouseApplicationWithASNNubmer(edoNbr)) {
					// throw new BusinessException("M80008");
					map.put("confirmMessage", errorMessage);
					map.put("needConfirm", "Y");

					// forwardHandler(httpservletrequest, "dnDetail");

					if (errorMessage != null) {
						map.put("error", errorMessage);
						result = new Result();
						result.setError(errorMessage);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: dnCreate result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
			} else {
				// send email & SMS alert.
				List<GBWareHouseAplnVO> warehouseList = outwardShutoutCargoEdoService
						.getWarehouseApplicationListByASNNubmer(edoNbr);
				if (warehouseList != null && warehouseList.size() > 0) {
					GBWareHouseAplnVO gbWareHouseAplnVO = null;
					for (Object object : warehouseList) {
						gbWareHouseAplnVO = (GBWareHouseAplnVO) object;

						String warehouseRefNubmer = gbWareHouseAplnVO.getAplnRefNo();

						String customerEmail = gbWareHouseAplnVO.getCustomerEmail();
						String customerTel = gbWareHouseAplnVO.getCustomerTelephone();
						String emailAlertInd = gbWareHouseAplnVO.getEmailAlertIndicator();
						String smsAlertInd = gbWareHouseAplnVO.getSmsAlertIndicator();


						String emailSubject = emailCreateDnSubject;
						emailSubject = emailSubject.replaceAll("<Wa Ref Nbr>",
								CommonUtility.deNull(warehouseRefNubmer));

						String emailBody = emailCreateDnBody;
						emailBody = emailBody.replaceAll("<Wa Ref Nbr>", CommonUtility.deNull(warehouseRefNubmer));
						emailBody = emailBody.replaceAll("<Expiry Date>", CommonUtility.deNull(s8));

						String sender = emailWarehouseFrom;

						// send email
						if ("Y".equals(emailAlertInd) && StringUtils.isNotBlank(customerEmail)) {
							try {

								EmailValueObject vo = new EmailValueObject();
								String[] emailStr = new String[] { customerEmail };
								vo.setRecipientAddress(emailStr);
								vo.setSubject(emailSubject);
								vo.setMessage(emailBody);
								vo.setSenderAddress(CommonUtility.deNull(sender));
								// messengerFacade.sendMessage(vo, "text/html");
							} catch (Exception e) {

								log.info(e);
							}
						}

						// send SMS
						if ("Y".equals(smsAlertInd) && StringUtils.isNotBlank(customerTel)) {
							try {

								List<String> customerTelList = new ArrayList<String>();
								customerTelList.add(customerTel);
								Sms vo = new Sms();
								vo.setToList(customerTelList);
								vo.setMessage(emailBody);

								outwardShutoutCargoEdoService.sendMessage(vo);
							} catch (Exception e) {

								log.info(e);
							}
						}
					}
				}

				// force submit and void warehouse .
				outwardShutoutCargoEdoService.voidWarehouseApplicationWithASNNubmer(edoNbr, s);

			}

			// END FPT modify for Warehouse Management CR, 10/02/2014.
			// amend by Zhenguo Deng(harbor) 01/08/2011 : START
			if (s18.equals("SL")) {
				try {
					// BEGIN added by Maksym JCMS Smart CR 6.2
					if (StringUtils.isNotBlank(cargoDes) && "Out of JP".equalsIgnoreCase(cargoDes.trim())) {
						if (!outwardShutoutCargoEdoService.isValidVehicleNumber(s12, s1)) {
							throw new BusinessException("M1000001");
						}
					}
					// END added by Maksym JCMS Smart CR 6.2
					s2 = outwardShutoutCargoEdoService.createShutoutDN(s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s,
							s17, s18, s19, cargoDes);

					// commented by hujun on 26/12/2011 ATB shouldn't affect the event logging
					// boolean flag = outwardShutoutCargoEdoService.chkraiseCharge(s3);

					// if(flag)
					// {
					String dn_nbr = s2;
					String userId = s;
					String status = outwardShutoutCargoEdoService.triggerShutoutCargoDN(dn_nbr, userId);
					map.put("updatestatus", status);
					// }
					// comment end

					vector = outwardShutoutCargoEdoService.fetchShutoutDNDetail(s3, s2);
					vector1 = outwardShutoutCargoEdoService.getVechDetails(s2);
				} catch (BusinessException be) {
					log.info("Exception dnCreate: ", be);
					errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
					if (errorMessage == null) {
						errorMessage = be.getMessage();
					}
				}catch (Exception ex) {
					log.info("\n\n 1***** " + ex);
					// BEGIN amended by Maksym JCMS Smart CR 6.2
					// } catch (BusinessException ex) {
					// log.info("\n\n 2***** " + ex);
					// END amended by Maksym JCMS Smart CR 6.2
					if (errorMessage == null) {
						errorMessage = ex.getMessage();
					}
				}
				map.put("dnDetail", vector);
				map.put("vechDetail", vector1);
				map.put("dnNbr", s2);

				if (errorMessage != null) {
					map.put("error", errorMessage);
					result = new Result();
					result.setError(errorMessage);
					result.setSuccess(false);
					result.setData(map);

				} else {
					result.setData(map);
					result.setSuccess(true);
					log.info("END: dnCreate result: " + result.toString());
				}

				return ResponseEntityUtil.success(result.toString());
			}
			// amend by Zhenguo Deng(harbor) 01/08/2011 : END

			try {

				/*
				 * s2 = outwardShutoutCargoEdoService.createDN(s3, s4, s5, s6, s7, s8, s9, s10,
				 * s11, s12, s13, s14, s15, s16, s, s17, s18, s19);
				 */
				// ++ Changed by VietND - remove parameter veh2,veh3,veh4,veh5, add parameter
				// cargoDes
				// BEGIN added by Maksym JCMS Smart CR 6.10
				if (StringUtils.isNotBlank(cargoDes) && "Out of JP".equalsIgnoreCase(cargoDes.trim())) {
					if (!outwardShutoutCargoEdoService.isValidVehicleNumber(s12, s1)) {
						throw new BusinessException("M1000001");
					}
				}
				// END added by Maksym JCMS Smart CR 6.10
				s2 = outwardShutoutCargoEdoService.createDN(s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s, s17, s18, s19,
						cargoDes);
				// -- VietND
				// Added by Babatunde on Dec., 2013 : START
				// This is to check if EDO_ASN_NBR is in TESN_JP_JP and auto create UA if true
				boolean isJPJP = outwardShutoutCargoEdoService.isTESN_JP_JP(s3, s19);
				if (isJPJP == true) {

					String uaNbr = outwardShutoutCargoEdoService.createUA(s19, s4, s5, s6, s7, s8, s9, s10, s17, s11,
							s12, s);

					StringTokenizer stringtokenizer = new StringTokenizer(outwardShutoutCargoEdoService.getUANbr(s19),
							"-");

					String ftrans = stringtokenizer.nextToken().trim();

					// if(outwardShutoutCargoEdoService.checkEsnStuffIndicator(s19)) {
					// ftrans="False";
					// }
					log.info("Need to Trigger CaB UA calling for create ? " + ftrans);

					// if (ftrans.equals("True")) {
					log.info("Before Trigger CaB UA calling for create");

					String vvCd = outwardShutoutCargoEdoService.getVcd(s19);

					String updatestatusUA = outwardShutoutCargoEdoService.TriggerUa(uaNbr, s, vvCd);

					log.info("After Trigger CaB UA called for create");

					if (updatestatusUA != null && !updatestatusUA.equals("")
							&& updatestatusUA.equalsIgnoreCase("FALSE")) {
						log.info("Trigger CaB UA calling DPE:  FALSE.");
						map.put("updatestatus", "FALSE");
					} else if (updatestatusUA != null && !updatestatusUA.equals("")
							&& updatestatusUA.equalsIgnoreCase("TRUE")) {
						map.put("updatestatus", "TRUE");
						log.info("Trigger CaB UA calling DPE  TRUE.");
					}

					// }

				}
				// Added by Babatunde on Dec., 2013 : END

				// Added by SONLT
				if (outwardShutoutCargoEdoService.checkESNCntr(s3)) {
					String cntrNo = "";
					String cntrSeq = "";
					String cntr = CommonUtility.deNull(criteria.getPredicates().get("cntrNo"));
					if ((cntr != null) && (!"".equals(cntr))) {
						String[] tmp = new String[2];
						tmp = cntr.split(",");
						cntrSeq = tmp[0].trim();
						cntrNo = tmp[1].trim();
						// Update container field for DN_DETAILS table
						outwardShutoutCargoEdoService.updateDN(cntrNo, s2);
						// Get current Status of the container
						/* TungVH 29-JAN-2010 starts */
						long declrWt = (long) Double
								.parseDouble(CommonUtility.deNull(criteria.getPredicates().get("declrWt")));
						log.info("-----------------declrWt----" + declrWt);
						/* TungVH 29-JAN-2010 ends */
						int declrPkgs = Integer
								.parseInt(CommonUtility.deNull(criteria.getPredicates().get("declrPkgs")));
						long weight = (Integer.parseInt(s9) * declrWt) / declrPkgs;

						// Check first DN for each MOT container to capture time and weight
						int noOfdn = 0;
						noOfdn = outwardShutoutCargoEdoService.checkFirstDN(s3, cntrNo);
						if (noOfdn <= 0) {
							// update status and time
							String newCatCd = outwardShutoutCargoEdoService.getNewCatCd(cntrSeq);
							outwardShutoutCargoEdoService.updateCntr(cntrSeq, cntrNo, s, newCatCd);

							// update weight
							outwardShutoutCargoEdoService.updateWeight(cntrSeq, weight, s, "ADD");
						} else {
							// update weight
							outwardShutoutCargoEdoService.updateWeight(cntrSeq, weight, s, "ADD");
						}
					}
				}
				// END

			}

			catch (Exception ex) {
				log.info("\n\n 2***** " + ex);
				// BEGIN added by Maksym JCMS Smart CR 6.2
				if (ex.getMessage().equals("M1000001")) {
					throw new BusinessException(ex.getMessage());
				}
				// END added by Maksym JCMS Smart CR 6.2
			}
			// Start added for SMART CR by FPT on 23-Jan-2014
			String refType = "", refNbr = "";
			boolean callSmartInterface = false;
			if (s2 != null && !"".equals(s2) && ("LT".equals(s18) || "T".equals(s18))) {
				if ("T".equals(s18) && StringUtils.isNotBlank(s19)) {
					String chkTesnJpJp_nbr = outwardShutoutCargoEdoService.chktesnJpJp_nbr(s19);
					refNbr = s19;

					if ("Y".equals(chkTesnJpJp_nbr)) {
						// Is this TESN_JP_JP
						refType = "TESNJJ";
						callSmartInterface = true; // added SL-SMART-20171006-01 Fixed to release for J TO J case
					} else {
						String chkTesnJpPsa_nbr = outwardShutoutCargoEdoService.chktesnJpPsa_nbr(s19);

						if ("Y".equals(chkTesnJpPsa_nbr)) {
							// Is this TESN_JP_PSA
							refType = "TESNJP";
							callSmartInterface = true;
						} else {
							refType = "ESN";
						}
					}
				} else {
					refType = "EDO";
					refNbr = s3;
					callSmartInterface = true;
				}
			}

			if (callSmartInterface) {
				String serverIp = null;
				String serverNm = null;
				log.info(
						"*********Before SMART interface calling for release the locations for the packages in DN and register this event in SMART: DN number="
								+ s2);
				try {

					InetAddress jpOnlineAddress = InetAddress.getLocalHost();
					if (jpOnlineAddress != null) {
						serverIp = jpOnlineAddress.getHostAddress();
						serverNm = jpOnlineAddress.getHostName();
					}

					log.info("releaseStorageOccupancy Smart input: refType =" + refType);
					log.info("releaseStorageOccupancy Smart input: refNbr=" + refNbr);
					log.info("releaseStorageOccupancy Smart input: Number of package=" + s9);

					// Call SMART interface for EDO or transhipment cargo JP-PSA only

					// release the locations for the packages of DN in SMART system
					SmartInterfaceInputVO inputObj = new SmartInterfaceInputVO();
					inputObj.setUserId(s);
					inputObj.setActionCd(SmartInterfaceConstants.CREATE_DN_EVENT_CD);
					inputObj.setOccSrcCd(SmartInterfaceConstants.SOURCE_JP);
					inputObj.setServerIp(serverIp);
					inputObj.setServerNm(serverNm);
					inputObj.setClassNm(this.getClass().getName());
					inputObj.setClassDesc("DN create handler class");
					inputObj.setRefNbr(refNbr);
					inputObj.setRefType(refType);
					inputObj.setStayingInJP(false);
					inputObj.setNbrPkgs(Integer.parseInt(s9));
					inputObj.setTransNbr(s2);

					DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					df.setLenient(false);
					inputObj.setTransDttm(df.parse(s8));

					// SmartInterfaceUtil.getInstance().releaseStorageOccupancy(inputObj);
				} catch (Exception ex) {
					log.info("Call SMART Interface releaseStorageOccupancy exception: DN number=" + s2);
					log.info("Exception: " , ex);
				}
				log.info("After SMART interface calling");

			}
			// End added for SMART CR by FPT on 23-Jan-2014
			boolean isCntrCrgDn = outwardShutoutCargoEdoService.chkCntrCrgDn(s2);// added by vani to chk cntr crg DN
			if (!isCntrCrgDn && !s2.equals(""))// if not cntr crg dn then only raise charges
			{
				boolean flag = outwardShutoutCargoEdoService.chkraiseCharge(s3);

				if (flag) {
					String s20 = outwardShutoutCargoEdoService.TriggerDN(s2, s);
					if (s20 != null && !s20.equals("") && s20.equalsIgnoreCase("FALSE"))
						map.put("updatestatus", "FALSE");
					else if (s20 != null && !s20.equals("") && s20.equalsIgnoreCase("TRUE"))
						map.put("updatestatus", "TRUE");
				}
			}
			if (!s2.equals("")) {
				vector = outwardShutoutCargoEdoService.fetchDNDetail(s3, s2, s4, s18, s19);
				vector1 = outwardShutoutCargoEdoService.getVechDetails(s2);
				map.put("dnDetail", vector);
				map.put("vechDetail", vector1);
				map.put("dnNbr", s2);
			} else {
				throw new BusinessException("M80007");
			}

		} catch (BusinessException be) {
			log.info("Exception dnCreate: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception dnCreate : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: dnCreate result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.ops.dnua.dn-->dnEdoDetailHandler

	@ApiOperation(value = "dnEdoDetail", response = String.class)
	@PostMapping(value = "/dnEdoDetail")
	public ResponseEntity<?> dnEdoDetail(HttpServletRequest request) throws BusinessException {

		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		String searchCrg = "";
		errorMessage = null;
		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** dnEdoDetail Start criteria :" + criteria.toString());

			String s5 = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String s7 = "";

			searchCrg = CommonUtility.deNull(criteria.getPredicates().get("searchcrg"));
			String s11 = "";
			s11 = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr")); // stuff sequence no for stuffing
			s7 = CommonUtility.deNull(criteria.getPredicates().get("flag"));
			String s12 = CommonUtility.deNull(criteria.getPredicates().get("release"));

			List<?> vector = null;
			List<EdoValueObjectOps> vector1 = new ArrayList<EdoValueObjectOps>();

			List<EdoValueObjectOps> subAdpVector = new ArrayList<EdoValueObjectOps>();
			// Added by SONLT---------------------------------------------
			boolean chk = outwardShutoutCargoEdoService.checkESNCntr(criteria.getPredicates().get("edo"));
			if (chk) {
				map.put("esncntr", "YES");
			} else {
				map.put("esncntr", "NO");
			}
			// End--------------------------------------------------------

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			map.put("userId", userId);

			if (s7 != null && s7.equals("Y")) {
				String s14;
				String cntrNbr = CommonUtility.deNull(criteria.getPredicates().get("cntrNbr"));
				String cntrSeq = "";
				if (!"".equals(CommonUtility.deNull(cntrNbr))) {
					cntrSeq = outwardShutoutCargoEdoService.getCntrSeq(cntrNbr);
				}

				// MCConsulting: if the 1st DN is cancelled then update subsequent active DN nbr
				// in the billable events table
				String transactionType = criteria.getPredicates().get("transType");
				boolean canCancelDn = true;
				if ((transactionType != null && transactionType.equals("L"))
						|| (searchCrg != null && searchCrg.equals("SL"))) {
					// system should not allow to cancel DN after the next day of DN creation

					String dnNbr = criteria.getPredicates().get("dnnbr");

					canCancelDn = outwardShutoutCargoEdoService.checkCancelDN(dnNbr);

					if (canCancelDn) {
						log.info("**Updating the first DN in billable events with subsequent Active DN");
						outwardShutoutCargoEdoService.checkAndUpdateFirstDN(criteria.getPredicates().get("edo"), dnNbr);

					} else {
						errorMessage = ConstantUtil.cancel_dn_001;
					}

				}

				// MCConsulting - check if the DN is allowed to cancel first before calling
				// cancelBillableCharges function
				if (canCancelDn) {
					if (outwardShutoutCargoEdoService.cancelBillableCharges(criteria.getPredicates().get("dnnbr"),
							"DN")) {
						// Begin TungVH
						String dnFirst = "";
						if (chk && !"".equals(CommonUtility.deNull(cntrSeq))) {
							dnFirst = outwardShutoutCargoEdoService.getDnCntrFirst(cntrSeq, cntrNbr);
						}
						// End TungVH

						// Amend by Zhenguo Deng(harbor) 03/08/2011 : START
						map.put("searchCrg", searchCrg);
						log.info("Cancel DN details :searchCrg :" + searchCrg);
						if (searchCrg.equals("SL")) {
							s14 = outwardShutoutCargoEdoService.cancelShutoutDN(criteria.getPredicates().get("edo"),
									criteria.getPredicates().get("dnnbr"), s5);
							
							log.info("s14: " + s14);
						} else {
							s14 = outwardShutoutCargoEdoService.cancelDN(criteria.getPredicates().get("edo"),
									criteria.getPredicates().get("dnnbr"), s5,
									criteria.getPredicates().get("transType"), searchCrg, s11);
							if (searchCrg.trim().equals("T")) {
								log.info("Cancel DN details :searchCrg :" + searchCrg);

								int nbrPkg = Integer.parseInt(criteria.getPredicates().get("tranQty"));
								String transDttm = criteria.getPredicates().get("transDttm");
								String transType = outwardShutoutCargoEdoService.checkTransType(s11);
								String dpNm = criteria.getPredicates().get("dpNm");
								String dpIcNbr = criteria.getPredicates().get("dpIcNbr");
								log.info("Cancel DN details :nbrPkg :" + nbrPkg);
								log.info("Cancel DN details :transType :" + transType);
								log.info("Cancel DN details :dpNm :" + dpNm);
								log.info("Cancel DN details :dpIcNbr :" + dpIcNbr);
								String uaNbr = outwardShutoutCargoEdoService.getUaNbr(s11, nbrPkg, transDttm, dpNm,
										dpIcNbr);
								log.info("Cancel DN details :uaNbr :" + uaNbr);
								if (uaNbr != null && !uaNbr.equals("")) {
									if (outwardShutoutCargoEdoService.cancelBillableCharges(uaNbr, "UA")) {
										outwardShutoutCargoEdoService.cancelUA(uaNbr, s11, transType, s5,
												Integer.toString(nbrPkg));
										log.info("UA Cancelled!");
									}
								}
							}
							// Added by Babatunde on Dec., 2013 : End

							// VietNguyen added to implement logic cancel DN -> should be check Stuff cntr

							if (chk && !"".equals(CommonUtility.deNull(cntrSeq))) {
								// check dn balance
								boolean countBalance = outwardShutoutCargoEdoService.countDNBalance(cntrNbr);
								log.info("Cancel DN details :and do check update stuff cntr : ?? :" + countBalance);
								if (!countBalance) {
									outwardShutoutCargoEdoService.updateCntrStatus(cntrSeq, s5);
								}
								// do update cntr status
							}

						}
						// Amend by Zhenguo Deng(harbor) 03/08/2011 : END
						// Added by SONLT
						if (chk && !"".equals(CommonUtility.deNull(cntrSeq))) {
							// Begin TungVH
							String dnNbr = criteria.getPredicates().get("dnnbr");
							int remDn = 0;
							remDn = outwardShutoutCargoEdoService.checkFirstDN(dnNbr, cntrNbr);
							if (remDn <= 0) {
								String newCatCd = outwardShutoutCargoEdoService.getNewCatCd(cntrSeq);
								outwardShutoutCargoEdoService.changeStatusCntr(cntrSeq, s5, newCatCd);
							}
							if (dnNbr.equals(dnFirst)) {
								// insert USTF into cntr_txn
								outwardShutoutCargoEdoService.cancel1stDn(cntrSeq, cntrNbr, s5);
								// Timestamp dttmSecond = null;
								// dttmSecond = outwardShutoutCargoEdoService.getDnSecond(cntrSeq, cntrNbr, s5);
								// if (dttmSecond == null) {
								// have not any DN
								// outwardShutoutCargoEdoService.changeStatusCntr(cntrSeq, s5);
								// } else {
								// At least one DN
								// outwardShutoutCargoEdoService.changeFirstDN(cntrSeq, cntrNbr, s5,
								// dttmSecond);
								// }
							}
							// End TungVH
							long declrWt = Long.parseLong(criteria.getPredicates().get("declrWt"));
							int declrPkgs = Integer.parseInt(criteria.getPredicates().get("declrPkgs"));
							int tranQty = Integer.parseInt(criteria.getPredicates().get("tranQty"));
							long weight = (tranQty * declrWt) / declrPkgs;
							outwardShutoutCargoEdoService.updateWeight(cntrSeq, weight, s5, "SUB");
						}
						// END SONLT
						// Start added for SMART CR by FPT on 24-Jan-2014
						log.info("searchCrg=" + searchCrg);
						String refType = "", refNbr = "";
						boolean callSmartInterface = false;
						if ("LT".equals(searchCrg) || "T".equals(searchCrg)) {
							if ("T".equals(searchCrg) && StringUtils.isNotBlank(s11)) {
								refNbr = s11;

								String chkTesnJpPsa_nbr = outwardShutoutCargoEdoService.chktesnJpPsa_nbr(s11);

								if ("Y".equals(chkTesnJpPsa_nbr)) {
									// Is this TESN_JP_PSA
									refType = "TESNJP";
									callSmartInterface = true;
								}
							} else {
								refType = "EDO";
								refNbr = criteria.getPredicates().get("edo");
								callSmartInterface = true;
							}
						}
						if (callSmartInterface) {
							String dnNbr = criteria.getPredicates().get("dnnbr");
							log.info(
									"Before SMART interface calling for restore the previous occupancy for the affected number of packages: DN number="
											+ dnNbr);
							try {

								double declrWt = Double.parseDouble(criteria.getPredicates().get("declrWt"));
								int declrPkgs = Integer.parseInt(criteria.getPredicates().get("declrPkgs"));
								int tranQty = Integer.parseInt(criteria.getPredicates().get("tranQty"));
								double weight = (double) (tranQty * declrWt) / declrPkgs;

								BigDecimal tonnage = BigDecimal.valueOf(weight / 1000);
								String vvCd = criteria.getPredicates().get("vvCd");

								String serverIp = null;
								String serverNm = null;
								InetAddress jpOnlineAddress = InetAddress.getLocalHost();
								if (jpOnlineAddress != null) {
									serverIp = jpOnlineAddress.getHostAddress();
									serverNm = jpOnlineAddress.getHostName();
								}

								log.info("DN Cancel serverIp    = " + serverIp);
								log.info("DN Cancel serverName = " + serverNm);
								log.info("DN Cancel number of package = " + tranQty);
								log.info("DN Cancel tonnage = " + tonnage.doubleValue());
								log.info("DN Cancel vvCd = " + vvCd);
								log.info("DN Cancel RefNbr = " + refNbr);

								SmartInterfaceInputVO inputObj = new SmartInterfaceInputVO();
								inputObj.setUserId(s5);
								inputObj.setOccSrcCd(SmartInterfaceConstants.SOURCE_JP);
								inputObj.setServerIp(serverIp);
								inputObj.setServerNm(serverNm);
								inputObj.setClassNm(this.getClass().getName());
								inputObj.setClassDesc("DN cancel handler class");
								inputObj.setRefNbr(refNbr);
								inputObj.setRefType(refType);
								inputObj.setNbrPkgs(tranQty);
								inputObj.setTonnage(tonnage);
								inputObj.setVvCd(vvCd);
								inputObj.setTransNbr(dnNbr);

								// SmartInterfaceUtil.getInstance().restoreStorageOccupancy(inputObj); todo
							} catch (Exception ex) {
								log.info("Call SMART Interface restoreStorageOccupancy exception:DN number=" + dnNbr);
								log.info("Exception: " + ex.getMessage());
							}
							log.info("After SMART interface calling");
						}
						// End edded for SMART CR by FPT on 24-Jan-2014
					} else {
						errorMessage = ConstantUtil.cancel_dn_002;
					}
				} // MCConsulting - End if canCancelDN
					// Amend by Zhenguo Deng(harbor) 03/08/2011 : START
				if (searchCrg.equals("SL")) {
					vector = new ArrayList<EdoValueObjectCargo>();
					vector = outwardShutoutCargoEdoService.getShutoutEdoDetail(criteria.getPredicates().get("edo"));
					vector1 = outwardShutoutCargoEdoService.fetchShutoutDNList(criteria.getPredicates().get("edo"));
				} else {
					vector = new ArrayList<EdoValueObjectOps>();
					vector = outwardShutoutCargoEdoService.fetchEdoDetails(criteria.getPredicates().get("edo"),
							searchCrg, s11);
					String listAllDn = criteria.getPredicates().get("listAllDn");
					if ("TRUE".equalsIgnoreCase(listAllDn)) {
						vector1 = outwardShutoutCargoEdoService.fetchDNList(criteria.getPredicates().get("edo"), "ALL",
								s11);
					} else
						vector1 = outwardShutoutCargoEdoService.fetchDNList(criteria.getPredicates().get("edo"),
								searchCrg, s11);
				}
				// Amend by Zhenguo Deng(harbor) 03/08/2011 : END

			} else {
				 vector = new ArrayList<EdoValueObjectOps>();
				vector = outwardShutoutCargoEdoService.fetchEdoDetails(criteria.getPredicates().get("edo"), searchCrg,
						s11);
				String listAllDn = criteria.getPredicates().get("listAllDn");
				if ("TRUE".equalsIgnoreCase(listAllDn)) {
					vector1 = outwardShutoutCargoEdoService.fetchDNList(criteria.getPredicates().get("edo"), "ALL",
							s11);
				} else
					vector1 = outwardShutoutCargoEdoService.fetchDNList(criteria.getPredicates().get("edo"), searchCrg,
							s11);

				// map.put("stuffSeqNbr",s11);

			}
			String s8 = outwardShutoutCargoEdoService.chktesnJpJp_nbr(s11);
			String s9 = outwardShutoutCargoEdoService.chktesnJpPsa_nbr(s11);
			// added by Vinayak on 05 Feb 2004
			boolean checkEdoStuff = false;
			if (searchCrg.trim().equals("T")) {
				checkEdoStuff = outwardShutoutCargoEdoService.chkEDOStuffing(criteria.getPredicates().get("edo")); // vinayak
																													// added
																													// 16
																													// jan
																													// 2004
			}
			// end added by Vinayak on 05 Feb 2004

			// added by Tatang on 15 Apr 2008 - begin

			// Added by Vietnd02 -- to check size =0
			if (vector.size() == 0 || vector == null) {
				errorMessage = ConstantUtil.Error_M4500;
			}
				
			// vietnd02::end
			// CashSalesValueObject csvo = cs.getCashSales(vector1);
			List<CashSalesValueObject> csList;
			if (vector1.size() > 0) {
				csList = outwardShutoutCargoEdoService.getCashSales(vector1);
			} else {
				csList = new ArrayList<CashSalesValueObject>();
			}
			map.put("csList", csList);
			log.info(vector1.size() + ", " + csList.size());
			// log.info(criteria.getPredicates().get("dnnbr") + " - " +
			// csvo.getCashReceiptNbr() + " - " + csvo.getReceiptDttm());
			// added by Tatang on 15 Apr 2008 - end

			// BEGIN FPT Amend to check for TESN JP - JP (existing tesnjpjp check for wrong
			// ESN number, maybe used for different purpose
			String edoTesnJpJp = "N";
			if (outwardShutoutCargoEdoService.chktesnJpJp(criteria.getPredicates().get("edo"))) {
				edoTesnJpJp = "Y";
			}
			log.info("edoTesnJpJp :" + edoTesnJpJp);
			map.put("edoTesnJpJp", edoTesnJpJp);
			// END FPT Amend to check for TESN JP - JP (existing tesnjpjp check for wrong
			// EDO number, maybe used for different purpose

			log.info("checkEdoStuff :" + checkEdoStuff);
			map.put("checkEdoStuff", "" + checkEdoStuff);
			map.put("tesnjpjp", "" + s8);
			map.put("tesnjppsa", "" + s9);
			map.put("edovect", vector);
			map.put("dnList", vector1);

			subAdpVector = outwardShutoutCargoEdoService.fetchSubAdpDetails(criteria.getPredicates().get("edo"));
			map.put("subAdpVector", subAdpVector);

			int spencialPkgs = outwardShutoutCargoEdoService.getSpencialPackage(criteria.getPredicates().get("edo"));
			map.put("spencialPkgs", spencialPkgs + "");
			// Amend by Zhenguo Deng(harbor) 03/08/2011 : START
			if (s12 != null && s12.equals("release")) {
				map.put("request", "ReleaseEdo");
			} else if (searchCrg.equals("SL")) {
				map.put("edo", criteria.getPredicates().get("edo"));
				map.put("edoValueObject", vector.size() > 0 ?  vector.get(0) : new ArrayList<EdoValueObjectCargo>());
				map.put("frommode", "SEARCH");
				map.put("funParam", "THREE");
				map.put("status", "L");
				map.put("dnVector", vector1);
				map.put("fromDn", "Y");
				map.put("showTop", "Y");
				map.put("request", "shutoutEdoSearch");
			} else {
				map.put("request", "dnEdoDetail");
			}
			// Amend by Zhenguo Deng(harbor) 03/08/2011 : END
		} catch (BusinessException be) {
			log.info("Exception dnEdoDetail: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception dnEdoDetail : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: dnEdoDetail result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.ops.dnua.dn-->dnUpdateHandler

	@ApiOperation(value = "dnUpdate", response = String.class)
	@PostMapping(value = "/dnUpdate")
	public ResponseEntity<?> dnUpdate(HttpServletRequest request) throws BusinessException {

		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** dnUpdate Start criteria :" + criteria.toString());

			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			// END added by Maksym JCMS Smart CR 6.2
			String edo = criteria.getPredicates().get("edo");
			String dnNbr = criteria.getPredicates().get("dnNbr");
			String transType = criteria.getPredicates().get("transType");
			String printView = null;
			String searchcrg = criteria.getPredicates().get("searchcrg");
			String tesn_nbr = criteria.getPredicates().get("tesn_nbr");
			String flag = "Y";
			String status = criteria.getPredicates().get("status");
			String updateFlag = criteria.getPredicates().get("updateFlag");
			String vehicleNo =  CommonUtility.deNull(criteria.getPredicates().get("vehicleNo")).toUpperCase();

			// BEGIN added by Maksym JCMS Smart CR 6.10 FPT, 2016-01-15
			String cargoDes = criteria.getPredicates().get("cargoDes");
			// END added by Maksym JCMS Smart CR 6.10

			if ("toUpdate".equals(updateFlag)) {
				request.setAttribute("edo", edo);
				map.put("dnNbr", dnNbr);
				map.put("transType", transType);
				map.put("printView", printView);
				map.put("searchcrg", searchcrg);
				map.put("tesn_nbr", tesn_nbr);

				map.put("flag", flag);
				map.put("status", status);

				map.put("vehicleNo", vehicleNo);

				map.put("cargoDes", cargoDes);

				map.put("request", "DNVehicleNoUpdate");
			} else if ("Update".equals(updateFlag)) {

				String new_vehicleNo = criteria.getPredicates().get("new_vehicleNo").toUpperCase().trim();

				if (!outwardShutoutCargoEdoService.isValidVehicleNumber(new_vehicleNo, companyCode)) {
					throw new BusinessException("M1000001");
				}

				outwardShutoutCargoEdoService.updateVehicleNo(dnNbr, new_vehicleNo);
				log.info("Writing from DNUpdateHandler  -- end of updateFlag = Update ... ");

				map.put("request", "dnDetail");

			}
		} catch (BusinessException be) {
			log.info("Exception dnUpdate: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception dnUpdate : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: dnUpdate result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.ops.dnua.dn --> dnDetailHandler
	@PostMapping(value = "/dnDetail")
	public ResponseEntity<?> dnDetail(HttpServletRequest request) throws BusinessException, FileNotFoundException, JRException, MalformedURLException {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("dnDetail : START " + criteria.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String s = userId;

			String s2 = "";
			s2 = CommonUtility.deNull(criteria.getPredicates().get("printView"));
			String s3 = CommonUtility.deNull(criteria.getPredicates().get("flag"));
			String s4 = "";
			s4 = CommonUtility.deNull(criteria.getPredicates().get("searchcrg"));
			String s5 = "";
			s5 = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr"));
			List<EdoValueObjectOps> vector = new ArrayList<EdoValueObjectOps>();
			List<EdoValueObjectOps> vector1 = new ArrayList<EdoValueObjectOps>();
			//
			boolean select = outwardShutoutCargoEdoService
					.chktesnJpJp(CommonUtility.deNull(criteria.getPredicates().get("edo")).toString());
			map.put("select", "" + select);

			// Added by VietNguyen DPE on 20/03/12014 for edo remark link start
			map.put("dnList", CommonUtility.deNull(criteria.getPredicates().get("dnList")));
			// Added by VietNguyen DPE on 20/03/12014 for edo remark link end

			String dd = CommonUtility.deNull(criteria.getPredicates().get("dnNbr"));
			log.info("dnNbr: " + dd);
			//
			if (s3.equals("Y")) {
				// amend by Zhenguo Deng(harbor) 02/08/2011 : START
				if (s4.equals("SL")) {
					vector = outwardShutoutCargoEdoService.fetchShutoutDNDetail(
							CommonUtility.deNull(criteria.getPredicates().get("edo")),
							CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
					vector1 = outwardShutoutCargoEdoService
							.getVechDetails(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
				} else {
					vector = outwardShutoutCargoEdoService.fetchDNDetail(
							CommonUtility.deNull(criteria.getPredicates().get("edo")),
							CommonUtility.deNull(criteria.getPredicates().get("dnNbr")),
							CommonUtility.deNull(criteria.getPredicates().get("status")), s4, s5);
					vector1 = outwardShutoutCargoEdoService
							.getVechDetails(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
				}
				// amend by Zhenguo Deng(harbor) 02/08/2011 : END
				// Check EdoESN whether it associate with cntr
				if (outwardShutoutCargoEdoService
						.checkESNCntr(CommonUtility.deNull(criteria.getPredicates().get("edo")))) {
					// get cntr from DN_DETAILS table
					String cntrNo = outwardShutoutCargoEdoService
							.getCntrNo(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
					map.put("cntrNo", cntrNo);
				}
			} else {
				// amend by Zhenguo Deng(harbor) 18/07/2011 :START
				if (CommonUtility.deNull(criteria.getPredicates().get("addDnParam")) != null) {
					map.put("addDnParam", "Y");
					vector = outwardShutoutCargoEdoService.fetchShutoutDNCreateDetail(
							CommonUtility.deNull(criteria.getPredicates().get("edo")),
							CommonUtility.deNull(criteria.getPredicates().get("status")), s4, s5);
				} else {

					vector = outwardShutoutCargoEdoService.fetchDNCreateDetail(
							CommonUtility.deNull(criteria.getPredicates().get("edo")),
							CommonUtility.deNull(criteria.getPredicates().get("status")), s4, s5);
					if (vector != null) {
						log.info("fetchDNCreateDetail(): " + vector.size());
					} else {
						log.info("fetchDNCreateDetail(): null vector");
					}
				}
				// amend by Zhenguo Deng(harbor) 18/07/2011 :END
			}
			if (s2 != null && !s2.equals("") && s2.equals("print")) {

				List<ChargeableBillValueObject> arraylist = outwardShutoutCargoEdoService
						.getGBBillCharge(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")), "DN");
				map.put("billarrlist", arraylist);
				String s6 = "";
				String s7 = "";
				String s8 = "";
				String s9 = "";
				String s10 = "";
				String s11 = "";
				double d = 0.0D;
				double d1 = 0.0D;
				double d2 = 0.0D;
				double d3 = 0.0D;
				double d4 = 0.0D;
				double d5 = 0.0D;
				double d6 = 0.0D;
				double d7 = 0.0D;
				double d8 = 0.0D;
				String s12 = "";
				String s13 = "";
				String s14 = "";
				String s15 = "";
				String s16 = "";
				String s17 = "";
				String s18 = "";
				double d9 = 0.0D;
				double d10 = 0.0D;
				double d11 = 0.0D;
				String s19 = "";
				String s20 = "";
				String s21 = "";
				double d12 = 0.0D;
				double d13 = 0.0D;
				double d14 = 0.0D;
				String s22 = "";
				double d15 = 0.0D;
				double d16 = 0.0D;
				double d17 = 0.0D;
				double d18 = 0.0D;
				double d19 = 0.0D;
				log.info("s15: " + s15);
				log.info("s16: " + s16);
				for (int i = 0; i < arraylist.size(); i++) {
					ChargeableBillValueObject chargeablebillvalueobject = null;
					chargeablebillvalueobject = (ChargeableBillValueObject) arraylist.get(i);

					if (i == 0) {
						s6 = chargeablebillvalueobject.getTariffCd();
						s9 = chargeablebillvalueobject.getTariffDesc();
						d6 = chargeablebillvalueobject.getNbrOtherUnit();
						d = chargeablebillvalueobject.getUnitRate();
						d3 = chargeablebillvalueobject.getTotalChargeAmt() + chargeablebillvalueobject.getGstAmt();
						d15 = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990")) {
							s12 = "CASH";
						} else {
							s12 = chargeablebillvalueobject.getAcctNbr();
						}
					}
					if (i == 1) {
						s7 = chargeablebillvalueobject.getTariffCd();
						s10 = chargeablebillvalueobject.getTariffDesc();
						d7 = chargeablebillvalueobject.getNbrOtherUnit();
						d1 = chargeablebillvalueobject.getUnitRate();
						d4 = chargeablebillvalueobject.getTotalChargeAmt() + chargeablebillvalueobject.getGstAmt();
						d16 = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990")) {
							s13 = "CASH";
						} else {
							s13 = chargeablebillvalueobject.getAcctNbr();
						}
					}
					if (i == 2) {
						s8 = chargeablebillvalueobject.getTariffCd();
						s11 = chargeablebillvalueobject.getTariffDesc();
						d8 = chargeablebillvalueobject.getNbrOtherUnit();
						d2 = chargeablebillvalueobject.getUnitRate();
						d5 = chargeablebillvalueobject.getTotalChargeAmt() + chargeablebillvalueobject.getGstAmt();
						d17 = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990")) {
							s14 = "CASH";
						} else {
							s14 = chargeablebillvalueobject.getAcctNbr();
						}
					}
					if (i == 3) {
						s17 = chargeablebillvalueobject.getTariffCd();
						s18 = chargeablebillvalueobject.getTariffDesc();
						d11 = chargeablebillvalueobject.getNbrOtherUnit();
						d9 = chargeablebillvalueobject.getUnitRate();
						d10 = chargeablebillvalueobject.getTotalChargeAmt() + chargeablebillvalueobject.getGstAmt();
						d18 = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990")) {
							s19 = "CASH";
						} else {
							s19 = chargeablebillvalueobject.getAcctNbr();
						}
					}
					if (i == 4) {
						s20 = chargeablebillvalueobject.getTariffCd();
						s21 = chargeablebillvalueobject.getTariffDesc();
						d14 = chargeablebillvalueobject.getNbrOtherUnit();
						d12 = chargeablebillvalueobject.getUnitRate();
						d13 = chargeablebillvalueobject.getTotalChargeAmt() + chargeablebillvalueobject.getGstAmt();
						d19 = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990")) {
							s22 = "CASH";
						} else {
							s22 = chargeablebillvalueobject.getAcctNbr();
						}
					}
				}

				EdoValueObjectOps edovalueobject = new EdoValueObjectOps();
				edovalueobject = (EdoValueObjectOps) vector.get(0);
				String s23 = edovalueobject.getAcctNo();
				outwardShutoutCargoEdoService
						.purgetemptableDN(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
				s16 = outwardShutoutCargoEdoService.insertTempDNPrintOut(
						CommonUtility.deNull(criteria.getPredicates().get("edo")),
						CommonUtility.deNull(criteria.getPredicates().get("dnNbr")),
						CommonUtility.deNull(criteria.getPredicates().get("status")), s4, s5);
				s15 = outwardShutoutCargoEdoService.insertTempBill(
						CommonUtility.deNull(criteria.getPredicates().get("dnNbr")), s6, s9, d6, d, d3, s12, s7, s10,
						d7, d1, d4, s13, s8, s11, d8, d2, d5, s14, s, s23, s17, s18, d11, d9, d10, s19, s20, s21, d14,
						d12, d13, s22, d15, d16, d17, d18, d19);

				/*
				 * ReportPrintingBeanHome reportprintingbeanhome = (ReportPrintingBeanHome)
				 * ejbhomefactory.lookUpHome("ReportPrintingBean"); ReportPrintingBean
				 * reportprintingbean = reportprintingbeanhome.create(); ReportValueObject
				 * reportvalueobject = new ReportValueObject(); String s24 = "DNReport.rpt";
				 * reportvalueobject.setReportFileName(s24); String s25 =
				 * CommonUtility.deNull(criteria.getPredicates().get("printername"); String s26
				 * = s25; reportvalueobject.setPrinterName(s26);
				 * reportvalueobject.setReportPageSize("a4");
				 * reportvalueobject.addStringPrompt(CommonUtility.deNull(criteria.getPredicates
				 * ().get("dnNbr")); boolean flag =
				 * reportprintingbean.printReport(reportvalueobject); if (flag) {
				 * log.info("Report DN Printed ..............>>>>>>>>>>>>>>>>>>>>>>"); }
				 */

//				String pdfDir = printingBeanPdf + '/';
				
				String jasperName = "DNReport.jrxml";
				log.info("jasperName = " + jasperName );

				// add by zhengguo deng on 10/8/2011
				if (s4.equals("SL")) {
					jasperName = "ShutoutDNReport.jrxml";
				}
				// add end

				// Set input parameter
				String dnNbr = (String) CommonUtility.deNull(criteria.getPredicates().get("dnNbr"));
				log.info("dnNbr = " + dnNbr);
				/*
				 * Map parameters = new HashMap(); parameters.put("DnNbr", dnNbr); //
				 */
				Map<String, Object> parameters = new HashMap<String, Object>();
				String tempFolder = temporaryReportWorkingDir;
				if (tempFolder == null || "".equalsIgnoreCase(tempFolder)) {
					tempFolder = "/jrpapp1/wrk/backup/temp/pdf";
				}

				String imageFilename = tempFolder + "/barcode_" + dnNbr + ".png";
				log.info("gen barcode: " + imageFilename);
				// BarcodeImageUtil.getInstance().genBarcodeImage(imageFilename, dnNbr);
				generateCode39Writer(imageFilename, dnNbr);
				parameters.put("DnNbr", dnNbr);
				parameters.put("barcodeImage", imageFilename);

				if (log.isInfoEnabled()) {
					log.info("param: " + parameters);
					log.info("dnNbr: " + dnNbr);
					log.info("imgFile: " + imageFilename);
				}

				// Stream to Jasper file
				
				String fileNameJasper = jasperName;
				
				log.info("gen the jasper report with data");
			
				List<ShutoutEdoDnReport> records = outwardShutoutCargoEdoService.getdnReportDetails(dnNbr);
				ShutoutEdoDnReport item = null;
				
				Iterator<ShutoutEdoDnReport> it = records.iterator();
				while (it.hasNext()) {
					item =  it.next();
				}
				
				log.info("item: " + item);
				
    			JasperPrint jasperPrint = outwardShutoutCargoEdoService.getJasperPrint(fileNameJasper, parameters, dnNbr, records);
    			
    			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
    			Date date = new Date();
    			String fileName = FilenameUtils.normalize(printingBeanPdf + "/DN_"+dateTimeFormat.format(date)+".pdf");
    			log.info("=== Printing: "+fileName);
    			JasperExportManager.exportReportToPdfFile(jasperPrint,fileName);
    			log.info("=== Print ok");
    			
    			//System.out.println("DN Before  JasperPrintManager..............");
    			//JasperPrintManager.printReport(jasperPrint, false);
    			//System.out.println("DN After  JasperPrintManager..............");
    			
    			//Select Printer to printing
    			String printerNm = (String) CommonUtility.deNull(criteria.getPredicates().get("printername"));
    			log.info("DN printername ------------> " + printerNm);
    			PrinterName printerName = new PrinterName(printerNm, null);
    			
    			PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
    			MediaSizeName mediaSizeName = MediaSize.findMedia(8.27F,11.69F,MediaPrintableArea.INCH);
    			printRequestAttributeSet.add(mediaSizeName);
    			printRequestAttributeSet.add(new Copies(1));
    			
				PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		        printServiceAttributeSet.add(printerName);

		        log.info(jasperPrint.getPages().size());
				JRPrintServiceExporter exporter = new JRPrintServiceExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
				configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
				configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
				configuration.setDisplayPageDialog(false);
				configuration.setDisplayPrintDialog(false);
				exporter.setConfiguration(configuration);
				exporter.exportReport();
				FileInputStream st = null;
				
				if (!new File(fileName).exists()) {
					log.info("filepath validation failed! - " + fileName);
					throw new BusinessException("filepath validation failed!");
				}
				
				st = new FileInputStream(fileName);		
				return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
						.body(new InputStreamResource(st));
				
			}

			// added by Tatang on 17 Apr 2008 - begin
			CashSalesValueObject csvo = outwardShutoutCargoEdoService
					.getCashSales(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
			map.put("cashsale", csvo);

			String machineId = outwardShutoutCargoEdoService.getMachineID(csvo.getCash_receipt_nbr());
			map.put("machineId", machineId);

			log.info("EDO CashSales Type : " + csvo.getCsType());

			String paymentMode = outwardShutoutCargoEdoService.getCashSalesPaymentCode(csvo.getCsType());
			map.put("paymentMode", paymentMode);

			String nets_refId = outwardShutoutCargoEdoService.getNETSRefID(csvo.getCash_receipt_nbr());
			map.put("nets_refId", nets_refId);

			// added by Tatang on 17 Apr 2008 - end

			map.put("dnDetail", vector);
			map.put("vechDetail", vector1);

			// Added by ZanFeng start on 30/01/2011
			if (StringUtils.isEmpty(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")))) {
				map.put("exitFlag", "FALSE");
			} else {
				boolean exitFlag = outwardShutoutCargoEdoService
						.checkVehicleExit(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
				if (exitFlag) {
					map.put("exitFlag", "TRUE");
				} else {
					map.put("exitFlag", "FALSE");
				}
			}

			map.put("status", CommonUtility.deNull(criteria.getPredicates().get("status")));
			// End

			if (s2 != null && !s2.equals("") && s2.equals("print")) {

				log.info("inside print in dndetail handler");
			} else {
				// Added by SONLT---------------------------------------------
				List<String[]> cntrArr = new ArrayList<String[]>();
				cntrArr = outwardShutoutCargoEdoService
						.getCntrNbr(CommonUtility.deNull(criteria.getPredicates().get("edo")));
				if (cntrArr != null && cntrArr.size() > 0) {
					map.put("cntrArr", cntrArr);
				}
				// End--------------------------------------------------------

			}

		} catch (BusinessException be) {
			log.info("Exception dnDetail: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception dnDetail : ", e);
			if(e.getMessage().contains("print") || e.getMessage().contains("Jasper")) {
				errorMessage = e.getMessage();			
			} else {
				errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get("M4201");
			}
			
		} finally {
			if (errorMessage != null && errorMessage.contains("Jasper")) {
				return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/html"))
						.body(null);
			} else if (errorMessage != null && errorMessage.contains("print")) {
				return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/xml"))
						.body(null);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("dnDetail : END " + result.toString());
		}

		return ResponseEntityUtil.success(result);
	}

	public static void generateCode39Writer(String barcodeText, String nbr) throws Exception {
		try {
			File outputfile = new File(barcodeText);
			BitMatrix bitMatrix = new MultiFormatWriter().encode(nbr, BarcodeFormat.CODE_128, 0, 0);
			BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
			ImageIO.write(bufferedImage, "png", outputfile);
		} catch (Exception e) {
			log.info("Exception generateCode39Writer : ", e);
			throw new BusinessException("M4201");
		}

	}
	// delegate.helper.gbms.cargo.shutoutCargo--> OutStandingListHandler
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/outStandingList")
	public ResponseEntity<?> OutStandingList(HttpServletRequest request) throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		TopsModel topsModel = new TopsModel();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("OutStandingList : START " + criteria.toString());

			List<EdoValueObjectCargo> vesselvoyagevector = new ArrayList<EdoValueObjectCargo>();
			List<EdoValueObjectCargo> vesselvoyagevector1 = new ArrayList<EdoValueObjectCargo>();
			java.lang.String strvarnbr = "";
			String isFetch = CommonUtility.deNull(criteria.getPredicates().get("isFetch"));
			String vesselName = "";
			String voyageNumber = "";
			if ((String) criteria.getPredicates().get("vesselName") != null) {
				vesselName = (CommonUtility.deNull(criteria.getPredicates().get("vesselName"))).toUpperCase().trim();
				voyageNumber = (CommonUtility.deNull(criteria.getPredicates().get("voyageNumber"))).toUpperCase()
						.trim();
			}
			String selectDropDown = CommonUtility.deNull(criteria.getPredicates().get("SelectDropDown"));
			map.put("selectDropDown", selectDropDown);
			if (vesselName == null || "".equalsIgnoreCase(vesselName)) {
				vesselName = CommonUtility.deNull(criteria.getPredicates().get("vslNm"));
				voyageNumber = CommonUtility.deNull(criteria.getPredicates().get("vslVoy"));
				map.remove("vslNm");
				map.remove("vslVoy");
			}

			map.put("vesselName", vesselName);
			map.put("voyageNumber", voyageNumber);
			map.put("isFetch", isFetch);

			if (criteria.getPredicates().get("varnbr") == null) {
				vesselvoyagevector = outwardShutoutCargoEdoService.getShutoutVesselVoyageNbrList();
				for (int i = 0; i < vesselvoyagevector.size(); i++) {
					EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
					edoValueObject = (EdoValueObjectCargo) vesselvoyagevector.get(i);
					topsModel.put(edoValueObject);
				}
			} else {
				if (!isFetch.equals("FETCH")) {
					strvarnbr = CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
					map.put("strvarnbr", strvarnbr);
				}
				List<EdoValueObjectCargo> edovesselvoyagevector = new ArrayList<EdoValueObjectCargo>();
				vesselvoyagevector = outwardShutoutCargoEdoService.getShutoutVesselVoyageNbrList();
				for (int i = 0; i < vesselvoyagevector.size(); i++) {
					EdoValueObjectCargo edoValueObject1 = new EdoValueObjectCargo();
					edoValueObject1 = (EdoValueObjectCargo) vesselvoyagevector.get(i);

					edovesselvoyagevector.add(edoValueObject1);
					// edovesselvoyagevector.addElement(edoValueObject1
					// .getVarNbr());
					// edovesselvoyagevector.addElement(edoValueObject1
					// .getVslNbr());
					// edovesselvoyagevector.addElement(edoValueObject1
					// .getOutVoyNbr());
				}
				vesselvoyagevector1 = outwardShutoutCargoEdoService.getShutoutVesselList(vesselName, voyageNumber);
				if (vesselvoyagevector1.size() != 0) {
					for (int i = 0; i < vesselvoyagevector1.size(); i++) {
						EdoValueObjectCargo edoValueObject1 = new EdoValueObjectCargo();
						edoValueObject1 = (EdoValueObjectCargo) vesselvoyagevector1.get(i);
						strvarnbr = edoValueObject1.getVarNbr();
					}

				} else {
					isFetch = "";
					map.put("isFetch", isFetch);
				}

				map.put("strvarnbr", strvarnbr);

				boolean checkVoyNumberStatus = outwardShutoutCargoEdoService.chkVslStat(strvarnbr);
				String cVNStatusStr = "FALSE";
				if (checkVoyNumberStatus == true) {
					cVNStatusStr = "TRUE";
				}
				map.put("cVNStatusStr", cVNStatusStr);

				map.put("edovesselvoyagevector", edovesselvoyagevector);
				EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
				int noRowsPerPage = 50;
				RecordPaging recPg = new RecordPaging();
				int numOfPage = 0;
				int curPage = 1;
				List<EdoValueObjectCargo> recs;
				boolean getInd = true;
				java.lang.String changelist = "";
				if (criteria.getPredicates().get("changelist") != null) {
					changelist = CommonUtility.deNull(criteria.getPredicates().get("changelist"));
				}

				if ((criteria.getPredicates().get("PageIndex") == null)
						|| ((criteria.getPredicates().get("PageIndex")).equals("null"))
						|| (changelist.equalsIgnoreCase("NEW"))) {
					List<EdoValueObjectCargo> edolistvector = outwardShutoutCargoEdoService.getOutStandingList(strvarnbr);
					if (edolistvector.size() > 0) {
						numOfPage = recPg.createRecordPagingCache("EdoList", edolistvector, noRowsPerPage);
						if (!((criteria.getPredicates().get("PageIndex") == null)
								|| ((criteria.getPredicates().get("PageIndex")).equals("null")))) {
							curPage = Integer.valueOf(criteria.getPredicates().get("PageIndex")).intValue();
							if (curPage > numOfPage) {
								curPage = numOfPage;
							}
						}
					} else {
						getInd = false;
					}
				} else {
					numOfPage = recPg.getNumOfPages("EdoList");
					curPage = Integer.valueOf(criteria.getPredicates().get("PageIndex")).intValue();
				}

				if (getInd) {
					recs = recPg.getRecordsPage("EdoList", curPage);

					int size = recs.size();
					int i = 0;
					for (i = 0; i < size; i++) {
						edoValueObject = (EdoValueObjectCargo) recs.get(i);
						if (edoValueObject != null)
							topsModel.put(edoValueObject);
					}

					map.put("totalPages", numOfPage + "");
					map.put("pageIndex", curPage + "");
				}
			}

			map.put("listData", topsModel);

		} catch (BusinessException be) {
			log.info("Exception OutStandingList: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception OutStandingList : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("OutStandingList : END " + result.toString());
		}

		return ResponseEntityUtil.success(result);
	}

	// delegate.helper.gbms.cargo.shutoutCargo --> ShutoutEdoAddHandler
	@PostMapping(value = "/shutoutEdoAdd")
	public ResponseEntity<?> ShutoutEdoAdd(HttpServletRequest request) throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("ShutoutEdoAdd : START " + criteria.toString());

			String varName = "";
			String varNo = "";
			// criteria.getPredicates().get("varnbr");
			if (!"".equalsIgnoreCase(criteria.getPredicates().get("vesselName"))
					|| criteria.getPredicates().get("vesselName") != null)
				varName = CommonUtility.deNull(criteria.getPredicates().get("vesselName"));
			if (!"".equalsIgnoreCase(criteria.getPredicates().get("voyageNumber"))
					|| criteria.getPredicates().get("voyageNumber") != null)
				varNo = CommonUtility.deNull(criteria.getPredicates().get("voyageNumber"));

			if (varName == null && !"".equalsIgnoreCase(varName) && criteria.getPredicates().get("vslNm") != null) {
				varName = CommonUtility.deNull(criteria.getPredicates().get("vslNm"));
				varNo = CommonUtility.deNull(criteria.getPredicates().get("vslVoy"));
			}
			map.put("varName", varName);
			map.put("varNo", varNo);
			String funParam = "ONE";
			String fun = "Add";

			if (criteria.getPredicates().get("strmode") != null)
				funParam = CommonUtility.deNull(criteria.getPredicates().get("strmode"));
			if (criteria.getPredicates().get("funmode") != null)
				fun = CommonUtility.deNull(criteria.getPredicates().get("funmode"));
			if (fun.equalsIgnoreCase("Add"))
				map.put("funmode", "Add");
			if (fun.equalsIgnoreCase("View"))
				map.put("funmode", "View");

			if (funParam.equalsIgnoreCase("ONE")) {
				String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vslnmvoynbr"));
				List<List<String>> esnNo = getEsnListByVvCd(vvCd);
				map.put("vvCd", vvCd);
				map.put("esnNoList", esnNo);
				map.put("strmode", "ONE");
			}
			if (funParam.equalsIgnoreCase("TWO")) {
				String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
				String esnAsnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				List<List<String>> esnNo = getEsnListByVvCd(vvCd);
				getEsnDetails(esnAsnNo, request, map);
				map.put("vvCd", vvCd);
				map.put("esnNoList", esnNo);
				map.put("strmode", "TWO");
			}
			if (funParam.equalsIgnoreCase("THREE")) {
				String adpNbr = "";
				String adpNm = "";
				if (criteria.getPredicates().get("adpNbr") != null)
					adpNbr = CommonUtility.deNull(criteria.getPredicates().get("adpNbr"));
				if (criteria.getPredicates().get("adpNm") != null)
					adpNm = CommonUtility.deNull(criteria.getPredicates().get("adpNm"));
				String companyName = outwardShutoutCargoEdoService.getCompanyName(adpNbr);
				map.put("adpnbr", adpNbr);
				if (adpNm.equalsIgnoreCase("") & companyName.equalsIgnoreCase("")) {
					errorMessage = "Enter Authorized Delivery Party Name";
				} else if (companyName.equalsIgnoreCase("")) {
					map.put("adpnmstatus", "NEW");
					map.put("adpnm", adpNm);
				} else {
					map.put("adpnmstatus", "OLD");
					map.put("adpnm", companyName);
				}
				List<EdoJpBilling> arraylist = new ArrayList<EdoJpBilling>();
				String vvCd = "";
				if (criteria.getPredicates().get("vvCd") != null)
					vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));

				String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				arraylist = outwardShutoutCargoEdoService.getEdoJpBillingNbr(adpNbr, companyCode, vvCd);
				map.put("jpbnbrarraylist", arraylist);
				map.put("strmode", "THREE");
				String edoPkgs = CommonUtility.deNull(criteria.getPredicates().get("edoPkgs"));
				String edoPkgsWt = CommonUtility.deNull(criteria.getPredicates().get("edoPkgsWt"));
				String edoPkgsVol = CommonUtility.deNull(criteria.getPredicates().get("edoPkgsVol"));
				String esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				String deliveryto = CommonUtility.deNull(criteria.getPredicates().get("deliveryto"));
				map.put("vvCd", vvCd);
				map.put("esnNo", esnNo);
				map.put("edoPkgs", edoPkgs);
				map.put("edoPkgsWt", edoPkgsWt);
				map.put("edoPkgsVol", edoPkgsVol);
				map.put("deliveryto", deliveryto);
			}
			if (funParam.equalsIgnoreCase("FOUR")) {
				String deliveryto = CommonUtility.deNull(criteria.getPredicates().get("deliveryto"));
				String adpNbr = CommonUtility.deNull(criteria.getPredicates().get("adpnbr"));
				String adpNm = CommonUtility.deNull(criteria.getPredicates().get("adpnm"));
				String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
				String adpnmstatus = CommonUtility.deNull(criteria.getPredicates().get("adpnmstatus"));
				String adpCustCd = "";
				if (adpnmstatus.equals("OLD")) {
					adpCustCd = outwardShutoutCargoEdoService.getCustomerNbr(adpNbr);
				}
				String edoPkgs = CommonUtility.deNull(criteria.getPredicates().get("edoPkgs"));
				String edoPkgsWt = CommonUtility.deNull(criteria.getPredicates().get("edoPkgsWt"));
				String edoPkgsVol = CommonUtility.deNull(criteria.getPredicates().get("edoPkgsVol"));
				String jpbnbr = CommonUtility.deNull(criteria.getPredicates().get("jpbnbr"));
				String type = "";
				String esnAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				if (jpbnbr.equals("cash")) {
					type = "C";
				} else {
					type = "A";
				}

				String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
				String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				EdoValueObjectCargo edo = new EdoValueObjectCargo();
				edo.setVarNbr(vvCd);
				edo.setEdoNbrPkgs(edoPkgs);
				edo.setNomWeight(edoPkgsWt);
				edo.setNomVolume(edoPkgsVol);
				edo.setEsnAsnNbr(esnAsnNbr);
				edo.setAcctNbr(jpbnbr);
				edo.setPkgTypeCd(type);
				edo.setConsNm(companyCode);
				edo.setAdpNbr(adpNbr);
				edo.setDeliveryTo(deliveryto);
				edo.setAdpNm(adpNm);
				edo.setAdpCustCd(adpCustCd);
				String edoNo = outwardShutoutCargoEdoService.insertShutoutEdoForDPE(edo, userId);
				map.put("edoNo", edoNo);
				map.put("vvCd", vvCd);
				map.put("strmode", "FOUR");
			}

		} catch (BusinessException be) {
			log.info("Exception ShutoutEdoAdd: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception ShutoutEdoAdd : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result.setErrors(mapError);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("ShutoutEdoAdd : END " + result.toString());
		}

		return ResponseEntityUtil.success(result);
	}

	// delegate.helper.gbms.cargo.shutoutCargo --> ShutoutEdoAmendHandler
	@PostMapping(value = "/shutoutEdoAmend")
	public ResponseEntity<?> ShutoutEdoAmend(HttpServletRequest request) throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("shutoutEdoAmend : START " + criteria.toString());

			String funParam = CommonUtility.deNull(criteria.getPredicates().get("strmode"));

			if (funParam.equalsIgnoreCase("ONE")) {
				String edoAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
				String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
				List<List<String>> esnNo = getEsnListByVvCd(vvCd);
				map.put("vvCd", vvCd);
				map.put("esnNoList", esnNo);
				List<EdoValueObjectCargo> vector = new ArrayList<EdoValueObjectCargo>();
				if (criteria.getPredicates().get("esnNo") != null) {
					String esnAsnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
					map.put("esnNo", esnAsnNo);
					vector = outwardShutoutCargoEdoService.getShutoutAddDetail(esnAsnNo);
				} else {
					vector = outwardShutoutCargoEdoService.getShutoutEdoDetail(edoAsnNbr);
				}
				EdoValueObjectCargo edoValueObject = (EdoValueObjectCargo) vector.get(0);
				map.put("edoValueObject", edoValueObject);
				map.put("edoasnnbr", edoAsnNbr);
				map.put("strmode", "ONE");
			}
			if (funParam.equalsIgnoreCase("TWO")) {
				String adpNbr = "";
				String adpNm = "";
				if (criteria.getPredicates().get("adpNbr") != null)
					adpNbr = CommonUtility.deNull(criteria.getPredicates().get("adpNbr"));
				if (criteria.getPredicates().get("adpNm") != null)
					adpNm = CommonUtility.deNull(criteria.getPredicates().get("adpNm"));
				String companyName = outwardShutoutCargoEdoService.getCompanyName(adpNbr);
				map.put("adpnbr", adpNbr);
				if (adpNm.equalsIgnoreCase("") & companyName.equalsIgnoreCase("")) {
					errorMessage = "Enter Authorized Delivery Party Name";
				} else if (companyName.equalsIgnoreCase("")) {
					map.put("adpnmstatus", "NEW");
					map.put("adpnm", adpNm);
				} else {
					map.put("adpnmstatus", "OLD");
					map.put("adpnm", companyName);
				}
				List<EdoJpBilling> arraylist = new ArrayList<EdoJpBilling>();
				String vvCd = "";
				if (criteria.getPredicates().get("vvCd") != null)
					vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));

				String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				;
				arraylist = outwardShutoutCargoEdoService.getEdoJpBillingNbr(adpNbr, companyCode, vvCd);
				map.put("jpbnbrarraylist", arraylist);
				String edoPkgs = CommonUtility.deNull(criteria.getPredicates().get("edoPkgs"));
				String edoPkgsWt = CommonUtility.deNull(criteria.getPredicates().get("edoPkgsWt"));
				String edoPkgsVol = CommonUtility.deNull(criteria.getPredicates().get("edoPkgsVol"));
				String esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				String deliveryto = CommonUtility.deNull(criteria.getPredicates().get("deliveryto"));
				String edoasnnbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
				map.put("edoasnnbr", edoasnnbr);
				map.put("vvCd", vvCd);
				map.put("esnNo", esnNo);
				map.put("edoPkgs", edoPkgs);
				map.put("edoPkgsWt", edoPkgsWt);
				map.put("edoPkgsVol", edoPkgsVol);
				map.put("deliveryto", deliveryto);
				map.put("strmode", "TWO");
			}
			if (funParam.equalsIgnoreCase("THREE")) {
				String edoasnnbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
				map.put("edoasnnbr", edoasnnbr);
				String deliveryto = CommonUtility.deNull(criteria.getPredicates().get("deliveryto"));
				String adpNbr = CommonUtility.deNull(criteria.getPredicates().get("adpnbr"));
				String adpNm = CommonUtility.deNull(criteria.getPredicates().get("adpnm"));
				String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
				map.put("vvCd", vvCd);
				String adpnmstatus = CommonUtility.deNull(criteria.getPredicates().get("adpnmstatus"));
				String adpCustCd = "";
				if (adpnmstatus.equals("OLD")) {
					adpCustCd = outwardShutoutCargoEdoService.getCustomerNbr(adpNbr);
				}
				String edoPkgs = CommonUtility.deNull(criteria.getPredicates().get("edoPkgs"));
				String edoPkgsWt = CommonUtility.deNull(criteria.getPredicates().get("edoPkgsWt"));
				String edoPkgsVol = CommonUtility.deNull(criteria.getPredicates().get("edoPkgsVol"));
				String jpbnbr = CommonUtility.deNull(criteria.getPredicates().get("jpbnbr"));
				String type = "";
				String esnAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				if (jpbnbr.equals("cash")) {
					type = "C";
				} else {
					type = "A";
				}

				String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
				String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				EdoValueObjectCargo edo = new EdoValueObjectCargo();
				edo.setEdoAsnNbr(edoasnnbr);
				edo.setVarNbr(vvCd);
				edo.setEdoNbrPkgs(edoPkgs);
				edo.setNomWeight(edoPkgsWt);
				edo.setNomVolume(edoPkgsVol);
				edo.setEsnAsnNbr(esnAsnNbr);
				edo.setAcctNbr(jpbnbr);
				edo.setPkgTypeCd(type);
				edo.setConsNm(companyCode);
				edo.setAdpNbr(adpNbr);
				edo.setDeliveryTo(deliveryto);
				edo.setAdpNm(adpNm);
				edo.setAdpCustCd(adpCustCd);
				String edoNo = outwardShutoutCargoEdoService.updateShutoutEdo(edo, userId);
				map.put("edoNo", edoNo);
				map.put("strmode", "THREE");
			}
			if (funParam.equalsIgnoreCase("OPENWHIND")) {
				String edoasnnbr = "";
				if (criteria.getPredicates().get("edoasnnbr") != null)
					edoasnnbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
				List<String> vector = outwardShutoutCargoEdoService.getWHIndicator(edoasnnbr);
				String s11 = (String) vector.get(0);
				String s15 = (String) vector.get(1);
				String s21 = (String) vector.get(2);
				String s25 = (String) vector.get(3);
				map.put("edoasnnbr", edoasnnbr);
				map.put("whind", s11);
				map.put("whappnbr", s15);
				map.put("remarks", s21);
				map.put("nodays", s25);
				map.put("strmode", "OPENWHIND");
			}
			if (funParam.equalsIgnoreCase("UPDATEWHIND")) {
				String edoasnnbr = "";
				String whind = "";
				String whappnbr = "";
				String remarks = "";
				String nodays = "";
				if (criteria.getPredicates().get("edoasnnbr") != null)
					edoasnnbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
				if (criteria.getPredicates().get("whind") != null)
					whind = CommonUtility.deNull(criteria.getPredicates().get("whind"));
				if (criteria.getPredicates().get("whappnbr") != null)
					whappnbr = CommonUtility.deNull(criteria.getPredicates().get("whappnbr"));
				if (criteria.getPredicates().get("remarks") != null)
					remarks = CommonUtility.deNull(criteria.getPredicates().get("remarks"));
				if (criteria.getPredicates().get("nodays") != null)
					nodays = CommonUtility.deNull(criteria.getPredicates().get("nodays"));

				String loginId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
				outwardShutoutCargoEdoService.updateWHIndicator(edoasnnbr, whind, whappnbr, remarks, nodays, loginId);

				List<EdoValueObjectCargo> vector = outwardShutoutCargoEdoService.getShutoutEdoDetail(edoasnnbr);
				if (vector.size() == 0) {
					throw new BusinessException("M20811");
				} else {
					EdoValueObjectCargo edoValueObject = (EdoValueObjectCargo) vector.get(0);
					map.put("edoValueObject", edoValueObject);
					map.put("frommode", "SEARCH");
				}
				map.put("funParam", "TWO");
				map.put("strmode", "SEARCH");
			}

		} catch (BusinessException be) {
			log.info("Exception shutoutEdoAmend: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception shutoutEdoAmend : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result.setErrors(mapError);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("shutoutEdoAmend : END " + result.toString());
		}

		return ResponseEntityUtil.success(result);
	}

	public void getEsnDetails(String esnAsnNo, HttpServletRequest request, Map<String, Object> map)
			throws BusinessException {
		try {
			log.info("START: getEsnDetails Start Obj "+" esnAsnNo:"+CommonUtility.deNull(esnAsnNo) +" request:"+request +" map:"+map );
			List<EdoValueObjectCargo> v = new ArrayList<EdoValueObjectCargo>();
			v = outwardShutoutCargoEdoService.getShutoutAddDetail(esnAsnNo);
			if (v.size() > 0) {
				EdoValueObjectCargo edoValueObject = (EdoValueObjectCargo) v.get(0);
				map.put("edoValueObject", edoValueObject);
			}
		} catch (Exception exception) {
			log.info("Exception: getEsnDetails ", exception);
		}
	}

	private List<List<String>> getEsnListByVvCd(String vvCd) throws BusinessException {
		List<List<String>> esnNo = new ArrayList<List<String>>();

		try {
			log.info("START: getEsnListByVvCd Start Obj "+" vvCd:"+CommonUtility.deNull(vvCd));
			esnNo = outwardShutoutCargoEdoService.getEsnList(vvCd);
		} catch (BusinessException e) {
			log.info("Exception: getEsnListByVvCd ", e);
		}
		return esnNo;
	}

	// delegate.helper.gbms.cargo.shutoutCargo --> ShutoutEdoDeleteHandler
	@PostMapping(value = "/shutoutEdoDelete")
	public ResponseEntity<?> ShutoutEdoDelete(HttpServletRequest request) throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("ShutoutEdoDelete : START " + criteria.toString());

			java.lang.String stredoasnnbr = "";
			if (criteria.getPredicates().get("edoasnnbr") != null) {
				stredoasnnbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
			}

			String UserId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String deletestatus = outwardShutoutCargoEdoService.deleteShutoutEdoDetails(stredoasnnbr, UserId);
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			map.put("vvCd", vvCd);
			map.put("edoNo", stredoasnnbr);
			map.put("strmode", "DELETE");
			map.put("deletestatus", deletestatus);

		} catch (BusinessException be) {
			log.info("Exception ShutoutEdoDelete: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception ShutoutEdoDelete : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("ShutoutEdoDelete : END " + result.toString());
		}

		return ResponseEntityUtil.success(result);
	}

	// delegate.helper.gbms.cargo.shutoutCargo --> ShutoutEdoSearchHandler
	@PostMapping(value = "/shutoutEdoSearch")
	public ResponseEntity<?> ShutoutEdoSearch(HttpServletRequest request) throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("ShutoutEdoSearch : START " + criteria.toString());

			String funParam = "ONE";
			funParam = CommonUtility.deNull(criteria.getPredicates().get("strmode"));
			if (funParam.equalsIgnoreCase("ONE")) {
				map.put("funParam", "ONE");
			}
			if (funParam.equalsIgnoreCase("TWO")) {
				String edoAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));

				List<EdoValueObjectCargo> vector = outwardShutoutCargoEdoService.getShutoutEdoDetail(edoAsnNbr);
				if (vector.size() == 0) {
					throw new BusinessException("M20811");
				} else {
					EdoValueObjectCargo edoValueObject = (EdoValueObjectCargo) vector.get(0);
					map.put("edoValueObject", edoValueObject);
					map.put("frommode", "SEARCH");
				}
				map.put("funParam", "TWO");
			}
			if (funParam.equalsIgnoreCase("THREE")) {
				String edoAsnNbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
				map.put("edo", edoAsnNbr);

				List<EdoValueObjectCargo> vector = outwardShutoutCargoEdoService.getShutoutEdoDetail(edoAsnNbr);
				if (vector.size() == 0) {
					throw new BusinessException("M20811");
				} else {
					EdoValueObjectCargo edoValueObject = (EdoValueObjectCargo) vector.get(0);
					map.put("edoValueObject", edoValueObject);
					map.put("frommode", "SEARCH");
				}
				map.put("funParam", "THREE");
				String status = "";
				if (CommonUtility.deNull(criteria.getPredicates().get("status")) != null) {
					status = CommonUtility.deNull(criteria.getPredicates().get("status"));
				}
				;
				map.put("status", status);
				List<EdoValueObjectOps> dnVector = outwardShutoutCargoEdoService.fetchShutoutDNList(edoAsnNbr);
				map.put("dnVector", dnVector);
				String showTop = "";
				if (CommonUtility.deNull(criteria.getPredicates().get("showTop")) != null) {
					showTop = CommonUtility.deNull(criteria.getPredicates().get("showTop"));
					map.put("showTop", showTop);
				}
			}

		} catch (BusinessException be) {
			log.info("Exception ShutoutEdoSearch: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception ShutoutEdoSearch : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("ShutoutEdoSearch : END " + result.toString());
		}

		return ResponseEntityUtil.success(result);
	}

	// delegate.helper.gbms.cargo.shutoutCargo -->
	// ShutoutEdoVesselVoyageNbrListHandler
	@PostMapping(value = "/shutoutEdoVesselVoyageNbrList")
	public ResponseEntity<?> ShutoutEdoVesselVoyageNbrList(HttpServletRequest request) throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();
		Map<String, Object> mapError = new HashMap<>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("ShutoutEdoVesselVoyageNbrList : START " + criteria.toString());

			List<EdoValueObjectCargo> vesselvoyagevector = new ArrayList<>();
			List<EdoValueObjectCargo> vesselvoyagevector1 = new ArrayList<>();
			String strvarnbr = "";
			String isFetch = CommonUtility.deNull(criteria.getPredicates().get("isFetch"));
			String vesselName = "";
			String voyageNumber = "";
			if (CommonUtility.deNull(criteria.getPredicates().get("vesselName")) != null) {
				vesselName = (CommonUtility.deNull(criteria.getPredicates().get("vesselName"))).toUpperCase().trim();
				voyageNumber = (CommonUtility.deNull(criteria.getPredicates().get("voyageNumber"))).toUpperCase()
						.trim();
			}
			String selectDropDown = CommonUtility.deNull(criteria.getPredicates().get("SelectDropDown"));
			map.put("selectDropDown", selectDropDown);
			if (vesselName == null || "".equalsIgnoreCase(vesselName)) {
				vesselName = CommonUtility.deNull(criteria.getPredicates().get("vslNm"));
				voyageNumber = CommonUtility.deNull(criteria.getPredicates().get("vslVoy"));
				map.remove("vslNm");
				map.remove("vslVoy");
			}

			map.put("vesselName", vesselName);
			map.put("voyageNumber", voyageNumber);
			map.put("isFetch", isFetch);

			if (criteria.getPredicates().get("varnbr") == null) {
				vesselvoyagevector = outwardShutoutCargoEdoService.getShutoutVesselVoyageNbrList();
				for (int i = 0; i < vesselvoyagevector.size(); i++) {
					EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
					edoValueObject = (EdoValueObjectCargo) vesselvoyagevector.get(i);
					topsModel.put(edoValueObject);
				}
			} else {
				if (!isFetch.equals("FETCH")) {
					strvarnbr = CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
					map.put("strvarnbr", strvarnbr);
				}
				List<EdoValueObjectCargo> edovesselvoyagevector = new ArrayList<EdoValueObjectCargo>();
				vesselvoyagevector = outwardShutoutCargoEdoService.getShutoutVesselVoyageNbrList();
				for (int i = 0; i < vesselvoyagevector.size(); i++) {
					EdoValueObjectCargo edoValueObject1 = new EdoValueObjectCargo();
					edoValueObject1 = (EdoValueObjectCargo) vesselvoyagevector.get(i);

					edovesselvoyagevector.add(edoValueObject1);

					// TODO
					// edovesselvoyagevector.addElement(edoValueObject1
					// .getVarNbr());
					// edovesselvoyagevector.addElement(edoValueObject1
					// .getVslNbr());
					// edovesselvoyagevector.addElement(edoValueObject1
					// .getOutVoyNbr());
				}
				vesselvoyagevector1 = outwardShutoutCargoEdoService.getShutoutVesselList(vesselName, voyageNumber);
				if (vesselvoyagevector1.size() != 0) {
					for (int i = 0; i < vesselvoyagevector1.size(); i++) {
						EdoValueObjectCargo edoValueObject1 = new EdoValueObjectCargo();
						edoValueObject1 = (EdoValueObjectCargo) vesselvoyagevector1.get(i);
						strvarnbr = edoValueObject1.getVarNbr();
					}

				} else {
					isFetch = "";
					map.put("isFetch", isFetch);
				}

				map.put("strvarnbr", strvarnbr);

				// ManifestEJBHome mfthome = (ManifestEJBHome) homeFactory
				// .lookUpHome("ManifestEJB");
				// ManifestEJBRemote mftrem = mfthome.create();
				//
				// boolean checkVoyNumberStatus = mftrem.chkVslStat(strvarnbr);
				// String cVNStatusStr = "FALSE";
				// if (checkVoyNumberStatus == true) {
				// cVNStatusStr = "TRUE";
				// }
				// paramMap.put("cVNStatusStr", cVNStatusStr);

				map.put("edovesselvoyagevector", edovesselvoyagevector);
				
				EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
				int total = 0;
				String changelist = "";
				if (criteria.getPredicates().get("changelist") != null) {
					changelist = CommonUtility.deNull(criteria.getPredicates().get("changelist"));
				}

				if ((criteria.getPredicates().get("PageIndex") == null)
						|| (((String) criteria.getPredicates().get("PageIndex")).equals("null"))
						|| (changelist.equalsIgnoreCase("NEW"))) {
					tableResult = outwardShutoutCargoEdoService.getShutoutEdoList(strvarnbr, criteria);
					total = tableResult.getData().getTotal();
					map.put("total", total);
					for (int i = 0; i < total; i++) {
						edoValueObject = (EdoValueObjectCargo) tableResult.getData().getListData().getTopsModel().get(i);
						topsModel.put(edoValueObject);
					}
				}

			}

			map.put("listData", topsModel);

		} catch (BusinessException be) {
			log.info("Exception ShutoutEdoVesselVoyageNbrList: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception ShutoutEdoVesselVoyageNbrList : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
                result = new Result();
                result.setErrors(mapError);
                result.setData(map);
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("ShutoutEdoVesselVoyageNbrList : END " + result.toString());
		}

		return ResponseEntityUtil.success(result);
	}
}
