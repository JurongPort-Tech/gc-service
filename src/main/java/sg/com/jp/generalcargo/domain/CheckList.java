package sg.com.jp.generalcargo.domain;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CheckList  implements TopsIObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// For name value pairs
	private String cd;
	private String nm;

	// For Check List
	private String item_cat_cd;
	private String item_cd;
	private String item_desc;
	private String item_problem_ind;
	private String item_status;
	private String create_user_id;
	private Date create_dttm;
	private String last_modify_user_id;
	private Date last_modify_dttm;
	private String limit;
	private String start;

	// For Joins
	private String item_cat_nm;
	private String last_modify_user_nm;
	private String last_modify_dttm_text;
	private String item_status_text;

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getCd() {
		return cd;
	}

	public void setCd(String cd) {
		this.cd = cd;
	}

	public String getNm() {
		return nm;
	}

	public void setNm(String nm) {
		this.nm = nm;
	}

	public String getItem_cat_cd() {
		return item_cat_cd;
	}

	public void setItem_cat_cd(String item_cat_cd) {
		this.item_cat_cd = item_cat_cd;
	}

	public String getItem_cd() {
		return item_cd;
	}

	public void setItem_cd(String item_cd) {
		this.item_cd = item_cd;
	}

	public String getItem_desc() {
		return item_desc;
	}

	public void setItem_desc(String item_desc) {
		this.item_desc = item_desc;
	}

	public String getItem_problem_ind() {
		return item_problem_ind;
	}

	public void setItem_problem_ind(String item_problem_ind) {
		this.item_problem_ind = item_problem_ind;
	}

	public String getItem_status() {
		return item_status;
	}

	public void setItem_status(String item_status) {
		this.item_status = item_status;
	}

	public String getCreate_user_id() {
		return create_user_id;
	}

	public void setCreate_user_id(String create_user_id) {
		this.create_user_id = create_user_id;
	}

	public Date getCreate_dttm() {
		return create_dttm;
	}

	public void setCreate_dttm(Date create_dttm) {
		this.create_dttm = create_dttm;
	}

	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}

	public Date getLast_modify_dttm() {
		return last_modify_dttm;
	}

	public void setLast_modify_dttm(Date last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}

	public String getItem_cat_nm() {
		return item_cat_nm;
	}

	public void setItem_cat_nm(String item_cat_nm) {
		this.item_cat_nm = item_cat_nm;
	}

	public String getLast_modify_user_nm() {
		return last_modify_user_nm;
	}

	public void setLast_modify_user_nm(String last_modify_user_nm) {
		this.last_modify_user_nm = last_modify_user_nm;
	}

	public String getLast_modify_dttm_text() {
		return last_modify_dttm_text;
	}

	public void setLast_modify_dttm_text(String last_modify_dttm_text) {
		this.last_modify_dttm_text = last_modify_dttm_text;
	}

	public String getItem_status_text() {
		return item_status_text;
	}

	public void setItem_status_text(String item_status_text) {
		this.item_status_text = item_status_text;
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
