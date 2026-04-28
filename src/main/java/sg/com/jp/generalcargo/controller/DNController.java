package sg.com.jp.generalcargo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DNCntrJasperReport;
import sg.com.jp.generalcargo.domain.EdoValueObjectContainerised;
import sg.com.jp.generalcargo.domain.ReportValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.service.DNService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = DNController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DNController {

	public static final String ENDPOINT = "gc/containerised/dn";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(DNController.class);

	@Autowired
	private DNService dnService;
	
	@Value("${ReportPrintingBean.directory.report}")
	private String directoryReport;

	@Value("${ReportPrintingBean.directory.pdf}")
	private String printingBeanPdf;
	
	// Region Start DnCreateCtrl
	// delegate.helper.gbms.containerised.dn ->dnCreateHandler
	@PostMapping(value = "/dnCreate")
	public ResponseEntity<?> DnCreate(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: DnCreate criteria:" + criteria.toString());
			String userid = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			List<EdoValueObjectContainerised> vector1 = new ArrayList<EdoValueObjectContainerised>();
			List<EdoValueObjectContainerised> vector2 = new ArrayList<EdoValueObjectContainerised>();
			String dnNbr = "";

			String edoNbr = "";
			String transtype = "";
			String edo_Nbr_Pkgs = "";
			String NomWt = "";
			String NomVol = "";
			String date_time = "";
			String transQty = "";
			String nric_no = "";
			String dpname = "";
			String cntrNum = "";
			String stuffDt = "";
			String veh1 = "";
			String veh2 = "";
			String veh3 = "";
			String veh4 = "";
			String veh5 = "";
			String icType = "";
			String searchcrg = "";
			searchcrg = CommonUtility.deNull(criteria.getPredicates().get("searchcrg"));
			String tesn_nbr = "";
			tesn_nbr = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr"));
			edoNbr = CommonUtility.deNull(criteria.getPredicates().get("edoNbr"));
			transtype = CommonUtility.deNull(criteria.getPredicates().get("transtype"));
			edo_Nbr_Pkgs = CommonUtility.deNull(criteria.getPredicates().get("edo_Nbr_Pkgs"));
			NomWt = CommonUtility.deNull(criteria.getPredicates().get("NomWt"));
			NomVol = CommonUtility.deNull(criteria.getPredicates().get("NomVol"));
			transQty = CommonUtility.deNull(criteria.getPredicates().get("transQty"));
			nric_no = CommonUtility.deNull(criteria.getPredicates().get("nric_no"));
			icType = CommonUtility.deNull(criteria.getPredicates().get("ictype"));
			dpname = CommonUtility.deNull(criteria.getPredicates().get("dpname"));
			cntrNum = CommonUtility.deNull(criteria.getPredicates().get("cntrNum"));
			stuffDt = CommonUtility.deNull(criteria.getPredicates().get("stuffingDate"));
			veh1 = CommonUtility.deNull(criteria.getPredicates().get("veh1"));
			veh2 = CommonUtility.deNull(criteria.getPredicates().get("veh2"));
			veh3 = CommonUtility.deNull(criteria.getPredicates().get("veh3"));
			veh4 = CommonUtility.deNull(criteria.getPredicates().get("veh4"));
			veh5 = CommonUtility.deNull(criteria.getPredicates().get("veh5"));
			date_time = CommonUtility.deNull(criteria.getPredicates().get("transDate"));

			dnNbr = dnService.createDN(edoNbr, transtype, edo_Nbr_Pkgs, NomWt, NomVol, date_time, transQty, nric_no,
					dpname, veh1, veh2, veh3, veh4, veh5, userid, icType, searchcrg, tesn_nbr, cntrNum, stuffDt);

			// added to trigger charges--25th July start
			boolean flag = dnService.chkraiseCharge(edoNbr);
			if (flag) {
				log.info("B4 transactionloggerejbremote");
				String s20 = dnService.TriggerDN(dnNbr, userid);
				log.info("A4 transactionloggerejbremote");
				if (s20 != null && !s20.equals("") && s20.equalsIgnoreCase("FALSE"))
					map.put("updatestatus", "FALSE");
				else if (s20 != null && !s20.equals("") && s20.equalsIgnoreCase("TRUE"))
					map.put("updatestatus", "TRUE");
			}
			// added to trigger charges--25th July end

			vector1 = dnService.fetchDNDetail(dnNbr, transtype, searchcrg, tesn_nbr);
			vector2 = dnService.getVechDetails(dnNbr);

			map.put("dnDetail", vector1);
			map.put("vechDetail", vector2);
			map.put("dnNbr", dnNbr);
			log.info("vector1---" + vector1.size());
		} catch (BusinessException e) {
			log.info("Exception DnCreate : ", e);
			errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception DnCreate : ", e);
			errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: DnCreate result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// Region End DnCreateCtrl

	// Region Start
	// delegate.helper.gbms.containerised.dn -->dnDetailHandler
	@PostMapping(value = "/dnDetail")
	public ResponseEntity<?> DnDetail(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
//		Resource resource = null;
		try {
			log.info("START: DnDetail criteria:" + criteria.toString());

			String login = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String viewPrint = "";
			viewPrint = CommonUtility.deNull(criteria.getPredicates().get("printView"));
			map.put("viewPrint",viewPrint);
			String flag = CommonUtility.deNull(criteria.getPredicates().get("flag"));
			map.put("flag",flag);
			String searchcrg = "";
			searchcrg = CommonUtility.deNull(criteria.getPredicates().get("searchcrg"));
			map.put("searchcrg",searchcrg);
			String tesn_nbr = "";
			tesn_nbr = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr"));
			map.put("tesn_nbr",tesn_nbr);
			String dnNbr = CommonUtility.deNull(criteria.getPredicates().get("dnNbr"));
			map.put("dnNbr",dnNbr);
			String edo = CommonUtility.deNull(criteria.getPredicates().get("edo"));
			map.put("edo",edo);
			String status = CommonUtility.deNull(criteria.getPredicates().get("status"));
			map.put("status",status);
			String noOfBkgs = CommonUtility.deNull(criteria.getPredicates().get("noOfBkgs"));
			map.put("noOfBkgs",noOfBkgs);
			String transtype = CommonUtility.deNull(criteria.getPredicates().get("noOfBkgs"));
			map.put("transtype",transtype);
			List<EdoValueObjectContainerised> vector1 = new ArrayList<EdoValueObjectContainerised>();
			List<EdoValueObjectContainerised> vector2 = new ArrayList<EdoValueObjectContainerised>();
			if (flag.equals("Y")) {
				vector1 = dnService.fetchDNDetail(dnNbr,status, searchcrg, tesn_nbr);
				vector2 = dnService.getVechDetails(dnNbr);
			} else
				vector1 = dnService.fetchDNCreateDetail(edo,status, searchcrg, tesn_nbr);

			if (viewPrint != null && !viewPrint.equals("") && viewPrint.equals("print")) {
				log.info("Before Trigger CaB DN calling for print details");

				List<ChargeableBillValueObject> arraylist = dnService.getGBBillCharge(dnNbr, "DN");
				log.info("After Trigger CaB DN called for print details");
				map.put("billarrlist", arraylist);

				String tarcdser = "";
				String tarcdwf = "";
				String tarcdsr = "";
				String tardescser = "";
				String tardescwf = "";
				String tardescsr = "";
				double urateser = 0.0;
				double uratewf = 0.0;
				double uratesr = 0.0;
				double totchrgamtser = 0.0;
				double totchrgamtwf = 0.0;
				double totchrgamtsr = 0.0;
				double billtonsser = 0.0;
				double billtonswf = 0.0;
				double billtonssr = 0.0;
				String actnbrser = "";
				String actnbrwf = "";
				String actnbrsr = "";

				String tarcdsr1 = "";
				String tardescsr1 = "";
				double uratesr1 = 0.0;
				double totchrgamtsr1 = 0.0;
				double billtonssr1 = 0.0;
				String actnbrsr1 = "";
				String tarcdsr2 = "";
				String tardescsr2 = "";
				double uratesr2 = 0.0;
				double totchrgamtsr2 = 0.0;
				double billtonssr2 = 0.0;
				String actnbrsr2 = "";

				double tunitser = 0.0;
				double tunitwhf = 0.0;
				double tunitsr = 0.0;
				double tunitstore = 0.0;
				double tunitserwhf = 0.0;

				for (int i = 0; i < arraylist.size(); i++) {
					ChargeableBillValueObject chargeablebillvalueobject = null;
					chargeablebillvalueobject = (ChargeableBillValueObject) arraylist.get(i);
					/*
					 * s15 = cntbill s6 = tarcdser s9 = tardescser d6 = billtonsser d = urateser d3
					 * = totchrgamtser s12 = actnbrser s7 = tarcdwf s10 = tardescwf d7 = billtonswf
					 * d1 = uratewf d4 = totchrgamtwf s13 = actnbrwf s8 = tarcdsr s11 = tardescsr d8
					 * = billtonssr d2 = uratesr d5 = totchrgamtsr s14 = actnbrsr s = login s23 =
					 * edo_act_nbr s17 = tarcdsr1 s18 = tardescsr1 d11 = billtonssr1 d9 = uratesr1
					 * d10 = totchrgamtsr1 s19 = actnbrsr1 s20 = tarcdsr2 s21 = tardescsr2 d14 =
					 * billtonssr2 d12 = uratesr2 d13 = totchrgamtsr2 s22 = actnbrsr2 d15 = tunitser
					 * d16 = tunitwhf d17 = tunitsr d18 = tunitstore d19 = tunitserwhf
					 */

					if (i == 0) {
						// s6 = chargeablebillvalueobject.getTariffCd();
						tarcdser = chargeablebillvalueobject.getTariffCd();
						// s9 = chargeablebillvalueobject.getTariffDesc();
						tardescser = chargeablebillvalueobject.getTariffDesc();
						// d6 = chargeablebillvalueobject.getNbrOtherUnit();
						billtonsser = chargeablebillvalueobject.getNbrOtherUnit();
						// d = chargeablebillvalueobject.getUnitRate();
						urateser = chargeablebillvalueobject.getUnitRate();
						// d3 = chargeablebillvalueobject.getTotalChargeAmt() +
						// chargeablebillvalueobject.getGstAmt();
						totchrgamtser = chargeablebillvalueobject.getTotalChargeAmt()
								+ chargeablebillvalueobject.getGstAmt();
						// d15 = chargeablebillvalueobject.getNbrTimeUnit();
						tunitser = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990"))
							actnbrser = "CASH";// s12 = "CASH";
						else
							actnbrser = chargeablebillvalueobject.getAcctNbr();
						// s12 = chargeablebillvalueobject.getAcctNbr();
					}
					if (i == 1) {
						// s7 = chargeablebillvalueobject.getTariffCd();
						tarcdwf = chargeablebillvalueobject.getTariffCd();
						// s10 = chargeablebillvalueobject.getTariffDesc();
						tardescwf = chargeablebillvalueobject.getTariffDesc();
						// d7 = chargeablebillvalueobject.getNbrOtherUnit();
						billtonswf = chargeablebillvalueobject.getNbrOtherUnit();
						// d1 = chargeablebillvalueobject.getUnitRate();
						uratewf = chargeablebillvalueobject.getUnitRate();
						// d4 = chargeablebillvalueobject.getTotalChargeAmt() +
						// chargeablebillvalueobject.getGstAmt();
						totchrgamtwf = chargeablebillvalueobject.getTotalChargeAmt()
								+ chargeablebillvalueobject.getGstAmt();
						// d16 = chargeablebillvalueobject.getNbrTimeUnit();
						tunitwhf = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990"))
							actnbrwf = "CASH";// s13 = "CASH";
						else
							actnbrwf = chargeablebillvalueobject.getAcctNbr();
						// s13 = chargeablebillvalueobject.getAcctNbr();
					}
					if (i == 2) {
						// s8 = chargeablebillvalueobject.getTariffCd();
						tarcdsr = chargeablebillvalueobject.getTariffCd();
						// s11 = chargeablebillvalueobject.getTariffDesc();
						tardescsr = chargeablebillvalueobject.getTariffDesc();
						// d8 = chargeablebillvalueobject.getNbrOtherUnit();
						billtonssr = chargeablebillvalueobject.getNbrOtherUnit();
						// d2 = chargeablebillvalueobject.getUnitRate();
						uratesr = chargeablebillvalueobject.getUnitRate();
						// d5 = chargeablebillvalueobject.getTotalChargeAmt() +
						// chargeablebillvalueobject.getGstAmt();
						totchrgamtsr = chargeablebillvalueobject.getTotalChargeAmt()
								+ chargeablebillvalueobject.getGstAmt();
						// d17 = chargeablebillvalueobject.getNbrTimeUnit();
						tunitsr = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990"))
							actnbrsr = "CASH";// s14 = "CASH";
						else
							actnbrsr = chargeablebillvalueobject.getAcctNbr();
						// s14 = chargeablebillvalueobject.getAcctNbr();
					}
					if (i == 3) {
						// s17 = chargeablebillvalueobject.getTariffCd();
						tarcdsr1 = chargeablebillvalueobject.getTariffCd();
						// s18 = chargeablebillvalueobject.getTariffDesc();
						tardescsr1 = chargeablebillvalueobject.getTariffDesc();
						// d11 = chargeablebillvalueobject.getNbrOtherUnit();
						billtonssr1 = chargeablebillvalueobject.getNbrOtherUnit();
						// d9 = chargeablebillvalueobject.getUnitRate();
						uratesr1 = chargeablebillvalueobject.getUnitRate();
						// d10 = chargeablebillvalueobject.getTotalChargeAmt() +
						// chargeablebillvalueobject.getGstAmt();
						totchrgamtsr1 = chargeablebillvalueobject.getTotalChargeAmt()
								+ chargeablebillvalueobject.getGstAmt();
						// d18 = chargeablebillvalueobject.getNbrTimeUnit();
						tunitstore = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990"))
							actnbrsr1 = "CASH";// s19 = "CASH";
						else
							actnbrsr1 = chargeablebillvalueobject.getAcctNbr();
					}
					if (i == 4) {
						// s20 = chargeablebillvalueobject.getTariffCd();
						tarcdsr2 = chargeablebillvalueobject.getTariffCd();
						// s21 = chargeablebillvalueobject.getTariffDesc();
						tardescsr2 = chargeablebillvalueobject.getTariffDesc();
						// d14 = chargeablebillvalueobject.getNbrOtherUnit();
						billtonssr2 = chargeablebillvalueobject.getNbrOtherUnit();
						// d12 = chargeablebillvalueobject.getUnitRate();
						uratesr2 = chargeablebillvalueobject.getUnitRate();
						// d13 = chargeablebillvalueobject.getTotalChargeAmt() +
						// chargeablebillvalueobject.getGstAmt();
						totchrgamtsr2 = chargeablebillvalueobject.getTotalChargeAmt()
								+ chargeablebillvalueobject.getGstAmt();
						// d19 = chargeablebillvalueobject.getNbrTimeUnit();
						tunitserwhf = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990"))
							actnbrsr2 = "CASH";// s22 = "CASH";
						else
							actnbrsr2 = chargeablebillvalueobject.getAcctNbr();
						// s22 = chargeablebillvalueobject.getAcctNbr();
					}
				}

				EdoValueObjectContainerised edoVo = new EdoValueObjectContainerised();
				edoVo = (EdoValueObjectContainerised) vector1.get(0);
				String edo_act_nbr = (String) edoVo.getAcctNo();

				dnService.purgetemptableDN(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
				dnService.insertTempDNPrintOut(dnNbr,
						CommonUtility.deNull(criteria.getPredicates().get("status")), searchcrg, tesn_nbr);
				dnService.insertTempBill(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")), tarcdser,
						tardescser, billtonsser, urateser, totchrgamtser, actnbrser, tarcdwf, tardescwf, billtonswf,
						uratewf, totchrgamtwf, actnbrwf, tarcdsr, tardescsr, billtonssr, uratesr, totchrgamtsr,
						actnbrsr, login, edo_act_nbr, tarcdsr1, tardescsr1, billtonssr1, uratesr1, totchrgamtsr1,
						actnbrsr1, tarcdsr2, tardescsr2, billtonssr2, uratesr2, totchrgamtsr2, actnbrsr2, tunitser,
						tunitwhf, tunitsr, tunitstore, tunitserwhf);
				
				ReportValueObject rvo = new ReportValueObject();
				String reportFilename = "CntrDNReport.jrxml";
				rvo.setReportFileName(reportFilename);
				String printerNm = (String) CommonUtility.deNull(criteria.getPredicates().get("printername"));
				rvo.setPrinterName(printerNm);

				
				List<DNCntrJasperReport>  records = dnService.getDnCntrJasperContent(dnNbr);
			
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("dnNbr", dnNbr);
    			JasperPrint jasperPrint = dnService.getJasperPrint(reportFilename, parameters, dnNbr, records);
    			
    			
    			String fileName = FilenameUtils.normalize(dnService.getPdfFileName(rvo,dnNbr));
    			log.info("=== Printing: "+fileName);
    			JasperExportManager.exportReportToPdfFile(jasperPrint,fileName);
    			log.info("=== Print ok");
    			
    			//System.out.println("DN Before  JasperPrintManager..............");
    			//JasperPrintManager.printReport(jasperPrint, false);
    			//System.out.println("DN After  JasperPrintManager..............");
    			
    			//Select Printer to printing
    			
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

			map.put("dnDetail", vector1);
			map.put("vechDetail", vector2);

			if (viewPrint != null && !viewPrint.equals("") && viewPrint.equals("print")) {
				 map.put("screen", "CntrViewPrint");
			} 

		} catch (BusinessException e) {
			log.info("Exception DnDetail : ", e);
			errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception DnDetail : ", e);
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
				log.info("END: DnDetail result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
	// Region DnCreateCtrl end

	// DnEdoDetailCtrl
	// delegate.helper.gbms.containerised.dn -->dnEdoDetailHandler
	@PostMapping(value = "/dnEdoDetail")
	public ResponseEntity<?> DnEdoDetail(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: DnEdoDetail criteria:" + criteria.toString());

			String login = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String flag = "";
			String tesnjpjp;
			String tesnjppsa;
			String searchcrg = "";
			searchcrg = CommonUtility.deNull(criteria.getPredicates().get("searchcrg"));
			String tesn_nbr = "";
			tesn_nbr = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr"));
			flag = CommonUtility.deNull(criteria.getPredicates().get("flag"));
			String release = CommonUtility.deNull(criteria.getPredicates().get("release"));

			List<EdoValueObjectContainerised> vector1 = new ArrayList<EdoValueObjectContainerised>();
			List<EdoValueObjectContainerised> vector2 = new ArrayList<EdoValueObjectContainerised>();
			if (flag != null && flag.equals("Y")) {
				if (dnService.cancelBillableCharges(CommonUtility.deNull(criteria.getPredicates().get("dnnbr")), "DN"))
					// Vani -- 25thJuly 03
					dnService.cancelDN(CommonUtility.deNull(criteria.getPredicates().get("edo")),
							CommonUtility.deNull(criteria.getPredicates().get("dnnbr")), login,
							CommonUtility.deNull(criteria.getPredicates().get("transType")), searchcrg, tesn_nbr);
				else// Vani -- 25thJuly 03
					errorMessage = "Bills raised cannot Cancel DN";// Vani -- 25thJuly 03

				vector1 = dnService.fetchEdoDetails(CommonUtility.deNull(criteria.getPredicates().get("edo")),
						searchcrg, tesn_nbr);
				vector2 = dnService.fetchDNList(CommonUtility.deNull(criteria.getPredicates().get("edo")), searchcrg,
						tesn_nbr);
			} else {
				vector1 = dnService.fetchEdoDetails(CommonUtility.deNull(criteria.getPredicates().get("edo")),
						searchcrg, tesn_nbr);
				vector2 = dnService.fetchDNList(CommonUtility.deNull(criteria.getPredicates().get("edo")), searchcrg,
						tesn_nbr);
			}
			tesnjpjp = dnService.chktesnJpJp_nbr(tesn_nbr);
			tesnjppsa = dnService.chktesnJpPsa_nbr(tesn_nbr);

			map.put("tesnjpjp", "" + tesnjpjp);
			map.put("tesnjppsa", "" + tesnjppsa);
			map.put("edovect", vector1);
			map.put("dnList", vector2);
			if (release != null && release.equals("release")) {
				// nextScreen(httpservletrequest, "CntrDnReleaseEdo");
			} else {
				// nextScreen(httpservletrequest, "CntrDnEdoDetail");
			}
		} catch (BusinessException e) {
			log.info("Exception DnEdoDetail : ", e);
			errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception DnEdoDetail : ", e);
			errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: DnEdoDetail result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.containerised.dn -->dnEdoSearchHandler
	@PostMapping(value = "/dnEdoSearch")
	public ResponseEntity<?> DnEdoSearch(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		boolean tesnjpjp;
		boolean tesnjppsa;
		try {
			log.info("START: DnEdoSearch criteria:" + criteria.toString());

			String ccode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String searchcrg = CommonUtility.deNull(criteria.getPredicates().get("searchcrg"));
			String txtedo = CommonUtility.deNull(criteria.getPredicates().get("txtedo"));

			List<EdoValueObjectContainerised> vector1 = dnService.fetchEdo(txtedo, ccode, searchcrg);
			String chktesnEdo = dnService.chktesnEdo(txtedo);
			String chkEdoNbr = dnService.chkEdoNbr(txtedo);
			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
				if (txtedo.length() > 6) {
					tesnjpjp = dnService.chktesnJpJp(txtedo);
					tesnjppsa = dnService.chktesnJpPsa(txtedo);
					map.put("tesnjpjp", "" + tesnjpjp);
					map.put("tesnjppsa", "" + tesnjppsa);
				}
			}

			if (txtedo.length() > 6) {
				map.put("chktesnEdo", "" + chktesnEdo);
				map.put("chkEdoNbr", "" + chkEdoNbr);
			}

			map.put("edovect", vector1);
		} catch (BusinessException e) {
			log.info("Exception DnEdoSearch : ", e);
			errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception DnEdoSearch : ", e);
			errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: DnEdoSearch result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
}
