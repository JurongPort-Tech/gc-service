package sg.com.jp.generalcargo.domain;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HatchWisePackageDetail extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vvCd;
	private String remarks;
	private List<HatchBreakDownPageDetail.HatchDetail> hatchDetail;
	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<HatchBreakDownPageDetail.HatchDetail> getHatchDetail() {
		return hatchDetail;
	}

	public void setHatchDetail(List<HatchBreakDownPageDetail.HatchDetail> hatchDetail) {
		this.hatchDetail = hatchDetail;
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
