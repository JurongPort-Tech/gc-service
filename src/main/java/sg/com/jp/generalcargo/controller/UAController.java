package sg.com.jp.generalcargo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import sg.com.jp.generalcargo.domain.ReportValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.UACntrJasperReport;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaEsnListValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.service.UAService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = UAController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UAController {

	public static final String ENDPOINT = "gc/containerised/ua";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(UAController.class);

	@Autowired
	private UAService uaService;

	// delegate.helper.gbms.containerised.ua -->UAEsnCancelHandler
	@PostMapping(value = "/uaEsnCancel")
	public ResponseEntity<?> UAEsnCancel(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: UAEsnCancel criteria:" + criteria.toString());

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String esnNo = "";
			String uanbr = "";
			String dispval = CommonUtility.deNull(criteria.getPredicates().get("disp"));
			String transtype = CommonUtility.deNull(criteria.getPredicates().get("transtype"));
			String uanbrpkgs = CommonUtility.deNull(criteria.getPredicates().get("uanbrpkgs"));
			uanbr = CommonUtility.deNull(criteria.getPredicates().get("UANbr"));

			List<UaEsnDetValueObject> esnView = new ArrayList<UaEsnDetValueObject>();
			List<UaListObject> uaList = new ArrayList<UaListObject>();

			try {
				if (CommonUtility.deNull(criteria.getPredicates().get("esnNo")) != null) {
					esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				}
			} catch (Exception e) {
				log.info("Exception UAEsnCancel : ", e);
			}

			if (dispval != null && !dispval.equals("") && dispval.equals("CancelUA")) {
				// added by Dongsheng on 11/12/2012 for SL-CIM-20121203-01.
				// If close shipment is done, UA cannot be cancelled
				if (uaService.chkVslStat(esnNo)) {
					errorMessage = "Cancel UA is not allowed after close shipment.";
				} else {

					if (uaService.cancelBillableCharges(uanbr, "UA")) {
						uaService.cancelUA(uanbr, esnNo, transtype, UserID, uanbrpkgs);
						esnView = uaService.getEsnView(esnNo, transtype);
						uaList = uaService.getUAList(esnNo);
						map.put("esnView", esnView);
						map.put("uaList", uaList);
					} else {
						errorMessage = "Bills raised cannot Cancel UA";
					}
				}
				// nextScreen(request, "CntrUAEsnView");
			}

		} catch (BusinessException e) {
			log.info("Exception UAEsnCancel : ", e);
			errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception UAEsnCancel : ", e);
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
				log.info("END: UAEsnCancel result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.containerised.ua -->UAEsnCreateHandlerS
	@PostMapping(value = "/uaEsnCreate")
	public ResponseEntity<?> UAEsnCreate(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: UAEsnCreate criteria:" + criteria.toString());

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String esnNo = "";
			String uanbr = "";
			String sysdate = "";
			String dispval = CommonUtility.deNull(criteria.getPredicates().get("disp"));
			String transtype = CommonUtility.deNull(criteria.getPredicates().get("transtype"));

			String Esn_Nbr_Pkgs = "";
			String NomWt = "";
			String NomVol = "";
			String date_time = "";
			String UA_Nbr_Pkgs = "";
			String nric_no = "";
			String ictype = "";
			String dpname = "";
			String veh1 = "";
			String veh2 = "";
			String veh3 = "";
			String veh4 = "";
			String veh5 = "";
			String strCntrNum = "";
			String strUnStuffDt = "";

			try {
				if (CommonUtility.deNull(criteria.getPredicates().get("esnNo")) != null) {
					esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				}
			} catch (Exception e) {
				log.info("Exception UAEsnCreate : ", e);
			}

			List<UaEsnDetValueObject> uacreatedisp = new ArrayList<UaEsnDetValueObject>();

			if (dispval != null && !dispval.equals("") && dispval.equals("Create")) {
				boolean vslstat = uaService.chkVslStat(esnNo);
				if (vslstat) {
					log.info("Writing from UAEsnCreateHandler.createUA");
					log.info("Vessel Status is closed cannot Create UA");
					// Commented out by Dongsheng on 2/4/2012. The code is stated incorrectly.
					// throw new BusinessException("M21621");
					throw new BusinessException("M20621");
				}
				boolean esnstat = uaService.chkESNStatus(esnNo);
				if (esnstat) {
					log.info("Writing from UAEsnCreateHandler.CreateUA");
					log.info("ESN Cancelled cannot create UA");
					throw new BusinessException("M20622");
				}
				boolean esnpkgs = uaService.chkESNPkgs(esnNo, transtype);
				if (esnpkgs) {
					log.info("Writing from UAEsnCreateHandler.CreateUA");
					log.info("No more Cargo Left for Shipment");
					throw new BusinessException("M20624");
				}

				if (!vslstat && !esnstat && !esnpkgs)
					uacreatedisp = uaService.getCreateUADisp(esnNo, transtype);

				map.put("uacreatedisp", uacreatedisp);

				// nextScreen(request, "CntrUACreate");
			}

			if (dispval != null && !dispval.equals("") && dispval.equals("Insert")) {
				Esn_Nbr_Pkgs = CommonUtility.deNull(criteria.getPredicates().get("esnnbrpkgs"));
				NomWt = CommonUtility.deNull(criteria.getPredicates().get("wt"));
				NomVol = CommonUtility.deNull(criteria.getPredicates().get("vol"));
				date_time = CommonUtility.deNull(criteria.getPredicates().get("date_time"));
				UA_Nbr_Pkgs = CommonUtility.deNull(criteria.getPredicates().get("trans_qty"));
				nric_no = CommonUtility.deNull(criteria.getPredicates().get("nric_no"));
				ictype = CommonUtility.deNull(criteria.getPredicates().get("ictype"));
				dpname = CommonUtility.deNull(criteria.getPredicates().get("dpname"));
				strCntrNum = CommonUtility.deNull(criteria.getPredicates().get("cntrNum"));
				strUnStuffDt = CommonUtility.deNull(criteria.getPredicates().get("unStuffingDate"));
				veh1 = CommonUtility.deNull(criteria.getPredicates().get("veh1"));
				veh2 = CommonUtility.deNull(criteria.getPredicates().get("veh2"));
				veh3 = CommonUtility.deNull(criteria.getPredicates().get("veh3"));
				veh4 = CommonUtility.deNull(criteria.getPredicates().get("veh4"));
				veh5 = CommonUtility.deNull(criteria.getPredicates().get("veh5"));
				transtype = CommonUtility.deNull(criteria.getPredicates().get("transtype"));

				log.info("111111strCntrNum == " + strCntrNum + "\tstrUnStuffDt == " + strUnStuffDt);

				StringTokenizer stringtokenizer = new StringTokenizer(uaService.getUANbr(esnNo), "-");
				// String uano =
				stringtokenizer.nextToken().trim();
				String ftrans = stringtokenizer.nextToken().trim();
				String vvcd = "";

				uanbr = uaService.createUA(esnNo, transtype, Esn_Nbr_Pkgs, NomWt, NomVol, date_time, UA_Nbr_Pkgs,
						nric_no, ictype, dpname, veh1, veh2, veh3, veh4, veh5, UserID, strCntrNum, strUnStuffDt);
				vvcd = uaService.getVcd(esnNo);
				log.info("vvcd>>>>>>>>>>>>>> from ua handler " + vvcd);

				if (ftrans.equals("True")) {
					log.info("Before Trigger CaB UA calling for create");

					String updatestatus = uaService.TriggerUa(uanbr, UserID, vvcd);
					log.info("After Trigger CaB UA called for create");
					if (updatestatus != null && !updatestatus.equals("") && updatestatus.equalsIgnoreCase("FALSE"))
						map.put("updatestatus", "FALSE");
					else if (updatestatus != null && !updatestatus.equals("") && updatestatus.equalsIgnoreCase("TRUE"))
						map.put("updatestatus", "TRUE");
				}

				map.put("viewcreated", "viewcreated");
				map.put("UANbr", uanbr);

				// nextScreen(request, "CntrUAView");
			}
			sysdate = uaService.getSysdate();
			map.put("sysdate", sysdate);

		} catch (BusinessException e) {
			log.info("Exception UAEsnCreate : ", e);
			errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception UAEsnCreate : ", e);
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
				log.info("END: UAEsnCreate result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.containerised.ua -->UAEsnSearchHandler
	@PostMapping(value = "/uaEsnSearch")
	public ResponseEntity<?> UAEsnSearch(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		int total = 0;
		try {
			log.info("START: UAEsnSearch criteria:" + criteria.toString());

			String esn_asn_nbr = "";
			String sysdate = "";
			String dispval = CommonUtility.deNull(criteria.getPredicates().get("disp"));
			String esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			String transtype = CommonUtility.deNull(criteria.getPredicates().get("transtype"));
			String ftransdtm = CommonUtility.deNull(criteria.getPredicates().get("ftransdtm"));

			List<UaEsnDetValueObject> esnView = new ArrayList<UaEsnDetValueObject>();
			List<UaListObject> uaList = new ArrayList<UaListObject>();

			// lak added for updftrans 11/02/2003
			if (dispval != null && !dispval.equals("") && dispval.equals("Updftrans")) {
				uaService.updFtrans(esnNo, transtype, ftransdtm);
			}
			try {
				if (CommonUtility.deNull(criteria.getPredicates().get("esnasnnbr")) != null) {
					esn_asn_nbr = CommonUtility.deNull(criteria.getPredicates().get("esnasnnbr"));
				}
			} catch (Exception e) {
				log.info("Exception UAEsnSearch : ", e);
			}

			List<UaEsnListValueObject> esnlist = new ArrayList<UaEsnListValueObject>();
			TableResult tableresult = uaService.getEsnList(esn_asn_nbr, criteria);
			
			Object listObject = null;
			listObject = tableresult.getData().getListData().getTopsModel();
			esnlist = new ArrayList<UaEsnListValueObject>();
			if (listObject instanceof List) {
				for (int j = 0; j < ((List<?>) listObject).size(); j++) {
					Object item = ((List<?>) listObject).get(j);
					if (item instanceof Object) {
						esnlist.add((UaEsnListValueObject) item);
					}
				}
			}
			total = tableresult.getData().getTotal();
			map.put("esnlist", esnlist);
			map.put("total", total);

			
			if (dispval != null && !dispval.equals("") && (dispval.equals("View") || dispval.equals("Updftrans"))) {
				esnView = uaService.getEsnView(esnNo, transtype);
				uaList = uaService.getUAList(esnNo);
				map.put("esnView", esnView);
				map.put("uaList", uaList);
			}

			
			sysdate = uaService.getSysdate();
			map.put("sysdate", sysdate);
			
			if (dispval != null && !dispval.equals("") && (dispval.equals("View") || dispval.equals("Updftrans"))) {
				// nextScreen(request, "CntrUAEsnView");
			} else {
				// nextScreen(request, "CntrUAFPSvlt");
			}

		} catch (BusinessException e) {
			log.info("Exception UAEsnSearch : ", e);
			errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception UAEsnSearch : ", e);
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
				log.info("END: UAEsnSearch result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.containerised.ua -->UAEsnViewHandler
	@PostMapping(value = "/uaEsnView")
	public ResponseEntity<?> UAEsnView(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: UAEsnView criteria:" + criteria.toString());

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String esnNo = "";
			String uanbr = "";
			String dispval = CommonUtility.deNull(criteria.getPredicates().get("disp"));
			String transtype = CommonUtility.deNull(criteria.getPredicates().get("transtype"));
			uanbr = CommonUtility.deNull(criteria.getPredicates().get("UANbr"));

			List<UaEsnDetValueObject> uaView = new ArrayList<UaEsnDetValueObject>();

			try {
				if (CommonUtility.deNull(criteria.getPredicates().get("esnNo")) != null) {
					esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				}
			} catch (Exception e) {
				log.info("Exception UAEsnView : ", e);
			}

			if (dispval != null && !dispval.equals("") && dispval.equals("PrintView")) {
				uaView = uaService.getUAViewPrint(uanbr, esnNo, transtype);

				UaEsnDetValueObject esnObj = new UaEsnDetValueObject();
				esnObj = (UaEsnDetValueObject) uaView.get(0);
				String cntrNum = esnObj.getCntrNbr();
				String unStuffDt = esnObj.getUnStuffDate();
				log.info("FROM HANDLER cntrNum == " + cntrNum + "\tunStuffDt ==" + unStuffDt);

				map.put("uaView", uaView);

				// nextScreen(request, "CntrUAView");
			}
			if (dispval != null && !dispval.equals("") && dispval.equals("PrintPage")) {
				uaView = uaService.getUAViewPrint(uanbr, esnNo, transtype);

				log.info("Before Trigger CaB UA calling for print details");

				List<ChargeableBillValueObject> rtarrlist = uaService.getGBBillCharge(uanbr, "UA");
				log.info("After Trigger CaB UA called for print details");

				map.put("billarrlist", rtarrlist);

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

				for (int j = 0; j < rtarrlist.size(); j++) {
					ChargeableBillValueObject cbVO = new ChargeableBillValueObject();
					cbVO = (ChargeableBillValueObject) rtarrlist.get(j);
					if (j == 0) {
						tarcdser = cbVO.getTariffCd();
						tardescser = cbVO.getTariffDesc();
						billtonsser = cbVO.getNbrOtherUnit();
						urateser = cbVO.getUnitRate();
						totchrgamtser = cbVO.getTotalChargeAmt() + cbVO.getGstAmt();
						if ((cbVO.getAcctNbr()).equals("N99990"))
							actnbrser = "CASH";
						else
							actnbrser = cbVO.getAcctNbr();
					}
					if (j == 1) {
						tarcdwf = cbVO.getTariffCd();
						tardescwf = cbVO.getTariffDesc();
						billtonswf = cbVO.getNbrOtherUnit();
						uratewf = cbVO.getUnitRate();
						totchrgamtwf = cbVO.getTotalChargeAmt() + cbVO.getGstAmt();
						if ((cbVO.getAcctNbr()).equals("N99990"))
							actnbrwf = "CASH";
						else
							actnbrwf = cbVO.getAcctNbr();
					}
					/*
					 * if(j==2) { tarcdsr = cbVO.getTariffCd(); tardescsr = cbVO.getTariffDesc();
					 * billtonssr = cbVO.getNbrOtherUnit(); uratesr = cbVO.getUnitRate();
					 * totchrgamtsr = cbVO.getTotalChargeAmt() + cbVO.getGstAmt();
					 * if((cbVO.getAcctNbr()).equals("N99990")) actnbrsr = "CASH"; else actnbrsr =
					 * cbVO.getAcctNbr(); }
					 */
				} // for int j

				UaEsnDetValueObject esnObj = new UaEsnDetValueObject();

				esnObj = (UaEsnDetValueObject) uaView.get(0);
				String esnactnbr = esnObj.getAct_no();

				uaService.purgetemptableUA(uanbr);
				uaService.insertTempUAPrintOut(uanbr, esnNo, transtype);
				uaService.insertTempBill(uanbr, tarcdser, tardescser, billtonsser, urateser, totchrgamtser, actnbrser,
						tarcdwf, tardescwf, billtonswf, uratewf, totchrgamtwf, actnbrwf, tarcdsr, tardescsr, billtonssr,
						uratesr, totchrgamtsr, actnbrsr, UserID, esnactnbr, tarcdsr1, tardescsr1, billtonssr1, uratesr1,
						totchrgamtsr1, actnbrsr1, tarcdsr2, tardescsr2, billtonssr2, uratesr2, totchrgamtsr2, actnbrsr2,
						tunitser, tunitwhf, tunitsr, tunitstore, tunitserwhf);

				
				ReportValueObject rvo = new ReportValueObject();
				String reportFilename = "CntrUAReport.jrxml";
				rvo.setReportFileName(reportFilename);
				String printerNm = (String) CommonUtility.deNull(criteria.getPredicates().get("printername"));
				rvo.setPrinterName(printerNm);

				
				List<UACntrJasperReport>  records = uaService.getUaCntrJasperContent(uanbr);
						
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("uanbr", uanbr);
    			JasperPrint jasperPrint = uaService.getJasperPrint(reportFilename, parameters, uanbr, records);
    			
    			
    			String fileName = FilenameUtils.normalize(uaService.getPdfFileName(rvo,uanbr));
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
			


			map.put("uaView", uaView);
			map.put("viewpage", "viewpage");
			
		} catch (BusinessException e) {
			log.info("Exception UAEsnView : ", e);
			errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception UAEsnView : ", e);
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
				log.info("END: UAEsnView result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
}
