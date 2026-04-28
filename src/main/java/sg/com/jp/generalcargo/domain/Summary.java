package sg.com.jp.generalcargo.domain;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Summary {
	private String totalLineItemProcessed;
	private String totalSuccess;
	private String totalFail;
	private List<Template> fileDetails;
	private XSSFWorkbook workbook;
	private boolean headerValid;
	private String type;
	private String summaryUploaded; // MACF
	private String typeCd; // MACF
	private String vvCd; // MACF
	private String checkVoy;
	
	public boolean isHeaderValid() {
		return headerValid;
	}

	public void setHeaderValid(boolean headerValid) {
		this.headerValid = headerValid;
	}

	public String getTotalLineItemProcessed() {
		return totalLineItemProcessed;
	}

	public void setTotalLineItemProcessed(String totalLineItemProcessed) {
		this.totalLineItemProcessed = totalLineItemProcessed;
	}

	public String getTotalSuccess() {
		return totalSuccess;
	}

	public void setTotalSuccess(String totalSuccess) {
		this.totalSuccess = totalSuccess;
	}

	public String getTotalFail() {
		return totalFail;
	}

	public void setTotalFail(String totalFail) {
		this.totalFail = totalFail;
	}

	public List<Template> getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(List<Template> fileDetails) {
		this.fileDetails = fileDetails;
	}

	public XSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(XSSFWorkbook workbook) {
		this.workbook = workbook;
	}
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public String getCheckVoy() {
		return checkVoy;
	}

	public void setCheckVoy(String checkVoy) {
		this.checkVoy = checkVoy;
	}

}
