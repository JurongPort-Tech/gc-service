package sg.com.jp.generalcargo.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
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

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GcOpsUaReport;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.LANPrinterVO;
import sg.com.jp.generalcargo.domain.SmartInterfaceInputVO;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaEsnListValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.restclient.SmartServiceRestClient;
import sg.com.jp.generalcargo.service.GeneralCargoUAService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;
import sg.com.jp.generalcargo.util.SmartInterfaceConstants;
import sg.com.jp.generalcargo.config.PrinterProperties;

@CrossOrigin
@RestController
@RequestMapping(value = GCOUaCtrl.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class GCOUaCtrl {

	public static final String ENDPOINT = "gc/ua";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";

	private static final Log log = LogFactory.getLog(GCOUaCtrl.class);
	private String errorMessage = null;

	@Autowired
	private SmartServiceRestClient smartServiceRestClient;

	@Autowired
	private GeneralCargoUAService uAService;
	
	@Autowired
    private PrinterProperties printerProperties;

	@Value("${pdfDir.downloadfile.path}")
	private String pdfDirPath;
	
	@Value("${PRINTERENDPOINT.URI}")
	private String printerUrl;
	
	@Value("${smart.rest.client.url}")
	private String smartBaseUrl;
	
	// delegate.helper.gbms.ops.dnua.ua-->UAEsnSearchHandler
	@PostMapping(value = "/uAEsnSearch")
	public ResponseEntity<?> uAEsnSearch(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		List<UaEsnDetValueObject> vector1 = null;
		List<UaListObject> vector3 = null;
		try {
			log.info("START: uAEsnSearch criteria:" + criteria.toString());

			String s = "";
			s = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String s2 = ""; //esnasnnbr
			String s3 = ""; //sysdate
			String s4 = CommonUtility.deNull(criteria.getPredicates().get("disp")); //in criteria 
			String s5 = CommonUtility.deNull(criteria.getPredicates().get("esnNo")); //replaced with esnasnnbr (s2) if null
			String s6 = CommonUtility.deNull(criteria.getPredicates().get("transtype")); //not found
			String s7 = CommonUtility.deNull(criteria.getPredicates().get("ftransdtm")); //not found
			String userId = s;
			map.put("userId", userId);
			map.put("isSailed", "No");
			/*
			 * >> Add by FPT.Thai - Oct 03 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			//String linkmode = CommonUtility.deNull(criteria.getPredicates().get("linkmode"));
			/*
			 * << Add by FPT.Thai - Oct 03 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */

			if (s4 != null && !s4.equals("") && s4.equals("Updftrans"))
				uAService.updFtrans(s5, s6, s7);
			try {
				if (CommonUtility.deNull(criteria.getPredicates().get("esnasnnbr")) != null)
					s2 = CommonUtility.deNull(criteria.getPredicates().get("esnasnnbr"));
			} catch (Exception exception) {
				log.info("Exception: uAEsnSearch ", exception);
				throw new BusinessException("M4201");
			}
			
			
			List<UaEsnListValueObject> vector4 = new ArrayList<UaEsnListValueObject>();
			log.info("esn number in search handler **:" + s2);
			vector4 = uAService.getEsnList(s2);
			//log.info("vector4 values =", vector4.toString()); 
			log.info("GetESNLIst ***" + vector4.size());
			// Nothing found by esn then could be in transferred cargo
			if (vector4.size() == 0) {
				List<UaEsnListValueObject> transferredCargo = uAService.getTransferredCargo(s2);
				map.put("esnTransCargoList", transferredCargo);
				log.info("GetENSLIst if part***" + transferredCargo.size());
			}
			
			//Workaround to populate s5 and s6
			if (s5 == null || s5 == ""){
				s5 = s2;
				log.info("s5 UPDATED VALUE FROM s2 = " + s5);
			}
			
			if (vector4 != null || vector4.size() > 0);{
				s6 = vector4.get(0).getTrans_type();
				log.info("s6 UPDATED VALUE FROM vector4 = " + s6);
			}
			log.info("****PRINT VALUE FOR S4, S5, S6*****");
			log.info("s4 = " +s4);

			if (s4 != null && !s4.equals("") && (s4.equals("View") || s4.equals("Updftrans") || s4.equals("Search"))) {
				log.info("s5 = " + s5);
				log.info("s6 = " + s6);
				vector1 = uAService.getEsnView(s5, s6);
				vector3 = uAService.getUAList(s5);
				// Check esn whether it associate with cntr
				if (uAService.checkESNCntr(s5))
					map.put("esncntr", "YES");
				else
					map.put("esncntr", "NO");

				for (int i = 0; i < vector1.size(); i++) {
                	UaEsnDetValueObject esnObj = new UaEsnDetValueObject();
                	esnObj = (UaEsnDetValueObject) vector1.get(i);
                	boolean isSailed = uAService.hasVesselSailed(esnObj.getVvCode());
    				if (isSailed) {
    					log.info("Vessel already sailed.");
    					map.put("isSailed", "Yes");
    				} else {
    					map.put("isSailed", "No");
    				}
				}
				map.put("esnView", vector1);
				map.put("uaList", vector3);
			}

			int total = 0;
			if (vector4.size() > 0) {
				total = vector4.size();
			}
			map.put("esnlist", vector4);
			map.put("total", total);
			s3 = uAService.getSysdate();
			map.put("sysdate", s3);

			// Added By chua 25 JUL 2008 BEGIN

			// map.put("csRemote", csRemote);
			// Added By chua 25 JUL 2008 END
			/*
			 * >> Add by FPT.Thai - Oct 03 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			//map.put("linkmode", linkmode);
			/*
			 * << Add by FPT.Thai - Oct 03 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			if (s4 != null && !s4.equals("") && (s4.equals("View") || s4.equals("Updftrans"))) {
				map.put("request", "UAEsnView");
			} else {
				map.put("request", "UAFPSvlt");
			}

			log.info("*****VECTOR1: " + vector1.toString());
			log.info("*****VECTOR3: " + vector3.toString());
			setParametersFormServlet(map, vector1, vector3);

		} catch (BusinessException e) {
			log.info("Exception: uAEsnSearch ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception: uAEsnSearch", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(mapError);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: uAEsnSearch result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	private void setParametersFormServlet(Map<String, Object> map, List<UaEsnDetValueObject> esnViewMap,
			List<UaListObject> uaListMap) throws BusinessException {
		log.info("START: setParametersFormServlet "+" map:"+map +" esnViewMap:"+esnViewMap.size() +" uaListMap:"+uaListMap);
		String esnno = "";
		String vslnm = "";
		String invoy = "";
		String outvoy = "";
		String bkref = "";
		String paymode = "";
		String bpname = "";
		String dcpkgs = "";
		String balpkgs = "";
		String storepkgs = "";
		String wt = "";
		String vol = "";
		String truknm = "";
		String truckic = "";
		String truckphno = "";
		String ftrans = "";
		String crgdesc = "";
		String markings = "";
		String transtype = "";
		String actnbr = "";
		String sysdate = "";
		String whInd = "";
		String whRemarks = "";
		String whAggrNbr = "";
		String isTesnJPJP = "";
		String terminal = "";
		String scheme = "";
		String subScheme = "";
		String gcOperations = "";

		sysdate = (String) map.get("sysdate");
		// added by HoaBT2: get userId

		int listsize = 0;

		List<UaEsnDetValueObject> esnView = new ArrayList<UaEsnDetValueObject>();
		List<UaListObject> uaList = new ArrayList<UaListObject>();

		esnView = esnViewMap;
		uaList = uaListMap;

		if(esnView != null) {
			for (int i = 0; i < esnView.size(); i++) {
				UaEsnDetValueObject esnObj = new UaEsnDetValueObject();
				esnObj = esnView.get(i);
	
				esnno = esnObj.getEsn_asn_nbr();
				vslnm = esnObj.getVessel_name();
				invoy = esnObj.getIn_voy_nbr();
				outvoy = esnObj.getOut_voy_nbr();
				bkref = esnObj.getBk_ref_nbr();
				paymode = (esnObj.getPay_mode()).equals("C") ? "Cash" : "Account";
				bpname = esnObj.getBill_party();
				dcpkgs = esnObj.getDecl_pkg();
				balpkgs = esnObj.getBal_pkg();
				storepkgs = esnObj.getPkg_stored();
				wt = esnObj.getWeight();
				vol = esnObj.getVolume();
				truknm = esnObj.getTrucker_name();
				truckphno = esnObj.getTrucker_cont_no();
				truckic = esnObj.getTrucker_ic();
				ftrans = esnObj.getFirst_trans();
				crgdesc = esnObj.getCargo_desc();
				markings = esnObj.getCargo_markings();
				transtype = esnObj.getTrans_type();
				whInd = esnObj.getWhInd();
				whAggrNbr = esnObj.getWhAggrNbr();
				whRemarks = esnObj.getWhRemarks();
				terminal = esnObj.getTerminal();
				scheme = esnObj.getScheme();
				subScheme = esnObj.getSubScheme();
				gcOperations = esnObj.getGcOperations();
				actnbr = esnObj.getAct_no();
				if (actnbr != null && !actnbr.equals("") && (actnbr.equals("CA") || actnbr.equals("CASH")))
					actnbr = "CASH";
			}
		}

		if (uaList == null)
			listsize = 0;
		else
			listsize = uaList.size();

		// Added By chua 25 JUL 2008 END

		// Added By Vietnv 25/03/2014
		if (StringUtils.equalsIgnoreCase(transtype, "A")) {
			isTesnJPJP = "disabled";
		}

		//

		List<String> listCashReceiptNbr = new ArrayList<String>();
		try {
			for (int i = 0; i < listsize; i++) {
				UaListObject uaObj = new UaListObject();
				uaObj = uaList.get(i);

				String CashReceiptNbr = CommonUtility
						.deNull((uAService.getCashSales(uaObj.getUa_nbr())).getCashReceiptNbr().toString());
				listCashReceiptNbr.add(CashReceiptNbr);

			}
		} catch (Exception e) {
			log.info("Exception setParametersFormServlet : ", e);
			throw new BusinessException("M4201");
		}

		map.put("esnno", esnno);
		map.put("vslnm", vslnm);
		map.put("invoy", invoy);
		map.put("outvoy", outvoy);
		map.put("bkref", bkref);
		map.put("paymode", paymode);
		map.put("bpname", bpname);
		map.put("dcpkgs", dcpkgs);
		map.put("balpkgs", balpkgs);
		map.put("storepkgs", storepkgs);
		map.put("wt", wt);
		map.put("vol", vol);
		map.put("truknm", truknm);
		map.put("truckic", truckic);
		map.put("truckphno", truckphno);
		map.put("ftrans", ftrans);
		map.put("crgdesc", crgdesc);
		map.put("markings", markings);
		map.put("transtype", transtype);
		map.put("actnbr", actnbr);
		map.put("sysdate", sysdate);
		map.put("whInd", whInd);
		map.put("whRemarks", whRemarks);
		map.put("whAggrNbr", whAggrNbr);
		map.put("isTesnJPJP", isTesnJPJP);
		map.put("terminal", terminal);
		map.put("scheme", scheme);
		map.put("subScheme", subScheme);
		map.put("gcOperations", gcOperations);

		map.put("listCashReceiptNbr", listCashReceiptNbr);
	}

	// delegate.helper.gbms.ops.dnua.ua --> UAEsnCreateHandler
	@PostMapping(value = "/uAEsnCreate")
	public ResponseEntity<?> uAEsnCreate(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: uAEsnCreate criteria:" + criteria.toString());

			String UserID = "";
			String coCd = "";
			UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String esnNo = "";
			String uanbr = "";
			String sysdate = "";
			String dispval = CommonUtility.deNull(criteria.getPredicates().get("disp"));
			String transtype = CommonUtility.deNull(criteria.getPredicates().get("transtype"));

			String NomWt = "";
			String Esn_Pkgs = "";
			String NomVol = "";
			String date_time = "";
			String UA_Nbr_Pkgs = "";
			String nric_no = "";
			String ictype = "";
			String dpname = "";
			String veh1 = "";
			String bkRef = ""; // Added by Babatunde on Jan. 23, 2014
			// ++ VietND02 - Remove Parameter
			/*
			 * String veh2 = ""; String veh3 = ""; String veh4 = ""; String veh5 = "";
			 */
			// -- VietND02

			try {
				if (CommonUtility.deNull(criteria.getPredicates().get("esnNo")) != null) {
					esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				}
			} catch (Exception e) {
				log.info("Exception: uAEsnCreate ", e);
				throw new BusinessException("M4201");
			}
			if (StringUtils.equals(coCd, "JP")) {
				boolean isJPtoJP = uAService.isTESN_JP_JP(esnNo);
				if (isJPtoJP == true) {
					log.info("Creation of UA for JP to JP is not allowed.");
					throw new BusinessException("M100010");
				}
			}
			bkRef = CommonUtility.deNull(criteria.getPredicates().get("bkRef"));
			boolean isClosed = uAService.isClosedShipment(bkRef);
			if (isClosed) {
				log.info("Shipment is closed, creation of UA is not allowed.");
				throw new BusinessException("M100013");
			}

			List<UaEsnDetValueObject> uacreatedisp = new ArrayList<UaEsnDetValueObject>();

			if (dispval != null && !dispval.equals("") && dispval.equals("Create")) {
				boolean vslstat = uAService.chkVslStat(esnNo);
				if (vslstat) {
					log.info("Writing from UAEsnCreateHandler.createUA");
					log.info("Vessel Status is closed cannot Create UA");
					// Amended by Dongsheng on 29/03/2012. The error code is wrong.
					// throw new BusinessException("M21621");
					throw new BusinessException("M20621");
				} else { // added by Dongsheng on 05/06/2012
					boolean uaCreationAllowed = uAService.checkBKCreatedAfterSHPReopen(esnNo);
					if (!uaCreationAllowed) {
						log.info(
								"Writing from UAEsnCreateHandler.createUA -- UA creation not allowed for ESN created before shipment reopen");
						throw new BusinessException("M20625");
					}
				} // end of addition by Dongsheng on 5/6/2012

				boolean esnstat = uAService.chkESNStatus(esnNo);
				if (esnstat) {
					log.info("Writing from UAEsnCreateHandler.CreateUA");
					log.info("ESN Cancelled cannot create UA");
					throw new BusinessException("M20622");
				}
				// log.info("transtype from handler "+transtype);
				// log.info("esnNo from handler "+esnNo);
				boolean esnpkgs = uAService.chkESNPkgs(esnNo, transtype);
				if (esnpkgs) {
					log.info("Writing from UAEsnCreateHandler.CreateUA");
					log.info("No more Cargo Left for Shipment");
					throw new BusinessException("M20624");
				}
				// old------------------------------------------------------
				// if (!vslstat && !esnstat && !esnpkgs)
				// uacreatedisp = uAService.getCreateUADisp(esnNo, transtype);
				// ---------------------------------------------------------
				// Modify by SONLT
				List<String[]> cntrNbr = new ArrayList<String[]>();
				if (!vslstat && !esnstat && !esnpkgs) {
					uacreatedisp = uAService.getCreateUADisp(esnNo, transtype);
					cntrNbr = uAService.getCntrNbr(esnNo);
				}
				map.put("cntrNbr", cntrNbr);
				if (uAService.checkESNCntr(esnNo)) {
					map.put("esncntr", "YES");
				}
				// End

				map.put("uacreatedisp", uacreatedisp);
				// nextScreen(request, "UACreate");
			}

			if (dispval != null && !dispval.equals("") && dispval.equals("Insert")) {
				// Esn_Nbr_Pkgs =
				// CommonUtility.deNull(criteria.getPredicates().get("esnNbrPkgs)"));
				NomWt = CommonUtility.deNull(criteria.getPredicates().get("wt"));
				NomVol = CommonUtility.deNull(criteria.getPredicates().get("vol"));
				date_time = CommonUtility.deNull(criteria.getPredicates().get("date_time"));
				UA_Nbr_Pkgs = CommonUtility.deNull(criteria.getPredicates().get("trans_qty"));
				nric_no = CommonUtility.deNull(criteria.getPredicates().get("nric_no"));
				ictype = CommonUtility.deNull(criteria.getPredicates().get("ictype"));
				dpname = CommonUtility.deNull(criteria.getPredicates().get("dpname"));
				veh1 = CommonUtility.deNull(criteria.getPredicates().get("veh1")).toUpperCase();
				Esn_Pkgs = CommonUtility.deNull(criteria.getPredicates().get("esnPkgs"));

				// ++ VietND02
				/*
				 * veh2 = CommonUtility.deNull(criteria.getPredicates().get("veh2"); veh3 =
				 * CommonUtility.deNull(criteria.getPredicates().get("veh3"); veh4 =
				 * CommonUtility.deNull(criteria.getPredicates().get("veh4"); veh5 =
				 * CommonUtility.deNull(criteria.getPredicates().get("veh5");
				 */
				// -- VietND02
				transtype = CommonUtility.deNull(criteria.getPredicates().get("transtype"));

				StringTokenizer stringtokenizer = new StringTokenizer(uAService.getUANbr(esnNo), "-");
				String uano = stringtokenizer.nextToken().trim();
				String ftrans = stringtokenizer.nextToken().trim();
				String vvcd = "";
				log.info(uano);
				// TVS-Added to block charges logging for stuffing esns.
				if (uAService.checkEsnStuffIndicator(esnNo))
					ftrans = "False";

				// Added by SONLT
				String cntrNo = "";
				String cntrSeq = "";
				String cntr = CommonUtility.deNull(criteria.getPredicates().get("cntrNo"));
				if ((cntr != null) && (!"".equals(cntr))) {
					String[] tmp = new String[2];
					tmp = cntr.split(",");
					cntrSeq = tmp[0].trim();
					cntrNo = tmp[1].trim();
				}
				// END

				String custCd = "";
				if (!StringUtils.equalsIgnoreCase("S", ictype)) {
					custCd = uAService.getCustCdByIcNbr(ictype, nric_no);
					if (StringUtils.isEmpty(custCd)) {
						log.info("The IC Number entered is not valid");
						throw new BusinessException("M100011");
					}
				}
				// BEGIN added by Maksym JCMS Smart CR 6.1

				if (!uAService.isValidVehicleNumber(veh1, coCd)) {
					throw new BusinessException("M1000001");
				}
				// END added by Maksym JCMS Smart CR 6.1
				uanbr = uAService.createUA(esnNo, transtype, Esn_Pkgs, NomWt, NomVol, date_time, UA_Nbr_Pkgs, nric_no,
						ictype, dpname, veh1, UserID);

				// Added by SONLT

				if (uAService.checkESNCntr(esnNo) && !"".equalsIgnoreCase(CommonUtility.deNull(cntrSeq))) {
					// Update container field for DN_DETAILS table
					uAService.updateUA(uanbr, cntrNo);

//			            long declrWt = Long.parseLong( CommonUtility.deNull(criteria.getPredicates().get("declrWt")));
//			      	  	int declrPkgs = Integer.parseInt( CommonUtility.deNull(criteria.getPredicates().get("declrPkgs")));
//			      	  	long weight = (Integer.parseInt(UA_Nbr_Pkgs)*declrWt)/declrPkgs;

					// Check first UA for MOT container to capture time
					int noOfua = 0;
					noOfua = uAService.checkFirstUA(esnNo, cntrNo);
					if (noOfua <= 0) {
						String newCatCd = uAService.getNewCatCd(cntrSeq);
//				          	  uAService.updateCntr(cntrSeq, cntrNo, UserID);
						// get standard Weight
						uAService.updateStdWeigth(cntrSeq, cntrNo, UserID, newCatCd);
						// update weight
//				          	  uAService.updateWeight(cntrSeq, weight, UserID, "SUB");
//				        }else{
//				        	 //update weight
//				        	 uAService.updateWeight(cntrSeq, weight, UserID, "SUB");
					}
				}
				// End

				vvcd = uAService.getVcd(esnNo);
				log.info("vvcd>>>>>>>>>>>>>> from ua handler " + vvcd);
				// Start added for SMART CR by FPT on 24-Jan-2014
				if ((vvcd != null && !"".equals(vvcd)) && ("E".equals(transtype) || "C".equals(transtype))) {
					log.info(
							"**********Before SMART interface calling for mark the respective locations as Occupied for the packages in UA and register this event in SMART: vvcd="
									+ vvcd + ",transtype=" + transtype + ", UA number=" + uanbr);
					try {
						String refType = "ESN";
						if ("E".equals(transtype)) {
							refType = "ESN";
						} else {
							refType = "TESNPJ";
						}

						int numberOfPackage = Integer.parseInt(UA_Nbr_Pkgs);

						// VietNguyen added on 03 Feb 2015
						// long declrWt = Long.parseLong(
						// CommonUtility.deNull(criteria.getPredicates().get("declrWt"));
						double declrWt = Double
								.parseDouble(CommonUtility.deNull(criteria.getPredicates().get("declrWt")));
						int declrPkgs = Integer
								.parseInt(CommonUtility.deNull(criteria.getPredicates().get("declrPkgs")));
						double weight = (double) (numberOfPackage * declrWt) / declrPkgs;

						// BigDecimal tonnage = new BigDecimal(weight / 1000);
						BigDecimal tonnage = new BigDecimal(weight).divide(new BigDecimal(1000),5,
								RoundingMode.HALF_UP);

						String serverIp = null;
						String serverNm = null;
						InetAddress jpOnlineAddress = InetAddress.getLocalHost();
						if (jpOnlineAddress != null) {
							serverIp = jpOnlineAddress.getHostAddress();
							serverNm = jpOnlineAddress.getHostName();
						}

						log.info("UA Create serverIp    = " + serverIp);
						log.info("UA Create serverName = " + serverNm);
						log.info("UA Create number of package = " + numberOfPackage);
						log.info("UA Create tonnage = " + tonnage.doubleValue());
						log.info("UA Create vvCd = " + vvcd);
						log.info("UA Create RefNbr = " + esnNo);
						log.info("UA Create RefType = " + refType);

						SmartInterfaceInputVO inputObj = new SmartInterfaceInputVO();
						inputObj.setUserId(UserID);
						inputObj.setVvCd(vvcd);
						inputObj.setOccSrcCd(SmartInterfaceConstants.SOURCE_JP);
						inputObj.setActionCd(SmartInterfaceConstants.CREATE_UA_EVENT_CD);
						inputObj.setServerIp(serverIp);
						inputObj.setServerNm(serverNm);
						inputObj.setClassNm(this.getClass().getName());
						inputObj.setClassDesc("UA create handler class");
						inputObj.setRefNbr(esnNo);
						inputObj.setRefType(refType);
						inputObj.setNbrPkgs(numberOfPackage);
						inputObj.setTonnage(tonnage);
						inputObj.setTransNbr(uanbr);

						log.info("UA Create TransDttm = " + date_time);

						DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
						df.setLenient(false);
						inputObj.setTransDttm(df.parse(date_time));
						String url = smartBaseUrl + "/markStorageOccupancy";
						log.info("[markStorageOccupancy] Calling SMART service URL= " + url);
						smartServiceRestClient.markStorageOccupancy(inputObj);
					} catch (Exception ex) {
						log.info("SMART Interface markStorageOccupancy exception: UA number=" + uanbr);
						log.info("Exception: " + ex.getMessage());
					}
					log.info("**********After SMART interface calling: UA number=" + uanbr);
				}
				// End added for SMART CR by FPT on 24-Jan-2014

				if (ftrans.equals("True")) {
					log.info("Before Trigger CaB UA calling for create");

					String updatestatus = uAService.TriggerUa(uanbr, UserID, vvcd);
					log.info("After Trigger CaB UA called for create");
					if (updatestatus != null && !updatestatus.equals("") && updatestatus.equalsIgnoreCase("FALSE"))
						map.put("updatestatus", "FALSE");
					else if (updatestatus != null && !updatestatus.equals("") && updatestatus.equalsIgnoreCase("TRUE"))
						map.put("updatestatus", "TRUE");
				}

				map.put("viewcreated", "viewcreated");
				map.put("UANbr", uanbr);
				log.info("1=======contno: " + cntrNo);
				map.put("cntrNo", cntrNo);

				// nextScreen(request, "UAView");
			}

			sysdate = uAService.getSysdate();
			map.put("sysdate", sysdate);

		} catch (BusinessException e) {
			log.info("Exception: uAEsnCreate ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception: uAEsnCreate", e);
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
				log.info("END: uAEsnCreate result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.ops.dnua.ua.-->UAEsnUpdateHandler
	@PostMapping(value = "/uAEsnUpdate")
	public ResponseEntity<?> uAEsnUpdate(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: uAEsnUpdate criteria:" + criteria.toString());

			String companyCode = "";
			companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String esnNo = "";
			String transtype = "";
			String uanbr = "";
			String vehicleNo = "";
			String updateFlag = "";
			esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			transtype = CommonUtility.deNull(criteria.getPredicates().get("transtype"));
			uanbr = CommonUtility.deNull(criteria.getPredicates().get("UANbr"));
			vehicleNo = CommonUtility.deNull(criteria.getPredicates().get("vehicleNo"));
			updateFlag = CommonUtility.deNull(criteria.getPredicates().get("updateFlag"));

			if ("toUpdate".equals(updateFlag)) {
				map.put("esnNo", esnNo);
				map.put("transtype", transtype);
				map.put("UANbr", uanbr);
				map.put("vehicleNo", vehicleNo);

				// nextScreen(request, "UAVehicleNoUpdate");
			} else if ("Update".equals(updateFlag)) {

				// shifted down by Dongsheng on 25/5/2016
				String new_vehicleNo = CommonUtility.deNull(criteria.getPredicates().get("new_vehicleNo")).toUpperCase()
						.trim();

				if (!uAService.isValidVehicleNumber(new_vehicleNo, companyCode)) {
					throw new BusinessException("M1000001");
				}
				// END added by Maksym JCMS Smart CR 6.1
				uAService.updateVehicleNo(uanbr, new_vehicleNo);

				// forwardHandler(request, "UAEsnView");
			}

		} catch (BusinessException e) {
			log.info("Exception: uAEsnUpdate ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception: uAEsnUpdate", e);
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
				log.info("END: uAEsnUpdate result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.ops.dnua.ua --> UAEsnViewHandler
	@PostMapping(value = "/uAEsnView")
	public ResponseEntity<?> uAEsnView(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		Resource resource = null;
		try {
			log.info("START: uAEsnView criteria:" + criteria.toString());
			String userId = criteria.getPredicates().get("userAccount");
			String esnNo = "";
			String uaNo = "";
			String mode = criteria.getPredicates().get("disp");
			String transType = criteria.getPredicates().get("transtype");
			uaNo = criteria.getPredicates().get("UANbr");

			if (criteria.getPredicates().get("esnNo") != null)
				esnNo = criteria.getPredicates().get("esnNo");

			// added by Chua on 25 JUL 2008 - begin

			CashSalesValueObject csvo = uAService.getCashSales(uaNo);
			map.put("cashsale", csvo);

			String machineId = uAService.getMachineID(csvo.getCashReceiptNbr());
			map.put("machineId", machineId);

			log.info("ESN CashSales Type : " + csvo.getCsType());

			String paymentMode = uAService.getCashSalesPaymentCode(csvo.getCsType());
			map.put("paymentMode", paymentMode);

			String nets_refId = uAService.getNETSRefID(csvo.getCashReceiptNbr());
			map.put("nets_refId", nets_refId);

			// Added by ZanFeng start on 30/01/2011
			boolean exitFlag = uAService.checkVehicleExist(uaNo);
			if (exitFlag) {
				map.put("exitFlag", "TRUE");
			} else {
				map.put("exitFlag", "FALSE");
			}
			// End

			// added by Chua on 25 JUL 2008 - end

			if (mode != null && !mode.equals("") && mode.equals("PrintView")) {
				List<UaEsnDetValueObject> uaList = uAService.getUAViewPrint(uaNo, esnNo, transType);
				map.put("uaView", uaList);
				map.put("screen", "UAView");
			}

			if (mode != null && !mode.equals("") && mode.equals("PrintPage")) {
				List<UaEsnDetValueObject> vector2 = uAService.getUAViewPrint(uaNo, esnNo, transType);

				log.info("Before Trigger CaB UA calling for print details");
				List<ChargeableBillValueObject> chargeList = uAService.getGBBillCharge(uaNo, "UA");
				log.info("After Trigger CaB UA called for print details");

				map.put("billarrlist", chargeList);
				String tariffCd = "";
				String tariffCd1 = "";
				String tariffCd2 = "";
				String tariffCd3 = "";
				String tariffCd4 = "";

				String tariffDesc = "";
				String tariffDesc1 = "";
				String tariffDesc2 = "";
				String tariffDesc3 = "";
				String tariffDesc4 = "";

				double unitRate = 0.0D;
				double unitRate1 = 0.0D;
				double unitRate2 = 0.0D;
				double unitRate3 = 0.0D;
				double unitRate4 = 0.0D;

				double totalItemAmt = 0.0D;
				double totalItemAmt1 = 0.0D;
				double totalItemAmt2 = 0.0D;
				double totalItemAmt3 = 0.0D;
				double totalItemAmt4 = 0.0D;

				double nbrOtherUnit = 0.0D;
				double nbrOtherUnit1 = 0.0D;
				double nbrOtherUnit3 = 0.0D;
				double nbrOtherUnit2 = 0.0D;
				double nbrOtherUnit4 = 0.0D;

				String billAcctNbr = "";
				String billAcctNbr1 = "";
				String billAcctNbr2 = "";
				String billAcctNbr3 = "";
				String billAcctNbr4 = "";

				double nbrTimeUnit = 0.0D;
				double nbrTimeUnit1 = 0.0D;
				double nbrTimeUnit2 = 0.0D;
				double nbrTimeUnit3 = 0.0D;
				double nbrTimeUnit4 = 0.0D;

				for (int i = 0; i < chargeList.size(); i++) {
					ChargeableBillValueObject chargeablebillvalueobject = new ChargeableBillValueObject();
					chargeablebillvalueobject = (ChargeableBillValueObject) chargeList.get(i);

					if (i == 0) {
						tariffCd = chargeablebillvalueobject.getTariffCd();
						tariffDesc = chargeablebillvalueobject.getTariffDesc();
						nbrOtherUnit = chargeablebillvalueobject.getNbrOtherUnit();
						unitRate = chargeablebillvalueobject.getUnitRate();
						totalItemAmt = chargeablebillvalueobject.getTotalChargeAmt()
								+ chargeablebillvalueobject.getGstAmt();
						nbrTimeUnit = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990"))
							billAcctNbr = "CASH";
						else
							billAcctNbr = chargeablebillvalueobject.getAcctNbr();
					}

					if (i == 1) {
						tariffCd1 = chargeablebillvalueobject.getTariffCd();
						tariffDesc1 = chargeablebillvalueobject.getTariffDesc();
						nbrOtherUnit1 = chargeablebillvalueobject.getNbrOtherUnit();
						unitRate1 = chargeablebillvalueobject.getUnitRate();
						totalItemAmt1 = chargeablebillvalueobject.getTotalChargeAmt()
								+ chargeablebillvalueobject.getGstAmt();
						nbrTimeUnit1 = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990"))
							billAcctNbr1 = "CASH";
						else
							billAcctNbr1 = chargeablebillvalueobject.getAcctNbr();
					}

					if (i == 2) {
						tariffCd2 = chargeablebillvalueobject.getTariffCd();
						tariffDesc2 = chargeablebillvalueobject.getTariffDesc();
						nbrOtherUnit2 = chargeablebillvalueobject.getNbrOtherUnit();
						unitRate2 = chargeablebillvalueobject.getUnitRate();
						totalItemAmt2 = chargeablebillvalueobject.getTotalChargeAmt()
								+ chargeablebillvalueobject.getGstAmt();
						nbrTimeUnit2 = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990"))
							billAcctNbr2 = "CASH";
						else
							billAcctNbr2 = chargeablebillvalueobject.getAcctNbr();
					}

					if (i == 3) {
						tariffCd3 = chargeablebillvalueobject.getTariffCd();
						tariffDesc3 = chargeablebillvalueobject.getTariffDesc();
						nbrOtherUnit3 = chargeablebillvalueobject.getNbrOtherUnit();
						unitRate3 = chargeablebillvalueobject.getUnitRate();
						totalItemAmt3 = chargeablebillvalueobject.getTotalChargeAmt()
								+ chargeablebillvalueobject.getGstAmt();
						nbrTimeUnit3 = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990"))
							billAcctNbr3 = "CASH";
						else
							billAcctNbr3 = chargeablebillvalueobject.getAcctNbr();
					}

					if (i == 4) {
						tariffCd4 = chargeablebillvalueobject.getTariffCd();
						tariffDesc4 = chargeablebillvalueobject.getTariffDesc();
						nbrOtherUnit4 = chargeablebillvalueobject.getNbrOtherUnit();
						unitRate4 = chargeablebillvalueobject.getUnitRate();
						totalItemAmt4 = chargeablebillvalueobject.getTotalChargeAmt()
								+ chargeablebillvalueobject.getGstAmt();
						nbrTimeUnit4 = chargeablebillvalueobject.getNbrTimeUnit();
						if (chargeablebillvalueobject.getAcctNbr().equals("N99990"))
							billAcctNbr4 = "CASH";
						else
							billAcctNbr4 = chargeablebillvalueobject.getAcctNbr();
					}
				}

				UaEsnDetValueObject uaEsnDetValueObject = (UaEsnDetValueObject) vector2.get(0);
				String acctNo = uaEsnDetValueObject.getAct_no();
				uAService.purgetemptableUA(uaNo);
				String insertTempUaStatus = uAService.insertTempUAPrintOut(uaNo, esnNo, transType);
				log.info("insertTempUaStatus: " + insertTempUaStatus);
				String insertTempBillStatus = uAService.insertTempBill(uaNo, tariffCd, tariffDesc, nbrOtherUnit,
						unitRate, totalItemAmt, billAcctNbr, tariffCd1, tariffDesc1, nbrOtherUnit1, unitRate1,
						totalItemAmt1, billAcctNbr1, tariffCd2, tariffDesc2, nbrOtherUnit2, unitRate2, totalItemAmt2,
						billAcctNbr2, userId, acctNo, tariffCd3, tariffDesc3, nbrOtherUnit3, unitRate3, totalItemAmt3,
						billAcctNbr3, tariffCd4, tariffDesc4, nbrOtherUnit4, unitRate4, totalItemAmt4, billAcctNbr4,
						nbrTimeUnit, nbrTimeUnit1, nbrTimeUnit2, nbrTimeUnit3, nbrTimeUnit4);
				log.info("insertTempBillStatus: " + insertTempBillStatus);

				/*
				 * ReportPrintingBeanHome reportprintingbeanhome =
				 * (ReportPrintingBeanHome)ejbhomefactory.lookUpHome("ReportPrintingBean");
				 * ReportPrintingBean reportprintingbean = reportprintingbeanhome.create();
				 * ReportValueObject reportvalueobject = new ReportValueObject(); String rptName
				 * = "UAPrint.jasper"; reportvalueobject.setReportFileName(rptName); String
				 * printerName = criteria.getPredicates().get("printername");
				 * log.info("printername ------------> " + printerName);
				 * reportvalueobject.setPrinterName(printerName);
				 * reportvalueobject.setReportPageSize("a4");
				 * //reportvalueobject.addStringPrompt(uaNo);
				 * reportvalueobject.addStringPrompt("D020312970061");
				 * //reportvalueobject.addStringPrompt("Out Of JP");
				 * 
				 * boolean flag = reportprintingbean.printReport(reportvalueobject); if(flag)
				 * log.info("Report UA Printed ..............>>>>>>>>>>>>>>>>>>>>>>");
				 */

				String pdfDir = pdfDirPath + '/';

				String jasperName = "UAReport.jrxml";
				String fileNameJasper = jasperName;

				// Set input parameter
				// uaNo
				log.info("uaNo = " + uaNo);
				/*
				 * Map parameters = new HashMap(); parameters.put("UaNbr", uaNo); //
				 */
				Map<String, Object> parameters = new HashMap<String, Object>();
				String tempFolder = pdfDirPath;
				if (tempFolder == null || "".equalsIgnoreCase(tempFolder)) {
					tempFolder = "/jrpapp1/wrk/backup/temp/pdf";
				}

				String imageFilename = tempFolder + "/barcode_" + uaNo + ".png";

				generateCode39Writer(imageFilename, uaNo);
				parameters.put("uaNo", uaNo);
				parameters.put("barcodeImage", imageFilename);

				// BarcodeImageUtil.getInstance().genBarcodeImage(imageFilename, uaNo);

				parameters.put("UaNbr", uaNo);
				parameters.put("PaymentMode", "");
				parameters.put("barcodeImage", imageFilename);

				if (log.isInfoEnabled()) {
					log.info("param: " + parameters);
					log.info("uaNbr: " + uaNo);
					log.info("imgFile: " + imageFilename);
				}

				List<GcOpsUaReport> uaListPrint = uAService.getUAPrintJasper(uaNo);

				JasperPrint jasperPrint = uAService.getJasperPrint(fileNameJasper, parameters, uaNo, uaListPrint);

				SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
				Date date = new Date();
				String fileName = "UA_" + dateTimeFormat.format(date) + ".pdf";
				String filePath = pdfDir + fileName;
				log.info("=== Printing: " + filePath);
				JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
				log.info("=== Print ok");

				String printerNm = (String) CommonUtility.deNull(criteria.getPredicates().get("printername"));
				log.info("UA printername ------------> " + printerNm);
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
				PrintService selectedService = null;
				PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
				if(services != null && services.length != 0){
				  for(PrintService service : services){
				      String existingPrinter = service.getName();
				      if(existingPrinter.equals(printerNm))
				      {
				          selectedService = service;
				          break;
				      }
				  }
				}
				if(selectedService != null){   
				  try{
				printServiceAttributeSet.add(printerName);
				  }catch(Exception e){
					  log.info("JasperReport Error: "+e.getMessage());
				  }
				}else{
					printerName = new PrinterName(services[0].getName(), null);
					printServiceAttributeSet.add(printerName);
					log.info("JasperReport Error: Printer not found!.Use the first option. ");
				}
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
				log.info("p1: " +  p1);
				resource = new UrlResource(p1.toUri());

				return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
						.header(HttpHeaders.CONTENT_DISPOSITION,
								"attachment; filename=\"" + resource.getFilename() + "\"")
						.body(resource);

//				map.put("uaView", vector2);
//				map.put("viewpage", "viewpage");
//				map.put("screen", "UAView");
			}
		} catch (IOException ioe) {
			log.info("Failed to read or encode the PDF file: " + ioe);
        } catch (BusinessException e) {
			log.info("Exception: UAEsnView ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception: UAEsnView", e);
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

	public static void generateCode39Writer(String barcodeText, String nbr) throws Exception {
		try {
			log.info("START: generateCode39Writer "+" barcodeText:"+CommonUtility.deNull(barcodeText) +" nbr:"+CommonUtility.deNull(nbr));
			File outputfile = new File(barcodeText);
			BitMatrix bitMatrix = new MultiFormatWriter().encode(nbr, BarcodeFormat.CODE_128, 0, 0);
			BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
			ImageIO.write(bufferedImage, "png", outputfile);
		} catch (Exception e) {
			log.info("Exception generateCode39Writer : ", e);
			throw new BusinessException("M4201");
		}

	}

	// delegate.helper.gbms.ops.dnua.ua --> UAEsnCancelHandler
	@PostMapping(value = "/uAEsnCancel")
	public ResponseEntity<?> uAEsnCancel(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: uAEsnCancel criteria:" + criteria.toString());

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String esnNo = "";
			String uanbr = "";
			String dispval = CommonUtility.deNull(criteria.getPredicates().get("disp"));
			String transtype = CommonUtility.deNull(criteria.getPredicates().get("transtype"));
			String uanbrpkgs = CommonUtility.deNull(criteria.getPredicates().get("uanbrpkgs"));
			uanbr = CommonUtility.deNull(criteria.getPredicates().get("UANbr"));
			String uaCreateDttm = CommonUtility.deNull(criteria.getPredicates().get("uaCreateDttm"));

			String vvCode = CommonUtility.deNull(criteria.getPredicates().get("vvCode"));
			log.info("UA Create DTTM / :" + uaCreateDttm + ":" + vvCode);
			List<UaEsnDetValueObject> esnView = new ArrayList<UaEsnDetValueObject>();
			List<UaListObject> uaList = new ArrayList<UaListObject>();

			try {
				if (criteria.getPredicates().get("esnNo") != null) {
					esnNo = criteria.getPredicates().get("esnNo");
				}
			} catch (Exception e) {
				log.info("Exception: uAEsnCancel ", e);
				throw new BusinessException("M4201");
			}

			if (dispval != null && !dispval.equals("") && dispval.equals("CancelUA")) {

				if(uAService.checkCancelUA(uanbr)) {
					errorMessage = "Cancel UA is not allowed. UA is already deleted";
				} 
				String cntrNbr = criteria.getPredicates().get("cntrNbr");
				String cntrSeq = "";
				if (!"".equals(CommonUtility.deNull(cntrNbr))) {
					cntrSeq = uAService.getCntrSeq(cntrNbr);
				}

				// added by Dongsheng on 11/12/2012 for SL-CIM-20121203-01.
				// If close shipment is done, UA cannot be cancelled
				if (uAService.chkVslStat(esnNo)) {
					errorMessage = ConstantUtil.ErrorMsg_Cancel_UA_Close_Shipment;
				} else if (uAService.isAsnShut(esnNo)) {
					errorMessage = ConstantUtil.ErrorMsg_Cancel_UA_ShutOut;
				} else if (uAService.isUABefCloseShp(vvCode, uaCreateDttm)) {
					errorMessage = ConstantUtil.ErrorMsg_Cancel_UA_Created_Close_Shipment;
				} else {

					if (uAService.cancelBillableCharges(uanbr, "UA")) {
						// Begin TungVH
						boolean chk = uAService.checkESNCntr(esnNo);
						String uaFirst = "";
						if (chk && !"".equals(CommonUtility.deNull(cntrSeq))) {
							uaFirst = uAService.getUaCntrFirst(cntrSeq, cntrNbr);
						}
						// End TungVH
						uAService.cancelUA(uanbr, esnNo, transtype, UserID, uanbrpkgs);
						// Begin TungVH
						int noOfua = 0;
						noOfua = uAService.checkFirstUA(esnNo, cntrNbr);
						// if (noOfua <= 0) { //Commented by Jade to solve UA cancellation failure issue
						// in SIT
						if (chk && noOfua <= 0) { // Amended by Jade to solve UA cancellation failure issue in SIT
							String newCatCd = uAService.getNewCatCd(cntrSeq);
							uAService.changeStatusCntr(cntrSeq, UserID, newCatCd);
						}
						if (chk && !"".equals(CommonUtility.deNull(cntrSeq))) {
							if (uanbr.equals(uaFirst)) {
								uAService.cancel1stUa(cntrSeq, cntrNbr, UserID);
								// Timestamp dttmSecond = null;
								// dttmSecond = uAService.getUaSecond(cntrSeq, cntrNbr, UserID);
								// if (dttmSecond == null) {
								// have not any DN
								// uAService.changeStatusCntr(cntrSeq, UserID);
								// } else {
								// At least one DN
								// uAService.changeFirstUA(cntrSeq, cntrNbr, UserID, dttmSecond);
								// }
							}

							// VietNguyen added to implement logic cancel DN -> should be check Stuff cntr

							if (chk && !"".equals(CommonUtility.deNull(cntrSeq))) {
								// check ua balance
								boolean countBalance = uAService.countUABalance(cntrNbr);
								log.info("Cancel DN details :and do check update stuff cntr : ?? :" + countBalance);
								if (!countBalance) {
									uAService.updateCntrStatus(cntrSeq, UserID);
								}
								// do update cntr status
							}
							// long declrWt = Long.parseLong(criteria.getPredicates().get("declrWt"));
							// int declrPkgs = Integer.parseInt(criteria.getPredicates().get("declrPkgs"));
							// int tranQty = Integer.parseInt(criteria.getPredicates().get("tranQty"));
							// long weight = (tranQty*declrWt)/declrPkgs;
							// uAService.updateWeight(cntrSeq, weight, UserID, "ADD");
						}
						// Start added for SMART CR by FPT on 24-Jan-2014
						log.info("********Cancel UA: transtype=" + transtype);
						if (("E".equals(transtype) || "A".equals(transtype) || "C".equals(transtype)
								|| "B".equals(transtype))) {

							String refType = null;
							if ("E".equals(transtype)) {
								refType = "ESN";
							} else if ("A".equals(transtype)) {
								refType = "TESNJJ";
							} else if ("C".equals(transtype)) {
								refType = "TESNPJ";
							} else if ("B".equals(transtype)) {
								refType = "TESNJP";
							}
							String vvcd = uAService.getVcd(esnNo);
							log.info(
									"********Before SMART interface calling for to release the occupied area and restore the reservation for the affected number of packages: UA number="
											+ uanbr);
							try {

								double declrWt = Double.parseDouble(criteria.getPredicates().get("declrWt"));
								double declrPkgs = Double.parseDouble(criteria.getPredicates().get("declrPkgs"));
								int numberOfPackage = Integer.parseInt(uanbrpkgs);
								double uaWeight = ((double) (numberOfPackage * declrWt) / declrPkgs) / 1000;

								String serverIp = null;
								String serverNm = null;
								InetAddress jpOnlineAddress = InetAddress.getLocalHost();
								if (jpOnlineAddress != null) {
									serverIp = jpOnlineAddress.getHostAddress();
									serverNm = jpOnlineAddress.getHostName();
								}

								log.info("UA Cancel: serverIp    = " + serverIp);
								log.info("UA Cancel: serverName = " + serverNm);
								log.info("UA Cancel: number of package = " + uanbrpkgs);
								log.info("UA Cancel: tonnage = " + uaWeight);
								log.info("UA Cancel: vvCd = " + vvcd);
								log.info("UA Cancel: RefNbr = " + esnNo);
								log.info("UA Cancel: RefType = " + refType);

								SmartInterfaceInputVO inputObj = new SmartInterfaceInputVO();
								inputObj.setUserId(UserID);
								inputObj.setVvCd(vvcd);
								inputObj.setOccSrcCd(SmartInterfaceConstants.SOURCE_JP);
								inputObj.setActionCd(SmartInterfaceConstants.CANCEL_UA_EVENT_CD);
								inputObj.setServerIp(serverIp);
								inputObj.setServerNm(serverNm);
								inputObj.setClassNm(this.getClass().getName());
								inputObj.setClassDesc("UA cancel handler class");
								inputObj.setRefNbr(esnNo);
								inputObj.setRefType(refType);
								inputObj.setStayingInJP(true);
								inputObj.setNbrPkgs(Integer.parseInt(uanbrpkgs));
								inputObj.setTonnage(BigDecimal.valueOf(uaWeight));
								inputObj.setTransNbr(uanbr);
								String url = smartBaseUrl + "/releaseStorageOccupancy";
								log.info("[releaseStorageOccupancy] Calling SMART service URL= " + url);
								smartServiceRestClient.releaseStorageOccupancy(inputObj);
							} catch (Exception ex) {
								log.info("Exception: UAEsnCancel ", ex);
								throw new BusinessException("M4201");
							}
							log.info("********After SMART interface calling");
							// End added for SMART CR by FPT on 24-Jan-2014
						}
						// End TungVH
						esnView = uAService.getEsnView(esnNo, transtype);
						uaList = uAService.getUAList(esnNo);
						map.put("esnView", esnView);
						map.put("uaList", uaList);
					} else {
						errorMessage = ConstantUtil.ErrorMsg_Cannot_Cancel_UA;
					}
					String esncntr = criteria.getPredicates().get("esncntr");
					map.put("esncntr", esncntr);
				}
				map.put("screen", "UAEsnView");
			}

		} catch (BusinessException e) {
			log.info("Exception: UAEsnCancel ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception: UAEsnCancel", e);
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
				log.info("END: UAEsnCancel result: " + result.toString());
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

