package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SummaryCuscar implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String totalRecordRcv = "0";
	private String totalError = "0";
	private String totalSuccess = "0";
	private String totalCreated = "0";
	private String totalUpdated = "0";
	private String totalDeleted = "0";
	private String fileError = "";
	private List<String> type =new ArrayList<String>();
	private List<String> blNbr =new ArrayList<String>();
	private Map<String, String> message;
	private Map<String, String> messageErr = new LinkedHashMap<>();
	private String vslNm = "";
	private String vslVoy = "";
	private String summaryUploaded;
	private String typeCd;
	private List<Template> fileDetails;

	public String getTotalRecordRcv() {
		return totalRecordRcv;
	}

	public void setTotalRecordRcv(String totalRecordRcv) {
		this.totalRecordRcv = totalRecordRcv;
	}

	public String getTotalError() {
		return totalError;
	}

	public void setTotalError(String totalError) {
		this.totalError = totalError;
	}

	public String getTotalSuccess() {
		return totalSuccess;
	}

	public void setTotalSuccess(String totalSuccess) {
		this.totalSuccess = totalSuccess;
	}

	public String getTotalCreated() {
		return totalCreated;
	}

	public void setTotalCreated(String totalCreated) {
		this.totalCreated = totalCreated;
	}

	public String getTotalUpdated() {
		return totalUpdated;
	}

	public void setTotalUpdated(String totalUpdated) {
		this.totalUpdated = totalUpdated;
	}

	public String getTotalDeleted() {
		return totalDeleted;
	}

	public void setTotalDeleted(String totalDeleted) {
		this.totalDeleted = totalDeleted;
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public String getFileError() {
		return fileError;
	}

	public void setFileError(String fileError) {
		this.fileError = fileError;
	}

	public Map<String, String> getMessage() {
		return message;
	}

	public void setMessage(Map<String, String> message) {
		this.message = message;
	}

	public String getVslNm() {
		return vslNm;
	}

	public void setVslNm(String vslNm) {
		this.vslNm = vslNm;
	}

	public String getVslVoy() {
		return vslVoy;
	}

	public void setVslVoy(String vslVoy) {
		this.vslVoy = vslVoy;
	}

	public List<String> getBlNbr() {
		return blNbr;
	}

	public void setBlNbr(List<String> blNbr) {
		this.blNbr = blNbr;
	}

	public String getSummaryUploaded() {
		return summaryUploaded;
	}

	public void setSummaryUploaded(String summaryUploaded) {
		this.summaryUploaded = summaryUploaded;
	}

	public String getTypeCd() {
		return typeCd;
	}

	public void setTypeCd(String typeCd) {
		this.typeCd = typeCd;
	}

	public List<Template> getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(List<Template> fileDetails) {
		this.fileDetails = fileDetails;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public Map<String, String> getMessageErr() {
		return messageErr;
	}

	public void setMessageErr(Map<String, String> messageErr) {
		this.messageErr = messageErr;
	}
}
