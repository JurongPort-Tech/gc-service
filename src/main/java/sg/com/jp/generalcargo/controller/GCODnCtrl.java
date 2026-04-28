package sg.com.jp.generalcargo.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import io.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EdoVO;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.domain.EmailValueObject;
import sg.com.jp.generalcargo.domain.GBWareHouseAplnVO;
import sg.com.jp.generalcargo.domain.GcOpsDnReport;
import sg.com.jp.generalcargo.domain.LANPrinterVO;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.SmartInterfaceInputVO;
import sg.com.jp.generalcargo.domain.Sms;
import sg.com.jp.generalcargo.restclient.SmartServiceRestClient;
import sg.com.jp.generalcargo.service.GeneralCargoDnService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;
import sg.com.jp.generalcargo.util.SmartInterfaceConstants;
import sg.com.jp.generalcargo.config.PrinterProperties;

@CrossOrigin
@RestController
@RequestMapping(value = GCODnCtrl.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GCODnCtrl {

	public static final String ENDPOINT = "gc/gcOps";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(GCODnCtrl.class);

	@Autowired
	private GeneralCargoDnService dnService;

	@Value("${ReportPrintingBean.directory.report}")
	private String reportPrintingBeanReport;

	@Value("${ReportPrintingBean.directory.pdf}")
	private String reportPrintingBeanPdf;

	@Value("${ReportPrintingBean.directory.pdf}")
	private String temporaryReportWorkingDir;

	@Value("${jp.common.notificationProperties.emailFromAddress}")
	private String emailWarehouseFrom;

	@Value("${jp.common.notificationProperties.VM.DNCreated.subject}")
	private String emailCreateDnSubject;

	@Value("${jp.common.notificationProperties.VM.DNCreated.body}")
	private String emailCreateDnBody;
	
	@Value("${PRINTERENDPOINT.URI}")
	private String printerUrl;
	
	@Value("${smart.rest.client.url}")
	private String smartBaseUrl;
	
	@Autowired
    private PrinterProperties printerProperties;

	@Autowired
	private SmartServiceRestClient smartServiceRestClient;

	// delegate.helper.gbms.ops.dnua.dn-->dnDetailHandler

	@ApiOperation(value = "dnDetail", response = String.class)
	@PostMapping(value = "/dnDetail")
	public ResponseEntity<?> dnDetail(HttpServletRequest request) throws BusinessException {
		Map<String, Object> map = new HashMap<>();
		Result result = new Result();
		Resource resource = null;
		errorMessage = null;
		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** dnDetail Start criteria :" + criteria.toString());

			String s = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String s2 = "";
			s2 = criteria.getPredicates().get("printView");
			String s3 = criteria.getPredicates().get("flag");
			String s4 = "";
			s4 = criteria.getPredicates().get("searchcrg");
			String s5 = "";
			s5 = criteria.getPredicates().get("tesn_nbr");
			List<EdoVO> vector = new ArrayList<EdoVO>();
			List<EdoVO> vector1 = new ArrayList<EdoVO>();
			//
			boolean select = dnService.chktesnJpJp(criteria.getPredicates().get("edo").toString());
			map.put("select", "" + select);

			map.put("dnList", criteria.getPredicates().get("dnList"));

			String dd = criteria.getPredicates().get("dnNbr");
			log.info("dnNbr: " + dd);
			//
			if (s3.equals("Y")) {

				if (s4.equals("SL")) {
					vector = dnService.fetchShutoutDNDetail(criteria.getPredicates().get("edo"),
							criteria.getPredicates().get("dnNbr"));
					vector1 = dnService.getVechDetails(criteria.getPredicates().get("dnNbr"));
				} else {
					vector = dnService.fetchDNDetail(criteria.getPredicates().get("edo"),
							criteria.getPredicates().get("dnNbr"), criteria.getPredicates().get("status"), s4, s5);
					vector1 = dnService.getVechDetails(criteria.getPredicates().get("dnNbr"));
				}

				if (dnService.checkESNCntr(criteria.getPredicates().get("edo"))) {

					String cntrNo = dnService.getCntrNo(criteria.getPredicates().get("dnNbr"));
					map.put("cntrNo", cntrNo);
				}
			} else {

				if (criteria.getPredicates().get("addDnParam") != null) {
					map.put("addDnParam", "Y");
					vector = dnService.fetchShutoutDNCreateDetail(criteria.getPredicates().get("edo"),
							criteria.getPredicates().get("status"), s4, s5);
				} else {

					vector = dnService.fetchDNCreateDetail(criteria.getPredicates().get("edo"),
							criteria.getPredicates().get("status"), s4, s5);
					if (vector != null) {
						log.info("fetchDNCreateDetail(): " + vector.size());
					} else {
						log.info("fetchDNCreateDetail(): null vector");
					}
				}

			}
			if (s2 != null && !s2.equals("") && s2.equals("print")) {

				List<ChargeableBillValueObject> arraylist = dnService
						.getGBBillCharge(criteria.getPredicates().get("dnNbr"), "DN");
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

				EdoVO edovalueobject = new EdoVO();
				edovalueobject = (EdoVO) vector.get(0);
				String s23 = edovalueobject.getAcctNo();
				dnService.purgetemptableDN(criteria.getPredicates().get("dnNbr"));
				s16 = dnService.insertTempDNPrintOut(criteria.getPredicates().get("edo"),
						criteria.getPredicates().get("dnNbr"), criteria.getPredicates().get("status"), s4, s5);
				s15 = dnService.insertTempBill(criteria.getPredicates().get("dnNbr"), s6, s9, d6, d, d3, s12, s7, s10,
						d7, d1, d4, s13, s8, s11, d8, d2, d5, s14, s, s23, s17, s18, d11, d9, d10, s19, s20, s21, d14,
						d12, d13, s22, d15, d16, d17, d18, d19);

				log.info("insertTempDNPrintOut : " + s16);
				log.info("insertTempBill : " + s15);

				/*
				 * ReportPrintingBeanHome reportprintingbeanhome = (ReportPrintingBeanHome)
				 * ejbhomefactory.lookUpHome("ReportPrintingBean"); ReportPrintingBean
				 * reportprintingbean = reportprintingbeanhome.create(); ReportValueObject
				 * reportvalueobject = new ReportValueObject(); String s24 = "DNReport.rpt";
				 * reportvalueobject.setReportFileName(s24); String s25 =
				 * criteria.getPredicates().get("printername"); String s26 = s25;
				 * reportvalueobject.setPrinterName(s26);
				 * reportvalueobject.setReportPageSize("a4");
				 * reportvalueobject.addStringPrompt(criteria.getPredicates().get("dnNbr"));
				 * boolean flag = reportprintingbean.printReport(reportvalueobject); if (flag) {
				 * log.info("Report DN Printed ..............>>>>>>>>>>>>>>>>>>>>>>"); }
				 */

				String reportDir = reportPrintingBeanReport + '/';
				String pdfDir = reportPrintingBeanPdf + '/';

				String fileNameJasper = "DNReport.jrxml";
				//String fileNameJasper = jasperName.split(".jasper")[0];
				log.info("jasperName = " + fileNameJasper + " " + reportDir + " " + pdfDir);

				// add by zhengguo deng on 10/8/2011
				if (s4.equals("SL")) {
					fileNameJasper = "ShutoutDNReport.jrxml";
				}
				// add end

				// Set input parameter
				String dnNbr = (String) criteria.getPredicates().get("dnNbr");
				log.info("dnNbr = " + dnNbr);
				/*
				 * Map parameters = new HashMap(); parameters.put("DnNbr", dnNbr); //
				 */
				HashMap<String, Object> parameters = new HashMap<String, Object>();
				String tempFolder = temporaryReportWorkingDir;
				if (tempFolder == null || "".equalsIgnoreCase(tempFolder)) {
					tempFolder = "/jrpapp1/wrk/backup/temp/pdf";
				}

				String imageFilename = tempFolder + "/barcode_" + dnNbr + ".png";
				log.info("gen barcode: " + imageFilename);

				generateCode39Writer(imageFilename, dnNbr);
				parameters.put("dnNo", dnNbr);
				parameters.put("barcodeImage", imageFilename);

//    			BarcodeImageUtil.getInstance().genBarcodeImage(imageFilename, dnNbr);
				parameters.put("DnNbr", dnNbr);
				parameters.put("PaymentMode", "");
				parameters.put("barcodeImage", imageFilename);
				
				log.info("param: " + parameters);
				log.info("dnNbr: " + dnNbr);
				log.info("imgFile: " + imageFilename);
				

				// Stream to Jasper file
				// java.io.InputStream is = new java.io.FileInputStream(reportDir + jasperName);

				log.info("getting gbms conn");
				// Create connection
				// Connection conn = DbConnectionFactory.getInstance().getConnection("GBMS");
				List<GcOpsDnReport> dnListPrint = dnService.getDNPrintJasper(dnNbr);

				JasperPrint jasperPrint = dnService.getJasperPrint(fileNameJasper, parameters, dnNbr, dnListPrint);

				SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
				Date date = new Date();
				String fileName = "DN_" + dateTimeFormat.format(date) + ".pdf";
				String filePath = pdfDir + fileName;
				log.info("=== Printing: " + filePath);
				JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
				log.info("=== Print ok");

				// log.info("DN Before JasperPrintManager..............");
				// JasperPrintManager.printReport(jasperPrint, false);
				// log.info("DN After JasperPrintManager..............");

				// Select Printer to printing
				String printerNm = criteria.getPredicates().get("printername").trim();
				log.info("DN printername ------------> " + printerNm);
				String resolvedPrinter = printerProperties.getMappedPrinter(printerNm);
				log.info("Resolved Printer: " + resolvedPrinter);

				String pdfFilePath = fileName;
		        String printerPath = resolvedPrinter;
		        String encodedPDF = encodeFileToBase64(filePath);
        
		        LANPrinterVO printerVO = new LANPrinterVO();
		        printerVO.setFilePath(pdfFilePath);
		        printerVO.setEncodedString(encodedPDF);
		        printerVO.setPrinterPath(printerPath);
		        String jsonPayload = printerVO.toString();
		        log.info("Payload converted to JSON : " + jsonPayload);
		        
		        RestTemplate restTemplate = new RestTemplate();
	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_JSON);
	            HttpEntity<String> entity = new HttpEntity<String>(jsonPayload, headers);
	            
		        String PRINTER_ENDPOINT = printerUrl;
		        log.info("REST CALL: endpoint " + PRINTER_ENDPOINT  + "  " +  "BODY  " +  entity.toString());
		        
		        try {
		        	 ResponseEntity<String> response = restTemplate.postForEntity(PRINTER_ENDPOINT, entity, String.class);
		        	 log.info("Response Status: " + response.getStatusCode());
				} catch (HttpClientErrorException e) {
					log.info("Exception sendPdfToPrinter : " +  e);
				} catch (HttpStatusCodeException httpEx) {
					log.info("Printer service responded with error code : " + httpEx.getRawStatusCode()  + " " + httpEx.getResponseBodyAsString());
				}
		        
				/*PrinterName printerName = new PrinterName(printerNm, null);

				PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
				MediaSizeName mediaSizeName = MediaSize.findMedia(8.27F, 11.69F, MediaPrintableArea.INCH);
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
				exporter.exportReport();*/
				
				Path p1 = Paths.get(filePath);
				resource = new UrlResource(p1.toUri());

				return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
						.header(HttpHeaders.CONTENT_DISPOSITION,
								"attachment; filename=\"" + resource.getFilename() + "\"")
						.body(resource);
			}

			// added by Tatang on 17 Apr 2008 - begin

			CashSalesValueObject csvo = dnService.getCashSales(criteria.getPredicates().get("dnNbr"));
			map.put("cashsale", csvo);

			String machineId = dnService.getMachineID(csvo.getCashReceiptNbr());
			map.put("machineId", machineId);

			log.info("EDO CashSales Type : " + csvo.getCsType());

			String paymentMode = dnService.getCashSalesPaymentCode(csvo.getCsType());
			map.put("paymentMode", paymentMode);

			String nets_refId = dnService.getNETSRefID(csvo.getCashReceiptNbr());
			map.put("nets_refId", nets_refId);

			// added by Tatang on 17 Apr 2008 - end

			map.put("dnDetail", vector);
			map.put("vechDetail", vector1);

			// Added by ZanFeng start on 30/01/2011
			if (StringUtils.isEmpty(criteria.getPredicates().get("dnNbr"))) {
				map.put("exitFlag", "FALSE");
			} else {
				boolean exitFlag = dnService.checkVehicleExit(criteria.getPredicates().get("dnNbr"));
				if (exitFlag) {
					map.put("exitFlag", "TRUE");
				} else {
					map.put("exitFlag", "FALSE");
				}
			}

			map.put("status", criteria.getPredicates().get("status"));
			// End

			if (s2 != null && !s2.equals("") && s2.equals("print")) {
				map.put("request", "dnDetail");
				log.info("inside print in dndetail handler");
			} else {
				// Added by SONLT---------------------------------------------
				List<String[]> cntrArr = new ArrayList<String[]>();
				cntrArr = dnService.getCntrNbr(criteria.getPredicates().get("edo"));
				if (cntrArr != null && cntrArr.size() > 0) {
					map.put("cntrArr", cntrArr);
				}
				// End--------------------------------------------------------
				map.put("request", "dnDetail");
			}
		} catch (BusinessException e) {
			log.error("Exception dnDetail : ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception: dnDetail", e);
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
				log.info("END: dnDetail result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	public static void generateCode39Writer(String barcodeText, String nbr) throws Exception {
		try {
			File outputfile = new File(barcodeText);
			BitMatrix bitMatrix = new MultiFormatWriter().encode(nbr, BarcodeFormat.CODE_128, 0, 0);
			BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
			ImageIO.write(bufferedImage, "png", outputfile);
		} catch (Exception e) {
			log.error("Exception generateCode39Writer : ", e);
			throw new BusinessException("M4201");
		}

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
			boolean chk = dnService.checkESNCntr(criteria.getPredicates().get("edo"));
			if (chk) {
				map.put("esncntr", "YES");
			} else {
				map.put("esncntr", "NO");
			}
			// End--------------------------------------------------------

			String userId = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			map.put("userId", userId);

			if (s7 != null && s7.equals("Y")) {
				String s14;
				String cntrNbr = CommonUtility.deNull(criteria.getPredicates().get("cntrNbr"));
				String cntrSeq = "";
				if (!"".equals(CommonUtility.deNull(cntrNbr))) {
					cntrSeq = dnService.getCntrSeq(cntrNbr);
				}

				// MCConsulting: if the 1st DN is cancelled then update subsequent active DN nbr
				// in the billable events table
				String transactionType = criteria.getPredicates().get("transType");
				boolean canCancelDn = true;
				if ((transactionType != null && transactionType.equals("L"))
						|| (searchCrg != null && searchCrg.equals("SL"))) {
					// system should not allow to cancel DN after the next day of DN creation

					String dnNbr = criteria.getPredicates().get("dnnbr");

					canCancelDn = dnService.checkCancelDN(dnNbr);

					if (canCancelDn) {
						log.info("**Updating the first DN in billable events with subsequent Active DN");
						dnService.checkAndUpdateFirstDN(criteria.getPredicates().get("edo"), dnNbr);

					} else {
						errorMessage = ConstantUtil.cancel_dn_001;
					}

				}

				// MCConsulting - check if the DN is allowed to cancel first before calling
				// cancelBillableCharges function
				if (canCancelDn) {
					if (dnService.cancelBillableCharges(criteria.getPredicates().get("dnnbr"),
							"DN")) {
						// Begin TungVH
						String dnFirst = "";
						if (chk && !"".equals(CommonUtility.deNull(cntrSeq))) {
							dnFirst = dnService.getDnCntrFirst(cntrSeq, cntrNbr);
						}
						// End TungVH

						// Amend by Zhenguo Deng(harbor) 03/08/2011 : START
						map.put("searchCrg", searchCrg);
						log.info("Cancel DN details :searchCrg :" + searchCrg);
						if (searchCrg.equals("SL")) {
							s14 = dnService.cancelShutoutDN(criteria.getPredicates().get("edo"),
									criteria.getPredicates().get("dnnbr"), s5);
							
							log.info("s14: " + s14);
						} else {
							s14 = dnService.cancelDN(criteria.getPredicates().get("edo"),
									criteria.getPredicates().get("dnnbr"), s5,
									criteria.getPredicates().get("transType"), searchCrg, s11);
							if (searchCrg.trim().equals("T")) {
								log.info("Cancel DN details :searchCrg :" + searchCrg);

								int nbrPkg = Integer.parseInt(criteria.getPredicates().get("tranQty"));
								String transDttm = criteria.getPredicates().get("transDttm");
								String transType = dnService.checkTransType(s11);
								String dpNm = criteria.getPredicates().get("dpNm");
								String dpIcNbr = criteria.getPredicates().get("dpIcNbr");
								log.info("Cancel DN details :nbrPkg :" + nbrPkg);
								log.info("Cancel DN details :transType :" + transType);
								log.info("Cancel DN details :dpNm :" + dpNm);
								log.info("Cancel DN details :dpIcNbr :" + dpIcNbr);
								String uaNbr = dnService.getUaNbr(s11, nbrPkg, transDttm, dpNm,
										dpIcNbr);
								log.info("Cancel DN details :uaNbr :" + uaNbr);
								if (uaNbr != null && !uaNbr.equals("")) {
									if (dnService.cancelBillableCharges(uaNbr, "UA")) {
										dnService.cancelUA(uaNbr, s11, transType, s5,
												Integer.toString(nbrPkg));
										log.info("UA Cancelled!");
									}
								}
							}
							// Added by Babatunde on Dec., 2013 : End

							// VietNguyen added to implement logic cancel DN -> should be check Stuff cntr

							if (chk && !"".equals(CommonUtility.deNull(cntrSeq))) {
								// check dn balance
								boolean countBalance = dnService.countDNBalance(cntrNbr);
								log.info("Cancel DN details :and do check update stuff cntr : ?? :" + countBalance);
								if (!countBalance) {
									dnService.updateCntrStatus(cntrSeq, s5);
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
							remDn = dnService.checkFirstDN(dnNbr, cntrNbr);
							if (remDn <= 0) {
								String newCatCd = dnService.getNewCatCd(cntrSeq);
								dnService.changeStatusCntr(cntrSeq, s5, newCatCd);
							}
							if (dnNbr.equals(dnFirst)) {
								// insert USTF into cntr_txn
								dnService.cancel1stDn(cntrSeq, cntrNbr, s5);
								// Timestamp dttmSecond = null;
								// dttmSecond = dnService.getDnSecond(cntrSeq, cntrNbr, s5);
								// if (dttmSecond == null) {
								// have not any DN
								// dnService.changeStatusCntr(cntrSeq, s5);
								// } else {
								// At least one DN
								// dnService.changeFirstDN(cntrSeq, cntrNbr, s5,
								// dttmSecond);
								// }
							}
							// End TungVH
							long declrWt = Long.parseLong(criteria.getPredicates().get("declrWt"));
							int declrPkgs = Integer.parseInt(criteria.getPredicates().get("declrPkgs"));
							int tranQty = Integer.parseInt(criteria.getPredicates().get("tranQty"));
							long weight = (tranQty * declrWt) / declrPkgs;
							dnService.updateWeight(cntrSeq, weight, s5, "SUB");
						}
						// END SONLT
						// Start added for SMART CR by FPT on 24-Jan-2014
						log.info("searchCrg=" + searchCrg);
						String refType = "", refNbr = "";
						boolean callSmartInterface = false;
						if ("LT".equals(searchCrg) || "T".equals(searchCrg)) {
							if ("T".equals(searchCrg) && StringUtils.isNotBlank(s11)) {
								refNbr = s11;

								String chkTesnJpPsa_nbr = dnService.chktesnJpPsa_nbr(s11);

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
					vector = dnService.getShutoutEdoDetail(criteria.getPredicates().get("edo"));
					vector1 = dnService.fetchShutoutDNList(criteria.getPredicates().get("edo"));
				} else {
					vector = new ArrayList<EdoValueObjectOps>();
					vector = dnService.fetchEdoDetails(criteria.getPredicates().get("edo"),
							searchCrg, s11);
					String listAllDn = criteria.getPredicates().get("listAllDn");
					if ("TRUE".equalsIgnoreCase(listAllDn)) {
						vector1 = dnService.fetchDNList(criteria.getPredicates().get("edo"), "ALL",
								s11);
					} else
						vector1 = dnService.fetchDNList(criteria.getPredicates().get("edo"),
								searchCrg, s11);
				}
				// Amend by Zhenguo Deng(harbor) 03/08/2011 : END

			} else {
				 vector = new ArrayList<EdoValueObjectOps>();
				vector = dnService.fetchEdoDetails(criteria.getPredicates().get("edo"), searchCrg,
						s11);
				String listAllDn = criteria.getPredicates().get("listAllDn");
				if ("TRUE".equalsIgnoreCase(listAllDn)) {
					vector1 = dnService.fetchDNList(criteria.getPredicates().get("edo"), "ALL",
							s11);
				} else
					vector1 = dnService.fetchDNList(criteria.getPredicates().get("edo"), searchCrg,
							s11);

				// map.put("stuffSeqNbr",s11);

			}
			String s8 = dnService.chktesnJpJp_nbr(s11);
			String s9 = dnService.chktesnJpPsa_nbr(s11);
			// added by Vinayak on 05 Feb 2004
			boolean checkEdoStuff = false;
			if (searchCrg.trim().equals("T")) {
				checkEdoStuff = dnService.chkEDOStuffing(criteria.getPredicates().get("edo")); // vinayak
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
				csList = dnService.getCashSales(vector1);
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
			if (dnService.chktesnJpJp(criteria.getPredicates().get("edo"))) {
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
			map.put("total", vector.size());
			map.put("dnList", vector1);

			subAdpVector = dnService.fetchSubAdpDetails(criteria.getPredicates().get("edo"));
			map.put("subAdpVector", subAdpVector);

			int spencialPkgs = dnService.getSpencialPackage(criteria.getPredicates().get("edo"));
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
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception dnEdoDetail : ", e);
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
				log.info("END: dnEdoDetail result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.ops.dnua.dn-->dnEdoSearchHandler

	@ApiOperation(value = "dnEdoSearch", response = String.class)
	@PostMapping(value = "/dnEdoSearch")
	public ResponseEntity<?> dnEdoSearch(HttpServletRequest request) throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		String searchcrg = "";
		String txtedo = "";

		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** dnEdoSearch Start criteria :" + criteria.toString());

			searchcrg = CommonUtility.deNull(criteria.getPredicates().get("searchcrg"));

			txtedo = CommonUtility.deNull(criteria.getPredicates().get("txtedo"));
			// Added by LongDh09::Start
			String showList = CommonUtility.deNull(criteria.getPredicates().get("showlist"));
			String isViewCargoDetail = CommonUtility.deNull(criteria.getPredicates().get("isViewCargoDetail"));
			log.info("dnEdoSearchHandler::showList = " + showList);
			log.info("dnEdoSearchHandler::isViewCargoDetail = " + isViewCargoDetail);

			String ccode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			List<EdoVO> vector1 = new ArrayList<EdoVO>();
			// Amended by HuJianPing on July 15 2011 : start
			String searchShutout = CommonUtility.deNull(criteria.getPredicates().get("searchShutout"));
			if (searchShutout != null && !"".equals(searchShutout)) {
				vector1 = dnService.fetchShutoutEdo(txtedo, ccode, searchcrg);
			} else {
				vector1 = dnService.fetchEdo(txtedo, ccode, searchcrg);
			}
			// Amended by HuJianPing on July 15 2011 : end
			String chktesnEdo = dnService.chktesnEdo(txtedo);
			String chkEdoNbr = dnService.chkEdoNbr(txtedo);
			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT") && txtedo.length() > 6) {
				boolean tesnjpjp = dnService.chktesnJpJp(txtedo);
				boolean tesnjppsa = dnService.chktesnJpPsa(txtedo);
				map.put("tesnjpjp", "".concat(String.valueOf(String.valueOf(tesnjpjp))));
				map.put("tesnjppsa", "".concat(String.valueOf(String.valueOf(tesnjppsa))));
			}
			if (txtedo.length() > 6) {
				map.put("chktesnEdo", "".concat(String.valueOf(String.valueOf(chktesnEdo))));
				map.put("chkEdoNbr", "".concat(String.valueOf(String.valueOf(chkEdoNbr))));
			}
			map.put("edovect", vector1);
			map.put("total", vector1.size());
			// Added by LongDh09::Start
			map.put("showList", showList);
			map.put("isViewCargoDetail", isViewCargoDetail);
			// Added by LongDh09::End
			map.put("request", "dnEdoSearch");
		} catch (BusinessException e) {
			log.error("Exception dnEdoSearch : ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception dnEdoSearch : ", e);
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
				log.info("END: dnEdoSearch result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.ops.dnua.dn-->dnUpdateHandler

	@ApiOperation(value = "dnUpdate", response = String.class)
	@PostMapping(value = "/dnUpdate")
	public ResponseEntity<?> dnUpdate(HttpServletRequest request) throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();

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
			String vehicleNo = criteria.getPredicates().get("vehicleNo").toUpperCase();

			// BEGIN added by Maksym JCMS Smart CR 6.10 FPT, 2016-01-15
			String cargoDes = criteria.getPredicates().get("cargoDes");
			// END added by Maksym JCMS Smart CR 6.10

			if ("toUpdate".equals(updateFlag)) {
				map.put("edo", edo);
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

				if (!dnService.isValidVehicleNumber(new_vehicleNo, companyCode)) {
					throw new BusinessException("M1000001");
				}

				dnService.updateVehicleNo(dnNbr, new_vehicleNo);
				log.info("Writing from DNUpdateHandler  -- end of updateFlag = Update ... ");

				map.put("request", "dnDetail");

			}
		} catch (BusinessException e) {
			log.error("Exception dnUpdate : ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception dnUpdate : ", e);
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
				log.info("END: dnUpdate result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.ops.dnua.dn-->dnCreateHandler

	@ApiOperation(value = "dnCreate", response = String.class)
	@PostMapping(value = "/dnCreate")
	public ResponseEntity<?> dnCreate(HttpServletRequest request) throws BusinessException {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();

		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** dnCreate Start criteria :" + criteria.toString());

			String s = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String s1 = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			List<EdoVO> vector = new ArrayList<EdoVO>();
			List<EdoVO> vector1 = new ArrayList<EdoVO>();
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
			 * s13 = criteria.getPredicates().get("veh2"); s14 =
			 * criteria.getPredicates().get("veh3"); s15 =
			 * criteria.getPredicates().get("veh4"); s16 =
			 * criteria.getPredicates().get("veh5");
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
				totalCusCd = dnService.getTotalCustCdByIcNumber(s10, s17);
				if (totalCusCd == 0) {
					log.info("Invalid IC number");
					throw new BusinessException("M100011");
				}
			}

			// start added by NS OCT 2022 to get truck records
			// if more than 1 record means truck haven go out yet, so prompt user error
			if (s18.equalsIgnoreCase("LT")) { // only for Local Cargo / Transhipment Cargo Delivered Locally
				int truckerOutNo = dnService.truckerOut(s3, s12);
				log.info("truckerOutNo : " + truckerOutNo);
				if (truckerOutNo > 0) {
					log.info("trucker haven't go out yet");
					throw new BusinessException(
							"One of the EDO DN tied to same vehicle is not yet truck out of JP. Please check");
				}
			}
			// end added

			// BEGIN FPT modify for Warehouse Management CR, 10/02/2014.

			String edoNbr = criteria.getPredicates().get("edoNbr");

			String errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get("M80008");
			log.info("errorMessage : " + errorMessage);

			if (!"Y".equalsIgnoreCase(CommonUtility.deNull(criteria.getPredicates().get("forceSubmit")))) {
				if (dnService.isExistWarehouseApplicationWithASNNubmer(edoNbr)) {
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
				List<GBWareHouseAplnVO> warehouseList = dnService.getWarehouseApplicationListByASNNubmer(edoNbr);
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

								dnService.sendMessage(vo);
							} catch (Exception e) {

								log.info(e);
							}
						}
					}
				}

				// force submit and void warehouse .
				dnService.voidWarehouseApplicationWithASNNubmer(edoNbr, s);

			}

			// END FPT modify for Warehouse Management CR, 10/02/2014.
			// amend by Zhenguo Deng(harbor) 01/08/2011 : START
			if (s18.equals("SL")) {
				// BEGIN added by Maksym JCMS Smart CR 6.2
				if (StringUtils.isNotBlank(cargoDes) && "Out of JP".equalsIgnoreCase(cargoDes.trim())) {
					if (!dnService.isValidVehicleNumber(s12, s1)) {
						throw new BusinessException("M1000001");
					}
				}
				// END added by Maksym JCMS Smart CR 6.2
				s2 = dnService.createShutoutDN(s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s, s17, s18, s19, cargoDes);

				// commented by hujun on 26/12/2011 ATB shouldn't affect the event logging
				// boolean flag = dnService.chkraiseCharge(s3);

				// if(flag)
				// {
				String dn_nbr = s2;
				String userId = s;
				String status = dnService.triggerShutoutCargoDN(dn_nbr, userId);
				map.put("updatestatus", status);
				// }
				// comment end

				vector = dnService.fetchShutoutDNDetail(s3, s2);
				vector1 = dnService.getVechDetails(s2);

				map.put("dnDetail", vector);
				map.put("vechDetail", vector1);
				map.put("dnNbr", s2);

				map.put("screen", "dnCreate");
				result.setData(map);
				result.setSuccess(true);
				log.info("END: dnCreate result: " + result.toString());
				return ResponseEntityUtil.success(result.toString());
			}
			// amend by Zhenguo Deng(harbor) 01/08/2011 : END

			try {

				/*
				 * s2 = dnService.createDN(s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14,
				 * s15, s16, s, s17, s18, s19);
				 */
				// ++ Changed by VietND - remove parameter veh2,veh3,veh4,veh5, add parameter
				// cargoDes
				// BEGIN added by Maksym JCMS Smart CR 6.10
				if (StringUtils.isNotBlank(cargoDes) && "Out of JP".equalsIgnoreCase(cargoDes.trim())) {
					if (!dnService.isValidVehicleNumber(s12, s1)) {
						throw new BusinessException("M1000001");
					}
				}
				// END added by Maksym JCMS Smart CR 6.10
				s2 = dnService.createDN(s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s, s17, s18, s19, cargoDes);
				// -- VietND
				// Added by Babatunde on Dec., 2013 : START
				// This is to check if EDO_ASN_NBR is in TESN_JP_JP and auto create UA if true
				boolean isJPJP = dnService.isTESN_JP_JP(s3, s19);
				if (isJPJP == true) {

					String uaNbr = dnService.createUA(s19, s4, s5, s6, s7, s8, s9, s10, s17, s11, s12, s);

					StringTokenizer stringtokenizer = new StringTokenizer(dnService.getUANbr(s19), "-");
					String ftrans = stringtokenizer.nextToken().trim();

					log.info("Need to Trigger CaB UA calling for create ? " + ftrans);

					log.info("Before Trigger CaB UA calling for create");

					String vvCd = dnService.getVcd(s19);

					String updatestatusUA = dnService.TriggerUa(uaNbr, s, vvCd);

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

				}
				// Added by Babatunde on Dec., 2013 : END

				// Added by SONLT
				if (dnService.checkESNCntr(s3)) {
					String cntrNo = "";
					String cntrSeq = "";
					String cntr = criteria.getPredicates().get("cntrNo");
					if ((cntr != null) && (!"".equals(cntr))) {
						String[] tmp = new String[2];
						tmp = cntr.split(",");
						cntrSeq = tmp[0].trim();
						cntrNo = tmp[1].trim();
						// Update container field for DN_DETAILS table
						dnService.updateDN(cntrNo, s2);
						// Get current Status of the container
						/* TungVH 29-JAN-2010 starts */
						long declrWt = (long) Double.parseDouble(criteria.getPredicates().get("declrWt"));
						log.info("-----------------declrWt----" + declrWt);
						/* TungVH 29-JAN-2010 ends */
						int declrPkgs = Integer.parseInt(criteria.getPredicates().get("declrPkgs"));
						long weight = (Integer.parseInt(s9) * declrWt) / declrPkgs;

						// Check first DN for each MOT container to capture time and weight
						int noOfdn = 0;
						noOfdn = dnService.checkFirstDN(s3, cntrNo);
						if (noOfdn <= 0) {
							// update status and time
							String newCatCd = dnService.getNewCatCd(cntrSeq);
							dnService.updateCntr(cntrSeq, cntrNo, s, newCatCd);

							// update weight
							dnService.updateWeight(cntrSeq, weight, s, "ADD");
						} else {
							// update weight
							dnService.updateWeight(cntrSeq, weight, s, "ADD");
						}
					}
				}
				// END

			} catch (BusinessException ex) {
				log.info("Exception : ", ex);
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
					String chkTesnJpJp_nbr = dnService.chktesnJpJp_nbr(s19);
					refNbr = s19;

					if ("Y".equals(chkTesnJpJp_nbr)) {
						// Is this TESN_JP_JP
						refType = "TESNJJ";
						callSmartInterface = true; // added SL-SMART-20171006-01 Fixed to release for J TO J case
					} else {
						String chkTesnJpPsa_nbr = dnService.chktesnJpPsa_nbr(s19);

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
					String url = smartBaseUrl + "/releaseStorageOccupancy";
					log.info("[releaseStorageOccupancy] Calling SMART service URL= " + url);
					smartServiceRestClient.releaseStorageOccupancy(inputObj);
				} catch (Exception ex) {
					log.error("Call SMART Interface releaseStorageOccupancy exception: DN number=" + s2);
					log.error("Exception: " + ex.getMessage());
				}
				log.info("After SMART interface calling");

			}
			// End added for SMART CR by FPT on 23-Jan-2014
			boolean isCntrCrgDn = dnService.chkCntrCrgDn(s2);// added by vani to chk cntr crg DN
			if (!isCntrCrgDn && !s2.equals(""))// if not cntr crg dn then only raise charges
			{
				boolean flag = dnService.chkraiseCharge(s3);

				if (flag) {
					String s20 = dnService.TriggerDN(s2, s);
					if (s20 != null && !s20.equals("") && s20.equalsIgnoreCase("FALSE"))
						map.put("updatestatus", "FALSE");
					else if (s20 != null && !s20.equals("") && s20.equalsIgnoreCase("TRUE"))
						map.put("updatestatus", "TRUE");
				}
			}
			if (!s2.equals("")) {
				vector = dnService.fetchDNDetail(s3, s2, s4, s18, s19);
				vector1 = dnService.getVechDetails(s2);
				map.put("dnDetail", vector);
				map.put("vechDetail", vector1);
				map.put("dnNbr", s2);
			} else {
				throw new BusinessException("M80007");
			}
		} catch (BusinessException e) {
			log.error("Exception dnCreate : ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception dnCreate : ", e);
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
				log.info("END: dnCreate result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
	
	public static String encodeFileToBase64(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileBytes);
    }

}
