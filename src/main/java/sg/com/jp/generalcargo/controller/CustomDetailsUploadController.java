package sg.com.jp.generalcargo.controller;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CustomDetailsActionTrailDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsFileUploadDetails;
import sg.com.jp.generalcargo.domain.FTZInterchangeVO;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.Summary;
import sg.com.jp.generalcargo.domain.SummaryCuscar;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.CustomDetailsService;
import sg.com.jp.generalcargo.service.CustomDetailsCuscarService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = CustomDetailsUploadController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CustomDetailsUploadController {

	public static final String ENDPOINT = "gc/customDetailsUpload";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";

	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(CustomDetailsUploadController.class);

	@Autowired
	private CustomDetailsService customDetailsService;
	@Autowired
	private CustomDetailsCuscarService ftzCuscarService;

	@PostMapping(value = "/getlistVessel")
	public ResponseEntity<?> getlistVessel(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** getlistVessel Start criteria :" + criteria.toString());

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String search = CommonUtility.deNull(criteria.getPredicates().get("search"));

			List<VesselVoyValueObject> vesselList = customDetailsService.getlistVessel(coCd, search);
			log.info("getlistVessel:" + vesselList.toString());
			map.put("data", vesselList);

		} catch (BusinessException be) {
			log.info("Exception getlistVessel : ", be);
			errorMessage = ConstantUtil.CUSTOM_DETAILS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception getlistVessel : ", e);
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END getlistVessel");
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/downloadExcelTemplate")
	public void downloadExcelTemplate(HttpServletRequest request, HttpServletResponse response) {
		try {
			log.info("START downloadExcelTemplate");
			Criteria criteria = CommonUtil.getCriteria(request);

			log.info(" downloadExcelTemplate criteria :" + criteria.toString());
			String vvCd = (CommonUtility.deNull(criteria.getPredicates().get("varNbr"))).trim();
			XSSFWorkbook wb = null;
			wb = customDetailsService.customDetailsExcelDownload(vvCd);
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=customDetails.xlsx");

			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			wb.close();
		} catch (BusinessException be) {
			log.info("Exception downloadExcelTemplate : ", be);
			errorMessage = ConstantUtil.CUSTOM_DETAILS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception downloadExcelTemplate : ", e);
		} finally {
			log.info("END downloadExcelTemplate");
		}
	}

	@PostMapping(value = "/customDetailsUploadDetail")
	public ResponseEntity<?> customDetailsUploadDetail(HttpServletRequest request) {
		Result result = new Result();
		PageDetails pageDetails = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START customDetailsUploadDetail criteria: " + criteria.toString());
			String vvCd = (CommonUtility.deNull(criteria.getPredicates().get("varNbr"))).trim();
			String vslName = (CommonUtility.deNull(criteria.getPredicates().get("vslName"))).trim();
			String inVoyNo = (CommonUtility.deNull(criteria.getPredicates().get("inVoyNo"))).trim();
			String outVoyNo = (CommonUtility.deNull(criteria.getPredicates().get("outVoyNo"))).trim();
			
			String latestvvCd = customDetailsService.getVvcdFromVesselDetails(vslName,inVoyNo,outVoyNo);
			
			if(vvCd.isEmpty() || (!latestvvCd.isEmpty() && !vvCd.equalsIgnoreCase(latestvvCd))) { // Add check if latestvvCd is null, don't assign value - NS APR 2025
				vvCd = latestvvCd;
			}
			
			log.info(" customDetailsUploadDetail: Param:vvCd:" + vvCd);
			pageDetails = customDetailsService.customDetailsUploadDetail(vvCd);
			
			String summary = customDetailsService.getTotalCntrForVessel(vvCd);
			pageDetails.setSummary(summary);
			
			
			log.info(" customDetailsUploadDetail: pageDetails:" + pageDetails.toString());
		} catch (BusinessException be) {
			log.info("Exception customDetailsUploadDetail : ", be);
			errorMessage = ConstantUtil.CUSTOM_DETAILS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception customDetailsUploadDetail : ", e);
			errorMessage = ConstantUtil.CUSTOM_DETAILS_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(pageDetails);
				result.setSuccess(true);
			}
			log.info("END customDetailsUploadDetail");
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/customDetailsActionTrailInfo")
	public ResponseEntity<?> customDetailsActionTrailInfo(HttpServletRequest request) {
		TableResult customDetails_hatch_act_trl = null;
		try {
			log.info("START : customDetailsActionTrailInfo");
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("Params:" + criteria.toString());

			customDetails_hatch_act_trl = customDetailsService.getCustomDetailsActionTrail(criteria);

			log.info("customDetailsActionTrailInfo:" + customDetails_hatch_act_trl.toString());
		} catch (BusinessException be) {
			log.info("Exception customDetailsActionTrail : ", be);
			errorMessage = ConstantUtil.CUSTOM_DETAILS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception customDetailsActionTrail : ", e);
		} finally {
			log.info("END customDetailsActionTrail");
		}
		return ResponseEntityUtil.success(customDetails_hatch_act_trl.toString());
	}

	@PostMapping(value = "/customDetailsActionTrailDetail")
	public ResponseEntity<?> customDetailsActionTrailDetail(HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		CustomDetailsActionTrailDetails customDetailsActionTrailDetail = null;
		try {
			log.info("START : customDetailsActionTrailDetail");
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("customDetailsActionTrailDetail Params:" + criteria.toString());
			String customDetails_act_trl_id = CommonUtility
					.deNull(criteria.getPredicates().get("customDetails_act_trl_id"));
			String typeCd = CommonUtility.deNull(criteria.getPredicates().get("typeCd"));
			customDetailsActionTrailDetail = customDetailsService
					.customDetailsActionTrailDetail(customDetails_act_trl_id, typeCd);
		} catch (BusinessException be) {
			log.info("Exception customDetailsActionTrailDetail : ", be);
			errorMessage = ConstantUtil.CUSTOM_DETAILS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception customDetailsActionTrailDetail : ", e);
			errorMessage = ConstantUtil.CUSTOM_DETAILS_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(customDetailsActionTrailDetail);
				result.setSuccess(true);
			}
			log.info("END customDetailsActionTrailDetail result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/excelProcessUpload")
	public ResponseEntity<?> excelProcessUpload(@RequestParam("file") MultipartFile uploadingFile,
			HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		String assignedFileName = null;
		CustomDetailsFileUploadDetails customDetailsFileUploadDetails = new CustomDetailsFileUploadDetails();
		try {
			log.info("START excelProcessUpload ");
			Criteria criteria = CommonUtil.getCriteria(request);
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String typeCd = (CommonUtility.deNull(criteria.getPredicates().get("typeCd"))).trim();
			String varNbr = (CommonUtility.deNull(criteria.getPredicates().get("varNbr"))).trim();

			log.info("excelProcessUpload Request Params:" + criteria.toString());
			Summary summary = new Summary();
			if (uploadingFile.getSize() > 0) {
				log.info("excelProcessUpload upload file size:" + uploadingFile.getSize() + ", File :"
						+ uploadingFile.getOriginalFilename());
			}

			if (uploadingFile.getSize() > 0) {
				log.info("Excel Process: Create a random gen UUID for " + uploadingFile.getOriginalFilename());
				String extension = FilenameUtils.getExtension(uploadingFile.getOriginalFilename());
				if (extension.equals("xls") || extension.equals("xlsx")) {
					/*
					 * UUID uuid = UUID.randomUUID(); assignedFileName = uuid.toString() + "." +
					 * extension; log.info("assignedFileName:" + assignedFileName); boolean
					 * fileStatus = customDetailsService.fileUpload(assignedFileName,
					 * uploadingFile,vvCd);
					 */
					assignedFileName = customDetailsService.fileUpload(uploadingFile, varNbr);
					log.info("fileUpload status :" + assignedFileName);
					if (assignedFileName != null && assignedFileName != "") {
						String lastTimestamp = customDetailsService.getTimeStamp();
						customDetailsFileUploadDetails.setVv_cd((varNbr));
						customDetailsFileUploadDetails.setActual_file_name(uploadingFile.getOriginalFilename());
						customDetailsFileUploadDetails.setAssigned_file_name(assignedFileName);
						customDetailsFileUploadDetails.setLast_modified_user_id(userId);
						customDetailsFileUploadDetails.setLast_modified_dttm(lastTimestamp);
						customDetailsFileUploadDetails.setTypeCd(typeCd);

						summary = customDetailsService.processCustomDetailsExcelFile(uploadingFile,
								customDetailsFileUploadDetails, varNbr, userId, companyCode);
						summary.setTypeCd(typeCd);
						summary.setVvCd(varNbr);
						result.setSuccess(true);
						result.setData(summary);
						log.info("excelProcessUpload: result: " + result.toString());
						// 4)insert action trail
						boolean res = customDetailsService.insertActionTrial(varNbr, typeCd, summary, lastTimestamp,
								userId);
						log.info("insertActionTrial:" + res);
					}
				} else {
					log.info("Exception excelProcessUpload : File should be xls or xlsx format");
					errorMessage = "M0010";
					result.setSuccess(false);
					result.setError(errorMessage);
					errorMessage = "";
				}
			} else {
				log.info("excelProcessUpload File missing ");
				result.setSuccess(false);
				result.setError("Upload file missing ");
			}

		} catch (BusinessException e) {
			log.error("Exception excelProcessUpload : ", e);
			errorMessage = ConstantUtil.CUSTOM_DETAILS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
			result.setSuccess(false);
			result.setError(errorMessage);
			result.setData(null);
		} catch (Exception ex) {
			log.info("Exception excelProcessUpload : ", ex);
			errorMessage = ConstantUtil.CUSTOM_DETAILS_ERROR_CONSTANT_MAP.get("M4201");
			result.setSuccess(false);
			result.setError(errorMessage);
		} finally {
			log.info("END excelProcessUpload :" + result.toString());
		}
		log.info("excelProcessUpload:result" + result.toString());
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/excelProcessDownload")
	public ResponseEntity<?> excelProcessDownload(HttpServletRequest request) {
		Resource resource = null;
		try {
			log.info("START excelProcessDownload");
			Criteria criteria = CommonUtil.getCriteria(request);
			String type = (CommonUtility.deNull(criteria.getPredicates().get("typeCd"))).trim();
			String refId = (CommonUtility.deNull(criteria.getPredicates().get("refId"))).trim();
			log.info("excelProcessDownload : Param: " + criteria.toString());

			resource = customDetailsService.excelProcessDownload(refId, type);

			String contentType = null;
			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (Exception ex) {
				log.info(" excelProcessDownload Could not determine file type.");
			}
			// Fallback to the default content type if type could not be determined
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception ex) {
			log.info("Exception excelProcessDownload : ", ex);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resource);
		} finally {
			log.info("END excelProcessDownload");
		}
	}

	@PostMapping(value = "/cuscarProcessUpload")
	public ResponseEntity<?> uploadCuscar(@RequestParam("file") MultipartFile uploadFile, HttpServletRequest request) {
		String filename = null;
		Result result = new Result();
		CustomDetailsFileUploadDetails customDetailsFileUploadDetails = new CustomDetailsFileUploadDetails();
		Criteria criteria = CommonUtil.getCriteria(request);
		errorMessage = null;
		boolean uploadExist;
		try {
			log.info("START: uploadCUSCAR criteria: " + criteria.toString());
			String userId = (CommonUtility.deNull(criteria.getPredicates().get("userAccount"))).trim();
			String companyCode = (CommonUtility.deNull(criteria.getPredicates().get("companyCode"))).trim();
			String typeCd = (CommonUtility.deNull(criteria.getPredicates().get("typeCd"))).trim();
			String varNbr = (CommonUtility.deNull(criteria.getPredicates().get("varNbr"))).trim();
			if (uploadFile.getSize() > 0) {
				uploadExist = true;
				log.info("Attachment :\n " + uploadFile.toString() + ", size :" + uploadFile.getSize() + "  Name :"
						+ uploadFile.getOriginalFilename());
			} else {
				uploadExist = false;
				log.info("Attachment : No Files " + ", size :" + uploadFile.getSize());

			}
			if (uploadExist) {
				log.info(" file uploded fileName :" + filename);
				log.info("CUSCAR Process: Create a random gen UUID for " + uploadFile.getOriginalFilename());
				String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
				if (extension.equals("txt") || extension.equals("edi")) {
					Path filePath = ftzCuscarService.uploadFile(criteria, uploadFile);
					if (filePath != null) {
						List<FTZInterchangeVO> intVoList = ftzCuscarService.parseData(filePath, varNbr); // parse data
						if (intVoList != null) {
							String lastTimestamp = customDetailsService.getTimeStamp();
							customDetailsFileUploadDetails.setVv_cd((varNbr));
							customDetailsFileUploadDetails.setActual_file_name(uploadFile.getOriginalFilename());
							customDetailsFileUploadDetails.setAssigned_file_name(filePath.getFileName().toString());
							customDetailsFileUploadDetails.setLast_modified_user_id(userId);
							customDetailsFileUploadDetails.setLast_modified_dttm(lastTimestamp);
							customDetailsFileUploadDetails.setTypeCd(typeCd);

							SummaryCuscar summaryObj = ftzCuscarService.processData(intVoList,
									customDetailsFileUploadDetails, varNbr, userId, companyCode);

							result.setSuccess(true);
							result.setData(summaryObj);
							// 4)insert action trail
							boolean res = customDetailsService.insertActionTrialCuscar(varNbr, typeCd, summaryObj,
									lastTimestamp, userId);
							log.info("insertActionTrial:" + res);
							
							result.setSuccess(true);
						} else {
							log.info("Exception uploadCuscar : File content should be in EDIFACT format");
							errorMessage = "File content should be in EDIFACT format";
							result.setSuccess(false);
							result.setError(errorMessage);
						}

						log.info("uploadCuscar: result: " + result.toString());
					} else {
						log.error("Upload file failed!");
						errorMessage = "Upload file failed!";
						result.setSuccess(false);
						result.setError(errorMessage);
					}
				} else {
					log.info("Exception uploadCuscar : File should be .edi or .txt format");
					errorMessage = "File should be .edi or .txt format";
					result.setSuccess(false);
					result.setError(errorMessage);
				}
			} else {
				log.info("uploadCuscar File missing ");
				result.setSuccess(false);
				result.setError("Upload file missing ");
			}

		} catch (BusinessException e) {
			log.error("Exception uploadCUSCAR : ", e);
			errorMessage = ConstantUtil.CUSTOM_DETAILS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
			result.setSuccess(false);
			result.setError(errorMessage);
			result.setData(null);
		} catch (Exception ex) {
			log.info("Exception uploadCUSCAR : ", ex);
			errorMessage = ConstantUtil.CUSTOM_DETAILS_ERROR_CONSTANT_MAP.get("M4201");
			result.setSuccess(false);
			result.setError(errorMessage);
		} finally {
			log.info("END: uploadCUSCAR result: " + result.toString());
		}
		log.info("uploadCUSCAR result: " + result.toString());
		return ResponseEntityUtil.success(result.toString());

	}

	@PostMapping(value = "/downloadProcessCuscarFile")
	public ResponseEntity<Resource> downloadProcessCuscarFile(HttpServletRequest request) {
		Resource resource = null;
		String contentType = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START : downloadProcessCuscarFile criteria:" + criteria.toString());
			String type = (CommonUtility.deNull(criteria.getPredicates().get("typeCd"))).trim();
			String refId = (CommonUtility.deNull(criteria.getPredicates().get("refId"))).trim();
			log.info("excelProcessDownload : Param: " + criteria.toString());

			resource = ftzCuscarService.downloadFile(refId, type);

			
			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (Exception ex) {
				log.info(" excelProcessDownload Could not determine file type.");
			}
			// Fallback to the default content type if type could not be determined
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception ex) {
			log.info("Exception downloadProcessCuscarFile : ", ex);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resource);
		} finally {
			log.info("File Downloaded Successfully");
		}
	}
}
