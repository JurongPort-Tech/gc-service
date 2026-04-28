package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ManifestUploadConfig {
	private Long mft_upload_config_id;
	private String attr_name;
	private String attr_desc;
	private String input_type;
	private Long display_seq;
	private String mandatory_ind;
	private String lookup_table;
	private String lookup_cat_cd;
	private String column_nm;
	private String last_modified_user_id;
	private String last_modified_dttm;

	public Long getMft_upload_config_id() {
		return mft_upload_config_id;
	}

	public void setMft_upload_config_id(Long mft_upload_config_id) {
		this.mft_upload_config_id = mft_upload_config_id;
	}

	public String getAttr_name() {
		return attr_name;
	}

	public void setAttr_name(String attr_name) {
		this.attr_name = attr_name;
	}

	public String getAttr_desc() {
		return attr_desc;
	}

	public void setAttr_desc(String attr_desc) {
		this.attr_desc = attr_desc;
	}

	public String getInput_type() {
		return input_type;
	}

	public void setInput_type(String input_type) {
		this.input_type = input_type;
	}

	public Long getDisplay_seq() {
		return display_seq;
	}

	public void setDisplay_seq(Long display_seq) {
		this.display_seq = display_seq;
	}

	public String getColumn_nm() {
		return column_nm;
	}

	public void setColumn_nm(String column_nm) {
		this.column_nm = column_nm;
	}

	public String getMandatory_ind() {
		return mandatory_ind;
	}

	public void setMandatory_ind(String mandatory_ind) {
		this.mandatory_ind = mandatory_ind;
	}

	public String getLookup_table() {
		return lookup_table;
	}

	public void setLookup_table(String lookup_table) {
		this.lookup_table = lookup_table;
	}

	public String getLookup_cat_cd() {
		return lookup_cat_cd;
	}

	public void setLookup_cat_cd(String lookup_cat_cd) {
		this.lookup_cat_cd = lookup_cat_cd;
	}
 

	public String getLast_modified_user_id() {
		return last_modified_user_id;
	}

	public void setLast_modified_user_id(String last_modified_user_id) {
		this.last_modified_user_id = last_modified_user_id;
	}

	public String getLast_modified_dttm() {
		return last_modified_dttm;
	}

	public void setLast_modified_dttm(String last_modified_dttm) {
		this.last_modified_dttm = last_modified_dttm;
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
