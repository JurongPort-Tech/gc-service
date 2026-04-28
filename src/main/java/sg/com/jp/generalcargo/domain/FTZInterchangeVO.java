package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FTZInterchangeVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FTZUNBVO unb;
	private FTZUNHVO unh;
	private FTZBGMVO bgm;
	private List<FTZDTMVO> dtm = new ArrayList<>(); // optional, max 9
	private List<FTZGroup4VO> group4; // max 9
	private List<FTZGroup5VO> group5; // max 9999
	private List<FTZGroup7VO> group7; // max 9999
	private List<FTZGroup8VO> group8; // max 9999
	private List<FTZGroup11VO> group11; // max 99
	private Map<String, FTZGroup11VO> group11Map = new LinkedHashMap<>();
	private List<FTZGroup14VO> group14; // max 9999
	private FTZUNTVO unt;
	private FTZUNZVO unz;

	public FTZUNBVO getUnb() {
		return unb;
	}

	public void setUnb(FTZUNBVO unb) {
		this.unb = unb;
	}
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public FTZUNHVO getUnh() {
		return unh;
	}

	public void setUnh(FTZUNHVO unh) {
		this.unh = unh;
	}

	public FTZBGMVO getBgm() {
		return bgm;
	}

	public void setBgm(FTZBGMVO bgm) {
		this.bgm = bgm;
	}

	public List<FTZGroup4VO> getGroup4() {
		return group4;
	}

	public void setGroup4(List<FTZGroup4VO> group4) {
		this.group4 = group4;
	}

	public List<FTZGroup5VO> getGroup5() {
		return group5;
	}

	public void setGroup5(List<FTZGroup5VO> group5) {
		this.group5 = group5;
	}

	public List<FTZGroup7VO> getGroup7() {
		return group7;
	}

	public void setGroup7(List<FTZGroup7VO> group7) {
		this.group7 = group7;
	}

	public List<FTZGroup11VO> getGroup11() {
		return group11;
	}

	public void setGroup11(List<FTZGroup11VO> group11) {
		this.group11 = group11;
	}

	public List<FTZGroup14VO> getGroup14() {
		return group14;
	}

	public void setGroup14(List<FTZGroup14VO> group14) {
		this.group14 = group14;
	}

	public FTZUNTVO getUnt() {
		return unt;
	}

	public void setUnt(FTZUNTVO unt) {
		this.unt = unt;
	}

	public FTZUNZVO getUnz() {
		return unz;
	}

	public void setUnz(FTZUNZVO unz) {
		this.unz = unz;
	}

	public List<FTZGroup8VO> getGroup8() {
		return group8;
	}

	public void setGroup8(List<FTZGroup8VO> group8) {
		this.group8 = group8;
	}

	public List<FTZDTMVO> getDtm() {
		return dtm;
	}

	public void setDtm(List<FTZDTMVO> dtm) {
		this.dtm = dtm;
	}

	public Map<String, FTZGroup11VO> getGroup11Map() {
		return group11Map;
	}

	public void setGroup11Map(Map<String, FTZGroup11VO> group11Map) {
		this.group11Map = group11Map;
	}
	
}
