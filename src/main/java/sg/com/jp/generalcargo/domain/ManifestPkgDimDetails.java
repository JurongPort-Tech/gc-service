package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ManifestPkgDimDetails {
	private Long mftDimSeqNbr;
	private String mftSeqNbr;
	private String nbrPkgs;
	private String weight;
	private String length;
	private String breadth;
	private String height;
	private String billNo;

	private String modifyUserid;
	private String modifyDttm;
	
	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getMftSeqNbr() {
		return mftSeqNbr;
	}

	public void setMftSeqNbr(String mftSeqNbr) {
		this.mftSeqNbr = mftSeqNbr;
	}

	public String getNbrPkgs() {
		return nbrPkgs;
	}

	public void setNbrPkgs(String nbrPkgs) {
		this.nbrPkgs = nbrPkgs;
	}



	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
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
	
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public String getModifyUserid() {
		return modifyUserid;
	}

	public void setModifyUserid(String modifyUserid) {
		this.modifyUserid = modifyUserid;
	}

	public String getModifyDttm() {
		return modifyDttm;
	}

	public void setModifyDttm(String modifyDttm) {
		this.modifyDttm = modifyDttm;
	}

	public Long getMftDimSeqNbr() {
		return mftDimSeqNbr;
	}

	public void setMftDimSeqNbr(Long mftDimSeqNbr) {
		this.mftDimSeqNbr = mftDimSeqNbr;
	}

}
