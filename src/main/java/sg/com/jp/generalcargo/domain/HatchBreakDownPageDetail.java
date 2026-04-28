package sg.com.jp.generalcargo.domain;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.CommonUtil;

public class HatchBreakDownPageDetail {

	private String vvCd;
	private String vesselName;
	private String voyageNo;
	private String remarks;
	private String nbrOfHatch;
	private List<HatchCardDetail> hatchCardInfo;
	private List<HatchDetail> hatchDetail;
	private Boolean isSubmissionAllowed;

	public static class HatchCardDetail {
		private String hatchNo;

		private List<CardDetail> cardDetail;

		public String getHatchNo() {
			return hatchNo;
		}

		public void setHatchNo(String hatchNo) {
			this.hatchNo = hatchNo;
		}

		public List<CardDetail> getCardDetail() {
			return cardDetail;
		}

		public void setCardDetail(List<CardDetail> cardDetail) {
			this.cardDetail = cardDetail;
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

	public static class CardDetail {
		private String billNo;
		private String cargoDesc;
		private String weight;
		private String packages;
		private String volume;

		public String getBillNo() {
			return billNo;
		}

		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}

		public String getCargoDesc() {
			return cargoDesc;
		}

		public void setCargoDesc(String cargoDesc) {
			this.cargoDesc = cargoDesc;
		}

		public String getWeight() {
			return weight;
		}

		public void setWeight(String weight) {
			this.weight = weight;
		}

		public String getPackages() {
			return packages;
		}

		public void setPackages(String packages) {
			this.packages = packages;
		}

		public String getVolume() {
			return volume;
		}

		public void setVolume(String volume) {
			this.volume = volume;
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

	public static class HatchDetail {
		private String billNo;
		private String cargoDesc;
		private String hsCode;
		private String numOfPackages;
		private String weight;
		private String volume;
		private String mftSeqNbr;

		private List<HatchInfo> hatchInfo;

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

		public String getCargoDesc() {
			return cargoDesc;
		}

		public void setCargoDesc(String cargoDesc) {
			this.cargoDesc = cargoDesc;
		}

		public String getHsCode() {
			return hsCode;
		}

		public void setHsCode(String hsCode) {
			this.hsCode = hsCode;
		}

		public String getNumOfPackages() {
			return numOfPackages;
		}

		public void setNumOfPackages(String numOfPackages) {
			this.numOfPackages = numOfPackages;
		}

		public String getWeight() {
			return weight;
		}

		public void setWeight(String weight) {
			this.weight = weight;
		}

		public String getVolume() {
			return volume;
		}

		public void setVolume(String volume) {
			this.volume = volume;
		}

		public List<HatchInfo> getHatchInfo() {
			return hatchInfo;
		}

		public void setHatchInfo(List<HatchInfo> hatchInfo) {
			this.hatchInfo = hatchInfo;
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

	public static class HatchInfo {
		private String hatchNo;
		private String packages;
		private String weight;
		private String volume;
		private String mftHatchSeqNbr;

		public String getHatchNo() {
			return CommonUtil.nullToZero(hatchNo);
		}

		public void setHatchNo(String hatchNo) {
			this.hatchNo = hatchNo;
		}

		public String getPackages() {
			return CommonUtil.nullToZero(packages);
		}

		public void setPackages(String packages) {
			this.packages = packages;
		}

		public String getWeight() {
			return CommonUtil.nullToZero(weight);
		}

		public void setWeight(String weight) {
			this.weight = weight;
		}

		public String getVolume() {
			return CommonUtil.nullToZero(volume);
		}

		public void setVolume(String volume) {
			this.volume = volume;
		}

		@Override
		public String toString() {
			try {
				return new ObjectMapper().writeValueAsString(this);
			} catch (JsonProcessingException e) {
				return "";
			}
		}

		public String getMftHatchSeqNbr() {
			return CommonUtil.nullToZero(mftHatchSeqNbr);
		}

		public void setMftHatchSeqNbr(String mftHatchSeqNbr) {
			this.mftHatchSeqNbr = mftHatchSeqNbr;
		}

	}

	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public String getVesselName() {
		return vesselName;
	}

	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	public String getVoyageNo() {
		return voyageNo;
	}

	public void setVoyageNo(String voyageNo) {
		this.voyageNo = voyageNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<HatchCardDetail> getHatchCardInfo() {
		return hatchCardInfo;
	}

	public void setHatchCardInfo(List<HatchCardDetail> hatchCardInfo) {
		this.hatchCardInfo = hatchCardInfo;
	}

	public List<HatchDetail> getHatchDetail() {
		return hatchDetail;
	}

	public void setHatchDetail(List<HatchDetail> hatchDetail) {
		this.hatchDetail = hatchDetail;
	}

	public String getNbrOfHatch() {
		return nbrOfHatch;
	}

	public void setNbrOfHatch(String nbrOfHatch) {
		this.nbrOfHatch = nbrOfHatch;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public Boolean getIsSubmissionAllowed() {
		return isSubmissionAllowed;
	}

	public void setIsSubmissionAllowed(Boolean isSubmissionAllowed) {
		this.isSubmissionAllowed = isSubmissionAllowed;
	}

}
