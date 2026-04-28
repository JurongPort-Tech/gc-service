package sg.com.jp.generalcargo.service.impl;

import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.dao.impl.CargoDocumentJdbcRepostory;
import sg.com.jp.generalcargo.domain.CargoDocUpload;
import sg.com.jp.generalcargo.domain.CargoDocUploadDetail;
import sg.com.jp.generalcargo.domain.CargoDocUploadNotificationDetail;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Email;
import sg.com.jp.generalcargo.domain.MiscDetail;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselDetail;
import sg.com.jp.generalcargo.service.CargoDocumentService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Service("CargoDocumentService")
public class CargoDocumentServiceImpl implements CargoDocumentService {

	private static final Log log = LogFactory.getLog(CargoDocumentServiceImpl.class);

	@Value("${cargomanifest.file.docupload.path}")
	String folderPath;

	@Value("${jp.common.notificationProperties.emailEndpoint}")
	private String emailSvcUrl;

	@Value("${jp.common.notificationProperties.cargomanifest.report.mail.template}")
	String cargoDoctemplate;

	@Value("${jp.common.notificationProperties.jpom.url}")
	private String jpomUrl;

	
	@Autowired
	private CargoDocumentJdbcRepostory cargoDocRepo;

	// Region CDU
	@Override
	public List<VesselDetail> getVesselInfo(String vesselName) throws BusinessException {
		return cargoDocRepo.getVesselInfo(vesselName);
	}

	@Override
	public CargoDocUpload getCargoDocUploadDetails(Criteria criteria) throws BusinessException {
		return cargoDocRepo.getCargoDocUploadDetails(criteria);
	}

	@Override
	public TableResult getCargoDocUploadAuditInfo(Criteria criteria) throws BusinessException {
		return cargoDocRepo.getCargoDocUploadAuditInfo(criteria);
	}

	@Override
	public Result saveCargoDocUpload(Criteria criteria) throws BusinessException

	{
		return cargoDocRepo.saveCargoDocUpload(criteria);
	}

	@Override
	public Result saveCargoDocUploadDetail(CargoDocUploadDetail obj) throws BusinessException

	{
		return cargoDocRepo.saveCargoDocUploadDetail(obj);
	}

	@Override
	public CargoDocUpload getCargoDocUploadAuditDetail(Criteria criteria) throws BusinessException
	{
		return cargoDocRepo.getCargoDocUploadAuditDetail(criteria);
	}

	@Override
	public String fileUpload(MultipartFile uploadFile, String vvCd) {
		try {
			log.info("fileUpload vvCd " + vvCd);
			if (uploadFile.getOriginalFilename().indexOf("/") >= 0
					|| uploadFile.getOriginalFilename().indexOf("\\") >= 0) {
				log.info("File name validation failed!");
				return null;
			}
			String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
			UUID uuid = UUID.randomUUID();
			String fileName = uuid.toString() + "." + extension;
			if (fileName.indexOf("/") >= 0 || fileName.indexOf("\\") >= 0) {
				log.info("File name validation failed!");
				return null;
			}
			String folderPathVvcd = folderPath + "/" + vvCd + "/";
			Path rootLocation = Paths.get(folderPathVvcd);
			if (!Files.exists(rootLocation)) {
				Files.createDirectories(rootLocation);
			}
			log.info("uploadFile data :" + uploadFile.getInputStream());
			Path folderLocation = rootLocation;
			if (!Files.exists(folderLocation)) {
				Files.createDirectories(folderLocation);
			}
			Path fileToDeletePath = folderLocation.resolve(fileName);
			Files.deleteIfExists(fileToDeletePath);
			log.info("fileUpload folderLocation :" + folderLocation);
			Files.copy(uploadFile.getInputStream(), folderLocation.resolve(fileName));
			
			log.info("END: *** fileUpload Result *****" + CommonUtility.deNull(fileName));
			return fileName;

		} catch (Exception e) {
			log.info("Exception fileUpload : ", e);
			return null;
		}
	}

	@Override
	public Resource fileDownload(Criteria criteria) {
		try {

			log.info("fileDownload criteria :" + criteria.toString());

			String fileName = criteria.getPredicates().get("assignedFileName");
			String vvCd = criteria.getPredicates().get("vvCd");
			Path rootLocation = Paths.get(folderPath + "/" + vvCd + "/").toAbsolutePath().normalize();
			Path filePath = rootLocation.resolve(fileName).normalize();
			log.info("fileDownload :" + filePath.toString());
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileSystemNotFoundException("File not found " + criteria);
			}
		} catch (Exception ex) {
			log.info("Exception fileDownload : ", ex);
			throw new FileSystemNotFoundException("File not found " + criteria);
		}
	}

	public Boolean isDocSubmissionAllowed(String vvCd, String coCd) throws BusinessException {
		return cargoDocRepo.isDocSubmissionAllowed(vvCd, coCd);
	}

	@Override
	public boolean sendNotification(Criteria criteria) throws BusinessException {
		
		try {
			log.info("START :sendNotification criteria: " + criteria.toString());
			String remarks = CommonUtility.deNull(criteria.getPredicates().get("remarks")).trim();
			String updatedBy = CommonUtility.deNull(criteria.getPredicates().get("userAccount")).trim();
			
			List<MiscDetail> emailInfo = cargoDocRepo.getCargoDocEmail("GC_EML_NOTIFICATIONS");
			if (emailInfo != null && emailInfo.size() > 0) {
				MiscDetail subjObj = emailInfo.stream()
						.filter(d -> d.getTypeCode().equalsIgnoreCase("CARGO_DOC_UPLOAD_SUB_SUBJECT")).findFirst()
						.orElse(null);
				if (subjObj != null && subjObj.getType().equalsIgnoreCase("Y")) {

					// read file template
					String cargoDocMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(),
							cargoDoctemplate);
					String docs = "";
					CargoDocUploadNotificationDetail obj = cargoDocRepo.getNotificationDetails(criteria);
					log.info("CargoDocUploadNotificationDetail detail :"+ obj.toString());
					if (obj!=null && obj.getCargoDocUploadInfo() != null
							&& obj.getCargoDocUploadInfo().size() > 0) {
						for (CargoDocUploadDetail code : obj.getCargoDocUploadInfo()) {
								docs = docs + code.getDocumentType() + ", ";
						}
						docs=CommonUtil.trimLastChar(docs.trim(), ",");
						updatedBy = obj.getCargoDocUploadInfo().get(0).getUploadedBy();
					}

					Map<String, String> emailInputData = new HashMap<String, String>();
					emailInputData.put("btr", obj.getBtr());
					emailInputData.put("etb", obj.getEtb());
					emailInputData.put("etu", obj.getEtu());
					emailInputData.put("agent", obj.getAgent());
					emailInputData.put("docs", docs);
					emailInputData.put("updatedBy", updatedBy);
					emailInputData.put("remarks", remarks);
					emailInputData.put("jpomurl", jpomUrl);
					log.info(" emailInputData :" + emailInputData.toString());
					String msg = CommonUtil.replaceVariablesInHtml(cargoDocMail, emailInputData);
					log.info("msg -> \n " + msg);

					Map<String, String> subjectInputData = new HashMap<String, String>();
					subjectInputData.put("vesselNameVoyage", obj.getVslNameVoyage());

					List<String> toEmailList = new ArrayList<String>();
					List<String> ccEmailList = new ArrayList<String>();
					Email email = new Email();
					email.setEmailSvcUrl(emailSvcUrl);
					email.setContentType("text/html");
					String cargoDocsubject;

					cargoDocsubject = subjObj.getTypeValue() +" "+ obj.getVslNameVoyage();
					MiscDetail fromObj = emailInfo.stream()
							.filter(d -> d.getTypeCode().equalsIgnoreCase("CARGO_DOC_UPLOAD_SUB_FROM")).findFirst()
							.orElse(null);
					if (fromObj != null) {
						email.setFrom(fromObj.getTypeValue());
					}
					MiscDetail toObj = emailInfo.stream()
							.filter(d -> d.getTypeCode().equalsIgnoreCase("CARGO_DOC_UPLOAD_SUB_TO")).findFirst()
							.orElse(null);
					if (toObj != null) {
						String[] toemails = toObj.getTypeValue().split(";");
						for (String emailId : toemails) {
							toEmailList.add(emailId);
						}
						email.setToList(toEmailList);
					}

					MiscDetail ccObj = emailInfo.stream()
							.filter(d -> d.getTypeCode().equalsIgnoreCase("CARGO_DOC_UPLOAD_SUB_CC")).findFirst()
							.orElse(null);
					if (ccObj != null) {
						if (ccObj.getTypeValue() != null && CommonUtil.deNull(ccObj.getTypeValue()) != "") {
							String[] ccemails = ccObj.getTypeValue().split(";");
							for (String emailId : ccemails) {
								ccEmailList.add(emailId);
							}
							email.setCcList(ccEmailList);
						}
					}

					email.setSubject(cargoDocsubject);
					email.setContent(msg);

					log.info("Email :" + email.toString());
					Boolean sendEmail = CommonUtil.sendEmail(email);
					log.info("Email Sent Flag :" + sendEmail);
					if (sendEmail) {
						return true;
					}
				} else {
					log.info("Email config is turned off ");
				}

			} else {
				log.info("email info not configured ");
			}
		} catch (Exception e) {
			log.info("Exception SendNotification : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END :SendNotification");
		}
		return false;
	}

	// EndRegion
}
