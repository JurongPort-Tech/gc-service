package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class CargoDocUploadDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vvCd;
	private String documentType;
	private String documentTypeCD;
	private String actualFileName;
	private String assignedFileName;
	private String attachmentFileName;
	private long fileSize;
	private String uploadedBy;
	private String uploadedDate;
	private String updateFlag;
	private String isMandatory;
	private String fileType;
	
	
	
	public String getVvCd() {
		return vvCd;
	}


	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}


	public String getDocumentType() {
		return documentType;
	}


	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}


	public String getDocumentTypeCD() {
		return documentTypeCD;
	}


	public void setDocumentTypeCD(String documentTypeCD) {
		this.documentTypeCD = documentTypeCD;
	}


	public String getActualFileName() {
		return actualFileName;
	}


	public void setActualFileName(String actualFileName) {
		this.actualFileName = actualFileName;
	}


	


	public String getAttachmentFileName() {
		return attachmentFileName;
	}


	public void setAttachmentFileName(String attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}





	public String getUploadedBy() {
		return uploadedBy;
	}


	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}


	public String getUploadedDate() {
		return uploadedDate;
	}


	public void setUploadedDate(String uploadedDate) {
		this.uploadedDate = uploadedDate;
	}


	public String getUpdateFlag() {
		return updateFlag;
	}


	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}


	public String getIsMandatory() {
		return isMandatory;
	}


	public void setIsMandatory(String isMandatory) {
		this.isMandatory = isMandatory;
	}


	public String getFileType() {
		return fileType;
	}


	public void setFileType(String fileType) {
		this.fileType = fileType;
	}


	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}


	public String getAssignedFileName() {
		return assignedFileName;
	}


	public void setAssignedFileName(String assignedFileName) {
		this.assignedFileName = assignedFileName;
	}


	public long getFileSize() {
		return fileSize;
	}


	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

}
