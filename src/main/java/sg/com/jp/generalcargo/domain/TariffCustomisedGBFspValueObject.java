package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TariffCustomisedGBFspValueObject implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int fspNBR;

	private String custCD;

	private String acctNBR;

	private String contractNBR;

	private String vslType;

	private String vslNm;

	private String outVOYNBR;

	private String inVOYNBR;

	private String mvmt;

	private String opType;

	private String stgType;

	private String gdsType;

	private String hsCD;

	private String fsp;

	private String lastModifyUserId;

	private Date lastModifyDTTM;

	private String vvcd;
	
	private String color;
	

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getCustCD() {
		return custCD;
	}

	public void setCustCD(String custCD) {
		this.custCD = custCD;
	}

	public String getAcctNBR() {
		return acctNBR;
	}

	public void setAcctNBR(String acctNBR) {
		this.acctNBR = acctNBR;
	}

	public String getContractNBR() {
		return contractNBR;
	}

	public void setContractNBR(String contractNBR) {
		this.contractNBR = contractNBR;
	}

	public String getVslType() {
		return vslType;
	}

	public void setVslType(String vslType) {
		this.vslType = vslType;
	}

	public String getOutVOYNBR() {
		return outVOYNBR;
	}

	public void setOutVOYNBR(String outVOYNBR) {
		this.outVOYNBR = outVOYNBR;
	}

	public String getInVOYNBR() {
		return inVOYNBR;
	}

	public void setInVOYNBR(String inVOYNBR) {
		this.inVOYNBR = inVOYNBR;
	}

	public String getMvmt() {
		return mvmt;
	}

	public void setMvmt(String mvmt) {
		this.mvmt = mvmt;
	}

	public String getOpType() {
		return opType;
	}

	public void setOpType(String opType) {
		this.opType = opType;
	}

	public String getStgType() {
		return stgType;
	}

	public void setStgType(String stgType) {
		this.stgType = stgType;
	}

	public String getGdsType() {
		return gdsType;
	}

	public void setGdsType(String gdsType) {
		this.gdsType = gdsType;
	}

	public String getHsCD() {
		return hsCD;
	}

	public void setHsCD(String hsCD) {
		this.hsCD = hsCD;
	}

	public String getFsp() {
		return fsp;
	}

	public void setFsp(String fsp) {
		this.fsp = fsp;
	}

	public String getLastModifyUserId() {
		return lastModifyUserId;
	}

	public void setLastModifyUserId(String lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}

	public Date getLastModifyDTTM() {
		return lastModifyDTTM;
	}

	public void setLastModifyDTTM(Date lastModifyDTTM) {
		this.lastModifyDTTM = lastModifyDTTM;
	}

	public int getFspNBR() {
		return fspNBR;
	}

	public void setFspNBR(int fspNBR) {
		this.fspNBR = fspNBR;
	}

	public String getVvcd() {
		return vvcd;
	}

	public void setVvcd(String vvcd) {
		this.vvcd = vvcd;
	}

	public String getVslNm() {
		return vslNm;
	}

	public void setVslNm(String vslNm) {
		this.vslNm = vslNm;
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
