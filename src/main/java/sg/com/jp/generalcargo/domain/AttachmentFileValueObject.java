package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AttachmentFileValueObject extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String misc_seq_nbr;
	private String doc_type;
	private String upload_file_nm;
	private String assign_file_nm;
	private String create_user_id;
	private String create_dttm;
	private String fileType;
	private String docRefId;
	private String docPath;
	private String updateInd;
	private String itemCd;

	
	public String getDocRefId() {
		return docRefId;
	}
	public void setDocRefId(String docRefId) {
		this.docRefId = docRefId;
	}
	public String getDocPath() {
		return docPath;
	}
	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}
	public String getUpdateInd() {
		return updateInd;
	}
	public void setUpdateInd(String updateInd) {
		this.updateInd = updateInd;
	}
	public String getItemCd() {
		return itemCd;
	}
	public void setItemCd(String itemCd) {
		this.itemCd = itemCd;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getMisc_seq_nbr() {
		return misc_seq_nbr;
	}
	public void setMisc_seq_nbr(String misc_seq_nbr) {
		this.misc_seq_nbr = misc_seq_nbr;
	}
	public String getDoc_type() {
		return doc_type;
	}
	public void setDoc_type(String doc_type) {
		this.doc_type = doc_type;
	}
	public String getUpload_file_nm() {
		return upload_file_nm;
	}
	public void setUpload_file_nm(String upload_file_nm) {
		this.upload_file_nm = upload_file_nm;
	}
	public String getAssign_file_nm() {
		return assign_file_nm;
	}
	public void setAssign_file_nm(String assign_file_nm) {
		this.assign_file_nm = assign_file_nm;
	}
	public String getCreate_user_id() {
		return create_user_id;
	}
	public void setCreate_user_id(String create_user_id) {
		this.create_user_id = create_user_id;
	}
	public String getCreate_dttm() {
		return create_dttm;
	}
	public void setCreate_dttm(String create_dttm) {
		this.create_dttm = create_dttm;
	}
	

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
	
}
