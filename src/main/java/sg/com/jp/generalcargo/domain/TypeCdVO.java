package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TypeCdVO extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String miscTypeCd;
	private String miscTypeNm;

	public String getMiscTypeCd() {
		return miscTypeCd;
	}

	public void setMiscTypeCd(String miscTypeCd) {
		this.miscTypeCd = miscTypeCd;
	}

	public String getMiscTypeNm() {
		return miscTypeNm;
	}

	public void setMiscTypeNm(String miscTypeNm) {
		this.miscTypeNm = miscTypeNm;
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
