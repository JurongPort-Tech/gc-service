package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import sg.com.jp.generalcargo.domain.CargoDocUpload;
import sg.com.jp.generalcargo.domain.CargoDocUploadDetail;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.CargoDocumentService;
import sg.com.jp.generalcargo.service.InwardCargoManifestService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = CargoDocumentContoller.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CargoDocumentContoller {

	@Autowired
	private CargoDocumentService cargoDocService;

	@Autowired
	private InwardCargoManifestService manifestService;

	public static final String ENDPOINT = "gc/cargodoc";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(CargoDocumentContoller.class);

	// Region CargoDocUpload

	@PostMapping("/getVesselInfo")
	public ResponseEntity<?> getVesselInfo(HttpServletRequest request) {

		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			errorMessage = null;
			log.info("START: getVesselInfo Start criteria :" + criteria.toString());
			TopsModel topsModel = new TopsModel();
			String coCD = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			List<VesselVoyValueObject> vesselCallList = manifestService.getVesselVoy(coCD);

			for (int i = 0; i < vesselCallList.size(); i++) {
				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj = (VesselVoyValueObject) vesselCallList.get(i);
				topsModel.put(vvvObj);
			}
			ArrayList<String> terminallist = new ArrayList<String>();
			ArrayList<String> vsllist = new ArrayList<String>();
			ArrayList<String> varlist = new ArrayList<String>();
			ArrayList<String> voylist = new ArrayList<String>();
			//Added By NS
			ArrayList<String> inVoylist = new ArrayList<String>();
			ArrayList<String> outVoylist = new ArrayList<String>();
			for (int i = 0; i < topsModel.getSize(); i++) {
				VesselVoyValueObject vvobj = new VesselVoyValueObject();
				String terminal = "";
				String vname = "";
				String voyno = "";
				String varnbr = "";
				String inVoyNo = "";
				String outVoyNo = "";
				vvobj = (VesselVoyValueObject) topsModel.get(i);
				// Changed by LongDh09::Start
				if (vvobj != null) {
					terminal = vvobj.getTerminal();
					vname = vvobj.getVslName();
					voyno = vvobj.getVoyNo();
					varnbr = vvobj.getVarNbr();
					inVoyNo	=vvobj.getInVoyNo();
					outVoyNo =vvobj.getOutVoyNo();
				}
				// Changed by LongDh09::End
				terminallist.add(terminal);
				vsllist.add(vname);
				voylist.add(voyno);
				varlist.add(varnbr);
				inVoylist.add(inVoyNo);
				outVoylist.add(outVoyNo);
			}
			map.put("VslTerminal", terminallist);
			map.put("VslName", vsllist);
			map.put("VoyNo", voylist);
			map.put("VarNo", varlist);
			map.put("InVoyNo", inVoylist);
			map.put("OutVoyNo", outVoylist);

		} catch (BusinessException e) {
			log.info("Exception getVesselInfo : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if(errorMessage == null) {
				errorMessage = e.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception getVesselInfo : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}

			log.info("END: getVesselInfo  result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}


	@RequestMapping(value = "/getCargoDocUploadDetails", method = RequestMethod.POST)
	public ResponseEntity<?> getCargoDocUploadDetails(HttpServletRequest request) {
		Result result = new Result();
		CargoDocUpload obj = null;
		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			result.setSuccess(true);
			log.info("START: getCargoDocUploadDetails Start criteria :" + criteria.toString());
			obj = cargoDocService.getCargoDocUploadDetails(criteria);
			if(obj!=null)
			{
				String vvCd =obj.getVvCd();
				String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				
				if(coCd.equalsIgnoreCase("JP") || coCd.equalsIgnoreCase(obj.getCreatedCustCode()))
				{
					Boolean isSubmissionAllowed=cargoDocService.isDocSubmissionAllowed(vvCd,coCd);
					obj.setIsSubmissionAllowed(isSubmissionAllowed);
					result.setData(obj);
					result.setSuccess(true);
					
				}
				else
				{
					result.setSuccess(false);
					result.setError("Access Denied!");
				}
				
			
			}
			else
			{
				result.setSuccess(false);
				result.setError("Invalid Vessel Name or voyage Number");
			}
		
		} catch (BusinessException e) {
			log.info("Exception getCargoDocUploadDetails : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if(errorMessage == null) {
				errorMessage = e.getMessage();
			}	
		} catch (Exception ex) {
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
			log.info("Exception getCargoDocUploadDetails : ", ex);
		
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(obj);
				result.setSuccess(true);
			}
			log.info("END: getCargoDocUploadDetails END.  Response :" + result.toString());
			
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@RequestMapping(value = "/getCargoDocUploadAuditInfo", method = RequestMethod.POST)
	public ResponseEntity<?> getCargoDocUploadAuditInfo(HttpServletRequest request) {
		TableResult result = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START:  getCargoDocUploadAuditInfo Start criteria :" + criteria.toString());
			
			log.info("Params:" + criteria.toString());

			result = cargoDocService.getCargoDocUploadAuditInfo(criteria);

			log.info("getCargoDocUploadAuditInfo:" + result.toString());
			
		} catch (BusinessException e) {
			log.info("Exception getCargoDocUploadAuditInfo : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if(errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception getCargoDocUploadAuditInfo " , e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setSuccess(false);
			} else {
				result.setSuccess(true);
			}
			log.info("END: getCargoDocUploadAuditInfo  Response :" + result.toString() );
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping("/saveCargoDocUpload")
	public ResponseEntity<?> saveCargoDocUpload(HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START : saveCargoDocUpload criteria:" + criteria.toString());
			result = cargoDocService.saveCargoDocUpload(criteria);
			
		} catch (BusinessException e) {
			log.info("Exception saveCargoDocUpload : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if(errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
			log.info("Exception saveCargoDocUpload:", e);
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(null);
			} else {
				result.setSuccess(true);
			}
			log.info("END: saveCargoDocUpload Result" + result.toString());
		}
		return ResponseEntityUtil.success(result);
	}

	@PostMapping(value = "/saveCargoDocUploadDetail")
	//public ResponseEntity<?> saveCargoDocUploadDetail(@RequestParam(value = "file", required = false) MultipartFile uploadingFile, HttpServletRequest request) {
	public ResponseEntity<?> saveCargoDocUploadDetail(@RequestParam("file") MultipartFile[] uploadingFiles,
				HttpServletRequest request) {
		String fileName = null;
		Result result = new Result();

		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: saveCargoDocUploadDetail criteria :" + criteria.toString());

			if (uploadingFiles.length > 0) {
				log.info("Attachment :\n " + uploadingFiles.toString() + ", length :"
						+ uploadingFiles.length + "  Name :" + uploadingFiles[0].getOriginalFilename());
			} else {
				log.info("Attachment : No Files " + ", length :" + uploadingFiles.length);
			}
		

			CargoDocUploadDetail obj = new CargoDocUploadDetail();

			obj.setDocumentType(criteria.getPredicates().get("documentType"));
			obj.setDocumentTypeCD(criteria.getPredicates().get("documentTypeCD"));
			obj.setUploadedBy(criteria.getPredicates().get("uploadedBy"));
			obj.setUploadedDate(criteria.getPredicates().get("uploadedDate"));
			obj.setUpdateFlag(criteria.getPredicates().get("updateFlag"));
			obj.setUploadedBy(criteria.getPredicates().get("userAccount"));
			obj.setVvCd(criteria.getPredicates().get("vvCd"));
			boolean isFileExist = Boolean.valueOf(criteria.getPredicates().get("isFileExist"));
			
			
			if (uploadingFiles.length > 0) {
				for (int i = 0; i < uploadingFiles.length; i++) {
					log.info("Attachment : " + uploadingFiles[i].toString());
					obj.setActualFileName(uploadingFiles[i].getOriginalFilename());
					obj.setFileType(uploadingFiles[i].getContentType());
					obj.setFileSize(uploadingFiles[i].getSize());
					fileName = cargoDocService.fileUpload(uploadingFiles[i], obj.getVvCd());
					log.info(" file uploded fileName :" + fileName);
					obj.setAssignedFileName(fileName);
				}
				
				
			} else if (isFileExist) {
				result.setSuccess(true);
				log.info("Returned Successfully:" + result.toString());
				return ResponseEntityUtil.success(result.toString());
			} else {
				obj.setUploadedDate(criteria.getPredicates().get("uploadedDate"));
			}
			cargoDocService.saveCargoDocUploadDetail(obj);		
			result.setSuccess(true);
			
		} catch (BusinessException e) {
			log.info("Exception saveCargoDocUploadDetail : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if(errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception ex) {
			log.info("Exception saveCargoDocUploadDetail DUCNA2(VSRAH): ", ex);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setSuccess(true);
			}
			log.info("END: saveCargoDocUploadDetail Result" + result.toString());
		}

		return ResponseEntityUtil.success(result.toString());
	}

	@RequestMapping(value = "/getAuditTrailDetails", method = RequestMethod.POST)
	public ResponseEntity<?> getAuditTrailDetails(HttpServletRequest request) {
		Result result = new Result();
		CargoDocUpload obj = null;
		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: getAuditTrailDetails Start criteria :" + criteria.toString());
			obj = cargoDocService.getCargoDocUploadAuditDetail(criteria);

			log.info("cargoAuditTrailDetails :" + obj.toString());
			
		} catch (BusinessException e) {
			log.info("Exception getAuditTrailDetails : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if(errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception ex) {
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
			log.info("Exception getAuditTrailDetails:  ", ex);
			
		} finally {
			if (errorMessage != null && errorMessage != "") {
				result.setSuccess(false);
				result.setError(errorMessage);
				errorMessage = "";
			} else {
				result.setData(obj);
				result.setSuccess(true);
			}
			log.info("END: getAuditTrailDetails END. \n Response :" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@RequestMapping(value = "/cargoDocDownload", method = RequestMethod.POST)
	public ResponseEntity<Resource> cargoFileDownloadAttachment(HttpServletRequest request) {
		Resource resource = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: cargoFileDownloadAttachment Start criteria :" + criteria.toString());
			resource = cargoDocService.fileDownload(criteria);
			String contentType = null;
			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (Exception ex) {
				log.info("Could not determine file type.");
			}

			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
			
		} catch (BusinessException e) {
			log.info("Exception cargoFileDownloadAttachment : ", e);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resource);
		} catch (Exception ex) {
			log.info("Exception cargoFileDownloadAttachment : ", ex);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resource);
		} finally {
			log.info("END: cargoFileDownloadAttachment File Downloaded Successfully");
		}
	}
	
	@ApiOperation(value = "Send Notification", response = String.class)
	@RequestMapping(value = "/sendNotification", method = RequestMethod.POST)
	public ResponseEntity<?> sendNotification(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		try { 
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: sendNotification criteria:" + criteria.toString());
			map.put("data", cargoDocService.sendNotification(criteria));
			result.setData(map);
			
		} catch (BusinessException e) {
			log.info("Exception sendNotification : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if(errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception sendNotification:  ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && errorMessage != "") {
				result.setSuccess(false);
				result.setError(errorMessage);
			} else {
				result.setSuccess(true);
			}
			log.info("END: sendNotification Result" + result.toString());
		}

		return ResponseEntityUtil.success(result.toString());
	}

	// EndRegion

}
