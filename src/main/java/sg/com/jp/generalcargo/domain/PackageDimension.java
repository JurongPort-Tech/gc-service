package sg.com.jp.generalcargo.domain;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PackageDimension implements Cloneable{
	private Long mft_dim_seq_nbr;
	private String mft_seq_nbr;
	private String var_nbr;
	private String bl_nbr;
	private String cargo_desc;
	private String total_pkg;
	private String gross_wt;
	private String nbr_of_pkg;
	private String total_pkg_wt_kg;
	private String length_pkg;
	private String breadth;
	private String height;
	private String userId;
	private int rowNum;
	private List<Comments> errorInfo;
	private String message;
	
	public Object clone()throws CloneNotSupportedException{  
		return super.clone();  
		}  
	public String getVar_nbr() {
		return var_nbr;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setVar_nbr(String var_nbr) {
		this.var_nbr = var_nbr;
	}

	public String getBl_nbr() {
		return bl_nbr;
	}

	public void setBl_nbr(String bl_nbr) {
		this.bl_nbr = bl_nbr;
	}

	public String getMft_seq_nbr() {
		return mft_seq_nbr;
	}

	public void setMft_seq_nbr(String mft_seq_nbr) {
		this.mft_seq_nbr = mft_seq_nbr;
	}

	 
	public String getLength_pkg() {
		return length_pkg;
	}

	public void setLength_pkg(String length_pkg) {
		this.length_pkg = length_pkg;
	}

	public String getBreadth() {
		return breadth;
	}

	public void setBreadth(String breadth) {
		this.breadth = breadth;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getNbr_of_pkg() {
		return nbr_of_pkg;
	}

	public void setNbr_of_pkg(String nbr_of_pkg) {
		this.nbr_of_pkg = nbr_of_pkg;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public String getCargo_desc() {
		return cargo_desc;
	}

	public void setCargo_desc(String cargo_desc) {
		this.cargo_desc = cargo_desc;
	}

	public String getTotal_pkg() {
		return total_pkg;
	}

	public void setTotal_pkg(String total_pkg) {
		this.total_pkg = total_pkg;
	}


	public String getGross_wt() {
		return gross_wt;
	}

	public void setGross_wt(String gross_wt) {
		this.gross_wt = gross_wt;
	}

	public String getTotal_pkg_wt_kg() {
		return total_pkg_wt_kg;
	}

	public void setTotal_pkg_wt_kg(String total_pkg_wt_kg) {
		this.total_pkg_wt_kg = total_pkg_wt_kg;
	}

	public List<Comments> getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(List<Comments> errorInfo) {
		this.errorInfo = errorInfo;
	}


	public int getRowNum() {
		return rowNum;
	}


	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}


	public Long getMft_dim_seq_nbr() {
		return mft_dim_seq_nbr;
	}


	public void setMft_dim_seq_nbr(Long mft_dim_seq_nbr) {
		this.mft_dim_seq_nbr = mft_dim_seq_nbr;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}
}
