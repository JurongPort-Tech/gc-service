package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class CargoDocUploadNotificationDetail implements Serializable {

	private static final long serialVersionUID = 4181802214962735553L;

	private String vvCd;
	private String vslNameVoyage;
	private String agent;
	private String btr;
	private String etb;
	private String etu;
	private List<CargoDocUploadDetail> cargoDocUploadInfo; 
	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public String getVslNameVoyage() {
		return vslNameVoyage;
	}

	public void setVslNameVoyage(String vslNameVoyage) {
		this.vslNameVoyage = vslNameVoyage;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getEtb() {
		return etb;
	}

	public void setEtb(String etb) {
		this.etb = etb;
	}

	public String getEtu() {
		return etu;
	}

	public void setEtu(String etu) {
		this.etu = etu;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public List<CargoDocUploadDetail> getCargoDocUploadInfo() {
		return cargoDocUploadInfo;
	}

	public void setCargoDocUploadInfo(List<CargoDocUploadDetail> cargoDocUploadInfo) {
		this.cargoDocUploadInfo = cargoDocUploadInfo;
	}

	public String getBtr() {
		return btr;
	}

	public void setBtr(String btr) {
		this.btr = btr;
	}

}
