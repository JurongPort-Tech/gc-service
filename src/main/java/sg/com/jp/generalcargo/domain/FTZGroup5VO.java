package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Group 5
public class FTZGroup5VO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FTZG5EQD eqd = new FTZG5EQD();
	private List<FTZG5SELVO> sel = new ArrayList<>(); // optional, max 9

	public FTZG5EQD getEqd() {
		return eqd;
	}

	public void setEqd(FTZG5EQD eqd) {
		this.eqd = eqd;
	}

	public List<FTZG5SELVO> getSel() {
		return sel;
	}

	public void setSel(List<FTZG5SELVO> sel) {
		this.sel = sel;
	}

}
