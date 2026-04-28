package sg.com.jp.generalcargo.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import io.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.MiscCodeValueObject;
import sg.com.jp.generalcargo.domain.PassOutNoteFormValueObject;
import sg.com.jp.generalcargo.domain.PassOutNoteValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.PassOutNoteService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.CreateException;
import sg.com.jp.generalcargo.util.PassOutNoteConstant;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;
import org.springframework.web.util.HtmlUtils;



@CrossOrigin
@RestController
@RequestMapping(value = PassOutNoteController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class PassOutNoteController {
	
	@Value("${pdfDir.downloadfile.path}")
	private String tempFolder;
	
	@Value("${pdfDir.downloadfile.path}")
	private String pdfDir;

	public static final String ENDPOINT = "gc/passOutNote";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(PassOutNoteController.class);

	@Autowired
	private PassOutNoteService passOutNoteCustService;

	// jp.src.delegate.helper.gbms.ops.tenant.passOutNote-->PassOutNoteHandler

	// StartRegion PassOutNoteHandler

	// method: perform()
	@ApiOperation(value = "passOutNoteProcess", response = String.class)
	@RequestMapping(value = "/passOutNoteProcess", method = RequestMethod.POST)
	public ResponseEntity<?> passOutNoteFunction(HttpServletRequest request) {
		log.info("Entered method passOutNoteFunction -- httprequest is " + request.toString());
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map1 = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: passOutNoteProcess criteria:" + criteria.toString());

			String action = (String) criteria.getPredicates().get("actionSubmit");

			if (action == null || action.equals("")) {
				map.put("screen", PassOutNoteConstant.Screen.FRAME_SCR);
			} else {

				// Top
				if (PassOutNoteConstant.Action.TOP_ACT.equals(action)) {
					topProcess(criteria, map);
				}

				// List
				else if (PassOutNoteConstant.Action.LIST_ACT.equals(action)) {
					String rdSearch = (String) criteria.getPredicates().get("rdSearch");
					String dateFrom = (String) criteria.getPredicates().get("tbDateFrom");
					String dateTo = (String) criteria.getPredicates().get("tbDateTo");
					String passOutNoteNo = (String) criteria.getPredicates().get("tbPassOutNoteNo");
					String company = (String) criteria.getPredicates().get("cbTenantCompanyTop");
					String searchNew = (String) criteria.getPredicates().get("searchNew");
					String searchActive = (String) criteria.getPredicates().get("searchActive");

					map.put("rdSearch", rdSearch);
					map.put("tbDateFrom", dateFrom);
					map.put("tbDateTo", dateTo);
					map.put("searchNew", searchNew);
					map.put("tbPassOutNoteNo", passOutNoteNo);
					map.put("cbTenantCompanyTop", company);
					map.put("searchActive", searchActive);
					listProcess(request, map);
				}

				else if (PassOutNoteConstant.Action.LIST_CURRENT_ACT.equals(action)) {
					DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
					Date date = new Date();
					String currentDate = dateFormat.format(date);

					String userType = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

					if (!"JP".equals(userType)) {
						String companyCode = (String) criteria.getPredicates().get("companyCode");
						map.put("cbTenantCompanyTop", companyCode);
					} 

					map.put("rdSearch", "date");
					map.put("tbDateFrom", currentDate);
					map.put("tbDateTo", currentDate);
					map.put("searchNew", "1");

					listProcess(request, map);
				}

				// Create - Click on the Create button in the Top screen
				else if (PassOutNoteConstant.Action.CREATE_ACT.equals(action)) {
					createProcess(criteria, map);
				}

				// Create Submit
				else if (PassOutNoteConstant.Action.CREATE_SUBMIT_ACT.equals(action)) {
					createSubmitProcess(criteria, map);
				}

				// Create Confirm
				else if (PassOutNoteConstant.Action.CREATE_CONFIRM_ACT.equals(action)) {
					createConfirmProcess(criteria, map);
				}

				// Delete
				else if (PassOutNoteConstant.Action.DELETE_ACT.equals(action)) {
					deleteProcess(request, map);
				}

				// View
				else if (PassOutNoteConstant.Action.VIEW_ACT.equals(action)) {
					viewProcess(criteria, map);
				}
			}

			
		} catch (BusinessException e) {
		 	log.info("Exception passOutNoteProcess : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			if (errorMessage == null) {
				errorMessage = e.getMessage();
			}
		}catch (Exception e) {
			log.info("Exception: passOutNoteProcess",e);
			
			result.setSuccess(false);
			result.setError(CommonUtility.getExceptionMessage(e));
		} finally {
			if (errorMessage != null) {
				map1.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map1);
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: passOutNoteProcess result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// method: storeTenantCompanyList()
	// Get Tenant company List, and assign to request
	public void storeTenantCompanyList(Criteria criteria, Map<String, Object> map) throws BusinessException{
		log.info("START: storeTenantCompanyList  "+" criteria:"+criteria.toString() +" map:"+map);

		List<MiscCodeValueObject> companyVec = getTenantCompanyList();
		log.info("END: *** storeTenantCompanyList Result *****" + companyVec.size());
		map.put("companyVec", companyVec);
	}

	// method: getTenantCompanyList()
	// Get Tenant company List
	public List<MiscCodeValueObject> getTenantCompanyList() throws BusinessException{
		List<MiscCodeValueObject> companyVec = new ArrayList<MiscCodeValueObject>();
		try {
			log.info("START: getTenantCompanyList  ");
			companyVec = passOutNoteCustService.getTenantCompanyList();
			log.info("END: *** getTenantCompanyList Result *****" + companyVec.size());
		} catch (BusinessException e) {
		 	log.info("Exception getTenantCompanyList : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			if (errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception: getTenantCompanyList ", e);
			
		}
		return companyVec;
	}

	// method: topProcess()
	/**
	 * topProcess Process for Top screen
	 * 
	 * @param request
	 */

	public void topProcess(Criteria criteria, Map<String, Object> map)  throws BusinessException{

		Result result = new Result();

		try {
			log.info("START: topProcess criteria:" + criteria.toString() +" map:"+map);

			// get company list
			storeTenantCompanyList(criteria, map);

			// get user type
			String userType = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			map.put("userType", userType);

			// get current date
			DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
			Date date = new Date();
			String currentDate = dateFormat.format(date);
			map.put("currentDate", currentDate);

			// Transfer to screen
			map.put("screen", PassOutNoteConstant.Screen.TOP_SCR);
		} catch (BusinessException e) {
		 	log.info("Exception topProcess : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			if (errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception: topProcess ", e);
			
			
			result.setSuccess(false);
			result.setError(CommonUtility.getExceptionMessage(e));
		}
		log.info("END: topProcess result: " + result.toString());

	}

	// method: createProcess()
	/**
	 * createProcess Process when click Create button - goto Create screen
	 * 
	 * @param request
	 */

	public void createProcess(Criteria criteria, Map<String, Object> map) throws BusinessException{
		Result result = new Result();
		try {
			log.info("START: createProcess criteria:" + criteria.toString() +" map:"+map);
			storeTenantCompanyList(criteria, map);

			String userType = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			map.put("userType", userType);

			map.put("screen", PassOutNoteConstant.Screen.CREATE_SCR);
		} catch (BusinessException e) {
		 	log.info("Exception createProcess : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			if (errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception:  createProcess", e);
		
			result.setSuccess(false);
			result.setError(CommonUtility.getExceptionMessage(e));
		}
		log.info("END: createProcess result: " + result.toString());

	}

	// method: createSubmitProcess()
	/**
	 * createSubmitProcess Process when click Submit button in the Create Screen
	 * 
	 * @param request
	 * @throws RemoteException
	 * @throws CreateException
	 */
	@ApiOperation(value = "createSubmitProcess", response = String.class)
	@RequestMapping(value = "/createSubmitProcess", method = RequestMethod.POST)
	public void createSubmitProcess(Criteria criteria, Map<String, Object> map)  throws BusinessException{
		Result result = new Result();
		try {
			log.info("START: createSubmitProcess criteria:" + criteria.toString() +" map:"+map);

			String tbPlateNo = ("" + criteria.getPredicates().get("tbPlateNo")).trim();
			String tbPassNo = CommonUtility.deNull(criteria.getPredicates().get("tbPassNo")).trim();
			String tbNoPkg = criteria.getPredicates().get("tbNoPkg").trim();
			String taCargoMarks = ("" + criteria.getPredicates().get("taCargoMarks")).trim();
			String taCargoDesc = ("" + criteria.getPredicates().get("taCargoDesc")).trim();
			String taRemarks = CommonUtility.deNull(criteria.getPredicates().get("taRemarks")).trim();
			//53711_PB-86 Creation of PON in JPOM - when user used special character '&', system display distorted details &amp; - start
			taCargoMarks = HtmlUtils.htmlUnescape(taCargoMarks);
			taCargoDesc = HtmlUtils.htmlUnescape(taCargoDesc);
			log.info("CargoMarks: " + taCargoMarks + " CargoDesc: " + taCargoDesc );
			//53711_PB-86 Creation of PON in JPOM - when user used special character '&', system display distorted details &amp; - end
			
			String cbCompany = ("" + criteria.getPredicates().get("cbTenantCompany")).trim();
						
			String tbNameDecl = ("" + criteria.getPredicates().get("tbNameDecl")).trim();
			String tbNric = ("" + criteria.getPredicates().get("tbNric")).trim();
			String tbCompanyDecl = ("" + criteria.getPredicates().get("tbCompanyDecl")).trim();
			String tbConsignee = ("" + criteria.getPredicates().get("tbConsignee")).trim();
			//53711_PB-86 Creation of PON in JPOM - when user used special character '&', system display distorted details &amp; - start
			tbConsignee = HtmlUtils.htmlUnescape(tbConsignee);
			tbNameDecl = HtmlUtils.htmlUnescape(tbNameDecl);
			tbCompanyDecl = HtmlUtils.htmlUnescape(tbCompanyDecl);
			log.info("Consignee: " + tbConsignee + " NameDecl: " + tbNameDecl + " CompanyDecl: " + tbCompanyDecl);
			//53711_PB-86 Creation of PON in JPOM - when user used special character '&', system display distorted details &amp; - end
			
			String userName = CommonUtility.deNull(criteria.getPredicates().get("userName"));
			String loginUser = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String userType = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String companyName = null;
			if ("JP".equals(userType)) {
				List<MiscCodeValueObject> companyList = getTenantCompanyList();
				if (cbCompany != null && !cbCompany.equals("")) {
					for (int i = 0; i < companyList.size(); i++) {
						MiscCodeValueObject miscCodeVO = (MiscCodeValueObject) companyList.get(i);
						if (cbCompany.equals(miscCodeVO.getTypeCode())) {
							companyName = miscCodeVO.getTypeName();
							break;
						}
					}
				}
			} else {
				cbCompany = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				companyName = passOutNoteCustService.getCompanyName(cbCompany);
			}

			// DatVT1 apply change - 2010.02.26
			String driverName = "";
			log.info( "Drivers pass no  " +tbPassNo.isEmpty());
			if (tbPassNo != null && !tbPassNo.equals("")) {
				// Get Driver Name from Driver Pass No
				driverName = passOutNoteCustService.getDriverName(tbPassNo);
				if (driverName == null || "".equals(driverName)) {
					driverName = "NOT FOUND";
					errorMessage =ConstantUtil.PASS_OUT_CONSTANT_MAP.get("M1010");
				}
			}
			// DatVT1 end

			String DATE_FORMAT_NOW = "ddMMyyyy HHmm";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_NOW);
			String dateTime = simpleDateFormat.format(new Date());

			PassOutNoteFormValueObject passOutNoteFormVO = new PassOutNoteFormValueObject();

			PassOutNoteValueObject passOutNoteVO = new PassOutNoteValueObject();
			passOutNoteVO.setTruckPlaceNo(tbPlateNo);
			passOutNoteVO.setDriverPassNo(tbPassNo);
			passOutNoteVO.setNoOfPkgs(tbNoPkg);
			passOutNoteVO.setCargoMarks(taCargoMarks);
			passOutNoteVO.setCargoDesc(taCargoDesc);
			passOutNoteVO.setRemarks(taRemarks);
			passOutNoteVO.setCompanyName(companyName);
			passOutNoteVO.setCompanyCode(cbCompany);
			passOutNoteVO.setCreatedBy(userName);
			passOutNoteVO.setDriverName(driverName);
			passOutNoteVO.setDateTime(dateTime);
			passOutNoteVO.setConsignee(tbConsignee);
			passOutNoteVO.setNameDecl(tbNameDecl);
			passOutNoteVO.setCompanyDecl(tbCompanyDecl);
			passOutNoteVO.setNricNo(tbNric);
			passOutNoteVO.setLoginUser(loginUser);
			passOutNoteFormVO.setPassOutNoteValueObject(passOutNoteVO);

			map.put("passOutNoteFormVO", passOutNoteFormVO);

			map.put("screen", PassOutNoteConstant.Screen.CREATE_CONFIRM_SCR);

		} catch (BusinessException e) {
		 	log.info("Exception createSubmitProcess : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			if (errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception:  createSubmitProcess ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			result.setSuccess(false);
			result.setError(CommonUtility.getExceptionMessage(e));
		}
		log.info("END: createSubmitProcess result: " + result.toString());

	}

	// method: createConfirmProcess()
	/**
	 * createConfirmProcess Process when click Confirm button in the Submit screen
	 * 
	 * @param request
	 */
	@ApiOperation(value = "createConfirmProcess", response = String.class)
	@RequestMapping(value = "/createConfirmProcess", method = RequestMethod.POST)
	public void createConfirmProcess(Criteria criteria, Map<String, Object> map)  throws BusinessException{
		Result result = new Result();
		try {
			log.info("START: createConfirmProcess criteria:" + criteria.toString() +" map:"+map);
			
			//PON JPOM Migration Issue - Changes Start
			String companyName = (String) criteria.getPredicates().get("companyName");
			String coCd = passOutNoteCustService.getCompanyCode(companyName);;
			//PON JPOM Migration Issue - Changes end
			
			String tokenCocd = (String) criteria.getPredicates().get("companyCode");
			String companyCode = "";
			
			PassOutNoteValueObject passOutNoteVO = new PassOutNoteValueObject();
			String dateTime = (String) criteria.getPredicates().get("dateTime");
			String createdBy = (String) criteria.getPredicates().get("createdBy");
			
			if(tokenCocd.equalsIgnoreCase("JP")) {
				companyCode = coCd;
			} else {
				companyCode = tokenCocd;
			}
			
			String placeNo = (String) criteria.getPredicates().get("placeNo");
			String passNo = (String) criteria.getPredicates().get("passNo");
			String driverName = (String) criteria.getPredicates().get("dirverName");
			String noOfPkgs = (String) criteria.getPredicates().get("noOfPkgs");
			String cargoMarks = (String) criteria.getPredicates().get("cargoMarks");
			String cargoDesc = (String) criteria.getPredicates().get("cargoDesc");
			String remark = (String) criteria.getPredicates().get("remark");
			String nameDecl = (String) criteria.getPredicates().get("nameDecl");
			String nricNo = (String) criteria.getPredicates().get("nricNo");
			String companyDecl = (String) criteria.getPredicates().get("companyDecl");
			String consignee = (String) criteria.getPredicates().get("consignee");
			String loginUser = (String) criteria.getPredicates().get("loginUser");

			passOutNoteVO.setDateTime(dateTime);
			passOutNoteVO.setCreatedBy(createdBy);
			passOutNoteVO.setCompanyCode(companyCode);
			passOutNoteVO.setCompanyName(companyName);
			passOutNoteVO.setTruckPlaceNo(placeNo);
			passOutNoteVO.setDriverPassNo(passNo);
			passOutNoteVO.setDriverName(driverName);
			passOutNoteVO.setNoOfPkgs(noOfPkgs);
			passOutNoteVO.setCargoDesc(cargoDesc);
			passOutNoteVO.setCargoMarks(cargoMarks);
			passOutNoteVO.setRemarks(remark);
			passOutNoteVO.setStatus("A");
			passOutNoteVO.setConsignee(consignee);
			passOutNoteVO.setNameDecl(nameDecl);
			passOutNoteVO.setNricNo(nricNo);
			passOutNoteVO.setCompanyDecl(companyDecl);
			passOutNoteVO.setLoginUser(loginUser);

			PassOutNoteFormValueObject passOutNoteFormVO = new PassOutNoteFormValueObject();
			passOutNoteFormVO.setPassOutNoteValueObject(passOutNoteVO);

			boolean result1 = passOutNoteCustService.createPassOutNote(passOutNoteFormVO);

			if (!result1) {
				
				errorMessage =ConstantUtil.PASS_OUT_CONSTANT_MAP.get("M1012");
			}
			map.put("passOutNoteFormVO", passOutNoteFormVO);

			map.put("screen", PassOutNoteConstant.Screen.CREATE_VIEW_SCR);
		} catch (BusinessException e) {
		 	log.info("Exception createConfirmProcess : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			if (errorMessage == null) {
				errorMessage = e.getMessage();
			}
		}  catch (Exception e) {
			log.info("Exception: createConfirmProcess ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			result.setSuccess(false);
			result.setError(CommonUtility.getExceptionMessage(e));
		}
		log.info("END: createConfirmProcess result: " + result.toString());

	}

	// method: listProcess()
	/**
	 * listProcess Process when click Search button or return to List Screen
	 * 
	 * @param request
	 */

	public void listProcess(HttpServletRequest request, Map<String, Object> map, PassOutNoteFormValueObject passOutNoteFormVO)  throws BusinessException{
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		TableResult tableresult = new TableResult();
		try {
			log.info("START: listProcess criteria:" + criteria.toString()+" map:"+map +" passOutNoteFormVO:"+passOutNoteFormVO.toString());

			String dateFrom = criteria.getPredicates().get("tbDateFrom");
			String dateTo = criteria.getPredicates().get("tbDateTo");
			String passOutNoteNo = criteria.getPredicates().get("tbPassOutNoteNo");
			String seachCompanyCode = criteria.getPredicates().get("cbTenantCompanyTop");
			String searchCompanyName = "";
			String searchNew = criteria.getPredicates().get("searchNew");
			String tmpSearchActive = criteria.getPredicates().get("searchActive");

			boolean searchActive = false;
			if (tmpSearchActive != null && !tmpSearchActive.equals("") && !tmpSearchActive.equals("false")) {
				searchActive = true;
			}


			if (searchNew != null && searchNew != "") {

				String userType = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

				// If this user is tenant
				if (!"JP".equals(userType)) {
					seachCompanyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				}

				List<MiscCodeValueObject> companyList = getTenantCompanyList();
				if (seachCompanyCode != null && !seachCompanyCode.equals("")) {
					for (int i = 0; i < companyList.size(); i++) {
						MiscCodeValueObject miscCodeVO = (MiscCodeValueObject) companyList.get(i);
						if (seachCompanyCode.equals(miscCodeVO.getTypeCode())) {
							searchCompanyName = miscCodeVO.getTypeName();
							break;
						}
					}
				}

				PassOutNoteValueObject passOutNoteVO = new PassOutNoteValueObject();
				passOutNoteVO.setSearchCompanyCode(seachCompanyCode);
				passOutNoteVO.setSearchCompanyName(searchCompanyName);
				passOutNoteVO.setSearchDateFrom(dateFrom);
				passOutNoteVO.setSearchDateTo(dateTo);
				passOutNoteVO.setSearchPassOutNoteNo(passOutNoteNo);
				passOutNoteVO.setSearchActive(searchActive);

				passOutNoteFormVO.setPassOutNoteValueObject(passOutNoteVO);

				log.info("passOutNoteFormVO " + passOutNoteFormVO.toString());
				tableresult = passOutNoteCustService.searchPassOutNote(passOutNoteFormVO, criteria);
				passOutNoteFormVO =  (PassOutNoteFormValueObject) tableresult.getData().getListData().getTopsModel().get(0);

				map.put("passOutNoteVOList", passOutNoteFormVO.getPassOutNoteValueObjectList());
				map.put("passOutNoteVO", passOutNoteFormVO);
				passOutNoteFormVO.setPassOutNoteValueObjectList(passOutNoteFormVO.getPassOutNoteValueObjectList());
				map.put("searchCompanyName", passOutNoteFormVO.getPassOutNoteValueObject().getSearchCompanyName());
				map.put("passOutNoteFormVO", passOutNoteFormVO);
				map.put("total", tableresult.getData().getTotal());
			} else {				
				tableresult = passOutNoteCustService.searchPassOutNote(passOutNoteFormVO, criteria);
				passOutNoteFormVO =  (PassOutNoteFormValueObject) tableresult.getData().getListData().getTopsModel().get(0);
				passOutNoteFormVO.setPassOutNoteValueObjectList(passOutNoteFormVO.getPassOutNoteValueObjectList());

				map.put("passOutNoteFormVO", passOutNoteFormVO);
				map.put("searchCompanyName", passOutNoteFormVO.getPassOutNoteValueObject().getSearchCompanyName());
				map.put("total", tableresult.getData().getTotal());
			}
			map.put("screen", PassOutNoteConstant.Screen.LIST_SCR);
		} catch (BusinessException e) {
		 	log.info("Exception listProcess : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception:  listProcess", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get("M4201");
		}
		log.info("END: listProcess result: " + result.toString());

	}

	// method: deleteProcess()
	/**
	 * deleteProcess
	 * 
	 * @param request
	 * @return
	 */

	public void deleteProcess(HttpServletRequest request, Map<String, Object> map)  throws BusinessException{

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();

		try {
			log.info("START: deleteProcess criteria:" + criteria.toString()+" map:"+map);

			String selectedPassOutNote = (String) criteria.getPredicates().get("selectedPassOutNote");
			if (selectedPassOutNote == null || selectedPassOutNote.equals("")) {
				// Cant delete a empty object
				map.put("screen", PassOutNoteConstant.Screen.LIST_SCR);
			} else {

				String[] selectedPassOutNoteAr = selectedPassOutNote.split(";");
				PassOutNoteFormValueObject passOutNoteFormVO = new PassOutNoteFormValueObject();
				passOutNoteFormVO.setSelectedPassOutNote(selectedPassOutNoteAr);

				boolean canDelete = passOutNoteCustService.checkDeletedPassOutNote(passOutNoteFormVO);
				if (!canDelete) {
					
					errorMessage =ConstantUtil.PASS_OUT_CONSTANT_MAP.get("M1014");
				} else {
					String deleteRemark = criteria.getPredicates().get("deleteRemark");
					if (deleteRemark == null) {
						deleteRemark = "";
					}

					String userName = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
					String companyName = CommonUtility.deNull(criteria.getPredicates().get("companyName"));
					String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
					String searchActive = CommonUtility.deNull(criteria.getPredicates().get("searchActive"));
					PassOutNoteValueObject passOutNoteVO = new PassOutNoteValueObject();
					passOutNoteVO.setCreatedBy(userName);
					passOutNoteVO.setDeleteRemark(deleteRemark);
					passOutNoteVO.setCompanyName(companyName);
					passOutNoteVO.setCompanyCode(companyCode);

					passOutNoteFormVO.setPassOutNoteValueObject(passOutNoteVO);

					boolean result1 = passOutNoteCustService.deletePassOutNote(passOutNoteFormVO);

					if (!result1) {
					
						errorMessage =ConstantUtil.PASS_OUT_CONSTANT_MAP.get("M0010");
					} else {
						String searchCompanyCode = passOutNoteFormVO.getPassOutNoteValueObject().getSearchCompanyCode();
						String searchDateFrom = passOutNoteFormVO.getPassOutNoteValueObject().getSearchDateFrom();
						String searchDateTo = passOutNoteFormVO.getPassOutNoteValueObject().getSearchDateTo();
						String searchPassOutNoteNo = passOutNoteFormVO.getPassOutNoteValueObject()
								.getSearchPassOutNoteNo();

						map.put("cbTenantCompanyTop", searchCompanyCode);
						map.put("tbDateFrom", searchDateFrom);
						map.put("tbDateTo", searchDateTo);
						map.put("tbPassOutNoteNo", searchPassOutNoteNo);
						map.put("searchActive", "" + searchActive);
						map.put("searchNew", "1");

						listProcess(request, map, passOutNoteFormVO);
					}
				}
			}
		} catch (BusinessException e) {
		 	log.info("Exception deleteProcess : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			if (errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception:  deleteProcess ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			result.setSuccess(false);
			result.setError(CommonUtility.getExceptionMessage(e));
		}
		log.info("END: deleteProcess result: " + result.toString());

	}

	// method: viewProcess()
	/**
	 * View
	 * 
	 * @param request
	 */

	public void viewProcess(Criteria criteria, Map<String, Object> map)  throws BusinessException{
		Result result = new Result();
		try {
			log.info("START: viewProcess criteria:" + criteria.toString()+" map:"+map);

			String passOutNoteId = (String) criteria.getPredicates().get("viewPassOutNoteId");

			if (passOutNoteId == null || passOutNoteId.equals("")) {
			
				errorMessage =ConstantUtil.PASS_OUT_CONSTANT_MAP.get("M0010");
			} else {
				PassOutNoteValueObject passOutNoteVO = new PassOutNoteValueObject();
				passOutNoteVO.setPassOutNoteId(passOutNoteId);
				PassOutNoteFormValueObject passOutNoteFormVO = new PassOutNoteFormValueObject();
				passOutNoteFormVO.setPassOutNoteValueObject(passOutNoteVO);

				passOutNoteFormVO = passOutNoteCustService.viewPassOutNote(passOutNoteFormVO);

				map.put("passOutNoteFormVO", passOutNoteFormVO);

				map.put("screen", PassOutNoteConstant.Screen.VIEW_SCR);
			}

		} catch (BusinessException e) {
		 	log.info("Exception viewProcess : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			if (errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception: viewProcess ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			
			result.setSuccess(false);
			result.setError(CommonUtility.getExceptionMessage(e));
		}
		log.info("END: viewProcess result: " + result.toString());

	}
	
	
	//PassOutNotePrintReportServlet method processRequest
	@ApiOperation(value = "printRecord", response = String.class)
	@RequestMapping(value = "/printRecord", method = RequestMethod.POST)
	public ResponseEntity<?> printRecord(HttpServletRequest request, HttpServletResponse response)  throws BusinessException{
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: printRecord criteria:" + criteria.toString());
	
			String ponId = CommonUtil.deNull(criteria.getPredicates().get("viewPassOutNoteId")).trim();  
			
			PassOutNoteValueObject passOutNoteVO = new PassOutNoteValueObject();
			passOutNoteVO.setPassOutNoteId(ponId);
			PassOutNoteFormValueObject passOutNoteFormVO = new PassOutNoteFormValueObject();
			passOutNoteFormVO.setPassOutNoteValueObject(passOutNoteVO);

			List<Map<String,Object>> records = passOutNoteCustService.printPassOutNote(passOutNoteFormVO);

			String tempFolder = this.tempFolder;
			if (tempFolder == null || "".equalsIgnoreCase(tempFolder)) {
				tempFolder = this.tempFolder;
			}

			String imageFilename = FilenameUtils.normalize(tempFolder + "/barcode_" + ponId + ".png");
			generateCode39Writer(imageFilename, ponId);
			
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("PON_ID",ponId);
			parameters.put("barcodeImage", imageFilename);
			
			
			if (log.isInfoEnabled()) {
				log.info("param: " + parameters);
				log.info("ponId: " + ponId);
				log.info("imgFile: " + imageFilename);
			}

			// Create connection 
			JasperPrint jasperPrint = passOutNoteCustService.jasperPrint(parameters, "PONPrint.jrxml" ,ponId,records);
			
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
			Date date = new Date();
			String fileName = FilenameUtils.normalize(pdfDir + "/" + ponId.toUpperCase()+"_"+dateTimeFormat.format(date)+".pdf");
			
			if (log.isInfoEnabled()) {
				log.info("=== Printing: " + fileName + "/" + ponId);
			}
		
			JRPdfExporter exporter = new JRPdfExporter();
			SimpleOutputStreamExporterOutput c = new SimpleOutputStreamExporterOutput(fileName);
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(c);
			exporter.exportReport();
			FileInputStream st = null;
			
			if (!new File(fileName).exists()) {
				log.info("filepath validation failed! - " + fileName);
				throw new BusinessException("filepath validation failed!");
			}
			
			
			st = new FileInputStream(fileName);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"" + fileName + "\"")
					.body(new InputStreamResource(st));

			
		} catch (BusinessException e) {
		 	log.info("Exception printRecord : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));			
			if (errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception:  printRecord ", e);
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
			}
			log.info("END: printRecord Result" + result.toString());
		}

		return ResponseEntityUtil.success(result.toString());
	}
	
	public static void generateCode39Writer(String barcodeText, String nbr) throws Exception {
		try {
			log.info("START: generateCode39Writer "+" barcodeText:"+CommonUtility.deNull(barcodeText) +" nbr:"+CommonUtility.deNull(nbr) );
			File outputfile = new File(barcodeText);
			BitMatrix bitMatrix = new MultiFormatWriter().encode(nbr, BarcodeFormat.CODE_128, 0, 0);
			BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
			ImageIO.write(bufferedImage, "png", outputfile);
		} catch (Exception e) {
			log.info("Exception: generateCode39Writer ", e);
		}

	}
	
	@RequestMapping(value = "/getCompanyName", method = RequestMethod.POST)
	public ResponseEntity<?> getCompanyName(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: get company name");
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getCompanyName "+" criteria:"+criteria.toString() );
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userAcct = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			map.put("userAcct", userAcct);
			map.put("companyCode", companyCode);
			
		} catch (Exception e) {
			log.info("Exception getCompanyName : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: get company name Result" + result.toString());
			}
		
		}
		return ResponseEntityUtil.success(result.toString());
	}

	
	// method: listProcess()
	/**
	 * listProcess Process when click Search button or return to List Screen
	 * 
	 * @param request
	 */

	public void listProcess(HttpServletRequest request, Map<String, Object> map)  throws BusinessException{
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		TableResult tableresult = new TableResult();
		try {
			log.info("START: listProcess criteria:" + criteria.toString()+" map:"+map);

			String dateFrom = criteria.getPredicates().get("tbDateFrom");
			String dateTo = criteria.getPredicates().get("tbDateTo");
			String passOutNoteNo = criteria.getPredicates().get("tbPassOutNoteNo");
			String seachCompanyCode = criteria.getPredicates().get("cbTenantCompanyTop");
			String searchCompanyName = "";
			String searchNew = criteria.getPredicates().get("searchNew");
			String tmpSearchActive = criteria.getPredicates().get("searchActive");

			boolean searchActive = false;
			if (tmpSearchActive != null && !tmpSearchActive.equals("") && !tmpSearchActive.equals("false")) {
				searchActive = true;
			}

			PassOutNoteFormValueObject passOutNoteFormVO = new PassOutNoteFormValueObject();

			if (searchNew != null && searchNew != "") {

				String userType = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

				// If this user is tenant
				if (!"JP".equals(userType)) {
					seachCompanyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				} 

				List<MiscCodeValueObject> companyList = getTenantCompanyList();
				if (seachCompanyCode != null && !seachCompanyCode.equals("")) {
					for (int i = 0; i < companyList.size(); i++) {
						MiscCodeValueObject miscCodeVO = (MiscCodeValueObject) companyList.get(i);
						if (seachCompanyCode.equals(miscCodeVO.getTypeCode())) {
							searchCompanyName = miscCodeVO.getTypeName();
							break;
						}
					}
				}

				PassOutNoteValueObject passOutNoteVO = new PassOutNoteValueObject();
				passOutNoteVO.setSearchCompanyCode(seachCompanyCode);
				passOutNoteVO.setSearchCompanyName(searchCompanyName);
				passOutNoteVO.setSearchDateFrom(dateFrom);
				passOutNoteVO.setSearchDateTo(dateTo);
				passOutNoteVO.setSearchPassOutNoteNo(passOutNoteNo);
				passOutNoteVO.setSearchActive(searchActive);

				passOutNoteFormVO.setPassOutNoteValueObject(passOutNoteVO);

				log.info("passOutNoteFormVO " + passOutNoteFormVO.toString());
				tableresult = passOutNoteCustService.searchPassOutNote(passOutNoteFormVO, criteria);
				passOutNoteFormVO =  (PassOutNoteFormValueObject) tableresult.getData().getListData().getTopsModel().get(0);

				map.put("passOutNoteVOList", passOutNoteFormVO.getPassOutNoteValueObjectList());
				map.put("passOutNoteVO", passOutNoteFormVO);
				passOutNoteFormVO.setPassOutNoteValueObjectList(passOutNoteFormVO.getPassOutNoteValueObjectList());
				map.put("searchCompanyName", passOutNoteFormVO.getPassOutNoteValueObject().getSearchCompanyName());
				map.put("passOutNoteFormVO", passOutNoteFormVO);
				map.put("total", tableresult.getData().getTotal());
			} else {				
				tableresult = passOutNoteCustService.searchPassOutNote(passOutNoteFormVO, criteria);
				passOutNoteFormVO =  (PassOutNoteFormValueObject) tableresult.getData().getListData().getTopsModel().get(0);
				passOutNoteFormVO.setPassOutNoteValueObjectList(passOutNoteFormVO.getPassOutNoteValueObjectList());

				map.put("passOutNoteFormVO", passOutNoteFormVO);
				map.put("searchCompanyName", passOutNoteFormVO.getPassOutNoteValueObject().getSearchCompanyName());
				map.put("total", tableresult.getData().getTotal());
			}
			map.put("screen", PassOutNoteConstant.Screen.LIST_SCR);
		} catch (BusinessException e) {
		 	log.info("Exception listProcess : ", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception:  listProcess", e);
			errorMessage = ConstantUtil.PASS_OUT_CONSTANT_MAP.get("M4201");
		}
		log.info("END: listProcess result: " + result.toString());

	}
	// EndRegion PassOutNoteHandler
}
