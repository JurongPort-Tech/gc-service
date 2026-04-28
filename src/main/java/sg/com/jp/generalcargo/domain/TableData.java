package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Revision History
 * ------------------------------------------------------------------------------------------------------
 * Author			Description												Version			Date
 * ------------------------------------------------------------------------------------------------------
 * MC Consulting	First Version											1.0				27-Mar-2019
 */

@JsonInclude(Include.NON_NULL)
public class TableData implements Serializable {

	private static final long serialVersionUID = 1L;

	private Boolean success;

	private int total;

	private int totalPage;

	private int PageIndex;

	private String screen;

	private String co;

	private String recordStatus;

	private TopsModel listData;

	public TopsModel getListData() {
		return this.listData;
	}

	public void setListData(TopsModel listData) {
		this.listData = listData;
	}

	public Boolean getSuccess() {
		return this.success;
	}

	public void isSuccess(Boolean success) {
		this.success = success;
	}

	public int getTotal() {
		return this.total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalPage() {
		return this.totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageIndex() {
		return this.PageIndex;
	}

	public void setPageIndex(int PageIndex) {
		this.PageIndex = PageIndex;
	}

	public String getScreen() {
		return this.screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public String getCo() {
		return this.co;
	}

	public void setCo(String co) {
		this.co = co;
	}

	public String getRecordStatus() {
		return this.recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
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
