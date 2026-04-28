package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 14 PCI PACKAGE IDENTIFICATION
public class FTZGroup14PCIVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// marking instruction code
	private String mrk_instr_cd;
	
	// marks & labels
	private String shpg_mrks_desc_1;
	private String shpg_mrks_desc_2;
	private String shpg_mrks_desc_3;
	private String shpg_mrks_desc_4;
	private String shpg_mrks_desc_5;
	private String shpg_mrks_desc_6;
	private String shpg_mrks_desc_7;
	private String shpg_mrks_desc_8;
	private String shpg_mrks_desc_9;
	private String shpg_mrks_desc_10;
	
	// container or package contents indicator code
	private String cntr_pkg_content_ind_cd;
	
	// type of marking
	private String type_of_marking;

	public String getMrk_instr_cd() {
		return mrk_instr_cd;
	}

	public void setMrk_instr_cd(String mrk_instr_cd) {
		this.mrk_instr_cd = mrk_instr_cd;
	}

	public String getShpg_mrks_desc_1() {
		return shpg_mrks_desc_1;
	}

	public void setShpg_mrks_desc_1(String shpg_mrks_desc_1) {
		this.shpg_mrks_desc_1 = shpg_mrks_desc_1;
	}

	public String getShpg_mrks_desc_2() {
		return shpg_mrks_desc_2;
	}

	public void setShpg_mrks_desc_2(String shpg_mrks_desc_2) {
		this.shpg_mrks_desc_2 = shpg_mrks_desc_2;
	}

	public String getShpg_mrks_desc_3() {
		return shpg_mrks_desc_3;
	}

	public void setShpg_mrks_desc_3(String shpg_mrks_desc_3) {
		this.shpg_mrks_desc_3 = shpg_mrks_desc_3;
	}

	public String getShpg_mrks_desc_4() {
		return shpg_mrks_desc_4;
	}

	public void setShpg_mrks_desc_4(String shpg_mrks_desc_4) {
		this.shpg_mrks_desc_4 = shpg_mrks_desc_4;
	}

	public String getShpg_mrks_desc_5() {
		return shpg_mrks_desc_5;
	}

	public void setShpg_mrks_desc_5(String shpg_mrks_desc_5) {
		this.shpg_mrks_desc_5 = shpg_mrks_desc_5;
	}

	public String getShpg_mrks_desc_6() {
		return shpg_mrks_desc_6;
	}

	public void setShpg_mrks_desc_6(String shpg_mrks_desc_6) {
		this.shpg_mrks_desc_6 = shpg_mrks_desc_6;
	}

	public String getShpg_mrks_desc_7() {
		return shpg_mrks_desc_7;
	}

	public void setShpg_mrks_desc_7(String shpg_mrks_desc_7) {
		this.shpg_mrks_desc_7 = shpg_mrks_desc_7;
	}

	public String getShpg_mrks_desc_8() {
		return shpg_mrks_desc_8;
	}

	public void setShpg_mrks_desc_8(String shpg_mrks_desc_8) {
		this.shpg_mrks_desc_8 = shpg_mrks_desc_8;
	}

	public String getShpg_mrks_desc_9() {
		return shpg_mrks_desc_9;
	}

	public void setShpg_mrks_desc_9(String shpg_mrks_desc_9) {
		this.shpg_mrks_desc_9 = shpg_mrks_desc_9;
	}

	public String getShpg_mrks_desc_10() {
		return shpg_mrks_desc_10;
	}

	public void setShpg_mrks_desc_10(String shpg_mrks_desc_10) {
		this.shpg_mrks_desc_10 = shpg_mrks_desc_10;
	}

	public String getCntr_pkg_content_ind_cd() {
		return cntr_pkg_content_ind_cd;
	}

	public void setCntr_pkg_content_ind_cd(String cntr_pkg_content_ind_cd) {
		this.cntr_pkg_content_ind_cd = cntr_pkg_content_ind_cd;
	}

	public String getType_of_marking() {
		return type_of_marking;
	}

	public void setType_of_marking(String type_of_marking) {
		this.type_of_marking = type_of_marking;
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
