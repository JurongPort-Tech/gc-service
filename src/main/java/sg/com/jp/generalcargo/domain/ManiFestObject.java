package sg.com.jp.generalcargo.domain;

import java.util.List;

public class ManiFestObject {
	
	private String action;
	private List<EdoblnbrStatus> edoblnbrStatus;
	private String vslNm;
	private String vslVoy;
	public String getVslNm() {
		return vslNm;
	}
	public void setVslNm(String vslNm) {
		this.vslNm = vslNm;
	}
	public String getVslVoy() {
		return vslVoy;
	}
	public void setVslVoy(String vslVoy) {
		this.vslVoy = vslVoy;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<EdoblnbrStatus> getEdoblnbrStatus() {
		return edoblnbrStatus;
	}
	public void setEdoblnbrStatus(List<EdoblnbrStatus> edoblnbrStatus) {
		this.edoblnbrStatus = edoblnbrStatus;
	}

}
