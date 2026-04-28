package sg.com.jp.generalcargo.domain;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CargoDimensionDetails extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vvCd;
	private List<CargoDimension> cargoDimensionInfo;
	private String remarks;

	public static class CargoDimension {
		
		private String billNo;
		private String nbrPkgs;
		private String weight;
		private String length;
		private String breadth;
		private String height;
		private String mftSeqNbr;
		
		public String getMftSeqNbr() {
			return mftSeqNbr;
		}

		public void setMftSeqNbr(String mftSeqNbr) {
			this.mftSeqNbr = mftSeqNbr;
		}

		public String getBillNo() {
			return billNo;
		}

		public void setBillNo(String billNo) {
			this.billNo = billNo;
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
	}

	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public List<CargoDimension> getCargoDimensionInfo() {
		return cargoDimensionInfo;
	}

	public void setCargoDimensionInfo(List<CargoDimension> cargoDimensionInfo) {
		this.cargoDimensionInfo = cargoDimensionInfo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
