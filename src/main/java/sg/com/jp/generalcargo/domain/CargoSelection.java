package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CargoSelection {

	private Long mft_ext_seq_nbr;
	private String mft_seq_nbr;
	private String cargo_selection_cd;
	private String cargo_selection;
	private String last_modify_user_id;
	private String last_modify_dttm;
	
	public Long getMft_ext_seq_nbr() {
		return mft_ext_seq_nbr;
	}

	public void setMft_ext_seq_nbr(Long mft_ext_seq_nbr) {
		this.mft_ext_seq_nbr = mft_ext_seq_nbr;
	}

	public String getMft_seq_nbr() {
		return mft_seq_nbr;
	}

	public void setMft_seq_nbr(String mft_seq_nbr) {
		this.mft_seq_nbr = mft_seq_nbr;
	}

	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}

	public String getCargo_selection_cd() {
		return cargo_selection_cd;
	}

	public void setCargo_selection_cd(String cargo_selection_cd) {
		this.cargo_selection_cd = cargo_selection_cd;
	}

	public String getCargo_selection() {
		return cargo_selection;
	}

	public void setCargo_selection(String cargo_selection) {
		this.cargo_selection = cargo_selection;
	}

	public String getLast_modify_dttm() {
		return last_modify_dttm;
	}

	public void setLast_modify_dttm(String last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
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
